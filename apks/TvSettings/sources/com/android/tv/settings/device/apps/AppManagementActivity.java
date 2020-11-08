package com.android.tv.settings.device.apps;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.TvSettingsActivity;

public class AppManagementActivity extends TvSettingsActivity {
    private static final String TAG = "AppManagementActivity";

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        Uri uri = getIntent().getData();
        if (uri != null) {
            return SettingsFragment.newInstance(uri.getSchemeSpecificPart());
        }
        Log.wtf(TAG, "No app to inspect (missing data uri in intent)");
        finish();
        return null;
    }

    public static class SettingsFragment extends BaseSettingsFragment {
        private static final String ARG_PACKAGE_NAME = "packageName";

        public static SettingsFragment newInstance(String packageName) {
            Bundle b = new Bundle(1);
            b.putString(ARG_PACKAGE_NAME, packageName);
            SettingsFragment f = new SettingsFragment();
            f.setArguments(b);
            return f;
        }

        public void onPreferenceStartInitialScreen() {
            Bundle b = new Bundle();
            AppManagementFragment.prepareArgs(b, getArguments().getString(ARG_PACKAGE_NAME));
            Fragment f = new AppManagementFragment();
            f.setArguments(b);
            startPreferenceFragment(f);
        }
    }
}
