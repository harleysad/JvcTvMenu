package com.android.tv.settings.util.bluetooth;

import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluetoothNameUtils {
    private static final Pattern COLOR_PATTERN = Pattern.compile("#([0-9a-f]{6})-#([0-9a-f]{6})(p?)(t?)(.*)", 2);
    private static final Pattern NAME_PATTERN = Pattern.compile("\"([0-9]{0,3}) ?(.*)\" \\((.*)\\)", 2);

    public static int getSetupType(String bluetoothName) {
        String typeStr;
        Matcher matcher = NAME_PATTERN.matcher(bluetoothName);
        if (!matcher.matches() || (typeStr = matcher.group(1)) == null) {
            return -1;
        }
        try {
            return Integer.parseInt(typeStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static LedConfiguration getColorConfiguration(String bluetoothName) {
        Matcher matcher = NAME_PATTERN.matcher(bluetoothName);
        if (!matcher.matches()) {
            return null;
        }
        String cs = matcher.group(3);
        if (TextUtils.isEmpty(cs)) {
            return null;
        }
        Matcher cm = COLOR_PATTERN.matcher(cs);
        if (!cm.matches()) {
            return null;
        }
        LedConfiguration config = new LedConfiguration(Integer.parseInt(cm.group(1), 16) | ViewCompat.MEASURED_STATE_MASK, Integer.parseInt(cm.group(2), 16) | ViewCompat.MEASURED_STATE_MASK, "p".equals(cm.group(3)));
        config.isTransient = "t".equals(cm.group(4));
        return config;
    }

    public static boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    private BluetoothNameUtils() {
    }
}
