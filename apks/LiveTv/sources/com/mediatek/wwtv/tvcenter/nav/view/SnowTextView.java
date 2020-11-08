package com.mediatek.wwtv.tvcenter.nav.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class SnowTextView extends TextView {
    public static final boolean ANIMATOR = false;
    public static final int DRAW_TEXT = 1;
    private static final String TAG = "SnowTextView";
    private Bitmap bitmap = null;
    private ObjectAnimator invisToInvis;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                SnowTextView.this.invalidate();
            }
        }
    };
    private Paint mPaint = new Paint();
    private ObjectAnimator translation;
    private ObjectAnimator translationY;
    private ObjectAnimator visToInvis;

    public SnowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.bitmap != null) {
            MtkLog.d(TAG, "onDraw>>>" + canvas + "   " + this.bitmap);
            canvas.drawBitmap(this.bitmap, new Rect(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), this.mPaint);
        }
    }

    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
        invalidate();
    }

    public void setVisibility(int visibility) {
        MtkLog.d(TAG, "setVisibility>>>" + visibility);
        super.setVisibility(visibility);
    }

    private void clearAnimations() {
        setAlpha(1.0f);
        setRotationX(0.0f);
        setRotationY(0.0f);
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        setScaleX(1.0f);
        setScaleY(1.0f);
    }

    private void setAnimator() {
        Interpolator accelerator = new LinearInterpolator();
        this.translation = ObjectAnimator.ofFloat(this, "translationX", new float[]{((float) (-ScreenConstant.SCREEN_WIDTH)) / 3.0f, ((float) ScreenConstant.SCREEN_WIDTH) / 3.0f});
        this.translationY = ObjectAnimator.ofFloat(this, "translationY", new float[]{((float) (-ScreenConstant.SCREEN_HEIGHT)) / 3.0f, ((float) ScreenConstant.SCREEN_HEIGHT) / 3.0f});
        this.visToInvis = ObjectAnimator.ofFloat(this, "rotationY", new float[]{-40.0f, 40.0f});
        this.invisToInvis = ObjectAnimator.ofFloat(this, "rotationX", new float[]{-50.0f, 50.0f});
        this.translation.setDuration(MessageType.delayForTKToMenu);
        this.translation.setRepeatMode(2);
        this.translation.setRepeatCount(-1);
        this.translationY.setDuration(4500);
        this.translationY.setRepeatMode(2);
        this.translationY.setRepeatCount(-1);
        this.visToInvis.setRepeatMode(2);
        this.visToInvis.setRepeatCount(-1);
        setScaleX(0.8f);
        this.invisToInvis.setRepeatMode(2);
        this.invisToInvis.setRepeatCount(-1);
        this.invisToInvis.setDuration(7000);
        this.visToInvis.setDuration(MessageType.delayMillis4);
        this.invisToInvis.setInterpolator(accelerator);
        this.visToInvis.setRepeatCount(-1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[]{this.invisToInvis, this.visToInvis, this.translation, this.translationY});
        set.start();
    }
}
