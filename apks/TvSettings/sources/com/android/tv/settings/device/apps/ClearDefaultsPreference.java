package com.android.tv.settings.device.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.device.apps.AppActionPreference;

public class ClearDefaultsPreference extends AppActionPreference {
    private final PackageManager mPackageManager;
    private final IUsbManager mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));

    public ClearDefaultsPreference(Context context, ApplicationsState.AppEntry entry) {
        super(context, entry);
        this.mPackageManager = context.getPackageManager();
        refresh();
        ConfirmationFragment.prepareArgs(getExtras(), this.mEntry.info.packageName);
    }

    public void refresh() {
        setTitle((int) R.string.device_apps_app_management_clear_default);
        setSummary(AppUtils.getLaunchByDefaultSummary(this.mEntry, this.mUsbManager, this.mPackageManager, getContext()));
    }

    public String getFragment() {
        return ConfirmationFragment.class.getName();
    }

    public static class ConfirmationFragment extends AppActionPreference.ConfirmationFragment {
        private static final String ARG_PACKAGE_NAME = "packageName";

        /* access modifiers changed from: private */
        public static void prepareArgs(@NonNull Bundle args, String packageName) {
            args.putString(ARG_PACKAGE_NAME, packageName);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            AppManagementFragment fragment = (AppManagementFragment) getTargetFragment();
            return new GuidanceStylist.Guidance(getString(R.string.device_apps_app_management_clear_default), (String) null, fragment.getAppName(), fragment.getAppIcon());
        }

        public void onOk() {
            PackageManager packageManager = getActivity().getPackageManager();
            String packageName = getArguments().getString(ARG_PACKAGE_NAME);
            packageManager.clearPackagePreferredActivities(packageName);
            try {
                IUsbManager.Stub.asInterface(ServiceManager.getService("usb")).clearDefaults(packageName, UserHandle.myUserId());
            } catch (RemoteException e) {
            }
        }
    }
}
