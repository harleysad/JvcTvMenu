package com.android.tv.settings.system;

import android.content.Context;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InputsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String KEY_CONNECTED_INPUTS = "connected_inputs";
    private static final String KEY_DEVICE_AUTO_OFF = "device_auto_off";
    private static final String KEY_DISCONNECTED_INPUTS = "disconnected_inputs";
    private static final String KEY_HDMI_CONTROL = "hdmi_control";
    private static final String KEY_STANDBY_INPUTS = "standby_inputs";
    private static final String KEY_TV_AUTO_ON = "tv_auto_on";
    private static final String TAG = "InputsFragment";
    private PreferenceGroup mConnectedGroup;
    private Context mContext;
    /* access modifiers changed from: private */
    public Map<String, String> mCustomLabels;
    private TwoStatePreference mDeviceAutoOffPref;
    private PreferenceGroup mDisconnectedGroup;
    private TwoStatePreference mHdmiControlPref;
    /* access modifiers changed from: private */
    public Set<String> mHiddenIds;
    private boolean mIsHDMISource = true;
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private PreferenceGroup mStandbyGroup;
    private TVSettingConfig mTVSettingConfig;
    private TwoStatePreference mTvAutoOnPref;
    private TvInputManager mTvInputManager;

    public static InputsFragment newInstance() {
        return new InputsFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTvInputManager = (TvInputManager) getContext().getSystemService("tv_input");
    }

    public void onResume() {
        super.onResume();
        Context context = getContext();
        this.mCustomLabels = TvInputInfo.TvInputSettings.getCustomLabels(context, 0);
        this.mHiddenIds = TvInputInfo.TvInputSettings.getHiddenTvInputIds(context, 0);
        refresh();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.inputs, (String) null);
        this.mConnectedGroup = (PreferenceGroup) findPreference(KEY_CONNECTED_INPUTS);
        this.mStandbyGroup = (PreferenceGroup) findPreference(KEY_STANDBY_INPUTS);
        this.mDisconnectedGroup = (PreferenceGroup) findPreference(KEY_DISCONNECTED_INPUTS);
        this.mHdmiControlPref = (TwoStatePreference) findPreference(KEY_HDMI_CONTROL);
        this.mDeviceAutoOffPref = (TwoStatePreference) findPreference(KEY_DEVICE_AUTO_OFF);
        this.mTvAutoOnPref = (TwoStatePreference) findPreference(KEY_TV_AUTO_ON);
        this.mContext = getContext();
        this.mTVSettingConfig = TVSettingConfig.getInstance(getContext());
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        initalMtkPref();
    }

    private void refresh() {
        boolean hc = readCecOption("hdmi_control_enabled");
        this.mHdmiControlPref.setChecked(hc);
        if (!hc) {
            this.mDeviceAutoOffPref.setChecked(hc);
            this.mTvAutoOnPref.setChecked(hc);
        } else {
            this.mDeviceAutoOffPref.setChecked(readCecOption("hdmi_control_auto_device_off_enabled"));
            this.mTvAutoOnPref.setChecked(readCecOption("hdmi_control_auto_wakeup_enabled"));
        }
        this.mDeviceAutoOffPref.setEnabled(hc);
        this.mTvAutoOnPref.setEnabled(hc);
        for (TvInputInfo info : this.mTvInputManager.getTvInputList()) {
            if (info.getType() != 0 && TextUtils.isEmpty(info.getParentId())) {
                try {
                    int state = this.mTvInputManager.getInputState(info.getId());
                    InputPreference inputPref = (InputPreference) findPreference(makeInputPrefKey(info));
                    if (inputPref == null) {
                        inputPref = new InputPreference(getPreferenceManager().getContext());
                    }
                    inputPref.refresh(info);
                    switch (state) {
                        case 0:
                            this.mConnectedGroup.addPreference(inputPref);
                            this.mStandbyGroup.removePreference(inputPref);
                            this.mDisconnectedGroup.removePreference(inputPref);
                            break;
                        case 1:
                            this.mConnectedGroup.removePreference(inputPref);
                            this.mStandbyGroup.addPreference(inputPref);
                            this.mDisconnectedGroup.removePreference(inputPref);
                            break;
                        case 2:
                            this.mConnectedGroup.removePreference(inputPref);
                            this.mStandbyGroup.removePreference(inputPref);
                            this.mDisconnectedGroup.addPreference(inputPref);
                            break;
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        }
        int connectedCount = this.mConnectedGroup.getPreferenceCount();
        this.mConnectedGroup.setTitle((CharSequence) getResources().getQuantityString(R.plurals.inputs_header_connected_input, connectedCount));
        boolean z = false;
        this.mConnectedGroup.setVisible(connectedCount > 0);
        int standbyCount = this.mStandbyGroup.getPreferenceCount();
        this.mStandbyGroup.setTitle((CharSequence) getResources().getQuantityString(R.plurals.inputs_header_standby_input, standbyCount));
        this.mStandbyGroup.setVisible(standbyCount > 0);
        int disconnectedCount = this.mDisconnectedGroup.getPreferenceCount();
        this.mDisconnectedGroup.setTitle((CharSequence) getResources().getQuantityString(R.plurals.inputs_header_disconnected_input, disconnectedCount));
        PreferenceGroup preferenceGroup = this.mDisconnectedGroup;
        if (disconnectedCount > 0) {
            z = true;
        }
        preferenceGroup.setVisible(z);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return super.onPreferenceTreeClick(preference);
        }
        char c = 65535;
        int hashCode = key.hashCode();
        if (hashCode != -1626464600) {
            if (hashCode != -501301322) {
                if (hashCode == 1859917234 && key.equals(KEY_TV_AUTO_ON)) {
                    c = 2;
                }
            } else if (key.equals(KEY_HDMI_CONTROL)) {
                c = 0;
            }
        } else if (key.equals(KEY_DEVICE_AUTO_OFF)) {
            c = 1;
        }
        switch (c) {
            case 0:
                boolean hc = this.mHdmiControlPref.isChecked();
                writeCecOption("hdmi_control_enabled", this.mHdmiControlPref.isChecked());
                this.mDeviceAutoOffPref.setChecked(hc);
                this.mTvAutoOnPref.setChecked(hc);
                this.mDeviceAutoOffPref.setEnabled(hc);
                this.mTvAutoOnPref.setEnabled(hc);
                writeCecOption("hdmi_control_auto_device_off_enabled", hc);
                writeCecOption("hdmi_control_auto_wakeup_enabled", hc);
                return true;
            case 1:
                writeCecOption("hdmi_control_auto_device_off_enabled", this.mDeviceAutoOffPref.isChecked());
                return true;
            case 2:
                writeCecOption("hdmi_control_auto_wakeup_enabled", this.mTvAutoOnPref.isChecked());
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    private void initalMtkPref() {
        createPreferences();
        ListPreference hdmiEdidVersionPref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_TV_HDMI_EDID_VERSION);
        if (hdmiEdidVersionPref != null) {
            hdmiEdidVersionPref.setEnabled(this.mPreferenceConfigUtils.isHDMISource(getContext()));
        }
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_DEVICE_INPUTS);
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e(TAG, "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_TV_HDMI_EDID_VERSION)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_inputs_header_hdmi_edid_version, (int) R.array.input_hdmi_edid_version_entries, (int) R.array.input_hdmi_edid_version_entries_values, (String) null));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_INPUTS_CECLIST)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.s_cec_dev_list, "com.android.tv.settings.partnercustomizer.inputs.CecDeviceListFragment"));
                }
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        Log.e(TAG, "onPreferenceChange preference " + preferenceKey);
        Log.d(TAG, "onPreferenceChange newValue == " + ((String) newValue));
        if (((preferenceKey.hashCode() == 1143250152 && preferenceKey.equals(PreferenceConfigUtils.KEY_TV_HDMI_EDID_VERSION)) ? (char) 0 : 65535) != 0) {
            return true;
        }
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return true;
    }

    private boolean readCecOption(String key) {
        return Settings.Global.getInt(getContext().getContentResolver(), key, 1) == 1;
    }

    private void writeCecOption(String key, boolean value) {
        Settings.Global.putInt(getContext().getContentResolver(), key, value);
    }

    private class InputPreference extends Preference {
        public InputPreference(Context context) {
            super(context);
        }

        public void refresh(TvInputInfo inputInfo) {
            String customLabel;
            setKey(InputsFragment.makeInputPrefKey(inputInfo));
            setTitle(inputInfo.loadLabel(getContext()));
            if (InputsFragment.this.mHiddenIds.contains(inputInfo.getId())) {
                customLabel = InputsFragment.this.getString(R.string.inputs_hide);
            } else {
                customLabel = (String) InputsFragment.this.mCustomLabels.get(inputInfo.getId());
                if (TextUtils.isEmpty(customLabel)) {
                    customLabel = inputInfo.loadLabel(getContext()).toString();
                }
            }
            setSummary((CharSequence) customLabel);
            setFragment(InputOptionsFragment.class.getName());
            InputOptionsFragment.prepareArgs(getExtras(), inputInfo);
        }
    }

    public static String makeInputPrefKey(TvInputInfo inputInfo) {
        return "InputPref:" + inputInfo.getId();
    }

    public int getMetricsCategory() {
        return 1299;
    }
}
