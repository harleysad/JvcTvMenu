package android.support.v17.preference;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class LeanbackSettingsFragmentCompat extends Fragment implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, PreferenceFragmentCompat.OnPreferenceStartScreenCallback, PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {
    private static final String PREFERENCE_FRAGMENT_TAG = "android.support.v17.preference.LeanbackSettingsFragment.PREFERENCE_FRAGMENT";
    private final RootViewOnKeyListener mRootViewOnKeyListener = new RootViewOnKeyListener();

    public abstract void onPreferenceStartInitialScreen();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leanback_settings_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            onPreferenceStartInitialScreen();
        }
    }

    public void onResume() {
        super.onResume();
        LeanbackSettingsRootView rootView = (LeanbackSettingsRootView) getView();
        if (rootView != null) {
            rootView.setOnBackKeyListener(this.mRootViewOnKeyListener);
        }
    }

    public void onPause() {
        super.onPause();
        LeanbackSettingsRootView rootView = (LeanbackSettingsRootView) getView();
        if (rootView != null) {
            rootView.setOnBackKeyListener((View.OnKeyListener) null);
        }
    }

    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragmentCompat caller, Preference pref) {
        LeanbackPreferenceDialogFragmentCompat leanbackPreferenceDialogFragmentCompat;
        if (caller != null) {
            if (pref instanceof ListPreference) {
                leanbackPreferenceDialogFragmentCompat = LeanbackListPreferenceDialogFragmentCompat.newInstanceSingle(((ListPreference) pref).getKey());
                leanbackPreferenceDialogFragmentCompat.setTargetFragment(caller, 0);
                startPreferenceFragment(leanbackPreferenceDialogFragmentCompat);
            } else if (pref instanceof MultiSelectListPreference) {
                leanbackPreferenceDialogFragmentCompat = LeanbackListPreferenceDialogFragmentCompat.newInstanceMulti(((MultiSelectListPreference) pref).getKey());
                leanbackPreferenceDialogFragmentCompat.setTargetFragment(caller, 0);
                startPreferenceFragment(leanbackPreferenceDialogFragmentCompat);
            } else if (!(pref instanceof EditTextPreference)) {
                return false;
            } else {
                leanbackPreferenceDialogFragmentCompat = LeanbackEditTextPreferenceDialogFragmentCompat.newInstance(pref.getKey());
                leanbackPreferenceDialogFragmentCompat.setTargetFragment(caller, 0);
                startPreferenceFragment(leanbackPreferenceDialogFragmentCompat);
            }
            LeanbackPreferenceDialogFragmentCompat leanbackPreferenceDialogFragmentCompat2 = leanbackPreferenceDialogFragmentCompat;
            return true;
        }
        throw new IllegalArgumentException("Cannot display dialog for preference " + pref + ", Caller must not be null!");
    }

    public void startPreferenceFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (getChildFragmentManager().findFragmentByTag(PREFERENCE_FRAGMENT_TAG) != null) {
            transaction.addToBackStack((String) null).replace(R.id.settings_preference_fragment_container, fragment, PREFERENCE_FRAGMENT_TAG);
        } else {
            transaction.add(R.id.settings_preference_fragment_container, fragment, PREFERENCE_FRAGMENT_TAG);
        }
        transaction.commit();
    }

    public void startImmersiveFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Fragment preferenceFragment = getChildFragmentManager().findFragmentByTag(PREFERENCE_FRAGMENT_TAG);
        if (preferenceFragment != null && !preferenceFragment.isHidden()) {
            transaction.remove(preferenceFragment);
        }
        transaction.add(R.id.settings_dialog_container, fragment).addToBackStack((String) null).commit();
    }

    private class RootViewOnKeyListener implements View.OnKeyListener {
        RootViewOnKeyListener() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                return LeanbackSettingsFragmentCompat.this.getChildFragmentManager().popBackStackImmediate();
            }
            return false;
        }
    }
}
