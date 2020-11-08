package com.android.tv.ui.sidepanel;

import android.view.View;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public class DividerItem extends Item {
    private String mTitle;
    private TextView mTitleView;

    public DividerItem() {
    }

    public DividerItem(String title) {
        this.mTitle = title;
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_divider;
    }

    /* access modifiers changed from: protected */
    public void onBind(View view) {
        super.onBind(view);
        this.mTitleView = (TextView) view.findViewById(R.id.title);
        if (this.mTitle == null) {
            this.mTitleView.setVisibility(8);
            view.setMinimumHeight(0);
            return;
        }
        this.mTitleView.setVisibility(0);
        this.mTitleView.setText(this.mTitle);
        view.setMinimumHeight(view.getContext().getResources().getDimensionPixelOffset(R.dimen.option_item_height));
    }

    /* access modifiers changed from: protected */
    public void onUnbind() {
        super.onUnbind();
        this.mTitleView = null;
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
    }
}
