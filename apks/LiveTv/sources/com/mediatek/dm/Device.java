package com.mediatek.dm;

import android.os.Parcel;
import android.os.Parcelable;

public final class Device implements Parcelable {
    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    public String mDeviceName;
    public String mDevicePath;
    public String mDrvName;
    public int mMajor;
    public int mMinor;
    public String mPartName;
    public int mStatus;

    public Device(int major, int minor, int status, String deviceName) {
        this.mDeviceName = deviceName;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
        this.mDevicePath = "null";
    }

    public Device(int major, int minor, int status, String deviceName, String devicePath) {
        this.mDeviceName = deviceName;
        this.mDevicePath = devicePath;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
    }

    public Device(int major, int minor, int status, String deviceName, String partName, String drvName, String devicePath) {
        this.mDeviceName = deviceName;
        this.mDevicePath = devicePath;
        this.mPartName = partName;
        this.mDrvName = drvName;
        this.mMajor = major;
        this.mMinor = minor;
        this.mStatus = status;
    }

    private Device(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mDeviceName);
        out.writeInt(this.mMajor);
        out.writeInt(this.mMinor);
        out.writeInt(this.mStatus);
        out.writeString(this.mDevicePath);
        out.writeString(this.mPartName);
        out.writeString(this.mDrvName);
    }

    public void readFromParcel(Parcel in) {
        this.mDeviceName = in.readString();
        this.mMajor = in.readInt();
        this.mMinor = in.readInt();
        this.mStatus = in.readInt();
        this.mDevicePath = in.readString();
        this.mPartName = in.readString();
        this.mDrvName = in.readString();
    }

    public int describeContents() {
        return 0;
    }
}
