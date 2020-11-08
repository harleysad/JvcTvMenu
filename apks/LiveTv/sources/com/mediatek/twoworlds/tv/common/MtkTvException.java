package com.mediatek.twoworlds.tv.common;

public class MtkTvException extends MtkTvExceptionBase {
    private static final long serialVersionUID = 1;

    public MtkTvException(int ret, String string) {
        super(ret, string);
    }

    public MtkTvException(String string) {
        super(string);
    }
}
