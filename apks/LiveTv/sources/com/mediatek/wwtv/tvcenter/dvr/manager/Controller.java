package com.mediatek.wwtv.tvcenter.dvr.manager;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.PlaybackParams;
import android.media.tv.TvContract;
import android.media.tv.TvRecordingClient;
import android.media.tv.TvTrackInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.dvr.controller.DVRFiles;
import com.mediatek.wwtv.tvcenter.dvr.db.DBHelper;
import com.mediatek.wwtv.tvcenter.dvr.db.RecordedProgramInfo;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.input.DtvInput;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SomeArgs;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TvInputCallbackMgr;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Controller {
    public static final int ATV = 1;
    public static final int COMPONENT = 7;
    public static final int COMPOSITE = 3;
    public static final int DTV = 2;
    public static final int HDMI = 9;
    public static final int MMP = 6;
    public static final int SCART = 5;
    public static final int S_VIDEO = 4;
    private static final String TAG = "Controller[dvr]";
    public static final int UNKOWN = 0;
    public static final int VGA = 8;
    private final TvRecordingClient mClient;
    private final Context mContext;
    private final TvInputCallbackMgr.DvrCallback mDvrCallback = new TvInputCallbackMgr.DvrCallback() {
        public void onTrackSelected(String inputId, int type, String trackId) {
        }

        public void onTimeShiftStatusChanged(String inputId, int status) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onTimeShiftStatusChanged," + inputId + "," + status);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4103;
                args.arg1 = inputId;
                args.argi2 = status;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onEvent(String inputId, String eventType, Bundle eventArgs) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onEvent[Timeshift]," + inputId + "," + eventType);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4104;
                args.arg1 = inputId;
                args.arg2 = eventType;
                args.arg3 = eventArgs;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onTracksChanged(String inputId, List<TvTrackInfo> tracks) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.i(Controller.TAG, "onEvent [DVR] , " + inputId + "," + tracks.size());
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = DvrConstant.MSG_CALLBACK_TRACK_CHANGED;
                args.arg1 = inputId;
                args.arg2 = tracks;
                int unused2 = Controller.this.sendMessage(args);
            }
        }
    };
    /* access modifiers changed from: private */
    public final DvrManager mDvrManager;
    private List<Handler> mHandlers = null;
    TvRecordingClient.RecordingCallback mRecordingCallback = new TvRecordingClient.RecordingCallback() {
        public void onConnectionFailed(String inputId) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4097;
                args.arg1 = inputId;
                int unused2 = Controller.this.sendMessage(args);
                MtkLog.d(Controller.TAG, "onConnectionFailed," + inputId);
            }
        }

        public void onDisconnected(String inputId) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4098;
                args.arg1 = inputId;
                int unused2 = Controller.this.sendMessage(args);
                MtkLog.d(Controller.TAG, "onDisconnected," + inputId);
            }
        }

        public void onTuned(Uri channelUri) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onTuned," + channelUri);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4099;
                args.arg1 = channelUri;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onRecordingStopped(Uri recordedProgramUri) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onRecordingStopped," + recordedProgramUri);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4100;
                args.arg1 = recordedProgramUri;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onError(int error) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onError," + error);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4101;
                args.argi2 = error;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onEvent(String inputId, String eventType, Bundle eventArgs) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onEvent," + inputId + "," + eventType + "," + eventArgs.containsKey("errId"));
                SomeArgs args = SomeArgs.obtain();
                if (eventType.equals(MtkTvTISMsgBase.MSG_START_RECORDING_ERROR_EVENT)) {
                    new Bundle();
                    Bundle bundle = eventArgs;
                    MtkLog.d(Controller.TAG, "onEvent," + bundle.toString() + "===" + bundle.get("errId"));
                    args.argi1 = 4101;
                    args.argi2 = bundle.getInt("errId");
                } else {
                    args.argi1 = 4102;
                    args.arg2 = eventType;
                }
                args.arg1 = inputId;
                args.arg3 = eventArgs;
                int unused2 = Controller.this.sendMessage(args);
            }
        }
    };
    private final TvInputCallbackMgr.TimeshiftCallback mTimeshiftCallback = new TvInputCallbackMgr.TimeshiftCallback() {
        public void onTimeShiftStatusChanged(String inputId, int status) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onTimeShiftStatusChanged," + inputId + "," + status);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4103;
                args.arg1 = inputId;
                args.argi2 = status;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onEvent(String inputId, String eventType, Bundle eventArgs) {
            DvrManager unused = Controller.this.mDvrManager;
            if (DvrManager.isSuppport()) {
                MtkLog.d(Controller.TAG, "onEvent[Timeshift]," + inputId + "," + eventType);
                SomeArgs args = SomeArgs.obtain();
                args.argi1 = 4104;
                args.arg1 = inputId;
                args.arg2 = eventType;
                args.arg3 = eventArgs;
                int unused2 = Controller.this.sendMessage(args);
            }
        }

        public void onChannelChanged(String inputId, Uri channelUri) {
        }
    };

    public Controller(Context context, DvrManager manager) {
        this.mDvrManager = manager;
        this.mContext = context;
        this.mClient = new TvRecordingClient(context, TAG, this.mRecordingCallback, (Handler) null);
        TvInputCallbackMgr.getInstance(this.mContext).setDvrCallback(this.mDvrCallback);
        this.mHandlers = new ArrayList();
    }

    public List<DVRFiles> getPvrFiles() {
        List<DVRFiles> list = new ArrayList<>();
        List<RecordedProgramInfo> recordList = DBHelper.getRecordedList(this.mContext);
        if (recordList == null) {
            return null;
        }
        new RecordedProgramInfo();
        for (int i = 0; i < recordList.size(); i++) {
            DVRFiles file = new DVRFiles();
            RecordedProgramInfo recordedProgramInfo = recordList.get(i);
            file.setmId(recordedProgramInfo.mId);
            file.setProgarmUri(TvContract.buildRecordedProgramUri(recordedProgramInfo.mId));
            TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).queryChannelById((int) recordedProgramInfo.mChannelId);
            if (tifChannelInfo != null) {
                file.setChannelName(tifChannelInfo.mDisplayName);
                file.setChannelNum(tifChannelInfo.mDisplayNumber);
            } else {
                file.setChannelName(recordedProgramInfo.mTitle);
                file.setChannelNum(recordedProgramInfo.mChannelId + "");
            }
            if (recordedProgramInfo.mStartTimeUtcMills > 0) {
                MtkLog.d(TAG, "mStartTimeUtcMills$ = " + recordList.get(i).mStartTimeUtcMills);
                MtkLog.d(TAG, "mStartTimeUtcMills% = " + recordedProgramInfo.mStartTimeUtcMills);
                Date date = new Date(recordedProgramInfo.mStartTimeUtcMills * 1000);
                MtkLog.d(TAG, "data = " + Util.dateToStringYMD(date));
                MtkLog.d(TAG, "time = " + Util.dateToString(date));
                file.setDate(Util.dateToStringYMD(date));
                file.setTime(Util.dateToString(date));
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTimeInMillis(recordedProgramInfo.mStartTimeUtcMills * 1000);
                MtkLog.d(TAG, "week = " + Util.getWeek(calendar.get(7)));
                file.setWeek(Util.getWeek(calendar.get(7)));
            }
            file.setProgramName(recordedProgramInfo.mRecordingDataUri);
            file.setDuration((long) recordedProgramInfo.mRecordingDurationMills);
            file.setRecording(!recordedProgramInfo.mSearchable);
            MtkLog.d(TAG, "recordedProgramInfo.mShortDescription = " + recordedProgramInfo.mShortDescription);
            file.setmDetailInfo(recordedProgramInfo.mShortDescription);
            file.dumpValues();
            list.add(file);
        }
        return list;
    }

    public int deletePvrFiles(Context content, long id) {
        return DBHelper.deleteRecordById(content, id);
    }

    public String getSrcType() {
        MtkTvRecord.getInstance();
        int type = MtkTvRecord.getSrcType();
        MtkLog.d(TAG, "srcType = " + type);
        switch (type) {
            case 0:
                return "UNKOWN";
            case 1:
                return "ATV";
            case 2:
                return "TV";
            case 3:
                return "Composite";
            case 4:
                return "SVIDEO";
            case 5:
                return "SCART";
            case 6:
                return "MMP";
            case 7:
                return "Component";
            case 8:
                return "VGA";
            case 9:
                return "HDMI";
            default:
                return "";
        }
    }

    private TvSurfaceView getTvView() {
        return TurnkeyUiMainActivity.getInstance().getTvView();
    }

    public void changeChannelByID(int keyCode, int arg1) {
        MtkTvChannelInfoBase chInfo;
        ChannelListDialog dialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (dialog != null && dialog.isShowing() && (chInfo = CommonIntegration.getInstance().getChannelById(arg1)) != null) {
            dialog.selectChannel(keyCode, chInfo);
        }
    }

    public void getRecordDuration() {
        MtkLog.d(TAG, "getRecordDuration");
        this.mClient.sendAppPrivateCommand(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION, (Bundle) null);
    }

    public void setBGM() {
        MtkLog.d(TAG, "setBGM");
        this.mClient.sendAppPrivateCommand("session_event_dvr_record_in_bgm", (Bundle) null);
    }

    public long getDvrPlayDuration() {
        return getTvView().timeshiftGetCurrentPositionMs();
    }

    public void addEventHandler(Handler handler) {
        this.mHandlers.add(handler);
    }

    public void removeEventHandler(Handler handler) {
        this.mHandlers.remove(handler);
    }

    /* access modifiers changed from: private */
    public int sendMessage(SomeArgs args) {
        for (Handler handler : this.mHandlers) {
            Message temp = Message.obtain();
            temp.what = args.argi1;
            temp.obj = args;
            handler.sendMessage(temp);
        }
        return 0;
    }

    public void tune(String inputId, Uri channelUri, Bundle params) {
        DvrManager dvrManager = this.mDvrManager;
        if (DvrManager.isSuppport()) {
            MtkLog.d(TAG, "tune," + inputId + "," + channelUri);
            try {
                this.mClient.tune(inputId, channelUri, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void tune(Uri channelUri, Bundle params) {
        String inputId = null;
        try {
            TIFChannelInfo info = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoByProviderId(ContentUris.parseId(channelUri));
            if (info != null) {
                inputId = info.mInputServiceName;
            }
            tune(inputId, channelUri, params);
        } catch (Exception ex) {
            MtkLog.d(TAG, "Exception: " + ex);
        }
    }

    public void startRecording(Uri programUri) {
        DvrManager dvrManager = this.mDvrManager;
        if (DvrManager.isSuppport()) {
            this.mClient.startRecording(programUri);
        }
    }

    public void stopRecording() {
        DvrManager dvrManager = this.mDvrManager;
        if (DvrManager.isSuppport()) {
            this.mClient.stopRecording();
        }
    }

    public void dvrRelease() {
        DvrManager dvrManager = this.mDvrManager;
        if (DvrManager.isSuppport()) {
            this.mClient.release();
        }
    }

    public void sendRecordCommand(@NonNull String action, Bundle data) {
        this.mClient.sendAppPrivateCommand(action, data);
    }

    public void dvrPlay(Uri recordedProgramUri, boolean isNeedLastMemory) {
        TIFChannelInfo info;
        String inputId = DtvInput.DEFAULT_ID;
        getTvView().reset();
        try {
            RecordedProgramInfo recordinfo = DBHelper.getRecordedInfoById(this.mContext, ContentUris.parseId(recordedProgramUri));
            if (!(recordinfo == null || (info = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoByProviderId(recordinfo.mChannelId)) == null)) {
                inputId = info.mInputServiceName;
            }
            MtkLog.d(TAG, "inputId: " + inputId);
        } catch (Exception ex) {
            MtkLog.d(TAG, "Exception: " + ex);
        }
        dvrPlay(inputId, recordedProgramUri, isNeedLastMemory);
    }

    public void dvrPlay(String inputId, Uri recordedProgramUri, boolean isNeedLastMemory) {
        DvrManager dvrManager = this.mDvrManager;
        if (!DvrManager.isSuppport()) {
            MtkLog.e(TAG, "mDvrManager.isSuppport() = false !!");
        } else if (getTvView() == null) {
            MtkLog.d(TAG, "dvrPlay, TvView null");
        } else {
            MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE, 1);
            MtkTvAppTV.getInstance().updatedSysStatus(MtkTvAppTVBase.SYS_MMP_RESUME);
            MtkLog.d(TAG, "dvrPlay");
            if (MenuConfigManager.getInstance(this.mContext).getDefault("g_audio__aud_type") == 2) {
                Bundle adBundle = new Bundle();
                adBundle.putInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_ADINFO_VALUE, 1);
                MtkLog.i(TAG, "set adAction info.");
                getTvView().sendAppPrivateCommand(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETADINFO, adBundle);
            }
            if (TVContent.getInstance(this.mContext).isFinCountry()) {
                Bundle adCountryBundle = new Bundle();
                adCountryBundle.putInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_ADINFO_VALUE, 1);
                adCountryBundle.putInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_COUNTRYINFO_VALUE, 1);
                MtkLog.i(TAG, "set adCountryAction info.");
                getTvView().sendAppPrivateCommand(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETADCOUNTRYINFO, adCountryBundle);
            }
            Intent intent = new Intent("mtk.intent.volume.status");
            intent.putExtra(NotificationCompat.CATEGORY_STATUS, 1);
            this.mContext.sendBroadcast(intent);
            getTvView().timeShiftPlay(inputId, recordedProgramUri);
        }
    }

    public void dvrPause() {
        if (getTvView() == null) {
            MtkLog.d(TAG, "dvrPause, TvView null");
            return;
        }
        MtkLog.d(TAG, "dvrPause");
        getTvView().timeShiftPause();
    }

    public void dvrStop() {
        Intent intent = new Intent("mtk.intent.volume.status");
        intent.putExtra(NotificationCompat.CATEGORY_STATUS, 0);
        this.mContext.sendBroadcast(intent);
        getTvView().reset();
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE, 0);
    }

    public void dvrResume() {
        if (getTvView() == null) {
            MtkLog.d(TAG, "dvrResume, TvView null");
            return;
        }
        MtkLog.d(TAG, "dvrResume");
        getTvView().timeShiftResume();
    }

    public void dvrSeekTo(long timeMs) {
        if (getTvView() == null) {
            MtkLog.d(TAG, "dvrSeekTo, TvView null");
            return;
        }
        MtkLog.d(TAG, "dvrSeekTo");
        try {
            getTvView().timeShiftSeekTo(timeMs);
        } catch (Exception e) {
        }
    }

    public void dvrPlaybackParams(@NonNull PlaybackParams params) {
        if (getTvView() == null) {
            MtkLog.d(TAG, "dvrPlaybackParams, TvView null");
            return;
        }
        MtkLog.d(TAG, "dvrPlaybackParams");
        getTvView().timeShiftSetPlaybackParams(params);
    }

    public void sendDvrPlaybackCommand(@NonNull String action, Bundle data) {
        if (getTvView() == null) {
            MtkLog.d(TAG, "sendDvrPlaybackCommand, TvView null");
            return;
        }
        MtkLog.d(TAG, "sendDvrPlaybackCommand," + action);
        getTvView().sendAppPrivateCommand(action, data);
    }

    public void handleRecordNotify(TvCallbackData data) {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = 4105;
        args.argi2 = data.param1;
        args.argi3 = data.param2;
        args.argi4 = data.param3;
        sendMessage(args);
        MtkLog.d(TAG, "handleRecordNotify," + data.param1 + "," + data.param2 + "," + data.param3);
    }

    public void handleRecordNotify(Intent intent) {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = DvrConstant.MSG_CALLBACK_ALARM_GO_OFF;
        args.arg1 = intent;
        sendMessage(args);
        MtkLog.d(TAG, "handleRecordNotify, " + intent.getLongExtra("startTime", 0) + "," + intent.getLongExtra("endTime", 0) + "," + intent.getStringExtra("channelID"));
    }

    public void onAppPrivateCommand(String action, Bundle data) {
        getTvView().sendAppPrivateCommand(action, data);
    }

    public void onTvShow() {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = DvrConstant.tv_show;
        sendMessage(args);
    }
}
