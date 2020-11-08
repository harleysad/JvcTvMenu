package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvATSCCloseCaptionBase {
    public static final String TAG = "MtkTvATSCCloseCaption";

    public enum atscCCDemeType {
        DEMO_TYPE_IS_SHOWING,
        DEMO_TYPE_FONT_SIZE,
        DEMO_TYPE_FONT_STYLE,
        DEMO_TYPE_FONT_COLOR,
        DEMO_TYPE_FONT_OPACITY,
        DEMO_TYPE_BG_COLOR,
        DEMO_TYPE_BG_OPACITY,
        DEMO_TYPE_WC_COLOR,
        DEMO_TYPE_WC_OPACITY,
        DEMO_TYPE_TOTAL_NUM
    }

    public MtkTvATSCCloseCaptionBase() {
        Log.d(TAG, "MtkTvAnalogCloseCaptionBase object created");
    }

    public int atscCCEnable(boolean b_flag) {
        StringBuilder sb = new StringBuilder();
        sb.append("CC is ");
        sb.append(b_flag ? "enable" : "disable");
        Log.d(TAG, sb.toString());
        return TVNativeWrapper.atscCCEnable_native(b_flag);
    }

    public int atscCCNextStream() {
        int i_ccNo = TVNativeWrapper.atscCCNextStream_native();
        Log.d(TAG, "Next cc stream is CS" + i_ccNo);
        return i_ccNo;
    }

    public int atscCCGetCCIndex() {
        int i_ccIndex = TVNativeWrapper.atscCCGetCCIndex_native();
        Log.d(TAG, "CC index is " + i_ccIndex);
        return i_ccIndex;
    }

    public int atscCCSetCcVisible(boolean b_visible) {
        Log.d(TAG, "atscCCSetCcVisible=" + b_visible);
        return TVNativeWrapper.atscCCSetCcVisible_native(b_visible);
    }

    public int atscCCDemoSet(int setType, int setValue) {
        Log.d(TAG, "atscCCDemoSet setType=" + setType + ", setValue=" + setValue);
        return TVNativeWrapper.atscCCDemoSet_native(setType, setValue);
    }
}
