package com.android.tv.settings.partnercustomizer.tvsettingservice;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScanDvbcBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Calendar;
import java.util.TimeZone;

public class TVSettingConfig {
    public static final String ACTION_DOVI_RESET = "com.android.tv.settings.partnercustomizer.tvsettingservice.DOVI_RESET";
    public static final String ACTION_NOTIFY_DOLBY_VISION = "com.android.tv.settings.partnercustomizer.tvsettingservice.NOTIFY_DOLBY_VISION";
    public static final String ACTION_NOTIFY_HDR_ENABLE = "com.android.tv.settings.partnercustomizer.tvsettingservice.NOTIFY_HDR_ENABLE";
    public static final String ACTION_NOTIFY_THREEDIM_ITEM_STATUS = "com.android.tv.settings.partnercustomizer.tvsettingservice.tvsettingservice.NOTIFY_THREEDIM_ITEM_STATUS";
    public static final String ACTION_NOTIFY_TVSETTING_VALUE = "com.android.tv.settings.partnercustomizer.tvsettingservice.NOTIFY_TVSETTING_VALUE";
    public static final String ACTION_SYNC_CONFIG_VALUE = "com.android.tv.settings.partnercustomizer.tvsettingservice.SYNC_CONFIG_VALUE";
    public static final String ADVANCED_VIDEO_4K_UHD_UPSCALING = "g_fusion_picture__4k_uhd_upscaling";
    public static final String AUDIO_IMPAIRED = "g_audio__aud_type";
    public static final String BACKLIGHT = "g_disp__disp_back_light";
    public static final String BLACK_BAR_DETECTION = "g_video__vid_black_bar_detect";
    public static final String BLUE_LIGHT = "g_video__vid_blue_light";
    public static final String BRIGHTNESS = "g_video__brightness";
    public static final String CFG_GRP_VIDEO_PREFIX = "g_video__";
    public static final String CFG_MENU_AUDIOINFO = "g_menu__audioinfo";
    public static final String CFG_MENU_AUDIOINFO_GET_CURRENT = "g_menu__audioinfocurrent";
    public static final String CFG_MENU_AUDIOINFO_GET_STRING = "audioinfogetstring";
    public static final String CFG_MENU_AUDIOINFO_GET_TOTAL = "g_menu__audioinfototal";
    public static final String CFG_MENU_AUDIOINFO_SET_DEINIT = "g_menu__audioinfodeinit";
    public static final String CFG_MENU_AUDIOINFO_SET_INIT = "g_menu__audioinfoinit";
    public static final String CFG_MENU_AUDIOINFO_SET_SELECT = "g_menu__audioinfoselect";
    public static final String CFG_VIDEO_VID_BLUE_LIGHT = "g_video__vid_blue_light";
    public static final String CFG_VIDEO_VID_HDR = "g_video__vid_hdr";
    public static final String COLOR_G_B = "g_video__clr_gain_b";
    public static final String COLOR_G_G = "g_video__clr_gain_g";
    public static final String COLOR_G_R = "g_video__clr_gain_r";
    public static final String COLOR_TEMPERATURE = "g_video__clr_temp";
    public static final String CONTRAST = "g_video__contrast";
    public static final String DYNAMIC_CONTRAST = "g_fusion_picture__dynamic_contrast";
    public static final String ENCODED_SURROUND_OUTPUT = "g_audio__aud_surround_ex";
    public static final String FILM_MODE = "g_fusion_picture__film_mode";
    public static final String GAMMA = "g_disp__disp_gamma";
    public static final String HDMI_EDID_INDEX = "g_menu_only__hdmi_edid_index";
    public static final String HDMI_EDID_VERSION = "g_fusion_picture__hdmi_edid_version";
    public static final String HDMI_RGB_RANGE = "g_fusion_picture__hdmi_rgb_range";
    public static final String HUE = "g_video__vid_hue";
    public static final String MACRO_DIMMING = "g_fusion_picture__macro_dimming";
    public static final String MPEG_ARTIFACT_REDUCTION = "g_fusion_picture__mpeg_art_reduction";
    public static final String NOISE_REDUCTION = "g_fusion_picture__noise_reduction";
    public static final String NOTIFY_SWITCH = "g_video__dovi_user_switch";
    public static final String PC_MODE = "g_fusion_picture__pc_mode";
    public static final String PICTURE_AUTO_BACKLIGHT = "g_video__auto_backlight_status";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_BLUE = "g_fusion_picture__color_tuner_bri_blue";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_CVAN = "g_fusion_picture__color_tuner_bri_cyan";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_FLESH_TONE = "g_fusion_picture__color_tuner_bri_flesh";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_GREEN = "g_fusion_picture__color_tuner_bri_green";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_MEGENTA = "g_fusion_picture__color_tuner_bri_magenta";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_RED = "g_fusion_picture__color_tuner_bri_red";
    public static final String PICTURE_COLOR_TUNE_BRIGHTNESS_YELLOW = "g_fusion_picture__color_tuner_bri_yellow";
    public static final String PICTURE_COLOR_TUNE_ENABLE = "g_fusion_picture__color_tuner_enable";
    public static final String PICTURE_COLOR_TUNE_GAIN_BLUE = "g_fusion_picture__color_tuner_gain_b";
    public static final String PICTURE_COLOR_TUNE_GAIN_GREEN = "g_fusion_picture__color_tuner_gain_g";
    public static final String PICTURE_COLOR_TUNE_GAIN_RED = "g_fusion_picture__color_tuner_gain_r";
    public static final String PICTURE_COLOR_TUNE_HUE_BLUE = "g_fusion_picture__color_tuner_hue_blue";
    public static final String PICTURE_COLOR_TUNE_HUE_CVAN = "g_fusion_picture__color_tuner_hue_cyan";
    public static final String PICTURE_COLOR_TUNE_HUE_FLESH_TONE = "g_fusion_picture__color_tuner_hue_flesh";
    public static final String PICTURE_COLOR_TUNE_HUE_GREEN = "g_fusion_picture__color_tuner_hue_green";
    public static final String PICTURE_COLOR_TUNE_HUE_MAGENTA = "g_fusion_picture__color_tuner_hue_magenta";
    public static final String PICTURE_COLOR_TUNE_HUE_RED = "g_fusion_picture__color_tuner_hue_red";
    public static final String PICTURE_COLOR_TUNE_HUE_YELLOW = "g_fusion_picture__color_tuner_hue_yellow";
    public static final String PICTURE_COLOR_TUNE_OFFSET_BLUE = "g_fusion_picture__color_tuner_offset_b";
    public static final String PICTURE_COLOR_TUNE_OFFSET_GREEN = "g_fusion_picture__color_tuner_offset_g";
    public static final String PICTURE_COLOR_TUNE_OFFSET_RED = "g_fusion_picture__color_tuner_offset_r";
    public static final String PICTURE_COLOR_TUNE_SATURATION_BLUE = "g_fusion_picture__color_tuner_sat_blue";
    public static final String PICTURE_COLOR_TUNE_SATURATION_CVAN = "g_fusion_picture__color_tuner_sat_cyan";
    public static final String PICTURE_COLOR_TUNE_SATURATION_FLESH_TONE = "g_fusion_picture__color_tuner_sat_flesh";
    public static final String PICTURE_COLOR_TUNE_SATURATION_GREEN = "g_fusion_picture__color_tuner_sat_green";
    public static final String PICTURE_COLOR_TUNE_SATURATION_MEGENTA = "g_fusion_picture__color_tuner_sat_magenta";
    public static final String PICTURE_COLOR_TUNE_SATURATION_RED = "g_fusion_picture__color_tuner_sat_red";
    public static final String PICTURE_COLOR_TUNE_SATURATION_YELLOW = "g_fusion_picture__color_tuner_sat_yellow";
    public static final String PICTURE_FORMAT = "g_fusion_picture__pictrue_format";
    public static final String PICTURE_MODE = "g_video__picture_mode";
    public static final String PICTURE_OFF = "g_audio__audio only";
    public static final String PICTURE_VGA_CLOCK = "g_vga__vga_clock";
    public static final String PICTURE_VGA_HP = "g_vga__vga_pos_h";
    public static final String PICTURE_VGA_PHASE = "g_vga__vga_phase";
    public static final String PICTURE_VGA_VP = "g_vga__vga_pos_v";
    public static final String PICTURE_WHITE_BALANCE11_BLUE = "g_fusion_picture__pq_wb_cor_blue";
    public static final String PICTURE_WHITE_BALANCE11_ENABLE = "g_fusion_picture__pq_wb_cor_enable";
    public static final String PICTURE_WHITE_BALANCE11_GAIN = "g_fusion_picture__pq_wb_cor_gain";
    public static final String PICTURE_WHITE_BALANCE11_GREEN = "g_fusion_picture__pq_wb_cor_green";
    public static final String PICTURE_WHITE_BALANCE11_RED = "g_fusion_picture__pq_wb_cor_red";
    public static final String RESET_SETTING = "g_video__dovi_reset_pic_setting";
    public static final String RETAIL_MODEL_PQ_DEMO = "g_video__vid_pq_demo";
    public static final String SATURATION = "g_video__vid_sat";
    public static final String SETUP_ANALOG_CAPTION = "g_cc__cc_analog_cc";
    public static final String SETUP_BACKGROUND_COLOR = "g_cc__dis_op_bk_color";
    public static final String SETUP_BACKGROUND_OPACITY = "g_cc__dis_op_bk_opacity";
    public static final String SETUP_CAPTION_DISPLAY = "g_cc__cc_enable";
    public static final String SETUP_CAPTION_STYLE = "g_cc__dcs";
    public static final String SETUP_DIGITAL_CAPTION = "g_cc__cc_digital_cc";
    public static final String SETUP_EDGE_COLOR = "g_cc__dis_op_eg_color";
    public static final String SETUP_EDGE_TYPE = "g_cc__dis_op_eg_type";
    public static final String SETUP_ENABLE_CAPTION = "g_cc__cc_caption";
    public static final String SETUP_FONT_COLOR = "g_cc__dis_op_ft_color";
    public static final String SETUP_FONT_OPACITY = "g_cc__dis_op_ft_opacity";
    public static final String SETUP_FONT_SIZE = "g_cc__dis_op_ft_size";
    public static final String SETUP_FONT_STYLE = "g_cc__dis_op_ft_style";
    public static final String SETUP_SUPERIMPOSE_SETUP = "g_cc__cc_si";
    public static final String SETUP_WINDOW_COLOR = "g_cc__dis_op_win_color";
    public static final String SETUP_WINDOW_OPACITY = "g_cc__dis_op_win_opacity";
    public static final String SETUP_WOL = "WOL";
    public static final String SETUP_WOW = "WOW";
    public static final String SHARPNESS = "g_video__vid_shp";
    public static final String SOUND_ABSOLUTE_3D_SOUND = "g_fusion_sound__absl_3d_sound";
    public static final String SOUND_ADVANCED_DIALOGUE_ENHANCER = "g_audio__dialogue_enhancer";
    public static final String SOUND_ADVANCED_DOLBY_ATMOS = "g_audio__dolby_atmos";
    public static final String SOUND_ADVANCED_DOLBY_AUDIO = "g_audio__dolby_audio_processing";
    public static final String SOUND_ADVANCED_SOUND_MODE = "g_audio__dolby_sound_mode";
    public static final String SOUND_ADVANCED_SURROUND_VIRTUALIZER = "g_audio__dolby_surround_virtualizer";
    public static final String SOUND_AUTO_VOLUME_CONTROL = "g_audio__agc";
    public static final String SOUND_AUTO_VOLUME_LEVELING = "g_fusion_sound__auto_volume_leveling";
    public static final String SOUND_BALANCE = "g_audio__aud_balance";
    public static final String SOUND_BASS = "g_audio__aud_bass";
    public static final String SOUND_BASS_ENHANCEMENT = "g_fusion_sound__bass_enhancement";
    public static final String SOUND_DBX_ENABLE = "g_fusion_sound__dbx_enable";
    public static final String SOUND_DBX_TOTAL_SONIC = "g_fusion_sound__total_sonic";
    public static final String SOUND_DBX_TOTAL_SURROUND = "g_fusion_sound__total_surround";
    public static final String SOUND_DBX_TOTAL_VOL = "g_fusion_sound__total_volume";
    public static final String SOUND_DELTA_VOLUME = "g_fusion_sound__delta_volume";
    public static final String SOUND_DIALOG_ENHANCEMENT = "g_fusion_sound__dialog_enhancement";
    public static final String SOUND_DIGITAL_OUTPUT = "g_fusion_sound__digital_autio_output";
    public static final String SOUND_DOWNMIX_MODE = "g_audio__dolby_dmix";
    public static final String SOUND_DTSSS2_ENABLE = "g_fusion_sound__dtsss2_enable";
    public static final String SOUND_DTSSS2_SURROUND = "g_fusion_sound__dtsss2_surround";
    public static final String SOUND_DTSSS2_TRUEVOLUME = "g_fusion_sound__dtsss2_true_volume";
    public static final String SOUND_DTS_DRC = "g_fusion_sound__dts_drc";
    public static final String SOUND_EQUALIZER = "g_audio__aud_equalizer";
    public static final String SOUND_EQUALIZER_DETAIL_10000HZ = "g_fusion_sound__equalizer_10khz";
    public static final String SOUND_EQUALIZER_DETAIL_120HZ = "g_fusion_sound__equalizer_120hz";
    public static final String SOUND_EQUALIZER_DETAIL_1500HZ = "g_fusion_sound__equalizer_1.5khz";
    public static final String SOUND_EQUALIZER_DETAIL_5000HZ = "g_fusion_sound__equalizer_5khz";
    public static final String SOUND_EQUALIZER_DETAIL_500HZ = "g_fusion_sound__equalizer_500hz";
    public static final String SOUND_LIMITER = "g_audio__dts_limiter";
    public static final String SOUND_MEDIA_ENABLED = "g_audio__agc";
    public static final String SOUND_SONIC_EMOTION = "g_fusion_sound__sonic_emosion";
    public static final String SOUND_SOUND_SURROUND = "g_audio__aud_surround";
    public static final String SOUND_SPDIF_DELAY = "g_audio__spdif_delay";
    public static final String SOUND_SPDIF_TYPE = "g_audio__spdif";
    public static final String SOUND_SPEAKER = "g_audio__aud_out_port";
    public static final String SOUND_SPEAKERS = "g_fusion_sound__tv_speakers";
    public static final String SOUND_STYLE = "g_audio__sound_mode";
    public static final String SOUND_TBHDX = "g_audio__dts_tbhdx";
    public static final String SOUND_TREBLE = "g_audio__aud_treble";
    public static final String SOUND_VIRTUAL_X = "g_audio__dts_virtual_x";
    public static final String SOUND__ADVANCED_VOLUME_LEVELER = "g_audio__volume_leveler";
    private static final String TAG = "TVSettingConfig";
    public static final String TIMER1 = "SETUP_timer1";
    public static final String TIMER2 = "SETUP_timer2";
    public static final String TIME_DATE = "SETUP_date";
    public static final String TIME_TIME = "SETUP_time";
    public static final String VIDEO_3D = "g_video__vid_3d_item";
    public static final String VIDEO_3D_3T2 = "g_video__vid_3d_to_2d";
    public static final String VIDEO_3D_DISTANCE = "g_video__vid_3d_distance";
    public static final String VIDEO_3D_DNR = "g_video__vid_3d_nr";
    public static final String VIDEO_3D_FIELD = "g_video__vid_3d_fld_depth";
    public static final String VIDEO_3D_IMG_SFTY = "g_video__vid_3d_img_sfty";
    public static final String VIDEO_3D_LF = "g_video__vid_3d_lr_switch";
    public static final String VIDEO_3D_MODE = "g_video__vid_3d_mode";
    public static final String VIDEO_3D_NAV = "g_video__vid_3d_nav_auto";
    public static final String VIDEO_3D_OSD_DEPTH = "g_video__vid_3d_osd_depth";
    public static final String VIDEO_3D_PROTRUDE = "g_video__vid_3d_protruden";
    public static final String VIDEO_ADAPTIVE_LUMA_CONTROL = "g_video__vid_luma";
    public static final String VIDEO_BLUE_STRETCH = "g_video__vid_blue_stretch";
    public static final String VIDEO_DI_FILM_MODE = "g_video__vid_di_film_mode";
    public static final String VIDEO_DNR = "g_video__vid_nr";
    public static final String VIDEO_DYNAMIC_CONTRAST = "g_fusion_picture__dynamic_contrast";
    public static final String VIDEO_FLESH_TONE = "g_video__vid_flash_tone";
    public static final String VIDEO_GAME_MODE = "g_video__vid_game_mode";
    public static final String VIDEO_HDR = "g_video__vid_hdr";
    public static final String VIDEO_LOCAL_CONTRAST_CONTROL = "g_video__vid_local_contrast";
    public static final String VIDEO_MJC_DEMO_PARTITION = "g_video__vid_mjc_demo";
    public static final String VIDEO_MJC_EFFECT = "g_video__vid_mjc_effect";
    public static final String VIDEO_MJC_MODE = "g_video__vid_mjc_mode";
    public static final String VIDEO_MPEG_NR = "g_video__vid_mpeg_nr";
    public static final String VIDEO_SUPER_RESOLUTION = "g_video__vid_super_resolution";
    public static final String VISUALLY_AC$_DE = "g_audio__aud_ac4_de_gain";
    public static final String VISUALLY_FADER_CONTROL = "g_audio__aud_ad_fader_ctrl";
    public static final String VISUALLY_HEADPHONE = "g_audio__aud_ad_hdphone";
    public static final String VISUALLY_IMPAIRED_AUDIO = "VISUALLY_IMPAIRED_AUDIO";
    public static final String VISUALLY_PAN_FADE = "g_audio__aud_ad_fade_pan";
    public static final String VISUALLY_SPEAKER = "g_audio__aud_ad_speaker";
    public static final String VISUALLY_VOLUME = "g_audio__aud_ad_volume";
    private static TVSettingConfig mInstance;
    private Context mContext;
    private MtkTvConfig mTvConfig = MtkTvConfig.getInstance();

    private TVSettingConfig(Context context) {
        this.mContext = context;
    }

    public static synchronized TVSettingConfig getInstance(Context context) {
        TVSettingConfig tVSettingConfig;
        synchronized (TVSettingConfig.class) {
            if (mInstance == null) {
                mInstance = new TVSettingConfig(context);
            }
            tVSettingConfig = mInstance;
        }
        return tVSettingConfig;
    }

    public int setConifg(String configId, int value) {
        Log.d(TAG, "setConfigValue is configId " + configId + " value " + value);
        this.mTvConfig.setConfigValue(configId, value);
        return 0;
    }

    public void setConfigValue(String cfgId, int value, boolean isUpate) {
        MtkLog.d(TAG, "setConfigValue cfgId:" + cfgId + "----value:" + value);
        int update = 0;
        if (isUpate) {
            update = 1;
        }
        this.mTvConfig.setConfigValue(cfgId, value, update);
    }

    public int getConfigValueInt(String configId) {
        int value = this.mTvConfig.getConfigValue(configId);
        Log.d(TAG, "getConfigValueInt configId:" + configId + "  value:" + value);
        return value;
    }

    public String getConfigValueString(String configId) {
        int value = this.mTvConfig.getConfigValue(configId);
        Log.d(TAG, "getConfigValueString configId:" + configId + "  value:" + value);
        return String.valueOf(value);
    }

    public String getConfigString(String configId) {
        String value = this.mTvConfig.getConfigString(configId);
        Log.d(TAG, "getConfigValueString configId:" + configId + "  value:" + value);
        return String.valueOf(value);
    }

    public boolean isConfigVisible(String cfgid) {
        boolean flag;
        if (this.mTvConfig.isConfigVisible(cfgid) == 0) {
            flag = true;
        } else {
            flag = false;
        }
        Log.d(TAG, "isConfigVisible " + cfgid + " " + flag);
        return flag;
    }

    public boolean isConfigEnabled(String cfgId) {
        return this.mTvConfig.isConfigEnabled(cfgId) == 0;
    }

    public int getMin(String itemID) {
        return MtkTvConfig.getMinValue(this.mTvConfig.getMinMaxConfigValue(itemID));
    }

    public int getMax(String itemID) {
        return MtkTvConfig.getMaxValue(this.mTvConfig.getMinMaxConfigValue(itemID));
    }

    public void destory() {
        this.mTvConfig = null;
        this.mContext = null;
        mInstance = null;
    }

    public void updatePowerOn(String cfgID, int enable, String date) {
        Log.d(TAG, "power on date:" + date);
        if (!TextUtils.isEmpty(date) && date.length() >= 5) {
            int timerValue = (((enable & 1) << 31) & Integer.MIN_VALUE) | (131071 & onTimeModified(date));
            Log.d("TVContent", "timerValue:" + timerValue + "cfgID:" + cfgID);
            this.mTvConfig.setConfigValue(cfgID, timerValue);
        }
    }

    public void updatePowerOff(String cfgID, int enable, String date) {
        Log.d(TAG, "power Off date:" + date);
        if (!TextUtils.isEmpty(date) && date.length() >= 5) {
            int timerValue = (((enable & 1) << 31) & Integer.MIN_VALUE) | (131071 & onTimeModified(date));
            Log.d("TVContent", "timerValue:" + timerValue + "cfgID:" + cfgID);
            this.mTvConfig.setConfigValue(cfgID, timerValue);
        }
    }

    public int onTimeModified(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        if ("0".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
            TimeZone tz = TimeZone.getTimeZone("Etc/UTC");
            Calendar cal = Calendar.getInstance();
            cal.set(11, hour);
            cal.set(12, minute);
            cal.add(11, 0 - ((tz.getRawOffset() / 3600) / 1000));
            long mills = cal.getTimeInMillis();
            MtkTvTimeFormatBase tb = new MtkTvTimeFormatBase();
            tb.setByUtcAndConvertToLocalTime(mills / 1000);
            MtkLog.d("{DT}{onTimeModified}", "Hour:" + tb.hour + "  Min:" + tb.minute);
            hour = tb.hour;
            minute = tb.minute;
        }
        return (hour * 3600) + (minute * 60);
    }

    public int getSleepTimerRemaining() {
        return MtkTvTime.getInstance().getSleepTimerRemainingTime();
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setSleepTimer(boolean r11) {
        /*
            r10 = this;
            java.lang.String r0 = "TVContent"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "direction:"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 0
            int r1 = r10.getSleepTimerRemaining()
            com.mediatek.twoworlds.tv.MtkTvTime r2 = com.mediatek.twoworlds.tv.MtkTvTime.getInstance()
            int r2 = r2.getSleepTimer(r11)
            java.lang.String r3 = "TVContent"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "leftmill:"
            r4.append(r5)
            r4.append(r1)
            java.lang.String r5 = ",mill=="
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            if (r1 <= 0) goto L_0x0145
            int r3 = r1 / 60
            r4 = 9
            r5 = 1
            if (r3 < r5) goto L_0x004f
            if (r3 >= r4) goto L_0x004f
            r0 = r11
            goto L_0x0126
        L_0x004f:
            r6 = 0
            r7 = 11
            r8 = 2
            if (r3 < r4) goto L_0x005e
            if (r3 >= r7) goto L_0x005e
            if (r11 == 0) goto L_0x005b
            r6 = r8
        L_0x005b:
            r0 = r6
            goto L_0x0126
        L_0x005e:
            r4 = 19
            if (r3 < r7) goto L_0x006b
            if (r3 >= r4) goto L_0x006b
            if (r11 == 0) goto L_0x0068
            r5 = r8
        L_0x0068:
            r0 = r5
            goto L_0x0126
        L_0x006b:
            r7 = 21
            r9 = 3
            if (r3 < r4) goto L_0x0079
            if (r3 >= r7) goto L_0x0079
            if (r11 == 0) goto L_0x0076
            r5 = r9
        L_0x0076:
            r0 = r5
            goto L_0x0126
        L_0x0079:
            r4 = 29
            if (r3 < r7) goto L_0x0086
            if (r3 >= r4) goto L_0x0086
            if (r11 == 0) goto L_0x0083
            r8 = r9
        L_0x0083:
            r0 = r8
            goto L_0x0126
        L_0x0086:
            r5 = 31
            r7 = 4
            if (r3 < r4) goto L_0x0094
            if (r3 >= r5) goto L_0x0094
            if (r11 == 0) goto L_0x0090
            goto L_0x0091
        L_0x0090:
            r7 = r8
        L_0x0091:
            r0 = r7
            goto L_0x0126
        L_0x0094:
            if (r3 < r5) goto L_0x00a1
            r4 = 39
            if (r3 >= r4) goto L_0x00a1
            if (r11 == 0) goto L_0x009d
            goto L_0x009e
        L_0x009d:
            r7 = r9
        L_0x009e:
            r0 = r7
            goto L_0x0126
        L_0x00a1:
            r4 = 39
            r5 = 5
            if (r3 < r4) goto L_0x00b1
            r4 = 41
            if (r3 >= r4) goto L_0x00b1
            if (r11 == 0) goto L_0x00ad
            goto L_0x00ae
        L_0x00ad:
            r5 = r9
        L_0x00ae:
            r0 = r5
            goto L_0x0126
        L_0x00b1:
            r4 = 41
            if (r3 < r4) goto L_0x00c0
            r4 = 49
            if (r3 >= r4) goto L_0x00c0
            if (r11 == 0) goto L_0x00bc
            goto L_0x00bd
        L_0x00bc:
            r5 = r7
        L_0x00bd:
            r0 = r5
            goto L_0x0126
        L_0x00c0:
            r4 = 49
            r8 = 6
            if (r3 < r4) goto L_0x00d0
            r4 = 51
            if (r3 >= r4) goto L_0x00d0
            if (r11 == 0) goto L_0x00cd
            r7 = r8
        L_0x00cd:
            r0 = r7
            goto L_0x0126
        L_0x00d0:
            r4 = 51
            if (r3 < r4) goto L_0x00de
            r4 = 59
            if (r3 >= r4) goto L_0x00de
            if (r11 == 0) goto L_0x00dc
            r5 = r8
        L_0x00dc:
            r0 = r5
            goto L_0x0126
        L_0x00de:
            r4 = 59
            r7 = 7
            if (r3 < r4) goto L_0x00ed
            r4 = 61
            if (r3 >= r4) goto L_0x00ed
            if (r11 == 0) goto L_0x00eb
            r5 = r7
        L_0x00eb:
            r0 = r5
            goto L_0x0126
        L_0x00ed:
            r4 = 61
            if (r3 < r4) goto L_0x00fb
            r4 = 89
            if (r3 >= r4) goto L_0x00fb
            if (r11 == 0) goto L_0x00f8
            goto L_0x00f9
        L_0x00f8:
            r7 = r8
        L_0x00f9:
            r0 = r7
            goto L_0x0126
        L_0x00fb:
            r4 = 89
            if (r3 < r4) goto L_0x010a
            r4 = 91
            if (r3 >= r4) goto L_0x010a
            if (r11 == 0) goto L_0x0108
            r8 = 8
        L_0x0108:
            r0 = r8
            goto L_0x0126
        L_0x010a:
            r4 = 91
            if (r3 < r4) goto L_0x0119
            r4 = 119(0x77, float:1.67E-43)
            if (r3 >= r4) goto L_0x0119
            if (r11 == 0) goto L_0x0117
            r7 = 8
        L_0x0117:
            r0 = r7
            goto L_0x0126
        L_0x0119:
            r4 = 119(0x77, float:1.67E-43)
            if (r3 < r4) goto L_0x0126
            r4 = 120(0x78, float:1.68E-43)
            if (r3 > r4) goto L_0x0126
            if (r11 == 0) goto L_0x0124
            goto L_0x0125
        L_0x0124:
            r6 = r7
        L_0x0125:
            r0 = r6
        L_0x0126:
            java.lang.String r4 = "TVContent"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "minute:"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r6 = "valueIndex:"
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            goto L_0x017a
        L_0x0145:
            int r3 = r2 / 60
            r4 = 10
            if (r3 == r4) goto L_0x0178
            r4 = 20
            if (r3 == r4) goto L_0x0176
            r4 = 30
            if (r3 == r4) goto L_0x0174
            r4 = 40
            if (r3 == r4) goto L_0x0172
            r4 = 50
            if (r3 == r4) goto L_0x0170
            r4 = 60
            if (r3 == r4) goto L_0x016e
            r4 = 90
            if (r3 == r4) goto L_0x016c
            r4 = 120(0x78, float:1.68E-43)
            if (r3 == r4) goto L_0x0169
            r0 = 0
            goto L_0x017a
        L_0x0169:
            r0 = 8
            goto L_0x017a
        L_0x016c:
            r0 = 7
            goto L_0x017a
        L_0x016e:
            r0 = 6
            goto L_0x017a
        L_0x0170:
            r0 = 5
            goto L_0x017a
        L_0x0172:
            r0 = 4
            goto L_0x017a
        L_0x0174:
            r0 = 3
            goto L_0x017a
        L_0x0176:
            r0 = 2
            goto L_0x017a
        L_0x0178:
            r0 = 1
        L_0x017a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig.setSleepTimer(boolean):int");
    }

    public int getTimerDefaultValue(String itemID) {
        int value = getConfigValueInt(itemID);
        if (itemID.equals(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON)) {
            if (value < 0) {
                if (this.mTvConfig.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE) < 0) {
                    return 2;
                }
                return 1;
            } else if (this.mTvConfig.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE) < 0) {
                return 2;
            } else {
                return 0;
            }
        } else if (!itemID.equals(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF)) {
            return value;
        } else {
            if (value < 0) {
                if (this.mTvConfig.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE) < 0) {
                    return 2;
                }
                return 1;
            } else if (this.mTvConfig.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE) < 0) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    public int getIndexByLeftTime(Long timeLeft) {
        if (timeLeft.longValue() < 10) {
            return 0;
        }
        if (timeLeft.longValue() < 20) {
            return 1;
        }
        if (timeLeft.longValue() < 30) {
            return 2;
        }
        if (timeLeft.longValue() < 40) {
            return 3;
        }
        if (timeLeft.longValue() < 50) {
            return 4;
        }
        if (timeLeft.longValue() < 60) {
            return 5;
        }
        if (timeLeft.longValue() < 90) {
            return 6;
        }
        if (timeLeft.longValue() < 120) {
            return 7;
        }
        return 8;
    }

    public boolean isSignalLoss() {
        boolean isSignalLoss = MtkTvBroadcast.getInstance().isSignalLoss();
        MtkLog.d(TAG, "isSignalLoss = " + isSignalLoss);
        return isSignalLoss;
    }

    public boolean isPictureOff() {
        int isPictureOff = this.mTvConfig.getConfigValue("g_audio__audio only");
        MtkLog.d(TAG, "isPictureOff,isPictureOff==" + isPictureOff);
        if (isPictureOff == 0) {
            return true;
        }
        return false;
    }

    public boolean isDolbyVision() {
        int cur = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_GRP_DOVI_INFO);
        Log.i(TAG, "isDolbyVision,cur==" + cur);
        if (cur != 0) {
            return true;
        }
        return false;
    }

    public boolean isInLiveTV() {
        int mmpMode = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE);
        Log.i(TAG, "isInLiveTV,mmpMode==" + mmpMode);
        if (mmpMode == 0) {
            return true;
        }
        return false;
    }

    public void resetAudioPref() {
        setConifg(MtkTvConfigTypeBase.CFG_FUSION_AUD_RESET_DEF, 0);
    }

    public void resetVideoPref() {
        setConifg(MtkTvConfigTypeBase.CFG_FUSION_PIC_RESET_DEF, 0);
    }

    public boolean isDVBCOperatorZiggo() {
        boolean isNLD = this.mTvConfig.getCountry().equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_NLD);
        boolean isZiggo = this.mTvConfig.getConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_CABLE_BRDCSTER) == MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_ZIGGO.ordinal();
        MtkLog.d(TAG, "isNLD:" + isNLD + " isZiggo:" + isZiggo);
        if (!isNLD || !isZiggo) {
            return false;
        }
        return true;
    }

    public boolean isCountryRus() {
        return this.mTvConfig.getCountry().equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_RUS);
    }

    public int getDolbyAtmosType() {
        return getConfigValueInt(MtkTvConfigTypeBase.CFG_AUD_DOLBY_INFO);
    }
}
