package com.mediatek.dm;

import android.os.RemoteException;
import android.util.Log;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_DEVICE_MANAGER_EVENT_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.IMtkDmManagerCallback;

/* compiled from: DeviceManager */
final class DmManagerCallback extends IMtkDmManagerCallback.Stub {
    private static final String TAG = "DeviceManagerCallback";
    private DeviceManager mDm = null;

    public DmManagerCallback(DeviceManager dm) {
        this.mDm = dm;
    }

    public void mtk_hidl_dm_event_nfy_fct(HIDL_DEVICE_MANAGER_EVENT_T event) {
        try {
            Log.i(TAG, "mtk_hidl_dm_event_nfy_fct event \n");
            Log.i(TAG, "mtk_hidl_dm_event_nfy_fct event: " + event.i4_type + ", s_mount_point_path: " + event.s_mount_point_path);
            if (this.mDm != null) {
                this.mDm.notifyDeviceEvent(DMConvertUtil.hidlToJavaEvent(event));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
