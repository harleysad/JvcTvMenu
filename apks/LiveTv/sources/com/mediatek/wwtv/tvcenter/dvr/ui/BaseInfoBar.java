package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.UImanager;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrConstant;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressLint({"ViewConstructor"})
public class BaseInfoBar extends PopupWindow {
    Runnable clearTask = new Runnable() {
        public void run() {
            MtkLog.e("BaseInfoBar", "BaseInfoBar.this.dismiss() start....");
            if (!UImanager.showing) {
                if (!(StateDvr.getInstance() == null || StateDvr.getInstance().getStatePVRHandler() == null)) {
                    StateDvr.getInstance().getStatePVRHandler().sendEmptyMessage(10001);
                }
                if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().getHandler() != null) {
                    StateDvrFileList.getInstance().getHandler().sendEmptyMessage(10001);
                }
            } else if (DvrManager.getInstance() != null) {
                DvrManager.getInstance().getTopHandler().sendEmptyMessage(DvrConstant.Dissmiss_Info_Bar);
            }
        }
    };
    private BaseInfoBar currentBaseInfoBar;
    public Activity mContext;
    public Long mDefaultDuration = Long.valueOf(MessageType.delayMillis10);
    public Long mRefreshDuration = 1000L;
    private ScheduledFuture<?> scheduleFuture;

    public BaseInfoBar(Activity context, int layoutID) {
        super(context.getLayoutInflater().inflate(layoutID, (ViewGroup) null), -2, -2);
        initView();
        this.mContext = context;
    }

    public BaseInfoBar(Activity context, int layoutID, Long duration, int width, int height) {
        super(context.getLayoutInflater().inflate(layoutID, (ViewGroup) null), width, height);
        initView();
        this.mContext = context;
        setDuration(Long.valueOf(duration.longValue()));
    }

    public void setDuration(Long duration) {
        this.mDefaultDuration = duration;
    }

    public void show() {
        if (!isShowing()) {
            setLocation();
            initView();
        }
        startTimeTask((BaseInfoBar) null);
    }

    public void show(BaseInfoBar bBar) {
        this.currentBaseInfoBar = bBar;
        if (!isShowing()) {
            setLocation();
            initView();
        }
        startTimeTask(bBar);
    }

    /* access modifiers changed from: protected */
    public void setLocation() {
        try {
            showAtLocation((RelativeLayout) this.mContext.findViewById(R.id.linear_glview), 17, 20, 20);
        } catch (Exception e) {
        }
    }

    public void initView() {
    }

    public void doSomething() {
    }

    public void dismiss() {
        StringBuilder sb = new StringBuilder();
        sb.append("dismiss()>");
        sb.append(isShowing());
        sb.append(">>>");
        sb.append(this.mContext == null ? this.mContext : Boolean.valueOf(this.mContext.isFinishing()));
        MtkLog.d("dismiss", sb.toString());
        if (isShowing() && this.mContext != null && !this.mContext.isFinishing()) {
            super.dismiss();
        }
        stopTimerTask();
    }

    public void startTimeTask(BaseInfoBar bBar) {
        ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(5);
        try {
            if (this.mDefaultDuration == null || this.mDefaultDuration.longValue() < 0) {
                this.mDefaultDuration = Long.valueOf(MessageType.delayMillis4);
            }
            if (this.scheduleFuture != null) {
                this.scheduleFuture.cancel(true);
            }
            this.scheduleFuture = scheduExec.schedule(this.clearTask, this.mDefaultDuration.longValue(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        }
    }

    public void stopTimerTask() {
        try {
            this.scheduleFuture.cancel(true);
            this.clearTask.run();
        } catch (Exception e) {
        }
    }
}
