package com.mediatek.dm;

import com.mediatek.dm.MountPoint;
import java.util.ArrayList;
import java.util.Iterator;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_DEVICE_MANAGER_EVENT_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_DEVICE_T;
import vendor.mediatek.tv.mtkdmservice.V1_0.HIDL_MOUNT_POINT_T;

public class DMConvertUtil {
    public static DeviceManagerEvent hidlToJavaEvent(HIDL_DEVICE_MANAGER_EVENT_T hidl_event) {
        return new DeviceManagerEvent(hidl_event.i4_type, hidl_event.s_mount_point_path, hidl_event.s_dev_name, hidl_event.s_dev_path, hidl_event.s_protocal);
    }

    public static HIDL_DEVICE_MANAGER_EVENT_T toHidlEvent(DeviceManagerEvent event) {
        HIDL_DEVICE_MANAGER_EVENT_T hidl_event = new HIDL_DEVICE_MANAGER_EVENT_T();
        if (event == null) {
            return hidl_event;
        }
        try {
            hidl_event.i4_type = event.getType();
            if (event.getMountPointPath() != null) {
                hidl_event.s_mount_point_path = event.getMountPointPath();
            }
            if (event.getInputDevName() != null) {
                hidl_event.s_dev_name = event.getInputDevName();
            }
            if (event.getInputProtocalStr() != null) {
                hidl_event.s_protocal = event.getInputProtocalStr();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hidl_event;
    }

    public static HIDL_DEVICE_T toHidlDevice(Device dev) {
        HIDL_DEVICE_T hidlDev = new HIDL_DEVICE_T();
        if (dev == null) {
            return hidlDev;
        }
        try {
            if (dev.mDeviceName != null) {
                hidlDev.s_device_name = dev.mDeviceName;
            }
            if (dev.mDevicePath != null) {
                hidlDev.s_device_path = dev.mDevicePath;
            }
            if (dev.mDrvName != null) {
                hidlDev.s_drv_name = dev.mDrvName;
            }
            if (dev.mPartName != null) {
                hidlDev.s_part_name = dev.mPartName;
            }
            hidlDev.i4_major = dev.mMajor;
            hidlDev.i4_minor = dev.mMinor;
            hidlDev.i4_status = dev.mStatus;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hidlDev;
    }

    public static HIDL_MOUNT_POINT_T toHidlMountPoint(MountPoint mnt) {
        HIDL_MOUNT_POINT_T hidlMnt = new HIDL_MOUNT_POINT_T();
        if (mnt == null) {
            return hidlMnt;
        }
        try {
            if (mnt.mMountPoint != null) {
                hidlMnt.s_mount_point = mnt.mMountPoint;
            }
            if (mnt.mDeviceName != null) {
                hidlMnt.s_device_name = mnt.mDeviceName;
            }
            if (mnt.mDiskName != null) {
                hidlMnt.s_disk_name = mnt.mDiskName;
            }
            if (mnt.mVolumeLabel != null) {
                hidlMnt.s_volume_label = mnt.mVolumeLabel;
            }
            hidlMnt.s_volume_type = mnt.mVolumeType;
            if (mnt.mDrvName != null) {
                hidlMnt.s_drv_name = mnt.mDrvName;
            }
            hidlMnt.i8_total_size = mnt.mTotalSize;
            hidlMnt.i8_free_size = mnt.mFreeSize;
            hidlMnt.i4_major = mnt.mMajor;
            hidlMnt.i4_minor = mnt.mMinor;
            hidlMnt.i4_status = mnt.mStatus;
            hidlMnt.e_fs_type = fsTypeToInt(mnt.mFsType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hidlMnt;
    }

    public static int fsTypeToInt(MountPoint.FS_TYPE fsType) {
        switch (fsType) {
            case FS_TYPE_FAT:
                return 1;
            case FS_TYPE_NTFS:
                return 2;
            case FS_TYPE_EXT2:
                return 3;
            case FS_TYPE_EXT3:
                return 4;
            case FS_TYPE_EXT4:
                return 5;
            case FS_TYPE_ISO9660:
                return 6;
            case FS_TYPE_EXFAT:
                return 7;
            default:
                return 0;
        }
    }

    public static MountPoint hidlToJavaMnt(HIDL_MOUNT_POINT_T hidlMnt) {
        HIDL_MOUNT_POINT_T hidl_mount_point_t = hidlMnt;
        return new MountPoint(hidl_mount_point_t.i8_total_size, hidl_mount_point_t.i8_free_size, hidl_mount_point_t.i4_major, hidl_mount_point_t.i4_minor, hidl_mount_point_t.i4_status, hidl_mount_point_t.s_mount_point, hidl_mount_point_t.s_device_name, hidl_mount_point_t.s_disk_name, hidl_mount_point_t.s_volume_label, hidl_mount_point_t.s_drv_name, hidl_mount_point_t.e_fs_type, hidl_mount_point_t.s_volume_type);
    }

    public static ArrayList<MountPoint> hidlToJavaMntList(ArrayList<HIDL_MOUNT_POINT_T> hidlMntPointArray) {
        ArrayList<MountPoint> retMntList = new ArrayList<>();
        Iterator<HIDL_MOUNT_POINT_T> it = hidlMntPointArray.iterator();
        while (it.hasNext()) {
            retMntList.add(hidlToJavaMnt(it.next()));
        }
        return retMntList;
    }

    public static Device hidlToJavaDevice(HIDL_DEVICE_T hidlDev) {
        return new Device(hidlDev.i4_major, hidlDev.i4_minor, hidlDev.i4_status, hidlDev.s_device_name, hidlDev.s_part_name, hidlDev.s_drv_name, hidlDev.s_device_path);
    }

    public static ArrayList<Device> hidlToJavaDeviceList(ArrayList<HIDL_DEVICE_T> hidlDeviceArray) {
        ArrayList<Device> retDevList = new ArrayList<>();
        Iterator<HIDL_DEVICE_T> it = hidlDeviceArray.iterator();
        while (it.hasNext()) {
            retDevList.add(hidlToJavaDevice(it.next()));
        }
        return retDevList;
    }

    public static ArrayList<HIDL_MOUNT_POINT_T> toHidlMntList(ArrayList<MountPoint> mntArray) {
        ArrayList<HIDL_MOUNT_POINT_T> retMntList = new ArrayList<>();
        if (mntArray == null) {
            return retMntList;
        }
        Iterator<MountPoint> it = mntArray.iterator();
        while (it.hasNext()) {
            retMntList.add(toHidlMountPoint(it.next()));
        }
        return retMntList;
    }

    public static ArrayList<HIDL_DEVICE_T> toHidlDeviceList(ArrayList<Device> devArray) {
        ArrayList<HIDL_DEVICE_T> retDevList = new ArrayList<>();
        if (devArray == null) {
            return retDevList;
        }
        Iterator<Device> it = devArray.iterator();
        while (it.hasNext()) {
            retDevList.add(toHidlDevice(it.next()));
        }
        return retDevList;
    }
}
