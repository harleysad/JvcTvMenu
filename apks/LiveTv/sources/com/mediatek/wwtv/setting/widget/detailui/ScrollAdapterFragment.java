package com.mediatek.wwtv.setting.widget.detailui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapter;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;

public class ScrollAdapterFragment extends Fragment implements LiteFragment {
    private BaseScrollAdapterFragment mBase = new BaseScrollAdapterFragment(this);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.mBase.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mBase.onViewCreated(view, savedInstanceState);
    }

    public ScrollAdapterView getScrollAdapterView() {
        return this.mBase.getScrollAdapterView();
    }

    public ScrollAdapter getAdapter() {
        return this.mBase.getAdapter();
    }

    public void setAdapter(ScrollAdapter adapter) {
        this.mBase.setAdapter(adapter);
    }

    public void setSelection(int position) {
        this.mBase.setSelection(position);
    }

    public void setSelectionSmooth(int position) {
        this.mBase.setSelectionSmooth(position);
    }

    public int getSelectedItemPosition() {
        return this.mBase.getSelectedItemPosition();
    }

    /* access modifiers changed from: protected */
    public void setBaseScrollAdapterFragment(BaseScrollAdapterFragment base) {
        this.mBase = base;
    }
}
