package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvGpioBase {
    protected static final int GPIO_ID_MIN = 0;
    public static final int GPIO_MODE_INPUT = 0;
    public static final int GPIO_MODE_OUTPUT = 1;
    public static final int GPIO_STATUS_HIGH = 2;
    public static final int GPIO_STATUS_ID_INVALID = -1;
    public static final int GPIO_STATUS_LOW = 1;
    public static final int GPIO_STATUS_MODE_INVALID = -2;
    public static final int GPIO_STATUS_UNKNOWN = 0;
    public static final String TAG = "TV_MtkTvGPIOControl";

    /* access modifiers changed from: protected */
    public int getGpioIdNumber() {
        return 0;
    }

    public int queryGpioStatus(int gpioId, int mode) {
        if (mode < 0 || mode > 1) {
            return -2;
        }
        int i = TVNativeWrapper.queryGpioStatus_native(gpioId, mode);
        Log.d(TAG, "queryGpioStatus ret = " + i);
        return i;
    }

    public int applyGpioStatus(int gpioId, int value) {
        if (2 != value && 1 != value) {
            return -1;
        }
        int i = TVNativeWrapper.applyGpioStatus_native(gpioId, value);
        Log.d(TAG, "applyGpioStatus ret = " + i);
        return i;
    }
}
