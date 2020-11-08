package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvScreenSaverBase {
    public static final String TAG = "TV_MtkTvScreenSaverBase";

    public int getScrnSvrMsgID() {
        Log.d(TAG, "ScreenSaverGetMsgID \n");
        return TVNativeWrapper.getScrnSvrMsgID_native();
    }
}
