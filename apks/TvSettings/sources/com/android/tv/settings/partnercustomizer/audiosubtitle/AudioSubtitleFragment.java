package com.android.tv.settings.partnercustomizer.audiosubtitle;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.twoworlds.tv.MtkTvHBBTVBase;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Locale;

public class AudioSubtitleFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "AudioSubtitleFragment";
    int actualtotal;
    int actualtotalaudio;
    MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[] defalutoffsub;
    int defaultaudiovalue;
    MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[] defaultoff;
    int defaultvalue;
    MtkTvHBBTVBase hbbtv;
    private Context mContext;
    /* access modifiers changed from: private */
    public String mStartKey;
    Preference.OnPreferenceChangeListener mvisuallyimpairedChangeListener;
    int nmemb;
    int[] ntotal;
    MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[] strmSbtl;
    MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[] strmaudio;

    public static AudioSubtitleFragment getInstance() {
        return new AudioSubtitleFragment();
    }

    public static AudioSubtitleFragment getInstance(String key) {
        return new AudioSubtitleFragment(key);
    }

    public AudioSubtitleFragment() {
        this.mStartKey = null;
        this.defaultoff = new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[1];
        this.defalutoffsub = new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[1];
        this.hbbtv = new MtkTvHBBTVBase();
        this.nmemb = 15;
        this.ntotal = new int[]{0};
        this.defaultvalue = -1;
        this.defaultaudiovalue = -1;
        this.actualtotal = 1;
        this.actualtotalaudio = 1;
        this.mvisuallyimpairedChangeListener = new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MtkLog.d(AudioSubtitleFragment.TAG, "onPreferenceChange " + preference + "," + preference.getKey() + "," + newValue);
                int value = Integer.valueOf((String) newValue).intValue();
                if (preference.getKey().equals(PreferenceConfigUtils.KEY_SUBTITLE)) {
                    if (value == AudioSubtitleFragment.this.actualtotal - 1) {
                        AudioSubtitleFragment.this.hbbtv.hbbtvSubtitleSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[]{AudioSubtitleFragment.this.defalutoffsub[0]}, 1);
                    } else {
                        AudioSubtitleFragment.this.hbbtv.hbbtvSubtitleSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[]{AudioSubtitleFragment.this.strmSbtl[value]}, 1);
                    }
                } else if (value == AudioSubtitleFragment.this.actualtotalaudio - 1) {
                    AudioSubtitleFragment.this.hbbtv.hbbtvAudioSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[]{AudioSubtitleFragment.this.defaultoff[0]}, 1);
                } else {
                    AudioSubtitleFragment.this.hbbtv.hbbtvAudioSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[]{AudioSubtitleFragment.this.strmaudio[value]}, 1);
                }
                ListPreference pre = (ListPreference) preference;
                pre.setSummary(pre.getEntries()[value]);
                pre.setValueIndex(value);
                if (AudioSubtitleFragment.this.mStartKey != null) {
                    Log.d(AudioSubtitleFragment.TAG, "send KEYCODE_BACK");
                    InstrumentationHandler.getInstance().sendKeyDownUpSync(4);
                }
                return true;
            }
        };
        this.mStartKey = null;
    }

    public AudioSubtitleFragment(String key) {
        this.mStartKey = null;
        this.defaultoff = new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[1];
        this.defalutoffsub = new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[1];
        this.hbbtv = new MtkTvHBBTVBase();
        this.nmemb = 15;
        this.ntotal = new int[]{0};
        this.defaultvalue = -1;
        this.defaultaudiovalue = -1;
        this.actualtotal = 1;
        this.actualtotalaudio = 1;
        this.mvisuallyimpairedChangeListener = new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MtkLog.d(AudioSubtitleFragment.TAG, "onPreferenceChange " + preference + "," + preference.getKey() + "," + newValue);
                int value = Integer.valueOf((String) newValue).intValue();
                if (preference.getKey().equals(PreferenceConfigUtils.KEY_SUBTITLE)) {
                    if (value == AudioSubtitleFragment.this.actualtotal - 1) {
                        AudioSubtitleFragment.this.hbbtv.hbbtvSubtitleSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[]{AudioSubtitleFragment.this.defalutoffsub[0]}, 1);
                    } else {
                        AudioSubtitleFragment.this.hbbtv.hbbtvSubtitleSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[]{AudioSubtitleFragment.this.strmSbtl[value]}, 1);
                    }
                } else if (value == AudioSubtitleFragment.this.actualtotalaudio - 1) {
                    AudioSubtitleFragment.this.hbbtv.hbbtvAudioSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[]{AudioSubtitleFragment.this.defaultoff[0]}, 1);
                } else {
                    AudioSubtitleFragment.this.hbbtv.hbbtvAudioSetActive(new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[]{AudioSubtitleFragment.this.strmaudio[value]}, 1);
                }
                ListPreference pre = (ListPreference) preference;
                pre.setSummary(pre.getEntries()[value]);
                pre.setValueIndex(value);
                if (AudioSubtitleFragment.this.mStartKey != null) {
                    Log.d(AudioSubtitleFragment.TAG, "send KEYCODE_BACK");
                    InstrumentationHandler.getInstance().sendKeyDownUpSync(4);
                }
                return true;
            }
        };
        this.mStartKey = key;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
    }

    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        showPreferenceFromStartkey();
    }

    private void showPreferenceFromStartkey() {
        MtkLog.d(TAG, "showPreferenceFromStartkey,mStartKey==" + this.mStartKey);
        if (this.mStartKey != null) {
            String str = this.mStartKey;
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != -1658858429) {
                if (hashCode == 774299009 && str.equals(PreferenceConfigUtils.KEY_AUDIO)) {
                    c = 0;
                }
            } else if (str.equals(PreferenceConfigUtils.KEY_SUBTITLE)) {
                c = 1;
            }
            switch (c) {
                case 0:
                case 1:
                    ListPreference startPreference = (ListPreference) findPreference(this.mStartKey);
                    startPreference.getPreferenceManager().showDialog(startPreference);
                    return;
                default:
                    return;
            }
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MtkLog.d(TAG, "onCreatePreferences");
        setPreferencesFromResource(R.xml.partner_audio_subtitle, (String) null);
        this.mContext = getContext();
        initData();
    }

    private void initData() {
        this.strmSbtl = new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[this.nmemb];
        this.strmaudio = new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[this.nmemb];
        this.defaultoff[0] = new MtkTvHBBTVBase.MtkTvHbbTVStreamAudio();
        this.defalutoffsub[0] = new MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle();
        this.defaultoff[0].index = -1;
        this.defaultoff[0].active = false;
        this.defalutoffsub[0].index = -1;
        this.defalutoffsub[0].active = false;
        this.hbbtv.hbbtvAudioGetList(this.strmaudio, this.nmemb, this.ntotal);
        MtkLog.d(TAG, "" + String.valueOf(this.strmaudio[0].lang));
        getPreferenceScreen().addPreference(getAudio(this.strmaudio));
        this.actualtotalaudio = this.ntotal[0] + 1;
        this.hbbtv.hbbtvSubtitleGetList(this.strmSbtl, this.nmemb, this.ntotal);
        getPreferenceScreen().addPreference(getSubtitle(this.strmSbtl));
        this.actualtotal = this.ntotal[0] + 1;
        MtkLog.d(TAG, this.actualtotalaudio + ",strmsbtl==" + this.strmSbtl + ",,strmaudio==" + this.strmaudio + ",,total=" + this.actualtotal);
    }

    private Preference getAudio(MtkTvHBBTVBase.MtkTvHbbTVStreamAudio[] audio) {
        String[] totalStrings = new String[(this.ntotal[0] + 1)];
        if (this.strmaudio == null) {
            MtkLog.d(TAG, "hbbtvaudioGetList is null");
            return null;
        }
        MtkLog.d(TAG, "total" + totalStrings.length + "===" + this.strmaudio.length);
        for (int i = 0; i < this.ntotal[0]; i++) {
            if (!(this.strmaudio[i] == null || this.strmaudio[i].lang == null)) {
                totalStrings[i] = String.valueOf(this.strmaudio[i].lang);
                Locale locale = new Locale(totalStrings[i]);
                MtkLog.d(TAG, "audio=" + totalStrings[i] + ",,audio real=" + locale.getDisplayLanguage());
            }
            if (this.strmaudio[i].active) {
                this.defaultaudiovalue = i;
            }
        }
        if (totalStrings.length != 0) {
            totalStrings[totalStrings.length - 1] = this.mContext.getResources().getString(R.string.pic_advance_video_entries_off);
        }
        if (this.defaultaudiovalue == -1) {
            this.defaultaudiovalue = totalStrings.length - 1;
        }
        MtkLog.d(TAG, "defaultaudiovalue==" + this.defaultaudiovalue);
        return createListPreference(PreferenceConfigUtils.KEY_AUDIO, "Audio", true, totalStrings, this.defaultaudiovalue);
    }

    private Preference getSubtitle(MtkTvHBBTVBase.MtkTvHbbTVStreamSubtitle[] subtitle) {
        String[] totalStrings = new String[(this.ntotal[0] + 1)];
        if (this.strmSbtl == null) {
            MtkLog.d(TAG, "hbbtvsubtitleGetList is null");
            return null;
        }
        for (int i = 0; i < this.ntotal[0]; i++) {
            if (this.strmSbtl[i].lang != null) {
                totalStrings[i] = String.valueOf(this.strmSbtl[i].lang);
                Locale locale = new Locale(totalStrings[i]);
                MtkLog.d(TAG, "subtitle=" + totalStrings[i] + ",,real==" + locale.getDisplayLanguage());
            }
            if (this.strmSbtl[i].active) {
                this.defaultvalue = i;
            }
        }
        if (totalStrings.length != 0) {
            totalStrings[totalStrings.length - 1] = this.mContext.getResources().getString(R.string.pic_advance_video_entries_off);
        }
        if (this.defaultvalue == -1) {
            this.defaultvalue = totalStrings.length - 1;
        }
        MtkLog.d(TAG, "defaultvalue==" + this.defaultvalue);
        return createListPreference(PreferenceConfigUtils.KEY_SUBTITLE, "Subtitle", true, totalStrings, this.defaultvalue);
    }

    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        MtkLog.d(TAG, "key==" + arg0.getKey() + ",  " + arg1);
        return true;
    }

    public Preference createListPreference(String key, String title, boolean status, String[] entries, int defValue) {
        ListPreference preference = createListPreferenceInternal(key, status, entries, getCharSequence(entries.length), String.valueOf(defValue));
        preference.setTitle((CharSequence) title);
        preference.setDialogTitle((CharSequence) title);
        return preference;
    }

    private ListPreference createListPreferenceInternal(String key, boolean status, String[] entries, String[] entryValues, String defValue) {
        ListPreference preference = new ListPreference(getPreferenceManager().getContext());
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
