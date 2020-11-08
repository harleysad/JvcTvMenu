package com.android.tv.settings.accessories;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.R;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class BluetoothAccessoryActivity extends TvSettingsActivity {
    public static final String EXTRA_ACCESSORY_ADDRESS = "accessory_address";
    public static final String EXTRA_ACCESSORY_ICON_ID = "accessory_icon_res";
    public static final String EXTRA_ACCESSORY_NAME = "accessory_name";

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        int deviceImgId;
        String deviceName;
        String deviceAddress = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            deviceAddress = bundle.getString(EXTRA_ACCESSORY_ADDRESS);
            deviceName = bundle.getString(EXTRA_ACCESSORY_NAME);
            deviceImgId = bundle.getInt(EXTRA_ACCESSORY_ICON_ID);
        } else {
            deviceName = getString(R.string.accessory_options);
            deviceImgId = R.drawable.ic_qs_bluetooth_not_connected;
        }
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(BluetoothAccessoryFragment.class.getName(), getArguments(deviceAddress, deviceName, deviceImgId));
    }

    public static Bundle getArguments(String deviceAddress, String deviceName, int deviceImgId) {
        Bundle b = new Bundle(3);
        b.putString(EXTRA_ACCESSORY_ADDRESS, deviceAddress);
        b.putString(EXTRA_ACCESSORY_NAME, deviceName);
        b.putInt(EXTRA_ACCESSORY_ICON_ID, deviceImgId);
        return b;
    }
}
