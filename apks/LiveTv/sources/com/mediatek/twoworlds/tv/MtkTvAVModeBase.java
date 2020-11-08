package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvScreenModeOverscan;
import com.mediatek.twoworlds.tv.model.MtkTvVideoInfoBase;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import java.util.ArrayList;
import java.util.List;

public class MtkTvAVModeBase {
    public static final String TAG = "TV_MtkTvAVModeBase";
    public static final int VIDEOINFO_TYPE_AUTO_BACKLIGHT = 1;
    public static final int VIDEOINFO_TYPE_DISP_CTRL_ATTR_INFO = 6;
    public static final int VIDEOINFO_TYPE_HDMI_PC_TIMING = 4;
    public static final int VIDEOINFO_TYPE_HDR_EOTF_TYPE = 2;
    public static final int VIDEOINFO_TYPE_HISTOGRAM_CONTROL = 3;
    public static final int VIDEOINFO_TYPE_HISTOGRAM_DATA = 2;
    public static final int VIDEOINFO_TYPE_HISTOGRAM_INFO = 3;
    public static final int VIDEOINFO_TYPE_LUMA_STATISTICS = 4;
    public static final int VIDEOINFO_TYPE_SAT_BY_HUE_HISTOGRAM = 5;
    public static final int VIDEOINFO_TYPE_SAT_BY_HUE_WIN_RANGE = 1;
    public String audioString = "";

    public int[] getAllSoundEffect() {
        Log.d(TAG, "Enter getAllSoundEffect, directly return\n");
        return TVNativeWrapper.getAllSoundEffect_native();
    }

    public int setSoundEffect(int soundEffect) {
        Log.d(TAG, "Enter getAllSoundEffect, directly return\n");
        TVNativeWrapper.setSoundEffect_native(soundEffect);
        return 0;
    }

    public int getSoundEffect() {
        Log.d(TAG, "Enter getAllSoundEffect, directly return\n");
        return TVNativeWrapper.getSoundEffect_native();
    }

    public int[] getAllPictureMode() {
        Log.d(TAG, "Enter getAllPictureMode, directly return\n");
        return TVNativeWrapper.getAllPictureMode_native();
    }

    public int setPictureMode(int pictureMode) {
        Log.d(TAG, "Enter setPictureMode, directly return\n");
        TVNativeWrapper.setPictureMode_native(pictureMode);
        return 0;
    }

    public int getPictureMode() {
        Log.d(TAG, "Enter getPictureMode, directly return\n");
        return TVNativeWrapper.getPictureMode_native();
    }

    public enum ScreenModeType {
        SCRN_MODE_TYPE_UNKNOWN(0),
        SCRN_MODE_TYPE_NORMAL(1),
        SCRN_MODE_TYPE_LETTERBOX(2),
        SCRN_MODE_TYPE_PAN_SCAN(3),
        SCRN_MODE_TYPE_USER_DEFINED(4),
        SCRN_MODE_TYPE_NON_LINEAR_ZOOM(5),
        SCRN_MODE_TYPE_DOT_BY_DOT(6),
        SCRN_MODE_TYPE_CUSTOM_DEF_0(7),
        SCRN_MODE_TYPE_CUSTOM_DEF_1(8),
        SCRN_MODE_TYPE_CUSTOM_DEF_2(9),
        SCRN_MODE_TYPE_CUSTOM_DEF_3(10),
        SCRN_MODE_TYPE_CUSTOM_DEF_4(11),
        SCRN_MODE_TYPE_CUSTOM_DEF_5(12),
        SCRN_MODE_TYPE_CUSTOM_DEF_6(13),
        SCRN_MODE_TYPE_CUSTOM_DEF_7(14),
        SCRN_MODE_TYPE_NLZ_CUSTOM_DEF_0(15),
        SCRN_MODE_TYPE_NLZ_CUSTOM_DEF_1(16),
        SCRN_MODE_TYPE_NLZ_CUSTOM_DEF_2(17),
        SCRN_MODE_TYPE_NLZ_CUSTOM_DEF_3(18);
        
        private int mScreenMode;

        private ScreenModeType(int ScreenModeType) {
            this.mScreenMode = ScreenModeType;
        }

        public int getScreenMode() {
            return this.mScreenMode;
        }
    }

    public int[] getAllScreenMode() {
        Log.d(TAG, "Enter getAllScreenMode, directly return\n");
        return TVNativeWrapper.getAllScreenMode_native();
    }

    public int setScreenMode(int screenMode) {
        Log.d(TAG, "Enter setScreenMode\n");
        int ret = TVNativeWrapper.setScreenMode_native(screenMode);
        Log.d(TAG, "Leave setScreenMode\n");
        return ret;
    }

    public int getScreenMode() {
        Log.d(TAG, "Enter getScreenMode\n");
        int scrnMode = TVNativeWrapper.getScreenMode_native();
        Log.d(TAG, "Leave getScreenMode\n");
        return scrnMode;
    }

    public boolean isZoomEnable() {
        Log.d(TAG, "Enter isZoomEnable\n");
        boolean isEnable = TVNativeWrapper.isZoomEnable_native();
        Log.d(TAG, "Leave isZoomEnable\n");
        return isEnable;
    }

    public int setCrntOverscan(int top, int bottom, int left, int right) {
        Log.d(TAG, "Enter setCrntOverscan\n");
        int ret = TVNativeWrapper.setCrntOverscan_native(top, bottom, left, right);
        Log.d(TAG, "Leave setCrntOverscan\n");
        return ret;
    }

    public MtkTvScreenModeOverscan getCrntOverscan() {
        Log.d(TAG, "Enter getCrntOverscan\n");
        MtkTvScreenModeOverscan overscan = new MtkTvScreenModeOverscan();
        if (TVNativeWrapper.getCrntOverscan_native(overscan) == 0) {
            Log.d(TAG, "Enter getCrntOverscan success\n");
            return overscan;
        }
        Log.d(TAG, "Enter getCrntOverscan fail\n");
        return null;
    }

    public int updateOverscanIni() {
        Log.d(TAG, "Enter updateOverscanIni\n");
        int ret = TVNativeWrapper.updateOverscanIni_native();
        Log.d(TAG, "Leave updateOverscanIni\n");
        return ret;
    }

    public int setFreeze(String pass, boolean flag) {
        Log.d(TAG, "setFreeze\n");
        return TVNativeWrapper.setFreeze_native(pass, flag);
    }

    public boolean isFreeze(String pass) {
        Log.d(TAG, "isFreeze\n");
        return TVNativeWrapper.isFreeze_native(pass);
    }

    public int setNextAudioLang() {
        Log.d(TAG, "setNextAudioLang");
        return TVNativeWrapper.setNextAudioLang_native();
    }

    public String getAudioLang() {
        Log.d(TAG, "getAudioLang");
        TVNativeWrapper.getAudioLang_native(this);
        return this.audioString;
    }

    public List<TvProviderAudioTrackBase> getAudioAvailableRecord() {
        Log.d(TAG, "getAudioAvailableRecord");
        List<TvProviderAudioTrackBase> audiolist = new ArrayList<>();
        TVNativeWrapper.getAudioAvailableRecord_native(audiolist);
        return audiolist;
    }

    public boolean selectAudioById(String audioId) {
        int i4_ret;
        Log.d(TAG, "selectAudioById");
        if (audioId == null) {
            i4_ret = TVNativeWrapper.unselectAudio_native();
        } else {
            int stream_tag = Integer.parseInt(audioId);
            Log.d(TAG, "Enter selectAudioById Here.stream_tag :" + stream_tag + "audio_id:" + audioId);
            i4_ret = TVNativeWrapper.selectAudioById_native(stream_tag);
        }
        if (i4_ret == 0) {
            return true;
        }
        return false;
    }

    public List<TvProviderAudioTrackBase> getInputSourceAudioAvailableRecord() {
        Log.d(TAG, "getInputSourceAudioAvailableRecord");
        List<TvProviderAudioTrackBase> audiolist = new ArrayList<>();
        TVNativeWrapper.getInputSourceAudioAvailableRecord_native(audiolist);
        return audiolist;
    }

    public boolean selectMainSubAudioById(int mainAudioId, int subAudioId) {
        Log.d(TAG, "selectAudioById main:" + mainAudioId + " sub:" + subAudioId);
        if (TVNativeWrapper.selectMainSubAudioById_native(mainAudioId, subAudioId) == 0) {
            return true;
        }
        return false;
    }

    public TvProviderAudioTrackBase getCurrentAudio() {
        Log.d(TAG, "Enter getCurrentAudio Here.");
        TvProviderAudioTrackBase audio_info = new TvProviderAudioTrackBase();
        if (TVNativeWrapper.getCurrentAudio_native(audio_info) != 0) {
            return null;
        }
        Log.d(TAG, "Enter getCurrentAudio Here" + audio_info.toString());
        return audio_info;
    }

    public TvProviderAudioTrackBase getInputSourceCurrentAudio() {
        Log.d(TAG, "Enter getInputSourceCurrentAudio Here.");
        TvProviderAudioTrackBase audio_info = new TvProviderAudioTrackBase();
        if (TVNativeWrapper.getInputSourceCurrentAudio_native(audio_info) != 0) {
            return null;
        }
        Log.d(TAG, "Enter getInputSourceCurrentAudio Here" + audio_info.toString());
        return audio_info;
    }

    /* access modifiers changed from: protected */
    public void setAudioLang(String str) {
        Log.d(TAG, "setAudioLang =" + str);
        this.audioString = str;
    }

    public int getAudioDecType() {
        Log.i(TAG, "getAudioDecType =" + -1);
        return TVNativeWrapper.getAudioDecType_native();
    }

    public int setVideoInfoValue(int setType, int setVal) {
        Log.i(TAG, "setVideoInfoValue type=" + setType + " val=" + setVal);
        return TVNativeWrapper.setVideoInfoValue_native(setType, setVal);
    }

    public int getVideoInfoValue(int getType) {
        Log.i(TAG, "getVideoInfoValue type=" + getType);
        return TVNativeWrapper.getVideoInfoValue_native(getType);
    }

    public int setVideoInfoData(int setType, MtkTvVideoInfoBase videoInfoBase) {
        Log.i(TAG, "setVideoInfoData type=" + setType);
        return TVNativeWrapper.setVideoInfoData_native(setType, videoInfoBase);
    }

    public MtkTvVideoInfoBase getVideoInfoData(int setType, MtkTvVideoInfoBase videoInfoBase) {
        Log.i(TAG, "getVideoInfoData type=" + setType);
        int ret = TVNativeWrapper.getVideoInfoData_native(setType, videoInfoBase);
        return videoInfoBase;
    }
}
