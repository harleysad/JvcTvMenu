package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvISDBRatingBase {
    public static final String TAG = "MtkTvISDBRatingBase";

    public int getISDBContentRatingSetting() {
        int isdbCntRatingsetting = TVNativeWrapper.getISDBContentRatingSetting_native();
        Log.d(TAG, "isdbCntRatingsetting=" + isdbCntRatingsetting);
        return isdbCntRatingsetting;
    }

    public void setISDBContentRatingSetting(int contentRatingSetting) {
        int ret = TVNativeWrapper.setISDBContentRatingSetting_native(contentRatingSetting);
        Log.d(TAG, "setISDBContentRating ret=" + ret);
    }

    public int getISDBAgeRatingSetting() {
        int isdbAgeRatingsetting = TVNativeWrapper.getISDBAgeRatingSetting_native();
        Log.d(TAG, "isdbCntRatingsetting=" + isdbAgeRatingsetting);
        return isdbAgeRatingsetting;
    }

    public void setISDBAgeRatingSetting(int ageRatingSetting) {
        int ret = TVNativeWrapper.setISDBAgeRatingSetting_native(ageRatingSetting);
        Log.d(TAG, "ageRatingSetting ret=" + ret);
    }
}
