package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.view.KeyEvent;
import com.mediatek.twoworlds.tv.MtkTvPipPop;
import com.mediatek.twoworlds.tv.model.MtkTvPipPopFucusInfoBase;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.FocusLabel;
import com.mediatek.wwtv.tvcenter.nav.view.MiscView;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.TwinkleView;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class FocusLabelControl extends NavBasicMisc implements ComponentStatusListener.ICStatusListener {
    private static final int AUDIO_CHANGE = 4;
    private static final int ENTER_NORMAL_STATE = 2;
    private static final int ENTER_PIP_STATE = 0;
    private static final int ENTER_POP_STATE = 1;
    private static final int FOCUS_CHANGE = 5;
    private final String TAG = "FocusLabelControl";
    private BannerView mBannerView;
    private ChannelListDialog mChannelListDialog;
    private final CommonIntegration mCommonIntegration;
    private final ComponentsManager mComponentsManager;
    private final KeyDispatch mDispatch;
    private FavoriteListDialog mFavoriteChannelListView;
    private FocusLabel mFocusLabel;
    private final IntegrationZoom mIntegrationZoom;
    private MiscView mMiscView;
    private final MtkTvPipPop mMtkTvPipPop;
    private MtkTvPipPopFucusInfoBase mMtkTvPipPopFucusInfo;
    private SundryShowTextView mShowTextView;
    private SourceListView mSourceListView;
    private final SundryImplement mSundryImplement;
    private TwinkleView mTwinkleView;
    private ZoomTipView mZoomTipView;

    public FocusLabelControl(Context mContext) {
        super(mContext);
        this.componentID = NavBasic.NAV_COMP_ID_POP;
        this.mDispatch = KeyDispatch.getInstance();
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.mComponentsManager = ComponentsManager.getInstance();
        this.mMtkTvPipPop = MtkTvPipPop.getInstance();
        this.mIntegrationZoom = IntegrationZoom.getInstance(mContext);
        this.mSundryImplement = SundryImplement.getInstanceNavSundryImplement(mContext);
        initComponentsView();
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(8, this);
    }

    private void initComponentsView() {
        this.mChannelListDialog = (ChannelListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        this.mFavoriteChannelListView = (FavoriteListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST);
        this.mSourceListView = (SourceListView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
        this.mShowTextView = (SundryShowTextView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
        this.mTwinkleView = (TwinkleView) this.mComponentsManager.getComponentById(16777232);
        this.mBannerView = (BannerView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_BANNER);
        this.mZoomTipView = (ZoomTipView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
        this.mMiscView = (MiscView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_MISC);
    }

    public void setVisibility(int visibility) {
        if (visibility == 0) {
            if (this.mFocusLabel.getVisibility() != 0) {
                MtkLog.d("FocusLabelControl", "come in FocusLabelControl ComponentStatusListener.NAV_ENTER_STANDBY  mFocusLabel.show()");
                if (this.mCommonIntegration.isPipOrPopState()) {
                    this.mFocusLabel.show();
                }
            }
        } else if (this.mFocusLabel.getVisibility() == 0) {
            MtkLog.d("FocusLabelControl", "come in FocusLabelControl ComponentStatusListener.NAV_ENTER_STANDBY mFocusLabel.release()");
            this.mFocusLabel.release();
        }
        super.setVisibility(visibility);
    }

    public boolean isCoExist(int componentID) {
        return true;
    }

    public boolean isKeyHandler(int keyCode) {
        if (keyCode != 171) {
            return false;
        }
        MtkLog.d("FocusLabelControl", "isKeyHandler, KEYCODE_MTKIR_PIPPOP");
        hideComponentsWithPIPKey();
        if ((ComponentsManager.getNativeActiveCompId() & NavBasic.NAV_NATIVE_COMP_ID_BASIC) != 0) {
            MtkLog.d("FocusLabelControl", "isKeyHandler, key already be passed to linux world(ginga)");
        } else {
            this.mDispatch.passKeyToNative(keyCode, (KeyEvent) null);
            if (1 != this.mIntegrationZoom.getCurrentZoom()) {
                this.mIntegrationZoom.setZoomModeToNormal();
            }
        }
        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
        return true;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d("FocusLabelControl", "KeyHandler keyCode = " + keyCode);
        if (keyCode != 171) {
            if (keyCode != 227) {
                if (keyCode != 10062) {
                    if (keyCode != 10065) {
                        switch (keyCode) {
                            case 21:
                            case 22:
                                if (!this.mCommonIntegration.isPipOrPopState() || notSupportChangeFocus() || reShowFocus()) {
                                    return true;
                                }
                                hideComponentsWithLeftKey();
                                MtkLog.d("FocusLabelControl", "come in FocusLabelControl KeyHandler passKeyToNative KEYCODE_DPAD_LEFT");
                                if (SundryImplement.getInstanceNavSundryImplement(this.mContext).isFreeze()) {
                                    SundryImplement.getInstanceNavSundryImplement(this.mContext).setFreeze(false);
                                }
                                this.mDispatch.passKeyToNative(keyCode, event);
                                ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                                return true;
                            default:
                                if (keyCode != 26) {
                                    reShowFocus();
                                }
                                return false;
                        }
                    }
                } else if (!this.mCommonIntegration.isPipOrPopState() || reShowFocus()) {
                    return true;
                } else {
                    MtkLog.d("FocusLabelControl", "come in FocusLabelControl KeyHandler passKeyToNative" + keyCode);
                    this.mDispatch.passKeyToNative(keyCode, event);
                    ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                    return true;
                }
            }
            if (!this.mCommonIntegration.isPipOrPopState() || reShowFocus()) {
                return true;
            }
            hideComponentsWithSizeAndPosition(keyCode);
            if (this.mSundryImplement.isFreeze() && keyCode == 10065) {
                this.mSundryImplement.setFreeze(false);
            }
            MtkLog.d("FocusLabelControl", "come in FocusLabelControl KeyHandler passKeyToNative" + keyCode);
            this.mDispatch.passKeyToNative(keyCode, event);
            return true;
        }
        if (this.mCommonIntegration.isPipOrPopState()) {
            if (reShowFocus()) {
                return true;
            }
            hideComponentsWithPIPKey();
            MtkLog.d("FocusLabelControl", "come in FocusLabelControl KeyHandler passKeyToNative KEYCODE_MTKIR_PIPPOP");
            this.mDispatch.passKeyToNative(keyCode, event);
        }
        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
        return true;
    }

    public boolean deinitView() {
        return false;
    }

    public boolean isVisible() {
        MtkLog.d("FocusLabelControl", "come in FocusLabelControl isVisible mCommonIntegration.isPipOrPopState() " + this.mCommonIntegration.isPipOrPopState());
        if (this.mCommonIntegration.isPipOrPopState()) {
            return true;
        }
        return false;
    }

    private void hideComponentsWithPIPKey() {
        if (this.mChannelListDialog.isShowing()) {
            this.mChannelListDialog.dismiss();
        }
        if (this.mFavoriteChannelListView.isShowing()) {
            this.mFavoriteChannelListView.dismiss();
        }
        if (this.mSourceListView.isShowing()) {
            this.mSourceListView.dismiss();
        }
        if (this.mShowTextView.isVisible()) {
            this.mShowTextView.setVisibility(8);
        }
        if (this.mTwinkleView.isVisible()) {
            this.mTwinkleView.setVisibility(8);
        }
        if (this.mZoomTipView.isVisible()) {
            this.mZoomTipView.setVisibility(8);
        }
        if (this.mMiscView.isVisible()) {
            this.mMiscView.setVisibility(8);
        }
    }

    private void hideComponentsWithLeftKey() {
        if (this.mTwinkleView.isVisible()) {
            this.mTwinkleView.setVisibility(8);
        }
        if (this.mShowTextView.isVisible()) {
            this.mShowTextView.setVisibility(8);
        }
        if (this.mMiscView.isVisible()) {
            this.mMiscView.setVisibility(8);
        }
    }

    private void hideComponentsWithSizeAndPosition(int key) {
        if (this.mShowTextView.isVisible()) {
            this.mShowTextView.setVisibility(8);
        }
        if (this.mChannelListDialog.isShowing()) {
            this.mChannelListDialog.dismiss();
        }
        if (this.mMiscView.isVisible()) {
            this.mMiscView.setVisibility(8);
        }
    }

    private boolean notSupportChangeFocus() {
        if (this.mChannelListDialog.isShowing() || this.mFavoriteChannelListView.isShowing() || this.mSourceListView.isShowing() || this.mBannerView.isChangingChannelWithNum()) {
            return true;
        }
        return false;
    }

    public boolean reShowFocus() {
        if (this.mFocusLabel == null || this.mFocusLabel.getVisibility() == 0) {
            return false;
        }
        this.mComponentsManager.showNavComponent(NavBasic.NAV_COMP_ID_POP);
        return true;
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID != 3 && statusID == 8) {
            MtkLog.d("FocusLabelControl", "come in FocusLabelControl ComponentStatusListener.NAV_ENTER_STANDBY");
            setVisibility(4);
        }
    }

    public void setFocusLable(FocusLabel focusLabel) {
        this.mFocusLabel = focusLabel;
    }
}
