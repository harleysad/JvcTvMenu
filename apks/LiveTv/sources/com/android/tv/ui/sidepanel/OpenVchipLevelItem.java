package com.android.tv.ui.sidepanel;

import android.view.View;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public class OpenVchipLevelItem extends CompoundButtonItem {
    private TextView mChannelNumberView;
    private int mOpenVchipDim = 0;
    private int mOpenVchipLevel = 0;
    private int mOpenVchipRegion = 0;
    private TextView mProgramTitleView;

    public OpenVchipLevelItem(int mRegion, int mDim, int mLevel, String mVchipLevelString) {
        super(mVchipLevelString, "");
        this.mOpenVchipRegion = mRegion;
        this.mOpenVchipDim = mDim;
        this.mOpenVchipLevel = mLevel;
    }

    public int getmOpenVchipDim() {
        return this.mOpenVchipDim;
    }

    public int getmOpenVchipLevel() {
        return this.mOpenVchipLevel;
    }

    public int getmOpenVchipRegion() {
        return this.mOpenVchipRegion;
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
}
