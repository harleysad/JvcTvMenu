package com.android.tv.settings.partnercustomizer.sound.equalizerdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class EqualizerDetailFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private final String TAG = "EqualizerDetailFragment";
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
        MtkLog.d("EqualizerDetailFragment", "onPreferenceTreeClick :  key =" + preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        MtkLog.d("EqualizerDetailFragment", "onPreferenceChange : preferenceKey = " + preference.getKey() + "  newValue = " + newValue);
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return false;
    }

    private void initPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_sound_equalizer_detail);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("sound_equalizer_detail");
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e("EqualizerDetailFragment", "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_120HZ)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreferenceTrans(this, prefKey, "120", "g_fusion_sound__equalizer_120hz", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_500HZ)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreferenceTrans(this, prefKey, "500", "g_fusion_sound__equalizer_500hz", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_1500HZ)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreferenceTrans(this, prefKey, "1500", "g_fusion_sound__equalizer_1.5khz", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_5000HZ)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreferenceTrans(this, prefKey, "5000", "g_fusion_sound__equalizer_5khz", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_EQUALIZER_DETAIL_10000HZ)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreferenceTrans(this, prefKey, "10000", "g_fusion_sound__equalizer_10khz", 1));
                }
            }
        }
    }

    public int getMetricsCategory() {
        return 1300;
    }
}
