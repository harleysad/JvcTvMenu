package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvNetworkBase {
    private final String TAG = "MtkTvNetworkBase";

    public boolean setWifiWolCtl(boolean isOn) {
        Log.d("MtkTvNetworkBase", "Enter setWifiWolCtl Here : " + isOn);
        return TVNativeWrapper.setWifiWolCtl_native(isOn);
    }

    public boolean getWifiWolCtl() {
        Log.d("MtkTvNetworkBase", "Enter getWifiWolCtl Here..");
        return TVNativeWrapper.getWifiWolCtl_native();
    }

    public boolean setEthernetWolCtl(boolean isOn) {
        Log.d("MtkTvNetworkBase", "Enter setEthernetWolCtl Here : " + isOn);
        return TVNativeWrapper.setEthernetWolCtl_native(isOn);
    }

    public boolean getEthernetWolCtl() {
        Log.d("MtkTvNetworkBase", "Enter getEthernetWolCtl Here..");
        return TVNativeWrapper.getEthernetWolCtl_native();
    }
}
