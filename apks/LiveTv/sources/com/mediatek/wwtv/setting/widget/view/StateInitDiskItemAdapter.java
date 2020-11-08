package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.Core;
import java.util.List;

public class StateInitDiskItemAdapter<T> extends BaseAdapter {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_PVR = 1;
    public static final int TYPE_TIMESHIFT = 2;
    private Context mContext;
    private List<T> mDiskList;
    private final LayoutInflater mInflater;
    private int select = 0;
    private int typeFlag = 0;

    public int getSelect() {
        return this.select;
    }

    public void setSelect(int select2) {
        this.select = select2;
    }

    public StateInitDiskItemAdapter(Context mContext2, List<T> mBtnList) {
        this.mDiskList = mBtnList;
        this.mContext = mContext2;
        this.mInflater = (LayoutInflater) mContext2.getSystemService("layout_inflater");
    }

    public void setGroup(List<T> mBtnList) {
        this.mDiskList = mBtnList;
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
            view = this.mInflater.inflate(R.layout.pvr_timeshfit_deviceitem_layout, (ViewGroup) null);
        } else {
            view = convertView;
        }
        setItemValue(view, (MountPoint) getItem(position), position);
        return view;
    }

    private void setItemValue(View view, MountPoint item, int position) {
        String str;
        String str2;
        TextView label = (TextView) view.findViewById(R.id.disk_label);
        TextView size = (TextView) view.findViewById(R.id.disk_size);
        TextView isTshift = (TextView) view.findViewById(R.id.disk_is_tshift);
        TextView na = (TextView) view.findViewById(R.id.disk_na);
        if (Core.NO_DEVICES.equals(item.mVolumeLabel) || TextUtils.isEmpty(item.mMountPoint)) {
            label.setText(Core.NO_DEVICES);
            size.setVisibility(4);
            isTshift.setVisibility(4);
            na.setVisibility(4);
            return;
        }
        if (item.mVolumeLabel == null) {
            str = "No name";
        } else {
            str = item.mVolumeLabel + " ";
        }
        label.setText(str);
        size.setVisibility(0);
        isTshift.setVisibility(0);
        na.setVisibility(0);
        size.setText(Util.getGBSizeOfDisk(item));
        String isString = String.valueOf(Util.getIsTshift(item));
        Log.d("PVR", isString);
        if (isString.contains("PVR")) {
            isString = isString.replace("PVR", this.mContext.getString(R.string.add_schedule));
        }
        isTshift.setText(isString);
        float speed = DiskSettingSubMenuDialog.getUsbSpeed(item);
        if (speed == 0.0f) {
            str2 = MtkTvRatingConvert2Goo.STR_MPAA_NA;
        } else {
            str2 = String.format("%3.1fMB/S ", new Object[]{Float.valueOf(speed)});
        }
        na.setText(str2);
    }

    public int getTypeFlag() {
        return this.typeFlag;
    }

    public void setTypeFlag(int typeFlag2) {
        this.typeFlag = typeFlag2;
    }
}
