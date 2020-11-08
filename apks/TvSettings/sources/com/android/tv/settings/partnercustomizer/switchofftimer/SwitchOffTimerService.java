package com.android.tv.settings.partnercustomizer.switchofftimer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;

public class SwitchOffTimerService extends Service {
    private static final int COUNT_DOWN_DISPLAY_THRESHOLD = 60;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private static final int COUNT_DOWN_SECOND = 60;
    private static final int MILLISINFUTURE = 60000;
    private static final int MSG_START_COUNT_DOWN = 1;
    private static final int MSG_START_SUSPEND = 3;
    private static final int MSG_USER_ACTIVITY_FOUND = 2;
    private static final String TAG = "SwitchOffTimerService";
    BroadcastReceiver ScreenOnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.v(SwitchOffTimerService.TAG, "ScreenOnReceiver onReceive:" + intent);
            if (SwitchOffTimerService.this.mHandler != null) {
                SwitchOffTimerService.this.mHandler.sendEmptyMessage(1);
            }
        }
    };
    /* access modifiers changed from: private */
    public Toast SwitchOffTimerDisplay = null;
    /* access modifiers changed from: private */
    public CountDownTimer countDownTimer;
    Handler mHandler;
    private HandlerThread mHandlerThread;
    /* access modifiers changed from: private */
    public Context myContext = this;

    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind()");
        return null;
    }

    public void onCreate() {
        Log.v(TAG, "onCreate()");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(this.ScreenOnReceiver, filter);
        this.mHandlerThread = new HandlerThread("T_SwitchOffTimerService", 10);
        this.mHandlerThread.start();
        this.mHandler = new MainHandler(this.mHandlerThread.getLooper());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        stopSwitchOffTimer();
        unregisterReceiver(this.ScreenOnReceiver);
        this.mHandlerThread.quit();
        this.mHandlerThread = null;
        this.mHandler = null;
        super.onDestroy();
    }

    public void stopSwitchOffTimer() {
        if (this.SwitchOffTimerDisplay != null) {
            this.SwitchOffTimerDisplay.cancel();
            this.SwitchOffTimerDisplay = null;
        }
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }
    }

    public void createSwitchOffTimer() {
        int SwitchOffTimerValue = Settings.Global.getInt(getContentResolver(), PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER, 0);
        Log.v(TAG, "createSwitchOffTimer:" + SwitchOffTimerValue);
        long value = ((long) (SwitchOffTimerValue * 60)) * 1000;
        stopSwitchOffTimer();
        if (value > 0) {
            this.countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(long millisUntilFinished) {
                    long RemainingTimeInSecond = millisUntilFinished / 1000;
                    Log.v(SwitchOffTimerService.TAG, "SwitchOffTimer():onTick():count= " + RemainingTimeInSecond);
                    if (RemainingTimeInSecond < 60 && RemainingTimeInSecond % 5 == 0) {
                        Toast unused = SwitchOffTimerService.this.SwitchOffTimerDisplay = Toast.makeText(SwitchOffTimerService.this.myContext, SwitchOffTimerService.this.myContext.getString(R.string.s_power_switch_off_timer_toast, new Object[]{Long.valueOf(RemainingTimeInSecond)}), 1);
                        SwitchOffTimerService.this.SwitchOffTimerDisplay.show();
                    }
                }

                public void onFinish() {
                    Log.v(SwitchOffTimerService.TAG, "SwitchOffTimer():onFinish()");
                    SwitchOffTimerService.this.mHandler.sendEmptyMessage(3);
                }
            };
            this.countDownTimer.start();
        }
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
                    SwitchOffTimerService.this.createSwitchOffTimer();
                    return;
                case 2:
                    Log.v(SwitchOffTimerService.TAG, "MSG_USER_ACTIVITY_FOUND");
                    if (SwitchOffTimerService.this.countDownTimer != null) {
                        try {
                            SwitchOffTimerService.this.countDownTimer.cancel();
                        } catch (Exception e) {
                        }
                        CountDownTimer unused = SwitchOffTimerService.this.countDownTimer = null;
                    }
                    SwitchOffTimerService.this.mHandler.sendEmptyMessage(1);
                    return;
                case 3:
                    Log.v(SwitchOffTimerService.TAG, "MSG_START_SUSPEND");
                    if (SwitchOffTimerService.this.SwitchOffTimerDisplay != null) {
                        SwitchOffTimerService.this.SwitchOffTimerDisplay.cancel();
                        Toast unused2 = SwitchOffTimerService.this.SwitchOffTimerDisplay = null;
                    }
                    SwitchOffTimerService.this.GoToSleep();
                    return;
                default:
                    Log.e(SwitchOffTimerService.TAG, "Unknown handleMessage");
                    return;
            }
        }
    }

    public void GoToSleep() {
        Log.v(TAG, "mPowerManager.goToSleep");
        ((PowerManager) getSystemService("power")).goToSleep(SystemClock.uptimeMillis(), 0, 0);
    }

    public static void bootup(Context context) {
        int SwitchOffTimerValue = Settings.Global.getInt(context.getContentResolver(), PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER, -1);
        if (SwitchOffTimerValue == -1) {
            Settings.Global.putInt(context.getContentResolver(), PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER, 0);
        }
        Log.v(TAG, "SwitchOffTimerValue= " + SwitchOffTimerValue);
        if (SwitchOffTimerValue > 0) {
            Intent intentSvc = new Intent(context, SwitchOffTimerService.class);
            intentSvc.putExtra("SwitchOffTimerValue", SwitchOffTimerValue);
            context.startService(intentSvc);
        }
    }
}
