package com.android.tv.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Keep
public class AccessibilityFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String AC4DE_KEY = "ac4_de";
    private static final String ACCESSIBILITY_SERVICES_KEY = "system_accessibility_services";
    private static final String AUDIO_DESCRIPTION_KEY = "audio_description";
    private static final String HEARING_IMPAIRED_KEY = "hearing_impaired";
    private static final String TOGGLE_HIGH_TEXT_CONTRAST_KEY = "toggle_high_text_contrast";
    private static final String VISUALLY_IMPAIRED_KEY = "visually_impaired";
    private String TAG = "_AccessibilityFragment";
    private ListPreference ac4de;
    private SwitchPreference audio;
    private boolean audio_flag;
    private SwitchPreference hearing;
    private boolean hearing_flag;
    private PreferenceGroup mServicesPref;
    private TVSettingConfig mTVSettingConfig;
    private Preference visually;

    public static AccessibilityFragment newInstance() {
        return new AccessibilityFragment();
    }

    public void onResume() {
        super.onResume();
        if (this.mServicesPref != null) {
            refreshServices(this.mServicesPref);
        }
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.accessibility, (String) null);
        TwoStatePreference highContrastPreference = (TwoStatePreference) findPreference(TOGGLE_HIGH_TEXT_CONTRAST_KEY);
        boolean z = false;
        if (Settings.Secure.getInt(getContext().getContentResolver(), "high_text_contrast_enabled", 0) == 1) {
            z = true;
        }
        highContrastPreference.setChecked(z);
        this.mServicesPref = (PreferenceGroup) findPreference(ACCESSIBILITY_SERVICES_KEY);
        if (this.mServicesPref != null) {
            refreshServices(this.mServicesPref);
        }
        this.mTVSettingConfig = TVSettingConfig.getInstance(getActivity());
        updatePrefEnabled();
        if (MarketRegionInfo.getCurrentMarketRegion() != 1) {
            getPreferenceScreen().removePreference(findPreference("captions_fusion"));
        }
        initAC4();
    }

    private void updatePrefEnabled() {
        int value = this.mTVSettingConfig.getConfigValueInt("g_audio__aud_type");
        this.audio = (SwitchPreference) findPreference(AUDIO_DESCRIPTION_KEY);
        this.hearing = (SwitchPreference) findPreference(HEARING_IMPAIRED_KEY);
        this.visually = findPreference(VISUALLY_IMPAIRED_KEY);
        this.ac4de = (ListPreference) findPreference("ac4_de");
        int i = 0;
        if (this.audio != null) {
            this.audio.setChecked(value == 2);
            this.audio.setVisible(false);
        }
        if (this.hearing != null) {
            this.hearing.setChecked(value == 1);
            this.hearing.setVisible(false);
        }
        if (this.visually != null) {
            this.visually.setEnabled(value == 2);
            this.visually.setVisible(false);
        }
        if (this.ac4de != null) {
            this.ac4de.setVisible(false);
        }
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_DEVICE_ACCESSIBILITY);
        if (prefKeys != null) {
            while (true) {
                int i2 = i;
                if (i2 < prefKeys.size()) {
                    String prefKey = prefKeys.get(i2);
                    if (prefKey == null) {
                        MtkLog.e(this.TAG, "prefKey is null");
                        return;
                    }
                    String str = this.TAG;
                    MtkLog.e(str, "prefKey is : " + prefKey);
                    if (prefKey.equals(AUDIO_DESCRIPTION_KEY) && this.audio != null) {
                        this.audio.setVisible(true);
                    }
                    if (prefKey.equals(HEARING_IMPAIRED_KEY) && this.hearing != null) {
                        this.hearing.setVisible(true);
                    }
                    if (prefKey.equals(VISUALLY_IMPAIRED_KEY) && this.visually != null) {
                        this.visually.setVisible(true);
                    }
                    if (prefKey.equals("ac4_de") && this.ac4de != null) {
                        this.ac4de.setVisible(true);
                        this.ac4de.setOnPreferenceChangeListener(this);
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    private void initAC4() {
        if (this.ac4de != null) {
            int val = this.mTVSettingConfig.getConfigValueInt("g_audio__aud_ac4_de_gain");
            this.ac4de.setSummary(this.ac4de.getEntries()[val]);
            this.ac4de.setValueIndex(val);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        String str = this.TAG;
        MtkLog.d(str, "onPreferenceChange preference == " + preferenceKey + "  " + ((String) newValue));
        PreferenceConfigUtils.getInstance(getPreferenceManager().getContext()).updatePreferenceChanged(this, preference, newValue);
        if (!preferenceKey.equals("ac4_de")) {
            return true;
        }
        this.mTVSettingConfig.setConifg("g_audio__aud_ac4_de_gain", Integer.parseInt(newValue + ""));
        initAC4();
        return true;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), TOGGLE_HIGH_TEXT_CONTRAST_KEY)) {
            Settings.Secure.putInt(getActivity().getContentResolver(), "high_text_contrast_enabled", ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (TextUtils.equals(preference.getKey(), AUDIO_DESCRIPTION_KEY)) {
            boolean check = ((SwitchPreference) preference).isChecked();
            this.mTVSettingConfig.setConifg("g_audio__aud_type", check ? 2 : 0);
            if (check && this.hearing != null) {
                this.hearing.setChecked(false);
            }
            if (this.visually != null) {
                this.visually.setEnabled(check);
            }
            return true;
        } else if (!TextUtils.equals(preference.getKey(), HEARING_IMPAIRED_KEY)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            boolean check2 = ((SwitchPreference) preference).isChecked();
            this.mTVSettingConfig.setConifg("g_audio__aud_type", check2);
            if (check2) {
                if (this.audio != null) {
                    this.audio.setChecked(false);
                }
                if (this.visually != null && check2) {
                    this.visually.setEnabled(false);
                }
            }
            return true;
        }
    }

    private void refreshServices(PreferenceGroup group) {
        List<AccessibilityServiceInfo> installedServiceInfos = ((AccessibilityManager) getActivity().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList();
        Set<ComponentName> enabledServices = AccessibilityUtils.getEnabledServicesFromSettings(getActivity());
        boolean z = false;
        boolean z2 = true;
        boolean accessibilityEnabled = Settings.Secure.getInt(getActivity().getContentResolver(), "accessibility_enabled", 0) == 1;
        for (AccessibilityServiceInfo accInfo : installedServiceInfos) {
            ServiceInfo serviceInfo = accInfo.getResolveInfo().serviceInfo;
            ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            if (MarketRegionInfo.getCurrentMarketRegion() == z2) {
                if (componentName.getClassName().contains("SwitchAccessService")) {
                }
            } else if (componentName.getClassName().contains("SwitchAccessService")) {
                PreferenceGroup preferenceGroup = group;
                z2 = true;
            } else if (componentName.getClassName().contains("TalkBackService")) {
            }
            boolean serviceEnabled = (!accessibilityEnabled || !enabledServices.contains(componentName)) ? z : z2;
            String title = accInfo.getResolveInfo().loadLabel(getActivity().getPackageManager()).toString();
            String key = "ServicePref:" + componentName.flattenToString();
            Preference servicePref = findPreference(key);
            if (servicePref == null) {
                servicePref = new Preference(group.getContext());
                servicePref.setKey(key);
            }
            servicePref.setTitle((CharSequence) title);
            servicePref.setSummary(serviceEnabled ? R.string.settings_on : R.string.settings_off);
            servicePref.setFragment(AccessibilityServiceFragment.class.getName());
            AccessibilityServiceFragment.prepareArgs(servicePref.getExtras(), serviceInfo.packageName, serviceInfo.name, accInfo.getSettingsActivityName(), title);
            group.addPreference(servicePref);
            z2 = true;
            z = false;
        }
        PreferenceGroup preferenceGroup2 = group;
    }

    public int getMetricsCategory() {
        return 2;
    }
}
