package com.android.tv.settings.partnercustomizer.picture;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class ColorTuneFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String TAG = "ColorTuneFragment";
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
        updatePrefEnable(((SwitchPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_ENABLE)).isChecked());
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference pref = (SwitchPreference) preference;
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(pref.isChecked()));
            if (TextUtils.equals(preference.getKey(), PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_ENABLE)) {
                updatePrefEnable(pref.isChecked());
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return true;
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_picture_color_tune);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("picture_color_tune");
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e(this.TAG, "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_ENABLE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_color_tune_enable, (String) null));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_hue, "com.android.tv.settings.partnercustomizer.picture.ColorTuneHueFragment"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_saturation, "com.android.tv.settings.partnercustomizer.picture.ColorTuneSaturationFragment"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_brightness, "com.android.tv.settings.partnercustomizer.picture.ColorTuneBrightnessFragment"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_color_tune_offset, "com.android.tv.settings.partnercustomizer.picture.ColorTuneOffsetFragment"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_color_tune_gain, "com.android.tv.settings.partnercustomizer.picture.ColorTuneGainFragment"));
            }
        }
    }

    private void updatePrefEnable(boolean enable) {
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_HUE).setEnabled(enable);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_SATURATION).setEnabled(enable);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_BRIGHTNESS).setEnabled(enable);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_OFFSET).setEnabled(enable);
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_COLOR_TUNE_GAIN).setEnabled(enable);
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
