package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvSubtitleBase {
    public static final int SUBTITLE_TRACK_INVALID = 255;
    public static final int SUBTITLE_TRACK_LANGUAGE_LENGTH = 4;
    private static final int SUBTITLE_TRACK_LIST_MAX_LEN = 256;
    private static final int SUBTITLE_TRACK_MAX_NUM = 128;
    public static final String TAG = "MtkTvSubtitleBase";
    public OneSubtitleTrack[] nfySubtitle_trackList = new OneSubtitleTrack[128];
    public int nfySubtitle_trackNum = 0;

    public enum SubtitleCallBackType {
        SUBTITLE_CALL_BACK_TYPE_TRACKS_UPDATE,
        SUBTITLE_CALL_BACK_TYPE_TRACK_INFO_SELECTED,
        SUBTITLE_CALL_BACK_TYPE_NUM
    }

    public enum SubtitleStreamType {
        SUBTITLE_STREAM_TYPE_UNKNOWN,
        SUBTITLE_STREAM_TYPE_SUBTITLE,
        SUBTITLE_STREAM_TYPE_TELTEXT
    }

    public MtkTvSubtitleBase() {
        Log.d(TAG, "MtkTvSubtitleBase object created");
    }

    public enum DealType {
        DEAL_TYPE_UNKNOWN(0),
        DEAL_TYPE_STOP_CURRENT(1);
        
        private int mDealType;

        private DealType(int edealType) {
            this.mDealType = edealType;
        }

        public int getDealType() {
            return this.mDealType;
        }
    }

    public int nextStream() {
        Log.d(TAG, "Enter nextStream\n");
        int ret = TVNativeWrapper.nextStream_native();
        Log.d(TAG, "Leave nextStream\n");
        return ret;
    }

    public int dealStream(int dealType) {
        Log.d(TAG, "Enter dealStream\n");
        Log.d(TAG, "Leave dealStream\n");
        return 0;
    }

    public int playStream(int trackId) {
        Log.d(TAG, "Enter playStream, trackid=" + trackId);
        int ret = TVNativeWrapper.playStream_native(trackId);
        Log.d(TAG, "Leave playStream\n");
        return ret;
    }

    public class OneSubtitleTrack {
        private static final int I4C_ONE_SUBTITLE_TRACK_LEN = 10;
        public Boolean trackHearingImpaired;
        public int trackId = 255;
        public int trackLangIdx;
        public String trackLanguage = "";
        public int trackTTXMagNum;
        public int trackTTXPageNum;
        public SubtitleStreamType trackType;

        public OneSubtitleTrack() {
        }
    }

    public synchronized int getTracks() {
        int[] data = new int[256];
        int[] intTrackLanguage = new int[4];
        Log.d(TAG, "Enter getTracks\n");
        int ret = TVNativeWrapper.SubtitleGetTracks_native(data);
        if (ret != 0) {
            Log.d(TAG, "Error getTracks\n");
            return ret;
        }
        int payloadIdx = 1;
        int payLoadLen = data[0];
        Log.d(TAG, "payLoadLen:" + payLoadLen + "\n");
        if (payLoadLen > 0) {
            this.nfySubtitle_trackNum = data[1];
            Log.d(TAG, "nfySubtitle_trackNum:" + this.nfySubtitle_trackNum + "\n");
            payLoadLen += -1;
            payloadIdx = 1 + 1;
        }
        if (this.nfySubtitle_trackNum > 0 && this.nfySubtitle_trackNum < 128) {
            int i = 0;
            while (i < this.nfySubtitle_trackNum && payLoadLen >= 10) {
                this.nfySubtitle_trackList[i] = new OneSubtitleTrack();
                int payloadIdx2 = payloadIdx + 1;
                this.nfySubtitle_trackList[i].trackId = data[payloadIdx];
                Log.d(TAG, "trackId:" + this.nfySubtitle_trackList[i].trackId + "\n");
                System.arraycopy(data, payloadIdx2, intTrackLanguage, 0, 4);
                this.nfySubtitle_trackList[i].trackLanguage = convertAsciiArrayToString(intTrackLanguage);
                Log.d(TAG, "trackLanguage:" + this.nfySubtitle_trackList[i].trackLanguage + "\n");
                int payloadIdx3 = payloadIdx2 + 4;
                int payloadIdx4 = payloadIdx3 + 1;
                this.nfySubtitle_trackList[i].trackLangIdx = data[payloadIdx3];
                Log.d(TAG, "trackLanguageIdx:" + this.nfySubtitle_trackList[i].trackLangIdx + "\n");
                if (data[payloadIdx4] == 0) {
                    this.nfySubtitle_trackList[i].trackHearingImpaired = false;
                } else {
                    this.nfySubtitle_trackList[i].trackHearingImpaired = true;
                }
                int payloadIdx5 = payloadIdx4 + 1;
                Log.d(TAG, "trackHearingImpaired:" + this.nfySubtitle_trackList[i].trackHearingImpaired + "\n");
                int payloadIdx6 = payloadIdx5 + 1;
                this.nfySubtitle_trackList[i].trackType = SubtitleStreamType.values()[data[payloadIdx5]];
                Log.d(TAG, "trackType:" + this.nfySubtitle_trackList[i].trackType + "\n");
                int payloadIdx7 = payloadIdx6 + 1;
                this.nfySubtitle_trackList[i].trackTTXMagNum = data[payloadIdx6];
                Log.d(TAG, "trackTTXMagNum:" + this.nfySubtitle_trackList[i].trackTTXMagNum + "\n");
                int payloadIdx8 = payloadIdx7 + 1;
                this.nfySubtitle_trackList[i].trackTTXPageNum = data[payloadIdx7];
                Log.d(TAG, "trackTTXPageNum:" + this.nfySubtitle_trackList[i].trackTTXPageNum + "\n");
                payLoadLen += -10;
                i++;
                payloadIdx = payloadIdx8;
            }
        }
        Log.d(TAG, "Leave getTracks\n");
        return ret;
    }

    private String convertAsciiArrayToString(int[] asciiArray) {
        StringBuilder stringBuilder = new StringBuilder();
        if (asciiArray == null) {
            return null;
        }
        for (int i : asciiArray) {
            stringBuilder.append((char) i);
        }
        return stringBuilder.toString();
    }
}
