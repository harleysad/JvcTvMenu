package com.mediatek.wwtv.tvcenter.commonview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ListView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PageImp;
import java.util.List;

public class CustListView extends ListView {
    private static final String TAG = "CustListView";
    private PageImp mPageImp;
    private int mSelectItem;
    private UpDateListView mUpdate;
    private boolean sourceFlag = false;

    public interface UpDateListView {
        void updata();
    }

    public CustListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustListView(Context context) {
        super(context);
    }

    public void initData(List<?> list, int perPage, UpDateListView update) {
        this.mPageImp = new PageImp(list, perPage);
        this.mUpdate = update;
    }

    public void initData(List<?> list, int perPage, UpDateListView update, boolean flag) {
        this.mPageImp = new PageImp(list, perPage);
        this.mUpdate = update;
        this.sourceFlag = flag;
    }

    public void initData(List<?> list, int perPage) {
        this.mPageImp = new PageImp(list, perPage);
    }

    public List<?> getCurrentList() {
        return this.mPageImp.getCurrentList();
    }

    public List<?> getNextList() {
        this.mPageImp.nextPage();
        return getCurrentList();
    }

    public List<?> getListWithPage(int page) {
        this.mPageImp.gotoPage(page);
        return getCurrentList();
    }

    public List<?> getPreList() {
        this.mPageImp.prePage();
        return getCurrentList();
    }

    public boolean hasPrePage() {
        return this.mPageImp.hasPrePage();
    }

    public boolean hasNextPage() {
        return this.mPageImp.hasNextPage();
    }

    public void setListCount(int count) {
        this.mPageImp.setCount(count);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.mSelectItem = getSelectedItemPosition();
        MtkLog.d(TAG, "onKeyDown mSelectItem:" + this.mSelectItem);
        MtkLog.d(TAG, "onKeyDown sourceFlag:" + this.sourceFlag);
        if (this.mPageImp == null) {
            return false;
        }
        switch (keyCode) {
            case 19:
                if (this.sourceFlag) {
                    if (this.mSelectItem == 0) {
                        MtkLog.d(TAG, "onKeyDown mPageImp.getCount():" + this.mPageImp.getCount());
                        setSelection(this.mPageImp.getCount() - 1);
                        break;
                    }
                } else {
                    MtkLog.d(TAG, "onkey  up mSelectItem  =   " + this.mSelectItem + " mPageImp.getPerPage()" + this.mPageImp.getPerPage());
                    if (this.mPageImp.getCount() > 0) {
                        if (this.mSelectItem % this.mPageImp.getPerPage() <= 0) {
                            this.mPageImp.prePage();
                            if (this.mUpdate != null) {
                                this.mUpdate.updata();
                            }
                            if (this.mPageImp.getPerPage() < this.mPageImp.getCount()) {
                                setSelection(this.mPageImp.getPerPage() - 1);
                            } else {
                                setSelection(this.mPageImp.getCount() - 1);
                            }
                        }
                        if (this.mSelectItem % this.mPageImp.getPerPage() == 0 && this.mPageImp.getPageNum() == 1) {
                            setSelection(this.mPageImp.getPerPage() - 1);
                            break;
                        }
                    } else {
                        return false;
                    }
                }
                break;
            case 20:
                if (!this.sourceFlag) {
                    if (this.mPageImp.getCount() > 0) {
                        if ((this.mSelectItem + 1) % this.mPageImp.getPerPage() == 0 || (this.mSelectItem + 1) % this.mPageImp.getCount() == 0) {
                            this.mPageImp.nextPage();
                            if (this.mUpdate != null) {
                                this.mUpdate.updata();
                            }
                            setSelection(0);
                        }
                        if ((this.mSelectItem + 1) % this.mPageImp.getPerPage() == 0 && this.mPageImp.getPageNum() == 1) {
                            setSelection(0);
                            break;
                        }
                    } else {
                        return false;
                    }
                } else if (this.mPageImp.getCount() > 0) {
                    if (this.mSelectItem == this.mPageImp.getCount() - 1) {
                        setSelection(0);
                        break;
                    }
                } else {
                    return false;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public PageImp getPageImp() {
        return this.mPageImp;
    }
}
