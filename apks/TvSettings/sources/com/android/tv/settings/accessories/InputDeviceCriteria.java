package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothClass;
import com.android.tv.settings.util.bluetooth.BluetoothDeviceCriteria;

public class InputDeviceCriteria extends BluetoothDeviceCriteria {
    public static final int MINOR_DEVICE_CLASS_GAMEPAD = Integer.parseInt("0000000001000", 2);
    public static final int MINOR_DEVICE_CLASS_JOYSTICK = Integer.parseInt("0000000000100", 2);
    public static final int MINOR_DEVICE_CLASS_KEYBOARD = Integer.parseInt("0000001000000", 2);
    public static final int MINOR_DEVICE_CLASS_POINTING = Integer.parseInt("0000010000000", 2);
    public static final int MINOR_DEVICE_CLASS_REMOTE = Integer.parseInt("0000000001100", 2);

    public boolean isMatchingMajorDeviceClass(int majorDeviceClass) {
        return majorDeviceClass == 1280;
    }

    public boolean isMatchingDeviceClass(int majorMinorClass) {
        return (((((MINOR_DEVICE_CLASS_POINTING | MINOR_DEVICE_CLASS_JOYSTICK) | MINOR_DEVICE_CLASS_GAMEPAD) | MINOR_DEVICE_CLASS_KEYBOARD) | MINOR_DEVICE_CLASS_REMOTE) & majorMinorClass) != 0;
    }

    public boolean isInputDevice(BluetoothClass bluetoothClass) {
        return isMatchingMajorDeviceClass(bluetoothClass.getMajorDeviceClass()) && isMatchingDeviceClass(bluetoothClass.getDeviceClass());
    }
}
