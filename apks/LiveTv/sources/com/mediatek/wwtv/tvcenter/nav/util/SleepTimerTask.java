package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SleepTimerTask {
    public static final int HIDE_DIALOG = 13;
    public static final int SHOW_DIALOG = 12;
    public static final String SLEEP_TIMER_ACTION = "com.mediatek.ui.menu.util.sleep.timer";
    private static String hideId = "hideDialog";
    static Context mContext;
    private static ScheduledFuture mCurrTask;
    private static ScheduledThreadPoolExecutor mExec;
    private static Map<String, ScheduledFuture> mTaskMap;
    private static CustomTimerTask mTimerTask;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 12:
                    MtkLog.d("SleepTimerTask", "sleep timer dialog show ");
                    SleepTimerTask.this.tipDialog = new SleepTimerTipDialog(SleepTimerTask.mContext, (String) msg.obj);
                    SleepTimerTask.this.tipDialog.getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
                    SleepTimerTask.this.tipDialog.show();
                    SleepTimerTask.this.tipDialog.setPositon(-400, 300);
                    break;
                case 13:
                    MtkLog.d("SleepTimerTask", "sleep timer dialog hide ");
                    if (SleepTimerTask.this.tipDialog != null) {
                        SleepTimerTask.this.tipDialog.dismiss();
                        break;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private long mills;
    SleepTimerTipDialog tipDialog = null;

    public SleepTimerTask(Context context, Handler handler, long mills2) {
        mContext = context;
        this.mills = mills2;
        if (mExec == null) {
            mExec = new ScheduledThreadPoolExecutor(1);
        }
        if (mTaskMap == null) {
            mTaskMap = new HashMap();
        }
    }

    public SleepTimerTask() {
    }

    public void doExec(String itemId) {
        if (mExec != null) {
            MtkLog.d("SleepTimerTask", "itemId is:" + itemId);
            ScheduledFuture task = mTaskMap.get(itemId);
            if (task != null) {
                boolean ret = task.cancel(false);
                mExec.remove(mTimerTask);
                MtkLog.d("SleepTimerTask", "task cancel " + ret);
            } else {
                MtkLog.d("SleepTimerTask", "task is null");
            }
            mTimerTask = new CustomTimerTask(itemId);
            mTaskMap.put(itemId, mExec.schedule(mTimerTask, this.mills, TimeUnit.MILLISECONDS));
            return;
        }
        MtkLog.e("SleepTimerTask", "sorry Timer is null");
    }

    public static void doCancelTask(String itemId) {
        if (itemId.equals("g_misc__auto_sleep")) {
            MtkTvConfig.getInstance().setConfigValue("g_misc__auto_sleep", 0);
            return;
        }
        MtkTvTime.getInstance().setSleepTimer(0);
        ScheduledFuture task = mTaskMap.get(itemId);
        if (task != null) {
            boolean ret = task.cancel(false);
            MtkLog.d("SleepTimerTask", "sleep timer is canceled ==" + ret);
            mTaskMap.put(itemId, (Object) null);
        }
        ScheduledFuture task2 = mTaskMap.get(hideId);
        if (task2 != null) {
            boolean ret2 = task2.cancel(false);
            MtkLog.d("SleepTimerTask", "sleep hide dialog task is canceled ==" + ret2);
            mTaskMap.put(hideId, (Object) null);
        }
    }

    public void doHideDialogTask() {
        mTaskMap.put(hideId, mExec.schedule(new HideDialogTask(), 290000, TimeUnit.MILLISECONDS));
    }

    public static void shutDown() {
        if (mExec != null) {
            mExec.shutdown();
        }
    }

    /* access modifiers changed from: private */
    public boolean haveRemainTime() {
        return MtkTvTime.getInstance().getSleepTimerRemainingTime() > 0;
    }

    class CustomTimerTask implements Runnable {
        String mItemId;

        CustomTimerTask(String itemId) {
            this.mItemId = itemId;
        }

        public void run() {
            MtkLog.d("SleepTimerTask", "send sleep tip broadcast");
            if (this.mItemId.equals("g_misc__auto_sleep")) {
                if (MtkTvConfig.getInstance().getConfigValue("g_misc__auto_sleep") == 0) {
                    MtkLog.d("SleepTimerTask", "auto sleep offed so don't send broadcast ");
                } else {
                    SleepTimerTask.this.showDialog(this.mItemId);
                }
            } else if (SleepTimerTask.this.haveRemainTime()) {
                SleepTimerTask.this.showDialog(this.mItemId);
            } else {
                MtkLog.d("SleepTimerTask", "sleep timer offed so don't send broadcast ");
            }
        }
    }

    /* access modifiers changed from: private */
    public void showDialog(String mItemID) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 12;
        msg.obj = mItemID;
        this.mHandler.sendMessage(msg);
        doHideDialogTask();
    }

    class HideDialogTask implements Runnable {
        HideDialogTask() {
        }

        public void run() {
            SleepTimerTask.this.hideDialog();
        }
    }

    public void hideDialog() {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 13;
        this.mHandler.sendMessage(msg);
    }
}
