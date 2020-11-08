package com.android.tv.ui.sidepanel;

import com.mediatek.wwtv.tvcenter.R;

public class RadioButtonItem extends CompoundButtonItem {
    public RadioButtonItem(String title) {
        super(title, (String) null);
    }

    public RadioButtonItem(String title, String description) {
        super(title, description);
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_radio_button;
    }

    /* access modifiers changed from: protected */
    public int getCompoundButtonId() {
        return R.id.radio_button;
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
        setChecked(true);
    }
}
