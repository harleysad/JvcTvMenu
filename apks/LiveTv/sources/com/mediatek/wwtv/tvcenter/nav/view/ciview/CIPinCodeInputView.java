package com.mediatek.wwtv.tvcenter.nav.view.ciview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class CIPinCodeInputView extends View {
    final int PADDING = 2;
    final int XOFFUNIT = 8;
    boolean flag = true;
    Context mContext;
    private int mCurrentSelectedPosition = -1;
    private float mHeight;
    private Paint mPaint;
    private String mText;
    private int mTextSize = 30;
    private float mWidth;
    private float x;
    private float y;

    public CIPinCodeInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void setText(String text) {
        this.mText = text;
        postInvalidate();
    }

    public String getText() {
        return this.mText;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        String textStr = this.mText;
        MtkLog.d("CIDIALOGEDIT", "onDraw===" + this.mCurrentSelectedPosition + "," + textStr);
        if (!TextUtils.isEmpty(textStr)) {
            this.mPaint = new Paint();
            this.mWidth = (float) getWidth();
            this.mHeight = (float) getHeight();
            this.x = (this.mWidth - this.mPaint.measureText(textStr)) / 2.0f;
            this.y = (this.mHeight + (this.mPaint.measureText(textStr, 0, 1) * 1.2f)) / 2.0f;
            this.mPaint.setTextSize((float) this.mTextSize);
            this.mPaint.setFlags(1);
            this.mPaint.setAntiAlias(true);
            Paint.FontMetrics fm = new Paint.FontMetrics();
            this.mPaint.getFontMetrics(fm);
            this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.mPaint.setStyle(Paint.Style.FILL);
            if (textStr.length() == 4) {
                canvas2.drawText(textStr, this.x - 24.0f, this.y, this.mPaint);
            } else {
                canvas2.drawText(textStr, this.x - ((float) (this.mCurrentSelectedPosition * 8)), this.y, this.mPaint);
            }
            if (this.mCurrentSelectedPosition != -1) {
                this.mPaint.setColor(InputDeviceCompat.SOURCE_ANY);
                if (this.mCurrentSelectedPosition == 0) {
                    if (textStr.length() == 4) {
                        this.x -= 24.0f;
                    }
                    MtkLog.d("CIDIALOGEDIT", "pos0--x===" + this.x + ",y==" + this.y);
                    String selStr = textStr.substring(this.mCurrentSelectedPosition, 1);
                    canvas2.drawRect(this.x, (fm.top + this.y) - 2.0f, this.x + this.mPaint.measureText(selStr), fm.bottom + this.y + 2.0f, this.mPaint);
                    this.mPaint.setColor(-7829368);
                    canvas2.drawText(selStr, (float) ((int) this.x), (float) ((int) this.y), this.mPaint);
                    return;
                }
                float offset = this.mPaint.measureText(textStr.substring(0, 1));
                this.x += this.mPaint.measureText(textStr.substring(0, this.mCurrentSelectedPosition));
                if (textStr.length() == 4) {
                    this.x -= 24.0f;
                } else {
                    this.x -= (float) (8 * this.mCurrentSelectedPosition);
                }
                MtkLog.d("CIDIALOGEDIT", "x===" + this.x + ",y==" + this.y);
                canvas2.drawRect(this.x, (fm.top + this.y) - 2.0f, this.x + offset, fm.bottom + this.y + 2.0f, this.mPaint);
                this.mPaint.setColor(-7829368);
                canvas2.drawText(textStr.substring(this.mCurrentSelectedPosition, this.mCurrentSelectedPosition + 1), (float) ((int) this.x), (float) ((int) this.y), this.mPaint);
            }
        }
    }

    public void setCursorPos(int keyCode) {
        if (!TextUtils.isEmpty(getText())) {
            if (keyCode == 21) {
                MtkLog.d("CIDIALOGEDIT", "left cursor index:" + this.mCurrentSelectedPosition);
                if (this.mCurrentSelectedPosition != 0) {
                    this.mCurrentSelectedPosition--;
                }
                postInvalidate();
                return;
            }
            MtkLog.d("CIDIALOGEDIT", "right cursor index:" + this.mCurrentSelectedPosition);
            if (this.mCurrentSelectedPosition != getText().length() - 1) {
                this.mCurrentSelectedPosition++;
            }
            postInvalidate();
        }
    }

    public void setCurrentSelectedPosition(int pos) {
        this.mCurrentSelectedPosition = pos;
    }

    public int getCurrentSelectedPosition() {
        return this.mCurrentSelectedPosition;
    }
}
