package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvDVBRatingBase {
    public static final String TAG = "MtkTvDVBRatingBase";

    public int getDVBAgeRatingSetting() {
        int dvbAgeRatingsetting = TVNativeWrapper.getDVBAgeRatingSetting_native();
        Log.d(TAG, "dvbAgeRatingsetting=" + dvbAgeRatingsetting);
        return dvbAgeRatingsetting;
    }

    public void setDVBAgeRatingSetting(int ageRatingSetting) {
        int ret = TVNativeWrapper.setDVBAgeRatingSetting_native(ageRatingSetting);
        Log.d(TAG, "ageRatingSetting ret=" + ret);
    }
}
