package android.support.v17.leanback.widget;

import android.support.v17.leanback.widget.StaggeredGrid;

final class StaggeredGridDefault extends StaggeredGrid {
    StaggeredGridDefault() {
    }

    /* access modifiers changed from: package-private */
    public int getRowMax(int rowIndex) {
        StaggeredGrid.Location loc;
        if (this.mFirstVisibleIndex < 0) {
            return Integer.MIN_VALUE;
        }
        if (!this.mReversedFlow) {
            int edge = this.mProvider.getEdge(this.mLastVisibleIndex);
            StaggeredGrid.Location loc2 = getLocation(this.mLastVisibleIndex);
            if (loc2.row != rowIndex) {
                int i = this.mLastVisibleIndex;
                while (true) {
                    i--;
                    if (i < getFirstIndex()) {
                        break;
                    }
                    edge -= loc2.offset;
                    loc2 = getLocation(i);
                    if (loc2.row == rowIndex) {
                        return loc2.size + edge;
                    }
                }
            } else {
                return loc2.size + edge;
            }
        } else {
            int edge2 = this.mProvider.getEdge(this.mFirstVisibleIndex);
            if (getLocation(this.mFirstVisibleIndex).row == rowIndex) {
                return edge2;
            }
            int i2 = this.mFirstVisibleIndex;
            do {
                i2++;
                if (i2 <= getLastIndex()) {
                    loc = getLocation(i2);
                    edge2 += loc.offset;
                }
            } while (loc.row != rowIndex);
            return edge2;
        }
        return Integer.MIN_VALUE;
    }

    /* access modifiers changed from: package-private */
    public int getRowMin(int rowIndex) {
        StaggeredGrid.Location loc;
        if (this.mFirstVisibleIndex < 0) {
            return Integer.MAX_VALUE;
        }
        if (this.mReversedFlow) {
            int edge = this.mProvider.getEdge(this.mLastVisibleIndex);
            StaggeredGrid.Location loc2 = getLocation(this.mLastVisibleIndex);
            if (loc2.row != rowIndex) {
                int i = this.mLastVisibleIndex;
                while (true) {
                    i--;
                    if (i < getFirstIndex()) {
                        break;
                    }
                    edge -= loc2.offset;
                    loc2 = getLocation(i);
                    if (loc2.row == rowIndex) {
                        return edge - loc2.size;
                    }
                }
            } else {
                return edge - loc2.size;
            }
        } else {
            int edge2 = this.mProvider.getEdge(this.mFirstVisibleIndex);
            if (getLocation(this.mFirstVisibleIndex).row == rowIndex) {
                return edge2;
            }
            int i2 = this.mFirstVisibleIndex;
            do {
                i2++;
                if (i2 <= getLastIndex()) {
                    loc = getLocation(i2);
                    edge2 += loc.offset;
                }
            } while (loc.row != rowIndex);
            return edge2;
        }
        return Integer.MAX_VALUE;
    }

    public int findRowMax(boolean findLarge, int indexLimit, int[] indices) {
        int value;
        int edge = this.mProvider.getEdge(indexLimit);
        StaggeredGrid.Location loc = getLocation(indexLimit);
        int row = loc.row;
        int index = indexLimit;
        int visitedRows = 1;
        int visitRow = row;
        if (this.mReversedFlow) {
            value = edge;
            int i = indexLimit + 1;
            while (visitedRows < this.mNumRows && i <= this.mLastVisibleIndex) {
                StaggeredGrid.Location loc2 = getLocation(i);
                edge += loc2.offset;
                if (loc2.row != visitRow) {
                    visitRow = loc2.row;
                    visitedRows++;
                    if (findLarge) {
                        if (edge <= value) {
                        }
                    } else if (edge >= value) {
                    }
                    row = visitRow;
                    value = edge;
                    index = i;
                }
                i++;
            }
        } else {
            value = this.mProvider.getSize(indexLimit) + edge;
            int i2 = indexLimit - 1;
            while (visitedRows < this.mNumRows && i2 >= this.mFirstVisibleIndex) {
                edge -= loc.offset;
                loc = getLocation(i2);
                if (loc.row != visitRow) {
                    visitRow = loc.row;
                    visitedRows++;
                    int newValue = this.mProvider.getSize(i2) + edge;
                    if (findLarge) {
                        if (newValue <= value) {
                        }
                    } else if (newValue >= value) {
                    }
                    row = visitRow;
                    value = newValue;
                    index = i2;
                }
                i2--;
            }
        }
        if (indices != null) {
            indices[0] = row;
            indices[1] = index;
        }
        return value;
    }

    public int findRowMin(boolean findLarge, int indexLimit, int[] indices) {
        int value;
        int edge = this.mProvider.getEdge(indexLimit);
        StaggeredGrid.Location loc = getLocation(indexLimit);
        int row = loc.row;
        int index = indexLimit;
        int visitedRows = 1;
        int visitRow = row;
        if (this.mReversedFlow) {
            value = edge - this.mProvider.getSize(indexLimit);
            int i = indexLimit - 1;
            while (visitedRows < this.mNumRows && i >= this.mFirstVisibleIndex) {
                edge -= loc.offset;
                loc = getLocation(i);
                if (loc.row != visitRow) {
                    visitRow = loc.row;
                    visitedRows++;
                    int newValue = edge - this.mProvider.getSize(i);
                    if (findLarge) {
                        if (newValue <= value) {
                        }
                    } else if (newValue >= value) {
                    }
                    value = newValue;
                    row = visitRow;
                    index = i;
                }
                i--;
            }
        } else {
            value = edge;
            int i2 = indexLimit + 1;
            while (visitedRows < this.mNumRows && i2 <= this.mLastVisibleIndex) {
                StaggeredGrid.Location loc2 = getLocation(i2);
                edge += loc2.offset;
                if (loc2.row != visitRow) {
                    visitRow = loc2.row;
                    visitedRows++;
                    if (findLarge) {
                        if (edge <= value) {
                        }
                    } else if (edge >= value) {
                    }
                    value = edge;
                    row = visitRow;
                    index = i2;
                }
                i2++;
            }
        }
        if (indices != null) {
            indices[0] = row;
            indices[1] = index;
        }
        return value;
    }

    private int findRowEdgeLimitSearchIndex(boolean append) {
        boolean wrapped = false;
        if (append) {
            for (int index = this.mLastVisibleIndex; index >= this.mFirstVisibleIndex; index--) {
                int row = getLocation(index).row;
                if (row == 0) {
                    wrapped = true;
                } else if (wrapped && row == this.mNumRows - 1) {
                    return index;
                }
            }
            return -1;
        }
        for (int index2 = this.mFirstVisibleIndex; index2 <= this.mLastVisibleIndex; index2++) {
            int row2 = getLocation(index2).row;
            if (row2 == this.mNumRows - 1) {
                wrapped = true;
            } else if (wrapped && row2 == 0) {
                return index2;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public boolean appendVisibleItemsWithoutCache(int toLimit, boolean oneColumnMode) {
        int edgeLimit;
        int edgeLimitSearchIndex;
        int rowIndex;
        int itemIndex;
        int rowIndex2;
        int location;
        int edgeLimit2;
        int findRowMax;
        int count = this.mProvider.getCount();
        if (this.mLastVisibleIndex < 0) {
            itemIndex = this.mStartIndex != -1 ? this.mStartIndex : 0;
            rowIndex = (this.mLocations.size() > 0 ? getLocation(getLastIndex()).row + 1 : itemIndex) % this.mNumRows;
            edgeLimit = 0;
            edgeLimitSearchIndex = 0;
        } else if (this.mLastVisibleIndex < getLastIndex()) {
            return false;
        } else {
            itemIndex = this.mLastVisibleIndex + 1;
            rowIndex = getLocation(this.mLastVisibleIndex).row;
            int edgeLimitSearchIndex2 = findRowEdgeLimitSearchIndex(true);
            if (edgeLimitSearchIndex2 < 0) {
                edgeLimit2 = Integer.MIN_VALUE;
                for (int i = 0; i < this.mNumRows; i++) {
                    edgeLimit2 = this.mReversedFlow ? getRowMin(i) : getRowMax(i);
                    if (edgeLimit2 != Integer.MIN_VALUE) {
                        break;
                    }
                }
            } else {
                if (this.mReversedFlow != 0) {
                    findRowMax = findRowMin(false, edgeLimitSearchIndex2, (int[]) null);
                } else {
                    findRowMax = findRowMax(true, edgeLimitSearchIndex2, (int[]) null);
                }
                edgeLimit2 = findRowMax;
            }
            edgeLimit = edgeLimit2;
            if (this.mReversedFlow == 0 ? getRowMax(rowIndex) >= edgeLimit : getRowMin(rowIndex) <= edgeLimit) {
                rowIndex++;
                if (rowIndex == this.mNumRows) {
                    rowIndex = 0;
                    edgeLimit = this.mReversedFlow ? findRowMin(false, (int[]) null) : findRowMax(true, (int[]) null);
                }
            }
            edgeLimitSearchIndex = 1;
        }
        int edgeLimitIsValid = edgeLimitSearchIndex;
        int itemIndex2 = itemIndex;
        boolean filledOne = false;
        loop1:
        while (true) {
            if (rowIndex < this.mNumRows) {
                if (itemIndex2 == count || (!oneColumnMode && checkAppendOverLimit(toLimit))) {
                    return filledOne;
                }
                int location2 = this.mReversedFlow ? getRowMin(rowIndex) : getRowMax(rowIndex);
                if (location2 != Integer.MAX_VALUE && location2 != Integer.MIN_VALUE) {
                    location = location2 + (this.mReversedFlow ? -this.mSpacing : this.mSpacing);
                } else if (rowIndex == 0) {
                    location = this.mReversedFlow ? getRowMin(this.mNumRows - 1) : getRowMax(this.mNumRows - 1);
                    if (!(location == Integer.MAX_VALUE || location == Integer.MIN_VALUE)) {
                        location += this.mReversedFlow ? -this.mSpacing : this.mSpacing;
                    }
                } else {
                    location = this.mReversedFlow ? getRowMax(rowIndex - 1) : getRowMin(rowIndex - 1);
                }
                int itemIndex3 = itemIndex2 + 1;
                int size = appendVisibleItemToRow(itemIndex2, rowIndex, location);
                filledOne = true;
                if (edgeLimitIsValid != 0) {
                    while (true) {
                        if (!this.mReversedFlow) {
                            if (location + size >= edgeLimit) {
                                break;
                            }
                        } else if (location - size <= edgeLimit) {
                            break;
                        }
                        if (itemIndex3 == count || (!oneColumnMode && checkAppendOverLimit(toLimit))) {
                            return true;
                        }
                        location += this.mReversedFlow ? (-size) - this.mSpacing : this.mSpacing + size;
                        size = appendVisibleItemToRow(itemIndex3, rowIndex, location);
                        itemIndex3++;
                    }
                } else {
                    edgeLimitIsValid = 1;
                    edgeLimit = this.mReversedFlow ? getRowMin(rowIndex) : getRowMax(rowIndex);
                }
                itemIndex2 = itemIndex3;
                rowIndex2 = rowIndex + 1;
            } else if (oneColumnMode) {
                return filledOne;
            } else {
                edgeLimit = this.mReversedFlow ? findRowMin(false, (int[]) null) : findRowMax(true, (int[]) null);
                rowIndex2 = 0;
            }
        }
        return filledOne;
    }

    /* access modifiers changed from: protected */
    public boolean prependVisibleItemsWithoutCache(int toLimit, boolean oneColumnMode) {
        int edgeLimit;
        int edgeLimitSearchIndex;
        int rowIndex;
        int itemIndex;
        int rowIndex2;
        int location;
        int i;
        if (this.mFirstVisibleIndex < 0) {
            itemIndex = this.mStartIndex != -1 ? this.mStartIndex : 0;
            rowIndex = (this.mLocations.size() > 0 ? (getLocation(getFirstIndex()).row + this.mNumRows) - 1 : itemIndex) % this.mNumRows;
            edgeLimit = 0;
            edgeLimitSearchIndex = 0;
        } else if (this.mFirstVisibleIndex > getFirstIndex()) {
            return false;
        } else {
            itemIndex = this.mFirstVisibleIndex - 1;
            rowIndex = getLocation(this.mFirstVisibleIndex).row;
            int edgeLimitSearchIndex2 = findRowEdgeLimitSearchIndex(false);
            if (edgeLimitSearchIndex2 < 0) {
                rowIndex--;
                edgeLimit = Integer.MAX_VALUE;
                for (int i2 = this.mNumRows - 1; i2 >= 0; i2--) {
                    edgeLimit = this.mReversedFlow ? getRowMax(i2) : getRowMin(i2);
                    if (edgeLimit != Integer.MAX_VALUE) {
                        break;
                    }
                }
            } else if (this.mReversedFlow != 0) {
                edgeLimit = findRowMax(true, edgeLimitSearchIndex2, (int[]) null);
            } else {
                edgeLimit = findRowMin(false, edgeLimitSearchIndex2, (int[]) null);
            }
            if (!this.mReversedFlow ? getRowMin(rowIndex) <= edgeLimit : getRowMax(rowIndex) >= edgeLimit) {
                rowIndex--;
                if (rowIndex < 0) {
                    rowIndex = this.mNumRows - 1;
                    if (this.mReversedFlow) {
                        i = findRowMax(true, (int[]) null);
                    } else {
                        i = findRowMin(false, (int[]) null);
                    }
                    edgeLimit = i;
                }
            }
            edgeLimitSearchIndex = 1;
        }
        int edgeLimitIsValid = edgeLimitSearchIndex;
        int itemIndex2 = itemIndex;
        boolean filledOne = false;
        loop1:
        while (true) {
            if (rowIndex >= 0) {
                if (itemIndex2 < 0 || (!oneColumnMode && checkPrependOverLimit(toLimit))) {
                    return filledOne;
                }
                int location2 = this.mReversedFlow ? getRowMax(rowIndex) : getRowMin(rowIndex);
                if (location2 != Integer.MAX_VALUE && location2 != Integer.MIN_VALUE) {
                    location = location2 + (this.mReversedFlow ? this.mSpacing : -this.mSpacing);
                } else if (rowIndex == this.mNumRows - 1) {
                    location = this.mReversedFlow ? getRowMax(0) : getRowMin(0);
                    if (!(location == Integer.MAX_VALUE || location == Integer.MIN_VALUE)) {
                        location += this.mReversedFlow ? this.mSpacing : -this.mSpacing;
                    }
                } else {
                    location = this.mReversedFlow ? getRowMin(rowIndex + 1) : getRowMax(rowIndex + 1);
                }
                int itemIndex3 = itemIndex2 - 1;
                int size = prependVisibleItemToRow(itemIndex2, rowIndex, location);
                filledOne = true;
                if (edgeLimitIsValid != 0) {
                    while (true) {
                        if (!this.mReversedFlow) {
                            if (location - size <= edgeLimit) {
                                break;
                            }
                        } else if (location + size >= edgeLimit) {
                            break;
                        }
                        if (itemIndex3 < 0 || (!oneColumnMode && checkPrependOverLimit(toLimit))) {
                            return true;
                        }
                        location += this.mReversedFlow ? this.mSpacing + size : (-size) - this.mSpacing;
                        size = prependVisibleItemToRow(itemIndex3, rowIndex, location);
                        itemIndex3--;
                    }
                } else {
                    edgeLimitIsValid = 1;
                    edgeLimit = this.mReversedFlow ? getRowMax(rowIndex) : getRowMin(rowIndex);
                }
                itemIndex2 = itemIndex3;
                rowIndex2 = rowIndex - 1;
            } else if (oneColumnMode) {
                return filledOne;
            } else {
                edgeLimit = this.mReversedFlow ? findRowMax(true, (int[]) null) : findRowMin(false, (int[]) null);
                rowIndex2 = this.mNumRows - 1;
            }
        }
        return filledOne;
    }
}
