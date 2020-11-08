package com.mediatek.wwtv.setting.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransitionImageAnimation {
    private static long DEFAULT_CANCEL_TRANSITION_MS = 250;
    private static long DEFAULT_TRANSITION_DURATION_MS = 250;
    private static long DEFAULT_TRANSITION_START_DELAY_MS = 160;
    private static long DEFAULT_TRANSITION_TIMEOUT_MS = MessageType.delayMillis5;
    private static final int STATE_CANCELLED = 3;
    private static final int STATE_FINISHED = 4;
    private static final int STATE_INITIAL = 0;
    private static final int STATE_TRANSITION = 2;
    private static final int STATE_WAIT_DST = 1;
    private static RectF sTmpRect1 = new RectF();
    private static RectF sTmpRect2 = new RectF();
    private long mCancelTransitionMs = DEFAULT_CANCEL_TRANSITION_MS;
    private Runnable mCancelTransitionRunnable = new Runnable() {
        public void run() {
            TransitionImageAnimation.this.cancelTransition();
        }
    };
    private Comparator<TransitionImage> mComparator = new TransitionImageMatcher();
    private View.OnLayoutChangeListener mInitializeClip = new View.OnLayoutChangeListener() {
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            v.removeOnLayoutChangeListener(this);
            boolean unused = TransitionImageAnimation.this.mListeningLayout = false;
            TransitionImageAnimation.this.setProgress(0.0f);
        }
    };
    private Interpolator mInterpolator = new DecelerateInterpolator();
    /* access modifiers changed from: private */
    public Listener mListener;
    /* access modifiers changed from: private */
    public boolean mListeningLayout;
    /* access modifiers changed from: private */
    public ViewGroup mRoot;
    /* access modifiers changed from: private */
    public int mState;
    private long mTransitionDurationMs = DEFAULT_TRANSITION_DURATION_MS;
    private long mTransitionStartDelayMs = DEFAULT_TRANSITION_START_DELAY_MS;
    private long mTransitionTimeoutMs = DEFAULT_TRANSITION_TIMEOUT_MS;
    /* access modifiers changed from: private */
    public List<TransitionImageView> mTransitions = new ArrayList();

    public static class Listener {
        public void onRemovedView(TransitionImage src, TransitionImage dst) {
        }

        public void onCancelled(TransitionImageAnimation animation) {
        }

        public void onFinished(TransitionImageAnimation animation) {
        }
    }

    public TransitionImageAnimation(ViewGroup root) {
        this.mRoot = root;
        this.mState = 0;
    }

    public TransitionImageAnimation listener(Listener listener) {
        this.mListener = listener;
        return this;
    }

    public Listener getListener() {
        return this.mListener;
    }

    public TransitionImageAnimation comparator(Comparator<TransitionImage> comparator) {
        this.mComparator = comparator;
        return this;
    }

    public Comparator<TransitionImage> getComparator() {
        return this.mComparator;
    }

    public TransitionImageAnimation interpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public TransitionImageAnimation timeoutMs(long timeoutMs) {
        this.mTransitionTimeoutMs = timeoutMs;
        return this;
    }

    public long getTimeoutMs() {
        return this.mTransitionTimeoutMs;
    }

    public TransitionImageAnimation cancelDurationMs(long ms) {
        this.mCancelTransitionMs = ms;
        return this;
    }

    public long getCancelDurationMs() {
        return this.mCancelTransitionMs;
    }

    public TransitionImageAnimation transitionStartDelayMs(long delay) {
        this.mTransitionStartDelayMs = delay;
        return this;
    }

    public long getTransitionStartDelayMs() {
        return this.mTransitionStartDelayMs;
    }

    public TransitionImageAnimation transitionDurationMs(long duration) {
        this.mTransitionDurationMs = duration;
        return this;
    }

    public long getTransitionDurationMs() {
        return this.mTransitionDurationMs;
    }

    public void addTransitionSource(TransitionImage src) {
        if (this.mState == 0) {
            TransitionImageView view = new TransitionImageView(this.mRoot.getContext());
            this.mRoot.addView(view);
            view.setSourceTransition(src);
            this.mTransitions.add(view);
            if (!this.mListeningLayout) {
                this.mListeningLayout = true;
                this.mRoot.addOnLayoutChangeListener(this.mInitializeClip);
            }
        }
    }

    public void startCancelTimer() {
        if (this.mState == 0) {
            this.mRoot.postDelayed(this.mCancelTransitionRunnable, this.mTransitionTimeoutMs);
            this.mState = 1;
        }
    }

    /* access modifiers changed from: private */
    public void setProgress(float progress) {
        int lastIndex = this.mTransitions.size() - 1;
        for (int i = lastIndex; i >= 0; i--) {
            TransitionImageView view = this.mTransitions.get(i);
            view.setProgress(progress);
            sTmpRect2.left = 0.0f;
            sTmpRect2.top = 0.0f;
            sTmpRect2.right = (float) view.getWidth();
            sTmpRect2.bottom = (float) view.getHeight();
            WindowLocationUtil.getLocationsInWindow((View) view, sTmpRect2);
            if (i == lastIndex) {
                view.clearExcludeClipRect();
                sTmpRect1.set(sTmpRect2);
            } else {
                view.setExcludeClipRect(sTmpRect1);
                sTmpRect1.union(sTmpRect2);
            }
            view.invalidate();
        }
    }

    public void startTransition() {
        if (this.mState == 1 || this.mState == 0) {
            for (int i = this.mTransitions.size() - 1; i >= 0; i--) {
                TransitionImageView view = this.mTransitions.get(i);
                if (view.getDestTransition() == null) {
                    cancelTransition(view);
                    this.mTransitions.remove(i);
                }
            }
            if (this.mTransitions.size() == 0) {
                this.mState = 3;
                if (this.mListener != null) {
                    this.mListener.onCancelled(this);
                    return;
                }
                return;
            }
            ValueAnimator v = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            v.setInterpolator(this.mInterpolator);
            v.setDuration(this.mTransitionDurationMs);
            v.setStartDelay(this.mTransitionStartDelayMs);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    TransitionImageAnimation.this.setProgress(animation.getAnimatedFraction());
                }
            });
            v.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    int count = TransitionImageAnimation.this.mTransitions.size();
                    for (int i = 0; i < count; i++) {
                        TransitionImageView view = (TransitionImageView) TransitionImageAnimation.this.mTransitions.get(i);
                        if (TransitionImageAnimation.this.mListener != null) {
                            TransitionImageAnimation.this.mListener.onRemovedView(view.getSourceTransition(), view.getDestTransition());
                        }
                        TransitionImageAnimation.this.mRoot.removeView(view);
                    }
                    TransitionImageAnimation.this.mTransitions.clear();
                    int unused = TransitionImageAnimation.this.mState = 4;
                    if (TransitionImageAnimation.this.mListener != null) {
                        TransitionImageAnimation.this.mListener.onFinished(TransitionImageAnimation.this);
                    }
                }
            });
            v.start();
            this.mState = 2;
        }
    }

    private void cancelTransition(final View iv) {
        iv.animate().alpha(0.0f).setDuration(this.mCancelTransitionMs).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator arg0) {
                TransitionImageAnimation.this.mRoot.removeView(iv);
            }
        }).start();
    }

    public void cancelTransition() {
        if (this.mState == 1 || this.mState == 0) {
            int count = this.mTransitions.size();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    cancelTransition(this.mTransitions.get(i));
                }
                this.mTransitions.clear();
            }
            this.mState = 3;
            if (this.mListener != null) {
                this.mListener.onCancelled(this);
            }
        }
    }

    public boolean addTransitionTarget(TransitionImage dst) {
        if (this.mState != 1 && this.mState != 0) {
            return false;
        }
        int count = this.mTransitions.size();
        for (int i = 0; i < count; i++) {
            TransitionImageView view = this.mTransitions.get(i);
            if (this.mComparator.compare(view.getSourceTransition(), dst) == 0) {
                view.setDestTransition(dst);
                return true;
            }
        }
        return false;
    }
}
