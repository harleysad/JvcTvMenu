package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class ListViewItemView extends RelativeLayout {
    protected Context context;
    protected SetConfigListViewAdapter.DataItem mDataItem;
    protected String mId;

    public ListViewItemView(Context context2, AttributeSet attrs, int defStyle) {
        super(context2, attrs, defStyle);
        this.context = context2;
    }

    public ListViewItemView(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        this.context = context2;
    }

    public String getmId() {
        return this.mId;
    }

    public void setmId(String mId2) {
        this.mId = mId2;
    }

    public SetConfigListViewAdapter.DataItem getmDataItem() {
        return this.mDataItem;
    }

    public void setmDataItem(SetConfigListViewAdapter.DataItem mDataItem2) {
        this.mDataItem = mDataItem2;
    }

    public ListViewItemView(Context context2) {
        super(context2);
        this.context = context2;
        setLayoutParams(new AbsListView.LayoutParams(-1, getSize()));
        setFocusable(true);
        setClickable(true);
        setEnabled(true);
    }

    public int getSize() {
        new Point();
        return ScreenConstant.SCREEN_HEIGHT / 13;
    }
}
