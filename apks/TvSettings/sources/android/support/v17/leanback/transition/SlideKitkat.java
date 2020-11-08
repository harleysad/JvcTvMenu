package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.R;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

@RequiresApi(19)
class SlideKitkat extends Visibility {
    private static final String TAG = "SlideKitkat";
    private static final TimeInterpolator sAccelerate = new AccelerateInterpolator();
    private static final CalculateSlide sCalculateBottom = new CalculateSlideVertical() {
        public float getGone(View view) {
            return view.getTranslationY() + ((float) view.getHeight());
        }
    };
    private static final CalculateSlide sCalculateEnd = new CalculateSlideHorizontal() {
        public float getGone(View view) {
            if (view.getLayoutDirection() == 1) {
                return view.getTranslationX() - ((float) view.getWidth());
            }
            return view.getTranslationX() + ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateLeft = new CalculateSlideHorizontal() {
        public float getGone(View view) {
            return view.getTranslationX() - ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateRight = new CalculateSlideHorizontal() {
        public float getGone(View view) {
            return view.getTranslationX() + ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateStart = new CalculateSlideHorizontal() {
        public float getGone(View view) {
            if (view.getLayoutDirection() == 1) {
                return view.getTranslationX() + ((float) view.getWidth());
            }
            return view.getTranslationX() - ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateTop = new CalculateSlideVertical() {
        public float getGone(View view) {
            return view.getTranslationY() - ((float) view.getHeight());
        }
    };
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    private CalculateSlide mSlideCalculator;
    private int mSlideEdge;

    private interface CalculateSlide {
        float getGone(View view);

        float getHere(View view);

        Property<View, Float> getProperty();
    }

    private static abstract class CalculateSlideHorizontal implements CalculateSlide {
        CalculateSlideHorizontal() {
        }

        public float getHere(View view) {
            return view.getTranslationX();
        }

        public Property<View, Float> getProperty() {
            return View.TRANSLATION_X;
        }
    }

    private static abstract class CalculateSlideVertical implements CalculateSlide {
        CalculateSlideVertical() {
        }

        public float getHere(View view) {
            return view.getTranslationY();
        }

        public Property<View, Float> getProperty() {
            return View.TRANSLATION_Y;
        }
    }

    public SlideKitkat() {
        setSlideEdge(80);
    }

    public SlideKitkat(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbSlide);
        setSlideEdge(a.getInt(R.styleable.lbSlide_lb_slideEdge, 80));
        long duration = (long) a.getInt(R.styleable.lbSlide_android_duration, -1);
        if (duration >= 0) {
            setDuration(duration);
        }
        long startDelay = (long) a.getInt(R.styleable.lbSlide_android_startDelay, -1);
        if (startDelay > 0) {
            setStartDelay(startDelay);
        }
        int resID = a.getResourceId(R.styleable.lbSlide_android_interpolator, 0);
        if (resID > 0) {
            setInterpolator(AnimationUtils.loadInterpolator(context, resID));
        }
        a.recycle();
    }

    public void setSlideEdge(int slideEdge) {
        if (slideEdge == 3) {
            this.mSlideCalculator = sCalculateLeft;
        } else if (slideEdge == 5) {
            this.mSlideCalculator = sCalculateRight;
        } else if (slideEdge == 48) {
            this.mSlideCalculator = sCalculateTop;
        } else if (slideEdge == 80) {
            this.mSlideCalculator = sCalculateBottom;
        } else if (slideEdge == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (slideEdge == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
        this.mSlideEdge = slideEdge;
    }

    public int getSlideEdge() {
        return this.mSlideEdge;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v0, resolved type: float[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.animation.Animator createAnimation(android.view.View r13, android.util.Property<android.view.View, java.lang.Float> r14, float r15, float r16, float r17, android.animation.TimeInterpolator r18, int r19) {
        /*
            r12 = this;
            r6 = r13
            r7 = r14
            int r0 = android.support.v17.leanback.R.id.lb_slide_transition_value
            java.lang.Object r0 = r6.getTag(r0)
            r8 = r0
            float[] r8 = (float[]) r8
            r0 = 0
            r1 = 1
            if (r8 == 0) goto L_0x0020
            android.util.Property r2 = android.view.View.TRANSLATION_Y
            if (r2 != r7) goto L_0x0016
            r2 = r8[r1]
            goto L_0x0018
        L_0x0016:
            r2 = r8[r0]
        L_0x0018:
            int r3 = android.support.v17.leanback.R.id.lb_slide_transition_value
            r4 = 0
            r6.setTag(r3, r4)
            r9 = r2
            goto L_0x0021
        L_0x0020:
            r9 = r15
        L_0x0021:
            r2 = 2
            float[] r2 = new float[r2]
            r2[r0] = r9
            r2[r1] = r16
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r6, r7, r2)
            android.support.v17.leanback.transition.SlideKitkat$SlideAnimatorListener r11 = new android.support.v17.leanback.transition.SlideKitkat$SlideAnimatorListener
            r0 = r11
            r1 = r6
            r2 = r7
            r3 = r17
            r4 = r16
            r5 = r19
            r0.<init>(r1, r2, r3, r4, r5)
            r10.addListener(r0)
            r10.addPauseListener(r0)
            r1 = r18
            r10.setInterpolator(r1)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.transition.SlideKitkat.createAnimation(android.view.View, android.util.Property, float, float, float, android.animation.TimeInterpolator, int):android.animation.Animator");
    }

    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        TransitionValues transitionValues = endValues;
        View view = transitionValues != null ? transitionValues.view : null;
        if (view == null) {
            return null;
        }
        float end = this.mSlideCalculator.getHere(view);
        return createAnimation(view, this.mSlideCalculator.getProperty(), this.mSlideCalculator.getGone(view), end, end, sDecelerate, 0);
    }

    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        TransitionValues transitionValues = startValues;
        View view = transitionValues != null ? transitionValues.view : null;
        if (view == null) {
            return null;
        }
        float start = this.mSlideCalculator.getHere(view);
        return createAnimation(view, this.mSlideCalculator.getProperty(), start, this.mSlideCalculator.getGone(view), start, sAccelerate, 4);
    }

    private static class SlideAnimatorListener extends AnimatorListenerAdapter {
        private boolean mCanceled = false;
        private final float mEndValue;
        private final int mFinalVisibility;
        private float mPausedValue;
        private final Property<View, Float> mProp;
        private final float mTerminalValue;
        private final View mView;

        public SlideAnimatorListener(View view, Property<View, Float> prop, float terminalValue, float endValue, int finalVisibility) {
            this.mProp = prop;
            this.mView = view;
            this.mTerminalValue = terminalValue;
            this.mEndValue = endValue;
            this.mFinalVisibility = finalVisibility;
            view.setVisibility(0);
        }

        public void onAnimationCancel(Animator animator) {
            this.mView.setTag(R.id.lb_slide_transition_value, new float[]{this.mView.getTranslationX(), this.mView.getTranslationY()});
            this.mProp.set(this.mView, Float.valueOf(this.mTerminalValue));
            this.mCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCanceled) {
                this.mProp.set(this.mView, Float.valueOf(this.mTerminalValue));
            }
            this.mView.setVisibility(this.mFinalVisibility);
        }

        public void onAnimationPause(Animator animator) {
            this.mPausedValue = this.mProp.get(this.mView).floatValue();
            this.mProp.set(this.mView, Float.valueOf(this.mEndValue));
            this.mView.setVisibility(this.mFinalVisibility);
        }

        public void onAnimationResume(Animator animator) {
            this.mProp.set(this.mView, Float.valueOf(this.mPausedValue));
            this.mView.setVisibility(0);
        }
    }
}
