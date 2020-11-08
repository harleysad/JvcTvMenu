package com.mediatek.twoworlds.tv;

import android.util.Log;
import android.view.KeyEvent;

public class MtkTvKeyEventBase {
    public static final String TAG = "MtkTvKeyEvent";
    private static KeyMapReader mKeyMapReader;

    public MtkTvKeyEventBase() {
        if (!TVNativeWrapper.is_emulator()) {
            if (mKeyMapReader == null) {
                try {
                    mKeyMapReader = new KeyMapReader("/system/usr/keylayout/ttxkeymap.ini");
                } catch (Exception e) {
                    e.printStackTrace();
                    mKeyMapReader = null;
                }
            }
            if (mKeyMapReader == null) {
                try {
                    mKeyMapReader = new KeyMapReader("/system/vendor/usr/keylayout/ttxkeymap.ini");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            Log.d(TAG, "KeyMap Reader init successful");
            return;
        }
        Log.d(TAG, "KeyMap Reader init successful (emulator)");
    }

    public int sendKeyClick(int dfb_keycode) {
        Log.d(TAG, "sendKeyClick " + dfb_keycode);
        TVNativeWrapper.SendKeyClick_native(dfb_keycode);
        return 0;
    }

    public int sendKey(int up, int dfb_keycode) {
        if (up != 0 && up != 1) {
            return -1;
        }
        Log.d(TAG, "sendKey " + up + " " + dfb_keycode);
        TVNativeWrapper.SendKey_native(up, dfb_keycode);
        return 0;
    }

    public int sendMouseMove(int abs_x, int abs_y, int max_x, int max_y) {
        if (abs_x < 0 || abs_x >= max_x || abs_y < 0 || abs_y >= max_y || max_x < 0 || max_x > 3840 || max_y < 0 || max_y > 2160) {
            return -1;
        }
        TVNativeWrapper.SendMouseMove_native(max_x, max_y, abs_x, abs_y);
        return 0;
    }

    public int sendMouseButton(int up, int button) {
        if (up != 0 && up != 1) {
            return -1;
        }
        if (button != 1 && button != 2 && button != 4) {
            return -1;
        }
        Log.d(TAG, "sendMouseButton " + up + " " + button);
        TVNativeWrapper.SendMouseButton_native(up, button);
        return 0;
    }

    public int androidKeyToDFBkey(int androidKeyCode) {
        int dfb_key_code = 0;
        if (mKeyMapReader != null) {
            dfb_key_code = mKeyMapReader.getMTKKeyCode(androidKeyCode);
        }
        Log.d(TAG, "androidKeyToDFBkey From " + androidKeyCode + " To " + dfb_key_code);
        return dfb_key_code;
    }

    public int androidKeyToDFBkey(int androidKeyCode, KeyEvent event) {
        int dfb_key_code = 0;
        if (event == null) {
            return -1;
        }
        if (mKeyMapReader != null) {
            dfb_key_code = mKeyMapReader.getMTKKeyCode(androidKeyCode);
        }
        if (event.getDeviceId() == 1 && dfb_key_code != -1) {
            dfb_key_code |= 268435456;
        }
        Log.d(TAG, "androidKeyToDFBkey From " + androidKeyCode + " To " + dfb_key_code);
        return dfb_key_code;
    }
}
