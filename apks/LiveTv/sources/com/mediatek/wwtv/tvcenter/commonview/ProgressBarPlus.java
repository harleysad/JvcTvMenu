package com.mediatek.wwtv.tvcenter.commonview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class ProgressBarPlus extends ProgressBar {
    private String NAMESPACE = "http://schemas.android.com/apk/res/android";
    private Paint mPaint;
    private Rect rect = new Rect();
    private String text;

    public ProgressBarPlus(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public ProgressBarPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public ProgressBarPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public synchronized void setProgress(int progress) {
        if (progress <= getMax()) {
            setText(progress);
            super.setProgress(progress);
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), this.rect);
        canvas.drawText(this.text, (float) ((getWidth() / 2) - this.rect.centerX()), (float) ((getHeight() / 2) - this.rect.centerY()), this.mPaint);
    }

    private void init(AttributeSet attrs) {
        this.mPaint = new Paint();
        this.mPaint.setColor(-1);
        if (attrs != null) {
            int rId = attrs.getAttributeResourceValue(this.NAMESPACE, "textSize", -1);
            this.mPaint.setTextSize(rId != -1 ? getResources().getDimension(rId) : 210.0f);
        }
    }

    private void setText(int progress) {
        int i = (progress * 100) / getMax();
        this.text = String.valueOf(i) + "%";
    }
}
