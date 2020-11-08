package com.android.tv.settings.autofill;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class AutofillPickerActivity extends TvSettingsActivity {
    public static final String EXTRA_PACKAGE_NAME = "package_name";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_PACKAGE_NAME, intent.getData().getSchemeSpecificPart());
        super.onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(AutofillPickerFragment.class.getName(), (Bundle) null);
    }
}
