package com.mediatek.wwtv.tvcenter.util;

import android.text.TextUtils;
import com.mediatek.twoworlds.tv.MtkTvParserIniBase;
import com.mediatek.twoworlds.tv.model.MtkTvParserIniInfoBase;
import java.util.ArrayList;
import java.util.List;

public class DataSeparaterUtil {
    private static final int ATV_ONLY = 8192;
    private static final int BT = 1048576;
    private static final int CHUPDOWNACTION = 131072;
    private static final int CI = 2048;
    private static final int COLORKEY = 262144;
    private static final int DAP = 16;
    private static final int DBX = 32;
    private static final int DTS = 8;
    private static final int DTSSS2 = 64;
    private static final int DVBC = 32768;
    private static final int DVBS = 65536;
    private static final int DVBT = 16384;
    private static final int EWBS = 524288;
    private static final int GINGA = 4;
    private static final int HBBTV = 1;
    private static final int MHEG5 = 1024;
    private static final int OAD = 256;
    private static final int PVR = 2;
    private static final int SONICE = 128;
    private static String TAG = "DataSeparaterUtil";
    private static final int TELETEXT = 512;
    private static final int TSHIFT = 4096;
    private static int exist = 0;
    private static int flag = 0;
    private static DataSeparaterUtil mDataSeparaterUtil;
    private static int value = 0;
    private final String INI_PATH_CUSTOM = "/CustomizationFunc.ini";
    private final String INI_PATH_SOUND = "/ADV_SOUND_SYSTEM.ini";
    private final String KEY_INI_ATV_ONLY = "ATV:atvOnly";
    private final String KEY_INI_AUTO_SLEEP = "AUTOSLEEP:bAUTOSLEEPEnabled";
    private final String KEY_INI_BLACKLIST_KEYCODE_TO_CEC = "KEYCODETOCEC:sBlackList";
    private final String KEY_INI_BT = "BT:bBTEnabled";
    private final String KEY_INI_CHUPDOWNACTION = "CHUPDOWNACTION:bAlwaysSupport";
    private final String KEY_INI_CI = "CI:bCIEnabled";
    private final String KEY_INI_DISABLE_COLOR_KEY = "COLORKEY:bForceRemoved";
    private final String KEY_INI_ENERGY_STAR_ICON = "PICTUREMODE:bIsShowEnergyStarIcon";
    private final String KEY_INI_EWBS = "EWBS:bEWBSEnabled";
    private final String KEY_INI_GINGA = "GINGA:bGINGAEnabled";
    private final String KEY_INI_HBBTV = "HBBTV:bHBBTVEnabled";
    private final String KEY_INI_MHEG5 = "MHEG5:bMHEG5Enabled";
    private final String KEY_INI_OAD = "OAD:bOADEnabled";
    private final String KEY_INI_PVR = "PVR:bPVREnabled";
    private final String KEY_INI_SOUND_ADV = "ADV_SOUND:Type";
    private final String KEY_INI_TELETEXT = "TELETEXT:bTELETEXTEnabled";
    private final String KEY_INI_TSHIFT = "PVR:bTShiftEnable";
    private final String KEY_INI_TUNER_MODE_C = "DVBSCAN:bDVBCEnabled";
    private final String KEY_INI_TUNER_MODE_S = "DVBSCAN:bDVBSEnabled";
    private final String KEY_INI_TUNER_MODE_T = "DVBSCAN:bDVBTEnabled";
    private String keycode_to_cec_blacklist = "";
    private final MtkTvParserIniBase mMtkTvParserIniBase = new MtkTvParserIniBase();

    public static DataSeparaterUtil getInstance() {
        if (mDataSeparaterUtil == null) {
            mDataSeparaterUtil = new DataSeparaterUtil();
        }
        String str = TAG;
        MtkLog.d(str, "value=" + Integer.toHexString(value) + ",flag=" + Integer.toHexString(flag) + ",exist=" + Integer.toHexString(exist));
        return mDataSeparaterUtil;
    }

    public boolean isSupportHbbtv() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 1) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "HBBTV:bHBBTVEnabled");
            MtkLog.d(TAG, "HBBTV iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 1;
            }
            flag |= 1;
            if ((value & 1) == 0) {
                return false;
            }
            return true;
        } else if ((value & 1) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportPvr() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 2) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "PVR:bPVREnabled");
            MtkLog.d(TAG, "PVR iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 2;
            }
            flag |= 2;
            if ((value & 2) == 0) {
                return false;
            }
            return true;
        } else if ((value & 2) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportGinga() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 4) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "GINGA:bGINGAEnabled");
            MtkLog.d(TAG, "GINGA iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 4;
            }
            flag |= 4;
            if ((value & 4) == 0) {
                return false;
            }
            return true;
        } else if ((value & 4) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportEWBS() {
        if ((flag & 524288) != 0) {
            return (value & 524288) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "EWBS:bEWBSEnabled");
        MtkLog.d(TAG, "EWBS iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 524288;
        }
        if (infobase.getErrorCode() == 0 && infobase.getIntData() != -1) {
            exist |= 524288;
        }
        flag |= 524288;
        if ((524288 & value) == 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportDTS() {
        if ((flag & 8) != 0) {
            return (value & 8) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/ADV_SOUND_SYSTEM.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "ADV_SOUND:Type");
        MtkLog.d(TAG, "DTS iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && (infobase.getIntData() == 1 || infobase.getIntData() == 255))) {
            value |= 8;
        }
        flag |= 8;
        if ((value & 8) == 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportDAP() {
        if ((flag & 16) != 0) {
            return (value & 16) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/ADV_SOUND_SYSTEM.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "ADV_SOUND:Type");
        MtkLog.d(TAG, "DAP iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && (infobase.getIntData() == 3 || infobase.getIntData() == 255))) {
            value |= 16;
        }
        flag |= 16;
        if ((value & 16) == 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportDBX() {
        if ((flag & 32) != 0) {
            return (value & 32) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/ADV_SOUND_SYSTEM.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "ADV_SOUND:Type");
        MtkLog.d(TAG, "DBX iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && (infobase.getIntData() == 2 || infobase.getIntData() == 255))) {
            value |= 32;
        }
        flag |= 32;
        if ((value & 32) == 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportDTSSS() {
        if ((flag & 64) != 0) {
            return (value & 64) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/ADV_SOUND_SYSTEM.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "ADV_SOUND:Type");
        MtkLog.d(TAG, "DTSSS iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && (infobase.getIntData() == 4 || infobase.getIntData() == 255))) {
            value |= 64;
        }
        flag |= 64;
        if ((value & 64) == 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportSonicEmotion() {
        if ((flag & 128) != 0) {
            return (value & 128) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/ADV_SOUND_SYSTEM.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "ADV_SOUND:Type");
        MtkLog.d(TAG, "Sonic iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && (infobase.getIntData() == 5 || infobase.getIntData() == 255))) {
            value |= 128;
        }
        flag |= 128;
        if ((value & 128) == 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportOAD() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 256) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "OAD:bOADEnabled");
            MtkLog.d(TAG, "OAD iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 256;
            }
            flag |= 256;
            if ((value & 256) == 0) {
                return false;
            }
            return true;
        } else if ((value & 256) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportTeletext() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 512) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "TELETEXT:bTELETEXTEnabled");
            MtkLog.d(TAG, "Teletext iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 512;
            }
            flag |= 512;
            if ((value & 512) == 0) {
                return false;
            }
            return true;
        } else if ((value & 512) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportMheg5() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 1024) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "MHEG5:bMHEG5Enabled");
            MtkLog.d(TAG, "Mheg5 iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 1024;
            }
            flag |= 1024;
            if ((value & 1024) == 0) {
                return false;
            }
            return true;
        } else if ((value & 1024) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportCI() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 2048) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "CI:bCIEnabled");
            MtkLog.d(TAG, "CI iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 2048;
            }
            flag |= 2048;
            if ((value & 2048) == 0) {
                return false;
            }
            return true;
        } else if ((value & 2048) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportTShift() {
        if (isAtvOnly()) {
            return false;
        }
        if ((flag & 4096) == 0) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "PVR:bTShiftEnable");
            MtkLog.d(TAG, "TSHIFT iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
            if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
                value |= 4096;
            }
            flag |= 4096;
            if ((value & 4096) == 0) {
                return false;
            }
            return true;
        } else if ((value & 4096) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportBT() {
        if ((flag & 1048576) != 0) {
            return (value & 1048576) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "BT:bBTEnabled");
        MtkLog.d(TAG, "BT iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 1048576;
        }
        flag |= 1048576;
        if ((1048576 & value) == 0) {
            return false;
        }
        return true;
    }

    public boolean isAtvOnly() {
        if ((flag & 8192) != 0) {
            return (value & 8192) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "ATV:atvOnly");
        MtkLog.d(TAG, "CI iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 8192;
        }
        flag |= 8192;
        if ((value & 8192) == 0) {
            return false;
        }
        return true;
    }

    public boolean isDVBTSupport() {
        if ((flag & 16384) != 0) {
            return (value & 16384) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "DVBSCAN:bDVBTEnabled");
        MtkLog.d(TAG, "dvbt iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 16384;
        }
        if (infobase.getErrorCode() == 0 && infobase.getIntData() != -1) {
            exist |= 16384;
        }
        flag |= 16384;
        if ((value & 16384) == 0) {
            return false;
        }
        return true;
    }

    public boolean isDVBCSupport() {
        if ((flag & 32768) != 0) {
            return (value & 32768) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "DVBSCAN:bDVBCEnabled");
        MtkLog.d(TAG, "DVBC iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 32768;
        }
        if (infobase.getErrorCode() == 0 && infobase.getIntData() != -1) {
            exist |= 32768;
        }
        flag |= 32768;
        if ((32768 & value) == 0) {
            return false;
        }
        return true;
    }

    public boolean isDVBSSupport() {
        if ((flag & 65536) != 0) {
            return (value & 65536) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "DVBSCAN:bDVBSEnabled");
        MtkLog.d(TAG, "DVBS iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 65536;
        }
        if (infobase.getErrorCode() == 0 && infobase.getIntData() != -1) {
            exist |= 65536;
        }
        flag |= 65536;
        if ((65536 & value) == 0) {
            return false;
        }
        return true;
    }

    public boolean isCHUPDOWNACTIONSupport() {
        if ((flag & 131072) != 0) {
            return (value & 131072) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "CHUPDOWNACTION:bAlwaysSupport");
        MtkLog.d(TAG, "CI iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0 || (infobase.getErrorCode() == 0 && infobase.getIntData() == 1)) {
            value |= 131072;
        }
        flag |= 131072;
        if ((131072 & value) == 0) {
            return false;
        }
        return true;
    }

    public boolean isTunerModeIniExist() {
        boolean z = true;
        if (isDVBTSupport() || isDVBCSupport() || isDVBSSupport()) {
            if ((exist & 65536) == 0 && (exist & 16384) == 0 && (exist & 32768) == 0) {
                z = false;
            }
            boolean ret = z;
            MtkLog.d(TAG, "isTunerModeIniExist = " + ret);
            return ret;
        }
        if ((exist & 65536) == 0 && (exist & 16384) == 0 && (exist & 32768) == 0) {
            z = false;
        }
        boolean isExist = z;
        MtkLog.d(TAG, "isTunerModeIniExist=" + isExist);
        return isExist;
    }

    public boolean isEWBSIniExist() {
        boolean z = false;
        if (isSupportEWBS()) {
            if ((exist & 524288) != 0) {
                z = true;
            }
            boolean ret = z;
            MtkLog.d(TAG, "isEWBSIniRet = " + ret);
            return ret;
        }
        if ((exist & 524288) != 0) {
            z = true;
        }
        boolean isExist = z;
        MtkLog.d(TAG, "isEWBSIniExist = " + isExist);
        return isExist;
    }

    public boolean isShowEnergyStarIcon() {
        int value2 = 0;
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "PICTUREMODE:bIsShowEnergyStarIcon");
        MtkLog.d(TAG, "energy star iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() == 0) {
            value2 = infobase.getIntData();
            MtkLog.d(TAG, "default value==" + value2);
        }
        MtkLog.d(TAG, "value==" + value2);
        if (value2 == 1) {
            return true;
        }
        return false;
    }

    public List<Integer> getBlacklistKeycodeToCec() {
        List<Integer> list = new ArrayList<>();
        if (TextUtils.isEmpty(this.keycode_to_cec_blacklist)) {
            String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
            MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getStringConfigData(iniPathString, "KEYCODETOCEC:sBlackList");
            MtkLog.d(TAG, "getBlacklistKeycodeToCec iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getStringData());
            if (!TextUtils.isEmpty(infobase.getStringData())) {
                this.keycode_to_cec_blacklist = infobase.getStringData();
            }
        }
        if (!TextUtils.isEmpty(this.keycode_to_cec_blacklist) && !TextUtils.isEmpty(this.keycode_to_cec_blacklist.trim())) {
            for (String string : this.keycode_to_cec_blacklist.split(",")) {
                try {
                    list.add(Integer.valueOf(Integer.parseInt(string)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public int getValueAutoSleep() {
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "AUTOSLEEP:bAUTOSLEEPEnabled");
        MtkLog.d(TAG, "CI iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() != 0) {
            MtkLog.d(TAG, "default value==4");
            return 4;
        } else if (infobase.getErrorCode() != 0) {
            return 4;
        } else {
            int value2 = infobase.getIntData();
            MtkLog.d(TAG, "default==" + value2);
            return value2;
        }
    }

    public boolean isDisableColorKey() {
        if ((flag & 262144) != 0) {
            return (value & 262144) != 0;
        }
        String iniPathString = this.mMtkTvParserIniBase.getFileBasePath() + "/CustomizationFunc.ini";
        MtkTvParserIniInfoBase infobase = this.mMtkTvParserIniBase.getIntConfigData(iniPathString, "COLORKEY:bForceRemoved");
        MtkLog.d(TAG, "ColorKey iniPathString=" + iniPathString + ",errorcode=" + infobase.getErrorCode() + ",data=" + infobase.getIntData());
        if (infobase.getErrorCode() == 0 && infobase.getIntData() == 1) {
            value |= 262144;
        }
        flag |= 262144;
        if ((262144 & value) == 0) {
            return false;
        }
        return true;
    }
}
