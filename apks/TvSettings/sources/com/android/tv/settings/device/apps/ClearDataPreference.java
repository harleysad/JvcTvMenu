package com.android.tv.settings.device.apps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.text.format.Formatter;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.device.apps.AppActionPreference;

public class ClearDataPreference extends AppActionPreference {
    private boolean mClearingData;

    public ClearDataPreference(Context context, ApplicationsState.AppEntry entry) {
        super(context, entry);
        refresh();
        ConfirmationFragment.prepareArgs(getExtras(), this.mEntry.info.packageName);
    }

    public void refresh() {
        String str;
        setTitle((int) R.string.device_apps_app_management_clear_data);
        Context context = getContext();
        if (this.mClearingData) {
            str = context.getString(R.string.computing_size);
        } else {
            str = Formatter.formatFileSize(context, this.mEntry.dataSize + this.mEntry.externalDataSize);
        }
        setSummary((CharSequence) str);
    }

    public void setClearingData(boolean clearingData) {
        this.mClearingData = clearingData;
        refresh();
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
            return new GuidanceStylist.Guidance(getString(R.string.device_apps_app_management_clear_data), getString(R.string.device_apps_app_management_clear_data_desc), fragment.getAppName(), fragment.getAppIcon());
        }

        public void onOk() {
            ((AppManagementFragment) getTargetFragment()).clearData();
        }
    }
}
