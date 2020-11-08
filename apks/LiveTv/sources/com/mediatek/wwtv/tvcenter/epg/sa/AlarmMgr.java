package com.mediatek.wwtv.tvcenter.epg.sa;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.epg.sa.db.DBMgrProgramList;
import com.mediatek.wwtv.tvcenter.epg.sa.db.EPGBookListViewDataItem;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmMgr {
    public static String ALARM_EPG_ACTION = "com.mediatek.wwtv.tvcenter.saepgreceiver";
    public static final int EPG_ALARM_MSG = 0;
    private static String LITV_PKG_EPG_ACTIVITY = "com.mediatek.wwtv.tvcenter.epg";
    private static String LITV_PKG_MAIN_ACTIVITY = "com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity";
    private static String LITV_PKG_NAME = "com.mediatek.wwtv.tvcenter";
    /* access modifiers changed from: private */
    public static String SA_EOG_DIALOG_ACTION = "com.mediatek.wwtv.tvcenter.saepg.activity";
    /* access modifiers changed from: private */
    public static String TAG = "AlarmMgr";
    private static AlarmMgr instance;
    public static long timeInterval;
    /* access modifiers changed from: private */
    public long currentMills;
    /* access modifiers changed from: private */
    public TurnkeyCommDialog mBookProgramConfirmDialog;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public EPGBookListViewDataItem mGotoChannelProgram;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            EPGBookListViewDataItem tempInfo = (EPGBookListViewDataItem) msg.obj;
            int curChannelId = CommonIntegration.getInstanceWithContext(AlarmMgr.this.mContext.getApplicationContext()).getCurrentChannelId();
            String access$100 = AlarmMgr.TAG;
            MtkLog.d(access$100, "curChannelId: " + curChannelId);
            if (tempInfo == null || curChannelId != tempInfo.mChannelId || !CommonIntegration.getInstance().isCurrentSourceTv() || !AlarmMgr.this.isTKRunning()) {
                if (!(tempInfo == null || tempInfo.mChannelId == 0)) {
                    Intent t = new Intent(AlarmMgr.SA_EOG_DIALOG_ACTION);
                    t.putExtra("currentMills", tempInfo.mProgramStartTime);
                    t.putExtra("programname", tempInfo.mProgramName);
                    t.putExtra("channelid", tempInfo.mChannelId);
                    t.addFlags(268435456);
                    AlarmMgr.this.mContext.startActivity(t);
                }
                super.handleMessage(msg);
                return;
            }
            MtkLog.d(AlarmMgr.TAG, "need not to change channel");
        }
    };
    private boolean mIsStarted = false;
    private Timer mTimer;
    private PendingIntent pendingIntent;
    /* access modifiers changed from: private */
    public long readMills;
    private EPGBookListViewDataItem tempInfo;

    public AlarmMgr(Context context) {
        this.mContext = context;
        this.mTimer = new Timer();
    }

    public static synchronized AlarmMgr getInstance(Context context) {
        AlarmMgr alarmMgr;
        synchronized (AlarmMgr.class) {
            if (instance == null) {
                instance = new AlarmMgr(context);
            }
            alarmMgr = instance;
        }
        return alarmMgr;
    }

    public void startTimer(boolean isFromTimeSetReceiver) {
        String str = TAG;
        MtkLog.d(str, "before startTimer>>>" + this.mTimer + ",mIsStarted>>>" + this.mIsStarted + ",isFromTimeSetReceiver>>>>" + isFromTimeSetReceiver);
        if (this.mTimer == null) {
            return;
        }
        if (isFromTimeSetReceiver || !this.mIsStarted) {
            String str2 = TAG;
            MtkLog.d(str2, "startTimer>>>" + this.mTimer);
            this.mIsStarted = true;
            this.mTimer.schedule(new TimerTask() {
                public void run() {
                    EPGBookListViewDataItem tempItem;
                    long unused = AlarmMgr.this.currentMills = EPGUtil.getCurrentTime();
                    MtkTvTimeFormatBase mtkTvTimeFormatBase = MtkTvTime.getInstance().getBroadcastTime();
                    mtkTvTimeFormatBase.set(AlarmMgr.this.currentMills);
                    String access$100 = AlarmMgr.TAG;
                    MtkLog.d(access$100, "startTimer>>" + mtkTvTimeFormatBase.hour + "   " + mtkTvTimeFormatBase.minute + "   " + mtkTvTimeFormatBase.second);
                    if (AlarmMgr.this.currentMills != 0) {
                        DBMgrProgramList.getInstance(AlarmMgr.this.mContext).getWriteableDB();
                        Iterator<EPGBookListViewDataItem> it = DBMgrProgramList.getInstance(AlarmMgr.this.mContext).getProgramList().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            tempItem = it.next();
                            String access$1002 = AlarmMgr.TAG;
                            MtkLog.d(access$1002, "tempItem>mProgramStartTime:" + tempItem.mProgramStartTime);
                            mtkTvTimeFormatBase.set(tempItem.mProgramStartTime);
                            String access$1003 = AlarmMgr.TAG;
                            MtkLog.d(access$1003, "mProgramStartTime>>" + mtkTvTimeFormatBase.hour + "   " + mtkTvTimeFormatBase.minute + "   " + mtkTvTimeFormatBase.second);
                            if ((tempItem.mProgramStartTime < AlarmMgr.this.currentMills || tempItem.mProgramStartTime - AlarmMgr.this.currentMills > 6) && (AlarmMgr.this.currentMills <= tempItem.mProgramStartTime || AlarmMgr.this.currentMills - tempItem.mProgramStartTime > 6)) {
                            }
                        }
                        DBMgrProgramList.getInstance(AlarmMgr.this.mContext).deleteProgram(tempItem);
                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = tempItem;
                        if (tempItem.mProgramStartTime >= AlarmMgr.this.currentMills) {
                            AlarmMgr.this.mHandler.sendMessageDelayed(message, (tempItem.mProgramStartTime - AlarmMgr.this.currentMills) * 1000);
                            MtkLog.d(AlarmMgr.TAG, "start time >= currentMills");
                        } else {
                            AlarmMgr.this.mHandler.sendMessageDelayed(message, 0);
                            MtkLog.d(AlarmMgr.TAG, "start time < currentMills");
                        }
                        DBMgrProgramList.getInstance(AlarmMgr.this.mContext).closeDB();
                    }
                }
            }, 1000, 3000);
        }
    }

    public void startTimer() {
        String str = TAG;
        MtkLog.d(str, "before startTimer>>>" + this.mTimer);
        if (this.mTimer != null && !this.mIsStarted) {
            String str2 = TAG;
            MtkLog.d(str2, "startTimer>>>" + this.mTimer);
            this.mIsStarted = true;
            this.mTimer.schedule(new TimerTask() {
                public void run() {
                    long unused = AlarmMgr.this.currentMills = EPGUtil.getCurrentTime();
                    MtkTvTimeFormatBase mtkTvTimeFormatBase = MtkTvTime.getInstance().getBroadcastTime();
                    mtkTvTimeFormatBase.set(AlarmMgr.this.currentMills);
                    String access$100 = AlarmMgr.TAG;
                    MtkLog.d(access$100, "startTimer>>" + mtkTvTimeFormatBase.hour + "   " + mtkTvTimeFormatBase.minute + "   " + mtkTvTimeFormatBase.second);
                    AlarmMgr alarmMgr = AlarmMgr.this;
                    SaveValue instance = SaveValue.getInstance(AlarmMgr.this.mContext);
                    StringBuilder sb = new StringBuilder();
                    sb.append(AlarmMgr.this.currentMills);
                    sb.append("");
                    long unused2 = alarmMgr.currentMills = instance.readLongValue(sb.toString(), 0);
                    if (AlarmMgr.this.currentMills != 0) {
                        long unused3 = AlarmMgr.this.readMills = AlarmMgr.this.currentMills;
                        DBMgrProgramList.getInstance(AlarmMgr.this.mContext).getWriteableDB();
                        EPGBookListViewDataItem tempInfo = null;
                        for (EPGBookListViewDataItem tempItem : DBMgrProgramList.getInstance(AlarmMgr.this.mContext).getProgramList()) {
                            if (tempItem.mProgramStartTime <= AlarmMgr.this.readMills) {
                                if (tempItem.mProgramStartTime == AlarmMgr.this.readMills) {
                                    tempInfo = tempItem;
                                }
                                DBMgrProgramList.getInstance(AlarmMgr.this.mContext).deleteProgram(tempItem);
                                SaveValue instance2 = SaveValue.getInstance(AlarmMgr.this.mContext);
                                instance2.removekey(AlarmMgr.this.currentMills + "");
                            }
                        }
                        DBMgrProgramList.getInstance(AlarmMgr.this.mContext).closeDB();
                        if (tempInfo != null && tempInfo.mChannelId != 0) {
                            EPGBookListViewDataItem unused4 = AlarmMgr.this.mGotoChannelProgram = tempInfo;
                            AlarmMgr.this.mHandler.sendEmptyMessage(0);
                        }
                    }
                }
            }, 1000, 1000);
        }
    }

    public void cancelTimer() {
        String str = TAG;
        MtkLog.d(str, "cancelTimer>>>" + this.mTimer);
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
            this.mIsStarted = false;
        }
    }

    public void startAlarm() {
        String str = TAG;
        MtkLog.d(str, "startAlarm  mIsStarted>>>" + this.mIsStarted);
        if (!this.mIsStarted) {
            this.mIsStarted = true;
            Intent intent = new Intent(ALARM_EPG_ACTION);
            intent.addFlags(16777216);
            this.pendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
            ((AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM)).setExact(1, EPGUtil.getCurrentTime() * 1000, this.pendingIntent);
        }
    }

    public void cancelAlarm() {
        String str = TAG;
        MtkLog.d(str, "cancelAlarm>>>" + this.pendingIntent + ",mIsStarted: " + this.mIsStarted);
        if (this.mIsStarted && this.pendingIntent != null) {
            this.mIsStarted = false;
            ((AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(this.pendingIntent);
        }
    }

    private void showBookConfirm(Context context) {
        if (this.mBookProgramConfirmDialog == null) {
            this.mBookProgramConfirmDialog = new TurnkeyCommDialog(context, 3);
            TurnkeyCommDialog turnkeyCommDialog = this.mBookProgramConfirmDialog;
            turnkeyCommDialog.setMessage(this.mGotoChannelProgram.mProgramName + context.getString(R.string.nav_epg_book_program_coming_tip));
        } else {
            TurnkeyCommDialog turnkeyCommDialog2 = this.mBookProgramConfirmDialog;
            turnkeyCommDialog2.setMessage(this.mGotoChannelProgram.mProgramName + context.getString(R.string.nav_epg_book_program_coming_tip));
            this.mBookProgramConfirmDialog.setText();
        }
        this.mBookProgramConfirmDialog.setButtonYesName(context.getString(R.string.menu_ok));
        this.mBookProgramConfirmDialog.setButtonNoName(context.getString(R.string.menu_cancel));
        this.mBookProgramConfirmDialog.show();
        this.mBookProgramConfirmDialog.getButtonYes().requestFocus();
        this.mBookProgramConfirmDialog.setPositon(-20, 70);
        this.mBookProgramConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                AlarmMgr.this.mBookProgramConfirmDialog.dismiss();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                if (!(AlarmMgr.this.mGotoChannelProgram == null || AlarmMgr.this.mGotoChannelProgram.mChannelId == 0)) {
                    CommonIntegration.getInstanceWithContext(AlarmMgr.this.mContext.getApplicationContext()).selectChannelById(AlarmMgr.this.mGotoChannelProgram.mChannelId);
                }
                AlarmMgr.this.mBookProgramConfirmDialog.dismiss();
                return true;
            }
        };
        this.mBookProgramConfirmDialog.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                AlarmMgr.this.mBookProgramConfirmDialog.dismiss();
                return true;
            }
        });
        this.mBookProgramConfirmDialog.getButtonYes().setOnKeyListener(yesListener);
    }

    /* access modifiers changed from: private */
    public boolean isTKRunning() {
        try {
            ComponentName cn = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningTasks(1).get(0).topActivity;
            String pkgName = cn.getPackageName();
            String activity = cn.getClassName();
            String str = TAG;
            Log.d(str, "isTargetActivityRunning pkgName :" + pkgName + ", activity :" + activity);
            if (!LITV_PKG_NAME.equalsIgnoreCase(pkgName)) {
                return false;
            }
            if (LITV_PKG_MAIN_ACTIVITY.equalsIgnoreCase(activity) || activity.contains(LITV_PKG_EPG_ACTIVITY)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
