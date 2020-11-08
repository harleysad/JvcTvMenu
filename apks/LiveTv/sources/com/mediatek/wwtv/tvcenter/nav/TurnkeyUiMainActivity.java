package com.mediatek.wwtv.tvcenter.nav;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.tv.TvInputInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.text.TextUtils;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.tv.dialog.PinDialogFragment;
import com.android.tv.menu.MenuOptionMain;
import com.android.tv.onboarding.SetupSourceActivity;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.twoworlds.tv.MtkTvCIBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEASBase;
import com.mediatek.twoworlds.tv.MtkTvGinga;
import com.mediatek.twoworlds.tv.MtkTvHighLevel;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvMultiView;
import com.mediatek.twoworlds.tv.MtkTvOAD;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvTimeshift;
import com.mediatek.twoworlds.tv.MtkTvUpgrade;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeDeliveryTypeBase;
import com.mediatek.wwtv.setting.base.scan.ui.ScanDialogActivity;
import com.mediatek.wwtv.setting.base.scan.ui.ScanThirdlyDialog;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.RecoveryRatings;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.view.ScheduleListDialog;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.commonview.DiagnosticDialog;
import com.mediatek.wwtv.tvcenter.commonview.TvBlockView;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGManager;
import com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity;
import com.mediatek.wwtv.tvcenter.epg.sa.AlarmMgr;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity;
import com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.util.BannerImplement;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.FocusLabelControl;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl;
import com.mediatek.wwtv.tvcenter.nav.util.OnLoadingListener;
import com.mediatek.wwtv.tvcenter.nav.util.SleepTimerTask;
import com.mediatek.wwtv.tvcenter.nav.util.TeletextImplement;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.CommonMsgDialog;
import com.mediatek.wwtv.tvcenter.nav.view.DVBT_Inactivechannel_ConfirmDialog;
import com.mediatek.wwtv.tvcenter.nav.view.EWSDialog;
import com.mediatek.wwtv.tvcenter.nav.view.FVP;
import com.mediatek.wwtv.tvcenter.nav.view.FloatView;
import com.mediatek.wwtv.tvcenter.nav.view.FocusLabel;
import com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog;
import com.mediatek.wwtv.tvcenter.nav.view.Hbbtv;
import com.mediatek.wwtv.tvcenter.nav.view.InfoBarDialog;
import com.mediatek.wwtv.tvcenter.nav.view.Mheg5;
import com.mediatek.wwtv.tvcenter.nav.view.MiscView;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowWithDialog;
import com.mediatek.wwtv.tvcenter.nav.view.TTXMain;
import com.mediatek.wwtv.tvcenter.nav.view.TwinkleView;
import com.mediatek.wwtv.tvcenter.nav.view.VgaPowerManager;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.oad.NavOADActivity;
import com.mediatek.wwtv.tvcenter.scan.DVBT_TNTHD_ConfirmDialog;
import com.mediatek.wwtv.tvcenter.scan.TKGSUserMessageDialog;
import com.mediatek.wwtv.tvcenter.search.SearchManagerHelper;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeshiftView;
import com.mediatek.wwtv.tvcenter.util.AudioFocusManager;
import com.mediatek.wwtv.tvcenter.util.Commands;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.Constants;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.NetflixUtil;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import com.mediatek.wwtv.tvcenter.util.tif.TvInputCallbackMgr;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TurnkeyUiMainActivity extends BaseActivity implements ComponentStatusListener.ICStatusListener, TvView.OnUnhandledInputEventListener, OnLoadingListener, PinDialogFragment.OnPinCheckedListener {
    public static final String LIVE_SETTING_SELECT_SOURCE = "com.mediatek.select.source";
    public static final String LIVE_SETTING_SELECT_SOURCE_NAME = "com.mediatek.select.sourcename";
    public static final String LIVE_SETTING_UPDATE_CONFLICT_SOURCELIST = "com.mediatek.update.conflict.sourcelist";
    public static final int MSG_CLOSE_SOURCE = 103;
    public static final int MSG_SELECT_CURRENT_CHANNEL = 104;
    public static final int MSG_SHOW_FAV_LIST_FULL_TOAST = 102;
    public static final int MSG_START = 100;
    public static final int MSG_START_INPUT_SETUP = 101;
    private static final int RESET_LAYOUT = 105;
    private static final String TAG = "TurnkeyUiMainActivity";
    private static final String TIMESHIFT_MODE_OFF = "com.mediatek.timeshift.mode.off";
    public static final String TURNKEY_ACTIVE_STATE = "turnkey_active_state";
    private static boolean isStartTv = true;
    private static InternalHandler mHandler;
    /* access modifiers changed from: private */
    public static TurnkeyUiMainActivity mainActivity = null;
    private static boolean showBannerInOnResume = true;
    private static final int[] startVssGreen = {22, KeyMap.KEYCODE_MTKIR_GREEN};
    private FrameLayout OriginalView;
    BroadcastReceiver addFacTvdiagnosticReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ((TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW)).stopTifTimeShift();
        }
    };
    private boolean bChangeSource = false;
    Long chupordowntime = 0L;
    BroadcastReceiver easOrCCReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "easReceiver");
            String action = intent.getAction();
            if (action.equals("com.mediatek.tv.easmsg")) {
                int compId = intent.getIntExtra("NavComponentShow", 0);
                MtkLog.v(TurnkeyUiMainActivity.TAG, "easReceiver, compId=" + compId);
                if ((251658240 & compId) == 16777216 && TurnkeyUiMainActivity.this.mNavCompsMagr != null) {
                    TurnkeyUiMainActivity.this.mNavCompsMagr.showNavComponent(compId);
                }
            } else if (action.equals("com.mediatek.tv.callcc")) {
                MtkLog.v(TurnkeyUiMainActivity.TAG, "easReceiver, callcc=");
                boolean visible = intent.getBooleanExtra("ccvisible", false);
                if (visible) {
                    MtkTvConfig.getInstance().setConfigValue("g_cc__dcs", 1);
                }
                MtkLog.v(TurnkeyUiMainActivity.TAG, "easReceiver, callcc visible is=" + visible);
                BannerImplement.getInstanceNavBannerImplement(context).setCCVisiable(visible);
            }
        }
    };
    private boolean isNeedStartSourceWithNotResume = false;
    /* access modifiers changed from: private */
    public boolean isShutdownFlow = false;
    long lastTime = 0;
    /* access modifiers changed from: private */
    public AudioFocusManager mAudioManager = null;
    private BannerView mBannerView;
    private List<Integer> mBlackListForKeyCodeToCEC = new ArrayList();
    private TvBlockView mBlockScreenForTuneView = null;
    /* access modifiers changed from: private */
    public boolean mCanChangeChannel = true;
    private Runnable mChannelDownRunnable = new Runnable() {
        public void run() {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "KeyMap.KEYCODE_MTKIR_CHDN>>>");
            if (TurnkeyUiMainActivity.this.mCommonIntegration.channelDown()) {
                boolean unused = TurnkeyUiMainActivity.this.mIsFromTK = true;
                int unused2 = TurnkeyUiMainActivity.this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_CHDN;
                return;
            }
            boolean unused3 = TurnkeyUiMainActivity.this.mCanChangeChannel = true;
        }
    };
    private Runnable mChannelPreRunnable = new Runnable() {
        public void run() {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "KeyMap.KEYCODE_MTKIR_PRECH>>>");
            if (TurnkeyUiMainActivity.this.mCommonIntegration.channelPre()) {
                boolean unused = TurnkeyUiMainActivity.this.mIsFromTK = true;
                int unused2 = TurnkeyUiMainActivity.this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_PRECH;
                return;
            }
            boolean unused3 = TurnkeyUiMainActivity.this.mCanChangeChannel = true;
        }
    };
    private Runnable mChannelUpRunnable = new Runnable() {
        public void run() {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "KeyMap.KEYCODE_MTKIR_CHUP>>>");
            if (TurnkeyUiMainActivity.this.mCommonIntegration.channelUp()) {
                boolean unused = TurnkeyUiMainActivity.this.mIsFromTK = true;
                int unused2 = TurnkeyUiMainActivity.this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_CHUP;
                return;
            }
            boolean unused3 = TurnkeyUiMainActivity.this.mCanChangeChannel = true;
        }
    };
    /* access modifiers changed from: private */
    public CommonIntegration mCommonIntegration;
    private DVBT_TNTHD_ConfirmDialog mConfirmDialog;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public MtkTvEASBase mEas;
    MtkTvHighLevel mHighLevel = new MtkTvHighLevel();
    /* access modifiers changed from: private */
    public InputSourceManager mInputSourceManager;
    /* access modifiers changed from: private */
    public boolean mIsFromTK;
    private boolean mIsLiveTVPause = false;
    private int mKeyCursor = 0;
    /* access modifiers changed from: private */
    public KeyDispatch mKeyDispatch;
    private MtkTvUpgrade mMtkTvUpgrade;
    /* access modifiers changed from: private */
    public ComponentsManager mNavCompsMagr;
    private boolean mNeedStartBanner = true;
    private boolean mNoNeedChangeChannel;
    private boolean mNoNeedResetSource;
    private TvSurfaceView mPipView = null;
    private ProgressBar mProgressBar;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "mReceiver,the intent action = " + intent.getAction());
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                MtkLog.d(TurnkeyUiMainActivity.TAG, "reason = " + intent.getExtra("reason"));
                if ("homekey".equals(intent.getExtra("reason"))) {
                    if (1 == MarketRegionInfo.getCurrentMarketRegion()) {
                        MtkLog.i(TurnkeyUiMainActivity.TAG, "ACTION_CLOSE_SYSTEM_DIALOGS, EASSetAndroidLaunchStatus(true)");
                        TurnkeyUiMainActivity.this.mEas.EASSetAndroidLaunchStatus(true);
                    }
                    if (!TurnkeyUiMainActivity.this.isInPictureInPictureMode()) {
                        if (MarketRegionInfo.isFunctionSupport(2)) {
                            MtkTvGinga.getInstance().stopGinga();
                        }
                        if (TurnkeyUiMainActivity.this.mCommonIntegration.isPipOrPopState() && MarketRegionInfo.isFunctionSupport(26)) {
                            ((MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP)).setVisibility(4);
                        }
                        ComponentStatusListener.getInstance().updateStatus(6, 1);
                    }
                }
            } else if ("com.mediatek.ui.menu.util.sleep.timer".equals(intent.getAction())) {
                String itemId = intent.getStringExtra("itemId");
                MtkLog.d(TurnkeyUiMainActivity.TAG, "onReceive sleep action 's itemid= " + itemId);
                long mills = intent.getLongExtra("mills", 0);
                if (mills == 0) {
                    new SleepTimerTask(TurnkeyUiMainActivity.mainActivity, (Handler) null, mills);
                    SleepTimerTask.doCancelTask(itemId);
                    return;
                }
                new SleepTimerTask(TurnkeyUiMainActivity.mainActivity, (Handler) null, mills).doExec(itemId);
            } else if (intent.getAction().equals(TurnkeyUiMainActivity.TIMESHIFT_MODE_OFF)) {
                MtkLog.e(TurnkeyUiMainActivity.TAG, "===TIMESHIFT_MODE_OFF====");
                if (intent.getBooleanExtra("timeshiftmod", false)) {
                    if (SystemProperties.get("vendor.mtk.tif.timeshift").equals("1")) {
                        ((TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW)).stopTifTimeShift();
                    }
                } else if (SystemProperties.get("vendor.mtk.tif.timeshift").equals("1")) {
                    ((TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW)).stopTifTimeShift();
                }
            } else if (TurnkeyUiMainActivity.LIVE_SETTING_SELECT_SOURCE.equals(intent.getAction())) {
                if (MarketRegionInfo.isFunctionSupport(13)) {
                    String extraSourceName = intent.getStringExtra(TurnkeyUiMainActivity.LIVE_SETTING_SELECT_SOURCE_NAME);
                    MtkLog.d(TurnkeyUiMainActivity.TAG, " LIVE_SETTING_SELECT_SOURCE, extraSourceName =" + extraSourceName);
                    if (TextUtils.isEmpty(extraSourceName)) {
                        MtkLog.d(TurnkeyUiMainActivity.TAG, " LIVE_SETTING_SELECT_SOURCE, ().equals(extraSourceName)");
                        if (TurnkeyUiMainActivity.this.mInputSourceManager != null) {
                            TurnkeyUiMainActivity.this.mInputSourceManager.resetCurrentInput();
                        }
                        if (MarketRegionInfo.isFunctionSupport(26)) {
                            TurnkeyUiMainActivity.this.getPipView().setStreamVolume(0.0f);
                            TurnkeyUiMainActivity.this.getTvView().setStreamVolume(1.0f);
                        }
                    } else if (TurnkeyUiMainActivity.this.mInputSourceManager != null) {
                        if (MarketRegionInfo.isFunctionSupport(39)) {
                            TurnkeyUiMainActivity.this.mInputSourceManager.changeInputSourceByHardwareId(TurnkeyUiMainActivity.this.mInputSourceManager.getHardwareIdByOriginalSourceName(extraSourceName));
                        } else if ("ATV".equalsIgnoreCase(extraSourceName)) {
                            TurnkeyUiMainActivity.this.mInputSourceManager.tuneChannelByTIFChannelInfoForAssistant(CommonIntegration.getInstance().getFirstATVChannelList());
                        } else if ("DTV".equalsIgnoreCase(extraSourceName)) {
                            TurnkeyUiMainActivity.this.mInputSourceManager.tuneChannelByTIFChannelInfoForAssistant(CommonIntegration.getInstance().getFirstDTVChannelList());
                        } else {
                            TurnkeyUiMainActivity.this.mInputSourceManager.changeInputSourceByHardwareId(TurnkeyUiMainActivity.this.mInputSourceManager.getHardwareIdByOriginalSourceName(extraSourceName));
                        }
                        BannerView bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                        if (bannerView != null) {
                            bannerView.showSimpleBanner();
                        }
                    }
                }
            } else if (TurnkeyUiMainActivity.LIVE_SETTING_UPDATE_CONFLICT_SOURCELIST.equals(intent.getAction())) {
                MtkLog.d(TurnkeyUiMainActivity.TAG, " LIVE_SETTING_UPDATE_CONFLICT_SOURCELIST");
                if (TurnkeyUiMainActivity.this.mInputSourceManager != null) {
                    TurnkeyUiMainActivity.this.mInputSourceManager.updateConflictSourceList();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mSendKeyCode;
    private LinearLayout mSundryLayout = null;
    private TextToSpeechUtil mTTS;
    private Handler mThreadHandler;
    private TifTimeShiftManager mTifTimeShiftManager;
    private TvSurfaceView mTvView = null;
    private int mVssCursor = 0;
    private LinearLayout mainSurfaceViewLy;
    public boolean mhasMuteVideo;
    private boolean reqMhegResume = false;
    BroadcastReceiver resetOrCleanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "resetOrCleanReceiver");
            String action = intent.getAction();
            TVContent menuTV = TVContent.getInstance(context);
            if (action.equals("com.mediatek.tv.setup.resetdefault")) {
                TurnkeyUiMainActivity.setStartTv(true);
                menuTV.cleanLocalData();
            } else if (action.equals("com.mediatek.tv.factory.cleanstorage")) {
                TurnkeyUiMainActivity.setStartTv(true);
                menuTV.cleanLocalData();
            } else if (action.equals("com.mediatek.tv.parental.cleanall")) {
                TurnkeyUiMainActivity.setStartTv(true);
                menuTV.cleanLocalData();
            }
        }
    };
    BroadcastReceiver selectChannelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "selectChannelReceiver");
            String action = intent.getAction();
            if (action.equals("com.mediatek.tv.selectchannel")) {
                String uriStr = intent.getStringExtra("channelUriStr");
                String channelNum = intent.getStringExtra("channelNum");
                String channelName = intent.getStringExtra("channelName");
                if (TurnkeyUiMainActivity.this.mInputSourceManager == null) {
                    return;
                }
                if (channelNum != null || channelName != null) {
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "selectChannelReceiver() channelNum: " + channelNum + " channelName: " + channelName);
                    TIFChannelInfo info = null;
                    if (channelNum != null) {
                        info = TIFChannelManager.getInstance(TurnkeyUiMainActivity.this.getApplicationContext()).getChannelByNumOrName(channelNum, false);
                    } else if (channelName != null) {
                        info = TIFChannelManager.getInstance(TurnkeyUiMainActivity.this.getApplicationContext()).getChannelByNumOrName(channelName, true);
                    }
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "selectChannelReceiver() info: " + info);
                    TurnkeyUiMainActivity.this.mInputSourceManager.tuneChannelByTIFChannelInfoForAssistant(info);
                } else if (uriStr != null) {
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "began to get select data:" + uriStr);
                    String tvInputId = TurnkeyUiMainActivity.this.mInputSourceManager.getTvInputId(intent.getStringExtra("path"));
                    TIFChannelManager.getInstance(TurnkeyUiMainActivity.this.getApplicationContext()).selectChannelByTIFInfo(TIFChannelManager.getInstance(TurnkeyUiMainActivity.this.getApplicationContext()).getTifChannelInfoByUri(Uri.parse(uriStr)));
                }
            } else if (action.equals("com.mediatek.tv.channelupdown")) {
                boolean isUp = intent.getBooleanExtra("upOrdown", true);
                MtkLog.d(TurnkeyUiMainActivity.TAG, "change channel to up or down:" + isUp);
                ChannelListDialog mChListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
                if (mChListDialog != null) {
                    mChListDialog.channelUpDown(isUp);
                }
            } else if (action.equals("com.mediatek.tv.channelpre") && TurnkeyUiMainActivity.this.mCommonIntegration != null) {
                MtkLog.d(TurnkeyUiMainActivity.TAG, "change channel pre");
                TurnkeyUiMainActivity.this.mCommonIntegration.channelPre();
            }
        }
    };
    BroadcastReceiver selectSourceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "selectSourceReceiver");
            if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
                String action = intent.getAction();
                String sourcename = action.substring(action.lastIndexOf(".") + 1);
                MtkLog.d(TurnkeyUiMainActivity.TAG, "selectSourceReceiver,sourcename" + sourcename);
                if (TurnkeyUiMainActivity.this.mInputSourceManager != null && !sourcename.equalsIgnoreCase(TurnkeyUiMainActivity.this.mInputSourceManager.autoChangeTestGetCurrentSourceName(TurnkeyUiMainActivity.this.mCommonIntegration.getCurrentFocus()))) {
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "selectSourceReceiver,autoChangeTestSourceChange =" + sourcename);
                    TurnkeyUiMainActivity.this.mInputSourceManager.autoChangeTestSourceChange(sourcename, TurnkeyUiMainActivity.this.mCommonIntegration.getCurrentFocus());
                }
            }
        }
    };
    private LinearLayout subSurfaceViewLy;

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MtkLog.v(TAG, "onActivityResult, requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (50331648 == requestCode) {
            if (50331649 == resultCode) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.height = -1;
                lp.width = -1;
                lp.y = 0;
                lp.x = 0;
                getWindow().setAttributes(lp);
            } else if (-1 == resultCode) {
                MtkLog.v(TAG, "onActivityResult, ChannelListDialog resultCode = " + resultCode);
                ChannelListDialog mChListDialog = (ChannelListDialog) ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_CH_LIST);
                if (mChListDialog != null && mChListDialog.isVisible()) {
                    MtkLog.v(TAG, "onActivityResult, resultCode=" + resultCode);
                    mChListDialog.onActivityResult(data);
                }
            }
        }
    }

    public boolean isLiveTVPause() {
        return this.mIsLiveTVPause;
    }

    public void resetLayout() {
        resetLayout(0);
    }

    public void resetLayout(int delaytime) {
        mHandler.obtainMessage().what = 105;
        mHandler.sendEmptyMessageDelayed(105, (long) delaytime);
    }

    public void selectCurrentChannelDelay(long time) {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(104, time);
        }
    }

    public static void setStartTv(boolean isStartTv2) {
        isStartTv = isStartTv2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, " onCreate()");
        maybeFinishFocusBootFromLauncher(getIntent());
        SaveValue.setLocalMemoryValue("tkOnCreate", true);
        this.mContext = this;
        mainActivity = this;
        this.mKeyDispatch = KeyDispatch.getInstance();
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        getWindow().addFlags(128);
        getWindow().addFlags(4194304);
        getWindow().addFlags(524288);
        ScreenConstant.SCREEN_WIDTH = outSize.x;
        ScreenConstant.SCREEN_HEIGHT = outSize.y;
        this.mAudioManager = AudioFocusManager.getInstance(getApplicationContext());
        preInit();
        init();
        if ((SaveValue.readLocalMemoryIntValue("mPreConfigsFlags") & 1408) == 0) {
            MtkLog.d(TAG, " onCreate, mPreConfigsFlags=" + SaveValue.readLocalMemoryIntValue("mPreConfigsFlags"));
            receiveIntent(getIntent());
        }
        this.reqMhegResume = false;
        CommonIntegration.getInstance().setContext(this);
        this.mMtkTvUpgrade = MtkTvUpgrade.getInstance();
        this.mInputSourceManager = TvSingletons.getSingletons().getInputSourceManager();
        if (1 == MarketRegionInfo.getCurrentMarketRegion()) {
            toastResultOfFirmwareUpgrade();
        }
        if (DataSeparaterUtil.getInstance() != null) {
            this.mBlackListForKeyCodeToCEC = DataSeparaterUtil.getInstance().getBlacklistKeycodeToCec();
        }
        MtkLog.d(TAG, " onCreate() end ~");
    }

    private void maybeFinishFocusBootFromLauncher(Intent intent) {
        String action = intent.getAction();
        MtkLog.d(TAG, "maybeFinishFocusBootFromLauncher action:" + action);
        if ("android.mtk.intent.action.ACTION_REQUEST_TOP_RESUME".equals(action)) {
            if (intent.getBooleanExtra("FORCE_LAUNCH_ON_BOOT", false)) {
                NetflixUtil.checkNetflixKeyWhenForceLaunchonBoot(this);
            }
        } else if ("com.android.tv.action.FORCE_LAUNCH_ON_BOOT".equals(action)) {
            NetflixUtil.checkNetflixKeyWhenForceLaunchonBoot(this);
        }
    }

    private void initConfig(boolean bStartTv) {
        boolean z = bStartTv;
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                if (MtkTvConfig.getInstance().getConfigValue("g_misc__bruning_mode") == 0) {
                    TurnkeyUiMainActivity.this.mHighLevel.setBroadcastUiVisibility(false);
                } else {
                    TurnkeyUiMainActivity.this.mHighLevel.setBroadcastUiVisibility(true);
                }
                if (1 == MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE)) {
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "come in set CFG_MISC_AV_COND_MMP_MODE, 0");
                    MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE, 0);
                }
                if ((ComponentsManager.getActiveCompId() & NavBasic.NAV_NATIVE_COMP_ID_BASIC) == 0) {
                    TurnkeyUiMainActivity.this.mHighLevel.launchInternalApp(2);
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "initConfig, launchInternalApp");
                }
            }
        });
    }

    private void toastResultOfFirmwareUpgrade() {
        Toast firmwareUpgradeToast = null;
        switch (this.mMtkTvUpgrade.queryUpgradeResult(MtkTvUpgradeDeliveryTypeBase.INTERNET)) {
            case 1:
                firmwareUpgradeToast = Toast.makeText(this, getResources().getString(R.string.download_firmware_upgrade_successfully), 1);
                break;
            case 2:
                firmwareUpgradeToast = Toast.makeText(this, getResources().getString(R.string.download_firmware_upgrade_unsuccessfully), 1);
                break;
        }
        if (firmwareUpgradeToast != null) {
            firmwareUpgradeToast.setGravity(17, 0, 0);
            firmwareUpgradeToast.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mNoNeedChangeChannel = false;
        receiveIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        MtkLog.d(TAG, " onStart()");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages((Object) null);
            TvCallbackHandler.getInstance().removeCallBackListener(mHandler);
            mHandler = null;
        }
        MtkLog.d(TAG, "onStart()" + DestroyApp.getRunningActivity());
        mHandler = new InternalHandler(this);
        TvCallbackHandler.getInstance().addCallBackListener(mHandler);
        this.isNeedStartSourceWithNotResume = true;
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                Commands.startCmd();
                if (1 == MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE)) {
                    MtkLog.d(TurnkeyUiMainActivity.TAG, "come in set CFG_MISC_AV_COND_MMP_MODE, 0");
                    MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE, 0);
                }
            }
        });
        CommonIntegration.resetBlueMuteForLiveTv(this);
        SaveValue.getInstance(this.mContext).saveBooleanValue(TURNKEY_ACTIVE_STATE, true);
        super.onStart();
        this.mAudioManager.createAudioTrack();
        this.mAudioManager.createMediaSession(this);
        MtkLog.v(TAG, "onResume isShutdownFlow=" + this.isShutdownFlow);
        if (this.isShutdownFlow) {
            checkNetflixResume();
        } else if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
            postInit(false);
        } else {
            DvrManager.getInstance().setState((StateBase) StateDvrPlayback.getInstance());
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.isNeedStartSourceWithNotResume = false;
        MtkLog.d(TAG, " onResume()");
        this.mIsLiveTVPause = false;
        super.onResume();
        if (this.mInputSourceManager != null) {
            DvrManager.getInstance().setStopDvrNotResumeLauncher(false);
            this.mInputSourceManager.resetScreenOffFlag();
            this.mInputSourceManager.setShutDownFlag(false);
        }
        TIFChannelManager.getInstance(this).handleUpdateChannels();
        Boolean isInPictureInPicureMode = Boolean.valueOf(SaveValue.getInstance(this.mContext).readBooleanValue("isInPictureInPicureMode"));
        if (!isInPictureInPictureMode() && isInPictureInPicureMode.booleanValue()) {
            ComponentStatusListener.getInstance().updateStatus(15, 0);
        }
        if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
            if (2 == MarketRegionInfo.getCurrentMarketRegion()) {
                MtkTvGinga.getInstance().startGinga();
                AlarmMgr.getInstance(getApplicationContext()).startTimer(false);
            }
            this.mNavCompsMagr = ComponentsManager.getInstance();
            if (this.reqMhegResume) {
                this.reqMhegResume = false;
                ComponentsManager.updateActiveCompId(true, 33554433);
            }
            MtkLog.v(TAG, "onResume isShutdownFlow=" + this.isShutdownFlow);
            if (!this.isShutdownFlow) {
                MtkLog.d(TAG, " onResume()-3");
                if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
                    postInit(false);
                } else {
                    DvrManager.getInstance().setState((StateBase) StateDvrPlayback.getInstance());
                }
            }
            showBannerInOnResume = true;
            if (!SaveValue.readLocalMemoryBooleanValue("isFirstStart")) {
                MtkLog.d(TAG, "when resume recovery ratings");
                RecoveryRatings.recoveryFromTVAPI(mainActivity);
                SaveValue.setLocalMemoryValue("isFirstStart", true);
            }
            if (SaveValue.readLocalMemoryBooleanValue("tkOnCreate")) {
                SaveValue.setLocalMemoryValue("tkOnCreate", false);
            }
            if (CommonIntegration.isEURegion() && this.mCommonIntegration.getSvl() == 4) {
                int OperatorName = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
                MtkLog.d(TAG, "OperatorName==" + OperatorName);
                if (OperatorName == 27) {
                    this.mThreadHandler.postDelayed(new Runnable() {
                        public void run() {
                            int ret = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_DVBS_TKGS_CHLST_UPDATE);
                            StringBuilder sb = new StringBuilder();
                            sb.append("is need to show dialog==");
                            sb.append(ret == 1);
                            MtkLog.d(TurnkeyUiMainActivity.TAG, sb.toString());
                            if (ret == 1) {
                                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_DVBS_TKGS_CHLST_UPDATE, 0);
                                String userMsg = new MtkTvScanDvbsBase().dvbsGetTKGSUserMessage();
                                MtkLog.d(TurnkeyUiMainActivity.TAG, "is need to show dialog userMsg" + userMsg);
                                TurnkeyUiMainActivity.this.showDVBS_TKGS_UserMsgDialog(userMsg);
                            }
                        }
                    }, MessageType.delayMillis4);
                }
            }
            if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
                StateDvr.setTkActive(true);
                StateDvr.getInstance().showCtrBar();
            }
            String isStartUp = SystemProperties.get("sys.xuss.checkcon", getString(R.string.n_first_start));
            if (TVContent.getInstance(getApplicationContext()).isItaCountry() && "".equals("auto") && isStartUp.equals("n_first_start")) {
                SystemProperties.set("sys.xuss.checkcon", "more_start");
                MtkTvScanDvbtBase.LcnConflictGroup[] lcnList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLcnConflictGroup();
                if (lcnList != null && lcnList.length > 0) {
                    new ScanThirdlyDialog(this.mContext, 3).show();
                }
            }
            this.mAudioManager.setMediaPlaybackPlaying();
            return;
        }
        DvrManager.getInstance().setState((StateBase) StateDvrPlayback.getInstance());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MtkLog.d(TAG, "onConfigurationChanged language updated.");
        init();
        ComponentStatusListener.getInstance().updateStatus(20, 0);
    }

    public static void setShowBannerInOnResume(boolean isSet) {
        showBannerInOnResume = isSet;
    }

    public void setChangeSource(boolean isChange) {
        this.bChangeSource = isChange;
    }

    public boolean getChangeSource() {
        return this.bChangeSource;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        MtkLog.d(TAG, " onPause()");
        super.onPause();
        this.mIsLiveTVPause = true;
        this.mNavCompsMagr.hideAllComponents();
        MtkLog.v(TAG, "onPause isShutdownFlow=" + this.isShutdownFlow);
        if (this.isShutdownFlow) {
            isStartTv = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        MtkLog.d(TAG, " onStop()");
        this.mNoNeedChangeChannel = false;
        this.mNeedStartBanner = false;
        isStartTv = true;
        getTvView().reset();
        if (!mHandler.hasMessages(103)) {
            Message msg = mHandler.obtainMessage();
            msg.what = 103;
            mHandler.sendMessage(msg);
        }
        CommonIntegration commonIntegration = this.mCommonIntegration;
        CommonIntegration.resetBlueMuteFor3rd(getApplicationContext());
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
            StateDvr.setTkActive(false);
            StateDvr.getInstance().clearWindow(true);
            StateDvr.getInstance().recoveryView();
        }
        if (StateDvrFileList.getInstance() != null) {
            StateDvrFileList.getInstance().recoveryView();
        }
        if (isInPictureInPictureMode()) {
            finish();
        }
        SaveValue.getInstance(this.mContext).saveBooleanValue(TURNKEY_ACTIVE_STATE, false);
        this.mAudioManager.releaseAudioTrackAndMediaSession();
    }

    public void finish() {
        super.finish();
        MtkLog.e(TAG, "finish()");
        if (MarketRegionInfo.isFunctionSupport(30)) {
            DvrManager.getInstance(this).uiManager.hiddenAllViews();
        }
    }

    public void onDestroy() {
        MtkLog.d(TAG, " onDestroy()");
        SaveValue.setLocalMemoryValue("mPreConfigsFlags", getChangingConfigurations());
        if (this.mThreadHandler != null) {
            this.mThreadHandler.removeCallbacks(this.mChannelUpRunnable);
            this.mThreadHandler.removeCallbacks(this.mChannelDownRunnable);
            this.mThreadHandler.removeCallbacks(this.mChannelPreRunnable);
            this.mChannelUpRunnable = null;
            this.mChannelDownRunnable = null;
            this.mChannelPreRunnable = null;
            this.mThreadHandler = null;
        }
        TvCallbackHandler.getInstance().removeCallBackListener(mHandler);
        unregisterTvReceiver();
        this.mNavCompsMagr.deinitComponents();
        this.mNavCompsMagr.clear();
        ComponentStatusListener.getInstance().removeAll();
        AlarmMgr.getInstance(getApplicationContext()).cancelTimer();
        isStartTv = true;
        super.onDestroy();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.i(TAG, "keyCode:" + keyCode + ",onKeyUpmKeyCursor=" + this.mKeyCursor);
        if (this.mKeyDispatch.isLongPressed()) {
            this.mKeyDispatch.passKeyToNative(1, keyCode, event);
        }
        if (event.getKeyCode() == 84) {
            SearchManagerHelper.getInstance(this).launchAssistAction();
            return true;
        }
        if (!(keyCode == 4 || keyCode == 171)) {
            switch (keyCode) {
                case 24:
                case 25:
                    break;
                default:
                    return super.onKeyUp(keyCode, event);
            }
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.i(TAG, "keyCode:" + keyCode + ",onKeyDownmKeyCursor=" + this.mKeyCursor);
        if (this.mCanChangeChannel) {
            this.mIsFromTK = false;
        }
        int keyCode2 = KeyMap.getKeyCode(event);
        if (isMapSerailKeys(keyCode2)) {
            MtkLog.i(TAG, "isMapSerailKeys(keyCode)");
            return true;
        }
        KeyHandler(keyCode2, event, false);
        switch (keyCode2) {
            case 126:
            case 127:
                MtkLog.i(TAG, "Return ture to intercept event.");
                return true;
            default:
                return super.onKeyDown(keyCode2, event);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private boolean isNormalTvState() {
        boolean isNormal = true;
        if (ComponentsManager.getActiveCompId() == 16777233 || (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing())) {
            MtkLog.i(TAG, "NAV_COMP_ID_TELETEXT OR StateFileList");
            return false;
        }
        if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
            MtkLog.i(TAG, "MtkTvPWDDialog.getInstance().PWDShow() == 0");
            isNormal = false;
        }
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            return false;
        }
        if (!isNormal || this.mCommonIntegration.isTVNormalState()) {
            return isNormal;
        }
        MtkLog.i(TAG, "CommonIntegration.getInstance().isTVNormalState()");
        return false;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x016d, code lost:
        if (r0.mCommonIntegration.isCurrentSourceTv() == false) goto L_0x025a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x01d8, code lost:
        if (r0.mCommonIntegration.isCurrentSourceTv() == false) goto L_0x025a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean KeyHandler(int r19, android.view.KeyEvent r20, boolean r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = r20
            r2 = r21
            r3 = 0
            r4 = 0
            java.lang.String r5 = "TurnkeyUiMainActivity"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "KeyHandler:("
            r6.append(r7)
            r7 = r19
            r6.append(r7)
            java.lang.String r8 = ","
            r6.append(r8)
            r6.append(r2)
            java.lang.String r8 = ")\n"
            r6.append(r8)
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r8 = r0.mNavCompsMagr
            java.lang.String r8 = r8.toString()
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.i(r5, r6)
            r18.onHideLoading()
            int r5 = com.mediatek.wwtv.tvcenter.util.KeyMap.getKeyCode(r19, r20)
            r6 = 30
            boolean r7 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r6)
            if (r7 == 0) goto L_0x0058
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r7 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
            boolean r7 = r7.pvrIsRecording()
            if (r7 != 0) goto L_0x0058
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r7 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
            com.mediatek.wwtv.tvcenter.dvr.controller.UImanager r7 = r7.uiManager
            r7.hiddenAllViews()
        L_0x0058:
            r7 = 1
            if (r1 == 0) goto L_0x00d5
            int r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getActiveCompId()
            java.lang.String r8 = "TurnkeyUiMainActivity"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "step 1. check active component: "
            r9.append(r10)
            r9.append(r4)
            java.lang.String r9 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            r8 = 33554433(0x2000001, float:9.403956E-38)
            if (r4 != r8) goto L_0x0080
            r9 = 172(0xac, float:2.41E-43)
            if (r5 != r9) goto L_0x0080
            r0.reqMhegResume = r7
        L_0x0080:
            r9 = 33554432(0x2000000, float:9.403955E-38)
            r9 = r9 & r4
            if (r9 == 0) goto L_0x00aa
            java.lang.String r9 = "TurnkeyUiMainActivity"
            java.lang.String r10 = "native component active"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            r9 = 13
            boolean r9 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r9)
            if (r9 == 0) goto L_0x00a1
            if (r4 != r8) goto L_0x00a1
            r8 = 171(0xab, float:2.4E-43)
            if (r5 != r8) goto L_0x00a1
            com.mediatek.wwtv.tvcenter.nav.util.PIPPOPSurfaceViewControl r8 = com.mediatek.wwtv.tvcenter.nav.util.PIPPOPSurfaceViewControl.getSurfaceViewControlInstance()
            r8.changeOutputWithTVState(r7)
        L_0x00a1:
            com.mediatek.wwtv.tvcenter.util.KeyDispatch r8 = r0.mKeyDispatch
            boolean r8 = r8.passKeyToNative(r5, r1)
            if (r8 != r7) goto L_0x00be
            return r7
        L_0x00aa:
            r8 = 16777216(0x1000000, float:2.3509887E-38)
            r8 = r8 & r4
            if (r8 == 0) goto L_0x00be
            if (r2 != 0) goto L_0x00be
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r8 = r0.mNavCompsMagr
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r8 = r8.getComponentById(r4)
            boolean r8 = r8.KeyHandler(r5, r1)
            if (r8 == 0) goto L_0x00be
            return r7
        L_0x00be:
            java.lang.String r8 = "TurnkeyUiMainActivity"
            java.lang.String r9 = "android component active"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            if (r2 == 0) goto L_0x00ce
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r8 = r0.mNavCompsMagr
            boolean r3 = r8.dispatchKeyToActiveComponent(r5, r1)
            goto L_0x00e2
        L_0x00ce:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r8 = r0.mNavCompsMagr
            boolean r3 = r8.dispatchKeyToActiveComponent(r5, r1, r2)
            goto L_0x00e2
        L_0x00d5:
            java.lang.String r8 = "TurnkeyUiMainActivity"
            java.lang.String r9 = "step 2. contains android active component"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r8 = r0.mNavCompsMagr
            boolean r3 = r8.dispatchKeyToActiveComponent(r5, r1, r2)
        L_0x00e2:
            if (r3 == 0) goto L_0x00e5
            return r7
        L_0x00e5:
            java.lang.String r8 = "TurnkeyUiMainActivity"
            java.lang.String r9 = "step 3. the active component do not handle the key, call isKeyHandler"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r8 = r0.mNavCompsMagr
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r8 = r8.showNavComponent(r5, r1)
            if (r8 == 0) goto L_0x00f5
            return r7
        L_0x00f5:
            java.lang.String r8 = "TurnkeyUiMainActivity"
            java.lang.String r9 = "step 4. default handled"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            int r5 = com.mediatek.wwtv.tvcenter.util.KeyMap.getKeyCode(r5, r1)
            r8 = 1000(0x3e8, double:4.94E-321)
            r10 = 6000(0x1770, double:2.9644E-320)
            r12 = 0
            r13 = 4104(0x1008, float:5.751E-42)
            switch(r5) {
                case 4: goto L_0x023a;
                case 19: goto L_0x01d2;
                case 20: goto L_0x0167;
                case 82: goto L_0x0165;
                case 86: goto L_0x0163;
                case 166: goto L_0x01dc;
                case 167: goto L_0x0171;
                case 172: goto L_0x0156;
                case 184: goto L_0x0154;
                case 185: goto L_0x0141;
                case 229: goto L_0x010c;
                default: goto L_0x010a;
            }
        L_0x010a:
            goto L_0x025a
        L_0x010c:
            boolean r6 = r0.mCanChangeChannel
            if (r6 == 0) goto L_0x025a
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r6 = r0.mNavCompsMagr
            r8 = 16777230(0x100000e, float:2.3509926E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r6 = r6.getComponentById(r8)
            com.mediatek.wwtv.tvcenter.nav.view.SourceListView r6 = (com.mediatek.wwtv.tvcenter.nav.view.SourceListView) r6
            boolean r6 = r6.isShowing()
            if (r6 == 0) goto L_0x012c
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r6 = r0.mNavCompsMagr
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r6 = r6.getComponentById(r8)
            com.mediatek.wwtv.tvcenter.nav.view.SourceListView r6 = (com.mediatek.wwtv.tvcenter.nav.view.SourceListView) r6
            r6.dismiss()
        L_0x012c:
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler r6 = mHandler
            r6.removeMessages(r13)
            r0.mCanChangeChannel = r12
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler r6 = mHandler
            r6.sendEmptyMessageDelayed(r13, r10)
            android.os.Handler r6 = r0.mThreadHandler
            java.lang.Runnable r8 = r0.mChannelPreRunnable
            r6.post(r8)
            goto L_0x025a
        L_0x0141:
            int r6 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.getCurrentMarketRegion()
            if (r6 == 0) goto L_0x0149
            goto L_0x025a
        L_0x0149:
            android.content.Intent r6 = new android.content.Intent
            java.lang.Class<com.mediatek.wwtv.tvcenter.capturelogo.CaptureLogoActivity> r8 = com.mediatek.wwtv.tvcenter.capturelogo.CaptureLogoActivity.class
            r6.<init>(r0, r8)
            r0.startActivity(r6)
            goto L_0x0165
        L_0x0154:
            goto L_0x025a
        L_0x0156:
            if (r1 == 0) goto L_0x015e
            int r6 = r20.getRepeatCount()
            if (r6 > 0) goto L_0x025a
        L_0x015e:
            r18.showEPG()
            goto L_0x025a
        L_0x0163:
            goto L_0x025a
        L_0x0165:
            goto L_0x025a
        L_0x0167:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r0.mCommonIntegration
            boolean r6 = r6.isCurrentSourceTv()
            if (r6 != 0) goto L_0x0171
            goto L_0x025a
        L_0x0171:
            com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil r6 = com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil.getInstance()
            if (r6 == 0) goto L_0x018b
            com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil r6 = com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil.getInstance()
            boolean r6 = r6.isCHUPDOWNACTIONSupport()
            if (r6 != 0) goto L_0x018b
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r0.mCommonIntegration
            boolean r6 = r6.isCurrentSourceTv()
            if (r6 != 0) goto L_0x018b
            goto L_0x025a
        L_0x018b:
            com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil r6 = r0.mTTS
            boolean r6 = r6.isTTSEnabled()
            if (r6 == 0) goto L_0x01af
            long r14 = java.lang.System.currentTimeMillis()
            java.lang.Long r6 = r0.chupordowntime
            long r16 = r6.longValue()
            long r14 = r14 - r16
            int r6 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r6 >= 0) goto L_0x01a5
            goto L_0x025a
        L_0x01a5:
            long r8 = java.lang.System.currentTimeMillis()
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            r0.chupordowntime = r6
        L_0x01af:
            boolean r6 = r0.mCanChangeChannel
            if (r6 == 0) goto L_0x025a
            java.lang.String r6 = "TurnkeyUiMainActivity"
            java.lang.String r8 = "channel changed ~"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r8)
            r18.dismissPwddialog()
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler r6 = mHandler
            r6.removeMessages(r13)
            r0.mCanChangeChannel = r12
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler r6 = mHandler
            r6.sendEmptyMessageDelayed(r13, r10)
            android.os.Handler r6 = r0.mThreadHandler
            java.lang.Runnable r8 = r0.mChannelDownRunnable
            r6.post(r8)
            goto L_0x025a
        L_0x01d2:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r0.mCommonIntegration
            boolean r6 = r6.isCurrentSourceTv()
            if (r6 != 0) goto L_0x01dc
            goto L_0x025a
        L_0x01dc:
            com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil r6 = com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil.getInstance()
            if (r6 == 0) goto L_0x01f5
            com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil r6 = com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil.getInstance()
            boolean r6 = r6.isCHUPDOWNACTIONSupport()
            if (r6 != 0) goto L_0x01f5
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r0.mCommonIntegration
            boolean r6 = r6.isCurrentSourceTv()
            if (r6 != 0) goto L_0x01f5
            goto L_0x025a
        L_0x01f5:
            com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil r6 = r0.mTTS
            boolean r6 = r6.isTTSEnabled()
            if (r6 == 0) goto L_0x0218
            long r14 = java.lang.System.currentTimeMillis()
            java.lang.Long r6 = r0.chupordowntime
            long r16 = r6.longValue()
            long r14 = r14 - r16
            int r6 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r6 >= 0) goto L_0x020e
            goto L_0x025a
        L_0x020e:
            long r8 = java.lang.System.currentTimeMillis()
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            r0.chupordowntime = r6
        L_0x0218:
            boolean r6 = r0.mCanChangeChannel
            if (r6 == 0) goto L_0x025a
            java.lang.String r6 = "TurnkeyUiMainActivity"
            java.lang.String r8 = "channel changed ~"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r8)
            r18.dismissPwddialog()
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler r6 = mHandler
            r6.removeMessages(r13)
            r0.mCanChangeChannel = r12
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler r6 = mHandler
            r6.sendEmptyMessageDelayed(r13, r10)
            android.os.Handler r6 = r0.mThreadHandler
            java.lang.Runnable r8 = r0.mChannelUpRunnable
            r6.post(r8)
            goto L_0x025a
        L_0x023a:
            boolean r6 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r6)
            if (r6 == 0) goto L_0x0249
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r6 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
            com.mediatek.wwtv.tvcenter.dvr.controller.UImanager r6 = r6.uiManager
            r6.hiddenAllViews()
        L_0x0249:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r6 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r8 = 16777232(0x1000010, float:2.3509932E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r6 = r6.getComponentById(r8)
            com.mediatek.wwtv.tvcenter.nav.view.TwinkleView r6 = (com.mediatek.wwtv.tvcenter.nav.view.TwinkleView) r6
            r6.showHandler()
        L_0x025a:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.KeyHandler(int, android.view.KeyEvent, boolean):boolean");
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        if (isMapSerailKeys(keyCode)) {
            return true;
        }
        return KeyHandler(keyCode, event, true);
    }

    private void preInit() {
        MtkLog.v(TAG, " preInit()");
        HandlerThread mHandlerThead = new HandlerThread(TAG);
        mHandlerThead.start();
        this.mThreadHandler = new Handler(mHandlerThead.getLooper());
        this.OriginalView = (FrameLayout) ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.nav_main_layout_test, (ViewGroup) null);
        setContentView(this.OriginalView);
        this.mTvView = (TvSurfaceView) this.OriginalView.findViewById(R.id.nav_tv_base_view);
        this.mPipView = (TvSurfaceView) this.OriginalView.findViewById(R.id.nav_pip_base_view);
        this.mBlockScreenForTuneView = (TvBlockView) this.OriginalView.findViewById(R.id.block_screen_for_tune);
        this.mTvView.setBlockView(this.mBlockScreenForTuneView);
        this.mTifTimeShiftManager = TifTimeShiftManager.getInstance(this, this.mTvView);
        this.mTvView.setZOrderMediaOverlay(false);
        this.mPipView.setZOrderMediaOverlay(true);
        this.mTvView.setCallback(TvInputCallbackMgr.getInstance(this).getTvInputCallback());
        this.mPipView.setCallback(TvInputCallbackMgr.getInstance(this).getTvInputCallback());
        this.mTvView.setOnUnhandledInputEventListener(this);
        this.mSundryLayout = (LinearLayout) this.OriginalView.findViewById(R.id.nav_sundry_layout);
        this.mProgressBar = (ProgressBar) this.OriginalView.findViewById(R.id.fbm_mode_progressbar);
        SaveValue.getInstance(this).saveBooleanValue(MenuConfigManager.PVR_START, false);
        if (Settings.Global.getInt(getContentResolver(), "no_signal_auto_power_off", -1) == -1) {
            Settings.Global.putInt(getContentResolver(), "no_signal_auto_power_off", 3);
        }
    }

    private void init() {
        this.mNavCompsMagr = ComponentsManager.getInstance();
        this.mNavCompsMagr.deinitComponents();
        this.mCommonIntegration = TvSingletons.getSingletons().getCommonIntegration();
        this.mTTS = new TextToSpeechUtil(this.mContext);
        this.mEas = new MtkTvEASBase();
        this.mNavCompsMagr.clear();
        BannerImplement.reset();
        DataReader.reset();
        this.mNavCompsMagr.addDialog(InfoBarDialog.getInstance(this));
        this.mNavCompsMagr.addDialog(new PwdDialog(this));
        this.mNavCompsMagr.addDialog(new SourceListView(this));
        this.mNavCompsMagr.addDialog(new ChannelListDialog(this));
        this.mNavCompsMagr.addDialog(new FavoriteListDialog(this));
        this.mNavCompsMagr.addDialog(new CIMainDialog(this));
        this.mNavCompsMagr.addDialog(new SundryShowWithDialog(this));
        if (MarketRegionInfo.isFunctionSupport(2) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportGinga()) {
            this.mNavCompsMagr.addDialog(new GingaTvDialog(this));
        }
        if (2 == MarketRegionInfo.getCurrentMarketRegion()) {
            this.mNavCompsMagr.addDialog(new CommonMsgDialog(this));
        }
        if (3 == MarketRegionInfo.getCurrentMarketRegion() && (MarketRegionInfo.isFunctionSupport(16) || MarketRegionInfo.isFunctionSupport(3) || MarketRegionInfo.isFunctionSupport(51))) {
            this.mNavCompsMagr.addDialog(new EWSDialog(this));
        }
        this.mNavCompsMagr.addView((ZoomTipView) findViewById(R.id.nav_zoomview));
        this.mNavCompsMagr.addView((SundryShowTextView) findViewById(R.id.nav_tv_shortTip_textview));
        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(-1, -1);
        MtkLog.d(TAG, "onConfigurationChanged language updated and refresh banner view.");
        this.mBannerView = new BannerView(getApplicationContext());
        this.mBannerView.setLayoutParams(mParams);
        ((RelativeLayout) findViewById(R.id.nav_banner_info_bar_all)).addView(this.mBannerView);
        this.mNavCompsMagr.addView(this.mBannerView);
        this.mNavCompsMagr.addView((TwinkleView) findViewById(R.id.nav_stv_special_model));
        TifTimeshiftView timeshiftView = (TifTimeshiftView) findViewById(R.id.nav_tiftimeshift);
        this.mNavCompsMagr.addView(timeshiftView);
        timeshiftView.setListener();
        this.mNavCompsMagr.addView((MiscView) findViewById(R.id.nav_misc_textview));
        this.mNavCompsMagr.addMisc(new MenuOptionMain(this));
        this.mNavCompsMagr.addMisc(DvrManager.getInstance(this));
        if (MarketRegionInfo.isFunctionSupport(6)) {
            this.mNavCompsMagr.addMisc(TTXMain.getInstance(this));
        }
        if (MarketRegionInfo.isFunctionSupport(5)) {
            this.mNavCompsMagr.addMisc(new Hbbtv(this));
        }
        if (MarketRegionInfo.isFunctionSupport(9)) {
            this.mNavCompsMagr.addMisc(new Mheg5(this));
        }
        if (MarketRegionInfo.isFunctionSupport(44)) {
            this.mNavCompsMagr.addMisc(new FVP(this));
        }
        if (MarketRegionInfo.isFunctionSupport(0)) {
            this.mNavCompsMagr.addMisc(FloatView.getInstance(this));
        }
        if (MarketRegionInfo.isFunctionSupport(13)) {
            this.mTvView.setHandleEvent(true);
            this.mPipView.setHandleEvent(true);
            if (MarketRegionInfo.isFunctionSupport(26)) {
                MtkLog.d(TAG, "add MultiViewControl");
                MultiViewControl mMultiViewControl = new MultiViewControl(this);
                mMultiViewControl.setFocusLable((FocusLabel) findViewById(R.id.nav_pip_focus_picture));
                this.mNavCompsMagr.addMisc(mMultiViewControl);
                mMultiViewControl.setOutputView(this.mTvView, this.mPipView, this.mainSurfaceViewLy, this.subSurfaceViewLy);
            }
        } else {
            MtkLog.d(TAG, "add FocusLabelControl");
            FocusLabelControl mFocusLabelControl = new FocusLabelControl(this);
            mFocusLabelControl.setFocusLable((FocusLabel) findViewById(R.id.nav_pip_focus_picture));
            this.mNavCompsMagr.addMisc(mFocusLabelControl);
        }
        this.mNavCompsMagr.addMisc(new VgaPowerManager(this));
        MtkLog.v(TAG, this.mNavCompsMagr.toString());
        registerTvReceiver();
        ComponentStatusListener lister = ComponentStatusListener.getInstance();
        lister.addListener(6, this);
        lister.addListener(7, this);
        lister.addListener(10, this);
        lister.addListener(14, this);
        lister.addListener(15, this);
        lister.addListener(18, this);
        lister.addListener(17, this);
    }

    /* access modifiers changed from: private */
    public void postInit(boolean isShutFlow) {
        MtkLog.v(TAG, " postInit()");
        this.mAudioManager.requestAudioFocus();
        MtkLog.d(TAG, "postInit||getPowerOnStatus");
        int powerOnStatus = MtkTvOAD.getInstance().getPowerOnStatus();
        if (this.mNavCompsMagr.isCompsDestroyed()) {
            init();
        }
        initConfig(isStartTv);
        MtkLog.d(TAG, " postInit(),onResume()-6,isStartTv = " + isStartTv);
        if (isStartTv) {
            MtkLog.d(TAG, " postInit(),onResume()-6");
            if (1 == MarketRegionInfo.getCurrentMarketRegion()) {
                MtkLog.d(TAG, " postInit(),onResume()-6,mEas.EASGetAndroidLaunchStatus()=" + this.mEas.EASGetAndroidLaunchStatus());
                if (this.mEas.EASGetAndroidLaunchStatus()) {
                    MtkLog.d(TAG, " postInit(),onResume()-6,EASSetAndroidLaunchStatus(false)");
                    this.mEas.EASSetAndroidLaunchStatus(false);
                }
            }
            if (MarketRegionInfo.isFunctionSupport(13)) {
                MtkLog.d(TAG, " postInit(),onResume()-6,current tv mode = " + this.mHighLevel.getCurrentTvMode());
                if (!isShutFlow || !this.mCommonIntegration.selectPowerOnChannel()) {
                    MtkLog.d(TAG, " postInit(),onResume()-8,mNoNeedChangeChannel:" + this.mNoNeedChangeChannel);
                    if (!this.mNoNeedChangeChannel && getTvView() != null && !getTvView().isStart()) {
                        this.mInputSourceManager.resetCurrentInput();
                    }
                } else {
                    MtkLog.d(TAG, " postInit(),onResume()-7,mCommonIntegration.selectPowerOnChannel()");
                }
                if (MarketRegionInfo.isFunctionSupport(26)) {
                    getPipView().setStreamVolume(0.0f);
                    getTvView().setStreamVolume(1.0f);
                }
            }
            if (!this.isNeedStartSourceWithNotResume) {
                isStartTv = false;
                MtkLog.d(TAG, "777777");
            }
        }
        ComponentStatusListener.getInstance().updateStatus(3, 0);
        ((BannerView) this.mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_BANNER)).setInterruptShowBanner(false);
        MtkLog.i(TAG, "will resume banner, postInit:" + this.mNavCompsMagr.toString());
        MtkLog.d(TAG, "resume isPipOrPopState: " + CommonIntegration.getInstance().isPipOrPopState());
        if ((this.mNeedStartBanner && !ComponentsManager.getInstance().isComponentsShow()) || CommonIntegration.getInstance().isPipOrPopState()) {
            MtkLog.i(TAG, "will resume banner, showBannerInOnResume:" + showBannerInOnResume);
            if (showBannerInOnResume && !SaveValue.readLocalMemoryBooleanValue("tkOnCreate")) {
                MtkLog.i(TAG, "will resume banner.");
                this.mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_BANNER).startComponent();
            }
        }
        this.mNeedStartBanner = true;
    }

    private void receiveIntent(Intent intent) {
        String action = intent.getAction();
        FloatView floatView = (FloatView) ComponentsManager.getInstance().getComponentById(16777217);
        MtkLog.d(TAG, "onNewIntent: action:" + intent + ", floatView: " + floatView);
        if (floatView == null || !floatView.isEasPlaying()) {
            if ("android.intent.action.VIEW".equals(action)) {
                this.mNoNeedResetSource = false;
                boolean bLiveTv = intent.getBooleanExtra("livetv", false);
                MtkLog.d(TAG, "bLiveTv:" + bLiveTv);
                if (bLiveTv) {
                    showBannerInOnResume = false;
                }
                processInputUri(intent.getData());
            } else if ("android.mtk.intent.action.ACTION_REQUEST_TOP_RESUME".equals(action)) {
                Boolean showSourceList = Boolean.valueOf(intent.getBooleanExtra("showSourceList", false));
                MtkLog.v(TAG, "onResume showSourceList=" + showSourceList);
                if (showSourceList.booleanValue()) {
                    isStartTv = true;
                }
            } else if ("android.media.tv.action.SETUP_INPUTS".equals(action)) {
                startActivity(new Intent(this, SetupSourceActivity.class).addFlags(268435456));
            }
            if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                StateDvrPlayback.getInstance().stopDvrFilePlay();
                return;
            }
            return;
        }
        MtkLog.d(TAG, "eas message is playing!!!");
    }

    public void processInputUri(Uri uri) {
        this.mNoNeedResetSource = InputSourceManager.getInstance().processInputUri(uri);
        this.mNoNeedChangeChannel = this.mNoNeedResetSource;
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 6) {
            this.mAudioManager.abandonAudioFocus();
            MtkTvMultiView.getInstance().setChgSource(false);
            MtkLog.d(TAG, "come in NAV_ENTER_LANCHER,to stop session");
            if (MarketRegionInfo.isFunctionSupport(13) && this.mCommonIntegration.isPipOrPopState() && MarketRegionInfo.isFunctionSupport(26)) {
                ((MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP)).setNormalTvModeWithLauncher(false);
            }
            MtkLog.d(TAG, "enter lancher");
            if (!SystemProperties.get("vendor.mtk.tif.timeshift").equals("1")) {
                TVAsyncExecutor.getInstance().execute(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(MessageType.delayMillis5);
                        } catch (Exception e) {
                        }
                        MtkTvTimeshift.getInstance().stop();
                    }
                });
            }
            if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
                MtkLog.d(TAG, "DvrManager.isSuppportDualTuner() = " + DvrManager.isSuppportDualTuner());
                DvrManager.isSuppportDualTuner();
            }
            if (ScheduleListDialog.getDialog() != null && ScheduleListDialog.getDialog().isShowing()) {
                ScheduleListDialog.getDialog().dismiss();
            }
            if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this) != null && ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).isShowing()) {
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).dismiss();
            }
            this.mContext.sendBroadcast(new Intent("com.mtk.dialog.dismiss"));
            if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                DvrManager.getInstance().setStopDvrNotResumeLauncher(true);
            }
            TTXMain ttxMain = (TTXMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TELETEXT);
            if (ttxMain != null && ttxMain.isActive) {
                TeletextImplement.getInstance().stopTTX();
            }
            sendBroadcast(new Intent("com.mediatek.dialog.dismiss"));
        } else if (statusID == 7) {
            new MtkTvAppTVBase().updatedSysStatus(MtkTvAppTVBase.SYS_MMP_RESUME);
        } else if (statusID == 10) {
            mHandler.removeMessages(4104);
            ComponentStatusListener.setParam1(value);
            MtkLog.d(TAG, "mIsFromTK>>>" + this.mIsFromTK);
            List<Integer> cpmsIDs = ComponentsManager.getInstance().getCurrentActiveComps();
            if (this.mIsFromTK || cpmsIDs.contains(Integer.valueOf(NavBasic.NAV_COMP_ID_FAV_LIST))) {
                ComponentStatusListener.getInstance().updateStatus(5, this.mSendKeyCode);
            }
            this.mCanChangeChannel = true;
        } else if (statusID == 14) {
            MultiViewControl mMultiViewControl = new MultiViewControl(getInstance().getApplicationContext());
            if (CommonIntegration.getInstance().isPipOrPopState()) {
                mMultiViewControl.setNormalTvModeWithGooglePiP();
                ComponentStatusListener.getInstance().delayUpdateStatus(14, 2500);
                return;
            }
            enterPictureInPictureModeIfPossible();
            SaveValue.getInstance(this.mContext).saveBooleanValue("isInPictureInPicureMode", true);
            DvrManager.getInstance().setStopDvrNotResumeLauncher(true);
            MtkTvConfig.getInstance().setAndroidWorldInfoToLinux(0, 1);
            MtkTvUtil.getInstance();
            MtkTvUtil.setOSDPlaneEnable(0, 0);
            MtkTvUtil.getInstance();
            MtkTvUtil.setOSDPlaneEnable(1, 0);
        } else if (statusID == 15) {
            SaveValue.getInstance(this.mContext).saveBooleanValue("isInPictureInPicureMode", false);
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    MtkTvConfig.getInstance().setAndroidWorldInfoToLinux(0, 0);
                    MtkTvUtil.getInstance();
                    MtkTvUtil.setOSDPlaneEnable(0, 1);
                    MtkTvUtil.getInstance();
                    MtkTvUtil.setOSDPlaneEnable(1, 1);
                    TurnkeyUiMainActivity.this.mAudioManager.unmuteTVAudio(1);
                }
            });
        } else if (statusID == 18) {
            powerOn();
        } else if (statusID == 17) {
            powerOff(value);
        }
    }

    private void checkNetflixResume() {
        MtkLog.d(TAG, "checkNetflixResume");
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                if (!NetflixUtil.isNetflixKeyResume()) {
                    TurnkeyUiMainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            TurnkeyUiMainActivity.this.postInit(true);
                        }
                    });
                }
            }
        });
    }

    private void powerOn() {
        MtkLog.d(TAG, "ACTION_SCREEN_ON isShutdownFlow: " + this.isShutdownFlow);
        MtkLog.d(TAG, "DestroyApp.isCurTaskTKUI() =" + DestroyApp.isCurTaskTKUI() + "DestroyApp.isCurActivityTkuiMainActivity()" + DestroyApp.isCurActivityTkuiMainActivity());
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording() && !DestroyApp.isCurActivityTkuiMainActivity()) {
            StateDvr.getInstance().clearWindow(false);
        }
        if (this.isShutdownFlow) {
            isStartTv = true;
            this.mInputSourceManager.resetScreenOffFlag();
            if (MarketRegionInfo.getCurrentMarketRegion() == 3) {
                TVAsyncExecutor.getInstance().execute(new Runnable() {
                    public void run() {
                        List<TIFChannelInfo> inactiveChannelList = TIFChannelManager.getInstance(TurnkeyUiMainActivity.this.mContext).getAttentionMaskChannels(TIFFunctionUtil.CH_CONFIRM_REMOVE_MASK, TIFFunctionUtil.CH_CONFIRM_REMOVE_VAL, -1);
                        MtkLog.d(TurnkeyUiMainActivity.TAG, "inactiveChannelList>>>" + inactiveChannelList.size());
                        if (inactiveChannelList.size() > 0) {
                            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                                public void run() {
                                    TurnkeyUiMainActivity.this.showDVBT_InactiveChannels_ConfirmDialog();
                                }
                            });
                        }
                    }
                });
            }
            this.isShutdownFlow = false;
            SaveValue saveVv = SaveValue.getInstance(this.mContext);
            String isTuneDone = saveVv.readStrValue("FineTune_IsFineTuneDone");
            MtkLog.e(TAG, "ACTION_SCREEN_ON,isTuneDone==" + isTuneDone);
            if ("false".equals(isTuneDone)) {
                saveVv.saveStrValue("FineTune_IsFineTuneDone", "true");
                final float resHz = Float.parseFloat(saveVv.readStrValue("FineTune_RestoreHz"));
                this.mThreadHandler.postDelayed(new Runnable() {
                    public void run() {
                        TurnkeyUiMainActivity.this.mCommonIntegration.restoreFineTune(resHz);
                    }
                }, 3000);
            }
        }
    }

    private void powerOff(int value) {
        List<MtkTvBookingBase> books;
        this.isShutdownFlow = true;
        if (SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
            TifTimeshiftView tifTimeshiftView = (TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW);
            tifTimeshiftView.setVisibility(8);
            tifTimeshiftView.stopTifTimeShift();
        }
        if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
            StateDvrPlayback.getInstance().stopDvrFilePlay();
        }
        if (value == 19) {
            this.mInputSourceManager.setShutDownFlag(true);
        }
        if (DestroyApp.isCurOADActivity()) {
            NavOADActivity.getInstance().stopOAD(false);
        }
        if (StateDvr.getInstance() != null) {
            StateDvr.getInstance().hideDvrdialog();
        }
        MtkLog.d(TAG, "DestroyApp.isCurTaskTKUI() =" + DestroyApp.isCurTaskTKUI() + "DestroyApp.isCurActivityTkuiMainActivity()" + DestroyApp.isCurActivityTkuiMainActivity());
        if (DestroyApp.isCurTaskTKUI() && !DestroyApp.isCurActivityTkuiMainActivity()) {
            resumeTurnkeyActivity(getApplicationContext());
        }
        MtkLog.d(TAG, "come in Intent.ACTION_SCREEN_OFF or ACTION_SHUTDOWN");
        TTXMain ttxMain = (TTXMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TELETEXT);
        if (!(ttxMain == null || ttxMain.favDialog == null)) {
            ttxMain.favDialog.clearFavlist();
        }
        this.mNoNeedResetSource = true;
        this.mNoNeedChangeChannel = false;
        if ((!MarketRegionInfo.isFunctionSupport(30) || StateDvr.getInstance() == null || !StateDvr.getInstance().isRecording()) && ((books = MtkTvRecord.getInstance().getBookingList()) == null || books.size() <= 0)) {
            this.mThreadHandler.post(new Runnable() {
                public void run() {
                    TurnkeyUiMainActivity.this.mHighLevel.stopTV();
                    TurnkeyUiMainActivity.this.mHighLevel.exitInternalApp(2);
                }
            });
        }
        this.mNavCompsMagr.hideAllComponents();
        ComponentStatusListener.getInstance().updateStatus(8, -1);
    }

    /* access modifiers changed from: private */
    public void resetTurnkeyLayout() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.height = -1;
        lp.width = -1;
        lp.y = 0;
        lp.x = 0;
        getWindow().setAttributes(lp);
        this.OriginalView.invalidate();
    }

    private static class InternalHandler extends Handler {
        /* access modifiers changed from: private */
        public final MtkTvCIBase ciBase = MtkTvCI.getInstance(0);
        private DiagnosticDialog dtcDialog;
        private final WeakReference<TurnkeyUiMainActivity> mDialog;

        public InternalHandler(TurnkeyUiMainActivity dialog) {
            this.mDialog = new WeakReference<>(dialog);
            showDiagnosticDialog();
            MtkLog.dumpMessageQueue(this, 1000);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(TurnkeyUiMainActivity.TAG, "[InternalHandler] handlerMessage occur~" + msg.what + "," + msg.arg1 + "," + msg.arg2 + "," + msg.obj);
            if (this.mDialog.get() == null) {
                return;
            }
            if (msg.what == msg.arg1 && msg.what == msg.arg2 && (msg.what & TvCallbackConst.MSG_CB_BASE_FLAG) != 0) {
                handlerCallbackMsg(msg);
            } else {
                handlerMessages(msg);
            }
        }

        private void showDiagnosticDialog() {
            if (this.dtcDialog == null) {
                this.dtcDialog = new DiagnosticDialog((Context) this.mDialog.get());
                this.dtcDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        InternalHandler.this.ciBase.setHDSConfirm(1);
                    }
                });
                this.dtcDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    public void onShow(DialogInterface dialog) {
                        InternalHandler.this.ciBase.setHDSConfirm(0);
                    }
                });
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:152:0x048c  */
        /* JADX WARNING: Removed duplicated region for block: B:153:0x0490  */
        /* JADX WARNING: Removed duplicated region for block: B:159:0x04bb  */
        /* JADX WARNING: Removed duplicated region for block: B:359:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void handlerCallbackMsg(android.os.Message r18) {
            /*
                r17 = this;
                r1 = r17
                r2 = r18
                java.lang.Object r0 = r2.obj
                r3 = r0
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r3 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r3
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "msg = "
                r4.append(r5)
                int r5 = r2.what
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                int r0 = r2.what
                r4 = 16777234(0x1000012, float:2.3509937E-38)
                r5 = 4
                r6 = 12
                r7 = 3
                r8 = 16777225(0x1000009, float:2.3509912E-38)
                r10 = 16777232(0x1000010, float:2.3509932E-38)
                r11 = 2
                r12 = 16777228(0x100000c, float:2.350992E-38)
                r13 = 16777221(0x1000005, float:2.35099E-38)
                r14 = 16777218(0x1000002, float:2.3509893E-38)
                r15 = 0
                r9 = 1
                switch(r0) {
                    case 1879048193: goto L_0x0778;
                    case 1879048194: goto L_0x0761;
                    case 1879048195: goto L_0x003f;
                    case 1879048196: goto L_0x003f;
                    case 1879048197: goto L_0x003f;
                    case 1879048198: goto L_0x0589;
                    case 1879048199: goto L_0x0570;
                    case 1879048200: goto L_0x003f;
                    case 1879048201: goto L_0x003f;
                    case 1879048202: goto L_0x003f;
                    case 1879048203: goto L_0x003f;
                    case 1879048204: goto L_0x003f;
                    case 1879048205: goto L_0x003f;
                    case 1879048206: goto L_0x003f;
                    case 1879048207: goto L_0x003f;
                    case 1879048208: goto L_0x003f;
                    case 1879048209: goto L_0x054d;
                    case 1879048210: goto L_0x053e;
                    case 1879048211: goto L_0x003f;
                    case 1879048212: goto L_0x0520;
                    case 1879048213: goto L_0x04f5;
                    case 1879048214: goto L_0x003f;
                    case 1879048215: goto L_0x042b;
                    case 1879048216: goto L_0x03b8;
                    case 1879048217: goto L_0x037a;
                    case 1879048218: goto L_0x031a;
                    case 1879048219: goto L_0x003f;
                    case 1879048220: goto L_0x02f7;
                    case 1879048221: goto L_0x0280;
                    case 1879048222: goto L_0x003f;
                    case 1879048223: goto L_0x026a;
                    case 1879048224: goto L_0x003f;
                    case 1879048225: goto L_0x0256;
                    case 1879048226: goto L_0x022a;
                    case 1879048227: goto L_0x003f;
                    case 1879048228: goto L_0x0221;
                    case 1879048229: goto L_0x020e;
                    case 1879048230: goto L_0x003f;
                    case 1879048231: goto L_0x003f;
                    case 1879048232: goto L_0x003f;
                    case 1879048233: goto L_0x01da;
                    case 1879048234: goto L_0x01c3;
                    case 1879048235: goto L_0x003f;
                    case 1879048236: goto L_0x0120;
                    case 1879048237: goto L_0x00ff;
                    case 1879048238: goto L_0x00fd;
                    case 1879048239: goto L_0x003f;
                    case 1879048240: goto L_0x00fb;
                    case 1879048241: goto L_0x007c;
                    case 1879048242: goto L_0x0056;
                    case 1879048243: goto L_0x0041;
                    default: goto L_0x003f;
                }
            L_0x003f:
                goto L_0x082c
            L_0x0041:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r12)
                com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog) r0
                if (r0 == 0) goto L_0x082c
                int r4 = r3.param1
                int r5 = r3.param2
                r0.changeVolume(r4, r5)
                goto L_0x082c
            L_0x0056:
                com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
                java.lang.String r0 = r0.getCountry()
                java.lang.String r4 = "GBR"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x082c
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 33554438(0x2000006, float:9.403962E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.FVP r0 = (com.mediatek.wwtv.tvcenter.nav.view.FVP) r0
                int r4 = r3.param1
                int r5 = r3.param2
                r0.handlerFVPMessage(r4, r5)
                goto L_0x082c
            L_0x007c:
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "MSG_CB_EWS_MSG  data.param1 = "
                r4.append(r5)
                int r5 = r3.param1
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r8)
                com.mediatek.wwtv.tvcenter.nav.view.EWSDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.EWSDialog) r0
                int r4 = r3.param1
                if (r4 == r9) goto L_0x00e7
                int r4 = r3.param1
                if (r4 != 0) goto L_0x00a7
                goto L_0x00e7
            L_0x00a7:
                int r4 = r3.param1
                if (r4 == r11) goto L_0x00af
                int r4 = r3.param1
                if (r4 != r7) goto L_0x082c
            L_0x00af:
                if (r0 == 0) goto L_0x00c3
                boolean r4 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurActivityTkuiMainActivity()
                if (r4 == 0) goto L_0x00c3
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.String r5 = "showEwsDialog"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                r0.show()
                goto L_0x082c
            L_0x00c3:
                if (r0 == 0) goto L_0x082c
                boolean r4 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurActivityTkuiMainActivity()
                if (r4 != 0) goto L_0x082c
                com.mediatek.twoworlds.tv.MtkTvScan r4 = com.mediatek.twoworlds.tv.MtkTvScan.getInstance()
                boolean r4 = r4.isScanning()
                if (r4 == 0) goto L_0x00d7
                goto L_0x082c
            L_0x00d7:
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
                android.content.Context r4 = r4.getApplicationContext()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.resumeTurnkeyActivity(r4)
                r0.show()
                goto L_0x082c
            L_0x00e7:
                if (r0 == 0) goto L_0x082c
                boolean r4 = r0.isShowing()
                if (r4 == 0) goto L_0x082c
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.String r5 = "dismiss ewsDialog"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                r0.dismiss()
                goto L_0x082c
            L_0x00fb:
                goto L_0x082c
            L_0x00fd:
                goto L_0x082c
            L_0x00ff:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r13)
                com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog) r0
                if (r0 == 0) goto L_0x082c
                boolean r4 = r0.isVisible()
                if (r4 == 0) goto L_0x082c
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r4 = r4.isGeneralSatMode()
                if (r4 == 0) goto L_0x082c
                r0.updateSatelliteList()
                goto L_0x082c
            L_0x0120:
                java.lang.Object r0 = r2.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "apdata.param1: "
                r5.append(r6)
                int r6 = r0.param1
                r5.append(r6)
                java.lang.String r6 = ", apdata.paramBool1: "
                r5.append(r6)
                boolean r6 = r0.paramBool1
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "isShutdownFlow>>>"
                r5.append(r6)
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r6 = r1.mDialog
                java.lang.Object r6 = r6.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r6 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r6
                boolean r6 = r6.isShutdownFlow
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r4 = r1.mDialog
                java.lang.Object r4 = r4.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r4
                boolean r4 = r4.isShutdownFlow
                if (r4 == 0) goto L_0x0196
                boolean r4 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurOADActivity()
                if (r4 == 0) goto L_0x017d
                return
            L_0x017d:
                boolean r4 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurEPGActivity()
                if (r4 == 0) goto L_0x082c
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.String r5 = "destory EPG"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r4 = r1.mDialog
                java.lang.Object r4 = r4.get()
                android.content.Context r4 = (android.content.Context) r4
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.resumeTurnkeyActivity(r4)
                return
            L_0x0196:
                int r4 = r0.param1
                if (r11 != r4) goto L_0x082c
                boolean r4 = r0.paramBool1
                if (r4 != r9) goto L_0x082c
                boolean r4 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurActivityTkuiMainActivity()
                if (r4 == 0) goto L_0x082c
                android.content.Intent r4 = new android.content.Intent
                r4.<init>()
                java.lang.Class<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r5 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.class
                java.lang.String r5 = r5.getSimpleName()
                java.lang.String r6 = com.mediatek.wwtv.tvcenter.util.DestroyApp.getRunningActivity()
                r4.putExtra(r5, r6)
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r5 = r1.mDialog
                java.lang.Object r5 = r5.get()
                android.content.Context r5 = (android.content.Context) r5
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.resumeTurnkeyActivity(r5)
                goto L_0x082c
            L_0x01c3:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r13)
                com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog) r0
                if (r0 == 0) goto L_0x082c
                boolean r4 = r0.isVisible()
                if (r4 == 0) goto L_0x082c
                r0.handleUpdateCallBack()
                goto L_0x082c
            L_0x01da:
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.String r4 = "RECORD_NFY---->:"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                java.lang.Object r0 = r2.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r4 = r1.mDialog
                java.lang.Object r4 = r4.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r4
                boolean r4 = r4.isShutdownFlow
                if (r4 == 0) goto L_0x01f5
                r0.param3 = r9
            L_0x01f5:
                r4 = 30
                boolean r4 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r4)
                if (r4 == 0) goto L_0x082c
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r4 = r1.mDialog
                java.lang.Object r4 = r4.get()
                android.content.Context r4 = (android.content.Context) r4
                com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r4 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance(r4)
                r4.handleRecordNTF((com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0)
                goto L_0x082c
            L_0x020e:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r10)
                com.mediatek.wwtv.tvcenter.nav.view.TwinkleView r0 = (com.mediatek.wwtv.tvcenter.nav.view.TwinkleView) r0
                if (r0 == 0) goto L_0x082c
                int r4 = r3.param1
                r0.handleCallBack(r4)
                goto L_0x082c
            L_0x0221:
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.String r4 = "receive TvCallbackConst.MSG_CB_PWD_DLG_MSG"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                goto L_0x082c
            L_0x022a:
                java.lang.Object r0 = r2.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                int r4 = r0.param1
                if (r4 != 0) goto L_0x082c
                int r4 = r0.param2
                switch(r4) {
                    case 6: goto L_0x0238;
                    case 7: goto L_0x0238;
                    case 8: goto L_0x0238;
                    case 9: goto L_0x0238;
                    case 10: goto L_0x0238;
                    case 11: goto L_0x0238;
                    case 12: goto L_0x0237;
                    case 13: goto L_0x0238;
                    case 14: goto L_0x0238;
                    default: goto L_0x0237;
                }
            L_0x0237:
                goto L_0x0254
            L_0x0238:
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r4 = r4.isTurnCHAfterExitEPG()
                if (r4 == 0) goto L_0x0254
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                r4.setTurnCHAfterExitEPG(r15)
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r4 = r1.mDialog
                java.lang.Object r4 = r4.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r4
                r4.muteVideoAndAudio(r15)
            L_0x0254:
                goto L_0x082c
            L_0x0256:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 16777231(0x100000f, float:2.350993E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.UpdaterDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.UpdaterDialog) r0
                if (r0 == 0) goto L_0x082c
                r0.modifyViewStatus(r3)
                goto L_0x082c
            L_0x026a:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 16777233(0x1000011, float:2.3509935E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.TTXMain r0 = (com.mediatek.wwtv.tvcenter.nav.view.TTXMain) r0
                if (r0 == 0) goto L_0x082c
                int r4 = r3.param1
                r0.handlerTTXMessage(r4)
                goto L_0x082c
            L_0x0280:
                com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback r0 = com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback.getInstance()
                if (r0 == 0) goto L_0x0292
                com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback r0 = com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback.getInstance()
                boolean r0 = r0.isRunning()
                if (r0 == 0) goto L_0x0292
                goto L_0x082c
            L_0x0292:
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.mainActivity
                java.lang.String r4 = "activity"
                java.lang.Object r0 = r0.getSystemService(r4)
                android.app.ActivityManager r0 = (android.app.ActivityManager) r0
                java.lang.String r4 = "com.mediatek.wwtv.setting"
                r0.killBackgroundProcesses(r4)
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r5 = 16777217(0x1000001, float:2.350989E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r4.getComponentById(r5)
                com.mediatek.wwtv.tvcenter.nav.view.FloatView r4 = (com.mediatek.wwtv.tvcenter.nav.view.FloatView) r4
                if (r4 == 0) goto L_0x02b9
                int r5 = r3.param1
                int r6 = r3.param2
                r4.handleEasMessage(r5, r6)
            L_0x02b9:
                java.lang.Object r5 = r2.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r5 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r5
                int r5 = r5.param1
                if (r5 != r9) goto L_0x082c
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r5 = r1.mDialog
                java.lang.Object r5 = r5.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r5 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r5
                android.content.Intent r6 = new android.content.Intent
                java.lang.String r7 = "com.mediatek.dialog.dismiss"
                r6.<init>(r7)
                r5.sendBroadcast(r6)
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r5 = r5.isPipOrPopState()
                if (r5 == 0) goto L_0x082c
                r5 = 26
                boolean r5 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r5)
                if (r5 == 0) goto L_0x082c
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r5 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r6 = 16777235(0x1000013, float:2.350994E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r5 = r5.getComponentById(r6)
                com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl r5 = (com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl) r5
                r5.setNormalTvModeWithEas()
                goto L_0x082c
            L_0x02f7:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 16777224(0x1000008, float:2.350991E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.CommonMsgDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.CommonMsgDialog) r0
                if (r0 == 0) goto L_0x082c
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4.hideAllComponents()
                int r4 = r3.param1
                int r5 = r3.param2
                int r6 = r3.param3
                java.lang.String r7 = r3.paramStr1
                r0.commonMsgHanndler(r4, r5, r6, r7)
                goto L_0x082c
            L_0x031a:
                int r0 = r3.param2
                if (r0 == 0) goto L_0x0337
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "MSG_CB_NO_USED_KEY_MSG, data.param2="
                r4.append(r5)
                int r5 = r3.param2
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.e(r0, r4)
                return
            L_0x0337:
                java.lang.Class<com.mediatek.wwtv.tvcenter.util.KeyDispatch> r4 = com.mediatek.wwtv.tvcenter.util.KeyDispatch.class
                monitor-enter(r4)
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r0 = r1.mDialog     // Catch:{ all -> 0x0377 }
                java.lang.Object r0 = r0.get()     // Catch:{ all -> 0x0377 }
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r0     // Catch:{ all -> 0x0377 }
                com.mediatek.wwtv.tvcenter.util.KeyDispatch r5 = r0.mKeyDispatch     // Catch:{ all -> 0x0377 }
                int r5 = r5.getPassedAndroidKey()     // Catch:{ all -> 0x0377 }
                com.mediatek.wwtv.tvcenter.util.KeyDispatch r6 = r0.mKeyDispatch     // Catch:{ all -> 0x0377 }
                int r6 = r6.androidKeyToDFBkey(r5)     // Catch:{ all -> 0x0377 }
                int r7 = r3.param1     // Catch:{ all -> 0x0377 }
                if (r6 == r7) goto L_0x0370
                java.lang.String r6 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0377 }
                r7.<init>()     // Catch:{ all -> 0x0377 }
                java.lang.String r8 = "key not map, data.param2="
                r7.append(r8)     // Catch:{ all -> 0x0377 }
                int r8 = r3.param1     // Catch:{ all -> 0x0377 }
                r7.append(r8)     // Catch:{ all -> 0x0377 }
                java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0377 }
                com.mediatek.wwtv.tvcenter.util.MtkLog.e(r6, r7)     // Catch:{ all -> 0x0377 }
                monitor-exit(r4)     // Catch:{ all -> 0x0377 }
                return
            L_0x0370:
                r6 = 0
                r0.KeyHandler(r5, r6, r9)     // Catch:{ all -> 0x0377 }
                monitor-exit(r4)     // Catch:{ all -> 0x0377 }
                goto L_0x082c
            L_0x0377:
                r0 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x0377 }
                throw r0
            L_0x037a:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 33554434(0x2000002, float:9.403957E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.Hbbtv r0 = (com.mediatek.wwtv.tvcenter.nav.view.Hbbtv) r0
                if (r0 == 0) goto L_0x0390
                int r4 = r3.param1
                int r5 = r3.param2
                r0.handlerHbbtvMessage(r4, r5)
            L_0x0390:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r4.getComponentById(r10)
                com.mediatek.wwtv.tvcenter.nav.view.TwinkleView r4 = (com.mediatek.wwtv.tvcenter.nav.view.TwinkleView) r4
                if (r4 == 0) goto L_0x03a3
                int r5 = r3.param1
                int r6 = r3.param2
                r4.handlerHbbtvMessage(r5, r6)
            L_0x03a3:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r5 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r5 = r5.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r5 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r5
                if (r5 == 0) goto L_0x03b6
                int r6 = r3.param1
                int r7 = r3.param2
                r5.handlerHbbtvMessage(r6, r7)
            L_0x03b6:
                goto L_0x082c
            L_0x03b8:
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r0 = r1.mDialog
                java.lang.Object r0 = r0.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r0
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r8 = "MSG_CB_OAD_MSG,msg_type:"
                r7.append(r8)
                int r8 = r3.param1
                r7.append(r8)
                java.lang.String r7 = r7.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r7)
                int r4 = r3.param1
                if (r4 != r6) goto L_0x03f4
                java.lang.String r4 = "showUpgradeMsg"
                boolean r4 = com.mediatek.wwtv.tvcenter.util.SaveValue.readLocalMemoryBooleanValue(r4)
                if (r4 == 0) goto L_0x03f4
                java.lang.String r4 = "Upgrade success."
                android.widget.Toast r4 = android.widget.Toast.makeText(r0, r4, r9)
                r4.show()
                java.lang.String r4 = "showUpgradeMsg"
                com.mediatek.wwtv.tvcenter.util.SaveValue.setLocalMemoryValue((java.lang.String) r4, (boolean) r15)
                goto L_0x082c
            L_0x03f4:
                int r4 = r3.param1
                if (r4 == r5) goto L_0x082c
                int r4 = r3.param1
                if (r4 != r9) goto L_0x03fe
                goto L_0x082c
            L_0x03fe:
                boolean r4 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurOADActivity()
                if (r4 != 0) goto L_0x082c
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.String r5 = "create OAD Activity"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                android.content.Intent r4 = new android.content.Intent
                java.lang.Class<com.mediatek.wwtv.tvcenter.oad.NavOADActivity> r5 = com.mediatek.wwtv.tvcenter.oad.NavOADActivity.class
                r4.<init>(r0, r5)
                r5 = 1610612736(0x60000000, float:3.6893488E19)
                r4.setFlags(r5)
                android.os.Bundle r5 = new android.os.Bundle
                r5.<init>()
                java.lang.String r6 = "updateType"
                int r7 = r3.param1
                r5.putInt(r6, r7)
                r4.putExtras(r5)
                r0.startActivity(r4)
                goto L_0x082c
            L_0x042b:
                com.mediatek.twoworlds.tv.MtkTvPWDDialog r0 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
                int r0 = r0.PWDShow()
                if (r0 == 0) goto L_0x0437
                r15 = r9
            L_0x0437:
                r0 = r15
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "MSG_CB_CI_MSG isPWDNotShow=="
                r5.append(r6)
                r5.append(r0)
                java.lang.String r5 = r5.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                r4 = 0
                r5 = 0
                int r6 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.getCurrentMarketRegion()
                if (r6 == 0) goto L_0x0471
                com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r6 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
                if (r6 == 0) goto L_0x0461
                goto L_0x0471
            L_0x0461:
                int r6 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.getCurrentMarketRegion()
                if (r7 != r6) goto L_0x0479
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r5 = r6.isCurrentSourceTv()
                goto L_0x0479
            L_0x0471:
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r4 = r6.isCurrentSourceDTV()
            L_0x0479:
                if (r5 != 0) goto L_0x047d
                if (r4 == 0) goto L_0x049d
            L_0x047d:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r6 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r7 = 16777245(0x100001d, float:2.3509968E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r6 = r6.getComponentById(r7)
                com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r6 = (com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog) r6
                if (r0 == 0) goto L_0x0490
                r6.handleCIMessage(r3)
                goto L_0x049d
            L_0x0490:
                boolean r7 = r6.isDialogIsShow()
                if (r7 != 0) goto L_0x049a
                r6.handleCIMessageDelay(r3)
                goto L_0x049d
            L_0x049a:
                r6.handleCIMessage(r3)
            L_0x049d:
                int r6 = r3.param3
                int r7 = r3.param2
                java.lang.String r8 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r11 = "messageType"
                r10.append(r11)
                r10.append(r7)
                java.lang.String r10 = r10.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r10)
                r8 = 22
                if (r7 != r8) goto L_0x082c
                java.lang.String r8 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r11 = "MTKTV_CI_NFY_COND_HDS_REQUEST>>>>isShow:"
                r10.append(r11)
                r10.append(r6)
                java.lang.String r10 = r10.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r10)
                if (r6 != 0) goto L_0x04e8
                com.mediatek.wwtv.tvcenter.commonview.DiagnosticDialog r8 = r1.dtcDialog
                if (r8 == 0) goto L_0x04de
                com.mediatek.wwtv.tvcenter.commonview.DiagnosticDialog r8 = r1.dtcDialog
                r8.show()
                goto L_0x082c
            L_0x04de:
                r17.showDiagnosticDialog()
                com.mediatek.wwtv.tvcenter.commonview.DiagnosticDialog r8 = r1.dtcDialog
                r8.show()
                goto L_0x082c
            L_0x04e8:
                if (r6 != r9) goto L_0x082c
                com.mediatek.wwtv.tvcenter.commonview.DiagnosticDialog r8 = r1.dtcDialog
                if (r8 == 0) goto L_0x082c
                com.mediatek.wwtv.tvcenter.commonview.DiagnosticDialog r8 = r1.dtcDialog
                r8.cancel()
                goto L_0x082c
            L_0x04f5:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 33554433(0x2000001, float:9.403956E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.Mheg5 r0 = (com.mediatek.wwtv.tvcenter.nav.view.Mheg5) r0
                if (r0 == 0) goto L_0x050b
                int r4 = r3.param1
                int r5 = r3.param2
                r0.handlerMheg5Message(r4, r5)
            L_0x050b:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r4.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r4 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r4
                if (r4 == 0) goto L_0x051e
                int r5 = r3.param1
                int r6 = r3.param2
                r4.handlerMheg5Message(r5, r6)
            L_0x051e:
                goto L_0x082c
            L_0x0520:
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.String r4 = "handle MSG_CB_AV_MODE_MSG"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r0 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r0
                if (r0 == 0) goto L_0x082c
                boolean r4 = r0.isVisible()
                if (r4 == 0) goto L_0x082c
                r0.updateBasicBarAudio()
                goto L_0x082c
            L_0x053e:
                com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
                boolean r0 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.isSuppport()
                if (r0 == 0) goto L_0x0549
                goto L_0x082c
            L_0x0549:
                java.lang.Object r0 = r2.obj
                goto L_0x082c
            L_0x054d:
                int r0 = r3.param1
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                int r4 = r4.getSvl()
                if (r0 != r4) goto L_0x082c
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r13)
                com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog) r0
                if (r0 == 0) goto L_0x056e
                boolean r4 = r0.isVisible()
                if (r4 == 0) goto L_0x056e
                r0.handleCallBack()
            L_0x056e:
                goto L_0x082c
            L_0x0570:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r12)
                com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog) r0
                if (r0 == 0) goto L_0x082c
                int r4 = r3.param1
                java.lang.String r5 = r3.paramStr1
                java.lang.Object r6 = r3.paramObj1
                java.lang.String r6 = (java.lang.String) r6
                r0.addGingaAppInfo(r4, r5, r6)
                goto L_0x082c
            L_0x0589:
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r11 = "mgs=MSG_CB_SVCTX_NOTIFY----->data.parama1  = "
                r7.append(r11)
                int r11 = r3.param1
                r7.append(r11)
                java.lang.String r7 = r7.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r7)
                int r0 = r3.param1
                r7 = 37
                if (r0 == r7) goto L_0x0639
                int r0 = r3.param1
                r11 = 20
                if (r0 != r11) goto L_0x05af
                goto L_0x0639
            L_0x05af:
                int r0 = r3.param1
                r10 = 7
                if (r0 != r10) goto L_0x05c4
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.String r4 = "MSG_CB_SVCTX_NOTIFY--->data.param1==SVCTX_NTFY_CODE_CHANNEL_CHANGE"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                r0.setCHChanging(r15)
                goto L_0x065a
            L_0x05c4:
                int r0 = r3.param1
                r10 = 9
                if (r0 == r10) goto L_0x05ed
                int r0 = r3.param1
                if (r0 != r6) goto L_0x05cf
                goto L_0x05ed
            L_0x05cf:
                int r0 = r3.param1
                r4 = 18
                if (r0 != r4) goto L_0x065a
                java.lang.String r0 = "TurnkeyUiMainActivity"
                java.lang.String r4 = "MSG_CB_SVCTX_NOTIFY--->data.param1==SVCTX_NTFY_CODE_RATING"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r4)
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r0 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r0
                if (r0 == 0) goto L_0x065a
                r0.updateRatingInfo()
                goto L_0x065a
            L_0x05ed:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r0 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r0
                if (r0 == 0) goto L_0x060c
                java.lang.String r6 = "TurnkeyUiMainActivity"
                java.lang.String r11 = "setBlockStateFromSVCTX"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r11)
                int r6 = r3.param1
                if (r6 != r10) goto L_0x0607
                r6 = r9
                goto L_0x0609
            L_0x0607:
                r6 = r15
            L_0x0609:
                r0.handleBlockStateFromSVCTX(r6)
            L_0x060c:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r6 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r6.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.PwdDialog r4 = (com.mediatek.wwtv.tvcenter.nav.view.PwdDialog) r4
                if (r4 == 0) goto L_0x0638
                int r6 = r3.param1
                if (r6 != r10) goto L_0x061e
                r9 = r15
            L_0x061e:
                r6 = r9
                java.lang.String r9 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r11 = "MSG_CB_SVCTX_NOTIFY--->type="
                r10.append(r11)
                r10.append(r6)
                java.lang.String r10 = r10.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
                r4.handleCallBack(r6)
            L_0x0638:
                goto L_0x065a
            L_0x0639:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r10)
                com.mediatek.wwtv.tvcenter.nav.view.TwinkleView r0 = (com.mediatek.wwtv.tvcenter.nav.view.TwinkleView) r0
                if (r0 == 0) goto L_0x064a
                int r4 = r3.param1
                r0.handlerVideoCallback(r4)
            L_0x064a:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r4.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r4 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r4
                if (r4 == 0) goto L_0x0659
                r4.unScramebled()
            L_0x0659:
            L_0x065a:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r4 = 16777229(0x100000d, float:2.3509923E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.InfoBarDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.InfoBarDialog) r0
                if (r0 == 0) goto L_0x066e
                int r4 = r3.param1
                r0.handlerMessage(r4)
            L_0x066e:
                int r4 = r3.param1
                if (r4 != r7) goto L_0x0698
                java.lang.String r4 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r7 = "native component id:"
                r6.append(r7)
                int r7 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getNativeActiveCompId()
                r6.append(r7)
                java.lang.String r6 = r6.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r6)
                com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor r4 = com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor.getInstance()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler$3 r6 = new com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler$3
                r6.<init>()
                r4.execute(r6)
            L_0x0698:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r4 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                r6 = 16777242(0x100001a, float:2.350996E-38)
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r4.getComponentById(r6)
                com.mediatek.wwtv.tvcenter.nav.view.VgaPowerManager r4 = (com.mediatek.wwtv.tvcenter.nav.view.VgaPowerManager) r4
                if (r4 == 0) goto L_0x0739
                int r6 = r3.param1
                r4.handlerMessage(r6)
                int r6 = r3.param1
                r7 = 5
                if (r6 != r7) goto L_0x072b
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r6 = r1.mDialog
                java.lang.Object r6 = r6.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r6 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r6
                android.content.ContentResolver r6 = r6.getContentResolver()
                java.lang.String r7 = "no_signal_auto_power_off"
                int r6 = android.provider.Settings.Global.getInt(r6, r7, r15)
                if (r6 != 0) goto L_0x06d6
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r6 = r1.mDialog
                java.lang.Object r6 = r6.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r6 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r6
                android.view.Window r6 = r6.getWindow()
                r7 = 128(0x80, float:1.794E-43)
                r6.clearFlags(r7)
            L_0x06d6:
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r6 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.mainActivity
                com.mediatek.wwtv.setting.util.TVContent r6 = com.mediatek.wwtv.setting.util.TVContent.getInstance(r6)
                boolean r6 = r6.isCurrentSourceTv()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r7 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.mainActivity
                com.mediatek.wwtv.setting.util.TVContent r7 = com.mediatek.wwtv.setting.util.TVContent.getInstance(r7)
                boolean r7 = r7.isLastInputSourceVGA()
                java.lang.String r9 = "TurnkeyUiMainActivity"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r11 = "check source isCurrTv =="
                r10.append(r11)
                r10.append(r6)
                java.lang.String r11 = ",isLastVGA =="
                r10.append(r11)
                r10.append(r7)
                java.lang.String r10 = r10.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
                if (r7 == 0) goto L_0x0715
                java.lang.String r9 = "TurnkeyUiMainActivity"
                java.lang.String r10 = "check MenuMain is available "
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            L_0x0715:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r9 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r8 = r9.getComponentById(r8)
                com.mediatek.wwtv.tvcenter.nav.view.EWSDialog r8 = (com.mediatek.wwtv.tvcenter.nav.view.EWSDialog) r8
                if (r8 == 0) goto L_0x072a
                boolean r9 = r8.isVisible()
                if (r9 == 0) goto L_0x072a
                r8.dismiss()
            L_0x072a:
                goto L_0x0739
            L_0x072b:
                int r6 = r3.param1
                if (r6 != 0) goto L_0x0739
                com.mediatek.wwtv.tvcenter.nav.util.MtkTvEWSPA r6 = com.mediatek.wwtv.tvcenter.nav.util.MtkTvEWSPA.getInstance()
                r6.deleteMonitorInst(r15)
                r6.createMonitorInst(r15)
            L_0x0739:
                com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r6 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
                if (r6 == 0) goto L_0x082c
                int r6 = r3.param1
                if (r6 != r5) goto L_0x0754
                java.lang.ref.WeakReference<com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity> r5 = r1.mDialog
                java.lang.Object r5 = r5.get()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r5 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r5
                android.view.Window r5 = r5.getWindow()
                r6 = 128(0x80, float:1.794E-43)
                r5.addFlags(r6)
            L_0x0754:
                com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r5 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
                com.mediatek.wwtv.tvcenter.dvr.manager.Controller r5 = r5.getController()
                r5.onTvShow()
                goto L_0x082c
            L_0x0761:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r13)
                com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog r0 = (com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog) r0
                if (r0 == 0) goto L_0x082c
                boolean r4 = r0.isVisible()
                if (r4 == 0) goto L_0x082c
                r0.handleCallBack()
                goto L_0x082c
            L_0x0778:
                int r0 = r3.param1
                if (r0 != r11) goto L_0x07ce
                int r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getActiveCompId()
                r5 = 16777230(0x100000e, float:2.3509926E-38)
                if (r0 != r5) goto L_0x0788
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.updateActiveCompId(r9, r15)
            L_0x0788:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r0 = r0.getComponentById(r14)
                com.mediatek.wwtv.tvcenter.nav.view.BannerView r0 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r0
                if (r0 == 0) goto L_0x0797
                r0.tuneChannelAfterTuneTVSource()
            L_0x0797:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r5 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r4 = r5.getComponentById(r4)
                com.mediatek.wwtv.tvcenter.nav.view.PwdDialog r4 = (com.mediatek.wwtv.tvcenter.nav.view.PwdDialog) r4
                if (r4 == 0) goto L_0x07b0
                com.mediatek.twoworlds.tv.MtkTvPWDDialog r5 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
                int r5 = r5.PWDShow()
                if (r5 == 0) goto L_0x07b0
                r4.dismiss()
            L_0x07b0:
                com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r5 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
                com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r5 = r5.getComponentById(r12)
                com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog r5 = (com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog) r5
                if (r5 == 0) goto L_0x07c1
                int r6 = r3.param1
                r5.handleSvctxMessage(r6)
            L_0x07c1:
                com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor r6 = com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor.getInstance()
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler$4 r7 = new com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity$InternalHandler$4
                r7.<init>()
                r6.execute(r7)
                goto L_0x082c
            L_0x07ce:
                int r0 = r3.param1
                if (r0 != 0) goto L_0x07e4
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r0 = r0.isDoPIPPOPAction()
                if (r0 == 0) goto L_0x07dd
                return
            L_0x07dd:
                boolean r0 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurTaskTKUI()
                if (r0 != 0) goto L_0x082c
                return
            L_0x07e4:
                int r0 = r3.param1
                r4 = 10
                if (r0 != r4) goto L_0x0808
                int r0 = r3.param2
                r4 = 5
                if (r0 == r4) goto L_0x07f4
                int r0 = r3.param2
                r4 = 6
                if (r0 != r4) goto L_0x0808
            L_0x07f4:
                boolean r0 = com.mediatek.wwtv.setting.util.MenuConfigManager.PICTURE_MODE_dOVI
                if (r0 != 0) goto L_0x082c
                com.mediatek.wwtv.setting.util.MenuConfigManager.PICTURE_MODE_dOVI = r9
                boolean r0 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.isSetPrefCret
                if (r0 == 0) goto L_0x082c
                com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r0 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()
                if (r0 == 0) goto L_0x0807
                r0.notifyPreferenceForVideo()
            L_0x0807:
                goto L_0x082c
            L_0x0808:
                int r0 = r3.param1
                r4 = 10
                if (r0 != r4) goto L_0x082c
                int r0 = r3.param2
                r4 = 5
                if (r0 == r4) goto L_0x082c
                int r0 = r3.param2
                r4 = 6
                if (r0 == r4) goto L_0x082c
                boolean r0 = com.mediatek.wwtv.setting.util.MenuConfigManager.PICTURE_MODE_dOVI
                if (r0 == 0) goto L_0x082c
                com.mediatek.wwtv.setting.util.MenuConfigManager.PICTURE_MODE_dOVI = r15
                boolean r0 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.isSetPrefCret
                if (r0 == 0) goto L_0x082c
                com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r0 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()
                if (r0 == 0) goto L_0x082b
                r0.notifyPreferenceForVideo()
            L_0x082b:
            L_0x082c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.InternalHandler.handlerCallbackMsg(android.os.Message):void");
        }

        private void showBanner() {
            BannerView bannerView;
            boolean isLiveTVPause = ((TurnkeyUiMainActivity) this.mDialog.get()).isLiveTVPause();
            MtkLog.d(TurnkeyUiMainActivity.TAG, "MSG_CB_SVCTX_NOTIFY--->isLiveTVPause=" + isLiveTVPause);
            if (!isLiveTVPause && (bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER)) != null) {
                bannerView.showSimpleBanner();
            }
        }

        private void handlerMessages(Message msg) {
            int i = msg.what;
            if (i == 288) {
                boolean isBarkerChannel = ((Boolean) msg.obj).booleanValue();
                MtkLog.d(TurnkeyUiMainActivity.TAG, "EPG_MUTE_VIDEO_AND_AUDIO isBarkerChannel=" + isBarkerChannel);
                ((TurnkeyUiMainActivity) this.mDialog.get()).muteVideoAndAudio(isBarkerChannel);
            } else if (i == 4104) {
                MtkLog.d(TurnkeyUiMainActivity.TAG, "ChannelListDialog.MESSAGE_DEFAULT_CAN_CHANGECHANNEL_DELAY reach");
                boolean unused = TurnkeyUiMainActivity.getInstance().mCanChangeChannel = true;
            } else if (i != 4113) {
                switch (i) {
                    case 101:
                        TvInputInfo info = InputSourceManager.getInstance().getTvInputInfo("main");
                        if (info != null && !TIFChannelManager.getInstance((Context) this.mDialog.get()).isChannelsExist() && info.createSetupIntent() != null) {
                            ((TurnkeyUiMainActivity) this.mDialog.get()).startActivity(info.createSetupIntent());
                            return;
                        }
                        return;
                    case 102:
                        MtkLog.d(TurnkeyUiMainActivity.TAG, "come in MSG_SHOW_FAV_LIST_FULL_TOAST");
                        CommonIntegration.getInstance().showFavFullMsg();
                        CommonIntegration.getInstance().setShowFAVListFullToastDealy(false);
                        return;
                    case 103:
                        Commands.stopCmd();
                        if (1 == MarketRegionInfo.getCurrentMarketRegion()) {
                            MtkLog.i(TurnkeyUiMainActivity.TAG, "MSG_CLOSE_SOURCE, EASSetAndroidLaunchStatus(true)");
                            ((TurnkeyUiMainActivity) this.mDialog.get()).mEas.EASSetAndroidLaunchStatus(true);
                        }
                        if (MarketRegionInfo.isFunctionSupport(2)) {
                            MtkTvGinga.getInstance().stopGinga();
                        }
                        if (MarketRegionInfo.isFunctionSupport(13)) {
                            MtkTvMultiView.getInstance().setChgSource(false);
                            if (!((TurnkeyUiMainActivity) this.mDialog.get()).mCommonIntegration.isPipOrPopState()) {
                                InputSourceManager.getInstance().stopSession();
                            } else if (MarketRegionInfo.isFunctionSupport(26)) {
                                ((MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP)).setVisibility(4);
                                ((MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP)).setNormalTvModeWithLauncher(false);
                            }
                        }
                        if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                            MtkLog.d(TurnkeyUiMainActivity.TAG, "turnkey callback : stop dvr ");
                            StateDvrPlayback.getInstance().stopDvrFilePlay();
                        }
                        SystemProperties.set("vendor.mtk.livetv.ready", "0");
                        MtkLog.i(TurnkeyUiMainActivity.TAG, "MSG_CLOSE_SOURCE, vendor.mtk.livetv.ready = " + SystemProperties.get("vendor.mtk.livetv.ready"));
                        ((TurnkeyUiMainActivity) this.mDialog.get()).mAudioManager.abandonAudioFocus();
                        if (MarketRegionInfo.isFunctionSupport(45)) {
                            MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_AV_COND_MMP_MODE, 1);
                            return;
                        }
                        return;
                    case 104:
                        if (MtkTvInputSourceBase.INPUT_TYPE_TV.equalsIgnoreCase(InputSourceManager.getInstance().getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus()))) {
                            TIFChannelManager.getInstance(TurnkeyUiMainActivity.mainActivity).selectCurrentChannelWithTIF();
                            return;
                        }
                        return;
                    case 105:
                        TurnkeyUiMainActivity.getInstance().resetTurnkeyLayout();
                        return;
                    default:
                        return;
                }
            } else {
                MtkLog.d(TurnkeyUiMainActivity.TAG, "ChannelListDialog.FIND_CHANNELLIST ");
                TurnkeyUiMainActivity.getInstance().showEditTextAct((Intent) msg.obj);
            }
        }
    }

    public void showEditTextAct(Intent intent) {
        MtkLog.d(TAG, "showEditTextAct.FIND_CHANNELLIST ");
        startActivityForResult(intent, NavBasic.NAV_REQUEST_CODE);
    }

    private void registerTvReceiver() {
        MtkLog.d(TAG, "come in registerTvReceiver");
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("android.intent.action.ACTION_PREPARE_SHUTDOWN");
        intentFilter1.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter1.addAction(TIMESHIFT_MODE_OFF);
        intentFilter1.addAction(LIVE_SETTING_SELECT_SOURCE);
        intentFilter1.addAction(LIVE_SETTING_UPDATE_CONFLICT_SOURCELIST);
        registerReceiver(this.mReceiver, intentFilter1);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mediatek.select.TV");
        filter.addAction("com.mediatek.select.DTV");
        filter.addAction("com.mediatek.select.Composite");
        filter.addAction("com.mediatek.select.Component");
        filter.addAction("com.mediatek.select.SCART");
        filter.addAction("com.mediatek.select.HDMI1");
        filter.addAction("com.mediatek.select.HDMI2");
        filter.addAction("com.mediatek.select.HDMI3");
        filter.addAction("com.mediatek.select.HDMI4");
        filter.addAction("com.mediatek.select.VGA");
        registerReceiver(this.selectSourceReceiver, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("com.mediatek.tv.selectchannel");
        filter2.addAction("com.mediatek.tv.channelupdown");
        filter2.addAction("com.mediatek.tv.channelpre");
        registerReceiver(this.selectChannelReceiver, filter2);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("com.mediatek.tv.setup.resetdefault");
        filter3.addAction("com.mediatek.tv.factory.cleanstorage");
        filter3.addAction("com.mediatek.tv.parental.cleanall");
        registerReceiver(this.resetOrCleanReceiver, filter3);
        IntentFilter filter4 = new IntentFilter();
        filter4.addAction("com.mediatek.tv.easmsg");
        filter4.addAction("com.mediatek.tv.callcc");
        registerReceiver(this.easOrCCReceiver, filter4);
        IntentFilter filter5 = new IntentFilter();
        filter5.addAction(TifTimeshiftView.ACTION_TIMESHIFT_STOP);
        registerReceiver(this.addFacTvdiagnosticReceiver, filter5);
    }

    private void unregisterTvReceiver() {
        unregisterReceiver(this.mReceiver);
        unregisterReceiver(this.selectSourceReceiver);
        unregisterReceiver(this.selectChannelReceiver);
        unregisterReceiver(this.resetOrCleanReceiver);
        unregisterReceiver(this.easOrCCReceiver);
        unregisterReceiver(TwinkleView.screenOnReceiver);
        unregisterReceiver(this.addFacTvdiagnosticReceiver);
    }

    public LinearLayout getSundryLayout() {
        return this.mSundryLayout;
    }

    public TvSurfaceView getTvView() {
        return this.mTvView;
    }

    public TvSurfaceView getPipView() {
        return this.mPipView;
    }

    public TvBlockView getBlockScreenView() {
        return this.mBlockScreenForTuneView;
    }

    private boolean isMapSerailKeys(int keyCode) {
        if (startVssGreen[this.mVssCursor] == keyCode) {
            MtkLog.i(TAG, "keyCode:" + keyCode + "--mVssCursor:" + this.mVssCursor);
            long currentTime = SystemClock.uptimeMillis();
            this.mVssCursor = this.mVssCursor + 1;
            if (this.mVssCursor == startVssGreen.length) {
                this.mVssCursor = 0;
                if (currentTime - this.lastTime < 1500) {
                    this.lastTime = 0;
                    startVss();
                    return true;
                }
                this.lastTime = 0;
            } else {
                this.lastTime = currentTime;
            }
        } else {
            this.mKeyCursor = 0;
            this.mVssCursor = 0;
            this.lastTime = 0;
        }
        return false;
    }

    private void startVss() {
        isNormalTvState();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public static TurnkeyUiMainActivity getInstance() {
        return mainActivity;
    }

    public static void resumeTurnkeyActivity(Context content) {
        if (DestroyApp.isCurActivityTkuiMainActivity() || content == null) {
            return;
        }
        if (DvrManager.getInstance().isStopDvrNotResumeLauncher()) {
            MtkLog.i(TAG, "isStopDvrNotResumeLauncher = true ");
            return;
        }
        Intent intent = new Intent("android.mtk.intent.action.ACTION_REQUEST_TOP_RESUME");
        intent.addCategory("android.intent.category.DEFAULT");
        MtkLog.e(TAG, "resumeTurnkeyActivity");
        intent.addFlags(268435456);
        content.startActivity(intent);
    }

    public void showDVBT_THD_ConfirmDialog() {
        MtkLog.d(TAG, "showDVBT_THD_ConfirmDialog()-TKUI");
        if (!"1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
            if (CommonIntegration.isCNRegion()) {
                MtkLog.d(TAG, "showDVBT_THD_ConfirmDialog(),is CN ,not support");
            } else if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
                MtkLog.d(TAG, "showDVBT_THD_ConfirmDialog(),Not TV Source");
            } else if (ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PVR_TIMESHIFT).isVisible()) {
                MtkLog.d(TAG, "showDVBT_THD_ConfirmDialog(),PVR is Running");
            } else {
                ((TwinkleView) this.mNavCompsMagr.getComponentById(16777232)).setVisibility(4);
                SourceListView sourceListView = (SourceListView) this.mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
                if (sourceListView != null && sourceListView.isShowing()) {
                    sourceListView.dismiss();
                }
                if (getInstance() == null) {
                    return;
                }
                if (!getInstance().isResumed()) {
                    MtkLog.d(TAG, "showDVBT_THD_ConfirmDialog(),Nav Not Resumed");
                    return;
                }
                this.mConfirmDialog = new DVBT_TNTHD_ConfirmDialog(getInstance());
                this.mConfirmDialog.setHandler(mHandler);
                this.mConfirmDialog.showConfirmDialog();
            }
        }
    }

    public DVBT_TNTHD_ConfirmDialog getConfirmDialog() {
        return this.mConfirmDialog;
    }

    public void showDVBS_TKGS_UserMsgDialog(String umsg) {
        MtkLog.d(TAG, "showDVBS_TKGS_UserMsgDialog()-umsg");
        if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
            MtkLog.d(TAG, "showDVBS_TKGS_UserMsgDialog(),Not TV Source");
        } else if (ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PVR_TIMESHIFT).isVisible()) {
            MtkLog.d(TAG, "showDVBS_TKGS_UserMsgDialog(),PVR is Running");
        } else if (getInstance() == null) {
        } else {
            if (!getInstance().isResumed()) {
                MtkLog.d(TAG, "showDVBS_TKGS_UserMsgDialog(),Nav Not Resumed");
                return;
            }
            MtkLog.d(TAG, "showTKGSUserMessageDialog>>>");
            new TKGSUserMessageDialog(getInstance()).showConfirmDialog(umsg);
        }
    }

    /* access modifiers changed from: private */
    public void showDVBT_InactiveChannels_ConfirmDialog() {
        MtkLog.d(TAG, "showDVBT_InactiveChannels_ConfirmDialog()-TKUI");
        if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
            MtkLog.d(TAG, "showDVBT_InactiveChannels_ConfirmDialog(),Not TV Source");
        } else if (ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PVR_TIMESHIFT).isVisible()) {
            MtkLog.d(TAG, "showDVBT_InactiveChannels_ConfirmDialog(),PVR is Running");
        } else if (getInstance() == null) {
        } else {
            if (!getInstance().isResumed()) {
                MtkLog.d(TAG, "showDVBT_InactiveChannels_ConfirmDialog(),Nav Not Resumed");
            } else {
                new DVBT_Inactivechannel_ConfirmDialog(getInstance()).showConfirmDialog();
            }
        }
    }

    public boolean onUnhandledInputEvent(InputEvent event) {
        if (!(event instanceof KeyEvent)) {
            return false;
        }
        MtkLog.d(TAG, "onUnhandledInputEvent " + event);
        if (((KeyEvent) event).getAction() != 0) {
            return false;
        }
        onKeyDown(((KeyEvent) event).getKeyCode(), (KeyEvent) event);
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        MtkLog.d(TAG, "dispatchKeyEvent event:" + event);
        if (!MarketRegionInfo.isFunctionSupport(35)) {
            TvInputInfo tvInputInfo = InputSourceManager.getInstance().getTvInputInfo("main");
            if (tvInputInfo == null || tvInputInfo.getType() != 1007 || tvInputInfo.getHdmiDeviceInfo() == null) {
                if (event.getKeyCode() == 82 && StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                    DvrManager.getInstance().restoreToDefault((StateBase) StateDvrFileList.getInstance());
                }
                return super.dispatchKeyEvent(event);
            }
            MtkLog.e(TAG, "Cec Device connected. dispatch key to cec.");
            MenuOptionMain menuOption = (MenuOptionMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG);
            boolean isInfoKey = KeyMap.getKeyCode(event) == 165;
            if ((menuOption != null && menuOption.isShowing()) || isInfoKey) {
                return super.dispatchKeyEvent(event);
            }
        }
        if (CommonIntegration.getInstance().isPipOrPopState()) {
            return super.dispatchKeyEvent(event);
        }
        if (CommonIntegration.getInstance().isCurrentSourceBlockEx() && event.getKeyCode() == 23) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = KeyMap.getKeyCode(event);
        if (!MarketRegionInfo.isFunctionSupport(35)) {
            if (!this.mBlackListForKeyCodeToCEC.isEmpty()) {
                if (this.mBlackListForKeyCodeToCEC.contains(Integer.valueOf(keyCode))) {
                    return super.dispatchKeyEvent(event);
                }
            } else if (Constants.BLACKLIST_KEYCODE_TO_TIS.contains(Integer.valueOf(keyCode))) {
                return super.dispatchKeyEvent(event);
            }
        }
        ZoomTipView mZoomTip = (ZoomTipView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
        if (IntegrationZoom.getInstance(this).screenModeZoomShow() && IntegrationZoom.getInstance(this).getCurrentZoom() == 2 && mZoomTip != null && mZoomTip.getVisibility() == 0 && (keyCode == 20 || keyCode == 19 || keyCode == 21 || keyCode == 22)) {
            MtkLog.e(TAG, "if zoom show , send key to tv not cec");
            return super.dispatchKeyEvent(event);
        } else if (dispatchKeyEventToSession(event) || super.dispatchKeyEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    private void dismissPwddialog() {
        PwdDialog mPWDDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
        if (mPWDDialog != null && mPWDDialog.isVisible()) {
            mPWDDialog.dismiss();
        }
    }

    private boolean dispatchKeyEventToSession(KeyEvent event) {
        KeyEvent keyEvent = event;
        boolean handled = false;
        if (!(this.mTvView == null || MtkTvPWDDialog.getInstance().PWDShow() == 0)) {
            if (event.getKeyCode() == 93) {
                handled = this.mTvView.dispatchKeyEvent(new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(), Cea708CCParser.Const.CODE_C1_CW1, event.getRepeatCount(), event.getMetaState(), event.getDeviceId(), event.getScanCode(), event.getFlags(), event.getSource()));
            } else if (event.getKeyCode() == 213) {
                handled = this.mTvView.dispatchKeyEvent(new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(), 222, event.getRepeatCount(), event.getMetaState(), event.getDeviceId(), event.getScanCode(), event.getFlags(), event.getSource()));
            } else {
                handled = this.mTvView.dispatchKeyEvent(keyEvent);
            }
        }
        MtkLog.d(TAG, "dispatchKeyEventToSession event (" + keyEvent + ") handled=(" + handled + ").");
        return handled;
    }

    public TifTimeShiftManager getmTifTimeShiftManager() {
        return this.mTifTimeShiftManager;
    }

    public MenuOptionMain getTvOptionsManager() {
        if (this.mNavCompsMagr != null) {
            return (MenuOptionMain) this.mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG);
        }
        return null;
    }

    public void startActivitySafe(Intent intent) {
        startActivity(intent);
    }

    public boolean showEPG() {
        boolean show = false;
        if (this.mCommonIntegration.isCurrentSourceTv() || this.mCommonIntegration.is3rdTVSource()) {
            if (MarketRegionInfo.isFunctionSupport(30) && StateDvr.getInstance() != null && StateDvr.getInstance().isRunning() && StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isRunning()) {
                return false;
            }
            if (SaveValue.getInstance(mainActivity).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
                Toast.makeText(mainActivity, "timeshift recording!", 0).show();
                return true;
            } else if (!CommonIntegration.getInstance().isMenuInputTvBlock() || MarketRegionInfo.getCurrentMarketRegion() == 1) {
                Activity act = DestroyApp.getTopActivity();
                if (act == null || (!(act instanceof EPGEuActivity) && !(act instanceof EPGSaActivity) && !(act instanceof EPGCnActivity) && !(act instanceof EPGUsActivity))) {
                    show = EPGManager.getInstance(this).startEpg(this, NavBasic.NAV_REQUEST_CODE);
                    if (!show && !this.mBannerView.isVisible()) {
                        this.mBannerView.isKeyHandler(KeyMap.KEYCODE_MTKIR_INFO);
                    }
                } else {
                    MtkLog.d(TAG, "DestroyApp.getTopActivity(): " + act.getComponentName());
                    Toast.makeText(this, "Please wait ...", 0).show();
                    return false;
                }
            } else if (!this.mBannerView.isVisible()) {
                this.mBannerView.showSimpleBanner();
            }
        }
        return show;
    }

    public void sendMuteMsg(boolean isMute) {
        mHandler.removeMessages(EPGConfig.EPG_MUTE_VIDEO_AND_AUDIO);
        Message msg = Message.obtain();
        msg.what = EPGConfig.EPG_MUTE_VIDEO_AND_AUDIO;
        msg.obj = Boolean.valueOf(isMute);
        mHandler.sendMessage(msg);
    }

    /* access modifiers changed from: private */
    public void muteVideoAndAudio(boolean isBarkerChannel) {
        int i = 8;
        if (isBarkerChannel && !this.mhasMuteVideo) {
            this.mhasMuteVideo = true;
            TvBlockView tvBlockView = this.mBlockScreenForTuneView;
            if (isBarkerChannel) {
                i = 0;
            }
            tvBlockView.setVisibility(i, 3);
            this.mAudioManager.muteTVAudio(2);
        } else if (!isBarkerChannel && this.mhasMuteVideo) {
            this.mhasMuteVideo = false;
            TvBlockView tvBlockView2 = this.mBlockScreenForTuneView;
            if (isBarkerChannel) {
                i = 0;
            }
            tvBlockView2.setVisibility(i, 3);
            this.mAudioManager.unmuteTVAudio(2);
        }
    }

    public void onShowLoading() {
        MtkLog.d("TvInputCallbackMgr", "onShowLoading " + this.mProgressBar.getVisibility());
        SourceListView sourceListView = (SourceListView) this.mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
        if (sourceListView != null) {
            sourceListView.dismissWaitDialog();
        }
        if (this.mProgressBar != null && this.mProgressBar.getVisibility() != 0) {
            this.mProgressBar.setVisibility(0);
        }
    }

    public void onHideLoading() {
        MtkLog.d("TvInputCallbackMgr", "onHideLoading " + this.mProgressBar.getVisibility());
        if (this.mProgressBar != null && this.mProgressBar.getVisibility() == 0) {
            this.mProgressBar.setVisibility(8);
        }
    }

    public void onPinChecked(boolean checked, int type, String rating) {
        if (type == 7 && checked) {
            int tuneMode = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src");
            if (tuneMode == 0) {
                Intent intent = new Intent(this.mContext, ScanDialogActivity.class);
                intent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBT);
                this.mContext.startActivity(intent);
            } else if (tuneMode == 1) {
                Intent intent2 = new Intent(this.mContext, ScanViewActivity.class);
                intent2.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBC);
                this.mContext.startActivity(intent2);
            }
            MtkLog.d("onPinChecked showDTVScan");
        }
    }
}
