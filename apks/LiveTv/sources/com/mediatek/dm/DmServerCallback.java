package com.mediatek.dm;

import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_DEVICE_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_MOUNT_POINT_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.IMtkDmRmtSrvCallback;

/* compiled from: DMServer */
final class DmServerCallback extends IMtkDmRmtSrvCallback.Stub {
    private static final String TAG = "DmServerCallback";
    private DMServer mDmServer = null;

    public DmServerCallback(DMServer dmServer) {
        this.mDmServer = dmServer;
    }

    public int mtk_hidl_dm_get_device_count() throws RemoteException {
        return this.mDmServer.getDeviceCount();
    }

    public int mtk_hidl_dm_vold_cb_fct(String command, String argument, String append) {
        return this.mDmServer.vold_do_cb(command, argument, append);
    }

    public void mtk_hidl_dm_umount_device(String s_dev_name) {
        Log.i(TAG, String.format("mtk_dm_hidl_umount_device: s_dev_name = %s", new Object[]{s_dev_name}));
        try {
            this.mDmServer.umountDevice(s_dev_name);
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute mtk_hidl_dm_umount_device function...", e);
        }
    }

    public void mtk_hidl_dm_mount_iso(String s_iso_file_path) {
        String format = String.format("DMvolume mountISO \"%s\"", new Object[]{s_iso_file_path});
        this.mDmServer.mountISO(s_iso_file_path);
    }

    public void mtk_hidl_dm_mount_iso_ex(String s_iso_file_path, String s_iso_label) {
        String format = String.format("DMvolume mountISOex \"%s\" \"%s\"", new Object[]{s_iso_file_path, s_iso_label});
        this.mDmServer.mountISOex(s_iso_file_path, s_iso_label);
    }

    public void mtk_hidl_dm_umount_iso(String s_iso_file_path) {
        String format = String.format("DMvolume umountISO \"%s\"", new Object[]{s_iso_file_path});
        this.mDmServer.umountISO(s_iso_file_path);
    }

    public void mtk_hidl_dm_mount_vol(String s_part_name) {
        Log.i(TAG, String.format("mtk_hidl_dm_mount_vol: s_part_name = %s", new Object[]{s_part_name}));
        this.mDmServer.mountVol(s_part_name);
    }

    public void mtk_hidl_dm_mount_vol_ex(String s_part_name, String s_mnt_point) {
        String format = String.format("DMvolume mountVol %s %s", new Object[]{s_part_name, s_mnt_point});
        this.mDmServer.mountVolEx(s_part_name, s_mnt_point);
    }

    public void mtk_hidl_dm_umount_vol(String s_part_name) {
        String format = String.format("DMvolume umountVol %s", new Object[]{s_part_name});
        this.mDmServer.umountVol(s_part_name);
    }

    public void mtk_hidl_dm_get_device_content(HIDL_DEVICE_T pt_device, IMtkDmRmtSrvCallback.mtk_hidl_dm_get_device_contentCallback _hidl_cb) throws RemoteException {
        int status = -1;
        ArrayList<MountPoint> retMntList = this.mDmServer.getDeviceContent(DMConvertUtil.hidlToJavaDevice(pt_device));
        if (retMntList != null) {
            status = 0;
        }
        _hidl_cb.onValues(status, DMConvertUtil.toHidlMntList(retMntList));
    }

    public void mtk_hidl_dm_get_device_list(IMtkDmRmtSrvCallback.mtk_hidl_dm_get_device_listCallback _hidl_cb) throws RemoteException {
        int status = -1;
        ArrayList<Device> devList = this.mDmServer.getDeviceList();
        if (devList != null) {
            status = 0;
        }
        _hidl_cb.onValues(status, DMConvertUtil.toHidlDeviceList(devList));
    }

    public int mtk_hidl_dm_get_mount_point_count() throws RemoteException {
        return this.mDmServer.getMountPointCount();
    }

    public void mtk_hidl_dm_get_mount_point_list(IMtkDmRmtSrvCallback.mtk_hidl_dm_get_mount_point_listCallback _hidl_cb) throws RemoteException {
        Log.i(TAG, String.format("mtk_hidl_dm_get_mount_point_list", new Object[0]));
        int status = -1;
        ArrayList<MountPoint> mntList = this.mDmServer.getMountPointList();
        if (mntList != null) {
            status = 0;
        }
        _hidl_cb.onValues(status, DMConvertUtil.toHidlMntList(mntList));
    }

    public void mtk_hidl_dm_get_mount_point(String path, IMtkDmRmtSrvCallback.mtk_hidl_dm_get_mount_pointCallback _hidl_cb) throws RemoteException {
        int status = -1;
        MountPoint mntPoint = this.mDmServer.getMountPoint(path);
        if (mntPoint != null) {
            status = 0;
        }
        _hidl_cb.onValues(status, DMConvertUtil.toHidlMountPoint(mntPoint));
    }

    public void mtk_hidl_dm_get_parent_device(HIDL_MOUNT_POINT_T pt_mount_point, IMtkDmRmtSrvCallback.mtk_hidl_dm_get_parent_deviceCallback _hidl_cb) throws RemoteException {
        int status = -1;
        Device dev = this.mDmServer.getParentDevice(DMConvertUtil.hidlToJavaMnt(pt_mount_point));
        if (dev != null) {
            status = 0;
        }
        _hidl_cb.onValues(status, DMConvertUtil.toHidlDevice(dev));
    }

    public boolean mtk_hidl_dm_is_virtual_device(String s_iso_mount_path) throws RemoteException {
        return this.mDmServer.isVirtualDevice(s_iso_mount_path);
    }
}
