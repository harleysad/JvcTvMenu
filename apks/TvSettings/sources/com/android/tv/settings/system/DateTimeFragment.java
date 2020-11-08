package com.android.tv.settings.system;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.settingslib.datetime.ZoneGetter;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import java.util.Calendar;
import java.util.Date;

@Keep
public class DateTimeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String AUTO_DATE_TIME_NTP = "network";
    private static final String AUTO_DATE_TIME_OFF = "off";
    private static final String AUTO_DATE_TIME_TS = "transport_stream";
    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";
    private static final String KEY_AUTO_DATE_TIME = "auto_date_time";
    private static final String KEY_SET_DATE = "set_date";
    private static final String KEY_SET_TIME = "set_time";
    private static final String KEY_SET_TIME_ZONE = "set_time_zone";
    private static final String KEY_USE_24_HOUR = "use_24_hour";
    private Preference mDatePref;
    private final Calendar mDummyDate = Calendar.getInstance();
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Activity activity = DateTimeFragment.this.getActivity();
            if (activity != null) {
                DateTimeFragment.this.updateTimeAndDateDisplay(activity);
            }
        }
    };
    private Preference mTime24Pref;
    private Preference mTimePref;
    private Preference mTimeZone;

    public static DateTimeFragment newInstance() {
        return new DateTimeFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.date_time, (String) null);
        boolean isRestricted = SecurityFragment.isRestrictedProfileInEffect(getContext());
        boolean isZiggo = TVSettingConfig.getInstance(getContext()).isDVBCOperatorZiggo();
        this.mDatePref = findPreference(KEY_SET_DATE);
        this.mDatePref.setVisible(!isRestricted);
        this.mTimePref = findPreference(KEY_SET_TIME);
        boolean z = false;
        this.mTimePref.setVisible(!isRestricted && !isZiggo);
        boolean tsTimeCapable = SystemProperties.getBoolean("ro.vendor.config.ts.date.time", false);
        ListPreference autoDateTimePref = (ListPreference) findPreference(KEY_AUTO_DATE_TIME);
        autoDateTimePref.setValue(getAutoDateTimeState());
        autoDateTimePref.setOnPreferenceChangeListener(this);
        if (tsTimeCapable) {
            autoDateTimePref.setEntries((int) R.array.auto_date_time_ts_entries);
            autoDateTimePref.setEntryValues((int) R.array.auto_date_time_ts_entry_values);
        }
        autoDateTimePref.setVisible(!isRestricted);
        this.mTimeZone = findPreference(KEY_SET_TIME_ZONE);
        Preference preference = this.mTimeZone;
        if (!isRestricted && !isZiggo) {
            z = true;
        }
        preference.setVisible(z);
        this.mTime24Pref = findPreference(KEY_USE_24_HOUR);
        this.mTime24Pref.setOnPreferenceChangeListener(this);
    }

    public void onResume() {
        super.onResume();
        ((SwitchPreference) this.mTime24Pref).setChecked(is24Hour());
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().registerReceiver(this.mIntentReceiver, filter, (String) null, (Handler) null);
        updateTimeAndDateDisplay(getActivity());
        updateTimeDateEnable();
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mIntentReceiver);
    }

    /* access modifiers changed from: private */
    public void updateTimeAndDateDisplay(Context context) {
        Calendar now = Calendar.getInstance();
        this.mDummyDate.setTimeZone(now.getTimeZone());
        this.mDummyDate.set(now.get(1), 11, 31, 13, 0, 0);
        Date dummyDate = this.mDummyDate.getTime();
        this.mDatePref.setSummary((CharSequence) DateFormat.getLongDateFormat(context).format(now.getTime()));
        this.mTimePref.setSummary((CharSequence) DateFormat.getTimeFormat(getActivity()).format(now.getTime()));
        if (TVSettingConfig.getInstance(getActivity()).getConfigValueInt(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS) == 1) {
            this.mTimeZone.setSummary((CharSequence) getActivity().getResources().getString(R.string.time_zone_as_broadcast));
        } else {
            CharSequence tz = ZoneGetter.getTimeZoneOffsetAndName(getActivity(), now.getTimeZone(), now.getTime());
            Log.d("DateTimeFragment", "display TimeZone:" + tz + " TimeZone:" + now.getTimeZone());
            this.mTimeZone.setSummary(tz);
        }
        this.mTime24Pref.setSummary((CharSequence) DateFormat.getTimeFormat(getActivity()).format(dummyDate));
    }

    private void updateTimeDateEnable() {
        boolean enable = TextUtils.equals(getAutoDateTimeState(), "off");
        this.mDatePref.setEnabled(enable);
        this.mTimePref.setEnabled(enable);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (TextUtils.equals(preference.getKey(), KEY_AUTO_DATE_TIME)) {
            String value = (String) newValue;
            if (TextUtils.equals(value, AUTO_DATE_TIME_NTP)) {
                setAutoDateTime(true);
            } else if (TextUtils.equals(value, AUTO_DATE_TIME_TS)) {
                throw new IllegalStateException("TS date is not yet implemented");
            } else if (TextUtils.equals(value, "off")) {
                setAutoDateTime(false);
            } else {
                throw new IllegalArgumentException("Unknown auto time value " + value);
            }
            updateTimeDateEnable();
        } else if (TextUtils.equals(preference.getKey(), KEY_USE_24_HOUR)) {
            boolean use24Hour = ((Boolean) newValue).booleanValue();
            set24Hour(use24Hour);
            timeUpdated(use24Hour);
        }
        return true;
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(getActivity());
    }

    private void timeUpdated(boolean use24Hour) {
        int timeFormatPreference;
        Intent timeChanged = new Intent("android.intent.action.TIME_SET");
        if (use24Hour) {
            timeFormatPreference = 1;
        } else {
            timeFormatPreference = 0;
        }
        timeChanged.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", timeFormatPreference);
        getContext().sendBroadcast(timeChanged);
    }

    private void set24Hour(boolean use24Hour) {
        Settings.System.putString(getContext().getContentResolver(), "time_12_24", use24Hour ? HOURS_24 : "12");
    }

    private void setAutoDateTime(boolean on) {
        Settings.Global.putInt(getContext().getContentResolver(), "auto_time", on);
    }

    private String getAutoDateTimeState() {
        if (Settings.Global.getInt(getContext().getContentResolver(), "auto_time", 0) > 0) {
            return AUTO_DATE_TIME_NTP;
        }
        return "off";
    }

    public int getMetricsCategory() {
        return 38;
    }
}
