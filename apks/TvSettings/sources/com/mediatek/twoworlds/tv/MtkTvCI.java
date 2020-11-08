package com.mediatek.twoworlds.tv;

public class MtkTvCI extends MtkTvCIBase {
    private static MtkTvCI mtkTvCI = null;

    private MtkTvCI(int slotid) {
        super(slotid);
    }

    public static MtkTvCI getInstance(int slotid) {
        if (mtkTvCI != null) {
            return mtkTvCI;
        }
        mtkTvCI = new MtkTvCI(slotid);
        return mtkTvCI;
    }
}
