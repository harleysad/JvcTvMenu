package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvDvbsSatelliteSettingBase {
    private static final String TAG = "TV_MtkTvDvbsSatelliteSetting";
    private DvbsSatellite22kType m22k = DvbsSatellite22kType.TUNER_DISEQC_22K_OFF;
    private int mBandfreq = 950;
    private DvbsChannelType mChannelType = DvbsChannelType.DVBS_SCAN_CHANNEL_TYPE_ALL;
    DisqecCfgUniversal mDisqCfg = DisqecCfgUniversal.DISCQEC_CFG_DISABLE;
    private int mLnbHighFreq = 10600;
    private int mLnbLowFreq = 9750;
    private DvbsTunerPolarization mPolarization = DvbsTunerPolarization.POL_LIN_HORIZONTAL;
    private int mPort = 0;
    private DvbsSatelliteRecID mSatelliteRecID = DvbsSatelliteRecID.DVBS_SATELLITE_1;
    private DvbsSatelliteScanType mSatelliteScanType = DvbsSatelliteScanType.DVBS_SCAN_TYPE_FULL;
    private DvbsSatelliteStatus mSatelliteStatus = DvbsSatelliteStatus.SATELLITE_ON;
    private DvbsServiceType mServiceType = DvbsServiceType.DVBS_SCAN_SERVICE_TYPE_ALL;
    private int mSymbolRate = 10714;
    private int mToneBurst = 0;
    private int mTransponderFreq = 10714;
    private TunerDiseqcType mTunerDiseqc = TunerDiseqcType.DISEQC_NONE;
    private TunerLnbType mTunerLnb = TunerLnbType.LNB_DUAL_FREQ;
    private TunerLnbPowerType mTunerLnbPower = TunerLnbPowerType.LNB_POWER_OFF;
    private TunerUnicablePosType mTunerUnicablePos = TunerUnicablePosType.UNICABLE_POS_A;
    private TunerUserBandType mTunerUserBand = TunerUserBandType.USER_BAND_1;

    public enum DisqecCfgUniversal {
        DISCQEC_CFG_DISABLE,
        DISCQEC_CFG_A,
        DISCQEC_CFG_B,
        DISCQEC_CFG_C,
        DISCQEC_CFG_D,
        DISCQEC_CFG_TONE_A,
        DISCQEC_CFG_TONE_B
    }

    public enum DvbsChannelType {
        DVBS_SCAN_CHANNEL_TYPE_ALL,
        DVBS_SCAN_CHANNEL_TYPE_TV,
        DVBS_SCAN_CHANNEL_TYPE_TV_AND_RADIO,
        DVBS_SCAN_CHANNEL_TYPE_NUM
    }

    public enum DvbsSatellite22kType {
        TUNER_DISEQC_22K_OFF,
        TUNER_DISEQC_22K_ON,
        TUNER_DISEQC_22K_AUTO,
        TUNER_DISEQC_22K_LAST_ENTRY
    }

    public enum DvbsSatelliteRecID {
        DVBS_SATELLITE_1,
        DVBS_SATELLITE_2,
        DVBS_SATELLITE_3,
        DVBS_SATELLITE_4
    }

    public enum DvbsSatelliteScanType {
        DVBS_SCAN_TYPE_FULL,
        DVBS_SCAN_TYPE_NETWORK,
        DVBS_SCAN_TYPE_MANUAL,
        DVBS_SCAN_TYPE_LAST_ENTRY
    }

    public enum DvbsSatelliteStatus {
        SATELLITE_ON,
        SATELLITE_OFF,
        SATELLITE_LAST_ENTRY
    }

    public enum DvbsServiceType {
        DVBS_SCAN_SERVICE_TYPE_ALL,
        DVBS_SCAN_SERVICE_TYPE_FREE,
        DVBS_SCAN_SERVICE_TYPE_NUM
    }

    public enum DvbsTunerPolarization {
        POL_UNKNOWN,
        POL_LIN_HORIZONTAL,
        POL_LIN_VERTICAL,
        POL_CIR_LEFT,
        POL_CIR_RIGHT
    }

    public enum TunerDiseqcType {
        DISEQC_NONE,
        DISEQC_2X1,
        DISEQC_4X1,
        DISEQC_8X1,
        DISEQC_16X1,
        DISEQC_1DOT2,
        DISEQC_USALS,
        DISEQC_UNICABLE
    }

    public enum TunerLnbPowerType {
        LNB_POWER_OFF,
        LNB_POWER_13V_18V,
        LNB_POWER_14V_19V,
        LNB_POWER_ON
    }

    public enum TunerLnbType {
        LNB_UNKNOWN,
        LNB_SINGLE_FREQ,
        LNB_DUAL_FREQ
    }

    public enum TunerUnicablePosType {
        UNICABLE_POS_UNKNOWN,
        UNICABLE_POS_A,
        UNICABLE_POS_B
    }

    public enum TunerUserBandType {
        USER_BAND_1,
        USER_BAND_2,
        USER_BAND_3,
        USER_BAND_4,
        USER_BAND_5,
        USER_BAND_6,
        USER_BAND_7,
        USER_BAND_8
    }

    public int setTunerUserBandType(TunerUserBandType mValue) {
        Log.d(TAG, "Enter setTunerUserBandType\n");
        this.mTunerUserBand = mValue;
        Log.d(TAG, "Leave setTunerUserBandType\n");
        return 0;
    }

    public TunerUserBandType getTunerUserBandTypeEnum() {
        Log.d(TAG, "getTunerUserBandType Ocurr\n");
        return this.mTunerUserBand;
    }

    public int getTunerUserBandType() {
        Log.d(TAG, "getTunerUserBandType Ocurr\n");
        if (TunerUserBandType.USER_BAND_1 == this.mTunerUserBand) {
            return 0;
        }
        if (TunerUserBandType.USER_BAND_2 == this.mTunerUserBand) {
            return 1;
        }
        if (TunerUserBandType.USER_BAND_3 == this.mTunerUserBand) {
            return 2;
        }
        if (TunerUserBandType.USER_BAND_4 == this.mTunerUserBand) {
            return 3;
        }
        if (TunerUserBandType.USER_BAND_5 == this.mTunerUserBand) {
            return 4;
        }
        if (TunerUserBandType.USER_BAND_6 == this.mTunerUserBand) {
            return 5;
        }
        if (TunerUserBandType.USER_BAND_7 == this.mTunerUserBand) {
            return 6;
        }
        if (TunerUserBandType.USER_BAND_8 == this.mTunerUserBand) {
            return 7;
        }
        return 0;
    }

    public int setBandfreq(int mValue) {
        Log.d(TAG, "Enter setBandfreq\n");
        int ret = 0;
        if (mValue < 0 || mValue > 9999) {
            ret = -1;
        } else {
            this.mBandfreq = mValue;
        }
        Log.d(TAG, "Leave setBandfreq\n");
        return ret;
    }

    public int getBandfreq() {
        Log.d(TAG, "getBandfreq Ocurr\n");
        return this.mBandfreq;
    }

    public int setSatelliteRecID(DvbsSatelliteRecID mValue) {
        Log.d(TAG, "Enter setSatelliteRecID\n");
        this.mSatelliteRecID = mValue;
        Log.d(TAG, "Leave setSatelliteRecID\n");
        return 0;
    }

    public DvbsSatelliteRecID getSatelliteRecIDEnum() {
        Log.d(TAG, "getSatelliteRecID Ocurr\n");
        return this.mSatelliteRecID;
    }

    public int getSatelliteRecID() {
        Log.d(TAG, "getSatelliteRecID Ocurr\n");
        if (DvbsSatelliteRecID.DVBS_SATELLITE_1 == this.mSatelliteRecID) {
            return 1;
        }
        if (DvbsSatelliteRecID.DVBS_SATELLITE_2 == this.mSatelliteRecID) {
            return 2;
        }
        if (DvbsSatelliteRecID.DVBS_SATELLITE_3 == this.mSatelliteRecID) {
            return 3;
        }
        if (DvbsSatelliteRecID.DVBS_SATELLITE_4 == this.mSatelliteRecID) {
            return 4;
        }
        return 1;
    }

    public int setSatelliteOnOff(DvbsSatelliteStatus mValue) {
        Log.d(TAG, "Enter setSatelliteOnOff\n");
        this.mSatelliteStatus = mValue;
        Log.d(TAG, "Leave setSatelliteOnOff\n");
        return 0;
    }

    public DvbsSatelliteStatus getSatelliteOnOffEnum() {
        Log.d(TAG, "getSatelliteOnOff Ocurr\n");
        return this.mSatelliteStatus;
    }

    public int getSatelliteOnOff() {
        Log.d(TAG, "getSatelliteOnOff Ocurr\n");
        if (DvbsSatelliteStatus.SATELLITE_ON == this.mSatelliteStatus) {
            return 1;
        }
        if (DvbsSatelliteStatus.SATELLITE_OFF == this.mSatelliteStatus) {
            return 0;
        }
        return 0;
    }

    public int setSatelliteScanType(DvbsSatelliteScanType mValue) {
        Log.d(TAG, "Enter setSatelliteScanType\n");
        this.mSatelliteScanType = mValue;
        Log.d(TAG, "Leave setSatelliteScanType\n");
        return 0;
    }

    public DvbsSatelliteScanType getSatelliteScanTypeEnum() {
        Log.d(TAG, "getSatelliteScanType Ocurr\n");
        return this.mSatelliteScanType;
    }

    public int getSatelliteScanType() {
        Log.d(TAG, "getSatelliteScanType Ocurr\n");
        if (DvbsSatelliteScanType.DVBS_SCAN_TYPE_FULL == this.mSatelliteScanType) {
            return 0;
        }
        if (DvbsSatelliteScanType.DVBS_SCAN_TYPE_NETWORK == this.mSatelliteScanType) {
            return 1;
        }
        if (DvbsSatelliteScanType.DVBS_SCAN_TYPE_MANUAL == this.mSatelliteScanType) {
            return 2;
        }
        return 0;
    }

    public int setDvbsScanServiceType(DvbsServiceType mValue) {
        Log.d(TAG, "Enter setDvbsScanServiceType\n");
        this.mServiceType = mValue;
        Log.d(TAG, "Leave setDvbsScanServiceType\n");
        return 0;
    }

    public DvbsServiceType getDvbsScanServiceTypeEnum() {
        Log.d(TAG, "getDvbsScanServiceType Ocurr\n");
        return this.mServiceType;
    }

    public int getDvbsScanServiceType() {
        Log.d(TAG, "getDvbsScanServiceType Ocurr\n");
        if (DvbsServiceType.DVBS_SCAN_SERVICE_TYPE_ALL == this.mServiceType) {
            return 0;
        }
        if (DvbsServiceType.DVBS_SCAN_SERVICE_TYPE_FREE == this.mServiceType) {
            return 1;
        }
        if (DvbsServiceType.DVBS_SCAN_SERVICE_TYPE_NUM == this.mServiceType) {
            return 2;
        }
        return 0;
    }

    public int setDvbsScanChannelType(DvbsChannelType mValue) {
        Log.d(TAG, "Enter setDvbsScanChannelType\n");
        this.mChannelType = mValue;
        Log.d(TAG, "Leave setDvbsScanChannelType\n");
        return 0;
    }

    public DvbsChannelType getDvbsScanChannelTypeEnum() {
        Log.d(TAG, "getDvbsScanChannelType Ocurr\n");
        return this.mChannelType;
    }

    public int getDvbsScanChannelType() {
        Log.d(TAG, "getDvbsScanChannelType Ocurr\n");
        if (DvbsChannelType.DVBS_SCAN_CHANNEL_TYPE_ALL == this.mChannelType) {
            return 0;
        }
        if (DvbsChannelType.DVBS_SCAN_CHANNEL_TYPE_TV == this.mChannelType) {
            return 1;
        }
        if (DvbsChannelType.DVBS_SCAN_CHANNEL_TYPE_TV_AND_RADIO == this.mChannelType) {
            return 2;
        }
        return 3;
    }

    public int setLnbLowFreq(int mValue) {
        Log.d(TAG, "Enter setLnbLowFreq\n");
        int ret = 0;
        if (mValue < 0 || mValue > 9999) {
            ret = -1;
        } else {
            this.mLnbLowFreq = mValue;
        }
        Log.d(TAG, "Leave setLnbLowFreq\n");
        return ret;
    }

    public int getLnbLowFreq() {
        Log.d(TAG, "getLnbLowFreq Ocurr\n");
        return this.mLnbLowFreq;
    }

    public int setLnbHighFreq(int mValue) {
        Log.d(TAG, "Enter setLnbHighFreq\n");
        int ret = 0;
        if (mValue < 0 || mValue > 99999) {
            ret = -1;
        } else {
            this.mLnbHighFreq = mValue;
        }
        Log.d(TAG, "Leave setLnbHighFreq\n");
        return ret;
    }

    public int getLnbHighFreq() {
        Log.d(TAG, "getLnbHighFreq Ocurr\n");
        return this.mLnbHighFreq;
    }

    public int Set22kType(DvbsSatellite22kType mValue) {
        Log.d(TAG, "Enter setToneBurst\n");
        this.m22k = mValue;
        Log.d(TAG, "Leave setToneBurst\n");
        return 0;
    }

    public int get22kType() {
        Log.d(TAG, "getToneBurst Ocurr\n");
        if (DvbsSatellite22kType.TUNER_DISEQC_22K_OFF == this.m22k) {
            return 0;
        }
        if (DvbsSatellite22kType.TUNER_DISEQC_22K_ON == this.m22k) {
            return 1;
        }
        if (DvbsSatellite22kType.TUNER_DISEQC_22K_AUTO == this.m22k) {
            return 2;
        }
        return 0;
    }

    public DvbsSatellite22kType get22kTypeEnum() {
        Log.d(TAG, "getToneBurst Ocurr\n");
        return this.m22k;
    }

    public int setDisqecCfgUniversal(DisqecCfgUniversal mValue) {
        Log.d(TAG, "Enter setTunerUnicablePosType\n");
        this.mDisqCfg = mValue;
        Log.d(TAG, "Leave setTunerUnicablePosType\n");
        return 0;
    }

    public int getDisqecCfgUniversal() {
        Log.d(TAG, "getTunerUnicablePosType Ocurr\n");
        if (DisqecCfgUniversal.DISCQEC_CFG_DISABLE == this.mDisqCfg) {
            return 0;
        }
        if (DisqecCfgUniversal.DISCQEC_CFG_A == this.mDisqCfg) {
            return 1;
        }
        if (DisqecCfgUniversal.DISCQEC_CFG_B == this.mDisqCfg) {
            return 2;
        }
        if (DisqecCfgUniversal.DISCQEC_CFG_C == this.mDisqCfg) {
            return 3;
        }
        if (DisqecCfgUniversal.DISCQEC_CFG_D == this.mDisqCfg) {
            return 4;
        }
        if (DisqecCfgUniversal.DISCQEC_CFG_TONE_A == this.mDisqCfg) {
            return 5;
        }
        if (DisqecCfgUniversal.DISCQEC_CFG_TONE_B == this.mDisqCfg) {
            return 6;
        }
        return 0;
    }

    public DisqecCfgUniversal getDisqecCfgUniversalEnum() {
        Log.d(TAG, "getDisqecCfgUniversal Ocurr\n");
        return this.mDisqCfg;
    }

    public int setTunerLnbPowerType(TunerLnbPowerType mValue) {
        Log.d(TAG, "Enter setTunerLnbPowerType\n");
        this.mTunerLnbPower = mValue;
        Log.d(TAG, "Leave setTunerLnbPowerType\n");
        return 0;
    }

    public TunerLnbPowerType getTunerLnbPowerTypeEnum() {
        Log.d(TAG, "getTunerLnbPowerType Ocurr\n");
        return this.mTunerLnbPower;
    }

    public int getTunerLnbPowerType() {
        Log.d(TAG, "getTunerLnbPowerType Ocurr\n");
        if (TunerLnbPowerType.LNB_POWER_OFF == this.mTunerLnbPower) {
            return 0;
        }
        if (TunerLnbPowerType.LNB_POWER_13V_18V == this.mTunerLnbPower) {
            return 1;
        }
        if (TunerLnbPowerType.LNB_POWER_14V_19V == this.mTunerLnbPower) {
            return 2;
        }
        if (TunerLnbPowerType.LNB_POWER_ON == this.mTunerLnbPower) {
            return 3;
        }
        return 0;
    }

    public int setTransponderFreq(int mValue) {
        Log.d(TAG, "Enter setTransponderFreq\n");
        int ret = 0;
        if (mValue < 0 || mValue > 99999) {
            ret = -1;
        } else {
            this.mTransponderFreq = mValue;
        }
        Log.d(TAG, "Leave setTransponderFreq\n");
        return ret;
    }

    public int getTransponderFreq() {
        Log.d(TAG, "getTransponderFreq Ocurr\n");
        return this.mTransponderFreq;
    }

    public int setSymbolRate(int mValue) {
        Log.d(TAG, "Enter setSymbolRate\n");
        int ret = 0;
        if (mValue < 0 || mValue > 99999) {
            ret = -1;
        } else {
            this.mSymbolRate = mValue;
        }
        Log.d(TAG, "Leave setSymbolRate\n");
        return ret;
    }

    public int getSymbolRate() {
        Log.d(TAG, "getSymbolRate Ocurr\n");
        return this.mSymbolRate;
    }

    public int SetTunerPolarization(DvbsTunerPolarization mValue) {
        Log.d(TAG, "Enter setToneBurst\n");
        this.mPolarization = mValue;
        Log.d(TAG, "Leave setToneBurst\n");
        return 0;
    }

    public int getTunerPolarization() {
        Log.d(TAG, "getToneBurst Ocurr\n");
        if (DvbsTunerPolarization.POL_UNKNOWN == this.mPolarization) {
            return 0;
        }
        if (DvbsTunerPolarization.POL_LIN_HORIZONTAL == this.mPolarization) {
            return 1;
        }
        if (DvbsTunerPolarization.POL_LIN_VERTICAL == this.mPolarization) {
            return 2;
        }
        if (DvbsTunerPolarization.POL_CIR_LEFT == this.mPolarization) {
            return 3;
        }
        if (DvbsTunerPolarization.POL_CIR_RIGHT == this.mPolarization) {
            return 4;
        }
        return 0;
    }

    public DvbsTunerPolarization getTunerPolarizationEnum() {
        Log.d(TAG, "getTunerPolarizationEnum Ocurr\n");
        return this.mPolarization;
    }
}
