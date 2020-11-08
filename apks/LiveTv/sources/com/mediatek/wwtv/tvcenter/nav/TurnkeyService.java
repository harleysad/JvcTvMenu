package com.mediatek.wwtv.tvcenter.nav;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import com.mediatek.wwtv.setting.widget.view.BlackView;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrConfirmDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.oad.NavOADActivity;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class TurnkeyService extends Service {
    public static final String SCAN_VIEW_ACTION = "com.mtk.messageFromScanView";
    public static final String SCAN_VIEW_LIFECIRLE = "lifecircle";
    private final String TAG = "TurnkeyService";
    /* access modifiers changed from: private */
    public boolean isFromScanView = false;
    private BlackView mBlackView;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Intent mIntent;
    private BroadcastReceiver mReciver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Context unused = TurnkeyService.this.mContext = context;
            Intent unused2 = TurnkeyService.this.mIntent = intent;
            new Handler(Looper.getMainLooper()).post(TurnkeyService.this.runnable);
        }
    };
    /* access modifiers changed from: private */
    public final Runnable runnable = new Runnable() {
        public void run() {
            MtkLog.d("TurnkeyService", "onReceive||intentAction =" + TurnkeyService.this.mIntent.getAction());
            if (TurnkeyService.SCAN_VIEW_ACTION.equals(TurnkeyService.this.mIntent.getAction())) {
                boolean unused = TurnkeyService.this.isFromScanView = TurnkeyService.this.mIntent.getBooleanExtra(TurnkeyService.SCAN_VIEW_LIFECIRLE, false);
                return;
            }
            TurnkeyService.this.handlerBroadcastOADMessage(TurnkeyService.this.mContext, TurnkeyService.this.mIntent);
            TurnkeyService.this.handlerBroadcastDVRMessage();
            TurnkeyService.this.handlerBroadcastTurnkeyMessage(TurnkeyService.this.mContext, TurnkeyService.this.mIntent);
            if ("android.intent.action.SCREEN_OFF".equals(TurnkeyService.this.mIntent.getAction()) && TurnkeyService.this.isFromScanView) {
                boolean unused2 = TurnkeyService.this.isFromScanView = false;
                TurnkeyUiMainActivity.resumeTurnkeyActivity(TurnkeyService.this.mContext);
            }
        }
    };

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onCreate() {
        super.onCreate();
        this.mBlackView = new BlackView(getApplicationContext());
        ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel("TurnkeyService", "TurnkeyService", 0));
        startForeground(100, new Notification.Builder(this, "TurnkeyService").getNotification());
        MtkLog.d("TurnkeyService", "oncreate");
        initPowerBroadcast();
        initAutoSleepValue();
    }

    public void onDestroy() {
        super.onDestroy();
        MtkLog.d("TurnkeyService", "ondestroy");
        unregisterReceiver(this.mReciver);
    }

    public void onLowMemory() {
        super.onLowMemory();
        MtkLog.d("TurnkeyService", "onLowMemory");
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        MtkLog.d("TurnkeyService", "onRebind");
    }

    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MtkLog.d("TurnkeyService", "onStartCommand");
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getBundleExtra("SCHEDULE");
        if (bundle != null) {
            DvrConfirmDialog mDialog = new DvrConfirmDialog(this, bundle.getString("PROMPT"), bundle.getString("TITLE"), bundle.getInt("CONFIRM_TYPE"));
            mDialog.getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
            mDialog.show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public boolean onUnbind(Intent intent) {
        MtkLog.d("TurnkeyService", "onUnbind");
        return super.onUnbind(intent);
    }

    public IBinder onBind(Intent arg0) {
        MtkLog.d("TurnkeyService", "onBind");
        return null;
    }

    private void initPowerBroadcast() {
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("android.intent.action.SCREEN_OFF");
        intentFilter1.addAction("android.intent.action.ACTION_SHUTDOWN");
        intentFilter1.addAction("android.intent.action.SCREEN_ON");
        intentFilter1.addAction(SCAN_VIEW_ACTION);
        registerReceiver(this.mReciver, intentFilter1);
    }

    private void initAutoSleepValue() {
        try {
            int a = Settings.Secure.getInt(getContentResolver(), "attentive_timeout", -2);
            Log.d("TurnkeyService", "" + a);
            int i = -1;
            int def = DataSeparaterUtil.getInstance() != null ? DataSeparaterUtil.getInstance().getValueAutoSleep() : -1;
            Log.d("TurnkeyService", "def==" + def);
            if (a == -2 && def != -1) {
                if (def != 2) {
                    ContentResolver contentResolver = getContentResolver();
                    if (def != 1) {
                        if (def != 0) {
                            i = ((int) (((long) def) * 3600000)) - 300000;
                        }
                    }
                    Settings.Secure.putInt(contentResolver, "attentive_timeout", i);
                } else if (def == -1) {
                    Settings.Secure.putInt(getContentResolver(), "attentive_timeout", 14100000);
                } else {
                    Settings.Secure.putInt(getContentResolver(), "attentive_timeout", 35000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void handlerBroadcastTurnkeyMessage(Context context, Intent intent) {
        ComponentStatusListener instance = ComponentStatusListener.getInstance();
        if (instance == null) {
            MtkLog.d("BootBroadcastReceiver", "instance is null!");
        } else if (instance == null) {
        } else {
            if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                this.mBlackView.show();
                instance.updateStatus(17, 99);
            } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                Log.d("TurnkeyService", "blackview dismiss start");
                this.mBlackView.dismiss();
                Log.d("TurnkeyService", "blackview dismiss end");
                if (!(DestroyApp.getSingletons() == null || DestroyApp.getSingletons().getChannelDataManager() == null)) {
                    MtkLog.d("BootBroadcastReceiver", "BGM scan update channel list ");
                    DestroyApp.getSingletons().getChannelDataManager().handleUpdateChannels();
                }
                instance.updateStatus(18, 99);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handlerBroadcastOADMessage(Context context, Intent intent) {
        if (NavOADActivity.getInstance() == null) {
            return;
        }
        if ("android.intent.action.SCREEN_OFF".equals(intent.getAction()) || "android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
            NavOADActivity.getInstance().setRemindMeLater(false);
        } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
            NavOADActivity.getInstance().setRemindMeLater(true);
        }
    }

    /* access modifiers changed from: private */
    public void handlerBroadcastDVRMessage() {
        if (MarketRegionInfo.isFunctionSupport(30) && StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            StateDvr.getInstance().showSmallCtrlBar();
            StateDvr.getInstance().clearWindow(true);
        }
    }
}
