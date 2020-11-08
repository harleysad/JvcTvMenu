package com.android.tv.settings.util.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BluetoothScanner {
    private static final int CONSECUTIVE_MISS_THRESHOLD = 4;
    private static final boolean DEBUG = false;
    private static final int FAILED_SETTING_NAME = 5;
    private static final int FOUND_ON_SCAN = -1;
    private static final int SCAN_DELAY = 4000;
    private static final String TAG = "BluetoothScanner";
    private static Receiver sReceiver;

    public static class Device {
        public String address;
        public BluetoothDevice btDevice;
        public String btName;
        public int configurationType = 0;
        public int consecutiveMisses;
        public LedConfiguration leds;
        public String name = "";

        public String toString() {
            return "Device(addr=" + this.address + " name=\"" + this.name + "\" leds=" + this.leds + "\" configuration_type=" + this.configurationType + ")";
        }

        public String getNameString() {
            Object[] objArr = new Object[2];
            objArr[0] = this.name;
            objArr[1] = this.leds == null ? "" : this.leds.getNameString();
            return String.format("\"%s\" (%s)", objArr);
        }

        public boolean setNameString(String str) {
            this.btName = str;
            if (str == null || !BluetoothNameUtils.isValidName(str)) {
                this.name = "";
                this.leds = null;
                return false;
            }
            this.leds = BluetoothNameUtils.getColorConfiguration(str);
            this.configurationType = BluetoothNameUtils.getSetupType(str);
            return true;
        }

        public boolean hasConfigurationType() {
            return this.configurationType != 0;
        }
    }

    public static class Listener {
        public void onScanningStarted() {
        }

        public void onScanningStopped(ArrayList<Device> arrayList) {
        }

        public void onDeviceAdded(Device device) {
        }

        public void onDeviceChanged(Device device) {
        }

        public void onDeviceRemoved(Device device) {
        }
    }

    private BluetoothScanner() {
        throw new RuntimeException("do not instantiate");
    }

    public static void startListening(Context context, Listener listener, List<BluetoothDeviceCriteria> criteria) {
        if (sReceiver == null) {
            sReceiver = new Receiver(context.getApplicationContext());
        }
        sReceiver.startListening(listener, criteria);
        Log.d(TAG, "startListening");
    }

    public static boolean stopListening(Listener listener) {
        Log.d(TAG, "stopListening sReceiver=" + sReceiver);
        if (sReceiver != null) {
            return sReceiver.stopListening(listener);
        }
        return false;
    }

    public static void scanNow() {
        if (sReceiver != null) {
            sReceiver.scanNow();
        }
    }

    public static void stopNow() {
        if (sReceiver != null) {
            sReceiver.stopNow();
        }
    }

    public static void removeDevice(Device device) {
        removeDevice(device.address);
    }

    public static void removeDevice(String btAddress) {
        if (sReceiver != null) {
            sReceiver.removeDevice(btAddress);
        }
    }

    private static class ClientRecord {
        public final ArrayList<Device> devices = new ArrayList<>();
        public final Listener listener;
        public final List<BluetoothDeviceCriteria> matchers;

        public ClientRecord(Listener listener2, List<BluetoothDeviceCriteria> matchers2) {
            this.listener = listener2;
            this.matchers = matchers2;
        }
    }

    private static class Receiver extends BroadcastReceiver {
        private static boolean mKeepScanning;
        private final BluetoothAdapter mBtAdapter;
        /* access modifiers changed from: private */
        public final ArrayList<ClientRecord> mClients = new ArrayList<>();
        private final Context mContext;
        /* access modifiers changed from: private */
        public final Handler mHandler = new Handler();
        /* access modifiers changed from: private */
        public final Object mListenerLock = new Object();
        private final ArrayList<Device> mPresentDevices = new ArrayList<>();
        private boolean mRegistered = false;
        /* access modifiers changed from: private */
        public final Runnable mScanTask = new Runnable() {
            public void run() {
                Receiver.this.mHandler.removeCallbacks(Receiver.this.mScanTask);
                Receiver.this.scanNow();
            }
        };
        private final Runnable mStopTask = new Runnable() {
            public void run() {
                synchronized (Receiver.this.mListenerLock) {
                    if (Receiver.this.mClients.size() != 0) {
                        throw new RuntimeException("mStopTask running with mListeners.size=" + Receiver.this.mClients.size());
                    }
                }
                Receiver.this.stopNow();
            }
        };

        public Receiver(Context context) {
            this.mContext = context;
            this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        public void startListening(Listener listener, List<BluetoothDeviceCriteria> matchers) {
            int size;
            ClientRecord newClient = new ClientRecord(listener, matchers);
            synchronized (this.mListenerLock) {
                int ptr = this.mClients.size() - 1;
                while (ptr > -1) {
                    if (this.mClients.get(ptr).listener != listener) {
                        ptr--;
                    } else {
                        throw new RuntimeException("Listener already registered: " + listener);
                    }
                }
                this.mClients.add(newClient);
                size = this.mClients.size();
            }
            if (size == 1) {
                this.mPresentDevices.clear();
                IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
                filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
                this.mContext.registerReceiver(this, filter);
                this.mRegistered = true;
            }
            mKeepScanning = true;
            int N = this.mPresentDevices.size();
            for (int i = 0; i < N; i++) {
                Device target = this.mPresentDevices.get(i);
                Iterator<BluetoothDeviceCriteria> it = newClient.matchers.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().isMatchingDevice(target.btDevice)) {
                            newClient.devices.add(target);
                            newClient.listener.onDeviceAdded(target);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            this.mHandler.removeCallbacks(this.mStopTask);
            this.mHandler.removeCallbacks(this.mScanTask);
            scanNow();
        }

        public boolean stopListening(Listener listener) {
            int size;
            boolean stopped = false;
            synchronized (this.mListenerLock) {
                int ptr = this.mClients.size() - 1;
                while (true) {
                    if (ptr <= -1) {
                        break;
                    } else if (this.mClients.get(ptr).listener == listener) {
                        this.mClients.remove(ptr);
                        stopped = true;
                        break;
                    } else {
                        ptr--;
                    }
                }
                size = this.mClients.size();
            }
            if (size == 0) {
                this.mHandler.removeCallbacks(this.mStopTask);
                this.mHandler.postDelayed(this.mStopTask, 20000);
            }
            return stopped;
        }

        public void scanNow() {
            if (this.mBtAdapter.isDiscovering()) {
                this.mBtAdapter.cancelDiscovery();
            }
            sendScanningStarted();
            this.mBtAdapter.startDiscovery();
        }

        public void stopNow() {
            int size;
            synchronized (this.mListenerLock) {
                size = this.mClients.size();
            }
            if (size == 0) {
                Log.d(BluetoothScanner.TAG, "mStopTask.run()");
                this.mHandler.removeCallbacks(this.mScanTask);
                this.mHandler.removeCallbacks(this.mStopTask);
                if (this.mBtAdapter != null) {
                    this.mBtAdapter.cancelDiscovery();
                }
                mKeepScanning = false;
                if (BluetoothAdapter.getDefaultAdapter().isEnabled() && this.mRegistered) {
                    this.mContext.unregisterReceiver(this);
                    this.mRegistered = false;
                }
            }
        }

        public void removeDevice(String btAddress) {
            int count = this.mPresentDevices.size();
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                Device d = this.mPresentDevices.get(i);
                if (btAddress.equals(d.address)) {
                    this.mPresentDevices.remove(d);
                    break;
                }
                i++;
            }
            int ptr = this.mClients.size();
            while (true) {
                ptr--;
                if (ptr > -1) {
                    ClientRecord client = this.mClients.get(ptr);
                    int devPtr = client.devices.size() - 1;
                    while (true) {
                        if (devPtr <= -1) {
                            break;
                        } else if (btAddress.equals(client.devices.get(devPtr).address)) {
                            client.devices.remove(devPtr);
                            break;
                        } else {
                            devPtr--;
                        }
                    }
                } else {
                    return;
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int i = 0;
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice btDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                String address = btDevice.getAddress();
                String name = btDevice.getName();
                if (address != null && name != null) {
                    if (name.endsWith("\u0000")) {
                        name = name.substring(0, name.length() - 1);
                    }
                    Device device = null;
                    int N = this.mPresentDevices.size();
                    while (true) {
                        if (i >= N) {
                            break;
                        }
                        Device d = this.mPresentDevices.get(i);
                        if (address.equals(d.address)) {
                            device = d;
                            break;
                        }
                        i++;
                    }
                    if (device == null) {
                        Device device2 = new Device();
                        device2.btDevice = btDevice;
                        device2.address = address;
                        device2.consecutiveMisses = -1;
                        device2.setNameString(name);
                        this.mPresentDevices.add(device2);
                        sendDeviceAdded(device2);
                        return;
                    }
                    device.consecutiveMisses = -1;
                    if (device.btName == name) {
                        return;
                    }
                    if (device.btName == null || !device.btName.equals(name)) {
                        device.setNameString(name);
                        sendDeviceChanged(device);
                    }
                }
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                for (int i2 = this.mPresentDevices.size() - 1; i2 >= 0; i2--) {
                    Device device3 = this.mPresentDevices.get(i2);
                    if (device3.consecutiveMisses < 0) {
                        device3.consecutiveMisses = 0;
                    } else if (device3.consecutiveMisses >= 4) {
                        this.mPresentDevices.remove(i2);
                        sendDeviceRemoved(device3);
                    } else {
                        device3.consecutiveMisses++;
                    }
                }
                sendScanningStopped();
                if (mKeepScanning) {
                    this.mHandler.postDelayed(this.mScanTask, 4000);
                }
            }
        }

        private void sendScanningStarted() {
            synchronized (this.mListenerLock) {
                int N = this.mClients.size();
                for (int i = 0; i < N; i++) {
                    this.mClients.get(i).listener.onScanningStarted();
                }
            }
        }

        private void sendScanningStopped() {
            synchronized (this.mListenerLock) {
                for (int i = this.mClients.size() - 1; i >= 0; i--) {
                    ClientRecord client = this.mClients.get(i);
                    client.listener.onScanningStopped(client.devices);
                }
            }
        }

        private void sendDeviceAdded(Device device) {
            synchronized (this.mListenerLock) {
                for (int ptr = this.mClients.size() - 1; ptr > -1; ptr--) {
                    ClientRecord client = this.mClients.get(ptr);
                    Iterator<BluetoothDeviceCriteria> it = client.matchers.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (it.next().isMatchingDevice(device.btDevice)) {
                            client.devices.add(device);
                            client.listener.onDeviceAdded(device);
                            break;
                        }
                    }
                }
            }
        }

        private void sendDeviceChanged(Device device) {
            synchronized (this.mListenerLock) {
                int N = this.mClients.size();
                for (int i = 0; i < N; i++) {
                    ClientRecord client = this.mClients.get(i);
                    int ptr = client.devices.size() - 1;
                    while (true) {
                        if (ptr <= -1) {
                            break;
                        } else if (client.devices.get(ptr).btDevice.getAddress().equals(device.btDevice.getAddress())) {
                            client.listener.onDeviceChanged(device);
                            break;
                        } else {
                            ptr--;
                        }
                    }
                }
            }
        }

        private void sendDeviceRemoved(Device device) {
            synchronized (this.mListenerLock) {
                for (int ptr = this.mClients.size() - 1; ptr > -1; ptr--) {
                    ClientRecord client = this.mClients.get(ptr);
                    int devPtr = client.devices.size() - 1;
                    while (true) {
                        if (devPtr <= -1) {
                            break;
                        } else if (client.devices.get(devPtr).btDevice.getAddress().equals(device.btDevice.getAddress())) {
                            client.devices.remove(devPtr);
                            client.listener.onDeviceRemoved(device);
                            break;
                        } else {
                            devPtr--;
                        }
                    }
                }
            }
        }
    }
}
