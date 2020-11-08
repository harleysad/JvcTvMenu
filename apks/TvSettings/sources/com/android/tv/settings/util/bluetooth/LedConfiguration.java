package com.android.tv.settings.util.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;

public class LedConfiguration implements Parcelable {
    public static final Parcelable.Creator<LedConfiguration> CREATOR = new Parcelable.Creator<LedConfiguration>() {
        public LedConfiguration createFromParcel(Parcel source) {
            int color0 = source.readInt();
            int color1 = source.readInt();
            boolean[] bools = new boolean[2];
            source.readBooleanArray(bools);
            LedConfiguration config = new LedConfiguration(color0, color1, bools[0]);
            config.isTransient = bools[1];
            return config;
        }

        public LedConfiguration[] newArray(int size) {
            return new LedConfiguration[size];
        }
    };
    public final int color0;
    public final int color1;
    public boolean isTransient;
    public final boolean pulse;

    public LedConfiguration(int color02, int color12, boolean pulse2) {
        this.color0 = color02;
        this.color1 = color12;
        this.pulse = pulse2;
    }

    public LedConfiguration(LedConfiguration that) {
        this.color0 = that.color0;
        this.color1 = that.color1;
        this.pulse = that.pulse;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LedConfiguration)) {
            return false;
        }
        LedConfiguration that = (LedConfiguration) o;
        if (areColorsEqual(that) && this.pulse == that.pulse && this.isTransient == that.isTransient) {
            return true;
        }
        return false;
    }

    public boolean areColorsEqual(LedConfiguration that) {
        if (that == null) {
            return false;
        }
        if ((this.color0 == that.color0 && this.color1 == that.color1) || (this.color0 == that.color1 && this.color1 == that.color0)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "LedConfiguration(" + getNameString() + ")";
    }

    public String getNameString() {
        Object[] objArr = new Object[4];
        objArr[0] = Integer.valueOf(this.color0 & ViewCompat.MEASURED_SIZE_MASK);
        objArr[1] = Integer.valueOf(this.color1 & ViewCompat.MEASURED_SIZE_MASK);
        objArr[2] = this.pulse ? "p" : "";
        objArr[3] = this.isTransient ? "t" : "";
        return String.format("#%06x-#%06x%s%s", objArr);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.color0);
        parcel.writeInt(this.color1);
        parcel.writeBooleanArray(new boolean[]{this.pulse, this.isTransient});
    }
}
