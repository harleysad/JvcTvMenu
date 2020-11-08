package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import com.android.settingslib.RestrictedLockUtils;

/* renamed from: com.android.settingslib.-$$Lambda$RestrictedLockUtils$ZGpdJ-Goya42TrXyPazgpDXw5os  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RestrictedLockUtils$ZGpdJGoya42TrXyPazgpDXw5os implements RestrictedLockUtils.LockSettingCheck {
    public static final /* synthetic */ $$Lambda$RestrictedLockUtils$ZGpdJGoya42TrXyPazgpDXw5os INSTANCE = new $$Lambda$RestrictedLockUtils$ZGpdJGoya42TrXyPazgpDXw5os();

    private /* synthetic */ $$Lambda$RestrictedLockUtils$ZGpdJGoya42TrXyPazgpDXw5os() {
    }

    public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        return RestrictedLockUtils.lambda$checkIfPasswordQualityIsSet$1(devicePolicyManager, componentName, i);
    }
}
