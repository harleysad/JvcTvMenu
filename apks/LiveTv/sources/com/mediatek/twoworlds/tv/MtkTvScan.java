package com.mediatek.twoworlds.tv;

public class MtkTvScan extends MtkTvScanBase {
    private static MtkTvScan mtkTvScan = null;

    private MtkTvScan() {
    }

    public static MtkTvScan getInstance() {
        if (mtkTvScan != null) {
            return mtkTvScan;
        }
        mtkTvScan = new MtkTvScan();
        return mtkTvScan;
    }
}
