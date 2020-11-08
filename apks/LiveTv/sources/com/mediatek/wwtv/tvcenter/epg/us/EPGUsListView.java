package com.mediatek.wwtv.tvcenter.epg.us;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ListView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PageImp;
import java.util.List;

public class EPGUsListView extends ListView {
    private static final String TAG = "EPGUsListView";
    private PageImp mPageImp;
    private int mSelectItem;
    private UpDateListView mUpdate;
    private boolean sourceFlag = false;

    public interface UpDateListView {
        void update(boolean z);
    }

    public EPGUsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EPGUsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EPGUsListView(Context context) {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.mSelectItem = getSelectedItemPosition();
        MtkLog.d(TAG, "onKeyDown mSelectItem:" + this.mSelectItem);
        if (keyCode == 93) {
            return false;
        }
        switch (keyCode) {
            case 19:
                if (this.sourceFlag) {
                    if (this.mSelectItem == 0) {
                        setSelection(this.mPageImp.getCount() - 1);
                        break;
                    }
                } else {
                    MtkLog.d(TAG, "onkey  up mSelectItem  =   " + this.mSelectItem + " mPageImp.getPerPage()" + this.mPageImp.getPerPage());
                    if (this.mPageImp.getCount() > 0 && getVisibility() == 0) {
                        if (this.mSelectItem == 0 && this.mUpdate != null) {
                            this.mUpdate.update(false);
                            break;
                        }
                    } else {
                        return false;
                    }
                }
                break;
            case 20:
                if (!this.sourceFlag) {
                    if (this.mPageImp.getCount() > 0 && getVisibility() == 0) {
                        if (this.mSelectItem >= 0 && ((this.mSelectItem + 1) % this.mPageImp.getPerPage() == 0 || (this.mSelectItem + 1) % this.mPageImp.getCount() == 0)) {
                            this.mPageImp.nextPage();
                            if (this.mUpdate != null && this.mPageImp.getCount() % this.mPageImp.getPerPage() == 0) {
                                this.mUpdate.update(true);
                                break;
                            }
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
