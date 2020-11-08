package com.mediatek.twoworlds.tv;

public class MtkTvEvent extends MtkTvEventBase {
    private static MtkTvEvent mtkTvEvent = null;

    private MtkTvEvent() {
    }

    public static MtkTvEvent getInstance() {
        if (mtkTvEvent != null) {
            return mtkTvEvent;
        }
        mtkTvEvent = new MtkTvEvent();
        return mtkTvEvent;
    }
}
