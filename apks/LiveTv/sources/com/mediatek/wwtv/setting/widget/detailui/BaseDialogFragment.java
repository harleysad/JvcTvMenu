package com.mediatek.wwtv.setting.widget.detailui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.TransitionImage;
import com.mediatek.wwtv.setting.util.TransitionImageAnimation;
import com.mediatek.wwtv.setting.util.UriUtils;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.view.FrameLayoutWithShadows;
import com.mediatek.wwtv.tvcenter.R;
import java.util.List;

public class BaseDialogFragment {
    public static final int ANIMATE_DELAY = 550;
    public static final int ANIMATE_IN_DURATION = 250;
    public static final int SLIDE_IN_DISTANCE = 120;
    public static final int SLIDE_IN_STAGGER = 100;
    public static final String TAG_ACTION = "action";
    public static final String TAG_CONTENT = "content";
    public int mActionAreaId = R.id.action_fragment;
    public ColorDrawable mBgDrawable = new ColorDrawable();
    public int mContentAreaId = R.id.content_fragment;
    public boolean mFirstOnStart = true;
    /* access modifiers changed from: private */
    public LiteFragment mFragment;
    public boolean mIntroAnimationInProgress = false;
    public FrameLayoutWithShadows mShadowLayer;

    public BaseDialogFragment(LiteFragment fragment) {
        this.mFragment = fragment;
    }

    public void onActionClicked(Activity activity, Action action) {
        if (activity instanceof ActionAdapter.Listener) {
            ((ActionAdapter.Listener) activity).onActionClicked(action);
            return;
        }
        Intent intent = action.getIntent();
        if (intent != null) {
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public void disableEntryAnimation() {
        this.mFirstOnStart = false;
    }

    public void setLayoutProperties(int contentAreaId, int actionAreaId) {
        this.mContentAreaId = contentAreaId;
        this.mActionAreaId = actionAreaId;
    }

    public void performEntryTransition(Activity activity, ViewGroup contentView, int iconResourceId, Uri iconResourceUri, ImageView icon, TextView title, TextView description, TextView breadcrumb) {
        int color;
        Uri iconUri;
        Activity activity2 = activity;
        ViewGroup viewGroup = contentView;
        int i = iconResourceId;
        final ImageView imageView = icon;
        RelativeLayout twoPane = (RelativeLayout) viewGroup.getChildAt(0);
        twoPane.setVisibility(4);
        this.mIntroAnimationInProgress = true;
        List<TransitionImage> images = TransitionImage.readMultipleFromIntent(activity2, activity.getIntent());
        TransitionImageAnimation ltransitionAnimation = null;
        if (images == null || images.size() <= 0) {
            iconUri = null;
            color = 0;
        } else {
            if (i != 0) {
                iconUri = Uri.parse(UriUtils.getAndroidResourceUri((Context) activity2, i));
            } else if (iconResourceUri != null) {
                iconUri = iconResourceUri;
            } else {
                iconUri = null;
            }
            TransitionImage src = images.get(0);
            color = src.getBackground();
            if (iconUri != null) {
                ltransitionAnimation = new TransitionImageAnimation(viewGroup);
                ltransitionAnimation.addTransitionSource(src);
                ltransitionAnimation.transitionDurationMs(250).transitionStartDelayMs(0).interpolator(new DecelerateInterpolator(1.0f));
            }
        }
        Uri iconUri2 = iconUri;
        int i2 = color;
        final TransitionImageAnimation transitionAnimation = ltransitionAnimation;
        activity2.overridePendingTransition(R.anim.hard_cut_in, R.anim.fade_out);
        int bgColor = this.mFragment.getResources().getColor(R.color.dialog_activity_background);
        this.mBgDrawable.setColor(bgColor);
        this.mBgDrawable.setAlpha(0);
        twoPane.setBackground(this.mBgDrawable);
        this.mShadowLayer = (FrameLayoutWithShadows) twoPane.findViewById(R.id.shadow_layout);
        if (transitionAnimation != null) {
            transitionAnimation.listener(new TransitionImageAnimation.Listener() {
                public void onRemovedView(TransitionImage src, TransitionImage dst) {
                    if (imageView != null) {
                        if (imageView.getVisibility() != 0) {
                            imageView.setImageDrawable(src.getBitmap());
                            int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
                            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                            lp.height = intrinsicWidth == 0 ? 0 : (lp.width * imageView.getDrawable().getIntrinsicHeight()) / intrinsicWidth;
                            imageView.setVisibility(0);
                        }
                        imageView.setAlpha(1.0f);
                    }
                    if (BaseDialogFragment.this.mShadowLayer != null) {
                        BaseDialogFragment.this.mShadowLayer.setShadowsAlpha(1.0f);
                    }
                    BaseDialogFragment.this.onIntroAnimationFinished();
                }
            });
            imageView.setAlpha(0.0f);
            if (this.mShadowLayer != null) {
                this.mShadowLayer.setShadowsAlpha(0.0f);
            }
        }
        final RelativeLayout relativeLayout = twoPane;
        final TextView textView = title;
        final TextView textView2 = breadcrumb;
        AnonymousClass2 r10 = r0;
        final TextView textView3 = description;
        ViewTreeObserver viewTreeObserver = twoPane.getViewTreeObserver();
        final Activity activity3 = activity2;
        int i3 = bgColor;
        final ImageView imageView2 = imageView;
        TransitionImageAnimation transitionImageAnimation = transitionAnimation;
        List<TransitionImage> list = images;
        final Uri uri = iconUri2;
        AnonymousClass2 r0 = new ViewTreeObserver.OnGlobalLayoutListener() {
            Runnable mEntryAnimationRunnable = new Runnable() {
                public void run() {
                    if (BaseDialogFragment.this.mFragment.isAdded()) {
                        relativeLayout.setVisibility(0);
                        ObjectAnimator oa = ObjectAnimator.ofInt(BaseDialogFragment.this.mBgDrawable, "alpha", new int[]{255});
                        oa.setDuration(250);
                        oa.setStartDelay(120);
                        oa.setInterpolator(new DecelerateInterpolator(1.0f));
                        oa.start();
                        BaseDialogFragment.this.prepareAndAnimateView(textView, 0.0f, -120.0f, 120, 250, new DecelerateInterpolator(1.0f), false);
                        BaseDialogFragment.this.prepareAndAnimateView(textView2, 0.0f, -120.0f, 120, 250, new DecelerateInterpolator(1.0f), false);
                        BaseDialogFragment.this.prepareAndAnimateView(textView3, 0.0f, -120.0f, 120, 250, new DecelerateInterpolator(1.0f), false);
                        View actionFragmentView = activity3.findViewById(BaseDialogFragment.this.mActionAreaId);
                        BaseDialogFragment.this.prepareAndAnimateView(actionFragmentView, 0.0f, (float) actionFragmentView.getMeasuredWidth(), 120, 250, new DecelerateInterpolator(1.0f), false);
                        if (imageView2 != null && transitionAnimation != null) {
                            TransitionImage target = new TransitionImage();
                            target.setUri(uri);
                            target.createFromImageView(imageView2);
                            if (imageView2.getBackground() instanceof ColorDrawable) {
                                target.setBackground(((ColorDrawable) imageView2.getBackground()).getColor());
                            }
                            transitionAnimation.addTransitionTarget(target);
                            transitionAnimation.startTransition();
                        } else if (imageView2 != null) {
                            BaseDialogFragment.this.prepareAndAnimateView(imageView2, 0.0f, -120.0f, 120, 250, new DecelerateInterpolator(1.0f), true);
                            if (BaseDialogFragment.this.mShadowLayer != null) {
                                BaseDialogFragment.this.mShadowLayer.setShadowsAlpha(0.0f);
                            }
                        }
                    }
                }
            };

            public void onGlobalLayout() {
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                relativeLayout.postOnAnimationDelayed(this.mEntryAnimationRunnable, 550);
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(r10);
    }

    public void prepareAndAnimateView(final View v, float initAlpha, float initTransX, int delay, int duration, Interpolator interpolator, final boolean isIcon) {
        if (v != null && v.getWindowToken() != null) {
            v.setLayerType(2, (Paint) null);
            v.buildLayer();
            v.setAlpha(initAlpha);
            v.setTranslationX(initTransX);
            v.animate().alpha(1.0f).translationX(0.0f).setDuration((long) duration).setStartDelay((long) delay);
            if (interpolator != null) {
                v.animate().setInterpolator(interpolator);
            }
            v.animate().setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    v.setLayerType(0, (Paint) null);
                    if (isIcon) {
                        if (BaseDialogFragment.this.mShadowLayer != null) {
                            BaseDialogFragment.this.mShadowLayer.setShadowsAlpha(1.0f);
                        }
                        BaseDialogFragment.this.onIntroAnimationFinished();
                    }
                }
            });
            v.animate().start();
        }
    }

    public void onIntroAnimationFinished() {
        this.mIntroAnimationInProgress = false;
    }

    public boolean isIntroAnimationInProgress() {
        return this.mIntroAnimationInProgress;
    }

    public ColorDrawable getBackgroundDrawable() {
        return this.mBgDrawable;
    }

    public void setBackgroundDrawable(ColorDrawable drawable) {
        this.mBgDrawable = drawable;
    }
}
