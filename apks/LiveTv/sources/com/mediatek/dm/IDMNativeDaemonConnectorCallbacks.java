package com.mediatek.dm;

interface IDMNativeDaemonConnectorCallbacks {
    void onDaemonConnected();

    boolean onEvent(int i, byte[] bArr, String[] strArr);
}
