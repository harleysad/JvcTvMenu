package com.mediatek.wwtv.tvcenter.tiftimeshift;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.MountPoint;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.Core;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog;
import com.mediatek.wwtv.tvcenter.nav.view.InfoBarDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.WeakHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramInfo;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class TifTimeshiftView extends NavBasicView {
    public static final String ACTION_TIMESHIFT_STOP = "com.mediatek.wwtv.tvcenter.tiftimeshift.TIMESHIFT_STOP";
    public static final int FILTER_VIDEO = 1;
    public static final int MSG_PAUSE_TIMESHIFT = 1001;
    public static final int PROGRESSBAR_WIDTH = 1000;
    public static final int PROGRESSBAR_WIDTH_AVAILABLE = 580;
    private static final String TAG = "TifTimeshiftView";
    private ImageView buffer;
    private ComponentsManager comManager;
    /* access modifiers changed from: private */
    public final Context context;
    private ImageView end;
    private ImageView indicator;
    /* access modifiers changed from: private */
    public InternalHandler mHandler;
    /* access modifiers changed from: private */
    public ImageView mPlayFNum;
    /* access modifiers changed from: private */
    public ImageView mPlayIcon;
    private long mProgramEndTimeMs = 0;
    private long mProgramStartTimeMs = 0;
    /* access modifiers changed from: private */
    public int mTFCallbackPlaybackStatus = -1;
    /* access modifiers changed from: private */
    public TifTimeShiftManager mTimeShiftManager;
    private TextView playingTime;
    private TextView programEndtime;
    private TextView programStartTime;
    int progressStartTimeMs = 1;
    private ImageView start;
    private ImageView watch;

    public TifTimeshiftView(Context context2, AttributeSet attrs, int defStyle) {
        super(context2, attrs, defStyle);
        this.componentID = NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW;
        this.context = context2;
    }

    public TifTimeshiftView(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        this.componentID = NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW;
        this.context = context2;
    }

    public TifTimeshiftView(Context context2) {
        super(context2);
        this.componentID = NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW;
        this.context = context2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0053, code lost:
        return super.isKeyHandler(r9);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isKeyHandler(int r9) {
        /*
            r8 = this;
            java.lang.String r0 = "TifTimeshiftView"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "isKeyHandler keyCode = "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 127(0x7f, float:1.78E-43)
            r1 = 1
            r2 = 0
            if (r9 == r0) goto L_0x0054
            r0 = 10467(0x28e3, float:1.4667E-41)
            if (r9 == r0) goto L_0x0054
            switch(r9) {
                case 85: goto L_0x0054;
                case 86: goto L_0x0024;
                case 87: goto L_0x0024;
                case 88: goto L_0x0024;
                case 89: goto L_0x0024;
                case 90: goto L_0x0024;
                default: goto L_0x0023;
            }
        L_0x0023:
            goto L_0x004f
        L_0x0024:
            com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
            java.lang.String r3 = "g_record__rec_tshift_mode"
            int r0 = r0.getConfigValue(r3)
            if (r0 != 0) goto L_0x0031
            return r2
        L_0x0031:
            android.content.Context r3 = r8.mContext
            com.mediatek.wwtv.tvcenter.util.SaveValue r3 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r3)
            java.lang.String r4 = "timeshift_start"
            boolean r3 = r3.readBooleanValue(r4)
            if (r3 != 0) goto L_0x0040
            return r2
        L_0x0040:
            int r2 = r8.getVisibility()
            if (r2 == 0) goto L_0x004f
            com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager r2 = r8.mTimeShiftManager
            boolean r2 = r2.isAvailable()
            if (r2 == 0) goto L_0x004f
            return r1
        L_0x004f:
            boolean r0 = super.isKeyHandler(r9)
            return r0
        L_0x0054:
            com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
            java.lang.String r3 = "g_record__rec_tshift_mode"
            int r0 = r0.getConfigValue(r3)
            if (r0 != 0) goto L_0x0061
            return r2
        L_0x0061:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r3 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r4 = 16777234(0x1000012, float:2.3509937E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r3 = r3.getComponentById(r4)
            com.mediatek.wwtv.tvcenter.nav.view.PwdDialog r3 = (com.mediatek.wwtv.tvcenter.nav.view.PwdDialog) r3
            com.mediatek.twoworlds.tv.MtkTvPWDDialog r4 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
            int r4 = r4.PWDShow()
            java.lang.String r5 = "TifTimeshiftView"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "MtkTvPWDDialog.getInstance().PWDShow(): "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            if (r4 != 0) goto L_0x0092
            r3.show()
            return r2
        L_0x0092:
            r8.dismissGingaDialog()
            r8.dismissGingaloadingDialog()
            java.lang.String r5 = "TifTimeshiftView"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "getVisibility="
            r6.append(r7)
            int r7 = r8.getVisibility()
            r6.append(r7)
            java.lang.String r7 = ",isAvailable="
            r6.append(r7)
            com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager r7 = r8.mTimeShiftManager
            boolean r7 = r7.isAvailable()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            int r5 = r8.getVisibility()
            if (r5 == 0) goto L_0x010f
            com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager r5 = r8.mTimeShiftManager
            boolean r5 = r5.isAvailable()
            if (r5 == 0) goto L_0x010f
            java.lang.String r5 = "TifTimeshiftView"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "isKeyhandler mTFCallbackPlaybackStatus:"
            r6.append(r7)
            int r7 = r8.mTFCallbackPlaybackStatus
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            r5 = -1
            r8.mTFCallbackPlaybackStatus = r5
            android.widget.ImageView r5 = r8.mPlayIcon
            r6 = 2131231146(0x7f0801aa, float:1.8078365E38)
            r5.setImageResource(r6)
            android.widget.ImageView r5 = r8.mPlayFNum
            r6 = 4
            r5.setVisibility(r6)
            com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager r5 = r8.mTimeShiftManager
            r5.pause()
            android.content.Context r5 = r8.mContext
            com.mediatek.wwtv.tvcenter.util.SaveValue r5 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r5)
            java.lang.String r6 = "timeshift_start"
            r5.saveBooleanValue(r6, r1)
            android.content.Context r5 = r8.mContext
            java.lang.String r6 = "timeshift_start"
            com.mediatek.wwtv.tvcenter.util.SaveValue.saveWorldBooleanValue(r5, r6, r1, r2)
            return r1
        L_0x010f:
            android.content.Context r1 = r8.context
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance(r1)
            boolean r1 = r1.diskIsReady()
            if (r1 != 0) goto L_0x0127
            android.content.Context r1 = r8.context
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance(r1)
            r5 = 4119(0x1017, float:5.772E-42)
            r1.showPromptInfo(r5)
            goto L_0x0133
        L_0x0127:
            android.content.Context r1 = r8.context
            r5 = 2131692524(0x7f0f0bec, float:1.901415E38)
            android.widget.Toast r1 = android.widget.Toast.makeText(r1, r5, r2)
            r1.show()
        L_0x0133:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeshiftView.isKeyHandler(int):boolean");
    }

    public boolean isCoExist(int componentID) {
        return super.isCoExist(componentID);
    }

    public boolean isVisible() {
        return SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d(TAG, "KeyHandler keyCode = " + keyCode);
        if (keyCode != 4) {
            if (keyCode != 229) {
                if (keyCode != 10467) {
                    switch (keyCode) {
                        case 85:
                            break;
                        case 86:
                            reset();
                            this.mHandler.sendEmptyMessage(1001);
                            return true;
                        case 87:
                            setVisibility(0);
                            startTimeout(10000);
                            if (this.mTimeShiftManager.isForwarding()) {
                                int displayedPlaySpeed = this.mTimeShiftManager.getDisplayedPlaySpeed();
                                TifTimeShiftManager tifTimeShiftManager = this.mTimeShiftManager;
                                if (displayedPlaySpeed != 1) {
                                    showToast(true);
                                    return true;
                                }
                            }
                            if (this.mTimeShiftManager.isRewinding()) {
                                int displayedPlaySpeed2 = this.mTimeShiftManager.getDisplayedPlaySpeed();
                                TifTimeShiftManager tifTimeShiftManager2 = this.mTimeShiftManager;
                                if (displayedPlaySpeed2 != 1) {
                                    showToast(false);
                                    return true;
                                }
                            }
                            this.mTimeShiftManager.jumpToNext();
                            return true;
                        case 88:
                            setVisibility(0);
                            startTimeout(10000);
                            if (this.mTimeShiftManager.isForwarding()) {
                                int displayedPlaySpeed3 = this.mTimeShiftManager.getDisplayedPlaySpeed();
                                TifTimeShiftManager tifTimeShiftManager3 = this.mTimeShiftManager;
                                if (displayedPlaySpeed3 != 1) {
                                    showToast(true);
                                    return true;
                                }
                            }
                            if (this.mTimeShiftManager.isRewinding()) {
                                int displayedPlaySpeed4 = this.mTimeShiftManager.getDisplayedPlaySpeed();
                                TifTimeShiftManager tifTimeShiftManager4 = this.mTimeShiftManager;
                                if (displayedPlaySpeed4 != 1) {
                                    showToast(false);
                                    return true;
                                }
                            }
                            this.mTimeShiftManager.jumpToPrevious();
                            return true;
                        case 89:
                            MtkLog.d(TAG, "rewind mTFCallbackPlaybackStatus:" + this.mTFCallbackPlaybackStatus);
                            if (this.mTFCallbackPlaybackStatus == 0 || CommonIntegration.getInstance().is3rdTVSource()) {
                                setVisibility(0);
                                startTimeout(10000);
                                dismissGingaDialog();
                                dismissGingaloadingDialog();
                                this.mTimeShiftManager.rewind();
                                return true;
                            }
                            MtkLog.d(TAG, "Rewind timeshift status error and return operation.");
                            return true;
                        case 90:
                            MtkLog.d(TAG, "fastforward mTFCallbackPlaybackStatus:" + this.mTFCallbackPlaybackStatus);
                            if (this.mTFCallbackPlaybackStatus == 0 || CommonIntegration.getInstance().is3rdTVSource()) {
                                setVisibility(0);
                                startTimeout(10000);
                                dismissGingaDialog();
                                dismissGingaloadingDialog();
                                this.mTimeShiftManager.fastForward();
                                return true;
                            }
                            MtkLog.d(TAG, "Fastforward timeshift status error and return operation.");
                            return true;
                        default:
                            switch (keyCode) {
                                case 126:
                                    MtkLog.d(TAG, "play mTFCallbackPlaybackStatus:" + this.mTFCallbackPlaybackStatus);
                                    if (this.mTFCallbackPlaybackStatus != 0 && !CommonIntegration.getInstance().is3rdTVSource()) {
                                        MtkLog.d(TAG, "Play timeshift status error and return operation.");
                                        return true;
                                    } else if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
                                        this.mPlayFNum.setVisibility(4);
                                        setVisibility(0);
                                        if (this.mTimeShiftManager.getPlayStatus() == 0) {
                                            startTimeout(10000);
                                        } else {
                                            stopTimeout();
                                        }
                                        dismissGingaDialog();
                                        dismissGingaloadingDialog();
                                        DvrManager.getInstance().speakText("timeshift play");
                                        this.mTimeShiftManager.play();
                                        return true;
                                    } else {
                                        MtkLog.d(TAG, "KeyHandler dvr running status:" + StateDvrPlayback.getInstance().isRunning());
                                        return false;
                                    }
                                case 127:
                                    MtkLog.d(TAG, "pause mTFCallbackPlaybackStatus:" + this.mTFCallbackPlaybackStatus);
                                    if (this.mTFCallbackPlaybackStatus != 0 && !CommonIntegration.getInstance().is3rdTVSource()) {
                                        MtkLog.d(TAG, "Pause timeshift status error and return operation.");
                                        return true;
                                    } else if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
                                        this.mPlayFNum.setVisibility(4);
                                        setVisibility(0);
                                        if (this.mTimeShiftManager.getPlayStatus() == 0) {
                                            startTimeout(10000);
                                        } else {
                                            stopTimeout();
                                        }
                                        dismissGingaDialog();
                                        dismissGingaloadingDialog();
                                        this.mTimeShiftManager.pause();
                                        return true;
                                    } else {
                                        MtkLog.d(TAG, "KeyHandler dvr running status:" + StateDvrPlayback.getInstance().isRunning());
                                        return false;
                                    }
                                default:
                                    switch (keyCode) {
                                        case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                                        case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                                            break;
                                        default:
                                            switch (keyCode) {
                                                case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
                                                    return true;
                                                case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
                                                    Toast.makeText(this.context, "timeshift recording!", 0).show();
                                                    return true;
                                            }
                                    }
                            }
                    }
                }
                MtkLog.d(TAG, "play/pause mTFCallbackPlaybackStatus:" + this.mTFCallbackPlaybackStatus);
                if (this.mTFCallbackPlaybackStatus != 0 && !CommonIntegration.getInstance().is3rdTVSource()) {
                    MtkLog.d(TAG, "Freeze/PlayPause timeshift status error and return operation.");
                    return true;
                } else if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
                    this.mPlayFNum.setVisibility(4);
                    setVisibility(0);
                    if (this.mTimeShiftManager.getPlayStatus() == 0) {
                        startTimeout(10000);
                    } else {
                        stopTimeout();
                    }
                    dismissGingaDialog();
                    dismissGingaloadingDialog();
                    this.mTimeShiftManager.togglePlayPause();
                    return true;
                } else {
                    MtkLog.d(TAG, "KeyHandler dvr running status:" + StateDvrPlayback.getInstance().isRunning());
                    return false;
                }
            } else if (CommonIntegration.getInstance().getLastChannelId() == CommonIntegration.getInstance().getCurrentChannelId()) {
                Toast.makeText(this.context, "No pre-channel can be switched!\n And TimeShift still recording.", 0).show();
                return true;
            }
            stopTifTimeShift();
        } else {
            MtkLog.d(TAG, "Press BACK key getVisibility(): " + getVisibility());
            if (getVisibility() != 0) {
                MtkLog.d(TAG, "Current timeshift view is not show, and rePress BACK key to stop timeshift play.");
                reset();
                this.mHandler.sendEmptyMessage(1001);
                return true;
            }
            this.mPlayFNum.setVisibility(4);
            setVisibility(8);
        }
        return super.KeyHandler(keyCode, event, fromNative);
    }

    private void dismissGingaDialog() {
        GingaTvDialog gingaTvDialog = (GingaTvDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_GINGA_TV);
        if (gingaTvDialog != null && gingaTvDialog.isVisible()) {
            gingaTvDialog.dismiss();
        }
    }

    private void dismissGingaloadingDialog() {
        InfoBarDialog infoBar = (InfoBarDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INFO_BAR);
        if (infoBar != null) {
            infoBar.handlerMessage(4);
        }
    }

    public void showToast(boolean isForwarding) {
        Toast toast;
        if (isForwarding) {
            toast = Toast.makeText(this.mContext, "Forwarding! Please try later.", 0);
        } else {
            toast = Toast.makeText(this.mContext, "Rewinding! Please try later.", 0);
        }
        toast.setGravity(17, 0, 0);
        toast.show();
    }

    public boolean initView() {
        View view = inflate(this.mContext, R.layout.tif_timeshift_layout, (ViewGroup) null);
        this.mPlayIcon = (ImageView) view.findViewById(R.id.tshift_plcontorl_btn);
        this.mPlayFNum = (ImageView) view.findViewById(R.id.tshift_plcontorl_btn_num);
        this.programStartTime = (TextView) view.findViewById(R.id.program_start_time);
        this.programEndtime = (TextView) view.findViewById(R.id.program_end_time);
        this.playingTime = (TextView) view.findViewById(R.id.tf_playing_time);
        this.start = (ImageView) view.findViewById(R.id.timeline_bg_start);
        this.watch = (ImageView) view.findViewById(R.id.watched);
        this.buffer = (ImageView) view.findViewById(R.id.buffered);
        this.end = (ImageView) view.findViewById(R.id.timeline_bg_end);
        this.indicator = (ImageView) view.findViewById(R.id.time_indicator);
        addView(view);
        this.mHandler = new InternalHandler(this);
        return super.initView();
    }

    public void setVisibility(int visibility) {
        MtkLog.d(TAG, "setVisibility-->visibility: " + visibility);
        MtkLog.printStackTrace();
        ArrayList<MountPoint> deviceList = DeviceManager.getInstance().getMountPointList();
        if (deviceList == null || deviceList.size() == 0) {
            visibility = 8;
        }
        if (deviceList.size() <= 1) {
            Iterator<MountPoint> it = deviceList.iterator();
            while (it.hasNext()) {
                MountPoint point = it.next();
                MtkLog.d(TAG, "mMountPoint: " + point.mMountPoint);
                File file = new File(point.mMountPoint + Core.TSHIFT_DIR_DISK);
                MtkLog.d(TAG, "tempIsTSHIFT: " + file.exists());
                if (!file.exists()) {
                    visibility = 8;
                }
            }
        }
        super.setVisibility(visibility);
    }

    public void stopTifTimeShift() {
        MtkLog.d(TAG, "stopTifTimeShift-->start.");
        if (this.mTimeShiftManager != null) {
            this.mTimeShiftManager.stop();
            SaveValue.getInstance(this.context).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
            SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
            reset();
            this.mPlayFNum.setVisibility(4);
            setVisibility(8);
        }
    }

    private void reset() {
        this.mProgramStartTimeMs = 0;
        this.mProgramEndTimeMs = 0;
        this.mTFCallbackPlaybackStatus = -1;
    }

    private void layoutProgress(View progress, long progressStartTimeMs2, long progressEndTimeMs) {
        int progressTime = Math.max(0, convertDurationToPixel(progressEndTimeMs - progressStartTimeMs2));
        MtkLog.d(TAG, "updateProgress-->layoutProgress progressTime: " + getTimeStringForLog((long) progressTime));
        layoutProgress(progress, progressTime);
    }

    private void layoutProgress(View progress, int width) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) progress.getLayoutParams();
        params.width = (width * 58) / 100;
        progress.setLayoutParams(params);
    }

    private int convertDurationToPixel(long duration) {
        if (this.mProgramEndTimeMs <= this.mProgramStartTimeMs) {
            return 0;
        }
        MtkLog.d(TAG, "updateProgress-->convertDurationToPixel duration: " + getTimeStringForLog(duration) + ", -to: " + getTimeStringForLog(this.mProgramEndTimeMs - this.mProgramStartTimeMs));
        MtkLog.d(TAG, "updateProgress-->duration: " + duration + ", -to: " + (this.mProgramEndTimeMs - this.mProgramStartTimeMs));
        int percent = (int) ((1000 * duration) / (this.mProgramEndTimeMs - this.mProgramStartTimeMs));
        StringBuilder sb = new StringBuilder();
        sb.append("updateProgress-->convertDurationToPixel percent: ");
        sb.append(percent);
        MtkLog.d(TAG, sb.toString());
        return percent;
    }

    /* access modifiers changed from: private */
    public void initializeTimeline() {
        TIFProgramInfo program = this.mTimeShiftManager.getProgramAt(this.mTimeShiftManager.getCurrentPositionMs());
        if (program != null) {
            this.mProgramStartTimeMs = program.getmStartTimeUtcSec();
            this.mProgramEndTimeMs = program.getmEndTimeUtcSec();
        }
        MtkLog.d(TAG, "initializeTimeline, program: " + program + ", " + getTimeStringForLog(this.mProgramStartTimeMs) + "," + getTimeStringForLog(this.mTimeShiftManager.getCurrentPositionMs()) + "," + getTimeStringForLog(this.mProgramEndTimeMs));
    }

    private void updateProgress() {
        long progressStartTimeMs2 = Math.min(this.mProgramEndTimeMs, Math.max(this.mProgramStartTimeMs, this.mTimeShiftManager.getRecordStartTimeMs()));
        long currentPlayingTimeMs = Math.min(this.mProgramEndTimeMs, Math.max(this.mProgramStartTimeMs, this.mTimeShiftManager.getCurrentPositionMs()));
        long progressEndTimeMs = Math.min(this.mProgramEndTimeMs, Math.max(this.mProgramStartTimeMs, this.mTimeShiftManager.getBroadcastTimeInUtcSeconds()));
        MtkLog.d(TAG, "updateProgress mProgramStartTimeMs: " + getTimeStringForLog(this.mProgramStartTimeMs) + "," + getTimeStringForLog(this.mProgramEndTimeMs));
        MtkLog.d(TAG, "updateProgress, " + getTimeStringForLog(progressStartTimeMs2) + "," + getTimeStringForLog(currentPlayingTimeMs) + "," + getTimeStringForLog(progressEndTimeMs) + "," + getTimeStringForLog(this.mTimeShiftManager.getRecordStartTimeMs()) + "," + getTimeStringForLog(this.mTimeShiftManager.getCurrentPositionMs()) + "," + getTimeStringForLog(this.mTimeShiftManager.getBroadcastTimeInUtcSeconds()));
        layoutProgress(this.start, this.mProgramStartTimeMs, progressStartTimeMs2);
        long j = currentPlayingTimeMs;
        layoutProgress(this.watch, progressStartTimeMs2, j);
        layoutProgress(this.buffer, j, progressEndTimeMs);
    }

    public void setListener() {
        this.mTimeShiftManager = TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager();
        this.mTimeShiftManager.setListener(new TifTimeShiftManager.Listener() {
            public void onAvailabilityChanged() {
                MtkLog.d(TifTimeshiftView.TAG, "onAvailabilityChanged");
                if (!TifTimeshiftView.this.mTimeShiftManager.isAvailable()) {
                    SaveValue.getInstance(TifTimeshiftView.this.mContext).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
                    SaveValue.saveWorldBooleanValue(TifTimeshiftView.this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
                    TifTimeshiftView.this.setVisibility(8);
                }
                TifTimeshiftView.this.initializeTimeline();
            }

            public void onPlayStatusChanged(int status) {
                MtkLog.d(TifTimeshiftView.TAG, "onPlayStatusChanged, status: " + status);
                int unused = TifTimeshiftView.this.mTFCallbackPlaybackStatus = status;
                if (TifTimeshiftView.this.mTimeShiftManager.isAvailable() && TifTimeshiftView.this.mTimeShiftManager.getAvailabilityChanged()) {
                    TifTimeshiftView.this.initializeTimeline();
                    TifTimeshiftView.this.updateAll();
                }
                if (status == 4) {
                    Toast.makeText(TifTimeshiftView.this.context, "Timeshift record full, and auto play.", 0).show();
                }
            }

            public void onRecordStartTimeChanged() {
                MtkLog.d(TifTimeshiftView.TAG, "onRecordStartTimeChanged");
                if (TifTimeshiftView.this.mTimeShiftManager.getmPlaybackSpeed() == 1) {
                    TifTimeshiftView.this.mPlayFNum.setVisibility(4);
                }
                if (TifTimeshiftView.this.mTimeShiftManager.isAvailable()) {
                    TifTimeshiftView.this.initializeTimeline();
                    TifTimeshiftView.this.updateAll();
                }
            }

            public void onCurrentPositionChanged() {
                MtkLog.d(TifTimeshiftView.TAG, "onCurrentPositionChanged");
                if (TifTimeshiftView.this.mTimeShiftManager.isAvailable()) {
                    TifTimeshiftView.this.updateAll();
                }
            }

            public void onProgramInfoChanged() {
                MtkLog.d(TifTimeshiftView.TAG, "onProgramInfoChanged");
                if (TifTimeshiftView.this.mTimeShiftManager.isAvailable()) {
                    TifTimeshiftView.this.initializeTimeline();
                    TifTimeshiftView.this.updateAll();
                }
            }

            public void onActionEnabledChanged(int actionId, boolean enabled) {
                MtkLog.d(TifTimeshiftView.TAG, "onActionEnabledChanged");
            }

            public void onSpeedChange(float speed) {
                MtkLog.d(TifTimeshiftView.TAG, "speed====" + speed);
                if (speed == 1.0f) {
                    TifTimeshiftView.this.mPlayFNum.setVisibility(4);
                    TifTimeshiftView.this.mPlayIcon.setImageResource(R.drawable.timeshift_play);
                } else if (speed == 0.0f) {
                    TifTimeshiftView.this.mPlayFNum.setVisibility(4);
                    TifTimeshiftView.this.mPlayIcon.setImageResource(R.drawable.timshift_pasuse);
                } else if (speed > 0.0f) {
                    TifTimeshiftView.this.showFastPlayInfo2(true, (int) speed);
                } else {
                    TifTimeshiftView.this.showFastPlayInfo2(false, (int) speed);
                }
            }

            public void onError(int errId) {
                if (errId == 1) {
                    TifTimeshiftView.this.mHandler.sendEmptyMessage(1001);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateAll() {
        updateTime();
        updateProgress();
        updateRecTimeText();
        updateButtons();
    }

    private void updateButtons() {
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            MtkLog.d(TAG, "updateButtons");
            int speed = this.mTimeShiftManager.getmPlaybackSpeed();
            int playStatus = this.mTimeShiftManager.getPlayStatus();
            int playDirection = this.mTimeShiftManager.getPlayDirection();
            MtkLog.d(TAG, "updateButtons speed: " + speed + ", playStatus: " + playStatus);
            if (speed == 1) {
                this.mPlayFNum.setVisibility(4);
                if (playStatus == 1) {
                    this.mPlayIcon.setImageResource(R.drawable.timeshift_play);
                } else {
                    this.mPlayIcon.setImageResource(R.drawable.timshift_pasuse);
                }
            } else if (playDirection == 1) {
                showFastPlayInfo2(false, speed);
            } else {
                showFastPlayInfo2(true, speed);
            }
        }
    }

    private void updateTime() {
        long currentPositionMs = this.mTimeShiftManager.getCurrentPositionMs();
        this.playingTime.setText(getTimeString(currentPositionMs));
        int currentTimePositionPixel = convertDurationToPixel(currentPositionMs - this.mProgramStartTimeMs);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.indicator.getLayoutParams();
        params.setMarginStart((currentTimePositionPixel * 58) / 100);
        MtkLog.d(TAG, params.width + " updateTime, " + currentPositionMs + "," + currentTimePositionPixel + "," + params.leftMargin);
        this.indicator.setLayoutParams(params);
    }

    private void updateRecTimeText() {
        MtkLog.d(TAG, "updateRecTimeText, " + getTimeStringForLog(this.mProgramStartTimeMs) + "," + getTimeStringForLog(this.mProgramEndTimeMs));
        this.programStartTime.setText(getTimeString(this.mProgramStartTimeMs));
        this.programEndtime.setText(getTimeString(this.mProgramEndTimeMs));
    }

    private String getTimeStringForLog(long timeMs) {
        return new SimpleDateFormat("HH:mm:ss").format(Long.valueOf(timeMs));
    }

    private String getTimeString(long timeMs) {
        return DateFormat.getTimeFormat(getContext()).format(Long.valueOf(timeMs));
    }

    public void showFastPlayInfo2(boolean FastForward, int speed) {
        MtkLog.d(TAG, "showFastPlayInfo2 FastForward: " + FastForward + ", speed: " + speed);
        this.mPlayFNum.setVisibility(0);
        if (FastForward) {
            this.mPlayIcon.setImageResource(R.drawable.timeshift_ff);
            onFast2(speed, 0, 1);
            return;
        }
        this.mPlayIcon.setImageResource(R.drawable.timeshift_fb);
        onFast2(speed, 1, 1);
    }

    public void onFast2(int speed, int status, int type) {
        MtkLog.d(TAG, "onFast2 speed: " + speed);
        if (speed == 1) {
            this.mPlayFNum.setVisibility(4);
            this.mPlayIcon.setImageResource(R.drawable.timeshift_play);
            return;
        }
        int switchValue = speed;
        if (switchValue < 0) {
            switchValue = -switchValue;
        }
        if (switchValue == 4) {
            this.mPlayFNum.setImageResource(R.drawable.timeshift_f_four);
        } else if (switchValue == 8) {
            this.mPlayFNum.setImageResource(R.drawable.timeshift_f_eight);
        } else if (switchValue == 16) {
            this.mPlayFNum.setImageResource(R.drawable.timeshift_f_six);
        } else if (switchValue != 32) {
            switch (switchValue) {
                case 1:
                    this.mPlayFNum.setImageResource(R.drawable.timeshift_f_one);
                    return;
                case 2:
                    this.mPlayFNum.setImageResource(R.drawable.timeshift_f_two);
                    return;
                default:
                    return;
            }
        } else {
            this.mPlayFNum.setImageResource(R.drawable.timeshift_f_more);
        }
    }

    private static class InternalHandler extends WeakHandler<TifTimeshiftView> {
        public InternalHandler(TifTimeshiftView ref) {
            super(ref);
        }

        public void handleMessage(Message msg, @NonNull TifTimeshiftView timeShiftview) {
            if (msg.what == 1001) {
                DvrManager.getInstance().speakText("timeshift stop");
                timeShiftview.mTimeShiftManager.stop();
                SaveValue.getInstance(timeShiftview.context).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
                SaveValue.saveWorldBooleanValue(timeShiftview.context, MenuConfigManager.TIMESHIFT_START, false, false);
                timeShiftview.mPlayFNum.setVisibility(4);
                timeShiftview.setVisibility(8);
            }
        }
    }
}
