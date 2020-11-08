package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvDvbcManualScanParaBase {
    private static final String TAG = "TV_MtkTvDvbcManualScanPara";
    private boolean isScanUp = false;
    private int mEndFreq = 862000;
    private int mStartFreq = 47000;

    public int setScanStartFreq(int mValue) {
        Log.d(TAG, "Enter setScanFreq\n");
        int ret = 0;
        if (mValue < 47000 || mValue > 862000) {
            ret = -1;
        } else {
            this.mStartFreq = mValue;
        }
        Log.d(TAG, "Leave setScanFreq\n");
        return ret;
    }

    public int getScanStartFreq() {
        Log.d(TAG, "getScanStartFreq Ocurr\n");
        return this.mStartFreq;
    }

    public int setScanEndFreq(int mValue) {
        Log.d(TAG, "Enter setScanFreq\n");
        int ret = 0;
        if (mValue < 47000 || mValue > 862000) {
            ret = -1;
        } else {
            this.mEndFreq = mValue;
        }
        Log.d(TAG, "Leave setScanFreq\n");
        return ret;
    }

    public int getScanEndFreq() {
        Log.d(TAG, "getScanEndFreq Ocurr\n");
        return this.mEndFreq;
    }

    public int setScanUp(boolean mValue) {
        Log.d(TAG, "Enter setScanUp\n");
        this.isScanUp = mValue;
        Log.d(TAG, "Leave setScanUp\n");
        return 0;
    }

    public boolean getScanUp() {
        Log.d(TAG, "getScanUp Ocurr\n");
        return this.isScanUp;
    }
}
