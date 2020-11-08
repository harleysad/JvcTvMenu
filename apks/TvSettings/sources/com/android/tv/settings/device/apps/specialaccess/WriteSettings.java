package com.android.tv.settings.device.apps.specialaccess;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.device.apps.specialaccess.ManageAppOp;

@Keep
public class WriteSettings extends ManageAppOp {
    private AppOpsManager mAppOpsManager;

    public int getMetricsCategory() {
        return 221;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
    }

    public int getAppOpsOpCode() {
        return 23;
    }

    public String getPermission() {
        return "android.permission.WRITE_SETTINGS";
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.write_settings, (String) null);
    }

    @NonNull
    public Preference bindPreference(@NonNull Preference preference, ApplicationsState.AppEntry entry) {
        TwoStatePreference switchPref = (SwitchPreference) preference;
        switchPref.setTitle((CharSequence) entry.label);
        switchPref.setKey(entry.info.packageName);
        switchPref.setIcon(entry.icon);
        switchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(entry) {
            private final /* synthetic */ ApplicationsState.AppEntry f$1;

            {
                this.f$1 = r2;
            }

            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return WriteSettings.this.setWriteSettingsAccess(this.f$1, (Boolean) obj);
            }
        });
        switchPref.setSummary(getPreferenceSummary(entry));
        switchPref.setChecked(((ManageAppOp.PermissionState) entry.extraInfo).isAllowed());
        return switchPref;
    }

    /* access modifiers changed from: private */
    public void setWriteSettingsAccess(ApplicationsState.AppEntry entry, Boolean grant) {
        this.mAppOpsManager.setMode(23, entry.info.uid, entry.info.packageName, grant.booleanValue() ? 0 : 2);
    }

    @NonNull
    public Preference createAppPreference() {
        return new SwitchPreference(getPreferenceManager().getContext());
    }

    @NonNull
    public PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }

    private CharSequence getPreferenceSummary(ApplicationsState.AppEntry entry) {
        int i;
        if (!(entry.extraInfo instanceof ManageAppOp.PermissionState)) {
            return null;
        }
        Context context = getContext();
        if (((ManageAppOp.PermissionState) entry.extraInfo).isAllowed()) {
            i = R.string.write_settings_on;
        } else {
            i = R.string.write_settings_off;
        }
        return context.getText(i);
    }
}
