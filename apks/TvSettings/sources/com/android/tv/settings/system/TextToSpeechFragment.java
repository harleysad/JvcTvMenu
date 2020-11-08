package com.android.tv.settings.system;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TtsEngines;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Keep;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Checkable;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.system.TtsEnginePreference;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Set;

@Keep
public class TextToSpeechFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, TtsEnginePreference.RadioButtonGroupState {
    private static final boolean DBG = false;
    private static final int GET_SAMPLE_TEXT = 1983;
    private static final String KEY_DEFAULT_RATE = "tts_default_rate";
    private static final String KEY_ENGINE_PREFERENCE_SECTION = "tts_engine_preference_section";
    private static final String KEY_ENGINE_SETTINGS = "tts_engine_settings";
    private static final String KEY_PLAY_EXAMPLE = "tts_play_example";
    private static final String KEY_STATUS = "tts_status";
    private static final String TAG = "TextToSpeechSettings";
    private static final int VOICE_DATA_INTEGRITY_CHECK = 1977;
    private List<String> mAvailableStrLocals;
    private Checkable mCurrentChecked;
    private Locale mCurrentDefaultLocale;
    private String mCurrentEngine;
    private int mDefaultRate = 100;
    private ListPreference mDefaultRatePref;
    private PreferenceCategory mEnginePreferenceCategory;
    private Preference mEngineSettingsPref;
    private Preference mEngineStatus;
    private TtsEngines mEnginesHelper = null;
    private final TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            TextToSpeechFragment.this.onInitEngine(status);
        }
    };
    private Preference mPlayExample;
    private String mPreviousEngine;
    private String mSampleText = null;
    private TextToSpeech mTts = null;
    private final TextToSpeech.OnInitListener mUpdateListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            TextToSpeechFragment.this.onUpdateEngine(status);
        }
    };

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.tts_settings);
        this.mEngineSettingsPref = findPreference(KEY_ENGINE_SETTINGS);
        this.mPlayExample = findPreference(KEY_PLAY_EXAMPLE);
        this.mPlayExample.setOnPreferenceClickListener(this);
        this.mPlayExample.setEnabled(false);
        this.mEnginePreferenceCategory = (PreferenceCategory) findPreference(KEY_ENGINE_PREFERENCE_SECTION);
        this.mDefaultRatePref = (ListPreference) findPreference(KEY_DEFAULT_RATE);
        this.mEngineStatus = findPreference(KEY_STATUS);
        updateEngineStatus(R.string.tts_status_checking);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setVolumeControlStream(3);
        this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mInitListener);
        this.mEnginesHelper = new TtsEngines(getActivity().getApplicationContext());
        setTtsUtteranceProgressListener();
        initSettings();
    }

    public void onResume() {
        super.onResume();
        if (this.mTts != null && this.mCurrentDefaultLocale != null) {
            if (!this.mCurrentDefaultLocale.equals(this.mTts.getDefaultLanguage())) {
                updateWidgetState(false);
                checkDefaultLocale();
            }
        }
    }

    private void setTtsUtteranceProgressListener() {
        if (this.mTts != null) {
            this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                public void onStart(String utteranceId) {
                }

                public void onDone(String utteranceId) {
                }

                public void onError(String utteranceId) {
                    Log.e(TextToSpeechFragment.TAG, "Error while trying to synthesize sample text");
                }
            });
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mTts != null) {
            this.mTts.shutdown();
            this.mTts = null;
        }
    }

    private void initSettings() {
        try {
            this.mDefaultRate = Settings.Secure.getInt(getActivity().getContentResolver(), KEY_DEFAULT_RATE);
        } catch (Settings.SettingNotFoundException e) {
            this.mDefaultRate = 100;
        }
        this.mDefaultRatePref.setValue(String.valueOf(this.mDefaultRate));
        this.mDefaultRatePref.setOnPreferenceChangeListener(this);
        this.mCurrentEngine = this.mTts.getCurrentEngine();
        this.mEnginePreferenceCategory.removeAll();
        for (TextToSpeech.EngineInfo engine : this.mEnginesHelper.getEngines()) {
            this.mEnginePreferenceCategory.addPreference(new TtsEnginePreference(getPreferenceManager().getContext(), engine, this));
        }
        checkVoiceData(this.mCurrentEngine);
    }

    public void onInitEngine(int status) {
        if (status == 0) {
            checkDefaultLocale();
        } else {
            updateWidgetState(false);
        }
    }

    private void checkDefaultLocale() {
        Locale defaultLocale = this.mTts.getDefaultLanguage();
        if (defaultLocale == null) {
            Log.e(TAG, "Failed to get default language from engine " + this.mCurrentEngine);
            updateWidgetState(false);
            updateEngineStatus(R.string.tts_status_not_supported);
            return;
        }
        Locale oldDefaultLocale = this.mCurrentDefaultLocale;
        this.mCurrentDefaultLocale = this.mEnginesHelper.parseLocaleString(defaultLocale.toString());
        if (!Objects.equals(oldDefaultLocale, this.mCurrentDefaultLocale)) {
            this.mSampleText = null;
        }
        this.mTts.setLanguage(defaultLocale);
        if (evaluateDefaultLocale() && this.mSampleText == null) {
            getSampleText();
        }
    }

    private boolean evaluateDefaultLocale() {
        if (this.mCurrentDefaultLocale == null || this.mAvailableStrLocals == null) {
            return false;
        }
        boolean notInAvailableLangauges = true;
        try {
            String defaultLocaleStr = this.mCurrentDefaultLocale.getISO3Language();
            if (!TextUtils.isEmpty(this.mCurrentDefaultLocale.getISO3Country())) {
                defaultLocaleStr = defaultLocaleStr + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + this.mCurrentDefaultLocale.getISO3Country();
            }
            if (!TextUtils.isEmpty(this.mCurrentDefaultLocale.getVariant())) {
                defaultLocaleStr = defaultLocaleStr + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + this.mCurrentDefaultLocale.getVariant();
            }
            Iterator<String> it = this.mAvailableStrLocals.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().equalsIgnoreCase(defaultLocaleStr)) {
                        notInAvailableLangauges = false;
                        break;
                    }
                } else {
                    break;
                }
            }
            int defaultAvailable = this.mTts.setLanguage(this.mCurrentDefaultLocale);
            if (defaultAvailable == -2 || defaultAvailable == -1 || notInAvailableLangauges) {
                updateEngineStatus(R.string.tts_status_not_supported);
                updateWidgetState(false);
                return false;
            }
            if (isNetworkRequiredForSynthesis()) {
                updateEngineStatus(R.string.tts_status_requires_network);
            } else {
                updateEngineStatus(R.string.tts_status_ok);
            }
            updateWidgetState(true);
            return true;
        } catch (MissingResourceException e) {
            updateEngineStatus(R.string.tts_status_not_supported);
            updateWidgetState(false);
            return false;
        }
    }

    private void getSampleText() {
        String currentEngine = this.mTts.getCurrentEngine();
        if (TextUtils.isEmpty(currentEngine)) {
            currentEngine = this.mTts.getDefaultEngine();
        }
        Intent intent = new Intent("android.speech.tts.engine.GET_SAMPLE_TEXT");
        intent.putExtra("language", this.mCurrentDefaultLocale.getLanguage());
        intent.putExtra("country", this.mCurrentDefaultLocale.getCountry());
        intent.putExtra("variant", this.mCurrentDefaultLocale.getVariant());
        intent.setPackage(currentEngine);
        try {
            startActivityForResult(intent, GET_SAMPLE_TEXT);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to get sample text, no activity found for " + intent + ")");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_SAMPLE_TEXT) {
            onSampleTextReceived(resultCode, data);
        } else if (requestCode == VOICE_DATA_INTEGRITY_CHECK) {
            onVoiceDataIntegrityCheckDone(data);
        }
    }

    private String getDefaultSampleString() {
        if (!(this.mTts == null || this.mTts.getLanguage() == null)) {
            try {
                String currentLang = this.mTts.getLanguage().getISO3Language();
                String[] strings = getActivity().getResources().getStringArray(R.array.tts_demo_strings);
                String[] langs = getActivity().getResources().getStringArray(R.array.tts_demo_string_langs);
                for (int i = 0; i < strings.length; i++) {
                    if (langs[i].equals(currentLang)) {
                        return strings[i];
                    }
                }
            } catch (MissingResourceException e) {
            }
        }
        return getString(R.string.tts_default_sample_string);
    }

    private boolean isNetworkRequiredForSynthesis() {
        Set<String> features = this.mTts.getFeatures(this.mCurrentDefaultLocale);
        return features != null && features.contains("networkTts") && !features.contains("embeddedTts");
    }

    private void onSampleTextReceived(int resultCode, Intent data) {
        String sample = getDefaultSampleString();
        if (!(resultCode != 0 || data == null || data.getStringExtra("sampleText") == null)) {
            sample = data.getStringExtra("sampleText");
        }
        this.mSampleText = sample;
        if (this.mSampleText != null) {
            updateWidgetState(true);
        } else {
            Log.e(TAG, "Did not have a sample string for the requested language. Using default");
        }
    }

    private void speakSampleText() {
        if (!isNetworkRequiredForSynthesis() || this.mTts.isLanguageAvailable(this.mCurrentDefaultLocale) >= 0) {
            HashMap<String, String> params = new HashMap<>();
            params.put("utteranceId", "Sample");
            this.mTts.speak(this.mSampleText, 0, params);
            return;
        }
        Log.w(TAG, "Network required for sample synthesis for requested language");
        displayNetworkAlert();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (!KEY_DEFAULT_RATE.equals(preference.getKey())) {
            return true;
        }
        this.mDefaultRate = Integer.parseInt((String) objValue);
        try {
            Settings.Secure.putInt(getActivity().getContentResolver(), KEY_DEFAULT_RATE, this.mDefaultRate);
            if (this.mTts == null) {
                return true;
            }
            this.mTts.setSpeechRate(((float) this.mDefaultRate) / 100.0f);
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, "could not persist default TTS rate setting", e);
            return true;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference != this.mPlayExample) {
            return false;
        }
        speakSampleText();
        return true;
    }

    private void updateWidgetState(boolean enable) {
        this.mPlayExample.setEnabled(enable);
        this.mDefaultRatePref.setEnabled(enable);
        this.mEngineStatus.setEnabled(enable);
    }

    private void updateEngineStatus(int resourceId) {
        Locale locale = this.mCurrentDefaultLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.mEngineStatus.setSummary((CharSequence) getString(resourceId, new Object[]{locale.getDisplayName()}));
    }

    private void displayNetworkAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(17039380).setMessage(getActivity().getString(R.string.tts_engine_network_required)).setCancelable(false).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private void updateDefaultEngine(String engine) {
        updateWidgetState(false);
        updateEngineStatus(R.string.tts_status_checking);
        this.mPreviousEngine = this.mTts.getCurrentEngine();
        try {
            this.mTts.shutdown();
            this.mTts = null;
        } catch (Exception e) {
            Log.e(TAG, "Error shutting down TTS engine" + e);
        }
        this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mUpdateListener, engine);
        setTtsUtteranceProgressListener();
    }

    public void onUpdateEngine(int status) {
        if (status == 0) {
            checkVoiceData(this.mTts.getCurrentEngine());
            return;
        }
        if (this.mPreviousEngine != null) {
            this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mInitListener, this.mPreviousEngine);
            setTtsUtteranceProgressListener();
        }
        this.mPreviousEngine = null;
    }

    private void checkVoiceData(String engine) {
        Intent intent = new Intent("android.speech.tts.engine.CHECK_TTS_DATA");
        intent.setPackage(engine);
        try {
            startActivityForResult(intent, VOICE_DATA_INTEGRITY_CHECK);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to check TTS data, no activity found for " + intent + ")");
        }
    }

    private void onVoiceDataIntegrityCheckDone(Intent data) {
        String engine = this.mTts.getCurrentEngine();
        if (engine == null) {
            Log.e(TAG, "Voice data check complete, but no engine bound");
        } else if (data == null) {
            Log.e(TAG, "Engine failed voice data integrity check (null return)" + this.mTts.getCurrentEngine());
        } else {
            Settings.Secure.putString(getActivity().getContentResolver(), "tts_default_synth", engine);
            this.mAvailableStrLocals = data.getStringArrayListExtra("availableVoices");
            if (this.mAvailableStrLocals == null) {
                Log.e(TAG, "Voice data check complete, but no available voices found");
                this.mAvailableStrLocals = new ArrayList();
            }
            if (evaluateDefaultLocale()) {
                getSampleText();
            }
            TextToSpeech.EngineInfo engineInfo = this.mEnginesHelper.getEngineInfo(engine);
            TtsEngineSettingsFragment.prepareArgs(this.mEngineSettingsPref.getExtras(), engineInfo.name, engineInfo.label, data);
        }
    }

    public Checkable getCurrentChecked() {
        return this.mCurrentChecked;
    }

    public String getCurrentKey() {
        return this.mCurrentEngine;
    }

    public void setCurrentChecked(Checkable current) {
        this.mCurrentChecked = current;
    }

    public void setCurrentKey(String key) {
        this.mCurrentEngine = key;
        updateDefaultEngine(this.mCurrentEngine);
    }

    public int getMetricsCategory() {
        return 94;
    }
}
