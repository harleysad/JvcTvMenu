package com.mediatek.twoworlds.tv;

public class MtkTvATSCCloseCaption extends MtkTvATSCCloseCaptionBase {
    private static MtkTvATSCCloseCaption mtkTvATSCCloseCaption = null;

    private MtkTvATSCCloseCaption() {
    }

    public static MtkTvATSCCloseCaption getInstance() {
        if (mtkTvATSCCloseCaption != null) {
            return mtkTvATSCCloseCaption;
        }
        mtkTvATSCCloseCaption = new MtkTvATSCCloseCaption();
        return mtkTvATSCCloseCaption;
    }
}
