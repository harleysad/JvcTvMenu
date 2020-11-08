package com.mediatek.wwtv.tvcenter.nav.fav;

import java.util.ArrayList;
import java.util.List;

public class ListData<T> {
    public static final int COUNT_EACH_PAGE = 7;
    private static final String TAG = " ListData ";
    private List<T> mAllList = new ArrayList();
    private List<T> mShowList = new ArrayList();
    private int theShowListEndIndex = 6;
    private int theShowListStartIndex = 0;

    public ListData(List<T> mAllDataList, T currentT) {
        if (mAllDataList != null) {
            this.mAllList = mAllDataList;
        }
        if (this.mAllList.size() <= 7) {
            this.theShowListStartIndex = 0;
            this.theShowListEndIndex = this.mAllList.size() - 1;
        } else if (currentT == null || (mAllDataList != null && !mAllDataList.contains(currentT))) {
            this.theShowListStartIndex = 0;
            this.theShowListEndIndex = 6;
        } else {
            this.theShowListStartIndex = this.mAllList.indexOf(currentT);
            this.theShowListEndIndex = (this.theShowListStartIndex + 7) - 1;
        }
    }

    public List<T> getAllData() {
        return this.mAllList;
    }

    public List<T> getPrePageData() {
        if (this.mAllList.size() <= 7) {
            this.theShowListStartIndex = 0;
            this.theShowListEndIndex = this.mAllList.size() - 1;
        } else if (this.theShowListStartIndex == 0) {
            this.theShowListEndIndex = this.mAllList.size() - 1;
            this.theShowListStartIndex = this.mAllList.size() - 7;
        } else {
            this.theShowListEndIndex = this.theShowListStartIndex - 1;
            this.theShowListStartIndex = (((this.theShowListEndIndex + this.mAllList.size()) + 1) - 7) % this.mAllList.size();
        }
        return getShowData();
    }

    public List<T> getNextPageData() {
        if (this.mAllList.size() <= 7) {
            this.theShowListStartIndex = 0;
            this.theShowListEndIndex = this.mAllList.size() - 1;
        } else {
            this.theShowListStartIndex = this.theShowListEndIndex + 1;
            this.theShowListEndIndex = ((this.theShowListStartIndex + 7) - 1) % this.mAllList.size();
        }
        return getShowData();
    }

    public List<T> getShowData(T currentT) {
        if (!this.mShowList.contains(currentT) && this.mAllList.size() != 0 && this.mAllList.contains(currentT)) {
            this.theShowListStartIndex = this.mAllList.indexOf(currentT);
            this.theShowListEndIndex = ((this.theShowListStartIndex + 7) - 1) % this.mAllList.size();
        }
        return getShowData();
    }

    public List<T> getShowData(T currentT, int position) {
        if (this.mAllList.indexOf(currentT) == -1) {
            this.theShowListStartIndex = 0;
            this.theShowListEndIndex = this.theShowListStartIndex + 6;
        } else if (position == -1) {
            this.theShowListStartIndex = this.mAllList.indexOf(currentT);
            this.theShowListEndIndex = ((this.theShowListStartIndex + 7) - 1) % this.mAllList.size();
        } else if (this.mAllList.size() < position) {
            this.theShowListStartIndex = 0;
            this.theShowListEndIndex = this.mAllList.size() - 1;
        } else {
            this.theShowListStartIndex = ((this.mAllList.indexOf(currentT) + this.mAllList.size()) - position) % this.mAllList.size();
            this.theShowListEndIndex = ((this.theShowListStartIndex + 7) - 1) % this.mAllList.size();
        }
        return getShowData();
    }

    public int getIdxInShowList(T t) {
        int index = this.mShowList.indexOf(t);
        if (index != -1 || t == null) {
            return index;
        }
        return this.mShowList.indexOf(findChannelByFrequence(this.mShowList, (float) ((TVChannel) t).getChannelID()));
    }

    private TVChannel findChannelByFrequence(List<T> mAllDataList, float frequence) {
        TVChannel result = null;
        for (T channel : mAllDataList) {
            if (((float) channel.getChannelID()) == frequence) {
                result = channel;
            }
        }
        return result;
    }

    private List<T> getShowData() {
        this.mShowList.clear();
        if (this.mAllList.size() == 0) {
            return this.mShowList;
        }
        if (this.mAllList.size() <= 7) {
            this.mShowList.addAll(this.mAllList);
            return this.mAllList;
        }
        if (this.theShowListStartIndex < this.theShowListEndIndex) {
            for (int i = this.theShowListStartIndex; i < this.theShowListEndIndex + 1; i++) {
                this.mShowList.add(this.mAllList.get(i));
            }
        } else {
            for (int i2 = this.theShowListStartIndex; i2 < this.mAllList.size(); i2++) {
                this.mShowList.add(this.mAllList.get(i2));
            }
            for (int i3 = 0; i3 <= this.theShowListEndIndex; i3++) {
                this.mShowList.add(this.mAllList.get(i3));
            }
        }
        return this.mShowList;
    }

    public int getCount() {
        return this.mAllList.size();
    }

    public void updateAllData(List<T> newAllData) {
        this.mAllList = newAllData;
    }

    public String toString() {
        return this.mAllList + "  " + this.mShowList;
    }
}
