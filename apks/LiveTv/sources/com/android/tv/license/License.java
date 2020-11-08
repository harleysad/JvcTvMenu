package com.android.tv.license;

import android.os.Parcel;
import android.os.Parcelable;

public final class License implements Comparable<License>, Parcelable {
    public static final Parcelable.Creator<License> CREATOR = new Parcelable.Creator<License>() {
        public License createFromParcel(Parcel in) {
            return new License(in);
        }

        public License[] newArray(int size) {
            return new License[size];
        }
    };
    private final String mLibraryName;
    private final int mLicenseLength;
    private final long mLicenseOffset;
    private final String mPath;

    static License create(String libraryName, long licenseOffset, int licenseLength, String path) {
        return new License(libraryName, licenseOffset, licenseLength, path);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mLibraryName);
        dest.writeLong(this.mLicenseOffset);
        dest.writeInt(this.mLicenseLength);
        dest.writeString(this.mPath);
    }

    public int compareTo(License o) {
        return this.mLibraryName.compareToIgnoreCase(o.getLibraryName());
    }

    public String toString() {
        return getLibraryName();
    }

    private License(String libraryName, long licenseOffset, int licenseLength, String path) {
        this.mLibraryName = libraryName;
        this.mLicenseOffset = licenseOffset;
        this.mLicenseLength = licenseLength;
        this.mPath = path;
    }

    private License(Parcel in) {
        this.mLibraryName = in.readString();
        this.mLicenseOffset = in.readLong();
        this.mLicenseLength = in.readInt();
        this.mPath = in.readString();
    }

    /* access modifiers changed from: package-private */
    public String getLibraryName() {
        return this.mLibraryName;
    }

    /* access modifiers changed from: package-private */
    public long getLicenseOffset() {
        return this.mLicenseOffset;
    }

    /* access modifiers changed from: package-private */
    public int getLicenseLength() {
        return this.mLicenseLength;
    }

    public String getPath() {
        return this.mPath;
    }
}
