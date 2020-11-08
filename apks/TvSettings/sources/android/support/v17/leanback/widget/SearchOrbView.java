package android.support.v17.leanback.widget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SearchOrbView extends FrameLayout implements View.OnClickListener {
    private boolean mAttachedToWindow;
    private boolean mColorAnimationEnabled;
    private ValueAnimator mColorAnimator;
    private final ArgbEvaluator mColorEvaluator;
    private Colors mColors;
    private final ValueAnimator.AnimatorUpdateListener mFocusUpdateListener;
    private final float mFocusedZ;
    private final float mFocusedZoom;
    private ImageView mIcon;
    private Drawable mIconDrawable;
    private View.OnClickListener mListener;
    private final int mPulseDurationMs;
    private View mRootView;
    private final int mScaleDurationMs;
    private View mSearchOrbView;
    private ValueAnimator mShadowFocusAnimator;
    private final float mUnfocusedZ;
    private final ValueAnimator.AnimatorUpdateListener mUpdateListener;

    public static class Colors {
        private static final float BRIGHTNESS_ALPHA = 0.15f;
        @ColorInt
        public int brightColor;
        @ColorInt
        public int color;
        @ColorInt
        public int iconColor;

        public Colors(@ColorInt int color2) {
            this(color2, color2);
        }

        public Colors(@ColorInt int color2, @ColorInt int brightColor2) {
            this(color2, brightColor2, 0);
        }

        public Colors(@ColorInt int color2, @ColorInt int brightColor2, @ColorInt int iconColor2) {
            this.color = color2;
            this.brightColor = brightColor2 == color2 ? getBrightColor(color2) : brightColor2;
            this.iconColor = iconColor2;
        }

        public static int getBrightColor(int color2) {
            return Color.argb((int) ((((float) Color.alpha(color2)) * 0.85f) + 38.25f), (int) ((((float) Color.red(color2)) * 0.85f) + 38.25f), (int) ((((float) Color.green(color2)) * 0.85f) + 38.25f), (int) ((((float) Color.blue(color2)) * 0.85f) + 38.25f));
        }
    }

    /* access modifiers changed from: package-private */
    public void setSearchOrbZ(float fraction) {
        ViewCompat.setZ(this.mSearchOrbView, this.mUnfocusedZ + ((this.mFocusedZ - this.mUnfocusedZ) * fraction));
    }

    public SearchOrbView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SearchOrbView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.searchOrbViewStyle);
    }

    public SearchOrbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mColorEvaluator = new ArgbEvaluator();
        this.mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animator) {
                SearchOrbView.this.setOrbViewColor(((Integer) animator.getAnimatedValue()).intValue());
            }
        };
        this.mFocusUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SearchOrbView.this.setSearchOrbZ(animation.getAnimatedFraction());
            }
        };
        Resources res = context.getResources();
        this.mRootView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(getLayoutResourceId(), this, true);
        this.mSearchOrbView = this.mRootView.findViewById(R.id.search_orb);
        this.mIcon = (ImageView) this.mRootView.findViewById(R.id.icon);
        this.mFocusedZoom = context.getResources().getFraction(R.fraction.lb_search_orb_focused_zoom, 1, 1);
        this.mPulseDurationMs = context.getResources().getInteger(R.integer.lb_search_orb_pulse_duration_ms);
        this.mScaleDurationMs = context.getResources().getInteger(R.integer.lb_search_orb_scale_duration_ms);
        this.mFocusedZ = (float) context.getResources().getDimensionPixelSize(R.dimen.lb_search_orb_focused_z);
        this.mUnfocusedZ = (float) context.getResources().getDimensionPixelSize(R.dimen.lb_search_orb_unfocused_z);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbSearchOrbView, defStyleAttr, 0);
        Drawable img = a.getDrawable(R.styleable.lbSearchOrbView_searchOrbIcon);
        setOrbIcon(img == null ? res.getDrawable(R.drawable.lb_ic_in_app_search) : img);
        int color = a.getColor(R.styleable.lbSearchOrbView_searchOrbColor, res.getColor(R.color.lb_default_search_color));
        setOrbColors(new Colors(color, a.getColor(R.styleable.lbSearchOrbView_searchOrbBrightColor, color), a.getColor(R.styleable.lbSearchOrbView_searchOrbIconColor, 0)));
        a.recycle();
        setFocusable(true);
        setClipChildren(false);
        setOnClickListener(this);
        setSoundEffectsEnabled(false);
        setSearchOrbZ(0.0f);
        ViewCompat.setZ(this.mIcon, this.mFocusedZ);
    }

    /* access modifiers changed from: package-private */
    public int getLayoutResourceId() {
        return R.layout.lb_search_orb;
    }

    /* access modifiers changed from: package-private */
    public void scaleOrbViewOnly(float scale) {
        this.mSearchOrbView.setScaleX(scale);
        this.mSearchOrbView.setScaleY(scale);
    }

    /* access modifiers changed from: package-private */
    public float getFocusedZoom() {
        return this.mFocusedZoom;
    }

    public void onClick(View view) {
        if (this.mListener != null) {
            this.mListener.onClick(view);
        }
    }

    private void startShadowFocusAnimation(boolean gainFocus, int duration) {
        if (this.mShadowFocusAnimator == null) {
            this.mShadowFocusAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mShadowFocusAnimator.addUpdateListener(this.mFocusUpdateListener);
        }
        if (gainFocus) {
            this.mShadowFocusAnimator.start();
        } else {
            this.mShadowFocusAnimator.reverse();
        }
        this.mShadowFocusAnimator.setDuration((long) duration);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        animateOnFocus(gainFocus);
    }

    /* access modifiers changed from: package-private */
    public void animateOnFocus(boolean hasFocus) {
        float zoom = hasFocus ? this.mFocusedZoom : 1.0f;
        this.mRootView.animate().scaleX(zoom).scaleY(zoom).setDuration((long) this.mScaleDurationMs).start();
        startShadowFocusAnimation(hasFocus, this.mScaleDurationMs);
        enableOrbColorAnimation(hasFocus);
    }

    public void setOrbIcon(Drawable icon) {
        this.mIconDrawable = icon;
        this.mIcon.setImageDrawable(this.mIconDrawable);
    }

    public Drawable getOrbIcon() {
        return this.mIconDrawable;
    }

    public void setOnOrbClickedListener(View.OnClickListener listener) {
        this.mListener = listener;
    }

    public void setOrbColor(int color) {
        setOrbColors(new Colors(color, color, 0));
    }

    @Deprecated
    public void setOrbColor(@ColorInt int color, @ColorInt int brightColor) {
        setOrbColors(new Colors(color, brightColor, 0));
    }

    @ColorInt
    public int getOrbColor() {
        return this.mColors.color;
    }

    public void setOrbColors(Colors colors) {
        this.mColors = colors;
        this.mIcon.setColorFilter(this.mColors.iconColor);
        if (this.mColorAnimator == null) {
            setOrbViewColor(this.mColors.color);
        } else {
            enableOrbColorAnimation(true);
        }
    }

    public Colors getOrbColors() {
        return this.mColors;
    }

    public void enableOrbColorAnimation(boolean enable) {
        this.mColorAnimationEnabled = enable;
        updateColorAnimator();
    }

    private void updateColorAnimator() {
        if (this.mColorAnimator != null) {
            this.mColorAnimator.end();
            this.mColorAnimator = null;
        }
        if (this.mColorAnimationEnabled && this.mAttachedToWindow) {
            this.mColorAnimator = ValueAnimator.ofObject(this.mColorEvaluator, new Object[]{Integer.valueOf(this.mColors.color), Integer.valueOf(this.mColors.brightColor), Integer.valueOf(this.mColors.color)});
            this.mColorAnimator.setRepeatCount(-1);
            this.mColorAnimator.setDuration((long) (this.mPulseDurationMs * 2));
            this.mColorAnimator.addUpdateListener(this.mUpdateListener);
            this.mColorAnimator.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void setOrbViewColor(int color) {
        if (this.mSearchOrbView.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) this.mSearchOrbView.getBackground()).setColor(color);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        updateColorAnimator();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        updateColorAnimator();
        super.onDetachedFromWindow();
    }
}