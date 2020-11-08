package com.mediatek.wwtv.tvcenter.commonview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class Loading extends TextView {
    private static final int REFRESH = 0;
    private static String TAG = "DvrFilelist";
    private static final long delay = 500;
    private static final long period = 500;
    private int count = 0;
    private boolean isLoading = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Loading.this.startLoading();
            }
            super.handleMessage(msg);
        }
    };

    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void drawLoading() {
        this.isLoading = true;
        MtkLog.d(TAG, "drawLoading()");
        this.count = 0;
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessageDelayed(0, 500);
    }

    /* access modifiers changed from: private */
    public void startLoading() {
        if (getVisibility() != 0) {
            MtkLog.d(TAG, "drawLoading() setVisibility");
            setVisibility(0);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.count % 4; i++) {
            builder.append(" .");
        }
        final String temp = builder.toString();
        post(new Runnable() {
            public void run() {
                Loading.this.setText(temp);
            }
        });
        this.count++;
        this.mHandler.sendEmptyMessageDelayed(0, 500);
    }

    public void stopDraw() {
        this.isLoading = false;
        MtkLog.d(TAG, "stopDraw()");
        this.mHandler.removeMessages(0);
        this.count = 0;
        setText("");
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
