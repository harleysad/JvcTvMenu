package vendor.mediatek.tv.mtkdmservice.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class HIDL_DEVICE_MANAGER_EVENT_T {
    public boolean b_connected;
    public int i4_type;
    public String s_dev_name = new String();
    public String s_dev_path = new String();
    public String s_mount_point_path = new String();
    public String s_protocal = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != HIDL_DEVICE_MANAGER_EVENT_T.class) {
            return false;
        }
        HIDL_DEVICE_MANAGER_EVENT_T other = (HIDL_DEVICE_MANAGER_EVENT_T) otherObject;
        if (this.i4_type == other.i4_type && HidlSupport.deepEquals(this.s_mount_point_path, other.s_mount_point_path) && HidlSupport.deepEquals(this.s_dev_name, other.s_dev_name) && HidlSupport.deepEquals(this.s_dev_path, other.s_dev_path) && HidlSupport.deepEquals(this.s_protocal, other.s_protocal) && this.b_connected == other.b_connected) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_type))), Integer.valueOf(HidlSupport.deepHashCode(this.s_mount_point_path)), Integer.valueOf(HidlSupport.deepHashCode(this.s_dev_name)), Integer.valueOf(HidlSupport.deepHashCode(this.s_dev_path)), Integer.valueOf(HidlSupport.deepHashCode(this.s_protocal)), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.b_connected)))});
    }

    public final String toString() {
        return "{" + ".i4_type = " + this.i4_type + ", .s_mount_point_path = " + this.s_mount_point_path + ", .s_dev_name = " + this.s_dev_name + ", .s_dev_path = " + this.s_dev_path + ", .s_protocal = " + this.s_protocal + ", .b_connected = " + this.b_connected + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(80), 0);
    }

    public static final ArrayList<HIDL_DEVICE_MANAGER_EVENT_T> readVectorFromParcel(HwParcel parcel) {
        ArrayList<HIDL_DEVICE_MANAGER_EVENT_T> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 80), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            HIDL_DEVICE_MANAGER_EVENT_T _hidl_vec_element = new HIDL_DEVICE_MANAGER_EVENT_T();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 80));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        HwBlob hwBlob = _hidl_blob;
        this.i4_type = hwBlob.getInt32(_hidl_offset + 0);
        this.s_mount_point_path = hwBlob.getString(_hidl_offset + 8);
        parcel.readEmbeddedBuffer((long) (this.s_mount_point_path.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        this.s_dev_name = hwBlob.getString(_hidl_offset + 24);
        parcel.readEmbeddedBuffer((long) (this.s_dev_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        this.s_dev_path = hwBlob.getString(_hidl_offset + 40);
        parcel.readEmbeddedBuffer((long) (this.s_dev_path.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 40 + 0, false);
        this.s_protocal = hwBlob.getString(_hidl_offset + 56);
        parcel.readEmbeddedBuffer((long) (this.s_protocal.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 56 + 0, false);
        this.b_connected = hwBlob.getBool(_hidl_offset + 72);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(80);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<HIDL_DEVICE_MANAGER_EVENT_T> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.i4_type);
        _hidl_blob.putString(8 + _hidl_offset, this.s_mount_point_path);
        _hidl_blob.putString(24 + _hidl_offset, this.s_dev_name);
        _hidl_blob.putString(40 + _hidl_offset, this.s_dev_path);
        _hidl_blob.putString(56 + _hidl_offset, this.s_protocal);
        _hidl_blob.putBool(72 + _hidl_offset, this.b_connected);
    }
}
