package com.android.tv.settings.partnercustomizer.sound.dbx;

import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class DBXFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String TAG = "DBXFragment";
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
        String str = this.TAG;
        MtkLog.d(str, "onPreferenceTreeClick :  key =" + preference.getKey());
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return true;
    }

    private void initPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.sound_dbx);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("sound_dbx");
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e(this.TAG, "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DBX_ENABLE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.sound_dbx_enable, "g_audio__aud_out_port"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_SONIC)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.sound_dbx_total_sonic, "g_audio__aud_out_port"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_VOL)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.sound_dbx_total_vol, (int) R.array.sound_dbx_total_vol_entries, (int) R.array.sound_dbx_total_vol_entry_values, (String) null));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_SOUND_DBX_TOTAL_SURROUND)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.sound_dbx_total_surround, "g_audio__aud_out_port"));
            }
        }
    }

    public int getMetricsCategory() {
        return 1300;
    }
}
