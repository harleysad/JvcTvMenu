package com.android.tv.twopanelsettings;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentProviderClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v17.preference.LeanbackListPreferenceDialogFragment;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import com.android.tv.twopanelsettings.slices.SlicePreference;
import com.android.tv.twopanelsettings.slices.SlicesConstants;
import java.util.Set;

public abstract class TwoPanelSettingsFragment extends Fragment implements PreferenceFragment.OnPreferenceStartFragmentCallback, PreferenceFragment.OnPreferenceStartScreenCallback, PreferenceFragment.OnPreferenceDisplayDialogCallback {
    private static final boolean DEBUG = false;
    private static final String EXTRA_PREF_PANEL_IDX = "com.android.tv.twopanelsettings.PREF_PANEL_IDX";
    private static final long PANEL_ANIMATION_DELAY_MS = 200;
    private static final long PANEL_ANIMATION_MS = 400;
    private static final String PREFERENCE_FRAGMENT_TAG = "com.android.tv.settings.TwoPanelSettingsFragment.PREFERENCE_FRAGMENT";
    private static final String PREVIEW_FRAGMENT_TAG = "com.android.tv.settings.TwoPanelSettingsFragment.PREVIEW_FRAGMENT";
    private static final String TAG = "TwoPanelSettingsFragment";
    private static final int[] frameResIds = {R.id.frame1, R.id.frame2, R.id.frame3, R.id.frame4, R.id.frame5, R.id.frame6, R.id.frame7, R.id.frame8, R.id.frame9, R.id.frame10};
    private static final int[] frameResOverlayIds = {R.id.frame1_overlay, R.id.frame2_overlay, R.id.frame3_overlay, R.id.frame4_overlay, R.id.frame5_overlay, R.id.frame6_overlay, R.id.frame7_overlay, R.id.frame8_overlay, R.id.frame9_overlay, R.id.frame10_overlay};
    private Handler mHandler;
    private boolean mIsNavigatingBack;
    private int mMaxScrollX;
    private OnChildViewHolderSelectedListener mOnChildViewHolderSelectedListener = new OnChildViewHolderSelectedListener() {
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
            if (child != null) {
                boolean unused = TwoPanelSettingsFragment.this.onPreferenceFocused(((PreferenceGroupAdapter) parent.getAdapter()).getItem(child.getAdapterPosition()));
            }
        }

        public void onChildViewHolderSelectedAndPositioned(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
        }
    };
    /* access modifiers changed from: private */
    public ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            TwoPanelSettingsFragment.this.getView().getViewTreeObserver().removeOnGlobalLayoutListener(TwoPanelSettingsFragment.this.mOnGlobalLayoutListener);
            TwoPanelSettingsFragment.this.moveToPanel(TwoPanelSettingsFragment.this.mPrefPanelIdx, false);
        }
    };
    /* access modifiers changed from: private */
    public int mPrefPanelIdx;
    private final RootViewOnKeyListener mRootViewOnKeyListener = new RootViewOnKeyListener();
    private HorizontalScrollView mScrollView;

    public interface SliceFragmentCallback {
        void onPreferenceFocused(Preference preference);
    }

    public abstract void onPreferenceStartInitialScreen();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.two_panel_settings_fragment, container, false);
        this.mScrollView = (HorizontalScrollView) v.findViewById(R.id.scrollview);
        this.mHandler = new Handler();
        if (savedInstanceState != null) {
            this.mPrefPanelIdx = savedInstanceState.getInt(EXTRA_PREF_PANEL_IDX, this.mPrefPanelIdx);
            v.getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
        }
        this.mMaxScrollX = computeMaxRightScroll();
        return v;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_PREF_PANEL_IDX, this.mPrefPanelIdx);
        super.onSaveInstanceState(outState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            onPreferenceStartInitialScreen();
        }
    }

    private boolean isLeanbackPreferenceFragment(String fragment) {
        try {
            return LeanbackPreferenceFragment.class.isAssignableFrom(Class.forName(fragment));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Fragment class not found.", e);
        }
    }

    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        if (pref.getFragment() == null) {
            return false;
        }
        Fragment preview = getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx + 1]);
        if (preview != null && !(preview instanceof DummyFragment)) {
            navigateToPreviewFragment();
        } else if (pref instanceof SlicePreference) {
            return false;
        } else {
            startImmersiveFragment(Fragment.instantiate(getActivity(), pref.getFragment(), pref.getExtras()));
        }
        return true;
    }

    public void navigateBack() {
        back(false);
    }

    public void navigateToPreviewFragment() {
        Fragment previewFragment = getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx + 1]);
        if (previewFragment != null && !(previewFragment instanceof DummyFragment)) {
            if (this.mPrefPanelIdx + 1 >= frameResIds.length) {
                Log.w(TAG, "Maximum level of depth reached.");
                return;
            }
            Fragment initialPreviewFragment = getInitialPreviewFragment(previewFragment);
            if (initialPreviewFragment == null) {
                initialPreviewFragment = new DummyFragment();
            }
            initialPreviewFragment.setExitTransition((Transition) null);
            this.mPrefPanelIdx++;
            addOrRemovePreferenceFocusedListener(getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx]), true);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(frameResIds[this.mPrefPanelIdx + 1], initialPreviewFragment, PREVIEW_FRAGMENT_TAG);
            transaction.commit();
            moveToPanel(this.mPrefPanelIdx, true);
            removeFragmentAndAddToBackStack(this.mPrefPanelIdx - 1);
        }
    }

    private void addOrRemovePreferenceFocusedListener(Fragment fragment, boolean isAddingListener) {
        VerticalGridView listView;
        if (fragment != null && (fragment instanceof LeanbackPreferenceFragment) && (listView = (VerticalGridView) ((LeanbackPreferenceFragment) fragment).getListView()) != null) {
            if (isAddingListener) {
                listView.setOnChildViewHolderSelectedListener(this.mOnChildViewHolderSelectedListener);
            } else {
                listView.setOnChildViewHolderSelectedListener((OnChildViewHolderSelectedListener) null);
            }
        }
    }

    public void startPreferenceFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(frameResIds[this.mPrefPanelIdx], fragment, PREFERENCE_FRAGMENT_TAG);
        transaction.commitNow();
        Fragment initialPreviewFragment = getInitialPreviewFragment(fragment);
        if (initialPreviewFragment == null) {
            initialPreviewFragment = new DummyFragment();
        }
        initialPreviewFragment.setExitTransition((Transition) null);
        FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
        transaction2.add(frameResIds[this.mPrefPanelIdx + 1], initialPreviewFragment, initialPreviewFragment.getClass().toString());
        transaction2.commit();
    }

    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragment caller, Preference pref) {
        if (caller != null) {
            Fragment preview = getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx + 1]);
            if (preview == null || (preview instanceof DummyFragment)) {
                return false;
            }
            this.mPrefPanelIdx++;
            moveToPanel(this.mPrefPanelIdx, true);
            removeFragmentAndAddToBackStack(this.mPrefPanelIdx - 1);
            return true;
        }
        throw new IllegalArgumentException("Cannot display dialog for preference " + pref + ", Caller must not be null!");
    }

    private boolean equalArguments(Bundle a, Bundle b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        Set<String> aks = a.keySet();
        Set<String> bks = b.keySet();
        if (a.size() != b.size() || !aks.containsAll(bks)) {
            return false;
        }
        for (String key : aks) {
            if (!(a.get(key) == null && b.get(key) == null) && (a.get(key) == null || b.get(key) == null || !a.get(key).equals(b.get(key)))) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean onPreferenceFocused(Preference pref) {
        Fragment prefFragment = getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx]);
        if (prefFragment instanceof SliceFragmentCallback) {
            ((SliceFragmentCallback) prefFragment).onPreferenceFocused(pref);
        }
        Fragment previewFragment = null;
        try {
            previewFragment = onCreatePreviewFragment(prefFragment, pref);
        } catch (Exception e) {
            Log.w(TAG, "Cannot instantiate the fragment from preference: " + pref, e);
        }
        if (previewFragment == null) {
            previewFragment = new DummyFragment();
        } else {
            previewFragment.setTargetFragment(prefFragment, 0);
        }
        Fragment existingPreviewFragment = getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx + 1]);
        if (existingPreviewFragment == null || !existingPreviewFragment.getClass().equals(previewFragment.getClass()) || !equalArguments(existingPreviewFragment.getArguments(), previewFragment.getArguments())) {
            if (existingPreviewFragment != null) {
                existingPreviewFragment.setExitTransition((Transition) null);
            }
            previewFragment.setEnterTransition(new Fade());
            previewFragment.setExitTransition((Transition) null);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(17498112, 17498113);
            transaction.replace(frameResIds[this.mPrefPanelIdx + 1], previewFragment);
            transaction.commit();
            getView().getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
            return true;
        }
        if (isRTL() && this.mScrollView.getScrollX() == 0 && this.mPrefPanelIdx == 0) {
            getView().getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isRTL() {
        return getResources().getConfiguration().getLayoutDirection() == 1;
    }

    public void onResume() {
        super.onResume();
        TwoPanelSettingsRootView rootView = (TwoPanelSettingsRootView) getView();
        if (rootView != null) {
            rootView.setOnBackKeyListener(this.mRootViewOnKeyListener);
        }
    }

    public void onPause() {
        super.onPause();
        TwoPanelSettingsRootView rootView = (TwoPanelSettingsRootView) getView();
        if (rootView != null) {
            rootView.setOnBackKeyListener((View.OnKeyListener) null);
        }
    }

    public void startImmersiveFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Fragment target = getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx]);
        fragment.setTargetFragment(target, 0);
        transaction.add(R.id.two_panel_fragment_container, fragment).remove(target).addToBackStack((String) null).commit();
    }

    public static class DummyFragment extends Fragment {
        @Nullable
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dummy_fragment, container, false);
        }
    }

    private class RootViewOnKeyListener implements View.OnKeyListener {
        private RootViewOnKeyListener() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean z = false;
            if (event.getAction() == 0 && (keyCode == 4 || ((!TwoPanelSettingsFragment.this.isRTL() && keyCode == 21) || (TwoPanelSettingsFragment.this.isRTL() && keyCode == 22)))) {
                TwoPanelSettingsFragment twoPanelSettingsFragment = TwoPanelSettingsFragment.this;
                if (keyCode == 4) {
                    z = true;
                }
                return twoPanelSettingsFragment.back(z);
            } else if (event.getAction() != 1 || ((TwoPanelSettingsFragment.this.isRTL() || keyCode != 22) && (!TwoPanelSettingsFragment.this.isRTL() || keyCode != 21))) {
                return false;
            } else {
                if (TwoPanelSettingsFragment.this.shouldPerformClick()) {
                    v.dispatchKeyEvent(new KeyEvent(0, 23));
                    v.dispatchKeyEvent(new KeyEvent(1, 23));
                } else {
                    TwoPanelSettingsFragment.this.navigateToPreviewFragment();
                }
                return true;
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldPerformClick() {
        Preference preference = getChosenPreference(getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx]));
        if (!(preference instanceof SlicePreference) || ((SlicePreference) preference).getSliceAction() == null || ((SlicePreference) preference).getUri() == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean back(final boolean isKeyBackPressed) {
        if (this.mIsNavigatingBack) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean unused = TwoPanelSettingsFragment.this.back(isKeyBackPressed);
                }
            }, PANEL_ANIMATION_MS);
            return true;
        } else if (getChildFragmentManager().findFragmentById(R.id.two_panel_fragment_container) != null) {
            getChildFragmentManager().popBackStack();
            moveToPanel(this.mPrefPanelIdx, false);
            return true;
        } else if (this.mPrefPanelIdx < 1) {
            if (isKeyBackPressed) {
                getActivity().finish();
            }
            return true;
        } else {
            this.mIsNavigatingBack = true;
            addOrRemovePreferenceFocusedListener(getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx]), false);
            getChildFragmentManager().popBackStack();
            this.mPrefPanelIdx--;
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    TwoPanelSettingsFragment.this.moveToPanel(TwoPanelSettingsFragment.this.mPrefPanelIdx, true);
                }
            }, PANEL_ANIMATION_DELAY_MS);
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    TwoPanelSettingsFragment.lambda$back$1(TwoPanelSettingsFragment.this);
                }
            }, PANEL_ANIMATION_MS);
            return true;
        }
    }

    public static /* synthetic */ void lambda$back$1(TwoPanelSettingsFragment twoPanelSettingsFragment) {
        twoPanelSettingsFragment.removeFragment(twoPanelSettingsFragment.mPrefPanelIdx + 2);
        twoPanelSettingsFragment.mIsNavigatingBack = false;
    }

    private void removeFragment(int index) {
        Fragment fragment = getChildFragmentManager().findFragmentById(frameResIds[index]);
        if (fragment != null) {
            getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void removeFragmentAndAddToBackStack(int index) {
        Fragment removePanel;
        if (index >= 0 && (removePanel = getChildFragmentManager().findFragmentById(frameResIds[index])) != null) {
            removePanel.setExitTransition(new Fade());
            FragmentTransaction remove = getChildFragmentManager().beginTransaction().remove(removePanel);
            remove.addToBackStack("remove " + removePanel.getClass().getName()).commit();
        }
    }

    private int computeMaxRightScroll() {
        int scrollViewWidth = getResources().getDimensionPixelSize(R.dimen.tp_settings_panes_width);
        int panelWidth = getResources().getDimensionPixelSize(R.dimen.tp_settings_preference_pane_width);
        int result = ((frameResIds.length * panelWidth) - scrollViewWidth) + getResources().getDimensionPixelSize(R.dimen.preference_pane_padding_end);
        if (result < 0) {
            return 0;
        }
        return result;
    }

    /* access modifiers changed from: private */
    public void moveToPanel(int index, boolean smoothScroll) {
        this.mHandler.post(new Runnable(index, smoothScroll) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoPanelSettingsFragment.lambda$moveToPanel$2(TwoPanelSettingsFragment.this, this.f$1, this.f$2);
            }
        });
    }

    public static /* synthetic */ void lambda$moveToPanel$2(TwoPanelSettingsFragment twoPanelSettingsFragment, int index, boolean smoothScroll) {
        View view;
        TwoPanelSettingsFragment twoPanelSettingsFragment2 = twoPanelSettingsFragment;
        if (twoPanelSettingsFragment.isAdded()) {
            int panelWidth = twoPanelSettingsFragment.getResources().getDimensionPixelSize(R.dimen.tp_settings_preference_pane_width);
            View scrollToPanelOverlay = twoPanelSettingsFragment.getView().findViewById(frameResOverlayIds[index]);
            View previewPanelOverlay = twoPanelSettingsFragment.getView().findViewById(frameResOverlayIds[index + 1]);
            int i = 0;
            boolean scrollsToPreview = !twoPanelSettingsFragment.isRTL() ? twoPanelSettingsFragment2.mScrollView.getScrollX() <= panelWidth * index : twoPanelSettingsFragment2.mScrollView.getScrollX() >= twoPanelSettingsFragment2.mMaxScrollX - (panelWidth * index);
            Fragment preview = twoPanelSettingsFragment.getChildFragmentManager().findFragmentById(frameResIds[index + 1]);
            boolean hasPreviewFragment = preview != null && !(preview instanceof DummyFragment);
            if (smoothScroll) {
                ObjectAnimator slideAnim = ObjectAnimator.ofInt(twoPanelSettingsFragment2.mScrollView, "scrollX", new int[]{twoPanelSettingsFragment2.mScrollView.getScrollX(), twoPanelSettingsFragment.isRTL() ? twoPanelSettingsFragment2.mMaxScrollX - (panelWidth * index) : panelWidth * index});
                slideAnim.setAutoCancel(true);
                slideAnim.setDuration(PANEL_ANIMATION_MS);
                slideAnim.start();
                if (scrollsToPreview) {
                    previewPanelOverlay.setAlpha(hasPreviewFragment ? 1.0f : 0.0f);
                    ObjectAnimator colorAnim = ObjectAnimator.ofFloat(scrollToPanelOverlay, "alpha", new float[]{scrollToPanelOverlay.getAlpha(), 0.0f});
                    colorAnim.setAutoCancel(true);
                    colorAnim.setDuration(PANEL_ANIMATION_MS);
                    colorAnim.start();
                } else {
                    scrollToPanelOverlay.setAlpha(0.0f);
                    float[] fArr = new float[2];
                    fArr[0] = previewPanelOverlay.getAlpha();
                    fArr[1] = hasPreviewFragment ? 1.0f : 0.0f;
                    ObjectAnimator colorAnim2 = ObjectAnimator.ofFloat(previewPanelOverlay, "alpha", fArr);
                    colorAnim2.setAutoCancel(true);
                    colorAnim2.setDuration(PANEL_ANIMATION_MS);
                    colorAnim2.start();
                }
            } else {
                twoPanelSettingsFragment2.mScrollView.scrollTo(twoPanelSettingsFragment.isRTL() ? twoPanelSettingsFragment2.mMaxScrollX - (panelWidth * index) : panelWidth * index, 0);
                float f = 0.0f;
                scrollToPanelOverlay.setAlpha(0.0f);
                if (hasPreviewFragment) {
                    f = 1.0f;
                }
                previewPanelOverlay.setAlpha(f);
            }
            Fragment fragment = twoPanelSettingsFragment.getChildFragmentManager().findFragmentById(frameResIds[index]);
            if (fragment != null && fragment.getView() != null) {
                fragment.getView().requestFocus();
                int[] iArr = frameResIds;
                int length = iArr.length;
                while (i < length) {
                    Fragment f2 = twoPanelSettingsFragment.getChildFragmentManager().findFragmentById(iArr[i]);
                    if (!(f2 == null || (view = f2.getView()) == null)) {
                        view.setImportantForAccessibility(f2 == fragment ? 1 : 4);
                    }
                    i++;
                    TwoPanelSettingsFragment twoPanelSettingsFragment3 = twoPanelSettingsFragment;
                }
            }
        }
    }

    private Fragment getInitialPreviewFragment(Fragment fragment) {
        if (!(fragment instanceof LeanbackPreferenceFragment)) {
            return null;
        }
        LeanbackPreferenceFragment leanbackPreferenceFragment = (LeanbackPreferenceFragment) fragment;
        if (leanbackPreferenceFragment.getListView() == null) {
            return null;
        }
        VerticalGridView listView = (VerticalGridView) leanbackPreferenceFragment.getListView();
        int position = listView.getSelectedPosition();
        PreferenceGroupAdapter adapter = (PreferenceGroupAdapter) leanbackPreferenceFragment.getListView().getAdapter();
        Preference chosenPreference = adapter.getItem(position);
        if (chosenPreference == null || (listView.findViewHolderForPosition(position) != null && !listView.findViewHolderForPosition(position).itemView.hasFocusable())) {
            chosenPreference = null;
            int i = 0;
            while (true) {
                if (i >= listView.getChildCount()) {
                    break;
                }
                View view = listView.getChildAt(i);
                if (view.hasFocusable()) {
                    chosenPreference = adapter.getItem(((PreferenceViewHolder) listView.getChildViewHolder(view)).getAdapterPosition());
                    break;
                }
                i++;
            }
        }
        if (chosenPreference == null) {
            return null;
        }
        return onCreatePreviewFragment(fragment, chosenPreference);
    }

    private Preference getChosenPreference(Fragment fragment) {
        if (!(fragment instanceof LeanbackPreferenceFragment)) {
            return null;
        }
        LeanbackPreferenceFragment leanbackPreferenceFragment = (LeanbackPreferenceFragment) fragment;
        if (leanbackPreferenceFragment.getListView() == null) {
            return null;
        }
        return ((PreferenceGroupAdapter) leanbackPreferenceFragment.getListView().getAdapter()).getItem(((VerticalGridView) leanbackPreferenceFragment.getListView()).getSelectedPosition());
    }

    public Fragment onCreatePreviewFragment(Fragment caller, Preference preference) {
        if (preference.getFragment() == null) {
            Fragment f = null;
            if (preference instanceof ListPreference) {
                f = TwoPanelListPreferenceDialogFragment.newInstanceSingle(preference.getKey());
            } else if (preference instanceof MultiSelectListPreference) {
                f = LeanbackListPreferenceDialogFragment.newInstanceMulti(preference.getKey());
            }
            if (!(f == null || caller == null)) {
                f.setTargetFragment(caller, 0);
            }
            return f;
        } else if (!isLeanbackPreferenceFragment(preference.getFragment())) {
            return null;
        } else {
            if (preference instanceof SlicePreference) {
                SlicePreference slicePref = (SlicePreference) preference;
                if (slicePref.getUri() == null || !isUriValid(slicePref.getUri())) {
                    return null;
                }
                Bundle b = preference.getExtras();
                b.putString("TAG_TARGET_URI", slicePref.getUri());
                b.putCharSequence(SlicesConstants.TAG_SCREEN_TITLE, slicePref.getTitle());
            }
            return Fragment.instantiate(getActivity(), preference.getFragment(), preference.getExtras());
        }
    }

    private boolean isUriValid(String uri) {
        ContentProviderClient client;
        if (uri == null || (client = getContext().getContentResolver().acquireContentProviderClient(Uri.parse(uri))) == null) {
            return false;
        }
        client.close();
        return true;
    }

    public void addListenerForFragment(Fragment fragment) {
        if (isFragmentInTheMainPanel(fragment)) {
            addOrRemovePreferenceFocusedListener(fragment, true);
        }
    }

    public void removeListenerForFragment(Fragment fragment) {
        addOrRemovePreferenceFocusedListener(fragment, false);
    }

    public boolean isFragmentInTheMainPanel(Fragment fragment) {
        return fragment == getChildFragmentManager().findFragmentById(frameResIds[this.mPrefPanelIdx]);
    }
}
