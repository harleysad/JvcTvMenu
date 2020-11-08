package com.android.tv.menu;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.media.tv.TvView;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.android.tv.common.util.DurationTimer;
import com.android.tv.menu.MenuRowFactory;
import com.android.tv.ui.hideable.AutoHideScheduler;
import com.android.tv.util.ViewCache;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu implements AccessibilityManager.AccessibilityStateChangeListener {
    private static final Map<Integer, Integer> PRELOAD_VIEW_IDS = new HashMap();
    public static final int REASON_GUIDE = 1;
    public static final int REASON_LAUNCH_TV_OPTIONS = 9;
    public static final int REASON_LAUNCH_TV_RECORD = 10;
    public static final int REASON_NONE = 0;
    public static final int REASON_PLAY_CONTROLS_FAST_FORWARD = 6;
    public static final int REASON_PLAY_CONTROLS_JUMP_TO_NEXT = 8;
    public static final int REASON_PLAY_CONTROLS_JUMP_TO_PREVIOUS = 7;
    public static final int REASON_PLAY_CONTROLS_PAUSE = 3;
    public static final int REASON_PLAY_CONTROLS_PLAY = 2;
    public static final int REASON_PLAY_CONTROLS_PLAY_PAUSE = 4;
    public static final int REASON_PLAY_CONTROLS_REWIND = 5;
    private static final String TAG = "Menu";
    private static final List<String> sRowIdListForReason = new ArrayList();
    private boolean mAnimationDisabledForTest;
    private final AutoHideScheduler mAutoHideScheduler;
    private final Context mContext;
    private final Animator mHideAnimator;
    private boolean mKeepVisible;
    private final List<MenuRow> mMenuRows = new ArrayList();
    private final IMenuView mMenuView;
    private OnAutoHideListener mOnAutoHideListener;
    private final OnMenuVisibilityChangeListener mOnMenuVisibilityChangeListener;
    private final Animator mShowAnimator;
    private final long mShowDurationMillis;
    private final DurationTimer mVisibleTimer = new DurationTimer();

    @Retention(RetentionPolicy.SOURCE)
    public @interface MenuShowReason {
    }

    public interface OnAutoHideListener {
        void onAutoHide();
    }

    public static abstract class OnMenuVisibilityChangeListener {
        public abstract void onMenuVisibilityChange(boolean z);
    }

    static {
        PRELOAD_VIEW_IDS.put(Integer.valueOf(R.layout.menu_card_guide), 1);
        PRELOAD_VIEW_IDS.put(Integer.valueOf(R.layout.menu_card_setup), 1);
        PRELOAD_VIEW_IDS.put(Integer.valueOf(R.layout.menu_card_app_link), 1);
        PRELOAD_VIEW_IDS.put(Integer.valueOf(R.layout.menu_card_channel), 10);
        PRELOAD_VIEW_IDS.put(Integer.valueOf(R.layout.menu_card_action), 7);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add(ChannelsRow.ID);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add((Object) null);
        sRowIdListForReason.add(MenuRowFactory.TvOptionsRow.ID);
        sRowIdListForReason.add(MenuRowFactory.RecordRow.ID);
    }

    public Menu(Context context, TvView tvView, MenuOptionMain optionsManager, IMenuView menuView, MenuRowFactory menuRowFactory, OnMenuVisibilityChangeListener onMenuVisibilityChangeListener, OnAutoHideListener onAutoHideListener) {
        this.mContext = context;
        this.mMenuView = menuView;
        this.mShowDurationMillis = (long) context.getResources().getInteger(R.integer.menu_show_duration);
        this.mOnMenuVisibilityChangeListener = onMenuVisibilityChangeListener;
        this.mShowAnimator = AnimatorInflater.loadAnimator(context, R.animator.menu_enter);
        this.mShowAnimator.setTarget(this.mMenuView);
        this.mHideAnimator = AnimatorInflater.loadAnimator(context, R.animator.menu_exit);
        this.mHideAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Menu.this.hideInternal();
            }
        });
        this.mHideAnimator.setTarget(this.mMenuView);
        addMenuRow(menuRowFactory.createMenuRow(this, ChannelsRow.class));
        addMenuRow(menuRowFactory.createMenuRow(this, MenuRowFactory.TvOptionsRow.class));
        if (MarketRegionInfo.isFunctionSupport(43) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportPvr()) {
            addMenuRow(menuRowFactory.createMenuRow(this, MenuRowFactory.RecordRow.class));
        } else if (MarketRegionInfo.isFunctionSupport(36)) {
            addMenuRow(menuRowFactory.createMenuRow(this, MenuRowFactory.RecordRow.class));
        }
        this.mMenuView.setMenuRows(this.mMenuRows);
        this.mOnAutoHideListener = onAutoHideListener;
        this.mAutoHideScheduler = new AutoHideScheduler(context, new Runnable() {
            public final void run() {
                Menu.this.autoHide();
            }
        });
    }

    /* access modifiers changed from: private */
    public void autoHide() {
        if (this.mOnAutoHideListener != null) {
            this.mOnAutoHideListener.onAutoHide();
        }
    }

    public void preloadItemViews() {
        HorizontalGridView fakeParent = new HorizontalGridView(this.mContext);
        for (Integer intValue : PRELOAD_VIEW_IDS.keySet()) {
            int id = intValue.intValue();
            ViewCache.getInstance().putView(this.mContext, id, fakeParent, PRELOAD_VIEW_IDS.get(Integer.valueOf(id)).intValue());
        }
    }

    public boolean update() {
        Log.d(TAG, "update main menu");
        return this.mMenuView.update(isActive());
    }

    public boolean update(String rowId) {
        Log.d(TAG, "update main menu");
        return this.mMenuView.update(rowId, isActive());
    }

    public void updateLanguage() {
        this.mMenuView.updateLanguage();
    }

    public void onRecentChannelsChanged() {
        Log.d(TAG, "onRecentChannelsChanged");
        for (MenuRow row : this.mMenuRows) {
            row.onRecentChannelsChanged();
        }
    }

    public void onStreamInfoChanged() {
        Log.d(TAG, "update options row in main menu");
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        this.mAutoHideScheduler.onAccessibilityStateChanged(enabled);
    }

    public boolean isActive() {
        return this.mMenuView.isVisible() && !this.mHideAnimator.isStarted();
    }

    public void show(int reason) {
        $$Lambda$Menu$XSt3rfb_Z8Xn9Q0xPxyVZa_HcU r2;
        Log.d(TAG, "menu show reason:" + reason);
        this.mVisibleTimer.start();
        if (this.mHideAnimator.isStarted()) {
            this.mHideAnimator.end();
        }
        if (this.mOnMenuVisibilityChangeListener != null) {
            this.mOnMenuVisibilityChangeListener.onMenuVisibilityChange(true);
        }
        String rowIdToSelect = sRowIdListForReason.get(reason);
        IMenuView iMenuView = this.mMenuView;
        if (this.mAnimationDisabledForTest) {
            r2 = null;
        } else {
            r2 = new Runnable() {
                public final void run() {
                    Menu.lambda$show$1(Menu.this);
                }
            };
        }
        iMenuView.onShow(reason, rowIdToSelect, r2);
        scheduleHide();
    }

    public static /* synthetic */ void lambda$show$1(Menu menu) {
        if (menu.isActive()) {
            menu.mShowAnimator.start();
        }
    }

    public void hide(boolean withAnimation) {
        Log.d(TAG, "menu hide :" + withAnimation);
        if (this.mShowAnimator.isStarted()) {
            this.mShowAnimator.cancel();
        }
        if (isActive()) {
            if (this.mAnimationDisabledForTest) {
                withAnimation = false;
            }
            this.mAutoHideScheduler.cancel();
            if (withAnimation) {
                if (!this.mHideAnimator.isStarted()) {
                    this.mHideAnimator.start();
                }
            } else if (this.mHideAnimator.isStarted()) {
                this.mHideAnimator.end();
            } else {
                hideInternal();
            }
        }
    }

    private void addMenuRow(MenuRow row) {
        if (row != null) {
            this.mMenuRows.add(row);
        }
    }

    public void release() {
        for (MenuRow row : this.mMenuRows) {
            row.release();
        }
        this.mAutoHideScheduler.cancel();
    }

    public void scheduleHide() {
        this.mAutoHideScheduler.schedule(this.mShowDurationMillis);
    }

    /* access modifiers changed from: private */
    public void hideInternal() {
        Log.d(TAG, "menu hideInternal");
        this.mMenuView.onHide();
        if (this.mOnMenuVisibilityChangeListener != null) {
            this.mOnMenuVisibilityChangeListener.onMenuVisibilityChange(false);
        }
    }

    public void setKeepVisible(boolean keepVisible) {
        this.mKeepVisible = keepVisible;
        if (this.mKeepVisible) {
            this.mAutoHideScheduler.cancel();
        } else if (isActive()) {
            scheduleHide();
        }
    }
}
