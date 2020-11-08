package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvScanATSCBase {
    private static final int EXCHANGE_GET_CHANNELID_BY_RF_INDEX = 5;
    private static final int EXCHANGE_GET_CURRENT_RF_INDEX = 4;
    private static final int EXCHANGE_GET_FIRST_SCAN_INDEX = 1;
    private static final int EXCHANGE_GET_LAST_SCAN_INDEX = 2;
    private static final int EXCHANGE_GET_TUNER_MODE_INDEX = 3;
    private static final int EXCHANGE_GET_TYPE_UNKNOWN = 0;
    private static final int EXCHANGE_HEADER_LEN = 3;
    private static final int EXCHANGE_HEADER_LEN_IDX = 1;
    private static final int EXCHANGE_HEADER_TYPE_IDX = 2;
    private static final int EXCHANGE_MAX_LEN = 50;
    private static final int EXCHANGE_PAYLOAD_1ST_IDX = 3;
    private static final int EXCHANGE_SET_TYPE_UNKNOWN = 1000;
    private static final int EXCHANGE_TOTAL_LEN_IDX = 0;
    private static final String TAG = "MtkTvScanATSC";

    public enum ScanATSCRet {
        SCAN_ATSC_RET_OK,
        SCAN_ATSC_RET_INTERNAL_ERROR
    }

    public int getFirstScanIndex() {
        int[] data = {0};
        if (ScanATSCRet.SCAN_ATSC_RET_OK == exchangeData(1, data)) {
            Log.d(TAG, "ATSC firstIndex=" + data[0]);
            return data[0];
        }
        Log.d(TAG, "ATSC exchangeData fail");
        return 0;
    }

    public int getLastScanIndex() {
        int[] data = {0};
        if (ScanATSCRet.SCAN_ATSC_RET_OK == exchangeData(2, data)) {
            Log.d(TAG, "ATSC lastIndex=" + data[0]);
            return data[0];
        }
        Log.d(TAG, "ATSC exchangeData fail");
        return 0;
    }

    public int getTunerModeIndex() {
        int[] data = {0};
        if (ScanATSCRet.SCAN_ATSC_RET_OK == exchangeData(3, data)) {
            Log.d(TAG, "ATSC tunerModeIndex=" + data[0]);
            return data[0];
        }
        Log.d(TAG, "ATSC exchangeData fail");
        return -1;
    }

    public int getCurrentRFIndex() {
        int[] data = {0};
        if (ScanATSCRet.SCAN_ATSC_RET_OK == exchangeData(4, data)) {
            Log.d(TAG, "ATSC getCurrentRFIndex=" + data[0]);
            return data[0];
        }
        Log.d(TAG, "ATSC exchangeData fail");
        return -1;
    }

    public int getChannelIDByRFIndex(int rfIndex) {
        int[] data = {rfIndex};
        if (ScanATSCRet.SCAN_ATSC_RET_OK == exchangeData(5, data)) {
            Log.d(TAG, "ATSC getChannelIDByRFIndex=" + data[0]);
            return data[0];
        }
        Log.d(TAG, "ATSC exchangeData fail");
        return -1;
    }

    private ScanATSCRet exchangeData(int exchangeType, int[] data) {
        ScanATSCRet scanATSCRet = ScanATSCRet.SCAN_ATSC_RET_INTERNAL_ERROR;
        int[] payload = data;
        if (payload == null) {
            payload = new int[1];
        }
        int totalLen = payload.length + 3;
        if (totalLen > 50) {
            Log.d(TAG, "[Error]bigger than EXCHANGE_MAX_LEN\n");
            return ScanATSCRet.SCAN_ATSC_RET_INTERNAL_ERROR;
        }
        int[] exchangeData = new int[totalLen];
        exchangeData[0] = totalLen;
        exchangeData[1] = 3;
        exchangeData[2] = exchangeType;
        int i = 0;
        for (int j = 3; j < totalLen; j++) {
            exchangeData[j] = payload[i];
            i++;
        }
        int retTVNativeWrapper = TVNativeWrapper.ScanATSCExchangeData_native(exchangeData);
        int i2 = 0;
        for (int j2 = 3; j2 < totalLen; j2++) {
            payload[i2] = exchangeData[j2];
            i2++;
        }
        if (retTVNativeWrapper >= 0) {
            return ScanATSCRet.SCAN_ATSC_RET_OK;
        }
        return ScanATSCRet.SCAN_ATSC_RET_INTERNAL_ERROR;
    }
}
