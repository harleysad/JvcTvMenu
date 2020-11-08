package com.android.tv.settings.partnercustomizer.retailmode;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class RetailModeSettingFragment extends LeanbackPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String RETAIL_MODE_SERVICE_NAME = "com.android.tv.settings.partnercustomizer.retailmode.RetailModeService";
    private static final String TAG = "RetailModeSettingFragment";
    private ContentResolver contentResolver;
    private boolean isRetailEnabled = false;
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private TVSettingConfig mTVSettingConfig;
    private ListPreference retailDemo;
    private ListPreference retailMessagingPref;
    private Preference retailModePref;

    public static RetailModeSettingFragment newInstance() {
        return new RetailModeSettingFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        MtkLog.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        updateMessagePrefEnable();
        onRetailModeEnabled();
        super.onResume();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        this.contentResolver = getContext().getContentResolver();
        this.mTVSettingConfig = TVSettingConfig.getInstance(getContext());
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        createPreferences();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String prefKey = preference.getKey();
        MtkLog.d(TAG, "onPreferenceTreeClick : prefKey = " + prefKey);
        if (prefKey != null && prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE)) {
            Intent intent = new Intent();
            intent.putExtra(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, this.isRetailEnabled);
            intent.setClassName("com.android.tv.settings", "com.android.tv.settings.partnercustomizer.retailmode.RetailModeConfirmActivity");
            getContext().startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        MtkLog.e(TAG, "onPreferenceChange preference == " + preferenceKey + "  " + ((String) newValue));
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return true;
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.s_retail_mode);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_DEVICE_RETAIL);
        if (prefKeys != null) {
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                if (prefKey == null) {
                    MtkLog.e(TAG, "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE)) {
                    this.retailModePref = this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.s_retail_mode, (String) null);
                    mScreen.addPreference(this.retailModePref);
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_MESSAGE)) {
                    this.retailMessagingPref = this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.s_retail_messaging, (int) R.array.a_retail_messaging_entries, (int) R.array.a_retail_messaging_entry_values, (String) null);
                    mScreen.addPreference(this.retailMessagingPref);
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_DEVICE_RETAILDEMO)) {
                    this.retailDemo = this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.s_pq_demo, (int) R.array.a_pq_demo_entries, (int) R.array.a_pq_demo_entry_values, (String) null);
                    mScreen.addPreference(this.retailDemo);
                }
            }
        }
    }

    private void updateMessagePrefEnable() {
        boolean z = true;
        if (PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, 0) != 1) {
            z = false;
        }
        this.isRetailEnabled = z;
        if (this.retailMessagingPref != null) {
            this.retailMessagingPref.setEnabled(this.isRetailEnabled);
        }
        if (this.retailDemo != null) {
            this.retailDemo.setEnabled(this.isRetailEnabled);
        }
        if (this.retailModePref != null) {
            this.retailModePref.setSummary((CharSequence) getString(this.isRetailEnabled ? R.string.pic_advance_video_entries_on : R.string.pic_advance_video_entries_off));
        }
    }

    private void onRetailModeEnabled() {
        this.isRetailEnabled = PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, 0) == 1;
        if (!this.isRetailEnabled) {
            getActivity().stopService(new Intent(getActivity(), RetailModeService.class));
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, this.retailMessagingPref, 0);
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, this.retailDemo, 0);
        } else if (!isServiceRunningCheck(RETAIL_MODE_SERVICE_NAME)) {
            MtkLog.d(TAG, "START RetailModeService...");
            Intent intentSvc = new Intent(getActivity(), RetailModeService.class);
            intentSvc.putExtra(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, 1);
            getActivity().startService(intentSvc);
        } else {
            MtkLog.d(TAG, "re-START RetailModeService...");
            getActivity().stopService(new Intent(getActivity(), RetailModeService.class));
            Intent intentSvc2 = new Intent(getActivity(), RetailModeService.class);
            intentSvc2.putExtra(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, 1);
            getActivity().startService(intentSvc2);
        }
    }

    public boolean isServiceRunningCheck(String TargetServiceName) {
        for (ActivityManager.RunningServiceInfo service : ((ActivityManager) getActivity().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (TargetServiceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
