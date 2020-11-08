package com.android.tv.settings.overlay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.util.Log;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.SettingsFragmentProvider;

@Keep
public class FeatureFactoryImpl extends FeatureFactory {
    public SettingsFragmentProvider getSettingsFragmentProvider() {
        return $$Lambda$XmADKZBisPCEdw5XEwNk2GKZ0.INSTANCE;
    }

    public boolean isTwoPanelLayout() {
        return false;
    }

    public static class SettingsFragment extends BaseSettingsFragment {
        public static SettingsFragment newInstance(String className, Bundle arguments) {
            SettingsFragment fragment = new SettingsFragment();
            Bundle args = arguments == null ? new Bundle() : new Bundle(arguments);
            args.putString("fragmentClassName", className);
            fragment.setArguments(args);
            return fragment;
        }

        public void onPreferenceStartInitialScreen() {
            try {
                Fragment fragment = (Fragment) Class.forName(getArguments().getString("fragmentClassName")).newInstance();
                fragment.setArguments(getArguments());
                startPreferenceFragment(fragment);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                Log.e("FeatureFactory", "Unable to start initial preference screen.", e);
            }
        }
    }
}
