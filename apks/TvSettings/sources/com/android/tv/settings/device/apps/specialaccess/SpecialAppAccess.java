package com.android.tv.settings.device.apps.specialaccess;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

@Keep
public class SpecialAppAccess extends SettingsPreferenceFragment {
    private static final String[] DISABLED_FEATURES_LOW_RAM_TV = {KEY_FEATURE_PIP};
    @VisibleForTesting
    static final String KEY_FEATURE_PIP = "picture_in_picture";

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.special_app_access, (String) null);
        updatePreferenceStates();
    }

    public int getMetricsCategory() {
        return 351;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updatePreferenceStates() {
        if (((ActivityManager) getContext().getSystemService("activity")).isLowRamDevice()) {
            for (String disabledFeature : DISABLED_FEATURES_LOW_RAM_TV) {
                removePreference(disabledFeature);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void removePreference(String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            getPreferenceScreen().removePreference(preference);
        }
    }
}
