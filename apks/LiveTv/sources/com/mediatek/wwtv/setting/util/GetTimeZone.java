package com.mediatek.wwtv.setting.util;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParserException;

public class GetTimeZone {
    private static final String XMLTAG_TIMEZONE = "timezone";
    private static GetTimeZone getTimeZone;
    public final String TAG = "GetTimeZone";
    AlarmManager alarm;
    private final Context mContext;
    private ArrayList<Action> mTimeZoneActions;
    private final TimeZone tz;
    private final String[] zoneIdNames;
    private String[] zonesArray;

    public GetTimeZone(Context context) {
        this.zoneIdNames = context.getResources().getStringArray(R.array.menu_setup_timezone_array);
        this.tz = Calendar.getInstance().getTimeZone();
        this.mContext = context;
        this.alarm = (AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
    }

    public static GetTimeZone getInstance(Context context) {
        getTimeZone = new GetTimeZone(context);
        return getTimeZone;
    }

    public void setTimeZone(int value, String zoneinfo) {
        int i = value;
        String str = zoneinfo;
        int tz_offset = 0;
        boolean dsl = false;
        if (i == 0) {
            MtkLog.v("GetTimeZone", "setTimeZone:set broadcast");
            TVContent.getInstance(this.mContext).setConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS, 1);
        } else {
            String gmtValue = str.substring("GMT".length() + str.indexOf("/") + 1);
            String[] hours = gmtValue.substring(1).split(":");
            int tz_offset2 = (Integer.parseInt(hours[0]) * 60 * 60) + (Integer.parseInt(hours[1]) * 60);
            if (gmtValue.contains(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING)) {
                tz_offset2 *= -1;
            }
            tz_offset = tz_offset2;
            TVContent.getInstance(this.mContext).setConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS, 0);
        }
        MtkLog.d("GetTimeZone", "setTimeZone:tz_offset:" + tz_offset);
        if (TVContent.getInstance(this.mContext).getConfigValue(MtkTvConfigTypeBase.CFG_TIME_AUTO_DST) == 1) {
            dsl = true;
        }
        MtkLog.d("GetTimeZone", "setTimeZone:value:" + i + "dsl:" + dsl);
        TVContent.getInstance(this.mContext).setConfigValue("g_time__time_zone", tz_offset, dsl);
    }

    @Deprecated
    public int getZoneId() {
        int value;
        int tz_offset = TVContent.getInstance(this.mContext).getConfigValue("g_time__time_zone");
        if (CommonIntegration.isSARegion()) {
            if (tz_offset == -57600) {
                tz_offset = 57600;
            }
            tz_offset -= 10800;
        }
        int index = -1;
        MtkLog.d("zone", "tz_offset:" + tz_offset);
        int i = 0;
        while (true) {
            if (i > MenuConfigManager.MAX_TIME_ZONE) {
                break;
            } else if (MenuConfigManager.zoneValue[i] == tz_offset) {
                index = i;
                break;
            } else {
                i++;
            }
        }
        MtkLog.d("zone", "index:" + index);
        if (index >= 22 && index < 22 + 13) {
            value = index - 22;
        } else if (index > 0 && index < 22) {
            value = index + 13;
        } else if (index == 0) {
            value = 14;
        } else {
            value = 13;
        }
        if (TVContent.getInstance(this.mContext).getConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS) == 1) {
            value = 13;
        }
        MtkLog.d("zone", "value:" + value);
        return value;
    }

    private static StringBuilder formatOffset(StringBuilder sb, long offset) {
        long off = (offset / 1000) / 60;
        sb.append("GMT");
        if (off < 0) {
            sb.append('-');
            off = -off;
        } else {
            sb.append('+');
        }
        int hours = (int) (off / 60);
        int minutes = (int) (off % 60);
        sb.append((char) ((hours / 10) + 48));
        sb.append((char) ((hours % 10) + 48));
        sb.append(':');
        sb.append((char) ((minutes / 10) + 48));
        sb.append((char) (48 + (minutes % 10)));
        return sb;
    }

    private class TimeZoneInfo implements Comparable<TimeZoneInfo> {
        public String tzId;
        public String tzName;
        public long tzOffset;

        public TimeZoneInfo(String id, String name, long offset) {
            this.tzId = id;
            this.tzName = name;
            this.tzOffset = offset;
        }

        public int compareTo(TimeZoneInfo another) {
            return (int) (this.tzOffset - another.tzOffset);
        }
    }

    private ArrayList<Action> getZoneActions(Context context) {
        if (this.mTimeZoneActions != null && this.mTimeZoneActions.size() != 0) {
            return this.mTimeZoneActions;
        }
        ArrayList<TimeZoneInfo> timeZones = getTimeZones(context);
        this.mTimeZoneActions = new ArrayList<>();
        Collections.sort(timeZones);
        TimeZone currentTz = TimeZone.getDefault();
        Iterator<TimeZoneInfo> it = timeZones.iterator();
        while (it.hasNext()) {
            TimeZoneInfo tz2 = it.next();
            StringBuilder name = new StringBuilder();
            this.mTimeZoneActions.add(getTimeZoneAction(tz2.tzId, tz2.tzName, formatOffset(name, tz2.tzOffset).toString(), currentTz.getID().equals(tz2.tzId)));
        }
        return this.mTimeZoneActions;
    }

    private ArrayList<TimeZoneInfo> getTimeZones(Context context) {
        int i;
        ArrayList<TimeZoneInfo> timeZones = new ArrayList<>();
        long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (true) {
                i = 2;
                if (xrp.next() == 2) {
                    break;
                }
            }
            xrp.next();
            while (true) {
                if (xrp.getEventType() == 3) {
                    break;
                }
                while (xrp.getEventType() != i && xrp.getEventType() != 1) {
                    xrp.next();
                }
                if (xrp.getEventType() == 1) {
                    break;
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    TimeZone tz2 = TimeZone.getTimeZone(id);
                    if (tz2 != null) {
                        TimeZoneInfo timeZoneInfo = r7;
                        TimeZoneInfo timeZoneInfo2 = new TimeZoneInfo(id, displayName, (long) tz2.getOffset(date));
                        timeZones.add(timeZoneInfo);
                    }
                }
                while (xrp.getEventType() != 3) {
                    xrp.next();
                }
                xrp.next();
                i = 2;
            }
            xrp.close();
        } catch (XmlPullParserException e) {
            Log.e("GetTimeZone", "Ill-formatted timezones.xml file");
        } catch (IOException e2) {
            Log.e("GetTimeZone", "Unable to read timezones.xml file");
        }
        return timeZones;
    }

    private static Action getTimeZoneAction(String tzId, String displayName, String gmt, boolean setChecked) {
        return new Action.Builder().key(tzId).title(displayName).description(gmt).checked(setChecked).build();
    }

    public String getTimeZoneOlsonID(int index) {
        if (this.mTimeZoneActions == null) {
            getZoneActions(this.mContext);
        }
        String olsonID = this.mTimeZoneActions.get(index).getKey();
        MtkLog.d("GetTimeZone", "getTimeZoneOlsonID " + olsonID);
        return olsonID;
    }

    public String[] generateTimeZonesArray() {
        if (this.zonesArray != null) {
            return this.zonesArray;
        }
        if (this.mTimeZoneActions == null) {
            getZoneActions(this.mContext);
        }
        int i = 1;
        this.zonesArray = new String[(this.mTimeZoneActions.size() + 1)];
        this.zonesArray[0] = "As Broadcast";
        while (true) {
            int i2 = i;
            if (i2 <= this.mTimeZoneActions.size()) {
                Action zone = this.mTimeZoneActions.get(i2 - 1);
                String[] strArr = this.zonesArray;
                strArr[i2] = zone.getmTitle() + "/" + zone.getDescription();
                i = i2 + 1;
            } else {
                MtkLog.d("GetTimeZone", "generateTimeZonesArray length:" + this.zonesArray.length);
                return this.zonesArray;
            }
        }
    }

    public int getCurrentTimeZoneIndex() {
        GetTimeZone getTimeZone2 = this;
        if (getTimeZone2.mTimeZoneActions == null) {
            getTimeZone2.getZoneActions(getTimeZone2.mContext);
        }
        int value = -1;
        if (TVContent.getInstance(getTimeZone2.mContext).getConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS) == 1) {
            return 0;
        }
        String zoneName = getTimeZone2.tz.getDisplayName();
        String zoneID = getTimeZone2.tz.getID();
        StringBuilder matcher = new StringBuilder();
        formatOffset(matcher, (long) TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis()));
        MtkLog.d("GetTimeZone", "matcher:" + matcher);
        MtkLog.d("GetTimeZone", "zoneName:" + zoneName + ",zoneID=" + zoneID);
        int i = 0;
        while (true) {
            if (i >= getTimeZone2.mTimeZoneActions.size()) {
                String broadcastCfgId = MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS;
                break;
            }
            Action zone = getTimeZone2.mTimeZoneActions.get(i);
            if (zoneID.equals(zone.getKey())) {
                MtkLog.d("GetTimeZone", "id equals index:" + i);
                value = i;
                Object obj = MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS;
                break;
            } else if (matcher.toString().equals(zone.getDescription())) {
                MtkLog.d("GetTimeZone", "matcher equals index:" + i);
                value = i;
                ArrayList<Action> sameGmtzones = getTimeZone2.findZonesBySameGmt(matcher.toString(), i);
                int j = 0;
                while (true) {
                    int j2 = j;
                    if (j2 >= sameGmtzones.size()) {
                        String broadcastCfgId2 = MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS;
                        String str = zoneName;
                        break;
                    } else if (zoneID.equals(sameGmtzones.get(j2).getKey())) {
                        value += j2;
                        Object obj2 = MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS;
                        StringBuilder sb = new StringBuilder();
                        String str2 = zoneName;
                        sb.append("matcher samegmt index:");
                        sb.append(value);
                        MtkLog.d("GetTimeZone", sb.toString());
                        break;
                    } else {
                        String broadcastCfgId3 = MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS;
                        String str3 = zoneName;
                        j = j2 + 1;
                    }
                }
            } else {
                String broadcastCfgId4 = MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS;
                String str4 = zoneName;
                i++;
                getTimeZone2 = this;
            }
        }
        MtkLog.d("GetTimeZone", "match value:" + (value + 1));
        return value + 1;
    }

    private ArrayList<Action> findZonesBySameGmt(String matcher, int pos) {
        ArrayList<Action> sameGmtZones = new ArrayList<>();
        for (int i = pos; i < this.mTimeZoneActions.size(); i++) {
            Action zone = this.mTimeZoneActions.get(i);
            if (!matcher.equals(zone.getDescription())) {
                break;
            }
            sameGmtZones.add(zone);
        }
        MtkLog.d("GetTimeZone", "findZonesBySameGmt size:" + sameGmtZones.size());
        return sameGmtZones;
    }
}
