package com.android.tv.settings.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.TextView;
import android.widget.Toast;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.PinDialogFragment;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class PinDialogFragment extends SafeDismissDialogFragment {
    protected static final String ARG_TYPE = "type";
    private static final boolean DEBUG = false;
    public static final String DIALOG_TAG = PinDialogFragment.class.getName();
    private static final int DISABLE_PIN_DURATION_MILLIS = 60000;
    private static final int MAX_WRONG_PIN_COUNT = 5;
    private static final int[] NUMBER_PICKERS_RES_ID = {R.id.first, R.id.second, R.id.third, R.id.fourth};
    private static final int PIN_DIALOG_RESULT_FAIL = 1;
    private static final int PIN_DIALOG_RESULT_SUCCESS = 0;
    public static final int PIN_DIALOG_TYPE_DELETE_PIN = 5;
    public static final int PIN_DIALOG_TYPE_ENTER_PIN = 2;
    public static final int PIN_DIALOG_TYPE_NEW_PIN = 3;
    private static final int PIN_DIALOG_TYPE_OLD_PIN = 4;
    public static final int PIN_DIALOG_TYPE_UNLOCK_CHANNEL = 0;
    public static final int PIN_DIALOG_TYPE_UNLOCK_PROGRAM = 1;
    private static final String TAG = "PinDialogFragment";
    private long mDisablePinUntil;
    private View mEnterPinView;
    private final Handler mHandler = new Handler();
    private String mOriginalPin;
    private PinNumberPicker[] mPickers;
    private String mPrevPin;
    private int mRetCode = 1;
    private TextView mTitleView;
    private int mType;
    private final Runnable mUpdateEnterPinRunnable = new Runnable() {
        public final void run() {
            PinDialogFragment.this.updateWrongPin();
        }
    };
    private int mWrongPinCount;
    private TextView mWrongPinView;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PinDialogType {
    }

    public interface ResultListener {
        void pinFragmentDone(int i, boolean z);
    }

    public abstract void deletePin(String str);

    public abstract long getPinDisabledUntil();

    public abstract boolean isPinCorrect(String str);

    public abstract boolean isPinSet();

    public abstract void setPin(String str, String str2);

    public abstract void setPinDisabledUntil(long j);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(1, 0);
        this.mDisablePinUntil = getPinDisabledUntil();
        if (getArguments().containsKey(ARG_TYPE)) {
            this.mType = getArguments().getInt(ARG_TYPE);
            return;
        }
        throw new IllegalStateException("Fragment arguments must specify type");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.getWindow().getAttributes().windowAnimations = R.style.pin_dialog_animation;
        PinNumberPicker.loadResources(dlg.getContext());
        return dlg;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pin_dialog, container, false);
        this.mWrongPinView = (TextView) v.findViewById(R.id.wrong_pin);
        this.mEnterPinView = v.findViewById(R.id.enter_pin);
        if (this.mEnterPinView != null) {
            this.mTitleView = (TextView) this.mEnterPinView.findViewById(R.id.title);
            if (!isPinSet()) {
                this.mType = 3;
            }
            int i = this.mType;
            if (i != 5) {
                switch (i) {
                    case 0:
                        this.mTitleView.setText(R.string.pin_enter_unlock_channel);
                        break;
                    case 1:
                        this.mTitleView.setText(R.string.pin_enter_unlock_program);
                        break;
                    case 2:
                        break;
                    case 3:
                        if (isPinSet()) {
                            this.mTitleView.setText(R.string.pin_enter_old_pin);
                            this.mType = 4;
                            break;
                        } else {
                            this.mTitleView.setText(R.string.pin_enter_new_pin);
                            break;
                        }
                }
            }
            this.mTitleView.setText(R.string.pin_enter_pin);
            this.mPickers = new PinNumberPicker[NUMBER_PICKERS_RES_ID.length];
            for (int i2 = 0; i2 < NUMBER_PICKERS_RES_ID.length; i2++) {
                this.mPickers[i2] = (PinNumberPicker) v.findViewById(NUMBER_PICKERS_RES_ID[i2]);
                this.mPickers[i2].setValueRange(0, 9);
                this.mPickers[i2].setPinDialogFragment(this);
                this.mPickers[i2].updateFocus();
            }
            for (int i3 = 0; i3 < NUMBER_PICKERS_RES_ID.length - 1; i3++) {
                this.mPickers[i3].setNextNumberPicker(this.mPickers[i3 + 1]);
            }
            if (this.mType != 3) {
                updateWrongPin();
            }
            if (savedInstanceState == null) {
                this.mPickers[0].requestFocus();
            }
            return v;
        }
        throw new IllegalStateException("R.id.enter_pin missing!");
    }

    /* access modifiers changed from: private */
    public void updateWrongPin() {
        if (getActivity() == null) {
            this.mHandler.removeCallbacks((Runnable) null);
            return;
        }
        long secondsLeft = (this.mDisablePinUntil - System.currentTimeMillis()) / 1000;
        if (secondsLeft < 1) {
            this.mWrongPinView.setVisibility(8);
            this.mEnterPinView.setVisibility(0);
            this.mWrongPinCount = 0;
            return;
        }
        this.mEnterPinView.setVisibility(8);
        this.mWrongPinView.setVisibility(0);
        this.mWrongPinView.setText(getResources().getString(R.string.pin_enter_wrong_seconds, new Object[]{Long.valueOf(secondsLeft)}));
        this.mHandler.postDelayed(this.mUpdateEnterPinRunnable, 1000);
    }

    private void exit(int retCode) {
        this.mRetCode = retCode;
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        boolean result = this.mRetCode == 0;
        Fragment f = getTargetFragment();
        if (f instanceof ResultListener) {
            ((ResultListener) f).pinFragmentDone(getTargetRequestCode(), result);
        } else if (getActivity() instanceof ResultListener) {
            ((ResultListener) getActivity()).pinFragmentDone(getTargetRequestCode(), result);
        }
    }

    private void handleWrongPin() {
        int i = this.mWrongPinCount + 1;
        this.mWrongPinCount = i;
        if (i >= 5) {
            this.mDisablePinUntil = System.currentTimeMillis() + 60000;
            setPinDisabledUntil(this.mDisablePinUntil);
            updateWrongPin();
            return;
        }
        showToast(R.string.pin_toast_wrong);
    }

    private void showToast(int resId) {
        Toast.makeText(getActivity(), resId, 0).show();
    }

    /* access modifiers changed from: private */
    public void done(String pin) {
        switch (this.mType) {
            case 0:
            case 1:
            case 2:
            case 5:
                if (!isPinSet() || isPinCorrect(pin)) {
                    if (this.mType == 5) {
                        deletePin(pin);
                    }
                    exit(0);
                    return;
                }
                resetPinInput();
                handleWrongPin();
                return;
            case 3:
                resetPinInput();
                if (this.mPrevPin == null) {
                    this.mPrevPin = pin;
                    this.mTitleView.setText(R.string.pin_enter_again);
                    return;
                } else if (pin.equals(this.mPrevPin)) {
                    setPin(pin, this.mOriginalPin);
                    exit(0);
                    return;
                } else {
                    this.mTitleView.setText(R.string.pin_enter_new_pin);
                    this.mPrevPin = null;
                    showToast(R.string.pin_toast_not_match);
                    return;
                }
            case 4:
                resetPinInput();
                if (isPinCorrect(pin)) {
                    this.mOriginalPin = pin;
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

    public int getType() {
        return this.mType;
    }

    /* access modifiers changed from: private */
    public String getPinInput() {
        String result = "";
        try {
            for (PinNumberPicker pnp : this.mPickers) {
                pnp.updateText();
                result = result + pnp.getValue();
            }
            return result;
        } catch (IllegalStateException e) {
            return "";
        }
    }

    private void resetPinInput() {
        for (PinNumberPicker pnp : this.mPickers) {
            pnp.setValueRange(0, 9);
        }
        this.mPickers[0].requestFocus();
    }

    public static final class PinNumberPicker extends FrameLayout {
        private static final int CURRENT_NUMBER_VIEW_INDEX = 2;
        private static final int[] NUMBER_VIEWS_RES_ID = {R.id.previous2_number, R.id.previous_number, R.id.current_number, R.id.next_number, R.id.next2_number};
        private static Animator sAdjacentNumberEnterAnimator;
        private static Animator sAdjacentNumberExitAnimator;
        private static float sAlphaForAdjacentNumber;
        private static float sAlphaForFocusedNumber;
        private static Animator sFocusedNumberEnterAnimator;
        private static Animator sFocusedNumberExitAnimator;
        private final View mBackgroundView;
        private boolean mCancelAnimation;
        private int mCurrentValue;
        private PinDialogFragment mDialog;
        private int mMaxValue;
        private int mMinValue;
        private PinNumberPicker mNextNumberPicker;
        private int mNextValue;
        private final int mNumberViewHeight;
        private final View mNumberViewHolder;
        private final TextView[] mNumberViews;
        private final OverScroller mScroller;

        public PinNumberPicker(Context context) {
            this(context, (AttributeSet) null);
        }

        public PinNumberPicker(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public PinNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public PinNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            View view = inflate(context, R.layout.pin_number_picker, this);
            this.mNumberViewHolder = view.findViewById(R.id.number_view_holder);
            if (this.mNumberViewHolder != null) {
                this.mBackgroundView = view.findViewById(R.id.focused_background);
                this.mNumberViews = new TextView[NUMBER_VIEWS_RES_ID.length];
                for (int i = 0; i < NUMBER_VIEWS_RES_ID.length; i++) {
                    this.mNumberViews[i] = (TextView) view.findViewById(NUMBER_VIEWS_RES_ID[i]);
                }
                this.mNumberViewHeight = context.getResources().getDimensionPixelOffset(R.dimen.pin_number_picker_text_view_height);
                this.mScroller = new OverScroller(context);
                this.mNumberViewHolder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public final void onFocusChange(View view, boolean z) {
                        PinDialogFragment.PinNumberPicker.this.updateFocus();
                    }
                });
                this.mNumberViewHolder.setOnKeyListener(new View.OnKeyListener() {
                    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                        return PinDialogFragment.PinNumberPicker.lambda$new$1(PinDialogFragment.PinNumberPicker.this, view, i, keyEvent);
                    }
                });
                this.mNumberViewHolder.setScrollY(this.mNumberViewHeight);
                return;
            }
            throw new IllegalStateException("R.id.number_view_holder missing!");
        }

        public static /* synthetic */ boolean lambda$new$1(PinNumberPicker pinNumberPicker, View v, int keyCode, KeyEvent event) {
            if (event.getAction() == 0) {
                switch (keyCode) {
                    case 19:
                    case 20:
                        if (!pinNumberPicker.mScroller.isFinished() || pinNumberPicker.mCancelAnimation) {
                            pinNumberPicker.endScrollAnimation();
                        }
                        if (pinNumberPicker.mScroller.isFinished() || pinNumberPicker.mCancelAnimation) {
                            pinNumberPicker.mCancelAnimation = false;
                            if (keyCode == 20) {
                                pinNumberPicker.mNextValue = pinNumberPicker.adjustValueInValidRange(pinNumberPicker.mCurrentValue + 1);
                                pinNumberPicker.startScrollAnimation(true);
                                pinNumberPicker.mScroller.startScroll(0, 0, 0, pinNumberPicker.mNumberViewHeight, pinNumberPicker.getResources().getInteger(R.integer.pin_number_scroll_duration));
                            } else {
                                pinNumberPicker.mNextValue = pinNumberPicker.adjustValueInValidRange(pinNumberPicker.mCurrentValue - 1);
                                pinNumberPicker.startScrollAnimation(false);
                                pinNumberPicker.mScroller.startScroll(0, 0, 0, -pinNumberPicker.mNumberViewHeight, pinNumberPicker.getResources().getInteger(R.integer.pin_number_scroll_duration));
                            }
                            pinNumberPicker.updateText();
                            pinNumberPicker.invalidate();
                        }
                        return true;
                }
            } else if (event.getAction() == 1) {
                switch (keyCode) {
                    case 19:
                    case 20:
                        pinNumberPicker.mCancelAnimation = true;
                        return true;
                }
            }
            return false;
        }

        static void loadResources(Context context) {
            if (sFocusedNumberEnterAnimator == null) {
                TypedValue outValue = new TypedValue();
                context.getResources().getValue(R.dimen.pin_alpha_for_focused_number, outValue, true);
                sAlphaForFocusedNumber = outValue.getFloat();
                context.getResources().getValue(R.dimen.pin_alpha_for_adjacent_number, outValue, true);
                sAlphaForAdjacentNumber = outValue.getFloat();
                sFocusedNumberEnterAnimator = AnimatorInflater.loadAnimator(context, R.animator.pin_focused_number_enter);
                sFocusedNumberExitAnimator = AnimatorInflater.loadAnimator(context, R.animator.pin_focused_number_exit);
                sAdjacentNumberEnterAnimator = AnimatorInflater.loadAnimator(context, R.animator.pin_adjacent_number_enter);
                sAdjacentNumberExitAnimator = AnimatorInflater.loadAnimator(context, R.animator.pin_adjacent_number_exit);
            }
        }

        public void computeScroll() {
            super.computeScroll();
            if (this.mScroller.computeScrollOffset()) {
                this.mNumberViewHolder.setScrollY(this.mScroller.getCurrY() + this.mNumberViewHeight);
                updateText();
                invalidate();
            } else if (this.mCurrentValue != this.mNextValue) {
                this.mCurrentValue = this.mNextValue;
            }
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getAction() != 1) {
                return super.dispatchKeyEvent(event);
            }
            int keyCode = event.getKeyCode();
            if (keyCode >= 7 && keyCode <= 16) {
                jumpNextValue(keyCode - 7);
            } else if (!(keyCode == 23 || keyCode == 66)) {
                return super.dispatchKeyEvent(event);
            }
            if (this.mNextNumberPicker == null) {
                String pin = this.mDialog.getPinInput();
                if (!TextUtils.isEmpty(pin)) {
                    this.mDialog.done(pin);
                }
            } else {
                this.mNextNumberPicker.requestFocus();
            }
            return true;
        }

        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            this.mNumberViewHolder.setFocusable(enabled);
            for (int i = 0; i < NUMBER_VIEWS_RES_ID.length; i++) {
                this.mNumberViews[i].setEnabled(enabled);
            }
        }

        /* access modifiers changed from: package-private */
        public void startScrollAnimation(boolean scrollUp) {
            if (scrollUp) {
                sAdjacentNumberExitAnimator.setTarget(this.mNumberViews[1]);
                sFocusedNumberExitAnimator.setTarget(this.mNumberViews[2]);
                sFocusedNumberEnterAnimator.setTarget(this.mNumberViews[3]);
                sAdjacentNumberEnterAnimator.setTarget(this.mNumberViews[4]);
            } else {
                sAdjacentNumberEnterAnimator.setTarget(this.mNumberViews[0]);
                sFocusedNumberEnterAnimator.setTarget(this.mNumberViews[1]);
                sFocusedNumberExitAnimator.setTarget(this.mNumberViews[2]);
                sAdjacentNumberExitAnimator.setTarget(this.mNumberViews[3]);
            }
            sAdjacentNumberExitAnimator.start();
            sFocusedNumberExitAnimator.start();
            sFocusedNumberEnterAnimator.start();
            sAdjacentNumberEnterAnimator.start();
        }

        /* access modifiers changed from: package-private */
        public void endScrollAnimation() {
            sAdjacentNumberExitAnimator.end();
            sFocusedNumberExitAnimator.end();
            sFocusedNumberEnterAnimator.end();
            sAdjacentNumberEnterAnimator.end();
            this.mCurrentValue = this.mNextValue;
            this.mNumberViews[1].setAlpha(sAlphaForAdjacentNumber);
            this.mNumberViews[2].setAlpha(sAlphaForFocusedNumber);
            this.mNumberViews[3].setAlpha(sAlphaForAdjacentNumber);
        }

        /* access modifiers changed from: package-private */
        public void setValueRange(int min, int max) {
            if (min <= max) {
                this.mMinValue = min;
                this.mMaxValue = max;
                int i = this.mMinValue - 1;
                this.mCurrentValue = i;
                this.mNextValue = i;
                clearText();
                this.mNumberViews[2].setText("—");
                return;
            }
            throw new IllegalArgumentException("The min value should be greater than or equal to the max value");
        }

        /* access modifiers changed from: package-private */
        public void setPinDialogFragment(PinDialogFragment dlg) {
            this.mDialog = dlg;
        }

        /* access modifiers changed from: package-private */
        public void setNextNumberPicker(PinNumberPicker picker) {
            this.mNextNumberPicker = picker;
        }

        /* access modifiers changed from: package-private */
        public int getValue() {
            if (this.mCurrentValue >= this.mMinValue && this.mCurrentValue <= this.mMaxValue) {
                return this.mCurrentValue;
            }
            throw new IllegalStateException("Value is not set");
        }

        /* access modifiers changed from: package-private */
        public void jumpNextValue(int value) {
            if (value < this.mMinValue || value > this.mMaxValue) {
                throw new IllegalStateException("Value is not set");
            }
            int adjustValueInValidRange = adjustValueInValidRange(value);
            this.mCurrentValue = adjustValueInValidRange;
            this.mNextValue = adjustValueInValidRange;
            updateText();
        }

        /* access modifiers changed from: package-private */
        public void updateFocus() {
            endScrollAnimation();
            if (this.mNumberViewHolder.isFocused()) {
                this.mBackgroundView.setVisibility(0);
                updateText();
                return;
            }
            this.mBackgroundView.setVisibility(8);
            if (!this.mScroller.isFinished()) {
                this.mCurrentValue = this.mNextValue;
                this.mScroller.abortAnimation();
            }
            clearText();
            this.mNumberViewHolder.setScrollY(this.mNumberViewHeight);
        }

        private void clearText() {
            for (int i = 0; i < NUMBER_VIEWS_RES_ID.length; i++) {
                if (i != 2) {
                    this.mNumberViews[i].setText("");
                } else if (this.mCurrentValue >= this.mMinValue && this.mCurrentValue <= this.mMaxValue) {
                    this.mNumberViews[i].setText("•");
                }
            }
        }

        /* access modifiers changed from: private */
        public void updateText() {
            if (this.mNumberViewHolder.isFocused()) {
                if (this.mCurrentValue < this.mMinValue || this.mCurrentValue > this.mMaxValue) {
                    int i = this.mMinValue;
                    this.mCurrentValue = i;
                    this.mNextValue = i;
                }
                int value = adjustValueInValidRange(this.mCurrentValue - 2);
                for (int i2 = 0; i2 < NUMBER_VIEWS_RES_ID.length; i2++) {
                    this.mNumberViews[i2].setText(String.valueOf(adjustValueInValidRange(value)));
                    value = adjustValueInValidRange(value + 1);
                }
            }
        }

        private int adjustValueInValidRange(int value) {
            int interval = (this.mMaxValue - this.mMinValue) + 1;
            if (value < this.mMinValue - interval || value > this.mMaxValue + interval) {
                throw new IllegalArgumentException("The value( " + value + ") is too small or too big to adjust");
            } else if (value < this.mMinValue) {
                return value + interval;
            } else {
                if (value > this.mMaxValue) {
                    return value - interval;
                }
                return value;
            }
        }
    }
}
