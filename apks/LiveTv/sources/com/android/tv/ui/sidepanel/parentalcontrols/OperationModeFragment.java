package com.android.tv.ui.sidepanel.parentalcontrols;

import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.ui.OnRepeatedKeyInterceptListener;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.RadioButtonItem;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import java.util.ArrayList;
import java.util.List;

public class OperationModeFragment extends SideFragment {
    private static final String ARGS_CHANNEL_ID = "args_channel_id";
    private static final String ARGS_INDEX = "args_index";
    private static final String TAG = "OperationModeFragment";
    /* access modifiers changed from: private */
    public int mChannelId;
    private int mIndex;
    private final List<Item> mItems = new ArrayList();
    private final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            OperationModeFragment.this.notifyDataSetChanged();
        }
    };

    public static OperationModeFragment create(int index, int channelId) {
        OperationModeFragment fragment = new OperationModeFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_INDEX, index);
        args.putInt(ARGS_CHANNEL_ID, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIndex = getArguments().getInt(ARGS_INDEX);
        this.mChannelId = getArguments().getInt(ARGS_CHANNEL_ID);
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
    public String getTitle() {
        return getString(R.string.menu_parental_channel_schedule_block_operation_mode);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mItems.clear();
        String[] valueStrings = getActivity().getResources().getStringArray(R.array.menu_parental_block_channel_schedule_operation_array);
        int i = 0;
        while (i < valueStrings.length) {
            RadioButtonItem item = new OperationModeItem(valueStrings[i], i);
            item.setChecked(i == this.mIndex);
            this.mItems.add(item);
            i++;
        }
        return this.mItems;
    }

    private class OperationModeItem extends RadioButtonItem {
        private final int mIndex;

        private OperationModeItem(String title, int index) {
            super(title);
            this.mIndex = index;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            MenuConfigManager instance = MenuConfigManager.getInstance(OperationModeFragment.this.getActivity());
            instance.setValue(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE + OperationModeFragment.this.mChannelId, this.mIndex);
        }
    }
}
