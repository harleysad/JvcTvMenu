package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;

public class SundryShowTextView extends NavBasicView implements ComponentStatusListener.ICStatusListener {
    private static final int MTS_AUDIO_CHANGE = 6;
    public static final String SLEEP_TIMER = "SETUP_sleep_timer";
    public static final String SLEEP_TIMER_ACTION = "com.mediatek.ui.menu.util.sleep.timer";
    private static final String TAG = "NavSundryShowTextView";
    private static final int TV_SCREEN_MODE_DOT_BY_DOT = 6;
    /* access modifiers changed from: private */
    public ComponentsManager comManager;
    private int index = 0;
    private boolean isSleep = false;
    /* access modifiers changed from: private */
    public boolean isSundryTime = false;
    /* access modifiers changed from: private */
    public int lastPressKeyCode;
    /* access modifiers changed from: private */
    public CommonIntegration mIntegration;
    /* access modifiers changed from: private */
    public IntegrationZoom mIntegrationZoom;
    /* access modifiers changed from: private */
    public SundryImplement mNavSundryImplement;
    private SaveValue mSaveValue;
    private MtkTvTime mTime;
    private TvCallbackHandler mTvCallbackHandler;
    private TVContent mTvContent;
    private final IntegrationZoom.ZoomListener mZoomListener = new IntegrationZoom.ZoomListener() {
        public void zoomShow(int value) {
            String[] unused = SundryShowTextView.this.mZoomMode = SundryShowTextView.this.getResources().getStringArray(R.array.nav_zoom_mode);
            SundryShowTextView.this.sundryTextView.setText(SundryShowTextView.this.mZoomMode[value]);
            SundryShowTextView.this.startTimeout(5000);
            MtkLog.d(SundryShowTextView.TAG, "zoomShow value = " + value + "mZoomTip" + SundryShowTextView.this.mZoomTip);
            if (SundryShowTextView.this.mZoomTip == null) {
                ZoomTipView unused2 = SundryShowTextView.this.mZoomTip = (ZoomTipView) SundryShowTextView.this.comManager.getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
            }
            MtkLog.d(SundryShowTextView.TAG, "zoomShow value = " + value + "mZoomTip" + SundryShowTextView.this.mZoomTip);
            if (SundryShowTextView.this.mZoomTip == null) {
                return;
            }
            if (value == 2) {
                SundryShowTextView.this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_ZOOM_PAN);
            } else {
                SundryShowTextView.this.mZoomTip.setVisibility(8);
            }
        }
    };
    /* access modifiers changed from: private */
    public String[] mZoomMode;
    /* access modifiers changed from: private */
    public ZoomTipView mZoomTip;
    /* access modifiers changed from: private */
    public final Handler navSundryHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 1879048198) {
                if (i != 1879048212) {
                    if (i != 1879048224) {
                    }
                    return;
                }
                TvCallbackData dataAvMode = (TvCallbackData) msg.obj;
                MtkLog.d(SundryShowTextView.TAG, "come in TvCallbackConst.MSG_CB_AV_MODE_MSG ==" + dataAvMode.param1);
                if (!MarketRegionInfo.isFunctionSupport(20) && 6 == dataAvMode.param1 && SundryShowTextView.this.mIsComponetShow && 213 == SundryShowTextView.this.lastPressKeyCode) {
                    SundryShowTextView.this.sundryTextView.setText(SundryShowTextView.this.mNavSundryImplement.getCurrentAudioLang());
                }
            } else if (!SundryShowTextView.this.mIntegration.isPipOrPopState()) {
                TvCallbackData data = (TvCallbackData) msg.obj;
                MtkLog.d(SundryShowTextView.TAG, "come in TvCallbackConst.MSG_CB_SVCTX_NOTIFY ==" + data.param1);
                if (5 == data.param1 || 10 == data.param1) {
                    if (SundryShowTextView.this.mIntegrationZoom == null) {
                        IntegrationZoom unused = SundryShowTextView.this.mIntegrationZoom = IntegrationZoom.getInstance((Context) null);
                    }
                    SundryShowTextView.this.mIntegrationZoom.setZoomModeToNormalWithThread();
                }
            }
        }
    };
    private String[] screenModeStringArray;
    private String[] sleepTimeStringArray;
    private String[] soundEffectStringArray;
    /* access modifiers changed from: private */
    public TextView sundryTextView;
    private int[] supportScreenModes;
    private int[] supportSoundEffects;
    private int times = 0;
    private TextToSpeechUtil ttsUtil;
    Runnable updateTime = new Runnable() {
        public void run() {
            if (SundryShowTextView.this.isSundryTime && SundryShowTextView.this.mIsComponetShow) {
                SundryShowTextView.this.sundryTextView.setText(SundryShowTextView.this.mNavSundryImplement.getCurrentTime());
                SundryShowTextView.this.navSundryHandler.postDelayed(this, 1000);
            }
        }
    };

    public SundryShowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SundryShowTextView(Context context) {
        super(context);
        init(context);
    }

    public SundryShowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mTvContent = TVContent.getInstance(this.mContext);
        this.componentID = NavBasic.NAV_COMP_ID_SUNDRY;
        this.comManager = ComponentsManager.getInstance();
        this.mIntegration = CommonIntegration.getInstance();
        this.mSaveValue = SaveValue.getInstance(context);
        this.mZoomTip = (ZoomTipView) this.comManager.getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
        this.mIntegrationZoom = IntegrationZoom.getInstance(this.mContext);
        this.mIntegrationZoom.setZoomListener(this.mZoomListener);
        this.mNavSundryImplement = SundryImplement.getInstanceNavSundryImplement(context);
        this.mTvCallbackHandler = TvCallbackHandler.getInstance();
        this.mTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_FEATURE_MSG, this.navSundryHandler);
        this.mTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_AV_MODE_MSG, this.navSundryHandler);
        this.mTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, this.navSundryHandler);
        this.screenModeStringArray = getResources().getStringArray(R.array.screen_mode_array_us);
        this.soundEffectStringArray = getResources().getStringArray(R.array.menu_audio_equalizer_array_us);
        this.mZoomMode = getResources().getStringArray(R.array.nav_zoom_mode);
        initSundryView();
        this.mTime = MtkTvTime.getInstance();
        ComponentStatusListener.getInstance().addListener(8, this);
        ComponentStatusListener.getInstance().addListener(4, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        this.ttsUtil = new TextToSpeechUtil(context);
    }

    public void setVisibility(int visibility) {
        if (visibility == 0) {
            startTimeout(5000);
        } else {
            this.isSleep = false;
        }
        ((TurnkeyUiMainActivity) this.mContext).getSundryLayout().setVisibility(visibility);
        super.setVisibility(visibility);
    }

    public boolean isKeyHandler(int keyCode) {
        Hbbtv hbbtv;
        MtkLog.v(TAG, "isKeyHandler");
        this.lastPressKeyCode = keyCode;
        this.isSundryTime = false;
        MtkLog.v(TAG, "keyCode:" + keyCode);
        if (keyCode != 213) {
            if (keyCode != 225) {
                if (keyCode == 251) {
                    this.mContext.startActivity(new Intent("android.settings.SETTINGS").putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_PICTURE_STYLE_SRC));
                    return false;
                } else if (keyCode == 255) {
                    BannerView bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                    if (bannerView.isShown()) {
                        bannerView.setVisibility(8);
                    }
                    InfoBarDialog info = (InfoBarDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INFO_BAR);
                    if (info != null) {
                        info.handlerMessage(4);
                    }
                    if (MarketRegionInfo.isFunctionSupport(30)) {
                        DvrManager.getInstance().uiManager.dissmiss();
                    }
                    return this.mIntegrationZoom.showCurrentZoom();
                } else if (keyCode == 10066) {
                    MtkLog.d(TAG, "come in isKeyHandler");
                    if (!this.mIsComponetShow) {
                        this.isSundryTime = true;
                        MtkLog.d(TAG, "come in isKeyHandler to show time");
                        this.sundryTextView.setText(this.mNavSundryImplement.getCurrentTime());
                        this.navSundryHandler.removeCallbacks(this.updateTime);
                        this.navSundryHandler.postDelayed(this.updateTime, 1000);
                    }
                    return true;
                } else if (keyCode == 10467) {
                    int value = MtkTvConfig.getInstance().getConfigValue("g_record__rec_tshift_mode");
                    MtkLog.d(TAG, "value:" + value);
                    if (value != 0) {
                        return false;
                    }
                    if (!this.mIsComponetShow) {
                        MtkLog.d(TAG, "isKeyHandler, FREEZE");
                        handlerFreezeKey();
                    }
                    return true;
                } else if (keyCode != 10471) {
                    switch (keyCode) {
                        case 222:
                            MtkLog.d(TAG, "come in isKeyHandler KEYCODE_MTKIR_SEFFECT");
                            if (!this.mIsComponetShow) {
                                if (this.mNavSundryImplement.isHeadphoneSetOn()) {
                                    this.sundryTextView.setText(R.string.nav_no_function);
                                    return true;
                                }
                                this.mContext.startActivity(new Intent("android.settings.SETTINGS").putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_SOUND_STYLE_SRC));
                                return false;
                            }
                            break;
                        case KeyMap.KEYCODE_MTKIR_SLEEP /*223*/:
                            return true;
                    }
                    MtkLog.v(TAG, "isKeyHandler false");
                    return false;
                }
            }
            if (MarketRegionInfo.getCurrentMarketRegion() == 3 && ComponentsManager.getActiveCompId() == 16777233) {
                return false;
            }
            if (this.mNavSundryImplement == null) {
                MtkLog.d(TAG, "isKeyHandler mNavSundryImplement == null!!!");
                return false;
            }
            this.supportScreenModes = this.mNavSundryImplement.getSupportScreenModes();
            MtkLog.d(TAG, "supportScreenModes:" + this.supportScreenModes);
            MtkLog.v(TAG, "ComponentsManager.getActiveCompId():" + ComponentsManager.getActiveCompId());
            if (this.supportScreenModes == null || this.mNavSundryImplement.getInternalScrnMode() || this.mNavSundryImplement.isGingaWindowResize()) {
                this.sundryTextView.setText(R.string.nav_no_function);
            } else if (!this.mIsComponetShow) {
                if (this.mNavSundryImplement.isFreeze()) {
                    this.mNavSundryImplement.setFreeze(false);
                }
                if (1 != this.mIntegrationZoom.getCurrentZoom() && !this.mIntegration.isPipOrPopState()) {
                    if (this.mZoomTip != null && this.mZoomTip.getVisibility() == 0) {
                        this.mZoomTip.setVisibility(8);
                    }
                    if (ComponentsManager.getNativeActiveCompId() != 33554435) {
                        this.mIntegrationZoom.setZoomMode(1);
                    }
                }
                this.mContext.startActivity(new Intent("android.settings.SETTINGS").putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_DISPLAY_MODE_SRC));
                return false;
            }
            return true;
        }
        MtkLog.d(TAG, "come in isKeyHandler mIsComponetShow = " + this.mIsComponetShow);
        if (ComponentsManager.getNativeActiveCompId() == 33554434 && (hbbtv = (Hbbtv) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_NATIVE_COMP_ID_HBBTV)) != null && hbbtv.getStreamBoolean().booleanValue()) {
            MtkLog.d(TAG, "getNativeActiveCompId() = 33554434");
            return true;
        } else if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
            MtkLog.d(TAG, "StateDvrPlayback is runing, not handle");
            return false;
        } else if (this.mNavSundryImplement.isFreeze()) {
            this.sundryTextView.setText(R.string.nav_no_function);
            return true;
        } else if (TifTimeShiftManager.getInstance().isPaused()) {
            this.sundryTextView.setText(R.string.nav_no_function);
            return true;
        } else {
            if (ComponentsManager.getActiveCompId() == 33554434) {
                MtkLog.d(TAG, "isKeyHandler: hbbtv ui ctvitive");
                KeyDispatch.getInstance().passKeyToNative(keyCode, (KeyEvent) null);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
            if (!this.mIsComponetShow) {
                if (this.mNavSundryImplement.getCurrentAudioLang().length() == 0) {
                    MtkLog.d(TAG, "come in isKeyHandler 1");
                    this.sundryTextView.setText(R.string.nav_no_function);
                } else if (CommonIntegration.getInstance().isCurrentSourceBlocked()) {
                    MtkLog.d(TAG, "source block !");
                    this.sundryTextView.setText(R.string.nav_no_function);
                } else {
                    MtkLog.d(TAG, "come in isKeyHandler 2, text = " + this.mNavSundryImplement.getCurrentAudioLang());
                    this.sundryTextView.setText(this.mNavSundryImplement.getCurrentAudioLang());
                }
                if (this.ttsUtil != null) {
                    this.ttsUtil.speak(this.sundryTextView.getText().toString());
                }
            }
            if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                DvrManager.getInstance().restoreToDefault((StateBase) StateDvrFileList.getInstance());
            }
            return true;
        }
    }

    public boolean isCoExist(int componentID) {
        switch (componentID) {
            case NavBasic.NAV_COMP_ID_BANNER /*16777218*/:
            case NavBasic.NAV_COMP_ID_ZOOM_PAN /*16777223*/:
            case NavBasic.NAV_COMP_ID_POP /*16777235*/:
            case NavBasic.NAV_COMP_ID_PVR_TIMESHIFT /*16777241*/:
                return true;
            case NavBasic.NAV_COMP_ID_CEC /*16777220*/:
                if (10467 == this.lastPressKeyCode) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        boolean changeValueFlag;
        boolean isHandler = true;
        this.isSundryTime = false;
        if (this.mNavSundryImplement == null) {
            MtkLog.d(TAG, "KeyHandler mNavSundryImplement == null!!!");
            return false;
        }
        if (!this.mIsComponetShow || this.lastPressKeyCode != keyCode) {
            changeValueFlag = false;
        } else {
            changeValueFlag = true;
        }
        switch (keyCode) {
            case 4:
            case 85:
            case 89:
            case 90:
            case 130:
                MtkLog.d(TAG, "KeyMap.KEYCODE_BACK>>");
                setVisibility(8);
                if (this.mZoomTip != null) {
                    this.mZoomTip.setVisibility(8);
                }
                isHandler = false;
                break;
            case KeyMap.KEYCODE_MTKIR_MTKIR_CC /*175*/:
                this.isSleep = false;
                if (3 != MarketRegionInfo.getCurrentMarketRegion()) {
                    if (this.mNavSundryImplement.isFreeze()) {
                        handlerFreezeKey();
                    }
                    isHandler = false;
                    break;
                } else {
                    return true;
                }
            case 213:
                this.isSleep = false;
                if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
                    if (!this.mNavSundryImplement.isFreeze()) {
                        if (!TifTimeShiftManager.getInstance().isPaused()) {
                            if (ComponentsManager.getActiveCompId() == 33554434) {
                                MtkLog.d(TAG, "KeyHandler: hbbtv ui ctvitive");
                                KeyDispatch.getInstance().passKeyToNative(keyCode, event);
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                }
                            }
                            if (!CommonIntegration.getInstance().isCurrentSourceBlocked()) {
                                if (event.getRepeatCount() <= 0) {
                                    if (changeValueFlag) {
                                        this.mNavSundryImplement.setNextAudioLang();
                                    } else if (this.mNavSundryImplement.getCurrentAudioLang().length() == 0) {
                                        this.sundryTextView.setText(R.string.nav_no_function);
                                    } else {
                                        this.sundryTextView.setText(this.mNavSundryImplement.getCurrentAudioLang());
                                    }
                                    if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                                        DvrManager.getInstance().restoreToDefault((StateBase) StateDvrFileList.getInstance());
                                        break;
                                    }
                                }
                            } else {
                                MtkLog.d(TAG, "source block !!");
                                this.sundryTextView.setText(R.string.nav_no_function);
                                break;
                            }
                        } else {
                            this.sundryTextView.setText(R.string.nav_no_function);
                            break;
                        }
                    } else {
                        this.sundryTextView.setText(R.string.nav_no_function);
                        break;
                    }
                } else {
                    MtkLog.d(TAG, "StateDvrPlayback is runing, not handle");
                    return false;
                }
                break;
            case 215:
                this.isSleep = false;
                if (3 == MarketRegionInfo.getCurrentMarketRegion()) {
                    if (this.mNavSundryImplement.isFreeze()) {
                        handlerFreezeKey();
                    }
                    isHandler = false;
                    break;
                } else {
                    return true;
                }
            case 222:
                this.isSleep = false;
                if (1 != this.mSaveValue.readValue(MenuConfigManager.MODE_LIST_STYLE)) {
                    if (!this.mNavSundryImplement.isHeadphoneSetOn()) {
                        this.mContext.startActivity(new Intent("android.settings.SETTINGS").putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_SOUND_STYLE_SRC));
                        break;
                    } else {
                        this.sundryTextView.setText(R.string.nav_no_function);
                        break;
                    }
                } else {
                    isHandler = false;
                    setVisibility(8);
                    break;
                }
            case KeyMap.KEYCODE_MTKIR_SLEEP /*223*/:
                break;
            case MenuConfigManager.TKGS_LOC_OPERATE_ADD:
            case 10471:
                this.isSleep = false;
                if (1 != this.mSaveValue.readValue(MenuConfigManager.MODE_LIST_STYLE)) {
                    MtkLog.v(TAG, "ComponentsManager.getNativeActiveCompId():" + ComponentsManager.getNativeActiveCompId());
                    if (!this.mNavSundryImplement.getInternalScrnMode() && !this.mNavSundryImplement.isGingaWindowResize()) {
                        this.mIntegrationZoom.setZoomModeToNormalWithThread();
                        this.mContext.startActivity(new Intent("android.settings.SETTINGS").putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_DISPLAY_MODE_SRC));
                        break;
                    } else {
                        this.sundryTextView.setText(R.string.nav_no_function);
                        break;
                    }
                } else {
                    isHandler = false;
                    setVisibility(8);
                    break;
                }
            case 251:
                this.mContext.startActivity(new Intent("android.settings.SETTINGS").putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_PICTURE_STYLE_SRC));
                break;
            case 255:
                this.isSleep = false;
                MtkLog.d(TAG, " keyhandler KEYCODE_MTKIR_ZOOM changeValueFlag =" + changeValueFlag);
                if (!changeValueFlag) {
                    boolean isFreeze = this.mNavSundryImplement.isFreeze();
                    MtkLog.d(TAG, "keyhandler KEYCODE_MTKIR_ZOOM: isFreeze:" + isFreeze);
                    if (isFreeze) {
                        this.mNavSundryImplement.setFreeze(false);
                    }
                    isHandler = this.mIntegrationZoom.showCurrentZoom();
                    break;
                } else {
                    isHandler = this.mIntegrationZoom.nextZoom();
                    break;
                }
            case 10066:
                this.isSleep = false;
                if (changeValueFlag) {
                    setVisibility(8);
                    MtkLog.d(TAG, "come in keyHandler to dismiss");
                    this.navSundryHandler.removeCallbacks(this.updateTime);
                } else {
                    this.isSundryTime = true;
                    MtkLog.d(TAG, "come in keyHandler to show time");
                    this.sundryTextView.setText(this.mNavSundryImplement.getCurrentTime());
                    this.navSundryHandler.removeCallbacks(this.updateTime);
                    this.navSundryHandler.postDelayed(this.updateTime, 1000);
                    startTimeout(5000);
                }
                isHandler = true;
                break;
            case KeyMap.KEYCODE_MTKIR_FREEZE /*10467*/:
                this.isSleep = false;
                MtkLog.d(TAG, "KeyHandler, KEYCODE_MTKIR_FREEZE");
                if (MtkTvConfig.getInstance().getConfigValue("g_record__rec_tshift_mode") == 0) {
                    handlerFreezeKey();
                    break;
                } else {
                    setVisibility(8);
                    return false;
                }
            default:
                isHandler = false;
                break;
        }
        if (isHandler) {
            this.lastPressKeyCode = keyCode;
            startTimeout(5000);
        }
        Log.d(TAG, "return isHandler val :" + isHandler);
        return isHandler;
    }

    private void handlerFreezeKey() {
        if (this.mZoomTip != null) {
            this.mZoomTip.setVisibility(8);
        }
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            this.sundryTextView.setText(R.string.nav_no_function);
            MtkLog.d(TAG, "handlerFreezeKey(): is3rdTVSource:true");
            return;
        }
        boolean isFreeze = !this.mNavSundryImplement.isFreeze();
        MtkLog.d(TAG, "handlerFreezeKey(): isFreeze:" + isFreeze);
        int setValue = this.mNavSundryImplement.setFreeze(isFreeze);
        MtkLog.d(TAG, "handlerFreezeKey, isFreeze:" + isFreeze + ", setValue:" + setValue);
        if (setValue == -2) {
            this.sundryTextView.setText(R.string.nav_no_function);
            return;
        }
        String[] freezeModeName = this.mContext.getResources().getStringArray(R.array.nav_freeze_strings);
        if (isFreeze) {
            this.sundryTextView.setText(freezeModeName[0]);
        } else {
            this.sundryTextView.setText(freezeModeName[1]);
        }
    }

    public void adjustTime(String[] time, String value) {
        int sleep = this.mTime.getSleepTimer();
        if (sleep == 0) {
            this.sundryTextView.setText(time[0]);
            Intent intent = new Intent("com.mediatek.ui.menu.util.sleep.timer");
            intent.putExtra("itemId", "SETUP_sleep_timer");
            intent.putExtra("mills", 0);
            this.mContext.sendBroadcast(intent);
        } else {
            TextView textView = this.sundryTextView;
            textView.setText("Sleep:" + (sleep / 60) + " Minutes");
            Intent intent2 = new Intent("com.mediatek.ui.menu.util.sleep.timer");
            intent2.putExtra("itemId", "SETUP_sleep_timer");
            intent2.putExtra("mills", ((long) (sleep + -300)) * 1000);
            this.mContext.sendBroadcast(intent2);
        }
        MtkLog.d(TAG, "adjustTime, sleep=" + sleep);
    }

    public void adjustTime(String[] time) {
        int minutes = this.times / 60;
        MtkLog.d(TAG, "times=" + this.times + ", minutes=" + minutes);
        TextView textView = this.sundryTextView;
        textView.setText("Sleep:" + (minutes + 1) + " Minutes");
    }

    private void initSundryView() {
        addView(inflate(this.mContext, R.layout.nav_sundry_view, (ViewGroup) null), new LinearLayout.LayoutParams(-1, -1));
        this.sundryTextView = (TextView) findViewById(R.id.nav_sundry_textview_id);
        this.sundryTextView.setImportantForAccessibility(1);
    }

    public int getCurrentValueIndex(int[] currentArray, int value) {
        if (currentArray != null) {
            for (int i = 0; i < currentArray.length; i++) {
                if (value == currentArray[i]) {
                    return i;
                }
            }
        }
        return 0;
    }

    public boolean deinitView() {
        this.mTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_FEATURE_MSG, this.navSundryHandler);
        this.mTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_AV_MODE_MSG, this.navSundryHandler);
        this.mTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, this.navSundryHandler);
        return true;
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus statusID =" + statusID);
        if (statusID == 8) {
            if (this.mIntegrationZoom == null) {
                this.mIntegrationZoom = IntegrationZoom.getInstance((Context) null);
            }
            this.mIntegrationZoom.setZoomModeToNormalWithThread();
            this.times = this.mTime.getSleepTimerRemainingTime();
            if (this.times > 0) {
                do {
                } while (this.mTime.getSleepTimer() != 0);
            }
        } else if (statusID == 4) {
            new Thread(new Runnable() {
                public void run() {
                    if (SundryShowTextView.this.mNavSundryImplement.isFreeze()) {
                        SundryShowTextView.this.mNavSundryImplement.setFreeze(false);
                    }
                    if (SundryShowTextView.this.mIntegrationZoom == null) {
                        IntegrationZoom unused = SundryShowTextView.this.mIntegrationZoom = IntegrationZoom.getInstance((Context) null);
                    }
                    if (!SundryShowTextView.this.mIntegration.isPOPState() && SundryShowTextView.this.mIntegrationZoom.screenModeZoomShow() && 1 != SundryShowTextView.this.mIntegrationZoom.getCurrentZoom()) {
                        if (SundryShowTextView.this.mZoomTip != null && SundryShowTextView.this.mZoomTip.getVisibility() == 0) {
                            ((Activity) SundryShowTextView.this.mContext).runOnUiThread(new Runnable() {
                                public void run() {
                                    SundryShowTextView.this.mZoomTip.setVisibility(8);
                                }
                            });
                        }
                        SundryShowTextView.this.mIntegrationZoom.setZoomMode(1);
                    }
                }
            }).start();
        } else if (statusID == 10) {
            new Thread(new Runnable() {
                public void run() {
                    if (1 != SundryShowTextView.this.mIntegrationZoom.getCurrentZoom() && SundryShowTextView.this.mIntegrationZoom.screenModeZoomShow()) {
                        SundryShowTextView.this.mIntegrationZoom.setZoomMode(1);
                        if (!(SundryShowTextView.this.mZoomTip == null || SundryShowTextView.this.mContext == null)) {
                            ((Activity) SundryShowTextView.this.mContext).runOnUiThread(new Runnable() {
                                public void run() {
                                    SundryShowTextView.this.mZoomTip.setVisibility(8);
                                }
                            });
                        }
                    }
                    if (SundryShowTextView.this.mContext != null && SundryShowTextView.this.getVisibility() == 0) {
                        ((Activity) SundryShowTextView.this.mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                SundryShowTextView.this.setVisibility(8);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public void updateTrackChanged(int trackType, String trackId) {
        if (MarketRegionInfo.isFunctionSupport(20) && trackType == 0 && this.mIsComponetShow && 213 == this.lastPressKeyCode) {
            MtkLog.d(TAG, "come in updateTrackChanged, to update current AUDIO_TRACK");
            if (CommonIntegration.getInstance().isCurrentSourceBlocked()) {
                MtkLog.d(TAG, "source block !!");
                this.sundryTextView.setText(R.string.nav_no_function);
            } else {
                this.sundryTextView.setText(this.mNavSundryImplement.getCurrentAudioLang());
            }
            if (this.ttsUtil != null) {
                MtkLog.d(TAG, "tts is on=== ");
                this.sundryTextView.setFocusable(true);
                this.sundryTextView.requestFocus();
                this.ttsUtil.speak(this.sundryTextView.getText().toString());
            }
        }
    }
}
