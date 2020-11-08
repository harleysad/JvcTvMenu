package com.android.tv.settings.partnercustomizer.device;

import android.content.Context;
import android.os.Bundle;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

public class Device3RdPrefFragment extends SettingsPreferenceFragment {
    public static Device3RdPrefFragment newInstance() {
        return new Device3RdPrefFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_device_3rd, (String) null);
    }

    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public int getMetricsCategory() {
        return 1300;
    }
}
