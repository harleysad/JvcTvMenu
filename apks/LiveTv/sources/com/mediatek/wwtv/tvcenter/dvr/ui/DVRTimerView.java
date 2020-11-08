package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;

public class DVRTimerView extends CommonInfoBar {
    private int channelId = 0;
    private final int defaultHeight = 100;
    private final int defaultOffsetX = 20;
    private final int defaultOffsetY = 20;
    private final int defaultWidth = EPGConfig.EPG_CHANGING_CHANNEL;
    private TextView mInfo;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private View mRootView;
    private WindowManager windowManager;
    private final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public DVRTimerView(Activity context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void setLocation() {
        addToWM();
    }

    public void setInfo(String info) {
        if (this.mRootView != null) {
            if (this.mInfo.getVisibility() != 4) {
                this.mInfo.setVisibility(0);
            }
            this.mInfo.setText(info);
        }
    }

    public void setCurrentTime(long mills) {
        long mills2 = mills + 1;
        long minute = mills2 / 60;
        setInfo(String.format("[%02d:%02d:%02d]", new Object[]{Long.valueOf(minute / 60), Long.valueOf(minute % 60), Long.valueOf(mills2 % 60)}));
    }

    private void addToWM() {
        this.windowManager = (WindowManager) this.mContext.getApplicationContext().getSystemService("window");
        this.wmParams.type = DvrManager.CHANGE_CHANNEL;
        this.wmParams.flags |= 8;
        this.wmParams.gravity = 83;
        this.wmParams.format = -3;
        getContentView().setBackgroundResource(R.drawable.translucent_background);
        this.mRootView = getContentView();
        this.mInfo = (TextView) this.mRootView.findViewById(R.id.info);
        setDefaultLocation();
        try {
            this.windowManager.addView(this.mRootView, this.wmParams);
        } catch (Exception e) {
        }
        try {
            Class.forName("android.widget.PopupWindow").getDeclaredMethod("setShowing", new Class[]{Boolean.TYPE}).invoke(this, new Object[]{true});
        } catch (Exception e2) {
            Log.d("DVRTimerView", "Exception " + e2);
        }
    }

    public void doSomething() {
        super.doSomething();
    }

    public void setDefaultLocation() {
        this.wmParams.x = 20;
        this.wmParams.y = 20;
        this.wmParams.width = EPGConfig.EPG_CHANGING_CHANNEL;
        this.wmParams.height = 100;
    }

    public void changeLocation() {
        this.wmParams.x = 0;
        this.wmParams.y = 110;
        this.windowManager.updateViewLayout(this.mRootView, this.wmParams);
    }

    public void dismiss() {
        if (!(this.windowManager == null || this.mRootView == null)) {
            try {
                this.windowManager.removeView(this.mRootView);
            } catch (Exception e) {
                e.toString();
            }
        }
        if (getmOnDismissListener() != null) {
            getmOnDismissListener().onDismiss();
        }
        try {
            Class.forName("android.widget.PopupWindow").getDeclaredMethod("setShowing", new Class[]{Boolean.TYPE}).invoke(this, new Object[]{false});
        } catch (Exception e2) {
            Log.d("DVRTimerView", "Exception " + e2);
        }
    }

    public PopupWindow.OnDismissListener getmOnDismissListener() {
        return this.mOnDismissListener;
    }

    public void setmOnDismissListener(PopupWindow.OnDismissListener mOnDismissListener2) {
        this.mOnDismissListener = mOnDismissListener2;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void setChannelId(int channelId2) {
        this.channelId = channelId2;
    }
}
