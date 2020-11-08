package com.mediatek.twoworlds.tv;

public class MtkTvMHEG5 extends MtkTvMHEG5Base {
    private static MtkTvMHEG5 mtkTvMHEG5 = null;

    private MtkTvMHEG5() {
    }

    public static MtkTvMHEG5 getInstance() {
        if (mtkTvMHEG5 != null) {
            return mtkTvMHEG5;
        }
        mtkTvMHEG5 = new MtkTvMHEG5();
        return mtkTvMHEG5;
    }
}
