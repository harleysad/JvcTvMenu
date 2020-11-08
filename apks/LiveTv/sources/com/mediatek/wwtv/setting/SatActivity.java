package com.mediatek.wwtv.setting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.ui.ScanDialogActivity;
import com.mediatek.wwtv.setting.fragments.SatListFrag;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.SatDetailUI;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.detailui.ActionFragment;
import com.mediatek.wwtv.setting.widget.detailui.ContentFragment;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SatActivity extends BaseSettingsActivity {
    static final int REQ_EDITTEXT = 35;
    public static boolean isSatListShow = false;
    final int MESSAGE_REFRESH_SIGNAL_QUALITY_LEVEL = 34;
    final int MESSAGE_UPDATE_TP = 33;
    /* access modifiers changed from: private */
    public String TAG = "SatActivity";
    int bandFreqTypeIndex = 0;
    /* access modifiers changed from: private */
    public LiveTVDialog cleanDialog;
    boolean isDualTuner = CommonIntegration.getInstance().isDualTunerEnable();
    private boolean isSingleCable = false;
    private int mAntennaType;
    /* access modifiers changed from: private */
    public Context mContext;
    int mDVBSCurrentOP = -1;
    Handler mHandler = new Handler() {
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: com.mediatek.wwtv.setting.widget.detailui.Action} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: com.mediatek.wwtv.setting.widget.detailui.Action} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: com.mediatek.wwtv.setting.widget.detailui.Action} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r15) {
            /*
                r14 = this;
                int r0 = r15.what
                r1 = 2131361839(0x7f0a002f, float:1.8343442E38)
                switch(r0) {
                    case 33: goto L_0x022e;
                    case 34: goto L_0x000a;
                    default: goto L_0x0008;
                }
            L_0x0008:
                goto L_0x02af
            L_0x000a:
                com.mediatek.wwtv.setting.SatActivity r0 = com.mediatek.wwtv.setting.SatActivity.this
                android.os.Handler r0 = r0.mHandler
                r2 = 34
                r0.removeMessages(r2)
                r0 = 0
                r3 = 0
                com.mediatek.wwtv.setting.SatActivity r4 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r4 = r4.mActionFragment
                boolean r4 = r4 instanceof com.mediatek.wwtv.setting.fragments.ProgressBarFrag
                r5 = 0
                if (r4 == 0) goto L_0x0089
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r1 = r1.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r1 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r1
                android.view.View r1 = r1.getView()
                if (r1 != 0) goto L_0x002b
                return
            L_0x002b:
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r1 = r1.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r1 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r1
                java.lang.String r1 = r1.getActionId()
                if (r1 == 0) goto L_0x005a
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r1 = r1.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r1 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r1
                java.lang.String r1 = r1.getActionId()
                java.lang.String r4 = "DVBS_SIGNAL_LEVEL"
                boolean r1 = r1.equals(r4)
                if (r1 == 0) goto L_0x005a
                int r1 = r15.arg1
                int r1 = java.lang.Math.max(r5, r1)
                com.mediatek.wwtv.setting.SatActivity r4 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r4 = r4.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r4 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r4
                r4.showValue(r1)
                goto L_0x01ed
            L_0x005a:
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r1 = r1.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r1 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r1
                java.lang.String r1 = r1.getActionId()
                if (r1 == 0) goto L_0x01ed
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r1 = r1.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r1 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r1
                java.lang.String r1 = r1.getActionId()
                java.lang.String r4 = "DVBS_SIGNAL_QULITY"
                boolean r1 = r1.equals(r4)
                if (r1 == 0) goto L_0x01ed
                int r1 = r15.arg2
                int r1 = java.lang.Math.max(r5, r1)
                com.mediatek.wwtv.setting.SatActivity r4 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r4 = r4.mActionFragment
                com.mediatek.wwtv.setting.fragments.ProgressBarFrag r4 = (com.mediatek.wwtv.setting.fragments.ProgressBarFrag) r4
                r4.showValue(r1)
                goto L_0x01ed
            L_0x0089:
                r4 = 0
                r6 = 0
                com.mediatek.twoworlds.tv.MtkTvConfig r7 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
                java.lang.String r8 = "g_bs__bs_sat_antenna_type"
                int r7 = r7.getConfigValue(r8)
                r8 = 24
                boolean r8 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r8)
                r9 = 2
                if (r7 != 0) goto L_0x00de
                if (r8 == 0) goto L_0x00de
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r10 = r10.mActionFragment
                boolean r10 = r10 instanceof com.mediatek.wwtv.setting.widget.detailui.ActionFragment
                if (r10 == 0) goto L_0x011d
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r10 = r10.mActionFragment
                com.mediatek.wwtv.setting.widget.detailui.ActionFragment r10 = (com.mediatek.wwtv.setting.widget.detailui.ActionFragment) r10
                android.view.View r10 = r10.getView()
                if (r10 != 0) goto L_0x00b5
                return
            L_0x00b5:
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r10 = r10.mActionFragment
                com.mediatek.wwtv.setting.widget.detailui.ActionFragment r10 = (com.mediatek.wwtv.setting.widget.detailui.ActionFragment) r10
                com.mediatek.wwtv.setting.widget.view.ScrollAdapterView r10 = r10.getScrollAdapterView()
                int r11 = r10.getChildCount()
                if (r11 < r9) goto L_0x00dd
                android.view.View r4 = r10.getChildAt(r5)
                int r9 = r11 + -1
                android.view.View r6 = r10.getChildAt(r9)
                java.lang.Object r9 = r4.getTag(r1)
                r0 = r9
                com.mediatek.wwtv.setting.widget.detailui.Action r0 = (com.mediatek.wwtv.setting.widget.detailui.Action) r0
                java.lang.Object r1 = r6.getTag(r1)
                com.mediatek.wwtv.setting.widget.detailui.Action r1 = (com.mediatek.wwtv.setting.widget.detailui.Action) r1
                r3 = r1
            L_0x00dd:
                goto L_0x011d
            L_0x00de:
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r10 = r10.mActionFragment
                boolean r10 = r10 instanceof com.mediatek.wwtv.setting.widget.detailui.ActionFragment
                if (r10 == 0) goto L_0x011d
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r10 = r10.mActionFragment
                com.mediatek.wwtv.setting.widget.detailui.ActionFragment r10 = (com.mediatek.wwtv.setting.widget.detailui.ActionFragment) r10
                android.view.View r10 = r10.getView()
                if (r10 != 0) goto L_0x00f3
                return
            L_0x00f3:
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r10 = r10.mActionFragment
                com.mediatek.wwtv.setting.widget.detailui.ActionFragment r10 = (com.mediatek.wwtv.setting.widget.detailui.ActionFragment) r10
                com.mediatek.wwtv.setting.widget.view.ScrollAdapterView r10 = r10.getScrollAdapterView()
                int r11 = r10.getChildCount()
                if (r11 < r9) goto L_0x011d
                int r9 = r11 + -1
                android.view.View r4 = r10.getChildAt(r9)
                int r9 = r11 + -2
                android.view.View r6 = r10.getChildAt(r9)
                java.lang.Object r9 = r4.getTag(r1)
                r0 = r9
                com.mediatek.wwtv.setting.widget.detailui.Action r0 = (com.mediatek.wwtv.setting.widget.detailui.Action) r0
                java.lang.Object r1 = r6.getTag(r1)
                r3 = r1
                com.mediatek.wwtv.setting.widget.detailui.Action r3 = (com.mediatek.wwtv.setting.widget.detailui.Action) r3
            L_0x011d:
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.String r1 = r1.TAG
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "refresh dvbs signal quality and level action1:"
                r9.append(r10)
                if (r0 != 0) goto L_0x0132
                java.lang.String r10 = ""
                goto L_0x0134
            L_0x0132:
                java.lang.String r10 = r0.mItemID
            L_0x0134:
                r9.append(r10)
                java.lang.String r10 = ",action2=="
                r9.append(r10)
                if (r3 != 0) goto L_0x0141
                java.lang.String r10 = ""
                goto L_0x0143
            L_0x0141:
                java.lang.String r10 = r3.mItemID
            L_0x0143:
                r9.append(r10)
                java.lang.String r9 = r9.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r9)
                if (r0 == 0) goto L_0x01ed
                java.lang.String r1 = r0.mItemID
                java.lang.String r9 = "DVBS_SIGNAL_LEVEL"
                boolean r1 = r1.equalsIgnoreCase(r9)
                if (r1 == 0) goto L_0x01ed
                if (r3 == 0) goto L_0x01ed
                java.lang.String r1 = r3.mItemID
                java.lang.String r9 = "DVBS_SIGNAL_QULITY"
                boolean r1 = r1.equalsIgnoreCase(r9)
                if (r1 == 0) goto L_0x01ed
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                com.mediatek.wwtv.setting.util.TVContent r1 = r1.mTV
                int r1 = r1.getSignalLevel()
                com.mediatek.wwtv.setting.SatActivity r9 = com.mediatek.wwtv.setting.SatActivity.this
                com.mediatek.wwtv.setting.util.TVContent r9 = r9.mTV
                int r9 = r9.getSignalQuality()
                com.mediatek.wwtv.setting.SatActivity r10 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.String r10 = r10.TAG
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                java.lang.String r12 = "refresh dvbs signal quality and level :value1:"
                r11.append(r12)
                r11.append(r1)
                java.lang.String r12 = ",value2=="
                r11.append(r12)
                r11.append(r9)
                java.lang.String r11 = r11.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r11)
                int r1 = java.lang.Math.max(r5, r1)
                int r9 = java.lang.Math.max(r5, r9)
                r0.mInitValue = r1
                r3.mInitValue = r9
                r0.setDescription((int) r1)
                r3.setDescription((int) r9)
                r10 = 2131361824(0x7f0a0020, float:1.8343411E38)
                android.view.View r11 = r4.findViewById(r10)
                android.widget.TextView r11 = (android.widget.TextView) r11
                android.view.View r10 = r6.findViewById(r10)
                android.widget.TextView r10 = (android.widget.TextView) r10
                java.lang.String r12 = r0.getDescription()
                r11.setText(r12)
                java.lang.String r12 = r0.getDescription()
                boolean r12 = android.text.TextUtils.isEmpty(r12)
                r13 = 8
                if (r12 == 0) goto L_0x01d1
                r12 = r13
                goto L_0x01d3
            L_0x01d1:
                r12 = r5
            L_0x01d3:
                r11.setVisibility(r12)
                java.lang.String r12 = r3.getDescription()
                r10.setText(r12)
                java.lang.String r12 = r3.getDescription()
                boolean r12 = android.text.TextUtils.isEmpty(r12)
                if (r12 == 0) goto L_0x01e9
                goto L_0x01ea
            L_0x01e9:
                r13 = r5
            L_0x01ea:
                r10.setVisibility(r13)
            L_0x01ed:
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.String r1 = r1.TAG
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "refresh  mState:"
                r4.append(r5)
                com.mediatek.wwtv.setting.SatActivity r5 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.Object r5 = r5.mState
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.Object r1 = r1.mState
                if (r1 == 0) goto L_0x02af
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.Object r1 = r1.mState
                com.mediatek.wwtv.setting.widget.detailui.Action$DataType r1 = (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r1
                com.mediatek.wwtv.setting.widget.detailui.Action$DataType r4 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.SATELITEDETAIL
                if (r1 == r4) goto L_0x0225
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.Object r1 = r1.mState
                com.mediatek.wwtv.setting.widget.detailui.Action$DataType r1 = (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r1
                com.mediatek.wwtv.setting.widget.detailui.Action$DataType r4 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.PROGRESSBAR
                if (r1 != r4) goto L_0x02af
            L_0x0225:
                com.mediatek.wwtv.setting.SatActivity r1 = com.mediatek.wwtv.setting.SatActivity.this
                r4 = 1000(0x3e8, double:4.94E-321)
                r1.sendMessageDelayedThread(r2, r4)
                goto L_0x02af
            L_0x022e:
                java.lang.Object r0 = r15.obj
                com.mediatek.wwtv.setting.widget.detailui.Action r0 = (com.mediatek.wwtv.setting.widget.detailui.Action) r0
                com.mediatek.wwtv.setting.widget.detailui.Action$DataType r2 = r0.mDataType
                com.mediatek.wwtv.setting.widget.detailui.Action$DataType r3 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.LASTVIEW
                if (r2 != r3) goto L_0x02af
                java.lang.String r2 = r0.mItemID
                java.lang.String r3 = com.mediatek.wwtv.setting.util.SettingsUtil.SPECIAL_SAT_DETAIL_INFO_ITEM_POL
                boolean r2 = r2.startsWith(r3)
                if (r2 == 0) goto L_0x02af
                com.mediatek.wwtv.setting.SatActivity r2 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.String r2 = r2.TAG
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "after goback for SAT POLA:"
                r3.append(r4)
                java.lang.String r4 = r0.mItemID
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
                com.mediatek.wwtv.setting.SatActivity r2 = com.mediatek.wwtv.setting.SatActivity.this
                android.app.Fragment r2 = r2.mActionFragment
                com.mediatek.wwtv.setting.widget.detailui.ActionFragment r2 = (com.mediatek.wwtv.setting.widget.detailui.ActionFragment) r2
                com.mediatek.wwtv.setting.widget.view.ScrollAdapterView r3 = r2.getScrollAdapterView()
                android.view.View r3 = r3.getSelectedView()
                if (r3 == 0) goto L_0x02ae
                java.lang.Object r1 = r3.getTag(r1)
                com.mediatek.wwtv.setting.widget.detailui.Action r1 = (com.mediatek.wwtv.setting.widget.detailui.Action) r1
                com.mediatek.wwtv.setting.SatActivity r4 = com.mediatek.wwtv.setting.SatActivity.this
                java.lang.String r4 = r4.TAG
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "after goback for SAT pAction:"
                r5.append(r6)
                java.lang.String r6 = r1.mItemID
                r5.append(r6)
                java.lang.String r6 = ",satID=="
                r5.append(r6)
                int r6 = r1.satID
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
                com.mediatek.wwtv.setting.SatActivity r4 = com.mediatek.wwtv.setting.SatActivity.this
                com.mediatek.wwtv.setting.util.MenuDataHelper r4 = r4.mDataHelper
                com.mediatek.wwtv.setting.SatActivity r5 = com.mediatek.wwtv.setting.SatActivity.this
                android.content.Context r5 = r5.mContext
                int r6 = r1.satID
                com.mediatek.wwtv.setting.widget.view.ScrollAdapterView r7 = r2.getScrollAdapterView()
                r4.saveDVBSSatTPInfo((android.content.Context) r5, (int) r6, (com.mediatek.wwtv.setting.widget.view.ScrollAdapterView) r7)
            L_0x02ae:
            L_0x02af:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.SatActivity.AnonymousClass1.handleMessage(android.os.Message):void");
        }
    };
    private String mItemId = "";
    private int selectPos = 0;
    private boolean showSatList = false;
    int subBandFreqTypeIndex = 0;
    int subUserbandIndex = 0;
    int subuserDefineFreq = 0;
    private String title = "";
    int userDefineFreq = 0;
    int userbandIndex = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MtkLog.d(this.TAG, "onCreate()");
        this.mContext = this;
        Intent intent = getIntent();
        this.title = intent.getStringExtra("title");
        this.mItemId = intent.getStringExtra("mItemId");
        this.selectPos = intent.getIntExtra("selectPos", 0);
        this.mAntennaType = TVContent.getInstance(this).getConfigValue("g_bs__bs_sat_antenna_type");
        String str = this.TAG;
        MtkLog.d(str, "mItemId=" + this.mItemId + ",title=" + this.title + ",mAntennaType=" + this.mAntennaType);
        if (this.showSatList) {
            this.mCurrAction = new Action(this.mItemId, this.title, 10004, 10004, 10004, (String[]) null, 1, Action.DataType.SATELITEINFO);
            onActionClicked(this.mCurrAction);
            this.showSatList = false;
        }
        this.showSatList = true;
        SaveValue savevalue = SaveValue.getInstance(this);
        savevalue.saveValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE, 1);
        savevalue.saveValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS, 1);
        savevalue.saveValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL, 0);
        super.onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MtkLog.d(this.TAG, "onResume()");
        Intent intent = getIntent();
        this.title = intent.getStringExtra("title");
        this.mItemId = intent.getStringExtra("mItemId");
        this.selectPos = intent.getIntExtra("selectPos", 0);
        if (this.showSatList) {
            this.mCurrAction = new Action(this.mItemId, this.title, 10004, 10004, 10004, (String[]) null, 1, Action.DataType.SATELITEINFO);
            onActionClicked(this.mCurrAction);
            this.showSatList = false;
        }
        MtkLog.d(this.TAG, "SatListonActionClicked");
        super.onResume();
    }

    public void goBack() {
        if (this.mCurrAction == null) {
            super.goBack();
            return;
        }
        String str = this.TAG;
        MtkLog.d(str, "goBack 0mCurrAction.mItemID : " + this.mCurrAction.mItemID);
        String beforeBackItemId = this.mCurrAction.mItemID;
        if (this.mCurrAction != null && this.mCurrAction.mItemID.equals(SettingsUtil.SPECIAL_SAT_DETAIL_INFO_ITEM_POL)) {
            final int satId = this.mCurrAction.satID;
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    ScanContent.setDVBSFreqToGetSignalQuality(satId);
                }
            }, 1500);
            String str2 = this.TAG;
            MtkLog.d(str2, "goBack 1mCurrAction.mItemID : " + this.mCurrAction.mItemID);
        } else if (beforeBackItemId.equals(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_SET)) {
            saveBandFrequencyToAcfg();
            this.isSingleCable = false;
        }
        String str3 = this.TAG;
        MtkLog.d(str3, "goBack mCurrAction =" + this.mCurrAction.mItemID);
        super.goBack();
    }

    private void saveBandFrequencyToAcfg() {
        int bandFreq;
        int subBandFreq;
        int bandFreq2;
        int bandFreq3;
        List<Action> childList = this.mCurrAction.mSubChildGroup;
        int atnatype = childList.get(0).mInitValue;
        int userBand = childList.get(1).mInitValue;
        String bandFreqDes = childList.get(2).getDescription();
        String userdef = this.mContext.getString(R.string.dvbs_band_freq_user_define);
        if (this.isDualTuner) {
            int subUserBand = childList.get(3).mInitValue;
            String subBandFreqDes = childList.get(4).getDescription();
            String subUserdef = this.mContext.getString(R.string.dvbs_band_freq_user_define);
            if (bandFreqDes.equalsIgnoreCase(userdef)) {
                bandFreq2 = this.userDefineFreq;
                if (subBandFreqDes.equalsIgnoreCase(subUserdef)) {
                    bandFreq3 = this.subuserDefineFreq;
                } else {
                    try {
                        bandFreq3 = Integer.parseInt(subBandFreqDes);
                    } catch (Exception e) {
                        Exception exc = e;
                        MtkLog.d(this.TAG, e.getMessage());
                        bandFreq3 = 950;
                    }
                }
            } else {
                try {
                    subBandFreq = Integer.parseInt(bandFreqDes);
                } catch (Exception e2) {
                    Exception exc2 = e2;
                    MtkLog.d(this.TAG, e2.getMessage());
                    subBandFreq = 950;
                }
                bandFreq2 = subBandFreq;
                if (subBandFreqDes.equalsIgnoreCase(subUserdef) != 0) {
                    bandFreq3 = this.subuserDefineFreq;
                } else {
                    try {
                        bandFreq3 = Integer.parseInt(subBandFreqDes);
                    } catch (Exception e3) {
                        Exception exc3 = e3;
                        MtkLog.d(this.TAG, e3.getMessage());
                        bandFreq3 = 950;
                    }
                }
            }
            int subBandFreq2 = bandFreq3;
            String str = subUserdef;
            ScanContent.getInstance(this.mContext).saveDVBSConfigSetting(this.mContext, atnatype, userBand, bandFreq2, subUserBand, subBandFreq2);
            return;
        }
        if (bandFreqDes.equalsIgnoreCase(userdef)) {
            bandFreq = childList.get(3).mInitValue;
        } else {
            try {
                bandFreq = Integer.parseInt(childList.get(2).getDescription());
            } catch (Exception e4) {
                Exception exc4 = e4;
                MtkLog.d(this.TAG, e4.getMessage());
                bandFreq = 950;
            }
        }
        ScanContent.getInstance(this.mContext).saveDVBSConfigSetting(this.mContext, atnatype, userBand, bandFreq);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void refreshActionList() {
        MtkLog.d(this.TAG, "refreshActionList");
        this.mActions.clear();
        switch ((Action.DataType) this.mState) {
            case SATELITEDETAIL:
                MtkLog.d(this.TAG, "AddSatDetailUI");
                SatDetailUI.getInstance(this.mContext).initSatelliteInfoViews(this.mCurrAction, this.mCurrAction.satID);
                this.mActions.addAll(this.mCurrAction.mSubChildGroup);
                String str = this.TAG;
                MtkLog.d(str, "mCurrAction = " + this.mCurrAction.mItemID + "  mSubChildGroup =" + this.mCurrAction.mSubChildGroup.size() + "satID =" + this.mCurrAction.satID);
                String str2 = this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("mActions.size()=");
                sb.append(this.mActions.size());
                MtkLog.d(str2, sb.toString());
                break;
            case SWICHOPTIONVIEW:
                super.refreshActionList();
                if (this.mCurrAction.mItemID.equals("g_misc__ch_list_type")) {
                    String profileName = MtkTvConfig.getInstance().getConfigString("g_misc__ch_list_slot");
                    if (TextUtils.isEmpty(profileName)) {
                        MtkLog.d(this.TAG, "not change profileName");
                        break;
                    } else {
                        String str3 = this.TAG;
                        MtkLog.d(str3, "change to profileName:" + profileName);
                        ((Action) this.mActions.get(1)).setmTitle(profileName);
                        break;
                    }
                }
                break;
            default:
                super.refreshActionList();
                break;
        }
        if (((Action.DataType) this.mState) == Action.DataType.SATELITEDETAIL) {
            MtkLog.d(this.TAG, "try to refresh dvbs signal quality and level");
            sendMessageDelayedThread(34, MessageType.delayMillis5);
            return;
        }
        MtkLog.d(this.TAG, "remove refresh dvbs signal quality and level");
        this.mHandler.removeMessages(34);
    }

    public void onActionClicked(Action action) {
        String str = this.TAG;
        MtkLog.d(str, "onactionClick action id:" + action.mItemID + ",action name:" + action.getTitle() + ",action dis:" + action.getDescription() + ",action initValue:" + action.mInitValue);
        if (action.mDataType == Action.DataType.SCANROOTVIEW) {
            if (action.mItemID.startsWith(MenuConfigManager.TV_CHANNEL_SCAN_DVBC_OPERATOR)) {
                ScanContent.setOperator(this.mContext, action.getmTitle());
                this.mDataHelper.changeEnable();
            }
        } else if (action.mDataType == Action.DataType.NUMVIEW || action.mDataType == Action.DataType.EDITTEXTVIEW) {
            gotoEditTextAct(action);
            return;
        } else if (action.mDataType == Action.DataType.LEFTRIGHT_VIEW || action.mDataType == Action.DataType.DISEQC12_SAVEINFO) {
            String mId = action.mItemID;
            if (!mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC10_PORT) && !mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC11_PORT)) {
                if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE)) {
                    this.mDataHelper.setDiseqc12MotorPageTuner(mId, action.mInitValue);
                    String str2 = this.TAG;
                    MtkLog.d(str2, "action id:" + action.mItemID + ",action init value:" + action.mInitValue);
                } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST) || mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT)) {
                    this.mDataHelper.setDiseqc12MovementControlPageTuner(mId);
                } else if (action.mDataType == Action.DataType.LEFTRIGHT_HASCHILDVIEW) {
                    if (action.getCallBack() != null) {
                        action.getCallBack().afterOptionValseChanged(action.getDescription());
                    }
                } else if (action.mDataType == Action.DataType.NUMVIEW || action.mDataType == Action.DataType.EDITTEXTVIEW) {
                    gotoEditTextAct(action);
                    return;
                }
            }
            if (action.getBeforeCallBack() != null) {
                action.getBeforeCallBack().beforeValueChanged(action.mPrevInitValue);
            }
            if (action.getCallBack() != null) {
                action.getCallBack().afterOptionValseChanged(action.getDescription());
                return;
            }
            return;
        }
        super.onActionClicked(action);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            String value = data.getStringExtra(SaveValue.GLOBAL_VALUE_VALUE);
            if (this.mActionFragment instanceof ActionFragment) {
                ActionFragment frag = (ActionFragment) this.mActionFragment;
                if (frag.getScrollAdapterView().getSelectedView() != null) {
                    Action selectAction = (Action) frag.getScrollAdapterView().getSelectedView().getTag(R.id.action_title);
                    String str = this.TAG;
                    MtkLog.d(str, "onActivityResult ationId:" + selectAction.mItemID);
                    if (selectAction.mDataType == Action.DataType.NUMVIEW) {
                        int now = Integer.parseInt(value);
                        if (now < selectAction.mStartValue) {
                            now = selectAction.mStartValue;
                        } else if (now > selectAction.mEndValue) {
                            now = selectAction.mEndValue;
                        }
                        selectAction.mInitValue = now;
                        if (selectAction.mItemID.endsWith(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_USERDEF)) {
                            this.userDefineFreq = now;
                        } else if (selectAction.mItemID.equals(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_SUB_USERDEF)) {
                            this.subuserDefineFreq = now;
                        }
                        selectAction.setDescription(now + "");
                        if (selectAction.getTitle().equals(this.mContext.getString(R.string.dvbs_tp_sys_rate)) || selectAction.getTitle().equals(this.mContext.getString(R.string.dvbs_tp_fre))) {
                            MenuDataHelper.getInstance(this.mContext).saveDVBSSatTPInfo(this.mContext, selectAction.satID, frag.getScrollAdapterView());
                            ScanContent.setDVBSFreqToGetSignalQuality(selectAction.satID);
                        } else if (selectAction.mItemID.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE) || selectAction.mItemID.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS)) {
                            SaveValue.getInstance(this.mContext).saveValue(selectAction.mItemID, selectAction.mInitValue);
                        }
                    } else if (selectAction.mDataType == Action.DataType.EDITTEXTVIEW) {
                        selectAction.mOptionValue = new String[]{value};
                        selectAction.setDescription(value);
                        if (selectAction.mItemID.equals(this.mContext.getResources().getString(R.string.dvbs_satellite_name))) {
                            SatDetailUI.getInstance(this.mContext).updateOnlySatelliteName(value);
                        }
                    }
                    ((ActionAdapter) frag.getAdapter()).notifyDataSetChanged();
                }
            }
        }
    }

    public void gotoEditTextAct(Action action) {
        if (action.isEnabled()) {
            Intent intent = new Intent(this.mContext, EditTextActivity.class);
            intent.putExtra("password", false);
            intent.putExtra("description", action.getTitle());
            intent.putExtra("itemId", action.mItemID);
            if (action.mDataType == Action.DataType.NUMVIEW) {
                intent.putExtra("initialText", "" + action.mInitValue);
                intent.putExtra("isDigit", true);
                if (action.mItemID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_TP_ITEMS) || action.mItemID.equals(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_TP_ITEMS)) {
                    intent.putExtra("length", 5);
                } else {
                    intent.putExtra("length", 9);
                }
            } else if (action.mDataType == Action.DataType.EDITTEXTVIEW) {
                intent.putExtra("initialText", action.getDescription());
                intent.putExtra("length", 16);
            }
            startActivityForResult(intent, 35);
            return;
        }
        MtkLog.e(this.TAG, "Option Item needn't go to editText Activity");
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        if (isSatListShow) {
            MtkLog.d(this.TAG, "onKeyUp back");
            finish();
            return true;
        }
        goBack();
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0 && event.getKeyCode() == 183) {
            String str = this.TAG;
            MtkLog.d(str, "Red key down=====isSatListShow:" + isSatListShow);
            if (isSatListShow) {
                this.isSingleCable = true;
                int antennaType = this.mTV.getConfigValue("g_bs__bs_sat_antenna_type");
                String str2 = this.TAG;
                MtkLog.d(str2, "antennaType:" + antennaType);
                if (antennaType != 0) {
                    Action action = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_SET, this.mContext.getString(R.string.dvbs_satellite_antenna_type), 10004, 10004, 10004, (String[]) null, 1, Action.DataType.HAVESUBCHILD);
                    action.mSubChildGroup = new ArrayList();
                    addDVBSAtennaTypeDetail(action);
                    onActionClicked(action);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void addDVBSAtennaTypeDetail(Action parentItem) {
        List<String> subFrequencyList;
        int subLen;
        Action action = parentItem;
        String[] tunerList = null;
        String[] tunerValueList = null;
        if (this.mAntennaType == 1) {
            tunerValueList = this.mResources.getStringArray(R.array.dvbs_user_band_arrays);
            tunerList = new String[tunerValueList.length];
            for (int i = 0; i < tunerValueList.length; i++) {
                tunerList[i] = this.mResources.getString(R.string.dvbs_user_band_item_id, new Object[]{Integer.valueOf(Integer.parseInt(tunerValueList[i]))});
            }
        } else if (this.mAntennaType == 2) {
            tunerValueList = this.mResources.getStringArray(R.array.dvbs_user_jess_band_arrays);
            tunerList = new String[tunerValueList.length];
            for (int i2 = 0; i2 < tunerValueList.length; i2++) {
                tunerList[i2] = this.mResources.getString(R.string.dvbs_user_band_item_id, new Object[]{Integer.valueOf(Integer.parseInt(tunerValueList[i2]))});
            }
        }
        String[] tunerList2 = tunerList;
        String[] strArr = tunerValueList;
        MtkLog.d(this.TAG, "satid=" + this.mCurrAction.satID);
        SatelliteInfo info = ScanContent.getDVBSsatellitesBySatID(this.mContext, this.mCurrAction.satID);
        if (info != null) {
            if (this.isDualTuner) {
                this.userbandIndex = info.getUserBand();
                this.subUserbandIndex = info.getSubUserBand();
            } else {
                this.userbandIndex = info.getUserBand();
            }
        }
        List<String> frequencyList = ScanContent.getSingleCableFreqsList(this.mContext, this.userbandIndex);
        String[] freqArray = (String[]) frequencyList.toArray();
        List<String> subFrequencyList2 = ScanContent.getSingleCableFreqsList(this.mContext, this.subUserbandIndex);
        String[] subFreqArray = (String[]) subFrequencyList2.toArray();
        if (info != null) {
            int bandFreq = info.getBandFreq();
            if (this.isDualTuner) {
                if (frequencyList.contains(bandFreq + "")) {
                    this.bandFreqTypeIndex = frequencyList.indexOf(bandFreq + "");
                } else {
                    this.bandFreqTypeIndex = frequencyList.size() - 1;
                    this.userDefineFreq = bandFreq;
                }
                int subBandFreq = info.getSubBandFreq();
                if (subFrequencyList2.contains(subBandFreq + "")) {
                    this.subBandFreqTypeIndex = subFrequencyList2.indexOf(subBandFreq + "");
                } else {
                    this.subBandFreqTypeIndex = subFrequencyList2.size() - 1;
                    this.subuserDefineFreq = subBandFreq;
                }
            } else {
                if (frequencyList.contains(bandFreq + "")) {
                    this.bandFreqTypeIndex = frequencyList.indexOf(bandFreq + "");
                } else {
                    this.bandFreqTypeIndex = frequencyList.size() - 1;
                    this.userDefineFreq = bandFreq;
                }
            }
        }
        MtkLog.d(this.TAG, "bandFreqTypeIndex=" + this.bandFreqTypeIndex + ",subUserbandIndex=" + this.subUserbandIndex + ",subBandFreqTypeIndex=" + this.subBandFreqTypeIndex + ",userDefineFreq=" + this.userDefineFreq + ",subuserDefineFreq=" + this.subuserDefineFreq);
        action.mSubChildGroup.clear();
        Action satAtnaType = new Action("g_bs__bs_sat_antenna_type", this.mResources.getString(R.string.dvbs_antenna_type), 10004, 10004, this.mAntennaType, new String[]{"Universal", "Single Cable", "Jess SingleCable"}, 1, Action.DataType.SWICHOPTIONVIEW);
        satAtnaType.setEnabled(false);
        String tunerTitle = this.mResources.getString(R.string.dvbs_user_band);
        String[] subTunerValueList = this.mResources.getStringArray(R.array.dvbs_user_band_arrays);
        String[] subTunerList = new String[subTunerValueList.length];
        for (int i3 = 0; i3 < subTunerValueList.length; i3++) {
            subTunerList[i3] = this.mResources.getString(R.string.dvbs_user_band_item_id, new Object[]{Integer.valueOf(Integer.parseInt(subTunerValueList[i3]))});
        }
        String subTunerTitle = this.mResources.getString(R.string.dvbs_sub_user_band);
        Action satUserBand = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_TUNER, tunerTitle, 10004, 10004, this.userbandIndex, tunerList2, 1, Action.DataType.OPTIONVIEW);
        Action satUserDefineFreq = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_SUB_TUNER, subTunerTitle, 10004, 10004, this.subUserbandIndex, subTunerList, 1, Action.DataType.OPTIONVIEW);
        Action satBandFreq = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_BANDFREQ, this.mResources.getString(R.string.dvbs_band_freq), 10004, 10004, this.bandFreqTypeIndex, freqArray, 1, Action.DataType.SWICHOPTIONVIEW);
        Action satBandFreq2 = satBandFreq;
        final Action subSatBandFreq = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_SUB_BANDFREQ, this.mResources.getString(R.string.dvbs_sub_band_frequency), 10004, 10004, this.subBandFreqTypeIndex, subFreqArray, 1, Action.DataType.SWICHOPTIONVIEW);
        Action satUserDefineFreq2 = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_USERDEF, this.mResources.getString(R.string.dvbs_user_define_frequency), 950, 2150, this.userDefineFreq, (String[]) null, 1, Action.DataType.NUMVIEW);
        final Action action2 = new Action(MenuConfigManager.DVBS_SAT_ATENNA_TYPE_SUB_USERDEF, this.mResources.getString(R.string.dvbs_band_freq_user_define), 950, 2150, this.subuserDefineFreq, (String[]) null, 1, Action.DataType.NUMVIEW);
        satAtnaType.mEffectGroup = new ArrayList();
        satAtnaType.mEffectGroup.add(satUserBand);
        satAtnaType.mEffectGroup.add(satUserDefineFreq);
        satAtnaType.mEffectGroup.add(satBandFreq2);
        satAtnaType.mEffectGroup.add(subSatBandFreq);
        satAtnaType.mSwitchHashMap = new HashMap<>();
        Action subSatUserBand = satUserDefineFreq;
        String[] subTunerList2 = subTunerList;
        String[] subTunerValueList2 = subTunerValueList;
        satAtnaType.mSwitchHashMap.put(0, new Boolean[]{false, false, false, false});
        satAtnaType.mSwitchHashMap.put(1, new Boolean[]{true, true, true, true});
        satBandFreq2.mEffectGroup = new ArrayList();
        satBandFreq2.mEffectGroup.add(satUserDefineFreq2);
        subSatBandFreq.mEffectGroup = new ArrayList();
        subSatBandFreq.mEffectGroup.add(action2);
        satBandFreq2.mSwitchHashMap = new HashMap<>();
        int len = satBandFreq2.mOptionValue.length;
        int i4 = 0;
        while (i4 < len) {
            satBandFreq2.mSwitchHashMap.put(Integer.valueOf(i4), new Boolean[]{true});
            i4++;
            satAtnaType = satAtnaType;
        }
        Action satAtnaType2 = satAtnaType;
        subSatBandFreq.mSwitchHashMap = new HashMap<>();
        int subLen2 = subSatBandFreq.mOptionValue.length;
        int i5 = 0;
        while (i5 < subLen2) {
            if (i5 == subLen2 - 1) {
                subLen = subLen2;
                subSatBandFreq.mSwitchHashMap.put(Integer.valueOf(i5), new Boolean[]{true});
                subFrequencyList = subFrequencyList2;
            } else {
                subLen = subLen2;
                subFrequencyList = subFrequencyList2;
                subSatBandFreq.mSwitchHashMap.put(Integer.valueOf(i5), new Boolean[]{false});
            }
            i5++;
            subLen2 = subLen;
            subFrequencyList2 = subFrequencyList;
        }
        String[] strArr2 = subTunerList2;
        final Action action3 = satUserBand;
        Action satAtnaType3 = satAtnaType2;
        String[] strArr3 = subTunerValueList2;
        final Action satAtnaType4 = satBandFreq2;
        Action satUserBand2 = satUserBand;
        final Action satUserBand3 = satAtnaType3;
        Action subSatUserBand2 = subSatUserBand;
        int i6 = subLen2;
        final Action subSatUserBand3 = satUserDefineFreq2;
        int len2 = len;
        List<String> list = subFrequencyList2;
        AnonymousClass3 r15 = r0;
        final Action action4 = subSatUserBand2;
        List<String> list2 = frequencyList;
        final Action action5 = subSatBandFreq;
        SatelliteInfo satelliteInfo = info;
        final Action action6 = action2;
        AnonymousClass3 r0 = new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                SatActivity.this.userbandIndex = 0;
                SatActivity.this.bandFreqTypeIndex = 0;
                action3.mInitValue = SatActivity.this.userbandIndex;
                action3.setDescription(SatActivity.this.userbandIndex);
                satAtnaType4.mInitValue = SatActivity.this.bandFreqTypeIndex;
                satAtnaType4.mOptionValue = (String[]) ScanContent.getSingleCableFreqsList(SatActivity.this.mContext, SatActivity.this.userbandIndex).toArray();
                satAtnaType4.setDescription(SatActivity.this.bandFreqTypeIndex);
                if (satUserBand3.mInitValue == 0) {
                    List<Action> childList = satUserBand3.mParent.mSubChildGroup;
                    if (childList.contains(action3)) {
                        childList.remove(action3);
                        childList.remove(satAtnaType4);
                        childList.remove(subSatUserBand3);
                        childList.remove(action4);
                        childList.remove(action5);
                        childList.remove(action6);
                    }
                } else if (satUserBand3.mInitValue == 1) {
                    List<Action> childList2 = satUserBand3.mParent.mSubChildGroup;
                    if (!childList2.contains(action3)) {
                        childList2.add(1, action3);
                        childList2.add(2, satAtnaType4);
                        String bandFreqDes = satAtnaType4.getDescription();
                        String userDefStr = SatActivity.this.mContext.getString(R.string.dvbs_band_freq_user_define);
                        String subBandFreqDes = action5.getDescription();
                        String subUserDefStr = SatActivity.this.mContext.getString(R.string.dvbs_band_freq_user_define);
                        if (bandFreqDes.equalsIgnoreCase(userDefStr)) {
                            childList2.add(3, subSatUserBand3);
                            childList2.add(4, action4);
                            childList2.add(5, action5);
                            if (subBandFreqDes.equalsIgnoreCase(subUserDefStr)) {
                                childList2.add(6, action6);
                                return;
                            }
                            return;
                        }
                        childList2.add(3, action4);
                        childList2.add(4, action5);
                        if (subBandFreqDes.equalsIgnoreCase(subUserDefStr)) {
                            childList2.add(5, action6);
                        }
                    }
                } else if (satUserBand3.mInitValue == 2) {
                    List<Action> childList3 = satUserBand3.mParent.mSubChildGroup;
                    if (!childList3.contains(action3)) {
                        childList3.add(action3);
                        childList3.add(satAtnaType4);
                        if (satAtnaType4.getDescription().equalsIgnoreCase(SatActivity.this.mContext.getString(R.string.dvbs_band_freq_user_define))) {
                            childList3.add(subSatUserBand3);
                        }
                    }
                }
            }
        };
        final Action satAtnaType5 = satAtnaType3;
        satAtnaType5.setOptionValueChangedCallBack(r15);
        final Action action7 = satUserBand2;
        final Action action8 = satAtnaType5;
        Action satUserBand4 = satUserBand2;
        satUserBand4.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                SatActivity.this.userbandIndex = action7.mInitValue;
                SatActivity.this.bandFreqTypeIndex = 0;
                satAtnaType4.mInitValue = SatActivity.this.bandFreqTypeIndex;
                satAtnaType4.mOptionValue = (String[]) ScanContent.getSingleCableFreqsList(SatActivity.this.mContext, SatActivity.this.userbandIndex).toArray();
                satAtnaType4.setDescription(SatActivity.this.bandFreqTypeIndex);
                List<Action> childList = action8.mParent.mSubChildGroup;
                if (satAtnaType4.mOptionValue[SatActivity.this.bandFreqTypeIndex].equalsIgnoreCase(SatActivity.this.mContext.getString(R.string.dvbs_band_freq_user_define))) {
                    if (!childList.contains(subSatUserBand3)) {
                        subSatUserBand3.setEnabled(true);
                        childList.add(subSatUserBand3);
                    }
                } else if (childList.contains(subSatUserBand3)) {
                    childList.remove(subSatUserBand3);
                }
            }
        });
        final Action subSatUserBand4 = subSatUserBand2;
        subSatUserBand4.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                SatActivity.this.subUserbandIndex = subSatUserBand4.mInitValue;
                SatActivity.this.subBandFreqTypeIndex = 0;
                subSatBandFreq.mInitValue = SatActivity.this.subBandFreqTypeIndex;
                subSatBandFreq.mOptionValue = (String[]) ScanContent.getSingleCableFreqsList(SatActivity.this.mContext, SatActivity.this.subUserbandIndex).toArray();
                subSatBandFreq.setDescription(SatActivity.this.subBandFreqTypeIndex);
            }
        });
        final Action action9 = satBandFreq2;
        final Action action10 = satAtnaType5;
        final Action action11 = satUserDefineFreq2;
        final Action action12 = action2;
        satBandFreq2.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                String access$000 = SatActivity.this.TAG;
                MtkLog.d(access$000, "satBandFreq afterName:" + afterName);
                SatActivity.this.bandFreqTypeIndex = action9.mInitValue;
                List<Action> childList = action10.mParent.mSubChildGroup;
                if (afterName.equalsIgnoreCase(SatActivity.this.mContext.getString(R.string.dvbs_band_freq_user_define))) {
                    if (!childList.contains(action11)) {
                        action11.setEnabled(true);
                        if (childList.contains(action12)) {
                            childList.remove(action12);
                            childList.add(action11);
                            childList.add(action12);
                            return;
                        }
                        childList.add(action11);
                    }
                } else if (childList.contains(action11)) {
                    childList.remove(action11);
                }
            }
        });
        subSatBandFreq.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                String access$000 = SatActivity.this.TAG;
                MtkLog.d(access$000, "subSatBandFreq afterName:" + afterName);
                SatActivity.this.subBandFreqTypeIndex = subSatBandFreq.mInitValue;
                List<Action> childList = satAtnaType5.mParent.mSubChildGroup;
                if (afterName.equalsIgnoreCase(SatActivity.this.mContext.getString(R.string.dvbs_band_freq_user_define))) {
                    if (!childList.contains(action2)) {
                        childList.add(action2);
                    }
                } else if (childList.contains(action2)) {
                    childList.remove(action2);
                }
            }
        });
        action.mSubChildGroup.add(satAtnaType5);
        if (this.mAntennaType != 0 && (this.mAntennaType == 1 || this.mAntennaType == 2)) {
            action.mSubChildGroup.add(satUserBand4);
            action.mSubChildGroup.add(satBandFreq2);
            if (this.isDualTuner) {
                action.mSubChildGroup.add(subSatUserBand4);
                action.mSubChildGroup.add(subSatBandFreq);
            }
            if (this.bandFreqTypeIndex == len2 - 1) {
                action.mSubChildGroup.add(satUserDefineFreq2);
            }
            if (this.subBandFreqTypeIndex == len2 - 1 && this.isDualTuner) {
                action.mSubChildGroup.add(action2);
            }
        }
        satAtnaType5.mParent = action;
        satUserBand4.mParent = action;
        satBandFreq2.mParent = action;
        satUserDefineFreq2.mParent = action;
        subSatUserBand4.mParent = action;
        subSatBandFreq.mParent = action;
        action2.mParent = action;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        MtkLog.d(this.TAG, "onPause()");
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d(this.TAG, "onStop()");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public Object getInitialDataType() {
        return Action.DataType.TOPVIEW;
    }

    /* access modifiers changed from: protected */
    public void updateView() {
        String str;
        MtkLog.d(this.TAG, "updateView");
        String str2 = this.TAG;
        MtkLog.d(str2, "DataType=" + ((Action.DataType) this.mState));
        switch ((Action.DataType) this.mState) {
            case SATELITEDETAIL:
            case SWICHOPTIONVIEW:
            case TOPVIEW:
            case OPTIONVIEW:
            case EFFECTOPTIONVIEW:
            case HAVESUBCHILD:
            case LEFTRIGHT_HASCHILDVIEW:
                refreshActionList();
                String title2 = this.mCurrAction == null ? "Menu TV" : this.mCurrAction.getTitle();
                if (this.mParentAction == null) {
                    str = this.mContext.getResources().getString(R.string.menu_interface_name);
                } else {
                    str = this.mParentAction.getTitle();
                }
                setView(title2, str, "", (int) R.drawable.menu_tv_icon);
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        List<String> operatorListStr;
                        int i = 0;
                        if (SatActivity.this.mCurrAction.mItemID.equals(MenuConfigManager.DVBS_SAT_RE_SCAN)) {
                            List<String> dvbsOPListStr = ScanContent.getDVBSOperatorList(SatActivity.this.mContext);
                            if (dvbsOPListStr != null && dvbsOPListStr.size() > 0) {
                                String opStr = ScanContent.getDVBSCurrentOPStr(SatActivity.this.mContext);
                                while (i < dvbsOPListStr.size()) {
                                    if (dvbsOPListStr.get(i).equalsIgnoreCase(opStr)) {
                                        int position = i;
                                        return;
                                    }
                                    i++;
                                }
                            }
                        } else if (SatActivity.this.mCurrAction.mItemID.equals(MenuConfigManager.TV_CHANNEL_SCAN_DVBC) && (operatorListStr = ScanContent.getCableOperationList(SatActivity.this.mContext)) != null && operatorListStr.size() > 0) {
                            int position2 = 0;
                            int size = operatorListStr.size();
                            String opStr2 = ScanContent.getCurrentOperatorStr(SatActivity.this.mContext);
                            while (true) {
                                if (i >= size) {
                                    break;
                                } else if (operatorListStr.get(i).equalsIgnoreCase(opStr2)) {
                                    position2 = i;
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            ((ActionFragment) SatActivity.this.mActionFragment).getScrollAdapterView().setSelectionSmooth(position2);
                        }
                    }
                }, 500);
                return;
            case SATELITEINFO:
                SatListFrag frag = new SatListFrag();
                frag.setAction(this.mCurrAction);
                frag.setSelectPos(this.selectPos);
                this.mActionFragment = frag;
                setViewWithActionFragment(this.mCurrAction == null ? " TV" : this.mCurrAction.getTitle(), this.mParentAction == null ? this.mContext.getResources().getString(R.string.menu_interface_name) : this.mParentAction.getTitle(), "", R.drawable.menu_tv_icon);
                frag.setNeedCheckM7Scan(true);
                return;
            default:
                return;
        }
    }

    public void showM7LNBScanConfirmDialog() {
        this.cleanDialog = new LiveTVDialog(this.mContext, "", this.mContext.getString(R.string.menu_tv_m7_scan_confirm), 3);
        this.cleanDialog.setButtonYesName("Scan");
        this.cleanDialog.setButtonNoName("Skip Scan");
        this.cleanDialog.bindKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int KeyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (KeyCode != 66 && KeyCode != 23) {
                    return false;
                }
                if (v.getId() == SatActivity.this.cleanDialog.getButtonYes().getId()) {
                    SatActivity.this.startActivityToDoM7LNBScan();
                    SatActivity.this.cleanDialog.dismiss();
                    return true;
                } else if (v.getId() != SatActivity.this.cleanDialog.getButtonNo().getId()) {
                    return true;
                } else {
                    SatActivity.this.cleanDialog.dismiss();
                    return true;
                }
            }
        });
        this.cleanDialog.show();
        this.cleanDialog.getButtonNo().requestFocus();
    }

    /* access modifiers changed from: private */
    public void startActivityToDoM7LNBScan() {
        Intent intent = new Intent(this.mContext, ScanDialogActivity.class);
        intent.putExtra("ActionID", MenuConfigManager.M7_LNB_Scan);
        intent.putExtra("lnb", true);
        this.mContext.startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void setActionsForTopView() {
    }

    /* access modifiers changed from: protected */
    public void setProperty(boolean enable) {
    }

    /* access modifiers changed from: protected */
    public void setViewWithActionFragment(String title2, String breadcrumb, String description, int iconResId) {
        this.mContentFragment = ContentFragment.newInstance(title2, breadcrumb, description, iconResId, getResources().getColor(R.color.icon_background));
        setContentAndActionFragments(this.mContentFragment, this.mActionFragment);
    }

    public void sendMessageDelayedThread(final int what, final long delayMillis) {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                int level = SatActivity.this.mTV.getSignalLevel();
                int quality = SatActivity.this.mTV.getSignalQuality();
                if (!SatActivity.this.isDestroyed()) {
                    Message msg = Message.obtain();
                    msg.what = what;
                    msg.arg1 = level;
                    msg.arg2 = quality;
                    SatActivity.this.mHandler.sendMessageDelayed(msg, delayMillis);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        SatDetailUI.setMySelfNull();
        super.onDestroy();
    }
}
