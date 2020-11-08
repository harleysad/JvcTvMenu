package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.PlaybackParams;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.wwtv.setting.util.LanguageUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.manager.Util;
import com.mediatek.wwtv.tvcenter.dvr.ui.DVRControlbar;
import com.mediatek.wwtv.tvcenter.dvr.ui.PinDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.MiscView;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.TwinkleView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.AudioBTManager;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.SomeArgs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StateDvrPlayback extends StateBase {
    private static final String TAG = "StateDvrPlayback";
    public static StateDvrPlayback mStateDvrPlayback;
    private final int MSG_CTRLBAR_DISMISS = EPGConfig.EPG_SHOW_LOCK_ICON;
    private final int MSG_DELAY_TTS = EPGConfig.EPG_UPDATE_API_EVENT_LIST;
    private final int MSG_REFRESH_TIME = EPGConfig.EPG_SELECT_CHANNEL_COMPLETE;
    private final int MSG_SPEAK_DVR_PLAY = EPGConfig.EPG_INIT_EVENT_LIST;
    private TextView audioName_tv;
    private String audioTrackId;
    /* access modifiers changed from: private */
    public int audioTrackIndex = 1;
    private List<TvTrackInfo> audioTrackInfos = null;
    private String audioTrackName;
    private final CallbackHandler callbackHandler = new CallbackHandler(this);
    /* access modifiers changed from: private */
    public PinDialog dialog;
    /* access modifiers changed from: private */
    public long fileCurrentTime;
    /* access modifiers changed from: private */
    public long fileHistoryTime;
    private String fileName;
    /* access modifiers changed from: private */
    public float filePlaySpeed;
    /* access modifiers changed from: private */
    public long fileTotalTime;
    private Uri fileUri;
    private ImageView forwardSpeed_iv;
    private ImageView forward_iv;
    /* access modifiers changed from: private */
    public boolean isFForFR = false;
    /* access modifiers changed from: private */
    public boolean isPlayStart = false;
    /* access modifiers changed from: private */
    public boolean isPlaying = true;
    /* access modifiers changed from: private */
    public boolean isReplayOK = true;
    /* access modifiers changed from: private */
    public boolean isSForSR = false;
    /* access modifiers changed from: private */
    public boolean isUnlockPin = false;
    private boolean iskeywithback = false;
    private DVRControlbar mControlbar;
    /* access modifiers changed from: private */
    public DvrPlaybackHnadler mHandler;
    private LanguageUtil mLanguageUtil;
    /* access modifiers changed from: private */
    public int mSpeedStepTemp;
    private final TvView mTvView = null;
    private Message msg;
    private TextView name_tv;
    private ImageView playOrPause_iv;
    private ProgressBar progressBar;
    private ImageView rewindSpeed_iv;
    private ImageView rewind_iv;
    /* access modifiers changed from: private */
    public boolean showAudioTrackFirst = true;
    private String subTitleTrackId;
    private List<TvTrackInfo> subTitleTrackInfos = null;
    private String subTitleTrackName = "";
    private TextView subtitleName_tv;
    /* access modifiers changed from: private */
    public int subtitleTrackIndex = 0;
    private TextView time_tv;

    private StateDvrPlayback(Context context, DvrManager manager) {
        super(context, manager);
        setType(StatusType.PLAYBACK);
        this.mLanguageUtil = new LanguageUtil(this.mContext);
        if (this.callbackHandler == null) {
            MtkLog.w(TAG, "callbackHandler = null");
            return;
        }
        MtkLog.w(TAG, "callbackHandler = " + this.callbackHandler);
    }

    public static StateDvrPlayback getInstance() {
        return mStateDvrPlayback;
    }

    public static StateDvrPlayback getInstance(Context context, DvrManager dvrManager) {
        if (mStateDvrPlayback == null) {
            dvrManager = DvrManager.getInstance();
        }
        mStateDvrPlayback = new StateDvrPlayback(context, dvrManager);
        return mStateDvrPlayback;
    }

    public static StateDvrPlayback getInstance(DvrManager dvrManager) {
        mStateDvrPlayback = new StateDvrPlayback(dvrManager.getContext(), dvrManager);
        return mStateDvrPlayback;
    }

    private TvSurfaceView getTvView() {
        return TurnkeyUiMainActivity.getInstance().getTvView();
    }

    private void initBigCtrlBar() {
        this.mControlbar = new DVRControlbar((Activity) this.mContext, R.layout.dvr_playback_ctrlbar, Long.valueOf(MessageType.delayMillis10), mStateDvrPlayback);
        this.name_tv = (TextView) findView(R.id.name_tv);
        this.time_tv = (TextView) findView(R.id.time_tv);
        this.rewind_iv = (ImageView) findView(R.id.rewind_iv);
        this.rewindSpeed_iv = (ImageView) findView(R.id.rewindSpeed_iv);
        this.playOrPause_iv = (ImageView) findView(R.id.playOrPause_iv);
        this.forward_iv = (ImageView) findView(R.id.forward_iv);
        this.forwardSpeed_iv = (ImageView) findView(R.id.forwardSpeed_iv);
        this.progressBar = (ProgressBar) findView(R.id.progressBar);
        this.subtitleName_tv = (TextView) findView(R.id.subtitleName_tv);
        this.audioName_tv = (TextView) findView(R.id.audioName_tv);
        this.progressBar.incrementProgressBy(1);
        this.progressBar.setProgress(0);
        this.rewind_iv.setVisibility(4);
        this.rewindSpeed_iv.setVisibility(4);
        this.forward_iv.setVisibility(4);
        this.forwardSpeed_iv.setVisibility(4);
        this.playOrPause_iv.setVisibility(0);
        this.playOrPause_iv.setImageResource(R.drawable.timeshift_play);
        this.subtitleName_tv.setVisibility(4);
        this.audioName_tv.setVisibility(4);
        showBigCtrlBar();
    }

    private View findView(int id) {
        return this.mControlbar.getContentView().findViewById(id);
    }

    /* access modifiers changed from: private */
    public void showBigCtrlBar() {
        MtkLog.i(TAG, "showBigCtrlBar start--->");
        MtkLog.printStackTrace();
        if (!TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode()) {
            if (this.mControlbar == null) {
                initBigCtrlBar();
                return;
            }
            TextView textView = this.name_tv;
            textView.setText(this.mContext.getResources().getString(R.string.dvr_playback_name) + this.fileName);
            if (this.fileCurrentTime == 0 && this.fileTotalTime == 0) {
                this.time_tv.setText("00:00:00/00:00:00");
                this.progressBar.setProgress(0);
            }
            this.subtitleName_tv.setVisibility(0);
            this.subtitleName_tv.setText(this.subTitleTrackName);
            if (this.audioTrackName != null && !this.audioTrackName.isEmpty()) {
                this.audioName_tv.setVisibility(0);
                this.audioName_tv.setText(this.audioTrackName);
            }
            if (this.isSForSR) {
                int i = this.mSpeedStepTemp;
                if (i == -32 || i == -16 || i == -8 || i == -4 || i == -2) {
                    this.rewind_iv.setVisibility(0);
                    this.rewindSpeed_iv.setVisibility(0);
                    this.forward_iv.setVisibility(4);
                    this.forwardSpeed_iv.setVisibility(4);
                    this.playOrPause_iv.setVisibility(4);
                    if (this.mSpeedStepTemp == -2) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_sf_2);
                    } else if (this.mSpeedStepTemp == -4) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_sf_4);
                    } else if (this.mSpeedStepTemp == -8) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_sf_8);
                    } else if (this.mSpeedStepTemp == -16) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_sf_16);
                    } else if (this.mSpeedStepTemp == -32) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_sf_32);
                    }
                } else {
                    if (!(i == 4 || i == 8 || i == 16 || i == 32)) {
                        switch (i) {
                            case 0:
                            case 1:
                                this.rewind_iv.setVisibility(4);
                                this.rewindSpeed_iv.setVisibility(4);
                                this.forward_iv.setVisibility(4);
                                this.forwardSpeed_iv.setVisibility(4);
                                this.playOrPause_iv.setVisibility(0);
                                if (this.filePlaySpeed != 1.0f) {
                                    if (this.filePlaySpeed == 0.0f) {
                                        this.playOrPause_iv.setImageResource(R.drawable.timshift_pasuse);
                                        break;
                                    }
                                } else {
                                    this.playOrPause_iv.setImageResource(R.drawable.timeshift_play);
                                    break;
                                }
                                break;
                            case 2:
                                break;
                        }
                    }
                    this.rewind_iv.setVisibility(4);
                    this.rewindSpeed_iv.setVisibility(4);
                    this.forward_iv.setVisibility(0);
                    this.forwardSpeed_iv.setVisibility(0);
                    this.playOrPause_iv.setVisibility(4);
                    if (this.mSpeedStepTemp == 2) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_sf_2);
                    } else if (this.mSpeedStepTemp == 4) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_sf_4);
                    } else if (this.mSpeedStepTemp == 8) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_sf_8);
                    } else if (this.mSpeedStepTemp == 16) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_sf_16);
                    } else if (this.mSpeedStepTemp == 32) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_sf_32);
                    }
                }
            } else {
                int i2 = this.mSpeedStepTemp;
                if (i2 == -32 || i2 == -16 || i2 == -8 || i2 == -4 || i2 == -2) {
                    this.rewind_iv.setVisibility(0);
                    this.rewindSpeed_iv.setVisibility(0);
                    this.forward_iv.setVisibility(4);
                    this.forwardSpeed_iv.setVisibility(4);
                    this.playOrPause_iv.setVisibility(4);
                    if (this.mSpeedStepTemp == -2) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_f_two);
                    } else if (this.mSpeedStepTemp == -4) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_f_four);
                    } else if (this.mSpeedStepTemp == -8) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_f_eight);
                    } else if (this.mSpeedStepTemp == -16) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_f_six);
                    } else if (this.mSpeedStepTemp == -32) {
                        this.rewindSpeed_iv.setImageResource(R.drawable.timeshift_f_more);
                    }
                } else {
                    if (!(i2 == 4 || i2 == 8 || i2 == 16 || i2 == 32)) {
                        switch (i2) {
                            case 0:
                            case 1:
                                this.rewind_iv.setVisibility(4);
                                this.rewindSpeed_iv.setVisibility(4);
                                this.forward_iv.setVisibility(4);
                                this.forwardSpeed_iv.setVisibility(4);
                                this.playOrPause_iv.setVisibility(0);
                                if (this.filePlaySpeed != 1.0f) {
                                    if (this.filePlaySpeed == 0.0f) {
                                        this.playOrPause_iv.setImageResource(R.drawable.timshift_pasuse);
                                        break;
                                    }
                                } else {
                                    this.playOrPause_iv.setImageResource(R.drawable.timeshift_play);
                                    break;
                                }
                                break;
                            case 2:
                                break;
                        }
                    }
                    this.rewind_iv.setVisibility(4);
                    this.rewindSpeed_iv.setVisibility(4);
                    this.forward_iv.setVisibility(0);
                    this.forwardSpeed_iv.setVisibility(0);
                    this.playOrPause_iv.setVisibility(4);
                    if (this.mSpeedStepTemp == 2) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_f_two);
                    } else if (this.mSpeedStepTemp == 4) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_f_four);
                    } else if (this.mSpeedStepTemp == 8) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_f_eight);
                    } else if (this.mSpeedStepTemp == 16) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_f_six);
                    } else if (this.mSpeedStepTemp == 32) {
                        this.forwardSpeed_iv.setImageResource(R.drawable.timeshift_f_more);
                    }
                }
            }
            this.mControlbar.show();
            this.mHandler.removeMessages(EPGConfig.EPG_SHOW_LOCK_ICON);
            this.msg = this.mHandler.obtainMessage(EPGConfig.EPG_SHOW_LOCK_ICON);
            this.mHandler.sendMessageDelayed(this.msg, MessageType.delayMillis10);
        }
    }

    public void dismissBigCtrlBar() {
        MtkLog.d(TAG, "dismissBigCtrlBar-->");
        if (this.mControlbar != null && this.mControlbar.isShowing()) {
            this.mControlbar.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void refreshTime() {
        updateTimeProgressBar();
        this.msg = this.mHandler.obtainMessage(EPGConfig.EPG_SELECT_CHANNEL_COMPLETE);
        this.mHandler.sendMessageDelayed(this.msg, 1000);
    }

    private void updateTimeProgressBar() {
        StringBuilder sb = new StringBuilder();
        String tt = Util.longStrToTimeStr(Long.valueOf(this.fileTotalTime));
        String cr = Util.longStrToTimeStr(Long.valueOf(this.fileCurrentTime));
        sb.append("[");
        sb.append(cr);
        sb.append("/");
        sb.append(tt);
        sb.append("]");
        MtkLog.i(TAG, "controlBarInfo==> time*** " + sb.toString());
        this.time_tv.setText(sb.toString());
        if (this.fileTotalTime != 0) {
            int pro = (int) Math.floor((double) ((100 * this.fileCurrentTime) / this.fileTotalTime));
            MtkLog.i(TAG, "pro=" + pro);
            this.progressBar.setProgress(pro);
            return;
        }
        this.progressBar.setProgress(0);
    }

    public boolean onKeyDown(int keycode) {
        MtkLog.d(TAG, "keycode==" + keycode);
        if (!this.isReplayOK) {
            getManager().showContinueToSeekNotSupport();
            return true;
        } else if (!this.isUnlockPin || keycode == 86 || keycode == 4) {
            switch (keycode) {
                case 4:
                case 86:
                    this.iskeywithback = true;
                    stopDvrFilePlay();
                    ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER).startComponent();
                    return true;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 19:
                case 20:
                case 130:
                case KeyMap.KEYCODE_MTKIR_INFO /*165*/:
                case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
                case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
                case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
                case 10062:
                case KeyMap.KEYCODE_MTKIR_FREEZE /*10467*/:
                    return true;
                case 21:
                    if (TurnkeyUiMainActivity.getInstance().getTvOptionsManager().isShowing()) {
                        MtkLog.i(TAG, "Menu is show, and can not seek left pvr.");
                        dismissBigCtrlBar();
                        break;
                    } else if (prepareSeekAction()) {
                        return true;
                    } else {
                        if (this.fileCurrentTime - MessageType.delayMillis3 <= 0) {
                            this.fileCurrentTime = 0;
                        } else {
                            this.fileCurrentTime -= MessageType.delayMillis3;
                        }
                        this.isReplayOK = false;
                        seekDvrFilePlay(this.fileCurrentTime);
                        showBigCtrlBar();
                        return true;
                    }
                case 22:
                    if (TurnkeyUiMainActivity.getInstance().getTvOptionsManager().isShowing()) {
                        MtkLog.i(TAG, "Menu is show, and can not seek right pvr.");
                        dismissBigCtrlBar();
                        break;
                    } else if (prepareSeekAction()) {
                        return true;
                    } else {
                        if (this.fileCurrentTime + MessageType.delayMillis3 > this.fileTotalTime) {
                            DvrManager manager = getManager();
                            getManager();
                            manager.showPromptInfo(DvrManager.PRO_PVR_PLAY_SEEK);
                        } else {
                            this.fileCurrentTime += MessageType.delayMillis3;
                            this.isReplayOK = false;
                            seekDvrFilePlay(this.fileCurrentTime);
                        }
                        showBigCtrlBar();
                        return true;
                    }
                case 23:
                case 85:
                case 126:
                case 127:
                    if (!this.isPlayStart) {
                        playDvrFile(this.fileUri);
                    } else if (this.isPlaying && ((!this.isFForFR && !this.isSForSR) || keycode == 127)) {
                        pauseDvrFilePlay();
                    } else if (this.isFForFR || this.isSForSR) {
                        this.filePlaySpeed = 1.0f;
                        this.mSpeedStepTemp = 1;
                        speakText("dvr play");
                        forwardDvrFilePlay(this.filePlaySpeed);
                    } else {
                        reSumeDvrFilePlay();
                    }
                    this.isFForFR = false;
                    this.isSForSR = false;
                    dismissSundryView();
                    dismissMiscView();
                    showBigCtrlBar();
                    return true;
                case 82:
                case 222:
                case KeyMap.KEYCODE_MTKIR_SLEEP /*223*/:
                case 251:
                case 255:
                case 10065:
                case 10066:
                case 10471:
                    dismissBigCtrlBar();
                    return false;
                case 89:
                    if (this.filePlaySpeed >= -1.0f) {
                        this.filePlaySpeed = -2.0f;
                        this.mSpeedStepTemp = -2;
                    } else if (this.filePlaySpeed * 2.0f > -64.0f) {
                        this.filePlaySpeed *= 2.0f;
                        this.mSpeedStepTemp *= 2;
                    } else {
                        this.filePlaySpeed = 1.0f;
                        this.mSpeedStepTemp = 1;
                    }
                    this.isFForFR = true;
                    this.isSForSR = false;
                    this.isPlaying = true;
                    speakText("dvr rewind");
                    rewindDvrFilePlay(this.filePlaySpeed);
                    dismissMiscView();
                    dismissSundryView();
                    return true;
                case 90:
                    if (this.filePlaySpeed <= 1.0f) {
                        this.filePlaySpeed = 2.0f;
                        this.mSpeedStepTemp = 2;
                    } else if (this.filePlaySpeed * 2.0f < 64.0f) {
                        this.filePlaySpeed *= 2.0f;
                        this.mSpeedStepTemp *= 2;
                    } else {
                        this.filePlaySpeed = 1.0f;
                        this.mSpeedStepTemp = 1;
                    }
                    this.isFForFR = true;
                    this.isSForSR = false;
                    this.isPlaying = true;
                    speakText("dvr forward");
                    forwardDvrFilePlay(this.filePlaySpeed);
                    dismissMiscView();
                    dismissSundryView();
                    return true;
                case 93:
                    if (this.mSpeedStepTemp <= 0 || this.filePlaySpeed > 1.0f) {
                        this.mSpeedStepTemp = 1;
                        this.mSpeedStepTemp <<= 1;
                    } else if (this.mSpeedStepTemp * 2 < 64) {
                        this.mSpeedStepTemp <<= 1;
                    } else {
                        this.mSpeedStepTemp = 1;
                    }
                    int i = this.mSpeedStepTemp;
                    if (i == 2) {
                        this.filePlaySpeed = 0.5f;
                    } else if (i == 4) {
                        this.filePlaySpeed = 0.25f;
                    } else if (i == 8) {
                        this.filePlaySpeed = 0.125f;
                    } else if (i == 16) {
                        this.filePlaySpeed = 0.0625f;
                    } else if (i != 32) {
                        this.filePlaySpeed = 1.0f;
                    } else {
                        this.filePlaySpeed = 0.03125f;
                    }
                    this.isSForSR = true;
                    this.isFForFR = false;
                    this.isPlaying = true;
                    speakText("dvr slow forward");
                    forwardDvrFilePlay(this.filePlaySpeed);
                    dismissMiscView();
                    dismissSundryView();
                    return true;
                case KeyMap.KEYCODE_MTKIR_MUTE /*164*/:
                    return false;
                case KeyMap.KEYCODE_MTKIR_MTKIR_CC /*175*/:
                case 215:
                    MtkLog.i(TAG, "keycode KEYCODE_MTKIR_MTKIR_CC  or  KEYCODE_MTKIR_SUBTITLE");
                    switchCc();
                    showBigCtrlBar();
                    return true;
                case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                    if (this.mSpeedStepTemp >= 0 || this.filePlaySpeed < -1.0f) {
                        this.mSpeedStepTemp = -1;
                        this.mSpeedStepTemp <<= 1;
                    } else if (this.mSpeedStepTemp * 2 > -64) {
                        this.mSpeedStepTemp <<= 1;
                    } else {
                        this.mSpeedStepTemp = 1;
                    }
                    int i2 = this.mSpeedStepTemp;
                    if (i2 == -32) {
                        this.filePlaySpeed = -0.03125f;
                    } else if (i2 == -16) {
                        this.filePlaySpeed = -0.0625f;
                    } else if (i2 == -8) {
                        this.filePlaySpeed = -0.125f;
                    } else if (i2 == -4) {
                        this.filePlaySpeed = -0.25f;
                    } else if (i2 != -2) {
                        this.filePlaySpeed = 1.0f;
                    } else {
                        this.filePlaySpeed = -0.5f;
                    }
                    this.isSForSR = true;
                    this.isFForFR = false;
                    this.isPlaying = true;
                    speakText("dvr slow rewind");
                    rewindDvrFilePlay(this.filePlaySpeed);
                    dismissMiscView();
                    dismissSundryView();
                    return true;
                case 213:
                    MtkLog.i(TAG, " KEYCODE_MTKIR_MTSAUDIO ");
                    switchAudioTrack();
                    dismissSundryView();
                    dismissMiscView();
                    showBigCtrlBar();
                    return true;
                case KeyMap.KEYCODE_MTKIR_MTKIR_TTX /*233*/:
                    return true;
            }
            return super.onKeyDown(keycode);
        } else {
            this.dialog.show();
            return true;
        }
    }

    private void dismissSundryView() {
        SundryShowTextView sundryShowTextView = (SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
        if (sundryShowTextView != null && sundryShowTextView.isVisible()) {
            sundryShowTextView.setVisibility(8);
        }
    }

    private void dismissMiscView() {
        MiscView miscView = (MiscView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_MISC);
        if (miscView != null && miscView.isVisible()) {
            miscView.setVisibility(8);
        }
    }

    public void prepareDvrFilePlay(DVRFiles dvrFiles) {
        getController().addEventHandler(this.callbackHandler);
        this.mHandler = new DvrPlaybackHnadler();
        setRunning(true);
        getDvrBaseInfo(dvrFiles);
        getDvrFileState();
        if (isNeedLastMemory()) {
            MtkLog.i(TAG, "Black screen when seek pvr.");
            TurnkeyUiMainActivity.getInstance().getBlockScreenView().setVisibility(0, 4);
        }
        isNeedLastMemory();
        playDvrFile(this.fileUri);
    }

    public boolean isNeedLastMemory() {
        return this.fileHistoryTime != 0 && this.fileHistoryTime < this.fileTotalTime;
    }

    private void playDvrFile(Uri recordedProgramUri) {
        MtkLog.i(TAG, "playDvrFile play dvr");
        if (!this.mHandler.hasMessages(EPGConfig.EPG_INIT_EVENT_LIST)) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(EPGConfig.EPG_INIT_EVENT_LIST), 500);
        }
        SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.PVR_PLAYBACK_START, true, false);
        getController().dvrPlay(recordedProgramUri, isNeedLastMemory());
        new Handler().post(new Runnable() {
            public void run() {
                AudioBTManager.getInstance(StateDvrPlayback.this.mContext).creatAudioPatch();
            }
        });
    }

    private void pauseDvrFilePlay() {
        MtkLog.i(TAG, "pause dvr pause");
        this.filePlaySpeed = 0.0f;
        this.mSpeedStepTemp = 0;
        speakText("dvr pause");
        getController().dvrPause();
        this.isPlaying = false;
    }

    /* access modifiers changed from: private */
    public void reSumeDvrFilePlay() {
        MtkLog.i(TAG, " resume dvr file");
        speakText("dvr play");
        this.filePlaySpeed = 1.0f;
        this.mSpeedStepTemp = 1;
        showBigCtrlBar();
        getController().dvrResume();
        this.isPlaying = true;
    }

    public void stopDvrFilePlay() {
        MtkLog.i(TAG, "stop dvr stop");
        saveStopMessages();
        if (TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode()) {
            TurnkeyUiMainActivity.getInstance().finish();
        }
        TurnkeyUiMainActivity.getInstance().getBlockScreenView().setVisibility(8, 4);
        if (!DestroyApp.isCurActivityTkuiMainActivity() && this.iskeywithback) {
            MtkLog.i(TAG, "Current Activity is not TkuiMainActivity.");
            TurnkeyUiMainActivity.getInstance();
            TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
            this.iskeywithback = false;
        }
        String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName("main");
        int detail = InputSourceManager.getInstance().changeCurrentInputSourceByName(sourceName);
        MtkLog.i(TAG, "sourceName ==" + sourceName + "  detail ==" + detail);
    }

    public void saveStopMessages() {
        if (this.dialog != null) {
            this.dialog.dismiss();
        }
        setDvrFileState(this.fileUri, this.fileCurrentTime);
        MtkLog.i(TAG, "setDvrFileState == " + this.fileUri + " -- " + this.fileCurrentTime);
        this.isPlaying = false;
        this.isFForFR = false;
        this.isSForSR = false;
        this.fileCurrentTime = 0;
        dismissBigCtrlBar();
        getManager().speakText("dvr stop");
        getController().dvrStop();
        this.mHandler.removeCallbacksAndMessages((Object) null);
        getController().removeEventHandler(this.callbackHandler);
        setRunning(false);
        getManager().restoreToDefault((StateBase) mStateDvrPlayback);
        restoreToDefault();
        this.fileHistoryTime = 0;
        this.fileTotalTime = 0;
        SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.PVR_PLAYBACK_START, false, false);
    }

    private void forwardDvrFilePlay(float speed) {
        PlaybackParams params = new PlaybackParams();
        params.setSpeed(speed);
        MtkLog.i(TAG, "forward play  speed==" + speed);
        getController().dvrPlaybackParams(params);
    }

    private void rewindDvrFilePlay(float speed) {
        MtkLog.i(TAG, "rewind play  speed==" + speed);
        forwardDvrFilePlay(speed);
    }

    /* access modifiers changed from: private */
    public void seekDvrFilePlay(long timeMs) {
        MtkLog.i(TAG, "seek dvr play  timeMs==" + timeMs);
        getController().dvrSeekTo(timeMs);
    }

    private void getDvrBaseInfo(DVRFiles dvrFiles) {
        this.fileUri = dvrFiles.getProgarmUri();
        this.fileName = dvrFiles.getProgramName();
        this.fileHistoryTime = 0;
        this.filePlaySpeed = 1.0f;
        this.mSpeedStepTemp = 1;
        this.fileTotalTime = dvrFiles.getDuration() * 1000;
        this.isPlaying = true;
        this.isFForFR = false;
        this.isSForSR = false;
        MtkLog.i(TAG, "getDvrBaseInfo :==>  fileUri= " + this.fileUri + " fileName== " + this.fileName + " fileTotalTime== " + this.fileTotalTime + " dvrFiles.getDurationStr()== " + dvrFiles.getDurationStr());
    }

    private void setDvrFileState(Uri uri, long currentTime) {
        if (!this.isUnlockPin) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);
            int max = Integer.MAX_VALUE;
            int is = 0;
            int max2 = 0;
            while (true) {
                int i = max2;
                if (i < 5) {
                    String dvr1 = sp.getString("DVRPLAYBACK" + i, "");
                    if (dvr1 == null || !dvr1.isEmpty()) {
                        String[] dvrs1 = dvr1.split(",");
                        int index = Integer.valueOf(dvrs1[0]).intValue();
                        if (Uri.parse(dvrs1[1]).equals(this.fileUri)) {
                            saveSharedP(i, index, uri, currentTime);
                            return;
                        }
                        int in = index;
                        if (max > in) {
                            is = i;
                            max = in;
                        }
                        max2 = i + 1;
                    } else {
                        saveSharedP(i, i, uri, currentTime);
                        return;
                    }
                } else {
                    saveSharedP(is, max + 5, uri, currentTime);
                    return;
                }
            }
        }
    }

    private void saveSharedP(int index, int number, Uri uri, long currentTime) {
        SharedPreferences.Editor edt = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edt.putString("DVRPLAYBACK" + index, String.valueOf(number) + "," + uri.toString() + "," + String.valueOf(currentTime));
        edt.commit();
    }

    private void getDvrFileState() {
        if (this.fileUri != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);
            int i = 0;
            while (i < 5) {
                String dvrList = sp.getString("DVRPLAYBACK" + String.valueOf(i), "");
                if (dvrList != null && !dvrList.isEmpty()) {
                    String[] abc = dvrList.split(",");
                    Uri uu = Uri.parse(abc[1]);
                    if (uu != null && uu.equals(this.fileUri)) {
                        this.fileHistoryTime = Long.valueOf(abc[2]).longValue();
                        if (this.fileHistoryTime >= this.fileTotalTime) {
                            this.fileHistoryTime = 0;
                        }
                        MtkLog.i(TAG, "getDvrFileState==>  index= " + i + " DVRPLAYBACK= " + String.valueOf(i) + " URi= " + uu + " fileHistoryTime = " + abc[2] + " and fileUri= " + this.fileUri);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private void switchCc() {
        if (this.subTitleTrackInfos == null || this.subTitleTrackInfos.size() <= 0) {
            MtkLog.e(TAG, "subtitleTackInfo == null !! ");
        } else if (this.subTitleTrackInfos.size() > this.subtitleTrackIndex) {
            this.subTitleTrackName = this.subTitleTrackInfos.get(this.subtitleTrackIndex).getLanguage();
            this.subTitleTrackId = this.subTitleTrackInfos.get(this.subtitleTrackIndex).getId();
            this.subTitleTrackName = (this.subtitleTrackIndex + 1) + "/" + this.subTitleTrackInfos.size() + " " + this.mLanguageUtil.getSubitleNameByValue(this.subTitleTrackName);
            getTvView().selectTrack(2, this.subTitleTrackId);
            MtkLog.i(TAG, "switchCCTrack ==true and subTitleTrackId== " + this.subTitleTrackId + ", subTitleTrackName: " + this.subTitleTrackName);
            this.subtitleTrackIndex = this.subtitleTrackIndex + 1;
        } else {
            this.subtitleTrackIndex = 0;
            this.subTitleTrackName = "";
            showBigCtrlBar();
            getTvView().setCaptionEnabled(false);
        }
    }

    private void switchAudioTrack() {
        if (this.audioTrackInfos == null || this.audioTrackInfos.size() <= 0) {
            MtkLog.e(TAG, "audioTackInfo == null !! ");
            return;
        }
        if (this.audioTrackInfos.size() <= this.audioTrackIndex) {
            this.audioTrackIndex = 0;
        }
        this.audioTrackName = " " + (this.audioTrackIndex + 1) + "/" + this.audioTrackInfos.size() + " " + this.mLanguageUtil.getSubitleNameByValue(this.audioTrackInfos.get(this.audioTrackIndex).getLanguage());
        TvSurfaceView tvView = getTvView();
        StringBuilder sb = new StringBuilder();
        sb.append(this.audioTrackIndex);
        sb.append("");
        tvView.selectTrack(0, sb.toString());
        MtkLog.i(TAG, "switchAudioTrack ==true and audioTrackIndex== " + this.audioTrackIndex);
        this.audioTrackIndex = this.audioTrackIndex + 1;
    }

    /* access modifiers changed from: private */
    public void getSubTitleTrackInfo() {
        this.subTitleTrackInfos = getTvView().getTracks(2);
        if (this.subTitleTrackInfos == null) {
            this.subTitleTrackInfos = new ArrayList();
        } else if (this.subTitleTrackInfos.size() > 0) {
            this.subtitleTrackIndex = 0;
            this.subTitleTrackName = this.subTitleTrackInfos.get(0).getLanguage();
            this.subTitleTrackId = this.subTitleTrackInfos.get(0).getId();
            this.subTitleTrackName = (this.subtitleTrackIndex + 1) + "/" + this.subTitleTrackInfos.size() + " " + this.mLanguageUtil.getSubitleNameByValue(this.subTitleTrackName);
            getTvView().selectTrack(2, this.subTitleTrackId);
            MtkLog.i(TAG, "subTitleTrackName == " + this.subTitleTrackName + "subTitleTrackId == " + this.subTitleTrackId + "currentTrackIndex == 0");
            this.subtitleTrackIndex = this.subtitleTrackIndex + 1;
        }
        MtkLog.i(TAG, "subtitletrackInfos.size == " + this.subTitleTrackInfos.size());
    }

    /* access modifiers changed from: private */
    public void getAudioTrackInfo() {
        this.audioTrackInfos = getTvView().getTracks(0);
        if (this.audioTrackInfos == null) {
            this.audioTrackInfos = new ArrayList();
        } else if (this.audioTrackInfos.size() > 0) {
            this.audioTrackName = " 1/" + this.audioTrackInfos.size() + " " + this.mLanguageUtil.getSubitleNameByValue(this.audioTrackInfos.get(0).getLanguage());
            this.audioTrackId = this.audioTrackInfos.get(0).getId();
            getTvView().selectTrack(0, this.audioTrackId);
            MtkLog.i(TAG, "audioTrackId == " + this.audioTrackId);
        }
        MtkLog.i(TAG, "audiotrackInfos.size == " + this.audioTrackInfos.size());
    }

    private void restoreToDefault() {
        this.isPlayStart = false;
        this.isReplayOK = true;
        this.isUnlockPin = false;
        this.subtitleTrackIndex = 0;
        this.subTitleTrackInfos = null;
        this.subTitleTrackName = null;
        this.subTitleTrackId = null;
        this.audioTrackIndex = 1;
        this.showAudioTrackFirst = true;
        this.audioTrackInfos = null;
        this.audioTrackName = null;
        this.audioTrackId = null;
        if (this.subtitleName_tv != null) {
            this.subtitleName_tv.setVisibility(8);
        }
        if (this.audioName_tv != null) {
            this.audioName_tv.setVisibility(8);
        }
    }

    private boolean prepareSeekAction() {
        if (this.filePlaySpeed <= 1.0f && this.filePlaySpeed >= -1.0f) {
            return false;
        }
        Toast.makeText(this.mContext, "speed invalid !", 0).show();
        return true;
    }

    /* access modifiers changed from: private */
    public void speakText(String str) {
        getManager().speakText(str);
        if (getManager().getTTSEnable()) {
            this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(EPGConfig.EPG_UPDATE_API_EVENT_LIST), 1000);
        }
    }

    class DvrPlaybackHnadler extends Handler {
        DvrPlaybackHnadler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EPGConfig.EPG_SHOW_LOCK_ICON /*272*/:
                    StateDvrPlayback.this.dismissBigCtrlBar();
                    break;
                case EPGConfig.EPG_SELECT_CHANNEL_COMPLETE /*273*/:
                    StateDvrPlayback.this.mHandler.removeMessages(EPGConfig.EPG_SELECT_CHANNEL_COMPLETE);
                    if (StateDvrPlayback.this.filePlaySpeed >= 0.0f) {
                        long unused = StateDvrPlayback.this.fileCurrentTime = StateDvrPlayback.this.fileCurrentTime + ((long) (1000.0f * StateDvrPlayback.this.filePlaySpeed));
                    } else if (StateDvrPlayback.this.fileCurrentTime <= 0) {
                        long unused2 = StateDvrPlayback.this.fileCurrentTime = 0;
                        float unused3 = StateDvrPlayback.this.filePlaySpeed = 0.0f;
                        int unused4 = StateDvrPlayback.this.mSpeedStepTemp = 0;
                    } else {
                        long unused5 = StateDvrPlayback.this.fileCurrentTime = StateDvrPlayback.this.fileCurrentTime + ((long) (1000.0f * StateDvrPlayback.this.filePlaySpeed));
                    }
                    long unused6 = StateDvrPlayback.this.fileCurrentTime = StateDvrPlayback.this.getController().getDvrPlayDuration();
                    if (StateDvrPlayback.this.fileCurrentTime < 0 || StateDvrPlayback.this.fileCurrentTime > StateDvrPlayback.this.fileTotalTime) {
                        MtkLog.e(StateDvrPlayback.TAG, "fileCurrentTime from surface is " + StateDvrPlayback.this.fileCurrentTime);
                        long unused7 = StateDvrPlayback.this.fileCurrentTime = 0;
                    }
                    StateDvrPlayback.this.refreshTime();
                    boolean unused8 = StateDvrPlayback.this.isReplayOK = true;
                    break;
                case EPGConfig.EPG_UPDATE_API_EVENT_LIST /*274*/:
                    StateDvrPlayback.this.showBigCtrlBar();
                    break;
                case EPGConfig.EPG_INIT_EVENT_LIST /*275*/:
                    StateDvrPlayback.this.speakText("dvr play");
                    break;
            }
            super.handleMessage(msg);
        }
    }

    class CallbackHandler extends Handler {
        WeakReference<StateDvrPlayback> mstaReference;

        public CallbackHandler(StateDvrPlayback mStateDvrPlayback) {
            this.mstaReference = new WeakReference<>(mStateDvrPlayback);
        }

        public void handleMessage(Message msg) {
            Message message = msg;
            if (message != null) {
                int callBack = message.what;
                SomeArgs args = (SomeArgs) message.obj;
                MtkLog.i(StateDvrPlayback.TAG, "msg =" + message.what);
                if (callBack != 4107) {
                    switch (callBack) {
                        case 4103:
                            MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TIMESHIFT_STATE 1 " + args.arg1);
                            MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TIMESHIFT_STATE 2 " + args.argi2);
                            return;
                        case 4104:
                            MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TIMESHIFT_EVENT 1 " + args.arg1);
                            MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TIMESHIFT_EVENT 2 " + args.arg2);
                            MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TIMESHIFT_EVENT 3 " + args.arg3);
                            Bundle bundle = (Bundle) args.arg3;
                            int setSpeedResult = bundle.getInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SPEED_SET_KEY, -2);
                            MtkLog.i(StateDvrPlayback.TAG, "setSpeedResult== " + setSpeedResult);
                            if (setSpeedResult == 0) {
                                StateDvrPlayback.this.showBigCtrlBar();
                            } else if (setSpeedResult == -1 && StateDvrPlayback.this.isRunning()) {
                                MtkLog.i(StateDvrPlayback.TAG, "set speed fail.");
                                boolean unused = StateDvrPlayback.this.isSForSR = false;
                                boolean unused2 = StateDvrPlayback.this.isFForFR = false;
                                float unused3 = StateDvrPlayback.this.filePlaySpeed = 1.0f;
                                int unused4 = StateDvrPlayback.this.mSpeedStepTemp = 1;
                                StateDvrPlayback.this.showBigCtrlBar();
                            }
                            int speedExtra = bundle.getInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SPEED_CHANGED_KEY);
                            MtkLog.i(StateDvrPlayback.TAG, "speedExtra== " + speedExtra + ", filePlaySpeed== " + StateDvrPlayback.this.filePlaySpeed);
                            if ((StateDvrPlayback.this.filePlaySpeed >= 2.0f || StateDvrPlayback.this.filePlaySpeed <= -2.0f) && speedExtra == 1) {
                                float unused5 = StateDvrPlayback.this.filePlaySpeed = (float) speedExtra;
                                int unused6 = StateDvrPlayback.this.mSpeedStepTemp = speedExtra;
                                boolean unused7 = StateDvrPlayback.this.isFForFR = false;
                                boolean unused8 = StateDvrPlayback.this.isSForSR = false;
                                StateDvrPlayback.this.showBigCtrlBar();
                            }
                            if (bundle.getInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_AUDIO_ONLY_SERVICE, -2) == 0) {
                                MtkLog.i(StateDvrPlayback.TAG, "audio only");
                                TwinkleView mTwinkleView = (TwinkleView) ComponentsManager.getInstance().getComponentById(16777232);
                                if (mTwinkleView != null) {
                                    MtkLog.i(StateDvrPlayback.TAG, "msg = handlecallback");
                                    mTwinkleView.handleCallBack(9);
                                }
                            }
                            String mString = bundle.getString(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY);
                            MtkLog.i(StateDvrPlayback.TAG, "mString== " + mString);
                            if (mString == null || !MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_COMPLETE_VALUE.equals(mString)) {
                                if (mString != null && MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_VALUE.equals(mString)) {
                                    boolean unused9 = StateDvrPlayback.this.isPlayStart = true;
                                    if (StateDvrPlayback.this.fileHistoryTime != 0 && StateDvrPlayback.this.fileHistoryTime < StateDvrPlayback.this.fileTotalTime) {
                                        StateDvrPlayback.this.seekDvrFilePlay(StateDvrPlayback.this.fileHistoryTime);
                                        long unused10 = StateDvrPlayback.this.fileHistoryTime = 0;
                                    }
                                    StateDvrPlayback.this.showBigCtrlBar();
                                    StateDvrPlayback.this.refreshTime();
                                }
                                if (mString == null || !MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_LOCKED.equals(mString)) {
                                    if (mString != null && MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ON_REPLY_VALUE.equals(mString)) {
                                        boolean unused11 = StateDvrPlayback.this.isReplayOK = false;
                                        long unused12 = StateDvrPlayback.this.fileCurrentTime = 0;
                                        float unused13 = StateDvrPlayback.this.filePlaySpeed = 1.0f;
                                        int unused14 = StateDvrPlayback.this.mSpeedStepTemp = 1;
                                        boolean unused15 = StateDvrPlayback.this.isPlaying = true;
                                        boolean unused16 = StateDvrPlayback.this.isFForFR = false;
                                        boolean unused17 = StateDvrPlayback.this.isSForSR = false;
                                        boolean unused18 = StateDvrPlayback.this.showAudioTrackFirst = true;
                                        int unused19 = StateDvrPlayback.this.audioTrackIndex = 1;
                                        int unused20 = StateDvrPlayback.this.subtitleTrackIndex = 0;
                                    }
                                    if (mString != null && MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_REPLY_DONE_VALUE.equals(mString)) {
                                        boolean unused21 = StateDvrPlayback.this.isReplayOK = true;
                                        StateDvrPlayback.this.showBigCtrlBar();
                                    }
                                    if (mString != null && MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_SEEK_COMPLETE_VALUE.equals(mString)) {
                                        MtkLog.i(StateDvrPlayback.TAG, "seek done and the view gone.");
                                        try {
                                            MtkLog.i(StateDvrPlayback.TAG, "sleep 1500ms to mute video.");
                                            Thread.sleep(1500);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        TurnkeyUiMainActivity.getInstance().getBlockScreenView().setVisibility(8, 4);
                                        boolean unused22 = StateDvrPlayback.this.isReplayOK = true;
                                    }
                                    if (mString != null && MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ERROR_UNKNOWN.equals(mString)) {
                                        StateDvrPlayback.this.stopDvrFilePlay();
                                    }
                                    if (mString == null) {
                                        return;
                                    }
                                    if (mString.equals(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_CORRUPT)) {
                                        DvrManager.getInstance().showFileNotSupport();
                                        StateDvrPlayback.this.stopDvrFilePlay();
                                        return;
                                    } else if (mString.equals(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_NOTSUPPORT)) {
                                        DvrManager.getInstance().showFileNotSupport();
                                        StateDvrPlayback.this.stopDvrFilePlay();
                                        return;
                                    } else {
                                        return;
                                    }
                                } else if (!StateDvrPlayback.this.isUnlockPin) {
                                    boolean unused23 = StateDvrPlayback.this.isUnlockPin = true;
                                    PinDialog unused24 = StateDvrPlayback.this.dialog = new PinDialog(2, new PinDialog.ResultListener() {
                                        public void play(String pin) {
                                            Bundle mBundle = new Bundle();
                                            try {
                                                mBundle.putInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_PIN_VALUE, Integer.valueOf(pin).intValue());
                                                StateDvrPlayback.this.getController().onAppPrivateCommand(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETPIN, mBundle);
                                            } catch (Exception e) {
                                            }
                                        }

                                        public void done(boolean success) {
                                            boolean unused = StateDvrPlayback.this.isUnlockPin = false;
                                            StateDvrPlayback.this.reSumeDvrFilePlay();
                                        }
                                    }, StateDvrPlayback.this.mContext);
                                    StateDvrPlayback.this.dialog.show();
                                    return;
                                } else {
                                    return;
                                }
                            } else {
                                long unused25 = StateDvrPlayback.this.fileCurrentTime = 0;
                                StateDvrPlayback.mStateDvrPlayback.stopDvrFilePlay();
                                if (StateDvrPlayback.this.getManager().setState((StateBase) StateDvrFileList.getInstance(DvrManager.getInstance()))) {
                                    MtkLog.i(StateDvrPlayback.TAG, "Show file list when pvr play completed.");
                                    StateDvrFileList.getInstance().showPVRlist();
                                    return;
                                }
                                return;
                            }
                        default:
                            return;
                    }
                } else {
                    MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TRACK_CHANGED i1 " + args.argi1);
                    MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TRACK_CHANGED 1 " + args.arg1);
                    MtkLog.i(StateDvrPlayback.TAG, "MSG_CALLBACK_TRACK_CHANGED 2 " + args.arg2);
                    if (StateDvrPlayback.getInstance().isRunning()) {
                        StateDvrPlayback.this.getSubTitleTrackInfo();
                        StateDvrPlayback.this.getAudioTrackInfo();
                        StateDvrPlayback.this.showBigCtrlBar();
                    }
                }
            } else {
                MtkLog.e(StateDvrPlayback.TAG, "callback handler msg ==null !! ");
            }
        }
    }
}
