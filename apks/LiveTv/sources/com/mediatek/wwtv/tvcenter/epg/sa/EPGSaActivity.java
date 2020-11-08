package com.mediatek.wwtv.tvcenter.epg.sa;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvIntentBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGListView;
import com.mediatek.wwtv.tvcenter.epg.sa.db.DBMgrProgramList;
import com.mediatek.wwtv.tvcenter.epg.sa.db.EPGBookListViewDataItem;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import java.util.Iterator;
import java.util.List;

public class EPGSaActivity extends BaseActivity implements DialogInterface.OnDismissListener, ComponentStatusListener.ICStatusListener {
    private static final String CHOOSE_BOOKED_EVENT = "mtk_intent_choose_the_booked_event";
    private static final int PER_PAGE_LINE = 4;
    private static final String TAG = "EPGSaActivity";
    /* access modifiers changed from: private */
    public long curTime = 0;
    /* access modifiers changed from: private */
    public EPGProgramInfo currentBookedProgram;
    /* access modifiers changed from: private */
    public int dayNum = 0;
    /* access modifiers changed from: private */
    public boolean isGetingData;
    /* access modifiers changed from: private */
    public boolean isSpecialState = false;
    private boolean isSupportPvr = true;
    /* access modifiers changed from: private */
    public long lastHourTime = 0;
    /* access modifiers changed from: private */
    public long lastTime = 0;
    private TextView mBeginTimeTv;
    /* access modifiers changed from: private */
    public TurnkeyCommDialog mBookProgramConfirmDialog;
    /* access modifiers changed from: private */
    public boolean mCanChangeTimeShow;
    /* access modifiers changed from: private */
    public TextView mCurrentDateTv;
    private int mCurrentPage = 1;
    /* access modifiers changed from: private */
    public EPGProgramInfo mCurrentSelectedProgramInfo;
    /* access modifiers changed from: private */
    public TextView mDataRetrievingShow;
    private Rect mEPGActivityRect;
    private EPGBookedListDilog mEPGBookedListDilog;
    private EPGPwdDialog mEPGPwdDialog;
    private TextView mEndTimeTv;
    private LinearLayout mEnterParentLayout;
    /* access modifiers changed from: private */
    public TextView mEnterTipTv;
    private Runnable mGetCurrentTimeRunnable = new Runnable() {
        public void run() {
            if (EPGSaActivity.this.mListViewAdpter != null) {
                long unused = EPGSaActivity.this.curTime = EPGUtil.getCurrentTime();
                if (EPGSaActivity.this.curTime - EPGSaActivity.this.lastTime < -600 || EPGSaActivity.this.curTime - EPGSaActivity.this.lastHourTime >= 3600) {
                    int unused2 = EPGSaActivity.this.dayNum = 0;
                    long unused3 = EPGSaActivity.this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
                    if (EPGSaActivity.this.curTime - EPGSaActivity.this.lastTime < 0) {
                        EPGConfig.FROM_WHERE = 27;
                    } else {
                        EPGConfig.FROM_WHERE = 26;
                    }
                    if (EPGSaActivity.this.mListViewAdpter != null) {
                        EPGSaActivity.this.mListViewAdpter.setDayNum(EPGSaActivity.this.dayNum);
                        EPGSaActivity.this.mListViewAdpter.setStartHour(EPGUtil.getCurrentHour());
                        boolean unused4 = EPGSaActivity.this.mNeedNowGetData = true;
                        boolean unused5 = EPGSaActivity.this.mCanChangeTimeShow = true;
                        EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
                        EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_UPDATE_API_EVENT_LIST, 1000);
                    } else {
                        return;
                    }
                }
                long unused6 = EPGSaActivity.this.lastTime = EPGSaActivity.this.curTime;
                String mDate = EPGUtil.formatCurrentTimeWith24Hours();
                Message msg = Message.obtain();
                msg.obj = mDate;
                msg.what = 1001;
                EPGSaActivity.this.mHandler.sendMessageDelayed(msg, 1000);
            }
        }
    };
    /* access modifiers changed from: private */
    public Runnable mGetTifEventListRunnable = new Runnable() {
        public void run() {
            if (EPGSaActivity.this.mListView != null && EPGSaActivity.this.mReader != null && EPGSaActivity.this.mListViewAdpter != null && EPGSaActivity.this.mListViewAdpter.getGroup() != null) {
                MtkLog.d(EPGSaActivity.TAG, "start read event in provider>" + EPGSaActivity.this.mListViewAdpter.getDayNum() + "   " + EPGSaActivity.this.mListViewAdpter.getStartHour());
                EPGSaActivity.this.mReader.readProgramInfoByTIF(EPGSaActivity.this.mListViewAdpter.getGroup(), EPGSaActivity.this.mListViewAdpter.getDayNum(), EPGSaActivity.this.mListViewAdpter.getStartHour());
                EPGSaActivity.this.mHandler.sendEmptyMessage(EPGConfig.EPG_SET_LIST_ADAPTER);
            }
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 1001) {
                if (i != 1879048194) {
                    if (i == 1879048207) {
                        TvCallbackData data = (TvCallbackData) msg.obj;
                        MtkLog.e(EPGSaActivity.TAG, "Epg MSG_CB_NFY_EVENT_UPDATE message:type:" + data.param1 + "==>" + data.param2 + "==>" + data.param3 + "==>" + data.param4);
                        switch (data.param1) {
                            case 5:
                                if (CommonIntegration.supportTIFFunction()) {
                                    return;
                                }
                                if (data.param2 > 0 || data.param3 > 0) {
                                    EPGSaActivity.this.updateProgramList();
                                    return;
                                }
                                return;
                            case 6:
                                if (EPGSaActivity.this.mListView != null && EPGSaActivity.this.mReader != null && EPGSaActivity.this.mListViewAdpter != null && EPGSaActivity.this.mListViewAdpter.getGroup() != null) {
                                    if (CommonIntegration.supportTIFFunction()) {
                                        MtkLog.d(EPGSaActivity.TAG, "6start read event in provider>" + EPGSaActivity.this.mListViewAdpter.getDayNum() + "   " + EPGSaActivity.this.mListViewAdpter.getStartHour());
                                        return;
                                    }
                                    MtkLog.d(EPGSaActivity.TAG, "6start read event from api>" + EPGSaActivity.this.mListViewAdpter.getDayNum() + "   " + EPGSaActivity.this.mListViewAdpter.getStartHour());
                                    EPGSaActivity.this.mReader.readChannelProgramInfoByTime(EPGSaActivity.this.mListViewAdpter.getGroup(), EPGSaActivity.this.mListViewAdpter.getDayNum(), EPGSaActivity.this.mListViewAdpter.getStartHour(), 2);
                                    EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_SET_LIST_ADAPTER);
                                    EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_SET_LIST_ADAPTER, 100);
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                    } else if (i != 1879048209) {
                        if (i != 1879048226) {
                            switch (i) {
                                case 4:
                                    MtkLog.d(EPGSaActivity.TAG, "EPGConfig.EPG_DATA_RETRIEVING");
                                    EPGSaActivity.this.mDataRetrievingShow.setVisibility(0);
                                    return;
                                case 5:
                                    MtkLog.d(EPGSaActivity.TAG, "EPGConfig.EPG_DATA_RETRIEVAL_FININSH");
                                    EPGSaActivity.this.mDataRetrievingShow.setVisibility(4);
                                    return;
                                case 6:
                                    if (EPGSaActivity.this.mListView != null) {
                                        EPGSaActivity.this.mListView.rawChangedOfChannel();
                                        return;
                                    }
                                    return;
                                default:
                                    switch (i) {
                                        case EPGConfig.EPG_SYNCHRONIZATION_MESSAGE:
                                            EPGSaActivity.this.changeTimeViewsShow(msg.arg1, msg.arg2);
                                            return;
                                        case EPGConfig.EPG_PROGRAMINFO_SHOW:
                                            EPGSaActivity.this.showSelectedProgramInfo();
                                            if (CommonIntegration.getInstance().is3rdTVSource()) {
                                                EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(263, 1000);
                                                return;
                                            }
                                            return;
                                        case EPGConfig.EPG_PROGRAM_STTL_SHOW:
                                            if (EPGSaActivity.this.mCurrentSelectedProgramInfo != null) {
                                                EPGSaActivity.this.setSubTitleImageViewState(EPGSaActivity.this.mCurrentSelectedProgramInfo);
                                                return;
                                            }
                                            return;
                                        case 262:
                                            MtkLog.d(EPGSaActivity.TAG, "EPGConfig.EPG_UPDATE_CHANNEL_LIST1111>" + EPGSaActivity.this.mListViewAdpter + ">>" + msg.arg1);
                                            if (EPGSaActivity.this.mReader != null && EPGSaActivity.this.mListViewAdpter != null && EPGSaActivity.this.mListView != null) {
                                                if (msg.arg1 != 8 || EPGSaActivity.this.mListView.getList() == null || EPGSaActivity.this.mListViewAdpter.getGroup() == null) {
                                                    EPGSaActivity.this.refreshChannelListWithThread();
                                                    return;
                                                } else {
                                                    EPGSaActivity.this.modifyChannelListWithThread();
                                                    return;
                                                }
                                            } else {
                                                return;
                                            }
                                        case 263:
                                            break;
                                        case EPGConfig.EPG_SET_LIST_ADAPTER:
                                            MtkLog.d(EPGSaActivity.TAG, "EPGConfig.EPG_SET_LIST_ADAPTER>" + EPGSaActivity.this.mListViewAdpter);
                                            if (EPGSaActivity.this.mListViewAdpter != null && EPGSaActivity.this.mListView != null) {
                                                EPGSaActivity.this.mListView.setAdapter((ListAdapter) EPGSaActivity.this.mListViewAdpter);
                                                EPGSaActivity.this.mListView.setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
                                                EPGSaActivity.this.showSelectedProgramInfo();
                                                EPGSaActivity.this.mHandler.sendEmptyMessage(5);
                                                EPGSaActivity.this.mListView.mCanChangeChannel = true;
                                                boolean unused = EPGSaActivity.this.isGetingData = false;
                                                return;
                                            }
                                            return;
                                        case EPGConfig.EPG_NOTIFY_LIST_ADAPTER:
                                            MtkLog.d(EPGSaActivity.TAG, "EPGConfig.EPG_NOTIFY_LIST_ADAPTER>" + EPGSaActivity.this.mListViewAdpter);
                                            if (EPGSaActivity.this.mListViewAdpter != null) {
                                                EPGSaActivity.this.mListViewAdpter.notifyDataSetChanged();
                                                EPGSaActivity.this.showSelectedProgramInfo();
                                                EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                                                EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 800);
                                                EPGSaActivity.this.mHandler.removeMessages(5);
                                                EPGSaActivity.this.mHandler.sendEmptyMessage(5);
                                                EPGSaActivity.this.mListView.mCanChangeChannel = true;
                                                return;
                                            }
                                            return;
                                        default:
                                            switch (i) {
                                                case EPGConfig.EPG_SHOW_LOCK_ICON:
                                                    if (EPGSaActivity.this.mListViewSelectedChild != null) {
                                                        EPGSaActivity.this.setLockImageViewState(EPGSaActivity.this.mListViewSelectedChild.getTVChannel());
                                                        return;
                                                    }
                                                    return;
                                                case EPGConfig.EPG_SELECT_CHANNEL_COMPLETE:
                                                    Log.d(EPGSaActivity.TAG, "nextChannel()...");
                                                    if (EPGSaActivity.this.mReader != null && EPGSaActivity.this.mListView != null) {
                                                        MtkLog.d(EPGSaActivity.TAG, "EPGConfig.EPG_SELECT_CHANNEL_COMPLETE");
                                                        EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_SET_LOCK_ICON);
                                                        if (EPGSaActivity.this.mLockImageView.getVisibility() == 0) {
                                                            EPGSaActivity.this.mLockImageView.setVisibility(4);
                                                        }
                                                        if (EPGSaActivity.this.mReader.isTvSourceLock()) {
                                                            EPGSaActivity.this.finish();
                                                            return;
                                                        }
                                                        EPGSaActivity.this.mEnterTipTv.setText("");
                                                        EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                                                        EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                                                        EPGSaActivity.this.mListView.mCanChangeChannel = true;
                                                        return;
                                                    }
                                                    return;
                                                case EPGConfig.EPG_UPDATE_API_EVENT_LIST:
                                                    if (EPGSaActivity.this.mListViewAdpter == null) {
                                                        return;
                                                    }
                                                    if (EPGSaActivity.this.mCanChangeTimeShow) {
                                                        boolean unused2 = EPGSaActivity.this.mCanChangeTimeShow = false;
                                                        EPGSaActivity.this.changeTimeViewsShow(EPGSaActivity.this.mListViewAdpter.getDayNum(), EPGSaActivity.this.mListViewAdpter.getStartHour());
                                                        EPGSaActivity.this.updateEventList(true);
                                                        return;
                                                    }
                                                    EPGSaActivity.this.updateEventList(false);
                                                    return;
                                                case EPGConfig.EPG_INIT_EVENT_LIST:
                                                    EPGSaActivity.this.setAdapter((List) msg.obj, 0, msg.arg2);
                                                    EPGSaActivity.this.changeTimeViewsShow(EPGSaActivity.this.dayNum, EPGSaActivity.this.mListViewAdpter.getStartHour());
                                                    EPGConfig.SELECTED_CHANNEL_POSITION = msg.arg1 % 6;
                                                    EPGSaActivity.this.mListView.setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
                                                    return;
                                                case EPGConfig.EPG_SET_LOCK_ICON:
                                                    EPGSaActivity.this.setLockIconVisibility(EPGSaActivity.this.isSpecialState);
                                                    return;
                                                case EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG:
                                                    break;
                                                case EPGConfig.EPG_REFRESH_CHANNEL_LIST:
                                                    if (EPGSaActivity.this.mListViewAdpter != null && EPGSaActivity.this.mListView != null) {
                                                        EPGSaActivity.this.updateAdapter((List) msg.obj, 0, msg.arg2);
                                                        EPGSaActivity.this.changeTimeViewsShow(EPGSaActivity.this.dayNum, EPGSaActivity.this.mListViewAdpter.getStartHour());
                                                        EPGConfig.SELECTED_CHANNEL_POSITION = msg.arg1 % 6;
                                                        EPGSaActivity.this.mListView.setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
                                                        return;
                                                    }
                                                    return;
                                                default:
                                                    return;
                                            }
                                    }
                                    if (EPGSaActivity.this.isGetingData) {
                                        MtkLog.d(EPGSaActivity.TAG, "is readig event in provider>");
                                        EPGSaActivity.this.mHandler.removeMessages(263);
                                        EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
                                        EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(263, MessageType.delayMillis5);
                                        return;
                                    }
                                    boolean unused3 = EPGSaActivity.this.isGetingData = true;
                                    if (EPGSaActivity.this.mThreadHandler != null && EPGSaActivity.this.mGetTifEventListRunnable != null) {
                                        EPGSaActivity.this.mThreadHandler.post(EPGSaActivity.this.mGetTifEventListRunnable);
                                        return;
                                    }
                                    return;
                            }
                        } else {
                            TvCallbackData specialMsgData = (TvCallbackData) msg.obj;
                            if (specialMsgData.param1 == 0) {
                                MtkLog.d(EPGSaActivity.TAG, "come in handleMessage BANNER_MSG_NAV value=== " + specialMsgData.param2);
                                switch (specialMsgData.param2) {
                                    case 2:
                                    case 3:
                                    case 4:
                                        if (specialMsgData.param2 != 4) {
                                            EPGSaActivity.this.mEnterTipTv.setText(EPGSaActivity.this.getString(R.string.nav_epg_unlock));
                                            boolean unused4 = EPGSaActivity.this.isSpecialState = true;
                                            boolean unused5 = EPGSaActivity.this.needFirstShowLock = false;
                                            EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_SET_LOCK_ICON);
                                            EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_SET_LOCK_ICON, MessageType.delayMillis5);
                                            return;
                                        } else if (EPGSaActivity.this.mReader.isTvSourceLock()) {
                                            EPGSaActivity.this.finish();
                                            return;
                                        } else {
                                            return;
                                        }
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    case 10:
                                    case 11:
                                    case 13:
                                    case 14:
                                        boolean unused6 = EPGSaActivity.this.isSpecialState = false;
                                        boolean unused7 = EPGSaActivity.this.needFirstShowLock = false;
                                        if (EPGSaActivity.this.mLockImageView.getVisibility() == 0) {
                                            EPGSaActivity.this.mLockImageView.setVisibility(4);
                                        }
                                        if (EPGSaActivity.this.mProgramTimeTv.getVisibility() == 0 && EPGSaActivity.this.mProgramDetailTv.getVisibility() != 0) {
                                            EPGSaActivity.this.mProgramDetailTv.setVisibility(0);
                                            if (EPGSaActivity.this.mCurrentSelectedProgramInfo != null) {
                                                EPGSaActivity.this.setProgramDetailTvState(EPGSaActivity.this.mCurrentSelectedProgramInfo);
                                            }
                                        }
                                        EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_SET_LOCK_ICON);
                                        EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_SET_LOCK_ICON, MessageType.delayMillis5);
                                        return;
                                    default:
                                        return;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                }
                MtkLog.d(EPGSaActivity.TAG, "EPG update channel list>" + ((TvCallbackData) msg.obj).param2);
                if (EPGSaActivity.this.mLastChannelUpdateMsg == 8) {
                    removeMessages(262);
                } else if (EPGSaActivity.this.mHandler.hasMessages(262)) {
                    return;
                }
                int unused8 = EPGSaActivity.this.mLastChannelUpdateMsg = ((TvCallbackData) msg.obj).param2;
                Message message = Message.obtain();
                message.what = 262;
                message.arg1 = EPGSaActivity.this.mLastChannelUpdateMsg;
                sendMessageDelayed(message, 3000);
                return;
            }
            EPGSaActivity.this.mCurrentDateTv.setText((String) msg.obj);
            removeMessages(1001);
            EPGSaActivity.this.setCurrentDate();
        }
    };
    private HandlerThread mHandlerThead;
    /* access modifiers changed from: private */
    public int mLastChannelUpdateMsg;
    /* access modifiers changed from: private */
    public EPGListView mListView;
    /* access modifiers changed from: private */
    public EPGListViewAdapter mListViewAdpter;
    private LinearLayout mListViewLayout;
    /* access modifiers changed from: private */
    public EPGChannelInfo mListViewSelectedChild;
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
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelListByTIF()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
                goto L_0x0022
            L_0x0015:
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelList()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
            L_0x0022:
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r1 = r1.mListView
                if (r1 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r1 = r1.mListView
                java.util.List r1 = r1.getList()
                if (r1 == 0) goto L_0x0061
                if (r0 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r1 = r1.mListViewAdpter
                if (r1 == 0) goto L_0x0061
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r2 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r2 = r2.mListView
                java.util.List r2 = r2.getList()
                r1.updateChannList(r2, r0)
                r1 = 27
                com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r1
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity$5$1 r2 = new com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity$5$1
                r2.<init>()
                r1.runOnUiThread(r2)
            L_0x0061:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.AnonymousClass5.run():void");
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
    private TextView mProgramNameTv;
    private TextView mProgramRating;
    /* access modifiers changed from: private */
    public TextView mProgramTimeTv;
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
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelListByTIF()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
                goto L_0x0022
            L_0x0015:
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                java.util.List r1 = r1.getAllChannelList()
                r0 = r1
                java.util.ArrayList r0 = (java.util.ArrayList) r0
            L_0x0022:
                if (r0 == 0) goto L_0x00b4
                int r1 = r0.size()
                if (r1 <= 0) goto L_0x00b4
                r1 = 0
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r2 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r2 = r2.mListView
                if (r2 == 0) goto L_0x0071
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r2 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r2 = r2.mListView
                int r3 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
                java.lang.Object r2 = r2.getItemAtPosition(r3)
                com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r2 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r2
                if (r2 == 0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r3 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r2.getTVChannel()
                int r4 = r4.getChannelId()
                boolean r3 = r3.isChannelExit(r4)
                if (r3 == 0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r3 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r2.getTVChannel()
                int r1 = r3.getChannelPosition(r4)
                goto L_0x0070
            L_0x0066:
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r3 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                int r1 = r3.getCurrentPlayChannelPosition()
            L_0x0070:
                goto L_0x007b
            L_0x0071:
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r2 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                com.mediatek.wwtv.tvcenter.epg.DataReader r2 = r2.mReader
                int r1 = r2.getCurrentPlayChannelPosition()
            L_0x007b:
                int r2 = r1 / 6
                int r2 = r2 + 1
                java.lang.String r3 = "EPGSaActivity"
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
                com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r4 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                android.os.Handler r4 = r4.mHandler
                r4.sendMessage(r3)
            L_0x00b4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.AnonymousClass6.run():void");
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
                MtkLog.d(EPGSaActivity.TAG, "actionName>>>" + actionName);
                if (actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN) || actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF)) {
                    int channelId = intent.getIntExtra("channel_id", 0);
                    if (EPGSaActivity.this.mListViewAdpter == null || !EPGSaActivity.this.mListViewAdpter.containsChannelId(channelId)) {
                        MtkLog.d(EPGSaActivity.TAG, "containsChannelId false");
                        return;
                    }
                    EPGSaActivity.this.mListViewAdpter.addAlreadyChnnelId(channelId);
                    MtkLog.d(EPGSaActivity.TAG, "containsChannelId true");
                    if (actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF) || !actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN)) {
                        return;
                    }
                    if (EPGSaActivity.this.mListViewAdpter.isAlreadyGetAll()) {
                        MtkLog.d(EPGSaActivity.TAG, "containsChannelId isAlreadyGetAll");
                        EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
                        EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG, MessageType.delayMillis5);
                        return;
                    }
                    if (!(EPGSaActivity.this.mDataRetrievingShow == null || EPGSaActivity.this.mDataRetrievingShow.getVisibility() == 0)) {
                        EPGSaActivity.this.mHandler.sendEmptyMessage(4);
                    }
                    EPGSaActivity.this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
                    EPGSaActivity.this.mHandler.removeMessages(263);
                    int delayTime = 1000;
                    if (EPGSaActivity.this.isGetingData) {
                        delayTime = 2000;
                    }
                    MtkLog.d(EPGSaActivity.TAG, "containsChannelId not isAlreadyGetAll>>" + delayTime);
                    EPGSaActivity.this.mHandler.sendEmptyMessageDelayed(263, (long) delayTime);
                } else if (actionName.equals(EPGSaActivity.CHOOSE_BOOKED_EVENT)) {
                    MtkLog.d(EPGSaActivity.TAG, "have choosed booked event, finish EPG");
                    EPGSaActivity.this.finish();
                }
            }
        }
    };
    private TextView mViewDetailTv;
    private LinearLayout mZeroParentLayout;
    private TextView mZeroTipTv;
    /* access modifiers changed from: private */
    public boolean needFirstShowLock = true;
    private String[] preValues = new String[5];

    /* access modifiers changed from: private */
    public void updateProgramList() {
        if (!this.mNeedNowGetData) {
            EPGConfig.FROM_WHERE = 27;
            this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
            this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_UPDATE_API_EVENT_LIST, 1000);
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
        setContentView(R.layout.epg_sa_main);
        if (!CommonIntegration.getInstance().isContextInit()) {
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
        this.mEPGPwdDialog.setAttachView(findViewById(R.id.epg_content_layout));
        this.mEPGPwdDialog.setOnDismissListener(this);
        this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
        removeOutOfDateBookEvents();
        if (DataSeparaterUtil.getInstance() != null) {
            this.isSupportPvr = DataSeparaterUtil.getInstance().isSupportPvr();
        }
        initUI();
        this.mListViewAdpter.setStartHour(EPGUtil.getCurrentHour());
        changeTimeViewsShow(this.dayNum, this.mListViewAdpter.getStartHour());
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        ComponentStatusListener.getInstance().addListener(6, this);
        registerUpdateReceiver();
        setCurrentDate();
        EPGConfig.init = true;
        EPGConfig.avoidFoucsChange = false;
    }

    private void registerUpdateReceiver() {
        IntentFilter updateIntentFilter = new IntentFilter();
        updateIntentFilter.addAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF);
        updateIntentFilter.addAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN);
        updateIntentFilter.addAction(CHOOSE_BOOKED_EVENT);
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
        this.mEnterParentLayout = (LinearLayout) findViewById(R.id.epg_bottom_view_enter_parent);
        this.mZeroParentLayout = (LinearLayout) findViewById(R.id.epg_bottom_view_enter_zero);
        this.mEnterTipTv = (TextView) findViewById(R.id.epg_bottom_view_enter);
        this.mZeroTipTv = (TextView) findViewById(R.id.epg_bottom_view_0);
        this.mZeroTipTv.setText(getString(R.string.nav_epg_zero_tip));
        if (this.isSupportPvr) {
            this.mEnterParentLayout.setVisibility(8);
            this.mZeroParentLayout.setVisibility(8);
        } else {
            this.mEnterParentLayout.setVisibility(0);
            this.mZeroParentLayout.setVisibility(0);
        }
        this.mLockImageView = (ImageView) findViewById(R.id.epg_info_lock_icon);
        this.mSttlImageView = (ImageView) findViewById(R.id.epg_info_sttl_icon);
        this.mRootLayout = (LinearLayout) findViewById(R.id.epg_root_layout);
        this.mListViewLayout = (LinearLayout) findViewById(R.id.epg_listview_layout);
        this.mListView = (EPGListView) findViewById(R.id.epg_program_forecast_listview);
        this.mListView.setHandler(this.mHandler);
        this.mListViewAdpter = new EPGListViewAdapter(this, EPGUtil.getCurrentHour());
        this.mProgramDetailTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        this.mListViewAdpter.setHandler(this.mHandler);
        this.mMyUpData = new MyUpdata();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        display.getMetrics(mDisplayMetrics);
        int width = ((int) ((((float) ScreenConstant.SCREEN_WIDTH) - (46.0f * mDisplayMetrics.density)) * 0.75f)) - 2;
        MtkLog.e(TAG, "setAdpter-----layoutParams.width--setWidth-->" + width);
        this.mListViewAdpter.setWidth(width);
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
                    r6 = this;
                    r0 = 0
                    boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                    if (r1 == 0) goto L_0x0015
                    com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelListByTIF()
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                    goto L_0x0022
                L_0x0015:
                    com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelList()
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                L_0x0022:
                    if (r0 == 0) goto L_0x006d
                    int r1 = r0.size()
                    if (r1 <= 0) goto L_0x006d
                    com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r1 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    int r1 = r1.getCurrentPlayChannelPosition()
                    int r2 = r1 / 6
                    int r2 = r2 + 1
                    java.lang.String r3 = "EPGSaActivity"
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
                    r4 = 275(0x113, float:3.85E-43)
                    r3.what = r4
                    com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r4 = com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.this
                    android.os.Handler r4 = r4.mHandler
                    r4.sendMessage(r3)
                L_0x006d:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.AnonymousClass3.run():void");
            }
        }).start();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        MtkLog.i(TAG, "EPG onResume()");
        if (this.mListViewAdpter != null && this.mListViewAdpter.getGroup() == null) {
            getChannelListWithThread();
        }
        this.mEPGActivityRect = new Rect(0, 0, ScreenConstant.SCREEN_WIDTH, ScreenConstant.SCREEN_HEIGHT);
        if (CommonIntegration.getInstance().is3rdTVSource()) {
            this.mHandler.sendEmptyMessageDelayed(263, 1000);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        MtkLog.i(TAG, "EPG on Pause");
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.i(TAG, "EPG onStop()");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "EPG onDestroy()...");
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        if (this.mHandler != null) {
            this.mHandler.removeMessages(262);
        }
        EPGLinearLayout.mCurrentSelectPosition = 0;
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        ComponentStatusListener.getInstance().removeListener(this);
        unregisterReceiver(this.mUpdateEventReceiver);
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
        ((DestroyApp) getApplication()).remove(this);
    }

    class MyUpdata implements EPGListView.UpDateListView {
        MyUpdata() {
        }

        public void updata(boolean next) {
            EPGSaActivity.this.mHandler.removeMessages(4);
            EPGSaActivity.this.mHandler.sendEmptyMessage(4);
            List<?> currentList = EPGSaActivity.this.mListView.getCurrentList();
            if (next) {
                EPGConfig.SELECTED_CHANNEL_POSITION = 0;
            } else {
                EPGConfig.SELECTED_CHANNEL_POSITION = currentList.size() - 1;
            }
            EPGSaActivity.this.mListViewAdpter.setGroup(currentList);
            EPGSaActivity.this.mListView.setAdapter((ListAdapter) EPGSaActivity.this.mListViewAdpter);
            EPGSaActivity.this.mListView.setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
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
        this.mListView.initData(adpter, 6, this.mMyUpData, pageNum);
        this.mListViewAdpter.setGroup(this.mListView.getCurrentList());
        this.mListView.setAdapter((ListAdapter) this.mListViewAdpter);
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

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0102, code lost:
        return super.onKeyDown(r11, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0103, code lost:
        changeBottomViewText(true, r11);
        new com.mediatek.wwtv.tvcenter.epg.sa.EpgType(r10).show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x010e, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0111, code lost:
        if (r10.mTotalPage <= 1) goto L_0x014d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0113, code lost:
        r10.mCurrentPage++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x011c, code lost:
        if (r10.mCurrentPage <= r10.mTotalPage) goto L_0x0120;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x011e, code lost:
        r10.mCurrentPage = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0120, code lost:
        r10.mProgramDetailTv.scrollTo(0, ((r10.mCurrentPage - 1) * 4) * r10.mProgramDetailTv.getLineHeight());
        r10.mPageInfoTv.setText(r10.mCurrentPage + "/" + r10.mTotalPage);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x014d, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x014e, code lost:
        r10.dayNum = r10.mListViewAdpter.getDayNum();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x015a, code lost:
        if (r10.dayNum != 8) goto L_0x015d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x015c, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x015d, code lost:
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = false;
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = 23;
        r10.dayNum++;
        r10.mListViewAdpter.setDayNum(r10.dayNum);
        r10.mListViewAdpter.setStartHour(0);
        r10.mCanChangeTimeShow = true;
        r10.mHandler.removeMessages(4);
        r10.mHandler.sendEmptyMessage(4);
        r10.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r10.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x018a, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x018b, code lost:
        r10.dayNum = r10.mListViewAdpter.getDayNum();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0195, code lost:
        if (r10.dayNum != 0) goto L_0x0198;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0197, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0198, code lost:
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = false;
        com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = 24;
        r10.dayNum--;
        r10.mListViewAdpter.setDayNum(r10.dayNum);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x01ac, code lost:
        if (r10.dayNum != 0) goto L_0x01b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x01ae, code lost:
        r10.mListViewAdpter.setStartHour(com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x01b8, code lost:
        r10.mListViewAdpter.setStartHour(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01bd, code lost:
        r10.mCanChangeTimeShow = true;
        r10.mHandler.removeMessages(4);
        r10.mHandler.sendEmptyMessage(4);
        r10.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r10.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x01d3, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r11, android.view.KeyEvent r12) {
        /*
            r10 = this;
            java.lang.String r0 = "EPGSaActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "event.getRepeatCount()>>>"
            r1.append(r2)
            int r2 = r12.getRepeatCount()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 500(0x1f4, double:2.47E-321)
            r2 = 274(0x112, float:3.84E-43)
            r3 = 4
            r4 = 0
            r5 = 1
            switch(r11) {
                case 183: goto L_0x018b;
                case 184: goto L_0x014e;
                case 185: goto L_0x010f;
                case 186: goto L_0x0103;
                default: goto L_0x0024;
            }
        L_0x0024:
            switch(r11) {
                case 4: goto L_0x00f5;
                case 7: goto L_0x00d0;
                case 23: goto L_0x002b;
                case 30: goto L_0x0103;
                case 33: goto L_0x002b;
                case 35: goto L_0x014e;
                case 46: goto L_0x018b;
                case 53: goto L_0x010f;
                case 66: goto L_0x002b;
                case 82: goto L_0x002a;
                case 172: goto L_0x00f5;
                case 178: goto L_0x0029;
                default: goto L_0x0027;
            }
        L_0x0027:
            goto L_0x00fe
        L_0x0029:
            return r5
        L_0x002a:
            return r5
        L_0x002b:
            java.lang.String r0 = "EPGSaActivity"
            java.lang.String r1 = "KEYCODE_DPAD_CENTER"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            boolean r0 = r10.isSupportPvr
            if (r0 != 0) goto L_0x0037
            return r5
        L_0x0037:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r0 = r10.mListView
            int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            java.lang.Object r0 = r0.getItemAtPosition(r1)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r0 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r0
            r10.mListViewSelectedChild = r0
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r0 = r10.mListViewSelectedChild
            if (r0 == 0) goto L_0x00cf
            com.mediatek.twoworlds.tv.MtkTvPWDDialog r0 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
            int r0 = r0.PWDShow()
            if (r0 != 0) goto L_0x0076
            android.os.Handler r1 = r10.mHandler
            r2 = 272(0x110, float:3.81E-43)
            boolean r1 = r1.hasMessages(r2)
            if (r1 == 0) goto L_0x0060
            android.os.Handler r1 = r10.mHandler
            r1.removeMessages(r2)
        L_0x0060:
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r1 = r10.mEPGPwdDialog
            r1.show()
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r1 = r10.mEPGPwdDialog
            r1.sendAutoDismissMessage()
            r10.changeBottomViewText(r5, r11)
            r10.setProgramInfoViewsInVisiable()
            android.widget.ImageView r1 = r10.mLockImageView
            r1.setVisibility(r3)
            goto L_0x00cf
        L_0x0076:
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r1 = r10.mListViewSelectedChild
            java.util.List r1 = r1.getmTVProgramInfoList()
            if (r1 == 0) goto L_0x00cf
            int r2 = r1.size()
            if (r2 <= 0) goto L_0x00cf
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListView r2 = r10.mListView
            int r3 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r2 = r2.getSelectedDynamicLinearLayout(r3)
            if (r2 == 0) goto L_0x00cf
            int r3 = r2.getmCurrentSelectPosition()
            if (r3 < 0) goto L_0x00cf
            int r3 = r2.getmCurrentSelectPosition()
            int r6 = r1.size()
            if (r3 >= r6) goto L_0x00cf
            int r3 = r2.getmCurrentSelectPosition()
            java.lang.Object r3 = r1.get(r3)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r3 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r3
            r10.currentBookedProgram = r3
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r3 = r10.currentBookedProgram
            if (r3 == 0) goto L_0x00cf
            long r6 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentTime()
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r3 = r10.currentBookedProgram
            java.lang.Long r3 = r3.getmStartTime()
            long r8 = r3.longValue()
            int r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r3 >= 0) goto L_0x00cf
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r3 = r10.currentBookedProgram
            boolean r3 = r10.programIsInDB(r3)
            if (r3 == 0) goto L_0x00cc
            r10.showBookConfirm(r4)
            goto L_0x00cf
        L_0x00cc:
            r10.showBookConfirm(r5)
        L_0x00cf:
            return r5
        L_0x00d0:
            boolean r0 = r10.isSupportPvr
            if (r0 != 0) goto L_0x00d5
            return r5
        L_0x00d5:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGBookedListDilog r0 = r10.mEPGBookedListDilog
            if (r0 != 0) goto L_0x00e0
            com.mediatek.wwtv.tvcenter.epg.sa.EPGBookedListDilog r0 = new com.mediatek.wwtv.tvcenter.epg.sa.EPGBookedListDilog
            r0.<init>(r10)
            r10.mEPGBookedListDilog = r0
        L_0x00e0:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGBookedListDilog r0 = r10.mEPGBookedListDilog
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x00ee
            com.mediatek.wwtv.tvcenter.epg.sa.EPGBookedListDilog r0 = r10.mEPGBookedListDilog
            r0.dismiss()
            goto L_0x00f4
        L_0x00ee:
            r10.changeBottomViewText(r5, r11)
            r10.showBookedProgramsList()
        L_0x00f4:
            return r5
        L_0x00f5:
            int r0 = r12.getRepeatCount()
            if (r0 > 0) goto L_0x00fe
            r10.finish()
        L_0x00fe:
            boolean r0 = super.onKeyDown(r11, r12)
            return r0
        L_0x0103:
            r10.changeBottomViewText(r5, r11)
            com.mediatek.wwtv.tvcenter.epg.sa.EpgType r0 = new com.mediatek.wwtv.tvcenter.epg.sa.EpgType
            r0.<init>(r10)
            r0.show()
            return r5
        L_0x010f:
            int r0 = r10.mTotalPage
            if (r0 <= r5) goto L_0x014d
            int r0 = r10.mCurrentPage
            int r0 = r0 + r5
            r10.mCurrentPage = r0
            int r0 = r10.mCurrentPage
            int r1 = r10.mTotalPage
            if (r0 <= r1) goto L_0x0120
            r10.mCurrentPage = r5
        L_0x0120:
            android.widget.TextView r0 = r10.mProgramDetailTv
            int r1 = r10.mCurrentPage
            int r1 = r1 - r5
            int r1 = r1 * r3
            android.widget.TextView r2 = r10.mProgramDetailTv
            int r2 = r2.getLineHeight()
            int r1 = r1 * r2
            r0.scrollTo(r4, r1)
            android.widget.TextView r0 = r10.mPageInfoTv
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r10.mCurrentPage
            r1.append(r2)
            java.lang.String r2 = "/"
            r1.append(r2)
            int r2 = r10.mTotalPage
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setText(r1)
        L_0x014d:
            return r5
        L_0x014e:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r6 = r10.mListViewAdpter
            int r6 = r6.getDayNum()
            r10.dayNum = r6
            int r6 = r10.dayNum
            r7 = 8
            if (r6 != r7) goto L_0x015d
            return r4
        L_0x015d:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r6 = 23
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r6
            int r6 = r10.dayNum
            int r6 = r6 + r5
            r10.dayNum = r6
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r6 = r10.mListViewAdpter
            int r7 = r10.dayNum
            r6.setDayNum(r7)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r6 = r10.mListViewAdpter
            r6.setStartHour(r4)
            r10.mCanChangeTimeShow = r5
            android.os.Handler r4 = r10.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r10.mHandler
            r4.sendEmptyMessage(r3)
            android.os.Handler r3 = r10.mHandler
            r3.removeMessages(r2)
            android.os.Handler r3 = r10.mHandler
            r3.sendEmptyMessageDelayed(r2, r0)
            return r5
        L_0x018b:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r6 = r10.mListViewAdpter
            int r6 = r6.getDayNum()
            r10.dayNum = r6
            int r6 = r10.dayNum
            if (r6 != 0) goto L_0x0198
            return r4
        L_0x0198:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r6 = 24
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r6
            int r6 = r10.dayNum
            int r6 = r6 - r5
            r10.dayNum = r6
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r6 = r10.mListViewAdpter
            int r7 = r10.dayNum
            r6.setDayNum(r7)
            int r6 = r10.dayNum
            if (r6 != 0) goto L_0x01b8
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r4 = r10.mListViewAdpter
            int r6 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour()
            r4.setStartHour(r6)
            goto L_0x01bd
        L_0x01b8:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r6 = r10.mListViewAdpter
            r6.setStartHour(r4)
        L_0x01bd:
            r10.mCanChangeTimeShow = r5
            android.os.Handler r4 = r10.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r10.mHandler
            r4.sendEmptyMessage(r3)
            android.os.Handler r3 = r10.mHandler
            r3.removeMessages(r2)
            android.os.Handler r3 = r10.mHandler
            r3.sendEmptyMessageDelayed(r2, r0)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyUp>>>>" + keyCode + "  " + event.getAction());
        if (keyCode == 93 || keyCode == 130) {
            boolean isCurrentSourceDTV = CommonIntegration.getInstance().isCurrentSourceDTV();
            MtkLog.d(TAG, "isCurrentSourceDTV=" + isCurrentSourceDTV);
            if (isCurrentSourceDTV) {
                calledByScheduleList();
            } else {
                Toast.makeText(getApplicationContext(), "Source not available,please change to DTV source", 0).show();
            }
            return true;
        }
        switch (keyCode) {
            case 24:
            case 25:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void showBookedProgramsList() {
        this.mEPGBookedListDilog.show();
        this.mEPGBookedListDilog.updateAdapter();
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
        if (this.mListViewAdpter != null) {
            this.mListViewSelectedChild = (EPGChannelInfo) this.mListView.getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
            if (this.mListViewSelectedChild != null) {
                EPGLinearLayout childView = this.mListView.getSelectedDynamicLinearLayout(EPGConfig.SELECTED_CHANNEL_POSITION);
                if (this.mEPGPwdDialog != null && this.mEPGPwdDialog.isShowing()) {
                    this.mEPGPwdDialog.dismiss();
                    MtkLog.d(TAG, "do dismiss pwd dialog!");
                }
                if (this.needFirstShowLock) {
                    this.needFirstShowLock = false;
                    this.mHandler.removeMessages(EPGConfig.EPG_SHOW_LOCK_ICON);
                    this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_SHOW_LOCK_ICON, 500);
                }
                List<EPGProgramInfo> mProgramList = this.mListViewSelectedChild.getmTVProgramInfoList();
                StringBuilder sb = new StringBuilder();
                sb.append("1mProgramList>>>");
                sb.append(childView == null ? null : Integer.valueOf(childView.getmCurrentSelectPosition()));
                sb.append("   ");
                sb.append(mProgramList);
                MtkLog.e(TAG, sb.toString());
                if (mProgramList != null && childView != null && mProgramList.size() > 0 && childView.getmCurrentSelectPosition() >= 0 && childView.getmCurrentSelectPosition() < mProgramList.size()) {
                    this.mCurrentSelectedProgramInfo = mProgramList.get(childView.getmCurrentSelectPosition());
                    if (this.mCurrentSelectedProgramInfo != null) {
                        this.mProgramNameTv.setVisibility(0);
                        String title = this.mCurrentSelectedProgramInfo.getTitle();
                        if (title == null || title.equals("")) {
                            this.mProgramNameTv.setText(getString(R.string.nav_epg_no_program_title));
                        } else {
                            this.mProgramNameTv.setText(title);
                        }
                        this.mProgramTimeTv.setVisibility(0);
                        this.mProgramTimeTv.setText(EPGTimeConvert.getInstance().formatProgramTimeInfo(this.mCurrentSelectedProgramInfo, EPGUtil.judgeFormatTime12_24(this)));
                        this.mProgramType.setVisibility(0);
                        this.mProgramRating.setVisibility(0);
                        setProgramDetailTvState(this.mCurrentSelectedProgramInfo);
                        this.mProgramType.setText(childView.getProgramTypeByProgram(this.mCurrentSelectedProgramInfo));
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
                    this.mProgramNameTv.setVisibility(0);
                    this.mProgramNameTv.setText(getString(R.string.nav_epg_no_program_data));
                    if (this.mEnterTipTv.getText().toString().equals(getString(R.string.nav_epg_unschedule)) || this.mEnterTipTv.getText().toString().equals(getString(R.string.nav_epg_schedule))) {
                        this.mEnterTipTv.setText("");
                    }
                } else if (childView == null) {
                    MtkLog.d(TAG, "childView == null this.isFinishing():" + isFinishing());
                    if (!isFinishing()) {
                        this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                        this.mHandler.sendEmptyMessage(EPGConfig.EPG_PROGRAMINFO_SHOW);
                    }
                } else {
                    if (mProgramList != null) {
                        MtkLog.e(TAG, "mProgramList>>>" + mProgramList + "   " + mProgramList.size());
                    } else {
                        MtkLog.e(TAG, "mProgramList>>>" + mProgramList);
                    }
                    setProgramInfoViewsInVisiable();
                    this.mProgramNameTv.setVisibility(0);
                    this.mProgramNameTv.setText(getString(R.string.nav_epg_no_program_data));
                    if (this.mEnterTipTv.getText().toString().equals(getString(R.string.nav_epg_unschedule)) || this.mEnterTipTv.getText().toString().equals(getString(R.string.nav_epg_schedule))) {
                        this.mEnterTipTv.setText("");
                    }
                }
            } else {
                setProgramInfoViewsInVisiable();
            }
        } else {
            setProgramInfoViewsInVisiable();
        }
    }

    private void setProgramInfoViewsInVisiable() {
        MtkLog.d(TAG, "setProgramInfoViewsInVisiable");
        this.mProgramNameTv.setVisibility(4);
        this.mProgramTimeTv.setVisibility(4);
        this.mProgramDetailTv.setVisibility(4);
        this.mSttlImageView.setVisibility(4);
        this.mProgramType.setVisibility(4);
        this.mProgramRating.setVisibility(4);
        this.mPageInfoTv.setText("");
        this.mViewDetailTv.setText("");
        this.mProgramType.setText("");
        this.mProgramRating.setText("");
    }

    /* access modifiers changed from: private */
    public void setLockImageViewState(MtkTvChannelInfoBase mChannel) {
        int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
        MtkLog.e(TAG, "showFlag>>>" + showFlag);
        if (showFlag == 0) {
            this.isSpecialState = true;
            this.mLockImageView.setVisibility(0);
            this.mEnterTipTv.setText(getString(R.string.nav_epg_unlock));
            return;
        }
        this.isSpecialState = false;
        this.mLockImageView.setVisibility(4);
        this.mEnterTipTv.setText("");
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
        MtkLog.e(TAG, "setProgramDetailTvState>>" + showFlag + ">>>" + this.isSpecialState + ">>>" + childProgramInfo);
        if (showFlag == 0 && this.mLockImageView.getVisibility() != 0) {
            setLockIconVisibility(true);
        }
        if (showFlag == 0 || this.isSpecialState) {
            this.mProgramDetailTv.setVisibility(4);
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
        } else if (childProgramInfo != null) {
            this.mProgramDetailTv.setVisibility(0);
            String mDetailContent = childProgramInfo.getDescribe();
            if (TextUtils.isEmpty(mDetailContent)) {
                this.mViewDetailTv.setText("");
                this.mPageInfoTv.setText("");
                this.mProgramDetailTv.setText(getString(R.string.nav_epg_no_program_detail));
            } else {
                this.mProgramDetailTv.setText(mDetailContent);
                initProgramDetailContent();
            }
            this.mLockImageView.setVisibility(4);
            if (childProgramInfo.getmStartTime().longValue() == 0 || EPGUtil.getCurrentTime() >= childProgramInfo.getmStartTime().longValue()) {
                this.mEnterTipTv.setText("");
            } else if (programIsInDB(childProgramInfo)) {
                this.mEnterTipTv.setText(getString(R.string.nav_epg_unschedule));
            } else {
                this.mEnterTipTv.setText(getString(R.string.nav_epg_schedule));
            }
        } else {
            this.mProgramDetailTv.setText("");
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
            this.mTotalPage = 0;
            this.mEnterTipTv.setText("");
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
                EPGSaActivity.this.initProgramDetailContent();
            }
        }, 10);
    }

    public void changeBottomViewText(boolean isEnter, int keyCode) {
        if (isEnter) {
            savePreValues();
            this.mPrevDayTv.setText("");
            this.mNextDayTv.setText("");
            this.mViewDetailTv.setText("");
            if (keyCode != 7) {
                if (keyCode != 23) {
                    if (keyCode != 30) {
                        if (!(keyCode == 33 || keyCode == 66)) {
                            if (keyCode != 186) {
                                return;
                            }
                        }
                    }
                    this.mEnterTipTv.setText("");
                    this.mZeroTipTv.setText("");
                    this.mTypeFilter.setText(getResources().getString(R.string.setup_exit));
                    return;
                }
                if (this.isSupportPvr) {
                    this.mEnterTipTv.setText("");
                    this.mZeroTipTv.setText("");
                    this.mTypeFilter.setText("");
                }
            } else if (this.isSupportPvr) {
                this.mEnterTipTv.setText("");
                this.mZeroTipTv.setText(getString(R.string.setup_exit));
                this.mTypeFilter.setText("");
            }
        } else {
            this.mPrevDayTv.setText(this.preValues[0]);
            this.mNextDayTv.setText(this.preValues[1]);
            this.mViewDetailTv.setText(this.preValues[2]);
            if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
                this.mEnterTipTv.setText(this.preValues[3]);
            } else if (this.mCurrentSelectedProgramInfo == null || this.mCurrentSelectedProgramInfo.getmStartTime().longValue() == 0 || EPGUtil.getCurrentTime() >= this.mCurrentSelectedProgramInfo.getmStartTime().longValue()) {
                this.mEnterTipTv.setText("");
            } else if (programIsInDB(this.mCurrentSelectedProgramInfo)) {
                this.mEnterTipTv.setText(getString(R.string.nav_epg_unschedule));
            } else {
                this.mEnterTipTv.setText(getString(R.string.nav_epg_schedule));
            }
            this.mZeroTipTv.setText(this.preValues[4]);
            this.mTypeFilter.setText(getResources().getString(R.string.epg_bottom_type_filter));
        }
    }

    private void savePreValues() {
        this.preValues[0] = this.mPrevDayTv.getText().toString();
        this.preValues[1] = this.mNextDayTv.getText().toString();
        this.preValues[2] = this.mViewDetailTv.getText().toString();
        this.preValues[3] = this.mEnterTipTv.getText().toString();
        this.preValues[4] = this.mZeroTipTv.getText().toString();
    }

    public void setIsNeedFirstShowLock(boolean isNeedFirstShowLock) {
        this.needFirstShowLock = isNeedFirstShowLock;
    }

    public void onDismiss(DialogInterface dialog) {
        MtkLog.d(TAG, "PWD onDismiss!!>>" + this.needFirstShowLock);
        showSelectedProgramInfo();
        changeBottomViewText(false, 0);
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
            if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this) == null) {
                ScheduleListItemInfoDialog mDialog = new ScheduleListItemInfoDialog(this, item);
                mDialog.setEpgFlag(true);
                mDialog.show();
            } else if (!ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).isShowing()) {
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).setEpgFlag(true);
                ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this).show();
            }
        }
        MtkLog.d("Timeshift_PVR", "calledByScheduleList()");
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

    private boolean programIsInDB(EPGProgramInfo tvProgramInfo) {
        DBMgrProgramList mDBMgrProgramList = DBMgrProgramList.getInstance(this);
        mDBMgrProgramList.getReadableDB();
        List<EPGBookListViewDataItem> mBookedList = mDBMgrProgramList.getProgramList();
        mDBMgrProgramList.closeDB();
        MtkLog.d(TAG, "setBookVisibility>>>" + mBookedList.size());
        for (EPGBookListViewDataItem tempInfo : mBookedList) {
            if (tempInfo.mProgramStartTime == tvProgramInfo.getmStartTime().longValue() && tempInfo.mChannelId == tvProgramInfo.getChannelId() && tempInfo.mProgramId == tvProgramInfo.getProgramId()) {
                return true;
            }
        }
        return false;
    }

    public void showBookConfirm(final boolean isBooking) {
        String message;
        String message2 = this.currentBookedProgram.getTitle();
        if (isBooking) {
            message = message2 + " " + getString(R.string.nav_epg_book_program_tip);
        } else {
            message = message2 + " " + getString(R.string.nav_epg_unbook_program_tip);
        }
        if (this.mBookProgramConfirmDialog == null) {
            this.mBookProgramConfirmDialog = new TurnkeyCommDialog(this, 3);
            this.mBookProgramConfirmDialog.setMessage(message);
        } else {
            this.mBookProgramConfirmDialog.setMessage(message);
            this.mBookProgramConfirmDialog.setText();
        }
        this.mBookProgramConfirmDialog.setButtonYesName(getString(R.string.menu_ok));
        this.mBookProgramConfirmDialog.setButtonNoName(getString(R.string.menu_cancel));
        this.mBookProgramConfirmDialog.show();
        this.mBookProgramConfirmDialog.getButtonYes().requestFocus();
        this.mBookProgramConfirmDialog.setPositon(-20, 70);
        this.mBookProgramConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                EPGSaActivity.this.mBookProgramConfirmDialog.dismiss();
                EPGSaActivity.this.notifyEPGLinearlayoutRefresh();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23)) {
                    return false;
                }
                EPGChannelInfo tvChannel = (EPGChannelInfo) EPGSaActivity.this.mListView.getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
                MtkLog.d(EPGSaActivity.TAG, "tvChannel>>>" + tvChannel);
                if (isBooking) {
                    EPGSaActivity.this.saveBookProgram(tvChannel, EPGSaActivity.this.currentBookedProgram);
                    AlarmMgr.getInstance(EPGSaActivity.this.getApplicationContext()).startTimer(false);
                } else {
                    EPGSaActivity.this.deleteBookProgram(tvChannel, EPGSaActivity.this.currentBookedProgram);
                }
                EPGSaActivity.this.notifyEPGLinearlayoutRefresh();
                EPGSaActivity.this.mBookProgramConfirmDialog.dismiss();
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
                EPGSaActivity.this.mBookProgramConfirmDialog.dismiss();
                EPGSaActivity.this.notifyEPGLinearlayoutRefresh();
                return true;
            }
        });
        this.mBookProgramConfirmDialog.getButtonYes().setOnKeyListener(yesListener);
    }

    /* access modifiers changed from: private */
    public void saveBookProgram(EPGChannelInfo currentChannel, EPGProgramInfo currentBookedProgram2) {
        String channel;
        MtkLog.d(TAG, "currentChannel>>>" + currentBookedProgram2.getChannelId() + "   " + currentBookedProgram2.getProgramId() + "   " + currentChannel.getmChanelNumString() + "   " + currentChannel.getmSubNum() + "   " + currentChannel.getName() + "\n" + currentBookedProgram2.getTitle() + "    " + currentBookedProgram2.getmStartTime());
        if (currentChannel.getmSubNum() == null || "".equals(currentChannel.getmSubNum())) {
            channel = currentChannel.getmChanelNumString() + "   " + currentChannel.getName();
        } else {
            channel = currentChannel.getmChanelNumString() + "." + currentChannel.getmSubNum() + "   " + currentChannel.getName();
        }
        DBMgrProgramList.getInstance(this).getWriteableDB();
        DBMgrProgramList.getInstance(this).addProgram(new EPGBookListViewDataItem(currentBookedProgram2.getChannelId(), currentBookedProgram2.getProgramId(), channel, currentBookedProgram2.getmTitle(), currentBookedProgram2.getmStartTime().longValue()));
        DBMgrProgramList.getInstance(this).closeDB();
    }

    /* access modifiers changed from: private */
    public void deleteBookProgram(EPGChannelInfo currentChannel, EPGProgramInfo currentBookedProgram2) {
        String channel;
        if (currentChannel.getmSubNum() == null || "".equals(currentChannel.getmSubNum())) {
            channel = currentChannel.getmChanelNumString() + "   " + currentChannel.getName();
        } else {
            channel = currentChannel.getmChanelNumString() + "." + currentChannel.getmSubNum() + "   " + currentChannel.getName();
        }
        DBMgrProgramList.getInstance(this).getWriteableDB();
        DBMgrProgramList.getInstance(this).deleteProgram(new EPGBookListViewDataItem(this.currentBookedProgram.getChannelId(), this.currentBookedProgram.getProgramId(), channel, this.currentBookedProgram.getmTitle(), this.currentBookedProgram.getmStartTime().longValue()));
        DBMgrProgramList.getInstance(this).closeDB();
        SaveValue.getInstance(this).removekey(currentBookedProgram2.getmStartTime() + "");
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "SA EPG updateComponentStatus>>>" + statusID + ">>" + value);
    }

    private void removeOutOfDateBookEvents() {
        MtkLog.d(TAG, "removeOutOfDateBookEvents ~");
        DBMgrProgramList mDBMgrProgramList = DBMgrProgramList.getInstance(this);
        mDBMgrProgramList.getWriteableDB();
        mDBMgrProgramList.getProgramListWithDelete();
        mDBMgrProgramList.closeDB();
    }
}
