package vendor.mediatek.tv.mtkdmservice.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class HIDL_DEVICE_T {
    public int i4_major;
    public int i4_minor;
    public int i4_status;
    public String s_device_name = new String();
    public String s_device_path = new String();
    public String s_drv_name = new String();
    public String s_part_name = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != HIDL_DEVICE_T.class) {
            return false;
        }
        HIDL_DEVICE_T other = (HIDL_DEVICE_T) otherObject;
        if (HidlSupport.deepEquals(this.s_device_name, other.s_device_name) && HidlSupport.deepEquals(this.s_device_path, other.s_device_path) && HidlSupport.deepEquals(this.s_part_name, other.s_part_name) && HidlSupport.deepEquals(this.s_drv_name, other.s_drv_name) && this.i4_major == other.i4_major && this.i4_minor == other.i4_minor && this.i4_status == other.i4_status) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.s_device_name)), Integer.valueOf(HidlSupport.deepHashCode(this.s_device_path)), Integer.valueOf(HidlSupport.deepHashCode(this.s_part_name)), Integer.valueOf(HidlSupport.deepHashCode(this.s_drv_name)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_major))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_minor))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_status)))});
    }

    public final String toString() {
        return "{" + ".s_device_name = " + this.s_device_name + ", .s_device_path = " + this.s_device_path + ", .s_part_name = " + this.s_part_name + ", .s_drv_name = " + this.s_drv_name + ", .i4_major = " + this.i4_major + ", .i4_minor = " + this.i4_minor + ", .i4_status = " + this.i4_status + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(80), 0);
    }

    public static final ArrayList<HIDL_DEVICE_T> readVectorFromParcel(HwParcel parcel) {
        ArrayList<HIDL_DEVICE_T> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 80), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            HIDL_DEVICE_T _hidl_vec_element = new HIDL_DEVICE_T();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 80));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        HwBlob hwBlob = _hidl_blob;
        this.s_device_name = hwBlob.getString(_hidl_offset + 0);
        parcel.readEmbeddedBuffer((long) (this.s_device_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        this.s_device_path = hwBlob.getString(_hidl_offset + 16);
        parcel.readEmbeddedBuffer((long) (this.s_device_path.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        this.s_part_name = hwBlob.getString(_hidl_offset + 32);
        parcel.readEmbeddedBuffer((long) (this.s_part_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        this.s_drv_name = hwBlob.getString(_hidl_offset + 48);
        parcel.readEmbeddedBuffer((long) (this.s_drv_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 48 + 0, false);
        this.i4_major = hwBlob.getInt32(_hidl_offset + 64);
        this.i4_minor = hwBlob.getInt32(_hidl_offset + 68);
        this.i4_status = hwBlob.getInt32(_hidl_offset + 72);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(80);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<HIDL_DEVICE_T> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 80);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 80));
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.s_device_name);
        _hidl_blob.putString(16 + _hidl_offset, this.s_device_path);
        _hidl_blob.putString(32 + _hidl_offset, this.s_part_name);
        _hidl_blob.putString(48 + _hidl_offset, this.s_drv_name);
        _hidl_blob.putInt32(64 + _hidl_offset, this.i4_major);
        _hidl_blob.putInt32(68 + _hidl_offset, this.i4_minor);
        _hidl_blob.putInt32(72 + _hidl_offset, this.i4_status);
    }
}
