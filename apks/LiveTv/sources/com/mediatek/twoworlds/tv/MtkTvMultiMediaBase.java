package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvMultiMediaBase {
    private final String TAG = "MtkTvMultiMediaBase";

    public String GetDrmRegistrationCode() {
        Log.d("MtkTvMultiMediaBase", "Enter GetDrmRegistrationCode_native + \n");
        return TVNativeWrapper.GetDrmRegistrationCode_native();
    }

    public String SetDrmDeactivation() {
        Log.d("MtkTvMultiMediaBase", "Enter SetDrmDeactivation_native + \n");
        return TVNativeWrapper.SetDrmDeactivation_native();
    }

    public long GetDrmUiHelpInfo() {
        Log.d("MtkTvMultiMediaBase", "Enter GetDrmUiHelpInfo_native + \n");
        return TVNativeWrapper.GetDrmUiHelpInfo_native();
    }

    public boolean GetDivXPlusSupport() {
        Log.d("MtkTvMultiMediaBase", "Enter GetDivXPlusSupport_native + \n");
        return TVNativeWrapper.GetDivXPlusSupport_native();
    }

    public boolean GetDivXHDSupport() {
        Log.d("MtkTvMultiMediaBase", "Enter GetDivXHDSupport_native + \n");
        return TVNativeWrapper.GetDivXHDSupport_native();
    }
}
