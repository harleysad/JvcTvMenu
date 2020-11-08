package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import java.util.HashMap;
import java.util.Map;

public class MtkTvConfigBase {
    public static final int CFGR_CANT_INIT = -1;
    public static final int CFGR_INV_ARG = -3;
    public static final int CFGR_NOT_INIT = -2;
    public static final int CFGR_NOT_SUPPORT = -4;
    public static final int CFGR_NO_ACTION = 1;
    public static final int CFGR_OK = 0;
    public static final int CFGR_REC_NOT_FOUND = -5;
    public static final String TAG = "TV_MtkTvConfig";
    private String cfgItems = "";
    private int cfgItemsStatus = 0;

    public int getMinMaxConfigValue(String cfgId) {
        return getMinMaxConfigValue(-1, cfgId);
    }

    private int getMinMaxConfigValue(int inputGroup, String cfgId) {
        Log.d(TAG, "Enter getMinMaxConfigValue, inputGroup=" + inputGroup + ", cfgId=" + cfgId + "\n");
        int value = TVNativeWrapper.getMinMaxConfigValue_native(inputGroup, cfgId);
        Log.d(TAG, "Leave getMinMaxConfigValue\n");
        return value;
    }

    public static int getMinValue(int value) {
        return (short) (65535 & value);
    }

    public static int getMaxValue(int value) {
        return (short) ((-65536 & value) >> 16);
    }

    private static String getStringByInt(int value) {
        return String.valueOf(new char[]{(char) (value >> 24), (char) ((value >> 16) & 255), (char) ((value >> 8) & 255)});
    }

    private static int getIntByString(String value) {
        char[] mChars = value.toCharArray();
        if (mChars.length != 3) {
            return 0;
        }
        return (mChars[0] << 24) | (mChars[1] << 16) | (mChars[2] << 8);
    }

    public String getCountry() {
        return getStringByInt(getConfigValue(MtkTvConfigTypeBase.CFG_COUNTRY_COUNTRY));
    }

    public int setCountry(String value) {
        int country = getIntByString(value);
        if (country == 0) {
            return -3;
        }
        return setConfigValue(MtkTvConfigTypeBase.CFG_COUNTRY_COUNTRY, country);
    }

    public String getLanguage() {
        return getStringByInt(getConfigValue(MtkTvConfigTypeBase.CFG_GUI_LANG_GUI_LANGUAGE));
    }

    public String getLanguage(String CfgId) {
        return getStringByInt(getConfigValue(CfgId));
    }

    public int setLanguage(String value) {
        int language = getIntByString(value);
        if (language == 0) {
            return -3;
        }
        return setConfigValue(MtkTvConfigTypeBase.CFG_GUI_LANG_GUI_LANGUAGE, language);
    }

    public int setLanguage(String CfgId, String value) {
        int language = getIntByString(value);
        if (language == 0) {
            return -3;
        }
        return setConfigValue(CfgId, language);
    }

    public int getConfigValue(String cfgId) {
        return getConfigValue(-1, cfgId);
    }

    public int getConfigValue(int inputGroup, String cfgId) {
        Log.d(TAG, "Enter getConfigValue, inputGroup=" + inputGroup + ", cfgId=" + cfgId + "\n");
        int value = TVNativeWrapper.getConfigValue_native(inputGroup, cfgId);
        Log.d(TAG, "Leave getConfigValue\n");
        return value;
    }

    public int setConfigValue(String cfgId, int value) {
        return setConfigValue(-1, cfgId, value, 3);
    }

    public int setConfigValue(String cfgId, int value, int isUpdate) {
        return setConfigValue(-1, cfgId, value, isUpdate);
    }

    public int resetTotalUserWatchTime() {
        return setConfigValue(MtkTvConfigTypeBase.CFG_MISC_TOTAL_USER_WATCH_TIME, 0, 1);
    }

    public int setConfigValue(int inputGroup, String cfgId, int value, int isUpdate) {
        Log.d(TAG, "Enter setConfigValue, inputGroup=" + inputGroup + ", cfgId=" + cfgId + ", value=" + value + ", isUpdate=" + isUpdate + "\n");
        int relValue = TVNativeWrapper.setConfigValue_native(inputGroup, cfgId, value, isUpdate);
        StringBuilder sb = new StringBuilder();
        sb.append("Leave setConfigValue, return value = ");
        sb.append(relValue);
        sb.append("\n");
        Log.d(TAG, sb.toString());
        return relValue;
    }

    public int dumpChannelList(int value) {
        int retValue = setConfigString(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_SET_PATH, "");
        if (retValue == 0) {
            return setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_DUMP, value, 3);
        }
        return retValue;
    }

    public int loadChannelList(int value) {
        int retValue = setConfigString(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_SET_PATH, "");
        if (retValue == 0) {
            return setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_LOAD, value, 3);
        }
        return retValue;
    }

    public int dumpChannelListByPath(int value, String absolutePath) {
        if (absolutePath == null) {
            return -3;
        }
        int retValue = setConfigString(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_SET_PATH, absolutePath);
        if (retValue == 0) {
            return setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_DUMP, value, 3);
        }
        return retValue;
    }

    public int loadChannelListByPath(int value, String absolutePath) {
        if (absolutePath == null) {
            return -3;
        }
        int retValue = setConfigString(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_SET_PATH, absolutePath);
        if (retValue == 0) {
            return setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CH_PRESET_LOAD, value, 3);
        }
        return retValue;
    }

    public int convertDrvValueToAppValue(String cfgId, int value) {
        return setConfigValue(cfgId, value, 4);
    }

    public int convertAppValueToDrvValue(String cfgId, int value) {
        return setConfigValue(cfgId, value, 5);
    }

    public int isConfigVisible(String cfgId) {
        return isConfigVisible(-1, cfgId);
    }

    public int isConfigVisible(int inputGroup, String cfgId) {
        Log.d(TAG, "Enter isConfigVisible, inputGroup=" + inputGroup + ", cfgId=" + cfgId + "\n");
        int relValue = TVNativeWrapper.isConfigVisible_native(inputGroup, cfgId);
        StringBuilder sb = new StringBuilder();
        sb.append("Leave isConfigVisible, return value = ");
        sb.append(relValue);
        sb.append("\n");
        Log.d(TAG, sb.toString());
        return relValue;
    }

    public int isConfigEnabled(String cfgId) {
        return isConfigEnabled(-1, cfgId);
    }

    public int isConfigEnabled(int inputGroup, String cfgId) {
        Log.d(TAG, "Enter isConfigEnabled, inputGroup=" + inputGroup + ", cfgId=" + cfgId + "\n");
        int relValue = TVNativeWrapper.isConfigEnabled_native(inputGroup, cfgId);
        StringBuilder sb = new StringBuilder();
        sb.append("Leave isConfigEnabled, return value = ");
        sb.append(relValue);
        sb.append("\n");
        Log.d(TAG, sb.toString());
        return relValue;
    }

    public Map<Integer, Boolean> isConfigItemsEnabled(String cfgId) {
        return isConfigItemsEnabled(-1, cfgId);
    }

    private Map<Integer, Boolean> isConfigItemsEnabled(int inputGroup, String cfgId) {
        int i = 0;
        Map<Integer, Boolean> map = null;
        Log.d(TAG, "Enter isConfigItemsEnabled, inputGroup=" + inputGroup + ", cfgId=" + cfgId + "\n");
        int relValue = TVNativeWrapper.isConfigItemsEnabled_native(inputGroup, cfgId, this);
        if (relValue != -1) {
            String[] items = this.cfgItems.split("\\|");
            map = new HashMap<>();
            while (i < items.length) {
                try {
                    Integer tmp = Integer.valueOf(items[i]);
                    if (((1 << i) & this.cfgItemsStatus) != 0) {
                        map.put(tmp, Boolean.TRUE);
                    } else {
                        map.put(tmp, Boolean.FALSE);
                    }
                    i++;
                } catch (Exception e) {
                }
            }
        }
        Log.d(TAG, "Leave isConfigItemsEnabled, return value = " + relValue + "\n");
        return map;
    }

    public int resetConfigValues(int type) {
        Log.d(TAG, "Enter resetConfigValues\n");
        int relValue = TVNativeWrapper.resetConfigValues_native(type);
        Log.d(TAG, "Leave resetConfigValues, return value = " + relValue + "\n");
        return relValue;
    }

    public String getConfigString(String cfgId) {
        Log.d(TAG, "Enter getConfigString\n");
        String relValue = TVNativeWrapper.getConfigString_native(cfgId);
        StringBuilder sb = new StringBuilder();
        sb.append("Leave getConfigString, return value = ");
        sb.append(relValue == null ? "null" : relValue);
        sb.append("\n");
        Log.d(TAG, sb.toString());
        return relValue;
    }

    public int setConfigString(String cfgId, String value) {
        Log.d(TAG, "Enter setConfigString\n");
        int relValue = TVNativeWrapper.setConfigString_native(cfgId, value);
        Log.d(TAG, "Leave setConfigString, return value = " + relValue + "\n");
        return relValue;
    }

    public int setPipConfig(int status) {
        Log.d(TAG, "Enter setPipConfig\n");
        int relValue = TVNativeWrapper.setPipConfig_native(status);
        Log.d(TAG, "Leave setPipConfig, return value = " + relValue + "\n");
        return relValue;
    }
}
