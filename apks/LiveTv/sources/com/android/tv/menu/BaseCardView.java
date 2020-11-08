package com.android.tv.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.tv.menu.ItemListRowView;
import com.mediatek.wwtv.tvcenter.R;

public abstract class BaseCardView<T> extends LinearLayout implements ItemListRowView.CardView<T> {
    private static final float SCALE_FACTOR_0F = 0.0f;
    private static final float SCALE_FACTOR_1F = 1.0f;
    /* access modifiers changed from: private */
    public final float mCardCornerRadius;
    private final float mCardHeight;
    private final int mCardImageWidth;
    private boolean mExtendViewOnFocus;
    private final float mExtendedCardHeight;
    private final float mExtendedTextViewHeight;
    private final int mFocusAnimDuration;
    private float mFocusAnimatedValue;
    private ValueAnimator mFocusAnimator;
    private final float mFocusTranslationZ;
    private boolean mSelected;
    private boolean mTextChanged;
    private int mTextResId;
    private String mTextString;
    @Nullable
    private TextView mTextView;
    @Nullable
    private TextView mTextViewFocused;
    private final float mTextViewHeight;
    private final float mVerticalCardMargin;

    public BaseCardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BaseCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClipToOutline(true);
        this.mFocusAnimDuration = getResources().getInteger(R.integer.menu_focus_anim_duration);
        this.mFocusTranslationZ = getResources().getDimension(R.dimen.channel_card_elevation_focused) - getResources().getDimension(R.dimen.card_elevation_normal);
        this.mVerticalCardMargin = (float) (2 * (getResources().getDimensionPixelOffset(R.dimen.menu_list_padding_top) + getResources().getDimensionPixelOffset(R.dimen.menu_list_margin_top)));
        setElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        this.mCardCornerRadius = (float) getResources().getDimensionPixelSize(R.dimen.channel_card_round_radius);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), BaseCardView.this.mCardCornerRadius);
            }
        });
        this.mCardImageWidth = getResources().getDimensionPixelSize(R.dimen.card_image_layout_width);
        this.mCardHeight = (float) getResources().getDimensionPixelSize(R.dimen.card_layout_height);
        this.mExtendedCardHeight = (float) getResources().getDimensionPixelSize(R.dimen.card_layout_height_extended);
        this.mTextViewHeight = (float) getResources().getDimensionPixelSize(R.dimen.card_meta_layout_height);
        this.mExtendedTextViewHeight = (float) getResources().getDimensionPixelOffset(R.dimen.card_meta_layout_height_extended);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTextView = (TextView) findViewById(R.id.card_text);
        this.mTextViewFocused = (TextView) findViewById(R.id.card_text_focused);
    }

    public void onBind(T t, boolean selected) {
        setFocusAnimatedValue(selected ? 1.0f : 0.0f);
    }

    public void onRecycled() {
    }

    public void onSelected() {
        this.mSelected = true;
        if (!isAttachedToWindow() || getVisibility() != 0) {
            cancelFocusAnimationIfAny();
            setFocusAnimatedValue(1.0f);
            return;
        }
        startFocusAnimation(1.0f);
    }

    public void onDeselected() {
        this.mSelected = false;
        if (!isAttachedToWindow() || getVisibility() != 0) {
            cancelFocusAnimationIfAny();
            setFocusAnimatedValue(0.0f);
            return;
        }
        startFocusAnimation(0.0f);
    }

    public void setText(int resId) {
        if (this.mTextResId != resId) {
            this.mTextResId = resId;
            this.mTextString = null;
            this.mTextChanged = true;
            if (this.mTextViewFocused != null) {
                this.mTextViewFocused.setText(resId);
            }
            if (this.mTextView != null) {
                this.mTextView.setText(resId);
            }
            onTextViewUpdated();
        }
    }

    public void setText(String text) {
        if (!TextUtils.equals(text, this.mTextString)) {
            this.mTextString = text;
            this.mTextResId = 0;
            this.mTextChanged = true;
            if (this.mTextViewFocused != null) {
                this.mTextViewFocused.setText(text);
            }
            if (this.mTextView != null) {
                this.mTextView.setText(text);
            }
            onTextViewUpdated();
        }
    }

    private void onTextViewUpdated() {
        float f = 0.0f;
        if (!(this.mTextView == null || this.mTextViewFocused == null)) {
            this.mTextViewFocused.measure(View.MeasureSpec.makeMeasureSpec(this.mCardImageWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
            boolean z = true;
            if (this.mTextViewFocused.getLineCount() <= 1) {
                z = false;
            }
            this.mExtendViewOnFocus = z;
            if (this.mExtendViewOnFocus) {
                setTextViewFocusedAlpha(this.mSelected ? 1.0f : 0.0f);
            } else {
                setTextViewFocusedAlpha(1.0f);
            }
        }
        if (this.mSelected) {
            f = 1.0f;
        }
        setFocusAnimatedValue(f);
    }

    public void setTextViewEnabled(boolean enabled) {
        if (this.mTextViewFocused != null) {
            this.mTextViewFocused.setEnabled(enabled);
        }
        if (this.mTextView != null) {
            this.mTextView.setEnabled(enabled);
        }
    }

    /* access modifiers changed from: protected */
    public void onFocusAnimationStart(boolean selected) {
        if (this.mExtendViewOnFocus) {
            setTextViewFocusedAlpha(selected ? 1.0f : 0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onFocusAnimationEnd(boolean selected) {
    }

    /* access modifiers changed from: protected */
    public void onSetFocusAnimatedValue(float animatedValue) {
        int height;
        float scale = 1.0f + ((this.mVerticalCardMargin / ((!this.mExtendViewOnFocus || !isFocused()) ? this.mCardHeight : this.mExtendedCardHeight)) * animatedValue);
        setScaleX(scale);
        setScaleY(scale);
        setTranslationZ(this.mFocusTranslationZ * animatedValue);
        if (this.mTextView != null && this.mTextViewFocused != null) {
            ViewGroup.LayoutParams params = this.mTextView.getLayoutParams();
            if (this.mExtendViewOnFocus) {
                height = Math.round(this.mTextViewHeight + ((this.mExtendedTextViewHeight - this.mTextViewHeight) * animatedValue));
            } else {
                height = (int) this.mTextViewHeight;
            }
            if (height != params.height) {
                params.height = height;
                setTextViewLayoutParams(params);
            }
            if (this.mExtendViewOnFocus) {
                setTextViewFocusedAlpha(animatedValue);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setFocusAnimatedValue(float animatedValue) {
        this.mFocusAnimatedValue = animatedValue;
        onSetFocusAnimatedValue(animatedValue);
    }

    private void startFocusAnimation(float targetAnimatedValue) {
        cancelFocusAnimationIfAny();
        final boolean selected = targetAnimatedValue == 1.0f;
        this.mFocusAnimator = ValueAnimator.ofFloat(new float[]{this.mFocusAnimatedValue, targetAnimatedValue});
        this.mFocusAnimator.setDuration((long) this.mFocusAnimDuration);
        this.mFocusAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                BaseCardView.this.setHasTransientState(true);
                BaseCardView.this.onFocusAnimationStart(selected);
            }

            public void onAnimationEnd(Animator animation) {
                BaseCardView.this.setHasTransientState(false);
                BaseCardView.this.onFocusAnimationEnd(selected);
            }
        });
        this.mFocusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                BaseCardView.this.setFocusAnimatedValue(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        this.mFocusAnimator.start();
    }

    private void cancelFocusAnimationIfAny() {
        if (this.mFocusAnimator != null) {
            this.mFocusAnimator.cancel();
            this.mFocusAnimator = null;
        }
    }

    private void setTextViewLayoutParams(ViewGroup.LayoutParams params) {
        this.mTextViewFocused.setLayoutParams(params);
        this.mTextView.setLayoutParams(params);
    }

    private void setTextViewFocusedAlpha(float focusedAlpha) {
        this.mTextViewFocused.setAlpha(focusedAlpha);
        this.mTextView.setAlpha(1.0f - focusedAlpha);
    }
}
