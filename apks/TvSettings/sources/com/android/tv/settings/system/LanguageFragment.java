package com.android.tv.settings.system;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.app.LocalePicker;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.Locale;
import java.util.Map;

@Keep
public class LanguageFragment extends SettingsPreferenceFragment {
    private static final String LANGUAGE_RADIO_GROUP = "language";
    private static final int LANGUAGE_SET_DELAY_MS = 500;
    private static final String TAG = "LanguageFragment";
    private final Handler mDelayHandler = new Handler();
    private final Map<String, LocalePicker.LocaleInfo> mLocaleInfoMap = new ArrayMap();
    /* access modifiers changed from: private */
    public Locale mNewLocale;
    private final Runnable mSetLanguageRunnable = new Runnable() {
        public void run() {
            LocalePicker.updateLocale(LanguageFragment.this.mNewLocale);
        }
    };

    public static LanguageFragment newInstance() {
        return new LanguageFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(themedContext);
        screen.setTitle((int) R.string.system_language);
        Locale currentLocale = null;
        try {
            currentLocale = ActivityManager.getService().getConfiguration().getLocales().get(0);
        } catch (RemoteException e) {
            Log.e(TAG, "Could not retrieve locale", e);
        }
        Preference activePref = null;
        for (LocalePicker.LocaleInfo localeInfo : LocalePicker.getAllAssetLocales(themedContext, DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext()))) {
            String languageTag = localeInfo.getLocale().toLanguageTag();
            this.mLocaleInfoMap.put(languageTag, localeInfo);
            RadioPreference radioPreference = new RadioPreference(themedContext);
            radioPreference.setKey(languageTag);
            radioPreference.setPersistent(false);
            radioPreference.setTitle((CharSequence) localeInfo.getLabel());
            radioPreference.setRadioGroup(LANGUAGE_RADIO_GROUP);
            radioPreference.setLayoutResource(R.layout.preference_reversed_widget);
            if (localeInfo.getLocale().equals(currentLocale)) {
                radioPreference.setChecked(true);
                activePref = radioPreference;
            }
            screen.addPreference(radioPreference);
        }
        if (activePref != null && savedInstanceState == null) {
            scrollToPreference(activePref);
        }
        setPreferenceScreen(screen);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RadioPreference) {
            RadioPreference radioPreference = (RadioPreference) preference;
            radioPreference.clearOtherRadioPreferences(getPreferenceScreen());
            if (radioPreference.isChecked()) {
                this.mNewLocale = this.mLocaleInfoMap.get(radioPreference.getKey()).getLocale();
                this.mDelayHandler.removeCallbacks(this.mSetLanguageRunnable);
                this.mDelayHandler.postDelayed(this.mSetLanguageRunnable, 500);
            } else {
                radioPreference.setChecked(true);
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    public int getMetricsCategory() {
        return 750;
    }
}
