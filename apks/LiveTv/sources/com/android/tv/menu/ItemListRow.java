package com.android.tv.menu;

import android.content.Context;
import com.android.tv.menu.ItemListRowView;
import com.mediatek.wwtv.tvcenter.R;

public class ItemListRow extends MenuRow {
    private ItemListRowView.ItemListAdapter mAdapter;

    public ItemListRow(Context context, Menu menu, int titleResId, int itemHeightResId, ItemListRowView.ItemListAdapter adapter) {
        this(context, menu, context.getString(titleResId), itemHeightResId, adapter);
    }

    public ItemListRow(Context context, Menu menu, String title, int itemHeightResId, ItemListRowView.ItemListAdapter adapter) {
        super(context, menu, title, itemHeightResId);
        this.mAdapter = adapter;
    }

    public ItemListRowView.ItemListAdapter<?> getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(ItemListRowView.ItemListAdapter<?> adapter) {
        this.mAdapter = adapter;
    }

    public void update() {
        this.mAdapter.update();
    }

    public boolean isVisible() {
        return this.mAdapter.getItemCount() > 0;
    }

    public int getLayoutResId() {
        return R.layout.item_list;
    }

    public String getId() {
        return getClass().getName();
    }
}
