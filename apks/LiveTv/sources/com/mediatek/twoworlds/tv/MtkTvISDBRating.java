package com.mediatek.twoworlds.tv;

public class MtkTvISDBRating extends MtkTvISDBRatingBase {
    private static MtkTvISDBRating mtkTvISDBRating = null;

    private MtkTvISDBRating() {
    }

    public static MtkTvISDBRating getInstance() {
        if (mtkTvISDBRating != null) {
            return mtkTvISDBRating;
        }
        mtkTvISDBRating = new MtkTvISDBRating();
        return mtkTvISDBRating;
    }
}
