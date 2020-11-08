package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import java.util.ArrayList;

public class RegionListAdapter extends BaseAdapter {
    private static final String TAG = "RegionListAdapter";
    LayoutInflater inflater;
    private final Context mContext;
    public ArrayList<TIFChannelInfo> mList;

    public RegionListAdapter(Context mContext2, ArrayList<TIFChannelInfo> mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
        this.inflater = LayoutInflater.from(mContext2);
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int arg0) {
        return this.mList.get(arg0);
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
        String svcProName = "¡ª¡ª¡ª¡ª";
        MtkTvChannelInfoBase channelInfoBase = this.mList.get(position).mMtkTvChannelInfo;
        if (channelInfoBase instanceof MtkTvDvbChannelInfo) {
            svcProName = ((MtkTvDvbChannelInfo) channelInfoBase).getSvcProName();
            MtkLog.d(TAG, "svcProName" + svcProName);
        }
        holder.region_name.setText(getServiceShowName(svcProName));
        MtkLog.d(TAG, "showName" + getServiceShowName(svcProName));
        return convertView;
    }

    class ViewHolder {
        TextView region_name;

        ViewHolder() {
        }
    }

    public String getServiceShowName(String name) {
        String wien = this.mContext.getResources().getString(R.string.wueb);
        String niederosterreich = this.mContext.getResources().getString(R.string.niederosterreich);
        String burgenland = this.mContext.getResources().getString(R.string.burgenland);
        String oberosterreich = this.mContext.getResources().getString(R.string.oberosterreich);
        String salzburg = this.mContext.getResources().getString(R.string.salzburg);
        String tirol = this.mContext.getResources().getString(R.string.tirol);
        String vorarlberg = this.mContext.getResources().getString(R.string.vorarlberg);
        String steiermark = this.mContext.getResources().getString(R.string.steiermark);
        String karntern = this.mContext.getResources().getString(R.string.karntern);
        if (name.equals("ORF [Region 1]")) {
            return wien;
        }
        if (name.equals("ORF [Region 2]")) {
            return niederosterreich;
        }
        if (name.equals("ORF [Region 3]")) {
            return burgenland;
        }
        if (name.equals("ORF [Region 4]")) {
            return oberosterreich;
        }
        if (name.equals("ORF [Region 5]")) {
            return salzburg;
        }
        if (name.equals("ORF [Region 6]")) {
            return tirol;
        }
        if (name.equals("ORF [Region 7]")) {
            return vorarlberg;
        }
        if (name.equals("ORF [Region 8]")) {
            return steiermark;
        }
        if (name.equals("ORF [Region 9]")) {
            return karntern;
        }
        return name;
    }
}
