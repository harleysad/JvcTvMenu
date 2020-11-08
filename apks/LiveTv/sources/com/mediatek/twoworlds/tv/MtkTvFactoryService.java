package com.mediatek.twoworlds.tv;

public class MtkTvFactoryService extends MtkTvFactoryServiceBase {
    private static MtkTvFactoryService mtkTvFactoryService = null;

    private MtkTvFactoryService() {
    }

    public static MtkTvFactoryService getInstance() {
        if (mtkTvFactoryService != null) {
            return mtkTvFactoryService;
        }
        mtkTvFactoryService = new MtkTvFactoryService();
        return mtkTvFactoryService;
    }
}
