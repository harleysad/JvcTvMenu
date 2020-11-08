package com.mediatek.wwtv.setting.widget.detailui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import java.util.ArrayList;

public abstract class DialogActivity extends Activity implements ActionAdapter.Listener, FragmentManager.OnBackStackChangedListener {
    protected static final int ANIMATE_DELAY = 550;
    protected static final int ANIMATE_IN_DURATION = 250;
    public static final String BACKSTACK_NAME_DIALOG = "backstack_name_dialog";
    public static final String EXTRA_CREATE_FRAGMENT_FROM_EXTRA = "create_fragment_from_extra";
    public static final String EXTRA_DIALOG_ACTIONS_START_INDEX = "dialog_actions_start_index";
    public static final String EXTRA_DIALOG_BREADCRUMB = "dialog_breadcrumb";
    public static final String EXTRA_DIALOG_DESCRIPTION = "dialog_description";
    public static final String EXTRA_DIALOG_IMAGE_BACKGROUND_COLOR = "dialog_image_background_color";
    public static final String EXTRA_DIALOG_IMAGE_URI = "dialog_image_uri";
    public static final String EXTRA_DIALOG_TITLE = "dialog_title";
    public static final String EXTRA_PARCELABLE_ACTIONS = "parcelable_actions";
    public static final String KEY_BACKSTACK_COUNT = "backstack_count";
    protected static final int SLIDE_IN_DISTANCE = 120;
    protected static final int SLIDE_IN_STAGGER = 100;
    public static final String TAG_DIALOG = "tag_dialog";
    private View mContent;
    private DialogFragment mDialogFragment = new DialogFragment();
    private int mLastBackStackCount = 0;
    private int mLayoutResId = R.layout.two_pane_dialog_frame;
    ProgressBar mWaiting;

    public DialogActivity() {
        this.mDialogFragment.setActivity(this);
    }

    public static Intent createIntent(Context context, String title, String breadcrumb, String description, String imageUri, ArrayList<Action> actions) {
        return createIntent(context, title, breadcrumb, description, imageUri, 0, actions);
    }

    public static Intent createIntent(Context context, String title, String breadcrumb, String description, String imageUri, int imageBackground, ArrayList<Action> actions) {
        Intent intent = new Intent(context, DialogActivity.class);
        intent.putExtra(EXTRA_DIALOG_TITLE, title);
        intent.putExtra(EXTRA_DIALOG_BREADCRUMB, breadcrumb);
        intent.putExtra(EXTRA_DIALOG_DESCRIPTION, description);
        intent.putExtra(EXTRA_DIALOG_IMAGE_URI, imageUri);
        intent.putExtra(EXTRA_DIALOG_IMAGE_BACKGROUND_COLOR, imageBackground);
        intent.putParcelableArrayListExtra(EXTRA_PARCELABLE_ACTIONS, actions);
        return intent;
    }

    public static Intent createIntent(Context context, String title, String breadcrumb, String description, String imageUri, ArrayList<Action> actions, Class<? extends DialogActivity> activityClass) {
        return createIntent(context, title, breadcrumb, description, imageUri, 0, actions, activityClass);
    }

    public static Intent createIntent(Context context, String title, String breadcrumb, String description, String imageUri, int imageBackground, ArrayList<Action> actions, Class<? extends DialogActivity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(EXTRA_DIALOG_TITLE, title);
        intent.putExtra(EXTRA_DIALOG_BREADCRUMB, breadcrumb);
        intent.putExtra(EXTRA_DIALOG_DESCRIPTION, description);
        intent.putExtra(EXTRA_DIALOG_IMAGE_URI, imageUri);
        intent.putExtra(EXTRA_DIALOG_IMAGE_BACKGROUND_COLOR, imageBackground);
        intent.putParcelableArrayListExtra(EXTRA_PARCELABLE_ACTIONS, actions);
        return intent;
    }

    public static Intent createIntent(Context context, String title, String breadcrumb, String description, String imageUri, int imageBackground, ArrayList<Action> actions, Class<? extends DialogActivity> activityClass, int startIndex) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(EXTRA_DIALOG_TITLE, title);
        intent.putExtra(EXTRA_DIALOG_BREADCRUMB, breadcrumb);
        intent.putExtra(EXTRA_DIALOG_DESCRIPTION, description);
        intent.putExtra(EXTRA_DIALOG_IMAGE_URI, imageUri);
        intent.putExtra(EXTRA_DIALOG_IMAGE_BACKGROUND_COLOR, imageBackground);
        intent.putParcelableArrayListExtra(EXTRA_PARCELABLE_ACTIONS, actions);
        intent.putExtra(EXTRA_DIALOG_ACTIONS_START_INDEX, startIndex);
        return intent;
    }

    public View getContentView() {
        return this.mContent;
    }

    public ProgressBar getWaitingBar() {
        return this.mWaiting;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 18) {
            getWindow().addFlags(NavBasic.NAV_NATIVE_COMP_ID_BASIC);
        }
        if (savedInstanceState != null) {
            this.mLastBackStackCount = savedInstanceState.getInt(KEY_BACKSTACK_COUNT);
        }
        super.onCreate(savedInstanceState);
        getFragmentManager().addOnBackStackChangedListener(this);
        LayoutInflater helium = (LayoutInflater) getSystemService("layout_inflater");
        this.mContent = helium.inflate(this.mLayoutResId, (ViewGroup) null);
        setContentView(this.mContent);
        if (this.mLayoutResId == R.layout.two_pane_dialog_frame) {
            helium.inflate(R.layout.dialog_container, (ViewGroup) this.mContent);
            setDialogFragment(this.mDialogFragment);
            this.mWaiting = (ProgressBar) this.mContent.findViewById(R.id.waiting);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean(EXTRA_CREATE_FRAGMENT_FROM_EXTRA)) {
            String title = bundle.getString(EXTRA_DIALOG_TITLE);
            String breadcrumb = bundle.getString(EXTRA_DIALOG_BREADCRUMB);
            String description = bundle.getString(EXTRA_DIALOG_DESCRIPTION);
            Uri imageUri = Uri.parse(bundle.getString(EXTRA_DIALOG_IMAGE_URI));
            int backgroundColor = bundle.getInt(EXTRA_DIALOG_IMAGE_BACKGROUND_COLOR);
            ArrayList<Action> actions = bundle.getParcelableArrayList(EXTRA_PARCELABLE_ACTIONS);
            setContentFragment(ContentFragment.newInstance(title, breadcrumb, description, imageUri, backgroundColor));
            setActionFragment(ActionFragment.newInstance(actions));
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_BACKSTACK_COUNT, this.mLastBackStackCount);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.mLayoutResId == R.layout.two_pane_dialog_frame) {
            getDialogFragment().performEntryTransition();
        }
    }

    public void onBackStackChanged() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count > 0 && count < this.mLastBackStackCount && BACKSTACK_NAME_DIALOG.equals(getFragmentManager().getBackStackEntryAt(count - 1).getName())) {
            getFragmentManager().popBackStack();
        }
        this.mLastBackStackCount = count;
    }

    public void onActionClicked(Action action) {
        Intent intent = action.getIntent();
        if (intent != null) {
            startActivity(intent);
        }
    }

    /* access modifiers changed from: protected */
    public void disableEntryAnimation() {
        getDialogFragment().disableEntryAnimation();
    }

    /* access modifiers changed from: protected */
    public void setLayoutProperties(int layoutResId, int contentAreaId, int actionAreaId) {
        this.mLayoutResId = layoutResId;
        getDialogFragment().setLayoutProperties(contentAreaId, actionAreaId);
    }

    /* access modifiers changed from: protected */
    public void prepareAndAnimateView(View v, float initAlpha, float initTransX, int delay, int duration, Interpolator interpolator, boolean isIcon) {
        getDialogFragment().prepareAndAnimateView(v, initAlpha, initTransX, delay, duration, interpolator, isIcon);
    }

    /* access modifiers changed from: protected */
    public void onIntroAnimationFinished() {
        getDialogFragment().onIntroAnimationFinished();
    }

    /* access modifiers changed from: protected */
    public boolean isIntroAnimationInProgress() {
        return getDialogFragment().isIntroAnimationInProgress();
    }

    /* access modifiers changed from: protected */
    public ColorDrawable getBackgroundDrawable() {
        return getDialogFragment().getBackgroundDrawable();
    }

    /* access modifiers changed from: protected */
    public void setBackgroundDrawable(ColorDrawable drawable) {
        getDialogFragment().setBackgroundDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public void setContentFragment(Fragment fragment) {
        getDialogFragment().setContentFragment(fragment);
    }

    /* access modifiers changed from: protected */
    public void setActionFragment(Fragment fragment) {
        getDialogFragment().setActionFragment(fragment);
    }

    /* access modifiers changed from: protected */
    public void setActionFragment(Fragment fragment, boolean addToBackStack) {
        getDialogFragment().setActionFragment(fragment, addToBackStack);
    }

    /* access modifiers changed from: protected */
    public Fragment getActionFragment() {
        return getDialogFragment().getActionFragment();
    }

    /* access modifiers changed from: protected */
    public Fragment getContentFragment() {
        return getDialogFragment().getContentFragment();
    }

    /* access modifiers changed from: protected */
    public void setContentAndActionFragments(Fragment contentFragment, Fragment actionFragment) {
        getDialogFragment().setContentAndActionFragments(contentFragment, actionFragment);
    }

    /* access modifiers changed from: protected */
    public void setContentAndActionFragments(Fragment contentFragment, Fragment actionFragment, boolean addToBackStack) {
        getDialogFragment().setContentAndActionFragments(contentFragment, actionFragment, addToBackStack);
    }

    /* access modifiers changed from: protected */
    public void setDialogFragment(DialogFragment fragment) {
        setDialogFragment(fragment, true);
    }

    /* access modifiers changed from: protected */
    public void setDialogFragment(DialogFragment fragment, boolean addToBackStack) {
        this.mDialogFragment = fragment;
        fragment.setActivity(this);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if ((fm.findFragmentByTag(TAG_DIALOG) != null) && addToBackStack) {
            ft.addToBackStack(BACKSTACK_NAME_DIALOG);
        }
        ft.replace(R.id.dialog_fragment, fragment, TAG_DIALOG);
        ft.commit();
    }

    /* access modifiers changed from: protected */
    public DialogFragment getDialogFragment() {
        DialogFragment fragment;
        FragmentManager fm = getFragmentManager();
        if (!(fm == null || (fragment = (DialogFragment) fm.findFragmentByTag(TAG_DIALOG)) == null)) {
            this.mDialogFragment = fragment;
        }
        return this.mDialogFragment;
    }
}
