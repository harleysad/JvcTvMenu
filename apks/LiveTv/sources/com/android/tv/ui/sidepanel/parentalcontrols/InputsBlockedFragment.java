package com.android.tv.ui.sidepanel.parentalcontrols;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.ui.OnRepeatedKeyInterceptListener;
import com.android.tv.ui.sidepanel.InputCheckItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InputsBlockedFragment extends SideFragment {
    public static final String TAG = "InputsBlock";
    private static final String TRACKER_LABEL = "Channels blocked";
    private EditChannel mEidtChannel;
    private InputSourceManager mInputSourceManager;
    private final List<Item> mItems = new ArrayList();
    /* access modifiers changed from: private */
    public long mLastFocusedChannelId = -1;
    private final ContentObserver mProgramUpdateObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            InputsBlockedFragment.this.notifyItemsChanged();
        }
    };
    private int mSelectedPosition = -1;
    /* access modifiers changed from: private */
    public boolean mUpdated;

    public void onCreate(Bundle savedInstanceState) {
        this.mEidtChannel = EditChannel.getInstance(getActivity().getApplicationContext());
        this.mInputSourceManager = InputSourceManager.getInstance(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
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
                            long unused = InputsBlockedFragment.this.mLastFocusedChannelId;
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
        super.onDestroyView();
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.option_inputs_locked);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mItems.clear();
        Log.d(TAG, "add Input block");
        Map<Integer, String> maps = InputUtil.getSourceList(getActivity());
        for (Integer hardwareId : maps.keySet()) {
            String input = maps.get(hardwareId);
            this.mItems.add(new InputBlockedItem(input, hardwareId.intValue()));
            Log.d(TAG, "getItemList :" + input);
        }
        return this.mItems;
    }

    private class InputBlockedItem extends InputCheckItem {
        private AbstractInput mInputInfo;

        private InputBlockedItem(String inputName, int hardwareId) {
            super(inputName);
            this.mInputInfo = InputUtil.getInput(hardwareId);
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
            Log.d("TAG", "onUpdate()");
            super.onUpdate();
            setChecked(this.mInputInfo.isBlock());
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            Log.d("TAG", "onSelected()");
            super.onSelected();
            this.mInputInfo.block(isChecked());
            boolean unused = InputsBlockedFragment.this.mUpdated = true;
            if (this.mInputInfo.getType() != 0 && this.mInputInfo.getType() != 20000) {
                return;
            }
            if (isChecked()) {
                ComponentStatusListener.getInstance().updateStatus(13, 0);
            } else {
                ComponentStatusListener.getInstance().updateStatus(12, 0);
            }
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            Log.d("TAG", "onFocused()");
            super.onFocused();
        }
    }
}
