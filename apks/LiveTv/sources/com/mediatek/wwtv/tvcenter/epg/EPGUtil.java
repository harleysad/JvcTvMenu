package com.mediatek.wwtv.tvcenter.epg;

import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class EPGUtil {
    private static final String TAG = "EPGUtil";

    public static String formatCurrentTime(Context context) {
        return judgeFormatTime12_24(context) == 1 ? formatCurrentTimeWith24Hours() : formatCurrentTimeWith12Hours();
    }

    public static String formatCurrentTime(Context context, boolean is3rdTVSource) {
        return judgeFormatTime12_24(context) == 1 ? formatCurrentTimeWith24Hours(is3rdTVSource) : formatCurrentTimeWith12Hours(is3rdTVSource);
    }

    public static String formatCurrentTimeWith24Hours() {
        return formatCurrentTimeWith24Hours(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static String formatCurrentTimeWith24Hours(boolean is3rdTVSource) {
        MtkTvTimeFormatBase timeFormat;
        if (is3rdTVSource) {
            timeFormat = MtkTvTime.getInstance().getLocalTime();
        } else {
            timeFormat = MtkTvTime.getInstance().getBroadcastTime();
        }
        String hour = String.valueOf(timeFormat.hour);
        String min = String.valueOf(timeFormat.minute);
        String sec = String.valueOf(timeFormat.second);
        if (timeFormat.minute < 10) {
            min = "0" + min;
        }
        if (timeFormat.second < 10) {
            sec = "0" + sec;
        }
        return getWeekFull(timeFormat.weekDay) + ",  " + timeFormat.monthDay + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + getEngMonthFull(timeFormat.month + 1) + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + timeFormat.year + " " + hour + ":" + min + ":" + sec;
    }

    public static String getEngMonthFull(int month) {
        if (month > DataReader.getInstance().getMonthFullArray().length || month < 1) {
            month = 1;
        }
        return DataReader.getInstance().getMonthFullArray()[month - 1];
    }

    public static String getEngMonthSimple(int month) {
        if (month > DataReader.getInstance().getMonthSimpleArray().length) {
            month = 1;
        }
        return DataReader.getInstance().getMonthSimpleArray()[month - 1];
    }

    public static long getCurrentLocalTimeMills() {
        return MtkTvTime.getInstance().getLocalTime().toMillis();
    }

    public static String formatCurrentTimeWith12Hours() {
        return formatCurrentTimeWith12Hours(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static String formatCurrentTimeWith12Hours(boolean is3rdTVSource) {
        MtkTvTimeFormatBase timeFormat;
        String amp_;
        if (is3rdTVSource) {
            timeFormat = MtkTvTime.getInstance().getLocalTime();
        } else {
            timeFormat = MtkTvTime.getInstance().getBroadcastTime();
        }
        if (timeFormat.hour <= 12) {
            amp_ = " AM";
        } else {
            timeFormat.hour -= 12;
            amp_ = " PM";
        }
        String hour = String.valueOf(timeFormat.hour);
        String min = String.valueOf(timeFormat.minute);
        String sec = String.valueOf(timeFormat.second);
        if (timeFormat.hour == 0) {
            hour = MtkTvRatingConvert2Goo.RATING_STR_12;
        }
        if (timeFormat.minute < 10) {
            min = "0" + min;
        }
        if (timeFormat.second < 10) {
            sec = "0" + sec;
        }
        return getWeekFull(timeFormat.weekDay) + ",  " + timeFormat.monthDay + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + getEngMonthFull(timeFormat.month + 1) + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + timeFormat.year + "  " + hour + ":" + min + ":" + sec + " " + amp_;
    }

    public static String getMDY() {
        MtkTvTimeFormatBase timeFormat;
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            timeFormat = MtkTvTime.getInstance().getLocalTime();
        } else {
            timeFormat = MtkTvTime.getInstance().getBroadcastTime();
        }
        return (timeFormat.month + 1) + "/" + timeFormat.monthDay + "/" + timeFormat.year + " ";
    }

    public static String getYMDLocalTime() {
        MtkTvTimeFormatBase timeFormat;
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            timeFormat = MtkTvTime.getInstance().getLocalTime();
        } else {
            timeFormat = MtkTvTime.getInstance().getBroadcastLocalTime();
        }
        return timeFormat.year + "/" + (timeFormat.month + 1) + "/" + timeFormat.monthDay + " ";
    }

    public static String getSimpleDate(long date) {
        MtkTvTimeFormatBase timeFormat;
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            timeFormat = MtkTvTime.getInstance().getLocalTime();
        } else {
            timeFormat = MtkTvTime.getInstance().getBroadcastTime();
        }
        timeFormat.set(date);
        return timeFormat.monthDay + "/" + (timeFormat.month + 1) + "/" + timeFormat.year + " ";
    }

    public static String getWeekFull(int value) {
        if (value > DataReader.getInstance().getWeekFullArray().length - 1) {
            value = 0;
        }
        return DataReader.getInstance().getWeekFullArray()[value];
    }

    public static String getWeek(int value) {
        if (value > DataReader.getInstance().getWeekSimpleArray().length - 1) {
            value = 0;
        }
        return DataReader.getInstance().getWeekSimpleArray()[value];
    }

    public static String formatTimeFor24(long time) {
        String hourString;
        String minString;
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(time);
        if (timeformat.hour < 10) {
            hourString = "0" + timeformat.hour;
        } else {
            hourString = "" + timeformat.hour;
        }
        if (timeformat.minute < 10) {
            minString = "0" + timeformat.minute;
        } else {
            minString = "" + timeformat.minute;
        }
        return hourString + ":" + minString;
    }

    public static String formatStartTime(long time, Context context) {
        String hourString;
        String minString;
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(time);
        String amp_ = "";
        if (judgeFormatTime12_24(context) == 1) {
            hourString = "" + timeformat.hour;
            MtkLog.d(TAG, "24 formatTime>>hourString" + hourString);
        } else {
            if (timeformat.hour <= 12) {
                amp_ = " AM";
            } else {
                timeformat.hour -= 12;
                amp_ = " PM";
            }
            if (timeformat.hour == 0) {
                hourString = MtkTvRatingConvert2Goo.RATING_STR_12;
            } else {
                hourString = "" + timeformat.hour;
            }
            MtkLog.d(TAG, "12 formatTime>>hourString" + hourString);
        }
        if (timeformat.minute < 10) {
            minString = "0" + timeformat.minute;
        } else {
            minString = "" + timeformat.minute;
        }
        return hourString + ":" + minString + amp_;
    }

    public static String formatTimeFor24(int hour, int minute) {
        String hourString;
        String minString;
        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = "" + hour;
        }
        if (minute < 10) {
            minString = "0" + minute;
        } else {
            minString = "" + minute;
        }
        return hourString + ":" + minString;
    }

    public static String formatTimeFor12(int hour, int minute) {
        String amp_;
        String minString;
        String hourString;
        if (hour < 12) {
            amp_ = " AM";
        } else {
            hour -= 12;
            amp_ = " PM";
        }
        if (minute < 10) {
            minString = "0" + minute;
        } else {
            minString = "" + minute;
        }
        if (hour == 0) {
            hourString = MtkTvRatingConvert2Goo.RATING_STR_12;
        } else {
            hourString = "" + hour;
        }
        return hourString + ":" + minString + amp_;
    }

    public static String formatTime(int hour, int minute, Context context) {
        String strTime = judgeFormatTime12_24(context) == 1 ? formatTimeFor24(hour, minute) : formatTimeFor12(hour, minute);
        MtkLog.d(TAG, "formatTime>>strTime" + strTime);
        return strTime;
    }

    public static int judgeFormatTime12_24(Context context) {
        String timeFormat = Settings.System.getString(context.getContentResolver(), "time_12_24");
        MtkLog.d(TAG, "judgeFormatTime12_24>>timeFormat" + timeFormat);
        if (TextUtils.equals(MtkTvRatingConvert2Goo.RATING_STR_12, timeFormat)) {
            return 0;
        }
        return 1;
    }

    public static String formatTime(long time, Context context) {
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(time);
        String strTime = judgeFormatTime12_24(context) == 1 ? formatTimeFor24(timeformat.hour, timeformat.minute) : formatTimeFor12(timeformat.hour, timeformat.minute);
        MtkLog.d(TAG, "formatTime>>strTime" + strTime);
        return strTime;
    }

    public static String formatEndTime(long startTime, long duration) {
        Date date = new Date(startTime + duration);
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm E,dd-MM", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        String endTimeStr = format.format(gCalendar.getTime());
        if (gCalendar.get(9) == 0) {
            return endTimeStr + " AM";
        }
        return endTimeStr + " PM";
    }

    public static String formatEndTimeDay(long startTime, long duration) {
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(startTime + duration);
        MtkLog.d(TAG, "timeformat.month11>>" + timeformat.month + ">>>" + timeformat.monthDay + ">>>" + timeformat.weekDay + ">>>");
        if (timeformat.month < 0 || timeformat.monthDay < 0 || timeformat.weekDay < 0) {
            return "";
        }
        return getWeek(timeformat.weekDay) + ", " + timeformat.monthDay + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + getEngMonthSimple(timeformat.month + 1);
    }

    public static String getWeekDayofTime(long time, long curDay) {
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(time);
        MtkLog.d(TAG, "getWeekDayofTimetimeformat.toMillis()222>>" + time + "   " + timeformat.toMillis() + "  " + curDay);
        if (timeformat.month < 0 || timeformat.monthDay < 0 || timeformat.weekDay < 0) {
            return "";
        }
        timeformat.set(((time - ((long) (timeformat.hour * MtkTvTimeFormatBase.SECONDS_PER_HOUR))) - ((long) (timeformat.minute * 60))) - ((long) timeformat.second));
        if (timeformat.toMillis() - curDay == 0) {
            return "Today";
        }
        if (timeformat.toMillis() - curDay == 86400) {
            return "Tomorrow";
        }
        if (curDay - timeformat.toMillis() == 86400) {
            return "Yesterday";
        }
        return getWeek(timeformat.weekDay);
    }

    public static int getDayOffset(long time) {
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(time);
        long time2 = ((time - ((long) (timeformat.hour * MtkTvTimeFormatBase.SECONDS_PER_HOUR))) - ((long) (timeformat.minute * 60))) - ((long) timeformat.second);
        timeformat.set(time2);
        long curDay = getCurrentDayStartTime();
        MtkLog.d(TAG, "getDayOffsettimeformat.toMillis()>>" + time2 + "   " + timeformat.toMillis() + "  " + curDay + "  " + ((timeformat.toMillis() - curDay) / 86400));
        return (int) ((timeformat.toMillis() - curDay) / 86400);
    }

    public static String getTodayOrTomorrow(long startTime, long curTime) {
        MtkLog.e(TAG, "getTodayOrTomorrow:starttime:" + startTime + "==>curtime:" + curTime);
        StringBuilder sb = new StringBuilder();
        sb.append("getTodayOrTomorrow:value:");
        sb.append(startTime - curTime);
        MtkLog.e(TAG, sb.toString());
        MtkTvTimeFormatBase timeformat = new MtkTvTimeFormatBase();
        timeformat.set(startTime);
        int startDay = timeformat.monthDay;
        timeformat.set(curTime);
        int dvalue = timeformat.monthDay - startDay;
        MtkLog.e(TAG, "getTodayOrTomorrow:day:" + dvalue);
        if (dvalue == 0) {
            return "Toady";
        }
        if (dvalue < 0) {
            return "Tomorrow";
        }
        if (dvalue > 0) {
            return "Yesterday";
        }
        return "Toady";
    }

    public static String getProTime(long second, long duration, Context context) {
        String v1 = formatStartTime(second, context);
        String v2 = formatStartTime(second + duration, context);
        String v3 = formatEndTimeDay(second, 0);
        return v1 + " - " + v2 + " " + v3;
    }

    public static long getCurrentTime() {
        return getCurrentTime(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static long getCurrentTime(boolean is3rdTVSource) {
        MtkTvTimeFormatBase tvTime;
        if (is3rdTVSource) {
            tvTime = MtkTvTime.getInstance().getLocalTime();
        } else {
            tvTime = MtkTvTime.getInstance().getBroadcastTime();
        }
        return tvTime.toMillis();
    }

    public static long getCurrentDayStartTime() {
        return getCurrentDayStartTime(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static long getCurrentDayStartTime(boolean is3rdTVSource) {
        MtkTvTimeFormatBase tvTime;
        if (is3rdTVSource) {
            tvTime = MtkTvTime.getInstance().getLocalTime();
        } else {
            tvTime = MtkTvTime.getInstance().getBroadcastTime();
        }
        MtkLog.d(TAG, "time in year:" + tvTime.hour);
        MtkLog.d(TAG, "time in year:" + tvTime.minute);
        MtkLog.d(TAG, "time in year:" + tvTime.second);
        return ((tvTime.toMillis() - ((long) (tvTime.hour * MtkTvTimeFormatBase.SECONDS_PER_HOUR))) - ((long) (tvTime.minute * 60))) - ((long) tvTime.second);
    }

    public static int getCurrentHour() {
        return getCurrentHour(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static int getCurrentHour(boolean is3rdTVSource) {
        MtkTvTimeFormatBase tvTime;
        if (is3rdTVSource) {
            tvTime = MtkTvTime.getInstance().getLocalTime();
        } else {
            tvTime = MtkTvTime.getInstance().getBroadcastTime();
        }
        MtkLog.d(TAG, "getCurrentHour ,tvTime.month+1: " + tvTime.month + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("getCurrentHour ,tvTime.monthDay: ");
        sb.append(tvTime.monthDay);
        MtkLog.d(TAG, sb.toString());
        MtkLog.d(TAG, "getCurrentHour ,tvTime.hour: " + tvTime.hour);
        String countryCode = MtkTvConfig.getInstance().getCountry();
        MtkLog.d(TAG, "getCurrentHour ,countryCode: " + countryCode);
        if (!countryCode.equals("NZL") || tvTime.month + 1 != 4 || tvTime.monthDay > 7 || tvTime.hour > 2 || tvTime.hour < 1) {
            return tvTime.hour;
        }
        MtkLog.d(TAG, "getCurrentHour ,- 1 hour");
        return tvTime.hour - 1;
    }

    public static long getCurrentDayHourMinute() {
        return getCurrentDayHourMinute(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static long getCurrentDayHourMinute(boolean is3rdTVSource) {
        MtkTvTimeFormatBase tvTime;
        if (is3rdTVSource) {
            tvTime = MtkTvTime.getInstance().getLocalTime();
        } else {
            tvTime = MtkTvTime.getInstance().getBroadcastTime();
        }
        return (tvTime.toMillis() - ((long) (tvTime.minute * 60))) - ((long) tvTime.second);
    }

    public static long getCurrentDateDayAsMills() {
        return getCurrentDateDayAsMills(CommonIntegration.getInstance().is3rdTVSource());
    }

    public static long getCurrentDateDayAsMills(boolean is3rdTVSource) {
        MtkTvTimeFormatBase mtkTvTimeFormatBase;
        if (is3rdTVSource) {
            mtkTvTimeFormatBase = MtkTvTime.getInstance().getLocalTime();
        } else {
            mtkTvTimeFormatBase = MtkTvTime.getInstance().getBroadcastTime();
        }
        return ((mtkTvTimeFormatBase.toMillis() - ((long) ((mtkTvTimeFormatBase.hour * 60) * 60))) - ((long) (mtkTvTimeFormatBase.minute * 60))) - ((long) mtkTvTimeFormatBase.second);
    }

    public static long getEpgLastTimeMills(int dayNum, int startHour, boolean withHour) {
        return getEpgLastTimeMills(dayNum, startHour, withHour, CommonIntegration.getInstance().is3rdTVSource());
    }

    public static long getEpgLastTimeMills(int dayNum, int startHour, boolean withHour, boolean is3rdTVSource) {
        MtkTvTimeFormatBase mtkTvTimeFormatBase;
        if (is3rdTVSource) {
            mtkTvTimeFormatBase = MtkTvTime.getInstance().getLocalTime();
        } else {
            mtkTvTimeFormatBase = MtkTvTime.getInstance().getBroadcastTime();
        }
        long curTimeMillSeconds = mtkTvTimeFormatBase.toMillis();
        if (!withHour) {
            curTimeMillSeconds = ((curTimeMillSeconds - ((long) ((mtkTvTimeFormatBase.hour * 60) * 60))) - ((long) (mtkTvTimeFormatBase.minute * 60))) - ((long) mtkTvTimeFormatBase.second);
        }
        return ((long) (((dayNum * 24) + startHour) * 60 * 60)) + curTimeMillSeconds;
    }

    public static int getEUIntervalHour(int dayNum, int startHour, int intervalHour) {
        return getEUIntervalHour(dayNum, startHour, intervalHour, CommonIntegration.getInstance().is3rdTVSource());
    }

    public static int getEUIntervalHour(int dayNum, int startHour, int intervalHour, boolean is3rdTVSource) {
        MtkTvTimeFormatBase mtkTvTimeFormatBase;
        MtkLog.d(TAG, "dayNum>>>" + dayNum + "  " + startHour + "  " + intervalHour);
        if (is3rdTVSource) {
            mtkTvTimeFormatBase = MtkTvTime.getInstance().getLocalTime();
        } else {
            mtkTvTimeFormatBase = MtkTvTime.getInstance().getBroadcastTime();
        }
        mtkTvTimeFormatBase.set(((long) (((dayNum * 24) + startHour + intervalHour) * 60 * 60)) + (((mtkTvTimeFormatBase.toMillis() - ((long) ((mtkTvTimeFormatBase.hour * 60) * 60))) - ((long) (mtkTvTimeFormatBase.minute * 60))) - ((long) mtkTvTimeFormatBase.second)));
        return mtkTvTimeFormatBase.hour;
    }

    public static Locale getLocaleLan() {
        return Locale.US;
    }

    public static void setPositon(int xxxx, int yyyy, Dialog dialog) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = xxxx;
        lp.y = yyyy;
        MtkLog.e(TAG, "cec:xxxx:" + xxxx + "_yyyy:" + yyyy);
        window.setAttributes(lp);
    }
}
