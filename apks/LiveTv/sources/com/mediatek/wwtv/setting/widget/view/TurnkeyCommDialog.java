package com.mediatek.wwtv.setting.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
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

public class TurnkeyCommDialog extends Dialog implements Runnable {
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

    public TurnkeyCommDialog(Context context) {
        super(context, 2131755419);
        this.buttonCount = 2;
        this.focusedButton = 0;
        this.TAG = "TurnkeyCommDialog";
        this.width = 0;
        this.height = 0;
        this.focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (v.getId()) {
                        case R.id.comm_dialog_buttonNo /*2131362026*/:
                            int unused = TurnkeyCommDialog.this.focusedButton = 2;
                            return;
                        case R.id.comm_dialog_buttonYes /*2131362027*/:
                            int unused2 = TurnkeyCommDialog.this.focusedButton = 1;
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
                        int unused = TurnkeyCommDialog.this.focusedButton = 2;
                        break;
                    case R.id.comm_dialog_buttonYes /*2131362027*/:
                        int unused2 = TurnkeyCommDialog.this.focusedButton = 1;
                        break;
                }
                TurnkeyCommDialog.this.requestButtonFocus();
                return false;
            }
        };
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    public TurnkeyCommDialog(Context context, int buttonCount2) {
        super(context, 2131755419);
        this.buttonCount = 2;
        this.focusedButton = 0;
        this.TAG = "TurnkeyCommDialog";
        this.width = 0;
        this.height = 0;
        this.focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (v.getId()) {
                        case R.id.comm_dialog_buttonNo /*2131362026*/:
                            int unused = TurnkeyCommDialog.this.focusedButton = 2;
                            return;
                        case R.id.comm_dialog_buttonYes /*2131362027*/:
                            int unused2 = TurnkeyCommDialog.this.focusedButton = 1;
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
                        int unused = TurnkeyCommDialog.this.focusedButton = 2;
                        break;
                    case R.id.comm_dialog_buttonYes /*2131362027*/:
                        int unused2 = TurnkeyCommDialog.this.focusedButton = 1;
                        break;
                }
                TurnkeyCommDialog.this.requestButtonFocus();
                return false;
            }
        };
        this.buttonCount = buttonCount2;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    public TurnkeyCommDialog(Context context, String title2, String info) {
        super(context, 2131755419);
        this.buttonCount = 2;
        this.focusedButton = 0;
        this.TAG = "TurnkeyCommDialog";
        this.width = 0;
        this.height = 0;
        this.focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (v.getId()) {
                        case R.id.comm_dialog_buttonNo /*2131362026*/:
                            int unused = TurnkeyCommDialog.this.focusedButton = 2;
                            return;
                        case R.id.comm_dialog_buttonYes /*2131362027*/:
                            int unused2 = TurnkeyCommDialog.this.focusedButton = 1;
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
                        int unused = TurnkeyCommDialog.this.focusedButton = 2;
                        break;
                    case R.id.comm_dialog_buttonYes /*2131362027*/:
                        int unused2 = TurnkeyCommDialog.this.focusedButton = 1;
                        break;
                }
                TurnkeyCommDialog.this.requestButtonFocus();
                return false;
            }
        };
        this.title = title2;
        this.message = info;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
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

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        if (this.buttonCount == 0) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 0+++++++++++++++");
            setContentView(R.layout.menu_dialog_no_button);
            this.width = (int) (((double) outSize.x) * 0.55d);
            this.lp.width = this.width;
            this.height = (int) (((double) outSize.y) * 0.2d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initNoButton();
        } else if (this.buttonCount == 3) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 3+++++++++++++++");
            setContentView(R.layout.menu_comm_long_dialog);
            this.width = (int) (((double) outSize.x) * 0.55d);
            this.lp.width = this.width;
            this.height = (int) (((double) outSize.y) * 0.2d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            init();
        } else if (this.buttonCount == 4) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 4+++++++++++++++");
            setContentView(R.layout.menu_dialog_network_gider);
            this.width = (int) (((double) outSize.x) * 0.55d);
            this.lp.width = this.width;
            this.height = (int) (((double) outSize.y) * 0.83d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initnetGider();
        } else if (this.buttonCount == 5) {
            MtkLog.d(this.TAG, "+++++++++++++Button = 5+++++++++++++++");
            setContentView(R.layout.menu_dialog_auto_adjust);
            this.width = (int) (((double) outSize.x) * 0.31d);
            this.lp.width = this.width;
            this.height = (int) (((double) outSize.y) * 0.25d);
            this.lp.height = this.height;
            this.window.setAttributes(this.lp);
            initAutoAdjust();
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
