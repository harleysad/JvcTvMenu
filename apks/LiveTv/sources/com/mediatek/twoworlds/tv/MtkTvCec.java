package com.mediatek.twoworlds.tv;

public class MtkTvCec extends MtkTvCecBase {
    private static MtkTvCec mtkTvCEC = null;

    private MtkTvCec() {
    }

    public static MtkTvCec getInstance() {
        if (mtkTvCEC != null) {
            return mtkTvCEC;
        }
        mtkTvCEC = new MtkTvCec();
        return mtkTvCEC;
    }
}
