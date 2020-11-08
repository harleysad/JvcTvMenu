package com.mediatek.twoworlds.tv;

public class MtkTvTeletext extends MtkTvTeletextBase {
    private static MtkTvTeletext mtkTvTeletext = null;

    private MtkTvTeletext() {
    }

    public static MtkTvTeletext getInstance() {
        if (mtkTvTeletext != null) {
            return mtkTvTeletext;
        }
        mtkTvTeletext = new MtkTvTeletext();
        return mtkTvTeletext;
    }
}
