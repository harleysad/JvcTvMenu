package com.mediatek.wwtv.tvcenter.nav.fav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import java.util.List;

public class ChannelAdapter extends BaseAdapter {
    private final String TAG = "ChannelAdapter";
    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<MtkTvChannelInfoBase> mcurrentChannelList;

    public ChannelAdapter(Context context, List<MtkTvChannelInfoBase> mcurrentChannelList2) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mcurrentChannelList = mcurrentChannelList2;
    }

    public List<MtkTvChannelInfoBase> getChannellist() {
        return this.mcurrentChannelList;
    }

    public int getCount() {
        if (this.mcurrentChannelList != null) {
            return this.mcurrentChannelList.size();
        }
        return 0;
    }

    public MtkTvChannelInfoBase getItem(int position) {
        return this.mcurrentChannelList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int isExistCh(int chId) {
        if (this.mcurrentChannelList == null) {
            return -1;
        }
        int size = this.mcurrentChannelList.size();
        for (int index = 0; index < size; index++) {
            if (this.mcurrentChannelList.get(index).getChannelId() == chId) {
                return index;
            }
        }
        return -1;
    }

    public void updateData(List<MtkTvChannelInfoBase> mcurrentChannelList2) {
        this.mcurrentChannelList = mcurrentChannelList2;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hodler;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.nav_channel_item, (ViewGroup) null);
            hodler = new ViewHolder();
            hodler.mChannelNumberTextView = (TextView) convertView.findViewById(R.id.nav_channel_list_item_NumberTV);
            hodler.mChannelNameTextView = (TextView) convertView.findViewById(R.id.nav_channel_list_item_NameTV);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHolder) convertView.getTag();
        }
        MtkTvChannelInfoBase mCurrentChannel = this.mcurrentChannelList.get(position);
        if (mCurrentChannel instanceof MtkTvATSCChannelInfo) {
            MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel;
            TextView textView = hodler.mChannelNumberTextView;
            textView.setText(tmpAtsc.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpAtsc.getMinorNum());
        } else if (mCurrentChannel instanceof MtkTvISDBChannelInfo) {
            MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel;
            TextView textView2 = hodler.mChannelNumberTextView;
            textView2.setText(tmpIsdb.getMajorNum() + "." + tmpIsdb.getMinorNum());
        } else {
            TextView textView3 = hodler.mChannelNumberTextView;
            textView3.setText("" + mCurrentChannel.getChannelNumber());
        }
        hodler.mChannelNameTextView.setText(mCurrentChannel.getServiceName());
        return convertView;
    }

    private class ViewHolder {
        TextView mChannelNameTextView;
        TextView mChannelNumberTextView;

        private ViewHolder() {
        }
    }
}
