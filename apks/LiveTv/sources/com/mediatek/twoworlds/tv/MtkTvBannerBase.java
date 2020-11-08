package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvBannerBase {
    public static final String TAG = "MtkTvBannerBase";

    public MtkTvBannerBase() {
        Log.d(TAG, "MtkTvBannerBase object created");
    }

    public boolean isDisplayFrmCH() {
        boolean b_display = TVNativeWrapper.isDisplayFrmCH_native(1, 0);
        Log.d(TAG, "isDisplayFrmCH=" + b_display);
        return b_display;
    }

    public boolean isDisplayFrmInfo() {
        boolean b_display = TVNativeWrapper.isDisplayFrmInfo_native(1, 1);
        Log.d(TAG, "isDisplayFrmInfo=" + b_display);
        return b_display;
    }

    public boolean isDisplayFrmDetail() {
        boolean b_display = TVNativeWrapper.isDisplayFrmDetail_native(1, 2);
        Log.d(TAG, "isDisplayFrmDetail=" + b_display);
        return b_display;
    }

    public String getChannelNumber() {
        String s_str = TVNativeWrapper.getChannelNumber_native(1, 3);
        Log.d(TAG, "getChannelNumber=" + s_str);
        return s_str;
    }

    public String getChannelName() {
        String s_str = TVNativeWrapper.getChannelName_native(1, 4);
        Log.d(TAG, "getChannelName=" + s_str);
        return s_str;
    }

    public String getTVSrc() {
        String s_str = TVNativeWrapper.getTVSrc_native(1, 5);
        Log.d(TAG, "getTVSrc=" + s_str);
        return s_str;
    }

    public String getTimer() {
        String s_str = TVNativeWrapper.getTimer_native(1, 6);
        Log.d(TAG, "getTimer=" + s_str);
        return s_str;
    }

    public String getMsg() {
        String s_str = TVNativeWrapper.getMsg_native(1, 7);
        Log.d(TAG, "getMsg=" + s_str);
        return s_str;
    }

    public String getProgTitle() {
        String s_str = TVNativeWrapper.getProgTitle_native(1, 8);
        Log.d(TAG, "getProgTitle=" + s_str);
        return s_str;
    }

    public String getProgCategory() {
        String s_str = TVNativeWrapper.getProgCategory_native(1, 9);
        Log.d(TAG, "getProgCategory=" + s_str);
        return s_str;
    }

    public String getProgCategoryIdx() {
        String s_str = TVNativeWrapper.getProgCategoryIdx_native(1, 37);
        Log.d(TAG, "getProgCategoryIdx=" + s_str);
        return s_str;
    }

    public String getNextProgDetail() {
        String s_str = TVNativeWrapper.getNextProgDetail_native(1, 38);
        Log.d(TAG, "getNextProgDetail=" + s_str);
        return s_str;
    }

    public String getNextRating() {
        String s_str = TVNativeWrapper.getNextRating_native(1, 39);
        Log.d(TAG, "getNextRating=" + s_str);
        return s_str;
    }

    public String getNextProgCategoryIdx() {
        String s_str = TVNativeWrapper.getNextProgCategoryIdx_native(1, 40);
        Log.d(TAG, "getNextProgCategoryIdx=" + s_str);
        return s_str;
    }

    public String getProgTime() {
        String s_str = TVNativeWrapper.getProgTime_native(1, 10);
        Log.d(TAG, "getProgTime=" + s_str);
        return s_str;
    }

    public String getNextProgTitle() {
        String s_str = TVNativeWrapper.getNextProgTitle_native(1, 11);
        Log.d(TAG, "getNextProgTitle=" + s_str);
        return s_str;
    }

    public String getNextProgTime() {
        String s_str = TVNativeWrapper.getNextProgTime_native(1, 12);
        Log.d(TAG, "getNextProgTime=" + s_str);
        return s_str;
    }

    public String getProgDetail() {
        String s_str = TVNativeWrapper.getProgDetail_native(1, 13);
        Log.d(TAG, "getProgDetail=" + s_str);
        return s_str;
    }

    public String getProgDetailPageIdx() {
        String s_str = TVNativeWrapper.getProgDetailPageIdx_native(1, 14);
        Log.d(TAG, "getProgDetailPageIdx=" + s_str);
        return s_str;
    }

    public String getRating() {
        String s_str = TVNativeWrapper.getRating_native(1, 15);
        Log.d(TAG, "getRating=" + s_str);
        return s_str;
    }

    public String getCaption() {
        String s_str = TVNativeWrapper.getCaption_native(1, 16);
        Log.d(TAG, "getCaption=" + s_str);
        return s_str;
    }

    public String getIptsCC() {
        String s_str = TVNativeWrapper.getIptsCC_native(1, 17);
        Log.d(TAG, "getIptsCC=" + s_str);
        return s_str;
    }

    public String getIptsRating() {
        String s_str = TVNativeWrapper.getIptsRating_native(1, 18);
        Log.d(TAG, "getIptsRating=" + s_str);
        return s_str;
    }

    public String getAudioInfo() {
        String s_str = TVNativeWrapper.getAudioInfo_native(1, 19);
        Log.d(TAG, "getAudioInfo=" + s_str);
        return s_str;
    }

    public String getVideoInfo() {
        String s_str = TVNativeWrapper.getVideoInfo_native(1, 20);
        Log.d(TAG, "getVideoInfo=" + s_str);
        return s_str;
    }

    public String getIptsName() {
        String s_str = TVNativeWrapper.getIptsName_native(1, 21);
        Log.d(TAG, "getIptsName=" + s_str);
        return s_str;
    }

    public String getIptsRslt() {
        String s_str = TVNativeWrapper.getIptsRslt_native(1, 22);
        Log.d(TAG, "getIptsRslt=" + s_str);
        return s_str;
    }

    public String getIptChannelNumber() {
        String s_str = TVNativeWrapper.getIptChannelNumber_native(1, 23);
        Log.d(TAG, "getIptChannelNumber=" + s_str);
        return s_str;
    }

    public boolean isDisplayIptsLockIcon() {
        boolean b_display = TVNativeWrapper.isDisplayIptsLockIcon_native(1, 24);
        Log.d(TAG, "isDisplayIptsLockIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayTVLockIcon() {
        boolean b_display = TVNativeWrapper.isDisplayTVLockIcon_native(1, 25);
        Log.d(TAG, "isDisplayTVLockIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayFavIcon() {
        boolean b_display = TVNativeWrapper.isDisplayFavIcon_native(1, 26);
        Log.d(TAG, "isDisplayFavIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayCaptionIcon() {
        boolean b_display = TVNativeWrapper.isDisplayCaptionIcon_native(1, 27);
        Log.d(TAG, "isDisplayCaptionIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayTtxIcon() {
        boolean b_display = TVNativeWrapper.isDisplayTtxIcon_native(1, 28);
        Log.d(TAG, "isDisplayTtxIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayProgDetailUpIcon() {
        boolean b_display = TVNativeWrapper.isDisplayProgDetailUpIcon_native(1, 29);
        Log.d(TAG, "isDisplayProgDetailUpIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayProgDetailDownIcon() {
        boolean b_display = TVNativeWrapper.isDisplayProgDetailDownIcon_native(1, 30);
        Log.d(TAG, "isDisplayProgDetailDownIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayLogoIcon() {
        boolean b_display = TVNativeWrapper.isDisplayLogoIcon_native(1, 31);
        Log.d(TAG, "isDisplayLogoIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayGingaIcon() {
        boolean b_display = TVNativeWrapper.isDisplayGingaIcon_native(1, 32);
        Log.d(TAG, "isDisplayGingaIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayADEyeIcon() {
        boolean b_display = TVNativeWrapper.isDisplayADEyeIcon_native(1, 33);
        Log.d(TAG, "isDisplayADEyeIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayADEarIcon() {
        boolean b_display = TVNativeWrapper.isDisplayADEarIcon_native(1, 34);
        Log.d(TAG, "isDisplayADEarIcon=" + b_display);
        return b_display;
    }

    public boolean isDisplayADAtmos() {
        boolean b_display = TVNativeWrapper.isDisplayADAtmos_native(1, 36);
        Log.d(TAG, "isDisplayADAtmos=" + b_display);
        return b_display;
    }

    public boolean isDisplayBanner() {
        boolean b_display = TVNativeWrapper.isDisplayBanner_native(1, 35);
        Log.d(TAG, "isDisplayBanner=" + b_display);
        return b_display;
    }
}
