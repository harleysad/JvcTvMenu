package com.mediatek.wwtv.tvcenter.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Toast {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    static final int LONG_DELAY = 3500;
    static final int SHORT_DELAY = 2000;
    static final String TAG = "Toast";
    static final boolean localLOGV = false;
    static Toast toast;
    final Context mContext;
    int mDuration;
    final TN mTN = new TN();
    View mView;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    private Toast(Context context) {
        this.mContext = context;
        this.mTN.mY = context.getResources().getDimensionPixelSize(17105364);
        this.mTN.mGravity = context.getResources().getInteger(17694875);
    }

    public static void show() {
        if (toast != null) {
            toast.mTN.show();
        }
    }

    public static void cancel() {
        if (toast != null) {
            toast.mTN.hide();
        }
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        this.mTN.mHorizontalMargin = horizontalMargin;
        this.mTN.mVerticalMargin = verticalMargin;
    }

    public float getHorizontalMargin() {
        return this.mTN.mHorizontalMargin;
    }

    public float getVerticalMargin() {
        return this.mTN.mVerticalMargin;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.mTN.mGravity = gravity;
        this.mTN.mX = xOffset;
        this.mTN.mY = yOffset;
    }

    public int getGravity() {
        return this.mTN.mGravity;
    }

    public int getXOffset() {
        return this.mTN.mX;
    }

    public int getYOffset() {
        return this.mTN.mY;
    }

    public WindowManager.LayoutParams getWindowParams() {
        return this.mTN.mParams;
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        if (toast == null) {
            toast = new Toast(context);
            View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(17367318, (ViewGroup) null);
            ((TextView) v.findViewById(16908299)).setText(text);
            toast.mView = v;
        } else {
            toast.setText(text);
        }
        toast.mDuration = duration;
        return toast;
    }

    public static Toast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public Toast setText(int resId) {
        return setText(this.mContext.getText(resId));
    }

    public Toast setText(CharSequence s) {
        if (this.mView != null) {
            TextView tv = (TextView) this.mView.findViewById(16908299);
            if (tv != null) {
                tv.setText(s);
                return this;
            }
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        throw new RuntimeException("This Toast was not created with Toast.makeText()");
    }

    private class TN {
        int mGravity;
        final Handler mHandler = new Handler(Looper.getMainLooper());
        final Runnable mHide = new Runnable() {
            public void run() {
                try {
                    TN.this.handleHide();
                    Toast.toast = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        float mHorizontalMargin;
        /* access modifiers changed from: private */
        public final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        final Runnable mShow = new Runnable() {
            public void run() {
                try {
                    TN.this.handleShow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        float mVerticalMargin;
        WindowManager mWM;
        int mX;
        int mY;

        TN() {
            this.mWM = (WindowManager) Toast.this.mContext.getSystemService("window");
            this.mParams.height = -2;
            this.mParams.width = -2;
            this.mParams.format = -3;
            this.mParams.windowAnimations = 16973828;
            this.mParams.setTitle(Toast.TAG);
            this.mParams.flags = Cea708CCParser.Const.CODE_C1_DF0;
        }

        public void show() {
            this.mHandler.post(this.mShow);
        }

        public void hide() {
            this.mHandler.post(this.mHide);
        }

        public void handleShow() {
            if (Toast.this.mView != null) {
                this.mHandler.removeCallbacks(this.mHide);
                int hideDelay = Toast.this.mDuration;
                if (hideDelay == 0) {
                    hideDelay = 2000;
                } else if (hideDelay == 1) {
                    hideDelay = Toast.LONG_DELAY;
                }
                this.mHandler.postDelayed(this.mHide, (long) hideDelay);
                String packageName = Toast.this.mContext.getPackageName();
                int gravity = Gravity.getAbsoluteGravity(this.mGravity, Toast.this.mContext.getResources().getConfiguration().getLayoutDirection());
                this.mParams.gravity = gravity;
                if ((gravity & 7) == 7) {
                    this.mParams.horizontalWeight = 1.0f;
                }
                if ((gravity & 112) == 112) {
                    this.mParams.verticalWeight = 1.0f;
                }
                this.mParams.x = this.mX;
                this.mParams.y = this.mY;
                this.mParams.verticalMargin = this.mVerticalMargin;
                this.mParams.horizontalMargin = this.mHorizontalMargin;
                this.mParams.packageName = packageName;
                handleHide();
                this.mWM.addView(Toast.this.mView, this.mParams);
                trySendAccessibilityEvent();
            }
        }

        private void trySendAccessibilityEvent() {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(Toast.this.mView.getContext());
            if (accessibilityManager.isEnabled()) {
                AccessibilityEvent event = AccessibilityEvent.obtain(64);
                event.setClassName(getClass().getName());
                event.setPackageName(Toast.this.mView.getContext().getPackageName());
                Toast.this.mView.dispatchPopulateAccessibilityEvent(event);
                accessibilityManager.sendAccessibilityEvent(event);
            }
        }

        public void handleHide() {
            if (Toast.this.mView != null && Toast.this.mView.getParent() != null) {
                this.mWM.removeViewImmediate(Toast.this.mView);
            }
        }
    }
}
