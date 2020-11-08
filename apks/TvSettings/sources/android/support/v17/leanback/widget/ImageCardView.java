package android.support.v17.leanback.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageCardView extends BaseCardView {
    private static final String ALPHA = "alpha";
    public static final int CARD_TYPE_FLAG_CONTENT = 2;
    public static final int CARD_TYPE_FLAG_ICON_LEFT = 8;
    public static final int CARD_TYPE_FLAG_ICON_RIGHT = 4;
    public static final int CARD_TYPE_FLAG_IMAGE_ONLY = 0;
    public static final int CARD_TYPE_FLAG_TITLE = 1;
    private boolean mAttachedToWindow;
    private ImageView mBadgeImage;
    private TextView mContentView;
    ObjectAnimator mFadeInAnimator;
    private ImageView mImageView;
    private ViewGroup mInfoArea;
    private TextView mTitleView;

    @Deprecated
    public ImageCardView(Context context, int themeResId) {
        this(new ContextThemeWrapper(context, themeResId));
    }

    public ImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildImageCardView(attrs, defStyleAttr, R.style.Widget_Leanback_ImageCardView);
    }

    private void buildImageCardView(AttributeSet attrs, int defStyleAttr, int defStyle) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lb_image_card_view, this);
        TypedArray cardAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.lbImageCardView, defStyleAttr, defStyle);
        int cardType = cardAttrs.getInt(R.styleable.lbImageCardView_lbImageCardViewType, 0);
        boolean hasImageOnly = cardType == 0;
        boolean hasTitle = (cardType & 1) == 1;
        boolean hasContent = (cardType & 2) == 2;
        boolean hasIconRight = (cardType & 4) == 4;
        boolean hasIconLeft = !hasIconRight && (cardType & 8) == 8;
        this.mImageView = (ImageView) findViewById(R.id.main_image);
        if (this.mImageView.getDrawable() == null) {
            this.mImageView.setVisibility(4);
        }
        this.mFadeInAnimator = ObjectAnimator.ofFloat(this.mImageView, ALPHA, new float[]{1.0f});
        this.mFadeInAnimator.setDuration((long) this.mImageView.getResources().getInteger(17694720));
        this.mInfoArea = (ViewGroup) findViewById(R.id.info_field);
        if (hasImageOnly) {
            removeView(this.mInfoArea);
            cardAttrs.recycle();
            return;
        }
        if (hasTitle) {
            this.mTitleView = (TextView) inflater.inflate(R.layout.lb_image_card_view_themed_title, this.mInfoArea, false);
            this.mInfoArea.addView(this.mTitleView);
        }
        if (hasContent) {
            this.mContentView = (TextView) inflater.inflate(R.layout.lb_image_card_view_themed_content, this.mInfoArea, false);
            this.mInfoArea.addView(this.mContentView);
        }
        if (hasIconRight || hasIconLeft) {
            int layoutId = R.layout.lb_image_card_view_themed_badge_right;
            if (hasIconLeft) {
                layoutId = R.layout.lb_image_card_view_themed_badge_left;
            }
            this.mBadgeImage = (ImageView) inflater.inflate(layoutId, this.mInfoArea, false);
            this.mInfoArea.addView(this.mBadgeImage);
        }
        if (hasTitle && !hasContent && this.mBadgeImage != null) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) this.mTitleView.getLayoutParams();
            if (hasIconLeft) {
                relativeLayoutParams.addRule(17, this.mBadgeImage.getId());
            } else {
                relativeLayoutParams.addRule(16, this.mBadgeImage.getId());
            }
            this.mTitleView.setLayoutParams(relativeLayoutParams);
        }
        if (hasContent) {
            RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) this.mContentView.getLayoutParams();
            if (!hasTitle) {
                relativeLayoutParams2.addRule(10);
            }
            if (hasIconLeft) {
                relativeLayoutParams2.removeRule(16);
                relativeLayoutParams2.removeRule(20);
                relativeLayoutParams2.addRule(17, this.mBadgeImage.getId());
            }
            this.mContentView.setLayoutParams(relativeLayoutParams2);
        }
        if (this.mBadgeImage != null) {
            RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) this.mBadgeImage.getLayoutParams();
            if (hasContent) {
                relativeLayoutParams3.addRule(8, this.mContentView.getId());
            } else if (hasTitle) {
                relativeLayoutParams3.addRule(8, this.mTitleView.getId());
            }
            this.mBadgeImage.setLayoutParams(relativeLayoutParams3);
        }
        Drawable background = cardAttrs.getDrawable(R.styleable.lbImageCardView_infoAreaBackground);
        if (background != null) {
            setInfoAreaBackground(background);
        }
        if (this.mBadgeImage != null && this.mBadgeImage.getDrawable() == null) {
            this.mBadgeImage.setVisibility(8);
        }
        cardAttrs.recycle();
    }

    public ImageCardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ImageCardView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageCardViewStyle);
    }

    public final ImageView getMainImageView() {
        return this.mImageView;
    }

    public void setMainImageAdjustViewBounds(boolean adjustViewBounds) {
        if (this.mImageView != null) {
            this.mImageView.setAdjustViewBounds(adjustViewBounds);
        }
    }

    public void setMainImageScaleType(ImageView.ScaleType scaleType) {
        if (this.mImageView != null) {
            this.mImageView.setScaleType(scaleType);
        }
    }

    public void setMainImage(Drawable drawable) {
        setMainImage(drawable, true);
    }

    public void setMainImage(Drawable drawable, boolean fade) {
        if (this.mImageView != null) {
            this.mImageView.setImageDrawable(drawable);
            if (drawable == null) {
                this.mFadeInAnimator.cancel();
                this.mImageView.setAlpha(1.0f);
                this.mImageView.setVisibility(4);
                return;
            }
            this.mImageView.setVisibility(0);
            if (fade) {
                fadeIn();
                return;
            }
            this.mFadeInAnimator.cancel();
            this.mImageView.setAlpha(1.0f);
        }
    }

    public void setMainImageDimensions(int width, int height) {
        ViewGroup.LayoutParams lp = this.mImageView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        this.mImageView.setLayoutParams(lp);
    }

    public Drawable getMainImage() {
        if (this.mImageView == null) {
            return null;
        }
        return this.mImageView.getDrawable();
    }

    public Drawable getInfoAreaBackground() {
        if (this.mInfoArea != null) {
            return this.mInfoArea.getBackground();
        }
        return null;
    }

    public void setInfoAreaBackground(Drawable drawable) {
        if (this.mInfoArea != null) {
            this.mInfoArea.setBackground(drawable);
        }
    }

    public void setInfoAreaBackgroundColor(@ColorInt int color) {
        if (this.mInfoArea != null) {
            this.mInfoArea.setBackgroundColor(color);
        }
    }

    public void setTitleText(CharSequence text) {
        if (this.mTitleView != null) {
            this.mTitleView.setText(text);
        }
    }

    public CharSequence getTitleText() {
        if (this.mTitleView == null) {
            return null;
        }
        return this.mTitleView.getText();
    }

    public void setContentText(CharSequence text) {
        if (this.mContentView != null) {
            this.mContentView.setText(text);
        }
    }

    public CharSequence getContentText() {
        if (this.mContentView == null) {
            return null;
        }
        return this.mContentView.getText();
    }

    public void setBadgeImage(Drawable drawable) {
        if (this.mBadgeImage != null) {
            this.mBadgeImage.setImageDrawable(drawable);
            if (drawable != null) {
                this.mBadgeImage.setVisibility(0);
            } else {
                this.mBadgeImage.setVisibility(8);
            }
        }
    }

    public Drawable getBadgeImage() {
        if (this.mBadgeImage == null) {
            return null;
        }
        return this.mBadgeImage.getDrawable();
    }

    private void fadeIn() {
        this.mImageView.setAlpha(0.0f);
        if (this.mAttachedToWindow) {
            this.mFadeInAnimator.start();
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        if (this.mImageView.getAlpha() == 0.0f) {
            fadeIn();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        this.mFadeInAnimator.cancel();
        this.mImageView.setAlpha(1.0f);
        super.onDetachedFromWindow();
    }
}
