package com.android.tv.settings.device.apps.specialaccess;

import android.app.AppOpsManager;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import android.util.Log;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.apps.specialaccess.ManageApplicationsController;

@Keep
public class PictureInPicture extends SettingsPreferenceFragment implements ManageApplicationsController.Callback {
    private static final String TAG = "PictureInPicture";
    /* access modifiers changed from: private */
    public AppOpsManager mAppOpsManager;
    private final ApplicationsState.AppFilter mFilter = new ApplicationsState.CompoundFilter(new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, ApplicationsState.FILTER_ALL_ENABLED), new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry info) {
            info.extraInfo = Boolean.valueOf(PictureInPicture.this.mAppOpsManager.checkOpNoThrow(67, info.info.uid, info.info.packageName) == 0);
            if (ManageAppOp.shouldIgnorePackage(PictureInPicture.this.getContext(), info.info.packageName) || !PictureInPicture.this.checkPackageHasPipActivities(info.info.packageName)) {
                return false;
            }
            return true;
        }
    });
    private ManageApplicationsController mManageApplicationsController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
        this.mManageApplicationsController = new ManageApplicationsController(getContext(), this, getLifecycle(), this.mFilter, ApplicationsState.ALPHA_COMPARATOR);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.picture_in_picture, (String) null);
    }

    public void onResume() {
        super.onResume();
        this.mManageApplicationsController.updateAppList();
    }

    /* access modifiers changed from: private */
    public boolean checkPackageHasPipActivities(String packageName) {
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(packageName, 1);
            if (packageInfo.activities == null) {
                return false;
            }
            for (ActivityInfo info : packageInfo.activities) {
                if (info.supportsPictureInPicture()) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Exception while fetching package info for " + packageName, e);
            return false;
        }
    }

    @NonNull
    public Preference bindPreference(@NonNull Preference preference, ApplicationsState.AppEntry entry) {
        int i;
        TwoStatePreference switchPref = (SwitchPreference) preference;
        switchPref.setTitle((CharSequence) entry.label);
        switchPref.setKey(entry.info.packageName);
        switchPref.setIcon(entry.icon);
        switchPref.setChecked(((Boolean) entry.extraInfo).booleanValue());
        switchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(entry) {
            private final /* synthetic */ ApplicationsState.AppEntry f$1;

            {
                this.f$1 = r2;
            }

            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return PictureInPicture.lambda$bindPreference$0(PictureInPicture.this, this.f$1, preference, obj);
            }
        });
        if (((Boolean) entry.extraInfo).booleanValue()) {
            i = R.string.app_permission_summary_allowed;
        } else {
            i = R.string.app_permission_summary_not_allowed;
        }
        switchPref.setSummary(i);
        return switchPref;
    }

    public static /* synthetic */ boolean lambda$bindPreference$0(PictureInPicture pictureInPicture, ApplicationsState.AppEntry entry, Preference pref, Object newValue) {
        pictureInPicture.mAppOpsManager.setMode(67, entry.info.uid, entry.info.packageName, ((Boolean) newValue).booleanValue() ? 0 : 2);
        return true;
    }

    @NonNull
    public Preference createAppPreference() {
        return new SwitchPreference(getPreferenceManager().getContext());
    }

    @NonNull
    public Preference getEmptyPreference() {
        Preference empty = new Preference(getPreferenceManager().getContext());
        empty.setKey("empty");
        empty.setTitle((int) R.string.picture_in_picture_empty_text);
        empty.setEnabled(false);
        return empty;
    }

    @NonNull
    public PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }

    public int getMetricsCategory() {
        return 812;
    }
}
