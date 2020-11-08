package com.android.tv.settings.system;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.datetime.ZoneGetter;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.utils.TimeZoneUtil;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Keep
public class TimeZoneFragment extends SettingsPreferenceFragment {
    private static final String AS_BROADCAST = "As broadcast";
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TimeZoneFragment.this.getActivity() != null) {
                TimeZoneFragment.this.updateZones();
            }
        }
    };

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(themedContext);
        screen.setTitle((int) R.string.system_set_time_zone);
        setPreferenceScreen(screen);
        List<Map<String, Object>> zoneList = ZoneGetter.getZonesList(getActivity());
        List<ZonePreference> zonePrefs = new ArrayList<>(zoneList.size() + 1);
        Map<String, Object> zoneAs = new HashMap<>();
        zoneAs.put(ZoneGetter.KEY_ID, AS_BROADCAST);
        zoneAs.put(ZoneGetter.KEY_DISPLAYNAME, getActivity().getResources().getString(R.string.time_zone_as_broadcast));
        zoneAs.put(ZoneGetter.KEY_GMT, getActivity().getResources().getString(R.string.time_zone_as_broadcast));
        zoneAs.put(ZoneGetter.KEY_OFFSET, 0);
        try {
            new TimeZoneUtil(themedContext, zoneList).enhanceTimeZoneList(zonePrefs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MarketRegionInfo.getCurrentMarketRegion() != 1) {
            zonePrefs.add(0, new ZonePreference(themedContext, zoneAs));
        }
        for (ZonePreference zonePref : zonePrefs) {
            screen.addPreference(zonePref);
        }
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().registerReceiver(this.mIntentReceiver, filter, (String) null, (Handler) null);
        updateZones();
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mIntentReceiver);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof ZonePreference) {
            if (AS_BROADCAST.equals(preference.getKey())) {
                setTimeZoneAsBroadcast();
            } else {
                TVSettingConfig.getInstance(getActivity()).setConifg(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS, 0);
                ((AlarmManager) getActivity().getSystemService(NotificationCompat.CATEGORY_ALARM)).setTimeZone(preference.getKey());
            }
            if (!getFragmentManager().popBackStackImmediate()) {
                getActivity().finish();
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    /* access modifiers changed from: private */
    public void updateZones() {
        String id = TimeZone.getDefault().getID();
        PreferenceScreen screen = getPreferenceScreen();
        int count = screen.getPreferenceCount();
        boolean isAsBst = TVSettingConfig.getInstance(getActivity()).getConfigValueInt(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS) == 1;
        Log.d("TimeZoneFragment", "isAsBst=" + isAsBst);
        int startIndex = 0;
        if (MarketRegionInfo.getCurrentMarketRegion() != 1) {
            startIndex = 1;
            ((ZonePreference) screen.findPreference(AS_BROADCAST)).setChecked(isAsBst);
        }
        Preference activePref = null;
        for (int i = startIndex; i < count; i++) {
            Preference pref = screen.getPreference(i);
            if (pref instanceof ZonePreference) {
                ZonePreference zonePref = (ZonePreference) pref;
                if (isAsBst) {
                    zonePref.setChecked(false);
                } else {
                    zonePref.setChecked(TextUtils.equals(zonePref.getKey(), id));
                    if (TextUtils.equals(zonePref.getKey(), id)) {
                        activePref = zonePref;
                    }
                }
            }
        }
        if (activePref != null) {
            scrollToPreference(activePref);
        }
    }

    public static class ZonePreference extends CheckBoxPreference {
        public String key;
        public String name;
        public Integer offset;
        public String summary;

        public ZonePreference(Context context, Map<? extends String, ?> zone) {
            super(context);
            setWidgetLayoutResource(R.layout.radio_preference_widget);
            this.offset = (Integer) zone.get(ZoneGetter.KEY_OFFSET);
            this.key = (String) zone.get(ZoneGetter.KEY_ID);
            this.name = (String) zone.get(ZoneGetter.KEY_DISPLAYNAME);
            this.summary = (String) zone.get(ZoneGetter.KEY_GMT);
            setKey(this.key);
            setPersistent(false);
            setTitle((CharSequence) this.name);
            setSummary((CharSequence) this.summary);
        }

        public String toString() {
            return "key:" + this.key + "  name:" + this.name + "  summary:" + this.summary;
        }
    }

    public int getMetricsCategory() {
        return 515;
    }

    public void setTimeZoneAsBroadcast() {
        Context context = getPreferenceManager().getContext();
        boolean dsl = true;
        TVSettingConfig.getInstance(context).setConifg(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS, 1);
        if (TVSettingConfig.getInstance(context).getConfigValueInt(MtkTvConfigTypeBase.CFG_TIME_AUTO_DST) != 1) {
            dsl = false;
        }
        TVSettingConfig.getInstance(context).setConfigValue(MtkTvConfigTypeBase.CFG_TIME_ZONE, 0, dsl);
    }
}
