package com.android.tv.ui.sidepanel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.android.tv.util.ViewCache;
import com.mediatek.wwtv.setting.LiveTvSetting;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.util.ArrayList;
import java.util.List;

public abstract class SideFragment<T extends Item> extends Fragment {
    public static final int INVALID_POSITION = -1;
    private static final int[] PRELOAD_VIEW_IDS = {R.layout.option_item_radio_button, R.layout.option_item_channel_lock, R.layout.option_item_check_box, R.layout.option_item_channel_check, R.layout.option_item_action};
    private static final int PRELOAD_VIEW_SIZE = 7;
    private static RecyclerView.RecycledViewPool sRecycledViewPool = new RecyclerView.RecycledViewPool();
    private ItemAdapter mAdapter;
    private TIFChannelManager mChannelDataManager;
    private final int mDebugHideKey;
    private final int mHideKey;
    /* access modifiers changed from: private */
    public List<T> mItems;
    private VerticalGridView mListView;
    private SideFragmentListener mListener;
    private TIFProgramManager mProgramDataManager;
    public SideFragmentManager mSideFragmentManager;

    public interface SideFragmentListener {
        void onSideFragmentViewDestroyed();
    }

    /* access modifiers changed from: protected */
    public abstract List<T> getItemList();

    /* access modifiers changed from: protected */
    public abstract String getTitle();

    public SideFragment() {
        this(0, 0);
    }

    public SideFragment(int hideKey, int debugHideKey) {
        this.mItems = new ArrayList();
        this.mHideKey = hideKey;
        this.mDebugHideKey = debugHideKey;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mChannelDataManager = TvSingletons.getSingletons().getChannelDataManager();
        this.mProgramDataManager = TvSingletons.getSingletons().getProgramDataManager();
        this.mSideFragmentManager = getMainActivity().getSideFragmentManager();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = ViewCache.getInstance().getOrCreateView(inflater, getFragmentLayoutResourceId(), container);
        ((TextView) view.findViewById(R.id.side_panel_title)).setText(getTitle());
        this.mListView = (VerticalGridView) view.findViewById(R.id.side_panel_list);
        this.mListView.setRecycledViewPool(sRecycledViewPool);
        this.mAdapter = new ItemAdapter(inflater, this.mItems);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.requestFocus();
        getData();
        return view;
    }

    /* access modifiers changed from: protected */
    public void getData() {
        new Thread(new GetDataRunnable()).start();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDetach() {
        super.onDetach();
    }

    public final boolean isHideKeyForThisPanel(int keyCode) {
        return this.mHideKey != 0 && this.mHideKey == keyCode;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mListView.swapAdapter((RecyclerView.Adapter) null, true);
        if (this.mListener != null) {
            this.mListener.onSideFragmentViewDestroyed();
        }
    }

    public final void setListener(SideFragmentListener listener) {
        this.mListener = listener;
    }

    /* access modifiers changed from: protected */
    public void setSelectedPosition(int position) {
        this.mListView.setSelectedPosition(position);
    }

    /* access modifiers changed from: protected */
    public int getSelectedPosition() {
        return this.mListView.getSelectedPosition();
    }

    public void setItems(List<T> items) {
        this.mAdapter.reset(items);
    }

    /* access modifiers changed from: protected */
    public void closeFragment() {
        this.mSideFragmentManager.popSideFragment();
    }

    /* access modifiers changed from: protected */
    public LiveTvSetting getMainActivity() {
        return LiveTvSetting.getInstance();
    }

    /* access modifiers changed from: protected */
    public TIFChannelManager getChannelDataManager() {
        return this.mChannelDataManager;
    }

    /* access modifiers changed from: protected */
    public TIFProgramManager getProgramDataManager() {
        return this.mProgramDataManager;
    }

    /* access modifiers changed from: protected */
    public void notifyDataSetChanged() {
        this.mAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void notifyItemChanged(int position) {
        notifyItemChanged(this.mAdapter.getItem(position));
    }

    /* access modifiers changed from: protected */
    public void notifyItemChanged(Item item) {
        item.notifyUpdated();
    }

    /* access modifiers changed from: protected */
    public void notifyItemsChanged() {
        notifyItemsChanged(0, this.mAdapter.getItemCount());
    }

    /* access modifiers changed from: protected */
    public void notifyItemsChanged(int positionStart) {
        notifyItemsChanged(positionStart, this.mAdapter.getItemCount() - positionStart);
    }

    /* access modifiers changed from: protected */
    public void notifyItemsChanged(int positionStart, int positionStart2) {
        while (true) {
            int itemCount = positionStart2 - 1;
            if (positionStart2 != 0) {
                notifyItemChanged(positionStart);
                positionStart++;
                positionStart2 = itemCount;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getFragmentLayoutResourceId() {
        return R.layout.option_fragment;
    }

    public static void preloadItemViews(Context context) {
        ViewCache.getInstance().putView(context, R.layout.option_fragment, new FrameLayout(context), 1);
        VerticalGridView fakeParent = new VerticalGridView(context);
        for (int id : PRELOAD_VIEW_IDS) {
            sRecycledViewPool.setMaxRecycledViews(id, 7);
            ViewCache.getInstance().putView(context, id, fakeParent, 7);
        }
    }

    public static void releaseRecycledViewPool() {
        sRecycledViewPool.clear();
    }

    private static class ItemAdapter<T extends Item> extends RecyclerView.Adapter<ViewHolder> {
        private List<T> mItems;
        private final LayoutInflater mLayoutInflater;

        private ItemAdapter(LayoutInflater layoutInflater, List<T> items) {
            this.mLayoutInflater = layoutInflater;
            this.mItems = items;
        }

        /* access modifiers changed from: private */
        public void reset(List<T> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(ViewCache.getInstance().getOrCreateView(this.mLayoutInflater, viewType, parent));
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(this, getItem(position));
        }

        public void onViewRecycled(ViewHolder holder) {
            holder.onUnbind();
        }

        public int getItemViewType(int position) {
            return getItem(position).getResourceId();
        }

        public int getItemCount() {
            if (this.mItems == null) {
                return 0;
            }
            return this.mItems.size();
        }

        /* access modifiers changed from: private */
        public T getItem(int position) {
            return (Item) this.mItems.get(position);
        }

        /* access modifiers changed from: private */
        public void clearRadioGroup(T item) {
            int position = this.mItems.indexOf(item);
            for (int i = position - 1; i >= 0; i--) {
                T t = (Item) this.mItems.get(i);
                T item2 = t;
                if (!(t instanceof RadioButtonItem)) {
                    break;
                }
                ((RadioButtonItem) item2).setChecked(false);
            }
            int i2 = position + 1;
            while (i2 < this.mItems.size()) {
                T t2 = (Item) this.mItems.get(i2);
                T item3 = t2;
                if (t2 instanceof RadioButtonItem) {
                    ((RadioButtonItem) item3).setChecked(false);
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {
        private ItemAdapter mAdapter;
        public Item mItem;

        private ViewHolder(View view) {
            super(view);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnFocusChangeListener(this);
        }

        public void onBind(ItemAdapter adapter, Item item) {
            this.mAdapter = adapter;
            this.mItem = item;
            this.mItem.onBind(this.itemView);
            this.mItem.onUpdate();
        }

        public void onUnbind() {
            this.mItem.onUnbind();
            this.mItem = null;
            this.mAdapter = null;
        }

        public void onClick(View view) {
            if (this.mItem instanceof RadioButtonItem) {
                this.mAdapter.clearRadioGroup(this.mItem);
            }
            if (view.getBackground() instanceof RippleDrawable) {
                view.postDelayed(new Runnable() {
                    public final void run() {
                        SideFragment.ViewHolder.lambda$onClick$0(SideFragment.ViewHolder.this);
                    }
                }, (long) view.getResources().getInteger(R.integer.side_panel_ripple_anim_duration));
            } else {
                this.mItem.onSelected();
            }
        }

        public static /* synthetic */ void lambda$onClick$0(ViewHolder viewHolder) {
            if (viewHolder.mItem != null) {
                viewHolder.mItem.onSelected();
            }
        }

        public void onFocusChange(View view, boolean focusGained) {
            if (focusGained) {
                this.mItem.onFocused();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void refreshUI() {
        notifyDataSetChanged();
    }

    private class GetDataRunnable implements Runnable {
        private GetDataRunnable() {
        }

        public void run() {
            SideFragment.this.mItems.clear();
            SideFragment.this.mItems.addAll(SideFragment.this.getItemList());
            Activity activity = SideFragment.this.getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        SideFragment.this.refreshUI();
                    }
                });
            }
        }
    }
}
