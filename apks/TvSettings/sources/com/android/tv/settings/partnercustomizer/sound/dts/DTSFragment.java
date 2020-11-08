package com.android.tv.settings.partnercustomizer.sound.dts;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DTSFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    PreferenceConfigUtils mPreferenceConfigUtils;

    public static DTSFragment newInstance() {
        return new DTSFragment();
    }

    public void onResume() {
        super.onResume();
        updatePrefEnabled();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String selection) {
        setPreferencesFromResource(R.xml.partner_dts, (String) null);
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        PreferenceUtils.setupSwitchPreferences(this, PreferenceConfigUtils.KEY_SOUND_DTS_VIRTUAL_X, PreferenceConfigUtils.KEY_SOUND_DTS_VIRTUAL_X);
        PreferenceUtils.setupSwitchPreferences(this, PreferenceConfigUtils.KEY_SOUND_DTS_TBHDX, PreferenceConfigUtils.KEY_SOUND_DTS_TBHDX);
        PreferenceUtils.setupSwitchPreferences(this, PreferenceConfigUtils.KEY_SOUND_DTS_LIMITER, PreferenceConfigUtils.KEY_SOUND_DTS_LIMITER);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        MtkLog.d("DTSFragment", "onPreferenceTreeClick :  key =" + preference.getKey());
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        }
        updatePrefEnabled();
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object selection) {
        return false;
    }

    private void updatePrefEnabled() {
        int vx = PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_SOUND_DTS_VIRTUAL_X);
        boolean z = false;
        findPreference(PreferenceConfigUtils.KEY_SOUND_DTS_TBHDX).setEnabled(vx != 0);
        Preference findPreference = findPreference(PreferenceConfigUtils.KEY_SOUND_DTS_LIMITER);
        if (vx != 0) {
            z = true;
        }
        findPreference.setEnabled(z);
    }

    public int getMetricsCategory() {
        return 336;
    }
}
