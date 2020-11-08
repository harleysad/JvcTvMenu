package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvParserIniInfoBase;

public class MtkTvParserIniBase {
    private final String TAG = "MtkTvParserIniBase";

    public String getFileBasePath() {
        Log.d("MtkTvParserIniBase", "Enter getFileBasePath Here..");
        String filePath = TVNativeWrapper.getFileBasePath_native();
        Log.d("MtkTvParserIniBase", "&&&&&, MtkTvParserIniBase.getFileBasePath. Name =" + filePath + ".\n");
        return filePath;
    }

    public MtkTvParserIniInfoBase getIntConfigData(String filePath, String keyName) {
        Log.d("MtkTvParserIniBase", "Enter getUnsignedConfigData Here..");
        return TVNativeWrapper.getIntConfigData_native(filePath, keyName);
    }

    public MtkTvParserIniInfoBase getStringConfigData(String filePath, String keyName) {
        Log.d("MtkTvParserIniBase", "Enter getStringConfigData Here..");
        return TVNativeWrapper.getStringConfigData_native(filePath, keyName);
    }
}
