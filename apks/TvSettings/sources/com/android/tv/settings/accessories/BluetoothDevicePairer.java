package com.android.tv.settings.accessories;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.InputDevice;
import com.android.tv.settings.util.bluetooth.BluetoothDeviceCriteria;
import com.android.tv.settings.util.bluetooth.BluetoothScanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BluetoothDevicePairer {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED";
    private static final boolean DEBUG = true;
    public static final int DELAY_AUTO_PAIRING = 15000;
    public static final int DELAY_MANUAL_PAIRING = 5000;
    public static final int DELAY_RETRY = 5000;
    private static final String[] INVALID_INPUT_KEYBOARD_DEVICE_NAMES = {"gpio-keypad", "cec_keyboard", "Virtual", "athome_remote"};
    private static final int MSG_PAIR = 1;
    private static final int MSG_START = 2;
    public static final int STATUS_CONNECTING = 4;
    public static final int STATUS_ERROR = -1;
    public static final int STATUS_NONE = 0;
    public static final int STATUS_PAIRING = 3;
    public static final int STATUS_SCANNING = 1;
    public static final int STATUS_WAITING_TO_PAIR = 2;
    public static final String TAG = "BluetoothDevicePairer";
    private boolean mAutoMode = true;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mBluetoothConnectionsState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(BluetoothDevicePairer.TAG, "onReceive() BluetoothConnectionsState");
            BluetoothDevice device = (BluetoothDevice) intent.getExtras().getParcelable("android.bluetooth.device.extra.DEVICE");
            if (device != null && device.getBluetoothClass().getDeviceClass() == 1408 && BluetoothDevicePairer.this.mTarget.getType() != 2) {
                Log.d(BluetoothDevicePairer.TAG, "Device Name = " + device.getName());
                int newState = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                int prevState = intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
                Log.d(BluetoothDevicePairer.TAG, "onReceive() prevState = " + prevState + " newState = " + newState);
                if (newState == 2 && prevState == 1) {
                    BluetoothDevicePairer.this.mContext.unregisterReceiver(BluetoothDevicePairer.this.mBluetoothConnectionsState);
                } else if (newState == 1 && prevState == 0) {
                    BluetoothDevicePairer.this.openConnection();
                }
            }
        }
    };
    private final ArrayList<BluetoothDeviceCriteria> mBluetoothDeviceCriteria = new ArrayList<>();
    private BroadcastReceiver mBluetoothStateReceiver;
    private final BluetoothScanner.Listener mBtListener = new BluetoothScanner.Listener() {
        public void onDeviceAdded(BluetoothScanner.Device device) {
            Log.d(BluetoothDevicePairer.TAG, "Adding device: " + device.btDevice.getAddress());
            BluetoothDevicePairer.this.onDeviceFound(device.btDevice);
        }

        public void onDeviceRemoved(BluetoothScanner.Device device) {
            Log.d(BluetoothDevicePairer.TAG, "Device lost: " + device.btDevice.getAddress());
            BluetoothDevicePairer.this.onDeviceLost(device.btDevice);
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    private final Handler mHandler;
    private InputDeviceCriteria mInputDeviceCriteria;
    private boolean mLinkReceiverRegistered = false;
    private final BroadcastReceiver mLinkStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Log.d(BluetoothDevicePairer.TAG, "There was a link status change for: " + device.getAddress());
            if (device.equals(BluetoothDevicePairer.this.mTarget)) {
                int bondState = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10);
                int previousBondState = intent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", 10);
                Log.d(BluetoothDevicePairer.TAG, "Bond states: old = " + previousBondState + ", new = " + bondState);
                if (bondState == 10 && previousBondState == 11) {
                    BluetoothDevicePairer.this.unregisterLinkStatusReceiver();
                    BluetoothDevicePairer.this.onBondFailed();
                } else if (bondState == 12) {
                    BluetoothDevicePairer.this.unregisterLinkStatusReceiver();
                    BluetoothDevicePairer.this.onBonded();
                }
            }
        }
    };
    private EventListener mListener;
    private long mNextStageTimestamp = -1;
    private final OpenConnectionCallback mOpenConnectionCallback = new OpenConnectionCallback() {
        public void succeeded() {
            BluetoothDevicePairer.this.setStatus(0);
        }

        public void failed() {
            BluetoothDevicePairer.this.setStatus(-1);
        }
    };
    private int mStatus = 0;
    /* access modifiers changed from: private */
    public BluetoothDevice mTarget;
    private final ArrayList<BluetoothDevice> mVisibleDevices = new ArrayList<>();

    public interface BluetoothConnector {
        void openConnection(BluetoothAdapter bluetoothAdapter);
    }

    public interface EventListener {
        void statusChanged();
    }

    public interface OpenConnectionCallback {
        void failed();

        void succeeded();
    }

    public static boolean hasValidInputDevice(Context context, int[] deviceIds) {
        InputManager inMan = (InputManager) context.getSystemService("input");
        int ptr = deviceIds.length - 1;
        while (true) {
            int index = 0;
            if (ptr <= -1) {
                return false;
            }
            InputDevice device = inMan.getInputDevice(deviceIds[ptr]);
            int sources = device.getSources();
            boolean isCompatible = false;
            if ((sources & InputDeviceCompat.SOURCE_DPAD) == 513) {
                isCompatible = true;
            }
            if ((sources & InputDeviceCompat.SOURCE_GAMEPAD) == 1025) {
                isCompatible = true;
            }
            if ((sources & InputDeviceCompat.SOURCE_KEYBOARD) == 257) {
                boolean isValidKeyboard = true;
                String keyboardName = device.getName();
                while (true) {
                    if (index >= INVALID_INPUT_KEYBOARD_DEVICE_NAMES.length) {
                        break;
                    } else if (keyboardName.equals(INVALID_INPUT_KEYBOARD_DEVICE_NAMES[index])) {
                        isValidKeyboard = false;
                        break;
                    } else {
                        index++;
                    }
                }
                if (isValidKeyboard) {
                    isCompatible = true;
                }
            }
            if ((sources & 8194) == 8194) {
                isCompatible = true;
            }
            if (!device.isVirtual() && isCompatible) {
                return true;
            }
            ptr--;
        }
    }

    public static boolean hasValidInputDevice(Context context) {
        return hasValidInputDevice(context, ((InputManager) context.getSystemService("input")).getInputDeviceIds());
    }

    public BluetoothDevicePairer(Context context, EventListener listener) {
        this.mContext = context.getApplicationContext();
        this.mListener = listener;
        addBluetoothDeviceCriteria();
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        BluetoothDevicePairer.this.startBonding();
                        return;
                    case 2:
                        BluetoothDevicePairer.this.start();
                        return;
                    default:
                        Log.d(BluetoothDevicePairer.TAG, "No handler case available for message: " + msg.what);
                        return;
                }
            }
        };
    }

    private void addBluetoothDeviceCriteria() {
        this.mInputDeviceCriteria = new InputDeviceCriteria();
        this.mBluetoothDeviceCriteria.add(this.mInputDeviceCriteria);
        ComponentName comp = new Intent(IBluetoothA2dp.class.getName()).resolveSystemService(this.mContext.getPackageManager(), 0);
        if (comp != null && this.mContext.getPackageManager().getComponentEnabledSetting(comp) != 2) {
            Log.d(TAG, "Adding A2dp device criteria for pairing");
            this.mBluetoothDeviceCriteria.add(new A2dpDeviceCriteria());
        }
    }

    public void start() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth not enabled, delaying startup.");
            if (this.mBluetoothStateReceiver == null) {
                this.mBluetoothStateReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 10) == 12) {
                            Log.d(BluetoothDevicePairer.TAG, "Bluetooth now enabled, starting.");
                            BluetoothDevicePairer.this.start();
                            return;
                        }
                        Log.d(BluetoothDevicePairer.TAG, "Bluetooth not yet started, got broadcast: " + intent);
                    }
                };
                this.mContext.registerReceiver(this.mBluetoothStateReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            }
            bluetoothAdapter.enable();
            return;
        }
        if (this.mBluetoothStateReceiver != null) {
            this.mContext.unregisterReceiver(this.mBluetoothStateReceiver);
            this.mBluetoothStateReceiver = null;
        }
        setStatus(1);
        BluetoothScanner.startListening(this.mContext, this.mBtListener, this.mBluetoothDeviceCriteria);
    }

    public void clearDeviceList() {
        doCancel();
        this.mVisibleDevices.clear();
    }

    public void cancelPairing() {
        this.mAutoMode = false;
        doCancel();
    }

    public void disableAutoPairing() {
        this.mAutoMode = false;
    }

    public void dispose() {
        this.mHandler.removeCallbacksAndMessages((Object) null);
        if (this.mLinkReceiverRegistered) {
            unregisterLinkStatusReceiver();
        }
        if (this.mBluetoothStateReceiver != null) {
            this.mContext.unregisterReceiver(this.mBluetoothStateReceiver);
        }
        stopScanning();
    }

    public void startPairing(BluetoothDevice device) {
        startPairing(device, true);
    }

    public int getStatus() {
        return this.mStatus;
    }

    public BluetoothDevice getTargetDevice() {
        return this.mTarget;
    }

    public long getNextStageTime() {
        return this.mNextStageTimestamp;
    }

    public List<BluetoothDevice> getAvailableDevices() {
        ArrayList<BluetoothDevice> copy = new ArrayList<>(this.mVisibleDevices.size());
        copy.addAll(this.mVisibleDevices);
        return copy;
    }

    public void setListener(EventListener listener) {
        this.mListener = listener;
    }

    public void invalidateDevice(BluetoothDevice device) {
        onDeviceLost(device);
    }

    private void startPairing(BluetoothDevice device, boolean isManual) {
        this.mAutoMode = !isManual;
        this.mTarget = device;
        if (!isInProgress()) {
            this.mHandler.removeCallbacksAndMessages((Object) null);
            this.mNextStageTimestamp = SystemClock.elapsedRealtime() + ((long) (this.mAutoMode ? DELAY_AUTO_PAIRING : 5000));
            stopScanning();
            Log.d(TAG, "stop scan");
            this.mHandler.sendEmptyMessageDelayed(1, this.mAutoMode ? 15000 : 5000);
            setStatus(2);
            return;
        }
        throw new RuntimeException("Pairing already in progress, you must cancel the previous request first");
    }

    public boolean isInProgress() {
        return (this.mStatus == 0 || this.mStatus == -1 || this.mStatus == 1 || this.mStatus == 2) ? false : true;
    }

    private void updateListener() {
        if (this.mListener != null) {
            this.mListener.statusChanged();
        }
    }

    /* access modifiers changed from: private */
    public void onDeviceFound(BluetoothDevice device) {
        if (!this.mVisibleDevices.contains(device)) {
            this.mVisibleDevices.add(device);
            Log.d(TAG, "Added device to visible list. Name = " + device.getName() + " , class = " + device.getBluetoothClass().getDeviceClass());
            updatePairingState();
            updateListener();
        }
    }

    /* access modifiers changed from: private */
    public void onDeviceLost(BluetoothDevice device) {
        if (this.mVisibleDevices.remove(device)) {
            updatePairingState();
            updateListener();
        }
    }

    private void updatePairingState() {
        if (this.mAutoMode) {
            BluetoothDevice candidate = getAutoPairDevice();
            if (candidate != null) {
                this.mTarget = candidate;
                startPairing(this.mTarget, false);
                return;
            }
            doCancel();
        }
    }

    private BluetoothDevice getAutoPairDevice() {
        List<BluetoothDevice> inputDevices = new ArrayList<>();
        Iterator<BluetoothDevice> it = this.mVisibleDevices.iterator();
        while (it.hasNext()) {
            BluetoothDevice device = it.next();
            if (this.mInputDeviceCriteria.isInputDevice(device.getBluetoothClass())) {
                inputDevices.add(device);
            }
        }
        if (inputDevices.size() == 1) {
            return inputDevices.get(0);
        }
        return null;
    }

    private void doCancel() {
        if (isInProgress()) {
            Log.d(TAG, "Pairing process has already begun, it can not be canceled.");
            return;
        }
        boolean wasListening = BluetoothScanner.stopListening(this.mBtListener);
        BluetoothScanner.stopNow();
        this.mHandler.removeCallbacksAndMessages((Object) null);
        unpairDevice(this.mTarget);
        this.mTarget = null;
        setStatus(0);
        if (wasListening) {
            start();
        }
    }

    /* access modifiers changed from: private */
    public void setStatus(int status) {
        this.mStatus = status;
        updateListener();
    }

    /* access modifiers changed from: private */
    public void startBonding() {
        setStatus(3);
        if (this.mTarget.getBondState() != 12) {
            registerLinkStatusReceiver();
            this.mTarget.createBond();
            return;
        }
        onBonded();
    }

    /* access modifiers changed from: private */
    public void onBonded() {
        if (this.mTarget.getBluetoothClass().getDeviceClass() != 1408 || this.mTarget.getType() == 2) {
            openConnection();
        }
    }

    /* access modifiers changed from: private */
    public void openConnection() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothConnector btConnector = getBluetoothConnector();
        if (btConnector != null) {
            setStatus(4);
            btConnector.openConnection(adapter);
            return;
        }
        Log.w(TAG, "There was an error getting the BluetoothConnector.");
        setStatus(-1);
        if (this.mLinkReceiverRegistered) {
            unregisterLinkStatusReceiver();
        }
        unpairDevice(this.mTarget);
    }

    /* access modifiers changed from: private */
    public void onBondFailed() {
        Log.w(TAG, "There was an error bonding with the device.");
        setStatus(-1);
        unpairDevice(this.mTarget);
        this.mNextStageTimestamp = SystemClock.elapsedRealtime() + 5000;
        this.mHandler.sendEmptyMessageDelayed(2, 5000);
    }

    private void registerLinkStatusReceiver() {
        this.mLinkReceiverRegistered = true;
        this.mContext.registerReceiver(this.mLinkStatusReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        this.mContext.registerReceiver(this.mBluetoothConnectionsState, new IntentFilter(ACTION_CONNECTION_STATE_CHANGED));
    }

    /* access modifiers changed from: private */
    public void unregisterLinkStatusReceiver() {
        this.mLinkReceiverRegistered = false;
        this.mContext.unregisterReceiver(this.mLinkStatusReceiver);
    }

    private void stopScanning() {
        BluetoothScanner.stopListening(this.mBtListener);
        BluetoothScanner.stopNow();
    }

    public boolean unpairDevice(BluetoothDevice device) {
        if (device == null) {
            return false;
        }
        int state = device.getBondState();
        if (state == 11) {
            device.cancelBondProcess();
        }
        if (state == 10) {
            return false;
        }
        if (device.removeBond()) {
            Log.d(TAG, "Bluetooth device successfully unpaired: " + device.getName());
            return true;
        }
        Log.e(TAG, "Failed to unpair Bluetooth Device: " + device.getName());
        return false;
    }

    private BluetoothConnector getBluetoothConnector() {
        int majorDeviceClass = this.mTarget.getBluetoothClass().getMajorDeviceClass();
        if (majorDeviceClass == 1024) {
            return new BluetoothA2dpConnector(this.mContext, this.mTarget, this.mOpenConnectionCallback);
        }
        if (majorDeviceClass == 1280) {
            return new BluetoothInputDeviceConnector(this.mContext, this.mTarget, this.mHandler, this.mOpenConnectionCallback);
        }
        Log.d(TAG, "Unhandle device class: " + majorDeviceClass);
        return null;
    }
}
