package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.android.tv.settings.R;

public class AccessoryUtils {
    @DrawableRes
    public static int getImageIdForDevice(@NonNull BluetoothDevice dev) {
        BluetoothClass bluetoothClass = dev.getBluetoothClass();
        if (bluetoothClass == null) {
            return R.drawable.ic_bluetooth;
        }
        int devClass = bluetoothClass.getDeviceClass();
        if (devClass == 1028) {
            return R.drawable.ic_headset_mic;
        }
        if (devClass == 1048 || devClass == 1044 || devClass == 1052 || devClass == 1064) {
            return R.drawable.ic_headset;
        }
        if ((InputDeviceCriteria.MINOR_DEVICE_CLASS_POINTING & devClass) != 0) {
            return R.drawable.ic_mouse;
        }
        if ((InputDeviceCriteria.MINOR_DEVICE_CLASS_JOYSTICK & devClass) != 0 || (InputDeviceCriteria.MINOR_DEVICE_CLASS_GAMEPAD & devClass) != 0) {
            return R.drawable.ic_games;
        }
        if ((InputDeviceCriteria.MINOR_DEVICE_CLASS_KEYBOARD & devClass) != 0) {
            return R.drawable.ic_keyboard;
        }
        return R.drawable.ic_bluetooth;
    }

    private AccessoryUtils() {
    }
}
