package com.android.tv.settings.partnercustomizer.timer;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;

@Keep
public class PowerOnChannelFragment extends LeanbackPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String LASTSTATUS = "Last Status";
    private static final String POWER_ON_CHANNEL_SELECT_MODE = "power_on_channel_select_mode";
    private static final String POWER_ON_CHANNEL_SHOW_CHANNELS = "power_on_channel_show_channels";
    private static final String TAG = "PowerOnChannelFragment";
    private Preference mPowerOnChannelShowChannelPref;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_power_on_channel, (String) null);
        PreferenceUtils.setupListPreference(this, POWER_ON_CHANNEL_SELECT_MODE, "tv_timer_power_on_channel_select_mode_entry_values");
        this.mPowerOnChannelShowChannelPref = findPreference(POWER_ON_CHANNEL_SHOW_CHANNELS);
        if (TextUtils.equals(LASTSTATUS, getSettingValue("tv_timer_power_on_channel_select_mode_entry_values"))) {
            this.mPowerOnChannelShowChannelPref.setEnabled(false);
        } else {
            this.mPowerOnChannelShowChannelPref.setEnabled(true);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        ContentResolver contentResolver = getContext().getContentResolver();
        Log.d(TAG, "newValue:" + newValue);
        if (((preferenceKey.hashCode() == -846405660 && preferenceKey.equals(POWER_ON_CHANNEL_SELECT_MODE)) ? (char) 0 : 65535) == 0) {
            Settings.Global.putString(contentResolver, "tv_timer_power_on_channel_select_mode_entry_values", (String) newValue);
            if (TextUtils.equals(LASTSTATUS, getSettingValue("tv_timer_power_on_channel_select_mode_entry_values"))) {
                this.mPowerOnChannelShowChannelPref.setEnabled(false);
            } else {
                this.mPowerOnChannelShowChannelPref.setEnabled(true);
            }
        }
        return true;
    }

    private String getSettingValue(String settingkey) {
        return Settings.Global.getString(getContext().getContentResolver(), settingkey);
    }
}
