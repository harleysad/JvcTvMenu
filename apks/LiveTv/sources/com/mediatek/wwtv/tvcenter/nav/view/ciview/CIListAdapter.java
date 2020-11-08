package com.mediatek.wwtv.tvcenter.nav.view.ciview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class CIListAdapter extends BaseAdapter {
    private static final String TAG = "CIListAdapter";
    Context mContext;
    String[] mData;
    ViewHolder mViewHolder;

    public CIListAdapter(Context context) {
        this.mContext = context;
    }

    public CIListAdapter(Context context, String[] data) {
        this.mContext = context;
        this.mData = data;
    }

    public int getCount() {
        if (this.mData == null) {
            return 0;
        }
        return this.mData.length;
    }

    public Object getItem(int position) {
        if (this.mData == null) {
            return null;
        }
        return this.mData[position];
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MtkLog.d(TAG, "getView-->" + position);
        if (convertView == null) {
            this.mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.menu_ci_main_item, (ViewGroup) null);
            this.mViewHolder.mTextView = (TextView) convertView.findViewById(R.id.menu_ci_main_item);
            convertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (this.mData != null && this.mData.length > 0 && this.mData[position] != null && this.mData[position].length() > 0) {
            MtkLog.d(TAG, "setText:" + this.mData[position].trim());
            this.mViewHolder.mTextView.setText(this.mData[position].trim());
        }
        return convertView;
    }

    class ViewHolder {
        TextView mTextView;

        ViewHolder() {
        }
    }

    public String[] getCIData() {
        return this.mData;
    }

    public void setCIGroup(String[] data) {
        MtkLog.d(TAG, "setCIGroup");
        this.mData = data;
    }
}
