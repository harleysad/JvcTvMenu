package com.android.tv.settings.device.apps;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.ArrayList;

public class AppManagementFragment extends SettingsPreferenceFragment {
    private static final String ARG_PACKAGE_NAME = "packageName";
    private static final String KEY_APP_STORAGE = "appStorage";
    private static final String KEY_CLEAR_CACHE = "clearCache";
    private static final String KEY_CLEAR_DATA = "clearData";
    private static final String KEY_CLEAR_DEFAULTS = "clearDefaults";
    private static final String KEY_ENABLE_DISABLE = "enableDisable";
    private static final String KEY_FORCE_STOP = "forceStop";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_OPEN = "open";
    private static final String KEY_PERMISSIONS = "permissions";
    private static final String KEY_UNINSTALL = "uninstall";
    private static final String KEY_VERSION = "version";
    private static final int REQUEST_MANAGE_SPACE = 2;
    private static final int REQUEST_UNINSTALL = 1;
    private static final int REQUEST_UNINSTALL_UPDATES = 3;
    private static final String TAG = "AppManagementFragment";
    /* access modifiers changed from: private */
    public AppStoragePreference mAppStoragePreference;
    /* access modifiers changed from: private */
    public ApplicationsState mApplicationsState;
    private Runnable mBailoutRunnable = new Runnable() {
        public final void run() {
            AppManagementFragment.lambda$new$0(AppManagementFragment.this);
        }
    };
    private final ApplicationsState.Callbacks mCallbacks = new ApplicationsStateCallbacks();
    /* access modifiers changed from: private */
    public ClearCachePreference mClearCachePreference;
    /* access modifiers changed from: private */
    public ClearDataPreference mClearDataPreference;
    private ClearDefaultsPreference mClearDefaultsPreference;
    private EnableDisablePreference mEnableDisablePreference;
    /* access modifiers changed from: private */
    public ApplicationsState.AppEntry mEntry;
    /* access modifiers changed from: private */
    public ForceStopPreference mForceStopPreference;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private NotificationsPreference mNotificationsPreference;
    private PackageManager mPackageManager;
    /* access modifiers changed from: private */
    public String mPackageName;
    private ApplicationsState.Session mSession;
    private UninstallPreference mUninstallPreference;

    public static /* synthetic */ void lambda$new$0(AppManagementFragment appManagementFragment) {
        if (appManagementFragment.isResumed() && !appManagementFragment.getFragmentManager().popBackStackImmediate()) {
            appManagementFragment.getActivity().onBackPressed();
        }
    }

    public static void prepareArgs(@NonNull Bundle args, String packageName) {
        args.putString(ARG_PACKAGE_NAME, packageName);
    }

    public int getMetricsCategory() {
        return 20;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mPackageName = getArguments().getString(ARG_PACKAGE_NAME);
        Activity activity = getActivity();
        this.mPackageManager = activity.getPackageManager();
        this.mApplicationsState = ApplicationsState.getInstance(activity.getApplication());
        this.mSession = this.mApplicationsState.newSession(this.mCallbacks, getLifecycle());
        this.mEntry = this.mApplicationsState.getEntry(this.mPackageName, UserHandle.myUserId());
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        if (this.mEntry == null) {
            Log.w(TAG, "App not found, trying to bail out");
            navigateBack();
        }
        if (this.mClearDefaultsPreference != null) {
            this.mClearDefaultsPreference.refresh();
        }
        if (this.mEnableDisablePreference != null) {
            this.mEnableDisablePreference.refresh();
        }
    }

    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(this.mBailoutRunnable);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mEntry != null) {
            int deleteResult = 0;
            switch (requestCode) {
                case 1:
                    if (data != null) {
                        deleteResult = data.getIntExtra("android.intent.extra.INSTALL_RESULT", 0);
                    }
                    if (deleteResult == 1) {
                        this.mApplicationsState.removePackage(this.mPackageName, UserHandle.getUserId(this.mEntry.info.uid));
                        navigateBack();
                        return;
                    }
                    Log.e(TAG, "Uninstall failed with result " + deleteResult);
                    return;
                case 2:
                    this.mClearDataPreference.setClearingData(false);
                    if (resultCode == -1) {
                        this.mApplicationsState.requestSize(this.mPackageName, UserHandle.getUserId(this.mEntry.info.uid));
                        return;
                    }
                    Log.w(TAG, "Failed to clear data!");
                    return;
                case 3:
                    this.mUninstallPreference.refresh();
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void navigateBack() {
        this.mHandler.removeCallbacks(this.mBailoutRunnable);
        this.mHandler.post(this.mBailoutRunnable);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        int i;
        Intent intent = preference.getIntent();
        if (intent == null) {
            return super.onPreferenceTreeClick(preference);
        }
        try {
            if (preference.equals(this.mUninstallPreference)) {
                this.mMetricsFeatureProvider.action(getContext(), 872, (Pair<Integer, Object>[]) new Pair[0]);
                if (this.mUninstallPreference.canUninstall()) {
                    i = 1;
                } else {
                    i = 3;
                }
                startActivityForResult(intent, i);
            } else {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Could not find activity to launch", e);
            Toast.makeText(getContext(), R.string.device_apps_app_management_not_available, 0).show();
        }
        return true;
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((CharSequence) getAppName());
        setPreferenceScreen(screen);
        updatePrefs();
    }

    /* access modifiers changed from: private */
    public void updatePrefs() {
        if (this.mEntry == null) {
            getPreferenceScreen().removeAll();
            return;
        }
        Context themedContext = getPreferenceManager().getContext();
        Preference versionPreference = findPreference(KEY_VERSION);
        if (versionPreference == null) {
            versionPreference = new Preference(themedContext);
            versionPreference.setKey(KEY_VERSION);
            replacePreference(versionPreference);
            versionPreference.setSelectable(false);
        }
        versionPreference.setTitle((CharSequence) getString(R.string.device_apps_app_management_version, new Object[]{this.mEntry.getVersion(getActivity())}));
        versionPreference.setSummary((CharSequence) this.mPackageName);
        Preference openPreference = findPreference(KEY_OPEN);
        if (openPreference == null) {
            openPreference = new Preference(themedContext);
            openPreference.setKey(KEY_OPEN);
            replacePreference(openPreference);
        }
        Intent appLaunchIntent = this.mPackageManager.getLeanbackLaunchIntentForPackage(this.mEntry.info.packageName);
        if (appLaunchIntent == null) {
            appLaunchIntent = this.mPackageManager.getLaunchIntentForPackage(this.mEntry.info.packageName);
        }
        if (appLaunchIntent != null) {
            openPreference.setIntent(appLaunchIntent);
            openPreference.setTitle((int) R.string.device_apps_app_management_open);
            openPreference.setVisible(true);
        } else {
            openPreference.setVisible(false);
        }
        if (this.mForceStopPreference == null) {
            this.mForceStopPreference = new ForceStopPreference(themedContext, this.mEntry);
            this.mForceStopPreference.setKey(KEY_FORCE_STOP);
            replacePreference(this.mForceStopPreference);
        } else {
            this.mForceStopPreference.setEntry(this.mEntry);
        }
        if (this.mUninstallPreference == null) {
            this.mUninstallPreference = new UninstallPreference(themedContext, this.mEntry);
            this.mUninstallPreference.setKey(KEY_UNINSTALL);
            replacePreference(this.mUninstallPreference);
        } else {
            this.mUninstallPreference.setEntry(this.mEntry);
        }
        if (this.mEnableDisablePreference == null) {
            this.mEnableDisablePreference = new EnableDisablePreference(themedContext, this.mEntry);
            this.mEnableDisablePreference.setKey(KEY_ENABLE_DISABLE);
            replacePreference(this.mEnableDisablePreference);
        } else {
            this.mEnableDisablePreference.setEntry(this.mEntry);
        }
        if (this.mAppStoragePreference == null) {
            this.mAppStoragePreference = new AppStoragePreference(themedContext, this.mEntry);
            this.mAppStoragePreference.setKey(KEY_APP_STORAGE);
            replacePreference(this.mAppStoragePreference);
        } else {
            this.mAppStoragePreference.setEntry(this.mEntry);
        }
        if (clearDataAllowed()) {
            if (this.mClearDataPreference == null) {
                this.mClearDataPreference = new ClearDataPreference(themedContext, this.mEntry);
                this.mClearDataPreference.setKey(KEY_CLEAR_DATA);
                replacePreference(this.mClearDataPreference);
            } else {
                this.mClearDataPreference.setEntry(this.mEntry);
            }
        }
        if (this.mClearCachePreference == null) {
            this.mClearCachePreference = new ClearCachePreference(themedContext, this.mEntry);
            this.mClearCachePreference.setKey(KEY_CLEAR_CACHE);
            replacePreference(this.mClearCachePreference);
        } else {
            this.mClearCachePreference.setEntry(this.mEntry);
        }
        if (this.mClearDefaultsPreference == null) {
            this.mClearDefaultsPreference = new ClearDefaultsPreference(themedContext, this.mEntry);
            this.mClearDefaultsPreference.setKey(KEY_CLEAR_DEFAULTS);
            replacePreference(this.mClearDefaultsPreference);
        } else {
            this.mClearDefaultsPreference.setEntry(this.mEntry);
        }
        if (this.mNotificationsPreference == null) {
            this.mNotificationsPreference = new NotificationsPreference(themedContext, this.mEntry);
            this.mNotificationsPreference.setKey(KEY_NOTIFICATIONS);
            replacePreference(this.mNotificationsPreference);
        } else {
            this.mNotificationsPreference.setEntry(this.mEntry);
        }
        Preference permissionsPreference = findPreference(KEY_PERMISSIONS);
        if (permissionsPreference == null) {
            permissionsPreference = new Preference(themedContext);
            permissionsPreference.setKey(KEY_PERMISSIONS);
            permissionsPreference.setTitle((int) R.string.device_apps_app_management_permissions);
            replacePreference(permissionsPreference);
        }
        permissionsPreference.setIntent(new Intent("android.intent.action.MANAGE_APP_PERMISSIONS").putExtra("android.intent.extra.PACKAGE_NAME", this.mPackageName));
    }

    private void replacePreference(Preference preference) {
        String key = preference.getKey();
        if (!TextUtils.isEmpty(key)) {
            Preference old = findPreference(key);
            if (old != null) {
                getPreferenceScreen().removePreference(old);
            }
            getPreferenceScreen().addPreference(preference);
            return;
        }
        throw new IllegalArgumentException("Can't replace a preference without a key");
    }

    public String getAppName() {
        if (this.mEntry == null) {
            return null;
        }
        this.mEntry.ensureLabel(getActivity());
        return this.mEntry.label;
    }

    public Drawable getAppIcon() {
        if (this.mEntry == null) {
            return null;
        }
        this.mApplicationsState.ensureIcon(this.mEntry);
        return this.mEntry.icon;
    }

    public void clearData() {
        if (!clearDataAllowed()) {
            Log.e(TAG, "Attempt to clear data failed. Clear data is disabled for " + this.mPackageName);
            return;
        }
        this.mMetricsFeatureProvider.action(getContext(), 876, (Pair<Integer, Object>[]) new Pair[0]);
        this.mClearDataPreference.setClearingData(true);
        String spaceManagementActivityName = this.mEntry.info.manageSpaceActivityName;
        if (spaceManagementActivityName == null) {
            this.mClearCachePreference.setClearingCache(true);
            if (!((ActivityManager) getActivity().getSystemService("activity")).clearApplicationUserData(this.mEntry.info.packageName, new IPackageDataObserver.Stub() {
                public void onRemoveCompleted(String packageName, final boolean succeeded) {
                    AppManagementFragment.this.mHandler.post(new Runnable() {
                        public void run() {
                            AppManagementFragment.this.mClearDataPreference.setClearingData(false);
                            AppManagementFragment.this.mClearCachePreference.setClearingCache(false);
                            if (succeeded) {
                                AppManagementFragment.this.dataCleared(true);
                            } else {
                                AppManagementFragment.this.dataCleared(false);
                            }
                        }
                    });
                }
            })) {
                this.mClearDataPreference.setClearingData(false);
                dataCleared(false);
            }
        } else if (!ActivityManager.isUserAMonkey()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setClassName(this.mEntry.info.packageName, spaceManagementActivityName);
            startActivityForResult(intent, 2);
        }
        this.mClearDataPreference.refresh();
    }

    /* access modifiers changed from: private */
    public void dataCleared(boolean succeeded) {
        if (succeeded) {
            this.mApplicationsState.requestSize(this.mPackageName, UserHandle.getUserId(this.mEntry.info.uid));
            return;
        }
        Log.w(TAG, "Failed to clear data!");
        this.mClearDataPreference.refresh();
    }

    public void clearCache() {
        this.mMetricsFeatureProvider.action(getContext(), 877, (Pair<Integer, Object>[]) new Pair[0]);
        this.mClearCachePreference.setClearingCache(true);
        this.mPackageManager.deleteApplicationCacheFiles(this.mEntry.info.packageName, new IPackageDataObserver.Stub() {
            public void onRemoveCompleted(String packageName, final boolean succeeded) {
                AppManagementFragment.this.mHandler.post(new Runnable() {
                    public void run() {
                        AppManagementFragment.this.mClearCachePreference.setClearingCache(false);
                        AppManagementFragment.this.cacheCleared(succeeded);
                    }
                });
            }
        });
        this.mClearCachePreference.refresh();
    }

    /* access modifiers changed from: private */
    public void cacheCleared(boolean succeeded) {
        if (succeeded) {
            this.mApplicationsState.requestSize(this.mPackageName, UserHandle.getUserId(this.mEntry.info.uid));
            return;
        }
        Log.w(TAG, "Failed to clear cache!");
        this.mClearCachePreference.refresh();
    }

    private boolean clearDataAllowed() {
        boolean sysApp = (this.mEntry.info.flags & 1) == 1;
        boolean allowClearData = (this.mEntry.info.flags & 64) == 64;
        if (!sysApp || allowClearData) {
            return true;
        }
        return false;
    }

    private class ApplicationsStateCallbacks implements ApplicationsState.Callbacks {
        private ApplicationsStateCallbacks() {
        }

        public void onRunningStateChanged(boolean running) {
            if (AppManagementFragment.this.mForceStopPreference != null) {
                AppManagementFragment.this.mForceStopPreference.refresh();
            }
        }

        public void onPackageListChanged() {
            if (AppManagementFragment.this.mEntry != null && AppManagementFragment.this.mEntry.info != null) {
                ApplicationsState.AppEntry unused = AppManagementFragment.this.mEntry = AppManagementFragment.this.mApplicationsState.getEntry(AppManagementFragment.this.mPackageName, UserHandle.getUserId(AppManagementFragment.this.mEntry.info.uid));
                if (AppManagementFragment.this.mEntry == null) {
                    AppManagementFragment.this.navigateBack();
                }
                AppManagementFragment.this.updatePrefs();
            }
        }

        public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        }

        public void onPackageIconChanged() {
        }

        public void onPackageSizeChanged(String packageName) {
            if (AppManagementFragment.this.mAppStoragePreference != null) {
                AppManagementFragment.this.mAppStoragePreference.refresh();
                AppManagementFragment.this.mClearCachePreference.refresh();
                if (AppManagementFragment.this.mClearDataPreference != null) {
                    AppManagementFragment.this.mClearDataPreference.refresh();
                }
            }
        }

        public void onAllSizesComputed() {
            if (AppManagementFragment.this.mAppStoragePreference != null) {
                AppManagementFragment.this.mAppStoragePreference.refresh();
                AppManagementFragment.this.mClearCachePreference.refresh();
                if (AppManagementFragment.this.mClearDataPreference != null) {
                    AppManagementFragment.this.mClearDataPreference.refresh();
                }
            }
        }

        public void onLauncherInfoChanged() {
            AppManagementFragment.this.updatePrefs();
        }

        public void onLoadEntriesCompleted() {
            ApplicationsState.AppEntry unused = AppManagementFragment.this.mEntry = AppManagementFragment.this.mApplicationsState.getEntry(AppManagementFragment.this.mPackageName, UserHandle.myUserId());
            AppManagementFragment.this.updatePrefs();
            if (AppManagementFragment.this.mAppStoragePreference != null) {
                AppManagementFragment.this.mAppStoragePreference.refresh();
                AppManagementFragment.this.mClearCachePreference.refresh();
                if (AppManagementFragment.this.mClearDataPreference != null) {
                    AppManagementFragment.this.mClearDataPreference.refresh();
                }
            }
        }
    }
}
