package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvScanCeBase {
    public static final int CE_RET_INTERNAL_ERR = -1;
    public static final int CE_RET_OK = 0;
    public static final int COUNTRY_CHN = 0;
    private static final int EX_HEADER_LEN = 2;
    private static final int EX_HEADER_LEN_IDX = 0;
    private static final int EX_HEADER_TYPE_IDX = 1;
    private static final int EX_MAX_LEN = 50;
    private static final int EX_PAYLOAD_1ST_IDX = 2;
    private static final int EX_TYPE_AUTO_SCAN = 3;
    private static final int EX_TYPE_CANCEL_SCAN = 1;
    private static final int EX_TYPE_QUICK_SCAN = 4;
    private static final int EX_TYPE_RF_SCAN = 2;
    private static final int EX_TYPE_UNKNOWN = 0;
    private static final int FREQ_MAX = 858000000;
    private static final int FREQ_MIN = 48000000;
    public static final int NIT_SEARCH_MODE_NUM = 2;
    public static final int NIT_SEARCH_MODE_OFF = 0;
    public static final int NIT_SEARCH_MODE_QUICK = 1;
    public static final int OPERATOR_BEIJING_GEHUAYOUXIAN = 1008;
    public static final int OPERATOR_CHANGSHA_GUOAN = 1004;
    public static final int OPERATOR_CHONGQING_YOUXIAN = 1019;
    public static final int OPERATOR_DALIAN_GUANGDIAN = 1011;
    public static final int OPERATOR_DAZHOU = 1024;
    public static final int OPERATOR_EZHOU_YOUXIAN = 1014;
    public static final int OPERATOR_GUANGZHOU_SHENGWANG = 1001;
    public static final int OPERATOR_GUANGZHOU_SHIWANG = 1002;
    public static final int OPERATOR_HANGZHOU_HUASHU = 1018;
    public static final int OPERATOR_HUBEI_HUANGSHI = 1010;
    public static final int OPERATOR_HUNAN_GUANGDIAN = 1003;
    public static final int OPERATOR_JIANGXI = 1021;
    public static final int OPERATOR_KUNSHAN = 1020;
    public static final int OPERATOR_MAX = 1024;
    public static final int OPERATOR_NEIMENGGU_YOUXIAN = 1013;
    public static final int OPERATOR_OTHERS = 0;
    public static final int OPERATOR_QINGDAO_GUANGDIAN = 1009;
    public static final int OPERATOR_SANMING_YOUXIAN = 1016;
    public static final int OPERATOR_SHANGHAI = 1015;
    public static final int OPERATOR_SHAOXING_GUANGDIAN = 1012;
    public static final int OPERATOR_SNN_OCN = 1022;
    public static final int OPERATOR_SNN_OTHERS = 1023;
    public static final int OPERATOR_WUHAN_SHENGWANG = 1005;
    public static final int OPERATOR_WUHAN_SHIWANG = 1006;
    public static final int OPERATOR_XIAN_GUANGDIAN = 1007;
    public static final int OPERATOR_ZHANGJIAGANG = 1017;
    private static final String TAG = "";
    private int cfgFlag = 0;
    private int country = 0;
    private int endFreq = 0;
    private int networkId = 0;
    private int nitMode = 0;
    private int operator = 0;
    private int startFreq = 0;

    private static void Log(int value) {
        StackTraceElement[] el = new Exception().getStackTrace();
        Log.e(TAG, el[1].getClassName() + "." + el[1].getMethodName() + " [" + new Integer(el[1].getLineNumber()).toString() + "] :---> " + value);
    }

    private static void Log(String str) {
        StackTraceElement[] el = new Exception().getStackTrace();
        Log.e(TAG, el[1].getClassName() + "." + el[1].getMethodName() + " [" + new Integer(el[1].getLineNumber()).toString() + "] :---> " + str);
    }

    public int setOperator(int value) {
        if (value == 0 || (value >= 1001 && value <= 1024)) {
            Log(this.operator + " -> " + value);
            this.operator = value;
            return 0;
        }
        Log("setting value (" + value + ") invalid, keep crnt " + this.operator);
        return -1;
    }

    public int getOperator() {
        Log(this.operator);
        return this.operator;
    }

    public int setNitMode(int value) {
        if (value < 0 || value >= 2) {
            Log("setting value (" + value + ") invalid, keep crnt " + this.nitMode);
            return -1;
        }
        Log(this.nitMode + " -> " + value);
        this.nitMode = value;
        return 0;
    }

    public int getNitMode() {
        Log(this.nitMode);
        return this.nitMode;
    }

    public int setNetworkID(int value) {
        if (value < 0 || value > 65535) {
            Log("setting value (" + value + ") invalid, keep crnt " + this.networkId);
            return -1;
        }
        Log(this.networkId + " -> " + value);
        this.networkId = value;
        return 0;
    }

    public int getNetWorkID() {
        Log(this.networkId);
        return this.networkId;
    }

    public int setStartFreq(int value) {
        if (value < FREQ_MIN || value > FREQ_MAX) {
            Log("setting value (" + value + ") invalid, keep crnt " + this.startFreq);
            return -1;
        }
        Log(this.startFreq + " -> " + value);
        this.startFreq = value;
        return 0;
    }

    public int getStartFreq() {
        Log(this.startFreq);
        return this.startFreq;
    }

    public int setEndFreq(int value) {
        if (value < FREQ_MIN || value > FREQ_MAX) {
            Log("setting value (" + value + ") invalid, keep crnt " + this.endFreq);
            return -1;
        }
        Log(this.endFreq + " -> " + value);
        this.endFreq = value;
        return 0;
    }

    public int getEndFreq() {
        Log(this.endFreq);
        return this.endFreq;
    }

    public int setCfgFlag(int value) {
        if (value >= 0) {
            Log(this.cfgFlag + " -> " + value);
            this.cfgFlag = value;
            return 0;
        }
        Log("setting value (" + value + ") invalid, keep crnt " + this.cfgFlag);
        return -1;
    }

    public int getCfgFlag() {
        Log(this.cfgFlag);
        return this.cfgFlag;
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

    public int startQuickScan(int freq) {
        Log(freq);
        int ret = setStartFreq(freq);
        if (ret != 0) {
            return ret;
        }
        int ret2 = setEndFreq(freq);
        if (ret2 != 0) {
            return ret2;
        }
        int ret3 = _toolExchange(4, new int[]{0, freq});
        Log("ret: " + ret3);
        return ret3;
    }

    public int startRfScan(int freq) {
        Log(freq);
        int ret = setStartFreq(freq);
        if (ret != 0) {
            return ret;
        }
        int ret2 = setEndFreq(freq);
        if (ret2 != 0) {
            return ret2;
        }
        int ret3 = _toolExchange(2, new int[]{0, freq});
        Log("ret: " + ret3);
        return ret3;
    }

    private String _toolConvertIntArrayToString(int[] intArray) {
        if (intArray == null) {
            Log("return null: null == intArray");
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int append : intArray) {
            stringBuilder.append(append);
            stringBuilder.append("  ");
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
        Log("ex_pkg len: " + totalLen + " [" + _toolConvertIntArrayToString(exchangeData) + "]");
        int retTVNativeWrapper = TVNativeWrapper.ScanCeExchangeData_native(exchangeData);
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
}
