package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvParserIniInfoBase {
    public static final int DATA_TYPE_INT = 0;
    public static final int DATA_TYPE_STRING = 1;
    public static final int ERROR_CODE_FAIL = -1;
    public static final int ERROR_CODE_NOT_IMPLEMENT = -2;
    public static final int ERROR_CODE_SUCCESS = 0;
    private static final String TAG = "MtkTvParserIniInfoBase";
    private int dataType = 0;
    private int errorCode = 0;
    private int intData = 0;
    private String stringData = "";

    public MtkTvParserIniInfoBase() {
        Log.d(TAG, "Enter MtkTvBookingBase struct Here.");
    }

    public int getDataType() {
        Log.d(TAG, "getErrorCode" + this.dataType);
        return this.dataType;
    }

    public int getErrorCode() {
        Log.d(TAG, "getErrorCode" + this.errorCode);
        return this.errorCode;
    }

    public int getIntData() {
        Log.d(TAG, "getSignedData" + this.intData);
        return this.intData;
    }

    public String getStringData() {
        Log.d(TAG, "getStringData" + this.stringData);
        return this.stringData;
    }

    public void setDataType(int dataType2) {
        Log.d(TAG, "getErrorCode" + dataType2);
        this.dataType = dataType2;
    }

    public void setErrorCode(int errorCode2) {
        Log.d(TAG, "getErrorCode" + errorCode2);
        this.errorCode = errorCode2;
    }

    public void setIntData(int intData2) {
        Log.d(TAG, "setSignedData" + intData2);
        this.intData = intData2;
    }

    public void setStringData(String stringData2) {
        Log.d(TAG, "setStringData" + stringData2);
        this.stringData = stringData2;
    }
}
