package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvHBBTVBase {
    private static final int AUDIO_PRELOAD_HEAD_LEN = 2;
    private static final int AUDIO_PRELOAD_INFO_ACTIVE = 1;
    private static final int AUDIO_PRELOAD_INFO_AD = 2;
    private static final int AUDIO_PRELOAD_INFO_INDEX = 0;
    private static final int AUDIO_PRELOAD_INFO_LANG = 3;
    private static final int AUDIO_PRELOAD_INFO_LANG_LEN = 4;
    private static final int AUDIO_PRELOAD_INFO_LEN = 9;
    private static final int AUDIO_PRELOAD_INFO_TAG = 7;
    private static final int AUDIO_PRELOAD_INFO_TAG_LEN = 2;
    private static final int AUDIO_PRELOAD_NMEMB = 0;
    private static final int AUDIO_PRELOAD_TOTAL = 1;
    private static final int EXCHANGE_FUNC_AUDIO_GET_LIST = 13;
    private static final int EXCHANGE_FUNC_AUDIO_SET_ACTIVE = 14;
    private static final int EXCHANGE_FUNC_DIALOG_FB = 10;
    private static final int EXCHANGE_FUNC_ENABLE = 1;
    private static final int EXCHANGE_FUNC_GET_FVP_SUPPORT = 18;
    private static final int EXCHANGE_FUNC_GET_HBBTV_SUPPORT = 17;
    private static final int EXCHANGE_FUNC_RESET_APR_MNGR_DB = 11;
    private static final int EXCHANGE_FUNC_START = 2;
    private static final int EXCHANGE_FUNC_STOP = 3;
    private static final int EXCHANGE_FUNC_SUBTITLE_GET_LIST = 15;
    private static final int EXCHANGE_FUNC_SUBTITLE_SET_ACTIVE = 16;
    private static final int EXCHANGE_FUNC_TYPE_UNKNOWN = 0;
    private static final int EXCHANGE_HEADER_TYPE_IDX = 1;
    private static final int EXCHANGE_HEAD_LEN = 2;
    private static final int EXCHANGE_MAX_LEN = 200;
    private static final int EXCHANGE_PAYLOAD_1ST_IDX = 2;
    private static final int EXCHANGE_TOTAL_LEN_IDX = 0;
    private static final int MTKTVAPI_HBBTV_APR_MNGR_DB_ERASE_MANUFACTURE_DATA = 1;
    private static final int MTKTVAPI_HBBTV_APR_MNGR_DB_ERASE_USER_DATA = 2;
    public static final int MTKTVAPI_HBBTV_APR_MNGR_DIALOG_VALUE_ALWAYS = 2;
    public static final int MTKTVAPI_HBBTV_APR_MNGR_DIALOG_VALUE_CANCEL = 0;
    public static final int MTKTVAPI_HBBTV_APR_MNGR_DIALOG_VALUE_ONCE = 1;
    private static final int SBTL_PRELOAD_HEAD_LEN = 2;
    private static final int SBTL_PRELOAD_INFO_ACTIVE = 1;
    private static final int SBTL_PRELOAD_INFO_ATTR = 3;
    private static final int SBTL_PRELOAD_INFO_INDEX = 0;
    private static final int SBTL_PRELOAD_INFO_LANG = 4;
    private static final int SBTL_PRELOAD_INFO_LANG_LEN = 4;
    private static final int SBTL_PRELOAD_INFO_LEN = 10;
    private static final int SBTL_PRELOAD_INFO_TAG = 8;
    private static final int SBTL_PRELOAD_INFO_TAG_LEN = 2;
    private static final int SBTL_PRELOAD_INFO_TYPE = 2;
    private static final int SBTL_PRELOAD_NMEMB = 0;
    private static final int SBTL_PRELOAD_TOTAL = 1;
    public static final String TAG = "MtkTvHBBTVBase";

    public enum HbbtvCmdType {
        MTKTVAPI_HBBTV_FUNC_UNKNOWN,
        MTKTVAPI_HBBTV_FUNC_ENABLE,
        MTKTVAPI_HBBTV_FUNC_START,
        MTKTVAPI_HBBTV_FUNC_STOP,
        MTKTVAPI_HBBTV_FUNC_ACCEPT,
        MTKTVAPI_HBBTV_FUNC_CANCEL,
        MTKTVAPI_HBBTV_SET_NETWORK_STATE,
        MTKTVAPI_HBBTV_SET_NETWORK_CONDITION_STATE
    }

    public enum HbbtvRet {
        HBBTV_RET_OK,
        HBBTV_RET_INTERNAL_ERROR
    }

    public static class MtkTvHbbTVStreamAudio {
        public boolean active;
        public boolean audio_description;
        public int index;
        public char[] lang;
        public long tag;

        public MtkTvHbbTVStreamAudio() {
            this.index = 0;
            this.active = false;
            this.audio_description = false;
            this.lang = new char[]{0, 0, 0, 0};
            this.tag = 0;
            this.index = 0;
            this.active = false;
            this.audio_description = false;
            this.lang[0] = 0;
            this.tag = 0;
        }

        public MtkTvHbbTVStreamAudio(MtkTvHbbTVStreamAudio aud) {
            this.index = 0;
            this.active = false;
            this.audio_description = false;
            this.lang = new char[]{0, 0, 0, 0};
            this.tag = 0;
            this.index = aud.index;
            this.active = aud.active;
            this.audio_description = aud.audio_description;
            for (int i = 0; i < 4; i++) {
                this.lang[i] = aud.lang[i];
            }
            this.tag = aud.tag;
        }
    }

    public enum HBBTV_SUBTITLE_ATTR_T {
        HBBTV_SUBTITLE_ATTR_NORMAL(0),
        HBBTV_ATTR_HEARING_IMPAIRED(1),
        HBBTV_SUBTITLE_ATTR_UNKNOWN(2);
        
        private int index;

        private HBBTV_SUBTITLE_ATTR_T(int index2) {
            this.index = 0;
            this.index = index2;
        }

        public int idOf() {
            return this.index;
        }
    }

    public enum HBBTV_STREAM_SUBTITLE_TYPE_T {
        HBBTV_STREAM_SUBTITLE_UNKNOWN(0),
        HBBTV_STREAM_SUBTITLE_TTML_TYPE(1),
        HBBTV_STREAM_SUBTITLE_DIVX_TYPE(2),
        HBBTV_STREAM_SUBTITLE_TEXT_ASS(3),
        HBBTV_STREAM_SUBTITLE_TEXT_SRT(4),
        HBBTV_STREAM_SUBTITLE_RLE(5),
        HBBTV_STREAM_SUBTITLE_CC_TYPE(6),
        HBBTV_STREAM_SUBTITLE_DVB_TYPE(7),
        HBBTV_STREAM_SUBTITLE_TELTEXT_TYPE(8);
        
        private int index;

        private HBBTV_STREAM_SUBTITLE_TYPE_T(int index2) {
            this.index = 0;
            this.index = index2;
        }

        public int idOf() {
            return this.index;
        }
    }

    public static class MtkTvHbbTVStreamSubtitle {
        public boolean active;
        public int attr;
        public int index;
        public char[] lang;
        public long tag;
        public int type;

        public MtkTvHbbTVStreamSubtitle() {
            this.index = 0;
            this.active = false;
            this.type = 0;
            this.attr = 2;
            this.lang = new char[]{0, 0, 0, 0};
            this.tag = 0;
            this.index = 0;
            this.active = false;
            this.type = 0;
            this.attr = 2;
            this.lang[0] = 0;
            this.tag = 0;
        }

        public MtkTvHbbTVStreamSubtitle(MtkTvHbbTVStreamSubtitle sbtl) {
            this.index = 0;
            this.active = false;
            this.type = 0;
            this.attr = 2;
            this.lang = new char[]{0, 0, 0, 0};
            this.tag = 0;
            this.index = sbtl.index;
            this.active = sbtl.active;
            this.type = sbtl.type;
            this.attr = sbtl.attr;
            for (int i = 0; i < 4; i++) {
                this.lang[i] = sbtl.lang[i];
            }
            this.tag = sbtl.tag;
        }
    }

    public MtkTvHBBTVBase() {
        Log.d(TAG, "MtkTvHBBTVBase object created");
    }

    public HbbtvRet enable(boolean enableOrDisable) {
        int isEnable;
        Log.d(TAG, "hbbtv enable entered");
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        if (enableOrDisable) {
            isEnable = 1;
        } else {
            isEnable = 0;
        }
        Log.i(TAG, "hbbtv enable:" + enableOrDisable);
        HbbtvRet ret = exchangeData(1, new int[]{isEnable});
        Log.d(TAG, "hbbtv enable exit");
        return ret;
    }

    public HbbtvRet start() {
        Log.d(TAG, "start hbbtv entered");
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        HbbtvRet ret = exchangeData(2, (int[]) null);
        Log.d(TAG, "start hbbtv exit");
        return ret;
    }

    public HbbtvRet stop() {
        Log.d(TAG, "stop hbbtv entered");
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        HbbtvRet ret = exchangeData(3, (int[]) null);
        Log.d(TAG, "stop hbbtv exit");
        return ret;
    }

    public HbbtvRet dialogFeedBack(int enableOrDisable) {
        Log.d(TAG, "hbbtv enable entered");
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        Log.i(TAG, "hbbtv dialog enable:" + enableOrDisable);
        HbbtvRet ret = exchangeData(10, new int[]{enableOrDisable});
        Log.d(TAG, "hbbtv dialog enable exit");
        return ret;
    }

    public int setAudioDescription(int e_enable) {
        Log.d(TAG, "setAudioDescription hbbtv entered");
        int ret = TVNativeWrapper.hbbtvSetAudioDescription_native(e_enable);
        Log.d(TAG, "setAudioDescription hbbtv exit");
        return ret;
    }

    public HbbtvRet eraseApvMngrDBData(int e_type) {
        Log.d(TAG, "eraseApvMngrDBData");
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        HbbtvRet ret = exchangeData(11, new int[]{e_type});
        Log.d(TAG, "eraseApvMngrDBData exit");
        return ret;
    }

    public int setDefaultAudioLang(String lang) {
        Log.d(TAG, "setDefaultAudioLang hbbtv entered");
        int ret = TVNativeWrapper.hbbtvSetDefaultAudioLang_native(lang);
        Log.d(TAG, "setDefaultAudioLang hbbtv exit");
        return ret;
    }

    public int setDefaultSubtitleLang(String lang, int enable) {
        Log.d(TAG, "setDefaultSubtitleLang hbbtv entered");
        int ret = TVNativeWrapper.hbbtvSetDefaultSubtitleLang_native(lang, enable);
        Log.d(TAG, "setDefaultSubtitleLang hbbtv exit");
        return ret;
    }

    public int setDefaultSubtitleLang(String lang) {
        return setDefaultSubtitleLang(lang, 1);
    }

    public int getStreamAudioCount() {
        Log.d(TAG, "getStreamAudioCount hbbtv entered");
        int ret = TVNativeWrapper.hbbtvStmGetAudioCount_native();
        Log.d(TAG, "getStreamAudioCount hbbtv exit");
        return ret;
    }

    public String getStreamAudioLang(int idx) {
        Log.d(TAG, "getStreamAudioLang hbbtv entered");
        String ret = TVNativeWrapper.hbbtvStmGetAudioLang_native(idx);
        Log.d(TAG, "getStreamAudioLang hbbtv exit");
        return ret;
    }

    public HbbtvRet getHBBTVSupport(boolean[] b_support) {
        Log.d(TAG, "getHBBTVSupport entered");
        int[] data = {0};
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        HbbtvRet ret = exchangeData(17, data);
        if (data[0] != 0) {
            b_support[0] = true;
        } else {
            b_support[0] = false;
        }
        Log.d(TAG, "getHBBTVSupport:" + b_support[0]);
        Log.d(TAG, "getHBBTVSupport exit");
        return ret;
    }

    public HbbtvRet getFVPSupport(boolean[] b_support) {
        Log.d(TAG, "getFVPSupport entered");
        int[] data = {0};
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        HbbtvRet ret = exchangeData(18, data);
        if (data[0] != 0) {
            b_support[0] = true;
        } else {
            b_support[0] = false;
        }
        Log.d(TAG, "getFVPSupport:" + b_support[0]);
        Log.d(TAG, "getFVPSupport exit");
        return ret;
    }

    public int hbbtvStmGetAduioIndex() {
        Log.d(TAG, "hbbtvStmGetAduioIndex hbbtv entered");
        int ret = TVNativeWrapper.hbbtvStmGetAudio_native();
        Log.d(TAG, "hbbtvStmGetAduioIndex hbbtv exit");
        return ret;
    }

    public int hbbtvStmSetAudioIndex(int idx) {
        Log.d(TAG, "hbbtvStmSetAudioIndex hbbtv entered");
        int ret = TVNativeWrapper.hbbtvStmSetAudioIndex_native(idx);
        Log.d(TAG, "hbbtvStmSetAudioIndex hbbtv exit");
        return ret;
    }

    public HbbtvRet hbbtvAudioGetList(MtkTvHbbTVStreamAudio[] strmAudio, int nmemb, int[] ntotal) {
        int i = nmemb;
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        if (i <= 0) {
            Log.d(TAG, "Invalid arg, nmemb:" + i);
            return HbbtvRet.HBBTV_RET_INTERNAL_ERROR;
        }
        int i2 = 2;
        int ttLen = (9 * i) + 2;
        int[] data = new int[ttLen];
        data[0] = i;
        boolean z = true;
        data[1] = ntotal[0];
        Log.d(TAG, "before exchangeData");
        for (int i3 = 0; i3 < ttLen; i3++) {
            Log.d(TAG, "[" + i3 + "]->" + data[i3] + "\t");
        }
        HbbtvRet ret = exchangeData(13, data);
        if (ret != HbbtvRet.HBBTV_RET_OK) {
            Log.d(TAG, "after exchangeData, return failed");
            return ret;
        }
        Log.d(TAG, "after exchangeData");
        for (int i4 = 0; i4 < ttLen; i4++) {
            Log.d(TAG, "[" + i4 + "]->" + data[i4] + "\t");
        }
        int nmemb2 = data[0];
        ntotal[0] = data[1];
        Log.d(TAG, "after exchangeData, nmemb:" + nmemb2 + ", ntotal[0]:" + ntotal[0]);
        if (nmemb2 > ntotal[0]) {
            nmemb2 = ntotal[0];
        }
        int i5 = 0;
        while (i5 < nmemb2) {
            strmAudio[i5] = new MtkTvHbbTVStreamAudio();
            int pos = i2 + (i5 * 9);
            strmAudio[i5].index = data[pos + 0];
            strmAudio[i5].active = data[pos + 1] == 0 ? false : z;
            strmAudio[i5].audio_description = data[pos + 2] == 0 ? false : z;
            for (int j = 0; j < 4; j++) {
                strmAudio[i5].lang[j] = (char) data[pos + 3 + j];
            }
            strmAudio[i5].tag = ((long) data[pos + 7]) << ((int) (((long) data[(pos + 7) + 1]) + 32));
            Log.d(TAG, "strmAudio[" + i5 + "]{idex:" + strmAudio[i5].index + ", active:" + strmAudio[i5].active + ", ad:" + strmAudio[i5].audio_description + ", lang:" + strmAudio[i5].lang + ", tag:" + strmAudio[i5].tag + "}");
            i5++;
            i2 = 2;
            z = true;
        }
        return ret;
    }

    public HbbtvRet hbbtvAudioSetActive(MtkTvHbbTVStreamAudio[] strmAudio, int nmemb) {
        int i = nmemb;
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        if (i <= 0) {
            Log.d(TAG, "Invalid arg, nmemb:" + i);
            return HbbtvRet.HBBTV_RET_INTERNAL_ERROR;
        }
        int ttLen = (9 * i) + 2;
        int[] data = new int[ttLen];
        data[0] = i;
        data[1] = 0;
        for (int i2 = 0; i2 < i; i2++) {
            int pos = 2 + (i2 * 9);
            data[pos + 0] = strmAudio[i2].index;
            data[pos + 1] = strmAudio[i2].active ? 1 : 0;
            data[pos + 2] = strmAudio[i2].audio_description ? 1 : 0;
            for (int j = 0; j < 4; j++) {
                data[pos + 3 + j] = strmAudio[i2].lang[j];
            }
            data[pos + 7] = (int) (strmAudio[i2].tag >> 32);
            data[pos + 7 + 1] = (int) (strmAudio[i2].tag & -1);
        }
        Log.d(TAG, "before exchangeData");
        for (int i3 = 0; i3 < ttLen; i3++) {
            Log.d(TAG, "[" + i3 + "]->" + data[i3] + "\t");
        }
        HbbtvRet ret = exchangeData(14, data);
        if (ret == HbbtvRet.HBBTV_RET_OK) {
            Log.d(TAG, "after exchangeData");
        } else {
            Log.d(TAG, "after exchangeData, return failed");
        }
        return ret;
    }

    public HbbtvRet hbbtvSubtitleGetList(MtkTvHbbTVStreamSubtitle[] strmSbtl, int nmemb, int[] ntotal) {
        int i = nmemb;
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        if (i <= 0) {
            Log.d(TAG, "Invalid arg, nmemb:" + i);
            return HbbtvRet.HBBTV_RET_INTERNAL_ERROR;
        }
        int i2 = 2;
        int ttLen = (10 * i) + 2;
        int[] data = new int[ttLen];
        data[0] = i;
        boolean z = true;
        data[1] = ntotal[0];
        Log.d(TAG, "before exchangeData");
        for (int i3 = 0; i3 < ttLen; i3++) {
            Log.d(TAG, "[" + i3 + "]->" + data[i3] + "\t");
        }
        HbbtvRet ret = exchangeData(15, data);
        if (ret != HbbtvRet.HBBTV_RET_OK) {
            Log.d(TAG, "after exchangeData, return failed");
            return ret;
        }
        Log.d(TAG, "after exchangeData");
        for (int i4 = 0; i4 < ttLen; i4++) {
            Log.d(TAG, "[" + i4 + "]->" + data[i4] + "\t");
        }
        int nmemb2 = data[0];
        ntotal[0] = data[1];
        Log.d(TAG, "after exchangeData, nmemb:" + nmemb2 + ", ntotal[0]:" + ntotal[0]);
        if (nmemb2 > ntotal[0]) {
            nmemb2 = ntotal[0];
        }
        int i5 = 0;
        while (i5 < nmemb2) {
            strmSbtl[i5] = new MtkTvHbbTVStreamSubtitle();
            int pos = i2 + (i5 * 10);
            strmSbtl[i5].index = data[pos + 0];
            strmSbtl[i5].active = data[pos + 1] == 0 ? false : z;
            strmSbtl[i5].type = data[pos + 2];
            strmSbtl[i5].attr = data[pos + 3];
            for (int j = 0; j < 4; j++) {
                strmSbtl[i5].lang[j] = (char) data[pos + 4 + j];
            }
            strmSbtl[i5].tag = ((long) data[pos + 8]) << ((int) (((long) data[(pos + 8) + 1]) + 32));
            Log.d(TAG, "strmSbtl[" + i5 + "]{idex:" + strmSbtl[i5].index + ", active:" + strmSbtl[i5].active + ", type:" + strmSbtl[i5].type + ", attr:" + strmSbtl[i5].attr + ", lang:" + strmSbtl[i5].lang + ", tag:" + strmSbtl[i5].tag + "}");
            i5++;
            i2 = 2;
            z = true;
        }
        return ret;
    }

    public HbbtvRet hbbtvSubtitleSetActive(MtkTvHbbTVStreamSubtitle[] strmSbtl, int nmemb) {
        int i = nmemb;
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        if (i <= 0) {
            Log.d(TAG, "Invalid arg, nmemb:" + i);
            return HbbtvRet.HBBTV_RET_INTERNAL_ERROR;
        }
        int ttLen = (10 * i) + 2;
        int[] data = new int[ttLen];
        data[0] = i;
        data[1] = 0;
        for (int i2 = 0; i2 < i; i2++) {
            int pos = 2 + (i2 * 10);
            data[pos + 0] = strmSbtl[i2].index;
            data[pos + 1] = strmSbtl[i2].active ? 1 : 0;
            data[pos + 2] = strmSbtl[i2].type;
            data[pos + 3] = strmSbtl[i2].attr;
            for (int j = 0; j < 4; j++) {
                data[pos + 4 + j] = strmSbtl[i2].lang[j];
            }
            data[pos + 8] = (int) (strmSbtl[i2].tag >> 32);
            data[pos + 8 + 1] = (int) (strmSbtl[i2].tag & -1);
        }
        Log.d(TAG, "before exchangeData");
        for (int i3 = 0; i3 < ttLen; i3++) {
            Log.d(TAG, "[" + i3 + "]->" + data[i3] + "\t");
        }
        HbbtvRet ret = exchangeData(16, data);
        if (ret == HbbtvRet.HBBTV_RET_OK) {
            Log.d(TAG, "after exchangeData");
        } else {
            Log.d(TAG, "after exchangeData, reture failed");
        }
        return ret;
    }

    public HbbtvRet exchangeData(int exchangeType, int[] data) {
        Log.d(TAG, "exchangeData, exchangeType==" + exchangeType + ",data==" + data);
        HbbtvRet hbbtvRet = HbbtvRet.HBBTV_RET_OK;
        int[] payload = data;
        if (payload == null) {
            payload = new int[1];
        }
        int totalLen = payload.length + 2;
        if (totalLen > 200) {
            Log.d(TAG, "[Error]bigger than EXCHANGE_MAX_LEN\n");
            return HbbtvRet.HBBTV_RET_INTERNAL_ERROR;
        }
        int[] exchangeData = new int[totalLen];
        exchangeData[0] = totalLen;
        exchangeData[1] = exchangeType;
        int i = 0;
        for (int j = 2; j < totalLen; j++) {
            exchangeData[j] = payload[i];
            i++;
        }
        int retTVNativeWrapper = TVNativeWrapper.hbbtvExchangeData_native(exchangeData);
        Log.d(TAG, "retTVNativeWrapper:" + retTVNativeWrapper + "\n");
        int i2 = 0;
        for (int j2 = 2; j2 < totalLen; j2++) {
            payload[i2] = exchangeData[j2];
            i2++;
        }
        if (retTVNativeWrapper >= 0) {
            return HbbtvRet.HBBTV_RET_OK;
        }
        return HbbtvRet.HBBTV_RET_INTERNAL_ERROR;
    }
}
