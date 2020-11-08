package com.mediatek.wwtv.tvcenter.util;

import java.util.List;

public class PageImp implements PageInterface {
    private static final String TAG = "PageImp";
    private int allNum;
    private List<?> currentList;
    private int currentPage = 1;
    private List<?> list;
    private int pageNum = 1;
    private int perPage = 6;

    public PageImp() {
    }

    public PageImp(List<?> list2, int perPage2) {
        init(list2, perPage2);
    }

    private void init(List<?> list2, int perPage2) {
        this.list = list2;
        if (perPage2 >= 1) {
            this.perPage = perPage2;
        }
        if (list2 != null && list2.size() > this.perPage) {
            this.currentList = list2.subList(0, this.perPage);
        }
        if (list2 != null) {
            this.allNum = list2.size();
            this.pageNum = ((this.allNum + this.perPage) - 1) / this.perPage;
            if (this.pageNum == 0) {
                this.pageNum = 1;
            }
        }
        MtkLog.d(TAG, "init()perPage:" + this.perPage + "allnum:" + this.allNum);
    }

    public int getCount() {
        return this.allNum;
    }

    public void setCount(int count) {
        this.allNum = count;
    }

    public List<?> getList() {
        return this.list;
    }

    public List<?> getCurrentList() {
        MtkLog.d(TAG, "getCurrentList()currentPage:" + this.currentPage + "pageNum:" + this.pageNum);
        if (this.list == null) {
            MtkLog.w(TAG, "list==null!");
            return null;
        }
        if (this.currentPage == this.pageNum) {
            this.currentList = this.list.subList((this.currentPage - 1) * this.perPage, this.allNum);
        } else {
            this.currentList = this.list.subList((this.currentPage - 1) * this.perPage, this.perPage * this.currentPage);
        }
        MtkLog.d(TAG, "currentList size" + this.currentList.size());
        return this.currentList;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public int getPerPage() {
        return this.perPage;
    }

    public void gotoPage(int n) {
        if (n > this.pageNum) {
            this.currentPage = this.pageNum;
        } else {
            this.currentPage = n;
        }
    }

    public boolean hasNextPage() {
        this.currentPage++;
        if (this.currentPage <= this.pageNum) {
            return true;
        }
        this.currentPage = this.pageNum;
        return false;
    }

    public boolean hasPrePage() {
        this.currentPage--;
        if (this.currentPage > 0) {
            return true;
        }
        this.currentPage = 1;
        return false;
    }

    public void headPage() {
        this.currentPage = 1;
    }

    public void lastPage() {
        this.currentPage = this.pageNum;
    }

    public void nextPage() {
        MtkLog.d(TAG, "nextPage");
        hasNextPage();
    }

    public void prePage() {
        MtkLog.d(TAG, "prePage");
        hasPrePage();
    }

    public void setPerPageNum(int perPage2) {
        this.perPage = perPage2;
    }
}
