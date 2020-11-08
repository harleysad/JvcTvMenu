package com.mediatek.dm;

import android.os.RemoteCallbackList;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DMCallBack {
    private static final String TAG = "[J]DMCallBack";
    private static final RemoteCallbackList<IDMCallback> clientCallbacks = new RemoteCallbackList<>();
    private boolean callBackDebug = true;
    private List<String> callBacks = new ArrayList();

    public void destroyCallBack() {
        Log.d(TAG, "destroyCallBack:");
        if (this.callBackDebug) {
            for (int i = 0; i < this.callBacks.size(); i++) {
                Log.d(TAG, this.callBacks.get(i));
            }
        }
        clientCallbacks.kill();
    }

    public void registerCallback(IDMCallback cb) {
        Log.d(TAG, "registerCallback:" + cb);
        if (cb != null) {
            clientCallbacks.register(cb);
            if (this.callBackDebug) {
                this.callBacks.add(cb.toString());
            }
        }
    }

    public void unregisterCallback(IDMCallback cb) {
        Log.d(TAG, "unregisterCallback:" + cb);
        if (cb != null) {
            clientCallbacks.unregister(cb);
            if (this.callBackDebug) {
                this.callBacks.remove(cb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void notifyDeviceEvent(DeviceManagerEvent event) {
        Log.i(TAG, "Notify step 3. notifyDeviceEvent=" + event);
        synchronized (DMCallBack.class) {
            int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    clientCallbacks.getBroadcastItem(i).notifyDeviceEvent(event);
                } catch (Exception e) {
                    Log.d(TAG, "notifyDeviceEvent Exception");
                }
            }
            clientCallbacks.finishBroadcast();
        }
    }
}
