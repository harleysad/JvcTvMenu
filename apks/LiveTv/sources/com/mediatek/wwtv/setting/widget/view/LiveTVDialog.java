package com.mediatek.wwtv.setting.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.Loading;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class LiveTVDialog extends Dialog implements Runnable {
    private String TAG;
    private int buttonCount;
    private Button buttonNo;
    private String buttonNoName;
    private String buttonOKName;
    private Button buttonYes;
    private String buttonYesName;
    View.OnFocusChangeListener focusChangeListener;
    /* access modifiers changed from: private */
    public int focusedButton;
    public int height;
    View.OnKeyListener keyListener;
    private Loading loading;
    WindowManager.LayoutParams lp;
    private String message;
    View.OnTouchListener onTouchListener;
    private TextView textView;
    private String title;
    private TextView titleView;
    private TextView waitView;
    public int width;
    Window window;
    private int xOff;
    private int yOff;

    public LiveTVDialog(Context context) {
        super(context, 2131755419);
        this.TAG = "LiveTVDialog";
        this.buttonCount = 2;
        this.focusedButton = 0;
        this.width = 0;
        this.height = 0;
        this.focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (v.getId()) {
                        case R.id.comm_dialog_buttonNo /*2131362026*/:
                            int unused = LiveTVDialog.this.focusedButton = 2;
                            return;
                        case R.id.comm_dialog_buttonYes /*2131362027*/:
                            int unused2 = LiveTVDialog.this.focusedButton = 1;
                            return;
                        default:
                            return;
                    }
                }
            }
        };
        this.onTouchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.comm_dialog_buttonNo /*2131362026*/:
                        int unused = LiveTVDialog.this.focusedButton = 2;
                        break;
                    case R.id.comm_dialog_buttonYes /*2131362027*/:
                        int unused2 = LiveTVDialog.this.focusedButton = 1;
                        break;
                }
                LiveTVDialog.this.requestButtonFocus();
                return false;
            }
        };
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    public LiveTVDialog(Context context, int buttonCount2) {
        super(context, 2131755419);
        this.TAG = "LiveTVDialog";
        this.buttonCount = 2;
        this.focusedButton = 0;
        this.width = 0;
        this.height = 0;
        this.focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (v.getId()) {
                        case R.id.comm_dialog_buttonNo /*2131362026*/:
                            int unused = LiveTVDialog.this.focusedButton = 2;
                            return;
                        case R.id.comm_dialog_buttonYes /*2131362027*/:
                            int unused2 = LiveTVDialog.this.focusedButton = 1;
                            return;
                        default:
                            return;
                    }
                }
            }
        };
        this.onTouchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.comm_dialog_buttonNo /*2131362026*/:
                        int unused = LiveTVDialog.this.focusedButton = 2;
                        break;
                    case R.id.comm_dialog_buttonYes /*2131362027*/:
                        int unused2 = LiveTVDialog.this.focusedButton = 1;
                        break;
                }
                LiveTVDialog.this.requestButtonFocus();
                return false;
            }
        };
        this.buttonCount = buttonCount2;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    public LiveTVDialog(Context context, String title2, String info, int buttonCount2) {
        super(context, 2131755419);
        this.TAG = "LiveTVDialog";
        this.buttonCount = 2;
        this.focusedButton = 0;
        this.width = 0;
        this.height = 0;
        this.focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (v.getId()) {
                        case R.id.comm_dialog_buttonNo /*2131362026*/:
                            int unused = LiveTVDialog.this.focusedButton = 2;
                            return;
                        case R.id.comm_dialog_buttonYes /*2131362027*/:
                            int unused2 = LiveTVDialog.this.focusedButton = 1;
                            return;
                        default:
                            return;
                    }
                }
            }
        };
        this.onTouchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.comm_dialog_buttonNo /*2131362026*/:
                        int unused = LiveTVDialog.this.focusedButton = 2;
                        break;
                    case R.id.comm_dialog_buttonYes /*2131362027*/:
                        int unused2 = LiveTVDialog.this.focusedButton = 1;
                        break;
                }
                LiveTVDialog.this.requestButtonFocus();
                return false;
            }
        };
        String str = this.TAG;
        MtkLog.d(str, "Coustrutor, " + buttonCount2);
        this.buttonCount = buttonCount2;
        this.title = title2;
        this.message = info;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    public void bindKeyListener(View.OnKeyListener keyListener2) {
        this.keyListener = keyListener2;
    }

    public void requestButtonFocus() {
        if ((this.buttonCount != 2 && this.buttonCount != 3) || this.focusedButton == 0) {
            return;
        }
        if (this.focusedButton == 1) {
            this.buttonYes.requestFocusFromTouch();
        } else if (this.focusedButton == 2) {
            this.buttonNo.requestFocusFromTouch();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        requestButtonFocus();
        return super.onTouchEvent(event);
    }

    public void onCreate(Bundle savedInstanceState) {
        String str = this.TAG;
        MtkLog.d(str, "onCreate, " + this.buttonCount);
        super.onCreate(savedInstanceState);
        if (this.buttonCount == 0) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 0+++++++++++++++");
            setContentView(R.layout.menu_dialog_no_button);
            this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
            this.lp.width = this.width;
            this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.2d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initNoButton();
        } else if (this.buttonCount == 3) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 3+++++++++++++++");
            setContentView(R.layout.menu_comm_long_dialog);
            this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
            this.lp.width = this.width;
            this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.2d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            init();
        } else if (this.buttonCount == 4) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 4+++++++++++++++");
            setContentView(R.layout.menu_dialog_network_gider);
            this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
            this.lp.width = this.width;
            this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.83d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initnetGider();
        } else if (this.buttonCount == 5) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 5+++++++++++++++");
            setContentView(R.layout.menu_dialog_auto_adjust);
            this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.31d);
            this.lp.width = this.width;
            this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.25d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initAutoAdjust();
        } else if (this.buttonCount == 6) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 6+++++++++++++++");
            setContentView(R.layout.menu_dialog_short_message);
            this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.31d);
            this.lp.width = this.width;
            this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.27d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initShortMessage();
        } else if (this.buttonCount == 7) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 6+++++++++++++++");
            setContentView(R.layout.menu_comm_one_button_dialog);
            this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.31d);
            this.lp.width = this.width;
            this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.15d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initOnebuttonMessage();
        }
    }

    private void init() {
        this.titleView = (TextView) findViewById(R.id.comm_dialog_title);
        this.textView = (TextView) findViewById(R.id.comm_dialog_text);
        this.buttonYes = (Button) findViewById(R.id.comm_dialog_buttonYes);
        this.buttonYes.setText(this.buttonYesName);
        this.buttonYes.setFocusable(true);
        this.buttonYes.requestFocus();
        this.buttonYes.setOnFocusChangeListener(this.focusChangeListener);
        this.buttonYes.setOnTouchListener(this.onTouchListener);
        this.buttonNo = (Button) findViewById(R.id.comm_dialog_buttonNo);
        this.buttonNo.setText(this.buttonNoName);
        this.buttonNo.setOnFocusChangeListener(this.focusChangeListener);
        this.buttonNo.setOnTouchListener(this.onTouchListener);
        this.titleView.setText(this.title);
        this.textView.setText(this.message);
        this.buttonYes.setOnKeyListener(this.keyListener);
        this.buttonNo.setOnKeyListener(this.keyListener);
    }

    private void initOnebuttonMessage() {
        this.textView = (TextView) findViewById(R.id.comm_dialog_text);
        this.textView.setText(this.message);
        this.buttonNo = (Button) findViewById(R.id.comm_dialog_buttonNo);
        this.buttonNo.setText(this.buttonNoName);
        this.buttonNo.setOnFocusChangeListener(this.focusChangeListener);
        this.buttonNo.setOnTouchListener(this.onTouchListener);
        this.buttonNo.setOnKeyListener(this.keyListener);
    }

    private void initNoButton() {
        this.titleView = (TextView) findViewById(R.id.comm_dialog_title);
        this.textView = (TextView) findViewById(R.id.comm_dialog_text);
        this.waitView = (TextView) findViewById(R.id.comm_dialog_wait);
        this.loading = (Loading) findViewById(R.id.comm_dialog_loading);
        this.titleView.setText(this.title);
        this.textView.setText(this.message);
    }

    private void initnetGider() {
        this.titleView = (TextView) findViewById(R.id.comm_dialog_title);
        this.textView = (TextView) findViewById(R.id.comm_dialog_text);
        this.titleView.setText(this.title);
        this.textView.setText(this.message);
    }

    private void initAutoAdjust() {
        this.loading = (Loading) findViewById(R.id.menu_dialog_auto_adjust_loading);
        this.textView = (TextView) findViewById(R.id.menu_dialog_auto_adjust_text);
        this.textView.setText(this.message);
        this.loading.drawLoading();
    }

    private void initShortMessage() {
        this.textView = (TextView) findViewById(R.id.menu_dialog_auto_adjust_text);
        this.textView.setText(this.message);
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

    public Button getButtonYes() {
        return this.buttonYes;
    }

    public Button getButtonNo() {
        return this.buttonNo;
    }

    public void setButtonYesName(String buttonYesName2) {
        this.buttonYesName = buttonYesName2;
    }

    public void setButtonNoName(String buttonNoName2) {
        this.buttonNoName = buttonNoName2;
    }

    public void setMessage(String info) {
        this.message = info;
    }

    public void setText() {
        this.textView.setText(this.message);
    }

    public void run() {
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getWaitView() {
        return this.waitView;
    }

    public Loading getLoading() {
        return this.loading;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 24:
            case 25:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
}
