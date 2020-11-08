package com.mediatek.twoworlds.tv;

public class MtkTvEAS {
    private static MtkTvEAS mtkTvEAS = null;

    private MtkTvEAS() {
    }

    public static MtkTvEAS getInstance() {
        if (mtkTvEAS != null) {
            return mtkTvEAS;
        }
        mtkTvEAS = new MtkTvEAS();
        return mtkTvEAS;
    }
}
