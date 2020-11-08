package com.mediatek.dm;

import android.os.IHwBinder;
import android.util.Log;

/* compiled from: DMServer */
final class DMDeathRecipient implements IHwBinder.DeathRecipient {
    private static final String TAG = "DMDeathRecipient";
    private DMServer mDmServer = null;

    public DMDeathRecipient(DMServer dmServer) {
        this.mDmServer = dmServer;
    }

    public void serviceDied(long cookie) {
        Log.e(TAG, "hidl service died.");
    }
}
