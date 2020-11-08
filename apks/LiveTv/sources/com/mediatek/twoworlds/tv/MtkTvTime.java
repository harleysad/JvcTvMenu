package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvTime extends MtkTvTimeBase {
    private static MtkTvTime mMtkTvTime = null;

    private MtkTvTime() {
    }

    public static synchronized MtkTvTime getInstance() {
        MtkTvTime mtkTvTime;
        synchronized (MtkTvTime.class) {
            if (mMtkTvTime == null) {
                mMtkTvTime = new MtkTvTime();
                Log.d(MtkTvTimeBase.TAG, "New class MtkTvTime\n");
            }
            mtkTvTime = mMtkTvTime;
        }
        return mtkTvTime;
    }
}
