package com.android.tv.settings.partnercustomizer.sound.dts_studio;

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

public class DTS_StudioSoundFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String TAG = "DTS_StudioSoundFragment";
    private PreferenceConfigUtils mPreferenceConfigUtils;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        createPreferences();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        return true;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        MtkLog.d(this.TAG, "onPreferenceTreeClick");
        if (preference instanceof SwitchPreference) {
            SwitchPreference pref = (SwitchPreference) preference;
            String str = this.TAG;
            MtkLog.d(str, "pref.isChecked() = " + pref.isChecked());
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, pref, Boolean.valueOf(pref.isChecked()));
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.sound_dts_studio);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("sound_dts_studio");
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e(this.TAG, "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_ENABLE)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_color_tune_enable, (String) null));
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_SURROUND)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.sound_dts_studio_surround, (String) null));
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DTS_STUDIO_TRUEVOLUME)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.sound_dts_studio_true_volume, (String) null));
                }
            }
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
