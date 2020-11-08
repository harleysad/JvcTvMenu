package com.mediatek.twoworlds.tv;

public class MtkTvGinga extends MtkTvGingaBase {
    private static MtkTvGinga mtkTvGinga = null;

    private MtkTvGinga() {
    }

    public static MtkTvGinga getInstance() {
        if (mtkTvGinga != null) {
            return mtkTvGinga;
        }
        mtkTvGinga = new MtkTvGinga();
        return mtkTvGinga;
    }
}
