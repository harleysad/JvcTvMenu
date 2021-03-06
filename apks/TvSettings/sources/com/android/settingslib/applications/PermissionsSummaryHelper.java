package com.android.settingslib.applications;

import android.content.Context;
import android.content.pm.permission.RuntimePermissionPresentationInfo;
import android.content.pm.permission.RuntimePermissionPresenter;
import android.os.Handler;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionsSummaryHelper {
    public static void getPermissionSummary(Context context, String pkg, final PermissionsResultCallback callback) {
        RuntimePermissionPresenter.getInstance(context).getAppPermissions(pkg, new RuntimePermissionPresenter.OnResultCallback() {
            public void onGetAppPermissions(List<RuntimePermissionPresentationInfo> permissions) {
                int permissionCount = permissions.size();
                int requestedCount = 0;
                List<CharSequence> grantedStandardLabels = new ArrayList<>();
                int grantedAdditionalCount = 0;
                int grantedStandardCount = 0;
                for (int i = 0; i < permissionCount; i++) {
                    RuntimePermissionPresentationInfo permission = permissions.get(i);
                    requestedCount++;
                    if (permission.isGranted()) {
                        if (permission.isStandard()) {
                            grantedStandardLabels.add(permission.getLabel());
                            grantedStandardCount++;
                        } else {
                            grantedAdditionalCount++;
                        }
                    }
                }
                Collator collator = Collator.getInstance();
                collator.setStrength(0);
                Collections.sort(grantedStandardLabels, collator);
                PermissionsResultCallback.this.onPermissionSummaryResult(grantedStandardCount, requestedCount, grantedAdditionalCount, grantedStandardLabels);
            }
        }, (Handler) null);
    }

    public static abstract class PermissionsResultCallback {
        public void onAppWithPermissionsCountsResult(int standardGrantedPermissionAppCount, int standardUsedPermissionAppCount) {
        }

        public void onPermissionSummaryResult(int standardGrantedPermissionCount, int requestedPermissionCount, int additionalGrantedPermissionCount, List<CharSequence> list) {
        }
    }
}
