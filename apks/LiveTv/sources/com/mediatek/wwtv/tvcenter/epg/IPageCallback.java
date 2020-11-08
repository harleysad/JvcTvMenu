package com.mediatek.wwtv.tvcenter.epg;

public interface IPageCallback {
    boolean hasNextPage();

    boolean hasPrePage();

    void onRefreshPage();
}
