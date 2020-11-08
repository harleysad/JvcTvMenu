package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvAnalogCloseCaptionBase {
    public static final String TAG = "MtkTvAnalogCloseCaption";

    public MtkTvAnalogCloseCaptionBase() {
        Log.d(TAG, "MtkTvAnalogCloseCaptionBase object created");
    }

    public int analogCCEnable(boolean b_flag) {
        StringBuilder sb = new StringBuilder();
        sb.append("CC is ");
        sb.append(b_flag ? "enable" : "disable");
        Log.d(TAG, sb.toString());
        return TVNativeWrapper.analogCCEnable_native(b_flag);
    }

    public int analogCCNextStream() {
        int i_ccNo = TVNativeWrapper.analogCCNextStream_native();
        Log.d(TAG, "Next cc stream is CC" + i_ccNo);
        return i_ccNo;
    }

    public int analogCCGetCcIndex() {
        int i_ccNo = TVNativeWrapper.analogCCGetCcIndex_native();
        Log.d(TAG, "getChannelNumber=" + i_ccNo);
        return i_ccNo;
    }

    public int analogCCSetCcVisible(boolean b_visible) {
        Log.d(TAG, "analogCCSetCcVisible=" + b_visible);
        return TVNativeWrapper.analogCCSetCcVisible_native(b_visible);
    }
}
