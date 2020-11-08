package com.android.tv.settings.inputmethod;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.inputmethod.InputMethodInfo;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.wrapper.PackageManagerWrapper;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.autofill.AutofillHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Keep
public class KeyboardFragment extends SettingsPreferenceFragment {
    private static final int INPUT_METHOD_PREFERENCE_ORDER = 2;
    @VisibleForTesting
    static final String KEY_AUTOFILL_CATEGORY = "autofillCategory";
    private static final String KEY_AUTOFILL_SETTINGS_PREFIX = "autofillSettings:";
    @VisibleForTesting
    static final String KEY_CURRENT_AUTOFILL = "currentAutofill";
    @VisibleForTesting
    static final String KEY_CURRENT_KEYBOARD = "currentKeyboard";
    @VisibleForTesting
    static final String KEY_KEYBOARD_CATEGORY = "keyboardCategory";
    private static final String KEY_KEYBOARD_SETTINGS_PREFIX = "keyboardSettings:";
    private static final String TAG = "KeyboardFragment";
    private PackageManagerWrapper mPm;

    public static KeyboardFragment newInstance() {
        return new KeyboardFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPm = new PackageManagerWrapper(context.getPackageManager());
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.keyboard, (String) null);
        findPreference(KEY_CURRENT_KEYBOARD).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return InputMethodHelper.setDefaultInputMethodId(KeyboardFragment.this.getContext(), (String) obj);
            }
        });
        updateUi();
    }

    public void onResume() {
        super.onResume();
        updateUi();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateUi() {
        updateAutofill();
        updateKeyboards();
    }

    private void updateKeyboards() {
        updateCurrentKeyboardPreference((ListPreference) findPreference(KEY_CURRENT_KEYBOARD));
        updateKeyboardsSettings();
    }

    private void updateCurrentKeyboardPreference(ListPreference currentKeyboardPref) {
        PackageManager packageManager = getContext().getPackageManager();
        List<InputMethodInfo> enabledInputMethodInfos = InputMethodHelper.getEnabledSystemInputMethodList(getContext());
        List<CharSequence> entries = new ArrayList<>(enabledInputMethodInfos.size());
        List<CharSequence> values = new ArrayList<>(enabledInputMethodInfos.size());
        int defaultIndex = 0;
        String defaultId = InputMethodHelper.getDefaultInputMethodId(getContext());
        for (InputMethodInfo info : enabledInputMethodInfos) {
            entries.add(info.loadLabel(packageManager));
            String id = info.getId();
            values.add(id);
            if (TextUtils.equals(id, defaultId)) {
                defaultIndex = values.size() - 1;
            }
        }
        currentKeyboardPref.setEntries((CharSequence[]) entries.toArray(new CharSequence[entries.size()]));
        currentKeyboardPref.setEntryValues((CharSequence[]) values.toArray(new CharSequence[values.size()]));
        if (entries.size() > 0) {
            currentKeyboardPref.setValueIndex(defaultIndex);
        }
    }

    /* access modifiers changed from: package-private */
    public Context getPreferenceContext() {
        return getPreferenceManager().getContext();
    }

    private void updateKeyboardsSettings() {
        int i;
        Context preferenceContext = getPreferenceContext();
        PackageManager packageManager = getContext().getPackageManager();
        List<InputMethodInfo> enabledInputMethodInfos = InputMethodHelper.getEnabledSystemInputMethodList(getContext());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Set<String> enabledInputMethodKeys = new ArraySet<>(enabledInputMethodInfos.size());
        Iterator<InputMethodInfo> it = enabledInputMethodInfos.iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            InputMethodInfo info = it.next();
            Intent settingsIntent = InputMethodHelper.getInputMethodSettingsIntent(info);
            if (settingsIntent != null) {
                String key = KEY_KEYBOARD_SETTINGS_PREFIX + info.getId();
                Preference preference = preferenceScreen.findPreference(key);
                if (preference == null) {
                    preference = new Preference(preferenceContext);
                    preference.setOrder(2);
                    preferenceScreen.addPreference(preference);
                }
                preference.setTitle((CharSequence) getContext().getString(R.string.title_settings, new Object[]{info.loadLabel(packageManager)}));
                preference.setKey(key);
                preference.setIntent(settingsIntent);
                enabledInputMethodKeys.add(key);
            }
        }
        while (true) {
            int i2 = i;
            if (i2 < preferenceScreen.getPreferenceCount()) {
                Preference preference2 = preferenceScreen.getPreference(i2);
                String key2 = preference2.getKey();
                if (TextUtils.isEmpty(key2) || !key2.startsWith(KEY_KEYBOARD_SETTINGS_PREFIX) || enabledInputMethodKeys.contains(key2)) {
                    i2++;
                } else {
                    preferenceScreen.removePreference(preference2);
                }
                i = i2;
            } else {
                return;
            }
        }
    }

    private void updateAutofill() {
        PreferenceCategory autofillCategory = (PreferenceCategory) findPreference(KEY_AUTOFILL_CATEGORY);
        List<DefaultAppInfo> candidates = getAutofillCandidates();
        if (candidates.isEmpty()) {
            findPreference(KEY_KEYBOARD_CATEGORY).setVisible(false);
            autofillCategory.setVisible(false);
            getPreferenceScreen().setTitle((int) R.string.system_keyboard);
            return;
        }
        findPreference(KEY_KEYBOARD_CATEGORY).setVisible(true);
        autofillCategory.setVisible(true);
        updateCurrentAutofillPreference(findPreference(KEY_CURRENT_AUTOFILL), candidates);
        updateAutofillSettings(candidates);
        getPreferenceScreen().setTitle((int) R.string.system_keyboard_autofill);
    }

    private List<DefaultAppInfo> getAutofillCandidates() {
        return AutofillHelper.getAutofillCandidates(getContext(), this.mPm, UserHandle.myUserId());
    }

    private void updateCurrentAutofillPreference(Preference currentAutofillPref, List<DefaultAppInfo> candidates) {
        CharSequence summary;
        DefaultAppInfo app = AutofillHelper.getCurrentAutofill(getContext(), candidates);
        if (app == null) {
            summary = getContext().getString(R.string.autofill_none);
        } else {
            summary = app.loadLabel();
        }
        currentAutofillPref.setSummary(summary);
    }

    private void updateAutofillSettings(List<DefaultAppInfo> candidates) {
        int i;
        Context preferenceContext = getPreferenceContext();
        PreferenceCategory autofillCategory = (PreferenceCategory) findPreference(KEY_AUTOFILL_CATEGORY);
        Set<String> autofillServicesKeys = new ArraySet<>(candidates.size());
        Iterator<DefaultAppInfo> it = candidates.iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            DefaultAppInfo info = it.next();
            Intent settingsIntent = AutofillHelper.getAutofillSettingsIntent(getContext(), this.mPm, info);
            if (settingsIntent != null) {
                String key = KEY_AUTOFILL_SETTINGS_PREFIX + info.getKey();
                Preference preference = findPreference(key);
                if (preference == null) {
                    preference = new Preference(preferenceContext);
                    autofillCategory.addPreference(preference);
                }
                preference.setTitle((CharSequence) getContext().getString(R.string.title_settings, new Object[]{info.loadLabel()}));
                preference.setKey(key);
                preference.setIntent(settingsIntent);
                autofillServicesKeys.add(key);
            }
        }
        while (true) {
            int i2 = i;
            if (i2 < autofillCategory.getPreferenceCount()) {
                Preference preference2 = autofillCategory.getPreference(i2);
                String key2 = preference2.getKey();
                if (TextUtils.isEmpty(key2) || !key2.startsWith(KEY_AUTOFILL_SETTINGS_PREFIX) || autofillServicesKeys.contains(key2)) {
                    i2++;
                } else {
                    autofillCategory.removePreference(preference2);
                }
                i = i2;
            } else {
                return;
            }
        }
    }

    public int getMetricsCategory() {
        return 58;
    }
}
