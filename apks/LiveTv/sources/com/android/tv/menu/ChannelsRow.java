package com.android.tv.menu;

import android.content.Context;
import com.android.tv.menu.ItemListRowView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;

public class ChannelsRow extends ItemListRow {
    public static final String ID = ChannelsRow.class.getName();
    public static final int MAX_COUNT_FOR_RECENT_CHANNELS = 10;
    public static final int MIN_COUNT_FOR_RECENT_CHANNELS = 5;
    private ChannelsRowAdapter mChannelsAdapter;

    public ChannelsRow(Context context, Menu menu) {
        super(context, menu, (int) R.string.menu_title_channels, (int) R.dimen.card_layout_height, (ItemListRowView.ItemListAdapter) null);
        this.mChannelsAdapter = new ChannelsRowAdapter(context, menu, 5, 10);
        setAdapter(this.mChannelsAdapter);
    }

    public void release() {
        super.release();
    }

    public void onRecentChannelsChanged() {
    }

    public String getId() {
        return ID;
    }

    public boolean isVisible() {
        return super.isVisible() && InputSourceManager.getInstance().isCurrentTvSource(CommonIntegration.getInstance().getCurrentFocus());
    }

    public String getTitle() {
        return this.mContext.getString(R.string.menu_title_channels);
    }
}
