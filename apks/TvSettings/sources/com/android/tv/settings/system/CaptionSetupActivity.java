package com.android.tv.settings.system;

import android.app.Activity;
import android.os.Bundle;

public class CaptionSetupActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(16908290, CaptionSettingsFragment.newInstance()).commit();
        }
    }
}
