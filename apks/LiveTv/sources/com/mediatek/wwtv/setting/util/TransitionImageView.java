package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.wwtv.setting.widget.view.RefcountBitmapDrawable;

class TransitionImageView extends View {
    private float mAlphaDiff;
    private int mBgAlphaDiff;
    private int mBgBlueDiff;
    private int mBgGreenDiff;
    private boolean mBgHasDiff;
    private int mBgRedDiff;
    private BitmapDrawable mBitmapDrawable;
    private float mClipBottomDiff;
    private float mClipLeftDiff;
    private RectF mClipRect;
    private float mClipRightDiff;
    private float mClipTopDiff;
    private ColorMatrix mColorMatrix;
    private TransitionImage mDst;
    private Rect mDstRect;
    private RectF mExcludeRect;
    private float mProgress;
    private float mSaturationDiff;
    private float mScaleX;
    private float mScaleXDiff;
    private float mScaleY;
    private float mScaleYDiff;
    private TransitionImage mSrc;
    private int mSrcBgColor;
    private RectF mSrcClipRect;
    private Rect mSrcRect;
    private RectF mSrcUnclipRect;
    private float mTranslationXDiff;
    private float mTranslationYDiff;
    private float mUnclipCenterXDiff;
    private float mUnclipCenterYDiff;
    private float mUnclipHeightDiffBeforeScale;
    private Rect mUnclipRect;
    private float mUnclipWidthDiffBeforeScale;

    public TransitionImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TransitionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransitionImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSrcRect = new Rect();
        this.mSrcUnclipRect = new RectF();
        this.mSrcClipRect = new RectF();
        this.mDstRect = new Rect();
        this.mClipRect = new RectF();
        this.mUnclipRect = new Rect();
        this.mColorMatrix = new ColorMatrix();
        setPivotX(0.0f);
        setPivotY(0.0f);
        setWillNotDraw(false);
    }

    public void setSourceTransition(TransitionImage src) {
        this.mSrc = src;
        initializeView();
    }

    public void setDestTransition(TransitionImage dst) {
        this.mDst = dst;
        calculateDiffs();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.mBitmapDrawable instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) this.mBitmapDrawable).getRefcountObject().releaseRef();
        }
        super.onDetachedFromWindow();
    }

    private void initializeView() {
        this.mBitmapDrawable = this.mSrc.getBitmap();
        this.mBitmapDrawable.mutate();
        this.mSrc.getOptimizedRect(this.mSrcRect);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = this.mSrcRect.width();
        params.height = this.mSrcRect.height();
        this.mSrcClipRect.set(this.mSrc.getClippedRect());
        this.mSrcClipRect.offset((float) (-this.mSrcRect.left), (float) (-this.mSrcRect.top));
        this.mSrcUnclipRect.set(this.mSrc.getUnclippedRect());
        this.mSrcUnclipRect.offset((float) (-this.mSrcRect.left), (float) (-this.mSrcRect.top));
        if (this.mSrc.getAlpha() != 1.0f) {
            this.mBitmapDrawable.setAlpha((int) (this.mSrc.getAlpha() * 255.0f));
        }
        if (this.mSrc.getSaturation() != 1.0f) {
            this.mColorMatrix.setSaturation(this.mSrc.getSaturation());
            this.mBitmapDrawable.setColorFilter(new ColorMatrixColorFilter(this.mColorMatrix));
        }
        this.mSrcBgColor = this.mSrc.getBackground();
        if (this.mSrcBgColor != 0) {
            setBackgroundColor(this.mSrcBgColor);
            getBackground().setAlpha((int) (this.mSrc.getAlpha() * 255.0f));
        }
        invalidate();
    }

    private void calculateDiffs() {
        this.mDst.getOptimizedRect(this.mDstRect);
        this.mScaleX = ((float) this.mDstRect.width()) / ((float) this.mSrcRect.width());
        this.mScaleY = ((float) this.mDstRect.height()) / ((float) this.mSrcRect.height());
        this.mScaleXDiff = this.mScaleX - 1.0f;
        this.mScaleYDiff = this.mScaleY - 1.0f;
        this.mTranslationXDiff = (float) (this.mDstRect.left - this.mSrcRect.left);
        this.mTranslationYDiff = (float) (this.mDstRect.top - this.mSrcRect.top);
        RectF dstClipRect = new RectF();
        dstClipRect.set(this.mDst.getClippedRect());
        dstClipRect.offset((float) (-this.mDstRect.left), (float) (-this.mDstRect.top));
        dstClipRect.left /= this.mScaleX;
        dstClipRect.right /= this.mScaleX;
        dstClipRect.top /= this.mScaleY;
        dstClipRect.bottom /= this.mScaleY;
        this.mClipLeftDiff = dstClipRect.left - this.mSrcClipRect.left;
        this.mClipRightDiff = dstClipRect.right - this.mSrcClipRect.right;
        this.mClipTopDiff = dstClipRect.top - this.mSrcClipRect.top;
        this.mClipBottomDiff = dstClipRect.bottom - this.mSrcClipRect.bottom;
        RectF dstUnclipRect = new RectF();
        dstUnclipRect.set(this.mDst.getUnclippedRect());
        dstUnclipRect.offset((float) (-this.mDstRect.left), (float) (-this.mDstRect.top));
        this.mUnclipWidthDiffBeforeScale = dstUnclipRect.width() - this.mSrcUnclipRect.width();
        this.mUnclipHeightDiffBeforeScale = dstUnclipRect.height() - this.mSrcUnclipRect.height();
        dstUnclipRect.left /= this.mScaleX;
        dstUnclipRect.right /= this.mScaleX;
        dstUnclipRect.top /= this.mScaleY;
        dstUnclipRect.bottom /= this.mScaleY;
        this.mUnclipCenterXDiff = dstUnclipRect.centerX() - this.mSrcUnclipRect.centerX();
        this.mUnclipCenterYDiff = dstUnclipRect.centerY() - this.mSrcUnclipRect.centerY();
        this.mAlphaDiff = this.mDst.getAlpha() - this.mSrc.getAlpha();
        int srcColor = this.mSrc.getBackground();
        int dstColor = this.mDst.getBackground();
        this.mBgAlphaDiff = Color.alpha(dstColor) - Color.alpha(srcColor);
        this.mBgRedDiff = Color.red(dstColor) - Color.red(srcColor);
        this.mBgGreenDiff = Color.green(dstColor) - Color.green(srcColor);
        this.mBgBlueDiff = Color.blue(dstColor) - Color.blue(srcColor);
        this.mSaturationDiff = this.mDst.getSaturation() - this.mSrc.getSaturation();
        this.mBgHasDiff = (this.mBgAlphaDiff == 0 && this.mBgRedDiff == 0 && this.mBgGreenDiff == 0 && this.mBgBlueDiff == 0) ? false : true;
    }

    public TransitionImage getSourceTransition() {
        return this.mSrc;
    }

    public TransitionImage getDestTransition() {
        return this.mDst;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        setScaleX((this.mScaleXDiff * this.mProgress) + 1.0f);
        setScaleY(1.0f + (this.mScaleYDiff * this.mProgress));
        setTranslationX(((float) this.mSrcRect.left) + (this.mProgress * this.mTranslationXDiff));
        setTranslationY(((float) this.mSrcRect.top) + (this.mProgress * this.mTranslationYDiff));
        float unclipCenterX = this.mSrcUnclipRect.centerX() + (this.mUnclipCenterXDiff * this.mProgress);
        float unclipCenterY = this.mSrcUnclipRect.centerY() + (this.mUnclipCenterYDiff * this.mProgress);
        float unclipWidthBeforeScale = this.mSrcUnclipRect.width() + (this.mUnclipWidthDiffBeforeScale * this.mProgress);
        float unclipHeightBeforeScale = this.mSrcUnclipRect.height() + (this.mUnclipHeightDiffBeforeScale * this.mProgress);
        float unclipWidth = unclipWidthBeforeScale / getScaleX();
        float unclipHeight = unclipHeightBeforeScale / getScaleY();
        this.mUnclipRect.left = (int) (unclipCenterX - (unclipWidth * 0.5f));
        this.mUnclipRect.top = (int) (unclipCenterY - (unclipHeight * 0.5f));
        this.mUnclipRect.right = (int) ((unclipWidth * 0.5f) + unclipCenterX);
        this.mUnclipRect.bottom = (int) ((0.5f * unclipHeight) + unclipCenterY);
        this.mBitmapDrawable.setBounds(this.mUnclipRect);
        this.mClipRect.left = this.mSrcClipRect.left + (this.mClipLeftDiff * this.mProgress);
        this.mClipRect.top = this.mSrcClipRect.top + (this.mClipTopDiff * this.mProgress);
        this.mClipRect.right = this.mSrcClipRect.right + (this.mClipRightDiff * this.mProgress);
        this.mClipRect.bottom = this.mSrcClipRect.bottom + (this.mClipBottomDiff * this.mProgress);
        if (this.mAlphaDiff != 0.0f) {
            int alpha = (int) ((this.mSrc.getAlpha() + (this.mAlphaDiff * this.mProgress)) * 255.0f);
            this.mBitmapDrawable.setAlpha(alpha);
            if (getBackground() != null) {
                getBackground().setAlpha(alpha);
            }
        }
        if (this.mSaturationDiff != 0.0f) {
            this.mColorMatrix.setSaturation(this.mSrc.getSaturation() + (this.mSaturationDiff * this.mProgress));
            this.mBitmapDrawable.setColorFilter(new ColorMatrixColorFilter(this.mColorMatrix));
        }
        if (this.mBgHasDiff) {
            setBackgroundColor(Color.argb(Color.alpha(this.mSrcBgColor) + ((int) (((float) this.mBgAlphaDiff) * this.mProgress)), Color.red(this.mSrcBgColor) + ((int) (((float) this.mBgRedDiff) * this.mProgress)), Color.green(this.mSrcBgColor) + ((int) (((float) this.mBgGreenDiff) * this.mProgress)), Color.blue(this.mSrcBgColor) + ((int) (((float) this.mBgBlueDiff) * this.mProgress))));
        }
        invalidate();
    }

    public float getProgress() {
        return this.mProgress;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mBitmapDrawable != null) {
            int count = canvas.save();
            canvas.clipRect(this.mClipRect);
            if (this.mExcludeRect != null) {
                canvas.clipRect(this.mExcludeRect, Region.Op.DIFFERENCE);
            }
            this.mBitmapDrawable.draw(canvas);
            canvas.restoreToCount(count);
        }
    }

    public void setExcludeClipRect(RectF rect) {
        if (this.mExcludeRect == null) {
            this.mExcludeRect = new RectF();
        }
        this.mExcludeRect.set(rect);
        this.mExcludeRect.offset(-getX(), -getY());
        this.mExcludeRect.left /= (this.mScaleXDiff * this.mProgress) + 1.0f;
        this.mExcludeRect.right /= (this.mScaleXDiff * this.mProgress) + 1.0f;
        this.mExcludeRect.top /= (this.mScaleYDiff * this.mProgress) + 1.0f;
        this.mExcludeRect.bottom /= 1.0f + (this.mScaleYDiff * this.mProgress);
    }

    public void clearExcludeClipRect() {
        this.mExcludeRect = null;
    }
}
