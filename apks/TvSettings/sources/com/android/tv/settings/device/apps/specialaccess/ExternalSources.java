package com.android.tv.settings.device.apps.specialaccess;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
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
public class ExternalSources extends ManageAppOp {
    private AppOpsManager mAppOpsManager;

    public int getMetricsCategory() {
        return 808;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
        super.onCreate(savedInstanceState);
    }

    public int getAppOpsOpCode() {
        return 66;
    }

    public String getPermission() {
        return "android.permission.REQUEST_INSTALL_PACKAGES";
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.manage_external_sources, (String) null);
    }

    @NonNull
    public Preference createAppPreference() {
        return new SwitchPreference(getPreferenceManager().getContext());
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
                return ExternalSources.this.setCanInstallApps(this.f$1, ((Boolean) obj).booleanValue());
            }
        });
        switchPref.setChecked(((ManageAppOp.PermissionState) entry.extraInfo).isAllowed());
        switchPref.setSummary(getPreferenceSummary(entry));
        switchPref.setEnabled(canChange(entry));
        return switchPref;
    }

    private boolean canChange(ApplicationsState.AppEntry entry) {
        int userRestrictionSource = UserManager.get(getContext()).getUserRestrictionSource("no_install_unknown_sources", UserHandle.getUserHandleForUid(entry.info.uid));
        if (userRestrictionSource == 4) {
            return false;
        }
        switch (userRestrictionSource) {
            case 1:
            case 2:
                return false;
            default:
                return true;
        }
    }

    private CharSequence getPreferenceSummary(ApplicationsState.AppEntry entry) {
        int i;
        int userRestrictionSource = UserManager.get(getContext()).getUserRestrictionSource("no_install_unknown_sources", UserHandle.getUserHandleForUid(entry.info.uid));
        if (userRestrictionSource != 4) {
            switch (userRestrictionSource) {
                case 1:
                    return getContext().getString(R.string.disabled);
                case 2:
                    break;
                default:
                    Context context = getContext();
                    if (((ManageAppOp.PermissionState) entry.extraInfo).isAllowed()) {
                        i = R.string.external_source_trusted;
                    } else {
                        i = R.string.external_source_untrusted;
                    }
                    return context.getString(i);
            }
        }
        return getContext().getString(R.string.disabled_by_admin);
    }

    /* access modifiers changed from: private */
    public void setCanInstallApps(ApplicationsState.AppEntry entry, boolean newState) {
        this.mAppOpsManager.setMode(66, entry.info.uid, entry.info.packageName, newState ? 0 : 2);
        updateAppList();
    }

    @NonNull
    public PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }
}
