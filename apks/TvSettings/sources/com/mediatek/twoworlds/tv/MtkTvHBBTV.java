package com.mediatek.twoworlds.tv;

public class MtkTvHBBTV extends MtkTvHBBTVBase {
    private static MtkTvHBBTV mtkTvHBBTV = null;

    private MtkTvHBBTV() {
    }

    public static MtkTvHBBTV getInstance() {
        if (mtkTvHBBTV != null) {
            return mtkTvHBBTV;
        }
        mtkTvHBBTV = new MtkTvHBBTV();
        return mtkTvHBBTV;
    }
}
