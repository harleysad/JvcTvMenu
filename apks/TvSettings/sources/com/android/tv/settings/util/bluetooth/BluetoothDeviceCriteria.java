package com.android.tv.settings.util.bluetooth;

import android.bluetooth.BluetoothDevice;
import java.util.regex.Pattern;

public class BluetoothDeviceCriteria {
    public static final String GOOGLE_MAC_PATTERN = "^(00:1A:11|F8:8F:CA).*";
    private final Pattern mAddressPattern;

    public BluetoothDeviceCriteria() {
        this(".*");
    }

    public BluetoothDeviceCriteria(String macAddressPattern) {
        this.mAddressPattern = Pattern.compile(macAddressPattern, 2);
    }

    public final boolean isMatchingDevice(BluetoothDevice device) {
        if (device != null && device.getAddress() != null && isMatchingMacAddress(device.getAddress()) && isMatchingMajorDeviceClass(device.getBluetoothClass().getMajorDeviceClass()) && isMatchingDeviceClass(device.getBluetoothClass().getDeviceClass())) {
            return true;
        }
        return false;
    }

    public boolean isMatchingMacAddress(String mac) {
        return this.mAddressPattern.matcher(mac).matches();
    }

    public boolean isMatchingMajorDeviceClass(int majorDeviceClass) {
        return true;
    }

    public boolean isMatchingDeviceClass(int majorMinorClass) {
        return true;
    }
}
