package com.mediatek.wwtv.tvcenter.nav.view.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.ref.WeakReference;

public class NavBasicView extends LinearLayout implements NavBasic {
    protected static String TAG = "NavBasicView";
    protected int componentID = 0;
    protected int componentPriority = 10;
    /* access modifiers changed from: protected */
    public Context mContext = null;
    private Handler mHandler = null;
    /* access modifiers changed from: protected */
    public boolean mIsComponetShow = false;

    public NavBasicView(Context context) {
        super(context);
        this.mContext = context;
        this.mHandler = new InternalHandler(this, context.getMainLooper());
        initView();
    }

    public NavBasicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mHandler = new InternalHandler(this, context.getMainLooper());
        initView();
    }

    public NavBasicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        this.mHandler = new InternalHandler(this, context.getMainLooper());
        initView();
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
        } else {
            synchronized (this) {
                this.mIsComponetShow = false;
                if (this.componentID == ComponentsManager.getActiveCompId()) {
                    ComponentsManager.updateActiveCompId(false, 0);
                }
                if ((251658240 & this.componentID) != 0) {
                    ComponentStatusListener.getInstance().updateStatus(1, getComponentID());
                }
                stopTimeout();
            }
        }
        super.setVisibility(visibility);
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

    public void startTimeout(int delay) {
        String str = TAG;
        MtkLog.d(str, "startTimeout delay=" + delay + ", componentID: " + this.componentID);
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
        String str = TAG;
        MtkLog.d(str, "stopTimeout, componentID: " + this.componentID);
        if (this.mHandler != null) {
            this.mHandler.removeMessages(this.componentID);
        }
    }

    private static class InternalHandler extends Handler {
        private final WeakReference<NavBasicView> mView;

        public InternalHandler(NavBasicView dialog, Looper L) {
            super(L);
            this.mView = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(NavBasicView.TAG, "[InternalHandler] handlerMessage occur~");
            if (this.mView.get() != null) {
                String str = NavBasicView.TAG;
                MtkLog.d(str, "[InternalHandler] mView.get().getComponentID(): " + ((NavBasicView) this.mView.get()).getComponentID());
                if (msg.arg1 != msg.arg2 || msg.arg1 != ((NavBasicView) this.mView.get()).getComponentID()) {
                    return;
                }
                if (msg.obj.equals(NavBasic.NAV_COMPONENT_HIDE_FLAG)) {
                    MtkLog.d(NavBasicView.TAG, "[InternalHandler] gone~");
                    ((NavBasicView) this.mView.get()).setVisibility(8);
                } else if (msg.obj.equals("NavComponentShow")) {
                    MtkLog.d(NavBasicView.TAG, "[InternalHandler] visible~");
                    ((NavBasicView) this.mView.get()).setVisibility(0);
                } else {
                    MtkLog.d(NavBasicView.TAG, "[InternalHandler] fail");
                }
            }
        }
    }
}
