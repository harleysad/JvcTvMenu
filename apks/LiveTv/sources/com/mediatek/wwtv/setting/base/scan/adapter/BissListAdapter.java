package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapter;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterBase;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class BissListAdapter extends BaseAdapter implements ScrollAdapter, ScrollAdapterView.OnScrollListener, View.OnKeyListener {
    private static final String TAG = "BissListAdapter";
    private Context mContext;
    public List<BissItem> mList;
    private ActionAdapter.Listener mListener;
    private ScrollAdapterView mScrollAdapterView;
    String[] onOffStr;
    int selNum;

    public BissListAdapter(Context mContext2, List<BissItem> mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
        MtkLog.d(TAG, "mList.size==" + mList2.size());
    }

    public void setListener(ActionAdapter.Listener ler) {
        this.mListener = ler;
    }

    public void viewRemoved(View view) {
    }

    public View getScrapView(ViewGroup parent) {
        return LayoutInflater.from(this.mContext).inflate(R.layout.menu_biss_list_item, parent, false);
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
        BissItem item = this.mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = getScrapView(parent);
            holder.numText = (TextView) convertView.findViewById(R.id.biss_num);
            holder.threePryText = (TextView) convertView.findViewById(R.id.biss_three_pry);
            holder.progIdText = (TextView) convertView.findViewById(R.id.biss_prog_id);
            holder.cwKeyText = (TextView) convertView.findViewById(R.id.biss_cwkey);
            holder.scanIcon = (ImageView) convertView.findViewById(R.id.biss_scan_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.scanIcon.setVisibility(8);
        if (item.isAddKey) {
            holder.numText.setVisibility(8);
            holder.progIdText.setVisibility(8);
            holder.cwKeyText.setVisibility(8);
            holder.threePryText.setText(item.getTitle());
        } else {
            holder.progIdText.setVisibility(0);
            holder.cwKeyText.setVisibility(0);
            TextView textView = holder.numText;
            textView.setText("" + item.bnum);
            holder.threePryText.setText(item.threePry);
            TextView textView2 = holder.progIdText;
            textView2.setText("" + item.progId);
            holder.cwKeyText.setText(item.cwKey);
        }
        convertView.setTag(R.id.biss_cwkey, item);
        MtkLog.d(TAG, "threePry:" + item.threePry);
        convertView.setOnKeyListener(this);
        return convertView;
    }

    public int getSelectItemNum() {
        return this.selNum;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKey------" + keyCode);
        if (v == null) {
            return false;
        }
        if ((keyCode != 23 && keyCode != 66 && keyCode != 160) || event.getAction() != 0) {
            return false;
        }
        BissItem item = (BissItem) v.getTag(R.id.biss_cwkey);
        this.selNum = item.bnum;
        if (this.mListener != null) {
            this.mListener.onActionClicked(item);
        }
        return true;
    }

    public void onScrolled(View view, int position, float mainPosition, float secondPosition) {
    }

    public ScrollAdapterBase getExpandAdapter() {
        return null;
    }

    public void setScrollAdapterView(ScrollAdapterView mListView) {
        this.mScrollAdapterView = mListView;
    }

    class ViewHolder {
        TextView cwKeyText;
        TextView numText;
        TextView progIdText;
        ImageView scanIcon;
        TextView threePryText;

        ViewHolder() {
        }
    }

    public static class BissItem extends Action {
        public int bnum;
        public String cwKey;
        public boolean isAddKey;
        public int progId;
        public String threePry;

        public BissItem(int bnum2, int progId2, String threePry2, String cwKey2) {
            super(MenuConfigManager.BISS_KEY_ITEM, "BissKey item", Action.DataType.BISSITEMVIEW);
            this.bnum = bnum2;
            this.progId = progId2;
            this.threePry = threePry2;
            this.cwKey = cwKey2;
            this.isAddKey = false;
        }

        public BissItem(boolean isAddKey2) {
            super(MenuConfigManager.BISS_KEY_ITEM_ADD, "Click To Add Biss Key", Action.DataType.HAVESUBCHILD);
            this.progId = -1;
            this.isAddKey = isAddKey2;
        }
    }
}
