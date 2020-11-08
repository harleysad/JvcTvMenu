package com.mediatek.wwtv.setting.widget.detailui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.tvcenter.R;

public class DialogFragment extends Fragment implements ActionAdapter.Listener, LiteFragment {
    private Activity mActivity;
    private BaseDialogFragment mBase = new BaseDialogFragment(this);

    public void onActionClicked(Action action) {
        this.mBase.onActionClicked(getRealActivity(), action);
    }

    /* access modifiers changed from: protected */
    public void disableEntryAnimation() {
        this.mBase.disableEntryAnimation();
    }

    public void performEntryTransition() {
        ContentFragment cf;
        if (this.mBase.mFirstOnStart) {
            this.mBase.mFirstOnStart = false;
            Fragment fragment = getContentFragment();
            if ((fragment instanceof ContentFragment) && (cf = (ContentFragment) fragment) != null && cf.getIcon() != null && cf.getTitle() != null && cf.getDescription() != null && cf.getBreadCrumb() != null) {
                this.mBase.performEntryTransition(getRealActivity(), (ViewGroup) ((DialogActivity) getRealActivity()).findViewById(16908290), cf.getIconResourceId(), cf.getIconResourceUri(), cf.getIcon(), cf.getTitle(), cf.getDescription(), cf.getBreadCrumb());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setLayoutProperties(int contentAreaId, int actionAreaId) {
        this.mBase.setLayoutProperties(contentAreaId, actionAreaId);
    }

    /* access modifiers changed from: protected */
    public void prepareAndAnimateView(View v, float initAlpha, float initTransX, int delay, int duration, Interpolator interpolator, boolean isIcon) {
        this.mBase.prepareAndAnimateView(v, initAlpha, initTransX, delay, duration, interpolator, isIcon);
    }

    /* access modifiers changed from: protected */
    public void onIntroAnimationFinished() {
        this.mBase.onIntroAnimationFinished();
    }

    /* access modifiers changed from: protected */
    public boolean isIntroAnimationInProgress() {
        return this.mBase.isIntroAnimationInProgress();
    }

    /* access modifiers changed from: protected */
    public ColorDrawable getBackgroundDrawable() {
        return this.mBase.getBackgroundDrawable();
    }

    /* access modifiers changed from: protected */
    public void setBackgroundDrawable(ColorDrawable drawable) {
        this.mBase.setBackgroundDrawable(drawable);
    }

    public void setActivity(Activity act) {
        this.mActivity = act;
    }

    private Activity getRealActivity() {
        return this.mActivity != null ? this.mActivity : getActivity();
    }

    /* access modifiers changed from: protected */
    public void setContentFragment(Fragment fragment) {
        getContentFragmentTransaction(fragment).commit();
    }

    /* access modifiers changed from: protected */
    public void setActionFragment(Fragment fragment) {
        setActionFragment(fragment, true);
    }

    /* access modifiers changed from: protected */
    public void setActionFragment(Fragment fragment, boolean addToBackStack) {
        addActionFragmentToTransaction(fragment, (FragmentTransaction) null, addToBackStack, getRealActivity().getFragmentManager()).commit();
    }

    /* access modifiers changed from: protected */
    public Fragment getActionFragment() {
        return getRealActivity().getFragmentManager().findFragmentByTag("action");
    }

    /* access modifiers changed from: protected */
    public Fragment getContentFragment() {
        return getRealActivity().getFragmentManager().findFragmentByTag(BaseDialogFragment.TAG_CONTENT);
    }

    /* access modifiers changed from: protected */
    public void setContentAndActionFragments(Fragment contentFragment, Fragment actionFragment) {
        setContentAndActionFragments(contentFragment, actionFragment, true);
    }

    /* access modifiers changed from: protected */
    public void setContentAndActionFragments(Fragment contentFragment, Fragment actionFragment, boolean addToBackStack) {
        addActionFragmentToTransaction(actionFragment, getContentFragmentTransaction(contentFragment), addToBackStack, getRealActivity().getFragmentManager()).commit();
    }

    private FragmentTransaction getContentFragmentTransaction(Fragment fragment) {
        FragmentManager fm = getRealActivity().getFragmentManager();
        boolean hasContent = fm.findFragmentByTag(BaseDialogFragment.TAG_CONTENT) != null;
        FragmentTransaction ft = fm.beginTransaction();
        if (hasContent) {
            addAnimations(ft);
        }
        ft.replace(this.mBase.mContentAreaId, fragment, BaseDialogFragment.TAG_CONTENT);
        return ft;
    }

    private FragmentTransaction addActionFragmentToTransaction(Fragment fragment, FragmentTransaction ft, boolean addToBackStack, FragmentManager fm) {
        if (ft == null) {
            ft = fm.beginTransaction();
        }
        if (fm.findFragmentByTag("action") != null) {
            addAnimations(ft);
            if (addToBackStack) {
                ft.addToBackStack((String) null);
            }
        }
        ft.replace(this.mBase.mActionAreaId, fragment, "action");
        if ((fragment instanceof ActionFragment) && !((ActionFragment) fragment).hasListener()) {
            ((ActionFragment) fragment).setListener(this);
        }
        return ft;
    }

    static void addAnimations(FragmentTransaction ft) {
        ft.setCustomAnimations(R.anim.fragment_slide_left_in, R.anim.fragment_slide_left_out, R.anim.fragment_slide_right_in, R.anim.fragment_slide_right_out);
    }
}
