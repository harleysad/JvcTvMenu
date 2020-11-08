package com.mediatek.wwtv.tvcenter.dvr.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.MtkTvRecordBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.setting.widget.view.ScheduleListDialog;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.IManagerInterface;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateNormal;
import com.mediatek.wwtv.tvcenter.dvr.controller.StatusType;
import com.mediatek.wwtv.tvcenter.dvr.controller.TVLogicManager;
import com.mediatek.wwtv.tvcenter.dvr.controller.UImanager;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity;
import com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyService;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.TwinkleView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class DvrManager extends NavBasicMisc implements DevListener, ComponentStatusListener.ICStatusListener, IManagerInterface<StateBase> {
    public static final int ALLOW_SYSTEM_SUSPEND = 2003;
    public static final String AUTO_SYNC = "SETUP_auto_syn";
    public static final int CHANGE_CHANNEL = 2002;
    private static final int CONTINUE_TO_SEEK_NOT_SUPPORT = 2005;
    public static final int CREATE_FAIL = 1;
    private static final int CREATE_SUCCESS = 2;
    public static final int Channel_NOT_Support = 8;
    public static final int DISK_ATTENTION = 4;
    private static final int DISK_NOT_READY = 0;
    public static final String DTV_DEVICE_INFO = "DTV_DEVICE_INFO";
    public static final String DTV_TSHIFT_OPTION = "DTV_TSHIFT_OPTION";
    private static final int FILE_NOT_SUPPORT = 10002;
    public static final int INVALID_VALUE = 10004;
    public static final int PRO_AV_STREAM_NOT_AVAILABLE = 4103;
    public static final int PRO_DISK_NOT_READY = 4101;
    public static final int PRO_DISK_REMOVE = 4118;
    public static final int PRO_FEATURE_NOT_SUPPORT = 4098;
    public static final int PRO_INPUT_LOCKED = 4105;
    public static final int PRO_INTERNAL_ERROR = 4114;
    public static final int PRO_NONE_TV_SOURCE = 4097;
    public static final int PRO_NO_ENOUGH_SPACE = 4112;
    public static final int PRO_PIP_STATE = 4096;
    public static final int PRO_PVR_PLAY_SEEK = 4120;
    public static final int PRO_PVR_RUNNING = 4117;
    public static final int PRO_PVR_STOP = 4116;
    public static final int PRO_RECORDING = 4102;
    public static final int PRO_SIGNAL_LOSS = 4099;
    public static final int PRO_STREAM_NOT_AUTHORIZED = 4104;
    public static final int PRO_TIME_SHIFT_DISK_NOT_READY = 4119;
    public static final int PRO_TSHIFT_RUNNING = 4100;
    public static final int PRO_UNKNOW_ERROR = 4115;
    public static final int PRO_VIDEO_RESOLUTION_ERROR = 4113;
    private static final int RECORD_FINISHED = 5;
    public static final String RECORD_START = "Recording_Session_event_recordingStart";
    private static final int SCHEDULE_PVR_CHANGE_CHANNEL = 8201;
    public static final String SCHEDULE_PVR_CHANNELLIST = "SCHEDULE_PVR_CHANNELLIST";
    public static final String SCHEDULE_PVR_REMINDER_TYPE = "SCHEDULE_PVR_REMINDER_TYPE";
    public static final String SCHEDULE_PVR_REPEAT_TYPE = "SCHEDULE_PVR_REPEAT_TYPE";
    public static final String SCHEDULE_PVR_SRCTYPE = "SCHEDULE_PVR_SRCTYPE";
    private static final int SCHEDULE_PVR_TASK = 7;
    private static final int SCHEDULE_PVR_TASK_STOP_TIME = 8200;
    public static final int STEP_VALUE = 1;
    private static final String TAG = "DvrManager";
    public static final String TIMER_powerOnTime = "powerOnTime";
    public static final String TIME_DATE = "SETUP_date";
    public static final String TIME_TIME = "SETUP_time";
    public static final int UNMOUNT_EVENT = 2004;
    public static final String UNMOUNT_EVENT_MSG_KEY = "UNMOUNT_EVENT_MSG_KEY";
    public static boolean isBGM = false;
    /* access modifiers changed from: private */
    public static DvrManager mDvrManager = null;
    private static MtkTvBookingBase newscheduleItem = null;
    /* access modifiers changed from: private */
    public static MtkTvBookingBase scheduleItem = null;
    private final int MSG_SCHEDULE_CHANGE_SOURCE;
    private final int MSG_SCHEDULE_DEFAULT;
    private final int MSG_SCHEUDLE_CANCEL;
    private final int MSG_SCHEUDLE_START;
    private ResetBroadcast broadcast;
    private DvrDialog conDialog;
    /* access modifiers changed from: private */
    public final Handler deviceHandler;
    private final Handler handler;
    /* access modifiers changed from: private */
    public int index;
    public boolean isBGMState;
    public boolean isPvrDialogShow;
    private boolean isStopDvrNotResumeLauncher;
    /* access modifiers changed from: private */
    public boolean isbgm;
    private boolean isfirstRecord;
    private Controller mController = null;
    private StateBase mCurrentState = null;
    private MountPoint mDvrDiskMountPoint = null;
    private Handler mScheduleHandler;
    private Stack<StateBase> mStates = null;
    /* access modifiers changed from: private */
    public TVLogicManager mTVLogicManager;
    private boolean menuExit;
    /* access modifiers changed from: private */
    public String prompt;
    private SaveValue saveValue;
    private long timeduration;
    private final MyHandler topHandler;
    private final TextToSpeechUtil ttUtil;
    private final int tvHeight;
    private final int tvWidth;
    public UImanager uiManager;

    private DvrManager(Context context) {
        super(context);
        this.isBGMState = false;
        this.menuExit = false;
        this.isPvrDialogShow = false;
        this.tvWidth = ScreenConstant.SCREEN_WIDTH;
        this.tvHeight = ScreenConstant.SCREEN_HEIGHT;
        this.conDialog = null;
        this.isStopDvrNotResumeLauncher = false;
        this.isbgm = false;
        this.isfirstRecord = false;
        this.broadcast = null;
        this.index = -1;
        this.timeduration = 0;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                MtkLog.e(DvrManager.TAG, "handleMessage, KEYCODE_MTKIR_RECORD");
                DvrManager.this.onKeyDown(130);
            }
        };
        this.deviceHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    MtkLog.e(DvrManager.TAG, "DevManager.getInstance()-------");
                    DevManager.getInstance().addDevListener(DvrManager.this);
                    if (!DvrManager.this.checkPvrMP()) {
                        DvrManager.this.setPvrMountPoint((MountPoint) null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MtkLog.e(DvrManager.TAG, "DevManager.getInstance()-----error--");
                    DvrManager.this.deviceHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        };
        this.MSG_SCHEUDLE_START = 1;
        this.MSG_SCHEUDLE_CANCEL = 2;
        this.MSG_SCHEDULE_CHANGE_SOURCE = 3;
        this.MSG_SCHEDULE_DEFAULT = 4;
        this.mScheduleHandler = new Handler() {
            public void handleMessage(Message msg) {
                Intent intent = new Intent("com.mtk.dialog.dismiss");
                switch (msg.what) {
                    case 1:
                        MtkLog.d(DvrManager.TAG, "isbgm=" + DvrManager.this.isbgm);
                        DvrManager.this.isresumeTurnkey();
                        DvrManager.this.mContext.sendBroadcast(intent);
                        DvrManager.this.handlerScheduleData(DvrManager.this.index);
                        int unused = DvrManager.this.index = -1;
                        DvrManager.this.startSchedulePvr();
                        removeMessages(2);
                        break;
                    case 2:
                        DvrManager.this.handlerScheduleData(DvrManager.this.index);
                        removeMessages(1);
                        DvrManager.this.mContext.sendBroadcast(intent);
                        int unused2 = DvrManager.this.index = -1;
                        break;
                    case 3:
                        DvrManager.this.setDuration();
                        if (DvrManager.this.isbgm) {
                            DvrManager.mDvrManager.getController().setBGM();
                        }
                        if (DvrManager.scheduleItem != null) {
                            DvrManager.mDvrManager.getController().tune(TvContract.buildChannelUri(TIFChannelManager.getInstance(DvrManager.this.mContext).getTIFChannelInfoById(DvrManager.scheduleItem.getChannelId()).mId), (Bundle) null);
                            MtkLog.d("1111+", "handler");
                            break;
                        }
                        break;
                    default:
                        int unused3 = DvrManager.this.index = -1;
                        break;
                }
                super.handleMessage(msg);
            }
        };
        this.componentID = NavBasic.NAV_COMP_ID_PVR_TIMESHIFT;
        this.mStates = new Stack<>();
        this.saveValue = SaveValue.getInstance(context);
        this.ttUtil = new TextToSpeechUtil(this.mContext);
        this.mController = new Controller(context, this);
        this.topHandler = new MyHandler((Activity) context);
        this.mTVLogicManager = TVLogicManager.getInstance(context, this);
        this.uiManager = new UImanager((Activity) context);
        setState((StateBase) setDefaultState(context));
        this.deviceHandler.sendEmptyMessageDelayed(0, 1000);
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(6, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        ArrayList<MountPoint> deviceList = DeviceManager.getInstance().getMountPointList();
        for (int i = 0; i < deviceList.size(); i++) {
            if (new File(deviceList.get(i).mMountPoint + Core.PVR_DISK_TAG).exists()) {
                MtkLog.d(TAG, "isTuned = " + deviceList.get(i).mMountPoint);
                MtkTvRecordBase.setDisk(deviceList.get(i).mMountPoint);
                return;
            }
        }
    }

    private void initBrodcast() {
        if (this.broadcast == null) {
            MtkLog.d(TAG, "initBrodcast");
            this.broadcast = new ResetBroadcast();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MASTER_CLEAR");
            filter.addAction("android.intent.action.SCREEN_OFF");
            filter.addAction("android.intent.action.SCREEN_ON");
            this.mContext.registerReceiver(this.broadcast, filter);
            return;
        }
        MtkLog.d(TAG, "initBrodcast failed ,already exsist !");
    }

    private void unregistBroadcast() {
        if (this.broadcast != null) {
            try {
                this.mContext.unregisterReceiver(this.broadcast);
            } catch (Exception e) {
            }
        }
    }

    class ResetBroadcast extends BroadcastReceiver {
        ResetBroadcast() {
        }

        public void onReceive(Context arg0, Intent arg1) {
            MtkLog.d(DvrManager.TAG, "onReceive android.intent.action.MASTER_CLEAR");
            if ("android.intent.action.SCREEN_OFF".equals(arg1.getAction())) {
                boolean unused = DvrManager.this.isbgm = true;
            }
            if ("android.intent.action.SCREEN_ON".equals(arg1.getAction())) {
                boolean unused2 = DvrManager.this.isbgm = false;
            }
            MtkLog.d(DvrManager.TAG, "isbgm=" + DvrManager.this.isbgm);
            if ("android.intent.action.MASTER_CLEAR".equals(arg1.getAction()) && DvrManager.getInstance() != null && DvrManager.getInstance().pvrIsRecording()) {
                DvrManager.this.getController().stopRecording();
            }
        }
    }

    public boolean checkScrambled() {
        TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).getCurrChannelInfo();
        if (tifChannelInfo == null) {
            return false;
        }
        if ((MtkTvChCommonBase.SB_VNET_SCRAMBLED & tifChannelInfo.mMtkTvChannelInfo.getNwMask()) > 0) {
            return true;
        }
        return false;
    }

    private StateNormal setDefaultState(Context context) {
        setVisibility(4);
        return new StateNormal(context, this);
    }

    public void startDvr(Uri programUri) {
        ArrayList<MountPoint> deviceList = DeviceManager.getInstance().getMountPointList();
        int i = 0;
        while (true) {
            if (i >= deviceList.size()) {
                break;
            }
            if (new File(deviceList.get(i).mMountPoint + Core.PVR_DISK_TAG).exists()) {
                MtkLog.d(TAG, "isTuned = " + deviceList.get(i).mMountPoint);
                MtkTvRecordBase.setDisk(deviceList.get(i).mMountPoint);
                break;
            }
            i++;
        }
        this.mController.startRecording(programUri);
        initBrodcast();
    }

    public void stopDvr() {
        this.mController.stopRecording();
        unregistBroadcast();
    }

    public void stopAllRunning() {
        if (this.mCurrentState == null || !"StateNormal".equals(this.mCurrentState.getClass().getSimpleName())) {
            try {
                if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
                    StateDvr.getInstance().getHandler().removeMessages(6);
                    this.mController.stopRecording();
                }
                if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isRunning()) {
                    restoreToDefault((StateBase) StateDvrFileList.getInstance());
                }
                if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                    restoreToDefault((StateBase) StateDvrPlayback.getInstance());
                }
            } catch (Exception e) {
                MtkLog.e(TAG, "stopAllRunning error== " + e.toString());
                e.printStackTrace();
            }
            if (this.uiManager != null) {
                this.uiManager.dissmiss();
                return;
            }
            return;
        }
        MtkLog.e("stopAllRunning", "mCurrentState:" + this.mCurrentState.getClass().getSimpleName());
    }

    public static DvrManager getInstance(Context context) {
        if (mDvrManager == null) {
            mDvrManager = new DvrManager(context);
        } else {
            mDvrManager.mContext = context;
            mDvrManager.updateStateContext(context);
        }
        return mDvrManager;
    }

    public synchronized void updateStateContext(Context mContext) {
        if (this.mStates != null) {
            for (int i = this.mStates.size() - 1; i >= 0; i--) {
                ((StateBase) this.mStates.get(i)).setmContext(mContext);
            }
        }
    }

    public static DvrManager getInstance() {
        return mDvrManager;
    }

    public static class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            TwinkleView mTwinkleView;
            Activity activity = (Activity) this.mActivity.get();
            switch (msg.what) {
                case 7:
                    DvrManager.mDvrManager.prepareSchedulePvrTask(msg.arg1);
                    break;
                case 1002:
                    DvrManager.mDvrManager.uiManager.showInfoBar(activity.getString(R.string.pvr_save_record));
                    if (ComponentsManager.getInstance().getComponentById(16777232) != null) {
                        ((TwinkleView) ComponentsManager.getInstance().getComponentById(16777232)).setVisibility(8);
                    }
                    if (StateDvr.getInstance().isChangeSource()) {
                        CommonIntegration.getInstance().channelUp();
                        ComponentStatusListener.getInstance().updateStatus(5, 0);
                        StateDvr.getInstance().setChangeSource(false);
                        break;
                    }
                    break;
                case DvrManager.CHANGE_CHANNEL /*2002*/:
                    DvrManager.mDvrManager.getController().changeChannelByID(msg.arg2, msg.arg1);
                    break;
                case DvrManager.UNMOUNT_EVENT /*2004*/:
                    DvrManager.mDvrManager.unmountEvent(msg.getData().getString(DvrManager.UNMOUNT_EVENT_MSG_KEY));
                    break;
                case DvrManager.CONTINUE_TO_SEEK_NOT_SUPPORT /*2005*/:
                    DvrManager.mDvrManager.uiManager.showInfoBar(activity.getString(R.string.continue_to_seek_not_support));
                    break;
                case DvrManager.SCHEDULE_PVR_TASK_STOP_TIME /*8200*/:
                    DvrManager.mDvrManager.setPVRStopTime(Long.valueOf(msg.getData().getLong("Duration")));
                    break;
                case DvrManager.SCHEDULE_PVR_CHANGE_CHANNEL /*8201*/:
                    if (DvrManager.scheduleItem != null) {
                        Short decode = Short.decode(DvrManager.scheduleItem.getEventTitle());
                        int channelID = DvrManager.scheduleItem.getChannelId();
                        MtkLog.d(DvrManager.TAG, "prepareScheduleTask:" + channelID + "," + DvrManager.isBGM);
                        if (channelID != -1 && channelID != 0) {
                            if (!InputSourceManager.getInstance().isCurrentTvSource(CommonIntegration.getInstance().getCurrentFocus())) {
                                MtkLog.d(DvrManager.TAG, "handleMessage,SCHEDULE_PVR_CHANGE_CHANNEL");
                                InputSourceManager.getInstance().saveOutputSourceName("TV", CommonIntegration.getInstance().getCurrentFocus());
                            }
                            if (DvrManager.isBGM) {
                                MtkTvChannelInfoBase channelInfo = CommonIntegration.getInstance().getChannelById(channelID);
                                CommonIntegration.getInstance();
                                MtkTvBroadcast mBroadcast = CommonIntegration.getInstanceMtkTvBroadcast();
                                if (!(channelInfo == null || mBroadcast == null || mBroadcast.channelSelect(channelInfo, false) == 0)) {
                                    mBroadcast.channelSelect(channelInfo, false);
                                }
                            } else {
                                CommonIntegration.getInstance().selectChannelById(channelID);
                            }
                            MtkLog.d(DvrManager.TAG, "handleMessage,prepareScheduleTask, true");
                            break;
                        } else {
                            return;
                        }
                    }
                    break;
                case DvrManager.FILE_NOT_SUPPORT /*10002*/:
                    DvrManager.mDvrManager.uiManager.showInfoBar(activity.getString(R.string.file_notsupport));
                    break;
                case DvrConstant.Dissmiss_Info_Bar:
                    if (DvrManager.mDvrManager.uiManager != null) {
                        DvrManager.mDvrManager.uiManager.dissmiss();
                        if (StateDvrFileList.getInstance() == null || !StateDvrFileList.getInstance().isShowing()) {
                            PwdDialog mPWDDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
                            if (MtkTvPWDDialog.getInstance().PWDShow() != 0 || (DvrManager.getInstance() != null && DvrManager.getInstance().pvrIsRecording())) {
                                if ((DvrManager.getInstance() == null || !DvrManager.getInstance().pvrIsRecording()) && (mTwinkleView = (TwinkleView) ComponentsManager.getInstance().getComponentById(16777232)) != null) {
                                    mTwinkleView.showHandler();
                                }
                            } else if (mPWDDialog != null) {
                                mPWDDialog.show();
                            }
                        }
                    }
                    boolean isRuning = false;
                    if (DvrManager.mDvrManager.getState() instanceof StateDvr) {
                        isRuning = ((StateDvr) DvrManager.mDvrManager.getState()).isRunning();
                    }
                    MtkLog.e("timeshift", "timeshift:isRuning:" + isRuning);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public class ScheduleHandler extends Handler {
        private final DvrDialog dialog;
        private final MtkTvBookingBase item;

        public ScheduleHandler(DvrDialog dialog2, MtkTvBookingBase item2) {
            this.dialog = dialog2;
            this.item = item2;
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MtkLog.d(DvrManager.TAG, "ScheduleHandler, handleMessage");
            try {
                this.dialog.dismiss();
                if (DvrManager.scheduleItem == null) {
                    return;
                }
                if (this.item.getRecordMode() != 1) {
                    TifTimeShiftManager mTimeShiftManager = TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager();
                    if (mTimeShiftManager != null && SaveValue.getInstance(DvrManager.this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
                        mTimeShiftManager.stop();
                        SaveValue.getInstance(DvrManager.this.mContext).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
                        SaveValue.saveWorldBooleanValue(DvrManager.this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
                        SystemClock.sleep(1000);
                    } else if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
                        DvrManager.getInstance().stopDvr();
                        return;
                    }
                    DvrManager.this.startSchedulePvr();
                    return;
                }
                DvrManager.this.clearSchedulePvr();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void isresumeTurnkey() {
        boolean isreume = SaveValue.getInstance(this.mContext).readBooleanValue(TurnkeyUiMainActivity.TURNKEY_ACTIVE_STATE);
        MtkLog.d(TAG, "is onstart turnkey==" + isreume);
        if (!isreume) {
            TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
        }
    }

    private void getStartTime(int index2) {
        List<MtkTvBookingBase> bookingBases = MtkTvRecord.getInstance().getBookingList();
        if (bookingBases != null && bookingBases.size() > 0) {
            newscheduleItem = bookingBases.get(index2);
        }
    }

    public void handleRecordNTF(TvCallbackData data) {
        TvCallbackData dataRecord = data;
        MtkLog.d(TAG, "handleRecordNotifyMsg, dataRecord.param1:" + dataRecord.param1 + ",dataRecord.param2:" + dataRecord.param2 + ",dataRecord.param2:" + dataRecord.param3);
        if (MtkTvRecordBase.RecordNotifyMsgType.values()[dataRecord.param1] == MtkTvRecordBase.RecordNotifyMsgType.RECORD_PVR_NTFY_VIEW_SCHEDULE_START) {
            MtkLog.d(TAG, "index=" + this.index + ",param2=" + dataRecord.param2);
            if (this.index != dataRecord.param2) {
                getStartTime(dataRecord.param2);
                MtkLog.d(TAG, "newschedule=" + newscheduleItem.toString());
                if (getDuration(newscheduleItem) <= 15 && getDuration(newscheduleItem) > 0) {
                    scheduleItem = newscheduleItem;
                    this.isfirstRecord = false;
                    MtkLog.d(TAG, "schedule=" + scheduleItem.toString());
                    this.index = dataRecord.param2;
                    if (dataRecord.param3 != 0) {
                        this.isbgm = true;
                        mDvrManager.getController().setBGM();
                    }
                    Activity act = DestroyApp.getTopActivity();
                    if (act != null && ((act instanceof EPGEuActivity) || (act instanceof EPGSaActivity) || (act instanceof EPGCnActivity) || (act instanceof EPGUsActivity))) {
                        MtkLog.d(TAG, "DestroyApp.getTopActivity(): " + act.getComponentName());
                        act.finish();
                    }
                    this.mContext.sendBroadcast(new Intent("finish_live_tv_settings"));
                    if (scheduleItem.getRecordMode() == 1) {
                        this.mScheduleHandler.sendEmptyMessageDelayed(2, getDuration(scheduleItem) * 1000);
                    } else {
                        initBrodcast();
                        this.mScheduleHandler.sendEmptyMessageDelayed(1, getDuration(scheduleItem) * 1000);
                    }
                    Intent intent = new Intent(this.mContext, TurnkeyService.class);
                    Bundle bundle = new Bundle();
                    TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(scheduleItem.getChannelId());
                    String nameString = tifChannelInfo == null ? scheduleItem.getEventTitle() : tifChannelInfo.mDisplayName;
                    bundle.putString("PROMPT", this.mContext.getResources().getString(R.string.dvr_dialog_message_schedule_pvr) + nameString + this.mContext.getResources().getString(R.string.dvr_dialog_message_schedule_pvr_start));
                    bundle.putString("TITLE", this.mContext.getResources().getString(R.string.dvr_dialog_message_schedule_title));
                    bundle.putInt("CONFIRM_TYPE", 1);
                    intent.putExtra("SCHEDULE", bundle);
                    this.mContext.startService(intent);
                    if (ScheduleListDialog.getDialog() != null && ScheduleListDialog.getDialog().isShowing()) {
                        ScheduleListDialog.getDialog().dismiss();
                    }
                    if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext) != null) {
                        ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext).dismiss();
                    }
                }
            }
        }
    }

    private long getDuration(MtkTvBookingBase sitem) {
        long start = sitem.getRecordStartTime();
        MtkLog.d(TAG, "time==" + start);
        MtkTvTimeFormatBase mTime = MtkTvTime.getInstance().getBroadcastLocalTime();
        MtkTvTimeFormatBase timeBaseTo = new MtkTvTimeFormatBase();
        new MtkTvTimeBase().convertTime(2, mTime, timeBaseTo);
        timeBaseTo.print("Jiayang.li--sys-utc");
        MtkLog.d(TAG, "getBroadcastLocalTime == " + timeBaseTo.toSeconds());
        MtkLog.d(TAG, "time now==" + (System.currentTimeMillis() / 1000));
        return start - timeBaseTo.toSeconds();
    }

    public void handleRecordNTF(boolean isStart) {
        if (isStart) {
            handlerScheduleData(this.index);
            this.index = -1;
            isresumeTurnkey();
            TifTimeShiftManager mTimeShiftManager = TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager();
            if (mTimeShiftManager != null && SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
                mTimeShiftManager.stop();
                SaveValue.getInstance(this.mContext).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
                SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
                SystemClock.sleep(1000);
            }
            startSchedulePvr();
            return;
        }
        this.mScheduleHandler.sendEmptyMessage(2);
    }

    /* access modifiers changed from: private */
    public void handlerScheduleData(int index2) {
        List<MtkTvBookingBase> bookingBases;
        if (index2 != -1 && (bookingBases = MtkTvRecord.getInstance().getBookingList()) != null && bookingBases.size() > 0) {
            MtkTvBookingBase bookingBase = bookingBases.get(index2);
            if (bookingBase.getRepeatMode() == 128) {
                MtkTvRecord.getInstance().deleteBooking(index2);
                MtkLog.d(TAG, "bookingsize==" + bookingBases.size());
            }
            scheduleItem = bookingBase;
            if (bookingBase.getRepeatMode() == 0) {
                MtkTvRecord.getInstance().deleteBooking(index2);
                bookingBase.setRecordStartTime(bookingBase.getRecordStartTime() + 86400);
                MtkTvRecord.getInstance().addBooking(bookingBase);
            }
            if (bookingBase.getRepeatMode() != 0 && bookingBase.getRepeatMode() != 128) {
                int repeatcount = bookingBase.getRepeatMode();
                int weekday = MtkTvTime.getInstance().getLocalTime().weekDay;
                MtkLog.d(TAG, "weekday==" + weekday);
                int count = -6;
                for (int i = 6; i >= 0; i--) {
                    if (((1 << i) & repeatcount) == (1 << i)) {
                        if (i > weekday) {
                            count = i - weekday;
                            MtkLog.d(TAG, "count1==" + count);
                        } else if (count <= 0) {
                            count = i - weekday;
                            MtkLog.d(TAG, "count2==" + count);
                        }
                    }
                }
                if (count == 0) {
                    count = 7;
                }
                MtkLog.d(TAG, "count==" + count);
                MtkTvRecord.getInstance().deleteBooking(index2);
                bookingBase.setRecordStartTime(bookingBase.getRecordStartTime() + ((long) (count * 24 * 60 * 60)));
                MtkTvRecord.getInstance().addBooking(bookingBase);
            }
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.e(TAG, "updateComponentStatus:" + statusID);
        if (isExitMenu() && statusID == 3) {
            setVisibility(0);
            setExitMenu(false);
        }
        if (statusID == 3 && StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            MtkLog.e(TAG, "updateComponentStatus: ====>BGM");
            setVisibility(0);
            StringBuilder sb = new StringBuilder();
            sb.append("activity2");
            ComponentsManager.getInstance();
            sb.append(ComponentsManager.getActiveCompId());
            MtkLog.e(TAG, sb.toString());
        }
        if (statusID == 3 && StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
            MtkLog.i(TAG, "updateComponentStatus: ====> StatedvrPlayback !");
        }
        if (statusID == 6 && StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isRunning()) {
            restoreToDefault((StateBase) StateDvrFileList.getInstance());
        }
        if (statusID == 10 && value == 0 && scheduleItem != null) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void toRecord() {
        MtkLog.e(TAG, "toRecord");
        if (scheduleItem != null) {
            Long valueOf = Long.valueOf(scheduleItem.getRecordStartTime());
            Message msg = getTopHandler().obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putLong("Duration", scheduleItem.getRecordDuration());
            msg.setData(bundle);
            msg.what = SCHEDULE_PVR_TASK_STOP_TIME;
            getTopHandler().sendMessage(msg);
            scheduleItem = null;
        }
    }

    /* access modifiers changed from: private */
    public void unmountEvent(String mountPointPath) {
        if ((getState() instanceof StateDvrFileList) && ((StateDvrFileList) getState()).isShowing()) {
            getInstance().restoreToDefault(getState());
            showPromptInfo(PRO_DISK_REMOVE);
        }
        if ((getState() instanceof StateDvrPlayback) && ((StateDvrPlayback) getState()).isRunning()) {
            showPromptInfo(PRO_DISK_REMOVE);
            StateDvrPlayback.getInstance().stopDvrFilePlay();
        }
        if (getPvrMountPoint() == null || mountPointPath.equalsIgnoreCase(getPvrMountPoint().mMountPoint)) {
            restoreToDefault(StatusType.DVR);
            setPvrMountPoint((MountPoint) null);
        }
    }

    public int getTVHeight() {
        return this.tvHeight;
    }

    public int getTVWidth() {
        return this.tvWidth;
    }

    public Context getContext() {
        return this.mContext;
    }

    public Controller getController() {
        return this.mController;
    }

    public boolean isExitMenu() {
        return this.menuExit;
    }

    public void setExitMenu(boolean value) {
        this.menuExit = value;
    }

    public TVLogicManager getTvLogicManager() {
        return this.mTVLogicManager;
    }

    public UImanager getUiManager() {
        return this.uiManager;
    }

    public boolean checkPvrMP() {
        ArrayList<MountPoint> list = getDeviceList();
        if (list != null && list.size() >= 1) {
            String mp = getSaveValue().readStrValue(Core.PVR_DISK);
            this.mDvrDiskMountPoint = list.get(0);
            for (int i = 0; i < list.size(); i++) {
                if (mp.equalsIgnoreCase(list.get(i).mMountPoint)) {
                    this.mDvrDiskMountPoint = list.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    public void setmTVLogicManager(TVLogicManager mTVLogicManager2) {
        this.mTVLogicManager = mTVLogicManager2;
    }

    public SaveValue getSaveValue() {
        if (this.saveValue == null) {
            this.saveValue = SaveValue.getInstance(this.mContext);
        }
        return this.saveValue;
    }

    public void setSaveValue(SaveValue saveValue2) {
        this.saveValue = saveValue2;
    }

    public boolean isPvrDialogShow() {
        return this.isPvrDialogShow;
    }

    public void setPvrDialogShow(boolean isPvrDialogShow2) {
        this.isPvrDialogShow = isPvrDialogShow2;
    }

    public MyHandler getTopHandler() {
        return this.topHandler;
    }

    public static MtkTvBookingBase getScheduleItem() {
        return scheduleItem;
    }

    public static void setScheduleItem(MtkTvBookingBase scheduleItem2) {
        scheduleItem = scheduleItem2;
    }

    public void showPromptInfo(int cases) {
        if (cases != 4) {
            switch (cases) {
                case 4096:
                    this.prompt = this.mContext.getString(R.string.pro_pip_running);
                    break;
                case 4097:
                    this.prompt = this.mContext.getString(R.string.pro_none_tv_source);
                    break;
                case 4098:
                    this.prompt = this.mContext.getString(R.string.feature_not_support);
                    break;
                case 4099:
                    this.prompt = this.mContext.getString(R.string.pro_signal_loss);
                    break;
                case 4100:
                    this.prompt = this.mContext.getString(R.string.pro_tshift_running);
                    break;
                case 4101:
                    this.prompt = this.mContext.getString(R.string.disk_not_ready);
                    break;
                case 4102:
                    this.prompt = this.mContext.getString(R.string.state_pvr_recording);
                    break;
                case 4103:
                    this.prompt = this.mContext.getString(R.string.pro_stream_not_available);
                    break;
                case 4104:
                    this.prompt = this.mContext.getString(R.string.pro_stream_not_authorized);
                    break;
                case 4105:
                    this.prompt = this.mContext.getString(R.string.pro_input_lock);
                    break;
                default:
                    switch (cases) {
                        case 4112:
                            this.prompt = this.mContext.getString(R.string.not_enough_space);
                            break;
                        case 4113:
                            this.prompt = this.mContext.getString(R.string.pro_video_resolution_error);
                            break;
                        case 4114:
                            this.prompt = this.mContext.getString(R.string.pro_internal_error);
                            break;
                        case PRO_UNKNOW_ERROR /*4115*/:
                            this.prompt = this.mContext.getString(R.string.feature_not_support);
                            break;
                        case PRO_PVR_STOP /*4116*/:
                            this.prompt = this.mContext.getString(R.string.pvr_save_record);
                            break;
                        case PRO_PVR_RUNNING /*4117*/:
                            this.prompt = this.mContext.getString(R.string.pro_pvr_running);
                            break;
                        case PRO_DISK_REMOVE /*4118*/:
                            this.prompt = this.mContext.getString(R.string.pro_disk_remove);
                            break;
                        case PRO_TIME_SHIFT_DISK_NOT_READY /*4119*/:
                            this.prompt = this.mContext.getString(R.string.pro_time_shift_disk_not_ready);
                            break;
                        case PRO_PVR_PLAY_SEEK /*4120*/:
                            this.prompt = this.mContext.getString(R.string.dvr_play_seek_msg);
                            break;
                        default:
                            this.prompt = "Feature not support.";
                            break;
                    }
            }
        } else {
            this.prompt = this.mContext.getString(R.string.attention_unplug_device);
        }
        ((Activity) this.mContext).runOnUiThread(new Runnable() {
            public void run() {
                DvrManager.this.mTVLogicManager.removeTwinkView();
                DvrManager.this.uiManager.showInfoBar(DvrManager.this.prompt);
            }
        });
    }

    public void showContinueToSeekNotSupport() {
        this.topHandler.sendEmptyMessage(CONTINUE_TO_SEEK_NOT_SUPPORT);
    }

    public void showFileNotSupport() {
        this.topHandler.sendEmptyMessage(FILE_NOT_SUPPORT);
    }

    public boolean diskIsReady() {
        return hasRemovableDisk();
    }

    public boolean isVisible() {
        if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
            return super.isVisible();
        }
        return true;
    }

    public void restoreToDefault(StateBase state) {
        restoreToDefault(state.getType());
    }

    public synchronized void restoreToDefault(StatusType... type) {
        MtkLog.printStackTrace();
        MtkLog.e(TAG, "restoreToDefault(...)");
        List<StatusType> typeList = Arrays.asList(type);
        for (int i = this.mStates.size() - 1; i >= 0; i--) {
            StateBase state = (StateBase) this.mStates.get(i);
            if (typeList.contains(state.getType())) {
                MtkLog.e(TAG, "restoreToNormal(...),Name:" + state.getType().name());
                this.mStates.remove(state);
                try {
                    this.mContext.sendBroadcastAsUser(new Intent("com.mtk.dialog.dismiss"), UserHandle.ALL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                state.onRelease();
            }
        }
        updateVisibility();
    }

    public boolean isKeyHandler(int keyCode) {
        MtkLog.e(TAG, "isKeyHandler:" + keyCode);
        return onKeyDown(keyCode);
    }

    public boolean isCoExist(int componentID) {
        switch (componentID) {
            case NavBasic.NAV_COMP_ID_CEC /*16777220*/:
            case 16777232:
                return true;
            case NavBasic.NAV_COMP_ID_CH_LIST /*16777221*/:
            case NavBasic.NAV_COMP_ID_SUNDRY /*16777222*/:
            case NavBasic.NAV_COMP_ID_ZOOM_PAN /*16777223*/:
            case NavBasic.NAV_COMP_ID_INPUT_SRC /*16777230*/:
            case NavBasic.NAV_COMP_ID_POP /*16777235*/:
            case NavBasic.NAV_COMP_ID_PVR_TIMESHIFT /*16777241*/:
                return true;
            case NavBasic.NAV_COMP_ID_FAV_LIST /*16777226*/:
            case NavBasic.NAV_COMP_ID_VOL_CTRL /*16777227*/:
            case NavBasic.NAV_COMP_ID_PWD_DLG /*16777234*/:
                return false;
            default:
                return super.isCoExist(componentID);
        }
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.e(TAG, "KeyHandler keyCode = " + keyCode);
        return onKeyDown(keyCode);
    }

    public boolean onKeyDown(int keyCode) {
        if (this.isBGMState) {
            DvrDialog pvrDialog = new DvrDialog(this.mContext, 0);
            MtkLog.e(TAG, "onKeyDown, timeshiftmanager:in");
            pvrDialog.show();
        }
        Iterator it = this.mStates.iterator();
        boolean handled = false;
        dumpStatesStack();
        synchronized (this) {
            for (int i = this.mStates.size() - 1; i >= 0; i--) {
                StateBase state = (StateBase) this.mStates.get(i);
                handled = state.onKeyDown(keyCode);
                if (handled) {
                    MtkLog.e(TAG, "KeyHandler," + state.getType().name() + "," + handled);
                    return true;
                }
            }
            return handled;
        }
    }

    public void setVisibility(int visibility) {
        MtkLog.d(TAG, "setVisibility = " + visibility);
        super.setVisibility(visibility);
        if (visibility == 4 && StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
            getInstance().restoreToDefault((StateBase) StateDvrFileList.getInstance());
        }
    }

    public void updateVisibility() {
        dumpStatesStack();
        int value = getVisibility();
        if (value == 0) {
            getTvLogicManager().removeTwinkView();
        }
        setVisibility(value);
    }

    private int getVisibility() {
        Iterator<StateBase> states = this.mStates.iterator();
        do {
            switch (states.next().getType()) {
                case DVR:
                case FILELIST:
                case PLAYBACK:
                    return 0;
            }
        } while (states.hasNext());
        return 4;
    }

    public void restoreAllToNormal() {
        do {
            StateBase state = (StateBase) this.mStates.lastElement();
            if (state.getType() != StatusType.NORMAL) {
                MtkLog.e(TAG, "Name:" + state.getType().name());
                state.onRelease();
                this.mStates.remove(state);
            }
        } while (this.mStates.size() > 1);
        updateVisibility();
    }

    public void restoreAllAfterException() {
        if (getController() != null) {
            getController().stopRecording();
        }
    }

    public float getDiskSpeed() {
        float size = getSaveValue().readFloatValue(Core.DISK_SPEED);
        if (size == 0.0f) {
            return 6.0f;
        }
        return size;
    }

    private Long getDiskFreesize(int selectedDisk) {
        return Long.valueOf(DevManager.getInstance().getMountList().get(selectedDisk).mFreeSize);
    }

    public Long getDiskFreesize(MountPoint mp) {
        if (mp == null) {
            return 0L;
        }
        return Long.valueOf(mp.mFreeSize);
    }

    public String[] getSizeList(boolean auto, Long size) {
        return Util.covertFreeSizeToArray(auto, size);
    }

    public boolean isPipPopMode() {
        return getTvLogicManager().isPipPopMode();
    }

    public boolean isScanning() {
        return getTvLogicManager().isScanning();
    }

    public boolean pvrIsRecording() {
        if (mDvrManager == null || StateDvr.getInstance() == null || !StateDvr.getInstance().isRecording()) {
            return false;
        }
        return true;
    }

    public void prepareSchedulePvrTask(int taskID) {
        MtkLog.d(TAG, "prepareSchedulePvrTask, " + taskID + ", " + scheduleItem);
        if (scheduleItem == null) {
            return;
        }
        if (TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager() == null || !SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
            DvrDialog conDialog2 = new DvrDialog((Activity) this.mContext, 1, DvrDialog.TYPE_SCHEDULE, 1);
            conDialog2.setScheduleItem(scheduleItem);
            conDialog2.show();
            new ScheduleHandler(conDialog2, scheduleItem).sendEmptyMessageDelayed(0, MessageType.delayMillis2);
            return;
        }
        DvrDialog stopTshift = new DvrDialog((Activity) this.mContext, 1, DvrDialog.TYPE_TSHIFT, 1);
        stopTshift.setScheduleItem(scheduleItem);
        stopTshift.show();
        new ScheduleHandler(stopTshift, scheduleItem).sendEmptyMessageDelayed(0, MessageType.delayMillis2);
    }

    public void startSchedulePvr() {
        MtkLog.d(TAG, "startSchedulePvr");
        if (!diskIsReady()) {
            handlerScheduleData(this.index);
            showPromptInfo(4101);
            MtkLog.d(TAG, "disk not ready");
            this.index = -1;
            return;
        }
        this.mContext.sendBroadcast(new Intent("com.mtk.dialog.dismiss"));
        if (scheduleItem == null) {
            MtkLog.d(TAG, "scheduleItem == null ");
        } else if (scheduleItem.getChannelId() > 0) {
            MtkLog.d("1111+", "is =" + CommonIntegration.getInstance().getTunerMode() + "," + scheduleItem.getTunerType());
            MtkLog.d(TAG, "scheduleItem.getTunerType() " + MarketRegionInfo.getCurrentMarketRegion() + "," + 16);
            if (CommonIntegration.getInstance().getTunerMode() == scheduleItem.getTunerType()) {
                setScheduleStart();
            } else if (CommonIntegration.getInstance().isCurrentSourceATV()) {
                InputSourceManager.getInstance().changeCurrentInputSourceByName("DTV");
                this.mScheduleHandler.sendEmptyMessageDelayed(3, 1000);
            } else {
                MtkLog.d(TAG, "scheduleItem.getTunerType() == > error");
            }
        } else {
            MtkLog.d(TAG, "scheduleItem.getChannelId() = " + scheduleItem.getChannelId());
        }
    }

    private void setScheduleStart() {
        if (getInstance().pvrIsRecording()) {
            MtkLog.d("1111+", "is recording");
            StateDvr.getInstance().getHandler().removeMessages(6);
            getController().stopRecording();
            return;
        }
        TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(scheduleItem.getChannelId());
        MtkLog.d(TAG, "uri==" + TvContract.buildChannelUri(tifChannelInfo.mId));
        TurnkeyUiMainActivity.getInstance().processInputUri(TvContract.buildChannelUri(tifChannelInfo.mId));
        SaveValue saveValue2 = this.saveValue;
        saveValue2.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
        this.mScheduleHandler.sendEmptyMessageDelayed(3, 500);
    }

    public void clearSchedulePvr() {
        scheduleItem = null;
        setScheduleItem((MtkTvBookingBase) null);
    }

    public void setPVRStopTime(Long duration) {
        MtkLog.d(TAG, "setPVRStopTime, " + duration);
        StateDvr.getInstance().setSchedulePVRDuration(new Long(duration.longValue()).intValue());
    }

    public void setDuration() {
        this.timeduration = getDuration(scheduleItem);
    }

    public long getDurations() {
        return this.timeduration;
    }

    public static boolean isSuppport() {
        return MarketRegionInfo.isFunctionSupport(30);
    }

    public ArrayList<MountPoint> getDeviceList() {
        return DeviceManager.getInstance().getMountPointList();
    }

    public static boolean isSuppportDualTuner() {
        return MarketRegionInfo.isFunctionSupport(32);
    }

    public boolean isMountPointExist(MountPoint mp) {
        if (mp == null) {
            return false;
        }
        Iterator<MountPoint> it = getDeviceList().iterator();
        while (it.hasNext()) {
            if (mp.mMountPoint.equalsIgnoreCase(it.next().mMountPoint)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRemovableDisk() {
        MtkLog.e(TAG, "hasRemovableDisk:" + getDeviceList().size());
        if (getDeviceList().size() > 0) {
            return true;
        }
        return false;
    }

    public MountPoint getPvrMountPoint() {
        if (!isMountPointExist(this.mDvrDiskMountPoint)) {
            setDefaultPvrMountPoint();
        }
        return this.mDvrDiskMountPoint;
    }

    public boolean setDefaultPvrMountPoint() {
        ArrayList<MountPoint> list = getDeviceList();
        if (list == null || list.size() < 1) {
            return false;
        }
        setPvrMountPoint(list.get(0));
        return true;
    }

    public void setPvrMountPoint(MountPoint pvrDisk) {
        this.mDvrDiskMountPoint = pvrDisk;
    }

    public boolean timeShiftIsEnable() {
        if (MenuConfigManager.getInstance(this.mContext).getDefault("g_record__rec_tshift_mode") == 1) {
            return true;
        }
        return false;
    }

    public boolean getTTSEnable() {
        return this.ttUtil.isTTSEnabled();
    }

    public StateBase getState(StatusType type) {
        Iterator<StateBase> statesIter = this.mStates.iterator();
        do {
            StateBase state = statesIter.next();
            if (state.getType() == type) {
                return state;
            }
        } while (statesIter.hasNext());
        return null;
    }

    public void setBGMState(boolean value) {
        this.isBGMState = value;
    }

    public StateBase getState() {
        return this.mCurrentState;
    }

    public boolean setState(StateBase state) {
        if (state == null) {
            return false;
        }
        int index2 = this.mStates.indexOf(state);
        if (index2 >= 0) {
            this.mStates.remove(index2);
        }
        this.mStates.push(state);
        this.mCurrentState = state;
        updateVisibility();
        return true;
    }

    public boolean removeState(StateBase state) {
        int index2;
        boolean result = false;
        if (state != null && (index2 = this.mStates.indexOf(state)) >= 0) {
            this.mStates.remove(index2);
            result = true;
        }
        updateVisibility();
        MtkLog.i(TAG, "removeState== current State is  " + state + " and result is " + result);
        return result;
    }

    public void hideStateView() {
        Iterator<StateBase> statesIter = this.mStates.iterator();
        do {
            statesIter.next().hideView();
        } while (statesIter.hasNext());
    }

    public void showStateView(StatusType type) {
        Iterator<StateBase> statesIter = this.mStates.iterator();
        do {
            StateBase state = statesIter.next();
            if (state.getType() == type) {
                state.showView();
            }
        } while (statesIter.hasNext());
    }

    public void speakText(String str) {
        if (mDvrManager == null || this.ttUtil == null) {
            MtkLog.e(TAG, "mDvrManager==NULL  or   textToSpeachUtil ==NULL");
        } else {
            this.ttUtil.speak(str);
        }
    }

    private synchronized void dumpStatesStack() {
        String log = "\n==dumpStatesStack==\n";
        for (int i = this.mStates.size() - 1; i >= 0; i--) {
            log = log + "==" + ((StateBase) this.mStates.get(i)).getType().name() + "\n";
        }
        MtkLog.e(TAG, log + "==end");
    }

    public void onEvent(DeviceManagerEvent event) {
        MtkLog.e(TAG, "DeviceManagerEvent: " + event.getType());
        switch (event.getType()) {
            case 602:
                MtkLog.e(TAG, "DeviceManagerEvent.umounted");
                Message msg = mDvrManager.getTopHandler().obtainMessage();
                msg.what = UNMOUNT_EVENT;
                Bundle bundle = new Bundle();
                bundle.putString(UNMOUNT_EVENT_MSG_KEY, event.getMountPointPath());
                msg.setData(bundle);
                mDvrManager.getTopHandler().sendMessage(msg);
                return;
            default:
                return;
        }
    }

    public void enqPinSuccess() {
        if (scheduleItem != null) {
            toRecord();
        }
    }

    public void enqPinWaitInputState(boolean isPincodeShow) {
    }

    public boolean tshiftIsRunning() {
        if (mDvrManager == null) {
            return false;
        }
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            return true;
        }
        if (StateDvrFileList.getInstance() == null || !StateDvrFileList.getInstance().isRunning()) {
            return false;
        }
        return true;
    }

    public void getPVRSrc() {
        ChannelListDialog mChannelDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (mChannelDialog != null && mChannelDialog.isShowing()) {
            mChannelDialog.dismiss();
        }
        MtkLog.e(TAG, "getPVRSrc, removeChannelList");
    }

    public String getPVRRecordingSrc() {
        if (StateDvr.getInstance() != null) {
            return StateDvr.getInstance().getPvrSource();
        }
        return null;
    }

    public boolean isStopDvrNotResumeLauncher() {
        return this.isStopDvrNotResumeLauncher;
    }

    public void setStopDvrNotResumeLauncher(boolean isStopDvrNotResumeLauncher2) {
        this.isStopDvrNotResumeLauncher = isStopDvrNotResumeLauncher2;
    }

    public boolean isInPictureMode() {
        if (TurnkeyUiMainActivity.getInstance() == null) {
            return false;
        }
        boolean is = TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode();
        MtkLog.d(TAG, "isInPictureMode = " + is);
        return is;
    }

    public void startScheduleList(EPGProgramInfo programInfo) {
        boolean isCurrentSourceDTV = CommonIntegration.getInstance().isCurrentSourceDTV();
        MtkLog.d(TAG, "isCurrentSourceDTV=" + isCurrentSourceDTV);
        if (!isCurrentSourceDTV) {
            Toast.makeText(this.mContext, R.string.nav_no_support_pvr_for_tvsource, 0).show();
        } else if (programInfo == null) {
            MtkLog.e(TAG, "start scheduleList failed for programInfo==null");
        } else {
            Long startTime = Long.valueOf(programInfo.getmStartTime().longValue() * 1000);
            if (startTime.longValue() == 0) {
                MtkLog.e(TAG, "startTime is invalid value!");
                return;
            }
            MtkTvBookingBase item = new MtkTvBookingBase();
            MtkLog.d(TAG, "start time in epg:" + startTime);
            MtkTvTimeFormatBase from = new MtkTvTimeFormatBase();
            MtkTvTimeFormatBase to = new MtkTvTimeFormatBase();
            from.setByUtc(startTime.longValue() / 1000);
            MtkTvTimeBase time = new MtkTvTimeBase();
            time.convertTime(5, from, to);
            Long startTime2 = Long.valueOf(to.toSeconds() * 1000);
            MtkLog.d(TAG, "startTime=" + startTime2 + " str = " + Util.timeToTimeStringEx(startTime2.longValue() * 1000, 0));
            if (startTime2.longValue() != -1) {
                item.setRecordStartTime(startTime2.longValue() / 1000);
            }
            from.setByUtc(Long.valueOf(programInfo.getmEndTime().longValue() * 1000).longValue() / 1000);
            time.convertTime(5, from, to);
            Long endTime = Long.valueOf(to.toSeconds() * 1000);
            MtkLog.d(TAG, "endTime=" + endTime);
            if (endTime.longValue() != -1) {
                item.setRecordDuration((endTime.longValue() / 1000) - (startTime2.longValue() / 1000));
            }
            item.setTunerType(CommonIntegration.getInstance().getTunerMode());
            if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext) == null) {
                ScheduleListItemInfoDialog mDialog = new ScheduleListItemInfoDialog(this.mContext, item);
                mDialog.setEpgFlag(true);
                mDialog.show();
            } else if (!ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext).isShowing()) {
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext).setEpgFlag(true);
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext).show();
            }
        }
    }
}
