package com.android.tv.menu;

import android.content.Context;
import android.util.AttributeSet;

public class SimpleCardView extends BaseCardView<ChannelsRowItem> {
    public SimpleCardView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public SimpleCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
