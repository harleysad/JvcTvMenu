package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.EPGBaseAdapter;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;

public class EPGEuChannelAdapter extends EPGBaseAdapter<EPGChannelInfo> {
    private static final String TAG = "EPGEuChannelAdapter";
    private Drawable mAnalogIcon;
    private int mSelPosNoFocus = -1;

    public void setSelPosNoFocus(int position) {
        this.mSelPosNoFocus = position;
        notifyDataSetChanged();
    }

    public EPGEuChannelAdapter(Context context) {
        super(context);
        this.mAnalogIcon = context.getResources().getDrawable(R.drawable.epg_channel_icon);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.epg_eu_2nd_listview_item_layout, (ViewGroup) null);
            mViewHolder.llayoutGroup = (LinearLayout) convertView.findViewById(R.id.epg_channel_item);
            mViewHolder.number = (TextView) convertView.findViewById(R.id.epg_channel_number);
            mViewHolder.name = (TextView) convertView.findViewById(R.id.epg_channel_name);
            mViewHolder.llayoutGroup.setLayoutParams(new AbsListView.LayoutParams(-1, this.mItemHeight));
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        EPGChannelInfo channelInfo = (EPGChannelInfo) this.group.get(position);
        if (channelInfo.getTVChannel() == null) {
            Drawable nothingIcon = this.mContext.getResources().getDrawable(R.drawable.translucent_background);
            nothingIcon.setBounds(0, 0, this.mAnalogIcon.getMinimumWidth(), this.mAnalogIcon.getMinimumWidth());
            mViewHolder.number.setCompoundDrawables(nothingIcon, (Drawable) null, (Drawable) null, (Drawable) null);
        } else if (channelInfo.getTVChannel().isRadioService()) {
            Drawable radioIcon = this.mContext.getResources().getDrawable(R.drawable.epg_radio_channel_icon);
            radioIcon.setBounds(0, 0, this.mAnalogIcon.getMinimumWidth(), this.mAnalogIcon.getMinimumWidth());
            mViewHolder.number.setCompoundDrawables(radioIcon, (Drawable) null, (Drawable) null, (Drawable) null);
        } else if (channelInfo.getTVChannel() instanceof MtkTvAnalogChannelInfo) {
            Drawable analogIcon = this.mContext.getResources().getDrawable(R.drawable.epg_channel_icon);
            analogIcon.setBounds(0, 0, analogIcon.getMinimumWidth(), analogIcon.getMinimumWidth());
            mViewHolder.number.setCompoundDrawables(analogIcon, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            Drawable nothingIcon2 = this.mContext.getResources().getDrawable(R.drawable.translucent_background);
            nothingIcon2.setBounds(0, 0, this.mAnalogIcon.getMinimumWidth(), this.mAnalogIcon.getMinimumWidth());
            mViewHolder.number.setCompoundDrawables(nothingIcon2, (Drawable) null, (Drawable) null, (Drawable) null);
        }
        if (this.mSelPosNoFocus == position) {
            mViewHolder.number.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
            mViewHolder.name.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
        } else {
            mViewHolder.number.setTextColor(this.mContext.getResources().getColor(R.drawable.epg_channel_font));
            mViewHolder.name.setTextColor(this.mContext.getResources().getColor(R.drawable.epg_channel_font));
        }
        mViewHolder.number.setCompoundDrawablePadding(10);
        mViewHolder.number.setText(channelInfo.getDisplayNumber());
        mViewHolder.name.setText(String.valueOf(channelInfo.getName()));
        return convertView;
    }

    class ViewHolder {
        LinearLayout llayoutGroup;
        TextView name;
        TextView number;

        ViewHolder() {
        }
    }
}
