package com.mediatek.wwtv.tvcenter.epg.cn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.common.MtkTvIntentBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.epg.cn.EPGListView;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import java.util.Iterator;
import java.util.List;

public class EPGCnActivity extends BaseActivity implements DialogInterface.OnDismissListener {
    private static final int PER_PAGE_LINE = 4;
    private static final String TAG = "EPGCnActivity";
    /* access modifiers changed from: private */
    public long curTime = 0;
    /* access modifiers changed from: private */
    public int dayNum = 0;
    /* access modifiers changed from: private */
    public AsyncTaskEventsLoad eventLoad;
    /* access modifiers changed from: private */
    public boolean isGetingData;
    /* access modifiers changed from: private */
    public boolean isSpecialState = false;
    /* access modifiers changed from: private */
    public long lastHourTime = 0;
    /* access modifiers changed from: private */
    public long lastTime = 0;
    private TextView mBeginTimeTv;
    /* access modifiers changed from: private */
    public boolean mCanChangeTimeShow;
    /* access modifiers changed from: private */
    public TextView mCurrentDateTv;
    private int mCurrentPage = 1;
    /* access modifiers changed from: private */
    public EPGProgramInfo mCurrentSelectedProgramInfo;
    /* access modifiers changed from: private */
    public TextView mDataRetrievingShow;
    private EPGPwdDialog mEPGPwdDialog;
    private TextView mEndTimeTv;
    private View mFreeViewHDLogo;
    private Runnable mGetCurrentTimeRunnable = new Runnable() {
        public void run() {
            if (EPGCnActivity.this.mListViewAdpter != null) {
                long unused = EPGCnActivity.this.curTime = EPGUtil.getCurrentTime();
                if (EPGCnActivity.this.curTime - EPGCnActivity.this.lastTime < -600 || EPGCnActivity.this.curTime - EPGCnActivity.this.lastHourTime >= 3600) {
                    MtkLog.d(EPGCnActivity.TAG, "=====curTime - last time: " + (EPGCnActivity.this.curTime - EPGCnActivity.this.lastHourTime) + "  " + EPGUtil.getCurrentHour());
                    int unused2 = EPGCnActivity.this.dayNum = 0;
                    long unused3 = EPGCnActivity.this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
                    if (EPGCnActivity.this.curTime - EPGCnActivity.this.lastTime < 0) {
                        EPGConfig.FROM_WHERE = 27;
                    } else {
                        EPGConfig.FROM_WHERE = 26;
                    }
                    if (EPGCnActivity.this.mListViewAdpter != null) {
                        EPGCnActivity.this.mListViewAdpter.setDayNum(EPGCnActivity.this.dayNum);
                        EPGCnActivity.this.mListViewAdpter.setStartHour(EPGUtil.getCurrentHour());
                        boolean unused4 = EPGCnActivity.this.mNeedNowGetData = true;
                        boolean unused5 = EPGCnActivity.this.mCanChangeTimeShow = true;
                        EPGCnActivity.this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
                        EPGCnActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_UPDATE_API_EVENT_LIST, 1000);
                    } else {
                        return;
                    }
                }
                long unused6 = EPGCnActivity.this.lastTime = EPGCnActivity.this.curTime;
                String mDate = EPGUtil.formatCurrentTimeWith24Hours();
                Message msg = Message.obtain();
                msg.obj = mDate;
                msg.what = 1001;
                EPGCnActivity.this.mHandler.sendMessageDelayed(msg, 1000);
            }
        }
    };
    private Runnable mGetEventsRunnable = new Runnable() {
        public void run() {
            EPGCnActivity.this.mReader.readProgramInfoByTIF(EPGCnActivity.this.mListViewAdpter.getGroup(), EPGCnActivity.this.mListViewAdpter.getDayNum(), EPGCnActivity.this.mListViewAdpter.getStartHour());
            EPGCnActivity.this.mHandler.post(new Runnable() {
                public void run() {
                    if (EPGCnActivity.this.mListViewAdpter != null && EPGCnActivity.this.mListView != null) {
                        EPGCnActivity.this.refreshUpdateLayout(EPGCnActivity.this.mListViewAdpter.getGroup());
                        EPGCnActivity.this.showSelectedProgramInfo();
                        EPGCnActivity.this.mHandler.sendEmptyMessage(5);
                        EPGCnActivity.this.mListView.mCanChangeChannel = true;
                        boolean unused = EPGCnActivity.this.isGetingData = false;
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        /* JADX WARNING: Code restructure failed: missing block: B:160:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:161:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:162:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:163:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:164:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:165:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:166:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x0218, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$100(r9.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x0220, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$600(r9.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x0228, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$000(r9.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x0230, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$1800(r9.this$0) == false) goto L_0x023a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x0232, code lost:
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.TAG, "AsyncTaskEventsLoad is readig event in provider>");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x0239, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x023a, code lost:
            com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$1802(r9.this$0, true);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x0245, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$100(r9.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x024d, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$000(r9.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:69:0x0259, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$000(r9.this$0).getGroup() == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:0x025b, code lost:
            com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.access$1900(r9.this$0);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r10) {
            /*
                r9 = this;
                int r0 = r10.what
                r1 = 1001(0x3e9, float:1.403E-42)
                if (r0 == r1) goto L_0x04ce
                r1 = 1879048207(0x7000000f, float:1.584566E29)
                if (r0 == r1) goto L_0x0458
                r1 = 1879048209(0x70000011, float:1.5845665E29)
                r2 = 8
                if (r0 == r1) goto L_0x0401
                r1 = 1879048226(0x70000022, float:1.5845697E29)
                r3 = 4
                r4 = 276(0x114, float:3.87E-43)
                r5 = 1
                r6 = 0
                if (r0 == r1) goto L_0x033c
                switch(r0) {
                    case 4: goto L_0x0331;
                    case 5: goto L_0x0326;
                    case 6: goto L_0x0313;
                    default: goto L_0x001f;
                }
            L_0x001f:
                r1 = 5
                r7 = 260(0x104, float:3.64E-43)
                switch(r0) {
                    case 259: goto L_0x02e6;
                    case 260: goto L_0x02df;
                    case 261: goto L_0x02ca;
                    case 262: goto L_0x0262;
                    case 263: goto L_0x0212;
                    case 264: goto L_0x01cb;
                    case 265: goto L_0x0193;
                    default: goto L_0x0025;
                }
            L_0x0025:
                switch(r0) {
                    case 273: goto L_0x012d;
                    case 274: goto L_0x00f1;
                    case 275: goto L_0x00ae;
                    case 276: goto L_0x00a1;
                    case 277: goto L_0x0212;
                    case 278: goto L_0x005e;
                    default: goto L_0x0028;
                }
            L_0x0028:
                switch(r0) {
                    case 1879048193: goto L_0x002d;
                    case 1879048194: goto L_0x0401;
                    default: goto L_0x002b;
                }
            L_0x002b:
                goto L_0x04e1
            L_0x002d:
                java.lang.String r0 = "EPGCnActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "MSG_CB_CONFIG"
                r1.append(r2)
                java.lang.Object r2 = r10.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r2 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r2
                int r2 = r2.param1
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                java.lang.Object r0 = r10.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                int r0 = r0.param1
                if (r0 != 0) goto L_0x04e1
                boolean r0 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurEPGActivity()
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.finish()
                goto L_0x04e1
            L_0x005e:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                java.lang.Object r1 = r10.obj
                java.util.List r1 = (java.util.List) r1
                int r2 = r10.arg2
                r0.updateAdapter(r1, r6, r2)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                int r1 = r1.dayNum
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r0.changeTimeViewsShow(r1, r2)
                int r0 = r10.arg1
                int r0 = r0 % 6
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r0
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                r0.setSelection(r1)
                goto L_0x04e1
            L_0x00a1:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean r1 = r1.isSpecialState
                r0.setLockIconVisibility(r1)
                goto L_0x04e1
            L_0x00ae:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                java.lang.Object r1 = r10.obj
                java.util.List r1 = (java.util.List) r1
                int r2 = r10.arg2
                r0.setAdapter(r1, r6, r2)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                int r1 = r1.dayNum
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r0.changeTimeViewsShow(r1, r2)
                int r0 = r10.arg1
                int r0 = r0 % 6
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r0
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                r0.setSelection(r1)
                goto L_0x04e1
            L_0x00f1:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean r0 = r0.mCanChangeTimeShow
                if (r0 == 0) goto L_0x0126
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r0.mCanChangeTimeShow = r6
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r1 = r1.mListViewAdpter
                int r1 = r1.getDayNum()
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r0.changeTimeViewsShow(r1, r2)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.updateEventList(r5)
                goto L_0x04e1
            L_0x0126:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.updateEventList(r6)
                goto L_0x04e1
            L_0x012d:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                java.lang.String r0 = "EPGCnActivity"
                java.lang.String r1 = "EPGConfig.EPG_SELECT_CHANNEL_COMPLETE"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.removeMessages(r4)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.ImageView r0 = r0.mLockImageView
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x0162
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.ImageView r0 = r0.mLockImageView
                r0.setVisibility(r3)
            L_0x0162:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                boolean r0 = r0.isTvSourceLock()
                if (r0 == 0) goto L_0x0175
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.finish()
                goto L_0x04e1
            L_0x0175:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.removeMessages(r7)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r1 = 1000(0x3e8, double:4.94E-321)
                r0.sendEmptyMessageDelayed(r7, r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                r0.mCanChangeChannel = r5
                goto L_0x04e1
            L_0x0193:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                r0.notifyDataSetChanged()
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.removeMessages(r7)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r2 = 800(0x320, double:3.953E-321)
                r0.sendEmptyMessageDelayed(r7, r2)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.sendEmptyMessage(r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                r0.mCanChangeChannel = r5
                goto L_0x04e1
            L_0x01cb:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r2 = r2.mListViewAdpter
                r0.setAdapter((android.widget.ListAdapter) r2)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                int r2 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                r0.setSelection(r2)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.showSelectedProgramInfo()
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.sendEmptyMessage(r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                r0.mCanChangeChannel = r5
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r0.isGetingData = r6
                goto L_0x04e1
            L_0x0212:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean r0 = r0.isGetingData
                if (r0 == 0) goto L_0x023a
                java.lang.String r0 = "EPGCnActivity"
                java.lang.String r1 = "AsyncTaskEventsLoad is readig event in provider>"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                return
            L_0x023a:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r0.isGetingData = r5
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                java.util.List r0 = r0.getGroup()
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.loadEvents()
                goto L_0x04e1
            L_0x0262:
                java.lang.String r0 = "EPGCnActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r3 = "EPGConfig.EPG_UPDATE_CHANNEL_LIST1111>"
                r1.append(r3)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r3 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r3 = r3.mListViewAdpter
                r1.append(r3)
                java.lang.String r3 = ">>"
                r1.append(r3)
                int r3 = r10.arg1
                r1.append(r3)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                int r0 = r10.arg1
                if (r0 != r2) goto L_0x02c3
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                java.util.List r0 = r0.getList()
                if (r0 == 0) goto L_0x02c3
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r0 = r0.mListViewAdpter
                java.util.List r0 = r0.getGroup()
                if (r0 == 0) goto L_0x02c3
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.modifyChannelListWithThread()
                goto L_0x04e1
            L_0x02c3:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.refreshChannelListWithThread()
                goto L_0x04e1
            L_0x02ca:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r0 = r0.mCurrentSelectedProgramInfo
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r1 = r1.mCurrentSelectedProgramInfo
                r0.setSubTitleImageViewState(r1)
                goto L_0x04e1
            L_0x02df:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.showSelectedProgramInfo()
                goto L_0x04e1
            L_0x02e6:
                java.lang.String r0 = "EPGCnActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "EPGConfig.EPG_SYNCHRONIZATION_MESSAGE>>"
                r1.append(r2)
                int r2 = r10.arg1
                r1.append(r2)
                java.lang.String r2 = "  "
                r1.append(r2)
                int r2 = r10.arg2
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                int r0 = r10.arg1
                int r1 = r10.arg2
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r2.changeTimeViewsShow(r0, r1)
                goto L_0x04e1
            L_0x0313:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r0.mListView
                r0.rawChangedOfChannel()
                goto L_0x04e1
            L_0x0326:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.TextView r0 = r0.mDataRetrievingShow
                r0.setVisibility(r3)
                goto L_0x04e1
            L_0x0331:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.TextView r0 = r0.mDataRetrievingShow
                r0.setVisibility(r6)
                goto L_0x04e1
            L_0x033c:
                java.lang.Object r0 = r10.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                int r1 = r0.param1
                if (r1 != 0) goto L_0x04e1
                java.lang.String r1 = "EPGCnActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r7 = "come in handleMessage BANNER_MSG_NAV value=== "
                r2.append(r7)
                int r7 = r0.param2
                r2.append(r7)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                int r1 = r0.param2
                r7 = 2000(0x7d0, double:9.88E-321)
                switch(r1) {
                    case 2: goto L_0x03cc;
                    case 3: goto L_0x03cc;
                    case 4: goto L_0x03cc;
                    case 5: goto L_0x0363;
                    case 6: goto L_0x0365;
                    case 7: goto L_0x0365;
                    case 8: goto L_0x0365;
                    case 9: goto L_0x0365;
                    case 10: goto L_0x0365;
                    case 11: goto L_0x0365;
                    case 12: goto L_0x0363;
                    case 13: goto L_0x0365;
                    case 14: goto L_0x0365;
                    default: goto L_0x0363;
                }
            L_0x0363:
                goto L_0x04e1
            L_0x0365:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r1.isSpecialState = r6
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r1.needFirstShowLock = r6
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.ImageView r1 = r1.mLockImageView
                int r1 = r1.getVisibility()
                if (r1 != 0) goto L_0x0384
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.ImageView r1 = r1.mLockImageView
                r1.setVisibility(r3)
            L_0x0384:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.TextView r1 = r1.mProgramNameTv
                int r1 = r1.getVisibility()
                if (r1 != 0) goto L_0x03b8
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.TextView r1 = r1.mProgramDetailTv
                int r1 = r1.getVisibility()
                if (r1 == 0) goto L_0x03b8
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.TextView r1 = r1.mProgramDetailTv
                r1.setVisibility(r6)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r1 = r1.mCurrentSelectedProgramInfo
                if (r1 == 0) goto L_0x03b8
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r2 = r2.mCurrentSelectedProgramInfo
                r1.setProgramDetailTvState(r2)
            L_0x03b8:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r1 = r1.mHandler
                r1.removeMessages(r4)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r1 = r1.mHandler
                r1.sendEmptyMessageDelayed(r4, r7)
                goto L_0x04e1
            L_0x03cc:
                int r1 = r0.param2
                if (r1 != r3) goto L_0x03e3
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                boolean r1 = r1.isTvSourceLock()
                if (r1 == 0) goto L_0x04e1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r1.finish()
                goto L_0x04e1
            L_0x03e3:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r1.isSpecialState = r5
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                boolean unused = r1.needFirstShowLock = r6
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r1 = r1.mHandler
                r1.removeMessages(r4)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r1 = r1.mHandler
                r1.sendEmptyMessageDelayed(r4, r7)
                goto L_0x04e1
            L_0x0401:
                java.lang.String r0 = "EPGCnActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r3 = "EPG update channel list>"
                r1.append(r3)
                java.lang.Object r3 = r10.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r3 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r3
                int r3 = r3.param2
                r1.append(r3)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                int r0 = r0.mLastChannelUpdateMsg
                r1 = 262(0x106, float:3.67E-43)
                if (r0 != r2) goto L_0x042b
                r9.removeMessages(r1)
                goto L_0x0438
            L_0x042b:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r0 = r0.mHandler
                boolean r0 = r0.hasMessages(r1)
                if (r0 == 0) goto L_0x0438
                return
            L_0x0438:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                java.lang.Object r2 = r10.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r2 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r2
                int r2 = r2.param2
                int unused = r0.mLastChannelUpdateMsg = r2
                android.os.Message r0 = android.os.Message.obtain()
                r0.what = r1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                int r1 = r1.mLastChannelUpdateMsg
                r0.arg1 = r1
                r1 = 3000(0xbb8, double:1.482E-320)
                r9.sendMessageDelayed(r0, r1)
                goto L_0x04e1
            L_0x0458:
                java.lang.Object r0 = r10.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                java.lang.String r1 = "EPGCnActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Epg MSG_CB_NFY_EVENT_UPDATE message:type:"
                r2.append(r3)
                int r3 = r0.param1
                r2.append(r3)
                java.lang.String r3 = "==>"
                r2.append(r3)
                int r3 = r0.param2
                r2.append(r3)
                java.lang.String r3 = "==>"
                r2.append(r3)
                int r3 = r0.param3
                r2.append(r3)
                java.lang.String r3 = "==>"
                r2.append(r3)
                int r3 = r0.param4
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.e(r1, r2)
                int r1 = r0.param1
                switch(r1) {
                    case 5: goto L_0x04ba;
                    case 6: goto L_0x0498;
                    default: goto L_0x0497;
                }
            L_0x0497:
                goto L_0x04e1
            L_0x0498:
                boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                if (r1 == 0) goto L_0x04ac
                java.lang.Thread r1 = new java.lang.Thread
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity$1$1 r2 = new com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity$1$1
                r2.<init>()
                r1.<init>(r2)
                r1.start()
                goto L_0x04e1
            L_0x04ac:
                java.lang.Thread r1 = new java.lang.Thread
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity$1$2 r2 = new com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity$1$2
                r2.<init>()
                r1.<init>(r2)
                r1.start()
                goto L_0x04e1
            L_0x04ba:
                boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                if (r1 != 0) goto L_0x04e1
                int r1 = r0.param2
                if (r1 > 0) goto L_0x04c8
                int r1 = r0.param3
                if (r1 <= 0) goto L_0x04e1
            L_0x04c8:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r1.updateProgramList()
                goto L_0x04e1
            L_0x04ce:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.widget.TextView r0 = r0.mCurrentDateTv
                java.lang.Object r1 = r10.obj
                java.lang.String r1 = (java.lang.String) r1
                r0.setText(r1)
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r0 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                r0.setCurrentDate()
            L_0x04e1:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.AnonymousClass1.handleMessage(android.os.Message):void");
        }
    };
    private HandlerThread mHandlerThead;
    /* access modifiers changed from: private */
    public boolean mHasInitListView;
    /* access modifiers changed from: private */
    public int mLastChannelUpdateMsg;
    private LinearLayout mLinearLayout;
    /* access modifiers changed from: private */
    public EPGListView mListView;
    /* access modifiers changed from: private */
    public EPGListViewAdapter mListViewAdpter;
    private LinearLayout mListViewLayout;
    private EPGChannelInfo mListViewSelectedChild;
    /* access modifiers changed from: private */
    public ImageView mLockImageView;
    private Runnable mModifyChannelListRunnable = new Runnable() {
        /* JADX WARNING: type inference failed for: r1v14, types: [java.util.List] */
        /* JADX WARNING: type inference failed for: r1v17, types: [java.util.List] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r3 = this;
                r0 = 0
                boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                if (r1 == 0) goto L_0x0015
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelListByTIF()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
                goto L_0x0022
            L_0x0015:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelList()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
            L_0x0022:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r1 = r1.mListView
                if (r1 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r1 = r1.mListView
                java.util.List r1 = r1.getList()
                if (r1 == 0) goto L_0x0061
                if (r0 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r1 = r1.mListViewAdpter
                if (r1 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r2 = r2.mListView
                java.util.List r2 = r2.getList()
                r1.updateChannList(r2, r0)
                r1 = 27
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r1
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity$5$1 r2 = new com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity$5$1
                r2.<init>()
                r1.runOnUiThread(r2)
            L_0x0061:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.AnonymousClass5.run():void");
        }
    };
    private MtkTvBanner mMtkTvBanner;
    private MyUpdata mMyUpData;
    /* access modifiers changed from: private */
    public boolean mNeedNowGetData;
    private TextView mNextDayTv;
    private TextView mPageInfoTv;
    private TextView mPrevDayTv;
    /* access modifiers changed from: private */
    public TextView mProgramDetailTv;
    /* access modifiers changed from: private */
    public TextView mProgramNameTv;
    private TextView mProgramRating;
    private TextView mProgramTimeTv;
    private TextView mProgramType;
    /* access modifiers changed from: private */
    public DataReader mReader;
    private Runnable mRefreshChannelListRunnable = new Runnable() {
        /* JADX WARNING: type inference failed for: r1v9, types: [java.util.List] */
        /* JADX WARNING: type inference failed for: r1v12, types: [java.util.List] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r6 = this;
                r0 = 0
                boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                if (r1 == 0) goto L_0x0015
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelListByTIF()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
                goto L_0x0022
            L_0x0015:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelList()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
            L_0x0022:
                if (r0 == 0) goto L_0x00b4
                int r1 = r0.size()
                if (r1 <= 0) goto L_0x00b4
                r1 = 0
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r2 = r2.mListView
                if (r2 == 0) goto L_0x0071
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r2 = r2.mListView
                int r3 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                java.lang.Object r2 = r2.getItemAtPosition(r3)
                com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r2 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r2
                if (r2 == 0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r3 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r2.getTVChannel()
                int r4 = r4.getChannelId()
                boolean r3 = r3.isChannelExit(r4)
                if (r3 == 0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r3 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r2.getTVChannel()
                int r1 = r3.getChannelPosition(r4)
                goto L_0x0070
            L_0x0066:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r3 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                int r1 = r3.getCurrentPlayChannelPosition()
            L_0x0070:
                goto L_0x007b
            L_0x0071:
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r2 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r2 = r2.mReader
                int r1 = r2.getCurrentPlayChannelPosition()
            L_0x007b:
                int r2 = r1 / 6
                int r2 = r2 + 1
                java.lang.String r3 = "EPGCnActivity"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "re setAdapter.>index>>>"
                r4.append(r5)
                r4.append(r1)
                java.lang.String r5 = "   pageNum>>"
                r4.append(r5)
                r4.append(r2)
                java.lang.String r4 = r4.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.e(r3, r4)
                android.os.Message r3 = android.os.Message.obtain()
                r3.arg1 = r1
                r3.arg2 = r2
                r3.obj = r0
                r4 = 278(0x116, float:3.9E-43)
                r3.what = r4
                com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r4 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                android.os.Handler r4 = r4.mHandler
                r4.sendMessage(r3)
            L_0x00b4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.AnonymousClass6.run():void");
        }
    };
    private LinearLayout mRootLayout;
    private TextView mSelectedDateTv;
    private ImageView mSttlImageView;
    private Handler mThreadHandler;
    private int mTotalPage;
    private TextView mTypeFilter;
    private BroadcastReceiver mUpdateEventReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (CommonIntegration.supportTIFFunction()) {
                String actionName = intent.getAction();
                MtkLog.d(EPGCnActivity.TAG, "actionName>>>" + actionName);
                if (actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN) || actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF)) {
                    int channelId = intent.getIntExtra("channel_id", 0);
                    if (EPGCnActivity.this.mListViewAdpter == null || !EPGCnActivity.this.mListViewAdpter.containsChannelId(channelId)) {
                        MtkLog.d(EPGCnActivity.TAG, "containsChannelId false find the channel id");
                        return;
                    }
                    MtkLog.d(EPGCnActivity.TAG, "containsChannelId true find the channel id");
                    if (!actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF) && actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN)) {
                        MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad read mHandler.hasMessages(EPGConfig.EPG_GET_TIF_EVENT_LIST)>>" + EPGCnActivity.this.mHandler.hasMessages(263) + ">>>" + EPGCnActivity.this.mHandler.hasMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG) + ">>>" + EPGCnActivity.this.isGetingData + ">>>" + EPGCnActivity.this.mListViewAdpter.isAlreadyGetAll());
                        int delayTime = 1000;
                        if (!EPGCnActivity.this.mListViewAdpter.isAlreadyGetAll()) {
                            EPGCnActivity.this.mListViewAdpter.addAlreadyChnnelId(channelId);
                            if (!EPGCnActivity.this.mHandler.hasMessages(263)) {
                                if (!(EPGCnActivity.this.mDataRetrievingShow == null || EPGCnActivity.this.mDataRetrievingShow.getVisibility() == 0)) {
                                    EPGCnActivity.this.mHandler.sendEmptyMessage(4);
                                }
                                if (EPGCnActivity.this.isGetingData) {
                                    delayTime = 3000;
                                }
                                EPGCnActivity.this.mHandler.sendEmptyMessageDelayed(263, (long) delayTime);
                            }
                        } else if (!EPGCnActivity.this.mHandler.hasMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG)) {
                            EPGCnActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG, (long) ChannelListDialog.DEFAULT_CHANGE_CHANNEL_DELAY_TIME);
                        }
                    }
                }
            }
        }
    };
    private boolean mUpdateOneByOne = true;
    private TextView mViewDetailTv;
    /* access modifiers changed from: private */
    public boolean needFirstShowLock = true;
    private String[] preValues = new String[3];

    /* access modifiers changed from: private */
    public void updateProgramList() {
        if (!this.mNeedNowGetData) {
            EPGConfig.FROM_WHERE = 27;
            this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
            this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_UPDATE_API_EVENT_LIST, 1000);
        }
    }

    private void updateEvent(TvCallbackData data) {
        List<EPGChannelInfo> mGroup;
        List<EPGProgramInfo> programList;
        int index;
        if (this.mListViewAdpter != null && (mGroup = this.mListViewAdpter.getGroup()) != null) {
            MtkLog.d(TAG, "updateEvent()");
            int size = mGroup.size();
            for (EPGChannelInfo tempChannelInfo : mGroup) {
                List<EPGProgramInfo> tempInfoList = tempChannelInfo.getmTVProgramInfoList();
                if (tempInfoList != null) {
                    for (EPGProgramInfo tempPrograminfo : tempInfoList) {
                        if (tempPrograminfo.getChannelId() == data.param2 && tempPrograminfo.getProgramId() == data.param3 && (programList = this.mReader.getChannelProgramList(tempChannelInfo.getTVChannel(), this.mListViewAdpter.getDayNum(), this.mListViewAdpter.getStartHour(), 2)) != null) {
                            for (EPGProgramInfo tempNewinfo : programList) {
                                if (tempNewinfo.getProgramId() == data.param3 && (index = tempInfoList.indexOf(tempPrograminfo)) != -1) {
                                    tempInfoList.set(index, tempNewinfo);
                                    showSelectedProgramInfo();
                                    return;
                                }
                            }
                            continue;
                        }
                    }
                    continue;
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DestroyApp) getApplication()).add(this);
        requestWindowFeature(1);
        Window mWindow = getWindow();
        if (mWindow != null) {
            mWindow.setFlags(1024, 1024);
        }
        setContentView(R.layout.epg_cn_main);
        this.mLinearLayout = (LinearLayout) findViewById(R.id.epg_root_layout);
        if (!CommonIntegration.getInstance().isContextInit()) {
            MtkLog.d(TAG, "init common integergration context");
            CommonIntegration.getInstance().setContext(getApplicationContext());
        }
        this.mHandlerThead = new HandlerThread(TAG);
        this.mHandlerThead.start();
        this.mThreadHandler = new Handler(this.mHandlerThead.getLooper());
        this.mReader = DataReader.getInstance(this);
        this.mReader.loadProgramType();
        this.mReader.loadMonthAndWeekRes();
        this.mMtkTvBanner = MtkTvBanner.getInstance();
        this.mEPGPwdDialog = new EPGPwdDialog(this);
        this.mEPGPwdDialog.setOnDismissListener(this);
        this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
        initUI();
        this.mListViewAdpter.setStartHour(EPGUtil.getCurrentHour());
        changeTimeViewsShow(this.dayNum, this.mListViewAdpter.getStartHour());
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CONFIG, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        registerUpdateReceiver();
        setCurrentDate();
        EPGConfig.init = true;
        EPGConfig.avoidFoucsChange = false;
    }

    private void registerUpdateReceiver() {
        IntentFilter updateIntentFilter = new IntentFilter();
        updateIntentFilter.addAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF);
        updateIntentFilter.addAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN);
        registerReceiver(this.mUpdateEventReceiver, updateIntentFilter);
    }

    private void initUI() {
        this.mDataRetrievingShow = (TextView) findViewById(R.id.epg_retrieving_data);
        this.mCurrentDateTv = (TextView) findViewById(R.id.epg_top_date_info_tv);
        this.mSelectedDateTv = (TextView) findViewById(R.id.epg_title_date_selected_tv);
        this.mBeginTimeTv = (TextView) findViewById(R.id.epg_title_time_begin_tv);
        this.mEndTimeTv = (TextView) findViewById(R.id.epg_title_time_end_tv);
        this.mProgramNameTv = (TextView) findViewById(R.id.epg_program_info_name);
        this.mProgramTimeTv = (TextView) findViewById(R.id.epg_program_info_time);
        this.mProgramDetailTv = (TextView) findViewById(R.id.epg_program_info_detail);
        this.mProgramDetailTv.setLines(4);
        this.mProgramType = (TextView) findViewById(R.id.epg_program_info_type);
        this.mProgramRating = (TextView) findViewById(R.id.epg_program_rating);
        this.mPageInfoTv = (TextView) findViewById(R.id.epg_info_page_tv);
        this.mPrevDayTv = (TextView) findViewById(R.id.epg_bottom_prev_day_tv);
        this.mNextDayTv = (TextView) findViewById(R.id.epg_bottom_next_day_tv);
        this.mViewDetailTv = (TextView) findViewById(R.id.epg_bottom_view_detail);
        this.mTypeFilter = (TextView) findViewById(R.id.epg_bottom_view_filter);
        this.mLockImageView = (ImageView) findViewById(R.id.epg_info_lock_icon);
        this.mSttlImageView = (ImageView) findViewById(R.id.epg_info_sttl_icon);
        this.mRootLayout = (LinearLayout) findViewById(R.id.epg_root_layout);
        this.mListViewLayout = (LinearLayout) findViewById(R.id.epg_listview_layout);
        this.mListView = (EPGListView) findViewById(R.id.epg_program_forecast_listview);
        this.mListView.setHandler(this.mHandler);
        this.mListViewAdpter = new EPGListViewAdapter(this, EPGUtil.getCurrentHour());
        this.mProgramDetailTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        this.mMyUpData = new MyUpdata();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        display.getMetrics(mDisplayMetrics);
        int width = ((int) ((((float) display.getWidth()) - (46.0f * mDisplayMetrics.density)) * 0.75f)) - 2;
        this.mListViewAdpter.setWidth(width);
        MtkLog.d(TAG, "List View Program Info Total Width----------------->" + width);
        this.mRootLayout.requestFocus();
        this.mListViewLayout.requestFocus();
        if (!this.mListView.hasFocus()) {
            MtkLog.d(TAG, "[ListView has not focus]");
            this.mListView.requestFocus();
            this.mListView.setSelection(0);
        } else {
            MtkLog.d(TAG, "[ListView has  focus]");
        }
        if (this.mListView.hasFocus()) {
            MtkLog.d(TAG, "[The second time, ListView has focus]");
        }
    }

    /* access modifiers changed from: private */
    public void modifyChannelListWithThread() {
        if (this.mThreadHandler != null && this.mModifyChannelListRunnable != null) {
            this.mThreadHandler.post(this.mModifyChannelListRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void refreshChannelListWithThread() {
        if (this.mThreadHandler != null && this.mRefreshChannelListRunnable != null) {
            this.mThreadHandler.post(this.mRefreshChannelListRunnable);
        }
    }

    private void getChannelListWithThread() {
        new Thread(new Runnable() {
            /* JADX WARNING: type inference failed for: r1v7, types: [java.util.List] */
            /* JADX WARNING: type inference failed for: r1v10, types: [java.util.List] */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r5 = this;
                    r0 = 0
                    boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                    if (r1 == 0) goto L_0x0015
                    com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelListByTIF()
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                    goto L_0x0022
                L_0x0015:
                    com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelList()
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                L_0x0022:
                    if (r0 == 0) goto L_0x004f
                    int r1 = r0.size()
                    if (r1 <= 0) goto L_0x004f
                    com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r1 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    int r1 = r1.getCurrentPlayChannelPosition()
                    int r2 = r1 / 6
                    int r2 = r2 + 1
                    android.os.Message r3 = android.os.Message.obtain()
                    r3.arg1 = r1
                    r3.arg2 = r2
                    r3.obj = r0
                    r4 = 275(0x113, float:3.85E-43)
                    r3.what = r4
                    com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity r4 = com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.this
                    android.os.Handler r4 = r4.mHandler
                    r4.sendMessage(r3)
                L_0x004f:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.AnonymousClass7.run():void");
            }
        }).start();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        StringBuilder sb = new StringBuilder();
        sb.append("onResume mListViewAdpter>>");
        sb.append(this.mListViewAdpter == null ? null : this.mListViewAdpter.getGroup());
        MtkLog.d(TAG, sb.toString());
        if (this.mListViewAdpter != null && this.mListViewAdpter.getGroup() == null) {
            getChannelListWithThread();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        MtkLog.i(TAG, "EPG on Pause");
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.i(TAG, "EPG on onStop");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        MtkLog.d(TAG, "onDestroy()");
        clearData();
        ((DestroyApp) getApplication()).remove(this);
    }

    class MyUpdata implements EPGListView.UpDateListView {
        MyUpdata() {
        }

        public void updata(boolean isNext) {
            if (EPGCnActivity.this.mHasInitListView) {
                EPGCnActivity.this.mHandler.removeMessages(4);
                EPGCnActivity.this.mHandler.sendEmptyMessage(4);
                EPGCnActivity.this.mListViewAdpter.clearWindowList();
                EPGCnActivity.this.mHandler.removeMessages(263);
                EPGCnActivity.this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
                if (EPGCnActivity.this.eventLoad != null) {
                    EPGCnActivity.this.eventLoad.cancel(true);
                    AsyncTaskEventsLoad unused = EPGCnActivity.this.eventLoad = null;
                }
                boolean unused2 = EPGCnActivity.this.isGetingData = false;
                List<?> currentList = EPGCnActivity.this.mListView.getCurrentList();
                if (isNext) {
                    EPGConfig.SELECTED_CHANNEL_POSITION = 0;
                } else {
                    EPGConfig.SELECTED_CHANNEL_POSITION = currentList.size() - 1;
                }
                EPGCnActivity.this.mListViewAdpter.setGroup(currentList);
                EPGCnActivity.this.mListView.setAdapter((ListAdapter) EPGCnActivity.this.mListViewAdpter);
                EPGCnActivity.this.mListView.setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAdapter(List<?> adpter, int dayNum2, int pageNum) {
        this.mListView.initData(adpter, 6, this.mMyUpData, pageNum);
        this.mListViewAdpter.setGroup(this.mListView.getCurrentList());
        this.mListView.updateEnablePosition(this.mListViewAdpter);
    }

    /* access modifiers changed from: private */
    public void setAdapter(List<?> adpter, int dayNum2, int pageNum) {
        if (this.mListView != null && this.mListViewAdpter != null) {
            this.mListView.initData(adpter, 6, this.mMyUpData, pageNum);
            this.mHasInitListView = true;
            this.mListViewAdpter.setGroup(this.mListView.getCurrentList());
            this.mListView.setAdapter((ListAdapter) this.mListViewAdpter);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentDate() {
        if (CommonIntegration.getInstance().isCurrentSourceATV()) {
            MtkLog.d(TAG, "isCurrentSourceATV~");
            this.mCurrentDateTv.setVisibility(4);
        } else {
            this.mCurrentDateTv.setVisibility(0);
        }
        if (this.mThreadHandler != null && this.mGetCurrentTimeRunnable != null) {
            this.mThreadHandler.post(this.mGetCurrentTimeRunnable);
        }
    }

    private void clearData() {
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        if (this.mHandler != null) {
            this.mHandler.removeMessages(262);
        }
        EPGLinearLayout.mCurrentSelectPosition = 0;
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CONFIG, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        if (this.mUpdateEventReceiver != null) {
            unregisterReceiver(this.mUpdateEventReceiver);
            this.mUpdateEventReceiver = null;
        }
        if (this.mListViewAdpter != null) {
            this.mListViewAdpter.clearWindowList();
        }
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1001);
            this.mHandler.removeMessages(263);
            this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
            this.mHandler.removeMessages(EPGConfig.EPG_SET_LIST_ADAPTER);
            this.mHandler.removeMessages(4);
        }
        if (this.mThreadHandler != null) {
            this.mThreadHandler.removeCallbacks(this.mGetEventsRunnable);
            this.mThreadHandler.removeCallbacks(this.mGetCurrentTimeRunnable);
            this.mThreadHandler.removeCallbacks(this.mModifyChannelListRunnable);
            this.mThreadHandler.removeCallbacks(this.mRefreshChannelListRunnable);
            this.mHandlerThead.quit();
            this.mGetEventsRunnable = null;
            this.mGetCurrentTimeRunnable = null;
            this.mModifyChannelListRunnable = null;
            this.mRefreshChannelListRunnable = null;
            this.mThreadHandler = null;
        }
        if (this.eventLoad != null) {
            this.eventLoad.cancel(true);
            this.eventLoad = null;
        }
        MtkTvEvent.getInstance().clearActiveWindows();
        if (this.mListViewAdpter != null) {
            this.mListViewAdpter = null;
        }
        if (this.mListView != null) {
            this.mListView = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x008b, code lost:
        changeBottomViewText(true, r9);
        new com.mediatek.wwtv.tvcenter.epg.cn.EpgType(r8).show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0096, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0099, code lost:
        if (r8.mTotalPage <= 1) goto L_0x00d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009b, code lost:
        r8.mCurrentPage++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a4, code lost:
        if (r8.mCurrentPage <= r8.mTotalPage) goto L_0x00a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a6, code lost:
        r8.mCurrentPage = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a8, code lost:
        r8.mProgramDetailTv.scrollTo(0, ((r8.mCurrentPage - 1) * 4) * r8.mProgramDetailTv.getLineHeight());
        r8.mPageInfoTv.setText(r8.mCurrentPage + "/" + r8.mTotalPage);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00d5, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00d6, code lost:
        r8.dayNum = r8.mListViewAdpter.getDayNum();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00e2, code lost:
        if (r8.dayNum != 8) goto L_0x00e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00e4, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00e5, code lost:
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = false;
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = 23;
        r8.dayNum++;
        r8.mListViewAdpter.setDayNum(r8.dayNum);
        r8.mListViewAdpter.setStartHour(0);
        r8.mCanChangeTimeShow = true;
        r8.mHandler.removeMessages(4);
        r8.mHandler.sendEmptyMessage(4);
        r8.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r8.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0112, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0113, code lost:
        r8.dayNum = r8.mListViewAdpter.getDayNum();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x011d, code lost:
        if (r8.dayNum != 0) goto L_0x0120;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x011f, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0120, code lost:
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = false;
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = 24;
        r8.dayNum--;
        r8.mListViewAdpter.setDayNum(r8.dayNum);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0134, code lost:
        if (r8.dayNum != 0) goto L_0x0140;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0136, code lost:
        r8.mListViewAdpter.setStartHour(com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0140, code lost:
        r8.mListViewAdpter.setStartHour(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0145, code lost:
        r8.mCanChangeTimeShow = true;
        r8.mHandler.removeMessages(4);
        r8.mHandler.sendEmptyMessage(4);
        r8.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r8.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x015b, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r9, android.view.KeyEvent r10) {
        /*
            r8 = this;
            java.lang.String r0 = "EPGCnActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "event.getRepeatCount()>>>"
            r1.append(r2)
            int r2 = r10.getRepeatCount()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 500(0x1f4, double:2.47E-321)
            r2 = 274(0x112, float:3.84E-43)
            r3 = 4
            r4 = 0
            r5 = 1
            switch(r9) {
                case 183: goto L_0x0113;
                case 184: goto L_0x00d6;
                case 185: goto L_0x0097;
                case 186: goto L_0x008b;
                default: goto L_0x0024;
            }
        L_0x0024:
            switch(r9) {
                case 4: goto L_0x0081;
                case 23: goto L_0x004a;
                case 30: goto L_0x008b;
                case 33: goto L_0x004a;
                case 35: goto L_0x00d6;
                case 46: goto L_0x0113;
                case 53: goto L_0x0097;
                case 66: goto L_0x004a;
                case 82: goto L_0x0049;
                case 93: goto L_0x002d;
                case 130: goto L_0x002d;
                case 172: goto L_0x0081;
                case 178: goto L_0x002c;
                default: goto L_0x0027;
            }
        L_0x0027:
            boolean r0 = super.onKeyDown(r9, r10)
            return r0
        L_0x002c:
            return r5
        L_0x002d:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r0 = r0.isCurrentSourceDTV()
            if (r0 == 0) goto L_0x003b
            r8.calledByScheduleList()
            goto L_0x0048
        L_0x003b:
            android.content.Context r0 = r8.getApplicationContext()
            java.lang.String r1 = "Source not available,please change to DTV source"
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r4)
            r0.show()
        L_0x0048:
            return r5
        L_0x0049:
            return r5
        L_0x004a:
            java.lang.String r0 = "EPGCnActivity"
            java.lang.String r1 = "KEYCODE_DPAD_CENTER"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListView r0 = r8.mListView
            int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            java.lang.Object r0 = r0.getItemAtPosition(r1)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r0 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r0
            r8.mListViewSelectedChild = r0
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r0 = r8.mListViewSelectedChild
            if (r0 == 0) goto L_0x0080
            com.mediatek.twoworlds.tv.MtkTvPWDDialog r0 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
            int r0 = r0.PWDShow()
            if (r0 != 0) goto L_0x0080
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r1 = r8.mEPGPwdDialog
            r1.show()
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r1 = r8.mEPGPwdDialog
            r1.sendAutoDismissMessage()
            r8.changeBottomViewText(r5, r9)
            r8.setProgramInfoViewsInVisiable()
            android.widget.ImageView r1 = r8.mLockImageView
            r1.setVisibility(r3)
        L_0x0080:
            return r5
        L_0x0081:
            int r0 = r10.getRepeatCount()
            if (r0 > 0) goto L_0x008a
            r8.finish()
        L_0x008a:
            return r5
        L_0x008b:
            r8.changeBottomViewText(r5, r9)
            com.mediatek.wwtv.tvcenter.epg.cn.EpgType r0 = new com.mediatek.wwtv.tvcenter.epg.cn.EpgType
            r0.<init>(r8)
            r0.show()
            return r5
        L_0x0097:
            int r0 = r8.mTotalPage
            if (r0 <= r5) goto L_0x00d5
            int r0 = r8.mCurrentPage
            int r0 = r0 + r5
            r8.mCurrentPage = r0
            int r0 = r8.mCurrentPage
            int r1 = r8.mTotalPage
            if (r0 <= r1) goto L_0x00a8
            r8.mCurrentPage = r5
        L_0x00a8:
            android.widget.TextView r0 = r8.mProgramDetailTv
            int r1 = r8.mCurrentPage
            int r1 = r1 - r5
            int r1 = r1 * r3
            android.widget.TextView r2 = r8.mProgramDetailTv
            int r2 = r2.getLineHeight()
            int r1 = r1 * r2
            r0.scrollTo(r4, r1)
            android.widget.TextView r0 = r8.mPageInfoTv
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r8.mCurrentPage
            r1.append(r2)
            java.lang.String r2 = "/"
            r1.append(r2)
            int r2 = r8.mTotalPage
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setText(r1)
        L_0x00d5:
            return r5
        L_0x00d6:
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r6 = r6.getDayNum()
            r8.dayNum = r6
            int r6 = r8.dayNum
            r7 = 8
            if (r6 != r7) goto L_0x00e5
            return r4
        L_0x00e5:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r6 = 23
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r6
            int r6 = r8.dayNum
            int r6 = r6 + r5
            r8.dayNum = r6
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r7 = r8.dayNum
            r6.setDayNum(r7)
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r6 = r8.mListViewAdpter
            r6.setStartHour(r4)
            r8.mCanChangeTimeShow = r5
            android.os.Handler r4 = r8.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r8.mHandler
            r4.sendEmptyMessage(r3)
            android.os.Handler r3 = r8.mHandler
            r3.removeMessages(r2)
            android.os.Handler r3 = r8.mHandler
            r3.sendEmptyMessageDelayed(r2, r0)
            return r5
        L_0x0113:
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r6 = r6.getDayNum()
            r8.dayNum = r6
            int r6 = r8.dayNum
            if (r6 != 0) goto L_0x0120
            return r4
        L_0x0120:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r6 = 24
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r6
            int r6 = r8.dayNum
            int r6 = r6 - r5
            r8.dayNum = r6
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r7 = r8.dayNum
            r6.setDayNum(r7)
            int r6 = r8.dayNum
            if (r6 != 0) goto L_0x0140
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r4 = r8.mListViewAdpter
            int r6 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour()
            r4.setStartHour(r6)
            goto L_0x0145
        L_0x0140:
            com.mediatek.wwtv.tvcenter.epg.cn.EPGListViewAdapter r6 = r8.mListViewAdpter
            r6.setStartHour(r4)
        L_0x0145:
            r8.mCanChangeTimeShow = r5
            android.os.Handler r4 = r8.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r8.mHandler
            r4.sendEmptyMessage(r3)
            android.os.Handler r3 = r8.mHandler
            r3.removeMessages(r2)
            android.os.Handler r3 = r8.mHandler
            r3.sendEmptyMessageDelayed(r2, r0)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyUp>>>>" + keyCode + "  " + event.getAction());
        switch (keyCode) {
            case 24:
            case 25:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /* access modifiers changed from: private */
    public void changeTimeViewsShow(int dayNum2, int startHour) {
        this.mSelectedDateTv.setText(EPGUtil.getSimpleDate(EPGUtil.getCurrentTime() + Long.valueOf(86400 * ((long) dayNum2)).longValue()));
        if (dayNum2 == 0) {
            this.mPrevDayTv.setText("");
        } else {
            this.mPrevDayTv.setText(getString(R.string.epg_bottom_prev_day));
        }
        if (dayNum2 == 8) {
            this.mNextDayTv.setText("");
        } else {
            this.mNextDayTv.setText(getString(R.string.epg_bottom_next_day));
        }
        TextView textView = this.mBeginTimeTv;
        textView.setText((EPGUtil.getEUIntervalHour(dayNum2, startHour, 0) % 24) + ":00");
        TextView textView2 = this.mEndTimeTv;
        textView2.setText((EPGUtil.getEUIntervalHour(dayNum2, startHour, 1) % 24) + ":00");
    }

    /* access modifiers changed from: private */
    public void updateEventList(boolean needSetActiveWindow) {
        if (this.mListViewAdpter != null) {
            this.mListView.mCanChangeChannel = false;
            this.mNeedNowGetData = false;
            if (needSetActiveWindow) {
                this.mListViewAdpter.setActiveWindow();
            } else if (!CommonIntegration.supportTIFFunction()) {
                new Thread(new Runnable() {
                    public void run() {
                        MtkLog.d(EPGCnActivity.TAG, "5start read event from api>" + EPGCnActivity.this.mListViewAdpter.getDayNum() + "   " + EPGCnActivity.this.mListViewAdpter.getStartHour());
                        EPGCnActivity.this.mReader.readChannelProgramInfoByTime(EPGCnActivity.this.mListViewAdpter.getGroup(), EPGCnActivity.this.mListViewAdpter.getDayNum(), EPGCnActivity.this.mListViewAdpter.getStartHour(), 2);
                        EPGCnActivity.this.mHandler.removeMessages(EPGConfig.EPG_SET_LIST_ADAPTER);
                        EPGCnActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_SET_LIST_ADAPTER, 100);
                    }
                }).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setLockIconVisibility(boolean isVisible) {
        MtkLog.d(TAG, "setLockIconVisibility>>>" + isVisible);
        if (isVisible) {
            if (this.mEPGPwdDialog == null || !this.mEPGPwdDialog.isShowing()) {
                this.mLockImageView.setVisibility(0);
            } else {
                this.mLockImageView.setVisibility(4);
            }
            this.mSttlImageView.setVisibility(4);
            if (this.mProgramDetailTv.getVisibility() == 0) {
                this.mProgramDetailTv.setVisibility(4);
                return;
            }
            return;
        }
        if (this.mLockImageView.getVisibility() == 0) {
            this.mLockImageView.setVisibility(4);
        }
        this.mHandler.removeMessages(EPGConfig.EPG_PROGRAM_STTL_SHOW);
        this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAM_STTL_SHOW, 1000);
    }

    /* access modifiers changed from: private */
    public void showSelectedProgramInfo() {
        if (this.mListViewAdpter != null) {
            this.mListViewSelectedChild = (EPGChannelInfo) this.mListView.getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
            if (this.mListViewSelectedChild != null) {
                EPGLinearLayout childView = this.mListView.getSelectedDynamicLinearLayout(EPGConfig.SELECTED_CHANNEL_POSITION);
                List<EPGProgramInfo> mProgramList = this.mListViewSelectedChild.getmTVProgramInfoList();
                if (this.mEPGPwdDialog != null && this.mEPGPwdDialog.isShowing()) {
                    this.mEPGPwdDialog.dismiss();
                    MtkLog.d(TAG, "do dismiss pwd dialog!");
                }
                if (this.needFirstShowLock) {
                    setLockImageViewState(this.mListViewSelectedChild.getTVChannel());
                }
                if (mProgramList != null && childView != null && mProgramList.size() > 0 && childView.getmCurrentSelectPosition() < mProgramList.size() && childView.getmCurrentSelectPosition() >= 0) {
                    this.mCurrentSelectedProgramInfo = mProgramList.get(childView.getmCurrentSelectPosition());
                    if (this.mCurrentSelectedProgramInfo != null) {
                        this.mProgramNameTv.setVisibility(0);
                        this.mProgramNameTv.setText(this.mCurrentSelectedProgramInfo.getTitle());
                        this.mProgramTimeTv.setVisibility(0);
                        this.mProgramTimeTv.setText(EPGTimeConvert.getInstance().formatProgramTimeInfo(this.mCurrentSelectedProgramInfo, EPGUtil.judgeFormatTime12_24(this)));
                        setProgramDetailTvState(this.mCurrentSelectedProgramInfo);
                        if (this.mCurrentSelectedProgramInfo.getMainType() >= 1) {
                            this.mProgramType.setText(this.mReader.getMainType()[this.mCurrentSelectedProgramInfo.getMainType() - 1]);
                        } else {
                            this.mProgramType.setText(getString(R.string.epg_info_program_type));
                        }
                        this.mProgramRating.setVisibility(0);
                        this.mProgramRating.setText(this.mCurrentSelectedProgramInfo.getRatingType());
                        if (!this.isSpecialState) {
                            setSubTitleImageViewState(this.mCurrentSelectedProgramInfo);
                        } else if (this.mSttlImageView.getVisibility() != 4) {
                            this.mSttlImageView.setVisibility(4);
                        }
                        if (this.mEPGPwdDialog != null && this.mEPGPwdDialog.isShowing()) {
                            setProgramInfoViewsInVisiable();
                            return;
                        }
                        return;
                    }
                    setProgramInfoViewsInVisiable();
                } else if (childView == null) {
                    MtkLog.d(TAG, "childView == null this.isFinishing():" + isFinishing());
                    if (!isFinishing()) {
                        this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                        this.mHandler.sendEmptyMessage(EPGConfig.EPG_PROGRAMINFO_SHOW);
                    }
                } else {
                    setProgramInfoViewsInVisiable();
                }
            } else {
                setProgramInfoViewsInVisiable();
            }
        } else {
            setProgramInfoViewsInVisiable();
        }
    }

    private void setProgramInfoViewsInVisiable() {
        this.mProgramNameTv.setVisibility(4);
        this.mProgramTimeTv.setVisibility(4);
        this.mProgramDetailTv.setVisibility(4);
        this.mSttlImageView.setVisibility(4);
        this.mProgramRating.setVisibility(4);
        this.mPageInfoTv.setText("");
        this.mViewDetailTv.setText("");
        this.mProgramType.setText("");
        this.mProgramRating.setText("");
    }

    private void setLockImageViewState(MtkTvChannelInfoBase mChannel) {
        if (mChannel == null) {
            return;
        }
        if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
            this.isSpecialState = true;
            this.mLockImageView.setVisibility(0);
            return;
        }
        this.isSpecialState = false;
        this.mLockImageView.setVisibility(4);
    }

    private boolean isShowSTTLIcon() {
        boolean showCaptionIcon = this.mMtkTvBanner.isDisplayCaptionIcon();
        MtkLog.d(TAG, "come in isShowCaptionIcon, value == " + showCaptionIcon);
        return showCaptionIcon;
    }

    /* access modifiers changed from: private */
    public void setSubTitleImageViewState(EPGProgramInfo childProgramInfo) {
        if (childProgramInfo == null || this.mListView == null || this.mListView.getCurrentChannel() == null) {
            this.mSttlImageView.setVisibility(4);
            return;
        }
        MtkLog.d(TAG, "setSubTitleImageViewState== " + childProgramInfo.isHasSubTitle() + "  " + this.mMtkTvBanner.isDisplayCaptionIcon());
        Long time = Long.valueOf(EPGUtil.getCurrentTime());
        List<EPGProgramInfo> mChildViewData = this.mListView.getCurrentChannel().getmTVProgramInfoList();
        if (mChildViewData != null) {
            boolean hasFind = false;
            Iterator<EPGProgramInfo> it = mChildViewData.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                EPGProgramInfo tempEpgProgram = it.next();
                if (tempEpgProgram.getChannelId() == childProgramInfo.getChannelId() && tempEpgProgram.getProgramId() == childProgramInfo.getProgramId()) {
                    hasFind = true;
                    break;
                }
            }
            if (!hasFind) {
                this.mSttlImageView.setVisibility(4);
            } else if (time.longValue() < childProgramInfo.getmStartTime().longValue() || time.longValue() > childProgramInfo.getmEndTime().longValue()) {
                if (childProgramInfo.isHasSubTitle()) {
                    this.mSttlImageView.setVisibility(0);
                } else {
                    this.mSttlImageView.setVisibility(4);
                }
            } else if (isShowSTTLIcon()) {
                this.mSttlImageView.setVisibility(0);
            } else {
                this.mSttlImageView.setVisibility(4);
            }
        } else {
            this.mSttlImageView.setVisibility(4);
        }
    }

    /* access modifiers changed from: private */
    public void setProgramDetailTvState(EPGProgramInfo childProgramInfo) {
        int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
        MtkLog.d(TAG, "setProgramDetailTvState>>" + this.isSpecialState + "   >>" + showFlag);
        if (showFlag == 0 && this.mLockImageView.getVisibility() != 0) {
            setLockIconVisibility(true);
        }
        if (showFlag == 0 || this.isSpecialState) {
            this.mProgramDetailTv.setVisibility(4);
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
        } else if (childProgramInfo == null || childProgramInfo.getDescribe() == null) {
            this.mProgramDetailTv.setText("");
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
            this.mTotalPage = 0;
        } else {
            this.mProgramDetailTv.setVisibility(0);
            String mDetailContent = childProgramInfo.getDescribe();
            this.mProgramDetailTv.setText(mDetailContent);
            if (TextUtils.isEmpty(mDetailContent)) {
                this.mViewDetailTv.setText("");
                this.mPageInfoTv.setText("");
            }
            initProgramDetailContent();
        }
    }

    /* access modifiers changed from: private */
    public void initProgramDetailContent() {
        int line = this.mProgramDetailTv.getLineCount();
        MtkLog.d(TAG, "--- initProgramDetailContent()---- Lines: " + line);
        if (line > 0) {
            this.mTotalPage = line % 4 == 0 ? line / 4 : (line / 4) + 1;
            this.mCurrentPage = 1;
            this.mProgramDetailTv.scrollTo(0, (this.mCurrentPage - 1) * 4 * this.mProgramDetailTv.getLineHeight());
            if (this.mTotalPage > 1) {
                TextView textView = this.mPageInfoTv;
                textView.setText(this.mCurrentPage + "/" + this.mTotalPage);
                this.mViewDetailTv.setText(getResources().getString(R.string.epg_bottom_view_detail));
                return;
            }
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
            return;
        }
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                EPGCnActivity.this.initProgramDetailContent();
            }
        }, 10);
    }

    public void changeBottomViewText(boolean isEnter, int keyCode) {
        if (isEnter) {
            savePreValues();
            this.mPrevDayTv.setText("");
            this.mNextDayTv.setText("");
            this.mViewDetailTv.setText("");
            if (keyCode != 23) {
                if (keyCode != 30) {
                    if (!(keyCode == 33 || keyCode == 66)) {
                        if (keyCode != 186) {
                            return;
                        }
                    }
                }
                this.mTypeFilter.setText(getResources().getString(R.string.setup_exit));
                return;
            }
            this.mTypeFilter.setText("");
            return;
        }
        this.mPrevDayTv.setText(this.preValues[0]);
        this.mNextDayTv.setText(this.preValues[1]);
        this.mViewDetailTv.setText(this.preValues[2]);
        this.mTypeFilter.setText(getResources().getString(R.string.epg_bottom_type_filter));
    }

    private void savePreValues() {
        this.preValues[0] = this.mPrevDayTv.getText().toString();
        this.preValues[1] = this.mNextDayTv.getText().toString();
        this.preValues[2] = this.mViewDetailTv.getText().toString();
    }

    public void setIsNeedFirstShowLock(boolean isNeedFirstShowLock) {
        this.needFirstShowLock = isNeedFirstShowLock;
    }

    public void onDismiss(DialogInterface dialog) {
        MtkLog.d(TAG, "PWD onDismiss!!>>" + this.needFirstShowLock);
        showSelectedProgramInfo();
        changeBottomViewText(false, 0);
    }

    /* access modifiers changed from: private */
    public void refreshUpdateLayout(List<EPGChannelInfo> channels) {
        EPGLinearLayout childView;
        for (EPGChannelInfo channel : channels) {
            if (this.mListViewAdpter != null && this.mListView != null) {
                int index = this.mListViewAdpter.getIndexOfChannel(channel);
                if (index >= 0 && index < this.mListView.getChildCount() && (childView = this.mListView.getSelectedDynamicLinearLayout(index)) != null) {
                    childView.refreshEventsLayout(channel, channel.getmTVProgramInfoList(), index);
                }
            } else {
                return;
            }
        }
    }

    public void notifyEPGLinearlayoutRefresh() {
        for (int i = 0; i < this.mListView.getChildCount(); i++) {
            EPGLinearLayout childView = this.mListView.getSelectedDynamicLinearLayout(i);
            if (childView != null) {
                childView.refreshTextLayout(i);
            }
        }
        if (this.mListViewSelectedChild != null) {
            setProgramDetailTvState(this.mCurrentSelectedProgramInfo);
        }
    }

    public void calledByScheduleList() {
        EPGChannelInfo selectedChild = (EPGChannelInfo) this.mListView.getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
        if (selectedChild != null) {
            List<EPGProgramInfo> programInfos = selectedChild.getmGroup();
            if (programInfos != null && programInfos.size() >= 1) {
                if (programInfos.size() != 1) {
                    MtkLog.d(TAG, "programInfos.size() >1 ==" + programInfos.size());
                } else if (programInfos.get(0).getmStartTime().longValue() == 0) {
                    MtkLog.d(TAG, "programInfos.size()==1 =and starttime=" + programInfos.get(0).getmStartTime());
                    return;
                }
            } else {
                return;
            }
        } else {
            MtkLog.d(TAG, "selectedChild=null");
        }
        MtkTvBookingBase item = new MtkTvBookingBase();
        Long startTime = EPGTimeConvert.getInstance().getStartTime(this.mCurrentSelectedProgramInfo);
        MtkLog.d(TAG, "startTime=" + startTime);
        if (startTime.longValue() != -1) {
            item.setRecordStartTime(startTime.longValue());
        }
        Long endTime = EPGTimeConvert.getInstance().getEndTime(this.mCurrentSelectedProgramInfo);
        MtkLog.d(TAG, "endTime=" + endTime);
        if (endTime.longValue() != -1) {
            item.setRecordDuration((endTime.longValue() / 1000) - (startTime.longValue() / 1000));
        }
        item.setTunerType(CommonIntegration.getInstance().getTunerMode());
        if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this) == null) {
            ScheduleListItemInfoDialog mDialog = new ScheduleListItemInfoDialog(this, item);
            mDialog.setEpgFlag(true);
            mDialog.show();
        } else if (!ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).isShowing()) {
            ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).setEpgFlag(true);
            ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).show();
        }
        MtkLog.d("Timeshift_PVR", "calledByScheduleList()");
    }

    /* access modifiers changed from: private */
    public void loadEvents() {
        if (this.mThreadHandler != null && this.mGetEventsRunnable != null) {
            this.mThreadHandler.post(this.mGetEventsRunnable);
        }
    }

    public class AsyncTaskEventsLoad extends AsyncTask<String, Integer, List<EPGChannelInfo>> {
        public AsyncTaskEventsLoad() {
        }

        /* access modifiers changed from: protected */
        public List<EPGChannelInfo> doInBackground(String... params) {
            MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad doInBackground:");
            return EPGCnActivity.this.mReader.readProgramInfoByTIF(EPGCnActivity.this.mListViewAdpter.getGroup(), EPGCnActivity.this.mListViewAdpter.getDayNum(), EPGCnActivity.this.mListViewAdpter.getStartHour());
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad onCancelled:");
            super.onCancelled();
        }

        /* access modifiers changed from: protected */
        public void onCancelled(List<EPGChannelInfo> result) {
            MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad onCancelled:" + result);
            super.onCancelled(result);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(List<EPGChannelInfo> result) {
            MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad onPostExecute:" + result);
            EPGCnActivity.this.refreshUpdateLayout(result);
            EPGCnActivity.this.showSelectedProgramInfo();
            EPGCnActivity.this.mHandler.sendEmptyMessage(5);
            EPGCnActivity.this.mListView.mCanChangeChannel = true;
            boolean unused = EPGCnActivity.this.isGetingData = false;
            super.onPostExecute(result);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad onPreExecute:");
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... values) {
            MtkLog.d(EPGCnActivity.TAG, "AsyncTaskEventsLoad onProgressUpdate:" + values);
            super.onProgressUpdate(values);
        }
    }
}
