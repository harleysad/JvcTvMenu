package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeDeliveryTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeFirmwareInfoBase;

public class MtkTvUpgradeBase {
    public static final String TAG = "MtkTvUpgrageBase";

    public int startUpgrade(MtkTvUpgradeDeliveryTypeBase type, boolean flag) throws MtkTvExceptionBase {
        int ret;
        Log.d(TAG, "+ startUpgrade(" + type + ", " + flag);
        try {
            synchronized (this) {
                ret = TVNativeWrapper.startUpgrade_native(type, flag);
            }
            if (ret != 0) {
                throw new MtkTvExceptionBase(ret, "TVNativeWrapper.startUpgrade_native fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- startUpgrade(" + type + ", " + flag);
        return 0;
    }

    public int leaveUpgrade(MtkTvUpgradeDeliveryTypeBase type) throws MtkTvExceptionBase {
        int ret;
        Log.d(TAG, "+ leaveUpgrade(" + type);
        try {
            synchronized (this) {
                ret = TVNativeWrapper.startUpgrade_native(type, false);
            }
            if (ret != 0) {
                throw new MtkTvExceptionBase(ret, "TVNativeWrapper.startUpgrade_native fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- leaveUpgrade(" + type);
        return 0;
    }

    public int triggerUpgrade(MtkTvUpgradeDeliveryTypeBase type) {
        int ret;
        Log.d(TAG, "+ triggerUpgrade(" + type + ")");
        try {
            synchronized (this) {
                ret = TVNativeWrapper.triggerUpgrade_native(type);
            }
            if (ret != 0) {
                throw new MtkTvExceptionBase(ret, "TVNativeWrapper.triggerUpgrade_native fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- triggerUpgrade(" + type + ")");
        return 0;
    }

    public int getFirmwareInfo(MtkTvUpgradeFirmwareInfoBase firmwareInfo) {
        int firmwareInfo_native;
        Log.d(TAG, "+ getFirmwareInfo()");
        try {
            synchronized (this) {
                firmwareInfo_native = TVNativeWrapper.getFirmwareInfo_native(firmwareInfo);
            }
            return firmwareInfo_native;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "- getFirmwareInfo()");
            return -1;
        }
    }

    public int startDownloadFirmware(MtkTvUpgradeDeliveryTypeBase type, String url, String fwPath) {
        int startDownloadFirmware_native;
        Log.d(TAG, "+ startDownloadFirmware()");
        try {
            synchronized (this) {
                startDownloadFirmware_native = TVNativeWrapper.startDownloadFirmware_native(type, url, fwPath);
            }
            return startDownloadFirmware_native;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "- startDownloadFirmware()");
            return -1;
        }
    }

    public int cancelDownloadFirmware(MtkTvUpgradeDeliveryTypeBase type) {
        int cancelDownloadFirmware_native;
        Log.d(TAG, "+ cancelDownloadFirmware()");
        try {
            synchronized (this) {
                cancelDownloadFirmware_native = TVNativeWrapper.cancelDownloadFirmware_native(type);
            }
            return cancelDownloadFirmware_native;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "- cancelDownloadFirmware()");
            return -1;
        }
    }

    public int startRebootUpgrade(MtkTvUpgradeDeliveryTypeBase type) {
        int startRebootUpgrade_native;
        Log.d(TAG, "+ startRebootUpgrade()");
        try {
            synchronized (this) {
                startRebootUpgrade_native = TVNativeWrapper.startRebootUpgrade_native(type);
            }
            return startRebootUpgrade_native;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "- startRebootUpgrade()");
            return -1;
        }
    }

    public int queryUpgradeResult(MtkTvUpgradeDeliveryTypeBase type) {
        int queryUpgradeResult_native;
        Log.d(TAG, "+ queryUpgradeResult()");
        try {
            synchronized (this) {
                queryUpgradeResult_native = TVNativeWrapper.queryUpgradeResult_native(type);
            }
            return queryUpgradeResult_native;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "- queryUpgradeResult()");
            return -1;
        }
    }
}
