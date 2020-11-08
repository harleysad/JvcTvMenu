package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvHighLevelBase {
    public static final String TAG = "MtkTvHighLevel";

    public MtkTvHighLevelBase() {
        Log.d(TAG, "MtkTvHighlevel object created");
    }

    public int getCurrentTvMode() {
        Log.d(TAG, "getCurrentTvMode");
        int i = TVNativeWrapper.HighLevel_native(107, 0, 0, 0, 0, 0, 0);
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        return 2;
    }

    public int startTV() {
        Log.d(TAG, "startTV");
        TVNativeWrapper.HighLevel_native(100, 0, 0, 0, 0, 0, 0);
        return 0;
    }

    public int stopTV() {
        Log.d(TAG, "stopTV");
        TVNativeWrapper.HighLevel_native(101, 0, 0, 0, 0, 0, 0);
        return 0;
    }

    public int stopAll() {
        Log.d(TAG, "stopAll");
        TVNativeWrapper.HighLevel_native(102, 0, 0, 0, 0, 0, 0);
        return 0;
    }

    public int launch3rdParty(int third_party_id) {
        Log.d(TAG, "launch_3rd_party id=" + third_party_id);
        if (third_party_id <= 0 || third_party_id >= 4) {
            return -1;
        }
        TVNativeWrapper.HighLevel_native(103, 1, third_party_id, 0, 0, 0, 0);
        return 0;
    }

    public int exit3rdParty(int third_party_id) {
        Log.d(TAG, "exit_3rd_party id=" + third_party_id);
        if (third_party_id <= 0 || third_party_id >= 4) {
            return -1;
        }
        TVNativeWrapper.HighLevel_native(103, 0, third_party_id, 0, 0, 0, 0);
        return 0;
    }

    public int launchInternalApp(int internal_app_id) {
        Log.d(TAG, "launch_Internal_App id=" + internal_app_id);
        if (internal_app_id <= 0 || internal_app_id >= 6) {
            return -1;
        }
        TVNativeWrapper.HighLevel_native(104, 1, internal_app_id, 0, 0, 0, 0);
        return 0;
    }

    public int launchInternalApp(int internal_app_id, String launchOption) {
        Log.d(TAG, "launchInternalApp internal_app_id=" + internal_app_id + ", launchOption=" + launchOption);
        if (internal_app_id <= 0 || internal_app_id >= 6 || internal_app_id != 3) {
            return -1;
        }
        int typeStart = launchOption.indexOf("=");
        int typeEnd = launchOption.indexOf(",");
        String type = launchOption.substring(typeStart + 1, typeEnd).trim().replace(" ", "");
        Log.d(TAG, "launchInternalApp typeStart=" + typeStart + ", typeEnd=" + typeEnd + ", type=" + type);
        int serviceStart = launchOption.indexOf("=", typeEnd + 1);
        int serviceEnd = launchOption.indexOf(",", typeEnd + 1);
        String service = launchOption.substring(serviceStart + 1, serviceEnd).trim().replace(" ", "");
        Log.d(TAG, "launchInternalApp serviceStart=" + serviceStart + ", serviceEnd=" + serviceEnd + ", service=" + service);
        int urlStart = launchOption.indexOf("=", serviceStart + 1);
        String url = launchOption.substring(urlStart + 1).trim().replace(" ", "");
        Log.d(TAG, "launchInternalApp urlStart=" + urlStart + ", url=" + url);
        return TVNativeWrapper.launchHbbtv_native(type, service, url);
    }

    public int exitInternalApp(int internal_app_id) {
        Log.d(TAG, "exit_Internal_App id=" + internal_app_id);
        if (internal_app_id <= 0 || internal_app_id >= 6) {
            return -1;
        }
        TVNativeWrapper.HighLevel_native(104, 0, internal_app_id, 0, 0, 0, 0);
        return 0;
    }

    public int stopHbbtv() {
        Log.d(TAG, "stopHbbtv");
        return TVNativeWrapper.HighLevel_native(110, 0, 0, 0, 0, 0, 0);
    }

    public int setViewPort(boolean is_decimated_video, int x, int y, int w, int h) {
        boolean z = is_decimated_video;
        int i = x;
        int i2 = y;
        int i3 = w;
        int i4 = h;
        Log.d(TAG, "setViewPort (Decimated)=" + z + " " + i + " " + i2 + " " + i3 + " " + i4);
        if (i < 0 || i >= 1920 || i2 < 0 || i2 >= 1080 || i3 < 0 || i3 > 1920 || i4 < 0 || i4 > 1080) {
            return -1;
        }
        if (z) {
            TVNativeWrapper.HighLevel_native(105, 1, i, i2, i3, i4, 0);
            return 0;
        }
        TVNativeWrapper.HighLevel_native(105, 0, i, i2, i3, i4, 0);
        return 0;
    }

    public int set3rdPartyViewPort(int x, int y, int w, int h) {
        if (x > 10000 || y > 10000 || w > 10000 || h > 10000 || x < 0 || y < 0 || w < 0 || h < 0 || x + w > 10000 || y + h > 10000) {
            return -1;
        }
        return TVNativeWrapper.HighLevel_native(110, 0, x, y, w, h, 0);
    }

    public boolean isIn3rdPartyMode() {
        Log.d(TAG, "is_in_3rd_party_mode");
        if (TVNativeWrapper.HighLevel_native(106, 0, 0, 0, 0, 0, 0) == 1) {
            return true;
        }
        return false;
    }

    public int setLinuxResume() {
        Log.d(TAG, "setLinuxResume");
        MtkTvUtilBase.saveTimeStamp("[JJ] call setLinuxResume");
        TVNativeWrapper.HighLevel_native(108, 1, 0, 0, 0, 0, 0);
        return 0;
    }

    public int setLinuxSuspend() {
        Log.d(TAG, "setLinuxSuspend");
        TVNativeWrapper.HighLevel_native(108, 0, 0, 0, 0, 0, 0);
        return 0;
    }

    public int setLinuxStandby() {
        Log.d(TAG, "setLinuxStandby");
        TVNativeWrapper.HighLevel_native(108, 2, 0, 0, 0, 0, 0);
        return 0;
    }

    public int setBroadcastUiVisibility(boolean b_visible) {
        boolean z = b_visible;
        Log.d(TAG, "setBroadcastUiVisibility " + z);
        if (z) {
            TVNativeWrapper.HighLevel_native(109, 1, 0, 0, 0, 0, 0);
            return 0;
        }
        TVNativeWrapper.HighLevel_native(109, 0, 0, 0, 0, 0, 0);
        return 0;
    }
}
