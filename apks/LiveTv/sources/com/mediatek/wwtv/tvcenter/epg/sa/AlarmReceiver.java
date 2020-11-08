package com.mediatek.wwtv.tvcenter.epg.sa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
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
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Iterator;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    /* access modifiers changed from: private */
    public static String SA_EOG_DIALOG_ACTION = "com.mediatek.wwtv.tvcenter.saepg.activity";
    /* access modifiers changed from: private */
    public static String TAG = "AlarmReceiver";
    /* access modifiers changed from: private */
    public TurnkeyCommDialog mBookProgramConfirmDialog;
    /* access modifiers changed from: private */
    public Context mContext;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int curChannelId = CommonIntegration.getInstanceWithContext(AlarmReceiver.this.mContext.getApplicationContext()).getCurrentChannelId();
            String access$100 = AlarmReceiver.TAG;
            MtkLog.d(access$100, "curChannelId: " + curChannelId);
            if (curChannelId != AlarmReceiver.this.tempInfo.mChannelId || !CommonIntegration.getInstance().isCurrentSourceTv()) {
                if (!(AlarmReceiver.this.tempInfo == null || AlarmReceiver.this.tempInfo.mChannelId == 0)) {
                    Intent t = new Intent(AlarmReceiver.SA_EOG_DIALOG_ACTION);
                    t.putExtra("currentMills", AlarmReceiver.this.tempInfo.mProgramStartTime);
                    t.putExtra("programname", AlarmReceiver.this.tempInfo.mProgramName);
                    t.putExtra("channelid", AlarmReceiver.this.tempInfo.mChannelId);
                    t.addFlags(268435456);
                    AlarmReceiver.this.mContext.startActivity(t);
                }
                super.handleMessage(msg);
                return;
            }
            MtkLog.d(AlarmReceiver.TAG, "need not to change channel");
        }
    };
    /* access modifiers changed from: private */
    public EPGBookListViewDataItem tempInfo;

    public void onReceive(Context context, Intent intent) {
        EPGBookListViewDataItem tempItem;
        this.mContext = context;
        String action = intent.getAction();
        String str = TAG;
        MtkLog.d(str, "AlarmReceiver action: " + action);
        if (action.equals("android.intent.action.TIME_SET")) {
            AlarmMgr.getInstance(this.mContext).startTimer(true);
        } else if (action.equals("")) {
            long currentMills = EPGUtil.getCurrentTime();
            String str2 = TAG;
            MtkLog.d(str2, "currentMills: " + currentMills);
            MtkTvTimeFormatBase mtkTvTimeFormatBase = MtkTvTime.getInstance().getBroadcastTime();
            mtkTvTimeFormatBase.set(currentMills);
            String str3 = TAG;
            MtkLog.d(str3, "currentMills>>" + mtkTvTimeFormatBase.hour + "   " + mtkTvTimeFormatBase.minute + "   " + mtkTvTimeFormatBase.second);
            if (currentMills != 0) {
                DBMgrProgramList.getInstance(this.mContext).getWriteableDB();
                List<EPGBookListViewDataItem> tempList = DBMgrProgramList.getInstance(this.mContext).getProgramList();
                this.tempInfo = null;
                Iterator<EPGBookListViewDataItem> it = tempList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    tempItem = it.next();
                    String str4 = TAG;
                    MtkLog.d(str4, "tempItem>mProgramStartTime:" + tempItem.mProgramStartTime);
                    mtkTvTimeFormatBase.set(tempItem.mProgramStartTime);
                    String str5 = TAG;
                    MtkLog.d(str5, "mProgramStartTime>>" + mtkTvTimeFormatBase.hour + "   " + mtkTvTimeFormatBase.minute + "   " + mtkTvTimeFormatBase.second);
                    if ((tempItem.mProgramStartTime < currentMills || tempItem.mProgramStartTime - currentMills > 6) && (currentMills <= tempItem.mProgramStartTime || currentMills - tempItem.mProgramStartTime > 6)) {
                    }
                }
                this.tempInfo = tempItem;
                DBMgrProgramList.getInstance(this.mContext).deleteProgram(tempItem);
                if (tempItem.mProgramStartTime >= currentMills) {
                    this.mHandler.sendEmptyMessageDelayed(0, (tempItem.mProgramStartTime - currentMills) * 1000);
                    MtkLog.d(TAG, "start time >= currentMills");
                } else {
                    this.mHandler.sendEmptyMessageDelayed(0, 0);
                    MtkLog.d(TAG, "start time < currentMills");
                }
                DBMgrProgramList.getInstance(this.mContext).closeDB();
            }
            scheduleAlarms(context);
        }
    }

    private void scheduleAlarms(Context context) {
        MtkLog.d(TAG, "scheduleAlarms");
        Intent intent = new Intent(AlarmMgr.ALARM_EPG_ACTION);
        intent.addFlags(16777216);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmMgr.getInstance(context).cancelAlarm();
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).setExact(1, (EPGUtil.getCurrentTime() * 1000) + MessageType.delayMillis5, pendingIntent);
    }

    private void showBookConfirm(Context context) {
        if (this.mBookProgramConfirmDialog == null) {
            this.mBookProgramConfirmDialog = new TurnkeyCommDialog(context, 3);
        }
        this.mBookProgramConfirmDialog.setMessage(context.getString(R.string.menu_tv_reset_all));
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
                AlarmReceiver.this.mBookProgramConfirmDialog.dismiss();
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
                AlarmReceiver.this.mBookProgramConfirmDialog.dismiss();
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
                AlarmReceiver.this.mBookProgramConfirmDialog.dismiss();
                return true;
            }
        });
        this.mBookProgramConfirmDialog.getButtonYes().setOnKeyListener(yesListener);
    }
}
