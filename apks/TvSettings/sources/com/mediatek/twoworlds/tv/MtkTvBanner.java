package com.mediatek.twoworlds.tv;

public class MtkTvBanner extends MtkTvBannerBase {
    private static MtkTvBanner mtkTvBanner = null;

    private MtkTvBanner() {
    }

    public static MtkTvBanner getInstance() {
        if (mtkTvBanner != null) {
            return mtkTvBanner;
        }
        mtkTvBanner = new MtkTvBanner();
        return mtkTvBanner;
    }
}
