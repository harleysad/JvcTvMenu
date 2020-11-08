package com.mediatek.dm;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.UnsupportedEncodingException;

public final class MountPoint implements Parcelable {
    public static final Parcelable.Creator<MountPoint> CREATOR = new Parcelable.Creator<MountPoint>() {
        public MountPoint createFromParcel(Parcel in) {
            return new MountPoint(in);
        }

        public MountPoint[] newArray(int size) {
            return new MountPoint[size];
        }
    };
    public String mDeviceName;
    public String mDiskName;
    public String mDrvName;
    public long mFreeSize;
    public FS_TYPE mFsType;
    public int mMajor;
    public int mMinor;
    public String mMountPoint;
    public int mStatus;
    public long mTotalSize;
    public String mVolumeLabel;
    public String mVolumeType;

    public enum FS_TYPE {
        FS_TYPE_INVAL,
        FS_TYPE_FAT,
        FS_TYPE_NTFS,
        FS_TYPE_EXT2,
        FS_TYPE_EXT3,
        FS_TYPE_EXT4,
        FS_TYPE_ISO9660,
        FS_TYPE_EXFAT
    }

    public MountPoint(long totalSize, long freeSize, int major, int minor, int status, String mountPoint, String deviceName, String volumeLabel, int fsType) {
        this.mMountPoint = mountPoint;
        this.mDeviceName = deviceName;
        this.mVolumeLabel = volumeLabel;
        this.mVolumeType = "USB";
        this.mTotalSize = totalSize;
        this.mFreeSize = freeSize;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
        switch (fsType) {
            case 1:
                this.mFsType = FS_TYPE.FS_TYPE_FAT;
                return;
            case 2:
                this.mFsType = FS_TYPE.FS_TYPE_NTFS;
                return;
            case 3:
                this.mFsType = FS_TYPE.FS_TYPE_EXT2;
                return;
            case 4:
                this.mFsType = FS_TYPE.FS_TYPE_EXT3;
                return;
            case 5:
                this.mFsType = FS_TYPE.FS_TYPE_EXT4;
                return;
            case 6:
                this.mFsType = FS_TYPE.FS_TYPE_ISO9660;
                return;
            case 7:
                this.mFsType = FS_TYPE.FS_TYPE_EXFAT;
                return;
            default:
                this.mFsType = FS_TYPE.FS_TYPE_INVAL;
                return;
        }
    }

    public MountPoint(long totalSize, long freeSize, int major, int minor, int status, String mountPoint, String deviceName, String diskName, String volumeLabel, String mntName, int fsType) {
        this.mMountPoint = mountPoint;
        this.mDeviceName = deviceName;
        this.mDiskName = diskName;
        this.mVolumeLabel = volumeLabel;
        this.mDrvName = mntName;
        this.mVolumeType = "USB";
        this.mTotalSize = totalSize;
        this.mFreeSize = freeSize;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
        switch (fsType) {
            case 1:
                this.mFsType = FS_TYPE.FS_TYPE_FAT;
                return;
            case 2:
                this.mFsType = FS_TYPE.FS_TYPE_NTFS;
                return;
            case 3:
                this.mFsType = FS_TYPE.FS_TYPE_EXT2;
                return;
            case 4:
                this.mFsType = FS_TYPE.FS_TYPE_EXT3;
                return;
            case 5:
                this.mFsType = FS_TYPE.FS_TYPE_EXT4;
                return;
            case 6:
                this.mFsType = FS_TYPE.FS_TYPE_ISO9660;
                return;
            case 7:
                this.mFsType = FS_TYPE.FS_TYPE_EXFAT;
                return;
            default:
                this.mFsType = FS_TYPE.FS_TYPE_INVAL;
                return;
        }
    }

    public MountPoint(long totalSize, long freeSize, int major, int minor, int status, String mountPoint, String deviceName, byte[] volumedata, int fsType) {
        this.mMountPoint = mountPoint;
        this.mDeviceName = deviceName;
        this.mVolumeType = "USB";
        this.mTotalSize = totalSize;
        this.mFreeSize = freeSize;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
        if (fsType == 1) {
            try {
                this.mVolumeLabel = new String(new String(volumedata, "cp936").getBytes("utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            this.mVolumeLabel = new String(volumedata);
        }
        switch (fsType) {
            case 1:
                this.mFsType = FS_TYPE.FS_TYPE_FAT;
                return;
            case 2:
                this.mFsType = FS_TYPE.FS_TYPE_NTFS;
                return;
            case 3:
                this.mFsType = FS_TYPE.FS_TYPE_EXT2;
                return;
            case 4:
                this.mFsType = FS_TYPE.FS_TYPE_EXT3;
                return;
            case 5:
                this.mFsType = FS_TYPE.FS_TYPE_EXT4;
                return;
            case 6:
                this.mFsType = FS_TYPE.FS_TYPE_ISO9660;
                return;
            case 7:
                this.mFsType = FS_TYPE.FS_TYPE_EXFAT;
                return;
            default:
                this.mFsType = FS_TYPE.FS_TYPE_INVAL;
                return;
        }
    }

    public MountPoint(long totalSize, long freeSize, int major, int minor, int status, String mountPoint, String deviceName, String diskName, String volumeLabel, String mntName, int fsType, String volumeType) {
        this.mMountPoint = mountPoint;
        this.mDeviceName = deviceName;
        this.mDiskName = diskName;
        this.mVolumeLabel = volumeLabel;
        this.mDrvName = mntName;
        this.mVolumeType = volumeType;
        this.mTotalSize = totalSize;
        this.mFreeSize = freeSize;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
        switch (fsType) {
            case 1:
                this.mFsType = FS_TYPE.FS_TYPE_FAT;
                return;
            case 2:
                this.mFsType = FS_TYPE.FS_TYPE_NTFS;
                return;
            case 3:
                this.mFsType = FS_TYPE.FS_TYPE_EXT2;
                return;
            case 4:
                this.mFsType = FS_TYPE.FS_TYPE_EXT3;
                return;
            case 5:
                this.mFsType = FS_TYPE.FS_TYPE_EXT4;
                return;
            case 6:
                this.mFsType = FS_TYPE.FS_TYPE_ISO9660;
                return;
            case 7:
                this.mFsType = FS_TYPE.FS_TYPE_EXFAT;
                return;
            default:
                this.mFsType = FS_TYPE.FS_TYPE_INVAL;
                return;
        }
    }

    public MountPoint(String volumeType, String mountPoint, String volumeLabel) {
        this.mMountPoint = mountPoint;
        this.mDeviceName = null;
        this.mVolumeLabel = volumeLabel;
        this.mVolumeType = volumeType;
        this.mTotalSize = 0;
        this.mFreeSize = 0;
        this.mMajor = 0;
        this.mMinor = 0;
        this.mStatus = 0;
        this.mFsType = FS_TYPE.FS_TYPE_INVAL;
    }

    private MountPoint(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mMountPoint);
        out.writeString(this.mDeviceName);
        out.writeString(this.mDiskName);
        out.writeString(this.mVolumeLabel);
        out.writeString(this.mVolumeType);
        out.writeString(this.mDrvName);
        out.writeLong(this.mTotalSize);
        out.writeLong(this.mFreeSize);
        out.writeInt(this.mMajor);
        out.writeInt(this.mMinor);
        out.writeInt(this.mStatus);
        out.writeInt(this.mFsType.ordinal());
    }

    public void readFromParcel(Parcel in) {
        this.mMountPoint = in.readString();
        this.mDeviceName = in.readString();
        this.mDiskName = in.readString();
        this.mVolumeLabel = in.readString();
        this.mVolumeType = in.readString();
        this.mDrvName = in.readString();
        this.mTotalSize = in.readLong();
        this.mFreeSize = in.readLong();
        this.mMajor = in.readInt();
        this.mMinor = in.readInt();
        this.mStatus = in.readInt();
        this.mFsType = FS_TYPE.values()[in.readInt()];
    }

    public int describeContents() {
        return 0;
    }
}
