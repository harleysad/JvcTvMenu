package com.android.tv.settings.device.apps.specialaccess;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;
import com.android.tv.settings.system.SecurityFragment;

public class ExternalSourcesActivity extends TvSettingsActivity {
    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        if (!SecurityFragment.isRestrictedProfileInEffect(this)) {
            return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(ExternalSources.class.getName(), (Bundle) null);
        }
        finish();
        return null;
    }
}
