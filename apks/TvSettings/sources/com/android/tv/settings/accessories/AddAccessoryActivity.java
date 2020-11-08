package com.android.tv.settings.accessories;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.settings.R;
import com.android.tv.settings.accessories.BluetoothDevicePairer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AddAccessoryActivity extends Activity implements BluetoothDevicePairer.EventListener {
    private static final String ACTION_CONNECT_INPUT = "com.google.android.intent.action.CONNECT_INPUT";
    private static final String ADDRESS_NONE = "NONE";
    private static final int AUTOPAIR_COUNT = 10;
    private static final int CANCEL_MESSAGE_TIMEOUT = 3000;
    private static final int CONNECT_OPERATION_TIMEOUT = 15000;
    private static final boolean DEBUG = false;
    private static final int DONE_MESSAGE_TIMEOUT = 3000;
    private static final int EXIT_TIMEOUT_MILLIS = 90000;
    private static final String INTENT_EXTRA_NO_INPUT_MODE = "no_input_mode";
    private static final int KEY_DOWN_TIME = 150;
    private static final int LONG_PRESS_DURATION = 3000;
    private static final int MSG_AUTOPAIR_TICK = 8;
    private static final int MSG_OP_TIMEOUT = 4;
    private static final int MSG_PAIRING_COMPLETE = 3;
    private static final int MSG_REMOVE_CANCELED = 2;
    private static final int MSG_RESTART = 5;
    private static final int MSG_START_AUTOPAIR_COUNTDOWN = 9;
    private static final int MSG_TRIGGER_SELECT_DOWN = 6;
    private static final int MSG_TRIGGER_SELECT_UP = 7;
    private static final int MSG_UPDATE_VIEW = 1;
    private static final int PAIR_OPERATION_TIMEOUT = 120000;
    private static final int RESTART_DELAY = 3000;
    private static final String SAVED_STATE_BLUETOOTH_DEVICES = "AddAccessoryActivity.BLUETOOTH_DEVICES";
    private static final String SAVED_STATE_CONTENT_FRAGMENT = "AddAccessoryActivity.CONTENT_FRAGMENT";
    private static final String SAVED_STATE_PREFERENCE_FRAGMENT = "AddAccessoryActivity.PREFERENCE_FRAGMENT";
    private static final String TAG = "AddAccessoryActivity";
    private static final int TIME_TO_START_AUTOPAIR_COUNT = 5000;
    /* access modifiers changed from: private */
    public static Intent mIntent;
    /* access modifiers changed from: private */
    public static boolean mIsSetupWizard = false;
    private final Handler mAutoExitHandler = new Handler();
    private final Runnable mAutoExitRunnable = new Runnable() {
        public final void run() {
            AddAccessoryActivity.this.finish();
        }
    };
    private List<BluetoothDevice> mBluetoothDevices;
    /* access modifiers changed from: private */
    public BluetoothDevicePairer mBluetoothPairer;
    /* access modifiers changed from: private */
    public String mCancelledAddress = "NONE";
    private AddAccessoryContentFragment mContentFragment;
    private String mCurrentTargetAddress = "NONE";
    private String mCurrentTargetStatus = "";
    private boolean mDone = false;
    /* access modifiers changed from: private */
    public boolean mHwKeyDidSelect;
    private boolean mHwKeyDown;
    private final MessageHandler mMsgHandler = new MessageHandler();
    private boolean mNoInputMode;
    private boolean mPairingBluetooth = false;
    private boolean mPairingInBackground = false;
    private boolean mPairingSuccess = false;
    private AddAccessoryPreferenceFragment mPreferenceFragment;
    private int mPreviousStatus = 0;

    private static class MessageHandler extends Handler {
        private WeakReference<AddAccessoryActivity> mActivityRef;

        private MessageHandler() {
            this.mActivityRef = new WeakReference<>((Object) null);
        }

        public void setActivity(AddAccessoryActivity activity) {
            this.mActivityRef = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            AddAccessoryActivity activity = (AddAccessoryActivity) this.mActivityRef.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.updateView();
                        return;
                    case 2:
                        String unused = activity.mCancelledAddress = "NONE";
                        activity.updateView();
                        return;
                    case 3:
                        if (AddAccessoryActivity.mIsSetupWizard) {
                            activity.setResult(-1, AddAccessoryActivity.mIntent);
                        }
                        activity.finish();
                        return;
                    case 4:
                        activity.handlePairingTimeout();
                        return;
                    case 5:
                        if (activity.mBluetoothPairer != null) {
                            activity.mBluetoothPairer.start();
                            activity.mBluetoothPairer.cancelPairing();
                            return;
                        }
                        return;
                    case 6:
                        activity.sendKeyEvent(23, true);
                        boolean unused2 = activity.mHwKeyDidSelect = true;
                        sendEmptyMessageDelayed(7, 150);
                        activity.cancelPairingCountdown();
                        return;
                    case 7:
                        activity.sendKeyEvent(23, false);
                        return;
                    case 8:
                        int countToAutoPair = msg.arg1 - 1;
                        if (countToAutoPair <= 0) {
                            activity.setPairingText((CharSequence) null);
                            activity.startAutoPairing();
                            return;
                        }
                        activity.setPairingText(activity.getString(R.string.accessories_autopair_msg, new Object[]{Integer.valueOf(countToAutoPair)}));
                        sendMessageDelayed(obtainMessage(8, countToAutoPair, 0, (Object) null), 1000);
                        return;
                    case 9:
                        activity.setPairingText(activity.getString(R.string.accessories_autopair_msg, new Object[]{10}));
                        sendMessageDelayed(obtainMessage(8, 10, 0, (Object) null), 1000);
                        return;
                    default:
                        super.handleMessage(msg);
                        return;
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        mIsSetupWizard = mIntent.getBooleanExtra("SetupWizard", false);
        setContentView(R.layout.lb_dialog_fragment);
        this.mMsgHandler.setActivity(this);
        getWindow().addFlags(2097280);
        this.mNoInputMode = getIntent().getBooleanExtra(INTENT_EXTRA_NO_INPUT_MODE, false);
        this.mHwKeyDown = false;
        if (savedInstanceState == null) {
            this.mBluetoothDevices = new ArrayList();
        } else {
            this.mBluetoothDevices = savedInstanceState.getParcelableArrayList(SAVED_STATE_BLUETOOTH_DEVICES);
        }
        FragmentManager fm = getFragmentManager();
        if (savedInstanceState == null) {
            this.mPreferenceFragment = AddAccessoryPreferenceFragment.newInstance();
            this.mContentFragment = AddAccessoryContentFragment.newInstance();
            fm.beginTransaction().add(R.id.action_fragment, this.mPreferenceFragment).add(R.id.content_fragment, this.mContentFragment).commit();
        } else {
            this.mPreferenceFragment = (AddAccessoryPreferenceFragment) fm.getFragment(savedInstanceState, SAVED_STATE_PREFERENCE_FRAGMENT);
            this.mContentFragment = (AddAccessoryContentFragment) fm.getFragment(savedInstanceState, SAVED_STATE_CONTENT_FRAGMENT);
        }
        rearrangeViews();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, SAVED_STATE_PREFERENCE_FRAGMENT, this.mPreferenceFragment);
        getFragmentManager().putFragment(outState, SAVED_STATE_CONTENT_FRAGMENT, this.mContentFragment);
        outState.putParcelableList(SAVED_STATE_BLUETOOTH_DEVICES, this.mBluetoothDevices);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (!this.mPairingInBackground) {
            startBluetoothPairer();
        }
        this.mPairingInBackground = false;
        SystemProperties.set("vendor.mtk.factory.disable.input", "1");
    }

    public void onResume() {
        super.onResume();
        if (this.mNoInputMode) {
            this.mAutoExitHandler.postDelayed(this.mAutoExitRunnable, 90000);
        }
    }

    public void onPause() {
        super.onPause();
        this.mAutoExitHandler.removeCallbacks(this.mAutoExitRunnable);
    }

    public void onStop() {
        if (!this.mPairingBluetooth) {
            stopBluetoothPairer();
            this.mMsgHandler.removeCallbacksAndMessages((Object) null);
        } else {
            this.mPairingInBackground = true;
        }
        SystemProperties.set("vendor.mtk.factory.disable.input", "0");
        Log.d(TAG, "set source key:" + SystemProperties.getInt("vendor.mtk.factory.disable.input", 0));
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        stopBluetoothPairer();
        this.mMsgHandler.removeCallbacksAndMessages((Object) null);
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if ((keyCode == 4 || keyCode == 3) && this.mPairingBluetooth && !this.mDone) {
            cancelBtPairing();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onNewIntent(Intent intent) {
        if (!ACTION_CONNECT_INPUT.equals(intent.getAction()) || (intent.getFlags() & 4194304) != 0) {
            setIntent(intent);
            return;
        }
        KeyEvent event = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
        if (event != null && event.getKeyCode() == 225) {
            if (event.getAction() == 1) {
                onHwKeyEvent(false);
            } else if (event.getAction() == 0) {
                onHwKeyEvent(true);
            }
        }
    }

    public void onActionClicked(String address) {
        cancelPairingCountdown();
        if (!this.mDone) {
            btDeviceClicked(address);
        }
    }

    private void onHwKeyEvent(boolean keyDown) {
        if (!this.mHwKeyDown) {
            if (keyDown) {
                this.mHwKeyDown = true;
                this.mHwKeyDidSelect = false;
                this.mMsgHandler.sendEmptyMessageDelayed(6, 3000);
            }
        } else if (!keyDown) {
            this.mHwKeyDown = false;
            this.mMsgHandler.removeMessages(6);
            if (!this.mHwKeyDidSelect) {
                this.mPreferenceFragment.advanceSelection();
            }
            this.mHwKeyDidSelect = false;
        }
    }

    /* access modifiers changed from: private */
    public void sendKeyEvent(int keyCode, boolean down) {
        InputManager iMgr = (InputManager) getSystemService("input");
        if (iMgr != null) {
            long time = SystemClock.uptimeMillis();
            iMgr.injectInputEvent(new KeyEvent(time, time, down ^ true ? 1 : 0, keyCode, 0), 0);
        }
    }

    /* access modifiers changed from: protected */
    public void updateView() {
        if (this.mPreferenceFragment != null && !isFinishing()) {
            int prevNumDevices = this.mPreferenceFragment.getPreferenceScreen().getPreferenceCount();
            this.mPreferenceFragment.updateList(this.mBluetoothDevices, this.mCurrentTargetAddress, this.mCurrentTargetStatus, this.mCancelledAddress);
            if (this.mNoInputMode) {
                this.mAutoExitHandler.removeCallbacks(this.mAutoExitRunnable);
                if (this.mBluetoothDevices.size() == 1 && prevNumDevices == 0) {
                    this.mMsgHandler.sendEmptyMessageDelayed(9, 5000);
                } else {
                    this.mAutoExitHandler.postDelayed(this.mAutoExitRunnable, 90000);
                    if (this.mBluetoothDevices.size() > 1) {
                        cancelPairingCountdown();
                    }
                }
            }
            TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.content_frame));
            rearrangeViews();
        }
    }

    private void rearrangeViews() {
        int i;
        int i2;
        boolean empty = this.mBluetoothDevices.isEmpty();
        View contentView = findViewById(R.id.content_fragment);
        ViewGroup.LayoutParams contentLayoutParams = contentView.getLayoutParams();
        if (empty) {
            i = -1;
        } else {
            i = getResources().getDimensionPixelSize(R.dimen.lb_content_section_width);
        }
        contentLayoutParams.width = i;
        contentView.setLayoutParams(contentLayoutParams);
        AddAccessoryContentFragment addAccessoryContentFragment = this.mContentFragment;
        if (empty) {
            i2 = getResources().getDimensionPixelSize(R.dimen.progress_fragment_content_width);
        } else {
            i2 = getResources().getDimensionPixelSize(R.dimen.bt_progress_width_narrow);
        }
        addAccessoryContentFragment.setContentWidth(i2);
    }

    /* access modifiers changed from: private */
    public void setPairingText(CharSequence text) {
        if (this.mContentFragment != null) {
            this.mContentFragment.setExtraText(text);
        }
    }

    /* access modifiers changed from: private */
    public void cancelPairingCountdown() {
        this.mMsgHandler.removeMessages(8);
        this.mMsgHandler.removeMessages(9);
        setPairingText((CharSequence) null);
    }

    private void setTimeout(int timeout) {
        cancelTimeout();
        this.mMsgHandler.sendEmptyMessageDelayed(4, (long) timeout);
    }

    private void cancelTimeout() {
        this.mMsgHandler.removeMessages(4);
    }

    /* access modifiers changed from: protected */
    public void startAutoPairing() {
        if (this.mBluetoothDevices.size() > 0) {
            onActionClicked(this.mBluetoothDevices.get(0).getAddress());
        }
    }

    private void btDeviceClicked(String clickedAddress) {
        if (this.mBluetoothPairer != null && !this.mBluetoothPairer.isInProgress()) {
            if (this.mBluetoothPairer.getStatus() != 2 || this.mBluetoothPairer.getTargetDevice() == null) {
                for (BluetoothDevice target : this.mBluetoothDevices) {
                    if (target.getAddress().equalsIgnoreCase(clickedAddress)) {
                        this.mCancelledAddress = "NONE";
                        setPairingBluetooth(true);
                        this.mBluetoothPairer.startPairing(target);
                        return;
                    }
                }
                return;
            }
            cancelBtPairing();
        }
    }

    private void cancelBtPairing() {
        if (this.mBluetoothPairer != null) {
            if (this.mBluetoothPairer.getTargetDevice() != null) {
                this.mCancelledAddress = this.mBluetoothPairer.getTargetDevice().getAddress();
            } else {
                this.mCancelledAddress = "NONE";
            }
            this.mBluetoothPairer.cancelPairing();
        }
        this.mPairingSuccess = false;
        setPairingBluetooth(false);
        this.mMsgHandler.sendEmptyMessageDelayed(2, 3000);
    }

    private void setPairingBluetooth(boolean pairing) {
        if (this.mPairingBluetooth != pairing) {
            this.mPairingBluetooth = pairing;
        }
    }

    private void startBluetoothPairer() {
        stopBluetoothPairer();
        this.mBluetoothPairer = new BluetoothDevicePairer(this, this);
        this.mBluetoothPairer.start();
        this.mBluetoothPairer.disableAutoPairing();
        this.mPairingSuccess = false;
        statusChanged();
    }

    private void stopBluetoothPairer() {
        if (this.mBluetoothPairer != null) {
            this.mBluetoothPairer.setListener((BluetoothDevicePairer.EventListener) null);
            this.mBluetoothPairer.dispose();
            this.mBluetoothPairer = null;
        }
    }

    private String getMessageForStatus(int status) {
        int msgId;
        if (status != -1) {
            switch (status) {
                case 2:
                case 3:
                    msgId = R.string.accessory_state_pairing;
                    break;
                case 4:
                    msgId = R.string.accessory_state_connecting;
                    break;
                default:
                    return "";
            }
        } else {
            msgId = R.string.accessory_state_error;
        }
        return getString(msgId);
    }

    public void statusChanged() {
        String address;
        if (this.mBluetoothPairer != null) {
            int size = this.mBluetoothPairer.getAvailableDevices().size();
            int status = this.mBluetoothPairer.getStatus();
            int oldStatus = this.mPreviousStatus;
            this.mPreviousStatus = status;
            if (this.mBluetoothPairer.getTargetDevice() == null) {
                address = "NONE";
            } else {
                address = this.mBluetoothPairer.getTargetDevice().getAddress();
            }
            this.mBluetoothDevices.clear();
            this.mBluetoothDevices.addAll(this.mBluetoothPairer.getAvailableDevices());
            cancelTimeout();
            switch (status) {
                case -1:
                    this.mPairingSuccess = false;
                    setPairingBluetooth(false);
                    if (this.mNoInputMode) {
                        clearDeviceList();
                        break;
                    }
                    break;
                case 0:
                    if (oldStatus == 4) {
                        if (!this.mPairingSuccess) {
                            this.mBluetoothPairer.invalidateDevice(this.mBluetoothPairer.getTargetDevice());
                            this.mBluetoothPairer.start();
                            this.mBluetoothPairer.cancelPairing();
                            setPairingBluetooth(false);
                            if (!this.mPairingSuccess && BluetoothDevicePairer.hasValidInputDevice(this)) {
                                this.mPairingSuccess = true;
                                break;
                            }
                        } else {
                            this.mCurrentTargetStatus = getString(R.string.accessory_state_paired);
                            this.mMsgHandler.sendEmptyMessage(1);
                            this.mMsgHandler.sendEmptyMessageDelayed(3, 3000);
                            this.mDone = true;
                            return;
                        }
                    }
                    break;
                case 1:
                    this.mPairingSuccess = false;
                    break;
                case 3:
                    this.mPairingSuccess = true;
                    setTimeout(PAIR_OPERATION_TIMEOUT);
                    break;
                case 4:
                    setTimeout(15000);
                    break;
            }
            this.mCurrentTargetAddress = address;
            this.mCurrentTargetStatus = getMessageForStatus(status);
            this.mMsgHandler.sendEmptyMessage(1);
        }
    }

    private void clearDeviceList() {
        this.mBluetoothDevices.clear();
        this.mBluetoothPairer.clearDeviceList();
    }

    /* access modifiers changed from: private */
    public void handlePairingTimeout() {
        if (this.mPairingInBackground) {
            finish();
            return;
        }
        this.mPairingSuccess = false;
        if (this.mBluetoothPairer != null) {
            this.mBluetoothPairer.cancelPairing();
        }
        this.mCurrentTargetStatus = getString(R.string.accessory_state_error);
        this.mMsgHandler.sendEmptyMessage(1);
        this.mMsgHandler.sendEmptyMessageDelayed(5, 3000);
    }

    /* access modifiers changed from: package-private */
    public List<BluetoothDevice> getBluetoothDevices() {
        return this.mBluetoothDevices;
    }

    /* access modifiers changed from: package-private */
    public String getCurrentTargetAddress() {
        return this.mCurrentTargetAddress;
    }

    /* access modifiers changed from: package-private */
    public String getCurrentTargetStatus() {
        return this.mCurrentTargetStatus;
    }

    /* access modifiers changed from: package-private */
    public String getCancelledAddress() {
        return this.mCancelledAddress;
    }

    public void onBackPressed() {
        if (mIsSetupWizard) {
            setResult(0, mIntent);
        }
        finish();
    }
}
