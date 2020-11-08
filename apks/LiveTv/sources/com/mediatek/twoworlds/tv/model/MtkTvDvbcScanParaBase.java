package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvDvbcScanParaBase {
    private static final String TAG = "TV_MtkTvDvbcScanPara";
    private int mFreq = -1;
    private int mNetWorkId = -1;
    private DvbcNitMode mNitMode = DvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF;

    public enum DvbcNitMode {
        DVBC_NIT_SEARCH_MODE_OFF,
        DVBC_NIT_SEARCH_MODE_QUICK,
        DVBC_NIT_SEARCH_MODE_EX_QUICK,
        DVBC_NIT_SEARCH_MODE_NUM
    }

    public int setNetWorkID(int mValue) {
        Log.d(TAG, "Enter setNetWorkID\n");
        int ret = 0;
        if (mValue < 0 || mValue > 9999) {
            ret = -1;
        } else {
            this.mNetWorkId = mValue;
        }
        Log.d(TAG, "Leave setNetWorkID\n");
        return ret;
    }

    public int getNetWorkID() {
        Log.d(TAG, "getNetWorkID Ocurr\n");
        return this.mNetWorkId;
    }

    public int setNitMode(DvbcNitMode mValue) {
        Log.d(TAG, "Enter setNitMode\n");
        this.mNitMode = mValue;
        Log.d(TAG, "Leave setNitMode\n");
        return 0;
    }

    public int getNitMode() {
        Log.d(TAG, "getScanStartFreq Ocurr\n");
        if (DvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF == this.mNitMode) {
            return 0;
        }
        if (DvbcNitMode.DVBC_NIT_SEARCH_MODE_QUICK == this.mNitMode) {
            return 1;
        }
        if (DvbcNitMode.DVBC_NIT_SEARCH_MODE_EX_QUICK == this.mNitMode) {
            return 2;
        }
        return 0;
    }

    public int setFreq(int mValue) {
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

    public int getFreq() {
        Log.d(TAG, "getScanStartFreq Ocurr\n");
        return this.mFreq;
    }
}
