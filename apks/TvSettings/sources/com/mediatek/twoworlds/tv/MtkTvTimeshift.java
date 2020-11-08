package com.mediatek.twoworlds.tv;

public class MtkTvTimeshift extends MtkTvTimeshiftBase {
    private static MtkTvTimeshift mInstance = null;

    private MtkTvTimeshift() {
    }

    public static MtkTvTimeshift getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        mInstance = new MtkTvTimeshift();
        return mInstance;
    }
}
