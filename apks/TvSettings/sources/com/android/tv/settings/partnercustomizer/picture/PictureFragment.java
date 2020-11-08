package com.android.tv.settings.partnercustomizer.picture;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.DolbyVisionLogoWindow;
import com.android.tv.settings.partnercustomizer.tvsettingservice.LoadingUI;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.android.tv.settings.partnercustomizer.utils.ProgressPreference;
import com.mediatek.twoworlds.tv.MtkTvAVModeBase;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import java.util.ArrayList;
import java.util.List;

public class PictureFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "PictureFragment";
    private final int UPDATE_PICMODE_DELAY;
    private final int UPDATE_PIC_DELAY;
    private final int UPDATE_PIC_OUT_TIMEOUT;
    private final int UPDATE_PIC_OUT_TIMEOUT_MSG;
    /* access modifiers changed from: private */
    public ContentResolver contentResolver;
    private boolean isDolbyVision;
    Handler mHandler;
    private LoadingUI mLoadingUI;
    private float mOrigDimAmount;
    /* access modifiers changed from: private */
    public PreferenceConfigUtils mPreferenceConfigUtils;
    private SharedPreferences mSharedPreferences;
    private String mStartKey;
    private TVSettingConfig mTVSettingConfig;
    DolbyVisionLogoWindow mWindow;
    private BroadcastReceiver myReceiver;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    public static PictureFragment newInstance(String key) {
        return new PictureFragment(key);
    }

    public PictureFragment(String key) {
        this.mOrigDimAmount = 0.0f;
        this.isDolbyVision = false;
        this.mStartKey = null;
        this.myReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    MtkLog.d(PictureFragment.TAG, "mreciver intent = " + intent.getAction());
                    if (TVSettingConfig.ACTION_NOTIFY_DOLBY_VISION.equals(intent.getAction())) {
                        PictureFragment.this.setDolbyVision(intent.getBooleanExtra("DOLBY", false));
                        PictureFragment.this.mHandler.sendEmptyMessageDelayed(1, 500);
                    }
                    if (TVSettingConfig.ACTION_NOTIFY_HDR_ENABLE.equals(intent.getAction())) {
                        PictureFragment.this.updateHDR_Visible();
                    }
                }
            }
        };
        this.UPDATE_PICMODE_DELAY = 1;
        this.UPDATE_PIC_DELAY = 2;
        this.UPDATE_PIC_OUT_TIMEOUT_MSG = 3;
        this.UPDATE_PIC_OUT_TIMEOUT = 6000;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        removeMessages(3);
                        PictureFragment.this.hideLoading();
                        PictureFragment.this.updateDolbyVisionState();
                        PictureFragment.this.updateAllPictureValue();
                        return;
                    case 2:
                        Log.d(PictureFragment.TAG, "UPDATE_PIC_DELAY");
                        PictureFragment.this.hideLoading();
                        removeMessages(2);
                        removeMessages(3);
                        PictureFragment.this.updateAllPictureValue();
                        return;
                    case 3:
                        Log.d(PictureFragment.TAG, "reset timeout");
                        PictureFragment.this.hideLoading();
                        return;
                    default:
                        return;
                }
            }
        };
        this.mStartKey = key;
    }

    public PictureFragment() {
        this.mOrigDimAmount = 0.0f;
        this.isDolbyVision = false;
        this.mStartKey = null;
        this.myReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    MtkLog.d(PictureFragment.TAG, "mreciver intent = " + intent.getAction());
                    if (TVSettingConfig.ACTION_NOTIFY_DOLBY_VISION.equals(intent.getAction())) {
                        PictureFragment.this.setDolbyVision(intent.getBooleanExtra("DOLBY", false));
                        PictureFragment.this.mHandler.sendEmptyMessageDelayed(1, 500);
                    }
                    if (TVSettingConfig.ACTION_NOTIFY_HDR_ENABLE.equals(intent.getAction())) {
                        PictureFragment.this.updateHDR_Visible();
                    }
                }
            }
        };
        this.UPDATE_PICMODE_DELAY = 1;
        this.UPDATE_PIC_DELAY = 2;
        this.UPDATE_PIC_OUT_TIMEOUT_MSG = 3;
        this.UPDATE_PIC_OUT_TIMEOUT = 6000;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        removeMessages(3);
                        PictureFragment.this.hideLoading();
                        PictureFragment.this.updateDolbyVisionState();
                        PictureFragment.this.updateAllPictureValue();
                        return;
                    case 2:
                        Log.d(PictureFragment.TAG, "UPDATE_PIC_DELAY");
                        PictureFragment.this.hideLoading();
                        removeMessages(2);
                        removeMessages(3);
                        PictureFragment.this.updateAllPictureValue();
                        return;
                    case 3:
                        Log.d(PictureFragment.TAG, "reset timeout");
                        PictureFragment.this.hideLoading();
                        return;
                    default:
                        return;
                }
            }
        };
        this.mStartKey = null;
    }

    private void showPreferenceFromStartkey() {
        MtkLog.d(TAG, "showPreferenceFromStartkey,mStartKey==" + this.mStartKey);
        if (this.mStartKey != null) {
            String str = this.mStartKey;
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != -786037692) {
                if (hashCode == 332045912 && str.equals(PreferenceConfigUtils.KEY_PICTURE_FORMAT)) {
                    c = 0;
                }
            } else if (str.equals(PreferenceConfigUtils.KEY_PICTURE_MODE)) {
                c = 1;
            }
            switch (c) {
                case 0:
                case 1:
                    ListPreference startPreference = (ListPreference) findPreference(this.mStartKey);
                    startPreference.getPreferenceManager().showDialog(startPreference);
                    return;
                default:
                    return;
            }
        }
    }

    public void onResume() {
        super.onResume();
        updateHDMI_VGA_Enabled();
        updateBacklightState();
        updatePictureModeUI();
        if (PreferenceConfigUtils.KEY_PICTURE_FORMAT.equals(this.mStartKey) || PreferenceConfigUtils.KEY_PICTURE_MODE.equals(this.mStartKey)) {
            showPreferenceFromStartkey();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        this.mOrigDimAmount = getActivity().getWindow().getAttributes().dimAmount;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(TVSettingConfig.ACTION_NOTIFY_DOLBY_VISION);
        filter.addAction(TVSettingConfig.ACTION_NOTIFY_HDR_ENABLE);
        getActivity().registerReceiver(this.myReceiver, filter);
    }

    public void onStart() {
        super.onStart();
        getActivity().getWindow().setDimAmount(0.0f);
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().getWindow().setDimAmount(this.mOrigDimAmount);
        hideDolbyWindow();
        getActivity().unregisterReceiver(this.myReceiver);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        MtkLog.d(TAG, "onCreatePreferences");
        this.contentResolver = getContext().getContentResolver();
        this.mTVSettingConfig = TVSettingConfig.getInstance(getContext());
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        updateDolbyVisionState();
        createPreferences();
        updateHDR_Visible();
        updatePictureFormat_Visible();
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_picture);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("picture_effects");
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e(TAG, "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_MODE)) {
                    if (this.isDolbyVision) {
                        int[] arrayIds = this.mPreferenceConfigUtils.getArrayIdsByCustomer(PreferenceConfigUtils.KEY_PICTURE_MODE_DOLBY);
                        mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, PreferenceConfigUtils.KEY_PICTURE_MODE, (int) R.string.device_picture_mode, arrayIds[0], arrayIds[1], "g_video__picture_mode"));
                    } else {
                        Preference pmPreference = this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, PreferenceConfigUtils.KEY_PICTURE_MODE, (int) R.string.device_picture_mode, "com.android.tv.settings.partnercustomizer.picture.PictureModeFragment");
                        pmPreference.setSummary((CharSequence) getCurrentPictureModeEntries());
                        mScreen.addPreference(pmPreference);
                    }
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_RESTORE)) {
                    if (this.isDolbyVision) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_restore, (String) null));
                    }
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_NOTIFICATION)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_notification, "g_video__dovi_user_switch"));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_auto_backlight, (int) R.array.picture_advanced_vedio_mjc_effect_entries, (int) R.array.a_auto_backlight_entry_values, (String) null));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_backlight, "g_disp__disp_back_light", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_BRIGHTNESS)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_brightness, "g_video__brightness", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_CONTRAST) && !this.mPreferenceConfigUtils.isVGASource(getContext())) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_contrast, "g_video__contrast", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_SATURATION) && !this.mPreferenceConfigUtils.isVGASource(getContext())) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_saturation, "g_video__vid_sat", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_HUE) && !this.mPreferenceConfigUtils.isVGASource(getContext())) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_hue, "g_video__vid_hue", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_SHARPNESS) && !this.mPreferenceConfigUtils.isVGASource(getContext())) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_sharpness, "g_video__vid_shp", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_GAMMA)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_gamma, (int) R.array.picture_gamma_entries, (int) R.array.picture_gamma_entry_values, "g_disp__disp_gamma"));
                } else if (prefKey.equals("picture_color_temperature")) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_color_temperature, "com.android.tv.settings.partnercustomizer.picture.ColorTemperatureFragment"));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_HDR)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_hdr, (int) R.array.picture_hdr_entries, (int) R.array.picture_hdr_entry_values, "g_video__vid_hdr"));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_FORMAT)) {
                    int[] arrayIdsByCustomer = this.mPreferenceConfigUtils.getArrayIdsByCustomer(PreferenceConfigUtils.KEY_PICTURE_FORMAT);
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_format, getSupportPictureFormatArrValue(1), getSupportPictureFormatArrValue(2), (String) null));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCED_VIDEO)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video, "com.android.tv.settings.partnercustomizer.picture.AdvanceVideoFragment"));
                } else if (prefKey.equals("picture_color_tune")) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_color_tune, "com.android.tv.settings.partnercustomizer.picture.ColorTuneFragment"));
                } else if (prefKey.equals("picture_white_balance11")) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_white_balance11, "com.android.tv.settings.partnercustomizer.picture.WhiteBalanceCorrection11Fragment"));
                } else if (prefKey.equals("picture_vga")) {
                    if (this.mPreferenceConfigUtils.isVGASource(getContext())) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_vga, "com.android.tv.settings.partnercustomizer.picture.VGAFragment"));
                    }
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_RESET_DEFAULT)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_reset_to_default, (String) null));
                }
            }
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        final String prefKey = preference.getKey();
        Log.d(TAG, "onPreferenceTreeClick : prefKey = " + prefKey);
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        } else if (TextUtils.equals(prefKey, PreferenceConfigUtils.KEY_PICTURE_RESTORE)) {
            getContext().sendBroadcast(new Intent(TVSettingConfig.ACTION_DOVI_RESET));
            showLoading();
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, 500);
        } else if (TextUtils.equals(prefKey, PreferenceConfigUtils.KEY_PICTURE_RESET_DEFAULT)) {
            new AlertDialog.Builder(getContext()).setMessage(R.string.string_reset).setPositiveButton(R.string.string_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    int val = PreferenceConfigUtils.getSettingValueInt(PictureFragment.this.contentResolver, prefKey);
                    Log.d(PictureFragment.TAG, "onClick : val = " + val);
                    PreferenceConfigUtils.putSettingValueInt(PictureFragment.this.contentResolver, prefKey, val + 1);
                    PictureFragment.this.mHandler.sendEmptyMessageDelayed(3, 6000);
                    PreferenceConfigUtils unused = PictureFragment.this.mPreferenceConfigUtils;
                    PreferenceConfigUtils.putSettingValueInt(PictureFragment.this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT, 0);
                    PictureFragment.this.updateBacklightState();
                    PictureFragment.this.showLoading();
                }
            }).setNegativeButton(R.string.string_no, (DialogInterface.OnClickListener) null).create().show();
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        Log.e(TAG, "onPreferenceChange preference == " + preferenceKey + "  " + ((String) newValue));
        if (this.mStartKey != null) {
            Log.d(TAG, "send KEYCODE_BACK");
            InstrumentationHandler.getInstance().sendKeyDownUpSync(4);
        }
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        char c = 65535;
        if (preferenceKey.hashCode() == 2104403808 && preferenceKey.equals(PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT)) {
            c = 0;
        }
        if (c == 0) {
            updateBacklightState();
            this.mTVSettingConfig.setConifg("g_video__auto_backlight_status", Integer.parseInt(((String) newValue) + ""));
        }
        if (this.mStartKey == null || !TextToSpeechUtil.isTTSEnabled(getActivity())) {
            return true;
        }
        this.mStartKey = null;
        this.mHandler.post(new Runnable() {
            public void run() {
                if (PictureFragment.this.getActivity() != null) {
                    PictureFragment.this.getActivity().finish();
                }
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    public void setDolbyVision(boolean isDolbyVision2) {
        this.isDolbyVision = isDolbyVision2;
    }

    private boolean isDolbyVision() {
        return this.isDolbyVision;
    }

    private void showDolbyWindow() {
        if (this.mWindow == null) {
            this.mWindow = new DolbyVisionLogoWindow(getContext());
        }
        Log.d(TAG, "new DolbyVisionLogoWindow(getContext());");
        if (this.mWindow != null && !this.mWindow.isShowing()) {
            this.mWindow.show();
        }
        Log.d(TAG, "mWindow.show();");
    }

    private void hideDolbyWindow() {
        if (this.mWindow != null) {
            this.mWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void updateDolbyVisionState() {
        this.isDolbyVision = this.mTVSettingConfig.isDolbyVision();
        MtkLog.d(TAG, "updateDolbyVisionState,isDolbyVision==" + this.isDolbyVision);
        if (this.isDolbyVision) {
            showDolbyWindow();
        } else {
            hideDolbyWindow();
        }
    }

    private void updatePicModeItemValue() {
        MtkLog.d(TAG, "updatePicModeItemValue,");
        try {
            if (this.isDolbyVision) {
                ListPreference prefL = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_MODE);
                if (prefL != null) {
                    int val = PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_MODE);
                    MtkLog.d(TAG, "val=" + val);
                    int[] arrayIds = this.mPreferenceConfigUtils.getArrayIdsByCustomer(this.isDolbyVision ? PreferenceConfigUtils.KEY_PICTURE_MODE_DOLBY : PreferenceConfigUtils.KEY_PICTURE_MODE);
                    prefL.setEntries(arrayIds[0]);
                    prefL.setEntryValues(arrayIds[1]);
                    this.mPreferenceConfigUtils.onPreferenceValueChange(prefL, Integer.valueOf(val));
                }
            } else {
                findPreference(PreferenceConfigUtils.KEY_PICTURE_MODE).setSummary((CharSequence) getCurrentPictureModeEntries());
            }
            ProgressPreference pref = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT);
            if (pref != null) {
                pref.setValue(PreferenceUtils.getSettingIntValue(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT, pref.getCurrentValue()));
            }
            ProgressPreference pref2 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_BRIGHTNESS);
            if (pref2 != null) {
                pref2.setValue(PreferenceUtils.getSettingIntValue(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_BRIGHTNESS, pref2.getCurrentValue()));
            }
            ProgressPreference pref3 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_CONTRAST);
            if (pref3 != null) {
                pref3.setValue(PreferenceUtils.getSettingIntValue(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_CONTRAST, pref3.getCurrentValue()));
            }
            ProgressPreference pref4 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_SATURATION);
            if (pref4 != null) {
                pref4.setValue(PreferenceUtils.getSettingIntValue(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_SATURATION, pref4.getCurrentValue()));
            }
            ProgressPreference pref5 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_HUE);
            if (pref5 != null) {
                pref5.setValue(PreferenceUtils.getSettingIntValue(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_HUE, pref5.getCurrentValue()));
            }
            ProgressPreference pref6 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_SHARPNESS);
            if (pref6 != null) {
                pref6.setValue(PreferenceUtils.getSettingIntValue(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_SHARPNESS, pref6.getCurrentValue()));
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void updateBacklightState() {
        MtkLog.d(TAG, "updateBacklightState,");
        int autoBacklight = PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT);
        MtkLog.d(TAG, "updateBacklightState,autoBacklight==" + autoBacklight);
        ProgressPreference pref = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_BACKLIGHT);
        if (pref == null) {
            return;
        }
        if (autoBacklight != 0 || isDolbyVision()) {
            pref.setEnabled(false);
        } else {
            pref.setEnabled(true);
        }
    }

    private void updatePictureModeUI() {
        MtkLog.d(TAG, "updatePictureModeUI,");
        if (!isDolbyVision()) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_MODE).setSummary((CharSequence) getCurrentPictureModeEntries());
        }
    }

    private String getCurrentPictureModeEntries() {
        int[] arrayIds = this.mPreferenceConfigUtils.getArrayIdsByCustomer(PreferenceConfigUtils.KEY_PICTURE_MODE);
        String[] entries = getContext().getResources().getStringArray(arrayIds[0]);
        String[] entriesValues = getContext().getResources().getStringArray(arrayIds[1]);
        PreferenceConfigUtils preferenceConfigUtils = this.mPreferenceConfigUtils;
        int val = PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_MODE);
        MtkLog.d(TAG, "getCurrentPictureModeEntries,val==" + val);
        for (int j = 0; j < entriesValues.length; j++) {
            if (val == Integer.parseInt(entriesValues[j])) {
                MtkLog.d(TAG, "entries[j] == " + entries[j]);
                return entries[j];
            }
        }
        return null;
    }

    private void resetPictureModeUI() {
        if (!isDolbyVision()) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, findPreference(PreferenceConfigUtils.KEY_PICTURE_MODE), 0);
        }
    }

    /* access modifiers changed from: private */
    public void updateAllPictureValue() {
        MtkLog.d(TAG, "updateAllPictureValue");
        updatePicModeItemValue();
        ListPreference prefL = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT);
        if (prefL != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_AUTO_BACKLIGHT)));
        }
        ListPreference prefL2 = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_GAMMA);
        if (prefL2 != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL2, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_GAMMA)));
        }
        ListPreference prefL3 = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_HDR);
        if (prefL3 != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL3, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_HDR)));
        }
        ListPreference prefL4 = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_FORMAT);
        if (prefL4 != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL4, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_FORMAT)));
        }
        SwitchPreference prefS = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_NOTIFICATION);
        if (prefS != null) {
            boolean z = true;
            if (PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_NOTIFICATION) != 1) {
                z = false;
            }
            prefS.setChecked(z);
        }
    }

    /* access modifiers changed from: private */
    public void updateHDR_Visible() {
        ListPreference pic_hdr = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_HDR);
        boolean z = false;
        if (pic_hdr != null) {
            pic_hdr.setVisible(false);
        }
        int hdrType = new MtkTvAVModeBase().getVideoInfoValue(2);
        MtkLog.d(TAG, "updateHDR_Visible hdrType = " + hdrType);
        if (pic_hdr != null) {
            if (hdrType != 0) {
                z = true;
            }
            pic_hdr.setVisible(z);
        }
    }

    private void updatePictureFormat_Visible() {
        ListPreference pictureFormat = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_FORMAT);
        String is3rdSource = SaveValue.readWorldStringValue(getContext(), "is3rdSource");
        Log.d(TAG, "is3rdSource : " + is3rdSource);
        if (!this.mTVSettingConfig.isInLiveTV() || is3rdSource == null || is3rdSource.equals("1") || this.mTVSettingConfig.isSignalLoss()) {
            if (pictureFormat != null) {
                pictureFormat.setVisible(false);
            }
        } else if (pictureFormat != null) {
            pictureFormat.setVisible(true);
        }
    }

    private void updateHDMI_VGA_Enabled() {
        boolean isTiming = !this.mPreferenceConfigUtils.isHDMIVGASignal(getContext()) && !this.mPreferenceConfigUtils.isVGASource(getContext());
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_CONTRAST) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_CONTRAST).setVisible(isTiming);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_SATURATION) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_SATURATION).setVisible(isTiming);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_HUE) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_HUE).setVisible(isTiming);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_SHARPNESS) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_SHARPNESS).setVisible(isTiming);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCED_VIDEO) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCED_VIDEO).setVisible(isTiming);
        }
        if (findPreference("picture_color_tune") != null) {
            findPreference("picture_color_tune").setVisible(isTiming);
        }
    }

    private CharSequence[] getSupportPictureFormatArrValue(int type) {
        String[] entries = getContext().getResources().getStringArray(R.array.screen_mode_array_funai);
        String[] entry_values = getContext().getResources().getStringArray(R.array.screen_mode_array_funai_values);
        ArrayList<String> listEntry = new ArrayList<>();
        ArrayList<String> listEntryValue = new ArrayList<>();
        int[] supportArrs = this.mPreferenceConfigUtils.getSupportScreenModes();
        if (supportArrs != null && supportArrs.length != 0) {
            Log.d(TAG, "supportArrs = " + supportArrs.length);
            for (int i = 0; i < supportArrs.length; i++) {
                String supp = supportArrs[i] + "";
                Log.d(TAG, "supp = " + supp);
                int j = 0;
                while (true) {
                    if (j >= entry_values.length) {
                        break;
                    }
                    String entry = entry_values[j];
                    if (entry.equals(supp)) {
                        Log.d(TAG, " i= " + i + " j = " + j + " value = " + entry);
                        listEntry.add(entries[j]);
                        listEntryValue.add(entry_values[j]);
                        break;
                    }
                    j++;
                }
            }
            CharSequence[] arrEntry = (CharSequence[]) listEntry.toArray(new CharSequence[listEntry.size()]);
            CharSequence[] arrEntryValue = (CharSequence[]) listEntryValue.toArray(new CharSequence[listEntryValue.size()]);
            if (type == 1) {
                return arrEntry;
            }
            return arrEntryValue;
        } else if (type == 1) {
            return entries;
        } else {
            return entry_values;
        }
    }

    /* access modifiers changed from: private */
    public void showLoading() {
        if (this.mLoadingUI == null) {
            this.mLoadingUI = new LoadingUI(getContext());
            this.mLoadingUI.show();
        } else if (!this.mLoadingUI.isShowing()) {
            this.mLoadingUI.show();
        }
    }

    /* access modifiers changed from: private */
    public void hideLoading() {
        try {
            if (this.mLoadingUI != null && this.mLoadingUI.isShowing()) {
                this.mLoadingUI.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
