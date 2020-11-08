package com.android.tv.settings.partnercustomizer.tvsettingservice.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.utils.ProgressPreference;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.Locale;

public class PreferenceConfigUtils {
    public static final String KEY_ABSOLUTE_3D_SOUND = "sound_absolute_3d_sound";
    public static final String KEY_AUDIO = "audio_hbbtv";
    public static final String KEY_AUTO_VOLUME_LEVELING = "sound_auto_volume_leveling";
    public static final String KEY_BASS_ENHANCEMENT = "sound_bass_enhancement";
    public static final String KEY_DAP = "sound_dap";
    public static final String KEY_DBX = "sound_dbx";
    public static final String KEY_DELTA_VOLUME = "sound_delta_volume";
    public static final String KEY_DEVICE_ACC_VISUALLY_AC4DE = "ac4_de";
    public static final String KEY_DEVICE_ACC_VISUALLY_FADER_CONTROL = "visually_fader_control";
    public static final String KEY_DEVICE_ACC_VISUALLY_HEADPHONE = "visually_headphone";
    public static final String KEY_DEVICE_ACC_VISUALLY_PANE = "visually_pane_fade";
    public static final String KEY_DEVICE_ACC_VISUALLY_SPEAKER = "visually_speaker";
    public static final String KEY_DEVICE_ACC_VISUALLY_VISUALLY_AUDIO = "visually_impaired_audio";
    public static final String KEY_DEVICE_ACC_VISUALLY_VOLUME = "visually_volume";
    public static final String KEY_DEVICE_INPUTS_CECLIST = "cec_dev_list";
    public static final String KEY_DEVICE_RETAILDEMO = "RetailDemo";
    public static final String KEY_DEVICE_RETAILMODE_ENABLE = "RetailModeEnable";
    public static final String KEY_DEVICE_RETAILMODE_MESSAGE = "RetailMessaging";
    public static final String KEY_DIALOG_ENHANCEMENT = "sound_dialog_enhancement";
    public static final String KEY_DIGITAL_OUTPUT = "sound_digital_output";
    public static final String KEY_DIGITAL_OUTPUT_TO_DEVICE = "sound_digital_output_to_device";
    public static final String KEY_EQUALIZER_DETAIL_10000HZ = "sound_equalizer_detail_10000hz";
    public static final String KEY_EQUALIZER_DETAIL_120HZ = "sound_equalizer_detail_120hz";
    public static final String KEY_EQUALIZER_DETAIL_1500HZ = "sound_equalizer_detail_1500hz";
    public static final String KEY_EQUALIZER_DETAIL_5000HZ = "sound_equalizer_detail_5000hz";
    public static final String KEY_EQUALIZER_DETAIL_500HZ = "sound_equalizer_detail_500hz";
    public static final String KEY_PICTURE = "picture_effects";
    public static final String KEY_PICTURE_ADVANCED_VIDEO = "picture_advanced_video";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_4K_UHD_UPSCALING = "tv_picture_video_4k_uhd_upscaling";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL = "tv_picture_video_adaptive_luma_control";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_BLUE_STRETCH = "tv_picture_video_blue_stretch";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_DI_FILM_MODE = "tv_picture_video_di_film_mode";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_DNR = "tv_picture_advance_video_dnr";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_FLESH_TONE = "tv_picture_video_flesh_tone";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_GAME_MODE = "tv_picture_video_game_mode";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_HDMI_RGB_RANGE = "tv_picture_video_hdmi_rgb_range";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_LOCAL_CONTRAST_CONTROL = "tv_picture_video_local_contrast_control";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_MJC = "tv_picture_advanced_video_MJC";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO = "picture_advanced_video_MJC_demo";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION = "tv_picture_video_mjc_demo_partition";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT = "tv_picture_advance_video_mjc_effect";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_MPEG_NR = "tv_picture_advance_video_mpeg_nr";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_NOISE_REDUCTION = "tv_picture_video_noise_reduction";
    public static final String KEY_PICTURE_ADVANCE_VIDEO_PC_MODE = "tv_picture_video_pc_mode";
    public static final String KEY_PICTURE_AUTO_BACKLIGHT = "picture_auto_backlight";
    public static final String KEY_PICTURE_BACKLIGHT = "picture_backlight";
    public static final String KEY_PICTURE_BLUELIGHT = "picture_blue_light";
    public static final String KEY_PICTURE_BLUE_GAIN = "picture_blue_gain";
    public static final String KEY_PICTURE_BRIGHTNESS = "picture_brightness";
    public static final String KEY_PICTURE_COLOR_TEMP = "picture_color_temperature";
    public static final String KEY_PICTURE_COLOR_TEMPERATURE = "picture_color_temperature";
    public static final String KEY_PICTURE_COLOR_TUNER = "picture_color_tune";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS = "tv_picture_color_tune_brightness";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_BLUE = "tv_picture_color_tune_brightness_blue";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_CVAN = "tv_picture_color_tune_brightness_cvan";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_FLESH_TONE = "tv_picture_color_tune_brightness_flesh_tone";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_GREEN = "tv_picture_color_tune_brightness_green";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_MEGENTA = "tv_picture_color_tune_brightness_megenta";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_RED = "tv_picture_color_tune_brightness_red";
    public static final String KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_YELLOW = "tv_picture_color_tune_brightness_yellow";
    public static final String KEY_PICTURE_COLOR_TUNE_ENABLE = "tv_picture_color_tune_enable";
    public static final String KEY_PICTURE_COLOR_TUNE_GAIN = "tv_picture_color_tune_gain";
    public static final String KEY_PICTURE_COLOR_TUNE_GAIN_BLUE = "tv_picture_color_tune_gain_blue";
    public static final String KEY_PICTURE_COLOR_TUNE_GAIN_GREEN = "tv_picture_color_tune_gain_green";
    public static final String KEY_PICTURE_COLOR_TUNE_GAIN_RED = "tv_picture_color_tune_gain_red";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE = "tv_picture_color_tune_hue";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_BLUE = "tv_picture_color_tune_hue_blue";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_CVAN = "tv_picture_color_tune_hue_cvan";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_FLESH_TONE = "tv_picture_color_tune_hue_flesh_tone";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_GREEN = "tv_picture_color_tune_hue_green";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_MAGENTA = "tv_picture_color_tune_hue_megenta";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_RED = "tv_picture_color_tune_hue_red";
    public static final String KEY_PICTURE_COLOR_TUNE_HUE_YELLOW = "tv_picture_color_tune_hue_yellow";
    public static final String KEY_PICTURE_COLOR_TUNE_OFFSET = "tv_picture_color_tune_offset";
    public static final String KEY_PICTURE_COLOR_TUNE_OFFSET_BLUE = "tv_picture_color_tune_offset_blue";
    public static final String KEY_PICTURE_COLOR_TUNE_OFFSET_GREEN = "tv_picture_color_tune_offset_green";
    public static final String KEY_PICTURE_COLOR_TUNE_OFFSET_RED = "tv_picture_color_tune_offset_red";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION = "tv_picture_color_tune_saturation";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_BLUE = "tv_picture_color_tune_saturation_blue";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_CVAN = "tv_picture_color_tune_saturation_cvan";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_FLESH_TONE = "tv_picture_color_tune_saturation_flesh_tone";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_GREEN = "tv_picture_color_tune_saturation_green";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_MEGENTA = "tv_picture_color_tune_saturation_megenta";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_RED = "tv_picture_color_tune_saturation_red";
    public static final String KEY_PICTURE_COLOR_TUNE_SATURATION_YELLOW = "tv_picture_color_tune_saturation_yellow";
    public static final String KEY_PICTURE_CONTRAST = "picture_contrast";
    public static final String KEY_PICTURE_FILM_MODE = "picture_film_mode";
    public static final String KEY_PICTURE_FORMAT = "picture_format";
    public static final String KEY_PICTURE_GAMMA = "picture_gamma";
    public static final String KEY_PICTURE_GREEN_GAIN = "picture_green_gain";
    public static final String KEY_PICTURE_HDR = "picture_list_hdr";
    public static final String KEY_PICTURE_HUE = "picture_hue";
    public static final String KEY_PICTURE_MODE = "picture_mode";
    public static final String KEY_PICTURE_MODE_DOLBY = "picture_mode_dolby";
    public static final String KEY_PICTURE_NOTIFICATION = "picture_notification";
    public static final String KEY_PICTURE_RED_GAIN = "picture_red_gain";
    public static final String KEY_PICTURE_RESET_DEFAULT = "picture_reset_to_default";
    public static final String KEY_PICTURE_RESTORE = "picture_restore";
    public static final String KEY_PICTURE_SATURATION = "picture_saturation";
    public static final String KEY_PICTURE_SHARPNESS = "picture_sharpness";
    public static final String KEY_PICTURE_VGA = "picture_vga";
    public static final String KEY_PICTURE_VGA_AUTO = "picture_vga_auto";
    public static final String KEY_PICTURE_VGA_CLOCK = "picture_vga_clock";
    public static final String KEY_PICTURE_VGA_HP = "picture_vga_hp";
    public static final String KEY_PICTURE_VGA_PHASE = "picture_vga_phase";
    public static final String KEY_PICTURE_VGA_VP = "picture_vga_vp";
    public static final String KEY_PICTURE_WHITE_BALANCE11 = "picture_white_balance11";
    public static final String KEY_PICTURE_WHITE_BALANCE11_BLUE = "picture_white_balance11_blue";
    public static final String KEY_PICTURE_WHITE_BALANCE11_ENABLE = "picture_white_balance11_enable";
    public static final String KEY_PICTURE_WHITE_BALANCE11_GAIN = "picture_white_balance11_gain";
    public static final String KEY_PICTURE_WHITE_BALANCE11_GREEN = "picture_white_balance11_green";
    public static final String KEY_PICTURE_WHITE_BALANCE11_RED = "picture_white_balance11_red";
    public static final String KEY_POWER_EFFECTS = "power_effects";
    public static final String KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF = "no_signal_auto_power_off";
    public static final String KEY_POWER_PICTURE_OFF = "power_picture_off";
    public static final String KEY_POWER_SLEEP_TIMER = "power_sleep_timer";
    public static final String KEY_POWER_SWITCH_OFF_TIMER = "power_switch_off_timer";
    public static final String KEY_SONIC_EMOTION = "sound_sonic_emotion";
    public static final String KEY_SOUND = "sound_effects";
    public static final String KEY_SOUND_ADVANCED_DIALOGUE_ENHANCEER = "sound_advanced_dialogue_enhancer";
    public static final String KEY_SOUND_ADVANCED_DOLBY_AP = "sound_advanced_dolby_ap";
    public static final String KEY_SOUND_ADVANCED_DOLBY_ATMOS = "sound_advanced_dolby_atmos";
    public static final String KEY_SOUND_ADVANCED_SOUND_MODE = "sound_advanced_sound_mode";
    public static final String KEY_SOUND_ADVANCED_SURROUND_VIRTUALIZER = "sound_advanced_surround_virtualizer";
    public static final String KEY_SOUND_ADVANCED_VOLUME_LEVELER = "sound_advanced_volume_leveler";
    public static final String KEY_SOUND_AUTO_VOLUME_CONTROL = "sound_auto_volume_control";
    public static final String KEY_SOUND_BALANCE = "sound_balance";
    public static final String KEY_SOUND_BASS = "sound_bass";
    public static final String KEY_SOUND_DBX_ENABLE = "sound_dbx_enable";
    public static final String KEY_SOUND_DBX_TOTAL_SONIC = "sound_dbx_total_sonic";
    public static final String KEY_SOUND_DBX_TOTAL_SURROUND = "sound_dbx_total_surround";
    public static final String KEY_SOUND_DBX_TOTAL_VOL = "sound_dbx_total_vol";
    public static final String KEY_SOUND_DOWNMIX_MODE = "sound_downmixmode";
    public static final String KEY_SOUND_DTS = "sound_dts";
    public static final String KEY_SOUND_DTS_DRC = "sound_dts_drc";
    public static final String KEY_SOUND_DTS_LIMITER = "sound_dts_limiter";
    public static final String KEY_SOUND_DTS_STUDIO = "sound_dts_studio";
    public static final String KEY_SOUND_DTS_STUDIO_ENABLE = "sound_dts_studio_enable";
    public static final String KEY_SOUND_DTS_STUDIO_SURROUND = "sound_dts_studio_surround";
    public static final String KEY_SOUND_DTS_STUDIO_TRUEVOLUME = "sound_dts_studio_truevolume";
    public static final String KEY_SOUND_DTS_TBHDX = "sound_dts_tbhdx";
    public static final String KEY_SOUND_DTS_VIRTUAL_X = "sound_dts_virtual_x";
    public static final String KEY_SOUND_EQUALIZER = "sound_equalizer";
    public static final String KEY_SOUND_EQUALIZER_DETAIL = "sound_equalizer_detail";
    public static final String KEY_SOUND_RESET_DEFAULT = "sound_reset_to_default";
    public static final String KEY_SOUND_SONIC_EMOTION_PRE = "sound_sonic_emotion_premium";
    public static final String KEY_SOUND_SOUND_SURROUND = "sound_sound_surround";
    public static final String KEY_SOUND_SPDIF_DELAY = "sound_spdif_delay";
    public static final String KEY_SOUND_SPDIF_TYPE = "sound_spdif_type";
    public static final String KEY_SOUND_SPEAKER = "sound_speakers";
    public static final String KEY_SOUND_SPEAKERS = "hdmi_arc_control_enabled";
    public static final String KEY_SOUND_STYLE = "sound_style";
    public static final String KEY_SOUND_SURROUND = "sound_surround";
    public static final String KEY_SOUND_TREBLE = "sound_treble";
    public static final String KEY_SOUND_TYPE = "sound_type";
    public static final String KEY_SUBTITLE = "subtitle_hbbtv";
    public static final String KEY_TV_HDMI_EDID_VERSION = "tv_input_hdmi_edid_version";
    public static final String LOCAL_LANGUAGE = "local_language_name";
    public static final String MAIN_SOURCE_NAME = "multi_view_main_source_name";
    private static final String TAG = "PreferenceConfigUtils";
    private static PreferenceConfigUtils mConfigUtils;
    private static Context themeContext;
    private TVSettingConfig mTVSettingConfig;

    public PreferenceConfigUtils(Context themeContext2) {
        themeContext = themeContext2;
        this.mTVSettingConfig = TVSettingConfig.getInstance(themeContext2);
    }

    public static PreferenceConfigUtils getInstance(Context themeContext2) {
        if (mConfigUtils == null || isThemeChanged(themeContext2)) {
            mConfigUtils = new PreferenceConfigUtils(themeContext2);
        }
        return mConfigUtils;
    }

    public ProgressPreference createProgressPreference(LeanbackPreferenceFragment fragment, String key, int titleId, String configId, int maxValue, int minValue, int mStep) {
        ProgressPreference pref = new ProgressPreference(themeContext);
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(titleId));
        pref.setMaxValue(maxValue);
        pref.setMinValue(minValue);
        pref.setmStep(mStep);
        pref.setCurrentValue(getSettingValueInt(fragment.getContext().getContentResolver(), key));
        if (fragment instanceof Preference.OnPreferenceChangeListener) {
            pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
        }
        return pref;
    }

    public ProgressPreference createProgressPreference(LeanbackPreferenceFragment fragment, String key, int titleId, String configId, int mStep) {
        ProgressPreference pref = new ProgressPreference(themeContext);
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(titleId));
        pref.setMaxValue(this.mTVSettingConfig.getMax(configId));
        pref.setMinValue(this.mTVSettingConfig.getMin(configId));
        pref.setmStep(mStep);
        pref.setCurrentValue(getSettingValueInt(fragment.getContext().getContentResolver(), key));
        if (fragment instanceof Preference.OnPreferenceChangeListener) {
            pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
        }
        return pref;
    }

    public ProgressPreference createProgressPreferenceTrans(LeanbackPreferenceFragment fragment, String key, String num, String configId, int mStep) {
        ProgressPreference pref = new ProgressPreference(themeContext);
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(R.string.device_sound_equalizer_detail_hz, new Object[]{num}));
        pref.setMaxValue(this.mTVSettingConfig.getMax(configId));
        pref.setMinValue(this.mTVSettingConfig.getMin(configId));
        pref.setmStep(mStep);
        pref.setCurrentValue(getSettingValueInt(fragment.getContext().getContentResolver(), key));
        if (fragment instanceof Preference.OnPreferenceChangeListener) {
            pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
        }
        return pref;
    }

    public SwitchPreference createSwitchPreference(LeanbackPreferenceFragment fragment, String key, int titleId, String configId) {
        SwitchPreference pref = new SwitchPreference(themeContext);
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(titleId));
        boolean z = false;
        pref.setPersistent(false);
        if (getSettingValueInt(fragment.getContext().getContentResolver(), key) == 1) {
            z = true;
        }
        pref.setChecked(z);
        return pref;
    }

    public ListPreference createListPreference(LeanbackPreferenceFragment fragment, String key, int titleId, int entriesId, int entryValuesId, String configId) {
        ListPreference pref = new ListPreference(themeContext);
        int val = getSettingValueInt(fragment.getContext().getContentResolver(), key);
        if (key.equalsIgnoreCase(KEY_SOUND_SPEAKERS)) {
            val = getSettingValueInt(fragment.getContext().getContentResolver(), key, 1);
        }
        if (val < 0) {
            val = 0;
        }
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(titleId));
        pref.setDialogTitle(titleId);
        pref.setPersistent(true);
        pref.setEntries(entriesId);
        pref.setEntryValues(entryValuesId);
        CharSequence[] ev = pref.getEntryValues();
        if (ev != null) {
            int i = 0;
            while (true) {
                if (i >= ev.length) {
                    break;
                }
                MtkLog.d(TAG, "mEntryValues[i].toString() " + ev[i].toString() + "    " + val);
                if (ev[i].toString().equals(String.valueOf(val))) {
                    val = i;
                    break;
                }
                i++;
            }
        }
        if (pref.getEntries().length <= val) {
            MtkLog.e(TAG, "createListPreference arrayIndexOutOfbound key = " + key + " val = " + val);
            val = 0;
        }
        pref.setSummary(pref.getEntries()[val]);
        pref.setValueIndex(val);
        if (fragment instanceof Preference.OnPreferenceChangeListener) {
            pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
        }
        return pref;
    }

    public ListPreference createListPreference(LeanbackPreferenceFragment fragment, String key, int titleId, CharSequence[] entries, CharSequence[] entryValues, String configId) {
        ListPreference pref = new ListPreference(themeContext);
        int val = getSettingValueInt(fragment.getContext().getContentResolver(), key);
        int i = 0;
        if (key.equalsIgnoreCase(KEY_SOUND_SPEAKERS)) {
            val = getSettingValueInt(fragment.getContext().getContentResolver(), key, 1);
        } else if (key.equalsIgnoreCase(KEY_PICTURE_FORMAT)) {
            val = this.mTVSettingConfig.getConfigValueInt("g_fusion_picture__pictrue_format");
            boolean isHasValue = false;
            for (CharSequence charSequence : entryValues) {
                if (charSequence.toString().equals(String.valueOf(val))) {
                    isHasValue = true;
                }
            }
            if (!isHasValue) {
                val = 0;
            }
            Log.d(TAG, "KEY_PICTURE_FORMAT val=" + val);
        }
        if (val < 0) {
            val = 0;
        }
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(titleId));
        pref.setDialogTitle(titleId);
        pref.setPersistent(true);
        pref.setEntries(entries);
        pref.setEntryValues(entryValues);
        if (entryValues != null) {
            while (true) {
                int i2 = i;
                if (i2 >= entryValues.length) {
                    break;
                }
                MtkLog.d(TAG, "mEntryValues[i].toString() " + entryValues[i2].toString() + "    " + val);
                if (entryValues[i2].toString().equals(String.valueOf(val))) {
                    val = i2;
                    break;
                }
                i = i2 + 1;
            }
        }
        if (pref.getEntries().length <= val) {
            MtkLog.e(TAG, "createListPreference arrayIndexOutOfbound key = " + key + " val = " + val);
            val = 0;
        }
        pref.setSummary(pref.getEntries()[val]);
        pref.setValueIndex(val);
        if (fragment instanceof Preference.OnPreferenceChangeListener) {
            pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
        }
        return pref;
    }

    public Preference createPreference(LeanbackPreferenceFragment fragment, String key, int titleId, String fragClassName) {
        Preference pref = new Preference(themeContext);
        pref.setKey(key);
        pref.setTitle((CharSequence) themeContext.getResources().getString(titleId));
        if (fragClassName != null) {
            pref.setFragment(fragClassName);
        }
        return pref;
    }

    public Preference createPreference(LeanbackPreferenceFragment fragment, String key, String title, String fragClassName) {
        Preference pref = new Preference(themeContext);
        pref.setKey(key);
        pref.setTitle((CharSequence) title);
        if (fragClassName != null) {
            pref.setFragment(fragClassName);
        }
        return pref;
    }

    public static int getSettingValueInt(ContentResolver mResolver, String settingKey) {
        int value = Settings.Global.getInt(mResolver, settingKey, 0);
        MtkLog.d(TAG, "getSettingValueInt : settingKey = " + settingKey + "  value = " + value);
        return value;
    }

    public static int getSettingValueInt(ContentResolver mResolver, String settingKey, int def) {
        int value = Settings.Global.getInt(mResolver, settingKey, def);
        MtkLog.d(TAG, "getSettingValueInt : settingKey = " + settingKey + "  value = " + value + " def = " + def);
        return value;
    }

    public static void putSettingValueInt(ContentResolver mResolver, String settingKey, int settingValue) {
        MtkLog.d(TAG, "putSettingValueInt : settingKey = " + settingKey + " settingValue = " + settingValue);
        Settings.Global.putInt(mResolver, settingKey, settingValue);
    }

    public boolean updatePreferenceChanged(LeanbackPreferenceFragment fragment, Preference preference, Object newValue) {
        int value = onPreferenceValueChange(preference, newValue);
        if (value == -1000) {
            return false;
        }
        putSettingValueInt(fragment.getContext().getContentResolver(), preference.getKey(), value);
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r1v10 */
    /* JADX WARNING: type inference failed for: r1v12, types: [int] */
    /* JADX WARNING: type inference failed for: r1v14 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onPreferenceValueChange(android.support.v7.preference.Preference r10, java.lang.Object r11) {
        /*
            r9 = this;
            boolean r0 = r10 instanceof android.support.v7.preference.ListPreference
            r1 = 0
            r2 = -1000(0xfffffffffffffc18, float:NaN)
            if (r0 == 0) goto L_0x0077
            r0 = r10
            android.support.v7.preference.ListPreference r0 = (android.support.v7.preference.ListPreference) r0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r11)
            java.lang.String r4 = ""
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.CharSequence[] r4 = r0.getEntryValues()
            java.lang.CharSequence[] r5 = r0.getEntries()
            int r6 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x002a }
            r2 = r6
            goto L_0x0032
        L_0x002a:
            r6 = move-exception
            r7 = r11
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r2 = r7.intValue()
        L_0x0032:
            if (r3 == 0) goto L_0x0076
            if (r4 == 0) goto L_0x0076
        L_0x0037:
            int r6 = r4.length
            if (r1 >= r6) goto L_0x0076
            java.lang.String r6 = "PreferenceConfigUtils"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "mEntryValues[i].toString() "
            r7.append(r8)
            r8 = r4[r1]
            java.lang.String r8 = r8.toString()
            r7.append(r8)
            java.lang.String r8 = "    "
            r7.append(r8)
            r7.append(r3)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            r6 = r4[r1]
            java.lang.String r6 = r6.toString()
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x0073
            r6 = r5[r1]
            r0.setSummary(r6)
            r0.setValueIndex(r1)
            goto L_0x0076
        L_0x0073:
            int r1 = r1 + 1
            goto L_0x0037
        L_0x0076:
            return r2
        L_0x0077:
            boolean r0 = r10 instanceof android.support.v14.preference.SwitchPreference
            if (r0 == 0) goto L_0x00be
            r0 = r10
            android.support.v14.preference.SwitchPreference r0 = (android.support.v14.preference.SwitchPreference) r0
            boolean r3 = r11 instanceof java.lang.Boolean
            r4 = 1
            if (r3 == 0) goto L_0x0092
            r2 = r11
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            r0.setChecked(r2)
            if (r2 != r4) goto L_0x0091
            r1 = r4
        L_0x0091:
            return r1
        L_0x0092:
            boolean r3 = r11 instanceof java.lang.Integer
            if (r3 == 0) goto L_0x00a5
            r3 = r11
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            if (r3 != r4) goto L_0x00a1
            r1 = r4
        L_0x00a1:
            r0.setChecked(r1)
            goto L_0x00bd
        L_0x00a5:
            boolean r1 = r11 instanceof java.lang.String
            if (r1 == 0) goto L_0x00b6
            r1 = r11
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r3 = "1"
            boolean r3 = r1.equals(r3)
            r0.setChecked(r3)
            goto L_0x00bd
        L_0x00b6:
            java.lang.String r1 = "PreferenceConfigUtils"
            java.lang.String r3 = "onPreferenceValueChange unknow value type"
            com.mediatek.wwtv.tvcenter.util.MtkLog.e(r1, r3)
        L_0x00bd:
            goto L_0x00db
        L_0x00be:
            boolean r0 = r10 instanceof com.android.tv.settings.partnercustomizer.utils.ProgressPreference
            if (r0 == 0) goto L_0x00db
            r0 = r10
            com.android.tv.settings.partnercustomizer.utils.ProgressPreference r0 = (com.android.tv.settings.partnercustomizer.utils.ProgressPreference) r0
            r1 = r2
            r2 = r11
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x00cf }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x00cf }
            r1 = r2
            goto L_0x00d7
        L_0x00cf:
            r2 = move-exception
            r3 = r11
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r1 = r3.intValue()
        L_0x00d7:
            r0.setValue(r1)
            return r1
        L_0x00db:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils.onPreferenceValueChange(android.support.v7.preference.Preference, java.lang.Object):int");
    }

    public ListPreference setupListPreferencesConfig(LeanbackPreferenceFragment fragment, String preferenceKey, String configId, int resId) {
        MtkLog.d(TAG, "setupListPreferencesConfig,preferenceKey==" + preferenceKey);
        ListPreference pref = (ListPreference) fragment.findPreference(preferenceKey);
        String stringValue = formatConfigValuetoString(configId, resId);
        if (pref != null) {
            pref.setValue(stringValue);
            if (fragment instanceof Preference.OnPreferenceChangeListener) {
                pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
            } else {
                MtkLog.e(TAG, "Cannot find preference for " + preferenceKey);
            }
        }
        return pref;
    }

    public String formatConfigValuetoString(String configId, int resId) {
        int value = this.mTVSettingConfig.getConfigValueInt(configId);
        String[] array = themeContext.getResources().getStringArray(resId);
        MtkLog.d(TAG, "value==" + value + ",array.length==" + array.length);
        String stringValue = null;
        if (value >= 0 && value < array.length) {
            stringValue = array[value];
        }
        MtkLog.d(TAG, "stringValue==" + stringValue);
        return stringValue;
    }

    public int formatPrefStringToConfigValue(String prefString, int resId) {
        String[] array = themeContext.getResources().getStringArray(resId);
        for (int i = 0; i < array.length; i++) {
            Log.e(TAG, "onPreferenceChange array[i] == " + array[i]);
            if (prefString.equalsIgnoreCase(array[i])) {
                Log.e(TAG, "formatPrefStringToConfigValue i == " + i);
                return i;
            }
        }
        return -1;
    }

    public int[] getSupportScreenModes() {
        return MtkTvAVMode.getInstance().getAllScreenMode();
    }

    public boolean isHDMISource(Context context) {
        int inputType = SaveValue.readWorldInputType(context);
        boolean isHDMISource = 17 == inputType;
        MtkLog.d(TAG, "isHDMISource==" + isHDMISource + ",sourceName==" + inputType);
        return isHDMISource;
    }

    private static boolean isThemeChanged(Context context) {
        String localLanguage = SaveValue.readWorldStringValue(context, LOCAL_LANGUAGE);
        String language = Locale.getDefault().getLanguage();
        Log.d(TAG, "isThemeChanged== localLanguage  " + localLanguage + " language = " + language);
        if (localLanguage == null || localLanguage.equals(language)) {
            return false;
        }
        SaveValue.writeWorldStringValue(context, LOCAL_LANGUAGE, language, true);
        return true;
    }

    public boolean isVGASource(Context context) {
        int inputType = SaveValue.readWorldInputType(context);
        boolean isVGASource = 16 == inputType;
        MtkLog.d(TAG, "isVGASource==" + isVGASource + ",sourceName==" + inputType);
        return isVGASource;
    }

    public boolean isHDMIVGASignal(Context context) {
        int tim = MtkTvAVMode.getInstance().getVideoInfoValue(4);
        MtkLog.d(TAG, "isHDMIVGASignal ==  tim = " + tim);
        return tim == 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int[] getArrayIdsByCustomer(java.lang.String r5) {
        /*
            r4 = this;
            java.lang.String r0 = com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig.getCustomerName()
            java.lang.String r1 = "PreferenceConfigUtils"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "getArrayIdsByCustomer,customer=="
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            int r1 = r5.hashCode()
            r2 = -2026700159(0xffffffff87330281, float:-1.3467199E-34)
            r3 = 2
            if (r1 == r2) goto L_0x0052
            r2 = -786037692(0xffffffffd1260444, float:-4.4564759E10)
            if (r1 == r2) goto L_0x0048
            r2 = 332045912(0x13ca9e58, float:5.1148108E-27)
            if (r1 == r2) goto L_0x003e
            r2 = 713845149(0x2a8c699d, float:2.494228E-13)
            if (r1 == r2) goto L_0x0034
            goto L_0x005c
        L_0x0034:
            java.lang.String r1 = "picture_mode_dolby"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x005c
            r1 = 1
            goto L_0x005d
        L_0x003e:
            java.lang.String r1 = "picture_format"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x005c
            r1 = r3
            goto L_0x005d
        L_0x0048:
            java.lang.String r1 = "picture_mode"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x005c
            r1 = 0
            goto L_0x005d
        L_0x0052:
            java.lang.String r1 = "sound_style"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x005c
            r1 = 3
            goto L_0x005d
        L_0x005c:
            r1 = -1
        L_0x005d:
            switch(r1) {
                case 0: goto L_0x00ad;
                case 1: goto L_0x00a7;
                case 2: goto L_0x007d;
                case 3: goto L_0x0061;
                default: goto L_0x0060;
            }
        L_0x0060:
            goto L_0x00c9
        L_0x0061:
            java.lang.String r1 = "funai"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x006f
            int[] r1 = new int[r3]
            r1 = {2130903211, 2130903212} // fill-array
            return r1
        L_0x006f:
            java.lang.String r1 = "mtk"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00c9
            int[] r1 = new int[r3]
            r1 = {2130903209, 2130903210} // fill-array
            return r1
        L_0x007d:
            java.lang.String r1 = "funai"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x008b
            int[] r1 = new int[r3]
            r1 = {2130903174, 2130903175} // fill-array
            return r1
        L_0x008b:
            java.lang.String r1 = "mtk"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00c9
            android.content.Context r1 = themeContext
            boolean r1 = r4.isVGASource(r1)
            if (r1 == 0) goto L_0x00a1
            int[] r1 = new int[r3]
            r1 = {2130903173, 2130903172} // fill-array
            return r1
        L_0x00a1:
            int[] r1 = new int[r3]
            r1 = {2130903170, 2130903171} // fill-array
            return r1
        L_0x00a7:
            int[] r1 = new int[r3]
            r1 = {2130903153, 2130903154} // fill-array
            return r1
        L_0x00ad:
            java.lang.String r1 = "funai"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00bb
            int[] r1 = new int[r3]
            r1 = {2130903159, 2130903160} // fill-array
            return r1
        L_0x00bb:
            java.lang.String r1 = "mtk"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00c9
            int[] r1 = new int[r3]
            r1 = {2130903159, 2130903160} // fill-array
            return r1
        L_0x00c9:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils.getArrayIdsByCustomer(java.lang.String):int[]");
    }
}
