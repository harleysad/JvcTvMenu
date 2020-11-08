package com.mediatek.twoworlds.tv;

public class MtkTvMHP extends MtkTvMHPBase {
    private static MtkTvMHP mtkTvMHP = null;

    private MtkTvMHP() {
    }

    public static MtkTvMHP getInstance() {
        if (mtkTvMHP != null) {
            return mtkTvMHP;
        }
        mtkTvMHP = new MtkTvMHP();
        return mtkTvMHP;
    }
}
