package com.android.tv.common.ui.setup;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.common.ui.setup.animation.FadeAndShortSlide;
import com.android.tv.common.ui.setup.animation.SetupAnimationHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class SetupFragment extends Fragment {
    public static final int FRAGMENT_ENTER_TRANSITION = 1;
    public static final int FRAGMENT_EXIT_TRANSITION = 2;
    public static final int FRAGMENT_REENTER_TRANSITION = 4;
    public static final int FRAGMENT_RETURN_TRANSITION = 8;
    /* access modifiers changed from: private */
    public boolean mEnterTransitionRunning;
    private final Transition.TransitionListener mTransitionListener = new Transition.TransitionListener() {
        public void onTransitionStart(Transition transition) {
            boolean unused = SetupFragment.this.mEnterTransitionRunning = true;
        }

        public void onTransitionEnd(Transition transition) {
            boolean unused = SetupFragment.this.mEnterTransitionRunning = false;
            SetupFragment.this.onEnterTransitionEnd();
        }

        public void onTransitionCancel(Transition transition) {
        }

        public void onTransitionPause(Transition transition) {
        }

        public void onTransitionResume(Transition transition) {
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    public @interface FragmentTransitionType {
    }

    /* access modifiers changed from: protected */
    public abstract int getLayoutResourceId();

    /* access modifiers changed from: protected */
    public boolean isEnterTransitionRunning() {
        return this.mEnterTransitionRunning;
    }

    /* access modifiers changed from: protected */
    public void onEnterTransitionEnd() {
    }

    public SetupFragment() {
        setAllowEnterTransitionOverlap(false);
        setAllowReturnTransitionOverlap(false);
        enableFragmentTransition(15);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), container, false);
        view.requestFocus();
        return view;
    }

    /* access modifiers changed from: protected */
    public void setOnClickAction(View view, final String category, final int actionId) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SetupFragment.this.onActionClick(category, actionId);
            }
        });
    }

    /* access modifiers changed from: protected */
    public boolean onActionClick(String category, int actionId) {
        return SetupActionHelper.onActionClick(this, category, actionId);
    }

    /* access modifiers changed from: protected */
    public boolean onActionClick(String category, int actionId, Bundle params) {
        return SetupActionHelper.onActionClick(this, category, actionId, params);
    }

    public void setEnterTransition(Transition transition) {
        super.setEnterTransition(transition);
        if (transition != null) {
            transition.addListener(this.mTransitionListener);
        }
    }

    public void setReenterTransition(Transition transition) {
        super.setReenterTransition(transition);
        if (transition != null) {
            transition.addListener(this.mTransitionListener);
        }
    }

    public void enableFragmentTransition(int mask) {
        Transition transition;
        Transition transition2;
        Transition transition3;
        Transition transition4 = null;
        if ((mask & 1) == 0) {
            transition = null;
        } else {
            transition = createTransition(GravityCompat.END);
        }
        setEnterTransition(transition);
        if ((mask & 2) == 0) {
            transition2 = null;
        } else {
            transition2 = createTransition(GravityCompat.START);
        }
        setExitTransition(transition2);
        if ((mask & 4) == 0) {
            transition3 = null;
        } else {
            transition3 = createTransition(GravityCompat.START);
        }
        setReenterTransition(transition3);
        if ((mask & 8) != 0) {
            transition4 = createTransition(GravityCompat.END);
        }
        setReturnTransition(transition4);
    }

    public void setFragmentTransition(int transitionType, int slideEdge) {
        if (transitionType == 4) {
            setReenterTransition(createTransition(slideEdge));
        } else if (transitionType != 8) {
            switch (transitionType) {
                case 1:
                    setEnterTransition(createTransition(slideEdge));
                    return;
                case 2:
                    setExitTransition(createTransition(slideEdge));
                    return;
                default:
                    return;
            }
        } else {
            setReturnTransition(createTransition(slideEdge));
        }
    }

    private Transition createTransition(int slideEdge) {
        return new SetupAnimationHelper.TransitionBuilder().setSlideEdge(slideEdge).setParentIdsForDelay(getParentIdsForDelay()).setExcludeIds(getExcludedTargetIds()).build();
    }

    public void setShortDistance(int mask) {
        if ((mask & 1) != 0) {
            Transition transition = getEnterTransition();
            if (transition instanceof FadeAndShortSlide) {
                SetupAnimationHelper.setShortDistance((FadeAndShortSlide) transition);
            }
        }
        if ((mask & 2) != 0) {
            Transition transition2 = getExitTransition();
            if (transition2 instanceof FadeAndShortSlide) {
                SetupAnimationHelper.setShortDistance((FadeAndShortSlide) transition2);
            }
        }
        if ((mask & 4) != 0) {
            Transition transition3 = getReenterTransition();
            if (transition3 instanceof FadeAndShortSlide) {
                SetupAnimationHelper.setShortDistance((FadeAndShortSlide) transition3);
            }
        }
        if ((mask & 8) != 0) {
            Transition transition4 = getReturnTransition();
            if (transition4 instanceof FadeAndShortSlide) {
                SetupAnimationHelper.setShortDistance((FadeAndShortSlide) transition4);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int[] getParentIdsForDelay() {
        return null;
    }

    /* access modifiers changed from: protected */
    public int[] getExcludedTargetIds() {
        return null;
    }

    public int[] getSharedElementIds() {
        return null;
    }
}
