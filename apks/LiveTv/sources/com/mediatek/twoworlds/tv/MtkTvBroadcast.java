package com.mediatek.twoworlds.tv;

public class MtkTvBroadcast extends MtkTvBroadcastBase {
    private static MtkTvBroadcast mtkTvBroadcast = null;

    private MtkTvBroadcast() {
    }

    public static MtkTvBroadcast getInstance() {
        if (mtkTvBroadcast != null) {
            return mtkTvBroadcast;
        }
        mtkTvBroadcast = new MtkTvBroadcast();
        return mtkTvBroadcast;
    }
}
