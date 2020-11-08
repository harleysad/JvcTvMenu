package com.mediatek.wwtv.tvcenter.epg.sa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.sa.db.EPGBookListViewDataItem;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.ArrayList;
import java.util.List;

public class EPGBookListAdapter extends BaseAdapter {
    private static final String TAG = "EPGBookListAdapter";
    private List<EPGBookListViewDataItem> mBookedListData;
    private Context mContext;
    private ListView mListView;
    private int mPosition;
    private ViewHolder mViewHolder;

    EPGBookListAdapter(Context context) {
        this.mContext = context;
        this.mBookedListData = new ArrayList();
    }

    public EPGBookListAdapter(Context context, ListView mainList) {
        this.mContext = context;
        this.mListView = mainList;
        this.mBookedListData = new ArrayList();
    }

    public EPGBookListAdapter(Context context, ListView mainList, List<EPGBookListViewDataItem> programList) {
        this.mContext = context;
        this.mListView = mainList;
        this.mBookedListData = programList;
    }

    public void addBookedProgram(EPGBookListViewDataItem tempItem) {
        this.mBookedListData.add(tempItem);
    }

    public int getCount() {
        if (this.mBookedListData == null) {
            return 0;
        }
        return this.mBookedListData.size();
    }

    public Object getItem(int position) {
        if (this.mBookedListData == null) {
            return null;
        }
        return this.mBookedListData.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        EPGBookListViewDataItem tempItem;
        if (convertView == null) {
            this.mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.epg_book_item_layout, (ViewGroup) null);
            this.mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.epg_book_icon);
            this.mViewHolder.mChannelTextView = (TextView) convertView.findViewById(R.id.epg_book_channel_name);
            this.mViewHolder.mProgramTextView = (TextView) convertView.findViewById(R.id.epg_book_program_name);
            convertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (!(this.mBookedListData == null || this.mBookedListData.size() <= 0 || (tempItem = this.mBookedListData.get(position)) == null)) {
            if (tempItem.marked) {
                this.mViewHolder.imageView.setVisibility(0);
            } else {
                this.mViewHolder.imageView.setVisibility(4);
            }
            this.mViewHolder.mChannelTextView.setText(tempItem.mChannelNoName);
            this.mViewHolder.mProgramTextView.setText(tempItem.mProgramName);
        }
        convertView.setLayoutParams(new AbsListView.LayoutParams((650 * ScreenConstant.SCREEN_WIDTH) / 1280, (int) (((((double) ScreenConstant.SCREEN_HEIGHT) * 0.85d) * 0.83d) / 14.0d)));
        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView mChannelTextView;
        public TextView mProgramTextView;

        ViewHolder() {
        }
    }

    public List<EPGBookListViewDataItem> getEPGBookListData() {
        return this.mBookedListData;
    }

    public void setEPGBookList(List<EPGBookListViewDataItem> data) {
        this.mBookedListData = data;
    }

    public void onKey(View v, int keyCode) {
        if (keyCode == 23 || keyCode == 33) {
            this.mPosition = this.mListView.getSelectedItemPosition();
            MtkLog.d(TAG, "Position: " + this.mPosition + "      is selected: " + this.mBookedListData.get(this.mPosition).marked);
            if (this.mBookedListData.get(this.mPosition).marked) {
                this.mBookedListData.get(this.mPosition).marked = false;
            } else {
                this.mBookedListData.get(this.mPosition).marked = true;
            }
            notifyDataSetChanged();
        }
    }
}
