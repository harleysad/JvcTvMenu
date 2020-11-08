package com.mediatek.wwtv.tvcenter.oad;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.commonview.CustListView;
import com.mediatek.wwtv.tvcenter.commonview.Loading;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.Constants;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class NavOADActivity extends BaseActivity {
    static final int AUTO_DOWNLOAD_COUNTDOWN = 0;
    static final int AUTO_EXIT_OAD = 5;
    static final int AUTO_EXIT_TIME = 300000;
    static final int AUTO_JUMP_CHANNEL = 4;
    static final int DELAY_SHOW_CONFIRM_UI = 2;
    static final int DOWN_LOAD_TIME_OUT_MSG = 3;
    static final int NOTIFY_CALL_BACK = 1;
    private static boolean SHOW_DOWN_LOAD_FAIL = true;
    private static final String TAG = "NavOADActivity";
    private static NavOADActivity oadActivity = null;
    private final boolean DEBUG = false;
    private final int OAD_INFORM_DOWNLOAD_FAIL = 6;
    private final int OAD_INFORM_DOWNLOAD_PROGRESS = 5;
    private final int OAD_INFORM_FILE_FOUND = 0;
    private final int OAD_INFORM_FILE_NOT_FOUND = 1;
    private final int OAD_INFORM_INSTALL = 7;
    private final int OAD_INFORM_INSTALL_PROGRESS = 8;
    private final int OAD_INFORM_INST_FAIL = 9;
    private final int OAD_INFORM_LINKAGE = 4;
    private final int OAD_INFORM_NEWEST_VERSION = 2;
    private final int OAD_INFORM_SCHEDULE = 3;
    private final int OAD_INFORM_SUCCESS = 10;
    private final int OAD_MTKTVAPI_INFORM_AUTO_DOWNLOAD_WITH_BGM = 13;
    private final int OAD_MTKTVAPI_INFORM_JUMP_SCHEDULE = 11;
    private final String OAD_PROP = Constants.MTK_3RD_APP_FLAG;
    private NavOADController controller;
    /* access modifiers changed from: private */
    public int currentDownloadProgress = 0;
    private BaseOADState currentState;
    /* access modifiers changed from: private */
    public boolean downLoadRetry = false;
    Intent intent;
    private boolean isRemindMeLater = true;
    private CustListView mBtnList;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public int mCountDownInt = 15;
    private TextView mCurrentVersion;
    private MyHandler mHandler;
    private boolean mIsStop;
    /* access modifiers changed from: private */
    public int mJumpChannelRemindTime = 10;
    private int mListViewWidth = 0;
    private Loading mLoadingPoint;
    private TextView mOADSubTitle;
    private TextView mOADSubTitle2;
    private TextView mOADWarnningMsg;
    private String mPackagePathAndName = null;
    private ProgressBar mProgress;
    private TextView mProgressStr;
    /* access modifiers changed from: private */
    public String mScheuleInfo;
    private TextView mTvFootExit;
    private TextView mTvFootNext;
    private TextView mTvFootSelect;

    interface IStepSquence {
        void lastPage();

        void nextPage();

        void updateUi();
    }

    enum Step {
        DEFAULT,
        DETECT,
        DOWNLOAD_CONFIRM,
        DOWNLOADING,
        FLASH_CONFIRM,
        FLASHING,
        RESTART_CONFIRM
    }

    static /* synthetic */ int access$010(NavOADActivity x0) {
        int i = x0.mCountDownInt;
        x0.mCountDownInt = i - 1;
        return i;
    }

    static /* synthetic */ int access$210(NavOADActivity x0) {
        int i = x0.mJumpChannelRemindTime;
        x0.mJumpChannelRemindTime = i - 1;
        return i;
    }

    static class MyHandler extends Handler {
        WeakReference<NavOADActivity> mActivity;

        MyHandler(NavOADActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(NavOADActivity.TAG, "handleMessageMsg.what = " + msg.what);
            switch (msg.what) {
                case 0:
                    if (!(this.mActivity == null || this.mActivity.get() == null || ((NavOADActivity) this.mActivity.get()).getCurrentState() == null)) {
                        if (((NavOADActivity) this.mActivity.get()).getCurrentState().step != Step.DETECT) {
                            NavOADActivity.access$010((NavOADActivity) this.mActivity.get());
                            if (((NavOADActivity) this.mActivity.get()).mCountDownInt <= 0) {
                                ((NavOADActivity) this.mActivity.get()).getCurrentState().nextPage();
                                int unused = ((NavOADActivity) this.mActivity.get()).mCountDownInt = 15;
                                break;
                            } else {
                                sendEmptyMessageDelayed(0, 1000);
                                ((NavOADActivity) this.mActivity.get()).updateAutoDownloadMsg(((NavOADActivity) this.mActivity.get()).mCountDownInt);
                                break;
                            }
                        } else {
                            int unused2 = ((NavOADActivity) this.mActivity.get()).mCountDownInt = 15;
                            return;
                        }
                    }
                case 1:
                    Bundle bundle = msg.getData();
                    ((NavOADActivity) this.mActivity.get()).onNotifyOADMessage(bundle.getInt("arg1"), bundle.getString("arg2"), bundle.getInt("arg3"), bundle.getBoolean("arg4"), bundle.getInt("arg5"));
                    break;
                case 2:
                    TvCallbackData data = (TvCallbackData) msg.obj;
                    ((NavOADActivity) this.mActivity.get()).onNotifyOADMessage(data.param1, data.paramStr1, data.param2, data.paramBool1, data.param3);
                    break;
                case 3:
                    ((NavOADActivity) this.mActivity.get()).setDownloadFailState();
                    break;
                case 4:
                    if (!(this.mActivity == null || this.mActivity.get() == null || ((NavOADActivity) this.mActivity.get()).getCurrentState() == null)) {
                        NavOADActivity.access$210((NavOADActivity) this.mActivity.get());
                        if (((NavOADActivity) this.mActivity.get()).mJumpChannelRemindTime <= 0) {
                            ((JumpChannelState) ((NavOADActivity) this.mActivity.get()).getCurrentState()).nextPage();
                            int unused3 = ((NavOADActivity) this.mActivity.get()).mJumpChannelRemindTime = 10;
                            break;
                        } else {
                            sendEmptyMessageDelayed(4, 1000);
                            ((NavOADActivity) this.mActivity.get()).updateAutoJumpChannelMsg(((NavOADActivity) this.mActivity.get()).mJumpChannelRemindTime);
                            break;
                        }
                    }
                case 5:
                    ((NavOADActivity) this.mActivity.get()).stopOAD(false);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    class BaseOADState implements IStepSquence {
        boolean onBackPress;
        Step step;

        public BaseOADState() {
            this.onBackPress = true;
            this.step = Step.DEFAULT;
            this.step = Step.DEFAULT;
        }

        public void nextPage() {
            if (NavOADActivity.this.getmHandler() != null) {
                NavOADActivity.this.getmHandler().removeMessages(0);
                NavOADActivity.this.getmHandler().removeMessages(5);
            }
            int unused = NavOADActivity.this.mCountDownInt = 15;
        }

        public void updateUi() {
        }

        public void clearTask() {
        }

        public void lastPage() {
        }
    }

    class DetectOADState extends BaseOADState {
        DetectOADState() {
            super();
        }

        public void updateUi() {
            this.step = Step.DETECT;
            NavOADActivity.this.showScanning();
            NavOADActivity.this.getController().remindMeLater();
            NavOADActivity.this.getController().manualDetect();
        }

        public void clearTask() {
            super.clearTask();
            NavOADActivity.this.getController().cancelManualDetect();
            NavOADActivity.this.finish();
        }
    }

    class DownloadConfirmOADState extends BaseOADState {
        DownloadConfirmOADState() {
            super();
        }

        public void nextPage() {
            super.nextPage();
            NavOADActivity.this.getController().acceptDownload();
            NavOADActivity.this.setCurrentState(new DownloadingOADState());
        }

        public void updateUi() {
            this.step = Step.DOWNLOAD_CONFIRM;
            NavOADActivity.this.showDownloadConfirm(NavOADController.getOADAutoDownload(NavOADActivity.this.mContext));
        }

        public void clearTask() {
            super.clearTask();
            NavOADActivity.this.stopOAD(false);
        }
    }

    class DownloadingOADState extends BaseOADState {
        DownloadingOADState() {
            super();
        }

        public void updateUi() {
            NavOADActivity.this.getmHandler().sendEmptyMessageDelayed(3, 3600000);
            this.step = Step.DOWNLOADING;
            NavOADActivity.this.showProgress();
        }

        public void lastPage() {
            super.lastPage();
        }

        public void clearTask() {
            super.clearTask();
            NavOADActivity.this.getController().cancelDownload();
            NavOADActivity.this.stopOAD(false);
        }
    }

    class DownloadFailOADState extends BaseOADState {
        DownloadFailOADState() {
            super();
        }

        public void updateUi() {
            NavOADActivity.this.showDownloadFail();
        }
    }

    class FlashConfirmOADState extends BaseOADState {
        FlashConfirmOADState() {
            super();
        }

        public void nextPage() {
            super.nextPage();
            NavOADActivity.this.getController().acceptFlash();
            NavOADActivity.this.setCurrentState(new FlashingState());
        }

        public void updateUi() {
            NavOADActivity.this.showFlashConfirm();
        }
    }

    class FlashingState extends BaseOADState {
        FlashingState() {
            super();
        }

        public void updateUi() {
            this.step = Step.FLASHING;
            NavOADActivity.this.showProgress();
        }
    }

    class FlashFailState extends BaseOADState {
        FlashFailState() {
            super();
        }

        public void updateUi() {
            NavOADActivity.this.showFlashFail();
        }
    }

    class RestartConfirmOADState extends BaseOADState {
        RestartConfirmOADState() {
            super();
        }

        public void nextPage() {
            super.nextPage();
        }

        public void updateUi() {
            NavOADActivity.this.showRestartConfirm();
        }
    }

    class ScheduleInfoState extends BaseOADState {
        ScheduleInfoState() {
            super();
        }

        public void updateUi() {
            String unused = NavOADActivity.this.mScheuleInfo = NavOADActivity.this.getController().getScheduleInfo();
            NavOADActivity.this.showScheduleInfo(NavOADActivity.this.mScheuleInfo);
        }
    }

    class JumpChannelState extends BaseOADState {
        JumpChannelState() {
            super();
        }

        public void updateUi() {
            int unused = NavOADActivity.this.mJumpChannelRemindTime = 10;
            NavOADActivity.this.showJumpChannelInfo();
        }

        public void nextPage() {
            if (NavOADActivity.this.getmHandler() != null) {
                NavOADActivity.this.getmHandler().removeMessages(4);
            }
            NavOADActivity.this.getController().acceptJumpChannel();
            NavOADActivity.this.stopOAD(false);
        }
    }

    class BGMAutoDownloadState extends BaseOADState {
        BGMAutoDownloadState() {
            super();
        }

        public void updateUi() {
            NavOADActivity.this.showBGMAutoDownloadToast();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getApplicationContext();
        oadActivity = this;
        setCurrentState(new BaseOADState());
        this.intent = getIntent();
        setWindowParams();
        initViews();
        setmHandler(new MyHandler(this));
        setController(new NavOADController(this));
    }

    private void setWindowParams() {
        getWindow().addFlags(128);
        requestWindowFeature(1);
        setContentView(R.layout.nav_oad_layout);
        TypedValue sca = new TypedValue();
        getResources().getValue(R.dimen.nav_oad_window_size_width, sca, true);
        int width = (int) (((float) ScreenConstant.SCREEN_WIDTH) * sca.getFloat());
        getResources().getValue(R.dimen.nav_oad_window_size_height, sca, true);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, (int) (((float) ScreenConstant.SCREEN_HEIGHT) * sca.getFloat()));
        lp.gravity = 17;
        ((LinearLayout) findViewById(R.id.nav_oad_layout)).setLayoutParams(lp);
        this.mListViewWidth = width / 3;
    }

    private void initViews() {
        this.mCurrentVersion = (TextView) findViewById(R.id.nav_oad_version);
        String currentVersion = "90";
        int oadVersion = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_OAD_OAD_VERSION_NUM);
        Log.d(TAG, "initViews||oadVersion =" + oadVersion);
        if (oadVersion != 0) {
            try {
                currentVersion = Integer.toHexString(oadVersion);
            } catch (Exception e) {
                Log.e(TAG, "toHexString exception");
            }
        }
        TextView textView = this.mCurrentVersion;
        textView.setText(getString(R.string.menu_versioninfo_version) + ":0x" + currentVersion);
        this.mOADSubTitle = (TextView) findViewById(R.id.nav_oad_intro);
        this.mOADSubTitle2 = (TextView) findViewById(R.id.nav_oad_download_wait);
        this.mOADWarnningMsg = (TextView) findViewById(R.id.nav_oad_warning_msg);
        this.mOADWarnningMsg.setVisibility(0);
        this.mProgressStr = (TextView) findViewById(R.id.nav_oad_progress_percent);
        this.mLoadingPoint = (Loading) findViewById(R.id.nav_oad_programming);
        this.mProgress = (ProgressBar) findViewById(R.id.nav_oad_progress_progressbar);
        this.mProgress.setMax(100);
        this.mProgress.setVisibility(4);
        this.mProgressStr.setVisibility(4);
        updateProgress(0);
        this.mBtnList = (CustListView) findViewById(R.id.nav_oad_op_list);
        this.mBtnList.setDivider((Drawable) null);
        this.mBtnList.setVisibility(4);
        this.mBtnList.getLayoutParams().width = this.mListViewWidth;
        this.mTvFootSelect = (TextView) findViewById(R.id.nav_oad_bottom_select);
        this.mTvFootNext = (TextView) findViewById(R.id.nav_oad_bottom_next);
        this.mTvFootExit = (TextView) findViewById(R.id.nav_oad_bottom_exit);
        this.mTvFootExit.setVisibility(0);
    }

    private void hiddenAll() {
        if (getmHandler() != null) {
            getmHandler().removeMessages(0);
            getmHandler().removeMessages(5);
        }
        this.mOADSubTitle.setText("");
        this.mOADSubTitle2.setText("");
        this.mOADSubTitle.setVisibility(4);
        this.mOADSubTitle2.setVisibility(4);
        this.mOADWarnningMsg.setText("");
        this.mOADWarnningMsg.setVisibility(4);
        try {
            this.mLoadingPoint.setVisibility(4);
            this.mLoadingPoint.stopDraw();
        } catch (Exception e) {
        }
        if (this.mProgress != null) {
            this.mProgress.setVisibility(4);
        }
        this.mProgressStr.setText("");
        this.mProgressStr.setVisibility(4);
        this.mBtnList.setVisibility(4);
        this.mTvFootSelect.setVisibility(4);
        this.mTvFootNext.setVisibility(4);
        clearTextViewStr();
    }

    /* access modifiers changed from: private */
    public void showScanning() {
        hiddenAll();
        this.mOADSubTitle.setText(getString(R.string.nav_oad_scanning));
        this.mOADSubTitle.setVisibility(0);
        this.mLoadingPoint.drawLoading();
        this.mLoadingPoint.setVisibility(0);
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_cancel));
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (0 == id) {
                    NavOADActivity.this.stopOAD(false);
                }
            }
        });
    }

    private void showScanningFailResult() {
        hiddenAll();
        this.mOADSubTitle.setVisibility(0);
        this.mOADSubTitle.setText(getString(R.string.nav_oad_scanning_result_fail));
    }

    private void showScanningFailResult(String str) {
        showScanningFailResult();
        this.mOADSubTitle.setText(str);
    }

    /* access modifiers changed from: private */
    public void showDownloadConfirm(boolean auto) {
        hiddenAll();
        MtkLog.d(TAG, "showDownloadConfirm||auto =" + auto);
        if (auto) {
            updateAutoDownloadMsg(this.mCountDownInt);
            getmHandler().sendEmptyMessage(0);
        } else {
            this.mOADSubTitle.setText(getString(R.string.nav_oad_manual_download_confirm));
            getmHandler().sendEmptyMessageDelayed(5, 300000);
        }
        this.mOADSubTitle.setVisibility(0);
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_accept_remind_me_later));
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(NavOADActivity.TAG, "showDownloadConfirm||position =" + position);
                switch (position) {
                    case 0:
                        if (NavOADActivity.this.getCurrentState() == null || NavOADActivity.this.getCurrentState().step != Step.DOWNLOAD_CONFIRM) {
                            NavOADActivity.this.getController().acceptDownload();
                            NavOADActivity.this.setCurrentState(new DownloadingOADState());
                            return;
                        }
                        NavOADActivity.this.getCurrentState().nextPage();
                        return;
                    case 1:
                        NavOADActivity.this.stopOAD(false);
                        return;
                    default:
                        return;
                }
            }
        });
        if (this.mBtnList.getChildAt(0) != null) {
            this.mBtnList.getChildAt(0).requestFocus();
        }
        this.mTvFootSelect.setVisibility(0);
        this.mTvFootNext.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void updateAutoDownloadMsg(int seconds) {
        String str = getString(R.string.nav_oad_auto_download_confirm);
        this.mOADSubTitle.setText(String.format(str, new Object[]{Integer.valueOf(seconds)}));
    }

    /* access modifiers changed from: private */
    public void updateAutoJumpChannelMsg(int lastTime) {
        String str = getString(R.string.nav_oad_schedule_oad_change_channel_confirm);
        this.mOADSubTitle.setText(String.format(str, new Object[]{Integer.valueOf(lastTime)}));
    }

    /* access modifiers changed from: private */
    public void showFlashConfirm() {
        hiddenAll();
        this.mOADSubTitle.setVisibility(0);
        this.mOADSubTitle.setText(getString(R.string.nav_oad_flash_confirm));
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_accept_reject));
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(NavOADActivity.TAG, "showFlashConfirm||position =" + position);
                switch (position) {
                    case 0:
                        if (NavOADActivity.this.getCurrentState() == null || NavOADActivity.this.getCurrentState().step != Step.FLASH_CONFIRM) {
                            NavOADActivity.this.getController().acceptFlash();
                            NavOADActivity.this.setCurrentState(new FlashingState());
                            return;
                        }
                        NavOADActivity.this.getCurrentState().nextPage();
                        return;
                    case 1:
                        NavOADActivity.this.stopOAD(false);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mTvFootSelect.setVisibility(0);
        this.mTvFootNext.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void showProgress() {
        hiddenAll();
        this.mBtnList.setVisibility(8);
        this.mOADSubTitle.setText("");
        this.mLoadingPoint.drawLoading();
        this.mLoadingPoint.setVisibility(0);
        if (getCurrentState().step == Step.DOWNLOADING) {
            this.mOADSubTitle.setText(getString(R.string.nav_oad_downloading));
            this.mOADSubTitle2.setText(getString(R.string.nav_oad_downloading_info));
        } else {
            this.mOADSubTitle.setText(getString(R.string.nav_oad_programming));
        }
        this.mOADSubTitle2.setVisibility(0);
        this.mOADSubTitle.setVisibility(0);
        this.mOADWarnningMsg.setText(getString(R.string.nav_oad_warning_msg_str));
        this.mOADWarnningMsg.setVisibility(0);
        this.mOADWarnningMsg.setTextColor(-1);
        this.mProgress.setVisibility(0);
        this.mProgressStr.setVisibility(0);
        if (this.downLoadRetry) {
            updateProgress(this.currentDownloadProgress);
            this.downLoadRetry = false;
            return;
        }
        updateProgress(0);
    }

    /* access modifiers changed from: private */
    public void showRestartConfirm() {
        hiddenAll();
        this.mOADSubTitle.setText(getString(R.string.nav_oad_restart_confirm));
        this.mOADSubTitle.setVisibility(0);
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_accept_reject));
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        NavOADActivity.this.getController().acceptRestart();
                        NavOADActivity.this.finish();
                        return;
                    case 1:
                        Log.d(NavOADActivity.TAG, "showRestartConfirm||reject");
                        NavOADActivity.this.stopOAD(true);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mTvFootSelect.setVisibility(0);
        this.mTvFootNext.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void showFlashFail() {
        hiddenAll();
        this.mOADSubTitle.setVisibility(0);
        this.mOADSubTitle.setText(getString(R.string.nav_oad_flash_fail));
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_yes_no));
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        NavOADActivity.this.getController().acceptFlash();
                        NavOADActivity.this.setCurrentState(new FlashingState());
                        return;
                    case 1:
                        NavOADActivity.this.stopOAD(false);
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showDownloadFail() {
        hiddenAll();
        this.mOADSubTitle.setVisibility(0);
        this.mOADSubTitle.setText(getString(R.string.nav_oad_manual_download_fail));
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_yes_no));
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        boolean unused = NavOADActivity.this.downLoadRetry = true;
                        NavOADActivity.this.setCurrentState(new DetectOADState());
                        return;
                    case 1:
                        int unused2 = NavOADActivity.this.currentDownloadProgress = 0;
                        NavOADActivity.this.setRemindMeLater(false);
                        NavOADActivity.this.stopOAD(false);
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showScheduleInfo(String scheduleInfo) {
        hiddenAll();
        this.mOADSubTitle.setText(String.format(this.mContext.getString(R.string.nav_oad_schedule_oad_accept_schedule_confirm), new Object[]{scheduleInfo}));
        this.mOADSubTitle.setVisibility(0);
        String[] btns = getResources().getStringArray(R.array.nav_oad_btns_yes_no);
        this.mBtnList.setVisibility(0);
        List<String> mDataList = Arrays.asList(btns);
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(NavOADActivity.TAG, "showScheduleInfo||position =" + position);
                if (-1 != id) {
                    switch (position) {
                        case 0:
                            NavOADActivity.this.getController().acceptScheduleOAD();
                            NavOADActivity.this.stopOAD(false);
                            return;
                        case 1:
                            NavOADActivity.this.stopOAD(false);
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        this.mBtnList.setVisibility(0);
        this.mTvFootSelect.setVisibility(0);
        this.mTvFootNext.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public void showJumpChannelInfo() {
        hiddenAll();
        updateAutoJumpChannelMsg(this.mJumpChannelRemindTime);
        getmHandler().sendEmptyMessage(4);
        this.mOADSubTitle.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_yes_no));
        this.mBtnList.setVisibility(0);
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        NavOADActivity.this.getmHandler().removeMessages(4);
                        NavOADActivity.this.getController().acceptJumpChannel();
                        NavOADActivity.this.stopOAD(false);
                        return;
                    case 1:
                        NavOADActivity.this.stopOAD(false);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBtnList.setVisibility(0);
        this.mTvFootSelect.setVisibility(0);
        this.mTvFootNext.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public void showBGMAutoDownloadToast() {
        hiddenAll();
        this.mOADSubTitle.setText(this.mContext.getString(R.string.nav_oad_atuo_download_with_bgm));
        this.mOADSubTitle.setVisibility(0);
        List<String> mDataList = Arrays.asList(getResources().getStringArray(R.array.nav_oad_btns_yes_no));
        this.mBtnList.setVisibility(0);
        this.mBtnList.initData(mDataList, mDataList.size());
        this.mBtnList.setAdapter(new MyAdapter(this, mDataList, R.layout.setup_data));
        this.mBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        NavOADActivity.this.getController().setOADAutoDownload(true);
                        NavOADActivity.this.stopOAD(false);
                        return;
                    case 1:
                        NavOADActivity.this.stopOAD(false);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBtnList.setVisibility(0);
        this.mTvFootSelect.setVisibility(0);
        this.mTvFootNext.setVisibility(4);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        MtkTvUtil.IRRemoteControl(5);
        getController().registerCallback(this);
        updateOADPorp(true);
        if (this.intent.getAction() == "start_detect_oad") {
            setCurrentState(new DetectOADState());
        } else if (this.intent.getExtras() != null) {
            int messageType = this.intent.getExtras().getInt("updateType");
            this.mScheuleInfo = "";
            MtkLog.d(TAG, "onStart||messageType =" + messageType);
            if (messageType == 3) {
                this.mScheuleInfo = this.intent.getExtras().getString("scheduleInfo");
            } else if (messageType == 11) {
                updateAutoJumpChannelMsg(this.mJumpChannelRemindTime);
            } else if (messageType == 13) {
                this.mOADSubTitle.setText(this.mContext.getString(R.string.nav_oad_atuo_download_with_bgm));
            }
            Message msg = getmHandler().obtainMessage();
            msg.what = 2;
            TvCallbackData data = new TvCallbackData();
            data.param1 = messageType;
            data.param2 = 0;
            data.param3 = -1;
            data.paramStr1 = this.mScheuleInfo;
            data.paramBool1 = false;
            msg.obj = data;
            getmHandler().sendMessageDelayed(msg, 1000);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (!this.mIsStop) {
            setRemindMeLater(false);
            stopOAD(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        updateOADPorp(false);
        getController().unRegisterCallback();
        String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName("main");
        int detail = InputSourceManager.getInstance().changeCurrentInputSourceByName(sourceName);
        MtkLog.d(TAG, "sourceName ==" + sourceName + "  detail ==" + detail);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    public MyHandler getmHandler() {
        return this.mHandler;
    }

    public void setmHandler(MyHandler mHandler2) {
        this.mHandler = mHandler2;
    }

    private void updateProgress(int progress) {
        if (getCurrentState().step == Step.DOWNLOADING || getCurrentState().step == Step.FLASHING) {
            this.mProgress.setProgress(progress);
            this.mProgressStr.setText(String.format("%d%%", new Object[]{Integer.valueOf(progress)}));
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 22 || event.getAction() != 0 || this.mBtnList.getVisibility() != 0 || this.mBtnList.getCount() <= 0) {
            return super.dispatchKeyEvent(event);
        }
        int position = Math.max(this.mBtnList.getSelectedItemPosition(), 0);
        this.mBtnList.performItemClick(this.mBtnList.getChildAt(position), position, (long) this.mBtnList.getChildAt(position).getId());
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDownKeyCode =" + keyCode);
        if (keyCode != 4) {
            switch (keyCode) {
                case 21:
                    return true;
                case 22:
                    return true;
                default:
                    return super.onKeyDown(keyCode, event);
            }
        } else {
            TurnkeyUiMainActivity.setShowBannerInOnResume(false);
            stopOAD(false);
            return true;
        }
    }

    public NavOADController getController() {
        return this.controller;
    }

    public void setController(NavOADController controller2) {
        this.controller = controller2;
    }

    public BaseOADState getCurrentState() {
        return this.currentState;
    }

    public void setDownloadFailState() {
        if (this.mProgress != null) {
            MtkLog.d(TAG, "OAD_INFORM_DOWNLOAD_FAIL,mProgress.getProgress() =" + this.mProgress.getProgress());
            this.currentDownloadProgress = this.mProgress.getProgress();
        }
        setCurrentState(new DownloadFailOADState());
    }

    public void setCurrentState(BaseOADState currentState2) {
        this.currentState = currentState2;
        if (this.mTvFootNext != null) {
            try {
                hiddenAll();
            } catch (Exception e) {
            }
        }
        currentState2.updateUi();
    }

    public void stopOAD(boolean isClearOadVersion) {
        MtkLog.printStackTrace();
        MtkTvUtil.IRRemoteControl(3);
        if (getmHandler() != null) {
            getmHandler().removeMessages(0);
            getmHandler().removeMessages(2);
            getmHandler().removeMessages(3);
            getmHandler().removeMessages(5);
        }
        if (getCurrentState() != null && getCurrentState().step == Step.DOWNLOADING) {
            getController().cancelDownload();
        }
        if (getCurrentState() != null && getCurrentState().step == Step.DETECT) {
            getController().cancelManualDetect();
        }
        if (getController() != null) {
            MtkLog.d(TAG, "stopOAD||IsRemindMeLater = " + isRemindMeLater() + "||isClearOad =" + isClearOadVersion);
            if (isRemindMeLater()) {
                getController().remindMeLater();
            }
            if (isClearOadVersion) {
                getController().clearOadVersion();
            }
        }
        this.mCountDownInt = 15;
        this.mIsStop = true;
        if (DvrManager.getInstance() != null) {
            DvrManager.getInstance().setStopDvrNotResumeLauncher(false);
        }
        finish();
    }

    public void onNotifyOADMessage(int messageType, String scheduleInfo, int progress, boolean autoDld, int argv5) {
        MtkLog.d(TAG, "onNotifyOADMessageType =" + messageType + "||scheduleInfo =" + scheduleInfo);
        getmHandler().removeMessages(3);
        switch (messageType) {
            case 0:
                if (oadActivity != null && getCurrentState().step != Step.DOWNLOAD_CONFIRM) {
                    setCurrentState(new DownloadConfirmOADState());
                    return;
                }
                return;
            case 1:
                showScanningFailResult();
                return;
            case 2:
                showScanningFailResult(getString(R.string.nav_oad_latest_version_already));
                return;
            case 3:
                setCurrentState(new ScheduleInfoState());
                return;
            case 4:
                stopOAD(false);
                return;
            case 5:
            case 8:
                if ((getCurrentState().step == Step.DOWNLOADING || getCurrentState().step == Step.FLASHING) && this.mProgressStr.getVisibility() != 0) {
                    this.mProgressStr.setVisibility(0);
                }
                getmHandler().sendEmptyMessageDelayed(3, 1200000);
                updateProgress(progress);
                return;
            case 6:
                if (this.mProgress != null) {
                    MtkLog.d(TAG, "OAD_INFORM_DOWNLOAD_FAIL,mProgress.getProgress() =" + this.mProgress.getProgress());
                    this.currentDownloadProgress = this.mProgress.getProgress();
                }
                setCurrentState(new DownloadFailOADState());
                return;
            case 9:
                setCurrentState(new FlashFailState());
                return;
            case 10:
                synchronized (this) {
                    this.mPackagePathAndName = scheduleInfo;
                }
                setCurrentState(new RestartConfirmOADState());
                return;
            case 11:
                setCurrentState(new JumpChannelState());
                return;
            case 13:
                setCurrentState(new BGMAutoDownloadState());
                return;
            default:
                return;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<String> mList;

        private MyAdapter(Context context, List<String> data, int resource) {
            this.mInflater = LayoutInflater.from(context);
            this.mList = data;
        }

        public int getCount() {
            return this.mList.size();
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nav_oad_setup_data, (ViewGroup) null);
            }
            ((TextView) convertView.findViewById(R.id.common_tv_setup_dialog_data_index)).setText(this.mList.get(position));
            return convertView;
        }
    }

    public synchronized boolean verifyPackage(RecoverySystem.ProgressListener listener) {
        if (this.mPackagePathAndName == null) {
            return false;
        }
        try {
            RecoverySystem.verifyPackage(new File(this.mPackagePathAndName), listener, new File("/system/etc/security/otacerts.zip"));
            return true;
        } catch (IOException e) {
            return false;
        } catch (GeneralSecurityException e2) {
            Log.e(TAG, "verify Package error " + e2);
            return false;
        }
    }

    public synchronized boolean installPackage() {
        if (this.mPackagePathAndName == null) {
            return false;
        }
        try {
            RecoverySystem.installPackage(this.mContext, new File(this.mPackagePathAndName));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static NavOADActivity getInstance() {
        return oadActivity;
    }

    private void clearTextViewStr() {
        this.mOADSubTitle.setText("");
        this.mOADSubTitle2.setText("");
        this.mOADWarnningMsg.setText("");
        this.mProgressStr.setText("");
        this.mScheuleInfo = "";
    }

    public boolean isRemindMeLater() {
        return this.isRemindMeLater;
    }

    public void setRemindMeLater(boolean isRemindMeLater2) {
        this.isRemindMeLater = isRemindMeLater2;
    }

    private void updateOADPorp(boolean isRunning) {
        SystemProperties.set(Constants.MTK_3RD_APP_FLAG, isRunning ? "1" : "0");
    }
}
