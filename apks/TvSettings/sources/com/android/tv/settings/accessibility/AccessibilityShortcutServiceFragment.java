package com.android.tv.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.accessibility.AccessibilityServiceConfirmationFragment;
import com.android.tv.twopanelsettings.TwoPanelSettingsFragment;
import java.util.List;

@Keep
public class AccessibilityShortcutServiceFragment extends SettingsPreferenceFragment implements AccessibilityServiceConfirmationFragment.OnAccessibilityServiceConfirmedListener {
    private static final String SERVICE_RADIO_GROUP = "service_group";
    private final Preference.OnPreferenceChangeListener mPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        public final boolean onPreferenceChange(Preference preference, Object obj) {
            return AccessibilityShortcutServiceFragment.lambda$new$0(AccessibilityShortcutServiceFragment.this, preference, obj);
        }
    };

    public static /* synthetic */ boolean lambda$new$0(AccessibilityShortcutServiceFragment accessibilityShortcutServiceFragment, Preference preference, Object newValue) {
        String newCompString = preference.getKey();
        String currentService = AccessibilityShortcutFragment.getCurrentService(accessibilityShortcutServiceFragment.getContext());
        if (((Boolean) newValue).booleanValue() && !TextUtils.equals(newCompString, currentService)) {
            Fragment confirmFragment = AccessibilityServiceConfirmationFragment.newInstance(ComponentName.unflattenFromString(newCompString), preference.getTitle(), true);
            confirmFragment.setTargetFragment(accessibilityShortcutServiceFragment, 0);
            Fragment settingsFragment = accessibilityShortcutServiceFragment.getCallbackFragment();
            if (settingsFragment instanceof LeanbackSettingsFragment) {
                ((LeanbackSettingsFragment) settingsFragment).startImmersiveFragment(confirmFragment);
            } else if (settingsFragment instanceof TwoPanelSettingsFragment) {
                ((TwoPanelSettingsFragment) settingsFragment).startImmersiveFragment(confirmFragment);
            } else {
                throw new IllegalStateException("Not attached to settings fragment??");
            }
        }
        return false;
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.accessibility_shortcut_service, (String) null);
        PreferenceScreen screen = getPreferenceScreen();
        Context themedContext = getPreferenceManager().getContext();
        List<AccessibilityServiceInfo> installedServices = ((AccessibilityManager) getContext().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList();
        PackageManager packageManager = getContext().getPackageManager();
        String currentService = AccessibilityShortcutFragment.getCurrentService(getContext());
        for (AccessibilityServiceInfo service : installedServices) {
            RadioPreference preference = new RadioPreference(themedContext);
            preference.setPersistent(false);
            preference.setRadioGroup(SERVICE_RADIO_GROUP);
            preference.setOnPreferenceChangeListener(this.mPreferenceChangeListener);
            String serviceString = service.getComponentName().flattenToString();
            if (TextUtils.equals(currentService, serviceString)) {
                preference.setChecked(true);
            }
            preference.setKey(serviceString);
            preference.setTitle(service.getResolveInfo().loadLabel(packageManager));
            screen.addPreference(preference);
        }
    }

    public void onAccessibilityServiceConfirmed(ComponentName componentName, boolean enabling) {
        Settings.Secure.putString(getContext().getContentResolver(), "accessibility_shortcut_target_service", componentName.flattenToString());
        getFragmentManager().popBackStack();
    }

    public int getMetricsCategory() {
        return 4;
    }
}
