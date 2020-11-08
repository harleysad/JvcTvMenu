package com.android.tv.settings.device.apps;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.util.Pair;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.tv.settings.R;
import com.android.tv.settings.device.apps.AppActionPreference;
import com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails;

public class ForceStopPreference extends AppActionPreference {
    public ForceStopPreference(Context context, ApplicationsState.AppEntry entry) {
        super(context, entry);
        ConfirmationFragment.prepareArgs(getExtras(), this.mEntry.info.packageName);
        refresh();
    }

    public void refresh() {
        setTitle((int) R.string.device_apps_app_management_force_stop);
        if (((DevicePolicyManager) getContext().getSystemService("device_policy")).packageHasActiveAdmins(this.mEntry.info.packageName)) {
            setVisible(false);
        } else if ((this.mEntry.info.flags & 2097152) == 0) {
            setVisible(true);
        } else {
            Intent intent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts(DirectoryAccessDetails.ARG_PACKAGE_NAME, this.mEntry.info.packageName, (String) null));
            intent.putExtra("android.intent.extra.PACKAGES", new String[]{this.mEntry.info.packageName});
            intent.putExtra("android.intent.extra.UID", this.mEntry.info.uid);
            intent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mEntry.info.uid));
            getContext().sendOrderedBroadcast(intent, (String) null, new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    ForceStopPreference.this.setVisible(getResultCode() != 0);
                }
            }, (Handler) null, 0, (String) null, (Bundle) null);
        }
    }

    public String getFragment() {
        return ConfirmationFragment.class.getName();
    }

    public static class ConfirmationFragment extends AppActionPreference.ConfirmationFragment {
        private static final String ARG_PACKAGE_NAME = "packageName";
        private final MetricsFeatureProvider mMetricsFeatureProvider = new MetricsFeatureProvider();

        /* access modifiers changed from: private */
        public static void prepareArgs(@NonNull Bundle args, String packageName) {
            args.putString(ARG_PACKAGE_NAME, packageName);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            AppManagementFragment fragment = (AppManagementFragment) getTargetFragment();
            return new GuidanceStylist.Guidance(getString(R.string.device_apps_app_management_force_stop), getString(R.string.device_apps_app_management_force_stop_desc), fragment.getAppName(), fragment.getAppIcon());
        }

        public void onOk() {
            String pkgName = getArguments().getString(ARG_PACKAGE_NAME);
            this.mMetricsFeatureProvider.action(getContext(), 807, pkgName, new Pair[0]);
            ((ActivityManager) getActivity().getSystemService("activity")).forceStopPackage(pkgName);
        }
    }
}
