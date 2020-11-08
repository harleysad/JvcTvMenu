package com.android.tv.settings.partnercustomizer.tvsettingservice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import com.android.tv.settings.R;

public class LoadingUI extends Dialog {
    private Context mContext;
    private WindowManager mWindowManager;

    public LoadingUI(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public LoadingUI(Context mContext2) {
        super(mContext2, 0);
        this.mContext = mContext2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.partner_loading);
        setWindowPosition(0, 0);
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
        this.mWindowManager = (WindowManager) this.mContext.getApplicationContext().getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getMetrics(dm);
        lp.width = (int) (((double) dm.widthPixels) * 0.1d);
        lp.height = (int) (((double) dm.heightPixels) * 0.1d);
        lp.x = x;
        lp.y = y;
        window.setAttributes(lp);
    }

    public void setWindowPosition(int x, int y) {
        setWindowPosition(x, y, 200, 200);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return true;
    }
}
