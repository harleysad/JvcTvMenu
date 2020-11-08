package com.mediatek.wwtv.tvcenter.nav.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.List;

public class SundryModeAdapter extends BaseAdapter {
    private static final int LIST_PAGE_MAX = 7;
    private static final String TAG = "SundryModeAdapter";
    private int currentKeyVlaue = -1;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> mModeList;
    private String[] pictureModeStringArray;
    private String[] screenModeStringArray;
    private String[] soundEffectModeStringArray;

    public int getCount() {
        return this.mModeList.size();
    }

    public Integer getItem(int position) {
        return this.mModeList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.nav_sundry_dialog_item, (ViewGroup) null);
            mViewHolder = new ViewHolder();
            mViewHolder.itemTextView = (TextView) convertView.findViewById(R.id.nav_sundry_mode_list_item_view);
            mViewHolder.itemTextView.setHeight((int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.05d));
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (!(this.mModeList == null || this.mModeList.size() <= 0 || this.currentKeyVlaue == -1)) {
            int i = this.currentKeyVlaue;
            if (i == 222) {
                mViewHolder.itemTextView.setText(this.soundEffectModeStringArray[this.mModeList.get(position).intValue()]);
            } else if (i == 251) {
                MtkLog.d(TAG, "come in getView == " + this.mModeList.get(position));
                mViewHolder.itemTextView.setText(this.pictureModeStringArray[this.mModeList.get(position).intValue()]);
            } else if (i == 10471) {
                mViewHolder.itemTextView.setText(this.screenModeStringArray[this.mModeList.get(position).intValue()]);
            }
        }
        return convertView;
    }

    public SundryModeAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.pictureModeStringArray = this.mContext.getResources().getStringArray(R.array.picture_effect_array_us);
        this.soundEffectModeStringArray = this.mContext.getResources().getStringArray(R.array.menu_audio_equalizer_array_us);
        this.screenModeStringArray = this.mContext.getResources().getStringArray(R.array.screen_mode_array_us);
    }

    public void updateList(List<Integer> modeList, int keyValue) {
        this.mModeList = modeList;
        this.currentKeyVlaue = keyValue;
        notifyDataSetChanged();
    }

    public void updateList(List<Integer> modeList) {
        this.mModeList = modeList;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView itemTextView;

        private ViewHolder() {
        }
    }
}
