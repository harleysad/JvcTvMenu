package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class BluetoothPairingRequest extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            int type = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
            Intent pairingIntent = new Intent();
            pairingIntent.setClass(context, BluetoothPairingDialog.class);
            pairingIntent.putExtra("android.bluetooth.device.extra.DEVICE", (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
            pairingIntent.putExtra("android.bluetooth.device.extra.PAIRING_VARIANT", type);
            if (type == 2 || type == 4 || type == 5) {
                pairingIntent.putExtra("android.bluetooth.device.extra.PAIRING_KEY", intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE));
            }
            pairingIntent.setAction("android.bluetooth.device.action.PAIRING_REQUEST");
            pairingIntent.setFlags(268435456);
            context.startActivity(pairingIntent);
        }
    }
}
