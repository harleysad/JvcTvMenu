package com.mediatek.wwtv.setting;

import android.app.Fragment;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

public abstract class BaseSettingsFragment extends LeanbackSettingsFragment {
    public final boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        Fragment f = Fragment.instantiate(getActivity(), pref.getFragment(), pref.getExtras());
        f.setTargetFragment(caller, 0);
        if ((f instanceof PreferenceFragment) || (f instanceof PreferenceDialogFragment)) {
            startPreferenceFragment(f);
            return true;
        }
        startImmersiveFragment(f);
        return true;
    }

    public final boolean onPreferenceStartScreen(PreferenceFragment caller, PreferenceScreen pref) {
        return false;
    }
}
