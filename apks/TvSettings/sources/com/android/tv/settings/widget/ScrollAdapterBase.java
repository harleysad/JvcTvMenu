package com.android.tv.settings.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface ScrollAdapterBase extends ListAdapter {
    View getScrapView(ViewGroup viewGroup);

    void viewRemoved(View view);
}
