package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class Loading extends TextView {
    private static final long delay = 1000;
    private static final long period = 500;
    /* access modifiers changed from: private */
    public int count = 0;
    private TimerTask task;
    private Timer timer;

    static /* synthetic */ int access$008(Loading x0) {
        int i = x0.count;
        x0.count = i + 1;
        return i;
    }

    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void drawLoading() {
        this.timer = new Timer("loading");
        this.task = new TimerTask() {
            public void run() {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < Loading.this.count % 4; i++) {
                    builder.append(" .");
                }
                final String temp = builder.toString();
                Loading.this.post(new Runnable() {
                    public void run() {
                        Loading.this.setText(temp);
                    }
                });
                Loading.access$008(Loading.this);
            }
        };
        this.timer.schedule(this.task, 1000, period);
    }

    public void stopDraw() {
        this.task.cancel();
        this.timer.cancel();
        setText("");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
