package com.android.tv.settings.device.sound;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class SoundActivity extends TvSettingsActivity {
    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(SoundFragment.class.getName(), (Bundle) null);
    }
}
