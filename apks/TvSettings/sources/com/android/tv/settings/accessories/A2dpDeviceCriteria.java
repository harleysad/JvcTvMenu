package com.android.tv.settings.accessories;

import android.util.Log;
import com.android.tv.settings.util.bluetooth.BluetoothDeviceCriteria;

public class A2dpDeviceCriteria extends BluetoothDeviceCriteria {
    public static final String TAG = "aah.A2dpDeviceCriteria";

    public boolean isMatchingMajorDeviceClass(int majorDeviceClass) {
        return majorDeviceClass == 1024;
    }

    public boolean isMatchingDeviceClass(int majorMinorClass) {
        Log.d(TAG, "isMatchingDeviceClass : " + majorMinorClass);
        return majorMinorClass == 1024 || majorMinorClass == 1028 || majorMinorClass == 1048 || majorMinorClass == 1044 || majorMinorClass == 1052 || majorMinorClass == 1064 || majorMinorClass == 1032 || majorMinorClass == 1060 || majorMinorClass == 1036;
    }
}
