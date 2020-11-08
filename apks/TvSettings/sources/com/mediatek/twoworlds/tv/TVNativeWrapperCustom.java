package com.mediatek.twoworlds.tv;

import android.util.Log;

public class TVNativeWrapperCustom {
    private static final String TAG = "TVNativeWrapperCustom(Emu)";

    protected static boolean is_emulator() {
        if (SystemProperties.get("vendor.mtk.inside").length() == 0 || "0".equals(SystemProperties.get("vendor.mtk.inside"))) {
            return true;
        }
        return false;
    }

    protected static int testCustom_native(int input1, int input2) {
        if (!is_emulator()) {
            return TVNativeCustom.testCustom_native(input1, input2);
        }
        Log.d(TAG, "testCustom_native called");
        return 0;
    }

    protected static int setAndroidWorldInfoToLinux_native(int mode, int value) {
        if (!is_emulator()) {
            return TVNativeCustom.setAndroidWorldInfoToLinux_native(mode, value);
        }
        Log.d(TAG, "setAndroidWorldInfoToLinux_native called");
        return 0;
    }

    protected static int genericSetAPI_native(int func_id, int param1, int param2) {
        if (!is_emulator()) {
            return TVNativeCustom.genericSetAPI_native(func_id, param1, param2);
        }
        Log.d(TAG, "genericSetAPI_native called");
        return 0;
    }
}
