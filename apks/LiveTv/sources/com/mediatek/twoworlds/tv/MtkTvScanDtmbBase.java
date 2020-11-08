package com.mediatek.twoworlds.tv;

import android.support.v4.os.EnvironmentCompat;
import android.util.Log;

public class MtkTvScanDtmbBase {
    public static final int DTMB_RET_INTERNAL_ERR = -1;
    public static final int DTMB_RET_OK = 0;
    private static final int EX_HEADER_LEN = 2;
    private static final int EX_HEADER_LEN_IDX = 0;
    private static final int EX_HEADER_TYPE_IDX = 1;
    private static final int EX_MAX_LEN = 50;
    private static final int EX_PAYLOAD_1ST_IDX = 2;
    private static final int EX_TYPE_AUTO_SCAN = 3;
    private static final int EX_TYPE_CANCEL_SCAN = 1;
    private static final int EX_TYPE_GET_RF_INFO = 4;
    private static final int EX_TYPE_RF_SCAN = 2;
    private static final int EX_TYPE_UNKNOWN = 0;
    public static final int RF_CURR = 0;
    public static final int RF_NEXT = 2;
    public static final int RF_PREV = 1;
    private static final String TAG = "";

    private static void Log(int value) {
        StackTraceElement[] el = new Exception().getStackTrace();
        Log.e(TAG, el[1].getClassName() + "." + el[1].getMethodName() + " [" + new Integer(el[1].getLineNumber()).toString() + "] :---> " + value);
    }

    private static void Log(String str) {
        StackTraceElement[] el = new Exception().getStackTrace();
        Log.e(TAG, el[1].getClassName() + "." + el[1].getMethodName() + " [" + new Integer(el[1].getLineNumber()).toString() + "] :---> " + str);
    }

    public int cancelScan() {
        Log("enter");
        int ret = _toolExchange(1);
        Log("ret: " + ret);
        return ret;
    }

    public int startAutoScan() {
        Log("enter");
        int ret = _toolExchange(3);
        Log("ret: " + ret);
        return ret;
    }

    public int startRfScan() {
        Log("enter");
        int ret = _toolExchange(2);
        Log("ret: " + ret);
        return ret;
    }

    public class RfInfo {
        private static final int MAX_RF_NAME_LEN = 20;
        public String rfChannelName;
        public int rfFreq;
        public int rfIndex;

        public RfInfo() {
        }
    }

    public RfInfo gotoDestinationRf(int rfDirection) {
        int i = rfDirection;
        if (i < 0 || i > 2) {
            Log("return: unknown rf direction " + i);
            return null;
        }
        Log("rfDirection: " + i + ", " + _toolMapIntToString(0, i));
        int[] dataExceptName = {0, i, 0, 0};
        int[] intChannelName = new int[20];
        int[] data = new int[(dataExceptName.length + intChannelName.length)];
        data[0] = 0;
        data[1] = i;
        int exRet = _toolExchange(4, data);
        int mwScanRet = data[0];
        if (exRet == 0 && mwScanRet == 0) {
            for (int i2 = 0; i2 < intChannelName.length; i2++) {
                intChannelName[i2] = data[dataExceptName.length + i2];
            }
            RfInfo rfInfo = new RfInfo();
            rfInfo.rfIndex = data[2];
            rfInfo.rfFreq = data[3];
            rfInfo.rfChannelName = _toolConvertAsciiArrayToString(intChannelName);
            Log("RF [" + rfInfo.rfChannelName + "]: idx " + rfInfo.rfIndex + ", freq " + rfInfo.rfFreq);
            return rfInfo;
        }
        Log("return null: exRet " + exRet + ", mwScanRet " + mwScanRet);
        return null;
    }

    private String _toolConvertAsciiArrayToString(int[] asciiArray) {
        if (asciiArray == null) {
            Log("return null: null == asciiArray");
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : asciiArray) {
            stringBuilder.append((char) i);
        }
        return stringBuilder.toString();
    }

    private String _toolConvertIntArrayToString(int[] intArray) {
        if (intArray == null) {
            Log("return null: null == intArray");
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < intArray.length; i++) {
            stringBuilder.append("  [" + i + "]");
            stringBuilder.append(intArray[i]);
        }
        return stringBuilder.toString();
    }

    private int _toolExchange(int exType) {
        return _toolExchange(exType, new int[]{0});
    }

    private int _toolExchange(int exchangeType, int[] data) {
        int[] payload = data;
        if (payload == null) {
            payload = new int[]{0};
        }
        int totalLen = payload.length + 2;
        if (totalLen > 50) {
            Log("return -1: ex_pkg len " + totalLen + " > limit len " + 50);
            return -1;
        }
        int[] exchangeData = new int[totalLen];
        exchangeData[0] = 2;
        exchangeData[1] = exchangeType;
        int i = 0;
        for (int j = 2; j < totalLen; j++) {
            exchangeData[j] = payload[i];
            i++;
        }
        Log("ex_pkg len(" + totalLen + "): " + _toolConvertIntArrayToString(exchangeData));
        int retTVNativeWrapper = TVNativeWrapper.ScanDtmbExchangeData_native(exchangeData);
        StringBuilder sb = new StringBuilder();
        sb.append("ex_pkg cooked: ");
        sb.append(_toolConvertIntArrayToString(exchangeData));
        Log(sb.toString());
        int i2 = 0;
        for (int j2 = 2; j2 < totalLen; j2++) {
            payload[i2] = exchangeData[j2];
            i2++;
        }
        if (retTVNativeWrapper >= 0) {
            return 0;
        }
        return -1;
    }

    private String _toolMapIntToString(int mapType, int data) {
        if (mapType != 0) {
            return "no-ops";
        }
        switch (data) {
            case 0:
                return "crnt";
            case 1:
                return "prev";
            case 2:
                return "next";
            default:
                return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }
}
