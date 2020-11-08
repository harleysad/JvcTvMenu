package com.mediatek.twoworlds.tv;

public class MtkTvMultiView extends MtkTvMultiViewBase {
    private static MtkTvMultiView mtkTvMultiView = null;

    private MtkTvMultiView() {
    }

    public static MtkTvMultiView getInstance() {
        if (mtkTvMultiView != null) {
            return mtkTvMultiView;
        }
        mtkTvMultiView = new MtkTvMultiView();
        return mtkTvMultiView;
    }
}
