package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvCecTimeInfoBase {
    private static final String TAG = "MtkTvCecTimeInfo";
    protected int dayOfMonth;
    protected int duration;
    protected int monthOfYear;
    protected int recSequence;
    protected int startTime;

    public MtkTvCecTimeInfoBase() {
        Log.d(TAG, "MtkTvCecTimeInfo Constructor\n");
    }

    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    /* access modifiers changed from: protected */
    public void setDayOfMonth(int dayOfMonth2) {
        this.dayOfMonth = dayOfMonth2;
    }

    public int getMonthOfYear() {
        return this.monthOfYear;
    }

    /* access modifiers changed from: protected */
    public void setMonthOfYear(int monthOfYear2) {
        this.monthOfYear = monthOfYear2;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime2) {
        this.startTime = startTime2;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration2) {
        this.duration = duration2;
    }

    public int getRecSequence() {
        return this.recSequence;
    }

    public void setRecSequence(int recSequence2) {
        this.recSequence = recSequence2;
    }
}
