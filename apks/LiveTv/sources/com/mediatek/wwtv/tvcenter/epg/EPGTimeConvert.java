package com.mediatek.wwtv.tvcenter.epg;

import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.epg.us.ListItemData;
import com.mediatek.wwtv.tvcenter.nav.util.BannerImplement;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EPGTimeConvert {
    private static final String TAG = "EPGTimeConvert";
    private static EPGTimeConvert tmConvert;

    private EPGTimeConvert() {
    }

    public static EPGTimeConvert getInstance() {
        if (tmConvert == null) {
            tmConvert = new EPGTimeConvert();
        }
        return tmConvert;
    }

    public long getHourtoMsec(int hour) {
        return ((long) hour) * 60 * 60;
    }

    public static float countShowWidth(long endTime, long startTime) {
        return ((float) (endTime - startTime)) / 7200.0f;
    }

    public float countShowWidth(long duration) {
        return ((float) duration) / 7200.0f;
    }

    public String getDetailDate(Date date) {
        return new SimpleDateFormat("E,dd-MM-yyyy HH:mm:ss", EPGUtil.getLocaleLan()).format(date);
    }

    public long setDate(long curTime, int day, long startHour) {
        MtkLog.e(TAG, "setDate:" + day + "==>" + startHour);
        return ((((long) (day * 24)) + startHour) * 60 * 60) + curTime;
    }

    public String getSimpleDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy", EPGUtil.getLocaleLan()).format(date);
    }

    public String formatProgramTimeInfo(EPGProgramInfo mTVProgramInfo, int timeType12_24) {
        if (mTVProgramInfo == null || mTVProgramInfo.getmTitle() == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        MtkTvTimeFormatBase timeFormatBase = new MtkTvTimeFormatBase();
        MtkLog.d(TAG, "formatProgramTimeInfoForTVSource------>mTVProgramInfo.getmStartTime()=" + mTVProgramInfo.getmStartTime());
        timeFormatBase.set(mTVProgramInfo.getmStartTime().longValue());
        String shour = String.valueOf(timeFormatBase.hour);
        MtkLog.d(TAG, "formatProgramTimeInfoForTVSource------>timeFormatBase.hour=" + timeFormatBase.hour + ",shour=" + shour + ",startDay=" + timeFormatBase.monthDay);
        String smin = String.valueOf(timeFormatBase.minute);
        if (timeFormatBase.minute < 10) {
            smin = "0" + smin;
        }
        MtkLog.d(TAG, "formatProgramTimeInfoForTVSource------>timeFormatBase.minute=" + timeFormatBase.minute + ",smin=" + smin);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(shour);
        sb2.append(":");
        sb2.append(smin);
        String startTime = sb2.toString();
        String monthDay = String.valueOf(timeFormatBase.monthDay);
        String valueOf = String.valueOf(timeFormatBase.month + 1);
        if (timeFormatBase.monthDay + 1 < 10) {
            monthDay = "0" + monthDay;
        }
        String dayTime = EPGUtil.getWeek(timeFormatBase.weekDay) + " , " + monthDay + " - " + EPGUtil.getEngMonthSimple(timeFormatBase.month + 1);
        timeFormatBase.set(mTVProgramInfo.getmEndTime().longValue());
        MtkLog.d(TAG, "formatProgramTimeInfoForTVSource------>endDay=" + timeFormatBase.monthDay);
        String ehour = String.valueOf(timeFormatBase.hour);
        String emin = String.valueOf(timeFormatBase.minute);
        if (timeFormatBase.minute < 10) {
            emin = "0" + emin;
        }
        String endTime = ehour + ":" + emin;
        if (timeType12_24 == 0) {
            startTime = Util.formatTime24_12(startTime);
            endTime = Util.formatTime24_12(endTime);
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(startTime);
        MtkTvTimeFormatBase mtkTvTimeFormatBase = timeFormatBase;
        sb3.append(" - ");
        sb3.append(endTime);
        sb.append(sb3.toString());
        if (mTVProgramInfo.getmEndTime().longValue() - mTVProgramInfo.getmStartTime().longValue() > 86400) {
            sb.append(BannerImplement.STR_TIME_SPAN_TAG);
        }
        sb.append("   " + dayTime);
        MtkLog.d(TAG, "formatProgramTimeInfoForTVSource------>sb.toString()=" + sb.toString());
        return sb.toString();
    }

    public static String converTimeByLong2Str(long time) {
        MtkTvTimeFormatBase timeFormatBase = new MtkTvTimeFormatBase();
        timeFormatBase.set(time);
        String shour = String.valueOf(timeFormatBase.hour);
        String smin = String.valueOf(timeFormatBase.minute);
        if (timeFormatBase.hour < 10) {
            shour = "0" + shour;
        }
        if (timeFormatBase.minute < 10) {
            smin = "0" + smin;
        }
        String strTime = shour + ":" + smin;
        MtkLog.d(TAG, "converTimeByLong2Str------>strTime=" + strTime);
        return strTime;
    }

    public Long getStartTime(ListItemData mTVProgramInfo) {
        return Long.valueOf(egpDataToDate(egpInfoToStr(Long.valueOf(mTVProgramInfo.getMillsStartTime()))).getTime());
    }

    public Long getEndTime(ListItemData mTVProgramInfo) {
        return Long.valueOf(egpDataToDate(egpInfoToStr(Long.valueOf(mTVProgramInfo.getMillsStartTime() + mTVProgramInfo.getMillsDurationTime()))).getTime());
    }

    public Long getStartTime(EPGProgramInfo mTVProgramInfo) {
        return Long.valueOf(egpDataToDate(egpInfoToStr(mTVProgramInfo.getmStartTime())).getTime());
    }

    public Long getEndTime(EPGProgramInfo mTVProgramInfo) {
        return Long.valueOf(egpDataToDate(egpInfoToStr(mTVProgramInfo.getmEndTime())).getTime());
    }

    public String egpInfoToStr(Long epgTime) {
        new StringBuilder();
        MtkTvTimeFormatBase timeFormatBase = new MtkTvTimeFormatBase();
        timeFormatBase.set(epgTime.longValue());
        String shour = String.valueOf(timeFormatBase.hour);
        String smin = String.valueOf(timeFormatBase.minute);
        if (timeFormatBase.hour < 10) {
            shour = "0" + shour;
        }
        if (timeFormatBase.minute < 10) {
            smin = "0" + smin;
        }
        String monthDay = String.valueOf(timeFormatBase.monthDay);
        String month = String.valueOf(timeFormatBase.month + 1);
        if (timeFormatBase.monthDay < 10) {
            monthDay = "0" + monthDay;
        }
        if (timeFormatBase.month + 1 < 10) {
            month = "0" + month;
        }
        int year = timeFormatBase.year;
        MtkLog.d(TAG, "Year:" + year);
        return String.format("%d/%s/%s,%s:%s:%d", new Object[]{Integer.valueOf(year), month, monthDay, shour, smin, Integer.valueOf(timeFormatBase.second)});
    }

    public Date egpDataToDate(String str) {
        Date date = new Date();
        try {
            return new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss", Locale.getDefault()).parse(str.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public String getHourMinite(Date date) {
        return new SimpleDateFormat("HH:mm", EPGUtil.getLocaleLan()).format(date);
    }

    public String getHourMinite(long time) {
        return new SimpleDateFormat("HH:mm", EPGUtil.getLocaleLan()).format(new Date(time));
    }
}
