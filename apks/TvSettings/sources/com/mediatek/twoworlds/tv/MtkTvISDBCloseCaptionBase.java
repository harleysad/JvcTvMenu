package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvISDBCloseCaptionBase {
    public static final String TAG = "MtkTvISDBCloseCaption";

    public MtkTvISDBCloseCaptionBase() {
        Log.d(TAG, "MtkTvISDBCloseCaptionBase object created");
    }

    public int ISDBCCEnable(boolean flag) {
        Log.d(TAG, "ISDBCCEnable: " + flag + " \n");
        return TVNativeWrapper.ISDBCCEnable_native(flag);
    }

    public int ISDBCCNextStream() {
        Log.d(TAG, "ISDBCCNextStream. \n");
        return TVNativeWrapper.ISDBCCNextStream_native();
    }

    public int ISDBCCGetCCString() {
        Log.d(TAG, "ISDBCCGetCCString \n");
        return TVNativeWrapper.ISDBCCGetCCString_native();
    }
}
