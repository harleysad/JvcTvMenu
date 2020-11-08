package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrFilelist;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeshiftView;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class StateDvrFileList extends StateBase implements AdapterView.OnItemClickListener {
    public static final int AUTO_DISMISS_FILE_LIST = 21;
    public static final int DELAY_RELEASE_STATE = 40961;
    private static final int DELETE_FILE_ = 110;
    private static final int HIDE_BANNER = 112;
    private static final int INIT_PLAYER = 1;
    private static final int MSG_GET_CUR_POS = 17;
    private static final int PROGRESS_CHANGED = 11;
    public static final int RESTORE_TO_NORMAL = 40963;
    public static final int SET_PVR_FILE = 40964;
    private static final int START_PLAYER = 2;
    private static final int STOP_PLAYER = 3;
    private static final int STOP_PLAYING = 111;
    private static final int Show_Progress = 18;
    private static final String TAG = "StateFileListDvr";
    public static final int UPDATE_SPEED = 40962;
    /* access modifiers changed from: private */
    public static boolean getListFlag = false;
    /* access modifiers changed from: private */
    public static DvrFilelist mFileListWindow;
    /* access modifiers changed from: private */
    public static List<DVRFiles> mPVRFileList;
    /* access modifiers changed from: private */
    public static StateDvrFileList mStateSelf;
    /* access modifiers changed from: private */
    public boolean flag = false;
    /* access modifiers changed from: private */
    public final MyHandler mHandler;
    private StateFileReceive sfReceive = null;

    static class MyHandler extends Handler {
        MyHandler() {
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 21) {
                if (i == 110) {
                    removeMessages(110);
                    StateDvrFileList.mStateSelf.delete();
                } else if (i != 112) {
                    switch (i) {
                        case 40963:
                            StateDvrFileList.mStateSelf.getManager().restoreAllToNormal();
                            break;
                        case StateDvrFileList.SET_PVR_FILE /*40964*/:
                            boolean unused = StateDvrFileList.getListFlag = false;
                            if (StateDvrFileList.mFileListWindow != null) {
                                StateDvrFileList.mFileListWindow.setmFileList(StateDvrFileList.mPVRFileList);
                                StateDvrFileList.mFileListWindow.initList();
                                break;
                            }
                            break;
                    }
                } else {
                    ((BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER)).setVisibility(8);
                }
            } else if (StateDvrFileList.mFileListWindow != null && StateDvrFileList.mFileListWindow.isShowing()) {
                DvrManager.getInstance().restoreToDefault(StatusType.FILELIST);
            }
            super.handleMessage(msg);
        }
    }

    /* access modifiers changed from: private */
    public void updateRecordOrPlayFileParams() {
    }

    /* access modifiers changed from: private */
    public void updateRecordOrPlayFileParams2() {
        mPVRFileList.clear();
        mPVRFileList = new ArrayList();
        new Thread(new Runnable() {
            public void run() {
                if (!StateDvrFileList.this.flag) {
                    boolean unused = StateDvrFileList.this.flag = true;
                    List<DVRFiles> files = StateDvrFileList.this.getManager().getController().getPvrFiles();
                    if (files != null) {
                        StateDvrFileList.mPVRFileList.addAll(files);
                        StateDvrFileList.this.updateRecordOrPlayFileParams();
                        StateDvrFileList.this.mHandler.sendEmptyMessage(StateDvrFileList.SET_PVR_FILE);
                        boolean unused2 = StateDvrFileList.this.flag = false;
                        return;
                    }
                    boolean unused3 = StateDvrFileList.this.flag = false;
                    StateDvrFileList.this.mHandler.sendEmptyMessage(StateDvrFileList.SET_PVR_FILE);
                }
            }
        }).start();
    }

    public StateDvrFileList(Context mContext, DvrManager manager) {
        super(mContext, manager);
        setType(StatusType.FILELIST);
        this.mContext = mContext;
        mStateSelf = this;
        this.mHandler = new MyHandler();
    }

    public void initControlView() {
    }

    public void recoveryView() {
        if (mFileListWindow != null) {
            mFileListWindow = null;
        }
    }

    public void showPVRlist() {
        try {
            TifTimeshiftView tifTimeshiftView = (TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW);
            if (tifTimeshiftView != null) {
                tifTimeshiftView.startTimeout(0);
            }
            this.sfReceive = new StateFileReceive();
            this.mContext.registerReceiver(this.sfReceive, new IntentFilter("com.mtk.state.file"));
            getManager().getTvLogicManager().removeChannelList(this.mContext);
            initPVRlist();
            if (mFileListWindow != null) {
                ZoomTipView mZoomTip = (ZoomTipView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
                if (mZoomTip != null) {
                    mZoomTip.setVisibility(8);
                }
                SundryShowTextView stxtView = (SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
                if (stxtView != null) {
                    stxtView.setVisibility(8);
                }
                this.mHandler.sendEmptyMessageDelayed(112, 500);
                mFileListWindow.setmFileList(mPVRFileList);
                mFileListWindow.initList();
                mFileListWindow.show();
            }
            if (!getListFlag) {
                getListFlag = true;
                mPVRFileList = new ArrayList();
                mPVRFileList.clear();
                new Thread(new Runnable() {
                    public void run() {
                        List<DVRFiles> files = StateDvrFileList.this.getManager().getController().getPvrFiles();
                        if (files != null) {
                            StateDvrFileList.mPVRFileList.addAll(files);
                            StateDvrFileList.this.updateRecordOrPlayFileParams();
                            StateDvrFileList.this.mHandler.sendEmptyMessage(StateDvrFileList.SET_PVR_FILE);
                            return;
                        }
                        boolean unused = StateDvrFileList.getListFlag = false;
                        StateDvrFileList.this.mHandler.sendEmptyMessage(StateDvrFileList.SET_PVR_FILE);
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPVRlist() {
        mFileListWindow = new DvrFilelist((Activity) this.mContext, this, this.mHandler);
        mFileListWindow.setListener(this);
        mFileListWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface arg0) {
            }
        });
    }

    public void reShowPVRlist() {
        if (mFileListWindow == null || !mFileListWindow.isShowing()) {
            mFileListWindow = null;
            showPVRlist();
            return;
        }
        this.mHandler.removeMessages(21);
        this.mHandler.sendEmptyMessageDelayed(21, MessageType.delayMillis10);
    }

    public void deletePvrFile() {
        this.mHandler.sendEmptyMessage(110);
    }

    public void delete() {
        mFileListWindow.deleteFile();
    }

    public DVRFiles getSelectedFile() {
        return mFileListWindow.getSelectedFile();
    }

    public boolean onKeyDown(int keycode) {
        switch (keycode) {
            case 4:
                if (mFileListWindow.isShowing()) {
                    mFileListWindow.dimissInfobar();
                }
                unRegister();
                getManager().restoreToDefault((StateBase) mStateSelf);
                return true;
            case 19:
            case 20:
            case 23:
            case KeyMap.KEYCODE_MTKIR_INFO /*165*/:
            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                return true;
            case 21:
            case 22:
            case 85:
            case 89:
            case 90:
            case 93:
            case 130:
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
            case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
            case KeyMap.KEYCODE_MTKIR_SOURCE /*178*/:
            case 227:
            case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
            case 10065:
                return true;
            case 24:
            case 25:
            case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
            case KeyMap.KEYCODE_MTKIR_FREEZE /*10467*/:
                return true;
            case 82:
                mFileListWindow.dismiss();
                break;
            case 86:
                return false;
            case 215:
                return true;
            case 222:
            case KeyMap.KEYCODE_MTKIR_SLEEP /*223*/:
            case 251:
            case 255:
            case 10066:
            case 10471:
                if (mFileListWindow.isShowing()) {
                    mFileListWindow.dimissInfobar();
                }
                getManager().restoreToDefault((StateBase) mStateSelf);
                unRegister();
                return false;
            case 10062:
                DvrManager.getInstance().uiManager.hiddenAllViews();
                if (!mFileListWindow.isShowing()) {
                    PwdDialog mPwdDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
                    if (mPwdDialog != null && mPwdDialog.isVisible()) {
                        mPwdDialog.dismiss();
                    }
                    reShowPVRlist();
                    return true;
                }
                unRegister();
                if (!mFileListWindow.isShowing()) {
                    return false;
                }
                if (mFileListWindow != null) {
                    mFileListWindow.dimissInfobar();
                    this.mHandler.removeMessages(21);
                    getManager().restoreToDefault((StateBase) mStateSelf);
                }
                return true;
        }
        return false;
    }

    public void unRegister() {
        if (this.sfReceive != null) {
            try {
                this.mContext.unregisterReceiver(this.sfReceive);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean dispatchTheOnkeyDown(int keycode) {
        switch (keycode) {
        }
        return false;
    }

    public void onResume() {
    }

    public void onPause() {
        if (mFileListWindow != null && mFileListWindow.isShowing()) {
            mFileListWindow.setOnDismissListener((DialogInterface.OnDismissListener) null);
            mFileListWindow.dimissInfobar();
        }
    }

    public void onStop() {
    }

    public void onRelease() {
        if (mFileListWindow != null && mFileListWindow.isShowing()) {
            mFileListWindow.setOnDismissListener((DialogInterface.OnDismissListener) null);
            mFileListWindow.dimissInfobar();
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MultiViewControl mMultiViewControl = (MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP);
        if (mMultiViewControl != null) {
            mMultiViewControl.setNormalTvModeWithLauncher(false);
        }
        if (ComponentsManager.getActiveCompId() == 16777230) {
            ((SourceListView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC)).dismiss();
        }
        if (mFileListWindow != null && mFileListWindow.isShowing()) {
            mFileListWindow.dismiss();
        }
        if (getInstance() != null) {
            DvrManager.getInstance().removeState(getInstance());
        }
        ComponentsManager.getInstance().hideAllComponents();
        playDvrFile(parent, position);
    }

    private void playDvrFile(AdapterView<?> parent, int position) {
        DVRFiles dvrFiles = (DVRFiles) parent.getItemAtPosition(position);
        Uri uri = dvrFiles.getProgarmUri();
        if (uri != null) {
            MtkLog.i(TAG, "dvr file play start !  uri==" + uri);
            DvrManager.getInstance().setState((StateBase) StateDvrPlayback.getInstance(getManager()));
            StateDvrPlayback.getInstance().prepareDvrFilePlay(dvrFiles);
            return;
        }
        MtkLog.e(TAG, "uri==null");
    }

    private void playPvrFileByMMPModule(AdapterView<?> adapterView, int position) {
    }

    public static StateDvrFileList getInstance() {
        return mStateSelf;
    }

    public static StateDvrFileList getInstance(DvrManager manager) {
        if (mStateSelf == null) {
            DvrManager manager2 = DvrManager.getInstance();
            mStateSelf = new StateDvrFileList(manager2.getContext(), manager2);
        }
        return mStateSelf;
    }

    public boolean isShowing() {
        if (mFileListWindow != null) {
            return mFileListWindow.isShowing();
        }
        return false;
    }

    public void dissmiss() {
        if (mFileListWindow.isShowing()) {
            mFileListWindow.dimissInfobar();
        }
        unRegister();
        getManager().restoreToDefault((StateBase) mStateSelf);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    class StateFileReceive extends BroadcastReceiver {
        StateFileReceive() {
        }

        public void onReceive(Context context, Intent intent) {
            StateDvrFileList.this.updateRecordOrPlayFileParams2();
        }
    }
}
