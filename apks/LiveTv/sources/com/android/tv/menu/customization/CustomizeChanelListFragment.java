package com.android.tv.menu.customization;

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
import com.android.tv.ui.sidepanel.CheckBoxItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomizeChanelListFragment extends SideFragment {
    private static final String TRACKER_LABEL = "CustomizeChanelListFragment";
    /* access modifiers changed from: private */
    public final List<MtkTvChannelInfoBase> mChannels = new ArrayList();
    /* access modifiers changed from: private */
    public EditChannel mEidtChannel;
    private final List<Item> mItems = new ArrayList();
    /* access modifiers changed from: private */
    public long mLastFocusedChannelId = -1;
    private final ContentObserver mProgramUpdateObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            CustomizeChanelListFragment.this.notifyItemsChanged();
        }
    };
    private int mSelectedPosition = -1;
    /* access modifiers changed from: private */
    public final Item mSkipAllItem = new SkipkAllItem();
    /* access modifiers changed from: private */
    public int mSkipedChannelCount;
    /* access modifiers changed from: private */
    public boolean mUpdated;

    static /* synthetic */ int access$612(CustomizeChanelListFragment x0, int x1) {
        int i = x0.mSkipedChannelCount + x1;
        x0.mSkipedChannelCount = i;
        return i;
    }

    /* access modifiers changed from: protected */
    public int getFragmentLayoutResourceId() {
        return R.layout.multi_audio_fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        MtkLog.d(TRACKER_LABEL, "onCreate()");
        this.mEidtChannel = EditChannel.getInstance(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        MtkLog.d(TRACKER_LABEL, "onCreateView()");
        if (this.mSelectedPosition != -1) {
            setSelectedPosition(this.mSelectedPosition);
        }
        VerticalGridView listView = (VerticalGridView) view.findViewById(R.id.side_panel_list);
        listView.setOnKeyInterceptListener(new OnRepeatedKeyInterceptListener(listView) {
            public boolean onInterceptKeyEvent(KeyEvent event) {
                if (event.getAction() == 1) {
                    switch (event.getKeyCode()) {
                        case 19:
                        case 20:
                            long unused = CustomizeChanelListFragment.this.mLastFocusedChannelId;
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
        MtkLog.d(TRACKER_LABEL, "onDestroyView()");
        getChannelDataManager().applyUpdatedValuesToDb();
        if (Build.VERSION.SDK_INT >= 26) {
            boolean z = this.mUpdated;
        }
        super.onDestroyView();
        MenuConfigManager.getInstance(getActivity().getApplicationContext()).setValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        MtkLog.d(TRACKER_LABEL, "getTitle()");
        return getString(R.string.menu_channel_customize_channel_list);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mItems.clear();
        this.mItems.add(this.mSkipAllItem);
        this.mChannels.clear();
        MtkLog.d(TRACKER_LABEL, "getItemList() start...");
        long currentChannelId = (long) CommonIntegration.getInstance().getCurrentChannelId();
        List<MtkTvChannelInfoBase> list = MenuDataHelper.getInstance(getActivity().getApplicationContext()).getTVChannelList();
        MtkLog.d(TRACKER_LABEL, "getItemList() mid ... ");
        for (MtkTvChannelInfoBase channel : list) {
            if (((long) channel.getChannelId()) != currentChannelId) {
                this.mChannels.add(channel);
            }
        }
        MtkLog.d(TRACKER_LABEL, "getItemList() end ... ");
        for (MtkTvChannelInfoBase channel2 : this.mChannels) {
            int channelId = channel2.getChannelId();
            this.mItems.add(new ChannelSkipedItem(channel2));
            if (this.mEidtChannel.isChannelSkip(channel2.getChannelId())) {
                this.mSkipedChannelCount++;
            }
            if (((long) channel2.getChannelId()) == currentChannelId) {
                this.mSelectedPosition = this.mItems.size() - 1;
            }
        }
        return this.mItems;
    }

    private class SkipkAllItem extends ActionItem {
        private TextView mTextView;

        public SkipkAllItem() {
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
            MtkLog.d(CustomizeChanelListFragment.TRACKER_LABEL, "onCreate()");
            boolean skip = !areAllChannelsSkiped();
            for (MtkTvChannelInfoBase channel : CustomizeChanelListFragment.this.mChannels) {
                CustomizeChanelListFragment.this.mEidtChannel.setChannelSkip((int) ((long) channel.getChannelId()), skip);
            }
            long currentChannelId = (long) CommonIntegration.getInstance().getCurrentChannelId();
            for (TIFChannelInfo channelInfo : CustomizeChanelListFragment.this.getChannelDataManager().getCurrentSVLChannelList()) {
                if (!(channelInfo.mMtkTvChannelInfo == null || currentChannelId == ((long) channelInfo.mMtkTvChannelInfo.getChannelId()))) {
                    channelInfo.mMtkTvChannelInfo.setSkip(skip);
                }
            }
            int unused = CustomizeChanelListFragment.this.mSkipedChannelCount = skip ? CustomizeChanelListFragment.this.mChannels.size() : 0;
            CustomizeChanelListFragment.this.notifyItemsChanged();
            boolean unused2 = CustomizeChanelListFragment.this.mUpdated = true;
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            super.onFocused();
            long unused = CustomizeChanelListFragment.this.mLastFocusedChannelId = -1;
        }

        private void updateText() {
            int i;
            TextView textView = this.mTextView;
            CustomizeChanelListFragment customizeChanelListFragment = CustomizeChanelListFragment.this;
            if (areAllChannelsSkiped()) {
                i = R.string.option_channels_unskip_all;
            } else {
                i = R.string.option_channels_skip_all;
            }
            textView.setText(customizeChanelListFragment.getString(i));
        }

        private boolean areAllChannelsSkiped() {
            return CustomizeChanelListFragment.this.mSkipedChannelCount == CustomizeChanelListFragment.this.mChannels.size();
        }
    }

    private class ChannelSkipedItem extends CheckBoxItem {
        private ChannelSkipedItem(MtkTvChannelInfoBase channel) {
            super(channel, CustomizeChanelListFragment.this.getChannelDataManager(), CustomizeChanelListFragment.this.getProgramDataManager());
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_check_box;
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            Log.d("TAG", "onUpdate()");
            super.onUpdate();
            setChecked(CustomizeChanelListFragment.this.mEidtChannel.isChannelSkip(getChannel().getChannelId()));
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            Log.d("TAG", "onSelected()");
            super.onSelected();
            Log.d("TAG", "onSelected()1 getChannel().getId() = " + getChannel().getChannelId());
            long channelId = (long) getChannel().getChannelId();
            CustomizeChanelListFragment.this.mEidtChannel.setChannelSkip((int) channelId, isChecked());
            TIFChannelInfo info = TIFChannelManager.getInstance(CustomizeChanelListFragment.this.getActivity()).getTIFChannelInfoById((int) channelId);
            Iterator<TIFChannelInfo> it = CustomizeChanelListFragment.this.getChannelDataManager().getCurrentSVLChannelList().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TIFChannelInfo channel = it.next();
                if (channel.mId == info.mId) {
                    channel.mMtkTvChannelInfo.setSkip(isChecked());
                    break;
                }
            }
            CustomizeChanelListFragment.access$612(CustomizeChanelListFragment.this, isChecked() ? 1 : -1);
            CustomizeChanelListFragment.this.notifyItemChanged(CustomizeChanelListFragment.this.mSkipAllItem);
            boolean unused = CustomizeChanelListFragment.this.mUpdated = true;
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            Log.d("TAG", "onFocused()");
            super.onFocused();
            long unused = CustomizeChanelListFragment.this.mLastFocusedChannelId = (long) getChannel().getChannelId();
        }
    }
}
