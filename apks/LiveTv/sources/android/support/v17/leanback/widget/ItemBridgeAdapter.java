package android.support.v17.leanback.widget;

import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class ItemBridgeAdapter extends RecyclerView.Adapter implements FacetProviderAdapter {
    static final boolean DEBUG = false;
    static final String TAG = "ItemBridgeAdapter";
    private ObjectAdapter mAdapter;
    private AdapterListener mAdapterListener;
    private ObjectAdapter.DataObserver mDataObserver;
    FocusHighlightHandler mFocusHighlight;
    private PresenterSelector mPresenterSelector;
    private ArrayList<Presenter> mPresenters;
    Wrapper mWrapper;

    public static abstract class Wrapper {
        public abstract View createWrapper(View view);

        public abstract void wrap(View view, View view2);
    }

    public static class AdapterListener {
        public void onAddPresenter(Presenter presenter, int type) {
        }

        public void onCreate(ViewHolder viewHolder) {
        }

        public void onBind(ViewHolder viewHolder) {
        }

        public void onBind(ViewHolder viewHolder, List payloads) {
            onBind(viewHolder);
        }

        public void onUnbind(ViewHolder viewHolder) {
        }

        public void onAttachedToWindow(ViewHolder viewHolder) {
        }

        public void onDetachedFromWindow(ViewHolder viewHolder) {
        }
    }

    final class OnFocusChangeListener implements View.OnFocusChangeListener {
        View.OnFocusChangeListener mChainedListener;

        OnFocusChangeListener() {
        }

        /* JADX WARNING: type inference failed for: r0v8, types: [android.view.ViewParent] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onFocusChange(android.view.View r2, boolean r3) {
            /*
                r1 = this;
                android.support.v17.leanback.widget.ItemBridgeAdapter r0 = android.support.v17.leanback.widget.ItemBridgeAdapter.this
                android.support.v17.leanback.widget.ItemBridgeAdapter$Wrapper r0 = r0.mWrapper
                if (r0 == 0) goto L_0x000d
                android.view.ViewParent r0 = r2.getParent()
                r2 = r0
                android.view.View r2 = (android.view.View) r2
            L_0x000d:
                android.support.v17.leanback.widget.ItemBridgeAdapter r0 = android.support.v17.leanback.widget.ItemBridgeAdapter.this
                android.support.v17.leanback.widget.FocusHighlightHandler r0 = r0.mFocusHighlight
                if (r0 == 0) goto L_0x001a
                android.support.v17.leanback.widget.ItemBridgeAdapter r0 = android.support.v17.leanback.widget.ItemBridgeAdapter.this
                android.support.v17.leanback.widget.FocusHighlightHandler r0 = r0.mFocusHighlight
                r0.onItemFocused(r2, r3)
            L_0x001a:
                android.view.View$OnFocusChangeListener r0 = r1.mChainedListener
                if (r0 == 0) goto L_0x0023
                android.view.View$OnFocusChangeListener r0 = r1.mChainedListener
                r0.onFocusChange(r2, r3)
            L_0x0023:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.ItemBridgeAdapter.OnFocusChangeListener.onFocusChange(android.view.View, boolean):void");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements FacetProvider {
        Object mExtraObject;
        final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();
        final Presenter.ViewHolder mHolder;
        Object mItem;
        final Presenter mPresenter;

        public final Presenter getPresenter() {
            return this.mPresenter;
        }

        public final Presenter.ViewHolder getViewHolder() {
            return this.mHolder;
        }

        public final Object getItem() {
            return this.mItem;
        }

        public final Object getExtraObject() {
            return this.mExtraObject;
        }

        public void setExtraObject(Object object) {
            this.mExtraObject = object;
        }

        public Object getFacet(Class<?> facetClass) {
            return this.mHolder.getFacet(facetClass);
        }

        ViewHolder(Presenter presenter, View view, Presenter.ViewHolder holder) {
            super(view);
            this.mPresenter = presenter;
            this.mHolder = holder;
        }
    }

    public ItemBridgeAdapter(ObjectAdapter adapter, PresenterSelector presenterSelector) {
        this.mPresenters = new ArrayList<>();
        this.mDataObserver = new ObjectAdapter.DataObserver() {
            public void onChanged() {
                ItemBridgeAdapter.this.notifyDataSetChanged();
            }

            public void onItemRangeChanged(int positionStart, int itemCount) {
                ItemBridgeAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
            }

            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                ItemBridgeAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                ItemBridgeAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                ItemBridgeAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
            }

            public void onItemMoved(int fromPosition, int toPosition) {
                ItemBridgeAdapter.this.notifyItemMoved(fromPosition, toPosition);
            }
        };
        setAdapter(adapter);
        this.mPresenterSelector = presenterSelector;
    }

    public ItemBridgeAdapter(ObjectAdapter adapter) {
        this(adapter, (PresenterSelector) null);
    }

    public ItemBridgeAdapter() {
        this.mPresenters = new ArrayList<>();
        this.mDataObserver = new ObjectAdapter.DataObserver() {
            public void onChanged() {
                ItemBridgeAdapter.this.notifyDataSetChanged();
            }

            public void onItemRangeChanged(int positionStart, int itemCount) {
                ItemBridgeAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
            }

            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                ItemBridgeAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                ItemBridgeAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                ItemBridgeAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
            }

            public void onItemMoved(int fromPosition, int toPosition) {
                ItemBridgeAdapter.this.notifyItemMoved(fromPosition, toPosition);
            }
        };
    }

    public void setAdapter(ObjectAdapter adapter) {
        if (adapter != this.mAdapter) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterObserver(this.mDataObserver);
            }
            this.mAdapter = adapter;
            if (this.mAdapter == null) {
                notifyDataSetChanged();
                return;
            }
            this.mAdapter.registerObserver(this.mDataObserver);
            if (hasStableIds() != this.mAdapter.hasStableIds()) {
                setHasStableIds(this.mAdapter.hasStableIds());
            }
            notifyDataSetChanged();
        }
    }

    public void setPresenter(PresenterSelector presenterSelector) {
        this.mPresenterSelector = presenterSelector;
        notifyDataSetChanged();
    }

    public void setWrapper(Wrapper wrapper) {
        this.mWrapper = wrapper;
    }

    public Wrapper getWrapper() {
        return this.mWrapper;
    }

    /* access modifiers changed from: package-private */
    public void setFocusHighlight(FocusHighlightHandler listener) {
        this.mFocusHighlight = listener;
    }

    public void clear() {
        setAdapter((ObjectAdapter) null);
    }

    public void setPresenterMapper(ArrayList<Presenter> presenters) {
        this.mPresenters = presenters;
    }

    public ArrayList<Presenter> getPresenterMapper() {
        return this.mPresenters;
    }

    public int getItemCount() {
        if (this.mAdapter != null) {
            return this.mAdapter.size();
        }
        return 0;
    }

    public int getItemViewType(int position) {
        PresenterSelector presenterSelector;
        if (this.mPresenterSelector != null) {
            presenterSelector = this.mPresenterSelector;
        } else {
            presenterSelector = this.mAdapter.getPresenterSelector();
        }
        Presenter presenter = presenterSelector.getPresenter(this.mAdapter.get(position));
        int type = this.mPresenters.indexOf(presenter);
        if (type < 0) {
            this.mPresenters.add(presenter);
            type = this.mPresenters.indexOf(presenter);
            onAddPresenter(presenter, type);
            if (this.mAdapterListener != null) {
                this.mAdapterListener.onAddPresenter(presenter, type);
            }
        }
        return type;
    }

    /* access modifiers changed from: protected */
    public void onAddPresenter(Presenter presenter, int type) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onBind(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onUnbind(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow(ViewHolder viewHolder) {
    }

    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Presenter.ViewHolder presenterVh;
        View view;
        Presenter presenter = this.mPresenters.get(viewType);
        if (this.mWrapper != null) {
            view = this.mWrapper.createWrapper(parent);
            presenterVh = presenter.onCreateViewHolder(parent);
            this.mWrapper.wrap(view, presenterVh.view);
        } else {
            presenterVh = presenter.onCreateViewHolder(parent);
            view = presenterVh.view;
        }
        ViewHolder viewHolder = new ViewHolder(presenter, view, presenterVh);
        onCreate(viewHolder);
        if (this.mAdapterListener != null) {
            this.mAdapterListener.onCreate(viewHolder);
        }
        View presenterView = viewHolder.mHolder.view;
        if (presenterView != null) {
            viewHolder.mFocusChangeListener.mChainedListener = presenterView.getOnFocusChangeListener();
            presenterView.setOnFocusChangeListener(viewHolder.mFocusChangeListener);
        }
        if (this.mFocusHighlight != null) {
            this.mFocusHighlight.onInitializeView(view);
        }
        return viewHolder;
    }

    public void setAdapterListener(AdapterListener listener) {
        this.mAdapterListener = listener;
    }

    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mItem = this.mAdapter.get(position);
        viewHolder.mPresenter.onBindViewHolder(viewHolder.mHolder, viewHolder.mItem);
        onBind(viewHolder);
        if (this.mAdapterListener != null) {
            this.mAdapterListener.onBind(viewHolder);
        }
    }

    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mItem = this.mAdapter.get(position);
        viewHolder.mPresenter.onBindViewHolder(viewHolder.mHolder, viewHolder.mItem, payloads);
        onBind(viewHolder);
        if (this.mAdapterListener != null) {
            this.mAdapterListener.onBind(viewHolder, payloads);
        }
    }

    public final void onViewRecycled(RecyclerView.ViewHolder holder) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mPresenter.onUnbindViewHolder(viewHolder.mHolder);
        onUnbind(viewHolder);
        if (this.mAdapterListener != null) {
            this.mAdapterListener.onUnbind(viewHolder);
        }
        viewHolder.mItem = null;
    }

    public final boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        onViewRecycled(holder);
        return false;
    }

    public final void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewHolder viewHolder = (ViewHolder) holder;
        onAttachedToWindow(viewHolder);
        if (this.mAdapterListener != null) {
            this.mAdapterListener.onAttachedToWindow(viewHolder);
        }
        viewHolder.mPresenter.onViewAttachedToWindow(viewHolder.mHolder);
    }

    public final void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mPresenter.onViewDetachedFromWindow(viewHolder.mHolder);
        onDetachedFromWindow(viewHolder);
        if (this.mAdapterListener != null) {
            this.mAdapterListener.onDetachedFromWindow(viewHolder);
        }
    }

    public long getItemId(int position) {
        return this.mAdapter.getId(position);
    }

    public FacetProvider getFacetProvider(int type) {
        return this.mPresenters.get(type);
    }
}
