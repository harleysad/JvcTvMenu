package com.mediatek.twoworlds.tv;

public class MtkTvISDBCloseCaption extends MtkTvISDBCloseCaptionBase {
    private static MtkTvISDBCloseCaption mtkTvISDBCloseCaption = null;

    private MtkTvISDBCloseCaption() {
    }

    public static MtkTvISDBCloseCaption getInstance() {
        if (mtkTvISDBCloseCaption != null) {
            return mtkTvISDBCloseCaption;
        }
        mtkTvISDBCloseCaption = new MtkTvISDBCloseCaption();
        return mtkTvISDBCloseCaption;
    }
}
