package com.mediatek.dm;

import android.os.Parcel;
import android.os.Parcelable;

public final class DeviceManagerEvent implements Parcelable {
    public static final Parcelable.Creator<DeviceManagerEvent> CREATOR = new Parcelable.Creator<DeviceManagerEvent>() {
        public DeviceManagerEvent createFromParcel(Parcel in) {
            return new DeviceManagerEvent(in);
        }

        public DeviceManagerEvent[] newArray(int size) {
            return new DeviceManagerEvent[size];
        }
    };
    public static final int accessing = 606;
    public static final int checking = 605;
    public static final int connected = 701;
    public static final int disconnected = 702;
    public static final int ejecting = 607;
    public static final int formating = 603;
    public static final int inputconnected = 755;
    public static final int inputdisconnected = 756;
    public static final int isomountfailed = 650;
    public static final int mounted = 601;
    public static final int parting = 604;
    public static final int productconnected = 753;
    public static final int productdisconnected = 754;
    public static final int umounted = 602;
    public static final int unsupported = 800;
    public static final int wificonnected = 751;
    public static final int wifidisconnected = 752;
    private String mDevName;
    private String mDevPath;
    private String mMountPointPath;
    private int mProtocal;
    private String mProtocalStr;
    private int mType;

    public DeviceManagerEvent(int type, String mountPointPath) {
        this.mType = type;
        this.mMountPointPath = mountPointPath;
        this.mDevPath = "null";
    }

    public DeviceManagerEvent(int type, String dev_name, String protocal) {
        this.mType = type;
        this.mDevName = dev_name;
        this.mProtocal = Integer.parseInt(protocal);
        this.mDevPath = "null";
        this.mProtocalStr = protocal;
    }

    public DeviceManagerEvent(int type, String dev_name, String devicePath, boolean fakeTmp) {
        this.mType = type;
        this.mDevName = dev_name;
        this.mDevPath = devicePath;
    }

    public DeviceManagerEvent(int type, String mnt_path, String dev_name, String devicePath, String protocal) {
        this.mType = type;
        this.mMountPointPath = mnt_path;
        this.mDevName = dev_name;
        this.mDevPath = devicePath;
        this.mProtocalStr = protocal;
        if (protocal != null) {
            try {
                if (protocal.length() != 0) {
                    this.mProtocal = Integer.parseInt(protocal);
                }
            } catch (Exception e) {
            }
        }
    }

    private DeviceManagerEvent(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mType);
        out.writeString(this.mMountPointPath);
        out.writeString(this.mDevPath);
        out.writeString(this.mDevName);
    }

    public void readFromParcel(Parcel in) {
        this.mType = in.readInt();
        this.mMountPointPath = in.readString();
        this.mDevPath = in.readString();
        this.mDevName = in.readString();
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getType() {
        return this.mType;
    }

    public String getMountPointPath() {
        if (this.mType == 751 || this.mType == 752) {
            return null;
        }
        return this.mMountPointPath;
    }

    public String getProductName() {
        if (this.mType == 753 || this.mType == 754) {
            return this.mMountPointPath;
        }
        return null;
    }

    public String getDevicePath() {
        if (this.mType == 753 || this.mType == 754 || this.mType == 701) {
            return this.mDevPath;
        }
        return null;
    }

    public String getWifiInterface() {
        if (this.mType == 751 || this.mType == 752) {
            return this.mMountPointPath;
        }
        return null;
    }

    public String getInputDevName() {
        if (this.mType == 701 || this.mType == 755 || this.mType == 756) {
            return this.mDevName;
        }
        return null;
    }

    public int getInputProtocal() {
        if (this.mType == 755 || this.mType == 756) {
            return this.mProtocal;
        }
        return -1;
    }

    public String getInputProtocalStr() {
        return this.mProtocalStr;
    }

    public int describeContents() {
        return 0;
    }
}
