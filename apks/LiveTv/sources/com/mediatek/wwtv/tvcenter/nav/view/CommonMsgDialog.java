package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvGingaBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;

public class CommonMsgDialog extends NavBasicDialog {
    private static String TAG = "CommonMsgDialog";
    private static final int TIME_OUT = 5000;
    private static final int WARNING_MSG_BOOK_REMINDER = 2;
    private static final int WARNING_MSG_DIALOG_HIDE = 255;
    private static final int WARNING_MSG_EWS = 0;
    private static final int WARNING_MSG_SPECIAL = 1;
    public static final int WARNING_MSG_STOP_CC_START_GINGA = 4;
    public static final int WARNING_MSG_STOP_GINGA_START_CC = 3;
    private String bookReminderString;
    View.OnClickListener buttonClickListener;
    /* access modifiers changed from: private */
    public boolean isAutoDismiss;
    private final TvCallbackHandler mCallbackHandler;
    /* access modifiers changed from: private */
    public int mChannelID;
    /* access modifiers changed from: private */
    public final CommonIntegration mCommonIntegration;
    private TextView mDialogContentTextView;
    private Button mDialogNoButton;
    private Button mDialogYesButton;
    private final WindowManager.LayoutParams mLayoutParams;
    /* access modifiers changed from: private */
    public int mMsgType;
    private final Window mWindow;
    private final WindowManager mWindowManager;

    public void commonMsgHanndler(int msgType, int channelId, int timeOut, String eventTitle) {
        this.mMsgType = msgType;
        String str = TAG;
        MtkLog.d(str, "commonMsgHanndler||msgType =" + msgType);
        if (msgType != 255) {
            switch (msgType) {
                case 0:
                    if (TVContent.getInstance(this.mContext).isPhiCountry()) {
                        MtkLog.d(TAG, "commonMsgHanndler||isPhiCountry");
                        return;
                    }
                    this.mChannelID = channelId;
                    if (!isShowing()) {
                        TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
                        show();
                    }
                    this.mDialogContentTextView.setText(R.string.common_dialog_ews_service_content);
                    return;
                case 1:
                    this.mChannelID = channelId;
                    if (!isShowing()) {
                        TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
                        show();
                    }
                    this.mDialogContentTextView.setText(R.string.common_dialog_special_service_content);
                    return;
                case 2:
                    this.mChannelID = channelId;
                    this.bookReminderString = eventTitle + " " + this.bookReminderString;
                    String str2 = TAG;
                    MtkLog.d(str2, "come in CommonMsgDialog mDialogMsgHandler,get eventName == " + eventTitle);
                    if (!isShowing()) {
                        show();
                    }
                    this.mDialogContentTextView.setText(this.bookReminderString);
                    String str3 = TAG;
                    MtkLog.d(str3, "come in CommonMsgDialog mDialogMsgHandler,get delay time == " + timeOut);
                    startTimeout(timeOut);
                    return;
                case 3:
                    MtkLog.d(TAG, "come in WARNING_MSG_STOP_GINGA_START_CC");
                    this.mChannelID = channelId;
                    this.isAutoDismiss = true;
                    if (!isShowing()) {
                        TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
                        show();
                    }
                    this.mDialogContentTextView.setText(R.string.common_dialog_start_cc_stop_ginga);
                    startTimeout(5000);
                    return;
                case 4:
                    if (MenuConfigManager.getInstance(this.mContext).getDefault("g_cc__cc_caption") == 0) {
                        MtkLog.d(TAG, "come in WARNING_MSG_STOP_CC_START_GINGA");
                        this.mChannelID = channelId;
                        this.isAutoDismiss = true;
                        if (!isShowing()) {
                            TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
                            show();
                        }
                        this.mDialogContentTextView.setText(R.string.nav_ginga_tv_for_tips);
                        startTimeout(5000);
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else {
            dismiss();
        }
    }

    public void dismiss() {
        super.dismiss();
        if (!this.isAutoDismiss) {
            return;
        }
        if (this.mMsgType == 3) {
            new MtkTvGingaBase().warningStartCC(false);
        } else if (this.mMsgType == 4) {
            new MtkTvGingaBase().warningStartGingaApp(false);
        }
    }

    public boolean isCoExist(int componentID) {
        return true;
    }

    public boolean isKeyHandler(int keyCode) {
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean KeyHandler(int r4, android.view.KeyEvent r5, boolean r6) {
        /*
            r3 = this;
            r0 = 4
            r1 = 1
            if (r4 == r0) goto L_0x003e
            r2 = 82
            if (r4 == r2) goto L_0x0029
            r2 = 172(0xac, float:2.41E-43)
            if (r4 == r2) goto L_0x0029
            switch(r4) {
                case 7: goto L_0x0028;
                case 8: goto L_0x0028;
                case 9: goto L_0x0028;
                case 10: goto L_0x0028;
                case 11: goto L_0x0028;
                case 12: goto L_0x0028;
                case 13: goto L_0x0028;
                case 14: goto L_0x0028;
                case 15: goto L_0x0028;
                case 16: goto L_0x0028;
                default: goto L_0x000f;
            }
        L_0x000f:
            switch(r4) {
                case 19: goto L_0x0028;
                case 20: goto L_0x0028;
                case 21: goto L_0x0019;
                case 22: goto L_0x0019;
                default: goto L_0x0012;
            }
        L_0x0012:
            switch(r4) {
                case 24: goto L_0x0028;
                case 25: goto L_0x0028;
                default: goto L_0x0015;
            }
        L_0x0015:
            switch(r4) {
                case 164: goto L_0x0028;
                case 165: goto L_0x0028;
                default: goto L_0x0018;
            }
        L_0x0018:
            goto L_0x002d
        L_0x0019:
            int r2 = r3.mMsgType
            if (r2 == r0) goto L_0x0022
            int r0 = r3.mMsgType
            r2 = 3
            if (r0 != r2) goto L_0x0027
        L_0x0022:
            r0 = 5000(0x1388, float:7.006E-42)
            r3.startTimeout(r0)
        L_0x0027:
            return r1
        L_0x0028:
            return r1
        L_0x0029:
            r3.dismiss()
        L_0x002d:
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            if (r0 == 0) goto L_0x003c
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            boolean r0 = r0.KeyHandler(r4, r5)
            return r0
        L_0x003c:
            r0 = 0
            return r0
        L_0x003e:
            r3.dismiss()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.CommonMsgDialog.KeyHandler(int, android.view.KeyEvent, boolean):boolean");
    }

    public CommonMsgDialog(Context context) {
        this(context, R.style.nav_dialog);
    }

    public CommonMsgDialog(Context context, int theme) {
        super(context, theme);
        this.isAutoDismiss = true;
        this.buttonClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.comm_dialog_buttonNo:
                        if (CommonMsgDialog.this.mMsgType == 4) {
                            boolean unused = CommonMsgDialog.this.isAutoDismiss = false;
                            new MtkTvGingaBase().warningStartGingaApp(false);
                        } else if (CommonMsgDialog.this.mMsgType == 3) {
                            boolean unused2 = CommonMsgDialog.this.isAutoDismiss = false;
                            new MtkTvGingaBase().warningStartCC(false);
                        }
                        CommonMsgDialog.this.dismiss();
                        return;
                    case R.id.comm_dialog_buttonYes:
                        if (CommonMsgDialog.this.mMsgType == 4) {
                            boolean unused3 = CommonMsgDialog.this.isAutoDismiss = false;
                            new MtkTvGingaBase().warningStartGingaApp(true);
                            InfoBarDialog.getInstance(CommonMsgDialog.this.mContext).show(0, CommonMsgDialog.this.mContext.getString(R.string.nav_ginga_tv_for_infobar));
                        } else if (CommonMsgDialog.this.mMsgType == 3) {
                            boolean unused4 = CommonMsgDialog.this.isAutoDismiss = false;
                            new MtkTvGingaBase().warningStartCC(true);
                        } else {
                            if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
                                DvrManager.getInstance().stopDvr();
                                try {
                                    Thread.sleep(MessageType.delayMillis5);
                                } catch (Exception e) {
                                }
                            }
                            CommonMsgDialog.this.mCommonIntegration.selectChannelById(CommonMsgDialog.this.mChannelID);
                        }
                        CommonMsgDialog.this.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
        this.componentID = NavBasic.NAV_COMP_ID_DIALOG_MSG;
        this.mWindow = getWindow();
        this.mWindowManager = this.mWindow.getWindowManager();
        this.mLayoutParams = this.mWindow.getAttributes();
        this.mCallbackHandler = TvCallbackHandler.getInstance();
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.bookReminderString = context.getResources().getString(R.string.commmon_dialog_book_reminder_content);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_common_msg_dialog);
        initDialogView();
        this.mLayoutParams.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.364d);
        this.mLayoutParams.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.185d);
        this.mWindow.setAttributes(this.mLayoutParams);
    }

    private void initDialogView() {
        this.mDialogContentTextView = (TextView) findViewById(R.id.nav_common_msg_dialog_content);
        this.mDialogYesButton = (Button) findViewById(R.id.comm_dialog_buttonYes);
        this.mDialogYesButton.setText(R.string.common_dialog_msg_yes);
        this.mDialogYesButton.setOnClickListener(this.buttonClickListener);
        this.mDialogNoButton = (Button) findViewById(R.id.comm_dialog_buttonNo);
        this.mDialogNoButton.setText(R.string.common_dialog_msg_no);
        this.mDialogNoButton.setFocusable(true);
        this.mDialogNoButton.requestFocus();
        this.mDialogNoButton.setOnClickListener(this.buttonClickListener);
    }

    public void setDialogPosition(int xOff, int yOff) {
        this.mLayoutParams.x = xOff;
        this.mLayoutParams.y = yOff;
        this.mWindow.setAttributes(this.mLayoutParams);
    }
}
