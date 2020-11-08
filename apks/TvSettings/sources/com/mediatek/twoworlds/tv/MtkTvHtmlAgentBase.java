package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvHtmlAgentBase {
    private static final int EXCHANGE_FUNC_GET_CURR_STATUS = 1;
    private static final int EXCHANGE_FUNC_TYPE_UNKNOWN = 0;
    private static final int EXCHANGE_HEADER_TYPE_IDX = 1;
    private static final int EXCHANGE_HEAD_LEN = 2;
    private static final int EXCHANGE_MAX_LEN = 50;
    private static final int EXCHANGE_PAYLOAD_1ST_IDX = 2;
    private static final int EXCHANGE_TOTAL_LEN_IDX = 0;
    public static final String TAG = "MtkTvHtmlAgentBase";

    public enum HtmlAgentCmdType {
        MTKTVAPI_HTML_AGENT_FUNC_UNKNOWN
    }

    public enum HtmlAgentRet {
        HTML_AGENT_RET_OK,
        HTML_AGENT_RET_INTERNAL_ERROR
    }

    public MtkTvHtmlAgentBase() {
        Log.d(TAG, "MtkTvHtmlAgentBase object created");
    }

    public HtmlAgentRet getCurrStatus() {
        Log.d(TAG, "HtmlAgent GetCurrStatus entered");
        HtmlAgentRet ret = HtmlAgentRet.HTML_AGENT_RET_OK;
        int[] data = new int[2];
        exchangeData(1, data);
        int tvApiRet = data[0];
        if (tvApiRet == 0) {
            ret = HtmlAgentRet.HTML_AGENT_RET_OK;
        }
        int currStatus = data[1];
        Log.d(TAG, "HtmlAgent GetCurrStatus return " + tvApiRet + "currStatus " + currStatus + "\n");
        return ret;
    }

    public HtmlAgentRet exchangeData(int exchangeType, int[] data) {
        HtmlAgentRet htmlAgentRet = HtmlAgentRet.HTML_AGENT_RET_OK;
        int[] payload = data;
        if (payload == null) {
            payload = new int[1];
        }
        Log.d(TAG, "exchangeData, exchangeType==" + exchangeType + ",data==" + data);
        int totalLen = payload.length + 2;
        if (totalLen > 50) {
            Log.d(TAG, "[Error]bigger than EXCHANGE_MAX_LEN\n");
            return HtmlAgentRet.HTML_AGENT_RET_INTERNAL_ERROR;
        }
        int[] exchangeData = new int[totalLen];
        exchangeData[0] = totalLen;
        exchangeData[1] = exchangeType;
        int i = 0;
        for (int j = 2; j < totalLen; j++) {
            exchangeData[j] = payload[i];
            i++;
        }
        int retTVNativeWrapper = TVNativeWrapper.htmlAgentExchangeData_native(exchangeData);
        int i2 = 0;
        for (int j2 = 2; j2 < totalLen; j2++) {
            payload[i2] = exchangeData[j2];
            i2++;
        }
        if (retTVNativeWrapper >= 0) {
            return HtmlAgentRet.HTML_AGENT_RET_OK;
        }
        return HtmlAgentRet.HTML_AGENT_RET_INTERNAL_ERROR;
    }
}
