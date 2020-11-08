package com.mediatek.dm;

import android.os.RemoteException;
import android.util.Log;
import com.mediatek.dm.IDMCallback;

public class DMClientHandler extends IDMCallback.Stub {
    private static final String TAG = "[J]DMClientHandler";
    private DeviceManager manager;

    public DMClientHandler(DeviceManager manager2) {
        this.manager = manager2;
    }

    public void notifyDeviceEvent(DeviceManagerEvent event) throws RemoteException {
        Log.i(TAG, "notifyDeviceEvent[2]");
        this.manager.notifyDeviceEvent(event);
    }
}
