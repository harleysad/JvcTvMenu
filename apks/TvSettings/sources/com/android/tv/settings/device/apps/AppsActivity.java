package com.android.tv.settings.device.apps;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class AppsActivity extends TvSettingsActivity {
    public static final String EXTRA_VOLUME_NAME = "volumeName";
    public static final String EXTRA_VOLUME_UUID = "volumeUuid";

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        Bundle args = getIntent().getExtras();
        String volumeUuid = null;
        String volumeName = null;
        if (args != null && args.containsKey("volumeUuid")) {
            volumeUuid = args.getString("volumeUuid");
            volumeName = args.getString("volumeName");
        }
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(AppsFragment.class.getName(), getArguments(volumeUuid, volumeName));
    }

    private Bundle getArguments(String volumeUuid, String volumeName) {
        Bundle b = new Bundle(2);
        b.putString("volumeUuid", volumeUuid);
        b.putString("volumeName", volumeName);
        return b;
    }
}
