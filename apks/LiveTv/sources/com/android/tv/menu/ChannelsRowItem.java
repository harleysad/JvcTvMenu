package com.android.tv.menu;

import android.support.annotation.NonNull;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;

public class ChannelsRowItem {
    public static final ChannelsRowItem APP_LINK_ITEM = new ChannelsRowItem(-4, (int) R.layout.menu_card_app_link);
    public static final int APP_LINK_ITEM_ID = -4;
    public static final int DOWN_ID = -6;
    public static final ChannelsRowItem DOWN_ITEM = new ChannelsRowItem(-6, (int) R.layout.menu_card_down);
    public static final int DVR_ITEM_ID = -3;
    public static final int FAVORITE_ID = -7;
    public static final ChannelsRowItem FAVORITE_ITEM = new ChannelsRowItem(-7, (int) R.layout.menu_card_favorite_channels);
    public static final ChannelsRowItem GUIDE_ITEM = new ChannelsRowItem(-1, (int) R.layout.menu_card_guide);
    public static final int GUIDE_ITEM_ID = -1;
    public static final ChannelsRowItem SETUP_ITEM = new ChannelsRowItem(-2, (int) R.layout.menu_card_setup);
    public static final int SETUP_ITEM_ID = -2;
    public static final int UP_ID = -5;
    public static final ChannelsRowItem UP_ITEM = new ChannelsRowItem(-5, (int) R.layout.menu_card_up);
    @NonNull
    private TIFChannelInfo mChannel;
    private final long mItemId;
    private final int mLayoutId;

    public ChannelsRowItem(@NonNull TIFChannelInfo channel, int layoutId) {
        this(channel.mId, layoutId);
        this.mChannel = channel;
    }

    private ChannelsRowItem(long itemId, int layoutId) {
        this.mItemId = itemId;
        this.mLayoutId = layoutId;
    }

    @NonNull
    public TIFChannelInfo getChannel() {
        return this.mChannel;
    }

    public void setChannel(@NonNull TIFChannelInfo channel) {
        this.mChannel = channel;
    }

    public int getLayoutId() {
        return this.mLayoutId;
    }

    public long getItemId() {
        return this.mItemId;
    }

    public String toString() {
        return "ChannelsRowItem{itemId=" + this.mItemId + ", layoutId=" + this.mLayoutId + ", channel=" + this.mChannel + "}";
    }
}
