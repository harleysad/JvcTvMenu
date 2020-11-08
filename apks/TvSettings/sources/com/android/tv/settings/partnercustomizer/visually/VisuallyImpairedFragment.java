package com.android.tv.settings.partnercustomizer.visually;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
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

public class VisuallyImpairedFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "VisuallyImpairedFragment";
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private TVSettingConfig mTVSettingConfig;

    public static VisuallyImpairedFragment getInstance() {
        return new VisuallyImpairedFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        MtkLog.d(TAG, "onCreatePreferences");
        this.mTVSettingConfig = TVSettingConfig.getInstance(getActivity());
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        createPrefs();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String prefKey = preference.getKey();
        Log.d(TAG, "onPreferenceTreeClick : prefKey = " + prefKey);
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        Log.e(TAG, "onPreferenceChange preference == " + preferenceKey + "  " + ((String) newValue));
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return true;
    }

    private void createPrefs() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.accessibility_visually_impaired);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_DEVICE_ACC_VISAULLY);
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e(TAG, "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_SPEAKER)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.accessibility_audio_visually_speaker, (String) null));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_HEADPHONE)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.accessibility_audio_visually_headphone, (String) null));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_VOLUME)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.accessibility_audio_visually_volume, "g_audio__aud_ad_volume", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_PANE)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.accessibility_audio_visually_pan_and_fade, "g_video__dovi_user_switch"));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_VISUALLY_AUDIO)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.accessibility_audio_visually_impaired_audio, "com.android.tv.settings.partnercustomizer.visually.VisuallyImpairedAudioFragment"));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_ACC_VISUALLY_FADER_CONTROL)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.menu_audio_visually_fader_control, (int) R.array.menu_audio_visually_fader_array, (int) R.array.menu_audio_visually_fader_array_values, (String) null));
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
    }

    public int getMetricsCategory() {
        return 336;
    }
}
