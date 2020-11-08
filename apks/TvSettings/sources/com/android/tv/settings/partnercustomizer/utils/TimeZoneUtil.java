package com.android.tv.settings.partnercustomizer.utils;

import android.content.Context;
import android.icu.text.TimeZoneNames;
import android.icu.util.TimeZone;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.datetime.ZoneGetter;
import com.android.tv.settings.R;
import com.android.tv.settings.system.TimeZoneFragment;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.util.CountryConfigEntry;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeZoneUtil {
    private static final String TAG = "TimeZoneUtil";
    private Context mContext;
    private Map<String, CountryConfigEntry> mCountryConfigMap = PartnerSettingsConfig.getCountryConfigMap();
    private List<Map<String, Object>> mSpecialZoneList = new ArrayList();
    private List<Map<String, Object>> mZoneList = new ArrayList();

    public TimeZoneUtil(Context context, List<Map<String, Object>> zoneList) {
        int i;
        this.mContext = context;
        if (zoneList != null) {
            this.mZoneList.addAll(zoneList);
        }
        Iterator<Map.Entry<String, CountryConfigEntry>> it = this.mCountryConfigMap.entrySet().iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, CountryConfigEntry> item = it.next();
            String countryCode = item.getKey();
            if (countryCode.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_RUS)) {
                String[] rusTzs = this.mContext.getResources().getStringArray(R.array.rus_timezone_keys);
                int length = rusTzs.length;
                while (i < length) {
                    String string = rusTzs[i];
                    this.mSpecialZoneList.add(buildTimeZoneMap(string, getDisplayName(context, string)));
                    i++;
                }
            } else if (countryCode.equalsIgnoreCase("AUS")) {
                String[] ausTzs = this.mContext.getResources().getStringArray(R.array.aus_timezone_keys);
                int length2 = ausTzs.length;
                while (i < length2) {
                    String string2 = ausTzs[i];
                    this.mSpecialZoneList.add(buildTimeZoneMap(string2, getDisplayName(context, string2)));
                    i++;
                }
            } else if (countryCode.equalsIgnoreCase("ESP")) {
                String[] espTzs = this.mContext.getResources().getStringArray(R.array.esp_timezone_keys);
                int length3 = espTzs.length;
                while (i < length3) {
                    String string3 = espTzs[i];
                    this.mSpecialZoneList.add(buildTimeZoneMap(string3, getDisplayName(context, string3)));
                    i++;
                }
            } else if (!TextUtils.isEmpty(TimeZone.getCanonicalID(item.getValue().time_zone))) {
                this.mSpecialZoneList.add(buildTimeZoneMap(item.getValue().time_zone, getDisplayName(context, item.getValue().time_zone)));
            }
        }
        String[] resTzs = this.mContext.getResources().getStringArray(R.array.time_zone_for_custom);
        Set<String> resSets = new HashSet<>();
        for (String string4 : resTzs) {
            resSets.add(string4);
        }
        for (String string5 : resSets) {
            MtkLog.d(TAG, string5);
            if (!TextUtils.isEmpty(string5) && !isExist(string5) && !TextUtils.isEmpty(TimeZone.getCanonicalID(string5))) {
                this.mSpecialZoneList.add(buildTimeZoneMap(string5, getDisplayName(context, string5)));
            }
        }
        String[] removeResTzs = this.mContext.getResources().getStringArray(R.array.remove_time_zone_for_custom);
        List<String> removeResSets = new ArrayList<>();
        int length4 = removeResTzs.length;
        while (i < length4) {
            removeResSets.add(removeResTzs[i]);
            i++;
        }
        Iterator<Map<String, Object>> iterator = this.mZoneList.iterator();
        while (iterator.hasNext()) {
            String itemKey = (String) iterator.next().get(ZoneGetter.KEY_ID);
            for (String removeItem : removeResSets) {
                if (itemKey.contains(removeItem) || itemKey.equals(removeItem)) {
                    iterator.remove();
                    Log.d(TAG, " TimeZoneUtil for customer remove some tz itemKey : " + itemKey);
                }
            }
        }
    }

    private boolean isExist(String key) {
        for (Map<String, Object> map : this.mSpecialZoneList) {
            if (map.values().contains(key)) {
                return true;
            }
        }
        return false;
    }

    private String getDisplayName(Context context, String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        TimeZoneNames timeZoneNames = TimeZoneNames.getInstance(context.getResources().getConfiguration().locale);
        String canonicalZoneId = TimeZone.getCanonicalID(key);
        if (TextUtils.isEmpty(canonicalZoneId)) {
            canonicalZoneId = key;
        }
        String displayName = timeZoneNames.getExemplarLocationName(canonicalZoneId);
        if (TextUtils.isEmpty(displayName)) {
            return getZoneLongName(timeZoneNames, java.util.TimeZone.getTimeZone(key), new Date());
        }
        return displayName;
    }

    private static String getZoneLongName(TimeZoneNames names, java.util.TimeZone tz, Date now) {
        TimeZoneNames.NameType nameType;
        if (tz.inDaylightTime(now)) {
            nameType = TimeZoneNames.NameType.LONG_DAYLIGHT;
        } else {
            nameType = TimeZoneNames.NameType.LONG_STANDARD;
        }
        return names.getDisplayName(tz.getID(), nameType, now.getTime());
    }

    public void enhanceTimeZoneList(List<TimeZoneFragment.ZonePreference> zonePrefs) {
        Iterator<Map<String, Object>> iterator = this.mZoneList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            Iterator<Map<String, Object>> it = this.mSpecialZoneList.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (map.values().contains(it.next().get(ZoneGetter.KEY_ID))) {
                        iterator.remove();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        for (Map<String, Object> zone : this.mZoneList) {
            zonePrefs.add(new TimeZoneFragment.ZonePreference(this.mContext, zone));
        }
        for (Map<String, Object> zone2 : this.mSpecialZoneList) {
            zonePrefs.add(new TimeZoneFragment.ZonePreference(this.mContext, zone2));
        }
        Collections.sort(zonePrefs, new Comparator<TimeZoneFragment.ZonePreference>() {
            public int compare(TimeZoneFragment.ZonePreference lhs, TimeZoneFragment.ZonePreference rhs) {
                return lhs.offset.intValue() - rhs.offset.intValue();
            }
        });
        for (TimeZoneFragment.ZonePreference zonePreference : zonePrefs) {
            Log.d("thewyp", zonePreference.toString());
        }
    }

    private List<Map<String, Object>> buildSpecialCountryTimeZones(String[] keys, String[] names) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            result.add(buildTimeZoneMap(keys[i], names[i]));
        }
        return result;
    }

    private Map<String, Object> buildTimeZoneMap(String key, String name) {
        Map<String, Object> result = new HashMap<>();
        result.put(ZoneGetter.KEY_ID, key);
        result.put(ZoneGetter.KEY_DISPLAYNAME, name);
        result.put(ZoneGetter.KEY_DISPLAY_LABEL, name);
        int offset = java.util.TimeZone.getTimeZone(key).getOffset(Calendar.getInstance().getTimeInMillis());
        int p = Math.abs(offset);
        StringBuilder sb = new StringBuilder();
        sb.append("GMT");
        if (offset < 0) {
            sb.append('-');
        } else {
            sb.append('+');
        }
        sb.append(p / 3600000);
        sb.append(AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);
        int min = (p / 60000) % 60;
        if (min < 10) {
            sb.append('0');
        }
        sb.append(min);
        result.put(ZoneGetter.KEY_GMT, sb.toString());
        result.put(ZoneGetter.KEY_OFFSET, Integer.valueOf(offset));
        result.put(ZoneGetter.KEY_OFFSET_LABEL, sb.toString());
        return result;
    }
}
