package com.android.tv.settings.name;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DeviceManager {
    public static final String ACTION_DEVICE_NAME_UPDATE = "com.android.tv.settings.name.DeviceManager.DEVICE_NAME_UPDATE";
    private static final String TAG = "DeviceManager";

    public static String getDeviceName(Context context) {
        return Settings.Global.getString(context.getContentResolver(), "device_name");
    }

    public static void setDeviceName(Context context, String name) {
        Settings.Global.putString(context.getContentResolver(), "device_name", name);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            btAdapter.setName(name);
        } else {
            Log.v(TAG, "Bluetooth adapter is null. Running on device without bluetooth?");
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_DEVICE_NAME_UPDATE));
    }
}
