package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.setting.EditTextActivity;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog;
import com.mediatek.wwtv.tvcenter.dvr.ui.OnDVRDialogListener;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelListDialog extends NavBasicDialog implements DialogInterface.OnDismissListener, ComponentStatusListener.ICStatusListener {
    private static final int ALL_CHANNEL = 0;
    private static final int ANALOG_CHANNEL = 5;
    private static final int CATEGORIES_BASE = 8449;
    private static final int CATEGORIES_CHANNEL = 1;
    public static final int CHANGE_TYPE_CHANGECHANNEL = 4114;
    public static int CHANGE_TYPE_NETWORK_INDEX = 5;
    private static final int CHANNEL_LIST = 1;
    private static final int CHANNEL_LIST_PAGE_MAX = 7;
    public static final int CHANNEL_LIST_SElECTED_FOR_TTS = 4112;
    private static final int CHANNEL_SELECTION = 0;
    private static final String CH_SORT = "sort";
    /* access modifiers changed from: private */
    public static String CH_TYPE = "";
    private static final String CH_TYPE_SECOND = "typeSecond";
    public static final int DEFAULT_CHANGE_CHANNEL_DELAY_TIME = 6000;
    private static final int DIGITAL_CHANNEL = 1;
    private static final int ENCRYPTED_CHANNEL = 4;
    private static final String FAVOURITE_TYPE = "favouriteType";
    public static final int FIND_CHANNELLIST = 4113;
    private static final int FREE_CHANNEL = 3;
    public static final int MESSAGE_DEFAULT_CAN_CHANGECHANNEL_DELAY = 4104;
    private static final int PACKS_BASE = 4353;
    private static final int PACKS_CHANNEL = 2;
    private static final int RADIO_CHANNEL = 2;
    private static final String SATELLITE_RECORDID = "satellite_recordid";
    private static final String SEL_MORE = "more";
    private static final int SET_SELECTION_INDEX = 4097;
    private static final String SPNAME = "CHMODE";
    private static final String TAG = "ChannelListDialog";
    private static final int TYPE_CHANGECHANNEL_DOWN = 4099;
    private static final int TYPE_CHANGECHANNEL_ENTER = 4101;
    private static final int TYPE_CHANGECHANNEL_PRE = 4100;
    private static final int TYPE_CHANGECHANNEL_UP = 4098;
    private static final int TYPE_REGET_CHANNELLIST = 4103;
    private static final int TYPE_RESET_CHANNELLIST = 4102;
    private static final int TYPE_UPDATE_CHANNELLIST = 4105;
    private static final String categoriesOther = "Other";
    private String CURRENT_CHANNEL_FIND;
    private int CURRENT_CHANNEL_MODE;
    /* access modifiers changed from: private */
    public int CURRENT_CHANNEL_SECOND_TYPE;
    /* access modifiers changed from: private */
    public int CURRENT_CHANNEL_SORT;
    /* access modifiers changed from: private */
    public int CURRENT_CHANNEL_TYPE;
    private int CURRENT_FAVOURITE_TYPE;
    /* access modifiers changed from: private */
    public int CURRENT_SELECT_MORE;
    /* access modifiers changed from: private */
    public boolean SELECT_TYPE_CHANGE_CH;
    /* access modifiers changed from: private */
    public ProgressBar chanelListProgressbar;
    /* access modifiers changed from: private */
    public CommonIntegration commonIntegration;
    private String[] favTypes;
    /* access modifiers changed from: private */
    public boolean hasNextPage;
    boolean isFindingForChannelList;
    private CommonIntegration.ChannelChangedListener listener;
    private View.AccessibilityDelegate mAccDelegate;
    private TextView mBlueKeyText;
    private ImageView mBlueicon;
    /* access modifiers changed from: private */
    public boolean mCanChangeChannel;
    /* access modifiers changed from: private */
    public ChannelAdapter mChannelAdapter;
    /* access modifiers changed from: private */
    public View mChannelDetailLayout;
    private TextView mChannelDetailTileText;
    /* access modifiers changed from: private */
    public Runnable mChannelDownRunnable;
    /* access modifiers changed from: private */
    public List<Integer> mChannelIdList;
    private View mChannelListFunctionLayout;
    private View mChannelListLayout;
    private View mChannelListPageUpDownLayout;
    /* access modifiers changed from: private */
    public View mChannelListTipView;
    /* access modifiers changed from: private */
    public ListView mChannelListView;
    /* access modifiers changed from: private */
    public Runnable mChannelPreRunnable;
    /* access modifiers changed from: private */
    public ListView mChannelSatelliteSecondView;
    /* access modifiers changed from: private */
    public ListView mChannelSatelliteView;
    /* access modifiers changed from: private */
    public ListView mChannelSelectMoreView;
    /* access modifiers changed from: private */
    public ListView mChannelSortView;
    private List<Integer> mChannelTypePacksOrCategory;
    /* access modifiers changed from: private */
    public ListView mChannelTypeView;
    /* access modifiers changed from: private */
    public Runnable mChannelUpRunnable;
    /* access modifiers changed from: private */
    public int mCurCategories;
    /* access modifiers changed from: private */
    public int mCurMask;
    /* access modifiers changed from: private */
    public int mCurVal;
    /* access modifiers changed from: private */
    public int mCurrentSatelliteRecordId;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private HandlerThread mHandlerThead;
    /* access modifiers changed from: private */
    public boolean mIsTifFunction;
    /* access modifiers changed from: private */
    public boolean mIsTuneChannel;
    /* access modifiers changed from: private */
    public int mLastSelection;
    private String mNavChannelDetailsChannelInfoString;
    private TextView mNavChannelDetailsChannelInfoTextView;
    private Runnable mResetChannelListRunnable;
    /* access modifiers changed from: private */
    public List<MtkTvDvbsConfigInfoBase> mSatelliteListinfo;
    private String[] mSatelliteRecords;
    /* access modifiers changed from: private */
    public SaveValue mSaveValue;
    /* access modifiers changed from: private */
    public boolean mSelectionShow;
    /* access modifiers changed from: private */
    public int mSendKeyCode;
    /* access modifiers changed from: private */
    public TIFChannelManager mTIFChannelManager;
    /* access modifiers changed from: private */
    public Handler mThreadHandler;
    /* access modifiers changed from: private */
    public String mTitlePre;
    /* access modifiers changed from: private */
    public TextView mTitleText;
    private MtkTvAppTVBase mTvAppTvBase;
    /* access modifiers changed from: private */
    public Runnable mUpdateChannelListRunnable;
    /* access modifiers changed from: private */
    public TextView mYellowKeyText;
    String[] moreItems;
    private MtkTvPWDDialog mtkTvPwd;
    /* access modifiers changed from: private */
    public int preChId;
    String sortTitle;
    String[] sorts;
    private String titlefavN;
    /* access modifiers changed from: private */
    public int ttsSelectchannelIndex;
    /* access modifiers changed from: private */
    public String[] types;
    /* access modifiers changed from: private */
    public List<String> typesSecond;

    public ChannelListDialog(Context context, int theme) {
        super(context, theme);
        this.CURRENT_FAVOURITE_TYPE = 0;
        this.CURRENT_CHANNEL_TYPE = 0;
        this.SELECT_TYPE_CHANGE_CH = false;
        this.CURRENT_CHANNEL_SECOND_TYPE = 0;
        this.CURRENT_SELECT_MORE = 0;
        this.CURRENT_CHANNEL_SORT = 0;
        this.CURRENT_CHANNEL_FIND = "findChannels";
        this.titlefavN = "";
        this.mCurrentSatelliteRecordId = -1;
        this.mSelectionShow = false;
        this.CURRENT_CHANNEL_MODE = 1;
        this.mCurMask = CommonIntegration.CH_LIST_MASK;
        this.mCurVal = CommonIntegration.CH_LIST_VAL;
        this.mCurCategories = -1;
        this.hasNextPage = false;
        this.mIsTuneChannel = true;
        this.mResetChannelListRunnable = new Runnable() {
            public void run() {
                TIFChannelInfo mTIFChannelInfo;
                MtkLog.d(ChannelListDialog.TAG, "thread channel mResetChannelListRunnable>>" + Thread.currentThread().getId() + ">>" + Thread.currentThread().getName());
                int chId = TIFFunctionUtil.getCurrentChannelId();
                if ((ChannelListDialog.this.commonIntegration.is3rdTVSource() || (ChannelListDialog.this.mCurMask == -1 && ChannelListDialog.this.mCurVal == -1)) && (mTIFChannelInfo = ChannelListDialog.this.mTIFChannelManager.getChannelInfoByUri()) != null && mTIFChannelInfo.mMtkTvChannelInfo == null) {
                    chId = (int) mTIFChannelInfo.mId;
                }
                MtkLog.d(ChannelListDialog.TAG, "thread channel mResetChannelListRunnable>> chId " + chId);
                List<TIFChannelInfo> tempList = ChannelListDialog.this.processTIFChListWithThread(chId);
                MtkLog.d(ChannelListDialog.TAG, "thread channel mResetChannelListRunnable>>time");
                Message msg = Message.obtain();
                msg.what = 4102;
                msg.arg1 = chId;
                msg.obj = tempList;
                ChannelListDialog.this.mHandler.sendMessage(msg);
            }
        };
        this.mUpdateChannelListRunnable = new Runnable() {
            public void run() {
                List<TIFChannelInfo> tempList;
                MtkLog.d(ChannelListDialog.TAG, "thread channel mUpdateChannelListRunnable>>" + Thread.currentThread().getId() + ">>" + Thread.currentThread().getName());
                if (ChannelListDialog.this.commonIntegration.is3rdTVSource()) {
                    tempList = ChannelListDialog.this.getAllChannelListByTIFFor3rdSource((int) ChannelListDialog.this.mChannelAdapter.getItem(0).mId);
                } else {
                    tempList = ChannelListDialog.this.updateCurrentChannelLlistByTIF();
                }
                Message msg = Message.obtain();
                msg.what = 4105;
                msg.obj = tempList;
                ChannelListDialog.this.mHandler.sendMessage(msg);
            }
        };
        this.mChannelUpRunnable = new Runnable() {
            public void run() {
                MtkLog.d(ChannelListDialog.TAG, "thread channel mChannelUpRunnable>>" + Thread.currentThread().getId() + ">>" + Thread.currentThread().getName() + " mIsTuneChannel " + ChannelListDialog.this.mIsTuneChannel);
                if (ChannelListDialog.this.mCanChangeChannel) {
                    boolean unused = ChannelListDialog.this.mCanChangeChannel = false;
                    if (ChannelListDialog.this.mIsTuneChannel) {
                        int unused2 = ChannelListDialog.this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_CHUP;
                        if (!ChannelListDialog.this.channelUpDown(true)) {
                            ComponentStatusListener.getInstance().updateStatus(10, 0);
                            boolean unused3 = ChannelListDialog.this.mCanChangeChannel = true;
                        }
                    } else if (ChannelListDialog.this.channelUpDown(true)) {
                        Message msg = Message.obtain();
                        msg.arg1 = KeyMap.KEYCODE_MTKIR_CHUP;
                        msg.what = 4097;
                        ChannelListDialog.this.mHandler.sendMessage(msg);
                    } else {
                        boolean unused4 = ChannelListDialog.this.mCanChangeChannel = true;
                    }
                }
            }
        };
        this.mChannelPreRunnable = new Runnable() {
            public void run() {
                MtkLog.d(ChannelListDialog.TAG, "thread channel mChannelPreRunnable>>" + Thread.currentThread().getId() + ">>" + Thread.currentThread().getName());
                if (ChannelListDialog.this.mCanChangeChannel) {
                    boolean unused = ChannelListDialog.this.mCanChangeChannel = false;
                    if (ChannelListDialog.this.mIsTuneChannel) {
                        int unused2 = ChannelListDialog.this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_PRECH;
                        if (!ChannelListDialog.this.mTIFChannelManager.channelPre()) {
                            boolean unused3 = ChannelListDialog.this.mCanChangeChannel = true;
                            return;
                        }
                        return;
                    }
                    int seletcPostion = ChannelListDialog.this.mChannelListView.getSelectedItemPosition();
                    MtkLog.d(ChannelListDialog.TAG, "mChannelPreRunnable seletcPostion = " + seletcPostion);
                    if (ChannelListDialog.this.commonIntegration.channelPre()) {
                        Message msg = Message.obtain();
                        msg.arg1 = KeyMap.KEYCODE_MTKIR_PRECH;
                        msg.arg2 = seletcPostion;
                        msg.what = 4097;
                        ChannelListDialog.this.mHandler.sendMessage(msg);
                        return;
                    }
                    boolean unused4 = ChannelListDialog.this.mCanChangeChannel = true;
                }
            }
        };
        this.mChannelDownRunnable = new Runnable() {
            public void run() {
                MtkLog.d(ChannelListDialog.TAG, "thread channel mChannelDownRunnable>>" + Thread.currentThread().getId() + ">>" + Thread.currentThread().getName());
                if (ChannelListDialog.this.mCanChangeChannel) {
                    boolean unused = ChannelListDialog.this.mCanChangeChannel = false;
                    if (ChannelListDialog.this.mIsTuneChannel) {
                        int unused2 = ChannelListDialog.this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_CHDN;
                        if (!ChannelListDialog.this.channelUpDown(false)) {
                            boolean unused3 = ChannelListDialog.this.mCanChangeChannel = true;
                            ComponentStatusListener.getInstance().updateStatus(10, 0);
                        }
                    } else if (ChannelListDialog.this.channelUpDown(false)) {
                        Message msg = Message.obtain();
                        msg.arg1 = KeyMap.KEYCODE_MTKIR_CHDN;
                        msg.what = 4097;
                        ChannelListDialog.this.mHandler.sendMessage(msg);
                    } else {
                        boolean unused4 = ChannelListDialog.this.mCanChangeChannel = true;
                    }
                }
            }
        };
        this.ttsSelectchannelIndex = -1;
        this.mAccDelegate = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                MtkLog.d(ChannelListDialog.TAG, "onRequestSendAccessibilityEvent.host.getId()" + host.getId());
                List<CharSequence> texts = event.getText();
                if (texts != null) {
                    MtkLog.d(ChannelListDialog.TAG, "texts :" + texts);
                    switch (host.getId()) {
                        case R.id.nav_channel_listview:
                            MtkLog.d(ChannelListDialog.TAG, "R.id.nav_channel_listview");
                            if (event.getEventType() != 32768) {
                                if (event.getEventType() == 1) {
                                    MtkLog.d(ChannelListDialog.TAG, "click item");
                                    if (ChannelListDialog.this.ttsSelectchannelIndex > -1) {
                                        TIFChannelInfo selectedChannel = ChannelListDialog.this.mTIFChannelManager.getTIFChannelInfoById(((Integer) ChannelListDialog.this.mChannelIdList.get(ChannelListDialog.this.ttsSelectchannelIndex)).intValue());
                                        TIFChannelInfo currentChannel = ChannelListDialog.this.mTIFChannelManager.getCurrChannelInfo();
                                        if (selectedChannel != null && currentChannel != null) {
                                            if (selectedChannel.mId != currentChannel.mId) {
                                                showPvrDialog(selectedChannel);
                                                break;
                                            }
                                        } else if (selectedChannel != null && currentChannel == null) {
                                            showPvrDialog(selectedChannel);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                int unused = ChannelListDialog.this.ttsSelectchannelIndex = findSelectItem(texts);
                                MtkLog.d(ChannelListDialog.TAG, ":ttsSelectchannelIndex =" + ChannelListDialog.this.ttsSelectchannelIndex);
                                if (ChannelListDialog.this.ttsSelectchannelIndex >= 0) {
                                    ChannelListDialog.this.startTimeout(10000);
                                    break;
                                }
                            }
                            break;
                        case R.id.nav_channel_select_more:
                            MtkLog.d(ChannelListDialog.TAG, "R.id.nav_channel_select_more");
                            if (event.getEventType() != 32768) {
                                if (event.getEventType() == 1) {
                                    MtkLog.d(ChannelListDialog.TAG, ": v.getId() = " + host.getId());
                                    boolean unused2 = ChannelListDialog.this.selectedItem(host, event);
                                    break;
                                }
                            } else {
                                int index = findSelectItemforMore(texts.get(0).toString());
                                MtkLog.d(ChannelListDialog.TAG, "nav_channel_select_more:index =" + index);
                                if (index >= 0) {
                                    ChannelListDialog.this.mHandler.removeMessages(4112);
                                    Message msg = Message.obtain();
                                    msg.what = 4112;
                                    msg.arg1 = R.id.nav_channel_select_more;
                                    msg.arg2 = index;
                                    ChannelListDialog.this.mHandler.sendMessageDelayed(msg, 90);
                                    break;
                                }
                            }
                            break;
                        case R.id.nav_channel_sort:
                            MtkLog.d(ChannelListDialog.TAG, "R.id.nav_channel_sort");
                            if (event.getEventType() != 32768) {
                                if (event.getEventType() == 1) {
                                    MtkLog.d(ChannelListDialog.TAG, ": v.getId() = " + host.getId());
                                    boolean unused3 = ChannelListDialog.this.selectedItem(host, event);
                                    break;
                                }
                            } else {
                                int index2 = findSelectItemforSort(texts.get(0).toString());
                                MtkLog.d(ChannelListDialog.TAG, "nav_channel_sort:index =" + index2);
                                if (index2 >= 0) {
                                    ChannelListDialog.this.mHandler.removeMessages(4112);
                                    Message msg2 = Message.obtain();
                                    msg2.what = 4112;
                                    msg2.arg1 = R.id.nav_channel_sort;
                                    msg2.arg2 = index2;
                                    ChannelListDialog.this.mHandler.sendMessageDelayed(msg2, 90);
                                    break;
                                }
                            }
                            break;
                        case R.id.nav_channel_typeview:
                            MtkLog.d(ChannelListDialog.TAG, "R.id.nav_channel_typeview");
                            if (event.getEventType() != 32768) {
                                if (event.getEventType() == 1) {
                                    MtkLog.d(ChannelListDialog.TAG, ": v.getId() = " + host.getId());
                                    boolean unused4 = ChannelListDialog.this.selectedItem(host, event);
                                    break;
                                }
                            } else {
                                int index3 = findSelectItemforType(texts.get(0).toString());
                                MtkLog.d(ChannelListDialog.TAG, "nav_channel_typeview:index =" + index3);
                                if (index3 >= 0) {
                                    ChannelListDialog.this.mHandler.removeMessages(4112);
                                    Message msg3 = Message.obtain();
                                    msg3.what = 4112;
                                    msg3.arg1 = R.id.nav_channel_typeview;
                                    msg3.arg2 = index3;
                                    ChannelListDialog.this.mHandler.sendMessageDelayed(msg3, 90);
                                    break;
                                }
                            }
                            break;
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d(ChannelListDialog.TAG, "Exception " + e);
                    return true;
                }
            }

            private void showPvrDialog(TIFChannelInfo selectedChannel) {
                final MtkTvChannelInfoBase selectedChannels = selectedChannel.mMtkTvChannelInfo;
                if (DvrManager.getInstance() == null || !DvrManager.getInstance().pvrIsRecording()) {
                    ChannelListDialog.this.selectTifChannel(23, selectedChannel);
                    Message msg = Message.obtain();
                    msg.arg1 = 23;
                    msg.what = 4097;
                    ChannelListDialog.this.mHandler.sendMessageDelayed(msg, 1200);
                    return;
                }
                String srctype = DvrManager.getInstance().getController().getSrcType();
                if (srctype.equals("TV") || InputSourceManager.getInstance(ChannelListDialog.this.mContext).getConflictSourceList().contains(srctype) || selectedChannel.mType.equals("1")) {
                    DvrDialog conDialog = new DvrDialog((Activity) ChannelListDialog.this.mContext, 40961, 23, 1);
                    MtkLog.e(ChannelListDialog.TAG, "channelID:-1,ID:" + selectedChannels.getChannelId());
                    MtkLog.e(ChannelListDialog.TAG, "channelID:-1,Name:" + selectedChannels.getServiceName());
                    conDialog.setMtkTvChannelInfoBase(selectedChannels.getChannelId());
                    conDialog.setOnPVRDialogListener(new OnDVRDialogListener() {
                        public void onDVRDialogListener(int keyCode) {
                            MtkLog.d(ChannelListDialog.TAG, "OnPVRDialogListener keyCode>>>" + keyCode);
                            if (keyCode == 23) {
                                Message msg = Message.obtain();
                                msg.what = 4101;
                                msg.arg1 = selectedChannels.getChannelId();
                                ChannelListDialog.this.mHandler.sendMessageDelayed(msg, 3000);
                            }
                        }
                    });
                    conDialog.show();
                    ChannelListDialog.this.dismiss();
                    return;
                }
                ChannelListDialog.this.selectTifChannel(23, selectedChannel);
            }

            private int findSelectItem(List<CharSequence> texts) {
                if (ChannelListDialog.this.mChannelIdList == null) {
                    return -1;
                }
                if (texts.size() > 1) {
                    MtkLog.d(ChannelListDialog.TAG, "findSelectItem texts.get(0) =" + texts.get(0).toString() + "findSelectItem texts.get(1) =" + texts.get(1).toString());
                    for (int i = 0; i < ChannelListDialog.this.mChannelIdList.size(); i++) {
                        TIFChannelInfo tifchannelinfo = ChannelListDialog.this.mTIFChannelManager.getTIFChannelInfoById(((Integer) ChannelListDialog.this.mChannelIdList.get(i)).intValue());
                        if (tifchannelinfo != null && tifchannelinfo.mDisplayNumber.equals(texts.get(0).toString()) && tifchannelinfo.mDisplayName.equals(texts.get(1).toString())) {
                            return i;
                        }
                    }
                } else {
                    MtkLog.d(ChannelListDialog.TAG, "findSelectItem texts =" + texts.get(0).toString());
                    for (int i2 = 0; i2 < ChannelListDialog.this.mChannelIdList.size(); i2++) {
                        TIFChannelInfo tifchannelinfo2 = ChannelListDialog.this.mTIFChannelManager.getTIFChannelInfoById(((Integer) ChannelListDialog.this.mChannelIdList.get(i2)).intValue());
                        if (tifchannelinfo2 != null && tifchannelinfo2.mDisplayNumber.equals(texts.get(0).toString())) {
                            return i2;
                        }
                    }
                }
                return -1;
            }

            private int findSelectItemforMore(String text) {
                MtkLog.d(ChannelListDialog.TAG, "findSelectItemforMore texts =" + text);
                if (ChannelListDialog.this.moreItems == null) {
                    return -1;
                }
                for (int i = 0; i < ChannelListDialog.this.moreItems.length; i++) {
                    MtkLog.d(ChannelListDialog.TAG, ":index =" + ChannelListDialog.this.moreItems[i] + " text = " + text);
                    if (ChannelListDialog.this.moreItems[i].equals(text)) {
                        return i;
                    }
                }
                return -1;
            }

            private int findSelectItemforType(String text) {
                MtkLog.d(ChannelListDialog.TAG, "findSelectItemforType texts =" + text);
                if (ChannelListDialog.this.types == null) {
                    return -1;
                }
                for (int i = 0; i < ChannelListDialog.this.types.length; i++) {
                    MtkLog.d(ChannelListDialog.TAG, ":index =" + ChannelListDialog.this.types[i] + " text = " + text);
                    if (ChannelListDialog.this.types[i].equals(text)) {
                        return i;
                    }
                }
                return -1;
            }

            private int findSelectItemforSort(String text) {
                MtkLog.d(ChannelListDialog.TAG, "findSelectItemforSort texts =" + text);
                if (ChannelListDialog.this.sorts == null) {
                    return -1;
                }
                for (int i = 0; i < ChannelListDialog.this.sorts.length; i++) {
                    MtkLog.d(ChannelListDialog.TAG, ":index =" + ChannelListDialog.this.sorts[i] + " text = " + text);
                    if (ChannelListDialog.this.sorts[i].equals(text)) {
                        return i;
                    }
                }
                return -1;
            }
        };
        this.listener = new CommonIntegration.ChannelChangedListener() {
            public void onChannelChanged() {
                if (ChannelListDialog.this.isShowing() && !ChannelListDialog.this.mSelectionShow) {
                    if (!ChannelListDialog.this.mIsTifFunction) {
                        ChannelListDialog.this.resetChList();
                    } else if (ChannelListDialog.this.mThreadHandler != null && !ChannelListDialog.this.mThreadHandler.hasCallbacks(ChannelListDialog.this.mUpdateChannelListRunnable)) {
                        ChannelListDialog.this.mThreadHandler.post(ChannelListDialog.this.mUpdateChannelListRunnable);
                    }
                }
            }
        };
        this.isFindingForChannelList = false;
        this.componentID = NavBasic.NAV_COMP_ID_CH_LIST;
        initHandler();
        this.commonIntegration = CommonIntegration.getInstance();
        CH_TYPE = CommonIntegration.CH_TYPE_BASE + this.commonIntegration.getSvl();
        this.mSaveValue = SaveValue.getInstance(context);
        this.mtkTvPwd = MtkTvPWDDialog.getInstance();
        this.mChannelIdList = new ArrayList();
        this.mTIFChannelManager = TIFChannelManager.getInstance(this.mContext);
        this.mTvAppTvBase = new MtkTvAppTVBase();
        this.mIsTifFunction = CommonIntegration.supportTIFFunction();
        ComponentStatusListener lister = ComponentStatusListener.getInstance();
        lister.addListener(10, this);
        lister.addListener(2, this);
        lister.addListener(3, this);
        this.mChannelTypePacksOrCategory = new ArrayList();
    }

    private void initHandler() {
        this.mHandlerThead = new HandlerThread(TAG);
        this.mHandlerThead.start();
        this.mThreadHandler = new Handler(this.mHandlerThead.getLooper());
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                int curChId;
                List<TIFChannelInfo> chList;
                List<MtkTvChannelInfoBase> tempApiChList;
                int tempmask;
                int tempVal;
                List<TIFChannelInfo> chList2;
                List<MtkTvChannelInfoBase> tempApiChList2;
                Message message = msg;
                super.handleMessage(msg);
                MtkLog.d(ChannelListDialog.TAG, "thread channel handler onResume()>>" + Thread.currentThread().getId() + ">>" + Thread.currentThread().getName());
                int i = message.what;
                if (i == 4112) {
                    MtkLog.d(ChannelListDialog.TAG, "CHANNEL_LIST_SElECTED_FOR_TTS data.parama1  = " + message.arg1);
                    int i2 = message.arg1;
                    if (i2 != R.id.nav_channel_typeview) {
                        switch (i2) {
                            case R.id.nav_channel_select_more:
                                ChannelListDialog.this.mChannelSelectMoreView.setSelection(message.arg2);
                                break;
                            case R.id.nav_channel_sort:
                                ChannelListDialog.this.mChannelSortView.setSelection(message.arg2);
                                break;
                            default:
                                ChannelListDialog.this.mChannelListView.setSelection(message.arg2);
                                break;
                        }
                    } else {
                        ChannelListDialog.this.mChannelTypeView.setSelection(message.arg2);
                    }
                    ChannelListDialog.this.startTimeout(10000);
                } else if (i == 4114) {
                    MtkLog.d(ChannelListDialog.TAG, "CHANGE_TYPE_CHANGECHANNEL select first channel in the chlist");
                    if (ChannelListDialog.this.mChannelListView.getAdapter() != null && ChannelListDialog.this.mChannelAdapter.getChannellist().size() > 0) {
                        ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.CH_TYPE, ChannelListDialog.this.CURRENT_CHANNEL_TYPE);
                        ChannelListDialog.this.mTIFChannelManager.selectChannelByTIFInfo(ChannelListDialog.this.mChannelAdapter.getChannellist().get(0));
                        ChannelListDialog.this.mHandler.removeMessages(4112);
                        Message mess = Message.obtain();
                        mess.what = 4112;
                        mess.arg1 = R.id.nav_channel_listview;
                        mess.arg2 = 0;
                        ChannelListDialog.this.mHandler.sendMessageDelayed(mess, 1000);
                    }
                } else if (i != 1879048198) {
                    switch (i) {
                        case 4097:
                            ComponentStatusListener.getInstance().updateStatus(5, message.arg1);
                            if (ChannelListDialog.this.commonIntegration.isdualtunermode()) {
                                curChId = ChannelListDialog.this.commonIntegration.get2NDCurrentChannelId();
                                MtkLog.d(ChannelListDialog.TAG, "thread channel handler onResume()>> isdualtunermode() " + curChId);
                            } else {
                                curChId = ChannelListDialog.this.commonIntegration.getCurrentChannelId();
                            }
                            MtkLog.d(ChannelListDialog.TAG, "thread channel handler onResume()>> " + curChId);
                            if (CommonIntegration.isUSRegion()) {
                                ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.updateCurrentChannelLlistByTIF());
                            }
                            int chIndex = ChannelListDialog.this.mChannelAdapter.isExistCh(curChId);
                            MtkLog.d(ChannelListDialog.TAG, "SET_SELECTION_INDEX chIndex = " + chIndex);
                            if (chIndex >= 0) {
                                ChannelListDialog.this.mChannelListView.requestFocus();
                                ChannelListDialog.this.mChannelListView.setSelection(chIndex);
                                int unused = ChannelListDialog.this.mLastSelection = chIndex;
                            } else if (ChannelListDialog.this.commonIntegration.isdualtunermode()) {
                                if (message.arg1 == 166) {
                                    ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.getNextPrePageChListByTIF(false));
                                    ChannelListDialog.this.mChannelListView.requestFocus();
                                    ChannelListDialog.this.mChannelListView.setSelection(0);
                                    int unused2 = ChannelListDialog.this.mLastSelection = 0;
                                } else if (message.arg1 == 167) {
                                    ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.getNextPrePageChListByTIF(true));
                                    ChannelListDialog.this.mChannelListView.requestFocus();
                                    ChannelListDialog.this.mChannelListView.setSelection(ChannelListDialog.this.mChannelAdapter.getCount() - 1);
                                    int unused3 = ChannelListDialog.this.mLastSelection = ChannelListDialog.this.mChannelAdapter.getCount() - 1;
                                } else if (message.arg1 == 229) {
                                    int tempmask2 = ChannelListDialog.this.mCurMask;
                                    int tempVal2 = ChannelListDialog.this.mCurVal;
                                    if (ChannelListDialog.this.mCurMask == CommonIntegration.CH_LIST_MASK && ChannelListDialog.this.mCurVal == CommonIntegration.CH_LIST_VAL) {
                                        int unused4 = ChannelListDialog.this.mCurMask = CommonIntegration.CH_LIST_DIGITAL_RADIO_MASK;
                                        int unused5 = ChannelListDialog.this.mCurVal = CommonIntegration.CH_LIST_DIGITAL_RADIO_VAL;
                                    }
                                    MtkLog.d(ChannelListDialog.TAG, "KEYCODE_MTKIR_PRECH chIndex =" + chIndex);
                                    if (!ChannelListDialog.this.isSelectionMode() || ChannelListDialog.this.commonIntegration.checkCurChMask(ChannelListDialog.this.mCurMask & (~MtkTvChCommonBase.SB_VNET_ACTIVE), ChannelListDialog.this.mCurVal & (~MtkTvChCommonBase.SB_VNET_ACTIVE))) {
                                        if (message.arg2 < 0) {
                                            message.arg2 = 0;
                                        }
                                        if (ChannelListDialog.this.mIsTifFunction) {
                                            TIFChannelManager access$1000 = ChannelListDialog.this.mTIFChannelManager;
                                            tempVal = tempVal2;
                                            int tempVal3 = ChannelListDialog.this.mCurMask;
                                            tempmask = tempmask2;
                                            chList2 = access$1000.getTIFPreOrNextChannelList(curChId, false, true, 7, tempVal3, ChannelListDialog.this.mCurVal);
                                        } else {
                                            tempVal = tempVal2;
                                            tempmask = tempmask2;
                                            if (!CommonIntegration.isEURegion() || ChannelListDialog.this.CURRENT_CHANNEL_TYPE == 0) {
                                                tempApiChList2 = ChannelListDialog.this.commonIntegration.getChList(curChId, message.arg2, 7 - message.arg2);
                                            } else if (ChannelListDialog.this.commonIntegration.checkCurChMask(ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal)) {
                                                tempApiChList2 = ChannelListDialog.this.commonIntegration.getChannelListByMaskValuefilter(curChId, message.arg2, 7 - message.arg2, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal, true);
                                            } else {
                                                tempApiChList2 = ChannelListDialog.this.commonIntegration.getChannelListByMaskValuefilter(curChId, message.arg2, 7 - message.arg2, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal, false);
                                            }
                                            chList2 = TIFFunctionUtil.getTIFChannelList(tempApiChList2);
                                        }
                                        ChannelListDialog.this.mChannelAdapter.updateData(chList2);
                                        ChannelListDialog.this.mChannelListView.requestFocus();
                                        ChannelListDialog.this.mChannelListView.setSelection(message.arg2);
                                        int unused6 = ChannelListDialog.this.mLastSelection = message.arg2;
                                    } else {
                                        ChannelListDialog.this.exit();
                                        tempVal = tempVal2;
                                        tempmask = tempmask2;
                                    }
                                    int unused7 = ChannelListDialog.this.mCurMask = tempmask;
                                    int unused8 = ChannelListDialog.this.mCurVal = tempVal;
                                }
                            } else if (message.arg1 == 166) {
                                if (ChannelListDialog.this.mIsTifFunction) {
                                    ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.mTIFChannelManager.getTIFPreOrNextChannelList(curChId, false, true, 7, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal));
                                } else {
                                    ChannelListDialog.this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(ChannelListDialog.this.getChannelList(ChannelListDialog.this.preChId, 2, 7, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal)));
                                }
                                ChannelListDialog.this.mChannelListView.requestFocus();
                                ChannelListDialog.this.mChannelListView.setSelection(0);
                                int unused9 = ChannelListDialog.this.mLastSelection = 0;
                            } else if (message.arg1 == 167) {
                                if (ChannelListDialog.this.mIsTifFunction) {
                                    ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.mTIFChannelManager.getTIFPreOrNextChannelList(curChId, true, true, 7, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal));
                                } else {
                                    ChannelListDialog.this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(ChannelListDialog.this.getChannelList(curChId + 1, 3, 7, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal)));
                                }
                                ChannelListDialog.this.mChannelListView.requestFocus();
                                ChannelListDialog.this.mChannelListView.setSelection(ChannelListDialog.this.mChannelAdapter.getCount() - 1);
                                int unused10 = ChannelListDialog.this.mLastSelection = ChannelListDialog.this.mChannelAdapter.getCount() - 1;
                            } else if (message.arg1 == 229) {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_MTKIR_PRECH chIndex =" + chIndex);
                                if (!ChannelListDialog.this.isSelectionMode() || ChannelListDialog.this.commonIntegration.checkCurChMask(ChannelListDialog.this.mCurMask & (~MtkTvChCommonBase.SB_VNET_ACTIVE), ChannelListDialog.this.mCurVal & (~MtkTvChCommonBase.SB_VNET_ACTIVE))) {
                                    if (message.arg2 < 0) {
                                        message.arg2 = 0;
                                    }
                                    if (ChannelListDialog.this.mIsTifFunction) {
                                        chList = ChannelListDialog.this.mTIFChannelManager.getTIFPreOrNextChannelList(curChId, false, true, 7, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal);
                                    } else {
                                        if (!CommonIntegration.isEURegion() || ChannelListDialog.this.CURRENT_CHANNEL_TYPE == 0) {
                                            tempApiChList = ChannelListDialog.this.commonIntegration.getChList(curChId, message.arg2, 7 - message.arg2);
                                        } else if (ChannelListDialog.this.commonIntegration.checkCurChMask(ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal)) {
                                            tempApiChList = ChannelListDialog.this.commonIntegration.getChannelListByMaskValuefilter(curChId, message.arg2, 7 - message.arg2, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal, true);
                                        } else {
                                            tempApiChList = ChannelListDialog.this.commonIntegration.getChannelListByMaskValuefilter(curChId, message.arg2, 7 - message.arg2, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal, false);
                                        }
                                        chList = TIFFunctionUtil.getTIFChannelList(tempApiChList);
                                    }
                                    ChannelListDialog.this.mChannelAdapter.updateData(chList);
                                    ChannelListDialog.this.mChannelListView.requestFocus();
                                    ChannelListDialog.this.mChannelListView.setSelection(message.arg2);
                                    int unused11 = ChannelListDialog.this.mLastSelection = message.arg2;
                                } else {
                                    ChannelListDialog.this.exit();
                                }
                            }
                            if (ChannelListDialog.this.mChannelAdapter != null) {
                                ChannelListDialog.this.saveLastPosition(curChId, ChannelListDialog.this.mChannelAdapter.getChannellist());
                            }
                            if (message.arg1 == 166) {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_MTKIR_CHUP....end");
                            } else if (message.arg1 == 167) {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_MTKIR_CHDN....end");
                            } else if (message.arg1 == 229) {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_MTKIR_PRECH....end");
                            } else if (message.arg1 == 23) {
                                MtkLog.d(ChannelListDialog.TAG, "tts_channellistselect....end");
                            }
                            boolean unused12 = ChannelListDialog.this.mCanChangeChannel = true;
                            return;
                        case 4098:
                            ChannelListDialog.this.mThreadHandler.post(ChannelListDialog.this.mChannelUpRunnable);
                            return;
                        case 4099:
                            ChannelListDialog.this.mThreadHandler.post(ChannelListDialog.this.mChannelDownRunnable);
                            return;
                        case 4100:
                            ChannelListDialog.this.mThreadHandler.post(ChannelListDialog.this.mChannelPreRunnable);
                            return;
                        case 4101:
                            CommonIntegration.getInstance().selectChannelById(message.arg1);
                            return;
                        case 4102:
                            List<TIFChannelInfo> tempChlist = (List) message.obj;
                            boolean unused13 = ChannelListDialog.this.mCanChangeChannel = true;
                            ChannelListDialog.this.mChannelListView.setVisibility(0);
                            if (tempChlist == null) {
                                MtkLog.d(ChannelListDialog.TAG, "mChlist = null");
                                ChannelListDialog.this.mChannelListView.setAdapter((ListAdapter) null);
                            } else {
                                ChannelAdapter unused14 = ChannelListDialog.this.mChannelAdapter = new ChannelAdapter(ChannelListDialog.this.mContext, tempChlist);
                                int index = ChannelListDialog.this.mChannelAdapter.isExistCh(message.arg1);
                                int selection = index < 0 ? ChannelListDialog.this.mLastSelection : index;
                                if (selection > ChannelListDialog.this.mChannelAdapter.getCount() - 1) {
                                    selection = 0;
                                }
                                int unused15 = ChannelListDialog.this.mLastSelection = selection;
                                if (ChannelListDialog.this.mChannelAdapter != null) {
                                    ChannelListDialog.this.saveLastPosition(ChannelListDialog.this.commonIntegration.getCurrentChannelId(), ChannelListDialog.this.mChannelAdapter.getChannellist());
                                }
                                ChannelListDialog.this.mChannelListView.setAdapter(ChannelListDialog.this.mChannelAdapter);
                                MtkLog.d(ChannelListDialog.TAG, "mChlist = " + tempChlist.size());
                                ChannelListDialog.this.mChannelListView.setFocusable(true);
                                ChannelListDialog.this.mChannelListView.requestFocus();
                                if (index > -1) {
                                    ChannelListDialog.this.mChannelListView.setSelection(selection);
                                }
                                if (!TIFFunctionUtil.checkChMask(ChannelListDialog.this.commonIntegration.getCurChInfo(), CommonIntegration.CH_FAKE_MASK, CommonIntegration.CH_FAKE_VAL)) {
                                    ChannelListDialog.this.mChannelListView.setFocusable(true);
                                    ChannelListDialog.this.mChannelListView.requestFocus();
                                    ChannelListDialog.this.mChannelListView.setSelection(selection);
                                } else {
                                    MtkLog.d(ChannelListDialog.TAG, "initHandler current channel is fake,no focus in the channel list ,channel name is " + ChannelListDialog.this.commonIntegration.getCurChInfo().getServiceName());
                                }
                                if (ChannelListDialog.this.SELECT_TYPE_CHANGE_CH) {
                                    boolean unused16 = ChannelListDialog.this.SELECT_TYPE_CHANGE_CH = false;
                                    MtkLog.d(ChannelListDialog.TAG, "ChannelListOnKey : change type then change chanenls ");
                                    ChannelListDialog.this.mHandler.sendEmptyMessage(4114);
                                }
                            }
                            if (ChannelListDialog.this.chanelListProgressbar != null && ChannelListDialog.this.chanelListProgressbar.getVisibility() == 0) {
                                MtkLog.d(ChannelListDialog.TAG, " chanelListProgressbar.GONE; ");
                                ChannelListDialog.this.chanelListProgressbar.setVisibility(8);
                            }
                            ChannelListDialog.this.showPageUpDownView();
                            if (ChannelListDialog.this.commonIntegration.isDisableColorKey()) {
                                ChannelListDialog.this.mChannelListTipView.setVisibility(4);
                            } else {
                                ChannelListDialog.this.mChannelListTipView.setVisibility(0);
                            }
                            ChannelListDialog.this.mChannelDetailLayout.setVisibility(4);
                            ChannelListDialog.this.startTimeout(10000);
                            return;
                        case 4103:
                            MtkLog.d(ChannelListDialog.TAG, "TYPE_REGET_CHANNELLIST>isShowing()>>" + ChannelListDialog.this.isShowing() + ">>" + ChannelListDialog.this.mSelectionShow);
                            if (ChannelListDialog.this.isShowing() && !ChannelListDialog.this.mSelectionShow && ChannelListDialog.this.mIsTifFunction && ChannelListDialog.this.mChannelAdapter != null) {
                                ChannelListDialog.this.mThreadHandler.post(ChannelListDialog.this.mUpdateChannelListRunnable);
                                return;
                            }
                            return;
                        case 4104:
                            MtkLog.d(ChannelListDialog.TAG, "ChannelListDialog.MESSAGE_DEFAULT_CAN_CHANGECHANNEL_DELAY reach");
                            boolean unused17 = ChannelListDialog.this.mCanChangeChannel = true;
                            return;
                        case 4105:
                            List<TIFChannelInfo> tempUpdateChlist = (List) message.obj;
                            if (!(tempUpdateChlist == null || ChannelListDialog.this.mChannelAdapter == null)) {
                                ChannelListDialog.this.mChannelAdapter.updateData(tempUpdateChlist);
                                ChannelListDialog.this.showPageUpDownView();
                                if (ChannelListDialog.this.mChannelListTipView.getVisibility() != 0) {
                                    ChannelListDialog.this.mChannelListTipView.setVisibility(0);
                                }
                                if (ChannelListDialog.this.mChannelDetailLayout.getVisibility() != 4) {
                                    ChannelListDialog.this.mChannelDetailLayout.setVisibility(4);
                                }
                            }
                            if (ChannelListDialog.this.chanelListProgressbar != null && ChannelListDialog.this.chanelListProgressbar.getVisibility() == 0) {
                                MtkLog.d(ChannelListDialog.TAG, " chanelListProgressbar.GONE; ");
                                ChannelListDialog.this.chanelListProgressbar.setVisibility(8);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else {
                    MtkLog.i(ChannelListDialog.TAG, "data.parama1  = " + ((TvCallbackData) message.obj).param1);
                }
            }
        };
    }

    public ChannelListDialog(Context context) {
        this(context, R.style.nav_dialog);
        MtkLog.d(TAG, "Constructor!");
    }

    private void initTypes() {
        if (this.commonIntegration.isOperatorNTVPLUS() || this.commonIntegration.isOperatorTKGS()) {
            MtkLog.d(TAG, "initMaskAndSatellites isOperatorNTVPLUS or isOperatorTKGS");
            this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type_for_russia_for_ntv);
        } else if (this.commonIntegration.isOperatorTELEKARTA()) {
            MtkLog.d(TAG, "initMaskAndSatellites isOperatorTELEKARTA");
            this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type_for_russia_for_telekarta);
        } else {
            CommonIntegration commonIntegration2 = this.commonIntegration;
            if (!CommonIntegration.isEUPARegion()) {
                CommonIntegration commonIntegration3 = this.commonIntegration;
                if (!CommonIntegration.isCNRegion()) {
                    CommonIntegration commonIntegration4 = this.commonIntegration;
                    if (!CommonIntegration.isUSRegion()) {
                        CommonIntegration commonIntegration5 = this.commonIntegration;
                        if (!CommonIntegration.isSARegion()) {
                            this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type);
                        }
                    }
                    this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type_for_us);
                }
            }
            this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type_for_pa);
        }
        CHANGE_TYPE_NETWORK_INDEX = this.types.length - 1;
    }

    public int getChannelNetworkIndex() {
        initTypes();
        return CHANGE_TYPE_NETWORK_INDEX;
    }

    private void initMaskAndSatellites() {
        MtkLog.d(TAG, "initMaskAndSatellites ");
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 3:
                this.CURRENT_CHANNEL_MODE = 0;
                this.CURRENT_CHANNEL_SORT = this.mSaveValue.readValue(CH_SORT, 0);
                this.CURRENT_SELECT_MORE = this.mSaveValue.readValue(SEL_MORE, 0);
                this.CURRENT_CHANNEL_SECOND_TYPE = this.mSaveValue.readValue(CH_TYPE_SECOND, -1);
                initTypes();
                break;
            case 1:
            case 2:
                this.CURRENT_CHANNEL_MODE = 0;
                this.CURRENT_CHANNEL_SORT = this.mSaveValue.readValue(CH_SORT, 0);
                this.CURRENT_SELECT_MORE = this.mSaveValue.readValue(SEL_MORE, 0);
                this.CURRENT_CHANNEL_SECOND_TYPE = this.mSaveValue.readValue(CH_TYPE_SECOND, -1);
                this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type_for_us);
                break;
        }
        MtkLog.d(TAG, "isSelectionMode CURRENT_CHANNEL_MODE =" + this.CURRENT_CHANNEL_MODE);
        if (isSelectionMode()) {
            if (this.CURRENT_CHANNEL_SECOND_TYPE != -1) {
                CommonIntegration commonIntegration2 = this.commonIntegration;
                if (CommonIntegration.isEURegion()) {
                    resetCategories();
                    MtkLog.d(TAG, "CURRENT_CHANNEL_TYPE>>mCurrentSatelliteRecordId>" + this.mCurrentSatelliteRecordId);
                }
            }
            this.CURRENT_CHANNEL_SECOND_TYPE = 0;
            this.CURRENT_CHANNEL_TYPE = this.mSaveValue.readValue(CH_TYPE, 0);
            MtkLog.d(TAG, "CURRENT_CHANNEL_TYPE>>>" + this.CURRENT_CHANNEL_TYPE);
            resetMask();
            MtkLog.d(TAG, "CURRENT_CHANNEL_TYPE>>mCurrentSatelliteRecordId>" + this.mCurrentSatelliteRecordId);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        setContentView(R.layout.nav_channellist);
        setWindowPosition();
        findViews();
        if (CommonIntegration.isUSRegion()) {
            loadChannelTypeResForUS();
        } else {
            loadSelectMoreRes();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        MtkLog.d(TAG, "onStart");
        this.mChannelDetailTileText.setText(this.mContext.getResources().getString(R.string.nav_channel_details));
        this.mChannelListView.setVisibility(0);
        this.mChannelTypeView.setVisibility(8);
        this.mChannelSatelliteView.setVisibility(8);
        this.mChannelSatelliteSecondView.setVisibility(8);
        startTimeout(10000);
        super.onStart();
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777218 || componentID != 16777235) {
            return false;
        }
        return true;
    }

    public class TIFChannelContentObserver extends ContentObserver {
        public TIFChannelContentObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MtkLog.d(ChannelListDialog.TAG, "TIFChannelContentObserver onChange>selfChange>>" + selfChange);
            if (ChannelListDialog.this.mIsTifFunction && ChannelListDialog.this.isShowing()) {
                ChannelListDialog.this.handleCallBack();
            }
        }
    }

    private int indexOfSatReId(int recId) {
        int index = -1;
        if (this.mSatelliteListinfo == null) {
            return -1;
        }
        for (MtkTvDvbsConfigInfoBase configInfo : this.mSatelliteListinfo) {
            index++;
            if (recId == configInfo.getSatlRecId()) {
                return index;
            }
        }
        return -1;
    }

    private void resetSatellites() {
        this.mCurrentSatelliteRecordId = this.mSaveValue.readValue(SATELLITE_RECORDID, 0);
        MtkLog.d(TAG, "mCurrentSatelliteRecordId>updateSatelliteList>>" + this.mCurrentSatelliteRecordId);
        this.mSatelliteListinfo = this.commonIntegration.getSatelliteListInfo(this.commonIntegration.getSatelliteCount());
        this.mSatelliteListinfo.add(0, this.commonIntegration.getDefaultSatellite("All"));
        this.mSatelliteRecords = this.commonIntegration.getSatelliteNames(this.mSatelliteListinfo);
        if (indexOfSatReId(this.mCurrentSatelliteRecordId) == -1) {
            this.mCurrentSatelliteRecordId = 0;
            this.mSaveValue.saveValue(SATELLITE_RECORDID, this.mCurrentSatelliteRecordId);
        }
    }

    public void updateSatelliteList() {
        resetSatellites();
        ArrayList<HashMap<String, String>> recordList = new ArrayList<>();
        for (String type : this.mSatelliteRecords) {
            HashMap<String, String> tmpType = new HashMap<>();
            tmpType.put(SATELLITE_RECORDID, type);
            recordList.add(tmpType);
        }
        this.mChannelSatelliteView.setAdapter(new SimpleAdapter(this.mContext, recordList, R.layout.nav_channel_type_item, new String[]{SATELLITE_RECORDID}, new int[]{R.id.nav_channel_type_list_item}));
        this.mChannelSatelliteView.setOnKeyListener(new ChannelListOnKey());
    }

    public void handleCallBack() {
        MtkLog.d(TAG, "handleCallBack()");
        if (!this.mSelectionShow) {
            this.mHandler.removeMessages(4103);
            this.mHandler.sendEmptyMessageDelayed(4103, 1500);
        }
    }

    public void handleUpdateCallBack() {
        MtkLog.d(TAG, "handleUpdateCallBack()");
        this.mChannelListView.invalidateViews();
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x019d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean channelUpDown(boolean r13) {
        /*
            r12 = this;
            java.lang.String r0 = "ChannelListDialog"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "channelUpDown isUp = "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = r0.isCurrentSourceATVforEuPA()
            if (r0 != 0) goto L_0x0033
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r0 == 0) goto L_0x002f
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = r0.isCurrentSourceATV()
            if (r0 == 0) goto L_0x002f
            goto L_0x0033
        L_0x002f:
            r12.initMaskAndSatellites()
            goto L_0x0061
        L_0x0033:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            int r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.CH_LIST_ANALOG_MASK
            r12.mCurMask = r0
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            int r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.CH_LIST_ANALOG_VAL
            r12.mCurVal = r0
            java.lang.String r0 = "ChannelListDialog"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "channelUpDown mCurMask :"
            r1.append(r2)
            int r2 = r12.mCurMask
            r1.append(r2)
            java.lang.String r2 = " ,mCurVal :"
            r1.append(r2)
            int r2 = r12.mCurVal
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
        L_0x0061:
            r0 = 1
            r1 = 32
            r2 = 0
            r3 = 5
            if (r13 == 0) goto L_0x019d
            boolean r4 = r12.mIsTifFunction
            if (r4 == 0) goto L_0x0169
            java.lang.String r4 = "ChannelListDialog"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getCurrentFocus = "
            r5.append(r6)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r12.commonIntegration
            java.lang.String r6 = r6.getCurrentFocus()
            java.lang.String r7 = "sub"
            boolean r6 = r6.equalsIgnoreCase(r7)
            r5.append(r6)
            java.lang.String r6 = "  isCurrentSourceTv = "
            r5.append(r6)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r12.commonIntegration
            boolean r6 = r6.isCurrentSourceTv()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            java.lang.String r4 = r4.getCurrentFocus()
            java.lang.String r5 = "sub"
            boolean r4 = r4.equalsIgnoreCase(r5)
            if (r4 == 0) goto L_0x013b
            boolean r1 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r1)
            if (r1 == 0) goto L_0x013b
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r12.commonIntegration
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 == 0) goto L_0x013b
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r12.commonIntegration
            int r1 = r1.get2NDCurrentChannelId()
            r11 = 0
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            boolean r4 = r4.isGeneralSatMode()
            if (r4 == 0) goto L_0x00df
            int r4 = r12.mCurrentSatelliteRecordId
            if (r4 <= 0) goto L_0x00df
            int r4 = r12.CURRENT_CHANNEL_TYPE
            if (r4 == r3) goto L_0x00df
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            r6 = 2
            r7 = 1
            int r8 = r12.mCurMask
            int r9 = r12.mCurVal
            int r10 = r12.mCurrentSatelliteRecordId
            r5 = r1
            java.util.List r3 = r4.getChannelListByMaskAndSat(r5, r6, r7, r8, r9, r10)
            goto L_0x00ec
        L_0x00df:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            r6 = 2
            r7 = 1
            int r8 = r12.mCurMask
            int r9 = r12.mCurVal
            r5 = r1
            java.util.List r3 = r4.getChListByMask(r5, r6, r7, r8, r9)
        L_0x00ec:
            if (r3 == 0) goto L_0x013a
            int r4 = r3.size()
            if (r4 != 0) goto L_0x00f5
            goto L_0x013a
        L_0x00f5:
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView r4 = r4.getPipView()
            com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager r5 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getInstance()
            java.lang.String r6 = "sub"
            android.media.tv.TvInputInfo r5 = r5.getTvInputInfo(r6)
            java.lang.String r5 = r5.getId()
            java.lang.Object r2 = r3.get(r2)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r2 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r2
            int r2 = r2.getChannelId()
            long r6 = (long) r2
            android.net.Uri r2 = com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase.createSvlChannelUri(r6)
            r4.tune(r5, r2)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r12.commonIntegration
            int r2 = r2.get2NDCurrentChannelId()
            java.lang.String r4 = "ChannelListDialog"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "channelUp channelid = "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            return r0
        L_0x013a:
            return r2
        L_0x013b:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = r0.isGeneralSatMode()
            if (r0 == 0) goto L_0x015e
            int r0 = r12.mCurrentSatelliteRecordId
            if (r0 <= 0) goto L_0x015e
            int r0 = r12.CURRENT_CHANNEL_TYPE
            if (r0 == r3) goto L_0x015e
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r0 = r12.mTIFChannelManager
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            int r3 = r12.mCurrentSatelliteRecordId
            boolean r0 = r0.channelUpDownByMaskAndSat(r1, r2, r3, r13)
            return r0
        L_0x015e:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r0 = r12.mTIFChannelManager
            int r1 = r12.mCurMask
            int r2 = r12.mCurVal
            boolean r0 = r0.channelUpDownByMask(r13, r1, r2)
            return r0
        L_0x0169:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = r0.isGeneralSatMode()
            if (r0 == 0) goto L_0x018c
            int r0 = r12.mCurrentSatelliteRecordId
            if (r0 <= 0) goto L_0x018c
            int r0 = r12.CURRENT_CHANNEL_TYPE
            if (r0 == r3) goto L_0x018c
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            int r3 = r12.mCurrentSatelliteRecordId
            boolean r0 = r0.channelUpDownByMaskAndSat(r1, r2, r3, r13)
            return r0
        L_0x018c:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            boolean r0 = r0.channelUpDownByMask(r13, r1, r2)
            return r0
        L_0x019d:
            boolean r4 = r12.mIsTifFunction
            if (r4 == 0) goto L_0x02a4
            java.lang.String r4 = "ChannelListDialog"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getCurrentFocus = "
            r5.append(r6)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r12.commonIntegration
            java.lang.String r6 = r6.getCurrentFocus()
            java.lang.String r7 = "sub"
            boolean r6 = r6.equalsIgnoreCase(r7)
            r5.append(r6)
            java.lang.String r6 = "  isCurrentSourceTv = "
            r5.append(r6)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = r12.commonIntegration
            boolean r6 = r6.isCurrentSourceTv()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            java.lang.String r4 = r4.getCurrentFocus()
            java.lang.String r5 = "sub"
            boolean r4 = r4.equalsIgnoreCase(r5)
            if (r4 == 0) goto L_0x0270
            boolean r1 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r1)
            if (r1 == 0) goto L_0x0270
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r12.commonIntegration
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 == 0) goto L_0x0270
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r12.commonIntegration
            int r1 = r1.get2NDCurrentChannelId()
            r11 = 0
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            boolean r4 = r4.isGeneralSatMode()
            if (r4 == 0) goto L_0x0214
            int r4 = r12.mCurrentSatelliteRecordId
            if (r4 <= 0) goto L_0x0214
            int r4 = r12.CURRENT_CHANNEL_TYPE
            if (r4 == r3) goto L_0x0214
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            r6 = 3
            r7 = 1
            int r8 = r12.mCurMask
            int r9 = r12.mCurVal
            int r10 = r12.mCurrentSatelliteRecordId
            r5 = r1
            java.util.List r3 = r4.getChannelListByMaskAndSat(r5, r6, r7, r8, r9, r10)
            goto L_0x0221
        L_0x0214:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r12.commonIntegration
            r6 = 3
            r7 = 1
            int r8 = r12.mCurMask
            int r9 = r12.mCurVal
            r5 = r1
            java.util.List r3 = r4.getChListByMask(r5, r6, r7, r8, r9)
        L_0x0221:
            if (r3 == 0) goto L_0x026f
            int r4 = r3.size()
            if (r4 != 0) goto L_0x022a
            goto L_0x026f
        L_0x022a:
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView r4 = r4.getPipView()
            com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager r5 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getInstance()
            java.lang.String r6 = "sub"
            android.media.tv.TvInputInfo r5 = r5.getTvInputInfo(r6)
            java.lang.String r5 = r5.getId()
            java.lang.Object r2 = r3.get(r2)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r2 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r2
            int r2 = r2.getChannelId()
            long r6 = (long) r2
            android.net.Uri r2 = com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase.createSvlChannelUri(r6)
            r4.tune(r5, r2)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r12.commonIntegration
            int r2 = r2.get2NDCurrentChannelId()
            java.lang.String r4 = "ChannelListDialog"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "channelUp channelid = "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            return r0
        L_0x026f:
            return r2
        L_0x0270:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = r0.isGeneralSatMode()
            if (r0 == 0) goto L_0x0293
            int r0 = r12.mCurrentSatelliteRecordId
            if (r0 <= 0) goto L_0x0293
            int r0 = r12.CURRENT_CHANNEL_TYPE
            if (r0 == r3) goto L_0x0293
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r0 = r12.mTIFChannelManager
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            int r3 = r12.mCurrentSatelliteRecordId
            boolean r0 = r0.channelUpDownByMaskAndSat(r1, r2, r3, r13)
            return r0
        L_0x0293:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r0 = r12.mTIFChannelManager
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            boolean r0 = r0.channelUpDownByMask(r13, r1, r2)
            return r0
        L_0x02a4:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            boolean r0 = r0.isGeneralSatMode()
            if (r0 == 0) goto L_0x02c7
            int r0 = r12.mCurrentSatelliteRecordId
            if (r0 <= 0) goto L_0x02c7
            int r0 = r12.CURRENT_CHANNEL_TYPE
            if (r0 == r3) goto L_0x02c7
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            int r3 = r12.mCurrentSatelliteRecordId
            boolean r0 = r0.channelUpDownByMaskAndSat(r1, r2, r3, r13)
            return r0
        L_0x02c7:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r12.commonIntegration
            int r1 = r12.mCurMask
            int r2 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r1 = r1 | r2
            int r2 = r12.mCurVal
            int r3 = com.mediatek.twoworlds.tv.common.MtkTvChCommonBase.SB_VNET_VISIBLE
            r2 = r2 | r3
            boolean r0 = r0.channelUpDownByMask(r13, r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog.channelUpDown(boolean):boolean");
    }

    /* access modifiers changed from: private */
    public void resetMask() {
        int TMP_CURRENT_CHANNEL_TYPE;
        int mask = CommonIntegration.CH_LIST_MASK;
        int val = CommonIntegration.CH_LIST_VAL;
        MtkLog.d(TAG, "resetChList CURRENT_CHANNEL_TYPE = " + this.CURRENT_CHANNEL_TYPE + "CURRENT_CHANNEL_MODE =" + this.CURRENT_CHANNEL_MODE);
        if ((this.commonIntegration.isOperatorNTVPLUS() || this.commonIntegration.isOperatorTKGS()) && this.CURRENT_CHANNEL_TYPE > 1) {
            TMP_CURRENT_CHANNEL_TYPE = this.CURRENT_CHANNEL_TYPE - 1;
        } else if (!this.commonIntegration.isOperatorTELEKARTA() || this.CURRENT_CHANNEL_TYPE <= 2) {
            TMP_CURRENT_CHANNEL_TYPE = this.CURRENT_CHANNEL_TYPE;
        } else {
            TMP_CURRENT_CHANNEL_TYPE = this.CURRENT_CHANNEL_TYPE - 2;
        }
        this.titlefavN = "";
        if (isSelectionMode()) {
            switch (TMP_CURRENT_CHANNEL_TYPE) {
                case 0:
                    mask = CommonIntegration.CH_LIST_MASK;
                    val = CommonIntegration.CH_LIST_VAL;
                    break;
                case 1:
                    mask = CommonIntegration.CH_LIST_DIGITAL_MASK;
                    val = CommonIntegration.CH_LIST_DIGITAL_VAL;
                    break;
                case 2:
                    mask = CommonIntegration.CH_LIST_RADIO_MASK;
                    val = CommonIntegration.CH_LIST_RADIO_VAL;
                    break;
                case 3:
                    mask = CommonIntegration.CH_LIST_FREE_MASK;
                    val = CommonIntegration.CH_LIST_FREE_VAL;
                    break;
                case 4:
                    mask = CommonIntegration.CH_LIST_SCRAMBLED_MASK;
                    val = CommonIntegration.CH_LIST_SCRAMBLED_VAL;
                    break;
                case 5:
                    mask = CommonIntegration.CH_LIST_ANALOG_MASK;
                    val = CommonIntegration.CH_LIST_ANALOG_VAL;
                    break;
            }
            if (this.types != null && this.types.length - 1 == this.CURRENT_CHANNEL_TYPE) {
                MtkLog.d(TAG, "resetMask types.length-1 = " + (this.types.length - 1));
                mask = -1;
                val = -1;
            } else if (this.types != null && this.types.length - 2 == this.CURRENT_CHANNEL_TYPE) {
                MtkLog.d(TAG, "resetMask types.length-2 favN");
                this.CURRENT_FAVOURITE_TYPE = this.mSaveValue.readValue(FAVOURITE_TYPE, 0);
                loadFavTypeRes();
                this.titlefavN = this.favTypes[this.CURRENT_FAVOURITE_TYPE];
                mask = CommonIntegration.favMask[this.CURRENT_FAVOURITE_TYPE];
                val = CommonIntegration.favMask[this.CURRENT_FAVOURITE_TYPE];
            }
        }
        this.mCurCategories = -1;
        TIFFunctionUtil.setmCurCategories(this.mCurCategories);
        this.mCurMask = mask;
        this.mCurVal = val;
        this.mSaveValue.saveValue(CommonIntegration.channelListfortypeMask, this.mCurMask);
        this.mSaveValue.saveValue(CommonIntegration.channelListfortypeMaskvalue, this.mCurVal);
        MtkLog.d(TAG, "resetMask mCurCategories =" + this.mCurCategories);
        Log.d(TAG, "resetMask mCurMask =" + this.mCurMask + " mCurVal: " + this.mCurVal);
    }

    /* access modifiers changed from: private */
    public void resetCategories() {
        MtkLog.d(TAG, "resetCategories CURRENT_CHANNEL_SECOND_TYPE = " + this.CURRENT_CHANNEL_SECOND_TYPE);
        MtkLog.d(TAG, "resetCategories mCurCategories =" + this.mCurCategories);
        this.CURRENT_CHANNEL_TYPE = 0;
        resetMask();
        MtkLog.d(TAG, "resetCategories mCurCategories =" + this.mCurCategories);
        if (this.typesSecond != null && this.typesSecond.size() > this.CURRENT_CHANNEL_SECOND_TYPE && this.typesSecond.get(this.CURRENT_CHANNEL_SECOND_TYPE).equalsIgnoreCase(categoriesOther)) {
            this.mCurCategories = 0;
        } else if (this.mChannelTypePacksOrCategory != null && this.mChannelTypePacksOrCategory.size() > this.CURRENT_CHANNEL_SECOND_TYPE) {
            this.mCurCategories = 1 << this.mChannelTypePacksOrCategory.get(this.CURRENT_CHANNEL_SECOND_TYPE).intValue();
        }
        TIFFunctionUtil.setmCurCategories(this.mCurCategories);
        MtkLog.d(TAG, "resetCategories mCurCategories =" + this.mCurCategories);
    }

    /* access modifiers changed from: private */
    public boolean isSelectionMode() {
        return this.CURRENT_CHANNEL_MODE == 0;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        boolean isHandled = true;
        if (this.mContext == null || !this.mCanChangeChannel) {
            return false;
        }
        MtkLog.d(TAG, "KeyHandler keyCode 111= " + keyCode);
        int keyCode2 = KeyMap.getKeyCode(keyCode, event);
        startTimeout(10000);
        MtkLog.d(TAG, "KeyHandler keyCode 222= " + keyCode2);
        if (keyCode2 != 4) {
            if (keyCode2 == 93) {
                MtkLog.d(TAG, "KEYCODE_PAGE_DOWN!!!!!");
            } else if (keyCode2 != 130) {
                if (keyCode2 != 229) {
                    if (keyCode2 != 10467) {
                        switch (keyCode2) {
                            case 85:
                                break;
                            case 86:
                                this.mSendKeyCode = 86;
                                isHandled = false;
                                break;
                            default:
                                switch (keyCode2) {
                                    case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                                        if (this.mCanChangeChannel) {
                                            this.mHandler.removeMessages(4104);
                                            this.mHandler.sendEmptyMessageDelayed(4104, MessageType.delayForTKToMenu);
                                            this.preChId = this.commonIntegration.getCurrentChannelId();
                                            MtkLog.d(TAG, "KEYCODE_MTKIR_CHUP....start");
                                            this.mThreadHandler.post(this.mChannelUpRunnable);
                                            break;
                                        }
                                        break;
                                    case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                                        if (this.mCanChangeChannel) {
                                            this.mHandler.removeMessages(4104);
                                            this.mHandler.sendEmptyMessageDelayed(4104, MessageType.delayForTKToMenu);
                                            MtkLog.d(TAG, "KEYCODE_MTKIR_CHDN....start");
                                            this.mThreadHandler.post(this.mChannelDownRunnable);
                                            break;
                                        }
                                        break;
                                    default:
                                        switch (keyCode2) {
                                            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                                MtkLog.d(TAG, "KEYCODE_MTKIR_RED");
                                                if (!this.commonIntegration.isDisableColorKey()) {
                                                    if (this.mChannelListPageUpDownLayout.getVisibility() == 0) {
                                                        if (this.mChannelListView.getSelectedItemPosition() == 0) {
                                                            if (this.hasNextPage) {
                                                                this.mChannelDetailLayout.setVisibility(4);
                                                                if (this.mIsTifFunction) {
                                                                    this.mChannelAdapter.updateData(getNextPrePageChListByTIF(false));
                                                                } else {
                                                                    this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(getNextPrePageChList(false)));
                                                                }
                                                                this.mChannelListView.requestFocus();
                                                                this.mChannelListView.setSelection(0);
                                                                break;
                                                            }
                                                        } else {
                                                            this.mChannelListView.requestFocus();
                                                            this.mChannelListView.setSelection(0);
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    MtkLog.d(TAG, "isDisableColorKey is not deal");
                                                    return false;
                                                }
                                                break;
                                            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                                MtkLog.d(TAG, "KEYCODE_MTKIR_GREEN");
                                                if (!this.commonIntegration.isDisableColorKey()) {
                                                    if (this.mChannelListPageUpDownLayout.getVisibility() == 0) {
                                                        if (this.mChannelListView.getSelectedItemPosition() == this.mChannelAdapter.getCount() - 1) {
                                                            if (this.hasNextPage) {
                                                                this.mChannelDetailLayout.setVisibility(4);
                                                                if (this.mIsTifFunction) {
                                                                    this.mChannelAdapter.updateData(getNextPrePageChListByTIF(true));
                                                                } else {
                                                                    this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(getNextPrePageChList(true)));
                                                                }
                                                                this.mChannelListView.requestFocus();
                                                                this.mChannelListView.setSelection(this.mChannelAdapter.getCount() - 1);
                                                                break;
                                                            }
                                                        } else {
                                                            this.mChannelListView.requestFocus();
                                                            this.mChannelListView.setSelection(this.mChannelAdapter.getCount() - 1);
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    MtkLog.d(TAG, "isDisableColorKey is not deal");
                                                    return false;
                                                }
                                                break;
                                            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                                if (!this.commonIntegration.isDisableColorKey()) {
                                                    if (!isSelectionMode()) {
                                                        MtkLog.d(TAG, "KEYCODE_MTKIR_YELLOW PA ATV ");
                                                        this.mChannelListView.dispatchKeyEvent(new KeyEvent(0, 23));
                                                        break;
                                                    } else {
                                                        if ((!CommonIntegration.isEURegion() || TIFFunctionUtil.isEUPARegion()) && !CommonIntegration.isSARegion()) {
                                                            CommonIntegration commonIntegration2 = this.commonIntegration;
                                                            if (!CommonIntegration.isEUPARegion() || !this.commonIntegration.isCurrentSourceDTV()) {
                                                                if (!CommonIntegration.isUSRegion()) {
                                                                    if (CommonIntegration.isCNRegion()) {
                                                                        this.mChannelListView.dispatchKeyEvent(new KeyEvent(0, 23));
                                                                        break;
                                                                    }
                                                                } else {
                                                                    MtkLog.d(TAG, "KEYCODE_MTKIR_YELLOW eu us ");
                                                                    this.mChannelListView.setVisibility(8);
                                                                    this.mChannelTypeView.setVisibility(0);
                                                                    this.mChannelListTipView.setVisibility(8);
                                                                    this.mChannelTypeView.setFocusable(true);
                                                                    this.mChannelTypeView.requestFocus();
                                                                    this.mChannelSatelliteSecondView.setVisibility(8);
                                                                    this.mHandler.removeMessages(4112);
                                                                    Message msg = Message.obtain();
                                                                    msg.what = 4112;
                                                                    msg.arg1 = R.id.nav_channel_typeview;
                                                                    msg.arg2 = this.CURRENT_CHANNEL_TYPE;
                                                                    this.mHandler.sendMessageDelayed(msg, 90);
                                                                    if (this.mChannelDetailLayout.getVisibility() == 0) {
                                                                        this.mChannelDetailLayout.setVisibility(4);
                                                                    }
                                                                    TextView textView = this.mTitleText;
                                                                    textView.setText(this.mContext.getResources().getString(R.string.nav_channel_list) + " - ");
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        MtkLog.d(TAG, "KEYCODE_MTKIR_YELLOW eu sa ");
                                                        loadSelectMoreRes();
                                                        this.mSelectionShow = true;
                                                        this.mChannelListView.setVisibility(8);
                                                        this.mChannelSelectMoreView.setVisibility(0);
                                                        this.mChannelListTipView.setVisibility(8);
                                                        this.mChannelSelectMoreView.setFocusable(true);
                                                        this.mChannelSelectMoreView.requestFocus();
                                                        this.mChannelSatelliteSecondView.setVisibility(8);
                                                        this.mHandler.removeMessages(4112);
                                                        Message msg2 = Message.obtain();
                                                        msg2.what = 4112;
                                                        msg2.arg1 = R.id.nav_channel_select_more;
                                                        msg2.arg2 = this.CURRENT_SELECT_MORE;
                                                        this.mHandler.sendMessageDelayed(msg2, 90);
                                                        if (this.mChannelDetailLayout.getVisibility() == 0) {
                                                            this.mChannelDetailLayout.setVisibility(4);
                                                        }
                                                        this.mTitleText.setText(this.mContext.getResources().getString(R.string.nav_select_more));
                                                        break;
                                                    }
                                                } else {
                                                    MtkLog.d(TAG, "isDisableColorKey is not deal");
                                                    return false;
                                                }
                                                break;
                                            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                                if (this.commonIntegration.isDisableColorKey()) {
                                                    MtkLog.d(TAG, "isDisableColorKey is not deal");
                                                    return false;
                                                } else if (this.mBlueKeyText.getVisibility() == 0) {
                                                    exit();
                                                    if (TurnkeyUiMainActivity.getInstance() != null) {
                                                        MtkLog.d(TAG, "TurnkeyUiMainActivity blue to favlist");
                                                        return TurnkeyUiMainActivity.getInstance().KeyHandler(217, event);
                                                    }
                                                }
                                                break;
                                            default:
                                                isHandled = false;
                                                break;
                                        }
                                }
                        }
                    }
                } else if (this.commonIntegration.is3rdTVSource()) {
                    return false;
                } else {
                    if (this.mCanChangeChannel) {
                        this.mHandler.removeMessages(4104);
                        this.mHandler.sendEmptyMessageDelayed(4104, MessageType.delayForTKToMenu);
                        MtkLog.d(TAG, "KEYCODE_MTKIR_PRECH....start");
                        this.mThreadHandler.post(this.mChannelPreRunnable);
                    }
                }
            }
            dismiss();
            isHandled = false;
        } else {
            exit();
        }
        if (isHandled || TurnkeyUiMainActivity.getInstance() == null) {
            return isHandled;
        }
        MtkLog.d(TAG, "TurnkeyUiMainActivity");
        return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode2, event);
    }

    public boolean channelUpDownNoVisible(boolean isUp) {
        MtkLog.d(TAG, "channelUpDown isUp = " + isUp);
        if (this.mIsTifFunction) {
            if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0 || this.CURRENT_CHANNEL_TYPE == 5) {
                return this.mTIFChannelManager.channelUpDownByMask(isUp, this.mCurMask, this.mCurVal);
            }
            return this.mTIFChannelManager.channelUpDownByMaskAndSat(this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId, isUp);
        } else if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0 || this.CURRENT_CHANNEL_TYPE == 5) {
            return this.commonIntegration.channelUpDownByMask(isUp, this.mCurMask, this.mCurVal);
        } else {
            return this.commonIntegration.channelUpDownByMaskAndSat(this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId, isUp);
        }
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        return KeyHandler(keyCode, event, false);
    }

    private String[] addSuffix(String[] strtemp, int strId) {
        if (this.mContext == null || strtemp.length == 0) {
            return null;
        }
        String[] str = new String[strtemp.length];
        for (int i = 0; i < strtemp.length; i++) {
            String numStr = strtemp[i];
            int num = (numStr == null || numStr.isEmpty()) ? Integer.MAX_VALUE : Integer.parseInt(numStr);
            if (num != Integer.MAX_VALUE) {
                str[i] = this.mContext.getResources().getString(strId, new Object[]{Integer.valueOf(num)});
            }
        }
        return str;
    }

    private void loadFavTypeRes() {
        this.favTypes = addSuffix(this.mContext.getResources().getStringArray(R.array.nav_favourite_type), R.string.str);
    }

    /* access modifiers changed from: private */
    public void loadChannelTypeRes() {
        MtkLog.d(TAG, "loadChannelTypeRes CURRENT_CHANNEL_TYPE" + this.CURRENT_CHANNEL_TYPE);
        if (isSelectionMode()) {
            this.mTitlePre = this.mContext.getResources().getString(R.string.nav_channel_list) + " - ";
            ArrayList<HashMap<String, String>> typeList = new ArrayList<>();
            for (String type : this.types) {
                HashMap<String, String> tmpType = new HashMap<>();
                tmpType.put(CH_TYPE, type);
                typeList.add(tmpType);
            }
            this.mChannelTypeView.setAdapter(new SimpleAdapter(this.mContext, typeList, R.layout.nav_channel_type_item, new String[]{CH_TYPE}, new int[]{R.id.nav_channel_type_list_item}));
            this.mChannelTypeView.setOnKeyListener(new ChannelListOnKey());
            this.mChannelTypeView.setVisibility(0);
            this.mChannelTypeView.setFocusable(true);
            this.mChannelTypeView.requestFocus();
            this.mHandler.removeMessages(4112);
            Message msg = Message.obtain();
            msg.what = 4112;
            msg.arg1 = R.id.nav_channel_typeview;
            msg.arg2 = this.CURRENT_CHANNEL_TYPE;
            this.mHandler.sendMessageDelayed(msg, 90);
        }
    }

    private void loadChannelTypeResForUS() {
        MtkLog.d(TAG, "loadChannelTypeRes CURRENT_CHANNEL_TYPE" + this.CURRENT_CHANNEL_TYPE);
        if (isSelectionMode()) {
            this.types = this.mContext.getResources().getStringArray(R.array.nav_channel_type_for_us);
            this.mTitlePre = this.mContext.getResources().getString(R.string.nav_channel_list) + " - ";
            ArrayList<HashMap<String, String>> typeList = new ArrayList<>();
            for (String type : this.types) {
                HashMap<String, String> tmpType = new HashMap<>();
                tmpType.put(CH_TYPE, type);
                typeList.add(tmpType);
            }
            this.mChannelTypeView.setAdapter(new SimpleAdapter(this.mContext, typeList, R.layout.nav_channel_type_item, new String[]{CH_TYPE}, new int[]{R.id.nav_channel_type_list_item}));
            this.mChannelTypeView.setOnKeyListener(new ChannelListOnKey());
        }
    }

    /* access modifiers changed from: private */
    public void loadChannelListTypeSecondeView() {
        ArrayList<HashMap<String, String>> typeList;
        MtkLog.d(TAG, "loadChannelListTypeSecondeView CURRENT_CHANNEL_TYPE" + this.CURRENT_CHANNEL_TYPE);
        this.mChannelTypePacksOrCategory.clear();
        MtkLog.d(TAG, "loadChannelListTypeSecondeView mChannelTypePacksOrCategory size " + this.mChannelTypePacksOrCategory.size());
        if (this.commonIntegration.isOperatorTELEKARTA()) {
            MtkLog.d(TAG, "loadChannelListTypeSecondeView isOperatorTELEKARTA = " + this.commonIntegration.isOperatorTELEKARTA());
            int categoriesnum = this.commonIntegration.dvbsGetCategoryNum();
            MtkLog.d(TAG, "loadChannelListTypeSecondeView categoriesnum" + categoriesnum);
            this.typesSecond = new ArrayList();
            String string = this.mContext.getResources().getString(R.string.nav_channel_list_title_categories);
            typeList = new ArrayList<>();
            for (int i = 0; i < categoriesnum; i++) {
                String[] categoriesname = this.commonIntegration.dvbsGetCategoryInfoByIdx(i).split("_");
                MtkLog.d(TAG, "loadChannelListTypeSecondeView true categoriesname" + categoriesname[0] + "categoriesname id =" + categoriesname[1]);
                if (this.CURRENT_CHANNEL_TYPE == 2) {
                    if (Integer.parseInt(categoriesname[1]) >= CATEGORIES_BASE) {
                        HashMap<String, String> tmpType = new HashMap<>();
                        tmpType.put(CH_TYPE_SECOND, categoriesname[0]);
                        this.mChannelTypePacksOrCategory.add(Integer.valueOf(i));
                        this.typesSecond.add(categoriesname[0]);
                        typeList.add(tmpType);
                    }
                } else if (this.CURRENT_CHANNEL_TYPE == 1 && Integer.parseInt(categoriesname[1]) >= PACKS_BASE && Integer.parseInt(categoriesname[1]) < CATEGORIES_BASE) {
                    HashMap<String, String> tmpType2 = new HashMap<>();
                    tmpType2.put(CH_TYPE_SECOND, categoriesname[0]);
                    this.mChannelTypePacksOrCategory.add(Integer.valueOf(i));
                    this.typesSecond.add(categoriesname[0]);
                    typeList.add(tmpType2);
                }
            }
            MtkLog.d(TAG, "loadChannelListTypeSecondeView types.length == 5 typeList.size " + typeList.size());
        } else if (this.commonIntegration.isOperatorNTVPLUS() || this.commonIntegration.isOperatorTKGS()) {
            MtkLog.d(TAG, "loadChannelListTypeSecondeView isOperatorNTVPLUS or isOperatorTKGS");
            int categoriesnum2 = this.commonIntegration.dvbsGetCategoryNum();
            this.typesSecond = new ArrayList();
            String string2 = this.mContext.getResources().getString(R.string.nav_channel_list_title_categories);
            typeList = new ArrayList<>();
            for (int i2 = 0; i2 < categoriesnum2; i2++) {
                HashMap<String, String> tmpType3 = new HashMap<>();
                String[] categoriesname2 = this.commonIntegration.dvbsGetCategoryInfoByIdx(i2).split("_");
                MtkLog.d(TAG, "loadChannelListTypeSecondeView false categoriesname" + categoriesname2[0]);
                tmpType3.put(CH_TYPE_SECOND, categoriesname2[0]);
                this.mChannelTypePacksOrCategory.add(Integer.valueOf(i2));
                this.typesSecond.add(categoriesname2[0]);
                typeList.add(tmpType3);
            }
            if (this.commonIntegration.isOperatorNTVPLUS()) {
                List<TIFChannelInfo> tifChaList = this.mTIFChannelManager.queryChanelListAll(TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
                String channelNum = tifChaList.get(tifChaList.size() - 1).mDisplayNumber;
                String[] chaNum = channelNum.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
                MtkLog.d(TAG, "loadChannelListTypeSecondeView channelNum = " + channelNum);
                if (Integer.parseInt(chaNum[0]) == 2000 || Integer.parseInt(chaNum[0]) > 2000) {
                    HashMap<String, String> tmpType4 = new HashMap<>();
                    MtkLog.d(TAG, "loadChannelListTypeSecondeView false categoriesOtherOther");
                    tmpType4.put(CH_TYPE_SECOND, categoriesOther);
                    this.mChannelTypePacksOrCategory.add(Integer.valueOf(categoriesnum2));
                    this.typesSecond.add(categoriesOther);
                    typeList.add(tmpType4);
                }
            }
            MtkLog.d(TAG, "loadChannelListTypeSecondeView types.length == 4 typeList.size " + typeList.size());
        } else {
            return;
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.mContext, typeList, R.layout.nav_channel_type_item, new String[]{CH_TYPE_SECOND}, new int[]{R.id.nav_channel_type_list_item});
        this.mChannelTypeView.setAdapter(simpleAdapter);
        this.mChannelTypeView.setVisibility(8);
        this.mChannelSatelliteSecondView.setVisibility(0);
        this.mChannelSatelliteSecondView.setAdapter(simpleAdapter);
        this.mChannelSatelliteSecondView.setOnKeyListener(new ChannelListOnKey());
        this.mChannelSatelliteSecondView.setFocusable(true);
        this.mChannelSatelliteSecondView.requestFocus();
        this.mChannelSatelliteSecondView.setSelection(this.CURRENT_CHANNEL_SECOND_TYPE);
    }

    private void loadSelectMoreRes() {
        MtkLog.d(TAG, "loadSelectMoreRes ");
        if (isSelectionMode()) {
            MtkLog.d(TAG, "loadSelectMoreRes isSelectionMode ture");
            this.moreItems = this.mContext.getResources().getStringArray(R.array.nav_channel_select_more);
            String string = this.mContext.getResources().getString(R.string.nav_select_more);
            ArrayList<HashMap<String, String>> typeList = new ArrayList<>();
            for (String type : this.moreItems) {
                HashMap<String, String> tmpType = new HashMap<>();
                tmpType.put(SEL_MORE, type);
                typeList.add(tmpType);
            }
            this.mChannelSelectMoreView.setAdapter(new SimpleAdapter(this.mContext, typeList, R.layout.nav_channel_type_item, new String[]{SEL_MORE}, new int[]{R.id.nav_channel_type_list_item}));
            this.mChannelSelectMoreView.setOnKeyListener(new ChannelListOnKey());
            this.mChannelSelectMoreView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MtkLog.d(ChannelListDialog.TAG, "loadSelectMoreRes parent " + parent + " ,view = " + view + ",position is " + position);
                    if (!TextToSpeechUtil.isTTSEnabled(ChannelListDialog.this.mContext)) {
                        ChannelListDialog.this.mTIFChannelManager.findChanelsForlist("", ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal);
                        int unused = ChannelListDialog.this.CURRENT_SELECT_MORE = position;
                        MtkLog.d(ChannelListDialog.TAG, "nav_channel_select_more>>> CURRENT_SELECT_MORE is " + ChannelListDialog.this.CURRENT_SELECT_MORE);
                        ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.SEL_MORE, ChannelListDialog.this.CURRENT_SELECT_MORE);
                        ChannelListDialog.this.mChannelSelectMoreView.setVisibility(8);
                        if (ChannelListDialog.this.CURRENT_SELECT_MORE == 0) {
                            MtkLog.d(ChannelListDialog.TAG, "nav_channel_select_more>>> loadChannelTypeRes() ");
                            ChannelListDialog.this.loadChannelTypeRes();
                            ChannelListDialog.this.mTitleText.setText(ChannelListDialog.this.mTitlePre);
                        } else if (ChannelListDialog.this.CURRENT_SELECT_MORE == 1) {
                            MtkLog.d(ChannelListDialog.TAG, "nav_channel_select_more>>> loadChannelSortRes() ");
                            ChannelListDialog.this.loadChannelSortRes();
                            ChannelListDialog.this.mTitleText.setText(ChannelListDialog.this.sortTitle);
                        } else if (ChannelListDialog.this.CURRENT_SELECT_MORE == 2) {
                            MtkLog.d(ChannelListDialog.TAG, "nav_channel_select_more>>> gotoSeacherTextAct() ");
                            ChannelListDialog.this.mChannelListView.setAdapter((ListAdapter) null);
                            ChannelListDialog.this.mChannelListView.setVisibility(0);
                            ChannelListDialog.this.gotoSeacherTextAct();
                        }
                        ChannelListDialog.this.startTimeout(10000);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x005a A[LOOP:0: B:11:0x0058->B:12:0x005a, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadChannelSortRes() {
        /*
            r10 = this;
            android.content.Context r0 = r10.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131692104(0x7f0f0a48, float:1.9013299E38)
            java.lang.String r0 = r0.getString(r1)
            r10.sortTitle = r0
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r10.commonIntegration
            boolean r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r0 == 0) goto L_0x001f
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r10.commonIntegration
            boolean r0 = r0.isCurrentSourceDTVforEuPA()
            if (r0 != 0) goto L_0x002f
        L_0x001f:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r10.commonIntegration
            boolean r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r0 == 0) goto L_0x003f
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = r10.commonIntegration
            boolean r0 = r0.isCurrentSourceDTV()
            if (r0 == 0) goto L_0x003f
        L_0x002f:
            android.content.Context r0 = r10.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903303(0x7f030107, float:1.741342E38)
            java.lang.String[] r0 = r0.getStringArray(r1)
            r10.sorts = r0
            goto L_0x004e
        L_0x003f:
            android.content.Context r0 = r10.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903302(0x7f030106, float:1.7413418E38)
            java.lang.String[] r0 = r0.getStringArray(r1)
            r10.sorts = r0
        L_0x004e:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String[] r1 = r10.sorts
            int r2 = r1.length
            r7 = 0
            r3 = r7
        L_0x0058:
            if (r3 >= r2) goto L_0x006c
            r4 = r1[r3]
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            java.lang.String r6 = "sort"
            r5.put(r6, r4)
            r0.add(r5)
            int r3 = r3 + 1
            goto L_0x0058
        L_0x006c:
            android.widget.SimpleAdapter r8 = new android.widget.SimpleAdapter
            android.content.Context r2 = r10.mContext
            r4 = 2131493072(0x7f0c00d0, float:1.8609614E38)
            java.lang.String r1 = "sort"
            java.lang.String[] r5 = new java.lang.String[]{r1}
            r9 = 1
            int[] r6 = new int[r9]
            r1 = 2131362508(0x7f0a02cc, float:1.8344799E38)
            r6[r7] = r1
            r1 = r8
            r3 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            android.widget.ListView r2 = r10.mChannelSortView
            r2.setAdapter(r1)
            android.widget.ListView r2 = r10.mChannelSortView
            com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog$ChannelListOnKey r3 = new com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog$ChannelListOnKey
            r3.<init>()
            r2.setOnKeyListener(r3)
            android.widget.ListView r2 = r10.mChannelSortView
            r2.setVisibility(r7)
            android.widget.ListView r2 = r10.mChannelSortView
            r2.setFocusable(r9)
            android.widget.ListView r2 = r10.mChannelSortView
            r2.requestFocus()
            android.os.Handler r2 = r10.mHandler
            r3 = 4112(0x1010, float:5.762E-42)
            r2.removeMessages(r3)
            android.os.Message r2 = android.os.Message.obtain()
            r2.what = r3
            r3 = 2131362502(0x7f0a02c6, float:1.8344786E38)
            r2.arg1 = r3
            int r3 = r10.CURRENT_CHANNEL_SORT
            r2.arg2 = r3
            android.os.Handler r3 = r10.mHandler
            r4 = 90
            r3.sendMessageDelayed(r2, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog.loadChannelSortRes():void");
    }

    public void gotoSeacherTextAct() {
        MtkLog.d(TAG, "gotoSeacherTextAct()");
        Intent intent = new Intent(this.mContext, EditTextActivity.class);
        intent.putExtra(EditTextActivity.TYPE_CLASS_TEXT, true);
        intent.putExtra("description", this.mContext.getResources().getString(R.string.nav_select_find_text));
        intent.putExtra("initialText", "");
        intent.putExtra("itemId", this.CURRENT_CHANNEL_FIND);
        intent.putExtra("length", 32);
        Message msg = Message.obtain();
        msg.what = 4113;
        msg.arg1 = 4113;
        msg.obj = intent;
        TurnkeyUiMainActivity.getInstance().getHandler().sendMessage(msg);
        MtkLog.e(TAG, "gotoSeacherTextAct() end");
    }

    private void findViews() {
        this.mChannelListLayout = findViewById(R.id.nav_channellist);
        this.mChannelListView = (ListView) findViewById(R.id.nav_channel_listview);
        this.mChannelTypeView = (ListView) findViewById(R.id.nav_channel_typeview);
        this.mChannelSelectMoreView = (ListView) findViewById(R.id.nav_channel_select_more);
        this.mChannelSortView = (ListView) findViewById(R.id.nav_channel_sort);
        this.mChannelSatelliteView = (ListView) findViewById(R.id.nav_channel_satellitetypeview);
        this.mChannelSatelliteSecondView = (ListView) findViewById(R.id.nav_channel_satellitetypesecondview);
        this.mChannelListTipView = findViewById(R.id.nav_channel_list_tip);
        this.mChannelListFunctionLayout = findViewById(R.id.nav_page_function);
        this.mChannelListPageUpDownLayout = findViewById(R.id.nav_page_up_down);
        this.mChannelDetailLayout = findViewById(R.id.nav_channel_details_layout);
        this.mChannelDetailLayout.setVisibility(4);
        this.mChannelDetailTileText = (TextView) findViewById(R.id.nav_channel_details_title);
        this.mNavChannelDetailsChannelInfoTextView = (TextView) findViewById(R.id.nav_channel_details_channel_info);
        this.mTitleText = (TextView) findViewById(R.id.nav_channel_list_title);
        this.mYellowKeyText = (TextView) findViewById(R.id.channel_nav_select_list);
        this.mBlueKeyText = (TextView) findViewById(R.id.channel_nav_exit);
        this.mBlueicon = (ImageView) findViewById(R.id.channel_nav_exit_icon);
        this.chanelListProgressbar = (ProgressBar) findViewById(R.id.nav_channel_list_progressbar);
    }

    /* access modifiers changed from: private */
    public List<TIFChannelInfo> updateCurrentChannelLlistByTIF() {
        List<TIFChannelInfo> mNextChannelList;
        MtkTvDvbChannelInfo curInfo;
        List<TIFChannelInfo> mTifChannelList = new ArrayList<>();
        TIFChannelInfo chInfo = null;
        int startChannelId = -1;
        int i = 0;
        if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0) {
            int preNum = this.mTIFChannelManager.getChannelListConfirmLength(8, this.mCurMask, this.mCurVal);
            this.hasNextPage = preNum > 7;
            MtkLog.d(TAG, "TIF SATE hasNextPage =" + this.hasNextPage);
            if (this.hasNextPage) {
                int index = 0;
                if (this.mChannelAdapter != null && this.mChannelAdapter.getCount() > 0) {
                    while (true) {
                        int i2 = i;
                        if (i2 >= this.mChannelAdapter.getCount()) {
                            break;
                        }
                        chInfo = this.mChannelAdapter.getItem(i2);
                        if (chInfo != null) {
                            if (this.commonIntegration.is3rdTVSource()) {
                                chInfo = this.mTIFChannelManager.getTIFChannelInfoById((int) chInfo.mId);
                                if (chInfo != null) {
                                    startChannelId = (int) chInfo.mId;
                                    index = i2;
                                    break;
                                }
                            } else {
                                chInfo = this.mTIFChannelManager.getTIFChannelInfoById(chInfo.mInternalProviderFlag3);
                                if (chInfo != null) {
                                    startChannelId = chInfo.mInternalProviderFlag3;
                                    index = i2;
                                    break;
                                }
                            }
                        }
                        i = i2 + 1;
                    }
                    List<TIFChannelInfo> mPreChannelList = null;
                    if (index > 0) {
                        mPreChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(startChannelId, true, false, index, this.mCurMask, this.mCurVal);
                    }
                    if (chInfo == null || startChannelId == -1) {
                        startChannelId = this.commonIntegration.getCurrentChannelId();
                        chInfo = this.mTIFChannelManager.getTIFChannelInfoById(startChannelId);
                    }
                    if (!CommonIntegration.isEURegion() || this.CURRENT_CHANNEL_TYPE == 0) {
                        if (this.commonIntegration.checkChMask(chInfo.mMtkTvChannelInfo, this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                            mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(startChannelId, false, true, 7 - index, this.mCurMask, this.mCurVal);
                        } else {
                            mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(startChannelId, false, false, 7 - index, this.mCurMask, this.mCurVal);
                        }
                    } else if (this.commonIntegration.checkChMask(chInfo.mMtkTvChannelInfo, this.mCurMask, this.mCurVal)) {
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(startChannelId, false, true, 7 - index, this.mCurMask, this.mCurVal);
                    } else {
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(startChannelId, false, false, 7 - index, this.mCurMask, this.mCurVal);
                    }
                    if (mPreChannelList != null) {
                        mTifChannelList.addAll(mPreChannelList);
                    }
                    if (mNextChannelList != null) {
                        mTifChannelList.addAll(mNextChannelList);
                    }
                    while (mTifChannelList.size() > 7) {
                        mTifChannelList.remove(mTifChannelList.size() - 1);
                    }
                    return mTifChannelList;
                } else if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                    return this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(this.commonIntegration.getCurrentChannelId(), false, true, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                } else {
                    return this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(this.commonIntegration.getCurrentChannelId(), false, false, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                }
            } else if (preNum <= 0) {
                return mTifChannelList;
            } else {
                if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                    return this.mTIFChannelManager.getTIFPreOrNextChannelList(-1, false, true, 7, this.mCurMask, this.mCurVal);
                }
                return this.mTIFChannelManager.getTIFPreOrNextChannelList(-1, false, false, 7, this.mCurMask, this.mCurVal);
            }
        } else if (this.CURRENT_CHANNEL_TYPE != 5) {
            int preNum2 = this.mTIFChannelManager.getSatelliteChannelConfirmCount(this.mCurrentSatelliteRecordId, 8, this.mCurMask, this.mCurVal);
            MtkLog.d(TAG, "preNum>mCurrentSatelliteRecordId>>>" + preNum2);
            this.hasNextPage = preNum2 > 7;
            MtkLog.d(TAG, "tif isGeneralSatMode hasNextPage =" + this.hasNextPage + "    startChannelId>>" + -1);
            if (this.hasNextPage) {
                List<TIFChannelInfo> mTifChannelList2 = new ArrayList<>();
                int index2 = 0;
                if (this.mChannelAdapter != null && this.mChannelAdapter.getCount() > 0) {
                    while (true) {
                        int i3 = i;
                        if (i3 < this.mChannelAdapter.getCount()) {
                            chInfo = this.mChannelAdapter.getItem(i3);
                            if (chInfo != null && (chInfo = this.mTIFChannelManager.getTIFChannelInfoById(chInfo.mMtkTvChannelInfo.getChannelId())) != null) {
                                startChannelId = chInfo.mMtkTvChannelInfo.getChannelId();
                                index2 = i3;
                                break;
                            }
                            i = i3 + 1;
                        } else {
                            break;
                        }
                    }
                    List<TIFChannelInfo> mPreChannelList2 = null;
                    if (chInfo == null || startChannelId == -1) {
                        startChannelId = this.commonIntegration.getCurrentChannelId();
                        TIFChannelInfo chInfo2 = this.mTIFChannelManager.getTIFChannelInfoById(startChannelId);
                    }
                    if (index2 > 0) {
                        mPreChannelList2 = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(startChannelId, true, false, this.mCurrentSatelliteRecordId, index2, this.mCurMask, this.mCurVal);
                    }
                    List<TIFChannelInfo> mNextChannelList2 = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(startChannelId, false, false, this.mCurrentSatelliteRecordId, 7 - index2, this.mCurMask, this.mCurVal);
                    if (mPreChannelList2 != null) {
                        mTifChannelList2.addAll(mPreChannelList2);
                    }
                    TIFChannelInfo tifCurrentChannel = this.mTIFChannelManager.getTIFChannelInfoById(startChannelId);
                    if (tifCurrentChannel != null) {
                        MtkTvChannelInfoBase mCurrentChannel = tifCurrentChannel.mMtkTvChannelInfo;
                        if ((mCurrentChannel instanceof MtkTvDvbChannelInfo) && (curInfo = (MtkTvDvbChannelInfo) mCurrentChannel) != null && this.commonIntegration.checkChMask(tifCurrentChannel.mMtkTvChannelInfo, this.mCurMask, this.mCurVal) && curInfo.getSatRecId() == this.mCurrentSatelliteRecordId) {
                            MtkLog.d(TAG, "tif curInfo.getSatRecId()>>" + curInfo.getSatRecId() + "  " + this.mCurrentSatelliteRecordId);
                            mTifChannelList2.add(tifCurrentChannel);
                        }
                    }
                    if (mNextChannelList2 != null) {
                        mTifChannelList2.addAll(mNextChannelList2);
                    }
                    while (mTifChannelList2.size() > 7) {
                        mTifChannelList2.remove(mTifChannelList2.size() - 1);
                    }
                    return mTifChannelList2;
                } else if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                    return this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(this.commonIntegration.getCurrentChannelId(), false, true, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                } else {
                    return this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(this.commonIntegration.getCurrentChannelId(), false, false, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                }
            } else if (preNum2 <= 0) {
                return mTifChannelList;
            } else {
                if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                    return this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(-1, false, true, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                }
                return this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(-1, false, false, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
            }
        } else {
            this.hasNextPage = false;
            return mTifChannelList;
        }
    }

    /* access modifiers changed from: private */
    public synchronized void resetChListByTIF() {
        this.mSelectionShow = false;
        ((Activity) this.mContext).runOnUiThread(new Runnable() {
            public void run() {
                ChannelListDialog.this.setChannelListTitle();
            }
        });
        this.mThreadHandler.post(this.mResetChannelListRunnable);
    }

    /* access modifiers changed from: private */
    public List<TIFChannelInfo> processTIFChListWithThread(int chId) {
        List<TIFChannelInfo> mTifChannelList;
        List<TIFChannelInfo> mNextChannelList;
        MtkTvDvbChannelInfo curInfo;
        int i = chId;
        boolean z = false;
        if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0) {
            int preNum = this.mTIFChannelManager.getChannelListConfirmLength(8, this.mCurMask, this.mCurVal);
            this.hasNextPage = preNum > 7;
            MtkLog.d(TAG, "TIF SATE hasNextPage =" + this.hasNextPage);
            if (this.hasNextPage) {
                int index = currentChannelInListIndex(chId);
                mTifChannelList = new ArrayList<>();
                List<TIFChannelInfo> mPreChannelList = null;
                if (index == -1 || !this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal)) {
                    MtkLog.d(TAG, "TIF SATE mLastSelection =" + this.mLastSelection);
                    if (this.mLastSelection < 0) {
                        this.mLastSelection = 0;
                    }
                    if (!CommonIntegration.isEURegion() || this.CURRENT_CHANNEL_TYPE == 0) {
                        if (this.mLastSelection > 0) {
                            mPreChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, true, false, this.mLastSelection, this.mCurMask, this.mCurVal);
                        }
                        if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                            mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, true, 7 - this.mLastSelection, this.mCurMask, this.mCurVal);
                        } else {
                            mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, false, 7 - this.mLastSelection, this.mCurMask, this.mCurVal);
                        }
                    } else if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal)) {
                        if (this.mLastSelection > 0) {
                            mPreChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, true, false, this.mLastSelection, this.mCurMask, this.mCurVal);
                        }
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, true, 7 - this.mLastSelection, this.mCurMask, this.mCurVal);
                    } else {
                        this.mLastSelection = 0;
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, false, 7 - this.mLastSelection, this.mCurMask, this.mCurVal);
                    }
                } else {
                    if (index > 0) {
                        mPreChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, true, false, index, this.mCurMask, this.mCurVal);
                    }
                    if (!CommonIntegration.isEURegion() || this.CURRENT_CHANNEL_TYPE == 0) {
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, true, 7 - index, this.mCurMask, this.mCurVal);
                    } else if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal)) {
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, true, 7 - index, this.mCurMask, this.mCurVal);
                    } else {
                        mNextChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(i, false, false, 7 - index, this.mCurMask, this.mCurVal);
                    }
                }
                if (mPreChannelList != null) {
                    mTifChannelList.addAll(mPreChannelList);
                }
                if (mNextChannelList != null) {
                    mTifChannelList.addAll(mNextChannelList);
                }
                while (mTifChannelList.size() > 7) {
                    mTifChannelList.remove(mTifChannelList.size() - 1);
                }
            } else if (preNum <= 0) {
                mTifChannelList = null;
            } else if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(-1, false, true, 7, this.mCurMask, this.mCurVal);
            } else {
                this.mLastSelection = 0;
                mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(-1, false, false, 7, this.mCurMask, this.mCurVal);
            }
        } else if (this.CURRENT_CHANNEL_TYPE != 5) {
            int preNum2 = this.mTIFChannelManager.getSatelliteChannelConfirmCount(this.mCurrentSatelliteRecordId, 8, this.mCurMask, this.mCurVal);
            MtkLog.d(TAG, "preNum>mCurrentSatelliteRecordId>>>" + preNum2);
            if (preNum2 > 7) {
                z = true;
            }
            this.hasNextPage = z;
            MtkLog.d(TAG, "tif isGeneralSatMode hasNextPage =" + this.hasNextPage + "    chId>>" + i);
            if (this.hasNextPage) {
                mTifChannelList = new ArrayList<>();
                List<TIFChannelInfo> mPreChannelList2 = null;
                int index2 = currentChannelInListIndex(chId);
                if (index2 < 0) {
                    index2 = 0;
                }
                int index3 = index2;
                if (index3 > 0) {
                    mPreChannelList2 = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(i, true, false, this.mCurrentSatelliteRecordId, index3, this.mCurMask, this.mCurVal);
                }
                List<TIFChannelInfo> mNextChannelList2 = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(i, false, false, this.mCurrentSatelliteRecordId, 7 - index3, this.mCurMask, this.mCurVal);
                if (mPreChannelList2 != null) {
                    mTifChannelList.addAll(mPreChannelList2);
                }
                TIFChannelInfo tifCurrentChannel = this.mTIFChannelManager.getTIFChannelInfoById(i);
                if (tifCurrentChannel != null) {
                    MtkTvChannelInfoBase mCurrentChannel = tifCurrentChannel.mMtkTvChannelInfo;
                    if ((mCurrentChannel instanceof MtkTvDvbChannelInfo) && (curInfo = (MtkTvDvbChannelInfo) mCurrentChannel) != null && this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) && curInfo.getSatRecId() == this.mCurrentSatelliteRecordId) {
                        MtkLog.d(TAG, "tif curInfo.getSatRecId()>>" + curInfo.getSatRecId() + "  " + this.mCurrentSatelliteRecordId);
                        mTifChannelList.add(tifCurrentChannel);
                    }
                }
                if (mNextChannelList2 != null) {
                    mTifChannelList.addAll(mNextChannelList2);
                }
                while (mTifChannelList.size() > 7) {
                    mTifChannelList.remove(mTifChannelList.size() - 1);
                }
            } else if (preNum2 <= 0) {
                mTifChannelList = null;
            } else if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(-1, false, true, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
            } else {
                mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(-1, false, false, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
            }
        } else {
            this.hasNextPage = false;
            mTifChannelList = null;
        }
        return mTifChannelList;
    }

    /* access modifiers changed from: private */
    public synchronized void resetChList() {
        this.mSelectionShow = false;
        ((Activity) this.mContext).runOnUiThread(new Runnable() {
            public void run() {
                if (ChannelListDialog.this.commonIntegration.is3rdTVSource()) {
                    ChannelListDialog.this.mTitleText.setText(R.string.nav_channel_list);
                    ChannelListDialog.this.mYellowKeyText.setText("");
                } else if (!ChannelListDialog.this.isSelectionMode()) {
                    ChannelListDialog.this.mTitleText.setText(R.string.nav_channel_list);
                } else if (CommonIntegration.isEURegion()) {
                    TextView access$4100 = ChannelListDialog.this.mTitleText;
                    access$4100.setText(ChannelListDialog.this.mTitlePre + ChannelListDialog.this.types[ChannelListDialog.this.CURRENT_CHANNEL_TYPE]);
                    ChannelListDialog.this.mYellowKeyText.setText(ChannelListDialog.this.mContext.getResources().getString(R.string.nav_select_list));
                } else {
                    ChannelListDialog.this.mTitleText.setText(R.string.nav_channel_list);
                    ChannelListDialog.this.mYellowKeyText.setText(ChannelListDialog.this.mContext.getResources().getString(R.string.nav_select_list_cn));
                }
            }
        });
        new Thread(new Runnable() {
            public void run() {
                int chId;
                if (!ChannelListDialog.this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") || !ChannelListDialog.this.commonIntegration.isDualTunerEnable() || !ChannelListDialog.this.commonIntegration.isCurrentSourceTv()) {
                    chId = ChannelListDialog.this.commonIntegration.getCurrentChannelId();
                    MtkLog.d(ChannelListDialog.TAG, "resetChList chId>> = " + chId);
                } else {
                    chId = ChannelListDialog.this.commonIntegration.get2NDCurrentChannelId();
                    MtkLog.d(ChannelListDialog.TAG, "resetChList dual tuner chId>> = " + chId);
                }
                List<TIFChannelInfo> tempList = ChannelListDialog.this.processListWithThread(chId);
                Message msg = Message.obtain();
                msg.what = 4102;
                msg.arg1 = chId;
                msg.obj = tempList;
                ChannelListDialog.this.mHandler.sendMessage(msg);
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public List<TIFChannelInfo> processListWithThread(int chId) {
        int preNum;
        List<TIFChannelInfo> tempChlist;
        List<MtkTvChannelInfoBase> tempApiChList;
        List<TIFChannelInfo> tempChlist2;
        List<MtkTvChannelInfoBase> tempApiChList2;
        MtkTvDvbChannelInfo curInfo;
        int i = chId;
        boolean z = false;
        if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0) {
            int preNum2 = this.commonIntegration.hasNextPageChannel(8, this.mCurMask, this.mCurVal);
            if (preNum2 > 7) {
                z = true;
            }
            this.hasNextPage = z;
            MtkLog.d(TAG, "hasNextPage =" + this.hasNextPage + "    chId>>" + i);
            if (this.hasNextPage) {
                int index = currentChannelInListIndex(chId);
                if (index == -1 || !this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal)) {
                    MtkLog.d(TAG, "tempApiChList CURRENT_CHANNEL_TYPE 2 " + this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal));
                    if (!CommonIntegration.isEURegion() || this.CURRENT_CHANNEL_TYPE == 0) {
                        MtkLog.d(TAG, "tempApiChList CURRENT_CHANNEL_TYPE 4 " + this.CURRENT_CHANNEL_TYPE);
                        if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) || CommonIntegration.isUSRegion()) {
                            tempApiChList = this.commonIntegration.getChList(i, this.mLastSelection, 7 - this.mLastSelection);
                        } else {
                            tempApiChList = this.commonIntegration.getChannelListByMaskValuefilter(i, this.mLastSelection, 7 - this.mLastSelection, this.mCurMask, this.mCurVal, false);
                        }
                    } else {
                        MtkLog.d(TAG, "tempApiChList CURRENT_CHANNEL_TYPE 3 " + this.CURRENT_CHANNEL_TYPE);
                        if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal)) {
                            tempApiChList = this.commonIntegration.getChannelListByMaskValuefilter(i, this.mLastSelection, 7 - this.mLastSelection, this.mCurMask, this.mCurVal, true);
                        } else {
                            tempApiChList = this.commonIntegration.getChannelListByMaskValuefilter(i, this.mLastSelection, 7 - this.mLastSelection, this.mCurMask, this.mCurVal, false);
                        }
                    }
                } else if (!CommonIntegration.isEURegion() || this.CURRENT_CHANNEL_TYPE == 0) {
                    MtkLog.d(TAG, "tempApiChList CURRENT_CHANNEL_TYPE 2 " + this.CURRENT_CHANNEL_TYPE);
                    tempApiChList = this.commonIntegration.getChList(i, index, 7 - index);
                } else {
                    MtkLog.d(TAG, "tempApiChList CURRENT_CHANNEL_TYPE 1 " + this.CURRENT_CHANNEL_TYPE);
                    if (this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal)) {
                        tempApiChList = this.commonIntegration.getChannelListByMaskValuefilter(i, index, 7 - index, this.mCurMask, this.mCurVal, true);
                    } else {
                        tempApiChList = this.commonIntegration.getChannelListByMaskValuefilter(i, index, 7 - index, this.mCurMask, this.mCurVal, false);
                    }
                }
                while (tempApiChList.size() > 7) {
                    tempApiChList.remove(tempApiChList.size() - 1);
                }
                MtkLog.d(TAG, "tempApiChList length " + tempApiChList.size());
                List<MtkTvChannelInfoBase> list = tempApiChList;
                int i2 = preNum2;
                return this.mTIFChannelManager.getTIFChannelList(tempApiChList);
            }
            if (preNum2 > 0) {
                preNum = preNum2;
                List<MtkTvChannelInfoBase> tempApiChList3 = getChannelList(0, 0, 7, this.mCurMask, this.mCurVal);
                tempChlist = TIFFunctionUtil.getTIFChannelList(tempApiChList3);
                List<MtkTvChannelInfoBase> list2 = tempApiChList3;
            } else {
                preNum = preNum2;
                tempChlist = null;
            }
            return tempChlist;
        } else if (this.CURRENT_CHANNEL_TYPE != 5) {
            int preNum3 = this.commonIntegration.getSatelliteChannelCount(this.mCurrentSatelliteRecordId, this.mCurMask, this.mCurVal);
            if (preNum3 > 7) {
                z = true;
            }
            this.hasNextPage = z;
            MtkLog.d(TAG, "isGeneralSatMode hasNextPage =" + this.hasNextPage + "    chId>>" + i);
            if (this.hasNextPage) {
                tempApiChList2 = new ArrayList<>();
                List<MtkTvChannelInfoBase> preList = null;
                int index2 = currentChannelInListIndex(chId);
                if (index2 < 0) {
                    index2 = 0;
                }
                int index3 = index2;
                if (index3 > 0) {
                    preList = this.commonIntegration.getChannelListByMaskAndSat(i, 3, index3, this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId);
                }
                List<MtkTvChannelInfoBase> nextList = this.commonIntegration.getChannelListByMaskAndSat(i, 2, 7 - index3, this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId);
                if (preList != null) {
                    tempApiChList2.addAll(preList);
                }
                MtkTvChannelInfoBase mCurrentChannel = this.commonIntegration.getCurChInfo();
                if ((mCurrentChannel instanceof MtkTvDvbChannelInfo) && (curInfo = (MtkTvDvbChannelInfo) mCurrentChannel) != null && this.commonIntegration.checkCurChMask(this.mCurMask, this.mCurVal) && curInfo.getSatRecId() == this.mCurrentSatelliteRecordId) {
                    MtkLog.d(TAG, "curInfo.getSatRecId()>>" + curInfo.getSatRecId() + "  " + this.mCurrentSatelliteRecordId);
                    tempApiChList2.add(curInfo);
                }
                if (nextList != null) {
                    tempApiChList2.addAll(nextList);
                }
                while (tempApiChList2.size() > 7) {
                    tempApiChList2.remove(tempApiChList2.size() - 1);
                }
                tempChlist2 = TIFFunctionUtil.getTIFChannelList(tempApiChList2);
            } else if (preNum3 <= 0) {
                return null;
            } else {
                tempApiChList2 = this.commonIntegration.getChannelListByMaskAndSat(i, 0, 7, this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId);
                tempChlist2 = TIFFunctionUtil.getTIFChannelList(tempApiChList2);
            }
            return tempChlist2;
        } else {
            this.hasNextPage = false;
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void saveLastPosition(int currentChannelId, List<TIFChannelInfo> tempChlist) {
        int channelId;
        if (tempChlist != null) {
            this.mChannelIdList.clear();
            int size = tempChlist.size();
            for (int i = 0; i < size; i++) {
                if (tempChlist.get(i).mMtkTvChannelInfo == null) {
                    channelId = (int) tempChlist.get(i).mId;
                } else {
                    channelId = tempChlist.get(i).mMtkTvChannelInfo.getChannelId();
                }
                this.mChannelIdList.add(Integer.valueOf(channelId));
                MtkLog.d(TAG, "currentChannelId>>>" + currentChannelId + "   " + channelId);
            }
        }
    }

    private int currentChannelInListIndex(int currentChannelId) {
        int size = this.mChannelIdList.size();
        MtkLog.d(TAG, "mChannelIdList size: " + size);
        for (int i = 0; i < size; i++) {
            MtkLog.d(TAG, "mChannelIdList  mChannelIdList.get(" + i + "): " + this.mChannelIdList.get(i));
            if (currentChannelId == this.mChannelIdList.get(i).intValue()) {
                MtkLog.d(TAG, "i>>>>" + i);
                return i;
            }
        }
        MtkLog.d(TAG, "i>>>>-1");
        return -1;
    }

    /* access modifiers changed from: private */
    public boolean selectedItem(ViewGroup v, AccessibilityEvent event) {
        MtkLog.d(TAG, ":selectedchanneltype v.getId() = " + v.getId());
        int id = v.getId();
        if (id != R.id.nav_channel_typeview) {
            switch (id) {
                case R.id.nav_channel_satellitetypeview:
                    this.mCurrentSatelliteRecordId = this.mSatelliteListinfo.get(this.mChannelSatelliteView.getSelectedItemPosition()).getSatlRecId();
                    MtkLog.d(TAG, "mCurrentSatelliteRecordId>>>" + this.mCurrentSatelliteRecordId);
                    this.mSaveValue.saveValue(SATELLITE_RECORDID, this.mCurrentSatelliteRecordId);
                    this.mChannelListView.setAdapter((ListAdapter) null);
                    this.mChannelListView.setVisibility(0);
                    this.mChannelSatelliteView.setVisibility(8);
                    if (this.mIsTifFunction) {
                        resetChListByTIF();
                    } else {
                        resetChList();
                    }
                    return true;
                case R.id.nav_channel_select_more:
                    this.mTIFChannelManager.findChanelsForlist("", this.mCurMask, this.mCurVal);
                    this.CURRENT_SELECT_MORE = this.mChannelSelectMoreView.getSelectedItemPosition();
                    MtkLog.d(TAG, "nav_channel_select_more>>> CURRENT_SELECT_MORE is " + this.CURRENT_SELECT_MORE);
                    this.mSaveValue.saveValue(SEL_MORE, this.CURRENT_SELECT_MORE);
                    this.mChannelSelectMoreView.setVisibility(8);
                    if (this.CURRENT_SELECT_MORE == 0) {
                        MtkLog.d(TAG, "nav_channel_select_more>>> loadChannelTypeRes() ");
                        loadChannelTypeRes();
                        this.mTitleText.setText(this.mTitlePre);
                    } else if (this.CURRENT_SELECT_MORE == 1) {
                        MtkLog.d(TAG, "nav_channel_select_more>>> loadChannelSortRes() ");
                        loadChannelSortRes();
                        this.mTitleText.setText(this.sortTitle);
                    } else if (this.CURRENT_SELECT_MORE == 2) {
                        MtkLog.d(TAG, "nav_channel_select_more>>> gotoSeacherTextAct() ");
                        this.mChannelListView.setAdapter((ListAdapter) null);
                        this.mChannelListView.setVisibility(0);
                        gotoSeacherTextAct();
                    }
                    startTimeout(10000);
                    return true;
                case R.id.nav_channel_sort:
                    this.mTIFChannelManager.findChanelsForlist("", this.mCurMask, this.mCurVal);
                    this.CURRENT_CHANNEL_SORT = this.mChannelSortView.getSelectedItemPosition();
                    MtkLog.d(TAG, "CURRENT_CHANNEL_SORT>>>" + this.CURRENT_CHANNEL_SORT);
                    this.mSaveValue.saveValue(CH_SORT, this.CURRENT_CHANNEL_SORT);
                    this.mChannelListView.setAdapter((ListAdapter) null);
                    this.mChannelListView.setVisibility(0);
                    this.mChannelSortView.setVisibility(8);
                    this.mTIFChannelManager.setCurrentChannelSort(this.CURRENT_CHANNEL_SORT);
                    if (this.mIsTifFunction) {
                        resetChListByTIF();
                    } else {
                        resetChList();
                    }
                    return true;
                default:
                    return false;
            }
        } else {
            this.mHandler.removeMessages(4114);
            this.CURRENT_CHANNEL_TYPE = this.mChannelTypeView.getSelectedItemPosition();
            MtkLog.d(TAG, "KEYCODE_DPAD_CENTER position = " + this.CURRENT_CHANNEL_TYPE);
            MtkLog.d(TAG, "KEYCODE_DPAD_CENTER commonIntegration.isOperatorNTVPLUS() = " + this.commonIntegration.isOperatorNTVPLUS());
            MtkLog.d(TAG, "KEYCODE_DPAD_CENTER commonIntegration.isOperatorTELEKARTA() = " + this.commonIntegration.isOperatorTELEKARTA());
            if ((this.commonIntegration.isOperatorNTVPLUS() || this.commonIntegration.isOperatorTKGS()) && this.CURRENT_CHANNEL_TYPE > 0) {
                if (this.CURRENT_CHANNEL_TYPE == 1) {
                    loadChannelListTypeSecondeView();
                    return true;
                }
                this.CURRENT_CHANNEL_TYPE--;
                resetMask();
                this.CURRENT_CHANNEL_TYPE++;
            } else if (!this.commonIntegration.isOperatorTELEKARTA() || this.CURRENT_CHANNEL_TYPE <= 0) {
                resetMask();
            } else if (this.CURRENT_CHANNEL_TYPE == 2 || this.CURRENT_CHANNEL_TYPE == 1) {
                loadChannelListTypeSecondeView();
                return true;
            } else {
                this.CURRENT_CHANNEL_TYPE -= 2;
                resetMask();
                this.CURRENT_CHANNEL_TYPE += 2;
            }
            this.typesSecond = null;
            MtkLog.d(TAG, "ChannelListOnKey ,mCurMask: " + this.mCurMask + " mCurVal " + this.mCurVal);
            if (this.mIsTifFunction) {
                MtkLog.d(TAG, "ChannelListOnKey = " + this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") + "  isCurrentSourceTv = " + this.commonIntegration.isCurrentSourceTv());
                if (this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") && MarketRegionInfo.isFunctionSupport(32) && this.commonIntegration.isCurrentSourceTv()) {
                    resetChList();
                } else if (this.mTIFChannelManager.getChannelListConfirmLength(1, this.mCurMask, this.mCurVal) > 0) {
                    this.mChannelListView.setVisibility(0);
                    this.mChannelTypeView.setVisibility(8);
                    resetChListByTIF();
                    MtkLog.d(TAG, "ChannelListOnKey : change type then change chanenls ");
                    this.mHandler.sendEmptyMessageDelayed(4114, 1000);
                } else {
                    MtkLog.d(TAG, "CHANGE_TYPE_CHANGECHANNEL current type no chanenls,return last type");
                    initMaskAndSatellites();
                    setChannelListTitle();
                    showPageUpDownView();
                    this.mChannelListTipView.setVisibility(0);
                    this.mChannelListView.setVisibility(0);
                    this.CURRENT_CHANNEL_TYPE = this.mSaveValue.readValue(CH_TYPE, 0);
                    this.mChannelTypeView.setSelection(this.CURRENT_CHANNEL_TYPE);
                    this.mChannelTypeView.setVisibility(8);
                }
            } else {
                resetChList();
            }
            return true;
        }
    }

    private void init() {
        MtkLog.d(TAG, "init types.length = " + this.types.length);
        this.mTitlePre = this.mContext.getResources().getString(R.string.nav_channel_list) + " - ";
        if (this.mIsTifFunction) {
            MtkLog.d(TAG, "getCurrentFocus = " + this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") + "  isCurrentSourceTv = " + this.commonIntegration.isCurrentSourceTv());
            if (!this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") || !MarketRegionInfo.isFunctionSupport(32) || !this.commonIntegration.isCurrentSourceTv()) {
                resetChListByTIF();
            } else {
                resetChList();
            }
        } else {
            resetChList();
        }
        if (3 == MarketRegionInfo.getCurrentMarketRegion()) {
            TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, this.mHandler);
        }
        this.mChannelListView.setAccessibilityDelegate(this.mAccDelegate);
        this.mChannelSelectMoreView.setAccessibilityDelegate(this.mAccDelegate);
        this.mChannelSortView.setAccessibilityDelegate(this.mAccDelegate);
        this.mChannelTypeView.setAccessibilityDelegate(this.mAccDelegate);
        this.mChannelSortView.setImportantForAccessibility(0);
        this.mChannelTypeView.setImportantForAccessibility(0);
        this.mChannelSelectMoreView.setImportantForAccessibility(0);
        this.mChannelListView.setOnKeyListener(new ChannelListOnKey());
    }

    public List<TIFChannelInfo> getTIFPreOrNextchlistFor3rdSource(boolean next) {
        MtkLog.d(TAG, "getTIFPreOrNextchlistFor3rdSource");
        List<TIFChannelInfo> mTIFChannelInfoList = new ArrayList<>();
        List<TIFChannelInfo> mTIFChList = null;
        if (this.mChannelAdapter == null || this.mChannelAdapter.getCount() <= 0) {
            MtkLog.d(TAG, "false mTIFChannelInfoList.size()= " + mTIFChannelInfoList.size());
            return mTIFChannelInfoList;
        }
        if (this.commonIntegration.is3rdTVSource()) {
            TvInputInfo tvInputInfo = this.commonIntegration.getTvInputInfo();
            if (tvInputInfo != null) {
                mTIFChList = this.mTIFChannelManager.getTIFChannelInfoBySource(tvInputInfo.getId());
                MtkLog.d(TAG, "mTIFChList length= " + mTIFChList.size());
            } else {
                MtkLog.d(TAG, "getAllChannelListByTIF, tvInputInfo");
            }
        }
        if (mTIFChList == null) {
            MtkLog.d(TAG, "getTIFPreOrNextchlistFor3rdSource mTIFChList is null");
            return mTIFChannelInfoList;
        }
        int i = 0;
        if (mTIFChList.size() <= 7) {
            this.hasNextPage = false;
            MtkLog.d(TAG, "mTIFChList.size() <= 7");
            return mTIFChList;
        }
        this.hasNextPage = true;
        if (!next) {
            TIFChannelInfo chInfo = this.mChannelAdapter.getItem(0);
            boolean startflag = false;
            List<TIFChannelInfo> temp = new ArrayList<>();
            for (int i2 = mTIFChList.size() - 1; i2 > -1; i2--) {
                if (chInfo.mId == mTIFChList.get(i2).mId) {
                    startflag = true;
                } else if (!startflag || temp.size() >= 7) {
                    startflag = false;
                } else {
                    temp.add(mTIFChList.get(i2));
                }
            }
            MtkLog.d(TAG, "temp.size()= " + temp.size());
            if (temp.size() < 7) {
                int tp = mTIFChList.size() - (7 - temp.size());
                for (int i3 = mTIFChList.size() - 1; i3 >= tp; i3--) {
                    temp.add(mTIFChList.get(i3));
                }
            }
            int i4 = temp.size() - 1;
            while (true) {
                int i5 = i4;
                if (i5 <= -1) {
                    break;
                }
                mTIFChannelInfoList.add(temp.get(i5));
                i4 = i5 - 1;
            }
        } else {
            TIFChannelInfo chInfo2 = this.mChannelAdapter.getItem(this.mChannelAdapter.getCount() - 1);
            boolean startflag2 = false;
            for (int i6 = 0; i6 < mTIFChList.size(); i6++) {
                if (chInfo2.mId == mTIFChList.get(i6).mId) {
                    startflag2 = true;
                } else if (!startflag2 || mTIFChannelInfoList.size() >= 7) {
                    startflag2 = false;
                } else {
                    mTIFChannelInfoList.add(mTIFChList.get(i6));
                }
            }
            if (mTIFChannelInfoList.size() < 7) {
                int tp2 = 7 - mTIFChannelInfoList.size();
                while (true) {
                    int i7 = i;
                    if (i7 >= tp2) {
                        break;
                    }
                    mTIFChannelInfoList.add(mTIFChList.get(i7));
                    i = i7 + 1;
                }
            }
        }
        MtkLog.d(TAG, "mTIFChannelInfoList.size()= " + mTIFChannelInfoList.size());
        return mTIFChannelInfoList;
    }

    public List<TIFChannelInfo> getAllChannelListByTIFFor3rdSource(int mId) {
        List<TIFChannelInfo> mTIFChannelInfoList = new ArrayList<>();
        List<TIFChannelInfo> mTIFChList = null;
        if (this.commonIntegration.is3rdTVSource()) {
            TvInputInfo tvInputInfo = this.commonIntegration.getTvInputInfo();
            if (tvInputInfo != null) {
                mTIFChList = this.mTIFChannelManager.getTIFChannelInfoBySource(tvInputInfo.getId());
            } else {
                MtkLog.e(TAG, "getAllChannelListByTIF, tvInputInfo");
            }
        }
        if (mTIFChList != null && mTIFChList.size() > 0) {
            MtkLog.d(TAG, "mTIFChList.size() = " + mTIFChList.size());
            int i = 0;
            if (mTIFChList.size() <= 7) {
                this.hasNextPage = false;
                mTIFChannelInfoList = mTIFChList;
            } else {
                this.hasNextPage = true;
                boolean startflag = false;
                for (int i2 = 0; i2 < mTIFChList.size(); i2++) {
                    if (((long) mId) == mTIFChList.get(i2).mId) {
                        startflag = true;
                    }
                    if (!startflag || mTIFChannelInfoList.size() >= 7) {
                        startflag = false;
                    } else {
                        mTIFChannelInfoList.add(mTIFChList.get(i2));
                    }
                }
                if (mTIFChannelInfoList.size() < 7) {
                    int tp = 7 - mTIFChannelInfoList.size();
                    while (true) {
                        int i3 = i;
                        if (i3 >= tp) {
                            break;
                        }
                        mTIFChannelInfoList.add(mTIFChList.get(i3));
                        i = i3 + 1;
                    }
                }
            }
        }
        MtkLog.d(TAG, "getAllChannelListByTIFFor3rdSource mTIFChannelInfoList.size()= " + mTIFChannelInfoList.size());
        return mTIFChannelInfoList;
    }

    public List<TIFChannelInfo> getChannelListByTIFFor3rdSource() {
        List<TIFChannelInfo> mTIFChannelInfoList = new ArrayList<>();
        TvInputInfo tvInputInfo = this.commonIntegration.getTvInputInfo();
        if (tvInputInfo != null) {
            return this.mTIFChannelManager.getTIFChannelInfoBySource(tvInputInfo.getId());
        }
        MtkLog.e(TAG, "getAllChannelListByTIF, tvInputInfo");
        return mTIFChannelInfoList;
    }

    /* access modifiers changed from: private */
    public List<MtkTvChannelInfoBase> getNextPrePageChList(boolean next) {
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        MtkTvChannelInfoBase chInfo = null;
        if (this.mChannelAdapter != null && this.mChannelAdapter.getCount() > 0) {
            if (next) {
                chInfo = this.mChannelAdapter.getItem(this.mChannelAdapter.getCount() - 1).mMtkTvChannelInfo;
                if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0 || this.CURRENT_CHANNEL_TYPE == 5) {
                    list = getChannelList(chInfo.getChannelId(), 2, 7, this.mCurMask, this.mCurVal);
                } else {
                    list = this.commonIntegration.getChannelListByMaskAndSat(chInfo.getChannelId(), 2, 7, this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId);
                }
            } else {
                chInfo = this.mChannelAdapter.getItem(0).mMtkTvChannelInfo;
                if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0 || this.CURRENT_CHANNEL_TYPE == 5) {
                    list = getChannelList(chInfo.getChannelId(), 3, 7, this.mCurMask, this.mCurVal);
                } else {
                    list = this.commonIntegration.getChannelListByMaskAndSat(chInfo.getChannelId(), 3, 7, this.mCurMask, this.mCurVal, this.mCurrentSatelliteRecordId);
                }
            }
        }
        if (!(this.mChannelAdapter == null || chInfo == null)) {
            saveLastPosition(chInfo.getChannelId(), TIFFunctionUtil.getTIFChannelList(list));
        }
        return list;
    }

    /* access modifiers changed from: private */
    public List<TIFChannelInfo> getNextPrePageChListByTIF(boolean next) {
        int channelId;
        int channelId2;
        int channelId3;
        if (!this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") || !this.commonIntegration.isDualTunerEnable() || !this.commonIntegration.isCurrentSourceTv()) {
            List<TIFChannelInfo> mTifChannelList = null;
            TIFChannelInfo chInfo = null;
            if (this.mChannelAdapter != null && this.mChannelAdapter.getCount() > 0) {
                if (next) {
                    chInfo = this.mChannelAdapter.getItem(this.mChannelAdapter.getCount() - 1);
                    if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0 || this.CURRENT_CHANNEL_TYPE == 5) {
                        if (chInfo.mMtkTvChannelInfo != null) {
                            channelId3 = chInfo.mMtkTvChannelInfo.getChannelId();
                        } else {
                            channelId3 = (int) chInfo.mId;
                        }
                        mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(channelId3, false, false, 7, this.mCurMask, this.mCurVal);
                    } else {
                        mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(chInfo.mMtkTvChannelInfo.getChannelId(), false, false, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                    }
                } else {
                    chInfo = this.mChannelAdapter.getItem(0);
                    if (!this.commonIntegration.isGeneralSatMode() || this.mCurrentSatelliteRecordId <= 0 || this.CURRENT_CHANNEL_TYPE == 5) {
                        if (chInfo.mMtkTvChannelInfo != null) {
                            channelId2 = chInfo.mMtkTvChannelInfo.getChannelId();
                        } else {
                            channelId2 = (int) chInfo.mId;
                        }
                        mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelList(channelId2, true, false, 7, this.mCurMask, this.mCurVal);
                    } else {
                        mTifChannelList = this.mTIFChannelManager.getTIFPreOrNextChannelListBySateRecId(chInfo.mMtkTvChannelInfo.getChannelId(), true, false, this.mCurrentSatelliteRecordId, 7, this.mCurMask, this.mCurVal);
                    }
                }
            }
            if (!(this.mChannelAdapter == null || chInfo == null)) {
                if (!this.commonIntegration.is3rdTVSource()) {
                    if (chInfo.mMtkTvChannelInfo != null) {
                        channelId = chInfo.mMtkTvChannelInfo.getChannelId();
                    } else {
                        channelId = (int) chInfo.mId;
                    }
                    saveLastPosition(channelId, mTifChannelList);
                } else {
                    saveLastPosition((int) chInfo.mId, mTifChannelList);
                }
            }
            return mTifChannelList;
        }
        int tempmask = this.mCurMask;
        int tempVal = this.mCurVal;
        if (this.mCurMask == CommonIntegration.CH_LIST_MASK && this.mCurVal == CommonIntegration.CH_LIST_VAL) {
            this.mCurMask = CommonIntegration.CH_LIST_DIGITAL_RADIO_MASK;
            this.mCurVal = CommonIntegration.CH_LIST_DIGITAL_RADIO_VAL;
        }
        List<MtkTvChannelInfoBase> mchannelbase = getNextPrePageChList(next);
        MtkLog.d(TAG, "getNextPrePageChListByTIF getCurrentFocus() mchannelbase= " + mchannelbase.size());
        this.mCurMask = tempmask;
        this.mCurVal = tempVal;
        return this.mTIFChannelManager.getTIFChannelList(mchannelbase);
    }

    private List<MtkTvChannelInfoBase> getAllChannelList(int chID, int dir, int count) {
        MtkLog.d(TAG, "getAllChannelList chID = " + chID + "dir =" + dir + "count = " + count);
        return this.commonIntegration.getChListByMask(chID, dir, count, CommonIntegration.CH_LIST_MASK, CommonIntegration.CH_LIST_VAL);
    }

    private List<TIFChannelInfo> getAllChannelListByTIF(int chID, boolean isPrePage, int count) {
        MtkLog.d(TAG, "getAllChannelList chID = " + chID);
        return this.mTIFChannelManager.getTIFPreOrNextChannelList(chID, isPrePage, true, count, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
    }

    /* access modifiers changed from: private */
    public List<MtkTvChannelInfoBase> getChannelList(int chID, int dir, int count, int mask, int val) {
        int nextCount;
        int prevCount;
        MtkLog.d(TAG, "getChannelList chID = " + chID + "dir =" + dir + "count = " + count + " mask = " + mask + " val =" + val);
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 2:
            case 3:
                return this.commonIntegration.getChListByMask(chID, dir, count, mask, val);
            case 1:
                int id = 0;
                if (dir != 0) {
                    switch (dir) {
                        case 2:
                            id = chID + 1;
                            prevCount = 0;
                            nextCount = count;
                            break;
                        case 3:
                            id = chID;
                            prevCount = count;
                            nextCount = 0;
                            break;
                        default:
                            prevCount = 0;
                            nextCount = count;
                            break;
                    }
                } else {
                    prevCount = 0;
                    nextCount = count;
                }
                return this.commonIntegration.getChList(id, prevCount, nextCount);
            default:
                return this.commonIntegration.getChListByMask(chID, dir, count, mask, val);
        }
    }

    private boolean isLock() {
        int showFlag = this.mtkTvPwd.PWDShow();
        MtkLog.d(TAG, "isLock showFlag = " + showFlag);
        if (showFlag == 0) {
            return true;
        }
        switch (showFlag) {
            case 2:
            case 3:
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: private */
    public void setChannelListTitle() {
        if (isSelectionMode()) {
            if ((!CommonIntegration.isEURegion() || TIFFunctionUtil.isEUPARegion()) && !CommonIntegration.isSARegion()) {
                CommonIntegration commonIntegration2 = this.commonIntegration;
                if (!CommonIntegration.isEUPARegion() || !this.commonIntegration.isCurrentSourceDTV()) {
                    if (!CommonIntegration.isUSRegion()) {
                        this.mTitleText.setText(R.string.nav_channel_list);
                        this.mYellowKeyText.setText(this.mContext.getResources().getString(R.string.nav_select_list_cn));
                        MtkLog.d(TAG, "setChannelListTitle cn!!! ");
                    } else if (this.commonIntegration.isDisableColorKey()) {
                        this.mTitleText.setText(this.mContext.getResources().getString(R.string.nav_channel_list));
                    } else if (this.titlefavN.length() > 0) {
                        MtkLog.d(TAG, "setChannelListTitle us type fav!!! ");
                        TextView textView = this.mTitleText;
                        textView.setText(this.mTitlePre + this.titlefavN);
                    } else {
                        TextView textView2 = this.mTitleText;
                        textView2.setText(this.mTitlePre + this.types[this.CURRENT_CHANNEL_TYPE]);
                        this.mYellowKeyText.setText(this.mContext.getResources().getString(R.string.nav_select_type));
                        MtkLog.d(TAG, "setChannelListTitle us!!! ");
                    }
                    MtkLog.d(TAG, "setChannelListTitle mYellowKeyText " + this.mYellowKeyText.getVisibility());
                }
            }
            if (this.titlefavN.length() > 0) {
                MtkLog.d(TAG, "setChannelListTitle eu eupadtv sa  type fav!!! ");
                TextView textView3 = this.mTitleText;
                textView3.setText(this.mTitlePre + this.titlefavN);
            } else if (this.typesSecond == null || this.typesSecond.size() <= 0 || this.typesSecond.size() <= this.CURRENT_CHANNEL_SECOND_TYPE) {
                TextView textView4 = this.mTitleText;
                textView4.setText(this.mTitlePre + this.types[this.CURRENT_CHANNEL_TYPE]);
            } else {
                TextView textView5 = this.mTitleText;
                textView5.setText(this.mTitlePre + this.typesSecond.get(this.CURRENT_CHANNEL_SECOND_TYPE));
            }
            MtkLog.d(TAG, "setChannelListTitle eu eupadtv sa!!! ");
            this.mYellowKeyText.setText(this.mContext.getResources().getString(R.string.nav_select_more));
            MtkLog.d(TAG, "setChannelListTitle mYellowKeyText " + this.mYellowKeyText.getVisibility());
        } else {
            this.mTitleText.setText(R.string.nav_channel_list);
            this.mYellowKeyText.setText(this.mContext.getResources().getString(R.string.nav_select_list_cn));
        }
        if (this.mCurMask == -1) {
            this.mBlueKeyText.setVisibility(4);
            this.mBlueicon.setVisibility(4);
            return;
        }
        this.mBlueKeyText.setVisibility(0);
        this.mBlueicon.setVisibility(0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a4, code lost:
        if (com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion() != false) goto L_0x00a6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isChannel() {
        /*
            r6 = this;
            r0 = 1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "type_"
            r1.append(r2)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r6.commonIntegration
            int r2 = r2.getSvl()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            CH_TYPE = r1
            java.lang.String r1 = "ChannelListDialog"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Channel list dialog isKeyHandler CH_TYPE "
            r2.append(r3)
            java.lang.String r3 = CH_TYPE
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            r6.initTypes()
            boolean r1 = r6.mIsTifFunction
            if (r1 == 0) goto L_0x0107
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.isDualChannellist()
            if (r1 == 0) goto L_0x004c
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.hasActiveChannel()
            if (r1 != 0) goto L_0x0118
            r0 = 0
            goto L_0x0118
        L_0x004c:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r1 = r6.mTIFChannelManager
            boolean r1 = r1.hasActiveChannel()
            if (r1 == 0) goto L_0x0096
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 != 0) goto L_0x005d
            goto L_0x0096
        L_0x005d:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r1 = r6.mTIFChannelManager
            java.util.List r1 = r1.get3RDChannelList()
            int r1 = r1.size()
            if (r1 != 0) goto L_0x0118
            java.lang.String r1 = "ChannelListDialog"
            java.lang.String r2 = "mTIFChannelManager.get3RDChannelList().size() is 0 deal channel list type "
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            com.mediatek.wwtv.tvcenter.util.SaveValue r1 = r6.mSaveValue
            java.lang.String r2 = "channelListfortype"
            r3 = 0
            int r1 = r1.readValue(r2, r3)
            com.mediatek.wwtv.tvcenter.util.SaveValue r2 = r6.mSaveValue
            java.lang.String r4 = "channelListfortypeMaskvalue"
            int r2 = r2.readValue(r4, r3)
            r4 = -1
            if (r1 != r4) goto L_0x0094
            if (r2 != r4) goto L_0x0094
            java.lang.String r4 = "ChannelListDialog"
            java.lang.String r5 = "mChannelsFor3RDSource channel list type set broadcast 0"
            android.util.Log.d(r4, r5)
            com.mediatek.wwtv.tvcenter.util.SaveValue r4 = r6.mSaveValue
            java.lang.String r5 = CH_TYPE
            r4.saveValue((java.lang.String) r5, (int) r3)
        L_0x0094:
            goto L_0x0118
        L_0x0096:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEURegion()
            if (r1 == 0) goto L_0x00a6
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r1 == 0) goto L_0x00c9
        L_0x00a6:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r1 != 0) goto L_0x00c9
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            if (r1 != 0) goto L_0x00c9
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r1 == 0) goto L_0x00c7
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.isCurrentSourceDTVforEuPA()
            if (r1 == 0) goto L_0x00c7
            goto L_0x00c9
        L_0x00c7:
            r0 = 0
            goto L_0x0118
        L_0x00c9:
            java.lang.String r1 = "ChannelListDialog"
            java.lang.String r2 = "hasChannel  isEURegion= "
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r1 = r6.mTIFChannelManager
            java.util.List r1 = r1.get3RDChannelList()
            int r1 = r1.size()
            r2 = 1
            if (r1 < r2) goto L_0x0105
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 != 0) goto L_0x00e6
            goto L_0x0105
        L_0x00e6:
            java.lang.String[] r1 = r6.types
            if (r1 == 0) goto L_0x0118
            java.lang.String[] r1 = r6.types
            int r1 = r1.length
            if (r1 <= 0) goto L_0x0118
            java.lang.String[] r1 = r6.types
            int r1 = r1.length
            int r1 = r1 - r2
            r6.CURRENT_CHANNEL_TYPE = r1
            r6.resetMask()
            r1 = 0
            r6.typesSecond = r1
            com.mediatek.wwtv.tvcenter.util.SaveValue r1 = r6.mSaveValue
            java.lang.String r2 = CH_TYPE
            int r3 = r6.CURRENT_CHANNEL_TYPE
            r1.saveValue((java.lang.String) r2, (int) r3)
            goto L_0x0118
        L_0x0105:
            r0 = 0
            goto L_0x0118
        L_0x0107:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.hasActiveChannel()
            if (r1 == 0) goto L_0x0117
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r6.commonIntegration
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 != 0) goto L_0x0118
        L_0x0117:
            r0 = 0
        L_0x0118:
            java.lang.String r1 = "ChannelListDialog"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "hasChannel = "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog.isChannel():boolean");
    }

    public void show() {
        MtkLog.d(TAG, "show");
        super.show();
        this.mTitleText.setImportantForAccessibility(2);
        setWindowPosition();
        if (this.mChannelDetailLayout != null) {
            this.mChannelDetailLayout.setVisibility(4);
        }
        if (this.commonIntegration.isDisableColorKey()) {
            this.mChannelListTipView.setVisibility(8);
        }
        this.mCanChangeChannel = false;
        if (!(this.chanelListProgressbar == null || this.chanelListProgressbar.getVisibility() == 0)) {
            this.chanelListProgressbar.setVisibility(0);
        }
        this.commonIntegration.setChannelChangedListener(this.listener);
        this.mChannelListView.setAdapter((ListAdapter) null);
        this.mChannelListView.setVisibility(8);
        init();
    }

    public void setWindowPosition() {
        WindowManager m = getWindow().getWindowManager();
        Display display = m.getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        TypedValue sca = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_marginY, sca, true);
        float chmarginY = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_marginX, sca, true);
        float chmarginX = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_size_width, sca, true);
        float chwidth = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_size_height, sca, true);
        float chheight = sca.getFloat();
        int height = (int) (((float) display.getHeight()) * chmarginY);
        int width = (int) (((float) display.getWidth()) * chmarginX);
        int menuWidth = (int) (((float) display.getWidth()) * chwidth);
        int height2 = (int) (((float) display.getHeight()) * chheight);
        lp.width = menuWidth;
        lp.height = display.getHeight();
        int x = display.getWidth();
        WindowManager windowManager = m;
        StringBuilder sb = new StringBuilder();
        TypedValue typedValue = sca;
        sb.append("setWindowPosition menuWidth ");
        sb.append(menuWidth);
        sb.append(" x ");
        sb.append(x);
        sb.append(" display.getWidth() ");
        sb.append(display.getWidth());
        MtkLog.d(TAG, sb.toString());
        lp.x = x;
        lp.y = 0;
        window.setAttributes(lp);
    }

    /* access modifiers changed from: private */
    public void showPageUpDownView() {
        if (this.hasNextPage) {
            if (this.mChannelListPageUpDownLayout.getVisibility() != 0) {
                this.mChannelListPageUpDownLayout.setVisibility(0);
            }
        } else if (this.mChannelListPageUpDownLayout.getVisibility() != 4) {
            this.mChannelListPageUpDownLayout.setVisibility(4);
        }
    }

    public void onDismiss(DialogInterface dialog) {
        MtkLog.d(TAG, "onDismiss!!!!!!!!!");
        this.mContext = null;
        this.mChannelListView.setOnKeyListener((View.OnKeyListener) null);
        this.mChannelListView.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) null);
    }

    public void exit() {
        dismiss();
    }

    public void dismiss() {
        MtkLog.d(TAG, "dismiss!!!!!!!");
        this.isFindingForChannelList = false;
        this.mChannelSelectMoreView.setVisibility(8);
        this.mChannelSortView.setVisibility(8);
        this.mChannelTypeView.setVisibility(8);
        this.mTIFChannelManager.findChanelsForlist("", this.mCurMask, this.mCurVal);
        this.mCanChangeChannel = true;
        if (this.mChannelListView != null) {
            this.mChannelListView.setAdapter((ListAdapter) null);
            this.mChannelListView.setVisibility(0);
            this.mLastSelection = this.mChannelListView.getSelectedItemPosition();
            if (TextToSpeechUtil.isTTSEnabled(this.mContext)) {
                this.mChannelListView.setFocusable(true);
                this.mChannelListView.requestFocus();
                this.mChannelListView.setSelection(this.mLastSelection);
            }
        }
        MtkLog.d(TAG, "mLastSelection>>>>>" + this.mLastSelection);
        this.mThreadHandler.removeCallbacks(this.mResetChannelListRunnable);
        this.mThreadHandler.removeCallbacks(this.mUpdateChannelListRunnable);
        this.mThreadHandler.removeCallbacks(this.mChannelUpRunnable);
        this.mThreadHandler.removeCallbacks(this.mChannelPreRunnable);
        this.mThreadHandler.removeCallbacks(this.mChannelDownRunnable);
        if (this.mChannelAdapter != null) {
            saveLastPosition(this.commonIntegration.getCurrentChannelId(), this.mChannelAdapter.getChannellist());
        }
        super.dismiss();
        if (this.commonIntegration.isShowFAVListFullToastDealy() && this.commonIntegration.isFavListFull()) {
            MtkLog.d(TAG, "dismiss!!!!!!!+ show fav list is full");
            TurnkeyUiMainActivity.getInstance().getHandler().removeMessages(102);
            TurnkeyUiMainActivity.getInstance().getHandler().sendEmptyMessage(102);
            this.commonIntegration.setShowFAVListFullToastDealy(false);
        }
    }

    class ChannelListOnKey implements View.OnKeyListener {
        ChannelListOnKey() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int id = v.getId();
            if (id != R.id.nav_channel_typeview) {
                switch (id) {
                    case R.id.nav_channel_listview:
                        int slectPosition = ChannelListDialog.this.mChannelListView.getSelectedItemPosition();
                        MtkLog.v(ChannelListDialog.TAG, "ChannelListOnKey keyCode =" + keyCode + " slectPosition = " + slectPosition + "mChannelListView =" + ChannelListDialog.this.mChannelListView);
                        if (ChannelListDialog.this.mChannelListView != null) {
                            MtkLog.d(ChannelListDialog.TAG, "onKey mChannelListView.getChildAt(slectPosition) =" + ChannelListDialog.this.mChannelListView.getChildAt(slectPosition));
                        }
                        if (slectPosition >= 0 && ChannelListDialog.this.mChannelListView.getChildAt(slectPosition) != null && event.getAction() == 0) {
                            if (keyCode == 23) {
                                ChannelListDialog.this.startTimeout(10000);
                                MtkLog.v(ChannelListDialog.TAG, "mChannelItemKeyLsner*********** slectPosition = " + ChannelListDialog.this.mChannelListView.getSelectedItemPosition());
                                if (TextToSpeechUtil.isTTSEnabled(ChannelListDialog.this.mContext)) {
                                    return true;
                                }
                                TIFChannelInfo tIFChannelInfo = (TIFChannelInfo) ChannelListDialog.this.mChannelListView.getSelectedItem();
                                if (tIFChannelInfo.mMtkTvChannelInfo != null) {
                                    final MtkTvChannelInfoBase selectedChannel = tIFChannelInfo.mMtkTvChannelInfo;
                                    if (!selectedChannel.equals(ChannelListDialog.this.commonIntegration.getCurChInfo())) {
                                        if (DvrManager.getInstance() == null || !DvrManager.getInstance().pvrIsRecording()) {
                                            MtkLog.e(ChannelListDialog.TAG, "channelID:-2,ID:" + selectedChannel.getChannelId());
                                            MtkLog.e(ChannelListDialog.TAG, "channelID:-2,Name:" + selectedChannel.getServiceName());
                                            ChannelListDialog.this.selectTifChannel(keyCode, tIFChannelInfo);
                                        } else {
                                            String srctype = DvrManager.getInstance().getController().getSrcType();
                                            if (srctype.equals("TV") || InputSourceManager.getInstance(ChannelListDialog.this.mContext).getConflictSourceList().contains(srctype) || tIFChannelInfo.mType.equals("1")) {
                                                DvrDialog conDialog = new DvrDialog((Activity) ChannelListDialog.this.mContext, 40961, keyCode, 1);
                                                MtkLog.e(ChannelListDialog.TAG, "channelID:-1,ID:" + selectedChannel.getChannelId());
                                                MtkLog.e(ChannelListDialog.TAG, "channelID:-1,Name:" + selectedChannel.getServiceName());
                                                conDialog.setMtkTvChannelInfoBase(selectedChannel.getChannelId());
                                                conDialog.setOnPVRDialogListener(new OnDVRDialogListener() {
                                                    public void onDVRDialogListener(int keyCode) {
                                                        MtkLog.d(ChannelListDialog.TAG, "OnPVRDialogListener keyCode>>>" + keyCode);
                                                        if (keyCode == 23) {
                                                            Message msg = Message.obtain();
                                                            msg.what = 4101;
                                                            msg.arg1 = selectedChannel.getChannelId();
                                                            ChannelListDialog.this.mHandler.sendMessageDelayed(msg, 3000);
                                                        }
                                                    }
                                                });
                                                conDialog.show();
                                                ChannelListDialog.this.dismiss();
                                            } else {
                                                ChannelListDialog.this.selectTifChannel(keyCode, tIFChannelInfo);
                                            }
                                        }
                                    }
                                } else {
                                    TIFChannelInfo currentTIFChannelInfo = ChannelListDialog.this.mTIFChannelManager.getCurrChannelInfo();
                                    if ((currentTIFChannelInfo == null || tIFChannelInfo.mId == currentTIFChannelInfo.mId) && currentTIFChannelInfo != null) {
                                        MtkLog.e(ChannelListDialog.TAG, "is3rdTVSource channelis null ");
                                    } else {
                                        MtkLog.e(ChannelListDialog.TAG, "is3rdTVSource channelID:-2,ID:" + tIFChannelInfo.mId);
                                        MtkLog.e(ChannelListDialog.TAG, "is3rdTVSource channelID:-2,Name:" + tIFChannelInfo.mDisplayName);
                                        ChannelListDialog.this.selectTifChannel(keyCode, tIFChannelInfo);
                                    }
                                }
                                return true;
                            } else if (keyCode != 93) {
                                switch (keyCode) {
                                    case 19:
                                        MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_UP!!!!!");
                                        ChannelListDialog.this.mChannelListView.getChildAt(slectPosition).requestFocusFromTouch();
                                        ChannelListDialog.this.mChannelDetailLayout.setVisibility(4);
                                        if (slectPosition != 0 || !ChannelListDialog.this.hasNextPage) {
                                            ChannelListDialog.this.startTimeout(10000);
                                            if (ChannelListDialog.this.mChannelListView.getSelectedItemPosition() != 0) {
                                                return false;
                                            }
                                            ChannelListDialog.this.mChannelListView.setSelection(ChannelListDialog.this.mChannelAdapter.getCount() - 1);
                                            return true;
                                        }
                                        if (ChannelListDialog.this.mIsTifFunction) {
                                            ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.getNextPrePageChListByTIF(false));
                                        } else {
                                            ChannelListDialog.this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(ChannelListDialog.this.getNextPrePageChList(false)));
                                        }
                                        ChannelListDialog.this.mChannelListView.setSelection(ChannelListDialog.this.mChannelAdapter.getCount() - 1);
                                        ChannelListDialog.this.startTimeout(10000);
                                        return true;
                                    case 20:
                                        MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_DOWN!!!!!");
                                        ChannelListDialog.this.mChannelListView.getChildAt(slectPosition).requestFocusFromTouch();
                                        ChannelListDialog.this.mChannelDetailLayout.setVisibility(4);
                                        if (slectPosition != ChannelListDialog.this.mChannelAdapter.getCount() - 1 || !ChannelListDialog.this.hasNextPage) {
                                            ChannelListDialog.this.startTimeout(10000);
                                            if (ChannelListDialog.this.mChannelListView.getSelectedItemPosition() != ChannelListDialog.this.mChannelAdapter.getCount() - 1) {
                                                return false;
                                            }
                                            ChannelListDialog.this.mChannelListView.setSelection(0);
                                            return true;
                                        }
                                        if (ChannelListDialog.this.mIsTifFunction) {
                                            ChannelListDialog.this.mChannelAdapter.updateData(ChannelListDialog.this.getNextPrePageChListByTIF(true));
                                        } else {
                                            ChannelListDialog.this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(ChannelListDialog.this.getNextPrePageChList(true)));
                                        }
                                        ChannelListDialog.this.mChannelListView.setSelection(0);
                                        ChannelListDialog.this.startTimeout(10000);
                                        return true;
                                }
                            } else {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_PAGE_DOWN!!!!!");
                                ChannelListDialog.this.dismiss();
                                return false;
                            }
                        }
                        break;
                    case R.id.nav_channel_satellitetypesecondview:
                        MtkLog.d(ChannelListDialog.TAG, "nav_channel_satellitetypesecondview !!!!!");
                        if (event.getAction() == 0) {
                            ChannelListDialog.this.startTimeout(10000);
                            if (keyCode != 23) {
                                switch (keyCode) {
                                    case 19:
                                        if (ChannelListDialog.this.mChannelSatelliteSecondView.getSelectedItemPosition() == 0) {
                                            ChannelListDialog.this.mChannelSatelliteSecondView.setSelection(ChannelListDialog.this.typesSecond.size() - 1);
                                            return true;
                                        }
                                        break;
                                    case 20:
                                        MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_DOWN position = " + ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition());
                                        if (ChannelListDialog.this.mChannelSatelliteSecondView.getSelectedItemPosition() == ChannelListDialog.this.typesSecond.size() - 1) {
                                            ChannelListDialog.this.mChannelSatelliteSecondView.setSelection(0);
                                            return true;
                                        }
                                        break;
                                    default:
                                        switch (keyCode) {
                                            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                                return true;
                                        }
                                }
                            } else if (ChannelListDialog.this.CURRENT_CHANNEL_SECOND_TYPE == ChannelListDialog.this.mChannelSatelliteSecondView.getSelectedItemPosition()) {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position == CURRENT_CHANNEL_SECOND_TYPE not deal");
                                return true;
                            } else {
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position = " + ChannelListDialog.this.mChannelSatelliteSecondView.getSelectedItemPosition());
                                int unused = ChannelListDialog.this.CURRENT_CHANNEL_SECOND_TYPE = ChannelListDialog.this.mChannelSatelliteSecondView.getSelectedItemPosition();
                                ChannelListDialog.this.resetCategories();
                                MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER mCurCategories = " + ChannelListDialog.this.mCurCategories);
                                ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.CH_TYPE_SECOND, ChannelListDialog.this.CURRENT_CHANNEL_SECOND_TYPE);
                                ChannelListDialog.this.mChannelListView.setAdapter((ListAdapter) null);
                                ChannelListDialog.this.mChannelListView.setVisibility(0);
                                ChannelListDialog.this.mChannelSatelliteSecondView.setVisibility(8);
                                if (ChannelListDialog.this.mIsTifFunction) {
                                    MtkLog.d(ChannelListDialog.TAG, "ChannelListOnKey = " + ChannelListDialog.this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") + "  isCurrentSourceTv = " + ChannelListDialog.this.commonIntegration.isCurrentSourceTv());
                                    if (!ChannelListDialog.this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") || !MarketRegionInfo.isFunctionSupport(32) || !ChannelListDialog.this.commonIntegration.isCurrentSourceTv()) {
                                        ChannelListDialog.this.mChannelListView.setVisibility(8);
                                        boolean unused2 = ChannelListDialog.this.SELECT_TYPE_CHANGE_CH = true;
                                        if (!(ChannelListDialog.this.chanelListProgressbar == null || ChannelListDialog.this.chanelListProgressbar.getVisibility() == 0)) {
                                            ChannelListDialog.this.chanelListProgressbar.setVisibility(0);
                                        }
                                        ChannelListDialog.this.resetChListByTIF();
                                    } else {
                                        ChannelListDialog.this.resetChList();
                                    }
                                } else {
                                    ChannelListDialog.this.resetChList();
                                }
                                return true;
                            }
                        }
                        break;
                    case R.id.nav_channel_satellitetypeview:
                        if (event.getAction() == 0) {
                            ChannelListDialog.this.startTimeout(10000);
                            if (keyCode != 23) {
                                switch (keyCode) {
                                    case 19:
                                        if (ChannelListDialog.this.mChannelSatelliteView.getSelectedItemPosition() == 0) {
                                            ChannelListDialog.this.mChannelSatelliteView.setSelection(ChannelListDialog.this.mChannelSatelliteView.getChildCount() - 1);
                                            return true;
                                        }
                                        break;
                                    case 20:
                                        MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_DOWN position = " + ChannelListDialog.this.mChannelSatelliteView.getSelectedItemPosition());
                                        if (ChannelListDialog.this.mChannelSatelliteView.getSelectedItemPosition() == ChannelListDialog.this.mChannelSatelliteView.getChildCount() - 1) {
                                            ChannelListDialog.this.mChannelSatelliteView.setSelection(0);
                                            return true;
                                        }
                                        break;
                                    default:
                                        switch (keyCode) {
                                            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                                return true;
                                        }
                                }
                            } else {
                                int unused3 = ChannelListDialog.this.mCurrentSatelliteRecordId = ((MtkTvDvbsConfigInfoBase) ChannelListDialog.this.mSatelliteListinfo.get(ChannelListDialog.this.mChannelSatelliteView.getSelectedItemPosition())).getSatlRecId();
                                MtkLog.d(ChannelListDialog.TAG, "mCurrentSatelliteRecordId>>>" + ChannelListDialog.this.mCurrentSatelliteRecordId);
                                ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.SATELLITE_RECORDID, ChannelListDialog.this.mCurrentSatelliteRecordId);
                                ChannelListDialog.this.mChannelListView.setAdapter((ListAdapter) null);
                                ChannelListDialog.this.mChannelListView.setVisibility(0);
                                ChannelListDialog.this.mChannelSatelliteView.setVisibility(8);
                                if (ChannelListDialog.this.mIsTifFunction) {
                                    ChannelListDialog.this.resetChListByTIF();
                                } else {
                                    ChannelListDialog.this.resetChList();
                                }
                                return true;
                            }
                        }
                        break;
                    case R.id.nav_channel_select_more:
                        if (event.getAction() == 0) {
                            ChannelListDialog.this.startTimeout(10000);
                            switch (keyCode) {
                                case 19:
                                    if (ChannelListDialog.this.mChannelSelectMoreView.getSelectedItemPosition() == 0) {
                                        ChannelListDialog.this.mChannelSelectMoreView.setSelection(ChannelListDialog.this.mChannelSelectMoreView.getChildCount() - 1);
                                        ChannelListDialog.this.startTimeout(10000);
                                        return true;
                                    }
                                    break;
                                case 20:
                                    MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_DOWN position = " + ChannelListDialog.this.mChannelSelectMoreView.getSelectedItemPosition());
                                    if (ChannelListDialog.this.mChannelSelectMoreView.getSelectedItemPosition() == ChannelListDialog.this.mChannelSelectMoreView.getChildCount() - 1) {
                                        ChannelListDialog.this.mChannelSelectMoreView.setSelection(0);
                                        ChannelListDialog.this.startTimeout(10000);
                                        return true;
                                    }
                                    break;
                                default:
                                    switch (keyCode) {
                                        case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                        case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                        case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                        case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                            return true;
                                    }
                            }
                        }
                        break;
                    case R.id.nav_channel_sort:
                        if (event.getAction() == 0) {
                            ChannelListDialog.this.startTimeout(10000);
                            if (keyCode != 23) {
                                switch (keyCode) {
                                    case 19:
                                        if (ChannelListDialog.this.mChannelSortView.getSelectedItemPosition() == 0) {
                                            ChannelListDialog.this.mChannelSortView.setSelection(ChannelListDialog.this.mChannelSortView.getChildCount() - 1);
                                            ChannelListDialog.this.startTimeout(10000);
                                            return true;
                                        }
                                        break;
                                    case 20:
                                        MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_DOWN position = " + ChannelListDialog.this.mChannelSortView.getSelectedItemPosition());
                                        if (ChannelListDialog.this.mChannelSortView.getSelectedItemPosition() == ChannelListDialog.this.mChannelSortView.getChildCount() - 1) {
                                            ChannelListDialog.this.mChannelSortView.setSelection(0);
                                            ChannelListDialog.this.startTimeout(10000);
                                            return true;
                                        }
                                        break;
                                    default:
                                        switch (keyCode) {
                                            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                                return true;
                                        }
                                }
                            } else if (TextToSpeechUtil.isTTSEnabled(ChannelListDialog.this.mContext)) {
                                return true;
                            } else {
                                if (ChannelListDialog.this.CURRENT_CHANNEL_SORT == ChannelListDialog.this.mChannelSortView.getSelectedItemPosition()) {
                                    MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position == CURRENT_CHANNEL_SORT not deal");
                                    return true;
                                }
                                ChannelListDialog.this.mTIFChannelManager.findChanelsForlist("", ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal);
                                int unused4 = ChannelListDialog.this.CURRENT_CHANNEL_SORT = ChannelListDialog.this.mChannelSortView.getSelectedItemPosition();
                                MtkLog.d(ChannelListDialog.TAG, "CURRENT_CHANNEL_SORT>>>" + ChannelListDialog.this.CURRENT_CHANNEL_SORT);
                                ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.CH_SORT, ChannelListDialog.this.CURRENT_CHANNEL_SORT);
                                ChannelListDialog.this.mChannelListView.setAdapter((ListAdapter) null);
                                ChannelListDialog.this.mChannelListView.setVisibility(0);
                                ChannelListDialog.this.mChannelSortView.setVisibility(8);
                                ChannelListDialog.this.mTIFChannelManager.setCurrentChannelSort(ChannelListDialog.this.CURRENT_CHANNEL_SORT);
                                if (ChannelListDialog.this.mIsTifFunction) {
                                    ChannelListDialog.this.resetChListByTIF();
                                } else {
                                    ChannelListDialog.this.resetChList();
                                }
                                return true;
                            }
                        }
                        break;
                }
            } else if (event.getAction() == 0) {
                ChannelListDialog.this.startTimeout(10000);
                if (keyCode != 23) {
                    switch (keyCode) {
                        case 19:
                            ChannelListDialog.this.startTimeout(10000);
                            if (ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() == 0) {
                                ChannelListDialog.this.mChannelTypeView.setSelection(ChannelListDialog.this.mChannelTypeView.getCount() - 1);
                                ChannelListDialog.this.startTimeout(10000);
                                return true;
                            }
                            break;
                        case 20:
                            MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_DOWN position = " + ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition());
                            if (ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() == ChannelListDialog.this.mChannelTypeView.getCount() - 1) {
                                ChannelListDialog.this.mChannelTypeView.setSelection(0);
                                ChannelListDialog.this.startTimeout(10000);
                                return true;
                            }
                            break;
                        default:
                            switch (keyCode) {
                                case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                    return true;
                            }
                    }
                } else if (TextToSpeechUtil.isTTSEnabled(ChannelListDialog.this.mContext)) {
                    return true;
                } else {
                    ChannelListDialog.this.mHandler.removeMessages(4114);
                    MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position = " + ChannelListDialog.this.CURRENT_CHANNEL_TYPE);
                    MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER commonIntegration.isOperatorNTVPLUS() = " + ChannelListDialog.this.commonIntegration.isOperatorNTVPLUS());
                    MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER commonIntegration.isOperatorTELEKARTA() = " + ChannelListDialog.this.commonIntegration.isOperatorTELEKARTA());
                    if ((ChannelListDialog.this.commonIntegration.isOperatorNTVPLUS() || ChannelListDialog.this.commonIntegration.isOperatorTKGS()) && ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() > 0) {
                        if (ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() == 1) {
                            int unused5 = ChannelListDialog.this.CURRENT_CHANNEL_TYPE = ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition();
                            ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.CH_TYPE, ChannelListDialog.this.CURRENT_CHANNEL_TYPE);
                            ChannelListDialog.this.loadChannelListTypeSecondeView();
                            return true;
                        } else if (ChannelListDialog.this.CURRENT_CHANNEL_TYPE == ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition()) {
                            MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position == CURRENT_CHANNEL_TYPE not deal");
                            return true;
                        } else {
                            int unused6 = ChannelListDialog.this.CURRENT_CHANNEL_TYPE = ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition();
                            ChannelListDialog.this.resetMask();
                        }
                    } else if (!ChannelListDialog.this.commonIntegration.isOperatorTELEKARTA() || ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() <= 0) {
                        if (ChannelListDialog.this.CURRENT_CHANNEL_TYPE == ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition()) {
                            MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position == CURRENT_CHANNEL_TYPE not deal");
                            return true;
                        }
                        int unused7 = ChannelListDialog.this.CURRENT_CHANNEL_TYPE = ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition();
                        ChannelListDialog.this.resetMask();
                    } else if (ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() == 2 || ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition() == 1) {
                        int unused8 = ChannelListDialog.this.CURRENT_CHANNEL_TYPE = ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition();
                        ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.CH_TYPE, ChannelListDialog.this.CURRENT_CHANNEL_TYPE);
                        ChannelListDialog.this.loadChannelListTypeSecondeView();
                        return true;
                    } else if (ChannelListDialog.this.CURRENT_CHANNEL_TYPE == ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition()) {
                        MtkLog.d(ChannelListDialog.TAG, "KEYCODE_DPAD_CENTER position == CURRENT_CHANNEL_TYPE not deal");
                        return true;
                    } else {
                        int unused9 = ChannelListDialog.this.CURRENT_CHANNEL_TYPE = ChannelListDialog.this.mChannelTypeView.getSelectedItemPosition();
                        ChannelListDialog.this.resetMask();
                    }
                    List unused10 = ChannelListDialog.this.typesSecond = null;
                    MtkLog.d(ChannelListDialog.TAG, "ChannelListOnKey ,mCurMask: " + ChannelListDialog.this.mCurMask + " mCurVal " + ChannelListDialog.this.mCurVal);
                    if (ChannelListDialog.this.mIsTifFunction) {
                        MtkLog.d(ChannelListDialog.TAG, "ChannelListOnKey = " + ChannelListDialog.this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") + "  isCurrentSourceTv = " + ChannelListDialog.this.commonIntegration.isCurrentSourceTv());
                        if (ChannelListDialog.this.commonIntegration.getCurrentFocus().equalsIgnoreCase("sub") && MarketRegionInfo.isFunctionSupport(32) && ChannelListDialog.this.commonIntegration.isCurrentSourceTv()) {
                            ChannelListDialog.this.resetChList();
                        } else if (ChannelListDialog.this.mTIFChannelManager.getChannelListConfirmLength(1, ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal) > 0) {
                            ChannelListDialog.this.mChannelListView.setVisibility(8);
                            ChannelListDialog.this.mChannelTypeView.setVisibility(8);
                            if (ChannelListDialog.this.commonIntegration.checkCurChMask(ChannelListDialog.this.mCurMask, ChannelListDialog.this.mCurVal)) {
                                boolean unused11 = ChannelListDialog.this.SELECT_TYPE_CHANGE_CH = false;
                            } else {
                                boolean unused12 = ChannelListDialog.this.SELECT_TYPE_CHANGE_CH = true;
                            }
                            if (!(ChannelListDialog.this.chanelListProgressbar == null || ChannelListDialog.this.chanelListProgressbar.getVisibility() == 0)) {
                                ChannelListDialog.this.chanelListProgressbar.setVisibility(0);
                            }
                            ChannelListDialog.this.resetChListByTIF();
                            ChannelListDialog.this.mSaveValue.saveValue(ChannelListDialog.CH_TYPE, ChannelListDialog.this.CURRENT_CHANNEL_TYPE);
                        } else {
                            MtkLog.d(ChannelListDialog.TAG, "CHANGE_TYPE_CHANGECHANNEL current type no chanenls,return last type");
                            Toast.makeText(ChannelListDialog.this.mContext, ChannelListDialog.this.mContext.getResources().getString(R.string.nav_select_type_no_channel), 0).show();
                        }
                    } else {
                        ChannelListDialog.this.resetChList();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void selectTifChannel(int keyCode, TIFChannelInfo selectedChannel) {
        if (keyCode == 23) {
            this.mChannelDetailLayout.setVisibility(4);
            if (this.commonIntegration.isdualtunermode()) {
                if (selectedChannel.mMtkTvChannelInfo != null) {
                    MtkLog.d(TAG, "selectTifChannel isUp = TIFChannelInfo  " + selectedChannel.mMtkTvChannelInfo.getChannelId() + "  " + MtkTvTISMsgBase.createSvlChannelUri((long) selectedChannel.mMtkTvChannelInfo.getChannelId()));
                    TurnkeyUiMainActivity.getInstance().getPipView().tune(InputSourceManager.getInstance().getTvInputInfo("sub").getId(), MtkTvTISMsgBase.createSvlChannelUri((long) selectedChannel.mMtkTvChannelInfo.getChannelId()));
                    int channelid = this.commonIntegration.get2NDCurrentChannelId();
                    MtkLog.d(TAG, "selectTifChannel channelid = " + channelid);
                    if (channelid == selectedChannel.mMtkTvChannelInfo.getChannelId()) {
                        this.mLastSelection = this.mChannelListView.getSelectedItemPosition();
                        if (this.mChannelAdapter != null) {
                            saveLastPosition(selectedChannel.mMtkTvChannelInfo.getChannelId(), this.mChannelAdapter.getChannellist());
                            return;
                        }
                        return;
                    }
                    return;
                }
                MtkLog.d(TAG, "selectTifChannel isdualtunermode is support netowrk channels");
            } else if (this.mTIFChannelManager.selectChannelByTIFInfo(selectedChannel)) {
                dismiss();
            }
        }
    }

    public void selectChannel(int keyCode, MtkTvChannelInfoBase selectedChannel) {
        MtkLog.e(TAG, "selectChannel(),keyCode:" + keyCode + ",selectedChannel:" + selectedChannel.getChannelId());
        if (keyCode != 23) {
            if (keyCode == 229) {
                int seletcPostion = this.mChannelListView.getSelectedItemPosition();
                MtkLog.e(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_PRECH");
                if (this.commonIntegration.channelPre()) {
                    ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                    int curChId = this.commonIntegration.getCurrentChannelId();
                    if (this.mChannelAdapter != null) {
                        int chIndex = this.mChannelAdapter.isExistCh(curChId);
                        MtkLog.d(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_PRECH  chIndex = " + chIndex);
                        if (chIndex >= 0) {
                            this.mChannelListView.requestFocus();
                            this.mChannelListView.setSelection(chIndex);
                            this.mLastSelection = chIndex;
                        } else if (seletcPostion != 0) {
                            MtkLog.d(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_PRECH  2step ");
                            if (this.mIsTifFunction) {
                                this.mChannelAdapter.updateData(getAllChannelListByTIF(curChId, false, 7));
                            } else {
                                this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(getAllChannelList(curChId, 2, 7)));
                            }
                            this.mChannelListView.requestFocus();
                            this.mChannelListView.setSelection(0);
                            this.mLastSelection = 0;
                        } else {
                            MtkLog.d(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_PRECH  3step ");
                            if (this.mIsTifFunction) {
                                this.mChannelAdapter.updateData(getAllChannelListByTIF(curChId, true, 7));
                            } else {
                                this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(getAllChannelList(curChId, 3, 7)));
                            }
                            this.mChannelListView.requestFocus();
                            this.mChannelListView.setSelection(this.mChannelAdapter.getCount() - 1);
                            this.mLastSelection = this.mChannelAdapter.getCount() - 1;
                        }
                        saveLastPosition(curChId, this.mChannelAdapter.getChannellist());
                        return;
                    }
                    return;
                }
                return;
            } else if (keyCode != 11238) {
                switch (keyCode) {
                    case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                        MtkLog.e(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_CHUP");
                        if (this.commonIntegration.channelUp()) {
                            ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                            int curChId2 = this.commonIntegration.getCurrentChannelId();
                            if (this.mChannelAdapter != null) {
                                int chIndex2 = this.mChannelAdapter.isExistCh(curChId2);
                                MtkLog.e(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_CHUP chIndex = " + chIndex2);
                                if (chIndex2 >= 0) {
                                    this.mChannelListView.requestFocus();
                                    this.mChannelListView.setSelection(chIndex2);
                                    this.mLastSelection = chIndex2;
                                } else {
                                    if (this.mIsTifFunction) {
                                        this.mChannelAdapter.updateData(getAllChannelListByTIF(curChId2, false, 7));
                                    } else {
                                        this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(getAllChannelList(curChId2, 2, 7)));
                                    }
                                    this.mChannelListView.requestFocus();
                                    this.mChannelListView.setSelection(0);
                                    this.mLastSelection = 0;
                                }
                                saveLastPosition(curChId2, this.mChannelAdapter.getChannellist());
                                return;
                            }
                            return;
                        }
                        return;
                    case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                        MtkLog.e(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_CHDN");
                        if (this.commonIntegration.channelDown()) {
                            ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                            int curChId3 = this.commonIntegration.getCurrentChannelId();
                            if (this.mChannelAdapter != null) {
                                int chIndex3 = this.mChannelAdapter.isExistCh(curChId3);
                                MtkLog.d(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_CHDN chIndex = " + chIndex3);
                                if (chIndex3 >= 0) {
                                    this.mChannelListView.requestFocus();
                                    this.mChannelListView.setSelection(chIndex3);
                                    this.mLastSelection = chIndex3;
                                } else {
                                    if (this.mIsTifFunction) {
                                        this.mChannelAdapter.updateData(getAllChannelListByTIF(curChId3, true, 7));
                                    } else {
                                        this.mChannelAdapter.updateData(TIFFunctionUtil.getTIFChannelList(getAllChannelList(curChId3, 3, 7)));
                                    }
                                    MtkLog.d(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_CHDN step 0");
                                    this.mChannelListView.requestFocus();
                                    this.mChannelListView.setSelection(this.mChannelAdapter.getCount() - 1);
                                    this.mLastSelection = this.mChannelAdapter.getCount() - 1;
                                }
                                MtkLog.d(TAG, "KeyHandler(),keycode==KeyMap.KEYCODE_MTKIR_CHDN step 1");
                                saveLastPosition(curChId3, this.mChannelAdapter.getChannellist());
                                return;
                            }
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
        this.mChannelDetailLayout.setVisibility(4);
        if (this.commonIntegration.selectChannelByInfo(selectedChannel)) {
            this.mLastSelection = this.mChannelListView.getSelectedItemPosition();
            if (this.mChannelAdapter != null) {
                saveLastPosition(selectedChannel.getChannelId(), this.mChannelAdapter.getChannellist());
            }
        }
    }

    public boolean isKeyHandler(int keyCode) {
        BannerView bannerView;
        if ((keyCode == 23 || keyCode == 216) && isChannel() && !isLock()) {
            CommonIntegration commonIntegration2 = this.commonIntegration;
            if (!CommonIntegration.isEUPARegion() || !this.commonIntegration.isCurrentSourceATVforEuPA()) {
                CommonIntegration commonIntegration3 = this.commonIntegration;
                if (!CommonIntegration.isCNRegion() || !this.commonIntegration.isCurrentSourceATV()) {
                    initMaskAndSatellites();
                    MtkLog.d(TAG, "Channel list dialog isKeyHandler return true");
                    return true;
                }
            }
            CommonIntegration commonIntegration4 = this.commonIntegration;
            this.mCurMask = CommonIntegration.CH_LIST_ANALOG_MASK;
            CommonIntegration commonIntegration5 = this.commonIntegration;
            this.mCurVal = CommonIntegration.CH_LIST_ANALOG_VAL;
            this.mCurCategories = -1;
            this.types = new String[1];
            this.CURRENT_CHANNEL_MODE = 1;
            TIFFunctionUtil.setmCurCategories(this.mCurCategories);
            MtkLog.d(TAG, "Channel list dialog isKeyHandler return true");
            return true;
        }
        if (keyCode == 23 && (bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER)) != null && !bannerView.isVisible()) {
            bannerView.isKeyHandler(KeyMap.KEYCODE_MTKIR_INFO);
        }
        MtkLog.d(TAG, "Channel list dialog isKeyHandler return false");
        return false;
    }

    private boolean isSignal() {
        return true;
    }

    class ChannelAdapter extends BaseAdapter {
        private final String TAG = "ChannelListDialog.ChannelAdapter";
        private Context mContext;
        private List<TIFChannelInfo> mCurrentTifChannelList;
        private LayoutInflater mInflater;

        public ChannelAdapter(Context context, List<TIFChannelInfo> currentTifChannelList) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
            this.mCurrentTifChannelList = currentTifChannelList;
        }

        public List<TIFChannelInfo> getChannellist() {
            return this.mCurrentTifChannelList;
        }

        public int getCount() {
            if (this.mCurrentTifChannelList != null) {
                return this.mCurrentTifChannelList.size();
            }
            return 0;
        }

        public int isExistCh(int chId) {
            if (this.mCurrentTifChannelList == null) {
                return -1;
            }
            int size = this.mCurrentTifChannelList.size();
            for (int index = 0; index < size; index++) {
                if ((this.mCurrentTifChannelList.get(index).mMtkTvChannelInfo != null && this.mCurrentTifChannelList.get(index).mMtkTvChannelInfo.getChannelId() == chId) || this.mCurrentTifChannelList.get(index).mId == ((long) chId)) {
                    return index;
                }
            }
            return -1;
        }

        public TIFChannelInfo getItem(int position) {
            return this.mCurrentTifChannelList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public void updateData(List<TIFChannelInfo> currentChannelList) {
            this.mCurrentTifChannelList = currentChannelList;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder hodler;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nav_channel_item, (ViewGroup) null);
                hodler = new ViewHolder();
                hodler.mChannelNumberTextView = (TextView) convertView.findViewById(R.id.nav_channel_list_item_NumberTV);
                hodler.mChannelNameTextView = (TextView) convertView.findViewById(R.id.nav_channel_list_item_NameTV);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHolder) convertView.getTag();
            }
            TIFChannelInfo mCurrentChannel = this.mCurrentTifChannelList.get(position);
            if (ChannelListDialog.this.mIsTifFunction) {
                if (ChannelListDialog.this.commonIntegration.is3rdTVSource() && mCurrentChannel.mMtkTvChannelInfo == null) {
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else if (mCurrentChannel.mMtkTvChannelInfo != null && mCurrentChannel.mMtkTvChannelInfo.isRadioService() && MarketRegionInfo.getCurrentMarketRegion() == 3) {
                    Drawable radioIcon = this.mContext.getResources().getDrawable(R.drawable.epg_radio_channel_icon);
                    radioIcon.setBounds(0, 0, radioIcon.getMinimumWidth(), radioIcon.getMinimumWidth());
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(radioIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                } else if (mCurrentChannel.mMtkTvChannelInfo != null && (mCurrentChannel.mMtkTvChannelInfo instanceof MtkTvISDBChannelInfo)) {
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(Drawable.createFromPath(ChannelListDialog.this.commonIntegration.getISDBChannelLogo((MtkTvISDBChannelInfo) mCurrentChannel.mMtkTvChannelInfo)), (Drawable) null, (Drawable) null, (Drawable) null);
                } else if (mCurrentChannel.mMtkTvChannelInfo == null || !(mCurrentChannel.mMtkTvChannelInfo instanceof MtkTvAnalogChannelInfo)) {
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else if (MarketRegionInfo.getCurrentMarketRegion() == 2 || MarketRegionInfo.getCurrentMarketRegion() == 3) {
                    Drawable analogIcon = this.mContext.getResources().getDrawable(R.drawable.epg_channel_icon);
                    analogIcon.setBounds(0, 0, analogIcon.getMinimumWidth(), analogIcon.getMinimumWidth());
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(analogIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                } else {
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                hodler.mChannelNumberTextView.setText(mCurrentChannel.mDisplayNumber);
                hodler.mChannelNameTextView.setText(mCurrentChannel.mDisplayName);
            } else {
                if (mCurrentChannel.mMtkTvChannelInfo.isRadioService() && MarketRegionInfo.getCurrentMarketRegion() == 3) {
                    Drawable radioIcon2 = this.mContext.getResources().getDrawable(R.drawable.epg_radio_channel_icon);
                    radioIcon2.setBounds(0, 0, radioIcon2.getMinimumWidth(), radioIcon2.getMinimumWidth());
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(radioIcon2, (Drawable) null, (Drawable) null, (Drawable) null);
                    TextView textView = hodler.mChannelNumberTextView;
                    textView.setText("" + mCurrentChannel.mMtkTvChannelInfo.getChannelNumber());
                } else if (mCurrentChannel.mMtkTvChannelInfo instanceof MtkTvATSCChannelInfo) {
                    MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel.mMtkTvChannelInfo;
                    TextView textView2 = hodler.mChannelNumberTextView;
                    textView2.setText(tmpAtsc.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpAtsc.getMinorNum());
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else if (mCurrentChannel.mMtkTvChannelInfo instanceof MtkTvISDBChannelInfo) {
                    MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel.mMtkTvChannelInfo;
                    TextView textView3 = hodler.mChannelNumberTextView;
                    textView3.setText(tmpIsdb.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpIsdb.getMinorNum());
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(Drawable.createFromPath(ChannelListDialog.this.commonIntegration.getISDBChannelLogo(tmpIsdb)), (Drawable) null, (Drawable) null, (Drawable) null);
                } else if (mCurrentChannel.mMtkTvChannelInfo instanceof MtkTvAnalogChannelInfo) {
                    if (MarketRegionInfo.getCurrentMarketRegion() == 2 || MarketRegionInfo.getCurrentMarketRegion() == 3) {
                        Drawable analogIcon2 = this.mContext.getResources().getDrawable(R.drawable.epg_channel_icon);
                        analogIcon2.setBounds(0, 0, analogIcon2.getMinimumWidth(), analogIcon2.getMinimumWidth());
                        hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(analogIcon2, (Drawable) null, (Drawable) null, (Drawable) null);
                    } else {
                        hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    TextView textView4 = hodler.mChannelNumberTextView;
                    textView4.setText("" + mCurrentChannel.mMtkTvChannelInfo.getChannelNumber());
                } else {
                    hodler.mChannelNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    TextView textView5 = hodler.mChannelNumberTextView;
                    textView5.setText("" + mCurrentChannel.mMtkTvChannelInfo.getChannelNumber());
                }
                if (mCurrentChannel.mMtkTvChannelInfo instanceof MtkTvDvbChannelInfo) {
                    MtkTvDvbChannelInfo tmpdvb = (MtkTvDvbChannelInfo) mCurrentChannel.mMtkTvChannelInfo;
                    String name = tmpdvb.getShortName();
                    if ((name == null || name.trim().length() <= 0) && (name = tmpdvb.getServiceName()) == null) {
                        name = "";
                    }
                    hodler.mChannelNameTextView.setText(name);
                } else {
                    hodler.mChannelNameTextView.setText(mCurrentChannel.mMtkTvChannelInfo.getServiceName());
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView mChannelNameTextView;
            TextView mChannelNumberTextView;

            ViewHolder() {
            }
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 10) {
            if (isVisible()) {
                this.mHandler.removeMessages(4104);
                if (this.mSendKeyCode == 86 || this.mSendKeyCode == 166 || this.mSendKeyCode == 167 || this.mSendKeyCode == 229) {
                    MtkLog.d(TAG, "updateComponentStatus>>>" + statusID + ">>" + value + ">>" + this.mSendKeyCode);
                    if (value != 0) {
                        this.mCanChangeChannel = true;
                    } else if (TIFFunctionUtil.checkChMask(this.mTIFChannelManager.getPreChannelInfo(), TIFFunctionUtil.CH_LIST_MASK, 0)) {
                        resetChListByTIF();
                        this.mCanChangeChannel = true;
                    } else {
                        Message msg = Message.obtain();
                        if (this.mSendKeyCode == 166) {
                            msg.arg2 = this.preChId;
                        }
                        if (this.mSendKeyCode == 86) {
                            this.mSendKeyCode = KeyMap.KEYCODE_MTKIR_CHUP;
                        }
                        msg.arg1 = this.mSendKeyCode;
                        msg.what = 4097;
                        this.mHandler.sendMessage(msg);
                    }
                }
            }
        } else if (statusID == 2) {
            if (value == 16777233 && isVisible()) {
                dismiss();
            }
        } else if (statusID == 3) {
            MtkLog.i(TAG, "updateComponentStatus statusID:" + statusID);
            MtkTvConfig config = MtkTvConfig.getInstance();
            int chanelUpdateMsg = config.getConfigValue("g_menu__ch_update_msg");
            int channelNewSvcAdded = config.getConfigValue("g_menu__new_svc_added");
            MtkLog.i(TAG, "chanelUpdateMsg:" + chanelUpdateMsg + " channelNewSvcAdded:" + channelNewSvcAdded);
            if (chanelUpdateMsg == 1 && channelNewSvcAdded > 0) {
                addChannelsDialog();
                config.setConfigValue("g_menu__new_svc_added", 0);
            }
        }
    }

    public void addChannelsDialog() {
        final LiveTVDialog liveTVDialog = new LiveTVDialog(this.mContext, 7);
        liveTVDialog.setMessage(this.mContext.getResources().getString(R.string.nav_channel_new_channels));
        liveTVDialog.setButtonNoName(this.mContext.getString(R.string.menu_ok));
        View.OnKeyListener listener2 = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 4 || event.getAction() != 0) {
                    return false;
                }
                MtkLog.d(ChannelListDialog.TAG, "liveTVDialog  exit for back key");
                liveTVDialog.dismiss();
                return true;
            }
        };
        liveTVDialog.show();
        liveTVDialog.bindKeyListener(listener2);
        liveTVDialog.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                MtkLog.d(ChannelListDialog.TAG, "liveTVDialog exit");
                liveTVDialog.dismiss();
                return true;
            }
        });
        liveTVDialog.getButtonNo().requestFocus();
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (liveTVDialog != null && liveTVDialog.isShowing()) {
                    liveTVDialog.dismiss();
                }
            }
        }, MessageType.delayMillis4);
    }

    public void onActivityResult(Intent data) {
        MtkLog.d(TAG, "onActivityResult>>>");
        if (data != null) {
            String value = data.getStringExtra(SaveValue.GLOBAL_VALUE_VALUE);
            MtkLog.d(TAG, "onActivityResult value:" + value);
            this.isFindingForChannelList = true;
            this.mTIFChannelManager.findChanelsForlist(value, this.mCurMask, this.mCurVal);
            if (this.mIsTifFunction) {
                resetChListByTIF();
            } else {
                resetChList();
            }
        }
    }
}
