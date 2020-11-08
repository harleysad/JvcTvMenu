package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.ui.OnRepeatedKeyInterceptListener;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.OpenVchipLevelItem;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class OpenVchipLevelFragment extends SideFragment {
    private static final String TAG = "OpenVchipLevelFragment";
    /* access modifiers changed from: private */
    public Context mContext;
    private final String mDimString = "OpenVchipDimIndex";
    private final List<Item> mItems = new ArrayList();
    /* access modifiers changed from: private */
    public long mLastFocusedChannelId = -1;
    private final String mRegionString = "OpenVchipRegionIndex";
    private SaveValue mSaveValue;
    private int mSelectedPosition = -1;
    private final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            OpenVchipLevelFragment.this.notifyDataSetChanged();
        }
    };
    /* access modifiers changed from: private */
    public TVContent mTV;
    private String mTitle;
    /* access modifiers changed from: private */
    public boolean mUpdated;

    public static String getDescription(Activity tvActivity) {
        return "";
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return this.mTitle.isEmpty() ? getString(R.string.parental_open_vchip_level) : this.mTitle;
    }

    /* access modifiers changed from: protected */
    public void setTitle(String title) {
        if (title != null) {
            this.mTitle = title;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity().getApplicationContext();
        this.mTV = TVContent.getInstance(getActivity().getApplicationContext());
        this.mSaveValue = SaveValue.getInstance(this.mContext);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
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
                            long unused = OpenVchipLevelFragment.this.mLastFocusedChannelId;
                            break;
                    }
                }
                return super.onInterceptKeyEvent(event);
            }
        });
        this.mUpdated = false;
        return view;
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mItems.clear();
        int regionIndex = this.mSaveValue.readValue("OpenVchipRegionIndex");
        MtkLog.d(TAG, "regionIndex=" + regionIndex);
        int dimIndex = this.mSaveValue.readValue("OpenVchipDimIndex");
        MtkLog.d(TAG, "dimIndex=" + dimIndex);
        this.mTV.getOpenVCHIPPara().setRegionIndex(regionIndex);
        this.mTV.getOpenVCHIPPara().setDimIndex(dimIndex);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(7);
        int levelNum = this.mTV.getOpenVchip().getLevelNum();
        MtkLog.d(TAG, "levelNum=" + levelNum);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(8);
        int k = 0;
        while (true) {
            int k2 = k;
            if (k2 >= levelNum) {
                return this.mItems;
            }
            this.mTV.getOpenVCHIPPara().setLevelIndex(k2 + 1);
            String textString = this.mTV.getOpenVchip().getLvlAbbrText();
            if (textString == null || textString.isEmpty()) {
                textString = this.mContext.getResources().getString(R.string.menu_string_rrt5_none_title);
            }
            String textString2 = textString;
            MtkLog.d(TAG, "textString=" + textString2 + ",regionIndex=" + regionIndex + ",dimIndex=" + dimIndex + ",k=" + k2);
            this.mItems.add(new OpenVchipItem(regionIndex, dimIndex, k2, textString2));
            k = k2 + 1;
        }
    }

    private class OpenVchipItem extends OpenVchipLevelItem {
        private OpenVchipItem(int mRegion, int mDim, int mLevel, String mLevelString) {
            super(mRegion, mDim, mLevel, mLevelString);
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_channel_lock;
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            View channelContent = view.findViewById(R.id.channel_content);
            if (channelContent != null) {
                channelContent.setVisibility(8);
            }
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            boolean z = true;
            if (OpenVchipLevelFragment.this.mTV.getNewOpenVchipSetting(getmOpenVchipRegion(), getmOpenVchipDim()).getLvlBlockData()[getmOpenVchipLevel()] != 1) {
                z = false;
            }
            setChecked(z);
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            int index = getmOpenVchipLevel();
            MtkLog.d(OpenVchipLevelFragment.TAG, "index=" + index);
            int reginIndex = getmOpenVchipRegion();
            MtkLog.d(OpenVchipLevelFragment.TAG, "reginIndex=" + reginIndex);
            int dimIndex = getmOpenVchipDim();
            byte b = OpenVchipLevelFragment.this.mTV.getNewOpenVchipSetting(reginIndex, dimIndex).getLvlBlockData()[index];
            EditChannel.getInstance(OpenVchipLevelFragment.this.mContext).setOpenVCHIP(reginIndex, dimIndex, index);
            OpenVchipLevelFragment.this.notifyItemsChanged();
            boolean unused = OpenVchipLevelFragment.this.mUpdated = true;
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            super.onFocused();
        }
    }
}
