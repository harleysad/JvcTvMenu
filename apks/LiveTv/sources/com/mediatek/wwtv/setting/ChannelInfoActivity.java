package com.mediatek.wwtv.setting;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter;
import com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter;
import com.mediatek.wwtv.setting.base.scan.ui.BaseCustomActivity;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.util.TransItem;
import com.mediatek.wwtv.setting.view.ChannelMenuViewBottom;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.view.FinetuneDialog;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChannelInfoActivity extends BaseCustomActivity implements ChannelInfoAdapter.EnterEditDetailListener {
    static final int REQ_EDITTEXT = 35;
    static final String TAG = "ChannelInfoActivity";
    final int CHANNEL_LIST_SElECTED_FOR_SETTING_TTS = 10;
    final String MSG_MOVE_UPDATED_NED = "mtk.intent.TV_PROVIDER_UPDATED_END";
    final int MSG_SORT_DELAY_TIP_DIALOG_HIDE = 54;
    final int MSG_SORT_DELAY_TIP_DIALOG_SHOW = 53;
    final int MSG_SORT_SELECT_CHANNEL = 52;
    int channelSortNum = 0;
    TurnkeyCommDialog deleteCofirm;
    public boolean isM7Enable = false;
    public boolean isTkgsEnable = false;
    private View.AccessibilityDelegate mAccDelegateForChList = new View.AccessibilityDelegate() {
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            MtkLog.d(ChannelInfoActivity.TAG, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
            if (ChannelInfoActivity.this.mListView != host) {
                MtkLog.d(ChannelInfoActivity.TAG, "host:" + ChannelInfoActivity.this.mListView + "," + host);
            } else {
                MtkLog.d(ChannelInfoActivity.TAG, ":host =false");
                List<CharSequence> texts = event.getText();
                if (texts == null) {
                    MtkLog.d(ChannelInfoActivity.TAG, "texts :" + texts);
                } else if (event.getEventType() == 32768) {
                    int index = findSelectItem(texts.get(0).toString());
                    MtkLog.d(ChannelInfoActivity.TAG, ":index =" + index);
                    if (index >= 0) {
                        ChannelInfoActivity.this.mSelCelHandler.removeMessages(10);
                        Message msg = Message.obtain();
                        msg.what = 10;
                        msg.arg1 = index;
                        ChannelInfoActivity.this.mSelCelHandler.sendMessageDelayed(msg, 90);
                    }
                }
            }
            try {
                return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
            } catch (Exception e) {
                Log.d(ChannelInfoActivity.TAG, "Exception " + e);
                return true;
            }
        }

        private int findSelectItem(String text) {
            MtkLog.d(ChannelInfoActivity.TAG, "texts =" + text);
            List<String[]> channelinfoList = ChannelInfoActivity.this.mHelper.getChannelInfo();
            if (channelinfoList == null) {
                return -1;
            }
            for (int i = 0; i < channelinfoList.size(); i++) {
                MtkLog.d(ChannelInfoActivity.TAG, ":index =" + channelinfoList.get(i)[0] + " text = " + text);
                if (channelinfoList.get(i)[0].equals(text)) {
                    return i;
                }
            }
            return -1;
        }
    };
    String mActionID;
    ChannelInfoAdapter mAdapter;
    private ChannelMenuViewBottom mChannelMenuViewBottom;
    Context mContext;
    EditDetailAdapter.EditItem mCurrEditItem;
    String mCurrEditItemId;
    String[] mData;
    EditDetailAdapter mDetailAdapter;
    EditChannel mEditChannel;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == msg.arg1 && msg.what == msg.arg2) {
                Object obj = msg.obj;
                int i = msg.what;
                return;
            }
            super.handleMessage(msg);
        }
    };
    MenuDataHelper mHelper;
    ListView mListView;
    Handler mSelCelHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 10) {
                switch (i) {
                    case 52:
                        Boolean bol = (Boolean) msg.obj;
                        MtkLog.d(ChannelInfoActivity.TAG, "channelSort mSelCelHandler bol value is :" + bol);
                        if (bol.booleanValue()) {
                            MtkLog.d(ChannelInfoActivity.TAG, "channelSort select the child -x:" + msg.arg2);
                            ChannelInfoActivity.this.mEditChannel.selectChannel(msg.arg2);
                            return;
                        }
                        MtkLog.d(ChannelInfoActivity.TAG, "channelSort select the child -y:" + msg.arg1);
                        ChannelInfoActivity.this.mEditChannel.selectChannel(msg.arg1);
                        return;
                    case 53:
                        if (ChannelInfoActivity.this.pdialog != null) {
                            ChannelInfoActivity.this.pdialog.show();
                        }
                        sendEmptyMessageDelayed(54, 3000);
                        return;
                    case 54:
                        MtkLog.d(ChannelInfoActivity.TAG, "channel MSG_SORT_DELAY_TIP_DIALOG_HIDE");
                        ChannelInfoActivity.this.bindData();
                        if (ChannelInfoActivity.this.pdialog != null) {
                            ChannelInfoActivity.this.pdialog.dismiss();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            } else {
                MtkLog.d(ChannelInfoActivity.TAG, " hi CHANNEL_LIST_SElECTED_FOR_SETTING_TTS index  ==" + msg.arg1);
                ChannelInfoActivity.this.mListView.setSelection(msg.arg1);
            }
        }
    };
    TVContent mTV;
    TransItem nowTItem;
    float numHz = 10.0f;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (ChannelInfoActivity.this.mListView.getAdapter() instanceof ChannelInfoAdapter) {
                ChannelInfoActivity.this.mData = (String[]) ChannelInfoActivity.this.mListView.getSelectedView().getTag(R.id.channel_info_no);
                MtkLog.d(ChannelInfoActivity.TAG, "ondispatch:" + ChannelInfoActivity.this.mData[0]);
                ((ChannelInfoAdapter) ChannelInfoActivity.this.mListView.getAdapter()).onKeyEnter(ChannelInfoActivity.this.mData, ChannelInfoActivity.this.mListView.getSelectedView());
            } else if (ChannelInfoActivity.this.mListView.getAdapter() instanceof EditDetailAdapter) {
                EditDetailAdapter.EditItem item = (EditDetailAdapter.EditItem) ChannelInfoActivity.this.mListView.getSelectedView().getTag(R.id.editdetail_value);
                if (item.isEnable) {
                    if (item.id.equals(MenuConfigManager.TV_FINETUNE)) {
                        ChannelInfoActivity.this.finetuneInfoDialog(ChannelInfoActivity.this.mData);
                    }
                    if (item.id.equals(MenuConfigManager.TV_STORE)) {
                        ChannelInfoActivity.this.showStoreChannelDialog();
                    } else {
                        ChannelInfoActivity.this.gotoEditTextAct(ChannelInfoActivity.this.mListView.getSelectedView());
                    }
                }
            }
        }
    };
    TextView page_mid_delete;
    ImageView page_mid_img;
    private RelativeLayout pagebtnLayout;
    ProgressDialog pdialog;
    LiveTVDialog storeChannelDialog;
    int theSelectChannelPosition;
    BroadcastReceiver updatedUIForMoveEnd = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(ChannelInfoActivity.TAG, "updatedUIForMoveEnd");
            if (intent.getAction().equals("mtk.intent.TV_PROVIDER_UPDATED_END") && ChannelInfoActivity.this.pdialog != null && ChannelInfoActivity.this.pdialog.isShowing()) {
                Log.d(ChannelInfoActivity.TAG, "updatedUIForMoveEnd MSG_MOVE_UPDATED_NED");
                TIFChannelManager.getInstance(ChannelInfoActivity.this.mContext).getAllChannels();
                if (MenuConfigManager.TV_CHANNEL_SORT.equals(ChannelInfoActivity.this.mActionID)) {
                    ChannelInfoActivity.this.mSelCelHandler.removeMessages(54);
                    ChannelInfoActivity.this.mSelCelHandler.sendEmptyMessageDelayed(54, MessageType.delayMillis5);
                } else if (MenuConfigManager.TV_CHANNEL_MOVE.equals(ChannelInfoActivity.this.mActionID)) {
                    ChannelInfoActivity.this.mSelCelHandler.sendEmptyMessageDelayed(54, MessageType.delayMillis5);
                }
            }
        }
    };

    private void registerTvReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("mtk.intent.TV_PROVIDER_UPDATED_END");
        registerReceiver(this.updatedUIForMoveEnd, filter);
    }

    private void unRegisterTvReceiver() {
        unregisterReceiver(this.updatedUIForMoveEnd);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.nowTItem = (TransItem) getIntent().getSerializableExtra("TransItem");
        this.mActionID = getIntent().getStringExtra("ActionID");
        this.mTV = TVContent.getInstance(this.mContext);
        this.mEditChannel = EditChannel.getInstance(this.mContext);
        this.mHelper = MenuDataHelper.getInstance(this.mContext);
        setContentView(R.layout.menu_channel_info_layout);
        this.mListView = (ListView) findViewById(R.id.channel_info_listview);
        this.pagebtnLayout = (RelativeLayout) findViewById(R.id.pagebutton);
        this.mChannelMenuViewBottom = (ChannelMenuViewBottom) findViewById(R.id.channelmenu_bottom);
        this.page_mid_img = (ImageView) findViewById(R.id.page_mid_img);
        this.page_mid_delete = (TextView) findViewById(R.id.page_mid_delete);
        this.mChannelMenuViewBottom.setVisibility(8);
        int tkgsmode = MenuConfigManager.getInstance(this).getDefault("g_misc__tkgs_operating_mode");
        boolean satOperOnly = CommonIntegration.getInstance().isPreferSatMode();
        boolean isTkgs = this.mTV.isTKGSOperator();
        MtkLog.d(TAG, "satOperOnly=" + satOperOnly + "mTV.isTurkeyCountry() :" + this.mTV.isTurkeyCountry() + "mTV.isTKGSOperator(): " + this.mTV.isTKGSOperator() + "tkgsmode=" + tkgsmode);
        if (tkgsmode == 1 && satOperOnly && isTkgs) {
            this.isTkgsEnable = true;
        }
        if (this.mTV.isM7ScanMode()) {
            this.isM7Enable = true;
        }
        if (!this.mActionID.equals(MenuConfigManager.TV_CHANNEL_EDIT) || (this.isTkgsEnable && this.mTV.getTKGSOperatorMode() != 2)) {
            this.page_mid_img.setVisibility(8);
            this.page_mid_delete.setVisibility(8);
        }
        this.mListView.setOnItemClickListener(this.onItemClickListener);
        bindData();
        registerTvReceiver();
    }

    /* access modifiers changed from: private */
    public void bindData() {
        this.mHelper.getTVData(this.mActionID);
        if (this.mActionID.equals("g_menu__soundtracks")) {
            this.mAdapter = new ChannelInfoAdapter(this.mContext, getSoundTracks(), this.nowTItem, this.mHelper.getGotoPage());
            this.pagebtnLayout.setVisibility(4);
        } else if (this.mActionID.equals("g_menu__audioinfo")) {
            this.mAdapter = new ChannelInfoAdapter(this.mContext, getSoundType(), this.nowTItem, this.mHelper.getGotoPage());
            this.pagebtnLayout.setVisibility(4);
        } else if (this.mActionID.equals(MenuConfigManager.POWER_ON_VALID_CHANNELS)) {
            getPowerOnChannelsPageAndPos();
            this.mAdapter = new ChannelInfoAdapter(this.mContext, this.mHelper.setChannelInfoList(this.mActionID), this.nowTItem, this.mHelper.getGotoPage());
        } else {
            this.mAdapter = new ChannelInfoAdapter(this.mContext, this.mHelper.setChannelInfoList(this.mActionID), this.nowTItem, this.mHelper.getGotoPage());
            this.mAdapter.setChannelSortNum(this.channelSortNum);
        }
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setAccessibilityDelegate(this.mAccDelegateForChList);
        this.mListView.setSelection(this.mHelper.getGotoPosition());
        TvCallbackHandler.getInstance().addCallBackListener(TvCallbackConst.MSG_CB_NFY_TSL_ID_UPDATE_MSG, this.mHandler);
    }

    /* access modifiers changed from: package-private */
    public void getPowerOnChannelsPageAndPos() {
        String cfgId;
        if (this.mTV.getCurrentTunerMode() == 0) {
            cfgId = "g_nav__air_on_time_ch";
        } else {
            cfgId = "g_nav__cable_on_time_ch";
        }
        int channelId = this.mTV.getConfigValue(cfgId);
        MtkLog.d(TAG, "getPowerOnChannelsPageAndPos power on-id:" + channelId);
        int idx = 0;
        int i = 0;
        while (true) {
            if (i >= this.mHelper.getChannelInfo().size()) {
                break;
            }
            String str = this.mHelper.getChannelInfo().get(i)[3];
            if (str.equals("" + channelId)) {
                idx = i;
                break;
            }
            i++;
        }
        this.mHelper.setGotoPage((idx / 10) + 1);
        this.mHelper.setGotoPosition(idx % 10);
    }

    /* access modifiers changed from: package-private */
    public List<String> getData() {
        List<String> data = new ArrayList<>();
        data.add("11111");
        data.add("111221");
        data.add("1116771");
        data.add("11344411");
        data.add("11ffg11");
        data.add("113345f11");
        data.add("1ggt411");
        data.add("11iuuuu1");
        data.add("1nnnbn11");
        return data;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        MtkLog.d(TAG, "onStart==");
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        TvCallbackHandler.getInstance().removeCallBackListener(TvCallbackConst.MSG_CB_NFY_TSL_ID_UPDATE_MSG, this.mHandler);
        super.onDestroy();
        unRegisterTvReceiver();
    }

    public void onPause() {
        if (!(this.mEditChannel.getRestoreHZ() == 0.0f || this.numHz == 10.0f || !this.mEditChannel.getStoredFlag())) {
            this.mEditChannel.setStoredFlag(true);
            this.mEditChannel.restoreFineTune();
        }
        this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
        super.onPause();
        this.channelSortNum = 0;
        this.mAdapter.setChannelSortNum(this.channelSortNum);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode;
        MtkLog.d(TAG, "dispatchKeyEvent==" + event.getAction() + "," + event.getKeyCode());
        if (event.getAction() == 0 && (keyCode = event.getKeyCode()) != 23) {
            if (keyCode != 30) {
                if (!(keyCode == 35 || keyCode == 46 || keyCode == 66)) {
                    if (keyCode != 186) {
                        switch (keyCode) {
                        }
                    }
                }
            }
            MtkLog.d(TAG, "dispatchKeyEvent==KEYCODE_B" + event.getKeyCode());
            if (this.page_mid_delete.getVisibility() == 0 && (this.mListView.getAdapter() instanceof ChannelInfoAdapter)) {
                this.mData = (String[]) this.mListView.getSelectedView().getTag(R.id.channel_info_no);
                if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(this.mData[0]) >= 4001) {
                    this.deleteCofirm = new TurnkeyCommDialog(this.mContext, 3);
                    this.deleteCofirm.setMessage(this.mContext.getString(R.string.menu_tv_delete_message));
                    this.deleteCofirm.setButtonYesName(this.mContext.getString(R.string.common_dialog_msg_yes));
                    this.deleteCofirm.setButtonNoName(this.mContext.getString(R.string.common_dialog_msg_no));
                    this.deleteCofirm.show();
                    this.deleteCofirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            int action = event.getAction();
                            if (keyCode != 4 || action != 0) {
                                return false;
                            }
                            MtkLog.d(ChannelInfoActivity.TAG, "deleteCofirm false back");
                            ChannelInfoActivity.this.deleteCofirm.dismiss();
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
                            MtkLog.d(ChannelInfoActivity.TAG, "deleteCofirm yes");
                            if ((ChannelInfoActivity.this.mListView.getAdapter() instanceof ChannelInfoAdapter) && ChannelInfoActivity.this.mActionID.equals(MenuConfigManager.TV_CHANNEL_EDIT)) {
                                MtkLog.d(ChannelInfoActivity.TAG, "dispatchKeyEvent==KEYCODE_B getAdapter() posotion is " + ChannelInfoActivity.this.mListView.getSelectedItemPosition());
                                if (ChannelInfoActivity.this.mListView.getSelectedView() != null) {
                                    int deleteId = Integer.parseInt(ChannelInfoActivity.this.mData[3]);
                                    MtkLog.d(ChannelInfoActivity.TAG, "==KEYCODE_B deleteId =" + deleteId);
                                    if (ChannelInfoActivity.this.mEditChannel.deleteInactiveChannel(deleteId)) {
                                        ChannelInfoActivity.this.mHelper.getTVData(ChannelInfoActivity.this.mActionID);
                                        if (ChannelInfoActivity.this.mHelper.getChNum() > 0) {
                                            ChannelInfoActivity.this.mAdapter = new ChannelInfoAdapter(ChannelInfoActivity.this.mContext, ChannelInfoActivity.this.mHelper.setChannelInfoList(ChannelInfoActivity.this.mActionID), ChannelInfoActivity.this.nowTItem, ChannelInfoActivity.this.mHelper.getGotoPage());
                                            ChannelInfoActivity.this.mListView.setAdapter(ChannelInfoActivity.this.mAdapter);
                                            ChannelInfoActivity.this.mListView.setSelection(ChannelInfoActivity.this.mHelper.getGotoPosition());
                                        } else {
                                            ChannelInfoActivity.this.mEditChannel.cleanChannelList();
                                            ChannelInfoActivity.this.finish();
                                        }
                                    }
                                }
                            }
                            ChannelInfoActivity.this.deleteCofirm.dismiss();
                            return true;
                        }
                    };
                    this.deleteCofirm.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() != 0) {
                                return false;
                            }
                            if (keyCode != 66 && keyCode != 23) {
                                return false;
                            }
                            MtkLog.d(ChannelInfoActivity.TAG, "deleteCofirm no");
                            ChannelInfoActivity.this.deleteCofirm.dismiss();
                            return true;
                        }
                    });
                    this.deleteCofirm.getButtonYes().setOnKeyListener(yesListener);
                } else {
                    Toast.makeText(this.mContext, this.mContext.getString(R.string.menu_tv_delete_not_forM7Number), 1).show();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void gotoEditTextAct(View selectedView) {
        EditDetailAdapter.EditItem item = (EditDetailAdapter.EditItem) selectedView.getTag(R.id.editdetail_value);
        this.mCurrEditItemId = item.id;
        this.mCurrEditItem = item;
        if (!item.isEnable || item.dataType != Action.DataType.INPUTBOX) {
            MtkLog.e(TAG, "Option Item needn't go to editText Activity");
            return;
        }
        Intent intent = new Intent(this.mContext, EditTextActivity.class);
        intent.putExtra("password", false);
        intent.putExtra("description", item.title);
        intent.putExtra("initialText", item.value);
        intent.putExtra("itemId", item.id);
        intent.putExtra("isDigit", item.isDigit);
        if (item.isDigit) {
            intent.putExtra("length", 9);
        }
        if (this.mCurrEditItemId.equals(MenuConfigManager.TV_FREQ) || this.mCurrEditItemId.equals(MenuConfigManager.TV_FREQ_SA)) {
            intent.putExtra("canFloat", true);
        }
        if (this.mCurrEditItemId.equals(MenuConfigManager.TV_CHANNEL_NO)) {
            intent.putExtra("length", 4);
        }
        if (this.mCurrEditItemId.equals(MenuConfigManager.TV_CHANNEL_SA_NAME)) {
            intent.putExtra("length", 32);
        }
        startActivityForResult(intent, 35);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            String value = data.getStringExtra(SaveValue.GLOBAL_VALUE_VALUE);
            MtkLog.d(TAG, "onActivityResult value:" + value);
            if (this.mCurrEditItemId.equals(MenuConfigManager.TV_FREQ_SA) || this.mCurrEditItemId.equals(MenuConfigManager.TV_FREQ)) {
                try {
                    float now = Float.parseFloat(value);
                    if (now < this.mCurrEditItem.minValue) {
                        if (Float.parseFloat(this.mData[4]) >= this.mCurrEditItem.maxValue) {
                            now = Float.parseFloat(this.mData[4]) - 1.2f;
                        } else {
                            now = this.mCurrEditItem.minValue;
                        }
                    } else if (now > this.mCurrEditItem.maxValue) {
                        if (Float.parseFloat(this.mData[4]) <= this.mCurrEditItem.minValue) {
                            now = Float.parseFloat(this.mData[4]) + 1.8f;
                        } else {
                            now = this.mCurrEditItem.maxValue;
                        }
                    }
                    String value2 = now + "";
                    if (this.mData[4] != value2) {
                        this.mData[4] = value2;
                        this.mHelper.updateChannelFreq(this.mData[3], this.mData[4]);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this.mContext, "Number is not valid!please reInput", 1).show();
                    e.printStackTrace();
                }
            } else if (this.mCurrEditItemId.equals(MenuConfigManager.TV_CHANNEL_SA_NAME)) {
                if (!value.equals(this.mData[2])) {
                    this.mData[2] = value;
                    this.mHelper.updateChannelName(this.mData[3], this.mData[2]);
                }
            } else if (this.mCurrEditItemId.equals(MenuConfigManager.TV_CHANNEL_NO)) {
                int now2 = Integer.parseInt(value);
                if (((float) now2) < this.mCurrEditItem.minValue) {
                    now2 = (int) this.mCurrEditItem.minValue;
                } else if (((float) now2) > this.mCurrEditItem.maxValue) {
                    now2 = (int) this.mCurrEditItem.maxValue;
                }
                String value3 = now2 + "";
                if (Integer.parseInt(this.mData[0]) != now2) {
                    if ((CommonIntegration.isEURegion() || CommonIntegration.isCNRegion()) && now2 == 0) {
                        Toast.makeText(this.mContext, this.mContext.getString(R.string.menu_dialog_numzero), 1).show();
                    } else if (this.isM7Enable) {
                        Toast.makeText(this.mContext, this.mContext.getString(R.string.menu_tv_edit_M7_num_more4000), 1).show();
                    } else if (!this.mHelper.checkDuplicate(value3)) {
                        this.mData[0] = value3;
                        if (CommonIntegration.isCNRegion()) {
                            this.mHelper.updateChannelNumberCnRegion(this.mData[3], this.mData[0]);
                        } else {
                            this.mHelper.updateChannelNumber(this.mData[3], this.mData[0]);
                        }
                    } else {
                        Toast.makeText(this.mContext, this.mContext.getString(R.string.menu_dialog_numrepeat), 1).show();
                    }
                }
            }
            this.mDetailAdapter.setNewList(this.mHelper.getChannelEditDetail(this.mData));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown==" + keyCode);
        switch (keyCode) {
            case 4:
                if (this.mListView.getAdapter() instanceof EditDetailAdapter) {
                    int currPage = this.mAdapter.getCurrPage();
                    List<String[]> editChannelInfos = this.mHelper.setChannelInfoList(this.mActionID);
                    int selIndex = 0;
                    int i = 0;
                    while (true) {
                        if (i < editChannelInfos.size()) {
                            String[] infosarray = editChannelInfos.get(i);
                            if (infosarray == null || !infosarray[0].equals(this.mData[0])) {
                                i++;
                            } else {
                                selIndex = i;
                                MtkLog.d(TAG, "for adapter's selIndex==" + selIndex);
                            }
                        }
                    }
                    this.theSelectChannelPosition = selIndex % 10;
                    this.mAdapter = new ChannelInfoAdapter(this.mContext, editChannelInfos, this.nowTItem, (selIndex / 10) + 1);
                    this.mListView.setAdapter(this.mAdapter);
                    MtkLog.d(TAG, "theSelectChannelPosition==" + this.theSelectChannelPosition);
                    this.mListView.setSelection(this.theSelectChannelPosition);
                    this.pagebtnLayout.setVisibility(0);
                    if (CommonIntegration.isCNRegion()) {
                        this.mChannelMenuViewBottom.setVisibility(0);
                    }
                    return true;
                }
                break;
            case 21:
            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                if (this.mListView.getAdapter() instanceof ChannelInfoAdapter) {
                    ((ChannelInfoAdapter) this.mListView.getAdapter()).goToPrevPage();
                    return true;
                } else if (this.mListView.getAdapter() instanceof EditDetailAdapter) {
                    ((EditDetailAdapter) this.mListView.getAdapter()).optionTurnLeft(this.mListView.getSelectedView(), this.mData);
                    return true;
                }
                break;
            case 22:
            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                if (this.mListView.getAdapter() instanceof ChannelInfoAdapter) {
                    ((ChannelInfoAdapter) this.mListView.getAdapter()).goToNextPage();
                    return true;
                } else if (this.mListView.getAdapter() instanceof EditDetailAdapter) {
                    ((EditDetailAdapter) this.mListView.getAdapter()).optionTurnRight(this.mListView.getSelectedView(), this.mData);
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.channelSortNum = 0;
    }

    public boolean channelMove(int ch_num_src) {
        String cfgId;
        if (this.channelSortNum != 0 && this.channelSortNum != ch_num_src) {
            initMoveDelayDialog();
            this.pdialog.show();
            MtkLog.e(TAG, "channelMoveNum:" + this.channelSortNum + " ch_num_src:" + ch_num_src);
            int oldChId = this.mEditChannel.getCurrentChannelId();
            boolean playNowChannel = this.channelSortNum == oldChId;
            boolean playNowChannelSrc = ch_num_src == oldChId;
            if (this.mTV.getCurrentTunerMode() == 0) {
                cfgId = "g_nav__air_on_time_ch";
            } else {
                cfgId = "g_nav__cable_on_time_ch";
            }
            int poweronchannelId = this.mTV.getConfigValue(cfgId);
            int powerONEqual = 0;
            if (poweronchannelId == this.channelSortNum) {
                powerONEqual = 1;
            } else if (poweronchannelId == ch_num_src) {
                powerONEqual = 2;
            }
            EditChannel.getInstance(this.mContext).channelMoveForfusion(this.channelSortNum, ch_num_src);
            String[] convertIDs = this.mHelper.updateChannelData(this.channelSortNum, ch_num_src);
            this.channelSortNum = Integer.parseInt(convertIDs[0]);
            int ch_num_src2 = Integer.parseInt(convertIDs[1]);
            MtkLog.e(TAG, "new channelMoveNum:" + this.channelSortNum + "new  ch_num_src:" + ch_num_src2);
            int newChId = this.mEditChannel.getCurrentChannelId();
            MtkLog.i(TAG, "channelMove oldChId:" + oldChId + ",newChId:" + newChId);
            if (newChId == oldChId) {
                MtkLog.d(TAG, "channelMove chid changed needn't select again");
            } else if (playNowChannel || playNowChannelSrc) {
                Message msg = this.mSelCelHandler.obtainMessage();
                msg.obj = Boolean.valueOf(playNowChannel);
                Objects.requireNonNull(this);
                msg.what = 52;
                msg.arg1 = this.channelSortNum;
                msg.arg2 = ch_num_src2;
                this.mSelCelHandler.sendMessageDelayed(msg, 1000);
            }
            if (powerONEqual == 1) {
                poweronchannelId = ch_num_src2;
                this.mTV.setConfigValue(cfgId, poweronchannelId);
            } else if (powerONEqual == 2) {
                poweronchannelId = this.channelSortNum;
                this.mTV.setConfigValue(cfgId, poweronchannelId);
            }
            MtkLog.e(TAG, "channelMove perEq:" + powerONEqual + "new perchId:" + poweronchannelId);
            this.channelSortNum = 0;
            this.mAdapter.setChannelSortNum(this.channelSortNum);
            return true;
        } else if (this.channelSortNum == ch_num_src) {
            this.channelSortNum = 0;
            this.mAdapter.setChannelSortNum(this.channelSortNum);
            return true;
        } else {
            this.channelSortNum = ch_num_src;
            return false;
        }
    }

    public boolean channelSort(int ch_num_src) {
        String cfgId;
        int i = ch_num_src;
        if (this.channelSortNum != 0 && this.channelSortNum != i) {
            this.pdialog = null;
            if (this.pdialog == null) {
                initSortDelayDialog();
            }
            MtkLog.e(TAG, "channelSortNum:" + this.channelSortNum + " ch_num_src:" + i);
            int oldChId = this.mEditChannel.getCurrentChannelId();
            boolean playNowChannel = this.channelSortNum == oldChId;
            boolean playNowChannelSrc = i == oldChId;
            if (this.mTV.getCurrentTunerMode() == 0) {
                cfgId = "g_nav__air_on_time_ch";
            } else {
                cfgId = "g_nav__cable_on_time_ch";
            }
            int poweronchannelId = this.mTV.getConfigValue(cfgId);
            int powerONEqual = 0;
            if (poweronchannelId == this.channelSortNum) {
                powerONEqual = 1;
            } else if (poweronchannelId == i) {
                powerONEqual = 2;
            }
            EditChannel.getInstance(this.mContext).channelSort(this.channelSortNum, i);
            String[] convertIDs = this.mHelper.updateChannelData(this.channelSortNum, i);
            this.channelSortNum = Integer.parseInt(convertIDs[0]);
            int ch_num_src2 = Integer.parseInt(convertIDs[1]);
            MtkLog.e(TAG, "new channelSortNum:" + this.channelSortNum + "new  ch_num_src:" + ch_num_src2);
            int position = getListSelectedPosition(ch_num_src2, this.channelSortNum);
            this.mListView.setSelection(position);
            int newChId = this.mEditChannel.getCurrentChannelId();
            MtkLog.i(TAG, "channelSort pos:" + position + ",oldChId:" + oldChId + ",newChId:" + newChId);
            if (newChId == oldChId) {
                MtkLog.d(TAG, "channelSort chid changed needn't select again");
            } else if (playNowChannel || playNowChannelSrc) {
                Message msg = this.mSelCelHandler.obtainMessage();
                msg.obj = Boolean.valueOf(playNowChannel);
                Objects.requireNonNull(this);
                msg.what = 52;
                msg.arg1 = this.channelSortNum;
                msg.arg2 = ch_num_src2;
                this.mSelCelHandler.sendMessageDelayed(msg, 1000);
            }
            if (powerONEqual == 1) {
                poweronchannelId = ch_num_src2;
                this.mTV.setConfigValue(cfgId, poweronchannelId);
            } else if (powerONEqual == 2) {
                poweronchannelId = this.channelSortNum;
                this.mTV.setConfigValue(cfgId, poweronchannelId);
            }
            MtkLog.e(TAG, "channelSort perEq:" + powerONEqual + "new perchId:" + poweronchannelId);
            this.channelSortNum = 0;
            this.mAdapter.setChannelSortNum(this.channelSortNum);
            Message msgtip = this.mSelCelHandler.obtainMessage();
            msgtip.what = 53;
            this.mSelCelHandler.sendMessage(msgtip);
            return true;
        } else if (this.channelSortNum == i) {
            this.channelSortNum = 0;
            this.mAdapter.setChannelSortNum(this.channelSortNum);
            return true;
        } else {
            this.channelSortNum = i;
            return false;
        }
    }

    private int getListSelectedPosition(int id, int did) {
        int position = -1;
        List<String[]> channelinfo = this.mHelper.getChannelInfo();
        if (channelinfo == null) {
            return -1;
        }
        int tempposition = -1;
        for (int i = 0; i < channelinfo.size(); i++) {
            int ch_num = Integer.parseInt(channelinfo.get(i)[3]);
            if (Math.abs(ch_num - id) <= 1) {
                position = i;
                if (tempposition != -1) {
                    break;
                }
            }
            if (Math.abs(ch_num - this.channelSortNum) <= 1) {
                tempposition = i;
                if (position != -1) {
                    break;
                }
            }
        }
        MtkLog.i("wangjinben", "position:" + position + " tempposition:" + tempposition);
        int position2 = position > tempposition ? tempposition : position;
        if (position2 < 0) {
            position2 = 0;
        }
        int position3 = this.mAdapter.updatePage(position2, channelinfo);
        this.mAdapter.clearCheckMap();
        this.mAdapter.notifyDataSetChanged();
        return position3;
    }

    public void finetuneInfoDialog(final String[] mData2) {
        final FinetuneDialog tcf = new FinetuneDialog(this);
        final NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setGroupingUsed(false);
        tcf.show();
        if (CommonIntegration.isSARegion() || CommonIntegration.isEURegion() || CommonIntegration.isCNRegion()) {
            tcf.setNumText(mData2[0]);
            this.numHz = Float.parseFloat(mData2[4]);
            tcf.setNameText(mData2[2]);
            int channelID = Integer.parseInt(mData2[3]);
            for (int i = 0; i < mData2.length; i++) {
                MtkLog.d(TAG, "finetuneInfoDialog" + mData2 + "[" + i + "]==" + mData2[i]);
            }
            MtkLog.d(TAG, "childView != null" + this.numHz + "channelID:" + channelID);
            if (channelID != this.mEditChannel.getCurrentChannelId()) {
                this.mEditChannel.selectChannel(channelID);
            }
        }
        tcf.setAdjustText(numberFormat.format((double) this.numHz) + "MHz");
        this.mEditChannel.setOriginalFrequency(this.numHz);
        this.mEditChannel.setRestoreHZ(this.numHz);
        tcf.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if ((keyCode == 66 || keyCode == 23) && action == 0) {
                    ChannelInfoActivity.this.mEditChannel.setStoredFlag(false);
                    tcf.dismiss();
                    mData2[4] = numberFormat.format((double) ChannelInfoActivity.this.numHz);
                    ChannelInfoActivity.this.mEditChannel.saveFineTune();
                    if (CommonIntegration.isCNRegion()) {
                        ((EditDetailAdapter.EditItem) ChannelInfoActivity.this.mListView.getChildAt(2).getTag(R.id.editdetail_value)).value = mData2[4];
                        ChannelInfoActivity.this.mDetailAdapter.notifyDataSetChanged();
                    } else {
                        ChannelInfoActivity.this.mAdapter.notifyDataSetChanged();
                    }
                    return true;
                } else if (keyCode == 4 && action == 0) {
                    ChannelInfoActivity.this.mEditChannel.setStoredFlag(true);
                    ChannelInfoActivity.this.mEditChannel.restoreFineTune();
                    tcf.dismiss();
                    return true;
                } else {
                    if ((keyCode == 21 || keyCode == 22) && action == 0) {
                        ChannelInfoActivity.this.numHz = ChannelInfoActivity.this.mEditChannel.fineTune(ChannelInfoActivity.this.numHz, keyCode);
                        FinetuneDialog finetuneDialog = tcf;
                        finetuneDialog.setAdjustText(numberFormat.format((double) ChannelInfoActivity.this.numHz) + "MHz");
                    }
                    return false;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showStoreChannelDialog() {
        this.storeChannelDialog = new LiveTVDialog(this.mContext, "", this.mContext.getString(R.string.menu_tv_store_data), 3);
        this.storeChannelDialog.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.storeChannelDialog.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.storeChannelDialog.bindKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23)) {
                    return false;
                }
                if (v.getId() == ChannelInfoActivity.this.storeChannelDialog.getButtonYes().getId()) {
                    ChannelInfoActivity.this.dispatchKeyEvent(new KeyEvent(0, 4));
                } else {
                    v.getId();
                    ChannelInfoActivity.this.storeChannelDialog.getButtonNo().getId();
                }
                ChannelInfoActivity.this.storeChannelDialog.dismiss();
                return true;
            }
        });
        this.storeChannelDialog.show();
    }

    public void enterEditDetailItem(String[] mData2) {
        MtkLog.d(TAG, "enterEditDetailItem:" + this.mListView.getSelectedItemPosition());
        this.theSelectChannelPosition = this.mListView.getSelectedItemPosition();
        this.mDetailAdapter = new EditDetailAdapter(this.mContext, this.mHelper.getChannelEditDetail(mData2));
        if (this.mListView.getAdapter() != null) {
            this.mListView.setAdapter(this.mDetailAdapter);
        }
        this.pagebtnLayout.setVisibility(8);
        if (CommonIntegration.isCNRegion()) {
            this.mChannelMenuViewBottom.setVisibility(8);
        }
    }

    private List<String[]> getSoundTracks() {
        this.mTV.setConfigValue("g_menu__soundtracksinit", 0);
        List<String[]> lists = new ArrayList<>();
        int soundListsize = this.mTV.getConfigValue("g_menu__soundtrackstotal");
        for (int i = 0; i < soundListsize; i++) {
            String soundString = this.mTV.getConfigString("audioinfogetstring_" + i);
            MtkLog.d(TAG, "soundString:" + soundString);
            String[] itemValueStrings = new String[3];
            itemValueStrings[0] = "" + (i + 1);
            if (soundString != null) {
                String[] temp = soundString.split("\\+");
                if (temp.length >= 2) {
                    itemValueStrings[1] = temp[0];
                    itemValueStrings[2] = temp[1];
                } else if (temp.length == 1) {
                    itemValueStrings[1] = temp[0];
                    itemValueStrings[2] = "";
                } else {
                    itemValueStrings[1] = "";
                    itemValueStrings[2] = "";
                }
            }
            lists.add(itemValueStrings);
        }
        return lists;
    }

    private List<String[]> getSoundType() {
        List<String[]> lists = new ArrayList<>();
        this.mTV.setConfigValue("g_menu__audioinfoinit", 0);
        int soundListsize = this.mTV.getConfigValue("g_menu__audioinfototal");
        for (int i = 0; i < soundListsize; i++) {
            String soundString = this.mTV.getConfigString("audioinfogetstring_" + i);
            MtkLog.d(TAG, "VisuallyImpaired:" + soundString);
            String[] itemValueStrings = new String[3];
            if (soundString != null) {
                itemValueStrings[0] = soundString;
                itemValueStrings[1] = "";
                itemValueStrings[2] = "";
            } else {
                itemValueStrings[0] = "";
                itemValueStrings[1] = "";
                itemValueStrings[2] = "";
            }
            lists.add(itemValueStrings);
        }
        return lists;
    }

    private void initMoveDelayDialog() {
        MtkLog.d(TAG, "initMoveDelayDialog()");
        this.pdialog = new ProgressDialog(this);
        this.pdialog.setProgressStyle(0);
        this.pdialog.setTitle(this.mContext.getResources().getString(R.string.menu_tv_move_loading));
        this.pdialog.setCancelable(false);
    }

    private void initSortDelayDialog() {
        MtkLog.d(TAG, "initSortDelayDialog()");
        this.pdialog = new ProgressDialog(this);
        this.pdialog.setProgressStyle(0);
        this.pdialog.setTitle(this.mContext.getResources().getString(R.string.menu_tv_sort_loading));
        this.pdialog.setCancelable(false);
    }
}
