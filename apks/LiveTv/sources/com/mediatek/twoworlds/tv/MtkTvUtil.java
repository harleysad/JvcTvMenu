package com.mediatek.twoworlds.tv;

public class MtkTvUtil extends MtkTvUtilBase {
    private static MtkTvUtil mtkTvUtil = null;

    private MtkTvUtil() {
    }

    public static MtkTvUtil getInstance() {
        if (mtkTvUtil != null) {
            return mtkTvUtil;
        }
        mtkTvUtil = new MtkTvUtil();
        return mtkTvUtil;
    }

    public int muteVideo(boolean bMute) {
        return TVNativeWrapperCustom.genericSetAPI_native(12000, bMute, 0);
    }

    public static int turnOnPanelAndSpeaker() {
        return TVNativeWrapper.HighLevel_native(13001, 0, 0, 0, 0, 0, 0);
    }

    public static int QHB_IsQuietHotBoot() {
        return TVNativeWrapper.HighLevel_native(13010, 0, 0, 0, 0, 0, 0);
    }

    public static int QHB_IsCancelledQuietHotBoot() {
        return TVNativeWrapper.HighLevel_native(13011, 0, 0, 0, 0, 0, 0);
    }

    public static int IRRemoteControl(int value) {
        return TVNativeWrapper.HighLevel_native(117, value, 0, 0, 0, 0, 0);
    }

    public static int KeypadControl(int value) {
        return TVNativeWrapper.HighLevel_native(118, value, 0, 0, 0, 0, 0);
    }

    public static int QHB_ClearQuietHotBoot() {
        return TVNativeWrapper.HighLevel_native(13012, 0, 0, 0, 0, 0, 0);
    }

    public static int QHB_StopCancellationCheckBeforeBootComplete() {
        return TVNativeWrapper.HighLevel_native(13013, 0, 0, 0, 0, 0, 0);
    }
}
