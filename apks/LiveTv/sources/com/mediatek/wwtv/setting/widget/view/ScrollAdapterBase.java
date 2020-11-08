package com.mediatek.wwtv.setting.widget.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface ScrollAdapterBase extends ListAdapter {
    View getScrapView(ViewGroup viewGroup);

    void viewRemoved(View view);
}
