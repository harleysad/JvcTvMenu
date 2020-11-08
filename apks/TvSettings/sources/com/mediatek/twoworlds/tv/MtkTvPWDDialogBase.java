package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvPWDDialogBase {
    public static final String TAG = "TV_MtkTvPWDDialogBase";

    public boolean checkPWD(String pass) {
        Log.d(TAG, "checkPWD\n");
        return TVNativeWrapper.checkPWD_native(pass);
    }

    public int PWDShow() {
        Log.d(TAG, "PWDShow \n");
        return TVNativeWrapper.PWDShow_native();
    }
}
