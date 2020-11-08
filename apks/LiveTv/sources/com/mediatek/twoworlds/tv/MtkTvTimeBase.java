package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvTimeRawDataBase;

public class MtkTvTimeBase {
    public static final int MTK_TV_TIME_CVT_TYPE_BRDCST_LOCAL_TO_BRDCST_UTC = 6;
    public static final int MTK_TV_TIME_CVT_TYPE_BRDCST_LOCAL_TO_SYS_UTC = 2;
    public static final int MTK_TV_TIME_CVT_TYPE_BRDCST_UTC_TO_BRDCST_LOCAL = 5;
    public static final int MTK_TV_TIME_CVT_TYPE_BRDCST_UTC_TO_SYS_UTC = 1;
    public static final int MTK_TV_TIME_CVT_TYPE_SYS_UTC_TO_BRDCST_LOCAL = 4;
    public static final int MTK_TV_TIME_CVT_TYPE_SYS_UTC_TO_BRDCST_UTC = 3;
    public static final int MTK_TV_TIME_NOT_SYNC = 0;
    public static final int MTK_TV_TIME_SLEEP_TIMER_120_MIN = 6;
    public static final int MTK_TV_TIME_SLEEP_TIMER_15_MIN = 1;
    public static final int MTK_TV_TIME_SLEEP_TIMER_30_MIN = 2;
    public static final int MTK_TV_TIME_SLEEP_TIMER_45_MIN = 3;
    public static final int MTK_TV_TIME_SLEEP_TIMER_60_MIN = 4;
    public static final int MTK_TV_TIME_SLEEP_TIMER_90_MIN = 5;
    public static final int MTK_TV_TIME_SLEEP_TIMER_OFF = 0;
    public static final int MTK_TV_TIME_SYNC_FROM_NTP = 2;
    public static final int MTK_TV_TIME_SYNC_FROM_TS = 1;
    public static final String TAG = "MtkTvTime";
    private static int mLastDtCond = -1;

    public MtkTvTimeFormatBase getBroadcastUtcTime() {
        MtkTvTimeFormatBase time = new MtkTvTimeFormatBase();
        TVNativeWrapper.getBroadcastTime_native(time);
        Log.d(TAG, "broadcastTime in utc with MtkTvTimeFormatBase instance: \n");
        return time;
    }

    public long getBroadcastTimeInUtcSeconds() {
        long seconds = TVNativeWrapper.getBroadcastTime_native(new MtkTvTimeFormatBase());
        Log.d(TAG, "broadcastTime in seconds: " + seconds);
        return seconds;
    }

    public boolean isReceivedTOT() {
        Log.d(TAG, "isReceivedTOT \n");
        return TVNativeWrapper.isReceivedTOT_native();
    }

    public int convertTime(int type, MtkTvTimeFormatBase from, MtkTvTimeFormatBase to) {
        return TVNativeWrapper.convertTime_native(type, from, to);
    }

    public MtkTvTimeRawDataBase getBrdcstRawData() {
        return TVNativeWrapper.getBrdcstRawData_native();
    }

    public MtkTvTimeFormatBase getBroadcastLocalTime() {
        Log.d(TAG, "getBroadcastLocalTime \n");
        MtkTvTimeFormatBase time = new MtkTvTimeFormatBase();
        long seconds = TVNativeWrapper.getBroadcastTime_native(time);
        Log.d(TAG, "broadcastTime in seconds: " + seconds + "\n");
        time.setByUtcAndConvertToLocalTime(seconds);
        return time;
    }

    public long getTimeZone() {
        Log.d(TAG, "getTimeZone ");
        return TVNativeWrapper.getTimeZone_native();
    }

    public int getDst() {
        return (int) TVNativeWrapper.getTimeOffset_native();
    }

    public int getSleepTimer() {
        return getSleepTimer(true);
    }

    public int getSleepTimer(boolean direction) {
        int val = TVNativeWrapper.getSleepTimer_native(direction);
        Log.d(TAG, "sleepTimer :" + val + "\n");
        return val;
    }

    public void setSleepTimer(int timer) {
        Log.d(TAG, "setSleepTimer to:" + timer + " \n");
        if (timer < 0 || timer > 6) {
            throw new IllegalArgumentException("arg is out of range");
        }
        TVNativeWrapper.setSleepTimer_native(timer);
    }

    public int getTotalOperationTime() {
        Log.d(TAG, "totalOperationTime \n");
        return TVNativeWrapper.getConfigValue_native(-1, MtkTvConfigTypeBase.CFG_MISC_TOTAL_OP_TIME);
    }

    public int getSleepTimerRemainingTime() {
        Log.d(TAG, "getSleepTimerRemainingTime \n");
        return TVNativeWrapper.getSleepTimerRemainingTime_native();
    }

    public void setLocalTimeZone(String timezone) {
        TVNativeWrapper.setLocalTimeZone_native(timezone);
        Log.d(TAG, "setLocalTimeZone: " + timezone);
    }

    public MtkTvTimeFormatBase getLocalTime() {
        long millis = TVNativeWrapper.getUtcTime_native();
        Log.d(TAG, "time in millis:" + millis + " \n");
        MtkTvTimeFormatBase time = new MtkTvTimeFormatBase();
        time.set(millis / 1000);
        return time;
    }

    public void setLocalTime(MtkTvTimeFormatBase when) {
        Log.d(TAG, "setLocalTime \n");
        long millis = when.toMillis();
        Log.d(TAG, "setTime:" + millis + "\n");
        TVNativeWrapper.setUtcTime_native(millis);
    }

    public boolean isLocalTimeEditable() {
        Log.d(TAG, "isLocalTimeEditable \n");
        return TVNativeWrapper.isTimeEditable_native();
    }

    public long getCurrentTimeInUtcSeconds() {
        long seconds = TVNativeWrapper.getUtcTime_native() / 1000;
        Log.d(TAG, "getTime seconds:" + seconds);
        return seconds;
    }

    public long getCurrentTimeInUtcMilliSeconds() {
        long milliSeconds = TVNativeWrapper.getUtcTime_native();
        Log.d(TAG, "getTime milliSeconds:" + milliSeconds);
        return milliSeconds;
    }

    public void setCurrentTimeInUtcSeconds(long seconds) {
        Log.d(TAG, "setTime seconds = " + seconds);
        TVNativeWrapper.setUtcTime_native(seconds);
    }

    public int getTimeSyncSource() {
        Log.d(TAG, "getTimeSyncSource ");
        return TVNativeWrapper.getTimeSyncSource_native();
    }

    public void setTimeSyncSource(int syncSource) {
        Log.d(TAG, "setTimeSyncSource ");
        if (syncSource < 0 || syncSource > 2) {
            throw new IllegalArgumentException("arg is out of range");
        }
        TVNativeWrapper.setTimeSyncSource_native(syncSource);
    }

    public void setTimeZone(long timeZone) {
        Log.d(TAG, "setTimeZone ");
        TVNativeWrapper.setTimeZone_native(timeZone);
    }

    public boolean isTimeZoneEditable() {
        Log.d(TAG, "isTimeZoneEditable ");
        return TVNativeWrapper.isTimeZoneEditable_native();
    }

    public void setDst(int dst) {
        Log.d(TAG, "setDst = " + dst);
        TVNativeWrapper.setTimeOffset_native((long) dst);
    }

    public MtkTvTimeFormatBase getBroadcastTime() {
        MtkTvTimeFormatBase time = new MtkTvTimeFormatBase();
        long seconds = TVNativeWrapper.getBroadcastTime_native(time);
        Log.d(TAG, "broadcastTime in seconds: " + seconds + "\n");
        time.set(seconds);
        return time;
    }

    public boolean isDstEditable() {
        Log.d(TAG, "isDstEditable \n");
        return TVNativeWrapper.isTimeOffsetEditable_native();
    }
}
