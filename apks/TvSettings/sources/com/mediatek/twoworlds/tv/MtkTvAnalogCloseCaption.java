package com.mediatek.twoworlds.tv;

public class MtkTvAnalogCloseCaption extends MtkTvAnalogCloseCaptionBase {
    private static MtkTvAnalogCloseCaption mtkTvAnalogCloseCaption = null;

    private MtkTvAnalogCloseCaption() {
    }

    public static MtkTvAnalogCloseCaption getInstance() {
        if (mtkTvAnalogCloseCaption != null) {
            return mtkTvAnalogCloseCaption;
        }
        mtkTvAnalogCloseCaption = new MtkTvAnalogCloseCaption();
        return mtkTvAnalogCloseCaption;
    }
}
