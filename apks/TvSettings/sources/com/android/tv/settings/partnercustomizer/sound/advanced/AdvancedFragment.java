package com.android.tv.settings.partnercustomizer.sound.advanced;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class AdvancedFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String TAG = "AdvancedFragment";
    private SwitchPreference dapPref;
    private SwitchPreference datPref;
    private ListPreference diaPref;
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private ContentResolver mResolver;
    private ListPreference soundModePref;
    private SwitchPreference surrPref;
    private SwitchPreference volPref;

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        updatePrefEnabled();
        super.onResume();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        this.mResolver = getContext().getContentResolver();
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        createPreferences();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        onPrefClick(preference);
        updatePrefEnabled();
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        onPrefChange(preference, newValue);
        updatePrefEnabled();
        return true;
    }

    private boolean onPrefClick(Preference preference) {
        String preferenceKey = preference.getKey();
        if (preferenceKey == null) {
            MtkLog.d(this.TAG, "onPrefClick: preferenceKey: null");
            return false;
        }
        String str = this.TAG;
        MtkLog.d(str, "onPrefClick: preferenceKey: " + preferenceKey);
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        }
        updatePrefEnabled();
        return true;
    }

    private boolean onPrefChange(Preference preference, Object newValue) {
        if (preference.getKey() == null) {
            MtkLog.d(this.TAG, "onPrefChange: preferenceKey: null");
            return false;
        }
        String str = this.TAG;
        Log.e(str, "onPreferenceChange preference.getKey " + preference.getKey());
        String str2 = this.TAG;
        Log.d(str2, "onPreferenceChange newValue == " + ((String) newValue));
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        updatePrefEnabled();
        return true;
    }

    private void updatePrefEnabled() {
        if (PreferenceConfigUtils.getSettingValueInt(this.mResolver, PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_AP) == 1) {
            if (this.soundModePref != null) {
                this.soundModePref.setEnabled(true);
            }
            if (PreferenceConfigUtils.getSettingValueInt(this.mResolver, PreferenceConfigUtils.KEY_SOUND_ADVANCED_SOUND_MODE) == 10) {
                pref(true);
            } else {
                pref(false);
            }
        } else {
            if (this.soundModePref != null) {
                this.soundModePref.setEnabled(false);
            }
            pref(false);
        }
    }

    private void pref(boolean enable) {
        if (this.volPref != null) {
            this.volPref.setEnabled(enable);
        }
        if (this.diaPref != null) {
            this.diaPref.setEnabled(enable);
        }
        if (this.surrPref != null) {
            this.surrPref.setEnabled(enable);
        }
        if (this.datPref != null) {
            this.datPref.setEnabled(enable);
        }
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.sound_dap);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("sound_dap");
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e(this.TAG, "prefKey is null");
                    return;
                }
                String str = this.TAG;
                MtkLog.e(str, "prefKey is : " + prefKey);
                if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_AP)) {
                    this.dapPref = this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.sound_dap, (String) null);
                    mScreen.addPreference(this.dapPref);
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_ADVANCED_SOUND_MODE)) {
                    this.soundModePref = this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_advanced_soundmode, (int) R.array.sound_advanced_sound_mode_entries, (int) R.array.sound_advanced_sound_mode_entry_values, (String) null);
                    mScreen.addPreference(this.soundModePref);
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_ADVANCED_VOLUME_LEVELER)) {
                    this.volPref = this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_advanced_volumeleveler, (String) null);
                    mScreen.addPreference(this.volPref);
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_ADVANCED_DIALOGUE_ENHANCEER)) {
                    this.diaPref = this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_sound_advanced_dialogueenhancer, (int) R.array.picture_advanced_video_local_contrast_entries, (int) R.array.sound_advanced_dialog_enhancer_entry_values, (String) null);
                    mScreen.addPreference(this.diaPref);
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_ADVANCED_SURROUND_VIRTUALIZER)) {
                    this.surrPref = this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_advanced_surroundvirtualizer, (String) null);
                    mScreen.addPreference(this.surrPref);
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_ADVANCED_DOLBY_ATMOS)) {
                    int value = TVSettingConfig.getInstance(getContext()).getDolbyAtmosType();
                    String str2 = this.TAG;
                    MtkLog.e(str2, "dolby atmos value = " + value);
                    if (value == 5) {
                        this.datPref = this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_advanced_dolbyatmos, (String) null);
                        mScreen.addPreference(this.datPref);
                    }
                }
            }
        }
    }

    private void setupPrefEnabled() {
    }

    public int getMetricsCategory() {
        return 336;
    }
}
