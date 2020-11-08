package com.mediatek.wwtv.tvcenter.util;

import android.view.KeyEvent;
import com.mediatek.twoworlds.tv.MtkTvParserIniBase;
import com.mediatek.twoworlds.tv.model.MtkTvParserIniInfoBase;
import java.util.HashMap;
import java.util.Map;

public class KeyMap {
    public static final int KEYCODE_0 = 7;
    public static final int KEYCODE_1 = 8;
    public static final int KEYCODE_2 = 9;
    public static final int KEYCODE_3 = 10;
    public static final int KEYCODE_4 = 11;
    public static final int KEYCODE_5 = 12;
    public static final int KEYCODE_6 = 13;
    public static final int KEYCODE_7 = 14;
    public static final int KEYCODE_8 = 15;
    public static final int KEYCODE_9 = 16;
    public static final int KEYCODE_BACK = 4;
    public static final int KEYCODE_DPAD_CENTER = 23;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;
    public static final int KEYCODE_DPAD_UP = 19;
    public static final int KEYCODE_HOME = 3;
    public static final int KEYCODE_MENU = 82;
    public static final int KEYCODE_MTKBT_EPG = 10174;
    public static final int KEYCODE_MTKIR_ANGLE = 214;
    public static final int KEYCODE_MTKIR_ASPECT = 10471;
    public static final int KEYCODE_MTKIR_AUDIO = 222;
    public static final int KEYCODE_MTKIR_BLUE = 186;
    public static final int KEYCODE_MTKIR_BLUETOOTH_ENTER = 126;
    public static final int KEYCODE_MTKIR_BLUETOOTH_ENTER_2 = 127;
    public static final int KEYCODE_MTKIR_BLUETOOTH_NEXT = 87;
    public static final int KEYCODE_MTKIR_BLUETOOTH_PRE = 88;
    public static final int KEYCODE_MTKIR_CHDN = 167;
    public static final int KEYCODE_MTKIR_CHUP = 166;
    public static final int KEYCODE_MTKIR_CLOCK = 10066;
    public static final int KEYCODE_MTKIR_EJECT = 93;
    public static final int KEYCODE_MTKIR_EPG = 10365;
    public static final int KEYCODE_MTKIR_FASTFORWARD = 90;
    public static final int KEYCODE_MTKIR_FREEZE = 10467;
    public static final int KEYCODE_MTKIR_GREEN = 184;
    public static final int KEYCODE_MTKIR_GUIDE = 172;
    public static final int KEYCODE_MTKIR_HOLD = 10061;
    public static final int KEYCODE_MTKIR_INDEX = 10060;
    public static final int KEYCODE_MTKIR_INFO = 165;
    public static final int KEYCODE_MTKIR_LIST = 10395;
    public static final int KEYCODE_MTKIR_MIX = 10470;
    public static final int KEYCODE_MTKIR_MTKIR_CC = 175;
    public static final int KEYCODE_MTKIR_MTKIR_SWAP = 10062;
    public static final int KEYCODE_MTKIR_MTKIR_TTX = 233;
    public static final int KEYCODE_MTKIR_MTS = 213;
    public static final int KEYCODE_MTKIR_MTSAUDIO = 213;
    public static final int KEYCODE_MTKIR_MUTE = 164;
    public static final int KEYCODE_MTKIR_NEXT = 87;
    public static final int KEYCODE_MTKIR_PAUSE = 127;
    public static final int KEYCODE_MTKIR_PEFFECT = 251;
    public static final int KEYCODE_MTKIR_PHOTO = 251;
    public static final int KEYCODE_MTKIR_PIPPOP = 171;
    public static final int KEYCODE_MTKIR_PIPPOS = 227;
    public static final int KEYCODE_MTKIR_PIPSIZE = 10065;
    public static final int KEYCODE_MTKIR_PLAY = 126;
    public static final int KEYCODE_MTKIR_PLAYPAUSE = 85;
    public static final int KEYCODE_MTKIR_PRECH = 229;
    public static final int KEYCODE_MTKIR_PREVIOUS = 88;
    public static final int KEYCODE_MTKIR_RECORD = 130;
    public static final int KEYCODE_MTKIR_RED = 183;
    public static final int KEYCODE_MTKIR_REPEAT = 10061;
    public static final int KEYCODE_MTKIR_REVEAL = 10063;
    public static final int KEYCODE_MTKIR_REWIND = 89;
    public static final int KEYCODE_MTKIR_SEFFECT = 222;
    public static final int KEYCODE_MTKIR_SIZE = 10065;
    public static final int KEYCODE_MTKIR_SLASH = 76;
    public static final int KEYCODE_MTKIR_SLEEP = 223;
    public static final int KEYCODE_MTKIR_SOURCE = 178;
    public static final int KEYCODE_MTKIR_STOP = 86;
    public static final int KEYCODE_MTKIR_SUBCODE = 212;
    public static final int KEYCODE_MTKIR_SUBTITLE = 215;
    public static final int KEYCODE_MTKIR_TIMER = 10066;
    public static final int KEYCODE_MTKIR_UPDATE = 10062;
    public static final int KEYCODE_MTKIR_YELLOW = 185;
    public static final int KEYCODE_MTKIR_ZOOM = 255;
    public static final int KEYCODE_PAGE_DOWN = 93;
    public static final int KEYCODE_PAGE_UP = 92;
    public static final int KEYCODE_PERIOD = 56;
    public static final int KEYCODE_POWER = 26;
    public static final int KEYCODE_RHOMBUS = 10471;
    public static final int KEYCODE_RO = 217;
    public static final int KEYCODE_SCANCODE_OFFSET = 10000;
    public static final int KEYCODE_VOLUME_DOWN = 25;
    public static final int KEYCODE_VOLUME_UP = 24;
    public static final int KEYCODE_YEN = 216;
    public static final Map<Integer, Integer> mCustomKeys = new HashMap();

    public static int getKeyCode(KeyEvent event) {
        if (event == null) {
            return getKeyCode(-1, event);
        }
        return getKeyCode(event.getKeyCode(), event);
    }

    public static int getKeyCode(int keyCode, KeyEvent event) {
        if (event == null) {
            return keyCode;
        }
        if (keyCode == 119 || keyCode == 0) {
            return 10000 + event.getScanCode();
        }
        return keyCode;
    }

    public static int getCustomKey(int keyCode) {
        if (mCustomKeys.size() == 0) {
            MtkTvParserIniBase mParserIni = new MtkTvParserIniBase();
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKIR_EPG), Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "EXIT_SCANCODE", 365) + 10000));
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKBT_EPG), Integer.valueOf(10000 + getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "BT", "EXIT_SCANCODE", 174)));
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKIR_MTKIR_TTX), Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "TTX_KEYCODE", KEYCODE_MTKIR_MTKIR_TTX)));
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKIR_INDEX), Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "INDEX_KEYCODE", KEYCODE_MTKIR_INDEX)));
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKIR_MIX), Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "MIX_KEYCODE", KEYCODE_MTKIR_MIX)));
            mCustomKeys.put(10061, Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "HOLD_KEYCODE", 10061)));
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKIR_REVEAL), Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "REVEAL_KEYCODE", KEYCODE_MTKIR_REVEAL)));
            mCustomKeys.put(Integer.valueOf(KEYCODE_MTKIR_SUBCODE), Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "SUBCODE_KEYCODE", 10064)));
            mCustomKeys.put(10065, Integer.valueOf(getIniConfigKeyCode(mParserIni, "/vendor/etc/customer_keymap.ini", "IR", "SIZE_KEYCODE", 10065)));
            String str = "\n";
            for (Integer key : mCustomKeys.keySet()) {
                str = str + "key(" + key.intValue() + ") value(" + mCustomKeys.get(key).intValue() + ")\n";
            }
            MtkLog.e("KeyMap", str);
        }
        Integer value = mCustomKeys.get(Integer.valueOf(keyCode));
        return value != null ? value.intValue() : keyCode;
    }

    private static int getIniConfigKeyCode(MtkTvParserIniBase init, String path, String tag, String key, int def) {
        MtkTvParserIniInfoBase info = init.getIntConfigData(path, tag + ":" + key);
        if (info.getErrorCode() == 0) {
            return info.getIntData() == 0 ? def : info.getIntData();
        }
        return def;
    }
}
