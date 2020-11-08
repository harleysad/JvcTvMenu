package android.support.v17.leanback.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@Deprecated
abstract class BaseRowFragment extends Fragment {
    private static final String CURRENT_SELECTED_POSITION = "currentSelectedPosition";
    private ObjectAdapter mAdapter;
    final ItemBridgeAdapter mBridgeAdapter = new ItemBridgeAdapter();
    LateSelectionObserver mLateSelectionObserver = new LateSelectionObserver();
    private boolean mPendingTransitionPrepare;
    private PresenterSelector mPresenterSelector;
    private final OnChildViewHolderSelectedListener mRowSelectedListener = new OnChildViewHolderSelectedListener() {
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder view, int position, int subposition) {
            if (!BaseRowFragment.this.mLateSelectionObserver.mIsLateSelection) {
                BaseRowFragment.this.mSelectedPosition = position;
                BaseRowFragment.this.onRowSelected(parent, view, position, subposition);
            }
        }
    };
    int mSelectedPosition = -1;
    VerticalGridView mVerticalGridView;

    /* access modifiers changed from: package-private */
    public abstract int getLayoutResourceId();

    BaseRowFragment() {
    }

    /* access modifiers changed from: package-private */
    public void onRowSelected(RecyclerView parent, RecyclerView.ViewHolder view, int position, int subposition) {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), container, false);
        this.mVerticalGridView = findGridViewFromRoot(view);
        if (this.mPendingTransitionPrepare) {
            this.mPendingTransitionPrepare = false;
            onTransitionPrepare();
        }
        return view;
    }

    /* access modifiers changed from: package-private */
    public VerticalGridView findGridViewFromRoot(View view) {
        return (VerticalGridView) view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mSelectedPosition = savedInstanceState.getInt(CURRENT_SELECTED_POSITION, -1);
        }
        setAdapterAndSelection();
        this.mVerticalGridView.setOnChildViewHolderSelectedListener(this.mRowSelectedListener);
    }

    private class LateSelectionObserver extends RecyclerView.AdapterDataObserver {
        boolean mIsLateSelection = false;

        LateSelectionObserver() {
        }

        public void onChanged() {
            performLateSelection();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            performLateSelection();
        }

        /* access modifiers changed from: package-private */
        public void startLateSelection() {
            this.mIsLateSelection = true;
            BaseRowFragment.this.mBridgeAdapter.registerAdapterDataObserver(this);
        }

        /* access modifiers changed from: package-private */
        public void performLateSelection() {
            clear();
            if (BaseRowFragment.this.mVerticalGridView != null) {
                BaseRowFragment.this.mVerticalGridView.setSelectedPosition(BaseRowFragment.this.mSelectedPosition);
            }
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            if (this.mIsLateSelection) {
                this.mIsLateSelection = false;
                BaseRowFragment.this.mBridgeAdapter.unregisterAdapterDataObserver(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdapterAndSelection() {
        if (this.mAdapter != null) {
            if (this.mVerticalGridView.getAdapter() != this.mBridgeAdapter) {
                this.mVerticalGridView.setAdapter(this.mBridgeAdapter);
            }
            if (this.mBridgeAdapter.getItemCount() == 0 && this.mSelectedPosition >= 0) {
                this.mLateSelectionObserver.startLateSelection();
            } else if (this.mSelectedPosition >= 0) {
                this.mVerticalGridView.setSelectedPosition(this.mSelectedPosition);
            }
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mLateSelectionObserver.clear();
        this.mVerticalGridView = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SELECTED_POSITION, this.mSelectedPosition);
    }

    public final void setPresenterSelector(PresenterSelector presenterSelector) {
        if (this.mPresenterSelector != presenterSelector) {
            this.mPresenterSelector = presenterSelector;
            updateAdapter();
        }
    }

    public final PresenterSelector getPresenterSelector() {
        return this.mPresenterSelector;
    }

    public final void setAdapter(ObjectAdapter rowsAdapter) {
        if (this.mAdapter != rowsAdapter) {
            this.mAdapter = rowsAdapter;
            updateAdapter();
        }
    }

    public final ObjectAdapter getAdapter() {
        return this.mAdapter;
    }

    public final ItemBridgeAdapter getBridgeAdapter() {
        return this.mBridgeAdapter;
    }

    public void setSelectedPosition(int position) {
        setSelectedPosition(position, true);
    }

    public int getSelectedPosition() {
        return this.mSelectedPosition;
    }

    public void setSelectedPosition(int position, boolean smooth) {
        if (this.mSelectedPosition != position) {
            this.mSelectedPosition = position;
            if (this.mVerticalGridView != null && !this.mLateSelectionObserver.mIsLateSelection) {
                if (smooth) {
                    this.mVerticalGridView.setSelectedPositionSmooth(position);
                } else {
                    this.mVerticalGridView.setSelectedPosition(position);
                }
            }
        }
    }

    public final VerticalGridView getVerticalGridView() {
        return this.mVerticalGridView;
    }

    /* access modifiers changed from: package-private */
    public void updateAdapter() {
        this.mBridgeAdapter.setAdapter(this.mAdapter);
        this.mBridgeAdapter.setPresenter(this.mPresenterSelector);
        if (this.mVerticalGridView != null) {
            setAdapterAndSelection();
        }
    }

    /* access modifiers changed from: package-private */
    public Object getItem(Row row, int position) {
        if (row instanceof ListRow) {
            return ((ListRow) row).getAdapter().get(position);
        }
        return null;
    }

    public boolean onTransitionPrepare() {
        if (this.mVerticalGridView != null) {
            this.mVerticalGridView.setAnimateChildLayout(false);
            this.mVerticalGridView.setScrollEnabled(false);
            return true;
        }
        this.mPendingTransitionPrepare = true;
        return false;
    }

    public void onTransitionStart() {
        if (this.mVerticalGridView != null) {
            this.mVerticalGridView.setPruneChild(false);
            this.mVerticalGridView.setLayoutFrozen(true);
            this.mVerticalGridView.setFocusSearchDisabled(true);
        }
    }

    public void onTransitionEnd() {
        if (this.mVerticalGridView != null) {
            this.mVerticalGridView.setLayoutFrozen(false);
            this.mVerticalGridView.setAnimateChildLayout(true);
            this.mVerticalGridView.setPruneChild(true);
            this.mVerticalGridView.setFocusSearchDisabled(false);
            this.mVerticalGridView.setScrollEnabled(true);
        }
    }

    public void setAlignment(int windowAlignOffsetTop) {
        if (this.mVerticalGridView != null) {
            this.mVerticalGridView.setItemAlignmentOffset(0);
            this.mVerticalGridView.setItemAlignmentOffsetPercent(-1.0f);
            this.mVerticalGridView.setWindowAlignmentOffset(windowAlignOffsetTop);
            this.mVerticalGridView.setWindowAlignmentOffsetPercent(-1.0f);
            this.mVerticalGridView.setWindowAlignment(0);
        }
    }
}
