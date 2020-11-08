package com.android.tv.settings.accessories;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Keep
public class BluetoothAccessoryFragment extends SettingsPreferenceFragment {
    private static final String ARG_ACCESSORY_ADDRESS = "accessory_address";
    private static final String ARG_ACCESSORY_ICON_ID = "accessory_icon_res";
    private static final String ARG_ACCESSORY_NAME = "accessory_name";
    private static final String ARG_DEVICE = "device";
    private static final boolean DEBUG = false;
    /* access modifiers changed from: private */
    public static final UUID GATT_BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    /* access modifiers changed from: private */
    public static final UUID GATT_BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private static final String KEY_BATTERY = "battery";
    private static final String KEY_CHANGE_NAME = "changeName";
    private static final String KEY_UNPAIR = "unpair";
    private static final String SAVE_STATE_UNPAIRING = "BluetoothAccessoryActivity.unpairing";
    private static final String TAG = "BluetoothAccessoryFrag";
    private static final int UNPAIR_TIMEOUT = 5000;
    private Runnable mBailoutRunnable = new Runnable() {
        public void run() {
            if (BluetoothAccessoryFragment.this.isResumed() && !BluetoothAccessoryFragment.this.getFragmentManager().popBackStackImmediate()) {
                BluetoothAccessoryFragment.this.getActivity().onBackPressed();
            }
        }
    };
    /* access modifiers changed from: private */
    public Preference mBatteryPref;
    private BroadcastReceiver mBroadcastReceiver;
    private Preference mChangeNamePref;
    private BluetoothDevice mDevice;
    private String mDeviceAddress;
    private BluetoothGatt mDeviceGatt;
    @DrawableRes
    private int mDeviceImgId;
    private String mDeviceName;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private Preference mUnpairPref;
    /* access modifiers changed from: private */
    public boolean mUnpairing;

    public static BluetoothAccessoryFragment newInstance(String deviceAddress, String deviceName, int deviceImgId) {
        Bundle b = new Bundle(3);
        prepareArgs(b, deviceAddress, deviceName, deviceImgId);
        BluetoothAccessoryFragment f = new BluetoothAccessoryFragment();
        f.setArguments(b);
        return f;
    }

    public static void prepareArgs(Bundle b, String deviceAddress, String deviceName, int deviceImgId) {
        b.putString("accessory_address", deviceAddress);
        b.putString("accessory_name", deviceName);
        b.putInt("accessory_icon_res", deviceImgId);
    }

    public void onCreate(Bundle savedInstanceState) {
        Set<BluetoothDevice> bondedDevices;
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.mDeviceAddress = bundle.getString("accessory_address");
            this.mDeviceName = bundle.getString("accessory_name");
            this.mDeviceImgId = bundle.getInt("accessory_icon_res");
        } else {
            this.mDeviceName = getString(R.string.accessory_options);
            this.mDeviceImgId = R.drawable.ic_qs_bluetooth_not_connected;
        }
        this.mUnpairing = savedInstanceState != null && savedInstanceState.getBoolean(SAVE_STATE_UNPAIRING);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && (bondedDevices = btAdapter.getBondedDevices()) != null) {
            Iterator<BluetoothDevice> it = bondedDevices.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                BluetoothDevice device = it.next();
                if (this.mDeviceAddress.equals(device.getAddress())) {
                    this.mDevice = device;
                    break;
                }
            }
        }
        if (this.mDevice == null) {
            navigateBack();
        }
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        if (this.mDevice != null && ((this.mDevice.getType() == 2 || this.mDevice.getType() == 3) && this.mDevice.getBondState() != 10)) {
            this.mDeviceGatt = this.mDevice.connectGatt(getActivity(), true, new GattBatteryCallbacks());
        }
        IntentFilter adapterIntentFilter = new IntentFilter();
        adapterIntentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.mBroadcastReceiver = new UnpairReceiver(this, this.mDevice);
        getActivity().registerReceiver(this.mBroadcastReceiver, adapterIntentFilter);
        if (this.mDevice != null && this.mDevice.getBondState() == 10) {
            navigateBack();
        }
    }

    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(this.mBailoutRunnable);
    }

    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(SAVE_STATE_UNPAIRING, this.mUnpairing);
    }

    public void onStop() {
        super.onStop();
        if (this.mDeviceGatt != null) {
            this.mDeviceGatt.close();
        }
        getActivity().unregisterReceiver(this.mBroadcastReceiver);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.bluetooth_accessory, (String) null);
        getPreferenceScreen().setTitle((CharSequence) this.mDeviceName);
        this.mChangeNamePref = findPreference(KEY_CHANGE_NAME);
        ChangeNameFragment.prepareArgs(this.mChangeNamePref.getExtras(), this.mDeviceName, this.mDeviceImgId);
        this.mUnpairPref = findPreference(KEY_UNPAIR);
        updatePrefsForUnpairing();
        UnpairConfirmFragment.prepareArgs(this.mUnpairPref.getExtras(), this.mDevice, this.mDeviceName, this.mDeviceImgId);
        this.mBatteryPref = findPreference(KEY_BATTERY);
        this.mBatteryPref.setVisible(false);
    }

    public void setUnpairing(boolean unpairing) {
        this.mUnpairing = unpairing;
        updatePrefsForUnpairing();
    }

    private void updatePrefsForUnpairing() {
        if (this.mUnpairing) {
            this.mUnpairPref.setTitle((int) R.string.accessory_unpairing);
            this.mUnpairPref.setEnabled(false);
            this.mChangeNamePref.setEnabled(false);
            return;
        }
        this.mUnpairPref.setTitle((int) R.string.accessory_unpair);
        this.mUnpairPref.setEnabled(true);
        this.mChangeNamePref.setEnabled(true);
    }

    /* access modifiers changed from: private */
    public void navigateBack() {
        this.mHandler.removeCallbacks(this.mBailoutRunnable);
        this.mHandler.post(this.mBailoutRunnable);
    }

    /* access modifiers changed from: private */
    public void renameDevice(String deviceName) {
        this.mDeviceName = deviceName;
        if (this.mDevice != null) {
            this.mDevice.setAlias(deviceName);
            getPreferenceScreen().setTitle((CharSequence) deviceName);
            setTitle(deviceName);
            ChangeNameFragment.prepareArgs(this.mChangeNamePref.getExtras(), this.mDeviceName, this.mDeviceImgId);
            UnpairConfirmFragment.prepareArgs(this.mUnpairPref.getExtras(), this.mDevice, this.mDeviceName, this.mDeviceImgId);
        }
    }

    private class GattBatteryCallbacks extends BluetoothGattCallback {
        private GattBatteryCallbacks() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == 0 && newState == 2) {
                gatt.discoverServices();
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService battService;
            BluetoothGattCharacteristic battLevel;
            if (status == 0 && (battService = gatt.getService(BluetoothAccessoryFragment.GATT_BATTERY_SERVICE_UUID)) != null && (battLevel = battService.getCharacteristic(BluetoothAccessoryFragment.GATT_BATTERY_LEVEL_CHARACTERISTIC_UUID)) != null) {
                gatt.readCharacteristic(battLevel);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0 && BluetoothAccessoryFragment.GATT_BATTERY_LEVEL_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                final int batteryLevel = characteristic.getIntValue(17, 0).intValue();
                BluetoothAccessoryFragment.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (BluetoothAccessoryFragment.this.mBatteryPref != null && !BluetoothAccessoryFragment.this.mUnpairing) {
                            BluetoothAccessoryFragment.this.mBatteryPref.setTitle((CharSequence) BluetoothAccessoryFragment.this.getString(R.string.accessory_battery, new Object[]{Integer.valueOf(batteryLevel)}));
                            BluetoothAccessoryFragment.this.mBatteryPref.setVisible(true);
                        }
                    }
                });
            }
        }
    }

    @Keep
    public static class ChangeNameFragment extends GuidedStepFragment {
        private final MetricsFeatureProvider mMetricsFeatureProvider = new MetricsFeatureProvider();

        public static void prepareArgs(@NonNull Bundle args, String deviceName, @DrawableRes int deviceImgId) {
            args.putString("accessory_name", deviceName);
            args.putInt("accessory_icon_res", deviceImgId);
        }

        public void onStart() {
            super.onStart();
            this.mMetricsFeatureProvider.action(getContext(), 161, (Pair<Integer, Object>[]) new Pair[0]);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.accessory_change_name_title), (String) null, getArguments().getString("accessory_name"), getContext().getDrawable(getArguments().getInt("accessory_icon_res", R.drawable.ic_qs_bluetooth_not_connected)));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((CharSequence) getArguments().getString("accessory_name"))).editable(true)).build());
        }

        public long onGuidedActionEditedAndProceed(GuidedAction action) {
            if (TextUtils.equals(action.getTitle(), getArguments().getString("accessory_name")) || !TextUtils.isGraphic(action.getTitle())) {
                return -2;
            }
            ((BluetoothAccessoryFragment) getTargetFragment()).renameDevice(action.getTitle().toString());
            getFragmentManager().popBackStack();
            return -2;
        }
    }

    private static class UnpairReceiver extends BroadcastReceiver {
        private final BluetoothDevice mDevice;
        private final Fragment mFragment;

        public UnpairReceiver(Fragment fragment, BluetoothDevice device) {
            this.mFragment = fragment;
            this.mDevice = device;
        }

        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10) == 10 && Objects.equals(this.mDevice, device)) {
                if (this.mFragment instanceof BluetoothAccessoryFragment) {
                    ((BluetoothAccessoryFragment) this.mFragment).navigateBack();
                } else if (this.mFragment instanceof UnpairConfirmFragment) {
                    ((UnpairConfirmFragment) this.mFragment).navigateBack();
                } else {
                    throw new IllegalStateException("UnpairReceiver attached to wrong fragment class");
                }
            }
        }
    }

    public static class UnpairConfirmFragment extends GuidedStepFragment {
        private Runnable mBailoutRunnable = new Runnable() {
            public void run() {
                if (UnpairConfirmFragment.this.isResumed() && !UnpairConfirmFragment.this.getFragmentManager().popBackStackImmediate()) {
                    UnpairConfirmFragment.this.getActivity().onBackPressed();
                }
            }
        };
        private BroadcastReceiver mBroadcastReceiver;
        private BluetoothDevice mDevice;
        private final Handler mHandler = new Handler();
        private final MetricsFeatureProvider mMetricsFeatureProvider = new MetricsFeatureProvider();
        private final Runnable mTimeoutRunnable = new Runnable() {
            public void run() {
                UnpairConfirmFragment.this.navigateBack();
            }
        };

        public static void prepareArgs(@NonNull Bundle args, BluetoothDevice device, String deviceName, @DrawableRes int deviceImgId) {
            args.putParcelable(BluetoothAccessoryFragment.ARG_DEVICE, device);
            args.putString("accessory_name", deviceName);
            args.putInt("accessory_icon_res", deviceImgId);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mDevice = (BluetoothDevice) getArguments().getParcelable(BluetoothAccessoryFragment.ARG_DEVICE);
            super.onCreate(savedInstanceState);
        }

        public void onStart() {
            super.onStart();
            if (this.mDevice.getBondState() == 10) {
                navigateBack();
            }
            IntentFilter adapterIntentFilter = new IntentFilter();
            adapterIntentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            this.mBroadcastReceiver = new UnpairReceiver(this, this.mDevice);
            getActivity().registerReceiver(this.mBroadcastReceiver, adapterIntentFilter);
            this.mMetricsFeatureProvider.action(getContext(), 1031, (Pair<Integer, Object>[]) new Pair[0]);
        }

        public void onStop() {
            super.onStop();
            getActivity().unregisterReceiver(this.mBroadcastReceiver);
        }

        public void onDestroy() {
            super.onDestroy();
            this.mHandler.removeCallbacks(this.mTimeoutRunnable);
            this.mHandler.removeCallbacks(this.mBailoutRunnable);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.accessory_unpair), (String) null, getArguments().getString("accessory_name"), getContext().getDrawable(getArguments().getInt("accessory_icon_res", R.drawable.ic_qs_bluetooth_not_connected)));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getContext();
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-4)).build());
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-5)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -4) {
                unpairDevice();
            } else if (action.getId() == -5) {
                getFragmentManager().popBackStack();
            } else {
                super.onGuidedActionClicked(action);
            }
        }

        /* access modifiers changed from: private */
        public void navigateBack() {
            this.mHandler.removeCallbacks(this.mBailoutRunnable);
            this.mHandler.post(this.mBailoutRunnable);
        }

        private void unpairDevice() {
            if (this.mDevice != null) {
                int state = this.mDevice.getBondState();
                if (state == 11) {
                    this.mDevice.cancelBondProcess();
                }
                if (state != 10) {
                    ((BluetoothAccessoryFragment) getTargetFragment()).setUnpairing(true);
                    this.mHandler.postDelayed(this.mTimeoutRunnable, 5000);
                    if (!this.mDevice.removeBond()) {
                        Log.e(BluetoothAccessoryFragment.TAG, "Failed to unpair Bluetooth Device: " + this.mDevice.getName());
                        return;
                    }
                    return;
                }
                return;
            }
            Log.e(BluetoothAccessoryFragment.TAG, "Bluetooth device not found. Address = " + this.mDevice.getAddress());
        }
    }

    public int getMetricsCategory() {
        return 539;
    }
}
