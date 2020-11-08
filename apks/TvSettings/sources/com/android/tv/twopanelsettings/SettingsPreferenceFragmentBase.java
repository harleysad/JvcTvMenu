package com.android.tv.twopanelsettings;

import android.support.v17.preference.LeanbackPreferenceFragment;

public abstract class SettingsPreferenceFragmentBase extends LeanbackPreferenceFragment {
    public void onResume() {
        super.onResume();
        if (getCallbackFragment() instanceof TwoPanelSettingsFragment) {
            ((TwoPanelSettingsFragment) getCallbackFragment()).addListenerForFragment(this);
        }
    }

    public void onPause() {
        super.onPause();
        if (getCallbackFragment() instanceof TwoPanelSettingsFragment) {
            ((TwoPanelSettingsFragment) getCallbackFragment()).removeListenerForFragment(this);
        }
    }
}
