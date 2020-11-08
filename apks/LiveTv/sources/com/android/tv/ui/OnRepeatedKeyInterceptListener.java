package com.android.tv.ui;

import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.BaseGridView;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.wwtv.tvcenter.util.WeakHandler;

public class OnRepeatedKeyInterceptListener implements BaseGridView.OnKeyInterceptListener {
    private static final boolean DEBUG = false;
    private static final int[] MAX_SKIPPED_VIEW_COUNT = {1, 4};
    private static final int MSG_MOVE_FOCUS = 1000;
    private static final String TAG = "OnRepeatedKeyListener";
    private static final int[] THRESHOLD_FAST_FOCUS_CHANGE_TIME_MS = {2000, 5000};
    /* access modifiers changed from: private */
    public int mDirection;
    private boolean mFocusAccelerated;
    private final MyHandler mHandler = new MyHandler();
    private long mRepeatedKeyInterval;
    /* access modifiers changed from: private */
    public final VerticalGridView mView;

    public OnRepeatedKeyInterceptListener(VerticalGridView view) {
        this.mView = view;
    }

    public boolean isFocusAccelerated() {
        return this.mFocusAccelerated;
    }

    public boolean onInterceptKeyEvent(KeyEvent event) {
        this.mHandler.removeMessages(1000);
        if (event.getKeyCode() != 19 && event.getKeyCode() != 20) {
            return false;
        }
        long duration = event.getEventTime() - event.getDownTime();
        if (duration < ((long) THRESHOLD_FAST_FOCUS_CHANGE_TIME_MS[0]) || event.isCanceled()) {
            this.mFocusAccelerated = false;
            return false;
        }
        this.mDirection = event.getKeyCode() == 19 ? 33 : 130;
        int skippedViewCount = MAX_SKIPPED_VIEW_COUNT[0];
        int i = 1;
        while (i < THRESHOLD_FAST_FOCUS_CHANGE_TIME_MS.length && ((long) THRESHOLD_FAST_FOCUS_CHANGE_TIME_MS[i]) < duration) {
            skippedViewCount = MAX_SKIPPED_VIEW_COUNT[i];
            i++;
        }
        if (event.getAction() == 0) {
            this.mRepeatedKeyInterval = duration / ((long) event.getRepeatCount());
            this.mFocusAccelerated = true;
        } else {
            this.mFocusAccelerated = false;
        }
        for (int i2 = 0; i2 < skippedViewCount; i2++) {
            this.mHandler.sendEmptyMessageDelayed(1000, (this.mRepeatedKeyInterval * ((long) i2)) / ((long) (skippedViewCount + 1)));
        }
        return false;
    }

    private static class MyHandler extends WeakHandler<OnRepeatedKeyInterceptListener> {
        private MyHandler(OnRepeatedKeyInterceptListener listener) {
            super(listener);
        }

        public void handleMessage(Message msg, @NonNull OnRepeatedKeyInterceptListener listener) {
            View focused;
            View v;
            if (msg.what == 1000 && (focused = listener.mView.findFocus()) != null && (v = focused.focusSearch(listener.mDirection)) != null && v != focused) {
                v.requestFocus(listener.mDirection);
            }
        }
    }
}
