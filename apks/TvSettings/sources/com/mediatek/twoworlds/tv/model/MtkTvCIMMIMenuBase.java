package com.mediatek.twoworlds.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MtkTvCIMMIMenuBase extends MtkTvCIMMIBase {
    public static final Parcelable.Creator<MtkTvCIMMIMenuBase> CREATOR = new Parcelable.Creator<MtkTvCIMMIMenuBase>() {
        public MtkTvCIMMIMenuBase createFromParcel(Parcel source) {
            return new MtkTvCIMMIMenuBase(source);
        }

        public MtkTvCIMMIMenuBase[] newArray(int size) {
            return new MtkTvCIMMIMenuBase[size];
        }
    };
    private static final String TAG = "MtkTvCIMMIMenu";
    private String bottom;
    private String[] itemList;
    private byte item_nb;
    private String subtitle;
    private String title;

    public MtkTvCIMMIMenuBase(int menuId, byte item_nb2, String title2, String subTitle, String bottom2, String[] itemlist) {
        super(menuId);
        this.item_nb = item_nb2;
        this.title = title2;
        this.subtitle = subTitle;
        this.bottom = bottom2;
        this.itemList = itemlist;
    }

    public MtkTvCIMMIMenuBase() {
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public void setSubtitle(String subtile) {
        this.subtitle = subtile;
    }

    public void setBottom(String bottom2) {
        this.bottom = bottom2;
    }

    public void setItemNum(byte item_nb2) {
        this.item_nb = item_nb2;
    }

    public void setItemList(String[] itemlist) {
        this.itemList = itemlist;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public String getBottom() {
        return this.bottom;
    }

    public byte getItemNum() {
        return this.item_nb;
    }

    public String[] getItemList() {
        return this.itemList;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MMIMenu [menu id=");
        builder.append(this.mmi_id);
        builder.append("\n");
        builder.append("choice_nb=");
        builder.append("\n");
        builder.append(this.item_nb);
        builder.append("title=");
        builder.append(this.title);
        builder.append("\n");
        builder.append("subtitle=");
        builder.append(this.subtitle);
        builder.append("\n");
        builder.append("bottom=");
        builder.append(this.bottom);
        builder.append("\n");
        for (int i = 0; i < this.item_nb; i++) {
            builder.append(", itemList=");
            builder.append(this.itemList[i]);
            builder.append("\n");
        }
        builder.append("\n");
        builder.append("]");
        return builder.toString();
    }

    public int describeContents() {
        return 0;
    }

    private MtkTvCIMMIMenuBase(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mmi_id);
        out.writeByte(this.item_nb);
        out.writeString(this.title);
        out.writeString(this.subtitle);
        out.writeString(this.bottom);
        if (this.itemList == null || this.itemList.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(this.itemList.length);
        }
        out.writeStringArray(this.itemList);
    }

    public void readFromParcel(Parcel in) {
        this.mmi_id = in.readInt();
        this.item_nb = in.readByte();
        this.title = in.readString();
        this.subtitle = in.readString();
        this.bottom = in.readString();
        int len = in.readInt();
        if (len > 0) {
            this.itemList = new String[len];
            in.readStringArray(this.itemList);
        }
    }
}
