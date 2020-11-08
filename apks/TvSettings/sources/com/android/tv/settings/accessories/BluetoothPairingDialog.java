package com.android.tv.settings.accessories;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.old.Action;
import com.android.tv.settings.dialog.old.ActionFragment;
import com.android.tv.settings.dialog.old.DialogActivity;
import com.android.tv.settings.util.AccessibilityHelper;
import java.util.ArrayList;
import java.util.Locale;

public class BluetoothPairingDialog extends DialogActivity {
    private static final int BLUETOOTH_PASSKEY_MAX_LENGTH = 6;
    private static final int BLUETOOTH_PIN_MAX_LENGTH = 16;
    private static final boolean DEBUG = false;
    private static final String KEY_CANCEL = "action_cancel";
    private static final String KEY_PAIR = "action_pair";
    private static final String TAG = "BluetoothPairingDialog";
    /* access modifiers changed from: private */
    public BluetoothDevice mDevice;
    private boolean mPairingInProgress = false;
    private String mPairingKey;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                int bondState = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                if (bondState == 12 || bondState == 10) {
                    BluetoothPairingDialog.this.dismiss();
                }
            } else if ("android.bluetooth.device.action.PAIRING_CANCEL".equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (device == null || device.equals(BluetoothPairingDialog.this.mDevice)) {
                    BluetoothPairingDialog.this.dismiss();
                }
            }
        }
    };
    private int mType;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) == 0) {
            Log.e(TAG, "Device not provisioned. finishing");
            finish();
            return;
        }
        Intent intent = getIntent();
        if (!"android.bluetooth.device.action.PAIRING_REQUEST".equals(intent.getAction())) {
            Log.e(TAG, "Error: this activity may be started only with intent android.bluetooth.device.action.PAIRING_REQUEST");
            finish();
            return;
        }
        this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        this.mType = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
        switch (this.mType) {
            case 0:
            case 1:
                createUserEntryDialog();
                break;
            case 2:
                int passkey = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE);
                if (passkey != Integer.MIN_VALUE) {
                    this.mPairingKey = String.format(Locale.US, "%06d", new Object[]{Integer.valueOf(passkey)});
                    createConfirmationDialog();
                    break;
                } else {
                    Log.e(TAG, "Invalid Confirmation Passkey received, not showing any dialog");
                    finish();
                    return;
                }
            case 3:
            case 6:
                createConfirmationDialog();
                break;
            case 4:
            case 5:
                int pairingKey = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE);
                if (pairingKey != Integer.MIN_VALUE) {
                    if (this.mType == 4) {
                        this.mPairingKey = String.format("%06d", new Object[]{Integer.valueOf(pairingKey)});
                    } else {
                        this.mPairingKey = String.format("%04d", new Object[]{Integer.valueOf(pairingKey)});
                    }
                    createConfirmationDialog();
                    break;
                } else {
                    Log.e(TAG, "Invalid Confirmation Passkey or PIN received, not showing any dialog");
                    finish();
                    return;
                }
            default:
                Log.e(TAG, "Incorrect pairing type received, not showing any dialog");
                finish();
                return;
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        View topLayout = ((ViewGroup) findViewById(16908290)).getChildAt(0);
        ColorDrawable bgDrawable = new ColorDrawable(getColor(R.color.dialog_activity_background));
        bgDrawable.setAlpha(255);
        topLayout.setBackground(bgDrawable);
        getWindow().addFlags(6815872);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.PAIRING_CANCEL");
        filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        registerReceiver(this.mReceiver, filter);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        unregisterReceiver(this.mReceiver);
        if (!this.mPairingInProgress) {
            cancelPairing();
        }
        dismiss();
        super.onPause();
    }

    public void onActionClicked(Action action) {
        String key = action.getKey();
        if (KEY_PAIR.equals(key)) {
            onPair((String) null);
            dismiss();
        } else if (KEY_CANCEL.equals(key)) {
            cancelPairing();
        }
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == 4) {
            cancelPairing();
        }
        return super.onKeyDown(keyCode, event);
    }

    private ArrayList<Action> getActions() {
        ArrayList<Action> actions = new ArrayList<>();
        switch (this.mType) {
            case 2:
            case 3:
            case 6:
                actions.add(new Action.Builder().key(KEY_PAIR).title(getString(R.string.bluetooth_pair)).build());
                actions.add(new Action.Builder().key(KEY_CANCEL).title(getString(R.string.bluetooth_cancel)).build());
                break;
            case 4:
            case 5:
                actions.add(new Action.Builder().key(KEY_CANCEL).title(getString(R.string.bluetooth_cancel)).build());
                break;
        }
        return actions;
    }

    /* access modifiers changed from: private */
    public void dismiss() {
        finish();
    }

    private void cancelPairing() {
        this.mDevice.cancelPairingUserInput();
    }

    private void createUserEntryDialog() {
        getFragmentManager().beginTransaction().replace(16908290, EntryDialogFragment.newInstance(this.mDevice, this.mType)).commit();
    }

    private void createConfirmationDialog() {
        setContentAndActionFragments(ConfirmationDialogFragment.newInstance(this.mDevice, this.mPairingKey, this.mType), ActionFragment.newInstance(getActions()));
    }

    /* access modifiers changed from: private */
    public void onPair(String value) {
        switch (this.mType) {
            case 0:
                byte[] pinBytes = BluetoothDevice.convertPinToBytes(value);
                if (pinBytes != null) {
                    this.mDevice.setPin(pinBytes);
                    this.mPairingInProgress = true;
                    return;
                }
                return;
            case 1:
                try {
                    this.mDevice.setPasskey(Integer.parseInt(value));
                    this.mPairingInProgress = true;
                    return;
                } catch (NumberFormatException e) {
                    Log.d(TAG, "pass key " + value + " is not an integer");
                    return;
                }
            case 2:
            case 3:
                this.mDevice.setPairingConfirmation(true);
                this.mPairingInProgress = true;
                return;
            case 4:
            case 5:
                return;
            case 6:
                this.mDevice.setRemoteOutOfBandData();
                this.mPairingInProgress = true;
                return;
            default:
                Log.e(TAG, "Incorrect pairing type received");
                return;
        }
    }

    public int getMetricsCategory() {
        return 613;
    }

    public static class EntryDialogFragment extends Fragment {
        private static final String ARG_DEVICE = "ConfirmationDialogFragment.DEVICE";
        private static final String ARG_TYPE = "ConfirmationDialogFragment.TYPE";
        private BluetoothDevice mDevice;
        private int mType;

        public static EntryDialogFragment newInstance(BluetoothDevice device, int type) {
            EntryDialogFragment fragment = new EntryDialogFragment();
            Bundle b = new Bundle(2);
            fragment.setArguments(b);
            b.putParcelable(ARG_DEVICE, device);
            b.putInt(ARG_TYPE, type);
            return fragment;
        }

        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            this.mDevice = (BluetoothDevice) args.getParcelable(ARG_DEVICE);
            this.mType = args.getInt(ARG_TYPE);
        }

        @Nullable
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            int maxLength;
            String instructions;
            View v = inflater.inflate(R.layout.bt_pairing_passkey_entry, container, false);
            TextView titleText = (TextView) v.findViewById(R.id.title_text);
            final EditText textInput = (EditText) v.findViewById(R.id.text_input);
            textInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String value = textInput.getText().toString();
                    if (actionId != 5 && (actionId != 0 || event.getAction() != 0)) {
                        return true;
                    }
                    ((BluetoothPairingDialog) EntryDialogFragment.this.getActivity()).onPair(value);
                    return true;
                }
            });
            switch (this.mType) {
                case 0:
                    instructions = getString(R.string.bluetooth_enter_pin_msg, new Object[]{this.mDevice.getName()});
                    ((TextView) v.findViewById(R.id.hint_text)).setText(getString(R.string.bluetooth_pin_values_hint));
                    textInput.setInputType(2);
                    maxLength = 16;
                    break;
                case 1:
                    instructions = getString(R.string.bluetooth_enter_passkey_msg, new Object[]{this.mDevice.getName()});
                    maxLength = 6;
                    textInput.setInputType(1);
                    break;
                default:
                    throw new IllegalStateException("Incorrect pairing type for createPinEntryView: " + this.mType);
            }
            titleText.setText(Html.fromHtml(instructions));
            textInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            return v;
        }
    }

    public static class ConfirmationDialogFragment extends Fragment {
        private static final String ARG_DEVICE = "ConfirmationDialogFragment.DEVICE";
        private static final String ARG_PAIRING_KEY = "ConfirmationDialogFragment.PAIRING_KEY";
        private static final String ARG_TYPE = "ConfirmationDialogFragment.TYPE";
        private BluetoothDevice mDevice;
        private String mPairingKey;
        private int mType;

        public static ConfirmationDialogFragment newInstance(BluetoothDevice device, String pairingKey, int type) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle b = new Bundle(3);
            b.putParcelable(ARG_DEVICE, device);
            b.putString(ARG_PAIRING_KEY, pairingKey);
            b.putInt(ARG_TYPE, type);
            fragment.setArguments(b);
            return fragment;
        }

        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            this.mDevice = (BluetoothDevice) args.getParcelable(ARG_DEVICE);
            this.mPairingKey = args.getString(ARG_PAIRING_KEY);
            this.mType = args.getInt(ARG_TYPE);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            String instructions;
            View v = inflater.inflate(R.layout.bt_pairing_passkey_display, container, false);
            TextView titleText = (TextView) v.findViewById(R.id.title);
            TextView instructionText = (TextView) v.findViewById(R.id.pairing_instructions);
            titleText.setText(getString(R.string.bluetooth_pairing_request));
            if (AccessibilityHelper.forceFocusableViews(getActivity())) {
                titleText.setFocusable(true);
                titleText.setFocusableInTouchMode(true);
                instructionText.setFocusable(true);
                instructionText.setFocusableInTouchMode(true);
            }
            switch (this.mType) {
                case 2:
                    instructions = getString(R.string.bluetooth_confirm_passkey_msg, new Object[]{this.mDevice.getName(), this.mPairingKey});
                    break;
                case 3:
                case 6:
                    instructions = getString(R.string.bluetooth_incoming_pairing_msg, new Object[]{this.mDevice.getName()});
                    break;
                case 4:
                case 5:
                    instructions = getString(R.string.bluetooth_display_passkey_pin_msg, new Object[]{this.mDevice.getName(), this.mPairingKey});
                    if (this.mType != 4) {
                        if (this.mType == 5) {
                            this.mDevice.setPin(BluetoothDevice.convertPinToBytes(this.mPairingKey));
                            break;
                        }
                    } else {
                        this.mDevice.setPairingConfirmation(true);
                        break;
                    }
                    break;
                default:
                    instructions = "";
                    break;
            }
            instructionText.setText(Html.fromHtml(instructions));
            return v;
        }
    }
}
