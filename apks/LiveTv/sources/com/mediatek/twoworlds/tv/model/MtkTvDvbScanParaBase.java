package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvDvbScanParaBase {
    private static final String TAG = "TV_MtkTvDvbScanPara";
    private boolean isScanUp = false;
    private int mFreq = 47000;
    private String s_rf_name = null;

    public int setScanFreq(int mValue) {
        Log.d(TAG, "Enter setScanFreq\n");
        int ret = 0;
        if (mValue < 47000 || mValue > 862000) {
            ret = -1;
        } else {
            this.mFreq = mValue;
        }
        Log.d(TAG, "Leave setScanFreq\n");
        return ret;
    }

    public int getScanFreq() {
        Log.d(TAG, "getScanFreq Ocurr\n");
        return this.mFreq;
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
