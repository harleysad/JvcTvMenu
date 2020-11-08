package com.mediatek.wwtv.tvcenter.capturelogo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class CaptureSelectArea extends View implements View.OnKeyListener {
    private static final String TAG = "CaptureSelectArea";
    private static final int minSize = 50;
    private static final int moveStep = 5;
    private boolean adjustSizeMode;
    private Rect area;
    private CaptureLogoActivity capActivity;
    private Handler mHandler;
    private int mWindowHeight;
    private int mWindowWidth;
    private int rHeight;
    private int rWidth;

    public CaptureSelectArea(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.rWidth = (int) (0.176d * ((double) ScreenConstant.SCREEN_WIDTH));
        this.rHeight = (int) (0.18d * ((double) ScreenConstant.SCREEN_HEIGHT));
        this.adjustSizeMode = false;
        this.area = new Rect(0, 0, this.rWidth, this.rHeight);
        MtkLog.e("tag", "rWidth:    " + this.rWidth + "  rHeight:" + this.rHeight);
        context.getSystemService("window");
        this.mWindowWidth = ScreenConstant.SCREEN_WIDTH;
        this.mWindowHeight = ScreenConstant.SCREEN_HEIGHT;
        setOnKeyListener(this);
    }

    public CaptureSelectArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.rWidth = (int) (0.176d * ((double) ScreenConstant.SCREEN_WIDTH));
        this.rHeight = (int) (0.18d * ((double) ScreenConstant.SCREEN_HEIGHT));
        this.adjustSizeMode = false;
        this.area = new Rect(0, 0, this.rWidth, this.rHeight);
        this.mWindowWidth = ScreenConstant.SCREEN_WIDTH;
        this.mWindowHeight = ScreenConstant.SCREEN_HEIGHT;
        setOnKeyListener(this);
    }

    public CaptureSelectArea(Context context) {
        super(context);
        this.rWidth = (int) (0.176d * ((double) ScreenConstant.SCREEN_WIDTH));
        this.rHeight = (int) (0.18d * ((double) ScreenConstant.SCREEN_HEIGHT));
        this.adjustSizeMode = false;
        this.area = new Rect(0, 0, this.rWidth, this.rHeight);
        this.mWindowWidth = ScreenConstant.SCREEN_WIDTH;
        this.mWindowHeight = ScreenConstant.SCREEN_HEIGHT;
        setOnKeyListener(this);
        this.capActivity = (CaptureLogoActivity) context;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(-1593868224);
        canvas.drawRect(this.area, paint);
    }

    public Rect getCaptureArea() {
        return this.area;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis4);
        if (event.getAction() == 0) {
            switch (keyCode) {
                case 19:
                    if (this.area.top < 0) {
                        this.area.top = 0;
                        break;
                    } else {
                        if (this.adjustSizeMode) {
                            if (this.area.height() > 50) {
                                Rect rect = this.area;
                                rect.bottom -= 5;
                                this.rHeight = this.area.height();
                            }
                        } else if (this.area.top > 0) {
                            Rect rect2 = this.area;
                            rect2.top -= 5;
                            this.area.bottom = this.area.top + this.rHeight;
                        }
                        invalidate();
                        break;
                    }
                case 20:
                    if (this.area.bottom >= this.mWindowHeight) {
                        this.area.bottom = this.mWindowHeight;
                        break;
                    } else {
                        if (this.adjustSizeMode) {
                            this.area.bottom += 5;
                            this.rHeight = this.area.height();
                        } else {
                            this.area.top += 5;
                            this.area.bottom = this.area.top + this.rHeight;
                        }
                        invalidate();
                        break;
                    }
                case 21:
                    if (this.area.left < 0) {
                        this.area.left = 0;
                        break;
                    } else {
                        if (this.adjustSizeMode) {
                            if (this.area.width() > 50) {
                                Rect rect3 = this.area;
                                rect3.right -= 5;
                                this.rWidth = this.area.width();
                            }
                        } else if (this.area.left > 0) {
                            Rect rect4 = this.area;
                            rect4.left -= 5;
                            this.area.right = this.area.left + this.rWidth;
                        }
                        invalidate();
                        break;
                    }
                case 22:
                    if (this.area.right >= this.mWindowWidth) {
                        this.area.right = this.mWindowWidth;
                        break;
                    } else {
                        if (this.adjustSizeMode) {
                            this.area.right += 5;
                            this.rWidth = this.area.width();
                        } else {
                            this.area.left += 5;
                            this.area.right = this.area.left + this.rWidth;
                        }
                        invalidate();
                        break;
                    }
                case 23:
                    this.adjustSizeMode = !this.adjustSizeMode;
                    MtkLog.d(TAG, "ajust size mode: " + this.adjustSizeMode);
                    if (!this.adjustSizeMode) {
                        setVisibility(8);
                        invalidate();
                        this.capActivity.createDialog(6);
                        CaptureLogoActivity.captureMain.setVisibility(0);
                        break;
                    }
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}
