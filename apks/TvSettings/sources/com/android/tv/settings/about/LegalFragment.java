package com.android.tv.settings.about;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

@Keep
public class LegalFragment extends SettingsPreferenceFragment {
    private static final String KEY_ADS = "ads";
    private static final String KEY_COPYRIGHT = "copyright";
    private static final String KEY_LICENSE = "license";
    private static final String KEY_TERMS = "terms";
    private static final String KEY_WEBVIEW_LICENSE = "webview_license";

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_legal, (String) null);
        PreferenceScreen screen = getPreferenceScreen();
        Context context = getActivity();
        PreferenceUtils.resolveSystemActivityOrRemove(context, screen, findPreference(KEY_TERMS), 1);
        PreferenceUtils.resolveSystemActivityOrRemove(context, screen, findPreference(KEY_LICENSE), 1);
        PreferenceUtils.resolveSystemActivityOrRemove(context, screen, findPreference(KEY_COPYRIGHT), 1);
        PreferenceUtils.resolveSystemActivityOrRemove(context, screen, findPreference(KEY_WEBVIEW_LICENSE), 1);
        PreferenceUtils.resolveSystemActivityOrRemove(context, screen, findPreference(KEY_ADS), 1);
    }

    public int getMetricsCategory() {
        return 225;
    }
}
