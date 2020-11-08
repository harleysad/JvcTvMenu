package com.android.tv.settings.system.development;

import android.content.ContentResolver;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import com.android.tv.settings.R;

@Keep
public class CaptionCustomFragment extends LeanbackPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_BACKGROUND_OPACITY = "background_opacity";
    private static final String KEY_BACKGROUND_SHOW = "background_show";
    private static final String KEY_EDGE_COLOR = "edge_color";
    private static final String KEY_EDGE_TYPE = "edge_type";
    private static final String KEY_FONT_FAMILY = "font_family";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_TEXT_OPACITY = "text_opacity";
    private static final String KEY_WINDOW_COLOR = "window_color";
    private static final String KEY_WINDOW_OPACITY = "window_opacity";
    private static final String KEY_WINDOW_SHOW = "window_show";
    private ListPreference mBackgroundColorPref;
    private ListPreference mBackgroundOpacityPref;
    private TwoStatePreference mBackgroundShowPref;
    private ListPreference mEdgeColorPref;
    private ListPreference mEdgeTypePref;
    private ListPreference mFontFamilyPref;
    private ListPreference mTextColorPref;
    private ListPreference mTextOpacityPref;
    private ListPreference mWindowColorPref;
    private ListPreference mWindowOpacityPref;
    private TwoStatePreference mWindowShowPref;

    public void onResume() {
        super.onResume();
        refresh();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.caption_custom, (String) null);
        TypedArray ta = getResources().obtainTypedArray(R.array.captioning_color_selector_ids);
        int colorLen = ta.length();
        String[] namedColors = getResources().getStringArray(R.array.captioning_color_selector_titles);
        String[] colorNames = new String[colorLen];
        String[] colorValues = new String[colorLen];
        for (int i = 0; i < colorLen; i++) {
            int color = ta.getColor(i, 0);
            colorValues[i] = Integer.toHexString(color & ViewCompat.MEASURED_SIZE_MASK);
            if (i < namedColors.length) {
                colorNames[i] = namedColors[i];
            } else {
                colorNames[i] = String.format("#%06X", new Object[]{Integer.valueOf(16777215 & color)});
            }
        }
        ta.recycle();
        this.mFontFamilyPref = (ListPreference) findPreference(KEY_FONT_FAMILY);
        this.mFontFamilyPref.setOnPreferenceChangeListener(this);
        this.mTextColorPref = (ListPreference) findPreference(KEY_TEXT_COLOR);
        this.mTextColorPref.setEntries((CharSequence[]) colorNames);
        this.mTextColorPref.setEntryValues((CharSequence[]) colorValues);
        this.mTextColorPref.setOnPreferenceChangeListener(this);
        this.mTextOpacityPref = (ListPreference) findPreference(KEY_TEXT_OPACITY);
        this.mTextOpacityPref.setOnPreferenceChangeListener(this);
        this.mEdgeTypePref = (ListPreference) findPreference(KEY_EDGE_TYPE);
        this.mEdgeTypePref.setOnPreferenceChangeListener(this);
        this.mEdgeColorPref = (ListPreference) findPreference(KEY_EDGE_COLOR);
        this.mEdgeColorPref.setEntries((CharSequence[]) colorNames);
        this.mEdgeColorPref.setEntryValues((CharSequence[]) colorValues);
        this.mEdgeColorPref.setOnPreferenceChangeListener(this);
        this.mBackgroundShowPref = (TwoStatePreference) findPreference(KEY_BACKGROUND_SHOW);
        this.mBackgroundColorPref = (ListPreference) findPreference(KEY_BACKGROUND_COLOR);
        this.mBackgroundColorPref.setEntries((CharSequence[]) colorNames);
        this.mBackgroundColorPref.setEntryValues((CharSequence[]) colorValues);
        this.mBackgroundColorPref.setOnPreferenceChangeListener(this);
        this.mBackgroundOpacityPref = (ListPreference) findPreference(KEY_BACKGROUND_OPACITY);
        this.mBackgroundOpacityPref.setOnPreferenceChangeListener(this);
        this.mWindowShowPref = (TwoStatePreference) findPreference(KEY_WINDOW_SHOW);
        this.mWindowColorPref = (ListPreference) findPreference(KEY_WINDOW_COLOR);
        this.mWindowColorPref.setEntries((CharSequence[]) colorNames);
        this.mWindowColorPref.setEntryValues((CharSequence[]) colorValues);
        this.mWindowColorPref.setOnPreferenceChangeListener(this);
        this.mWindowOpacityPref = (ListPreference) findPreference(KEY_WINDOW_OPACITY);
        this.mWindowOpacityPref.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (TextUtils.isEmpty(key)) {
            return super.onPreferenceTreeClick(preference);
        }
        char c = 65535;
        int hashCode = key.hashCode();
        if (hashCode != 1313098606) {
            if (hashCode == 1914744300 && key.equals(KEY_WINDOW_SHOW)) {
                c = 1;
            }
        } else if (key.equals(KEY_BACKGROUND_SHOW)) {
            c = 0;
        }
        switch (c) {
            case 0:
                setCaptionsBackgroundVisible(((TwoStatePreference) preference).isChecked());
                return true;
            case 1:
                setCaptionsWindowVisible(((TwoStatePreference) preference).isChecked());
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (!TextUtils.isEmpty(key)) {
            char c = 65535;
            switch (key.hashCode()) {
                case -2115337775:
                    if (key.equals(KEY_TEXT_COLOR)) {
                        c = 1;
                        break;
                    }
                    break;
                case -1645805983:
                    if (key.equals(KEY_EDGE_COLOR)) {
                        c = 4;
                        break;
                    }
                    break;
                case -787039660:
                    if (key.equals(KEY_WINDOW_COLOR)) {
                        c = 7;
                        break;
                    }
                    break;
                case 224520316:
                    if (key.equals(KEY_EDGE_TYPE)) {
                        c = 3;
                        break;
                    }
                    break;
                case 758146809:
                    if (key.equals(KEY_TEXT_OPACITY)) {
                        c = 2;
                        break;
                    }
                    break;
                case 919004666:
                    if (key.equals(KEY_BACKGROUND_OPACITY)) {
                        c = 6;
                        break;
                    }
                    break;
                case 1534043476:
                    if (key.equals(KEY_FONT_FAMILY)) {
                        c = 0;
                        break;
                    }
                    break;
                case 1647348412:
                    if (key.equals(KEY_WINDOW_OPACITY)) {
                        c = 8;
                        break;
                    }
                    break;
                case 2036780306:
                    if (key.equals(KEY_BACKGROUND_COLOR)) {
                        c = 5;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    setCaptionsFontFamily((String) newValue);
                    break;
                case 1:
                    setCaptionsTextColor((String) newValue);
                    break;
                case 2:
                    setCaptionsTextOpacity((String) newValue);
                    break;
                case 3:
                    setCaptionsEdgeType((String) newValue);
                    break;
                case 4:
                    setCaptionsEdgeColor((String) newValue);
                    break;
                case 5:
                    setCaptionsBackgroundColor((String) newValue);
                    break;
                case 6:
                    setCaptionsBackgroundOpacity((String) newValue);
                    break;
                case 7:
                    setCaptionsWindowColor((String) newValue);
                    break;
                case 8:
                    setCaptionsWindowOpacity((String) newValue);
                    break;
                default:
                    throw new IllegalStateException("Preference change with unknown key " + key);
            }
            return true;
        }
        throw new IllegalStateException("Unknown preference change");
    }

    private void refresh() {
        this.mFontFamilyPref.setValue(getCaptionsFontFamily());
        this.mTextColorPref.setValue(getCaptionsTextColor());
        this.mTextOpacityPref.setValue(getCaptionsTextOpacity());
        this.mEdgeTypePref.setValue(getCaptionsEdgeType());
        this.mEdgeColorPref.setValue(getCaptionsEdgeColor());
        this.mBackgroundShowPref.setChecked(isCaptionsBackgroundVisible());
        setCaptionsBackgroundVisible(isCaptionsBackgroundVisible());
        this.mBackgroundColorPref.setValue(getCaptionsBackgroundColor());
        this.mBackgroundOpacityPref.setValue(getCaptionsBackgroundOpacity());
        this.mWindowShowPref.setChecked(isCaptionsWindowVisible());
        this.mWindowColorPref.setValue(getCaptionsWindowColor());
        this.mWindowOpacityPref.setValue(getCaptionsWindowOpacity());
    }

    private String getCaptionsFontFamily() {
        String typeface = Settings.Secure.getString(getContext().getContentResolver(), "accessibility_captioning_typeface");
        return TextUtils.isEmpty(typeface) ? "default" : typeface;
    }

    private void setCaptionsFontFamily(String fontFamily) {
        if (TextUtils.equals(fontFamily, "default")) {
            Settings.Secure.putString(getContext().getContentResolver(), "accessibility_captioning_typeface", (String) null);
        } else {
            Settings.Secure.putString(getContext().getContentResolver(), "accessibility_captioning_typeface", fontFamily);
        }
    }

    private String getCaptionsTextColor() {
        return Integer.toHexString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_foreground_color", -1) & ViewCompat.MEASURED_SIZE_MASK);
    }

    private void setCaptionsTextColor(String textColor) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_foreground_color", (((int) Long.parseLong(textColor, 16)) & ViewCompat.MEASURED_SIZE_MASK) | (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_foreground_color", ViewCompat.MEASURED_STATE_MASK) & ViewCompat.MEASURED_STATE_MASK));
    }

    private String getCaptionsTextOpacity() {
        return opacityToString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_foreground_color", 0) & ViewCompat.MEASURED_STATE_MASK);
    }

    private void setCaptionsTextOpacity(String textOpacity) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_foreground_color", (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_foreground_color", 0) & ViewCompat.MEASURED_SIZE_MASK) | (((int) Long.parseLong(textOpacity, 16)) & ViewCompat.MEASURED_STATE_MASK));
    }

    private String getCaptionsEdgeType() {
        return Integer.toString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_edge_type", 0));
    }

    private void setCaptionsEdgeType(String edgeType) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_edge_type", Integer.parseInt(edgeType));
    }

    private String getCaptionsEdgeColor() {
        return Integer.toHexString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_edge_color", 0) & ViewCompat.MEASURED_SIZE_MASK);
    }

    private void setCaptionsEdgeColor(String edgeColor) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_edge_color", ((int) Long.parseLong(edgeColor, 16)) | ViewCompat.MEASURED_STATE_MASK);
    }

    private boolean isCaptionsBackgroundVisible() {
        return (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_background_color", 0) & ViewCompat.MEASURED_STATE_MASK) != 0;
    }

    private void setCaptionsBackgroundVisible(boolean visible) {
        int i;
        ContentResolver contentResolver = getContext().getContentResolver();
        if (visible) {
            i = ViewCompat.MEASURED_STATE_MASK;
        } else {
            i = 0;
        }
        Settings.Secure.putInt(contentResolver, "accessibility_captioning_background_color", i);
        if (!visible) {
            this.mBackgroundColorPref.setValue(Integer.toHexString(0));
            this.mBackgroundOpacityPref.setValue(opacityToString(0));
        }
    }

    private String getCaptionsBackgroundColor() {
        return Integer.toHexString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_background_color", 0) & ViewCompat.MEASURED_SIZE_MASK);
    }

    private void setCaptionsBackgroundColor(String backgroundColor) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_background_color", (((int) Long.parseLong(backgroundColor, 16)) & ViewCompat.MEASURED_SIZE_MASK) | (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_background_color", ViewCompat.MEASURED_STATE_MASK) & ViewCompat.MEASURED_STATE_MASK));
    }

    private String getCaptionsBackgroundOpacity() {
        return opacityToString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_background_color", 0) & ViewCompat.MEASURED_STATE_MASK);
    }

    private void setCaptionsBackgroundOpacity(String backgroundOpacity) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_background_color", (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_background_color", 0) & ViewCompat.MEASURED_SIZE_MASK) | (((int) Long.parseLong(backgroundOpacity, 16)) & ViewCompat.MEASURED_STATE_MASK));
    }

    private boolean isCaptionsWindowVisible() {
        return (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_window_color", 0) & ViewCompat.MEASURED_STATE_MASK) != 0;
    }

    private void setCaptionsWindowVisible(boolean visible) {
        int i;
        ContentResolver contentResolver = getContext().getContentResolver();
        if (visible) {
            i = ViewCompat.MEASURED_STATE_MASK;
        } else {
            i = 0;
        }
        Settings.Secure.putInt(contentResolver, "accessibility_captioning_window_color", i);
        if (!visible) {
            this.mWindowColorPref.setValue(Integer.toHexString(0));
            this.mWindowOpacityPref.setValue(opacityToString(0));
        }
    }

    private String getCaptionsWindowColor() {
        return Integer.toHexString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_window_color", 0) & ViewCompat.MEASURED_SIZE_MASK);
    }

    private void setCaptionsWindowColor(String windowColor) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_window_color", (((int) Long.parseLong(windowColor, 16)) & ViewCompat.MEASURED_SIZE_MASK) | (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_window_color", ViewCompat.MEASURED_STATE_MASK) & ViewCompat.MEASURED_STATE_MASK));
    }

    private String getCaptionsWindowOpacity() {
        return opacityToString(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_window_color", 0) & ViewCompat.MEASURED_STATE_MASK);
    }

    private void setCaptionsWindowOpacity(String windowOpacity) {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_captioning_window_color", (Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_captioning_window_color", 0) & ViewCompat.MEASURED_SIZE_MASK) | (((int) Long.parseLong(windowOpacity, 16)) & ViewCompat.MEASURED_STATE_MASK));
    }

    private String opacityToString(int opacity) {
        int i = -16777216 & opacity;
        if (i == Integer.MIN_VALUE) {
            return "80FFFFFF";
        }
        if (i == -1073741824) {
            return "C0FFFFFF";
        }
        if (i != 1073741824) {
            return "FFFFFFFF";
        }
        return "40FFFFFF";
    }
}
