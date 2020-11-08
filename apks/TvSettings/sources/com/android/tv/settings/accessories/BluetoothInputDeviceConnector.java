package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidHost;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.util.Log;
import com.android.tv.settings.accessories.BluetoothDevicePairer;

public class BluetoothInputDeviceConnector implements BluetoothDevicePairer.BluetoothConnector {
    private static final boolean DEBUG = false;
    private static final String[] INVALID_INPUT_KEYBOARD_DEVICE_NAMES = {"gpio-keypad", "cec_keyboard", "Virtual", "athome_remote"};
    public static final String TAG = "BtInputDeviceConnector";
    /* access modifiers changed from: private */
    public Context mContext;
    private Handler mHandler;
    private InputManager.InputDeviceListener mInputListener = new InputManager.InputDeviceListener() {
        public void onInputDeviceRemoved(int deviceId) {
        }

        public void onInputDeviceChanged(int deviceId) {
        }

        public void onInputDeviceAdded(int deviceId) {
            if (BluetoothDevicePairer.hasValidInputDevice(BluetoothInputDeviceConnector.this.mContext, new int[]{deviceId})) {
                BluetoothInputDeviceConnector.this.onInputAdded();
            }
        }
    };
    private boolean mInputMethodMonitorRegistered = false;
    /* access modifiers changed from: private */
    public BluetoothHidHost mInputProxy;
    /* access modifiers changed from: private */
    public BluetoothDevicePairer.OpenConnectionCallback mOpenConnectionCallback;
    private BluetoothProfile.ServiceListener mServiceConnection = new BluetoothProfile.ServiceListener() {
        public void onServiceDisconnected(int profile) {
            Log.w(BluetoothInputDeviceConnector.TAG, "Service disconnected, perhaps unexpectedly");
            BluetoothInputDeviceConnector.this.unregisterInputMethodMonitor();
            BluetoothInputDeviceConnector.this.closeInputProfileProxy();
            BluetoothInputDeviceConnector.this.mOpenConnectionCallback.failed();
        }

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            BluetoothHidHost unused = BluetoothInputDeviceConnector.this.mInputProxy = (BluetoothHidHost) proxy;
            if (BluetoothInputDeviceConnector.this.mTarget != null) {
                BluetoothInputDeviceConnector.this.registerInputMethodMonitor();
                BluetoothInputDeviceConnector.this.mInputProxy.connect(BluetoothInputDeviceConnector.this.mTarget);
                BluetoothInputDeviceConnector.this.mInputProxy.setPriority(BluetoothInputDeviceConnector.this.mTarget, 1000);
            }
        }
    };
    /* access modifiers changed from: private */
    public BluetoothDevice mTarget;

    /* access modifiers changed from: private */
    public void registerInputMethodMonitor() {
        InputManager inputManager = (InputManager) this.mContext.getSystemService("input");
        inputManager.registerInputDeviceListener(this.mInputListener, this.mHandler);
        int[] inputDeviceIds = inputManager.getInputDeviceIds();
        this.mInputMethodMonitorRegistered = true;
    }

    /* access modifiers changed from: private */
    public void onInputAdded() {
        unregisterInputMethodMonitor();
        closeInputProfileProxy();
        this.mOpenConnectionCallback.succeeded();
    }

    /* access modifiers changed from: private */
    public void unregisterInputMethodMonitor() {
        if (this.mInputMethodMonitorRegistered) {
            ((InputManager) this.mContext.getSystemService("input")).unregisterInputDeviceListener(this.mInputListener);
            this.mInputMethodMonitorRegistered = false;
        }
    }

    /* access modifiers changed from: private */
    public void closeInputProfileProxy() {
        if (this.mInputProxy != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, this.mInputProxy);
                this.mInputProxy = null;
            } catch (Throwable t) {
                Log.w(TAG, "Error cleaning up input profile proxy", t);
            }
        }
    }

    private BluetoothInputDeviceConnector() {
    }

    public BluetoothInputDeviceConnector(Context context, BluetoothDevice target, Handler handler, BluetoothDevicePairer.OpenConnectionCallback callback) {
        this.mContext = context;
        this.mTarget = target;
        this.mHandler = handler;
        this.mOpenConnectionCallback = callback;
    }

    public void openConnection(BluetoothAdapter adapter) {
        if (!adapter.getProfileProxy(this.mContext, this.mServiceConnection, 4)) {
            this.mOpenConnectionCallback.failed();
        }
    }
}
