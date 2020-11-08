package com.android.tv.settings.device;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class StorageResetActivity extends TvSettingsActivity {
    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(StorageSummaryFragment.class.getName(), (Bundle) null);
    }
}
