package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvScanPalSecamBase {
    private static final int EXCHANGE_GET_TYPE_FREQ_RANGE = 2;
    private static final int EXCHANGE_GET_TYPE_RF_INFO = 1;
    private static final int EXCHANGE_GET_TYPE_UNKNOWN = 0;
    private static final int EXCHANGE_HEADER_LEN = 3;
    private static final int EXCHANGE_HEADER_LEN_IDX = 1;
    private static final int EXCHANGE_HEADER_TYPE_IDX = 2;
    private static final int EXCHANGE_MAX_LEN = 50;
    private static final int EXCHANGE_PAYLOAD_1ST_IDX = 3;
    private static final int EXCHANGE_SET_TYPE_AUTO_SCAN = 1002;
    private static final int EXCHANGE_SET_TYPE_CANCEL_SCAN = 1001;
    private static final int EXCHANGE_SET_TYPE_RANGE_SCAN = 1004;
    private static final int EXCHANGE_SET_TYPE_UNKNOWN = 1000;
    private static final int EXCHANGE_SET_TYPE_UPDATE_SCAN = 1003;
    private static final int EXCHANGE_TOTAL_LEN_IDX = 0;
    private static final String TAG = "MtkTvScanPalSecam";

    public enum ScanPalSecamRet {
        SCAN_PAL_SECAM_RET_OK,
        SCAN_PAL_SECAM_RET_INTERNAL_ERROR
    }

    public class ScanPalSecamFreqRange {
        public int lower_freq;
        public int upper_freq;

        public ScanPalSecamFreqRange() {
        }
    }

    public ScanPalSecamRet startAutoScan() {
        Log.d(TAG, "Enter startAutoScan\n");
        ScanPalSecamRet ret = opOnlyTypeAndRet(1002);
        Log.d(TAG, "Leave startAutoScan\n");
        return ret;
    }

    public ScanPalSecamRet startRangeScan(int startFreq, int endFreq) {
        ScanPalSecamRet ret = ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
        Log.d(TAG, "Enter startRangeScan startFreq " + startFreq + " endFreq " + endFreq + "\n");
        int[] data = {startFreq, endFreq};
        exchangeData(1004, data);
        if (data[0] == 0) {
            ret = ScanPalSecamRet.SCAN_PAL_SECAM_RET_OK;
        }
        Log.d(TAG, "Leave startRangeScan return" + ret + "\n");
        return ret;
    }

    public ScanPalSecamRet startUpdateScan() {
        Log.d(TAG, "Enter startUpdateScan\n");
        ScanPalSecamRet ret = opOnlyTypeAndRet(1003);
        Log.d(TAG, "Leave startUpdateScan\n");
        return ret;
    }

    public ScanPalSecamRet cancelScan() {
        Log.d(TAG, "Enter cancelScan\n");
        ScanPalSecamRet ret = opOnlyTypeAndRet(1001);
        Log.d(TAG, "Leave cancelScan\n");
        return ret;
    }

    public ScanPalSecamRet getFreqRange(ScanPalSecamFreqRange range) {
        ScanPalSecamRet ret = ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
        int[] data = new int[3];
        Log.d(TAG, "Enter getFreqRange\n");
        exchangeData(2, data);
        int mwScanRet = data[0];
        if (mwScanRet == 0) {
            ret = ScanPalSecamRet.SCAN_PAL_SECAM_RET_OK;
        }
        range.upper_freq = data[1];
        range.lower_freq = data[2];
        Log.d(TAG, "getFreqRange: ret " + mwScanRet + " upper_freq " + range.upper_freq + " lower_freq " + range.lower_freq + "\n");
        Log.d(TAG, "Leave getFreqRange\n");
        return ret;
    }

    private ScanPalSecamRet opOnlyTypeAndRet(int exchangeScanType) {
        Log.d(TAG, "Enter opOnlyTypeAndRet:" + exchangeScanType + "\n");
        ScanPalSecamRet ret = ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
        int[] data = {0};
        exchangeData(exchangeScanType, data);
        int mwScanRet = data[0];
        Log.d(TAG, "ret opOnlyTypeAndRet:" + mwScanRet + "\n");
        if (mwScanRet == 0) {
            ret = ScanPalSecamRet.SCAN_PAL_SECAM_RET_OK;
        }
        Log.d(TAG, "Leave opOnlyTypeAndRet\n");
        return ret;
    }

    private ScanPalSecamRet exchangeData(int exchangeType, int[] data) {
        ScanPalSecamRet scanPalSecamRet = ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
        int[] payload = data;
        if (payload == null) {
            payload = new int[1];
        }
        int totalLen = payload.length + 3;
        if (totalLen > 50) {
            Log.d(TAG, "[Error]bigger than EXCHANGE_MAX_LEN\n");
            return ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
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
        int retTVNativeWrapper = TVNativeWrapper.ScanPalSecamExchangeData_native(exchangeData);
        int i2 = 0;
        for (int j2 = 3; j2 < totalLen; j2++) {
            payload[i2] = exchangeData[j2];
            i2++;
        }
        if (retTVNativeWrapper >= 0) {
            return ScanPalSecamRet.SCAN_PAL_SECAM_RET_OK;
        }
        return ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
    }
}
