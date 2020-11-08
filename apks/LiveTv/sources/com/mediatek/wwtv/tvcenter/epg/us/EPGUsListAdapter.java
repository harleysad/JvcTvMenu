package com.mediatek.wwtv.tvcenter.epg.us;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class EPGUsListAdapter extends BaseAdapter {
    private List<ListItemData> dataList = new ArrayList();
    private int dayNum;
    private Context mContext;

    public EPGUsListAdapter(Context context, List<ListItemData> dataList2) {
        this.mContext = context;
        this.dataList = dataList2;
    }

    public List<ListItemData> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<ListItemData> dataList2) {
        this.dataList = dataList2;
    }

    public int getDayNum() {
        return this.dayNum;
    }

    public void setDayNum(int dayNum2) {
        this.dayNum = dayNum2;
    }

    public int getCount() {
        if (this.dataList == null) {
            return 0;
        }
        return this.dataList.size();
    }

    public Object getItem(int position) {
        if (this.dataList == null || this.dataList.size() - 1 < position) {
            return null;
        }
        return this.dataList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        MtkLog.e("mListAdapter", "Epg getView:" + position);
        if (this.dataList == null || this.dataList.size() <= 0) {
            return convertView;
        }
        ListItemData itemData = this.dataList.get(position);
        if (holder.listItemView == null) {
            holder.listItemView = new ListItemView(this.mContext);
        }
        holder.listItemView.setAdapter(itemData);
        return holder.listItemView;
    }

    class ViewHolder {
        ListItemView listItemView;

        ViewHolder() {
        }
    }
}
