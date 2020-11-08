package com.android.tv.ui.sidepanel.parentalcontrols;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.ui.OnRepeatedKeyInterceptListener;
import com.android.tv.ui.sidepanel.ActionItem;
import com.android.tv.ui.sidepanel.ChannelCheckItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChannelsBlockedFragment extends SideFragment {
    private static final String TAG = "ChannelsBlockedFragment";
    /* access modifiers changed from: private */
    public int mBlockedChannelCount;
    /* access modifiers changed from: private */
    public final List<MtkTvChannelInfoBase> mChannels = new ArrayList();
    /* access modifiers changed from: private */
    public EditChannel mEidtChannel;
    private final List<Item> mItems = new ArrayList();
    /* access modifiers changed from: private */
    public long mLastFocusedChannelId = -1;
    /* access modifiers changed from: private */
    public final Item mLockAllItem = new BlockAllItem();
    private final ContentObserver mProgramUpdateObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            ChannelsBlockedFragment.this.notifyItemsChanged();
        }
    };
    private int mSelectedPosition = -1;
    /* access modifiers changed from: private */
    public boolean mUpdated;

    static /* synthetic */ int access$612(ChannelsBlockedFragment x0, int x1) {
        int i = x0.mBlockedChannelCount + x1;
        x0.mBlockedChannelCount = i;
        return i;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mEidtChannel = EditChannel.getInstance(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        VerticalGridView listView = (VerticalGridView) view.findViewById(R.id.side_panel_list);
        listView.setOnKeyInterceptListener(new OnRepeatedKeyInterceptListener(listView) {
            public boolean onInterceptKeyEvent(KeyEvent event) {
                if (event.getAction() == 1) {
                    switch (event.getKeyCode()) {
                        case 19:
                        case 20:
                            long unused = ChannelsBlockedFragment.this.mLastFocusedChannelId;
                            break;
                    }
                }
                return super.onInterceptKeyEvent(event);
            }
        });
        this.mUpdated = false;
        return view;
    }

    public void onDestroyView() {
        getChannelDataManager().applyUpdatedValuesToDb();
        if (Build.VERSION.SDK_INT >= 26) {
            boolean z = this.mUpdated;
        }
        super.onDestroyView();
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.option_channels_locked);
    }

    /* access modifiers changed from: protected */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (this.mSelectedPosition != -1) {
            setSelectedPosition(this.mSelectedPosition);
        }
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        boolean isEuPARegion = CommonIntegration.getInstance().isCurrentSourceATVforEuPA();
        this.mItems.clear();
        this.mItems.add(this.mLockAllItem);
        this.mChannels.clear();
        List<MtkTvChannelInfoBase> list = MenuDataHelper.getInstance(getActivity().getApplicationContext()).getTVChannelList();
        MtkLog.d(TAG, "list.size=" + list.size());
        this.mChannels.addAll(list);
        long currentChannelId = (long) CommonIntegration.getInstance().getCurrentChannelId();
        for (MtkTvChannelInfoBase channel : list) {
            int channelId = channel.getChannelId();
            if (isEuPARegion) {
                channel._setChannelNumber(CommonIntegration.getInstance().getAnalogChannelDisplayNumInt(channel.getChannelNumber()));
            }
            this.mItems.add(new ChannelBlockedItem(channel));
            if (((long) channel.getChannelId()) == currentChannelId) {
                this.mSelectedPosition = this.mItems.size() - 1;
            }
        }
        this.mBlockedChannelCount = this.mEidtChannel.getBlockChannelNumForSource();
        MtkLog.d(TAG, "mBlockedChannelCount=" + this.mBlockedChannelCount);
        return this.mItems;
    }

    private class BlockAllItem extends ActionItem {
        private TextView mTextView;

        public BlockAllItem() {
            super((String) null);
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            this.mTextView = (TextView) view.findViewById(R.id.title);
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            updateText();
        }

        /* access modifiers changed from: protected */
        public void onUnbind() {
            super.onUnbind();
            this.mTextView = null;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            boolean lock = !areAllChannelsBlocked();
            ChannelsBlockedFragment.this.mEidtChannel.blockChannel((List<MtkTvChannelInfoBase>) ChannelsBlockedFragment.this.mChannels, lock);
            for (TIFChannelInfo channelInfo : ChannelsBlockedFragment.this.getChannelDataManager().getChannelList()) {
                channelInfo.mLocked = lock;
            }
            int unused = ChannelsBlockedFragment.this.mBlockedChannelCount = lock ? ChannelsBlockedFragment.this.mChannels.size() : 0;
            ChannelsBlockedFragment.this.notifyItemsChanged();
            boolean unused2 = ChannelsBlockedFragment.this.mUpdated = true;
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            super.onFocused();
            long unused = ChannelsBlockedFragment.this.mLastFocusedChannelId = -1;
        }

        private void updateText() {
            int i;
            TextView textView = this.mTextView;
            ChannelsBlockedFragment channelsBlockedFragment = ChannelsBlockedFragment.this;
            if (areAllChannelsBlocked()) {
                i = R.string.option_channels_unlock_all;
            } else {
                i = R.string.option_channels_lock_all;
            }
            textView.setText(channelsBlockedFragment.getString(i));
        }

        private boolean areAllChannelsBlocked() {
            return ChannelsBlockedFragment.this.mBlockedChannelCount == ChannelsBlockedFragment.this.mChannels.size();
        }
    }

    private class ChannelBlockedItem extends ChannelCheckItem {
        private ChannelBlockedItem(MtkTvChannelInfoBase channel) {
            super(channel, ChannelsBlockedFragment.this.getChannelDataManager(), ChannelsBlockedFragment.this.getProgramDataManager());
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_channel_lock;
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            setChecked(ChannelsBlockedFragment.this.mEidtChannel.isChannelBlock(getChannel().getChannelId()));
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            ChannelsBlockedFragment.this.getChannelDataManager().updateLocked(Long.valueOf((long) getChannel().getChannelId()), isChecked());
            ChannelsBlockedFragment.this.mEidtChannel.blockChannel(getChannel(), isChecked());
            getChannel().setBlock(isChecked());
            TIFChannelInfo info = TIFChannelManager.getInstance(ChannelsBlockedFragment.this.getActivity()).getTIFChannelInfoById((int) ((long) getChannel().getChannelId()));
            if (info != null) {
                Iterator<TIFChannelInfo> it = ChannelsBlockedFragment.this.getChannelDataManager().getChannelList().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    TIFChannelInfo channel = it.next();
                    if (channel.mId == info.mId) {
                        channel.mLocked = isChecked();
                        break;
                    }
                }
            }
            ChannelsBlockedFragment.access$612(ChannelsBlockedFragment.this, isChecked() ? 1 : -1);
            ChannelsBlockedFragment.this.notifyItemChanged(ChannelsBlockedFragment.this.mLockAllItem);
            boolean unused = ChannelsBlockedFragment.this.mUpdated = true;
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            Log.d("TAG", "onFocused()");
            super.onFocused();
            long unused = ChannelsBlockedFragment.this.mLastFocusedChannelId = (long) getChannel().getChannelId();
        }
    }
}
