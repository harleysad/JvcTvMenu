package vendor.mediatek.tv.mtkdmservice.V1_0;

import java.util.ArrayList;

public final class HIDL_EVENT_T {
    public static final int HIDL_ACCESSING = 606;
    public static final int HIDL_CHECKING = 605;
    public static final int HIDL_CONNECTED = 701;
    public static final int HIDL_DISCONNECTED = 702;
    public static final int HIDL_EJECTING = 607;
    public static final int HIDL_FORMATING = 603;
    public static final int HIDL_INPUT_CONNECTED = 755;
    public static final int HIDL_INPUT_DISCONNECTED = 756;
    public static final int HIDL_ISO_MOUNT_FAILED = 650;
    public static final int HIDL_MOUNTED = 601;
    public static final int HIDL_PARTING = 604;
    public static final int HIDL_PRODUCT_CONNECTED = 753;
    public static final int HIDL_PRODUCT_DISCONNECTED = 754;
    public static final int HIDL_UMOUNTED = 602;
    public static final int HIDL_UNSUPPORTED = 800;
    public static final int HIDL_WIFI_CONNECTED = 751;
    public static final int HIDL_WIFI_DISCONNECTED = 752;

    public static final String toString(int o) {
        if (o == 601) {
            return "HIDL_MOUNTED";
        }
        if (o == 602) {
            return "HIDL_UMOUNTED";
        }
        if (o == 603) {
            return "HIDL_FORMATING";
        }
        if (o == 604) {
            return "HIDL_PARTING";
        }
        if (o == 605) {
            return "HIDL_CHECKING";
        }
        if (o == 606) {
            return "HIDL_ACCESSING";
        }
        if (o == 607) {
            return "HIDL_EJECTING";
        }
        if (o == 800) {
            return "HIDL_UNSUPPORTED";
        }
        if (o == 701) {
            return "HIDL_CONNECTED";
        }
        if (o == 702) {
            return "HIDL_DISCONNECTED";
        }
        if (o == 650) {
            return "HIDL_ISO_MOUNT_FAILED";
        }
        if (o == 751) {
            return "HIDL_WIFI_CONNECTED";
        }
        if (o == 752) {
            return "HIDL_WIFI_DISCONNECTED";
        }
        if (o == 753) {
            return "HIDL_PRODUCT_CONNECTED";
        }
        if (o == 754) {
            return "HIDL_PRODUCT_DISCONNECTED";
        }
        if (o == 755) {
            return "HIDL_INPUT_CONNECTED";
        }
        if (o == 756) {
            return "HIDL_INPUT_DISCONNECTED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 601) == 601) {
            list.add("HIDL_MOUNTED");
            flipped = 0 | 601;
        }
        if ((o & 602) == 602) {
            list.add("HIDL_UMOUNTED");
            flipped |= 602;
        }
        if ((o & 603) == 603) {
            list.add("HIDL_FORMATING");
            flipped |= 603;
        }
        if ((o & 604) == 604) {
            list.add("HIDL_PARTING");
            flipped |= 604;
        }
        if ((o & 605) == 605) {
            list.add("HIDL_CHECKING");
            flipped |= 605;
        }
        if ((o & 606) == 606) {
            list.add("HIDL_ACCESSING");
            flipped |= 606;
        }
        if ((o & 607) == 607) {
            list.add("HIDL_EJECTING");
            flipped |= 607;
        }
        if ((o & 800) == 800) {
            list.add("HIDL_UNSUPPORTED");
            flipped |= 800;
        }
        if ((o & 701) == 701) {
            list.add("HIDL_CONNECTED");
            flipped |= 701;
        }
        if ((o & 702) == 702) {
            list.add("HIDL_DISCONNECTED");
            flipped |= 702;
        }
        if ((o & 650) == 650) {
            list.add("HIDL_ISO_MOUNT_FAILED");
            flipped |= 650;
        }
        if ((o & 751) == 751) {
            list.add("HIDL_WIFI_CONNECTED");
            flipped |= 751;
        }
        if ((o & 752) == 752) {
            list.add("HIDL_WIFI_DISCONNECTED");
            flipped |= 752;
        }
        if ((o & 753) == 753) {
            list.add("HIDL_PRODUCT_CONNECTED");
            flipped |= 753;
        }
        if ((o & 754) == 754) {
            list.add("HIDL_PRODUCT_DISCONNECTED");
            flipped |= 754;
        }
        if ((o & 755) == 755) {
            list.add("HIDL_INPUT_CONNECTED");
            flipped |= 755;
        }
        if ((o & 756) == 756) {
            list.add("HIDL_INPUT_DISCONNECTED");
            flipped |= 756;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
