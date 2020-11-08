package android.support.v17.leanback.transition;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R;
import android.support.v4.view.GravityCompat;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

@RequiresApi(21)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class FadeAndShortSlide extends Visibility {
    private static final String PROPNAME_SCREEN_POSITION = "android:fadeAndShortSlideTransition:screenPosition";
    static final CalculateSlide sCalculateBottom = new CalculateSlide() {
        public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationY() + t.getVerticalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateEnd = new CalculateSlide() {
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            boolean isRtl = true;
            if (sceneRoot.getLayoutDirection() != 1) {
                isRtl = false;
            }
            if (isRtl) {
                return view.getTranslationX() - t.getHorizontalDistance(sceneRoot);
            }
            return view.getTranslationX() + t.getHorizontalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateStart = new CalculateSlide() {
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            boolean isRtl = true;
            if (sceneRoot.getLayoutDirection() != 1) {
                isRtl = false;
            }
            if (isRtl) {
                return view.getTranslationX() + t.getHorizontalDistance(sceneRoot);
            }
            return view.getTranslationX() - t.getHorizontalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateStartEnd = new CalculateSlide() {
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            int sceneRootCenter;
            int viewCenter = position[0] + (view.getWidth() / 2);
            sceneRoot.getLocationOnScreen(position);
            Rect center = t.getEpicenter();
            if (center == null) {
                sceneRootCenter = position[0] + (sceneRoot.getWidth() / 2);
            } else {
                sceneRootCenter = center.centerX();
            }
            if (viewCenter < sceneRootCenter) {
                return view.getTranslationX() - t.getHorizontalDistance(sceneRoot);
            }
            return view.getTranslationX() + t.getHorizontalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateTop = new CalculateSlide() {
        public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationY() - t.getVerticalDistance(sceneRoot);
        }
    };
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    private float mDistance;
    private Visibility mFade;
    private CalculateSlide mSlideCalculator;
    final CalculateSlide sCalculateTopBottom;

    private static abstract class CalculateSlide {
        CalculateSlide() {
        }

        /* access modifiers changed from: package-private */
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationX();
        }

        /* access modifiers changed from: package-private */
        public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationY();
        }
    }

    /* access modifiers changed from: package-private */
    public float getHorizontalDistance(ViewGroup sceneRoot) {
        return this.mDistance >= 0.0f ? this.mDistance : (float) (sceneRoot.getWidth() / 4);
    }

    /* access modifiers changed from: package-private */
    public float getVerticalDistance(ViewGroup sceneRoot) {
        return this.mDistance >= 0.0f ? this.mDistance : (float) (sceneRoot.getHeight() / 4);
    }

    public FadeAndShortSlide() {
        this(GravityCompat.START);
    }

    public FadeAndShortSlide(int slideEdge) {
        this.mFade = new Fade();
        this.mDistance = -1.0f;
        this.sCalculateTopBottom = new CalculateSlide() {
            public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
                int sceneRootCenter;
                int viewCenter = position[1] + (view.getHeight() / 2);
                sceneRoot.getLocationOnScreen(position);
                Rect center = FadeAndShortSlide.this.getEpicenter();
                if (center == null) {
                    sceneRootCenter = position[1] + (sceneRoot.getHeight() / 2);
                } else {
                    sceneRootCenter = center.centerY();
                }
                if (viewCenter < sceneRootCenter) {
                    return view.getTranslationY() - t.getVerticalDistance(sceneRoot);
                }
                return view.getTranslationY() + t.getVerticalDistance(sceneRoot);
            }
        };
        setSlideEdge(slideEdge);
    }

    public FadeAndShortSlide(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFade = new Fade();
        this.mDistance = -1.0f;
        this.sCalculateTopBottom = new CalculateSlide() {
            public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
                int sceneRootCenter;
                int viewCenter = position[1] + (view.getHeight() / 2);
                sceneRoot.getLocationOnScreen(position);
                Rect center = FadeAndShortSlide.this.getEpicenter();
                if (center == null) {
                    sceneRootCenter = position[1] + (sceneRoot.getHeight() / 2);
                } else {
                    sceneRootCenter = center.centerY();
                }
                if (viewCenter < sceneRootCenter) {
                    return view.getTranslationY() - t.getVerticalDistance(sceneRoot);
                }
                return view.getTranslationY() + t.getVerticalDistance(sceneRoot);
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbSlide);
        setSlideEdge(a.getInt(R.styleable.lbSlide_lb_slideEdge, GravityCompat.START));
        a.recycle();
    }

    public void setEpicenterCallback(Transition.EpicenterCallback epicenterCallback) {
        this.mFade.setEpicenterCallback(epicenterCallback);
        super.setEpicenterCallback(epicenterCallback);
    }

    private void captureValues(TransitionValues transitionValues) {
        int[] position = new int[2];
        transitionValues.view.getLocationOnScreen(position);
        transitionValues.values.put(PROPNAME_SCREEN_POSITION, position);
    }

    public void captureStartValues(TransitionValues transitionValues) {
        this.mFade.captureStartValues(transitionValues);
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    public void captureEndValues(TransitionValues transitionValues) {
        this.mFade.captureEndValues(transitionValues);
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    public void setSlideEdge(int slideEdge) {
        if (slideEdge == 48) {
            this.mSlideCalculator = sCalculateTop;
        } else if (slideEdge == 80) {
            this.mSlideCalculator = sCalculateBottom;
        } else if (slideEdge == 112) {
            this.mSlideCalculator = this.sCalculateTopBottom;
        } else if (slideEdge == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (slideEdge == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else if (slideEdge == 8388615) {
            this.mSlideCalculator = sCalculateStartEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
    }

    /* JADX WARNING: type inference failed for: r23v0, types: [android.view.View] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.Animator onAppear(android.view.ViewGroup r22, android.view.View r23, android.transition.TransitionValues r24, android.transition.TransitionValues r25) {
        /*
            r21 = this;
            r10 = r21
            r11 = r22
            r12 = r23
            r13 = r25
            r0 = 0
            if (r13 != 0) goto L_0x000c
            return r0
        L_0x000c:
            if (r11 != r12) goto L_0x000f
            return r0
        L_0x000f:
            java.util.Map r0 = r13.values
            java.lang.String r1 = "android:fadeAndShortSlideTransition:screenPosition"
            java.lang.Object r0 = r0.get(r1)
            r14 = r0
            int[] r14 = (int[]) r14
            r0 = 0
            r15 = r14[r0]
            r0 = 1
            r16 = r14[r0]
            float r17 = r23.getTranslationX()
            android.support.v17.leanback.transition.FadeAndShortSlide$CalculateSlide r0 = r10.mSlideCalculator
            float r18 = r0.getGoneX(r10, r11, r12, r14)
            float r19 = r23.getTranslationY()
            android.support.v17.leanback.transition.FadeAndShortSlide$CalculateSlide r0 = r10.mSlideCalculator
            float r20 = r0.getGoneY(r10, r11, r12, r14)
            android.animation.TimeInterpolator r8 = sDecelerate
            r0 = r12
            r1 = r13
            r2 = r15
            r3 = r16
            r4 = r18
            r5 = r20
            r6 = r17
            r7 = r19
            r9 = r10
            android.animation.Animator r0 = android.support.v17.leanback.transition.TranslationAnimationCreator.createAnimation(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9)
            android.transition.Visibility r1 = r10.mFade
            r2 = r24
            android.animation.Animator r1 = r1.onAppear(r11, r12, r2, r13)
            if (r0 != 0) goto L_0x0053
            return r1
        L_0x0053:
            if (r1 != 0) goto L_0x0056
            return r0
        L_0x0056:
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            android.animation.AnimatorSet$Builder r4 = r3.play(r0)
            r4.with(r1)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.transition.FadeAndShortSlide.onAppear(android.view.ViewGroup, android.view.View, android.transition.TransitionValues, android.transition.TransitionValues):android.animation.Animator");
    }

    /* JADX WARNING: type inference failed for: r23v0, types: [android.view.View] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.Animator onDisappear(android.view.ViewGroup r22, android.view.View r23, android.transition.TransitionValues r24, android.transition.TransitionValues r25) {
        /*
            r21 = this;
            r10 = r21
            r11 = r22
            r12 = r23
            r13 = r24
            r0 = 0
            if (r13 != 0) goto L_0x000c
            return r0
        L_0x000c:
            if (r11 != r12) goto L_0x000f
            return r0
        L_0x000f:
            java.util.Map r0 = r13.values
            java.lang.String r1 = "android:fadeAndShortSlideTransition:screenPosition"
            java.lang.Object r0 = r0.get(r1)
            r14 = r0
            int[] r14 = (int[]) r14
            r0 = 0
            r15 = r14[r0]
            r0 = 1
            r16 = r14[r0]
            float r17 = r23.getTranslationX()
            android.support.v17.leanback.transition.FadeAndShortSlide$CalculateSlide r0 = r10.mSlideCalculator
            float r18 = r0.getGoneX(r10, r11, r12, r14)
            float r19 = r23.getTranslationY()
            android.support.v17.leanback.transition.FadeAndShortSlide$CalculateSlide r0 = r10.mSlideCalculator
            float r20 = r0.getGoneY(r10, r11, r12, r14)
            android.animation.TimeInterpolator r8 = sDecelerate
            r0 = r12
            r1 = r13
            r2 = r15
            r3 = r16
            r4 = r17
            r5 = r19
            r6 = r18
            r7 = r20
            r9 = r10
            android.animation.Animator r0 = android.support.v17.leanback.transition.TranslationAnimationCreator.createAnimation(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9)
            android.transition.Visibility r1 = r10.mFade
            r2 = r25
            android.animation.Animator r1 = r1.onDisappear(r11, r12, r13, r2)
            if (r0 != 0) goto L_0x0053
            return r1
        L_0x0053:
            if (r1 != 0) goto L_0x0056
            return r0
        L_0x0056:
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            android.animation.AnimatorSet$Builder r4 = r3.play(r0)
            r4.with(r1)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.transition.FadeAndShortSlide.onDisappear(android.view.ViewGroup, android.view.View, android.transition.TransitionValues, android.transition.TransitionValues):android.animation.Animator");
    }

    public Transition addListener(Transition.TransitionListener listener) {
        this.mFade.addListener(listener);
        return super.addListener(listener);
    }

    public Transition removeListener(Transition.TransitionListener listener) {
        this.mFade.removeListener(listener);
        return super.removeListener(listener);
    }

    public float getDistance() {
        return this.mDistance;
    }

    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    public Transition clone() {
        FadeAndShortSlide clone = (FadeAndShortSlide) super.clone();
        clone.mFade = (Visibility) this.mFade.clone();
        return clone;
    }
}
