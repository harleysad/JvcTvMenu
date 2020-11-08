package com.mediatek.net;

import android.util.Log;

public class MtkNetworkNative {
    private static final String TAG = "MtkNetworkNative";
    private String mInterface;

    public static native boolean isWoPacketEnableNative();

    public static native boolean setEnableWifiCSANative(boolean z);

    public static native boolean setEnableWifiPsAwakeNative(boolean z);

    public static native boolean setWifiPsAwakeIntervalNative(int i);

    public static native boolean setWoPacketNative(boolean z);

    public static native boolean wifiNativeSetWowl(boolean z);

    static {
        Log.d(TAG, "MtkNetworkNative begin load  libcom_mediatek_net_jni.so !");
        try {
            System.loadLibrary("com_mediatek_net_jni");
        } catch (UnsatisfiedLinkError e) {
            Log.i(TAG, "WARNING: Could not load library!");
        }
    }

    public MtkNetworkNative(String iface) {
        Log.d(TAG, "structure");
        this.mInterface = iface;
    }
}
