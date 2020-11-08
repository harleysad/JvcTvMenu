package com.mediatek.twoworlds.tv;

public class MtkTvAVMode extends MtkTvAVModeBase {
    private static MtkTvAVMode mtkTvAVMode = null;

    private MtkTvAVMode() {
    }

    public static MtkTvAVMode getInstance() {
        if (mtkTvAVMode != null) {
            return mtkTvAVMode;
        }
        mtkTvAVMode = new MtkTvAVMode();
        return mtkTvAVMode;
    }
}
