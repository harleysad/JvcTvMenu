package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import androidx.slice.core.SliceActionImpl;
import com.android.tv.twopanelsettings.R;

public class SlicePreference extends Preference implements HasSliceAction {
    private static final String TAG = "SlicePreference";
    private SliceActionImpl mAction;
    private SliceActionImpl mFollowUpAction;
    private String mUri;

    public SlicePreference(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public SlicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            initStyleAttributes(attrs);
        }
    }

    private void initStyleAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SlicePreference);
        for (int i = a.getIndexCount() - 1; i >= 0; i--) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SlicePreference_uri) {
                this.mUri = a.getString(attr);
                return;
            }
        }
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getUri() {
        return this.mUri;
    }

    public void setSliceAction(SliceActionImpl action) {
        this.mAction = action;
    }

    public SliceActionImpl getFollowupSliceAction() {
        return this.mFollowUpAction;
    }

    public void setFollowupSliceAction(SliceActionImpl sliceAction) {
        this.mFollowUpAction = sliceAction;
    }

    public SliceActionImpl getSliceAction() {
        return this.mAction;
    }
}
