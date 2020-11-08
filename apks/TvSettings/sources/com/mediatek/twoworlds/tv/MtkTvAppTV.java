package com.mediatek.twoworlds.tv;

public class MtkTvAppTV extends MtkTvAppTVBase {
    private static MtkTvAppTV mtkTvAppTV = null;

    private MtkTvAppTV() {
    }

    public static MtkTvAppTV getInstance() {
        if (mtkTvAppTV != null) {
            return mtkTvAppTV;
        }
        mtkTvAppTV = new MtkTvAppTV();
        return mtkTvAppTV;
    }
}
