package android.support.v17.preference;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

@Deprecated
public abstract class LeanbackSettingsFragment extends Fragment implements PreferenceFragment.OnPreferenceStartFragmentCallback, PreferenceFragment.OnPreferenceStartScreenCallback, PreferenceFragment.OnPreferenceDisplayDialogCallback {
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

    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragment caller, Preference pref) {
        LeanbackListPreferenceDialogFragment leanbackListPreferenceDialogFragment;
        if (caller != null) {
            if (pref instanceof ListPreference) {
                leanbackListPreferenceDialogFragment = LeanbackListPreferenceDialogFragment.newInstanceSingle(((ListPreference) pref).getKey());
                leanbackListPreferenceDialogFragment.setTargetFragment(caller, 0);
                startPreferenceFragment(leanbackListPreferenceDialogFragment);
            } else if (!(pref instanceof MultiSelectListPreference)) {
                return false;
            } else {
                leanbackListPreferenceDialogFragment = LeanbackListPreferenceDialogFragment.newInstanceMulti(((MultiSelectListPreference) pref).getKey());
                leanbackListPreferenceDialogFragment.setTargetFragment(caller, 0);
                startPreferenceFragment(leanbackListPreferenceDialogFragment);
            }
            LeanbackListPreferenceDialogFragment leanbackListPreferenceDialogFragment2 = leanbackListPreferenceDialogFragment;
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
            if (Build.VERSION.SDK_INT < 23) {
                transaction.add(R.id.settings_preference_fragment_container, new DummyFragment());
            }
            transaction.remove(preferenceFragment);
        }
        transaction.add(R.id.settings_dialog_container, fragment).addToBackStack((String) null).commit();
    }

    private class RootViewOnKeyListener implements View.OnKeyListener {
        RootViewOnKeyListener() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                return LeanbackSettingsFragment.this.getChildFragmentManager().popBackStackImmediate();
            }
            return false;
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static class DummyFragment extends Fragment {
        @Nullable
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = new Space(inflater.getContext());
            v.setVisibility(8);
            return v;
        }
    }
}
