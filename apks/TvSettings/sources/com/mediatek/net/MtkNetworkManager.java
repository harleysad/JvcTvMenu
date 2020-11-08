package com.mediatek.net;

import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvNetwork;

public class MtkNetworkManager {
    private static final String TAG = "MtkNetworkManager";
    private static MtkNetworkManager mInstance;
    private MtkTvConfig mMtkTvConfig = null;
    private MtkNetworkNative mNative = null;
    private MtkTvNetwork mNetwork = null;

    public static MtkNetworkManager getInstance() {
        if (mInstance == null) {
            mInstance = new MtkNetworkManager();
        }
        return mInstance;
    }

    private MtkNetworkManager() {
        Log.d(TAG, "Create MtkNetworkManager");
        this.mNetwork = MtkTvNetwork.getInstance();
        this.mMtkTvConfig = MtkTvConfig.getInstance();
        this.mNative = new MtkNetworkNative("wlan0");
    }

    public boolean enableWolAndWoWL(boolean b) {
        boolean result = setEnableWoWL(b) && setEnableWol(b);
        Log.d(TAG, "enableWolAndWoWL api result is " + result);
        return result;
    }

    public boolean isWolAndWowlEnable() {
        boolean result = isEnanbleWoWL() && isEnableWol();
        Log.d(TAG, "isWolAndWowlEnable api result is " + result);
        return result;
    }

    public boolean setEnableWoPacket(boolean b) {
        MtkNetworkNative mtkNetworkNative = this.mNative;
        boolean result = MtkNetworkNative.setWoPacketNative(b);
        Log.d(TAG, "setEnableWoPacket api driver result is " + result);
        if (!result) {
            return result;
        }
        boolean z = false;
        if (this.mMtkTvConfig.setConfigValue(1, "g_network__wake_on_packet", b ? 1 : 0, 0) == 0) {
            z = true;
        }
        boolean result2 = z;
        Log.d(TAG, "setEnableWoPacket api TvConfig result is " + result2);
        return result2;
    }

    public boolean isWoPacketEnable() {
        int ret = this.mMtkTvConfig.getConfigValue(1, "g_network__wake_on_packet");
        Log.d(TAG, "isWoPacketEnable api getTvConfig value is " + ret);
        if (ret == 1) {
            return true;
        }
        return false;
    }

    public boolean setWifiPsAwakeInterval(int interval) {
        MtkNetworkNative mtkNetworkNative = this.mNative;
        boolean result = MtkNetworkNative.setWifiPsAwakeIntervalNative(interval);
        Log.d(TAG, "setWifiPsAwakeInterval api result is " + result);
        return result;
    }

    public boolean setEnableWifiPsAwake(boolean b) {
        MtkNetworkNative mtkNetworkNative = this.mNative;
        boolean result = MtkNetworkNative.setEnableWifiPsAwakeNative(b);
        Log.d(TAG, "setEnableWifiPsAwake api driver result is " + result);
        if (!result) {
            return result;
        }
        boolean z = false;
        if (this.mMtkTvConfig.setConfigValue(1, "g_misc__ps_awake", b ? 1 : 0, 0) == 0) {
            z = true;
        }
        boolean result2 = z;
        Log.d(TAG, "setEnableWifiPsAwake api TvConfig result is " + result2);
        return result2;
    }

    public boolean setEnableWifiCSA(boolean b) {
        MtkNetworkNative mtkNetworkNative = this.mNative;
        boolean result = MtkNetworkNative.setEnableWifiCSANative(b);
        Log.d(TAG, "setEnableWifiCSA api driver result is " + result);
        if (!result) {
            return result;
        }
        boolean z = false;
        if (this.mMtkTvConfig.setConfigValue(1, "g_misc__wifi_csa", b ? 1 : 0, 0) == 0) {
            z = true;
        }
        boolean result2 = z;
        Log.d(TAG, "setEnableWifiCSA api TvConfig result is " + result2);
        return result2;
    }

    public boolean isWifiPsAwakeEnable() {
        int ret = this.mMtkTvConfig.getConfigValue(1, "g_misc__ps_awake");
        Log.d(TAG, "isWifiPsAwakeEnable api result is " + ret);
        if (ret == 1) {
            return true;
        }
        return false;
    }

    public boolean isWifiCSAEnable() {
        int ret = this.mMtkTvConfig.getConfigValue(1, "g_misc__wifi_csa");
        Log.d(TAG, "isWifiCSAEnable api result is " + ret);
        if (ret == 1) {
            return true;
        }
        return false;
    }

    public boolean isEnanbleWoWL() {
        boolean result = this.mNetwork.getWifiWolCtl();
        Log.d(TAG, "isEnanbleWoWL api result is " + result);
        return result;
    }

    public boolean setEnableWoWL(boolean enable) {
        MtkNetworkNative mtkNetworkNative = this.mNative;
        boolean result = MtkNetworkNative.wifiNativeSetWowl(enable);
        Log.d(TAG, "setEnableWoWL api driver result is " + result);
        if (!result) {
            return result;
        }
        boolean result2 = this.mNetwork.setWifiWolCtl(enable);
        Log.d(TAG, "setEnableWoWL api TvConfig result is " + result2);
        return result2;
    }

    public boolean setEnableWol(boolean enable) {
        boolean result = this.mNetwork.setEthernetWolCtl(enable);
        Log.d(TAG, "setEnableWol api TvConfig result is " + result);
        return result;
    }

    public boolean isEnableWol() {
        boolean result = this.mNetwork.getEthernetWolCtl();
        Log.d(TAG, "isEnableWol api result is " + result);
        return result;
    }
}
