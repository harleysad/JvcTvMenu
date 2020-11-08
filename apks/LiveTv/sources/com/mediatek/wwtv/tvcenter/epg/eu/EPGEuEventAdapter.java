package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGBaseAdapter;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;

public class EPGEuEventAdapter extends EPGBaseAdapter<EPGProgramInfo> {
    private static final String TAG = "EPGEuEventAdapter";
    private Drawable mAnalogIcon;

    public EPGEuEventAdapter(Context context) {
        super(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.epg_eu_2nd_listitem_textview, (ViewGroup) null);
            mViewHolder.llayoutGroup = (LinearLayout) convertView.findViewById(R.id.epg_2nd_list_item);
            mViewHolder.time = (TextView) convertView.findViewById(R.id.epg_2nd_list_item_time);
            mViewHolder.title = (TextView) convertView.findViewById(R.id.epg_2nd_list_item_title);
            mViewHolder.llayoutGroup.setLayoutParams(new AbsListView.LayoutParams(-1, this.mItemHeight));
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        EPGProgramInfo programInfo = (EPGProgramInfo) this.group.get(position);
        boolean highlight = highlightEpgType(programInfo);
        MtkLog.d(TAG, "highlight=" + highlight);
        mViewHolder.llayoutGroup.setBackgroundResource(highlight ? R.drawable.epg_2nd_event_highlight_bg : R.drawable.epg_2nd_event_bg);
        TextView textView = mViewHolder.time;
        textView.setText(programInfo.getmStartTimeStr() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + programInfo.getmEndTimeStr());
        mViewHolder.title.setText(TextUtils.isEmpty(programInfo.getmTitle()) ? this.mContext.getResources().getString(R.string.nav_epg_no_program_title) : programInfo.getmTitle());
        return convertView;
    }

    class ViewHolder {
        LinearLayout llayoutGroup;
        TextView time;
        TextView title;

        ViewHolder() {
        }
    }

    private boolean highlightEpgType(EPGProgramInfo programInfo) {
        int mainType = programInfo.getMainType();
        int subType = programInfo.getSubType();
        MtkLog.d(TAG, "mainType=" + mainType + ",subType=" + subType);
        if (mainType < 1) {
            return false;
        }
        int mainTypeLength = DataReader.getInstance(this.mContext).getMainType().length;
        MtkLog.d(TAG, "mainTypeLength=" + mainTypeLength);
        if (mainType >= mainTypeLength) {
            return false;
        }
        String mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType - 1];
        String subStr = null;
        if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
            subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
        }
        MtkLog.d(TAG, "mainStr=" + mainStr + ",subStr=" + subStr);
        if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && !subHasSelected(mainType)) {
            MtkLog.d(TAG, "subHasSelected");
            return true;
        } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) || !thisSubSelected(mainType, subType)) {
            return false;
        } else {
            MtkLog.d(TAG, "thisSubSelected");
            return true;
        }
    }

    private boolean subHasSelected(int mainTypeIndex) {
        String[] subType = DataReader.getInstance(this.mContext).getSubType()[mainTypeIndex];
        if (subType != null) {
            for (String readBooleanValue : subType) {
                if (SaveValue.getInstance(this.mContext).readBooleanValue(readBooleanValue, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean thisSubSelected(int mainTypeIndex, int subTypeIndex) {
        String[] subType = DataReader.getInstance(this.mContext).getSubType()[mainTypeIndex];
        if (subType == null || subTypeIndex < 0 || subTypeIndex >= subType.length) {
            return false;
        }
        return SaveValue.getInstance(this.mContext).readBooleanValue(subType[subTypeIndex], false);
    }
}
