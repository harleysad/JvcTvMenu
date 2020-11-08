package com.android.tv.settings.inputmethod;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class InputMethodAndSubtypeEnablerActivity extends TvSettingsActivity {
    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(InputMethodAndSubtypeEnablerFragment.class.getName(), (Bundle) null);
    }
}
