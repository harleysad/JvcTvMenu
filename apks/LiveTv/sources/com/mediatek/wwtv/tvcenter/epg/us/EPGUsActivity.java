package com.mediatek.wwtv.tvcenter.epg.us;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvEventATSC;
import com.mediatek.twoworlds.tv.MtkTvEventATSCBase;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import java.util.ArrayList;
import java.util.List;

public class EPGUsActivity extends BaseActivity implements View.OnKeyListener, AdapterView.OnItemSelectedListener, ComponentStatusListener.ICStatusListener {
    private static final int PER_PAGE_LINE = 5;
    private static final int REQUEST_TIME_OUT = 5000;
    private static final String TAG = "EPGUsActivity";
    /* access modifiers changed from: private */
    public ImageView arrowDown;
    /* access modifiers changed from: private */
    public ImageView arrowUp;
    private LinearLayout bottomParentLayout;
    private TextView centerChannelTextView;
    /* access modifiers changed from: private */
    public List<ListItemData> dataGroup = new ArrayList();
    /* access modifiers changed from: private */
    public int dayOffset = 0;
    /* access modifiers changed from: private */
    public long dayTime;
    private CommonIntegration integration;
    /* access modifiers changed from: private */
    public TextView leftChannelTextView;
    /* access modifiers changed from: private */
    public EPGUsListView listView;
    /* access modifiers changed from: private */
    public View lockIconView;
    /* access modifiers changed from: private */
    public List<Integer> mAlreadyRequestList;
    private Runnable mCaculatePrePagesRequesListRunnable = new Runnable() {
        public void run() {
            List unused = EPGUsActivity.this.mCalculatePrePagesRequestList = EPGUsActivity.this.usManager.getDataGroup(EPGUsActivity.this.mReGetDataRequsetTime, EPGUsActivity.this.mReGetDataGetCount);
            MtkLog.d(EPGUsActivity.TAG, "11111111mCalculatePrePagesRequestList>>>" + EPGUsActivity.this.mCalculatePrePagesRequestList.size());
            if (EPGUsActivity.this.mCalculatePrePagesRequestList.size() == 0 && EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                EPGUsActivity.this.progressDialog.dismiss();
            }
        }
    };
    /* access modifiers changed from: private */
    public List<Integer> mCalculatePrePagesAlreadyRequestList;
    /* access modifiers changed from: private */
    public List<Integer> mCalculatePrePagesRequestList;
    /* access modifiers changed from: private */
    public boolean mCanCalcPrePages;
    /* access modifiers changed from: private */
    public boolean mCanChangeChannel;
    private boolean mCanJudgeNextPage;
    private boolean mCanKeyUpToExit;
    private Runnable mChangeChannelNextRunnable = new Runnable() {
        public void run() {
            if (!EPGUsActivity.this.usManager.onRightChannel()) {
                boolean unused = EPGUsActivity.this.mCanChangeChannel = true;
                EPGUsChannelManager epgcm = EPGUsChannelManager.getInstance(EPGUsActivity.this);
                if (epgcm.isPreChannelDig() != epgcm.isCurrentChannelDig()) {
                    EPGUsActivity.this.sendChangeSourceMessage();
                }
            }
        }
    };
    private Runnable mChangeChannelPreRunnable = new Runnable() {
        public void run() {
            if (!EPGUsActivity.this.usManager.onLeftChannel()) {
                boolean unused = EPGUsActivity.this.mCanChangeChannel = true;
                EPGUsChannelManager epgcm = EPGUsChannelManager.getInstance(EPGUsActivity.this);
                if (epgcm.isPreChannelDig() != epgcm.isCurrentChannelDig()) {
                    EPGUsActivity.this.sendChangeSourceMessage();
                }
            }
        }
    };
    private long mCurrentKeyPageTime;
    private int mCurrentPage = 1;
    /* access modifiers changed from: private */
    public long mCurrentTime;
    private EPGPwdDialog mEPGPwdDialog;
    /* access modifiers changed from: private */
    public final MtkTvEventATSCBase mEpgEevent = MtkTvEventATSC.getInstance();
    private EpgUsUpdate mEpgUsUpdate;
    private Runnable mGetRequestListRunnable = new Runnable() {
        public void run() {
            List unused = EPGUsActivity.this.mRequestList = EPGUsActivity.this.usManager.getDataGroup(EPGUsActivity.this.mReGetDataRequsetTime, EPGUsActivity.this.mReGetDataGetCount);
        }
    };
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtkTvEventInfoBase eventInfo;
            Message message = msg;
            switch (message.what) {
                case 262:
                    if (EPGUsActivity.this.usManager != null && EPGUsActivity.this.mHandler != null) {
                        EPGUsActivity.this.usManager.initChannels();
                        EPGUsActivity.this.initChannelView();
                        return;
                    }
                    return;
                case 1001:
                    long unused = EPGUsActivity.this.mCurrentTime = EPGUtil.getCurrentTime();
                    if (EPGUsActivity.this.mCurrentTime - EPGUsActivity.this.mLastTime < 0) {
                        long unused2 = EPGUsActivity.this.mLastTime = EPGUsActivity.this.mCurrentTime;
                        removeMessages(1005);
                        EPGUsActivity.this.mHandler.removeMessages(1004);
                        if (EPGUsActivity.this.progressDialog != null && !EPGUsActivity.this.progressDialog.isShowing()) {
                            EPGUsActivity.this.progressDialog.show();
                        }
                        EPGUsActivity.this.setPageNoVisivle();
                        EPGUsActivity.this.mHandler.sendEmptyMessageDelayed(1004, 1000);
                    }
                    if (EPGUsActivity.this.mCurrentTime - EPGUsActivity.this.mLastTime > 5) {
                        long unused3 = EPGUsActivity.this.mLastTime = EPGUsActivity.this.mCurrentTime;
                    }
                    removeMessages(1001);
                    EPGUsActivity.this.timeTextView.setText(EPGUsActivity.this.usManager.getTimeToShow());
                    if (CommonIntegration.getInstance().isCurrentSourceATV()) {
                        EPGUsActivity.this.timeTextView.setVisibility(4);
                    } else {
                        EPGUsActivity.this.timeTextView.setVisibility(0);
                    }
                    sendEmptyMessageDelayed(1001, 1000);
                    return;
                case 1002:
                    removeMessages(1002);
                    EPGUsActivity.this.setListViewAdapter();
                    return;
                case 1004:
                    MtkLog.d(EPGUsActivity.TAG, "Message_ReFreshData");
                    EPGUsActivity.this.mHandler.removeMessages(1004);
                    if (EPGUsActivity.this.usManager != null) {
                        EPGUsActivity.this.usManager.initChannels();
                    }
                    EPGUsActivity.this.reRefresh();
                    return;
                case 1005:
                    removeMessages(1005);
                    if (EPGUsActivity.this.mJudgeHasNextPage) {
                        EPGUsManager.requestComplete = true;
                        boolean unused4 = EPGUsActivity.this.mJudgeHasNextPage = false;
                        return;
                    }
                    MtkLog.e(EPGUsActivity.TAG, "progressDialog:finish:" + EPGUsActivity.this.progressDialog);
                    if (EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                        EPGUsActivity.this.progressDialog.dismiss();
                    }
                    if (EPGUsActivity.this.usManager == null || EPGUsActivity.this.usManager.getDataGroup() == null || EPGUsActivity.this.usManager.getDataGroup().size() != 0) {
                        EPGUsManager.requestComplete = true;
                        if (CommonIntegration.getInstance().is3rdTVSource()) {
                            MtkLog.d(EPGUsActivity.TAG, "progressDialog:finish, is3rdTVSource");
                            EPGUsActivity.this.setListViewAdapter();
                            return;
                        }
                        return;
                    } else if (EPGUsActivity.this.mPrePageStartTimeList == null || EPGUsActivity.this.mPrePageStartTimeList.size() <= 0) {
                        EPGUsActivity.this.usManager.getDataGroup().add(EPGUsActivity.this.usManager.getNoProItem());
                        int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
                        MtkLog.d(EPGUsActivity.TAG, "Message_ShouldFinish>>>" + showFlag);
                        if (showFlag == 0) {
                            EPGUsActivity.this.usManager.getDataGroup().get(0).setBlocked(true);
                        }
                        EPGUsActivity.this.setListViewAdapter();
                        return;
                    } else {
                        removeMessages(1005);
                        EPGUsActivity.this.mHandler.removeMessages(1004);
                        if (EPGUsActivity.this.progressDialog != null && !EPGUsActivity.this.progressDialog.isShowing()) {
                            EPGUsActivity.this.progressDialog.show();
                        }
                        EPGUsActivity.this.setPageNoVisivle();
                        EPGUsActivity.this.mHandler.sendEmptyMessageDelayed(1004, 1000);
                        return;
                    }
                case 4104:
                    MtkLog.d(EPGUsActivity.TAG, "ChannelListDialog.MESSAGE_DEFAULT_CAN_CHANGECHANNEL_DELAY reach");
                    boolean unused5 = EPGUsActivity.this.mCanChangeChannel = true;
                    return;
                case TvCallbackConst.MSG_CB_CHANNELIST /*1879048194*/:
                case TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE /*1879048209*/:
                    MtkLog.d(EPGUsActivity.TAG, "US EPG update channel list>" + ((TvCallbackData) message.obj).param2);
                    sendEmptyMessageDelayed(262, 1000);
                    return;
                case TvCallbackConst.MSG_CB_SVCTX_NOTIFY /*1879048198*/:
                    TvCallbackData svctx_data = (TvCallbackData) message.obj;
                    MtkLog.d(EPGUsActivity.TAG, "svctx notify, type: " + svctx_data.param1);
                    if (svctx_data.param1 == 5 || svctx_data.param1 == 0) {
                        if (EPGUsActivity.this.progressDialog != null && !EPGUsActivity.this.progressDialog.isShowing()) {
                            EPGUsActivity.this.progressDialog.show();
                        }
                        EPGUsActivity.this.setPageNoVisivle();
                        EPGUsActivity.this.mHandler.removeMessages(1004);
                        EPGUsActivity.this.mHandler.sendEmptyMessageDelayed(1004, 200);
                        return;
                    }
                    return;
                case TvCallbackConst.MSG_CB_EAS_MSG /*1879048221*/:
                    if (((TvCallbackData) message.obj).param1 == 1) {
                        Intent intent = new Intent(EPGUsActivity.this, TurnkeyUiMainActivity.class);
                        intent.putExtra("NavComponentShow", 16777217);
                        EPGUsActivity.this.setResult(NavBasic.NAV_RESULT_CODE_MENU, intent);
                        EPGUsActivity.this.finish();
                        return;
                    }
                    return;
                case TvCallbackConst.MSG_CB_ATSC_EVENT_MSG /*1879048230*/:
                    TvCallbackData data = (TvCallbackData) message.obj;
                    MtkLog.e(EPGUsActivity.TAG, "Epg message:type:" + data.param1 + "==>" + data.param2 + "==>" + data.param3 + "==>" + data.param4);
                    if (EPGUsActivity.this.usManager == null) {
                        MtkLog.d(EPGUsActivity.TAG, "usManager is null hand callback MSG_CB_ATSC_EVENT_MSG");
                        return;
                    }
                    int preDataGroupSize = EPGUsActivity.this.usManager.groupSize();
                    if (EPGUsActivity.this.mJudgeHasNextPage && !EPGUsActivity.this.mRequestList.contains(Integer.valueOf(data.param2)) && EPGUsActivity.this.mJudgeNextpageRequestList.contains(Integer.valueOf(data.param2))) {
                        MtkLog.d(EPGUsActivity.TAG, "dddddd" + data.param1 + "==>" + data.param2 + "==>" + data.param3 + "==>" + data.param4);
                        removeMessages(1005);
                        if (EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                            EPGUsActivity.this.progressDialog.dismiss();
                        }
                        ListItemData judgeEventItem = EPGUsActivity.this.usManager.getEventItem(data.param2);
                        if (judgeEventItem == null || judgeEventItem.getMillsDurationTime() <= 0) {
                            if (judgeEventItem != null) {
                                MtkLog.d(EPGUsActivity.TAG, "dddddd==>" + judgeEventItem.getMillsDurationTime());
                            } else {
                                MtkLog.d(EPGUsActivity.TAG, "dddddd==>null");
                            }
                            EPGUsManager.requestComplete = true;
                            EPGUsActivity.this.arrowDown.setVisibility(4);
                        } else {
                            EPGUsManager.requestComplete = false;
                            if (EPGUsActivity.this.listView.getVisibility() == 0) {
                                EPGUsActivity.this.arrowDown.setVisibility(0);
                            } else {
                                EPGUsActivity.this.arrowDown.setVisibility(4);
                            }
                        }
                        EPGUsActivity.this.refreshFoot();
                        boolean unused6 = EPGUsActivity.this.mJudgeHasNextPage = false;
                        if (EPGUsActivity.this.mEpgEevent != null) {
                            EPGUsActivity.this.mEpgEevent.freeEvent(data.param2);
                        }
                        if (!EPGUsManager.requestComplete && !EPGUsActivity.this.mJudgeHasNextDay && !EPGUsActivity.this.mNoNeedRequestNextDayData && EPGUsActivity.this.dayOffset == EPGUsActivity.this.mMaxDayNum) {
                            EPGUsActivity.this.judgeHasNextDayData();
                            return;
                        }
                        return;
                    } else if (EPGUsActivity.this.mJudgeHasNextDay && !EPGUsActivity.this.mRequestList.contains(Integer.valueOf(data.param2)) && EPGUsActivity.this.mJudgeNextDayRequestList.contains(Integer.valueOf(data.param2))) {
                        removeMessages(1005);
                        if (EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                            EPGUsActivity.this.progressDialog.dismiss();
                        }
                        ListItemData judgeEventItem2 = EPGUsActivity.this.usManager.getEventItem(data.param2);
                        if (judgeEventItem2 == null || judgeEventItem2.getMillsDurationTime() <= 0) {
                            if (judgeEventItem2 != null) {
                                MtkLog.d(EPGUsActivity.TAG, "mJudgeHasNextDay==>" + judgeEventItem2.getMillsDurationTime());
                            } else {
                                MtkLog.d(EPGUsActivity.TAG, "mJudgeHasNextDay==>null");
                            }
                            boolean unused7 = EPGUsActivity.this.mNoNeedRequestNextDayData = true;
                            if (EPGUsActivity.this.mMaxDayNum < EPGUsActivity.this.dayOffset) {
                                int unused8 = EPGUsActivity.this.mMaxDayNum = EPGUsActivity.this.dayOffset;
                            }
                        } else {
                            int unused9 = EPGUsActivity.this.mMaxDayNum = EPGUsActivity.this.dayOffset + 1;
                        }
                        EPGUsActivity.this.refreshFoot();
                        boolean unused10 = EPGUsActivity.this.mJudgeHasNextDay = false;
                        if (EPGUsActivity.this.mEpgEevent != null) {
                            EPGUsActivity.this.mEpgEevent.freeEvent(data.param2);
                            return;
                        }
                        return;
                    } else if (EPGUsActivity.this.mCalculatePrePagesRequestList == null || !EPGUsActivity.this.mCalculatePrePagesRequestList.contains(Integer.valueOf(data.param2))) {
                        if (data.param2 != 0 && EPGUsActivity.this.mRequestList.contains(Integer.valueOf(data.param2)) && !EPGUsActivity.this.mAlreadyRequestList.contains(Integer.valueOf(data.param2))) {
                            EPGUsActivity.this.mAlreadyRequestList.add(Integer.valueOf(data.param2));
                            EPGUsActivity.access$2508(EPGUsActivity.this);
                            if (EPGUsActivity.this.mRequestList.size() == EPGUsActivity.this.mAlreadyRequestList.size()) {
                                EPGUsActivity.this.judegeOnePageHasNextDay(EPGUsActivity.this.usManager.getDataGroup());
                            }
                        }
                        if ((data.param1 == 2 || data.param1 == 5 || data.param1 == 3 || data.param1 == 4) && EPGUsActivity.this.mRequestList.contains(Integer.valueOf(data.param2))) {
                            ListItemData eventItem = EPGUsActivity.this.usManager.getEventItem(data.param2);
                            if (data.param1 == 3 || data.param1 == 4) {
                                MtkLog.d(EPGUsActivity.TAG, "data.param1==  size == >>" + data.param1);
                                if (EPGUsActivity.this.mRequestList != null) {
                                    for (int i = 0; i < EPGUsActivity.this.mRequestList.size(); i++) {
                                        EPGUsActivity.this.mEpgEevent.freeEvent(((Integer) EPGUsActivity.this.mRequestList.get(i)).intValue());
                                    }
                                    EPGUsActivity.this.mRequestList.clear();
                                }
                                if (EPGUsActivity.this.progressDialog != null && !EPGUsActivity.this.progressDialog.isShowing()) {
                                    EPGUsActivity.this.progressDialog.show();
                                }
                                EPGUsActivity.this.setPageNoVisivle();
                                EPGUsActivity.this.mHandler.removeMessages(1004);
                                EPGUsActivity.this.mHandler.sendEmptyMessageDelayed(1004, 3000);
                            } else if (eventItem != null) {
                                removeMessages(1005);
                                if (EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                                    EPGUsActivity.this.progressDialog.dismiss();
                                }
                                if (EPGUsActivity.this.mNeedClearNextOldData) {
                                    if (EPGUsActivity.this.mLastPageFirstTime <= 0 || EPGUsActivity.this.mLastPageFirstTime <= EPGUtil.getCurrentTime()) {
                                        MtkLog.d(EPGUsActivity.TAG, "add -----------------------0");
                                        EPGUsActivity.this.mPrePageStartTimeList.add(0L);
                                    } else {
                                        EPGUsActivity.this.mPrePageStartTimeList.add(Long.valueOf(EPGUsActivity.this.mLastPageFirstTime));
                                    }
                                    EPGUsActivity.this.usManager.clearDataGroup();
                                    if (EPGUsActivity.this.mListAdapter != null) {
                                        EPGUsActivity.this.mListAdapter.notifyDataSetChanged();
                                    }
                                    boolean unused11 = EPGUsActivity.this.mNeedClearNextOldData = false;
                                    boolean unused12 = EPGUsActivity.this.mIsNextpage = true;
                                }
                                if (EPGUsActivity.this.mNeedClearPreOldData) {
                                    if (EPGUsActivity.this.mPrePageStartTimeList.size() > 0) {
                                        EPGUsActivity.this.mPrePageStartTimeList.remove(EPGUsActivity.this.mPrePageStartTimeList.size() - 1);
                                    }
                                    EPGUsActivity.this.usManager.clearDataGroup();
                                    if (EPGUsActivity.this.mListAdapter != null) {
                                        EPGUsActivity.this.mListAdapter.notifyDataSetChanged();
                                    }
                                    boolean unused13 = EPGUsActivity.this.mNeedClearPreOldData = false;
                                    boolean unused14 = EPGUsActivity.this.mIsPrepage = true;
                                }
                                if (data.param1 == 2) {
                                    EPGUsActivity.this.usManager.addDataGroupItem(eventItem);
                                } else {
                                    EPGUsActivity.this.usManager.updateDataGroup(eventItem, data.param2);
                                }
                                MtkLog.d(EPGUsActivity.TAG, "mNeedFilterEvent>>>" + EPGUsActivity.this.mNeedFilterEvent + "  " + EPGUsActivity.this.usManager.groupSize());
                                if (EPGUsActivity.this.mNeedFilterEvent && EPGUsActivity.this.usManager.groupSize() > 0) {
                                    if (EPGUsActivity.this.usManager.getDataGroup().get(0).getMillsStartTime() > 0 && EPGUsActivity.this.usManager.getDataGroup().get(0).getMillsStartTime() < EPGUsActivity.this.startTime + EPGUsActivity.this.dayTime) {
                                        EPGUsActivity.this.mEpgEevent.freeEvent(EPGUsActivity.this.usManager.getDataGroup().get(0).getEventId());
                                        EPGUsActivity.this.mRequestList.remove(Integer.valueOf(EPGUsActivity.this.usManager.getDataGroup().get(0).getEventId()));
                                        EPGUsActivity.this.mAlreadyRequestList.remove(Integer.valueOf(EPGUsActivity.this.usManager.getDataGroup().get(0).getEventId()));
                                        EPGUsActivity.this.usManager.getDataGroup().remove(0);
                                    }
                                    if (EPGUsActivity.this.usManager.groupSize() > 0 && EPGUsActivity.this.mAlreadyRequestList.size() >= 4) {
                                        boolean unused15 = EPGUsActivity.this.mNeedFilterEvent = false;
                                    }
                                }
                                if (EPGUsActivity.this.usManager.groupSize() > 4) {
                                    EPGUsActivity.this.usManager.getDataGroup().remove(EPGUsActivity.this.usManager.groupSize() - 1);
                                }
                                if (!(EPGUsActivity.this.mListAdapter == null || preDataGroupSize == EPGUsActivity.this.usManager.groupSize())) {
                                    EPGUsActivity.this.mListAdapter.notifyDataSetChanged();
                                }
                                sendEmptyMessageDelayed(1002, 100);
                                if (EPGUsActivity.this.mCanCalcPrePages && EPGUsActivity.this.usManager.groupSize() > 0) {
                                    MtkLog.d(EPGUsActivity.TAG, "xinsheng eventItem.getMillsStartTime() >>" + eventItem.getMillsStartTime());
                                    boolean unused16 = EPGUsActivity.this.mCanCalcPrePages = false;
                                    EPGUsActivity.this.calculatPrePages(0, 4, true);
                                }
                            }
                        }
                        MtkLog.e(EPGUsActivity.TAG, "Epg message:reCount:" + EPGUsActivity.this.reCount);
                        if (data.param1 == 0 && EPGUsActivity.this.usManager != null && EPGUsActivity.this.usManager.getDataGroup() != null && EPGUsActivity.this.usManager.clearEvent(data.param2)) {
                            if (EPGUsActivity.this.mListAdapter != null) {
                                EPGUsActivity.this.mListAdapter.notifyDataSetChanged();
                            }
                            if (EPGUsActivity.this.mRequestList != null && EPGUsActivity.this.mRequestList.contains(Integer.valueOf(data.param2))) {
                                EPGUsActivity.this.mRequestList.remove(Integer.valueOf(data.param2));
                                EPGUsActivity.access$2510(EPGUsActivity.this);
                            }
                            boolean unused17 = EPGUsActivity.this.mIsNextpage = true;
                            sendEmptyMessageDelayed(1002, 100);
                            if (((EPGUsActivity.this.usManager.getDataGroup() != null && EPGUsActivity.this.usManager.getDataGroup().size() == 0) || (EPGUsActivity.this.usManager.getDataGroup() != null && EPGUsActivity.this.usManager.getDataGroup().size() == 1 && EPGUsActivity.this.usManager.getDataGroup().get(0).getMillsStartTime() == 0)) && !EPGUsActivity.this.mNeedClearNextOldData && !EPGUsActivity.this.mNeedClearPreOldData) {
                                MtkLog.d(EPGUsActivity.TAG, "data.param1==0  size == 0");
                                if (EPGUsActivity.this.progressDialog != null && !EPGUsActivity.this.progressDialog.isShowing()) {
                                    EPGUsActivity.this.progressDialog.show();
                                }
                                EPGUsActivity.this.setPageNoVisivle();
                                EPGUsActivity.this.mHandler.removeMessages(1004);
                                EPGUsActivity.this.mHandler.sendEmptyMessageDelayed(1004, 3000);
                                return;
                            } else if ((EPGUsActivity.this.mNeedClearNextOldData || EPGUsActivity.this.mNeedClearPreOldData) && EPGUsActivity.this.usManager.getDataGroup() != null && EPGUsActivity.this.usManager.getDataGroup().size() == 0) {
                                if (EPGUsActivity.this.progressDialog != null && !EPGUsActivity.this.progressDialog.isShowing()) {
                                    EPGUsActivity.this.progressDialog.show();
                                }
                                EPGUsActivity.this.mHandler.removeMessages(1004);
                                return;
                            } else if (EPGUsActivity.this.mRequestList != null && EPGUsActivity.this.mRequestList.size() < 4 && EPGUsActivity.this.usManager.getDataGroup().size() < 4) {
                                EPGUsActivity.this.mHandler.removeMessages(1004);
                                EPGUsActivity.this.mHandler.sendEmptyMessageDelayed(1004, MessageType.delayMillis4);
                                return;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        MtkLog.d(EPGUsActivity.TAG, "xinsheng mCalculatePrePagesRequestList>>>>" + EPGUsActivity.this.mCalculatePrePagesRequestList.size() + "  " + data.param1);
                        if (!EPGUsActivity.this.mCalculatePrePagesAlreadyRequestList.contains(Integer.valueOf(data.param2))) {
                            EPGUsActivity.this.mCalculatePrePagesAlreadyRequestList.add(Integer.valueOf(data.param2));
                        }
                        if ((data.param1 == 2 || data.param1 == 5) && (eventInfo = EPGUsActivity.this.usManager.getEvent(data.param2)) != null && eventInfo.getStartTime() > 0 && EPGUsActivity.this.usManager.getDataGroup() != null && EPGUsActivity.this.usManager.groupSize() > 0) {
                            long firstStartTime = EPGUsActivity.this.usManager.getDataGroup().get(0).getMillsStartTime();
                            MtkLog.d(EPGUsActivity.TAG, "xinsheng eventInfo>>>" + eventInfo.getStartTime() + "  " + firstStartTime + "  " + EPGUsActivity.this.mPrePagesStartTimes.size() + "  " + EPGUsActivity.this.mCalculatePrePagesAlreadyRequestList.size());
                            if (eventInfo.getStartTime() < firstStartTime) {
                                if (EPGUsActivity.this.mPreStartTime < eventInfo.getStartTime() + eventInfo.getDuration()) {
                                    long unused18 = EPGUsActivity.this.mPreStartTime = eventInfo.getStartTime() + eventInfo.getDuration();
                                }
                                if (EPGUsActivity.this.mPrePagesStartTimes != null) {
                                    if (EPGUsActivity.this.mPrePagesStartTimes.size() == 0) {
                                        EPGUsActivity.this.mPrePagesStartTimes.add(Long.valueOf(eventInfo.getStartTime()));
                                    } else if (((Long) EPGUsActivity.this.mPrePagesStartTimes.get(EPGUsActivity.this.mPrePagesStartTimes.size() - 1)).longValue() < eventInfo.getStartTime()) {
                                        EPGUsActivity.this.mPrePagesStartTimes.add(Long.valueOf(eventInfo.getStartTime()));
                                    }
                                    if (EPGUsActivity.this.mCalculatePrePagesAlreadyRequestList.size() == EPGUsActivity.this.mCalculatePrePagesRequestList.size() && EPGUsActivity.this.mPrePagesStartTimes.size() % 4 == 0) {
                                        EPGUsActivity.this.calculatPrePages(EPGUsActivity.this.mPreStartTime, 4, false);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            if (EPGUsActivity.this.mCalculatePrePagesRequestList != null) {
                                for (int i2 = 0; i2 < EPGUsActivity.this.mCalculatePrePagesRequestList.size(); i2++) {
                                    EPGUsActivity.this.mEpgEevent.freeEvent(((Integer) EPGUsActivity.this.mCalculatePrePagesRequestList.get(i2)).intValue());
                                }
                                EPGUsActivity.this.mCalculatePrePagesRequestList.clear();
                            }
                            EPGUsActivity.this.mCalculatePrePagesAlreadyRequestList.clear();
                            EPGUsActivity.this.mPrePageStartTimeList.clear();
                            if (EPGUsActivity.this.mPrePagesStartTimes.size() % 4 != 0) {
                                int i3 = 0;
                                while (i3 < 4) {
                                    EPGUsActivity.this.mPrePagesStartTimes.add(0, 0L);
                                    if (EPGUsActivity.this.mPrePagesStartTimes.size() % 4 != 0) {
                                        i3++;
                                    }
                                }
                            }
                            int count = EPGUsActivity.this.mPrePagesStartTimes.size() / 4;
                            for (int i4 = 0; i4 < count; i4++) {
                                if (i4 == 0) {
                                    EPGUsActivity.this.mPrePageStartTimeList.add(0L);
                                } else {
                                    EPGUsActivity.this.mPrePageStartTimeList.add((Long) EPGUsActivity.this.mPrePagesStartTimes.get(i4 * 4));
                                }
                            }
                            if (EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                                EPGUsActivity.this.progressDialog.dismiss();
                            }
                            if (EPGUsActivity.this.mPrePageStartTimeList.size() <= 0 || ((Long) EPGUsActivity.this.mPrePageStartTimeList.get(EPGUsActivity.this.mPrePageStartTimeList.size() - 1)).longValue() < 0 || EPGUsActivity.this.dataGroup.size() <= 0) {
                                EPGUsActivity.this.arrowUp.setVisibility(4);
                                return;
                            } else {
                                EPGUsActivity.this.arrowUp.setVisibility(0);
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                default:
                    return;
            }
        }
    };
    private HandlerThread mHandlerThead;
    /* access modifiers changed from: private */
    public boolean mIsNextpage;
    /* access modifiers changed from: private */
    public boolean mIsPrepage;
    /* access modifiers changed from: private */
    public boolean mJudgeHasNextDay;
    /* access modifiers changed from: private */
    public boolean mJudgeHasNextPage;
    private Runnable mJudgeNextDayRequesListRunnable = new Runnable() {
        public void run() {
            List unused = EPGUsActivity.this.mJudgeNextDayRequestList = EPGUsActivity.this.usManager.getDataGroup(EPGUsActivity.this.mReGetDataRequsetTime, EPGUsActivity.this.mReGetDataGetCount);
            MtkLog.d(EPGUsActivity.TAG, "11111111mJudgeNextDayRequestList>>>" + EPGUsActivity.this.mJudgeNextDayRequestList.size());
            if (EPGUsActivity.this.mJudgeNextDayRequestList.size() == 0 && EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                EPGUsActivity.this.progressDialog.dismiss();
            }
        }
    };
    /* access modifiers changed from: private */
    public List<Integer> mJudgeNextDayRequestList;
    private Runnable mJudgeNextPageRequesListRunnable = new Runnable() {
        public void run() {
            List unused = EPGUsActivity.this.mJudgeNextpageRequestList = EPGUsActivity.this.usManager.getDataGroup(EPGUsActivity.this.mReGetDataRequsetTime, EPGUsActivity.this.mReGetDataGetCount);
            MtkLog.d(EPGUsActivity.TAG, "11111111mJudgeNextpageRequestList>>>" + EPGUsActivity.this.mJudgeNextpageRequestList.size());
            if (EPGUsActivity.this.mJudgeNextpageRequestList.size() == 0 && EPGUsActivity.this.progressDialog != null && EPGUsActivity.this.progressDialog.isShowing()) {
                EPGUsActivity.this.progressDialog.dismiss();
            }
        }
    };
    private long mJudgeNextPageStartTime;
    /* access modifiers changed from: private */
    public List<Integer> mJudgeNextpageRequestList;
    /* access modifiers changed from: private */
    public long mLastPageFirstTime;
    /* access modifiers changed from: private */
    public long mLastTime;
    /* access modifiers changed from: private */
    public EPGUsListAdapter mListAdapter;
    /* access modifiers changed from: private */
    public int mMaxDayNum;
    /* access modifiers changed from: private */
    public boolean mNeedClearNextOldData;
    /* access modifiers changed from: private */
    public boolean mNeedClearPreOldData;
    /* access modifiers changed from: private */
    public boolean mNeedFilterEvent;
    /* access modifiers changed from: private */
    public boolean mNoNeedRequestNextDayData;
    private TextView mPageInfoTv;
    /* access modifiers changed from: private */
    public List<Long> mPrePageStartTimeList;
    /* access modifiers changed from: private */
    public List<Long> mPrePagesStartTimes;
    /* access modifiers changed from: private */
    public long mPreStartTime;
    /* access modifiers changed from: private */
    public int mReGetDataGetCount;
    /* access modifiers changed from: private */
    public long mReGetDataRequsetTime;
    /* access modifiers changed from: private */
    public List<Integer> mRequestList;
    private Handler mThreadHandler;
    private int mTotalPage;
    private TextView nextPageTv;
    private TextView nextText;
    private TextView prePageTv;
    private TextView preText;
    /* access modifiers changed from: private */
    public TextView programDetail;
    /* access modifiers changed from: private */
    public TextView programRating;
    /* access modifiers changed from: private */
    public TextView programTime;
    /* access modifiers changed from: private */
    public EPGProgressDialog progressDialog;
    /* access modifiers changed from: private */
    public int reCount = 0;
    /* access modifiers changed from: private */
    public TextView rightChannelTextView;
    /* access modifiers changed from: private */
    public long startTime;
    /* access modifiers changed from: private */
    public TextView timeTextView;
    /* access modifiers changed from: private */
    public EPGUsManager usManager;

    static /* synthetic */ int access$2508(EPGUsActivity x0) {
        int i = x0.reCount;
        x0.reCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$2510(EPGUsActivity x0) {
        int i = x0.reCount;
        x0.reCount = i - 1;
        return i;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: private */
    public void judegeOnePageHasNextDay(List<ListItemData> dataGroup2) {
        if (dataGroup2 != null) {
            for (ListItemData tempData : dataGroup2) {
                if (tempData.getMillsStartTime() > 0) {
                    int dayNum = EPGUtil.getDayOffset(tempData.getMillsStartTime());
                    if (dayNum < 0) {
                        dayNum = 0;
                    }
                    if (this.mMaxDayNum < dayNum) {
                        this.mMaxDayNum = dayNum;
                    }
                }
            }
            refreshFoot();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()...");
        super.onCreate(savedInstanceState);
        ((DestroyApp) getApplication()).add(this);
        setContentView(R.layout.epg_us_main);
        initViews();
        initData();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d(TAG, "onStop()");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()...");
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1001);
        }
        ComponentStatusListener.getInstance().removeListener(this);
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        this.progressDialog = null;
        if (this.mRequestList != null) {
            for (int i = 0; i < this.mRequestList.size(); i++) {
                this.mEpgEevent.freeEvent(this.mRequestList.get(i).intValue());
            }
            this.mRequestList.clear();
        }
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_ATSC_EVENT_MSG, this.mHandler);
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, this.mHandler);
        if (this.mThreadHandler != null) {
            this.mThreadHandler.removeCallbacks(this.mChangeChannelPreRunnable);
            this.mThreadHandler.removeCallbacks(this.mChangeChannelNextRunnable);
            this.mThreadHandler.removeCallbacks(this.mGetRequestListRunnable);
            this.mThreadHandler.removeCallbacks(this.mJudgeNextPageRequesListRunnable);
            this.mThreadHandler.removeCallbacks(this.mJudgeNextDayRequesListRunnable);
            this.mThreadHandler.removeCallbacks(this.mCaculatePrePagesRequesListRunnable);
            this.mHandlerThead.quit();
            this.mChangeChannelPreRunnable = null;
            this.mChangeChannelNextRunnable = null;
            this.mGetRequestListRunnable = null;
            this.mJudgeNextPageRequesListRunnable = null;
            this.mJudgeNextDayRequesListRunnable = null;
            this.mCaculatePrePagesRequesListRunnable = null;
            this.mThreadHandler = null;
        }
        if (this.usManager != null) {
            if (this.listView != null) {
                this.listView.setAdapter((ListAdapter) null);
                MtkLog.d(TAG, "setAdapter null");
            }
            this.usManager.clearData();
            this.usManager = null;
        }
        ((DestroyApp) getApplication()).remove(this);
    }

    private void initViews() {
        this.timeTextView = (TextView) findViewById(R.id.epg_us_currenttime);
        this.listView = (EPGUsListView) findViewById(R.id.epg_us_program_listview);
        this.listView.setFocusable(true);
        this.listView.setVisibility(0);
        this.listView.setOnKeyListener(this);
        this.listView.setOnItemSelectedListener(this);
        this.leftChannelTextView = (TextView) findViewById(R.id.epg_us_channel_left);
        this.leftChannelTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EPGUsActivity.this.changeChannelWithThread(true);
            }
        });
        this.centerChannelTextView = (TextView) findViewById(R.id.epg_us_channel_center);
        this.rightChannelTextView = (TextView) findViewById(R.id.epg_us_channel_right);
        this.rightChannelTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EPGUsActivity.this.changeChannelWithThread(false);
            }
        });
        this.bottomParentLayout = (LinearLayout) findViewById(R.id.epg_bottom_layout);
        if (CommonIntegration.getInstance().isDisableColorKey()) {
            this.bottomParentLayout.setVisibility(4);
        }
        this.mPageInfoTv = (TextView) findViewById(R.id.epg_info_page_tv);
        this.preText = (TextView) findViewById(R.id.epg_bottom_prev_day_tv);
        this.nextText = (TextView) findViewById(R.id.epg_bottom_next_day_tv);
        this.prePageTv = (TextView) findViewById(R.id.epg_bottom_view_detail);
        this.nextPageTv = (TextView) findViewById(R.id.epg_bottom_view_filter);
        this.programTime = (TextView) findViewById(R.id.epg_us_program_time);
        this.lockIconView = findViewById(R.id.epg_info_lock_icon);
        this.programRating = (TextView) findViewById(R.id.epg_us_program_type);
        this.programRating.setVisibility(4);
        this.programDetail = (TextView) findViewById(R.id.epg_us_program_detail);
        this.programDetail.setLines(5);
        this.programDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
        this.arrowDown = (ImageView) findViewById(R.id.epg_us_arrow_down);
        this.arrowUp = (ImageView) findViewById(R.id.epg_us_arrow_up);
        this.mEPGPwdDialog = new EPGPwdDialog(this);
        this.mEPGPwdDialog.setAttachView(findViewById(R.id.epg_content_layout));
        this.mEPGPwdDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                MtkTvChannelInfoBase curChannel = CommonIntegration.getInstance().getCurChInfo();
                if (MtkTvPWDDialog.getInstance().PWDShow() != 0) {
                    EPGUsActivity.this.mListAdapter.notifyDataSetChanged();
                    EPGUsActivity.this.listView.setVisibility(0);
                    EPGUsActivity.this.listView.requestFocus();
                    if (EPGUsActivity.this.dataGroup.size() < 4 || EPGUsManager.requestComplete) {
                        EPGUsActivity.this.arrowDown.setVisibility(4);
                    } else {
                        EPGUsActivity.this.arrowDown.setVisibility(0);
                    }
                    EPGUsActivity.this.programTime.setVisibility(0);
                    EPGUsActivity.this.lockIconView.setVisibility(4);
                    ListItemData itemData = (ListItemData) EPGUsActivity.this.listView.getSelectedItem();
                    if (itemData == null || !itemData.isBlocked()) {
                        EPGUsActivity.this.programDetail.setVisibility(0);
                    } else {
                        EPGUsActivity.this.programDetail.setVisibility(4);
                    }
                    EPGUsActivity.this.programRating.setVisibility(0);
                    EPGUsActivity.this.initProgramDetailContent();
                    EPGUsActivity.this.refreshFoot();
                } else if (!CommonIntegration.getInstance().isCurrentSourceBlocked() && curChannel != null && !curChannel.isBlock()) {
                    ListItemData itemData2 = (ListItemData) EPGUsActivity.this.listView.getSelectedItem();
                    if (itemData2 == null || !itemData2.isBlocked()) {
                        EPGUsActivity.this.programDetail.setVisibility(0);
                    } else {
                        EPGUsActivity.this.programDetail.setVisibility(4);
                    }
                }
            }
        });
        this.leftChannelTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                EPGUsActivity.this.leftChannelTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                try {
                    int height = EPGUsActivity.this.leftChannelTextView.getHeight();
                    Drawable leftDrawable = EPGUsActivity.this.getResources().getDrawable(R.drawable.nav_icon_arrow_left);
                    Drawable rightDrawable = EPGUsActivity.this.getResources().getDrawable(R.drawable.nav_icon_arrow_right);
                    int minimumWidth = leftDrawable.getMinimumWidth();
                    int minimumHeight = leftDrawable.getMinimumHeight();
                    if (minimumHeight > 0) {
                        int width = (minimumWidth * height) / minimumHeight;
                        leftDrawable.setBounds(0, 0, width, height);
                        rightDrawable.setBounds(0, 0, width, height);
                        EPGUsActivity.this.leftChannelTextView.setCompoundDrawables((Drawable) null, (Drawable) null, leftDrawable, (Drawable) null);
                        EPGUsActivity.this.rightChannelTextView.setCompoundDrawables(rightDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initData() {
        MtkLog.d(TAG, "initData()");
        if (!CommonIntegration.getInstance().isContextInit()) {
            CommonIntegration.getInstance().setContext(getApplicationContext());
        }
        this.mHandlerThead = new HandlerThread(TAG);
        this.mHandlerThead.start();
        this.mThreadHandler = new Handler(this.mHandlerThead.getLooper());
        DataReader.getInstance(this).loadMonthAndWeekRes();
        this.mCanChangeChannel = true;
        this.mJudgeNextpageRequestList = new ArrayList();
        this.mJudgeNextDayRequestList = new ArrayList();
        this.mAlreadyRequestList = new ArrayList();
        this.mRequestList = new ArrayList();
        this.integration = CommonIntegration.getInstance();
        this.dataGroup.clear();
        this.usManager = EPGUsManager.getInstance(this);
        if (this.usManager.getDataGroup() != null) {
            this.usManager.getDataGroup().clear();
            if (this.mListAdapter != null) {
                this.mListAdapter.notifyDataSetChanged();
            }
        }
        this.mPrePageStartTimeList = new ArrayList();
        this.mEpgUsUpdate = new EpgUsUpdate();
        this.mLastTime = EPGUtil.getCurrentTime();
        initListView();
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_ATSC_EVENT_MSG, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_EAS_MSG, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, this.mHandler);
        ComponentStatusListener lister = ComponentStatusListener.getInstance();
        lister.addListener(10, this);
        lister.addListener(6, this);
        this.progressDialog = EPGUsManager.getInstance(this).loading(this, true);
        this.progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                MtkLog.d(EPGUsActivity.TAG, "progressDialog onKey>>>>" + keyCode + "  action>>" + event.getAction());
                if (event.getAction() == 0) {
                    MtkLog.d(EPGUsActivity.TAG, "event.getRepeatCount()>>>" + event.getRepeatCount());
                    if (keyCode != 4) {
                        if (keyCode == 82) {
                            return true;
                        }
                        if (keyCode != 172) {
                            switch (keyCode) {
                                case 21:
                                    EPGUsActivity.this.changeChannelWithThread(true);
                                    return true;
                                case 22:
                                    EPGUsActivity.this.changeChannelWithThread(false);
                                    return true;
                            }
                        }
                    }
                    if (event.getRepeatCount() <= 0) {
                        EPGUsActivity.this.finish();
                    }
                    return true;
                }
                return false;
            }
        });
        this.startTime = EPGUtil.getCurrentTime();
        this.dayTime = 0;
        EPGUsManager.requestComplete = false;
        reGetdata(0, this.dayTime);
    }

    /* access modifiers changed from: private */
    public void changeChannelWithThread(boolean isLeft) {
        MtkLog.d(TAG, "changeChannelWithThread >>>>" + this.mCanChangeChannel);
        if (this.mCanChangeChannel) {
            this.mHandler.removeMessages(4104);
            this.mCanChangeChannel = false;
            this.mHandler.sendEmptyMessageDelayed(4104, MessageType.delayForTKToMenu);
            if (this.mPrePageStartTimeList != null) {
                this.mPrePageStartTimeList.clear();
            }
            if (isLeft) {
                if (this.mThreadHandler != null && this.mChangeChannelPreRunnable != null) {
                    this.mThreadHandler.post(this.mChangeChannelPreRunnable);
                }
            } else if (this.mThreadHandler != null && this.mChangeChannelNextRunnable != null) {
                this.mThreadHandler.post(this.mChangeChannelNextRunnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendChangeSourceMessage() {
        this.mHandler.removeMessages(1004);
        Message changeMessage = this.mHandler.obtainMessage();
        changeMessage.what = 1004;
        this.mHandler.sendMessage(changeMessage);
        this.mCanChangeChannel = true;
    }

    /* access modifiers changed from: private */
    public void setPageNoVisivle() {
        this.programTime.setVisibility(4);
        this.programDetail.setVisibility(4);
        this.programRating.setVisibility(4);
        this.lockIconView.setVisibility(4);
        this.arrowDown.setVisibility(4);
        this.arrowUp.setVisibility(4);
        this.listView.setVisibility(4);
        refreshFoot();
    }

    public void reRefresh() {
        if (this.usManager != null) {
            initChannelView();
            this.usManager.getDataGroup().clear();
            if (this.mListAdapter != null) {
                this.mListAdapter.notifyDataSetChanged();
            }
            if (this.mJudgeNextDayRequestList != null) {
                for (int i = 0; i < this.mJudgeNextDayRequestList.size(); i++) {
                    this.mEpgEevent.freeEvent(this.mJudgeNextDayRequestList.get(i).intValue());
                }
                this.mJudgeNextDayRequestList.clear();
            }
            if (this.mJudgeNextpageRequestList != null) {
                for (int i2 = 0; i2 < this.mJudgeNextpageRequestList.size(); i2++) {
                    this.mEpgEevent.freeEvent(this.mJudgeNextpageRequestList.get(i2).intValue());
                }
                this.mJudgeNextpageRequestList.clear();
            }
            refreshFoot();
            this.mJudgeHasNextPage = false;
            this.mJudgeHasNextDay = false;
            this.mNoNeedRequestNextDayData = false;
            this.dayOffset = 0;
            this.mMaxDayNum = 0;
            EPGUsManager.requestComplete = false;
            this.startTime = EPGUtil.getCurrentTime();
            this.dayTime = 0;
            this.mCanCalcPrePages = false;
            this.mNeedClearPreOldData = false;
            this.mNeedClearNextOldData = false;
            this.mPrePageStartTimeList.clear();
            this.mNeedFilterEvent = false;
            reGetdata(0, this.dayTime);
        }
    }

    public void reGetdata(long start, long time) {
        if (this.mRequestList != null) {
            for (int i = 0; i < this.mRequestList.size(); i++) {
                this.mEpgEevent.freeEvent(this.mRequestList.get(i).intValue());
            }
            this.mRequestList.clear();
        }
        if (this.mCalculatePrePagesRequestList != null) {
            for (int i2 = 0; i2 < this.mCalculatePrePagesRequestList.size(); i2++) {
                this.mEpgEevent.freeEvent(this.mCalculatePrePagesRequestList.get(i2).intValue());
            }
            this.mCalculatePrePagesRequestList.clear();
        }
        if (this.mJudgeNextDayRequestList != null) {
            for (int i3 = 0; i3 < this.mJudgeNextDayRequestList.size(); i3++) {
                this.mEpgEevent.freeEvent(this.mJudgeNextDayRequestList.get(i3).intValue());
            }
            this.mJudgeNextDayRequestList.clear();
        }
        if (this.mJudgeNextpageRequestList != null) {
            for (int i4 = 0; i4 < this.mJudgeNextpageRequestList.size(); i4++) {
                this.mEpgEevent.freeEvent(this.mJudgeNextpageRequestList.get(i4).intValue());
            }
            this.mJudgeNextpageRequestList.clear();
        }
        if (this.progressDialog != null && !this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }
        setPageNoVisivle();
        this.reCount = 0;
        if (!this.usManager.isCurChATV()) {
            this.mHandler.removeMessages(1005);
            this.mHandler.sendEmptyMessageDelayed(1005, MessageType.delayMillis4);
        } else if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
            this.mHandler.removeMessages(1005);
            this.mHandler.sendEmptyMessage(1005);
        }
        this.mAlreadyRequestList.clear();
        this.mCanJudgeNextPage = true;
        this.mJudgeHasNextPage = false;
        this.mReGetDataRequsetTime = start + time;
        this.mReGetDataGetCount = 5;
        if (this.mThreadHandler != null && this.mGetRequestListRunnable != null) {
            this.mThreadHandler.post(this.mGetRequestListRunnable);
        }
    }

    private void initListView() {
        this.timeTextView.setText(this.usManager.getTimeToShow());
        this.mHandler.removeMessages(1001);
        this.mHandler.sendEmptyMessageDelayed(1001, 1000);
        initChannelView();
    }

    /* access modifiers changed from: private */
    public void initChannelView() {
        if (this.usManager != null) {
            String channelNumPre = this.usManager.getChannelNumPre();
            String channelNumNext = this.usManager.getChannelNumNext();
            if (TextUtils.isEmpty(channelNumPre)) {
                this.leftChannelTextView.setVisibility(4);
            } else {
                this.leftChannelTextView.setVisibility(0);
                this.leftChannelTextView.setText(channelNumPre);
            }
            if (TextUtils.isEmpty(channelNumNext)) {
                this.rightChannelTextView.setVisibility(4);
            } else {
                this.rightChannelTextView.setVisibility(0);
                this.rightChannelTextView.setText(channelNumNext);
            }
            TextView textView = this.centerChannelTextView;
            textView.setText(this.usManager.getChannelNumCur() + "  " + this.usManager.getChannelNameCur());
            MtkLog.e(TAG, "initChannelView CurNum Name:" + this.usManager.getChannelNumCur() + this.usManager.getChannelNameCur() + "\nPreNum:" + channelNumPre + "\nNextNum:" + channelNumNext);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: com.mediatek.wwtv.tvcenter.epg.us.ListItemData} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: com.mediatek.wwtv.tvcenter.epg.us.ListItemData} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchKeyEvent(android.view.KeyEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getKeyCode()
            java.lang.String r1 = "EPGUsActivity"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "dispatchKeyEvent: keyCode="
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.e(r1, r2)
            r1 = 23
            if (r0 == r1) goto L_0x0027
            r1 = 66
            if (r0 == r1) goto L_0x0027
            boolean r1 = super.dispatchKeyEvent(r8)
            return r1
        L_0x0027:
            int r1 = r8.getAction()
            if (r1 != 0) goto L_0x00d6
            com.mediatek.twoworlds.tv.MtkTvPWDDialog r1 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
            int r1 = r1.PWDShow()
            java.lang.String r2 = "EPGUsActivity"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "dispatchKeyEvent KeyMap.KEYCODE_DPAD_CENTER>>>"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            r2 = 4
            r3 = 0
            if (r1 != 0) goto L_0x00a6
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsChannelManager r4 = com.mediatek.wwtv.tvcenter.epg.us.EPGUsChannelManager.getInstance(r7)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = r4.getChannelCurrent()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r5 = r5.isCurrentSourceBlocked()
            if (r5 != 0) goto L_0x009b
            if (r4 == 0) goto L_0x006a
            boolean r5 = r4.isBlock()
            if (r5 == 0) goto L_0x006a
            goto L_0x009b
        L_0x006a:
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r5 = r7.listView
            java.lang.Object r5 = r5.getSelectedItem()
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r5 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r5
            if (r5 != 0) goto L_0x007d
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r6 = r7.listView
            java.lang.Object r3 = r6.getItemAtPosition(r3)
            r5 = r3
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r5 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r5
        L_0x007d:
            boolean r3 = r5.isBlocked()
            if (r3 == 0) goto L_0x00a5
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r3 = r7.mEPGPwdDialog
            r3.show()
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r3 = r7.mEPGPwdDialog
            r3.sendAutoDismissMessage()
            android.widget.TextView r3 = r7.programDetail
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x00a5
            android.widget.TextView r3 = r7.programDetail
            r3.setVisibility(r2)
            goto L_0x00a5
        L_0x009b:
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r2 = r7.mEPGPwdDialog
            r2.show()
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r2 = r7.mEPGPwdDialog
            r2.sendAutoDismissMessage()
        L_0x00a5:
            goto L_0x00d6
        L_0x00a6:
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r4 = r7.listView
            java.lang.Object r4 = r4.getSelectedItem()
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r4 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r4
            if (r4 != 0) goto L_0x00b9
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r5 = r7.listView
            java.lang.Object r3 = r5.getItemAtPosition(r3)
            r4 = r3
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r4 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r4
        L_0x00b9:
            boolean r3 = r4.isBlocked()
            if (r3 == 0) goto L_0x00d6
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r3 = r7.mEPGPwdDialog
            r3.show()
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r3 = r7.mEPGPwdDialog
            r3.sendAutoDismissMessage()
            android.widget.TextView r3 = r7.programDetail
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x00d6
            android.widget.TextView r3 = r7.programDetail
            r3.setVisibility(r2)
        L_0x00d6:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown>>>>" + keyCode + "  " + event.getAction());
        StringBuilder sb = new StringBuilder();
        sb.append("event.getRepeatCount()>>>");
        sb.append(event.getRepeatCount());
        MtkLog.d(TAG, sb.toString());
        if (keyCode != 4) {
            if (keyCode == 23 || keyCode == 33 || keyCode == 66) {
                int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
                MtkLog.d(TAG, "KeyMap.KEYCODE_DPAD_CENTER>>>" + showFlag);
                if (showFlag == 0) {
                    this.lockIconView.setVisibility(8);
                    this.mEPGPwdDialog.show();
                    this.mEPGPwdDialog.sendAutoDismissMessage();
                }
                return true;
            } else if (keyCode == 82) {
                return true;
            } else {
                if (keyCode != 172) {
                    return super.onKeyDown(keyCode, event);
                }
            }
        }
        if (event.getRepeatCount() <= 0) {
            finish();
        }
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyUp>>>>>" + keyCode + "  " + event.getAction());
        switch (keyCode) {
            case 21:
                changeChannelWithThread(true);
                return true;
            case 22:
                changeChannelWithThread(false);
                return true;
            case 24:
            case 25:
                return true;
            case 30:
            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                getNextPageDetailInfo();
                return true;
            case 53:
            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                getPrePageDetailInfo();
                return true;
            case 93:
            case 130:
                if (CommonIntegration.getInstance().isCurrentSourceDTV()) {
                    calledByScheduleList();
                } else {
                    Toast.makeText(getApplicationContext(), "Source not available,please change to DTV source", 0).show();
                }
                return true;
            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                boolean canGetNextData = getPreDayPrograms();
                return true;
            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                boolean nextDayPrograms = getNextDayPrograms();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private boolean getPreDayPrograms() {
        if (this.dayOffset == 0 || this.usManager.isCurChATV()) {
            return false;
        }
        if (System.currentTimeMillis() - this.mCurrentKeyPageTime < 500 && System.currentTimeMillis() - this.mCurrentKeyPageTime > 0) {
            return false;
        }
        if (this.mPrePageStartTimeList != null) {
            this.mPrePageStartTimeList.clear();
        }
        this.mCurrentKeyPageTime = System.currentTimeMillis();
        this.usManager.clearDataGroup();
        if (this.mListAdapter != null) {
            this.mListAdapter.notifyDataSetChanged();
        }
        this.dayOffset--;
        this.mJudgeHasNextDay = false;
        this.mNoNeedRequestNextDayData = false;
        this.mMaxDayNum = this.dayOffset;
        EPGUsManager.requestComplete = false;
        if (this.dayOffset == 0) {
            this.mNeedFilterEvent = false;
            this.startTime = EPGUtil.getCurrentTime();
            this.dayTime = 0;
            reGetdata(0, this.dayTime);
        } else {
            this.mNeedFilterEvent = true;
            this.mCanCalcPrePages = true;
            this.startTime = EPGUtil.getCurrentDayStartTime();
            this.dayTime = ((long) (this.dayOffset * 24 * 60)) * 60;
            reGetdata(EPGUtil.getCurrentDayStartTime(), this.dayTime);
        }
        return true;
    }

    private boolean getNextDayPrograms() {
        if (this.dayOffset == this.mMaxDayNum || this.usManager.isCurChATV()) {
            return false;
        }
        if (System.currentTimeMillis() - this.mCurrentKeyPageTime < 500 && System.currentTimeMillis() - this.mCurrentKeyPageTime > 0) {
            return false;
        }
        if (this.mPrePageStartTimeList != null) {
            this.mPrePageStartTimeList.clear();
        }
        this.mCurrentKeyPageTime = System.currentTimeMillis();
        this.usManager.clearDataGroup();
        if (this.mListAdapter != null) {
            this.mListAdapter.notifyDataSetChanged();
        }
        this.dayOffset++;
        this.mJudgeHasNextDay = false;
        this.mNoNeedRequestNextDayData = false;
        this.mMaxDayNum = this.dayOffset;
        EPGUsManager.requestComplete = false;
        if (this.dayOffset == 0) {
            this.mNeedFilterEvent = false;
            this.startTime = EPGUtil.getCurrentTime();
            this.dayTime = 0;
            reGetdata(0, this.dayTime);
        } else {
            this.mNeedFilterEvent = true;
            this.mCanCalcPrePages = true;
            this.startTime = EPGUtil.getCurrentDayStartTime();
            this.dayTime = ((long) (this.dayOffset * 24 * 60)) * 60;
            reGetdata(EPGUtil.getCurrentDayStartTime(), this.dayTime);
        }
        return true;
    }

    private boolean getPrePageDetailInfo() {
        if (this.mTotalPage <= 1 || this.mCurrentPage == 1) {
            return false;
        }
        this.mCurrentPage--;
        if (this.mCurrentPage == 1) {
            this.prePageTv.setText("");
            this.nextPageTv.setText(getString(R.string.epg_bottom_next_page));
        } else {
            this.prePageTv.setText(getString(R.string.epg_bottom_prev_page));
            this.nextPageTv.setText(getString(R.string.epg_bottom_next_page));
        }
        this.programDetail.scrollTo(0, (this.mCurrentPage - 1) * 5 * this.programDetail.getLineHeight());
        this.mPageInfoTv.setText(this.mCurrentPage + "/" + this.mTotalPage);
        return true;
    }

    private boolean getNextPageDetailInfo() {
        if (this.mTotalPage <= 1 || this.mCurrentPage == this.mTotalPage) {
            return false;
        }
        this.mCurrentPage++;
        if (this.mCurrentPage == this.mTotalPage) {
            this.prePageTv.setText(getString(R.string.epg_bottom_prev_page));
            this.nextPageTv.setText("");
        } else {
            this.prePageTv.setText(getString(R.string.epg_bottom_prev_page));
            this.nextPageTv.setText(getString(R.string.epg_bottom_next_page));
        }
        this.programDetail.scrollTo(0, (this.mCurrentPage - 1) * 5 * this.programDetail.getLineHeight());
        this.mPageInfoTv.setText(this.mCurrentPage + "/" + this.mTotalPage);
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: com.mediatek.wwtv.tvcenter.epg.us.ListItemData} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKey(android.view.View r7, int r8, android.view.KeyEvent r9) {
        /*
            r6 = this;
            java.lang.String r0 = "EPGUsActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onKey>>>>"
            r1.append(r2)
            r1.append(r8)
            java.lang.String r2 = "   "
            r1.append(r2)
            int r2 = r7.getId()
            r1.append(r2)
            java.lang.String r2 = "  "
            r1.append(r2)
            int r2 = r9.getAction()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            int r0 = r9.getAction()
            r1 = 0
            r2 = 1
            if (r0 != r2) goto L_0x009a
            r0 = 23
            if (r8 == r0) goto L_0x0043
            r0 = 33
            if (r8 == r0) goto L_0x0043
            r0 = 66
            if (r8 == r0) goto L_0x0043
            goto L_0x009a
        L_0x0043:
            com.mediatek.twoworlds.tv.MtkTvPWDDialog r0 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
            int r0 = r0.PWDShow()
            java.lang.String r3 = "EPGUsActivity"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "KeyMap.KEYCODE_DPAD_CENTER>>>"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r3 = r6.listView
            java.lang.Object r3 = r3.getSelectedItem()
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r3 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r3
            if (r3 != 0) goto L_0x0074
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r4 = r6.listView
            java.lang.Object r1 = r4.getItemAtPosition(r1)
            r3 = r1
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r3 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r3
        L_0x0074:
            boolean r1 = r3.isBlocked()
            if (r1 == 0) goto L_0x0099
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r1 = r6.mEPGPwdDialog
            r1.show()
            android.view.View r1 = r6.lockIconView
            r4 = 8
            r1.setVisibility(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r1 = r6.mEPGPwdDialog
            r1.sendAutoDismissMessage()
            android.widget.TextView r1 = r6.programDetail
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0099
            android.widget.TextView r1 = r6.programDetail
            r4 = 4
            r1.setVisibility(r4)
        L_0x0099:
            return r2
        L_0x009a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity.onKey(android.view.View, int, android.view.KeyEvent):boolean");
    }

    class EpgUsUpdate implements EPGUsListView.UpDateListView {
        EpgUsUpdate() {
        }

        public void update(boolean isNextPage) {
            if (isNextPage) {
                if (EPGUsActivity.this.dataGroup.size() > 0 && !EPGUsManager.requestComplete && ((ListItemData) EPGUsActivity.this.dataGroup.get(EPGUsActivity.this.dataGroup.size() - 1)).getMillsStartTime() > 0 && ((ListItemData) EPGUsActivity.this.dataGroup.get(EPGUsActivity.this.dataGroup.size() - 1)).getMillsDurationTime() > 0) {
                    boolean unused = EPGUsActivity.this.mNeedClearNextOldData = true;
                    long unused2 = EPGUsActivity.this.mLastPageFirstTime = ((ListItemData) EPGUsActivity.this.dataGroup.get(0)).getMillsStartTime();
                    EPGUsActivity.this.reGetdata(((ListItemData) EPGUsActivity.this.dataGroup.get(EPGUsActivity.this.dataGroup.size() - 1)).getMillsStartTime(), ((ListItemData) EPGUsActivity.this.dataGroup.get(EPGUsActivity.this.dataGroup.size() - 1)).getMillsDurationTime());
                }
            } else if (EPGUsActivity.this.dataGroup.size() > 0 && ((ListItemData) EPGUsActivity.this.dataGroup.get(0)).getMillsStartTime() > 0 && ((ListItemData) EPGUsActivity.this.dataGroup.get(0)).getMillsDurationTime() > 0 && EPGUsActivity.this.mPrePageStartTimeList.size() > 0) {
                boolean unused3 = EPGUsActivity.this.mNeedClearPreOldData = true;
                EPGUsActivity.this.reGetdata(((Long) EPGUsActivity.this.mPrePageStartTimeList.get(EPGUsActivity.this.mPrePageStartTimeList.size() - 1)).longValue(), 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setListViewAdapter() {
        if (this.usManager != null) {
            this.dataGroup = this.usManager.getDataGroup();
            MtkLog.e(TAG, ":dataGroup:" + this.dataGroup.size());
            int preDataGroupSize = this.dataGroup.size();
            if (this.dataGroup.size() > 1 && this.dataGroup.get(0).getMillsStartTime() == 0) {
                this.dataGroup.remove(0);
            }
            for (int i = 0; i < this.dataGroup.size(); i++) {
                ListItemData tempData = this.dataGroup.get(i);
                if (tempData.getProgramStartTime() == null || tempData.getProgramStartTime().equals("")) {
                    tempData.setProgramStartTime(EPGUtil.formatStartTime(tempData.getMillsStartTime(), this));
                    tempData.setProgramTime(EPGUtil.getProTime(tempData.getMillsStartTime(), tempData.getMillsDurationTime(), this));
                }
            }
            if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
                MtkTvChannelInfoBase curChannel = EPGUsChannelManager.getInstance(this).getChannelCurrent();
                if (CommonIntegration.getInstance().isCurrentSourceBlocked() || (curChannel != null && curChannel.isBlock())) {
                    this.listView.setVisibility(4);
                    this.lockIconView.setVisibility(0);
                } else {
                    this.listView.setVisibility(0);
                    this.lockIconView.setVisibility(4);
                }
            } else {
                this.listView.setVisibility(0);
                this.lockIconView.setVisibility(4);
            }
            this.listView.initData(this.dataGroup, 4, this.mEpgUsUpdate);
            if (this.mListAdapter == null || this.integration.is3rdTVSource()) {
                this.mListAdapter = new EPGUsListAdapter(getApplicationContext(), this.dataGroup);
                this.listView.setAdapter(this.mListAdapter);
                MtkLog.d(TAG, "setAdapter mListAdapter");
            } else {
                if (this.mIsNextpage) {
                    this.listView.setSelection(0);
                    this.mIsNextpage = false;
                }
                if (this.mIsPrepage) {
                    this.listView.setSelection(this.mListAdapter.getCount() - 1);
                    if (this.mListAdapter.getCount() == 4) {
                        this.mIsPrepage = false;
                    }
                }
                if (preDataGroupSize != this.dataGroup.size()) {
                    this.mListAdapter.notifyDataSetChanged();
                }
            }
            if (this.dataGroup.size() < 4) {
                EPGUsManager.requestComplete = true;
            }
            if (this.dataGroup.size() < 4 || this.listView.getVisibility() != 0 || EPGUsManager.requestComplete) {
                this.arrowDown.setVisibility(4);
            } else {
                this.arrowDown.setVisibility(0);
            }
            if (this.mPrePageStartTimeList.size() <= 0 || this.mPrePageStartTimeList.get(this.mPrePageStartTimeList.size() - 1).longValue() < 0 || this.dataGroup.size() <= 0) {
                this.arrowUp.setVisibility(4);
            } else {
                this.arrowUp.setVisibility(0);
            }
            if (TextToSpeechUtil.isTTSEnabled(this)) {
                MtkLog.d(TAG, "TTS enable");
            } else {
                MtkLog.d(TAG, "TTS disEnable");
                this.listView.setFocusable(true);
                this.listView.requestFocus();
                this.listView.requestFocusFromTouch();
                this.listView.requestFocus(130);
            }
            refreshBottomData();
            MtkLog.e(TAG, ":mJudgeHasNextPagedataGroup:" + this.dataGroup.size() + "  " + this.mJudgeHasNextPage + "  " + this.mCanJudgeNextPage);
            if (this.dataGroup.size() == 4 && !this.mJudgeHasNextPage && this.mCanJudgeNextPage) {
                this.mCanJudgeNextPage = false;
                this.mJudgeHasNextPage = true;
                if (this.progressDialog != null && !this.progressDialog.isShowing()) {
                    this.progressDialog.show();
                }
                this.mHandler.removeMessages(1005);
                this.mHandler.sendEmptyMessageDelayed(1005, MessageType.delayMillis4);
                this.mReGetDataRequsetTime = this.dataGroup.get(this.dataGroup.size() - 1).getMillsStartTime() + this.dataGroup.get(this.dataGroup.size() - 1).getMillsDurationTime();
                this.mReGetDataGetCount = 1;
                if (this.mThreadHandler != null && this.mJudgeNextPageRequesListRunnable != null) {
                    this.mThreadHandler.post(this.mJudgeNextPageRequesListRunnable);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void judgeHasNextDayData() {
        MtkLog.e(TAG, ":judgeHasNextDayData:" + this.dataGroup.size() + "  " + this.mJudgeHasNextDay);
        this.mJudgeHasNextDay = true;
        if (this.progressDialog != null && !this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }
        this.mHandler.removeMessages(1005);
        this.mHandler.sendEmptyMessageDelayed(1005, MessageType.delayMillis4);
        this.mJudgeNextPageStartTime = EPGUtil.getCurrentDayStartTime() + ((long) ((this.dayOffset + 1) * 24 * 60 * 60));
        this.mReGetDataRequsetTime = this.mJudgeNextPageStartTime;
        this.mReGetDataGetCount = 1;
        if (this.mThreadHandler != null && this.mJudgeNextDayRequesListRunnable != null) {
            this.mThreadHandler.post(this.mJudgeNextDayRequesListRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void calculatPrePages(long startTime2, int count, boolean withStart) {
        StringBuilder sb = new StringBuilder();
        sb.append(":calculatPrePages:");
        sb.append(this.dataGroup.size());
        sb.append("  ");
        sb.append(this.mCanCalcPrePages);
        sb.append("   ");
        sb.append(this.usManager == null ? null : Integer.valueOf(this.usManager.groupSize()));
        MtkLog.e(TAG, sb.toString());
        if (this.progressDialog != null && !this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }
        if (this.mCalculatePrePagesRequestList != null) {
            for (int i = 0; i < this.mCalculatePrePagesRequestList.size(); i++) {
                this.mEpgEevent.freeEvent(this.mCalculatePrePagesRequestList.get(i).intValue());
            }
            this.mCalculatePrePagesRequestList.clear();
        }
        if (withStart) {
            this.mPreStartTime = 0;
            if (this.mPrePagesStartTimes == null) {
                this.mPrePagesStartTimes = new ArrayList();
            } else {
                this.mPrePagesStartTimes.clear();
            }
        }
        if (this.mCalculatePrePagesAlreadyRequestList == null) {
            this.mCalculatePrePagesAlreadyRequestList = new ArrayList();
        } else {
            this.mCalculatePrePagesAlreadyRequestList.clear();
        }
        this.mReGetDataRequsetTime = startTime2;
        this.mReGetDataGetCount = count;
        if (this.mThreadHandler != null && this.mCaculatePrePagesRequesListRunnable != null) {
            this.mThreadHandler.post(this.mCaculatePrePagesRequesListRunnable);
        }
    }

    public void setProgramBlock(boolean isBlock) {
        for (int i = 0; i < this.dataGroup.size(); i++) {
            ListItemData tempData = this.dataGroup.get(i);
            if (tempData != null) {
                tempData.setBlocked(isBlock);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initProgramDetailContent() {
        int i;
        int line = this.programDetail.getLineCount();
        MtkLog.d(TAG, "--- initProgramDetailContent()---- Lines: " + line);
        if (line <= 0 || this.programDetail.getVisibility() != 0) {
            this.mPageInfoTv.setText("");
            this.prePageTv.setText("");
            this.nextPageTv.setText("");
            return;
        }
        if (line % 5 == 0) {
            i = line / 5;
        } else {
            i = (line / 5) + 1;
        }
        this.mTotalPage = i;
        this.mCurrentPage = 1;
        this.programDetail.scrollTo(0, (this.mCurrentPage - 1) * 5 * this.programDetail.getLineHeight());
        if (this.mTotalPage > 1) {
            TextView textView = this.mPageInfoTv;
            textView.setText(this.mCurrentPage + "/" + this.mTotalPage);
            this.prePageTv.setText("");
            this.nextPageTv.setText(getResources().getString(R.string.epg_bottom_next_page));
            return;
        }
        this.mPageInfoTv.setText("");
        this.prePageTv.setText("");
        this.nextPageTv.setText("");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: com.mediatek.wwtv.tvcenter.epg.us.ListItemData} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void refreshBottomData() {
        /*
            r7 = this;
            java.lang.String r0 = "EPGUsActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "listView.getVisibility()>>>"
            r1.append(r2)
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r2 = r7.listView
            int r2 = r2.getVisibility()
            r1.append(r2)
            java.lang.String r2 = "   "
            r1.append(r2)
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r2 = r7.listView
            boolean r2 = r2.hasFocus()
            r1.append(r2)
            java.lang.String r2 = " "
            r1.append(r2)
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r2 = r7.listView
            int r2 = r2.getSelectedItemPosition()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r0 = r7.listView
            int r0 = r0.getSelectedItemPosition()
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListAdapter r1 = r7.mListAdapter
            java.util.List r1 = r1.getDataList()
            int r1 = r1.size()
            r2 = 0
            if (r0 <= r1) goto L_0x0050
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r0 = r7.listView
            r0.setSelection(r2)
        L_0x0050:
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r0 = r7.listView
            java.lang.Object r0 = r0.getSelectedItem()
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r0 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r0
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r1 = r7.listView
            int r1 = r1.getSelectedItemPosition()
            r3 = -1
            if (r1 == r3) goto L_0x0073
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r1 = r7.listView
            int r1 = r1.getSelectedItemPosition()
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListAdapter r3 = r7.mListAdapter
            java.util.List r3 = r3.getDataList()
            int r3 = r3.size()
            if (r1 <= r3) goto L_0x007c
        L_0x0073:
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsListView r1 = r7.listView
            java.lang.Object r1 = r1.getItemAtPosition(r2)
            r0 = r1
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r0 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r0
        L_0x007c:
            if (r0 != 0) goto L_0x007f
            return
        L_0x007f:
            java.lang.String r1 = "EPGUsActivity"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "onItemSelected:"
            r3.append(r4)
            r3.append(r0)
            java.lang.String r4 = "   "
            r3.append(r4)
            java.lang.String r4 = r0.getItemProgramName()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            long r3 = r0.getMillsStartTime()
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x00ce
            long r3 = r0.getMillsDurationTime()
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x00b4
            goto L_0x00ce
        L_0x00b4:
            android.widget.TextView r1 = r7.programTime
            java.lang.String r3 = r0.getProgramTime()
            r1.setText(r3)
            long r3 = r0.getMillsStartTime()
            int r1 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getDayOffset(r3)
            r7.dayOffset = r1
            int r1 = r7.dayOffset
            if (r1 >= 0) goto L_0x00d5
            r7.dayOffset = r2
            goto L_0x00d5
        L_0x00ce:
            android.widget.TextView r1 = r7.programTime
            java.lang.String r3 = ""
            r1.setText(r3)
        L_0x00d5:
            android.widget.TextView r1 = r7.programDetail
            java.lang.String r3 = r0.getItemProgramDetail()
            r1.setText(r3)
            android.widget.TextView r1 = r7.programRating
            java.lang.String r3 = r0.getItemProgramType()
            r1.setText(r3)
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsManager r1 = r7.usManager
            java.util.List r1 = r1.getDataGroup()
            int r1 = r1.size()
            r3 = 1
            r4 = 4
            if (r1 != r3) goto L_0x0133
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsManager r1 = r7.usManager
            java.util.List r1 = r1.getDataGroup()
            java.lang.Object r1 = r1.get(r2)
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r1 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r1
            boolean r1 = r1.isValid()
            if (r1 != 0) goto L_0x0133
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.is3rdTVSource()
            if (r1 != 0) goto L_0x0133
            android.widget.TextView r1 = r7.programRating
            r3 = 2131692037(0x7f0f0a05, float:1.9013163E38)
            java.lang.String r3 = r7.getString(r3)
            r1.setText(r3)
            android.widget.TextView r1 = r7.programDetail
            r3 = 2131692034(0x7f0f0a02, float:1.9013157E38)
            java.lang.String r3 = r7.getString(r3)
            r1.setText(r3)
            android.widget.TextView r1 = r7.programTime
            r1.setVisibility(r4)
            android.widget.ImageView r1 = r7.arrowDown
            r1.setVisibility(r4)
        L_0x0133:
            com.mediatek.twoworlds.tv.MtkTvPWDDialog r1 = com.mediatek.twoworlds.tv.MtkTvPWDDialog.getInstance()
            int r1 = r1.PWDShow()
            if (r1 != 0) goto L_0x01af
            com.mediatek.wwtv.tvcenter.epg.us.EPGUsChannelManager r3 = com.mediatek.wwtv.tvcenter.epg.us.EPGUsChannelManager.getInstance(r7)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r3 = r3.getChannelCurrent()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r5 = r5.isCurrentSourceBlocked()
            if (r5 != 0) goto L_0x019a
            if (r3 == 0) goto L_0x0158
            boolean r5 = r3.isBlock()
            if (r5 == 0) goto L_0x0158
            goto L_0x019a
        L_0x0158:
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r5 = r7.mEPGPwdDialog
            if (r5 == 0) goto L_0x0179
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r5 = r7.mEPGPwdDialog
            boolean r5 = r5.isShowing()
            if (r5 == 0) goto L_0x0179
            android.widget.TextView r5 = r7.programTime
            r5.setVisibility(r2)
            android.widget.TextView r5 = r7.programDetail
            r5.setVisibility(r4)
            android.widget.TextView r5 = r7.programRating
            r5.setVisibility(r2)
            android.view.View r2 = r7.lockIconView
            r2.setVisibility(r4)
            goto L_0x01ae
        L_0x0179:
            boolean r5 = r0.isBlocked()
            if (r5 == 0) goto L_0x0185
            android.widget.TextView r5 = r7.programDetail
            r5.setVisibility(r4)
            goto L_0x018a
        L_0x0185:
            android.widget.TextView r5 = r7.programDetail
            r5.setVisibility(r2)
        L_0x018a:
            android.widget.TextView r5 = r7.programTime
            r5.setVisibility(r2)
            android.widget.TextView r5 = r7.programRating
            r5.setVisibility(r2)
            android.view.View r2 = r7.lockIconView
            r2.setVisibility(r4)
            goto L_0x01ae
        L_0x019a:
            android.widget.TextView r5 = r7.programTime
            r5.setVisibility(r4)
            android.widget.TextView r5 = r7.programDetail
            r5.setVisibility(r4)
            android.widget.TextView r5 = r7.programRating
            r5.setVisibility(r4)
            android.view.View r5 = r7.lockIconView
            r5.setVisibility(r2)
        L_0x01ae:
            goto L_0x01cf
        L_0x01af:
            boolean r3 = r0.isBlocked()
            if (r3 == 0) goto L_0x01bb
            android.widget.TextView r3 = r7.programDetail
            r3.setVisibility(r4)
            goto L_0x01c0
        L_0x01bb:
            android.widget.TextView r3 = r7.programDetail
            r3.setVisibility(r2)
        L_0x01c0:
            android.widget.TextView r3 = r7.programTime
            r3.setVisibility(r2)
            android.widget.TextView r3 = r7.programRating
            r3.setVisibility(r2)
            android.view.View r2 = r7.lockIconView
            r2.setVisibility(r4)
        L_0x01cf:
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r2 = r7.mEPGPwdDialog
            if (r2 == 0) goto L_0x01e0
            com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog r2 = r7.mEPGPwdDialog
            boolean r2 = r2.isShowing()
            if (r2 == 0) goto L_0x01e0
            android.widget.TextView r2 = r7.programDetail
            r2.setVisibility(r4)
        L_0x01e0:
            r7.initProgramDetailContent()
            r7.refreshFoot()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity.refreshBottomData():void");
    }

    public void calledByScheduleList() {
        MtkLog.d(TAG, "usManager.getDataGroup().size=" + this.usManager.getDataGroup().size());
        MtkLog.d(TAG, "usManager.getDataGroup().get(0).getItemProgramName()=" + this.usManager.getDataGroup().get(0).getItemProgramName());
        if (this.usManager.getDataGroup() == null || ((this.usManager.getDataGroup().size() != 1 || !this.usManager.getDataGroup().get(0).getItemProgramName().equals(getString(R.string.nav_epg_no_program_title))) && this.usManager.getDataGroup().size() >= 1)) {
            MtkTvBookingBase item = new MtkTvBookingBase();
            if (this.listView.getSelectedItem() != null) {
                ListItemData itemData = (ListItemData) this.listView.getSelectedItem();
                MtkTvTimeFormatBase from = new MtkTvTimeFormatBase();
                MtkTvTimeFormatBase to = new MtkTvTimeFormatBase();
                from.setByUtc((itemData.getMillsStartTime() * 1000) / 1000);
                MtkTvTimeBase time = new MtkTvTimeBase();
                time.convertTime(5, from, to);
                long startTime2 = to.toSeconds() * 1000;
                StringBuilder sb = new StringBuilder();
                sb.append("startTime=");
                sb.append(startTime2);
                sb.append(" str = ");
                ListItemData itemData2 = itemData;
                sb.append(Util.timeToTimeStringEx(startTime2 * 1000, 0));
                MtkLog.d(TAG, sb.toString());
                if (startTime2 != -1) {
                    item.setRecordStartTime(startTime2 / 1000);
                }
                ListItemData itemData3 = itemData2;
                from.setByUtc(Long.valueOf((itemData3.getMillsStartTime() + itemData3.getMillsDurationTime()) * 1000).longValue() / 1000);
                time.convertTime(5, from, to);
                Long endTime = Long.valueOf(to.toSeconds() * 1000);
                MtkLog.d(TAG, "endTime=" + endTime);
                if (endTime.longValue() != -1) {
                    item.setRecordDuration((endTime.longValue() / 1000) - (startTime2 / 1000));
                }
                item.setTunerType(CommonIntegration.getInstance().getTunerMode());
            }
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
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        refreshBottomData();
        MtkLog.e(TAG, "onItemSelected:" + position + "   " + EPGUsManager.requestComplete + "  " + this.mJudgeHasNextDay + this.mNoNeedRequestNextDayData + this.dayOffset + "  " + this.mMaxDayNum);
        if (this.dataGroup.size() == 4 && !EPGUsManager.requestComplete && !this.mJudgeHasNextDay && !this.mNoNeedRequestNextDayData && this.dayOffset == this.mMaxDayNum) {
            judgeHasNextDayData();
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
        MtkLog.e(TAG, "onNothingSelected:");
    }

    public void refreshFoot() {
        MtkLog.e(TAG, "dayOff:" + this.dayOffset + "  " + this.mMaxDayNum);
        if (this.listView.getVisibility() != 0) {
            this.preText.setText("");
            this.nextText.setText("");
        } else if (this.usManager.isCurChATV()) {
            this.preText.setText("");
            this.nextText.setText("");
        } else if (this.dayOffset == 0) {
            this.preText.setText("");
            if (this.mMaxDayNum == this.dayOffset) {
                this.nextText.setText("");
            } else {
                this.nextText.setText(R.string.epg_bottom_next_day);
            }
        } else if (this.dayOffset == -1) {
            this.preText.setText("");
            this.nextText.setText(R.string.epg_bottom_next_day);
        } else if (this.dayOffset == this.mMaxDayNum) {
            this.preText.setText(R.string.epg_bottom_prev_day);
            this.nextText.setText("");
        } else {
            this.preText.setText(R.string.epg_bottom_prev_day);
            this.nextText.setText(R.string.epg_bottom_next_day);
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "US EPG updateComponentStatus>>>" + statusID + ">>" + value + ">>>" + this.usManager);
        if (statusID == 10) {
            Log.d(TAG, "nextChannel()...");
            this.mHandler.removeMessages(4104);
            if (value == 0 && this.usManager != null) {
                this.mHandler.removeMessages(262);
                this.usManager.initChannels();
                this.mHandler.removeMessages(1004);
                this.mHandler.sendEmptyMessageDelayed(1004, 100);
                MtkLog.d(TAG, "US EPG updateComponentStatus:send message_refreshData");
                if (this.integration.is3rdTVSource()) {
                    MtkLog.d(TAG, "US EPG Message_Refresh_ListView");
                    this.mHandler.sendEmptyMessageDelayed(1002, 100);
                }
            }
            this.mCanChangeChannel = true;
        }
    }
}
