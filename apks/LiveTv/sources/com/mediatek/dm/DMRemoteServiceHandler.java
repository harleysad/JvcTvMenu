package com.mediatek.dm;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.mediatek.dm.IDMRemoteService;

public class DMRemoteServiceHandler extends IDMRemoteService.Stub {
    public static final String SERVICE_NAME = "DMRemoteService";
    private static final String TAG = "[J]DMRemoteServiceHandler";
    private Context context;
    private DMServer dm;
    private DMCallBack dmCallback = new DMCallBack();

    public DMRemoteServiceHandler(Context context2) {
        this.context = context2;
    }

    public void registerDMCallback(IDMCallback cb) throws RemoteException {
        Log.d(TAG, "Enter  registerCallback ");
        Log.d(TAG, "cb = " + cb);
        this.dmCallback.registerCallback(cb);
        Log.d(TAG, "Leave  registerCallback ");
    }

    public void unregisterDMCallback(IDMCallback cb) throws RemoteException {
        Log.d(TAG, "Enter  registerCallback ");
        Log.d(TAG, "cb = " + cb);
        this.dmCallback.unregisterCallback(cb);
        Log.d(TAG, "Leave  registerCallback ");
    }

    public void notifyListener(DeviceManagerEvent event) {
        Log.i(TAG, "Notify step 2.\n");
        this.dmCallback.notifyDeviceEvent(event);
    }
}
