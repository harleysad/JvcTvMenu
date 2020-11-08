package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.ref.WeakReference;

public class NavBaseInfoBar extends PopupWindow implements ComponentStatusListener.ICStatusListener {
    private static NavBaseInfoBar mSelf;
    public Activity mContext;
    private Long mDefaultDuration = Long.valueOf(MessageType.delayMillis5);
    private MyHandler mHandler;

    static class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            super.handleMessage(msg);
        }
    }

    public NavBaseInfoBar(Activity context, int layoutID) {
        super(context.getLayoutInflater().inflate(layoutID, (ViewGroup) null), -2, -2);
        initView();
        mSelf = this;
        this.mHandler = new MyHandler(context);
        this.mContext = context;
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(5, this);
    }

    public NavBaseInfoBar(Activity context, int layoutID, Long duration, int width, int height) {
        super(context.getLayoutInflater().inflate(layoutID, (ViewGroup) null), width, height);
        initView();
        this.mContext = context;
        setDuration(Long.valueOf(duration.longValue()));
        mSelf = this;
        this.mHandler = new MyHandler(context);
    }

    public void setDuration(Long duration) {
        this.mDefaultDuration = duration;
    }

    public void show() {
        setLocation();
        initView();
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                NavBaseInfoBar.this.dismiss();
            }
        }, this.mDefaultDuration.longValue());
    }

    /* access modifiers changed from: protected */
    public void setLocation() {
        MtkLog.e("mContext", "mContext:" + this.mContext);
        View view = this.mContext.findViewById(R.id.linear_glview);
        showAtLocation(view, 17, 20, (view.getHeight() / 2) - ((view.getHeight() / 2) / 3));
    }

    public void initView() {
    }

    public void updateComponentStatus(int statusID, int value) {
        dismiss();
    }
}
