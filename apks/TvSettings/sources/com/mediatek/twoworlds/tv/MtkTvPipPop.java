package com.mediatek.twoworlds.tv;

public class MtkTvPipPop extends MtkTvPipPopBase {
    private static MtkTvPipPop mtkTvPipPop = null;

    private MtkTvPipPop() {
    }

    public static MtkTvPipPop getInstance() {
        if (mtkTvPipPop != null) {
            return mtkTvPipPop;
        }
        mtkTvPipPop = new MtkTvPipPop();
        return mtkTvPipPop;
    }
}
