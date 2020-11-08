package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import com.android.settingslib.RestrictedLockUtils;

/* renamed from: com.android.settingslib.-$$Lambda$RestrictedLockUtils$sbYwAwFLTMW969YNG1W7ojc-r04  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RestrictedLockUtils$sbYwAwFLTMW969YNG1W7ojcr04 implements RestrictedLockUtils.LockSettingCheck {
    public static final /* synthetic */ $$Lambda$RestrictedLockUtils$sbYwAwFLTMW969YNG1W7ojcr04 INSTANCE = new $$Lambda$RestrictedLockUtils$sbYwAwFLTMW969YNG1W7ojcr04();

    private /* synthetic */ $$Lambda$RestrictedLockUtils$sbYwAwFLTMW969YNG1W7ojcr04() {
    }

    public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        return RestrictedLockUtils.lambda$checkIfMaximumTimeToLockIsSet$2(devicePolicyManager, componentName, i);
    }
}
