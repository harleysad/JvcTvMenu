package com.mediatek.twoworlds.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MtkTvEventComponentBase implements Parcelable {
    public static final Parcelable.Creator<MtkTvEventComponentBase> CREATOR = new Parcelable.Creator<MtkTvEventComponentBase>() {
        public MtkTvEventComponentBase createFromParcel(Parcel source) {
            return new MtkTvEventComponentBase(source);
        }

        public MtkTvEventComponentBase[] newArray(int size) {
            return new MtkTvEventComponentBase[size];
        }
    };
    private short componentTag;
    private short componentType;
    private short streamContent;

    public short getStreamContent() {
        return this.streamContent;
    }

    public void setStreamContent(short streamContent2) {
        this.streamContent = streamContent2;
    }

    public short getComponentType() {
        return this.componentType;
    }

    public void setComponentType(short componentType2) {
        this.componentType = componentType2;
    }

    public short getComponentTag() {
        return this.componentTag;
    }

    public void setComponentTag(short componentTag2) {
        this.componentTag = componentTag2;
    }

    public MtkTvEventComponentBase() {
    }

    public MtkTvEventComponentBase(short streamContent2, short componentType2, short componentTag2) {
        this.streamContent = streamContent2;
        this.componentType = componentType2;
        this.componentTag = componentTag2;
    }

    public int describeContents() {
        return 0;
    }

    private MtkTvEventComponentBase(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.streamContent);
        out.writeInt(this.componentType);
        out.writeInt(this.componentTag);
    }

    public void readFromParcel(Parcel in) {
        this.streamContent = (short) in.readInt();
        this.componentType = (short) in.readInt();
        this.componentTag = (short) in.readInt();
    }
}
