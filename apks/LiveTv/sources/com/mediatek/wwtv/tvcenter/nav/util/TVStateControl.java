package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvPipPop;
import com.mediatek.twoworlds.tv.model.MtkTvPipPopFucusInfoBase;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
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
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class TVStateControl extends NavBasicMisc implements ComponentStatusListener.ICStatusListener {
    private static final int AUDIO_CHANGE = 4;
    private static final int ENTER_NORMAL_STATE = 2;
    private static final int ENTER_PIP_STATE = 0;
    private static final int ENTER_POP_STATE = 1;
    private static final int FOCUS_CHANGE = 5;
    private static final int PIPPOS_KEY_VALUE = 311;
    private static final int PIPSIZE_KYE_VALUE = 312;
    private static final int SWAP_CHANEGE = 3;
    private static final int SWAP_KEY_VALUE = 315;
    private static PIPPOPSurfaceViewControl mViewControl;
    private final String TAG = "TVStateControl";
    private boolean bindSubSource = false;
    private String currentFocusWin;
    private int focusIconMarginX;
    private int focusIconMarginY;
    private String focusSourceName = "";
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
    private final InputSourceManager mSourceManager;
    private final SundryImplement mSundryImplement;
    private TwinkleView mTwinkleView;
    private ZoomTipView mZoomTipView;
    private String mainSourceName = "";
    private String subSourceName = "";

    public void setOutputView(TvSurfaceView mainOutput, TvSurfaceView subOutput, LinearLayout mainLY, LinearLayout subLY) {
        mViewControl.setSignalOutputView(mainOutput, subOutput, mainLY, subLY);
    }

    public TVStateControl(Context mContext) {
        super(mContext);
        mViewControl = PIPPOPSurfaceViewControl.getSurfaceViewControlInstance();
        this.componentID = NavBasic.NAV_COMP_ID_POP;
        this.mDispatch = KeyDispatch.getInstance();
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.mComponentsManager = ComponentsManager.getInstance();
        this.mMtkTvPipPop = MtkTvPipPop.getInstance();
        this.mIntegrationZoom = IntegrationZoom.getInstance(mContext);
        this.mSundryImplement = SundryImplement.getInstanceNavSundryImplement(mContext);
        this.mSourceManager = InputSourceManager.getInstance(mContext);
        initComponentsView();
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(8, this);
        ComponentStatusListener.getInstance().addListener(7, this);
    }

    private void initComponentsView() {
        this.mChannelListDialog = (ChannelListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        this.mFavoriteChannelListView = (FavoriteListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST);
        this.mSourceListView = (SourceListView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
        this.mShowTextView = (SundryShowTextView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
        this.mTwinkleView = (TwinkleView) this.mComponentsManager.getComponentById(16777232);
        this.mBannerView = (BannerView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_BANNER);
        this.mBannerView.setAlpha(0.9f);
        this.mZoomTipView = (ZoomTipView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
        this.mMiscView = (MiscView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_MISC);
    }

    public void setVisibility(int visibility) {
        if (visibility == 0) {
            MtkLog.d("TVStateControl", "come in TVStateControl ComponentStatusListener.NAV_ENTER_STANDBY setVisibility,mFocusLabel.getVisibility()=" + this.mFocusLabel.getVisibility());
            if (this.mFocusLabel.getVisibility() != 0) {
                MtkLog.d("TVStateControl", "come in TVStateControl ComponentStatusListener.NAV_ENTER_STANDBY  mFocusLabel.show() ==" + this.mCommonIntegration.isPipOrPopState());
                if (this.mCommonIntegration.isPipOrPopState()) {
                    MtkLog.d("TVStateControl", "come in TVStateControl setVisibility mFocusLabel.show()");
                    this.mFocusLabel.show();
                }
            }
        } else {
            MtkLog.d("TVStateControl", "come in TVStateControl ComponentStatusListener.NAV_ENTER_STANDBY setVisibility=" + this.mFocusLabel.getVisibility());
            if (this.mFocusLabel.getVisibility() == 0) {
                MtkLog.d("TVStateControl", "come in TVStateControl ComponentStatusListener.NAV_ENTER_STANDBY mFocusLabel.release()");
                this.mFocusLabel.release();
            }
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
        MtkLog.d("TVStateControl", "isKeyHandler, KEYCODE_MTKIR_PIPPOP");
        hideComponentsWithPIPKey();
        this.mCommonIntegration.setDoPIPPOPAction(true);
        if ((ComponentsManager.getNativeActiveCompId() & NavBasic.NAV_NATIVE_COMP_ID_BASIC) != 0) {
            MtkLog.d("TVStateControl", "isKeyHandler, key already be passed to linux world(ginga)");
        } else {
            this.bindSubSource = true;
            TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(4);
            this.mDispatch.passKeyToNative(keyCode, (KeyEvent) null);
            if (1 != this.mIntegrationZoom.getCurrentZoom()) {
                this.mIntegrationZoom.setZoomModeToNormal();
            }
            this.mCommonIntegration.setStopTIFSetupWizardFunction(true);
        }
        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
        return true;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d("TVStateControl", "KeyHandler keyCode = " + keyCode);
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
                                MtkLog.d("TVStateControl", "come in TVStateControl KeyHandler passKeyToNative,keyCode =" + keyCode);
                                if (SundryImplement.getInstanceNavSundryImplement(this.mContext).isFreeze()) {
                                    SundryImplement.getInstanceNavSundryImplement(this.mContext).setFreeze(false);
                                }
                                this.mDispatch.passKeyToNative(keyCode, event);
                                ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                                return true;
                            default:
                                if (keyCode != 26 && this.mCommonIntegration.isPipOrPopState()) {
                                    reShowFocus();
                                }
                                return false;
                        }
                    }
                } else if (!this.mCommonIntegration.isPipOrPopState() || reShowFocus()) {
                    return true;
                } else {
                    MtkLog.d("TVStateControl", "come in TVStateControl KeyHandler passKeyToNative" + keyCode);
                    this.mDispatch.passKeyToNative(315, event);
                    ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                    return true;
                }
            }
            if (!this.mCommonIntegration.isPipOrPopState() || reShowFocus() || !this.mCommonIntegration.isPIPState()) {
                return true;
            }
            hideComponentsWithSizeAndPosition(keyCode);
            if (this.mSundryImplement.isFreeze() && keyCode == 10065) {
                this.mSundryImplement.setFreeze(false);
            }
            MtkLog.d("TVStateControl", "come in TVStateControl KeyHandler passKeyToNative" + keyCode);
            if (MarketRegionInfo.isFunctionSupport(13)) {
                if (227 == keyCode) {
                    mViewControl.changeSubOutputPosition();
                } else {
                    mViewControl.changeSubOutputSize();
                }
                if ("sub".equals(this.mCommonIntegration.getCurrentFocus())) {
                    updateFocusLabelPosition();
                }
            } else {
                this.mDispatch.passKeyToNative(keyCode, event);
            }
            return true;
        }
        if (this.mCommonIntegration.isPipOrPopState()) {
            int nextTVState = 0;
            if (reShowFocus()) {
                return true;
            }
            hideComponentsWithPIPKey();
            MtkLog.d("TVStateControl", "come in TVStateControl KeyHandler passKeyToNative KEYCODE_MTKIR_PIPPOP");
            if (1 == this.mCommonIntegration.getCurrentTVState()) {
                nextTVState = 2;
            } else if (2 == this.mCommonIntegration.getCurrentTVState()) {
                nextTVState = 0;
                this.currentFocusWin = this.mCommonIntegration.getCurrentFocus();
                this.focusSourceName = this.mSourceManager.getCurrentInputSourceName(this.currentFocusWin);
            }
            this.mCommonIntegration.setDoPIPPOPAction(true);
            this.mDispatch.passKeyToNative(keyCode, event);
            if (nextTVState == 0) {
                MtkLog.d("TVStateControl", "PIPPOPConstant.TV_NORMAL_STATE,mCommonIntegration.setStopTIFSetupWizardFunction(false);");
                this.mCommonIntegration.setStopTIFSetupWizardFunction(false);
            }
        }
        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
        return true;
    }

    public boolean deinitView() {
        return false;
    }

    public boolean isVisible() {
        MtkLog.d("TVStateControl", "come in TVStateControl isVisible mCommonIntegration.isPipOrPopState() " + this.mCommonIntegration.isPipOrPopState());
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
        MtkLog.d("TVStateControl", "come in need to reShowFocus");
        this.mComponentsManager.showNavComponent(NavBasic.NAV_COMP_ID_POP);
        return true;
    }

    private void updateFocusLabelPosition() {
        if ("main".equals(this.mCommonIntegration.getCurrentFocus())) {
            this.mFocusLabel.setPadding(mViewControl.getMainPosition()[0], mViewControl.getMainPosition()[1]);
        } else {
            this.mFocusLabel.setPadding(mViewControl.getSubPosition()[0], mViewControl.getSubPosition()[1]);
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID != 3) {
            switch (statusID) {
                case 7:
                case 8:
                    MtkLog.d("TVStateControl", "come in TVStateControl updateComponentStatus,statusID=" + statusID);
                    setVisibility(4);
                    return;
                default:
                    return;
            }
        }
    }

    public void setFocusLable(FocusLabel focusLabel) {
        this.mFocusLabel = focusLabel;
    }

    private void rebindMainAndSubInputSource(int currentState) {
        this.mainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        MtkLog.d("TVStateControl", " mainSourceName=" + this.mainSourceName);
        this.subSourceName = this.mSourceManager.getCurrentInputSourceName("sub");
        MtkLog.d("TVStateControl", " subSourceName=" + this.subSourceName);
        if (!TextUtils.isEmpty(this.mainSourceName)) {
            if (this.subSourceName.length() == 0) {
                this.subSourceName = MtkTvInputSource.getInstance().getCurrentInputSourceName("sub");
                MtkLog.d("TVStateControl", " subSourceName=" + this.subSourceName);
            }
            if (currentState == 0) {
                if ("sub".equals(this.currentFocusWin)) {
                    this.mSourceManager.changeCurrentInputSourceByName(this.focusSourceName, "main");
                } else {
                    this.mSourceManager.changeCurrentInputSourceByName(this.mainSourceName, "main");
                }
                this.mSourceManager.stopPipSession();
            } else if (!TextUtils.isEmpty(this.subSourceName)) {
                this.mSourceManager.changeCurrentInputSourceByName(this.mainSourceName, "main");
                if (!this.mainSourceName.equals(this.subSourceName)) {
                    MtkLog.d("TVStateControl", "!mainSourceName.equals(subSourceName)");
                    this.mSourceManager.changeCurrentInputSourceByName(this.subSourceName, "sub");
                } else {
                    MtkLog.d("TVStateControl", "mainSourceName.equals(subSourceName)");
                }
            }
        }
        this.mCommonIntegration.setDoPIPPOPAction(false);
    }

    private void doSwapSource() {
        String currentMainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        MtkLog.d("TVStateControl", " currentMainSourceName=" + currentMainSourceName);
        String currentSubSourceName = this.mSourceManager.getCurrentInputSourceName("sub");
        MtkLog.d("TVStateControl", " currentSubSourceName=" + currentSubSourceName);
        if (!TextUtils.isEmpty(currentMainSourceName)) {
            if (currentSubSourceName.length() == 0) {
                currentSubSourceName = MtkTvInputSource.getInstance().getCurrentInputSourceName("sub");
                MtkLog.d("TVStateControl", "currentSubSourceName.length() == 0,currentSubSourceName=" + currentSubSourceName);
            }
            if (TextUtils.isEmpty(currentSubSourceName) || currentMainSourceName.equals(currentSubSourceName)) {
                MtkLog.d("TVStateControl", "currentMainSourceName.equals(currentSubSourceName)");
                return;
            }
            MtkLog.d("TVStateControl", "!currentMainSourceName.equals(currentSubSourceName)");
            this.mSourceManager.changeCurrentInputSourceByName(currentMainSourceName, "sub");
            this.mSourceManager.changeCurrentInputSourceByName(currentSubSourceName, "main");
        }
    }
}
