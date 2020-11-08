package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.tv.dialog.PinDialogFragment;
import com.android.tv.menu.MenuOptionMain;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.UImanager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.List;

public class PwdDialog extends NavBasicDialog implements ComponentStatusListener.ICStatusListener {
    public static final String AUTO_TEST_PROPERTY = "vendor.mtk.auto_test";
    private static final int INPUT_ERROR_TIMES = 3;
    public static final int MSG_CHECK = 0;
    private static final int MSG_CHECK_DELAY = 500;
    public static final int PASSWORD_ERROR_DELAY = 5;
    public static final int PASSWORD_ERROR_DELAY_TIME = 900000;
    public static final int PASSWORD_VIEW_DISMISS_PWD_INPUT = 1;
    public static final int PASSWORD_VIEW_HIDE = 4;
    public static final int PASSWORD_VIEW_HINT_DVBS = 2;
    public static final int PASSWORD_VIEW_PWD_ERROR = 3;
    public static final int PASSWORD_VIEW_SHOW_PWD_INPUT = 0;
    public static final int PASSWORD_WAITE = 10;
    private static final String PWD_CHAR = "*";
    private static final String TAG = "PwdDialog";
    public static final int UPDATE_MESSAGE = 6;
    private static TvContentRating mRating;
    private final ComponentsManager comManager;
    /* access modifiers changed from: private */
    public boolean mCheckedPwd;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public int mInputPwdErrorTimes;
    private PinDialogFragment mPinDialogFragment;
    private int mode;
    private final MtkTvPWDDialog mtkTvPwd;
    /* access modifiers changed from: private */
    public String password;
    TextWatcher passwordInputTextWatcher;
    TextWatcher passwordInputTextWatcher3;
    private String password_in;
    TextView pwdError;
    TextView pwdValue;
    TextView pwdValue1;
    TextView pwdValue2;
    TextView pwdValue3;
    LinearLayout pwdView;
    private TextView pwd_name;
    private String showPasswordStr;
    private final MtkTvAppTVBase tvAppTvBase;
    /* access modifiers changed from: private */
    public int update_flag;
    /* access modifiers changed from: private */
    public int update_flag1;

    public PwdDialog(Context context, int theme) {
        super(context, theme);
        this.mode = 0;
        this.password = "";
        this.password_in = "";
        this.showPasswordStr = "";
        this.update_flag = 1;
        this.update_flag1 = 1;
        this.passwordInputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        this.passwordInputTextWatcher3 = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    PwdDialog.this.mHandler.sendEmptyMessageDelayed(0, 500);
                }
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i != 0) {
                    switch (i) {
                        case 5:
                            int unused = PwdDialog.this.mInputPwdErrorTimes = 0;
                            return;
                        case 6:
                            int unused2 = PwdDialog.this.update_flag1 = PwdDialog.this.update_flag;
                            MtkLog.d(PwdDialog.TAG, "update_flag1 = " + PwdDialog.this.update_flag1);
                            if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                                return;
                            }
                            if ((StateDvr.getInstance() == null || !StateDvr.getInstance().isBigCtrlBarShow()) && PwdDialog.this.mCheckedPwd) {
                                PwdDialog.this.updateState();
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else {
                    PwdDialog.this.checkPassWord(PwdDialog.this.password);
                }
            }
        };
        this.componentID = NavBasic.NAV_COMP_ID_PWD_DLG;
        this.mtkTvPwd = MtkTvPWDDialog.getInstance();
        this.comManager = ComponentsManager.getInstance();
        this.tvAppTvBase = new MtkTvAppTVBase();
        ComponentStatusListener.getInstance().addListener(5, this);
        ComponentStatusListener.getInstance().addListener(2, this);
        ComponentStatusListener.getInstance().addListener(12, this);
        ComponentStatusListener.getInstance().addListener(13, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        this.mInputPwdErrorTimes = 0;
        init();
    }

    private void init() {
        this.mPinDialogFragment = PinDialogFragment.create(6);
        this.mPinDialogFragment.setOnPinCheckCallback(new PinDialogFragment.OnPinCheckCallback() {
            public boolean onCheckPIN(String pin) {
                return PwdDialog.this.checkPWD(pin);
            }

            public void startTimeout() {
                PwdDialog.this.startTimeout(10000);
            }

            public void stopTimeout() {
                PwdDialog.this.stopTimeout();
            }

            public void pinExit() {
                PwdDialog.this.notifyNavHide();
            }

            public void onKey(int keyCode, KeyEvent event) {
                MtkLog.d(PwdDialog.TAG, "onKey:" + keyCode + ",event:" + event);
                TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode, event, false);
            }
        });
    }

    public PwdDialog(Context context) {
        this(context, R.style.nav_dialog);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_input_pwd_view);
        findViews();
        MtkLog.d(TAG, "onCreate mode = " + this.mode);
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777218 || componentID == 16777235) {
            return true;
        }
        return false;
    }

    public boolean isKeyHandler(int keyCode) {
        if (keyCode != 23 || ((FavoriteListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST)).isShowing()) {
            return false;
        }
        int showFlag = this.mtkTvPwd.PWDShow();
        if (isContentBlock()) {
            showFlag = 0;
        }
        if (this.mInputPwdErrorTimes < 3) {
            MtkLog.d(TAG, "isKeyHandler showFlag = " + showFlag);
            if (showFlag != 0) {
                switch (showFlag) {
                    case 2:
                    case 3:
                        break;
                    default:
                        return false;
                }
            }
            this.mode = showFlag;
            return true;
        }
        if (showFlag != 0) {
            switch (showFlag) {
                case 2:
                case 3:
                    break;
                default:
                    return false;
            }
        }
        this.mode = showFlag;
        showPasswordView(10);
        return true;
    }

    public void show() {
        boolean is3rdTVSource = CommonIntegration.getInstance().is3rdTVSource();
        if (!DestroyApp.isCurActivityTkuiMainActivity()) {
            return;
        }
        if (!is3rdTVSource || isContentBlock(is3rdTVSource)) {
            showPasswordView(this.mode);
        }
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        return KeyHandler(keyCode, event);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        boolean isHandle = true;
        int keyCode2 = KeyMap.getKeyCode(keyCode, event);
        if (keyCode2 == 129 && CommonIntegration.getInstance().isCurrentSourceBlocked()) {
            return true;
        }
        MtkLog.d(TAG, "KeyHandler keyCode = " + keyCode2 + "mode = " + this.mode);
        if (this.mInputPwdErrorTimes >= 3) {
            isHandle = false;
        } else if (keyCode2 == 4) {
            dismiss();
            return true;
        } else if (keyCode2 != 23) {
            if (keyCode2 != 93) {
                switch (keyCode2) {
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
                        startTimeout(10000);
                        if (this.password.length() < 4 && this.pwdView.getVisibility() == 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(this.password);
                            sb.append(keyCode2 - 7);
                            this.password = sb.toString();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("");
                            sb2.append(keyCode2 - 7);
                            this.password_in = sb2.toString();
                            if (this.password.length() == 1) {
                                this.pwdValue.setText(PWD_CHAR);
                            }
                            if (this.password.length() == 2) {
                                this.pwdValue1.setText(PWD_CHAR);
                            }
                            if (this.password.length() == 3) {
                                this.pwdValue2.setText(PWD_CHAR);
                            }
                            if (this.password.length() == 4) {
                                this.pwdValue3.setText(PWD_CHAR);
                                break;
                            }
                        }
                        break;
                    default:
                        switch (keyCode2) {
                            case 19:
                            case 20:
                                return false;
                            default:
                                isHandle = false;
                                break;
                        }
                }
            } else {
                BannerView banneView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                MtkLog.d(TAG, "banneView   pwd  " + banneView);
                if (banneView != null) {
                    banneView.changeFavChannel();
                }
            }
        } else if (this.mode == 3) {
            showPasswordView(0);
        } else if (this.mode == 0) {
            String inputStr = getInputString();
            if (inputStr == null || inputStr.length() == 0) {
                dismiss();
            } else {
                this.mHandler.removeMessages(0);
                checkPassWord(inputStr);
            }
        }
        MtkLog.d(TAG, "isHandle>>>>" + isHandle + "   mContext>" + this.mContext);
        if (isHandle || this.mContext == null || !(this.mContext instanceof TurnkeyUiMainActivity)) {
            return isHandle;
        }
        MtkLog.d(TAG, "enter TurnkeyUiMainActivity mContext");
        return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode2, event);
    }

    private void findViews() {
        this.pwdError = (TextView) findViewById(R.id.nav_tv_pwd_error);
        this.pwd_name = (TextView) findViewById(R.id.nav_tv_pwd_name);
        this.pwdView = (LinearLayout) findViewById(R.id.nav_tv_pwd_view);
        this.pwdValue = (TextView) findViewById(R.id.nav_tv_pwd_value);
        this.pwdValue1 = (TextView) findViewById(R.id.nav_tv_pwd_value1);
        this.pwdValue2 = (TextView) findViewById(R.id.nav_tv_pwd_value2);
        this.pwdValue3 = (TextView) findViewById(R.id.nav_tv_pwd_value3);
        this.pwdValue3.setInputType(0);
        this.pwdValue3.addTextChangedListener(this.passwordInputTextWatcher3);
        this.pwdValue.setInputType(0);
        this.pwdValue.addTextChangedListener(this.passwordInputTextWatcher);
    }

    public void setWindowPosition() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int menuWidth = (823 * ScreenConstant.SCREEN_WIDTH) / 1280;
        int menuHeight = (420 * ScreenConstant.SCREEN_HEIGHT) / 720;
        lp.width = menuWidth;
        lp.height = menuHeight;
        lp.x = ((ScreenConstant.SCREEN_WIDTH / 2) - (menuWidth / 2)) - ((ScreenConstant.SCREEN_WIDTH * 240) / 1280);
        lp.y = ((ScreenConstant.SCREEN_HEIGHT / 2) - ((4 * ScreenConstant.SCREEN_HEIGHT) / 720)) - (menuHeight / 2);
        window.setAttributes(lp);
    }

    public void dismiss() {
        if (isShowing()) {
            this.mPinDialogFragment.dismiss();
            CIMainDialog ciMainDialog = (CIMainDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG);
            if (ciMainDialog != null && ciMainDialog.getHandler() != null) {
                MtkLog.d(TAG, "dismiss ,sendEmptyMessage(0xF6)");
                ciMainDialog.getHandler().sendEmptyMessage(246);
            }
        }
    }

    public void handleCallBack(int type) {
        if (this.mInputPwdErrorTimes < 3) {
            this.mode = type;
            if (this.mode == 0) {
                this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
            } else if (this.mode == 1) {
                dismiss();
            }
        }
    }

    public void checkPassWord(String pwd) {
        this.mCheckedPwd = true;
        boolean isPass = this.mtkTvPwd.checkPWD(pwd);
        MtkLog.d(TAG, "checkPassWord isPass = " + isPass + "pwd =" + pwd);
        if (isPass) {
            if (CommonIntegration.getInstance().is3rdTVSource()) {
                TurnkeyUiMainActivity.getInstance().getTvView().unblockContent(mRating);
                mRating = null;
            }
            this.tvAppTvBase.unlockService(CommonIntegration.getInstance().getCurrentFocus());
            dismiss();
            this.mInputPwdErrorTimes = 0;
            return;
        }
        this.mInputPwdErrorTimes++;
        if (this.mInputPwdErrorTimes < 3) {
            showPasswordView(3);
            Toast.makeText(this.mContext, R.string.nav_parent_wrong_psw, 0).show();
            return;
        }
        showPasswordView(10);
        this.mHandler.sendEmptyMessageDelayed(5, 900000);
    }

    public boolean checkPWD(String pwd) {
        this.mCheckedPwd = true;
        boolean isPass = this.mtkTvPwd.checkPWD(pwd);
        MtkLog.d(TAG, "checkPWD isPass = " + isPass + "pwd =" + pwd);
        if (isPass) {
            if (CommonIntegration.getInstance().is3rdTVSource()) {
                TurnkeyUiMainActivity.getInstance().getTvView().unblockContent(mRating);
                mRating = null;
            }
            this.tvAppTvBase.unlockService(CommonIntegration.getInstance().getCurrentFocus());
        }
        return isPass;
    }

    public void showPasswordView(int mode2) {
        this.mCheckedPwd = false;
        super.setTTSEnabled(Util.isTTSEnabled(this.mContext));
        if (isShowing()) {
            MtkLog.d(TAG, "PWDDialog is showing!");
            return;
        }
        this.mPinDialogFragment.setShowing(true);
        this.mPinDialogFragment.show(TurnkeyUiMainActivity.getInstance().getFragmentManager(), "PinDialogFragment");
        startTimeout(10000);
    }

    public void showPasswordViewOld(int mode2) {
        this.mCheckedPwd = false;
        MtkLog.d(TAG, "showPasswordView mode = " + mode2);
        MtkLog.d(TAG, "ENTER2 = " + this.mContext.getResources().getText(R.string.nav_parent_psw));
        this.pwd_name.setText(this.mContext.getResources().getText(R.string.nav_parent_psw));
        this.pwdValue.setText((CharSequence) null);
        this.pwdValue1.setText((CharSequence) null);
        this.pwdValue2.setText((CharSequence) null);
        this.pwdValue3.setText((CharSequence) null);
        this.password = "";
        this.showPasswordStr = "";
        this.mode = mode2;
        if (mode2 != 10) {
            switch (mode2) {
                case 0:
                    this.pwdView.setVisibility(0);
                    this.pwdError.setVisibility(8);
                    startTimeout(10000);
                    if (SystemProperties.getInt(AUTO_TEST_PROPERTY, 0) != 0) {
                        Log.d(TAG, "auto_test for pwd-dialog show");
                        return;
                    }
                    return;
                case 1:
                case 4:
                    dismiss();
                    return;
                case 2:
                    break;
                case 3:
                    this.pwdValue.setText((CharSequence) null);
                    this.pwdValue1.setText((CharSequence) null);
                    this.pwdValue2.setText((CharSequence) null);
                    this.pwdValue3.setText((CharSequence) null);
                    this.password = "";
                    this.showPasswordStr = "";
                    startTimeout(10000);
                    return;
                default:
                    return;
            }
        }
        this.pwdError.setText(R.string.nav_parent_dvbs_hint);
        this.pwdError.setVisibility(0);
        this.pwdView.setVisibility(8);
        startTimeout(5000);
    }

    public String getInputString() {
        if (this.password != null) {
            return this.password;
        }
        return null;
    }

    public void updateComponentStatus(int statusID, int value) {
        ChannelListDialog mChListDialog;
        MtkLog.d(TAG, "updateComponentStatus statusID =" + statusID + "   " + CommonIntegration.getInstance().isPipOrPopState());
        if (this.mInputPwdErrorTimes >= 3) {
            int showFlag = this.mtkTvPwd.PWDShow();
            if (showFlag == 0 && (mChListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST)) != null && mChListDialog.isVisible()) {
                mChListDialog.dismiss();
            }
            if (statusID == 10) {
                MtkLog.d(TAG, "PWD_NAV_CHANNEL_CHANGED");
                MtkLog.d(TAG, "change_channel showFlag = " + showFlag);
                MtkLog.d(TAG, "this.isShowing() = " + isShowing());
                if (showFlag != 0) {
                    switch (showFlag) {
                        case 2:
                        case 3:
                            break;
                        default:
                            if (isShowing()) {
                                dismiss();
                                return;
                            }
                            return;
                    }
                }
                this.mode = showFlag;
                if (isShowing()) {
                    showPasswordView(this.mode);
                } else {
                    this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
                }
            }
        } else if (statusID == 2) {
            List<Integer> cpmsIDs = ComponentsManager.getInstance().getCurrentActiveComps();
            boolean coexitsComp = false;
            if (!cpmsIDs.contains(Integer.valueOf(NavBasic.NAV_COMP_ID_FAV_LIST)) && !cpmsIDs.contains(Integer.valueOf(NavBasic.NAV_COMP_ID_MISC)) && !cpmsIDs.contains(Integer.valueOf(NavBasic.NAV_COMP_ID_SUNDRY)) && !UImanager.showing && ((StateDvrFileList.getInstance() == null || !StateDvrFileList.getInstance().isShowing()) && cpmsIDs.contains(Integer.valueOf(NavBasic.NAV_COMP_ID_BANNER)) && !((SourceListView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC)).isShowing() && !((MenuOptionMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG)).isShowing())) {
                if (CommonIntegration.getInstance().isCurrentSourceBlocked()) {
                    coexitsComp = ((BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER)).PWD_SHOW_FLAG != 1;
                } else {
                    BannerView banner = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                    if (banner.getSimpleBanner().getChannelNameVisibility() != 0) {
                        isPutyDigitalStr(banner.getSimpleBanner().getFirstLineStr());
                    }
                }
            }
            if (isContentBlock() && !((SourceListView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC)).isShowing()) {
                coexitsComp = true;
            }
            if (coexitsComp) {
                if (isContentBlock()) {
                    this.update_flag = 0;
                } else {
                    this.update_flag = this.mtkTvPwd.PWDShow();
                    MtkLog.d(TAG, "updateComponentStatus mtkTvPwd.PWDShow()=" + this.mtkTvPwd.PWDShow());
                }
                MtkLog.d(TAG, "updateComponentStatus update_flag=" + this.update_flag);
                if (CommonIntegration.getInstance().isPipOrPopState()) {
                    int showFlag2 = this.mtkTvPwd.PWDShow();
                    MtkLog.d(TAG, "updateComponentStatus showFlag = " + showFlag2);
                    if (showFlag2 != 0) {
                        switch (showFlag2) {
                            case 2:
                            case 3:
                                break;
                            default:
                                if (isShowing()) {
                                    dismiss();
                                    break;
                                }
                                break;
                        }
                    }
                    this.mode = showFlag2;
                    if (isShowing()) {
                        showPasswordView(this.mode);
                    } else {
                        this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
                    }
                } else {
                    this.mHandler.removeMessages(6);
                    this.mHandler.sendEmptyMessageDelayed(6, 400);
                }
            }
            MtkLog.e(TAG, "7777770000");
        } else if (statusID != 5) {
            if (statusID != 10) {
                switch (statusID) {
                    case 12:
                        boolean is3rd = CommonIntegration.getInstance().is3rdTVSource();
                        if (this.mContext == null || !is3rd || TurnkeyUiMainActivity.getInstance().getTvView().isContentBlock(is3rd)) {
                            int pwdFlag = this.mtkTvPwd.PWDShow();
                            MtkLog.d(TAG, "Pwd content allowed, the current PWDShow:" + pwdFlag);
                            if (pwdFlag != 0) {
                                dismiss();
                                return;
                            }
                            return;
                        }
                        TurnkeyUiMainActivity.getInstance().getTvView().unblockContent(mRating);
                        return;
                    case 13:
                        MtkLog.d(TAG, "Pwd content blocked");
                        if (this.mContext != null && CommonIntegration.getInstance().is3rdTVSource()) {
                            TurnkeyUiMainActivity.getInstance().getTvView().blockContent();
                            if (DestroyApp.isCurActivityTkuiMainActivity()) {
                                this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
                                return;
                            }
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        } else if (CommonIntegration.getInstance().isPipOrPopState()) {
            switch (value) {
                case 21:
                case 22:
                    int showFlag3 = this.mtkTvPwd.PWDShow();
                    MtkLog.d(TAG, "NAV_KEY_OCCUR showFlag = " + showFlag3);
                    switch (showFlag3) {
                        case 0:
                        case 2:
                        case 3:
                            this.mode = showFlag3;
                            if (isShowing()) {
                                showPasswordView(this.mode);
                                return;
                            } else {
                                this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
                                return;
                            }
                        case 1:
                            if (isShowing()) {
                                dismiss();
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                default:
                    return;
            }
        }
    }

    public boolean isShowing() {
        return this.mPinDialogFragment != null && this.mPinDialogFragment.isShowing();
    }

    /* access modifiers changed from: private */
    public void updateState() {
        int showFlag = this.update_flag1;
        MtkLog.d(TAG, "updateState showFlag = " + showFlag);
        if (showFlag != 0) {
            switch (showFlag) {
                case 2:
                case 3:
                    break;
                default:
                    if (isShowing()) {
                        dismiss();
                        return;
                    }
                    return;
            }
        }
        this.mode = showFlag;
        boolean isMenuShow = ((MenuOptionMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG)).isShowing();
        if (isShowing()) {
            showPasswordView(this.mode);
        } else if (!isMenuShow) {
            this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
        }
    }

    public void updatePwd() {
        if (CommonIntegration.getInstance().isCurrentSourceBlocked()) {
            int showFlag = this.mtkTvPwd.PWDShow();
            MtkLog.d(TAG, "updateComponentStatus showFlag = " + showFlag);
            if (showFlag != 0) {
                switch (showFlag) {
                    case 2:
                    case 3:
                        break;
                    default:
                        if (isShowing()) {
                            dismiss();
                            return;
                        }
                        return;
                }
            }
            this.mode = showFlag;
            if (isShowing()) {
                showPasswordView(this.mode);
            } else {
                this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
            }
        }
    }

    private boolean isPutyDigitalStr(String str) {
        if (str == null) {
            return false;
        }
        return str.matches("[0-9]+");
    }

    public boolean isContentBlock() {
        return isContentBlock(CommonIntegration.getInstance().is3rdTVSource());
    }

    public boolean isContentBlock(boolean is3rdChannel) {
        return (is3rdChannel && mRating != null) || TurnkeyUiMainActivity.getInstance().getTvView().isContentBlock(is3rdChannel);
    }

    public static void setContentBlockRating(TvContentRating rating) {
        mRating = rating;
    }
}
