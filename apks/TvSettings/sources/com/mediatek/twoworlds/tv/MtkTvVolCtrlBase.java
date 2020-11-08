package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;

public class MtkTvVolCtrlBase {
    private static final String TAG = "MtkTvVolCtrl";

    public enum SpeakerType {
        AUDDEC_SPK_MODE_LR,
        AUDDEC_SPK_MODE_LL,
        AUDDEC_SPK_MODE_RR,
        AUDDEC_SPK_MODE_MIX,
        AUDDEC_SPK_MODE_INVALID
    }

    public enum VolType {
        VOL_TV_SPEAKER,
        VOL_HEAD_PHONE,
        VOL_FLAG
    }

    private static int getVolType(VolType mType) {
        if (VolType.VOL_TV_SPEAKER == mType) {
            return 0;
        }
        if (VolType.VOL_HEAD_PHONE != mType && VolType.VOL_FLAG == mType) {
            return 2;
        }
        return 1;
    }

    public int incrVolume() {
        Log.d(TAG, "Enter vol_incr.");
        int volume = 0;
        int current = getVolume();
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] incrVolume() is called!");
        } else if (current >= 100) {
            volume = 100;
        } else {
            volume = TVNativeWrapper.incrVolume_native();
        }
        Log.d(TAG, "incrVolume " + volume);
        Log.d(TAG, "Leave vol_incr.");
        return volume;
    }

    public int incrVolume(VolType mType) {
        Log.d(TAG, "Enter vol_incr.");
        int volume = 0;
        int current = getVolume(mType);
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] incrVolume() is called!");
        } else if (current >= 100) {
            volume = 100;
        } else {
            volume = TVNativeWrapper.incrVolume_native(getVolType(mType));
        }
        Log.d(TAG, "incrVolume " + volume);
        Log.d(TAG, "Leave vol_incr.");
        return volume;
    }

    public int decrVolume() {
        Log.d(TAG, "Enter vol_decr.");
        int volume = 0;
        int current = getVolume();
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] incrVolume() is called!");
        } else if (current <= 0) {
            volume = 0;
        } else {
            volume = TVNativeWrapper.decrVolume_native();
        }
        Log.d(TAG, "decrVolume " + volume);
        Log.d(TAG, "Leave vol_decr.");
        return volume;
    }

    public int decrVolume(VolType mType) {
        Log.d(TAG, "Enter vol_decr.");
        int volume = 0;
        int current = getVolume(mType);
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] incrVolume() is called!");
        } else if (current <= 0) {
            volume = 0;
        } else {
            volume = TVNativeWrapper.decrVolume_native(getVolType(mType));
        }
        Log.d(TAG, "decrVolume " + volume);
        Log.d(TAG, "Leave vol_decr.");
        return volume;
    }

    public int setVolume(int value) {
        Log.d(TAG, "Enter set_vol.");
        int err = 0;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] setVolume_native() is called!");
            if (value < 0 || value > 100) {
                err = -1;
            }
        } else if (value < 0 || value > 100) {
            err = -1;
        } else {
            err = TVNativeWrapper.setVolume_native(value);
        }
        Log.d(TAG, "setVolume_native " + err);
        Log.d(TAG, "Leave set_vol.");
        return err;
    }

    public int setVolume(VolType mType, int value) {
        Log.d(TAG, "Enter set_vol.");
        int err = 0;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] setVolume_native() is called!");
            if (value < 0 || value > 100) {
                err = -1;
            }
        } else {
            err = (value < 0 || value > 100) ? -1 : TVNativeWrapper.setVolume_native(getVolType(mType), value);
        }
        Log.d(TAG, "setVolume_native " + err);
        Log.d(TAG, "Leave set_vol.");
        return err;
    }

    public int getVolume() {
        Log.d(TAG, "Enter get_vol.");
        int volume = 0;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] getVolume_native() is called!");
        } else {
            volume = TVNativeWrapper.getVolume_native();
        }
        Log.d(TAG, "get_vol " + volume);
        Log.d(TAG, "Leave get_vol.");
        return volume;
    }

    public int getVolume(VolType mType) {
        Log.d(TAG, "Enter get_vol.");
        int volume = 0;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] getVolume_native() is called!");
        } else {
            volume = TVNativeWrapper.getVolume_native(getVolType(mType));
        }
        Log.d(TAG, "get_vol " + volume);
        Log.d(TAG, "Leave get_vol.");
        return volume;
    }

    public int setMute(boolean isMute) {
        Log.d(TAG, "Enter set_mute.");
        int err = 0;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] setMute_native() is called!");
        } else {
            err = TVNativeWrapper.setMute_native(isMute);
        }
        Log.d(TAG, "setMute_native " + err);
        Log.d(TAG, "Leave set_mute.");
        return err;
    }

    public int setMute(VolType mType, boolean isMute) {
        Log.d(TAG, "Enter set_mute.");
        int err = 0;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] setMute_native() is called!");
        } else {
            err = TVNativeWrapper.setMute_native(getVolType(mType), isMute);
        }
        Log.d(TAG, "setMute_native " + err);
        Log.d(TAG, "Leave set_mute.");
        return err;
    }

    public boolean getMute() {
        Log.d(TAG, "Enter get_mute.");
        boolean isMute = false;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] getMute_native() is called!");
        } else {
            isMute = TVNativeWrapper.getMute_native();
        }
        Log.d(TAG, "getMute " + isMute);
        Log.d(TAG, "Leave get_mute.");
        return isMute;
    }

    public boolean getMute(VolType mType) {
        Log.d(TAG, "Enter get_mute.");
        boolean isMute = false;
        if (MtkTvChCommonBase.isEmulator()) {
            Log.i(TAG, "[Emulator] getMute_native() is called!");
        } else {
            isMute = TVNativeWrapper.getMute_native(getVolType(mType));
        }
        Log.d(TAG, "getMute " + isMute);
        Log.d(TAG, "Leave get_mute.");
        return isMute;
    }

    public SpeakerType getSpeakerOutMode() {
        switch (TVNativeWrapper.HighLevel_native(113, 0, 0, 0, 0, 0, 0)) {
            case 0:
                return SpeakerType.AUDDEC_SPK_MODE_LR;
            case 1:
                return SpeakerType.AUDDEC_SPK_MODE_LL;
            case 2:
                return SpeakerType.AUDDEC_SPK_MODE_RR;
            case 3:
                return SpeakerType.AUDDEC_SPK_MODE_MIX;
            default:
                return SpeakerType.AUDDEC_SPK_MODE_INVALID;
        }
    }

    public int setSpeakerOutMode(SpeakerType type) {
        int value;
        switch (type) {
            case AUDDEC_SPK_MODE_LR:
                value = 0;
                break;
            case AUDDEC_SPK_MODE_LL:
                value = 1;
                break;
            case AUDDEC_SPK_MODE_RR:
                value = 2;
                break;
            case AUDDEC_SPK_MODE_MIX:
                value = 3;
                break;
            default:
                return -1;
        }
        return TVNativeWrapper.HighLevel_native(114, value, 0, 0, 0, 0, 0);
    }
}
