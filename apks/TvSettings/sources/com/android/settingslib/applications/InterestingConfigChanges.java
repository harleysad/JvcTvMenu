package com.android.settingslib.applications;

import android.content.res.Configuration;
import android.content.res.Resources;

public class InterestingConfigChanges {
    private final int mFlags;
    private final Configuration mLastConfiguration;
    private int mLastDensity;

    public InterestingConfigChanges() {
        this(-2147482876);
    }

    public InterestingConfigChanges(int flags) {
        this.mLastConfiguration = new Configuration();
        this.mFlags = flags;
    }

    public boolean applyNewConfig(Resources res) {
        int configChanges = this.mLastConfiguration.updateFrom(Configuration.generateDelta(this.mLastConfiguration, res.getConfiguration()));
        if (!(this.mLastDensity != res.getDisplayMetrics().densityDpi) && (this.mFlags & configChanges) == 0) {
            return false;
        }
        this.mLastDensity = res.getDisplayMetrics().densityDpi;
        return true;
    }
}
