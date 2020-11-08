package com.mediatek.wwtv.tvcenter.commonview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadingDialog(Context mContext) {
        super(mContext, 0);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.loading);
        setCancelable(false);
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }

    public void setWindowPosition(int x, int y, int width, int height) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (ScreenConstant.SCREEN_WIDTH * width) / 1920;
        lp.height = (ScreenConstant.SCREEN_HEIGHT * height) / 1080;
        lp.x = x;
        lp.y = y;
        window.setAttributes(lp);
    }

    public void setWindowPosition(int x, int y) {
        setWindowPosition(x, y, 200, 200);
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
