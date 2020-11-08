package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvScanDvbsBase {
    public static final int DVBS_OPERATOR_NAME_ASTRA_HD_PLUS = 1;
    public static final int DVBS_OPERATOR_NAME_ASTRA_INTERNATIONAL_LCN = 22;
    public static final int DVBS_OPERATOR_NAME_AUSTRIASAT = 3;
    public static final int DVBS_OPERATOR_NAME_CANALDIGITAAL_HD = 4;
    public static final int DVBS_OPERATOR_NAME_CANALDIGITAAL_SD = 5;
    public static final int DVBS_OPERATOR_NAME_CANAL_DIGITAL = 13;
    public static final int DVBS_OPERATOR_NAME_CANAL_DIGITAL_DNK = 101;
    public static final int DVBS_OPERATOR_NAME_CANAL_DIGITAL_FIN = 102;
    public static final int DVBS_OPERATOR_NAME_CANAL_DIGITAL_NOR = 103;
    public static final int DVBS_OPERATOR_NAME_CANAL_DIGITAL_SWE = 104;
    public static final int DVBS_OPERATOR_NAME_CYFRA_PLUS = 18;
    public static final int DVBS_OPERATOR_NAME_CYFROWY_POLSAT = 19;
    public static final int DVBS_OPERATOR_NAME_DEUTSCHLAND = 30;
    public static final int DVBS_OPERATOR_NAME_DIGITURK_EUTELSAT = 16;
    public static final int DVBS_OPERATOR_NAME_DIGITURK_TURKSAT = 15;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV = 29;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_CZE = 114;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_CZE_MOBILE = 116;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_HUN = 112;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_ROU = 111;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_SRB = 113;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_SVK = 115;
    public static final int DVBS_OPERATOR_NAME_DIGI_TV_SVK_MAGIO = 117;
    public static final int DVBS_OPERATOR_NAME_D_SMART = 20;
    public static final int DVBS_OPERATOR_NAME_FRANSAT = 17;
    public static final int DVBS_OPERATOR_NAME_FREENETTV = 37;
    public static final int DVBS_OPERATOR_NAME_FREEVIEW_SAT = 28;
    public static final int DVBS_OPERATOR_NAME_HELLO = 26;
    public static final int DVBS_OPERATOR_NAME_JOYNE_BEL = 39;
    public static final int DVBS_OPERATOR_NAME_JOYNE_NLD = 38;
    public static final int DVBS_OPERATOR_NAME_MTS = 34;
    public static final int DVBS_OPERATOR_NAME_NC_PLUS = 24;
    public static final int DVBS_OPERATOR_NAME_NNK = 14;
    public static final int DVBS_OPERATOR_NAME_NTV_PLUS = 21;
    public static final int DVBS_OPERATOR_NAME_ORS = 10;
    public static final int DVBS_OPERATOR_NAME_OTHERS = 0;
    public static final int DVBS_OPERATOR_NAME_SIMPLITV = 33;
    public static final int DVBS_OPERATOR_NAME_SKY_DEUTSCHLAND = 2;
    public static final int DVBS_OPERATOR_NAME_SMART_HD_PLUS = 23;
    public static final int DVBS_OPERATOR_NAME_SYKLINK_CZ = 8;
    public static final int DVBS_OPERATOR_NAME_SYKLINK_SK = 9;
    public static final int DVBS_OPERATOR_NAME_TELEKARTA = 35;
    public static final int DVBS_OPERATOR_NAME_TELESAT_BELGIUM = 11;
    public static final int DVBS_OPERATOR_NAME_TELESAT_LUXEMBOURG = 12;
    public static final int DVBS_OPERATOR_NAME_TIVIBU = 31;
    public static final int DVBS_OPERATOR_NAME_TIVU_SAT = 25;
    public static final int DVBS_OPERATOR_NAME_TKGS = 27;
    public static final int DVBS_OPERATOR_NAME_TRICOLOR = 32;
    public static final int DVBS_OPERATOR_NAME_TV_VLAANDEREN_HD = 6;
    public static final int DVBS_OPERATOR_NAME_TV_VLAANDEREN_SD = 7;
    public static final int DVBS_OPERATOR_NAME_VALUE_MAX = 255;
    public static final int DVBS_OPERATOR_NAME_WHITE_LABEL_PLATFORM_LCN = 36;
    public static final int DVBS_OPERATOR_STEPS = 100;
    private static final int EXCHANGE_MAX_LEN = 256;
    private static final int EXCHANGE_MAX_PAYLOAD_LEN = 253;
    private static final int EXCHANGE_OTHER_LEN_EXCEPT_PAYLOAD_DATA = 3;
    private static final int EXCHANGE_PAYLOAD_1ST_IDX = 3;
    private static final int EXCHANGE_PAYLOAD_LEN_IDX = 2;
    private static final int EXCHANGE_RET_IDX = 1;
    private static final int EXCHANGE_TYPE_CANCEL_SCAN = 2;
    private static final int EXCHANGE_TYPE_IDX = 0;
    private static final int EXCHANGE_TYPE_M7G_CHANNEL_SEARCH_CANCEL = 6;
    private static final int EXCHANGE_TYPE_M7G_CHANNEL_SEARCH_START = 5;
    private static final int EXCHANGE_TYPE_M7G_LNB_SEARCH_CANCEL = 4;
    private static final int EXCHANGE_TYPE_M7G_LNB_SEARCH_START = 3;
    private static final int EXCHANGE_TYPE_SB_GET = 2000;
    private static final int EXCHANGE_TYPE_SB_GET_BAT_ID = 2010;
    private static final int EXCHANGE_TYPE_SB_GET_CATEGORY_NUM = 2014;
    private static final int EXCHANGE_TYPE_SB_GET_DEFAULT_LNB_CONFIG = 2009;
    private static final int EXCHANGE_TYPE_SB_GET_M7_MAIN_SATELLITE = 2008;
    private static final int EXCHANGE_TYPE_SB_GET_NFY_BAT_INFO = 2004;
    private static final int EXCHANGE_TYPE_SB_GET_NFY_GET_INFO = 2011;
    private static final int EXCHANGE_TYPE_SB_GET_NFY_MDU_DETECT = 2005;
    private static final int EXCHANGE_TYPE_SB_GET_NFY_NEW_SVC = 2003;
    private static final int EXCHANGE_TYPE_SB_GET_NFY_SAT_NAME = 2001;
    private static final int EXCHANGE_TYPE_SB_GET_NFY_SVC_UPDATE = 2002;
    private static final int EXCHANGE_TYPE_SB_GET_NUM_SCANNED_CH = 2012;
    private static final int EXCHANGE_TYPE_SB_GET_SAVE_BGM_INFO = 2007;
    private static final int EXCHANGE_TYPE_SB_GET_TABLE_VERSION = 2013;
    private static final int EXCHANGE_TYPE_SB_GET_UI_SATE_TRANSPONDER = 2006;
    private static final int EXCHANGE_TYPE_SB_SET = 1000;
    private static final int EXCHANGE_TYPE_SB_SET_DISABLE_AUTO_UPDATE = 1018;
    private static final int EXCHANGE_TYPE_SB_SET_SAVE_BGM_INFO = 1003;
    private static final int EXCHANGE_TYPE_SB_SET_TABLE_VERSION = 1019;
    private static final int EXCHANGE_TYPE_SB_SET_UI_22K = 1004;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DEFT_SATE_INFO_BY_OPT = 1001;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_10_DISABLE = 1006;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_10_PORT = 1007;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_10_RESET = 1009;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_10_TONE_BURST = 1008;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_11_DISABLE = 1020;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_11_PORT = 1021;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_DISABLE_LIMITS = 1010;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_GOTO_POS = 1014;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_LIMIT_EAST = 1011;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_LIMIT_WEST = 1012;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_MOVE_EAST = 1015;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_MOVE_WEST = 1016;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_STOP_MOVE = 1017;
    private static final int EXCHANGE_TYPE_SB_SET_UI_DISEQC_12_STORE_POS = 1013;
    private static final int EXCHANGE_TYPE_SB_SET_UI_LNB_POWER = 1005;
    private static final int EXCHANGE_TYPE_SB_SET_UI_LNB_POWER_EX = 1022;
    private static final int EXCHANGE_TYPE_SB_SET_UI_SATE_TRANSPONDER = 1002;
    private static final int EXCHANGE_TYPE_SB_TKGS = 3000;
    private static final int EXCHANGE_TYPE_SB_TKGS_ADD_ONE_VISIBLE_LOCATOR = 3004;
    private static final int EXCHANGE_TYPE_SB_TKGS_CLEAN_ALL_HIDDEN_LOCATORS = 3002;
    private static final int EXCHANGE_TYPE_SB_TKGS_CLEAN_ALL_VISIBLE_LOCATORS = 3011;
    private static final int EXCHANGE_TYPE_SB_TKGS_DEL_ONE_VISIBLE_LOCATOR = 3005;
    private static final int EXCHANGE_TYPE_SB_TKGS_GET_ALL_HIDDEN_LOCATORS = 3001;
    private static final int EXCHANGE_TYPE_SB_TKGS_GET_ALL_SVC_LIST = 3008;
    private static final int EXCHANGE_TYPE_SB_TKGS_GET_ALL_VISIBLE_LOCATORS = 3003;
    private static final int EXCHANGE_TYPE_SB_TKGS_GET_CATEGORY_NUN = 3010;
    private static final int EXCHANGE_TYPE_SB_TKGS_IS_SAME_LOCATOR = 3007;
    private static final int EXCHANGE_TYPE_SB_TKGS_SEL_PREF_SVC_LIST = 3009;
    private static final int EXCHANGE_TYPE_SB_TKGS_UPD_ONE_VISIBLE_LOCATOR = 3006;
    private static final int EXCHANGE_TYPE_SCAN = 0;
    private static final int EXCHANGE_TYPE_START_SCAN = 1;
    private static final int MAX_BAT_NAME_LEN = 32;
    private static final int MAX_NFY_DATA_REC_NAME_LEN = 32;
    private static final int MAX_SAT_NAME_LEN = 25;
    public static final int MAX_SAT_NUM = 4;
    private static final int MAX_SVC_LIST_NAME_LEN = 32;
    public static final int MAX_USER_MESSAGE_LEN = 64;
    public static final int SB_DVBS_CONFIG_BGM_SCAN = 65536;
    public static final int SB_DVBS_CONFIG_INSTALL_FREE_SVC_ONLY = 8;
    public static final int SB_DVBS_CONFIG_INSTALL_RADIO_SVC_ONLY = 32;
    public static final int SB_DVBS_CONFIG_INSTALL_SCRAMBLE_SVC_ONLY = 262144;
    public static final int SB_DVBS_CONFIG_INSTALL_TV_RADIO_SVC_ONLY = 64;
    public static final int SB_DVBS_CONFIG_INSTALL_TV_SVC_ONLY = 16;
    public static final int SB_DVBS_GENERAL_LIST_ID = 3;
    public static final int SB_DVBS_GET_INFO_MASK_TKGS_SAME_VERSION = 1;
    public static final int SB_DVBS_GET_INFO_MASK_TKGS_SVC_LIST = 4;
    public static final int SB_DVBS_GET_INFO_MASK_TKGS_USER_MESSAGE = 2;
    public static final int SB_DVBS_INVALID_RECORD_ID = 0;
    public static final int SB_DVBS_INVALID_VALUE = -1;
    public static final int SB_DVBS_PID_INVALID_VALUE = 8191;
    public static final int SB_DVBS_PREFERRED_LIST_ID = 4;
    public static final int SB_DVBS_TABLE_INVALID_VERSION = 255;
    private static final String TAG = "MtkTvScanDvbs";
    public static final String TVAPI_DVBS_CATEGORY_INFO_BY_IDX = "g_tkgs__category_info_by_idx";
    public static final String TVAPI_DVBS_GRP_CATEGORY_PREFIX = "g_category__";
    public static final String TVAPI_DVBS_GRP_TKGS_PREFIX = "g_tkgs__";
    public static final String TVAPI_DVBS_LINK_STRING = "-";
    public static final String TVAPI_DVBS_NFY_LIST_NAME_BY_IDX = "nfy_list_name_by_idx";
    public static final String TVAPI_DVBS_TKGS_CATEGORY_INFO_BY_IDX = "g_tkgs__category_info_by_idx";
    public static final String TVAPI_DVBS_TKGS_USER_MESSAGE = "g_tkgs__user_message";
    public TKGSOneSvcList[] TKGS_AllSvcLists;
    public int TKGS_PrefSvcListNo;
    public int TKGS_SvclistNum;
    public int TKGS_allHiddenLocators;
    public int TKGS_allVisibleLocators;
    public TKGSOneLocator[] TKGS_hiddenLocatorsList;
    public Boolean TKGS_isSameLocator;
    public TKGSOneLocator[] TKGS_visibleLocatorsList;
    public int category_num = 0;
    public int getBat_id = -1;
    public DvbsM7MainSatelliteIdx getM7MainSat_idx;
    public int getTable_version = 255;
    public OneLNBConfig[] lnbConfig_lnbList;
    public int lnbConfig_lnbNum;
    public int nfyBatInfo_batNum;
    public int nfyGetInfo_appAdded;
    public int nfyGetInfo_appDelete;
    public int nfyGetInfo_lstNfyNum;
    public int nfyGetInfo_mask;
    public int nfyGetInfo_orbitPos;
    public int nfyGetInfo_radioAdded;
    public int nfyGetInfo_radioDelete;
    public int nfyGetInfo_satlRecID;
    public MtkTvScanTpInfo nfyGetInfo_tpInfo = new MtkTvScanTpInfo();
    public int nfyGetInfo_tvAdded;
    public int nfyGetInfo_tvDelete;
    public String nfyGetInfo_usrMessage;
    public TunerMduType nfyMduDetect_mduType;
    public TunerMduType nfySatName_mduType;
    public int nfySatName_orbPos;
    public String nfySatName_satName;
    public int nfySatName_satlRecId;
    public int nfySvcUpd_apAdd;
    public int nfySvcUpd_apDel;
    public int nfySvcUpd_rdAdd;
    public int nfySvcUpd_rdDel;
    public int nfySvcUpd_tvAdd;
    public int nfySvcUpd_tvDel;
    public int numCH_dataScanned;
    public int numCH_radioScanned;
    public int numCH_tvScanned;
    public OneBatData[] nyfBatInfo_batList;
    public OneRECNfyData[] nyfGetInfo_list;
    public int tkgs_category_num = 0;

    public enum DvbsM7MainSatelliteIdx {
        M7_MAIN_SATELLITE_IDX_INVALID,
        M7_MAIN_SATELLITE_IDX_1,
        M7_MAIN_SATELLITE_IDX_2,
        M7_MAIN_SATELLITE_IDX_3,
        M7_MAIN_SATELLITE_IDX_4
    }

    public enum DvbsTableType {
        DVBS_TABLE_TYPE_UNKNOWN,
        DVBS_TABLE_TYPE_TKGS
    }

    public enum SbDvbsScanType {
        SB_DVBS_SCAN_TYPE_UNKNOWN,
        SB_DVBS_SCAN_TYPE_AUTO_MODE,
        SB_DVBS_SCAN_TYPE_PRESET_MODE,
        SB_DVBS_SCAN_TYPE_NETWORK_SCAN,
        SB_DVBS_SCAN_TYPE_SINGLE_TP_SCAN,
        SB_DVBS_SCAN_TYPE_UPDATE_SCAN,
        SB_DVBS_SCAN_TYPE_FREQ_MANUAL_SCAN,
        SB_DVBS_SCAN_TYPE_SAT_CHECK,
        SB_DVBS_SCAN_TYPE_SAT_SEARCH,
        SB_DVBS_SCAN_TYPE_COMPLETE,
        SB_DVBS_SCAN_TYPE_NUM
    }

    public enum ScanDvbsRet {
        SCAN_DVBS_RET_OK,
        SCAN_DVBS_RET_INTERNAL_ERROR,
        SCAN_DVBS_RET_INV_ARG
    }

    public enum TunerLnbPower {
        TUNER_LNB_POWER_OFF,
        TUNER_LNB_POWER_13V_18V,
        TUNER_LNB_POWER_14V_19V,
        TUNER_LNB_POWER_ON
    }

    public enum TunerLnbPowerEx {
        TUNER_LNB_POWER_EX_OFF,
        TUNER_LNB_POWER_EX_13V,
        TUNER_LNB_POWER_EX_18V
    }

    public enum TunerLnbType {
        TUNER_LNB_TYPE_UNKNOWN,
        TUNER_LNB_TYPE_SINGLE_FREQ,
        TUNER_LNB_TYPE_DUAL_FREQ
    }

    public enum TunerMduType {
        TUNER_MDU_TYPE_UNKNOWN,
        TUNER_MDU_TYPE_1,
        TUNER_MDU_TYPE_2,
        TUNER_MDU_TYPE_3,
        TUNER_MDU_TYPE_4,
        TUNER_MDU_TYPE_5
    }

    public enum TunerPolarizationType {
        POL_UNKNOWN(0),
        POL_LIN_HORIZONTAL(1),
        POL_LIN_VERTICAL(2),
        POL_CIR_LEFT(3),
        POL_CIR_RIGHT(4);
        
        private final int value;

        public int getValue() {
            return this.value;
        }

        private TunerPolarizationType(int value2) {
            this.value = value2;
        }
    }

    public class MtkTvScanTpInfo {
        private static final int I4C_TP_INFO_LEN = 3;
        private static final String TAG = "MtkTvScanTpInfo";
        public TunerPolarizationType ePol = TunerPolarizationType.POL_UNKNOWN;
        public int i4Frequency = 0;
        public int i4Symbolrate = 0;

        public MtkTvScanTpInfo() {
        }

        public void dispatchData(int[] data) {
            this.i4Frequency = data[0];
            this.i4Symbolrate = data[1];
            this.ePol = TunerPolarizationType.values()[data[2]];
        }

        public void patchData(int[] data) {
            data[0] = this.i4Frequency;
            data[1] = this.i4Symbolrate;
            data[2] = this.ePol.ordinal();
        }
    }

    public class MtkTvSbDvbsNetworkScanInfo {
        private static final int I4C_DVBS_NETWORK_SCAN_INFO_LEN = 9;
        public static final int SB_DVBS_SCAN_NUM_ASSOCIATED_SATL_REC_ID = 3;
        private static final String TAG = "MtkTvSbDvbsNetworkScanInfo";
        public int[] aAssocSatlRec = new int[3];
        public int i4AssocSatlRecNum = 0;
        public int i4BatID = -1;
        public int i4NetworkID = -1;
        public MtkTvScanTpInfo tpInfo = new MtkTvScanTpInfo();

        public MtkTvSbDvbsNetworkScanInfo() {
        }

        /* access modifiers changed from: private */
        public void dispatchData(int[] data) {
            this.i4NetworkID = data[0];
            this.i4BatID = data[1];
            this.i4AssocSatlRecNum = data[2];
            int i = 0;
            while (i < this.i4AssocSatlRecNum && i < 3) {
                this.aAssocSatlRec[i] = data[3 + i];
                i++;
            }
            int[] exData = new int[3];
            System.arraycopy(data, 6, exData, 0, 3);
            this.tpInfo.dispatchData(exData);
        }

        /* access modifiers changed from: private */
        public void patchData(int[] data) {
            data[0] = this.i4NetworkID;
            data[1] = this.i4BatID;
            data[2] = this.i4AssocSatlRecNum;
            int i = 0;
            while (i < this.i4AssocSatlRecNum && i < 3) {
                data[3 + i] = this.aAssocSatlRec[i];
                i++;
            }
            int[] exData = new int[3];
            this.tpInfo.patchData(exData);
            System.arraycopy(exData, 0, data, 6, 3);
        }
    }

    public class MtkTvSbDvbsSatSearchInfo {
        private static final String TAG = "MtkTvSbDvbsSatSearchInfo";
        public Boolean bIsCustomizedLNB = false;

        public MtkTvSbDvbsSatSearchInfo() {
        }

        /* access modifiers changed from: private */
        public void dispatchData(int[] data) {
            this.bIsCustomizedLNB = Boolean.valueOf("" + data[0]);
        }

        /* access modifiers changed from: private */
        public void patchData(int[] data) {
            data[0] = this.bIsCustomizedLNB.booleanValue();
        }
    }

    public class MtkTvSbDvbsPresetScanInfo {
        private static final int I4C_DVBS_PRESET_SCAN_INFO_LEN = 2;
        private static final String TAG = "MtkTvSbDvbsPresetScanInfo";
        public Boolean bIsNetworkSearch = false;
        public int i4NetworkID = -1;

        public MtkTvSbDvbsPresetScanInfo() {
        }

        /* access modifiers changed from: private */
        public void dispatchData(int[] data) {
            this.bIsNetworkSearch = Boolean.valueOf("" + data[0]);
            this.i4NetworkID = data[1];
        }

        /* access modifiers changed from: private */
        public void patchData(int[] data) {
            data[0] = this.bIsNetworkSearch.booleanValue();
            data[1] = this.i4NetworkID;
        }
    }

    public class MtkTvSbDvbsScanData {
        private static final int I4C_DVBS_SCAN_DATA_LEN = 18;
        private static final String TAG = "MtkTvSbDvbsScanData";
        public Boolean bIsBgm = false;
        public Boolean bIsGetInfoStep = false;
        public Boolean bIsM7PortDetect = false;
        public Boolean bIsMduDetect = false;
        public SbDvbsScanType eSbDvbsScanType = SbDvbsScanType.SB_DVBS_SCAN_TYPE_UNKNOWN;
        public int i4DvbsOperatorName = 0;
        public int i4EngCfgFlag = 0;
        public int i4SatlID = 3;
        public int i4SatlRecID = 0;
        public MtkTvSbDvbsScanInfo scanInfo = new MtkTvSbDvbsScanInfo();

        public MtkTvSbDvbsScanData() {
        }

        public class MtkTvSbDvbsScanInfo {
            private static final int I4C_SCAN_INFO_LEN = 9;
            private static final String TAG = "MtkTvSbDvbsScanInfo";
            /* access modifiers changed from: private */
            public SbDvbsScanType eSbDvbsScanType = SbDvbsScanType.SB_DVBS_SCAN_TYPE_UNKNOWN;
            public MtkTvSbDvbsNetworkScanInfo networkScanInfo = new MtkTvSbDvbsNetworkScanInfo();
            public MtkTvSbDvbsPresetScanInfo presetScanInfo = new MtkTvSbDvbsPresetScanInfo();
            public MtkTvSbDvbsSatSearchInfo satSearchInfo = new MtkTvSbDvbsSatSearchInfo();
            public MtkTvScanTpInfo singleTpScanInfo = new MtkTvScanTpInfo();

            public MtkTvSbDvbsScanInfo() {
            }

            /* access modifiers changed from: private */
            public void dispatchData(int[] data) {
                if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_SINGLE_TP_SCAN) {
                    this.singleTpScanInfo.dispatchData(data);
                } else if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_NETWORK_SCAN) {
                    this.networkScanInfo.dispatchData(data);
                } else if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_PRESET_MODE) {
                    this.presetScanInfo.dispatchData(data);
                } else if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_SAT_SEARCH) {
                    this.satSearchInfo.dispatchData(data);
                }
            }

            /* access modifiers changed from: private */
            public void patchData(int[] data) {
                if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_SINGLE_TP_SCAN) {
                    this.singleTpScanInfo.patchData(data);
                } else if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_NETWORK_SCAN) {
                    this.networkScanInfo.patchData(data);
                } else if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_PRESET_MODE) {
                    this.presetScanInfo.patchData(data);
                } else if (this.eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_SAT_SEARCH) {
                    this.satSearchInfo.patchData(data);
                }
            }
        }

        private void dispatchData(int[] data) {
            this.i4SatlID = data[0];
            this.i4SatlRecID = data[1];
            this.i4DvbsOperatorName = data[2];
            this.eSbDvbsScanType = SbDvbsScanType.values()[data[3]];
            this.i4EngCfgFlag = data[4];
            this.bIsBgm = Boolean.valueOf("" + data[5]);
            this.bIsGetInfoStep = Boolean.valueOf("" + data[6]);
            this.bIsMduDetect = Boolean.valueOf("" + data[7]);
            this.bIsM7PortDetect = Boolean.valueOf("" + data[8]);
            int[] exData = new int[9];
            System.arraycopy(data, 9, exData, 0, 9);
            this.scanInfo.dispatchData(exData);
        }

        /* access modifiers changed from: private */
        public void patchData(int[] data) {
            data[0] = this.i4SatlID;
            data[1] = this.i4SatlRecID;
            data[2] = this.i4DvbsOperatorName;
            data[3] = this.eSbDvbsScanType.ordinal();
            data[4] = this.i4EngCfgFlag;
            data[5] = this.bIsBgm.booleanValue();
            data[6] = this.bIsGetInfoStep.booleanValue();
            data[7] = this.bIsMduDetect.booleanValue();
            data[8] = this.bIsM7PortDetect.booleanValue();
            int[] exData = new int[9];
            SbDvbsScanType unused = this.scanInfo.eSbDvbsScanType = this.eSbDvbsScanType;
            this.scanInfo.patchData(exData);
            System.arraycopy(exData, 0, data, 9, 9);
        }
    }

    public class MtkTvSbDvbsBGMData {
        private static final int I4C_DVBS_BGM_DATA_LEN = 51;
        private static final String TAG = "MtkTvSbDvbsBGMData";
        public OneBGMData[] bgmData_List = new OneBGMData[4];
        public int i4DvbsOperatorName = 0;
        public int i4SatlID = 3;
        public int i4ScanTimes = 0;

        public MtkTvSbDvbsBGMData() {
        }

        public class OneBGMData {
            private static final int I4C_DVBS_ONE_BGM_DATA_LEN = 12;
            public SbDvbsScanType eSbDvbsScanType = SbDvbsScanType.SB_DVBS_SCAN_TYPE_UNKNOWN;
            public int i4EngCfgFlag = 0;
            public int i4SatRecID = 0;
            public MtkTvSbDvbsNetworkScanInfo networkScanInfo = new MtkTvSbDvbsNetworkScanInfo();

            public OneBGMData() {
            }
        }

        /* access modifiers changed from: private */
        public void dispatchData(int[] data) {
            this.i4SatlID = data[0];
            this.i4DvbsOperatorName = data[1];
            this.i4ScanTimes = data[2];
            int i = 0;
            while (i < this.i4ScanTimes && i < 4) {
                this.bgmData_List[i] = new OneBGMData();
                this.bgmData_List[i].eSbDvbsScanType = SbDvbsScanType.values()[data[3 + (i * 12)]];
                this.bgmData_List[i].i4EngCfgFlag = data[4 + (i * 12)];
                this.bgmData_List[i].i4SatRecID = data[5 + (i * 12)];
                if (this.bgmData_List[i].eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_NETWORK_SCAN) {
                    int[] exData = new int[9];
                    System.arraycopy(data, 6 + (i * 12), exData, 0, 9);
                    this.bgmData_List[i].networkScanInfo.dispatchData(exData);
                }
                i++;
            }
        }

        /* access modifiers changed from: private */
        public void patchData(int[] data) {
            data[0] = this.i4SatlID;
            data[1] = this.i4DvbsOperatorName;
            data[2] = this.i4ScanTimes;
            int i = 0;
            while (i < this.i4ScanTimes && i < 4) {
                data[3 + (i * 12)] = this.bgmData_List[i].eSbDvbsScanType.ordinal();
                data[4 + (i * 12)] = this.bgmData_List[i].i4EngCfgFlag;
                data[5 + (i * 12)] = this.bgmData_List[i].i4SatRecID;
                if (this.bgmData_List[i].eSbDvbsScanType == SbDvbsScanType.SB_DVBS_SCAN_TYPE_NETWORK_SCAN) {
                    int[] exData = new int[9];
                    this.bgmData_List[i].networkScanInfo.patchData(exData);
                    System.arraycopy(exData, 0, data, 6 + (i * 12), 9);
                }
                i++;
            }
        }
    }

    public ScanDvbsRet dvbsStartScan(MtkTvSbDvbsScanData sbDvbsScanData) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] scanData = new int[18];
        int[] data = new int[19];
        Log.d(TAG, "Enter dvbsStartScan\n");
        sbDvbsScanData.patchData(scanData);
        data[0] = 18;
        System.arraycopy(scanData, 0, data, 1, 18);
        ScanDvbsRet ret = exchange(1, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsStartScan\n");
        }
        Log.d(TAG, "Leave dvbsStartScan\n");
        return ret;
    }

    public ScanDvbsRet dvbsCancelScan() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsCancelScan\n");
        ScanDvbsRet ret = exchange(2, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsCancelScan\n");
        }
        Log.d(TAG, "Leave dvbsCancelScan\n");
        return ret;
    }

    public ScanDvbsRet dvbsM7LNBSearch() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsM7LNBSearch\n");
        ScanDvbsRet ret = exchange(3, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsM7LNBSearch\n");
        }
        Log.d(TAG, "Leave dvbsM7LNBSearch\n");
        return ret;
    }

    public ScanDvbsRet dvbsM7LNBSearchCancel() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsM7LNBSearchCancel\n");
        ScanDvbsRet ret = exchange(4, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsM7LNBSearchCancel\n");
        }
        Log.d(TAG, "Leave dvbsM7LNBSearchCancel\n");
        return ret;
    }

    public ScanDvbsRet dvbsM7ChannelSearch(int dvbsScanCFGSetting) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsM7ChannelSearch: " + dvbsScanCFGSetting + "\n");
        ScanDvbsRet ret = exchange(5, new int[]{1, dvbsScanCFGSetting});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsM7ChannelSearch\n");
        }
        Log.d(TAG, "Leave dvbsM7ChannelSearch\n");
        return ret;
    }

    public ScanDvbsRet dvbsM7ChannelSearchCancel() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsM7ChannelSearchCancel\n");
        ScanDvbsRet ret = exchange(6, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsM7ChannelSearchCancel\n");
        }
        Log.d(TAG, "Leave dvbsM7ChannelSearchCancel\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetUIDeftSateInfoByOpt(int dvbsOperator) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetUIDeftSateInfoByOpt: " + dvbsOperator + "\n");
        ScanDvbsRet ret = exchange(1001, new int[]{1, dvbsOperator});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetUIDeftSateInfoByOpt\n");
        }
        Log.d(TAG, "Leave dvbsSetUIDeftSateInfoByOpt\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetUISateTransponder(int satlRecId, MtkTvScanTpInfo tpInfo) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[5];
        int[] tpInfoData = new int[3];
        tpInfo.patchData(tpInfoData);
        data[0] = 4;
        data[1] = satlRecId;
        System.arraycopy(tpInfoData, 0, data, 2, 3);
        Log.d(TAG, "Enter dvbsSetUISateTransponder: " + satlRecId + "\n");
        Log.d(TAG, "Enter dvbsSetUISateTransponder: " + tpInfo.i4Frequency + "\n");
        Log.d(TAG, "Enter dvbsSetUISateTransponder: " + tpInfo.i4Symbolrate + "\n");
        Log.d(TAG, "Enter dvbsSetUISateTransponder: " + tpInfo.ePol + "\n");
        ScanDvbsRet ret = exchange(1002, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetUISateTransponder\n");
        }
        Log.d(TAG, "Leave dvbsSetUISateTransponder\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetSaveBgmData(MtkTvSbDvbsBGMData sbDvbsBGMData) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] bgmData = new int[51];
        int[] data = new int[52];
        Log.d(TAG, "Enter dvbsSetSaveBgmData\n");
        sbDvbsBGMData.patchData(bgmData);
        data[0] = 51;
        System.arraycopy(bgmData, 0, data, 1, 51);
        ScanDvbsRet ret = exchange(1003, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetSaveBgmData\n");
        }
        Log.d(TAG, "Leave dvbsSetSaveBgmData\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTuner22k(int k22) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTuner22k: " + k22 + "\n");
        ScanDvbsRet ret = exchange(1004, new int[]{1, k22});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTuner22k\n");
        }
        Log.d(TAG, "Leave dvbsSetTuner22k\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerLnbPower(int lnbPower) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerLnbPower: " + lnbPower + "\n");
        ScanDvbsRet ret = exchange(1005, new int[]{1, lnbPower});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerLnbPower\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerLnbPower\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerLnbPowerEx(int lnbPowerEx) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerLnbPowerEx: " + lnbPowerEx + "\n");
        ScanDvbsRet ret = exchange(1022, new int[]{1, lnbPowerEx});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerLnbPowerEx\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerLnbPowerEx\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc10Disable() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc10Disable\n");
        ScanDvbsRet ret = exchange(1006, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc10Disable\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc10Disable\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc10Port(int portValue) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc10Port: " + portValue + "\n");
        ScanDvbsRet ret = exchange(1007, new int[]{1, portValue});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc10Port\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc10Port\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc11Disable() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc11Disable\n");
        ScanDvbsRet ret = exchange(1020, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc11Disable\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc11Disable\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc11Port(int portValue) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc11Port: " + portValue + "\n");
        ScanDvbsRet ret = exchange(1021, new int[]{1, portValue});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc11Port\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc11Port\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc10ToneBurst(int toneBurst) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc10ToneBurst: " + toneBurst + "\n");
        ScanDvbsRet ret = exchange(1008, new int[]{1, toneBurst});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc10ToneBurst\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc10ToneBurst\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc10Reset() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc10Reset\n");
        ScanDvbsRet ret = exchange(1009, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc10Reset\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc10Reset\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12DisableLimits() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12DisableLimits\n");
        ScanDvbsRet ret = exchange(1010, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12DisableLimits\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12DisableLimits\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12LimitEast() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12LimitEast\n");
        ScanDvbsRet ret = exchange(1011, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12LimitEast\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12LimitEast\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12LimitWest() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12LimitWest\n");
        ScanDvbsRet ret = exchange(1012, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12LimitWest\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12LimitWest\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12StorePos(int storePos) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12StorePos: " + storePos + "\n");
        ScanDvbsRet ret = exchange(1013, new int[]{1, storePos});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12StorePos\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12StorePos\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12GotoPos(int gotoPos) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12GotoPos: " + gotoPos + "\n");
        ScanDvbsRet ret = exchange(1014, new int[]{1, gotoPos});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12GotoPos\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12GotoPos\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12MoveEast(int moveEast) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12MoveEast: " + moveEast + "\n");
        ScanDvbsRet ret = exchange(1015, new int[]{1, moveEast});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12MoveEast\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12MoveEast\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12MoveWest(int moveWest) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12MoveWest: " + moveWest + "\n");
        ScanDvbsRet ret = exchange(1016, new int[]{1, moveWest});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12MoveWest\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12MoveWest\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTunerDiseqc12StopMove() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsSetTunerDiseqc12StopMove\n");
        ScanDvbsRet ret = exchange(1017, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTunerDiseqc12StopMove\n");
        }
        Log.d(TAG, "Leave dvbsSetTunerDiseqc12StopMove\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetNfySatName() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        int[] intSatName = new int[25];
        Log.d(TAG, "Enter dvbsGetNfySatName\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(2001, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetNfySatName\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.nfySatName_satlRecId = data[1];
            Log.d(TAG, "nfySatName_satlRecId:" + this.nfySatName_satlRecId + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (payLoadLen > 0) {
            this.nfySatName_orbPos = data[payloadIdx];
            Log.d(TAG, "nfySatName_orbPos:" + this.nfySatName_orbPos + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfySatName_mduType = TunerMduType.values()[data[payloadIdx]];
            Log.d(TAG, "nfySatName_mduType:" + this.nfySatName_mduType + "\n");
            payloadIdx++;
            payLoadLen += -1;
        }
        if (payLoadLen > 0) {
            System.arraycopy(data, payloadIdx, intSatName, 0, payLoadLen);
            this.nfySatName_satName = convertAsciiArrayToString(intSatName);
            Log.d(TAG, "nfySatName_satName:" + this.nfySatName_satName + "\n");
        }
        Log.d(TAG, "Leave dvbsGetNfySatName\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetNfyMduDetect() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetNfyMduDetect\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_NFY_MDU_DETECT, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetNfyMduDetect\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.nfyMduDetect_mduType = TunerMduType.values()[data[1]];
            Log.d(TAG, "nfyMduDetect_mduType:" + this.nfyMduDetect_mduType + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetNfyMduDetect\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetNfySvcUpd() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetNfySvcUpd\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(2002, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetNfySvcUpd\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.nfySvcUpd_tvAdd = data[1];
            Log.d(TAG, "nfySvcUpd_tvAdd:" + this.nfySvcUpd_tvAdd + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (payLoadLen > 0) {
            this.nfySvcUpd_tvDel = data[payloadIdx];
            Log.d(TAG, "nfySvcUpd_tvDel:" + this.nfySvcUpd_tvDel + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfySvcUpd_rdAdd = data[payloadIdx];
            Log.d(TAG, "nfySvcUpd_rdAdd:" + this.nfySvcUpd_rdAdd + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfySvcUpd_rdDel = data[payloadIdx];
            Log.d(TAG, "nfySvcUpd_rdDel:" + this.nfySvcUpd_rdDel + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfySvcUpd_apAdd = data[payloadIdx];
            Log.d(TAG, "nfySvcUpd_apAdd:" + this.nfySvcUpd_apAdd + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfySvcUpd_apDel = data[payloadIdx];
            Log.d(TAG, "nfySvcUpd_apDel:" + this.nfySvcUpd_apDel + "\n");
            int payLoadLen2 = payLoadLen + -1;
            int i = payloadIdx + 1;
        }
        Log.d(TAG, "Leave dvbsGetNfySvcUpd\n");
        return ret;
    }

    public class OneBatData {
        private static final int I4C_ONE_BAT_DATA_LEN = 33;
        public int batId = -1;
        public String batName = "";

        public OneBatData() {
        }
    }

    public ScanDvbsRet dvbsGetNfyBatInfo() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        int[] intBatName = new int[32];
        Log.d(TAG, "Enter dvbsGetNfyBatInfo\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(2004, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetNfyBatInfo\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.nfyBatInfo_batNum = data[1];
            Log.d(TAG, "nfyBatInfo_batNum:" + this.nfyBatInfo_batNum + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (this.nfyBatInfo_batNum > 0) {
            this.nyfBatInfo_batList = new OneBatData[this.nfyBatInfo_batNum];
            for (int i = 0; i < this.nfyBatInfo_batNum && payLoadLen >= 33; i++) {
                this.nyfBatInfo_batList[i] = new OneBatData();
                int payloadIdx2 = payloadIdx + 1;
                this.nyfBatInfo_batList[i].batId = data[payloadIdx];
                Log.d(TAG, "batId:" + this.nyfBatInfo_batList[i].batId + "\n");
                System.arraycopy(data, payloadIdx2, intBatName, 0, 32);
                this.nyfBatInfo_batList[i].batName = convertAsciiArrayToString(intBatName);
                Log.d(TAG, "batName:" + this.nyfBatInfo_batList[i].batName + "\n");
                payloadIdx = payloadIdx2 + 32;
                payLoadLen += -33;
            }
        }
        Log.d(TAG, "Leave dvbsGetNfyBatInfo\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetUISateTransponder(int satlRecId, MtkTvScanTpInfo tpInfo) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetUISateTransponder: " + satlRecId + "\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        data[1] = satlRecId;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_UI_SATE_TRANSPONDER, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetUISateTransponder\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            tpInfo.i4Frequency = data[1];
            Log.d(TAG, "get Frequency:" + tpInfo.i4Frequency + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (payLoadLen > 0) {
            tpInfo.i4Symbolrate = data[payloadIdx];
            Log.d(TAG, "get Symbolrate:" + tpInfo.i4Symbolrate + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            tpInfo.ePol = TunerPolarizationType.values()[data[payloadIdx]];
            Log.d(TAG, "get Pol:" + tpInfo.ePol + "\n");
            int payloadIdx2 = payloadIdx + 1;
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetUISateTransponder\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetM7MainSatIdx(int i4DvbsOperatorName) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetM7MainSatIdx\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        data[1] = i4DvbsOperatorName;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_M7_MAIN_SATELLITE, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetM7MainSatIdx\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.getM7MainSat_idx = DvbsM7MainSatelliteIdx.values()[data[1]];
            Log.d(TAG, "getM7MainSat_idx:" + this.getM7MainSat_idx + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetM7MainSatIdx\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetSaveBgmData(MtkTvSbDvbsBGMData sbDvbsBGMData) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] bgmData = new int[51];
        int[] data = new int[52];
        Log.d(TAG, "Enter dvbsGetSaveBgmData\n");
        data[0] = 51;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_SAVE_BGM_INFO, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetSaveBgmData\n");
            return ret;
        }
        System.arraycopy(data, 1, bgmData, 0, 51);
        sbDvbsBGMData.dispatchData(bgmData);
        Log.d(TAG, "Leave dvbsGetSaveBgmData\n");
        return ret;
    }

    public class OneLNBConfig {
        private static final int I4C_ONE_LNB_CONFIG_LEN = 4;
        public int highFreq = 0;
        public TunerLnbType lnbType = TunerLnbType.TUNER_LNB_TYPE_UNKNOWN;
        public int lowFreq = 0;
        public int switchFreq = 0;

        public OneLNBConfig() {
        }
    }

    public ScanDvbsRet dvbsGetDefaultLNBConfig() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetDefaultLNBConfig\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_DEFAULT_LNB_CONFIG, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetDefaultLNBConfig\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.lnbConfig_lnbNum = data[1];
            Log.d(TAG, "lnbConfig_lnbNum:" + this.lnbConfig_lnbNum + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (this.lnbConfig_lnbNum > 0) {
            this.lnbConfig_lnbList = new OneLNBConfig[this.lnbConfig_lnbNum];
            int i = 0;
            while (i < this.lnbConfig_lnbNum && payLoadLen >= 4) {
                this.lnbConfig_lnbList[i] = new OneLNBConfig();
                int payloadIdx2 = payloadIdx + 1;
                this.lnbConfig_lnbList[i].lnbType = TunerLnbType.values()[data[payloadIdx]];
                int payloadIdx3 = payloadIdx2 + 1;
                this.lnbConfig_lnbList[i].lowFreq = data[payloadIdx2];
                int payloadIdx4 = payloadIdx3 + 1;
                this.lnbConfig_lnbList[i].highFreq = data[payloadIdx3];
                int payloadIdx5 = payloadIdx4 + 1;
                this.lnbConfig_lnbList[i].switchFreq = data[payloadIdx4];
                Log.d(TAG, "lnbType:" + this.lnbConfig_lnbList[i].lnbType + "\n");
                Log.d(TAG, "lowFreq:" + this.lnbConfig_lnbList[i].lowFreq + "\n");
                Log.d(TAG, "highFreq:" + this.lnbConfig_lnbList[i].highFreq + "\n");
                Log.d(TAG, "switchFreq:" + this.lnbConfig_lnbList[i].switchFreq + "\n");
                payLoadLen += -4;
                i++;
                payloadIdx = payloadIdx5;
            }
        }
        Log.d(TAG, "Leave dvbsGetDefaultLNBConfig\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetBatId(int i4DvbsOperatorName) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetBatId\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        data[1] = i4DvbsOperatorName;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_BAT_ID, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetBatId\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.getBat_id = data[1];
            Log.d(TAG, "getBat_id:" + this.getBat_id + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetBatId\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetNumScannedCH() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetNumScannedCH\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_NUM_SCANNED_CH, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetNumScannedCH\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.numCH_tvScanned = data[1];
            Log.d(TAG, "numCH_tvScanned:" + this.numCH_tvScanned + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (payLoadLen > 0) {
            this.numCH_radioScanned = data[payloadIdx];
            Log.d(TAG, "numCH_radioScanned:" + this.numCH_radioScanned + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.numCH_dataScanned = data[payloadIdx];
            Log.d(TAG, "numCH_dataScanned:" + this.numCH_dataScanned + "\n");
            int payLoadLen2 = payLoadLen + -1;
            int i = payloadIdx + 1;
        }
        Log.d(TAG, "Leave dvbsGetNumScannedCH\n");
        return ret;
    }

    public class OneRECNfyData {
        private static final int I4C_ONE_REC_NFY_DATA_LEN = 33;
        public int recId = -1;
        public String recName = "";

        public OneRECNfyData() {
        }
    }

    public ScanDvbsRet dvbsGetNfyGetInfo() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        int[] exData = new int[3];
        int[] iArr = new int[64];
        int[] intRecName = new int[32];
        Log.d(TAG, "Enter dvbsGetNfyGetInfo\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_NFY_GET_INFO, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetNfyGetInfo\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.nfyGetInfo_satlRecID = data[1];
            Log.d(TAG, "nfyGetInfo_satlRecID:" + this.nfyGetInfo_satlRecID + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_orbitPos = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_orbitPos:" + this.nfyGetInfo_orbitPos + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen >= 3) {
            System.arraycopy(data, payloadIdx, exData, 0, 3);
            this.nfyGetInfo_tpInfo.dispatchData(exData);
            Log.d(TAG, "nfyGetInfo_tpInfo_freq:" + this.nfyGetInfo_tpInfo.i4Frequency + "\n");
            Log.d(TAG, "nfyGetInfo_tpInfo_Pol:" + this.nfyGetInfo_tpInfo.ePol + "\n");
            Log.d(TAG, "nfyGetInfo_tpInfo_SymbolRate:" + this.nfyGetInfo_tpInfo.i4Symbolrate + "\n");
            payloadIdx += 3;
            payLoadLen += -3;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_mask = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_mask:" + this.nfyGetInfo_mask + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen >= 64) {
            this.nfyGetInfo_usrMessage = getUserMessage(TVAPI_DVBS_TKGS_USER_MESSAGE);
            Log.d(TAG, "nfyGetInfo_usrMessage:" + this.nfyGetInfo_usrMessage + "\n");
            payloadIdx += 64;
            payLoadLen += -64;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_tvAdded = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_tvAdded:" + this.nfyGetInfo_tvAdded + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_tvDelete = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_tvDelete:" + this.nfyGetInfo_tvDelete + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_radioAdded = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_radioAdded:" + this.nfyGetInfo_radioAdded + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_radioDelete = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_radioDelete:" + this.nfyGetInfo_radioDelete + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_appAdded = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_appAdded:" + this.nfyGetInfo_appAdded + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_appDelete = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_appDelete:" + this.nfyGetInfo_appDelete + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (payLoadLen > 0) {
            this.nfyGetInfo_lstNfyNum = data[payloadIdx];
            Log.d(TAG, "nfyGetInfo_lstNfyNum:" + this.nfyGetInfo_lstNfyNum + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (this.nfyGetInfo_lstNfyNum > 0) {
            this.nyfGetInfo_list = new OneRECNfyData[this.nfyGetInfo_lstNfyNum];
            for (int i = 0; i < this.nfyGetInfo_lstNfyNum && payLoadLen >= 33; i++) {
                this.nyfGetInfo_list[i] = new OneRECNfyData();
                int payloadIdx2 = payloadIdx + 1;
                this.nyfGetInfo_list[i].recId = data[payloadIdx];
                Log.d(TAG, "recId:" + this.nyfGetInfo_list[i].recId + "\n");
                System.arraycopy(data, payloadIdx2, intRecName, 0, 32);
                this.nyfGetInfo_list[i].recName = convertAsciiArrayToString(intRecName);
                Log.d(TAG, "recName:" + this.nyfGetInfo_list[i].recName + "\n");
                this.nyfGetInfo_list[i].recName = getNfyInfo_recName_ByIdx(i);
                Log.d(TAG, "recName-by userMessage:" + this.nyfGetInfo_list[i].recName + "\n");
                payloadIdx = payloadIdx2 + 32;
                payLoadLen += -33;
            }
        }
        Log.d(TAG, "Leave dvbsGetNfyGetInfo\n");
        return ret;
    }

    public String dvbsGetTKGSUserMessage() {
        Log.d(TAG, "Enter dvbsGetTKGSUserMessage\n");
        String userMessage = getUserMessage(TVAPI_DVBS_TKGS_USER_MESSAGE);
        Log.d(TAG, "Leave dvbsGetTKGSUserMessage:" + userMessage + "\n");
        return userMessage;
    }

    public ScanDvbsRet dvbsGetTKGSCagegoryNum() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetTKGSCagegoryNum\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_GET_CATEGORY_NUN, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetTKGSCagegoryNum\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.tkgs_category_num = data[1];
            Log.d(TAG, "tkgs_category_num:" + this.tkgs_category_num + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetTKGSCagegoryNum\n");
        return ret;
    }

    public String dvbsGetTKGSCategoryInfoByIdx(int categoryIdx) {
        Log.d(TAG, "Enter dvbsGetTKGSCategoryInfoByIdx:" + categoryIdx + "\n");
        String str_categoryIdx = "g_tkgs__category_info_by_idx" + TVAPI_DVBS_LINK_STRING + Integer.toString(categoryIdx);
        Log.d(TAG, "str_categoryIdx:" + str_categoryIdx + "\n");
        String str_categoryName = getUserMessage(str_categoryIdx);
        Log.d(TAG, "Leave dvbsGetTKGSCategoryInfoByIdx:" + str_categoryName + "\n");
        return str_categoryName;
    }

    public ScanDvbsRet dvbsGetTableVersion(DvbsTableType tableType) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetTableVersion:" + tableType + "\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        data[1] = tableType.ordinal();
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_TABLE_VERSION, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetTableVersion\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.getTable_version = data[1];
            Log.d(TAG, "getTable_version:" + this.getTable_version + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetTableVersion\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetTableVersion(DvbsTableType tableType, int tableVersion) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = {2, tableType.ordinal(), tableVersion};
        Log.d(TAG, "Enter dvbsSetTableVersion: table type" + tableType + "table ver" + tableVersion + "\n");
        ScanDvbsRet ret = exchange(1019, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetTableVersion\n");
        }
        Log.d(TAG, "Leave dvbsSetTableVersion\n");
        return ret;
    }

    private ScanDvbsRet dvbsSetDisableAutoUpdateForCrnt(Boolean isDisAutoUpd) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = {1, isDisAutoUpd.booleanValue()};
        Log.d(TAG, "Enter dvbsSetDisableAutoUpdateForCrnt, isDisAutoUpd=" + isDisAutoUpd + "\n");
        ScanDvbsRet ret = exchange(1018, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsSetDisableAutoUpdateForCrnt\n");
        }
        Log.d(TAG, "Leave dvbsSetDisableAutoUpdateForCrnt\n");
        return ret;
    }

    public ScanDvbsRet dvbsSetDisableAutoUpdateForCrnt() {
        return dvbsSetDisableAutoUpdateForCrnt(true);
    }

    public ScanDvbsRet dvbsSetEnableAutoUpdateForCrnt() {
        return dvbsSetDisableAutoUpdateForCrnt(false);
    }

    public class TKGSOneLocator {
        private static final int I4C_TKGS_ONE_LOCATOR_LEN = 5;
        public int PID;
        public int recordID;
        public MtkTvScanTpInfo tpInfo;

        public TKGSOneLocator() {
            this.tpInfo = new MtkTvScanTpInfo();
            this.recordID = 0;
            this.PID = 8191;
        }

        public TKGSOneLocator(int pid, int freq, int symrate, int pol) {
            this.tpInfo = new MtkTvScanTpInfo();
            this.PID = pid;
            this.tpInfo.i4Frequency = freq;
            this.tpInfo.i4Symbolrate = symrate;
            this.tpInfo.ePol = TunerPolarizationType.values()[pol];
        }

        /* access modifiers changed from: private */
        public void dispatchData(int[] data) {
            this.recordID = data[0];
            this.PID = data[1];
            int[] exData = new int[3];
            System.arraycopy(data, 2, exData, 0, 3);
            this.tpInfo.dispatchData(exData);
        }

        /* access modifiers changed from: private */
        public void patchData(int[] data) {
            data[0] = this.recordID;
            data[1] = this.PID;
            int[] exData = new int[3];
            this.tpInfo.patchData(exData);
            System.arraycopy(exData, 0, data, 2, 3);
        }
    }

    public ScanDvbsRet dvbsTKGSGetAllHiddenLocators() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        int[] exData = new int[5];
        Log.d(TAG, "Enter dvbsTKGSGetAllHiddenLocators\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_GET_ALL_HIDDEN_LOCATORS, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSGetAllHiddenLocators\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.TKGS_allHiddenLocators = data[1];
            Log.d(TAG, "TKGS_allHiddenLocators:" + this.TKGS_allHiddenLocators + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (this.TKGS_allHiddenLocators > 0) {
            this.TKGS_hiddenLocatorsList = new TKGSOneLocator[this.TKGS_allHiddenLocators];
            for (int i = 0; i < this.TKGS_allHiddenLocators && payLoadLen >= 5; i++) {
                this.TKGS_hiddenLocatorsList[i] = new TKGSOneLocator();
                System.arraycopy(data, payloadIdx, exData, 0, 5);
                this.TKGS_hiddenLocatorsList[i].dispatchData(exData);
                Log.d(TAG, "recordID=" + this.TKGS_hiddenLocatorsList[i].recordID + "PID=" + this.TKGS_hiddenLocatorsList[i].PID + "\n");
                Log.d(TAG, "tpinfo:freq=" + this.TKGS_hiddenLocatorsList[i].tpInfo.i4Frequency + "pol=" + this.TKGS_hiddenLocatorsList[i].tpInfo.ePol + "symbol=" + this.TKGS_hiddenLocatorsList[i].tpInfo.i4Symbolrate + "\n");
                payloadIdx += 5;
                payLoadLen += -5;
            }
        }
        Log.d(TAG, "Leave dvbsTKGSGetAllHiddenLocators\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSCleanAllHiddenLocators() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsTKGSCleanAllHiddenLocators\n");
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_CLEAN_ALL_HIDDEN_LOCATORS, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSCleanAllHiddenLocators\n");
        }
        Log.d(TAG, "Leave dvbsTKGSCleanAllHiddenLocators\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSGetAllVisibleLocators() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        int[] exData = new int[5];
        Log.d(TAG, "Enter dvbsTKGSGetAllVisibleLocators\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_GET_ALL_VISIBLE_LOCATORS, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSGetAllVisibleLocators\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.TKGS_allVisibleLocators = data[1];
            Log.d(TAG, "TKGS_allVisibleLocators:" + this.TKGS_allVisibleLocators + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (this.TKGS_allVisibleLocators > 0) {
            this.TKGS_visibleLocatorsList = new TKGSOneLocator[this.TKGS_allVisibleLocators];
            for (int i = 0; i < this.TKGS_allVisibleLocators && payLoadLen >= 5; i++) {
                this.TKGS_visibleLocatorsList[i] = new TKGSOneLocator();
                System.arraycopy(data, payloadIdx, exData, 0, 5);
                this.TKGS_visibleLocatorsList[i].dispatchData(exData);
                Log.d(TAG, "recordID=" + this.TKGS_visibleLocatorsList[i].recordID + "PID=" + this.TKGS_visibleLocatorsList[i].PID + "\n");
                Log.d(TAG, "tpinfo:freq=" + this.TKGS_visibleLocatorsList[i].tpInfo.i4Frequency + "pol=" + this.TKGS_visibleLocatorsList[i].tpInfo.ePol + "symbol=" + this.TKGS_visibleLocatorsList[i].tpInfo.i4Symbolrate + "\n");
                payloadIdx += 5;
                payLoadLen += -5;
            }
        }
        Log.d(TAG, "Leave dvbsTKGSGetAllVisibleLocators\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSAddOneVisibleLocator(TKGSOneLocator oneLocator) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] oneLocatorData = new int[5];
        int[] data = new int[6];
        Log.d(TAG, "Enter dvbsTKGSAddOneVisibleLocator\n");
        Log.d(TAG, "Before add:recordID=" + oneLocator.recordID + "PID=" + oneLocator.PID + "\n");
        Log.d(TAG, "tpinfo:freq=" + oneLocator.tpInfo.i4Frequency + "pol=" + oneLocator.tpInfo.ePol + "symbol=" + oneLocator.tpInfo.i4Symbolrate + "\n");
        data[0] = 5;
        oneLocator.patchData(oneLocatorData);
        System.arraycopy(oneLocatorData, 0, data, 1, 5);
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_ADD_ONE_VISIBLE_LOCATOR, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSAddOneVisibleLocator\n");
            return ret;
        }
        if (data[0] >= 5) {
            System.arraycopy(data, 1, oneLocatorData, 0, 5);
            oneLocator.dispatchData(oneLocatorData);
            Log.d(TAG, "After add:recordID=" + oneLocator.recordID + "PID=" + oneLocator.PID + "\n");
            Log.d(TAG, "tpinfo:freq=" + oneLocator.tpInfo.i4Frequency + "pol=" + oneLocator.tpInfo.ePol + "symbol=" + oneLocator.tpInfo.i4Symbolrate + "\n");
        }
        Log.d(TAG, "Leave dvbsTKGSAddOneVisibleLocator\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSDelOneVisibleLocator(int recordID) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsTKGSDelOneVisibleLocator, recordID=" + recordID + "\n");
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_DEL_ONE_VISIBLE_LOCATOR, new int[]{1, recordID});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSDelOneVisibleLocator\n");
            return ret;
        }
        Log.d(TAG, "Leave dvbsTKGSDelOneVisibleLocator\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSUpdOneVisibleLocator(TKGSOneLocator oneLocator) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] oneLocatorData = new int[5];
        int[] data = new int[6];
        Log.d(TAG, "Enter dvbsTKGSUpdOneVisibleLocator\n");
        Log.d(TAG, "Before upd:recordID=" + oneLocator.recordID + "PID=" + oneLocator.PID + "\n");
        Log.d(TAG, "tpinfo:freq=" + oneLocator.tpInfo.i4Frequency + "pol=" + oneLocator.tpInfo.ePol + "symbol=" + oneLocator.tpInfo.i4Symbolrate + "\n");
        if (oneLocator.recordID == 0) {
            Log.d(TAG, "Error dvbsTKGSUpdOneVisibleLocator, invalid record id\n");
            return ScanDvbsRet.SCAN_DVBS_RET_INV_ARG;
        }
        data[0] = 5;
        oneLocator.patchData(oneLocatorData);
        System.arraycopy(oneLocatorData, 0, data, 1, 5);
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_UPD_ONE_VISIBLE_LOCATOR, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSUpdOneVisibleLocator\n");
            return ret;
        }
        Log.d(TAG, "Leave dvbsTKGSUpdOneVisibleLocator\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSIsSameVisibleLocator(TKGSOneLocator oneLocator, TKGSOneLocator otherLocator) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] oneLocatorData = new int[5];
        int[] otherLocatorData = new int[5];
        int[] data = new int[11];
        Log.d(TAG, "Enter dvbsTKGSIsSameVisibleLocator\n");
        Log.d(TAG, "One is:recordID=" + oneLocator.recordID + "PID=" + oneLocator.PID + "\n");
        Log.d(TAG, "tpinfo:freq=" + oneLocator.tpInfo.i4Frequency + "pol=" + oneLocator.tpInfo.ePol + "symbol=" + oneLocator.tpInfo.i4Symbolrate + "\n");
        StringBuilder sb = new StringBuilder();
        sb.append("Other is:recordID=");
        sb.append(otherLocator.recordID);
        sb.append("PID=");
        sb.append(otherLocator.PID);
        sb.append("\n");
        Log.d(TAG, sb.toString());
        Log.d(TAG, "tpinfo:freq=" + otherLocator.tpInfo.i4Frequency + "pol=" + otherLocator.tpInfo.ePol + "symbol=" + otherLocator.tpInfo.i4Symbolrate + "\n");
        boolean z = false;
        data[0] = 10;
        oneLocator.patchData(oneLocatorData);
        System.arraycopy(oneLocatorData, 0, data, 1, 5);
        otherLocator.patchData(otherLocatorData);
        System.arraycopy(otherLocatorData, 0, data, 6, 5);
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_IS_SAME_LOCATOR, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSIsSameVisibleLocator\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            if (data[1] != 0) {
                z = true;
            }
            this.TKGS_isSameLocator = Boolean.valueOf(z);
            Log.d(TAG, "TKGS_isSameLocator:" + this.TKGS_isSameLocator + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsTKGSIsSameVisibleLocator\n");
        return ret;
    }

    public class TKGSOneSvcList {
        private static final int I4C_ONE_SVC_LIST_LEN = 33;
        public String svcListName = "";
        public int svcListNo = -1;

        public TKGSOneSvcList() {
        }
    }

    public ScanDvbsRet dvbsTKGSGetAllSvcList() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        int[] intSvcListName = new int[32];
        Log.d(TAG, "Enter dvbsTKGSGetAllSvcList\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_GET_ALL_SVC_LIST, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSGetAllSvcList\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.TKGS_SvclistNum = data[1];
            Log.d(TAG, "TKGS_SvclistNum:" + this.TKGS_SvclistNum + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (payLoadLen > 0) {
            this.TKGS_PrefSvcListNo = data[payloadIdx];
            Log.d(TAG, "TKGS_PrefSvcListNo:" + this.TKGS_PrefSvcListNo + "\n");
            payLoadLen += -1;
            payloadIdx++;
        }
        if (this.TKGS_SvclistNum > 0) {
            this.TKGS_AllSvcLists = new TKGSOneSvcList[this.TKGS_SvclistNum];
            for (int i = 0; i < this.TKGS_SvclistNum && payLoadLen >= 33; i++) {
                this.TKGS_AllSvcLists[i] = new TKGSOneSvcList();
                int payloadIdx2 = payloadIdx + 1;
                this.TKGS_AllSvcLists[i].svcListNo = data[payloadIdx];
                Log.d(TAG, "svcListNo:" + this.TKGS_AllSvcLists[i].svcListNo + "\n");
                System.arraycopy(data, payloadIdx2, intSvcListName, 0, 32);
                this.TKGS_AllSvcLists[i].svcListName = convertAsciiArrayToString(intSvcListName);
                Log.d(TAG, "svcListName:" + this.TKGS_AllSvcLists[i].svcListName + "\n");
                payloadIdx = payloadIdx2 + 32;
                payLoadLen += -33;
            }
        }
        Log.d(TAG, "Leave dvbsTKGSGetAllSvcList\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSSelSvcList(int prefSvcListNo) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsTKGSSelSvcList: prefSvcListNo:" + prefSvcListNo + "\n");
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_SEL_PREF_SVC_LIST, new int[]{1, prefSvcListNo});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSSelSvcList\n");
        }
        Log.d(TAG, "Leave dvbsTKGSSelSvcList\n");
        return ret;
    }

    public ScanDvbsRet dvbsTKGSCleanAllVisibleLocators() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        Log.d(TAG, "Enter dvbsTKGSCleanAllVisibleLocators\n");
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_TKGS_CLEAN_ALL_VISIBLE_LOCATORS, new int[]{0});
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsTKGSCleanAllVisibleLocators\n");
        }
        Log.d(TAG, "Leave dvbsTKGSCleanAllVisibleLocators\n");
        return ret;
    }

    public ScanDvbsRet dvbsGetCategoryNum() {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int[] data = new int[254];
        Log.d(TAG, "Enter dvbsGetCategoryNum\n");
        data[0] = EXCHANGE_MAX_PAYLOAD_LEN;
        ScanDvbsRet ret = exchange(EXCHANGE_TYPE_SB_GET_CATEGORY_NUM, data);
        if (ScanDvbsRet.SCAN_DVBS_RET_OK != ret) {
            Log.d(TAG, "Error dvbsGetCategoryNum\n");
            return ret;
        }
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.category_num = data[1];
            Log.d(TAG, "category_num:" + this.category_num + "\n");
            int payLoadLen2 = payLoadLen + -1;
        }
        Log.d(TAG, "Leave dvbsGetCategoryNum\n");
        return ret;
    }

    public String dvbsGetCategoryInfoByIdx(int categoryIdx) {
        Log.d(TAG, "Enter dvbsGetCategoryInfoByIdx:" + categoryIdx + "\n");
        String str_categoryIdx = "g_tkgs__category_info_by_idx" + TVAPI_DVBS_LINK_STRING + Integer.toString(categoryIdx);
        Log.d(TAG, "str_categoryIdx:" + str_categoryIdx + "\n");
        String str_categoryName = getUserMessage(str_categoryIdx);
        Log.d(TAG, "Leave dvbsGetCategoryInfoByIdx:" + str_categoryName + "\n");
        return str_categoryName;
    }

    private ScanDvbsRet exchange(int exchangeType, int[] data) {
        ScanDvbsRet scanDvbsRet = ScanDvbsRet.SCAN_DVBS_RET_OK;
        int mwRet = -1;
        int[] payload = data;
        int totalLen = 3;
        if (payload.length > 0) {
            totalLen = 3 + (payload.length - 1);
        }
        int totalLen2 = totalLen;
        Log.d(TAG, "totalLen: " + totalLen2 + "\n");
        if (totalLen2 > 256) {
            Log.d(TAG, "[Error]bigger than EXCHANGE_MAX_LEN\n");
            return ScanDvbsRet.SCAN_DVBS_RET_INTERNAL_ERROR;
        }
        int[] exchangeData = new int[totalLen2];
        exchangeData[0] = exchangeType;
        exchangeData[1] = -1;
        int j = 2;
        int i = 0;
        int j2 = 2;
        while (i < payload.length) {
            exchangeData[j2] = payload[i];
            i++;
            j2++;
        }
        int retTVNativeWrapper = TVNativeWrapper.ScanDvbsExchangeData_native(exchangeData);
        if (retTVNativeWrapper >= 0) {
            int mwExchangeType = exchangeData[0];
            mwRet = exchangeData[1];
            if (mwRet >= 0 && mwExchangeType == exchangeType) {
                int i2 = 0;
                while (true) {
                    int j3 = j;
                    if (i2 >= payload.length) {
                        break;
                    }
                    payload[i2] = exchangeData[j3];
                    i2++;
                    j = j3 + 1;
                }
            }
        }
        if (retTVNativeWrapper < 0 || mwRet < 0) {
            return ScanDvbsRet.SCAN_DVBS_RET_INTERNAL_ERROR;
        }
        return ScanDvbsRet.SCAN_DVBS_RET_OK;
    }

    private String getUserMessage(String strId) {
        Log.d(TAG, "Enter getUserMessage:" + strId + "\n");
        String relValue = TVNativeWrapper.ScanDvbsgetUserMessage_native(strId);
        StringBuilder sb = new StringBuilder();
        sb.append("Leave getUserMessage, return value = ");
        sb.append(relValue == null ? "null" : relValue);
        sb.append("\n");
        Log.d(TAG, sb.toString());
        return relValue;
    }

    private String convertAsciiArrayToString(int[] asciiArray) {
        StringBuilder stringBuilder = new StringBuilder();
        if (asciiArray == null) {
            return null;
        }
        int i = 0;
        while (i < asciiArray.length && asciiArray[i] != 0) {
            stringBuilder.append((char) asciiArray[i]);
            i++;
        }
        return stringBuilder.toString();
    }

    private String getNfyInfo_recName_ByIdx(int recIdx) {
        Log.d(TAG, "Enter getNfyInfo_recName_ByIdx:" + recIdx + "\n");
        String str_recIdx = TVAPI_DVBS_NFY_LIST_NAME_BY_IDX + TVAPI_DVBS_LINK_STRING + Integer.toString(recIdx);
        Log.d(TAG, "str_recIdx:" + str_recIdx + "\n");
        String str_recName = getUserMessage(str_recIdx);
        Log.d(TAG, "Leave getNfyInfo_recName_ByIdx:" + str_recName + "\n");
        return str_recName;
    }
}
