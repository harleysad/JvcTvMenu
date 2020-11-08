package com.mediatek.wwtv.tvcenter.commonview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class DiagnosticDialog extends Dialog implements View.OnKeyListener {
    private Button cancel_bt;
    public int height = 0;
    private WindowManager.LayoutParams lp;
    private Context mContext;
    private TextView mToastMessage;
    public int width = 0;
    private Window window;
    private int xOff;
    private int yOff;

    public DiagnosticDialog(Context context) {
        super(context, 2131755419);
        this.mContext = context;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic_screen_dialog);
        this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.45d);
        this.lp.width = this.width;
        this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.25d);
        this.lp.height = this.height;
        this.window.setAttributes(this.lp);
        init();
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

    public void init() {
        this.mToastMessage = (TextView) findViewById(R.id.diagnostic_screen_toast_message);
        this.mToastMessage.setText("Some thing goes wrong,please visit the website!");
        this.cancel_bt = (Button) findViewById(R.id.diagnostic_screen_toast_message_btn_cancel);
        this.cancel_bt.setOnKeyListener(this);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != 0) {
            return true;
        }
        if (keyCode != 4) {
            if (keyCode != 66) {
                switch (keyCode) {
                    case 21:
                        return true;
                    case 22:
                        break;
                    case 23:
                        break;
                    default:
                        return false;
                }
            }
            cancel();
            return true;
        }
        cancel();
        return true;
    }
}
