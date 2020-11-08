package com.android.tv.ui.sidepanel;

import android.view.View;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public abstract class ActionItem extends Item {
    protected String mDescription;
    protected String mTitle;

    public ActionItem(String title) {
        this(title, (String) null);
    }

    public ActionItem() {
        this("", (String) null);
    }

    public ActionItem(String title, String description) {
        this.mTitle = title;
        this.mDescription = description;
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_action;
    }

    /* access modifiers changed from: protected */
    public void onBind(View view) {
        super.onBind(view);
        ((TextView) view.findViewById(R.id.title)).setText(this.mTitle);
        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        if (this.mDescription != null) {
            descriptionView.setVisibility(0);
            descriptionView.setText(this.mDescription);
            return;
        }
        descriptionView.setVisibility(8);
    }
}
