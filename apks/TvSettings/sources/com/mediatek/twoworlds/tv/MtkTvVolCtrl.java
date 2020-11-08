package com.mediatek.twoworlds.tv;

public class MtkTvVolCtrl extends MtkTvVolCtrlBase {
    private static MtkTvVolCtrl mtkTvVolCtrl = null;

    private MtkTvVolCtrl() {
    }

    public static MtkTvVolCtrl getInstance() {
        if (mtkTvVolCtrl != null) {
            return mtkTvVolCtrl;
        }
        mtkTvVolCtrl = new MtkTvVolCtrl();
        return mtkTvVolCtrl;
    }
}
