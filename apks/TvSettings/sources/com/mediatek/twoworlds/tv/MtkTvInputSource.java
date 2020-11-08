package com.mediatek.twoworlds.tv;

public class MtkTvInputSource extends MtkTvInputSourceBase {
    private static MtkTvInputSource mtkTvInputSource = null;

    private MtkTvInputSource() {
    }

    public static MtkTvInputSource getInstance() {
        if (mtkTvInputSource != null) {
            return mtkTvInputSource;
        }
        mtkTvInputSource = new MtkTvInputSource();
        return mtkTvInputSource;
    }
}
