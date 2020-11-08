package com.android.tv.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.List;

@Keep
public class AccessibilityShortcutFragment extends SettingsPreferenceFragment {
    private static final String KEY_ENABLE = "enable";
    private static final String KEY_SERVICE = "service";

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.accessibility_shortcut, (String) null);
        TwoStatePreference enablePref = (TwoStatePreference) findPreference(KEY_ENABLE);
        enablePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return AccessibilityShortcutFragment.this.setAccessibilityShortcutEnabled(((Boolean) obj).booleanValue());
            }
        });
        boolean shortcutEnabled = true;
        if (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_shortcut_enabled", 1) != 1) {
            shortcutEnabled = false;
        }
        enablePref.setChecked(shortcutEnabled);
    }

    public void onResume() {
        super.onResume();
        Preference servicePref = findPreference("service");
        List<AccessibilityServiceInfo> installedServices = ((AccessibilityManager) getContext().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList();
        PackageManager packageManager = getContext().getPackageManager();
        String currentService = getCurrentService(getContext());
        for (AccessibilityServiceInfo service : installedServices) {
            if (TextUtils.equals(currentService, service.getComponentName().flattenToString())) {
                servicePref.setSummary(service.getResolveInfo().loadLabel(packageManager));
            }
        }
    }

    /* access modifiers changed from: private */
    public void setAccessibilityShortcutEnabled(boolean enabled) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_shortcut_enabled", enabled);
        findPreference("service").setEnabled(enabled);
    }

    static String getCurrentService(Context context) {
        ComponentName shortcutName;
        String shortcutServiceString = AccessibilityUtils.getShortcutTargetServiceComponentNameString(context, UserHandle.myUserId());
        if (shortcutServiceString == null || (shortcutName = ComponentName.unflattenFromString(shortcutServiceString)) == null) {
            return null;
        }
        return shortcutName.flattenToString();
    }

    public int getMetricsCategory() {
        return 6;
    }
}
