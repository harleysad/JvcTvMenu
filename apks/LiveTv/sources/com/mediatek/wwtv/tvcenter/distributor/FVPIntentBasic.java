package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;

public abstract class FVPIntentBasic {
    public static final String FVP_ACTION = "android.intent.action.SEND";
    public static final String FVP_CALLER = "Caller";
    public static final String FVP_DETAIL = "Detail";
    static final String FVP_MODE = "Mode";
    static final String FVP_MODE_AOK = "AOK";
    static final String FVP_MODE_CNT = "CNT";
    static final String FVP_MODE_FST = "FST";
    static final String FVP_MODE_IPS = "IPS";
    static final String FVP_MODE_LCN = "LCN";
    static final String FVP_MODE_LST = "LST";
    static final String FVP_MODE_PLY = "PLY";
    static final String FVP_MODE_RCU = "RCU";
    static final String FVP_MODE_RST = "RST";
    static final String FVP_MODE_SCN = "SCN";
    static final String FVP_MODE_SRV = "SRV";
    static final String FVP_MODE_TOU = "TOU";
    static final String FVP_MODE_UIB = "UIB";
    static final String FVP_REQUEST = "Request";
    static final String FVP_REQUEST_AIT_URL = "FVP_AIT_URL";
    static final String FVP_REQUEST_APP_INS = "FVP_APP_INS";
    static final String FVP_REQUEST_CHN_SCN = "FVP_CHN_SCN";
    static final String FVP_REQUEST_EPG_REQ = "FVP_EPG_REQ";
    static final String FVP_REQUEST_LVE_REQ = "FVP_LVE_REQ";
    static final String FVP_REQUEST_MNR_ARE = "FVP_MNR_ARE";
    static final String FVP_REQUEST_TOU_MSG = "FVP_TOU_MSG";
    static final String FVP_TYPE = "fvp/request";

    public abstract Intent getLaunchedIntent(Context context, Intent intent);

    public abstract String[] getMode();

    public abstract String getRequest();

    public static String getType(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getType();
    }

    static String getRequest(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(FVP_REQUEST);
    }

    static String getMode(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(FVP_MODE);
    }

    static int getIndexOfArray(String[] array, String target) {
        int index = 0;
        if (array == null || target == null) {
            return -1;
        }
        while (index < array.length && !array[index].equals(target)) {
            index++;
        }
        if (index >= array.length) {
            return -1;
        }
        return index;
    }

    public static String getDetail(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(FVP_DETAIL);
    }

    public static String getCaller(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(FVP_CALLER);
    }

    public static boolean isFvpRequest(Intent intent) {
        return FVP_TYPE.equals(getType(intent));
    }

    public boolean isMatched(Intent intent) {
        String[] modes = getMode();
        if (getRequest() == null || modes == null || !getRequest().equals(getRequest(intent))) {
            return false;
        }
        for (int i = 0; i < modes.length; i++) {
            if (modes[i] != null && modes[i].equals(getMode(intent))) {
                return true;
            }
        }
        return false;
    }
}
