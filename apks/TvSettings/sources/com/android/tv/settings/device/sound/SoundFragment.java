package com.android.tv.settings.device.sound;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.LoadingUI;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.android.tv.settings.partnercustomizer.utils.ProgressPreference;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import java.util.ArrayList;
import java.util.List;

@Keep
public class SoundFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    static final String KEY_SOUND_EFFECTS = "sound_effects";
    static final String KEY_SURROUND_PASSTHROUGH = "surround_passthrough";
    static final String KEY_SURROUND_SOUND_CATEGORY = "surround_sound_category";
    static final String KEY_SURROUND_SOUND_FORMAT_PREFIX = "surround_sound_format_";
    static final String VAL_SURROUND_SOUND_ALWAYS = "always";
    static final String VAL_SURROUND_SOUND_AUTO = "auto";
    static final String VAL_SURROUND_SOUND_MANUAL = "manual";
    static final String VAL_SURROUND_SOUND_NEVER = "never";
    private final int MSG_UPDATE_SOUND_STYLE_RELATED_ITEM;
    private final String TAG;
    private final int UPDATE_AUD_DELAY;
    /* access modifiers changed from: private */
    public ContentResolver contentResolver;
    private BroadcastReceiver headReceiver;
    private AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private LoadingUI mLoadingUI;
    /* access modifiers changed from: private */
    public PreferenceConfigUtils mPreferenceConfigUtils;
    private String mStartKey;
    private TVSettingConfig mTVSettingConfig;
    private PreferenceManager manager;
    private String marketregion;

    public static SoundFragment newInstance() {
        return new SoundFragment();
    }

    public static SoundFragment newInstance(String key) {
        return new SoundFragment(key);
    }

    public SoundFragment() {
        this.TAG = "SoundFragment";
        this.mStartKey = null;
        this.UPDATE_AUD_DELAY = 2;
        this.MSG_UPDATE_SOUND_STYLE_RELATED_ITEM = 3;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        SoundFragment.this.hideLoading();
                        SoundFragment.this.updateAllAudioPrefValue();
                        return;
                    case 3:
                        SoundFragment.this.updateSoundStyleRelatedItemUI();
                        return;
                    default:
                        return;
                }
            }
        };
        this.headReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && "android.intent.action.HEADSET_PLUG".equals(action) && intent.hasExtra("state")) {
                    SoundFragment.this.onOneEnabled();
                    if (intent.getIntExtra("state", 0) != 0) {
                        intent.getIntExtra("state", 0);
                    }
                }
            }
        };
        this.mStartKey = null;
    }

    public SoundFragment(String key) {
        this.TAG = "SoundFragment";
        this.mStartKey = null;
        this.UPDATE_AUD_DELAY = 2;
        this.MSG_UPDATE_SOUND_STYLE_RELATED_ITEM = 3;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        SoundFragment.this.hideLoading();
                        SoundFragment.this.updateAllAudioPrefValue();
                        return;
                    case 3:
                        SoundFragment.this.updateSoundStyleRelatedItemUI();
                        return;
                    default:
                        return;
                }
            }
        };
        this.headReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && "android.intent.action.HEADSET_PLUG".equals(action) && intent.hasExtra("state")) {
                    SoundFragment.this.onOneEnabled();
                    if (intent.getIntExtra("state", 0) != 0) {
                        intent.getIntExtra("state", 0);
                    }
                }
            }
        };
        this.mStartKey = key;
    }

    private void showPreferenceFromStartkey() {
        MtkLog.d("SoundFragment", "showPreferenceFromStartkey,mStartKey==" + this.mStartKey);
        if (this.mStartKey != null) {
            String str = this.mStartKey;
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != -2026700159) {
                if (hashCode == -1156353205 && str.equals(PreferenceConfigUtils.KEY_SOUND_SPEAKERS)) {
                    c = 0;
                }
            } else if (str.equals(PreferenceConfigUtils.KEY_SOUND_STYLE)) {
                c = 1;
            }
            switch (c) {
                case 0:
                case 1:
                    this.manager.showDialog((ListPreference) findPreference(this.mStartKey));
                    return;
                default:
                    return;
            }
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.marketregion = SystemProperties.get("ro.vendor.mtk.system.marketregion");
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sound, (String) null);
        TwoStatePreference soundPref = (TwoStatePreference) findPreference("sound_effects");
        soundPref.setChecked(getSoundEffectsEnabled());
        this.manager = getPreferenceManager();
        this.mAudioManager = (AudioManager) getContext().getSystemService("audio");
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(this.manager.getContext());
        this.mTVSettingConfig = TVSettingConfig.getInstance(getContext());
        this.contentResolver = getContext().getContentResolver();
        registHeadsetReciver();
        if (soundPref != null) {
            soundPref.setVisible(false);
        }
        createPreferences();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "sound_effects")) {
            setSoundEffectsEnabled(((TwoStatePreference) preference).isChecked());
        }
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        } else {
            final String key = preference.getKey();
            if (key != null && key.equals(PreferenceConfigUtils.KEY_SOUND_RESET_DEFAULT)) {
                new AlertDialog.Builder(getContext()).setMessage(R.string.string_reset).setPositiveButton(R.string.string_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int val = PreferenceConfigUtils.getSettingValueInt(SoundFragment.this.contentResolver, key);
                        Log.d("SoundFragment", "onClick : val = " + val);
                        PreferenceConfigUtils.putSettingValueInt(SoundFragment.this.contentResolver, key, val + 1);
                        SoundFragment.this.mHandler.sendEmptyMessageDelayed(2, 2500);
                        PreferenceConfigUtils unused = SoundFragment.this.mPreferenceConfigUtils;
                        PreferenceConfigUtils.putSettingValueInt(SoundFragment.this.contentResolver, PreferenceConfigUtils.KEY_SOUND_SPEAKERS, 1);
                        SoundFragment.this.showLoading();
                    }
                }).setNegativeButton(R.string.string_no, (DialogInterface.OnClickListener) null).create().show();
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.e("SoundFragment", "onPreferenceChange preference.getKey " + preference.getKey());
        Log.d("SoundFragment", "onPreferenceChange newValue == " + ((String) newValue));
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY) != null) {
            findPreference(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY).setEnabled(setupSPDIF_DelayPref());
        }
        if (TextUtils.equals(preference.getKey(), PreferenceConfigUtils.KEY_SOUND_STYLE)) {
            this.mHandler.sendEmptyMessageDelayed(3, 1000);
        }
        if (this.mStartKey == null || !TextToSpeechUtil.isTTSEnabled(getActivity())) {
            return true;
        }
        this.mStartKey = null;
        this.mHandler.post(new Runnable() {
            public void run() {
                if (SoundFragment.this.getActivity() != null) {
                    SoundFragment.this.getActivity().finish();
                }
            }
        });
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.headReceiver != null) {
            getContext().unregisterReceiver(this.headReceiver);
        }
    }

    private boolean isOnePlusOpened() {
        return isDtsOpened() || isSonicEmotionOpened() || isDBXOpened() || isDAPOpened() || isDTSStudioOpened();
    }

    private boolean isDtsOpened() {
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_DTS) != null) {
            int vx = PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SOUND_DTS_VIRTUAL_X);
            Log.e("SoundFragment", "isDtsOpened " + vx);
            if (vx == 0) {
                return false;
            }
            return true;
        }
        Log.e("SoundFragment", "isDtsOpened no pref");
        return false;
    }

    private boolean isSonicEmotionOpened() {
        if (findPreference("sound_sonic_emotion_premium") == null) {
            Log.e("SoundFragment", "isSonicEmotionOpened no pref");
            return false;
        } else if (PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SONIC_EMOTION) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDBXOpened() {
        if (findPreference("sound_dbx") == null) {
            Log.e("SoundFragment", "isDBXOpened no pref");
            return false;
        } else if (PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SOUND_DBX_ENABLE) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDAPOpened() {
        if (findPreference("sound_dap") == null) {
            Log.e("SoundFragment", "isDAPOpened no pref");
            return false;
        } else if (PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_AP) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDTSStudioOpened() {
        if (findPreference("sound_dts_studio") == null) {
            Log.e("SoundFragment", "isDTSStudioOpened no pref");
            return false;
        } else if (PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_ENABLE) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void onResume() {
        super.onResume();
        updateSettingsEnable();
        showPreferenceFromStartkey();
    }

    private void updateSettingsEnable() {
        boolean oneOpened = isOnePlusOpened();
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY) != null) {
            findPreference(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY).setEnabled(setupSPDIF_DelayPref());
        }
        Log.e("SoundFragment", "updateSettingsEnable " + oneOpened);
        if (oneOpened) {
            Preference preference = findPreference(PreferenceConfigUtils.KEY_SOUND_DTS);
            if (preference != null) {
                preference.setEnabled(isDtsOpened());
            }
            Preference preference2 = findPreference("sound_sonic_emotion_premium");
            if (preference2 != null) {
                preference2.setEnabled(isSonicEmotionOpened());
            }
            Preference preference3 = findPreference("sound_dbx");
            if (preference3 != null) {
                preference3.setEnabled(isDBXOpened());
            }
            Preference preference4 = findPreference("sound_dap");
            if (preference4 != null) {
                preference4.setEnabled(isDAPOpened());
            }
            Preference preference5 = findPreference("sound_dts_studio");
            if (preference5 != null) {
                preference5.setEnabled(isDTSStudioOpened());
            }
        } else {
            Preference preference6 = findPreference(PreferenceConfigUtils.KEY_SOUND_DTS);
            if (preference6 != null) {
                preference6.setEnabled(true);
            }
            Preference preference7 = findPreference("sound_sonic_emotion_premium");
            if (preference7 != null) {
                preference7.setEnabled(true);
            }
            Preference preference8 = findPreference("sound_dbx");
            if (preference8 != null) {
                preference8.setEnabled(true);
            }
            Preference preference9 = findPreference("sound_dap");
            if (preference9 != null) {
                preference9.setEnabled(true);
            }
            Preference preference10 = findPreference("sound_dts_studio");
            if (preference10 != null) {
                preference10.setEnabled(true);
            }
        }
        onOneEnabled();
    }

    private void setupSPDIF_TypePref(ListPreference sound_spdif_type_pref) {
        int spdifTypeOption = TVSettingConfig.getInstance(getContext()).getMax("g_audio__spdif") - TVSettingConfig.getInstance(getContext()).getMin("g_audio__spdif");
        MtkLog.d("SoundFragment", "spdifTypeOption is " + spdifTypeOption);
        if (spdifTypeOption == 5) {
            String[] entries = getContext().getResources().getStringArray(R.array.sound_spdif_type_entries_ms12b);
            String[] entry_values = getContext().getResources().getStringArray(R.array.sound_spdif_type_entry_values_ms12b);
            sound_spdif_type_pref.setEntries((CharSequence[]) entries);
            sound_spdif_type_pref.setEntryValues((CharSequence[]) entry_values);
        }
    }

    private boolean setupSPDIF_DelayPref() {
        if (PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SOUND_SPDIF_TYPE) == 0) {
            return false;
        }
        return true;
    }

    private boolean getSoundEffectsEnabled() {
        return getSoundEffectsEnabled(getActivity().getContentResolver());
    }

    public static boolean getSoundEffectsEnabled(ContentResolver contentResolver2) {
        return Settings.System.getInt(contentResolver2, "sound_effects_enabled", 1) != 0;
    }

    private void setSoundEffectsEnabled(boolean enabled) {
        if (enabled) {
            this.mAudioManager.loadSoundEffects();
        } else {
            this.mAudioManager.unloadSoundEffects();
        }
        Settings.System.putInt(getActivity().getContentResolver(), "sound_effects_enabled", enabled);
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("sound_effects");
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e("SoundFragment", "prefKey is null");
                    return;
                }
                MtkLog.e("SoundFragment", "prefKey is : " + prefKey);
                if (prefKey.equals("sound_effects")) {
                    if (findPreference("sound_effects") != null) {
                        findPreference("sound_effects").setVisible(true);
                    }
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_STYLE)) {
                    int[] arrayIds = this.mPreferenceConfigUtils.getArrayIdsByCustomer(prefKey);
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_style, arrayIds[0], arrayIds[1], "g_audio__sound_mode"));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_BALANCE)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_sound_balance, "g_audio__aud_balance", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_BASS)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_sound_bass, "g_audio__aud_bass", 1));
                } else {
                    if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_TREBLE)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_sound_treble, "g_audio__aud_treble", 1));
                    }
                    if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_surround, "g_audio__aud_surround"));
                    } else if (prefKey.equals("sound_equalizer_detail")) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_equalizer_detail, "com.android.tv.settings.partnercustomizer.sound.equalizerdetail.EqualizerDetailFragment"));
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_SPEAKER)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, PreferenceConfigUtils.KEY_SOUND_SPEAKERS, (int) R.string.device_sound_speakers, (int) R.array.sound_speakers_entries, (int) R.array.sound_speakers_entry_values, "g_audio__aud_equalizer"));
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_SPDIF_TYPE)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_digital_output, (int) R.array.sound_spdif_type_entries, (int) R.array.sound_spdif_type_entry_values, "g_audio__spdif"));
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY)) {
                        ProgressPreference p = this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_sound_spdifdelay, "g_audio__spdif_delay", 10);
                        p.setMaxValue(p.getMaxValue() * 10);
                        p.setMinValue(p.getMinValue() * 10);
                        mScreen.addPreference(p);
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_autovolumecontrol, "g_audio__agc"));
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DOWNMIX_MODE)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_downmixmode, (int) R.array.sound_downmix_mode_entries, (int) R.array.sound_downmix_mode_entry_values, "g_audio__dolby_dmix"));
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DTS_DRC)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_dts_drc, "g_fusion_sound__dts_drc"));
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DTS)) {
                        if (DataSeparaterUtil.getInstance() == null || !DataSeparaterUtil.getInstance().isSupportDTS()) {
                            MtkLog.d("SoundFragment", "not support DTS");
                        } else {
                            mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.sound_dts, "com.android.tv.settings.partnercustomizer.sound.dts.DTSFragment"));
                        }
                    } else if (prefKey.equals("sound_sonic_emotion_premium")) {
                        if (DataSeparaterUtil.getInstance() == null || !DataSeparaterUtil.getInstance().isSupportSonicEmotion()) {
                            MtkLog.d("SoundFragment", "not support SONIC_EMOTION_PRE");
                        } else {
                            mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_sonic_emotion, "com.android.tv.settings.partnercustomizer.sound.sonic_emotion_premium.SonicEmotionPremiumFragment"));
                        }
                    } else if (prefKey.equals("sound_dbx")) {
                        if (DataSeparaterUtil.getInstance() == null || !DataSeparaterUtil.getInstance().isSupportDBX()) {
                            MtkLog.d("SoundFragment", "not support DBX");
                        } else {
                            mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.sound_dbx, "com.android.tv.settings.partnercustomizer.sound.dbx.DBXFragment"));
                        }
                    } else if (prefKey.equals("sound_dap")) {
                        if (DataSeparaterUtil.getInstance() == null || !DataSeparaterUtil.getInstance().isSupportDAP()) {
                            MtkLog.d("SoundFragment", "not support DAP");
                        } else {
                            mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.sound_dap, "com.android.tv.settings.partnercustomizer.sound.advanced.AdvancedFragment"));
                        }
                    } else if (prefKey.equals("sound_dts_studio")) {
                        if (DataSeparaterUtil.getInstance() == null || !DataSeparaterUtil.getInstance().isSupportDTSSS()) {
                            MtkLog.d("SoundFragment", "not support DTS_STUDIO");
                        } else {
                            mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.sound_dts_studio, "com.android.tv.settings.partnercustomizer.sound.dts_studio.DTS_StudioSoundFragment"));
                        }
                    } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_RESET_DEFAULT)) {
                        mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.sound_reset_to_default, (String) null));
                    }
                }
            }
            if (!prefKeys.contains("sound_effects")) {
                String eff1 = Settings.Global.getString(this.contentResolver, "sound_effect");
                String eff = eff1 == null ? "Off" : eff1;
                MtkLog.d("SoundFragment", "eff = " + eff);
                if (findPreference("sound_effects") != null && eff.equalsIgnoreCase("On")) {
                    findPreference("sound_effects").setVisible(true);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAllAudioPrefValue() {
        MtkLog.d("SoundFragment", "updateAllAudioPrefValue");
        ProgressPreference prefP = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_BALANCE);
        if (prefP != null) {
            prefP.setValue(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_BALANCE));
        }
        ProgressPreference prefP2 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_BASS);
        if (prefP2 != null) {
            prefP2.setValue(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_BASS));
        }
        ProgressPreference prefP3 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_TREBLE);
        if (prefP3 != null) {
            prefP3.setValue(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_TREBLE));
        }
        ProgressPreference prefP4 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY);
        if (prefP4 != null) {
            prefP4.setEnabled(setupSPDIF_DelayPref());
            prefP4.setValue(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_SPDIF_DELAY));
        }
        ListPreference prefL = (ListPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_STYLE);
        if (prefL != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_STYLE)));
        }
        ListPreference prefL2 = (ListPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_SPEAKERS);
        if (prefL2 != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL2, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_SPEAKERS)));
        }
        ListPreference prefL3 = (ListPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_SPDIF_TYPE);
        if (prefL3 != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL3, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_SPDIF_TYPE)));
        }
        ListPreference prefL4 = (ListPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_DOWNMIX_MODE);
        if (prefL4 != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(prefL4, Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_DOWNMIX_MODE)));
        }
        SwitchPreference prefS = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND);
        boolean z = false;
        if (prefS != null) {
            prefS.setChecked(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND) == 1);
        }
        SwitchPreference prefS2 = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL);
        if (prefS2 != null) {
            prefS2.setChecked(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL) == 1);
        }
        SwitchPreference prefS3 = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_DTS_DRC);
        if (prefS3 != null) {
            if (PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_DTS_DRC) == 1) {
                z = true;
            }
            prefS3.setChecked(z);
        }
        updateSettingsEnable();
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

    /* access modifiers changed from: private */
    public void updateSoundStyleRelatedItemUI() {
        MtkLog.d("SoundFragment", "updateSoundStyleItemValue");
        try {
            this.mPreferenceConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_SOUND_BALANCE), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_BALANCE)));
            this.mPreferenceConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_SOUND_BASS), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_BASS)));
            this.mPreferenceConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_SOUND_TREBLE), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_TREBLE)));
            this.mPreferenceConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND)));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void onOneEnabled() {
        boolean enable = isOnePlusOpened();
        boolean headset = getWiredHeadsetState();
        boolean z = false;
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_STYLE) != null) {
            this.mPreferenceConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_SOUND_STYLE), Integer.valueOf(this.mTVSettingConfig.getConfigValueInt("g_audio__sound_mode")));
            findPreference(PreferenceConfigUtils.KEY_SOUND_STYLE).setEnabled(!enable && !headset);
        }
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_BALANCE) != null) {
            ProgressPreference p = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_BALANCE);
            p.setCurrentValue(this.mTVSettingConfig.getConfigValueInt("g_audio__aud_balance"));
            p.setEnabled(!enable);
        }
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_BASS) != null) {
            ProgressPreference p2 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_BASS);
            p2.setCurrentValue(this.mTVSettingConfig.getConfigValueInt("g_audio__aud_bass"));
            p2.setEnabled(!enable && !headset);
        }
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_TREBLE) != null) {
            ProgressPreference p3 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_TREBLE);
            p3.setCurrentValue(this.mTVSettingConfig.getConfigValueInt("g_audio__aud_treble"));
            p3.setEnabled(!enable && !headset);
        }
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND) != null) {
            ((SwitchPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND)).setChecked(this.mTVSettingConfig.getConfigValueInt("g_audio__aud_surround") == 1);
            findPreference(PreferenceConfigUtils.KEY_SOUND_SOUND_SURROUND).setEnabled(!enable && !headset);
        }
        if (findPreference("sound_equalizer_detail") != null) {
            findPreference("sound_equalizer_detail").setEnabled(!enable && !headset);
        }
        if (findPreference(PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL) != null) {
            SwitchPreference switchPreference = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL);
            if (this.mTVSettingConfig.getConfigValueInt("g_audio__agc") == 1) {
                z = true;
            }
            switchPreference.setChecked(z);
            findPreference(PreferenceConfigUtils.KEY_SOUND_AUTO_VOLUME_CONTROL).setEnabled(!enable);
        }
    }

    private boolean getWiredHeadsetState() {
        boolean on = this.mAudioManager.isWiredHeadsetOn();
        MtkLog.d("SoundFragment", "getWiredHeadsetState = " + on);
        return on;
    }

    private void registHeadsetReciver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("android.intent.action.HEADSET_PLUG");
        getContext().registerReceiver(this.headReceiver, mFilter);
    }

    static String getSurroundPassthroughSetting(Context context) {
        switch (Settings.Global.getInt(context.getContentResolver(), "encoded_surround_output", 0)) {
            case 1:
                return VAL_SURROUND_SOUND_NEVER;
            case 2:
            case 3:
                return VAL_SURROUND_SOUND_MANUAL;
            default:
                return VAL_SURROUND_SOUND_AUTO;
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
