package com.mediatek.wwtv.setting.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.media.tv.TvTrackInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.tv.dialog.PinDialogFragment;
import com.android.tv.menu.customization.CustomizeChanelListActivity;
import com.android.tv.onboarding.SetupSourceActivity;
import com.android.tv.ui.sidepanel.SideFragmentManager;
import com.android.tv.util.LicenseUtils;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.wwtv.setting.ChannelInfoActivity;
import com.mediatek.wwtv.setting.LiveTvSetting;
import com.mediatek.wwtv.setting.SatActivity;
import com.mediatek.wwtv.setting.TKGSSettingActivity;
import com.mediatek.wwtv.setting.TvSettingsActivity;
import com.mediatek.wwtv.setting.WebActivity;
import com.mediatek.wwtv.setting.base.scan.adapter.BissListAdapter;
import com.mediatek.wwtv.setting.base.scan.model.CableOperator;
import com.mediatek.wwtv.setting.base.scan.model.DVBCCNScanner;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.ui.ScanDialogActivity;
import com.mediatek.wwtv.setting.base.scan.ui.ScanThirdlyDialog;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.setting.fragments.SatListFrag;
import com.mediatek.wwtv.setting.parental.ContentRatingSystem;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.MenuSystemInfo;
import com.mediatek.wwtv.setting.util.RegionConst;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.util.TransItem;
import com.mediatek.wwtv.setting.view.BissKeyEditDialog;
import com.mediatek.wwtv.setting.view.BissKeyPreferenceDialog;
import com.mediatek.wwtv.setting.view.DivxDialog;
import com.mediatek.wwtv.setting.view.FacPreset;
import com.mediatek.wwtv.setting.view.FacSetup;
import com.mediatek.wwtv.setting.view.MenuMjcDemoDialog;
import com.mediatek.wwtv.setting.view.NetUpdateGider;
import com.mediatek.wwtv.setting.view.PostalCodeEditDialog;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.view.DatePicker;
import com.mediatek.wwtv.setting.widget.view.DownloadFirmwareDialog;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.setting.widget.view.ScheduleListDialog;
import com.mediatek.wwtv.setting.widget.view.TimePicker;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeshiftView;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SettingsPreferenceScreen {
    public static final String FACTORY_HEAD = "factory__";
    static final int MESSAGE_SEND_RESET = 35;
    private static final int SCREEN_MODE_DOT_BY_DOT = 6;
    private static final String TAG = "SettingsPreferenceScreen";
    public static Preference bissPreference;
    public static int block = -1;
    public static int dimIndex;
    public static boolean isSetPrefCret = false;
    /* access modifiers changed from: private */
    public static CommonIntegration mNavIntegration = null;
    private static SettingsPreferenceScreen mPreference = null;
    public static int reginIndex;
    public static Preference tkgsSetting;
    public static Preference tvFirstLanguageEU;
    public static Preference tvFirstVoice;
    public static Preference tvSecondLanguageEU;
    private Preference HUEPre;
    private String ItemName;
    private final String KEY_PRE_ORDER_BACK_LIGHT = "back_light";
    private final String KEY_PRE_ORDER_BRIGHTNESS = "brightness";
    private final String KEY_PRE_ORDER_CONTRAST = "contrast";
    private final String KEY_PRE_ORDER_DOVI_RESET = "reset";
    private final String KEY_PRE_ORDER_HUE = "hue";
    private final String KEY_PRE_ORDER_PICMODE = "picmode";
    private final String KEY_PRE_ORDER_SATURATION = "saturation";
    private final String KEY_PRE_ORDER_SHARPNESS = "sharpness";
    /* access modifiers changed from: private */
    public final MtkTvAppTVBase appTV;
    Preference audioSpdifDelay;
    LiveTVDialog autoAdjustDialog;
    /* access modifiers changed from: private */
    public int autoTimeOut = 0;
    private Preference backLightPre;
    public List<BissListAdapter.BissItem> bissitems;
    private PreferenceScreen bisskeyGroup;
    private ArrayList<Preference> bisskeyPreferences;
    private String[] blueMute;
    private String[] blueStretch;
    /* access modifiers changed from: private */
    public int bnum = 0;
    private Preference brightnessPre;
    private Preference chanelDecode;
    private Preference channelEdit;
    private Preference channelMove;
    Preference channelScanItem;
    private Preference channelSort;
    private Preference channelskip;
    /* access modifiers changed from: private */
    public LiveTVDialog cleanDialog;
    private Preference cleanList;
    private String[] colorTemperature2;
    private Preference contrastPre;
    /* access modifiers changed from: private */
    public Preference cwPreference;
    private String[] dPLanguage;
    private String[] dTLanguage;
    Preference demoPre;
    String dim;
    private String[] dnr;
    private String[] doViNoti;
    private String[] doViRestore;
    private String[] fleshTone;
    /* access modifiers changed from: private */
    public Preference frePreference;
    private String[] gameMode;
    private String[] gamma;
    private boolean haveScreenMode = false;
    private String[] hdmiMode;
    private String[] hdr;
    boolean isCamCardPlugin = false;
    private String[] localContrast;
    private String[] luma;
    String[] m3D2DArr;
    String[] m3DImgSafetyArr;
    String[] m3DLrSwitchArr;
    private String[] m3DModeArr;
    private String[] m3DModeStrArr;
    String[] m3DNavArr;
    private String[] mBlackBar;
    public final Handler mCAMHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TvCallbackData data = (TvCallbackData) msg.obj;
            if (data == null) {
                MtkLog.d(SettingsPreferenceScreen.TAG, "msg data null");
                return;
            }
            MtkLog.d(SettingsPreferenceScreen.TAG, "cam msg.what:" + msg.what + ",data.param2==" + data.param2);
            if (msg.what == 1879048215) {
                switch (data.param2) {
                    case 1:
                        MtkLog.d(SettingsPreferenceScreen.TAG, "cam card insert");
                        SettingsPreferenceScreen.this.isCamCardPlugin = true;
                        return;
                    case 2:
                        MtkLog.d(SettingsPreferenceScreen.TAG, "cam card remove!!");
                        SettingsPreferenceScreen.this.isCamCardPlugin = false;
                        return;
                    default:
                        return;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public LiveTVDialog mCleanAllConfirmDialog;
    Preference.OnPreferenceClickListener mClickListener = new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
            int pola;
            String threePry;
            if ("Horizonal".equals(SettingsPreferenceScreen.this.ploaPreference.getSummary().toString())) {
                pola = 0;
            } else {
                pola = 1;
            }
            Log.d(SettingsPreferenceScreen.TAG, "click set pola:" + pola);
            int freq = Integer.valueOf(SettingsPreferenceScreen.this.frePreference.getSummary().toString()).intValue();
            int rate = Integer.valueOf(SettingsPreferenceScreen.this.symPreference.getSummary().toString()).intValue();
            int progId = Integer.valueOf(SettingsPreferenceScreen.this.progPreference.getSummary().toString()).intValue();
            if (pola <= 0) {
                threePry = freq + "H" + rate;
            } else {
                threePry = freq + MtkTvRatingConvert2Goo.SUB_RATING_STR_V + rate;
            }
            Log.d(SettingsPreferenceScreen.TAG, "do save data...threePry==" + threePry);
            String cwkey = SettingsPreferenceScreen.this.cwPreference.getSummary().toString();
            if (preference.getKey().equals(MenuConfigManager.BISS_KEY_ITEM_SAVE)) {
                if (SettingsPreferenceScreen.this.mHelper.operateBissKeyinfo(new BissListAdapter.BissItem(-1, progId, threePry, cwkey), 0) == -2) {
                    Toast.makeText(SettingsPreferenceScreen.this.mContext, "BissKey Existed!", 0).show();
                } else {
                    SettingsPreferenceScreen.this.updateBissKey();
                }
            } else if (preference.getKey().equals(MenuConfigManager.BISS_KEY_ITEM_UPDATE)) {
                if (SettingsPreferenceScreen.this.mHelper.operateBissKeyinfo(new BissListAdapter.BissItem(SettingsPreferenceScreen.this.bnum, progId, threePry, cwkey), 1) == -2) {
                    Toast.makeText(SettingsPreferenceScreen.this.mContext, "BissKey Existed!", 0).show();
                } else {
                    SettingsPreferenceScreen.this.updateBissKey();
                }
            } else if (preference.getKey().equals(MenuConfigManager.BISS_KEY_ITEM_DELETE)) {
                SettingsPreferenceScreen.this.mHelper.operateBissKeyinfo(new BissListAdapter.BissItem(SettingsPreferenceScreen.this.bnum, progId, threePry, cwkey), 2);
                SettingsPreferenceScreen.this.updateBissKey();
            }
            return false;
        }
    };
    /* access modifiers changed from: private */
    public final MenuConfigManager mConfigManager;
    /* access modifiers changed from: private */
    public Context mContext;
    protected MenuDataHelper mDataHelper;
    private DownloadFirmwareDialog mDownloadFirmwareDialog;
    /* access modifiers changed from: private */
    public final EditChannel mEditChannel;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            boolean flag;
            int i = msg.what;
            if (i != 35) {
                switch (i) {
                    case 115:
                    case 116:
                        Preference mobj = (Preference) msg.obj;
                        MtkLog.d(SettingsPreferenceScreen.TAG, "MESSAGE_AUTOADJUST fro id:" + mobj.getKey());
                        SettingsPreferenceScreen.access$808(SettingsPreferenceScreen.this);
                        if (mobj.getKey().equals(MenuConfigManager.FV_AUTOPHASE)) {
                            flag = SettingsPreferenceScreen.this.appTV.AutoClockPhasePostionCondSuccess(SettingsPreferenceScreen.mNavIntegration.getCurrentFocus());
                        } else {
                            flag = SettingsPreferenceScreen.this.appTV.AutoColorCondSuccess(SettingsPreferenceScreen.mNavIntegration.getCurrentFocus());
                        }
                        if (flag || SettingsPreferenceScreen.this.autoTimeOut >= 5) {
                            int unused = SettingsPreferenceScreen.this.autoTimeOut = 0;
                            SettingsPreferenceScreen.this.autoAdjustDialog.dismiss();
                            if (mobj.getKey().equals(MenuConfigManager.AUTO_ADJUST)) {
                                PreferenceData.getInstance(SettingsPreferenceScreen.this.mContext.getApplicationContext()).invalidate(mobj.getKey(), 0);
                            }
                            SettingsPreferenceScreen.this.mHandler.removeMessages(115);
                            SettingsPreferenceScreen.this.mHandler.removeMessages(116);
                            return;
                        }
                        Message message = obtainMessage();
                        message.what = 115;
                        message.obj = mobj;
                        sendMessageDelayed(message, 1000);
                        return;
                    default:
                        return;
                }
            } else {
                MtkLog.d("gitmaster", "before at sned resetbroad==" + System.currentTimeMillis());
                SettingsUtil.sendResetBroadcast(SettingsPreferenceScreen.this.mContext);
                if (SettingsPreferenceScreen.this.pdialog != null) {
                    SettingsPreferenceScreen.this.pdialog.dismiss();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final MenuDataHelper mHelper;
    /* access modifiers changed from: private */
    public PreferenceManager mPreferenceManager;
    Preference mSatelliteRescan;
    public ScanPreferenceClickListener mScanPreferenceClickListener = new ScanPreferenceClickListener();
    private String[] mScreenMode;
    /* access modifiers changed from: private */
    public Preference mScreenModeItem;
    private SideFragmentManager mSideFragmentManager;
    public final Handler mSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1879048198) {
                MtkLog.d(SettingsPreferenceScreen.TAG, "msg.what:" + msg.what);
                TvCallbackData backData = (TvCallbackData) msg.obj;
                MtkLog.d(SettingsPreferenceScreen.TAG, "backData.param1:" + backData.param1 + ",param2:" + backData.param2);
                int i = backData.param1;
                if (i != 9) {
                    if (i != 20) {
                        if (i != 37) {
                            switch (i) {
                                case 4:
                                    Log.d(SettingsPreferenceScreen.TAG, "SettingsUtil.SVCTX_NTFY_CODE_SIGNAL_LOCKED");
                                    if (SettingsPreferenceScreen.this.mScreenModeItem != null && !SettingsPreferenceScreen.this.isHaveScreenModeItem()) {
                                        if (SettingsPreferenceScreen.this.mTV.isHaveScreenMode()) {
                                            SettingsPreferenceScreen.this.reloadScreenMode();
                                            return;
                                        }
                                        SettingsPreferenceScreen.this.mainPreferenceScreen.removePreference(SettingsPreferenceScreen.this.mScreenModeItem);
                                        SettingsPreferenceScreen.this.mScreenModeItem.setEnabled(false);
                                        return;
                                    }
                                    return;
                                case 5:
                                    if (SettingsPreferenceScreen.this.mScreenModeItem != null && SettingsPreferenceScreen.this.isHaveScreenModeItem()) {
                                        SettingsPreferenceScreen.this.mainPreferenceScreen.removePreference(SettingsPreferenceScreen.this.mScreenModeItem);
                                        SettingsPreferenceScreen.this.mScreenModeItem.setEnabled(false);
                                    }
                                    Log.d(SettingsPreferenceScreen.TAG, "SettingsUtil.SVCTX_NTFY_CODE_SIGNAL_LOSS");
                                    return;
                                default:
                                    switch (i) {
                                        case 22:
                                            break;
                                        case 23:
                                        case 24:
                                        case 25:
                                        case 26:
                                        case 27:
                                            MtkLog.d("picasa", "SCRAMBLED param1 ==" + backData.param1);
                                            if (SettingsPreferenceScreen.this.mScreenModeItem == null) {
                                                return;
                                            }
                                            if (SettingsPreferenceScreen.this.isHaveScreenModeItem()) {
                                                MtkLog.d("picasa", "SCRAMBLED --- isCamCardPlugin ==" + SettingsPreferenceScreen.this.isCamCardPlugin);
                                                if (!SettingsPreferenceScreen.this.isCamCardPlugin) {
                                                    SettingsPreferenceScreen.this.mainPreferenceScreen.removePreference(SettingsPreferenceScreen.this.mScreenModeItem);
                                                    SettingsPreferenceScreen.this.mScreenModeItem.setEnabled(false);
                                                    return;
                                                }
                                                return;
                                            }
                                            MtkLog.d("picasa", "SCRAMBLED ### isCamCardPlugin ==" + SettingsPreferenceScreen.this.isCamCardPlugin);
                                            return;
                                        default:
                                            return;
                                    }
                            }
                        } else {
                            MtkLog.d("picasa", "SVCTX_NTFY_CODE_VIDEO_FMT_UPDATE = " + backData.param1);
                            if (SettingsPreferenceScreen.this.videohdr == null) {
                                return;
                            }
                            if (SettingsPreferenceScreen.this.mTV.iCurrentInputSourceHasSignal()) {
                                MtkLog.d(SettingsPreferenceScreen.TAG, "MenuConfigManager.VIDEO_HDR .isConfigVisible " + SettingsPreferenceScreen.this.mTV.isConfigVisible("g_video__vid_hdr"));
                                if (SettingsPreferenceScreen.this.mTV.isConfigVisible("g_video__vid_hdr")) {
                                    MtkLog.d(SettingsPreferenceScreen.TAG, "MenuConfigManager.VIDEO_HDR hidetrue");
                                    if (SettingsPreferenceScreen.this.isHaveHDRItem()) {
                                        SettingsPreferenceScreen.this.mainPreferenceScreen.removePreference(SettingsPreferenceScreen.this.videohdr);
                                        SettingsPreferenceScreen.this.videohdr.setEnabled(false);
                                        return;
                                    }
                                    return;
                                } else if (!SettingsPreferenceScreen.this.isHaveHDRItem()) {
                                    SettingsPreferenceScreen.this.mainPreferenceScreen.addPreference(SettingsPreferenceScreen.this.videohdr);
                                    SettingsPreferenceScreen.this.videohdr.setEnabled(true);
                                    return;
                                } else {
                                    return;
                                }
                            } else if (!SettingsPreferenceScreen.this.isHaveHDRItem()) {
                                SettingsPreferenceScreen.this.mainPreferenceScreen.addPreference(SettingsPreferenceScreen.this.videohdr);
                                SettingsPreferenceScreen.this.videohdr.setEnabled(true);
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                    MtkLog.d("picasa", "CODE param1 ==" + backData.param1);
                    if (!SettingsPreferenceScreen.this.isHaveScreenModeItem()) {
                        MtkLog.d("picasa", "CODE ### isCamCardPlugin ==" + SettingsPreferenceScreen.this.isCamCardPlugin);
                        if (SettingsPreferenceScreen.this.isCamCardPlugin) {
                            SettingsPreferenceScreen.this.mainPreferenceScreen.addPreference(SettingsPreferenceScreen.this.mScreenModeItem);
                            SettingsPreferenceScreen.this.mScreenModeItem.setEnabled(true);
                            return;
                        }
                        return;
                    }
                    return;
                }
                Log.d(SettingsPreferenceScreen.TAG, "SettingsUtil.SVCTX_NTFY_CODE_SERVICE_BLOCKED");
                if (SettingsPreferenceScreen.this.mScreenModeItem != null && SettingsPreferenceScreen.this.isHaveScreenModeItem()) {
                    SettingsPreferenceScreen.this.mainPreferenceScreen.removePreference(SettingsPreferenceScreen.this.mScreenModeItem);
                    SettingsPreferenceScreen.this.mScreenModeItem.setEnabled(false);
                }
            }
        }
    };
    private String[] mSuperResolution;
    /* access modifiers changed from: private */
    public final TVContent mTV;
    PreferenceScreen mainPreferenceScreen;
    Preference.OnPreferenceChangeListener mvisuallyimpairedChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            MtkLog.d(SettingsPreferenceScreen.TAG, "onPreferenceChange " + preference + "," + preference.getKey() + "," + newValue);
            int tempvalue = -1;
            if (newValue instanceof Boolean) {
                MtkLog.d(SettingsPreferenceScreen.TAG, "instanceof Boolean");
                tempvalue = ((Boolean) newValue).booleanValue();
            } else if (newValue instanceof String) {
                tempvalue = Integer.parseInt((String) newValue);
            }
            if (preference.getKey().startsWith("g_audio__aud_ad_speaker")) {
                if (tempvalue != 0) {
                    SettingsPreferenceScreen.this.visuallyvolume.setEnabled(true);
                } else if (SettingsPreferenceScreen.this.mConfigManager.getDefault("g_audio__aud_ad_hdphone") == 0) {
                    SettingsPreferenceScreen.this.visuallyvolume.setEnabled(false);
                } else {
                    SettingsPreferenceScreen.this.visuallyvolume.setEnabled(true);
                }
                SettingsPreferenceScreen.this.mConfigManager.setValue(preference.getKey(), tempvalue);
            } else if (preference.getKey().startsWith("g_audio__aud_ad_hdphone")) {
                if (tempvalue != 0) {
                    SettingsPreferenceScreen.this.visuallyvolume.setEnabled(true);
                } else if (SettingsPreferenceScreen.this.mConfigManager.getDefault("g_audio__aud_ad_speaker") == 0) {
                    SettingsPreferenceScreen.this.visuallyvolume.setEnabled(false);
                } else {
                    SettingsPreferenceScreen.this.visuallyvolume.setEnabled(true);
                }
                SettingsPreferenceScreen.this.mConfigManager.setValue(preference.getKey(), tempvalue);
            } else if (preference.getKey().startsWith("g_audio__spdif")) {
                if (CommonIntegration.isEURegion()) {
                    if (tempvalue == 2) {
                        SettingsPreferenceScreen.this.audioSpdifDelay.setEnabled(true);
                    } else {
                        SettingsPreferenceScreen.this.audioSpdifDelay.setEnabled(false);
                    }
                } else if (CommonIntegration.isCNRegion()) {
                    if (tempvalue == 0) {
                        SettingsPreferenceScreen.this.audioSpdifDelay.setEnabled(false);
                    } else {
                        SettingsPreferenceScreen.this.audioSpdifDelay.setEnabled(true);
                    }
                }
                SettingsPreferenceScreen.this.mConfigManager.setValue(preference.getKey(), tempvalue);
            } else if (preference.getKey().startsWith("g_video__vid_mjc_demo")) {
                Context themedContext = SettingsPreferenceScreen.this.mPreferenceManager.getContext();
                PreferenceUtil instance = PreferenceUtil.getInstance(themedContext);
                SettingsPreferenceScreen.this.mConfigManager.setValue(preference.getKey(), tempvalue);
                MenuMjcDemoDialog dialog = new MenuMjcDemoDialog(themedContext);
                DialogPreference demoPretemp = (DialogPreference) SettingsPreferenceScreen.this.demoPre;
                demoPretemp.setDialog(dialog);
                SettingsPreferenceScreen.this.demoPre = demoPretemp;
            }
            PreferenceData.getInstance(SettingsPreferenceScreen.this.mContext.getApplicationContext()).invalidate(preference.getKey(), newValue);
            return true;
        }
    };
    private TurnkeyCommDialog netWorkUpgradeDialog;
    private List<Preference> openVChipLevels = new ArrayList();
    private Preference openVchip;
    /* access modifiers changed from: private */
    public ProgressDialog pdialog = null;
    private String[] pictureMode;
    private Preference pictureModePre;
    /* access modifiers changed from: private */
    public Preference ploaPreference;
    private String[] pqsplitdemoMode;
    private HashMap<String, Integer> preOrder;
    /* access modifiers changed from: private */
    public Preference progPreference;
    String regin;
    public PreferenceScreen regionSettingScreen;
    private Preference reset_setting;
    private Preference saChannelEdit;
    private Preference saChannelFine;
    SatListFrag satListFrag = new SatListFrag();
    Preference satelliteRescans;
    List<SatelliteInfo> satellites;
    private Preference saturationPre;
    private Preference sharpnessPre;
    Preference spdifType;
    /* access modifiers changed from: private */
    public Preference symPreference;
    private String[] tPLevel;
    private final String tempDate = "def";
    Preference tkgsResetTabver;
    private String[] tunerModeStrEu;
    private Preference updateScan;
    private PreferenceUtil util;
    private String[] vgaMode;
    private String[] videoDiFilm;
    private String[] videoEffect;
    private String[] videoMjcMode;
    private String[] videoMpegNr;
    /* access modifiers changed from: private */
    public Preference videohdr;
    Preference visuallyImpaired = null;
    Preference visuallyImpairedAudioInfo = null;
    Preference visuallyvolume = null;

    static /* synthetic */ int access$808(SettingsPreferenceScreen x0) {
        int i = x0.autoTimeOut;
        x0.autoTimeOut = i + 1;
        return i;
    }

    private SettingsPreferenceScreen(Context context, PreferenceManager manager) {
        this.mContext = context;
        this.mPreferenceManager = manager;
        this.mHelper = MenuDataHelper.getInstance(this.mContext);
        this.mTV = TVContent.getInstance(this.mContext);
        this.mDataHelper = MenuDataHelper.getInstance(this.mContext);
        this.mEditChannel = EditChannel.getInstance(this.mContext);
        this.mConfigManager = MenuConfigManager.getInstance(this.mContext);
        mNavIntegration = CommonIntegration.getInstance();
        this.appTV = new MtkTvAppTVBase();
        this.bisskeyPreferences = new ArrayList<>();
        this.mSideFragmentManager = new SideFragmentManager((LiveTvSetting) context, (Runnable) null, (Runnable) null);
    }

    public static SettingsPreferenceScreen getInstance(Context context, PreferenceManager manager) {
        if (mPreference == null) {
            mPreference = new SettingsPreferenceScreen(context, manager);
            isSetPrefCret = true;
        }
        mPreference.mContext = context;
        mPreference.mPreferenceManager = manager;
        return mPreference;
    }

    public static SettingsPreferenceScreen getInstance() {
        if (mPreference != null) {
            return mPreference;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void reloadScreenMode() {
        this.mainPreferenceScreen.addPreference(this.mScreenModeItem);
        this.mScreenModeItem.setEnabled(true);
    }

    public PreferenceScreen getMainScreen() {
        MtkLog.d(TAG, "isCurrentSourceATV" + this.mTV.isCurrentSourceATV());
        Context themedContext = this.mPreferenceManager.getContext();
        this.mainPreferenceScreen = this.mPreferenceManager.createPreferenceScreen(themedContext);
        this.mainPreferenceScreen.setTitle((int) R.string.menu_advanced_options);
        boolean isDvrPlaybackState = false;
        if ((DvrManager.getInstance().getState() instanceof StateDvrPlayback) && DvrManager.getInstance().getState().isRunning()) {
            isDvrPlaybackState = true;
        }
        PreferenceCategory audio = new PreferenceCategory(themedContext);
        audio.setTitle((int) R.string.menu_tab_audio);
        audio.setKey(audio.getTitle().toString());
        this.mainPreferenceScreen.addPreference(audio);
        addAudioSettings(this.mainPreferenceScreen, themedContext, audio);
        PreferenceCategory setup = new PreferenceCategory(themedContext);
        setup.setTitle((int) R.string.menu_tab_setup);
        setup.setKey(setup.getTitle().toString());
        if (!isDvrPlaybackState) {
            this.mainPreferenceScreen.addPreference(setup);
            addSetupSettings(this.mainPreferenceScreen, themedContext);
        }
        return this.mainPreferenceScreen;
    }

    private boolean isTVSource() {
        int type = SaveValue.readWorldInputType(this.mContext);
        MtkLog.d(TAG, "isTVSource, input type = " + type);
        if (type == 11 || type == 13 || type == 12) {
            return true;
        }
        return false;
    }

    private int getIndexOfMtsMode(String[] modes, String mode) {
        for (int i = 0; i < modes.length; i++) {
            if (mode.equalsIgnoreCase(modes[i])) {
                MtkLog.d(TAG, "getIndexOfMtsMode=" + i);
                return i;
            }
        }
        return 0;
    }

    public PreferenceScreen getChannelMainScreen() {
        int defaultAudio;
        String[] multiAudio;
        boolean isTVSource = isTVSource();
        Context themedContext = this.mPreferenceManager.getContext();
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.mainPreferenceScreen = this.mPreferenceManager.createPreferenceScreen(themedContext);
        this.mainPreferenceScreen.setTitle((int) R.string.menu_channel);
        boolean isPvrOrTimeShiftRunning = this.mTV.isCanScan();
        if (isTVSource && !this.mTV.isTvInputBlock()) {
            if (MarketRegionInfo.getCurrentMarketRegion() == 1) {
                Preference channelSource = util2.createPreference(MenuConfigManager.CHANNEL_CHANNEL_SOURCES, (int) R.string.menu_channel_channel_sources, new Intent(themedContext, SetupSourceActivity.class));
                this.mainPreferenceScreen.addPreference(channelSource);
                channelSource.setEnabled(!isPvrOrTimeShiftRunning);
            } else if (MarketRegionInfo.getCurrentMarketRegion() == 3) {
                Preference tvChannel = util2.createPreference(MenuConfigManager.TV_EU_CHANNEL, (int) R.string.menu_tv_channels);
                this.mainPreferenceScreen.addPreference(tvChannel);
                tvChannel.setEnabled(!isPvrOrTimeShiftRunning);
            } else {
                Preference saChannel = util2.createPreference(MenuConfigManager.TV_CHANNEL, (int) R.string.menu_tv_channels);
                this.mainPreferenceScreen.addPreference(saChannel);
                saChannel.setEnabled(!isPvrOrTimeShiftRunning);
            }
            if (CommonIntegration.isUSRegion()) {
                Preference customizeChannelList = util2.createPreference(MenuConfigManager.CHANNEL_CUSTOMIZE_CHANNEL_LIST, (int) R.string.menu_channel_customize_channel_list, new Intent(themedContext, CustomizeChanelListActivity.class));
                customizeChannelList.setSummary((int) R.string.menu_channel_customize_channel_list_summary);
                this.mainPreferenceScreen.addPreference(customizeChannelList);
                if (mNavIntegration.hasActiveChannel(true)) {
                    customizeChannelList.setEnabled(true);
                } else {
                    customizeChannelList.setEnabled(false);
                }
            }
            this.tunerModeStrEu = CommonIntegration.getInstanceWithContext(themedContext).getTunerModes();
            String[] tunerModeValues = CommonIntegration.getInstanceWithContext(themedContext).getTunerModeValues();
            int tunerMode = this.mConfigManager.getDefault("g_bs__bs_src");
            String[] strArr = this.tunerModeStrEu;
            int i = tunerMode;
            Preference channelInstallationMode = util2.createListPreference("g_bs__bs_src", (int) R.string.menu_channel_channel_installation_mode, true, strArr, tunerModeValues, tunerMode + "");
            this.mainPreferenceScreen.addPreference(channelInstallationMode);
            channelInstallationMode.setEnabled(isPvrOrTimeShiftRunning ^ true);
            MtkLog.d(TAG, "Tuner mode size=" + tunerModeValues.length);
            if (tunerModeValues.length <= 1 || ((MarketRegionInfo.isFunctionSupport(16) && this.mTV.isCurrentSourceATV()) || this.mTV.isChineseTWN() || this.mTV.isHKRegion())) {
                this.mainPreferenceScreen.removePreference(channelInstallationMode);
            }
            if (CommonIntegration.isEURegion()) {
                this.mainPreferenceScreen.addPreference(util2.createSwitchPreference("g_misc__auto_svc_update", (int) R.string.menu_setup_auto_channel_update, this.mTV.getConfigValue("g_misc__auto_svc_update") == 1));
                this.mainPreferenceScreen.addPreference(util2.createSwitchPreference("g_menu__ch_update_msg", (int) R.string.menu_setup_channel_update_msg, this.mTV.getConfigValue("g_menu__ch_update_msg") == 1));
                String[] channelListType = themedContext.getResources().getStringArray(R.array.menu_tv_channel_listtype);
                Preference tvChannelListType = util2.createListPreference("g_misc__ch_list_type", (int) R.string.menu_tv_channel_list_type, true, channelListType, util2.mConfigManager.getDefault("g_misc__ch_list_type"));
                boolean isCLTypeShow = this.mTV.isConfigVisible("g_misc__ch_list_type");
                String profileName = MtkTvConfig.getInstance().getConfigString("g_misc__ch_list_slot");
                if (isCLTypeShow && !TextUtils.isEmpty(profileName)) {
                    MtkLog.d(TAG, "MenuConfigManager.CHANNEL_LIST_TYPE, isCLTypeShow : " + isCLTypeShow);
                    this.mainPreferenceScreen.addPreference(tvChannelListType);
                }
                if (util2.mConfigManager.getDefault("g_misc__ch_list_type") != 0) {
                    MtkLog.d(TAG, "profileName " + profileName);
                    channelListType[1] = profileName;
                    tvChannelListType.setSummary((CharSequence) profileName);
                } else {
                    MtkLog.d(TAG, "profileName " + profileName);
                    channelListType[1] = profileName;
                    tvChannelListType.setSummary((CharSequence) channelListType[0]);
                }
            }
        }
        Preference parentalControls = util2.createPreference(MenuConfigManager.CHANNEL_PARENTAL_CONTROLS, (int) R.string.menu_channel_parental_controls);
        this.mainPreferenceScreen.addPreference(parentalControls);
        parentalControls.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                PinDialogFragment dialog = PinDialogFragment.create(2);
                ((TvSettingsActivity) SettingsPreferenceScreen.this.mContext).hide();
                dialog.show(((TvSettingsActivity) SettingsPreferenceScreen.this.mContext).getFragmentManager(), "PinDialogFragment");
                return true;
            }
        });
        if (isTVSource && !this.mTV.isTvInputBlock()) {
            if (CommonIntegration.isUSRegion()) {
                SundryImplement sundryIm = SundryImplement.getInstanceNavSundryImplement(themedContext);
                String[] stringArray = themedContext.getResources().getStringArray(R.array.menu_tv_audio_channel_mts_array);
                String key = "g_audio__aud_mts";
                if (CommonIntegration.getInstance().isCurrentSourceATV() || !this.mTV.iCurrentInputSourceHasSignal()) {
                    multiAudio = sundryIm.getAllMtsModes();
                    defaultAudio = getIndexOfMtsMode(multiAudio, sundryIm.getMtsModeString(this.mTV.getConfigValue("g_audio__aud_mts")));
                } else {
                    multiAudio = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_array);
                    key = "g_aud_lang__aud_language";
                    defaultAudio = util2.mOsdLanguage.getAudioLanguage(key);
                }
                this.mainPreferenceScreen.addPreference(util2.createListPreference(key, (int) R.string.menu_channel_multi_audio, true, multiAudio, defaultAudio));
            } else if (CommonIntegration.isEURegion()) {
                SundryImplement sundryIm2 = SundryImplement.getInstanceNavSundryImplement(themedContext);
                String[] audioLanuage = themedContext.getResources().getStringArray(R.array.menu_tv_audio_language_eu_array);
                String[] audioLanuage2nd = themedContext.getResources().getStringArray(R.array.menu_tv_audio_language_eu_array);
                if (MarketRegionInfo.isFunctionSupport(16)) {
                    String[] langArray = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_array_PA);
                    tvFirstLanguageEU = util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, langArray, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language"));
                    tvSecondLanguageEU = util2.createListPreference("g_aud_lang__aud_2nd_language", (int) R.string.menu_tv_audio_language2nd, true, langArray, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_2nd_language"));
                } else {
                    tvFirstLanguageEU = util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, audioLanuage, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language"));
                    tvSecondLanguageEU = util2.createListPreference("g_aud_lang__aud_2nd_language", (int) R.string.menu_tv_audio_language2nd, true, audioLanuage2nd, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_2nd_language"));
                }
                if (CommonIntegration.getInstance().isCurrentSourceATV() || (!this.mTV.iCurrentInputSourceHasSignal() && !CommonIntegration.getInstance().hasActiveChannel())) {
                    String[] mts = sundryIm2.getAllMtsModes();
                    tvFirstVoice = util2.createListPreference("g_audio__aud_mts", (int) R.string.menu_tv_mts, true, mts, getIndexOfMtsMode(mts, sundryIm2.getMtsModeString(this.mTV.getConfigValue("g_audio__aud_mts"))));
                    this.mainPreferenceScreen.addPreference(tvFirstVoice);
                }
                this.mainPreferenceScreen.addPreference(tvFirstLanguageEU);
                this.mainPreferenceScreen.addPreference(tvSecondLanguageEU);
                if (!this.mTV.isConfigVisible("g_menu__audio_lang_attr")) {
                    this.mainPreferenceScreen.removePreference(tvFirstLanguageEU);
                    this.mainPreferenceScreen.removePreference(tvSecondLanguageEU);
                }
            } else if (CommonIntegration.isSARegion()) {
                if (CommonIntegration.getInstance().isCurrentSourceATV() || (!this.mTV.iCurrentInputSourceHasSignal() && !CommonIntegration.getInstance().hasActiveChannel())) {
                    SundryImplement sundryIm3 = SundryImplement.getInstanceNavSundryImplement(themedContext);
                    String[] mts2 = sundryIm3.getAllMtsModes();
                    this.mainPreferenceScreen.addPreference(util2.createListPreference("g_audio__aud_mts", (int) R.string.menu_tv_mts_us, true, mts2, getIndexOfMtsMode(mts2, sundryIm3.getMtsModeString(this.mTV.getConfigValue("g_audio__aud_mts")))));
                } else {
                    this.mainPreferenceScreen.addPreference(util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, themedContext.getResources().getStringArray(R.array.menu_tv_audio_language_array_SA), util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language")));
                }
            }
        }
        Intent intent = new Intent(themedContext, WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_URL, LicenseUtils.LICENSE_FILE);
        this.mainPreferenceScreen.addPreference(util2.createPreference(MenuConfigManager.CHANNEL_OPEN_SOURCE_LICENSES, (int) R.string.settings_menu_licenses, intent));
        Preference version = util2.createPreference(MenuConfigManager.CHANNEL_VERSION, (int) R.string.menu_channel_version);
        version.setSummary((CharSequence) ((DestroyApp) themedContext.getApplicationContext()).getVersionName());
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });
        this.mainPreferenceScreen.addPreference(version);
        return this.mainPreferenceScreen;
    }

    public PreferenceScreen getSubScreen(String parentId) {
        String str;
        Context themedContext = this.mPreferenceManager.getContext();
        PreferenceScreen preferenceScreen = this.mPreferenceManager.createPreferenceScreen(themedContext);
        preferenceScreen.setKey(parentId);
        if (TextUtils.equals(MenuConfigManager.VIDEO_COLOR_TEMPERATURE, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_video_color_temperature);
            return addVideoSubColorTemperatureSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.VIDEO_ADVANCED_VIDEO, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_video_advancedvideo);
            return addVideoSubAdvancedVideoSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.MJC, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_video_mjc);
            return addVideoSubAdvancedVideoSubMJCSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.VGA, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_video_vga);
            return addVideoSubVgaViSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.VIDEO_3D, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_video_3d);
            return addVideoSub3DSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_POWER_ON_CH, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_power_on_channel);
            return addSetupSubTime_PowerOnChannelSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.CAPTION, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_caption_setup);
            return addSetupSubCaptionSetupSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_DIGITAL_STYLE, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_digital_style);
            return addSetupSubCaptionSubCaptionStyleSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_NETWORK, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_network);
            return addSetupSubNetWorkSubSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.BISS_KEY, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_biss_key);
            return addSetupSubBissKeySettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.BISS_KEY_ITEM_ADD, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_biss_key_add_key);
            return addSetupSubAddBissKeySettings(preferenceScreen, themedContext, 0, 0);
        } else if (parentId != null && parentId.contains(MenuConfigManager.BISS_KEY_ITEM_UPDATE)) {
            int index = Integer.valueOf(parentId.substring(MenuConfigManager.BISS_KEY_ITEM_UPDATE.length())).intValue();
            preferenceScreen.setTitle((CharSequence) "Biss Key " + index);
            return addSetupSubAddBissKeySettings(preferenceScreen, themedContext, 1, index - 1);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_HDMI, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_hdmi_2_0_setting);
            return addSetupSubHDMISubSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_RECORD_SETTING, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_record_setting);
            return addSetupSubRecordSettings(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_CHANNEL_UPDATE, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_channel_update);
            return addSetupSubChannelUpdate(preferenceScreen, themedContext);
        } else if (TextUtils.equals("SETUP_version_info", parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_version_info);
            return addVersionInfoSubPage(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SYSTEM_INFORMATION, parentId)) {
            preferenceScreen.setTitle((int) R.string.menu_setup_system_info);
            return addSystemInfoSubPage(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_REGION_SETTING, parentId)) {
            if (this.mTV.isEcuadorCountry()) {
                preferenceScreen.setTitle((int) R.string.menu_setup_region_setting_ecuador);
                return addSetupSubRegionSettingsEcu(preferenceScreen, themedContext);
            }
            preferenceScreen.setTitle((int) R.string.menu_setup_region_setting_philippines);
            return addSetupSubRegionSettingsPhi(preferenceScreen, themedContext);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_REGION_SETTING_LUZON, parentId) || TextUtils.equals(MenuConfigManager.SETUP_REGION_SETTING_VISAYAS, parentId) || TextUtils.equals(MenuConfigManager.SETUP_REGION_SETTING_MINDANAO, parentId)) {
            return addSetupSubRegionSettingsPhiNationwide(preferenceScreen, themedContext, parentId);
        } else {
            if (TextUtils.equals(MenuConfigManager.GINGA_SETUP, parentId)) {
                preferenceScreen.setTitle((int) R.string.menu_setup_ginga_setup);
                return addSetupSubGingaSettings(preferenceScreen, themedContext);
            } else if (TextUtils.equals(MenuConfigManager.TV_EU_CHANNEL, parentId)) {
                preferenceScreen.setTitle((int) R.string.menu_tv_channels);
                if (MarketRegionInfo.isFunctionSupport(16) && this.mTV.isCurrentSourceATV()) {
                    return loadDataTvCable(preferenceScreen, themedContext);
                }
                if (this.mTV.isChineseTWN() || this.mTV.isHKRegion()) {
                    return loadDataTvAntenna(preferenceScreen, themedContext);
                }
                switch (CommonIntegration.getInstance().getTunerMode()) {
                    case 0:
                        return loadDataTvAntenna(preferenceScreen, themedContext);
                    case 1:
                        return loadDataTvCable(preferenceScreen, themedContext);
                    case 2:
                    case 3:
                        return loadDataTvPersonalSatllites(preferenceScreen, themedContext);
                    default:
                        return loadDataTvAntenna(preferenceScreen, themedContext);
                }
            } else {
                if (TextUtils.equals(MenuConfigManager.TV_CHANNEL, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_tv_channel_setup);
                    if (CommonIntegration.isSARegion()) {
                        return addSAChannel(preferenceScreen, themedContext);
                    }
                    if (CommonIntegration.isCNRegion()) {
                        return addCNChannel(preferenceScreen, themedContext);
                    }
                }
                if (TextUtils.equals(MenuConfigManager.DVBS_SAT_RE_SCAN, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_s_sate_rescan);
                    return addDVBSRescan(preferenceScreen, themedContext);
                }
                if (TextUtils.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBC, parentId)) {
                    if (ScanContent.convertStrOperator(this.mContext, ScanContent.getCableOperationList(this.mContext)).size() > 0) {
                        preferenceScreen.setTitle((int) R.string.menu_tv_channel_scan);
                        return addCableScan(preferenceScreen, themedContext);
                    }
                    preferenceScreen.setTitle((int) R.string.menu_tv_channel_scan);
                }
                if (TextUtils.equals(MenuConfigManager.VISUALLY_IMPAIRED, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_audio_visually_impaired);
                    return addAudioSubvisuallyimpairedSettings(preferenceScreen, themedContext);
                } else if (TextUtils.equals("g_menu__audioinfo", parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_audio_visually_impaired_audio);
                    return addAudioSubvisuallyimpairedAudioSettings(preferenceScreen, themedContext);
                } else if (TextUtils.equals("g_menu__soundtracks", parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_audio_sound_tracks);
                    return addAudioSubSoundTracks(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FREEVIEW_SETTING, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_string_freeview_setting);
                    ComponentName cName = new ComponentName("com.mediatek.wwtv.setupwizard", "com.mediatek.wwtv.setupwizard.FVPAnnounceActivity");
                    Intent intent = new Intent();
                    intent.setComponent(cName);
                    preferenceScreen.addPreference(this.util.createPreference(MenuConfigManager.FREEVIEW_TERM_CONDITION, (int) R.string.menu_string_term_condition, intent));
                    return preferenceScreen;
                } else if (TextUtils.equals(MenuConfigManager.SUBTITLE_GROUP, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_setup_subtitle);
                    return addSetupSubSubtitleSettings(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.SETUP_HBBTV, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_setup_HBBTV_settings);
                    return addSetupSubHbbtvSettings(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.SETUP_TELETEXT, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_setup_teletext);
                    return addSetupSubTeletextSettings(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.SETUP_OAD_SETTING, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_setup_oad_setting);
                    return addSetupSubOADSettings(preferenceScreen, themedContext);
                } else if (parentId.startsWith(MenuConfigManager.PARENTAL_TIF_CONTENT_RATGINS)) {
                    String[] strInfo = parentId.split("\\|");
                    preferenceScreen.setTitle((CharSequence) strInfo == null ? "ContentRatings" : strInfo[strInfo.length - 1]);
                    loadContentRatingsSystems(preferenceScreen, themedContext, parentId);
                    return preferenceScreen;
                } else if (TextUtils.equals(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_parental_channel_schedule_block);
                    return addParentalSecondChannelScheduleBlock(preferenceScreen, themedContext);
                } else if (parentId.startsWith(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST)) {
                    MtkLog.d(TAG, "parentId:" + parentId);
                    String tid = parentId.substring(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST.length());
                    MtkTvChannelInfoBase channelInfo = mNavIntegration.getChannelById(Integer.valueOf(tid).intValue());
                    if (channelInfo != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("");
                        if (this.mDataHelper.getDisplayChNumber(Integer.valueOf(tid).intValue()) == null) {
                            str = "";
                        } else {
                            str = this.mDataHelper.getDisplayChNumber(Integer.valueOf(tid).intValue());
                        }
                        sb.append(str);
                        sb.append("        ");
                        sb.append(channelInfo.getServiceName() == null ? "" : channelInfo.getServiceName());
                        preferenceScreen.setTitle((CharSequence) sb.toString());
                    }
                    return addParentalLastSubChannelScheduleBlock(preferenceScreen, themedContext, parentId);
                } else if (TextUtils.equals(MenuConfigManager.FV_COLORTEMPERATURE, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_video_color_temperature);
                    return addFacVidColorTmp(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FA_MTS_SYSTEM, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_audio_mts_system);
                    return addFacAudMts(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FA_A2SYSTEM, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_audio_a2_system);
                    return addFacAudA2System(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FA_PALSYSTEM, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_audio_pal_system);
                    return addFacAudPalSystem(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FA_EUSYSTEM, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_audio_eu_system);
                    return addFacAudEuSystem(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FACTORY_TV_TUNER_DIAGNOSTIC, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_TV_tunerdiag);
                    return addFacTVdiagnostic(preferenceScreen, themedContext);
                } else if (TextUtils.equals(MenuConfigManager.FACTORY_SETUP_CAPTION, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_factory_setup_caption);
                    return addFacSetupCaption(preferenceScreen, themedContext);
                } else if (parentId.equals(MenuConfigManager.PARENTAL_CHANNEL_BLOCK)) {
                    preferenceScreen.setTitle((int) R.string.menu_parental_channel_block);
                    return addParentalChannelBlock(preferenceScreen, themedContext);
                } else if (parentId.startsWith(MenuConfigManager.PARENTAL_OPEN_VCHIP_REGIN)) {
                    MtkLog.d(TAG, "parentId:" + parentId);
                    this.regin = parentId.substring(MenuConfigManager.PARENTAL_OPEN_VCHIP_REGIN.length());
                    int reginNum = Integer.parseInt(this.regin);
                    preferenceScreen.setTitle((CharSequence) this.mTV.getOpenVchip().getRegionText());
                    return addParentalSubOpenVChipDim(preferenceScreen, themedContext, reginNum);
                } else if (TextUtils.equals(MenuConfigManager.PARENTAL_OPEN_VCHIP, parentId)) {
                    preferenceScreen.setTitle((int) R.string.menu_open_vchip);
                    return addParentalSubOpenVChipSetting(preferenceScreen, themedContext);
                } else if (!parentId.startsWith(MenuConfigManager.PARENTAL_OPEN_VCHIP_DIM)) {
                    return null;
                } else {
                    MtkLog.d(TAG, "parentId:" + parentId);
                    this.dim = parentId.substring(MenuConfigManager.PARENTAL_OPEN_VCHIP_DIM.length());
                    int dimNum = Integer.parseInt(this.dim);
                    preferenceScreen.setTitle((CharSequence) this.mTV.getOpenVchip().getDimText());
                    return addParentalSubOpenVChipLevel(preferenceScreen, themedContext, dimNum);
                }
            }
        }
    }

    public PreferenceScreen getFactoryScreen() {
        Context themedContext = this.mPreferenceManager.getContext();
        PreferenceScreen screen = this.mPreferenceManager.createPreferenceScreen(themedContext);
        screen.setTitle((int) R.string.menu_factory_name);
        screen.setKey(MenuConfigManager.FACTORY_VIDEO);
        PreferenceCategory video = new PreferenceCategory(themedContext);
        video.setTitle((int) R.string.menu_factory_video);
        video.setKey(FACTORY_HEAD + video.getTitle().toString());
        screen.addPreference(video);
        addFactoryVideoSettings(screen, themedContext);
        PreferenceCategory tv = new PreferenceCategory(themedContext);
        tv.setTitle((int) R.string.menu_factory_TV);
        tv.setKey(FACTORY_HEAD + tv.getTitle().toString());
        screen.addPreference(tv);
        addFactoryTVSettings(screen, themedContext);
        PreferenceCategory setup = new PreferenceCategory(themedContext);
        setup.setTitle((int) R.string.menu_factory_setup);
        setup.setKey(FACTORY_HEAD + setup.getTitle().toString());
        screen.addPreference(setup);
        addFactorySetupSettings(screen, themedContext);
        return screen;
    }

    public void notifyPreferenceForVideo() {
        this.util = PreferenceUtil.getInstance(this.mPreferenceManager.getContext());
        if (this.pictureModePre != null && this.util != null) {
            int cur = MtkTvConfig.getInstance().getConfigValue("g_video__picture_mode");
            if (cur == 5 || cur == 6) {
                this.mainPreferenceScreen.removePreference(this.pictureModePre);
                this.pictureMode = this.mContext.getResources().getStringArray(R.array.picture_effect_array_dovi);
                this.pictureModePre = this.util.createListPreference("g_video__picture_mode", (int) R.string.menu_video_picture_mode, true, this.pictureMode, cur - 5);
                this.pictureModePre.setOrder(this.preOrder.get("picmode").intValue());
                this.mainPreferenceScreen.addPreference(this.pictureModePre);
                this.reset_setting = this.util.createClickPreference("g_video__dovi_reset_pic_setting", (int) R.string.menu_video_restore);
                int resetOrder = this.preOrder.get("reset").intValue();
                Log.d(TAG, "reset_setting value is " + resetOrder);
                this.reset_setting.setOrder(resetOrder);
                this.mainPreferenceScreen.addPreference(this.reset_setting);
            } else {
                this.mainPreferenceScreen.removePreference(this.pictureModePre);
                this.mainPreferenceScreen.removePreference(this.reset_setting);
                this.pictureMode = this.mContext.getResources().getStringArray(R.array.picture_effect_array);
                this.pictureModePre = this.util.createListPreference("g_video__picture_mode", (int) R.string.menu_video_picture_mode, true, this.pictureMode, this.util.mConfigManager.getDefault("g_video__picture_mode") - this.util.mConfigManager.getMin("g_video__picture_mode"));
                this.pictureModePre.setOrder(this.preOrder.get("picmode").intValue());
                this.mainPreferenceScreen.addPreference(this.pictureModePre);
            }
            this.mainPreferenceScreen.removePreference(this.backLightPre);
            this.mainPreferenceScreen.removePreference(this.brightnessPre);
            this.mainPreferenceScreen.removePreference(this.contrastPre);
            this.mainPreferenceScreen.removePreference(this.saturationPre);
            this.mainPreferenceScreen.removePreference(this.HUEPre);
            this.mainPreferenceScreen.removePreference(this.sharpnessPre);
            this.backLightPre = this.util.createProgressPreference("g_disp__disp_back_light", R.string.menu_video_backlight, true);
            this.backLightPre.setOrder(this.preOrder.get("back_light").intValue());
            this.mainPreferenceScreen.addPreference(this.backLightPre);
            this.brightnessPre = this.util.createProgressPreference("g_video__brightness", R.string.menu_video_brighttness, true);
            this.brightnessPre.setOrder(this.preOrder.get("brightness").intValue());
            this.mainPreferenceScreen.addPreference(this.brightnessPre);
            this.contrastPre = this.util.createProgressPreference("g_video__contrast", R.string.menu_video_contrast, true);
            this.contrastPre.setOrder(this.preOrder.get("contrast").intValue());
            this.mainPreferenceScreen.addPreference(this.contrastPre);
            this.saturationPre = this.util.createProgressPreference("g_video__vid_sat", R.string.menu_video_saturation, true);
            this.HUEPre = this.util.createProgressPreference("g_video__vid_hue", R.string.menu_video_hue, false);
            this.sharpnessPre = this.util.createProgressPreference("g_video__vid_shp", R.string.menu_video_sharpness, false);
            this.saturationPre.setOrder(this.preOrder.get("saturation").intValue());
            this.HUEPre.setOrder(this.preOrder.get("hue").intValue());
            this.sharpnessPre.setOrder(this.preOrder.get("sharpness").intValue());
            boolean isSub = mNavIntegration.getCurrentFocus().equalsIgnoreCase("sub");
            boolean isSA = CommonIntegration.isSARegion();
            if (!this.mTV.isCurrentSourceVGA()) {
                this.mainPreferenceScreen.addPreference(this.saturationPre);
                this.mainPreferenceScreen.addPreference(this.HUEPre);
                if (!isSub || isSA) {
                    this.mainPreferenceScreen.addPreference(this.sharpnessPre);
                }
            } else if (!isSA) {
                this.mainPreferenceScreen.addPreference(this.saturationPre);
                this.mainPreferenceScreen.addPreference(this.HUEPre);
            }
            if (isSub && !CommonIntegration.isSARegion()) {
                this.mainPreferenceScreen.removePreference(this.sharpnessPre);
            }
            if (this.mTV.isCurrentSourceVGA()) {
                this.mainPreferenceScreen.removePreference(this.sharpnessPre);
            }
        }
    }

    private void addVideoSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        this.util = PreferenceUtil.getInstance(themedContext);
        if (CommonIntegration.isCNRegion()) {
            this.vgaMode = this.mContext.getResources().getStringArray(R.array.menu_video_vga_mode_array_cn);
        } else {
            this.vgaMode = this.mContext.getResources().getStringArray(R.array.menu_video_vga_mode_array);
        }
        Preference vgaModePre = this.util.createListPreference("g_video__vid_vga_mode", (int) R.string.menu_video_vga_mode, true, this.vgaMode, this.util.mConfigManager.getDefault("g_video__vid_vga_mode"));
        Preference videovga = this.util.createPreference(MenuConfigManager.VGA, (int) R.string.menu_video_vga);
        preferenceScreen.addPreference(vgaModePre);
        preferenceScreen.addPreference(videovga);
        if (this.mTV.isCurrentSourceVGA()) {
            if (this.mTV.iCurrentInputSourceHasSignal()) {
                vgaModePre.setEnabled(true);
                videovga.setEnabled(true);
            } else {
                vgaModePre.setEnabled(false);
                videovga.setEnabled(false);
            }
            vgaModePre.setVisible(true);
            videovga.setVisible(true);
            return;
        }
        vgaModePre.setVisible(false);
        videovga.setVisible(false);
    }

    private PreferenceScreen addVideoSubColorTemperatureSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createListPreference("g_video__clr_temp", (int) R.string.menu_video_color_temperature, true, this.colorTemperature2, util2.mConfigManager.getDefault("g_video__clr_temp") - util2.mConfigManager.getMin("g_video__clr_temp")));
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_gain_r", R.string.menu_video_color_g_red, false));
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_gain_g", R.string.menu_video_color_g_green, false));
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_gain_b", R.string.menu_video_color_g_blue, false));
        return preferenceScreen;
    }

    private PreferenceScreen addVideoSubAdvancedVideoSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        Preference diFilmModePre;
        Preference localPre;
        boolean z;
        boolean z2;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        if (CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
            if (this.mTV.isCurrentSourceVGA() && CommonIntegration.isUSRegion()) {
                preferenceScreen2.addPreference(util2.createListPreference("g_video__vid_black_bar_detect", (int) R.string.menu_video_black_bar, true, this.mBlackBar, util2.mConfigManager.getDefault("g_video__vid_black_bar_detect")));
                return preferenceScreen2;
            } else if (!CommonIntegration.isSARegion() && !this.mTV.isCurrentSourceTv()) {
                return preferenceScreen2;
            }
        }
        Preference DNRPre = util2.createListPreference("g_video__vid_nr", (int) R.string.menu_video_dnr, true, this.dnr, util2.mConfigManager.getDefault("g_video__vid_nr") - util2.mConfigManager.getMin("g_video__vid_nr"));
        Preference lumaPre = util2.createListPreference("g_video__vid_luma", (int) R.string.menu_video_luma, true, this.luma, util2.mConfigManager.getDefault("g_video__vid_luma") - util2.mConfigManager.getMin("g_video__vid_luma"));
        Preference localPre2 = util2.createListPreference("g_video__vid_local_contrast", (int) R.string.menu_video_luma, true, this.localContrast, util2.mConfigManager.getDefault("g_video__vid_local_contrast") - util2.mConfigManager.getMin("g_video__vid_local_contrast"));
        Preference fleshTonePre = util2.createListPreference("g_video__vid_flash_tone", (int) R.string.menu_video_flesh_tone, true, this.fleshTone, util2.mConfigManager.getDefault("g_video__vid_flash_tone") - util2.mConfigManager.getMin("g_video__vid_flash_tone"));
        if (CommonIntegration.isSARegion()) {
            if (CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
                DNRPre.setEnabled(false);
                lumaPre.setEnabled(false);
            }
            if (!this.mTV.isCurrentSourceVGA()) {
                preferenceScreen2.addPreference(DNRPre);
                preferenceScreen2.addPreference(fleshTonePre);
                preferenceScreen2.addPreference(lumaPre);
            }
            preferenceScreen2.addPreference(util2.createListPreference("g_video__vid_blue_mute", (int) R.string.menu_setup_bluemute, true, this.blueMute, util2.mConfigManager.getDefault("g_video__vid_blue_mute") - util2.mConfigManager.getMin("g_video__vid_blue_mute")));
        }
        if (CommonIntegration.isEURegion() || CommonIntegration.isUSRegion() || CommonIntegration.isCNRegion()) {
            preferenceScreen2.addPreference(DNRPre);
            Preference mpegNrPre = util2.createListPreference("g_video__vid_mpeg_nr", (int) R.string.menu_video_mpeg_nr, true, this.videoMpegNr, util2.mConfigManager.getDefault("g_video__vid_mpeg_nr") - util2.mConfigManager.getMin("g_video__vid_mpeg_nr"));
            Preference diFilmModePre2 = util2.createListPreference("g_video__vid_di_film_mode", (int) R.string.menu_video_di_film_mode, true, this.videoDiFilm, util2.mConfigManager.getDefault("g_video__vid_di_film_mode"));
            Preference diFilmModePre3 = diFilmModePre2;
            Preference blueStretchPre = util2.createListPreference("g_video__vid_blue_stretch", (int) R.string.menu_video_blue_stretch, true, this.blueStretch, this.mTV.getConfigValue("g_video__vid_blue_stretch"));
            Preference gameModePre = util2.createListPreference("g_video__vid_game_mode", (int) R.string.menu_video_wme_mode, true, this.gameMode, util2.mConfigManager.getDefault("g_video__vid_game_mode") - util2.mConfigManager.getMin("g_video__vid_game_mode"));
            if (this.mTV.isConfigEnabled("g_video__vid_game_mode")) {
                gameModePre.setEnabled(true);
            } else {
                gameModePre.setEnabled(false);
            }
            preferenceScreen2.addPreference(mpegNrPre);
            preferenceScreen2.addPreference(lumaPre);
            preferenceScreen2.addPreference(fleshTonePre);
            if (!this.mTV.isCurrentSourceVGA()) {
                diFilmModePre = diFilmModePre3;
                preferenceScreen2.addPreference(diFilmModePre);
            } else {
                diFilmModePre = diFilmModePre3;
            }
            preferenceScreen2.addPreference(blueStretchPre);
            preferenceScreen2.addPreference(gameModePre);
            if (CommonIntegration.isCNRegion()) {
                Preference preference = localPre2;
                localPre = diFilmModePre;
                preferenceScreen2.addPreference(util2.createListPreference("g_video__vid_pq_demo", (int) R.string.menu_video_pq_split_mode, true, this.pqsplitdemoMode, util2.mConfigManager.getDefault("g_video__vid_pq_demo") - util2.mConfigManager.getMin("g_video__vid_pq_demo")));
            } else {
                localPre = diFilmModePre;
            }
            Preference MJCPre = util2.createPreference(MenuConfigManager.MJC, (int) R.string.menu_video_mjc);
            preferenceScreen2.addPreference(MJCPre);
            if (this.mTV.getConfigValue("g_video__vid_vga_mode") == 0) {
                DNRPre.setEnabled(false);
                mpegNrPre.setEnabled(false);
                lumaPre.setEnabled(false);
                fleshTonePre.setEnabled(false);
                blueStretchPre.setEnabled(false);
                z = true;
            } else {
                z = true;
                DNRPre.setEnabled(true);
                mpegNrPre.setEnabled(true);
                lumaPre.setEnabled(true);
                fleshTonePre.setEnabled(true);
                blueStretchPre.setEnabled(true);
            }
            if (this.mTV.getConfigValue("g_video__vid_game_mode") == 0) {
                MJCPre.setEnabled(z);
                if (CommonIntegration.getInstance().isPipOrPopState()) {
                    z2 = false;
                    MJCPre.setEnabled(false);
                } else {
                    z2 = false;
                }
            } else {
                z2 = false;
                MJCPre.setEnabled(false);
            }
            if (this.mTV.isConfigEnabled("g_video__vid_di_film_mode")) {
                localPre.setEnabled(true);
            } else {
                localPre.setEnabled(z2);
            }
            if (CommonIntegration.isUSRegion()) {
                Preference preference2 = MJCPre;
                preferenceScreen2.addPreference(util2.createListPreference("g_video__vid_black_bar_detect", (int) R.string.menu_video_black_bar, true, this.mBlackBar, util2.mConfigManager.getDefault("g_video__vid_black_bar_detect")));
            }
            Preference superResolutionPre = util2.createListPreference("g_video__vid_super_resolution", (int) R.string.menu_video_super_resolution, true, this.mSuperResolution, util2.mConfigManager.getDefault("g_video__vid_super_resolution"));
            preferenceScreen2.addPreference(superResolutionPre);
            if (!CommonIntegration.isEURegion() || !this.mTV.isCurrentSourceVGA()) {
            } else {
                Preference preference3 = superResolutionPre;
                preferenceScreen2.addPreference(util2.createListPreference("g_video__vid_super_resolution", (int) R.string.menu_video_super_resolution, true, this.mSuperResolution, util2.mConfigManager.getDefault("g_video__vid_super_resolution")));
            }
            Preference hdmiModePre = util2.createListPreference("g_video__vid_hdmi_mode", (int) R.string.menu_video_hdmi_mode, true, this.hdmiMode, util2.mConfigManager.getDefault("g_video__vid_hdmi_mode"));
            if (this.mTV.isCurrentSourceHDMI()) {
                preferenceScreen2.addPreference(hdmiModePre);
                if (util2.mConfigManager.getDefault("g_video__vid_hdmi_mode") != 1) {
                    DNRPre.setEnabled(true);
                    mpegNrPre.setEnabled(true);
                    lumaPre.setEnabled(true);
                    fleshTonePre.setEnabled(true);
                    blueStretchPre.setEnabled(true);
                } else {
                    DNRPre.setEnabled(false);
                    mpegNrPre.setEnabled(false);
                    lumaPre.setEnabled(false);
                    fleshTonePre.setEnabled(false);
                    blueStretchPre.setEnabled(false);
                }
            }
        } else {
            Preference preference4 = localPre2;
        }
        preferenceScreen2.addPreference(util2.createProgressPreference("g_video__vid_blue_light", R.string.menu_video_blue_light, true));
        this.videohdr = util2.createListPreference("g_video__vid_hdr", (int) R.string.menu_video_hdr, true, this.hdr, util2.mConfigManager.getDefault("g_video__vid_hdr"));
        if (!this.mTV.iCurrentInputSourceHasSignal()) {
            preferenceScreen2.addPreference(this.videohdr);
        } else if (this.mTV.isConfigVisible("g_video__vid_hdr")) {
            preferenceScreen2.addPreference(this.videohdr);
        }
        return preferenceScreen2;
    }

    private PreferenceScreen addVideoSubAdvancedVideoSubMJCSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        int effect_Def = this.mTV.getConfigValue("g_video__vid_mjc_effect");
        preferenceScreen.addPreference(util2.createListPreference("g_video__vid_mjc_effect", (int) R.string.menu_video_mjc_effect, true, this.videoEffect, util2.mConfigManager.getDefault("g_video__vid_mjc_effect")));
        Preference demoPartitionPre = util2.createListPreference("g_video__vid_mjc_demo", (int) R.string.menu_video_mjc_demo_partition, true, this.videoMjcMode, util2.mConfigManager.getDefault("g_video__vid_mjc_demo"));
        demoPartitionPre.setOnPreferenceChangeListener(this.mvisuallyimpairedChangeListener);
        preferenceScreen.addPreference(demoPartitionPre);
        this.demoPre = util2.createDialogPreference(MenuConfigManager.DEMO, (int) R.string.menu_video_mjc_demo, (Dialog) new MenuMjcDemoDialog(themedContext));
        preferenceScreen.addPreference(this.demoPre);
        if (effect_Def == 0) {
            demoPartitionPre.setEnabled(false);
            this.demoPre.setEnabled(false);
        } else {
            demoPartitionPre.setEnabled(true);
            this.demoPre.setEnabled(true);
        }
        return preferenceScreen;
    }

    /* access modifiers changed from: private */
    public boolean isHaveScreenModeItem() {
        if (this.mainPreferenceScreen.findPreference("g_video__screen_mode") != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isHaveHDRItem() {
        if (this.mainPreferenceScreen.findPreference("g_video__vid_hdr") != null) {
            return true;
        }
        return false;
    }

    private PreferenceScreen addSetupSubTime_PowerOnChannelSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        int def = SaveValue.getInstance(themedContext).readValue(MenuConfigManager.SELECT_MODE);
        preferenceScreen2.addPreference(util2.createListPreference(MenuConfigManager.SELECT_MODE, (int) R.string.menu_setup_power_on_channel_mode, true, this.mContext.getResources().getStringArray(R.array.menu_setup_power_on_channel_select_mode_array), def));
        boolean enable = CommonIntegration.getInstance().hasActiveChannel();
        Intent intent = new Intent(this.mContext, ChannelInfoActivity.class);
        intent.putExtra("TransItem", new TransItem(MenuConfigManager.SETUP_POWER_ONCHANNEL_LIST, "", 10004, 10004, 10004));
        intent.putExtra("ActionID", MenuConfigManager.POWER_ON_VALID_CHANNELS);
        Preference channel = util2.createPreference(MenuConfigManager.SETUP_POWER_ONCHANNEL_LIST, (int) R.string.menu_setup_power_on_show_channels, intent);
        channel.setEnabled(enable);
        preferenceScreen2.addPreference(channel);
        return preferenceScreen2;
    }

    public PreferenceScreen getCaptionSetupScreen() {
        MtkLog.d(TAG, "getCaptionSetupScreen");
        Context themedContext = this.mPreferenceManager.getContext();
        this.mainPreferenceScreen = this.mPreferenceManager.createPreferenceScreen(themedContext);
        this.mainPreferenceScreen.setTitle((int) R.string.menu_setup_caption_setup);
        return addSetupSubCaptionSetupSettings(this.mainPreferenceScreen, themedContext);
    }

    private PreferenceScreen addSetupSubCaptionSetupSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        String[] DGT_Entries;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        if (CommonIntegration.isSARegion()) {
            preferenceScreen2.addPreference(util2.createListPreference("g_cc__cc_caption", (int) R.string.menu_setup_caption_enable, true, this.mContext.getResources().getStringArray(R.array.menu_setup_enable_caption_array), this.mTV.getConfigValue("g_cc__cc_caption")));
        }
        Preference analog = util2.createListPreference("g_cc__cc_analog_cc", (int) R.string.menu_setup_analog_closed_caption, true, this.mContext.getResources().getStringArray(R.array.menu_setup_analog_caption_array), this.mTV.getConfigValue("g_cc__cc_analog_cc"));
        preferenceScreen2.addPreference(analog);
        if (DataSeparaterUtil.getInstance().isAtvOnly()) {
            return preferenceScreen2;
        }
        if (CommonIntegration.isSARegion()) {
            DGT_Entries = addSuffix(this.mContext.getResources().getStringArray(R.array.menu_setup_digital_caption_sa_array), R.string.menu_arrays_Service_x);
        } else {
            DGT_Entries = addSuffix(this.mContext.getResources().getStringArray(R.array.menu_setup_digital_caption_array), R.string.menu_arrays_Language_x);
        }
        String[] DGT_Entries2 = DGT_Entries;
        Preference digitalCaption = util2.createListPreference("g_cc__cc_digital_cc", (int) R.string.menu_setup_digital_caption, true, DGT_Entries2, this.mTV.getConfigValue("g_cc__cc_digital_cc"));
        preferenceScreen2.addPreference(digitalCaption);
        analog.setEnabled(true);
        if (CommonIntegration.isUSRegion()) {
            digitalCaption.setEnabled(true);
        } else if (CommonIntegration.isSARegion()) {
            digitalCaption.setEnabled(false);
        }
        MtkTvChannelInfoBase channelInfoBase = null;
        if (CommonIntegration.getInstance().isCurrentSourceTv()) {
            channelInfoBase = CommonIntegration.getInstance().getCurChInfo();
            if (channelInfoBase != null) {
                if (channelInfoBase instanceof MtkTvAnalogChannelInfo) {
                    analog.setEnabled(true);
                } else {
                    MtkLog.d(TAG, "digitalCaption.setEnable(true)");
                    if (CommonIntegration.isUSRegion()) {
                        MtkLog.d(TAG, "analog.setEnable(true)");
                        analog.setEnabled(true);
                    } else if (CommonIntegration.isSARegion()) {
                        analog.setEnabled(false);
                    }
                    digitalCaption.setEnabled(true);
                }
            }
            if (CommonIntegration.getInstance().getChannelAllNumByAPI() <= 0) {
                analog.setEnabled(false);
            }
        } else {
            analog.setEnabled(true);
            digitalCaption.setEnabled(false);
        }
        MtkTvChannelInfoBase channelInfoBase2 = channelInfoBase;
        String[] superCaption = this.mContext.getResources().getStringArray(R.array.menu_setup_superimpose_setup_array);
        if (CommonIntegration.isUSRegion()) {
            preferenceScreen2.addPreference(util2.createPreference(MenuConfigManager.SETUP_DIGITAL_STYLE, (int) R.string.menu_setup_digital_style));
            MtkTvChannelInfoBase mtkTvChannelInfoBase = channelInfoBase2;
        } else if (CommonIntegration.isSARegion()) {
            MtkTvChannelInfoBase channelInfoBase3 = channelInfoBase2;
            Preference superimpose = util2.createListPreference("g_cc__cc_si", (int) R.string.menu_setup_superimpose_setup, true, addSuffix(superCaption, R.string.menu_arrays_Language_x), this.mTV.getConfigValue("g_cc__cc_si"));
            preferenceScreen2.addPreference(superimpose);
            superimpose.setEnabled(false);
            if (CommonIntegration.getInstance().isCurrentSourceTv()) {
                MtkTvChannelInfoBase channelInfoBase4 = channelInfoBase3;
                if (channelInfoBase4 != null) {
                    if (channelInfoBase4 instanceof MtkTvAnalogChannelInfo) {
                        superimpose.setEnabled(false);
                    } else {
                        superimpose.setEnabled(true);
                    }
                }
            }
            return preferenceScreen2;
        }
        String[] strArr = superCaption;
        return preferenceScreen2;
    }

    private PreferenceScreen addSetupSubCaptionSubCaptionStyleSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] CS_Entries = this.mContext.getResources().getStringArray(R.array.menu_setup_caption_style_array);
        int CS_Def = this.mTV.getConfigValue("g_cc__dcs");
        preferenceScreen2.addPreference(util2.createListPreference("g_cc__dcs", (int) R.string.menu_setup_caption_style, true, CS_Entries, CS_Def));
        String[] FT_Entries = this.mContext.getResources().getStringArray(R.array.menu_setup_font_size_array);
        int FT_Def = this.mTV.getConfigValue("g_cc__dis_op_ft_size");
        Preference FTPreference = util2.createListPreference("g_cc__dis_op_ft_size", (int) R.string.menu_setup_font_size, true, FT_Entries, FT_Def);
        preferenceScreen2.addPreference(FTPreference);
        String[] FS_Entries = addSuffix(this.mContext.getResources().getStringArray(R.array.menu_setup_font_style_array), R.string.menu_arrays_Style_x);
        int FS_Def = this.mTV.getConfigValue("g_cc__dis_op_ft_style");
        Preference FSPreference = util2.createListPreference("g_cc__dis_op_ft_style", (int) R.string.menu_setup_font_style, true, FS_Entries, FS_Def);
        preferenceScreen2.addPreference(FSPreference);
        String[] strArr = CS_Entries;
        Preference FSPreference2 = FSPreference;
        Preference FCPreference = util2.createListPreference("g_cc__dis_op_ft_color", (int) R.string.menu_setup_font_color, true, this.mContext.getResources().getStringArray(R.array.menu_setup_font_color_array), this.mTV.getConfigValue("g_cc__dis_op_ft_color"));
        preferenceScreen2.addPreference(FCPreference);
        String[] strArr2 = FT_Entries;
        Preference FCPreference2 = FCPreference;
        Preference FOPreference = util2.createListPreference("g_cc__dis_op_ft_opacity", (int) R.string.menu_setup_font_opacity, true, this.mContext.getResources().getStringArray(R.array.menu_setup_background_opacity_array), this.mTV.getConfigValue("g_cc__dis_op_ft_opacity"));
        preferenceScreen2.addPreference(FOPreference);
        int i = FT_Def;
        Preference FOPreference2 = FOPreference;
        Preference BCPreference = util2.createListPreference("g_cc__dis_op_bk_color", (int) R.string.menu_setup_background_color, true, this.mContext.getResources().getStringArray(R.array.menu_setup_window_color_array), this.mTV.getConfigValue("g_cc__dis_op_bk_color"));
        preferenceScreen2.addPreference(BCPreference);
        String[] strArr3 = FS_Entries;
        Preference BCPreference2 = BCPreference;
        Preference BOPreference = util2.createListPreference("g_cc__dis_op_bk_opacity", (int) R.string.menu_setup_background_opacity, true, this.mContext.getResources().getStringArray(R.array.menu_setup_window_opacity_array), this.mTV.getConfigValue("g_cc__dis_op_bk_opacity"));
        preferenceScreen2.addPreference(BOPreference);
        int i2 = FS_Def;
        Preference BOPreference2 = BOPreference;
        Preference WCPreference = util2.createListPreference("g_cc__dis_op_win_color", (int) R.string.menu_setup_window_color, true, this.mContext.getResources().getStringArray(R.array.menu_setup_window_color_array), this.mTV.getConfigValue("g_cc__dis_op_win_color"));
        preferenceScreen2.addPreference(WCPreference);
        String[] WO_Entries = this.mContext.getResources().getStringArray(R.array.menu_setup_window_opacity_array);
        int WO_Def = this.mTV.getConfigValue("g_cc__dis_op_win_opacity");
        Preference WCPreference2 = WCPreference;
        Preference WOPreference = util2.createListPreference("g_cc__dis_op_win_opacity", (int) R.string.menu_setup_window_opacity, true, WO_Entries, WO_Def);
        preferenceScreen2.addPreference(WOPreference);
        if (CS_Def == 0) {
            FTPreference.setEnabled(false);
            FSPreference2.setEnabled(false);
            FCPreference2.setEnabled(false);
            FOPreference2.setEnabled(false);
            BCPreference2.setEnabled(false);
            BOPreference2.setEnabled(false);
            WCPreference2.setEnabled(false);
            WOPreference.setEnabled(false);
        } else {
            FTPreference.setEnabled(true);
            FSPreference2.setEnabled(true);
            FCPreference2.setEnabled(true);
            FOPreference2.setEnabled(true);
            BCPreference2.setEnabled(true);
            BOPreference2.setEnabled(true);
            WCPreference2.setEnabled(true);
            WOPreference.setEnabled(true);
        }
        return preferenceScreen2;
    }

    private PreferenceScreen addSetupSubNetWorkSubSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        preferenceScreen.addPreference(PreferenceUtil.getInstance(themedContext).createClickPreference(MenuConfigManager.SETUP_UPGRADENET, (int) R.string.menu_setup_auto_network_upgrade, new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                SettingsPreferenceScreen.this.networkUpdate();
                return false;
            }
        }));
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubBissKeySettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.BISS_KEY_ITEM_ADD, (int) R.string.menu_setup_biss_key_add_item));
        this.bissitems = this.mHelper.convertToBissItemList();
        if (this.bissitems != null) {
            int size = this.bissitems.size();
            for (int i = 0; i < size; i++) {
                Preference bisskeyPreference = util2.createPreference(MenuConfigManager.BISS_KEY_ITEM_UPDATE + (i + 1), this.bissitems.get(i).progId + " " + this.bissitems.get(i).threePry + " " + this.bissitems.get(i).cwKey);
                preferenceScreen.addPreference(bisskeyPreference);
                this.bisskeyPreferences.add(bisskeyPreference);
            }
        }
        this.bisskeyGroup = preferenceScreen;
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubAddBissKeySettings(PreferenceScreen preferenceScreen, Context themedContext, int flag, int index) {
        BissListAdapter.BissItem defItem;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        Context context = themedContext;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        int freq = -1;
        int rate = -1;
        int pola = -1;
        int prog_id = -1;
        String cwKeystr = "0000000000000000";
        if (flag == 0) {
            defItem = this.mHelper.getDefaultBissItem();
            int i = index;
        } else {
            defItem = this.bissitems.get(index);
        }
        if (defItem != null) {
            int findPola = defItem.threePry.indexOf(72);
            if (findPola == -1) {
                findPola = defItem.threePry.indexOf(86);
                pola = 1;
            } else {
                pola = 0;
            }
            freq = Integer.parseInt(defItem.threePry.substring(0, findPola));
            rate = Integer.parseInt(defItem.threePry.substring(findPola + 1));
            prog_id = defItem.progId;
            cwKeystr = defItem.cwKey;
        }
        Log.d(TAG, "get pola:" + pola + "");
        BissKeyEditDialog fredialog = new BissKeyEditDialog(context, themedContext.getResources().getString(R.string.menu_setup_biss_key_freqency), String.valueOf(freq), MenuConfigManager.BISS_KEY_FREQ);
        fredialog.setLength(9);
        this.frePreference = util2.createDialogPreference(MenuConfigManager.BISS_KEY_FREQ, (int) R.string.menu_setup_biss_key_freqency, (Dialog) fredialog);
        this.frePreference.setSummary((CharSequence) String.valueOf(freq));
        fredialog.setInputType(2);
        fredialog.setPreference(this.frePreference);
        preferenceScreen2.addPreference(this.frePreference);
        int i2 = freq;
        BissKeyEditDialog bissKeyEditDialog = fredialog;
        BissKeyEditDialog symdialog = new BissKeyEditDialog(context, themedContext.getResources().getString(R.string.menu_c_rfscan_symbol_rate), String.valueOf(rate), MenuConfigManager.BISS_KEY_SYMBOL_RATE);
        symdialog.setLength(5);
        symdialog.setInputType(2);
        this.symPreference = util2.createDialogPreference(MenuConfigManager.BISS_KEY_SYMBOL_RATE, (int) R.string.menu_c_rfscan_symbol_rate, (Dialog) symdialog);
        this.symPreference.setSummary((CharSequence) String.valueOf(rate));
        symdialog.setPreference(this.symPreference);
        preferenceScreen2.addPreference(this.symPreference);
        String[] entries = themedContext.getResources().getStringArray(R.array.menu_setup_ploazation_array);
        BissKeyEditDialog bissKeyEditDialog2 = symdialog;
        String[] stringArray = themedContext.getResources().getStringArray(R.array.menu_setup_ploazation_array_value);
        StringBuilder sb = new StringBuilder();
        int i3 = rate;
        sb.append("click:");
        sb.append(pola);
        Log.d(TAG, sb.toString());
        BissKeyPreferenceDialog poladialog = new BissKeyPreferenceDialog(context, pola);
        this.ploaPreference = util2.createDialogPreference(MenuConfigManager.BISS_KEY_SVC_ID, (int) R.string.menu_setup_biss_key_polazation, (Dialog) poladialog);
        if (pola == 0) {
            String[] strArr = entries;
            this.ploaPreference.setSummary((CharSequence) themedContext.getResources().getString(R.string.menu_setup_biss_key_horizonal));
        } else {
            this.ploaPreference.setSummary((CharSequence) themedContext.getResources().getString(R.string.menu_setup_biss_key_vertical));
        }
        poladialog.setPreference(this.ploaPreference);
        poladialog.setDefaultValue(pola);
        preferenceScreen2.addPreference(this.ploaPreference);
        String progString = themedContext.getResources().getString(R.string.menu_setup_biss_key_prog_id);
        BissKeyPreferenceDialog bissKeyPreferenceDialog = poladialog;
        BissKeyEditDialog progdialog = new BissKeyEditDialog(context, progString, String.valueOf(prog_id), MenuConfigManager.BISS_KEY_SVC_ID);
        progdialog.setLength(5);
        this.progPreference = util2.createDialogPreference(MenuConfigManager.BISS_KEY_SVC_ID, (int) R.string.menu_setup_biss_key_prog_id, (Dialog) progdialog);
        this.progPreference.setSummary((CharSequence) String.valueOf(prog_id));
        progdialog.setPreference(this.progPreference);
        progdialog.setInputType(2);
        preferenceScreen2.addPreference(this.progPreference);
        BissKeyEditDialog bissKeyEditDialog3 = progdialog;
        String str = progString;
        BissKeyEditDialog cwdialog = new BissKeyEditDialog(context, themedContext.getResources().getString(R.string.menu_setup_biss_key_cw_key), String.valueOf(cwKeystr), MenuConfigManager.BISS_KEY_CW_KEY);
        cwdialog.setLength(16);
        this.cwPreference = util2.createDialogPreference(MenuConfigManager.BISS_KEY_CW_KEY, (int) R.string.menu_setup_biss_key_cw_key, (Dialog) cwdialog);
        this.cwPreference.setSummary((CharSequence) String.valueOf(cwKeystr));
        cwdialog.setPreference(this.cwPreference);
        preferenceScreen2.addPreference(this.cwPreference);
        if (flag == 0) {
            Preference savepreference = new Preference(context);
            savepreference.setKey(MenuConfigManager.BISS_KEY_ITEM_SAVE);
            savepreference.setTitle((CharSequence) "Save Key");
            savepreference.setOnPreferenceClickListener(this.mClickListener);
            preferenceScreen2.addPreference(savepreference);
        } else {
            this.bnum = defItem.bnum;
            Preference updatepreference = new Preference(context);
            updatepreference.setKey(MenuConfigManager.BISS_KEY_ITEM_UPDATE);
            updatepreference.setTitle((CharSequence) "Update Key");
            updatepreference.setOnPreferenceClickListener(this.mClickListener);
            preferenceScreen2.addPreference(updatepreference);
            Preference deletepreference = new Preference(context);
            deletepreference.setKey(MenuConfigManager.BISS_KEY_ITEM_DELETE);
            deletepreference.setTitle((CharSequence) "Delete Key");
            deletepreference.setOnPreferenceClickListener(this.mClickListener);
            preferenceScreen2.addPreference(deletepreference);
        }
        return preferenceScreen2;
    }

    private PreferenceScreen addSetupSubHDMISubSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        preferenceScreen.addPreference(PreferenceUtil.getInstance(themedContext).createListPreference("g_menu_only__hdmi_edid_index", (int) R.string.menu_setup_hdmi_signalformat, true, this.mContext.getResources().getStringArray(R.array.menu_setup_signalformat_array), this.mTV.getConfigValue("g_menu_only__hdmi_edid_index")));
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubTKGSSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createListPreference("g_misc__tkgs_operating_mode", (int) R.string.menu_setup_TKGS_setting, true, new String[]{"Automatic", "Customisable", "TKGS Off"}, this.mConfigManager.getDefault("g_misc__tkgs_operating_mode")));
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.TKGS_LOC_LIST, (int) R.string.menu_setup_TKGS_locate_list));
        Preference hiddenLocations = util2.createPreference(MenuConfigManager.TKGS_HIDD_LOCS, (int) R.string.menu_setup_TKGS_hidden_locations);
        this.tkgsResetTabver = util2.createDialogPreference(MenuConfigManager.TKGS_RESET_TAB_VERSION, (int) R.string.TKGS_reset_table_version, (Dialog) this.cleanDialog);
        int tversion = this.mHelper.getTKGSTableVersion();
        if (tversion == 255) {
            this.tkgsResetTabver.setSummary((CharSequence) "None");
        } else {
            Preference preference = this.tkgsResetTabver;
            preference.setSummary((CharSequence) "" + tversion);
        }
        if (this.mConfigManager.getDefault(MenuConfigManager.TKGS_FAC_SETUP_AVAIL_CONDITION) == 0) {
            preferenceScreen.addPreference(this.tkgsResetTabver);
        } else {
            preferenceScreen.addPreference(hiddenLocations);
            preferenceScreen.addPreference(this.tkgsResetTabver);
        }
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubRecordSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        boolean z = false;
        ScheduleListDialog scheduleListDialog = new ScheduleListDialog(themedContext, 0);
        scheduleListDialog.setEpgFlag(false);
        preferenceScreen.addPreference(util2.createDialogPreference(MenuConfigManager.SETUP_SCHEDUCE_LIST, (int) R.string.menu_setup_schedule_list, (Dialog) scheduleListDialog));
        if (MarketRegionInfo.isFunctionSupport(36)) {
            if (this.mTV.getConfigValue("g_record__rec_tshift_mode") == 1) {
                z = true;
            }
            preferenceScreen.addPreference(util2.createSwitchPreference("g_record__rec_tshift_mode", (int) R.string.menu_setup_time_shifting_mode, z));
        }
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubChannelUpdate(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        boolean z = false;
        preferenceScreen.addPreference(util2.createSwitchPreference("g_misc__auto_svc_update", (int) R.string.menu_setup_auto_channel_update, this.mTV.getConfigValue("g_misc__auto_svc_update") == 1));
        if (this.mTV.getConfigValue("g_menu__ch_update_msg") == 1) {
            z = true;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference("g_menu__ch_update_msg", (int) R.string.menu_setup_channel_update_msg, z));
        return preferenceScreen;
    }

    private PreferenceScreen addVersionInfoSubPage(PreferenceScreen preferenceScreen, Context themedContext) {
        TVContent tvContent = TVContent.getInstance(themedContext);
        String modelName = tvContent.getSysVersion(3, "");
        String modeNameshow = modelName.substring(modelName.lastIndexOf("_") + 1);
        String version = SystemProperties.get("ro.vendor.customer.software.version");
        if (version == null || version.equals("")) {
            version = tvContent.getSysVersion(0, version);
        }
        String serialNum = tvContent.getSysVersion(2, "");
        preferenceScreen.addPreference(this.util.createPreferenceWithSummary("modelName", R.string.menu_versioninfo_name, modeNameshow));
        preferenceScreen.addPreference(this.util.createPreferenceWithSummary("version", R.string.menu_versioninfo_version, version));
        preferenceScreen.addPreference(this.util.createPreferenceWithSummary("serialNum", R.string.menu_versioninfo_number, serialNum));
        return preferenceScreen;
    }

    private PreferenceScreen addSystemInfoSubPage(PreferenceScreen preferenceScreen, Context themedContext) {
        return new MenuSystemInfo(themedContext).getPreferenceScreen(preferenceScreen);
    }

    private PreferenceScreen addSetupSubRegionSettingsEcu(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        boolean isEcuador = this.mTV.isEcuadorCountry();
        String[] subtitleEntry = this.mContext.getResources().getStringArray(R.array.menu_setup_region_setting_ecuador_pro_array);
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        int itemPosition = SaveValue.getInstance(this.mContext).readValue(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING);
        int selectPosition = SaveValue.getInstance(this.mContext).readValue(MenuConfigManager.SETUP_REGION_SETTING_SELECT);
        MtkLog.d(TAG, "item positon:" + itemPosition + ",select position:" + selectPosition);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < subtitleEntry.length) {
                int defaultValue = i2 == itemPosition ? selectPosition : 0;
                int cityIndex = RegionConst.getEcuadorCityArray(i2);
                String[] entryVlaue = this.mContext.getResources().getStringArray(cityIndex);
                int i3 = cityIndex;
                Preference proPreference = util2.createListPreference(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING + i2, subtitleEntry[i2], true, entryVlaue, defaultValue);
                proPreference.setSummary((CharSequence) entryVlaue[defaultValue]);
                MtkLog.d(TAG, "region setting:" + entryVlaue[defaultValue] + ",isEcuador:" + isEcuador);
                preferenceScreen2.addPreference(proPreference);
                i = i2 + 1;
            } else {
                this.regionSettingScreen = preferenceScreen2;
                return preferenceScreen2;
            }
        }
    }

    private PreferenceScreen addSetupSubRegionSettingsPhi(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_REGION_SETTING_LUZON, (int) R.string.menu_setup_region_setting_philippines_luzon));
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_REGION_SETTING_VISAYAS, (int) R.string.menu_setup_region_setting_philippines_visayas));
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_REGION_SETTING_MINDANAO, (int) R.string.menu_setup_region_setting_philippines_mindanao));
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubRegionSettingsPhiNationwide(PreferenceScreen preferenceScreen, Context themedContext, String parentId) {
        int[] cityIndex;
        int proResArray;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        String str = parentId;
        if (TextUtils.equals(MenuConfigManager.SETUP_REGION_SETTING_LUZON, str)) {
            proResArray = R.array.menu_setup_region_setting_phi_pro_luzon_array;
            cityIndex = RegionConst.phiProsCityLuzong;
            preferenceScreen2.setTitle((int) R.string.menu_setup_region_setting_philippines_luzon);
        } else if (TextUtils.equals(MenuConfigManager.SETUP_REGION_SETTING_VISAYAS, str)) {
            proResArray = R.array.menu_setup_region_setting_phi_pro_visayas_array;
            cityIndex = RegionConst.phiProsCityVisayas;
            preferenceScreen2.setTitle((int) R.string.menu_setup_region_setting_philippines_visayas);
        } else {
            proResArray = R.array.menu_setup_region_setting_phi_pro_mindanao_array;
            cityIndex = RegionConst.phiProsCityMindanao;
            preferenceScreen2.setTitle((int) R.string.menu_setup_region_setting_philippines_mindanao);
        }
        String[] subtitleEntry = this.mContext.getResources().getStringArray(proResArray);
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        int itemPosition = SaveValue.getInstance(this.mContext).readValue(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING);
        int selectPosition = SaveValue.getInstance(this.mContext).readValue(MenuConfigManager.SETUP_REGION_SETTING_SELECT);
        String pId = SaveValue.getInstance(this.mContext).readStrValue(MenuConfigManager.SETUP_REGION_SETTING);
        MtkLog.d(TAG, "item positon:" + itemPosition + ",select position:" + selectPosition + ",pId:" + pId);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < subtitleEntry.length) {
                int defaultValue = i2 == itemPosition ? selectPosition : 0;
                String[] entryVlaue = this.mContext.getResources().getStringArray(cityIndex[i2]);
                preferenceScreen2.addPreference(util2.createListPreference(str + i2, subtitleEntry[i2], true, entryVlaue, defaultValue));
                i = i2 + 1;
            } else {
                this.regionSettingScreen = preferenceScreen2;
                return preferenceScreen2;
            }
        }
    }

    private PreferenceScreen addSetupSubGingaSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        boolean z = true;
        preferenceScreen.addPreference(util2.createSwitchPreference("g_ginga__ginga_enable", (int) R.string.menu_setup_ginga_enable, this.mTV.getConfigValue("g_ginga__ginga_enable") != 0));
        if (this.mTV.getConfigValue("g_ginga__ginga_auto_start") == 0) {
            z = false;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference("g_ginga__ginga_auto_start", (int) R.string.menu_setup_ginga_auto_start_app, z));
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubOADSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        if (!SaveValue.getInstance(this.mContext).readBooleanValue("Auto_Download_aod")) {
            SaveValue.getInstance(this.mContext).saveBooleanValue("Auto_Download_aod", true);
            this.mConfigManager.setValue("g_oad__oad_sel_options_auto_download", 1);
        }
        preferenceScreen.addPreference(util2.createListPreference("g_oad__oad_sel_options_auto_download", (int) R.string.menu_setup_oad_set_auto_dl, true, this.mContext.getResources().getStringArray(R.array.menu_setup_oad_auto_download_array), this.mConfigManager.getDefault("g_oad__oad_sel_options_auto_download")));
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubSubtitleSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        int arrayID;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createListPreference("g_subtitle__subtitle_enable", (int) R.string.menu_setup_analog_subtitle, true, this.mContext.getResources().getStringArray(R.array.menu_setup_analog_subtitle_array), this.mConfigManager.getDefault("g_subtitle__subtitle_enable")));
        if (this.mTV.isIDNCountry() || this.mTV.isMYSCountry() || this.mTV.isAUSCountry() || this.mTV.isVNMCountry()) {
            arrayID = R.array.menu_tv_subtitle_language_in_mys_aus_tha_vnm_array;
        } else if (this.mTV.isSQPCountry()) {
            arrayID = R.array.menu_tv_subtitle_language_sgp_array;
        } else if (this.mTV.isNZLCountry()) {
            arrayID = R.array.menu_tv_subtitle_language_nzl_array;
        } else if (CommonIntegration.isEUPARegion()) {
            arrayID = R.array.menu_tv_subtitle_language_pa_array;
        } else {
            arrayID = R.array.menu_tv_subtitle_language_eu_array;
        }
        String[] subtitleEntry = this.mContext.getResources().getStringArray(arrayID);
        preferenceScreen.addPreference(util2.createListPreference("g_subtitle__subtitle_lang", (int) R.string.menu_setup_digital_subtitle_lang, true, subtitleEntry, util2.mOsdLanguage.getSubtitleLanguage("g_subtitle__subtitle_lang")));
        preferenceScreen.addPreference(util2.createListPreference("g_subtitle__subtitle_lang_2nd", (int) R.string.menu_setup_digital_subtitle_lang_2nd, true, subtitleEntry, util2.mOsdLanguage.getSubtitleLanguage("g_subtitle__subtitle_lang_2nd")));
        preferenceScreen.addPreference(util2.createListPreference("g_subtitle__subtitle_attr", (int) R.string.menu_setup_subtitle_type, true, this.mContext.getResources().getStringArray(R.array.menu_setup_subtitle_type_array), this.mConfigManager.getDefault("g_subtitle__subtitle_attr")));
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubTeletextSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        if (CommonIntegration.isCNRegion()) {
            this.dTLanguage = this.mContext.getResources().getStringArray(R.array.menu_setup_digital_teletext_language_array_cn);
        } else {
            this.dTLanguage = this.mContext.getResources().getStringArray(R.array.menu_setup_digital_teletext_language_array);
        }
        if (CommonIntegration.isCNRegion()) {
            this.dPLanguage = this.mContext.getResources().getStringArray(R.array.menu_setup_decoding_page_language_array_cn);
        } else {
            this.dPLanguage = this.mContext.getResources().getStringArray(R.array.menu_setup_decoding_page_language_array);
        }
        this.tPLevel = this.mContext.getResources().getStringArray(R.array.menu_setup_ttx_presentation_level_array);
        if (CommonIntegration.isEURegion() && MarketRegionInfo.isFunctionSupport(6)) {
            preferenceScreen.addPreference(util2.createListPreference("g_ttx_lang__ttx_digtl_es_select", (int) R.string.menu_setup_digital_teletext_language, true, this.dTLanguage, this.mConfigManager.getDefault("g_ttx_lang__ttx_digtl_es_select")));
            preferenceScreen.addPreference(util2.createListPreference("g_ttx_lang__ttx_decode_lang", (int) R.string.menu_setup_decoding_page_language, true, this.dPLanguage, this.mConfigManager.getDefault("g_ttx_lang__ttx_decode_lang")));
        } else if (CommonIntegration.isCNRegion()) {
            preferenceScreen.addPreference(util2.createListPreference("g_ttx_lang__ttx_digtl_es_select", (int) R.string.menu_setup_digital_teletext_language, true, this.dTLanguage, this.mConfigManager.getDefault("g_ttx_lang__ttx_digtl_es_select")));
            preferenceScreen.addPreference(util2.createListPreference("g_ttx_lang__ttx_decode_lang", (int) R.string.menu_setup_decoding_page_language, true, this.dPLanguage, this.mConfigManager.getDefault("g_ttx_lang__ttx_decode_lang")));
        }
        return preferenceScreen;
    }

    private PreferenceScreen addSetupSubHbbtvSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        MtkLog.d(TAG, "addSetupSubHbbtvSettings :> start..");
        String[] hbbtvStrings = this.mContext.getResources().getStringArray(R.array.menu_hbbtv_do_not_track);
        String[] hbbtvcookieStrs = this.mContext.getResources().getStringArray(R.array.menu_hbbtv_cookie_setting);
        preferenceScreen.addPreference(util2.createSwitchPreference("g_menu__hbbtv", (int) R.string.menu_setup_HBBTV_Support, getDefaultBoolean(this.mConfigManager.getDefault("g_menu__hbbtv"))));
        preferenceScreen.addPreference(util2.createListPreference("g_menu__do_not_track", (int) R.string.menu_setup_HBBTV_not_track, true, hbbtvStrings, this.mConfigManager.getDefault("g_menu__do_not_track")));
        preferenceScreen.addPreference(util2.createListPreference("g_menu__allow_3rd_cookies", (int) R.string.menu_setup_HBBTV_cookie_settings, true, hbbtvcookieStrs, this.mConfigManager.getDefault("g_menu__allow_3rd_cookies")));
        preferenceScreen.addPreference(util2.createSwitchPreference("g_menu__persistent_storage", (int) R.string.menu_setup_HBBTV_persistent_storage, getDefaultBoolean(this.mConfigManager.getDefault("g_menu__persistent_storage"))));
        preferenceScreen.addPreference(util2.createSwitchPreference("g_menu__block_tracking_sites", (int) R.string.menu_setup_HBBTV_track_sites, getDefaultBoolean(this.mConfigManager.getDefault("g_menu__block_tracking_sites"))));
        preferenceScreen.addPreference(util2.createSwitchPreference("g_menu__dev_id", (int) R.string.menu_setup_HBBTV_deviceid, getDefaultBoolean(this.mConfigManager.getDefault("g_menu__dev_id"))));
        preferenceScreen.addPreference(util2.createDialogPreference(MenuConfigManager.HBBTV_RESET_DEVICE_ID, (int) R.string.menu_setup_HBBTV_reset_deviceid, (Dialog) new AlertDialog.Builder(this.mContext).setTitle(R.string.menu_setup_HBBTV_reset_deviceid).setMessage(R.string.menu_setup_HBBTV_reset_deviceid_message).setPositiveButton(R.string.menu_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                SettingsPreferenceScreen.this.mTV.setConfigValue("g_menu__dhbbtv_dev_id_timestamp", (int) (System.currentTimeMillis() / 1000));
            }
        }).create()));
        return preferenceScreen;
    }

    private PreferenceScreen addVideoSubVgaViSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        MtkLog.d(TAG, "addVideoSubVgaViSettings :> start..");
        Preference vgaAutoAdjust = util2.createClickPreference(MenuConfigManager.AUTO_ADJUST, (int) R.string.menu_video_auto_adjust);
        preferenceScreen.addPreference(vgaAutoAdjust);
        preferenceScreen.addPreference(util2.createProgressPreference("g_vga__vga_pos_h", R.string.menu_video_hposition, true));
        preferenceScreen.addPreference(util2.createProgressPreference("g_vga__vga_pos_v", R.string.menu_video_vposition, true));
        preferenceScreen.addPreference(util2.createProgressPreference("g_vga__vga_phase", R.string.menu_video_phase, true));
        preferenceScreen.addPreference(util2.createProgressPreference("g_vga__vga_clock", R.string.menu_video_clock, true));
        vgaAutoAdjust.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                MtkLog.d(SettingsPreferenceScreen.TAG, "onPreferenceClick " + preference);
                SettingsPreferenceScreen.this.autoAdjustInfo(SettingsPreferenceScreen.this.mContext.getString(R.string.menu_video_auto_adjust_info));
                Message message = SettingsPreferenceScreen.this.mHandler.obtainMessage();
                message.obj = preference;
                if (preference.getKey().equals(MenuConfigManager.AUTO_ADJUST)) {
                    SettingsPreferenceScreen.this.appTV.setAutoClockPhasePostion(SettingsPreferenceScreen.mNavIntegration.getCurrentFocus());
                    message.what = 115;
                } else if (preference.getKey().equals(MenuConfigManager.FV_AUTOCOLOR)) {
                    SettingsPreferenceScreen.this.appTV.setAutoColor(SettingsPreferenceScreen.mNavIntegration.getCurrentFocus());
                    message.what = 116;
                } else {
                    SettingsPreferenceScreen.this.appTV.setAutoClockPhasePostion(SettingsPreferenceScreen.mNavIntegration.getCurrentFocus());
                    message.what = 115;
                }
                SettingsPreferenceScreen.this.mHandler.sendMessageDelayed(message, 1000);
                SettingsPreferenceScreen.this.mConfigManager.setValueDefault(preference.getKey());
                return true;
            }
        });
        return preferenceScreen;
    }

    /* access modifiers changed from: private */
    public void autoAdjustInfo(String mShowMessage) {
        MtkLog.d(TAG, "autoAdjustInfo");
        this.autoAdjustDialog = new LiveTVDialog(this.mContext, 5);
        this.autoAdjustDialog.setMessage(mShowMessage);
        this.autoAdjustDialog.show();
        this.autoAdjustDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }

    private PreferenceScreen addParentalSubOpenVChipLevel(PreferenceScreen preferenceScreen, final Context themedContext, int dimNum) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(5);
        this.mTV.getOpenVCHIPPara().setDimIndex(dimNum);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(7);
        int levelNum = this.mTV.getOpenVchip().getLevelNum();
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(8);
        reginIndex = Integer.parseInt(this.regin);
        dimIndex = Integer.parseInt(this.dim);
        byte[] levelBlockStrings = this.mTV.getNewOpenVchipSetting(reginIndex, dimIndex).getLvlBlockData();
        MtkLog.d(TAG, "levelNum:" + levelNum + "levelBlockStrings:" + levelBlockStrings);
        int k = 0;
        for (int m = 0; m < levelBlockStrings.length; m++) {
            MtkLog.d(TAG, "print levelBlockStrings[ " + m + "]=" + levelBlockStrings[m]);
        }
        while (true) {
            int k2 = k;
            if (k2 >= levelNum) {
                return preferenceScreen;
            }
            this.mTV.getOpenVCHIPPara().setLevelIndex(k2 + 1);
            this.ItemName = MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL + k2;
            String textString = this.mTV.getOpenVchip().getLvlAbbrText();
            if (textString != null) {
                MtkLog.d(TAG, "textString:" + textString);
            } else {
                MtkLog.d(TAG, "textString == null");
                textString = "";
            }
            String textString2 = textString;
            MtkLog.d(TAG, "levelBlockStrings[" + k2 + "]:===levelBlockStrings:" + levelBlockStrings[k2]);
            StringBuilder sb = new StringBuilder();
            sb.append(MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL);
            sb.append(k2);
            Preference openvchipItemLevel = util2.createListPreference(sb.toString(), textString2, true, new String[]{"OFF", "ON"}, (int) levelBlockStrings[k2]);
            byte[] lvlBlockData = this.mTV.getNewOpenVchipSetting(reginIndex, dimIndex).getLvlBlockData();
            openvchipItemLevel.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = Integer.parseInt(preference.getKey().substring(MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL.length(), preference.getKey().length()));
                    byte iniValue = TVContent.getInstance(SettingsPreferenceScreen.this.mContext).getNewOpenVchipSetting(SettingsPreferenceScreen.reginIndex, SettingsPreferenceScreen.dimIndex).getLvlBlockData()[index];
                    MtkLog.d("yiqinghuang", "reginIndex =" + SettingsPreferenceScreen.reginIndex + ",dimIndex =" + SettingsPreferenceScreen.dimIndex + "index =" + index);
                    byte[] lvlBlockData = SettingsPreferenceScreen.this.mTV.getNewOpenVchipSetting(SettingsPreferenceScreen.reginIndex, SettingsPreferenceScreen.dimIndex).getLvlBlockData();
                    int value = Integer.parseInt((String) newValue);
                    SaveValue.getInstance(themedContext).saveValue(preference.getKey(), value);
                    if (value == iniValue) {
                        return true;
                    }
                    EditChannel.getInstance(SettingsPreferenceScreen.this.mContext).setOpenVCHIP(SettingsPreferenceScreen.reginIndex, SettingsPreferenceScreen.dimIndex, index);
                    return true;
                }
            });
            preferenceScreen.addPreference(openvchipItemLevel);
            k = k2 + 1;
        }
    }

    private PreferenceScreen addParentalSubOpenVChipDim(PreferenceScreen preferenceScreen, Context themedContext, int reginNum) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(1);
        this.mTV.getOpenVCHIPPara().setRegionIndex(reginNum);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(4);
        int dimNum = this.mTV.getOpenVchip().getDimNum();
        for (int j = 0; j < dimNum; j++) {
            this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(5);
            this.mTV.getOpenVCHIPPara().setDimIndex(j);
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.PARENTAL_OPEN_VCHIP_DIM + j, this.mTV.getOpenVchip().getDimText()));
        }
        return preferenceScreen;
    }

    private PreferenceScreen addParentalSubOpenVChipSetting(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(0);
        int regionNum = this.mTV.getOpenVchip().getRegionNum();
        for (int i = 0; i < regionNum; i++) {
            this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(1);
            this.mTV.getOpenVCHIPPara().setRegionIndex(i);
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.PARENTAL_OPEN_VCHIP_REGIN + i, this.mTV.getOpenVchip().getRegionText()));
        }
        return preferenceScreen;
    }

    private boolean isContainVchip() {
        if (this.mainPreferenceScreen.findPreference("g_video__screen_mode") == null) {
            return false;
        }
        return true;
    }

    private PreferenceScreen addParentalSecondChannelScheduleBlock(PreferenceScreen preferenceScreen, Context themedContext) {
        String description;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] optionValue = {"Off", "Block"};
        for (MtkTvChannelInfoBase infobase : MenuDataHelper.getInstance(this.mContext).getTVChannelList()) {
            int tid = infobase.getChannelId();
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(this.mDataHelper.getDisplayChNumber(tid));
            sb.append("        ");
            sb.append(infobase.getServiceName() == null ? "" : infobase.getServiceName());
            String name = sb.toString();
            if (this.mEditChannel.getSchBlockType(infobase.getChannelId()) == 0) {
                description = optionValue[0];
            } else {
                description = optionValue[1];
            }
            Preference refer = util2.createPreference(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST + tid, name);
            refer.setSummary((CharSequence) description);
            preferenceScreen.addPreference(refer);
        }
        return preferenceScreen;
    }

    private PreferenceScreen addParentalLastSubChannelScheduleBlock(PreferenceScreen preferenceScreen, Context themedContext, String parentId) {
        boolean isEndTimeEnable;
        boolean isStartTimeEnable;
        boolean isEndDateEnable;
        boolean isStartDateEnable;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        List<MtkTvChannelInfoBase> list = MenuDataHelper.getInstance(this.mContext).getTVChannelList();
        MtkTvChannelInfoBase tempInfoBase = null;
        int channelId = -1;
        for (MtkTvChannelInfoBase infobase : list) {
            if (TextUtils.equals(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST + infobase.getChannelId(), parentId)) {
                MtkTvChannelInfoBase tempInfoBase2 = infobase;
                int channelId2 = tempInfoBase2.getChannelId();
                block = this.mEditChannel.getSchBlockType(infobase);
                MtkLog.d(TAG, "block: " + block);
                tempInfoBase = tempInfoBase2;
                channelId = channelId2;
            }
        }
        String str = parentId;
        if (tempInfoBase == null) {
            MtkTvChannelInfoBase mtkTvChannelInfoBase = tempInfoBase;
        } else if (channelId == -1) {
            List<MtkTvChannelInfoBase> list2 = list;
            MtkTvChannelInfoBase mtkTvChannelInfoBase2 = tempInfoBase;
        } else {
            String[] valueStrings = this.mContext.getResources().getStringArray(R.array.menu_parental_block_channel_schedule_operation_array);
            int index = this.mConfigManager.getDefault(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE + channelId);
            String operationModeSummary = valueStrings[index];
            MtkLog.d(TAG, "index: " + index);
            Preference refer = util2.createListPreference(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE + channelId, (int) R.string.menu_parental_channel_schedule_block_operation_mode, true, valueStrings, index);
            refer.setSummary((CharSequence) operationModeSummary);
            preferenceScreen2.addPreference(refer);
            if (block == 0) {
                isStartDateEnable = false;
                isEndDateEnable = false;
                isStartTimeEnable = false;
                isEndTimeEnable = false;
                Preference preference = refer;
            } else {
                Preference preference2 = refer;
                if (block == 1) {
                    isStartDateEnable = false;
                    isStartTimeEnable = true;
                    isEndDateEnable = false;
                    isEndTimeEnable = true;
                } else {
                    isStartDateEnable = true;
                    isStartTimeEnable = true;
                    isEndDateEnable = true;
                    isEndTimeEnable = true;
                }
            }
            SaveValue instance = SaveValue.getInstance(this.mContext);
            StringBuilder sb = new StringBuilder();
            List<MtkTvChannelInfoBase> list3 = list;
            sb.append(MenuConfigManager.TIME_START_DATE);
            sb.append(channelId);
            String startDate = instance.readStrValue(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            MtkTvChannelInfoBase mtkTvChannelInfoBase3 = tempInfoBase;
            sb2.append("addParentalLastSubChannelScheduleBlock: startDate:");
            sb2.append(startDate);
            Log.d(TAG, sb2.toString());
            Preference startDatePreference = util2.createFragmentPreference(MenuConfigManager.TIME_START_DATE + channelId, R.string.menu_parental_channel_schedule_start_date, isStartDateEnable, DatePicker.class.getName());
            startDatePreference.setSummary((CharSequence) startDate);
            preferenceScreen2.addPreference(startDatePreference);
            SaveValue instance2 = SaveValue.getInstance(this.mContext);
            StringBuilder sb3 = new StringBuilder();
            String str2 = startDate;
            sb3.append(MenuConfigManager.TIME_START_TIME);
            sb3.append(channelId);
            String startTime = instance2.readStrValue(sb3.toString());
            boolean z = isStartDateEnable;
            Preference startTimePreference = util2.createFragmentPreference(MenuConfigManager.TIME_START_TIME + channelId, R.string.menu_parental_channel_schedule_start_time, isStartTimeEnable, TimePicker.class.getName());
            startTimePreference.setSummary((CharSequence) startTime);
            preferenceScreen2.addPreference(startTimePreference);
            SaveValue instance3 = SaveValue.getInstance(this.mContext);
            StringBuilder sb4 = new StringBuilder();
            String str3 = startTime;
            sb4.append(MenuConfigManager.TIME_END_DATE);
            sb4.append(channelId);
            String endDate = instance3.readStrValue(sb4.toString());
            Preference preference3 = startTimePreference;
            Preference endDatePreference = util2.createFragmentPreference(MenuConfigManager.TIME_END_DATE + channelId, R.string.menu_parental_channel_schedule_end_date, isEndDateEnable, DatePicker.class.getName());
            endDatePreference.setSummary((CharSequence) endDate);
            preferenceScreen2.addPreference(endDatePreference);
            String endTime = SaveValue.getInstance(this.mContext).readStrValue(MenuConfigManager.TIME_END_TIME + channelId);
            String str4 = endDate;
            Preference endTimePreference = util2.createFragmentPreference(MenuConfigManager.TIME_END_TIME + channelId, R.string.menu_parental_channel_schedule_end_time, isEndTimeEnable, TimePicker.class.getName());
            endTimePreference.setSummary((CharSequence) endTime);
            preferenceScreen2.addPreference(endTimePreference);
            return preferenceScreen2;
        }
        MtkLog.d(TAG, "channel info is null or something is wrong!");
        return preferenceScreen2;
    }

    private PreferenceScreen addVideoSub3DSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        Preference m3DOsdDepth;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        MtkLog.d(TAG, "addVideoSub3DSettings  stsrt>>");
        this.m3DModeArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_mode_array);
        if (util2.mConfigManager.getDefault("g_video__vid_3d_nav_auto") == 1) {
            this.m3DModeArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_mode_array_for_1);
        } else if (util2.mConfigManager.getDefault("g_video__vid_3d_nav_auto") == 0) {
            this.m3DModeArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_mode_array_for_0);
        }
        this.m3DNavArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_nav_array);
        this.m3D2DArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_3t2switch_array);
        this.m3DImgSafetyArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_image_safety_array);
        this.m3DLrSwitchArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_lrswitch_array);
        Preference m3DMode = util2.createListPreference("g_video__vid_3d_mode", (int) R.string.menu_video_3d_mode, true, this.m3DModeArr, util2.mConfigManager.getDefault("g_video__vid_3d_mode"));
        Preference m3DNav = util2.createListPreference("g_video__vid_3d_nav_auto", (int) R.string.menu_video_3d_nav, true, this.m3DNavArr, util2.mConfigManager.getDefault("g_video__vid_3d_nav_auto"));
        Preference m3D2D = util2.createListPreference("g_video__vid_3d_to_2d", (int) R.string.menu_video_3d_3t2, true, this.m3D2DArr, util2.mConfigManager.getDefault("g_video__vid_3d_to_2d"));
        Preference m3DDepthField = util2.createProgressPreference("g_video__vid_3d_fld_depth", R.string.menu_video_3d_depth_field, false);
        Preference m3DProtrude = util2.createProgressPreference("g_video__vid_3d_protruden", R.string.menu_video_3d_protrude, false);
        Preference m3DDistance = util2.createProgressPreference("g_video__vid_3d_distance", R.string.menu_video_3d_distance, false);
        String[] strArr = this.m3DImgSafetyArr;
        String[] strArr2 = strArr;
        Object obj = "g_video__vid_3d_img_sfty";
        Preference m3DDistance2 = m3DDistance;
        Preference m3DImgSafety = util2.createListPreference("g_video__vid_3d_img_sfty", (int) R.string.menu_video_3d_image_safety, true, strArr2, util2.mConfigManager.getDefault("g_video__vid_3d_img_sfty"));
        String[] strArr3 = this.m3DLrSwitchArr;
        String[] strArr4 = strArr3;
        Object obj2 = "g_video__vid_3d_lr_switch";
        Preference m3DImgSafety2 = m3DImgSafety;
        Preference m3DLrSwitch = util2.createListPreference("g_video__vid_3d_lr_switch", (int) R.string.menu_video_3d_leftright, true, strArr4, util2.mConfigManager.getDefault("g_video__vid_3d_lr_switch"));
        Preference m3DOsdDepth2 = util2.createProgressPreference("g_video__vid_3d_osd_depth", R.string.menu_video_3d_osd, true);
        ArrayList<Boolean> m3DConfigList = this.mConfigManager.get3DConfig();
        boolean m3DModeFlag = m3DConfigList.get(0).booleanValue();
        boolean m3DNavFlag = m3DConfigList.get(1).booleanValue();
        boolean m3D2DFlag = m3DConfigList.get(2).booleanValue();
        boolean m3DDepthFieldFlag = m3DConfigList.get(3).booleanValue();
        boolean m3DProtrudeFlag = m3DConfigList.get(4).booleanValue();
        Object obj3 = "g_video__vid_3d_osd_depth";
        boolean m3DDistanceFlag = m3DConfigList.get(5).booleanValue();
        PreferenceUtil preferenceUtil = util2;
        boolean m3DImgSafetyFlag = m3DConfigList.get(6).booleanValue();
        boolean m3DLrSwitchFlag = m3DConfigList.get(7).booleanValue();
        Preference m3DOsdDepth3 = m3DOsdDepth2;
        boolean m3DOsdDepthFlag = m3DConfigList.get(8).booleanValue();
        m3DMode.setEnabled(m3DModeFlag);
        m3DNav.setEnabled(m3DNavFlag);
        m3D2D.setEnabled(m3D2DFlag);
        m3DDepthField.setEnabled(m3DDepthFieldFlag);
        m3DProtrude.setEnabled(m3DProtrudeFlag);
        boolean z = m3DProtrudeFlag;
        Preference m3DDistance3 = m3DDistance2;
        m3DDistance3.setEnabled(m3DDistanceFlag);
        boolean z2 = m3DDistanceFlag;
        Preference m3DImgSafety3 = m3DImgSafety2;
        m3DImgSafety3.setEnabled(m3DImgSafetyFlag);
        m3DLrSwitch.setEnabled(m3DLrSwitchFlag);
        if (!CommonIntegration.isCNRegion()) {
            boolean z3 = m3DLrSwitchFlag;
            m3DOsdDepth = m3DOsdDepth3;
            m3DOsdDepth.setEnabled(m3DOsdDepthFlag);
        } else {
            boolean z4 = m3DLrSwitchFlag;
            m3DOsdDepth = m3DOsdDepth3;
        }
        boolean z5 = m3DOsdDepthFlag;
        PreferenceScreen preferenceScreen3 = preferenceScreen;
        preferenceScreen3.addItemFromInflater(m3DMode);
        preferenceScreen3.addItemFromInflater(m3DNav);
        preferenceScreen3.addItemFromInflater(m3D2D);
        preferenceScreen3.addItemFromInflater(m3DDepthField);
        preferenceScreen3.addItemFromInflater(m3DProtrude);
        preferenceScreen3.addItemFromInflater(m3DDistance3);
        preferenceScreen3.addItemFromInflater(m3DImgSafety3);
        preferenceScreen3.addItemFromInflater(m3DLrSwitch);
        if (!CommonIntegration.isCNRegion()) {
            preferenceScreen3.addItemFromInflater(m3DOsdDepth);
        }
        return preferenceScreen3;
    }

    private PreferenceScreen addAudioSubvisuallyimpairedSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        MtkLog.d(TAG, "addAudioSubvisuallyimpairedSettings  stsrt>>");
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.menu_audio_visually_speaker_array);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.menu_audio_visually_headphone_array);
        this.ItemName = "g_audio__aud_ad_speaker";
        Preference speaker = util2.createSwitchPreference(this.ItemName, (int) R.string.menu_audio_visually_speaker, util2.mConfigManager.getDefault(this.ItemName) != 0);
        speaker.setOnPreferenceChangeListener(this.mvisuallyimpairedChangeListener);
        this.ItemName = "g_audio__aud_ad_hdphone";
        Preference Headphone = util2.createSwitchPreference(this.ItemName, (int) R.string.menu_audio_visually_headphone, util2.mConfigManager.getDefault(this.ItemName) != 0);
        Headphone.setOnPreferenceChangeListener(this.mvisuallyimpairedChangeListener);
        this.visuallyvolume = util2.createProgressPreference("g_audio__aud_ad_volume", R.string.menu_audio_visually_volume, true);
        this.ItemName = "g_audio__aud_ad_fade_pan";
        Preference panaAndFade = util2.createSwitchPreference(this.ItemName, (int) R.string.menu_audio_visually_pan_and_fade, util2.mConfigManager.getDefault(this.ItemName) != 0);
        this.ItemName = "g_menu__audioinfo";
        this.visuallyImpairedAudioInfo = util2.createPreference(this.ItemName, (int) R.string.menu_audio_visually_impaired_audio);
        preferenceScreen.addPreference(speaker);
        preferenceScreen.addPreference(Headphone);
        if (util2.mConfigManager.getDefault("g_audio__aud_ad_speaker") == 0 && util2.mConfigManager.getDefault("g_audio__aud_ad_hdphone") == 0) {
            this.visuallyvolume.setEnabled(false);
        } else {
            this.visuallyvolume.setEnabled(true);
        }
        preferenceScreen.addPreference(this.visuallyvolume);
        if (CommonIntegration.isEURegion()) {
            preferenceScreen.addPreference(panaAndFade);
            preferenceScreen.addPreference(this.visuallyImpairedAudioInfo);
        }
        return preferenceScreen;
    }

    private PreferenceScreen addAudioSubvisuallyimpairedAudioSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        Preference soundtrackItem;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.mTV.setConfigValue("g_menu__audioinfoinit", 0);
        int soundListsize = this.mTV.getConfigValue("g_menu__audioinfototal");
        int viIndex = this.mTV.getConfigValue("g_menu__audioinfocurrent");
        Log.d("MENUAudioActivity", "soundListsize: " + soundListsize);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= soundListsize) {
                return preferenceScreen2;
            }
            String ItemName2 = "audioinfogetstring_" + i2;
            String soundString = this.mTV.getConfigString(ItemName2);
            MtkLog.d("MENUAudioActivity", "VisuallyImpaired:" + soundString);
            String[] itemValueStrings = new String[3];
            if (soundString != null) {
                itemValueStrings[0] = soundString;
                itemValueStrings[1] = "";
                itemValueStrings[2] = "";
            } else {
                itemValueStrings[0] = "";
                itemValueStrings[1] = "";
                itemValueStrings[2] = "";
            }
            if (viIndex == i2) {
                soundtrackItem = util2.createListPreference(ItemName2, new String("" + (i2 + 1)), true, new String[]{soundString}, 0);
            } else {
                soundtrackItem = util2.createListPreference(ItemName2, new String("" + (i2 + 1)), true, new String[]{soundString}, 1);
            }
            preferenceScreen2.addPreference(soundtrackItem);
            i = i2 + 1;
        }
    }

    private PreferenceScreen addAudioSubSoundTracks(PreferenceScreen preferenceScreen, Context themedContext) {
        List filterTracks;
        String trackId;
        String lang;
        MtkTvAVMode navTvAvMode;
        String itemKey;
        char c;
        Preference soundtrackItem;
        String lang2;
        String trackId2;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        MtkTvAVMode navTvAvMode2 = MtkTvAVMode.getInstance();
        SundryImplement sundry = SundryImplement.getInstanceNavSundryImplement(themedContext);
        String currentID = "";
        MtkLog.d(TAG, "start Log the audio track info ");
        MtkLog.d(TAG, "end Log the audio track info ");
        char c2 = 0;
        if (!MarketRegionInfo.isFunctionSupport(20)) {
            filterTracks = sundry.filterAudioTracksForNav(navTvAvMode2.getAudioAvailableRecord());
            if (navTvAvMode2.getCurrentAudio() != null) {
                currentID = String.valueOf(navTvAvMode2.getCurrentAudio().getAudioId());
            }
        } else if ("main" == CommonIntegration.getInstance().getCurrentFocus()) {
            filterTracks = sundry.filterAudioTracks(TurnkeyUiMainActivity.getInstance().getTvView().getTracks(0));
            currentID = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(0);
        } else {
            filterTracks = sundry.filterAudioTracks(TurnkeyUiMainActivity.getInstance().getPipView().getTracks(0));
            currentID = TurnkeyUiMainActivity.getInstance().getPipView().getSelectedTrack(0);
        }
        List filterTracks2 = filterTracks;
        String currentID2 = currentID;
        if (filterTracks2 != null) {
            for (Object O : filterTracks2) {
                if (O instanceof TvTrackInfo) {
                    trackId = ((TvTrackInfo) O).getId();
                    lang = sundry.getAudioTrackLangByIdNoNumb(trackId, filterTracks2);
                } else if (O instanceof TvProviderAudioTrackBase) {
                    trackId = String.valueOf(((TvProviderAudioTrackBase) O).getAudioId());
                    lang = sundry.getAudioTrackLangByIdNoNumbForNav(trackId, filterTracks2);
                }
                String lang3 = lang;
                String trackId3 = trackId;
                String itemKey2 = "soundtracksgetstring_" + trackId3;
                if (currentID2 == null || currentID2.length() <= 0 || !currentID2.equalsIgnoreCase(trackId3)) {
                    itemKey = itemKey2;
                    lang2 = lang3;
                    navTvAvMode = navTvAvMode2;
                    trackId2 = trackId3;
                    c = 0;
                    soundtrackItem = util2.createListPreference(itemKey, lang2, true, new String[]{lang2}, 1);
                } else {
                    String[] strArr = new String[1];
                    strArr[c2] = lang3;
                    itemKey = itemKey2;
                    lang2 = lang3;
                    navTvAvMode = navTvAvMode2;
                    trackId2 = trackId3;
                    soundtrackItem = util2.createListPreference(itemKey2, lang3, true, strArr, 0);
                    c = 0;
                }
                MtkLog.d(TAG, "soundtrackItem:" + itemKey + "---" + lang2 + "---" + trackId2);
                preferenceScreen2.addPreference(soundtrackItem);
                c2 = c;
                navTvAvMode2 = navTvAvMode;
            }
        } else {
            MtkTvAVMode mtkTvAVMode = navTvAvMode2;
        }
        return preferenceScreen2;
    }

    private void addAudioSettings(PreferenceScreen preferenceScreen, Context themedContext, PreferenceCategory category) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        MtkLog.d(TAG, "addAudioSettings  stsrt>>");
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.menu_audio_equalizer_array);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.menu_audio_type_array);
        if (!DataSeparaterUtil.getInstance().isAtvOnly()) {
            MtkLog.d(TAG, "sound track support : " + MarketRegionInfo.isFunctionSupport(41));
            Preference soundtrack = util2.createPreference("g_menu__soundtracks", (int) R.string.menu_audio_sound_tracks);
            preferenceScreen.addPreference(soundtrack);
            MtkLog.d(TAG, "isCurrentSourceDTV : " + this.mTV.isCurrentSourceDTV());
            if (!MarketRegionInfo.isFunctionSupport(41) || !this.mTV.isCurrentSourceDTV()) {
                soundtrack.setVisible(false);
                category.setVisible(false);
                return;
            }
            soundtrack.setVisible(true);
            category.setVisible(true);
        }
    }

    private PreferenceScreen addAudioSubViSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createSwitchPreference("g_audio__aud_ad_speaker", (int) R.string.menu_audio_visually_speaker, true));
        preferenceScreen.addPreference(util2.createSwitchPreference("g_audio__aud_ad_hdphone", (int) R.string.menu_audio_visually_headphone, true));
        return preferenceScreen;
    }

    private void addTvSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] tunerModeStr = this.mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
        String[] mts = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_channel_mts_array);
        String[] audioLanuage = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_array);
        MtkLog.d(TAG, "addTvSettings  ");
        Preference tvTunerMode = util2.createListPreference("g_bs__bs_src", (int) R.string.menu_tv_tuner_mode, true, tunerModeStr, util2.mConfigManager.getDefault("g_bs__bs_src"));
        if (this.mTV.isTshitRunning() || this.mTV.isPVrRunning()) {
            tvTunerMode.setEnabled(false);
        } else {
            tvTunerMode.setEnabled(true);
        }
        preferenceScreen2.addPreference(tvTunerMode);
        if (CommonIntegration.isUSRegion()) {
            Intent intentScanD = new Intent(this.mContext, ScanDialogActivity.class);
            intentScanD.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN);
            Preference channelScan = util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN, (int) R.string.menu_tv_channel_scan, intentScanD);
            if (this.mTV.isTshitRunning() || this.mTV.isPVrRunning()) {
                channelScan.setEnabled(false);
            } else {
                channelScan.setEnabled(true);
            }
            preferenceScreen2.addPreference(channelScan);
            Intent intentChannelSkip = new Intent(this.mContext, ChannelInfoActivity.class);
            TransItem trans = new TransItem(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST, "", 10004, 10004, 10004);
            intentChannelSkip.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SKIP);
            intentChannelSkip.putExtra("TransItem", trans);
            this.channelskip = util2.createPreference(MenuConfigManager.TV_CHANNEL_SKIP, (int) R.string.menu_tv_channel_skip, intentChannelSkip);
            if (!this.mTV.isCurrentSourceTv() || !CommonIntegration.getInstance().hasActiveChannel() || this.mTV.isTshitRunning() || this.mTV.isPVrRunning()) {
                this.channelskip.setEnabled(false);
            } else {
                this.channelskip.setEnabled(true);
            }
            preferenceScreen2.addPreference(this.channelskip);
            TransItem transItem = trans;
            preferenceScreen2.addPreference(util2.createListPreference("g_audio__aud_mts", (int) R.string.menu_tv_mts_us, true, mts, util2.mConfigManager.getDefault("g_audio__aud_mts")));
            preferenceScreen2.addPreference(util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, audioLanuage, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language")));
        }
    }

    private PreferenceScreen addCNChannel(PreferenceScreen preferenceScreen, Context themedContext) {
        Intent mScanIntent;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        if (DVBCCNScanner.mQuichScanSwitch && this.mTV.isCurrentSourceDTV() && CommonIntegration.getInstance().getTunerMode() == 1) {
            mScanIntent = new Intent(this.mContext, ScanViewActivity.class);
            mScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN);
        } else if (this.mTV.isCurrentSourceATV()) {
            mScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
            mScanIntent.putExtra("ActionID", MenuConfigManager.TV_SYSTEM);
            mScanIntent.putExtra("ActionParentID", MenuConfigManager.TV_CHANNEL_SCAN);
        } else {
            mScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
            mScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN);
        }
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN, this.mContext.getString(R.string.menu_tv_channel_scan), mScanIntent));
        this.ItemName = MenuConfigManager.TV_UPDATE_SCAN;
        if (this.mTV.isCurrentSourceATV()) {
            mScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
            mScanIntent.putExtra("ActionID", MenuConfigManager.TV_SYSTEM);
            mScanIntent.putExtra("ActionParentID", MenuConfigManager.TV_UPDATE_SCAN);
        }
        this.updateScan = util2.createPreference(this.ItemName, this.mContext.getString(R.string.menu_tv_update_scan), mScanIntent);
        this.ItemName = MenuConfigManager.TV_ANALOG_SCAN;
        if (this.mTV.isCurrentSourceATV()) {
            mScanIntent = new Intent(this.mContext, ScanViewActivity.class);
            mScanIntent.putExtra("ActionID", this.ItemName);
        }
        Preference analogScan = util2.createPreference(this.ItemName, this.mContext.getString(R.string.menu_tv_analog_manual_scan), mScanIntent);
        this.ItemName = MenuConfigManager.TV_SINGLE_RF_SCAN_CN;
        if (this.mTV.isCurrentSourceDTV()) {
            mScanIntent = new Intent(this.mContext, ScanViewActivity.class);
            mScanIntent.putExtra("ActionID", this.ItemName);
        }
        Preference RFScan = util2.createPreference(this.ItemName, this.mContext.getString(R.string.menu_tv_single_rf_scan), mScanIntent);
        this.ItemName = MenuConfigManager.TV_CHANNEL_EDIT;
        Intent mScanIntent2 = new Intent(this.mContext, ChannelInfoActivity.class);
        mScanIntent2.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_EDIT_LIST, "", 10004, 10004, 10004));
        mScanIntent2.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_EDIT);
        this.channelEdit = util2.createPreference(this.ItemName, this.mContext.getString(R.string.menu_tv_channel_edit), mScanIntent2);
        this.cleanList = util2.createClickPreference(MenuConfigManager.TV_CHANNEL_CLEAR, this.mContext.getString(R.string.menu_tv_clear_channel_list), (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
        if (!CommonIntegration.getInstance().isCurrentSourceDTV()) {
            preferenceScreen.addPreference(this.updateScan);
            preferenceScreen.addPreference(analogScan);
        } else {
            preferenceScreen.addPreference(RFScan);
        }
        preferenceScreen.addPreference(this.channelEdit);
        preferenceScreen.addPreference(this.cleanList);
        if (mNavIntegration.hasActiveChannel()) {
            this.channelEdit.setEnabled(true);
            this.cleanList.setEnabled(true);
        } else {
            this.channelEdit.setEnabled(false);
            this.cleanList.setEnabled(false);
        }
        return preferenceScreen;
    }

    private PreferenceScreen addSAChannel(PreferenceScreen preferenceScreen, Context themedContext) {
        boolean z;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_channel_mts_array);
        boolean isPVRSrt = this.mTV.isPVrRunning();
        Intent sAChannelScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
        sAChannelScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN);
        Preference channelScan = util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN, (int) R.string.menu_tv_channel_scan, sAChannelScanIntent);
        preferenceScreen2.addPreference(channelScan);
        if (DataSeparaterUtil.getInstance().isAtvOnly()) {
            this.updateScan = null;
        } else {
            Intent sAUpdateScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
            sAUpdateScanIntent.putExtra("ActionID", MenuConfigManager.TV_UPDATE_SCAN);
            this.updateScan = util2.createPreference(MenuConfigManager.TV_UPDATE_SCAN, (int) R.string.menu_tv_update_scan, sAUpdateScanIntent);
            preferenceScreen2.addPreference(this.updateScan);
        }
        Intent skipSAIntent = new Intent(this.mContext, ChannelInfoActivity.class);
        skipSAIntent.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST, "", 10004, 10004, 10004));
        skipSAIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SKIP);
        this.channelskip = util2.createPreference(MenuConfigManager.TV_CHANNEL_SKIP, (int) R.string.menu_tv_channel_skip, skipSAIntent);
        preferenceScreen2.addPreference(this.channelskip);
        Intent editSAIntent = new Intent(this.mContext, ChannelInfoActivity.class);
        editSAIntent.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_EDIT_LIST, "", 10004, 10004, 10004));
        editSAIntent.putExtra("ActionID", MenuConfigManager.TV_SA_CHANNEL_EDIT);
        this.saChannelEdit = util2.createPreference(MenuConfigManager.TV_SA_CHANNEL_EDIT, (int) R.string.menu_tv_channel_edit, editSAIntent);
        preferenceScreen2.addPreference(this.saChannelEdit);
        Intent fineSAIntent = new Intent(this.mContext, ChannelInfoActivity.class);
        fineSAIntent.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNELFINE_TUNE_EDIT_LIST, "", 10004, 10004, 10004));
        fineSAIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNELFINE_TUNE);
        this.saChannelFine = util2.createPreference(MenuConfigManager.TV_CHANNELFINE_TUNE, (int) R.string.menu_tv_channelfine_tune, fineSAIntent);
        preferenceScreen2.addPreference(this.saChannelFine);
        if (isPVRSrt) {
            z = false;
        } else if (this.mTV.isTshitRunning()) {
            PreferenceUtil preferenceUtil = util2;
            z = false;
        } else {
            channelScan.setEnabled(true);
            if (this.updateScan != null) {
                this.updateScan.setEnabled(true);
            }
            MtkTvChannelInfoBase mtktvChannelinfo = mNavIntegration.getCurChInfoByTIF();
            if (mtktvChannelinfo == null || !mtktvChannelinfo.isAnalogService()) {
            } else {
                PreferenceUtil preferenceUtil2 = util2;
                if (!TIFFunctionUtil.checkChMask(mtktvChannelinfo, TIFFunctionUtil.CH_FAKE_MASK, TIFFunctionUtil.CH_FAKE_VAL)) {
                    this.saChannelFine.setEnabled(true);
                    bindChannelPreference();
                    this.mDataHelper.changePreferenceEnable();
                    return preferenceScreen2;
                }
            }
            this.saChannelFine.setEnabled(false);
            bindChannelPreference();
            this.mDataHelper.changePreferenceEnable();
            return preferenceScreen2;
        }
        channelScan.setEnabled(z);
        if (this.updateScan != null) {
            this.updateScan.setEnabled(z);
        }
        this.channelskip.setEnabled(z);
        this.saChannelEdit.setEnabled(z);
        this.saChannelFine.setEnabled(z);
        bindChannelPreference();
        this.mDataHelper.changePreferenceEnable();
        return preferenceScreen2;
    }

    private void addCNTvSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        Context context = themedContext;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] Freeze_Change_Ch = themedContext.getResources().getStringArray(R.array.menu_tv_freeze_channel_array);
        String[] Tshift_Option = themedContext.getResources().getStringArray(R.array.menu_tv_tshift_config_array);
        String[] stringArray = themedContext.getResources().getStringArray(R.array.menu_tv_scan_mode_array);
        String[] tunerModeStr = themedContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
        this.ItemName = "g_bs__bs_src";
        Preference tvTunerMode = util2.createListPreference(this.ItemName, context.getString(R.string.menu_tv_tuner_mode), true, tunerModeStr, this.mConfigManager.getDefault("g_bs__bs_src"));
        if (this.mTV.isPVrRunning() || this.mTV.isTshitRunning()) {
            tvTunerMode.setEnabled(false);
        } else {
            tvTunerMode.setEnabled(true);
        }
        if (CommonIntegration.getInstance().isCurrentSourceDTV()) {
            preferenceScreen2.addPreference(tvTunerMode);
        }
        this.ItemName = MenuConfigManager.TV_CHANNEL;
        preferenceScreen2.addPreference(util2.createPreference(this.ItemName, context.getString(R.string.menu_tv_channels)));
        this.ItemName = "g_menu__ch_frz_chg";
        Preference freezeChannel = util2.createListPreference(this.ItemName, context.getString(R.string.menu_tv_freeze_channel), true, Freeze_Change_Ch, this.mConfigManager.getDefault(this.ItemName));
        if (CommonIntegration.getInstance().isCurrentSourceATV()) {
            preferenceScreen2.addPreference(freezeChannel);
        }
        this.ItemName = "DTV_TSHIFT_OPTION";
        Preference preference = freezeChannel;
        Preference createListPreference = util2.createListPreference(this.ItemName, context.getString(R.string.menu_tv_tshift_config), true, Tshift_Option, this.mConfigManager.getValueFromPrefer(this.ItemName));
        this.ItemName = "DTV_DEVICE_INFO";
        MtkLog.e(TAG, "show CN device info");
    }

    private void addUS_SATvSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        boolean z;
        Preference channelScan;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        Context context = themedContext;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] tunerModeStr = themedContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
        String[] mts = themedContext.getResources().getStringArray(R.array.menu_tv_audio_channel_mts_array);
        boolean isPVRSrt = this.mTV.isPVrRunning();
        Preference tvTunerMode = util2.createListPreference("g_bs__bs_src", (int) R.string.menu_tv_tuner_mode, true, tunerModeStr, util2.mConfigManager.getDefault("g_bs__bs_src"));
        preferenceScreen2.addPreference(tvTunerMode);
        preferenceScreen2.addPreference(util2.createListPreference("g_audio__aud_mts", (int) R.string.menu_tv_mts_us, true, mts, util2.mConfigManager.getDefault("g_audio__aud_mts")));
        if (CommonIntegration.isUSRegion()) {
            String[] audioLanuage = themedContext.getResources().getStringArray(R.array.menu_tv_audio_language_array);
            Intent uSChannelScanIntent = new Intent(context, ScanDialogActivity.class);
            uSChannelScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN);
            Preference channelScan2 = util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN, (int) R.string.menu_tv_channel_scan, uSChannelScanIntent);
            preferenceScreen2.addPreference(channelScan2);
            if (isPVRSrt || this.mTV.isTshitRunning()) {
                channelScan2.setEnabled(false);
            } else {
                channelScan2.setEnabled(true);
            }
            Preference channelScan3 = channelScan2;
            Intent intent = uSChannelScanIntent;
            preferenceScreen2.addPreference(util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, audioLanuage, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language")));
            Intent skipIntent = new Intent(context, ChannelInfoActivity.class);
            skipIntent.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST, "", 10004, 10004, 10004));
            skipIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SKIP);
            this.channelskip = util2.createPreference(MenuConfigManager.TV_CHANNEL_SKIP, (int) R.string.menu_tv_channel_skip, skipIntent);
            if (isPVRSrt) {
                channelScan = channelScan3;
            } else if (this.mTV.isTshitRunning()) {
                channelScan = channelScan3;
            } else {
                tvTunerMode.setEnabled(true);
                channelScan3.setEnabled(true);
                z = false;
                preferenceScreen2.addPreference(this.channelskip);
            }
            z = false;
            tvTunerMode.setEnabled(false);
            channelScan.setEnabled(false);
            this.channelskip.setEnabled(false);
            preferenceScreen2.addPreference(this.channelskip);
        } else {
            z = false;
        }
        if (CommonIntegration.isSARegion()) {
            Preference tvChannel = util2.createPreference(MenuConfigManager.TV_CHANNEL, (int) R.string.menu_tv_channel_setup);
            preferenceScreen2.addPreference(tvChannel);
            if (isPVRSrt || this.mTV.isTshitRunning()) {
                tvChannel.setEnabled(z);
            } else {
                tvChannel.setEnabled(true);
            }
            if (isPVRSrt || this.mTV.isTshitRunning()) {
                tvTunerMode.setEnabled(z);
            } else {
                tvTunerMode.setEnabled(true);
            }
            bindChannelPreference();
            this.mDataHelper.changePreferenceEnable();
        }
    }

    private void addEUTvSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        Preference tvCountryRegion;
        Preference tvCountryRegion2;
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] tunerModeStr = themedContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
        if (!MarketRegionInfo.isFunctionSupport(4) || MarketRegionInfo.isFunctionSupport(16)) {
            this.tunerModeStrEu = tunerModeStr;
        } else if (ScanContent.getDVBSOperatorList(this.mContext).size() == 0) {
            this.tunerModeStrEu = themedContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu_sat_only);
        } else {
            this.tunerModeStrEu = themedContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu);
        }
        String[] mts = themedContext.getResources().getStringArray(R.array.menu_tv_audio_channel_array);
        String[] audioLanuage = themedContext.getResources().getStringArray(R.array.menu_tv_audio_language_eu_array);
        String[] audioLanuage2nd = themedContext.getResources().getStringArray(R.array.menu_tv_audio_language_eu_array);
        Preference tvTunerModeEU = util2.createListPreference("g_bs__bs_src", (int) R.string.menu_tv_tuner_mode, true, this.tunerModeStrEu, this.mConfigManager.getDefault("g_bs__bs_src"));
        tvFirstVoice = util2.createListPreference("g_audio__aud_mts", (int) R.string.menu_tv_mts, true, mts, util2.mConfigManager.getDefault("g_audio__aud_mts"));
        String[] regionStringValue = themedContext.getResources().getStringArray(R.array.menu_tv_country_region_id_eu);
        String[] regionStrings = new String[regionStringValue.length];
        for (int i = 0; i < regionStringValue.length; i++) {
            regionStrings[i] = themedContext.getResources().getString(R.string.menu_arrays_Country_Region_ID_x, new Object[]{Integer.valueOf(Integer.parseInt(regionStringValue[i]))});
        }
        String[] strArr = tunerModeStr;
        String[] strArr2 = regionStrings;
        String[] strArr3 = regionStringValue;
        Preference tvCountryRegion3 = util2.createListPreference("g_country__country_rid", R.string.menu_tv_country_region_id, true, regionStrings, util2.mConfigManager.getDefault("g_country__country_rid"));
        if (MarketRegionInfo.isFunctionSupport(3) && this.mTV.isAusCountry()) {
            tvCountryRegion3 = util2.createListPreference("g_country__country_rid", (int) R.string.menu_tv_oceania_country_region, true, themedContext.getResources().getStringArray(R.array.menu_tv_oceania_country_region), util2.mConfigManager.getDefault("g_country__country_rid"));
        }
        Preference tvCountryRegion4 = tvCountryRegion3;
        if (MarketRegionInfo.isFunctionSupport(16)) {
            String[] langArray = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_array_PA);
            if (MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("NZL")) {
                langArray = this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_array_PA_NZL);
            }
            String[] langArray2 = langArray;
            tvCountryRegion = tvCountryRegion4;
            tvFirstLanguageEU = util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, langArray2, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language"));
            tvSecondLanguageEU = util2.createListPreference("g_aud_lang__aud_2nd_language", (int) R.string.menu_tv_audio_language2nd, true, langArray2, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_2nd_language"));
        } else {
            tvCountryRegion = tvCountryRegion4;
            tvFirstLanguageEU = util2.createListPreference("g_aud_lang__aud_language", (int) R.string.menu_tv_audio_language, true, audioLanuage, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_language"));
            tvSecondLanguageEU = util2.createListPreference("g_aud_lang__aud_2nd_language", (int) R.string.menu_tv_audio_language2nd, true, audioLanuage2nd, util2.mOsdLanguage.getAudioLanguage("g_aud_lang__aud_2nd_language"));
        }
        Preference tvChannel = util2.createPreference(MenuConfigManager.TV_EU_CHANNEL, (int) R.string.menu_tv_channels);
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__ch_list_type") > 0) {
            tvTunerModeEU.setEnabled(false);
        } else {
            tvTunerModeEU.setEnabled(true);
        }
        if (this.mTV.isPVrRunning() || this.mTV.isTshitRunning()) {
            tvTunerModeEU.setEnabled(false);
            tvChannel.setEnabled(false);
        } else {
            tvTunerModeEU.setEnabled(true);
            tvChannel.setEnabled(true);
        }
        preferenceScreen2.addPreference(tvTunerModeEU);
        if (this.mTV.isShowCountryRegion()) {
            tvCountryRegion2 = tvCountryRegion;
            preferenceScreen2.addPreference(tvCountryRegion2);
        } else {
            tvCountryRegion2 = tvCountryRegion;
        }
        preferenceScreen2.addPreference(tvFirstVoice);
        if (!CommonIntegration.getInstance().isCurrentSourceATV() && (this.mTV.iCurrentInputSourceHasSignal() || CommonIntegration.getInstance().hasActiveChannel())) {
            preferenceScreen2.removePreference(tvFirstVoice);
        }
        preferenceScreen2.addPreference(tvFirstLanguageEU);
        preferenceScreen2.addPreference(tvSecondLanguageEU);
        if (!this.mTV.isConfigVisible("g_menu__audio_lang_attr")) {
            preferenceScreen2.removePreference(tvFirstLanguageEU);
            preferenceScreen2.removePreference(tvSecondLanguageEU);
        }
        String[] channelListType = themedContext.getResources().getStringArray(R.array.menu_tv_channel_listtype);
        Preference preference = tvCountryRegion2;
        Preference tvChannel2 = tvChannel;
        Preference tvChannelListType = util2.createListPreference("g_misc__ch_list_type", (int) R.string.menu_tv_channel_list_type, true, channelListType, util2.mConfigManager.getDefault("g_misc__ch_list_type"));
        if (this.mTV.isConfigVisible("g_misc__ch_list_type")) {
            preferenceScreen2.addPreference(tvChannelListType);
        }
        String profileName = MtkTvConfig.getInstance().getConfigString("g_misc__ch_list_slot");
        if (!TextUtils.isEmpty(profileName)) {
            MtkLog.d(TAG, "profileName " + profileName);
            channelListType[1] = profileName;
            tvChannelListType.setSummary((CharSequence) profileName);
        }
        preferenceScreen2.addPreference(tvChannel2);
        Preference tvDualTuner = util2.createSwitchPreference("g_misc__2nd_channel_enable", (int) R.string.menu_tv_dual_tuner, util2.mConfigManager.getDefault("g_misc__2nd_channel_enable") != 0);
        if (!MarketRegionInfo.isFunctionSupport(32) || CommonIntegration.getInstance().isPipOrPopState()) {
            preferenceScreen2.removePreference(tvDualTuner);
        } else {
            preferenceScreen2.addPreference(tvDualTuner);
        }
        bindChannelPreference();
        this.mDataHelper.changePreferenceEnable();
    }

    private void initCleanDialog() {
        this.cleanDialog = new LiveTVDialog(this.mContext, "", this.mContext.getString(R.string.menu_tv_clear_channel_info), 3);
        this.cleanDialog.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.cleanDialog.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.cleanDialog.bindKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23 && keyCode != 183)) {
                    return false;
                }
                if (v.getId() == SettingsPreferenceScreen.this.cleanDialog.getButtonYes().getId()) {
                    EditChannel.getInstance(SettingsPreferenceScreen.this.mContext).cleanChannelList();
                    SettingsPreferenceScreen.this.cleanDialog.dismiss();
                    if (SettingsPreferenceScreen.this.mTV.getConfigValue("g_misc__ch_list_type") > 0) {
                        SettingsPreferenceScreen.this.mTV.setConfigValue("g_misc__ch_list_type", 0);
                    }
                    if (SettingsPreferenceScreen.mNavIntegration.getCurrentFocus().equals("sub") && (SettingsPreferenceScreen.mNavIntegration.isCurrentSourceATV() || SettingsPreferenceScreen.mNavIntegration.isCurrentSourceDTV())) {
                        InputSourceManager.getInstance().stopPipSession();
                    } else if (SettingsPreferenceScreen.mNavIntegration.getCurrentFocus().equals("main") && (SettingsPreferenceScreen.mNavIntegration.isCurrentSourceATV() || SettingsPreferenceScreen.mNavIntegration.isCurrentSourceDTV())) {
                        InputSourceManager.getInstance().stopSession();
                    }
                    SettingsPreferenceScreen.this.changePreferenceEnable();
                    return true;
                } else if (v.getId() != SettingsPreferenceScreen.this.cleanDialog.getButtonNo().getId()) {
                    return true;
                } else {
                    SettingsPreferenceScreen.this.cleanDialog.dismiss();
                    return true;
                }
            }
        });
    }

    public void changePreferenceEnable() {
        MtkLog.d(TAG, "changePreferenceEnable() start changeEnable");
        if (CommonIntegration.isCNRegion()) {
            if (mNavIntegration.hasActiveChannel()) {
                MtkLog.d(TAG, "changeEnable true");
                if (!(this.channelEdit == null || this.cleanList == null)) {
                    this.channelEdit.setEnabled(true);
                    this.cleanList.setEnabled(true);
                }
            } else {
                MtkLog.d(TAG, "changeEnable false");
                if (!(this.channelEdit == null || this.cleanList == null)) {
                    this.channelEdit.setEnabled(false);
                    this.cleanList.setEnabled(false);
                }
            }
        } else if (this.channelskip == null) {
            MtkLog.d(TAG, "end changeEnable null return");
            return;
        } else if (mNavIntegration.hasActiveChannel()) {
            MtkLog.d(TAG, "changeEnable true");
            this.channelskip.setEnabled(true);
            if (!(!CommonIntegration.isSARegion() || this.saChannelEdit == null || this.saChannelFine == null)) {
                this.saChannelEdit.setEnabled(true);
                if (TIFChannelManager.getInstance(this.mContext).hasATVChannels()) {
                    this.saChannelFine.setEnabled(true);
                } else {
                    this.saChannelFine.setEnabled(false);
                }
            }
            if (CommonIntegration.isEURegion() && this.saChannelFine != null) {
                MtkLog.d(TAG, "changeEnable saChannelFine != nulltrue");
                this.channelskip.setEnabled(true);
                this.channelSort.setEnabled(true);
                this.channelEdit.setEnabled(true);
                StringBuilder sb = new StringBuilder();
                sb.append("changeEnable saChannelFine != nulltrue-source:");
                sb.append(!this.mTV.isCurrentSourceDTV());
                MtkLog.d(TAG, sb.toString());
                if (!this.mTV.isCurrentSourceDTV()) {
                    this.saChannelFine.setEnabled(true);
                } else {
                    this.saChannelFine.setEnabled(false);
                }
                MtkLog.d(TAG, "mTV.isTurkeyCountry()> " + this.mTV.isTurkeyCountry() + "dvbs_operator_name_tivibu > " + ScanContent.getDVBSCurrentOPStr(this.mContext) + "  >>   " + this.mContext.getString(R.string.dvbs_operator_name_tivibu));
                if (!this.mTV.isTurkeyCountry() || !ScanContent.getDVBSCurrentOPStr(this.mContext).equalsIgnoreCase(this.mContext.getString(R.string.dvbs_operator_name_tivibu))) {
                    this.channelskip.setEnabled(true);
                    this.channelSort.setEnabled(true);
                    this.cleanList.setEnabled(true);
                } else {
                    this.channelEdit.setEnabled(false);
                    this.channelskip.setEnabled(false);
                    this.channelSort.setEnabled(false);
                    this.cleanList.setEnabled(false);
                }
                this.cleanList.setEnabled(true);
            }
            this.mDataHelper.setSkipSortEditItemHid(this.channelskip, this.channelSort, this.channelEdit);
        } else {
            MtkLog.d(TAG, "changeEnable false");
            this.channelskip.setEnabled(false);
            if (!(!CommonIntegration.isSARegion() || this.saChannelEdit == null || this.saChannelFine == null)) {
                this.saChannelEdit.setEnabled(false);
                this.saChannelFine.setEnabled(false);
            }
            if (CommonIntegration.isEURegion() && this.saChannelFine != null) {
                MtkLog.d(TAG, "changeEnable saChannelFine != nullfalse");
                this.channelskip.setEnabled(false);
                this.channelSort.setEnabled(false);
                this.channelEdit.setEnabled(false);
                this.saChannelFine.setEnabled(false);
                this.cleanList.setEnabled(false);
            }
        }
        MtkLog.d(TAG, "end changeEnable set success");
    }

    public PreferenceScreen loadDataTvAntenna(PreferenceScreen preferenceCategory, Context themedContext) {
        PreferenceScreen preferenceScreen = preferenceCategory;
        MtkLog.d(TAG, "loadDataTvAntenna");
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceCategory.removeAll();
        Intent euAntennaChannelScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
        euAntennaChannelScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBT);
        Preference channelScan = util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN_DVBT, (int) R.string.menu_tv_channel_scan, euAntennaChannelScanIntent);
        preferenceScreen.addPreference(channelScan);
        Intent euAntennaUpdateScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
        euAntennaUpdateScanIntent.putExtra("ActionID", MenuConfigManager.TV_UPDATE_SCAN_DVBT_UPDATE);
        this.updateScan = util2.createPreference(MenuConfigManager.TV_UPDATE_SCAN_DVBT_UPDATE, (int) R.string.menu_tv_update_scan, euAntennaUpdateScanIntent);
        preferenceScreen.addPreference(this.updateScan);
        Intent euAnalogScanIntent = new Intent(this.mContext, ScanViewActivity.class);
        euAnalogScanIntent.putExtra("ActionID", MenuConfigManager.TV_ANALOG_SCAN);
        Preference analogScan = util2.createPreference(MenuConfigManager.TV_ANALOG_SCAN, (int) R.string.menu_tv_analog_manual_scan, euAnalogScanIntent);
        if (!MarketRegionInfo.isFunctionSupport(16) || !this.mTV.isCurrentSourceDTV()) {
            preferenceScreen.addPreference(analogScan);
        }
        Intent euRFScanIntent = new Intent(this.mContext, ScanViewActivity.class);
        euRFScanIntent.putExtra("ActionID", MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN);
        Preference RFScan = util2.createPreference(MenuConfigManager.TV_DVBT_SINGLE_RF_SCAN, (int) R.string.menu_tv_single_rf_scan, euRFScanIntent);
        preferenceScreen.addPreference(RFScan);
        int lcnValue = this.mConfigManager.getDefault("g_fusion_common__lcn");
        Log.d("yupeng", "lcn:" + lcnValue);
        String string = themedContext.getResources().getString(R.string.menu_channel_scan_lcn);
        int lcnValue2 = lcnValue;
        String[] stringArray = themedContext.getResources().getStringArray(R.array.menu_tv_lcn);
        Preference RFScan2 = RFScan;
        Preference RFScan3 = util2.createListPreference("g_fusion_common__lcn", string, true, stringArray, lcnValue2);
        preferenceScreen.addPreference(RFScan3);
        Preference preference = RFScan3;
        Preference channelScanType = util2.createListPreference("g_fusion_common__encrypt_dvbt", themedContext.getResources().getString(R.string.menu_channel_scan_type), true, themedContext.getResources().getStringArray(R.array.menu_tv_channel_scan_type), this.mConfigManager.getDefault("g_fusion_common__encrypt_dvbt"));
        preferenceScreen.addPreference(channelScanType);
        Preference preference2 = channelScanType;
        Preference channelStoreType = util2.createListPreference("g_fusion_common__storage_dvbt", themedContext.getResources().getString(R.string.menu_channel_store_type), true, themedContext.getResources().getStringArray(R.array.menu_tv_channel_store_type), this.mConfigManager.getDefault("g_fusion_common__storage_dvbt"));
        preferenceScreen.addPreference(channelStoreType);
        Preference favoriteNetworkSelect = util2.createDialogPreference(MenuConfigManager.TV_FAVORITE_NETWORK, (int) R.string.menu_c_favorite_net, (Dialog) new ScanThirdlyDialog(this.mContext, 1));
        favoriteNetworkSelect.setEnabled(CommonIntegration.getInstance().isFavoriteNetworkEnable());
        preferenceScreen.addPreference(favoriteNetworkSelect);
        Intent intentAntenaSkip = new Intent(this.mContext, ChannelInfoActivity.class);
        intentAntenaSkip.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST, "", 10004, 10004, 10004));
        Intent intent = euAntennaChannelScanIntent;
        intentAntenaSkip.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SKIP);
        this.channelskip = util2.createPreference(MenuConfigManager.TV_CHANNEL_SKIP, (int) R.string.menu_tv_channel_skip, intentAntenaSkip);
        preferenceScreen.addPreference(this.channelskip);
        Preference preference3 = channelStoreType;
        Intent intentAntenaSort = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem = new TransItem(MenuConfigManager.TV_CHANNEL_SORT_CHANNELLIST, "", 10004, 10004, 10004);
        intentAntenaSort.putExtra("TransItem", transItem);
        TransItem transItem2 = transItem;
        intentAntenaSort.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SORT);
        this.channelSort = util2.createPreference(MenuConfigManager.TV_CHANNEL_SORT, (int) R.string.menu_c_chanel_sor, intentAntenaSort);
        preferenceScreen.addPreference(this.channelSort);
        Intent intent2 = intentAntenaSort;
        Intent intentAntenaMove = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem3 = new TransItem(MenuConfigManager.TV_CHANNEL_MOVE_CHANNELLIST, "", 10004, 10004, 10004);
        intentAntenaMove.putExtra("TransItem", transItem3);
        TransItem transItem4 = transItem3;
        intentAntenaMove.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_MOVE);
        this.channelMove = util2.createPreference(MenuConfigManager.TV_CHANNEL_MOVE, (int) R.string.menu_c_chanel_Move, intentAntenaMove);
        preferenceScreen.addPreference(this.channelMove);
        Intent intent3 = intentAntenaMove;
        Intent intentAntenaEdit = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem5 = new TransItem(MenuConfigManager.TV_CHANNEL_EDIT_LIST, "", 10004, 10004, 10004);
        intentAntenaEdit.putExtra("TransItem", transItem5);
        TransItem transItem6 = transItem5;
        intentAntenaEdit.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_EDIT);
        this.channelEdit = util2.createPreference(MenuConfigManager.TV_CHANNEL_EDIT, (int) R.string.menu_tv_channel_edit, intentAntenaEdit);
        preferenceScreen.addPreference(this.channelEdit);
        Intent intent4 = intentAntenaEdit;
        Intent intentAntenaFine = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem7 = new TransItem(MenuConfigManager.TV_CHANNELFINE_TUNE_EDIT_LIST, "", 10004, 10004, 10004);
        intentAntenaFine.putExtra("TransItem", transItem7);
        TransItem transItem8 = transItem7;
        intentAntenaFine.putExtra("ActionID", MenuConfigManager.TV_CHANNELFINE_TUNE);
        this.saChannelFine = util2.createPreference(MenuConfigManager.TV_CHANNELFINE_TUNE, (int) R.string.menu_c_analog_tune, intentAntenaFine);
        if (!MarketRegionInfo.isFunctionSupport(16) || !this.mTV.isCurrentSourceDTV()) {
            preferenceScreen.addPreference(this.saChannelFine);
        }
        Intent intent5 = intentAntenaFine;
        this.cleanList = util2.createClickPreference(MenuConfigManager.TV_CHANNEL_CLEAR, (int) R.string.menu_tv_clear_channel_list, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
        preferenceScreen.addPreference(this.cleanList);
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__ch_list_type") > 0) {
            channelScan.setEnabled(false);
            this.updateScan.setEnabled(false);
            analogScan.setEnabled(false);
            RFScan2.setEnabled(false);
            this.saChannelFine.setEnabled(false);
        } else {
            channelScan.setEnabled(true);
            setPreferenceStatus(analogScan, RFScan2);
            this.updateScan.setEnabled(true);
            this.saChannelFine.setEnabled(true);
        }
        if (!mNavIntegration.hasActiveChannel() || mNavIntegration.is3rdTVSource()) {
            this.channelEdit.setEnabled(false);
            this.channelSort.setEnabled(false);
            this.saChannelFine.setEnabled(false);
            this.channelMove.setEnabled(false);
            this.cleanList.setEnabled(false);
            if (!mNavIntegration.hasActiveChannel(true) || mNavIntegration.is3rdTVSource()) {
                MtkLog.d(TAG, "channelskip T false");
                this.channelskip.setEnabled(false);
            } else {
                MtkLog.d(TAG, "channelskip T true");
                this.channelskip.setEnabled(true);
            }
        } else {
            this.channelEdit.setEnabled(true);
            this.channelSort.setEnabled(true);
            MtkTvChannelInfoBase mtktvChannelinfo = mNavIntegration.getCurChInfoByTIF();
            if (mtktvChannelinfo == null || !mtktvChannelinfo.isAnalogService()) {
                this.saChannelFine.setEnabled(false);
            } else {
                this.saChannelFine.setEnabled(true);
            }
            this.channelMove.setEnabled(true);
            this.cleanList.setEnabled(true);
            this.channelskip.setEnabled(true);
        }
        bindChannelPreference();
        this.mDataHelper.changePreferenceEnable();
        if (this.mTV.getConfigValue("g_fusion_common__lcn") != 0 && !mNavIntegration.isCurrentSourceATVforEuPA()) {
            MtkLog.d(TAG, "CHANNEL_LCN");
            this.channelSort.setEnabled(false);
            this.channelMove.setEnabled(false);
        }
        return preferenceScreen;
    }

    private void setPreferenceStatus(Preference analogScan, Preference RFScan) {
        if (!MarketRegionInfo.isFunctionSupport(16)) {
            if (ScanContent.isCountryUK()) {
                analogScan.setEnabled(false);
            } else {
                analogScan.setEnabled(true);
            }
            RFScan.setEnabled(true);
        } else if (this.mTV.isCurrentSourceATV()) {
            analogScan.setEnabled(true);
            RFScan.setEnabled(false);
        } else {
            analogScan.setEnabled(false);
            RFScan.setEnabled(true);
        }
    }

    private PreferenceScreen addCableScan(PreferenceScreen preferenceCategory, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceCategory.removeAll();
        List<CableOperator> operatorList = ScanContent.convertStrOperator(this.mContext, ScanContent.getCableOperationList(this.mContext));
        int size = operatorList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                CableOperator operator = operatorList.get(i);
                String name = ScanContent.getOperatorStr(this.mContext, operator);
                ScanContent.setOperator(this.mContext, name);
                Intent intent = new Intent(this.mContext, ScanViewActivity.class);
                intent.putExtra("ActionID", "channel_scan_dvbc_fulls_operator#" + operator.ordinal());
                intent.putExtra("CableOperator", name);
                if (ScanContent.isCountryIre() && name.equalsIgnoreCase(this.mContext.getResources().getString(R.string.dvbc_operator_upc))) {
                    name = this.mContext.getResources().getString(R.string.dvbc_operator_virgin_media);
                }
                preferenceCategory.addPreference(util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN_DVBC_OPERATOR, name, intent));
            }
        }
        return preferenceCategory;
    }

    public PreferenceScreen loadDataTvCable(PreferenceScreen preferenceCategory, Context themedContext) {
        boolean z;
        boolean z2;
        PreferenceScreen preferenceScreen = preferenceCategory;
        if ((MarketRegionInfo.isFunctionSupport(16) || MarketRegionInfo.isFunctionSupport(19)) && this.mTV.isCurrentSourceATV()) {
            PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
            preferenceCategory.removeAll();
            Intent euAntennaChannelScanIntent = new Intent(this.mContext, ScanDialogActivity.class);
            euAntennaChannelScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBT);
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.TV_CHANNEL_SCAN_DVBT, (int) R.string.menu_tv_channel_scan, euAntennaChannelScanIntent));
        } else {
            PreferenceUtil util3 = PreferenceUtil.getInstance(themedContext);
            preferenceCategory.removeAll();
            if (ScanContent.convertStrOperator(this.mContext, ScanContent.getCableOperationList(this.mContext)).size() > 0) {
                this.channelScanItem = util3.createPreference(MenuConfigManager.TV_CHANNEL_SCAN_DVBC, (int) R.string.menu_tv_channel_scan);
                preferenceScreen.addPreference(this.channelScanItem);
            } else {
                ScanContent.setOperator(this.mContext, this.mContext.getString(R.string.dvbc_operator_others));
                Intent cableChannelScanIntent = new Intent(this.mContext, ScanViewActivity.class);
                cableChannelScanIntent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBC);
                this.channelScanItem = util3.createPreference(MenuConfigManager.TV_CHANNEL_SCAN_DVBC, (int) R.string.menu_tv_channel_scan, cableChannelScanIntent);
                preferenceScreen.addPreference(this.channelScanItem);
            }
        }
        Intent cableAnalogScanIntent = new Intent(this.mContext, ScanViewActivity.class);
        cableAnalogScanIntent.putExtra("ActionID", MenuConfigManager.TV_ANALOG_SCAN);
        Preference analogScan = this.util.createPreference(MenuConfigManager.TV_ANALOG_SCAN, (int) R.string.menu_tv_analog_manual_scan, cableAnalogScanIntent);
        if (MarketRegionInfo.isFunctionSupport(3) || this.mTV.isCurrentSourceATV()) {
            preferenceScreen.addPreference(analogScan);
        }
        Intent cableRFScanIntent = new Intent(this.mContext, ScanViewActivity.class);
        cableRFScanIntent.putExtra("ActionID", MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN);
        Preference mCableRFScan = this.util.createPreference(MenuConfigManager.TV_DVBC_SINGLE_RF_SCAN, (int) R.string.menu_tv_single_rf_scan, cableRFScanIntent);
        preferenceScreen.addPreference(mCableRFScan);
        preferenceScreen.addPreference(this.util.createListPreference("g_fusion_common__lcn", themedContext.getResources().getString(R.string.menu_channel_scan_lcn), true, themedContext.getResources().getStringArray(R.array.menu_tv_lcn), this.mConfigManager.getDefault("g_fusion_common__lcn")));
        preferenceScreen.addPreference(this.util.createListPreference("g_fusion_common__encrypt_dvbc", themedContext.getResources().getString(R.string.menu_channel_scan_type), true, themedContext.getResources().getStringArray(R.array.menu_tv_channel_scan_type), this.mConfigManager.getDefault("g_fusion_common__encrypt_dvbc")));
        preferenceScreen.addPreference(this.util.createListPreference("g_fusion_common__storage_dvbc", themedContext.getResources().getString(R.string.menu_channel_store_type), true, themedContext.getResources().getStringArray(R.array.menu_tv_channel_store_type), this.mConfigManager.getDefault("g_fusion_common__storage_dvbc")));
        Preference favoriteNetworkSelect = this.util.createDialogPreference(MenuConfigManager.TV_FAVORITE_NETWORK, (int) R.string.menu_c_favorite_net, (Dialog) new ScanThirdlyDialog(this.mContext, 1));
        favoriteNetworkSelect.setEnabled(CommonIntegration.getInstance().isFavoriteNetworkEnable());
        preferenceScreen.addPreference(favoriteNetworkSelect);
        Intent intentCableSkip = new Intent(this.mContext, ChannelInfoActivity.class);
        intentCableSkip.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST, "", 10004, 10004, 10004));
        intentCableSkip.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SKIP);
        this.channelskip = this.util.createPreference(MenuConfigManager.TV_CHANNEL_SKIP, (int) R.string.menu_tv_channel_skip, intentCableSkip);
        preferenceScreen.addPreference(this.channelskip);
        Intent intentCableSort = new Intent(this.mContext, ChannelInfoActivity.class);
        intentCableSort.putExtra("TransItem", new TransItem(MenuConfigManager.TV_CHANNEL_SORT_CHANNELLIST, "", 10004, 10004, 10004));
        Intent intent = cableAnalogScanIntent;
        intentCableSort.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SORT);
        Intent intent2 = cableRFScanIntent;
        this.channelSort = this.util.createPreference(MenuConfigManager.TV_CHANNEL_SORT, (int) R.string.menu_c_chanel_sor, intentCableSort);
        preferenceScreen.addPreference(this.channelSort);
        Intent intentAntenaMove = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem = new TransItem(MenuConfigManager.TV_CHANNEL_MOVE_CHANNELLIST, "", 10004, 10004, 10004);
        intentAntenaMove.putExtra("TransItem", transItem);
        Intent intent3 = intentCableSort;
        intentAntenaMove.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_MOVE);
        TransItem transItem2 = transItem;
        this.channelMove = this.util.createPreference(MenuConfigManager.TV_CHANNEL_MOVE, (int) R.string.menu_c_chanel_Move, intentAntenaMove);
        preferenceScreen.addPreference(this.channelMove);
        Intent intentCableEdit = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem3 = new TransItem(MenuConfigManager.TV_CHANNEL_EDIT_LIST, "", 10004, 10004, 10004);
        intentCableEdit.putExtra("TransItem", transItem3);
        Intent intent4 = intentAntenaMove;
        intentCableEdit.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_EDIT);
        TransItem transItem4 = transItem3;
        this.channelEdit = this.util.createPreference(MenuConfigManager.TV_CHANNEL_EDIT, (int) R.string.menu_tv_channel_edit, intentCableEdit);
        preferenceScreen.addPreference(this.channelEdit);
        Intent intentCableFine = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem5 = new TransItem(MenuConfigManager.TV_CHANNELFINE_TUNE_EDIT_LIST, "", 10004, 10004, 10004);
        intentCableFine.putExtra("TransItem", transItem5);
        Intent intent5 = intentCableEdit;
        intentCableFine.putExtra("ActionID", MenuConfigManager.TV_CHANNELFINE_TUNE);
        TransItem transItem6 = transItem5;
        this.saChannelFine = this.util.createPreference(MenuConfigManager.TV_CHANNELFINE_TUNE, (int) R.string.menu_c_analog_tune, intentCableFine);
        if (!MarketRegionInfo.isFunctionSupport(16) || !this.mTV.isCurrentSourceDTV()) {
            preferenceScreen.addPreference(this.saChannelFine);
        }
        Intent intent6 = intentCableFine;
        this.cleanList = this.util.createClickPreference(MenuConfigManager.TV_CHANNEL_CLEAR, (int) R.string.menu_tv_clear_channel_list, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
        preferenceScreen.addPreference(this.cleanList);
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__ch_list_type") > 0) {
            if (this.channelScanItem != null) {
                this.channelScanItem.setEnabled(false);
            }
            analogScan.setEnabled(false);
            mCableRFScan.setEnabled(false);
            this.saChannelFine.setEnabled(false);
        } else {
            if (this.channelScanItem != null) {
                z2 = true;
                this.channelScanItem.setEnabled(true);
            } else {
                z2 = true;
            }
            setPreferenceStatus(analogScan, mCableRFScan);
            this.saChannelFine.setEnabled(z2);
        }
        if (ScanContent.isRCSRDSOp() || ScanContent.isTELNETOp()) {
            mCableRFScan.setEnabled(false);
        }
        if (!mNavIntegration.hasActiveChannel() || mNavIntegration.is3rdTVSource()) {
            this.channelEdit.setEnabled(false);
            this.channelSort.setEnabled(false);
            this.saChannelFine.setEnabled(false);
            this.channelMove.setEnabled(false);
            if (!mNavIntegration.hasActiveChannel(true) || mNavIntegration.is3rdTVSource()) {
                MtkLog.d(TAG, "channelskip cable false");
                this.channelskip.setEnabled(false);
            } else {
                MtkLog.d(TAG, "channelskip cable true");
                this.channelskip.setEnabled(true);
            }
            this.cleanList.setEnabled(false);
        } else {
            if (ScanContent.isRCSRDSOp() || ScanContent.isTELNETOp()) {
                z = true;
                this.channelEdit.setEnabled(false);
            } else {
                z = true;
                this.channelEdit.setEnabled(true);
            }
            this.channelSort.setEnabled(z);
            MtkTvChannelInfoBase mtktvChannelinfo = mNavIntegration.getCurChInfoByTIF();
            if (mtktvChannelinfo == null || !mtktvChannelinfo.isAnalogService()) {
                this.saChannelFine.setEnabled(false);
            } else {
                this.saChannelFine.setEnabled(z);
            }
            this.channelskip.setEnabled(z);
            this.channelMove.setEnabled(z);
            this.cleanList.setEnabled(z);
        }
        bindChannelPreference();
        if (this.mTV.getConfigValue("g_fusion_common__lcn") != 0 && !mNavIntegration.isCurrentSourceATVforEuPA()) {
            MtkLog.d(TAG, "CHANNEL_LCN");
            this.channelSort.setEnabled(false);
            this.channelMove.setEnabled(false);
        }
        return preferenceScreen;
    }

    private PreferenceScreen addDVBSRescan(PreferenceScreen preferenceCategory, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceCategory.removeAll();
        List<String> operatorListStr = ScanContent.getDVBSOperatorList(this.mContext);
        int size = operatorListStr.size();
        for (int i = 0; i < size; i++) {
            String name = operatorListStr.get(i);
            Intent intentOperator = new Intent(this.mContext, SatActivity.class);
            intentOperator.putExtra("mItemId", MenuConfigManager.DVBS_SAT_OP);
            intentOperator.putExtra("title", name);
            intentOperator.putExtra("selectPos", i);
            preferenceCategory.addPreference(util2.createPreference(MenuConfigManager.DVBS_SAT_OP, name, intentOperator));
        }
        return preferenceCategory;
    }

    public PreferenceScreen loadDataTvPersonalSatllites(PreferenceScreen preferenceCategory, Context themedContext) {
        PreferenceScreen preferenceScreen = preferenceCategory;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceCategory.removeAll();
        Action satelliteRescan = new Action(MenuConfigManager.DVBS_SAT_RE_SCAN, this.mContext.getString(R.string.menu_s_sate_rescan), 10004, 10004, 10004, (String[]) null, 1, Action.DataType.HAVESUBCHILD);
        List<String> operatorListStr = ScanContent.getDVBSOperatorList(this.mContext);
        int size = operatorListStr.size();
        if (!ScanContent.isPreferedSat() || size <= 0) {
            Intent intentRescan = new Intent(this.mContext, SatActivity.class);
            intentRescan.putExtra("mItemId", MenuConfigManager.DVBS_SAT_RE_SCAN);
            intentRescan.putExtra("title", this.mContext.getString(R.string.menu_s_sate_rescan));
            intentRescan.putExtra("selectPos", 0);
            this.mSatelliteRescan = util2.createPreference(MenuConfigManager.DVBS_SAT_RE_SCAN, (int) R.string.menu_s_sate_rescan, intentRescan);
            preferenceScreen.addPreference(this.mSatelliteRescan);
        } else {
            this.satelliteRescans = util2.createPreference(MenuConfigManager.DVBS_SAT_RE_SCAN, (int) R.string.menu_s_sate_rescan);
            preferenceScreen.addPreference(this.satelliteRescans);
        }
        String string = this.mContext.getString(R.string.menu_s_sate_add);
        Action.DataType dataType = Action.DataType.SATELITEINFO;
        int i = R.string.menu_s_sate_add;
        Action action = new Action("Satellite Add", string, 10004, 10004, 10004, (String[]) null, 1, dataType);
        Intent intentAdd = new Intent(this.mContext, SatActivity.class);
        intentAdd.putExtra("mItemId", "Satellite Add");
        intentAdd.putExtra("title", this.mContext.getString(i));
        intentAdd.putExtra("selectPos", -1);
        Preference mSatelliteAdd = util2.createPreference("Satellite Add", i, intentAdd);
        preferenceScreen.addPreference(mSatelliteAdd);
        this.satellites = ScanContent.getDVBSsatellites(this.mContext);
        if (this.mHelper.buildDVBSSATDetailInfo(action, this.satellites, 2).size() == 0) {
            mSatelliteAdd.setEnabled(false);
        } else {
            mSatelliteAdd.setEnabled(true);
        }
        Action action2 = new Action(MenuConfigManager.DVBS_SAT_UPDATE_SCAN, this.mContext.getString(R.string.menu_s_sate_update), 10004, 10004, 10004, (String[]) null, 1, Action.DataType.SATELITEINFO);
        Intent intentUpdate = new Intent(this.mContext, SatActivity.class);
        intentUpdate.putExtra("mItemId", MenuConfigManager.DVBS_SAT_UPDATE_SCAN);
        intentUpdate.putExtra("title", this.mContext.getString(R.string.menu_s_sate_update));
        intentUpdate.putExtra("selectPos", -1);
        preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.DVBS_SAT_UPDATE_SCAN, (int) R.string.menu_s_sate_update, intentUpdate));
        Action action3 = new Action(MenuConfigManager.DVBS_SAT_MANUAL_TURNING, this.mContext.getString(R.string.menu_s_sate_tuning), 10004, 10004, 10004, (String[]) null, 1, Action.DataType.SATELITEINFO);
        List<String> list = operatorListStr;
        Intent intentManual = new Intent(this.mContext, SatActivity.class);
        intentManual.putExtra("mItemId", MenuConfigManager.DVBS_SAT_MANUAL_TURNING);
        int i2 = size;
        intentManual.putExtra("title", this.mContext.getString(R.string.menu_s_sate_tuning));
        intentManual.putExtra("selectPos", -1);
        Preference mSatelliteManualTuning = util2.createPreference(MenuConfigManager.DVBS_SAT_MANUAL_TURNING, (int) R.string.menu_s_sate_tuning, intentManual);
        preferenceScreen.addPreference(mSatelliteManualTuning);
        Intent intent = intentManual;
        ScanThirdlyDialog thirdDialog = new ScanThirdlyDialog(this.mContext, 1);
        Preference favoriteNetworkSelect = util2.createDialogPreference(MenuConfigManager.TV_FAVORITE_NETWORK, (int) R.string.menu_c_favorite_net, (Dialog) thirdDialog);
        favoriteNetworkSelect.setEnabled(CommonIntegration.getInstance().isFavoriteNetworkEnable());
        preferenceScreen.addPreference(favoriteNetworkSelect);
        ScanThirdlyDialog scanThirdlyDialog = thirdDialog;
        Preference preference = mSatelliteManualTuning;
        Intent intentSatSkip = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem = new TransItem(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST, "", 10004, 10004, 10004);
        intentSatSkip.putExtra("TransItem", transItem);
        TransItem transItem2 = transItem;
        intentSatSkip.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SKIP);
        this.channelskip = util2.createPreference(MenuConfigManager.TV_CHANNEL_SKIP, (int) R.string.menu_tv_channel_skip, intentSatSkip);
        preferenceScreen.addPreference(this.channelskip);
        Intent intent2 = intentSatSkip;
        Intent intentSatSort = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem3 = new TransItem(MenuConfigManager.TV_CHANNEL_SORT_CHANNELLIST, "", 10004, 10004, 10004);
        intentSatSort.putExtra("TransItem", transItem3);
        TransItem transItem4 = transItem3;
        intentSatSort.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SORT);
        this.channelSort = util2.createPreference(MenuConfigManager.TV_CHANNEL_SORT, (int) R.string.menu_c_chanel_sor, intentSatSort);
        preferenceScreen.addPreference(this.channelSort);
        Intent intent3 = intentSatSort;
        Intent intentAntenaMove = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem5 = new TransItem(MenuConfigManager.TV_CHANNEL_MOVE_CHANNELLIST, "", 10004, 10004, 10004);
        intentAntenaMove.putExtra("TransItem", transItem5);
        TransItem transItem6 = transItem5;
        intentAntenaMove.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_MOVE);
        this.channelMove = util2.createPreference(MenuConfigManager.TV_CHANNEL_MOVE, (int) R.string.menu_c_chanel_Move, intentAntenaMove);
        preferenceScreen.addPreference(this.channelMove);
        Intent intent4 = intentAntenaMove;
        Intent intentSatEdit = new Intent(this.mContext, ChannelInfoActivity.class);
        TransItem transItem7 = new TransItem(MenuConfigManager.TV_CHANNEL_EDIT_LIST, "", 10004, 10004, 10004);
        intentSatEdit.putExtra("TransItem", transItem7);
        TransItem transItem8 = transItem7;
        intentSatEdit.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_EDIT);
        this.channelEdit = util2.createPreference(MenuConfigManager.TV_CHANNEL_EDIT, (int) R.string.menu_tv_channel_edit, intentSatEdit);
        preferenceScreen.addPreference(this.channelEdit);
        Intent intent5 = intentSatEdit;
        this.cleanList = util2.createClickPreference(MenuConfigManager.TV_CHANNEL_CLEAR, (int) R.string.menu_tv_clear_channel_list, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
        preferenceScreen.addPreference(this.cleanList);
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__ch_list_type") > 0) {
            satelliteRescan.setEnabled(false);
            action.setEnabled(false);
            action2.setEnabled(false);
            action3.setEnabled(false);
        } else {
            satelliteRescan.setEnabled(true);
            action.setEnabled(true);
            action2.setEnabled(true);
            action3.setEnabled(true);
        }
        boolean isTKGS = ScanContent.isPreferedSat() && this.mTV.isTurkeyCountry() && this.mTV.isTKGSOperator();
        if (!mNavIntegration.hasActiveChannel() || mNavIntegration.is3rdTVSource()) {
            Action action4 = satelliteRescan;
            this.channelEdit.setEnabled(false);
            this.channelSort.setEnabled(false);
            this.channelMove.setEnabled(false);
            this.cleanList.setEnabled(false);
            if (!mNavIntegration.hasActiveChannel(true) || mNavIntegration.is3rdTVSource()) {
                MtkLog.d(TAG, "channelskip satllite false");
                this.channelskip.setEnabled(false);
            } else {
                MtkLog.d(TAG, "channelskip satllite true");
                this.channelskip.setEnabled(true);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            PreferenceUtil preferenceUtil = util2;
            sb.append("mTV.isTurkeyCountry()> ");
            sb.append(this.mTV.isTurkeyCountry());
            sb.append("dvbs_operator_name_tivibu > ");
            sb.append(ScanContent.getDVBSCurrentOPStr(this.mContext));
            sb.append("  >>   ");
            Action action5 = satelliteRescan;
            sb.append(this.mContext.getString(R.string.dvbs_operator_name_tivibu));
            MtkLog.d(TAG, sb.toString());
            if (!this.mTV.isTurkeyCountry() || !ScanContent.getDVBSCurrentOPStr(this.mContext).equalsIgnoreCase(this.mContext.getString(R.string.dvbs_operator_name_tivibu))) {
                this.channelEdit.setEnabled(true);
                this.channelskip.setEnabled(true);
                this.channelSort.setEnabled(true);
                this.channelMove.setEnabled(true);
                this.cleanList.setEnabled(true);
            } else {
                this.channelEdit.setEnabled(false);
                this.channelskip.setEnabled(false);
                this.channelSort.setEnabled(false);
                this.channelMove.setEnabled(false);
                this.cleanList.setEnabled(false);
            }
            if (isTKGS) {
                MtkLog.d(TAG, "isTKGS getTKGSOperatorMode");
                if (this.mTV.getTKGSOperatorMode() == 0) {
                    MtkLog.d(TAG, "isTKGSoperator auto");
                    action.setEnabled(false);
                    this.channelSort.setEnabled(false);
                    this.channelMove.setEnabled(false);
                    this.channelEdit.setEnabled(false);
                } else if (this.mTV.getTKGSOperatorMode() == 1) {
                    this.channelEdit.setEnabled(false);
                }
            }
            if (this.mTV.isM7ScanMode()) {
                if (this.mHelper.isM7HasNumExceed4K()) {
                    this.channelSort.setEnabled(true);
                    this.channelMove.setEnabled(true);
                } else {
                    this.channelSort.setEnabled(false);
                    this.channelMove.setEnabled(false);
                }
            }
        }
        if (isTKGS) {
            MtkLog.d(TAG, "isTKGS");
            if (this.mTV.getTKGSOperatorMode() == 0) {
                MtkLog.d(TAG, "isTKGSoperator auto");
                action.setEnabled(false);
            }
        } else {
            MtkLog.d(TAG, "is not TKGS");
        }
        bindChannelPreference();
        this.mDataHelper.changePreferenceEnable();
        return preferenceScreen;
    }

    private void getScreenMode() {
        String[] arrayString = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us);
        int[] screenList = this.mHelper.getSupportScreenModes();
        if (screenList == null) {
            this.mScreenMode = arrayString;
            return;
        }
        String[] mScreenModeList = new String[screenList.length];
        for (int i = 0; i < screenList.length; i++) {
            mScreenModeList[i] = arrayString[screenList[i]];
        }
        this.mScreenMode = mScreenModeList;
        if (this.mScreenMode != null) {
            for (String s : this.mScreenMode) {
                MtkLog.d(TAG, "screen mode is :" + s);
            }
        }
    }

    private void addSetupSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.haveScreenMode = false;
        String[] screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_eu_analog);
        boolean isPipopScreen = false;
        if (CommonIntegration.isSARegion() || CommonIntegration.isUSRegion()) {
            if (this.mTV.isCurrentSourceVGA()) {
                screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_vga);
            } else {
                screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_sa_analog);
                if (CommonIntegration.isUSRegion() && this.mTV.isCurrentSourceTv() && !this.mTV.isAnalog(CommonIntegration.getInstance().getCurChInfo())) {
                    screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us_dtv);
                }
                if (CommonIntegration.getInstance().isPOPState()) {
                    screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_sa_pop);
                } else if (CommonIntegration.getInstance().isPIPState() && "sub".equalsIgnoreCase(CommonIntegration.getInstance().getCurrentFocus())) {
                    screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_sa_pip);
                    if (CommonIntegration.isUSRegion()) {
                        screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us_pip_sub);
                    }
                } else if (CommonIntegration.getInstance().isPIPState()) {
                    screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us_pip_main_analog);
                }
            }
        } else if (CommonIntegration.isCNRegion()) {
            screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array);
        } else if (CommonIntegration.isEURegion()) {
            if (CommonIntegration.getInstance().isPOPState()) {
                isPipopScreen = true;
                MtkLog.d(TAG, "do pip specail11");
                screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_sa_pop);
            } else if (CommonIntegration.getInstance().isPIPState() && "sub".equalsIgnoreCase(CommonIntegration.getInstance().getCurrentFocus())) {
                isPipopScreen = true;
                MtkLog.d(TAG, "do pip specail22");
                screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_sa_pip);
            } else if (CommonIntegration.getInstance().isPIPState()) {
                isPipopScreen = true;
                MtkLog.d(TAG, "do pip specail33");
                screenMode = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us_pip_main_analog);
            }
        }
        MtkLog.d(TAG, "init region setting");
        MtkLog.d(TAG, "isSupportEWBS" + DataSeparaterUtil.getInstance().isSupportEWBS());
        MtkLog.d(TAG, "isEWBSIniExist" + DataSeparaterUtil.getInstance().isEWBSIniExist());
        if (DataSeparaterUtil.getInstance().isEWBSIniExist()) {
            if (DataSeparaterUtil.getInstance().isSupportEWBS()) {
                preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_REGION_SETTING, (int) R.string.menu_setup_region_setting));
            }
        } else if ((this.mTV.isEcuadorCountry() || this.mTV.isPhiCountry()) && MarketRegionInfo.isFunctionSupport(34) && !DataSeparaterUtil.getInstance().isAtvOnly()) {
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_REGION_SETTING, (int) R.string.menu_setup_region_setting));
        }
        if (!CommonIntegration.isCNRegion() && mNavIntegration.hasVGASource() && InputSourceManager.getInstance().getCurrentChannelSourceName().equalsIgnoreCase("VGA")) {
            preferenceScreen.addPreference(util2.createSwitchPreference("g_misc__dpms", (int) R.string.menu_setup_dpms, this.mTV.getConfigValue("g_misc__dpms") == 1));
        }
        if (!CommonIntegration.isSARegion() && MarketRegionInfo.isFunctionSupport(47)) {
            preferenceScreen.addPreference(util2.createSwitchPreference("g_video__vid_blue_mute", (int) R.string.menu_setup_bluemute, util2.mConfigManager.getDefault("g_video__vid_blue_mute") == 1));
        }
        if (!CommonIntegration.isUSRegion() && MarketRegionInfo.isFunctionSupport(48)) {
            Preference powerChannel = util2.createPreference(MenuConfigManager.SETUP_POWER_ON_CH, (int) R.string.menu_setup_power_on_channel);
            powerChannel.setEnabled(isTVSource());
            preferenceScreen.addPreference(powerChannel);
        }
        if (MarketRegionInfo.isFunctionSupport(23)) {
            DivxDialog divxrdialog = new DivxDialog(themedContext);
            divxrdialog.setItemId(MenuConfigManager.DIVX_REG);
            preferenceScreen.addPreference(util2.createDialogPreference(MenuConfigManager.SETUP_DIVX_REGISTRATION, (int) R.string.menu_setup_divx_registration, (Dialog) divxrdialog));
        }
        if (MarketRegionInfo.isFunctionSupport(23)) {
            preferenceScreen.addPreference(util2.createDialogPreference(MenuConfigManager.SETUP_DIVX_DEACTIVATION, (int) R.string.menu_setup_divx_deactivation, (Dialog) new DivxDialog(themedContext)));
        }
        if (CommonIntegration.isSARegion() && MarketRegionInfo.isFunctionSupport(2) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportGinga() && this.mTV.isCurrentSourceDTV()) {
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.GINGA_SETUP, (int) R.string.menu_setup_ginga_setup));
        }
        if (CommonIntegration.isEURegion()) {
            if (MarketRegionInfo.isFunctionSupport(42) && DataSeparaterUtil.getInstance().isSupportMheg5()) {
                preferenceScreen.addPreference(util2.createSwitchPreference("g_misc__mheg_inter_ch", (int) R.string.menu_setup_interaction_channel, getDefaultBoolean(this.mConfigManager.getDefault("g_misc__mheg_inter_ch"))));
            }
            if (MarketRegionInfo.isFunctionSupport(9) && DataSeparaterUtil.getInstance().isSupportMheg5() && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportMheg5()) {
                preferenceScreen.addPreference(util2.createSwitchPreference("g_misc__mheg_pin_protection", (int) R.string.menu_setup_MHEG_PIN_Protection, getDefaultBoolean(this.mConfigManager.getDefault("g_misc__mheg_pin_protection"))));
            }
            if (MarketRegionInfo.isFunctionSupport(3)) {
                preferenceScreen.addPreference(util2.createSwitchPreference("g_misc__freeview_mode", (int) R.string.menu_setup_oceania_freeview, getDefaultBoolean(this.mConfigManager.getDefault("g_misc__freeview_mode"))));
            }
            if (MarketRegionInfo.isFunctionSupport(5) && DataSeparaterUtil.getInstance().isSupportHbbtv() && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportHbbtv()) {
                preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_HBBTV, (int) R.string.menu_setup_HBBTV_settings));
            }
            if (this.mTV.isUKCountry()) {
                preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.FREEVIEW_SETTING, (int) R.string.menu_string_freeview_setting));
            }
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SUBTITLE_GROUP, (int) R.string.menu_setup_subtitle));
        }
        if ((CommonIntegration.isEURegion() || CommonIntegration.isCNRegion()) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportTeletext()) {
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SETUP_TELETEXT, (int) R.string.menu_setup_teletext));
        }
        if (CommonIntegration.isEURegion() && MarketRegionInfo.isFunctionSupport(10) && DataSeparaterUtil.getInstance().isSupportOAD() && this.mTV.isCurrentSourceTv()) {
            boolean isPVRSrt = SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.PVR_START);
            Preference OADPreference = util2.createPreference(MenuConfigManager.SETUP_OAD_SETTING, (int) R.string.menu_setup_oad_setting);
            preferenceScreen.addPreference(OADPreference);
            if (isPVRSrt || SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START) || this.mTV.isCurrentSourceATV()) {
                OADPreference.setEnabled(false);
            } else {
                OADPreference.setEnabled(true);
            }
        }
        if (CommonIntegration.isEURegion() && isSupportBissKey() && MarketRegionInfo.isFunctionSupport(49)) {
            bissPreference = util2.createPreference(MenuConfigManager.BISS_KEY, (int) R.string.menu_setup_biss_key);
            preferenceScreen.addPreference(bissPreference);
            int tunerMode = CommonIntegration.getInstance().getSvl();
            if (!(tunerMode == 3 || tunerMode == 4)) {
                preferenceScreen.removePreference(bissPreference);
            }
        }
        if (CommonIntegration.isEURegion() != 0 && (this.mTV.isIDNCountry() || (MtkTvConfig.getInstance().getCountry().equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_IDN) && "1".equals(SystemProperties.get("ro.vendor.mtk.system.ews.support"))))) {
            String initVal = MtkTvConfig.getInstance().getConfigString("g_eas__lct_ct");
            PostalCodeEditDialog postalCodedialog = new PostalCodeEditDialog(themedContext, this.mContext.getResources().getString(R.string.menu_setup_postal_code_setting), initVal, MenuConfigManager.SETUP_POSTAL_CODE);
            postalCodedialog.setLength(5);
            Preference postalCodeference = util2.createDialogPreference(MenuConfigManager.SETUP_POSTAL_CODE, (int) R.string.menu_setup_postal_code_setting, (Dialog) postalCodedialog);
            postalCodeference.setSummary((CharSequence) initVal);
            postalCodedialog.setPreference(postalCodeference);
            preferenceScreen.addPreference(postalCodeference);
        }
        if (!(DataSeparaterUtil.getInstance() == null || DataSeparaterUtil.getInstance().getValueAutoSleep() == 1)) {
            preferenceScreen.addPreference(util2.createListPreference(MenuConfigManager.POWER_SETTING_CONFIG_VALUE, (int) R.string.menu_string_power_setting, true, addSuffix(this.mContext.getResources().getStringArray(R.array.menu_setting_power_time), R.string.menu_arrays_power_x_hours), util2.mConfigManager.getDefaultPowerSetting(themedContext)));
        }
        if (CommonIntegration.isEURegion()) {
            boolean satOperOnly = CommonIntegration.getInstance().isPreferSatMode();
            MtkLog.d(TAG, "TKGSSetting:" + satOperOnly + this.mTV.isTurkeyCountry() + this.mTV.isTKGSOperator());
            if (satOperOnly && this.mTV.isTurkeyCountry()) {
                tkgsSetting = util2.createPreference(MenuConfigManager.TKGS_SETTING, (int) R.string.menu_tkgs, new Intent(this.mContext, TKGSSettingActivity.class));
                preferenceScreen.addPreference(tkgsSetting);
            }
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.SYSTEM_INFORMATION, (int) R.string.menu_setup_system_info));
        }
        WindowManager.LayoutParams lp = TurnkeyUiMainActivity.getInstance().getWindow().getAttributes();
        lp.width = (int) (((double) SettingsUtil.SCREEN_WIDTH) * 0.89d);
        lp.height = (int) (((double) SettingsUtil.SCREEN_HEIGHT) * 0.94d);
        Intent i = new Intent();
        i.setComponent(new ComponentName("tv.samba.ssm", "tv.samba.ssm.activities.SambaTVSettingsActivity"));
        i.putExtra("tv.samba.ssm.SETTINGS_TITLE", "Samba Interactive TV");
        i.putExtra("tv.samba.ssm.SETTINGS_TITLE_BREADCRUMB", "Menu Setup");
        i.putExtra("tv.samba.ssm.AUTH_TOKEN", "94e3e9edce62ec09ef3459576c513c8d76a4d8e614b0cb6e");
        i.putExtra("tv.samba.ssm.LAYOUT_PARAMS", lp);
        if (MarketRegionInfo.isFunctionSupport(35)) {
            preferenceScreen.addPreference(util2.createPreference("Samba_Settings", (int) R.string.menu_samba_setting, i));
        }
    }

    private String[] addSuffix(String[] str, int strId) {
        if (this.mContext == null) {
            return null;
        }
        for (int i = 0; i < str.length; i++) {
            String numStr = str[i];
            int num = (numStr == null || numStr.isEmpty() || !numStr.matches("^[0-9]*$")) ? Integer.MAX_VALUE : Integer.parseInt(numStr);
            if (num != Integer.MAX_VALUE) {
                str[i] = this.mContext.getResources().getString(strId, new Object[]{Integer.valueOf(num)});
            }
        }
        return str;
    }

    private PreferenceScreen addParentalChannelBlock(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        List<MtkTvChannelInfoBase> list = MenuDataHelper.getInstance(this.mContext).getTVChannelList();
        if (!this.mTV.isCurrentSourceTv() || !CommonIntegration.getInstance().hasActiveChannel()) {
            preferenceScreen.setEnabled(false);
        } else {
            preferenceScreen.setEnabled(true);
        }
        int idx = 0;
        if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
            int currentChannelID = EditChannel.getInstance(themedContext).getCurrentChannelId();
            EditChannel.getInstance(themedContext).blockChannel(currentChannelID, !EditChannel.getInstance(themedContext).isChannelBlock(currentChannelID));
        }
        for (MtkTvChannelInfoBase infobase : list) {
            int tid = infobase.getChannelId();
            idx++;
            int initvalue = EditChannel.getInstance(themedContext).isChannelBlock(infobase.getChannelId());
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(this.mDataHelper.getDisplayChNumber(tid));
            sb.append("        ");
            sb.append(infobase.getServiceName() == null ? "" : infobase.getServiceName());
            String name = sb.toString();
            Preference channelblock = util2.createSwitchPreference("parental_block_channellist:" + infobase.getChannelId(), name, EditChannel.getInstance(themedContext).isChannelBlock(infobase.getChannelId()));
            if (infobase instanceof MtkTvAnalogChannelInfo) {
                channelblock.setSummary((CharSequence) "ATV");
            } else {
                channelblock.setSummary((CharSequence) "DTV");
            }
            preferenceScreen.addPreference(channelblock);
        }
        return preferenceScreen;
    }

    private void addFactoryVideoSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.menu_factory_auto_dima_array);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.menu_factory_video_diedge_array);
        this.ItemName = "g_misc__flip";
        boolean z = false;
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_video_flip, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_misc__mirror";
        String str = this.ItemName;
        if (util2.mConfigManager.getDefault(this.ItemName) == 1) {
            z = true;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_video_mirror, z));
    }

    private void addFactoryAudioSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] mFaCompression = this.mContext.getResources().getStringArray(R.array.menu_factory_audio_compression_array);
        String[] mFaCompressionFactor = this.mContext.getResources().getStringArray(R.array.menu_factory_audio_compressionfactor_array);
        if (CommonIntegration.isCNRegion()) {
            mFaCompressionFactor = this.mContext.getResources().getStringArray(R.array.menu_factory_audio_compressionfactor_array_cn);
        }
        String[] mFaCompressionFactor2 = mFaCompressionFactor;
        this.ItemName = "g_audio__dolby_cert_mode";
        String str = this.ItemName;
        boolean z = true;
        if (util2.mConfigManager.getDefault(this.ItemName) != 1) {
            z = false;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_audio_dolby_banner, z));
        this.ItemName = "g_audio__dolby_cmpss";
        preferenceScreen.addPreference(util2.createListPreference(this.ItemName, (int) R.string.menu_factory_audio_compression, true, mFaCompression, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_audio__dolby_drc";
        preferenceScreen.addPreference(util2.createListPreference(this.ItemName, (int) R.string.menu_factory_audio_compression_factor, true, mFaCompressionFactor2, util2.mConfigManager.getDefault(this.ItemName)));
        if (CommonIntegration.isUSRegion()) {
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.FA_MTS_SYSTEM, (int) R.string.menu_factory_audio_mts_system));
        } else if (CommonIntegration.isEURegion() || CommonIntegration.isCNRegion()) {
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.FA_A2SYSTEM, (int) R.string.menu_factory_audio_a2_system));
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.FA_PALSYSTEM, (int) R.string.menu_factory_audio_pal_system));
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.FA_EUSYSTEM, (int) R.string.menu_factory_audio_eu_system));
        }
        this.ItemName = "g_audio__aud_latency";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_latency, false));
    }

    private void addFactoryTVSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        boolean isPVRSrt = this.mTV.isPVrRunning();
        boolean isTSSrt = this.mTV.isTshitRunning();
        boolean isDvrPlaying = StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning();
        MtkLog.i(TAG, "isDvrPlaying = " + isDvrPlaying);
        if (CommonIntegration.isUSRegion()) {
            this.ItemName = MenuConfigManager.FACTORY_TV_RANGE_SCAN;
            Intent intent = new Intent(this.mContext, ScanViewActivity.class);
            intent.putExtra("ActionID", this.ItemName);
            Preference preference = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_range_scan, intent);
            preference.setEnabled(!isPVRSrt && !isTSSrt && !isDvrPlaying);
            preferenceScreen.addPreference(preference);
            this.ItemName = MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN;
            Intent intent2 = new Intent(this.mContext, ScanViewActivity.class);
            intent2.putExtra("ActionID", this.ItemName);
            Preference preference2 = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_single_rf_scan, intent2);
            preference2.setEnabled(!isPVRSrt && !isTSSrt && !isDvrPlaying);
            preferenceScreen.addPreference(preference2);
            this.ItemName = MenuConfigManager.FACTORY_TV_FACTORY_SCAN;
            Intent intent3 = new Intent(this.mContext, ScanDialogActivity.class);
            intent3.putExtra("ActionID", this.ItemName);
            Preference preference3 = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_factory_scan, intent3);
            preference3.setEnabled(!isPVRSrt && !isTSSrt && !isDvrPlaying);
            preferenceScreen.addPreference(preference3);
        } else if (CommonIntegration.isSARegion()) {
            this.ItemName = MenuConfigManager.FACTORY_TV_RANGE_SCAN_DIG;
            Intent intent4 = new Intent(this.mContext, ScanViewActivity.class);
            intent4.putExtra("ActionID", this.ItemName);
            Preference facTVDigitalChannelScan = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_digital_channel_scan, intent4);
            facTVDigitalChannelScan.setEnabled(!isPVRSrt && !isTSSrt && this.mTV.getCurrentTunerMode() < 1 && !isDvrPlaying);
            preferenceScreen.addPreference(facTVDigitalChannelScan);
            this.ItemName = MenuConfigManager.FACTORY_TV_RANGE_SCAN_ANA;
            Intent intent5 = new Intent(this.mContext, ScanViewActivity.class);
            intent5.putExtra("ActionID", this.ItemName);
            Preference preference4 = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_analog_channel_scan, intent5);
            preference4.setEnabled(!isPVRSrt && !isTSSrt && !isDvrPlaying);
            preferenceScreen.addPreference(preference4);
            this.ItemName = MenuConfigManager.FACTORY_TV_SINGLE_RF_SCAN;
            Intent intent6 = new Intent(this.mContext, ScanViewActivity.class);
            intent6.putExtra("ActionID", this.ItemName);
            Preference preference5 = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_single_rf_scan, intent6);
            preference5.setEnabled(!isPVRSrt && !isTSSrt && this.mTV.getCurrentTunerMode() < 1 && !isDvrPlaying);
            if (this.mTV.getCurrentTunerMode() >= 1) {
                facTVDigitalChannelScan.setEnabled(false);
                preference5.setEnabled(false);
            }
            preferenceScreen.addPreference(preference5);
        } else if (CommonIntegration.isEURegion()) {
            this.ItemName = MenuConfigManager.FACTORY_TV_RANGE_SCAN_DIG;
            Intent intent7 = new Intent(this.mContext, ScanViewActivity.class);
            intent7.putExtra("ActionID", this.ItemName);
            Preference preference6 = util2.createPreference(this.ItemName, (int) R.string.menu_factory_TV_digital_channel_scan, intent7);
            preference6.setEnabled(!isPVRSrt && !isTSSrt && !isDvrPlaying);
            if (this.mTV.getCurrentTunerMode() >= 1) {
                preference6.setEnabled(false);
            }
            preferenceScreen.addPreference(preference6);
        }
        if (CommonIntegration.isEURegion() || CommonIntegration.isUSRegion() || CommonIntegration.isCNRegion()) {
            Preference preference7 = util2.createPreference(MenuConfigManager.FACTORY_TV_TUNER_DIAGNOSTIC, (int) R.string.menu_factory_TV_tunerdiag);
            preferenceScreen.addPreference(preference7);
            if (TifTimeShiftManager.getInstance() == null || !TifTimeShiftManager.getInstance().isAvailable()) {
                preference7.setEnabled(true);
            } else {
                preference7.setEnabled(false);
            }
        }
    }

    private void addFactorySetupSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] mFvEventForm = this.mContext.getResources().getStringArray(R.array.menu_factory_setup_eventform);
        Preference cleanStorage = util2.createClickPreference(MenuConfigManager.FACTORY_SETUP_CLEAN_STORAGE, (int) R.string.menu_factory_setup_cleanstorage, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
        this.ItemName = "g_misc__uart_factory_mode";
        String str = this.ItemName;
        boolean z = true;
        if (util2.mConfigManager.getDefault(this.ItemName) != 1) {
            z = false;
        }
        Preference uartMode = util2.createSwitchPreference(str, (int) R.string.menu_factory_setup_uart_factory_mode, z);
        if (CommonIntegration.isUSRegion()) {
            preferenceScreen.addPreference(cleanStorage);
            preferenceScreen.addPreference(uartMode);
        } else if (CommonIntegration.isSARegion()) {
            preferenceScreen.addPreference(uartMode);
            preferenceScreen.addPreference(cleanStorage);
            this.ItemName = MenuConfigManager.FACTORY_SETUP_CAPTION;
            preferenceScreen.addPreference(util2.createPreference(this.ItemName, (int) R.string.menu_factory_setup_caption));
        } else if (CommonIntegration.isEURegion()) {
            this.ItemName = "g_misc__evt_form";
            preferenceScreen.addPreference(util2.createListPreference(this.ItemName, (int) R.string.menu_factory_setup_eventform, true, mFvEventForm, util2.mConfigManager.getDefault(this.ItemName)));
            this.ItemName = MenuConfigManager.FACTORY_SETUP_CI_UPDATE;
            Preference updateCi = util2.createClickPreference(this.ItemName, (int) R.string.menu_factory_setup_ci_update, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
            this.ItemName = MenuConfigManager.FACTORY_SETUP_CI_ECP_UPDATE;
            Preference updateCi1 = util2.createClickPreference(this.ItemName, (int) R.string.menu_factory_setup_ci_ecp_update, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
            this.ItemName = MenuConfigManager.FACTORY_SETUP_CI_ERASE;
            Preference eraseCi = util2.createClickPreference(this.ItemName, (int) R.string.menu_factory_setup_ci_erase, (Preference.OnPreferenceClickListener) FacSetup.getInstance(this.mContext));
            if (CommonIntegration.isEURegion()) {
                preferenceScreen.addPreference(uartMode);
                preferenceScreen.addPreference(updateCi);
                preferenceScreen.addPreference(updateCi1);
                preferenceScreen.addPreference(eraseCi);
            }
            preferenceScreen.addPreference(cleanStorage);
            if (CommonIntegration.isEURegion()) {
                this.ItemName = MenuConfigManager.TKGS_FAC_SETUP_AVAIL_CONDITION;
                PreferenceUtil preferenceUtil = util2;
                preferenceScreen.addPreference(preferenceUtil.createListPreference(this.ItemName, "TKGS Availability Condition", true, new String[]{"Normal", "Certification"}, util2.mConfigManager.getDefault(this.ItemName)));
            }
        }
    }

    private void addFactoryPresetSettings(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        preferenceScreen.addPreference(util2.createClickPreference(MenuConfigManager.FACTORY_PRESET_CH_DUMP, (int) R.string.menu_factory_preset_dump, (Preference.OnPreferenceClickListener) FacPreset.getInstance(this.mContext)));
        preferenceScreen.addPreference(util2.createClickPreference(MenuConfigManager.FACTORY_PRESET_CH_PRINT, (int) R.string.menu_factory_preset_print, (Preference.OnPreferenceClickListener) FacPreset.getInstance(this.mContext)));
        preferenceScreen.addPreference(util2.createClickPreference(MenuConfigManager.FACTORY_PRESET_CH_RESTORE, (int) R.string.menu_factory_preset_restore, (Preference.OnPreferenceClickListener) FacPreset.getInstance(this.mContext)));
    }

    private PreferenceScreen addFacVidColorTmp(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        String[] mFvColor = this.mContext.getResources().getStringArray(R.array.menu_factory_video_colortemperature_array);
        this.ItemName = "g_video__clr_temp";
        Preference preference = util2.createListPreference(this.ItemName, (int) R.string.menu_factory_video_color_temperature, true, mFvColor, util2.mConfigManager.getDefault(this.ItemName) - util2.mConfigManager.getMin(this.ItemName));
        preferenceScreen.addPreference(preference);
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_gain_r", R.string.menu_factory_video_color_rgain, false));
        preferenceScreen.addPreference(preference);
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_gain_g", R.string.menu_factory_video_color_ggain, false));
        preferenceScreen.addPreference(preference);
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_gain_b", R.string.menu_factory_video_color_bgain, false));
        preferenceScreen.addPreference(preference);
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_offset_r", R.string.menu_factory_video_color_roffset, false));
        preferenceScreen.addPreference(preference);
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_offset_g", R.string.menu_factory_video_color_goffset, false));
        preferenceScreen.addPreference(preference);
        preferenceScreen.addPreference(util2.createProgressPreference("g_video__clr_offset_b", R.string.menu_factory_video_color_boffset, false));
        preferenceScreen.addPreference(preference);
        return preferenceScreen;
    }

    private PreferenceScreen addFacAudMts(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.ItemName = "g_misc__number_of_check";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_numbersofcheck, false, 30, 80, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__numbers_of_pilot";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_numbersofpilot, false, 0, 50, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__pilot_threshold_high";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_pilot_threshold_high, false));
        this.ItemName = "g_misc__pilot_threshold_low";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_pilot_threshold_low, false, 80, 150, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__numbers_of_sap";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_number_of_sap, false, 0, 50, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__sap_threshold_hith";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_sap_threshold_high, false));
        this.ItemName = "g_misc__sap_threshold_low";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_sap_threshold_low, false, 70, 130, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__high_deviation_mode";
        boolean z = true;
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_audio_mts_high_deviation_mode, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_misc__carrier_shift_function";
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_audio_mts_carriershiftfunction, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_misc__fm_saturation_mute";
        String str = this.ItemName;
        if (util2.mConfigManager.getDefault(this.ItemName) != 1) {
            z = false;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_audio_mts_fmstaurationmute, z));
        this.ItemName = "g_misc__fm_carrier_mute_mode";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_fmcarriermutemode, false));
        this.ItemName = "g_misc__fm_carrier_mute_threshold_high";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_fmcarriermutethreshold_high, false, 20, 180, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__fm_carrier_mute_threshold_low";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_fmcarriermutethreshold_high, false, 110, 180, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__mono_stero_fine_tune_volume";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_mono_stero_finetunevolume, false, 0, 40, util2.mConfigManager.getDefault(this.ItemName)));
        this.ItemName = "g_misc__sap_fine_tune_volume";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_mts_sap_finetunevolume, false, 0, 40, util2.mConfigManager.getDefault(this.ItemName)));
        return preferenceScreen;
    }

    private PreferenceScreen addFacAudA2System(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.ItemName = "g_misc__a2 sys num of check";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_numbersofcheck, false));
        this.ItemName = "g_misc__a2 sys num of double";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_numbersofdouble, false));
        this.ItemName = "g_misc__a2 sys mono wight";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_monoweight, false));
        this.ItemName = "g_misc__a2 sysstereo weight";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_stereoweight, false));
        this.ItemName = "g_misc__a2 sys dual weight";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_dualweight, false));
        this.ItemName = "g_misc__a2 sys hight deviation mode";
        boolean z = true;
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_audio_a2_highdeviationmode, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_misc__a2 sys carrier shift function";
        String str = this.ItemName;
        if (util2.mConfigManager.getDefault(this.ItemName) != 1) {
            z = false;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_audio_a2_carriershiftfunction, z));
        this.ItemName = "g_misc__a2 sys fm carrier mute mode";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_fmcarriermutemode, false));
        this.ItemName = "g_misc__a2 sys fm carrier mute threshold hight";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_fmcarriermutethreshold_high, false));
        this.ItemName = "g_misc__a2 sys fm carrier mute threshold low";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_fmcarriermutethreshold_low, false));
        this.ItemName = "g_misc__a2 sys fine tune valume";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_a2_finetunevolume, false));
        return preferenceScreen;
    }

    private PreferenceScreen addFacAudPalSystem(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.ItemName = "g_misc__correct threshold";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_correctthreshold, false));
        this.ItemName = "g_misc__total sync loop";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_totalsyncloop, false));
        this.ItemName = "g_misc__error threshold";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_errorthreshold, false));
        this.ItemName = "g_misc__parity error threshold";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_parityerrorthreshold, false));
        this.ItemName = "g_misc__every num frames";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_everynumberframes, false));
        this.ItemName = "g_misc__high deviation mode";
        boolean z = true;
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_audio_pal_highdeviationmode, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_misc__am carrier mute mode";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_amcarriermutemode, false));
        this.ItemName = "g_misc__am carrier mute threshold high";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_amcarriermutethresholdhigh, false));
        this.ItemName = "g_misc__am carrier mute threshold low";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_amcarriermutethresholdlow, false));
        this.ItemName = "g_misc__carrier shift function";
        String str = this.ItemName;
        if (util2.mConfigManager.getDefault(this.ItemName) != 1) {
            z = false;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_audio_pal_carriershiftfunction, z));
        this.ItemName = "g_misc__fm carrier mute mode";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_fmcarriermutemode, false));
        this.ItemName = "g_misc__fm carrier mute threshold high";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_fmcarriermutethresholdhigh, false));
        this.ItemName = "g_misc__fm carrier mute threshold low";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_fmcarriermutethresholdlow, false));
        this.ItemName = "g_misc__pal fine tune volume";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_palfinetunevolume, false));
        this.ItemName = "g_misc__am fine tune volume";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_amfinetunevolume, false));
        this.ItemName = "g_misc__nicam fine tune volume";
        preferenceScreen.addPreference(util2.createProgressPreference(this.ItemName, R.string.menu_factory_audio_pal_nicamfinetunevolume, false));
        return preferenceScreen;
    }

    private PreferenceScreen addFacAudEuSystem(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.ItemName = "g_misc__fm_saturation_mute";
        boolean z = false;
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_audio_eu_fmsaturationmute, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_misc__non_eu";
        String str = this.ItemName;
        if (util2.mConfigManager.getDefault(this.ItemName) == 1) {
            z = true;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_audio_eu_eunoneusystem, z));
        return preferenceScreen;
    }

    private void timeShiftStop() {
        this.mContext.sendBroadcast(new Intent(TifTimeshiftView.ACTION_TIMESHIFT_STOP));
    }

    private PreferenceScreen addFacTVdiagnostic(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        timeShiftStop();
        List<String> displayName = new ArrayList<>();
        List<String> displayValue = new ArrayList<>();
        if (!this.mTV.isCurrentSourceTv() || this.mTV.isCurrentSourceDTV()) {
            MtkLog.d(TAG, "addFacTVdiagnostic not show");
            MtkTvUtil.getInstance().tunerFacQuery(false, displayName, displayValue);
        } else {
            MtkLog.d(TAG, "addFacTVdiagnostic show atv ==" + this.mTV.isCurrentSourceATV());
            MtkTvUtil.getInstance().tunerFacQuery(true, displayName, displayValue);
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= displayName.size()) {
                break;
            }
            preferenceScreen.addPreference(util2.createListPreference(MenuConfigManager.FACTORY_TV_TUNER_DIAGNOSTIC_NOINFO + i2, displayName.get(i2), true, new String[]{displayValue.get(i2)}, 0));
            i = i2 + 1;
        }
        if (displayName.size() == 0) {
            preferenceScreen.addPreference(util2.createPreference(MenuConfigManager.FACTORY_TV_TUNER_DIAGNOSTIC_NOINFO, (int) R.string.menu_factory_TV_tunerdiag_noinfo, (Intent) null));
        }
        return preferenceScreen;
    }

    private PreferenceScreen addFacSetupCaption(PreferenceScreen preferenceScreen, Context themedContext) {
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        this.ItemName = "g_cc__cc_attr_ex_size_idx";
        boolean z = false;
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_setup_extern_size, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_cc__cc_attr_equal_width_idx";
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_setup_equal_width, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_cc__cc_attr_auto_line_feed_idx";
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_setup_auto_line, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_cc__cc_attr_roll_up_mode_idx";
        preferenceScreen.addPreference(util2.createSwitchPreference(this.ItemName, (int) R.string.menu_factory_setup_roll_up, util2.mConfigManager.getDefault(this.ItemName) == 1));
        this.ItemName = "g_cc__cc_attr_support_utf8_idx";
        String str = this.ItemName;
        if (util2.mConfigManager.getDefault(this.ItemName) == 1) {
            z = true;
        }
        preferenceScreen.addPreference(util2.createSwitchPreference(str, (int) R.string.menu_factory_setup_utf8, z));
        return preferenceScreen;
    }

    private void bindChannelPreference() {
        Map<String, Preference> storeMap = this.mDataHelper.getChannelPreferenceMap();
        storeMap.put("channelskip", this.channelskip);
        storeMap.put("channelSort", this.channelSort);
        storeMap.put("channelEdit", this.channelEdit);
        storeMap.put("cleanList", this.cleanList);
        storeMap.put("saChannelEdit", this.saChannelEdit);
        storeMap.put("saChannelFine", this.saChannelFine);
        storeMap.put("updateScan", this.updateScan);
    }

    private Intent bindIntentForPreference(Preference binded) {
        if (binded == null) {
            return null;
        }
        String itemID = binded.getKey();
        String mItem = "";
        if (itemID.equals(MenuConfigManager.TV_SA_CHANNEL_EDIT) || itemID.equals(MenuConfigManager.TV_CHANNEL_EDIT)) {
            mItem = MenuConfigManager.TV_CHANNEL_EDIT_LIST;
        } else if (itemID.equals(MenuConfigManager.TV_CHANNELFINE_TUNE)) {
            mItem = MenuConfigManager.TV_CHANNELFINE_TUNE_EDIT_LIST;
        } else if (itemID.equals(MenuConfigManager.TV_CHANNEL_SORT)) {
            mItem = MenuConfigManager.TV_CHANNEL_SORT_CHANNELLIST;
        } else if (itemID.equals(MenuConfigManager.TV_CHANNEL_DECODE)) {
            mItem = MenuConfigManager.TV_CHANNEL_DECODE_LIST;
        } else if (itemID.equals(MenuConfigManager.TV_CHANNEL_SKIP)) {
            mItem = MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST;
        }
        Intent intent = new Intent(this.mContext, ChannelInfoActivity.class);
        intent.putExtra("TransItem", new TransItem(mItem, "", 10004, 10004, 10004));
        intent.putExtra("ActionID", itemID);
        binded.setIntent(intent);
        return intent;
    }

    private Intent setSatActivityIntent(String mItemId, String title, int pos) {
        Intent intent = new Intent(this.mContext, SatActivity.class);
        intent.putExtra("mItemId", mItemId);
        intent.putExtra("title", title);
        intent.putExtra("selectPos", pos);
        return intent;
    }

    private void setSatListFragArgument(String mItemId, String title, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("mItemId", mItemId);
        bundle.putString("title", title);
        bundle.putInt("selectPos", pos);
        this.satListFrag.setArguments(bundle);
    }

    private void cleanParentalChannelConfirm() {
        this.mCleanAllConfirmDialog = new LiveTVDialog(this.mContext, "", this.mContext.getString(R.string.menu_tv_clear_channel_info), 3);
        this.mCleanAllConfirmDialog.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.mCleanAllConfirmDialog.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.mCleanAllConfirmDialog.getButtonNo().requestFocus();
        this.mCleanAllConfirmDialog.setPositon(-20, 70);
        this.mCleanAllConfirmDialog.bindKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23 && keyCode != 183)) {
                    return false;
                }
                if (v.getId() == SettingsPreferenceScreen.this.mCleanAllConfirmDialog.getButtonYes().getId()) {
                    SettingsPreferenceScreen.this.mDataHelper.resetCallFlashStore();
                    SettingsPreferenceScreen.this.mEditChannel.resetDefAfterClean();
                    ProgressDialog unused = SettingsPreferenceScreen.this.pdialog = ProgressDialog.show(SettingsPreferenceScreen.this.mContext, "Clean All", "reseting please wait...", false, false);
                    SettingsPreferenceScreen.this.mEditChannel.resetParental(SettingsPreferenceScreen.this.mContext, new Runnable() {
                        public void run() {
                            SettingsPreferenceScreen.this.mCleanAllConfirmDialog.dismiss();
                        }
                    });
                    SettingsPreferenceScreen.this.mTV.resetPri(SettingsPreferenceScreen.this.mHandler);
                    return true;
                } else if (v.getId() != SettingsPreferenceScreen.this.mCleanAllConfirmDialog.getButtonNo().getId()) {
                    return true;
                } else {
                    SettingsPreferenceScreen.this.mCleanAllConfirmDialog.dismiss();
                    return true;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void networkUpdate() {
        this.netWorkUpgradeDialog = new TurnkeyCommDialog(this.mContext, 0);
        this.netWorkUpgradeDialog.setMessage(this.mContext.getResources().getString(R.string.menu_setup_upgrade_info1));
        this.netWorkUpgradeDialog.show();
        new Rect((ScreenConstant.SCREEN_WIDTH - this.netWorkUpgradeDialog.width) / 2, (ScreenConstant.SCREEN_HEIGHT - this.netWorkUpgradeDialog.height) / 2, (ScreenConstant.SCREEN_WIDTH + this.netWorkUpgradeDialog.width) / 2, (ScreenConstant.SCREEN_HEIGHT + this.netWorkUpgradeDialog.height) / 2);
        Handler handler = new Handler();
        final NetUpdateGider netUpdateGiderDialog = new NetUpdateGider(this.mContext, this.netWorkUpgradeDialog);
        handler.postDelayed(netUpdateGiderDialog, 1000);
        netUpdateGiderDialog.getShowDialog().setTitle(this.mContext.getResources().getString(R.string.menu_setup_upgrade_info2_title));
        netUpdateGiderDialog.getShowDialog().setMessage(this.mContext.getResources().getString(R.string.menu_setup_upgrade_info2));
        netUpdateGiderDialog.getShowDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return true;
                }
                netUpdateGiderDialog.getShowDialog().dismiss();
                return true;
            }
        });
    }

    private boolean getDefaultBoolean(int value) {
        if (1 == value) {
            return true;
        }
        return false;
    }

    private void loadContentRatingsSystems(PreferenceScreen preferenceScreen, Context themedContext, String id) {
        PreferenceScreen preferenceScreen2 = preferenceScreen;
        String str = id;
        PreferenceUtil util2 = PreferenceUtil.getInstance(themedContext);
        List<ContentRatingSystem> mContentRatingSystems = util2.mConfigManager.loadContentRatingsSystems();
        if (mContentRatingSystems != null && mContentRatingSystems.size() != 0) {
            if (str == null) {
                for (ContentRatingSystem info : mContentRatingSystems) {
                    List<ContentRatingSystem.Rating> ratings = info.getRatings();
                    if (info.isCustom()) {
                        Log.d(TAG, "[Ratings] contents:" + info.getTitle() + "," + info.isCustom() + "," + info.getName());
                        StringBuilder sb = new StringBuilder();
                        sb.append("parental_tif_content_ratings|");
                        sb.append(info.getId());
                        sb.append("|");
                        sb.append(info.getTitle());
                        preferenceScreen2.addPreference(util2.createPreference(sb.toString(), info.getTitle()));
                    }
                }
                Context context = themedContext;
                List<ContentRatingSystem> list = mContentRatingSystems;
                return;
            }
            if (str.startsWith(MenuConfigManager.PARENTAL_TIF_CONTENT_RATGINS)) {
                String[] strInfo = str.split("\\|");
                if (strInfo != null) {
                    int i = 2;
                    if (strInfo.length < 2) {
                        Context context2 = themedContext;
                        List<ContentRatingSystem> list2 = mContentRatingSystems;
                        return;
                    }
                    Log.d(TAG, "[Ratings] id: " + str + ", " + strInfo[strInfo.length - 2]);
                    for (ContentRatingSystem info2 : mContentRatingSystems) {
                        if (info2.isCustom() && strInfo[strInfo.length - i].equals(info2.getId())) {
                            List<ContentRatingSystem.Rating> ratins = info2.getRatings();
                            this.ItemName = "parental_tif_content_ratings_system|" + info2.getId() + "|" + info2.getTitle();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("[Ratings] ItemName:");
                            sb2.append(this.ItemName);
                            Log.d(TAG, sb2.toString());
                            boolean isEnabled = util2.mConfigManager.getDefault(this.ItemName) == 1;
                            preferenceScreen2.addPreference(util2.createSwitchPreference(this.ItemName, info2.getTitle(), isEnabled));
                            PreferenceCategory category = new PreferenceCategory(themedContext);
                            category.setTitle((CharSequence) info2.getTitle());
                            category.setKey(info2.getTitle());
                            preferenceScreen2.addPreference(category);
                            new String[(ratins.size() + 1)][0] = MtkTvConfigTypeBase.S639_CFG_SUBTITLE_LANG_OFF;
                            int i2 = 0;
                            while (true) {
                                int i3 = i2;
                                if (i3 >= ratins.size()) {
                                    break;
                                }
                                this.ItemName = "parental_tif_ratings_system_cnt|" + info2.getId() + "|" + ratins.get(i3).getTitle();
                                List<ContentRatingSystem> mContentRatingSystems2 = mContentRatingSystems;
                                Preference pref = util2.createSwitchPreference(this.ItemName, ratins.get(i3).getTitle(), util2.mConfigManager.getDefault(this.ItemName) == 1);
                                pref.setEnabled(isEnabled);
                                preferenceScreen2.addPreference(pref);
                                i2 = i3 + 1;
                                mContentRatingSystems = mContentRatingSystems2;
                                String str2 = id;
                            }
                            String str3 = id;
                            i = 2;
                        }
                    }
                } else {
                    Context context3 = themedContext;
                    List<ContentRatingSystem> list3 = mContentRatingSystems;
                    return;
                }
            }
            Context context4 = themedContext;
            List<ContentRatingSystem> list4 = mContentRatingSystems;
        }
    }

    /* access modifiers changed from: private */
    public void updateBissKey() {
        Iterator<Preference> it = this.bisskeyPreferences.iterator();
        while (it.hasNext()) {
            this.bisskeyGroup.removePreference(it.next());
        }
        this.bissitems = this.mHelper.convertToBissItemList();
        if (this.bissitems != null) {
            int size = this.bissitems.size();
            PreferenceUtil util2 = PreferenceUtil.getInstance(this.mPreferenceManager.getContext());
            for (int i = 0; i < size; i++) {
                Preference bisskeyPreference = util2.createPreference(MenuConfigManager.BISS_KEY_ITEM_UPDATE + (i + 1), this.bissitems.get(i).progId + " " + this.bissitems.get(i).threePry + " " + this.bissitems.get(i).cwKey);
                this.bisskeyGroup.addPreference(bisskeyPreference);
                this.bisskeyPreferences.add(bisskeyPreference);
            }
        }
        InstrumentationHandler.getInstance().sendKeyDownUpSync(4);
    }

    private boolean isSupportBissKey() {
        int freq = -1;
        int rate = -1;
        int prog_id = -1;
        BissListAdapter.BissItem defItem = this.mHelper.getDefaultBissItem();
        if (defItem != null) {
            int findPola = defItem.threePry.indexOf(72);
            if (findPola == -1) {
                findPola = defItem.threePry.indexOf(86);
            }
            freq = Integer.parseInt(defItem.threePry.substring(0, findPola));
            rate = Integer.parseInt(defItem.threePry.substring(findPola + 1));
            prog_id = defItem.progId;
            String cwKeystr = defItem.cwKey;
        }
        if (freq == 0 && rate == 0 && prog_id == 0) {
            return false;
        }
        return true;
    }

    class ScanPreferenceClickListener implements Preference.OnPreferenceClickListener {
        String mActionId = null;

        ScanPreferenceClickListener() {
        }

        public void setActionId(String actionId) {
            this.mActionId = actionId;
        }

        public boolean onPreferenceClick(Preference preference) {
            PinDialogFragment dialog = PinDialogFragment.create(7);
            if (!MenuConfigManager.CHANNEL_CHANNEL_SOURCES.equals(this.mActionId)) {
                ((TvSettingsActivity) SettingsPreferenceScreen.this.mContext).hide();
            }
            ((TvSettingsActivity) SettingsPreferenceScreen.this.mContext).setActionId(this.mActionId);
            dialog.show(((TvSettingsActivity) SettingsPreferenceScreen.this.mContext).getFragmentManager(), "PinDialogFragment");
            return true;
        }
    }
}
