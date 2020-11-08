package com.mediatek.wwtv.tvcenter.nav.view.ciview;

import android.content.Context;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.Constants;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;

public class CIStateChangedCallBack {
    private static CIStateChangedCallBack mCIState = null;
    private static Context mContext;
    private String TAG = "CIStateChangedCallBack";
    private boolean bListObj = false;
    private int camUpgrade = 0;
    private MtkTvCIMMIEnqBase enquiry = null;
    private MtkTvCI mCi = null;
    private String mCiName = null;
    private TvCallbackData mData = null;
    private MtkTvCIMMIMenuBase menu = null;
    private CIPinCodeDialog pincodedialog;
    boolean regStatus = false;
    private int slot_id = 0;

    public interface CIMenuUpdateListener {
        void ciCamScan(int i);

        void ciRemoved();

        void enqReceived(MtkTvCIMMIEnqBase mtkTvCIMMIEnqBase);

        void menuEnqClosed();

        void menuReceived(MtkTvCIMMIMenuBase mtkTvCIMMIMenuBase);
    }

    public enum CIPinCodeReplyType {
        CI_PIN_BAD_CODE,
        CI_PIN_CICAM_BUSY,
        CI_PIN_CODE_CORRECT,
        CI_PIN_CODE_UNCONFIRMED,
        CI_PIN_BLANK_NOT_REQUIRED,
        CI_PIN_CONTENT_SCRAMBLED
    }

    private CIStateChangedCallBack(Context context) {
        mContext = context;
    }

    public void setPinCodeDialog(CIPinCodeDialog dialog) {
        this.pincodedialog = dialog;
    }

    public CIPinCodeDialog getPinCodeDialog() {
        return this.pincodedialog;
    }

    public void setCIClose() {
        if (getCIHandle() != null) {
            this.mCi.setMMIClose();
        }
    }

    public static CIStateChangedCallBack getInstance(Context context) {
        if (mCIState == null) {
            mCIState = new CIStateChangedCallBack(context);
        }
        return mCIState;
    }

    private void camMenuShowRequest(Context megSrc, int msgType) {
        int PWDShow;
        if (InputSourceManager.getInstance().isCurrentTvSource(CommonIntegration.getInstance().getCurrentFocus()) && !InputSourceManager.getInstance().isBlock("TV") && (PWDShow = MtkTvPWDDialog.getInstance().PWDShow()) != 0) {
            switch (PWDShow) {
                case 2:
                case 3:
                    return;
                default:
                    return;
            }
        }
    }

    public void handleCiCallback(Context megSrc, TvCallbackData data, CIMenuUpdateListener listener) {
        String str = this.TAG;
        MtkLog.d(str, "handleCiCallback, " + data.param2);
        try {
            if (data.param1 != this.slot_id) {
                this.slot_id = data.param1;
            }
            switch (data.param2) {
                case 0:
                    if (data.param1 != this.slot_id) {
                        this.slot_id = data.param1;
                        break;
                    }
                    break;
                case 1:
                    CIMainDialog ciMainDialog = (CIMainDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG);
                    if (ciMainDialog != null) {
                        ciMainDialog.showChildView(CIMainDialog.CIViewType.CI_DATA_TYPE_NO_CARD);
                        ciMainDialog.showNoCardInfo(getInstance(mContext).getCIName());
                        if (!ciMainDialog.isShowing()) {
                            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_CI_DIALOG);
                            break;
                        }
                    }
                    break;
                case 2:
                    this.slot_id = 0;
                    this.menu = null;
                    this.enquiry = null;
                    MtkLog.d(this.TAG, "when card remove set upgrade to 0");
                    this.camUpgrade = 0;
                    if (listener != null) {
                        listener.ciRemoved();
                        break;
                    }
                    break;
                case 3:
                    this.enquiry = (MtkTvCIMMIEnqBase) data.paramObj2;
                    CIMainDialog.resetTryCamScan();
                    if (listener != null) {
                        listener.enqReceived(this.enquiry);
                        break;
                    }
                    break;
                case 4:
                case 5:
                    if (data.param2 == 5) {
                        this.bListObj = true;
                    } else {
                        this.bListObj = false;
                    }
                    this.menu = (MtkTvCIMMIMenuBase) data.paramObj1;
                    String str2 = this.TAG;
                    MtkLog.d(str2, "scube, menu=" + ((MtkTvCIMMIMenuBase) data.paramObj1));
                    String str3 = this.TAG;
                    MtkLog.d(str3, "listener:" + listener);
                    if (listener != null) {
                        listener.menuReceived(this.menu);
                    }
                    if (this.bListObj) {
                        if (this.menu.getTitle() != null && this.menu.getTitle().contains("Upgrade") && this.menu.getTitle().contains("Test")) {
                            MtkLog.d(this.TAG, "CI upgrade begin to send upgrade progress");
                            TvCallbackData tdata = new TvCallbackData();
                            tdata.param2 = 8;
                            handleCiCallback(megSrc, tdata, listener);
                            break;
                        } else {
                            String str4 = this.TAG;
                            MtkLog.d(str4, "CI menu title ==" + this.menu.getTitle());
                            break;
                        }
                    }
                    break;
                case 6:
                    if (data.param1 == this.slot_id) {
                        if (getCIHandle() != null) {
                            this.mCi.setMMICloseDone();
                            if (listener != null) {
                                listener.menuEnqClosed();
                            }
                            this.menu = null;
                            this.enquiry = null;
                            break;
                        }
                    } else {
                        String str5 = this.TAG;
                        MtkLog.d(str5, "MTKTV_CI_NFY_COND_MMI_CLOSE, " + data.param1 + "," + this.slot_id);
                        CIMainDialog.setNeedShowInfoDialog(false);
                        return;
                    }
                    break;
                case 7:
                case 8:
                    if (7 != data.param2) {
                        MtkLog.d(this.TAG, "upgrade progressing");
                        this.camUpgrade = 2;
                        break;
                    } else {
                        MtkLog.d(this.TAG, "reday to upgrade");
                        this.camUpgrade = 1;
                        break;
                    }
                case 9:
                case 10:
                    this.camUpgrade = 0;
                    break;
                case 11:
                case 12:
                case 13:
                case 14:
                    listener.ciCamScan(data.param2);
                    break;
                case 15:
                    checkReplyValue(data.param3);
                    break;
            }
            Constants.slot_id = this.slot_id;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean camUpgradeStatus() {
        return this.camUpgrade == 2;
    }

    public MtkTvCI getCIHandle() {
        if (this.slot_id == -1) {
            MtkLog.d(this.TAG, "getCIHandle, null");
            this.mCi = null;
        } else {
            this.mCi = MtkTvCI.getInstance(this.slot_id);
        }
        return this.mCi;
    }

    public String getCIName() {
        if (getCIHandle() != null) {
            this.mCiName = this.mCi.getCamName();
        } else {
            this.mCiName = "";
        }
        String str = this.TAG;
        MtkLog.d(str, "getCIName, name=" + this.mCiName);
        return this.mCiName;
    }

    public MtkTvCIMMIMenuBase getMtkTvCIMMIMenu() {
        String str = this.TAG;
        MtkLog.d(str, "getMtkTvCIMMIMenuBase, menu=" + this.menu);
        return this.menu;
    }

    public MtkTvCIMMIEnqBase getMtkTvCIMMIEnq() {
        String str = this.TAG;
        MtkLog.d(str, "getMtkTvCIMMIEnqBase, enquiry=" + this.enquiry);
        return this.enquiry;
    }

    public void selectMenuItem(int num) {
        String str = this.TAG;
        MtkLog.d(str, "selectMenuItem, num=" + num);
        if (getCIHandle() != null) {
            this.mCi.setMenuAnswer(this.menu.getMMIId(), this.bListObj ? 0 : num + 1);
        }
    }

    public void answerEnquiry(int bAnswer, String data) {
        if (getCIHandle() != null) {
            this.mCi.setEnqAnswer(getMtkTvCIMMIEnq().getMMIId(), bAnswer, data);
        }
    }

    public byte getAnsTextLen() {
        if (this.enquiry == null) {
            return -1;
        }
        String str = this.TAG;
        MtkLog.d(str, "getAnsTextLen, enquiry=" + this.enquiry);
        return this.enquiry.getAnsTextLen();
    }

    public boolean isBlindAns() {
        if (this.enquiry == null) {
            return false;
        }
        String str = this.TAG;
        MtkLog.d(str, "isBlindAns, enquiry=" + this.enquiry);
        return this.enquiry.getBlindAns();
    }

    public int cancelCurrMenu() {
        String str = this.TAG;
        MtkLog.d(str, "cancelCurrMenu, menu=" + this.menu);
        if (getCIHandle() != null) {
            return this.mCi.setMenuAnswer(this.menu.getMMIId(), 0);
        }
        return 0;
    }

    public boolean isCamActive() {
        if (this.mCi != null) {
            return this.mCi.getSlotActive();
        }
        return false;
    }

    public TvCallbackData getReqShowData() {
        MtkLog.d(this.TAG, "getReqShowData");
        return this.mData;
    }

    public void setReqShowData(TvCallbackData data) {
        MtkLog.d(this.TAG, "setReqShowData");
        if (this.mData == null) {
            this.mData = new TvCallbackData();
        }
        this.mData = data;
    }

    private void checkReplyValue(int ret) {
        CIPinCodeReplyType type = CIPinCodeReplyType.values()[ret];
        String str = this.TAG;
        MtkLog.d(str, "CIPinCodeReplyType is " + type);
        switch (type) {
            case CI_PIN_CODE_CORRECT:
                if (this.pincodedialog != null && this.pincodedialog.isShowing()) {
                    this.pincodedialog.dismiss();
                    Toast.makeText(mContext, "correct!", 1).show();
                }
                CIMainDialog.setNeedShowInfoDialog(false);
                return;
            case CI_PIN_CODE_UNCONFIRMED:
            case CI_PIN_CONTENT_SCRAMBLED:
            case CI_PIN_CICAM_BUSY:
                MtkLog.d(this.TAG, "these 3 type do nothing");
                Toast.makeText(mContext, "some invalid type", 1).show();
                return;
            case CI_PIN_BAD_CODE:
                if (DestroyApp.isCurTaskTKUI()) {
                    Toast.makeText(mContext, mContext.getString(R.string.menu_setup_ci_pin_code_incorrect_tip), 1).show();
                    return;
                }
                return;
            case CI_PIN_BLANK_NOT_REQUIRED:
                MtkLog.d(this.TAG, "do nothing");
                CIMainDialog.setNeedShowInfoDialog(false);
                return;
            default:
                return;
        }
    }
}
