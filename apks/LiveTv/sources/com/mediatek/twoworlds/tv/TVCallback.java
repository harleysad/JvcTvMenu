package com.mediatek.twoworlds.tv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import com.mediatek.twoworlds.tv.common.MtkTvIntentBase;
import com.mediatek.twoworlds.tv.common.MtkTvIntentNotifyCodeBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TVCallback {
    private static final String TAG = "TVCallback";
    /* access modifiers changed from: private */
    public static final CallbackList<MtkTvTVCallbackHandler> clientCallbacks = new CallbackList<>();
    private static final TVCallback mCallback = new TVCallback();
    /* access modifiers changed from: private */
    public static final Map<String, ArrayList<MtkTvTVCallbackHandler>> mCfgValChgCallBacks = new HashMap();
    /* access modifiers changed from: private */
    public static Context mContext;
    private static HandlerThread mHandlerThead = null;
    private static int mLastDtValue = -1;
    private static Handler mThreadHandler;
    /* access modifiers changed from: private */
    public static PendingIntent pendItforBGM = null;
    /* access modifiers changed from: private */
    public static PendingIntent pendItforNORMAL_ON = null;
    private boolean callBackDebug = true;
    private List<String> callBacks = new ArrayList();

    private TVCallback() {
        if (mHandlerThead == null) {
            mHandlerThead = new HandlerThread(TAG);
            mHandlerThead.start();
            mThreadHandler = new Handler(mHandlerThead.getLooper());
        }
    }

    public static TVCallback getTVCallback() {
        return mCallback;
    }

    public static void setContext(Context context) {
        mContext = context;
        TVNativeWrapper.registerDefaultCallback_native();
    }

    public void destroyCallBack() {
        TvDebugLog.d(TAG, "destroyCallBack:");
        if (this.callBackDebug) {
            for (int i = 0; i < this.callBacks.size(); i++) {
                TvDebugLog.d(TAG, this.callBacks.get(i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void registerCallback(MtkTvTVCallbackHandler cb) {
        synchronized (TVCallback.class) {
            TvDebugLog.d(TAG, "registerCallback:" + cb);
            if (cb != null) {
                clientCallbacks.add(cb);
                if (this.callBackDebug) {
                    this.callBacks.add(cb.toString());
                }
            }
        }
        TVNativeWrapper.registerDefaultCallback_native();
    }

    /* access modifiers changed from: protected */
    public void unregisterCallback(MtkTvTVCallbackHandler cb) {
        synchronized (TVCallback.class) {
            TvDebugLog.d(TAG, "unregisterCallback:" + cb);
            if (cb != null) {
                clientCallbacks.remove(cb);
                if (this.callBackDebug) {
                    this.callBacks.remove(cb.toString());
                }
                if (this.callBacks.size() == 0) {
                    TVNativeWrapper.unregisterDefaultCallback_native();
                }
            }
        }
    }

    public int addListenerConfig(MtkTvTVCallbackHandler cb, String cfgId) {
        TvDebugLog.d(TAG, "addListenerConfig: " + cb.toString() + ", " + cfgId);
        synchronized (MtkTvConfig.class) {
            ArrayList<MtkTvTVCallbackHandler> callbacklist = mCfgValChgCallBacks.get(cfgId);
            if (callbacklist == null) {
                TVNativeWrapper.addConfigListener_native(cfgId);
                TvDebugLog.d(TAG, "addListenerConfig: new, create list");
                ArrayList arrayList = new ArrayList();
                arrayList.add(cb);
                mCfgValChgCallBacks.put(cfgId, arrayList);
            } else {
                if (!callbacklist.contains(cb)) {
                    callbacklist.add(cb);
                }
                TvDebugLog.d(TAG, "addListenerConfig: add to list," + callbacklist.size());
            }
        }
        return 0;
    }

    public int removeListenerConfig(MtkTvTVCallbackHandler cb, String cfgId) {
        TvDebugLog.d(TAG, "removeListenerConfig: " + cb.toString() + ", " + cfgId);
        List<String> removeList = new ArrayList<>();
        synchronized (MtkTvConfig.class) {
            if (cfgId == null) {
                try {
                    for (String obj : mCfgValChgCallBacks.keySet()) {
                        removeList.add(obj.toString());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                removeList.add(cfgId);
            }
            for (String id : removeList) {
                removeOneListenerConfig(cb, id);
            }
        }
        return 0;
    }

    private int removeOneListenerConfig(MtkTvTVCallbackHandler cb, String cfgId) {
        synchronized (MtkTvConfig.class) {
            ArrayList<MtkTvTVCallbackHandler> callbacklist = mCfgValChgCallBacks.get(cfgId);
            if (callbacklist != null) {
                callbacklist.remove(cb);
                if (callbacklist.size() == 0) {
                    mCfgValChgCallBacks.remove(cfgId);
                    TVNativeWrapper.removeConfigListener_native(cfgId);
                }
            }
        }
        return 0;
    }

    protected static int DO_notifySvctxNotificationCode(int code) {
        TvDebugLog.d(TAG, "(From JNI) notifySvctxNotificationCode=" + code);
        synchronized (TVCallback.class) {
            try {
                int N = clientCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        int ret = clientCallbacks.getBroadcastItem(i).notifySvctxNotificationCode(code);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                }
                clientCallbacks.finishBroadcast();
                return 0;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    protected static int DO_notifySvctxNotificationCode(int path, int code) {
        TvDebugLog.d(TAG, "(From JNI) notifySvctxNotificationCode(path = " + path + ",code = " + code + ")");
        synchronized (TVCallback.class) {
            try {
                int N = clientCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        int ret = clientCallbacks.getBroadcastItem(i).notifySvctxNotificationCodebyPath(path, code);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                }
                clientCallbacks.finishBroadcast();
                return 0;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    protected static int DO_notifyOtherMessage(int a1, int a2, int a3) {
        TvDebugLog.d(TAG, "(From JNI) a1=" + a1 + " a2=" + a2 + " a3=" + a3);
        synchronized (TVCallback.class) {
            try {
                int N = clientCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        int ret = clientCallbacks.getBroadcastItem(i).notifyOtherMessage(a1, a2, a3);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                }
                clientCallbacks.finishBroadcast();
                return 0;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    protected static int DO_notifyConfigMessage(int notifyId, int data) {
        int ret = 0;
        TvDebugLog.d(TAG, "notifyConfigMessage notifyId=" + notifyId + " data=" + data);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyConfigMessage(notifyId, data);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyChannelListUpdateMsg(int condition, int reason, int data) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyChannelListUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyChannelListUpdateMsg(condition, reason, data);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyListModeUpdateMsg(int oldMode, int newMode) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyListModeUpdateMsg: oldMode = " + oldMode + ", newMode = " + newMode + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyListModeUpdateMsg(oldMode, newMode);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyOclScanInfo(int msgId, int channelNum) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyOclScanInfo: msgId = " + msgId + ", channelNum = " + channelNum + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyOclScanInfo(msgId, channelNum);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyBisskeyUpdateMsg(int condition, int reason, int data) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyBisskeyUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyBisskeyUpdateMsg(condition, reason, data);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifySatlListUpdateMsg(int condition, int reason, int data) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifySatlListUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifySatlListUpdateMsg(condition, reason, data);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTvproviderUpdateMsg(int svlid, int count, int[] eventType, int[] svlRecId) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTvproviderUpdateMsg");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTvproviderUpdateMsg(svlid, count, eventType, svlRecId);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    public static int DO_notifyConcernColumnUpdateMsg(int svlid, int count, int[] eventType, int[] svlRecId) throws RemoteException {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyConcernColumnUpdateMsg");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyConcernColumnUpdateMsg(svlid, count, eventType, svlRecId);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifySvlIdUpdateMsg(int condition, int reason, int data) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifySvlIdUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifySvlIdUpdateMsg(condition, reason, data);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTslIdUpdateMsg(int condition, int reason, int data) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTslIdUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTslIdUpdateMsg(condition, reason, data);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyNativeAppStatus(int nativeAppId, boolean show) {
        int ret = 0;
        TvDebugLog.d(TAG, "notifyNativeAppStatus nativeAppId=" + nativeAppId + " show=" + show);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyNativeAppStatus(nativeAppId, show);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyShowOSDMessage(int stringId, int msgType) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyShowOSDMessage stringId=" + stringId + " msgType=" + msgType);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyShowOSDMessage(stringId, msgType);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyHideOSDMessage() {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyHideOSDMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyHideOSDMessage();
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyAmpVolCtrlMessage(int volume, boolean isMute) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyAmpVolCtrlMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyAmpVolCtrlMessage(volume, isMute);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyCecNotificationCode(int code) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyCecNotificationCode");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyCecNotificationCode(code);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyCecFrameInfo(int initLA, int destLA, int opcode, int[] operand, int operandSize) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyCecFrameInfo");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyCecFrameInfo(initLA, destLA, opcode, operand, operandSize);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifySysAudMod(int sysAudMod) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifySysAudMod");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifySysAudMod(sysAudMod);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyCecActiveSource(int destLA, int destPA, boolean activeRoutingPath) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyCecActiveSource");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyCecActiveSource(destLA, destPA, activeRoutingPath);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyMhlScratchpadData(int destLA, int adopterId, int dataLen, int[] scratchpadData) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyMhlScratchpadData");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyMhlScratchpadData(destLA, adopterId, dataLen, scratchpadData);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyDeviceDiscovery() {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyDeviceDiscovery");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyDeviceDiscovery();
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyUiMsDisplay(int uiType, boolean show) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyUiMsDisplay");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyUiMsDisplay(uiType, show);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifySpdInfoFrame(int upStatus, int upStatusChg, int svStatus, int svStatusChg, int[] vndrName, int[] productDes, int srcInfo) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifySpdInfoFrame");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < N) {
                    try {
                        ret = clientCallbacks.getBroadcastItem(i2).notifySpdInfoFrame(upStatus, upStatusChg, svStatus, svStatusChg, vndrName, productDes, srcInfo);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    }
                    i = i2 + 1;
                } else {
                    clientCallbacks.finishBroadcast();
                }
            }
        }
        return ret;
    }

    protected static int DO_notifyEventNotificationCode(int type, int arg1, int arg2, int arg3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyEventNotificationCode");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyEventNotification(type, arg1, arg2, (long) arg3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyRecordPBCallback(int type, int arg1, int arg2, int arg3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyRecordPBCallback");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyRecordPBNotification(type, arg1, arg2, (long) arg3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyLinuxPowerRequest(int action, int tag) {
        final int localTag = tag;
        TvDebugLog.d(TAG, "notifyLinuxPowerRequest action=" + action + " tag=" + tag);
        synchronized (TVCallback.class) {
            if (action == 0) {
                try {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Intent intent = new Intent();
                                intent.addFlags(268435488);
                                intent.setAction(MtkTvIntentNotifyCodeBase.MTK_INTENT_STRING);
                                intent.putExtra("code", 1);
                                intent.putExtra("reason", localTag);
                                if (TVCallback.mContext != null) {
                                    TVCallback.mContext.sendBroadcast(intent);
                                }
                            } catch (Exception e) {
                                TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                            }
                        }
                    }).start();
                } catch (Throwable th) {
                    throw th;
                }
            } else if (action == 1) {
                TvDebugLog.d(TAG, "Power on by linux is not implemented.\n");
            } else if (action == 2) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Intent intent = new Intent();
                            intent.addFlags(268435488);
                            intent.setAction(MtkTvIntentBase.MTK_INTENT_BGM_FINISHED_STRING);
                            if (TVCallback.mContext != null) {
                                TVCallback.mContext.sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                        }
                    }
                }).start();
            } else if (action == 3) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Intent intent = new Intent();
                            intent.addFlags(268435488);
                            intent.setAction(MtkTvIntentBase.MTK_INTENT_BGM_POWER_ON_INTERRUPT_STRING);
                            if (TVCallback.mContext != null) {
                                TVCallback.mContext.sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                        }
                    }
                }).start();
            } else if (action == 10) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Intent intent = new Intent();
                            intent.addFlags(268435488);
                            intent.setAction("mtk.intent.rtctimer.set.bgm");
                            if (TVCallback.mContext != null) {
                                long mTriggerTime = ((long) localTag) * 1000;
                                long mCurrentTime = System.currentTimeMillis();
                                AlarmManager alarmMgr = (AlarmManager) TVCallback.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
                                if (TVCallback.pendItforBGM != null) {
                                    alarmMgr.cancel(TVCallback.pendItforBGM);
                                    PendingIntent unused = TVCallback.pendItforBGM = null;
                                }
                                if (TVCallback.pendItforNORMAL_ON != null) {
                                    alarmMgr.cancel(TVCallback.pendItforNORMAL_ON);
                                    PendingIntent unused2 = TVCallback.pendItforNORMAL_ON = null;
                                }
                                PendingIntent pendIt = PendingIntent.getBroadcast(TVCallback.mContext, 0, intent, 134217728);
                                alarmMgr.setExact(0, mTriggerTime, pendIt);
                                PendingIntent unused3 = TVCallback.pendItforBGM = pendIt;
                                TvDebugLog.d(TVCallback.TAG, "Set AlarmManager for Linux RTC = " + mTriggerTime + ", Current system RTC = " + mCurrentTime);
                            }
                        } catch (Exception e) {
                            TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                        }
                    }
                }).start();
            } else if (action == 11) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Intent intent = new Intent();
                            intent.addFlags(268435488);
                            intent.setAction("mtk.intent.rtctimer.set.normal_wakeup");
                            if (TVCallback.mContext != null) {
                                long mTriggerTime = ((long) localTag) * 1000;
                                long mCurrentTime = System.currentTimeMillis();
                                AlarmManager alarmMgr = (AlarmManager) TVCallback.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
                                if (TVCallback.pendItforBGM != null) {
                                    alarmMgr.cancel(TVCallback.pendItforBGM);
                                    PendingIntent unused = TVCallback.pendItforBGM = null;
                                }
                                if (TVCallback.pendItforNORMAL_ON != null) {
                                    alarmMgr.cancel(TVCallback.pendItforNORMAL_ON);
                                    PendingIntent unused2 = TVCallback.pendItforNORMAL_ON = null;
                                }
                                PendingIntent pendIt = PendingIntent.getBroadcast(TVCallback.mContext, 0, intent, 134217728);
                                alarmMgr.setExact(0, mTriggerTime, pendIt);
                                PendingIntent unused3 = TVCallback.pendItforNORMAL_ON = pendIt;
                                TvDebugLog.d(TVCallback.TAG, "Set AlarmManager for Linux RTC = " + mTriggerTime + ", Current system RTC = " + mCurrentTime);
                            }
                        } catch (Exception e) {
                            TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                        }
                    }
                }).start();
            } else if (action == 12) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            if (TVCallback.mContext != null) {
                                AlarmManager alarmMgr = (AlarmManager) TVCallback.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
                                if (TVCallback.pendItforBGM != null) {
                                    alarmMgr.cancel(TVCallback.pendItforBGM);
                                    PendingIntent unused = TVCallback.pendItforBGM = null;
                                }
                                if (TVCallback.pendItforNORMAL_ON != null) {
                                    alarmMgr.cancel(TVCallback.pendItforNORMAL_ON);
                                    PendingIntent unused2 = TVCallback.pendItforNORMAL_ON = null;
                                }
                                TvDebugLog.d(TVCallback.TAG, "Disable AlarmManager for Linux RTC.");
                            }
                            Intent intent = new Intent();
                            intent.addFlags(4);
                            intent.setAction(MtkTvIntentNotifyCodeBase.MTK_INTENT_STRING);
                            intent.putExtra("code", 2);
                            if (TVCallback.mContext != null) {
                                TVCallback.mContext.sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                        }
                    }
                }).start();
            }
        }
        return 0;
    }

    protected static int DO_notifyScanNotification(int msg_id, int scanProgress, int channelNum, int argv4) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyScanNotification");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyScanNotification(msg_id, scanProgress, channelNum, argv4);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifySimulated3dAutoTurnOff() {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifySimulated3dAutoTurnOff");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifySimulated3dAutoTurnOff();
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifySleepTimerChange(int type, int remainingTime) {
        TvDebugLog.d(TAG, "DO_notifySleepTimerChange type=" + type + ", remaining time =" + remainingTime);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifySleepTimerChange(type, remainingTime);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyTimeSyncSourceChange(int source) {
        TvDebugLog.d(TAG, "DO_notifyTimeSyncSourceChange source=" + source);
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction(MtkTvIntentBase.MTK_INTENT_TIME_SYNC_SOURCE_CHANGED);
                if (TVCallback.mContext != null) {
                    TVCallback.mContext.sendBroadcast(intent);
                }
            }
        }).start();
        return 0;
    }

    /* access modifiers changed from: private */
    public static void syncTimeFromLinuxToAndroid() {
        MtkTvTimeBase timeSetting = new MtkTvTimeBase();
        if (timeSetting.getTimeSyncSource() != 1 || mContext == null) {
            TvDebugLog.d(TAG, "{DT}sync src not come from TS, DO NOT sync Time from linux to android ");
            return;
        }
        Context context = mContext;
        Context context2 = mContext;
        AlarmManager alarm = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        long milliSeconds = timeSetting.getBroadcastTimeInUtcSeconds();
        TvDebugLog.d(TAG, "{DT} set time " + milliSeconds + " to alarm manager");
        try {
            alarm.setTime(1000 * milliSeconds);
        } catch (Exception e) {
            TvDebugLog.d(TAG, "{DT}sync Time from linux to android fail", e);
        }
    }

    /* access modifiers changed from: private */
    public static void syncTimeZoneFromLinuxToAndroid() {
        String tzStr;
        MtkTvTimeBase timeSetting = new MtkTvTimeBase();
        if (timeSetting.getTimeSyncSource() != 1 || mContext == null) {
            TvDebugLog.d(TAG, "{DT}sync src not come from TS, DO NOT sync TZ from linux to android ");
            return;
        }
        Context context = mContext;
        Context context2 = mContext;
        AlarmManager alarm = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        TimeZone zone = TimeZone.getDefault();
        int totalOffset = ((int) timeSetting.getTimeZone()) + timeSetting.getDst();
        if (totalOffset == zone.getOffset(System.currentTimeMillis()) / 1000) {
            TvDebugLog.d(TAG, "{DT} no need to sync time zone from Linux to Android, totalOffset = " + totalOffset);
            return;
        }
        int hourOffset = totalOffset / MtkTvTimeFormatBase.SECONDS_PER_HOUR;
        if (totalOffset > 0) {
            tzStr = "Etc/GMT" + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING;
        } else {
            tzStr = "Etc/GMT" + "+";
        }
        String tzStr2 = tzStr + String.format("%d", new Object[]{Integer.valueOf(hourOffset)});
        TvDebugLog.d(TAG, "{DT} need sync time zone to Android ,timezone = " + tzStr2);
        try {
            alarm.setTimeZone(tzStr2);
        } catch (Exception e) {
            TvDebugLog.d(TAG, "{DT}sync timezone from linux to android fail", e);
        }
    }

    protected static int DO_notifyDtEvent(int cond, int arg) {
        TvDebugLog.d(TAG, "DO_notifyDtEvent cond = " + cond);
        boolean needSyncTime = false;
        boolean needSyncTimeZone = false;
        boolean needUpdateDiffValue = false;
        final int localArg = arg;
        if (cond <= 4) {
            if (arg != mLastDtValue || cond == 4) {
                needSyncTime = true;
            } else {
                needSyncTime = false;
            }
            mLastDtValue = arg;
        } else if (cond != 3 && cond != 4 && cond != 5 && cond != 8 && cond != 9 && cond != 10 && cond != 13) {
            needSyncTimeZone = false;
            needUpdateDiffValue = false;
        } else if (cond != 13) {
            needSyncTimeZone = true;
        } else {
            needUpdateDiffValue = true;
        }
        if ("0".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
            needSyncTimeZone = false;
            TvDebugLog.d(TAG, "{DT} ro.vendor.mtk.system.timesync.existed is false, disable sync time/tz to android\r\n");
        } else {
            needUpdateDiffValue = false;
        }
        if (needSyncTime) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(MtkTvIntentBase.MTK_INTENT_TIME_TS_UTC_UPDATE);
                        if (TVCallback.mContext != null) {
                            TVCallback.mContext.sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        TvDebugLog.d(TVCallback.TAG, "{DT} sendBroadcastAsUser failed.", e);
                    }
                    if ("1".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
                        TVCallback.syncTimeFromLinuxToAndroid();
                        TVCallback.syncTimeZoneFromLinuxToAndroid();
                    }
                }
            }).start();
        }
        if (needSyncTimeZone) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(MtkTvIntentBase.MTK_INTENT_TIME_ZONE_CHANGED);
                        intent.putExtra("TimeZone", localArg);
                        if (TVCallback.mContext != null) {
                            TVCallback.mContext.sendBroadcast(intent);
                        }
                        if ("1".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
                            TVCallback.syncTimeZoneFromLinuxToAndroid();
                        }
                    } catch (Exception e) {
                        TvDebugLog.d(TVCallback.TAG, "{DT} sendBroadcastAsUser failed.", e);
                    }
                }
            }).start();
        }
        if (!needUpdateDiffValue) {
            return 0;
        }
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction(MtkTvIntentBase.MTK_INTENT_TIME_SYS_BRDCST_DIFF_CHANGED);
                intent.putExtra("TimeSysBrdcstDiff", localArg);
                if (TVCallback.mContext != null) {
                    TVCallback.mContext.sendBroadcast(intent);
                }
            }
        }).start();
        return 0;
    }

    protected static int DO_notifyOADMessage(int messageType, String dataString, int progress, boolean autoDld, int argv5) {
        StringBuilder sb = new StringBuilder();
        sb.append("DO_notifyOADMessage messageType=");
        int i = messageType;
        sb.append(i);
        sb.append(", dataString =");
        String str = dataString;
        sb.append(str);
        sb.append(", progress =");
        int i2 = progress;
        sb.append(i2);
        sb.append(", autoDld =");
        boolean z = autoDld;
        sb.append(z);
        TvDebugLog.d(TAG, sb.toString());
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 < N) {
                    try {
                        clientCallbacks.getBroadcastItem(i4).notifyOADMessage(i, str, i2, z, argv5);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    }
                    i3 = i4 + 1;
                } else {
                    clientCallbacks.finishBroadcast();
                }
            }
        }
        return 0;
    }

    protected static int DO_notifyMHEG5Message(int type, int argv1, int argv2, int argv3) {
        TvDebugLog.d(TAG, "(From JNI)notifyMHEG5Message=" + type);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyMHEG5Message(type, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyMHEG5LanuchHbbtv(String sAppURL, int argv1) {
        TvDebugLog.d(TAG, "(From JNI)DO_notifyMHEG5LanuchHbbtv.");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyMHEG5LanuchHbbtv(sAppURL, argv1);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyMHEG5MimeTypeSupport(String sMimeType, boolean[] pbSupported, int argv1) {
        TvDebugLog.d(TAG, "(From JNI)DO_notifyMHEG5MimeTypeSupport.");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyMHEG5MimeTypeSupport(sMimeType, pbSupported, argv1);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyEWSPAMessage(int type) {
        TvDebugLog.d(TAG, "(From JNI)notifyEWSPAMessage=" + type);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyEWSPAMessage(type);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyMHPMessage(int type, int argv1, int argv2, int argv3) {
        TvDebugLog.d(TAG, "(From JNI)notifyMHPMessage=" + type);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyMHPMessage(type, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyBannerMessage(int msgType, int msgName, int argv1, int argv2) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyBannerMessage msgType=" + msgType + ", msgName=" + msgName);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyBannerMessage(msgType, msgName, argv1, argv2);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyEASMessage(int updateType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyEASMessage type=" + updateType + ", argv1 =" + argv1);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyEASMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyRecordCallback(int type, int arg1, int arg2) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyRecordCallback");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyRecordNotification(type, arg1, arg2);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTimeshiftCallback(int type, long arg1) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTimeshiftCallback");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTimeshiftNotification(type, arg1);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTimeshiftRecordStatus(int status, long arg1) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTimeshiftRecordStatus");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTimeshiftRecordStatus(status, arg1);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTimeshiftNoDiskFile(long arg1) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTimeshiftNoDiskFile");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTimeshiftNoDiskFile(arg1);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTimeshiftSpeedUpdate(int speed) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTimeshiftSpeedUpdate");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTimeshiftSpeedUpdate(speed);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTimeshiftPlaybackStatusUpdate(int status) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTimeshiftPlaybackStatusUpdate");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTimeshiftPlaybackStatusUpdate(status);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyTimeshiftStorageRemoved(int status) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyTimeshiftStorageRemoved");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyTimeshiftStorageRemoved();
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyCIMessage(int slot_id, int messageType, int arg3, int arg4, MtkTvCIMMIMenuBase MMIMenu, MtkTvCIMMIEnqBase MMIEnq) {
        StringBuilder sb = new StringBuilder();
        sb.append("DO_notifyCIMessage messageType=");
        int i = messageType;
        sb.append(i);
        sb.append(", slid_id =");
        int i2 = slot_id;
        sb.append(i2);
        TvDebugLog.d(TAG, sb.toString());
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 < N) {
                    try {
                        clientCallbacks.getBroadcastItem(i4).notifyCIMessage(i2, i, arg3, arg4, MMIMenu, MMIEnq);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    }
                    i3 = i4 + 1;
                } else {
                    clientCallbacks.finishBroadcast();
                }
            }
        }
        return 0;
    }

    protected static int DO_notifyAVModeMessage(int updateType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyAVModeMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyAVModeMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyOpenVCHIPMessage(int updateType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "notifyOpenVCHIPMessage type=" + updateType + ", argv1 =" + argv1);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyOpenVCHIPMessage(updateType, argv1, argv2, (long) argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyATSCEventMessage(int updateType, int argv1, int argv2, long argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyATSCEventMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyATSCEventMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyPWDDialogMessage(int updateType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyPWDDialogMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyPWDDialogMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyScreenSaverMessage(int msgID, int argv1, int argv2, int argv3) {
        TvDebugLog.d(TAG, "(From JNI)notifyScreenSaverMessage=" + msgID);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyScreenSaverMessage(msgID, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyWarningMessage(int updateType, int channelID, String eventInfo, int duration, int args5) {
        TvDebugLog.d(TAG, "(From JNI)notifyWarningMessage=" + updateType);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyWarningMessage(updateType, channelID, eventInfo, duration, args5);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyTeletextMessage(int msg_id, int argv1, int argv2, int argv3) {
        TvDebugLog.d(TAG, "(From JNI)notifyTeletextMessage messageID=" + msg_id);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyTeletextMessage(msg_id, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyGingaMessage(int updateType, String sAppID, String sAppName) {
        TvDebugLog.d(TAG, "(From JNI)notifyGingaMessage \n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyGingaMessage(updateType, sAppID, sAppName);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyGingaVolumeChanged(int updateType, int level) {
        TvDebugLog.d(TAG, "(From JNI)DO_notifyGingaVolumeChanged \n");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyGingaVolumeChanged(updateType, level);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyHBBTVMessage(int callbackType, int[] callbackData, int callbackDataLen) {
        TvDebugLog.d(TAG, "(From JNI)DO_notifyHBBTVMessage callbackType=" + callbackType);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyHBBTVMessage(callbackType, callbackData, callbackDataLen);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int Do_notifyFeatureMessage(int updateType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "Do_notifyFeatureMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyFeatureMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int Do_notifyBroadcastMessage(int msgType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "Do_notifyBroadcastMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyBroadcastMessage(msgType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyPipPopMessage(int updateType, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyPipPopMessage");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyPipPopMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyUpgradeMessage(int messageType, int arg1, int arg2, int arg3) {
        TvDebugLog.d(TAG, "DO_notifyUpgradeMessage messageType=" + messageType + ", arg1 =" + arg1 + ", arg2 =" + arg2);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyUpgradeMessage(messageType, arg1, arg2, arg3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyNoUsedkeyMessage(int updateType, int argv1, int argv2, int argv3) {
        TvDebugLog.d(TAG, "DO_notifyNoUsedkeyMessage messageType=" + updateType);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyNoUsedkeyMessage(updateType, argv1, argv2, (long) argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyCDTLogoMessage(int updateType, int argv1, int argv2, int argv3) {
        TvDebugLog.d(TAG, "DO_notifyCDTChLogoMessage messageType=" + updateType);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyCDTLogoMessage(updateType, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    protected static int DO_notifyGpioStatus(int gpioId, int status) {
        TvDebugLog.d(TAG, "DO_notifyGpioStatus gpioId=" + gpioId + ", status =" + status);
        synchronized (TVCallback.class) {
            try {
                int N = clientCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        TvDebugLog.d(TAG, "callback : " + clientCallbacks.getBroadcastItem(i));
                        int ret = clientCallbacks.getBroadcastItem(i).notifyGpioStatus(gpioId, status);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                }
                clientCallbacks.finishBroadcast();
                return 0;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    protected static int DO_notifyUARTSerialPortCallback(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data) {
        TvDebugLog.d(TAG, "DO_notifyUARTSerialPortCallback uartSerialID=" + uartSerialID + ",ioNotifyCond =" + ioNotifyCond + ",eventCode=" + eventCode + ",data=" + data);
        synchronized (TVCallback.class) {
            try {
                int N = clientCallbacks.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        TvDebugLog.d(TAG, "callback : " + clientCallbacks.getBroadcastItem(i));
                        int ret = clientCallbacks.getBroadcastItem(i).notifyUARTSerialPortCallback(uartSerialID, ioNotifyCond, eventCode, data);
                    } catch (Exception e) {
                        TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                }
                clientCallbacks.finishBroadcast();
                return 0;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    protected static int DO_notifySubtitleMsg(int msg_id, int argv1, int argv2, int argv3) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifySubtitleMsg");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifySubtitleMsg(msg_id, argv1, argv2, argv3);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyInputSignalChanged(int inputId, boolean hasSignal) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyInputSignalChanged");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyInputSignalChanged(inputId, hasSignal);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_notifyVdpMuteAllFinished(int vdpId) {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_notifyVdpMuteAllFinished");
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyVdpMuteAllFinished(vdpId);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

    protected static int DO_queryNetworkType() {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_queryNetworkType");
        if (mContext == null) {
            return -1;
        }
        synchronized (TVCallback.class) {
            NetworkInfo info = ((ConnectivityManager) mContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (info != null) {
                ret = info.getType();
                TvDebugLog.d(TAG, "DO_queryNetworkType getType=" + ret);
            } else {
                TvDebugLog.d(TAG, "DO_queryNetworkType getType failed" + 0);
            }
        }
        return ret;
    }

    protected static int DO_queryWifiState() {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_queryWifiState");
        if (mContext == null) {
            return -1;
        }
        synchronized (TVCallback.class) {
            NetworkInfo info = ((ConnectivityManager) mContext.getSystemService("connectivity")).getNetworkInfo(1);
            if (info != null) {
                ret = info.getState().ordinal();
                TvDebugLog.d(TAG, "DO_queryWifiState getState=" + ret);
            } else {
                TvDebugLog.d(TAG, "DO_queryWifiState getState failed" + 0);
            }
        }
        return ret;
    }

    protected static int DO_queryEthernetState() {
        int ret = 0;
        TvDebugLog.d(TAG, "DO_queryEthernetState");
        if (mContext == null) {
            return -1;
        }
        synchronized (TVCallback.class) {
            NetworkInfo info = ((ConnectivityManager) mContext.getSystemService("connectivity")).getNetworkInfo(9);
            if (info != null) {
                ret = info.getState().ordinal();
                TvDebugLog.d(TAG, "DO_queryEthernetState getState=" + ret);
            } else {
                TvDebugLog.d(TAG, "DO_queryEthernetState getState failed" + 0);
            }
        }
        return ret;
    }

    public static int DO_notifyConfigValuechanged(String cfgId) {
        final String cfgid = cfgId;
        TvDebugLog.d(TAG, "DO_notifyConfigValuechanged cfgId=" + cfgId);
        mThreadHandler.post(new Runnable() {
            public void run() {
                synchronized (TVCallback.class) {
                    ArrayList<MtkTvTVCallbackHandler> callbacklist = (ArrayList) TVCallback.mCfgValChgCallBacks.get(cfgid);
                    if (callbacklist != null) {
                        int N = TVCallback.clientCallbacks.beginBroadcast();
                        for (int i = 0; i < N; i++) {
                            try {
                                if (callbacklist.contains(TVCallback.clientCallbacks.getBroadcastItem(i))) {
                                    ((MtkTvTVCallbackHandler) TVCallback.clientCallbacks.getBroadcastItem(i)).notifyConfigValuechanged(cfgid);
                                }
                            } catch (Exception e) {
                                TvDebugLog.d(TVCallback.TAG, "RemoteException:" + e.getMessage());
                            }
                        }
                        TVCallback.clientCallbacks.finishBroadcast();
                    }
                }
            }
        });
        return 0;
    }

    protected static int DO_notifyHtmlAgentMessage(int callbackType, int[] callbackData, int callbackDataLen) {
        TvDebugLog.d(TAG, "(From JNI)DO_notifyHtmlAgentMessage callbackType=" + callbackType);
        synchronized (TVCallback.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyHtmlAgentMessage(callbackType, callbackData, callbackDataLen);
                } catch (Exception e) {
                    TvDebugLog.d(TAG, "RemoteException:" + e.getMessage());
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return 0;
    }

    private static class CallbackList<T> extends ArrayList<T> {
        private CallbackList() {
        }

        public int beginBroadcast() {
            return size();
        }

        public void finishBroadcast() {
        }

        /* access modifiers changed from: package-private */
        public T getBroadcastItem(int position) {
            return get(position);
        }
    }
}
