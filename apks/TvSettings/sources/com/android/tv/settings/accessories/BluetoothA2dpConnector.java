package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.android.tv.settings.accessories.BluetoothDevicePairer;

public class BluetoothA2dpConnector implements BluetoothDevicePairer.BluetoothConnector {
    private static final boolean DEBUG = false;
    public static final String TAG = "BluetoothA2dpConnector";
    private BluetoothA2dp mA2dpProfile;
    private BroadcastReceiver mConnectionStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")).equals(BluetoothA2dpConnector.this.mTarget)) {
                int previousState = intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 1);
                int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 1);
                if (previousState == 1) {
                    if (state == 2) {
                        BluetoothA2dpConnector.this.mOpenConnectionCallback.succeeded();
                    } else if (state == 0) {
                        Log.d(BluetoothA2dpConnector.TAG, "Failed to connect");
                        BluetoothA2dpConnector.this.mOpenConnectionCallback.failed();
                    }
                    BluetoothA2dpConnector.this.unregisterConnectionStateReceiver();
                    BluetoothA2dpConnector.this.closeA2dpProfileProxy();
                }
            }
        }
    };
    private boolean mConnectionStateReceiverRegistered = false;
    private Context mContext;
    /* access modifiers changed from: private */
    public BluetoothDevicePairer.OpenConnectionCallback mOpenConnectionCallback;
    private BluetoothProfile.ServiceListener mServiceConnection = new BluetoothProfile.ServiceListener() {
        public void onServiceDisconnected(int profile) {
            Log.w(BluetoothA2dpConnector.TAG, "Service disconnected, perhaps unexpectedly");
            BluetoothA2dpConnector.this.unregisterConnectionStateReceiver();
            BluetoothA2dpConnector.this.closeA2dpProfileProxy();
            BluetoothA2dpConnector.this.mOpenConnectionCallback.failed();
        }

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            BluetoothA2dp mA2dpProfile = (BluetoothA2dp) proxy;
            BluetoothA2dpConnector.this.registerConnectionStateReceiver();
            int connectTries = 0;
            while (true) {
                int connectTries2 = connectTries + 1;
                if (connectTries >= 8) {
                    break;
                }
                boolean connected = mA2dpProfile.getPriority(BluetoothA2dpConnector.this.mTarget) == 100;
                Log.i(BluetoothA2dpConnector.TAG, "XXXXXX wait conntect is enable count = " + connectTries2);
                if (connected) {
                    break;
                }
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                }
                connectTries = connectTries2;
            }
            mA2dpProfile.connect(BluetoothA2dpConnector.this.mTarget);
            mA2dpProfile.setPriority(BluetoothA2dpConnector.this.mTarget, 1000);
        }
    };
    /* access modifiers changed from: private */
    public BluetoothDevice mTarget;

    private BluetoothA2dpConnector() {
    }

    public BluetoothA2dpConnector(Context context, BluetoothDevice target, BluetoothDevicePairer.OpenConnectionCallback callback) {
        this.mContext = context;
        this.mTarget = target;
        this.mOpenConnectionCallback = callback;
    }

    public void openConnection(BluetoothAdapter adapter) {
        if (!adapter.getProfileProxy(this.mContext, this.mServiceConnection, 2)) {
            this.mOpenConnectionCallback.failed();
        }
    }

    /* access modifiers changed from: private */
    public void closeA2dpProfileProxy() {
        if (this.mA2dpProfile != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(2, this.mA2dpProfile);
                this.mA2dpProfile = null;
            } catch (Throwable t) {
                Log.w(TAG, "Error cleaning up A2DP proxy", t);
            }
        }
    }

    /* access modifiers changed from: private */
    public void registerConnectionStateReceiver() {
        this.mContext.registerReceiver(this.mConnectionStateReceiver, new IntentFilter("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED"));
        this.mConnectionStateReceiverRegistered = true;
    }

    /* access modifiers changed from: private */
    public void unregisterConnectionStateReceiver() {
        if (this.mConnectionStateReceiverRegistered) {
            this.mContext.unregisterReceiver(this.mConnectionStateReceiver);
            this.mConnectionStateReceiverRegistered = false;
        }
    }
}
