package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.common.MtkTvIntentBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGPwdDialog;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.epg.IPageCallback;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PageImp;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import java.util.ArrayList;
import java.util.List;

public class EPGEu2ndActivity extends BaseActivity implements DialogInterface.OnDismissListener, ComponentStatusListener.ICStatusListener, EPGEuIView {
    private static final int PER_PAGE_LINE = 4;
    private static final String TAG = "EPGEu2ndActivity";
    /* access modifiers changed from: private */
    public long curTime = 0;
    /* access modifiers changed from: private */
    public int dayNum = 0;
    /* access modifiers changed from: private */
    public long lastHourTime = 0;
    /* access modifiers changed from: private */
    public long lastTime = 0;
    private final Bundle mBundle = new Bundle();
    /* access modifiers changed from: private */
    public EPGEuChannelAdapter mChannelAdapter;
    /* access modifiers changed from: private */
    public TextView mCurrentDateTv;
    private int mCurrentPage = 1;
    /* access modifiers changed from: private */
    public EPGProgramInfo mCurrentSelectedProgramInfo;
    /* access modifiers changed from: private */
    public EPGEuIAction mDataAction;
    private TextView mDataRetrievingShow;
    private DigitTurnCHView mDigitTurnCHView;
    /* access modifiers changed from: private */
    public EPGEuHelper mEPGEuHelper = new EPGEuHelper();
    private EPGPwdDialog mEPGPwdDialog;
    /* access modifiers changed from: private */
    public EPGEuEventAdapter mEventAdapter;
    private View mFreeViewHDLogo;
    private Runnable mGetCurrentTimeRunnable = new Runnable() {
        public void run() {
            long unused = EPGEu2ndActivity.this.curTime = EPGUtil.getCurrentTime(EPGEu2ndActivity.this.mDataAction.is3rdTVSource());
            if (EPGEu2ndActivity.this.curTime - EPGEu2ndActivity.this.lastTime < -600 || EPGEu2ndActivity.this.curTime - EPGEu2ndActivity.this.lastHourTime >= 3600) {
                int unused2 = EPGEu2ndActivity.this.dayNum = 0;
                long unused3 = EPGEu2ndActivity.this.lastHourTime = EPGUtil.getCurrentDayHourMinute(EPGEu2ndActivity.this.mDataAction.is3rdTVSource());
                int unused4 = EPGEu2ndActivity.this.mStartHour = EPGUtil.getCurrentHour(EPGEu2ndActivity.this.mDataAction.is3rdTVSource());
                MtkLog.d(EPGEu2ndActivity.TAG, "mGetCurrentTimeRunnable~mStartHour=" + EPGEu2ndActivity.this.mStartHour);
                EPGEu2ndActivity.this.mHandler.removeMessages(EPGConfig.EPG_UPDATE_API_EVENT_LIST);
                EPGEu2ndActivity.this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_UPDATE_API_EVENT_LIST, 1000);
            }
            long unused5 = EPGEu2ndActivity.this.lastTime = EPGEu2ndActivity.this.curTime;
            String mDate = EPGUtil.formatCurrentTime(EPGEu2ndActivity.this, EPGEu2ndActivity.this.mDataAction.is3rdTVSource());
            MtkLog.d(EPGEu2ndActivity.TAG, "mGetCurrentTimeRunnable~mDate=" + mDate);
            Message msg = Message.obtain();
            msg.obj = mDate;
            msg.what = 1001;
            EPGEu2ndActivity.this.mHandler.sendMessageDelayed(msg, 1000);
        }
    };
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtkLog.d(EPGEu2ndActivity.TAG, "EPGEuHandleMessage>>msg.what=" + msg.what + ",msg.arg1=" + msg.arg1 + ",msg.arg2=" + msg.arg2);
            int i = msg.what;
            if (i == 262) {
                MtkLog.d(EPGEu2ndActivity.TAG, "EPGConfig.EPG_UPDATE_CHANNEL_LIST1111>" + msg.arg1);
                EPGEu2ndActivity.this.getChannelList();
            } else if (i == 274) {
                EPGEu2ndActivity.this.changeTimeViewsShow(EPGEu2ndActivity.this.dayNum, EPGEu2ndActivity.this.mStartHour);
                EPGEu2ndActivity.this.loadEventList();
            } else if (i != 1001) {
                if (i != 1879048194) {
                    if (i == 1879048207) {
                        TvCallbackData data = (TvCallbackData) msg.obj;
                        MtkLog.e(EPGEu2ndActivity.TAG, "Epg MSG_CB_NFY_EVENT_UPDATE message:type:" + data.param1 + "==>" + data.param2 + "==>" + data.param3 + "==>" + data.param4);
                        switch (data.param1) {
                        }
                        return;
                    } else if (i != 1879048209) {
                        if (i == 1879048226) {
                            TvCallbackData specialMsgData = (TvCallbackData) msg.obj;
                            if (specialMsgData.param1 == 0) {
                                MtkLog.d(EPGEu2ndActivity.TAG, "come in handleMessage BANNER_MSG_NAV value=== " + specialMsgData.param2);
                                switch (specialMsgData.param2) {
                                    case 2:
                                    case 3:
                                    case 4:
                                        EPGEu2ndActivity.this.mDataAction.checkPWDShow();
                                        return;
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    case 10:
                                    case 11:
                                    case 13:
                                    case 14:
                                        EPGEu2ndActivity.this.mDataAction.checkPWDShow();
                                        MtkLog.d(EPGEu2ndActivity.TAG, "Mute_current_channel----->specialMsgData.param2=" + specialMsgData.param2);
                                        TurnkeyUiMainActivity.getInstance().sendMuteMsg(CommonIntegration.getInstance().isBarkChannel(EPGEu2ndActivity.this.mEPGEuHelper.getCurChannel()));
                                        return;
                                    default:
                                        return;
                                }
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                }
                MtkLog.d(EPGEu2ndActivity.TAG, "EPG update channel list>" + ((TvCallbackData) msg.obj).param2);
                if (EPGEu2ndActivity.this.mLastChannelUpdateMsg == 8) {
                    removeMessages(262);
                } else if (EPGEu2ndActivity.this.mHandler.hasMessages(262)) {
                    return;
                }
                int unused = EPGEu2ndActivity.this.mLastChannelUpdateMsg = ((TvCallbackData) msg.obj).param2;
                Message message = Message.obtain();
                message.what = 262;
                message.arg1 = EPGEu2ndActivity.this.mLastChannelUpdateMsg;
                sendMessageDelayed(message, 1000);
            } else {
                EPGEu2ndActivity.this.mCurrentDateTv.setText((String) msg.obj);
                EPGEu2ndActivity.this.setCurrentDate();
            }
        }
    };
    private HandlerThread mHandlerThead;
    private boolean mHasInitListView;
    private boolean mIsBarkerCHTurnWhenEnter = false;
    private boolean mIsBarkerCHTurnWhenExit = false;
    /* access modifiers changed from: private */
    public int mLastChannelUpdateMsg;
    /* access modifiers changed from: private */
    public int mListHeight;
    private ImageView mLockImageView;
    /* access modifiers changed from: private */
    public EPGChannelListView mLvChannel;
    private EPGEventListView mLvEvent;
    private MtkTvBanner mMtkTvBanner;
    private TextView mNextDayTv;
    /* access modifiers changed from: private */
    public PageImp mPageChannel = null;
    /* access modifiers changed from: private */
    public PageImp mPageEvent = null;
    private TextView mPageInfoTv;
    private TextView mPrevDayTv;
    private TextView mProgramDetailTv;
    private TextView mProgramNameTv;
    private TextView mProgramRating;
    private TextView mProgramTimeTv;
    private TextView mProgramType;
    /* access modifiers changed from: private */
    public EPGChannelInfo mSelectChannelInfo;
    private TextView mSelectedDateTv;
    /* access modifiers changed from: private */
    public int mStartHour;
    private ImageView mSttlImageView;
    private Handler mThreadHandler;
    private int mTotalPage;
    private TextView mTypeFilter;
    private BroadcastReceiver mUpdateEventReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (CommonIntegration.supportTIFFunction()) {
                String actionName = intent.getAction();
                MtkLog.d(EPGEu2ndActivity.TAG, "actionName>>>" + actionName);
                if ((actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN) || actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF)) && !actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF) && actionName.equals(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN)) {
                    int channelId = intent.getIntExtra("channel_id", 0);
                    MtkLog.d(EPGEu2ndActivity.TAG, "getProgramListByChId--->channelId=" + channelId);
                    EPGEu2ndActivity.this.getCurCHEventList();
                }
            }
        }
    };
    private TextView mViewDetailTv;
    private LinearLayout mllayoutDetails;
    private final String[] preValues = new String[3];

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
        setContentView(R.layout.epg_eu_2nd_main);
        initUI();
        initData();
        changeTimeViewsShow(this.dayNum, this.mStartHour);
        registerListeners();
        registerUpdateReceiver();
        setCurrentDate();
        mesureListView();
    }

    private void registerListeners() {
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_EVENT_NFY, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNELIST, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE, this.mHandler);
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        ComponentStatusListener lister = ComponentStatusListener.getInstance();
        lister.addListener(10, this);
        lister.addListener(6, this);
        lister.addListener(17, this);
        this.mLvChannel.addPageCallback(new IPageCallback() {
            public boolean hasPrePage() {
                boolean z = false;
                if (EPGEu2ndActivity.this.mPageChannel == null) {
                    return false;
                }
                if (EPGEu2ndActivity.this.mPageChannel.getPageNum() > 1) {
                    z = true;
                }
                boolean hasMorePage = z;
                if (hasMorePage && !EPGEu2ndActivity.this.mPageChannel.hasPrePage()) {
                    EPGEu2ndActivity.this.mPageChannel.gotoPage(EPGEu2ndActivity.this.mPageChannel.getPageNum());
                }
                MtkLog.d(EPGEu2ndActivity.TAG, "hasMorePage=" + hasMorePage);
                return hasMorePage;
            }

            public boolean hasNextPage() {
                boolean z = false;
                if (EPGEu2ndActivity.this.mPageChannel == null) {
                    return false;
                }
                if (EPGEu2ndActivity.this.mPageChannel.getPageNum() > 1) {
                    z = true;
                }
                boolean hasMorePage = z;
                if (hasMorePage && !EPGEu2ndActivity.this.mPageChannel.hasNextPage()) {
                    EPGEu2ndActivity.this.mPageChannel.gotoPage(1);
                }
                MtkLog.d(EPGEu2ndActivity.TAG, "hasMorePage=" + hasMorePage);
                return hasMorePage;
            }

            public void onRefreshPage() {
                EPGEu2ndActivity.this.updateChannelList();
            }
        });
        this.mLvChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                boolean firstEnterEPG = EPGEu2ndActivity.this.mSelectChannelInfo == null;
                EPGChannelInfo unused = EPGEu2ndActivity.this.mSelectChannelInfo = (EPGChannelInfo) EPGEu2ndActivity.this.mChannelAdapter.getItem(position);
                if (!firstEnterEPG) {
                    EPGEu2ndActivity.this.turnChannel(EPGEu2ndActivity.this.mSelectChannelInfo);
                }
                EPGEu2ndActivity.this.loadEventList();
                MtkLog.d(EPGEu2ndActivity.TAG, "channelInfo=" + EPGEu2ndActivity.this.mSelectChannelInfo.getName());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.mLvChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                if (!EPGEu2ndActivity.this.checkPwdShow()) {
                }
            }
        });
        this.mLvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                if (!EPGEu2ndActivity.this.checkPwdShow()) {
                }
            }
        });
        this.mLvEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                EPGProgramInfo unused = EPGEu2ndActivity.this.mCurrentSelectedProgramInfo = (EPGProgramInfo) EPGEu2ndActivity.this.mEventAdapter.getItem(position);
                MtkLog.d(EPGEu2ndActivity.TAG, "mCurrentSelectedProgramInfo=" + EPGEu2ndActivity.this.mCurrentSelectedProgramInfo.getmTitle() + ",position=" + position);
                EPGEu2ndActivity.this.refreshDetailsInfo(EPGEu2ndActivity.this.mCurrentSelectedProgramInfo);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.mLvChannel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    EPGEu2ndActivity.this.mChannelAdapter.setSelPosNoFocus(-1);
                    return;
                }
                int lastSelPos = EPGEu2ndActivity.this.mLvChannel.getSelectedItemPosition();
                MtkLog.d(EPGEu2ndActivity.TAG, "lastSelPos=" + lastSelPos);
                EPGEu2ndActivity.this.mChannelAdapter.setSelPosNoFocus(lastSelPos);
            }
        });
        this.mLvEvent.addPageCallback(new IPageCallback() {
            public boolean hasPrePage() {
                boolean z = false;
                if (EPGEu2ndActivity.this.mPageEvent == null) {
                    return false;
                }
                if (EPGEu2ndActivity.this.mPageEvent.getPageNum() > 1) {
                    z = true;
                }
                boolean hasMorePage = z;
                if (hasMorePage && !EPGEu2ndActivity.this.mPageEvent.hasPrePage()) {
                    EPGEu2ndActivity.this.mPageEvent.gotoPage(EPGEu2ndActivity.this.mPageEvent.getPageNum());
                }
                MtkLog.d(EPGEu2ndActivity.TAG, "hasMorePage=" + hasMorePage);
                return hasMorePage;
            }

            public boolean hasNextPage() {
                boolean z = false;
                if (EPGEu2ndActivity.this.mPageEvent == null) {
                    return false;
                }
                if (EPGEu2ndActivity.this.mPageEvent.getPageNum() > 1) {
                    z = true;
                }
                boolean hasMorePage = z;
                if (hasMorePage && !EPGEu2ndActivity.this.mPageEvent.hasNextPage()) {
                    EPGEu2ndActivity.this.mPageEvent.gotoPage(1);
                }
                MtkLog.d(EPGEu2ndActivity.TAG, "hasMorePage=" + hasMorePage);
                return hasMorePage;
            }

            public void onRefreshPage() {
                EPGEu2ndActivity.this.updateEventList();
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean checkPwdShow() {
        if (this.mLockImageView.getVisibility() != 0) {
            return false;
        }
        this.mEPGPwdDialog.show();
        this.mEPGPwdDialog.sendAutoDismissMessage();
        changeBottomViewText(true, 66);
        setProgramInfoViewsInVisiable();
        this.mLockImageView.setVisibility(4);
        return true;
    }

    /* access modifiers changed from: private */
    public void getCurCHEventList() {
        MtkLog.d(TAG, "getCurCHEventList start!");
        if (this.mSelectChannelInfo != null) {
            this.mDataAction.getProgramListByChId(this.mSelectChannelInfo, this.dayNum, this.mStartHour);
        }
    }

    /* access modifiers changed from: private */
    public void loadEventList() {
        MtkLog.d(TAG, "loadEventList start!");
        if (this.mSelectChannelInfo != null) {
            if (this.mDataAction.is3rdTVSource()) {
                getCurCHEventList();
            } else {
                this.mDataAction.setActiveWindow(this.mSelectChannelInfo, this.dayNum, this.mStartHour);
            }
        }
    }

    private void initData() {
        this.mHandlerThead = new HandlerThread(TAG);
        this.mHandlerThead.start();
        this.mThreadHandler = new Handler(this.mHandlerThead.getLooper());
        this.mMtkTvBanner = MtkTvBanner.getInstance();
        this.mEPGPwdDialog = new EPGPwdDialog(this);
        this.mEPGPwdDialog.setAttachView(findViewById(R.id.epg_content_layout));
        this.mEPGPwdDialog.setOnDismissListener(this);
        this.lastHourTime = EPGUtil.getCurrentDayHourMinute();
        this.mDataAction = new EPGEuActionImpl(this, this);
        this.mChannelAdapter = new EPGEuChannelAdapter(this);
        this.mEventAdapter = new EPGEuEventAdapter(this);
        this.mLvChannel.setAdapter(this.mChannelAdapter);
        this.mLvEvent.setAdapter(this.mEventAdapter);
        this.mStartHour = EPGUtil.getCurrentHour();
        this.mFreeViewHDLogo.setVisibility(this.mDataAction.isCountryUK() ? 0 : 4);
        getChannelList();
    }

    private void addDigitTurnCHView() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.digit_turn_view_width), getResources().getDimensionPixelOffset(R.dimen.digit_turn_view_height));
        lp.gravity = 17;
        this.mDigitTurnCHView = new DigitTurnCHView(this);
        this.mDigitTurnCHView.requestFocus();
        this.mDigitTurnCHView.setVisibility(8);
        this.mDigitTurnCHView.setOnDigitTurnCHCallback(new DigitTurnCHView.OnDigitTurnCHCallback() {
            public void onTurnCH(int inputDigit) {
                List<?> currentList = EPGEu2ndActivity.this.mPageChannel.getCurrentList();
                if (currentList == null) {
                    MtkLog.w("onTurnCH", "channelList==null");
                    return;
                }
                int index = -1;
                long channelId = 0;
                EPGChannelInfo changeChannel = null;
                int i = 0;
                while (true) {
                    if (i >= currentList.size()) {
                        break;
                    }
                    EPGChannelInfo channelInfo = (EPGChannelInfo) currentList.get(i);
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
                EPGEu2ndActivity.this.mLvChannel.setSelection((index / 6) + 1);
                EPGEu2ndActivity.this.turnChannel(changeChannel);
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
        this.mLvChannel = (EPGChannelListView) findViewById(R.id.lv_channel);
        this.mLvEvent = (EPGEventListView) findViewById(R.id.lv_event);
        this.mFreeViewHDLogo = findViewById(R.id.epg_freeviewhd_logo);
        this.mDataRetrievingShow = (TextView) findViewById(R.id.epg_retrieving_data);
        this.mCurrentDateTv = (TextView) findViewById(R.id.epg_top_date_info_tv);
        this.mSelectedDateTv = (TextView) findViewById(R.id.epg_title_date_selected_tv);
        this.mllayoutDetails = (LinearLayout) findViewById(R.id.epg_content_layout);
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
        this.mProgramDetailTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        addDigitTurnCHView();
    }

    private void mesureListView() {
        findViewById(R.id.epg_listview_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                EPGEu2ndActivity.this.findViewById(R.id.epg_listview_layout).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int unused = EPGEu2ndActivity.this.mListHeight = EPGEu2ndActivity.this.findViewById(R.id.epg_listview_layout).getHeight();
                EPGEu2ndActivity.this.mChannelAdapter.setItemHeight(EPGEu2ndActivity.this.mListHeight / 6);
                EPGEu2ndActivity.this.mEventAdapter.setItemHeight(EPGEu2ndActivity.this.mListHeight / 6);
                EPGEu2ndActivity.this.getChannelList();
            }
        });
    }

    /* access modifiers changed from: private */
    public void getChannelList() {
        this.mDataAction.getChannelList();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
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
        this.mEPGEuHelper.selectCHAfterExitEPG();
        selectChannelAfterExitEPG();
        clearData();
        ((DestroyApp) getApplication()).remove(this);
    }

    private void selectChannelWhenEnterEPG() {
        this.mEPGEuHelper.selectCHWithEnterEPG();
    }

    private void selectChannelAfterExitEPG() {
        MtkTvChannelInfoBase channelInfo = CommonIntegration.getInstance().getCurChInfo();
        if (this.mSelectChannelInfo != null) {
            channelInfo = this.mSelectChannelInfo.getTVChannel();
        }
        this.mEPGEuHelper.setCurChannel(channelInfo);
        this.mEPGEuHelper.selectCHAfterExitEPG();
    }

    /* access modifiers changed from: private */
    public void setCurrentDate() {
        if (this.mDataAction.isCurrentSourceATV()) {
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
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.mHandler);
        if (this.mHandler != null) {
            this.mHandler.removeMessages(262);
        }
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_EVENT_NFY, this.mHandler);
        ComponentStatusListener.getInstance().removeListener(this);
        if (this.mUpdateEventReceiver != null) {
            unregisterReceiver(this.mUpdateEventReceiver);
            this.mUpdateEventReceiver = null;
        }
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1001);
        }
        if (this.mThreadHandler != null) {
            this.mThreadHandler.removeCallbacks(this.mGetCurrentTimeRunnable);
            this.mHandlerThead.quit();
            this.mGetCurrentTimeRunnable = null;
            this.mThreadHandler = null;
            this.mHandlerThead = null;
        }
        this.mDataAction.clearActiveWindow();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x006f, code lost:
        return super.onKeyDown(r8, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0070, code lost:
        changeBottomViewText(true, r8);
        r1 = new com.mediatek.wwtv.tvcenter.epg.eu.EpgType(r7);
        r1.setOnDismissListener(new com.mediatek.wwtv.tvcenter.epg.eu.EPGEu2ndActivity.AnonymousClass13(r7));
        r1.show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0083, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0086, code lost:
        if (r7.mTotalPage <= 1) goto L_0x00c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0088, code lost:
        r7.mCurrentPage++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0091, code lost:
        if (r7.mCurrentPage <= r7.mTotalPage) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0093, code lost:
        r7.mCurrentPage = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0095, code lost:
        r7.mProgramDetailTv.scrollTo(0, ((r7.mCurrentPage - 1) * 4) * r7.mProgramDetailTv.getLineHeight());
        r7.mPageInfoTv.setText(r7.mCurrentPage + "/" + r7.mTotalPage);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00c3, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00c8, code lost:
        if (r7.dayNum != 8) goto L_0x00cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00ca, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00cb, code lost:
        r7.dayNum++;
        r7.mStartHour = 0;
        r7.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r7.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00dc, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00df, code lost:
        if (r7.dayNum != 0) goto L_0x00e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00e1, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00e2, code lost:
        r7.dayNum--;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00e9, code lost:
        if (r7.dayNum != 0) goto L_0x00f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00eb, code lost:
        r7.mStartHour = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00f2, code lost:
        r7.mStartHour = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00f4, code lost:
        r7.mHandler.removeMessages(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST);
        r7.mHandler.sendEmptyMessageDelayed(com.mediatek.wwtv.tvcenter.epg.EPGConfig.EPG_UPDATE_API_EVENT_LIST, 500);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00fe, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r8, android.view.KeyEvent r9) {
        /*
            r7 = this;
            java.lang.String r0 = "EPGEu2ndActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "event.getRepeatCount()>>>"
            r1.append(r2)
            int r2 = r9.getRepeatCount()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            java.lang.String r0 = "EPGEu2ndActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "keyCode="
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 1
            switch(r8) {
                case 7: goto L_0x00ff;
                case 8: goto L_0x00ff;
                case 9: goto L_0x00ff;
                case 10: goto L_0x00ff;
                case 11: goto L_0x00ff;
                case 12: goto L_0x00ff;
                case 13: goto L_0x00ff;
                case 14: goto L_0x00ff;
                case 15: goto L_0x00ff;
                case 16: goto L_0x00ff;
                default: goto L_0x0034;
            }
        L_0x0034:
            r1 = 500(0x1f4, double:2.47E-321)
            r3 = 274(0x112, float:3.84E-43)
            r4 = 0
            switch(r8) {
                case 183: goto L_0x00dd;
                case 184: goto L_0x00c4;
                case 185: goto L_0x0084;
                case 186: goto L_0x0070;
                default: goto L_0x003c;
            }
        L_0x003c:
            switch(r8) {
                case 4: goto L_0x0054;
                case 23: goto L_0x004c;
                case 30: goto L_0x0070;
                case 33: goto L_0x004c;
                case 35: goto L_0x00c4;
                case 46: goto L_0x00dd;
                case 53: goto L_0x0084;
                case 66: goto L_0x004c;
                case 82: goto L_0x004b;
                case 172: goto L_0x0041;
                case 178: goto L_0x0040;
                default: goto L_0x003f;
            }
        L_0x003f:
            goto L_0x006b
        L_0x0040:
            return r0
        L_0x0041:
            int r1 = r9.getRepeatCount()
            if (r1 > 0) goto L_0x004a
            r7.finish()
        L_0x004a:
            return r0
        L_0x004b:
            return r0
        L_0x004c:
            java.lang.String r1 = "EPGEu2ndActivity"
            java.lang.String r2 = "KEYCODE_DPAD_CENTER"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            return r0
        L_0x0054:
            com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView r0 = r7.mDigitTurnCHView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0062
            com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView r0 = r7.mDigitTurnCHView
            r0.hideView()
            goto L_0x006b
        L_0x0062:
            int r0 = r9.getRepeatCount()
            if (r0 > 0) goto L_0x006b
            r7.finish()
        L_0x006b:
            boolean r0 = super.onKeyDown(r8, r9)
            return r0
        L_0x0070:
            r7.changeBottomViewText(r0, r8)
            com.mediatek.wwtv.tvcenter.epg.eu.EpgType r1 = new com.mediatek.wwtv.tvcenter.epg.eu.EpgType
            r1.<init>(r7)
            com.mediatek.wwtv.tvcenter.epg.eu.EPGEu2ndActivity$13 r2 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGEu2ndActivity$13
            r2.<init>(r1)
            r1.setOnDismissListener(r2)
            r1.show()
            return r0
        L_0x0084:
            int r1 = r7.mTotalPage
            if (r1 <= r0) goto L_0x00c3
            int r1 = r7.mCurrentPage
            int r1 = r1 + r0
            r7.mCurrentPage = r1
            int r1 = r7.mCurrentPage
            int r2 = r7.mTotalPage
            if (r1 <= r2) goto L_0x0095
            r7.mCurrentPage = r0
        L_0x0095:
            android.widget.TextView r1 = r7.mProgramDetailTv
            int r2 = r7.mCurrentPage
            int r2 = r2 - r0
            int r2 = r2 * 4
            android.widget.TextView r3 = r7.mProgramDetailTv
            int r3 = r3.getLineHeight()
            int r2 = r2 * r3
            r1.scrollTo(r4, r2)
            android.widget.TextView r1 = r7.mPageInfoTv
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r7.mCurrentPage
            r2.append(r3)
            java.lang.String r3 = "/"
            r2.append(r3)
            int r3 = r7.mTotalPage
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.setText(r2)
        L_0x00c3:
            return r0
        L_0x00c4:
            int r5 = r7.dayNum
            r6 = 8
            if (r5 != r6) goto L_0x00cb
            return r4
        L_0x00cb:
            int r5 = r7.dayNum
            int r5 = r5 + r0
            r7.dayNum = r5
            r7.mStartHour = r4
            android.os.Handler r4 = r7.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r7.mHandler
            r4.sendEmptyMessageDelayed(r3, r1)
            return r0
        L_0x00dd:
            int r5 = r7.dayNum
            if (r5 != 0) goto L_0x00e2
            return r4
        L_0x00e2:
            int r5 = r7.dayNum
            int r5 = r5 - r0
            r7.dayNum = r5
            int r5 = r7.dayNum
            if (r5 != 0) goto L_0x00f2
            int r4 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour()
            r7.mStartHour = r4
            goto L_0x00f4
        L_0x00f2:
            r7.mStartHour = r4
        L_0x00f4:
            android.os.Handler r4 = r7.mHandler
            r4.removeMessages(r3)
            android.os.Handler r4 = r7.mHandler
            r4.sendEmptyMessageDelayed(r3, r1)
            return r0
        L_0x00ff:
            com.mediatek.wwtv.tvcenter.epg.DigitTurnCHView r1 = r7.mDigitTurnCHView
            r1.keyHandler(r8, r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEu2ndActivity.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyUp>>>>" + keyCode + "  " + event.getAction());
        if (keyCode == 93 || keyCode == 130) {
            calledByScheduleList();
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
    }

    private void setLockIconVisibility(boolean isLocked) {
        MtkLog.d(TAG, "setLockIconVisibility>>>" + isLocked);
        if (isLocked) {
            if (this.mEPGPwdDialog == null || !this.mEPGPwdDialog.isShowing()) {
                this.mLockImageView.setVisibility(0);
            } else {
                this.mLockImageView.setVisibility(4);
            }
            this.mSttlImageView.setVisibility(4);
            this.mProgramDetailTv.setVisibility(4);
            return;
        }
        this.mLockImageView.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public void refreshDetailsInfo(EPGProgramInfo programInfo) {
        if (this.mSelectChannelInfo == null) {
            MtkLog.d(TAG, "mSelectChannelInfo==null");
            return;
        }
        MtkTvChannelInfoBase tvChannel = this.mSelectChannelInfo.getTVChannel();
        if (tvChannel == null) {
            MtkLog.d(TAG, "tvChannel==null");
        } else {
            this.mDataAction.refreshDetailsInfo(programInfo, tvChannel.getChannelId());
        }
    }

    private void setProgramInfoViewsInVisiable() {
        this.mProgramNameTv.setVisibility(4);
        this.mProgramTimeTv.setVisibility(4);
        this.mProgramDetailTv.setVisibility(4);
        this.mSttlImageView.setVisibility(4);
        this.mPageInfoTv.setText("");
        this.mViewDetailTv.setText("");
        this.mProgramType.setText("");
        this.mProgramRating.setText("");
    }

    private boolean isShowSTTLIcon() {
        boolean showCaptionIcon = this.mMtkTvBanner.isDisplayCaptionIcon();
        MtkLog.d(TAG, "come in isShowCaptionIcon, value == " + showCaptionIcon);
        return showCaptionIcon;
    }

    private void setProgramDetailTvState(EPGProgramInfo programInfo) {
        if (programInfo == null || programInfo.getAppendDescription() == null) {
            this.mProgramDetailTv.setText("");
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
            this.mTotalPage = 0;
            return;
        }
        this.mProgramDetailTv.setVisibility(0);
        String mDetailContent = programInfo.getAppendDescription();
        this.mProgramDetailTv.setText(mDetailContent);
        if (TextUtils.isEmpty(mDetailContent)) {
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
        }
        initProgramDetailContent();
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
                EPGEu2ndActivity.this.initProgramDetailContent();
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

    public void onDismiss(DialogInterface dialog) {
        MtkLog.d(TAG, "PWD onDismiss!!>>");
        this.mDataAction.checkPWDShow();
        refreshDetailsInfo(this.mCurrentSelectedProgramInfo);
        changeBottomViewText(false, 0);
    }

    private void calledByScheduleList() {
        DvrManager.getInstance(this).startScheduleList(this.mCurrentSelectedProgramInfo);
        MtkLog.d("Timeshift_PVR", "calledByScheduleList()");
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "EU EPG updateComponentStatus>>>" + statusID + ">>" + value);
        if (statusID == 10) {
            CommonIntegration.getInstance().setCHChanging(false);
        } else if (statusID == 6) {
            finish();
        } else if (statusID == 17) {
            finish();
        }
    }

    public void updateEventDetails(EPGProgramInfo programInfo) {
        if (programInfo == null) {
            MtkLog.d(TAG, "updateEventDetails---->programInfo==null");
            this.mllayoutDetails.setVisibility(4);
            return;
        }
        this.mllayoutDetails.setVisibility(0);
        this.mProgramNameTv.setText(TextUtils.isEmpty(this.mCurrentSelectedProgramInfo.getTitle()) ? getResources().getString(R.string.nav_epg_no_program_title) : this.mCurrentSelectedProgramInfo.getTitle());
        this.mProgramNameTv.setVisibility(0);
        String startTime = this.mDataAction.getTimeType12_24() == 1 ? programInfo.getmStartTimeStr() : Util.formatTime24_12(programInfo.getmStartTimeStr());
        String endTime = this.mDataAction.getTimeType12_24() == 1 ? programInfo.getmEndTimeStr() : Util.formatTime24_12(programInfo.getmEndTimeStr());
        MtkLog.w(TAG, "regetProgramInfo------->startTime=" + startTime + ",endTime=" + endTime);
        TextView textView = this.mProgramTimeTv;
        textView.setText(startTime + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + endTime);
        this.mProgramTimeTv.setVisibility(0);
        setProgramDetailTvState(programInfo);
        this.mProgramType.setText(programInfo.getProgramType());
        this.mProgramRating.setText(programInfo.getRatingType());
    }

    public void updateChannelList(ArrayList<EPGChannelInfo> channelList, int selectIndex, int page) {
        this.mPageChannel = new PageImp(channelList, 6);
        this.mPageChannel.gotoPage(page);
        updateChannelList();
        this.mLvChannel.setSelection(selectIndex % 6);
    }

    public void updateProgramList(List<EPGProgramInfo> programList) {
        this.mPageEvent = new PageImp(programList, 6);
        this.mPageEvent.gotoPage(1);
        MtkLog.d(TAG, "updateProgramList------>programList.size: " + programList.size());
        updateEventList();
    }

    /* access modifiers changed from: private */
    public void updateChannelList() {
        this.mChannelAdapter.setGroup(this.mPageChannel.getCurrentList());
        this.mChannelAdapter.notifyDataSetChanged();
        this.mLvChannel.requestFocus();
    }

    /* access modifiers changed from: private */
    public void updateEventList() {
        EPGProgramInfo ePGProgramInfo;
        List<?> currentList = this.mPageEvent.getCurrentList();
        this.mEventAdapter.setGroup(currentList);
        this.mEventAdapter.notifyDataSetChanged();
        if (currentList.isEmpty()) {
            ePGProgramInfo = null;
        } else {
            ePGProgramInfo = (EPGProgramInfo) currentList.get(0);
        }
        this.mCurrentSelectedProgramInfo = ePGProgramInfo;
        this.mDataAction.checkPWDShow();
        refreshDetailsInfo(this.mCurrentSelectedProgramInfo);
    }

    public void showLoading() {
        this.mDataRetrievingShow.setVisibility(0);
    }

    public void dismissLoading() {
        this.mDataRetrievingShow.setVisibility(4);
    }

    public void updateLockStatus(boolean isLocked) {
        if (!isLocked && this.mCurrentSelectedProgramInfo != null) {
            MtkLog.d(TAG, "updateEventDetails---->isHasSubTitle=" + this.mCurrentSelectedProgramInfo.isHasSubTitle());
            this.mSttlImageView.setVisibility(this.mCurrentSelectedProgramInfo.isHasSubTitle() ? 0 : 4);
        }
        setLockIconVisibility(isLocked);
        if (isLocked) {
            this.mProgramDetailTv.setVisibility(4);
            this.mViewDetailTv.setText("");
            this.mPageInfoTv.setText("");
            return;
        }
        this.mProgramDetailTv.setVisibility(0);
    }
}
