package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvIntentBase;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGListView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import java.util.Iterator;
import java.util.List;

public class EPGEuActivity extends BaseActivity implements DialogInterface.OnDismissListener, ComponentStatusListener.ICStatusListener {
    private static final int CHANGE_CHANNEL_TIME_OUT = 3000;
    private static final int PER_PAGE_LINE = 4;
    private static final String TAG = "EPGEuActivity";
    public static boolean mIsEpgChannelChange;
    /* access modifiers changed from: private */
    public long curTime = 0;
    /* access modifiers changed from: private */
    public int dayNum = 0;
    /* access modifiers changed from: private */
    public boolean isGetingData;
    /* access modifiers changed from: private */
    public boolean isSpecialState = false;
    /* access modifiers changed from: private */
    public long lastHourTime = 0;
    /* access modifiers changed from: private */
    public long lastTime = 0;
    private TextView mBeginTimeTv;
    private final Bundle mBundle = new Bundle();
    /* access modifiers changed from: private */
    public boolean mCanChangeTimeShow;
    /* access modifiers changed from: private */
    public TextView mCurrentDateTv;
    private int mCurrentPage = 1;
    /* access modifiers changed from: private */
    public EPGProgramInfo mCurrentSelectedProgramInfo;
    /* access modifiers changed from: private */
    public TextView mDataRetrievingShow;
    private DigitTurnCHView mDigitTurnCHView;
    private Rect mEPGActivityRect;
    /* access modifiers changed from: private */
    public EPGEuHelper mEPGEuHelper = new EPGEuHelper();
    /* access modifiers changed from: private */
    public EPGPwdDialog mEPGPwdDialog;
    private TextView mEndTimeTv;
    private View mFreeViewHDLogo;
    private Runnable mGetCurrentTimeRunnable = new Runnable() {
        public void run() {
            if (EPGEuActivity.this.mListViewAdpter != null) {
                long unused = EPGEuActivity.this.curTime = EPGUtil.getCurrentTime();
                if (EPGEuActivity.this.curTime - EPGEuActivity.this.lastTime < -600 || EPGEuActivity.this.curTime - EPGEuActivity.this.lastHourTime >= 3600) {
                    MtkLog.d(EPGEuActivity.TAG, "=====curTime - last time: " + (EPGEuActivity.this.curTime - EPGEuActivity.this.lastHourTime) + "  " + EPGUtil.getCurrentHour());
                    int unused2 = EPGEuActivity.this.dayNum = 0;
                    long unused3 = EPGEuActivity.this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
                    if (EPGEuActivity.this.curTime - EPGEuActivity.this.lastTime < 0) {
                        EPGConfig.FROM_WHERE = 27;
                    } else {
                        EPGConfig.FROM_WHERE = 26;
                    }
                    if (EPGEuActivity.this.mListViewAdpter != null) {
                        EPGEuActivity.this.mListViewAdpter.setDayNum(EPGEuActivity.this.dayNum);
                        EPGEuActivity.this.mListViewAdpter.setStartHour(EPGUtil.getCurrentHour());
                        boolean unused4 = EPGEuActivity.this.mNeedNowGetData = true;
                        boolean unused5 = EPGEuActivity.this.mCanChangeTimeShow = true;
                        EPGEuActivity.this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
                        EPGEuActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_UPDATE_API_EVENT_LIST, 1000);
                    } else {
                        return;
                    }
                }
                long unused6 = EPGEuActivity.this.lastTime = EPGEuActivity.this.curTime;
                String mDate = EPGUtil.formatCurrentTime(EPGEuActivity.this);
                Message msg = Message.obtain();
                msg.obj = mDate;
                msg.what = 1001;
                EPGEuActivity.this.mHandler.sendMessageDelayed(msg, 1000);
            }
        }
    };
    /* access modifiers changed from: private */
    public GetTifEventListRunnable mGetTifEventListRunnable = new GetTifEventListRunnable();
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        /* JADX WARNING: Code restructure failed: missing block: B:181:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:182:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:183:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:184:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:185:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:186:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x02b2, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$200(r10.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x02ba, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$700(r10.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x02c2, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$100(r10.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:69:0x02ca, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2000(r10.this$0) == false) goto L_0x02f3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:0x02cc, code lost:
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.TAG, "is readig event in provider>");
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$800(r10.this$0).removeMessages(263);
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$800(r10.this$0).removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$800(r10.this$0).sendEmptyMessageDelayed(263, 3000);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x02f2, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:72:0x02f3, code lost:
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2002(r10.this$0, true);
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.TAG, "start read event in provider>" + com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$100(r10.this$0).getDayNum() + "   " + com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$100(r10.this$0).getStartHour());
         */
        /* JADX WARNING: Code restructure failed: missing block: B:73:0x0330, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2100(r10.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:75:0x0338, code lost:
            if (com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2200(r10.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:76:0x033a, code lost:
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2200(r10.this$0).channelId = (java.lang.Integer) r11.obj;
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2100(r10.this$0).post(com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.access$2200(r10.this$0));
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r11) {
            /*
                r10 = this;
                java.lang.String r0 = "EPGEuActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "EPGEuHandleMessage>>msg.what="
                r1.append(r2)
                int r2 = r11.what
                r1.append(r2)
                java.lang.String r2 = ",msg.arg1="
                r1.append(r2)
                int r2 = r11.arg1
                r1.append(r2)
                java.lang.String r2 = ",msg.arg2="
                r1.append(r2)
                int r2 = r11.arg2
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                int r0 = r11.what
                r1 = 280(0x118, float:3.92E-43)
                if (r0 == r1) goto L_0x06c7
                r1 = 1001(0x3e9, float:1.403E-42)
                if (r0 == r1) goto L_0x06b4
                r1 = 4104(0x1008, float:5.751E-42)
                if (r0 == r1) goto L_0x06ac
                r1 = 1879048207(0x7000000f, float:1.584566E29)
                if (r0 == r1) goto L_0x0596
                r1 = 1879048209(0x70000011, float:1.5845665E29)
                r2 = 8
                r3 = 3000(0xbb8, double:1.482E-320)
                if (r0 == r1) goto L_0x0541
                r1 = 1879048226(0x70000022, float:1.5845697E29)
                r5 = 4
                r6 = 276(0x114, float:3.87E-43)
                r7 = 1
                r8 = 0
                if (r0 == r1) goto L_0x0436
                switch(r0) {
                    case 4: goto L_0x042b;
                    case 5: goto L_0x0420;
                    case 6: goto L_0x040d;
                    default: goto L_0x0055;
                }
            L_0x0055:
                r1 = 5
                r9 = 260(0x104, float:3.64E-43)
                switch(r0) {
                    case 259: goto L_0x03e0;
                    case 260: goto L_0x03d4;
                    case 261: goto L_0x03bf;
                    case 262: goto L_0x0357;
                    case 263: goto L_0x02ac;
                    case 264: goto L_0x0223;
                    case 265: goto L_0x01eb;
                    default: goto L_0x005b;
                }
            L_0x005b:
                switch(r0) {
                    case 273: goto L_0x0185;
                    case 274: goto L_0x0149;
                    case 275: goto L_0x0106;
                    case 276: goto L_0x00f9;
                    case 277: goto L_0x02ac;
                    case 278: goto L_0x00b6;
                    default: goto L_0x005e;
                }
            L_0x005e:
                switch(r0) {
                    case 1879048193: goto L_0x0063;
                    case 1879048194: goto L_0x0541;
                    default: goto L_0x0061;
                }
            L_0x0061:
                goto L_0x06e7
            L_0x0063:
                java.lang.String r0 = "EPGEuActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "MSG_CB_CONFIG"
                r1.append(r2)
                java.lang.Object r2 = r11.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r2 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r2
                int r2 = r2.param1
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                java.lang.String r1 = "power"
                java.lang.Object r0 = r0.getSystemService(r1)
                android.os.PowerManager r0 = (android.os.PowerManager) r0
                java.lang.String r1 = "EPGEuActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "MSG_CB_CONFIG: screen is on :"
                r2.append(r3)
                boolean r3 = r0.isInteractive()
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                java.lang.Object r1 = r11.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r1 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r1
                int r1 = r1.param1
                if (r1 != 0) goto L_0x00b4
                boolean r1 = com.mediatek.wwtv.tvcenter.util.DestroyApp.isCurEPGActivity()
                if (r1 == 0) goto L_0x00b4
                r0.isInteractive()
            L_0x00b4:
                goto L_0x06e7
            L_0x00b6:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                java.lang.Object r1 = r11.obj
                java.util.List r1 = (java.util.List) r1
                int r2 = r11.arg2
                r0.updateAdapter(r1, r8, r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                int r1 = r1.dayNum
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r0.changeTimeViewsShow(r1, r2)
                int r0 = r11.arg1
                int r0 = r0 % 6
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r0
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                r0.setSelection(r1)
                goto L_0x06e7
            L_0x00f9:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean r1 = r1.isSpecialState
                r0.setLockIconVisibility(r1)
                goto L_0x06e7
            L_0x0106:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                java.lang.Object r1 = r11.obj
                java.util.List r1 = (java.util.List) r1
                int r2 = r11.arg2
                r0.setAdapter(r1, r8, r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                int r1 = r1.dayNum
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r0.changeTimeViewsShow(r1, r2)
                int r0 = r11.arg1
                int r0 = r0 % 6
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r0
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                r0.setSelection(r1)
                goto L_0x06e7
            L_0x0149:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean r0 = r0.mCanChangeTimeShow
                if (r0 == 0) goto L_0x017e
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r0.mCanChangeTimeShow = r8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r1.mListViewAdpter
                int r1 = r1.getDayNum()
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r0.changeTimeViewsShow(r1, r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.updateEventList(r7)
                goto L_0x06e7
            L_0x017e:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.updateEventList(r8)
                goto L_0x06e7
            L_0x0185:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                java.lang.String r0 = "EPGEuActivity"
                java.lang.String r1 = "EPGConfig.EPG_SELECT_CHANNEL_COMPLETE"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.removeMessages(r6)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.ImageView r0 = r0.mLockImageView
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x01ba
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.ImageView r0 = r0.mLockImageView
                r0.setVisibility(r5)
            L_0x01ba:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                boolean r0 = r0.isTvSourceLock()
                if (r0 == 0) goto L_0x01cd
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.finish()
                goto L_0x06e7
            L_0x01cd:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.removeMessages(r9)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r1 = 1000(0x3e8, double:4.94E-321)
                r0.sendEmptyMessageDelayed(r9, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                r0.mCanChangeChannel = r7
                goto L_0x06e7
            L_0x01eb:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                r0.notifyDataSetChanged()
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.removeMessages(r9)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r2 = 800(0x320, double:3.953E-321)
                r0.sendEmptyMessageDelayed(r9, r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.sendEmptyMessage(r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                r0.mCanChangeChannel = r7
                goto L_0x06e7
            L_0x0223:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                r0.setAdapter((android.widget.ListAdapter) r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                int r2 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                r0.setSelection(r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.sendEmptyMessage(r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                r0.mCanChangeChannel = r7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r0.isGetingData = r8
                java.lang.Object r0 = r11.obj
                java.lang.Integer r0 = (java.lang.Integer) r0
                java.lang.String r1 = "EPGEuActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "EPG_SET_LIST_ADAPTER--->channelId="
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                if (r0 == 0) goto L_0x02a5
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                int r1 = r1.getCurrentChannelId()
                java.lang.String r2 = "EPGEuActivity"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "EPG_SET_LIST_ADAPTER--->chId="
                r3.append(r4)
                r3.append(r1)
                java.lang.String r3 = r3.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
                int r2 = r0.intValue()
                if (r2 == r1) goto L_0x02a5
                goto L_0x06e7
            L_0x02a5:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r1.showSelectedProgramInfo()
                goto L_0x06e7
            L_0x02ac:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean r0 = r0.isGetingData
                if (r0 == 0) goto L_0x02f3
                java.lang.String r0 = "EPGEuActivity"
                java.lang.String r1 = "is readig event in provider>"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r1 = 263(0x107, float:3.69E-43)
                r0.removeMessages(r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r2 = 277(0x115, float:3.88E-43)
                r0.removeMessages(r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                r0.sendEmptyMessageDelayed(r1, r3)
                return
            L_0x02f3:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r0.isGetingData = r7
                java.lang.String r0 = "EPGEuActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "start read event in provider>"
                r1.append(r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getDayNum()
                r1.append(r2)
                java.lang.String r2 = "   "
                r1.append(r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                int r2 = r2.getStartHour()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mThreadHandler
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$GetTifEventListRunnable r0 = r0.mGetTifEventListRunnable
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$GetTifEventListRunnable r0 = r0.mGetTifEventListRunnable
                java.lang.Object r1 = r11.obj
                java.lang.Integer r1 = (java.lang.Integer) r1
                r0.channelId = r1
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mThreadHandler
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$GetTifEventListRunnable r1 = r1.mGetTifEventListRunnable
                r0.post(r1)
                goto L_0x06e7
            L_0x0357:
                java.lang.String r0 = "EPGEuActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r3 = "EPGConfig.EPG_UPDATE_CHANNEL_LIST1111>"
                r1.append(r3)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r3 = r3.mListViewAdpter
                r1.append(r3)
                java.lang.String r3 = ">>"
                r1.append(r3)
                int r3 = r11.arg1
                r1.append(r3)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r0 = r0.mReader
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                int r0 = r11.arg1
                if (r0 != r2) goto L_0x03b8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                java.util.List r0 = r0.getList()
                if (r0 == 0) goto L_0x03b8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r0 = r0.mListViewAdpter
                java.util.List r0 = r0.getGroup()
                if (r0 == 0) goto L_0x03b8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.modifyChannelListWithThread()
                goto L_0x06e7
            L_0x03b8:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.refreshChannelListWithThread()
                goto L_0x06e7
            L_0x03bf:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r0 = r0.mCurrentSelectedProgramInfo
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r1 = r1.mCurrentSelectedProgramInfo
                r0.setSubTitleImageViewState(r1)
                goto L_0x06e7
            L_0x03d4:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.showSelectedProgramInfo()
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.getTifEventList()
                goto L_0x06e7
            L_0x03e0:
                java.lang.String r0 = "EPGEuActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "EPGConfig.EPG_SYNCHRONIZATION_MESSAGE>>"
                r1.append(r2)
                int r2 = r11.arg1
                r1.append(r2)
                java.lang.String r2 = "  "
                r1.append(r2)
                int r2 = r11.arg2
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                int r0 = r11.arg1
                int r1 = r11.arg2
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r2.changeTimeViewsShow(r0, r1)
                goto L_0x06e7
            L_0x040d:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                if (r0 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r0 = r0.mListView
                r0.rawChangedOfChannel()
                goto L_0x06e7
            L_0x0420:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.TextView r0 = r0.mDataRetrievingShow
                r0.setVisibility(r5)
                goto L_0x06e7
            L_0x042b:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.TextView r0 = r0.mDataRetrievingShow
                r0.setVisibility(r8)
                goto L_0x06e7
            L_0x0436:
                java.lang.Object r0 = r11.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                int r1 = r0.param1
                if (r1 != 0) goto L_0x06e7
                java.lang.String r1 = "EPGEuActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "come in handleMessage BANNER_MSG_NAV value=== "
                r2.append(r3)
                int r3 = r0.param2
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                int r1 = r0.param2
                r2 = 2000(0x7d0, double:9.88E-321)
                switch(r1) {
                    case 2: goto L_0x050c;
                    case 3: goto L_0x050c;
                    case 4: goto L_0x050c;
                    case 5: goto L_0x045d;
                    case 6: goto L_0x045f;
                    case 7: goto L_0x045f;
                    case 8: goto L_0x045f;
                    case 9: goto L_0x045f;
                    case 10: goto L_0x045f;
                    case 11: goto L_0x045f;
                    case 12: goto L_0x045d;
                    case 13: goto L_0x045f;
                    case 14: goto L_0x045f;
                    default: goto L_0x045d;
                }
            L_0x045d:
                goto L_0x06e7
            L_0x045f:
                java.lang.String r1 = "EPGEuActivity"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r7 = "Mute_current_channel----->specialMsgData.param2="
                r4.append(r7)
                int r7 = r0.param2
                r4.append(r7)
                java.lang.String r4 = r4.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuHelper r4 = r4.mEPGEuHelper
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r4.getCurChannel()
                boolean r1 = r1.isBarkChannel(r4)
                java.lang.String r4 = "EPGEuActivity"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r9 = "Mute_current_channel----->isBarkChannel="
                r7.append(r9)
                r7.append(r1)
                java.lang.String r7 = r7.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r7)
                com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
                r4.sendMuteMsg(r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r4.isSpecialState = r8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r4.needFirstShowLock = r8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.ImageView r4 = r4.mLockImageView
                int r4 = r4.getVisibility()
                if (r4 != 0) goto L_0x04c5
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.ImageView r4 = r4.mLockImageView
                r4.setVisibility(r5)
            L_0x04c5:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.TextView r4 = r4.mProgramNameTv
                int r4 = r4.getVisibility()
                if (r4 != 0) goto L_0x04f8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.TextView r4 = r4.mProgramDetailTv
                int r4 = r4.getVisibility()
                if (r4 == 0) goto L_0x04f8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.TextView r4 = r4.mProgramDetailTv
                r4.setVisibility(r8)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r4 = r4.mCurrentSelectedProgramInfo
                if (r4 == 0) goto L_0x04f8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$1$1 r5 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$1$1
                r5.<init>()
                r4.checkPwdShow(r5)
            L_0x04f8:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r4 = r4.mHandler
                r4.removeMessages(r6)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r4 = r4.mHandler
                r4.sendEmptyMessageDelayed(r6, r2)
                goto L_0x06e7
            L_0x050c:
                int r1 = r0.param2
                if (r1 != r5) goto L_0x0523
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                boolean r1 = r1.isTvSourceLock()
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r1.finish()
                goto L_0x06e7
            L_0x0523:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r1.isSpecialState = r7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                boolean unused = r1.needFirstShowLock = r8
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r1 = r1.mHandler
                r1.removeMessages(r6)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r1 = r1.mHandler
                r1.sendEmptyMessageDelayed(r6, r2)
                goto L_0x06e7
            L_0x0541:
                java.lang.String r0 = "EPGEuActivity"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r5 = "EPG update channel list>"
                r1.append(r5)
                java.lang.Object r5 = r11.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r5 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r5
                int r5 = r5.param2
                r1.append(r5)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                int r0 = r0.mLastChannelUpdateMsg
                r1 = 262(0x106, float:3.67E-43)
                if (r0 != r2) goto L_0x056b
                r10.removeMessages(r1)
                goto L_0x0578
            L_0x056b:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r0 = r0.mHandler
                boolean r0 = r0.hasMessages(r1)
                if (r0 == 0) goto L_0x0578
                return
            L_0x0578:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                java.lang.Object r2 = r11.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r2 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r2
                int r2 = r2.param2
                int unused = r0.mLastChannelUpdateMsg = r2
                android.os.Message r0 = android.os.Message.obtain()
                r0.what = r1
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                int r1 = r1.mLastChannelUpdateMsg
                r0.arg1 = r1
                r10.sendMessageDelayed(r0, r3)
                goto L_0x06e7
            L_0x0596:
                java.lang.Object r0 = r11.obj
                com.mediatek.wwtv.tvcenter.util.TvCallbackData r0 = (com.mediatek.wwtv.tvcenter.util.TvCallbackData) r0
                java.lang.String r1 = "EPGEuActivity"
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
                    case 5: goto L_0x0698;
                    case 6: goto L_0x05d7;
                    default: goto L_0x05d5;
                }
            L_0x05d5:
                goto L_0x06e7
            L_0x05d7:
                boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                if (r1 == 0) goto L_0x0635
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r1 = r1.mListView
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r1.mListViewAdpter
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r1.mListViewAdpter
                java.util.List r1 = r1.getGroup()
                if (r1 == 0) goto L_0x06e7
                java.lang.String r1 = "EPGEuActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "666start read event in provider>"
                r2.append(r3)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r3 = r3.mListViewAdpter
                int r3 = r3.getDayNum()
                r2.append(r3)
                java.lang.String r3 = "   "
                r2.append(r3)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r3 = r3.mListViewAdpter
                int r3 = r3.getStartHour()
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                goto L_0x06e7
            L_0x0635:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r1 = r1.mListView
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r1.mListViewAdpter
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r1.mListViewAdpter
                java.util.List r1 = r1.getGroup()
                if (r1 == 0) goto L_0x06e7
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r2 = r2.mListViewAdpter
                java.util.List r2 = r2.getGroup()
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r3 = r3.mListViewAdpter
                int r3 = r3.getDayNum()
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r4 = r4.mListViewAdpter
                int r4 = r4.getStartHour()
                r5 = 2
                r1.readChannelProgramInfoByTime(r2, r3, r4, r5)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r1 = r1.mHandler
                r2 = 264(0x108, float:3.7E-43)
                r1.removeMessages(r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r1 = r1.mHandler
                r3 = 100
                r1.sendEmptyMessageDelayed(r2, r3)
                goto L_0x06e7
            L_0x0698:
                boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                if (r1 != 0) goto L_0x06e7
                int r1 = r0.param2
                if (r1 > 0) goto L_0x06a6
                int r1 = r0.param3
                if (r1 <= 0) goto L_0x06e7
            L_0x06a6:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r1.updateProgramList()
                goto L_0x06e7
            L_0x06ac:
                java.lang.String r0 = "EPGEuActivity"
                java.lang.String r1 = "ChannelListDialog.MESSAGE_DEFAULT_CAN_CHANGECHANNEL_DELAY reach"
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                goto L_0x06e7
            L_0x06b4:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.widget.TextView r0 = r0.mCurrentDateTv
                java.lang.Object r1 = r11.obj
                java.lang.String r1 = (java.lang.String) r1
                r0.setText(r1)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r0 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r0.setCurrentDate()
                goto L_0x06e7
            L_0x06c7:
                java.lang.Object r0 = r11.obj
                com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r0 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r0
                java.lang.String r1 = "EPGEuActivity"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "EPGConfig.EPG_CHANGING_CHANNEL>>channelInfo="
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                r1.turnChannel(r0)
            L_0x06e7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.AnonymousClass1.handleMessage(android.os.Message):void");
        }
    };
    private HandlerThread mHandlerThead;
    /* access modifiers changed from: private */
    public boolean mHasInitListView;
    private boolean mIs3rdTVSource;
    private boolean mIsCountryUK;
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
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelListByTIF()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
                goto L_0x0022
            L_0x0015:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelList()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
            L_0x0022:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r1 = r1.mListView
                if (r1 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r1 = r1.mListView
                java.util.List r1 = r1.getList()
                if (r1 == 0) goto L_0x0061
                if (r0 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r1.mListViewAdpter
                if (r1 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r2 = r2.mListView
                java.util.List r2 = r2.getList()
                r1.updateChannList(r2, r0)
                r1 = 27
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r1
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$7$1 r2 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$7$1
                r2.<init>()
                r1.runOnUiThread(r2)
            L_0x0061:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.AnonymousClass7.run():void");
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
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelListByTIF()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
                goto L_0x0022
            L_0x0015:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelList()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
            L_0x0022:
                if (r0 == 0) goto L_0x00b4
                int r1 = r0.size()
                if (r1 <= 0) goto L_0x00b4
                r1 = 0
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r2 = r2.mListView
                if (r2 == 0) goto L_0x0071
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r2 = r2.mListView
                int r3 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                java.lang.Object r2 = r2.getItemAtPosition(r3)
                com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r2 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r2
                if (r2 == 0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r2.getTVChannel()
                int r4 = r4.getChannelId()
                boolean r3 = r3.isChannelExit(r4)
                if (r3 == 0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r2.getTVChannel()
                int r1 = r3.getChannelPosition(r4)
                goto L_0x0070
            L_0x0066:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                int r1 = r3.getCurrentPlayChannelPosition()
            L_0x0070:
                goto L_0x007b
            L_0x0071:
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r2 = r2.mReader
                int r1 = r2.getCurrentPlayChannelPosition()
            L_0x007b:
                int r2 = r1 / 6
                int r2 = r2 + 1
                java.lang.String r3 = "EPGEuActivity"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "setAdapter.>index>>>"
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
                com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                android.os.Handler r4 = r4.mHandler
                r4.sendMessage(r3)
            L_0x00b4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.AnonymousClass8.run():void");
        }
    };
    private LinearLayout mRootLayout;
    private TextView mSelectedDateTv;
    private ImageView mSttlImageView;
    /* access modifiers changed from: private */
    public Handler mThreadHandler;
    private int mTotalPage;
    private TextView mTypeFilter;
    private BroadcastReceiver mUpdateEventReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (CommonIntegration.supportTIFFunction()) {
                String actionName = intent.getAction();
                MtkLog.d(EPGEuActivity.TAG, "actionName>>>" + actionName);
                if (actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN) || actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF)) {
                    int channelId = intent.getIntExtra("channel_id", 0);
                    MtkLog.d(EPGEuActivity.TAG, "mUpdateEventReceiver channelId>>>" + channelId);
                    if (EPGEuActivity.this.mListViewAdpter == null || !EPGEuActivity.this.mListViewAdpter.containsChannelId(channelId)) {
                        MtkLog.d(EPGEuActivity.TAG, "containsChannelId false");
                        return;
                    }
                    EPGEuActivity.this.mListViewAdpter.addAlreadyChnnelId(channelId);
                    MtkLog.d(EPGEuActivity.TAG, "containsChannelId true");
                    if (!actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF) && actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN)) {
                        EPGEuActivity.this.mListView.mCanChangeChannel = false;
                        if (EPGEuActivity.this.mListViewAdpter.isAlreadyGetAll()) {
                            EPGEuActivity.this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
                            EPGEuActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG, MessageType.delayForTKToMenu);
                            return;
                        }
                        if (!(EPGEuActivity.this.mDataRetrievingShow == null || EPGEuActivity.this.mDataRetrievingShow.getVisibility() == 0)) {
                            EPGEuActivity.this.mHandler.sendEmptyMessage(4);
                        }
                        EPGEuActivity.this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
                        EPGEuActivity.this.mHandler.removeMessages(263);
                        int delayTime = 1000;
                        if (EPGEuActivity.this.isGetingData) {
                            delayTime = 2000;
                        }
                        EPGEuActivity.this.mHandler.sendEmptyMessageDelayed(263, (long) delayTime);
                    }
                }
            }
        }
    };
    private TextView mViewDetailTv;
    /* access modifiers changed from: private */
    public boolean needFirstShowLock = true;
    private final String[] preValues = new String[3];

    interface IUIRefresh {
        void refreshUI(int i);
    }

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
        Log.d(TAG, "onCreate()...");
        ((DestroyApp) getApplication()).add(this);
        requestWindowFeature(1);
        Window mWindow = getWindow();
        if (mWindow != null) {
            mWindow.setFlags(1024, 1024);
        }
        selectChannelWhenEnterEPG();
        setContentView(R.layout.epg_eu_main);
        this.mLinearLayout = (LinearLayout) findViewById(R.id.epg_root_layout);
        if (!CommonIntegration.getInstance().isContextInit()) {
            MtkLog.d(TAG, "init common integergration context");
            CommonIntegration.getInstance().setContext(getApplicationContext());
        }
        this.mIs3rdTVSource = CommonIntegration.getInstance().is3rdTVSource();
        this.mHandlerThead = new HandlerThread(TAG);
        this.mHandlerThead.start();
        this.mThreadHandler = new Handler(this.mHandlerThead.getLooper());
        this.mReader = DataReader.getInstance(this);
        this.mReader.loadProgramType();
        this.mReader.loadMonthAndWeekRes();
        this.mMtkTvBanner = MtkTvBanner.getInstance();
        this.mEPGPwdDialog = new EPGPwdDialog(this);
        this.mEPGPwdDialog.setAttachView(findViewById(R.id.epg_content_layout));
        this.mEPGPwdDialog.setOnDismissListener(this);
        this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
        initUI();
        this.mListViewAdpter.setStartHour(EPGUtil.getCurrentHour());
        changeTimeViewsShow(this.dayNum, this.mListViewAdpter.getStartHour());
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_EVENT_NFY, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CONFIG, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        ComponentStatusListener lister = ComponentStatusListener.getInstance();
        lister.addListener(10, this);
        lister.addListener(6, this);
        lister.addListener(17, this);
        registerUpdateReceiver();
        setCurrentDate();
        EPGConfig.init = true;
        EPGConfig.avoidFoucsChange = false;
        addDigitTurnCHView();
    }

    private void addDigitTurnCHView() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.digit_turn_view_width), getResources().getDimensionPixelOffset(R.dimen.digit_turn_view_height));
        lp.gravity = 17;
        this.mDigitTurnCHView = new DigitTurnCHView(this);
        this.mDigitTurnCHView.requestFocus();
        this.mDigitTurnCHView.setVisibility(8);
        this.mDigitTurnCHView.setOnDigitTurnCHCallback(new DigitTurnCHView.OnDigitTurnCHCallback() {
            public void onTurnCH(int inputDigit) {
                List<?> list = EPGEuActivity.this.mListView.getList();
                int index = -1;
                long channelId = 0;
                EPGChannelInfo changeChannel = null;
                int i = 0;
                while (true) {
                    if (i >= list.size()) {
                        break;
                    }
                    EPGChannelInfo channelInfo = (EPGChannelInfo) list.get(i);
                    if (channelInfo.getmChanelNum() == inputDigit) {
                        changeChannel = channelInfo;
                        index = i;
                        channelId = channelInfo.mId;
                        MtkLog.d("onTurnCH", "channelInfo=[num:" + channelInfo.getmChanelNum() + ",name:" + channelInfo.getName() + "]");
                        break;
                    }
                    i++;
                }
                if (index < 0) {
                    MtkLog.d("onTurnCH", "index<0");
                    return;
                }
                MtkLog.d("onTurnCH", "index=" + index + ",channelId=" + channelId);
                Message msg = Message.obtain();
                msg.arg1 = index;
                msg.arg2 = (index / 6) + 1;
                msg.obj = list;
                msg.what = EPGConfig.EPG_INIT_EVENT_LIST;
                EPGEuActivity.this.mHandler.sendMessage(msg);
                EPGEuActivity.this.turnChannel(changeChannel);
            }
        });
        this.mDigitTurnCHView.setBackgroundResource(R.drawable.nav_sundry_bg);
        ((FrameLayout) getWindow().getDecorView().findViewById(16908290)).addView(this.mDigitTurnCHView, lp);
    }

    /* access modifiers changed from: private */
    public void turnChannel(EPGChannelInfo changeChannel) {
        this.mEPGEuHelper.turnChannel(changeChannel);
        TurnkeyUiMainActivity.getInstance().sendMuteMsg(CommonIntegration.getInstance().isBarkChannel(this.mEPGEuHelper.getCurChannel()));
    }

    private void registerUpdateReceiver() {
        IntentFilter updateIntentFilter = new IntentFilter();
        updateIntentFilter.addAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF);
        updateIntentFilter.addAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN);
        registerReceiver(this.mUpdateEventReceiver, updateIntentFilter);
    }

    private void initUI() {
        this.mFreeViewHDLogo = findViewById(R.id.epg_freeviewhd_logo);
        String country = MtkTvConfig.getInstance().getCountry();
        MtkLog.d(TAG, "country=" + country);
        if (country == null || !country.equals("GBR")) {
            this.mFreeViewHDLogo.setVisibility(4);
            this.mIsCountryUK = false;
        } else {
            this.mFreeViewHDLogo.setVisibility(0);
            this.mIsCountryUK = true;
        }
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
        display.getMetrics(new DisplayMetrics());
        int width = (int) (((float) display.getWidth()) * 0.73f);
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
                    r2 = 1
                    if (r1 == 0) goto L_0x0016
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelListByTIF(r2)
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                    goto L_0x0023
                L_0x0016:
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelList(r2)
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                L_0x0023:
                    if (r0 == 0) goto L_0x004f
                    int r1 = r0.size()
                    if (r1 <= 0) goto L_0x004f
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    int r1 = r1.getCurrentPlayChannelPosition()
                    int r3 = r1 / 6
                    int r3 = r3 + r2
                    android.os.Message r2 = android.os.Message.obtain()
                    r2.arg1 = r1
                    r2.arg2 = r3
                    r2.obj = r0
                    r4 = 275(0x113, float:3.85E-43)
                    r2.what = r4
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity r4 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.this
                    android.os.Handler r4 = r4.mHandler
                    r4.sendMessage(r2)
                L_0x004f:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.AnonymousClass4.run():void");
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
        this.mEPGActivityRect = new Rect(0, 0, ScreenConstant.SCREEN_WIDTH, ScreenConstant.SCREEN_HEIGHT);
        getTifEventList();
    }

    /* access modifiers changed from: private */
    public void getTifEventList() {
        new Thread(new Runnable() {
            public void run() {
                boolean is3rdTVSource = CommonIntegration.getInstance().is3rdTVSource();
                MtkLog.i(EPGEuActivity.TAG, "is3rdTVSource=" + is3rdTVSource);
                if (is3rdTVSource) {
                    EPGEuActivity.this.mHandler.sendEmptyMessageDelayed(263, 1000);
                }
            }
        }).start();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        MtkLog.i(TAG, "EPG on Pause");
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d(TAG, "onStop()");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        MtkLog.d(TAG, "onDestroy()...");
        selectChannelAfterExitEPG();
        clearData();
        ((DestroyApp) getApplication()).remove(this);
    }

    public void setBarkerChannel(boolean isBarkerChannel) {
        if (isBarkerChannel) {
            this.mBundle.putByte(MtkTvTISMsgBase.MSG_CHANNEL_IS_BARKER_CHANNEL, (byte) 1);
        } else {
            this.mBundle.putByte(MtkTvTISMsgBase.MSG_CHANNEL_IS_BARKER_CHANNEL, (byte) 0);
        }
        TurnkeyUiMainActivity.getInstance().getTvView().sendAppPrivateCommand(MtkTvTISMsgBase.MTK_TIS_MSG_CHANNEL, this.mBundle);
    }

    private void selectChannelWhenEnterEPG() {
        this.mEPGEuHelper.selectCHWithEnterEPG();
    }

    private void selectChannelAfterExitEPG() {
        MtkTvChannelInfoBase curChannel = CommonIntegration.getInstance().getCurChInfo();
        EPGChannelInfo currentChannel = (EPGChannelInfo) this.mListView.getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
        if (currentChannel != null) {
            curChannel = currentChannel.getTVChannel();
        }
        this.mEPGEuHelper.setCurChannel(curChannel);
        this.mEPGEuHelper.selectCHAfterExitEPG();
    }

    class MyUpdata implements EPGListView.UpDateListView {
        MyUpdata() {
        }

        public void updata(boolean isNext) {
            if (EPGEuActivity.this.mHasInitListView) {
                EPGEuActivity.this.mHandler.removeMessages(4);
                EPGEuActivity.this.mHandler.sendEmptyMessage(4);
                List<?> currentList = EPGEuActivity.this.mListView.getCurrentList();
                if (isNext) {
                    EPGConfig.SELECTED_CHANNEL_POSITION = 0;
                } else {
                    EPGConfig.SELECTED_CHANNEL_POSITION = currentList.size() - 1;
                }
                EPGEuActivity.this.mListViewAdpter.setGroup(currentList);
                EPGEuActivity.this.mListView.setAdapter((ListAdapter) EPGEuActivity.this.mListViewAdpter);
                EPGEuActivity.this.mListView.setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
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

    private class GetTifEventListRunnable implements Runnable {
        public Integer channelId;

        private GetTifEventListRunnable() {
        }

        public void run() {
            if (EPGEuActivity.this.mListView != null && EPGEuActivity.this.mReader != null && EPGEuActivity.this.mListViewAdpter != null && EPGEuActivity.this.mListViewAdpter.getGroup() != null) {
                MtkLog.d(EPGEuActivity.TAG, "start read event in provider>" + EPGEuActivity.this.mListViewAdpter.getDayNum() + "   " + EPGEuActivity.this.mListViewAdpter.getStartHour());
                EPGEuActivity.this.mReader.readProgramInfoByTIF(EPGEuActivity.this.mListViewAdpter.getGroup(), EPGEuActivity.this.mListViewAdpter.getDayNum(), EPGEuActivity.this.mListViewAdpter.getStartHour());
                Message msg = Message.obtain();
                msg.what = EPGConfig.EPG_SET_LIST_ADAPTER;
                msg.obj = this.channelId;
                EPGEuActivity.this.mHandler.sendMessage(msg);
            }
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
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_EVENT_NFY, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CONFIG, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        ComponentStatusListener.getInstance().removeListener(this);
        if (this.mUpdateEventReceiver != null) {
            unregisterReceiver(this.mUpdateEventReceiver);
            this.mUpdateEventReceiver = null;
        }
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1001);
            this.mHandler.removeMessages(263);
            this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
            this.mHandler.removeMessages(EPGConfig.EPG_SET_LIST_ADAPTER);
            this.mHandler.removeMessages(4);
        }
        if (this.mThreadHandler != null) {
            this.mThreadHandler.removeCallbacks(this.mGetTifEventListRunnable);
            this.mThreadHandler.removeCallbacks(this.mGetCurrentTimeRunnable);
            this.mThreadHandler.removeCallbacks(this.mModifyChannelListRunnable);
            this.mThreadHandler.removeCallbacks(this.mRefreshChannelListRunnable);
            this.mHandlerThead.quit();
            this.mGetTifEventListRunnable = null;
            this.mGetCurrentTimeRunnable = null;
            this.mModifyChannelListRunnable = null;
            this.mRefreshChannelListRunnable = null;
            this.mThreadHandler = null;
            this.mHandlerThead = null;
        }
        MtkTvEvent.getInstance().clearActiveWindows();
        if (this.mListViewAdpter != null) {
            this.mListViewAdpter = null;
        }
        if (this.mListView != null) {
            this.mListView = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0088, code lost:
        return super.onKeyDown(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0089, code lost:
        changeBottomViewText(true, r9);
        r1 = new com.mediatek.wwtv.tvcenter.epg.eu.EpgType(r8);
        r1.setOnDismissListener(new com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.AnonymousClass9(r8));
        r1.show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x009c, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009f, code lost:
        if (r8.mTotalPage <= 1) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a1, code lost:
        r8.mCurrentPage++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00aa, code lost:
        if (r8.mCurrentPage <= r8.mTotalPage) goto L_0x00ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ac, code lost:
        r8.mCurrentPage = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ae, code lost:
        r8.mProgramDetailTv.scrollTo(0, ((r8.mCurrentPage - 1) * 4) * r8.mProgramDetailTv.getLineHeight());
        r8.mPageInfoTv.setText(r8.mCurrentPage + "/" + r8.mTotalPage);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00db, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00dc, code lost:
        r8.dayNum = r8.mListViewAdpter.getDayNum();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00e8, code lost:
        if (r8.dayNum != 8) goto L_0x00eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00ea, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00eb, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0118, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0119, code lost:
        r8.dayNum = r8.mListViewAdpter.getDayNum();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0123, code lost:
        if (r8.dayNum != 0) goto L_0x0126;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0125, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0126, code lost:
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = false;
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = 24;
        r8.dayNum--;
        r8.mListViewAdpter.setDayNum(r8.dayNum);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x013a, code lost:
        if (r8.dayNum != 0) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x013c, code lost:
        r8.mListViewAdpter.setStartHour(com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0146, code lost:
        r8.mListViewAdpter.setStartHour(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x014b, code lost:
        r8.mCanChangeTimeShow = true;
        r8.mHandler.removeMessages(4);
        r8.mHandler.sendEmptyMessage(4);
        r8.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r8.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0161, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(final int r9, android.view.KeyEvent r10) {
        /*
            r8 = this;
            java.lang.String r0 = "EPGEuActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "event.getRepeatCount()>>>"
            r1.append(r2)
            int r2 = r10.getRepeatCount()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            java.lang.String r0 = "EPGEuActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "keyCode="
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 1
            switch(r9) {
                case 7: goto L_0x0162;
                case 8: goto L_0x0162;
                case 9: goto L_0x0162;
                case 10: goto L_0x0162;
                case 11: goto L_0x0162;
                case 12: goto L_0x0162;
                case 13: goto L_0x0162;
                case 14: goto L_0x0162;
                case 15: goto L_0x0162;
                case 16: goto L_0x0162;
                default: goto L_0x0034;
            }
        L_0x0034:
            r1 = 500(0x1f4, double:2.47E-321)
            r3 = 274(0x112, float:3.84E-43)
            r4 = 4
            r5 = 0
            switch(r9) {
                case 183: goto L_0x0119;
                case 184: goto L_0x00dc;
                case 185: goto L_0x009d;
                case 186: goto L_0x0089;
                default: goto L_0x003d;
            }
        L_0x003d:
            switch(r9) {
                case 4: goto L_0x006d;
                case 23: goto L_0x004d;
                case 30: goto L_0x0089;
                case 33: goto L_0x004d;
                case 35: goto L_0x00dc;
                case 46: goto L_0x0119;
                case 53: goto L_0x009d;
                case 66: goto L_0x004d;
                case 82: goto L_0x004c;
                case 172: goto L_0x0042;
                case 178: goto L_0x0041;
                default: goto L_0x0040;
            }
        L_0x0040:
            goto L_0x0084
        L_0x0041:
            return r0
        L_0x0042:
            int r1 = r10.getRepeatCount()
            if (r1 > 0) goto L_0x004b
            r8.finish()
        L_0x004b:
            return r0
        L_0x004c:
            return r0
        L_0x004d:
            java.lang.String r1 = "EPGEuActivity"
            java.lang.String r2 = "KEYCODE_DPAD_CENTER"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListView r1 = r8.mListView
            int r2 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            java.lang.Object r1 = r1.getItemAtPosition(r2)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r1 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r1
            r8.mListViewSelectedChild = r1
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r1 = r8.mListViewSelectedChild
            if (r1 == 0) goto L_0x006c
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$10 r1 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$10
            r1.<init>(r9)
            r8.checkPwdShow(r1)
        L_0x006c:
            return r0
        L_0x006d:
            com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView r0 = r8.mDigitTurnCHView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x007b
            com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView r0 = r8.mDigitTurnCHView
            r0.hideView()
            goto L_0x0084
        L_0x007b:
            int r0 = r10.getRepeatCount()
            if (r0 > 0) goto L_0x0084
            r8.finish()
        L_0x0084:
            boolean r0 = super.onKeyDown(r9, r10)
            return r0
        L_0x0089:
            r8.changeBottomViewText(r0, r9)
            com.mediatek.wwtv.tvcenter.epg.eu.EpgType r1 = new com.mediatek.wwtv.tvcenter.epg.eu.EpgType
            r1.<init>(r8)
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$9 r2 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity$9
            r2.<init>(r1)
            r1.setOnDismissListener(r2)
            r1.show()
            return r0
        L_0x009d:
            int r1 = r8.mTotalPage
            if (r1 <= r0) goto L_0x00db
            int r1 = r8.mCurrentPage
            int r1 = r1 + r0
            r8.mCurrentPage = r1
            int r1 = r8.mCurrentPage
            int r2 = r8.mTotalPage
            if (r1 <= r2) goto L_0x00ae
            r8.mCurrentPage = r0
        L_0x00ae:
            android.widget.TextView r1 = r8.mProgramDetailTv
            int r2 = r8.mCurrentPage
            int r2 = r2 - r0
            int r2 = r2 * r4
            android.widget.TextView r3 = r8.mProgramDetailTv
            int r3 = r3.getLineHeight()
            int r2 = r2 * r3
            r1.scrollTo(r5, r2)
            android.widget.TextView r1 = r8.mPageInfoTv
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r8.mCurrentPage
            r2.append(r3)
            java.lang.String r3 = "/"
            r2.append(r3)
            int r3 = r8.mTotalPage
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.setText(r2)
        L_0x00db:
            return r0
        L_0x00dc:
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r6 = r6.getDayNum()
            r8.dayNum = r6
            int r6 = r8.dayNum
            r7 = 8
            if (r6 != r7) goto L_0x00eb
            return r5
        L_0x00eb:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r5
            r6 = 23
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r6
            int r6 = r8.dayNum
            int r6 = r6 + r0
            r8.dayNum = r6
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r7 = r8.dayNum
            r6.setDayNum(r7)
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r6 = r8.mListViewAdpter
            r6.setStartHour(r5)
            r8.mCanChangeTimeShow = r0
            android.os.Handler r5 = r8.mHandler
            r5.removeMessages(r4)
            android.os.Handler r5 = r8.mHandler
            r5.sendEmptyMessage(r4)
            android.os.Handler r4 = r8.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r8.mHandler
            r4.sendEmptyMessageDelayed(r3, r1)
            return r0
        L_0x0119:
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r6 = r6.getDayNum()
            r8.dayNum = r6
            int r6 = r8.dayNum
            if (r6 != 0) goto L_0x0126
            return r5
        L_0x0126:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r5
            r6 = 24
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r6
            int r6 = r8.dayNum
            int r6 = r6 - r0
            r8.dayNum = r6
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r6 = r8.mListViewAdpter
            int r7 = r8.dayNum
            r6.setDayNum(r7)
            int r6 = r8.dayNum
            if (r6 != 0) goto L_0x0146
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r5 = r8.mListViewAdpter
            int r6 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour()
            r5.setStartHour(r6)
            goto L_0x014b
        L_0x0146:
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r6 = r8.mListViewAdpter
            r6.setStartHour(r5)
        L_0x014b:
            r8.mCanChangeTimeShow = r0
            android.os.Handler r5 = r8.mHandler
            r5.removeMessages(r4)
            android.os.Handler r5 = r8.mHandler
            r5.sendEmptyMessage(r4)
            android.os.Handler r4 = r8.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r8.mHandler
            r4.sendEmptyMessageDelayed(r3, r1)
            return r0
        L_0x0162:
            com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView r1 = r8.mDigitTurnCHView
            r1.keyHandler(r9, r10)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyUp>>>>" + keyCode + "  " + event.getAction());
        if (keyCode != 130) {
            switch (keyCode) {
                case 24:
                case 25:
                    return true;
                default:
                    return super.onKeyUp(keyCode, event);
            }
        } else {
            boolean isCurrentSourceDTV = CommonIntegration.getInstance().isCurrentSourceDTV();
            boolean is3rdTv = CommonIntegration.getInstance().is3rdTVSource();
            MtkLog.d(TAG, "isCurrentSourceDTV=" + isCurrentSourceDTV + "," + is3rdTv);
            if (is3rdTv) {
                Toast.makeText(this, R.string.nav_no_support_pvr_for_tvsource, 0).show();
                return true;
            }
            if (isCurrentSourceDTV) {
                calledByScheduleList();
            } else {
                Toast.makeText(this, R.string.nav_no_support_pvr_for_tvsource, 0).show();
            }
            return true;
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
                if (!this.mListViewAdpter.setActiveWindow()) {
                    this.mHandler.removeMessages(263);
                    this.mHandler.sendEmptyMessageDelayed(263, 500);
                }
            } else if (!CommonIntegration.supportTIFFunction()) {
                MtkLog.d(TAG, "5start read event from api>" + this.mListViewAdpter.getDayNum() + "   " + this.mListViewAdpter.getStartHour());
                this.mReader.readChannelProgramInfoByTime(this.mListViewAdpter.getGroup(), this.mListViewAdpter.getDayNum(), this.mListViewAdpter.getStartHour(), 2);
                this.mHandler.removeMessages(EPGConfig.EPG_SET_LIST_ADAPTER);
                this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_SET_LIST_ADAPTER, 100);
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
        checkPwdShow(new IUIRefresh() {
            public void refreshUI(int showFlag) {
                EPGEuActivity.this.refreshSelectedProgramInfo(showFlag);
            }
        });
    }

    /* access modifiers changed from: private */
    public void refreshSelectedProgramInfo(int showFlag) {
        String str;
        if (this.mListViewAdpter != null) {
            MtkLog.d(TAG, "SELECTED_CHANNEL_POSITION:" + EPGConfig.SELECTED_CHANNEL_POSITION);
            this.mListViewSelectedChild = (EPGChannelInfo) this.mListView.getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
            StringBuilder sb = new StringBuilder();
            sb.append("showSelectedProgramInfo EPGConfig.SELECTED_CHANNEL_POSITION:");
            sb.append(EPGConfig.SELECTED_CHANNEL_POSITION);
            sb.append("   ");
            String str2 = null;
            if (this.mListViewSelectedChild == null) {
                str = null;
            } else {
                str = "not null";
            }
            sb.append(str);
            MtkLog.d(TAG, sb.toString());
            if (this.mListViewSelectedChild != null) {
                EPGLinearLayout childView = this.mListView.getSelectedDynamicLinearLayout(EPGConfig.SELECTED_CHANNEL_POSITION);
                List<EPGProgramInfo> mProgramList = this.mListViewSelectedChild.getmTVProgramInfoList();
                if (this.mEPGPwdDialog != null && this.mEPGPwdDialog.isShowing()) {
                    this.mEPGPwdDialog.dismiss();
                    MtkLog.d(TAG, "do dismiss pwd dialog!");
                }
                if (this.needFirstShowLock) {
                    setProgramDetailTvState(this.mCurrentSelectedProgramInfo, showFlag);
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("showSelectedProgramInfo childView:");
                sb2.append(childView == null ? null : "not null");
                sb2.append("  mProgramList:");
                sb2.append(mProgramList == null ? null : Integer.valueOf(mProgramList.size()));
                sb2.append("   CurrentSelectPosition:");
                sb2.append(EPGLinearLayout.mCurrentSelectPosition);
                MtkLog.d(TAG, sb2.toString());
                if (mProgramList != null && childView != null && mProgramList.size() > 0 && childView.getmCurrentSelectPosition() < mProgramList.size() && childView.getmCurrentSelectPosition() >= 0) {
                    this.mCurrentSelectedProgramInfo = mProgramList.get(childView.getmCurrentSelectPosition());
                    if (this.mCurrentSelectedProgramInfo != null) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("showSelectedProgramInfo mCurrentSelectedProgramInfo:");
                        if (this.mCurrentSelectedProgramInfo != null) {
                            str2 = this.mCurrentSelectedProgramInfo.getTitle();
                        }
                        sb3.append(str2);
                        sb3.append("  ");
                        sb3.append(childView.getmCurrentSelectPosition());
                        sb3.append("  Type:");
                        sb3.append(this.mCurrentSelectedProgramInfo.getMainType());
                        MtkLog.d(TAG, sb3.toString());
                        if (this.mProgramNameTv.getVisibility() != 0) {
                            this.mProgramNameTv.setVisibility(0);
                        }
                        this.mProgramNameTv.setText(TextUtils.isEmpty(this.mCurrentSelectedProgramInfo.getTitle()) ? getResources().getString(R.string.nav_epg_no_program_title) : this.mCurrentSelectedProgramInfo.getTitle());
                        if (this.mProgramTimeTv.getVisibility() != 0) {
                            this.mProgramTimeTv.setVisibility(0);
                        }
                        this.mProgramTimeTv.setText(EPGTimeConvert.getInstance().formatProgramTimeInfo(this.mCurrentSelectedProgramInfo, EPGUtil.judgeFormatTime12_24(this)));
                        setProgramDetailTvState(this.mCurrentSelectedProgramInfo, showFlag);
                        if (this.mIsCountryUK && this.mCurrentSelectedProgramInfo.getMainType() > this.mReader.getMainType().length) {
                            this.mProgramType.setText(getString(R.string.nav_epg_not_support));
                        } else if (this.mCurrentSelectedProgramInfo.getMainType() >= 1) {
                            this.mProgramType.setText(this.mReader.getMainType()[this.mCurrentSelectedProgramInfo.getMainType() - 1]);
                        } else {
                            this.mProgramType.setText(getString(R.string.nav_epg_unclassified));
                        }
                        if (this.mProgramRating.getVisibility() != 0) {
                            this.mProgramRating.setVisibility(0);
                        }
                        this.mProgramRating.setText(this.mReader.mapRating2CustomerStr(this.mCurrentSelectedProgramInfo.getRatingValue(), this.mCurrentSelectedProgramInfo.getRatingType()));
                        if (!this.isSpecialState) {
                            setSubTitleImageViewState(this.mCurrentSelectedProgramInfo);
                        } else if (this.mSttlImageView.getVisibility() != 4) {
                            this.mSttlImageView.setVisibility(4);
                        }
                        if (this.mEPGPwdDialog != null && this.mEPGPwdDialog.isShowing()) {
                            setProgramInfoViewsInVisiable();
                        }
                    } else if (mProgramList == null || mProgramList.size() <= 0 || isFinishing()) {
                        setProgramInfoViewsInVisiable();
                    } else {
                        this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                        this.mHandler.sendEmptyMessage(EPGConfig.EPG_PROGRAMINFO_SHOW);
                    }
                } else if (childView == null) {
                    MtkLog.d(TAG, "showSelectedProgramInfo childView == null this.isFinishing():" + isFinishing());
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

    /* access modifiers changed from: private */
    public void setProgramInfoViewsInVisiable() {
        if (this.mProgramNameTv.getVisibility() != 4) {
            this.mProgramNameTv.setVisibility(4);
        }
        if (this.mProgramTimeTv.getVisibility() != 4) {
            this.mProgramTimeTv.setVisibility(4);
        }
        if (this.mProgramDetailTv.getVisibility() != 4) {
            this.mProgramDetailTv.setVisibility(4);
        }
        if (this.mSttlImageView.getVisibility() != 4) {
            this.mSttlImageView.setVisibility(4);
        }
        if (this.mProgramRating.getVisibility() != 4) {
            this.mProgramRating.setVisibility(4);
        }
        this.mPageInfoTv.setText("");
        this.mViewDetailTv.setText("");
        this.mProgramType.setText("");
        this.mProgramRating.setText("");
    }

    /* access modifiers changed from: private */
    public void checkPwdShow(final IUIRefresh uiRefresh) {
        new Thread(new Runnable() {
            public void run() {
                final int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
                MtkLog.d(EPGEuActivity.TAG, "showFlag=" + showFlag);
                EPGEuActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        uiRefresh.refreshUI(showFlag);
                    }
                });
            }
        }).start();
    }

    private void setLockImageViewState(MtkTvChannelInfoBase mChannel, int showFlag) {
        if (mChannel == null) {
            return;
        }
        if (showFlag != 0 || (this.mEPGPwdDialog != null && this.mEPGPwdDialog.isShowing())) {
            this.isSpecialState = false;
            this.mLockImageView.setVisibility(4);
            return;
        }
        this.isSpecialState = true;
        this.mLockImageView.setVisibility(0);
    }

    private boolean isShowSTTLIcon() {
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            MtkLog.d(TAG, "come in isShowCaptionIcon, is3rdTVSource == true");
            return false;
        }
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
    public void setProgramDetailTvState(EPGProgramInfo childProgramInfo, int showFlag) {
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
        int i;
        int line = this.mProgramDetailTv.getLineCount();
        MtkLog.d(TAG, "--- initProgramDetailContent()---- Lines: " + line);
        if (line > 0) {
            if (line % 4 == 0) {
                i = line / 4;
            } else {
                i = (line / 4) + 1;
            }
            this.mTotalPage = i;
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
                EPGEuActivity.this.initProgramDetailContent();
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

    public void notifyEPGLinearlayoutRefresh() {
        for (int i = 0; i < this.mListView.getChildCount(); i++) {
            EPGLinearLayout childView = this.mListView.getSelectedDynamicLinearLayout(i);
            if (childView != null) {
                childView.refreshTextLayout(i);
            }
        }
        if (this.mListViewSelectedChild != null) {
            checkPwdShow(new IUIRefresh() {
                public void refreshUI(int showFlag) {
                    EPGEuActivity.this.setProgramDetailTvState(EPGEuActivity.this.mCurrentSelectedProgramInfo, showFlag);
                }
            });
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
        if (this.mCurrentSelectedProgramInfo != null) {
            MtkTvBookingBase item = new MtkTvBookingBase();
            Long startTime = Long.valueOf(this.mCurrentSelectedProgramInfo.getmStartTime().longValue() * 1000);
            MtkLog.d("jiayang", "start time in epg:" + startTime);
            MtkTvTimeFormatBase from = new MtkTvTimeFormatBase();
            MtkTvTimeFormatBase to = new MtkTvTimeFormatBase();
            from.setByUtc(startTime.longValue() / 1000);
            MtkTvTimeBase time = new MtkTvTimeBase();
            time.convertTime(5, from, to);
            Long startTime2 = Long.valueOf(to.toSeconds() * 1000);
            MtkLog.d(TAG, "startTime=" + startTime2 + " str = " + Util.timeToTimeStringEx(startTime2.longValue() * 1000, 0));
            if (startTime2.longValue() != -1) {
                item.setRecordStartTime(startTime2.longValue() / 1000);
            }
            from.setByUtc(Long.valueOf(this.mCurrentSelectedProgramInfo.getmEndTime().longValue() * 1000).longValue() / 1000);
            time.convertTime(5, from, to);
            Long endTime = Long.valueOf(to.toSeconds() * 1000);
            MtkLog.d(TAG, "endTime=" + endTime);
            if (endTime.longValue() != -1) {
                item.setRecordDuration((endTime.longValue() / 1000) - (startTime2.longValue() / 1000));
            }
            item.setTunerType(CommonIntegration.getInstance().getTunerMode());
            item.setRepeatMode(128);
            if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this) == null) {
                ScheduleListItemInfoDialog mDialog = new ScheduleListItemInfoDialog(this, item);
                mDialog.setEpgFlag(true);
                mDialog.show();
            } else if (!ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).isShowing()) {
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).setEpgFlag(true);
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).show();
            }
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "EU EPG updateComponentStatus>>>" + statusID + ">>" + value);
        if (statusID == 10) {
            CommonIntegration.getInstance().setCHChanging(false);
            this.mHandler.removeMessages(4104);
        } else if (statusID == 6) {
            finish();
        } else if (statusID == 17) {
            finish();
        }
    }
}
