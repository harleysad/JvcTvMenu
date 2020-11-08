package com.mediatek.wwtv.tvcenter.nav.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvScreenSaverBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.controller.UImanager;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import java.util.Locale;

public class TwinkleView extends NavBasicView implements ComponentStatusListener.ICStatusListener {
    public static final int MSG_STR_ID_AUDIO_PROG = 9;
    public static final int MSG_STR_ID_EMPTY = 0;
    public static final int MSG_STR_ID_GETTING_DATA = 3;
    public static final int MSG_STR_ID_HD_VIDEO_NOT_SUPPORT = 17;
    public static final int MSG_STR_ID_HIDDEN_CH = 8;
    private static final int MSG_STR_ID_LAST_VALID_ENTRY = 255;
    public static final int MSG_STR_ID_LOCKED_CH = 4;
    public static final int MSG_STR_ID_LOCKED_INP = 6;
    public static final int MSG_STR_ID_LOCKED_PROG = 5;
    public static final int MSG_STR_ID_NON_BRDCSTING = 18;
    public static final int MSG_STR_ID_NO_AUDIO_STRM = 13;
    public static final int MSG_STR_ID_NO_AUDIO_VIDEO = 10;
    public static final int MSG_STR_ID_NO_CH_DTIL = 12;
    public static final int MSG_STR_ID_NO_CH_IN_LIST = 19;
    public static final int MSG_STR_ID_NO_EVN_DTIL = 11;
    public static final int MSG_STR_ID_NO_EVN_TILE = 7;
    public static final int MSG_STR_ID_NO_SIGNAL = 1;
    public static final int MSG_STR_ID_NO_VIDEO_STRM = 14;
    public static final int MSG_STR_ID_PLS_WAIT = 15;
    public static final int MSG_STR_ID_SCAN_CH = 2;
    public static final int MSG_STR_ID_SCRAMBLED = 21;
    public static final int MSG_STR_ID_TTX_SBTI_X_RATED_BLOCKED = 20;
    public static final int MSG_STR_ID_VIDEO_NOT_SUPPORT = 16;
    private static final int NAV_TIMEOUT_SHOW_TWINKLE = 0;
    private static final int SHOW_TWINKLE = 100;
    private static final String TAG = "TwinkleView";
    public static final BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                MtkLog.d(TwinkleView.TAG, "screen on success");
            }
        }
    };
    private ComponentsManager comManager;
    /* access modifiers changed from: private */
    public CommonIntegration commonIntegration;
    private Handler handler;
    private InputSourceManager inputSourceManager;
    /* access modifiers changed from: private */
    public int lastIndex = 0;
    private Bitmap mBmp;
    private TvCallbackHandler mCallbackHandler;
    private Canvas mCanvas;
    private final int mCanvasOff = 6;
    private final int mCanvasX = 0;
    private int mCanvasY = 50;
    private final int mFontSize = 60;
    private Paint mPaint;
    private Rect mRect;
    /* access modifiers changed from: private */
    public MtkTvScreenSaverBase mScreenSaver;
    private String[] mTwinkleArray;
    private SnowTextView mView;
    private int msg_id = 0;
    /* access modifiers changed from: private */
    public boolean screenOnFlag = false;
    private boolean videoShowFlag = false;

    @SuppressLint({"NewApi"})
    public void init() {
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.twinke_view, this);
        this.mView = (SnowTextView) findViewById(R.id.tw_view);
        this.componentID = 16777232;
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(2, this);
        ComponentStatusListener.getInstance().addListener(3, this);
        this.comManager = ComponentsManager.getInstance();
        this.handler = new MyHandler();
        this.mTwinkleArray = this.mContext.getResources().getStringArray(R.array.nav_twinkle_strings);
        this.mCanvas = new Canvas();
        this.mRect = new Rect();
        this.mPaint = new Paint();
        this.mScreenSaver = new MtkTvScreenSaverBase();
        this.mPaint.setTextLocale(Locale.ENGLISH);
        this.mPaint.setTextSize(60.0f);
        this.mPaint.setTypeface(Typeface.SANS_SERIF);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-1118482);
        this.commonIntegration = CommonIntegration.getInstance();
        this.inputSourceManager = InputSourceManager.getInstance();
    }

    public TwinkleView(Context context) {
        super(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        context.registerReceiver(screenOnReceiver, intentFilter);
        init();
    }

    public TwinkleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        context.registerReceiver(screenOnReceiver, intentFilter);
        init();
    }

    public TwinkleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        context.registerReceiver(screenOnReceiver, intentFilter);
        init();
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777241 && StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
            return true;
        }
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        if (keyCode != 85 || !this.inputSourceManager.isBlock("TV")) {
            return super.KeyHandler(keyCode, event, fromNative);
        }
        return true;
    }

    public void setVisibility(int visibility) {
        this.commonIntegration.is3rdTVSource();
        super.setVisibility(visibility);
        this.mView.setVisibility(visibility);
    }

    public void handlerHbbtvMessage(int type, int message) {
        MtkLog.d(TAG, "handlerHbbtvMessage, type=" + type + ", message=" + message);
        switch (type) {
            case 1:
            case 3:
                MtkLog.d(TAG, "handlerHbbtvMessage, hbbtv is showing");
                setVisibility(8);
                return;
            case 2:
            case 4:
                MtkLog.d(TAG, "handlerHbbtvMessage, hbbtv is hiding");
                handleCallBack(this.lastIndex);
                return;
            default:
                return;
        }
    }

    public void handleCallBack(int type) {
        MtkLog.d(TAG, " handleCallBack MSG_CB_SCREEN_SAVER_MSG type =" + type + ",lastIndex==" + this.lastIndex);
        this.lastIndex = type;
        if (this.lastIndex != 3) {
            showHandler();
        }
        if (this.lastIndex == 3 && !this.videoShowFlag) {
            postDelayed(new Runnable() {
                public void run() {
                    MtkLog.d(TwinkleView.TAG, "delay to show retrieving data!");
                    TwinkleView.this.showHandler();
                }
            }, 300);
        }
        if (this.lastIndex == 3 && this.videoShowFlag) {
            MtkLog.d(TAG, "videoShowFlag==true");
            this.videoShowFlag = false;
        } else if (this.mContext != null && DestroyApp.isCurActivityTkuiMainActivity() && DestroyApp.isCurActivityTkuiMainActivity()) {
            for (Integer ID : ComponentsManager.getInstance().getCurrentActiveComps()) {
                if (ID.intValue() == 16777232) {
                    return;
                }
            }
        }
    }

    public void handlerVideoCallback(int type) {
        if (type == 37 || 20 == type) {
            MtkLog.d(TAG, "show video success");
            if (getVisibility() == 0) {
                setVisibility(8);
            }
            this.videoShowFlag = true;
        }
    }

    private class MyHandler extends Handler {
        private MyHandler() {
        }

        public void handleMessage(Message msg) {
            MtkLog.d(TwinkleView.TAG, "handleMessage msg =" + msg + "TvCallbackConst.MSG_CB_SCREEN_SAVER_MSG =" + TvCallbackConst.MSG_CB_SCREEN_SAVER_MSG + "msg.what =" + msg.what);
            if (TwinkleView.this.mContext != null && msg.what == 100) {
                MtkLog.d(TwinkleView.TAG, "SHOW_TWINKLE");
                if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning() && TwinkleView.this.lastIndex != 9) {
                    MtkLog.d(TwinkleView.TAG, "showTwinkle||Dvr is Running,return !");
                } else if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                    MtkLog.d(TwinkleView.TAG, "showTwinkle||Dvr dialog is showing,return !");
                } else if (TwinkleView.this.commonIntegration.is3rdTVSource()) {
                    MtkLog.w(TwinkleView.TAG, "current source is is3rdTVSource!");
                    boolean isBlockFor3rd = ((PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG)).isContentBlock(true);
                    MtkLog.d(TwinkleView.TAG, "SHOW_TWINKLE||isBlockFor3rd =" + isBlockFor3rd + "||SourceBlocked =" + TwinkleView.this.commonIntegration.isCurrentSourceBlocked());
                    if (!isBlockFor3rd || !TwinkleView.this.commonIntegration.isCurrentSourceBlocked() || ((TwinkleView.this.isComponentsShow() && TwinkleView.this.getVisibility() != 0) || !DestroyApp.isCurActivityTkuiMainActivity())) {
                        MtkLog.d(TwinkleView.TAG, "SHOW_TWINKLE|not blocked");
                        TwinkleView.this.setVisibility(8);
                        return;
                    }
                    MtkLog.d(TwinkleView.TAG, "SHOW_TWINKLE||3rd Source Blocked");
                    TwinkleView.this.showSpecialView(6);
                } else {
                    MtkLog.d(TwinkleView.TAG, "getVisibility()==" + TwinkleView.this.getVisibility());
                    synchronized (TwinkleView.class) {
                        if ((!TwinkleView.this.isComponentsShow() || TwinkleView.this.getVisibility() == 0) && !TwinkleView.this.isUImanagerShowing() && DestroyApp.isCurActivityTkuiMainActivity()) {
                            ComponentsManager.getInstance();
                            if (33554435 != ComponentsManager.getNativeActiveCompId() && !TwinkleView.this.isCommonDialogShowing()) {
                                if (MtkTvPWDDialog.getInstance().PWDShow() != 0) {
                                    if (!TwinkleView.this.screenOnFlag) {
                                        MtkLog.d(TwinkleView.TAG, "SHOW_TWINKLE||Index =" + TwinkleView.this.lastIndex);
                                        TwinkleView.this.showSpecialView(TwinkleView.this.lastIndex);
                                    }
                                }
                                MtkLog.d(TwinkleView.TAG, "SHOW_TWINKLE||getScrnSvrMsgID =" + TwinkleView.this.mScreenSaver.getScrnSvrMsgID());
                                TwinkleView.this.showSpecialView(TwinkleView.this.mScreenSaver.getScrnSvrMsgID());
                                boolean unused = TwinkleView.this.screenOnFlag = false;
                            }
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isUImanagerShowing() {
        if (UImanager.showing) {
            MtkLog.d(TAG, "isUImanagerShowing true");
            return true;
        }
        MtkLog.d(TAG, "isUImanagerShowing false");
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isComponentsShow() {
        MtkLog.d(TAG, "isComponentsShow");
        boolean coexitComp = true;
        for (Integer ID : ComponentsManager.getInstance().getCurrentActiveComps()) {
            if (ID.intValue() == 16777241) {
                coexitComp = false;
                ComponentsManager.updateActiveCompId(false, ID.intValue());
            }
        }
        if (!coexitComp && StateDvr.getInstance() != null && StateDvr.getInstance().isSmallCtrlBarShow() && (StateDvrFileList.getInstance() == null || (StateDvrFileList.getInstance() != null && !StateDvrFileList.getInstance().isShowing()))) {
            MtkLog.d(TAG, "isComponentsShow false 2");
            return false;
        } else if (!ComponentsManager.getInstance().isComponentsShow()) {
            MtkLog.d(TAG, "isComponentsShow false 1");
            return false;
        } else if (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning()) {
            MtkLog.d(TAG, "isComponentsShow true");
            return true;
        } else {
            MtkLog.d(TAG, "isComponentsShow false 3");
            return false;
        }
    }

    private boolean isCIComponentShow() {
        MtkLog.d(TAG, "isCIComponentShow");
        boolean coexitComp = true;
        for (Integer ID : ComponentsManager.getInstance().getCurrentActiveComps()) {
            if (ID.intValue() == 16777241) {
                coexitComp = false;
                ComponentsManager.updateActiveCompId(false, ID.intValue());
            }
        }
        if (!coexitComp && StateDvr.getInstance() != null && StateDvr.getInstance().isSmallCtrlBarShow() && (StateDvrFileList.getInstance() == null || (StateDvrFileList.getInstance() != null && !StateDvrFileList.getInstance().isShowing()))) {
            MtkLog.d(TAG, "isCIComponentShow false 2");
            return false;
        } else if (!ComponentsManager.getInstance().isComponentsShow()) {
            MtkLog.d(TAG, "isCIComponentShow false 1");
            return false;
        } else if (ComponentsManager.getActiveCompId() == 16777245) {
            MtkLog.d(TAG, "isCIComponentShow true");
            return true;
        } else {
            MtkLog.d(TAG, "isCIComponentShow false");
            return false;
        }
    }

    private boolean isPvrOrDvrShow() {
        boolean z = false;
        if (!MarketRegionInfo.isFunctionSupport(30)) {
            return false;
        }
        if (DvrManager.getInstance().isPvrDialogShow || (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording())) {
            z = true;
        }
        boolean isDvrManagerPvrDialogShow = z;
        MtkLog.d(TAG, "isPvrOrDvrShow isDvrManagerPvrDialogShow==" + isDvrManagerPvrDialogShow);
        return isDvrManagerPvrDialogShow;
    }

    /* access modifiers changed from: private */
    public boolean isCommonDialogShowing() {
        TurnkeyUiMainActivity activity = (TurnkeyUiMainActivity) this.mContext;
        if (activity.getConfirmDialog() != null) {
            return activity.getConfirmDialog().isConfirmDialogShowing();
        }
        return false;
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "twinkle||updateComponentStatus||statusID =" + statusID);
        if (statusID != 18) {
            switch (statusID) {
                case 1:
                    MtkLog.d(TAG, "statusHIDE value =" + value);
                    if (value == 16777232) {
                        MtkLog.d(TAG, "twinkle hide itself !!!");
                        return;
                    } else if (this.handler != null) {
                        if (this.handler.hasMessages(100)) {
                            this.handler.removeMessages(100);
                        }
                        MtkLog.d(TAG, "NAV_COMPONENT_HIDE NAV_COMP_ID_PWD_DLG");
                        this.handler.sendEmptyMessage(100);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    MtkLog.d(TAG, "NAV_COMPONENT_SHOW");
                    ComponentsManager.getInstance();
                    if (ComponentsManager.getActiveCompId() != 16777232) {
                        MtkLog.d(TAG, "NAV_COMPONENT_SHOW dismiss twinkle");
                        if (getVisibility() == 0) {
                            setVisibility(8);
                            return;
                        }
                        return;
                    }
                    MtkLog.d(TAG, "twinkle show itself !");
                    return;
                case 3:
                    if (!isComponentsShow()) {
                        ComponentsManager.getInstance();
                        if (ComponentsManager.getActiveCompId() != 16777232 && this.handler != null) {
                            if (this.handler.hasMessages(100)) {
                                this.handler.removeMessages(100);
                            }
                            MtkLog.d(TAG, "updateComponentStatus sendEmptyMessageDelayed SHOW_TWINKLE");
                            this.handler.sendEmptyMessageDelayed(100, 0);
                            return;
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else {
            synchronized (TwinkleView.class) {
                this.screenOnFlag = true;
            }
        }
    }

    public void showOutside() {
        MtkLog.d(TAG, "showOutside||getScrnSvrMsgID =" + this.mScreenSaver.getScrnSvrMsgID());
        showSpecialView(this.mScreenSaver.getScrnSvrMsgID());
    }

    public void showHandler() {
        MtkLog.d(TAG, "showHandler");
        if (this.handler != null) {
            if (this.handler.hasMessages(100)) {
                this.handler.removeMessages(100);
            }
            this.handler.sendEmptyMessage(100);
        }
    }

    /* access modifiers changed from: private */
    public void showSpecialView(int index) {
        MtkLog.d(TAG, "showSpecialView||lastIndex =" + index);
        if (index != 0 && index != 255) {
            this.mTwinkleArray = this.mContext.getResources().getStringArray(R.array.nav_twinkle_strings);
            if (index >= 0 && index < this.mTwinkleArray.length) {
                this.mPaint.getTextBounds(this.mTwinkleArray[index], 0, this.mTwinkleArray[index].length(), this.mRect);
                MtkLog.d(TAG, "rectWidth =" + this.mRect.width() + "||rectHeight =" + this.mRect.height());
                this.mBmp = Bitmap.createBitmap(this.mRect.width() + 6, this.mRect.height() + 6, Bitmap.Config.ARGB_8888);
                this.mCanvas.setBitmap(this.mBmp);
                this.mCanvasY = this.mRect.height() > 60 ? 55 : 50;
                this.mCanvas.drawText(this.mTwinkleArray[index], 0.0f, (float) this.mCanvasY, this.mPaint);
            }
            this.mView.setWidth(this.mRect.width() + 6);
            this.mView.setHeight(this.mRect.height() + 6);
            this.mView.setBitmap(this.mBmp);
            this.comManager.showNavComponent(16777232);
        } else if (getVisibility() == 0) {
            MtkLog.d(TAG, "showSpecialView||----GONE----");
            setVisibility(8);
        }
    }
}
