package com.mediatek.twoworlds.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MtkTvCIMMIEnqBase extends MtkTvCIMMIBase {
    public static final Parcelable.Creator<MtkTvCIMMIEnqBase> CREATOR = new Parcelable.Creator<MtkTvCIMMIEnqBase>() {
        public MtkTvCIMMIEnqBase createFromParcel(Parcel source) {
            return new MtkTvCIMMIEnqBase(source);
        }

        public MtkTvCIMMIEnqBase[] newArray(int size) {
            return new MtkTvCIMMIEnqBase[size];
        }
    };
    private static final String TAG = "MtkTvCIMMIEnq";
    private byte ans_txt_len;
    private byte b_blind_ans;
    private String w2s_text;

    public MtkTvCIMMIEnqBase(int enq_id, byte anwDataLen, byte bBlindAnswe, String text) {
        super(enq_id);
        this.ans_txt_len = anwDataLen;
        this.b_blind_ans = bBlindAnswe;
        this.w2s_text = text;
    }

    public MtkTvCIMMIEnqBase() {
    }

    public void setText(String enq_text) {
        this.w2s_text = enq_text;
    }

    public void setBlindAns(byte bBlindAnswe) {
        this.b_blind_ans = bBlindAnswe;
    }

    public void setAnsTextLen(byte ans_txt_len2) {
        this.ans_txt_len = ans_txt_len2;
    }

    public String getText() {
        return this.w2s_text;
    }

    public boolean getBlindAns() {
        return this.b_blind_ans > 0;
    }

    public byte getAnsTextLen() {
        return this.ans_txt_len;
    }

    public String toString() {
        return "MMIEnq [enq id=" + this.mmi_id + ", text=" + this.w2s_text + ", blind answer=" + this.b_blind_ans + ", answer text length=" + this.ans_txt_len + "]";
    }

    public int describeContents() {
        return 0;
    }

    private MtkTvCIMMIEnqBase(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mmi_id);
        out.writeString(this.w2s_text);
        out.writeByte(this.b_blind_ans);
        out.writeByte(this.ans_txt_len);
    }

    public void readFromParcel(Parcel in) {
        this.mmi_id = in.readInt();
        this.w2s_text = in.readString();
        this.b_blind_ans = in.readByte();
        this.ans_txt_len = in.readByte();
    }
}
