package com.mediatek.twoworlds.tv.common;

public class MtkTvExceptionBase extends Exception {
    private static final long serialVersionUID = 1;
    private int errorCode;
    private String errorMessage;

    public MtkTvExceptionBase() {
    }

    public MtkTvExceptionBase(String errorMessage2) {
        this.errorMessage = errorMessage2;
    }

    public MtkTvExceptionBase(int errorCode2, String errorMessage2) {
        this.errorCode = errorCode2;
        this.errorMessage = errorMessage2;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String toString() {
        return "Error Code=" + this.errorCode + " \tError Message=" + this.errorMessage;
    }
}
