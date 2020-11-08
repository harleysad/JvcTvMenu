package com.android.tv.settings.partnercustomizer.tvsettingservice;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import com.android.tv.settings.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DolbyVisionLogoWindow extends PopupWindow {
    private static DolbyVisionLogoWindow mDolbyVisionLogoWindow;
    private Context mContext;
    private View mView;
    private WindowManager manager;

    public DolbyVisionLogoWindow(Context context) {
        this.mContext = context;
        init();
    }

    public void show() {
        if (isShowing()) {
            showAtLocation((FrameLayout) ((Activity) this.mContext).findViewById(16908290), 17, 0, 0);
        }
    }

    public void dismiss() {
        if (!(this.mView == null || this.manager == null)) {
            try {
                this.manager.removeView(this.mView);
            } catch (Exception e) {
            }
        }
        super.dismiss();
    }

    private void init() {
        this.manager = (WindowManager) this.mContext.getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.flags = 8;
        params.type = 2002;
        params.gravity = 8388661;
        params.format = -3;
        params.x = 0;
        params.y = 0;
        DisplayMetrics dm = new DisplayMetrics();
        this.manager.getDefaultDisplay().getMetrics(dm);
        params.width = (int) (((double) dm.widthPixels) * 0.25d);
        params.height = (int) (((double) dm.heightPixels) * 0.09d);
        MtkLog.d("params.width ", "params.width = " + params.width + "  params.height = " + params.height);
        this.mView = LayoutInflater.from(this.mContext).inflate(R.layout.partner_dolbyvision_logo_window, (ViewGroup) null);
        setContentView(this.mView);
        try {
            this.manager.addView(this.mView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
