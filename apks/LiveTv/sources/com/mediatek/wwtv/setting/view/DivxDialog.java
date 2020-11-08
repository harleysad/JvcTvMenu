package com.mediatek.wwtv.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class DivxDialog extends Dialog {
    protected static final String TAG = "DivxDialog";
    public static int flag = 0;
    final String CODE = "BX9ASIKQEI";
    final String DEREGISTER_COLON = ":";
    private Context context;
    public int height = 0;
    boolean isPositionView = false;
    private String itemId = "";
    WindowManager.LayoutParams lp;
    private LayoutInflater mInflater;
    private String mItemId;
    private LinearLayout mLayout;
    private ViewGroup mRootView;
    TVContent mtvcontent = TVContent.getInstance(this.context);
    /* access modifiers changed from: private */
    public Button vButtonCancel;
    /* access modifiers changed from: private */
    public Button vButtonOK;
    private TextView vTextView;
    public int width = 0;
    Window window;
    private int xOff;
    private int yOff;

    public DivxDialog(Context context2) {
        super(context2, 2131755419);
        this.context = context2;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_divx_info);
        this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
        this.lp.width = this.width;
        this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.35d);
        this.lp.height = this.height;
        this.lp.x = 0 - (this.lp.width / 3);
        this.window.setAttributes(this.lp);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        init();
        setDivxInfo();
    }

    public void setItemId(String itemId2) {
        this.itemId = itemId2;
    }

    public void setPositon(int xoff, int yoff) {
        Window window2 = getWindow();
        WindowManager.LayoutParams lp2 = window2.getAttributes();
        lp2.x = xoff;
        lp2.y = yoff;
        this.xOff = xoff;
        this.yOff = yoff;
        window2.setAttributes(lp2);
    }

    public void setDivxInfo() {
        if (this.itemId.equals(MenuConfigManager.DIVX_REG)) {
            this.mLayout.setVisibility(8);
            this.vButtonCancel.setVisibility(8);
            String registrationCode = this.mtvcontent.getDrmRegistrationCode();
            if (registrationCode == null) {
                registrationCode = "Invalid";
            }
            MtkLog.i(TAG, "registeration:" + registrationCode);
            this.vTextView.setText(this.context.getResources().getString(R.string.menu_setup_divxreg).replace("BX9ASIKQEI", registrationCode));
        } else {
            this.mLayout.setVisibility(0);
            this.vButtonCancel.setVisibility(0);
            long uiHelpInfo = this.mtvcontent.getDrmUiHelpInfo();
            MtkLog.i(TAG, "uihelp:" + uiHelpInfo);
            if ((2 & uiHelpInfo) == 0) {
                String deactivation = this.mtvcontent.setDrmDeactivation();
                MtkLog.i(TAG, "deactivation:" + deactivation);
                String deactiveMsg = this.context.getResources().getString(R.string.menu_setup_divxdea);
                if (deactivation != null) {
                    int pos = deactiveMsg.indexOf(":");
                    StringBuffer sbuf = new StringBuffer(deactiveMsg);
                    sbuf.insert(pos + 1, deactivation);
                    deactiveMsg = sbuf.toString();
                }
                this.vTextView.setText(deactiveMsg);
            } else {
                this.vTextView.setText("Your device is already registered.\nAre you sure you wish\nto deregister?");
                flag = 1;
            }
        }
        this.vButtonOK.requestFocus();
    }

    public void init() {
        this.vTextView = (TextView) findViewById(R.id.common_divx_name);
        this.vButtonOK = (Button) findViewById(R.id.common_divx_btn_ok);
        this.vButtonOK.requestFocus();
        this.vButtonCancel = (Button) findViewById(R.id.common_divx_btn_cancel);
        this.mLayout = (LinearLayout) findViewById(R.id.common_divx_ll_cancel);
        this.vButtonOK.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return true;
                }
                if (keyCode != 4) {
                    if (keyCode != 66) {
                        if (keyCode == 183) {
                            return DivxDialog.this.onEnter();
                        }
                        switch (keyCode) {
                            case 21:
                                MtkLog.d("MenuMain", "KEYCODE_DPAD_LEFT");
                                if (!DivxDialog.this.vButtonOK.hasFocus() || DivxDialog.this.vButtonCancel.getVisibility() != 0) {
                                    MtkLog.d("MenuMain", "KEYCODE_DPAD_LEFT vButtonOK requestFocus");
                                    DivxDialog.this.vButtonOK.requestFocus();
                                } else {
                                    DivxDialog.this.vButtonCancel.requestFocus();
                                }
                                return true;
                            case 22:
                                MtkLog.d("MenuMain", "KEYCODE_DPAD_RIGHT");
                                if (!DivxDialog.this.vButtonOK.hasFocus() || DivxDialog.this.vButtonCancel.getVisibility() != 0) {
                                    MtkLog.d("MenuMain", "KEYCODE_DPAD_RIGHT vButtonOK requestFocus");
                                    DivxDialog.this.vButtonOK.requestFocus();
                                } else {
                                    DivxDialog.this.vButtonCancel.requestFocus();
                                }
                                return true;
                            case 23:
                                break;
                            default:
                                return false;
                        }
                    }
                    return DivxDialog.this.onEnter();
                }
                DivxDialog.this.cancel();
                return true;
            }
        });
        this.vButtonCancel.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return true;
                }
                if (keyCode != 4) {
                    if (keyCode != 66) {
                        if (keyCode != 183) {
                            switch (keyCode) {
                                case 21:
                                    if (DivxDialog.this.vButtonCancel.hasFocus()) {
                                        DivxDialog.this.vButtonOK.requestFocus();
                                    }
                                    return true;
                                case 22:
                                    if (DivxDialog.this.vButtonCancel.hasFocus()) {
                                        DivxDialog.this.vButtonOK.requestFocus();
                                    }
                                    return true;
                                case 23:
                                    break;
                                default:
                                    return false;
                            }
                        } else {
                            DivxDialog.this.cancel();
                            return false;
                        }
                    }
                    MtkLog.d("MenuMain", "onKey");
                    DivxDialog.this.cancel();
                    return false;
                }
                DivxDialog.this.cancel();
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean onEnter() {
        MtkLog.d("MenuMain", "onKey");
        if (this.mLayout.getVisibility() == 0) {
            MtkLog.d("MenuMain", "vButtonCancel.getVisibility() == View.VISIBLE");
            this.mLayout.setVisibility(8);
            this.vButtonCancel.setVisibility(8);
            if (flag == 0) {
                String registrationCode = this.mtvcontent.getDrmRegistrationCode();
                if (registrationCode == null) {
                    registrationCode = "Invalid";
                }
                MtkLog.i(TAG, "registeration:" + registrationCode);
                this.vTextView.setText(this.context.getResources().getString(R.string.menu_setup_divxreg).replace("BX9ASIKQEI", registrationCode));
                return true;
            }
            this.mLayout.setVisibility(0);
            this.vButtonCancel.setVisibility(0);
            MtkLog.i(TAG, "vButtonCancel:visible?");
            String deactivation = this.mtvcontent.setDrmDeactivation();
            MtkLog.i(TAG, "deactivation:" + deactivation);
            String drmMsg = this.context.getResources().getString(R.string.menu_setup_divxdea);
            if (deactivation != null) {
                int pos = drmMsg.indexOf(":");
                StringBuffer sbuf = new StringBuffer(drmMsg);
                sbuf.insert(pos + 1, deactivation);
                drmMsg = sbuf.toString();
            }
            this.vTextView.setText(drmMsg);
            flag = 0;
            return true;
        }
        cancel();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
