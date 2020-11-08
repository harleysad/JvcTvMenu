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
import com.android.tv.settings.device.apps.specialaccess.ManageApplicationsController;

@Keep
public class AppUsageAccess extends ManageAppOp implements ManageApplicationsController.Callback {
    private AppOpsManager mAppOpsManager;

    public int getMetricsCategory() {
        return 95;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
    }

    public int getAppOpsOpCode() {
        return 43;
    }

    public String getPermission() {
        return "android.permission.PACKAGE_USAGE_STATS";
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
                return AppUsageAccess.this.setAppUsageAccess(this.f$1, ((Boolean) obj).booleanValue());
            }
        });
        switchPref.setSummary(getPreferenceSummary(entry));
        switchPref.setChecked(((ManageAppOp.PermissionState) entry.extraInfo).isAllowed());
        return switchPref;
    }

    @NonNull
    public Preference createAppPreference() {
        return new SwitchPreference(getPreferenceManager().getContext());
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_usage_access, (String) null);
    }

    private CharSequence getPreferenceSummary(ApplicationsState.AppEntry entry) {
        int i;
        if (!(entry.extraInfo instanceof ManageAppOp.PermissionState)) {
            return null;
        }
        Context context = getContext();
        if (((ManageAppOp.PermissionState) entry.extraInfo).isAllowed()) {
            i = R.string.app_permission_summary_allowed;
        } else {
            i = R.string.app_permission_summary_not_allowed;
        }
        return context.getText(i);
    }

    /* access modifiers changed from: private */
    public void setAppUsageAccess(ApplicationsState.AppEntry entry, boolean grant) {
        this.mAppOpsManager.setMode(43, entry.info.uid, entry.info.packageName, grant ^ true ? 1 : 0);
        updateAppList();
    }

    @NonNull
    public PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }
}
