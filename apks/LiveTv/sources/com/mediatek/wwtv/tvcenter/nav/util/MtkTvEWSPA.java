package com.mediatek.wwtv.tvcenter.nav.util;

import com.mediatek.twoworlds.tv.MtkTvEWSPABase;

public class MtkTvEWSPA extends MtkTvEWSPABase {
    private static MtkTvEWSPA instance;

    private MtkTvEWSPA() {
    }

    public static synchronized MtkTvEWSPA getInstance() {
        MtkTvEWSPA mtkTvEWSPA;
        synchronized (MtkTvEWSPA.class) {
            if (instance == null) {
                instance = new MtkTvEWSPA();
            }
            mtkTvEWSPA = instance;
        }
        return mtkTvEWSPA;
    }
}
