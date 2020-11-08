package com.mediatek.twoworlds.tv.model;

public class TvProviderAudioTrackBase {
    public static final int AUD_DECODE_TYPE_AAC = 4;
    public static final int AUD_DECODE_TYPE_AC3 = 1;
    public static final int AUD_DECODE_TYPE_DTS_EXPRESS = 17;
    public static final int AUD_DECODE_TYPE_DTS_HS = 18;
    public static final int AUD_DECODE_TYPE_DTS_NORMAL = 16;
    public static final int AUD_DECODE_TYPE_EAC3 = 2;
    public static final int AUD_DECODE_TYPE_FLAC = 3;
    public static final int AUD_DECODE_TYPE_HEAAC = 5;
    public static final int AUD_DECODE_TYPE_HEAAC_V2 = 6;
    public static final int AUD_DECODE_TYPE_LPCM_ALAW = 8;
    public static final int AUD_DECODE_TYPE_LPCM_ULAW = 9;
    public static final int AUD_DECODE_TYPE_MPEG1_LAYER1 = 12;
    public static final int AUD_DECODE_TYPE_MPEG1_LAYER3 = 10;
    public static final int AUD_DECODE_TYPE_MPEG1_LAYR2 = 11;
    public static final int AUD_DECODE_TYPE_MPEG2_LAYER1 = 15;
    public static final int AUD_DECODE_TYPE_MPEG2_LAYER2 = 14;
    public static final int AUD_DECODE_TYPE_MPEG2_LAYER3 = 13;
    public static final int AUD_DECODE_TYPE_PCM = 7;
    public static final int AUD_DECODE_TYPE_UNKNOWN = 0;
    public static final int AUD_EDITORIAL_CLASS_HEARING_IMPAIRED_CLEAN = 3;
    public static final int AUD_EDITORIAL_CLASS_MAIN = 1;
    public static final int AUD_EDITORIAL_CLASS_RESERVED = 0;
    public static final int AUD_EDITORIAL_CLASS_VISUAL_IMPAIRED_AD = 2;
    public static final int AUD_EDITORIAL_CLASS_VISUAL_IMPAIRED_SPOKEN_SUBTITLE = 4;
    public static final int AUD_ENC_AAC = 7;
    public static final int AUD_ENC_AC3 = 1;
    public static final int AUD_ENC_AC4 = 26;
    public static final int AUD_ENC_ALAC = 24;
    public static final int AUD_ENC_AMR = 21;
    public static final int AUD_ENC_APE = 23;
    public static final int AUD_ENC_AWB = 20;
    public static final int AUD_ENC_COOK = 15;
    public static final int AUD_ENC_DRA = 16;
    public static final int AUD_ENC_DTS = 6;
    public static final int AUD_ENC_EU_CANAL_PLUS = 8;
    public static final int AUD_ENC_E_AC3 = 12;
    public static final int AUD_ENC_FLAC = 22;
    public static final int AUD_ENC_FM_RADIO = 14;
    public static final int AUD_ENC_LPCM = 13;
    public static final int AUD_ENC_MPEG_1 = 2;
    public static final int AUD_ENC_MPEG_2 = 3;
    public static final int AUD_ENC_OPUS = 25;
    public static final int AUD_ENC_PCM = 4;
    public static final int AUD_ENC_TV_SYS = 5;
    public static final int AUD_ENC_UNKNOWN = 0;
    public static final int AUD_ENC_VORBIS = 17;
    public static final int AUD_ENC_WMA_LOSSLESS = 19;
    public static final int AUD_ENC_WMA_PRO = 18;
    public static final int AUD_ENC_WMA_V1 = 9;
    public static final int AUD_ENC_WMA_V2 = 10;
    public static final int AUD_ENC_WMA_V3 = 11;
    public static final int AUD_FMT_DUAL_MONO = 2;
    public static final int AUD_FMT_MONO = 1;
    public static final int AUD_FMT_STEREO = 3;
    public static final int AUD_FMT_SUBSTREAM = 5;
    public static final int AUD_FMT_TYPE_5_1 = 4;
    public static final int AUD_FMT_UNKNOWN = 0;
    public static final int AUD_MIX_TYPE_INDEPENDENT = 2;
    public static final int AUD_MIX_TYPE_SUPPLEMENTARY = 1;
    public static final int AUD_MIX_TYPE_UNKNOWN = 0;
    public static final int AUD_TYPE_CLEAN = 1;
    public static final int AUD_TYPE_COMMENTARY = 8;
    public static final int AUD_TYPE_COMPLETE_MAIN = 5;
    public static final int AUD_TYPE_DIALOGUE = 7;
    public static final int AUD_TYPE_EMERGENCY = 9;
    public static final int AUD_TYPE_HEARING_IMPAIRED = 2;
    public static final int AUD_TYPE_KARAOKE = 9;
    public static final int AUD_TYPE_MUSIC_AND_EFFECT = 6;
    public static final int AUD_TYPE_RESERVED = 4;
    public static final int AUD_TYPE_UNKNOWN = 0;
    public static final int AUD_TYPE_VISUAL_IMPAIRED = 3;
    public static final int AUD_TYPE_VOICE_OVER = 8;
    private static final String TAG = "TvProviderAudioTrack";
    private int mAudioAtmos;
    private int mAudioChannelCount;
    private int mAudioDecodeType;
    private int mAudioEditorialClass;
    private int mAudioEncodeType;
    private int mAudioFormat;
    private int mAudioId;
    private int mAudioIndex;
    private int mAudioIsMixedWithAD;
    private String mAudioLanguage;
    private int mAudioMixType;
    private int mAudioPid;
    private int mAudioSampleRate;
    private int mAudioType;

    public int getAudioChannelCount() {
        return this.mAudioChannelCount;
    }

    public void setAudioChannelCount(int mAudioChannelCount2) {
        this.mAudioChannelCount = mAudioChannelCount2;
    }

    public int getAudioSampleRate() {
        return this.mAudioSampleRate;
    }

    /* access modifiers changed from: protected */
    public void setAudioSampleRate(int mAudioSampleRate2) {
        this.mAudioSampleRate = mAudioSampleRate2;
    }

    public int getAudioId() {
        return this.mAudioId;
    }

    public void setAudioId(int mAudioId2) {
        this.mAudioId = mAudioId2;
    }

    public int getAudioType() {
        return this.mAudioType;
    }

    public void setAudioType(int mAudioType2) {
        this.mAudioType = mAudioType2;
    }

    public int getAudioMixType() {
        return this.mAudioMixType;
    }

    public void setAudioMixType(int mAudioMixType2) {
        this.mAudioMixType = mAudioMixType2;
    }

    public int getAudioEditorialClass() {
        return this.mAudioEditorialClass;
    }

    public void setAudioEditorialClass(int mAudioEditorialClass2) {
        this.mAudioEditorialClass = mAudioEditorialClass2;
    }

    public int getAudioEncodeType() {
        return this.mAudioEncodeType;
    }

    public void setAudioEncodeType(int mAudioEncodeType2) {
        this.mAudioEncodeType = mAudioEncodeType2;
    }

    public int getAudioIsMixedWithAD() {
        return this.mAudioIsMixedWithAD;
    }

    public void setAudioIsMixedWithAD(int mAudioIsMixedWithAD2) {
        this.mAudioIsMixedWithAD = mAudioIsMixedWithAD2;
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public void setAudioFormat(int mAudioFormat2) {
        this.mAudioFormat = mAudioFormat2;
    }

    public int getAudioIndex() {
        return this.mAudioIndex;
    }

    public void setAudioIndex(int mAudioIndex2) {
        this.mAudioIndex = mAudioIndex2;
    }

    public int getAudioPid() {
        return this.mAudioPid;
    }

    public void setAudioPid(int mAudioPid2) {
        this.mAudioPid = mAudioPid2;
    }

    public int getAudioDecodeType() {
        return this.mAudioDecodeType;
    }

    public void setAudioDecodeType(int mAudioDecodeType2) {
        this.mAudioDecodeType = mAudioDecodeType2;
    }

    public String getAudioLanguage() {
        return this.mAudioLanguage;
    }

    public void setAudioLanguage(String mAudioLanguage2) {
        this.mAudioLanguage = mAudioLanguage2;
    }

    public int getAudioAtmos() {
        return this.mAudioAtmos;
    }

    public void setAudioAtmos(int mAudioAtmos2) {
        this.mAudioAtmos = mAudioAtmos2;
    }

    public String toString() {
        return "TvProviderAudioTrack info mAudioId=" + this.mAudioId + " , mAudioLanguage=" + this.mAudioLanguage + " , mAudioChannelCount=" + this.mAudioChannelCount + " , mAudioSampleRate=" + this.mAudioSampleRate + ",mAudioType=" + this.mAudioType + ",mAudioMixType=" + this.mAudioMixType + ",mAudioEditorialClass=" + this.mAudioEditorialClass + "mAudioEncodeType=" + this.mAudioEncodeType + "mAudioDecodeType=" + this.mAudioDecodeType + "mAudioIsMixedWithAD=" + this.mAudioIsMixedWithAD + "mAudioFormat=" + this.mAudioFormat + "mAudioIndex=" + this.mAudioIndex + "mAudioPid=" + this.mAudioPid + "mAudioAtmos=" + this.mAudioAtmos + "\n";
    }
}
