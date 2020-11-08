package com.mediatek.twoworlds.tv;

public class MtkTvGpio extends MtkTvGpioBase {
    private static final int GPIO_ID_COMPONENT = 3;
    public static final int GPIO_ID_CVBS = 1;
    private static final int GPIO_ID_HEADPHONE = 4;
    private static final int GPIO_ID_MHL_CBUS = 5;
    private static final int GPIO_ID_MINI_CVBS = 2;

    /* access modifiers changed from: protected */
    public int getGpioIdNumber() {
        return 5;
    }
}
