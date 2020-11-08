package com.mediatek.wwtv.setting.widget.view;

import android.os.Parcel;
import android.os.Parcelable;

public class PickerColumn implements Parcelable {
    public static Parcelable.Creator<PickerColumn> CREATOR = new Parcelable.Creator<PickerColumn>() {
        public PickerColumn createFromParcel(Parcel source) {
            return new PickerColumn(source);
        }

        public PickerColumn[] newArray(int size) {
            return new PickerColumn[size];
        }
    };
    private final String[] mItems;

    public PickerColumn(String[] items) {
        if (items != null) {
            this.mItems = items;
            return;
        }
        throw new IllegalArgumentException("items for PickerColumn cannot be null");
    }

    public PickerColumn(Parcel source) {
        this.mItems = new String[source.readInt()];
        source.readStringArray(this.mItems);
    }

    public String[] getItems() {
        return this.mItems;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mItems.length);
        dest.writeStringArray(this.mItems);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : this.mItems) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}
