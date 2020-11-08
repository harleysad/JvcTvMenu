package com.android.tv.settings.partnercustomizer.picture;

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

public class ColorTuneHueFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String TAG = "";
    private PreferenceConfigUtils mPreferenceConfigUtils;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
    }

    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        createPreferences();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return false;
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_picture_hue);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_PICTURE_COLOR_TUNE_HUE);
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e(this.TAG, "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_RED)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_red, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_GREEN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_green, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_BLUE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_blue, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_CVAN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_cvan, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_MAGENTA)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_megenta, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_YELLOW)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_yellow, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE_FLESH_TONE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_flesh_tone, (String) null, 100, 0, 1));
            }
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
