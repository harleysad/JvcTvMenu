package com.android.tv.common.ui.setup.animation;

import android.animation.TimeInterpolator;
import android.support.v4.view.GravityCompat;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.Comparator;
import java.util.List;

public class FadeAndShortSlide extends Visibility {
    private static final TimeInterpolator APPEAR_INTERPOLATOR = new DecelerateInterpolator();
    private static final int DEFAULT_DISTANCE = 200;
    private static final TimeInterpolator DISAPPEAR_INTERPOLATOR = new AccelerateInterpolator();
    private static final String PROPNAME_DELAY = "propname_delay";
    private static final String PROPNAME_SCREEN_POSITION = "android_fadeAndShortSlideTransition_screenPosition";
    private static final CalculateSlide sCalculateEnd = new CalculateSlide() {
        public float getGoneX(ViewGroup sceneRoot, View view, int[] position, int distance) {
            boolean isRtl = true;
            if (sceneRoot.getLayoutDirection() != 1) {
                isRtl = false;
            }
            if (isRtl) {
                return view.getTranslationX() - ((float) distance);
            }
            return view.getTranslationX() + ((float) distance);
        }
    };
    private static final CalculateSlide sCalculateStart = new CalculateSlide() {
        public float getGoneX(ViewGroup sceneRoot, View view, int[] position, int distance) {
            boolean isRtl = true;
            if (sceneRoot.getLayoutDirection() != 1) {
                isRtl = false;
            }
            if (isRtl) {
                return view.getTranslationX() + ((float) distance);
            }
            return view.getTranslationX() - ((float) distance);
        }
    };
    private static final ViewPositionComparator sViewPositionComparator = new ViewPositionComparator();
    private int mDistance;
    private Visibility mFade;
    private final int[] mParentIdsForDelay;
    private CalculateSlide mSlideCalculator;
    private int mSlideEdge;

    private static abstract class CalculateSlide {
        public abstract float getGoneX(ViewGroup viewGroup, View view, int[] iArr, int i);

        private CalculateSlide() {
        }
    }

    public FadeAndShortSlide() {
        this(GravityCompat.START);
    }

    public FadeAndShortSlide(int slideEdge) {
        this(slideEdge, (int[]) null);
    }

    public FadeAndShortSlide(int slideEdge, int[] parentIdsForDelay) {
        this.mSlideCalculator = sCalculateEnd;
        this.mFade = new Fade();
        this.mDistance = 200;
        setSlideEdge(slideEdge);
        this.mParentIdsForDelay = parentIdsForDelay;
    }

    public void setEpicenterCallback(Transition.EpicenterCallback epicenterCallback) {
        super.setEpicenterCallback(epicenterCallback);
        this.mFade.setEpicenterCallback(epicenterCallback);
    }

    private void captureValues(TransitionValues transitionValues) {
        int[] position = new int[2];
        transitionValues.view.getLocationOnScreen(position);
        transitionValues.values.put(PROPNAME_SCREEN_POSITION, position);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0043, code lost:
        if (r3 == r6) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004e, code lost:
        if (r3 == r6) goto L_0x0045;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getDelayOrder(android.view.View r9, boolean r10) {
        /*
            r8 = this;
            int[] r0 = r8.mParentIdsForDelay
            r1 = -1
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            android.view.View r0 = r8.findParentForDelay(r9)
            if (r0 == 0) goto L_0x005d
            boolean r2 = r0 instanceof android.view.ViewGroup
            if (r2 != 0) goto L_0x0011
            goto L_0x005d
        L_0x0011:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = r0
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            r8.getTransitionTargets(r2, r1)
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$ViewPositionComparator r2 = sViewPositionComparator
            r2.mParentForDelay = r0
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$ViewPositionComparator r2 = sViewPositionComparator
            int r3 = r9.getLayoutDirection()
            r4 = 0
            r5 = 1
            if (r3 != 0) goto L_0x002c
            r3 = r5
            goto L_0x002d
        L_0x002c:
            r3 = r4
        L_0x002d:
            r2.mIsLtr = r3
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$ViewPositionComparator r2 = sViewPositionComparator
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$ViewPositionComparator r3 = sViewPositionComparator
            boolean r3 = r3.mIsLtr
            r6 = 8388611(0x800003, float:1.1754948E-38)
            r7 = 8388613(0x800005, float:1.175495E-38)
            if (r3 == 0) goto L_0x0048
            int r3 = r8.mSlideEdge
            if (r10 == 0) goto L_0x0043
            r6 = r7
        L_0x0043:
            if (r3 != r6) goto L_0x0047
        L_0x0045:
            r4 = r5
            goto L_0x0051
        L_0x0047:
            goto L_0x0051
        L_0x0048:
            int r3 = r8.mSlideEdge
            if (r10 == 0) goto L_0x004d
            goto L_0x004e
        L_0x004d:
            r6 = r7
        L_0x004e:
            if (r3 != r6) goto L_0x0051
            goto L_0x0045
        L_0x0051:
            r2.mToLeft = r4
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$ViewPositionComparator r2 = sViewPositionComparator
            java.util.Collections.sort(r1, r2)
            int r2 = r1.indexOf(r9)
            return r2
        L_0x005d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.common.ui.setup.animation.FadeAndShortSlide.getDelayOrder(android.view.View, boolean):int");
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.View findParentForDelay(android.view.View r3) {
        /*
            r2 = this;
            int r0 = r3.getId()
            boolean r0 = r2.isParentForDelay(r0)
            if (r0 == 0) goto L_0x000b
            return r3
        L_0x000b:
            r0 = r3
        L_0x000c:
            android.view.ViewParent r1 = r0.getParent()
            boolean r1 = r1 instanceof android.view.View
            if (r1 == 0) goto L_0x0026
            android.view.ViewParent r1 = r0.getParent()
            r0 = r1
            android.view.View r0 = (android.view.View) r0
            int r1 = r0.getId()
            boolean r1 = r2.isParentForDelay(r1)
            if (r1 == 0) goto L_0x000c
            return r0
        L_0x0026:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.common.ui.setup.animation.FadeAndShortSlide.findParentForDelay(android.view.View):android.view.View");
    }

    private boolean isParentForDelay(int viewId) {
        for (int id : this.mParentIdsForDelay) {
            if (id == viewId) {
                return true;
            }
        }
        return false;
    }

    private void getTransitionTargets(ViewGroup parent, List<View> transitionTargets) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            if (!(child instanceof ViewGroup) || ((ViewGroup) child).isTransitionGroup()) {
                transitionTargets.add(child);
            } else {
                getTransitionTargets((ViewGroup) child, transitionTargets);
            }
        }
    }

    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        this.mFade.captureStartValues(transitionValues);
        captureValues(transitionValues);
        int delayIndex = getDelayOrder(transitionValues.view, false);
        if (delayIndex > 0) {
            transitionValues.values.put(PROPNAME_DELAY, Long.valueOf(((long) delayIndex) * SetupAnimationHelper.DELAY_BETWEEN_SIBLINGS_MS));
        }
    }

    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        this.mFade.captureEndValues(transitionValues);
        captureValues(transitionValues);
        int delayIndex = getDelayOrder(transitionValues.view, true);
        if (delayIndex > 0) {
            transitionValues.values.put(PROPNAME_DELAY, Long.valueOf(((long) delayIndex) * SetupAnimationHelper.DELAY_BETWEEN_SIBLINGS_MS));
        }
    }

    public void setSlideEdge(int slideEdge) {
        this.mSlideEdge = slideEdge;
        if (slideEdge == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (slideEdge == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000c, code lost:
        r12 = (int[]) r10.values.get(PROPNAME_SCREEN_POSITION);
        r13 = r12[0];
        r14 = r18.getTranslationX();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.Animator onAppear(android.view.ViewGroup r17, android.view.View r18, android.transition.TransitionValues r19, android.transition.TransitionValues r20) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r18
            r10 = r20
            r11 = 0
            if (r10 != 0) goto L_0x000c
            return r11
        L_0x000c:
            java.util.Map r0 = r10.values
            java.lang.String r1 = "android_fadeAndShortSlideTransition_screenPosition"
            java.lang.Object r0 = r0.get(r1)
            r12 = r0
            int[] r12 = (int[]) r12
            r0 = 0
            r13 = r12[r0]
            float r14 = r18.getTranslationX()
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$CalculateSlide r0 = r7.mSlideCalculator
            int r1 = r7.mDistance
            float r15 = r0.getGoneX(r8, r9, r12, r1)
            android.animation.TimeInterpolator r5 = APPEAR_INTERPOLATOR
            r0 = r9
            r1 = r10
            r2 = r13
            r3 = r15
            r4 = r14
            r6 = r7
            android.animation.Animator r0 = com.android.tv.common.ui.setup.animation.TranslationAnimationCreator.createAnimation(r0, r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x0035
            return r11
        L_0x0035:
            android.transition.Visibility r1 = r7.mFade
            android.animation.TimeInterpolator r2 = APPEAR_INTERPOLATOR
            r1.setInterpolator(r2)
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            android.animation.AnimatorSet$Builder r2 = r1.play(r0)
            android.transition.Visibility r3 = r7.mFade
            r4 = r19
            android.animation.Animator r3 = r3.onAppear(r8, r9, r4, r10)
            r2.with(r3)
            java.util.Map r2 = r10.values
            java.lang.String r3 = "propname_delay"
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            if (r2 == 0) goto L_0x0063
            long r5 = r2.longValue()
            r1.setStartDelay(r5)
        L_0x0063:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.common.ui.setup.animation.FadeAndShortSlide.onAppear(android.view.ViewGroup, android.view.View, android.transition.TransitionValues, android.transition.TransitionValues):android.animation.Animator");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000c, code lost:
        r12 = (int[]) r10.values.get(PROPNAME_SCREEN_POSITION);
        r13 = r12[0];
        r14 = r18.getTranslationX();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.Animator onDisappear(android.view.ViewGroup r17, android.view.View r18, android.transition.TransitionValues r19, android.transition.TransitionValues r20) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r18
            r10 = r19
            r11 = 0
            if (r10 != 0) goto L_0x000c
            return r11
        L_0x000c:
            java.util.Map r0 = r10.values
            java.lang.String r1 = "android_fadeAndShortSlideTransition_screenPosition"
            java.lang.Object r0 = r0.get(r1)
            r12 = r0
            int[] r12 = (int[]) r12
            r0 = 0
            r13 = r12[r0]
            float r14 = r18.getTranslationX()
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$CalculateSlide r0 = r7.mSlideCalculator
            int r1 = r7.mDistance
            float r15 = r0.getGoneX(r8, r9, r12, r1)
            android.animation.TimeInterpolator r5 = DISAPPEAR_INTERPOLATOR
            r0 = r9
            r1 = r10
            r2 = r13
            r3 = r14
            r4 = r15
            r6 = r7
            android.animation.Animator r0 = com.android.tv.common.ui.setup.animation.TranslationAnimationCreator.createAnimation(r0, r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x0035
            return r11
        L_0x0035:
            android.transition.Visibility r1 = r7.mFade
            android.animation.TimeInterpolator r2 = DISAPPEAR_INTERPOLATOR
            r1.setInterpolator(r2)
            android.transition.Visibility r1 = r7.mFade
            r2 = r20
            android.animation.Animator r1 = r1.onDisappear(r8, r9, r10, r2)
            if (r1 != 0) goto L_0x0047
            return r11
        L_0x0047:
            com.android.tv.common.ui.setup.animation.FadeAndShortSlide$3 r3 = new com.android.tv.common.ui.setup.animation.FadeAndShortSlide$3
            r3.<init>(r1, r9)
            r1.addListener(r3)
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            android.animation.AnimatorSet$Builder r4 = r3.play(r0)
            r4.with(r1)
            java.util.Map r4 = r10.values
            java.lang.String r5 = "propname_delay"
            java.lang.Object r4 = r4.get(r5)
            java.lang.Long r4 = (java.lang.Long) r4
            if (r4 == 0) goto L_0x006e
            long r5 = r4.longValue()
            r3.setStartDelay(r5)
        L_0x006e:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.common.ui.setup.animation.FadeAndShortSlide.onDisappear(android.view.ViewGroup, android.view.View, android.transition.TransitionValues, android.transition.TransitionValues):android.animation.Animator");
    }

    public Transition addListener(Transition.TransitionListener listener) {
        this.mFade.addListener(listener);
        return super.addListener(listener);
    }

    public Transition removeListener(Transition.TransitionListener listener) {
        this.mFade.removeListener(listener);
        return super.removeListener(listener);
    }

    public Transition clone() {
        FadeAndShortSlide clone = (FadeAndShortSlide) super.clone();
        clone.mFade = (Visibility) this.mFade.clone();
        return clone;
    }

    public Transition setDuration(long duration) {
        long scaledDuration = SetupAnimationHelper.applyAnimationTimeScale(duration);
        this.mFade.setDuration(scaledDuration);
        return super.setDuration(scaledDuration);
    }

    public void setDistance(int distance) {
        this.mDistance = distance;
    }

    private static class ViewPositionComparator implements Comparator<View> {
        boolean mIsLtr;
        View mParentForDelay;
        boolean mToLeft;

        private ViewPositionComparator() {
        }

        public int compare(View lhs, View rhs) {
            int start2;
            int start1;
            if (this.mIsLtr) {
                start1 = getRelativeLeft(lhs, this.mParentForDelay);
                start2 = getRelativeLeft(rhs, this.mParentForDelay);
            } else {
                start1 = getRelativeRight(lhs, this.mParentForDelay);
                start2 = getRelativeRight(rhs, this.mParentForDelay);
            }
            if (this.mToLeft) {
                if (start1 > start2) {
                    return 1;
                }
                if (start1 < start2) {
                    return -1;
                }
            } else if (start1 > start2) {
                return -1;
            } else {
                if (start1 < start2) {
                    return 1;
                }
            }
            return Integer.compare(getRelativeTop(lhs, this.mParentForDelay), getRelativeTop(rhs, this.mParentForDelay));
        }

        private int getRelativeLeft(View child, View ancestor) {
            ViewParent parent = child.getParent();
            int left = child.getLeft();
            while ((parent instanceof View) && parent != ancestor) {
                left += ((View) parent).getLeft();
                parent = parent.getParent();
            }
            return left;
        }

        private int getRelativeRight(View child, View ancestor) {
            ViewParent parent = child.getParent();
            int right = child.getRight();
            while ((parent instanceof View) && parent != ancestor) {
                right += ((View) parent).getLeft();
                parent = parent.getParent();
            }
            return right;
        }

        private int getRelativeTop(View child, View ancestor) {
            ViewParent parent = child.getParent();
            int top = child.getTop();
            while ((parent instanceof View) && parent != ancestor) {
                top += ((View) parent).getTop();
                parent = parent.getParent();
            }
            return top;
        }
    }
}
