package com.mediatek.wwtv.tvcenter.util;

import android.view.KeyEvent;
import com.mediatek.twoworlds.tv.MtkTvKeyEvent;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.FVP;
import com.mediatek.wwtv.tvcenter.nav.view.TTXMain;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;

public final class KeyDispatch {
    private static final String TAG = "KeyDispatch";
    private static KeyDispatch mKeyDispatch = null;
    private boolean mIsLongpressed;
    private MtkTvKeyEvent mKey;
    private int mPassedAndroidKey;
    private int mPassedAndroidScanKey;

    private KeyDispatch() {
        this.mKey = null;
        this.mPassedAndroidKey = -1;
        this.mPassedAndroidScanKey = -1;
        this.mIsLongpressed = false;
        this.mKey = MtkTvKeyEvent.getInstance();
    }

    public static KeyDispatch getInstance() {
        if (mKeyDispatch == null) {
            mKeyDispatch = new KeyDispatch();
        }
        return mKeyDispatch;
    }

    private boolean isKeyValid(int keyCode) {
        switch (keyCode) {
            case 82:
            case 213:
            case 214:
                return false;
            case KeyMap.KEYCODE_MTKIR_MUTE /*164*/:
                return false;
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                TTXMain ttxMain = (TTXMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TELETEXT);
                return (ttxMain != null && ttxMain.isActive) || ((FVP) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_NATIVE_COMP_ID_FVP)).isFVPActive();
            case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
                MtkLog.d(TAG, "Epg key~");
                if (ComponentsManager.getActiveCompId() == 33554435 && 2 == MarketRegionInfo.getCurrentMarketRegion()) {
                    MtkLog.d(TAG, "Ginga do not handle epg key");
                    return false;
                }
            case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
                TTXMain ttxMain2 = (TTXMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TELETEXT);
                return ttxMain2 != null && ttxMain2.isActive;
            case 10061:
                TTXMain ttx = (TTXMain) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TELETEXT);
                if (33554434 == ComponentsManager.getActiveCompId()) {
                    return true;
                }
                if (ttx == null || !ttx.isActive) {
                    return false;
                }
                MtkLog.d(TAG, "TTX is active,return true");
                return true;
        }
        return true;
    }

    protected static void remove() {
        mKeyDispatch = null;
    }

    public boolean passKeyToNative(int keyCode, KeyEvent event) {
        int dfbkeycode;
        synchronized (KeyDispatch.class) {
            this.mPassedAndroidKey = keyCode;
            this.mPassedAndroidScanKey = getScanCode(this.mPassedAndroidKey, event);
            if (this.mPassedAndroidScanKey != -1) {
                this.mPassedAndroidKey = this.mPassedAndroidScanKey;
            }
        }
        if (event == null) {
            int dfbkeycode2 = this.mKey.androidKeyToDFBkey(this.mPassedAndroidKey);
            return dfbkeycode2 != -1 && this.mKey.sendKeyClick(dfbkeycode2) == 0;
        } else if (!isKeyValid(this.mPassedAndroidKey) || (dfbkeycode = this.mKey.androidKeyToDFBkey(this.mPassedAndroidKey, event)) == -1) {
            return false;
        } else {
            if (event.getRepeatCount() > 0) {
                if (this.mIsLongpressed) {
                    return true;
                }
                this.mIsLongpressed = true;
                MtkLog.v(TAG, "repeat, mIsLongpressed = " + this.mIsLongpressed);
                if (this.mKey.sendKey(0, dfbkeycode) == 0) {
                    return true;
                }
                return false;
            } else if (this.mKey.sendKeyClick(dfbkeycode) == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean passKeyToNative(int updown, int keyCode, KeyEvent event) {
        int dfbkeycode;
        if (updown == 1 && this.mIsLongpressed) {
            this.mIsLongpressed = false;
            MtkLog.v(TAG, "cancel, mIsLongpressed = " + this.mIsLongpressed);
        }
        synchronized (KeyDispatch.class) {
            this.mPassedAndroidKey = keyCode;
            this.mPassedAndroidScanKey = getScanCode(this.mPassedAndroidKey, event);
            if (this.mPassedAndroidScanKey != -1) {
                this.mPassedAndroidKey = this.mPassedAndroidScanKey;
            }
        }
        if (event == null) {
            dfbkeycode = this.mKey.androidKeyToDFBkey(this.mPassedAndroidKey);
        } else if (!isKeyValid(this.mPassedAndroidKey)) {
            return false;
        } else {
            dfbkeycode = this.mKey.androidKeyToDFBkey(this.mPassedAndroidKey, event);
        }
        return dfbkeycode != -1 && this.mKey.sendKey(updown, dfbkeycode) == 0;
    }

    public int getPassedAndroidKey() {
        return this.mPassedAndroidKey;
    }

    public int androidKeyToDFBkey(int keycode) {
        synchronized (KeyDispatch.class) {
            if (this.mPassedAndroidScanKey == KeyMap.getCustomKey(KeyMap.KEYCODE_MTKIR_EPG)) {
                keycode = KeyMap.getCustomKey(KeyMap.KEYCODE_MTKIR_EPG);
            } else if (this.mPassedAndroidScanKey == KeyMap.getCustomKey(KeyMap.KEYCODE_MTKBT_EPG)) {
                keycode = KeyMap.getCustomKey(KeyMap.KEYCODE_MTKBT_EPG);
            }
        }
        return this.mKey.androidKeyToDFBkey(keycode);
    }

    private int getScanCode(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            int value = event == null ? -1 : event.getScanCode() + 10000;
            if (value == KeyMap.getCustomKey(KeyMap.KEYCODE_MTKIR_EPG) || value == KeyMap.getCustomKey(KeyMap.KEYCODE_MTKBT_EPG)) {
                return value;
            }
            return -1;
        }
        return -1;
    }

    public boolean isLongPressed() {
        return this.mIsLongpressed;
    }
}
