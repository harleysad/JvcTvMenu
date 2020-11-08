package com.android.tv.ui.sidepanel;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public abstract class CompoundButtonItem extends Item {
    private static int sDefaultMaxLine = 0;
    private boolean mChecked;
    private final String mCheckedTitle;
    private CompoundButton mCompoundButton;
    private final String mDescription;
    private final int mMaxLine;
    private TextView mTextView;
    private final String mUncheckedTitle;

    /* access modifiers changed from: protected */
    public abstract int getCompoundButtonId();

    public CompoundButtonItem(String title, String description) {
        this(title, title, description);
    }

    public CompoundButtonItem(String checkedTitle, String uncheckedTitle, String description) {
        this.mCheckedTitle = checkedTitle;
        this.mUncheckedTitle = uncheckedTitle;
        this.mDescription = description;
        this.mMaxLine = 0;
    }

    public CompoundButtonItem(String checkedTitle, String uncheckedTitle, String description, int maxLine) {
        this.mCheckedTitle = checkedTitle;
        this.mUncheckedTitle = uncheckedTitle;
        this.mDescription = description;
        this.mMaxLine = maxLine;
    }

    /* access modifiers changed from: protected */
    public int getTitleViewId() {
        return R.id.title;
    }

    /* access modifiers changed from: protected */
    public int getDescriptionViewId() {
        return R.id.description;
    }

    /* access modifiers changed from: protected */
    public void onBind(View view) {
        super.onBind(view);
        this.mCompoundButton = (CompoundButton) view.findViewById(getCompoundButtonId());
        this.mTextView = (TextView) view.findViewById(getTitleViewId());
        TextView descriptionView = (TextView) view.findViewById(getDescriptionViewId());
        if (this.mDescription != null) {
            if (this.mMaxLine != 0) {
                descriptionView.setMaxLines(this.mMaxLine);
            } else {
                if (sDefaultMaxLine == 0) {
                    sDefaultMaxLine = view.getContext().getResources().getInteger(R.integer.option_item_description_max_lines);
                }
                descriptionView.setMaxLines(sDefaultMaxLine);
            }
            descriptionView.setVisibility(0);
            descriptionView.setText(this.mDescription);
            return;
        }
        descriptionView.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onUnbind() {
        super.onUnbind();
        this.mTextView = null;
        this.mCompoundButton = null;
    }

    /* access modifiers changed from: protected */
    public void onUpdate() {
        super.onUpdate();
        updateInternal();
    }

    public void setChecked(boolean checked) {
        if (this.mChecked != checked) {
            this.mChecked = checked;
            updateInternal();
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    private void updateInternal() {
        if (isBound()) {
            if (this.mTextView != null) {
                this.mTextView.setText(this.mChecked ? this.mCheckedTitle : this.mUncheckedTitle);
            }
            if (this.mCompoundButton != null) {
                this.mCompoundButton.setChecked(this.mChecked);
            }
        }
    }
}
