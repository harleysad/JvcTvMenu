package com.mediatek.twoworlds.tv.model;

public class MtkTvTeletextPageBase {
    public static final int TTX_ANY_PAGE_SUB_CODE = 16255;
    public static final int TTX_NULL_PAGE_NUMBER = 255;
    private int pageNumber;
    private int pageSubCode;

    public MtkTvTeletextPageBase() {
        this.pageNumber = 255;
        this.pageSubCode = TTX_ANY_PAGE_SUB_CODE;
    }

    public MtkTvTeletextPageBase(int pageNumber2, int pageSubCode2) {
        this.pageNumber = pageNumber2;
        this.pageSubCode = pageSubCode2;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber2) {
        this.pageNumber = pageNumber2;
    }

    public int getPageSubCode() {
        return this.pageSubCode;
    }

    public void setPageSubCode(int pageSubCode2) {
        this.pageSubCode = pageSubCode2;
    }
}
