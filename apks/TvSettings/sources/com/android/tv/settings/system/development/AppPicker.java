package com.android.tv.settings.system.development;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.TextUtils;
import com.android.tv.settings.R;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

public class AppPicker extends Activity {
    public static final String EXTRA_DEBUGGABLE = "com.android.settings.extra.DEBUGGABLE";
    public static final String EXTRA_REQUESTIING_PERMISSION = "com.android.settings.extra.REQUESTIING_PERMISSION";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, AppPickerFragment.newInstance(getIntent().getStringExtra(EXTRA_REQUESTIING_PERMISSION), Boolean.valueOf(getIntent().getBooleanExtra(EXTRA_DEBUGGABLE, false)).booleanValue()), 16908290);
        }
    }

    public static class AppPickerFragment extends GuidedStepFragment {
        private boolean mDebuggableOnly;
        private String mPermissionName;

        public static AppPickerFragment newInstance(String permissionName, boolean debuggableOnly) {
            AppPickerFragment f = new AppPickerFragment();
            Bundle b = new Bundle(2);
            b.putString(AppPicker.EXTRA_REQUESTIING_PERMISSION, permissionName);
            b.putBoolean(AppPicker.EXTRA_DEBUGGABLE, debuggableOnly);
            f.setArguments(b);
            return f;
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mPermissionName = getArguments().getString(AppPicker.EXTRA_REQUESTIING_PERMISSION);
            this.mDebuggableOnly = getArguments().getBoolean(AppPicker.EXTRA_DEBUGGABLE);
            super.onCreate(savedInstanceState);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.choose_application), (String) null, (String) null, getContext().getDrawable(R.drawable.ic_adb_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            List<ApplicationInfo> pkgs = getActivity().getPackageManager().getInstalledApplications(0);
            PackageManager pm = getActivity().getPackageManager();
            for (ApplicationInfo ai : pkgs) {
                if (ai.uid != 1000 && (!this.mDebuggableOnly || (ai.flags & 2) != 0 || !"user".equals(Build.TYPE))) {
                    if (this.mPermissionName != null) {
                        boolean requestsPermission = false;
                        try {
                            PackageInfo pi = pm.getPackageInfo(ai.packageName, 4096);
                            if (pi.requestedPermissions != null) {
                                String[] strArr = pi.requestedPermissions;
                                int length = strArr.length;
                                int i = 0;
                                while (true) {
                                    if (i >= length) {
                                        break;
                                    } else if (strArr[i].equals(this.mPermissionName)) {
                                        requestsPermission = true;
                                        break;
                                    } else {
                                        i++;
                                    }
                                }
                                if (!requestsPermission) {
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    }
                    actions.add(new AppAction(ai.packageName, ai.loadLabel(pm).toString(), ai.loadIcon(pm)));
                }
            }
            actions.sort(new Comparator<GuidedAction>() {
                private final Collator mCollator = Collator.getInstance();

                public int compare(GuidedAction a, GuidedAction b) {
                    return this.mCollator.compare(a.getTitle(), b.getTitle());
                }
            });
            actions.add(0, new AppAction((String) null, getString(R.string.no_application), (Drawable) null));
        }

        public void onGuidedActionClicked(GuidedAction action) {
            Intent intent = new Intent();
            String packageName = ((AppAction) action).getPackageName();
            if (!TextUtils.isEmpty(packageName)) {
                intent.setAction(packageName);
            }
            getActivity().setResult(-1, intent);
            getActivity().finish();
        }

        private static class AppAction extends GuidedAction {
            private final String mPackageName;

            public AppAction(String packageName, String label, Drawable icon) {
                this.mPackageName = packageName;
                setTitle(label);
                setDescription(packageName);
                setIcon(icon);
                setEnabled(true);
                setFocusable(true);
            }

            public String getPackageName() {
                return this.mPackageName;
            }
        }
    }
}
