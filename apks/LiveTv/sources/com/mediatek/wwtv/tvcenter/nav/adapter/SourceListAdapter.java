package com.mediatek.wwtv.tvcenter.nav.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class SourceListAdapter extends BaseAdapter {
    private final String TAG = "SourceListAdapter";
    private float itemHeightPercent = 0.04648f;
    private Drawable mConflictIcon;
    private List<String> mConflictInputsList;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> mSourceList;
    private Drawable mSourceSelectedIcon;
    private Drawable mSourceUnSelectedIcon;

    public SourceListAdapter(Context context, List<Integer> mSourceList2, List<String> mConflictList, Drawable mSourceSelectedIcon2, Drawable mSourceUnSelectedIcon2, Drawable mConflictIcon2) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mSourceList = mSourceList2;
        this.mConflictInputsList = mConflictList;
        this.mSourceSelectedIcon = mSourceSelectedIcon2;
        this.mSourceUnSelectedIcon = mSourceUnSelectedIcon2;
        this.mConflictIcon = mConflictIcon2;
        TypedValue typedValue = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.nav_source_list_dialog_item_height, typedValue, true);
        this.itemHeightPercent = typedValue.getFloat();
    }

    public void updateList(List<Integer> mSourceList2, List<String> mConflictList) {
        this.mSourceList = mSourceList2;
        this.mConflictInputsList = mConflictList;
    }

    public int getCount() {
        return this.mSourceList.size();
    }

    public String getItem(int position) {
        AbstractInput input = InputUtil.getInput(this.mSourceList.get(position));
        String customLabel = input.getCustomSourceName(this.mContext);
        if (TextUtils.isEmpty(customLabel) || TextUtils.equals(customLabel, "null")) {
            return input.getSourceName(this.mContext);
        }
        return customLabel;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hodler;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.nav_source_item, (ViewGroup) null);
            hodler = new ViewHolder();
            hodler.mTextView = (TextView) convertView.findViewById(R.id.nav_source_list_item_tv);
            hodler.mIcon = (ImageView) convertView.findViewById(R.id.nav_source_list_item_icon);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHolder) convertView.getTag();
        }
        hodler.mTextView.setText(getItem(position));
        int inputSourceHardwareId = InputSourceManager.getInstance().getCurrentInputSourceHardwareId();
        MtkLog.d("SourceListAdapter", "--- inputSourceHardwareId = " + inputSourceHardwareId);
        if (this.mSourceList.get(position).intValue() == inputSourceHardwareId) {
            hodler.mIcon.setImageDrawable(this.mSourceSelectedIcon);
        } else {
            hodler.mIcon.setImageDrawable(this.mSourceUnSelectedIcon);
        }
        convertView.setFocusableInTouchMode(true);
        return convertView;
    }

    private class ViewHolder {
        ImageView mIcon;
        TextView mTextView;

        private ViewHolder() {
        }
    }
}
