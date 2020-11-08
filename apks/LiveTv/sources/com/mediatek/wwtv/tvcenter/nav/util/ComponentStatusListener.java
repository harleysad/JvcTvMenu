package com.mediatek.wwtv.tvcenter.nav.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseArray;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ComponentStatusListener {
    public static final int NAV_CHANNEL_CHANGED = 10;
    public static final int NAV_CHANNEL_RETURN = 16;
    public static final int NAV_COMPONENT_HIDE = 1;
    public static final int NAV_COMPONENT_SHOW = 2;
    public static final int NAV_CONTENT_ALLOWED = 12;
    public static final int NAV_CONTENT_BLOCKED = 13;
    public static final int NAV_ENTER_ANDR_PIP = 14;
    public static final int NAV_ENTER_LANCHER = 6;
    public static final int NAV_ENTER_MMP = 7;
    public static final int NAV_ENTER_STANDBY = 8;
    public static final int NAV_EXIT_ANDR_PIP = 15;
    public static final int NAV_INPUT_SELECT = 9;
    public static final int NAV_KEY_OCCUR = 5;
    public static final int NAV_LANGUAGE_CHANGED = 20;
    public static final int NAV_PAUSE = 4;
    public static final int NAV_POWER_OFF = 17;
    public static final int NAV_POWER_ON = 18;
    public static final int NAV_RESUME = 3;
    public static final int NAV_SHUT_DOWN = 19;
    private static final int STATUS_CHANGED = 1;
    private static final String TAG = "ComponentStatusListener";
    private static ComponentStatusListener mCSListener = null;
    private static int mParam1 = 0;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public SparseArray<List<ICStatusListener>> mRigister;
    private HandlerThread mThread;

    public interface ICStatusListener {
        void updateComponentStatus(int i, int i2);
    }

    private ComponentStatusListener() {
        this.mHandler = null;
        this.mThread = null;
        this.mRigister = new SparseArray<>();
        this.mThread = new HandlerThread(TAG, 0);
        this.mThread.start();
        this.mHandler = new InternalHandler(this);
    }

    public static synchronized ComponentStatusListener getInstance() {
        ComponentStatusListener componentStatusListener;
        synchronized (ComponentStatusListener.class) {
            if (mCSListener == null) {
                mCSListener = new ComponentStatusListener();
            }
            componentStatusListener = mCSListener;
        }
        return componentStatusListener;
    }

    public void updateStatus(int statusID) {
        delayUpdateStatus(statusID, 0, 0);
    }

    public void updateStatus(int statusID, int value) {
        delayUpdateStatus(statusID, value, 0);
    }

    public void delayUpdateStatus(int statusID, long delayMillis) {
        delayUpdateStatus(statusID, 0, delayMillis);
    }

    public void delayUpdateStatus(int statusID, int value, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = statusID;
        msg.arg2 = value;
        MtkLog.d(TAG, "updateStatus, statusID=" + statusID + ",value=" + value + ",delayMillis=" + delayMillis);
        if (delayMillis > 0) {
            this.mHandler.sendMessageDelayed(msg, delayMillis);
        } else {
            this.mHandler.sendMessage(msg);
        }
    }

    public boolean addListener(int statusID, ICStatusListener listener) {
        List<ICStatusListener> handlers = this.mRigister.get(statusID);
        MtkLog.d(TAG, "ComponentStatusListener, key=" + statusID);
        if (handlers != null) {
            for (ICStatusListener handler : handlers) {
                if (listener == handler) {
                    MtkLog.d(TAG, "addListener, already existed");
                    return false;
                }
            }
        } else {
            MtkLog.d(TAG, "ComponentStatusListener, new ArrayList");
            handlers = new ArrayList<>();
            this.mRigister.append(statusID, handlers);
        }
        return handlers.add(listener);
    }

    public boolean removeListener(ICStatusListener listener) {
        int size = this.mRigister.size();
        for (int i = 0; i < size; i++) {
            this.mRigister.valueAt(i).remove(listener);
        }
        return true;
    }

    public boolean removeAll() {
        synchronized (ComponentStatusListener.class) {
            this.mRigister.clear();
            this.mThread.quit();
            this.mThread = null;
            mCSListener = null;
        }
        return true;
    }

    public static int getParam1() {
        return mParam1;
    }

    public static void setParam1(int param) {
        synchronized (ComponentStatusListener.class) {
            mParam1 = param;
        }
    }

    private static class InternalHandler extends Handler {
        private final WeakReference<ComponentStatusListener> mDialog;

        public InternalHandler(ComponentStatusListener dialog) {
            this.mDialog = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(ComponentStatusListener.TAG, "[InternalHandler] handlerMessage occur~" + msg.what);
            if (this.mDialog.get() != null) {
                synchronized (ComponentStatusListener.class) {
                    List<ICStatusListener> handlers = (List) ((ComponentStatusListener) this.mDialog.get()).mRigister.get(msg.what);
                    if (handlers != null) {
                        for (ICStatusListener listener : handlers) {
                            listener.updateComponentStatus(msg.what, msg.arg2);
                        }
                    }
                    if (msg.what == 2 && TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode()) {
                        ComponentsManager.getInstance().hideAllComponents();
                    }
                }
            }
        }
    }
}
