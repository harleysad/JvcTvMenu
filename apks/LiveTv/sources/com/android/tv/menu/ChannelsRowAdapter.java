package com.android.tv.menu;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.tv.menu.ItemListRowView;
import com.android.tv.onboarding.SetupSourceActivity;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.fav.FavChannelManager;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.List;

public class ChannelsRowAdapter extends ItemListRowView.ItemListAdapter<ChannelsRowItem> implements AccessibilityManager.AccessibilityStateChangeListener {
    private static final int[] BACKGROUND_IMAGES = {R.drawable.ic_favorite, R.drawable.ic_favorite_red};
    private static final int[] FAVORITE_TIPS = {R.string.options_item_edit_my_favorite, R.string.options_item_my_favorite};
    private static final String TAG = ChannelsRowAdapter.class.getSimpleName();
    /* access modifiers changed from: private */
    public final Context mContext;
    private ImageView mFavoriteImageView = null;
    private TextView mFavoriteTextView = null;
    private TextView mGuideView = null;
    private final int mMaxCount;
    /* access modifiers changed from: private */
    public final Menu mMenu;
    private final int mMinCount;
    private TextView mNewChannelView = null;
    private boolean mShowChannelUpDown;

    public ChannelsRowAdapter(Context context, Menu menu, int minCount, int maxCount) {
        super(context);
        this.mContext = context;
        this.mMenu = menu;
        this.mMinCount = minCount;
        this.mMaxCount = maxCount;
        setHasStableIds(true);
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mShowChannelUpDown = accessibilityManager.isEnabled();
        accessibilityManager.addAccessibilityStateChangeListener(this);
    }

    public int getItemViewType(int position) {
        return ((ChannelsRowItem) getItemList().get(position)).getLayoutId();
    }

    /* access modifiers changed from: protected */
    public int getLayoutResId(int viewType) {
        return viewType;
    }

    public long getItemId(int position) {
        return ((ChannelsRowItem) getItemList().get(position)).getItemId();
    }

    public void onBindViewHolder(ItemListRowView.ItemListAdapter.MyViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        String str = TAG;
        Log.d(str, "onBindViewHolder " + viewType);
        if (viewType == R.layout.menu_card_guide) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onGuideClicked(view);
                }
            });
            this.mGuideView = (TextView) viewHolder.itemView.findViewById(R.id.menu_card_guide_text);
            this.mGuideView.setText(this.mContext.getString(R.string.channels_item_program_guide));
        } else if (viewType == R.layout.menu_card_up) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onChannelUpClicked(view);
                }
            });
        } else if (viewType == R.layout.menu_card_down) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onChannelDownClicked(view);
                }
            });
        } else if (viewType == R.layout.menu_card_setup) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onSetupClicked(view);
                }
            });
            this.mNewChannelView = (TextView) viewHolder.itemView.findViewById(R.id.menu_card_new_channel_text);
            this.mNewChannelView.setText(R.string.channels_item_setup);
        } else if (viewType == R.layout.menu_card_app_link) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onAppLinkClicked(view);
                }
            });
        } else if (viewType == R.layout.menu_card_favorite_channels) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onFavoriteClicked(view);
                }
            });
            this.mFavoriteImageView = (ImageView) viewHolder.itemView.findViewById(R.id.menu_card_favorite_image);
            this.mFavoriteTextView = (TextView) viewHolder.itemView.findViewById(R.id.menu_card_favorite_text);
            updateFavoriteIcon();
        } else {
            viewHolder.itemView.setTag(((ChannelsRowItem) getItemList().get(position)).getChannel());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelsRowAdapter.this.onChannelClicked(view);
                }
            });
        }
        super.onBindViewHolder(viewHolder, position);
    }

    public void update() {
        if (getItemCount() == 0) {
            createItems();
        } else {
            updateItems();
        }
    }

    /* access modifiers changed from: private */
    public void onGuideClicked(View unused) {
        String str = TAG;
        Log.d(str, "onGuideClicked " + unused);
        if (DvrManager.getInstance().pvrIsRecording()) {
            ComponentsManager.getInstance().hideAllComponents();
            StateDvr.getInstance().onKeyDown(KeyMap.KEYCODE_MTKIR_GUIDE);
            return;
        }
        getMainActivity().showEPG();
    }

    /* access modifiers changed from: private */
    public void onChannelDownClicked(View unused) {
        String str = TAG;
        Log.d(str, "onChannelDownClicked " + unused);
        InstrumentationHandler.getInstance().sendKeyDownUpSync(KeyMap.KEYCODE_MTKIR_CHDN);
    }

    /* access modifiers changed from: private */
    public void onChannelUpClicked(View unused) {
        String str = TAG;
        Log.d(str, "onChannelUpClicked " + unused);
        InstrumentationHandler.getInstance().sendKeyDownUpSync(KeyMap.KEYCODE_MTKIR_CHUP);
    }

    /* access modifiers changed from: private */
    public void onSetupClicked(View unused) {
        if (SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
            Toast.makeText(this.mContext, "timeshift recording!", 0).show();
        } else if (DvrManager.getInstance().pvrIsRecording()) {
            Toast.makeText(this.mContext, this.mContext.getResources().getString(R.string.title_pvr_running), 0).show();
        } else {
            getMainActivity().startActivity(new Intent(this.mContext, SetupSourceActivity.class));
        }
    }

    /* access modifiers changed from: private */
    public void onFavoriteClicked(View unused) {
        String str = TAG;
        Log.d(str, "onGuideClicked " + unused);
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                FavChannelManager.getInstance(ChannelsRowAdapter.this.mContext).favAddOrErase();
                ChannelsRowAdapter.this.getMainActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ChannelsRowAdapter.this.updateFavoriteIcon();
                    }
                });
                if (ChannelsRowAdapter.this.mMenu != null) {
                    ChannelsRowAdapter.this.mMenu.scheduleHide();
                }
            }
        });
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [boolean] */
    /* access modifiers changed from: private */
    public void updateFavoriteIcon() {
        ? isFavChannel = FavChannelManager.getInstance(this.mContext).isFavChannel();
        String str = TAG;
        Log.d(str, "updateFavoriteIcon " + isFavChannel);
        if (this.mFavoriteImageView != null) {
            this.mFavoriteImageView.setImageDrawable(this.mContext.getResources().getDrawable(BACKGROUND_IMAGES[isFavChannel]));
        }
        if (this.mFavoriteTextView != null) {
            this.mFavoriteTextView.setText(FAVORITE_TIPS[isFavChannel]);
        }
    }

    private void onDvrClicked(View unused) {
    }

    /* access modifiers changed from: private */
    public void onAppLinkClicked(View view) {
        Intent intent = ((AppLinkCardView) view).getIntent();
        if (intent != null) {
            getMainActivity().startActivitySafe(intent);
        }
    }

    /* access modifiers changed from: private */
    public void onChannelClicked(View view) {
        try {
            TIFChannelInfo channel = (TIFChannelInfo) view.getTag();
            getMainActivity().getTvView().tune(channel.mInputServiceName, ContentUris.withAppendedId(TvContract.Channels.CONTENT_URI, channel.mId));
            MenuOptionMain tvOptionsManager = getMainActivity().getTvOptionsManager();
            if (tvOptionsManager != null) {
                tvOptionsManager.setVisibility(4);
            }
        } catch (Exception e) {
        }
    }

    private void createItems() {
        List<ChannelsRowItem> items = new ArrayList<>();
        if (needToShowFavoriteItem()) {
            items.add(ChannelsRowItem.FAVORITE_ITEM);
        }
        if (needToShowGuideItem()) {
            items.add(ChannelsRowItem.GUIDE_ITEM);
        }
        if (this.mShowChannelUpDown) {
            items.add(ChannelsRowItem.UP_ITEM);
            items.add(ChannelsRowItem.DOWN_ITEM);
        }
        String str = TAG;
        Log.d(str, "createItems||needToShowSetupItem =" + needToShowSetupItem());
        if (needToShowSetupItem()) {
            items.add(ChannelsRowItem.SETUP_ITEM);
        }
        if (needToShowAppLinkItem()) {
            ChannelsRowItem.APP_LINK_ITEM.setChannel(TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri());
            items.add(ChannelsRowItem.APP_LINK_ITEM);
        }
        Log.d(TAG, "createItems||setItemList");
        setItemList(items);
    }

    private void updateItems() {
        List<ChannelsRowItem> items = getItemList();
        int currentIndex = 0;
        if (updateItem(needToShowFavoriteItem(), ChannelsRowItem.FAVORITE_ITEM, 0)) {
            updateFavoriteIcon();
            currentIndex = 0 + 1;
        }
        if (updateItem(needToShowGuideItem(), ChannelsRowItem.GUIDE_ITEM, currentIndex)) {
            currentIndex++;
        }
        if (updateItem(this.mShowChannelUpDown, ChannelsRowItem.UP_ITEM, currentIndex)) {
            currentIndex++;
        }
        if (updateItem(this.mShowChannelUpDown, ChannelsRowItem.DOWN_ITEM, currentIndex)) {
            currentIndex++;
        }
        String str = TAG;
        Log.d(str, "updateItems||needToShowSetupItem =" + needToShowSetupItem());
        if (updateItem(needToShowSetupItem(), ChannelsRowItem.SETUP_ITEM, currentIndex)) {
            currentIndex++;
        }
        if (updateItem(needToShowAppLinkItem(), ChannelsRowItem.APP_LINK_ITEM, currentIndex)) {
            ((ChannelsRowItem) getItemList().get(currentIndex)).setChannel(TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri());
            notifyDataSetChanged();
            currentIndex++;
        }
        int numOldChannels = items.size() - currentIndex;
        if (numOldChannels > 0) {
            while (items.size() > currentIndex) {
                items.remove(items.size() - 1);
            }
            notifyItemRangeRemoved(currentIndex, numOldChannels);
        }
        int numNewChannels = items.size() - currentIndex;
        if (numNewChannels > 0) {
            notifyItemRangeInserted(currentIndex, numNewChannels);
        }
    }

    private boolean updateItem(boolean needToShow, ChannelsRowItem item, int index) {
        List<ChannelsRowItem> items = getItemList();
        boolean isItemInList = index < items.size() && item.equals(items.get(index));
        if (needToShow && !isItemInList) {
            items.add(index, item);
            notifyItemInserted(index);
        } else if (!needToShow && isItemInList) {
            items.remove(index);
            notifyItemRemoved(index);
        }
        return needToShow;
    }

    private boolean needToShowFavoriteItem() {
        CommonIntegration ci = TvSingletons.getSingletons().getCommonIntegration();
        return ci.isCurrentSourceTv() && ci.hasActiveChannel() && !ci.isCurrentSourceBlocked() && !ci.is3rdTVSource() && !DataSeparaterUtil.getInstance().isDisableColorKey();
    }

    private boolean needToShowGuideItem() {
        return MarketRegionInfo.isFunctionSupport(37) && !DataSeparaterUtil.getInstance().isAtvOnly();
    }

    private boolean needToShowSetupItem() {
        return true;
    }

    private boolean needToShowAppLinkItem() {
        TIFChannelInfo currentChannel = TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri();
        if (currentChannel == null || currentChannel.getAppLinkType(this.mContext) == -1 || InputSourceManager.getInstance().getTvInputAppInfo(currentChannel.mInputServiceName) == null) {
            return false;
        }
        return true;
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        this.mShowChannelUpDown = enabled;
        update();
    }
}
