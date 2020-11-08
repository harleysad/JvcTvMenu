package com.mediatek.twoworlds.tv;

public class MtkTvChannelList extends MtkTvChannelListBase {
    private static MtkTvChannelList mtkTvChannelList = null;

    private MtkTvChannelList() {
    }

    public static MtkTvChannelList getInstance() {
        if (mtkTvChannelList != null) {
            return mtkTvChannelList;
        }
        mtkTvChannelList = new MtkTvChannelList();
        return mtkTvChannelList;
    }
}
