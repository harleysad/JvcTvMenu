package com.mediatek.twoworlds.tv;

public class MtkTvSoftKeyboard extends MtkTvSoftKeyboardBase {
    private static MtkTvSoftKeyboard mtkTvSoftKeyBoard = null;

    private MtkTvSoftKeyboard() {
    }

    public static MtkTvSoftKeyboard getInstance() {
        if (mtkTvSoftKeyBoard != null) {
            return mtkTvSoftKeyBoard;
        }
        mtkTvSoftKeyBoard = new MtkTvSoftKeyboard();
        return mtkTvSoftKeyBoard;
    }
}
