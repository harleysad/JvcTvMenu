package com.mediatek.wwtv.tvcenter.nav.view.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.ref.WeakReference;

public class NavBasicDialog extends Dialog implements NavBasic {
    protected static String TAG = "NavBasicDialog";
    protected int componentID = 0;
    protected int componentPriority = 10;
    /* access modifiers changed from: protected */
    public Context mContext = null;
    private Handler mHandler = null;
    protected boolean mIsComponetShow = false;
    private boolean mTTSEnable;

    public NavBasicDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        this.mHandler = new InternalHandler(this, context.getMainLooper());
    }

    public void show() {
        synchronized (this) {
            this.mIsComponetShow = true;
            if ((this.componentID & NavBasic.NAV_COMP_ID_MASK) != 0) {
                ComponentsManager.updateActiveCompId(false, this.componentID);
                ComponentStatusListener.getInstance().updateStatus(2, this.componentID);
            }
        }
        super.show();
    }

    public void dismiss() {
        super.dismiss();
        synchronized (this) {
            this.mIsComponetShow = false;
            if (this.componentID == ComponentsManager.getActiveCompId()) {
                ComponentsManager.updateActiveCompId(false, 0);
            }
            if ((this.componentID & NavBasic.NAV_COMP_ID_MASK) != 0) {
                ComponentStatusListener.getInstance().updateStatus(1, this.componentID);
            }
        }
        stopTimeout();
    }

    /* access modifiers changed from: protected */
    public void notifyNavHide() {
        synchronized (this) {
            if (this.componentID == ComponentsManager.getActiveCompId()) {
                ComponentsManager.updateActiveCompId(false, 0);
            }
            if ((this.componentID & NavBasic.NAV_COMP_ID_MASK) != 0) {
                ComponentStatusListener.getInstance().updateStatus(1, this.componentID);
            }
        }
    }

    public boolean isVisible() {
        return this.mIsComponetShow;
    }

    public boolean isKeyHandler(int keyCode) {
        return false;
    }

    public int getComponentID() {
        return this.componentID;
    }

    public int getPriority() {
        return this.componentPriority;
    }

    public boolean isCoExist(int componentID2) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        return KeyHandler(keyCode, event, false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!((this.componentID & NavBasic.NAV_COMP_ID_MASK) == 0 || this.componentID == ComponentsManager.getActiveCompId())) {
            ComponentsManager.updateActiveCompId(false, this.componentID);
        }
        KeyHandler(keyCode, event, false);
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

    public boolean initView() {
        return false;
    }

    public boolean startComponent() {
        return false;
    }

    public boolean deinitView() {
        this.mContext = null;
        this.mHandler = null;
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getWindow().setWindowAnimations(0);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    public void startTimeout(int delay) {
        if (this.mTTSEnable) {
            MtkLog.d(TAG, "TTS is enabled,don't remove dialog");
            return;
        }
        String str = TAG;
        MtkLog.d(str, "startTimeout delay=" + delay);
        if (this.mHandler != null) {
            this.mHandler.removeMessages(this.componentID);
            Message msg = Message.obtain();
            msg.obj = NavBasic.NAV_COMPONENT_HIDE_FLAG;
            msg.what = this.componentID;
            msg.arg1 = this.componentID;
            msg.arg2 = this.componentID;
            this.mHandler.sendMessageDelayed(msg, (long) delay);
        }
    }

    /* access modifiers changed from: protected */
    public void setTTSEnabled(boolean enable) {
        this.mTTSEnable = enable;
    }

    public void stopTimeout() {
        MtkLog.d(TAG, "stopTimeout");
        if (this.mHandler != null) {
            this.mHandler.removeMessages(this.componentID);
        }
    }

    private static class InternalHandler extends Handler {
        private final WeakReference<NavBasicDialog> mDialog;

        public InternalHandler(NavBasicDialog dialog, Looper L) {
            super(L);
            this.mDialog = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(NavBasicDialog.TAG, "[InternalHandler] handlerMessage occur~");
            if (this.mDialog.get() != null && msg.arg1 == msg.arg2 && msg.arg1 == ((NavBasicDialog) this.mDialog.get()).getComponentID() && msg.obj.equals(NavBasic.NAV_COMPONENT_HIDE_FLAG)) {
                MtkLog.d(NavBasicDialog.TAG, "[InternalHandler] dismiss()~");
                ((NavBasicDialog) this.mDialog.get()).dismiss();
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("Base", "KeyEvent=" + event);
        if (!(DataSeparaterUtil.getInstance() == null || DataSeparaterUtil.getInstance().getValueAutoSleep() == 1)) {
            TurnkeyUiMainActivity.getInstance().getHandlers().sendEmptyMessage(2);
        }
        return super.dispatchKeyEvent(event);
    }
}
