package com.mediatek.wwtv.setting.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class PinDialog extends Dialog {
    private static final int DISABLE_PIN_DURATION_MILLIS = 60000;
    private static final int MAX_WRONG_PIN_COUNT = 5;
    private static final int[] NUMBER_PICKERS_RES_ID = {R.id.first, R.id.second, R.id.third, R.id.fourth};
    private static final int PIN_DIALOG_RESULT_FAIL = 1;
    private static final int PIN_DIALOG_RESULT_SUCCESS = 0;
    public static final int PIN_DIALOG_TYPE_ENTER_PIN = 2;
    public static final int PIN_DIALOG_TYPE_NEW_PIN = 3;
    private static final int PIN_DIALOG_TYPE_OLD_PIN = 4;
    private static final String TAG = "PinDialog";
    private Context context;
    private long mDisablePinUntil;
    private View mEnterPinView;
    private final Handler mHandler = new Handler();
    private ResultListener mListener;
    private PinNumberPicker[] mPickers;
    private String mPrevPin;
    private int mRetCode;
    private TextView mTitleView;
    private int mType;
    private final Runnable mUpdateEnterPinRunnable = new Runnable() {
        public void run() {
            PinDialog.this.updateWrongPin();
        }
    };
    private int mWrongPinCount;
    private TextView mWrongPinView;

    public interface ResultListener {
        void done(boolean z);
    }

    public long getPinDisabledUntil() {
        return this.mDisablePinUntil;
    }

    public void setPinDisabledUntil(long retryDisableTimeout) {
        this.mDisablePinUntil = retryDisableTimeout;
    }

    public void setPin(String pin) {
        MtkTvConfig.getInstance().setConfigString(MtkTvConfigTypeBase.CFG_PWD_PASSWORD, pin);
    }

    public boolean isPinCorrect(String pin) {
        if (MtkTvPWDDialog.getInstance().checkPWD(pin)) {
            return true;
        }
        return false;
    }

    public boolean isPinSet() {
        return false;
    }

    public PinDialog(int type, ResultListener listener, Context context2) {
        super(context2, R.style.nav_dialog);
        this.mType = type;
        this.mListener = listener;
        this.mRetCode = 1;
        this.context = context2;
        PinNumberPicker.loadResources(context2);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mDisablePinUntil = getPinDisabledUntil();
        initView();
    }

    private void initView() {
        setContentView(R.layout.pin_dialog);
        this.mWrongPinView = (TextView) findViewById(R.id.wrong_pin);
        this.mEnterPinView = findViewById(R.id.enter_pin);
        this.mTitleView = (TextView) this.mEnterPinView.findViewById(R.id.title);
        switch (this.mType) {
            case 2:
                this.mTitleView.setText(this.context.getString(R.string.menu_parental_password_enter));
                break;
            case 3:
                if (isPinSet()) {
                    this.mTitleView.setText("Reinput New Password");
                    this.mType = 4;
                    break;
                } else {
                    this.mTitleView.setText(this.context.getString(R.string.menu_parental_password_new));
                    break;
                }
        }
        this.mPickers = new PinNumberPicker[NUMBER_PICKERS_RES_ID.length];
        for (int i = 0; i < NUMBER_PICKERS_RES_ID.length; i++) {
            this.mPickers[i] = (PinNumberPicker) findViewById(NUMBER_PICKERS_RES_ID[i]);
            this.mPickers[i].setValueRange(0, 9);
            this.mPickers[i].setPinDialog(this);
            this.mPickers[i].updateFocus();
        }
        for (int i2 = 0; i2 < NUMBER_PICKERS_RES_ID.length - 1; i2++) {
            this.mPickers[i2].setNextNumberPicker(this.mPickers[i2 + 1]);
        }
        this.mPickers[0].requestFocus();
        updateWrongPin();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
        lp.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.35d);
        lp.x = 0 - (lp.width / 3);
        getWindow().setAttributes(lp);
    }

    /* access modifiers changed from: private */
    public void updateWrongPin() {
        if ((this.mDisablePinUntil - System.currentTimeMillis()) / 1000 < 1) {
            this.mWrongPinView.setVisibility(4);
            this.mEnterPinView.setVisibility(0);
            this.mWrongPinCount = 0;
            return;
        }
        this.mEnterPinView.setVisibility(4);
        this.mWrongPinView.setVisibility(0);
        if (this.mType == 3) {
            this.mWrongPinView.setText(this.context.getString(R.string.menu_parental_password_matchinfo));
        } else {
            this.mWrongPinView.setText(this.context.getString(R.string.menu_parental_password_incorrect));
        }
        this.mHandler.postDelayed(this.mUpdateEnterPinRunnable, MessageType.delayMillis5);
    }

    private void exit(int retCode) {
        this.mRetCode = retCode;
        if (this.mListener != null) {
            this.mListener.done(this.mRetCode == 0);
        }
        dismiss();
    }

    private void handleWrongPin() {
        int i = this.mWrongPinCount + 1;
        this.mWrongPinCount = i;
        if (i >= 5) {
            this.mDisablePinUntil = System.currentTimeMillis() + MessageType.delayMillis2;
            setPinDisabledUntil(this.mDisablePinUntil);
            updateWrongPin();
            return;
        }
        this.mDisablePinUntil = System.currentTimeMillis() + MessageType.delayMillis5;
        setPinDisabledUntil(this.mDisablePinUntil);
        updateWrongPin();
    }

    private void showToast(String str) {
        Toast.makeText(this.context, str, 0).show();
    }

    /* access modifiers changed from: private */
    public void done(String pin) {
        MtkLog.d(TAG, "done: mType=" + this.mType + " pin=" + pin);
        switch (this.mType) {
            case 2:
                if (isPinCorrect(pin)) {
                    resetPinInput();
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
                    this.mTitleView.setText(this.context.getString(R.string.menu_parental_password_new_re));
                    return;
                } else if (pin.equals(this.mPrevPin)) {
                    setPin(pin);
                    exit(0);
                    this.mPrevPin = null;
                    this.mTitleView.setText(this.context.getString(R.string.menu_parental_password_new));
                    return;
                } else {
                    this.mTitleView.setText(this.context.getString(R.string.menu_parental_password_new));
                    this.mPrevPin = null;
                    handleWrongPin();
                    return;
                }
            case 4:
                if (isPinCorrect(pin)) {
                    this.mType = 3;
                    resetPinInput();
                    this.mTitleView.setText(this.context.getString(R.string.menu_parental_password_new));
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

    public void setResultListener(ResultListener listener) {
        this.mListener = listener;
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
        /* access modifiers changed from: private */
        public boolean mCancelAnimation;
        /* access modifiers changed from: private */
        public int mCurrentValue;
        private PinDialog mDialog;
        private int mMaxValue;
        private int mMinValue;
        private PinNumberPicker mNextNumberPicker;
        /* access modifiers changed from: private */
        public int mNextValue;
        /* access modifiers changed from: private */
        public int mNumberViewHeight;
        private final View mNumberViewHolder;
        private final TextView[] mNumberViews;
        /* access modifiers changed from: private */
        public final OverScroller mScroller;

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
            this.mBackgroundView = view.findViewById(R.id.focused_background);
            this.mNumberViews = new TextView[NUMBER_VIEWS_RES_ID.length];
            for (int i = 0; i < NUMBER_VIEWS_RES_ID.length; i++) {
                this.mNumberViews[i] = (TextView) view.findViewById(NUMBER_VIEWS_RES_ID[i]);
            }
            this.mNumberViewHeight = context.getResources().getDimensionPixelOffset(R.dimen.pin_number_picker_text_view_height);
            this.mScroller = new OverScroller(context);
            this.mNumberViewHolder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    PinNumberPicker.this.updateFocus();
                }
            });
            this.mNumberViewHolder.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 0) {
                        switch (keyCode) {
                            case 19:
                            case 20:
                                if (!PinNumberPicker.this.mScroller.isFinished() || PinNumberPicker.this.mCancelAnimation) {
                                    PinNumberPicker.this.endScrollAnimation();
                                }
                                if (PinNumberPicker.this.mScroller.isFinished() || PinNumberPicker.this.mCancelAnimation) {
                                    boolean unused = PinNumberPicker.this.mCancelAnimation = false;
                                    if (keyCode == 20) {
                                        int unused2 = PinNumberPicker.this.mNextValue = PinNumberPicker.this.adjustValueInValidRange(PinNumberPicker.this.mCurrentValue + 1);
                                        PinNumberPicker.this.startScrollAnimation(true);
                                        PinNumberPicker.this.mScroller.startScroll(0, 0, 0, PinNumberPicker.this.mNumberViewHeight, PinNumberPicker.this.getResources().getInteger(R.integer.pin_number_scroll_duration));
                                    } else {
                                        int unused3 = PinNumberPicker.this.mNextValue = PinNumberPicker.this.adjustValueInValidRange(PinNumberPicker.this.mCurrentValue - 1);
                                        PinNumberPicker.this.startScrollAnimation(false);
                                        PinNumberPicker.this.mScroller.startScroll(0, 0, 0, -PinNumberPicker.this.mNumberViewHeight, PinNumberPicker.this.getResources().getInteger(R.integer.pin_number_scroll_duration));
                                    }
                                    PinNumberPicker.this.updateText();
                                    PinNumberPicker.this.invalidate();
                                }
                                return true;
                        }
                    } else if (event.getAction() == 1) {
                        switch (keyCode) {
                            case 19:
                            case 20:
                                boolean unused4 = PinNumberPicker.this.mCancelAnimation = true;
                                return true;
                        }
                    }
                    return false;
                }
            });
            this.mNumberViewHolder.setScrollY(this.mNumberViewHeight);
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
                setNextValue(keyCode - 7);
                if (this.mNextNumberPicker == null) {
                    if (this.mScroller.isFinished() || this.mCancelAnimation) {
                        this.mCancelAnimation = false;
                        startScrollAnimation(true);
                        this.mScroller.startScroll(0, 0, 0, 0, getResources().getInteger(R.integer.pin_number_scroll_duration));
                        this.mCurrentValue = this.mNextValue;
                        updateText();
                        invalidate();
                        endScrollAnimation();
                    }
                    return true;
                }
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
        public void setPinDialog(PinDialog dlg) {
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
        public void setNextValue(int value) {
            if (value < this.mMinValue || value > this.mMaxValue) {
                throw new IllegalStateException("Value is not set");
            }
            this.mNextValue = adjustValueInValidRange(value);
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
                    this.mNumberViews[i].setText(String.valueOf(this.mCurrentValue));
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

        /* access modifiers changed from: private */
        public int adjustValueInValidRange(int value) {
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
