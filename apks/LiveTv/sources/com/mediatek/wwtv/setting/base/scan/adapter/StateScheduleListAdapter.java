package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import java.util.List;

public class StateScheduleListAdapter<T> extends BaseAdapter {
    private final Context mContext;
    private final List<T> mDiskList;
    private final LayoutInflater mInflater;

    public StateScheduleListAdapter(Context mContext2, List<T> itemList) {
        this.mDiskList = itemList;
        this.mContext = mContext2;
        this.mInflater = (LayoutInflater) mContext2.getSystemService("layout_inflater");
    }

    public int getCount() {
        return this.mDiskList.size();
    }

    public T getItem(int position) {
        return this.mDiskList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = this.mInflater.inflate(R.layout.pvr_tshfit_schudule_item_layout, (ViewGroup) null);
        } else {
            view = convertView;
        }
        setItemValue(view, (MtkTvBookingBase) getItem(position));
        return view;
    }

    private void setItemValue(View view, MtkTvBookingBase item) {
        TextView label = (TextView) view.findViewById(R.id.schedule_channel_name);
        if (item.getSourceType() == 0) {
            label.setText("CH" + item.getEventTitle());
        } else {
            label.setVisibility(8);
        }
        ((TextView) view.findViewById(R.id.schedule_date)).setText(Util.longStrToTimeStrN(Long.valueOf(item.getRecordStartTime() * 1000)));
        ((TextView) view.findViewById(R.id.schedule_duration)).setText(Util.longToHrMinN(Long.valueOf(item.getRecordDuration())));
        TextView repeatType = (TextView) view.findViewById(R.id.schedule_internel);
        String[] repeat = this.mContext.getResources().getStringArray(R.array.pvr_tshift_repeat_type);
        int i = 0;
        if (item.getRepeatMode() == 128) {
            repeatType.setText(repeat[0]);
        } else if (item.getRepeatMode() == 0) {
            repeatType.setText(repeat[2]);
        } else {
            repeatType.setText(repeat[1]);
        }
        TextView scheduleType = (TextView) view.findViewById(R.id.schedule_notification);
        String[] schedule = this.mContext.getResources().getStringArray(R.array.pvr_tshift_schedule_type);
        if (item.getRecordMode() != 2) {
            i = item.getRecordMode();
        }
        scheduleType.setText(schedule[i]);
    }
}
