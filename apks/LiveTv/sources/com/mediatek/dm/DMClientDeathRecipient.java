package com.mediatek.dm;

import android.os.IHwBinder;
import android.util.Log;

/* compiled from: DeviceManager */
final class DMClientDeathRecipient implements IHwBinder.DeathRecipient {
    private static final String TAG = "DeviceManagerDeath";
    private DeviceManager mDm = null;

    public DMClientDeathRecipient(DeviceManager dm) {
        this.mDm = dm;
    }

    public void serviceDied(long cookie) {
        Log.e(TAG, "hidl service died.cookie:%lld" + cookie);
    }
}
