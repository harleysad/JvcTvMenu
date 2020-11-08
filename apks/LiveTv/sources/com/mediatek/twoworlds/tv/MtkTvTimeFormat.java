package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvTimeFormat extends MtkTvTimeFormatBase {
    private static MtkTvTimeFormat mMtkTvTimeFormat = null;

    private MtkTvTimeFormat() {
    }

    public static synchronized MtkTvTimeFormat getInstance() {
        MtkTvTimeFormat mtkTvTimeFormat;
        synchronized (MtkTvTimeFormat.class) {
            if (mMtkTvTimeFormat == null) {
                mMtkTvTimeFormat = new MtkTvTimeFormat();
                Log.d(MtkTvTimeFormatBase.TAG, "New class MtkTvTime\n");
            }
            mtkTvTimeFormat = mMtkTvTimeFormat;
        }
        return mtkTvTimeFormat;
    }
}
