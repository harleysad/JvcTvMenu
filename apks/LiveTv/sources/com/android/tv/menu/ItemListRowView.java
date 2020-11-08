package com.android.tv.menu;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.util.ViewCache;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import java.util.Collections;
import java.util.List;

public class ItemListRowView extends MenuRowView implements OnChildSelectedListener {
    private static final String TAG = MenuView.TAG;
    private HorizontalGridView mListView;
    /* access modifiers changed from: private */
    public CardView<?> mSelectedCard;

    public interface CardView<T> {
        void onBind(T t, boolean z);

        void onDeselected();

        void onRecycled();

        void onSelected();
    }

    public ItemListRowView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ItemListRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemListRowView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public ItemListRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mListView = (HorizontalGridView) getContentsView();
        this.mListView.setItemAnimator((RecyclerView.ItemAnimator) null);
    }

    /* access modifiers changed from: protected */
    public int getContentsViewId() {
        return R.id.list_view;
    }

    public void onBind(MenuRow row) {
        super.onBind(row);
        ItemListAdapter<?> adapter = ((ItemListRow) row).getAdapter();
        ItemListRowView unused = adapter.mItemListView = this;
        this.mListView.setOnChildSelectedListener(this);
        this.mListView.setAdapter(adapter);
    }

    public void initialize(int reason) {
        super.initialize(reason);
        setInitialFocusView(this.mListView);
        this.mListView.setSelectedPosition(getAdapter().getInitialPosition());
    }

    private ItemListAdapter<?> getAdapter() {
        return (ItemListAdapter) this.mListView.getAdapter();
    }

    public void onChildSelected(ViewGroup parent, View child, int position, long id) {
        String str = TAG;
        Log.d(str, "onChildSelected: child=" + child);
        if (this.mSelectedCard != child) {
            if (this.mSelectedCard != null) {
                this.mSelectedCard.onDeselected();
            }
            this.mSelectedCard = (CardView) child;
            if (this.mSelectedCard != null) {
                this.mSelectedCard.onSelected();
            }
        }
    }

    public static abstract class ItemListAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {
        private List<T> mItemList = Collections.emptyList();
        /* access modifiers changed from: private */
        public ItemListRowView mItemListView;
        private final LayoutInflater mLayoutInflater;
        private final TurnkeyUiMainActivity mMainActivity;

        /* access modifiers changed from: protected */
        public abstract int getLayoutResId(int i);

        public abstract void update();

        public ItemListAdapter(Context context) {
            this.mMainActivity = (TurnkeyUiMainActivity) context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        public void release() {
        }

        public int getInitialPosition() {
            return 0;
        }

        /* access modifiers changed from: protected */
        public TurnkeyUiMainActivity getMainActivity() {
            return this.mMainActivity;
        }

        /* access modifiers changed from: protected */
        public List<T> getItemList() {
            return this.mItemList;
        }

        /* access modifiers changed from: protected */
        public void setItemList(List<T> itemList) {
            int oldSize = this.mItemList.size();
            int newSize = itemList.size();
            this.mItemList = itemList;
            if (oldSize > newSize) {
                notifyItemRangeChanged(0, newSize);
                notifyItemRangeRemoved(newSize, oldSize - newSize);
            } else if (oldSize < newSize) {
                notifyItemRangeChanged(0, oldSize);
                notifyItemRangeInserted(oldSize, newSize - oldSize);
            } else {
                notifyItemRangeChanged(0, oldSize);
            }
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getItemCount() {
            return this.mItemList.size();
        }

        /* access modifiers changed from: protected */
        public int getItemPosition(T item) {
            return this.mItemList.indexOf(item);
        }

        /* access modifiers changed from: protected */
        public boolean containsItem(T item) {
            return this.mItemList.contains(item);
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(ViewCache.getInstance().getOrCreateView(this.mLayoutInflater, getLayoutResId(viewType), parent));
        }

        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            CardView<T> cardView = (CardView) viewHolder.itemView;
            cardView.onBind(this.mItemList.get(position), cardView.equals(this.mItemListView.mSelectedCard));
        }

        public void onViewRecycled(MyViewHolder viewHolder) {
            super.onViewRecycled(viewHolder);
            ((CardView) viewHolder.itemView).onRecycled();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            public MyViewHolder(View view) {
                super(view);
            }
        }
    }
}
