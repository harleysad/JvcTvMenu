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

public class ColorTuneBrightnessFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String TAG = "ColorTuneBrightnessFragment";
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
        mScreen.setTitle((int) R.string.device_picture_brightness);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_PICTURE_COLOR_TUNE_BRIGHTNESS);
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e(this.TAG, "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_RED)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_red, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_GREEN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_green, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_BLUE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_blue, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_CVAN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_cvan, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_MEGENTA)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_megenta, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_YELLOW)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_yellow, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS_FLESH_TONE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_flesh_tone, (String) null, 100, 0, 1));
            }
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
