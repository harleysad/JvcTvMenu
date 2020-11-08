package com.android.tv.settings.partnercustomizer.visually;

import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class VisuallyImpairedAudioFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "VisuallyImpairedAudioFragment";
    private static final String VISUALLY_IMPAIRED_KEY = "visually_impaired_audio_key";
    private ListPreference listpre;
    /* access modifiers changed from: private */
    public TVSettingConfig mTVSettingConfig;
    Preference.OnPreferenceChangeListener mvisuallyimpairedChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            MtkLog.d(VisuallyImpairedAudioFragment.TAG, "onPreferenceChange " + preference + "," + preference.getKey() + "," + newValue);
            String preferenceKey = preference.getKey();
            if (preference.getKey().startsWith(TVSettingConfig.CFG_MENU_AUDIOINFO_GET_STRING)) {
                String[] spls = preference.getKey().split("_");
                if (spls == null || spls.length != 2) {
                    MtkLog.e(VisuallyImpairedAudioFragment.TAG, "mID is not correct:" + preference.getKey());
                } else {
                    int indexs = Integer.parseInt(spls[1]);
                    MtkLog.d(VisuallyImpairedAudioFragment.TAG, "set CFG_MENU_AUDIOINFO_GET_STRING" + indexs);
                    VisuallyImpairedAudioFragment.this.mTVSettingConfig.setConifg("g_menu__audioinfoselect", indexs);
                }
            }
            if (preferenceKey.startsWith(TVSettingConfig.CFG_MENU_AUDIOINFO_GET_STRING)) {
                for (int i = 0; i < VisuallyImpairedAudioFragment.this.getPreferenceScreen().getPreferenceCount(); i++) {
                    android.support.v7.preference.ListPreference listPreference = (android.support.v7.preference.ListPreference) VisuallyImpairedAudioFragment.this.getPreferenceScreen().getPreference(i);
                    if (listPreference.getKey() != preferenceKey) {
                        listPreference.setValue("1");
                        listPreference.setSummary("1");
                    }
                }
            }
            return true;
        }
    };
    int viIndex;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_visually_impaired_audio, (String) null);
        MtkLog.d(TAG, "onCreatePreferences");
        this.mTVSettingConfig = TVSettingConfig.getInstance(getActivity());
        initData();
    }

    private void initData() {
        Preference soundtrackItem;
        this.mTVSettingConfig.setConifg("g_menu__audioinfoinit", 0);
        int soundListsize = this.mTVSettingConfig.getConfigValueInt("g_menu__audioinfototal");
        this.viIndex = this.mTVSettingConfig.getConfigValueInt("g_menu__audioinfocurrent");
        Log.d("MENUAudioActivity", "soundListsize: " + soundListsize);
        for (int i = 0; i < soundListsize; i++) {
            String ItemName = "audioinfogetstring_" + i;
            String soundString = this.mTVSettingConfig.getConfigString(ItemName);
            MtkLog.d("MENUAudioActivity", ItemName + "VisuallyImpaired:" + soundString);
            String[] itemValueStrings = new String[3];
            if (soundString != null) {
                itemValueStrings[0] = soundString;
                itemValueStrings[1] = "";
                itemValueStrings[2] = "";
            } else {
                itemValueStrings[0] = "";
                itemValueStrings[1] = "";
                itemValueStrings[2] = "";
            }
            if (this.viIndex == i) {
                soundtrackItem = createListPreference(ItemName, new String("" + (i + 1)), true, new String[]{soundString}, 0);
            } else {
                soundtrackItem = createListPreference(ItemName, new String("" + (i + 1)), true, new String[]{soundString}, 1);
            }
            getPreferenceScreen().addPreference(soundtrackItem);
        }
    }

    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        MtkLog.d(TAG, "key==" + arg0.getKey() + ",  " + arg1);
        return true;
    }

    public Preference createListPreference(String key, String title, boolean status, String[] entries, int defValue) {
        android.support.v7.preference.ListPreference preference = createListPreferenceInternal(key, status, entries, getCharSequence(entries.length), String.valueOf(defValue));
        preference.setTitle((CharSequence) title);
        preference.setDialogTitle((CharSequence) title);
        return preference;
    }

    private android.support.v7.preference.ListPreference createListPreferenceInternal(String key, boolean status, String[] entries, String[] entryValues, String defValue) {
        android.support.v7.preference.ListPreference preference = new android.support.v7.preference.ListPreference(getPreferenceManager().getContext());
        preference.setKey(key);
        preference.setPersistent(false);
        preference.setEnabled(status);
        preference.setEntries((CharSequence[]) entries);
        preference.setEntryValues((CharSequence[]) entryValues);
        preference.setValue(defValue);
        for (int i = 0; i < entryValues.length; i++) {
            if (entryValues[i].equals(defValue)) {
                preference.setSummary(entries[i]);
            }
        }
        preference.setOnPreferenceChangeListener(this.mvisuallyimpairedChangeListener);
        return preference;
    }

    public void onResume() {
        super.onResume();
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference tempPre = getPreferenceScreen().getPreference(i);
            if (tempPre instanceof android.support.v7.preference.ListPreference) {
                android.support.v7.preference.ListPreference tmp = (android.support.v7.preference.ListPreference) tempPre;
                MtkLog.d(TAG, "set CFG_MENU_AUDIOINFO_GET_STRING==" + tmp.getKey());
                tmp.setSummary(tmp.getEntries()[this.mTVSettingConfig.getConfigValueInt(tmp.getKey())]);
            }
        }
    }

    public static String[] getCharSequence(int size) {
        String[] seq = new String[size];
        for (int i = 0; i < size; i++) {
            seq[i] = String.valueOf(i);
        }
        return seq;
    }

    public int getMetricsCategory() {
        return 336;
    }
}
