package com.android.tv.ui.sidepanel;

import android.view.View;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public abstract class InputCheckItem extends CompoundButtonItem {
    private TextView mChannelNumberView;
    private TextView mProgramTitleView;
    private String mSourceName;

    public InputCheckItem(String inputName) {
        super(inputName, (String) null);
        this.mSourceName = inputName;
    }

    /* access modifiers changed from: protected */
    public String getInputName() {
        return this.mSourceName;
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_channel_check;
    }

    /* access modifiers changed from: protected */
    public int getCompoundButtonId() {
        return R.id.check_box;
    }

    /* access modifiers changed from: protected */
    public int getTitleViewId() {
        return R.id.channel_name;
    }

    /* access modifiers changed from: protected */
    public int getDescriptionViewId() {
        return R.id.program_title;
    }

    /* access modifiers changed from: protected */
    public void onBind(View view) {
        super.onBind(view);
        this.mChannelNumberView = (TextView) view.findViewById(R.id.channel_number);
        this.mProgramTitleView = (TextView) view.findViewById(R.id.program_title);
    }

    /* access modifiers changed from: protected */
    public void onUpdate() {
        super.onUpdate();
        this.mChannelNumberView.setText("");
        updateProgramTitle();
    }

    /* access modifiers changed from: protected */
    public void onUnbind() {
        this.mProgramTitleView = null;
        this.mChannelNumberView = null;
        super.onUnbind();
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
        setChecked(!isChecked());
    }

    private void updateProgramTitle() {
    }
}
