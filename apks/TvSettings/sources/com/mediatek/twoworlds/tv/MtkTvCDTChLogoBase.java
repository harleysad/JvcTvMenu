package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvCDTChLogoBase {
    public static final String TAG = "MtkTvCDTChLogoBase";

    public String getChLogoPNGFilePath(int curOriginalNetworkID, int curServiceID, int curFrequency) {
        Log.d(TAG, "#### CH LOGO onid=" + curOriginalNetworkID + ",svlid=" + curServiceID + ",freq=" + curFrequency);
        String CDTLogoFilePath = TVNativeWrapper.isdbCDTGetChannelLogo_native(curOriginalNetworkID, curServiceID, curFrequency);
        StringBuilder sb = new StringBuilder();
        sb.append("#### CH LOGO getChLogoPNGFilePath=");
        sb.append(CDTLogoFilePath);
        Log.d(TAG, sb.toString());
        return CDTLogoFilePath;
    }
}
