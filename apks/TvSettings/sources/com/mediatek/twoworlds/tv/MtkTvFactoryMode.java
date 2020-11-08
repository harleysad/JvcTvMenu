package com.mediatek.twoworlds.tv;

public class MtkTvFactoryMode extends MtkTvFactoryModeBase {
    private static MtkTvFactoryMode mtkTvFactoryMode = null;

    private MtkTvFactoryMode() {
    }

    public static MtkTvFactoryMode getInstance() {
        if (mtkTvFactoryMode != null) {
            return mtkTvFactoryMode;
        }
        mtkTvFactoryMode = new MtkTvFactoryMode();
        return mtkTvFactoryMode;
    }
}
