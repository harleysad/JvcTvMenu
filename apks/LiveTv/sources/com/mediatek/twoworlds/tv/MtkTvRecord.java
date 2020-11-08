package com.mediatek.twoworlds.tv;

public class MtkTvRecord extends MtkTvRecordBase {
    private static MtkTvRecord mInstance = null;

    private MtkTvRecord() {
    }

    public static MtkTvRecord getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        mInstance = new MtkTvRecord();
        return mInstance;
    }
}
