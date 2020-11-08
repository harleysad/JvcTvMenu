package com.mediatek.dm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.storage.IStorageManager;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_DEVICE_MANAGER_EVENT_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.IMtkDmCommon;

public class DMServer {
    /* access modifiers changed from: private */
    public static String TAG = "DMServer";
    private static boolean mBootupDone = false;
    /* access modifiers changed from: private */
    public static final Object mDMLock = new Object();
    private static final boolean mDebug = false;
    /* access modifiers changed from: private */
    public static IMtkDmCommon mDmHidlService = null;
    /* access modifiers changed from: private */
    public static boolean mDtvReady;
    /* access modifiers changed from: private */
    public boolean isFastboot;
    /* access modifiers changed from: private */
    public boolean isScreenOff;
    private final Context mContext;
    private DMDeathRecipient mDeathRecipient = null;
    /* access modifiers changed from: private */
    public ArrayList<Device> mDevices;
    private DmServerCallback mDmHidlCallback = null;
    private final StorageEventListener mListener = new StorageEventListener() {
        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
            String access$000 = DMServer.TAG;
            Log.d(access$000, "onVolumeStateChanged vol=" + vol);
            DMServer.this.onVolumeStateChangedInternal(vol);
        }
    };
    private ArrayList<String> mMountPartName;
    /* access modifiers changed from: private */
    public ArrayList<MountPoint> mMountPoints;
    /* access modifiers changed from: private */
    public final IStorageManager mSm;
    /* access modifiers changed from: private */
    public StorageManager mStorageManager;

    private native void nativeDtvComRpcInit(DMServer dMServer);

    private native void nativeDtvComRpcUinit();

    public DMServer(Context context) {
        this.mContext = context;
        this.mMountPoints = new ArrayList<>();
        this.mDevices = new ArrayList<>();
        this.mMountPartName = new ArrayList<>();
        this.mStorageManager = (StorageManager) context.getSystemService("storage");
        this.mSm = IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        if (this.mSm != null) {
            try {
                mDmHidlService = IMtkDmCommon.getService();
                int retry = 0;
                while (mDmHidlService == null) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mDmHidlService = IMtkDmCommon.getService();
                    retry++;
                }
                Log.d(TAG, "RETRY: wait dm hidl service ready retry = " + retry);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            this.mDmHidlCallback = new DmServerCallback(this);
            this.mDeathRecipient = new DMDeathRecipient(this);
            try {
                if (mDmHidlService != null) {
                    mDmHidlService.linkToDeath(this.mDeathRecipient, 22222);
                    mDmHidlService.mtk_hidl_dm_register_dmserver_callback(this.mDmHidlCallback);
                }
            } catch (RemoteException e3) {
                e3.printStackTrace();
            }
            mDtvReady = false;
            mBootupDone = false;
            if (needCallDtvFun()) {
                dtvComRpcInit();
            }
            nativeStart();
            initFastbootReceiver(context);
            this.mStorageManager.registerListener(this.mListener);
            mBootupDone = true;
            return;
        }
        throw new IllegalStateException("Failed to find running mount service");
    }

    public void releaseInstance() {
        nativeEnd();
    }

    public void dtvComRpcInit() {
        Log.i(TAG, "dtvComRpcInit start");
        new Thread("DMServer## dtvComRpcInit") {
            public void run() {
                try {
                    do {
                    } while (!new File("/data/vendor/tmp/dtv_svc_is_ready").exists());
                    Log.d(DMServer.TAG, "init rpc.");
                    if (DMServer.mDmHidlService != null) {
                        DMServer.mDmHidlService.mtk_hidl_rpc_init();
                    }
                    DMServer.this.vold_do_cb("DTV_START", (String) null, (String) null);
                    while (true) {
                        sleep(6000000);
                    }
                } catch (Exception ex) {
                    Log.v(DMServer.TAG, "dtvComRpcInit", ex);
                }
            }
        }.start();
    }

    private void initFastbootReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                    if ("1".equals(SystemProperties.get("sys.pm.warmboot"))) {
                        boolean unused = DMServer.this.isFastboot = true;
                        String access$000 = DMServer.TAG;
                        Log.v(access$000, "isFastboot:" + DMServer.this.isFastboot);
                    }
                } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    boolean unused2 = DMServer.this.isScreenOff = true;
                }
            }
        }, filter);
    }

    /* access modifiers changed from: private */
    public void onVolumeStateChangedInternal(final VolumeInfo vol) {
        final int state = vol.getState();
        if (vol.getType() == 0) {
            if (state == 2 || state == 3) {
                final VolumeInfo msVol = vol;
                new Thread("DMServer## msfilter_mount_thread") {
                    /* JADX WARNING: Can't fix incorrect switch cases order */
                    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00da, code lost:
                        r4 = 65535;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00db, code lost:
                        switch(r4) {
                            case 0: goto L_0x00e7;
                            case 1: goto L_0x00e7;
                            case 2: goto L_0x00e5;
                            case 3: goto L_0x00e5;
                            case 4: goto L_0x00e3;
                            case 5: goto L_0x00e1;
                            case 6: goto L_0x00e1;
                            default: goto L_0x00de;
                        };
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00de, code lost:
                        r3 = 1;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00df, code lost:
                        r6 = r3;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00e1, code lost:
                        r3 = 5;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00e3, code lost:
                        r3 = 7;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00e5, code lost:
                        r3 = 2;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00e7, code lost:
                        r3 = 1;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ef, code lost:
                        if (r5.getDescription() != null) goto L_0x0123;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f1, code lost:
                        r5.fsLabel = com.mediatek.dm.DMServer.access$600(r1.this$0).getStorageVolume(new java.io.File(r14)).getDescription((android.content.Context) null);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "fsLabel from storageVolume =" + r0.fsLabel);
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0123, code lost:
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "devname : " + r15);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "systemtype : " + r8);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "major : " + r10);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "minor : " + r9);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "type : " + r6);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "getDescription : " + r0.getDescription());
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "getPath : " + r0.getPath());
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "sysPath : " + r0.getDisk().sysPath);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "diskId : " + r0.getDisk().getId());
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0219, code lost:
                        if (r15.indexOf("mtpvolume") == -1) goto L_0x0220;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:43:0x021b, code lost:
                        r3 = r15;
                        r4 = r15;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:44:0x021d, code lost:
                        r13 = r3;
                        r11 = r4;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0220, code lost:
                        r5 = r0.getDisk().sysPath;
                        r3 = r5.substring(r5.lastIndexOf("/block/") + "/block/".length());
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0239, code lost:
                        if ((r9 % 16) != 0) goto L_0x023d;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:47:0x023b, code lost:
                        r4 = r3;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:48:0x023d, code lost:
                        r11 = r3 + (r9 % 16);
                        r13 = r3;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0253, code lost:
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "initRpcReceiver drvName : " + r13);
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "initRpcReceiver mntName : " + r11);
                        r4 = new android.os.StatFs(r14);
                        r7 = 1;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0292, code lost:
                        if (r4.getTotalBytes() > 0) goto L_0x02ab;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0296, code lost:
                        if (r7 <= 10) goto L_0x029c;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0298, code lost:
                        r24 = r6;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:55:0x029c, code lost:
                        r7 = r7 + 1;
                        java.lang.Thread.sleep(500);
                        r4.restat(r14);
                        r6 = r6;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:56:0x02ab, code lost:
                        r24 = r6;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:57:0x02ad, code lost:
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "RETRY: get mountpoint size ino: retry = " + r7);
                        r3 = com.mediatek.dm.DMServer.access$000();
                        r5 = new java.lang.StringBuilder();
                        r5.append("getTotalBytes : ");
                        r25 = r7;
                        r5.append(r4.getTotalBytes());
                        android.util.Log.v(r3, r5.toString());
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "getAvailableBytes : " + r4.getAvailableBytes());
                        r6 = r1.this$0;
                        r20 = r4.getTotalBytes() / android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                        r22 = r4.getAvailableBytes() / android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                        r26 = r0.getPath().toString();
                        r27 = r0.getDisk().getId();
                        r28 = r0.getDescription();
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0327, code lost:
                        r3 = r3;
                        r29 = r4;
                        r4 = r20;
                        r30 = r0;
                        r0 = r6;
                        r1 = r3;
                        r20 = r24;
                        r21 = r25;
                        r6 = r22;
                        r22 = r8;
                        r23 = r9;
                        r24 = r10;
                        r31 = r11;
                        r11 = r26;
                        r18 = r12;
                        r19 = r13;
                        r25 = r14;
                        r26 = r15;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
                        r3 = new com.mediatek.dm.MountPoint(r4, r6, r10, r9, 601, r11, r2, r27, r28, r31, r20);
                        com.mediatek.dm.DMServer.access$700(r0, r1);
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:61:0x035a, code lost:
                        r1 = r33;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:63:?, code lost:
                        r12 = r30;
                        com.mediatek.dm.DMServer.access$800(r1.this$0, new com.mediatek.dm.Device(r24, r23, 601, r12.getDisk().getId(), r2, r19, r12.getDisk().sysPath));
                        android.util.Log.d(com.mediatek.dm.DMServer.access$000(), "Search volume_id: " + r2 + " from MountPoint List");
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:64:0x03a3, code lost:
                        if (com.mediatek.dm.DMServer.access$900(r1.this$0, r2) != false) goto L_0x03d8;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:65:0x03a5, code lost:
                        android.util.Log.d(com.mediatek.dm.DMServer.access$000(), "Notify connected event for: " + r2);
                        com.mediatek.dm.DMServer.access$1000(r1.this$0, new com.mediatek.dm.DeviceManagerEvent(701, r12.getPath().toString(), r12.getDisk().sysPath, false));
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:66:0x03d8, code lost:
                        com.mediatek.dm.DMServer.access$1000(r1.this$0, new com.mediatek.dm.DeviceManagerEvent(601, r12.getPath().toString()));
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:67:0x03f2, code lost:
                        if (com.mediatek.dm.DMServer.access$1100(r1.this$0) == false) goto L_?;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:69:0x03f9, code lost:
                        if (com.mediatek.dm.DMServer.access$1200() != true) goto L_?;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:70:0x03fb, code lost:
                        android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "#### call nativeVoldSendMsg to dtv_svc: path = " + r12.getPath().toString() + ", volume_id = " + r2);
                        com.mediatek.dm.DMServer.access$1300(r1.this$0, r12.getPath().toString(), r31, 601);
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0433, code lost:
                        r0 = e;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0434, code lost:
                        r1 = r33;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:78:?, code lost:
                        return;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
                        return;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
                        return;
                     */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r33 = this;
                            r1 = r33
                            android.os.storage.VolumeInfo r0 = r1     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r2 = r0.getId()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r0.getFsUuid()     // Catch:{ Exception -> 0x0437 }
                            r15 = r3
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r3.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = "/storage/"
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            r3.append(r15)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0437 }
                            r14 = r3
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = "volume_id : "
                            r4.append(r5)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r2)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = "uuid : "
                            r4.append(r5)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r15)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = ":"
                            int r3 = r2.indexOf(r3)     // Catch:{ Exception -> 0x0437 }
                            r13 = 1
                            int r3 = r3 + r13
                            int r4 = r2.length()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r2.substring(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            r12 = r3
                            java.lang.String r3 = ","
                            java.lang.String[] r3 = r12.split(r3)     // Catch:{ Exception -> 0x0437 }
                            r17 = r3
                            r11 = 0
                            r3 = r17[r11]     // Catch:{ Exception -> 0x0437 }
                            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0437 }
                            r10 = r3
                            r3 = r17[r13]     // Catch:{ Exception -> 0x0437 }
                            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0437 }
                            r9 = r3
                            r3 = 0
                            com.mediatek.dm.DMServer r4 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.getDeviceInformation(r15)     // Catch:{ Exception -> 0x0437 }
                            r8 = r4
                            if (r8 != 0) goto L_0x008b
                            java.lang.String r4 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = "filesystemtype is null."
                            android.util.Log.v(r4, r5)     // Catch:{ Exception -> 0x0437 }
                            return
                        L_0x008b:
                            int r4 = r8.hashCode()     // Catch:{ Exception -> 0x0437 }
                            r5 = -1
                            switch(r4) {
                                case -877034094: goto L_0x00d0;
                                case 3127858: goto L_0x00c6;
                                case 3127859: goto L_0x00bc;
                                case 3391763: goto L_0x00b2;
                                case 3616483: goto L_0x00a8;
                                case 97201976: goto L_0x009e;
                                case 110520199: goto L_0x0094;
                                default: goto L_0x0093;
                            }     // Catch:{ Exception -> 0x0437 }
                        L_0x0093:
                            goto L_0x00da
                        L_0x0094:
                            java.lang.String r4 = "tntfs"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = 3
                            goto L_0x00db
                        L_0x009e:
                            java.lang.String r4 = "fat32"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = r13
                            goto L_0x00db
                        L_0x00a8:
                            java.lang.String r4 = "vfat"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = r11
                            goto L_0x00db
                        L_0x00b2:
                            java.lang.String r4 = "ntfs"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = 2
                            goto L_0x00db
                        L_0x00bc:
                            java.lang.String r4 = "ext4"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = 6
                            goto L_0x00db
                        L_0x00c6:
                            java.lang.String r4 = "ext3"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = 5
                            goto L_0x00db
                        L_0x00d0:
                            java.lang.String r4 = "texfat"
                            boolean r4 = r8.equals(r4)     // Catch:{ Exception -> 0x0437 }
                            if (r4 == 0) goto L_0x00da
                            r4 = 4
                            goto L_0x00db
                        L_0x00da:
                            r4 = r5
                        L_0x00db:
                            switch(r4) {
                                case 0: goto L_0x00e7;
                                case 1: goto L_0x00e7;
                                case 2: goto L_0x00e5;
                                case 3: goto L_0x00e5;
                                case 4: goto L_0x00e3;
                                case 5: goto L_0x00e1;
                                case 6: goto L_0x00e1;
                                default: goto L_0x00de;
                            }     // Catch:{ Exception -> 0x0437 }
                        L_0x00de:
                            r3 = 1
                        L_0x00df:
                            r6 = r3
                            goto L_0x00e9
                        L_0x00e1:
                            r3 = 5
                            goto L_0x00df
                        L_0x00e3:
                            r3 = 7
                            goto L_0x00df
                        L_0x00e5:
                            r3 = 2
                            goto L_0x00df
                        L_0x00e7:
                            r3 = 1
                            goto L_0x00df
                        L_0x00e9:
                            android.os.storage.VolumeInfo r3 = r5     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r3.getDescription()     // Catch:{ Exception -> 0x0437 }
                            if (r3 != 0) goto L_0x0123
                            com.mediatek.dm.DMServer r3 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.StorageManager r3 = r3.mStorageManager     // Catch:{ Exception -> 0x0437 }
                            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x0437 }
                            r4.<init>(r14)     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.StorageVolume r3 = r3.getStorageVolume(r4)     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.VolumeInfo r4 = r5     // Catch:{ Exception -> 0x0437 }
                            r7 = 0
                            java.lang.String r7 = r3.getDescription(r7)     // Catch:{ Exception -> 0x0437 }
                            r4.fsLabel = r7     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r7.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r11 = "fsLabel from storageVolume ="
                            r7.append(r11)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r11 = r0.fsLabel     // Catch:{ Exception -> 0x0437 }
                            r7.append(r11)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r4, r7)     // Catch:{ Exception -> 0x0437 }
                        L_0x0123:
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "devname : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r15)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "systemtype : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r8)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "major : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r10)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "minor : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r9)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "type : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r6)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "getDescription : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = r0.getDescription()     // Catch:{ Exception -> 0x0437 }
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "getPath : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.io.File r7 = r0.getPath()     // Catch:{ Exception -> 0x0437 }
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "sysPath : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.DiskInfo r7 = r0.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = r7.sysPath     // Catch:{ Exception -> 0x0437 }
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "diskId : "
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.DiskInfo r7 = r0.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = r7.getId()     // Catch:{ Exception -> 0x0437 }
                            r4.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            r3 = 0
                            r4 = 0
                            java.lang.String r7 = "mtpvolume"
                            int r7 = r15.indexOf(r7)     // Catch:{ Exception -> 0x0437 }
                            if (r7 == r5) goto L_0x0220
                            r3 = r15
                            r4 = r15
                        L_0x021d:
                            r13 = r3
                            r11 = r4
                            goto L_0x0253
                        L_0x0220:
                            android.os.storage.DiskInfo r5 = r0.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = r5.sysPath     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = "/block/"
                            int r11 = r5.lastIndexOf(r7)     // Catch:{ Exception -> 0x0437 }
                            int r16 = r7.length()     // Catch:{ Exception -> 0x0437 }
                            int r13 = r11 + r16
                            java.lang.String r13 = r5.substring(r13)     // Catch:{ Exception -> 0x0437 }
                            r3 = r13
                            int r13 = r9 % 16
                            if (r13 != 0) goto L_0x023d
                            r4 = r3
                            goto L_0x021d
                        L_0x023d:
                            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r13.<init>()     // Catch:{ Exception -> 0x0437 }
                            r13.append(r3)     // Catch:{ Exception -> 0x0437 }
                            r20 = r3
                            int r3 = r9 % 16
                            r13.append(r3)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r13.toString()     // Catch:{ Exception -> 0x0437 }
                            r11 = r3
                            r13 = r20
                        L_0x0253:
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = "initRpcReceiver drvName : "
                            r4.append(r5)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r13)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r4.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = "initRpcReceiver mntName : "
                            r4.append(r5)     // Catch:{ Exception -> 0x0437 }
                            r4.append(r11)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r4)     // Catch:{ Exception -> 0x0437 }
                            r3 = 1
                            android.os.StatFs r4 = new android.os.StatFs     // Catch:{ Exception -> 0x0437 }
                            r4.<init>(r14)     // Catch:{ Exception -> 0x0437 }
                            r7 = r3
                        L_0x028a:
                            long r20 = r4.getTotalBytes()     // Catch:{ Exception -> 0x0437 }
                            r22 = 0
                            int r3 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1))
                            if (r3 > 0) goto L_0x02ab
                            r3 = 10
                            if (r7 <= r3) goto L_0x029c
                            r24 = r6
                            goto L_0x02ad
                        L_0x029c:
                            int r7 = r7 + 1
                            r24 = r6
                            r5 = 500(0x1f4, double:2.47E-321)
                            java.lang.Thread.sleep(r5)     // Catch:{ Exception -> 0x0437 }
                            r4.restat(r14)     // Catch:{ Exception -> 0x0437 }
                            r6 = r24
                            goto L_0x028a
                        L_0x02ab:
                            r24 = r6
                        L_0x02ad:
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r5.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r6 = "RETRY: get mountpoint size ino: retry = "
                            r5.append(r6)     // Catch:{ Exception -> 0x0437 }
                            r5.append(r7)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r5)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r5.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r6 = "getTotalBytes : "
                            r5.append(r6)     // Catch:{ Exception -> 0x0437 }
                            r25 = r7
                            long r6 = r4.getTotalBytes()     // Catch:{ Exception -> 0x0437 }
                            r5.append(r6)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r5)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r5.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r6 = "getAvailableBytes : "
                            r5.append(r6)     // Catch:{ Exception -> 0x0437 }
                            long r6 = r4.getAvailableBytes()     // Catch:{ Exception -> 0x0437 }
                            r5.append(r6)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r3, r5)     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DMServer r6 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.MountPoint r7 = new com.mediatek.dm.MountPoint     // Catch:{ Exception -> 0x0437 }
                            long r20 = r4.getTotalBytes()     // Catch:{ Exception -> 0x0437 }
                            r22 = 1024(0x400, double:5.06E-321)
                            long r20 = r20 / r22
                            long r26 = r4.getAvailableBytes()     // Catch:{ Exception -> 0x0437 }
                            long r22 = r26 / r22
                            r16 = 601(0x259, float:8.42E-43)
                            java.io.File r3 = r0.getPath()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r26 = r3.toString()     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.DiskInfo r3 = r0.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r27 = r3.getId()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r28 = r0.getDescription()     // Catch:{ Exception -> 0x0437 }
                            r3 = r7
                            r29 = r4
                            r4 = r20
                            r30 = r0
                            r0 = r6
                            r1 = r7
                            r20 = r24
                            r21 = r25
                            r6 = r22
                            r22 = r8
                            r8 = r10
                            r23 = r9
                            r24 = r10
                            r10 = r16
                            r31 = r11
                            r11 = r26
                            r18 = r12
                            r12 = r2
                            r19 = r13
                            r13 = r27
                            r25 = r14
                            r14 = r28
                            r26 = r15
                            r15 = r31
                            r16 = r20
                            r3.<init>(r4, r6, r8, r9, r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x0433 }
                            r0.addMountPointsList(r1)     // Catch:{ Exception -> 0x0433 }
                            r1 = r33
                            com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.Device r11 = new com.mediatek.dm.Device     // Catch:{ Exception -> 0x0437 }
                            r6 = 601(0x259, float:8.42E-43)
                            r12 = r30
                            android.os.storage.DiskInfo r3 = r12.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r7 = r3.getId()     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.DiskInfo r3 = r12.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r10 = r3.sysPath     // Catch:{ Exception -> 0x0437 }
                            r3 = r11
                            r4 = r24
                            r5 = r23
                            r8 = r2
                            r9 = r19
                            r3.<init>(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0437 }
                            r0.addDevicesList(r11)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r0 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r3.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = "Search volume_id: "
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            r3.append(r2)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = " from MountPoint List"
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.d(r0, r3)     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            boolean r0 = r0.removeFromPartName(r2)     // Catch:{ Exception -> 0x0437 }
                            if (r0 != 0) goto L_0x03d8
                            java.lang.String r0 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r3.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = "Notify connected event for: "
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            r3.append(r2)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.d(r0, r3)     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DeviceManagerEvent r3 = new com.mediatek.dm.DeviceManagerEvent     // Catch:{ Exception -> 0x0437 }
                            r4 = 701(0x2bd, float:9.82E-43)
                            java.io.File r5 = r12.getPath()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0437 }
                            android.os.storage.DiskInfo r6 = r12.getDisk()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r6 = r6.sysPath     // Catch:{ Exception -> 0x0437 }
                            r7 = 0
                            r3.<init>(r4, r5, r6, r7)     // Catch:{ Exception -> 0x0437 }
                            r0.eventNotification(r3)     // Catch:{ Exception -> 0x0437 }
                        L_0x03d8:
                            com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DeviceManagerEvent r3 = new com.mediatek.dm.DeviceManagerEvent     // Catch:{ Exception -> 0x0437 }
                            java.io.File r4 = r12.getPath()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            r5 = 601(0x259, float:8.42E-43)
                            r3.<init>((int) r5, (java.lang.String) r4)     // Catch:{ Exception -> 0x0437 }
                            r0.eventNotification(r3)     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            boolean r0 = r0.needCallDtvFun()     // Catch:{ Exception -> 0x0437 }
                            if (r0 == 0) goto L_0x0432
                            boolean r0 = com.mediatek.dm.DMServer.mDtvReady     // Catch:{ Exception -> 0x0437 }
                            r3 = 1
                            if (r0 != r3) goto L_0x0432
                            java.lang.String r0 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0437 }
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0437 }
                            r3.<init>()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = "#### call nativeVoldSendMsg to dtv_svc: path = "
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            java.io.File r4 = r12.getPath()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0437 }
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r4 = ", volume_id = "
                            r3.append(r4)     // Catch:{ Exception -> 0x0437 }
                            r3.append(r2)     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0437 }
                            android.util.Log.v(r0, r3)     // Catch:{ Exception -> 0x0437 }
                            com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0437 }
                            java.io.File r3 = r12.getPath()     // Catch:{ Exception -> 0x0437 }
                            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0437 }
                            r4 = r31
                            int unused = r0.nativeVoldSendMsg(r3, r4, r5)     // Catch:{ Exception -> 0x0437 }
                        L_0x0432:
                            goto L_0x0441
                        L_0x0433:
                            r0 = move-exception
                            r1 = r33
                            goto L_0x0438
                        L_0x0437:
                            r0 = move-exception
                        L_0x0438:
                            java.lang.String r2 = com.mediatek.dm.DMServer.TAG
                            java.lang.String r3 = "Failed to get media information on insertion"
                            android.util.Log.v(r2, r3, r0)
                        L_0x0441:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.dm.DMServer.AnonymousClass4.run():void");
                    }
                }.start();
            } else if (state == 0 || state == 8 || state == 7 || state == 5) {
                final VolumeInfo umsVol = vol;
                new Thread("DMServer## msfilter_unmount_thread") {
                    public void run() {
                        try {
                            VolumeInfo local_vol = umsVol;
                            String volume_id = local_vol.getId();
                            String uuid = local_vol.getFsUuid();
                            String mountpoint = "/storage/" + uuid;
                            if (state == 5) {
                                Log.v(DMServer.TAG, "notify ecject intent: " + mountpoint);
                                DMServer.this.eventNotification(new DeviceManagerEvent(607, mountpoint));
                                boolean unused = DMServer.this.removeFromPartName(volume_id);
                            }
                            String mntName = DMServer.this.getMntName(mountpoint);
                            if (mntName != null && volume_id != null && uuid != null) {
                                DMServer.this.doUmountedEvent(mountpoint);
                                DMServer.this.doRemoveEvent(volume_id);
                                DMServer.this.eventNotification(new DeviceManagerEvent(602, mountpoint));
                                if (DMServer.this.needCallDtvFun() && DMServer.mDtvReady) {
                                    int unused2 = DMServer.this.nativeVoldSendMsg(mountpoint, mntName, 602);
                                }
                            }
                        } catch (Exception e) {
                            Log.v(DMServer.TAG, "Failed to remove media when unplug out disk", e);
                        }
                    }
                }.start();
            }
        }
    }

    private void initRpcReceiver(Context context) {
        IntentFilter msfilter = new IntentFilter();
        msfilter.addAction("android.os.storage.action.VOLUME_STATE_CHANGED");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(final Context context, Intent intent) {
                String access$000 = DMServer.TAG;
                Log.v(access$000, "#########SWTEST#### intent to string:  " + intent.toString());
                if (TextUtils.equals(intent.getAction(), "android.os.storage.action.VOLUME_STATE_CHANGED")) {
                    final int state = intent.getIntExtra("android.os.storage.extra.VOLUME_STATE", -1);
                    if (state == 2 || state == 3) {
                        Log.v(DMServer.TAG, "=============================================================");
                        String access$0002 = DMServer.TAG;
                        Log.v(access$0002, "intent mount volume id = " + intent.getExtra("android.os.storage.extra.VOLUME_ID"));
                        String access$0003 = DMServer.TAG;
                        Log.v(access$0003, "intent mount volume state = " + intent.getExtra("android.os.storage.extra.VOLUME_STATE"));
                        String access$0004 = DMServer.TAG;
                        Log.v(access$0004, "intent mount volume uuid = " + intent.getExtra("android.os.storage.extra.FS_UUID"));
                        Log.v(DMServer.TAG, "Unmounted=0, Checking=1,Mounted=2,MountedRO=3,Format=4,Eject=5,Unmounted=6,Remove=7,BadRemove=8");
                        Log.v(DMServer.TAG, "=============================================================");
                        final Intent msintent = intent;
                        new Thread("DMServer## msfilter_mount_thread") {
                            /* JADX WARNING: Can't fix incorrect switch cases order */
                            /* JADX WARNING: Code restructure failed: missing block: B:42:0x011e, code lost:
                                r4 = 65535;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:43:0x011f, code lost:
                                switch(r4) {
                                    case 0: goto L_0x012b;
                                    case 1: goto L_0x012b;
                                    case 2: goto L_0x0129;
                                    case 3: goto L_0x0129;
                                    case 4: goto L_0x0127;
                                    case 5: goto L_0x0125;
                                    case 6: goto L_0x0125;
                                    default: goto L_0x0122;
                                };
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:44:0x0122, code lost:
                                r2 = 1;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:45:0x0123, code lost:
                                r4 = r2;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:46:0x0125, code lost:
                                r2 = 5;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:47:0x0127, code lost:
                                r2 = 7;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:48:0x0129, code lost:
                                r2 = 2;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:49:0x012b, code lost:
                                r2 = 1;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:51:0x0131, code lost:
                                if (r11.getDescription() != null) goto L_0x0147;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:52:0x0133, code lost:
                                r11.fsLabel = r11.buildStorageVolume(r5, r5.getUserId(), false).getDescription(r5);
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:53:0x0147, code lost:
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "devname : " + r15);
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "systemtype : " + r6);
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "major : " + r7);
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "minor : " + r5);
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "type : " + r4);
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "getDescription : " + r11.getDescription());
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "getPath : " + r11.getPath());
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "sysPath : " + r11.getDisk().sysPath);
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "diskId : " + r11.getDisk().getId());
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:54:0x023d, code lost:
                                if (r15.indexOf("mtpvolume") == -1) goto L_0x0244;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:55:0x023f, code lost:
                                r10 = r15;
                                r8 = r15;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:56:0x0244, code lost:
                                r3 = r11.getDisk().sysPath;
                                r2 = r3.substring(r3.lastIndexOf("/block/") + "/block/".length());
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:57:0x025e, code lost:
                                if ((r5 % 16) != 0) goto L_0x0263;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:58:0x0260, code lost:
                                r8 = r2;
                                r10 = r2;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:59:0x0263, code lost:
                                r22 = r3;
                                r8 = r2 + (r5 % 16);
                                r10 = r2;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:60:0x027b, code lost:
                                r2 = com.mediatek.dm.DMServer.access$000();
                                r3 = new java.lang.StringBuilder();
                                r24 = r4;
                                r3.append("initRpcReceiver drvName : ");
                                r3.append(r10);
                                android.util.Log.v(r2, r3.toString());
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "initRpcReceiver mntName : " + r8);
                                r3 = new android.os.StatFs(r14);
                                r4 = 1;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:62:0x02bc, code lost:
                                if (r3.getTotalBytes() > 0) goto L_0x02d9;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:64:0x02c0, code lost:
                                if (r4 <= 10) goto L_0x02c6;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:65:0x02c2, code lost:
                                r25 = r5;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:66:0x02c6, code lost:
                                java.lang.Thread.sleep(500);
                                r3.restat(r14);
                                r5 = r5;
                                r4 = r4 + 1;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:67:0x02d9, code lost:
                                r25 = r5;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:68:0x02db, code lost:
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "RETRY: get mountpoint size ino: retry = " + r4);
                                r2 = com.mediatek.dm.DMServer.access$000();
                                r5 = new java.lang.StringBuilder();
                                r5.append("getTotalBytes : ");
                                r28 = r7;
                                r5.append(r3.getTotalBytes());
                                android.util.Log.v(r2, r5.toString());
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "getAvailableBytes : " + r3.getAvailableBytes());
                                r7 = r1.this$1.this$0;
                                r20 = r3.getTotalBytes() / android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                                r22 = r3.getAvailableBytes() / android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                                r31 = r3;
                                r32 = r4;
                                r3 = r20;
                                r33 = r2;
                                r20 = r25;
                                r21 = r6;
                                r5 = r22;
                                r22 = r28;
                                r35 = r8;
                                r19 = r9;
                                r17 = r12;
                                r18 = r13;
                                r25 = r14;
                                r26 = r15;
                                r2 = new com.mediatek.dm.MountPoint(r3, r5, r22, r20, 601, r11.getPath().toString(), r0, r11.getDisk().getId(), r11.getDescription(), r35, r24);
                                com.mediatek.dm.DMServer.access$700(r7, r33);
                                r12 = r11;
                                com.mediatek.dm.DMServer.access$800(r1.this$1.this$0, new com.mediatek.dm.Device(r22, r20, 601, r12.getDisk().getId(), r0, r10, r12.getDisk().sysPath));
                                android.util.Log.d(com.mediatek.dm.DMServer.access$000(), "Search volume_id: " + r0 + " from MountPoint List");
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:69:0x03e2, code lost:
                                if (com.mediatek.dm.DMServer.access$900(r1.this$1.this$0, r0) != false) goto L_0x0419;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:70:0x03e4, code lost:
                                android.util.Log.d(com.mediatek.dm.DMServer.access$000(), "Notify connected event for: " + r0);
                                com.mediatek.dm.DMServer.access$1000(r1.this$1.this$0, new com.mediatek.dm.DeviceManagerEvent(701, r12.getPath().toString(), r12.getDisk().sysPath, false));
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:71:0x0419, code lost:
                                com.mediatek.dm.DMServer.access$1000(r1.this$1.this$0, new com.mediatek.dm.DeviceManagerEvent(601, r12.getPath().toString()));
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:72:0x0437, code lost:
                                if (com.mediatek.dm.DMServer.access$1100(r1.this$1.this$0) == false) goto L_0x0482;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:74:0x043e, code lost:
                                if (com.mediatek.dm.DMServer.access$1200() != true) goto L_0x0482;
                             */
                            /* JADX WARNING: Code restructure failed: missing block: B:75:0x0440, code lost:
                                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "#### call nativeVoldSendMsg to dtv_svc: path = " + r12.getPath().toString() + ", volume_id = " + r0);
                                com.mediatek.dm.DMServer.access$1300(r1.this$1.this$0, r12.getPath().toString(), r35, 601);
                             */
                            /* Code decompiled incorrectly, please refer to instructions dump. */
                            public void run() {
                                /*
                                    r37 = this;
                                    r1 = r37
                                    android.content.Intent r0 = r1     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = "android.os.storage.extra.VOLUME_ID"
                                    java.lang.String r0 = r0.getStringExtra(r2)     // Catch:{ Exception -> 0x048d }
                                    android.content.Intent r2 = r1     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = "android.os.storage.extra.FS_UUID"
                                    java.lang.String r2 = r2.getStringExtra(r3)     // Catch:{ Exception -> 0x048d }
                                    r15 = r2
                                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r2.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = "/storage/"
                                    r2.append(r3)     // Catch:{ Exception -> 0x048d }
                                    r2.append(r15)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x048d }
                                    r14 = r2
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = "volume_id : "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r0)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = "uuid : "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r15)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.StorageManager r2 = r2.mStorageManager     // Catch:{ Exception -> 0x048d }
                                    java.util.List r2 = r2.getVolumes()     // Catch:{ Exception -> 0x048d }
                                    r13 = r2
                                    java.util.Iterator r12 = r13.iterator()     // Catch:{ Exception -> 0x048d }
                                L_0x0066:
                                    boolean r2 = r12.hasNext()     // Catch:{ Exception -> 0x048d }
                                    if (r2 == 0) goto L_0x048c
                                    java.lang.Object r2 = r12.next()     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.VolumeInfo r2 = (android.os.storage.VolumeInfo) r2     // Catch:{ Exception -> 0x048d }
                                    r11 = r2
                                    java.lang.String r2 = r11.getFsUuid()     // Catch:{ Exception -> 0x048d }
                                    if (r2 == 0) goto L_0x047a
                                    java.lang.String r2 = r11.getId()     // Catch:{ Exception -> 0x048d }
                                    if (r2 == 0) goto L_0x047a
                                    java.lang.String r2 = r11.getFsUuid()     // Catch:{ Exception -> 0x048d }
                                    boolean r2 = r2.equals(r15)     // Catch:{ Exception -> 0x048d }
                                    if (r2 == 0) goto L_0x047a
                                    java.lang.String r2 = r11.getId()     // Catch:{ Exception -> 0x048d }
                                    boolean r2 = r2.equals(r0)     // Catch:{ Exception -> 0x048d }
                                    if (r2 == 0) goto L_0x047a
                                    int r2 = r11.getState()     // Catch:{ Exception -> 0x048d }
                                    r3 = 2
                                    if (r2 != r3) goto L_0x047a
                                    java.lang.String r2 = ":"
                                    int r2 = r0.indexOf(r2)     // Catch:{ Exception -> 0x048d }
                                    r10 = 1
                                    int r2 = r2 + r10
                                    int r4 = r0.length()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = r0.substring(r2, r4)     // Catch:{ Exception -> 0x048d }
                                    r9 = r2
                                    java.lang.String r2 = ","
                                    java.lang.String[] r2 = r9.split(r2)     // Catch:{ Exception -> 0x048d }
                                    r16 = r2
                                    r8 = 0
                                    r2 = r16[r8]     // Catch:{ Exception -> 0x048d }
                                    int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x048d }
                                    r7 = r2
                                    r2 = r16[r10]     // Catch:{ Exception -> 0x048d }
                                    int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x048d }
                                    r5 = r2
                                    r2 = 0
                                    com.mediatek.dm.DMServer$6 r4 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r4 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = r4.getDeviceInformation(r15)     // Catch:{ Exception -> 0x048d }
                                    r6 = r4
                                    if (r6 != 0) goto L_0x00cf
                                    goto L_0x0066
                                L_0x00cf:
                                    int r4 = r6.hashCode()     // Catch:{ Exception -> 0x048d }
                                    r3 = -1
                                    switch(r4) {
                                        case -877034094: goto L_0x0114;
                                        case 3127858: goto L_0x010a;
                                        case 3127859: goto L_0x0100;
                                        case 3391763: goto L_0x00f6;
                                        case 3616483: goto L_0x00ec;
                                        case 97201976: goto L_0x00e2;
                                        case 110520199: goto L_0x00d8;
                                        default: goto L_0x00d7;
                                    }     // Catch:{ Exception -> 0x048d }
                                L_0x00d7:
                                    goto L_0x011e
                                L_0x00d8:
                                    java.lang.String r4 = "tntfs"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = 3
                                    goto L_0x011f
                                L_0x00e2:
                                    java.lang.String r4 = "fat32"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = r10
                                    goto L_0x011f
                                L_0x00ec:
                                    java.lang.String r4 = "vfat"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = r8
                                    goto L_0x011f
                                L_0x00f6:
                                    java.lang.String r4 = "ntfs"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = 2
                                    goto L_0x011f
                                L_0x0100:
                                    java.lang.String r4 = "ext4"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = 6
                                    goto L_0x011f
                                L_0x010a:
                                    java.lang.String r4 = "ext3"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = 5
                                    goto L_0x011f
                                L_0x0114:
                                    java.lang.String r4 = "texfat"
                                    boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x048d }
                                    if (r4 == 0) goto L_0x011e
                                    r4 = 4
                                    goto L_0x011f
                                L_0x011e:
                                    r4 = r3
                                L_0x011f:
                                    switch(r4) {
                                        case 0: goto L_0x012b;
                                        case 1: goto L_0x012b;
                                        case 2: goto L_0x0129;
                                        case 3: goto L_0x0129;
                                        case 4: goto L_0x0127;
                                        case 5: goto L_0x0125;
                                        case 6: goto L_0x0125;
                                        default: goto L_0x0122;
                                    }     // Catch:{ Exception -> 0x048d }
                                L_0x0122:
                                    r2 = 1
                                L_0x0123:
                                    r4 = r2
                                    goto L_0x012d
                                L_0x0125:
                                    r2 = 5
                                    goto L_0x0123
                                L_0x0127:
                                    r2 = 7
                                    goto L_0x0123
                                L_0x0129:
                                    r2 = 2
                                    goto L_0x0123
                                L_0x012b:
                                    r2 = 1
                                    goto L_0x0123
                                L_0x012d:
                                    java.lang.String r2 = r11.getDescription()     // Catch:{ Exception -> 0x048d }
                                    if (r2 != 0) goto L_0x0147
                                    android.content.Context r2 = r5     // Catch:{ Exception -> 0x048d }
                                    int r2 = r2.getUserId()     // Catch:{ Exception -> 0x048d }
                                    android.content.Context r10 = r5     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.StorageVolume r10 = r11.buildStorageVolume(r10, r2, r8)     // Catch:{ Exception -> 0x048d }
                                    android.content.Context r8 = r5     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r10.getDescription(r8)     // Catch:{ Exception -> 0x048d }
                                    r11.fsLabel = r8     // Catch:{ Exception -> 0x048d }
                                L_0x0147:
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "devname : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    r8.append(r15)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "systemtype : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    r8.append(r6)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "major : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    r8.append(r7)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "minor : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    r8.append(r5)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "type : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    r8.append(r4)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "getDescription : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = r11.getDescription()     // Catch:{ Exception -> 0x048d }
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "getPath : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.io.File r10 = r11.getPath()     // Catch:{ Exception -> 0x048d }
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "sysPath : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.DiskInfo r10 = r11.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = r10.sysPath     // Catch:{ Exception -> 0x048d }
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r8.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "diskId : "
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.DiskInfo r10 = r11.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = r10.getId()     // Catch:{ Exception -> 0x048d }
                                    r8.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r8)     // Catch:{ Exception -> 0x048d }
                                    r2 = 0
                                    r8 = 0
                                    java.lang.String r10 = "mtpvolume"
                                    int r10 = r15.indexOf(r10)     // Catch:{ Exception -> 0x048d }
                                    if (r10 == r3) goto L_0x0244
                                    r2 = r15
                                    r3 = r15
                                    r10 = r2
                                    r8 = r3
                                    goto L_0x027b
                                L_0x0244:
                                    android.os.storage.DiskInfo r3 = r11.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.sysPath     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r10 = "/block/"
                                    int r17 = r3.lastIndexOf(r10)     // Catch:{ Exception -> 0x048d }
                                    int r20 = r10.length()     // Catch:{ Exception -> 0x048d }
                                    r21 = r2
                                    int r2 = r17 + r20
                                    java.lang.String r2 = r3.substring(r2)     // Catch:{ Exception -> 0x048d }
                                    int r20 = r5 % 16
                                    if (r20 != 0) goto L_0x0263
                                    r8 = r2
                                    r10 = r2
                                    goto L_0x027b
                                L_0x0263:
                                    r22 = r3
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    r3.append(r2)     // Catch:{ Exception -> 0x048d }
                                    r23 = r2
                                    int r2 = r5 % 16
                                    r3.append(r2)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    r8 = r2
                                    r10 = r23
                                L_0x027b:
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    r24 = r4
                                    java.lang.String r4 = "initRpcReceiver drvName : "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r10)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = "initRpcReceiver mntName : "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r8)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    r2 = 1
                                    android.os.StatFs r3 = new android.os.StatFs     // Catch:{ Exception -> 0x048d }
                                    r3.<init>(r14)     // Catch:{ Exception -> 0x048d }
                                    r4 = r2
                                L_0x02b4:
                                    long r20 = r3.getTotalBytes()     // Catch:{ Exception -> 0x048d }
                                    r22 = 0
                                    int r2 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1))
                                    if (r2 > 0) goto L_0x02d9
                                    r2 = 10
                                    if (r4 <= r2) goto L_0x02c6
                                    r25 = r5
                                    goto L_0x02db
                                L_0x02c6:
                                    int r4 = r4 + 1
                                    r26 = r4
                                    r25 = r5
                                    r4 = 500(0x1f4, double:2.47E-321)
                                    java.lang.Thread.sleep(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.restat(r14)     // Catch:{ Exception -> 0x048d }
                                    r5 = r25
                                    r4 = r26
                                    goto L_0x02b4
                                L_0x02d9:
                                    r25 = r5
                                L_0x02db:
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r5.<init>()     // Catch:{ Exception -> 0x048d }
                                    r27 = r6
                                    java.lang.String r6 = "RETRY: get mountpoint size ino: retry = "
                                    r5.append(r6)     // Catch:{ Exception -> 0x048d }
                                    r5.append(r4)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r5)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r5.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r6 = "getTotalBytes : "
                                    r5.append(r6)     // Catch:{ Exception -> 0x048d }
                                    r28 = r7
                                    long r6 = r3.getTotalBytes()     // Catch:{ Exception -> 0x048d }
                                    r5.append(r6)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r5)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r5.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r6 = "getAvailableBytes : "
                                    r5.append(r6)     // Catch:{ Exception -> 0x048d }
                                    long r6 = r3.getAvailableBytes()     // Catch:{ Exception -> 0x048d }
                                    r5.append(r6)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r5)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r7 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.MountPoint r5 = new com.mediatek.dm.MountPoint     // Catch:{ Exception -> 0x048d }
                                    long r20 = r3.getTotalBytes()     // Catch:{ Exception -> 0x048d }
                                    r22 = 1024(0x400, double:5.06E-321)
                                    long r20 = r20 / r22
                                    long r29 = r3.getAvailableBytes()     // Catch:{ Exception -> 0x048d }
                                    long r22 = r29 / r22
                                    r17 = 601(0x259, float:8.42E-43)
                                    java.io.File r2 = r11.getPath()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r26 = r2.toString()     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.DiskInfo r2 = r11.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r29 = r2.getId()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r30 = r11.getDescription()     // Catch:{ Exception -> 0x048d }
                                    r2 = r5
                                    r31 = r3
                                    r32 = r4
                                    r3 = r20
                                    r33 = r5
                                    r20 = r25
                                    r21 = r27
                                    r5 = r22
                                    r34 = r7
                                    r22 = r28
                                    r7 = r22
                                    r35 = r8
                                    r8 = r20
                                    r19 = r9
                                    r9 = r17
                                    r23 = r10
                                    r10 = r26
                                    r36 = r11
                                    r11 = r0
                                    r17 = r12
                                    r12 = r29
                                    r18 = r13
                                    r13 = r30
                                    r25 = r14
                                    r14 = r35
                                    r26 = r15
                                    r15 = r24
                                    r2.<init>(r3, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x048d }
                                    r3 = r33
                                    r2 = r34
                                    r2.addMountPointsList(r3)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r10 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.Device r11 = new com.mediatek.dm.Device     // Catch:{ Exception -> 0x048d }
                                    r5 = 601(0x259, float:8.42E-43)
                                    r12 = r36
                                    android.os.storage.DiskInfo r2 = r12.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r6 = r2.getId()     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.DiskInfo r2 = r12.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r9 = r2.sysPath     // Catch:{ Exception -> 0x048d }
                                    r2 = r11
                                    r3 = r22
                                    r4 = r20
                                    r7 = r0
                                    r8 = r23
                                    r2.<init>(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x048d }
                                    r10.addDevicesList(r11)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = "Search volume_id: "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r0)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = " from MountPoint List"
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.d(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    boolean r2 = r2.removeFromPartName(r0)     // Catch:{ Exception -> 0x048d }
                                    if (r2 != 0) goto L_0x0419
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = "Notify connected event for: "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r0)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.d(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DeviceManagerEvent r3 = new com.mediatek.dm.DeviceManagerEvent     // Catch:{ Exception -> 0x048d }
                                    r4 = 701(0x2bd, float:9.82E-43)
                                    java.io.File r5 = r12.getPath()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x048d }
                                    android.os.storage.DiskInfo r6 = r12.getDisk()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r6 = r6.sysPath     // Catch:{ Exception -> 0x048d }
                                    r7 = 0
                                    r3.<init>(r4, r5, r6, r7)     // Catch:{ Exception -> 0x048d }
                                    r2.eventNotification(r3)     // Catch:{ Exception -> 0x048d }
                                L_0x0419:
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DeviceManagerEvent r3 = new com.mediatek.dm.DeviceManagerEvent     // Catch:{ Exception -> 0x048d }
                                    java.io.File r4 = r12.getPath()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x048d }
                                    r5 = 601(0x259, float:8.42E-43)
                                    r3.<init>((int) r5, (java.lang.String) r4)     // Catch:{ Exception -> 0x048d }
                                    r2.eventNotification(r3)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    boolean r2 = r2.needCallDtvFun()     // Catch:{ Exception -> 0x048d }
                                    if (r2 == 0) goto L_0x0482
                                    boolean r2 = com.mediatek.dm.DMServer.mDtvReady     // Catch:{ Exception -> 0x048d }
                                    r3 = 1
                                    if (r2 != r3) goto L_0x0482
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x048d }
                                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d }
                                    r3.<init>()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = "#### call nativeVoldSendMsg to dtv_svc: path = "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    java.io.File r4 = r12.getPath()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x048d }
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r4 = ", volume_id = "
                                    r3.append(r4)     // Catch:{ Exception -> 0x048d }
                                    r3.append(r0)     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    android.util.Log.v(r2, r3)     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer$6 r2 = com.mediatek.dm.DMServer.AnonymousClass6.this     // Catch:{ Exception -> 0x048d }
                                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x048d }
                                    java.io.File r3 = r12.getPath()     // Catch:{ Exception -> 0x048d }
                                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d }
                                    r8 = r35
                                    int unused = r2.nativeVoldSendMsg(r3, r8, r5)     // Catch:{ Exception -> 0x048d }
                                    goto L_0x0482
                                L_0x047a:
                                    r17 = r12
                                    r18 = r13
                                    r25 = r14
                                    r26 = r15
                                L_0x0482:
                                    r12 = r17
                                    r13 = r18
                                    r14 = r25
                                    r15 = r26
                                    goto L_0x0066
                                L_0x048c:
                                    goto L_0x0497
                                L_0x048d:
                                    r0 = move-exception
                                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG
                                    java.lang.String r3 = "Failed to get media information on insertion"
                                    android.util.Log.v(r2, r3, r0)
                                L_0x0497:
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.dm.DMServer.AnonymousClass6.AnonymousClass1.run():void");
                            }
                        }.start();
                    } else if (state == 0 || state == 8 || state == 7 || state == 5) {
                        Log.v(DMServer.TAG, "=============================================================");
                        String access$0005 = DMServer.TAG;
                        Log.v(access$0005, "intent unmount id = " + intent.getStringExtra("android.os.storage.extra.VOLUME_ID"));
                        String access$0006 = DMServer.TAG;
                        Log.v(access$0006, "intent unmount state = " + intent.getExtra("android.os.storage.extra.VOLUME_STATE"));
                        String access$0007 = DMServer.TAG;
                        Log.v(access$0007, "intent unmount uuid = " + intent.getExtra("android.os.storage.extra.FS_UUID"));
                        Log.v(DMServer.TAG, "Unmounted=0, Checking=1,Mounted=2,MountedRO=3,Formatting=4,Ejecting=5,Unmountable=6,Removed=7,BadRemove=8");
                        Log.v(DMServer.TAG, "=============================================================");
                        final Intent umsintent = intent;
                        new Thread("DMServer## msfilter_unmount_thread") {
                            public void run() {
                                try {
                                    String volume_id = umsintent.getStringExtra("android.os.storage.extra.VOLUME_ID");
                                    String uuid = umsintent.getStringExtra("android.os.storage.extra.FS_UUID");
                                    String mountpoint = "/storage/" + uuid;
                                    if (state == 5) {
                                        Log.v(DMServer.TAG, "notify ecject intent: " + mountpoint);
                                        DMServer.this.eventNotification(new DeviceManagerEvent(607, mountpoint));
                                        boolean unused = DMServer.this.removeFromPartName(volume_id);
                                    }
                                    String mntName = DMServer.this.getMntName(mountpoint);
                                    if (mntName != null && volume_id != null && uuid != null) {
                                        DMServer.this.doUmountedEvent(mountpoint);
                                        DMServer.this.doRemoveEvent(volume_id);
                                        DMServer.this.eventNotification(new DeviceManagerEvent(602, mountpoint));
                                        if (DMServer.this.needCallDtvFun() && DMServer.mDtvReady) {
                                            int unused2 = DMServer.this.nativeVoldSendMsg(mountpoint, mntName, 602);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.v(DMServer.TAG, "Failed to remove media when unplug out disk", e);
                                }
                            }
                        }.start();
                    }
                }
            }
        }, msfilter);
    }

    public boolean isVirtualDevice(String isoMountPath) {
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (it.hasNext()) {
                MountPoint mntPoint = it.next();
                if (mntPoint.mMountPoint != null) {
                    if (mntPoint.mMountPoint.equals(isoMountPath)) {
                        if (mntPoint.mVolumeLabel != null) {
                            if (mntPoint.mVolumeLabel.equals("ISOVirtualDevice")) {
                                Log.v(TAG, "It is a Virtual Device. \n");
                                return true;
                            }
                            Log.v(TAG, "It is not a Virtual Device. \n");
                            return false;
                        }
                    }
                }
            }
            Log.v(TAG, "It is not valid isoMountPath, invalid argment. \n");
            return false;
        }
    }

    public boolean isNoitfyPrepareDone() {
        return mBootupDone;
    }

    public void setDMSysProperty(boolean isTrue) {
        if (isTrue) {
            Log.i(TAG, "Set DM system property to 1!\n");
            SystemProperties.set("sys.vold.autoflag", "1");
            return;
        }
        Log.i(TAG, "Set DM system property to 0!\n");
        SystemProperties.set("sys.vold.autoflag", "0");
    }

    public int getDeviceCount() {
        int size;
        synchronized (mDMLock) {
            size = this.mDevices.size();
        }
        return size;
    }

    public ArrayList<MountPoint> getDeviceContent(Device dev) {
        ArrayList<MountPoint> retMntList = null;
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (it.hasNext()) {
                MountPoint mnt = it.next();
                if (dev.mDeviceName != null && dev.mDeviceName.equals(mnt.mDiskName)) {
                    if (retMntList == null) {
                        retMntList = new ArrayList<>();
                    }
                    retMntList.add(mnt);
                }
            }
        }
        return retMntList;
    }

    public ArrayList<Device> getDeviceList() {
        ArrayList<Device> arrayList;
        synchronized (mDMLock) {
            arrayList = this.mDevices;
        }
        return arrayList;
    }

    public int getMountPointCount() {
        int size;
        synchronized (mDMLock) {
            size = this.mMountPoints.size();
        }
        return size;
    }

    public ArrayList<MountPoint> getMountPointList() {
        ArrayList<MountPoint> arrayList;
        Log.v(TAG, "enter getMountPointList");
        synchronized (mDMLock) {
            Log.v(TAG, "enter lock getMountPointList");
            arrayList = this.mMountPoints;
        }
        return arrayList;
    }

    public MountPoint getMountPoint(String path) {
        MountPoint retMntPoint = null;
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MountPoint mnt = it.next();
                if (path != null && path.equals(mnt.mMountPoint)) {
                    retMntPoint = mnt;
                    break;
                }
            }
        }
        return retMntPoint;
    }

    public Device getParentDevice(MountPoint mntpoint) {
        Device retDev = null;
        synchronized (mDMLock) {
            Iterator<Device> it = this.mDevices.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Device dev = it.next();
                if (dev.mPartName != null && dev.mPartName.equals(mntpoint.mDeviceName) && mntpoint.mMajor == dev.mMajor && mntpoint.mMinor == dev.mMinor) {
                    retDev = dev;
                    break;
                }
            }
        }
        return retDev;
    }

    public void umountDevice(String devName) {
        nativeUmount(devName);
    }

    public void umountVol(String partName) {
        nativeUmountVol(partName);
    }

    public void mountVol(String partName) {
        nativeMountVol(partName);
    }

    public void mountVolEx(String partName, String mntPoint) {
        nativeMountVol(partName, mntPoint);
    }

    public void mountISO(String isoFilePath) {
        nativeMountISO(isoFilePath);
    }

    public void mountISOex(String isoFilePath, String isoLabel) {
        nativeMountISOex(isoFilePath, isoLabel);
    }

    public void umountISO(String isoMountPath) {
        nativeUmountISO(isoMountPath);
    }

    /* access modifiers changed from: private */
    public void addMountPointsList(MountPoint mntp) {
        synchronized (mDMLock) {
            if (this.mMountPoints.size() != 0) {
                for (int i = 0; i < this.mMountPoints.size(); i++) {
                    String str = TAG;
                    Log.d(str, "MountPoint : " + this.mMountPoints.get(i).mMountPoint);
                    String str2 = TAG;
                    Log.d(str2, "DeviceName : " + this.mMountPoints.get(i).mDeviceName);
                    String str3 = TAG;
                    Log.d(str3, "DiskName : " + this.mMountPoints.get(i).mDiskName);
                    String str4 = TAG;
                    Log.d(str4, "VolumeLabel : " + this.mMountPoints.get(i).mVolumeLabel);
                    if (mntp.mDeviceName.equals(this.mMountPoints.get(i).mDeviceName)) {
                        String str5 = TAG;
                        Log.e(str5, "Duplication detected on MountPoints list. " + mntp.mDeviceName);
                        this.mMountPoints.remove(i);
                    }
                }
            }
            this.mMountPoints.add(mntp);
        }
    }

    /* access modifiers changed from: private */
    public void addDevicesList(Device dev) {
        synchronized (mDMLock) {
            if (this.mDevices.size() != 0) {
                for (int i = 0; i < this.mDevices.size(); i++) {
                    String str = TAG;
                    Log.d(str, "DeviceName : " + this.mDevices.get(i).mDeviceName + "\n");
                    String str2 = TAG;
                    Log.d(str2, "PartName : " + this.mDevices.get(i).mPartName + "\n");
                    String str3 = TAG;
                    Log.d(str3, "Major : " + this.mDevices.get(i).mMajor + "\n");
                    String str4 = TAG;
                    Log.d(str4, "Minor : " + this.mDevices.get(i).mMinor + "\n");
                    String str5 = TAG;
                    Log.d(str5, "DevicePath : " + this.mDevices.get(i).mDevicePath + "\n");
                    if (dev.mPartName.equals(this.mDevices.get(i).mPartName)) {
                        String str6 = TAG;
                        Log.e(str6, "Duplication detected on Devices list. " + dev.mPartName);
                        this.mDevices.remove(i);
                    }
                }
            }
            this.mDevices.add(dev);
        }
    }

    /* access modifiers changed from: private */
    public String getDeviceInformation(String uuid) {
        String[] fields;
        if (uuid == null) {
            return null;
        }
        String phyMountpoint = "/mnt/media_rw/" + uuid;
        Log.i(TAG, String.format("label = %s, phyMountpoint = %s", new Object[]{uuid, phyMountpoint}));
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/self/mounts"));
            do {
                String readLine = br.readLine();
                String line = readLine;
                if (readLine == null) {
                    return null;
                }
                fields = line.split(" ");
            } while (!fields[1].equals(phyMountpoint));
            return fields[2];
        } catch (FileNotFoundException e) {
            Log.i(TAG, String.format("File not found: /proc/self/mounts", new Object[0]));
        } catch (IOException e2) {
            Log.i(TAG, String.format("IOException", new Object[0]));
        } catch (Exception e3) {
            Log.i(TAG, String.format("Exception", new Object[0]));
        }
    }

    private void printMountPoints() {
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (it.hasNext()) {
                MountPoint mnt = it.next();
                Log.i(TAG, String.format("[MP]mVolumeLabel: %s", new Object[]{mnt.mVolumeLabel}));
                Log.i(TAG, String.format("[MP]mDeviceName: %s", new Object[]{mnt.mDeviceName}));
                Log.i(TAG, String.format("[MP]mDiskName: %s", new Object[]{mnt.mDiskName}));
                Log.i(TAG, String.format("[MP]mMountPoint: %s", new Object[]{mnt.mMountPoint}));
                Log.i(TAG, String.format("[MP]mTotalSize: %d", new Object[]{Long.valueOf(mnt.mTotalSize)}));
                Log.i(TAG, String.format("[MP]mFreeSize: %d", new Object[]{Long.valueOf(mnt.mFreeSize)}));
                Log.i(TAG, String.format("[MP]mMajor: %d", new Object[]{Integer.valueOf(mnt.mMajor)}));
                Log.i(TAG, String.format("[MP]mMinor: %d", new Object[]{Integer.valueOf(mnt.mMinor)}));
            }
        }
    }

    private void printDevices() {
        synchronized (mDMLock) {
            Iterator<Device> it = this.mDevices.iterator();
            while (it.hasNext()) {
                Device dev = it.next();
                Log.i(TAG, String.format("[DEV]mDeviceName: %s", new Object[]{dev.mDeviceName}));
                Log.i(TAG, String.format("[DEV]mDevicePath: %s", new Object[]{dev.mDevicePath}));
                Log.i(TAG, String.format("[DEV]mPartName: %s", new Object[]{dev.mPartName}));
                Log.i(TAG, String.format("[DEV]mStatus: %d", new Object[]{Integer.valueOf(dev.mStatus)}));
                Log.i(TAG, String.format("[DEV]mMajor: %d", new Object[]{Integer.valueOf(dev.mMajor)}));
                Log.i(TAG, String.format("[DEV]mMinor: %d", new Object[]{Integer.valueOf(dev.mMinor)}));
            }
        }
    }

    /* access modifiers changed from: private */
    public void doUmountedEvent(String mountpoint) {
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MountPoint mntPoint = it.next();
                if (mntPoint.mMountPoint != null && mntPoint.mMountPoint.equals(mountpoint)) {
                    Log.i(TAG, String.format("[DM]: Receive Event. mountpoint: %s", new Object[]{mountpoint}));
                    this.mMountPoints.remove(mntPoint);
                    break;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void doRemoveEvent(String deviceName) {
        synchronized (mDMLock) {
            Iterator<Device> it = this.mDevices.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Device dev = it.next();
                if (deviceName != null && deviceName.equals(dev.mPartName)) {
                    Log.i(TAG, String.format("Volume disconnected, volume_id: %s", new Object[]{deviceName}));
                    this.mDevices.remove(dev);
                    break;
                }
            }
        }
    }

    private void nativeUmount(String deviceName) {
        if (this.mSm == null) {
            Log.e(TAG, "nativeUmountVol:: mSn is null.");
            return;
        }
        Log.i(TAG, String.format("Try to execute nativeUmount function: dev_name = %s", new Object[]{deviceName}));
        try {
            synchronized (mDMLock) {
                Iterator<MountPoint> it = this.mMountPoints.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    MountPoint mntPoint = it.next();
                    if (mntPoint.mDeviceName != null && mntPoint.mDeviceName.equals(deviceName) && mntPoint.mStatus == 601) {
                        this.mSm.unmount(mntPoint.mDeviceName);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute nativeUmount function...", e);
        }
    }

    private void nativeUmountVol(String partName) {
        if (this.mSm == null) {
            Log.e(TAG, "nativeUmountVol:: mSm is null.");
            return;
        }
        Log.i(TAG, String.format("Try to execute nativeUmountVol function: part_name = %s", new Object[]{partName}));
        try {
            synchronized (mDMLock) {
                Iterator<MountPoint> it = this.mMountPoints.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    MountPoint mntPoint = it.next();
                    if (mntPoint.mDeviceName != null && mntPoint.mDeviceName.equals(partName) && mntPoint.mStatus == 601) {
                        this.mSm.unmount(mntPoint.mDeviceName);
                        Thread.sleep(MessageType.delayMillis5);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "nativeUmountVol InterruptException.");
        }
    }

    private void nativeMountVol(String partName) {
        if (this.mSm == null) {
            Log.e(TAG, "nativeUmountVol:: mSm is null.");
            return;
        }
        Log.i(TAG, String.format("Try to execute nativeMountVol function: part_name = %s", new Object[]{partName}));
        try {
            VolumeInfo[] volumes = this.mSm.getVolumes(0);
            int length = volumes.length;
            int i = 0;
            while (i < length) {
                VolumeInfo vol = volumes[i];
                Log.i(TAG, String.format("vol.getId = %s", new Object[]{vol.getId()}));
                Log.i(TAG, String.format("vol.getType = %d", new Object[]{Integer.valueOf(vol.getType())}));
                Log.i(TAG, String.format("vol.getState = %d", new Object[]{Integer.valueOf(vol.getState())}));
                Log.i(TAG, String.format("vol.getFsUuid = %s", new Object[]{vol.getFsUuid()}));
                Log.i(TAG, String.format("vol.path = %s", new Object[]{vol.path}));
                Log.i(TAG, String.format("vol.internalPath = %s", new Object[]{vol.internalPath}));
                if (partName == null || !partName.equals(vol.getId()) || vol.getState() == 2 || vol.getState() == 3) {
                    i++;
                } else {
                    Log.i(TAG, String.format("call MountService to mountVolume, path = %s", new Object[]{vol.path}));
                    synchronized (mDMLock) {
                        this.mMountPartName.add(new String(partName));
                    }
                    this.mSm.mount(vol.getId());
                    Thread.sleep(1000);
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "nativeMountVol InterruptException.");
            synchronized (mDMLock) {
                this.mMountPartName.remove(partName);
            }
        }
    }

    private void nativeMountVol(String partName, String mntPoint) {
        String format = String.format("DMvolume mountVol %s %s", new Object[]{partName, mntPoint});
    }

    /* access modifiers changed from: private */
    public VolumeInfo findVolumeInfoWithName(String volumeid) {
        if (volumeid == null) {
            return null;
        }
        try {
            for (VolumeInfo vol : this.mSm.getVolumes(0)) {
                if (vol.getType() == 0 && vol.getId() != null && vol.getId().equals(volumeid)) {
                    return vol;
                }
            }
        } catch (RemoteException e) {
            Log.v(TAG, "findVolumeInfoWithName cannot find VolumeInfo for: " + volumeid);
        }
        return null;
    }

    private void nativeStart() {
        Log.i(TAG, "DMvolume start");
        new Thread("DMServer## nativeStart_mount_thread") {
            /* JADX WARNING: Code restructure failed: missing block: B:115:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:23:0x0096, code lost:
                r0 = 0;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:26:0x0098, code lost:
                if (r0 >= r2.length) goto L_0x031c;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x009a, code lost:
                r4 = r2[r0].getPath();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a7, code lost:
                if (r4.indexOf("emulated") == -1) goto L_0x00b0;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a9, code lost:
                r35 = r0;
                r34 = r2;
                r13 = r3;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:31:0x00bc, code lost:
                if (r2[r0].getState().equals("mounted") == false) goto L_0x02fe;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c4, code lost:
                if (r2[r0].isEmulated() != false) goto L_0x02fe;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c6, code lost:
                r5 = r2[r0].getId();
                r15 = r2[r0].getPath();
                r13 = r5.substring(r5.indexOf(":") + 1, r5.length());
                r21 = r13.split(",");
                r12 = java.lang.Integer.parseInt(r21[r3]);
                r22 = java.lang.Integer.parseInt(r21[1]);
                r10 = r15.substring(r15.lastIndexOf(47) + 1);
                r11 = com.mediatek.dm.DMServer.access$500(r1.this$0, r10);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:35:0x010e, code lost:
                if (r11 != null) goto L_0x0111;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:38:0x0115, code lost:
                switch(r11.hashCode()) {
                    case -877034094: goto L_0x0155;
                    case 3127858: goto L_0x014b;
                    case 3127859: goto L_0x0141;
                    case 3391763: goto L_0x0137;
                    case 3616483: goto L_0x012d;
                    case 97201976: goto L_0x0123;
                    case 110520199: goto L_0x0119;
                    default: goto L_0x0118;
                };
             */
            /* JADX WARNING: Code restructure failed: missing block: B:41:0x011f, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_TNTFS) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:42:0x0121, code lost:
                r8 = 3;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:44:0x0129, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_FAT32) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:45:0x012b, code lost:
                r8 = 1;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:47:0x0133, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_VFAT) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:48:0x0135, code lost:
                r8 = r3;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:50:0x013d, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_NTFS) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:51:0x013f, code lost:
                r8 = 2;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:53:0x0147, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_EXT4) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:54:0x0149, code lost:
                r8 = 6;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:56:0x0151, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_EXT3) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:57:0x0153, code lost:
                r8 = 5;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:59:0x015b, code lost:
                if (r11.equals(com.mediatek.dm.FileSystemType.FS_TEXFAT) == false) goto L_0x015f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:60:0x015d, code lost:
                r8 = 4;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:61:0x015f, code lost:
                r8 = 65535;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:62:0x0160, code lost:
                switch(r8) {
                    case 0: goto L_0x016d;
                    case 1: goto L_0x016d;
                    case 2: goto L_0x016b;
                    case 3: goto L_0x016b;
                    case 4: goto L_0x0169;
                    case 5: goto L_0x0167;
                    case 6: goto L_0x0167;
                    default: goto L_0x0163;
                };
             */
            /* JADX WARNING: Code restructure failed: missing block: B:63:0x0163, code lost:
                r7 = 1;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:64:0x0164, code lost:
                r23 = r7;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:65:0x0167, code lost:
                r7 = 5;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:66:0x0169, code lost:
                r7 = 7;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:67:0x016b, code lost:
                r7 = 2;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:68:0x016d, code lost:
                r7 = 1;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:69:0x016f, code lost:
                r8 = new android.os.StatFs(r15);
                r9 = 1;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:71:0x017e, code lost:
                if (r8.getTotalBytes() > 0) goto L_0x0198;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:73:0x0182, code lost:
                if (r9 <= 10) goto L_0x0188;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:74:0x0184, code lost:
                r24 = r4;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:75:0x0188, code lost:
                r9 = r9 + 1;
                java.lang.Thread.sleep(500);
                r8.restat(r15);
                r4 = r4;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:76:0x0198, code lost:
                r24 = r4;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:77:0x019a, code lost:
                r3 = com.mediatek.dm.DMServer.access$2100(r1.this$0, r5);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:78:0x01a0, code lost:
                if (r3 != null) goto L_0x01aa;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:79:0x01a2, code lost:
                r35 = r0;
                r34 = r2;
                r13 = 0;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:81:0x01b2, code lost:
                if (r10.indexOf("mtpvolume") == -1) goto L_0x01b7;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:82:0x01b4, code lost:
                r4 = r10;
                r6 = r10;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:83:0x01b7, code lost:
                r6 = r3.getDisk().sysPath;
                r4 = r6.substring(r6.lastIndexOf("/block/") + "/block/".length());
             */
            /* JADX WARNING: Code restructure failed: missing block: B:84:0x01d1, code lost:
                if ((r22 % 16) != 0) goto L_0x01d6;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:85:0x01d3, code lost:
                r6 = r4;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:86:0x01d6, code lost:
                r26 = r6;
                r6 = r4 + (r22 % 16);
                r4 = r4;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:87:0x01ee, code lost:
                r7 = com.mediatek.dm.DMServer.access$000();
                r14 = new java.lang.StringBuilder();
                r14.append("nativeStart drvName : ");
                r14.append(r4);
                android.util.Log.v(r7, r14.toString());
                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "nativeStart mntName : " + r6);
                r14 = r1.this$0;
                r30 = r7;
                r29 = r9;
                r28 = r8;
                r31 = r10;
                r32 = r11;
                r33 = r13;
                r35 = r0;
                r34 = r2;
                r0 = r15;
                r7 = new com.mediatek.dm.MountPoint(r8.getTotalBytes() / android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID, r8.getAvailableBytes() / android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID, r12, r22, 601, r15.toString(), r5, r3.getDisk().getId(), r2[r0].getUserLabel(), r6, r23);
                com.mediatek.dm.DMServer.access$700(r14, r30);
                com.mediatek.dm.DMServer.access$800(r1.this$0, new com.mediatek.dm.Device(r12, r22, 601, r3.getDisk().getId(), r5, r4, r3.getDisk().sysPath));
                com.mediatek.dm.DMServer.access$1000(r1.this$0, new com.mediatek.dm.DeviceManagerEvent(601, r0.toString()));
                r13 = 0;
                com.mediatek.dm.DMServer.access$1000(r1.this$0, new com.mediatek.dm.DeviceManagerEvent(701, r0.toString(), r3.getDisk().sysPath, false));
             */
            /* JADX WARNING: Code restructure failed: missing block: B:88:0x02c7, code lost:
                if (com.mediatek.dm.DMServer.access$1100(r1.this$0) == false) goto L_0x0303;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:90:0x02ce, code lost:
                if (com.mediatek.dm.DMServer.access$1200() != true) goto L_0x0303;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:91:0x02d0, code lost:
                android.util.Log.v(com.mediatek.dm.DMServer.access$000(), "#### call nativeVoldSendMsg to dtv_svc: path = " + r0.toString() + ", volume_id = " + r5);
                com.mediatek.dm.DMServer.access$1300(r1.this$0, r0.toString(), r6, 601);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:92:0x02fe, code lost:
                r35 = r0;
                r34 = r2;
                r13 = r3;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:93:0x0303, code lost:
                r0 = r35 + 1;
                r3 = r13;
                r2 = r34;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r37 = this;
                    r1 = r37
                    com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    android.os.storage.IStorageManager r0 = r0.mSm     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r2 = "DMRemoteagent"
                    r3 = 0
                    android.os.storage.StorageVolume[] r0 = r0.getVolumeList(r3, r2, r3)     // Catch:{ Exception -> 0x0312 }
                    r2 = r0
                    if (r2 != 0) goto L_0x001c
                    java.lang.String r0 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r3 = "IN nativeStart: mountservice get volume list failed."
                    android.util.Log.e(r0, r3)     // Catch:{ Exception -> 0x0312 }
                    return
                L_0x001c:
                    java.lang.Object r4 = com.mediatek.dm.DMServer.mDMLock     // Catch:{ Exception -> 0x0312 }
                    monitor-enter(r4)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ all -> 0x030b }
                    java.util.ArrayList r0 = r0.mMountPoints     // Catch:{ all -> 0x030b }
                    int r0 = r0.size()     // Catch:{ all -> 0x030b }
                    if (r0 == 0) goto L_0x0055
                    java.lang.String r0 = com.mediatek.dm.DMServer.TAG     // Catch:{ all -> 0x0050 }
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0050 }
                    r5.<init>()     // Catch:{ all -> 0x0050 }
                    java.lang.String r6 = "IN nativeStart: MountPoints list is not empty. size = "
                    r5.append(r6)     // Catch:{ all -> 0x0050 }
                    com.mediatek.dm.DMServer r6 = com.mediatek.dm.DMServer.this     // Catch:{ all -> 0x0050 }
                    java.util.ArrayList r6 = r6.mMountPoints     // Catch:{ all -> 0x0050 }
                    int r6 = r6.size()     // Catch:{ all -> 0x0050 }
                    r5.append(r6)     // Catch:{ all -> 0x0050 }
                    java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0050 }
                    android.util.Log.e(r0, r5)     // Catch:{ all -> 0x0050 }
                    goto L_0x0055
                L_0x0050:
                    r0 = move-exception
                    r34 = r2
                    goto L_0x030e
                L_0x0055:
                    com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ all -> 0x030b }
                    java.util.ArrayList r0 = r0.mDevices     // Catch:{ all -> 0x030b }
                    int r0 = r0.size()     // Catch:{ all -> 0x030b }
                    if (r0 == 0) goto L_0x0083
                    java.lang.String r0 = com.mediatek.dm.DMServer.TAG     // Catch:{ all -> 0x0050 }
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0050 }
                    r5.<init>()     // Catch:{ all -> 0x0050 }
                    java.lang.String r6 = "IN nativeStart: Devices list is not empty. size = "
                    r5.append(r6)     // Catch:{ all -> 0x0050 }
                    com.mediatek.dm.DMServer r6 = com.mediatek.dm.DMServer.this     // Catch:{ all -> 0x0050 }
                    java.util.ArrayList r6 = r6.mDevices     // Catch:{ all -> 0x0050 }
                    int r6 = r6.size()     // Catch:{ all -> 0x0050 }
                    r5.append(r6)     // Catch:{ all -> 0x0050 }
                    java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0050 }
                    android.util.Log.e(r0, r5)     // Catch:{ all -> 0x0050 }
                L_0x0083:
                    com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ all -> 0x030b }
                    java.util.ArrayList r0 = r0.mMountPoints     // Catch:{ all -> 0x030b }
                    r0.clear()     // Catch:{ all -> 0x030b }
                    com.mediatek.dm.DMServer r0 = com.mediatek.dm.DMServer.this     // Catch:{ all -> 0x030b }
                    java.util.ArrayList r0 = r0.mDevices     // Catch:{ all -> 0x030b }
                    r0.clear()     // Catch:{ all -> 0x030b }
                    monitor-exit(r4)     // Catch:{ all -> 0x030b }
                    r0 = r3
                L_0x0097:
                    int r4 = r2.length     // Catch:{ Exception -> 0x0312 }
                    if (r0 >= r4) goto L_0x030a
                    r4 = r2[r0]     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r4 = r4.getPath()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r5 = "emulated"
                    int r5 = r4.indexOf(r5)     // Catch:{ Exception -> 0x0312 }
                    r6 = -1
                    if (r5 == r6) goto L_0x00b0
                L_0x00a9:
                    r35 = r0
                    r34 = r2
                    r13 = r3
                    goto L_0x0303
                L_0x00b0:
                    r5 = r2[r0]     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r5 = r5.getState()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r7 = "mounted"
                    boolean r5 = r5.equals(r7)     // Catch:{ Exception -> 0x0312 }
                    if (r5 == 0) goto L_0x02fe
                    r5 = r2[r0]     // Catch:{ Exception -> 0x0312 }
                    boolean r5 = r5.isEmulated()     // Catch:{ Exception -> 0x0312 }
                    if (r5 != 0) goto L_0x02fe
                    r5 = r2[r0]     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r5 = r5.getId()     // Catch:{ Exception -> 0x0312 }
                    r7 = r2[r0]     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r7 = r7.getPath()     // Catch:{ Exception -> 0x0312 }
                    r15 = r7
                    java.lang.String r7 = ":"
                    int r7 = r5.indexOf(r7)     // Catch:{ Exception -> 0x0312 }
                    r14 = 1
                    int r7 = r7 + r14
                    int r8 = r5.length()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r7 = r5.substring(r7, r8)     // Catch:{ Exception -> 0x0312 }
                    r13 = r7
                    java.lang.String r7 = ","
                    java.lang.String[] r7 = r13.split(r7)     // Catch:{ Exception -> 0x0312 }
                    r21 = r7
                    r7 = r21[r3]     // Catch:{ Exception -> 0x0312 }
                    int r12 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0312 }
                    r7 = r21[r14]     // Catch:{ Exception -> 0x0312 }
                    int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0312 }
                    r22 = r7
                    r7 = 0
                    r8 = 47
                    int r8 = r15.lastIndexOf(r8)     // Catch:{ Exception -> 0x0312 }
                    int r8 = r8 + r14
                    java.lang.String r8 = r15.substring(r8)     // Catch:{ Exception -> 0x0312 }
                    r10 = r8
                    com.mediatek.dm.DMServer r8 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r8 = r8.getDeviceInformation(r10)     // Catch:{ Exception -> 0x0312 }
                    r11 = r8
                    if (r11 != 0) goto L_0x0111
                    goto L_0x00a9
                L_0x0111:
                    int r8 = r11.hashCode()     // Catch:{ Exception -> 0x0312 }
                    switch(r8) {
                        case -877034094: goto L_0x0155;
                        case 3127858: goto L_0x014b;
                        case 3127859: goto L_0x0141;
                        case 3391763: goto L_0x0137;
                        case 3616483: goto L_0x012d;
                        case 97201976: goto L_0x0123;
                        case 110520199: goto L_0x0119;
                        default: goto L_0x0118;
                    }     // Catch:{ Exception -> 0x0312 }
                L_0x0118:
                    goto L_0x015f
                L_0x0119:
                    java.lang.String r8 = "tntfs"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = 3
                    goto L_0x0160
                L_0x0123:
                    java.lang.String r8 = "fat32"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = r14
                    goto L_0x0160
                L_0x012d:
                    java.lang.String r8 = "vfat"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = r3
                    goto L_0x0160
                L_0x0137:
                    java.lang.String r8 = "ntfs"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = 2
                    goto L_0x0160
                L_0x0141:
                    java.lang.String r8 = "ext4"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = 6
                    goto L_0x0160
                L_0x014b:
                    java.lang.String r8 = "ext3"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = 5
                    goto L_0x0160
                L_0x0155:
                    java.lang.String r8 = "texfat"
                    boolean r8 = r11.equals(r8)     // Catch:{ Exception -> 0x0312 }
                    if (r8 == 0) goto L_0x015f
                    r8 = 4
                    goto L_0x0160
                L_0x015f:
                    r8 = r6
                L_0x0160:
                    switch(r8) {
                        case 0: goto L_0x016d;
                        case 1: goto L_0x016d;
                        case 2: goto L_0x016b;
                        case 3: goto L_0x016b;
                        case 4: goto L_0x0169;
                        case 5: goto L_0x0167;
                        case 6: goto L_0x0167;
                        default: goto L_0x0163;
                    }     // Catch:{ Exception -> 0x0312 }
                L_0x0163:
                    r7 = 1
                L_0x0164:
                    r23 = r7
                    goto L_0x016f
                L_0x0167:
                    r7 = 5
                    goto L_0x0164
                L_0x0169:
                    r7 = 7
                    goto L_0x0164
                L_0x016b:
                    r7 = 2
                    goto L_0x0164
                L_0x016d:
                    r7 = 1
                    goto L_0x0164
                L_0x016f:
                    r7 = 1
                    android.os.StatFs r8 = new android.os.StatFs     // Catch:{ Exception -> 0x0312 }
                    r8.<init>(r15)     // Catch:{ Exception -> 0x0312 }
                    r9 = r7
                L_0x0176:
                    long r16 = r8.getTotalBytes()     // Catch:{ Exception -> 0x0312 }
                    r18 = 0
                    int r7 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
                    if (r7 > 0) goto L_0x0198
                    r7 = 10
                    if (r9 <= r7) goto L_0x0188
                    r24 = r4
                    goto L_0x019a
                L_0x0188:
                    int r9 = r9 + 1
                    r24 = r4
                    r3 = 500(0x1f4, double:2.47E-321)
                    java.lang.Thread.sleep(r3)     // Catch:{ Exception -> 0x0312 }
                    r8.restat(r15)     // Catch:{ Exception -> 0x0312 }
                    r4 = r24
                    r3 = 0
                    goto L_0x0176
                L_0x0198:
                    r24 = r4
                L_0x019a:
                    com.mediatek.dm.DMServer r3 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    android.os.storage.VolumeInfo r3 = r3.findVolumeInfoWithName(r5)     // Catch:{ Exception -> 0x0312 }
                    if (r3 != 0) goto L_0x01aa
                    r35 = r0
                    r34 = r2
                    r13 = 0
                    goto L_0x0303
                L_0x01aa:
                    r4 = 0
                    r7 = 0
                    java.lang.String r14 = "mtpvolume"
                    int r14 = r10.indexOf(r14)     // Catch:{ Exception -> 0x0312 }
                    if (r14 == r6) goto L_0x01b7
                    r4 = r10
                    r6 = r10
                    goto L_0x01ee
                L_0x01b7:
                    android.os.storage.DiskInfo r6 = r3.getDisk()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r6 = r6.sysPath     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r14 = "/block/"
                    int r16 = r6.lastIndexOf(r14)     // Catch:{ Exception -> 0x0312 }
                    int r17 = r14.length()     // Catch:{ Exception -> 0x0312 }
                    r25 = r4
                    int r4 = r16 + r17
                    java.lang.String r4 = r6.substring(r4)     // Catch:{ Exception -> 0x0312 }
                    int r17 = r22 % 16
                    if (r17 != 0) goto L_0x01d6
                    r7 = r4
                    r6 = r7
                    goto L_0x01ee
                L_0x01d6:
                    r26 = r6
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0312 }
                    r6.<init>()     // Catch:{ Exception -> 0x0312 }
                    r6.append(r4)     // Catch:{ Exception -> 0x0312 }
                    r27 = r4
                    int r4 = r22 % 16
                    r6.append(r4)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r4 = r6.toString()     // Catch:{ Exception -> 0x0312 }
                    r6 = r4
                    r4 = r27
                L_0x01ee:
                    java.lang.String r7 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0312 }
                    java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0312 }
                    r14.<init>()     // Catch:{ Exception -> 0x0312 }
                    r28 = r9
                    java.lang.String r9 = "nativeStart drvName : "
                    r14.append(r9)     // Catch:{ Exception -> 0x0312 }
                    r14.append(r4)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r9 = r14.toString()     // Catch:{ Exception -> 0x0312 }
                    android.util.Log.v(r7, r9)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r7 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0312 }
                    java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0312 }
                    r9.<init>()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r14 = "nativeStart mntName : "
                    r9.append(r14)     // Catch:{ Exception -> 0x0312 }
                    r9.append(r6)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0312 }
                    android.util.Log.v(r7, r9)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r14 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.MountPoint r9 = new com.mediatek.dm.MountPoint     // Catch:{ Exception -> 0x0312 }
                    long r16 = r8.getTotalBytes()     // Catch:{ Exception -> 0x0312 }
                    r18 = 1024(0x400, double:5.06E-321)
                    long r16 = r16 / r18
                    long r25 = r8.getAvailableBytes()     // Catch:{ Exception -> 0x0312 }
                    long r18 = r25 / r18
                    r20 = 601(0x259, float:8.42E-43)
                    java.lang.String r25 = r15.toString()     // Catch:{ Exception -> 0x0312 }
                    android.os.storage.DiskInfo r7 = r3.getDisk()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r26 = r7.getId()     // Catch:{ Exception -> 0x0312 }
                    r7 = r2[r0]     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r27 = r7.getUserLabel()     // Catch:{ Exception -> 0x0312 }
                    r7 = r9
                    r30 = r9
                    r29 = r28
                    r28 = r8
                    r8 = r16
                    r31 = r10
                    r32 = r11
                    r10 = r18
                    r33 = r13
                    r13 = r22
                    r35 = r0
                    r34 = r2
                    r2 = r14
                    r0 = 1
                    r14 = r20
                    r0 = r15
                    r15 = r25
                    r16 = r5
                    r17 = r26
                    r18 = r27
                    r19 = r6
                    r20 = r23
                    r7.<init>(r8, r10, r12, r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x0312 }
                    r7 = r30
                    r2.addMountPointsList(r7)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.Device r7 = new com.mediatek.dm.Device     // Catch:{ Exception -> 0x0312 }
                    r16 = 601(0x259, float:8.42E-43)
                    android.os.storage.DiskInfo r8 = r3.getDisk()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r17 = r8.getId()     // Catch:{ Exception -> 0x0312 }
                    android.os.storage.DiskInfo r8 = r3.getDisk()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r8 = r8.sysPath     // Catch:{ Exception -> 0x0312 }
                    r13 = r7
                    r14 = r12
                    r15 = r22
                    r18 = r5
                    r19 = r4
                    r20 = r8
                    r13.<init>(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x0312 }
                    r2.addDevicesList(r7)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DeviceManagerEvent r7 = new com.mediatek.dm.DeviceManagerEvent     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r8 = r0.toString()     // Catch:{ Exception -> 0x0312 }
                    r9 = 601(0x259, float:8.42E-43)
                    r7.<init>((int) r9, (java.lang.String) r8)     // Catch:{ Exception -> 0x0312 }
                    r2.eventNotification(r7)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DeviceManagerEvent r7 = new com.mediatek.dm.DeviceManagerEvent     // Catch:{ Exception -> 0x0312 }
                    r8 = 701(0x2bd, float:9.82E-43)
                    java.lang.String r10 = r0.toString()     // Catch:{ Exception -> 0x0312 }
                    android.os.storage.DiskInfo r11 = r3.getDisk()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r11 = r11.sysPath     // Catch:{ Exception -> 0x0312 }
                    r13 = 0
                    r7.<init>(r8, r10, r11, r13)     // Catch:{ Exception -> 0x0312 }
                    r2.eventNotification(r7)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    boolean r2 = r2.needCallDtvFun()     // Catch:{ Exception -> 0x0312 }
                    if (r2 == 0) goto L_0x0303
                    boolean r2 = com.mediatek.dm.DMServer.mDtvReady     // Catch:{ Exception -> 0x0312 }
                    r7 = 1
                    if (r2 != r7) goto L_0x0303
                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG     // Catch:{ Exception -> 0x0312 }
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0312 }
                    r7.<init>()     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r8 = "#### call nativeVoldSendMsg to dtv_svc: path = "
                    r7.append(r8)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r8 = r0.toString()     // Catch:{ Exception -> 0x0312 }
                    r7.append(r8)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r8 = ", volume_id = "
                    r7.append(r8)     // Catch:{ Exception -> 0x0312 }
                    r7.append(r5)     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0312 }
                    android.util.Log.v(r2, r7)     // Catch:{ Exception -> 0x0312 }
                    com.mediatek.dm.DMServer r2 = com.mediatek.dm.DMServer.this     // Catch:{ Exception -> 0x0312 }
                    java.lang.String r7 = r0.toString()     // Catch:{ Exception -> 0x0312 }
                    int unused = r2.nativeVoldSendMsg(r7, r6, r9)     // Catch:{ Exception -> 0x0312 }
                    goto L_0x0303
                L_0x02fe:
                    r35 = r0
                    r34 = r2
                    r13 = r3
                L_0x0303:
                    int r0 = r35 + 1
                    r3 = r13
                    r2 = r34
                    goto L_0x0097
                L_0x030a:
                    goto L_0x031c
                L_0x030b:
                    r0 = move-exception
                    r34 = r2
                L_0x030e:
                    monitor-exit(r4)     // Catch:{ all -> 0x0310 }
                    throw r0     // Catch:{ Exception -> 0x0312 }
                L_0x0310:
                    r0 = move-exception
                    goto L_0x030e
                L_0x0312:
                    r0 = move-exception
                    java.lang.String r2 = com.mediatek.dm.DMServer.TAG
                    java.lang.String r3 = "Failed to get media information on insertion"
                    android.util.Log.v(r2, r3, r0)
                L_0x031c:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.dm.DMServer.AnonymousClass7.run():void");
            }
        }.start();
    }

    private void nativeEnd() {
        String format = String.format("DMvolume end", new Object[0]);
        try {
            needCallDtvFun();
        } catch (Exception e) {
            Log.i(TAG, "nativeEnd DMNativeDaemonConnectorException");
        }
    }

    public int vold_do_cb(String cmd, String arg, String arg_append) {
        Log.i(TAG, "vold_do_cb enter\n");
        String str = TAG;
        Log.i(str, cmd + " " + arg + " " + arg_append);
        try {
            if (cmd.equals("DTV_START")) {
                if (!mDtvReady) {
                    mDtvReady = true;
                    vold_do_dtv_start_cmd();
                }
            } else if (cmd.equals("mount")) {
                Log.i(TAG, "vold_do_cb:MOUNT cmd from Linux world\n");
            } else if (cmd.equals("umount")) {
                Log.i(TAG, "vold_do_cb:UMOUNT cmd from Linux world\n");
            } else if (cmd.equals("mountISO")) {
                Log.i(TAG, "vold_do_cb:MOUNTISO cmd from Linux world\n");
            } else if (cmd.equals("umountISO")) {
                Log.i(TAG, "vold_do_cb:UMOUNTISO cmd from Linux world\n");
            } else if (cmd.equals("input_add")) {
                Log.i(TAG, "vold_do_cb:input_add cmd from Linux world\n");
                if (!(arg == null || arg_append == null)) {
                    eventNotification(new DeviceManagerEvent(755, arg, arg_append));
                }
            } else if (cmd.equals("input_remove")) {
                Log.i(TAG, "vold_do_cb:input_remove cmd from Linux world\n");
                if (!(arg == null || arg_append == null)) {
                    eventNotification(new DeviceManagerEvent(756, arg, arg_append));
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "vold_do_cb DMNativeDaemonConnectorException");
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void eventNotification(DeviceManagerEvent event) {
        try {
            String str = TAG;
            Log.e(str, "eventNotification. event type:" + event.getType());
            if (mDmHidlService == null) {
                String str2 = TAG;
                Log.e(str2, "eventNotification mDmHidlService is null. event type:" + event.getType());
                return;
            }
            HIDL_DEVICE_MANAGER_EVENT_T hidl_event = DMConvertUtil.toHidlEvent(event);
            String str3 = TAG;
            Log.e(str3, "eventNotification. hidl_event event type:" + hidl_event.i4_type);
            mDmHidlService.mtk_hidl_dm_event_notification(hidl_event);
            String str4 = TAG;
            Log.e(str4, "eventNotification return. hidl_event event type:" + hidl_event.i4_type);
        } catch (Exception e) {
            String str5 = TAG;
            Log.e(str5, "eventNotification exception." + e);
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public int nativeVoldSendMsg(String mountpoint, String devpath, int event) {
        try {
            if (mDmHidlService == null) {
                Log.e(TAG, "nativeVoldSendMsg mDmHidlService is null.");
            }
            String str = TAG;
            Log.v(str, "nativeVoldSendMsg, begin: event type:" + event + ", devpath:" + devpath);
            int ret = mDmHidlService.mtk_hidl_dm_vold_nfy_handler(mountpoint, devpath, event);
            String str2 = TAG;
            Log.v(str2, "nativeVoldSendMsg, end: event type:" + event + ", devpath:" + devpath);
            return ret;
        } catch (Exception e) {
            Log.i(TAG, "nativeVoldSendMsg exception.");
            return -1;
        }
    }

    private void nativeMountISO(String isoFilePath) {
        String format = String.format("DMvolume mountISO \"%s\"", new Object[]{isoFilePath});
    }

    private void nativeUmountISO(String isoMountPath) {
        String format = String.format("DMvolume umountISO \"%s\"", new Object[]{isoMountPath});
    }

    private void nativeMountISOex(String isoFilePath, String isoLabel) {
        String format = String.format("DMvolume mountISOex \"%s\" \"%s\"", new Object[]{isoFilePath, isoLabel});
    }

    private void vold_do_dtv_start_cmd() {
        Log.i(TAG, "do DTV_START cmd: \n");
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (it.hasNext()) {
                MountPoint mnt = it.next();
                Log.i(TAG, String.format("dev name: %s", new Object[]{mnt.mDrvName}));
                Log.i(TAG, String.format("mount point: %s", new Object[]{mnt.mMountPoint}));
                Log.i(TAG, String.format("total size: %d", new Object[]{Long.valueOf(mnt.mTotalSize)}));
                nativeVoldSendMsg(mnt.mMountPoint, mnt.mDrvName, 601);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean needCallDtvFun() {
        if (!SystemProperties.get("ro.vendor.mtk.twoworlds").equals("1") || !SystemProperties.get("ro.vendor.mtk.sys_driver_only").equals("0")) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public String getMntName(String mountpoint) {
        String drvName = null;
        synchronized (mDMLock) {
            Iterator<MountPoint> it = this.mMountPoints.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MountPoint mntPoint = it.next();
                if (mntPoint.mMountPoint.equals(mountpoint) && mntPoint.mStatus == 601) {
                    String str = TAG;
                    Log.v(str, "initRpcReceiver umount dump mDrvName: " + mntPoint.mDrvName);
                    drvName = mntPoint.mDrvName;
                    break;
                }
            }
        }
        return drvName;
    }

    /* access modifiers changed from: private */
    public boolean removeFromPartName(String volume_id) {
        boolean ret = false;
        synchronized (mDMLock) {
            if (this.mMountPartName.contains(volume_id)) {
                String str = TAG;
                Log.v(str, "Clear connected notification flag: " + volume_id);
                this.mMountPartName.remove(volume_id);
                ret = true;
            }
        }
        return ret;
    }
}
