package com.android.tv.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.accessibility.AccessibilityServiceConfirmationFragment;

public class AccessibilityServiceFragment extends SettingsPreferenceFragment implements AccessibilityServiceConfirmationFragment.OnAccessibilityServiceConfirmedListener {
    private static final String ARG_LABEL = "label";
    private static final String ARG_PACKAGE_NAME = "packageName";
    private static final String ARG_SERVICE_NAME = "serviceName";
    private static final String ARG_SETTINGS_ACTIVITY_NAME = "settingsActivityName";
    private TwoStatePreference mEnablePref;

    public static void prepareArgs(@NonNull Bundle args, String packageName, String serviceName, String activityName, String label) {
        args.putString(ARG_PACKAGE_NAME, packageName);
        args.putString(ARG_SERVICE_NAME, serviceName);
        args.putString(ARG_SETTINGS_ACTIVITY_NAME, activityName);
        args.putString(ARG_LABEL, label);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(themedContext);
        screen.setTitle((CharSequence) getArguments().getString(ARG_LABEL));
        this.mEnablePref = new SwitchPreference(themedContext);
        this.mEnablePref.setTitle((int) R.string.system_accessibility_status);
        this.mEnablePref.setFragment(AccessibilityServiceConfirmationFragment.class.getName());
        screen.addPreference(this.mEnablePref);
        Preference settingsPref = new Preference(themedContext);
        settingsPref.setTitle((int) R.string.system_accessibility_config);
        String activityName = getArguments().getString(ARG_SETTINGS_ACTIVITY_NAME);
        if (!TextUtils.isEmpty(activityName)) {
            settingsPref.setIntent(new Intent("android.intent.action.MAIN").setComponent(new ComponentName(getArguments().getString(ARG_PACKAGE_NAME), activityName)));
        } else {
            settingsPref.setEnabled(false);
        }
        screen.addPreference(settingsPref);
        setPreferenceScreen(screen);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference != this.mEnablePref) {
            return super.onPreferenceTreeClick(preference);
        }
        updateEnablePref();
        super.onPreferenceTreeClick(preference);
        return true;
    }

    private void updateEnablePref() {
        String packageName = getArguments().getString(ARG_PACKAGE_NAME);
        String serviceName = getArguments().getString(ARG_SERVICE_NAME);
        boolean enabled = AccessibilityUtils.getEnabledServicesFromSettings(getActivity()).contains(new ComponentName(packageName, serviceName));
        this.mEnablePref.setChecked(enabled);
        AccessibilityServiceConfirmationFragment.prepareArgs(this.mEnablePref.getExtras(), new ComponentName(packageName, serviceName), getArguments().getString(ARG_LABEL), !enabled);
    }

    public void onResume() {
        super.onResume();
        updateEnablePref();
    }

    public void onAccessibilityServiceConfirmed(ComponentName componentName, boolean enabling) {
        AccessibilityUtils.setAccessibilityServiceState(getActivity(), componentName, enabling);
        if (this.mEnablePref != null) {
            this.mEnablePref.setChecked(enabling);
        }
    }

    public int getMetricsCategory() {
        return 4;
    }
}
