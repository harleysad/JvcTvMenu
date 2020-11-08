package com.mediatek.twoworlds.tv;

public class MtkTvNetwork extends MtkTvNetworkBase {
    private static MtkTvNetwork mInstance = null;
    private final String TAG = "MtkTvNetwork";

    private MtkTvNetwork() {
    }

    public static MtkTvNetwork getInstance() {
        if (mInstance == null) {
            mInstance = new MtkTvNetwork();
        }
        return mInstance;
    }
}
