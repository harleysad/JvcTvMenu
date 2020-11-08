package com.android.tv.settings.system;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TtsEngines;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

@Keep
public class TtsEngineSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String ARG_ENGINE_LABEL = "engineLabel";
    private static final String ARG_ENGINE_NAME = "engineName";
    private static final String ARG_VOICES = "voices";
    private static final boolean DBG = false;
    private static final String KEY_ENGINE_LOCALE = "tts_default_lang";
    private static final String KEY_ENGINE_SETTINGS = "tts_engine_settings";
    private static final String KEY_INSTALL_DATA = "tts_install_data";
    private static final String STATE_KEY_LOCALE_ENTRIES = "locale_entries";
    private static final String STATE_KEY_LOCALE_ENTRY_VALUES = "locale_entry_values";
    private static final String STATE_KEY_LOCALE_VALUE = "locale_value";
    private static final String TAG = "TtsEngineSettings";
    private static final int VOICE_DATA_INTEGRITY_CHECK = 1977;
    private Preference mEngineSettingsPreference;
    private TtsEngines mEnginesHelper;
    private Preference mInstallVoicesPreference;
    private final BroadcastReceiver mLanguagesChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.speech.tts.engine.TTS_DATA_INSTALLED".equals(intent.getAction())) {
                TtsEngineSettingsFragment.this.checkTtsData();
            }
        }
    };
    /* access modifiers changed from: private */
    public ListPreference mLocalePreference;
    private int mSelectedLocaleIndex = -1;
    private TextToSpeech mTts;
    private final TextToSpeech.OnInitListener mTtsInitListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            if (status != 0) {
                TtsEngineSettingsFragment.this.getFragmentManager().popBackStack();
            } else {
                TtsEngineSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        TtsEngineSettingsFragment.this.mLocalePreference.setEnabled(true);
                    }
                });
            }
        }
    };
    private Intent mVoiceDataDetails;

    public static void prepareArgs(@NonNull Bundle args, String engineName, String engineLabel, Intent voiceCheckData) {
        args.clear();
        args.putString(ARG_ENGINE_NAME, engineName);
        args.putString(ARG_ENGINE_LABEL, engineLabel);
        args.putParcelable(ARG_VOICES, voiceCheckData);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.tts_engine_settings);
        PreferenceScreen screen = getPreferenceScreen();
        screen.setTitle((CharSequence) getEngineLabel());
        screen.setKey(getEngineName());
        this.mLocalePreference = (ListPreference) findPreference(KEY_ENGINE_LOCALE);
        this.mLocalePreference.setOnPreferenceChangeListener(this);
        this.mEngineSettingsPreference = findPreference(KEY_ENGINE_SETTINGS);
        this.mEngineSettingsPreference.setOnPreferenceClickListener(this);
        this.mInstallVoicesPreference = findPreference(KEY_INSTALL_DATA);
        this.mInstallVoicesPreference.setOnPreferenceClickListener(this);
        boolean z = true;
        this.mEngineSettingsPreference.setTitle((CharSequence) getResources().getString(R.string.tts_engine_settings_title, new Object[]{getEngineLabel()}));
        Intent settingsIntent = this.mEnginesHelper.getSettingsIntent(getEngineName());
        this.mEngineSettingsPreference.setIntent(settingsIntent);
        if (settingsIntent == null) {
            this.mEngineSettingsPreference.setEnabled(false);
        }
        this.mInstallVoicesPreference.setEnabled(false);
        if (savedInstanceState == null) {
            this.mLocalePreference.setEnabled(false);
            this.mLocalePreference.setEntries(new CharSequence[0]);
            this.mLocalePreference.setEntryValues(new CharSequence[0]);
            return;
        }
        CharSequence[] entries = savedInstanceState.getCharSequenceArray(STATE_KEY_LOCALE_ENTRIES);
        CharSequence[] entryValues = savedInstanceState.getCharSequenceArray(STATE_KEY_LOCALE_ENTRY_VALUES);
        CharSequence value = savedInstanceState.getCharSequence(STATE_KEY_LOCALE_VALUE);
        this.mLocalePreference.setEntries(entries);
        this.mLocalePreference.setEntryValues(entryValues);
        this.mLocalePreference.setValue(value != null ? value.toString() : null);
        ListPreference listPreference = this.mLocalePreference;
        if (entries.length <= 0) {
            z = false;
        }
        listPreference.setEnabled(z);
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mEnginesHelper = new TtsEngines(getActivity());
        super.onCreate(savedInstanceState);
        this.mVoiceDataDetails = (Intent) getArguments().getParcelable(ARG_VOICES);
        this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mTtsInitListener, getEngineName());
        checkTtsData();
        getActivity().registerReceiver(this.mLanguagesChangedReceiver, new IntentFilter("android.speech.tts.engine.TTS_DATA_INSTALLED"));
    }

    public void onDestroy() {
        getActivity().unregisterReceiver(this.mLanguagesChangedReceiver);
        this.mTts.shutdown();
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequenceArray(STATE_KEY_LOCALE_ENTRIES, this.mLocalePreference.getEntries());
        outState.putCharSequenceArray(STATE_KEY_LOCALE_ENTRY_VALUES, this.mLocalePreference.getEntryValues());
        outState.putCharSequence(STATE_KEY_LOCALE_VALUE, this.mLocalePreference.getValue());
    }

    /* access modifiers changed from: private */
    public void checkTtsData() {
        Intent intent = new Intent("android.speech.tts.engine.CHECK_TTS_DATA");
        intent.setPackage(getEngineName());
        try {
            startActivityForResult(intent, VOICE_DATA_INTEGRITY_CHECK);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to check TTS data, no activity found for " + intent + ")");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != VOICE_DATA_INTEGRITY_CHECK) {
            return;
        }
        if (resultCode != 0) {
            updateVoiceDetails(data);
        } else {
            Log.e(TAG, "CheckVoiceData activity failed");
        }
    }

    private void updateVoiceDetails(Intent data) {
        if (data == null) {
            Log.e(TAG, "Engine failed voice data integrity check (null return)" + this.mTts.getCurrentEngine());
            return;
        }
        this.mVoiceDataDetails = data;
        ArrayList<String> available = this.mVoiceDataDetails.getStringArrayListExtra("availableVoices");
        ArrayList<String> unavailable = this.mVoiceDataDetails.getStringArrayListExtra("unavailableVoices");
        if (unavailable == null || unavailable.size() <= 0) {
            this.mInstallVoicesPreference.setEnabled(false);
        } else {
            this.mInstallVoicesPreference.setEnabled(true);
        }
        if (available == null) {
            Log.e(TAG, "TTS data check failed (available == null).");
            this.mLocalePreference.setEnabled(false);
            return;
        }
        updateDefaultLocalePref(available);
    }

    private void updateDefaultLocalePref(ArrayList<String> availableLangs) {
        if (availableLangs == null || availableLangs.size() == 0) {
            this.mLocalePreference.setEnabled(false);
            return;
        }
        Locale currentLocale = null;
        if (!this.mEnginesHelper.isLocaleSetToDefaultForEngine(getEngineName())) {
            currentLocale = this.mEnginesHelper.getLocalePrefForEngine(getEngineName());
        }
        ArrayList<Pair<String, Locale>> entryPairs = new ArrayList<>(availableLangs.size());
        for (int i = 0; i < availableLangs.size(); i++) {
            Locale locale = this.mEnginesHelper.parseLocaleString(availableLangs.get(i));
            if (locale != null) {
                entryPairs.add(new Pair(locale.getDisplayName(), locale));
            }
        }
        entryPairs.sort($$Lambda$TtsEngineSettingsFragment$Uqv3H2JDLs9QKXOHMYC7MFvAFBI.INSTANCE);
        this.mSelectedLocaleIndex = 0;
        CharSequence[] entries = new CharSequence[(availableLangs.size() + 1)];
        CharSequence[] entryValues = new CharSequence[(availableLangs.size() + 1)];
        entries[0] = getString(R.string.tts_lang_use_system);
        entryValues[0] = "";
        int i2 = 1;
        Iterator<Pair<String, Locale>> it = entryPairs.iterator();
        while (it.hasNext()) {
            Pair<String, Locale> entry = it.next();
            if (((Locale) entry.second).equals(currentLocale)) {
                this.mSelectedLocaleIndex = i2;
            }
            entries[i2] = (CharSequence) entry.first;
            entryValues[i2] = ((Locale) entry.second).toString();
            i2++;
        }
        this.mLocalePreference.setEntries(entries);
        this.mLocalePreference.setEntryValues(entryValues);
        this.mLocalePreference.setEnabled(true);
        setLocalePreference(this.mSelectedLocaleIndex);
    }

    private void setLocalePreference(int index) {
        if (index < 0) {
            this.mLocalePreference.setValue("");
            this.mLocalePreference.setSummary((int) R.string.tts_lang_not_selected);
            return;
        }
        this.mLocalePreference.setValueIndex(index);
        this.mLocalePreference.setSummary(this.mLocalePreference.getEntries()[index]);
    }

    private void installVoiceData() {
        if (!TextUtils.isEmpty(getEngineName())) {
            Intent intent = new Intent("android.speech.tts.engine.INSTALL_TTS_DATA");
            intent.setPackage(getEngineName());
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Failed to install TTS data, no activity found for " + intent + ")");
            }
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference != this.mInstallVoicesPreference) {
            return false;
        }
        installVoiceData();
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference != this.mLocalePreference) {
            return false;
        }
        String localeString = (String) newValue;
        updateLanguageTo(!TextUtils.isEmpty(localeString) ? this.mEnginesHelper.parseLocaleString(localeString) : null);
        return true;
    }

    private void updateLanguageTo(Locale locale) {
        int selectedLocaleIndex = -1;
        String localeString = locale != null ? locale.toString() : "";
        int i = 0;
        while (true) {
            if (i >= this.mLocalePreference.getEntryValues().length) {
                break;
            } else if (localeString.equalsIgnoreCase(this.mLocalePreference.getEntryValues()[i].toString())) {
                selectedLocaleIndex = i;
                break;
            } else {
                i++;
            }
        }
        if (selectedLocaleIndex == -1) {
            Log.w(TAG, "updateLanguageTo called with unknown locale argument");
            return;
        }
        this.mLocalePreference.setSummary(this.mLocalePreference.getEntries()[selectedLocaleIndex]);
        this.mSelectedLocaleIndex = selectedLocaleIndex;
        this.mEnginesHelper.updateLocalePrefForEngine(getEngineName(), locale);
        if (getEngineName().equals(this.mTts.getCurrentEngine())) {
            this.mTts.setLanguage(locale != null ? locale : Locale.getDefault());
        }
    }

    private String getEngineName() {
        return getArguments().getString(ARG_ENGINE_NAME);
    }

    private String getEngineLabel() {
        return getArguments().getString(ARG_ENGINE_LABEL);
    }

    public int getMetricsCategory() {
        return 93;
    }
}
