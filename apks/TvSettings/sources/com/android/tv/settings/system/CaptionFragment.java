package com.android.tv.settings.system;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import com.android.internal.app.LocalePicker;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.List;

public class CaptionFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String KEY_CAPTIONS_DISPLAY = "captions_display";
    private static final String KEY_CAPTIONS_LANGUAGE = "captions_language";
    private static final String KEY_CAPTIONS_STYLE_0 = "captions_style_0";
    private static final String KEY_CAPTIONS_STYLE_1 = "captions_style_1";
    private static final String KEY_CAPTIONS_STYLE_2 = "captions_style_2";
    private static final String KEY_CAPTIONS_STYLE_3 = "captions_style_3";
    private static final String KEY_CAPTIONS_STYLE_CUSTOM = "captions_style_custom";
    private static final String KEY_CAPTIONS_STYLE_GROUP = "captions_style";
    private static final String KEY_CAPTIONS_TEXT_SIZE = "captions_text_size";
    private TwoStatePreference mCaptionsDisplayPref;
    private ListPreference mCaptionsLanguagePref;
    private RadioPreference mCaptionsStyle0Pref;
    private RadioPreference mCaptionsStyle1Pref;
    private RadioPreference mCaptionsStyle2Pref;
    private RadioPreference mCaptionsStyle3Pref;
    private RadioPreference mCaptionsStyleCustomPref;
    private PreferenceGroup mCaptionsStyleGroup;
    private ListPreference mCaptionsTextSizePref;

    public static CaptionFragment newInstance() {
        return new CaptionFragment();
    }

    public void onResume() {
        super.onResume();
        refresh();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.caption, (String) null);
        this.mCaptionsDisplayPref = (TwoStatePreference) findPreference(KEY_CAPTIONS_DISPLAY);
        List<LocalePicker.LocaleInfo> localeInfoList = LocalePicker.getAllAssetLocales(getContext(), false);
        String[] langNames = new String[(localeInfoList.size() + 1)];
        String[] langLocales = new String[(localeInfoList.size() + 1)];
        langNames[0] = getString(R.string.captions_language_default);
        langLocales[0] = "";
        int i = 1;
        for (LocalePicker.LocaleInfo info : localeInfoList) {
            langNames[i] = info.toString();
            langLocales[i] = info.getLocale().toString();
            i++;
        }
        this.mCaptionsLanguagePref = (ListPreference) findPreference(KEY_CAPTIONS_LANGUAGE);
        this.mCaptionsLanguagePref.setEntries((CharSequence[]) langNames);
        this.mCaptionsLanguagePref.setEntryValues((CharSequence[]) langLocales);
        this.mCaptionsLanguagePref.setOnPreferenceChangeListener(this);
        this.mCaptionsTextSizePref = (ListPreference) findPreference(KEY_CAPTIONS_TEXT_SIZE);
        this.mCaptionsTextSizePref.setOnPreferenceChangeListener(this);
        this.mCaptionsStyleGroup = (PreferenceGroup) findPreference(KEY_CAPTIONS_STYLE_GROUP);
        this.mCaptionsStyle0Pref = (RadioPreference) findPreference(KEY_CAPTIONS_STYLE_0);
        this.mCaptionsStyle1Pref = (RadioPreference) findPreference(KEY_CAPTIONS_STYLE_1);
        this.mCaptionsStyle2Pref = (RadioPreference) findPreference(KEY_CAPTIONS_STYLE_2);
        this.mCaptionsStyle3Pref = (RadioPreference) findPreference(KEY_CAPTIONS_STYLE_3);
        this.mCaptionsStyleCustomPref = (RadioPreference) findPreference(KEY_CAPTIONS_STYLE_CUSTOM);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceTreeClick(android.support.v7.preference.Preference r9) {
        /*
            r8 = this;
            java.lang.String r0 = r9.getKey()
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x000f
            boolean r1 = super.onPreferenceTreeClick(r9)
            return r1
        L_0x000f:
            int r1 = r0.hashCode()
            r2 = 1702544848(0x657ac5d0, float:7.401504E22)
            r3 = 0
            r4 = 2
            r5 = 3
            r6 = -1
            r7 = 1
            if (r1 == r2) goto L_0x0058
            r2 = 1718820209(0x66731d71, float:2.8701954E23)
            if (r1 == r2) goto L_0x004e
            switch(r1) {
                case -1844433360: goto L_0x0044;
                case -1844433359: goto L_0x003a;
                case -1844433358: goto L_0x0030;
                case -1844433357: goto L_0x0026;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x0062
        L_0x0026:
            java.lang.String r1 = "captions_style_3"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0062
            r1 = 4
            goto L_0x0063
        L_0x0030:
            java.lang.String r1 = "captions_style_2"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0062
            r1 = r5
            goto L_0x0063
        L_0x003a:
            java.lang.String r1 = "captions_style_1"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0062
            r1 = r4
            goto L_0x0063
        L_0x0044:
            java.lang.String r1 = "captions_style_0"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0062
            r1 = r7
            goto L_0x0063
        L_0x004e:
            java.lang.String r1 = "captions_style_custom"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0062
            r1 = 5
            goto L_0x0063
        L_0x0058:
            java.lang.String r1 = "captions_display"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0062
            r1 = r3
            goto L_0x0063
        L_0x0062:
            r1 = r6
        L_0x0063:
            switch(r1) {
                case 0: goto L_0x00bf;
                case 1: goto L_0x00af;
                case 2: goto L_0x009f;
                case 3: goto L_0x008f;
                case 4: goto L_0x007f;
                case 5: goto L_0x006b;
                default: goto L_0x0066;
            }
        L_0x0066:
            boolean r1 = super.onPreferenceTreeClick(r9)
            return r1
        L_0x006b:
            r8.setCaptionsStyle(r6)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyleCustomPref
            r1.setChecked(r7)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyleCustomPref
            android.support.v7.preference.PreferenceGroup r2 = r8.mCaptionsStyleGroup
            r1.clearOtherRadioPreferences(r2)
            boolean r1 = super.onPreferenceTreeClick(r9)
            return r1
        L_0x007f:
            r8.setCaptionsStyle(r5)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle3Pref
            r1.setChecked(r7)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle3Pref
            android.support.v7.preference.PreferenceGroup r2 = r8.mCaptionsStyleGroup
            r1.clearOtherRadioPreferences(r2)
            return r7
        L_0x008f:
            r8.setCaptionsStyle(r4)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle2Pref
            r1.setChecked(r7)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle2Pref
            android.support.v7.preference.PreferenceGroup r2 = r8.mCaptionsStyleGroup
            r1.clearOtherRadioPreferences(r2)
            return r7
        L_0x009f:
            r8.setCaptionsStyle(r7)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle1Pref
            r1.setChecked(r7)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle1Pref
            android.support.v7.preference.PreferenceGroup r2 = r8.mCaptionsStyleGroup
            r1.clearOtherRadioPreferences(r2)
            return r7
        L_0x00af:
            r8.setCaptionsStyle(r3)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle0Pref
            r1.setChecked(r7)
            com.android.tv.settings.RadioPreference r1 = r8.mCaptionsStyle0Pref
            android.support.v7.preference.PreferenceGroup r2 = r8.mCaptionsStyleGroup
            r1.clearOtherRadioPreferences(r2)
            return r7
        L_0x00bf:
            r1 = r9
            android.support.v7.preference.TwoStatePreference r1 = (android.support.v7.preference.TwoStatePreference) r1
            boolean r1 = r1.isChecked()
            r8.setCaptionsEnabled(r1)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.system.CaptionFragment.onPreferenceTreeClick(android.support.v7.preference.Preference):boolean");
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (!TextUtils.isEmpty(key)) {
            char c = 65535;
            int hashCode = key.hashCode();
            if (hashCode != -1522128543) {
                if (hashCode == -659388406 && key.equals(KEY_CAPTIONS_LANGUAGE)) {
                    c = 0;
                }
            } else if (key.equals(KEY_CAPTIONS_TEXT_SIZE)) {
                c = 1;
            }
            switch (c) {
                case 0:
                    setCaptionsLocale((String) newValue);
                    break;
                case 1:
                    setCaptionsTextSize((String) newValue);
                    break;
                default:
                    throw new IllegalStateException("Preference change with unknown key " + key);
            }
            return true;
        }
        throw new IllegalStateException("Unknown preference change");
    }

    private void refresh() {
        this.mCaptionsDisplayPref.setChecked(getCaptionsEnabled());
        this.mCaptionsLanguagePref.setValue(getCaptionsLocale());
        this.mCaptionsTextSizePref.setValue(getCaptionsTextSize());
        int captionsStyle = getCaptionsStyle();
        if (captionsStyle != -1) {
            switch (captionsStyle) {
                case 1:
                    this.mCaptionsStyle1Pref.setChecked(true);
                    this.mCaptionsStyle1Pref.clearOtherRadioPreferences(this.mCaptionsStyleGroup);
                    return;
                case 2:
                    this.mCaptionsStyle2Pref.setChecked(true);
                    this.mCaptionsStyle2Pref.clearOtherRadioPreferences(this.mCaptionsStyleGroup);
                    return;
                case 3:
                    this.mCaptionsStyle3Pref.setChecked(true);
                    this.mCaptionsStyle3Pref.clearOtherRadioPreferences(this.mCaptionsStyleGroup);
                    return;
                default:
                    this.mCaptionsStyle0Pref.setChecked(true);
                    this.mCaptionsStyle0Pref.clearOtherRadioPreferences(this.mCaptionsStyleGroup);
                    return;
            }
        } else {
            this.mCaptionsStyleCustomPref.setChecked(true);
            this.mCaptionsStyleCustomPref.clearOtherRadioPreferences(this.mCaptionsStyleGroup);
        }
    }

    private boolean getCaptionsEnabled() {
        return Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_enabled", 0) != 0;
    }

    private void setCaptionsEnabled(boolean enabled) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_enabled", enabled);
    }

    private int getCaptionsStyle() {
        return Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_preset", 0);
    }

    private void setCaptionsStyle(int style) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_preset", style);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(CaptionSettingsFragment.ACTION_REFRESH_CAPTIONS_PREVIEW));
    }

    private String getCaptionsLocale() {
        String captionLocale = Settings.Secure.getString(getContext().getContentResolver(), "accessibility_captioning_locale");
        return captionLocale == null ? "" : captionLocale;
    }

    private void setCaptionsLocale(String locale) {
        Settings.Secure.putString(getContext().getContentResolver(), "accessibility_captioning_locale", locale);
    }

    private String getCaptionsTextSize() {
        float textSize = Settings.Secure.getFloat(getContext().getContentResolver(), "accessibility_captioning_font_scale", 1.0f);
        if (0.0f <= textSize && ((double) textSize) < 0.375d) {
            return "0.25";
        }
        if (((double) textSize) < 0.75d) {
            return "0.5";
        }
        if (((double) textSize) < 1.25d) {
            return "1.0";
        }
        if (((double) textSize) < 1.75d) {
            return "1.5";
        }
        if (((double) textSize) < 2.5d) {
            return "2.0";
        }
        return "1.0";
    }

    private void setCaptionsTextSize(String textSize) {
        Settings.Secure.putFloat(getContext().getContentResolver(), "accessibility_captioning_font_scale", Float.parseFloat(textSize));
    }

    public int getMetricsCategory() {
        return 3;
    }
}
