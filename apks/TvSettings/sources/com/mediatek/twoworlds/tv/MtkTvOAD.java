package com.mediatek.twoworlds.tv;

public class MtkTvOAD extends MtkTvOADBase {
    private static MtkTvOAD mtkTvOAD = null;

    private MtkTvOAD() {
    }

    public static MtkTvOAD getInstance() {
        if (mtkTvOAD != null) {
            return mtkTvOAD;
        }
        mtkTvOAD = new MtkTvOAD();
        return mtkTvOAD;
    }
}
