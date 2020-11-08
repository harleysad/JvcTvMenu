package com.android.tv.menu;

import android.content.Context;
import com.android.tv.menu.customization.CustomAction;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class TvOptionsRowAdapter extends CustomizableOptionsRowAdapter {
    private static final String TAG = "TvOptionsRowAdapter";
    private static final String TAG_IS3RD_SOURCE = "is3rdSource";
    private boolean mInAppPipAction = true;
    private int mPositionCiAction;
    private int mPositionPipAction;

    public TvOptionsRowAdapter(Context context, List<CustomAction> customActions) {
        super(context, customActions);
        this.mInAppPipAction = context.getPackageManager().hasSystemFeature("android.software.picture_in_picture");
    }

    /* access modifiers changed from: protected */
    public List<MenuAction> createBaseActions() {
        List<MenuAction> actionList = new ArrayList<>();
        if (MarketRegionInfo.isFunctionSupport(38)) {
            actionList.add(MenuAction.SELECT_SOURCE_ACTION);
            setOptionChangedListener(MenuAction.SELECT_SOURCE_ACTION);
        }
        actionList.add(MenuAction.SELECT_AUTO_PICTURE_ACTION);
        setOptionChangedListener(MenuAction.SELECT_AUTO_PICTURE_ACTION);
        actionList.add(MenuAction.SELECT_DISPLAY_MODE_ACTION);
        setOptionChangedListener(MenuAction.SELECT_DISPLAY_MODE_ACTION);
        boolean isCNDTV = true;
        if (!(1 == MarketRegionInfo.getCurrentMarketRegion() || 2 == MarketRegionInfo.getCurrentMarketRegion())) {
            MenuAction.SELECT_CLOSED_CAPTION_ACTION.setActionNameResId(R.string.menu_setup_subtitle);
        }
        setOptionChangedListener(MenuAction.SELECT_CLOSED_CAPTION_ACTION);
        if (1 == MarketRegionInfo.getCurrentMarketRegion()) {
            actionList.add(MenuAction.SELECT_AUDIO_LANGUAGE_ACTION);
            setOptionChangedListener(MenuAction.SELECT_AUDIO_LANGUAGE_ACTION);
        }
        if (this.mInAppPipAction) {
            actionList.add(MenuAction.PIP_IN_APP_ACTION);
            setOptionChangedListener(MenuAction.PIP_IN_APP_ACTION);
        }
        actionList.add(MenuAction.SELECT_SPEAKERS_ACTION);
        setOptionChangedListener(MenuAction.SELECT_SPEAKERS_ACTION);
        actionList.add(MenuAction.POWER_ACTION);
        setOptionChangedListener(MenuAction.POWER_ACTION);
        CommonIntegration ci = TvSingletons.getSingletons().getCommonIntegration();
        boolean isEUTV = CommonIntegration.isEURegion() && ci.isCurrentSourceTv();
        if (!CommonIntegration.isCNRegion() || !ci.isCurrentSourceDTV()) {
            isCNDTV = false;
        }
        if (DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportCI() && ((isEUTV && !CommonIntegration.isEUPARegion()) || isCNDTV || CommonIntegration.isEUPARegion())) {
            actionList.add(MenuAction.BROADCAST_TV_CI_ACTION);
            setOptionChangedListener(MenuAction.BROADCAST_TV_CI_ACTION);
            if (CommonIntegration.isEUPARegion() && ci.isCurrentSourceATV()) {
                MenuAction.setEnabled(MenuAction.BROADCAST_TV_CI_ACTION, false);
            }
        }
        if (MarketRegionInfo.isFunctionSupport(10)) {
            MtkLog.d(TAG, "FunctionSupport:true");
            if (DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportOAD()) {
                MtkLog.d(TAG, "isSupportOAD:true");
                if (CommonIntegration.isEURegion() || CommonIntegration.isEUPARegion()) {
                    actionList.add(MenuAction.BROADCAST_TV_OAD_ACTION);
                    setOptionChangedListener(MenuAction.BROADCAST_TV_OAD_ACTION);
                }
            }
        }
        if (CommonIntegration.isSARegion() && MarketRegionInfo.isFunctionSupport(2) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportGinga()) {
            MtkLog.d(TAG, "createBaseActions||add ginga");
            actionList.add(MenuAction.GINGA_SELECTION);
            setOptionChangedListener(MenuAction.GINGA_SELECTION);
        }
        if (PartnerSettingsConfig.isMiscItemDisplay("menu_advanced_options")) {
            actionList.add(MenuAction.BROADCAST_TV_SETTINGS_ACTION);
            setOptionChangedListener(MenuAction.BROADCAST_TV_SETTINGS_ACTION);
        }
        actionList.add(MenuAction.SETTINGS_ACTION);
        setOptionChangedListener(MenuAction.SETTINGS_ACTION);
        return actionList;
    }

    /* access modifiers changed from: protected */
    public boolean updateActions() {
        boolean changed = false;
        if (updateMultiAudioAction()) {
            changed = true;
        }
        if (updatePictureModeAction()) {
            changed = true;
        }
        if (updateCIMenuAction()) {
            changed = true;
        }
        if (updateOADAction()) {
            changed = true;
        }
        if (updateDisplayModeAction()) {
            changed = true;
        }
        if (updateClosedCaptionAction()) {
            changed = true;
        }
        if (updateGingaAction()) {
            return true;
        }
        return changed;
    }

    private boolean updateClosedCaptionAction() {
        int index;
        boolean mShow = false;
        if (3 != MarketRegionInfo.getCurrentMarketRegion() && !DataSeparaterUtil.getInstance().isAtvOnly()) {
            MenuAction.setEnabled(MenuAction.SELECT_CLOSED_CAPTION_ACTION, !CommonIntegration.getInstance().isCurrentSourceHDMI());
            mShow = true;
        } else if (CommonIntegration.getInstance().is3rdTVSource()) {
            MtkLog.d(TAG, "updateClosedCaptionAction||3rdTVSource");
            mShow = true;
        }
        if (mShow && getActionIndex(MenuAction.SELECT_CLOSED_CAPTION_ACTION.getType()) < 0) {
            MtkLog.d(TAG, "updateClosedCaptionAction||addAction");
            addAction(getActionIndex(MenuAction.SELECT_DISPLAY_MODE_ACTION.getType()) + 1, MenuAction.SELECT_CLOSED_CAPTION_ACTION);
        } else if (!mShow && (index = getActionIndex(MenuAction.SELECT_CLOSED_CAPTION_ACTION.getType())) >= 0) {
            removeAction(index);
            notifyItemRemoved(index);
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean updateMultiAudioAction() {
        return false;
    }

    private boolean updateDisplayModeAction() {
        CommonIntegration ci = TvSingletons.getSingletons().getCommonIntegration();
        MtkTvAVMode navMtkTvAVMode = MtkTvAVMode.getInstance();
        boolean is3Rd = ci.is3rdTVSource();
        boolean isEnable = false;
        SaveValue.writeWorldStringValue(getMainActivity(), is3Rd ? "1" : "0", TAG_IS3RD_SOURCE, true);
        if (navMtkTvAVMode != null) {
            int[] allScreenMode = navMtkTvAVMode.getAllScreenMode();
            isEnable = allScreenMode != null && allScreenMode.length > 0;
        }
        MtkLog.d(TAG, "updateDisplayModeAction||is3Rd =" + is3Rd + "||isEnable =" + isEnable);
        MenuAction.setEnabled(MenuAction.SELECT_DISPLAY_MODE_ACTION, !is3Rd && isEnable);
        if (is3Rd || !isEnable) {
            return false;
        }
        return true;
    }

    private boolean updateCIMenuAction() {
        boolean isShow;
        CommonIntegration ci = TvSingletons.getSingletons().getCommonIntegration();
        boolean isEUTV = CommonIntegration.isEURegion() && ci.isCurrentSourceTv();
        boolean isCNDTV = CommonIntegration.isCNRegion() && ci.isCurrentSourceDTV();
        boolean isSupportCI = DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportCI();
        MtkLog.d(TAG, "updateCIMenuAction||isEUTV =" + isEUTV + "||isCNDTV =" + isCNDTV + "||isSupportCI =" + isSupportCI);
        if (!isSupportCI || ((!isEUTV || CommonIntegration.isEUPARegion()) && !isCNDTV && (!CommonIntegration.isEUPARegion() || !ci.isCurrentSourceDTV()))) {
            isShow = false;
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_CI_ACTION, false);
        } else {
            isShow = true;
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_CI_ACTION, true);
        }
        MtkLog.d(TAG, "updateCIMenuAction||isShow =" + isShow);
        if (isShow && getActionIndex(MenuAction.BROADCAST_TV_CI_ACTION.getType()) < 0) {
            MtkLog.d(TAG, "updateCIMenuAction||addAction");
            addAction(getActionIndex(MenuAction.POWER_ACTION.getType()) + 1, MenuAction.BROADCAST_TV_CI_ACTION);
        }
        return true;
    }

    private boolean updateOADAction() {
        CommonIntegration ci = TvSingletons.getSingletons().getCommonIntegration();
        if (ci.isPipOrPopState()) {
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_OAD_ACTION, false);
            return true;
        } else if ((DvrManager.getInstance().getState() instanceof StateDvrPlayback) && DvrManager.getInstance().getState().isRunning()) {
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_OAD_ACTION, false);
            return true;
        } else if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_OAD_ACTION, false);
            return true;
        } else if ((!CommonIntegration.isEURegion() || CommonIntegration.isEUPARegion() || !ci.isCurrentSourceTv()) && (!CommonIntegration.isEUPARegion() || !ci.isCurrentSourceDTVforEuPA() || ci.is3rdTVSource())) {
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_OAD_ACTION, false);
            return true;
        } else {
            MenuAction.setEnabled(MenuAction.BROADCAST_TV_OAD_ACTION, true);
            return false;
        }
    }

    private boolean updatePictureModeAction() {
        return false;
    }

    private boolean updateGingaAction() {
        boolean isGingaEnable = TvSingletons.getSingletons().getCommonIntegration().isCurrentSourceDTV();
        MtkLog.d(TAG, "updateGingaAction||isGingaEnable =" + isGingaEnable);
        MenuAction.setEnabled(MenuAction.GINGA_SELECTION, isGingaEnable);
        return isGingaEnable;
    }

    /* access modifiers changed from: protected */
    public void executeBaseAction(int type) {
        if (type != 22) {
            switch (type) {
                case 0:
                    MenuAction.showCCSetting(getMainActivity());
                    return;
                case 1:
                    MenuAction.showPictureFormatSetting(getMainActivity());
                    return;
                case 2:
                    MenuAction.enterAndroidPIP();
                    return;
                case 4:
                    MenuAction.showMultiAudioSetting(getMainActivity());
                    return;
                case 7:
                    MenuAction.showSetting(getMainActivity());
                    return;
                case 9:
                    MenuAction.showPictureModeSetting(getMainActivity());
                    return;
                case 10:
                    MenuAction.showSoundSpeakersSetting(getMainActivity());
                    return;
                case 11:
                    MenuAction.showBroadcastTvSetting(getMainActivity());
                    return;
                case 12:
                    MenuAction.showPowerSetting(getMainActivity());
                    return;
                case 13:
                    MenuAction.showOAD(getMainActivity());
                    return;
                case 14:
                    MenuAction.showCI(getMainActivity());
                    return;
                case 15:
                    MenuAction.enterAndroidSource();
                    return;
                default:
                    return;
            }
        } else {
            MtkLog.d(TAG, "executeBaseAction||OPTION_GINGA");
            MenuAction.showGinga(getMainActivity());
        }
    }
}
