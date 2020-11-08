package com.android.tv.settings;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.system.LeanbackPickerDialogFragment;
import com.android.tv.settings.system.LeanbackPickerDialogPreference;

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

    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragment caller, Preference pref) {
        Fragment f;
        if (!(pref instanceof LeanbackPickerDialogPreference)) {
            return super.onPreferenceDisplayDialog(caller, pref);
        }
        if (((LeanbackPickerDialogPreference) pref).getType().equals("date")) {
            f = LeanbackPickerDialogFragment.newDatePickerInstance(pref.getKey());
        } else {
            f = LeanbackPickerDialogFragment.newTimePickerInstance(pref.getKey());
        }
        f.setTargetFragment(caller, 0);
        startPreferenceFragment(f);
        return true;
    }
}
