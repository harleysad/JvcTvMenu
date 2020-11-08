package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;

public class MtkTvATSCRatingBase {
    public static final int MTKTV_ATSC_CANADA_ENG_14 = 5;
    public static final int MTKTV_ATSC_CANADA_ENG_18 = 6;
    public static final int MTKTV_ATSC_CANADA_ENG_C = 1;
    public static final int MTKTV_ATSC_CANADA_ENG_C8 = 2;
    public static final int MTKTV_ATSC_CANADA_ENG_E = 0;
    public static final int MTKTV_ATSC_CANADA_ENG_G = 3;
    public static final int MTKTV_ATSC_CANADA_ENG_OFF = 7;
    public static final int MTKTV_ATSC_CANADA_ENG_PG = 4;
    public static final int MTKTV_ATSC_CANADA_FRE_13 = 3;
    public static final int MTKTV_ATSC_CANADA_FRE_16 = 4;
    public static final int MTKTV_ATSC_CANADA_FRE_18 = 5;
    public static final int MTKTV_ATSC_CANADA_FRE_8 = 2;
    public static final int MTKTV_ATSC_CANADA_FRE_E = 0;
    public static final int MTKTV_ATSC_CANADA_FRE_G = 1;
    public static final int MTKTV_ATSC_CANADA_FRE_OFF = 6;
    public static final int MTKTV_ATSC_MOVIE_G = 0;
    public static final int MTKTV_ATSC_MOVIE_NC_17 = 4;
    public static final int MTKTV_ATSC_MOVIE_OFF = 6;
    public static final int MTKTV_ATSC_MOVIE_PG = 1;
    public static final int MTKTV_ATSC_MOVIE_PG13 = 2;
    public static final int MTKTV_ATSC_MOVIE_R = 3;
    public static final int MTKTV_ATSC_MOVIE_X = 5;
    public static final String TAG = "MtkTvATSCRatingBase";

    public MtkTvUSTvRatingSettingInfoBase getUSTvRatingSettingInfo() {
        MtkTvUSTvRatingSettingInfoBase ratingInfo = new MtkTvUSTvRatingSettingInfoBase();
        int ret = TVNativeWrapper.getUSTvRatingSettingInfo_native(ratingInfo);
        Log.d(TAG, "getUSTvRating ret=" + ret);
        return ratingInfo;
    }

    public void setUSTvRatingSettingInfo(MtkTvUSTvRatingSettingInfoBase tVRatingInfo) {
        int ret = TVNativeWrapper.setUSTvRatingSettingInfo_native(tVRatingInfo);
        Log.d(TAG, "setUSTvRating ret=" + ret);
    }

    public int getUSMovieRatingSettingInfo() {
        int movieRatingSettingInfo = TVNativeWrapper.getUSMovieRatingSettingInfo_native();
        Log.d(TAG, "movieRatingSettingInfo=" + movieRatingSettingInfo);
        return movieRatingSettingInfo;
    }

    public void setUSMovieRatingSettingInfo(int movieRatingSettingInfo) {
        int ret = TVNativeWrapper.setUSMovieRatingSettingInfo_native(movieRatingSettingInfo);
        Log.d(TAG, "setUSMovieRating ret=" + ret);
    }

    public int getCANEngRatingSettingInfo() {
        int engRatingSettingInfo = TVNativeWrapper.getCANEngRatingSettingInfo_native();
        Log.d(TAG, "engRatingSettingInfo=" + engRatingSettingInfo);
        return engRatingSettingInfo;
    }

    public void setCANEngRatingSettingInfo(int engRatingSettingInfo) {
        int ret = TVNativeWrapper.setCANEngRatingSettingInfo_native(engRatingSettingInfo);
        Log.d(TAG, "setCANEngRating ret=" + ret);
    }

    public int getCANFreRatingSettingInfo() {
        int freRatingSettingInfo = TVNativeWrapper.getCANFreRatingSettingInfo_native();
        Log.d(TAG, "freRatingSettingInfo=" + freRatingSettingInfo);
        return freRatingSettingInfo;
    }

    public void setCANFreRatingSettingInfo(int freRatingSettingInfo) {
        int ret = TVNativeWrapper.setCANFreRatingSettingInfo_native(freRatingSettingInfo);
        Log.d(TAG, "setCANFreRating ret=" + ret);
    }

    public boolean getBlockUnrated() {
        boolean isBlockUnrated = TVNativeWrapper.getBlockUnrated_native();
        Log.d(TAG, "isBlockUnrated=" + isBlockUnrated);
        return isBlockUnrated;
    }

    public void setBlockUnrated(boolean isBlockUnrated) {
        int ret = TVNativeWrapper.setBlockUnrated_native(isBlockUnrated);
        Log.d(TAG, "setBlockUnrated ret=" + ret);
    }

    public boolean getRatingEnable() {
        boolean isRatingEnable = TVNativeWrapper.getRatingEnable_native();
        Log.d(TAG, "isRatingEnable=" + isRatingEnable);
        return isRatingEnable;
    }

    public void setRatingEnable(boolean isRatingEnable) {
        int ret = TVNativeWrapper.setRatingEnable_native(isRatingEnable);
        Log.d(TAG, "setRatingEnable ret=" + ret);
    }

    public boolean isOpenVCHIPInfoAvailable() {
        boolean isOpenVCHIPAvail = TVNativeWrapper.isOpenVCHIPInfoAvailable_native();
        Log.d(TAG, "isOpenVCHIPAvail=" + isOpenVCHIPAvail);
        return isOpenVCHIPAvail;
    }

    public MtkTvOpenVCHIPInfoBase getOpenVCHIPInfo(MtkTvOpenVCHIPParaBase opVCHIPPara) {
        MtkTvOpenVCHIPInfoBase openVCHIPInfo = new MtkTvOpenVCHIPInfoBase();
        int ret = TVNativeWrapper.getOpenVCHIPInfo_native(opVCHIPPara, openVCHIPInfo);
        Log.d(TAG, "getOpenVCHIPInfo ret=" + ret);
        return openVCHIPInfo;
    }

    public void setOpenVCHIPSettingInfo(MtkTvOpenVCHIPSettingInfoBase opVCHIPSettinginfo) {
        int ret = TVNativeWrapper.setOpenVCHIPSettingInfo_native(opVCHIPSettinginfo);
        Log.d(TAG, "setOpenVCHIPSettingInfo ret=" + ret);
    }

    public MtkTvOpenVCHIPSettingInfoBase getOpenVCHIPSettingInfo() {
        MtkTvOpenVCHIPSettingInfoBase opBlockInfo = new MtkTvOpenVCHIPSettingInfoBase();
        int ret = TVNativeWrapper.getOpenVCHIPSettingInfo_native(opBlockInfo);
        Log.d(TAG, "getOpenVCHIPSettingInfo ret=" + ret);
        return opBlockInfo;
    }

    public MtkTvOpenVCHIPSettingInfoBase getOpenVCHIPSettingInfo(int regionIndex, int dimIndex) {
        MtkTvOpenVCHIPSettingInfoBase opBlockInfo = new MtkTvOpenVCHIPSettingInfoBase();
        opBlockInfo.setRegionIndex(regionIndex);
        opBlockInfo.setDimIndex(dimIndex);
        opBlockInfo.setUnratedBlock(0);
        int ret = TVNativeWrapper.getOpenVCHIPSettingInfo_native(opBlockInfo);
        Log.d(TAG, "getOpenVCHIPSettingInfo ret=" + ret);
        return opBlockInfo;
    }

    public int setAtscStorage(boolean b_stroe) {
        int ret = TVNativeWrapper.setAtscStorage_native(b_stroe);
        Log.d(TAG, "setAtscStorage ret=" + ret);
        return ret;
    }
}
