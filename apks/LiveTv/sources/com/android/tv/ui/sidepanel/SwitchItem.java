package com.android.tv.ui.sidepanel;

import com.mediatek.wwtv.tvcenter.R;

public class SwitchItem extends CompoundButtonItem {
    public SwitchItem(String title) {
        this(title, (String) null, (String) null);
    }

    public SwitchItem(String checkedTitle, String uncheckedTitle) {
        this(checkedTitle, uncheckedTitle, (String) null);
    }

    public SwitchItem(String checkedTitle, String uncheckedTitle, String description) {
        super(checkedTitle, uncheckedTitle, description);
    }

    public SwitchItem(String checkedTitle, String uncheckedTitle, String description, int maxLines) {
        super(checkedTitle, uncheckedTitle, description, maxLines);
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_switch;
    }

    /* access modifiers changed from: protected */
    public int getCompoundButtonId() {
        return R.id.switch_button;
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
        setChecked(!isChecked());
    }
}
