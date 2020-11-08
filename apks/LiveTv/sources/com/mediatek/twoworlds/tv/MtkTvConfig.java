package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvConfig extends MtkTvConfigBase {
    private static MtkTvConfig mtkTvConfig = null;

    private MtkTvConfig() {
    }

    public static MtkTvConfig getInstance() {
        if (mtkTvConfig != null) {
            return mtkTvConfig;
        }
        mtkTvConfig = new MtkTvConfig();
        return mtkTvConfig;
    }

    public int setAndroidWorldInfoToLinux(int mode, int value) {
        int relValue;
        Log.d(MtkTvConfigBase.TAG, "Enter setAndroidWorldInfoToLinux\n");
        synchronized (MtkTvConfig.class) {
            relValue = TVNativeWrapperCustom.setAndroidWorldInfoToLinux_native(mode, value);
        }
        Log.d(MtkTvConfigBase.TAG, "Leave setAndroidWorldInfoToLinux, return value = " + relValue + "\n");
        return relValue;
    }
}
