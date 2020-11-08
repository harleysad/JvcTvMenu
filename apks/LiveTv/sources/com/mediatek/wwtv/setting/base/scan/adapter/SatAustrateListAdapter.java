package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public class SatAustrateListAdapter extends BaseAdapter {
    private static final String TAG = "SatAustrateListAdapter";
    LayoutInflater inflater;
    private final Context mContext;
    public String[] mList;

    public SatAustrateListAdapter(Context mContext2, String[] mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
        this.inflater = LayoutInflater.from(mContext2);
    }

    public int getCount() {
        return this.mList.length;
    }

    public Object getItem(int arg0) {
        return this.mList[arg0];
    }

    public long getItemId(int arg0) {
        return (long) arg0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.region_list_item, parent, false);
            holder.region_name = (TextView) convertView.findViewById(R.id.region_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.region_name.setText(this.mList[position]);
        return convertView;
    }

    class ViewHolder {
        TextView region_name;

        ViewHolder() {
        }
    }
}
