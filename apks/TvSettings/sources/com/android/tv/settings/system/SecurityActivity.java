package com.android.tv.settings.system;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class SecurityActivity extends TvSettingsActivity {
    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(SecurityFragment.class.getName(), (Bundle) null);
    }
}
