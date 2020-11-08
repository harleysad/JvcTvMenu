package com.mediatek.twoworlds.tv;

public class MtkTvATSCRating extends MtkTvATSCRatingBase {
    private static MtkTvATSCRating mtkTvATSCRating = null;

    private MtkTvATSCRating() {
    }

    public static MtkTvATSCRating getInstance() {
        if (mtkTvATSCRating != null) {
            return mtkTvATSCRating;
        }
        mtkTvATSCRating = new MtkTvATSCRating();
        return mtkTvATSCRating;
    }
}
