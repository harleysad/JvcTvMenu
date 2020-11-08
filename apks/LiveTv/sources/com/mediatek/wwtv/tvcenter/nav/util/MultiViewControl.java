package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvMultiView;
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
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;

public class MultiViewControl extends NavBasicMisc implements ComponentStatusListener.ICStatusListener {
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
    private final String TAG = "MultiViewControl";
    private String currentFocusWin = "main";
    private int focusIconMarginX;
    private int focusIconMarginY;
    private final Handler focusLabelChangeHandler = new Handler(this.mContext.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1879048193) {
                TvCallbackData CFGdata = (TvCallbackData) msg.obj;
                MtkLog.d("MultiViewControl", "come in MultiViewControl,TvCallbackConst.MSG_CB_CONFIG,mainSourceName =" + MultiViewControl.this.mainSourceName + ", subSourceName =" + MultiViewControl.this.subSourceName);
                MtkLog.d("MultiViewControl", "come in MultiViewControl,TvCallbackConst.MSG_CB_CONFIG,CFGdata.param1 =" + CFGdata.param1 + ", CFGdata.param2 =" + CFGdata.param2);
            }
        }
    };
    private String focusSourceName = "";
    boolean isLastChangeFocusComplete = true;
    private BannerView mBannerView;
    private ChannelListDialog mChannelListDialog;
    /* access modifiers changed from: private */
    public final CommonIntegration mCommonIntegration;
    private final ComponentsManager mComponentsManager;
    private final KeyDispatch mDispatch;
    private FavoriteListDialog mFavoriteChannelListView;
    private FocusLabel mFocusLabel;
    private final IntegrationZoom mIntegrationZoom;
    private MiscView mMiscView;
    /* access modifiers changed from: private */
    public final MtkTvMultiView mMtkTvMultiView;
    private SundryShowTextView mShowTextView;
    private SourceListView mSourceListView;
    private final InputSourceManager mSourceManager;
    private final SundryImplement mSundryImplement;
    private final TvCallbackHandler mTvCallbackHandler;
    private TwinkleView mTwinkleView;
    private ZoomTipView mZoomTipView;
    /* access modifiers changed from: private */
    public String mainSourceName = "";
    /* access modifiers changed from: private */
    public String subSourceName = "";

    public void setOutputView(TvSurfaceView mainOutput, TvSurfaceView subOutput, LinearLayout mainLY, LinearLayout subLY) {
        mViewControl.setSignalOutputView(mainOutput, subOutput, mainLY, subLY);
    }

    public MultiViewControl(Context mContext) {
        super(mContext);
        mViewControl = PIPPOPSurfaceViewControl.getSurfaceViewControlInstance();
        this.componentID = NavBasic.NAV_COMP_ID_POP;
        this.mDispatch = KeyDispatch.getInstance();
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.mComponentsManager = ComponentsManager.getInstance();
        this.mIntegrationZoom = IntegrationZoom.getInstance(mContext);
        this.mSundryImplement = SundryImplement.getInstanceNavSundryImplement(mContext);
        this.mSourceManager = InputSourceManager.getInstance(mContext);
        this.mMtkTvMultiView = MtkTvMultiView.getInstance();
        this.mTvCallbackHandler = TvCallbackHandler.getInstance();
        this.mTvCallbackHandler.addCallBackListener(this.focusLabelChangeHandler);
        initComponentsView();
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(8, this);
        ComponentStatusListener.getInstance().addListener(7, this);
        ComponentStatusListener.getInstance().addListener(5, this);
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
            MtkLog.d("MultiViewControl", "come in MultiViewControl ComponentStatusListener.NAV_ENTER_STANDBY setVisibility,mFocusLabel.getVisibility()=" + this.mFocusLabel.getVisibility());
            if (this.mFocusLabel.getVisibility() != 0) {
                MtkLog.d("MultiViewControl", "come in MultiViewControl ComponentStatusListener.NAV_ENTER_STANDBY mFocusLabel.show() ==" + this.mCommonIntegration.isPipOrPopState());
                if (this.mCommonIntegration.isPipOrPopState()) {
                    MtkLog.d("MultiViewControl", "come in MultiViewControl setVisibility mFocusLabel.show()");
                    this.mFocusLabel.show();
                }
            }
        } else {
            MtkLog.d("MultiViewControl", "come in MultiViewControl ComponentStatusListener.NAV_ENTER_STANDBY setVisibility=" + this.mFocusLabel.getVisibility());
            if (this.mFocusLabel.getVisibility() == 0) {
                MtkLog.d("MultiViewControl", "come in MultiViewControl ComponentStatusListener.NAV_ENTER_STANDBY mFocusLabel.release()");
                this.mFocusLabel.release();
            }
        }
        super.setVisibility(visibility);
    }

    public boolean isCoExist(int componentID) {
        return true;
    }

    public boolean isKeyHandler(int keyCode) {
        return false;
    }

    public void setModeToPIP() {
        MtkLog.d("MultiViewControl", "subTvViewVisible");
        TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(0);
        this.mMtkTvMultiView.setChgSource(false);
        this.mCommonIntegration.recordCurrentTvState(1);
        this.mCommonIntegration.setStopTIFSetupWizardFunction(true);
        this.mMtkTvMultiView.setNewTvMode(1);
        mViewControl.changeOutputWithTVState(1);
        rebindMainAndSubInputSource(1);
        updateFocusLabelPosition();
        TurnkeyUiMainActivity.getInstance().getBlockScreenView().setVisibility(4);
        setVisibility(0);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d("MultiViewControl", "KeyHandler keyCode = " + keyCode);
        Log.d("MultiViewControl", "KeyHandler keyCode = " + keyCode);
        if (event != null && event.getRepeatCount() >= 1) {
            return true;
        }
        if (keyCode != 171) {
            if (keyCode != 227) {
                if (keyCode != 10062) {
                    if (keyCode != 10065) {
                        switch (keyCode) {
                            case 21:
                            case 22:
                                MtkLog.d("MultiViewControl", "isLastChangeFocusComplete =" + this.isLastChangeFocusComplete);
                                if (!this.isLastChangeFocusComplete) {
                                    return true;
                                }
                                this.isLastChangeFocusComplete = false;
                                MtkLog.d("MultiViewControl", "come in MultiViewControl KeyHandler,keyCode =" + keyCode);
                                MtkLog.d("MultiViewControl", "come in MultiViewControl KeyHandler,mCommonIntegration.isPipOrPopState() =" + this.mCommonIntegration.isPipOrPopState());
                                if (this.mCommonIntegration.isPipOrPopState()) {
                                    if (notSupportChangeFocus()) {
                                        MtkLog.d("MultiViewControl", "come in MultiViewControl KeyHandler notSupportChangeFocus()");
                                        this.isLastChangeFocusComplete = true;
                                        return true;
                                    } else if (reShowFocus()) {
                                        this.isLastChangeFocusComplete = true;
                                        return true;
                                    } else {
                                        hideComponentsWithLeftKey();
                                        if (SundryImplement.getInstanceNavSundryImplement(this.mContext).isFreeze()) {
                                            SundryImplement.getInstanceNavSundryImplement(this.mContext).setFreeze(false);
                                        }
                                        MtkLog.d("yiqinghuang", "mCommonIntegration.getCurrentFocus()" + this.mCommonIntegration.getCurrentFocus());
                                        if ("main" == this.mCommonIntegration.getCurrentFocus()) {
                                            MtkLog.d("yiqinghuang", "setCurrentFocusSub");
                                            this.mCommonIntegration.setCurrentFocus("sub");
                                            TurnkeyUiMainActivity.getInstance().getTvView().setStreamVolume(0.0f);
                                            TurnkeyUiMainActivity.getInstance().getPipView().setStreamVolume(1.0f);
                                        } else {
                                            MtkLog.d("yiqinghuang", "setCurrentFocusMain");
                                            this.mCommonIntegration.setCurrentFocus("main");
                                            TurnkeyUiMainActivity.getInstance().getPipView().setStreamVolume(0.0f);
                                            TurnkeyUiMainActivity.getInstance().getTvView().setStreamVolume(1.0f);
                                        }
                                        updateFocusLabelPosition();
                                        this.currentFocusWin = this.mCommonIntegration.getCurrentFocus();
                                        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                                        this.isLastChangeFocusComplete = true;
                                    }
                                }
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
                    MtkLog.d("MultiViewControl", "come in MultiViewControl KeyHandler passKeyToNative" + keyCode);
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
            MtkLog.d("MultiViewControl", "come in TVStateControl KeyHandler passKeyToNative" + keyCode);
            if (MarketRegionInfo.isFunctionSupport(13)) {
                if (227 == keyCode) {
                    mViewControl.changeSubOutputPosition();
                    Log.d("MultiViewControl", "PIP_POS_OK");
                } else {
                    mViewControl.changeSubOutputSize();
                    if (Build.VERSION.SDK_INT <= 23) {
                        this.mMtkTvMultiView.setNewTvMode(1);
                        this.mSourceManager.stopPipSession(false);
                        this.mSourceManager.changeCurrentInputSourceByName(this.mSourceManager.getCurrentInputSourceName("sub"), "sub");
                    }
                    Log.d("MultiViewControl", "PIP_SIZE_OK");
                }
                if ("sub".equals(this.mCommonIntegration.getCurrentFocus())) {
                    updateFocusLabelPosition();
                }
            } else {
                this.mDispatch.passKeyToNative(keyCode, event);
            }
            return true;
        }
        MtkLog.d("MultiViewControl", "is3rdTVSource(): " + this.mCommonIntegration.is3rdTVSource() + ", isPipOrPopState(): " + this.mCommonIntegration.isPipOrPopState());
        if (this.mCommonIntegration.is3rdTVSource()) {
            Toast.makeText(this.mContext, "Don't support Third Party Source in PIP/POP Mode!", 0).show();
            return true;
        }
        MtkLog.d("MultiViewControl", "getCurrentTVState(): " + this.mCommonIntegration.getCurrentTVState());
        if (this.mCommonIntegration.isPipOrPopState()) {
            if (this.mSourceListView.isShowing()) {
                MtkLog.d("MultiViewControl", "KeyHandler, SourceListView is Showing");
                return true;
            } else if (reShowFocus()) {
                return true;
            } else {
                int nextTVState = 0;
                hideComponentsWithPIPKey();
                MtkLog.d("MultiViewControl", "come in MultiViewControl KeyHandler passKeyToNative KEYCODE_MTKIR_PIPPOP");
                if (1 == this.mCommonIntegration.getCurrentTVState()) {
                    nextTVState = 2;
                } else if (2 == this.mCommonIntegration.getCurrentTVState()) {
                    nextTVState = 0;
                    this.focusSourceName = this.mSourceManager.getCurrentInputSourceName(this.currentFocusWin);
                    TurnkeyUiMainActivity.getInstance().getTvView().reset();
                    TurnkeyUiMainActivity.getInstance().getPipView().reset();
                    String focus = this.mCommonIntegration.getCurrentFocus();
                    MtkLog.d("MultiViewControl", "If focus is sub, need get the screen mode from api. focus: " + focus);
                    if (mViewControl != null && "sub".equals(focus)) {
                        mViewControl.setScreenModeChangedFlag(true);
                    }
                }
                this.mCommonIntegration.setDoPIPPOPAction(true);
                this.mMtkTvMultiView.setChgSource(false);
                this.mMtkTvMultiView.setNewTvMode(nextTVState);
                mViewControl.changeOutputWithTVState(nextTVState);
                this.mCommonIntegration.recordCurrentTvState(nextTVState);
                if (nextTVState == 0) {
                    MtkLog.d("MultiViewControl", "PIPPOPConstant.TV_NORMAL_STATE,mCommonIntegration.setStopTIFSetupWizardFunction(false);");
                    this.mCommonIntegration.setCurrentFocus("main");
                    this.mCommonIntegration.setStopTIFSetupWizardFunction(false);
                }
                rebindMainAndSubInputSource(nextTVState);
                if (nextTVState != 0) {
                    updateFocusLabelPosition();
                    Log.d("MultiViewControl", "Enter_POP_OK");
                } else {
                    setVisibility(8);
                    MtkLog.d("MultiViewControl", "PipView Gone");
                    TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(8);
                    Log.d("MultiViewControl", "Enter_Normal_OK");
                }
            }
        }
        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
        return true;
    }

    public void setCurrentFocusWin(String currentFocusWin2) {
        this.currentFocusWin = currentFocusWin2;
    }

    public boolean deinitView() {
        this.mTvCallbackHandler.removeCallBackListener(this.focusLabelChangeHandler);
        return false;
    }

    public boolean isVisible() {
        MtkLog.d("MultiViewControl", "come in MultiViewControl isVisible mCommonIntegration.isPipOrPopState() " + this.mCommonIntegration.isPipOrPopState());
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
        MtkLog.d("MultiViewControl", "come in need to reShowFocus");
        this.mComponentsManager.showNavComponent(NavBasic.NAV_COMP_ID_POP);
        return true;
    }

    private void updateFocusLabelPosition() {
        MtkLog.d("yiqinghuang", "mCommonIntegration.getCurrentFocus()" + this.mCommonIntegration.getCurrentFocus());
        if ("main".equals(this.mCommonIntegration.getCurrentFocus())) {
            this.mFocusLabel.setPadding(mViewControl.getMainPosition()[0], mViewControl.getMainPosition()[1]);
        } else {
            this.mFocusLabel.setPadding(mViewControl.getSubPosition()[0], mViewControl.getSubPosition()[1]);
        }
        this.mFocusLabel.show();
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d("MultiViewControl", "statesID:" + statusID + ", value: " + value);
        if (statusID == 3) {
            return;
        }
        if (statusID != 5) {
            switch (statusID) {
                case 7:
                    MtkLog.d("MultiViewControl", "come in MultiViewControl updateComponentStatus,statusID=" + statusID);
                    setVisibility(4);
                    return;
                case 8:
                    MtkLog.d("MultiViewControl", "come in MultiViewControl updateComponentStatus,statusID=" + statusID);
                    if (this.mFocusLabel != null) {
                        setVisibility(4);
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else if (value == 10471 && this.mCommonIntegration.isPipOrPopState()) {
            MtkLog.d("MultiViewControl", "isPOPState");
            mViewControl.setScreenModeChangedFlag(true);
        }
    }

    public void setFocusLable(FocusLabel focusLabel) {
        this.mFocusLabel = focusLabel;
    }

    private void rebindMainAndSubInputSource(int currentState) {
        this.mainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        MtkLog.d("MultiViewControl", " mainSourceName=" + this.mainSourceName);
        this.subSourceName = this.mSourceManager.getCurrentInputSourceName("sub");
        MtkLog.d("MultiViewControl", " subSourceName=" + this.subSourceName);
        if (this.subSourceName == null || this.subSourceName.equals(this.mainSourceName) || this.subSourceName.length() == 0 || this.subSourceName.equals("0") || this.mSourceManager.isConflicted(this.mainSourceName, this.subSourceName) || !this.mSourceManager.isSourceEnable(this.subSourceName)) {
            this.subSourceName = this.mSourceManager.querySourceNameWithAnother(this.mainSourceName);
            MtkLog.d("MultiViewControl", " querySourceNameWithAnother,subSourceName=" + this.subSourceName);
        }
        MtkLog.d("MultiViewControl", "rebindMainAndSubInputSource,currentState ==" + currentState);
        if (currentState == 0) {
            MtkLog.d("MultiViewControl", "rebindMainAndSubInputSource,PIPPOPConstant.TV_NORMAL_STATE");
            MtkLog.d("MultiViewControl", "rebindMainAndSubInputSource,currentFocusWin ==" + this.currentFocusWin);
            if ("sub".equals(this.currentFocusWin)) {
                this.mSourceManager.changeCurrentInputSourceByName(this.focusSourceName, "main");
                MtkLog.d("MultiViewControl", "changeCurrentInputSourceByNameMain" + this.focusSourceName);
                this.mSourceManager.saveOutputSourceName(this.mainSourceName, "sub");
            } else {
                this.mSourceManager.changeCurrentInputSourceByName(this.mainSourceName, "main");
                MtkLog.d("MultiViewControl", "changeCurrentInputSourceByNameMain" + this.mainSourceName);
            }
            this.currentFocusWin = "main";
        } else if (1 == currentState) {
            MtkLog.d("MultiViewControl", "rebindMainAndSubInputSource,PIPPOPConstant.TV_PIP_STATE");
            this.mSourceManager.stopSession(false);
            this.mSourceManager.changeCurrentInputSourceByName(this.mainSourceName, "main");
            MtkLog.d("MultiViewControl", "!mainSourceName.equals(subSourceName)");
            this.mSourceManager.changeCurrentInputSourceByName(this.subSourceName, "sub");
        } else {
            this.mSourceManager.stopSession(false);
            this.mSourceManager.stopPipSession(false);
            this.mSourceManager.changeCurrentInputSourceByName(this.mainSourceName, "main");
            this.mSourceManager.changeCurrentInputSourceByName(this.subSourceName, "sub");
            MtkLog.d("MultiViewControl", "rebindMainAndSubInputSource,PIPPOPConstant.TV_POP_STATE");
        }
        this.mCommonIntegration.setDoPIPPOPAction(false);
    }

    private void doSwapSource() {
        String currentMainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        MtkLog.d("MultiViewControl", " currentMainSourceName=" + currentMainSourceName);
        String currentSubSourceName = this.mSourceManager.getCurrentInputSourceName("sub");
        MtkLog.d("MultiViewControl", " currentSubSourceName=" + currentSubSourceName);
        if (!currentMainSourceName.equals(currentSubSourceName)) {
            MtkLog.d("MultiViewControl", "!currentMainSourceName.equals(currentSubSourceName)");
            this.mSourceManager.changeCurrentInputSourceByName(currentMainSourceName, "sub");
            this.mSourceManager.changeCurrentInputSourceByName(currentSubSourceName, "main");
            return;
        }
        MtkLog.d("MultiViewControl", "currentMainSourceName.equals(currentSubSourceName)");
    }

    public void setNormalTvModeWithGooglePiP() {
        this.mainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        this.subSourceName = this.mSourceManager.getCurrentInputSourceName("sub");
        MtkLog.d("MultiViewControl", "come in setNormalTvModeWithEas,currentFocusWin==" + this.currentFocusWin + ", subSourceName ==" + this.subSourceName + ", mainSourceName == " + this.mainSourceName);
        this.mCommonIntegration.setDoPIPPOPAction(true);
        this.mMtkTvMultiView.setChgSource(false);
        this.mCommonIntegration.setStopTIFSetupWizardFunction(false);
        this.mMtkTvMultiView.setNewTvMode(0);
        mViewControl.changeOutputWithTVState(0);
        this.mCommonIntegration.recordCurrentTvState(0);
        this.mCommonIntegration.setCurrentFocus("main");
        rebindMainAndSubInputSource(0);
        TurnkeyUiMainActivity.getInstance().getPipView().reset();
        TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(8);
    }

    public void setNormalTvModeWithEas() {
        this.mainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        this.subSourceName = this.mSourceManager.getCurrentInputSourceName("sub");
        MtkLog.d("MultiViewControl", "come in setNormalTvModeWithEas,currentFocusWin==" + this.currentFocusWin + ", subSourceName ==" + this.subSourceName + ", mainSourceName == " + this.mainSourceName);
        TurnkeyUiMainActivity.getInstance().getPipView().reset();
        this.mMtkTvMultiView.setNewTvMode(0);
        mViewControl.changeOutputWithTVState(0);
        this.mCommonIntegration.recordCurrentTvState(0);
        this.mCommonIntegration.setCurrentFocus("main");
        TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(8);
        if (this.subSourceName.equals("TV")) {
            this.mSourceManager.saveOutputSourceName("TV", "main");
            this.mSourceManager.saveOutputSourceName(this.mainSourceName, "sub");
        }
    }

    public void setNormalTvModeWithMenuReset() {
        TurnkeyUiMainActivity.getInstance().getPipView().reset();
        this.mMtkTvMultiView.setNewTvMode(0);
        mViewControl.changeOutputWithTVState(0);
        this.mCommonIntegration.recordCurrentTvState(0);
        this.mCommonIntegration.setCurrentFocus("main");
        this.mSourceManager.saveOutputSourceName("", "sub");
        TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(8);
    }

    public void setNormalTvModeWithLauncher(boolean enterLauncher) {
        this.mainSourceName = this.mSourceManager.getCurrentInputSourceName("main");
        this.focusSourceName = this.mSourceManager.getCurrentInputSourceName(this.currentFocusWin);
        MtkLog.d("MultiViewControl", "come in setNormalTvModeWithLauncher,currentFocusWin==" + this.currentFocusWin + ", focusSourceName ==" + this.focusSourceName + ", mainSourceName == " + this.mainSourceName);
        TurnkeyUiMainActivity.getInstance().getTvView().reset();
        TurnkeyUiMainActivity.getInstance().getPipView().reset();
        TurnkeyUiMainActivity.getInstance().getPipView().setVisibility(8);
        new Thread(new Runnable() {
            public void run() {
                MultiViewControl.this.mMtkTvMultiView.setNewTvMode(0);
                MultiViewControl.this.mCommonIntegration.recordCurrentTvState(0);
                MultiViewControl.this.mCommonIntegration.setCurrentFocus("main");
            }
        }).start();
        if (mViewControl != null && this.mCommonIntegration.isPIPState()) {
            MtkLog.d("MultiViewControl", "need update the screen mode,so set the flag to be true!");
            mViewControl.setScreenModeChangedFlag(true);
        }
        mViewControl.changeOutputWithTVState(0);
        if ("sub".equals(this.currentFocusWin)) {
            if (enterLauncher) {
                this.mSourceManager.changeCurrentInputSourceByName(this.focusSourceName, "main");
            } else {
                this.mSourceManager.saveOutputSourceName(this.focusSourceName, "main");
            }
            this.mSourceManager.saveOutputSourceName(this.mainSourceName, "sub");
        } else if (enterLauncher) {
            this.mSourceManager.changeCurrentInputSourceByName(this.mainSourceName, "main");
        }
        this.currentFocusWin = "main";
        new Thread(new Runnable() {
            public void run() {
            }
        }).start();
        if (enterLauncher) {
            TurnkeyUiMainActivity.getInstance().getTvView().setStreamVolume(1.0f);
        }
    }
}
