package vendor.mediatek.tv.mtkdmservice.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class HIDL_MOUNT_POINT_T {
    public int e_fs_type;
    public int i4_major;
    public int i4_minor;
    public int i4_status;
    public long i8_free_size;
    public long i8_total_size;
    public String s_device_name = new String();
    public String s_disk_name = new String();
    public String s_drv_name = new String();
    public String s_mount_point = new String();
    public String s_volume_label = new String();
    public String s_volume_type = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != HIDL_MOUNT_POINT_T.class) {
            return false;
        }
        HIDL_MOUNT_POINT_T other = (HIDL_MOUNT_POINT_T) otherObject;
        if (HidlSupport.deepEquals(this.s_mount_point, other.s_mount_point) && HidlSupport.deepEquals(this.s_device_name, other.s_device_name) && HidlSupport.deepEquals(this.s_disk_name, other.s_disk_name) && HidlSupport.deepEquals(this.s_volume_label, other.s_volume_label) && HidlSupport.deepEquals(this.s_volume_type, other.s_volume_type) && HidlSupport.deepEquals(this.s_drv_name, other.s_drv_name) && this.i8_total_size == other.i8_total_size && this.i8_free_size == other.i8_free_size && this.i4_major == other.i4_major && this.i4_minor == other.i4_minor && this.i4_status == other.i4_status && this.e_fs_type == other.e_fs_type) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.s_mount_point)), Integer.valueOf(HidlSupport.deepHashCode(this.s_device_name)), Integer.valueOf(HidlSupport.deepHashCode(this.s_disk_name)), Integer.valueOf(HidlSupport.deepHashCode(this.s_volume_label)), Integer.valueOf(HidlSupport.deepHashCode(this.s_volume_type)), Integer.valueOf(HidlSupport.deepHashCode(this.s_drv_name)), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.i8_total_size))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.i8_free_size))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_major))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_minor))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.i4_status))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.e_fs_type)))});
    }

    public final String toString() {
        return "{" + ".s_mount_point = " + this.s_mount_point + ", .s_device_name = " + this.s_device_name + ", .s_disk_name = " + this.s_disk_name + ", .s_volume_label = " + this.s_volume_label + ", .s_volume_type = " + this.s_volume_type + ", .s_drv_name = " + this.s_drv_name + ", .i8_total_size = " + this.i8_total_size + ", .i8_free_size = " + this.i8_free_size + ", .i4_major = " + this.i4_major + ", .i4_minor = " + this.i4_minor + ", .i4_status = " + this.i4_status + ", .e_fs_type = " + HIDL_FS_TYPE_T.toString(this.e_fs_type) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(128), 0);
    }

    public static final ArrayList<HIDL_MOUNT_POINT_T> readVectorFromParcel(HwParcel parcel) {
        ArrayList<HIDL_MOUNT_POINT_T> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 128), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            HIDL_MOUNT_POINT_T _hidl_vec_element = new HIDL_MOUNT_POINT_T();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 128));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        HwBlob hwBlob = _hidl_blob;
        this.s_mount_point = hwBlob.getString(_hidl_offset + 0);
        parcel.readEmbeddedBuffer((long) (this.s_mount_point.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        this.s_device_name = hwBlob.getString(_hidl_offset + 16);
        parcel.readEmbeddedBuffer((long) (this.s_device_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        this.s_disk_name = hwBlob.getString(_hidl_offset + 32);
        parcel.readEmbeddedBuffer((long) (this.s_disk_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        this.s_volume_label = hwBlob.getString(_hidl_offset + 48);
        parcel.readEmbeddedBuffer((long) (this.s_volume_label.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 48 + 0, false);
        this.s_volume_type = hwBlob.getString(_hidl_offset + 64);
        parcel.readEmbeddedBuffer((long) (this.s_volume_type.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 64 + 0, false);
        this.s_drv_name = hwBlob.getString(_hidl_offset + 80);
        parcel.readEmbeddedBuffer((long) (this.s_drv_name.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 80 + 0, false);
        this.i8_total_size = hwBlob.getInt64(_hidl_offset + 96);
        this.i8_free_size = hwBlob.getInt64(_hidl_offset + 104);
        this.i4_major = hwBlob.getInt32(_hidl_offset + 112);
        this.i4_minor = hwBlob.getInt32(_hidl_offset + 116);
        this.i4_status = hwBlob.getInt32(_hidl_offset + 120);
        this.e_fs_type = hwBlob.getInt32(_hidl_offset + 124);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(128);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<HIDL_MOUNT_POINT_T> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 128);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 128));
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.s_mount_point);
        _hidl_blob.putString(16 + _hidl_offset, this.s_device_name);
        _hidl_blob.putString(32 + _hidl_offset, this.s_disk_name);
        _hidl_blob.putString(48 + _hidl_offset, this.s_volume_label);
        _hidl_blob.putString(64 + _hidl_offset, this.s_volume_type);
        _hidl_blob.putString(80 + _hidl_offset, this.s_drv_name);
        _hidl_blob.putInt64(96 + _hidl_offset, this.i8_total_size);
        _hidl_blob.putInt64(104 + _hidl_offset, this.i8_free_size);
        _hidl_blob.putInt32(112 + _hidl_offset, this.i4_major);
        _hidl_blob.putInt32(116 + _hidl_offset, this.i4_minor);
        _hidl_blob.putInt32(120 + _hidl_offset, this.i4_status);
        _hidl_blob.putInt32(124 + _hidl_offset, this.e_fs_type);
    }
}
