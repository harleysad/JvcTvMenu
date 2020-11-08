package com.mediatek.twoworlds.tv;

public class MtkTvDVBRating extends MtkTvDVBRatingBase {
    private static MtkTvDVBRating mtkTvDVBRating = null;

    private MtkTvDVBRating() {
    }

    public static MtkTvDVBRating getInstance() {
        if (mtkTvDVBRating != null) {
            return mtkTvDVBRating;
        }
        mtkTvDVBRating = new MtkTvDVBRating();
        return mtkTvDVBRating;
    }
}
