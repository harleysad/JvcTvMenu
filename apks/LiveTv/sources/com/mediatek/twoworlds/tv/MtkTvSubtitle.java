package com.mediatek.twoworlds.tv;

public class MtkTvSubtitle extends MtkTvSubtitleBase {
    private static MtkTvSubtitle mtkTvSubtitle = null;

    private MtkTvSubtitle() {
    }

    public static MtkTvSubtitle getInstance() {
        if (mtkTvSubtitle != null) {
            return mtkTvSubtitle;
        }
        mtkTvSubtitle = new MtkTvSubtitle();
        return mtkTvSubtitle;
    }
}
