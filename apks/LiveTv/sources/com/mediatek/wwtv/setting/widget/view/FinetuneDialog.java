package com.mediatek.wwtv.setting.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class FinetuneDialog extends Dialog {
    static final String TAG = "FinetuneDialog";
    private TextView adjustText;
    public int count = 2;
    public int menuHeight = 0;
    public int menuWidth = 0;
    private TextView nameText;
    private TextView numText;
    private int xOff;
    private int yOff;

    public FinetuneDialog(Context context) {
        super(context, 2131755419);
    }

    public FinetuneDialog(Context context, int buttonCount) {
        super(context, 2131755419);
    }

    public FinetuneDialog(Context context, String title, String info) {
        super(context, 2131755419);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_comm_finetune);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        this.menuWidth = SettingsUtil.SCREEN_WIDTH / 3;
        this.menuHeight = SettingsUtil.SCREEN_HEIGHT / 3;
        MtkLog.d(TAG, "SettingsUtil.SCREEN_WIDTH: " + SettingsUtil.SCREEN_WIDTH + "  SettingsUtil.SCREEN_HEIGHT:  " + SettingsUtil.SCREEN_HEIGHT);
        lp.width = this.menuWidth;
        lp.height = this.menuHeight;
        window.setAttributes(lp);
        init();
    }

    private void init() {
        this.numText = (TextView) findViewById(R.id.comm_finetune_numr);
        this.nameText = (TextView) findViewById(R.id.comm_finetune_namer);
        this.adjustText = (TextView) findViewById(R.id.comm_finetune_frequency);
    }

    public void setNumText(String num) {
        String chNum = getContext().getString(R.string.menu_tv_channel_no);
        TextView textView = this.numText;
        textView.setText(chNum + num);
    }

    public void setNameText(String name) {
        this.nameText.setText(name);
    }

    public void setAdjustText(String hz) {
        String adjustFre = getContext().getString(R.string.menu_tv_freq);
        TextView textView = this.adjustText;
        textView.setText(adjustFre + hz);
    }

    public void setPositon(int xoff, int yoff) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = xoff;
        lp.y = yoff;
        this.xOff = xoff;
        this.yOff = yoff;
        window.setAttributes(lp);
    }

    public void setSize() {
        Window window = getWindow();
        Display d = window.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.height = d.getHeight();
        p.width = d.getWidth();
        window.setAttributes(p);
    }

    public TextView getNumText() {
        return this.numText;
    }

    public void setNumText(TextView numText2) {
        this.numText = numText2;
    }

    public TextView getNameText() {
        return this.nameText;
    }

    public void setNameText(TextView nameText2) {
        this.nameText = nameText2;
    }

    public TextView getAdjustText() {
        return this.adjustText;
    }

    public void setAdjustText(TextView adjustText2) {
        this.adjustText = adjustText2;
    }

    public void setxOff(int xOff2) {
        this.xOff = xOff2;
    }

    public void setyOff(int yOff2) {
        this.yOff = yOff2;
    }

    public int getxOff() {
        return this.xOff;
    }

    public int getyOff() {
        return this.yOff;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 164) {
            return true;
        }
        switch (keyCode) {
            case 24:
            case 25:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
}
