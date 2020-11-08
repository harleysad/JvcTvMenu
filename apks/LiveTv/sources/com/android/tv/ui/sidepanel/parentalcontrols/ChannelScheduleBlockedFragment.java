package com.android.tv.ui.sidepanel.parentalcontrols;

import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.ui.OnRepeatedKeyInterceptListener;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.android.tv.ui.sidepanel.SubMenuItem;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import java.util.ArrayList;
import java.util.List;

public class ChannelScheduleBlockedFragment extends SideFragment {
    private static final String TAG = "ChannelScheduleBlockedFragment";
    private EditChannel mEidtChannel;
    private final List<Item> mItems = new ArrayList();
    private int mSelectedPosition = -1;
    /* access modifiers changed from: private */
    public final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            ChannelScheduleBlockedFragment.this.notifyDataSetChanged();
        }
    };

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
                    event.getKeyCode();
                }
                return super.onInterceptKeyEvent(event);
            }
        });
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    /* access modifiers changed from: protected */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (this.mSelectedPosition != -1) {
            setSelectedPosition(this.mSelectedPosition);
        }
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.menu_parental_channel_schedule_block);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        String description;
        this.mItems.clear();
        String[] optionValue = {"Off", "Block"};
        for (MtkTvChannelInfoBase infobase : MenuDataHelper.getInstance(getActivity()).getTVChannelList()) {
            int tid = infobase.getChannelId();
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(MenuDataHelper.getInstance(getActivity()).getDisplayChNumber(tid));
            sb.append("        ");
            sb.append(infobase.getServiceName() == null ? "" : infobase.getServiceName());
            String name = sb.toString();
            if (EditChannel.getInstance(getActivity()).getSchBlockType(infobase.getChannelId()) == 0) {
                description = optionValue[0];
            } else {
                description = optionValue[1];
            }
            String description2 = description;
            if (infobase.getChannelId() == CommonIntegration.getInstance().getCurrentChannelId()) {
                this.mSelectedPosition = this.mItems.size() - 1;
            }
            final String str = name;
            final int i = tid;
            this.mItems.add(new SubMenuItem(name, description2, this.mSideFragmentManager) {
                /* access modifiers changed from: protected */
                public SideFragment getFragment() {
                    SideFragment fragment = SubChannelScheduleBlockedFragment.create(str, i);
                    fragment.setListener(ChannelScheduleBlockedFragment.this.mSideFragmentListener);
                    return fragment;
                }
            });
        }
        return this.mItems;
    }
}
