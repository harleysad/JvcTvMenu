package com.mediatek.wwtv.setting.util;

import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class Pager {
    public int ITEM_PER_PAGE = 10;
    int allItemNum;
    private List<?> currentList;
    public int currentPage = 1;
    private List<?> list;
    public int pageTotal = 1;
    public int selectPosition;

    public Pager(List<?> list2, int gotoPage) {
        this.list = list2;
        if (list2.size() % this.ITEM_PER_PAGE == 0) {
            this.pageTotal = list2.size() / this.ITEM_PER_PAGE;
        } else {
            this.pageTotal = (list2.size() / this.ITEM_PER_PAGE) + 1;
        }
        this.allItemNum = list2.size();
        if (this.currentPage < gotoPage) {
            this.currentPage = gotoPage;
        }
    }

    public void setPagerList(List<?> list2) {
        this.list = list2;
    }

    public List<?> getRealDataList() {
        if (this.currentPage == this.pageTotal) {
            this.currentList = this.list.subList((this.currentPage - 1) * this.ITEM_PER_PAGE, this.allItemNum);
        } else {
            MtkLog.d("Pager", "currentPage==" + this.currentPage + ",size ==" + this.list.size());
            this.currentList = this.list.subList((this.currentPage + -1) * this.ITEM_PER_PAGE, this.currentPage * this.ITEM_PER_PAGE);
        }
        return this.currentList;
    }
}
