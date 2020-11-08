package com.mediatek.wwtv.tvcenter.epg;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity;
import com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class EPGPwdDialog extends Dialog {
    public static final int CHECK_PASSWORD = 4;
    private static final int MSG_CHECK_DELAY = 100;
    private static final int MSG_PWD_ERROR_DISMISS = 0;
    public static final int PASSWORD_DISMISS_DELAY = 3;
    public static final int PASSWORD_VIEW_PWD_ERROR = 2;
    public static final int PASSWORD_VIEW_PWD_INPUT = 1;
    private static final String TAG = "EPGPwdDialog";
    private View mAttachView;
    private Context mContext;
    private MtkTvChannelInfoBase mCurrentLockedChannel;
    private EPGProgramInfo mEPGProgramInfo;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private int marginX;
    private int marginY;
    private int menuHeight;
    private int menuWidth;
    private boolean misLock;
    public int mode;
    /* access modifiers changed from: private */
    public String password;
    TextWatcher passwordInputTextWatcher;
    /* access modifiers changed from: private */
    public TextView pwdError;
    private TextView pwdValue;
    private LinearLayout pwdView;
    private String showPasswordStr;
    private MtkTvAppTVBase tvAppTvBase;

    public void setCurrChannel(MtkTvChannelInfoBase currCh) {
        this.mCurrentLockedChannel = currCh;
    }

    public void setAttachView(View attachView) {
        this.mAttachView = attachView;
    }

    public void initLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (((float) (this.menuWidth * ScreenConstant.SCREEN_WIDTH)) / 1280.0f);
        lp.height = this.menuHeight;
        lp.y = this.marginY;
        window.setAttributes(lp);
        MtkLog.d(TAG, "lp.height=" + lp.height + ",lp.y=" + lp.y);
    }

    private void mesureAttachViewLocation() {
        int[] location = new int[2];
        this.mAttachView.getLocationInWindow(location);
        MtkLog.d(TAG, "x=" + location[0] + ",y=" + location[1]);
        this.menuHeight = (int) (((float) (this.menuHeight * ScreenConstant.SCREEN_HEIGHT)) / 720.0f);
        StringBuilder sb = new StringBuilder();
        sb.append("menuHeight=");
        sb.append(this.menuHeight);
        MtkLog.d(TAG, sb.toString());
        this.marginY = location[1] - ((ScreenConstant.SCREEN_HEIGHT - this.menuHeight) / 2);
        MtkLog.d(TAG, "marginY=" + this.marginY);
    }

    public void show() {
        if (this.marginY == 0) {
            mesureAttachViewLocation();
        }
        initLayoutParams();
        super.show();
    }

    public void setEPGprogram(EPGProgramInfo ePGProgramInfo) {
        this.mEPGProgramInfo = ePGProgramInfo;
    }

    public EPGPwdDialog(Context context, int theme) {
        super(context, theme);
        this.mode = 1;
        this.password = "";
        this.showPasswordStr = "";
        this.menuWidth = 390;
        this.menuHeight = 220;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int i = msg.what;
                if (i != 0) {
                    switch (i) {
                        case 3:
                            EPGPwdDialog.this.dismiss();
                            return;
                        case 4:
                            EPGPwdDialog.this.mHandler.removeMessages(4);
                            MtkLog.d(EPGPwdDialog.TAG, "checkPassWord>>>" + EPGPwdDialog.this.password);
                            EPGPwdDialog.this.checkPassWord(EPGPwdDialog.this.password);
                            return;
                        default:
                            return;
                    }
                } else {
                    EPGPwdDialog.this.mHandler.removeMessages(0);
                    if (EPGPwdDialog.this.pwdError.getVisibility() != 8) {
                        EPGPwdDialog.this.pwdError.setVisibility(8);
                    }
                }
            }
        };
        this.passwordInputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                MtkLog.d(EPGPwdDialog.TAG, "s.toString()>>>" + s.toString() + "   " + s.toString().length());
                if (s.toString().length() == 4) {
                    EPGPwdDialog.this.mHandler.sendEmptyMessageDelayed(4, 100);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        setContentView(R.layout.epg_pwd_view);
        findViews();
    }

    public EPGPwdDialog(Context context) {
        this(context, R.style.nav_dialog);
        MtkLog.d(TAG, "Constructor!!");
        this.mContext = context;
        this.password = "";
        this.showPasswordStr = "";
        this.tvAppTvBase = new MtkTvAppTVBase();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.password = "";
        this.showPasswordStr = "";
        showPasswordView(1);
        if (this.pwdError.getVisibility() != 8) {
            this.pwdError.setVisibility(8);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown>>>" + keyCode);
        int keyCode2 = event.getKeyCode();
        if (keyCode2 != 4) {
            if (keyCode2 != 82) {
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
                        inputChar(keyCode - 7);
                        return true;
                    default:
                        switch (keyCode2) {
                            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                break;
                        }
                }
            }
            return true;
        }
        dismiss();
        if (keyCode == 24 || keyCode == 25) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void findViews() {
        this.pwdError = (TextView) findViewById(R.id.epg_pwd_error);
        this.pwdView = (LinearLayout) findViewById(R.id.epg_input_pwd_view);
        this.pwdValue = (TextView) findViewById(R.id.epg_pwd_value);
        this.pwdValue.setInputType(0);
        this.pwdValue.addTextChangedListener(this.passwordInputTextWatcher);
    }

    public void dismiss() {
        this.mHandler.removeMessages(3);
        this.mHandler.removeMessages(0);
        MtkLog.d(TAG, "misLock>>>" + this.misLock);
        if (this.mContext instanceof EPGEuActivity) {
            ((EPGEuActivity) this.mContext).setIsNeedFirstShowLock(this.misLock);
        } else if (this.mContext instanceof EPGSaActivity) {
            ((EPGSaActivity) this.mContext).setIsNeedFirstShowLock(this.misLock);
        } else if (this.mContext instanceof EPGCnActivity) {
            ((EPGCnActivity) this.mContext).setIsNeedFirstShowLock(this.misLock);
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            MtkLog.d(TAG, "error:" + e.getMessage());
        }
    }

    public void checkPassWord(String pwd) {
        if (MtkTvPWDDialog.getInstance().checkPWD(pwd)) {
            this.tvAppTvBase.unlockService(CommonIntegration.getInstance().getCurrentFocus());
            if (this.mContext instanceof EPGUsActivity) {
                ((EPGUsActivity) this.mContext).setProgramBlock(false);
            }
            this.misLock = false;
            this.mHandler.sendEmptyMessageDelayed(3, 400);
            return;
        }
        showPasswordView(2);
        this.mHandler.sendEmptyMessageDelayed(0, MessageType.delayMillis5);
    }

    public void showPasswordView(int mode2) {
        this.pwdValue.setText((CharSequence) null);
        this.password = "";
        this.showPasswordStr = "";
        switch (mode2) {
            case 1:
                if (this.pwdView.getVisibility() != 0) {
                    this.pwdView.setVisibility(0);
                }
                this.mode = 1;
                return;
            case 2:
                Log.d(TAG, "password error");
                if (this.pwdView.getVisibility() != 0) {
                    this.pwdView.setVisibility(0);
                }
                if (this.pwdError.getVisibility() != 0) {
                    this.pwdError.setVisibility(0);
                }
                this.mode = 2;
                return;
            default:
                return;
        }
    }

    public void inputChar(int num) {
        MtkLog.d(TAG, "password>>>" + this.password);
        if (this.password != null && this.password.length() < 4) {
            this.mHandler.removeMessages(0);
            this.mHandler.sendEmptyMessage(0);
            sendAutoDismissMessage();
            this.password += num;
            this.showPasswordStr += "*";
            this.pwdValue.setText(this.showPasswordStr);
        }
    }

    public String getInputString() {
        if (this.password != null) {
            return this.password;
        }
        return null;
    }

    public boolean isPasswordRight(Context context, String mString) {
        if (SaveValue.getInstance(context).readStrValue("password", "1234").equals(mString)) {
            return true;
        }
        return false;
    }

    public void sendAutoDismissMessage() {
        this.misLock = true;
        this.mHandler.removeMessages(3);
        this.mHandler.sendEmptyMessageDelayed(3, MessageType.delayMillis10);
    }
}
