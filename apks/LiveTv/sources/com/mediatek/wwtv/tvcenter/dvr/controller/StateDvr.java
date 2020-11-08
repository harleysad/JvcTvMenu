package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.tv.menu.MenuOptionMain;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.MtkTvRecordBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.manager.Util;
import com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar;
import com.mediatek.wwtv.tvcenter.dvr.ui.DVRTimerView;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.util.BannerImplement;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.SomeArgs;
import java.io.File;

public class StateDvr extends StateBase {
    private static final int Clear_All_View = 12;
    private static final int DEBUGDEBUG = 111111;
    protected static final int MMP_PLAYER_ERROR = 20;
    private static final int PAUSE_RECORD_TIMER = 4;
    private static final int RECORD_PAUSE = 13;
    private static final int RECORD_START = 10;
    private static final int RECORD_STOP = 11;
    public static final int REFRESH_TIMER = 6;
    private static final int SHOW_BIG_CTRL_BAR = 1;
    private static final int SHOW_SMALL_CTRL_BAR = 2;
    private static final int START_RECORD_TIMER = 3;
    private static final int STOP_RECORD_TIMER = 5;
    private static final String TAG = "StateDvr";
    private static boolean isTkActive = true;
    public static StateDvr stateDvr;
    private final int INTERVAL_SECOND = 60;
    private final int MAX_TIMING = 43200;
    private final int MIN_TIMING = 60;
    private DvrDialog conDialog2;
    private boolean isChangeSource = false;
    private boolean isChannellist = false;
    /* access modifiers changed from: private */
    public boolean isDirection = false;
    public String isSameSource = "";
    private final int lastFreeSize = 0;
    private StringBuilder mAllTimerToStr = new StringBuilder();
    private TextView mBRecordTimer;
    private TextView mBTextViewDate;
    private TextView mBTextViewFileInfo;
    private DVRControlbar mBigCtrlBar;
    private final Long mDuration = Long.valueOf(MessageType.delayMillis10);
    /* access modifiers changed from: private */
    public MyHandler mHandler;
    private DVRFiles mPVRFile;
    private boolean mShowDiskAttention = true;
    private DVRTimerView mSmallCtrlBar;
    private String recordTimeStr = "00:00:00";
    /* access modifiers changed from: private */
    public int recordTimer = 0;
    public int recordTimingLong = 1800;
    private String recordTimingStr = "12:00:00";
    private String remainTimeStr = "00:00";
    public int schedulePvrTimeing = -1;

    private StateDvr(Context context, DvrManager manager) {
        super(context, manager);
        setType(StatusType.DVR);
        getController().addEventHandler(new CallbackHandler(this));
    }

    public static StateDvr getInstance(Context mContext, DvrManager manager) {
        if (stateDvr == null) {
            stateDvr = new StateDvr(mContext, DvrManager.getInstance());
        }
        return stateDvr;
    }

    public static StateDvr getInstance() {
        return stateDvr;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public static boolean isTkActive() {
        return isTkActive;
    }

    public static void setTkActive(boolean isTkActive2) {
        isTkActive = isTkActive2;
    }

    public void prepareStart() {
        if (!CommonIntegration.getInstance().isCurrentSourceDTV()) {
            MtkTvInputSource.getInstance().setScartAutoJump(false);
        }
        SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.PVR_START, true, false);
        SaveValue.getInstance(this.mContext).saveBooleanValue(MenuConfigManager.PVR_START, true);
        this.mShowDiskAttention = true;
        this.mHandler = new MyHandler();
        this.mHandler.sendEmptyMessage(6);
        clearAllWindow();
        ChannelListDialog channelListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        SourceListView sourceListView = (SourceListView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
        boolean ismenushow = ((MenuOptionMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG)).isShowing();
        if ((channelListDialog != null && channelListDialog.isShowing()) || ((sourceListView != null && sourceListView.isShowing()) || ismenushow)) {
            this.isChannellist = true;
        }
        startPVRrecord();
        setRunning(true);
        if (getManager().isPvrDialogShow() || this.isChannellist || ismenushow) {
            showCtrBar();
            this.isChannellist = false;
            return;
        }
        showBigCtrlBar();
    }

    public void onResume() {
        if (isRunning()) {
            showBigCtrlBar();
        }
        clearWindow(false);
        this.mHandler.sendEmptyMessage(6);
    }

    public void onRelease() {
        super.onRelease();
    }

    public void showCtrBar() {
        if (this.mBigCtrlBar != null && this.mBigCtrlBar.isShowing()) {
            return;
        }
        if (this.mSmallCtrlBar == null || !this.mSmallCtrlBar.isShowing()) {
            showSmallCtrlBar();
        }
    }

    public void showSmallCtrlBar() {
        if (this.mSmallCtrlBar == null) {
            this.mSmallCtrlBar = new DVRTimerView((Activity) this.mContext);
            this.mSmallCtrlBar.getContentView().findViewById(R.id.pvr_rec_icon_small).setVisibility(0);
            if (stateDvr.getRecordTimer() == 0) {
                this.mSmallCtrlBar.setInfo("00:00:00");
            } else {
                this.mSmallCtrlBar.setInfo(Util.secondToString(stateDvr.getRecordTimer()));
            }
            this.mSmallCtrlBar.setOnDismissListener(new PopupWindow.OnDismissListener() {
                public void onDismiss() {
                    StateDvr.this.isRunning();
                }
            });
            this.mSmallCtrlBar.setDuration(43200000L);
            this.mSmallCtrlBar.show();
        } else if (!this.mSmallCtrlBar.isShowing()) {
            DVRTimerView dVRTimerView = this.mSmallCtrlBar;
            dVRTimerView.setInfo("[" + Util.secondToString(stateDvr.getRecordTimer()) + "]");
            this.mSmallCtrlBar.show();
        }
        DvrManager.getInstance().setVisibility(4);
    }

    public void showBigCtrlBar() {
        if (DvrManager.getInstance().isInPictureMode()) {
            showSmallCtrlBar();
            return;
        }
        if (this.mBigCtrlBar != null) {
            showProgramInfo();
        } else {
            initBigCtrlBar();
        }
        this.mBigCtrlBar.show();
    }

    private void initBigCtrlBar() {
        this.mBigCtrlBar = new DVRControlbar((Activity) this.mContext, R.layout.pvr_timeshfit_pvrworking, Long.valueOf(MessageType.delayMillis10), this);
        this.mBRecordTimer = (TextView) this.mBigCtrlBar.getContentView().findViewById(R.id.pvr_working_rec_time);
        this.mBTextViewDate = (TextView) this.mBigCtrlBar.getContentView().findViewById(R.id.pvr_working_currenttime);
        this.mBTextViewFileInfo = (TextView) this.mBigCtrlBar.getContentView().findViewById(R.id.info);
        this.mBigCtrlBar.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                if (StateDvr.this.isRunning()) {
                    StateDvr.this.mHandler.sendEmptyMessage(2);
                }
            }
        });
        showProgramInfo();
    }

    private void showProgramInfo() {
        if (CommonIntegration.getInstance().isCurrentSourceATV()) {
            this.mBTextViewFileInfo.setText("1" + "      " + getManager().getTvLogicManager().getChannelNumStr());
        } else if (CommonIntegration.getInstance().isCurrentSourceDTV()) {
            StringBuilder info = new StringBuilder();
            info.append(getManager().getTvLogicManager().getChannelNumStr());
            info.append("      ");
            String channelName = getManager().getTvLogicManager().getChannelName();
            if (channelName != null) {
                if (channelName.length() >= 30) {
                    channelName = channelName.substring(0, 27) + "...";
                }
                info.append(channelName);
            }
            info.append("      ");
            String pString = BannerImplement.getInstanceNavBannerImplement(this.mContext).getProgramTitle();
            if (pString == null || pString == "") {
                pString = "(No program title.)";
            }
            info.append(pString);
            MtkLog.e("showProgramInfo", "showProgramInfo:" + getManager().getTvLogicManager().getChannelName());
            this.mBTextViewFileInfo.setText(info.toString());
        } else {
            StringBuilder info2 = new StringBuilder();
            info2.append(CommonIntegration.getInstance().getCurrentSource());
            MtkLog.e("showProgramInfo", "showProgramInfo:" + CommonIntegration.getInstance().getCurrentSource());
            this.mBTextViewFileInfo.setText(info2.toString());
        }
    }

    public void dissmissBigCtrlBar() {
        if (this.mBigCtrlBar != null && this.mBigCtrlBar.isShowing()) {
            this.mBigCtrlBar.dismiss();
        }
    }

    public void recoveryView() {
        if (this.mBigCtrlBar != null) {
            this.mBigCtrlBar = null;
        }
    }

    public void clearWindow(boolean bigger) {
        if (!bigger) {
            this.mHandler.removeMessages(2);
            if (this.mSmallCtrlBar != null && this.mSmallCtrlBar.isShowing()) {
                this.mSmallCtrlBar.dismiss();
            }
        } else if (this.mBigCtrlBar != null && this.mBigCtrlBar.isShowing()) {
            this.mBigCtrlBar.dismiss();
        }
    }

    public void clearAllWindow() {
        clearWindow(true);
        clearWindow(false);
        resetAllParams();
    }

    private void resetAllParams() {
        this.recordTimeStr = "00:00:00";
        this.remainTimeStr = "00:00";
        this.recordTimingLong = 1800;
        this.schedulePvrTimeing = -1;
        this.recordTimingStr = "12:00:00";
        this.isSameSource = "";
        this.recordTimer = 0;
    }

    public void setSchedulePVRDuration(int duration) {
        this.schedulePvrTimeing = duration;
    }

    /* access modifiers changed from: private */
    public void refreshTimer() {
        this.mAllTimerToStr = new StringBuilder();
        this.mAllTimerToStr.append("[");
        this.recordTimeStr = Util.secondToString(stateDvr.getRecordTimer());
        if (this.schedulePvrTimeing != -1) {
            this.recordTimingLong = this.schedulePvrTimeing;
            setRecordTimingStr(Util.secondToString(this.recordTimingLong));
            this.schedulePvrTimeing = -1;
        }
        if (this.mSmallCtrlBar != null && this.mSmallCtrlBar.isShowing()) {
            this.mAllTimerToStr.append(this.recordTimeStr);
            this.mAllTimerToStr.append("]");
            this.mSmallCtrlBar.setInfo(this.mAllTimerToStr.toString());
        } else if (this.mBigCtrlBar != null && this.mBigCtrlBar.isShowing()) {
            this.remainTimeStr = countRemainSizeTime();
            this.mAllTimerToStr.append(this.recordTimeStr);
            this.mAllTimerToStr.append("/");
            this.mAllTimerToStr.append(this.remainTimeStr);
            this.mAllTimerToStr.append("/");
            this.mAllTimerToStr.append(getRecordTimingStr());
            this.mAllTimerToStr.append("]");
            this.mBRecordTimer.setText(this.mAllTimerToStr);
            this.mBTextViewDate.setText(Util.formatCurrentTime());
        }
        rollBackUIState();
    }

    private void rollBackUIState() {
        if (isRunning() && isTkActive) {
            try {
                if (!this.mBigCtrlBar.isShowing() && !this.mSmallCtrlBar.isShowing()) {
                    showBigCtrlBar();
                }
            } catch (Exception e) {
                Util.showELog(e.toString());
            }
            if (this.mBigCtrlBar != null && this.mBigCtrlBar.isShowing() && this.mSmallCtrlBar != null && this.mSmallCtrlBar.isShowing()) {
                this.mSmallCtrlBar.dismiss();
            }
        }
    }

    public boolean startPVRrecord() {
        if (this.mShowDiskAttention && !getManager().isPvrDialogShow() && !this.isChannellist) {
            getManager().showPromptInfo(4);
            this.mShowDiskAttention = false;
        }
        this.isSameSource = CommonIntegration.getInstance().getCurrentSource();
        MtkLog.e("isSameSource", "isSameSource:" + this.isSameSource);
        return false;
    }

    private String countRemainSizeTime() {
        Long diskFreeSize = Long.valueOf(MtkTvRecordBase.getStorageFreeSize());
        int remainTime = (int) ((((float) (diskFreeSize.longValue() / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED)) / getManager().getDiskSpeed()) + 0.5f);
        MtkLog.e(TAG, "short:" + remainTime);
        Long fileSize = Long.valueOf(MtkTvRecord.getRecordingFilesize());
        int duration = stateDvr.getRecordTimer();
        if (duration > 5 && fileSize.longValue() > 10485760) {
            remainTime = (int) (diskFreeSize.longValue() / (fileSize.longValue() / ((long) duration)));
            MtkLog.e(TAG, "short:" + remainTime);
        }
        return Util.secondToString(remainTime);
    }

    /* access modifiers changed from: private */
    public boolean checkTimer() {
        int currentTime = Util.strToSecond(this.recordTimeStr);
        int TotalTime = Util.strToSecond(this.recordTimingStr);
        MtkLog.d(TAG, "currentTime=" + currentTime + ",TotalTime=" + TotalTime);
        if (currentTime >= TotalTime) {
            return true;
        }
        return false;
    }

    private void addRecordTiming() {
        this.recordTimingLong = 60 + this.recordTimingLong;
        if (this.recordTimingLong > 43200) {
            this.recordTimingLong = 43200;
        }
        setRecordTimingStr(Util.secondToString(this.recordTimingLong));
        refreshTimer();
    }

    private void decreaseRecordTiming() {
        this.recordTimingLong -= 60;
        if (this.recordTimingLong < 60 || this.recordTimingLong < this.recordTimer) {
            this.recordTimingLong = Math.max(60, ((this.recordTimer / 60) + 1) * 60);
        }
        setRecordTimingStr(Util.secondToString(this.recordTimingLong));
        refreshTimer();
    }

    private void decreaseHourTiming() {
        this.recordTimingLong -= 3600;
        if (this.recordTimingLong < 60 || this.recordTimingLong < this.recordTimer) {
            this.recordTimingLong = Math.max(60, ((this.recordTimer / 60) + 1) * 60);
        }
        setRecordTimingStr(Util.secondToString(this.recordTimingLong));
        refreshTimer();
    }

    private void addHourTiming() {
        this.recordTimingLong = MtkTvTimeFormatBase.SECONDS_PER_HOUR + this.recordTimingLong;
        if (this.recordTimingLong > 43200) {
            this.recordTimingLong = 43200;
        }
        setRecordTimingStr(Util.secondToString(this.recordTimingLong));
        refreshTimer();
    }

    public boolean isRecording() {
        boolean running = false;
        if (this.mBigCtrlBar != null && this.mBigCtrlBar.isShowing()) {
            running = true;
        }
        if (this.mSmallCtrlBar != null && this.mSmallCtrlBar.isShowing()) {
            running = true;
        }
        return running || isRunning();
    }

    public boolean isBigCtrlBarShow() {
        if (this.mBigCtrlBar == null || !this.mBigCtrlBar.isShowing()) {
            return false;
        }
        return true;
    }

    public boolean isSmallCtrlBarShow() {
        if (this.mSmallCtrlBar == null || !this.mSmallCtrlBar.isShowing()) {
            return false;
        }
        return true;
    }

    public Handler getStatePVRHandler() {
        return this.mHandler;
    }

    public String getPvrSource() {
        return this.isSameSource;
    }

    public int getRecordTimer() {
        return this.recordTimer;
    }

    public void setRecordTimer(int recordTimer2) {
        this.recordTimer = recordTimer2;
    }

    public String getRecordTimingStr() {
        return this.recordTimingStr;
    }

    public void setRecordTimingStr(String recordTimingStr2) {
        this.recordTimingStr = recordTimingStr2;
    }

    public void stopDvrRecord(int messageType) {
        this.mContext.sendBroadcast(new Intent("com.mediatek.pvr.file"));
        if (isRunning()) {
            FavoriteListDialog favlist = (FavoriteListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST);
            if (favlist != null && favlist.isShowing()) {
                favlist.dismiss();
            }
            ChannelListDialog channelListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
            if (channelListDialog != null && channelListDialog.isShowing()) {
                channelListDialog.dismiss();
            }
            if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                StateDvrFileList.getInstance().dissmiss();
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    StateDvr.this.getManager().showPromptInfo(DvrManager.PRO_PVR_STOP);
                }
            }, 500);
        }
        setRunning(false);
        clearAllWindow();
        this.isSameSource = "";
        this.mHandler.sendEmptyMessage(12);
        MtkTvInputSource.getInstance().setScartAutoJump(true);
        SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.PVR_START, false, false);
        SaveValue.getInstance(this.mContext).saveBooleanValue(MenuConfigManager.PVR_START, false);
        DvrManager.getInstance().restoreToDefault((StateBase) stateDvr);
    }

    public void hideDvrdialog() {
        if (DvrManager.getInstance() != null && this.conDialog2 != null && this.conDialog2.isShowing()) {
            this.conDialog2.dismiss();
        }
    }

    class MyHandler extends Handler {
        MyHandler() {
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 20) {
                if (i != 10001) {
                    if (i != StateDvr.DEBUGDEBUG) {
                        switch (i) {
                            case 1:
                                if (StateDvr.stateDvr.isRunning()) {
                                    StateDvr.stateDvr.showBigCtrlBar();
                                    StateDvr.stateDvr.clearWindow(false);
                                    break;
                                }
                                break;
                            case 2:
                                if (StateDvr.stateDvr.isRunning()) {
                                    StateDvr.stateDvr.showSmallCtrlBar();
                                    StateDvr.stateDvr.clearWindow(true);
                                    break;
                                }
                                break;
                            case 3:
                            case 4:
                            case 5:
                                break;
                            case 6:
                                removeMessages(6);
                                if (!StateDvr.stateDvr.checkTimer()) {
                                    StateDvr.this.getController().getRecordDuration();
                                    if (SystemProperties.getInt(PwdDialog.AUTO_TEST_PROPERTY, 0) == 1 && StateDvr.this.recordTimer >= 15) {
                                        try {
                                            new File("/data/vendor/tmp/autotest/pvr_pass").createNewFile();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    StateDvr.stateDvr.refreshTimer();
                                    break;
                                } else {
                                    StateDvr.this.getController().stopRecording();
                                    StateDvr.this.mContext.sendBroadcast(new Intent("com.mtk.state.file"));
                                    return;
                                }
                            default:
                                switch (i) {
                                    case 10:
                                        StateDvr.stateDvr.setRunning(true);
                                        break;
                                    case 11:
                                        StateDvr.stateDvr.setRunning(false);
                                        StateDvr.stateDvr.getController().stopRecording();
                                        break;
                                    case 13:
                                        StateDvr.stateDvr.setRunning(false);
                                        break;
                                }
                        }
                    }
                } else {
                    if (!StateDvr.this.isDirection) {
                        StateDvr.stateDvr.dissmissBigCtrlBar();
                    }
                    boolean unused = StateDvr.this.isDirection = false;
                }
            }
            super.handleMessage(msg);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01e6, code lost:
        r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r6.mContext).getPreChannelInfo();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x01f6, code lost:
        if (r1.equals("TV") != false) goto L_0x0213;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x0206, code lost:
        if (com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getInstance(r6.mContext).getConflictSourceList().contains(r1) != false) goto L_0x0213;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0210, code lost:
        if (r2.mType.equals("1") != false) goto L_0x0213;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x021d, code lost:
        if (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r6.mContext).hasOneChannel() != false) goto L_0x0255;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x0229, code lost:
        if (com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getInstance().isCurrentTvSource("main") != false) goto L_0x022c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x022c, code lost:
        r0 = (com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog) com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance().getComponentById(com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic.NAV_COMP_ID_FAV_LIST);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0239, code lost:
        if (r0 == null) goto L_0x0244;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x023f, code lost:
        if (r0.isShowing() == false) goto L_0x0244;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0241, code lost:
        r0.dismiss();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0244, code lost:
        r6.conDialog2 = new com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog((android.app.Activity) r6.mContext, 1, r7, 1);
        r6.conDialog2.show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x0254, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x0255, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0259, code lost:
        if (r6.mHandler == null) goto L_0x0260;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x025b, code lost:
        r6.mHandler.sendEmptyMessage(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0260, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x0263, code lost:
        if (r6.mHandler == null) goto L_0x026a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x0265, code lost:
        r6.mHandler.sendEmptyMessage(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x026a, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x019e, code lost:
        if (r6.mHandler == null) goto L_0x01a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01a0, code lost:
        r6.mHandler.sendEmptyMessage(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01a5, code lost:
        r1 = getController().getSrcType();
        com.mediatek.wwtv.tvcenter.util.MtkLog.d(TAG, "srctype = " + r1);
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01c6, code lost:
        if (r7 != 166) goto L_0x01d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01c8, code lost:
        r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r6.mContext).getUpAndDownChannel(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01d5, code lost:
        if (r7 != 167) goto L_0x01e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01d7, code lost:
        r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r6.mContext).getUpAndDownChannel(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01e4, code lost:
        if (r7 != 229) goto L_0x01f0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r7) {
        /*
            r6 = this;
            r0 = 0
            switch(r7) {
                case 7: goto L_0x02e7;
                case 8: goto L_0x02e7;
                case 9: goto L_0x02e7;
                case 10: goto L_0x02e7;
                case 11: goto L_0x02e7;
                case 12: goto L_0x02e7;
                case 13: goto L_0x02e7;
                case 14: goto L_0x02e7;
                case 15: goto L_0x02e7;
                case 16: goto L_0x02e7;
                default: goto L_0x0004;
            }
        L_0x0004:
            r1 = 10000(0x2710, double:4.9407E-320)
            r3 = 2
            r4 = 10001(0x2711, float:1.4014E-41)
            r5 = 1
            switch(r7) {
                case 19: goto L_0x02c8;
                case 20: goto L_0x02a9;
                case 21: goto L_0x028a;
                case 22: goto L_0x026b;
                case 23: goto L_0x0261;
                case 24: goto L_0x0257;
                case 25: goto L_0x0257;
                default: goto L_0x000d;
            }
        L_0x000d:
            switch(r7) {
                case 89: goto L_0x0257;
                case 90: goto L_0x0256;
                default: goto L_0x0010;
            }
        L_0x0010:
            switch(r7) {
                case 166: goto L_0x019c;
                case 167: goto L_0x019c;
                default: goto L_0x0013;
            }
        L_0x0013:
            switch(r7) {
                case 171: goto L_0x019b;
                case 172: goto L_0x0158;
                default: goto L_0x0016;
            }
        L_0x0016:
            switch(r7) {
                case 183: goto L_0x0157;
                case 184: goto L_0x0156;
                default: goto L_0x0019;
            }
        L_0x0019:
            switch(r7) {
                case 222: goto L_0x0261;
                case 223: goto L_0x0261;
                default: goto L_0x001c;
            }
        L_0x001c:
            r1 = 8
            switch(r7) {
                case 4: goto L_0x014c;
                case 82: goto L_0x013b;
                case 86: goto L_0x010a;
                case 93: goto L_0x0109;
                case 127: goto L_0x00e5;
                case 130: goto L_0x0089;
                case 213: goto L_0x0261;
                case 215: goto L_0x0078;
                case 229: goto L_0x019c;
                case 251: goto L_0x0261;
                case 255: goto L_0x0261;
                case 10062: goto L_0x0023;
                case 10066: goto L_0x0261;
                case 10467: goto L_0x00e5;
                case 10471: goto L_0x0261;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0255
        L_0x0023:
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            if (r0 == 0) goto L_0x0030
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x0030
            return r5
        L_0x0030:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r2 = 16777234(0x1000012, float:2.3509937E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r2)
            com.mediatek.wwtv.tvcenter.nav.view.PwdDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.PwdDialog) r0
            if (r0 == 0) goto L_0x0048
            boolean r2 = r0.isVisible()
            if (r2 == 0) goto L_0x0048
            r0.dismiss()
        L_0x0048:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r2 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r3 = 16777218(0x1000002, float:2.3509893E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r2 = r2.getComponentById(r3)
            com.mediatek.wwtv.tvcenter.nav.view.BannerView r2 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r2
            boolean r3 = r2.isVisible()
            if (r3 == 0) goto L_0x005e
            r2.setVisibility(r1)
        L_0x005e:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = r6.getManager()
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r3 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList r3 = com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList.getInstance(r3)
            boolean r1 = r1.setState((com.mediatek.wwtv.tvcenter.dvr.controller.StateBase) r3)
            if (r1 == 0) goto L_0x0077
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList r1 = com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList.getInstance()
            r1.showPVRlist()
        L_0x0077:
            return r5
        L_0x0078:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = r6.getManager()
            com.mediatek.wwtv.tvcenter.dvr.controller.UImanager r1 = r1.uiManager
            r1.hiddenAllViews()
            r6.showSmallCtrlBar()
            r6.clearWindow(r5)
            goto L_0x0255
        L_0x0089:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r2 = 16777228(0x100000c, float:2.350992E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r2)
            com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog) r0
            if (r0 == 0) goto L_0x00a1
            boolean r2 = r0.isVisible()
            if (r2 == 0) goto L_0x00a1
            r0.dismiss()
        L_0x00a1:
            com.mediatek.wwtv.setting.widget.view.ScheduleListDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListDialog.getDialog()
            if (r2 == 0) goto L_0x00b8
            com.mediatek.wwtv.setting.widget.view.ScheduleListDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListDialog.getDialog()
            boolean r2 = r2.isShowing()
            if (r2 == 0) goto L_0x00b8
            com.mediatek.wwtv.setting.widget.view.ScheduleListDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListDialog.getDialog()
            r2.dismiss()
        L_0x00b8:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r2 = r6.getManager()
            boolean r3 = r6.isRecording()
            if (r3 == 0) goto L_0x00e4
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r3 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r4 = 16777240(0x1000018, float:2.3509954E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r3 = r3.getComponentById(r4)
            com.mediatek.wwtv.tvcenter.nav.view.MiscView r3 = (com.mediatek.wwtv.tvcenter.nav.view.MiscView) r3
            if (r3 == 0) goto L_0x00da
            boolean r4 = r3.isVisible()
            if (r4 == 0) goto L_0x00da
            r3.setVisibility(r1)
        L_0x00da:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            r1.sendEmptyMessage(r5)
            r1 = 4102(0x1006, float:5.748E-42)
            r2.showPromptInfo(r1)
        L_0x00e4:
            return r5
        L_0x00e5:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            if (r1 == 0) goto L_0x00ee
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            r1.sendEmptyMessage(r3)
        L_0x00ee:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
            boolean r1 = r1.timeShiftIsEnable()
            if (r1 == 0) goto L_0x0108
            android.os.Handler r0 = new android.os.Handler
            r0.<init>()
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$4 r1 = new com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$4
            r1.<init>()
            r2 = 500(0x1f4, double:2.47E-321)
            r0.postDelayed(r1, r2)
            return r5
        L_0x0108:
            return r0
        L_0x0109:
            return r0
        L_0x010a:
            int r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getActiveCompId()
            r1 = 16777230(0x100000e, float:2.3509926E-38)
            if (r0 != r1) goto L_0x0120
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r1)
            com.mediatek.wwtv.tvcenter.nav.view.SourceListView r0 = (com.mediatek.wwtv.tvcenter.nav.view.SourceListView) r0
            r0.dismiss()
        L_0x0120:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r0 = r6.getManager()
            java.lang.String r1 = "stop record"
            r0.speakText(r1)
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            if (r0 == 0) goto L_0x0133
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r1 = 6
            r0.removeMessages(r1)
        L_0x0133:
            com.mediatek.wwtv.tvcenter.dvr.manager.Controller r0 = r6.getController()
            r0.stopRecording()
            return r5
        L_0x013b:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = r6.getManager()
            com.mediatek.wwtv.tvcenter.dvr.controller.UImanager r1 = r1.uiManager
            r1.hiddenAllViews()
            r6.showSmallCtrlBar()
            r6.clearWindow(r5)
            goto L_0x0255
        L_0x014c:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            if (r1 == 0) goto L_0x0155
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            r1.sendEmptyMessage(r3)
        L_0x0155:
            return r0
        L_0x0156:
            return r5
        L_0x0157:
            return r5
        L_0x0158:
            r0 = 1
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 == 0) goto L_0x0187
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurrentSourceBlocked()
            if (r1 == 0) goto L_0x0175
            int r1 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.getCurrentMarketRegion()
            if (r1 == r5) goto L_0x0188
            r0 = 0
            goto L_0x0188
        L_0x0175:
            int r1 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.getCurrentMarketRegion()
            if (r1 == r5) goto L_0x0188
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            int r1 = r1.getAllEPGChannelLength()
            if (r1 > 0) goto L_0x0188
            r0 = 0
            goto L_0x0188
        L_0x0187:
            r0 = 0
        L_0x0188:
            if (r0 == 0) goto L_0x019a
            com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog r1 = new com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog
            android.content.Context r2 = r6.mContext
            android.app.Activity r2 = (android.app.Activity) r2
            r1.<init>(r2, r5, r7, r5)
            r6.conDialog2 = r1
            com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog r1 = r6.conDialog2
            r1.show()
        L_0x019a:
            return r5
        L_0x019b:
            return r0
        L_0x019c:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            if (r1 == 0) goto L_0x01a5
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            r1.sendEmptyMessage(r3)
        L_0x01a5:
            com.mediatek.wwtv.tvcenter.dvr.manager.Controller r1 = r6.getController()
            java.lang.String r1 = r1.getSrcType()
            java.lang.String r2 = "StateDvr"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "srctype = "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            r2 = 0
            r3 = 166(0xa6, float:2.33E-43)
            if (r7 != r3) goto L_0x01d3
            android.content.Context r3 = r6.mContext
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r3 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r3)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r2 = r3.getUpAndDownChannel(r5)
            goto L_0x01f0
        L_0x01d3:
            r3 = 167(0xa7, float:2.34E-43)
            if (r7 != r3) goto L_0x01e2
            android.content.Context r3 = r6.mContext
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r3 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r3)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r2 = r3.getUpAndDownChannel(r0)
            goto L_0x01f0
        L_0x01e2:
            r3 = 229(0xe5, float:3.21E-43)
            if (r7 != r3) goto L_0x01f0
            android.content.Context r3 = r6.mContext
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r3 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r3)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r2 = r3.getPreChannelInfo()
        L_0x01f0:
            java.lang.String r3 = "TV"
            boolean r3 = r1.equals(r3)
            if (r3 != 0) goto L_0x0213
            android.content.Context r3 = r6.mContext
            com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager r3 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getInstance(r3)
            java.util.ArrayList r3 = r3.getConflictSourceList()
            boolean r3 = r3.contains(r1)
            if (r3 != 0) goto L_0x0213
            java.lang.String r3 = r2.mType
            java.lang.String r4 = "1"
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x0213
            goto L_0x0255
        L_0x0213:
            android.content.Context r3 = r6.mContext
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r3 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r3)
            boolean r3 = r3.hasOneChannel()
            if (r3 != 0) goto L_0x0255
            com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager r3 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getInstance()
            java.lang.String r4 = "main"
            boolean r3 = r3.isCurrentTvSource(r4)
            if (r3 != 0) goto L_0x022c
            goto L_0x0255
        L_0x022c:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r3 = 16777226(0x100000a, float:2.3509915E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r3)
            com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog r0 = (com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog) r0
            if (r0 == 0) goto L_0x0244
            boolean r3 = r0.isShowing()
            if (r3 == 0) goto L_0x0244
            r0.dismiss()
        L_0x0244:
            com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog r3 = new com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog
            android.content.Context r4 = r6.mContext
            android.app.Activity r4 = (android.app.Activity) r4
            r3.<init>(r4, r5, r7, r5)
            r6.conDialog2 = r3
            com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog r3 = r6.conDialog2
            r3.show()
            return r5
        L_0x0255:
            return r0
        L_0x0256:
            return r5
        L_0x0257:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            if (r1 == 0) goto L_0x0260
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            r1.sendEmptyMessage(r3)
        L_0x0260:
            return r0
        L_0x0261:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            if (r1 == 0) goto L_0x026a
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r1 = r6.mHandler
            r1.sendEmptyMessage(r3)
        L_0x026a:
            return r0
        L_0x026b:
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            if (r0 == 0) goto L_0x0289
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x0289
            r6.addRecordTiming()
            r6.showBigCtrlBar()
            r6.isDirection = r5
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.removeMessages(r4)
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.sendEmptyMessageDelayed(r4, r1)
        L_0x0289:
            return r5
        L_0x028a:
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            if (r0 == 0) goto L_0x02a8
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x02a8
            r6.decreaseRecordTiming()
            r6.showBigCtrlBar()
            r6.isDirection = r5
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.removeMessages(r4)
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.sendEmptyMessageDelayed(r4, r1)
        L_0x02a8:
            return r5
        L_0x02a9:
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            if (r0 == 0) goto L_0x02c7
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x02c7
            r6.decreaseHourTiming()
            r6.showBigCtrlBar()
            r6.isDirection = r5
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.removeMessages(r4)
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.sendEmptyMessageDelayed(r4, r1)
        L_0x02c7:
            return r5
        L_0x02c8:
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            if (r0 == 0) goto L_0x02e6
            com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar r0 = r6.mBigCtrlBar
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x02e6
            r6.addHourTiming()
            r6.showBigCtrlBar()
            r6.isDirection = r5
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.removeMessages(r4)
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr$MyHandler r0 = r6.mHandler
            r0.sendEmptyMessageDelayed(r4, r1)
        L_0x02e6:
            return r5
        L_0x02e7:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr.onKeyDown(int):boolean");
    }

    public boolean isChangeSource() {
        return this.isChangeSource;
    }

    public void setChangeSource(boolean isChangeSource2) {
        this.isChangeSource = isChangeSource2;
    }

    static class CallbackHandler extends Handler {
        StateDvr stateDvr;

        CallbackHandler(StateDvr stateDvr2) {
            this.stateDvr = stateDvr2;
        }

        public void handleMessage(Message msg) {
            if (msg != null) {
                MtkLog.d(StateDvr.TAG, "callBack = " + msg.what);
                int callBack = msg.what;
                SomeArgs args = (SomeArgs) msg.obj;
                switch (callBack) {
                    case 4100:
                        StateNormal.setTuned(false);
                        StateNormal.setTvNotShow(true);
                        if (this.stateDvr.getHandler() != null) {
                            this.stateDvr.getHandler().removeMessages(6);
                        }
                        DvrManager.getInstance().getController().dvrRelease();
                        this.stateDvr.stopDvrRecord(1002);
                        DvrManager.getInstance();
                        if (DvrManager.getScheduleItem() != null) {
                            DvrManager.getInstance().startSchedulePvr();
                            return;
                        }
                        return;
                    case 4101:
                        int i = args.argi2;
                        DvrManager.getInstance().restoreToDefault((StateBase) this.stateDvr);
                        DvrManager.getInstance().restoreToDefault(new StatusType[0]);
                        return;
                    case 4102:
                        Bundle bundle = (Bundle) args.arg3;
                        if (((String) args.arg2).equals(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION)) {
                            this.stateDvr.setRecordTimer(bundle.getInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION_VALUE));
                            this.stateDvr.refreshTimer();
                            if (this.stateDvr.checkTimer()) {
                                this.stateDvr.getController().stopRecording();
                                this.stateDvr.mContext.sendBroadcast(new Intent("com.mtk.state.file"));
                                return;
                            }
                            this.stateDvr.mHandler.sendEmptyMessageDelayed(6, 1000);
                            MtkLog.d(StateDvr.TAG, "callBack recordTimer = " + this.stateDvr.getRecordTimer());
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private static int getErrorId(int errorId) {
        MtkLog.d(TAG, "errorid=" + errorId);
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
