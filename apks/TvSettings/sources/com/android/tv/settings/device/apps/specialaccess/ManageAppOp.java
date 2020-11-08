package com.android.tv.settings.device.apps.specialaccess;

import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.SliceBroadcastRelay;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.apps.specialaccess.ManageApplicationsController;
import java.util.Comparator;

public abstract class ManageAppOp extends SettingsPreferenceFragment implements ManageApplicationsController.Callback {
    private static final String TAG = "ManageAppOps";
    private AppOpsManager mAppOpsManager;
    private IPackageManager mIPackageManager;
    private ManageApplicationsController mManageApplicationsController;

    public abstract int getAppOpsOpCode();

    public abstract String getPermission();

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mManageApplicationsController = new ManageApplicationsController(context, this, getLifecycle(), getAppFilter(), getAppComparator());
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mIPackageManager = ActivityThread.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public ApplicationsState.AppFilter getAppFilter() {
        return new ApplicationsState.AppFilter() {
            public void init() {
            }

            public boolean filterApp(ApplicationsState.AppEntry entry) {
                entry.extraInfo = ManageAppOp.this.createPermissionStateFor(entry.info.packageName, entry.info.uid);
                return !ManageAppOp.shouldIgnorePackage(ManageAppOp.this.getContext(), entry.info.packageName) && ((PermissionState) entry.extraInfo).isPermissible();
            }
        };
    }

    @Nullable
    public Comparator<ApplicationsState.AppEntry> getAppComparator() {
        return ApplicationsState.ALPHA_COMPARATOR;
    }

    public void updateAppList() {
        this.mManageApplicationsController.updateAppList();
    }

    private boolean hasRequestedAppOpPermission(String permission, String packageName) {
        try {
            return ArrayUtils.contains(this.mIPackageManager.getAppOpPermissionPackages(permission), packageName);
        } catch (RemoteException e) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    private boolean hasPermission(int uid) {
        try {
            if (this.mIPackageManager.checkUidPermission(getPermission(), uid) == 0) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    private int getAppOpMode(int uid, String packageName) {
        return this.mAppOpsManager.checkOpNoThrow(getAppOpsOpCode(), uid, packageName);
    }

    /* access modifiers changed from: private */
    public PermissionState createPermissionStateFor(String packageName, int uid) {
        return new PermissionState(hasRequestedAppOpPermission(getPermission(), packageName), hasPermission(uid), getAppOpMode(uid, packageName));
    }

    static boolean shouldIgnorePackage(Context context, String packageName) {
        return context == null || packageName.equals("android") || packageName.equals(SliceBroadcastRelay.SYSTEMUI_PACKAGE) || packageName.equals(context.getPackageName());
    }

    public static class PermissionState {
        public final int appOpMode;
        public final boolean permissionGranted;
        public final boolean permissionRequested;

        private PermissionState(boolean permissionRequested2, boolean permissionGranted2, int appOpMode2) {
            this.permissionRequested = permissionRequested2;
            this.permissionGranted = permissionGranted2;
            this.appOpMode = appOpMode2;
        }

        public boolean isAllowed() {
            if (this.appOpMode == 3) {
                return this.permissionGranted;
            }
            return this.appOpMode == 0;
        }

        public boolean isPermissible() {
            return this.appOpMode != 3 || this.permissionRequested;
        }

        public String toString() {
            return "[permissionGranted: " + this.permissionGranted + ", permissionRequested: " + this.permissionRequested + ", appOpMode: " + this.appOpMode + "]";
        }
    }

    @NonNull
    public Preference getEmptyPreference() {
        Preference empty = new Preference(getPreferenceManager().getContext());
        empty.setKey("empty");
        empty.setTitle((int) R.string.noApplications);
        empty.setEnabled(false);
        return empty;
    }
}
