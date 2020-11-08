package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvTimeFormatBase {
    public static final int SECONDS_PER_HOUR = 3600;
    public static final String TAG = "MtkTvTimeFormat";
    public int hour;
    public boolean isDstOn;
    public boolean isGmt;
    public int minute;
    public int month;
    public int monthDay;
    public int second;
    public int weekDay;
    public int year;

    public MtkTvTimeFormatBase() {
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = 1;
        this.month = 0;
        this.year = 1970;
        this.weekDay = 0;
        this.isGmt = true;
    }

    public MtkTvTimeFormatBase(MtkTvTimeFormatBase other) {
        set(other);
    }

    public void setByUtc(long seconds) {
        TVNativeWrapper.convertUTCSecToDTG_native(seconds, this);
    }

    public void setByUtcAndConvertToLocalTime(long second2) {
        TVNativeWrapper.convertMillisToLocalTime_native(second2, this);
    }

    public void set(MtkTvTimeFormatBase that) {
        this.second = that.second;
        this.minute = that.minute;
        this.hour = that.hour;
        this.monthDay = that.monthDay;
        this.month = that.month;
        this.year = that.year;
        this.weekDay = that.weekDay;
        this.isGmt = that.isGmt;
    }

    public void set(int second2, int minute2, int hour2, int monthDay2, int month2, int year2) {
        this.second = second2;
        this.minute = minute2;
        this.hour = hour2;
        this.monthDay = monthDay2;
        this.month = month2;
        this.year = year2;
    }

    public long toSeconds() {
        return TVNativeWrapper.convertDTGToSeconds_native(this);
    }

    public void print(String TAG2) {
        Log.d(TAG2, "" + this.year + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + this.month + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + this.monthDay + " " + this.hour + ":" + this.minute + ":" + this.second + "  weekofday:" + this.weekDay + " isDst:" + this.isDstOn + "   isGmt:" + this.isGmt);
    }

    public long toMillis() {
        long millis = TVNativeWrapper.convertLocalTimeToMillis_native(this);
        Log.d(TAG, "toMillis() " + millis + "\n");
        return millis;
    }

    public void set(long second2) {
        setByUtcAndConvertToLocalTime(second2);
    }
}
