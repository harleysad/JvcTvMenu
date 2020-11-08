package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.Set;

@Keep
public class AccessoriesFragment extends SettingsPreferenceFragment {
    private static final String KEY_ADD_ACCESSORY = "add_accessory";
    private static final String TAG = "AccessoriesFragment";
    private Preference mAddAccessory;
    private final BroadcastReceiver mBCMReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AccessoriesFragment.this.updateAccessories();
        }
    };
    private BluetoothAdapter mBtAdapter;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.remotes_and_accessories, (String) null);
        this.mAddAccessory = findPreference(KEY_ADD_ACCESSORY);
    }

    public int getMetricsCategory() {
        return 24;
    }

    /* access modifiers changed from: private */
    public void updateAccessories() {
        String desc;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            if (this.mBtAdapter == null) {
                preferenceScreen.removeAll();
                return;
            }
            Set<BluetoothDevice> bondedDevices = this.mBtAdapter.getBondedDevices();
            if (bondedDevices == null) {
                preferenceScreen.removeAll();
                return;
            }
            Context themedContext = getPreferenceManager().getContext();
            Set<String> touchedKeys = new ArraySet<>(bondedDevices.size() + 1);
            if (this.mAddAccessory != null) {
                touchedKeys.add(this.mAddAccessory.getKey());
            }
            for (BluetoothDevice device : bondedDevices) {
                String deviceAddress = device.getAddress();
                if (TextUtils.isEmpty(deviceAddress)) {
                    Log.w(TAG, "Skipping mysteriously empty bluetooth device");
                } else {
                    if (device.isConnected()) {
                        desc = getString(R.string.accessory_connected);
                    } else {
                        desc = null;
                    }
                    String key = "BluetoothDevice:" + deviceAddress;
                    touchedKeys.add(key);
                    Preference preference = preferenceScreen.findPreference(key);
                    if (preference == null) {
                        preference = new Preference(themedContext);
                        preference.setKey(key);
                    }
                    String deviceName = device.getAliasName();
                    preference.setTitle((CharSequence) deviceName);
                    preference.setSummary((CharSequence) desc);
                    int deviceImgId = AccessoryUtils.getImageIdForDevice(device);
                    preference.setIcon(deviceImgId);
                    preference.setFragment(BluetoothAccessoryFragment.class.getName());
                    BluetoothAccessoryFragment.prepareArgs(preference.getExtras(), deviceAddress, deviceName, deviceImgId);
                    preferenceScreen.addPreference(preference);
                }
            }
            int i = 0;
            while (i < preferenceScreen.getPreferenceCount()) {
                Preference preference2 = preferenceScreen.getPreference(i);
                if (touchedKeys.contains(preference2.getKey())) {
                    i++;
                } else {
                    preferenceScreen.removePreference(preference2);
                }
            }
        }
    }

    public void onStart() {
        super.onStart();
        IntentFilter btChangeFilter = new IntentFilter();
        btChangeFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        btChangeFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        btChangeFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        getContext().registerReceiver(this.mBCMReceiver, btChangeFilter);
    }

    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(this.mBCMReceiver);
    }

    public void onResume() {
        super.onResume();
        updateAccessories();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        super.onCreate(savedInstanceState);
    }
}
