package com.android.tv.settings;

import android.app.Fragment;
import android.os.Bundle;

public interface SettingsFragmentProvider {
    Fragment newSettingsFragment(String str, Bundle bundle) throws IllegalArgumentException;
}
