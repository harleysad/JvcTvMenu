package com.android.tv.settings.partnercustomizer.retailmode;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.wwtv.tvcenter.util.SystemsApi;

public class RetailModeService extends Service {
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private static final int COUNT_DOWN_SECOND = 60;
    private static final int COUNT_DOWN_SECOND_DEMO = 30;
    private static final String DEMO_PLAY_CLASS_NAME = "fusion.android.tv.demo.MainActivity";
    private static final String DEMO_PLAY_PACKAGE_NAME = "fusion.android.tv.demo";
    private static final int MILLISINFUTURE = 60000;
    private static final int MILLISINFUTURE_DEMO = 30000;
    private static final int MSG_START_COUNT_DOWN = 1;
    private static final int MSG_START_RETAIL_DEMO = 3;
    private static final int MSG_USER_ACTIVITY_FOUND = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "com.android.tv.settings.retailmode.RetailModeService";
    private static final String NOTIFICATION_CHANNEL_NAME = "RetailModeService";
    public static final String PACKAGE_NAME_LIVE_TV = "com.mediatek.wwtv.tvcenter";
    public static final String PACKAGE_NAME_TV_LAUNCHER = "com.google.android.tvlauncher";
    private static final String RETAIL_END_BROADCAST_STRING = "fusion.android.tv.demo.action.DONE";
    private static final String RETAIL_MESSAGING_DB_FIELD = "RetailMessaging";
    private static final String RETAIL_MODE_DB_FIELD = "RetailModeEnable";
    private static final String TAG = "RetailModeService";
    private static final String VAL_MESSAGING_DEMO_VIDEO = "demo_video";
    private static final String VAL_MESSAGING_E_POP_BOTTOM = "e_POP_bottom";
    private static final String VAL_MESSAGING_E_POP_LEFT = "e_POP_left";
    BroadcastReceiver ScreenOnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.v("RetailModeService", "ScreenOnReceiver onReceive:" + intent);
            if (RetailModeService.this.mHandler != null) {
                RetailModeService.this.mHandler.sendEmptyMessage(1);
            }
        }
    };
    /* access modifiers changed from: private */
    public CountDownTimer countDownTimer;
    Handler mHandler;
    private HandlerThread mHandlerThread;

    public IBinder onBind(Intent intent) {
        Log.v("RetailModeService", "onBind()");
        return null;
    }

    public void onCreate() {
        Log.v("RetailModeService", "onCreate()");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction(RETAIL_END_BROADCAST_STRING);
        registerReceiver(this.ScreenOnReceiver, filter);
        this.mHandlerThread = new HandlerThread("T_RetailModeService", 10);
        this.mHandlerThread.start();
        this.mHandler = new MainHandler(this.mHandlerThread.getLooper());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("RetailModeService", "onStartCommand()");
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v("RetailModeService", "onDestroy()");
        stopDemoTimer();
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
            this.mHandlerThread = null;
        }
        this.mHandler = null;
        unregisterReceiver(this.ScreenOnReceiver);
    }

    public void stopDemoTimer() {
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }
    }

    public void createDemoTimer() {
        Log.v("RetailModeService", "start countDownTimer:");
        stopDemoTimer();
        this.countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.v("RetailModeService", "countDownTimer():onTick():count= " + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                Log.v("RetailModeService", "countDownTimer():onFinish()");
                RetailModeService.this.mHandler.sendEmptyMessage(3);
            }
        };
        this.countDownTimer.start();
    }

    final class MainHandler extends Handler {
        MainHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            removeMessages(1);
            removeMessages(2);
            removeMessages(3);
            switch (msg.what) {
                case 1:
                    if (RetailModeService.this.countDownTimer != null) {
                        try {
                            RetailModeService.this.countDownTimer.cancel();
                        } catch (Exception e) {
                        }
                        CountDownTimer unused = RetailModeService.this.countDownTimer = null;
                    }
                    if (!RetailModeService.this.isLauncherAndLiveTvInForeground() || !SystemsApi.isUserSetupComplete(RetailModeService.this) || RetailModeService.this.isScanning()) {
                        RetailModeService.this.mHandler.sendEmptyMessageDelayed(1, 1000);
                        return;
                    } else {
                        RetailModeService.this.createDemoTimer();
                        return;
                    }
                case 2:
                    Log.v("RetailModeService", "MSG_USER_ACTIVITY_FOUND");
                    if (RetailModeService.this.isLauncherAndLiveTvInForeground()) {
                        RetailModeService.this.mHandler.sendEmptyMessage(1);
                        return;
                    } else {
                        RetailModeService.this.mHandler.sendEmptyMessageDelayed(2, 1000);
                        return;
                    }
                case 3:
                    Log.v("RetailModeService", "MSG_START_RETAIL_DEMO");
                    if (!RetailModeService.this.isLauncherAndLiveTvInForeground() || !SystemsApi.isUserSetupComplete(RetailModeService.this) || RetailModeService.this.isScanning()) {
                        if (RetailModeService.this.countDownTimer != null) {
                            try {
                                RetailModeService.this.countDownTimer.cancel();
                            } catch (Exception e2) {
                            }
                            CountDownTimer unused2 = RetailModeService.this.countDownTimer = null;
                        }
                        RetailModeService.this.mHandler.sendEmptyMessageDelayed(1, 1000);
                        return;
                    }
                    RetailModeService.this.StartRetailDemo();
                    RetailModeService.this.mHandler.sendEmptyMessageDelayed(2, 1000);
                    return;
                default:
                    Log.e("RetailModeService", "Unknown handleMessage");
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void StartRetailDemo() {
        int ret = Settings.Global.getInt(getContentResolver(), "RetailMessaging", 0);
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setComponent(new ComponentName(DEMO_PLAY_PACKAGE_NAME, DEMO_PLAY_CLASS_NAME));
        intent.putExtra("RetailMessaging", ret);
        Log.v("RetailModeService", "startActivity(" + ret + "):RetailDemo:" + DEMO_PLAY_PACKAGE_NAME + "/" + DEMO_PLAY_CLASS_NAME);
        try {
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public boolean isLauncherAndLiveTvInForeground() {
        ComponentName componentName = ((ActivityManager) getSystemService("activity")).getRunningTasks(1).get(0).topActivity;
        Log.v("RetailModeService", "isLauncherAndLiveTvInForeground - " + componentName);
        if (!componentName.getPackageName().equals(PACKAGE_NAME_TV_LAUNCHER) && !componentName.getPackageName().equals(PACKAGE_NAME_LIVE_TV)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }

    public static void bootup(Context context) {
        int RetailModeEnable = Settings.Global.getInt(context.getContentResolver(), "RetailModeEnable", -1);
        String RetailMessaging = Settings.Global.getString(context.getContentResolver(), "RetailMessaging");
        if (RetailModeEnable == -1) {
            Settings.Global.putInt(context.getContentResolver(), "RetailModeEnable", 0);
        }
        if (RetailMessaging == null) {
            Settings.Global.putInt(context.getContentResolver(), "RetailMessaging", 0);
        }
        if (RetailModeEnable > 0) {
            Intent intentSvc = new Intent(context, RetailModeService.class);
            intentSvc.putExtra("RetailModeEnable", RetailModeEnable);
            intentSvc.putExtra("RetailMessaging", RetailMessaging);
            Log.v("RetailModeService", "startService RetailModeEnable= " + RetailModeEnable);
            context.startService(intentSvc);
        }
    }
}
