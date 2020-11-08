package com.mediatek.twoworlds.tv;

public class MtkTvKeyEvent extends MtkTvKeyEventBase {
    private static MtkTvKeyEvent mtkTvKeyEvent = null;

    private MtkTvKeyEvent() {
    }

    public static MtkTvKeyEvent getInstance() {
        if (mtkTvKeyEvent != null) {
            return mtkTvKeyEvent;
        }
        mtkTvKeyEvent = new MtkTvKeyEvent();
        return mtkTvKeyEvent;
    }
}
