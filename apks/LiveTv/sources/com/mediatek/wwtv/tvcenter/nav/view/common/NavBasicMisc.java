package com.mediatek.wwtv.tvcenter.nav.view.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.ref.WeakReference;

public class NavBasicMisc implements NavBasic {
    protected static String TAG = "NavBasicMisc";
    protected int componentID = 0;
    protected int componentPriority = 10;
    /* access modifiers changed from: protected */
    public Context mContext = null;
    private Handler mHandler = null;
    protected boolean mIsComponetShow = false;

    public NavBasicMisc(Context mContext2) {
        this.mContext = mContext2;
        this.mHandler = new InternalHandler(this, mContext2.getMainLooper());
    }

    public boolean isVisible() {
        boolean z;
        synchronized (this) {
            z = this.mIsComponetShow;
        }
        return z;
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

    public Handler getHandler() {
        return this.mHandler;
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

    public void setVisibility(int visibility) {
        if (visibility == 0) {
            synchronized (this) {
                this.mIsComponetShow = true;
                if ((251658240 & this.componentID) != 0) {
                    ComponentsManager.updateActiveCompId(false, this.componentID);
                    ComponentStatusListener.getInstance().updateStatus(2, this.componentID);
                }
            }
            return;
        }
        synchronized (this) {
            this.mIsComponetShow = false;
            if (this.componentID == ComponentsManager.getActiveCompId()) {
                ComponentsManager.updateActiveCompId(false, 0);
            }
            if ((251658240 & this.componentID) != 0) {
                ComponentStatusListener.getInstance().updateStatus(1, getComponentID());
            }
        }
        stopTimeout();
    }

    public void startTimeout(int delay) {
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

    public void stopTimeout() {
        MtkLog.d(TAG, "stopTimeout");
        if (this.mHandler != null) {
            this.mHandler.removeMessages(this.componentID);
        }
    }

    private static class InternalHandler extends Handler {
        private final WeakReference<NavBasicMisc> mMisc;

        public InternalHandler(NavBasicMisc dialog, Looper L) {
            super(L);
            this.mMisc = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(NavBasicMisc.TAG, "[InternalHandler] handlerMessage occur~");
            if (this.mMisc.get() == null || msg.arg1 != msg.arg2 || msg.arg1 != ((NavBasicMisc) this.mMisc.get()).getComponentID()) {
                return;
            }
            if (msg.obj.equals(NavBasic.NAV_COMPONENT_HIDE_FLAG)) {
                MtkLog.d(NavBasicMisc.TAG, "[InternalHandler] invisible~");
                ((NavBasicMisc) this.mMisc.get()).setVisibility(4);
            } else if (msg.obj.equals("NavComponentShow")) {
                MtkLog.d(NavBasicMisc.TAG, "[InternalHandler] visible~");
                ((NavBasicMisc) this.mMisc.get()).setVisibility(0);
            } else {
                MtkLog.d(NavBasicMisc.TAG, "[InternalHandler] fail");
            }
        }
    }
}
