package com.mediatek.wwtv.tvcenter.epg;

import android.content.Context;
import android.widget.BaseAdapter;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public abstract class EPGBaseAdapter<T> extends BaseAdapter {
    public List<T> group = null;
    public Context mContext;
    public int mItemHeight = 0;

    public EPGBaseAdapter(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getCount() {
        if (this.group == null) {
            return 0;
        }
        return this.group.size();
    }

    public Object getItem(int position) {
        if (this.group == null || position < 0 || position >= this.group.size()) {
            return null;
        }
        MtkLog.d("outofindex", "position is :" + position + "==group.size() is :" + this.group.size());
        return this.group.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean isEmpty() {
        if (this.group == null) {
            return true;
        }
        return this.group.isEmpty();
    }

    public void setGroup(List<T> g) {
        this.group = g;
    }

    public void addGroup(List<T> g) {
        if (this.group != null) {
            this.group.addAll(g);
        }
    }

    public List<T> getGroup() {
        return this.group;
    }

    public int getItemHeight() {
        return this.mItemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.mItemHeight = itemHeight;
    }
}
