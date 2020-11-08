package com.android.tv.settings.partnercustomizer.sound.sonic_emotion_premium;

import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class SonicEmotionPremiumFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private final String TAG = "-SonicEmotionPremiumFragment-";
    private PreferenceConfigUtils mPreferenceConfigUtils;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        initPreferences();
    }

    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        MtkLog.d("-SonicEmotionPremiumFragment-", "onPreferenceTreeClick :  key =" + preference.getKey());
        if (!(preference instanceof SwitchPreference)) {
            return false;
        }
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        MtkLog.d("-SonicEmotionPremiumFragment-", "onPreferenceChange : preferenceKey = " + preference.getKey() + "  newValue = " + newValue);
        return false;
    }

    private void initPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_sound_sonic_emotion);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("sound_sonic_emotion_premium");
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e("-SonicEmotionPremiumFragment-", "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_SONIC_EMOTION)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_sonic_emotion, "g_audio__aud_out_port"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_DIALOG_ENHANCEMENT)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_dialog_enhancement, "g_audio__aud_out_port"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_BASS_ENHANCEMENT)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_bass_enhancement, "g_audio__aud_out_port"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_ABSOLUTE_3D_SOUND)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_sound_absolute_3d_sound, "g_audio__aud_out_port"));
            }
        }
    }

    public int getMetricsCategory() {
        return 1300;
    }
}
