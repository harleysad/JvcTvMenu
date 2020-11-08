package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvCQAMScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcManualScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsSatelliteSettingBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanParaBase;

public class MtkTvScanBase {
    private static final String TAG = "TV_MtkTvScan";
    private static MarketRegion mRegion = MarketRegion.PLF_OPT_REGION_EU;
    private MtkTvScanATSCBase mtkTvScanATSCBase = new MtkTvScanATSCBase();
    private MtkTvScanDvbcBase mtkTvScanDvbcBase = new MtkTvScanDvbcBase();
    private MtkTvScanDvbtBase mtkTvScanDvbtBase = new MtkTvScanDvbtBase();
    private MtkTvScanISDBBase mtkTvScanISDBBase = new MtkTvScanISDBBase();
    private MtkTvScanPalSecamBase mtkTvScanPalSecamBase = new MtkTvScanPalSecamBase();

    public enum CallBackType {
        CALL_BACK_TYPE_AUTO,
        CALL_BACK_TYPE_ATV,
        CALL_BACK_TYPE_DTV,
        CALL_BACK_TYPE_DTV_DVBT_UI_OP,
        CALL_BACK_TYPE_DTV_DVBT_NWK_UPDATE,
        CALL_BACK_TYPE_DTV_DVBT_TYPE_NUM,
        CALL_BACK_TYPE_DTV_DVBT_FULL_SCAN,
        CALL_BACK_TYPE_DTV_DVBT_SVC_UPDATE,
        CALL_BACK_TYPE_DTV_DVBC_NWK_UPDATE,
        CALL_BACK_TYPE_DTV_DVBS_SAT_NAME_NFY_FCT,
        CALL_BACK_TYPE_DTV_DVBS_SVC_UPDATE_NFY_FCT,
        CALL_BACK_TYPE_DTV_DVBS_NEW_SVC_NFY_FCT,
        CALL_BACK_TYPE_DTV_DVBS_BOUQUET_INFO_NFY_FCT,
        CALL_BACK_TYPE_DTV_DVBS_MDU_DETECT_NFY_FCT,
        CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT,
        CALL_BACK_TYPE_DTV_DVBC_NW_NAME_GOTTEN,
        CALL_BACK_TYPE_DTV_DVBC_FREQ_LOCK_FAIL,
        CALL_BACK_TYPE_DTV_DVBC_NID_NOT_FOUND,
        CALL_BACK_TYPE_DTV_DVBC_SVC_UPDATE,
        CALL_BACK_TYPE_DTV_DVBC_TYPE_NUM,
        CALL_BACK_TYPE_NUM
    }

    public enum DvbcNitMode {
        DVBC_NIT_SEARCH_MODE_OFF,
        DVBC_NIT_SEARCH_MODE_QUICK,
        DVBC_NIT_SEARCH_MODE_EX_QUICK,
        DVBC_NIT_SEARCH_MODE_NUM
    }

    public enum MarketRegion {
        PLF_OPT_REGION_NAFTA,
        PLF_OPT_REGION_LATAM,
        PLF_OPT_REGION_GA,
        PLF_OPT_REGION_CHINA,
        PLF_OPT_REGION_EU,
        PLF_OPT_REGION_RUSSIA,
        PLF_OPT_REGION_PAD,
        PLF_OPT_REGION_TAIWAN,
        PLF_OPT_REGION_COLOMBIA,
        PLF_OPT_REGION_LAST_VALID_ENTRY
    }

    public enum ScanMode {
        SCAN_MODE_FULL,
        SCAN_MODE_UPDATE,
        SCAN_MODE_RANGE,
        SCAN_MODE_QUICK,
        SCAN_MODE_ADD_ON,
        SCAN_MODE_SINGLE_RF_CHANNEL,
        SCAN_MODE_RANGE_RF_CHANNEL,
        SCAN_MODE_MANUAL_RF_CHANNEL,
        SCAN_MODE_MANUAL_FREQ,
        SCAN_MODE_BGM,
        SCAN_MODE_DVBC_NIT_SEARCH_MODE_OFF,
        SCAN_MODE_DVBC_NIT_SEARCH_MODE_QUICK,
        SCAN_MODE_DVBC_NIT_SEARCH_MODE_EX_QUICK,
        SCAN_MODE_NUM
    }

    public enum ScanType {
        SCAN_TYPE_PAL,
        SCAN_TYPE_DVBT,
        SCAN_TYPE_DVBC,
        SCAN_TYPE_NTSC,
        SCAN_TYPE_ATSC,
        SCAN_TYPE_ISDB,
        SCAN_TYPE_CQAM,
        SCAN_TYPE_DVBT2,
        SCAN_TYPE_DVBS,
        SCAN_TYPE_DTMB,
        SCAN_TYPE_US,
        SCAN_TYPE_SA,
        SCAN_TYPE_NUM
    }

    public enum UIMode {
        UI_MODE_NETWORK,
        UI_MODE_LCN,
        UI_MODE_LCN_V2,
        UI_MODE_UK_REGION,
        UI_MODE_NULL,
        UI_MODE_NUM
    }

    MtkTvScanBase() {
    }

    public MtkTvScanDvbcBase getScanDvbcInstance() {
        return this.mtkTvScanDvbcBase;
    }

    public MtkTvScanATSCBase getScanATSCInstance() {
        return this.mtkTvScanATSCBase;
    }

    public MtkTvScanISDBBase getScanISDBInstance() {
        return this.mtkTvScanISDBBase;
    }

    public MtkTvScanDvbtBase getScanDvbtInstance() {
        return this.mtkTvScanDvbtBase;
    }

    public MtkTvScanPalSecamBase getScanPalSecamInstance() {
        return this.mtkTvScanPalSecamBase;
    }

    public int startScan(ScanType mType, ScanMode mMode, boolean isDefaultSetting) {
        Log.d(TAG, "Enter startScan\n");
        int type = 0;
        int mode = 0;
        if (ScanType.SCAN_TYPE_PAL == mType) {
            type = 0;
        } else if (ScanType.SCAN_TYPE_DVBT == mType) {
            type = 1;
        } else if (ScanType.SCAN_TYPE_DVBC == mType) {
            type = 2;
        } else if (ScanType.SCAN_TYPE_NTSC == mType) {
            type = 3;
        } else if (ScanType.SCAN_TYPE_ATSC == mType) {
            type = 4;
        } else if (ScanType.SCAN_TYPE_ISDB == mType) {
            type = 5;
        } else if (ScanType.SCAN_TYPE_CQAM == mType) {
            type = 6;
        } else if (ScanType.SCAN_TYPE_DVBT2 == mType) {
            type = 7;
        } else if (ScanType.SCAN_TYPE_DVBS == mType) {
            type = 8;
        } else if (ScanType.SCAN_TYPE_DTMB == mType) {
            type = 9;
        } else if (ScanType.SCAN_TYPE_US == mType) {
            type = 10;
        } else if (ScanType.SCAN_TYPE_SA == mType) {
            type = 11;
        }
        if (ScanMode.SCAN_MODE_FULL == mMode) {
            mode = 0;
        } else if (ScanMode.SCAN_MODE_UPDATE == mMode) {
            mode = 1;
        } else if (ScanMode.SCAN_MODE_RANGE == mMode) {
            mode = 2;
        } else if (ScanMode.SCAN_MODE_QUICK == mMode) {
            mode = 3;
        } else if (ScanMode.SCAN_MODE_ADD_ON == mMode) {
            mode = 4;
        } else if (ScanMode.SCAN_MODE_SINGLE_RF_CHANNEL == mMode) {
            mode = 5;
        } else if (ScanMode.SCAN_MODE_RANGE_RF_CHANNEL == mMode) {
            mode = 6;
        } else if (ScanMode.SCAN_MODE_MANUAL_RF_CHANNEL == mMode) {
            mode = 7;
        } else if (ScanMode.SCAN_MODE_MANUAL_FREQ == mMode) {
            mode = 8;
        }
        int ret = TVNativeWrapper.startScan_native(type, mode, isDefaultSetting);
        Log.d(TAG, "Leave startScan\n");
        return ret;
    }

    public int cancelScan() {
        Log.d(TAG, "Enter cancelScan\n");
        int ret = TVNativeWrapper.cancelScan_native();
        Log.d(TAG, "Leave cancelScan\n");
        return ret;
    }

    public boolean isScanning() {
        return TVNativeWrapper.isScaning_native();
    }

    public int setDvbScanParas(MtkTvDvbScanParaBase mDvbScanPara) {
        Log.d(TAG, "Enter setDvbScanParas\n");
        int ret = TVNativeWrapper.setDvbScanParas(mDvbScanPara);
        Log.d(TAG, "Leave setDvbScanParas\n");
        return ret;
    }

    public int setDvbcManualScanParas(MtkTvDvbcManualScanParaBase mDvbcScanPara) {
        Log.d(TAG, "Enter setDvbcManualScanParas\n");
        int ret = TVNativeWrapper.setDvbcManualScanParas(mDvbcScanPara);
        Log.d(TAG, "Leave setDvbcManualScanParas\n");
        return ret;
    }

    public int setDvbcScanParas(MtkTvDvbcScanParaBase mDvbcScanPara) {
        Log.d(TAG, "Enter setDvbcScanParas\n");
        int ret = TVNativeWrapper.setDvbcScanParas(mDvbcScanPara);
        Log.d(TAG, "Leave setDvbcScanParas\n");
        return ret;
    }

    public int setSatelliteSetting(MtkTvDvbsSatelliteSettingBase mPara) {
        Log.d(TAG, "Enter setSatelliteSetting\n");
        int ret = TVNativeWrapper.setSatelliteSetting(mPara);
        Log.d(TAG, "Leave setSatelliteSetting\n");
        return ret;
    }

    public int setATSCScanParas(MtkTvATSCScanParaBase mATSCScanPara) {
        Log.d(TAG, "Enter setATSCScanParas\n");
        int ret = TVNativeWrapper.setATSCScanParas(mATSCScanPara);
        Log.d(TAG, "Leave setATSCScanParas\n");
        return ret;
    }

    public int setCQAMScanParas(MtkTvCQAMScanParaBase mCQAMScanPara) {
        return 0;
    }

    public int setISDBScanParas(MtkTvISDBScanParaBase mISDBScanPara) {
        Log.d(TAG, "Enter setISDBScanParas\n");
        int ret = TVNativeWrapper.setISDBScanParas(mISDBScanPara);
        Log.d(TAG, "Leave setISDBScanParas\n");
        return ret;
    }

    public int setNTSCScanParas(MtkTvNTSCScanParaBase mNTSCScanPara) {
        Log.d(TAG, "Enter setNTSCScanParas\n");
        int ret = TVNativeWrapper.setNTSCScanParas(mNTSCScanPara);
        Log.d(TAG, "Leave setNTSCScanParas\n");
        return ret;
    }

    public static MarketRegion getMarketRegion() {
        Log.d(TAG, "Enter getMarketRegion\n");
        int ret = TVNativeWrapper.getMarketRegion();
        Log.d(TAG, "getMarketRegion " + ret);
        if (ret == 0) {
            mRegion = MarketRegion.PLF_OPT_REGION_NAFTA;
            Log.d(TAG, "PLF_OPT_REGION_NAFTA\n");
        } else if (1 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_LATAM;
            Log.d(TAG, "PLF_OPT_REGION_LATAM\n");
        } else if (2 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_GA;
            Log.d(TAG, "PLF_OPT_REGION_GA\n");
        } else if (3 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_CHINA;
            Log.d(TAG, "PLF_OPT_REGION_CHINA\n");
        } else if (4 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_EU;
            Log.d(TAG, "PLF_OPT_REGION_EU\n");
        } else if (5 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_RUSSIA;
            Log.d(TAG, "PLF_OPT_REGION_RUSSIA\n");
        } else if (6 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_PAD;
            Log.d(TAG, "PLF_OPT_REGION_PAD\n");
        } else if (7 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_TAIWAN;
            Log.d(TAG, "PLF_OPT_REGION_TAIWAN\n");
        } else if (8 == ret) {
            mRegion = MarketRegion.PLF_OPT_REGION_COLOMBIA;
            Log.d(TAG, "PLF_OPT_REGION_COLOMBIA\n");
        } else {
            mRegion = MarketRegion.PLF_OPT_REGION_EU;
            Log.d(TAG, "PLF_OPT_REGION_EU\n");
        }
        Log.d(TAG, "Leave getMarketRegion\n");
        return mRegion;
    }

    public UIMode getUIMode() {
        UIMode mUIMode;
        Log.d(TAG, "Enter getUIMode\n");
        UIMode uIMode = UIMode.UI_MODE_NUM;
        int ret = TVNativeWrapper.cancelScan_native();
        if (ret == 0) {
            mUIMode = UIMode.UI_MODE_NETWORK;
        } else if (1 == ret) {
            mUIMode = UIMode.UI_MODE_LCN;
        } else if (2 == ret) {
            mUIMode = UIMode.UI_MODE_LCN_V2;
        } else if (3 == ret) {
            mUIMode = UIMode.UI_MODE_UK_REGION;
        } else if (4 == ret) {
            mUIMode = UIMode.UI_MODE_NULL;
        } else {
            mUIMode = UIMode.UI_MODE_NUM;
        }
        Log.d(TAG, "Leave getUIMode\n");
        return mUIMode;
    }
}
