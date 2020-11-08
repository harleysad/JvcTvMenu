package vendor.mediatek.tv.mtkdmservice.V1_0;

import java.util.ArrayList;

public final class HIDL_FS_TYPE_T {
    public static final int HIDL_FS_TYPE_EXFAT = 7;
    public static final int HIDL_FS_TYPE_EXT2 = 3;
    public static final int HIDL_FS_TYPE_EXT3 = 4;
    public static final int HIDL_FS_TYPE_EXT4 = 5;
    public static final int HIDL_FS_TYPE_FAT = 1;
    public static final int HIDL_FS_TYPE_INVAL = 0;
    public static final int HIDL_FS_TYPE_ISO9660 = 6;
    public static final int HIDL_FS_TYPE_NTFS = 2;

    public static final String toString(int o) {
        if (o == 0) {
            return "HIDL_FS_TYPE_INVAL";
        }
        if (o == 1) {
            return "HIDL_FS_TYPE_FAT";
        }
        if (o == 2) {
            return "HIDL_FS_TYPE_NTFS";
        }
        if (o == 3) {
            return "HIDL_FS_TYPE_EXT2";
        }
        if (o == 4) {
            return "HIDL_FS_TYPE_EXT3";
        }
        if (o == 5) {
            return "HIDL_FS_TYPE_EXT4";
        }
        if (o == 6) {
            return "HIDL_FS_TYPE_ISO9660";
        }
        if (o == 7) {
            return "HIDL_FS_TYPE_EXFAT";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add("HIDL_FS_TYPE_INVAL");
        if ((o & 1) == 1) {
            list.add("HIDL_FS_TYPE_FAT");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("HIDL_FS_TYPE_NTFS");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("HIDL_FS_TYPE_EXT2");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("HIDL_FS_TYPE_EXT3");
            flipped |= 4;
        }
        if ((o & 5) == 5) {
            list.add("HIDL_FS_TYPE_EXT4");
            flipped |= 5;
        }
        if ((o & 6) == 6) {
            list.add("HIDL_FS_TYPE_ISO9660");
            flipped |= 6;
        }
        if ((o & 7) == 7) {
            list.add("HIDL_FS_TYPE_EXFAT");
            flipped |= 7;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
