package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mediatek.wwtv.tvcenter.R;

public class RefcountImageView extends ImageView {
    private boolean mAutoUnrefOnDetach;
    private RectF mClipRect;
    private boolean mHasClipRect;

    public RefcountImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RefcountImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefcountImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mClipRect = new RectF();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefcountImageView);
        this.mAutoUnrefOnDetach = a.getBoolean(0, true);
        a.recycle();
    }

    public void setAutoUnrefOnDetach(boolean autoUnref) {
        this.mAutoUnrefOnDetach = autoUnref;
    }

    public boolean getAutoUnrefOnDetach() {
        return this.mAutoUnrefOnDetach;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.mAutoUnrefOnDetach) {
            setImageDrawable((Drawable) null);
        }
        super.onDetachedFromWindow();
    }

    public void setImageDrawable(Drawable drawable) {
        Drawable previousDrawable = getDrawable();
        super.setImageDrawable(drawable);
        releaseRef(previousDrawable);
    }

    private static void releaseRef(Drawable drawable) {
        if (drawable instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) drawable).getRefcountObject().releaseRef();
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int z = layerDrawable.getNumberOfLayers();
            for (int i = 0; i < z; i++) {
                releaseRef(layerDrawable.getDrawable(i));
            }
        }
    }

    public void setClipRect(float l, float t, float r, float b) {
        this.mClipRect.set(l, t, r, b);
        this.mHasClipRect = true;
    }

    public void clearClipRect() {
        this.mHasClipRect = false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mHasClipRect) {
            int saveCount = canvas.save();
            canvas.clipRect(this.mClipRect);
            super.onDraw(canvas);
            canvas.restoreToCount(saveCount);
            return;
        }
        super.onDraw(canvas);
    }
}
