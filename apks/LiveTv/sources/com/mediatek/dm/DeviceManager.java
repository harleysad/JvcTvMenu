package com.mediatek.dm;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_DEVICE_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_MOUNT_POINT_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.IMtkDmCommon;

public class DeviceManager {
    private static final String TAG = "DeviceManager";
    private static DeviceManager dm = null;
    private DMClientDeathRecipient mDeathRecipient = null;
    private DmManagerCallback mDmHidlCallback = null;
    private IMtkDmCommon mDmHidlService = null;
    private ArrayList<DeviceManagerListener> mListeners = new ArrayList<>();

    public static DeviceManager getInstance() {
        synchronized (DeviceManager.class) {
            if (dm == null) {
                Log.d(TAG, "[New] Create DeviceManager");
                dm = new DeviceManager();
            }
        }
        Log.d(TAG, "[Exist]got an DeviceManager instance " + dm);
        return dm;
    }

    private DeviceManager() {
        try {
            this.mDmHidlService = IMtkDmCommon.getService();
            int retry = 0;
            while (this.mDmHidlService == null) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.mDmHidlService = IMtkDmCommon.getService();
                retry++;
            }
            Log.d(TAG, "[5.26-2]jing wait dm hidl service ready retry = " + retry);
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
        if (this.mDmHidlCallback == null) {
            this.mDmHidlCallback = new DmManagerCallback(this);
        }
        if (this.mDeathRecipient == null) {
            this.mDeathRecipient = new DMClientDeathRecipient(this);
        }
        try {
            if (this.mDmHidlService != null) {
                this.mDmHidlService.mtk_hidl_dm_register_device_manager_callback(this.mDmHidlCallback);
                this.mDmHidlService.linkToDeath(this.mDeathRecipient, 11111);
            }
        } catch (RemoteException e3) {
            e3.printStackTrace();
        }
    }

    public void finalize() {
        try {
            if (this.mDmHidlService != null) {
                Log.i(TAG, "finalize called, call mtk_hidl_dm_unregister_device_manager_callback now.\n");
                this.mDmHidlService.mtk_hidl_dm_unregister_device_manager_callback(this.mDmHidlCallback);
                this.mDmHidlService.unlinkToDeath(this.mDeathRecipient);
            }
            this.mDmHidlCallback = null;
            this.mDeathRecipient = null;
            this.mDmHidlService = null;
            dm = null;
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        try {
            super.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void releaseInstance() {
        Log.i(TAG, "releaseInstance.\n");
    }

    public void addListener(DeviceManagerListener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(DeviceManagerListener listener) {
        this.mListeners.remove(listener);
    }

    /* access modifiers changed from: protected */
    public void onDeviceManagerListener(DeviceManagerEvent event) {
        Log.i(TAG, "Notify step 5[end].\n");
        Iterator<DeviceManagerListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            Log.i(TAG, "Notify begin to end.\n");
            it.next().onEvent(event);
            Log.i(TAG, "Notify listener onEvent return.\n");
        }
    }

    public int getDeviceCount() {
        try {
            if (this.mDmHidlService != null) {
                int count = this.mDmHidlService.mtk_hidl_dm_get_device_count();
                Log.i(TAG, "getDeviceCount: count:%d.\n" + count);
                return count;
            }
            Log.e(TAG, "getDeviceCount: mDmHidlService is null.\n");
            return 0;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public ArrayList<MountPoint> getDeviceContent(Device dev) {
        try {
            HIDL_DEVICE_T hidlDev = DMConvertUtil.toHidlDevice(dev);
            if (this.mDmHidlService != null) {
                return DMConvertUtil.hidlToJavaMntList(this.mDmHidlService.mtk_hidl_dm_get_device_content(hidlDev));
            }
            Log.e(TAG, "getDeviceContent: mDmHidlService is null.\n");
            return null;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<Device> getDeviceList() {
        try {
            Log.i(TAG, "getDeviceList.");
            if (this.mDmHidlService != null) {
                return DMConvertUtil.hidlToJavaDeviceList(this.mDmHidlService.mtk_hidl_dm_get_device_list());
            }
            Log.e(TAG, "getDeviceList: mDmHidlService is null.\n");
            return null;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int getMountPointCount() {
        try {
            if (this.mDmHidlService == null) {
                Log.e(TAG, "getMountPointCount: mDmHidlService is null.\n");
                return 0;
            }
            int ret = this.mDmHidlService.mtk_hidl_dm_get_mount_point_count();
            Log.i(TAG, "getMountPointCount, count = %d." + ret);
            return ret;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public ArrayList<MountPoint> getMountPointList() {
        try {
            Log.i(TAG, "getMountPointList.");
            if (this.mDmHidlService == null) {
                Log.e(TAG, "getMountPointList: mDmHidlService is null.\n");
                return null;
            }
            ArrayList<MountPoint> mntList = DMConvertUtil.hidlToJavaMntList(this.mDmHidlService.mtk_hidl_dm_get_mount_point_list());
            Log.i(TAG, "getMountPointList. size: " + mntList.size());
            return mntList;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            Log.i(TAG, "getMountPointList. return null");
            return null;
        }
    }

    private class MountPointCallback implements IMtkDmCommon.mtk_hidl_dm_get_mount_pointCallback {
        public HIDL_MOUNT_POINT_T mHidlMnt;
        public int retStatus;

        private MountPointCallback() {
            this.retStatus = -1;
        }

        public void onValues(int status, HIDL_MOUNT_POINT_T hidlMnt) {
            this.retStatus = status;
            if (status == 0) {
                this.mHidlMnt = hidlMnt;
            } else {
                this.mHidlMnt = null;
            }
        }
    }

    public MountPoint getMountPoint(String path) {
        try {
            MountPointCallback callback = new MountPointCallback();
            if (this.mDmHidlService == null) {
                Log.e(TAG, "getMountPoint: mDmHidlService is null.\n");
                return null;
            }
            this.mDmHidlService.mtk_hidl_dm_get_mount_point(path, callback);
            if (callback.retStatus == 0) {
                return DMConvertUtil.hidlToJavaMnt(callback.mHidlMnt);
            }
            return null;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private class DeviceCallback implements IMtkDmCommon.mtk_hidl_dm_get_parent_deviceCallback {
        public HIDL_DEVICE_T mHidlDev;
        public int retStatus;

        private DeviceCallback() {
            this.retStatus = -1;
        }

        public void onValues(int status, HIDL_DEVICE_T hidlDev) {
            this.retStatus = status;
            if (status == 0) {
                this.mHidlDev = hidlDev;
            } else {
                this.mHidlDev = null;
            }
        }
    }

    public Device getParentDevice(MountPoint mntpoint) {
        try {
            DeviceCallback callback = new DeviceCallback();
            if (this.mDmHidlService == null) {
                Log.e(TAG, "getParentDevice: mDmHidlService is null.\n");
                return null;
            }
            this.mDmHidlService.mtk_hidl_dm_get_parent_device(DMConvertUtil.toHidlMountPoint(mntpoint), callback);
            if (callback.retStatus == 0) {
                return DMConvertUtil.hidlToJavaDevice(callback.mHidlDev);
            }
            return null;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void umountDevice(String devName) {
        if (devName == null) {
            try {
                Log.e(TAG, "umountDevice input is null, return.\n");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (this.mDmHidlService == null) {
            Log.e(TAG, "umountDevice: mDmHidlService is null.\n");
        } else {
            this.mDmHidlService.mtk_hidl_dm_umount_device(devName);
        }
    }

    public void umountVol(String partName) {
        if (partName == null) {
            try {
                Log.e(TAG, "umountVol input is null, return.\n");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (this.mDmHidlService == null) {
            Log.e(TAG, "umountDevice: mDmHidlService is null.\n");
        } else {
            this.mDmHidlService.mtk_hidl_dm_umount_volume(partName);
        }
    }

    public int mountVol(String partName) {
        if (partName == null) {
            try {
                Log.e(TAG, "mountVol input is null, return -1.\n");
                return -1;
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return -1;
            }
        } else if (this.mDmHidlService == null) {
            Log.e(TAG, "mountVol: mDmHidlService is null.\n");
            return -1;
        } else {
            this.mDmHidlService.mtk_hidl_dm_mount_volume(partName);
            return 0;
        }
    }

    public void mountVolEx(String partName, String mntPoint) {
        if (partName == null || mntPoint == null) {
            Log.e(TAG, "mountVolEx input is null, return.\n");
            return;
        }
        try {
            if (this.mDmHidlService == null) {
                Log.e(TAG, "mountVolEx: mDmHidlService is null.\n");
            } else {
                this.mDmHidlService.mtk_hidl_dm_mount_volume_ex(partName, mntPoint);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void mountISO(String isoFilePath) {
        if (isoFilePath == null) {
            try {
                Log.e(TAG, "mountISO input is null, return.\n");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (this.mDmHidlService == null) {
            Log.e(TAG, "mountISO: mDmHidlService is null.\n");
        } else {
            this.mDmHidlService.mtk_hidl_dm_mount_iso(isoFilePath);
        }
    }

    public void mountISOex(String isoFilePath, String isoLabel) {
        if (isoFilePath == null || isoLabel == null) {
            Log.e(TAG, "mountISOex input is null, return.\n");
            return;
        }
        try {
            if (this.mDmHidlService == null) {
                Log.e(TAG, "mountISOex: mDmHidlService is null.\n");
            } else {
                this.mDmHidlService.mtk_hidl_dm_mount_iso_ex(isoFilePath, isoLabel);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void umountISO(String isoMountPath) {
        if (isoMountPath == null) {
            try {
                Log.e(TAG, "umountISO input is null, return.\n");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (this.mDmHidlService == null) {
            Log.e(TAG, "umountISO: mDmHidlService is null.\n");
        } else {
            this.mDmHidlService.mtk_hidl_dm_umount_iso(isoMountPath);
        }
    }

    public int formatVol(String fs_type, String dev_Name) {
        if (fs_type == null || dev_Name == null) {
            Log.e(TAG, "formatVol input is null, return -1.\n");
            return -1;
        }
        try {
            if (this.mDmHidlService != null) {
                return this.mDmHidlService.mtk_hidl_dm_format_volume(fs_type, dev_Name);
            }
            Log.e(TAG, "formatVol: mDmHidlService is null.\n");
            return -1;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public int checkVol(String fs_type, String dev_Name) {
        if (fs_type == null || dev_Name == null) {
            Log.e(TAG, "checkVol input is null, return -1.\n");
            return -1;
        }
        try {
            if (this.mDmHidlService != null) {
                return this.mDmHidlService.mtk_hidl_dm_check_volume(fs_type, dev_Name);
            }
            Log.e(TAG, "checkVol: mDmHidlService is null.\n");
            return -1;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public int partedDisk(String cfg_FilePath, String dev_Name) {
        if (cfg_FilePath == null || dev_Name == null) {
            Log.e(TAG, "partedDisk input is null, return -1.\n");
            return -1;
        }
        try {
            if (this.mDmHidlService != null) {
                return this.mDmHidlService.mtk_hidl_dm_parted_disk(cfg_FilePath, dev_Name);
            }
            Log.e(TAG, "partedDisk: mDmHidlService is null.\n");
            return -1;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public boolean isVirtualDevice(String isoMountPath) {
        if (isoMountPath == null) {
            try {
                Log.e(TAG, "isVirtualDevice input is null, return false.\n");
                return false;
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return false;
            }
        } else if (this.mDmHidlService != null) {
            return this.mDmHidlService.mtk_hidl_dm_is_virtual_device(isoMountPath);
        } else {
            Log.e(TAG, "isVirtualDevice: mDmHidlService is null.\n");
            return false;
        }
    }

    public boolean isNoitfyPrepareDone() {
        try {
            if (this.mDmHidlService != null) {
                return this.mDmHidlService.mtk_hidl_dm_is_notify_prepare_done();
            }
            Log.e(TAG, "isNoitfyPrepareDone: mDmHidlService is null.\n");
            return false;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void setDMSysProperty(boolean isTrue) {
        try {
            if (this.mDmHidlService == null) {
                Log.e(TAG, "setDMSysProperty: mDmHidlService is null.\n");
            } else {
                this.mDmHidlService.mtk_hidl_dm_set_sys_property(isTrue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public IBinder asBinder() {
        return null;
    }

    public void notifyDeviceEvent(DeviceManagerEvent event) throws RemoteException {
        Log.i(TAG, "notifyDeviceEvent.\n");
        onDeviceManagerListener(event);
    }
}
