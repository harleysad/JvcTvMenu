package com.mediatek.wwtv.setting.base.scan.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.SaveChannelsConfirmDialog;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.model.ScanParams;
import com.mediatek.wwtv.setting.base.scan.model.ScannerListener;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.Loading;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.io.File;
import java.util.Arrays;

public class ScanDialogActivity extends BaseCustomActivity {
    private static final String TAG = "ScanDialogActivity";
    /* access modifiers changed from: private */
    public int count = 1;
    /* access modifiers changed from: private */
    public boolean isChannelSelected;
    boolean isFocused;
    /* access modifiers changed from: private */
    public boolean isScanning;
    private LinearLayout lineLay;
    boolean lnb = false;
    /* access modifiers changed from: private */
    public Loading loading;
    String mActionID;
    String mActionParentID;
    private TextView mAnalogChannel;
    /* access modifiers changed from: private */
    public boolean mCanCompleteScan;
    /* access modifiers changed from: private */
    public Context mContext;
    private TextView mDVBSChannels;
    private TextView mFinishpercentage;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 122) {
                MtkLog.d(ScanDialogActivity.TAG, "MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL>>>");
                ScanDialogActivity.this.selectChannel();
                boolean unused = ScanDialogActivity.this.isChannelSelected = true;
            } else if (i != 250) {
                switch (i) {
                    case MessageType.MESSAGE_START_SCAN /*125*/:
                        ScanDialogActivity.this.mTV.getScanManager().startATVAutoScan();
                        return;
                    case 126:
                        MtkLog.d(ScanDialogActivity.TAG, "MESSAGE_START_ATV_SCAN");
                        ScanDialogActivity.this.mTV.startScanByScanManager(2, ScanDialogActivity.this.mListener, (ScanParams) null);
                        return;
                    default:
                        return;
                }
            } else {
                boolean isLocked = ScanDialogActivity.this.mTV.isTsLocked();
                ScanDialogActivity.access$408(ScanDialogActivity.this);
                MtkLog.d(ScanDialogActivity.TAG, "isLocked:" + isLocked + " tsLockTimes:" + ScanDialogActivity.this.tsLockTimes + " index:" + ScanDialogActivity.this.mTV.getScanManager().getDVBSCurrentIndex());
                if ((ScanDialogActivity.this.tsLockTimes <= 55 || ScanDialogActivity.this.tsLockTimes >= 65) && !isLocked) {
                    sendEmptyMessageDelayed(250, 1000);
                    return;
                }
                MtkLog.d(ScanDialogActivity.TAG, "position moveing done. start scan.");
                ScanDialogActivity.this.mStateTextView.setText(ScanDialogActivity.this.mContext.getString(R.string.menu_setup_channel_scan));
                removeMessages(250);
                ScanDialogActivity.this.mTV.getScanManager().startDvbsScanAfterTsLock();
            }
        }
    };
    private boolean mIsATVSource;
    /* access modifiers changed from: private */
    public String mItemId;
    /* access modifiers changed from: private */
    public int mLastChannelId;
    /* access modifiers changed from: private */
    public ScannerListener mListener = new ScannerListener() {
        public void onCompleted(int completeValue) {
            switch (completeValue) {
                case 0:
                    ScanDialogActivity.this.mHandler.post(new Runnable() {
                        public void run() {
                            int unused = ScanDialogActivity.this.count = 0;
                            boolean unused2 = ScanDialogActivity.this.onScanning = false;
                            MtkLog.d(ScanDialogActivity.TAG, " ---- scan COMPLETE_ERROR----");
                            ScanDialogActivity.this.showCancelScanInfo();
                            int unused3 = ScanDialogActivity.this.nowProgress = 0;
                            ScanDialogActivity.this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                            ScanDialogActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                        }
                    });
                    return;
                case 1:
                    ScanDialogActivity.this.mHandler.post(new Runnable() {
                        public void run() {
                            int unused = ScanDialogActivity.this.count = 0;
                            boolean unused2 = ScanDialogActivity.this.onScanning = false;
                            int unused3 = ScanDialogActivity.this.nowProgress = 0;
                            MtkLog.d(ScanDialogActivity.TAG, " ---- scan canceled----");
                            ScanDialogActivity.this.showCancelScanInfo();
                            if (ScanDialogActivity.this.mTV.hasOPToDo()) {
                                ScanDialogActivity.this.showTRDUI();
                            }
                            if (!ScanDialogActivity.this.mHandler.hasMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL)) {
                                ScanDialogActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                                ScanDialogActivity.this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
                            }
                        }
                    });
                    return;
                case 2:
                    ScanDialogActivity.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            MtkLog.d(ScanDialogActivity.TAG, " ---- scan COMPLETE_OK---- postDelayed");
                            if (ScanDialogActivity.this.mItemId.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBT) || ScanDialogActivity.this.mItemId.equals(MenuConfigManager.TV_UPDATE_SCAN_DVBT_UPDATE) || ScanDialogActivity.this.mItemId.equals(MenuConfigManager.TV_UPDATE_SCAN)) {
                                MtkLog.d(ScanDialogActivity.TAG, " ---- mTV.isScanTaskFinish()----" + ScanDialogActivity.this.mTV.isScanTaskFinish());
                                TvCallbackData backData = new TvCallbackData();
                                if (ScanDialogActivity.this.mTV.isScanTaskFinish()) {
                                    ScanDialogActivity.this.loading.stopDraw();
                                    backData.param1 = 1;
                                    backData.param2 = 100;
                                    ScanDialogActivity.this.updateCommonScanProgress(backData);
                                    if (ScanDialogActivity.this.mTV.getScanManager().hasOPToDo()) {
                                        MtkLog.d(ScanDialogActivity.TAG, "onCompleted(),hasOPToDo....,show TRDviews");
                                        ScanDialogActivity.this.showTRDUI();
                                    } else {
                                        ScanDialogActivity.this.mTV.uiOpEnd();
                                        ScanDialogActivity.this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                                        ScanDialogActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                                    }
                                    int unused = ScanDialogActivity.this.nowProgress = 0;
                                    ScanDialogActivity.this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
                                } else if (!ScanDialogActivity.this.mStateTextView.getText().equals(ScanDialogActivity.this.mContext.getString(R.string.menu_tv_analog_manual_scan_cancel))) {
                                    int unused2 = ScanDialogActivity.this.nowProgress = 50;
                                    ScanDialogActivity.this.mHandler.postDelayed(new Runnable() {
                                        public void run() {
                                            ScanDialogActivity.this.mTV.startOtherScanTask(ScanDialogActivity.this.mListener);
                                        }
                                    }, 1000);
                                    return;
                                } else {
                                    ScanDialogActivity.this.mListener.onCompleted(1);
                                }
                            } else {
                                MtkLog.d(ScanDialogActivity.TAG, " ---- scan COMPLETE_OK---- mCanCompleteScan>" + ScanDialogActivity.this.mCanCompleteScan);
                                if (ScanDialogActivity.this.mCanCompleteScan || !CommonIntegration.isUSRegion()) {
                                    ScanDialogActivity.this.loading.stopDraw();
                                    TvCallbackData backData2 = new TvCallbackData();
                                    backData2.param1 = 1;
                                    backData2.param2 = 100;
                                    ScanDialogActivity.this.updateCommonScanProgress(backData2);
                                    ScanDialogActivity.this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                                    ScanDialogActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                                } else {
                                    boolean unused3 = ScanDialogActivity.this.mCanCompleteScan = true;
                                }
                            }
                            if (CommonIntegration.isEURegion()) {
                                ScanDialogActivity.this.showTRDUI();
                            }
                            if (ScanDialogActivity.this.lnb) {
                                ScanDialogActivity.this.finish();
                            }
                            if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
                                boolean unused4 = ScanDialogActivity.this.writeAutotestScanFile();
                            }
                        }
                    }, 0);
                    return;
                default:
                    return;
            }
        }

        public void onFrequence(int freq) {
        }

        public void onProgress(int progress, int channels) {
        }

        public void onProgress(final int progress, final int channels, final int type) {
            ScanDialogActivity.this.mHandler.post(new Runnable() {
                public void run() {
                    TvCallbackData backData = new TvCallbackData();
                    backData.param2 = progress;
                    backData.param3 = channels;
                    backData.param4 = type;
                    ScanDialogActivity.this.updateScanProgress(backData);
                }
            });
        }

        public void onDVBSInfoUpdated(int argv4, final String name) {
            ScanDialogActivity.this.mHandler.post(new Runnable() {
                public void run() {
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                    if (name.equals("ChangeSatelliteFrequence")) {
                        ScanDialogActivity.this.beforeDvbsScan();
                    } else if (ScanDialogActivity.this.mNumberChannel != null && !name.equalsIgnoreCase("null")) {
                        ScanDialogActivity.this.mNumberChannel.setText(ScanDialogActivity.this.getSatName(name));
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public boolean mNeedChangeToFirstChannel;
    /* access modifiers changed from: private */
    public TextView mNumberChannel;
    private ProgressBar mScanprogressbar;
    /* access modifiers changed from: private */
    public TextView mStateTextView;
    /* access modifiers changed from: private */
    public TVContent mTV;
    private TextView mTunerModeView;
    /* access modifiers changed from: private */
    public int nowProgress;
    /* access modifiers changed from: private */
    public boolean onScanning;
    public Runnable runnable = new Runnable() {
        public void run() {
            MtkLog.d(ScanDialogActivity.TAG, "runnable>>continue scan");
            if (!ScanDialogActivity.this.mStateTextView.getText().toString().equals(ScanDialogActivity.this.mContext.getString(R.string.menu_setup_channel_scan_cancel)) && !ScanDialogActivity.this.mStateTextView.getText().toString().equals(ScanDialogActivity.this.mContext.getString(R.string.menu_setup_channel_scan_done))) {
                boolean unused = ScanDialogActivity.this.onScanning = true;
                ScanDialogActivity.this.mStateTextView.setText(ScanDialogActivity.this.mContext.getString(R.string.menu_setup_channel_scan));
                ScanDialogActivity.this.loading.drawLoading();
                if (ScanDialogActivity.this.loading.getVisibility() != 0) {
                    ScanDialogActivity.this.loading.setVisibility(0);
                }
                if (ScanDialogActivity.this.mNumberChannel.getVisibility() != 0) {
                    ScanDialogActivity.this.mNumberChannel.setVisibility(0);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int satID = -1;
    private int scanProgressAnalog;
    private int scanProgressDigital;
    ScanThirdlyDialog thirdDialog;
    private Toast toast;
    /* access modifiers changed from: private */
    public int tsLockTimes = 0;

    static /* synthetic */ int access$408(ScanDialogActivity x0) {
        int i = x0.tsLockTimes;
        x0.tsLockTimes = i + 1;
        return i;
    }

    static /* synthetic */ int access$704(ScanDialogActivity x0) {
        int i = x0.count + 1;
        x0.count = i;
        return i;
    }

    /* access modifiers changed from: private */
    public void beforeDvbsScan() {
        SatelliteInfo satelliteInfo = ScanContent.getDVBSEnablesatellites(this.mContext).get(this.mTV.getScanManager().getDVBSCurrentIndex());
        updateSatelliteName();
        if (satelliteInfo.getMotorType() == 5) {
            ScanContent.setDVBSFreqToGetSignalQuality(satelliteInfo.getSatlRecId());
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan_positioner_moving));
            this.tsLockTimes = 0;
            this.mHandler.removeMessages(250);
            this.mHandler.sendEmptyMessage(250);
            return;
        }
        this.mTV.getScanManager().startDvbsScanAfterTsLock();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    private void reMapRegions() {
        MtkLog.d(TAG, "reMapRegions()");
        MtkTvScanDvbtBase.TargetRegion[] regionList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetTargetRegion();
        if (regionList == null || regionList.length <= 0) {
            MtkLog.d(TAG, "debugOP(),TargetRegion==null");
        } else {
            this.mTV.getScanManager().reMapRegions(Arrays.asList(regionList));
        }
    }

    /* access modifiers changed from: private */
    public void showTRDUI() {
        MtkLog.d(TAG, "reMapRegions()");
        if (!this.mItemId.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBT) && this.mTV.getScanManager().getOPType() == 0) {
            MtkLog.e(TAG, "UK only full scan show region list");
            this.mTV.getScanManager().uiOpEnd();
        }
        int oPType = this.mTV.getScanManager().getOPType();
        if (oPType == 4) {
            this.thirdDialog = new ScanThirdlyDialog(this.mContext, 4);
        } else if (oPType != 8) {
            switch (oPType) {
                case 0:
                    reMapRegions();
                    this.thirdDialog = new ScanThirdlyDialog(this.mContext, 2);
                    this.thirdDialog.setCancelable(false);
                    break;
                case 1:
                    this.thirdDialog = new ScanThirdlyDialog(this.mContext, 3);
                    break;
                case 2:
                    this.thirdDialog = new ScanThirdlyDialog(this.mContext, 1);
                    break;
            }
        } else {
            showTRDSaveChannelConfirmDialog();
        }
        if (this.thirdDialog != null) {
            this.thirdDialog.show();
        }
        this.mTV.getScanManager().uiOpEnd();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.mTV = TVContent.getInstance(this.mContext);
        this.mTV.stopTimeShift();
        this.mTV.mHandler = this.mHandler;
        this.mActionID = getIntent().getStringExtra("ActionID");
        this.lnb = getIntent().getBooleanExtra("lnb", false);
        if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
            MenuConfigManager mConfigManager = MenuConfigManager.getInstance(this.mContext);
            mConfigManager.setValue("g_bs__bs_src", getIntent().getIntExtra("tuner_mode", mConfigManager.getDefault("g_bs__bs_src")));
        }
        this.mActionParentID = getIntent().getStringExtra("ActionParentID");
        this.satID = getIntent().getIntExtra("SatID", -1);
        this.mIsATVSource = this.mTV.isCurrentSourceATV();
        MtkLog.d(TAG, "mIsATVSource" + this.mIsATVSource);
        initTvscan(this.mContext, this.mActionID);
        if (!this.mTV.isItaCountry() || this.satID != -1) {
            startScan();
            return;
        }
        ScanThirdlyDialog thirdDialog2 = new ScanThirdlyDialog(this.mContext, 5);
        thirdDialog2.setCancelable(false);
        thirdDialog2.show();
    }

    /* access modifiers changed from: private */
    public void showToast() {
        if (this.toast == null) {
            this.toast = Toast.makeText(this.mContext, R.string.menu_setup_ci_10s_answer_tip, 0);
        }
        this.toast.show();
    }

    private void cancleToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
    }

    public void onPause() {
        cancleScan();
        this.isChannelSelected = true;
        cancleToast();
        this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
        super.onPause();
    }

    private void initTvscan(Context context, String itemId) {
        setContentView(R.layout.scan_dialog_layout);
        this.mItemId = itemId;
        this.mContext = context;
        this.lineLay = (LinearLayout) findViewById(R.id.menu_scan_dialog_id);
        this.mStateTextView = (TextView) findViewById(R.id.state);
        this.mStateTextView.setText(context.getString(R.string.menu_setup_channel_scan));
        this.loading = (Loading) findViewById(R.id.setup_tv_scan_loading);
        this.mScanprogressbar = (ProgressBar) findViewById(R.id.scanprogressbar);
        this.mScanprogressbar.setMax(100);
        this.mScanprogressbar.setProgress(0);
        this.mFinishpercentage = (TextView) findViewById(R.id.finishpercentage);
        this.mFinishpercentage.setText(String.format("%3d%s", new Object[]{0, "%"}));
        this.mDVBSChannels = (TextView) findViewById(R.id.dvbsusedigital_channels);
        this.mNumberChannel = (TextView) findViewById(R.id.numberchannel);
        this.mAnalogChannel = (TextView) findViewById(R.id.analoguechannel);
        this.mTunerModeView = (TextView) findViewById(R.id.trun_mode);
        if (this.lnb) {
            this.mNumberChannel.setVisibility(8);
            this.mAnalogChannel.setVisibility(8);
        }
        if ((MarketRegionInfo.isFunctionSupport(16) || MarketRegionInfo.isFunctionSupport(19)) && this.mTV.isCurrentSourceATV()) {
            this.mTunerModeView.setVisibility(8);
        } else {
            String[] mTnuerArr = this.mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu);
            int tunerMode = this.mTV.getCurrentTunerMode();
            if (tunerMode < 2) {
                if (CommonIntegration.isCNRegion() && this.mIsATVSource) {
                    tunerMode = 1;
                }
                this.mTunerModeView.setText(mTnuerArr[tunerMode]);
            } else if (CommonIntegration.getInstance().isPreferSatMode()) {
                this.mTunerModeView.setText(mTnuerArr[2]);
            } else {
                this.mTunerModeView.setText(mTnuerArr[3]);
            }
        }
        this.nowProgress = 0;
    }

    public void startScan() {
        this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
        this.onScanning = true;
        this.nowProgress = 0;
        this.mFinishpercentage.setText(String.format("%3d%s", new Object[]{0, "%"}));
        this.mScanprogressbar.setProgress(0);
        this.loading.drawLoading();
        if (CommonIntegration.isUSRegion()) {
            if (this.satID == -1) {
                this.mAnalogChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_ana), 0}));
                this.mNumberChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), 0}));
            }
        } else if (CommonIntegration.isCNRegion()) {
            this.mNumberChannel.setText(String.format("%s%3d", new Object[]{getChannelString(), 0}));
            this.mAnalogChannel.setText("");
        } else if (CommonIntegration.isEURegion()) {
            if (MarketRegionInfo.isFunctionSupport(16)) {
                if (this.mTV.isCurrentSourceATV()) {
                    this.mNumberChannel.setText("");
                    this.mAnalogChannel.setText(String.format("%s%3d", new Object[]{getChannelString(), 0}));
                } else if (this.mTV.isCurrentSourceDTV()) {
                    this.mNumberChannel.setText(String.format("%s%3d", new Object[]{getChannelString(), 0}));
                    this.mAnalogChannel.setText("");
                }
            } else if (this.satID == -1) {
                this.mAnalogChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_ana), 0}));
                this.mNumberChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), 0}));
            }
        } else if (CommonIntegration.isSARegion()) {
            this.mAnalogChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_ana), 0}));
            if (this.mTV.getCurrentTunerMode() == 1) {
                this.mNumberChannel.setVisibility(4);
            } else {
                this.mNumberChannel.setVisibility(0);
                TextView textView = this.mNumberChannel;
                textView.setText(this.mContext.getString(R.string.menu_setup_channel_scan_dig) + "0");
            }
        }
        this.mLastChannelId = EditChannel.getInstance(this.mContext).getCurrentChannelId();
        if (this.mItemId.equals(MenuConfigManager.TV_UPDATE_SCAN)) {
            MtkLog.d(TAG, "--- UPDATE_SCAN");
            this.isChannelSelected = false;
            this.mTV.startScanByScanManager(4, this.mListener, (ScanParams) null);
        } else if (this.mItemId.equals(MenuConfigManager.TV_CHANNEL_SCAN) || this.mItemId.equals(MenuConfigManager.FACTORY_TV_FACTORY_SCAN)) {
            if (this.mItemId.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
                this.isChannelSelected = false;
                this.mNeedChangeToFirstChannel = true;
            }
            if (CommonIntegration.isCNRegion()) {
                this.mTV.startScanByScanManager(1, this.mListener, (ScanParams) null);
            } else {
                if (CommonIntegration.isUSRegion()) {
                    this.mCanCompleteScan = false;
                }
                this.mTV.startScanByScanManager(0, this.mListener, new ScanParams());
            }
        } else if (this.mItemId.equals(MenuConfigManager.TV_UPDATE_SCAN_DVBT_UPDATE)) {
            this.isChannelSelected = false;
            if (this.mTV.isUKCountry()) {
                MtkLog.d(TAG, "--- DVBT Update Scan ONLY DTV");
                this.mTV.startScanByScanManager(9, this.mListener, (ScanParams) null);
            } else {
                MtkLog.d(TAG, "---DVBT Update Scan startScan");
                this.mTV.startScanByScanManager(4, this.mListener, (ScanParams) null);
            }
        } else if (this.mItemId.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBT)) {
            this.isChannelSelected = false;
            this.mNeedChangeToFirstChannel = true;
            if (MarketRegionInfo.isFunctionSupport(16)) {
                EditChannel.getInstance(this.mContext).cleanChannelList();
                if (!this.mTV.isCurrentSourceATV() || this.mTV.isChineseTWN()) {
                    MtkLog.d(TAG, "---DTV_SCAN---");
                    this.mTV.startScanByScanManager(1, this.mListener, (ScanParams) null);
                } else {
                    MtkLog.d(TAG, "---ATV_SCAN---");
                    this.mHandler.removeMessages(126);
                    this.mHandler.sendEmptyMessageDelayed(126, 800);
                }
            } else if (this.mTV.isUKCountry()) {
                this.mTV.startScanByScanManager(1, this.mListener, (ScanParams) null);
                MtkLog.d(TAG, "---ONLY SCAN DTV");
            } else {
                MtkLog.d(TAG, "---DVBT Full Scan startScan");
                this.mTV.startScanByScanManager(0, this.mListener, (ScanParams) null);
            }
        } else if (this.mItemId.equals(MenuConfigManager.M7_LNB_Scan)) {
            this.mTV.startScanByScanManager(17, this.mListener, (ScanParams) null);
            MtkLog.d(TAG, "--start M7 LNB Scan");
        } else if (this.mItemId.equals(MenuConfigManager.TV_SYSTEM)) {
            if (this.mActionParentID != null) {
                if (this.mActionParentID.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
                    this.mNeedChangeToFirstChannel = true;
                    this.mTV.startATVAutoOrUpdateScan(2, this.mListener, (ScanParams) null);
                } else if (this.mActionParentID.equals(MenuConfigManager.TV_UPDATE_SCAN)) {
                    this.mTV.startATVAutoOrUpdateScan(5, this.mListener, (ScanParams) null);
                }
            }
        } else if (!this.mItemId.equals(MenuConfigManager.COLOR_SYSTEM)) {
            MtkLog.d(TAG, "--- startScan");
        } else if (this.mActionParentID != null) {
            if (this.mActionParentID.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
                this.mNeedChangeToFirstChannel = true;
                this.mTV.startATVAutoOrUpdateScan(2, this.mListener, (ScanParams) null);
            } else if (this.mActionParentID.equals(MenuConfigManager.TV_UPDATE_SCAN)) {
                this.mTV.startATVAutoOrUpdateScan(5, this.mListener, (ScanParams) null);
            }
        }
        if (this.mTV.getCurrentTunerMode() < 2) {
            return;
        }
        if (this.mItemId.equals(MenuConfigManager.DVBS_SAT_MANUAL_TURNING) || this.mItemId.equals(MenuConfigManager.DVBS_SAT_MANUAL_TURNING_TP)) {
            startDVBSTPScan();
        }
    }

    private void updateSatelliteName() {
        if (this.satID != -1) {
            this.mDVBSChannels.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), 0}));
            TextView textView = this.mAnalogChannel;
            textView.setText(this.mContext.getString(R.string.menu_setup_satellite_name) + this.mTV.getScanManager().getFirstSatName());
            this.mNumberChannel.setText(getSatName(this.mTV.getScanManager().getFirstSatName()));
        }
    }

    private void startDVBSTPScan() {
        MtkLog.d(TAG, "startDVBSTPScan()");
        if (this.satID != -1) {
            this.mDVBSChannels.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), 0}));
            this.mAnalogChannel.setText(this.mContext.getString(R.string.menu_setup_satellite_name));
            this.mNumberChannel.setText(String.format("%s: ", new Object[]{getString(R.string.satellites)}));
            this.mTV.getScanManager().startScan(0, this.mListener, prepareDVBSTPParam(this.satID));
        }
    }

    private ScanParams prepareDVBSTPParam(int satID2) {
        MtkLog.d(TAG, "prepareDVBSTPParam()");
        DVBSSettingsInfo params = new DVBSSettingsInfo();
        params.context = this.mContext;
        params.getSatelliteInfo().setSatlRecId(satID2);
        params.scanMode = 2;
        params.mIsDvbsNeedCleanChannelDB = false;
        return params;
    }

    /* access modifiers changed from: private */
    public void updateScanProgress(TvCallbackData backData) {
        MtkLog.i(TAG, "[Channels ]:   [Progress]: " + backData.param2 + "param3:" + backData.param3);
        if (this.onScanning) {
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        }
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
                this.mNumberChannel.setText(String.format("%s%3d", new Object[]{getChannelString(), Integer.valueOf(backData.param3)}));
                updateCommonScanProgress(backData);
                return;
            case 1:
            case 2:
                if (backData.param4 == 1) {
                    this.mAnalogChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_ana), Integer.valueOf(backData.param3)}));
                } else {
                    this.mNumberChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), Integer.valueOf(backData.param3)}));
                }
                updateCommonScanProgress(backData);
                return;
            case 3:
                updateEUScanProgress(backData);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void updateCommonScanProgress(TvCallbackData backData) {
        int progress = backData.param2;
        if (this.mTV.getActionSize() > 0 || this.nowProgress == 50) {
            progress = this.nowProgress + (progress / 2);
        }
        int progress2 = Math.max(progress, this.mScanprogressbar.getProgress());
        this.mScanprogressbar.setProgress(progress2);
        this.mFinishpercentage.setText(String.format("%3d%s", new Object[]{Integer.valueOf(progress2), "%"}));
        Log.d(TAG, "scanProgress==" + progress2);
        MtkLog.d(TAG, " ---- updateCommonScanProgress----" + progress2);
        if (progress2 >= 100 && backData.param1 == 1) {
            this.onScanning = false;
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan_done));
            this.loading.stopDraw();
            this.loading.setVisibility(4);
        }
    }

    private void updateEUScanProgress(TvCallbackData backData) {
        if (this.satID == -1) {
            if (backData.param4 == 1 || backData.param4 == 0) {
                this.mAnalogChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_ana), Integer.valueOf(backData.param3)}));
            } else if (backData.param4 == 2) {
                this.mNumberChannel.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), Integer.valueOf(backData.param3)}));
            }
        } else if (backData.param4 == 2) {
            this.mDVBSChannels.setText(String.format("%s%3d", new Object[]{this.mContext.getString(R.string.menu_setup_channel_scan_dig), Integer.valueOf(backData.param3)}));
        }
        updateCommonScanProgress(backData);
    }

    private int countWholeProgress(TvCallbackData backData) {
        if (!CommonIntegration.isEURegion()) {
            return backData.param2;
        }
        if (this.scanProgressDigital != 100) {
            this.scanProgressDigital = backData.param2;
        } else {
            this.scanProgressAnalog = backData.param2;
        }
        MtkLog.d(TAG, "countWholeProgress(),itemID:" + this.mItemId);
        if (this.mItemId.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBT) || this.mItemId.equals(MenuConfigManager.TV_UPDATE_SCAN_DVBT_UPDATE)) {
            return (this.scanProgressAnalog / 2) + (this.scanProgressDigital / 2);
        }
        return backData.param2;
    }

    private void reSetProgress() {
        this.scanProgressAnalog = 0;
        this.scanProgressDigital = 0;
    }

    /* access modifiers changed from: private */
    public boolean writeAutotestScanFile() {
        Log.d(TAG, "writeAutotestScanFile()");
        boolean succ = false;
        try {
            File atvfile = new File("/data/vendor/tmp/autotest/scan_atv_with_signal");
            File dtvFile = new File("/data/vendor/tmp/autotest/scan_dtv_with_signal");
            if (TIFChannelManager.getInstance(this.mContext).hasATVChannels()) {
                succ = atvfile.createNewFile();
            }
            if (TIFChannelManager.getInstance(this.mContext).hasDTVChannels()) {
                return dtvFile.createNewFile();
            }
            return succ;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cancleScan() {
        MtkLog.d(TAG, "--- call cancel scan");
        this.mTV.cancelScan();
        this.count = 0;
        this.onScanning = false;
        showCancelScanInfo();
        reSetProgress();
    }

    public void continueScan() {
        if (this.count != 2) {
            this.count = 2;
        }
        reSetProgress();
        startScan();
        MtkLog.d(TAG, " ---- continueScan----");
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        this.mHandler.postDelayed(this.runnable, 1000);
    }

    private String getChannelString() {
        if (!this.mIsATVSource) {
            return this.mContext.getString(R.string.menu_setup_channel_scan_dig);
        }
        return this.mContext.getString(R.string.menu_setup_channel_scan_ana);
    }

    /* access modifiers changed from: private */
    public void showCancelScanInfo() {
        MtkLog.d(TAG, "showCancelScanInfo");
        if (this.mHandler.hasCallbacks(this.runnable)) {
            this.mHandler.removeCallbacks(this.runnable);
        }
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan_cancel));
        this.loading.stopDraw();
        this.loading.setVisibility(4);
    }

    public void showCompleteInfo() {
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan_done));
        this.mScanprogressbar.setProgress(100);
        this.mFinishpercentage.setText(String.format("%3d%s", new Object[]{100, "%"}));
        this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
        this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, 1000);
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                ScanDialogActivity.this.finish();
            }
        }, 1000);
    }

    public boolean showScanStateInfo(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "--- showScanStateInfo");
        if (this.mStateTextView.getText().toString().equals(this.mContext.getString(R.string.menu_setup_channel_scan_done))) {
            this.loading.stopDraw();
            this.loading.setVisibility(4);
            return super.onKeyDown(keyCode, event);
        }
        MtkLog.d(TAG, "--- showScanStateInfo cancelScan");
        cancleScan();
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown" + keyCode);
        if (keyCode == 4) {
            MtkLog.d(TAG, "*****Scan Count =  *******" + this.count);
            if (this.mHandler.hasMessages(MessageType.MESSAGE_START_SCAN)) {
                this.mHandler.removeMessages(MessageType.MESSAGE_START_SCAN);
                this.mTV.getScanManager().rollbackChannelsWhenScanNothingOnUIThread();
                this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                showScanStateInfo(keyCode, event);
                return true;
            } else if (this.count != 0) {
                MtkLog.d(TAG, "*****showScanStateInfo  *******" + this.mItemId + "   mTV.isScanning():" + this.mTV.isScanning());
                if (!this.mHandler.hasMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL)) {
                    this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                }
                showScanStateInfo(keyCode, event);
                return true;
            } else if (!this.isChannelSelected) {
                showToast();
                Log.d(TAG, "toast show");
                return true;
            } else {
                this.loading.stopDraw();
                this.loading.setText("");
                this.isScanning = false;
                return super.onKeyDown(keyCode, event);
            }
        } else if (keyCode != 82) {
            return super.onKeyDown(keyCode, event);
        } else {
            return true;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 24 || keyCode == 25) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* access modifiers changed from: private */
    public void selectChannel() {
        MtkLog.d(TAG, "selectChannel>>>>>" + this.mLastChannelId + ">>>" + this.mNeedChangeToFirstChannel + ">>" + TIFChannelManager.getInstance(this.mContext).isChannelsExist());
        new Thread() {
            public void run() {
                if (ScanDialogActivity.this.mTV.isScanning()) {
                    return;
                }
                if (ScanDialogActivity.this.satID == -1) {
                    if (ScanDialogActivity.this.mNeedChangeToFirstChannel) {
                        TIFChannelInfo info = TIFChannelManager.getInstance(ScanDialogActivity.this.mContext).getFirstChannelForScan();
                        MtkLog.d(ScanDialogActivity.TAG, "selectChannel>>>>>" + info);
                        if (info != null) {
                            TIFChannelManager.getInstance(ScanDialogActivity.this.mContext).selectChannelByTIFInfo(info);
                            SaveValue instance = SaveValue.getInstance(ScanDialogActivity.this.mContext);
                            instance.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
                        } else {
                            MtkLog.d(ScanDialogActivity.TAG, "selectChannel>>>>> null");
                            EditChannel.getInstance(ScanDialogActivity.this.mContext).selectChannel(ScanDialogActivity.this.mLastChannelId);
                        }
                    } else {
                        EditChannel.getInstance(ScanDialogActivity.this.mContext).selectChannel(ScanDialogActivity.this.mLastChannelId);
                    }
                    MtkLog.d(ScanDialogActivity.TAG, "select broadcast chlist type");
                    SaveValue instance2 = SaveValue.getInstance(ScanDialogActivity.this.mContext);
                    instance2.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
                } else if (ScanDialogActivity.this.mTV.getScanManager().hasChannels()) {
                    int frequency = ScanContent.getDVBSTransponder(ScanDialogActivity.this.satID).i4Frequency;
                    MtkLog.d(ScanDialogActivity.TAG, "TP selectChannel frequency=" + frequency);
                    TVContent.getInstance(ScanDialogActivity.this.mContext).changeChannelByQueryFreq(frequency);
                }
            }
        }.start();
    }

    public CharSequence getSatName(String satName) {
        return String.format("%s: %4s", new Object[]{getString(R.string.satellites), String.format("%d/%d", new Object[]{Integer.valueOf(this.mTV.getScanManager().getDVBSCurrentIndex() + 1), Integer.valueOf(this.mTV.getScanManager().getDVBSTotalSatSize())})});
    }

    private DialogInterface.OnKeyListener TvScanOnKeyListener() {
        return new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode == 82) {
                    ScanDialogActivity.this.cancleScan();
                    ScanDialogActivity.this.loading.stopDraw();
                    ScanDialogActivity.this.finish();
                    return true;
                } else if (action != 0) {
                    return false;
                } else {
                    if (keyCode == 4) {
                        return onBackPressOnTvScan();
                    }
                    if (keyCode != 23) {
                        return false;
                    }
                    return onEnterPressOnTvScan();
                }
            }

            private boolean onEnterPressOnTvScan() {
                if (ScanDialogActivity.this.onScanning) {
                    return false;
                }
                boolean unused = ScanDialogActivity.this.onScanning = true;
                if (ScanDialogActivity.this.mStateTextView.getText().toString().equals(ScanDialogActivity.this.getString(R.string.menu_setup_channel_scan_done))) {
                    ScanDialogActivity.this.continueScan();
                    return true;
                } else if (ScanDialogActivity.this.count == 1) {
                    return true;
                } else {
                    ScanDialogActivity.access$704(ScanDialogActivity.this);
                    ScanDialogActivity.this.mStateTextView.setText(ScanDialogActivity.this.mContext.getString(R.string.menu_tv_scann_allchannels));
                    ScanDialogActivity.this.continueScan();
                    return true;
                }
            }

            private boolean onBackPressOnTvScan() {
                MtkLog.d(ScanDialogActivity.TAG, "*****Scan Count =  *******" + ScanDialogActivity.this.count);
                if (ScanDialogActivity.this.count != 0) {
                    MtkLog.d(ScanDialogActivity.TAG, "*****showScanStateInfo  *******");
                    return true;
                } else if (!ScanDialogActivity.this.isChannelSelected) {
                    ScanDialogActivity.this.showToast();
                    Log.d(ScanDialogActivity.TAG, "toast show");
                    return true;
                } else {
                    ScanDialogActivity.this.loading.stopDraw();
                    ScanDialogActivity.this.loading.setText("");
                    ScanDialogActivity.this.finish();
                    boolean unused = ScanDialogActivity.this.isScanning = false;
                    return true;
                }
            }
        };
    }

    public void showTRDSaveChannelConfirmDialog() {
        MtkLog.d(TAG, "showTRDSaveChannelConfirmDialog()");
        new SaveChannelsConfirmDialog(this.mContext).showConfirmDialog();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mListener = null;
        super.onDestroy();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        MtkLog.d(TAG, "*****onWindowFocusChanged  *******" + hasFocus);
        this.isFocused = hasFocus;
    }
}
