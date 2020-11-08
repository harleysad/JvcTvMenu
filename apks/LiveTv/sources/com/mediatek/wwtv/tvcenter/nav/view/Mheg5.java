package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvMHEG5;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvSubtitle;
import com.mediatek.twoworlds.tv.MtkTvSubtitleBase;
import com.mediatek.twoworlds.tv.model.MtkTvMHEG5PfgBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.ConfirmDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class Mheg5 extends NavBasicMisc implements ComponentStatusListener.ICStatusListener, ConfirmDialog.IResultCallback {
    private static final String TAG = "Mheg5";
    private InternalPINDialog dialog = null;
    private int mConflictDialogType = -1;
    private int mMheg5MessageUpdateType = -1;
    /* access modifiers changed from: private */
    public MtkTvMHEG5 mheg = MtkTvMHEG5.getInstance();

    public Mheg5(Context context) {
        super(context);
        this.componentID = 33554433;
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(10, this);
    }

    public boolean isVisible() {
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        return false;
    }

    public boolean initView() {
        return false;
    }

    public boolean deinitView() {
        this.dialog = null;
        return false;
    }

    public void handlerMheg5Message(int message, int param2) {
        MtkLog.e(TAG, "handlerMheg5Message, message = " + message + ",param2==" + param2);
        this.mMheg5MessageUpdateType = message;
        this.mConflictDialogType = param2;
        if (message != 257) {
            switch (message) {
                case 1:
                    CIMainDialog ciDialog = (CIMainDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG);
                    if (ciDialog != null && ciDialog.isShowing()) {
                        ciDialog.dismiss();
                    }
                    ComponentsManager.updateActiveCompId(true, 33554433);
                    ComponentStatusListener.getInstance().updateStatus(2, 0);
                    return;
                case 2:
                    if (ComponentsManager.getNativeActiveCompId() == 33554433) {
                        ComponentsManager.updateActiveCompId(true, 0);
                        ComponentStatusListener.getInstance().updateStatus(1, 0);
                        return;
                    }
                    return;
                case 3:
                    CIMainDialog ciDialog2 = (CIMainDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG);
                    if (ciDialog2 != null && ciDialog2.isShowing()) {
                        ciDialog2.dismiss();
                    }
                    ComponentsManager.updateActiveCompId(true, 33554433);
                    ComponentStatusListener.getInstance().updateStatus(2, 0);
                    MtkTvMHEG5PfgBase pfg = this.mheg.getPfgInfo();
                    MtkLog.e(TAG, "getPfgInfo:" + pfg.pfgString);
                    if (!pfg.show) {
                        return;
                    }
                    if (this.dialog == null || !this.dialog.isShowing()) {
                        this.dialog = new InternalPINDialog(this.mContext, pfg.pfgString);
                        this.dialog.show();
                        return;
                    }
                    return;
                case 5:
                    ConfirmDialog dialog2 = new ConfirmDialog(this.mContext, this.mContext.getString(R.string.nav_mheg5_tips));
                    dialog2.setTimeout(5000);
                    dialog2.setCallback(this);
                    dialog2.showDialog();
                    return;
                default:
                    return;
            }
        } else {
            String dialogTips = null;
            if (1 == this.mConflictDialogType) {
                dialogTips = this.mContext.getString(R.string.mheg5_show_confict_dialog_tips1);
            } else if (2 == this.mConflictDialogType) {
                dialogTips = this.mContext.getString(R.string.mheg5_show_confict_dialog_tips2);
            } else if (3 == this.mConflictDialogType) {
                dialogTips = this.mContext.getString(R.string.mheg5_show_confict_dialog_tips3);
            }
            ConfirmDialog conflictDialog = new ConfirmDialog(this.mContext, dialogTips);
            conflictDialog.setTimeout(5000);
            conflictDialog.setCallback(this);
            conflictDialog.showDialog();
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus>>" + statusID + ">>>" + value);
        if (statusID == 10) {
            if (ComponentsManager.getNativeActiveCompId() != 33554433) {
            }
        } else if (statusID == 1 && !ComponentsManager.getInstance().isComponentsShow()) {
            ComponentsManager.nativeComponentReActive();
        }
    }

    private class InternalPINDialog extends NavBasicDialog {
        private String mPfg = "";
        private String password = "";
        private TextView pwdError;
        private TextView pwdValue;
        private String showPasswordStr = "";

        public InternalPINDialog(Context context, String pfg) {
            super(context, R.id.nav_tv_pwd_value);
            this.mPfg = pfg;
        }

        public boolean initView() {
            setContentView(R.layout.nav_mheg_pwd_view);
            this.pwdError = (TextView) findViewById(R.id.nav_mheg5_pwd_error);
            this.pwdError.setText(this.mPfg);
            this.pwdError.setVisibility(0);
            this.pwdValue = (TextView) findViewById(R.id.nav_mheg5_pwd_value);
            this.pwdValue.setInputType(0);
            this.pwdValue.setVisibility(0);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            super.onStart();
            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.45d);
            lp.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.15d);
            lp.x = 0;
            lp.y = 0;
            lp.gravity = 17;
            window.setAttributes(lp);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            MtkLog.d(TAG, "dispatchKeyEvent: keyCode=" + keyCode);
            if (event.getAction() == 0) {
                startTimeout(NavBasic.NAV_TIMEOUT_120);
                if (keyCode != 4) {
                    if (keyCode != 23 && keyCode != 33 && keyCode != 66) {
                        switch (keyCode) {
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                                if (this.password.length() < 4) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(this.password);
                                    sb.append(keyCode - 7);
                                    this.password = sb.toString();
                                    this.showPasswordStr += "*";
                                    this.pwdValue.setText(this.showPasswordStr);
                                    if (this.password.length() == 4) {
                                        startTimeout(1000);
                                        break;
                                    }
                                }
                                break;
                        }
                    } else if (this.password.length() == 4) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
            return super.dispatchKeyEvent(event);
        }

        public void dismiss() {
            Mheg5.this.mheg.setPfgResult(MtkTvPWDDialog.getInstance().checkPWD(this.password));
            super.dismiss();
        }
    }

    public void handleUserSelection(int result) {
        MtkLog.d(TAG, "handleUserSelection,result==" + result + ",mMheg5MessageUpdateType==" + this.mMheg5MessageUpdateType);
        if (result == 0) {
            if (257 == this.mMheg5MessageUpdateType) {
                this.mheg.setMheg5Status(this.mMheg5MessageUpdateType, this.mConflictDialogType, 1, 0);
                MtkLog.d(TAG, "handleUserSelection,BTN_YES_CLICK,mConflictDialogType==" + this.mConflictDialogType);
                return;
            }
            MtkTvSubtitle.getInstance().dealStream(MtkTvSubtitleBase.DealType.DEAL_TYPE_STOP_CURRENT.getDealType());
        } else if (257 == this.mMheg5MessageUpdateType) {
            this.mheg.setMheg5Status(this.mMheg5MessageUpdateType, this.mConflictDialogType, 0, 0);
            MtkLog.d(TAG, "handleUserSelection,BTN_NO_CLICK,mConflictDialogType==" + this.mConflictDialogType);
        }
    }
}
