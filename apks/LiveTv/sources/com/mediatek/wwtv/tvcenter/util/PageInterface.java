package com.mediatek.wwtv.tvcenter.util;

import java.util.List;

public interface PageInterface {
    int getCount();

    List<?> getCurrentList();

    int getCurrentPage();

    int getPageNum();

    int getPerPage();

    void gotoPage(int i);

    boolean hasNextPage();

    boolean hasPrePage();

    void headPage();

    void lastPage();

    void nextPage();

    void prePage();

    void setPerPageNum(int i);
}
