package com.android.tv.settings.util;

import android.content.Intent;

public class ThemeHelper {
    public static final String EXTRA_FROM_SETUP_WIZARD = "firstRun";

    public static boolean fromSetupWizard(Intent intent) {
        return intent.getBooleanExtra(EXTRA_FROM_SETUP_WIZARD, false);
    }

    private ThemeHelper() {
    }
}
