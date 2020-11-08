package com.android.tv.settings.partnercustomizer.timer;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.settingslib.wifi.AccessPoint;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import java.util.Calendar;

@Keep
public class TimerFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String OFF = "0";
    private static final String ONCE = "2";
    private static final String SET_TIMER_POWER_OFF_TIMER = "set_timer_power_off_timer";
    public static final String SET_TIMER_POWER_OFF_TIMER_TYPE = "set_timer_power_off_timer_type";
    private static final String SET_TIMER_POWER_ON_CHANNEL = "set_timer_power_on_channel";
    private static final String SET_TIMER_POWER_ON_TIMER = "set_timer_power_on_timer";
    public static final String SET_TIMER_POWER_ON_TIMER_TYPE = "set_timer_power_on_timer_type";
    public static final String SLEEP_TIMER_ACTION = "com.mediatek.ui.menu.util.sleep.timer";
    private static final String TAG = "TimerFragment";
    private Context context;
    private String[] entries = {"Off", "10 Minutes", "20 Minutes", "30 Minutes", "40 Minutes", "50 Minutes", "60 Minutes", "90 Minutes", "120 Minutes"};
    private boolean isSetOnce = false;
    private Preference mPowerOffTimerPref;
    private ListPreference mPowerOffTimerTypePref;
    private Preference mPowerOnChannelPref;
    private Preference mPowerOnTimerPref;
    private ListPreference mPowerOnTimerTypePref;
    private TVSettingConfig mTVSettingConfig;
    private int[] sleeps = {0, 600, 1200, 1800, AccessPoint.LOWER_FREQ_24GHZ, PathInterpolatorCompat.MAX_NUM_POINTS, 5400, 7200};

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_timer, (String) null);
        this.context = getContext();
        this.mTVSettingConfig = TVSettingConfig.getInstance(this.context);
        this.mPowerOnTimerPref = findPreference(SET_TIMER_POWER_ON_TIMER);
        this.mPowerOnChannelPref = findPreference(SET_TIMER_POWER_ON_CHANNEL);
        this.mPowerOnChannelPref.setVisible(false);
        updateStatusPref(this.mPowerOnTimerPref, "tv_timer_power_on_time_type_entry_values");
        this.mPowerOffTimerPref = findPreference(SET_TIMER_POWER_OFF_TIMER);
        updateStatusPref(this.mPowerOffTimerPref, "tv_timer_power_off_time_type_entry_values");
        initPowerTimerTypePref();
    }

    private void initPowerTimerTypePref() {
        this.mPowerOffTimerTypePref = (ListPreference) findPreference(SET_TIMER_POWER_OFF_TIMER_TYPE);
        this.mPowerOffTimerTypePref.setOnPreferenceChangeListener(this);
        int powerOffValue = this.mTVSettingConfig.getTimerDefaultValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF);
        String[] offentry = this.context.getResources().getStringArray(R.array.timer_power_on_timer_type_entries);
        this.mPowerOffTimerTypePref.setSummary(offentry[Integer.valueOf(powerOffValue).intValue()]);
        this.mPowerOffTimerTypePref.setValueIndex(powerOffValue);
        if (powerOffValue == 0) {
            this.mPowerOffTimerPref.setEnabled(false);
        } else {
            this.mPowerOffTimerPref.setEnabled(true);
        }
        this.mPowerOnTimerTypePref = (ListPreference) findPreference(SET_TIMER_POWER_ON_TIMER_TYPE);
        this.mPowerOnTimerTypePref.setOnPreferenceChangeListener(this);
        int powerOnValue = this.mTVSettingConfig.getTimerDefaultValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON);
        this.mPowerOnTimerTypePref.setValueIndex(powerOnValue);
        this.mPowerOnTimerTypePref.setSummary(offentry[Integer.valueOf(powerOnValue).intValue()]);
        if (powerOnValue == 0) {
            this.mPowerOnTimerPref.setEnabled(false);
        } else {
            this.mPowerOnTimerPref.setEnabled(true);
        }
    }

    private String getCurrentTimeStr() {
        boolean is24HourFormat = DateFormat.is24HourFormat(this.context);
        Calendar cal = Calendar.getInstance();
        String time = cal.get(11) + ":" + cal.get(12);
        Log.d(TAG, "getCurrentTimeStr : " + time);
        return time;
    }

    private String formatHourStr(String time) {
        if (!DateFormat.is24HourFormat(this.context)) {
            Log.d(TAG, "formatHourStr : " + time);
            try {
                String h = time.split(":")[0];
                String m = time.split(":")[1];
                int hour = Integer.parseInt(h);
                if (hour > 12 && hour < 24) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(hour - 12);
                    sb.append(":");
                    sb.append(m);
                    sb.append(" PM");
                    time = sb.toString();
                } else if (hour == 24) {
                    time = "00:" + m + " AM";
                } else if (hour == 12) {
                    time = hour + ":" + m + " PM";
                } else {
                    time = hour + ":" + m + " AM";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "formatHourStr : " + time);
        return time;
    }

    public void onResume() {
        super.onResume();
        if (this.mPowerOnTimerPref != null) {
            String powerOnTimer = getSettingValue("tv_timer_power_on_timer_values");
            if (!TextUtils.isEmpty(powerOnTimer)) {
                this.mPowerOnTimerPref.setSummary((CharSequence) formatHourStr(powerOnTimer));
                if (powerOnTimer.equalsIgnoreCase(getCurrentTimeStr()) && this.mPowerOnTimerTypePref.getValue() != null && this.mPowerOnTimerTypePref.getValue().equalsIgnoreCase(ONCE)) {
                    this.mPowerOnTimerTypePref.setValueIndex(0);
                    this.mPowerOnTimerPref.setEnabled(false);
                }
            } else {
                this.mPowerOnTimerPref.setSummary((CharSequence) "00:00");
            }
        }
        if (this.mPowerOffTimerPref != null) {
            String powerOnTimer2 = getSettingValue("tv_timer_power_off_timer_values");
            if (!TextUtils.isEmpty(powerOnTimer2)) {
                this.mPowerOffTimerPref.setSummary((CharSequence) formatHourStr(powerOnTimer2));
            } else {
                this.mPowerOffTimerPref.setSummary((CharSequence) "00:00");
            }
        }
        initPowerTimerTypePref();
    }

    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x008f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(android.support.v7.preference.Preference r9, java.lang.Object r10) {
        /*
            r8 = this;
            java.lang.String r0 = r9.getKey()
            android.content.Context r1 = r8.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            java.lang.String r2 = "TimerFragment"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "newValue:"
            r3.append(r4)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r2, r3)
            r2 = 1
            r8.isSetOnce = r2
            r3 = r10
            java.lang.String r3 = (java.lang.String) r3
            int r4 = r0.hashCode()
            r5 = -1666373885(0xffffffff9cad2703, float:-1.1458264E-21)
            r6 = 0
            if (r4 == r5) goto L_0x0042
            r5 = 1621682677(0x60a8e9f5, float:9.737223E19)
            if (r4 == r5) goto L_0x0038
            goto L_0x004c
        L_0x0038:
            java.lang.String r4 = "set_timer_power_off_timer_type"
            boolean r4 = r0.equals(r4)
            if (r4 == 0) goto L_0x004c
            r4 = r2
            goto L_0x004d
        L_0x0042:
            java.lang.String r4 = "set_timer_power_on_timer_type"
            boolean r4 = r0.equals(r4)
            if (r4 == 0) goto L_0x004c
            r4 = r6
            goto L_0x004d
        L_0x004c:
            r4 = -1
        L_0x004d:
            r5 = 2130903222(0x7f0300b6, float:1.7413256E38)
            switch(r4) {
                case 0: goto L_0x008f;
                case 1: goto L_0x0054;
                default: goto L_0x0053;
            }
        L_0x0053:
            goto L_0x00be
        L_0x0054:
            java.lang.String r4 = "tv_timer_power_off_time_type_entry_values"
            r7 = r10
            java.lang.String r7 = (java.lang.String) r7
            android.provider.Settings.Global.putString(r1, r4, r7)
            android.content.Context r4 = r8.context
            android.content.res.Resources r4 = r4.getResources()
            java.lang.String[] r4 = r4.getStringArray(r5)
            android.support.v7.preference.ListPreference r5 = r8.mPowerOffTimerTypePref
            r7 = r10
            java.lang.String r7 = (java.lang.String) r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            int r7 = r7.intValue()
            r7 = r4[r7]
            r5.setSummary(r7)
            java.lang.String r5 = "0"
            boolean r5 = r5.equals(r3)
            if (r5 == 0) goto L_0x0086
            android.support.v7.preference.Preference r5 = r8.mPowerOffTimerPref
            r5.setEnabled(r6)
            goto L_0x008b
        L_0x0086:
            android.support.v7.preference.Preference r5 = r8.mPowerOffTimerPref
            r5.setEnabled(r2)
        L_0x008b:
            r8.setPowerOffTimerType()
            goto L_0x00be
        L_0x008f:
            java.lang.String r4 = "tv_timer_power_on_time_type_entry_values"
            r6 = r10
            java.lang.String r6 = (java.lang.String) r6
            android.provider.Settings.Global.putString(r1, r4, r6)
            android.support.v7.preference.Preference r4 = r8.mPowerOnTimerPref
            android.support.v7.preference.Preference r6 = r8.mPowerOnChannelPref
            java.lang.String r7 = "tv_timer_power_on_time_type_entry_values"
            r8.updateStatusPref(r4, r6, r7)
            android.content.Context r4 = r8.context
            android.content.res.Resources r4 = r4.getResources()
            java.lang.String[] r4 = r4.getStringArray(r5)
            r5 = r10
            java.lang.String r5 = (java.lang.String) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            int r5 = r5.intValue()
            r5 = r4[r5]
            r9.setSummary((java.lang.CharSequence) r5)
            r8.setPowerOnTimerType()
        L_0x00be:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.timer.TimerFragment.onPreferenceChange(android.support.v7.preference.Preference, java.lang.Object):boolean");
    }

    private String getSettingValue(String settingkey) {
        return Settings.Global.getString(getContext().getContentResolver(), settingkey);
    }

    private void updateStatusPref(Preference Pref1, Preference Pref2, String settingkey) {
        if (TextUtils.equals(OFF, getSettingValue(settingkey))) {
            Pref1.setEnabled(false);
            Pref2.setEnabled(false);
            return;
        }
        Pref1.setEnabled(true);
        Pref2.setEnabled(true);
    }

    private void updateStatusPref(Preference Pref1, String settingkey) {
        if (TextUtils.equals(OFF, getSettingValue(settingkey)) || TextUtils.isEmpty(getSettingValue(settingkey))) {
            Pref1.setEnabled(false);
        } else {
            Pref1.setEnabled(true);
        }
    }

    private boolean isResetPowerOffOnce(Preference mPowerTimerPref) {
        String currentTime = DateFormat.getTimeFormat(this.context).format(Calendar.getInstance().getTime());
        String powerOffTimer = String.valueOf(mPowerTimerPref.getSummary());
        int currentHour = new Integer(currentTime.substring(0, currentTime.indexOf(":"))).intValue();
        int currentMin0 = new Integer(currentTime.substring(currentTime.indexOf(":") + 1, currentTime.indexOf(":") + 2)).intValue();
        int currentMin1 = new Integer(currentTime.substring(currentTime.indexOf(":") + 2, currentTime.indexOf(":") + 3)).intValue();
        Log.d(TAG, "mPowerOffTimerPref::" + powerOffTimer);
        int currentTotalMin = (currentHour * 60) + (currentMin0 * 10) + currentMin1;
        int powerOffTotalMin = (new Integer(powerOffTimer.substring(0, 1)).intValue() * 60 * 10) + (new Integer(powerOffTimer.substring(1, 2)).intValue() * 60) + (new Integer(powerOffTimer.substring(3, 4)).intValue() * 10) + new Integer(powerOffTimer.substring(4, 5)).intValue();
        Log.d(TAG, "currentTotalMin::" + currentTotalMin);
        Log.d(TAG, "powerOffTotalMin::" + powerOffTotalMin);
        if (currentTotalMin > powerOffTotalMin) {
            return true;
        }
        return false;
    }

    private void setPowerOffTimerType() {
        Context mContext = getContext();
        int value = Settings.Global.getInt(mContext.getContentResolver(), "tv_timer_power_off_time_type_entry_values", 0);
        String time = Settings.Global.getString(mContext.getContentResolver(), "tv_timer_power_off_timer_values");
        if (TextUtils.isEmpty(time)) {
            time = "00:00";
        }
        Log.d(TAG, "setPowerOffTimerType: " + value + ",time:" + time);
        if (value == 0) {
            this.mTVSettingConfig.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 0, time);
            this.mTVSettingConfig.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 0, time);
        } else if (value == 1) {
            this.mTVSettingConfig.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 1, time);
            this.mTVSettingConfig.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 0, time);
        } else {
            this.mTVSettingConfig.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 1, time);
            this.mTVSettingConfig.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 1, time);
        }
        Log.d(TAG, "setPowerOffTimer timer: " + Settings.Global.getString(mContext.getContentResolver(), "tv_timer_power_off_timer_values"));
    }

    private void setPowerOnTimerType() {
        Context mContext = getContext();
        int value = Settings.Global.getInt(mContext.getContentResolver(), "tv_timer_power_on_time_type_entry_values", 0);
        String time = Settings.Global.getString(mContext.getContentResolver(), "tv_timer_power_on_timer_values");
        if (TextUtils.isEmpty(time)) {
            time = "00:00";
        }
        Log.d(TAG, "setPowerOnTimerType: " + value + ",time:" + time);
        if (value == 0) {
            this.mTVSettingConfig.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 0, time);
            this.mTVSettingConfig.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 0, time);
        } else if (value == 1) {
            this.mTVSettingConfig.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 1, time);
            this.mTVSettingConfig.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 0, time);
        } else {
            this.mTVSettingConfig.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 1, time);
            this.mTVSettingConfig.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 1, time);
        }
        Log.d(TAG, "setPowerOnTimer timer: " + Settings.Global.getString(mContext.getContentResolver(), "tv_timer_power_on_timer_values"));
    }

    public int getMetricsCategory() {
        return 746;
    }
}
