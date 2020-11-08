package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvTimeRawDataBase {
    public static final String TAG = "MtkTvTimeRawDataBase";
    public boolean isBrdcstTimeAvailable;
    public boolean isTotLtoMatched;
    public long localTimeOffset;
    public long nextTimeOffset;
    public long timeOfChanage;

    public MtkTvTimeRawDataBase() {
        this.isTotLtoMatched = false;
        this.localTimeOffset = 0;
        this.nextTimeOffset = 0;
        this.timeOfChanage = 0;
        this.isBrdcstTimeAvailable = false;
    }

    public MtkTvTimeRawDataBase(boolean isTotLtoMatched2, boolean isBrdcstTimeAvailable2, long localTimeOffset2, long nextTimeOffset2, long timeOfChanage2) {
        this.isTotLtoMatched = isTotLtoMatched2;
        this.localTimeOffset = localTimeOffset2;
        this.nextTimeOffset = nextTimeOffset2;
        this.timeOfChanage = timeOfChanage2;
        this.isBrdcstTimeAvailable = isBrdcstTimeAvailable2;
    }

    public void print(String TAG2) {
        Log.d(TAG2, "    isTotLtoMatched :" + this.isTotLtoMatched + "    localTimeOffset:" + this.localTimeOffset + "    nextTimeOffset:" + this.nextTimeOffset + "    timeOfChanage:" + this.timeOfChanage + "    isBrdcstTimeAvailable:" + this.isBrdcstTimeAvailable);
    }
}
