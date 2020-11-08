package com.android.tv.twopanelsettings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class TwoPanelScrollView extends HorizontalScrollView {
    public TwoPanelScrollView(Context context) {
        super(context);
    }

    public TwoPanelScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean arrowScroll(int direction) {
        return true;
    }
}
