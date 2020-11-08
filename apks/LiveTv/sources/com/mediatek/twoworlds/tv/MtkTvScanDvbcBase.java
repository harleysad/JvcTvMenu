package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvScanDvbcBase {
    private static final int DVBC_EXCHANGE_GET_DEF_FREQ = 200;
    private static final int DVBC_EXCHANGE_GET_DEF_NETWORK_ID = 201;
    private static final int DVBC_EXCHANGE_GET_DEF_QAM = 203;
    private static final int DVBC_EXCHANGE_GET_DEF_SYMBOL_RATE = 202;
    private static final int DVBC_EXCHANGE_GET_NETWORK_ID_NUMBER = 204;
    private static final int DVBC_EXCHANGE_GET_TYPE_SCAN_MAP = 200;
    private static final int DVBC_EXCHANGE_GET_TYPE_UNKNOWN = 0;
    private static final int DVBC_EXCHANGE_HEADER_LEN = 3;
    private static final int DVBC_EXCHANGE_HEADER_LEN_IDX = 1;
    private static final int DVBC_EXCHANGE_HEADER_TYPE_IDX = 2;
    private static final int DVBC_EXCHANGE_MAX_LEN = 50;
    private static final int DVBC_EXCHANGE_PAYLOAD_1ST_IDX = 3;
    private static final int DVBC_EXCHANGE_PAYLOAD_COUNTRY_ID = 3;
    private static final int DVBC_EXCHANGE_PAYLOAD_DATA = 6;
    private static final int DVBC_EXCHANGE_PAYLOAD_DATA_LEN = 5;
    private static final int DVBC_EXCHANGE_PAYLOAD_OPERATOR = 4;
    private static final int DVBC_EXCHANGE_SET_ASU_STATUS = 1001;
    private static final int DVBC_EXCHANGE_SET_TYPE_AUTO_SCAN = 1003;
    private static final int DVBC_EXCHANGE_SET_TYPE_CANCEL_SCAN = 1006;
    private static final int DVBC_EXCHANGE_SET_TYPE_RF_SCAN = 1005;
    private static final int DVBC_EXCHANGE_SET_TYPE_SCAN_PARAS = 1002;
    private static final int DVBC_EXCHANGE_SET_TYPE_UNKNOWN = 1000;
    private static final int DVBC_EXCHANGE_SET_TYPE_UPDATE_SCAN = 1004;
    private static final int DVBC_EXCHANGE_TOTAL_LEN_IDX = 0;
    private static final int DVBC_STR_GET_TYPE_SCANNING_NW_NAME = 0;
    private static final String TAG = "MtkTvScanDvbc";

    public enum ScanDvbcRet {
        SCAN_DVBC_RET_OK,
        SCAN_DVBC_RET_INTERNAL_ERROR
    }

    public enum ScanDvbcCountryId {
        DVBC_COUNRY_NUL(0),
        DVBC_COUNRY_AUT(1),
        DVBC_COUNRY_BEL(2),
        DVBC_COUNRY_BGR(3),
        DVBC_COUNRY_CHE(4),
        DVBC_COUNRY_CZE(5),
        DVBC_COUNRY_DEU(6),
        DVBC_COUNRY_DNK(7),
        DVBC_COUNRY_ESP(8),
        DVBC_COUNRY_EST(9),
        DVBC_COUNRY_FIN(10),
        DVBC_COUNRY_FRA(11),
        DVBC_COUNRY_GBR(12),
        DVBC_COUNRY_GRC(13),
        DVBC_COUNRY_HRV(14),
        DVBC_COUNRY_HUN(15),
        DVBC_COUNRY_IRL(16),
        DVBC_COUNRY_ITA(17),
        DVBC_COUNRY_LUX(18),
        DVBC_COUNRY_LVA(19),
        DVBC_COUNRY_NLD(20),
        DVBC_COUNRY_NOR(21),
        DVBC_COUNRY_POL(22),
        DVBC_COUNRY_PRT(23),
        DVBC_COUNRY_ROU(24),
        DVBC_COUNRY_RUS(25),
        DVBC_COUNRY_SRB(26),
        DVBC_COUNRY_SVK(27),
        DVBC_COUNRY_SVN(28),
        DVBC_COUNRY_SWE(29),
        DVBC_COUNRY_TUR(30),
        DVBC_COUNRY_UKR(31),
        DVBC_COUNRY_COL(32),
        DVBC_COUNRY_AUS(33),
        DVBC_COUNRY_AZE(34),
        DVBC_COUNRY_GHA(35),
        DVBC_COUNRY_IDN(36),
        DVBC_COUNRY_IND(37),
        DVBC_COUNRY_IRN(38),
        DVBC_COUNRY_KEN(39),
        DVBC_COUNRY_MAR(40),
        DVBC_COUNRY_MMR(41),
        DVBC_COUNRY_MYS(42),
        DVBC_COUNRY_NGA(43),
        DVBC_COUNRY_NZL(44),
        DVBC_COUNRY_PHL(45),
        DVBC_COUNRY_SAU(46),
        DVBC_COUNRY_SGP(47),
        DVBC_COUNRY_THA(48),
        DVBC_COUNRY_TUN(49),
        DVBC_COUNRY_TZA(50),
        DVBC_COUNRY_UGA(51),
        DVBC_COUNRY_VNM(52),
        DVBC_COUNRY_XEA(53),
        DVBC_COUNRY_XGC(54),
        DVBC_COUNRY_XIS(55),
        DVBC_COUNRY_XLV(56),
        DVBC_COUNRY_XNA(57),
        DVBC_COUNRY_XWA(58),
        DVBC_COUNRY_ZAF(59);
        
        private int index;

        private ScanDvbcCountryId(int index2) {
            this.index = 0;
            this.index = index2;
        }

        public int idOf() {
            return this.index;
        }
    }

    public enum ScanDvbcOperator {
        DVBC_OPERATOR_NAME_OTHERS(0),
        DVBC_OPERATOR_NAME_UPC(1),
        DVBC_OPERATOR_NAME_COMHEM(2),
        DVBC_OPERATOR_NAME_CANAL_DIGITAL(3),
        DVBC_OPERATOR_NAME_TELE2(4),
        DVBC_OPERATOR_NAME_STOFA(5),
        DVBC_OPERATOR_NAME_YOUSEE(6),
        DVBC_OPERATOR_NAME_ZIGGO(7),
        DVBC_OPERATOR_NAME_UNITYMEDIA(8),
        DVBC_OPERATOR_NAME_NUMERICABLE(9),
        DVBC_OPERATOR_NAME_VOLIA(10),
        DVBC_OPERATOR_NAME_TELEMACH(11),
        DVBC_OPERATOR_NAME_ONLIME(12),
        DVBC_OPERATOR_NAME_AKADO(13),
        DVBC_OPERATOR_NAME_TKT(14),
        DVBC_OPERATOR_NAME_DIVAN_TV(15),
        DVBC_OPERATOR_NAME_NET1(16),
        DVBC_OPERATOR_NAME_KDG(17),
        DVBC_OPERATOR_NAME_KBW(18),
        DVBC_OPERATOR_NAME_BLIZOO(19),
        DVBC_OPERATOR_NAME_TELENET(20),
        DVBC_OPERATOR_NAME_GLENTEN(21),
        DVBC_OPERATOR_NAME_TELECOLUMBUS(22),
        DVBC_OPERATOR_NAME_RCS_RDS(23),
        DVBC_OPERATOR_NAME_VOO(24),
        DVBC_OPERATOR_NAME_KDG_HD(25),
        DVBC_OPERATOR_NAME_KRS(26),
        DVBC_OPERATOR_NAME_TELEING(27),
        DVBC_OPERATOR_NAME_MTS(28),
        DVBC_OPERATOR_NAME_TVOE_STP(29),
        DVBC_OPERATOR_NAME_TVOE_EKA(30),
        DVBC_OPERATOR_NAME_TELEKOM(31);
        
        private int index;

        private ScanDvbcOperator(int index2) {
            this.index = 0;
            this.index = index2;
        }

        public int idOf() {
            return this.index;
        }
    }

    public enum ScanDvbcNitMode {
        DVBC_NIT_SEARCH_MODE_OFF(0),
        DVBC_NIT_SEARCH_MODE_QUICK(1),
        DVBC_NIT_SEARCH_MODE_EX_QUICK(2),
        DVBC_NIT_SEARCH_MODE_NUM(3);
        
        private int index;

        private ScanDvbcNitMode(int index2) {
            this.index = 0;
            this.index = index2;
        }

        public int idOf() {
            return this.index;
        }
    }

    public class MtkTvScanDvbcParameter {
        private static final String TAG = "MtkTvScanDvbcParameter";
        private int mCfgFlag;
        private int mEndFreq;
        private int mModulation;
        private int mNetWorkId;
        private ScanDvbcNitMode mNitMode;
        private int mStartFreq;
        private int mSymRate;

        public MtkTvScanDvbcParameter() {
            this.mNitMode = ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF;
            this.mNetWorkId = -1;
            this.mStartFreq = -1;
            this.mEndFreq = -1;
            this.mCfgFlag = 0;
            this.mModulation = -1;
            this.mSymRate = -1;
            this.mNitMode = ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF;
            this.mNetWorkId = -1;
            this.mStartFreq = -1;
            this.mEndFreq = -1;
            this.mCfgFlag = 0;
            this.mModulation = -1;
            this.mSymRate = -1;
        }

        public int setNetWorkID(int mValue) {
            Log.d(TAG, "Enter setNetWorkID (" + mValue + ")\n");
            if (mValue < 0 || mValue > 65535) {
                this.mNetWorkId = -1;
            } else {
                this.mNetWorkId = mValue;
            }
            Log.d(TAG, "Leave setNetWorkID ret=" + 0 + "\n");
            return 0;
        }

        public int getNetWorkID() {
            Log.d(TAG, "getNetWorkID ret=" + this.mNetWorkId + "\n");
            return this.mNetWorkId;
        }

        public int setNitMode(ScanDvbcNitMode mValue) {
            Log.d(TAG, "Enter setNitMode\n");
            if (mValue == ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF || mValue == ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_QUICK || mValue == ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_EX_QUICK) {
                this.mNitMode = mValue;
            } else {
                this.mNitMode = ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF;
            }
            Log.d(TAG, "Leave setNitMode\n");
            return 0;
        }

        public ScanDvbcNitMode getNitMode() {
            Log.d(TAG, "getNitMode Ocurr\n");
            return this.mNitMode;
        }

        public int setStartFreq(int mValue) {
            Log.d(TAG, "Enter setStartFreq(" + mValue + ")\n");
            if (mValue > 0) {
                this.mStartFreq = mValue;
            } else {
                this.mStartFreq = -1;
            }
            Log.d(TAG, "Leave setScanFreq ret=" + 0 + "\n");
            return 0;
        }

        public int getStartFreq() {
            Log.d(TAG, "getStartFreq Ocurr\n");
            return this.mStartFreq;
        }

        public int setEndFreq(int mValue) {
            Log.d(TAG, "Enter setEndFreq(" + mValue + ")\n");
            if (mValue > 0) {
                this.mEndFreq = mValue;
            } else {
                this.mEndFreq = -1;
            }
            Log.d(TAG, "Leave setEndFreq ret=" + 0 + "\n");
            return 0;
        }

        public int getEndFreq() {
            Log.d(TAG, "getEndFreq Ocurr\n");
            return this.mEndFreq;
        }

        public int setCfgFlag(int mValue) {
            Log.d(TAG, "Enter setCfgFlag(" + mValue + ")\n");
            this.mCfgFlag = mValue;
            Log.d(TAG, "Leave setCfgFlag ret=" + 0 + "\n");
            return 0;
        }

        public int getCfgFlag() {
            Log.d(TAG, "getCfgFlag Ocurr\n");
            return this.mCfgFlag;
        }

        public int setModulation(int mValue) {
            Log.d(TAG, "Enter setModulation(" + mValue + ")\n");
            if (mValue > 0) {
                this.mModulation = mValue;
            } else {
                this.mModulation = -1;
            }
            Log.d(TAG, "Leave setModulation ret=" + 0 + "\n");
            return 0;
        }

        public int getModulation() {
            Log.d(TAG, "getModulation Ocurr\n");
            return this.mModulation;
        }

        public int setSymRate(int mValue) {
            Log.d(TAG, "Enter setSymRate(" + mValue + ")\n");
            if (mValue > 0) {
                this.mSymRate = mValue;
            } else {
                this.mSymRate = -1;
            }
            Log.d(TAG, "Leave setSymRate ret=" + 0 + "\n");
            return 0;
        }

        public int getSymRate() {
            Log.d(TAG, "getSymRate Ocurr\n");
            return this.mSymRate;
        }
    }

    public int getDefaultFrequency(ScanDvbcCountryId countryId, ScanDvbcOperator operator) {
        Log.d(TAG, "Enter getDefaultFrequency");
        int defFreq = getOpOneData(countryId, operator, 200);
        Log.d(TAG, "Exit getDefaultFrequency: " + defFreq + "\n");
        return defFreq;
    }

    public int getDefaultNetworkId(ScanDvbcCountryId countryId, ScanDvbcOperator operator) {
        Log.d(TAG, "Enter getDefaultNetworkId");
        int defNwId = getOpOneData(countryId, operator, 201);
        Log.d(TAG, "Exit getDefaultNetworkId: " + defNwId + "\n");
        return defNwId;
    }

    public int getNetworkIdNumber(ScanDvbcCountryId countryId, ScanDvbcOperator operator) {
        Log.d(TAG, "Enter getNetworkIdNumber");
        int nwIdNumb = getOpOneData(countryId, operator, 204);
        Log.d(TAG, "Exit getNetworkIdNumber: " + nwIdNumb + "\n");
        return nwIdNumb;
    }

    public int getDefaultSymbolRate(ScanDvbcCountryId countryId, ScanDvbcOperator operator) {
        Log.d(TAG, "Enter getDefaultSymbolRate");
        int defSymRate = getOpOneData(countryId, operator, 202);
        Log.d(TAG, "Exit getDefaultSymbolRate: " + defSymRate + "\n");
        return defSymRate;
    }

    public int getDefaultModulation(ScanDvbcCountryId countryId, ScanDvbcOperator operator) {
        Log.d(TAG, "Enter getDefaultModulation");
        int defMod = getOpOneData(countryId, operator, 203);
        Log.d(TAG, "Exit getDefaultModulation: " + defMod + "\n");
        return defMod;
    }

    public String getScanningNwName() {
        Log.d(TAG, "Enter getScanningNwName\n");
        String nwName = TVNativeWrapper.ScanDvbcGetStrData_native(0);
        Log.d(TAG, "Exit getScanningNwName: " + nwName + "\n");
        return nwName;
    }

    public ScanDvbcRet setDvbcScanParas(MtkTvScanDvbcParameter mDvbcScanPara) {
        ScanDvbcCountryId countryId = ScanDvbcCountryId.DVBC_COUNRY_NUL;
        ScanDvbcOperator operator = ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
        Log.d(TAG, "Enter setDvbcScanParas\n");
        ScanDvbcRet scanDvbcRet = exchangeData(1002, new int[]{countryId.idOf(), operator.idOf(), 7, mDvbcScanPara.getNitMode().idOf(), mDvbcScanPara.getNetWorkID(), mDvbcScanPara.getStartFreq(), mDvbcScanPara.getEndFreq(), mDvbcScanPara.getCfgFlag(), mDvbcScanPara.getModulation(), mDvbcScanPara.getSymRate()});
        if (ScanDvbcRet.SCAN_DVBC_RET_OK == scanDvbcRet) {
            Log.d(TAG, "Leave setDvbcScanParas ok");
        } else {
            Log.d(TAG, "Leave setDvbcScanParas fail");
        }
        return scanDvbcRet;
    }

    public ScanDvbcRet startAutoScan() {
        ScanDvbcCountryId countryId = ScanDvbcCountryId.DVBC_COUNRY_NUL;
        ScanDvbcOperator operator = ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
        Log.d(TAG, "Enter startAutoScan");
        ScanDvbcRet scanDvbcRet = setOpOneData(countryId, operator, 1003, 0);
        Log.d(TAG, "Exit startAutoScan");
        return scanDvbcRet;
    }

    public ScanDvbcRet startUpdateScan() {
        ScanDvbcCountryId countryId = ScanDvbcCountryId.DVBC_COUNRY_NUL;
        ScanDvbcOperator operator = ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
        Log.d(TAG, "Enter startUpdateScan");
        ScanDvbcRet scanDvbcRet = setOpOneData(countryId, operator, 1004, 0);
        Log.d(TAG, "Exit startUpdateScan");
        return scanDvbcRet;
    }

    public ScanDvbcRet startRfScan() {
        ScanDvbcCountryId countryId = ScanDvbcCountryId.DVBC_COUNRY_NUL;
        ScanDvbcOperator operator = ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
        Log.d(TAG, "Enter startRfScan");
        ScanDvbcRet scanDvbcRet = setOpOneData(countryId, operator, 1005, 0);
        Log.d(TAG, "Exit startRfScan");
        return scanDvbcRet;
    }

    public ScanDvbcRet cancelScan() {
        ScanDvbcCountryId countryId = ScanDvbcCountryId.DVBC_COUNRY_NUL;
        ScanDvbcOperator operator = ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
        Log.d(TAG, "Enter cancelScan");
        ScanDvbcRet scanDvbcRet = setOpOneData(countryId, operator, 1006, 0);
        Log.d(TAG, "Exit cancelScan");
        return scanDvbcRet;
    }

    private int getOpOneData(ScanDvbcCountryId countryId, ScanDvbcOperator operator, int exchangeType) {
        int[] data = {countryId.idOf(), operator.idOf(), 1, 0};
        if (ScanDvbcRet.SCAN_DVBC_RET_OK == exchangeData(exchangeType, data)) {
            int value = data[3];
            Log.d(TAG, "getOpOneData " + value);
            return value;
        }
        Log.d(TAG, "getOpOneData fail");
        return -1;
    }

    private ScanDvbcRet setOpOneData(ScanDvbcCountryId countryId, ScanDvbcOperator operator, int exchangeType, int value) {
        ScanDvbcRet scanDvbcRet = exchangeData(exchangeType, new int[]{countryId.idOf(), operator.idOf(), 1, value});
        if (ScanDvbcRet.SCAN_DVBC_RET_OK == scanDvbcRet) {
            Log.d(TAG, "setOpOneData ok");
        } else {
            Log.d(TAG, "setOpOneData fail");
        }
        return scanDvbcRet;
    }

    private ScanDvbcRet exchangeData(int exchangeType, int[] data) {
        ScanDvbcRet ret;
        ScanDvbcRet scanDvbcRet = ScanDvbcRet.SCAN_DVBC_RET_INTERNAL_ERROR;
        int[] payload = data;
        Log.d(TAG, "Enter exchangeData\n");
        if (payload == null) {
            payload = new int[]{0};
        }
        int j = 3;
        int totalLen = payload.length + 3;
        if (totalLen > 50) {
            Log.d(TAG, "[Error]bigger than EXCHANGE_MAX_LEN\n");
            return ScanDvbcRet.SCAN_DVBC_RET_INTERNAL_ERROR;
        }
        int[] exData = new int[totalLen];
        exData[0] = totalLen;
        exData[1] = 3;
        exData[2] = exchangeType;
        int i = 0;
        while (true) {
            int j2 = j;
            if (j2 >= totalLen) {
                break;
            }
            exData[j2] = payload[i];
            i++;
            j = j2 + 1;
        }
        Log.d(TAG, "\npayload: \n");
        for (int i2 = 0; i2 < payload.length; i2++) {
            Log.d(TAG, "[" + i2 + "]->" + payload[i2] + "\t");
        }
        Log.d(TAG, "\n");
        Log.d(TAG, "\ninput exData: \n");
        for (int i3 = 0; i3 < totalLen; i3++) {
            Log.d(TAG, "[" + i3 + "]->" + exData[i3] + "\t");
        }
        Log.d(TAG, "\n");
        int retTVNativeWrapper = TVNativeWrapper.ScanDvbcExchangeData_native(exData);
        Log.d(TAG, "\noutput exData: \n");
        for (int i4 = 0; i4 < totalLen; i4++) {
            Log.d(TAG, "[" + i4 + "]->" + exData[i4] + "\t");
        }
        Log.d(TAG, "\n");
        int i5 = 0;
        for (int j3 = 3; j3 < totalLen; j3++) {
            payload[i5] = exData[j3];
            i5++;
        }
        if (retTVNativeWrapper >= 0) {
            ret = ScanDvbcRet.SCAN_DVBC_RET_OK;
        } else {
            ret = ScanDvbcRet.SCAN_DVBC_RET_INTERNAL_ERROR;
        }
        Log.d(TAG, "Exit exchangeData\n");
        return ret;
    }
}
