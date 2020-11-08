package com.mediatek.wwtv.setting.base.scan.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.EditTextActivity;
import com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter;
import com.mediatek.wwtv.setting.base.scan.model.CableOperator;
import com.mediatek.wwtv.setting.base.scan.model.DVBCCNScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBCScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo;
import com.mediatek.wwtv.setting.base.scan.model.DVBTCNScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBTScanner;
import com.mediatek.wwtv.setting.base.scan.model.EUATVScanner;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.model.ScanParams;
import com.mediatek.wwtv.setting.base.scan.model.ScannerListener;
import com.mediatek.wwtv.setting.base.scan.model.TKGSContinueScanConfirmDialog;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.SatDetailUI;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.Loading;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.scan.TKGSUserMessageDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScanViewActivity extends BaseCustomActivity {
    private static final String FAV_RANGE_RF_CHANNEL = "fav_range_rf_channel";
    private static final String FAV_US_SINGLE_RF_CHANNEL = "fav_us_single_rf_channel";
    private static final int REQ_EDITTEXT = 35;
    private static final String TAG = "ScanViewActivity";
    public static boolean isSelectedChannel = true;
    private final int SCANL_LIST_SElECTED_FOR_SETTING_TTS = EPGConfig.EPG_SHOW_LOCK_ICON;
    private String[] allRFChannels;
    /* access modifiers changed from: private */
    public String cableOperator;
    /* access modifiers changed from: private */
    public LiveTVDialog dialog;
    private ScanParams dvbsParams;
    /* access modifiers changed from: private */
    public EditChannel editChannel;
    /* access modifiers changed from: private */
    public int freFrom;
    /* access modifiers changed from: private */
    public int freTo;
    int frequencyValue;
    private boolean isAutoScan = false;
    /* access modifiers changed from: private */
    public boolean isScanning = false;
    public Loading loading;
    /* access modifiers changed from: private */
    public int mATVFreq;
    private View.AccessibilityDelegate mAccDelegateForChList = new View.AccessibilityDelegate() {
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            MtkLog.d(ScanViewActivity.TAG, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
            if (ScanViewActivity.this.mListView != host) {
                MtkLog.d(ScanViewActivity.TAG, "host:" + ScanViewActivity.this.mListView + "," + host);
            } else {
                MtkLog.d(ScanViewActivity.TAG, ":host =false");
                List<CharSequence> texts = event.getText();
                if (texts == null) {
                    MtkLog.d(ScanViewActivity.TAG, "texts :" + texts);
                } else {
                    for (int i = 0; i < texts.size(); i++) {
                        System.out.println("" + i + "===" + texts.get(i));
                    }
                    MtkLog.d(ScanViewActivity.TAG, "enventtype :" + event.getEventType());
                    if (event.getEventType() == 32768) {
                        int index = findSelectItem(texts.get(texts.size() - 1).toString());
                        MtkLog.d(ScanViewActivity.TAG, ":index =" + index);
                        if (index >= 0) {
                            ScanViewActivity.this.mSelCelHandler.removeMessages(EPGConfig.EPG_SHOW_LOCK_ICON);
                            Message msg = Message.obtain();
                            msg.what = EPGConfig.EPG_SHOW_LOCK_ICON;
                            msg.arg1 = index;
                            ScanViewActivity.this.mSelCelHandler.sendMessageDelayed(msg, 400);
                        }
                    }
                }
            }
            try {
                return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
            } catch (Exception e) {
                Log.d(ScanViewActivity.TAG, "Exception " + e);
                return true;
            }
        }

        private int findSelectItem(String text) {
            MtkLog.d(ScanViewActivity.TAG, "texts =" + text);
            List<ScanFactorAdapter.ScanFactorItem> scanList = ScanViewActivity.this.mAdapter.getList();
            if (scanList == null) {
                return -1;
            }
            for (int i = 0; i < scanList.size(); i++) {
                MtkLog.d(ScanViewActivity.TAG, ":index =" + scanList.get(i).title + " text = " + text);
                if (scanList.get(i).title.equals(text)) {
                    return i;
                }
            }
            return -1;
        }
    };
    /* access modifiers changed from: private */
    public String mActionID = "";
    /* access modifiers changed from: private */
    public ScanFactorAdapter mAdapter;
    /* access modifiers changed from: private */
    public int mAddedChannelCount = -1;
    /* access modifiers changed from: private */
    public TextView mAnaloguechannel;
    /* access modifiers changed from: private */
    public boolean mCanCompleteScan;
    /* access modifiers changed from: private */
    public MenuConfigManager mConfigManager;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mCurrFactorItemId;
    /* access modifiers changed from: private */
    public int mCurrntRFChannel;
    /* access modifiers changed from: private */
    public TextView mDVBSChannels;
    private ScanFactorAdapter.ScanFactorItem mFactorItem;
    public TextView mFinishpercentage;
    private TextView mFromChannel;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            ScanFactorAdapter.ScanFactorItem itemQual;
            int minValue;
            ScanFactorAdapter.ScanFactorItem itemQual2;
            ScanFactorAdapter.ScanFactorItem itemLevel;
            super.handleMessage(msg);
            if (msg.what == 1879048197) {
                TvCallbackData backData = (TvCallbackData) msg.obj;
                int i = backData.param1;
                if (i == 4) {
                    ScanViewActivity.isSelectedChannel = true;
                    boolean unused = ScanViewActivity.this.isScanning = false;
                    ScanViewActivity.this.loading.stopDraw();
                    ScanViewActivity.this.loading.setVisibility(4);
                    ScanViewActivity.this.mTv.removeCallBackListener(ScanViewActivity.this.mHandler);
                    ScanViewActivity.this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                    ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                    MtkLog.i(ScanViewActivity.TAG, "[Channels]: MSG_SCAN_CANCEL");
                } else if (i != 8) {
                    switch (i) {
                        case 1:
                            boolean unused2 = ScanViewActivity.this.isScanning = false;
                            MtkLog.i(ScanViewActivity.TAG, "[MSG_SCAN_COMPLETE]:   [Progress]: " + backData.param2 + "backData.param3:" + backData.param3 + "backData.param4:" + backData.param4 + "scan mode:>" + ScanViewActivity.this.mCanCompleteScan);
                            if (ScanViewActivity.this.mCanCompleteScan || !CommonIntegration.isUSRegion()) {
                                ScanViewActivity.this.setScanProgress(100);
                                ScanViewActivity.this.loading.stopDraw();
                                ScanViewActivity.this.loading.setVisibility(4);
                                ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_done));
                                ScanViewActivity.this.mTv.removeCallBackListener(ScanViewActivity.this.mHandler);
                                ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(103, MessageType.delayMillis2);
                                MtkLog.i(ScanViewActivity.TAG, "[mScanType]: " + ScanViewActivity.this.mScanType);
                                if (ScanViewActivity.this.mScanType.equals(ScanViewActivity.FAV_US_SINGLE_RF_CHANNEL)) {
                                    ScanViewActivity.this.mHandler.postDelayed(new Runnable() {
                                        public void run() {
                                            String inputSourceName;
                                            int channelId = ScanViewActivity.this.mTv.getChannelIDByRFIndex(ScanViewActivity.this.freFrom);
                                            MtkLog.i(ScanViewActivity.TAG, "[MSG_SCAN_PROGRESS mLastChannelID]: " + channelId + ">>>>" + ScanViewActivity.this.mLastChannelID);
                                            if (channelId <= 0) {
                                                channelId = ScanViewActivity.this.mLastChannelID;
                                            } else {
                                                TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(ScanViewActivity.this.mContext).getTIFChannelInfoById(channelId);
                                                if (!(tifChannelInfo == null || tifChannelInfo.mMtkTvChannelInfo == null || tifChannelInfo.mMtkTvChannelInfo.getBrdcstType() == 1)) {
                                                    if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
                                                        if (CommonIntegration.isEURegion() || CommonIntegration.isCNRegion()) {
                                                            inputSourceName = "DTV";
                                                        } else {
                                                            inputSourceName = "TV";
                                                        }
                                                        InputSourceManager.getInstance().saveOutputSourceName(inputSourceName, CommonIntegration.getInstance().getCurrentFocus());
                                                    }
                                                    TIFChannelManager.getInstance(ScanViewActivity.this.mContext).selectChannelByTIFInfo(tifChannelInfo);
                                                }
                                            }
                                            MtkLog.i(ScanViewActivity.TAG, "[getFreFrom]: " + ScanViewActivity.this.freFrom + "channelId:" + channelId);
                                            if (!ScanViewActivity.this.isFinishing()) {
                                                ScanViewActivity.this.sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 0);
                                            }
                                        }
                                    }, 1000);
                                    if (backData.param3 == 0) {
                                        ScanViewActivity.isSelectedChannel = true;
                                        return;
                                    }
                                    return;
                                }
                                ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(103, MessageType.delayMillis2);
                                ScanViewActivity.this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                                ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                                return;
                            }
                            boolean unused3 = ScanViewActivity.this.mCanCompleteScan = true;
                            return;
                        case 2:
                            MtkLog.i(ScanViewActivity.TAG, "[MSG_SCAN_PROGRESS]:   [Progress]: " + backData.param2 + "backData.param3:" + backData.param3 + "backData.param4:" + backData.param4 + "scan mode:" + ScanViewActivity.this.mAddedChannelCount);
                            if (!ScanViewActivity.this.mStateTextView.getText().equals(ScanViewActivity.this.mContext.getString(R.string.menu_tv_analog_manual_scan_cancel))) {
                                ScanViewActivity.this.mStateTextView.setVisibility(0);
                                ScanViewActivity.this.loading.setVisibility(0);
                                if (!ScanViewActivity.this.loading.isLoading()) {
                                    ScanViewActivity.this.loading.drawLoading();
                                }
                                ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan));
                                if (CommonIntegration.isUSRegion() || CommonIntegration.isSARegion() || CommonIntegration.isEURegion()) {
                                    if (backData.param4 == 1) {
                                        TextView access$1100 = ScanViewActivity.this.mAnaloguechannel;
                                        access$1100.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_ana) + backData.param3);
                                    } else {
                                        TextView access$1200 = ScanViewActivity.this.mNumberChannel;
                                        access$1200.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_dig) + backData.param3);
                                    }
                                }
                                ScanViewActivity.access$1008(ScanViewActivity.this);
                                if (!CommonIntegration.isEURegion()) {
                                    TextView access$1400 = ScanViewActivity.this.mNumberFromTo;
                                    access$1400.setText((ScanViewActivity.this.freFrom + ScanViewActivity.this.mAddedChannelCount) + "/" + ScanViewActivity.this.freTo);
                                }
                                ScanViewActivity.this.setScanProgress(backData.param2);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else {
                    ScanViewActivity.isSelectedChannel = true;
                    boolean unused4 = ScanViewActivity.this.isScanning = false;
                    ScanViewActivity.this.mTv.removeCallBackListener(ScanViewActivity.this.mHandler);
                    ScanViewActivity.this.loading.stopDraw();
                    ScanViewActivity.this.loading.setVisibility(4);
                    ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_error));
                    ScanViewActivity.this.mHandler.removeMessages(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL);
                    ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.delayMillis5);
                    MtkLog.i(ScanViewActivity.TAG, "[Channels]: MSG_SCAN_ABORT");
                }
            } else if (msg.what == 122) {
                MtkLog.d(ScanViewActivity.TAG, "MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL>>>");
                ScanViewActivity.this.selectChannel();
                ScanViewActivity.isSelectedChannel = true;
                MenuDataHelper.getInstance(ScanViewActivity.this.mContext).changeEnable();
            } else if (msg.what == 125) {
                ScanViewActivity.this.mTv.getScanManager().startATVAutoScan();
            } else if (msg.what == 310) {
                new Thread(new Runnable() {
                    public void run() {
                        ScanFactorAdapter.ScanFactorItem itemLevel = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                        MtkLog.d(ScanViewActivity.TAG, "mTV.isScanning()1>>" + ScanViewActivity.this.mTv.isScanning() + ">>>" + DVBTScanner.selectedRFChannelFreq);
                        if (!ScanViewActivity.this.mTv.isScanning() && !ScanViewActivity.this.mTv.changeChannelByQueryFreq(DVBTScanner.selectedRFChannelFreq) && itemLevel.id.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
                            ScanViewActivity.this.editChannel.tuneDVBTRFSignal();
                        }
                        ScanViewActivity.this.sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 0);
                    }
                }).start();
            } else if (msg.what == 315) {
                MtkLog.d(ScanViewActivity.TAG, "handle MENU_CN_TV_RF_SCAN_CONNECTTURN");
                new Thread(new Runnable() {
                    public void run() {
                        ScanFactorAdapter.ScanFactorItem itemLevel = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                        MtkLog.d(ScanViewActivity.TAG, "mTV.isScanning()1>>" + ScanViewActivity.this.mTv.isScanning() + ">>>" + DVBTCNScanner.selectedRFChannelFreq);
                        if (!ScanViewActivity.this.mTv.isScanning() && !ScanViewActivity.this.mTv.changeChannelByQueryFreq(DVBTCNScanner.selectedRFChannelFreq) && itemLevel.id.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL)) {
                            ScanViewActivity.this.editChannel.tuneDVBTRFSignal();
                        }
                        ScanViewActivity.this.sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 0);
                    }
                }).start();
            } else if (msg.what == 316) {
                new Thread(new Runnable() {
                    public void run() {
                        MtkLog.d(ScanViewActivity.TAG, "mTv.isScanning()1>>" + ScanViewActivity.this.mTv.isScanning() + ",now RF Index:" + ScanViewActivity.this.mCurrntRFChannel);
                        if (!ScanViewActivity.this.mTv.isScanning() && !ScanViewActivity.this.mTv.changeChannelByQueryFreq(ScanViewActivity.this.mCurrntRFChannel)) {
                            ScanViewActivity.this.editChannel.tuneUSSAFacRFSignalLevel(ScanViewActivity.this.mCurrntRFChannel);
                        }
                        ScanViewActivity.this.sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 0);
                    }
                }).start();
            } else if (msg.what == 301) {
                int signalLevel = msg.arg1;
                int signalQuality = msg.arg2;
                MtkLog.d("fff", "handled--MENU_TV_RF_SCAN_REFRESH---signalLevel--->" + signalLevel + "   signalQuality:" + signalQuality);
                if (CommonIntegration.isSARegion() || (CommonIntegration.isCNRegion() && ScanViewActivity.this.mActionID != null && ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CN) && ScanViewActivity.this.mTunerMode == 0)) {
                    ScanFactorAdapter.ScanFactorItem itemLevel2 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(1).getTag(R.id.factor_name);
                    ScanViewActivity.this.mListView.getChildAt(2).getTag(R.id.factor_name);
                    if (itemLevel2 != null) {
                        itemLevel2.progress = signalLevel;
                    }
                    if (CommonIntegration.isSARegion() && (itemQual = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(2).getTag(R.id.factor_name)) != null) {
                        itemQual.optionValue = signalQuality;
                    }
                } else if (CommonIntegration.isUSRegion() && !ScanViewActivity.this.mTv.isScanning()) {
                    ScanFactorAdapter.ScanFactorItem itemLevel3 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(3).getTag(R.id.factor_name);
                    if (itemLevel3 != null) {
                        itemLevel3.progress = signalLevel;
                    }
                } else if (CommonIntegration.isEURegion() && !ScanViewActivity.this.mTv.isScanning() && ((minValue = ScanViewActivity.this.mConfigManager.getDefault("g_bs__bs_src")) == 1 || minValue == 0)) {
                    if (minValue == 0) {
                        itemLevel = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(1).getTag(R.id.factor_name);
                        itemQual2 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(2).getTag(R.id.factor_name);
                    } else {
                        itemLevel = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(2).getTag(R.id.factor_name);
                        itemQual2 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(3).getTag(R.id.factor_name);
                    }
                    if (itemLevel != null) {
                        itemLevel.progress = signalLevel;
                    }
                    if (itemQual2 != null) {
                        itemQual2.progress = signalQuality;
                    }
                }
                if (!ScanViewActivity.this.isFinishing()) {
                    ScanViewActivity.this.mAdapter.notifyDataSetChanged();
                    ScanViewActivity.this.sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 1000);
                }
            } else if (msg.what == 250) {
                boolean isLocked = ScanViewActivity.this.mTv.isTsLocked();
                ScanViewActivity.access$2308(ScanViewActivity.this);
                MtkLog.d(ScanViewActivity.TAG, "isLocked:" + isLocked + " tsLockTimes:" + ScanViewActivity.this.tsLockTimes + " index:" + ScanViewActivity.this.mTv.getScanManager().getDVBSCurrentIndex());
                if ((ScanViewActivity.this.tsLockTimes <= 55 || ScanViewActivity.this.tsLockTimes >= 65) && !isLocked) {
                    sendEmptyMessageDelayed(250, 1000);
                    return;
                }
                MtkLog.d(ScanViewActivity.TAG, "position moveing done. start scan.");
                ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan));
                removeMessages(250);
                ScanViewActivity.this.mTv.getScanManager().startDvbsScanAfterTsLock();
            }
        }
    };
    private String mItemID = MenuConfigManager.FACTORY_TV_RANGE_SCAN_DIG;
    /* access modifiers changed from: private */
    public int mLastChannelID;
    /* access modifiers changed from: private */
    public ListView mListView;
    /* access modifiers changed from: private */
    public String mLocationID;
    boolean mNeedChangeStartFrq = false;
    /* access modifiers changed from: private */
    public TextView mNumberChannel;
    /* access modifiers changed from: private */
    public TextView mNumberFromTo;
    public int mPercentage;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && ScanViewActivity.this.mActionID != null && CommonIntegration.isEURegion()) {
                if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_ANALOG_SCAN) || ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN) || ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN)) {
                    ScanViewActivity.this.finish();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mSatID;
    /* access modifiers changed from: private */
    public ScannerListener mScanListener = new ScannerListener() {
        String factorId;

        public void onCompleted(final int completeValue) {
            MtkLog.d(ScanViewActivity.TAG, "MenuMain mExitLevel onCompleted:" + completeValue + "mTv.isScanTaskFinish():" + ScanViewActivity.this.mTv.isScanTaskFinish());
            final ScanFactorAdapter.ScanFactorItem item = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
            this.factorId = item.id;
            if (this.factorId == null) {
                this.factorId = "";
            }
            ScanViewActivity.this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    ScanViewActivity.this.loading.stopDraw();
                    if (completeValue == 2) {
                        if (ScanViewActivity.this.mTv.isScanTaskFinish()) {
                            boolean unused = ScanViewActivity.this.isScanning = false;
                            if (ScanContent.isSatOpHDAustria()) {
                                ArrayList<TIFChannelInfo> tifChannelList = TIFChannelManager.getInstance(ScanViewActivity.this.mContext).queryRegionChannelForHDAustria();
                                if (tifChannelList.size() > 0) {
                                    Intent intent = new Intent(ScanViewActivity.this, RegionalisationAusActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("regions", tifChannelList);
                                    intent.putExtra("regions", bundle);
                                    ScanViewActivity.this.startActivity(intent);
                                }
                            } else if (ScanContent.isSatOpDiveo()) {
                                Map<String, List<TIFChannelInfo>> queryRegionChannelForDiveo = TIFChannelManager.getInstance(ScanViewActivity.this.mContext).queryRegionChannelForDiveo();
                                if (queryRegionChannelForDiveo.size() > 0) {
                                    Intent intent2 = new Intent(ScanViewActivity.this, RegionalisationAusActivity.class);
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putSerializable("regions", (Serializable) queryRegionChannelForDiveo);
                                    intent2.putExtra("regions", bundle2);
                                    ScanViewActivity.this.startActivity(intent2);
                                }
                            }
                            ScanViewActivity.this.showTricolorChannelListDialog();
                            ScanViewActivity.this.loading.setVisibility(4);
                            if (ScanViewActivity.this.mTv.hasOPToDo()) {
                                MtkLog.d(ScanViewActivity.TAG, "onCompleted(),hasOPToDo....,show TRDviews");
                                ScanViewActivity.this.showDVBSNFYInfoView();
                            } else {
                                ScanViewActivity.this.mTv.uiOpEnd();
                            }
                            if (ScanContent.isPreferedSat() && ScanViewActivity.this.mTv.isTurkeyCountry() && ScanViewActivity.this.mTv.isTKGSOperator()) {
                                int mask = ScanViewActivity.this.mTv.getScanManager().getTkgsNfyInfoMask();
                                String userMsg = ScanViewActivity.this.mTv.getScanManager().getDVBSTkgsUserMessage();
                                MtkLog.d(ScanViewActivity.TAG, "sanling scan userMsg 33==" + userMsg + ",mask==" + mask);
                                if (!TextUtils.isEmpty(userMsg)) {
                                    ScanViewActivity.this.mTv.getScanManager().setTkgsNfyInfoMask(0);
                                    ScanViewActivity.this.showTKGSUserMessageDialog(userMsg);
                                }
                            }
                            int unused2 = ScanViewActivity.this.nowProgress = 0;
                            if ((!CommonIntegration.isEURegion() || AnonymousClass3.this.factorId == null || (!AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.SYM_RATE))) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                                if (AnonymousClass3.this.factorId != null && (AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN) || AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG))) {
                                    ScanViewActivity.this.selectChannel();
                                } else if (!CommonIntegration.isCNRegion() || AnonymousClass3.this.factorId == null || (!AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_START_FREQUENCY) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_END_FREQUENCY))) {
                                    if (CommonIntegration.isCNRegion() && AnonymousClass3.this.factorId != null && (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL) || AnonymousClass3.this.factorId.equals(MenuConfigManager.SYM_RATE) || AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN))) {
                                        MtkLog.d(ScanViewActivity.TAG, "TV_SINGLE_SCAN_RF_CHANNEL>>");
                                        if ((AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || AnonymousClass3.this.factorId.equals(MenuConfigManager.SYM_RATE) || AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL)) && ScanViewActivity.this.mTv.getScanManager().isSingleRFScan() && !ScanViewActivity.this.selectScanedRFChannel() && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL)) {
                                            ScanViewActivity.this.selectChannel(true);
                                        }
                                    } else if (AnonymousClass3.this.factorId == null || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_ANANLOG_SCAN_UP) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_ANANLOG_SCAN_DOWN) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_START_FREQUENCY) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN)) {
                                        float freq = (float) (((double) ScanViewActivity.this.mATVFreq) / 1000000.0d);
                                        MtkLog.d(ScanViewActivity.TAG, "ScanUP/ScanDown Finshed. please set frequency!" + ScanViewActivity.this.mATVFreq + ">>" + freq);
                                        if (item.factorType != 4) {
                                            MtkLog.d(ScanViewActivity.TAG, "ScanUP/ScanDown Finshed. Not NumView");
                                        } else if (freq == 0.0f) {
                                            MtkLog.d(ScanViewActivity.TAG, "ScanUP/ScanDown freq == 0");
                                            ScanViewActivity.this.mNumberFromTo.setText(ScanViewActivity.this.mContext.getString(R.string.menu_tv_freq_scan_no_channel));
                                        } else if (ScanViewActivity.this.mTv.changeChannelByFreq(ScanViewActivity.this.mATVFreq)) {
                                            float freq2 = Math.max(freq, 44.0f);
                                            ((EditText) ScanViewActivity.this.mListView.getChildAt(0).findViewById(R.id.factor_input)).setText(String.format("%.2f", new Object[]{Float.valueOf(freq2)}));
                                            ((ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(0).getTag(R.id.factor_name)).inputValue = (int) freq2;
                                            ScanViewActivity.this.mNeedChangeStartFrq = true;
                                            ScanViewActivity.this.setChlistBroadcast();
                                            MtkLog.d(ScanViewActivity.TAG, "ScanUP/ScanDown Finshed. freq is==" + freq2);
                                        } else {
                                            ScanViewActivity.this.mNumberFromTo.setText(ScanViewActivity.this.mContext.getString(R.string.menu_tv_freq_scan_no_channel));
                                        }
                                    } else {
                                        ScanViewActivity.this.selectChannel(true);
                                    }
                                }
                            } else if (ScanViewActivity.this.mTv.getScanManager().isSingleRFScan()) {
                                boolean unused3 = ScanViewActivity.this.selectScanedRFChannel();
                            } else if (AnonymousClass3.this.factorId != null && AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                                ScanViewActivity.this.selectChannel();
                            }
                            ScanViewActivity.this.setScanProgress(100);
                            ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_done));
                            MenuDataHelper.getInstance(ScanViewActivity.this.mContext).changeEnable();
                            ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(103, MessageType.delayMillis2);
                        } else if (!ScanViewActivity.this.mStateTextView.getText().equals(ScanViewActivity.this.mContext.getString(R.string.menu_tv_analog_manual_scan_cancel))) {
                            MtkLog.d(ScanViewActivity.TAG, "onCompleted(),task not finished,start another task to scan");
                            ScanViewActivity.this.loading.setVisibility(0);
                            ScanViewActivity.this.loading.drawLoading();
                            boolean unused4 = ScanViewActivity.this.isScanning = true;
                            if (ScanViewActivity.this.svl > 2) {
                                ScanViewActivity.this.mTv.startDVBSScanTask(ScanViewActivity.this.mScanListener);
                                TextView access$1100 = ScanViewActivity.this.mAnaloguechannel;
                                access$1100.setText(ScanViewActivity.this.getString(R.string.menu_setup_satellite_name) + ScanViewActivity.this.mTv.getScanManager().getFirstSatName());
                                ScanViewActivity.this.mNumberChannel.setText(ScanViewActivity.this.getSatName(ScanViewActivity.this.mTv.getScanManager().getFirstSatName()));
                                return;
                            }
                            int unused5 = ScanViewActivity.this.nowProgress = 50;
                            ScanViewActivity.this.mTv.startOtherScanTask(ScanViewActivity.this.mScanListener);
                        } else {
                            ScanViewActivity.this.mScanListener.onCompleted(1);
                        }
                    } else if (completeValue == 0) {
                        boolean unused6 = ScanViewActivity.this.isScanning = false;
                        if ((!CommonIntegration.isEURegion() || AnonymousClass3.this.factorId == null || (!AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.SYM_RATE))) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                            if (!CommonIntegration.isCNRegion() || AnonymousClass3.this.factorId == null || (!AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_START_FREQUENCY) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_END_FREQUENCY))) {
                                if (CommonIntegration.isCNRegion() && AnonymousClass3.this.factorId != null && (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL) || AnonymousClass3.this.factorId.equals(MenuConfigManager.SYM_RATE) || AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN))) {
                                    MtkLog.d(ScanViewActivity.TAG, "TV_SINGLE_SCAN_RF_CHANNEL>>");
                                    if (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN)) {
                                        ScanViewActivity.this.selectChannel(true);
                                    }
                                } else if (AnonymousClass3.this.factorId != null && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_ANANLOG_SCAN_UP) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_ANANLOG_SCAN_DOWN) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_START_FREQUENCY) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG)) {
                                    AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN);
                                }
                            }
                        } else if (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
                            ScanViewActivity.this.refreshDVBTRFSignalQualityAndLevel(1000);
                        } else if (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                            ScanViewActivity.this.selectChannel();
                        }
                        int unused7 = ScanViewActivity.this.nowProgress = 0;
                        ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_error));
                        ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(103, MessageType.delayMillis2);
                    } else if (completeValue == 1) {
                        if (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_ANANLOG_SCAN_UP) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_ANANLOG_SCAN_DOWN) || AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_CHANNEL_START_FREQUENCY)) {
                            MtkLog.d(ScanViewActivity.TAG, "ScanUP/ScanDown Canceled!");
                            if (!ScanViewActivity.this.isScanning && ScanViewActivity.this.mATVFreq != 0) {
                                ScanViewActivity.this.mNumberFromTo.setText(String.format("%s%7.2f %s", new Object[]{ScanViewActivity.this.mContext.getString(R.string.menu_tv_freq), Float.valueOf((float) (((double) ScanViewActivity.this.mATVFreq) / 1000000.0d)), ScanViewActivity.this.mContext.getString(R.string.menu_tv_rf_scan_frequency_mhz)}));
                                if (CommonIntegration.getInstance().isCurrentSourceATV()) {
                                    ScanViewActivity.this.selectChannel(true);
                                }
                            }
                        } else if (!CommonIntegration.isEURegion() || (!AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION) && !AnonymousClass3.this.factorId.equals(MenuConfigManager.SYM_RATE))) {
                            ScanViewActivity.this.selectChannel();
                        } else if (AnonymousClass3.this.factorId.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
                            ScanViewActivity.this.refreshDVBTRFSignalQualityAndLevel(1000);
                        } else if (CommonIntegration.getInstance().isCurrentSourceDTV()) {
                            ScanViewActivity.this.selectChannel(true);
                        }
                        MenuDataHelper.getInstance(ScanViewActivity.this.mContext).changeEnable();
                        ScanViewActivity.this.mHandler.sendEmptyMessageDelayed(103, MessageType.delayMillis2);
                    }
                }
            }, 1000);
            if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
                boolean unused = ScanViewActivity.this.writeAutotestScanFile();
            }
        }

        public void onFrequence(int freq) {
            MtkLog.d(ScanViewActivity.TAG, "onFrequence:" + freq);
            MtkLog.d(ScanViewActivity.TAG, "onFrequence,Str," + ScanViewActivity.this.mNumberFromTo.getText());
            int unused = ScanViewActivity.this.mATVFreq = freq;
            final float frequency = (float) (((double) freq) / 1000000.0d);
            ScanViewActivity.this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    ScanViewActivity.this.mNumberFromTo.setText(String.format("%s%7.2f %s", new Object[]{ScanViewActivity.this.mContext.getString(R.string.menu_tv_freq), Float.valueOf(frequency), ScanViewActivity.this.mContext.getString(R.string.menu_tv_rf_scan_frequency_mhz)}));
                }
            }, 10);
        }

        public void onProgress(int progress, int channels) {
        }

        public void onProgress(int progress, int channels, int type) {
            String digitalNum = ScanViewActivity.this.mContext.getString(R.string.menu_tv_digital_channels);
            final int i = progress;
            final int i2 = channels;
            final int i3 = type;
            final String string = ScanViewActivity.this.mContext.getString(R.string.menu_tv_analog_channels);
            final String str = digitalNum;
            ScanViewActivity.this.mHandler.post(new Runnable() {
                public void run() {
                    int progressInt = i;
                    MtkLog.v(ScanViewActivity.TAG, "onProgress=" + i + ",nowProgress=" + ScanViewActivity.this.nowProgress + ",channels=" + i2 + ",type=" + i3 + ",mTv.getActionSize():" + ScanViewActivity.this.mTv.getActionSize());
                    if (ScanViewActivity.this.svl > 2) {
                        int totalSize = ScanViewActivity.this.mTv.getScanManager().getDVBSTotalSatSize();
                        int currentIndex = ScanViewActivity.this.mTv.getScanManager().getDVBSCurrentIndex();
                        if (totalSize > 1) {
                            progressInt = ((currentIndex * 100) / totalSize) + (i / totalSize);
                        } else {
                            progressInt = i;
                        }
                        ScanViewActivity.this.mTv.getScanManager().setDVBSScannedChannel(currentIndex, i2);
                    } else if (ScanViewActivity.this.mTv.getActionSize() > 0 || ScanViewActivity.this.nowProgress == 50) {
                        progressInt = ScanViewActivity.this.nowProgress + (i / 2);
                    }
                    ScanViewActivity.this.mFinishpercentage.setText(String.format("%3d%s", new Object[]{Integer.valueOf(progressInt), "%"}));
                    ScanViewActivity.this.loading.setVisibility(0);
                    if (!ScanViewActivity.this.loading.isLoading()) {
                        ScanViewActivity.this.loading.drawLoading();
                    }
                    ScanViewActivity.this.progressBar.setProgress(progressInt);
                    switch (i3) {
                        case 0:
                        case 1:
                            ScanViewActivity.this.mAnaloguechannel.setText(String.format("%s:%3d", new Object[]{string, Integer.valueOf(i2)}));
                            return;
                        case 2:
                            if (ScanViewActivity.this.svl > 2) {
                                MtkLog.d(ScanViewActivity.TAG, "onProgress(),updateChannelNum:");
                                int dvbsChannels = ScanViewActivity.this.mTv.getScanManager().getDVBSScannedChannel();
                                ScanViewActivity.this.mDVBSChannels.setText(String.format("%s:%3d", new Object[]{str, Integer.valueOf(dvbsChannels)}));
                                TextView access$1100 = ScanViewActivity.this.mAnaloguechannel;
                                access$1100.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_satellite_name) + ScanViewActivity.this.mTv.getScanManager().getFirstSatName());
                                ScanViewActivity.this.mNumberChannel.setText(ScanViewActivity.this.getSatName(ScanViewActivity.this.mTv.getScanManager().getFirstSatName()));
                                MtkLog.d(ScanViewActivity.TAG, "progressInt:" + progressInt + " " + ScanViewActivity.this.getSatName(""));
                                ScanViewActivity.this.mTv.getScanManager().setChannelsNum(dvbsChannels, 2);
                                return;
                            }
                            ScanViewActivity.this.mNumberChannel.setText(String.format("%s:%3d", new Object[]{str, Integer.valueOf(i2)}));
                            return;
                        default:
                            return;
                    }
                }
            });
        }

        /* access modifiers changed from: private */
        public void beforeDvbsScan() {
            SatelliteInfo satelliteInfo = ScanContent.getDVBSEnablesatellites(ScanViewActivity.this.mContext).get(ScanViewActivity.this.mTv.getScanManager().getDVBSCurrentIndex());
            ScanViewActivity.this.updateSatelliteName();
            if (satelliteInfo.getMotorType() == 5) {
                ScanContent.setDVBSFreqToGetSignalQuality(satelliteInfo.getSatlRecId());
                ScanViewActivity.this.mStateTextView.setText(ScanViewActivity.this.mContext.getString(R.string.menu_setup_channel_scan_positioner_moving));
                int unused = ScanViewActivity.this.tsLockTimes = 0;
                ScanViewActivity.this.mHandler.removeMessages(250);
                ScanViewActivity.this.mHandler.sendEmptyMessage(250);
                return;
            }
            ScanViewActivity.this.mTv.getScanManager().startDvbsScanAfterTsLock();
        }

        public void onDVBSInfoUpdated(final int argv4, final String name) {
            if (!TextUtils.isEmpty(name)) {
                ScanViewActivity.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (name.equals("TKGS_LOGO_UPDATE")) {
                            ScanViewActivity.this.showTKGSUpdateScanDialog(argv4);
                        } else if (name.equals("TKGS_LOGO")) {
                            ScanViewActivity.this.showTKGSRescanServiceListView(argv4);
                        } else if (name.equals("ChangeSatelliteFrequence")) {
                            AnonymousClass3.this.beforeDvbsScan();
                        } else {
                            int satId = ScanViewActivity.this.mTv.getScanManager().getCurrentSalId();
                            MtkLog.d(ScanViewActivity.TAG, "onDVBSInfoUpdated=" + name + ",salId=" + satId);
                            SatDetailUI.getInstance(ScanViewActivity.this.mContext).updateOnlySatelliteName(name, satId);
                        }
                    }
                });
            } else if (argv4 != 5) {
                ScanViewActivity.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (ScanViewActivity.this.mNumberChannel != null && !name.isEmpty()) {
                            ScanViewActivity.this.mNumberChannel.setText(ScanViewActivity.this.getSatName(name));
                        }
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public String mScanType = FAV_US_SINGLE_RF_CHANNEL;
    /* access modifiers changed from: private */
    public Handler mSelCelHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 272) {
                MtkLog.d(ScanViewActivity.TAG, " hi SCAN_LIST_SElECTED_FOR_SETTING_TTS index  ==" + msg.arg1);
                ScanViewActivity.this.mListView.setSelection(msg.arg1);
            }
        }
    };
    /* access modifiers changed from: private */
    public TextView mStateTextView;
    private String[] mTnuerArr;
    private TextView mToChannel;
    /* access modifiers changed from: private */
    public int mTunerMode;
    private TextView mTunerModeView;
    /* access modifiers changed from: private */
    public TVContent mTv;
    int netWorkIdValue;
    /* access modifiers changed from: private */
    public int nowProgress;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            MtkLog.d(ScanViewActivity.TAG, "onItemClick: position:" + position);
            if (ScanViewActivity.this.mActionID != null && !ScanViewActivity.this.isScanning) {
                if (ScanViewActivity.this.mActionID.startsWith(MenuConfigManager.FACTORY_TV_RANGE_SCAN)) {
                    ScanViewActivity.this.startFacRangeScan(((ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name)).id, ScanViewActivity.this.mActionID);
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN)) {
                    ScanViewActivity.this.startFacSingleScan(MenuConfigManager.FAV_US_SINGLE_RF_CHANNEL);
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN)) {
                    ScanFactorAdapter.ScanFactorItem sleItem = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                    if (sleItem.id.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
                        ScanViewActivity.this.startSingleRFScan(sleItem.optionValue);
                    }
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_SCAN)) {
                    if (((ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name)).id.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN)) {
                        ScanViewActivity.this.startDVBSFullScan(ScanViewActivity.this.mSatID, -1, ScanViewActivity.this.mLocationID);
                    }
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_ANALOG_SCAN)) {
                    if (CommonIntegration.isCNRegion()) {
                        ScanFactorAdapter.ScanFactorItem sleItem2 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                        ScanFactorAdapter.ScanFactorItem freqStartItem = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(0).getTag(R.id.factor_name);
                        ScanFactorAdapter.ScanFactorItem freqEndItem = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(1).getTag(R.id.factor_name);
                        if (sleItem2.id.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN)) {
                            ScanViewActivity.this.startAnalogATVFreqRangeScan(freqStartItem.inputValue, freqEndItem.inputValue);
                        } else {
                            ScanViewActivity.this.gotoEditTextAct(sleItem2);
                        }
                    } else {
                        ScanFactorAdapter.ScanFactorItem sleItem3 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                        ScanFactorAdapter.ScanFactorItem freqItem = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getChildAt(0).getTag(R.id.factor_name);
                        if (sleItem3.id.equals(MenuConfigManager.TV_ANANLOG_SCAN_UP)) {
                            ScanViewActivity scanViewActivity = ScanViewActivity.this;
                            scanViewActivity.startAnalogScan(true, "" + freqItem.inputValue);
                        } else if (sleItem3.id.equals(MenuConfigManager.TV_ANANLOG_SCAN_DOWN)) {
                            ScanViewActivity scanViewActivity2 = ScanViewActivity.this;
                            scanViewActivity2.startAnalogScan(false, "" + freqItem.inputValue);
                        } else {
                            ScanViewActivity.this.gotoEditTextAct(sleItem3);
                        }
                    }
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CN)) {
                    ScanFactorAdapter.ScanFactorItem sleItem4 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                    if (sleItem4.id.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL)) {
                        ScanViewActivity.this.startSingleRFScan(sleItem4.optionValue);
                    } else if (sleItem4.id.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN)) {
                        ScanViewActivity.this.startDVBCSingleRFScan();
                    } else if (sleItem4.id.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || sleItem4.id.equals(MenuConfigManager.SYM_RATE)) {
                        ScanViewActivity.this.gotoEditTextAct(sleItem4);
                    }
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBC) || ScanViewActivity.this.mActionID.startsWith(MenuConfigManager.TV_CHANNEL_SCAN_DVBC_OPERATOR)) {
                    ScanFactorAdapter.ScanFactorItem sleItem5 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                    if (sleItem5.id.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                        ScanViewActivity.this.startDVBCFullScan();
                    } else if (ScanViewActivity.this.cableOperator == null || !ScanViewActivity.this.cableOperator.equals("RCS RDS")) {
                        ScanViewActivity.this.gotoEditTextAct(sleItem5, true);
                    }
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN)) {
                    ScanFactorAdapter.ScanFactorItem sleItem6 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                    if (sleItem6.id.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                        ScanViewActivity.this.startDVBCSingleRFScan();
                    } else {
                        ScanViewActivity.this.gotoEditTextAct(sleItem6);
                    }
                } else if (ScanViewActivity.this.mActionID.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
                    ScanFactorAdapter.ScanFactorItem sleItem7 = (ScanFactorAdapter.ScanFactorItem) ScanViewActivity.this.mListView.getSelectedView().getTag(R.id.factor_name);
                    if (sleItem7.id.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY)) {
                        ScanViewActivity.this.gotoEditTextAct(sleItem7);
                    } else if (sleItem7.id.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
                        ScanViewActivity.this.startDVBCCNQuickFullScan();
                    }
                }
            }
        }
    };
    public ProgressBar progressBar;
    private SaveValue saveV;
    /* access modifiers changed from: private */
    public int svl;
    /* access modifiers changed from: private */
    public int tsLockTimes = 0;

    static /* synthetic */ int access$1008(ScanViewActivity x0) {
        int i = x0.mAddedChannelCount;
        x0.mAddedChannelCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$2308(ScanViewActivity x0) {
        int i = x0.tsLockTimes;
        x0.tsLockTimes = i + 1;
        return i;
    }

    /* access modifiers changed from: private */
    public void selectChannel() {
        selectChannel(false);
    }

    /* access modifiers changed from: private */
    public boolean selectScanedRFChannel() {
        MtkLog.d(TAG, "selectScanedRFChannel>");
        if (!this.mTv.selectScanedRFChannel()) {
            return false;
        }
        setChlistBroadcast();
        return true;
    }

    /* access modifiers changed from: private */
    public void selectChannel(boolean needSelectCurrent) {
        final int current = CommonIntegration.getInstance().getCurrentChannelId();
        boolean haveChannels = this.mTv.getScanManager().hasChannels();
        MtkLog.d(TAG, "current >>>>>" + current + ",haveChannes=" + haveChannels);
        if (needSelectCurrent || !haveChannels) {
            this.editChannel.selectChannel(current);
        } else {
            new Thread() {
                public void run() {
                    TIFChannelInfo info = TIFChannelManager.getInstance(ScanViewActivity.this.mContext).getFirstChannelForScan();
                    MtkLog.d(ScanViewActivity.TAG, "selectChannel>>>>>" + info);
                    if (info != null) {
                        TIFChannelManager.getInstance(ScanViewActivity.this.mContext).selectChannelByTIFInfo(info);
                        return;
                    }
                    MtkLog.d(ScanViewActivity.TAG, "selectChannel>>>>> null");
                    ScanViewActivity.this.editChannel.selectChannel(current);
                }
            }.start();
        }
        setChlistBroadcast();
    }

    /* access modifiers changed from: private */
    public void setChlistBroadcast() {
        MtkLog.d(TAG, "select broadcast chlist type");
        SaveValue instance = SaveValue.getInstance(this.mContext);
        instance.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_view_layout);
        this.mContext = this;
        this.svl = CommonIntegration.getInstance().getSvlFromACFG();
        this.mActionID = getIntent().getStringExtra("ActionID");
        this.isAutoScan = getIntent().getBooleanExtra("isAutoScan", false);
        this.cableOperator = getIntent().getStringExtra("CableOperator");
        if (this.cableOperator != null) {
            ScanContent.setOperator(this, this.cableOperator);
        }
        if (this.mActionID != null && this.mActionID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_SCAN)) {
            this.mSatID = getIntent().getIntExtra("SatID", -1);
            this.mLocationID = getIntent().getStringExtra("LocationID");
        }
        if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
            MenuConfigManager mConfigManager2 = MenuConfigManager.getInstance(this.mContext);
            mConfigManager2.setValue("g_bs__bs_src", getIntent().getIntExtra("tuner_mode", mConfigManager2.getDefault("g_bs__bs_src")));
        }
        init();
        this.mTv.mHandler = this.mHandler;
        this.mTv.stopTimeShift();
        if (this.mActionID != null) {
            if (CommonIntegration.isEURegion()) {
                if (this.mActionID.equals(MenuConfigManager.TV_ANALOG_SCAN)) {
                    if (CommonIntegration.getInstance().isCurrentSourceDTV()) {
                        CommonIntegration.getInstance().stopMainOrSubTv();
                    }
                } else if (this.mActionID.equals(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN) || this.mActionID.equals(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN)) {
                    if (CommonIntegration.getInstance().isCurrentSourceATV()) {
                        CommonIntegration.getInstance().stopMainOrSubTv();
                    } else {
                        if (this.mActionID.equals(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN)) {
                            this.editChannel.tuneDVBCRFSignal(this.mTv.getInitDVBCRFFreq() * 1000);
                        }
                        sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 1000);
                    }
                }
            }
            if ((CommonIntegration.isUSRegion() || CommonIntegration.isSARegion()) && this.mActionID.equals(MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN) && CommonIntegration.getInstance().isCurrentSourceATV()) {
                CommonIntegration.getInstance().stopMainOrSubTv();
            }
            if (this.mActionID.equals(MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN) || (this.mActionID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CN) && this.mTunerMode == 0)) {
                sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 1000);
            }
        }
        String operator = ScanContent.getOperator(this.mContext);
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter1.addAction("android.intent.action.SCREEN_OFF");
        intentFilter1.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(this.mReceiver, intentFilter1);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (this.mTv.isScanning()) {
            cancelScan();
            MtkLog.d(TAG, "onPause() mActionID:" + this.mActionID);
        }
        this.mTv.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        MtkLog.d(TAG, "onDestory....");
        unregisterReceiver(this.mReceiver);
        this.mTv.removeCallBackListener(this.mHandler);
        this.mTv.setScanListener((ScannerListener) null);
        this.mScanListener = null;
        TurnkeyUiMainActivity instance = TurnkeyUiMainActivity.getInstance();
        super.onDestroy();
    }

    private void init() {
        this.mTv = TVContent.getInstance(this.mContext);
        this.saveV = SaveValue.getInstance(this.mContext);
        this.mConfigManager = MenuConfigManager.getInstance(this.mContext);
        this.editChannel = EditChannel.getInstance(this.mContext);
        this.mStateTextView = (TextView) findViewById(R.id.state);
        this.progressBar = (ProgressBar) findViewById(R.id.scanprogressbar);
        this.mFinishpercentage = (TextView) findViewById(R.id.finishpercentage);
        this.loading = (Loading) findViewById(R.id.setup_tv_scan_loading);
        TextView textView = this.mFinishpercentage;
        textView.setText(this.mPercentage + "%");
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        this.mAnaloguechannel = (TextView) findViewById(R.id.analoguechannel);
        this.mAnaloguechannel.setText(this.mContext.getString(R.string.menu_setup_channel_scan_ana));
        this.mNumberChannel = (TextView) findViewById(R.id.numberchannel);
        this.mNumberChannel.setText(this.mContext.getString(R.string.menu_setup_channel_scan_dig));
        this.mTunerModeView = (TextView) findViewById(R.id.trun_mode);
        this.mTnuerArr = this.mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu);
        this.mTunerModeView.setText(this.mTnuerArr[this.mTv.getCurrentTunerMode()]);
        this.mNumberFromTo = (TextView) findViewById(R.id.numberchannel_from_to);
        this.mNumberFromTo.setVisibility(4);
        this.mDVBSChannels = (TextView) findViewById(R.id.dvbsusedigital_channels);
        if (CommonIntegration.isEURegion() && this.mActionID != null && (this.mActionID.equals(MenuConfigManager.FACTORY_TV_RANGE_SCAN_DIG) || this.mActionID.equals(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN))) {
            this.allRFChannels = this.mTv.getDvbtAllRFChannels();
        }
        initFactorListView();
        this.mTunerMode = this.mTv.getCurrentTunerMode();
        if (this.mTunerMode >= 2) {
            this.mAnaloguechannel.setText("");
            this.mNumberChannel.setText("");
        }
        initText(this.mActionID);
    }

    private void initFactorListView() {
        this.mListView = (ListView) findViewById(R.id.scan_factor_listview);
        this.mAdapter = new ScanFactorAdapter(this, generateFactors());
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setAccessibilityDelegate(this.mAccDelegateForChList);
        this.mListView.setOnItemClickListener(this.onItemClickListener);
    }

    private void showInformDialogForVoo() {
        this.dialog = new LiveTVDialog(this, "", this.mContext.getString(R.string.menu_c_scan_clear_channel), 3);
        this.dialog.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.dialog.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.dialog.bindKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23 && keyCode != 183) {
                    return false;
                }
                if (v.getId() == ScanViewActivity.this.dialog.getButtonYes().getId()) {
                    ScanViewActivity.this.dialog.dismiss();
                    ScanViewActivity.this.startDVBCFullScan();
                    return true;
                } else if (v.getId() != ScanViewActivity.this.dialog.getButtonNo().getId()) {
                    return true;
                } else {
                    ScanViewActivity.this.dialog.dismiss();
                    return true;
                }
            }
        });
        this.dialog.show();
        this.dialog.getButtonNo().requestFocus();
    }

    private List<ScanFactorAdapter.ScanFactorItem> generateFactors() {
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        if (this.mActionID == null) {
            return list;
        }
        if (this.mActionID.startsWith(MenuConfigManager.FACTORY_TV_RANGE_SCAN)) {
            return loadUSSAEURangeScan();
        }
        if (this.mActionID.equals(MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN)) {
            return loadUSSASingleScan();
        }
        if (this.mActionID.equals(MenuConfigManager.TV_ANALOG_SCAN)) {
            return loadAnalogManualScan();
        }
        if (this.mActionID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CN)) {
            return loadCEDTMBSingleRFScan();
        }
        if (this.mActionID.equals(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN)) {
            ScanFactorAdapter.ScanFactorItem scanFactorItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS, this.mContext.getString(R.string.menu_tv_single_rf_channel), 0, new String[]{this.mTv.getRFChannel(0)}, true);
            ScanFactorAdapter.ScanFactorItem mSignalLevel = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_LEVEL, this.mContext.getString(R.string.menu_tv_single_signal_level), 0, false);
            ScanFactorAdapter.ScanFactorItem mSignalQuality = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_QUALITY, this.mContext.getString(R.string.menu_tv_single_signal_quality), 0, false);
            list.add(scanFactorItem);
            list.add(mSignalLevel);
            list.add(mSignalQuality);
            return list;
        } else if (this.mActionID.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBC)) {
            list.addAll(initNoOparatorItem());
            if (!"1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
                return list;
            }
            ScanContent.setOperator(this.mContext, this.mContext.getString(R.string.dvbc_operator_others));
            startDVBCFullScan();
            return list;
        } else if (this.mActionID.startsWith(MenuConfigManager.TV_CHANNEL_SCAN_DVBC_OPERATOR)) {
            list.addAll(initDVBCOperatorItem(CableOperator.values()[Integer.parseInt(this.mActionID.split("#")[1])]));
            return list;
        } else if (this.mActionID.equals(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN)) {
            list.addAll(genDVBCRfScanItem());
            return list;
        } else if (this.mActionID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_SCAN)) {
            list.addAll(genDVBSScanItem());
            return list;
        } else if (this.mActionID.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
            return initDVBCCNQuickFullItem();
        } else {
            return list;
        }
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

    private List<ScanFactorAdapter.ScanFactorItem> initDVBCCNQuickFullItem() {
        String fullStr = this.mContext.getString(R.string.menu_arrays_Full);
        String quickStr = this.mContext.getString(R.string.menu_arrays_Quick);
        String[] scanModeArrayForDVBCCN = this.mContext.getResources().getStringArray(R.array.menu_scan_mode_cn);
        List<ScanFactorAdapter.ScanFactorItem> addList = new ArrayList<>();
        ScanFactorAdapter.ScanFactorItem scanMode = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.SCAN_MODE_DVBC, this.mContext.getString(R.string.menu_tv_sigle_scan_mode), 0, scanModeArrayForDVBCCN, true);
        scanMode.value = scanModeArrayForDVBCCN[0];
        addList.add(scanMode);
        int scanLowFrq = DVBCCNScanner.mLowerFreq / 1000000;
        final ScanFactorAdapter.ScanFactorItem frequecy = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY, this.mContext.getString(R.string.menu_c_rfscan_frequency_cn), scanLowFrq, scanLowFrq, DVBCCNScanner.mHighFreq / 1000000, true);
        ScanFactorAdapter.ScanFactorItem startScan = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN, this.mContext.getString(R.string.menu_c_scan), true);
        addList.add(startScan);
        final ScanFactorAdapter.ScanFactorItem scanFactorItem = scanMode;
        final String str = quickStr;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem2 = startScan;
        List<ScanFactorAdapter.ScanFactorItem> addList2 = addList;
        final String str2 = fullStr;
        scanMode.addScanOptionChangeListener(new ScanFactorAdapter.ScanOptionChangeListener() {
            public void onScanOptionChange(String afterName) {
                if (afterName != null && !afterName.equals("")) {
                    scanFactorItem.value = afterName;
                    List<ScanFactorAdapter.ScanFactorItem> list = ScanViewActivity.this.mAdapter.getList();
                    list.remove(frequecy);
                    if (afterName.equalsIgnoreCase(str)) {
                        MtkLog.e("OptionValuseChangedCallBack>>>>", "notifcy ------");
                        list.add(frequecy);
                        list.remove(scanFactorItem2);
                        list.add(scanFactorItem2);
                    } else {
                        afterName.equalsIgnoreCase(str2);
                    }
                    ScanViewActivity.this.mAdapter.notifyDataSetChanged();
                }
            }
        });
        return addList2;
    }

    private List<ScanFactorAdapter.ScanFactorItem> initDVBCOperatorItem(CableOperator operator) {
        CableOperator cableOperator2 = operator;
        MtkLog.v(TAG, "start initDVBCOperatorItem() operator:" + operator.name());
        String advanceStr = this.mContext.getString(R.string.menu_arrays_Advance);
        String fullStr = this.mContext.getString(R.string.menu_arrays_Full);
        String quickStr = this.mContext.getString(R.string.menu_arrays_Quick);
        String[] scanModeArray = ScanContent.initScanModesForOperator(this.mContext, cableOperator2, (List<CableOperator>) null);
        String operatorStr = ScanContent.getOperatorStr(this.mContext, cableOperator2);
        List<ScanFactorAdapter.ScanFactorItem> addList = new ArrayList<>();
        int scanModeIndex = getDVBCScanmodeIndex(scanModeArray);
        ScanFactorAdapter.ScanFactorItem scanMode = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.SCAN_MODE_DVBC, this.mContext.getString(R.string.menu_tv_sigle_scan_mode), getDVBCScanmodeIndex(scanModeArray), scanModeArray, true);
        addList.add(scanMode);
        if (scanModeArray.length == 1 || isBelgiumCableVoo()) {
            scanMode.isEnable = false;
        }
        String scanModeString = scanModeArray[scanModeIndex];
        scanMode.value = scanModeString;
        if (!MarketRegionInfo.isFunctionSupport(16)) {
            String[] scanTypeArray = this.mContext.getResources().getStringArray(R.array.menu_scan_type);
            final ScanFactorAdapter.ScanFactorItem scanFactorItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_TYPE, this.mContext.getString(R.string.menu_channel_scan_type), 0, scanTypeArray, true);
            scanFactorItem.value = scanTypeArray[0];
            scanFactorItem.addScanOptionChangeListener(new ScanFactorAdapter.ScanOptionChangeListener() {
                public void onScanOptionChange(String afterName) {
                    if (afterName != null && !afterName.equals("")) {
                        scanFactorItem.value = afterName;
                        ScanViewActivity.this.mAdapter.notifyDataSetChanged();
                    }
                }
            });
            addList.add(scanFactorItem);
        }
        int frequencyValue2 = ScanContent.getFrequencyOperator(this.mContext, scanModeString, cableOperator2);
        String autoFreqStr = "";
        if (frequencyValue2 == -1) {
            autoFreqStr = this.mContext.getString(R.string.menu_arrays_Auto);
        }
        String autoFreqStr2 = autoFreqStr;
        ScanFactorAdapter.ScanFactorItem frequecy = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY, this.mContext.getString(R.string.menu_c_rfscan_frequency), frequencyValue2, 0, 999999, true);
        frequecy.value = autoFreqStr2;
        if (frequencyValue2 != -2) {
            addList.add(frequecy);
        }
        int netWorkIdValue2 = ScanContent.getNetWorkIDOperator(this.mContext, scanModeString, cableOperator2);
        String autoNetworkIDStr = "";
        if (MarketRegionInfo.isFunctionSupport(3)) {
            netWorkIdValue2 = -1;
        }
        if (netWorkIdValue2 == -1) {
            autoNetworkIDStr = this.mContext.getString(R.string.menu_arrays_Auto);
        } else if (netWorkIdValue2 == -3) {
            autoNetworkIDStr = "";
        }
        String autoNetworkIDStr2 = autoNetworkIDStr;
        if (isBelgiumCableVoo()) {
            netWorkIdValue2 = -1;
        }
        ScanFactorAdapter.ScanFactorItem frequecy2 = frequecy;
        ScanFactorAdapter.ScanFactorItem netWorkID = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_NETWORKID, this.mContext.getString(R.string.menu_c_net), netWorkIdValue2, 0, 999999, true);
        if (isBelgiumCableVoo()) {
            netWorkID.value = "0000";
        } else {
            netWorkID.value = autoNetworkIDStr2;
        }
        if (netWorkIdValue2 != -2) {
            addList.add(netWorkID);
        }
        String autoNetworkIDStr3 = autoNetworkIDStr2;
        ScanFactorAdapter.ScanFactorItem scan = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN, this.mContext.getString(R.string.menu_c_scan), true);
        addList.add(scan);
        AnonymousClass11 r11 = r0;
        int i = netWorkIdValue2;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem2 = scanMode;
        ScanFactorAdapter.ScanFactorItem scan2 = scan;
        String str = autoNetworkIDStr3;
        final String[] strArr = scanModeArray;
        ScanFactorAdapter.ScanFactorItem netWorkID2 = netWorkID;
        final CableOperator cableOperator3 = cableOperator2;
        String str2 = autoFreqStr2;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem3 = frequecy2;
        int i2 = frequencyValue2;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem4 = netWorkID2;
        String str3 = scanModeString;
        final String scanModeString2 = quickStr;
        String str4 = quickStr;
        ScanFactorAdapter.ScanFactorItem scanMode2 = scanMode;
        final ScanFactorAdapter.ScanFactorItem scanMode3 = scan2;
        List<ScanFactorAdapter.ScanFactorItem> addList2 = addList;
        final String str5 = advanceStr;
        String[] strArr2 = scanModeArray;
        final String str6 = fullStr;
        AnonymousClass11 r0 = new ScanFactorAdapter.ScanOptionChangeListener() {
            public void onScanOptionChange(String afterName) {
                if (afterName != null && !afterName.equals("")) {
                    scanFactorItem2.value = afterName;
                    SaveValue.setLocalMemoryValue("DVBCScanMode", ScanViewActivity.this.getIndexFromScanmodeName(afterName, strArr));
                    int frequencyValue = ScanContent.getFrequencyOperator(ScanViewActivity.this.mContext, afterName, cableOperator3);
                    int netWorkIdValue = ScanContent.getNetWorkIDOperator(ScanViewActivity.this.mContext, afterName, cableOperator3);
                    scanFactorItem3.inputValue = frequencyValue;
                    scanFactorItem4.inputValue = netWorkIdValue;
                    scanFactorItem3.value = frequencyValue == -1 ? ScanViewActivity.this.mContext.getString(R.string.menu_arrays_Auto) : "";
                    scanFactorItem4.value = netWorkIdValue == -1 ? ScanViewActivity.this.mContext.getString(R.string.menu_arrays_Auto) : "";
                    List<ScanFactorAdapter.ScanFactorItem> list = ScanViewActivity.this.mAdapter.getList();
                    list.remove(scanFactorItem3);
                    list.remove(scanFactorItem4);
                    MtkLog.d(ScanViewActivity.TAG, String.format("afterName%s,frequencyValue:%s,netWorkIdValue:%s", new Object[]{afterName, Integer.valueOf(frequencyValue), Integer.valueOf(netWorkIdValue)}));
                    if (afterName.equalsIgnoreCase(scanModeString2)) {
                        if (frequencyValue != -2) {
                            list.add(scanFactorItem3);
                        }
                        if (netWorkIdValue != -2) {
                            list.add(scanFactorItem4);
                        }
                        list.remove(scanMode3);
                        list.add(scanMode3);
                    } else if (afterName.equalsIgnoreCase(str5)) {
                        if (frequencyValue != -2) {
                            list.add(scanFactorItem3);
                        }
                        if (netWorkIdValue != -2) {
                            list.add(scanFactorItem4);
                        }
                        list.remove(scanMode3);
                        list.add(scanMode3);
                    } else {
                        afterName.equalsIgnoreCase(str6);
                    }
                    ScanViewActivity.this.mAdapter.notifyDataSetChanged();
                }
            }
        };
        scanMode2.addScanOptionChangeListener(r11);
        MtkLog.v(TAG, "end initDVBCOperatorItem() operator:" + operator.name());
        return addList2;
    }

    private List<ScanFactorAdapter.ScanFactorItem> initNoOparatorItem() {
        MtkLog.v(TAG, "start initNoOparatorItem()");
        String advanceStr = this.mContext.getString(R.string.menu_arrays_Advance);
        String fullStr = this.mContext.getString(R.string.menu_arrays_Full);
        String quickStr = this.mContext.getString(R.string.menu_arrays_Quick);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.menu_scan_mode);
        CableOperator operator = CableOperator.OTHER;
        String[] scanModeArray = ScanContent.initScanModesForOperator(this.mContext, operator, (List<CableOperator>) null);
        List<ScanFactorAdapter.ScanFactorItem> addList = new ArrayList<>();
        ScanFactorAdapter.ScanFactorItem scanMode = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.SCAN_MODE_DVBC, this.mContext.getString(R.string.menu_tv_sigle_scan_mode), this.mConfigManager.getDefaultScan("g_scan_mode__scan_mode"), scanModeArray, true);
        addList.add(scanMode);
        String scanModeString = scanModeArray[this.mConfigManager.getDefaultScan("g_scan_mode__scan_mode")];
        scanMode.value = scanModeString;
        if (!MarketRegionInfo.isFunctionSupport(16)) {
            String[] scanTypeArray = this.mContext.getResources().getStringArray(R.array.menu_scan_type);
            final ScanFactorAdapter.ScanFactorItem scanFactorItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_TYPE, this.mContext.getString(R.string.menu_channel_scan_type), 0, scanTypeArray, true);
            scanFactorItem.value = scanTypeArray[0];
            scanFactorItem.addScanOptionChangeListener(new ScanFactorAdapter.ScanOptionChangeListener() {
                public void onScanOptionChange(String afterName) {
                    if (afterName != null && !afterName.equals("")) {
                        scanFactorItem.value = afterName;
                        ScanViewActivity.this.mAdapter.notifyDataSetChanged();
                    }
                }
            });
            addList.add(scanFactorItem);
        }
        this.frequencyValue = ScanContent.getFrequencyOperator(this.mContext, scanModeString, operator);
        String autoFreqStr = "";
        if (this.frequencyValue == -1) {
            autoFreqStr = this.mContext.getString(R.string.menu_arrays_Auto);
        }
        String autoFreqStr2 = autoFreqStr;
        ScanFactorAdapter.ScanFactorItem frequecy = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY, this.mContext.getString(R.string.menu_c_rfscan_frequency), this.frequencyValue, 0, 999999, true);
        frequecy.value = autoFreqStr2;
        if (this.frequencyValue != -2) {
            addList.add(frequecy);
        }
        this.netWorkIdValue = ScanContent.getNetWorkIDOperator(this.mContext, scanModeString, operator);
        String autoNetworkIDStr = "";
        if (MarketRegionInfo.isFunctionSupport(3)) {
            this.netWorkIdValue = -1;
        }
        if (this.netWorkIdValue == -1) {
            autoNetworkIDStr = this.mContext.getString(R.string.menu_arrays_Auto);
        } else if (this.netWorkIdValue == -3) {
            autoNetworkIDStr = "";
        }
        String autoNetworkIDStr2 = autoNetworkIDStr;
        ScanFactorAdapter.ScanFactorItem netWorkID = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_NETWORKID, this.mContext.getString(R.string.menu_c_net), this.netWorkIdValue, 0, 999999, true);
        netWorkID.value = autoNetworkIDStr2;
        if (this.netWorkIdValue != -2) {
            addList.add(netWorkID);
        }
        String autoNetworkIDStr3 = autoNetworkIDStr2;
        ScanFactorAdapter.ScanFactorItem scan = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN, this.mContext.getString(R.string.menu_c_scan), true);
        addList.add(scan);
        List<ScanFactorAdapter.ScanFactorItem> addList2 = addList;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem2 = scanMode;
        AnonymousClass13 r9 = r0;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem3 = netWorkID;
        ScanFactorAdapter.ScanFactorItem scan2 = scan;
        String str = autoNetworkIDStr3;
        final ScanFactorAdapter.ScanFactorItem scan3 = frequecy;
        ScanFactorAdapter.ScanFactorItem scanFactorItem4 = frequecy;
        final String str2 = advanceStr;
        String str3 = autoFreqStr2;
        final String autoFreqStr3 = quickStr;
        String str4 = scanModeString;
        final ScanFactorAdapter.ScanFactorItem scanFactorItem5 = scan2;
        String str5 = advanceStr;
        ScanFactorAdapter.ScanFactorItem scanMode2 = scanMode;
        final String str6 = fullStr;
        AnonymousClass13 r0 = new ScanFactorAdapter.ScanOptionChangeListener() {
            public void onScanOptionChange(String afterName) {
                if (afterName != null && !afterName.equals("")) {
                    scanFactorItem2.value = afterName;
                    scanFactorItem3.inputValue = ScanViewActivity.this.netWorkIdValue;
                    scan3.inputValue = ScanViewActivity.this.frequencyValue;
                    List<ScanFactorAdapter.ScanFactorItem> list = ScanViewActivity.this.mAdapter.getList();
                    if (afterName.equalsIgnoreCase(str2) || afterName.equalsIgnoreCase(autoFreqStr3)) {
                        if (!list.contains(scan3)) {
                            if (list.contains(scanFactorItem3)) {
                                list.remove(scanFactorItem3);
                            }
                            list.add(scan3);
                        }
                        if (!list.contains(scanFactorItem3)) {
                            list.add(scanFactorItem3);
                        }
                        list.remove(scanFactorItem5);
                        list.add(scanFactorItem5);
                    } else if (afterName.equalsIgnoreCase(str6)) {
                        list.remove(scan3);
                        list.remove(scanFactorItem3);
                    }
                    ScanViewActivity.this.mAdapter.notifyDataSetChanged();
                }
            }
        };
        scanMode2.addScanOptionChangeListener(r9);
        MtkLog.v(TAG, "end initNoOparatorItem()");
        return addList2;
    }

    private List<ScanFactorAdapter.ScanFactorItem> loadUSSAEURangeScan() {
        ScanFactorAdapter.ScanFactorItem mtoChItem;
        ScanFactorAdapter.ScanFactorItem mfromChItem;
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        if (!CommonIntegration.isEURegion()) {
            mfromChItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.FAV_US_RANGE_FROM_CHANNEL, this.mContext.getString(R.string.menu_tv_sigle_from_channel), "" + this.mTv.getFirstScanIndex(), true);
            mtoChItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.FAV_US_RANGE_TO_CHANNEL, this.mContext.getString(R.string.menu_tv_sigle_to_channcel), "" + this.mTv.getLastScanIndex(), true);
            this.freFrom = this.mTv.getFirstScanIndex();
            this.freTo = this.mTv.getLastScanIndex();
            if (CommonIntegration.isUSRegion() && this.mActionID != null && this.mActionID.equals(MenuConfigManager.FACTORY_TV_RANGE_SCAN)) {
                list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.US_SCAN_MODE, this.mContext.getString(R.string.menu_tv_sigle_scan_mode), this.mTv.getUSRangeScanMode(), this.mContext.getResources().getStringArray(R.array.menu_tv_us_scan_mode_array), true));
            }
        } else {
            char[] chs = this.allRFChannels[this.allRFChannels.length - 1].toCharArray();
            for (char ch : chs) {
                MtkLog.d(TAG, "convert ch:" + ch + "after:" + chs[2]);
            }
            ScanFactorAdapter.ScanFactorItem mfromChItem2 = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.FAV_US_RANGE_FROM_CHANNEL, this.mContext.getString(R.string.menu_tv_sigle_from_channel), this.allRFChannels[0], true);
            mtoChItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.FAV_US_RANGE_TO_CHANNEL, this.mContext.getString(R.string.menu_tv_sigle_to_channcel), this.allRFChannels[this.allRFChannels.length - 1], true);
            this.freFrom = 0;
            this.freTo = this.allRFChannels.length - 1;
            mfromChItem = mfromChItem2;
        }
        list.add(mfromChItem);
        list.add(mtoChItem);
        return list;
    }

    private List<ScanFactorAdapter.ScanFactorItem> loadCEDTMBSingleRFScan() {
        MtkLog.d(TAG, "start call loadCEDTMBSingleRF:");
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        String[] scanModulation = this.mContext.getResources().getStringArray(R.array.menu_tv_scan_mode_array);
        int tunerMode = this.mConfigManager.getDefault("g_bs__bs_src");
        MtkLog.d(TAG, "tunerMode:" + tunerMode);
        int scanLowFrq = DVBCCNScanner.mLowerFreq / 1000000;
        int scanUpperFreq = DVBCCNScanner.mHighFreq / 1000000;
        if (tunerMode == 1) {
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY, this.mContext.getString(R.string.menu_tv_rf_scan_frequency), scanLowFrq, scanLowFrq, scanUpperFreq, true));
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.SYM_RATE, this.mContext.getString(R.string.menu_tv_rf_sym_rate), ScanContent.getSystemRate(), 1000, 9999, true));
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION, this.mContext.getString(R.string.menu_tv_rf_scan_mode), this.saveV.readValue(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION), scanModulation, true));
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN, this.mContext.getString(R.string.menu_tv_status_value), true));
        } else {
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL, this.mContext.getString(R.string.menu_tv_single_rf_channel), 0, new String[]{this.mTv.getCNRFChannel(0)}, true));
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_LEVEL, this.mContext.getString(R.string.menu_tv_single_signal_level), this.editChannel.getSignalLevel(), false));
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_QUALITY, this.mContext.getString(R.string.menu_tv_single_signal_quality), this.editChannel.getSignalQuality(), this.mContext.getResources().getStringArray(R.array.menu_setup_tv_single_signal_quality), false));
        }
        MtkLog.d(TAG, "end call loadCEDTMBSingleRF:");
        return list;
    }

    private List<ScanFactorAdapter.ScanFactorItem> loadAnalogManualScan() {
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        if (CommonIntegration.isCNRegion()) {
            ScanFactorAdapter.ScanFactorItem mScanStratFreqency = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_CHANNEL_START_FREQUENCY, this.mContext.getString(R.string.menu_tv_start_frequency), 44, 44, EUATVScanner.FREQ_HIGH_VALUE, true);
            ScanFactorAdapter.ScanFactorItem mScanEndFreqency = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_CHANNEL_END_FREQUENCY, this.mContext.getString(R.string.menu_tv_end_frequency), EUATVScanner.FREQ_HIGH_VALUE, 44, EUATVScanner.FREQ_HIGH_VALUE, true);
            list.add(mScanStratFreqency);
            list.add(mScanEndFreqency);
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_CHANNEL_STARTSCAN, this.mContext.getString(R.string.menu_tv_status_value), true));
        } else if (CommonIntegration.isEURegion()) {
            ScanFactorAdapter.ScanFactorItem mScanFreqency = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_CHANNEL_START_FREQUENCY, this.mContext.getString(R.string.menu_tv_start_frequency), this.mTv.getInitATVFreq(), 44, EUATVScanner.FREQ_HIGH_VALUE, true);
            ScanFactorAdapter.ScanFactorItem mScanUp = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_ANANLOG_SCAN_UP, this.mContext.getString(R.string.menu_c_analogscan_scanup), true);
            list.add(mScanFreqency);
            list.add(mScanUp);
            list.add(new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_ANANLOG_SCAN_DOWN, this.mContext.getString(R.string.menu_c_analogscan_scandown), true));
        }
        return list;
    }

    private List<ScanFactorAdapter.ScanFactorItem> loadUSSASingleScan() {
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        int rfIndex = this.mTv.getRFScanIndex();
        ScanFactorAdapter.ScanFactorItem mSingleRFChannel = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.FAV_US_SINGLE_RF_CHANNEL, this.mContext.getString(R.string.menu_tv_single_rf_channel), String.valueOf(rfIndex), true);
        this.freFrom = rfIndex;
        ScanFactorAdapter.ScanFactorItem mSignalLevel = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_LEVEL, this.mContext.getString(R.string.menu_tv_single_signal_level), this.editChannel.getSignalLevel(), false);
        if (CommonIntegration.isSARegion()) {
            ScanFactorAdapter.ScanFactorItem mSignalQuality = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_QUALITY, this.mContext.getString(R.string.menu_tv_single_signal_quality), this.editChannel.getSignalQuality(), this.mContext.getResources().getStringArray(R.array.menu_setup_tv_single_signal_quality), false);
            list.add(mSingleRFChannel);
            list.add(mSignalLevel);
            list.add(mSignalQuality);
        } else if (CommonIntegration.isUSRegion()) {
            String[] freplans = this.mContext.getResources().getStringArray(R.array.menu_tv_scan_frequency_plan_array);
            String[] modulationarrys = this.mContext.getResources().getStringArray(R.array.menu_tv_scan_modulation_array);
            int tunerMode = this.mConfigManager.getDefault("g_bs__bs_src");
            ScanFactorAdapter.ScanFactorItem mSingleMod = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_MODULATION, this.mContext.getString(R.string.menu_tv_sigle_modulation), this.saveV.readValue(MenuConfigManager.TV_SINGLE_SCAN_MODULATION), modulationarrys, false);
            String string = this.mContext.getString(R.string.menu_tv_sigle_frequency_plan);
            int i = this.mConfigManager.getDefault(MenuConfigManager.FREQUENEY_PLAN);
            String str = MenuConfigManager.FREQUENEY_PLAN;
            Object obj = MenuConfigManager.FREQUENEY_PLAN;
            ScanFactorAdapter.ScanFactorItem mSingleMod2 = mSingleMod;
            ScanFactorAdapter.ScanFactorItem mFreqPlan = new ScanFactorAdapter.ScanFactorItem(str, string, i, freplans, false);
            if (tunerMode == 0) {
                mSingleMod2.isEnable = false;
                mFreqPlan.isEnable = false;
            } else {
                mSingleMod2.isEnable = true;
                mFreqPlan.isEnable = true;
            }
            list.add(mSingleRFChannel);
            list.add(mFreqPlan);
            list.add(mSingleMod2);
            list.add(mSignalLevel);
        }
        return list;
    }

    private List<ScanFactorAdapter.ScanFactorItem> genDVBCRfScanItem() {
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        ScanFactorAdapter.ScanFactorItem freqency = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ, this.mContext.getString(R.string.menu_c_rfscan_frequency), this.mTv.getInitDVBCRFFreq(), 100, 999999, true);
        new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBC_SINGLE_RF_SCAN_MODULATION, this.mContext.getString(R.string.menu_tv_sigle_modulation), this.saveV.readValue("g_scan_mode__scan_mode"), this.mContext.getResources().getStringArray(R.array.menu_tv_scan_mode_array), true);
        new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.SYM_RATE, this.mContext.getString(R.string.menu_c_rfscan_symbol_rate), ScanContent.getSystemRate(), 1000, 9999, true);
        ScanFactorAdapter.ScanFactorItem scan = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN, this.mContext.getString(R.string.menu_c_scan), true);
        int mLevelValue = this.editChannel.getSignalLevel();
        if (CommonIntegration.getInstance().isCurrentSourceATV()) {
            mLevelValue = 0;
        }
        ScanFactorAdapter.ScanFactorItem mSignalLevel = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_LEVEL, this.mContext.getString(R.string.menu_tv_single_signal_level), mLevelValue, false);
        int mQualValue = this.editChannel.getSignalQuality();
        if (CommonIntegration.getInstance().isCurrentSourceATV()) {
            mQualValue = 0;
        }
        ScanFactorAdapter.ScanFactorItem mSignalQuality = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_QUALITY, this.mContext.getString(R.string.menu_tv_single_signal_quality), mQualValue, false);
        list.add(freqency);
        list.add(scan);
        list.add(mSignalLevel);
        list.add(mSignalQuality);
        return list;
    }

    /* access modifiers changed from: private */
    public void showTKGSUserMessageDialog(String message) {
        MtkLog.d(TAG, "showTKGSUserMessageDialog>>>");
        new TKGSUserMessageDialog(this.mContext).showConfirmDialog(message);
    }

    private List<ScanFactorAdapter.ScanFactorItem> genDVBSScanItem() {
        List<ScanFactorAdapter.ScanFactorItem> list = new ArrayList<>();
        List<String> scanModeList = ScanContent.getDVBSScanMode(this.mContext);
        List<String> scanChannels = ScanContent.getDVBSConfigInfoChannels(this.mContext);
        List<String> scanStores = ScanContent.getDVBSConfigInfoChannelStoreTypes(this.mContext);
        ScanFactorAdapter.ScanFactorItem scanFactorItem = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG, this.mContext.getResources().getString(R.string.dvbs_scan_mode), 0, (String[]) scanModeList.toArray(new String[scanModeList.size()]), true);
        if (scanModeList.size() > 1) {
            scanFactorItem.isEnable = true;
        } else {
            scanFactorItem.isEnable = false;
        }
        String channelsTitle = this.mContext.getResources().getString(R.string.dvbs_satellite_channel);
        String storeTypeTitle = this.mContext.getResources().getString(R.string.dvbs_satellite_channel_store_type);
        int i = R.string.dvbs_satellite_channel_store_type;
        ScanFactorAdapter.ScanFactorItem scanType = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_SCAN_CONFIG, channelsTitle, 0, (String[]) scanChannels.toArray(new String[scanChannels.size()]), true);
        String string = this.mContext.getResources().getString(i);
        ScanFactorAdapter.ScanFactorItem scanFactorItem2 = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_STORE_CONFIG, storeTypeTitle, 0, (String[]) scanStores.toArray(new String[scanStores.size()]), true);
        ScanFactorAdapter.ScanFactorItem scan = new ScanFactorAdapter.ScanFactorItem(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN, this.mContext.getString(R.string.menu_c_scan), true);
        list.add(scanFactorItem);
        list.add(scanType);
        list.add(scanFactorItem2);
        list.add(scan);
        return list;
    }

    private void initText(String itemID) {
        this.mItemID = itemID;
        if (itemID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_SCAN)) {
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_tv_scann_allchannels));
            this.mDVBSChannels.setText(this.mContext.getString(R.string.menu_tv_digital_channels));
            this.mAnaloguechannel.setText("");
        } else if (this.mActionID != null && this.mActionID.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_tv_scan_digital_channel_init));
        } else if (this.mTv.isCurrentSourceDTV()) {
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_tv_single_rf_scan_init));
        } else {
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_tv_analog_manual_scan_init));
        }
        this.loading.setVisibility(4);
        setScanProgress(0);
        this.progressBar.setVisibility(0);
        this.mFinishpercentage.setVisibility(0);
        MtkLog.d(TAG, "test-0:" + itemID);
        this.mDVBSChannels.setVisibility(4);
        if (itemID.equals(MenuConfigManager.FACTORY_TV_RANGE_SCAN)) {
            this.mStateTextView.setText(R.string.menu_tv_us_range_scan);
            this.mTunerModeView.setText(this.mTnuerArr[this.mTv.getCurrentTunerMode()]);
            checkScanMode();
            this.mNumberFromTo.setVisibility(4);
        } else if (itemID.equals(MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN) || itemID.equals(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN) || itemID.equals(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN) || itemID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CN)) {
            this.mStateTextView.setText(R.string.menu_tv_single_rf_scan_init);
            this.mTunerModeView.setText(this.mTnuerArr[this.mTv.getCurrentTunerMode()]);
            this.mAnaloguechannel.setVisibility(4);
            this.mNumberChannel.setVisibility(0);
            this.mNumberFromTo.setVisibility(4);
        } else if (itemID.equals(MenuConfigManager.FACTORY_TV_RANGE_SCAN_DIG)) {
            this.mStateTextView.setText(R.string.menu_tv_us_range_scan);
            this.mAnaloguechannel.setVisibility(4);
            this.mNumberChannel.setVisibility(0);
            this.mTunerModeView.setVisibility(4);
            this.mNumberFromTo.setVisibility(0);
            this.mNumberFromTo.setText("");
        } else if (itemID.equals(MenuConfigManager.FACTORY_TV_RANGE_SCAN_ANA)) {
            this.mStateTextView.setText(R.string.menu_tv_us_range_scan);
            this.mAnaloguechannel.setVisibility(0);
            this.mNumberChannel.setVisibility(4);
            this.mTunerModeView.setVisibility(4);
            this.mNumberFromTo.setVisibility(4);
        } else if (itemID.equals(MenuConfigManager.TV_ANALOG_SCAN)) {
            this.mStateTextView.setText(R.string.menu_tv_analog_manual_scan_init);
            this.mNumberChannel.setVisibility(4);
            this.mTunerModeView.setVisibility(4);
            this.mAnaloguechannel.setVisibility(4);
            this.progressBar.setVisibility(4);
            this.mFinishpercentage.setVisibility(4);
            this.mNumberFromTo.setVisibility(4);
        } else if (itemID.startsWith(MenuConfigManager.TV_CHANNEL_SCAN_DVBC_OPERATOR)) {
            this.mStateTextView.setText(R.string.menu_tv_scann_allchannels);
            this.mNumberChannel.setVisibility(4);
            this.mTunerModeView.setVisibility(4);
            this.mAnaloguechannel.setVisibility(4);
            this.mNumberChannel.setVisibility(4);
            this.mNumberFromTo.setVisibility(4);
        } else if (itemID.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBC)) {
            MtkLog.d(TAG, "test-1:channel_scan_dvbc_fulls");
            this.mStateTextView.setText(R.string.menu_tv_scann_allchannels);
            this.mNumberChannel.setVisibility(0);
            this.mTunerModeView.setVisibility(0);
            this.mAnaloguechannel.setVisibility(0);
            this.mNumberFromTo.setVisibility(4);
        } else if (this.mActionID == null || !this.mActionID.equals(MenuConfigManager.TV_CHANNEL_SCAN)) {
            this.mNumberChannel.setVisibility(4);
            this.mTunerModeView.setVisibility(4);
            this.mNumberFromTo.setVisibility(4);
        } else {
            this.mAnaloguechannel.setVisibility(4);
        }
        this.mNumberChannel.setText(this.mContext.getString(R.string.menu_setup_channel_scan_dig));
        this.mAnaloguechannel.setText(this.mContext.getString(R.string.menu_setup_channel_scan_ana));
        this.mTunerMode = this.mTv.getCurrentTunerMode();
        if (this.mTunerMode >= 2) {
            this.mAnaloguechannel.setText("");
            this.mNumberChannel.setText("");
        }
        if (!MarketRegionInfo.isFunctionSupport(16)) {
            return;
        }
        if (this.mTv.isCurrentSourceATV()) {
            this.mNumberChannel.setText("");
        } else if (this.mTv.isCurrentSourceDTV()) {
            this.mAnaloguechannel.setText("");
        }
    }

    private int checkScanMode() {
        int scanmode = this.mTv.getUSRangeScanMode();
        MtkLog.d(TAG, "checkScanMode:" + scanmode);
        if (scanmode == 0) {
            this.mAnaloguechannel.setVisibility(0);
            this.mNumberChannel.setVisibility(0);
        } else if (scanmode == 1) {
            this.mAnaloguechannel.setVisibility(0);
            this.mNumberChannel.setVisibility(4);
        } else {
            this.mAnaloguechannel.setVisibility(4);
            this.mNumberChannel.setVisibility(0);
        }
        return scanmode;
    }

    /* access modifiers changed from: private */
    public void setScanProgress(int progress) {
        TextView textView = this.mFinishpercentage;
        textView.setText(progress + "%");
        this.progressBar.setProgress(progress);
    }

    public void cancelScan() {
        this.mTv.cancelScan();
        if (CommonIntegration.isEURegion() && this.mItemID != null && (this.mItemID.equals(MenuConfigManager.FAV_US_RANGE_FROM_CHANNEL) || this.mItemID.equals(MenuConfigManager.FAV_US_RANGE_TO_CHANNEL))) {
            this.mTv.forOnlyEUdvbtCancescan();
        }
        this.isScanning = false;
        this.loading.stopDraw();
        this.nowProgress = 0;
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_tv_analog_manual_scan_cancel));
    }

    private void facRangeScan(String itemID, String actionId) {
        this.mTv.addScanCallBackListener(this.mHandler);
        this.mTv.setScanListener((ScannerListener) null);
        this.mLastChannelID = this.editChannel.getCurrentChannelId();
        this.mScanType = FAV_RANGE_RF_CHANNEL;
        this.mItemID = itemID;
        MtkLog.i(TAG, "addScanCallBackListener mLastChannelID:" + this.mLastChannelID + ">>" + this.mItemID);
        if (!actionId.startsWith(MenuConfigManager.FACTORY_TV_RANGE_SCAN)) {
            return;
        }
        if (CommonIntegration.isUSRegion()) {
            if (checkScanMode() == 0) {
                this.mCanCompleteScan = false;
            } else {
                this.mCanCompleteScan = true;
            }
            this.mTv.startUsRangeScan(this.freFrom, this.freTo);
        } else if (CommonIntegration.isSARegion()) {
            this.mAddedChannelCount = -1;
            TextView textView = this.mNumberFromTo;
            textView.setText(this.freFrom + "/" + this.freTo);
            this.mTv.startSaRangleScan(this.freFrom, this.freTo, actionId);
        } else if (CommonIntegration.isEURegion()) {
            this.mAddedChannelCount = -1;
            this.mTv.startEURangleScan(this.freFrom, this.freTo);
        }
    }

    private void singleRFChannelScan(int rfchannel, String itemID) {
        this.mTv.stopTimeShift();
        MtkLog.d(TAG, "stopTimeShift!!!!!!");
        this.mTv.addScanCallBackListener(this.mHandler);
        this.mTv.setScanListener((ScannerListener) null);
        MtkLog.i(TAG, "addScanCallBackListene itemID:" + itemID + ">>>" + rfchannel);
        this.mLastChannelID = this.editChannel.getCurrentChannelId();
        this.mScanType = FAV_US_SINGLE_RF_CHANNEL;
        if (itemID.equals(MenuConfigManager.FAV_US_SINGLE_RF_CHANNEL) || itemID.equals(MenuConfigManager.FREQUENEY_PLAN) || itemID.equals(MenuConfigManager.TV_SINGLE_SCAN_MODULATION)) {
            this.mAnaloguechannel.setVisibility(4);
            this.mNumberChannel.setVisibility(0);
            this.mNumberChannel.setText(this.mContext.getString(R.string.menu_setup_channel_scan_dig));
            setScanProgress(0);
            this.mCanCompleteScan = true;
            this.mTv.startUsSaSingleScan(rfchannel);
        }
    }

    /* access modifiers changed from: private */
    public void startFacRangeScan(String itemID, String ActionId) {
        this.mHandler.removeMessages(103);
        this.isScanning = true;
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        this.mFinishpercentage.setText("0%");
        setScanProgress(0);
        this.loading.setVisibility(0);
        this.loading.drawLoading();
        if (this.freFrom > this.freTo) {
            int from = this.freFrom;
            int to = this.freTo;
            this.mFromChannel = (TextView) this.mListView.getChildAt(0).findViewById(R.id.factor_num);
            this.mToChannel = (TextView) this.mListView.getChildAt(1).findViewById(R.id.factor_num);
            this.mFromChannel.setText(this.allRFChannels[to]);
            this.mToChannel.setText(this.allRFChannels[from]);
            this.freFrom = to;
            this.freTo = from;
        }
        facRangeScan(itemID, ActionId);
    }

    /* access modifiers changed from: private */
    public void startFacSingleScan(String itemID) {
        if (isSelectedChannel) {
            MtkLog.d("yiqinghuang", "startFacSingleScan" + isSelectedChannel);
            isSelectedChannel = false;
            this.mHandler.removeMessages(103);
            this.isScanning = true;
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
            this.loading.setVisibility(0);
            this.loading.drawLoading();
            singleRFChannelScan(this.freFrom, itemID);
        }
    }

    /* access modifiers changed from: private */
    public void startAnalogScan(boolean scanUp, String freq) {
        MtkLog.d(TAG, "startAnalogScan()");
        this.isScanning = true;
        this.mATVFreq = 0;
        prepareUIBeforeScan();
        this.mAnaloguechannel.setVisibility(4);
        this.mNumberChannel.setVisibility(4);
        this.mNumberFromTo.setVisibility(0);
        this.mNumberFromTo.setText(this.mContext.getString(R.string.menu_tv_freq));
        ScanParams params = prepareScanUpDownParam(scanUp, freq);
        this.loading.setVisibility(0);
        this.loading.drawLoading();
        if (scanUp) {
            this.mAnaloguechannel.setText(this.mContext.getString(R.string.menu_c_analogscan_scanup));
            setScanProgress(0);
            this.mTv.getScanManager().startScan(6, this.mScanListener, params);
            return;
        }
        this.mAnaloguechannel.setText(this.mContext.getString(R.string.menu_c_analogscan_scandown));
        setScanProgress(0);
        this.mTv.getScanManager().startScan(7, this.mScanListener, params);
    }

    /* access modifiers changed from: private */
    public void startAnalogATVFreqRangeScan(int startFreq, int endFreq) {
        if (startFreq == endFreq) {
            Toast.makeText(this.mContext, "Start Frenqency is same as End Frequency.Please Change.", 0).show();
            return;
        }
        MtkLog.d(TAG, "startAnalogATVFreqRangeScan()");
        this.isScanning = true;
        this.mNumberChannel.setVisibility(4);
        this.mNumberFromTo.setVisibility(4);
        this.mAnaloguechannel.setVisibility(0);
        this.mAnaloguechannel.setText(getString(R.string.menu_setup_channel_scan_ana));
        ScanParams params = prepareATVFreqRangeScanParam(startFreq, endFreq);
        setScanProgress(0);
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        this.loading.drawLoading();
        this.mTv.getScanManager().startScan(12, this.mScanListener, params);
    }

    /* access modifiers changed from: private */
    public void startSingleRFScan(int rfChannel) {
        this.isScanning = true;
        MtkLog.d("ScanViewActivity_EU", "startSingleRFScan()");
        prepareUIBeforeScan();
        this.mTv.startSingleRFScan(rfChannel, this.mScanListener);
    }

    /* access modifiers changed from: private */
    public void startDVBCSingleRFScan() {
        this.isScanning = true;
        MtkLog.d(TAG, "startDVBCSingleRFScan()");
        prepareUIBeforeScan();
        this.mTv.startScanByScanManager(3, this.mScanListener, prepareSingleRFScanParam());
    }

    /* access modifiers changed from: private */
    public void startDVBCCNQuickFullScan() {
        MtkLog.d(TAG, "startDVBCCNQuickScan()");
        this.isScanning = true;
        ScanParams params = new ScanParams();
        params.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        try {
            params = prepareDVBCScanParam();
            if (params.freq > 0) {
                params.freq *= 1000000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        prepareUIBeforeScan();
        this.mTv.startScanByScanManager(1, this.mScanListener, params);
    }

    /* access modifiers changed from: private */
    public void startDVBCFullScan() {
        this.isScanning = true;
        ScanParams params = new ScanParams();
        params.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        params.freq = 306000;
        params.networkID = 999;
        try {
            MtkLog.d(TAG, "-----------------------");
            params = prepareDVBCScanParam();
            if (params.freq != -3) {
                if (params.networkID != -3) {
                    MtkLog.d(TAG, "startDVBCFullScan()");
                    prepareUIBeforeScan();
                    if (MarketRegionInfo.isFunctionSupport(16)) {
                        if (this.mTv.isCurrentSourceATV()) {
                            this.mAnaloguechannel.setVisibility(0);
                            this.mNumberChannel.setVisibility(8);
                        } else if (this.mTv.isCurrentSourceDTV()) {
                            this.mAnaloguechannel.setVisibility(8);
                            this.mNumberChannel.setVisibility(0);
                        }
                    } else if (params.dvbcScanType == ScanParams.Dvbc_scan_type.DIGITAL) {
                        this.mAnaloguechannel.setVisibility(8);
                        this.mNumberChannel.setVisibility(0);
                    } else {
                        this.mAnaloguechannel.setVisibility(0);
                        this.mNumberChannel.setVisibility(0);
                    }
                    String analogNum = this.mContext.getString(R.string.menu_tv_analog_channels);
                    TextView textView = this.mAnaloguechannel;
                    textView.setText(String.format("%s%3d", new Object[]{analogNum + ":", 0}));
                    if (MarketRegionInfo.isFunctionSupport(16)) {
                        EditChannel.getInstance(this.mContext).cleanChannelList();
                        if (this.mTv.isCurrentSourceATV()) {
                            MtkLog.d(TAG, "startATV_SCAN!!");
                            this.mTv.startScanByScanManager(2, this.mScanListener, params);
                            return;
                        } else if (this.mTv.isCurrentSourceDTV()) {
                            MtkLog.d(TAG, "startDTV_SCAN!!");
                            this.mTv.startScanByScanManager(1, this.mScanListener, params);
                            return;
                        } else {
                            return;
                        }
                    } else if (this.mTv.isUKCountry()) {
                        MtkLog.d(TAG, "uk only startDTV_SCAN!!");
                        this.mTv.startScanByScanManager(1, this.mScanListener, params);
                        return;
                    } else if (params.dvbcScanType == ScanParams.Dvbc_scan_type.DIGITAL) {
                        MtkLog.d(TAG, "DIGITAL dvbcScanType startDTV_SCAN!!!");
                        this.mTv.getScanManager().setRollbackCleanChannel();
                        this.mTv.startScanByScanManager(1, this.mScanListener, params);
                        return;
                    } else {
                        MtkLog.d(TAG, "FULL_SCAN!!");
                        this.mTv.startScanByScanManager(0, this.mScanListener, params);
                        return;
                    }
                }
            }
            MtkLog.d(TAG, "Please set frequency/networkID firstly!!!");
            this.isScanning = false;
            Toast.makeText(this, "Please set frequency/networkID firstly!", 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDVBSFullScan(int satID, int batID, String mLocationId) {
        MtkLog.d(TAG, "startDVBSFullScan(),batID:" + batID + ">>>" + satID + ">>>" + mLocationId);
        this.isScanning = true;
        prepareUIBeforeDVBSScan();
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        this.mNumberFromTo.setVisibility(4);
        this.dvbsParams = prepareDVBSParam(satID, mLocationId);
        if (batID != -1) {
            ((DVBSSettingsInfo) this.dvbsParams).BATId = batID;
        }
        String string = this.mContext.getString(R.string.menu_tv_digital_channels);
        setScanProgress(0);
        MtkLog.d(TAG, "mTv.isM7ScanMode():" + this.mTv.isM7ScanMode());
        if (this.mTv.isM7ScanMode()) {
            this.mTv.getScanManager().startScan(18, this.mScanListener, this.dvbsParams);
        } else {
            this.mTv.getScanManager().startScan(0, this.mScanListener, this.dvbsParams);
        }
    }

    /* access modifiers changed from: private */
    public void updateSatelliteName() {
        TextView textView = this.mAnaloguechannel;
        textView.setText(getString(R.string.menu_setup_satellite_name) + this.mTv.getScanManager().getFirstSatName());
        this.mNumberChannel.setText(getSatName(this.mTv.getScanManager().getFirstSatName()));
    }

    public void startDVBSFullScan(int satID, int batID, int tkgsId, String mLocationId) {
        MtkLog.d(TAG, "startDVBSFullScan(),batID:" + batID + ">>>" + satID + ">>>" + tkgsId + ">>>" + mLocationId);
        this.isScanning = true;
        prepareUIBeforeScan();
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        this.mNumberFromTo.setVisibility(4);
        this.dvbsParams = prepareDVBSParam(satID, mLocationId);
        if (batID != -1) {
            ((DVBSSettingsInfo) this.dvbsParams).BATId = batID;
        }
        if (tkgsId != -1) {
            ((DVBSSettingsInfo) this.dvbsParams).tkgsType = tkgsId;
        }
        String digitalNum = this.mContext.getString(R.string.menu_tv_digital_channels);
        setScanProgress(0);
        this.mTv.getScanManager().startScan(0, this.mScanListener, this.dvbsParams);
        this.mDVBSChannels.setVisibility(0);
        TextView textView = this.mDVBSChannels;
        textView.setText(String.format("%s%3d", new Object[]{digitalNum + ":", 0}));
        this.mAnaloguechannel.setVisibility(0);
        TextView textView2 = this.mAnaloguechannel;
        textView2.setText(getString(R.string.menu_setup_satellite_name) + this.mTv.getScanManager().getFirstSatName());
        this.mNumberChannel.setText(getSatName(this.mTv.getScanManager().getFirstSatName()));
    }

    public void reStartDVBSFullScanAfterTricolorChannelList(int i4BatID) {
        if (this.dvbsParams == null || ((DVBSSettingsInfo) this.dvbsParams).i4BatID != -1) {
            MtkLog.d(TAG, "reStartDVBSFullScanAfterTricolorChannelList error");
            return;
        }
        this.isScanning = true;
        prepareUIBeforeScan();
        this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        String digitalNum = this.mContext.getString(R.string.menu_tv_digital_channels);
        setScanProgress(0);
        MtkLog.d(TAG, "reStartDVBSFullScanAfterTricolorChannelList i4BatID:" + i4BatID);
        ((DVBSSettingsInfo) this.dvbsParams).i4BatID = i4BatID;
        this.mTv.getScanManager().startScan(0, this.mScanListener, this.dvbsParams);
        this.mDVBSChannels.setVisibility(0);
        TextView textView = this.mDVBSChannels;
        textView.setText(String.format("%s%3d", new Object[]{digitalNum + ":", 0}));
        this.mAnaloguechannel.setVisibility(0);
        TextView textView2 = this.mAnaloguechannel;
        textView2.setText(getString(R.string.menu_setup_satellite_name) + this.mTv.getScanManager().getFirstSatName());
        this.mNumberChannel.setText(getSatName(this.mTv.getScanManager().getFirstSatName()));
    }

    private ScanParams prepareDVBSParam(int satID, String mLocationId) {
        MtkLog.d(TAG, "prepareDVBSParam()>>>" + satID + ">>>" + mLocationId);
        DVBSSettingsInfo params = new DVBSSettingsInfo();
        params.context = this.mContext;
        params.getSatelliteInfo().setSatlRecId(satID);
        params.menuSelectedOP = ScanContent.getDVBSCurroperator();
        List<SatelliteInfo> mRescanSatLocalInfoList = ScanContent.getDVBSsatellites(this.mContext);
        params.mRescanSatLocalInfoList = mRescanSatLocalInfoList;
        params.mRescanSatLocalTPInfoList = ScanContent.getDVBSTransponderList(mRescanSatLocalInfoList);
        ScanFactorAdapter.ScanFactorItem item0 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(0).getTag(R.id.factor_name);
        ScanFactorAdapter.ScanFactorItem item1 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(1).getTag(R.id.factor_name);
        ScanFactorAdapter.ScanFactorItem item2 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(2).getTag(R.id.factor_name);
        String scanMode = item0.optionValues[item0.optionValue];
        String channels = item1.optionValues[item1.optionValue];
        String storeType = item2.optionValues[item2.optionValue];
        if (scanMode.equalsIgnoreCase(this.mContext.getResources().getString(R.string.dvbs_scan_mode_network))) {
            params.scanMode = 0;
        } else {
            params.scanMode = 1;
        }
        if (channels.equalsIgnoreCase(this.mContext.getResources().getString(R.string.dvbs_channel_encrypted))) {
            params.scanChannels = 0;
        } else if (channels.equalsIgnoreCase(this.mContext.getResources().getString(R.string.dvbs_channel_free))) {
            params.scanChannels = 1;
        } else {
            params.scanChannels = 2;
        }
        if (storeType.equalsIgnoreCase(this.mContext.getResources().getString(R.string.dvbs_channel_story_type_digital))) {
            params.scanStoreType = 0;
        } else if (storeType.equalsIgnoreCase(this.mContext.getResources().getString(R.string.dvbs_channel_story_type_radio))) {
            params.scanStoreType = 1;
        } else {
            params.scanStoreType = 2;
        }
        MtkLog.d(TAG, "scanMode: " + params.scanMode);
        MtkLog.d(TAG, "scanChannels: " + params.scanChannels);
        MtkLog.d(TAG, "menuSelectedOP: " + params.menuSelectedOP);
        if (this.mTv.getScanManager().isBATCountry()) {
            params.checkBATInfo = true;
        } else {
            params.checkBATInfo = false;
        }
        if (mLocationId != null && mLocationId.equals(MenuConfigManager.DVBS_SAT_UPDATE_SCAN)) {
            params.isUpdateScan = true;
            params.mIsDvbsNeedCleanChannelDB = false;
        }
        if (mLocationId != null && mLocationId.equalsIgnoreCase("Satellite Add")) {
            SatelliteInfo satInfo = ScanContent.getDVBSsatellitesBySatID(this.mContext, satID);
            if (satInfo != null) {
                params.setSatelliteInfo(satInfo);
            }
            params.mIsOnlyScanOneSatellite = true;
            params.mIsDvbsNeedCleanChannelDB = false;
        }
        return params;
    }

    private ScanParams prepareSingleRFScanParam() {
        MtkLog.d(TAG, "prepareSingleRFScanParam()");
        ScanParams params = new ScanParams();
        String frequency = "" + ((ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(0).getTag(R.id.factor_name)).inputValue;
        if (frequency.equalsIgnoreCase("")) {
            params.freq = -1;
        } else {
            params.freq = Integer.valueOf(frequency).intValue();
            if (CommonIntegration.isCNRegion()) {
                params.freq *= 1000000;
            }
        }
        MtkLog.d(TAG, "frequency: " + frequency);
        if (CommonIntegration.isCNRegion()) {
            DVBCCNScanner.selectedRFChannelFreq = params.freq;
        } else {
            DVBCScanner.selectedRFChannelFreq = params.freq * 1000;
        }
        return params;
    }

    private ScanParams prepareDVBCScanParam() {
        MtkLog.d(TAG, "prepareDVBCScanParam()");
        ScanParams params = new ScanParams();
        String scanmode = "";
        String scantype = "";
        String frequency = "";
        String networkID = "";
        int i = 0;
        while (i < this.mListView.getChildCount()) {
            try {
                ScanFactorAdapter.ScanFactorItem item = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(i).getTag(R.id.factor_name);
                if (item.id.equalsIgnoreCase(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY)) {
                    frequency = String.valueOf(item.inputValue);
                } else if (item.id.equalsIgnoreCase(MenuConfigManager.TV_DVBC_SCAN_NETWORKID)) {
                    networkID = String.valueOf(item.inputValue);
                } else if (item.id.equalsIgnoreCase(MenuConfigManager.SCAN_MODE_DVBC)) {
                    scanmode = item.value;
                } else if (item.id.equalsIgnoreCase(MenuConfigManager.TV_DVBC_SCAN_TYPE)) {
                    scantype = item.value;
                }
                i++;
            } catch (Exception e) {
            }
        }
        MtkLog.d(TAG, "scanmode: " + scanmode + "   frequency: " + frequency + "   networkID: " + networkID);
        if (scanmode.equalsIgnoreCase(this.mContext.getString(R.string.menu_arrays_Advance))) {
            params.dvbcScanMode = ScanParams.Dvbc_scan_mode.ADVANCE;
        } else if (scanmode.equalsIgnoreCase(this.mContext.getString(R.string.menu_arrays_Full))) {
            params.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        } else if (scanmode.equalsIgnoreCase(this.mContext.getString(R.string.menu_arrays_Quick))) {
            params.dvbcScanMode = ScanParams.Dvbc_scan_mode.QUICK;
        }
        if (scantype.equalsIgnoreCase(this.mContext.getString(R.string.menu_arrays_Only_Digital_Channels))) {
            params.dvbcScanType = ScanParams.Dvbc_scan_type.DIGITAL;
        } else if (scantype.equalsIgnoreCase(this.mContext.getString(R.string.menu_arrays_All_channels))) {
            params.dvbcScanType = ScanParams.Dvbc_scan_type.ALL;
        }
        if (frequency.equalsIgnoreCase("")) {
            params.freq = ScanContent.getFrequencyOperator(this.mContext, scanmode);
        } else {
            params.freq = Integer.valueOf(frequency).intValue();
        }
        if (CommonIntegration.isEURegion()) {
            if (networkID.equalsIgnoreCase("")) {
                params.networkID = ScanContent.getNetWorkIDOperator(this.mContext, scanmode);
            } else {
                params.networkID = Integer.valueOf(networkID).intValue();
            }
        }
        MtkLog.d(TAG, "scanmodeName(): " + params.dvbcScanMode.name() + " scantypeName(): " + params.dvbcScanType.name());
        return params;
    }

    private void prepareUIBeforeScan() {
        this.mHandler.removeMessages(103);
        if (this.mTv.getScanManager().getTuneMode() < 2) {
            this.mStateTextView.setText(this.mContext.getString(R.string.menu_setup_channel_scan));
        }
        this.loading.setVisibility(0);
        this.mAnaloguechannel.setVisibility(4);
        this.mNumberChannel.setVisibility(0);
        setScanProgress(0);
        this.loading.drawLoading();
        String digitalNum = this.mContext.getString(R.string.menu_tv_digital_channels);
        TextView textView = this.mNumberChannel;
        textView.setText(String.format("%s%3d", new Object[]{digitalNum + ":", 0}));
    }

    private void prepareUIBeforeDVBSScan() {
        this.mHandler.removeMessages(103);
        this.loading.setVisibility(0);
        String digitalNum = this.mContext.getString(R.string.menu_tv_digital_channels);
        this.mAnaloguechannel.setText(getString(R.string.menu_setup_satellite_name));
        this.mAnaloguechannel.setVisibility(0);
        this.mNumberChannel.setText(String.format("%s: ", new Object[]{getString(R.string.satellites)}));
        this.mNumberChannel.setVisibility(0);
        TextView textView = this.mDVBSChannels;
        textView.setText(String.format("%s%3d", new Object[]{digitalNum + ":", 0}));
        this.mDVBSChannels.setVisibility(0);
        setScanProgress(0);
        this.loading.drawLoading();
    }

    private ScanParams prepareScanUpDownParam(boolean scanUp, String frequency) {
        MtkLog.d(TAG, "prepareScanUpDownParam()");
        ScanParams params = new ScanParams();
        if (frequency.equalsIgnoreCase("")) {
            params.freq = -1;
        } else {
            params.freq = Integer.valueOf(frequency).intValue();
            if (scanUp) {
                if (this.mNeedChangeStartFrq) {
                    params.freq += 2;
                }
                if (params.freq >= 865) {
                    params.freq = 44;
                }
            } else {
                if (this.mNeedChangeStartFrq) {
                    params.freq -= 2;
                }
                if (params.freq <= 44) {
                    params.freq = EUATVScanner.FREQ_HIGH_VALUE;
                }
            }
        }
        MtkLog.d(TAG, "frequency: " + params.freq);
        return params;
    }

    private ScanParams prepareATVFreqRangeScanParam(int startFreq, int endFreq) {
        MtkLog.d(TAG, "prepareATVFreqRangeScanParam()");
        ScanParams params = new ScanParams();
        params.startfreq = startFreq;
        params.endfreq = endFreq;
        MtkLog.d(TAG, "frequency: " + startFreq + ">>>" + endFreq);
        return params;
    }

    private void handleRightForStartScan() {
        ScanFactorAdapter.ScanFactorItem item = (ScanFactorAdapter.ScanFactorItem) this.mListView.getSelectedView().getTag(R.id.factor_name);
        ScanFactorAdapter.ScanFactorItem freqItem = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(0).getTag(R.id.factor_name);
        if (item.id.equals(MenuConfigManager.TV_ANANLOG_SCAN_UP)) {
            startAnalogScan(true, "" + freqItem.inputValue);
        } else if (item.id.equals(MenuConfigManager.TV_ANANLOG_SCAN_DOWN)) {
            startAnalogScan(false, "" + freqItem.inputValue);
        } else if (item.id.equals(MenuConfigManager.TV_CHANNEL_STARTSCAN_CEC_CN) || item.id.equals(MenuConfigManager.TV_DVBC_CHANNELS_START_SCAN)) {
            if (MenuConfigManager.TV_SINGLE_RF_SCAN_CN.equals(this.mActionID) || MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN.equals(this.mActionID)) {
                startDVBCSingleRFScan();
            } else if (MenuConfigManager.TV_CHANNEL_SCAN_DVBC.equals(this.mActionID) || MenuConfigManager.TV_CHANNEL_SCAN_DVBC_OPERATOR.equals(this.mActionID)) {
                startDVBCFullScan();
            }
        } else if (this.mActionID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_SCAN) && item.id.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN)) {
            startDVBSFullScan(this.mSatID, -1, this.mLocationID);
        }
    }

    private void handleWhenLeftRight(boolean direction) {
        int now;
        ScanFactorAdapter.ScanFactorItem item = (ScanFactorAdapter.ScanFactorItem) this.mListView.getSelectedView().getTag(R.id.factor_name);
        if (!item.id.equals(MenuConfigManager.TV_ANANLOG_SCAN_UP) && !item.id.equals(MenuConfigManager.TV_ANANLOG_SCAN_DOWN)) {
            if (item.id.equals(MenuConfigManager.FAV_US_RANGE_FROM_CHANNEL)) {
                setFromToChannelNumber(item, direction, true);
            } else if (item.id.equals(MenuConfigManager.FAV_US_RANGE_TO_CHANNEL)) {
                setFromToChannelNumber(item, direction, false);
            } else if (item.id.equals(MenuConfigManager.FAV_US_SINGLE_RF_CHANNEL)) {
                int now2 = Integer.parseInt(item.value);
                if (!direction) {
                    now = now2 - 1;
                    if (now < this.mTv.getFirstScanIndex()) {
                        now = this.mTv.getLastScanIndex();
                    }
                } else {
                    now = now2 + 1;
                    if (now > this.mTv.getLastScanIndex()) {
                        now = this.mTv.getFirstScanIndex();
                    }
                }
                item.value = "" + now;
                this.freFrom = now;
                int freq = now;
                MtkLog.d(TAG, "mTv.isScanning()1>>" + this.mTv.isScanning() + "   now RF Index:" + freq);
                if (!this.mTv.isScanning()) {
                    this.editChannel.tuneUSSAFacRFSignalLevel(freq);
                }
                sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 0);
            } else if (item.isEnable && item.factorType == 1) {
                if (!direction) {
                    if (item.id.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
                        item.optionValues[item.optionValue] = this.mTv.getRFChannel(1);
                        refreshDVBTRFSignalQualityAndLevel(1000);
                    } else if (item.id.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL)) {
                        item.optionValues[item.optionValue] = this.mTv.getCNRFChannel(1);
                        refreshDVBTRFSignalQualityAndLevel(1000);
                    } else {
                        item.optionValue--;
                        if (item.optionValue < 0) {
                            item.optionValue = item.optionValues.length - 1;
                        }
                        if ((item.id.equals(MenuConfigManager.SCAN_MODE_DVBC) || item.id.equals(MenuConfigManager.TV_DVBC_SCAN_TYPE)) && item.listener != null) {
                            item.listener.onScanOptionChange(item.optionValues[item.optionValue]);
                        }
                    }
                } else if (item.id.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
                    item.optionValues[item.optionValue] = this.mTv.getRFChannel(2);
                    refreshDVBTRFSignalQualityAndLevel(1000);
                } else if (item.id.equals(MenuConfigManager.TV_SINGLE_SCAN_RF_CHANNEL)) {
                    item.optionValues[item.optionValue] = this.mTv.getCNRFChannel(2);
                    refreshDVBTRFSignalQualityAndLevel(1000);
                } else {
                    item.optionValue++;
                    if (item.optionValue > item.optionValues.length - 1) {
                        item.optionValue = 0;
                    }
                    if ((item.id.equals(MenuConfigManager.SCAN_MODE_DVBC) || item.id.equals(MenuConfigManager.TV_DVBC_SCAN_TYPE)) && item.listener != null) {
                        item.listener.onScanOptionChange(item.optionValues[item.optionValue]);
                    }
                }
                MtkLog.d(TAG, "item.id>>" + item.id + ">>" + item.optionValue);
                this.mConfigManager.setScanValue(item.id, item.optionValue);
                if (MenuConfigManager.TV_SINGLE_SCAN_MODULATION.equals(item.id) || MenuConfigManager.FREQUENEY_PLAN.equals(item.id)) {
                    refreshUSSASignalQualityAndLevel(1000);
                }
            }
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void refreshUSSASignalQualityAndLevel(long delayMills) {
        if (CommonIntegration.isUSRegion()) {
            ((ProgressBar) this.mListView.getChildAt(3).findViewById(R.id.factor_progress)).setProgress(0);
            ScanFactorAdapter.ScanFactorItem itemLevel = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(3).getTag(R.id.factor_name);
            if (itemLevel != null) {
                itemLevel.progress = 0;
            }
        } else if (CommonIntegration.isSARegion()) {
            ((ProgressBar) this.mListView.getChildAt(1).findViewById(R.id.factor_progress)).setProgress(0);
            ((ProgressBar) this.mListView.getChildAt(2).findViewById(R.id.factor_progress)).setProgress(0);
            ScanFactorAdapter.ScanFactorItem itemLevel2 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(1).getTag(R.id.factor_name);
            if (itemLevel2 != null) {
                itemLevel2.progress = 0;
            }
        }
        this.mHandler.removeMessages(MessageType.MENU_TV_RF_SCAN_REFRESH);
        this.mHandler.removeMessages(MessageType.MENU_USSA_TV_RF_SCAN_CONNECTTURN);
        this.mHandler.sendEmptyMessageDelayed(MessageType.MENU_USSA_TV_RF_SCAN_CONNECTTURN, delayMills);
    }

    private void setFromToChannelNumber(ScanFactorAdapter.ScanFactorItem item, boolean direction, boolean isFrom) {
        ScanFactorAdapter.ScanFactorItem item1;
        int now;
        ScanFactorAdapter.ScanFactorItem item2;
        int now2;
        int idx = 0;
        if (CommonIntegration.isEURegion()) {
            String fcstr = item.value;
            while (true) {
                if (idx >= this.allRFChannels.length) {
                    break;
                } else if (!fcstr.equals(this.allRFChannels[idx])) {
                    idx++;
                } else if (!direction) {
                    idx--;
                    if (isFrom) {
                        if (idx < 0) {
                            idx = this.freTo;
                        }
                    } else if (idx < this.freFrom) {
                        idx = this.allRFChannels.length - 1;
                    }
                } else {
                    MtkLog.e(TAG, "compared:" + fcstr + "|");
                    idx++;
                    if (isFrom) {
                        if (idx > this.freTo) {
                            idx = 0;
                        }
                    } else if (idx > this.allRFChannels.length - 1) {
                        idx = this.freFrom;
                    }
                }
            }
            item.value = this.allRFChannels[idx];
            if (isFrom) {
                this.freFrom = idx;
            } else {
                this.freTo = idx;
            }
        } else if (isFrom) {
            int now3 = Integer.parseInt(item.value);
            if (CommonIntegration.isUSRegion()) {
                item2 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(2).getTag(R.id.factor_name);
            } else {
                item2 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(1).getTag(R.id.factor_name);
            }
            int freqTo = Integer.parseInt(item2.value);
            if (!direction) {
                now2 = now3 - 1;
                if (now2 < this.mTv.getFirstScanIndex()) {
                    now2 = freqTo;
                }
            } else {
                now2 = now3 + 1;
                if (now2 > freqTo) {
                    now2 = this.mTv.getFirstScanIndex();
                }
            }
            item.value = "" + now2;
            this.freFrom = now2;
        } else {
            int now4 = Integer.parseInt(item.value);
            if (CommonIntegration.isUSRegion()) {
                item1 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(1).getTag(R.id.factor_name);
            } else {
                item1 = (ScanFactorAdapter.ScanFactorItem) this.mListView.getChildAt(0).getTag(R.id.factor_name);
            }
            int freqFrom = Integer.parseInt(item1.value);
            if (!direction) {
                now = now4 - 1;
                if (now < freqFrom) {
                    now = this.mTv.getLastScanIndex();
                }
            } else {
                now = now4 + 1;
                if (now > this.mTv.getLastScanIndex()) {
                    now = freqFrom;
                }
            }
            item.value = "" + now;
            this.freTo = now;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode;
        if (event.getAction() == 0 && (keyCode = event.getKeyCode()) != 4) {
            if (keyCode == 23 || keyCode == 66) {
                if (this.isScanning) {
                    return true;
                }
            } else if (this.isScanning) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /* access modifiers changed from: private */
    public void gotoEditTextAct(ScanFactorAdapter.ScanFactorItem item) {
        gotoEditTextAct(item, false);
    }

    /* access modifiers changed from: private */
    public void gotoEditTextAct(ScanFactorAdapter.ScanFactorItem item, boolean allowEmpty) {
        this.mCurrFactorItemId = item.id;
        if (!item.isEnable || item.factorType != 3) {
            MtkLog.e(TAG, "Option Item needn't go to editText Activity");
            return;
        }
        this.mNeedChangeStartFrq = false;
        Intent intent = new Intent(this.mContext, EditTextActivity.class);
        intent.putExtra("password", false);
        intent.putExtra("description", item.title);
        String showText = "" + item.inputValue;
        if (MenuConfigManager.TV_DVBC_SCAN_FREQUENCY.equals(item.id) || MenuConfigManager.TV_DVBC_SCAN_NETWORKID.equals(item.id)) {
            showText = ((TextView) this.mListView.getSelectedView().findViewById(R.id.factor_input)).getText().toString();
            MtkLog.d(TAG, "showText:" + showText + "  isnull:" + TextUtils.isEmpty(showText));
            if (!TextUtils.isEmpty(showText) && item.inputValue < item.minValue) {
                if (showText.equals(this.mContext.getString(R.string.menu_arrays_Auto))) {
                    showText = "";
                } else {
                    showText = item.minValue + "";
                }
                if (isBelgiumCableVoo()) {
                    showText = "0000";
                }
            }
        } else if (item.inputValue < item.minValue) {
            showText = item.minValue + "";
        }
        intent.putExtra("initialText", showText);
        intent.putExtra("itemId", item.id);
        intent.putExtra("isDigit", true);
        intent.putExtra(EditTextActivity.EXTRA_ALLOW_EMPTY, allowEmpty);
        if (item.id.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || item.id.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ)) {
            intent.putExtra("length", 6);
        } else if (item.id.equals(MenuConfigManager.SYM_RATE)) {
            intent.putExtra("length", 4);
        } else {
            intent.putExtra("length", 6);
        }
        startActivityForResult(intent, 35);
    }

    private boolean isBelgiumCableVoo() {
        return ScanContent.isCountryBel() && this.cableOperator.equals(this.mContext.getString(R.string.dvbc_operator_voo));
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        MtkLog.d(TAG, "onStop....");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown...." + this.isScanning);
        if (keyCode != 4) {
            if (keyCode != 186) {
                switch (keyCode) {
                    case 19:
                    case 20:
                        break;
                    case 21:
                        if (this.cableOperator == null || !this.cableOperator.equals("RCS RDS")) {
                            handleWhenLeftRight(false);
                            break;
                        }
                    case 22:
                        if (this.cableOperator == null || !this.cableOperator.equals("RCS RDS")) {
                            handleRightForStartScan();
                            handleWhenLeftRight(true);
                            break;
                        }
                    default:
                        switch (keyCode) {
                            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                MtkLog.d(TAG, "onKeyDown....red183");
                                if (this.cableOperator == null || !this.cableOperator.equals("RCS RDS")) {
                                    handleWhenLeftRight(false);
                                    break;
                                }
                            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                MtkLog.d(TAG, "onKeyDown....green184");
                                if (this.cableOperator == null || !this.cableOperator.equals("RCS RDS")) {
                                    handleWhenLeftRight(true);
                                    break;
                                }
                        }
                        break;
                }
            } else if (this.isScanning) {
                cancelScan();
                return true;
            } else {
                finish();
            }
        } else if (this.isScanning) {
            cancelScan();
            return true;
        } else {
            beforeBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void beforeBack() {
        this.mHandler.removeMessages(MessageType.MENU_TV_RF_SCAN_REFRESH);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        isSelectedChannel = true;
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            String value = data.getStringExtra(SaveValue.GLOBAL_VALUE_VALUE);
            if (this.mCurrFactorItemId.equals(MenuConfigManager.TV_CHANNEL_START_FREQUENCY) || this.mCurrFactorItemId.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || this.mCurrFactorItemId.equals(MenuConfigManager.TV_DVBC_SCAN_NETWORKID) || this.mCurrFactorItemId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ) || this.mCurrFactorItemId.equals(MenuConfigManager.SYM_RATE) || this.mCurrFactorItemId.equals(MenuConfigManager.TV_CHANNEL_END_FREQUENCY)) {
                ScanFactorAdapter.ScanFactorItem item = (ScanFactorAdapter.ScanFactorItem) this.mListView.getSelectedView().getTag(R.id.factor_name);
                MtkLog.d(TAG, "onActivityResult value=" + value + ",item.value=" + item.value);
                item.inputValue = Integer.parseInt(value);
                if ((this.mCurrFactorItemId.equals(MenuConfigManager.TV_DVBC_SCAN_FREQUENCY) || this.mCurrFactorItemId.equals(MenuConfigManager.TV_DVBC_SCAN_NETWORKID)) && item.inputValue == -1) {
                    item.value = this.mContext.getString(R.string.menu_arrays_Auto);
                } else if (item.inputValue > item.maxValue) {
                    item.inputValue = item.maxValue;
                    Toast.makeText(this.mContext, this.mContext.getString(R.string.menu_string_postal_code_toast_invalid_input), 0).show();
                } else if (item.inputValue < item.minValue) {
                    item.inputValue = item.minValue;
                    Toast.makeText(this.mContext, this.mContext.getString(R.string.menu_string_postal_code_toast_invalid_input), 0).show();
                }
                if (this.mCurrFactorItemId.equals(MenuConfigManager.DVBC_SINGLE_RF_SCAN_FREQ) && item.inputValue != -1) {
                    this.editChannel.tuneDVBCRFSignal(item.inputValue * 1000);
                    sendMessageDelayedThread(MessageType.MENU_TV_RF_SCAN_REFRESH, 1000);
                }
            }
            this.mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* access modifiers changed from: private */
    public CharSequence getSatName(String satName) {
        return String.format("%s: %4s", new Object[]{getString(R.string.satellites), String.format("%d/%d", new Object[]{Integer.valueOf(this.mTv.getScanManager().getDVBSCurrentIndex() + 1), Integer.valueOf(this.mTv.getScanManager().getDVBSTotalSatSize())})});
    }

    /* access modifiers changed from: private */
    public void refreshDVBTRFSignalQualityAndLevel(long delayMills) {
        ((ProgressBar) this.mListView.getChildAt(1).findViewById(R.id.factor_progress)).setProgress(0);
        ((ProgressBar) this.mListView.getChildAt(2).findViewById(R.id.factor_progress)).setProgress(0);
        this.mHandler.removeMessages(MessageType.MENU_TV_RF_SCAN_REFRESH);
        if (CommonIntegration.isCNRegion()) {
            this.mHandler.removeMessages(MessageType.MENU_CN_TV_RF_SCAN_CONNECTTURN);
            this.mHandler.sendEmptyMessageDelayed(MessageType.MENU_CN_TV_RF_SCAN_CONNECTTURN, delayMills);
            return;
        }
        this.mHandler.removeMessages(MessageType.MENU_DVBT_RF_SCAN_TUNESIGNAL);
        this.mHandler.sendEmptyMessageDelayed(MessageType.MENU_DVBT_RF_SCAN_TUNESIGNAL, delayMills);
    }

    /* access modifiers changed from: private */
    public void showTKGSUpdateScanDialog(int satId) {
        MtkLog.d(TAG, "showTKGSUpdateScanDialog satId>>>" + satId);
        new TKGSContinueScanConfirmDialog(this.mContext, satId).showConfirmDialog();
    }

    /* access modifiers changed from: private */
    public void showTricolorChannelListDialog() {
        MtkLog.d(TAG, "showTricolorChannelListDialog satId>>>");
        if (this.mTv.getScanManager().getOPType() == 17) {
            this.loading.stopDraw();
            new ScanThirdlyDialog(this, 3, 0).show();
        }
    }

    /* access modifiers changed from: private */
    public void showTKGSRescanServiceListView(int satId) {
        MtkLog.d(TAG, "showTKGSRescanServiceListView>>>");
        this.loading.stopDraw();
        new ScanThirdlyDialog(this, 2, satId).show();
    }

    /* access modifiers changed from: private */
    public void showDVBSNFYInfoView() {
        MtkLog.d(TAG, "showDVBSNFYInfoView>>>" + this.mTv.getScanManager().getOPType());
        if (this.mTv.getScanManager().getOPType() == 16) {
            this.loading.stopDraw();
            new ScanThirdlyDialog(this, 1, this.mSatID).show();
        }
    }

    private int getDVBCScanmodeIndex(String[] scanModes) {
        int scanMode = SaveValue.readLocalMemoryIntValue("DVBCScanMode");
        if (scanMode == -1 || scanMode > scanModes.length - 1) {
            SaveValue.setLocalMemoryValue("DVBCScanMode", scanModes.length - 1);
            MtkLog.d(TAG, "getDVBCScanmodeIndex last index");
            return scanModes.length - 1;
        }
        MtkLog.d(TAG, "getDVBCScanmodeIndex =" + scanMode);
        return scanMode;
    }

    /* access modifiers changed from: private */
    public int getIndexFromScanmodeName(String name, String[] scanModes) {
        for (int i = 0; i < scanModes.length; i++) {
            if (name.equals(scanModes[i])) {
                return i;
            }
        }
        return 0;
    }

    public void sendMessageDelayedThread(final int what, final long delayMillis) {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                int level = ScanViewActivity.this.editChannel.getSignalLevel();
                int quality = ScanViewActivity.this.editChannel.getSignalQuality();
                MtkLog.d(ScanViewActivity.TAG, "level=" + level + ",quality=" + quality);
                if (!ScanViewActivity.this.isDestroyed()) {
                    Message msg = Message.obtain();
                    msg.what = what;
                    msg.arg1 = level;
                    msg.arg2 = quality;
                    ScanViewActivity.this.mHandler.removeMessages(what);
                    if (delayMillis > 0) {
                        ScanViewActivity.this.mHandler.sendMessageDelayed(msg, delayMillis);
                    } else {
                        ScanViewActivity.this.mHandler.sendMessage(msg);
                    }
                }
            }
        });
    }
}
