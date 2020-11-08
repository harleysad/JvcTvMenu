package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvMHPBase {
    public static final String TAG = "MtkTvMHPBase";

    public MtkTvMHPBase() {
        Log.d(TAG, "MtkTvMHPBase object created");
    }

    public int enter() {
        Log.d(TAG, "enter entered");
        new MtkTvMHPBase();
        int ret = TVNativeWrapper.enter_native();
        Log.d(TAG, "enter exit " + ret);
        return ret;
    }
}
