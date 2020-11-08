package com.android.tv.dialog;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.tv.dialog.picker.PinPicker;
import com.android.tv.util.TvSettings;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class PinDialogFragment extends SafeDismissDialogFragment implements DialogInterface.OnKeyListener {
    private static final String ARGS_RATING = "args_rating";
    private static final String ARGS_TITLE = "args_title";
    private static final String ARGS_TYPE = "args_type";
    private static final boolean DEBUG = false;
    public static final String DIALOG_TAG = PinDialogFragment.class.getName();
    private static final int DISABLE_PIN_DURATION_MILLIS = 60000;
    private static final int MAX_WRONG_PIN_COUNT = 5;
    public static final int PIN_DIALOG_TYPE_CI_ENTER_PIN = 8;
    public static final int PIN_DIALOG_TYPE_COMMON_UNLOCK = 6;
    public static final int PIN_DIALOG_TYPE_ENTER_PIN = 2;
    public static final int PIN_DIALOG_TYPE_NEW_PIN = 3;
    private static final int PIN_DIALOG_TYPE_OLD_PIN = 4;
    public static final int PIN_DIALOG_TYPE_START_SCAN = 7;
    public static final int PIN_DIALOG_TYPE_UNLOCK_CHANNEL = 0;
    public static final int PIN_DIALOG_TYPE_UNLOCK_DVR = 5;
    public static final int PIN_DIALOG_TYPE_UNLOCK_PROGRAM = 1;
    private static final String TAG = "PinDialogFragment";
    private static final String TRACKER_LABEL = "Pin dialog";
    private long mDisablePinUntil;
    private boolean mDismissSilently;
    private View mEnterPinView;
    private final Handler mHandler = new Handler();
    private boolean mIsShowing = false;
    /* access modifiers changed from: private */
    public OnPinCheckCallback mOnPinCheckCallback;
    private PinPicker mPicker;
    private String mPin;
    private boolean mPinChecked;
    private String mPrevPin;
    private String mRatingString;
    private int mRequestType;
    private SharedPreferences mSharedPreferences;
    private String mTitleString;
    private TextView mTitleView;
    private int mType;
    private int mWrongPinCount;
    private TextView mWrongPinView;

    public interface OnPinCheckCallback {
        boolean onCheckPIN(String str);

        void onKey(int i, KeyEvent keyEvent);

        void pinExit();

        void startTimeout();

        void stopTimeout();
    }

    public interface OnPinCheckedListener {
        void onPinChecked(boolean z, int i, String str);
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public void setShowing(boolean isShowing) {
        this.mIsShowing = isShowing;
    }

    public static PinDialogFragment create(int type) {
        return create(type, (String) null);
    }

    public static PinDialogFragment create(String title, int type) {
        PinDialogFragment fragment = new PinDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_TYPE, type);
        args.putString(ARGS_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static PinDialogFragment create(int type, String rating) {
        PinDialogFragment fragment = new PinDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_TYPE, type);
        args.putString(ARGS_RATING, rating);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPinCheckCallback(OnPinCheckCallback onPinCheckCallback) {
        this.mOnPinCheckCallback = onPinCheckCallback;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mRequestType = getArguments().getInt(ARGS_TYPE, 2);
        this.mType = this.mRequestType;
        this.mRatingString = getArguments().getString(ARGS_RATING);
        this.mTitleString = getArguments().getString(ARGS_TITLE);
        setStyle(0, 2131755410);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.mDisablePinUntil = TvSettings.getDisablePinUntil(getActivity());
        if (ActivityManager.isUserAMonkey() && Math.random() < 0.5d) {
            exit(true);
        }
        this.mPinChecked = false;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.getWindow().getAttributes().windowAnimations = R.style.pin_dialog_animation;
        return dlg;
    }

    public String getTrackerLabel() {
        return TRACKER_LABEL;
    }

    public void onStart() {
        super.onStart();
        Dialog dlg = getDialog();
        if (dlg != null) {
            dlg.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.pin_dialog_width), -2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pin_dialog_smooth, container, false);
        this.mWrongPinView = (TextView) v.findViewById(R.id.wrong_pin);
        this.mEnterPinView = v.findViewById(R.id.enter_pin);
        this.mTitleView = (TextView) this.mEnterPinView.findViewById(R.id.title);
        this.mPicker = (PinPicker) v.findViewById(R.id.pin_picker);
        getDialog().setOnKeyListener(this);
        if (this.mOnPinCheckCallback != null) {
            this.mPicker.setDisableFirstCenterKey(true);
            this.mPicker.setOnKeyDownCallback(new PinPicker.OnKeyDownCallback() {
                public void onKeyDown(int keyCode) {
                    PinDialogFragment.this.mOnPinCheckCallback.startTimeout();
                }
            });
        }
        this.mPicker.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PinDialogFragment.lambda$onCreateView$0(PinDialogFragment.this, view);
            }
        });
        if (TextUtils.isEmpty(getPin())) {
            this.mType = 3;
        }
        switch (this.mType) {
            case 0:
                this.mTitleView.setText(R.string.pin_enter_unlock_channel);
                break;
            case 1:
                this.mTitleView.setText(R.string.pin_enter_unlock_program);
                break;
            case 2:
            case 7:
                this.mTitleView.setText(R.string.pin_enter_pin);
                break;
            case 3:
                if (!TextUtils.isEmpty(getPin())) {
                    this.mTitleView.setText(R.string.pin_enter_old_pin);
                    this.mType = 4;
                    break;
                } else {
                    this.mTitleView.setText(R.string.pin_enter_create_pin);
                    break;
                }
            case 5:
                if (!TvContentRating.UNRATED.equals(TvContentRating.unflattenFromString(this.mRatingString))) {
                    this.mTitleView.setText(getString(R.string.pin_enter_unlock_dvr));
                    break;
                } else {
                    this.mTitleView.setText(getString(R.string.pin_enter_unlock_dvr_unrated));
                    break;
                }
            case 6:
                this.mTitleView.setText(R.string.nav_parent_psw);
                break;
            case 8:
                this.mTitleView.setText(this.mTitleString);
                break;
        }
        if (this.mType != 3) {
            updateWrongPin();
        }
        this.mPicker.requestFocus();
        return v;
    }

    public static /* synthetic */ void lambda$onCreateView$0(PinDialogFragment pinDialogFragment, View view) {
        String pin = pinDialogFragment.getPinInput();
        if (!TextUtils.isEmpty(pin)) {
            pinDialogFragment.done(pin);
        }
    }

    /* access modifiers changed from: private */
    public void updateWrongPin() {
        if (getActivity() == null) {
            this.mHandler.removeCallbacks((Runnable) null);
            return;
        }
        int remainingSeconds = (int) ((this.mDisablePinUntil - SystemClock.uptimeMillis()) / 1000);
        if (remainingSeconds < 1) {
            this.mWrongPinView.setVisibility(4);
            this.mEnterPinView.setVisibility(0);
            this.mWrongPinCount = 0;
            return;
        }
        this.mEnterPinView.setVisibility(4);
        this.mWrongPinView.setVisibility(0);
        this.mWrongPinView.setText(getResources().getQuantityString(R.plurals.pin_enter_countdown, remainingSeconds, new Object[]{Integer.valueOf(remainingSeconds)}));
        this.mHandler.postDelayed(new Runnable() {
            public final void run() {
                PinDialogFragment.this.updateWrongPin();
            }
        }, 1000);
    }

    private void exit(boolean pinChecked) {
        this.mPinChecked = pinChecked;
        dismiss();
    }

    public void dismissSilently() {
        this.mDismissSilently = true;
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!this.mDismissSilently && (getActivity() instanceof OnPinCheckedListener)) {
            ((OnPinCheckedListener) getActivity()).onPinChecked(this.mPinChecked, this.mRequestType, this.mRatingString);
        }
        this.mDismissSilently = false;
    }

    private void handleWrongPin() {
        int i = this.mWrongPinCount + 1;
        this.mWrongPinCount = i;
        if (i >= 5) {
            this.mDisablePinUntil = SystemClock.uptimeMillis() + MessageType.delayMillis2;
            TvSettings.setDisablePinUntil(getActivity(), this.mDisablePinUntil);
            updateWrongPin();
            return;
        }
        showToast(R.string.pin_toast_wrong);
    }

    private void showToast(int resId) {
        Toast.makeText(getActivity(), resId, 0).show();
    }

    private void done(String pin) {
        MtkLog.d(TAG, "done: mType=" + this.mType + " pin=" + pin + " stored=" + getPin());
        switch (this.mType) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 8:
                if (checkPIN(pin)) {
                    exit(true);
                    return;
                }
                resetPinInput();
                handleWrongPin();
                return;
            case 3:
                resetPinInput();
                if (pin.equals("0000")) {
                    this.mPrevPin = null;
                    showToast(R.string.menu_wrong_new_pin_notify);
                    return;
                } else if (this.mPrevPin == null) {
                    this.mPrevPin = pin;
                    this.mTitleView.setText(R.string.pin_enter_again);
                    return;
                } else if (pin.equals(this.mPrevPin)) {
                    setPin(pin);
                    exit(true);
                    return;
                } else {
                    if (TextUtils.isEmpty(getPin())) {
                        this.mTitleView.setText(R.string.pin_enter_create_pin);
                    } else {
                        this.mTitleView.setText(R.string.pin_enter_new_pin);
                    }
                    this.mPrevPin = null;
                    showToast(R.string.pin_toast_not_match);
                    return;
                }
            case 4:
                resetPinInput();
                if (pin.equals(getPin())) {
                    this.mType = 3;
                    this.mTitleView.setText(R.string.pin_enter_new_pin);
                    return;
                }
                handleWrongPin();
                return;
            default:
                return;
        }
    }

    public void dismiss() {
        super.dismissAllowingStateLoss();
        resetPinInput();
        if (this.mOnPinCheckCallback != null) {
            this.mOnPinCheckCallback.pinExit();
        }
        this.mIsShowing = false;
    }

    private boolean checkPIN(String pin) {
        if (this.mOnPinCheckCallback != null) {
            return this.mOnPinCheckCallback.onCheckPIN(pin);
        }
        return TextUtils.isEmpty(getPin()) || pin.equals(getPin());
    }

    public int getType() {
        return this.mType;
    }

    public void setPin(String pin) {
        this.mPin = pin;
        MtkTvConfig.getInstance().setConfigString(MtkTvConfigTypeBase.CFG_PWD_PASSWORD, pin);
    }

    public String getPin() {
        if (this.mPin == null) {
            this.mPin = MtkTvConfig.getInstance().getConfigString(MtkTvConfigTypeBase.CFG_PWD_PASSWORD);
        }
        return this.mPin;
    }

    private String getPinInput() {
        return this.mPicker.getPinInput();
    }

    private void resetPinInput() {
        if (this.mPicker != null) {
            this.mPicker.resetPinInput();
        }
    }

    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getAction() != 1) {
            dismiss();
            if (this.mOnPinCheckCallback != null) {
                this.mOnPinCheckCallback.stopTimeout();
            }
            return true;
        } else if (this.mOnPinCheckCallback == null || event.getAction() != 0) {
            return false;
        } else {
            if (keyCode != 166 && keyCode != 167) {
                return false;
            }
            this.mOnPinCheckCallback.onKey(keyCode, event);
            return false;
        }
    }
}
