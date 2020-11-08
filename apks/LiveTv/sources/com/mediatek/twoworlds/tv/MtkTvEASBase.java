package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvEASParaBase;

public class MtkTvEASBase {
    private static final String TAG = "MtkTvEAS";

    public MtkTvEASBase() {
        Log.d(TAG, "MtkTvEASBase object created");
    }

    public int getEASCurrentStatus(MtkTvEASParaBase mEASPara) {
        Log.d(TAG, "Enter getEASCurrentStatus Here.\n");
        int ret = TVNativeWrapper.getEASCurrentStatus_native(mEASPara);
        if (ret != 0) {
            Log.e(TAG, "TVNativeWrapper.getEASCurrentStatus_native failed! return " + ret + ".\n");
        }
        return ret;
    }

    public int EASSetAndroidLaunchStatus(boolean b_enter) {
        Log.d(TAG, "EASSetAndroidLaunchStatus=" + b_enter);
        return TVNativeWrapper.setEASAndroidLaunchStatus_native(b_enter);
    }

    public boolean EASGetAndroidLaunchStatus() {
        Log.d(TAG, "EASGetAndroidLaunchStatus");
        boolean isEnter = TVNativeWrapper.getEASAndroidLaunchStatus_native();
        Log.d(TAG, "EASGetAndroidLaunchStatus " + isEnter);
        Log.d(TAG, "Leave EASGetAndroidLaunchStatus.");
        return isEnter;
    }
}
