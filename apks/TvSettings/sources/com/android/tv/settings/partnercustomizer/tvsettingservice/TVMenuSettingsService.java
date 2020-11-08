package com.android.tv.settings.partnercustomizer.tvsettingservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.net.MtkNetworkManager;
import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.HashMap;
import java.util.Map;

public class TVMenuSettingsService extends Service {
    private static final int COLOR_TEMP_SET_COMPLETE = 13;
    private static final int PIC_RESET_TO_DEFUALT_COMPLETE = 12;
    private static final int PIC_UPDATE_COMPLETE = 14;
    private final Uri BASE_URI = Settings.Global.CONTENT_URI;
    private final int HANDLER_DOVI_NOTIFY = 1;
    private final int HANDLER_NOTIFY_THREEDIM_ITEM_STATUS = 2;
    private final int HANDLER_RESET_AUDIO = 4;
    private final int HANDLER_RESET_VIDEO = 5;
    private final int HANDLER_SOURCE_CHANGE = 3;
    private final int HANDLER_SYNC_DATA_COMPLETE = 6;
    private final String TAG = "tvmenusettingsservice";
    Map<String, Integer> arrayMap = new HashMap();
    private Map<String, String> colorTempMap = new HashMap();
    private boolean isDolbyVision;
    /* access modifiers changed from: private */
    public boolean isPictureRefresh = false;
    /* access modifiers changed from: private */
    public Map<String, String> itemIds = new HashMap();
    /* access modifiers changed from: private */
    public ContentResolver mContentResolver;
    private Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtkLog.d("tvmenusettingsservice", "handleMessage : msg = " + msg.what + "   arg = " + msg.arg1);
            int i = msg.what;
            switch (i) {
                case 1:
                    boolean unused = TVMenuSettingsService.this.isPictureRefresh = true;
                    if (msg.arg1 == 5 || msg.arg1 == 6) {
                        TVMenuSettingsService.this.setDolbyVision(true);
                        return;
                    } else {
                        TVMenuSettingsService.this.setDolbyVision(false);
                        return;
                    }
                case 2:
                    Intent intent = new Intent(TVSettingConfig.ACTION_NOTIFY_THREEDIM_ITEM_STATUS);
                    intent.putExtra("is_update_3dmode_ui", msg.arg1);
                    TVMenuSettingsService.this.sendBroadcast(intent);
                    return;
                case 3:
                    MtkLog.d("tvmenusettingsservice", "HANDLER_SOURCE_CHANGE: msg.arg1 = " + msg.arg1);
                    TVMenuSettingsService.this.updateSettingDB(TVMenuSettingsService.this.itemIds);
                    return;
                case 4:
                    MtkLog.d("tvmenusettingsservice", "HANDLER_RESET_AUDIO: msg.arg1 = " + msg.arg1);
                    TVMenuSettingsService.this.updateSettingDB(TVMenuSettingsService.this.itemIds);
                    return;
                case 5:
                    MtkLog.d("tvmenusettingsservice", "HANDLER_RESET_VIDEO: msg.arg1 = " + msg.arg1);
                    TVMenuSettingsService.this.updateSettingDB(TVMenuSettingsService.this.itemIds);
                    return;
                case 6:
                    if (TVMenuSettingsService.this.isPictureRefresh) {
                        Intent intent2 = new Intent(TVSettingConfig.ACTION_NOTIFY_DOLBY_VISION);
                        intent2.putExtra("DOLBY", TVMenuSettingsService.this.isDolbyVision());
                        TVMenuSettingsService.this.sendBroadcast(intent2);
                        boolean unused2 = TVMenuSettingsService.this.isPictureRefresh = false;
                        return;
                    }
                    return;
                default:
                    switch (i) {
                        case 12:
                            boolean unused3 = TVMenuSettingsService.this.isPictureRefresh = true;
                            TVMenuSettingsService.this.updateSettingDB(TVMenuSettingsService.this.pictureModeMap);
                            return;
                        case 14:
                            boolean unused4 = TVMenuSettingsService.this.isPictureRefresh = true;
                            TVMenuSettingsService.this.updateSettingDB(TVMenuSettingsService.this.pictureModeMap);
                            return;
                        default:
                            return;
                    }
            }
        }
    };
    /* access modifiers changed from: private */
    public TVSettingConfig mSettingConfig;
    /* access modifiers changed from: private */
    public SettingObserver mSettingObserver;
    private String marketregion;
    private MtkTvAppTV mtkTvAppTV;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("yajun", "mreciver intent = " + intent.getAction());
            if (TVSettingConfig.ACTION_DOVI_RESET.equals(intent.getAction())) {
                TVMenuSettingsService.this.mSettingConfig.setConifg("g_video__dovi_reset_pic_setting", 0);
                TVMenuSettingsService.this.updateSettingDB(TVMenuSettingsService.this.pictureModeMap);
            }
        }
    };
    /* access modifiers changed from: private */
    public Map<String, String> pictureModeMap = new HashMap();
    /* access modifiers changed from: private */
    public final String[] prefKeys = {PreferenceConfigUtils.KEY_SOUND_SURROUND, PreferenceConfigUtils.KEY_SOUND_BALANCE, PreferenceConfigUtils.KEY_SOUND_BASS, PreferenceConfigUtils.KEY_SOUND_TREBLE, PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND, PreferenceConfigUtils.KEY_SOUND_EQUALIZER, PreferenceConfigUtils.KEY_SOUND_SPDIF_TYPE, PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY, PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL, PreferenceConfigUtils.KEY_SOUND_TYPE, PreferenceConfigUtils.KEY_SOUND_DOWNMIX_MODE, PreferenceConfigUtils.KEY_SOUND_DTS_DRC, "sound_sonic_emotion_premium", PreferenceConfigUtils.KEY_AUTO_VOLUME_LEVELING, PreferenceConfigUtils.KEY_DIGITAL_OUTPUT_TO_DEVICE, PreferenceConfigUtils.KEY_DIGITAL_OUTPUT, PreferenceConfigUtils.KEY_DELTA_VOLUME, PreferenceConfigUtils.KEY_SOUND_RESET_DEFAULT, PreferenceConfigUtils.KEY_SOUND_STYLE, PreferenceConfigUtils.KEY_SOUND_DTS_VIRTUAL_X, PreferenceConfigUtils.KEY_SOUND_DTS_TBHDX, PreferenceConfigUtils.KEY_SOUND_DTS_LIMITER, PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_120HZ, PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_500HZ, PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_1500HZ, PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_5000HZ, PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_10000HZ, PreferenceConfigUtils.KEY_SONIC_EMOTION, PreferenceConfigUtils.KEY_DIALOG_ENHANCEMENT, PreferenceConfigUtils.KEY_BASS_ENHANCEMENT, PreferenceConfigUtils.KEY_ABSOLUTE_3D_SOUND, PreferenceConfigUtils.KEY_SOUND_DBX_ENABLE, PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_SONIC, PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_VOL, PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_SURROUND, PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_AP, PreferenceConfigUtils.KEY_SOUND_ADVANCED_SOUND_MODE, PreferenceConfigUtils.KEY_SOUND_ADVANCED_VOLUME_LEVELER, PreferenceConfigUtils.KEY_SOUND_ADVANCED_DIALOGUE_ENHANCEER, PreferenceConfigUtils.KEY_SOUND_ADVANCED_SURROUND_VIRTUALIZER, PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_ATMOS, PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_ENABLE, PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_SURROUND, PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_TRUEVOLUME, PreferenceConfigUtils.KEY_PICTURE_MODE, PreferenceConfigUtils.KEY_PICTURE_MODE_DOLBY, PreferenceConfigUtils.KEY_PICTURE_NOTIFICATION, PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT, PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT, PreferenceConfigUtils.KEY_PICTURE_BRIGHTNESS, PreferenceConfigUtils.KEY_PICTURE_CONTRAST, PreferenceConfigUtils.KEY_PICTURE_SATURATION, PreferenceConfigUtils.KEY_PICTURE_HUE, PreferenceConfigUtils.KEY_PICTURE_SHARPNESS, PreferenceConfigUtils.KEY_PICTURE_GAMMA, PreferenceConfigUtils.KEY_PICTURE_BLUELIGHT, PreferenceConfigUtils.KEY_PICTURE_HDR, PreferenceConfigUtils.KEY_PICTURE_RESET_DEFAULT, "picture_color_temperature", PreferenceConfigUtils.KEY_PICTURE_RED_GAIN, PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN, PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DNR, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MPEG_NR, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_LOCAL_CONTRAST_CONTROL, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_FLESH_TONE, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DI_FILM_MODE, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_BLUE_STRETCH, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_GAME_MODE, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION, "network_wow", "network_wol", PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_ENABLE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_RED, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_GREEN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_BLUE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_CVAN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_MAGENTA, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_YELLOW, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_FLESH_TONE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_RED, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_GREEN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_BLUE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_CVAN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_MEGENTA, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_YELLOW, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_FLESH_TONE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_RED, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_GREEN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_BLUE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_CVAN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_MEGENTA, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_YELLOW, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_FLESH_TONE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_RED, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_GREEN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_BLUE, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_RED, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_GREEN, PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_BLUE, PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_ENABLE, PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GAIN, PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED, PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN, PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE, PreferenceConfigUtils.KEY_PICTURE_VGA_HP, PreferenceConfigUtils.KEY_PICTURE_VGA_VP, PreferenceConfigUtils.KEY_PICTURE_VGA_PHASE, PreferenceConfigUtils.KEY_PICTURE_VGA_CLOCK, PreferenceConfigUtils.KEY_TV_HDMI_EDID_VERSION, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_SPEAKER, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_HEADPHONE, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_VOLUME, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_PANE, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_VISUALLY_AUDIO, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_FADER_CONTROL, PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_AC4DE, PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, PreferenceConfigUtils.KEY_PICTURE_FORMAT, PreferenceConfigUtils.KEY_PICTURE_FILM_MODE, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_PC_MODE, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_NOISE_REDUCTION, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_4K_UHD_UPSCALING, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_HDMI_RGB_RANGE, PreferenceConfigUtils.KEY_DEVICE_RETAILDEMO};
    private Map<String, String> soundStyleMap = new HashMap();
    private Map<String, String> whiteBalanceMap = new HashMap();

    public TVMenuSettingsService() {
    }

    public IBinder onBind(Intent i) {
        return null;
    }

    public void onCreate() {
        MtkLog.d("tvmenusettingsservice", "oncreate");
        super.onCreate();
        this.mContext = getApplicationContext();
        this.marketregion = SystemProperties.get("ro.vendor.mtk.system.marketregion");
        this.mContentResolver = getContentResolver();
        this.mSettingObserver = new SettingObserver((Handler) null);
        this.mSettingConfig = TVSettingConfig.getInstance(this.mContext);
        this.mtkTvAppTV = MtkTvAppTV.getInstance();
        new NotifyCallbackHandler();
        registerSettingObserver();
        initMapdata();
        initArrayData();
        initPicModeMap();
        initColorTempMap();
        initWhiteBalnaceMap();
        initSoundStyleMap();
        syncConfigDataToSetting();
        updateNetwork();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TVSettingConfig.ACTION_DOVI_RESET);
        this.mContext.registerReceiver(this.myReceiver, filter);
    }

    public void onDestroy() {
        unregisterSettingObserver();
        clearMapData();
        MtkLog.d("tvmenusettingsservice", "onDestroy");
        super.onDestroy();
    }

    private void registerSettingObserver() {
        if (this.mContentResolver == null) {
            this.mContentResolver = getContentResolver();
        }
        new Thread(new Runnable() {
            public void run() {
                for (String key : TVMenuSettingsService.this.prefKeys) {
                    TVMenuSettingsService.this.mContentResolver.registerContentObserver(TVMenuSettingsService.this.buildUri(key), true, TVMenuSettingsService.this.mSettingObserver);
                }
            }
        }).start();
    }

    private void unregisterSettingObserver() {
        this.mContentResolver.unregisterContentObserver(this.mSettingObserver);
    }

    private void syncConfigDataToSetting() {
        MtkLog.d("tvmenusettingsservice", "syncConfigDataToSetting");
        updateSettingDB(this.itemIds);
    }

    /* access modifiers changed from: private */
    public void updateConfigData(String configItemId, String settingItemId) {
        int settingValueInt;
        MtkLog.d("tvmenusettingsservice", "updateConfigData: configItemId = " + configItemId + " settingItemId = " + settingItemId);
        if (configItemId == null || configItemId.isEmpty()) {
            MtkLog.e("tvmenusettingsservice", "updateConfigData configItemId is null or empty");
            return;
        }
        try {
            int configValueInt = this.mSettingConfig.getConfigValueInt(configItemId);
            String settingValueString = PreferenceUtils.getSettingStringValue(this.mContentResolver, settingItemId);
            if (this.arrayMap.get(settingItemId) == null) {
                settingValueInt = Integer.parseInt(settingValueString);
                if ("g_video__vid_shp".equalsIgnoreCase(configItemId)) {
                    int value = MtkTvConfig.getInstance().getMinMaxConfigValue(configItemId);
                    int min = MtkTvConfig.getMinValue(value);
                    int max = MtkTvConfig.getMaxValue(value);
                    if (settingValueInt < min) {
                        settingValueInt = max;
                    } else if (settingValueInt > max) {
                        settingValueInt = min;
                    }
                } else if ("g_audio__spdif_delay".equalsIgnoreCase(configItemId)) {
                    settingValueInt /= 10;
                }
            } else {
                settingValueInt = formatSettingValueToInt(settingItemId, settingValueString);
            }
            int settingValueInt2 = reorganizeSettingValueIntToConfigValue(configItemId, settingValueInt);
            if (configValueInt != settingValueInt2) {
                if ("g_video__clr_gain_r".equalsIgnoreCase(configItemId) || "g_video__clr_gain_g".equalsIgnoreCase(configItemId) || "g_video__clr_gain_b".equalsIgnoreCase(configItemId)) {
                    int r = this.mSettingConfig.getConfigValueInt("g_video__clr_gain_r");
                    int g = this.mSettingConfig.getConfigValueInt("g_video__clr_gain_g");
                    int b = this.mSettingConfig.getConfigValueInt("g_video__clr_gain_b");
                    if (this.mSettingConfig.getConfigValueInt("g_video__clr_temp") != 0) {
                        this.mSettingConfig.setConifg("g_video__clr_temp", 0);
                        this.mSettingConfig.setConifg("g_video__clr_gain_r", r);
                        this.mSettingConfig.setConifg("g_video__clr_gain_g", g);
                        this.mSettingConfig.setConifg("g_video__clr_gain_b", b);
                    }
                }
                if (configItemId.equalsIgnoreCase("g_fusion_picture__pictrue_format")) {
                    this.mtkTvAppTV.setVideoMute("main", true);
                }
                this.mSettingConfig.setConifg(configItemId, settingValueInt2);
                if (configItemId.equalsIgnoreCase("g_fusion_picture__pictrue_format")) {
                    this.mtkTvAppTV.setVideoMute("main", false);
                }
                if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DNR)) {
                    this.mSettingConfig.setConifg("g_video__vid_3d_nr", settingValueInt2);
                }
                if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_PC_MODE)) {
                    this.mSettingConfig.setConifg("g_fusion_picture__pictrue_format", this.mSettingConfig.getConfigValueInt("g_fusion_picture__pictrue_format"));
                }
            }
        } catch (Exception e) {
        }
    }

    private int reorganizeSettingValueIntToConfigValue(String configItemId, int settingValueInt) {
        int configValue = settingValueInt;
        if ("g_audio__spdif".equalsIgnoreCase(configItemId)) {
            if (settingValueInt == 4 || settingValueInt == 5) {
                PreferenceConfigUtils.putSettingValueInt(this.mContentResolver, "external_surround_sound_enabled", 1);
                PreferenceConfigUtils.putSettingValueInt(this.mContentResolver, "nrdp_external_surround_sound_enabled", 1);
            } else {
                PreferenceConfigUtils.putSettingValueInt(this.mContentResolver, "external_surround_sound_enabled", 0);
                PreferenceConfigUtils.putSettingValueInt(this.mContentResolver, "nrdp_external_surround_sound_enabled", 0);
            }
        }
        "g_fusion_picture__pictrue_format".equalsIgnoreCase(configItemId);
        "g_audio__dolby_dmix".equalsIgnoreCase(configItemId);
        MtkLog.d("tvmenusettingsservice", "configValue = " + configValue);
        return configValue;
    }

    /* access modifiers changed from: private */
    public int reorganizeConfigValueToSettingValue(String configItemId, int configValue) {
        int settingValueInt = configValue;
        configItemId.equalsIgnoreCase("g_video__picture_mode");
        "g_video__vid_di_film_mode".equalsIgnoreCase(configItemId);
        "g_disp__disp_gamma".equalsIgnoreCase(configItemId);
        "g_audio__spdif".equalsIgnoreCase(configItemId);
        "g_audio__dolby_dmix".equalsIgnoreCase(configItemId);
        MtkLog.d("tvmenusettingsservice", "settingValueInt = " + settingValueInt);
        return settingValueInt;
    }

    /* access modifiers changed from: private */
    public void updateConfigDataChanged(String settingItemId) {
        MtkLog.d("tvmenusettingsservice", "updateConfigDataChanged: settingItemId = " + settingItemId);
        if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_PICTURE_MODE)) {
            updateSettingDB(this.pictureModeMap);
        }
        if (settingItemId.equalsIgnoreCase("picture_color_temperature")) {
            updateSettingDB(this.colorTempMap);
        }
        if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GAIN)) {
            updateSettingDB(this.whiteBalanceMap);
        }
        settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_PICTURE_NOTIFICATION);
        if (settingItemId.equalsIgnoreCase("sound_effects") || settingItemId.equalsIgnoreCase("picture_effects")) {
            updateSettingDB(this.itemIds);
        }
        if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_SOUND_STYLE)) {
            updateSettingDB(this.soundStyleMap);
        }
        if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_PICTURE_RESET_DEFAULT)) {
            MtkLog.d("tvmenusettingsservice", "resetVideoPref ");
            this.mSettingConfig.resetVideoPref();
            updateSettingDB(this.itemIds);
        }
        if (settingItemId.equalsIgnoreCase(PreferenceConfigUtils.KEY_SOUND_RESET_DEFAULT)) {
            MtkLog.d("tvmenusettingsservice", "resetAudioPref ");
            this.mSettingConfig.resetAudioPref();
            this.mHandler.sendEmptyMessageDelayed(4, 200);
        }
    }

    /* access modifiers changed from: private */
    public void updateSettingDB(final Map<String, String> map) {
        MtkLog.d("tvmenusettingsservice", "updateSettingDB " + map.size());
        new Thread(new Runnable() {
            public void run() {
                for (Map.Entry<String, String> mode : map.entrySet()) {
                    int configValue = TVMenuSettingsService.this.reorganizeConfigValueToSettingValue(mode.getValue(), TVMenuSettingsService.this.mSettingConfig.getConfigValueInt(mode.getValue()));
                    String settingStringValue = PreferenceUtils.getSettingStringValue(TVMenuSettingsService.this.mContentResolver, mode.getKey());
                    if (TVMenuSettingsService.this.arrayMap.get(mode.getKey()) == null) {
                        try {
                            int settingIntValue = Integer.parseInt(settingStringValue);
                            if ("g_audio__spdif_delay".equalsIgnoreCase(mode.getValue())) {
                                configValue *= 10;
                            }
                            if (settingIntValue != configValue) {
                                PreferenceUtils.putSettingValueInt(TVMenuSettingsService.this.mContentResolver, mode.getKey(), configValue);
                            }
                        } catch (NumberFormatException e) {
                            PreferenceUtils.putSettingValueInt(TVMenuSettingsService.this.mContentResolver, mode.getKey(), configValue);
                        }
                    } else {
                        String settingStringValue2 = TVMenuSettingsService.this.formatConfigValuetoString(mode.getKey(), configValue);
                        if (settingStringValue == null || !settingStringValue.equalsIgnoreCase(settingStringValue2)) {
                            PreferenceUtils.putSettingValueString(TVMenuSettingsService.this.mContentResolver, mode.getKey(), settingStringValue2);
                        }
                    }
                }
                if (TVMenuSettingsService.this.isPictureRefresh && map == TVMenuSettingsService.this.pictureModeMap) {
                    TVMenuSettingsService.this.mHandler.sendEmptyMessage(6);
                }
            }
        }).start();
    }

    private class SettingObserver extends ContentObserver {
        public SettingObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            MtkLog.d("tvmenusettingsservice", "onChange ++ ");
            super.onChange(selfChange);
        }

        public void onChange(boolean selfChange, Uri uri) {
            if (uri == null) {
                MtkLog.e("tvmenusettingsservice", "onChange uri = null");
                return;
            }
            MtkLog.d("tvmenusettingsservice", "onChange + ");
            String settingItemId = TVMenuSettingsService.this.formatUriToString(uri);
            TVMenuSettingsService.this.updateConfigData(TVMenuSettingsService.this.formatSettingToConfigId(settingItemId), settingItemId);
            TVMenuSettingsService.this.updateConfigDataChanged(settingItemId);
            TVMenuSettingsService.this.updateNetwork();
            super.onChange(selfChange, uri);
        }
    }

    /* access modifiers changed from: private */
    public Uri buildUri(String uId) {
        return this.BASE_URI.buildUpon().appendPath(uId).build();
    }

    /* access modifiers changed from: private */
    public String formatUriToString(Uri uri) {
        String u = uri.toString();
        return u.replace(this.BASE_URI.toString() + "/", "");
    }

    private int formatSettingValueToInt(String settingItemId, String settingItemValue) {
        MtkLog.d("tvmenusettingsservice", "formatSettingValueToInt settingItemId = " + settingItemId + "  settingItemValue  " + settingItemValue);
        try {
            int resourceId = this.arrayMap.get(settingItemId).intValue();
            MtkLog.e("tvmenusettingsservice", "resourceId = " + resourceId);
            String[] array = this.mContext.getResources().getStringArray(resourceId);
            for (int i = 0; i < array.length; i++) {
                if (settingItemValue.equalsIgnoreCase(array[i])) {
                    return i;
                }
            }
        } catch (Exception e) {
            MtkLog.e("tvmenusettingsservice", "formatSettingValueToInt faild !!!");
            e.printStackTrace();
        }
        MtkLog.e("tvmenusettingsservice", "formatSettingValueToInt error: value is null !");
        return 0;
    }

    /* access modifiers changed from: private */
    public String formatConfigValuetoString(String settingItemId, int configIntValue) {
        try {
            return this.mContext.getResources().getStringArray(this.arrayMap.get(settingItemId).intValue())[configIntValue];
        } catch (Exception e) {
            MtkLog.e("tvmenusettingsservice", "formatConfigValuetoString == null exception !!");
            MtkLog.e("tvmenusettingsservice", "formatConfigValuetoString Error: !!");
            return "";
        }
    }

    private void updateArrayResource() {
    }

    /* access modifiers changed from: private */
    public String formatSettingToConfigId(String settingId) {
        return this.itemIds.get(settingId);
    }

    /* access modifiers changed from: private */
    public void updateNetwork() {
        setWol();
        setWow();
    }

    private void setWol() {
        try {
            Log.d("tvmenusettingsservice", "setWol wol start.");
            int wol = Settings.Global.getInt(this.mContentResolver, "network_wol");
            Log.d("tvmenusettingsservice", "setWol wol: " + wol);
            if (this.mSettingConfig != null) {
                int currentValue = MtkNetworkManager.getInstance().isEnableWol();
                Log.d("tvmenusettingsservice", "setWol currentValue: " + ((int) currentValue));
                if (wol != currentValue) {
                    MtkNetworkManager instance = MtkNetworkManager.getInstance();
                    boolean z = true;
                    if (wol != 1) {
                        z = false;
                    }
                    instance.setEnableWol(z);
                }
                Log.d("tvmenusettingsservice", "setWol after wol v:" + (MtkNetworkManager.getInstance().isEnableWol() ? 1 : 0));
            }
        } catch (Settings.SettingNotFoundException ex) {
            Log.w("tvmenusettingsservice", "setWol ex: " + ex);
        }
    }

    private void setWow() {
        try {
            Log.d("tvmenusettingsservice", "setWow wow start.");
            int wow = Settings.Global.getInt(this.mContentResolver, "network_wow");
            Log.d("tvmenusettingsservice", "setWow wow: " + wow);
            if (this.mSettingConfig != null) {
                int currentValue = MtkNetworkManager.getInstance().isEnanbleWoWL();
                Log.d("tvmenusettingsservice", "setWow currentValue: " + ((int) currentValue));
                if (wow != currentValue) {
                    MtkNetworkManager instance = MtkNetworkManager.getInstance();
                    boolean z = true;
                    if (wow != 1) {
                        z = false;
                    }
                    instance.setEnableWoWL(z);
                }
                Log.d("tvmenusettingsservice", "setWow after wow v:" + (MtkNetworkManager.getInstance().isEnanbleWoWL() ? 1 : 0));
            }
        } catch (Settings.SettingNotFoundException ex) {
            Log.w("tvmenusettingsservice", "setWow ex: " + ex);
        }
    }

    private void setHDMI() {
        try {
            int hdmi = Settings.Global.getInt(this.mContentResolver, "hdmi_signal_format");
            Log.d("tvmenusettingsservice", "setHDMI hdmi: " + hdmi);
            if (this.mSettingConfig != null) {
                if (hdmi != this.mSettingConfig.getConfigValueInt("g_menu_only__hdmi_edid_index")) {
                    this.mSettingConfig.setConifg("g_menu_only__hdmi_edid_index", hdmi);
                }
                Log.d("tvmenusettingsservice", "setHDMI hdmi v:" + this.mSettingConfig.getConfigValueInt("g_menu_only__hdmi_edid_index"));
            }
        } catch (Settings.SettingNotFoundException ex) {
            Log.w("tvmenusettingsservice", "setHDMI ex: " + ex);
        }
    }

    private class NotifyCallbackHandler extends MtkTvTVCallbackHandler {
        private NotifyCallbackHandler() {
        }

        public int notifyConfigMessage(int notifyId, int data) {
            MtkLog.d("tvmenusettingsservice", "NotifyCallbackHandler: notifyId = " + notifyId + " data: " + data);
            if (notifyId == 10) {
                Message msg = TVMenuSettingsService.this.mHandler.obtainMessage();
                msg.what = 1;
                msg.arg1 = data;
                TVMenuSettingsService.this.mHandler.sendMessage(msg);
                return 0;
            } else if (notifyId == 0) {
                Message msg2 = TVMenuSettingsService.this.mHandler.obtainMessage();
                msg2.what = 3;
                msg2.arg1 = data;
                TVMenuSettingsService.this.mHandler.sendMessage(msg2);
                return 0;
            } else if (notifyId == 12) {
                Message msg3 = TVMenuSettingsService.this.mHandler.obtainMessage();
                msg3.what = 12;
                msg3.arg1 = data;
                TVMenuSettingsService.this.mHandler.sendMessage(msg3);
                return 0;
            } else if (notifyId == 13) {
                Message msg4 = TVMenuSettingsService.this.mHandler.obtainMessage();
                msg4.what = 13;
                msg4.arg1 = data;
                TVMenuSettingsService.this.mHandler.sendMessage(msg4);
                return 0;
            } else if (notifyId != 14) {
                return 0;
            } else {
                Message msg5 = TVMenuSettingsService.this.mHandler.obtainMessage();
                msg5.what = 14;
                msg5.arg1 = data;
                TVMenuSettingsService.this.mHandler.sendMessage(msg5);
                return 0;
            }
        }

        public int notifyVideoInfoMessage(int updateType, int argv1, int argv2, int argv3, int argv4, int argv5, int argv6) {
            MtkLog.d("tvmenusettingsservice", "notifyVideoInfoMessage: updateType = " + updateType + " argv1: " + argv1 + " argv2: " + argv2 + " argv3: " + argv3 + " argv4: " + argv4 + " argv5: " + argv5 + " argv6: " + argv6);
            TVMenuSettingsService.this.sendBroadcast(new Intent(TVSettingConfig.ACTION_NOTIFY_HDR_ENABLE));
            return 0;
        }
    }

    public boolean isDolbyVision() {
        return this.isDolbyVision;
    }

    public void setDolbyVision(boolean isDolbyVision2) {
        this.isDolbyVision = isDolbyVision2;
    }

    private void initArrayData() {
        this.arrayMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT, Integer.valueOf(R.array.picture_advanced_vedio_mjc_effect_entries_values));
        this.arrayMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION, Integer.valueOf(R.array.picture_advanced_vedio_mjc_demo_partition_entries_values));
    }

    private void modifyArrayData(String settingKey, int resourceId) {
        this.arrayMap.put(settingKey, Integer.valueOf(resourceId));
    }

    private void initMapdata() {
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_SURROUND, "g_audio__aud_surround_ex");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_BALANCE, "g_audio__aud_balance");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_BASS, "g_audio__aud_bass");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_TREBLE, "g_audio__aud_treble");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND, "g_audio__aud_surround");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_EQUALIZER, "g_audio__aud_equalizer");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_SPDIF_TYPE, "g_audio__spdif");
        this.itemIds.put(PreferenceConfigUtils.KEY_DIGITAL_OUTPUT, "g_audio__spdif");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY, "g_audio__spdif_delay");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL, "g_audio__agc");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DOWNMIX_MODE, "g_audio__dolby_dmix");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_DRC, "g_fusion_sound__dts_drc");
        this.itemIds.put("sound_sonic_emotion_premium", "g_fusion_sound__sonic_emosion");
        this.itemIds.put(PreferenceConfigUtils.KEY_AUTO_VOLUME_LEVELING, "g_fusion_sound__auto_volume_leveling");
        this.itemIds.put(PreferenceConfigUtils.KEY_DIGITAL_OUTPUT_TO_DEVICE, "g_fusion_sound__digital_autio_output");
        this.itemIds.put(PreferenceConfigUtils.KEY_DELTA_VOLUME, "g_fusion_sound__delta_volume");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_STYLE, "g_audio__sound_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_120HZ, "g_fusion_sound__equalizer_120hz");
        this.itemIds.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_500HZ, "g_fusion_sound__equalizer_500hz");
        this.itemIds.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_1500HZ, "g_fusion_sound__equalizer_1.5khz");
        this.itemIds.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_5000HZ, "g_fusion_sound__equalizer_5khz");
        this.itemIds.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_10000HZ, "g_fusion_sound__equalizer_10khz");
        this.itemIds.put(PreferenceConfigUtils.KEY_SONIC_EMOTION, "g_fusion_sound__sonic_emosion");
        this.itemIds.put(PreferenceConfigUtils.KEY_DIALOG_ENHANCEMENT, "g_fusion_sound__dialog_enhancement");
        this.itemIds.put(PreferenceConfigUtils.KEY_BASS_ENHANCEMENT, "g_fusion_sound__bass_enhancement");
        this.itemIds.put(PreferenceConfigUtils.KEY_ABSOLUTE_3D_SOUND, "g_fusion_sound__absl_3d_sound");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DBX_ENABLE, "g_fusion_sound__dbx_enable");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_SONIC, "g_fusion_sound__total_sonic");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_VOL, "g_fusion_sound__total_volume");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_SURROUND, "g_fusion_sound__total_surround");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_VIRTUAL_X, "g_audio__dts_virtual_x");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_TBHDX, "g_audio__dts_tbhdx");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_LIMITER, "g_audio__dts_limiter");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_AP, "g_audio__dolby_audio_processing");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_ADVANCED_SOUND_MODE, "g_audio__dolby_sound_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_ADVANCED_VOLUME_LEVELER, "g_audio__volume_leveler");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_ADVANCED_DIALOGUE_ENHANCEER, "g_audio__dialogue_enhancer");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_ADVANCED_SURROUND_VIRTUALIZER, "g_audio__dolby_surround_virtualizer");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_ATMOS, "g_audio__dolby_atmos");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_ENABLE, "g_fusion_sound__dtsss2_enable");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_SURROUND, "g_fusion_sound__dtsss2_surround");
        this.itemIds.put(PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_TRUEVOLUME, "g_fusion_sound__dtsss2_true_volume");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_MODE, "g_video__picture_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_MODE_DOLBY, "g_video__picture_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_NOTIFICATION, "g_video__dovi_user_switch");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT, "g_disp__disp_back_light");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_BRIGHTNESS, "g_video__brightness");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_CONTRAST, "g_video__contrast");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_SATURATION, "g_video__vid_sat");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_HUE, "g_video__vid_hue");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_SHARPNESS, "g_video__vid_shp");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_GAMMA, "g_disp__disp_gamma");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_BLUELIGHT, "g_video__vid_blue_light");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_HDR, "g_video__vid_hdr");
        this.itemIds.put("picture_color_temperature", "g_video__clr_temp");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_RED_GAIN, "g_video__clr_gain_r");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN, "g_video__clr_gain_g");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN, "g_video__clr_gain_b");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DNR, "g_video__vid_nr");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MPEG_NR, "g_video__vid_mpeg_nr");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL, "g_video__vid_luma");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_LOCAL_CONTRAST_CONTROL, "g_video__vid_local_contrast");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_FLESH_TONE, "g_video__vid_flash_tone");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DI_FILM_MODE, "g_video__vid_di_film_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_BLUE_STRETCH, "g_video__vid_blue_stretch");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_GAME_MODE, "g_video__vid_game_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT, "g_video__vid_mjc_effect");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION, "g_video__vid_mjc_demo");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_ENABLE, "g_fusion_picture__color_tuner_enable");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_RED, "g_fusion_picture__color_tuner_hue_red");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_GREEN, "g_fusion_picture__color_tuner_hue_green");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_BLUE, "g_fusion_picture__color_tuner_hue_blue");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_CVAN, "g_fusion_picture__color_tuner_hue_cyan");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_MAGENTA, "g_fusion_picture__color_tuner_hue_magenta");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_YELLOW, "g_fusion_picture__color_tuner_hue_yellow");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_FLESH_TONE, "g_fusion_picture__color_tuner_hue_flesh");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_RED, "g_fusion_picture__color_tuner_sat_red");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_GREEN, "g_fusion_picture__color_tuner_sat_green");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_BLUE, "g_fusion_picture__color_tuner_sat_blue");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_CVAN, "g_fusion_picture__color_tuner_sat_cyan");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_MEGENTA, "g_fusion_picture__color_tuner_sat_magenta");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_YELLOW, "g_fusion_picture__color_tuner_sat_yellow");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_FLESH_TONE, "g_fusion_picture__color_tuner_sat_flesh");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_RED, "g_fusion_picture__color_tuner_bri_red");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_GREEN, "g_fusion_picture__color_tuner_bri_green");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_BLUE, "g_fusion_picture__color_tuner_bri_blue");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_CVAN, "g_fusion_picture__color_tuner_bri_cyan");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_MEGENTA, "g_fusion_picture__color_tuner_bri_magenta");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_YELLOW, "g_fusion_picture__color_tuner_bri_yellow");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_FLESH_TONE, "g_fusion_picture__color_tuner_bri_flesh");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_RED, "g_fusion_picture__color_tuner_offset_r");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_GREEN, "g_fusion_picture__color_tuner_offset_g");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_BLUE, "g_fusion_picture__color_tuner_offset_b");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_RED, "g_fusion_picture__color_tuner_gain_r");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_GREEN, "g_fusion_picture__color_tuner_gain_g");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_BLUE, "g_fusion_picture__color_tuner_gain_b");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_ENABLE, "g_fusion_picture__pq_wb_cor_enable");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GAIN, "g_fusion_picture__pq_wb_cor_gain");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED, "g_fusion_picture__pq_wb_cor_red");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN, "g_fusion_picture__pq_wb_cor_green");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE, "g_fusion_picture__pq_wb_cor_blue");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_VGA_HP, "g_vga__vga_pos_h");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_VGA_VP, "g_vga__vga_pos_v");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_VGA_PHASE, "g_vga__vga_phase");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_VGA_CLOCK, "g_vga__vga_clock");
        this.itemIds.put(PreferenceConfigUtils.KEY_TV_HDMI_EDID_VERSION, "g_fusion_picture__hdmi_edid_version");
        this.itemIds.put("tv_picture_3d_mode", "g_video__vid_3d_mode");
        this.itemIds.put("tv_picture_3d_navigation", "g_video__vid_3d_nav_auto");
        this.itemIds.put("tv_picture_3d_to_2d", "g_video__vid_3d_to_2d");
        this.itemIds.put("tv_picture_depth_of_field", "g_video__vid_3d_fld_depth");
        this.itemIds.put("tv_picture_protrude", "g_video__vid_3d_protruden");
        this.itemIds.put("tv_picture_distance_to_tv", "g_video__vid_3d_distance");
        this.itemIds.put("tv_picture_image_safety", "g_video__vid_3d_img_sfty");
        this.itemIds.put("tv_picture_l_r_switch", "g_video__vid_3d_lr_switch");
        this.itemIds.put("tv_picture_osd_depth", "g_video__vid_3d_osd_depth");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_SPEAKER, "g_audio__aud_ad_speaker");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_HEADPHONE, "g_audio__aud_ad_hdphone");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_VOLUME, "g_audio__aud_ad_volume");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_PANE, "g_audio__aud_ad_fade_pan");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_FADER_CONTROL, "g_audio__aud_ad_fader_ctrl");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_AC4DE, "g_audio__aud_ac4_de_gain");
        this.itemIds.put(PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, "g_audio__audio only");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_PC_MODE, "g_fusion_picture__pc_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_FORMAT, "g_fusion_picture__pictrue_format");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_FILM_MODE, "g_fusion_picture__film_mode");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_NOISE_REDUCTION, "g_fusion_picture__noise_reduction");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_4K_UHD_UPSCALING, "g_fusion_picture__4k_uhd_upscaling");
        this.itemIds.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_HDMI_RGB_RANGE, "g_fusion_picture__hdmi_rgb_range");
        this.itemIds.put(PreferenceConfigUtils.KEY_DEVICE_RETAILDEMO, "g_video__vid_pq_demo");
    }

    private void initPicModeMap() {
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_MODE, "g_video__picture_mode");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_MODE_DOLBY, "g_video__picture_mode");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT, "g_disp__disp_back_light");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_BRIGHTNESS, "g_video__brightness");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_CONTRAST, "g_video__contrast");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_SATURATION, "g_video__vid_sat");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_HUE, "g_video__vid_hue");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_SHARPNESS, "g_video__vid_shp");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_GAMMA, "g_disp__disp_gamma");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DNR, "g_video__vid_nr");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_LOCAL_CONTRAST_CONTROL, "g_video__vid_local_contrast");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MPEG_NR, "g_video__vid_mpeg_nr");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT, "g_video__vid_mjc_effect");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL, "g_video__vid_luma");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_BLUE_STRETCH, "g_video__vid_blue_stretch");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_HDMI_RGB_RANGE, "g_fusion_picture__hdmi_rgb_range");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DI_FILM_MODE, "g_video__vid_di_film_mode");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_GAME_MODE, "g_video__vid_game_mode");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_PC_MODE, "g_fusion_picture__pc_mode");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_ENABLE, "g_fusion_picture__color_tuner_enable");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_RED, "g_fusion_picture__color_tuner_hue_red");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_GREEN, "g_fusion_picture__color_tuner_hue_green");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_BLUE, "g_fusion_picture__color_tuner_hue_blue");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_CVAN, "g_fusion_picture__color_tuner_hue_cyan");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_MAGENTA, "g_fusion_picture__color_tuner_hue_magenta");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_YELLOW, "g_fusion_picture__color_tuner_hue_yellow");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_FLESH_TONE, "g_fusion_picture__color_tuner_hue_flesh");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_RED, "g_fusion_picture__color_tuner_sat_red");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_GREEN, "g_fusion_picture__color_tuner_sat_green");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_BLUE, "g_fusion_picture__color_tuner_sat_blue");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_CVAN, "g_fusion_picture__color_tuner_sat_cyan");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_MEGENTA, "g_fusion_picture__color_tuner_sat_magenta");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_YELLOW, "g_fusion_picture__color_tuner_sat_yellow");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION_FLESH_TONE, "g_fusion_picture__color_tuner_sat_flesh");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_RED, "g_fusion_picture__color_tuner_bri_red");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_GREEN, "g_fusion_picture__color_tuner_bri_green");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_BLUE, "g_fusion_picture__color_tuner_bri_blue");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_CVAN, "g_fusion_picture__color_tuner_bri_cyan");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_MEGENTA, "g_fusion_picture__color_tuner_bri_magenta");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_YELLOW, "g_fusion_picture__color_tuner_bri_yellow");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_FLESH_TONE, "g_fusion_picture__color_tuner_bri_flesh");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_RED, "g_fusion_picture__color_tuner_offset_r");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_GREEN, "g_fusion_picture__color_tuner_offset_g");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET_BLUE, "g_fusion_picture__color_tuner_offset_b");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_RED, "g_fusion_picture__color_tuner_gain_r");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_GREEN, "g_fusion_picture__color_tuner_gain_g");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN_BLUE, "g_fusion_picture__color_tuner_gain_b");
        this.pictureModeMap.put("picture_color_temperature", "g_video__clr_temp");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_RED_GAIN, "g_video__clr_gain_r");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN, "g_video__clr_gain_g");
        this.pictureModeMap.put(PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN, "g_video__clr_gain_b");
    }

    private void initSoundStyleMap() {
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_SOUND_BALANCE, "g_audio__aud_balance");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_SOUND_BASS, "g_audio__aud_bass");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_SOUND_TREBLE, "g_audio__aud_treble");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND, "g_audio__aud_surround");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_120HZ, "g_fusion_sound__equalizer_120hz");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_500HZ, "g_fusion_sound__equalizer_500hz");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_1500HZ, "g_fusion_sound__equalizer_1.5khz");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_5000HZ, "g_fusion_sound__equalizer_5khz");
        this.soundStyleMap.put(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_10000HZ, "g_fusion_sound__equalizer_10khz");
    }

    private void initColorTempMap() {
        this.colorTempMap.put(PreferenceConfigUtils.KEY_PICTURE_RED_GAIN, "g_video__clr_gain_r");
        this.colorTempMap.put(PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN, "g_video__clr_gain_g");
        this.colorTempMap.put(PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN, "g_video__clr_gain_b");
    }

    private void initWhiteBalnaceMap() {
        this.whiteBalanceMap.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED, "g_fusion_picture__pq_wb_cor_red");
        this.whiteBalanceMap.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN, "g_fusion_picture__pq_wb_cor_green");
        this.whiteBalanceMap.put(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE, "g_fusion_picture__pq_wb_cor_blue");
    }

    private void clearMapData() {
        this.itemIds.clear();
        this.arrayMap.clear();
        this.pictureModeMap.clear();
    }
}
