package com.android.tv.settings.system;

import android.content.Context;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.system.InputCustomNameFragment;
import java.util.Map;
import java.util.Set;

public class InputOptionsFragment extends SettingsPreferenceFragment implements InputCustomNameFragment.Callback {
    private static final String ARG_INPUT = "input";
    private static final String KEY_NAMES = "names";
    private static final String KEY_NAME_CUSTOM = "name_custom";
    private static final String KEY_NAME_DEFAULT = "name_default";
    private static final String KEY_SHOW_INPUT = "show_input";
    private Map<String, String> mCustomLabels;
    private Set<String> mHiddenIds;
    private TvInputInfo mInputInfo;
    private TwoStatePreference mNameCustomPref;
    private TwoStatePreference mNameDefaultPref;
    private PreferenceGroup mNamesGroup;
    private TwoStatePreference mShowPref;

    public static void prepareArgs(@NonNull Bundle args, TvInputInfo inputInfo) {
        args.putParcelable(ARG_INPUT, inputInfo);
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mInputInfo = (TvInputInfo) getArguments().getParcelable(ARG_INPUT);
        super.onCreate(savedInstanceState);
        Context context = getContext();
        this.mCustomLabels = TvInputInfo.TvInputSettings.getCustomLabels(context, 0);
        this.mHiddenIds = TvInputInfo.TvInputSettings.getHiddenTvInputIds(context, 0);
    }

    public void onResume() {
        super.onResume();
        refresh();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.input_options, (String) null);
        getPreferenceScreen().setTitle(this.mInputInfo.loadLabel(getContext()));
        this.mShowPref = (TwoStatePreference) findPreference(KEY_SHOW_INPUT);
        this.mNamesGroup = (PreferenceGroup) findPreference(KEY_NAMES);
        this.mNameDefaultPref = (TwoStatePreference) findPreference(KEY_NAME_DEFAULT);
        this.mNameCustomPref = (TwoStatePreference) findPreference(KEY_NAME_CUSTOM);
    }

    private void refresh() {
        this.mShowPref.setChecked(!this.mHiddenIds.contains(this.mInputInfo.getId()));
        CharSequence defaultLabel = this.mInputInfo.loadLabel(getContext());
        CharSequence customLabel = this.mCustomLabels.get(this.mInputInfo.getId());
        boolean nameMatched = false;
        for (int i = 0; i < this.mNamesGroup.getPreferenceCount(); i++) {
            TwoStatePreference namePref = (TwoStatePreference) this.mNamesGroup.getPreference(i);
            if (!TextUtils.equals(namePref.getKey(), KEY_NAME_DEFAULT) && !TextUtils.equals(namePref.getKey(), KEY_NAME_CUSTOM)) {
                boolean nameMatch = TextUtils.equals(namePref.getTitle(), customLabel);
                namePref.setChecked(nameMatch);
                nameMatched |= nameMatch;
            }
        }
        this.mNameDefaultPref.setTitle(defaultLabel);
        boolean nameIsDefault = TextUtils.isEmpty(customLabel);
        this.mNameDefaultPref.setChecked(nameIsDefault);
        InputCustomNameFragment.prepareArgs(this.mNameCustomPref.getExtras(), defaultLabel, nameIsDefault ? defaultLabel : customLabel);
        if (nameIsDefault || nameMatched) {
            this.mNameCustomPref.setChecked(false);
            this.mNameCustomPref.setSummary((CharSequence) null);
            return;
        }
        this.mNameCustomPref.setChecked(true);
        this.mNameCustomPref.setSummary(customLabel);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return super.onPreferenceTreeClick(preference);
        }
        if (preference instanceof RadioPreference) {
            RadioPreference radioPreference = (RadioPreference) preference;
            radioPreference.setChecked(true);
            radioPreference.clearOtherRadioPreferences(this.mNamesGroup);
            if (TextUtils.equals(key, KEY_NAME_CUSTOM)) {
                return super.onPreferenceTreeClick(preference);
            }
            if (TextUtils.equals(key, KEY_NAME_DEFAULT)) {
                setInputName((CharSequence) null);
                return true;
            }
            setInputName(preference.getTitle());
        }
        char c = 65535;
        if (key.hashCode() == 1116382216 && key.equals(KEY_SHOW_INPUT)) {
            c = 0;
        }
        if (c != 0) {
            return super.onPreferenceTreeClick(preference);
        }
        setInputVisible(((TwoStatePreference) preference).isChecked());
        return true;
    }

    private void setInputName(CharSequence name) {
        if (TextUtils.isEmpty(name)) {
            this.mCustomLabels.remove(this.mInputInfo.getId());
        } else {
            this.mCustomLabels.put(this.mInputInfo.getId(), name.toString());
        }
        TvInputInfo.TvInputSettings.putCustomLabels(getContext(), this.mCustomLabels, 0);
    }

    private void setInputVisible(boolean visible) {
        if ((!this.mHiddenIds.contains(this.mInputInfo.getId())) != visible) {
            if (visible) {
                this.mHiddenIds.remove(this.mInputInfo.getId());
            } else {
                this.mHiddenIds.add(this.mInputInfo.getId());
            }
            TvInputInfo.TvInputSettings.putHiddenTvInputs(getContext(), this.mHiddenIds, 0);
        }
    }

    public void onSetCustomName(CharSequence name) {
        setInputName(name);
    }

    public int getMetricsCategory() {
        return 1330;
    }
}
