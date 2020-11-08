package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.app.Activity;
import android.content.Context;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog;
import com.mediatek.wwtv.tvcenter.nav.view.MiscView;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.SomeArgs;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class StateNormal extends StateBase {
    private static final String TAG = "StateNormalDvr";
    /* access modifiers changed from: private */
    public static boolean isTuned = false;
    public static StateNormal mStateNormal;
    private static boolean noAudio = false;
    private static boolean noVideo = false;
    /* access modifiers changed from: private */
    public static int timeout = 3;
    /* access modifiers changed from: private */
    public static boolean tvNotShow = true;

    static /* synthetic */ int access$106() {
        int i = timeout - 1;
        timeout = i;
        return i;
    }

    static /* synthetic */ int access$110() {
        int i = timeout;
        timeout = i - 1;
        return i;
    }

    public StateNormal(Context context, DvrManager manager) {
        super(context, manager);
        setType(StatusType.NORMAL);
        getController().addEventHandler(new CallBackHandler(manager));
    }

    public static StateNormal getInstance() {
        if (mStateNormal == null) {
            return null;
        }
        return mStateNormal;
    }

    public static boolean isTuned() {
        return isTuned;
    }

    public static void setTuned(boolean isTuned2) {
        isTuned = isTuned2;
    }

    public static void setisVideo(boolean noVideo2) {
        MtkLog.d(TAG, "novideo=" + noVideo2);
        noVideo = noVideo2;
    }

    public static boolean isVideo() {
        return noVideo;
    }

    public static void setisAudio(boolean noAudio2) {
        MtkLog.d(TAG, "noaudio=" + noAudio2);
        noAudio = noAudio2;
    }

    public static boolean isAudio() {
        return noAudio;
    }

    public static boolean isTvNotShow() {
        return tvNotShow;
    }

    public static void setTvNotShow(boolean tvNotShow2) {
        tvNotShow = tvNotShow2;
    }

    static class CallBackHandler extends Handler {
        private int mRecordStatus = 2;
        WeakReference<DvrManager> weakReference;

        CallBackHandler(DvrManager dvrManager) {
            this.weakReference = new WeakReference<>(dvrManager);
        }

        public void handleMessage(Message msg) {
            if (msg != null) {
                DvrManager dvrManager = (DvrManager) this.weakReference.get();
                int callBack = msg.what;
                SomeArgs args = (SomeArgs) msg.obj;
                MtkLog.d(StateNormal.TAG, "callBack = " + callBack);
                if (callBack != 10007) {
                    switch (callBack) {
                        case 4097:
                            boolean unused = StateNormal.isTuned = false;
                            dvrManager.restoreToDefault(new StatusType[0]);
                            this.mRecordStatus = 2;
                            return;
                        case 4098:
                            boolean unused2 = StateNormal.isTuned = false;
                            dvrManager.restoreToDefault(new StatusType[0]);
                            this.mRecordStatus = 2;
                            return;
                        case 4099:
                            boolean unused3 = StateNormal.isTuned = true;
                            long programId = TIFProgramManager.getInstance(DvrManager.getInstance().getContext()).queryCurrentProgram();
                            Uri programUri = null;
                            if (programId != -1) {
                                programUri = TvContract.buildProgramUri(programId);
                            }
                            if (!DvrManager.getInstance().pvrIsRecording() && this.mRecordStatus > 1) {
                                if (StateNormal.timeout != 0) {
                                    StateNormal.access$110();
                                    MtkLog.d(StateNormal.TAG, "retry more");
                                    sendEmptyMessageDelayed(4099, 1000);
                                }
                                try {
                                    DvrManager.getInstance().startDvr(programUri);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                this.mRecordStatus = 0;
                                return;
                            }
                            return;
                        case 4100:
                            this.mRecordStatus = 3;
                            return;
                        case 4101:
                            int error = args.argi2;
                            MtkLog.d(StateNormal.TAG, "error = " + error);
                            dvrManager.restoreToDefault(new StatusType[0]);
                            this.mRecordStatus = 3;
                            boolean unused4 = StateNormal.isTuned = false;
                            boolean unused5 = StateNormal.tvNotShow = true;
                            if (DvrManager.getScheduleItem() != null) {
                                DvrManager.setScheduleItem((MtkTvBookingBase) null);
                            }
                            if (StateNormal.timeout == 0 || error == 1) {
                                dvrManager.showPromptInfo(StateNormal.getErrorId(error));
                                dvrManager.getController().dvrRelease();
                                int unused6 = StateNormal.timeout = 3;
                                return;
                            }
                            StateNormal.access$106();
                            return;
                        case 4102:
                            int unused7 = StateNormal.timeout = 3;
                            if (((String) args.arg2).equals("Recording_Session_event_recordingStart")) {
                                MtkLog.d(StateNormal.TAG, "RECORD_START");
                                this.mRecordStatus = 1;
                                removeMessages(4099);
                                dvrManager.speakText("start record");
                                SystemClock.sleep(500);
                                StateDvr stateDvr = StateDvr.getInstance(dvrManager.getContext(), dvrManager);
                                if (DvrManager.getScheduleItem() != null) {
                                    long deafult = DvrManager.getInstance().getDurations();
                                    if (deafult > 0) {
                                        MtkLog.d("1111+", "is recording=" + DvrManager.getScheduleItem().getRecordDuration());
                                        DvrManager.getScheduleItem().setRecordDuration(DvrManager.getScheduleItem().getRecordDuration() + deafult);
                                    }
                                    dvrManager.toRecord();
                                }
                                stateDvr.prepareStart();
                                dvrManager.setState((StateBase) stateDvr);
                                PwdDialog mPwdDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
                                if (mPwdDialog != null && mPwdDialog.isVisible()) {
                                    mPwdDialog.dismiss();
                                }
                                GingaTvDialog gingaTvDialog = (GingaTvDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_GINGA_TV);
                                if (gingaTvDialog != null && gingaTvDialog.isVisible()) {
                                    gingaTvDialog.dismiss();
                                }
                                FavoriteListDialog showFavoriteChannelListView = (FavoriteListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST);
                                if (showFavoriteChannelListView != null && showFavoriteChannelListView.isVisible()) {
                                    showFavoriteChannelListView.dismiss();
                                }
                                MiscView miscView = (MiscView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_MISC);
                                if (miscView != null && miscView.isVisible()) {
                                    miscView.setVisibility(8);
                                    return;
                                }
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else {
                    MtkLog.d(StateNormal.TAG, "tvNotShow = false");
                    boolean unused8 = StateNormal.tvNotShow = false;
                    if (!StateNormal.isTuned && StateDvr.getInstance() != null) {
                        dvrManager.restoreToDefault((StateBase) StateDvr.getInstance());
                    }
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode) {
        MtkLog.d(TAG, "onKeyDown, " + keyCode);
        if (keyCode != 130) {
            if (keyCode == 10062 && (DataSeparaterUtil.getInstance() == null || DataSeparaterUtil.getInstance().isSupportPvr())) {
                if (getManager().diskIsReady()) {
                    PwdDialog mPwdDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
                    if (mPwdDialog != null && mPwdDialog.isVisible()) {
                        mPwdDialog.dismiss();
                    }
                    ((BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER)).setVisibility(8);
                    getManager().uiManager.hiddenAllViews();
                    if (getManager().setState((StateBase) StateDvrFileList.getInstance(DvrManager.getInstance()))) {
                        GingaTvDialog g = (GingaTvDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_GINGA_TV);
                        if (MarketRegionInfo.getCurrentMarketRegion() == 2 && g != null) {
                            g.dismiss();
                        }
                        StateDvrFileList.getInstance().showPVRlist();
                        return true;
                    }
                    onRelease();
                } else {
                    getManager().showPromptInfo(4101);
                }
            }
        } else if (DataSeparaterUtil.getInstance() == null || DataSeparaterUtil.getInstance().isSupportPvr()) {
            List<Integer> activesComps = ComponentsManager.getInstance().getCurrentActiveComps();
            if (activesComps != null && activesComps.size() > 0) {
                Iterator<Integer> it = activesComps.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().intValue() == 16777230) {
                            MtkLog.d(TAG, "input source is active");
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (CommonIntegration.getInstance().is3rdTVSource()) {
                this.mManager.showPromptInfo(4098);
            } else if (CommonIntegration.getInstance().isPipOrPopState()) {
                this.mManager.showPromptInfo(4096);
            } else {
                new Thread(new Runnable() {
                    public void run() {
                        final boolean isSignalLoss = TVContent.getInstance(StateNormal.this.mContext).isSignalLoss();
                        MtkLog.d(StateNormal.TAG, "isSignalLoss=" + isSignalLoss);
                        ((Activity) StateNormal.this.mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                if (isSignalLoss) {
                                    StateNormal.this.mManager.showPromptInfo(4099);
                                } else {
                                    StateNormal.this.onKeyStartRecord();
                                }
                            }
                        });
                    }
                }).start();
            }
        } else {
            Toast.makeText(this.mContext, this.mContext.getString(R.string.nav_epg_not_support), 0).show();
        }
        return super.onKeyDown(keyCode);
    }

    /* access modifiers changed from: private */
    public void onKeyStartRecord() {
        if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
            this.mManager.showPromptInfo(4097);
        } else if (TVLogicManager.getInstance() != null && TVLogicManager.getInstance().getCurrentChannel() == null) {
            MtkLog.d(TAG, "isvideo = " + isVideo() + ",isaudio=" + isAudio());
            this.mManager.showPromptInfo(4103);
        } else if (SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
            this.mManager.showPromptInfo(4100);
        } else if (CommonIntegration.getInstance().isCurrentSourceATV()) {
            this.mManager.showPromptInfo(4097);
        } else if (!this.mManager.diskIsReady()) {
            this.mManager.showPromptInfo(4101);
        } else if (!isTuned) {
            MtkLog.d(TAG, "isTuned = false");
            tvNotShow = true;
            try {
                TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(CommonIntegration.getInstance().getCurrentChannelId());
                if (tifChannelInfo != null) {
                    this.mManager.getController().tune(TvContract.buildChannelUri(tifChannelInfo.mId), (Bundle) null);
                    timeout = 0;
                    DvrManager.getInstance().setState((StateBase) StateDvr.getInstance(DvrManager.getInstance().getContext(), DvrManager.getInstance()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MtkLog.d(TAG, "isTuned = true");
        }
    }

    public void onResume() {
        setRunning(true);
    }

    public void removeZoomTips() {
        SundryShowTextView stxtView;
        ((ZoomTipView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN)).setVisibility(8);
        if (IntegrationZoom.getInstance(this.mContext).screenModeZoomShow() && (stxtView = (SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY)) != null) {
            stxtView.setVisibility(8);
        }
    }

    public void reSetZoomValues(Context mContext) {
        IntegrationZoom.getInstance(mContext).setZoomMode(1);
        removeZoomTips();
    }

    /* access modifiers changed from: private */
    public static int getErrorId(int errorId) {
        if (errorId != 16) {
            if (errorId != 24867948) {
                switch (errorId) {
                    case 0:
                        return 4098;
                    case 1:
                        return 4102;
                    case 2:
                        return 4103;
                    case 3:
                        return 4098;
                    case 4:
                        return 4098;
                    case 5:
                        return 4103;
                    case 6:
                        return 4104;
                    case 7:
                        return 4105;
                    case 8:
                        break;
                    case 9:
                        return 4112;
                    case 10:
                        return 4112;
                    case 11:
                        break;
                    case 12:
                        return 4114;
                    case 13:
                        return 4098;
                    default:
                        return -1;
                }
            }
            return 4101;
        }
        return 4113;
    }
}
