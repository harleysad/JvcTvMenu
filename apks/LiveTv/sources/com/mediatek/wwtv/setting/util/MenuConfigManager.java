package com.mediatek.wwtv.setting.util;

import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContentRatingSystemInfo;
import android.media.tv.TvInputManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvATSCCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAVModeBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvTimeFormat;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.wwtv.setting.LiveTvSetting;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.parental.ContentRatingSystem;
import com.mediatek.wwtv.setting.parental.ContentRatingsManager;
import com.mediatek.wwtv.setting.parental.ContentRatingsParser;
import com.mediatek.wwtv.setting.parental.ParentalControlSettings;
import com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuConfigManager {
    public static final String ACTION_SYNC_CONFIG_VALUE = "com.android.tv.settings.SYNC_CONFIG_VALUE";
    public static final String ADAPTIVE_LOCAL_CONTRAST_CONTROL = "g_video__vid_local_contrast";
    public static final String ADAPTIVE_LUMA_CONTROL = "g_video__vid_luma";
    public static final String ADDRESS_TYPE = "UNDEFINE_address_type";
    public static final String ANALOG_SUBTITLE = "g_subtitle__subtitle_enable";
    public static final int AUDIO_SYS_MASK_AM = 1;
    public static final int AUDIO_SYS_MASK_BTSC = 256;
    public static final int AUDIO_SYS_MASK_FM_A2 = 8;
    public static final int AUDIO_SYS_MASK_FM_A2_DK1 = 16;
    public static final int AUDIO_SYS_MASK_FM_A2_DK2 = 32;
    public static final int AUDIO_SYS_MASK_FM_MONO = 2;
    public static final int AUDIO_SYS_MASK_NICAM = 128;
    public static final String AUTO_ADJUST = "SUB_AUTO_ADJUST";
    public static final String AUTO_SLEEP = "g_misc__auto_sleep";
    public static final String AUTO_START_APPLICATION = "g_ginga__ginga_auto_start";
    public static final String AUTO_SYNC = "SETUP_auto_syn";
    public static final String AVCMODE = "g_audio__agc";
    public static final String BACKLIGHT = "g_disp__disp_back_light";
    public static final String BALANCE = "g_audio__aud_balance";
    public static final String BASS = "g_audio__aud_bass";
    public static final String BISS_KEY = "biss_key";
    public static final String BISS_KEY_CW_KEY = "biss_key_cw_key";
    public static final String BISS_KEY_FREQ = "biss_freqency";
    public static final int BISS_KEY_FREQ_MAX = 13000;
    public static final int BISS_KEY_FREQ_MIN = 3000;
    public static final String BISS_KEY_ITEM = "biss_key_item";
    public static final String BISS_KEY_ITEM_ADD = "biss_key_item_add";
    public static final String BISS_KEY_ITEM_DELETE = "biss_key_item_delete";
    public static final String BISS_KEY_ITEM_SAVE = "biss_key_item_save";
    public static final String BISS_KEY_ITEM_UPDATE = "biss_key_item_update";
    public static final int BISS_KEY_OPERATE_ADD = 241;
    public static final int BISS_KEY_OPERATE_DELETE = 243;
    public static final int BISS_KEY_OPERATE_UPDATE = 242;
    public static final String BISS_KEY_POLAZATION = "biss_key_polazation";
    public static final String BISS_KEY_SVC_ID = "biss_key_sevice_id";
    public static final int BISS_KEY_SVC_ID_MAX = 65535;
    public static final int BISS_KEY_SVC_ID_MIN = 0;
    public static final String BISS_KEY_SYMBOL_RATE = "biss_sysbol_rate";
    public static final int BISS_KEY_SYMBOL_RATE_MAX = 45000;
    public static final int BISS_KEY_SYMBOL_RATE_MIN = 2000;
    public static final String BLACK_BAR_DETECTION = "g_video__vid_black_bar_detect";
    public static final String BLUE_LIGHT = "g_video__vid_blue_light";
    public static final String BLUE_MUTE = "g_video__vid_blue_mute";
    public static final String BLUE_STRETCH = "g_video__vid_blue_stretch";
    public static final String BRDCST_TYPE = "g_bs__bs_brdcst_type";
    public static final String BRIGHTNESS = "g_video__brightness";
    public static final String CAPTION = "SETUP_caption_setup";
    public static final String CAPTURE_LOGO_SELECT = "SETUP_capture_logo";
    public static final String CEC_AUTO_OFF = "g_cec__cec_auto_off";
    public static final String CEC_AUTO_ON = "g_cec__cec_auto_on";
    public static final String CEC_CEC_FUN = "g_cec__cec_func";
    public static final String CEC_DEVICE_DISCOVERY = "cec_device";
    public static final int CEC_FUNTION_OFF = 0;
    public static final int CEC_FUNTION_ON = 1;
    public static final String CEC_SAC_OFUN = "g_cec__cec_sac_func";
    public static final String CFG_AUD_AUD_BBE_MODE = "g_audio__aud_bbe_mode";
    public static final String CFG_MENU_AUDIOINFO = "g_menu__audioinfo";
    public static final String CFG_MENU_AUDIOINFO_GET_CURRENT = "g_menu__audioinfocurrent";
    public static final String CFG_MENU_AUDIOINFO_GET_STRING = "audioinfogetstring";
    public static final String CFG_MENU_AUDIOINFO_GET_TOTAL = "g_menu__audioinfototal";
    public static final String CFG_MENU_AUDIOINFO_SET_DEINIT = "g_menu__audioinfodeinit";
    public static final String CFG_MENU_AUDIOINFO_SET_INIT = "g_menu__audioinfoinit";
    public static final String CFG_MENU_AUDIOINFO_SET_SELECT = "g_menu__audioinfoselect";
    public static final String CFG_MENU_AUDIO_AD_TYP = "g_menu__audio_ad_type";
    public static final String CFG_MENU_AUDIO_LANGUAGE_ATTR = "g_menu__audio_lang_attr";
    public static final String CFG_MENU_XVYCC = "g_menu__xvYCC";
    public static final String CFG_VIDEO_VID_MJC_DEMO_STATUS = "g_video__vid_mjc_status";
    public static final String CHANNEL_CAM_PROFILE_SCAN = "g_misc__cam_profile_scan";
    public static final String CHANNEL_CHANNEL_INSTALLATION_MODE = "channel_channel_installation_mode";
    public static final String CHANNEL_CHANNEL_SOURCES = "channel_channel_sources";
    public static final String CHANNEL_CUSTOMIZE_CHANNEL_LIST = "channel_customize_channel_list";
    public static final String CHANNEL_DVBC_SCAN_TYPE = "g_fusion_common__encrypt_dvbc";
    public static final String CHANNEL_DVBC_STORE_TYPE = "g_fusion_common__storage_dvbc";
    public static final String CHANNEL_DVBT_SCAN_TYPE = "g_fusion_common__encrypt_dvbt";
    public static final String CHANNEL_DVBT_STORE_TYPE = "g_fusion_common__storage_dvbt";
    public static final String CHANNEL_LCN = "g_fusion_common__lcn";
    public static final String CHANNEL_LIST_SLOT = "g_misc__ch_list_slot";
    public static final String CHANNEL_LIST_TYPE = "g_misc__ch_list_type";
    public static final String CHANNEL_MULTI_AUDIO = "channel_multi_audio";
    public static final String CHANNEL_OPEN_SOURCE_LICENSES = "channel_open_source_licenses";
    public static final String CHANNEL_PARENTAL_CONTROLS = "channel_parental_controls";
    public static final String CHANNEL_VERSION = "channel_version";
    public static final String CLOCK = "g_vga__vga_clock";
    public static final String COLOR_G_B = "g_video__clr_gain_b";
    public static final String COLOR_G_G = "g_video__clr_gain_g";
    public static final String COLOR_G_R = "g_video__clr_gain_r";
    public static final String COLOR_SYSTEM = "SCAN_OPTION_COLOR_SYSTEM";
    public static final String COLOR_TEMPERATURE = "g_video__clr_temp";
    public static final String COMMON_INTERFACE = "SETUP_common_interface";
    public static final String CONTRAST = "g_video__contrast";
    public static final String COUNTRY_REGION_ID = "g_country__country_rid";
    public static final String DEMO = "UNDEFINE_DEMO";
    public static final String DEMO_PARTITION = "g_video__vid_mjc_demo";
    public static final String DIGITAL_SUBTITLE_LANG = "g_subtitle__subtitle_lang";
    public static final String DIGITAL_SUBTITLE_LANG_2ND = "g_subtitle__subtitle_lang_2nd";
    public static final String DIGITAL_SUBTITLE_LANG_ENABLE = "g_subtitle__subtitle_enable_ex";
    public static final String DIGITAL_SUBTITLE_LANG_ENABLE_2ND = "g_subtitle__subtitle_enable_ex_2nd";
    public static final String DIVX_DEA = "SETUP_divx_dea";
    public static final String DIVX_REG = "SETUP_divx_reg";
    public static final String DI_FILM_MODE = "g_video__vid_di_film_mode";
    public static final String DLNA = "SETUP_dlna";
    public static final String DNR = "g_video__vid_nr";
    public static final String DOWNLOAD_FIRMWARE = "download_firmware";
    public static final String DOWNMIX_MODE = "g_audio__dolby_dmix";
    public static final String DPMS = "g_misc__dpms";
    public static final String DTV_DEVICE_INFO = "DTV_DEVICE_INFO";
    public static final String DTV_SCHEDULE_LIST = "DTV_SCHEDULE_LIST";
    public static final String DTV_TSHIFT_OPTION = "DTV_TSHIFT_OPTION";
    public static final String DUAL_TUNER = "g_misc__2nd_tuner_type";
    public static final String DVBC_SINGLE_RF_SCAN_FREQ = "dvbc_single_rf_scan_freq";
    public static final String DVBC_SINGLE_RF_SCAN_MODULATION = "dvbc_single_rf_scan_modulation";
    public static final String DVBS_DETAIL_DISEQC10_PORT = "DVBS_DETAIL_DISEQC10_PORT";
    public static final String DVBS_DETAIL_DISEQC11_PORT = "DVBS_DETAIL_DISEQC11_PORT";
    public static final String DVBS_DETAIL_DISEQC12_SET = "DVBS_DETAIL_DISEQC12_SET";
    public static final String DVBS_DETAIL_DISEQC_MOTOR = "DVBS_DETAIL_DISEQC_MOTOR";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS = "DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION = "DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE = "DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST = "DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST = "DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL = "DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL";
    public static final String DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION = "DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION";
    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST = "DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST";
    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST = "DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST";
    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE = "DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE";
    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT = "DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT";
    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS = "DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS";
    public static final String DVBS_DETAIL_LNB_POWER = "DVBS_DETAIL_LNB_POWER";
    public static final String DVBS_DETAIL_POSITION = "DVBS_DETAIL_POSITION";
    public static final String DVBS_SAT_ADD = "Satellite Add";
    public static final String DVBS_SAT_ATENNA_TYPE = "g_bs__bs_sat_antenna_type";
    public static final String DVBS_SAT_ATENNA_TYPE_BANDFREQ = "Satellite atenna type band freq";
    public static final String DVBS_SAT_ATENNA_TYPE_SET = "Satellite atenna type set";
    public static final String DVBS_SAT_ATENNA_TYPE_SUB_BANDFREQ = "Satellite atenna type sub band freq";
    public static final String DVBS_SAT_ATENNA_TYPE_SUB_TUNER = "Satellite atenna type sub tuner";
    public static final String DVBS_SAT_ATENNA_TYPE_SUB_USERDEF = "Satellite atenna type sub user define";
    public static final String DVBS_SAT_ATENNA_TYPE_TUNER = "Satellite atenna type tuner";
    public static final String DVBS_SAT_ATENNA_TYPE_USERDEF = "Satellite atenna type user define";
    public static final String DVBS_SAT_COMMON_TP = "DVBS_SAT_COMMON_TP";
    public static final String DVBS_SAT_DEDATIL_INFO = "DVBS_SAT_DEDATIL_INFO";
    public static final String DVBS_SAT_DEDATIL_INFO_ITEMS = "DVBS_SAT_DEDATIL_INFO_ITEMS";
    public static final String DVBS_SAT_DEDATIL_INFO_SCAN = "DVBS_SAT_DEDATIL_INFO_SCAN";
    public static final String DVBS_SAT_DEDATIL_INFO_START_SCAN = "DVBS_SAT_DEDATIL_INFO_START_SCAN";
    public static final String DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG = "DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG";
    public static final String DVBS_SAT_DEDATIL_INFO_START_SCAN_SCAN_CONFIG = "DVBS_SAT_DEDATIL_INFO_START_SCAN_SCAN_CONFIG";
    public static final String DVBS_SAT_DEDATIL_INFO_START_SCAN_STORE_CONFIG = "DVBS_SAT_DEDATIL_INFO_START_SCAN_STORE_CONFIG";
    public static final String DVBS_SAT_DEDATIL_INFO_TP_ITEMS = "DVBS_SAT_DEDATIL_INFO_TP_ITEMS";
    public static final String DVBS_SAT_MANUAL_TURNING = "DVBS_SAT_MANUAL_TURNING";
    public static final String DVBS_SAT_MANUAL_TURNING_TP = "DVBS_SAT_MANUAL_TURNING_TP";
    public static final String DVBS_SAT_OP = "DVBS_SAT_OP";
    public static final String DVBS_SAT_OP_REGION = "DVBS_SAT_OP_REGION";
    public static final String DVBS_SAT_PREFIX = "DVBS_SAT_";
    public static final String DVBS_SAT_RE_SCAN = "Satellite Re-scan";
    public static final String DVBS_SAT_UPDATE_SCAN = "Satellite Update";
    public static final String DVBS_SIGNAL_LEVEL = "DVBS_SIGNAL_LEVEL";
    public static final String DVBS_SIGNAL_QULITY = "DVBS_SIGNAL_QULITY";
    public static final int EDIT_CHANNEL_LENGTH = 16;
    public static final String EFFECT = "g_video__vid_mjc_effect";
    private static final int EIGHT_SLEEP_TIME_MS = 28500000;
    public static final String EQUALIZE = "g_audio__aud_equalizer";
    public static final String FAA2_CARRIERSHIFTFUNCTION = "g_misc__a2 sys carrier shift function";
    public static final String FAA2_DUALWEIGHT = "g_misc__a2 sys dual weight";
    public static final String FAA2_FINETUNEVOLUME = "g_misc__a2 sys fine tune valume";
    public static final String FAA2_FMCARRIERMUTEMODE = "g_misc__a2 sys fm carrier mute mode";
    public static final String FAA2_FMCARRIERMUTETHRESHOLDHIGH = "g_misc__a2 sys fm carrier mute threshold hight";
    public static final String FAA2_FMCARRIERMUTETHRESHOLDLOW = "g_misc__a2 sys fm carrier mute threshold low";
    public static final String FAA2_HIGHDEVIATIONMODE = "g_misc__a2 sys hight deviation mode";
    public static final String FAA2_MONOWEIGHT = "g_misc__a2 sys mono wight";
    public static final String FAA2_NUMBERSOFCHECK = "g_misc__a2 sys num of check";
    public static final String FAA2_NUMBERSOFDOUBLE = "g_misc__a2 sys num of double";
    public static final String FAA2_STEREOWEIGHT = "g_misc__a2 sysstereo weight";
    public static final String FACTORY_AUDIO = "SUB_factory_audio";
    public static final String FACTORY_PRESET_CH = "SUB_preset_ch";
    public static final String FACTORY_PRESET_CH_DUMP = "preset_ch_dump";
    public static final String FACTORY_PRESET_CH_PRINT = "preset_ch_print";
    public static final String FACTORY_PRESET_CH_RESTORE = "preset_ch_restore";
    public static final String FACTORY_SETUP = "SUB_factory_setup";
    public static final String FACTORY_SETUP_AUTO = "g_cc__cc_attr_auto_line_feed_idx";
    public static final String FACTORY_SETUP_BURNING_MODE = "g_misc__bruning_mode";
    public static final String FACTORY_SETUP_CAPTION = "UNDEFINE_mts_factory_setup_cap";
    public static final String FACTORY_SETUP_CI_ECP_UPDATE = "factory_updateCi_ecp";
    public static final String FACTORY_SETUP_CI_ERASE = "factory_eraseCi";
    public static final String FACTORY_SETUP_CI_QUERY = "factory_queryCi";
    public static final String FACTORY_SETUP_CI_UPDATE = "factory_updateCi";
    public static final String FACTORY_SETUP_CLEAN_STORAGE = "factory_setup_clean_storage";
    public static final String FACTORY_SETUP_DATA_SERVICE_SUPPORT = "g_misc__fac_data_service";
    public static final String FACTORY_SETUP_EQUAL = "g_cc__cc_attr_equal_width_idx";
    public static final String FACTORY_SETUP_EVENT_FORM = "g_misc__evt_form";
    public static final String FACTORY_SETUP_EXTERN = "g_cc__cc_attr_ex_size_idx";
    public static final String FACTORY_SETUP_ROLL = "g_cc__cc_attr_roll_up_mode_idx";
    public static final String FACTORY_SETUP_UART_MODE = "g_misc__uart_factory_mode";
    public static final String FACTORY_SETUP_UTF8 = "g_cc__cc_attr_support_utf8_idx";
    public static final String FACTORY_TV = "SUB_factory_TV";
    public static final String FACTORY_TV_FACTORY_SCAN = "tuner_factory_scan";
    public static final String FACTORY_TV_RANGE_SCAN = "tuner_range_scan";
    public static final String FACTORY_TV_RANGE_SCAN_ANA = "tuner_range_scan_ana";
    public static final String FACTORY_TV_RANGE_SCAN_DIG = "tuner_range_scan_dig";
    public static final String FACTORY_TV_SINGLE_RF_SCAN = "tuner_single_rf_scan";
    public static final String FACTORY_TV_TUNER_DIAGNOSTIC = "tuner_diagnostic";
    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_LOCK = "tuner_diagnostic_lock";
    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_NOINFO = "tuner_diagnostic_noinfo";
    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_RF = "tuner_diagnostic_rf";
    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_VERSION = "tuner_diagnostic_version";
    public static final String FACTORY_VIDEO = "SUB_factory_video";
    public static final String FAEU_EU_NON = "g_misc__non_eu";
    public static final String FAEU_FM = "g_misc__fm_saturation_mute";
    public static final String FAMTS_CARRIER_SHIFT_FUNCTION = "g_misc__carrier_shift_function";
    public static final String FAMTS_FM_CARRIER_MUTE_MODE = "g_misc__fm_carrier_mute_mode";
    public static final String FAMTS_FM_CARRIER_MUTE_THRESHOLD_HIGH = "g_misc__fm_carrier_mute_threshold_high";
    public static final String FAMTS_FM_CARRIER_MUTE_THRESHOLD_LOW = "g_misc__fm_carrier_mute_threshold_low";
    public static final String FAMTS_FM_STAURATION_MODE = "g_misc__fm_saturation_mute";
    public static final String FAMTS_HIGH_DEVIATION_MODE = "g_misc__high_deviation_mode";
    public static final String FAMTS_MONO_STERO_FINE_TUNE_VOLUME = "g_misc__mono_stero_fine_tune_volume";
    public static final String FAMTS_NUMBERSOFCHECK = "g_misc__number_of_check";
    public static final String FAMTS_NUMBERSOFPILOT = "g_misc__numbers_of_pilot";
    public static final String FAMTS_NUMBERSOFSAP = "g_misc__numbers_of_sap";
    public static final String FAMTS_PILOT_THRESHOLD_HIGH = "g_misc__pilot_threshold_high";
    public static final String FAMTS_PILOT_THRESHOLD_LOW = "g_misc__pilot_threshold_low";
    public static final String FAMTS_SAP_FINE_TUNE_VOLUME = "g_misc__sap_fine_tune_volume";
    public static final String FAMTS_SAP_THRESHOLD_HIGH = "g_misc__sap_threshold_hith";
    public static final String FAMTS_SAP_THRESHOLD_LOW = "g_misc__sap_threshold_low";
    public static final String FAPAL_AMCARRIERMUTEMODE = "g_misc__am carrier mute mode";
    public static final String FAPAL_AMCARRIERMUTETHRESHOLDHIGH = "g_misc__am carrier mute threshold high";
    public static final String FAPAL_AMCARRIERMUTETHRESHOLDLOW = "g_misc__am carrier mute threshold low";
    public static final String FAPAL_AMFINETUNEVOLUME = "g_misc__am fine tune volume";
    public static final String FAPAL_CARRIERSHIFTFUNCTION = "g_misc__carrier shift function";
    public static final String FAPAL_CORRECTTHRESHOLD = "g_misc__correct threshold";
    public static final String FAPAL_ERRORTHRESHOLD = "g_misc__error threshold";
    public static final String FAPAL_EVERYNUMBERFRAMES = "g_misc__every num frames";
    public static final String FAPAL_FMCARRIERMUTEMODE = "g_misc__fm carrier mute mode";
    public static final String FAPAL_FMCARRIERMUTETHRESHOLDHIGH = "g_misc__fm carrier mute threshold high";
    public static final String FAPAL_FMCARRIERMUTETHRESHOLDLOW = "g_misc__fm carrier mute threshold low";
    public static final String FAPAL_HIGHDEVIATIONMODE = "g_misc__high deviation mode";
    public static final String FAPAL_NICAMFINETUNEVOLUME = "g_misc__nicam fine tune volume";
    public static final String FAPAL_PALFINETUNEVOLUME = "g_misc__pal fine tune volume";
    public static final String FAPAL_PARITYERRORTHRESHOLD = "g_misc__parity error threshold";
    public static final String FAPAL_TOTALSYNCLOOP = "g_misc__total sync loop";
    public static final String FAST_BOOT = "SETUP_fast_boot";
    public static final String FAV_SA_SINGLE_RF_CHANNEL = "SA_single_rf_channel";
    public static final String FAV_US_RANGE_FROM_CHANNEL = "US_range_frome_channel";
    public static final String FAV_US_RANGE_TO_CHANNEL = "US_range_to_channel";
    public static final String FAV_US_SINGLE_RF_CHANNEL = "US_single_rf_channel";
    public static final String FA_A2SYSTEM = "SUB_FA_A2SYSTEM";
    public static final String FA_COMPRESSION = "g_audio__dolby_cmpss";
    public static final String FA_COMPRESSIONFACTOR = "g_audio__dolby_drc";
    public static final String FA_DOLBYBANNER = "g_audio__dolby_cert_mode";
    public static final String FA_EUSYSTEM = "SUB_FA_EUSYSTEM";
    public static final String FA_LATENCY = "g_audio__aud_latency";
    public static final String FA_MTS_SYSTEM = "SUB_FA_MTS_SYSTEM";
    public static final String FA_PALSYSTEM = "SUB_FA_PALSYSTEM";
    public static final String FLESH_TONE = "g_video__vid_flash_tone";
    public static final int FOCUS_OPTION_CHANGE_CHANNEL = 0;
    private static final int FOUR_SLEEP_TIME_MS = 14100000;
    public static final String FREEVIEW_SETTING = "SETUP_freeviewplay_setting";
    public static final String FREEVIEW_TERM_CONDITION = "SETUP_freview_term_condition";
    public static final String FREQUENCY_LEN_6 = "frequency_length_six";
    public static final String FREQUENEY_PLAN = "US_single_RF_plan";
    public static final String FV_AUTOCOLOR = "SUB_FV_AUTOCOLOR";
    public static final String FV_AUTOPHASE = "SUB_FV_AUTOPHASE";
    public static final String FV_COLORTEMPERATURE = "SUB_FV_COLORTEMPERATURE";
    public static final String FV_COLORTEMPERATURECHILD = "g_video__clr_temp";
    public static final String FV_COLOR_G_B = "g_video__clr_gain_b";
    public static final String FV_COLOR_G_G = "g_video__clr_gain_g";
    public static final String FV_COLOR_G_R = "g_video__clr_gain_r";
    public static final String FV_COLOR_O_B = "g_video__clr_offset_b";
    public static final String FV_COLOR_O_G = "g_video__clr_offset_g";
    public static final String FV_COLOR_O_R = "g_video__clr_offset_r";
    public static final String FV_DIEDGE = "g_video__vid_di_edge";
    public static final String FV_DIMA = "g_video__vid_di_ma";
    public static final String FV_FLIP = "g_misc__flip";
    public static final String FV_HPOSITION = "g_video__vid_pos_h";
    public static final String FV_LOCAL_DIMMING = "g_misc__dimming";
    public static final String FV_MIRROR = "g_misc__mirror";
    public static final String FV_VGA_PHASE = "g_vga__vga_phase";
    public static final String FV_VIDEO_VID_XVYCC = "g_video__vid_xvycc";
    public static final String FV_VPOSITION = "g_video__vid_pos_v";
    public static final String FV_WCG = "g_video__vid_wgc";
    public static final String FV_YPBPR_PHASE = "g_video__vid_ypbpr_phase";
    public static final String GAME_MODE = "g_video__vid_game_mode";
    public static final String GAMMA = "g_disp__disp_gamma";
    public static final String GENERALESATELITE = "g_misc__2nd_prefer_ch_lst";
    public static final String GINGA_ENABLE = "g_ginga__ginga_enable";
    public static final String GINGA_SETUP = "SETUP_ginga_setup";
    public static final String GRAPHIC = "g_video__vid_super_resolution";
    public static final String HBBTV_ALLOW_3RD_COOKIES = "g_menu__allow_3rd_cookies";
    public static final String HBBTV_BLOCK_TRACKING_SITES = "g_menu__block_tracking_sites";
    public static final String HBBTV_DEV_ID = "g_menu__dev_id";
    public static final String HBBTV_DEV_ID_TIMESTAMP = "g_menu__dhbbtv_dev_id_timestamp";
    public static final String HBBTV_DO_NOT_TRACK = "g_menu__do_not_track";
    public static final String HBBTV_PERSISTENT_STORAGE = "g_menu__persistent_storage";
    public static final String HBBTV_RESET_DEVICE_ID = "SETUP_hbbtv_reset_device_id";
    public static final String HBBTV_SUPPORT = "g_menu__hbbtv";
    public static final String HDMI_MODE = "g_video__vid_hdmi_mode";
    public static final String HPOSITION = "g_vga__vga_pos_h";
    public static final String HUE = "g_video__vid_hue";
    public static final String INTERACTION_CHANNEL = "g_misc__mheg_inter_ch";
    public static final int INVALID_VALUE = 10004;
    public static final String LICENSE_INFO = "SETUP_license_info";
    public static final String M7_LNB_Scan = "dvbs_m7_lnb_search";
    public static int MAX_TIME_ZONE = 34;
    public static final String MENU_MJC_MODE = "g_video__vid_mjc_mode";
    public static final String MHEG_PIN_PROTECTION = "g_misc__mheg_pin_protection";
    public static final String MJC = "UNDEFINE_MJC";
    public static final String MODE_DMR_CONTROL = "SETUP_dmr_contrl";
    public static final String MODE_LIST_STYLE = "SETUP_sundry_mode_style";
    public static final String MPEG_NR = "g_video__vid_mpeg_nr";
    public static final int MSG_SCAN_ABORT = 8;
    public static final int MSG_SCAN_CANCEL = 4;
    public static final int MSG_SCAN_COMPLETE = 1;
    public static final int MSG_SCAN_PROGRESS = 2;
    public static final int MSG_SCAN_UNKNOW = 0;
    public static final String MY_NET_PLACE = "SETUP_net_place";
    public static final String NOTIFY_SWITCH = "g_video__dovi_user_switch";
    public static final String NO_SIGNAL_AUTO_POWER_OFF = "no_signal_auto_power_off";
    public static final String OCEANIA_FREEVIEW = "g_misc__freeview_mode";
    public static final String OCEANIA_POSTAL = "g_eas__lct_st";
    public static final String OSD_LANGUAGE = "g_gui_lang__gui_language";
    public static final String PARENTAL_AGE_RATINGS = "PARENTAL_AGE_RATINGS";
    public static final String PARENTAL_AGE_RATINGS_EU = "PARENTAL_AGE_RATINGS_EU";
    public static final String PARENTAL_AGE_RATINGS_EU_OCEANIA_AUS = "PARENTAL_AGE_RATINGS_EU_EU_OCEANIA_AUS";
    public static final String PARENTAL_AGE_RATINGS_EU_SGP = "PARENTAL_AGE_RATINGS_EU_SGP";
    public static final String PARENTAL_AGE_RATINGS_EU_THL = "PARENTAL_AGE_RATINGS_EU_THL";
    public static final String PARENTAL_AGE_RATINGS_EU_ZAF = "PARENTAL_AGE_RATINGS_EU_ZAF";
    public static final String PARENTAL_BLOCK_UNRATED = "parental_block_unrated";
    public static final String PARENTAL_CANADIAN_ENGLISH_RATINGS = "parental_canadian_english_ratings";
    public static final String PARENTAL_CANADIAN_FRENCH_RATINGS = "parental_canadian_french_ratings";
    public static final String PARENTAL_CFG_RATING_BL_END_TIME = "g_rating__bl_end_type";
    public static final String PARENTAL_CFG_RATING_BL_START_TIME = "g_rating__bl_start_type";
    public static final String PARENTAL_CFG_RATING_BL_TYPE = "g_rating__bl_type";
    public static final String PARENTAL_CHANGE_PASSWORD = "parental_change_password";
    public static final String PARENTAL_CHANNEL_BLOCK = "parental_channel_block";
    public static final String PARENTAL_CHANNEL_BLOCK_CHANNELLIST = "parental_block_channellist";
    public static final String PARENTAL_CHANNEL_SCHEDULE_BLOCK = "parental_channel_schedule_block";
    public static final String PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST = "parental_channel_schedule_block_channellist";
    public static final String PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE = "parental_channel_schedule_block_MOde";
    public static final String PARENTAL_CLEAN_ALL = "parental_clean_all";
    public static final String PARENTAL_CONTENT_RATINGS = "PARENTAL_CONTENT_RATINGS";
    public static final String PARENTAL_ENTER_PASSWORD = "parental_enter_password";
    public static final String PARENTAL_INPUT_BLOCK = "parental_input_block";
    public static final String PARENTAL_INPUT_BLOCK_SOURCE = "UNDEFINE_parental_input_block_source";
    public static final String PARENTAL_OPEN_VCHIP = "parental_open_vchip";
    public static final String PARENTAL_OPEN_VCHIP_DIM = "parental_open_vchip_dim";
    public static final String PARENTAL_OPEN_VCHIP_LEVEL = "parental_open_vchip_level";
    public static final String PARENTAL_OPEN_VCHIP_REGIN = "parental_open_vchip_regin";
    public static final String PARENTAL_PASSWORD = "parental_password";
    public static final String PARENTAL_PASSWORD_NEW = "parental_password_new";
    public static final String PARENTAL_PASSWORD_NEW_RE = "parental_password_new_re";
    public static final String PARENTAL_PROGRAM_BLOCK = "parental_program_block";
    public static final String PARENTAL_RATINGS_ENABLE = "parental_ratings_enable";
    public static final String PARENTAL_TIF_CONTENT_RATGINS = "parental_tif_content_ratings";
    public static final String PARENTAL_TIF_CONTENT_RATGINS_SYSTEM = "parental_tif_content_ratings_system";
    public static final String PARENTAL_TIF_RATGINS_SYSTEM_CONTENT = "parental_tif_ratings_system_cnt";
    public static final String PARENTAL_TIME_INTERVAL_BLOCK = "parental_time_interval_block";
    public static final String PARENTAL_US_MOVIE_RATINGS = "parental_us_movie_ratings";
    public static final String PARENTAL_US_TV_RATINGS = "parental_us_tv_ratings";
    public static final String PHASE = "g_vga__vga_phase";
    public static final String PICTURE_MODE = "g_video__picture_mode";
    public static boolean PICTURE_MODE_dOVI = false;
    public static final String POWER_OFF_MUSIC = "POWER_OFF_MUSIC";
    public static final String POWER_OFF_TIMER = "g_timer__timer_off";
    public static final String POWER_ON_CH_AIR_MODE = "g_nav__air_on_time_ch";
    public static final String POWER_ON_CH_CABLE_MODE = "g_nav__cable_on_time_ch";
    public static final String POWER_ON_MUSIC = "POWER_ON_MUSIC";
    public static final String POWER_ON_TIMER = "g_timer__timer_on";
    public static final String POWER_ON_VALID_CHANNELS = "SETUP_poweron_valid_channels";
    public static final String POWER_SETTING_CONFIG_VALUE = "g_record_setting_power_setting";
    public static final String POWER_SETTING_VALUE = "livetv_power_setting_config";
    public static final String PQ_SPLIT_SCREEN_DEMO_MODE = "g_video__vid_pq_demo";
    public static final String PVR_PLAYBACK_START = "pvr_playback_start";
    public static final String PVR_START = "pvr_start";
    public static final String RESET_DEFAULT = "RESET_DEFAULT";
    public static final String RESET_SETTING = "g_video__dovi_reset_pic_setting";
    public static final String SATURATION = "g_video__vid_sat";
    public static final String SCAN_MODE = "g_scan_mode__scan_mode";
    public static final String SCAN_MODE_DVBC = "cfg_scan_mode_scan_mode_dvbc";
    public static final String SCART = "g_scart__scard";
    public static final String SCART1 = "g_scart__scard0";
    public static final String SCART2 = "g_scart__scard1";
    public static final int SCC_AUD_MTS_DUAL1 = 4;
    public static final int SCC_AUD_MTS_DUAL2 = 5;
    public static final int SCC_AUD_MTS_FM_MONO = 10;
    public static final int SCC_AUD_MTS_FM_STEREO = 11;
    public static final int SCC_AUD_MTS_MONO = 1;
    public static final int SCC_AUD_MTS_NICAM_DUAL1 = 8;
    public static final int SCC_AUD_MTS_NICAM_DUAL2 = 9;
    public static final int SCC_AUD_MTS_NICAM_MONO = 6;
    public static final int SCC_AUD_MTS_NICAM_STEREO = 7;
    public static final int SCC_AUD_MTS_STEREO = 2;
    public static final int SCC_AUD_MTS_SUB_LANG = 3;
    public static final int SCC_AUD_MTS_UNKNOWN = 0;
    public static final String SCHEDULE_PVR_CHANNELLIST = "SCHEDULE_PVR_CHANNELLIST";
    public static final String SCHEDULE_PVR_REMINDER_TYPE = "SCHEDULE_PVR_REMINDER_TYPE";
    public static final String SCHEDULE_PVR_REPEAT_TYPE = "SCHEDULE_PVR_REPEAT_TYPE";
    public static final String SCHEDULE_PVR_SRCTYPE = "SCHEDULE_PVR_SRCTYPE";
    public static final String SCREEN_MODE = "g_video__screen_mode";
    public static final String SELECT_MODE = "SETUP_select_mode";
    public static final String SETUP_ANALOG_CAPTION = "g_cc__cc_analog_cc";
    public static final String SETUP_APPLICATION = "application";
    public static final String SETUP_AUTO_CHANNEL_UPDATE = "g_misc__auto_svc_update";
    public static final String SETUP_BACKGROUND_COLOR = "g_cc__dis_op_bk_color";
    public static final String SETUP_BACKGROUND_OPACITY = "g_cc__dis_op_bk_opacity";
    public static final String SETUP_CAPTION_STYLE = "g_cc__dcs";
    public static final String SETUP_CHANNEL_NEW_SVC_ADDED = "g_menu__new_svc_added";
    public static final String SETUP_CHANNEL_UPDATE = "SETUP_channel_update";
    public static final String SETUP_CHANNEL_UPDATE_MSG = "g_menu__ch_update_msg";
    public static final String SETUP_DECODING_PAGE_LANGUAGE = "g_ttx_lang__ttx_decode_lang";
    public static final String SETUP_DEVICE_INFO = "SETUP_deivce_info";
    public static final String SETUP_DIGITAL_CAPTION = "g_cc__cc_digital_cc";
    public static final String SETUP_DIGITAL_STYLE = "SETUP_digital_style";
    public static final String SETUP_DIGITAL_TELETEXT_LANGUAGE = "g_ttx_lang__ttx_digtl_es_select";
    public static final String SETUP_DIVX_DEACTIVATION = "SETUP_divx_deactivation";
    public static final String SETUP_DIVX_REGISTRATION = "SETUP_divx_registration";
    public static final String SETUP_ENABLE_CAPTION = "g_cc__cc_caption";
    public static final String SETUP_FONT_COLOR = "g_cc__dis_op_ft_color";
    public static final String SETUP_FONT_OPACITY = "g_cc__dis_op_ft_opacity";
    public static final String SETUP_FONT_SIZE = "g_cc__dis_op_ft_size";
    public static final String SETUP_FONT_STYLE = "g_cc__dis_op_ft_style";
    public static final String SETUP_HBBTV = "SETUP_hbbtv";
    public static final String SETUP_HDMI = "SETUP_hdmi_setup";
    public static final String SETUP_LICENSE_INFO = "SETUP_license_info";
    public static final String SETUP_MSI = "g_misc__msi";
    public static final String SETUP_NETWORK = "SETUP_network";
    public static final String SETUP_OAD_DETECT = "SETUP_oad_detect";
    public static final String SETUP_OAD_SETTING = "SETUP_OADSetting";
    public static final String SETUP_OAD_SET_AUTO_DOWNLOAD = "g_oad__oad_sel_options_auto_download";
    public static final String SETUP_PIP_POP = "SETUP_pip_pop";
    public static final String SETUP_PIP_POP_MODE = "SETUP_pip_pop_mode";
    public static final String SETUP_PIP_POP_POSITION = "SETUP_pip_pop_position";
    public static final String SETUP_PIP_POP_SIZE = "SETUP_pip_pop_size";
    public static final String SETUP_PIP_POP_SOURCE = "SETUP_pip_pop_source";
    public static final String SETUP_POSTAL_CODE = "SETUP_postal_code";
    public static final String SETUP_POWER_ONCHANNEL_LIST = "SETUP_power_onchannel";
    public static final String SETUP_POWER_ON_CH = "SETUP_PowerOnCh";
    public static final String SETUP_RECORD_MODE = "g_record__av_rec_mode";
    public static final String SETUP_RECORD_QUALITY = "g_video__vid_rec_quality";
    public static final String SETUP_RECORD_SETTING = "SETUP_recordSetting";
    public static final String SETUP_REGION_PHILIPPINES_SETTING = "SETUP_regionSetting_philippines";
    public static final String SETUP_REGION_SETTING = "SETUP_regionSetting";
    public static final String SETUP_REGION_SETTING_LUZON = "SETUP_regionSetting_LUZON";
    public static final String SETUP_REGION_SETTING_MINDANAO = "SETUP_regionSetting_MINDANAO";
    public static final String SETUP_REGION_SETTING_SELECT = "SETUP_regionSetting_select";
    public static final String SETUP_REGION_SETTING_VISAYAS = "SETUP_regionSetting_VISAYAS";
    public static final String SETUP_SCHEDUCE_LIST = "SETUP_schedule_list";
    public static final String SETUP_SHIFTING_MODE = "g_record__rec_tshift_mode";
    public static final String SETUP_SIGNAL_FORMAT = "g_menu_only__hdmi_edid_index";
    public static final String SETUP_SUPERIMPOSE_SETUP = "g_cc__cc_si";
    public static final String SETUP_TELETEXT = "SETUP_teletext";
    public static final String SETUP_TIME_SET = "SETUP_time_set";
    public static final String SETUP_TIME_SETUP = "SETUP_time_setup";
    public static final String SETUP_TIME_ZONE = "SETUP_time_zone";
    public static final String SETUP_TTX_PRESENTATION_LEVEL = "g_ttx_lang__ttx_presentation_level";
    public static final String SETUP_UPGRADENET = "SETUP_upgradeNet";
    public static final String SETUP_US_SUB_TIME_ZONE = "g_time__time_zone";
    public static final String SETUP_US_TIME_ZONE = "SETUP_us_time_zone";
    public static final String SETUP_VERSION_INFO = "SETUP_version_info";
    public static final String SETUP_WINDOW_COLOR = "g_cc__dis_op_win_color";
    public static final String SETUP_WINDOW_OPACITY = "g_cc__dis_op_win_opacity";
    public static final String SHARPNESS = "g_video__vid_shp";
    private static final int SIX_SLEEP_TIME_MS = 21300000;
    public static final String SLEEP_TIMER = "SETUP_sleep_timer";
    public static final String SOUNDTRACKS_GET_CURRENT = "g_menu__soundtrackscurrent";
    public static final String SOUNDTRACKS_GET_ENABLE = "g_menu__soundtracksenable";
    public static final String SOUNDTRACKS_GET_STRING = "soundtracksgetstring";
    public static final String SOUNDTRACKS_GET_TOTAL = "g_menu__soundtrackstotal";
    public static final String SOUNDTRACKS_SET_DEINIT = "g_menu__soundtracksdeinit";
    public static final String SOUNDTRACKS_SET_INIT = "g_menu__soundtracksinit";
    public static final String SOUNDTRACKS_SET_SELECT = "g_menu__soundtracksselect";
    public static final String SOUND_TRACKS = "g_menu__soundtracks";
    public static final String SPDIF_DELAY = "g_audio__spdif_delay";
    public static final String SPDIF_MODE = "g_audio__spdif";
    public static final String SPEAKER_MODE = "g_audio__aud_out_port";
    public static final String SRS_MODE = "g_audio__aud_surround";
    public static final int STEP_BIG_VALUE = 10;
    public static final int STEP_VALUE = 1;
    public static final String SUBTITLE_GROUP = "SUBTITLE_GROUP";
    public static final String SUBTITLE_TYPE = "g_subtitle__subtitle_attr";
    public static final String SUPER_RESOLUTION = "g_video__vid_super_resolution";
    public static final String SVL_ID = "g_bs__bs_svl_id";
    public static final String SYM_RATE = "dvbc_single_rf_scan_sym_rate";
    public static final String SYSTEM_INFORMATION = "SETUP_system_information";
    private static final String TAG = "MenuConfigManager";
    public static final String TIMER1 = "SETUP_timer1";
    public static final String TIMER2 = "SETUP_timer2";
    public static final String TIMESHIFT_START = "timeshift_start";
    public static final String TIME_DATE = "SETUP_date";
    public static final String TIME_END_DATE = "SETUP_end_date";
    public static final String TIME_END_TIME = "SETUP_end_time";
    public static final String TIME_START_DATE = "SETUP_start_date";
    public static final String TIME_START_TIME = "SETUP_start_time";
    public static final String TIME_TIME = "SETUP_time";
    public static String TKGS_FAC_SETUP_AVAIL_CONDITION = MtkTvConfigTypeBase.CFG_MISC_TKGS_AVAILABILITY_COND;
    public static final String TKGS_HIDD_LOCS = "tkgs_hidden_locs";
    public static final String TKGS_LOC_FREQ = "tkgs_loc_freqency";
    public static final String TKGS_LOC_ITEM = "tkgs_loc_item";
    public static final String TKGS_LOC_ITEM_ADD = "tkgs_loc_item_add";
    public static final String TKGS_LOC_ITEM_DELETE = "tkgs_loc_item_delete";
    public static final String TKGS_LOC_ITEM_HIDD_CLEANALL = "tkgs_loc_item_clean_all_hidd";
    public static final String TKGS_LOC_ITEM_SAVE = "tkgs_loc_item_save";
    public static final String TKGS_LOC_ITEM_UPDATE = "tkgs_loc_item_update";
    public static final String TKGS_LOC_LIST = "tkgs_loc_list";
    public static final int TKGS_LOC_OPERATE_ADD = 225;
    public static final int TKGS_LOC_OPERATE_DELETE = 227;
    public static final int TKGS_LOC_OPERATE_UPDATE = 226;
    public static final String TKGS_LOC_POLAZATION = "tkgs_loc_polazation";
    public static final String TKGS_LOC_SVC_ID = "tkgs_loc_sevice_id";
    public static final int TKGS_LOC_SVC_ID_MAX = 8191;
    public static final int TKGS_LOC_SVC_ID_MIN = 0;
    public static final String TKGS_LOC_SYMBOL_RATE = "tkgs_loc_sysbol_rate";
    public static final String TKGS_OPER_MODE = "g_misc__tkgs_operating_mode";
    public static final String TKGS_PREFER_LIST = "tkgs_prefer_list";
    public static final String TKGS_RESET_TAB_VERSION = "tkgs_reset_tab_version";
    public static final String TKGS_SETTING = "tkgs_setting";
    public static final String TREBLE = "g_audio__aud_treble";
    public static final String TUNER_MODE = "g_bs__bs_src";
    public static final String TUNER_MODE2 = "g_misc__2nd_tuner_type";
    public static final String TUNER_MODE_CN_USER_SET = "g_bs__bs_user_src";
    public static final String TUNER_MODE_PREFER_SAT = "g_two_sat_chlist__preferred_sat";
    public static final String TV_ANALOG_SCAN = "analog_scan";
    public static final String TV_ANANLOG_SCAN_DOWN = "scan_down";
    public static final String TV_ANANLOG_SCAN_UP = "scan_up";
    public static final String TV_AUDIO_LANGUAGE = "g_aud_lang__aud_language";
    public static final String TV_AUDIO_LANGUAGE_2 = "g_aud_lang__aud_2nd_language";
    public static final String TV_AUTO_FINETUNE = "UNDEFINE_channel_edit_aft";
    public static final String TV_CHANNEL = "tv_channel";
    public static final String TV_CHANNELFINE_TUNE = "Analog ChannelFine Tune";
    public static final String TV_CHANNELFINE_TUNE_EDIT_LIST = "UNDEFINE_ChannelFine Tune";
    public static final String TV_CHANNEL_AFTER_SCAN_UK_REGION = "tv_channel_after_scan_UK_region";
    public static final String TV_CHANNEL_CLEAR = "channel_clean";
    public static final String TV_CHANNEL_COLOR_SYSTEM = "CHANNELEDIT_color_system";
    public static final String TV_CHANNEL_DECODE = "channel_decode";
    public static final String TV_CHANNEL_DECODE_LIST = "channel_decode_list";
    public static final String TV_CHANNEL_EDIT = "channel_edit";
    public static final String TV_CHANNEL_EDIT_LIST = "UNDEFINE_channel_edit_list";
    public static final String TV_CHANNEL_END_FREQUENCY = "UNDEFINE_channel_end_frequency";
    public static final String TV_CHANNEL_INACTIVE_LIST = "inactive_channel_list";
    public static final String TV_CHANNEL_MOVE = "channel_move";
    public static final String TV_CHANNEL_MOVE_CHANNELLIST = "tv_channel_move_channellist";
    public static final String TV_CHANNEL_NAME = "UNDEFINE_channel_edit_name";
    public static final String TV_CHANNEL_NO = "UNDEFINE_channel_edit_no";
    public static final String TV_CHANNEL_NW_ANALOG_NAME = "UNDEFINE_channel_nw_analog_name";
    public static final String TV_CHANNEL_NW_NAME = "UNDEFINE_channel_nw_name";
    public static final String TV_CHANNEL_SATELLITE_ADD = "Satellite Add";
    public static final String TV_CHANNEL_SA_NAME = "UNDEFINE_channel_edit_sa_name";
    public static final String TV_CHANNEL_SA_NO = "UNDEFINE_channel_edit_sa_no";
    public static final String TV_CHANNEL_SA_TSNAME = "UNDEFINE_channel_edit_sa_tsname";
    public static final String TV_CHANNEL_SCAN = "channel_scan";
    public static final String TV_CHANNEL_SCAN_DVBC = "channel_scan_dvbc_fulls";
    public static final String TV_CHANNEL_SCAN_DVBC_OPERATOR = "channel_scan_dvbc_fulls_operator";
    public static final String TV_CHANNEL_SCAN_DVBT = "channel_scan_dvbt_full";
    public static final String TV_CHANNEL_SKIP = "channel_skip";
    public static final String TV_CHANNEL_SKIP_CHANNELLIST = "tv_channel_skip_channellist";
    public static final String TV_CHANNEL_SORT = "channel_sort";
    public static final String TV_CHANNEL_SORT_CHANNELLIST = "tv_channel_sort_channellist";
    public static final String TV_CHANNEL_STARTSCAN = "start_scan";
    public static final String TV_CHANNEL_STARTSCAN_CEC_CN = "start_scan_dvbc_cn";
    public static final String TV_CHANNEL_START_FREQUENCY = "UNDEFINE_channel_start_frequency";
    public static final String TV_CI_CAM_SCAN = "tv_ci_cam__scan";
    public static final String TV_DUAL_TUNER = "g_misc__2nd_channel_enable";
    public static final String TV_DVBC_CHANNELS_START_SCAN = "dvbc_scan_channel_start";
    public static final String TV_DVBC_SCAN_FREQUENCY = "tv_dvbc_scan_frequency";
    public static final String TV_DVBC_SCAN_NETWORKID = "tv_dvbc_scan_networkid";
    public static final String TV_DVBC_SCAN_TYPE = "tv_dvbc_scan_type";
    public static final String TV_DVBC_SINGLE_RF_SCAN = "tv_dvbc_single_rf_scan";
    public static final String TV_DVBT_SINGLE_RF_SCAN = "tv_dvbt_single_rf_scan";
    public static final String TV_EU_CHANNEL = "tveuChannel";
    public static final String TV_FAVORITE_NETWORK = "favorite_network_select";
    public static final String TV_FINETUNE = "channel_edit_finetune";
    public static final String TV_FREEZE_CHANNEL = "g_menu__ch_frz_chg";
    public static final String TV_FREQ = "UNDEFINE_channel_edit_frequency";
    public static final String TV_FREQ_SA = "UNDEFINE_channel_edit_frequency_sa";
    public static final String TV_MTS_MODE = "g_audio__aud_mts";
    public static final String TV_SA_CHANNEL_EDIT = "channel_sa_edit";
    public static final String TV_SINGLE_RF_SCAN_CHANNELS = "single_rf_scan_rf_channel";
    public static final String TV_SINGLE_RF_SCAN_CN = "single_rf_scan_cn";
    public static final String TV_SINGLE_SCAN_MODULATION = "UNDEFINE_tv_single_scan_modu";
    public static final String TV_SINGLE_SCAN_RF_CHANNEL = "UNDEFINE_tv_single_rf_channel";
    public static final String TV_SINGLE_SCAN_SIGNAL_LEVEL = "UNDEFINE_tv_single_scan_signal_level";
    public static final String TV_SINGLE_SCAN_SIGNAL_QUALITY = "UNDEFINE_tv_singl_scan_signal_quality";
    public static final String TV_SKIP = "UNDEFINE_channel_edit_skip";
    public static final String TV_SOUND_SYSTEM = "CHANNELEDIT_sound_system";
    public static final String TV_STORE = "channel_edit_store";
    public static final String TV_SYSTEM = "SCAN_OPTION_TV_SYSTEM";
    public static final int TV_SYS_MASK_B = 2;
    public static final int TV_SYS_MASK_D = 8;
    public static final int TV_SYS_MASK_G = 64;
    public static final int TV_SYS_MASK_I = 256;
    public static final int TV_SYS_MASK_K = 1024;
    public static final int TV_SYS_MASK_L = 4096;
    public static final int TV_SYS_MASK_L_PRIME = 8192;
    public static final int TV_SYS_MASK_M = 16384;
    public static final int TV_SYS_MASK_N = 32768;
    public static final String TV_UPDATE_SCAN = "update_scan";
    public static final String TV_UPDATE_SCAN_DVBT_UPDATE = "channel_scan_dvbt_UPDATE";
    public static final String TYPE = "g_audio__aud_type";
    public static final String USER_PREFERENCE = "TODO:MtkTvConfigTypeBase.CFG_MISC_CI_AMMI_PRIORITY_VALUE";
    public static final String US_SCAN_MODE = "usg_scan_mode__scan_mode";
    public static final String VERSION_INFO = "SETUP_version_info";
    public static final String VGA = "SUB_VGA";
    public static final String VGA_MODE = "g_video__vid_vga_mode";
    public static final String VIDEO_3D = "g_video__vid_3d_item";
    public static final String VIDEO_3D_3T2 = "g_video__vid_3d_to_2d";
    public static final String VIDEO_3D_DISTANCE = "g_video__vid_3d_distance";
    public static final String VIDEO_3D_FIELD = "g_video__vid_3d_fld_depth";
    public static final String VIDEO_3D_IMG_SFTY = "g_video__vid_3d_img_sfty";
    public static final String VIDEO_3D_LF = "g_video__vid_3d_lr_switch";
    public static final String VIDEO_3D_MODE = "g_video__vid_3d_mode";
    public static final String VIDEO_3D_NAV = "g_video__vid_3d_nav_auto";
    public static final String VIDEO_3D_OSD_DEPTH = "g_video__vid_3d_osd_depth";
    public static final String VIDEO_3D_PROTRUDE = "g_video__vid_3d_protruden";
    public static final String VIDEO_ADVANCED_VIDEO = "advancedVideo";
    public static final String VIDEO_COLOR_TEMPERATURE = "colorTemprature";
    public static final String VIDEO_HDR = "g_video__vid_hdr";
    public static final String VISUALLY_HEADPHONE = "g_audio__aud_ad_hdphone";
    public static final String VISUALLY_IMPAIRED = "SUB_VISUALLYIMPAIRED";
    public static final String VISUALLY_IMPAIRED_AUDIO = "VISUALLY_IMPAIRED_AUDIO";
    public static final String VISUALLY_PAN_FADE = "g_audio__aud_ad_fade_pan";
    public static final String VISUALLY_SPEAKER = "g_audio__aud_ad_speaker";
    public static final String VISUALLY_VOLUME = "g_audio__aud_ad_volume";
    public static final String VPOSITION = "g_vga__vga_pos_v";
    public static final String WAKEUP_VGA = "g_misc__wakeup_reason";
    public static final int WIFI_COMMON_BIND = 0;
    public static final int WIFI_COMMON_MANUAL_FAIL = 9;
    public static final int WIFI_COMMON_MANUAL_INVAPASS = 7;
    public static final int WIFI_COMMON_MANUAL_INVASSID = 6;
    public static final int WIFI_COMMON_MANUAL_SUCCESS = 8;
    public static final int WIFI_COMMON_MANUAL_TIMEOUT = 17;
    public static final int WIFI_COMMON_NODONGLE = 1;
    public static final int WIFI_COMMON_NO_AP = 18;
    public static final int WIFI_COMMON_NO_WPS_AP = 19;
    public static final int WIFI_COMMON_PBC_FAIL = 13;
    public static final int WIFI_COMMON_PBC_HINT = 15;
    public static final int WIFI_COMMON_PBC_SUCCESS = 14;
    public static final int WIFI_COMMON_PIN_FAIL = 11;
    public static final int WIFI_COMMON_PIN_SUCCESS = 12;
    public static final int WIFI_COMMON_SCAN_FAIL = 2;
    public static final int WIFI_COMMON_SCAN_INVALID = 3;
    public static final int WIFI_COMMON_SCAN_SUCCESS = 4;
    public static final int WIFI_COMMON_SCAN_TIMEOUT = 16;
    public static final int WIFI_CONNECT_MANUAL = 2;
    public static final int WIFI_CONNECT_PBC = 6;
    public static final int WIFI_CONNECT_PIN_AP = 4;
    public static final int WIFI_CONNECT_PIN_AUTO = 3;
    public static final int WIFI_CONNECT_SCAN = 1;
    public static final int WIFI_CONNECT_SCANING = 0;
    public static final int WIFI_CONNECT_WPS_SCANING = 5;
    public static final int WIFI_INPUT_MANUAL_PASS = 2;
    public static final int WIFI_INPUT_MANUAL_SSID = 1;
    public static final int WIFI_INPUT_SCAN_PASS = 0;
    public static final int WIFI_SCAN_NORMAL = 0;
    public static final int WIFI_SCAN_WPS = 1;
    public static final int W_CONFIRM_AUTO = 3;
    public static final int W_CONFIRM_NONE = 1;
    public static final int W_CONFIRM_UNKNOWN = 0;
    public static final int W_CONFIRM_WEP = 2;
    public static final int W_CONFIRM_WPA2_PSK_AES = 6;
    public static final int W_CONFIRM_WPA2_PSK_TKIP = 5;
    public static final int W_CONFIRM_WPA_PSK_AES = 4;
    public static final int W_CONFIRM_WPA_PSK_TKIP = 7;
    public static final int W_SECURITY_AES = 3;
    public static final int W_SECURITY_NONE = 0;
    public static final int W_SECURITY_TKIP = 2;
    public static final int W_SECURITY_WEP = 1;
    private static MenuConfigManager mConfigManager;
    public static HashMap<String, Integer> mScreenMode = new HashMap<>();
    public static HashMap<Integer, String> mScreenModeReverse = new HashMap<>();
    public static int[] zoneValue = {0, 0, MtkTvTimeFormatBase.SECONDS_PER_HOUR, 7200, 10800, 12600, 14400, 16200, 18000, 19800, 20700, 21600, 23400, 25200, 28800, 32400, 34200, 36000, 39600, 43200, 45900, 46800, -43200, -39600, -36000, -32400, -28800, -25200, -21600, -18000, -14400, -12600, -10800, -7200, -3600};
    private AsyncOSDLan att;
    private final int defaultValue = 0;
    private final List<ContentRatingSystem> mContentRatingSystems = new ArrayList();
    ContentRatingsManager mContentRatingsManager = null;
    /* access modifiers changed from: private */
    public final Context mContext;
    Action mItem;
    ParentalControlSettings mParentalControlSettings;
    private List<TvContentRatingSystemInfo> mRatingSystem = null;
    private String[] mScreenModeList;
    private final TVContent mTV;
    private final MtkTvConfig mTvConfig;
    private TvInputManager mTvInputManager = null;
    private final int maxValue = 0;
    private final int minValue = 0;
    private final MtkTvAVModeBase mtkMode;
    LanguageUtil osdLanguage;
    private ContentRatingsParser parser;
    private ProgressDialog pdialog;
    SaveValue save;
    private final int tvOptionValue = 0;

    private MenuConfigManager(Context context) {
        this.mContext = context;
        this.mTV = TVContent.getInstance(context);
        this.save = SaveValue.getInstance(context);
        this.mTvConfig = MtkTvConfig.getInstance();
        this.mtkMode = new MtkTvAVModeBase();
        if (mScreenMode.isEmpty()) {
            init();
        }
    }

    private void init() {
        String[] array = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us);
        for (int i = 0; i < array.length; i++) {
            mScreenMode.put(array[i], Integer.valueOf(i));
            mScreenModeReverse.put(Integer.valueOf(i), array[i]);
        }
    }

    public void reloadScreenModes() {
        String[] array = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us);
        for (int i = 0; i < array.length; i++) {
            mScreenMode.put(array[i], Integer.valueOf(i));
            mScreenModeReverse.put(Integer.valueOf(i), array[i]);
        }
    }

    public static MenuConfigManager getInstance(Context context) {
        if (mConfigManager == null) {
            mConfigManager = new MenuConfigManager(context);
        }
        return mConfigManager;
    }

    public ArrayList<Boolean> get3DConfig() {
        ArrayList<Boolean> m3DList = new ArrayList<>();
        boolean m3DModeFlag = isConfigEnabled("g_video__vid_3d_mode");
        boolean m3DNavFlag = isConfigEnabled("g_video__vid_3d_nav_auto");
        boolean m3D2DFlag = isConfigEnabled("g_video__vid_3d_to_2d");
        boolean m3DDepthFieldFlag = isConfigEnabled("g_video__vid_3d_fld_depth");
        boolean m3DProtrudeFlag = isConfigEnabled("g_video__vid_3d_protruden");
        boolean m3DDistanceFlag = isConfigEnabled("g_video__vid_3d_distance");
        boolean m3DImgSafetyFlag = isConfigEnabled("g_video__vid_3d_img_sfty");
        boolean m3DLrSwitchFlag = isConfigEnabled("g_video__vid_3d_lr_switch");
        boolean m3DOsdDepthFlag = isConfigEnabled("g_video__vid_3d_osd_depth");
        m3DList.add(Boolean.valueOf(m3DModeFlag));
        m3DList.add(Boolean.valueOf(m3DNavFlag));
        m3DList.add(Boolean.valueOf(m3D2DFlag));
        m3DList.add(Boolean.valueOf(m3DDepthFieldFlag));
        m3DList.add(Boolean.valueOf(m3DProtrudeFlag));
        m3DList.add(Boolean.valueOf(m3DDistanceFlag));
        m3DList.add(Boolean.valueOf(m3DImgSafetyFlag));
        m3DList.add(Boolean.valueOf(m3DLrSwitchFlag));
        m3DList.add(Boolean.valueOf(m3DOsdDepthFlag));
        return m3DList;
    }

    public int getValueFromPrefer(String itemID) {
        return this.save.readValue(itemID);
    }

    public void setValueToPrefer(String itemID, int value) {
        this.save.saveValue(itemID, value);
    }

    public int getMin(String itemID) {
        return MtkTvConfig.getMinValue(this.mTvConfig.getMinMaxConfigValue(itemID));
    }

    public int getMax(String itemID) {
        if (itemID.equals("g_audio__aud_latency")) {
            return 680;
        }
        int value = this.mTvConfig.getMinMaxConfigValue(itemID);
        MtkLog.d(TAG, "getMax, value:" + value);
        return MtkTvConfig.getMaxValue(value);
    }

    public String[] getSupporScreenMode(int[] array) {
        if (array == null) {
            return null;
        }
        this.mScreenModeList = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            this.mScreenModeList[i] = mScreenModeReverse.get(Integer.valueOf(array[i]));
        }
        return this.mScreenModeList;
    }

    public String[] getScreenModeList() {
        if (this.mScreenModeList == null) {
            this.mScreenModeList = new String[0];
        }
        return this.mScreenModeList;
    }

    public int getScreenMode(String[] screenMode, String itemID) {
        int value = this.mTvConfig.getConfigValue(itemID);
        String key = mScreenModeReverse.get(Integer.valueOf(value));
        int i = 0;
        while (true) {
            if (i >= screenMode.length) {
                break;
            } else if (key.equals(screenMode[i])) {
                value = i;
                break;
            } else {
                i++;
            }
        }
        MtkLog.d(TAG, "getScreenMode, value:" + key + "," + value);
        return value;
    }

    public int getDefault(String itemID) {
        int value;
        int value2;
        int value3;
        if (itemID.startsWith(PARENTAL_TIF_CONTENT_RATGINS_SYSTEM) || itemID.startsWith(PARENTAL_TIF_RATGINS_SYSTEM_CONTENT)) {
            return getContentRatingValue(itemID);
        }
        if (itemID.startsWith(PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE)) {
            String channelId = itemID.substring(PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE.length());
            return this.save.readValue(PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE + channelId);
        }
        int value4 = this.mTvConfig.getConfigValue(itemID);
        MtkLog.d(TAG, "getDefault, itemID:" + itemID + ", value:" + value4);
        if (itemID.equals("g_audio__dolby_cmpss")) {
            if (value4 == 0) {
                return 0;
            }
            if (value4 == 3 || CommonIntegration.isEURegion()) {
                return 1;
            }
            return 0;
        } else if (itemID.equals("g_video__vid_di_film_mode")) {
            if (CommonIntegration.isCNRegion()) {
                if (value4 > 2) {
                    return 2;
                }
                return value4;
            } else if (value4 > 1) {
                return 1;
            } else {
                return value4;
            }
        } else if (itemID.equals("no_signal_auto_power_off")) {
            int value5 = this.save.readValue("no_signal_auto_power_off");
            if (value5 < 20) {
                return 20;
            }
            return value5;
        } else if (itemID.equals("g_disp__disp_gamma")) {
            if (value4 < 1) {
                return 0;
            }
            return value4 - 1;
        } else if (itemID.equals("g_bs__bs_src")) {
            if (CommonIntegration.getInstance().isDualTunerEnable() && CommonIntegration.getInstance().getCurrentFocus().equals("sub")) {
                value4 = this.mTV.getConfigValue("g_misc__2nd_tuner_type");
            }
            if (CommonIntegration.isCNRegion()) {
                value4 = this.mTV.getConfigValue("g_bs__bs_user_src");
            }
            MtkLog.d(TAG, "get default TUNER_MODE>>" + value4);
            if (value4 < 2 || ScanContent.getDVBSOperatorList(this.mContext).size() <= 0) {
                return value4;
            }
            if (CommonIntegration.getInstance().isPreferSatMode()) {
                return 2;
            }
            return 3;
        } else {
            if (itemID.equals("g_video__screen_mode")) {
                if (!this.mTV.isCurrentSourceVGA()) {
                    if (CommonIntegration.getInstance().isPOPState()) {
                        if (value4 > 3) {
                            return 0;
                        }
                        return value4;
                    } else if (!CommonIntegration.getInstance().isPIPState() || !"sub".equalsIgnoreCase(CommonIntegration.getInstance().getCurrentFocus())) {
                        if (CommonIntegration.isSARegion()) {
                            MtkLog.i("OptionView", "sa value:" + value4);
                            if ("main".equalsIgnoreCase(CommonIntegration.getInstance().getCurrentFocus())) {
                                if (value4 == 6) {
                                    value4 = 4;
                                } else if (value4 == 7) {
                                    value4 = 0;
                                }
                                MtkLog.i("OptionView", "sa sa value:" + value4);
                                return value4;
                            } else if (value4 == 7) {
                                return 0;
                            } else {
                                if (value4 == 5) {
                                    return 3;
                                }
                                if (value4 == 3) {
                                    return 4;
                                }
                                if (value4 == 6) {
                                    return 5;
                                }
                                if (value4 == 1 || value4 == 2) {
                                    return value4;
                                }
                                return 0;
                            }
                        } else if (CommonIntegration.isUSRegion()) {
                            if (CommonIntegration.getInstance().getCurChInfo() instanceof MtkTvAnalogChannelInfo) {
                                if (CommonIntegration.getInstance().isPIPState()) {
                                    if (value4 == 7) {
                                        return 0;
                                    }
                                    if (value4 == 6) {
                                        return 4;
                                    }
                                    if (value4 == 1 || value4 == 2 || value4 == 3) {
                                        return value4;
                                    }
                                    return 0;
                                } else if (value4 == 7) {
                                    return 0;
                                } else {
                                    if (value4 == 5) {
                                        return 3;
                                    }
                                    if (value4 == 3) {
                                        return 4;
                                    }
                                    if (value4 == 6) {
                                        return 5;
                                    }
                                    if (value4 == 1 || value4 == 2) {
                                        return value4;
                                    }
                                    return 0;
                                }
                            } else if (value4 == 7) {
                                return 0;
                            } else {
                                if (value4 == 1) {
                                    return 1;
                                }
                                if (value4 == 3) {
                                    return 2;
                                }
                                return 0;
                            }
                        }
                    } else if (CommonIntegration.isUSRegion()) {
                        if (value4 > 3) {
                            return 0;
                        }
                        return value4;
                    } else if (value4 > 2) {
                        return 0;
                    } else {
                        return value4;
                    }
                } else if (value4 == 3 || value4 == 6) {
                    return 2;
                } else {
                    if (value4 == 2) {
                        return 1;
                    }
                    if (value4 == 1) {
                        return 0;
                    }
                    return 0;
                }
            }
            if (itemID.equals("g_audio__aud_mts")) {
                return value4;
            }
            if (itemID.equals(PARENTAL_AGE_RATINGS_EU_SGP)) {
                int value6 = this.mTV.getSgpTIFRatingPlus();
                MtkLog.d(TAG, "getSgpRatingValue ==" + value6);
                return value6;
            } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_THL)) {
                int value7 = this.mTV.getThlTIFRatingPlus();
                MtkLog.d(TAG, "getThlTIFRatingPlus ==" + value7);
                return value7;
            } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_ZAF)) {
                int value8 = this.mTV.getZafTIFRatingPlus();
                MtkLog.d(TAG, "getZafRatingValue ==" + value8);
                return value8;
            } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU)) {
                if (MarketRegionInfo.isFunctionSupport(21)) {
                    value3 = this.mTV.getDVBTIFRatingPlus();
                    if (MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("FRA") && value3 >= 3) {
                        value3--;
                    }
                    MtkLog.d("biaoqing", "value1 ==" + value3);
                } else {
                    value3 = this.mTV.getDVBRating().getDVBAgeRatingSetting();
                }
                if (value3 >= 3 && value3 <= 18) {
                    return value3 - 2;
                }
                if (!MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("FRA")) {
                    return 0;
                }
                if (value3 == 2) {
                    return value3 - 2;
                }
                return 18 - 3;
            } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_OCEANIA_AUS)) {
                int value9 = this.mTV.getDVBRating().getDVBAgeRatingSetting();
                if (value9 < 1) {
                    return 0;
                }
                if (value9 <= 1) {
                    return 7;
                }
                if (value9 <= 9) {
                    return 6;
                }
                if (value9 <= 11) {
                    return 5;
                }
                if (value9 <= 13) {
                    return 4;
                }
                if (value9 <= 15) {
                    return 3;
                }
                if (value9 <= 17) {
                    return 2;
                }
                if (value9 > 17) {
                    return 1;
                }
                return value9;
            } else if (itemID.equals(PARENTAL_AGE_RATINGS)) {
                if (MarketRegionInfo.isFunctionSupport(31)) {
                    return this.mTV.getSATIFAgeRating();
                }
                return this.mTV.getIsdbRating().getISDBAgeRatingSetting();
            } else if (itemID.equals(PARENTAL_CONTENT_RATINGS)) {
                if (MarketRegionInfo.isFunctionSupport(31)) {
                    int value10 = this.mTV.getSATIFContentRating();
                    MtkLog.d(TAG, "getDefault F_TIF_SA_RATING = YESValue = " + value10);
                    return value10;
                }
                int value11 = this.mTV.getIsdbRating().getISDBContentRatingSetting();
                MtkLog.d(TAG, "getDefault F_TIF_SA_RATING = NOValue = " + value11);
                return value11;
            } else if (itemID.equals("g_timer__timer_on")) {
                MtkLog.d(TAG, "value:" + value4 + "ON_ONCE:" + this.mTV.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE));
                if (value4 < 0) {
                    value2 = 1;
                    if (this.mTV.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE) < 0) {
                        value2 = 2;
                    }
                } else {
                    value2 = 0;
                    if (this.mTV.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE) < 0) {
                        value2 = 2;
                    }
                }
                this.save.saveValue(itemID, value2);
                return value2;
            } else if (itemID.equals("g_timer__timer_off")) {
                if (value4 < 0) {
                    value = 1;
                    if (this.mTV.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE) < 0) {
                        value = 2;
                    }
                } else {
                    value = 0;
                    if (this.mTV.getConfigValue(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE) < 0) {
                        value = 2;
                    }
                }
                this.save.saveValue(itemID, value);
                return value;
            } else if (itemID.equals("g_country__country_rid")) {
                MtkLog.d(TAG, "get COUNTRY_REGION_ID>>>" + value4);
                if (!MarketRegionInfo.isFunctionSupport(3) || !this.mTV.isAusCountry()) {
                    switch (value4) {
                        case 1:
                            return 0;
                        case 2:
                            return 1;
                        default:
                            return 0;
                    }
                } else if (value4 < 2 || value4 > 8) {
                    return 0;
                } else {
                    return value4 - 2;
                }
            } else if (itemID.equalsIgnoreCase("g_audio__spdif")) {
                if (value4 == 3 || value4 == 2) {
                    return 2;
                }
                if (value4 > 3) {
                    return 3;
                }
                return value4;
            } else if (itemID.equals("g_audio__spdif_delay")) {
                if (value4 <= -1 || value4 >= 26) {
                    return Cea708CCParser.Const.CODE_C1_DLW;
                }
                return value4 * 10;
            } else if (itemID.equalsIgnoreCase("g_audio__dolby_dmix")) {
                int value12 = this.mTV.getConfigValue(itemID);
                if (CommonIntegration.isEURegion()) {
                    return value12;
                }
                if (value12 == 2) {
                    return 0;
                }
                if (value12 == 11) {
                    return 1;
                }
                if (value12 == 1) {
                    return 2;
                }
                return value12;
            } else if (!itemID.equalsIgnoreCase("g_video__vid_3d_mode")) {
                return value4;
            } else {
                int value13 = this.mTV.getConfigValue(itemID);
                if (this.mTV.getConfigValue("g_video__vid_3d_nav_auto") == 1) {
                    if (value13 == 0) {
                        return 0;
                    }
                    return 1;
                } else if (this.mTV.getConfigValue("g_video__vid_3d_nav_auto") != 0) {
                    return value13;
                } else {
                    if (value13 == 0) {
                        return 0;
                    }
                    if (value13 == 2) {
                        return 1;
                    }
                    if (value13 == 4) {
                        return 2;
                    }
                    if (value13 == 5) {
                        return 3;
                    }
                    if (value13 == 8) {
                        return 4;
                    }
                    return value13;
                }
            }
        }
    }

    public String getConfigString(String cfgID) {
        return this.mTV.getConfigString(cfgID);
    }

    public int getDefaultScan(String itemID) {
        int value = 0;
        if (this.mTV != null) {
            value = this.mTV.getConfigValue(itemID);
        }
        MtkLog.d(TAG, "getDefaultScanvalue>>>" + value);
        return value;
    }

    public void setValue(String itemID, Object newValue) {
        if (itemID.equals("g_video__screen_mode")) {
            int index = Integer.valueOf((String) newValue).intValue();
            String[] temp = MenuDataHelper.getInstance(this.mContext).getScreenMode();
            if (temp != null && temp.length >= index) {
                int stored_value = mScreenMode.get(temp[index]).intValue();
                MtkLog.d(TAG, "setValue, " + itemID + "," + newValue + "," + stored_value);
                if (this.mtkMode != null) {
                    this.mtkMode.setScreenMode(stored_value);
                }
            }
        } else if ((newValue instanceof Integer) != 0) {
            MtkLog.d(TAG, "instanceof Integer");
            Integer new_name = (Integer) newValue;
            MtkLog.d(TAG, "new_name " + new_name);
            setValue(itemID, new_name.intValue(), this.mItem);
        } else if (newValue instanceof String) {
            MtkLog.d(TAG, "instanceof String");
            setValue(itemID, Integer.valueOf((String) newValue).intValue(), this.mItem);
        }
    }

    public void setValueDefault(String itemID) {
        if (itemID.equals(FACTORY_PRESET_CH_DUMP)) {
            this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_PRE_CH_DUMP_CH_INFO_2USB, 0);
        } else if (itemID.equals(FACTORY_PRESET_CH_PRINT)) {
            this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_PRE_CH_DUMP_CH_INFO_2TERM, 0);
        } else if (itemID.equals(FACTORY_PRESET_CH_RESTORE)) {
            this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_PRE_CH_LOAD_PRESET_CH, 0);
        } else if (itemID.equals("g_video__dovi_reset_pic_setting")) {
            this.mTV.setConfigValue("g_video__dovi_reset_pic_setting", 0);
        }
    }

    public void setValue(String itemID, int value) {
        setValue(itemID, value, this.mItem);
    }

    public void setValue(String itemID, int value, Action item) {
        int auto_sleep;
        int value2;
        int value3;
        int value4;
        int value5;
        MtkLog.d(TAG, "set value: " + itemID + "---" + value);
        this.mItem = item;
        if (itemID != null && this.mTV != null) {
            if (itemID.equals("g_video__dovi_user_switch")) {
                if (this.mTV != null) {
                    this.mTV.setConfigValue(itemID, value);
                }
            } else if (itemID.startsWith(PARENTAL_TIF_CONTENT_RATGINS_SYSTEM) || itemID.startsWith(PARENTAL_TIF_RATGINS_SYSTEM_CONTENT)) {
                setContentRatingValue(itemID, value);
            } else if (itemID.equalsIgnoreCase("g_misc__uart_factory_mode")) {
                this.mTV.setConfigValue(itemID, value);
                this.mContext.sendBroadcast(new Intent("android.mtk.intent.action.UART_SETTINGS_CHANGED"));
            } else {
                if (itemID.equalsIgnoreCase("no_signal_auto_power_off")) {
                    this.save.saveValue("no_signal_auto_power_off", value);
                }
                if (itemID.equalsIgnoreCase("g_misc__2nd_channel_enable")) {
                    this.mContext.sendBroadcast(new Intent(TurnkeyUiMainActivity.LIVE_SETTING_UPDATE_CONFLICT_SOURCELIST));
                }
                if (!itemID.equalsIgnoreCase("g_video__clr_gain_r") && !itemID.equalsIgnoreCase("g_video__clr_gain_g") && !itemID.equalsIgnoreCase("g_video__clr_gain_b")) {
                    if (itemID.startsWith(PARENTAL_CHANNEL_BLOCK_CHANNELLIST)) {
                        String channelNumber = itemID.split(":")[1];
                        MtkLog.d(TAG, "itemID =" + itemID + "ChannelNumber = " + channelNumber + "Value = " + value);
                        String[] realIdAndValue = SettingsUtil.getRealIdAndValue(itemID);
                        boolean block = EditChannel.getInstance(this.mContext).isChannelBlock(Integer.valueOf(channelNumber).intValue());
                        if (value == 1 && !block) {
                            EditChannel.getInstance(this.mContext).blockChannel(Integer.valueOf(channelNumber).intValue(), true);
                        } else if (value == 0 && block) {
                            EditChannel.getInstance(this.mContext).blockChannel(Integer.valueOf(channelNumber).intValue(), false);
                        } else if (value == 0 && !block) {
                            EditChannel.getInstance(this.mContext).blockChannel(Integer.valueOf(channelNumber).intValue(), false);
                        } else if (value == 1 && block) {
                            EditChannel.getInstance(this.mContext).blockChannel(Integer.valueOf(channelNumber).intValue(), true);
                        }
                    }
                    if (itemID != null && itemID.equals("SETUP_auto_syn")) {
                        this.save.saveValue("SETUP_auto_syn", value);
                    } else if (itemID != null && itemID.equals(SELECT_MODE)) {
                        if (value == 0) {
                            EditChannel.getInstance(this.mContext).disablePowerOnChannel();
                        }
                        this.save.saveValue(SELECT_MODE, value);
                    } else if (itemID.equals("g_audio__aud_mts")) {
                        this.mTV.setConfigValue(itemID, SundryImplement.getInstanceNavSundryImplement(this.mContext).getMtsByModeString(SundryImplement.getInstanceNavSundryImplement(this.mContext).getAllMtsModes()[value]));
                    } else if (itemID.equals("g_audio__dolby_cmpss")) {
                        switch (value) {
                            case 0:
                                value5 = 0;
                                break;
                            case 1:
                                value5 = 3;
                                break;
                            default:
                                value5 = 0;
                                if (CommonIntegration.isEURegion()) {
                                    value5 = 3;
                                    break;
                                }
                                break;
                        }
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, value5);
                        }
                    } else if (itemID.equals("g_country__country_rid")) {
                        MtkLog.d(TAG, "set COUNTRY_REGION_ID>>>" + value);
                        if (!MarketRegionInfo.isFunctionSupport(3) || !this.mTV.isAusCountry()) {
                            switch (value) {
                                case 0:
                                    value4 = 1;
                                    break;
                                case 1:
                                    value4 = 2;
                                    break;
                                default:
                                    value4 = 1;
                                    break;
                            }
                        } else {
                            value4 = value + 2;
                        }
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, value4);
                        }
                    } else if (itemID.equals("g_audio__spdif_delay")) {
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, value / 10);
                        }
                    } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU)) {
                        if (MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("FRA")) {
                            value++;
                        }
                        if (value < 1 || value > 16) {
                            value3 = 0;
                        } else {
                            value3 = value + 2;
                        }
                        if (MarketRegionInfo.isFunctionSupport(21)) {
                            this.mTV.genereateDVBTIFRatingPlus(value3);
                        } else {
                            this.mTV.getDVBRating().setDVBAgeRatingSetting(value3);
                        }
                    } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_SGP)) {
                        if (value >= 0 && value <= 6) {
                            this.mTV.genereateSingaporeTIFRating(value);
                        }
                    } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_THL)) {
                        if (value >= 0 && value <= 7) {
                            this.mTV.genereateThailandTIFRating(value);
                        }
                    } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_ZAF)) {
                        if (value >= 0 && value <= 6) {
                            this.mTV.genereateZafTIFRating(value);
                        }
                    } else if (itemID.equals(PARENTAL_AGE_RATINGS_EU_OCEANIA_AUS)) {
                        switch (value) {
                            case 1:
                                value = 18;
                                break;
                            case 2:
                                value = 17;
                                break;
                            case 3:
                                value = 15;
                                break;
                            case 4:
                                value = 13;
                                break;
                            case 5:
                                value = 11;
                                break;
                            case 6:
                                value = 9;
                                break;
                            case 7:
                                value = 1;
                                break;
                        }
                        this.mTV.getDVBRating().setDVBAgeRatingSetting(value);
                    } else if (itemID.equals("g_video__screen_mode")) {
                        int stored_value = mScreenMode.get(item.mOptionValue[value]).intValue();
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, stored_value);
                        }
                    } else if (itemID.equalsIgnoreCase("g_audio__aud_latency")) {
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, value);
                        }
                    } else if (itemID.equalsIgnoreCase("g_audio__spdif")) {
                        if (value == 1 || value == 3) {
                            Settings.Global.putInt(this.mContext.getContentResolver(), "nrdp_external_surround_sound_enabled", 1);
                        } else {
                            Settings.Global.putInt(this.mContext.getContentResolver(), "nrdp_external_surround_sound_enabled", 0);
                        }
                        if (value >= 2) {
                            value++;
                        }
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, value);
                        }
                    } else if (!itemID.equalsIgnoreCase("g_audio__dolby_dmix")) {
                        if (itemID.equalsIgnoreCase("g_video__vid_blue_mute")) {
                            SaveValue.getInstance(this.mContext);
                            SaveValue.saveWorldValue(this.mContext, "g_video__vid_blue_mute", value, true);
                        }
                        if (itemID.equals("g_disp__disp_gamma")) {
                            int value6 = value + 1;
                            if (this.mTV != null) {
                                this.mTV.setConfigValue(itemID, value6);
                            }
                        } else if (itemID.equals("g_video__vid_vga_mode")) {
                            int value7 = value + 1;
                            if (this.mTV != null) {
                                this.mTV.setConfigValue(itemID, value7);
                            }
                        } else if (itemID.equals("g_video__vid_3d_nav_auto")) {
                            if (value == 2) {
                                this.mTV.setConfigValue("g_video__vid_3d_mode", 1);
                            } else {
                                this.mTV.setConfigValue("g_video__vid_3d_mode", 0);
                            }
                            this.mTV.setConfigValue(itemID, value);
                        } else {
                            if (itemID.equalsIgnoreCase("g_video__vid_3d_mode")) {
                                if (this.mTV.getConfigValue("g_video__vid_3d_nav_auto") == 1) {
                                    if (value == 0) {
                                        value = 0;
                                    } else {
                                        value = 2;
                                    }
                                } else if (this.mTV.getConfigValue("g_video__vid_3d_nav_auto") == 0) {
                                    if (value == 0) {
                                        value = 0;
                                    } else if (value == 1) {
                                        value = 2;
                                    } else if (value == 2) {
                                        value = 4;
                                    } else if (value == 3) {
                                        value = 5;
                                    } else if (value == 4) {
                                        value = 6;
                                    }
                                }
                                this.mTV.setConfigValue(itemID, value);
                            }
                            if (itemID.contains(PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE)) {
                                String channelId = itemID.substring(PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE.length());
                                SettingsPreferenceScreen.block = value;
                                SaveValue saveValue = this.save;
                                saveValue.saveValue(PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE + channelId, value);
                                setSchBlock(Integer.valueOf(channelId).intValue(), value);
                            } else if (itemID.equals(PARENTAL_RATINGS_ENABLE)) {
                                if (value == 0) {
                                    this.mTV.setRatingEnable(false);
                                } else {
                                    this.mTV.setRatingEnable(true);
                                }
                            } else if (itemID.equals(PARENTAL_AGE_RATINGS)) {
                                if (MarketRegionInfo.isFunctionSupport(31)) {
                                    this.mTV.setSATIFAgeRating(value);
                                } else {
                                    this.mTV.getIsdbRating().setISDBAgeRatingSetting(value);
                                }
                            } else if (itemID.equals(PARENTAL_CONTENT_RATINGS)) {
                                if (MarketRegionInfo.isFunctionSupport(31)) {
                                    MtkLog.d(TAG, "setValue F_TIF_SA_RATING = YESValue = " + value);
                                    this.mTV.setSATIFContentRating(value);
                                    return;
                                }
                                if (value == 3) {
                                    value = 4;
                                } else if (value == 4) {
                                    value = 3;
                                }
                                MtkLog.d(TAG, "setValue F_TIF_SA_RATING = NOValue = " + value);
                                this.mTV.getIsdbRating().setISDBContentRatingSetting(value);
                            } else if (itemID.equals(PARENTAL_BLOCK_UNRATED)) {
                                if (value == 0) {
                                    this.mTV.setBlockUnrated(false);
                                } else {
                                    this.mTV.setBlockUnrated(true);
                                }
                            } else if (itemID.equals("g_gui_lang__gui_language")) {
                                if (this.att != null && this.att.getStatus() == AsyncTask.Status.RUNNING) {
                                    this.att.cancel(true);
                                    this.att = null;
                                }
                                this.att = new AsyncOSDLan();
                                this.att.execute(new String[]{String.valueOf(value)});
                            } else if (itemID.equals("g_aud_lang__aud_language") || itemID.equals("g_aud_lang__aud_2nd_language")) {
                                this.osdLanguage = new LanguageUtil(this.mContext.getApplicationContext());
                                MtkLog.d(TAG, "osdLanguage.setAudioLanguage  = " + itemID + " Value = " + value);
                                try {
                                    this.osdLanguage.setAudioLanguage(itemID, value);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (itemID.equalsIgnoreCase(SETUP_TIME_ZONE)) {
                                this.save.saveValue(itemID, value);
                                AlarmManager alarm = (AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
                                GetTimeZone getZone = GetTimeZone.getInstance(this.mContext);
                                String[] timezones = getZone.generateTimeZonesArray();
                                if (timezones[value].equals("As Broadcast")) {
                                    SaveValue.getInstance(this.mContext).saveBooleanValue("Zone_time", true);
                                } else {
                                    alarm.setTimeZone(getZone.getTimeZoneOlsonID(value - 1));
                                    SaveValue.getInstance(this.mContext).saveBooleanValue("Zone_time", false);
                                }
                                MtkLog.d(TAG, "timezone -value:" + value);
                                getZone.setTimeZone(value, timezones[value]);
                            } else {
                                if (itemID.equals("g_time__time_zone")) {
                                    this.osdLanguage = new LanguageUtil(this.mContext.getApplicationContext());
                                    try {
                                        this.osdLanguage.setTimeZone(value);
                                        if (value == 0) {
                                            this.mTV.setConfigValue(itemID, value);
                                            return;
                                        } else {
                                            this.mTV.setConfigValue(itemID, 8 - value);
                                            return;
                                        }
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                                if (itemID.equals("g_timer__timer_on")) {
                                    MtkLog.d(TAG, "[" + itemID + "] POWER_ON_TIMER value: " + value);
                                    if (value == 0) {
                                        this.mTV.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 0, this.save.readStrValue(TIMER1));
                                        this.mTV.updatePowerOn("g_timer__timer_on", 0, this.save.readStrValue(TIMER1));
                                    } else if (value == 1) {
                                        this.mTV.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 0, this.save.readStrValue(TIMER1));
                                        this.mTV.updatePowerOn("g_timer__timer_on", 1, this.save.readStrValue(TIMER1));
                                    } else {
                                        this.mTV.updatePowerOn("g_timer__timer_on", 1, this.save.readStrValue(TIMER1));
                                        this.mTV.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 1, this.save.readStrValue(TIMER1));
                                    }
                                    this.save.saveValue(itemID, value);
                                } else if (itemID.equals("g_timer__timer_off")) {
                                    if (value == 0) {
                                        this.mTV.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 0, this.save.readStrValue(TIMER2));
                                        this.mTV.updatePowerOff("g_timer__timer_off", 0, this.save.readStrValue(TIMER2));
                                    } else if (value == 1) {
                                        this.mTV.updatePowerOff("g_timer__timer_off", 1, this.save.readStrValue(TIMER2));
                                        this.mTV.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 0, this.save.readStrValue(TIMER2));
                                    } else {
                                        this.mTV.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 1, this.save.readStrValue(TIMER2));
                                        this.mTV.updatePowerOff("g_timer__timer_off", 1, this.save.readStrValue(TIMER2));
                                    }
                                    this.save.saveValue(itemID, value);
                                } else if (itemID.equalsIgnoreCase("g_rating__bl_type")) {
                                    this.mTV.setTimeInterval(value);
                                } else {
                                    if (itemID.equals("g_cc__dcs")) {
                                        MtkLog.d(TAG, "captionStyle, " + value);
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(0, value);
                                    } else if (itemID.equals("g_cc__dis_op_ft_size")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(1, value);
                                    } else if (itemID.equals("g_cc__dis_op_ft_style")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(2, value);
                                    } else if (itemID.equals("g_cc__dis_op_ft_color")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(3, value);
                                    } else if (itemID.equals("g_cc__dis_op_ft_opacity")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(4, value);
                                    } else if (itemID.equals("g_cc__dis_op_bk_color")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(5, value);
                                    } else if (itemID.equals("g_cc__dis_op_bk_opacity")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(6, value);
                                    } else if (itemID.equals("g_cc__dis_op_win_color")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(7, value);
                                    } else if (itemID.equals("g_cc__dis_op_win_opacity")) {
                                        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(8, value);
                                    }
                                    if (itemID.equals("g_misc__auto_sleep")) {
                                        if (!CommonIntegration.isEURegion()) {
                                            switch (value) {
                                                case 0:
                                                    auto_sleep = 0;
                                                    break;
                                                case 1:
                                                    auto_sleep = MtkTvTimeFormatBase.SECONDS_PER_HOUR;
                                                    break;
                                                case 2:
                                                    auto_sleep = 7200;
                                                    break;
                                                case 3:
                                                    auto_sleep = 18000;
                                                    break;
                                                default:
                                                    auto_sleep = 0;
                                                    break;
                                            }
                                        } else {
                                            switch (value) {
                                                case 0:
                                                    auto_sleep = 0;
                                                    break;
                                                case 1:
                                                    auto_sleep = 14400;
                                                    break;
                                                case 2:
                                                    auto_sleep = 21600;
                                                    break;
                                                case 3:
                                                    auto_sleep = 28800;
                                                    break;
                                                default:
                                                    auto_sleep = 14400;
                                                    break;
                                            }
                                        }
                                        this.mTV.setConfigValue(itemID, auto_sleep);
                                    } else if (!itemID.equals("g_nav__air_on_time_ch") && !itemID.equals("g_nav__cable_on_time_ch")) {
                                        if (itemID.equals("g_video__vid_shp")) {
                                            int min = getMin(itemID);
                                            int max = getMax(itemID);
                                            if (value >= min && value <= max) {
                                                MtkLog.v(TAG, "Normal Case.--------------");
                                                if (this.mTV != null) {
                                                    this.mTV.setConfigValue(itemID, value);
                                                }
                                            } else if (value < min) {
                                                MtkLog.v(TAG, "Minimum Case.--------------");
                                                if (this.mTV != null) {
                                                    this.mTV.setConfigValue(itemID, max);
                                                }
                                            } else {
                                                MtkLog.v(TAG, "Maximum Case.--------------");
                                                if (this.mTV != null) {
                                                    this.mTV.setConfigValue(itemID, min);
                                                }
                                            }
                                        } else if (itemID.equals("g_bs__bs_src")) {
                                            setTunerMode(value, true);
                                        } else if ("g_subtitle__subtitle_lang_2nd".equals(itemID) || "g_subtitle__subtitle_lang".equals(itemID)) {
                                            this.osdLanguage = new LanguageUtil(this.mContext.getApplicationContext());
                                            try {
                                                this.osdLanguage.setSubtitleLanguage(itemID, value);
                                            } catch (Exception e3) {
                                                e3.printStackTrace();
                                            }
                                        } else {
                                            if (itemID.startsWith(CFG_MENU_AUDIOINFO_GET_STRING)) {
                                                String[] spls = itemID.split("_");
                                                if (spls == null || spls.length != 2) {
                                                    MtkLog.e(TAG, "mID is not correct:" + itemID);
                                                } else {
                                                    int index = Integer.parseInt(spls[1]);
                                                    MtkLog.d(TAG, "set CFG_MENU_AUDIOINFO_GET_STRING" + index);
                                                    this.mTV.setConfigValue("g_menu__audioinfoselect", index);
                                                }
                                            }
                                            if (itemID.startsWith(SOUNDTRACKS_GET_STRING)) {
                                                String trackId = "";
                                                String[] spls2 = itemID.split("_");
                                                if (spls2 != null && spls2.length == 2) {
                                                    trackId = spls2[1];
                                                }
                                                if (!MarketRegionInfo.isFunctionSupport(20)) {
                                                    MtkTvAVMode.getInstance().selectAudioById(trackId);
                                                } else if ("main" == CommonIntegration.getInstance().getCurrentFocus()) {
                                                    TurnkeyUiMainActivity.getInstance().getTvView().selectTrack(0, trackId);
                                                } else {
                                                    TurnkeyUiMainActivity.getInstance().getPipView().selectTrack(0, trackId);
                                                }
                                            }
                                            if (value < getMin(itemID) || value > getMax(itemID)) {
                                                MtkLog.d(TAG, "[" + itemID + "] set value: " + value + "   Min value: " + getMin(itemID) + "   Max value" + getMax(itemID));
                                                if (this.mTV != null) {
                                                    this.mTV.setConfigValue(itemID, value);
                                                }
                                            } else if (this.mTV != null) {
                                                this.mTV.setConfigValue(itemID, value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (!CommonIntegration.isEURegion()) {
                        if (value == 0) {
                            value2 = 2;
                        } else if (value == 1) {
                            value2 = 11;
                        } else if (value == 2) {
                            value2 = 1;
                        } else {
                            value2 = 0;
                        }
                        if (this.mTV != null) {
                            this.mTV.setConfigValue(itemID, value2);
                        }
                    } else if (this.mTV != null) {
                        this.mTV.setConfigValue(itemID, value);
                    }
                } else if (value != getDefault(itemID)) {
                    this.mTV.setConfigValue("g_video__clr_temp", 0);
                    this.mTV.setConfigValue(itemID, value);
                }
            }
        }
    }

    public void setTunerModeFromSvlId(int svlId) {
        int value = CommonIntegration.getInstance().getTunerModeFromSvlId(svlId);
        MtkLog.d(TAG, "setTunerModeFromSvlId value=" + value);
        setTunerMode(value, false);
    }

    private void setTunerMode(int value, boolean needSelectChannel) {
        ChannelListDialog channelListDialog;
        String itemID = "g_bs__bs_src";
        if (this.mTV != null) {
            int networkIndex = 0;
            if (needSelectChannel && (channelListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST)) != null) {
                networkIndex = channelListDialog.getChannelNetworkIndex();
            }
            if (CommonIntegration.getInstance().isDualTunerEnable() && CommonIntegration.getInstance().getCurrentFocus().equals("sub")) {
                itemID = "g_misc__2nd_tuner_type";
            }
            MtkLog.v(TAG, "set TUNER_MODE," + value);
            MtkLog.v(TAG, "isBootFromLiveTV ," + LiveTvSetting.isBootFromLiveTV() + "," + networkIndex);
            if (CommonIntegration.isEURegion()) {
                boolean notOperator = ScanContent.getDVBSOperatorList(this.mContext).size() <= 0;
                if (!needSelectChannel || !LiveTvSetting.isBootFromLiveTV()) {
                    if (value == 2 && notOperator) {
                        value = 3;
                    }
                    this.mTV.setConfigValue(itemID, value, true);
                } else if (CommonIntegration.getInstance().is3rdTVSource()) {
                    if (value == 2 && notOperator) {
                        value = 3;
                    }
                    this.mTV.setConfigValue(itemID, value, true);
                    SaveValue instance = SaveValue.getInstance(this.mContext);
                    instance.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), networkIndex);
                } else {
                    if (value == 2 && notOperator) {
                        value = 3;
                    }
                    this.mTV.setConfigValue(itemID, value);
                    SaveValue instance2 = SaveValue.getInstance(this.mContext);
                    instance2.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
                }
                TvSingletons.getSingletons().getCommonIntegration();
                if (CommonIntegration.isEUPARegion()) {
                    if (!needSelectChannel || !LiveTvSetting.isBootFromLiveTV()) {
                        this.mTV.setConfigValue("g_bs__bs_user_src", value, true);
                    } else if (CommonIntegration.getInstance().is3rdTVSource()) {
                        this.mTV.setConfigValue("g_bs__bs_user_src", value, true);
                        SaveValue instance3 = SaveValue.getInstance(this.mContext);
                        instance3.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), networkIndex);
                    } else {
                        this.mTV.setConfigValue("g_bs__bs_user_src", value);
                        SaveValue instance4 = SaveValue.getInstance(this.mContext);
                        instance4.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
                    }
                }
                int currentOp = ScanContent.getDVBSCurrentOP();
                int preferSat = this.mTV.getConfigValue("g_two_sat_chlist__preferred_sat");
                MtkLog.v(TAG, "set TUNER_MODE-EU111," + this.mTV.getConfigValue(itemID) + ">>>" + preferSat + ">>>" + currentOp + ">>>" + this.mTV.getConfigValue("g_bs__bs_svl_id"));
                if (currentOp == 0 && preferSat != 0) {
                    ScanContent.setSelectedSatelliteOPFromMenu(this.mContext, 0);
                    return;
                }
                return;
            }
            TvSingletons.getSingletons().getCommonIntegration();
            if (CommonIntegration.isCNRegion()) {
                MtkLog.v(TAG, "set TUNER_MODE_USER_SET," + value);
                if (!needSelectChannel || !LiveTvSetting.isBootFromLiveTV()) {
                    this.mTV.setConfigValue("g_bs__bs_user_src", value, true);
                } else if (CommonIntegration.getInstance().is3rdTVSource()) {
                    this.mTV.setConfigValue("g_bs__bs_user_src", value, true);
                    SaveValue instance5 = SaveValue.getInstance(this.mContext);
                    instance5.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), networkIndex);
                } else {
                    this.mTV.setConfigValue("g_bs__bs_user_src", value);
                    SaveValue instance6 = SaveValue.getInstance(this.mContext);
                    instance6.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
                }
            } else if (!needSelectChannel || !LiveTvSetting.isBootFromLiveTV()) {
                this.mTV.setConfigValue(itemID, value, true);
            } else if (CommonIntegration.getInstance().is3rdTVSource()) {
                this.mTV.setConfigValue(itemID, value, true);
                SaveValue instance7 = SaveValue.getInstance(this.mContext);
                instance7.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), networkIndex);
            } else {
                this.mTV.setConfigValue(itemID, value);
                SaveValue instance8 = SaveValue.getInstance(this.mContext);
                instance8.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
            }
        }
    }

    public void setActionValue(Action action) {
        setValue(action.mItemID, action.mInitValue, action);
    }

    private long getBlockFromTime(int channelID) {
        int i = channelID;
        SaveValue instance = SaveValue.getInstance(this.mContext);
        String dateString = instance.readStrValue(TIME_START_DATE + i);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7));
        int monthDay = Integer.parseInt(dateString.substring(8));
        SaveValue instance2 = SaveValue.getInstance(this.mContext);
        String timeString = instance2.readStrValue(TIME_START_TIME + i);
        int hour = Integer.parseInt(timeString.substring(0, 2));
        int minute = Integer.parseInt(timeString.substring(3, 5));
        if (year == 1970 && ((month == 0 || month == 1) && monthDay == 1)) {
            year = 2014;
            month = 2;
            monthDay = 27;
        }
        MtkLog.d("EidtChannel setSchBlockFromUTCTime", "year:" + year + "month:" + month + "monthDay:" + monthDay + "hour:" + hour + "minute:" + minute + "second:" + 0);
        MtkTvTimeFormat.getInstance().set(0, minute, hour, monthDay, month + -1, year);
        return MtkTvTimeFormat.getInstance().toMillis();
    }

    private long getBlockEndTime(int channelID) {
        int i = channelID;
        SaveValue instance = SaveValue.getInstance(this.mContext);
        String dateString = instance.readStrValue(TIME_END_DATE + i);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7));
        int monthDay = Integer.parseInt(dateString.substring(8));
        SaveValue instance2 = SaveValue.getInstance(this.mContext);
        String timeString = instance2.readStrValue(TIME_END_TIME + i);
        int hour = Integer.parseInt(timeString.substring(0, 2));
        int minute = Integer.parseInt(timeString.substring(3, 5));
        if (year == 1970 && ((month == 0 || month == 1) && monthDay == 1)) {
            year = 2014;
            month = 2;
            monthDay = 27;
        }
        MtkLog.d("EidtChannel setSchBlockTOUTCTime", "year:" + year + "month:" + month + "monthDay:" + monthDay + "hour:" + hour + "minute:" + minute + "second:" + 0);
        MtkTvTimeFormat.getInstance().set(0, minute, hour, monthDay, month + -1, year);
        return MtkTvTimeFormat.getInstance().toMillis();
    }

    private void setSchBlock(int chid, int value) {
        EditChannel.getInstance(this.mContext).setSchBlock(chid, getBlockFromTime(chid), getBlockEndTime(chid), value);
    }

    public void setScanValue(String itemID, int value) {
        if (TV_SINGLE_SCAN_MODULATION.equals(itemID) || FREQUENEY_PLAN.equals(itemID) || US_SCAN_MODE.equals(itemID) || DVBC_SINGLE_RF_SCAN_MODULATION.equals(itemID)) {
            this.save.saveValue(itemID, value);
        }
    }

    public void toastWearGlass() {
        Toast toast = new Toast(this.mContext);
        toast.setGravity(17, 6, 20);
        toast.setDuration(0);
        TextView view = new TextView(this.mContext);
        view.setBackgroundColor(R.color.pin_dialog_background);
        view.setGravity(17);
        view.setText(this.mContext.getString(R.string.menu_video_3d_wear_glass));
        toast.setView(view);
        toast.show();
    }

    private TvInputManager getTvInputManager(Context context) {
        if (this.mTvInputManager == null) {
            this.mTvInputManager = (TvInputManager) context.getSystemService("tv_input");
        }
        return this.mTvInputManager;
    }

    private ContentRatingsManager getContentRatingsManager(Context context) {
        if (this.mContentRatingsManager == null) {
            this.mContentRatingsManager = new ContentRatingsManager(context);
        }
        return this.mContentRatingsManager;
    }

    private ParentalControlSettings getParentalControlSettings(Context context) {
        if (this.mParentalControlSettings == null) {
            this.mParentalControlSettings = new ParentalControlSettings(context);
        }
        this.mParentalControlSettings.loadRatings();
        return this.mParentalControlSettings;
    }

    public List<ContentRatingSystem> loadContentRatingsSystems() {
        this.mRatingSystem = getTvInputManager(this.mContext).getTvContentRatingSystemList();
        if (this.mRatingSystem == null || this.mRatingSystem.size() == 0) {
            return null;
        }
        this.parser = new ContentRatingsParser(this.mContext);
        this.mContentRatingSystems.clear();
        MtkLog.d(TAG, "[Ratings] mRatingSystem size:" + this.mRatingSystem.size());
        for (TvContentRatingSystemInfo info : this.mRatingSystem) {
            List<ContentRatingSystem> list = this.parser.parse(info);
            if (list != null) {
                this.mContentRatingSystems.addAll(list);
                Log.d(TAG, "[Ratings] list:" + list.size());
            }
        }
        return this.mContentRatingSystems;
    }

    private void setContentRatingValue(String id, int value) {
        MtkLog.d(TAG, "[setContentRatingValue] id:" + id + ",value = " + value);
        if (id.startsWith(PARENTAL_TIF_CONTENT_RATGINS_SYSTEM)) {
            for (ContentRatingSystem info : this.mContentRatingSystems) {
                MtkLog.d(TAG, "[setContentRatingValue] m info:" + info.getId());
                if (id.startsWith("parental_tif_content_ratings_system|" + info.getId())) {
                    MtkLog.d(TAG, "[setContentRatingValue] id:" + info);
                    getParentalControlSettings(this.mContext).setContentRatingSystemEnabled(getContentRatingsManager(this.mContext), info, value == 1);
                }
            }
        } else if (id.startsWith(PARENTAL_TIF_RATGINS_SYSTEM_CONTENT)) {
            for (ContentRatingSystem info2 : this.mContentRatingSystems) {
                MtkLog.d(TAG, "[setContentRatingValue] c info:" + info2.getId());
                if (id.startsWith("parental_tif_ratings_system_cnt|" + info2.getId())) {
                    for (int i = 0; i < info2.getRatings().size(); i++) {
                        MtkLog.d(TAG, "[setContentRatingValue] getTitle:" + info2.getRatings().get(i).getTitle());
                        if (id.endsWith(info2.getRatings().get(i).getTitle())) {
                            MtkLog.d(TAG, "[setContentRatingValue] i:" + i);
                            getParentalControlSettings(this.mContext).setRatingBlocked(info2, info2.getRatings().get(i), value == 1);
                        }
                    }
                }
            }
        }
    }

    private int getContentRatingValue(String id) {
        MtkLog.d(TAG, "[getContentRatingValue] id:" + id);
        if (id.startsWith(PARENTAL_TIF_CONTENT_RATGINS_SYSTEM)) {
            for (ContentRatingSystem info : this.mContentRatingSystems) {
                MtkLog.d(TAG, "[getContentRatingValue] m info:" + info);
                if (id.startsWith("parental_tif_content_ratings_system|" + info.getId())) {
                    MtkLog.d(TAG, "[getContentRatingValue] id:" + info);
                    return getParentalControlSettings(this.mContext).isContentRatingSystemEnabled(info) ? 1 : 0;
                }
            }
        } else if (id.startsWith(PARENTAL_TIF_RATGINS_SYSTEM_CONTENT)) {
            for (ContentRatingSystem info2 : this.mContentRatingSystems) {
                MtkLog.d(TAG, "[getContentRatingValue] c info:" + info2);
                if (id.startsWith("parental_tif_ratings_system_cnt|" + info2.getId())) {
                    int i = 0;
                    while (i < info2.getRatings().size()) {
                        MtkLog.d(TAG, "[getContentRatingValue] getTitle:" + info2.getRatings().get(i).getTitle());
                        if (!id.endsWith(info2.getRatings().get(i).getTitle())) {
                            i++;
                        } else {
                            MtkLog.d(TAG, "[getContentRatingValue] id:" + i);
                            if (getParentalControlSettings(this.mContext).isRatingBlocked(info2, info2.getRatings().get(i))) {
                                return 1;
                            }
                            return 0;
                        }
                    }
                    continue;
                }
            }
        }
        MtkLog.d(TAG, "[getContentRatingValue] failed.");
        return 0;
    }

    public boolean isConfigEnabled(String cfgId) {
        return this.mTvConfig.isConfigEnabled(cfgId) == 0;
    }

    public boolean isConfigVisible(String cfgid) {
        return this.mTvConfig.isConfigVisible(cfgid) == 0;
    }

    private class AsyncOSDLan extends AsyncTask<String, String, String> {
        AsyncOSDLan() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            int value = Integer.parseInt(arg0[0]);
            MenuConfigManager.this.osdLanguage = new LanguageUtil(MenuConfigManager.this.mContext.getApplicationContext());
            try {
                MenuConfigManager.this.osdLanguage.setLanguage(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return arg0[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public String getDefaultPowerSettingValue(Context context) {
        switch (getPowerOffSettingTime(context)) {
            case 0:
                return context.getResources().getString(R.string.menu_arrays_power_never);
            case 1:
                return context.getResources().getString(R.string.menu_arrays_power_x_hours, new Object[]{4});
            case 2:
                return context.getResources().getString(R.string.menu_arrays_power_x_hours, new Object[]{6});
            case 3:
                return context.getResources().getString(R.string.menu_arrays_power_x_hours, new Object[]{8});
            default:
                return "4 hours";
        }
    }

    public int getDefaultPowerSetting(Context context) {
        int def = getAPDefaultValue(DataSeparaterUtil.getInstance() != null ? DataSeparaterUtil.getInstance().getValueAutoSleep() : -1);
        int old = getIntautoSleepValue(context);
        int powersetting = SaveValue.readWorldIntValue(context, POWER_SETTING_VALUE, -1);
        MtkLog.d(TAG, "powersetting1==" + powersetting);
        if (old == -2 && powersetting == -1) {
            SaveValue.saveWorldValue(context, POWER_SETTING_VALUE, def, true);
            setAutoSleepValue(def + 1);
        } else {
            SaveValue.saveWorldValue(context, POWER_SETTING_VALUE, old, true);
        }
        int powersetting2 = SaveValue.readWorldIntValue(context, POWER_SETTING_VALUE, -1);
        MtkLog.d(TAG, "powersetting=" + powersetting2);
        if (powersetting2 < 0) {
            return 1;
        }
        return powersetting2;
    }

    public int getPowerOffSettingTime(Context context) {
        int leftmin = Integer.valueOf(context.getResources().getString(R.string.menu_string_power_left_minite)).intValue();
        int four = Integer.valueOf(context.getResources().getString(R.string.menu_string_power_four)).intValue();
        int six = Integer.valueOf(context.getResources().getString(R.string.menu_string_power_six)).intValue();
        int eight = Integer.valueOf(context.getResources().getString(R.string.menu_string_power_eight)).intValue();
        switch (getDefaultPowerSetting(context)) {
            case 0:
                return 0;
            case 1:
                return ((four * 60) * 60) - (leftmin * 60);
            case 2:
                return ((six * 60) * 60) - (leftmin * 60);
            case 3:
                return ((eight * 60) * 60) - (leftmin * 60);
            default:
                return 0;
        }
    }

    public int getAPDefaultValue(int value) {
        if (value == 0) {
            return 0;
        }
        if (value == 4) {
            return 1;
        }
        if (value == 6) {
            return 2;
        }
        if (value != 8) {
            return value;
        }
        return 3;
    }

    public void setAutoSleepValue(int a) {
        int value = getLongAuto(a);
        MtkLog.printStackTrace();
        if ((DataSeparaterUtil.getInstance() != null ? DataSeparaterUtil.getInstance().getValueAutoSleep() : -1) == 2) {
            value = getLongAuto(a + 100);
        }
        MtkLog.d(TAG, "autosleep value ==" + a + ",actua ==" + value);
        Settings.Secure.putInt(TurnkeyUiMainActivity.getInstance().getContentResolver(), "attentive_timeout", value);
    }

    private int getLongAuto(int a) {
        switch (a) {
            case 1:
                return -1;
            case 2:
                return FOUR_SLEEP_TIME_MS;
            case 3:
                return SIX_SLEEP_TIME_MS;
            case 4:
                return EIGHT_SLEEP_TIME_MS;
            default:
                switch (a) {
                    case 102:
                        return 35000;
                    case 103:
                        return 60000;
                    case 104:
                        return 600000;
                    default:
                        return -1;
                }
        }
    }

    private int getIntautoSleepValue(Context context) {
        switch (Settings.Secure.getInt(context.getContentResolver(), "attentive_timeout", -2)) {
            case -2:
                return -2;
            case -1:
                return 0;
            case FOUR_SLEEP_TIME_MS /*14100000*/:
                return 1;
            case SIX_SLEEP_TIME_MS /*21300000*/:
                return 2;
            case EIGHT_SLEEP_TIME_MS /*28500000*/:
                return 3;
            default:
                return 1;
        }
    }
}
