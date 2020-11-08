package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.support.v4.media.MediaPlayer2;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.dm.DMNativeDaemonConnector;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import java.lang.ref.WeakReference;

public class VgaPowerManager extends NavBasicMisc implements ComponentStatusListener.ICStatusListener {
    protected static final String FIRST_POWER_ON = "first_power_on";
    private static final int GO_VGA_POWER_OFF = 2;
    private static final int GO_VGA_POWER_OFF_DIALOG = 1;
    public static final String KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF = "no_signal_auto_power_off";
    private static final String TAG = "VgaPowerManager";
    /* access modifiers changed from: private */
    public static ConfirmDialog mConfirmDialog = null;
    private static final String mSourceName = "VGA";
    private final Uri BASE_URI = Settings.Global.CONTENT_URI;
    /* access modifiers changed from: private */
    public InternalHandler handler;
    private boolean isLocked = true;
    /* access modifiers changed from: private */
    public boolean isSignalLoss = false;
    /* access modifiers changed from: private */
    public ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final IDreamManager mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));

    public VgaPowerManager(Context context) {
        super(context);
        SystemProperties.set("vendor.mtk.svctx.stoped", "1");
        this.componentID = NavBasic.NAV_COMP_ID_POWER_OFF;
        this.handler = new InternalHandler(this, context.getMainLooper());
        mConfirmDialog = new ConfirmDialog(this.mContext, R.layout.nav_ib_view, this.mContext.getString(R.string.vga_no_sinal_info));
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(2, this);
        ComponentStatusListener.getInstance().addListener(18, this);
        ComponentStatusListener.getInstance().addListener(17, this);
        this.mContentResolver = context.getContentResolver();
        this.mContentResolver.registerContentObserver(this.BASE_URI.buildUpon().appendPath("no_signal_auto_power_off").build(), true, new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                MtkLog.d(VgaPowerManager.TAG, "onChange isSignalLoss:" + VgaPowerManager.this.isSignalLoss);
                VgaPowerManager.this.sendMessage(VgaPowerManager.this.isSignalLoss);
                if (VgaPowerManager.this.isSignalLoss) {
                    VgaPowerManager.this.handler.post(new Runnable() {
                        public void run() {
                            if (VgaPowerManager.this.mContext == null || ((TurnkeyUiMainActivity) VgaPowerManager.this.mContext) == null || ((TurnkeyUiMainActivity) VgaPowerManager.this.mContext).getWindow() == null) {
                                MtkLog.d(VgaPowerManager.TAG, "can't get turnkey window.");
                            } else if (Settings.Global.getInt(VgaPowerManager.this.mContentResolver, "no_signal_auto_power_off", 0) == 0) {
                                MtkLog.d(VgaPowerManager.TAG, "clearFlags FLAG_KEEP_SCREEN_ON");
                                ((TurnkeyUiMainActivity) VgaPowerManager.this.mContext).getWindow().clearFlags(128);
                            } else {
                                MtkLog.d(VgaPowerManager.TAG, "addFlags FLAG_KEEP_SCREEN_ON");
                                ((TurnkeyUiMainActivity) VgaPowerManager.this.mContext).getWindow().addFlags(128);
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean isKeyHandler(int keyCode) {
        if (!this.isSignalLoss) {
            return false;
        }
        this.handler.removeMessages(1);
        sendMessage(true);
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        if (this.handler == null) {
            return false;
        }
        this.handler.removeMessages(2);
        this.handler.removeMessages(1);
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
        }
        setVisibility(4);
        return false;
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0) {
            if (mConfirmDialog != null && !mConfirmDialog.isShowing()) {
                mConfirmDialog.show();
            }
        } else if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
            mConfirmDialog.dismiss();
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__dpms") == 0 && TvSingletons.getSingletons().getCommonIntegration().isCurrentSourceVGA()) {
            MtkLog.d(TAG, "updateComponentStatus, dpms disabled~");
        } else if (InputSourceManager.getInstance().isBlock(mSourceName) && this.isLocked) {
            MtkLog.d(TAG, "updateComponentStatus, VGA blocked~");
        } else if (statusID == 2) {
            if (16777232 == value && this.isSignalLoss) {
                MtkLog.d(TAG, "updateComponentStatus, statusID=" + statusID);
                sendMessage(true);
            }
        } else if (statusID == 1) {
            if (this.isSignalLoss && !ComponentsManager.getInstance().isComponentsShow() && !isDreaming()) {
                MtkLog.d(TAG, "updateComponentStatus, statusID=" + statusID);
                sendMessage(true);
            }
        } else if (statusID == 18) {
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    if (SaveValue.getInstance(VgaPowerManager.this.mContext).readBooleanValue(VgaPowerManager.FIRST_POWER_ON)) {
                        int value = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_VIDEO_VID_ENABLE_SGNL_WAKEUP);
                        if ((value & 1) != 0 && VgaPowerManager.isOnDPMS()) {
                            MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_VIDEO_VID_ENABLE_SGNL_WAKEUP, (~1) & value);
                            MtkTvUtil.getInstance();
                            int value2 = MtkTvUtil.getPclWakeupSetup();
                            MtkLog.d(VgaPowerManager.TAG, "reset wakeup flag");
                            MtkTvUtil.getInstance();
                            MtkTvUtil.setPclWakeupSetup((~1) & value2);
                            return;
                        }
                        return;
                    }
                    SaveValue.getInstance(VgaPowerManager.this.mContext).saveBooleanValue(VgaPowerManager.FIRST_POWER_ON, true);
                }
            });
        } else if (statusID == 17) {
            this.handler.removeMessages(2);
            this.handler.removeMessages(1);
        }
    }

    public void handlerMessage(int type) {
        MtkLog.d(TAG, "handlerMessage, type=" + type);
        if (type == 13) {
            Log.d(TAG, "svctx notify stoped, set property 1");
            SystemProperties.set("vendor.mtk.svctx.stoped", "1");
        } else if (type == 4 || type == 5) {
            SystemProperties.set("vendor.mtk.svctx.stoped", "0");
        }
        if (type == 5 || type == 10) {
            this.isSignalLoss = true;
            sendMessage(this.isSignalLoss);
        } else if (type == 12) {
            if (InputSourceManager.getInstance().isBlock(mSourceName)) {
                this.isLocked = false;
            }
        } else if (type == 9 || type == 11) {
            this.isLocked = true;
        } else if (type == 0 || type == 20 || type == 21 || type == 37 || type == 38) {
            this.isSignalLoss = false;
            sendMessage(this.isSignalLoss);
        }
    }

    public void handleSourceKey() {
        MtkLog.i(TAG, "+++++ handleSourceKey +++++");
        if (this.handler != null) {
            this.handler.removeMessages(2);
            this.handler.removeMessages(1);
            if (mConfirmDialog != null && mConfirmDialog.isVisible()) {
                mConfirmDialog.dismiss();
            }
            setVisibility(4);
        }
    }

    /* access modifiers changed from: private */
    public void sendMessage(boolean bStart) {
        if (!bStart) {
            if (this.handler != null) {
                MtkLog.d(TAG, "handlerMessage, clean message");
                this.handler.removeMessages(2);
                this.handler.removeMessages(1);
            }
            if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
                MtkLog.d(TAG, "sendMessage false == setVisibility(View.INVISIBLE)");
                setVisibility(4);
            }
        } else if (MtkTvConfig.getInstance().getConfigValue("g_misc__dpms") == 0 && TvSingletons.getSingletons().getCommonIntegration().isCurrentSourceVGA()) {
            MtkLog.d(TAG, "updateComponentStatus, dpms disabled~");
        } else if (InputSourceManager.getInstance().isBlock(mSourceName) && this.isLocked) {
            MtkLog.d(TAG, "updateComponentStatus, VGA blocked~");
        } else if (CommonIntegration.getInstance().isPipOrPopState()) {
            MtkLog.d(TAG, "updateComponentStatus, in pip/pop state~");
        } else if (MtkTvScan.getInstance().isScanning()) {
            MtkLog.d(TAG, "enablePowerOff isScanning~");
        } else if (!CommonIntegration.getInstance().isCurrentSourceTv() || CommonIntegration.getInstance().getChannelAllNumByAPI() > 0) {
            MtkLog.d(TAG, "handlerMessage, GO_VGA_POWER_OFF_DIALOG");
            int value = Settings.Global.getInt(this.mContentResolver, "no_signal_auto_power_off", 0);
            MtkLog.d(TAG, "KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF:" + value);
            int delaytime = 0;
            switch (value) {
                case 1:
                    delaytime = 300;
                    break;
                case 2:
                    delaytime = DMNativeDaemonConnector.ResponseCode.UnsolicitedInformational;
                    break;
                case 3:
                    delaytime = MediaPlayer2.MEDIA_INFO_TIMED_TEXT_ERROR;
                    break;
                case 4:
                    delaytime = 1800;
                    break;
                case 5:
                    delaytime = MtkTvTimeFormatBase.SECONDS_PER_HOUR;
                    break;
            }
            this.handler.removeMessages(1);
            if (value != 0) {
                this.handler.sendEmptyMessageDelayed(1, (long) (delaytime * 1000));
            }
        } else {
            MtkLog.d(TAG, "enablePowerOff, please scan channel!");
        }
    }

    private class ConfirmDialog extends NavBasicDialog {
        private TextView mInfo;
        String mInfoTip = "";

        public ConfirmDialog(Context context, int theme, String info) {
            super(context, R.style.dialog);
            setContentView(theme);
            this.mInfoTip = info;
        }

        public boolean initView() {
            super.initView();
            ((ImageView) findViewById(R.id.ib_image_icon)).setVisibility(8);
            this.mInfo = (TextView) findViewById(R.id.ib_text);
            this.mInfo.setText(this.mInfoTip);
            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (450 * ScreenConstant.SCREEN_WIDTH) / 1280;
            lp.height = (180 * ScreenConstant.SCREEN_HEIGHT) / 720;
            window.setAttributes(lp);
            return true;
        }

        public void show() {
            VgaPowerManager.this.handler.sendEmptyMessageDelayed(2, MessageType.delayMillis4);
            super.show();
        }

        public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
            VgaPowerManager.this.handler.removeMessages(2);
            VgaPowerManager.this.handler.removeMessages(1);
            VgaPowerManager.this.setVisibility(4);
            if (keyCode == 4) {
                ComponentsManager.getInstance().showNavComponent(16777232);
                return true;
            } else if (TurnkeyUiMainActivity.getInstance() != null) {
                return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode, event);
            } else {
                return false;
            }
        }
    }

    private static class InternalHandler extends Handler {
        /* access modifiers changed from: private */
        public final WeakReference<VgaPowerManager> mPowerManager;

        public InternalHandler(VgaPowerManager manager, Looper L) {
            super(L);
            this.mPowerManager = new WeakReference<>(manager);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(VgaPowerManager.TAG, "[InternalHandler] handlerMessage occur~");
            if (this.mPowerManager.get() == null || CommonIntegration.getInstance().is3rdTVSource()) {
                return;
            }
            if (1 == msg.what) {
                if (CommonIntegration.getInstance().isPipOrPopState()) {
                    MtkLog.d(VgaPowerManager.TAG, "updateComponentStatus, in pip/pop state~");
                } else if (MtkTvScan.getInstance().isScanning()) {
                    MtkLog.d(VgaPowerManager.TAG, " isScanning~ disable power off!");
                } else {
                    String currentInputSourceName = MtkTvInputSource.getInstance().getCurrentInputSourceName();
                    boolean isTurnkey = false;
                    try {
                        isTurnkey = ((ActivityManager) ((VgaPowerManager) this.mPowerManager.get()).mContext.getSystemService("activity")).getRunningTasks(1).get(0).topActivity.getShortClassName().contains("TurnkeyUiMainActivity");
                    } catch (Exception e) {
                    }
                    boolean isTurnkeyActive = SaveValue.getInstance(((VgaPowerManager) this.mPowerManager.get()).mContext).readBooleanValue(TurnkeyUiMainActivity.TURNKEY_ACTIVE_STATE);
                    MtkLog.d(VgaPowerManager.TAG, "current activity is TurnkeyUiMainActivity ? " + isTurnkey + " isTurnkeyActive:" + isTurnkeyActive);
                    if (isTurnkey || isTurnkeyActive) {
                        try {
                            if (((VgaPowerManager) this.mPowerManager.get()).isDreaming()) {
                                MtkLog.d("thewyp", "awaken");
                                ((VgaPowerManager) this.mPowerManager.get()).mDreamManager.awaken();
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        MtkLog.d("thewyp", "show dialog");
                        ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_POWER_OFF);
                    }
                }
            } else if (2 == msg.what) {
                ComponentsManager.getInstance().hideAllComponents();
                if (VgaPowerManager.mConfirmDialog != null && VgaPowerManager.mConfirmDialog.isShowing()) {
                    VgaPowerManager.mConfirmDialog.dismiss();
                    MtkLog.d(VgaPowerManager.TAG, "come in GO_VGA_POWER_OFF msg to dismiss dialog");
                }
                MtkLog.d(VgaPowerManager.TAG, "[InternalHandler] GO_VGA_POWER_OFF");
                TVAsyncExecutor.getInstance().execute(new Runnable() {
                    public void run() {
                        if (VgaPowerManager.isOnDPMS()) {
                            MtkTvUtil.getInstance();
                            int value = MtkTvUtil.getPclWakeupSetup();
                            MtkTvUtil.getInstance();
                            MtkTvUtil.setPclWakeupSetup(value | 1);
                            MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_VIDEO_VID_ENABLE_SGNL_WAKEUP, MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_VIDEO_VID_ENABLE_SGNL_WAKEUP) | 1);
                        }
                        ((PowerManager) ((VgaPowerManager) InternalHandler.this.mPowerManager.get()).mContext.getSystemService("power")).goToSleep(SystemClock.uptimeMillis());
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean isOnDPMS() {
        boolean z = true;
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__dpms") != 1) {
            z = false;
        }
        boolean isOnDPMS = z;
        MtkLog.d(TAG, "isOnDPMS=" + isOnDPMS);
        return isOnDPMS;
    }

    /* access modifiers changed from: private */
    public boolean isDreaming() {
        boolean z = false;
        boolean result = false;
        try {
            if (this.mDreamManager != null && this.mDreamManager.isDreaming()) {
                z = true;
            }
            result = z;
        } catch (Exception e) {
            e.printStackTrace();
        }
        MtkLog.d(TAG, "isDreaming:" + result);
        return result;
    }
}
