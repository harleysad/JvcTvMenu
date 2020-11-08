package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyHeader;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyInfoBase;

public class MtkTvBisskeyBase {
    public static final int BSL_OPERATOR_ADD = 0;
    public static final int BSL_OPERATOR_DEL = 2;
    public static final int BSL_OPERATOR_MOD = 1;
    public static final int BSL_RET_FAIL = -1;
    public static final int BSL_RET_OK = 0;
    static final String TAG = "MtkTvBisskey";

    public int getRecordsNumber(int bslId) {
        Log.d(TAG, "Enter getRecordsNumber(" + bslId + ")\n");
        int ret = TVNativeWrapper.getRecordsNumber_native(bslId);
        if (ret >= 0) {
            return ret;
        }
        Log.e(TAG, "TVNativeWrapper.getRecordsNumber failed! return " + ret + ".\n");
        return -1;
    }

    public int setBisskeyInfo(int bslId, MtkTvBisskeyInfoBase bslInfo, int operator) {
        Log.d(TAG, "Enter setBisskeyInfo(" + bslId + ", bslInfo ," + operator + ")\n");
        if (operator < 0 || operator > 2) {
            Log.e(TAG, "operator unrecognized failed! operator = " + operator + ".\n");
            return -1;
        }
        int ret = TVNativeWrapper.setBisskeyInfo_native(bslId, bslInfo, operator);
        if (ret >= 0) {
            return ret;
        }
        Log.e(TAG, "TVNativeWrapper.setBisskeyInfo failed! return " + ret + ".\n");
        return -1;
    }

    public MtkTvBisskeyInfoBase getRecordByBslRecId(int bslId, int bslRecId) {
        Log.d(TAG, "Enter getRecordByBslRecId(" + bslId + ", " + bslRecId + ")\n");
        return TVNativeWrapper.getRecordByBslRecId_native(bslId, bslRecId);
    }

    public int getRecordNumByHeader(int bslId, MtkTvBisskeyHeader header) {
        Log.d(TAG, "Enter getRecordNumByHeader(" + bslId + ")\n");
        if (header == null) {
            Log.e(TAG, "header is null! .\n");
            return -1;
        }
        int ret = TVNativeWrapper.getRecordNumByHeader_native(bslId, header);
        if (ret >= 0) {
            return ret;
        }
        Log.e(TAG, "TVNativeWrapper.getRecordNumByHeader failed! return " + ret + ".\n");
        return -1;
    }

    public MtkTvBisskeyInfoBase getRecordByHeader(int bslId, MtkTvBisskeyHeader header, int index) {
        Log.d(TAG, "Enter getRecordByHeader(" + bslId + ", index=" + index + ")\n");
        if (header != null) {
            return TVNativeWrapper.getRecordByHeader_native(bslId, header, index);
        }
        Log.e(TAG, "header is null! .\n");
        return null;
    }

    public MtkTvBisskeyInfoBase getRecordByIndex(int bslId, int index) {
        Log.d(TAG, "Enter getRecordByIndex(" + bslId + ", " + index + ")\n");
        return TVNativeWrapper.getRecordByIndex_native(bslId, index);
    }

    public int bisskeyClean(int bslId) {
        Log.d(TAG, "Enter bisskeyClean(" + bslId + ")\n");
        int ret = TVNativeWrapper.bisskeyClean_native(bslId);
        if (ret >= 0) {
            return ret;
        }
        Log.e(TAG, "TVNativeWrapper.bisskeyClean failed! return " + ret + ".\n");
        return -1;
    }

    public boolean bisskeyIsSameRecord(MtkTvBisskeyInfoBase bslInfo1, MtkTvBisskeyInfoBase bslInfo2) {
        Log.d(TAG, "Enter bisskeyIsSameRecord()\n");
        int ret = TVNativeWrapper.bisskeyIsSameRecord_native(bslInfo1, bslInfo2);
        Log.d(TAG, "TVNativeWrapper.bisskeyIsSameRecord , return " + ret + ".\n");
        if (ret == 1) {
            return true;
        }
        return false;
    }

    public MtkTvBisskeyInfoBase bisskeyGetDefaultRecord() {
        Log.d(TAG, "Enter bisskeyGetDefaultRecord()\n");
        return TVNativeWrapper.bisskeyGetDefaultRecord_native();
    }

    public int bisskeySetKeyForCurrentChannel() {
        Log.d(TAG, "Enter bisskeySetKeyForCurrentChannels()\n");
        return TVNativeWrapper.bisskeySetKeyForCurrentChannel_native();
    }
}
