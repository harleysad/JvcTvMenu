package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.android.tv.onboarding.SetupSourceActivity;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.commonview.CustListView;
import com.mediatek.wwtv.tvcenter.commonview.LoadingDialog;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.adapter.SourceListAdapter;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.Toast;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SourceListView extends NavBasicDialog implements InputSourceManager.ISourceListListener, ComponentStatusListener.ICStatusListener {
    private static final int MSG_OPEN_SOURCE_SETUP = 40964;
    private static final int MSG_START_INPUT = 40962;
    private static final int MSG_UPDATE_SELECTION = 40961;
    private static final int MSG_WAIT_TIME_OUT = 40963;
    /* access modifiers changed from: private */
    public static final String TAG = SourceListView.class.getSimpleName();
    public static int marginX;
    public static int marginY;
    public static int menuHeight = 400;
    public static int menuWidth = 343;
    private final Handler listerner;
    private final View.AccessibilityDelegate mAccDelegate;
    /* access modifiers changed from: private */
    public CommonIntegration mCommonIntegration;
    private Drawable mConflictIcon;
    private List<String> mConflictList;
    private LoadingDialog mDialog;
    /* access modifiers changed from: private */
    public List<Integer> mSourceList;
    private SourceListAdapter mSourceListAdapter;
    /* access modifiers changed from: private */
    public CustListView mSourceListView;
    private Drawable mSourceSelectedIcon;
    private String mSourceTitle;
    private Drawable mSourceUnSelectedIcon;
    private final CustListView.UpDateListView mSoureListUpDate;
    private String mTTSToastString;
    private TextView mTitleTextView;
    private TvCallbackHandler mTvCallbackHandler;
    private MtkTvPWDDialog mtkTvPwd;
    private int selectedSourcePosition;
    private TextToSpeechUtil textToSpeechUtil;
    public int x;
    public int y;

    private void init() {
        this.mtkTvPwd = MtkTvPWDDialog.getInstance();
        this.mSourceListView = (CustListView) findViewById(R.id.nav_source_listview);
        this.mSourceListView.setAccessibilityDelegate(this.mAccDelegate);
        this.mTitleTextView = (TextView) findViewById(R.id.nav_tv_source_title);
        this.mTitleTextView.setImportantForAccessibility(2);
        this.mSourceSelectedIcon = this.mContext.getResources().getDrawable(R.drawable.source_list_selected);
        int icon_w = (int) this.mContext.getResources().getDimension(R.dimen.nav_source_list_item_icon_widgh);
        int icon_h = (int) this.mContext.getResources().getDimension(R.dimen.nav_source_list_item_icon_height);
        this.mSourceSelectedIcon.setBounds(0, 0, icon_w, icon_h);
        this.mSourceUnSelectedIcon = this.mContext.getResources().getDrawable(R.drawable.source_list_selected_nor);
        this.mSourceUnSelectedIcon.setBounds(0, 0, icon_w, icon_h);
        if (MarketRegionInfo.isFunctionSupport(27)) {
            this.mConflictIcon = this.mContext.getResources().getDrawable(R.drawable.translucent_background);
        } else {
            this.mConflictIcon = this.mContext.getResources().getDrawable(R.drawable.nav_source_pip_disable_icon);
        }
        this.mConflictIcon.setBounds(0, 0, icon_w, icon_h);
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.mDialog = new LoadingDialog(this.mContext, R.style.loadding_dialog);
    }

    public void updateSourceListSize(int sourceListSize) {
        if (this.mSourceListView != null) {
            this.mSourceListView.setListCount(sourceListSize);
        }
    }

    private void setWindowPosition() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        TypedValue typedValue = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.nav_source_list_dialog_width, typedValue, true);
        float w_percent = typedValue.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_source_list_dialog_height, typedValue, true);
        float h_percent = typedValue.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_source_list_dialog_bottom_margin, typedValue, true);
        float t_percent = typedValue.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_source_list_dialog_right_margin, typedValue, true);
        marginX = (int) (((float) display.getWidth()) * typedValue.getFloat());
        marginY = (int) (((float) display.getHeight()) * t_percent);
        menuWidth = (int) (((float) display.getWidth()) * w_percent);
        menuHeight = (int) (((float) display.getHeight()) * h_percent);
        lp.width = menuWidth;
        lp.height = menuHeight;
        lp.x += marginX;
        lp.y += marginY;
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
            window.setGravity(8388691);
        } else {
            window.setGravity(8388693);
        }
        window.setAttributes(lp);
    }

    /* access modifiers changed from: private */
    public void setSourceListData() {
        this.mSourceList = TvSingletons.getSingletons().getInputSourceManager().getInputHardwareIdList();
        if (this.mSourceListAdapter == null) {
            this.mSourceListAdapter = new SourceListAdapter(this.mContext, this.mSourceList, this.mConflictList, this.mSourceSelectedIcon, this.mSourceUnSelectedIcon, this.mConflictIcon);
            this.mSourceListView.setAdapter(this.mSourceListAdapter);
        } else {
            this.mSourceListAdapter.updateList(this.mSourceList, this.mConflictList);
        }
        this.mSourceListAdapter.notifyDataSetChanged();
    }

    private void initData() {
        MtkLog.d(TAG, "initData");
        this.mSourceList = TvSingletons.getSingletons().getInputSourceManager().getInputHardwareIdList();
        this.mConflictList = TvSingletons.getSingletons().getInputSourceManager().getConflictSourceList();
        if (this.mSourceList != null) {
            String str = TAG;
            MtkLog.d(str, "mSourceList != null, size=" + this.mSourceList.size());
            this.mSourceListView.initData(this.mSourceList, this.mSourceList.size(), this.mSoureListUpDate, true);
            setSourceListData();
        }
    }

    public SourceListView(Context context) {
        this(context, R.style.dialog);
    }

    public SourceListView(Context context, int theme) {
        super(context, theme);
        this.selectedSourcePosition = 0;
        this.listerner = new Handler() {
            public void handleMessage(Message msg) {
                String access$000 = SourceListView.TAG;
                MtkLog.d(access$000, "handleMessage>>" + msg.what);
                switch (msg.what) {
                    case 40961:
                        SourceListView.this.updateSourceListSelection();
                        return;
                    case 40962:
                        String name = TvSingletons.getSingletons().getInputSourceManager().getCurrentInputSourceName("main");
                        if (name.length() != 0 || msg.arg1 <= 0) {
                            MtkLog.d(SourceListView.TAG, "[InternalHandler] change to current input source");
                            if (InputSourceManager.getInstance().getTvInputInfo("main") == null) {
                                InputSourceManager.getInstance().changeCurrentInputSourceByName(name);
                                return;
                            }
                            return;
                        }
                        String access$0002 = SourceListView.TAG;
                        MtkLog.d(access$0002, "[InternalHandler] change to current input source, " + msg.arg1);
                        Message tmp = Message.obtain();
                        tmp.what = 40962;
                        tmp.arg1 = msg.arg1 - 1;
                        sendMessageDelayed(tmp, 200);
                        return;
                    case 40963:
                        SourceListView.this.dismissWaitDialog();
                        return;
                    case 40964:
                        InputSourceManager inputSourceManager = TvSingletons.getSingletons().getInputSourceManager();
                        CommonIntegration unused = SourceListView.this.mCommonIntegration = CommonIntegration.getInstance();
                        if (inputSourceManager == null || SourceListView.this.mCommonIntegration == null) {
                            String access$0003 = SourceListView.TAG;
                            MtkLog.d(access$0003, "ism: " + inputSourceManager + "mCommonIntegration:" + SourceListView.this.mCommonIntegration);
                            return;
                        }
                        List<TIFChannelInfo> list = TIFChannelManager.getInstance(SourceListView.this.mContext).getTIFChannelInfoBySource(inputSourceManager.getTvInputId("main"));
                        if (list != null && SourceListView.this.mCommonIntegration.is3rdTVSource() && list.size() < 1) {
                            SourceListView.this.dismiss();
                            Intent intent = new Intent(SourceListView.this.mContext, SetupSourceActivity.class);
                            intent.addFlags(268435456);
                            SourceListView.this.mContext.startActivity(intent);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.x = 0;
        this.y = 0;
        this.mSourceList = new ArrayList();
        this.mCommonIntegration = null;
        this.mDialog = null;
        this.mAccDelegate = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                if (SourceListView.this.mSourceListView != host) {
                    MtkLog.d(SourceListView.TAG + "WEIJING", "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
                } else {
                    List<CharSequence> texts = event.getText();
                    if (texts == null || texts.isEmpty()) {
                        MtkLog.d(SourceListView.TAG + "WEIJING", ":" + texts);
                    } else if (event.getEventType() == 32768) {
                        int index = findSelectItem(texts.get(0).toString());
                        MtkLog.d(SourceListView.TAG, "WEIJING index = " + index + "texts =" + texts);
                        SourceListView.this.mSourceListView.setSelection(index);
                        SourceListView.this.startTimeout(10000);
                    } else if (event.getEventType() == 1) {
                        SourceListView.this.handleCenterKey();
                        SourceListView.this.dismiss();
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d(SourceListView.TAG, "Exception " + e);
                    return true;
                }
            }

            private int findSelectItem(String string) {
                if (TextUtils.isEmpty(string) || SourceListView.this.mSourceList.isEmpty()) {
                    return -1;
                }
                try {
                    Map<Integer, String> map = InputUtil.getSourceList(SourceListView.this.mContext);
                    for (int i = 0; i < SourceListView.this.mSourceList.size(); i++) {
                        if (string.equals(map.get(SourceListView.this.mSourceList.get(i)))) {
                            return i;
                        }
                    }
                    return -1;
                } catch (Exception e) {
                    return -1;
                }
            }
        };
        this.mSoureListUpDate = new CustListView.UpDateListView() {
            public void updata() {
                SourceListView.this.setSourceListData();
            }
        };
        this.mTTSToastString = "Please use UP/DOWN key to change source when TalkBack On";
        this.componentID = NavBasic.NAV_COMP_ID_INPUT_SRC;
        ComponentStatusListener.getInstance().addListener(9, this);
        ComponentStatusListener.getInstance().addListener(2, this);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_source_list);
        init();
        setWindowPosition();
        initData();
        this.mTvCallbackHandler = TvCallbackHandler.getInstance();
        this.mTvCallbackHandler.addCallBackListener(this.listerner);
        this.textToSpeechUtil = new TextToSpeechUtil(this.mContext);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if ("sub".equals(CommonIntegration.getInstance().getCurrentFocus())) {
            this.mSourceTitle = this.mContext.getResources().getString(R.string.nav_source_sub_title);
        } else {
            this.mSourceTitle = this.mContext.getResources().getString(R.string.nav_source_main_title);
        }
        if (this.mSourceList != null) {
            updateSourceListSize(this.mSourceList.size());
        }
        this.mTitleTextView.setText(this.mSourceTitle);
    }

    public boolean isKeyHandler(int keyCode) {
        String str = TAG;
        MtkLog.d(str, "isKeyHandler keyCode:" + keyCode);
        if (keyCode == 178) {
            return true;
        }
        if (StateDvr.getInstance() == null || !StateDvr.getInstance().isRunning()) {
            return false;
        }
        StateDvr.getInstance().dissmissBigCtrlBar();
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003b, code lost:
        dismiss(r5.getKeyCode());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004a, code lost:
        if (com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getNativeActiveCompId() != 33554433) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004c, code lost:
        com.mediatek.wwtv.tvcenter.util.MtkLog.d(TAG, "sourcelist,NAV_NATIVE_COMP_ID_MHEG5 active ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0053, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0058, code lost:
        if (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance() == null) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0062, code lost:
        return com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance().KeyHandler(r4, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0063, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean KeyHandler(int r4, android.view.KeyEvent r5, boolean r6) {
        /*
            r3 = this;
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "KeyHandler, keyCode:"
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            int r0 = r5.getKeyCode()
            r1 = 1
            switch(r0) {
                case 7: goto L_0x0065;
                case 8: goto L_0x0065;
                case 9: goto L_0x0065;
                case 10: goto L_0x0065;
                case 11: goto L_0x0065;
                case 12: goto L_0x0065;
                case 13: goto L_0x0065;
                case 14: goto L_0x0065;
                case 15: goto L_0x0065;
                case 16: goto L_0x0065;
                default: goto L_0x001e;
            }
        L_0x001e:
            switch(r0) {
                case 19: goto L_0x0065;
                case 20: goto L_0x0065;
                case 21: goto L_0x0043;
                case 22: goto L_0x0043;
                default: goto L_0x0021;
            }
        L_0x0021:
            switch(r0) {
                case 24: goto L_0x003b;
                case 25: goto L_0x003b;
                default: goto L_0x0024;
            }
        L_0x0024:
            switch(r0) {
                case 164: goto L_0x003b;
                case 165: goto L_0x0065;
                case 166: goto L_0x003b;
                case 167: goto L_0x003b;
                default: goto L_0x0027;
            }
        L_0x0027:
            switch(r0) {
                case 4: goto L_0x0037;
                case 82: goto L_0x003b;
                case 85: goto L_0x0065;
                case 89: goto L_0x0065;
                case 93: goto L_0x0065;
                case 130: goto L_0x0065;
                case 172: goto L_0x003b;
                case 178: goto L_0x002b;
                case 183: goto L_0x0043;
                case 229: goto L_0x003b;
                case 255: goto L_0x0065;
                case 10062: goto L_0x0065;
                case 10467: goto L_0x0065;
                default: goto L_0x002a;
            }
        L_0x002a:
            goto L_0x0054
        L_0x002b:
            r3.handleSourceKey()
            com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener.getInstance()
            r2 = 5
            r0.updateStatus(r2, r4)
            return r1
        L_0x0037:
            r3.dismiss()
            return r1
        L_0x003b:
            int r0 = r5.getKeyCode()
            r3.dismiss(r0)
            goto L_0x0054
        L_0x0043:
            int r0 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getNativeActiveCompId()
            r2 = 33554433(0x2000001, float:9.403956E-38)
            if (r0 != r2) goto L_0x0054
            java.lang.String r0 = TAG
            java.lang.String r2 = "sourcelist,NAV_NATIVE_COMP_ID_MHEG5 active "
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r2)
            return r1
        L_0x0054:
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            if (r0 == 0) goto L_0x0063
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            boolean r0 = r0.KeyHandler(r4, r5)
            return r0
        L_0x0063:
            r0 = 0
            return r0
        L_0x0065:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.SourceListView.KeyHandler(int, android.view.KeyEvent, boolean):boolean");
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777234) {
            return false;
        }
        if (componentID != 16777241) {
            return super.isCoExist(componentID);
        }
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        String str = TAG;
        MtkLog.e(str, "dispatchKeyEvent: keyCode=" + keyCode);
        startTimeout(10000);
        if (MarketRegionInfo.isFunctionSupport(30) && DvrManager.getInstance() != null && !DvrManager.getInstance().pvrIsRecording()) {
            DvrManager.getInstance().uiManager.hiddenAllViews();
        }
        switch (keyCode) {
            case 23:
            case 66:
                if (event.getAction() == 0) {
                    BannerView banner = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                    if (banner != null) {
                        banner.cancelNumChangeChannel();
                    }
                    this.selectedSourcePosition = this.mSourceListView.getSelectedItemPosition();
                    if (this.selectedSourcePosition < 0) {
                        this.selectedSourcePosition = 0;
                    }
                    if (TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager() == null || !SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
                        handleCenterKey();
                        ComponentStatusListener.getInstance().updateStatus(5, keyCode);
                    } else {
                        dismiss();
                        new DvrDialog((Activity) this.mContext, 1, (int) DvrDialog.TYPE_Change_Source, this, 2).show();
                    }
                    return true;
                }
                break;
            case 24:
            case 25:
            case KeyMap.KEYCODE_MTKIR_MUTE /*164*/:
                dismiss();
                break;
            case 92:
            case 93:
                return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void show() {
        MtkLog.d(TAG, "show()");
        if (MarketRegionInfo.isFunctionSupport(30)) {
            DvrManager.getInstance(TurnkeyUiMainActivity.getInstance()).uiManager.hiddenAllViews();
            if (StateDvr.stateDvr != null) {
                StateDvr.getInstance().dissmissBigCtrlBar();
            }
            if (StateDvrPlayback.mStateDvrPlayback != null) {
                StateDvrPlayback.getInstance().dismissBigCtrlBar();
            }
        } else {
            if (StateDvr.stateDvr != null) {
                StateDvr.getInstance().dissmissBigCtrlBar();
            }
            if (StateDvrPlayback.mStateDvrPlayback != null) {
                StateDvrPlayback.getInstance().dismissBigCtrlBar();
            }
        }
        this.mContext.sendBroadcast(new Intent("com.mediatek.dialog.dismiss"));
        MarketRegionInfo.isFunctionSupport(13);
        super.show();
        this.mConflictList = TvSingletons.getSingletons().getInputSourceManager().getConflictSourceList();
        setSourceListData();
        String str = TAG;
        MtkLog.d(str, "mSourceList:" + this.mSourceList + ",mConflictList:" + this.mConflictList);
        updateSourceListSelection();
        startTimeout(10000);
    }

    public void dismiss() {
        MtkLog.d(TAG, "dismiss()");
        if (MarketRegionInfo.isFunctionSupport(13)) {
            TvSingletons.getSingletons().getInputSourceManager().removeListener(this);
        }
        super.dismiss();
        ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
        this.mtkTvPwd = MtkTvPWDDialog.getInstance();
        if (this.mtkTvPwd != null) {
            int PWDShow = this.mtkTvPwd.PWDShow();
        }
        CIMainDialog mCIDialog = (CIMainDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG);
        if (mCIDialog != null && mCIDialog.mCIState.getPinCodeDialog() != null) {
            MtkLog.d(TAG, "dismiss() mCIDialog.mCIState.getPinCodeDialog()");
            mCIDialog.mCIState.getPinCodeDialog().dismiss();
        }
    }

    private void dismiss(int keycode) {
        if (keycode == 82) {
            MtkLog.d(TAG, "dismiss by keycode :KEYCODE_MENU");
            if (MarketRegionInfo.isFunctionSupport(13)) {
                TvSingletons.getSingletons().getInputSourceManager().removeListener(this);
            }
            PwdDialog mPWDDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
            if (mPWDDialog.isShowing()) {
                mPWDDialog.dismiss();
            }
            super.dismiss();
            return;
        }
        dismiss();
    }

    public void handleSourceKey() {
        if (this.textToSpeechUtil.isTTSEnabled()) {
            Toast.makeText(this.mContext, (CharSequence) this.mTTSToastString, 1);
            Toast.show();
            return;
        }
        TurnkeyUiMainActivity.getInstance().setChangeSource(true);
        MtkLog.d(TAG, "handleSourceKey()");
        startTimeout(10000);
        if (this.mDialog == null || !this.mDialog.isShowing()) {
            showWaitDialog();
            TvSingletons.getSingletons().getInputSourceManager().autoChangeToNextInputSource(this.mCommonIntegration.getCurrentFocus());
            if (!MarketRegionInfo.isFunctionSupport(13)) {
                this.mSourceListView.setSelection(this.mSourceList.indexOf(Integer.valueOf(TvSingletons.getSingletons().getInputSourceManager().getCurrentInputSourceHardwareId(CommonIntegration.getInstance().getCurrentFocus()))));
            }
            updateSourceListSelection();
            updateSrcList();
        }
    }

    public void handleCenterKey() {
        String conflictToastString;
        InputSourceManager inputSourceManager = TvSingletons.getSingletons().getInputSourceManager();
        TurnkeyUiMainActivity.getInstance().setChangeSource(true);
        startTimeout(10000);
        int selectedPosition = this.mSourceListView.getSelectedItemPosition();
        int firstVisiblePosition = this.mSourceListView.getFirstVisiblePosition();
        String str = TAG;
        MtkLog.e(str, "handleCenterKey() selectedPosition:" + selectedPosition);
        String str2 = TAG;
        MtkLog.e(str2, "handleCenterKey() firstVisiblePosition:" + firstVisiblePosition);
        if (selectedPosition != this.mSourceList.indexOf(Integer.valueOf(inputSourceManager.getCurrentInputSourceHardwareId(this.mCommonIntegration.getCurrentFocus())))) {
            if (!inputSourceManager.getConflict(this.mSourceList.get(selectedPosition).intValue())) {
                AbstractInput input = InputUtil.getInput(this.mSourceList.get(selectedPosition));
                if (input.isNeedAbortTune()) {
                    Toast.makeText(this.mContext, (CharSequence) this.mContext.getResources().getString(R.string.special_input_toast, new Object[]{InputUtil.getSourceList(this.mContext).get(Integer.valueOf(input.getHardwareId()))}), 0);
                    Toast.show();
                    return;
                }
                Toast.cancel();
                showWaitDialog();
                inputSourceManager.changeInputSourceByHardwareId(this.mSourceList.get(selectedPosition).intValue());
                this.mSourceListAdapter.notifyDataSetChanged();
                dismiss(0);
            } else {
                String conflictToastString2 = this.mContext.getResources().getString(R.string.nav_conflict_source_toast);
                if ("sub".equals(this.mCommonIntegration.getCurrentFocus())) {
                    conflictToastString = String.format(conflictToastString2, new Object[]{"main"});
                } else {
                    conflictToastString = String.format(conflictToastString2, new Object[]{"sub"});
                }
                Toast.makeText(this.mContext.getApplicationContext(), (CharSequence) conflictToastString, 2000);
                Toast.show();
            }
        }
        if (this.mSourceListView.getChildAt(selectedPosition) != null) {
            this.mSourceListView.setSelection(selectedPosition);
        }
    }

    public void updateSrcList() {
        this.mSourceList = InputSourceManager.getInstance().getInputHardwareIdList();
        this.mConflictList = InputSourceManager.getInstance().getConflictSourceList();
        updateSourceListSize(this.mSourceList.size());
        this.mSourceListAdapter.updateList(this.mSourceList, this.mConflictList);
        this.mSourceListAdapter.notifyDataSetChanged();
    }

    private void handleUpDownKey(int keyCode, KeyEvent keyEvent) {
        String str = TAG;
        MtkLog.d(str, "handleUpDownKey, " + keyCode);
        startTimeout(10000);
        int selectedItemPosition1 = this.mSourceList.indexOf(Integer.valueOf(TvSingletons.getSingletons().getInputSourceManager().getCurrentInputSourceHardwareId(this.mCommonIntegration.getCurrentFocus())));
        if (selectedItemPosition1 < 0) {
            selectedItemPosition1 = 0;
        }
        if (this.mSourceListView.getChildAt(selectedItemPosition1) != null) {
            this.mSourceListView.getChildAt(selectedItemPosition1).requestFocusFromTouch();
        }
        this.mSourceListView.onKeyDown(keyCode, keyEvent);
    }

    public void showWaitDialog() {
        if (this.mDialog != null) {
            if (this.mDialog.isShowing()) {
                this.mDialog.dismiss();
            }
            this.mDialog.show();
            this.listerner.sendEmptyMessageDelayed(40963, 1000);
        }
    }

    public void dismissWaitDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 9) {
            Message msg = Message.obtain();
            msg.what = 40962;
            msg.arg1 = 100;
            this.listerner.sendMessage(msg);
        } else if (statusID != 2) {
        } else {
            if (value == 16777233 && isVisible()) {
                dismiss();
            } else if (value == 16777224 && isVisible()) {
                MtkLog.d(TAG, "dismiss~ ");
                dismiss();
            }
        }
    }

    public void onAvailabilityChanged(String inputId, int state) {
        InputSourceManager inputSourceManager = TvSingletons.getSingletons().getInputSourceManager();
        if (!inputSourceManager.getInputSourceStatus().contains(0)) {
            MtkLog.d(TAG, "onAvailabilityChanged");
            this.mConflictList = inputSourceManager.getConflictSourceList();
            this.mSourceListView.initData(this.mSourceList, this.mSourceList.size(), this.mSoureListUpDate, true);
            setSourceListData();
            String str = TAG;
            MtkLog.d(str, "mSourceList:" + this.mSourceList + ",mConflictList:" + this.mConflictList);
            updateSourceListSelection();
        }
    }

    public void updateSourceListSelection() {
        this.mSourceListView.setSelection(this.mSourceList.indexOf(Integer.valueOf(TvSingletons.getSingletons().getInputSourceManager().getCurrentInputSourceHardwareId(this.mCommonIntegration.getCurrentFocus()))));
    }

    public void sendMsgOpenSourceSetup() {
        this.listerner.sendEmptyMessageDelayed(40964, 1500);
        MtkLog.d(TAG, "from inputsourcemanager change to 3rd source.");
    }
}
