package com.android.tv.settings.device.apps;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.util.Pair;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.tv.settings.R;
import com.android.tv.settings.device.apps.AppActionPreference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EnableDisablePreference extends AppActionPreference {
    private final PackageManager mPackageManager;

    public EnableDisablePreference(Context context, ApplicationsState.AppEntry entry) {
        super(context, entry);
        this.mPackageManager = context.getPackageManager();
        refresh();
    }

    public void refresh() {
        if (UninstallPreference.canUninstall(this.mEntry) || !canDisable()) {
            setVisible(false);
            return;
        }
        setVisible(true);
        if (this.mEntry.info.enabled) {
            setTitle((int) R.string.device_apps_app_management_disable);
            ConfirmationFragment.prepareArgs(getExtras(), this.mEntry.info.packageName, false);
            return;
        }
        setTitle((int) R.string.device_apps_app_management_enable);
        ConfirmationFragment.prepareArgs(getExtras(), this.mEntry.info.packageName, true);
    }

    private boolean canDisable() {
        HashSet<String> homePackages = getHomePackages();
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(this.mEntry.info.packageName, 8768);
            if (homePackages.contains(this.mEntry.info.packageName) || Utils.isSystemPackage(getContext().getResources(), this.mPackageManager, packageInfo)) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private HashSet<String> getHomePackages() {
        HashSet<String> homePackages = new HashSet<>();
        List<ResolveInfo> homeActivities = new ArrayList<>();
        this.mPackageManager.getHomeActivities(homeActivities);
        for (ResolveInfo ri : homeActivities) {
            String activityPkg = ri.activityInfo.packageName;
            homePackages.add(activityPkg);
            Bundle metadata = ri.activityInfo.metaData;
            if (metadata != null) {
                String metaPkg = metadata.getString("android.app.home.alternate");
                if (signaturesMatch(this.mPackageManager, metaPkg, activityPkg)) {
                    homePackages.add(metaPkg);
                }
            }
        }
        return homePackages;
    }

    private static boolean signaturesMatch(PackageManager pm, String pkg1, String pkg2) {
        if (pkg1 == null || pkg2 == null) {
            return false;
        }
        try {
            if (pm.checkSignatures(pkg1, pkg2) >= 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getFragment() {
        return ConfirmationFragment.class.getName();
    }

    public static class ConfirmationFragment extends AppActionPreference.ConfirmationFragment {
        private static final String ARG_ENABLE = "enable";
        private static final String ARG_PACKAGE_NAME = "packageName";
        private final MetricsFeatureProvider mMetricsFeatureProvider = new MetricsFeatureProvider();

        /* access modifiers changed from: private */
        public static void prepareArgs(@NonNull Bundle args, String packageName, boolean enable) {
            args.putString(ARG_PACKAGE_NAME, packageName);
            args.putBoolean(ARG_ENABLE, enable);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            int i;
            int i2;
            AppManagementFragment fragment = (AppManagementFragment) getTargetFragment();
            Boolean enable = Boolean.valueOf(getArguments().getBoolean(ARG_ENABLE));
            if (enable.booleanValue()) {
                i = R.string.device_apps_app_management_enable;
            } else {
                i = R.string.device_apps_app_management_disable;
            }
            String string = getString(i);
            if (enable.booleanValue()) {
                i2 = R.string.device_apps_app_management_enable_desc;
            } else {
                i2 = R.string.device_apps_app_management_disable_desc;
            }
            return new GuidanceStylist.Guidance(string, getString(i2), fragment.getAppName(), fragment.getAppIcon());
        }

        public void onOk() {
            int i;
            boolean enable = getArguments().getBoolean(ARG_ENABLE);
            MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
            Context context = getContext();
            if (enable) {
                i = 875;
            } else {
                i = 874;
            }
            metricsFeatureProvider.action(context, i, (Pair<Integer, Object>[]) new Pair[0]);
            getActivity().getPackageManager().setApplicationEnabledSetting(getArguments().getString(ARG_PACKAGE_NAME), enable ? 0 : 3, 0);
        }
    }
}
