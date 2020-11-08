package android.support.v17.leanback.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.CircularIntArray;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import java.io.PrintWriter;
import java.util.Arrays;

abstract class Grid {
    public static final int START_DEFAULT = -1;
    protected int mFirstVisibleIndex = -1;
    protected int mLastVisibleIndex = -1;
    protected int mNumRows;
    protected Provider mProvider;
    protected boolean mReversedFlow;
    protected int mSpacing;
    protected int mStartIndex = -1;
    Object[] mTmpItem = new Object[1];
    protected CircularIntArray[] mTmpItemPositionsInRows;

    public interface Provider {
        void addItem(Object obj, int i, int i2, int i3, int i4);

        int createItem(int i, boolean z, Object[] objArr, boolean z2);

        int getCount();

        int getEdge(int i);

        int getMinIndex();

        int getSize(int i);

        void removeItem(int i);
    }

    /* access modifiers changed from: protected */
    public abstract boolean appendVisibleItems(int i, boolean z);

    public abstract void debugPrint(PrintWriter printWriter);

    /* access modifiers changed from: protected */
    public abstract int findRowMax(boolean z, int i, int[] iArr);

    /* access modifiers changed from: protected */
    public abstract int findRowMin(boolean z, int i, int[] iArr);

    public abstract CircularIntArray[] getItemPositionsInRows(int i, int i2);

    public abstract Location getLocation(int i);

    /* access modifiers changed from: protected */
    public abstract boolean prependVisibleItems(int i, boolean z);

    Grid() {
    }

    public static class Location {
        public int row;

        public Location(int row2) {
            this.row = row2;
        }
    }

    public static Grid createGrid(int rows) {
        if (rows == 1) {
            return new SingleRow();
        }
        Grid grid = new StaggeredGridDefault();
        grid.setNumRows(rows);
        return grid;
    }

    public final void setSpacing(int spacing) {
        this.mSpacing = spacing;
    }

    public final void setReversedFlow(boolean reversedFlow) {
        this.mReversedFlow = reversedFlow;
    }

    public boolean isReversedFlow() {
        return this.mReversedFlow;
    }

    public void setProvider(Provider provider) {
        this.mProvider = provider;
    }

    public void setStart(int startIndex) {
        this.mStartIndex = startIndex;
    }

    public int getNumRows() {
        return this.mNumRows;
    }

    /* access modifiers changed from: package-private */
    public void setNumRows(int numRows) {
        if (numRows <= 0) {
            throw new IllegalArgumentException();
        } else if (this.mNumRows != numRows) {
            this.mNumRows = numRows;
            this.mTmpItemPositionsInRows = new CircularIntArray[this.mNumRows];
            for (int i = 0; i < this.mNumRows; i++) {
                this.mTmpItemPositionsInRows[i] = new CircularIntArray();
            }
        }
    }

    public final int getFirstVisibleIndex() {
        return this.mFirstVisibleIndex;
    }

    public final int getLastVisibleIndex() {
        return this.mLastVisibleIndex;
    }

    public void resetVisibleIndex() {
        this.mLastVisibleIndex = -1;
        this.mFirstVisibleIndex = -1;
    }

    public void invalidateItemsAfter(int index) {
        if (index >= 0 && this.mLastVisibleIndex >= 0) {
            if (this.mLastVisibleIndex >= index) {
                this.mLastVisibleIndex = index - 1;
            }
            resetVisibleIndexIfEmpty();
            if (getFirstVisibleIndex() < 0) {
                setStart(index);
            }
        }
    }

    public final int getRowIndex(int index) {
        Location location = getLocation(index);
        if (location == null) {
            return -1;
        }
        return location.row;
    }

    public final int findRowMin(boolean findLarge, @Nullable int[] indices) {
        return findRowMin(findLarge, this.mReversedFlow ? this.mLastVisibleIndex : this.mFirstVisibleIndex, indices);
    }

    public final int findRowMax(boolean findLarge, @Nullable int[] indices) {
        return findRowMax(findLarge, this.mReversedFlow ? this.mFirstVisibleIndex : this.mLastVisibleIndex, indices);
    }

    /* access modifiers changed from: protected */
    public final boolean checkAppendOverLimit(int toLimit) {
        if (this.mLastVisibleIndex < 0) {
            return false;
        }
        if (this.mReversedFlow) {
            if (findRowMin(true, (int[]) null) > this.mSpacing + toLimit) {
                return false;
            }
        } else if (findRowMax(false, (int[]) null) < toLimit - this.mSpacing) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public final boolean checkPrependOverLimit(int toLimit) {
        if (this.mLastVisibleIndex < 0) {
            return false;
        }
        if (this.mReversedFlow) {
            if (findRowMax(false, (int[]) null) < toLimit - this.mSpacing) {
                return false;
            }
        } else if (findRowMin(true, (int[]) null) > this.mSpacing + toLimit) {
            return false;
        }
        return true;
    }

    public final CircularIntArray[] getItemPositionsInRows() {
        return getItemPositionsInRows(getFirstVisibleIndex(), getLastVisibleIndex());
    }

    public final boolean prependOneColumnVisibleItems() {
        return prependVisibleItems(this.mReversedFlow ? Integer.MIN_VALUE : Integer.MAX_VALUE, true);
    }

    public final void prependVisibleItems(int toLimit) {
        prependVisibleItems(toLimit, false);
    }

    public boolean appendOneColumnVisibleItems() {
        return appendVisibleItems(this.mReversedFlow ? Integer.MAX_VALUE : Integer.MIN_VALUE, true);
    }

    public final void appendVisibleItems(int toLimit) {
        appendVisibleItems(toLimit, false);
    }

    public void removeInvisibleItemsAtEnd(int aboveIndex, int toLimit) {
        while (this.mLastVisibleIndex >= this.mFirstVisibleIndex && this.mLastVisibleIndex > aboveIndex) {
            boolean offEnd = false;
            if (this.mReversedFlow ? this.mProvider.getEdge(this.mLastVisibleIndex) <= toLimit : this.mProvider.getEdge(this.mLastVisibleIndex) >= toLimit) {
                offEnd = true;
            }
            if (!offEnd) {
                break;
            }
            this.mProvider.removeItem(this.mLastVisibleIndex);
            this.mLastVisibleIndex--;
        }
        resetVisibleIndexIfEmpty();
    }

    public void removeInvisibleItemsAtFront(int belowIndex, int toLimit) {
        while (this.mLastVisibleIndex >= this.mFirstVisibleIndex && this.mFirstVisibleIndex < belowIndex) {
            int size = this.mProvider.getSize(this.mFirstVisibleIndex);
            boolean offFront = false;
            if (this.mReversedFlow ? this.mProvider.getEdge(this.mFirstVisibleIndex) - size >= toLimit : this.mProvider.getEdge(this.mFirstVisibleIndex) + size <= toLimit) {
                offFront = true;
            }
            if (!offFront) {
                break;
            }
            this.mProvider.removeItem(this.mFirstVisibleIndex);
            this.mFirstVisibleIndex++;
        }
        resetVisibleIndexIfEmpty();
    }

    private void resetVisibleIndexIfEmpty() {
        if (this.mLastVisibleIndex < this.mFirstVisibleIndex) {
            resetVisibleIndex();
        }
    }

    public void fillDisappearingItems(int[] positions, int positionsLength, SparseIntArray positionToRow) {
        int edge;
        int edge2;
        int edge3;
        int i;
        int[] iArr = positions;
        int i2 = positionsLength;
        SparseIntArray sparseIntArray = positionToRow;
        int lastPos = getLastVisibleIndex();
        int resultSearchLast = lastPos >= 0 ? Arrays.binarySearch(iArr, 0, i2, lastPos) : 0;
        if (resultSearchLast < 0) {
            int firstDisappearingIndex = (-resultSearchLast) - 1;
            if (this.mReversedFlow) {
                edge3 = (this.mProvider.getEdge(lastPos) - this.mProvider.getSize(lastPos)) - this.mSpacing;
            } else {
                edge3 = this.mProvider.getEdge(lastPos) + this.mProvider.getSize(lastPos) + this.mSpacing;
            }
            int edge4 = edge3;
            for (int i3 = firstDisappearingIndex; i3 < i2; i3++) {
                int disappearingIndex = iArr[i3];
                int disappearingRow = sparseIntArray.get(disappearingIndex);
                if (disappearingRow < 0) {
                    disappearingRow = 0;
                }
                int size = this.mProvider.createItem(disappearingIndex, true, this.mTmpItem, true);
                int i4 = disappearingIndex;
                this.mProvider.addItem(this.mTmpItem[0], disappearingIndex, size, disappearingRow, edge4);
                if (this.mReversedFlow) {
                    i = (edge4 - size) - this.mSpacing;
                } else {
                    i = edge4 + size + this.mSpacing;
                }
                edge4 = i;
            }
        }
        int firstDisappearingIndex2 = getFirstVisibleIndex();
        int resultSearchFirst = firstDisappearingIndex2 >= 0 ? Arrays.binarySearch(iArr, 0, i2, firstDisappearingIndex2) : 0;
        if (resultSearchFirst < 0) {
            int firstDisappearingIndex3 = (-resultSearchFirst) - 2;
            if (this.mReversedFlow) {
                edge = this.mProvider.getEdge(firstDisappearingIndex2);
            } else {
                edge = this.mProvider.getEdge(firstDisappearingIndex2);
            }
            int edge5 = edge;
            for (int i5 = firstDisappearingIndex3; i5 >= 0; i5--) {
                int disappearingIndex2 = iArr[i5];
                int disappearingRow2 = sparseIntArray.get(disappearingIndex2);
                if (disappearingRow2 < 0) {
                    disappearingRow2 = 0;
                }
                int disappearingRow3 = disappearingRow2;
                int size2 = this.mProvider.createItem(disappearingIndex2, false, this.mTmpItem, true);
                if (this.mReversedFlow) {
                    edge2 = this.mSpacing + edge5 + size2;
                } else {
                    edge2 = (edge5 - this.mSpacing) - size2;
                }
                edge5 = edge2;
                int i6 = disappearingIndex2;
                this.mProvider.addItem(this.mTmpItem[0], disappearingIndex2, size2, disappearingRow3, edge5);
            }
        }
    }

    public void collectAdjacentPrefetchPositions(int fromLimit, int da, @NonNull RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
    }
}
