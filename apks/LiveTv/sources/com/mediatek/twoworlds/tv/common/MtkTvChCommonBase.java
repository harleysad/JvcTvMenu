package com.mediatek.twoworlds.tv.common;

import android.util.Log;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.TVNativeWrapper;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class MtkTvChCommonBase {
    public static final int AUDIO_SYS_AM = 1;
    public static final int AUDIO_SYS_BTSC = 256;
    public static final int AUDIO_SYS_FM_A2 = 8;
    public static final int AUDIO_SYS_FM_A2_DK1 = 16;
    public static final int AUDIO_SYS_FM_A2_DK2 = 32;
    public static final int AUDIO_SYS_FM_EIA_J = 4;
    public static final int AUDIO_SYS_FM_MONO = 2;
    public static final int AUDIO_SYS_FM_RADIO = 64;
    public static final int AUDIO_SYS_NICAM = 128;
    public static final int AUDIO_SYS_UNKNOWN = 0;
    public static final int BRDCST_MEDIUM_1394 = 7;
    public static final int BRDCST_MEDIUM_ANA_CABLE = 5;
    public static final int BRDCST_MEDIUM_ANA_SATELLITE = 6;
    public static final int BRDCST_MEDIUM_ANA_TERRESTRIAL = 4;
    public static final int BRDCST_MEDIUM_DIG_CABLE = 2;
    public static final int BRDCST_MEDIUM_DIG_SATELLITE = 3;
    public static final int BRDCST_MEDIUM_DIG_TERRESTRIAL = 1;
    public static final int BRDCST_MEDIUM_UNKNOWN = 0;
    public static final int BRDCST_TYPE_ANALOG = 1;
    public static final int BRDCST_TYPE_ATSC = 3;
    public static final int BRDCST_TYPE_DTMB = 7;
    public static final int BRDCST_TYPE_DVB = 2;
    public static final int BRDCST_TYPE_FMRDO = 6;
    public static final int BRDCST_TYPE_ISDB = 5;
    public static final int BRDCST_TYPE_MHP = 8;
    public static final int BRDCST_TYPE_SCTE = 4;
    public static final int BRDCST_TYPE_UNKNOWN = 0;
    public static final int CHANNEL_TYPE_ANALOG = 1;
    public static final int CHANNEL_TYPE_ATSC = 3;
    public static final int CHANNEL_TYPE_DIGITAL = 2;
    public static final int CHANNEL_TYPE_ISDB = 5;
    public static final int CHANNEL_TYPE_SCTE = 4;
    public static final int CH_CMM_RET_FAIL = -1;
    public static final int CH_CMM_RET_OK = 0;
    public static final boolean CI_PLUS_SUPPORT = true;
    public static final int COLOR_SYS_NTSC = 0;
    public static final int COLOR_SYS_NTSC_443 = 3;
    public static final int COLOR_SYS_PAL = 1;
    public static final int COLOR_SYS_PAL_60 = 6;
    public static final int COLOR_SYS_PAL_M = 4;
    public static final int COLOR_SYS_PAL_N = 5;
    public static final int COLOR_SYS_SECAM = 2;
    public static final int COLOR_SYS_UNKNOWN = -1;
    public static final int COND_SVL_CLOSED = 3;
    public static final int COND_SVL_UNKNOWN = 4;
    public static final int COND_SVL_UPDATED = 2;
    public static final int COND_SVL_UPDATING = 1;
    public static Map<String, Integer> DBSvlMap = new HashMap();
    public static String DB_AIR = "DB_AIR";
    public static String DB_ANA = "DB_ANA";
    public static String DB_ANALOG = "DB_ANALOG";
    public static String DB_ANALOG_TEMP = "DB_ANALOG_TEMP";
    public static String DB_ATV = "DB_ATV";
    public static String DB_CAB = "DB_CAB";
    public static String DB_CI_PLUS = "DB_CI_PLUS";
    public static String DB_DTV = "DB_DTV";
    public static String DB_SAT = "DB_SAT";
    public static String DB_SAT_PRF = "DB_SAT_PRF";
    public static final int DIGITAL_FAVORITES1 = 0;
    public static final int DIGITAL_FAVORITES2 = 1;
    public static final int DIGITAL_FAVORITES3 = 2;
    public static final int DIGITAL_FAVORITES4 = 3;
    public static final int DIGITAL_FAVORITES_TYPE_ALL = 0;
    public static final int DIGITAL_FAVORITES_TYPE_RADIO = 2;
    public static final int DIGITAL_FAVORITES_TYPE_TV = 1;
    public static final boolean DVBS_PREF_SUPPORT = true;
    public static final boolean DVBS_SUPPORT = true;
    public static final int INVALID_CHANNEL_ID = -1;
    public static final int MAX_ELEMENTS_DIGITAL_FAVORITES_SUPPORTED = 100;
    public static final int NFY_SVL_REASON_UNKNOWN = 0;
    public static final int NFY_SVL_RECORD_ADD = 2;
    public static final int NFY_SVL_RECORD_DEL = 4;
    public static final int NFY_SVL_RECORD_MOD = 8;
    public static int SB_RECORD_NOT_SAVE_CH_NUM = 2;
    public static int SB_VNET_ACTIVE = 2;
    public static int SB_VNET_ACTIVE_EPG_EDITED = 1048576;
    public static int SB_VNET_ALL = 1;
    public static int SB_VNET_ANALOG_SERVICE = 4096;
    public static int SB_VNET_BACKUP1 = 4096;
    public static int SB_VNET_BACKUP2 = 8192;
    public static int SB_VNET_BACKUP3 = 16384;
    public static int SB_VNET_BLOCKED = 256;
    public static int SB_VNET_CH_NAME_EDITED = 131072;
    public static int SB_VNET_EPG = 4;
    public static int SB_VNET_FAKE = 32768;
    public static int SB_VNET_FAVORITE1 = 16;
    public static int SB_VNET_FAVORITE2 = 32;
    public static int SB_VNET_FAVORITE3 = 64;
    public static int SB_VNET_FAVORITE4 = 128;
    public static int SB_VNET_FREQ_EDITED = 2097152;
    public static int SB_VNET_INB_LIST = 1024;
    public static int SB_VNET_LCN_APPLIED = 262144;
    public static int SB_VNET_NUMERIC_SELECTABLE = 512;
    public static int SB_VNET_OOB_LIST = 512;
    public static int SB_VNET_RADIO_SERVICE = 1024;
    public static int SB_VNET_REMOVAL = 4194304;
    public static int SB_VNET_REMOVAL_TO_CONFIRM = 8388608;
    public static int SB_VNET_SCRAMBLED = 2048;
    public static int SB_VNET_TV_SERVICE = 8192;
    public static int SB_VNET_USER_TMP_UNLOCK = 65536;
    public static int SB_VNET_USE_DECODER = 524288;
    public static int SB_VNET_USE_DECODER_2 = 16384;
    public static int SB_VNET_VISIBLE = 8;
    public static int SB_VOPT_3D_SERVICE = 1048576;
    public static int SB_VOPT_ACI_APPIED = 128;
    public static int SB_VOPT_CH_NAME_EDITED = 8;
    public static int SB_VOPT_CH_NUM_EDITED = 1024;
    public static int SB_VOPT_CURRENT_COUNTRY = 4096;
    public static int SB_VOPT_DELETED_BY_USER = 8192;
    public static int SB_VOPT_FREQ_EDITED = 16;
    public static int SB_VOPT_HAS_SMD = 8192;
    public static int SB_VOPT_HD_SIMULCAST = 128;
    public static int SB_VOPT_HD_SIMULICAST_LCN_APPLIED = 131072;
    public static int SB_VOPT_IS_BRDCSTING = 16384;
    public static int SB_VOPT_IS_ONE_SEGMENT = 262144;
    public static int SB_VOPT_LCN_APPLIED = 32;
    public static int SB_VOPT_LCN_REORDERING = 524288;
    public static int SB_VOPT_MANUAL_OBTAINED = 64;
    public static int SB_VOPT_MODIFIED_VISIBLE = 131072;
    public static int SB_VOPT_NOT_SAVE_CH_NUM = 2;
    public static int SB_VOPT_NVOD_REF = 16384;
    public static int SB_VOPT_NVOD_TS = 32768;
    public static int SB_VOPT_NZL_REMOVE_DUPLICATE_LCN_CH = 2097152;
    public static int SB_VOPT_PORTUGAL_HD_SIMULCAST = 2048;
    public static int SB_VOPT_REMOVED_BY_HD_SIMULICAST = 262144;
    public static int SB_VOPT_SDT_AVAILABLE = 512;
    public static int SB_VOPT_SIGNAL_BOOSTER = 256;
    public static int SB_VOPT_SVC_REMOVE_SIMULICAST = 65536;
    public static int SB_VOPT_USER_TMP_UNLOCK = 4;
    public static final int SVL_SERVICE_TYPE_APP = 3;
    public static final int SVL_SERVICE_TYPE_CI14_VIRTUAL_CH = 17;
    public static final int SVL_SERVICE_TYPE_HBBTV = 15;
    public static final int SVL_SERVICE_TYPE_ISDB_BOOKMARK = 11;
    public static final int SVL_SERVICE_TYPE_ISDB_DATA = 6;
    public static final int SVL_SERVICE_TYPE_ISDB_DIGITAL_AUDIO = 5;
    public static final int SVL_SERVICE_TYPE_ISDB_DIGITAL_TV = 4;
    public static final int SVL_SERVICE_TYPE_ISDB_ENGINEERING = 10;
    public static final int SVL_SERVICE_TYPE_ISDB_SPECIAL_AUDIO = 8;
    public static final int SVL_SERVICE_TYPE_ISDB_SPECIAL_DATA = 9;
    public static final int SVL_SERVICE_TYPE_ISDB_SPECIAL_VIDEO = 7;
    public static final int SVL_SERVICE_TYPE_MHEG = 13;
    public static final int SVL_SERVICE_TYPE_MHP = 14;
    public static final int SVL_SERVICE_TYPE_PRIVATE = 16;
    public static final int SVL_SERVICE_TYPE_RADIO = 2;
    public static final int SVL_SERVICE_TYPE_TIMESHIFT = 12;
    public static final int SVL_SERVICE_TYPE_TV = 1;
    public static final int SVL_SERVICE_TYPE_UNKNOWN = 0;
    private static final String TAG = "MtkTvChCommon";
    public static final int TV_SYS_A = 1;
    public static final int TV_SYS_AUTO = Integer.MIN_VALUE;
    public static final int TV_SYS_B = 2;
    public static final int TV_SYS_C = 4;
    public static final int TV_SYS_D = 8;
    public static final int TV_SYS_E = 16;
    public static final int TV_SYS_F = 32;
    public static final int TV_SYS_G = 64;
    public static final int TV_SYS_H = 128;
    public static final int TV_SYS_I = 256;
    public static final int TV_SYS_J = 512;
    public static final int TV_SYS_K = 1024;
    public static final int TV_SYS_K_PRIME = 2048;
    public static final int TV_SYS_L = 4096;
    public static final int TV_SYS_L_PRIME = 8192;
    public static final int TV_SYS_M = 16384;
    public static final int TV_SYS_N = 32768;
    public static final int TV_SYS_UNKNOWN = 0;
    private static Map<Integer, String> audioSystemNameMap = new HashMap();
    private static Map<Integer, String> colorSystemNameMap = new HashMap();
    private static Map<Integer, String> tvSystemNameMap = new HashMap();

    public enum BroadcastMedium {
        UKNOWN,
        DVBT,
        DVBC,
        DVBGS,
        DVBPS,
        CI
    }

    public enum ChannelOperator {
        APPEND,
        UPDATE,
        DELETE
    }

    public enum ChannelRFInfoType {
        RF_QUALITY,
        RF_LEVEL,
        RF_PHYSICAL_CH,
        RF_FREQUENCY,
        RF_MODULATION,
        RF_SNR
    }

    public enum FavoritesOperator {
        APPEND,
        DELETE,
        CLEAR
    }

    public enum TunerModulation {
        MOD_UNKNOWN,
        MOD_PSK_8,
        MOD_VSB_8,
        MOD_VSB_16,
        MOD_QAM_16,
        MOD_QAM_32,
        MOD_QAM_64,
        MOD_QAM_80,
        MOD_QAM_96,
        MOD_QAM_112,
        MOD_QAM_128,
        MOD_QAM_160,
        MOD_QAM_192,
        MOD_QAM_224,
        MOD_QAM_256,
        MOD_QAM_320,
        MOD_QAM_384,
        MOD_QAM_448,
        MOD_QAM_512,
        MOD_QAM_640,
        MOD_QAM_768,
        MOD_QAM_896,
        MOD_QAM_1024,
        MOD_QPSK,
        MOD_OQPSK,
        MOD_BPSK,
        MOD_VSB_AM,
        MOD_QAM_4_NR,
        MOD_FM_RADIO
    }

    static {
        tvSystemNameMap.put(0, "Unknow");
        tvSystemNameMap.put(1, MtkTvRatingConvert2Goo.SUB_RATING_STR_A);
        tvSystemNameMap.put(2, "B");
        tvSystemNameMap.put(4, "C");
        tvSystemNameMap.put(8, MtkTvRatingConvert2Goo.SUB_RATING_STR_D);
        tvSystemNameMap.put(16, MtkTvRatingConvert2Goo.STR_CA_TV_E);
        tvSystemNameMap.put(32, MtkTvRatingConvert2Goo.RATING_STR_F);
        tvSystemNameMap.put(64, "G");
        tvSystemNameMap.put(128, "H");
        tvSystemNameMap.put(256, "I");
        tvSystemNameMap.put(512, "J");
        tvSystemNameMap.put(1024, "K");
        tvSystemNameMap.put(1024, "K");
        tvSystemNameMap.put(2048, "K'");
        tvSystemNameMap.put(4096, "L");
        tvSystemNameMap.put(8192, "L'");
        tvSystemNameMap.put(16384, MtkTvRatingConvert2Goo.RATING_STR_M);
        tvSystemNameMap.put(32768, "N");
        tvSystemNameMap.put(Integer.MIN_VALUE, "AUTO");
        colorSystemNameMap.put(-1, "Unknow");
        colorSystemNameMap.put(0, "NTSC");
        colorSystemNameMap.put(1, "PAL");
        colorSystemNameMap.put(2, "SECAM");
        colorSystemNameMap.put(3, "NTSC_443");
        colorSystemNameMap.put(4, "PAL M");
        colorSystemNameMap.put(5, "PAL N");
        colorSystemNameMap.put(6, "PAL 60");
        audioSystemNameMap.put(0, "Unknow");
        audioSystemNameMap.put(1, "AM");
        audioSystemNameMap.put(2, "FM MONO");
        audioSystemNameMap.put(4, "FM EIA J");
        audioSystemNameMap.put(8, "FM A2");
        audioSystemNameMap.put(16, "FM A2 DK1");
        audioSystemNameMap.put(32, "FM A2 DK2");
        audioSystemNameMap.put(64, "FM RADIO");
        audioSystemNameMap.put(128, "NICAM");
        audioSystemNameMap.put(256, "BTSC");
        DBSvlMap.put(DB_DTV, 1);
        DBSvlMap.put(DB_AIR, 1);
        DBSvlMap.put(DB_ATV, 2);
        DBSvlMap.put(DB_ANA, 2);
        DBSvlMap.put(DB_CAB, 2);
        DBSvlMap.put(DB_SAT, 3);
        DBSvlMap.put(DB_SAT_PRF, 4);
        DBSvlMap.put(DB_CI_PLUS, 5);
    }

    public static TunerModulation getModulationByValue(int modulation) {
        TunerModulation tunerModulation;
        TunerModulation tunerModulation2 = TunerModulation.MOD_UNKNOWN;
        switch (modulation) {
            case 1:
                tunerModulation = TunerModulation.MOD_PSK_8;
                break;
            case 2:
                tunerModulation = TunerModulation.MOD_VSB_8;
                break;
            case 3:
                tunerModulation = TunerModulation.MOD_VSB_16;
                break;
            case 4:
                tunerModulation = TunerModulation.MOD_QAM_16;
                break;
            case 5:
                tunerModulation = TunerModulation.MOD_QAM_32;
                break;
            case 6:
                tunerModulation = TunerModulation.MOD_QAM_64;
                break;
            case 7:
                tunerModulation = TunerModulation.MOD_QAM_80;
                break;
            case 8:
                tunerModulation = TunerModulation.MOD_QAM_96;
                break;
            case 9:
                tunerModulation = TunerModulation.MOD_QAM_112;
                break;
            case 10:
                tunerModulation = TunerModulation.MOD_QAM_128;
                break;
            case 11:
                tunerModulation = TunerModulation.MOD_QAM_160;
                break;
            case 12:
                tunerModulation = TunerModulation.MOD_QAM_192;
                break;
            case 13:
                tunerModulation = TunerModulation.MOD_QAM_224;
                break;
            case 14:
                tunerModulation = TunerModulation.MOD_QAM_256;
                break;
            case 15:
                tunerModulation = TunerModulation.MOD_QAM_320;
                break;
            case 16:
                tunerModulation = TunerModulation.MOD_QAM_384;
                break;
            case 17:
                tunerModulation = TunerModulation.MOD_QAM_448;
                break;
            case 18:
                tunerModulation = TunerModulation.MOD_QAM_512;
                break;
            case 19:
                tunerModulation = TunerModulation.MOD_QAM_640;
                break;
            case 20:
                tunerModulation = TunerModulation.MOD_QAM_768;
                break;
            case 21:
                tunerModulation = TunerModulation.MOD_QAM_896;
                break;
            case 22:
                tunerModulation = TunerModulation.MOD_QAM_1024;
                break;
            case 23:
                tunerModulation = TunerModulation.MOD_QPSK;
                break;
            case 24:
                tunerModulation = TunerModulation.MOD_OQPSK;
                break;
            case 25:
                tunerModulation = TunerModulation.MOD_BPSK;
                break;
            case 26:
                tunerModulation = TunerModulation.MOD_VSB_AM;
                break;
            case 27:
                tunerModulation = TunerModulation.MOD_QAM_4_NR;
                break;
            case 28:
                tunerModulation = TunerModulation.MOD_FM_RADIO;
                break;
            default:
                tunerModulation = TunerModulation.MOD_UNKNOWN;
                break;
        }
        Log.d(TAG, "getModulationByValue: " + modulation + "--" + tunerModulation.toString() + "\n");
        return tunerModulation;
    }

    public static String getTvSystemName(int tvSystem) {
        return tvSystemNameMap.get(Integer.valueOf(tvSystem));
    }

    public static String getColorSystemName(int colorSystem) {
        return colorSystemNameMap.get(Integer.valueOf(colorSystem));
    }

    public static String getAudioSystemName(int audioSystem) {
        return audioSystemNameMap.get(Integer.valueOf(audioSystem));
    }

    public static int getSvlIdByName(String dbName) {
        if (DBSvlMap.containsKey(dbName)) {
            return DBSvlMap.get(dbName).intValue();
        }
        Log.d(TAG, "Can not find svlid by name " + dbName);
        return -1;
    }

    public static void main(String[] args) {
        PrintStream printStream = System.out;
        printStream.println(getTvSystemName(2) + "|" + getTvSystemName(64));
    }

    public static boolean isEmulator() {
        if (SystemProperties.get("vendor.mtk.inside").length() == 0 || "0".equals(SystemProperties.get("vendor.mtk.inside"))) {
            return true;
        }
        return false;
    }

    public static int SB_ATSC_GET_MAJOR_CHANNEL_NUM(int channelId) {
        return TVNativeWrapper.sbAtscGetMajorChannelNum_native(channelId);
    }

    public static int SB_ATSC_GET_MINOR_CHANNEL_NUM(int channelId) {
        return TVNativeWrapper.sbAtscGetMinorChannelNum_native(channelId);
    }

    public static int SB_ISDB_GET_MAJOR_NUMBER(int channelId) {
        return TVNativeWrapper.sbIsdbGetMajorNumber_native(channelId);
    }

    public static int SB_ISDB_GET_MINOR_NUMBER(int channelId) {
        return TVNativeWrapper.sbIsdbGetMinorNumber_native(channelId);
    }

    public static int SB_ISDB_GET_CHANNEL_INDEX(int channelId) {
        return TVNativeWrapper.sbIsdbGetChannelIndex_native(channelId);
    }
}
