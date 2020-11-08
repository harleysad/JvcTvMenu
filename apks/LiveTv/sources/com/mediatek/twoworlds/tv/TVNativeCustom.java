package com.mediatek.twoworlds.tv;

public class TVNativeCustom {
    private static final String TAG = "TVNative";

    protected static native int genericSetAPI_native(int i, int i2, int i3);

    protected static native int setAndroidWorldInfoToLinux_native(int i, int i2);

    protected static native int testCustom_native(int i, int i2);

    static {
        TvDebugLog.i(TAG, "Load libcom_mediatek_twoworlds_tv_jni.so start !");
        System.loadLibrary("com_mediatek_twoworlds_tv_jni");
        TvDebugLog.i(TAG, "Load libcom_mediatek_twoworlds_tv_jni.so OK !");
    }
}
