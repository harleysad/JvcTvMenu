package com.mediatek.wwtv.tvcenter.nav.view.ciview;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.tv.dialog.PinDialogFragment;
import com.mediatek.twoworlds.tv.MtkTvCIBase;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.PinDialogFragment;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CIMainDialog extends NavBasicDialog {
    static AfterDealCIEnqListener AfdealCiEnqLser = null;
    private static final String MENU_CI_USER_PREFERENCE = "menu_ci_user_preference";
    private static final String MENU_CI_USER_PREFERENCE_AMMI_ID = "menu_ci_user_preference_ammi";
    private static final String MENU_CI_USER_PREFERENCE_BROADCAST_ID = "menu_ci_user_preference_broadcast";
    private static final String MENU_CI_USER_PREFERENCE_DEFAULT_ID = "menu_ci_user_preference_default";
    private static final String TAG = "CIMainDialog";
    private static boolean needShowInfoDialog = true;
    static boolean tryToCamScan = false;
    private final int CHANNEL_LIST_SElECTED_FOR_TTS;
    private final int SElECTED_CHANNEL_CAM_SCAN;
    /* access modifiers changed from: private */
    public boolean bNeedShowCamScan;
    /* access modifiers changed from: private */
    public TurnkeyCommDialog camScanCofirm;
    private LinearLayout ciDialog;
    private CIStateChangedCallBack.CIMenuUpdateListener ciMenuUpdateListener;
    /* access modifiers changed from: private */
    public TvCallbackData data;
    PinDialogFragment dialog;
    private boolean dialogIsShow;
    private final LayoutInflater inflater;
    /* access modifiers changed from: private */
    public boolean isMmiItemBack;
    private byte length;
    /* access modifiers changed from: private */
    public final Map<Integer, Integer> levelSelIdxMap;
    private View.AccessibilityDelegate mAccDelegateforCiList;
    /* access modifiers changed from: private */
    public String[] mCIGroup;
    public CIStateChangedCallBack mCIState;
    /* access modifiers changed from: private */
    public CIViewType mCIViewType;
    private final View.OnKeyListener mCardNameListener;
    private CIListAdapter mCiAdapter;
    /* access modifiers changed from: private */
    public TextView mCiCamMenu;
    private LinearLayout mCiCamMenuLayout;
    private TextView mCiCamScan;
    private PinDialogFragment mCiEnqInput;
    private LinearLayout mCiEnqLayout;
    private TextView mCiEnqName;
    private TextView mCiEnqSubtitle;
    private TextView mCiEnqTitle;
    private TextView mCiInfo;
    private final CIMainDialog mCiMainDialog;
    private TextView mCiMenuBottom;
    private LinearLayout mCiMenuLayout;
    /* access modifiers changed from: private */
    public ListView mCiMenuList;
    private TextView mCiMenuName;
    private TextView mCiMenuSubtitle;
    private TextView mCiMenuTitle;
    /* access modifiers changed from: private */
    public TextView mCiNoCard;
    private LinearLayout mCiNoCardLayout;
    private TextView mCiPinCode;
    private TextView mCiUPAmmi;
    private TextView mCiUPBroadcast;
    private TextView mCiUPDefault;
    /* access modifiers changed from: private */
    public TextView mCiUserPreference;
    private LinearLayout mCiUserPreferenceLayout;
    private final View.OnKeyListener mCiUserPreferenceListener;
    private int mCurrentIndex;
    /* access modifiers changed from: private */
    public String mEditStr;
    private final View.OnKeyListener mEnqInputKeyListener;
    private int mEnqType;
    private boolean mFirstShow;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mInputCharChange;
    private final View.OnKeyListener mMenuListKeyListener;
    private String mPreEditStr;
    TIFChannelManager mTIFChannelManager;
    private final View.OnKeyListener mUserPreferenceKeyListener;
    MenuConfigManager menuConfigManager;
    /* access modifiers changed from: private */
    public int mmiMenuLevel;
    private char num;
    NumberKeyListener numberKeyListener;
    /* access modifiers changed from: private */
    public boolean shouldDialogDismiss;
    private char[] tempChar;

    public interface AfterDealCIEnqListener {
        void enqPinSuccess();

        void enqPinWaitInputState(boolean z);
    }

    public enum CIPinCapsType {
        CI_PIN_CAPS_NONE,
        CI_PIN_CAPS_CAS_ONLY,
        CI_PIN_CAPS_CAS_AND_FTA,
        CI_PIN_CAPS_CAS_ONLY_CACHED,
        CI_PIN_CAPS_CAS_AND_FTA_CACHED
    }

    public enum CIViewType {
        CI_DATA_TYPE_CAM_MENU,
        CI_DATA_TYPE_NO_CARD,
        CI_DATA_TYPE_MENU,
        CI_DATA_TYPE_ENQ,
        CI_DATA_TYPE_USER_PREFERENCE
    }

    static /* synthetic */ int access$208(CIMainDialog x0) {
        int i = x0.mmiMenuLevel;
        x0.mmiMenuLevel = i + 1;
        return i;
    }

    static /* synthetic */ int access$210(CIMainDialog x0) {
        int i = x0.mmiMenuLevel;
        x0.mmiMenuLevel = i - 1;
        return i;
    }

    public static void bindAfterDealCIEnqListener(AfterDealCIEnqListener lster) {
        AfdealCiEnqLser = lster;
    }

    public CIMainDialog(Context context) {
        this(context, R.style.nav_dialog);
    }

    public CIMainDialog(Context context, int theme) {
        super(context, theme);
        this.mCIViewType = CIViewType.CI_DATA_TYPE_CAM_MENU;
        this.mCIState = null;
        this.mEditStr = "";
        this.mCurrentIndex = 0;
        this.mPreEditStr = "";
        this.mFirstShow = false;
        this.mInputCharChange = true;
        this.bNeedShowCamScan = false;
        this.mEnqType = 1;
        this.isMmiItemBack = false;
        this.mmiMenuLevel = 0;
        this.shouldDialogDismiss = false;
        this.dialogIsShow = false;
        this.CHANNEL_LIST_SElECTED_FOR_TTS = 19;
        this.SElECTED_CHANNEL_CAM_SCAN = 20;
        this.mCiUserPreferenceListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if ((keyCode != 66 && keyCode != 23) || v.getId() != R.id.menu_ci_user_preference) {
                    return false;
                }
                CIMainDialog.this.showChildView(CIViewType.CI_DATA_TYPE_USER_PREFERENCE);
                return false;
            }
        };
        this.mCardNameListener = new View.OnKeyListener() {
            /* JADX WARNING: Code restructure failed: missing block: B:51:0x0195, code lost:
                if (com.mediatek.twoworlds.tv.MtkTvCI.getEnqID() != -1) goto L_0x0197;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onKey(android.view.View r6, int r7, android.view.KeyEvent r8) {
                /*
                    r5 = this;
                    r0 = 1
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    boolean r1 = r1.camUpgradeStatus()
                    r2 = 1
                    if (r1 == 0) goto L_0x0014
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.String r3 = "mCardNameListener cam upgrading..., disable key process"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.v(r1, r3)
                    return r2
                L_0x0014:
                    int r1 = r8.getAction()
                    if (r1 != 0) goto L_0x01ad
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "mCardNameListener, keyCode="
                    r3.append(r4)
                    r3.append(r7)
                    java.lang.String r3 = r3.toString()
                    com.mediatek.wwtv.tvcenter.util.MtkLog.v(r1, r3)
                    r1 = 66
                    if (r7 == r1) goto L_0x008f
                    r1 = 23
                    if (r7 != r1) goto L_0x0039
                    goto L_0x008f
                L_0x0039:
                    r1 = 4
                    if (r7 != r1) goto L_0x006e
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.String r3 = "key back"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r1 = r1.mCIViewType
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r3 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.CIViewType.CI_DATA_TYPE_CAM_MENU
                    if (r1 == r3) goto L_0x0066
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    android.widget.TextView r1 = r1.mCiCamMenu
                    if (r1 == 0) goto L_0x0066
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r3 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.CIViewType.CI_DATA_TYPE_CAM_MENU
                    r1.showChildView(r3)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    android.widget.TextView r1 = r1.mCiCamMenu
                    r1.requestFocus()
                    return r2
                L_0x0066:
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    r2 = 0
                    r1.setCurrCIViewType(r2)
                    goto L_0x01ad
                L_0x006e:
                    r1 = 20
                    if (r7 == r1) goto L_0x0086
                    r1 = 19
                    if (r7 != r1) goto L_0x0077
                    goto L_0x0086
                L_0x0077:
                    r1 = 167(0xa7, float:2.34E-43)
                    if (r7 == r1) goto L_0x0083
                    r1 = 166(0xa6, float:2.33E-43)
                    if (r7 == r1) goto L_0x0083
                    r1 = 86
                    if (r7 != r1) goto L_0x01ad
                L_0x0083:
                    r0 = 0
                    goto L_0x01ad
                L_0x0086:
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.String r2 = "do nothing"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                    goto L_0x01ad
                L_0x008f:
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "mCIViewType:"
                    r3.append(r4)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r4 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r4 = r4.mCIViewType
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r1 = r1.mCIViewType
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r3 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.CIViewType.CI_DATA_TYPE_CAM_MENU
                    if (r1 != r3) goto L_0x0154
                    int r1 = r6.getId()
                    r3 = 2131362455(0x7f0a0297, float:1.8344691E38)
                    if (r1 != r3) goto L_0x00da
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r2 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.CIViewType.CI_DATA_TYPE_NO_CARD
                    r1.showChildView(r2)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r2 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    android.content.Context r2 = r2.mContext
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r2 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack.getInstance(r2)
                    java.lang.String r2 = r2.getCIName()
                    r1.showNoCardInfo(r2)
                    goto L_0x01ad
                L_0x00da:
                    int r1 = r6.getId()
                    r3 = 2131362456(0x7f0a0298, float:1.8344693E38)
                    if (r1 != r3) goto L_0x00f7
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.String r3 = "start cam scan true"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    com.mediatek.twoworlds.tv.MtkTvCI r1 = r1.getCIHandle()
                    r1.startCamScan(r2)
                    goto L_0x01ad
                L_0x00f7:
                    int r1 = r6.getId()
                    r2 = 2131362468(0x7f0a02a4, float:1.8344717E38)
                    if (r1 != r2) goto L_0x0109
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$CIViewType r2 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.CIViewType.CI_DATA_TYPE_USER_PREFERENCE
                    r1.showChildView(r2)
                    goto L_0x01ad
                L_0x0109:
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.String r2 = "CIPinCodeDialog show"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    r1.dismiss()
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.android.tv.dialog.PinDialogFragment r1 = r1.dialog
                    if (r1 != 0) goto L_0x013e
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r2 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    android.content.Context r2 = r2.mContext
                    r3 = 2131691371(0x7f0f076b, float:1.9011812E38)
                    java.lang.String r2 = r2.getString(r3)
                    r3 = 8
                    com.android.tv.dialog.PinDialogFragment r2 = com.android.tv.dialog.PinDialogFragment.create((java.lang.String) r2, (int) r3)
                    r1.dialog = r2
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.android.tv.dialog.PinDialogFragment r1 = r1.dialog
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$5$1 r2 = new com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog$5$1
                    r2.<init>()
                    r1.setOnPinCheckCallback(r2)
                L_0x013e:
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.android.tv.dialog.PinDialogFragment r1 = r1.dialog
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r2 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    android.content.Context r2 = r2.mContext
                    com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r2 = (com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity) r2
                    android.app.FragmentManager r2 = r2.getFragmentManager()
                    java.lang.String r3 = "CIPinDialogFragment"
                    r1.show(r2, r3)
                    goto L_0x01ad
                L_0x0154:
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "isCamActive:"
                    r3.append(r4)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r4 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r4 = r4.mCIState
                    boolean r4 = r4.isCamActive()
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    boolean r1 = r1.isCamActive()
                    if (r1 != r2) goto L_0x01ad
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    r1.getCIHandle()
                    int r1 = com.mediatek.twoworlds.tv.MtkTvCI.getMenuListID()
                    r2 = -1
                    if (r1 != r2) goto L_0x0197
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    r1.getCIHandle()
                    int r1 = com.mediatek.twoworlds.tv.MtkTvCI.getEnqID()
                    if (r1 == r2) goto L_0x01a2
                L_0x0197:
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    com.mediatek.twoworlds.tv.MtkTvCI r1 = r1.getCIHandle()
                    r1.setMMICloseDone()
                L_0x01a2:
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack r1 = r1.mCIState
                    com.mediatek.twoworlds.tv.MtkTvCI r1 = r1.getCIHandle()
                    r1.enterMMI()
                L_0x01ad:
                    if (r0 != 0) goto L_0x01ca
                    com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r1 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
                    if (r1 == 0) goto L_0x01ca
                    java.lang.String r1 = "CIMainDialog"
                    java.lang.String r2 = "TurnkeyUiMainActivity"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                    com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.this
                    r1.dismiss()
                    com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r1 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
                    boolean r1 = r1.KeyHandler(r7, r8)
                    return r1
                L_0x01ca:
                    r1 = 0
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog.AnonymousClass5.onKey(android.view.View, int, android.view.KeyEvent):boolean");
            }
        };
        this.mMenuListKeyListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                MtkLog.v(CIMainDialog.TAG, "mMenuListKeyListener, onKey, keyCode=" + keyCode);
                if (CIMainDialog.this.mCIState.camUpgradeStatus()) {
                    MtkLog.v(CIMainDialog.TAG, "mMenuListKeyListener cam upgrading..., disable key process");
                    return true;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    int position = CIMainDialog.this.mCiMenuList.getSelectedItemPosition();
                    if (keyCode == 4) {
                        MtkLog.d(CIMainDialog.TAG, "keycode back");
                        boolean unused = CIMainDialog.this.isMmiItemBack = true;
                        CIMainDialog.access$210(CIMainDialog.this);
                        MtkLog.d(CIMainDialog.TAG, "mmiMenuLevel--");
                        CIMainDialog.this.mCIState.cancelCurrMenu();
                        return true;
                    } else if (keyCode == 23 || keyCode == 66) {
                        if (position < 0) {
                            position = 0;
                        }
                        CIMainDialog.this.mCIState.selectMenuItem(position);
                        CIMainDialog.this.levelSelIdxMap.put(Integer.valueOf(CIMainDialog.this.mmiMenuLevel), Integer.valueOf(CIMainDialog.this.mCiMenuList.getSelectedItemPosition()));
                        MtkLog.d("levelSelIdxMap", "enter pos--idx==" + CIMainDialog.this.mCiMenuList.getSelectedItemPosition() + ",level==" + CIMainDialog.this.mmiMenuLevel);
                        CIMainDialog.access$208(CIMainDialog.this);
                        StringBuilder sb = new StringBuilder();
                        sb.append("mmiMenuLevel++:");
                        sb.append(CIMainDialog.this.mmiMenuLevel);
                        MtkLog.d(CIMainDialog.TAG, sb.toString());
                        return false;
                    } else if (keyCode != 82) {
                        switch (keyCode) {
                            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                                MtkLog.d(CIMainDialog.TAG, "TurnkeyUiMainActivity");
                                if (TurnkeyUiMainActivity.getInstance() == null) {
                                    return false;
                                }
                                MtkLog.d(CIMainDialog.TAG, "TurnkeyUiMainActivity.getInstance()");
                                CIMainDialog.this.dismiss();
                                return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode, event);
                            default:
                                return false;
                        }
                    } else {
                        CIMainDialog.this.dismiss();
                        return true;
                    }
                }
            }
        };
        this.mEnqInputKeyListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    MtkLog.v(CIMainDialog.TAG, "mEnqInputKeyListener,onKey, keyCode=" + keyCode);
                    if (keyCode == 4) {
                        MtkLog.d(CIMainDialog.TAG, "keyback");
                        CIMainDialog.this.mCIState.answerEnquiry(0, "");
                        boolean unused = CIMainDialog.this.isMmiItemBack = true;
                        CIMainDialog.access$210(CIMainDialog.this);
                        return true;
                    } else if (keyCode == 23 || keyCode == 66) {
                        MtkLog.d(CIMainDialog.TAG, "mEditStr:" + CIMainDialog.this.mEditStr);
                        CIMainDialog.this.mCIState.answerEnquiry(1, CIMainDialog.this.mEditStr);
                    }
                }
                return false;
            }
        };
        this.mUserPreferenceKeyListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    MtkLog.v(CIMainDialog.TAG, "mEnqInputKeyListener,onKey, keyCode=" + keyCode);
                    if (keyCode == 4) {
                        CIMainDialog.this.showChildView(CIViewType.CI_DATA_TYPE_CAM_MENU);
                        CIMainDialog.this.mCiUserPreference.requestFocus();
                        return true;
                    } else if (keyCode == 23 || keyCode == 66) {
                        if (v.getId() == R.id.menu_ci_user_preference_default) {
                            CIMainDialog.this.menuConfigManager.setValue(MenuConfigManager.USER_PREFERENCE, 0);
                            CIMainDialog.this.backToLastPage();
                            SaveValue.getInstance(CIMainDialog.this.mContext).saveStrValue(CIMainDialog.MENU_CI_USER_PREFERENCE, CIMainDialog.MENU_CI_USER_PREFERENCE_DEFAULT_ID);
                        } else if (v.getId() == R.id.menu_ci_user_preference_ammi) {
                            CIMainDialog.this.menuConfigManager.setValue(MenuConfigManager.USER_PREFERENCE, 1);
                            CIMainDialog.this.backToLastPage();
                            SaveValue.getInstance(CIMainDialog.this.mContext).saveStrValue(CIMainDialog.MENU_CI_USER_PREFERENCE, CIMainDialog.MENU_CI_USER_PREFERENCE_AMMI_ID);
                        } else if (v.getId() == R.id.menu_ci_user_preference_broadcast) {
                            CIMainDialog.this.menuConfigManager.setValue(MenuConfigManager.USER_PREFERENCE, 2);
                            CIMainDialog.this.backToLastPage();
                            SaveValue.getInstance(CIMainDialog.this.mContext).saveStrValue(CIMainDialog.MENU_CI_USER_PREFERENCE, CIMainDialog.MENU_CI_USER_PREFERENCE_BROADCAST_ID);
                        }
                    }
                }
                return false;
            }
        };
        this.numberKeyListener = new NumberKeyListener() {
            /* access modifiers changed from: protected */
            public char[] getAcceptedChars() {
                return new char[]{'a'};
            }

            public int getInputType() {
                return 0;
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                int i = msg.what;
                boolean isPWDShow1 = false;
                switch (i) {
                    case 19:
                        CIMainDialog.this.mCiMenuList.setSelection(msg.arg1);
                        return;
                    case 20:
                        MtkLog.d(CIMainDialog.TAG, "handleMessage SElECTED_CHANNEL_CAM_SCAN");
                        if (CIMainDialog.this.mTIFChannelManager.hasActiveChannel()) {
                            TIFChannelInfo mTIFChannelInfo = CIMainDialog.this.mTIFChannelManager.getCurrChannelInfo();
                            if (mTIFChannelInfo == null) {
                                MtkLog.d(CIMainDialog.TAG, "handleMessage SElECTED_CHANNEL_CAM_SCAN currentchanenl is null.");
                                if (CIMainDialog.this.mTIFChannelManager.getAllDTVTIFChannels().size() > 0) {
                                    mTIFChannelInfo = CIMainDialog.this.mTIFChannelManager.getAllDTVTIFChannels().get(0);
                                }
                            }
                            MtkLog.d(CIMainDialog.TAG, "handleCIMessage:select channel mTIFChannelInfo :" + mTIFChannelInfo);
                            CIMainDialog.this.mTIFChannelManager.selectChannelByTIFInfo(mTIFChannelInfo);
                            return;
                        }
                        return;
                    default:
                        switch (i) {
                            case MenuConfigManager.BISS_KEY_OPERATE_ADD:
                                MtkLog.d(CIMainDialog.TAG, "enq Closed send msg to dismiss");
                                if (CIMainDialog.this.shouldDialogDismiss) {
                                    boolean unused = CIMainDialog.this.shouldDialogDismiss = false;
                                    if (CIMainDialog.AfdealCiEnqLser != null) {
                                        CIMainDialog.AfdealCiEnqLser.enqPinWaitInputState(false);
                                        CIMainDialog.AfdealCiEnqLser.enqPinSuccess();
                                    }
                                }
                                CIMainDialog.this.dismiss();
                                return;
                            case MenuConfigManager.BISS_KEY_OPERATE_UPDATE:
                                MtkLog.d(CIMainDialog.TAG, "enq enter send remove msg");
                                boolean unused2 = CIMainDialog.this.shouldDialogDismiss = false;
                                if (CIMainDialog.AfdealCiEnqLser != null) {
                                    CIMainDialog.AfdealCiEnqLser.enqPinWaitInputState(true);
                                }
                                removeMessages(MenuConfigManager.BISS_KEY_OPERATE_ADD);
                                return;
                            case MenuConfigManager.BISS_KEY_OPERATE_DELETE:
                                MtkLog.d(CIMainDialog.TAG, "handleCi message delay remove msg");
                                if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
                                    isPWDShow1 = true;
                                }
                                if (!isPWDShow1) {
                                    CIMainDialog.this.handleCIMessage((TvCallbackData) msg.obj);
                                    return;
                                }
                                TvCallbackData unused3 = CIMainDialog.this.data = (TvCallbackData) msg.obj;
                                return;
                            case 244:
                                MtkLog.d(CIMainDialog.TAG, "CIMainDialog after 1min dismiss");
                                CIMainDialog.this.dismiss();
                                return;
                            case 245:
                                MtkLog.d(CIMainDialog.TAG, "Cam  1s no answer , pop toast ");
                                CIMainDialog.this.dismiss();
                                Toast.makeText(CIMainDialog.this.mContext, CIMainDialog.this.mContext.getString(R.string.menu_setup_ci_10s_answer_tip), 1).show();
                                return;
                            case 246:
                                MtkLog.d(CIMainDialog.TAG, "PWD dialog dismiss ,deal ci last mess ");
                                if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
                                    isPWDShow1 = true;
                                }
                                MtkLog.d(CIMainDialog.TAG, "PWD dialog dismiss ,deal ci last mess isPWDShow is" + isPWDShow1);
                                if (CIMainDialog.this.data != null) {
                                    CIMainDialog.this.handleCIMessage(CIMainDialog.this.data);
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                }
            }
        };
        this.mAccDelegateforCiList = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                MtkLog.d(CIMainDialog.TAG, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
                if (CIMainDialog.this.mCiMenuList != host) {
                    MtkLog.d(CIMainDialog.TAG, "host:" + CIMainDialog.this.mCiMenuList + "," + host);
                } else {
                    MtkLog.d(CIMainDialog.TAG, ":host =false");
                    List<CharSequence> texts = event.getText();
                    if (texts == null) {
                        MtkLog.d(CIMainDialog.TAG, "texts :" + texts);
                    } else if (event.getEventType() == 32768) {
                        int index = findSelectItem(texts.get(0).toString());
                        MtkLog.d(CIMainDialog.TAG, ":index =" + index);
                        if (index >= 0) {
                            CIMainDialog.this.mHandler.removeMessages(19);
                            Message msg = Message.obtain();
                            msg.what = 19;
                            msg.arg1 = index;
                            CIMainDialog.this.mHandler.sendMessageDelayed(msg, 400);
                        }
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d(CIMainDialog.TAG, "Exception " + e);
                    return true;
                }
            }

            private int findSelectItem(String text) {
                MtkLog.d(CIMainDialog.TAG, "texts =" + text);
                if (CIMainDialog.this.mCIGroup == null) {
                    return -1;
                }
                for (int i = 0; i < CIMainDialog.this.mCIGroup.length; i++) {
                    MtkLog.d(CIMainDialog.TAG, ":index =" + CIMainDialog.this.mCIGroup[i] + " text = " + text);
                    if (CIMainDialog.this.mCIGroup[i].equals(text)) {
                        return i;
                    }
                }
                return -1;
            }
        };
        this.componentID = NavBasic.NAV_COMP_ID_CI_DIALOG;
        this.levelSelIdxMap = new HashMap();
        this.mCiMainDialog = this;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        initDialog(context);
        this.menuConfigManager = MenuConfigManager.getInstance(context);
        this.mTIFChannelManager = TIFChannelManager.getInstance(context);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
    }

    public void show() {
        if (StateDvrPlayback.getInstance() != null) {
            StateDvrPlayback.getInstance().dismissBigCtrlBar();
        }
        super.show();
        this.mCiInfo.setText(this.mContext.getResources().getString(R.string.ci_title));
        showChildView(this.mCIViewType);
        this.dialogIsShow = true;
    }

    public boolean deinitView() {
        if (!(this.mCiEnqLayout == null || this.mCiEnqInput == null)) {
            FragmentTransaction ft = TurnkeyUiMainActivity.getInstance().getFragmentManager().beginTransaction();
            ft.remove(this.mCiEnqInput);
            ft.commitNow();
        }
        super.deinitView();
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public static void resetTryCamScan() {
        MtkLog.d("NavCI", "resetTryCamScan() tryToCamScan is:" + tryToCamScan);
        if (tryToCamScan) {
            tryToCamScan = false;
        }
    }

    public static void setNeedShowInfoDialog(boolean isSet) {
        needShowInfoDialog = isSet;
    }

    private void setWindowPosition() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        TypedValue sca = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.nav_ci_window_size_width, sca, true);
        float w = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_ci_window_size_height, sca, true);
        float h = sca.getFloat();
        lp.width = (int) (((float) display.getWidth()) * w);
        lp.height = (int) (((float) display.getHeight()) * h);
        lp.gravity = 17;
        window.setAttributes(lp);
    }

    public void setNeedShowCamScan(boolean bNeed) {
        MtkLog.d(TAG, "setNeedShowCamScan:" + bNeed);
        this.bNeedShowCamScan = bNeed;
    }

    public void showChildView(CIViewType viewType) {
        MtkLog.v(TAG, "showChildView, viewType=" + viewType);
        this.mCIViewType = viewType;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        switch (viewType) {
            case CI_DATA_TYPE_CAM_MENU:
                this.ciDialog.removeAllViews();
                this.ciDialog.addView(this.mCiCamMenuLayout, layoutParams);
                this.mCiCamMenu = (TextView) this.mCiCamMenuLayout.findViewById(R.id.menu_ci_cam_menu);
                this.mCiCamMenu.setText(this.mContext.getResources().getString(R.string.menu_setup_ci_cam_menu));
                this.mCiCamMenu.setOnKeyListener(this.mCardNameListener);
                this.mCiUserPreference = (TextView) this.mCiCamMenuLayout.findViewById(R.id.menu_ci_user_preference);
                this.mCiUserPreference.setText(this.mContext.getResources().getString(R.string.menu_ci_user_preference));
                this.mCiUserPreference.setOnKeyListener(this.mCardNameListener);
                int menuId = MtkTvCIBase.getCamPinCaps();
                MtkLog.d(TAG, "showChildView,menuID=" + menuId);
                if (menuId == CIPinCapsType.CI_PIN_CAPS_CAS_ONLY_CACHED.ordinal() || menuId == CIPinCapsType.CI_PIN_CAPS_CAS_AND_FTA_CACHED.ordinal()) {
                    this.mCiPinCode = (TextView) this.mCiCamMenuLayout.findViewById(R.id.menu_ci_pin_code);
                    this.mCiPinCode.setText(this.mContext.getResources().getString(R.string.menu_setup_ci_pin_code));
                    this.mCiPinCode.setVisibility(0);
                    this.mCiPinCode.setOnKeyListener(this.mCardNameListener);
                }
                this.mCiCamScan = (TextView) this.mCiCamMenuLayout.findViewById(R.id.menu_ci_cam_scan);
                this.mCiCamScan.setText(this.mContext.getResources().getString(R.string.menu_ci_cam_scan));
                if (this.bNeedShowCamScan) {
                    this.mCiCamScan.setVisibility(0);
                    this.mCiCamScan.setOnKeyListener(this.mCardNameListener);
                    return;
                } else if (TVContent.getInstance(this.mContext).isConfigVisible(MenuConfigManager.CHANNEL_CAM_PROFILE_SCAN)) {
                    MtkLog.d(TAG, "visible g_misc__cam_profile_scan");
                    this.mCiCamScan.setVisibility(0);
                    this.mCiCamScan.setOnKeyListener(this.mCardNameListener);
                    return;
                } else {
                    this.mCiCamScan.setVisibility(8);
                    return;
                }
            case CI_DATA_TYPE_NO_CARD:
                this.ciDialog.removeAllViews();
                this.ciDialog.addView(this.mCiNoCardLayout, layoutParams);
                this.mCiNoCard = (TextView) this.mCiNoCardLayout.findViewById(R.id.menu_ci_no_card);
                this.mCiNoCard.requestFocus();
                this.mCiNoCard.setOnKeyListener(this.mCardNameListener);
                return;
            case CI_DATA_TYPE_MENU:
                this.ciDialog.removeAllViews();
                this.ciDialog.addView(this.mCiMenuLayout, layoutParams);
                this.mCiMenuTitle = (TextView) this.mCiMenuLayout.findViewById(R.id.menu_ci_main_title);
                this.mCiMenuName = (TextView) this.mCiMenuLayout.findViewById(R.id.menu_ci_main_name);
                this.mCiMenuSubtitle = (TextView) this.mCiMenuLayout.findViewById(R.id.menu_ci_main_subtitle);
                this.mCiMenuBottom = (TextView) this.mCiMenuLayout.findViewById(R.id.menu_ci_main_bottom);
                this.mCiMenuList = (ListView) this.mCiMenuLayout.findViewById(R.id.menu_ci_main_list);
                this.mCiMenuList.setOnKeyListener(this.mMenuListKeyListener);
                this.mCiMenuList.setAccessibilityDelegate(this.mAccDelegateforCiList);
                return;
            case CI_DATA_TYPE_ENQ:
                MtkLog.d(TAG, "CI_DATA_TYPE_ENQ");
                MtkLog.d(TAG, "ciDialog:" + this.ciDialog);
                this.ciDialog.removeAllViews();
                this.ciDialog.addView(this.mCiEnqLayout, layoutParams);
                this.mCiEnqTitle = (TextView) this.mCiEnqLayout.findViewById(R.id.menu_ci_enq_title);
                this.mCiEnqName = (TextView) this.mCiEnqLayout.findViewById(R.id.menu_ci_enq_name);
                this.mCiEnqSubtitle = (TextView) this.mCiEnqLayout.findViewById(R.id.menu_ci_enq_subtitle);
                MtkLog.d(TAG, "ciDialog number key slid.");
                this.mCiEnqInput.setResultListener(new PinDialogFragment.ResultListener() {
                    public void done(String pinCode) {
                        MtkLog.d(CIMainDialog.TAG, "answerEnquiry:" + pinCode);
                        CIMainDialog.this.mCIState.answerEnquiry(1, pinCode);
                        CIMainDialog.this.mHandler.removeMessages(245);
                        CIMainDialog.this.mHandler.sendEmptyMessageDelayed(245, 1000);
                    }
                });
                this.mCiEnqInput.setCancelBackListener(new PinDialogFragment.CancelBackListener() {
                    public void cancel() {
                        MtkLog.d(CIMainDialog.TAG, "mCiEnqInput keyback cancel");
                        CIMainDialog.this.mCIState.answerEnquiry(0, "");
                        boolean unused = CIMainDialog.this.isMmiItemBack = true;
                        CIMainDialog.access$210(CIMainDialog.this);
                    }
                });
                this.mCiEnqInput.requestPickerFocus();
                return;
            case CI_DATA_TYPE_USER_PREFERENCE:
                this.ciDialog.removeAllViews();
                this.ciDialog.addView(this.mCiUserPreferenceLayout, layoutParams);
                this.mCiUPDefault = (TextView) this.mCiUserPreferenceLayout.findViewById(R.id.menu_ci_user_preference_default);
                this.mCiUPDefault.setText(this.mContext.getResources().getString(R.string.menu_ci_user_preference_item_default));
                this.mCiUPDefault.setOnKeyListener(this.mUserPreferenceKeyListener);
                this.mCiUPAmmi = (TextView) this.mCiUserPreferenceLayout.findViewById(R.id.menu_ci_user_preference_ammi);
                this.mCiUPAmmi.setText(this.mContext.getResources().getString(R.string.menu_ci_user_preference_item_ammi));
                this.mCiUPAmmi.setOnKeyListener(this.mUserPreferenceKeyListener);
                this.mCiUPBroadcast = (TextView) this.mCiUserPreferenceLayout.findViewById(R.id.menu_ci_user_preference_broadcast);
                this.mCiUPBroadcast.setText(this.mContext.getResources().getString(R.string.menu_ci_user_preference_item_broadcast));
                this.mCiUPBroadcast.setOnKeyListener(this.mUserPreferenceKeyListener);
                String ciValue = SaveValue.getInstance(this.mContext).readStrValue(MENU_CI_USER_PREFERENCE);
                if (MENU_CI_USER_PREFERENCE_DEFAULT_ID.equals(ciValue)) {
                    this.mCiUPDefault.requestFocus();
                    return;
                } else if (MENU_CI_USER_PREFERENCE_AMMI_ID.equals(ciValue)) {
                    this.mCiUPAmmi.requestFocus();
                    return;
                } else if (MENU_CI_USER_PREFERENCE_BROADCAST_ID.equals(ciValue)) {
                    this.mCiUPBroadcast.requestFocus();
                    return;
                } else {
                    this.mCiUPDefault.requestFocus();
                    return;
                }
            default:
                return;
        }
    }

    private void initDialog(Context context) {
        MtkLog.d(TAG, "initDialog");
        this.mCiCamMenuLayout = (LinearLayout) this.inflater.inflate(R.layout.menu_ci_cam_menu, (ViewGroup) null);
        this.mCiNoCardLayout = (LinearLayout) this.inflater.inflate(R.layout.menu_ci_no_card, (ViewGroup) null);
        this.mCiMenuLayout = (LinearLayout) this.inflater.inflate(R.layout.menu_ci_main, (ViewGroup) null);
        this.mCiEnqLayout = (LinearLayout) this.inflater.inflate(R.layout.menu_ci_enq, (ViewGroup) null);
        this.mCiUserPreferenceLayout = (LinearLayout) this.inflater.inflate(R.layout.menu_ci_user_preference, (ViewGroup) null);
        this.mCIState = CIStateChangedCallBack.getInstance(context);
        setContentView(R.layout.ci_dialog);
        setWindowPosition();
        this.ciDialog = (LinearLayout) findViewById(R.id.ci_dialog);
        this.mCiInfo = (TextView) findViewById(R.id.ci_title);
        this.ciMenuUpdateListener = new CIStateChangedCallBack.CIMenuUpdateListener() {
            public void menuEnqClosed() {
                MtkLog.v(CIMainDialog.TAG, "menuEnqClosed");
                CIMainDialog.this.showChildView(CIViewType.CI_DATA_TYPE_NO_CARD);
                CIMainDialog.this.showNoCardInfo(CIMainDialog.this.mCIState.getCIHandle().getCamName());
                CIMainDialog.this.mCiNoCard.requestFocus();
                if (CIMainDialog.this.mCIState.getMtkTvCIMMIEnq() != null) {
                    boolean unused = CIMainDialog.this.shouldDialogDismiss = true;
                }
                CIMainDialog.this.mHandler.sendEmptyMessageDelayed(MenuConfigManager.BISS_KEY_OPERATE_ADD, MessageType.delayMillis5);
            }

            public void enqReceived(MtkTvCIMMIEnqBase enquiry) {
                MtkLog.d(CIMainDialog.TAG, "enqReceived,enquiry:" + enquiry);
                CIMainDialog.this.showChildView(CIViewType.CI_DATA_TYPE_ENQ);
                CIMainDialog.this.showCiEnqInfo(CIMainDialog.this.mCIState.getCIName(), "", enquiry.getText());
                CIMainDialog.this.showCiEnqInfo(CIMainDialog.this.mCIState.getCIHandle().getCamName(), "", enquiry.getText());
                CIMainDialog.this.mHandler.sendEmptyMessage(MenuConfigManager.BISS_KEY_OPERATE_UPDATE);
                CIMainDialog.this.mHandler.removeMessages(244);
            }

            public void menuReceived(MtkTvCIMMIMenuBase menu) {
                MtkLog.v(CIMainDialog.TAG, "menuReceived, menu=" + menu);
                CIMainDialog.this.showChildView(CIViewType.CI_DATA_TYPE_MENU);
                if (menu != null) {
                    if (menu.getItemList() == null) {
                        menu.setItemList(new String[]{"back"});
                    }
                    CIMainDialog.this.showCiMenuInfo(CIMainDialog.this.mCIState.getCIHandle().getCamName(), menu.getTitle(), menu.getSubtitle(), menu.getBottom(), menu.getItemList());
                    CIMainDialog.this.mHandler.removeMessages(244);
                    CIMainDialog.this.mHandler.sendEmptyMessageDelayed(244, MessageType.delayMillis2);
                }
            }

            public void ciRemoved() {
                MtkLog.d(CIMainDialog.TAG, "ciRemoved");
                CIMainDialog.this.showChildView(CIViewType.CI_DATA_TYPE_NO_CARD);
                CIMainDialog.this.showNoCardInfo(CIMainDialog.this.mContext.getString(R.string.menu_setup_ci_no_card));
                boolean unused = CIMainDialog.this.bNeedShowCamScan = false;
            }

            public void ciCamScan(int message) {
                MtkLog.d(CIMainDialog.TAG, "ciCamScan");
                if (CIMainDialog.this.isShowing()) {
                    MtkLog.d(CIMainDialog.TAG, "before ciCamScan dismiss ci info dialog");
                    CIMainDialog.this.dismiss();
                }
                CIMainDialog.this.camScanReqShow(message);
            }
        };
        this.mCiEnqInput = (PinDialogFragment) ((TurnkeyUiMainActivity) this.mContext).getFragmentManager().findFragmentById(R.id.ci_input_pin_code);
    }

    public void handleCIMessageDelay(TvCallbackData data2) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = MenuConfigManager.BISS_KEY_OPERATE_DELETE;
        msg.obj = data2;
        this.mHandler.sendMessageDelayed(msg, 2500);
    }

    public void handleCIMessage(TvCallbackData data2) {
        int navcompid = ComponentsManager.getNativeActiveCompId();
        MtkLog.d(TAG, "handleCIMessage:" + data2.param2 + ",nav-components-id==" + navcompid);
        StringBuilder sb = new StringBuilder();
        sb.append("handleCIMessage: isScanning ,not show dialog  ");
        sb.append(TVContent.getInstance(this.mContext).isScanning());
        MtkLog.d(TAG, sb.toString());
        needShowInfoDialog = true;
        this.mHandler.removeMessages(245);
        CIStateChangedCallBack.getInstance(this.mContext).handleCiCallback(this.mContext, data2, this.ciMenuUpdateListener);
        if (data2.param2 == 6) {
            MtkLog.d(TAG, "handleCIMessage: close CI,not show dialog");
            return;
        }
        if (data2.param2 == 16 || data2.param2 == 18 || data2.param2 == 23) {
            MtkLog.d(TAG, "handleCIMessage ci+1.4:data.param2 £º" + data2.param2);
            MtkLog.d(TAG, "handleCIMessage: not deal with the profile search message");
            needShowInfoDialog = false;
        }
        if (data2.param2 == 17 || data2.param2 == 19) {
            MtkLog.d(TAG, "handleCIMessage:select channel start:");
            needShowInfoDialog = false;
            this.mHandler.removeMessages(20);
            this.mHandler.sendEmptyMessageDelayed(20, 1000);
        }
        if (isShowing()) {
            return;
        }
        if ((this.camScanCofirm == null || !this.camScanCofirm.isShowing()) && needShowInfoDialog) {
            MtkLog.d(TAG, "handleCIMessage:show cidialog:");
            this.mContext.sendBroadcast(new Intent("finish_live_tv_settings"));
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_CI_DIALOG);
            this.mHandler.removeMessages(244);
            this.mHandler.sendEmptyMessageDelayed(244, MessageType.delayMillis2);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        if (this.mCIState.camUpgradeStatus()) {
            return true;
        }
        return super.KeyHandler(keyCode, event, fromNative);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        return KeyHandler(keyCode, event, false);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode;
        if (event.getAction() == 0 && ((keyCode = event.getKeyCode()) == 66 || keyCode == 23 || keyCode == 4 || keyCode == 20 || keyCode == 19)) {
            this.mHandler.removeMessages(244);
            this.mHandler.sendEmptyMessageDelayed(244, MessageType.delayMillis2);
        }
        if (this.mCIState.camUpgradeStatus()) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /* access modifiers changed from: private */
    public void backToLastPage() {
        showChildView(CIViewType.CI_DATA_TYPE_CAM_MENU);
        this.mCiUserPreference.requestFocus();
    }

    public void showNoCardInfo(String cardName) {
        if (cardName == null) {
            cardName = "";
        }
        MtkLog.v(TAG, "showNoCardInfo, cardName=" + cardName);
        this.mmiMenuLevel = 0;
        this.levelSelIdxMap.clear();
        if (cardName == null || cardName.length() == 0) {
            cardName = this.mContext.getString(R.string.menu_setup_ci_no_card);
        }
        this.mCiNoCard.setText(cardName.trim());
    }

    public void showCiMenuInfo(String cardName, String cardTitle, String cardSubtitle, String cardBottom, String[] cardListData) {
        if (cardTitle == null) {
            cardTitle = "";
        }
        if (cardName == null) {
            cardName = "";
        }
        if (cardSubtitle == null) {
            cardSubtitle = "";
        }
        if (cardBottom == null) {
            cardBottom = "";
        }
        MtkLog.v(TAG, "showCiMenuInfo, cardTitle=" + cardName + ",cardName=" + cardTitle + ",cardSubtitle=" + cardSubtitle + ",cardBottom=" + cardBottom);
        this.mCiMenuTitle.setText(cardName.trim());
        this.mCiMenuName.setText(cardTitle.trim());
        this.mCiMenuSubtitle.setText(cardSubtitle.trim());
        this.mCiMenuBottom.setText(cardBottom.trim());
        List<String> tempItemList = new ArrayList<>();
        int length2 = cardListData.length;
        for (int i = 0; i < length2; i++) {
            String s = cardListData[i];
            if (!TextUtils.isEmpty(s)) {
                tempItemList.add(s);
            } else {
                MtkLog.d(TAG, "a empty item so needn't to show");
            }
        }
        this.mCIGroup = (String[]) tempItemList.toArray(new String[0]);
        for (int i2 = 0; i2 < this.mCIGroup.length; i2++) {
            MtkLog.d(TAG, "mCIGroup[" + i2 + "]=" + this.mCIGroup[i2]);
        }
        this.mCiAdapter = new CIListAdapter(this.mContext);
        this.mCiAdapter.setCIGroup(this.mCIGroup);
        this.mCiMenuList.setAdapter(this.mCiAdapter);
        this.mCiAdapter.notifyDataSetChanged();
        this.mCiMenuList.setFocusable(true);
        this.mCiMenuList.requestFocus();
        MtkLog.d(TAG, "isMmiItemBack:" + this.isMmiItemBack + ",levelSelIdxMap:" + this.levelSelIdxMap + ",mmiMenuLevel:" + this.mmiMenuLevel);
        if (this.isMmiItemBack && this.levelSelIdxMap != null && this.levelSelIdxMap.size() != 0) {
            this.isMmiItemBack = false;
            int key = this.mmiMenuLevel - 1;
            if (key < 0) {
                key = 0;
            }
            MtkLog.d("levelSelIdxMap", "idx==" + this.levelSelIdxMap.get(Integer.valueOf(key)).intValue() + ",level==" + key);
            this.mCiMenuList.setSelection(this.levelSelIdxMap.get(Integer.valueOf(key)).intValue());
        }
    }

    public void camScanReqShow(int message) {
        MtkLog.d("NavCI", "camScanReqShow() message is:" + message);
        if (message != 14 || ((this.camScanCofirm == null || !this.camScanCofirm.isShowing()) && !tryToCamScan)) {
            this.mCIState = CIStateChangedCallBack.getInstance(this.mContext);
            if (this.camScanCofirm == null) {
                this.camScanCofirm = new TurnkeyCommDialog(getContext(), 3);
            }
            if (message == 11) {
                this.bNeedShowCamScan = true;
                this.camScanCofirm.setMessage(this.mContext.getString(R.string.cam_scan_warning));
            } else if (message == 12) {
                this.bNeedShowCamScan = true;
                this.camScanCofirm.setMessage(this.mContext.getString(R.string.cam_scan_urgent));
            } else if (message == 13) {
                this.bNeedShowCamScan = true;
                this.camScanCofirm.setMessage(this.mContext.getString(R.string.cam_scan_not_init));
            } else if (message == 14) {
                this.bNeedShowCamScan = true;
                this.camScanCofirm.setMessage(this.mContext.getString(R.string.cam_scan_schedule));
            }
            this.camScanCofirm.setButtonYesName(this.mContext.getString(R.string.menu_ok));
            this.camScanCofirm.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
            this.camScanCofirm.show();
            this.camScanCofirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    int action = event.getAction();
                    MtkLog.d("NavCI", "camScanReqShow back button");
                    if (keyCode != 4 || action != 0) {
                        return false;
                    }
                    MtkLog.d("NavCI", "camScanReqShow startcamScan false");
                    CIMainDialog.this.mCIState.getCIHandle().startCamScan(false);
                    CIMainDialog.this.camScanCofirm.dismiss();
                    return true;
                }
            });
            View.OnKeyListener yesListener = new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != 0) {
                        return false;
                    }
                    MtkLog.d("NavCI", "camScanReqShow yes button");
                    if (keyCode != 66 && keyCode != 23) {
                        return false;
                    }
                    CIMainDialog.tryToCamScan = true;
                    MtkLog.d("NavCI", "camScanReqShow startcamScan true");
                    CIMainDialog.this.mCIState.getCIHandle().startCamScan(true);
                    CIMainDialog.this.camScanCofirm.dismiss();
                    return true;
                }
            };
            this.camScanCofirm.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 0) {
                        MtkLog.d("NavCI", "camScanReqShowe no button");
                        if (keyCode == 66 || keyCode == 23) {
                            MtkLog.d("NavCI", "camScanReqShow startcamScan false");
                            CIMainDialog.this.mCIState.getCIHandle().startCamScan(false);
                            CIMainDialog.this.camScanCofirm.dismiss();
                            return true;
                        }
                    }
                    return false;
                }
            });
            this.camScanCofirm.getButtonYes().setOnKeyListener(yesListener);
            this.camScanCofirm.getButtonNo().requestFocus();
            return;
        }
        MtkLog.d("NavCI", "MTKTV_CI_CAM_SCAN_ENQ_SCHEDULE camScanReqShowed needn't show again try:" + tryToCamScan);
    }

    /* access modifiers changed from: private */
    public void showCiEnqInfo(String cardTitle, String cardName, String cardSubtitle) {
        MtkLog.d(TAG, "showCiEnqInfo:title->" + cardTitle + ",cardName->" + cardName + ",subtitle->" + cardSubtitle);
        if (cardTitle == null) {
            cardTitle = "";
        }
        if (cardName == null) {
            cardName = "";
        }
        if (cardSubtitle == null) {
            cardSubtitle = "";
        }
        MtkLog.v(TAG, "showCiMenuInfo, cardTitle=" + cardTitle + ",cardName=" + cardName + ",cardSubtitle=" + cardSubtitle);
        this.mHandler.removeMessages(1);
        this.mCiEnqTitle.setText(cardTitle.trim());
        this.mCiEnqName.setText(cardName.trim());
        this.mCiEnqSubtitle.setText(cardSubtitle.trim());
        this.mEditStr = "";
        this.mCurrentIndex = 0;
        this.mInputCharChange = false;
        if (this.mCIState.isBlindAns()) {
            this.mEnqType = 1;
            this.mPreEditStr = "_";
        } else {
            this.mEnqType = 0;
            this.mPreEditStr = "_";
        }
        this.mFirstShow = true;
        if ((this.mCIState.getAnsTextLen() & -16) == 0) {
            this.length = this.mCIState.getAnsTextLen();
        } else {
            this.length = 15;
        }
        MtkLog.d(TAG, "---------length------" + this.length);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    private void setPassword() {
        MtkLog.v(TAG, "setPassword,mEnqType:" + this.mEnqType);
        if (this.mEnqType == 1) {
            int i = 0;
            this.mInputCharChange = false;
            this.mEditStr = this.mPreEditStr;
            MtkLog.d(TAG, "mCurrentIndex:" + this.mCurrentIndex + ",mEditStr:" + this.mEditStr + ",length:" + this.length);
            if (this.mCurrentIndex <= this.mEditStr.length() - 1 && this.mEditStr.length() < this.length) {
                this.mCurrentIndex++;
                if (this.mCurrentIndex > this.mEditStr.length() - 1) {
                    this.mPreEditStr += "_";
                    this.tempChar = this.mPreEditStr.toCharArray();
                    while (i < this.tempChar.length - 1) {
                        this.tempChar[i] = '*';
                        i++;
                    }
                    this.tempChar[this.tempChar.length - 1] = '_';
                    return;
                }
                this.tempChar = this.mEditStr.toCharArray();
                while (i < this.mEditStr.length()) {
                    this.tempChar[i] = '*';
                    i++;
                }
            } else if (this.mCurrentIndex <= this.mEditStr.length() - 1 || this.mEditStr.length() >= this.length) {
                this.mCurrentIndex++;
                if (this.mCurrentIndex > this.length - 1) {
                    this.mCurrentIndex = this.length - 1;
                }
                this.tempChar = this.mEditStr.toCharArray();
                while (i < this.mEditStr.length()) {
                    this.tempChar[i] = '*';
                    i++;
                }
            } else {
                this.mCurrentIndex++;
                if (this.mCurrentIndex <= this.length - 1) {
                    this.mPreEditStr += "_";
                    this.tempChar = this.mPreEditStr.toCharArray();
                    while (i < this.tempChar.length - 1) {
                        this.tempChar[i] = '*';
                        i++;
                    }
                    this.tempChar[this.tempChar.length - 1] = '_';
                    return;
                }
                this.mCurrentIndex = this.length - 1;
                this.mPreEditStr += "_";
                this.tempChar = this.mPreEditStr.toCharArray();
                while (i < this.tempChar.length) {
                    this.tempChar[i] = '*';
                    i++;
                }
            }
        }
    }

    public CIViewType getCurrCIViewType() {
        MtkLog.d(TAG, "getCurrCIViewType:" + this.mCIViewType);
        return this.mCIViewType;
    }

    public void setCurrCIViewType(CIViewType type) {
        MtkLog.d(TAG, "setCurrCIViewType:" + type);
        this.mCIViewType = type;
    }

    public void handlerMessage(int code) {
        MtkLog.d(TAG, " handlerMessage code = " + code);
        if ((code == 4 || code == 5 || code == 10 || code == 11) && isVisible()) {
            dismiss();
        }
    }

    public void dismiss() {
        MtkLog.d(TAG, " dismiss cidialog");
        if (this.mCIState != null) {
            this.mCIState.setCIClose();
        }
        boolean isPWDShow = false;
        this.dialogIsShow = false;
        super.dismiss();
        if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
            isPWDShow = true;
        }
        if (isPWDShow) {
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
        }
    }

    public boolean isDialogIsShow() {
        return this.dialogIsShow;
    }

    public void setDialogIsShow(boolean dialogIsShow2) {
        this.dialogIsShow = dialogIsShow2;
    }
}
