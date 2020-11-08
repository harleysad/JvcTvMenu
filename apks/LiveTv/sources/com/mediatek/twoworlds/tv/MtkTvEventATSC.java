package com.mediatek.twoworlds.tv;

public class MtkTvEventATSC extends MtkTvEventATSCBase {
    private static MtkTvEventATSC mtkTvEventATSC = null;

    private MtkTvEventATSC() {
    }

    public static MtkTvEventATSC getInstance() {
        if (mtkTvEventATSC != null) {
            return mtkTvEventATSC;
        }
        mtkTvEventATSC = new MtkTvEventATSC();
        return mtkTvEventATSC;
    }
}
