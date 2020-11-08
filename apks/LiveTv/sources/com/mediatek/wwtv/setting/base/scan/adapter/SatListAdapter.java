package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.wwtv.setting.base.scan.ui.ScanDialogActivity;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapter;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterBase;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class SatListAdapter extends BaseAdapter implements ScrollAdapter, ScrollAdapterView.OnScrollListener, View.OnKeyListener {
    private static final String TAG = "SatListAdapter";
    private final Context mContext;
    private final float mDisabledChevronAlpha;
    private final float mDisabledDescriptionAlpha;
    private final float mDisabledTitleAlpha;
    public List<SatItem> mList;
    private ActionAdapter.Listener mListener;
    boolean mNeedDisable = false;
    private ScrollAdapterView mScrollAdapterView;
    private final float mSelectedChevronAlpha;
    private final float mSelectedDescriptionAlpha;
    private final float mSelectedTitleAlpha;
    private View mSelectedView = null;
    private final float mUnselectedAlpha;
    private final float mUnselectedDescriptionAlpha;
    String[] onOffStr;
    int selNum;

    public SatListAdapter(Context mContext2, List<SatItem> mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
        this.onOffStr = mContext2.getResources().getStringArray(R.array.dvbs_sat_on_off);
        Resources resources = mContext2.getResources();
        this.mUnselectedAlpha = getFloat(R.dimen.list_item_unselected_text_alpha);
        this.mSelectedTitleAlpha = getFloat(R.dimen.list_item_selected_title_text_alpha);
        this.mDisabledTitleAlpha = getFloat(R.dimen.list_item_disabled_title_text_alpha);
        this.mSelectedDescriptionAlpha = getFloat(R.dimen.list_item_selected_description_text_alpha);
        this.mUnselectedDescriptionAlpha = getFloat(R.dimen.list_item_unselected_description_text_alpha);
        this.mDisabledDescriptionAlpha = getFloat(R.dimen.list_item_disabled_description_text_alpha);
        this.mSelectedChevronAlpha = getFloat(R.dimen.list_item_selected_chevron_background_alpha);
        this.mDisabledChevronAlpha = getFloat(R.dimen.list_item_disabled_chevron_background_alpha);
    }

    public void setListener(ActionAdapter.Listener ler) {
        this.mListener = ler;
    }

    private float getFloat(int resourceId) {
        TypedValue buffer = new TypedValue();
        this.mContext.getResources().getValue(resourceId, buffer, true);
        return buffer.getFloat();
    }

    public void viewRemoved(View view) {
    }

    public View getScrapView(ViewGroup parent) {
        return LayoutInflater.from(this.mContext).inflate(R.layout.menu_sat_list_item, parent, false);
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
        SatItem item = this.mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = getScrapView(parent);
            holder.numText = (TextView) convertView.findViewById(R.id.satellite_num);
            holder.nameText = (TextView) convertView.findViewById(R.id.satellite_name);
            holder.abilityText = (TextView) convertView.findViewById(R.id.satellite_able);
            holder.onOffText = (TextView) convertView.findViewById(R.id.satellite_state);
            holder.scanIcon = (ImageView) convertView.findViewById(R.id.satellite_scan_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.numText.setText(item.satNum);
        holder.nameText.setText(item.name);
        holder.abilityText.setText(item.abilityType);
        convertView.setOnKeyListener(this);
        if (item.isOn) {
            holder.onOffText.setText(this.onOffStr[0]);
            holder.scanIcon.setVisibility(0);
            convertView.setAlpha(1.0f);
        } else {
            if (this.mNeedDisable) {
                convertView.setAlpha(0.4f);
                convertView.setOnKeyListener((View.OnKeyListener) null);
            }
            holder.onOffText.setText(this.onOffStr[1]);
            holder.scanIcon.setVisibility(4);
        }
        convertView.setTag(R.id.satellite_num, item);
        MtkLog.d("SatAdapter", "num:" + item.satNum);
        return convertView;
    }

    public int getSelectItemNum() {
        return this.selNum;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        MtkLog.d("SatAdpter", "onKey------" + keyCode);
        if (v == null) {
            return false;
        }
        if (!(keyCode == 66 || keyCode == 160)) {
            switch (keyCode) {
                case 22:
                    if (event.getAction() != 0) {
                        return false;
                    }
                    MtkLog.d("SatAdpter", "press right key to enter scan view------");
                    SatItem item2 = (SatItem) v.getTag(R.id.satellite_num);
                    if (item2.isOn) {
                        if (item2.parentAction.mItemID.equals(MenuConfigManager.DVBS_SAT_MANUAL_TURNING)) {
                            Intent intent = new Intent(this.mContext, ScanDialogActivity.class);
                            intent.putExtra("ActionID", item2.parentAction.mItemID);
                            intent.putExtra("SatID", item2.satid);
                            this.mContext.startActivity(intent);
                        } else {
                            Intent intent2 = new Intent(this.mContext, ScanViewActivity.class);
                            intent2.putExtra("ActionID", MenuConfigManager.DVBS_SAT_DEDATIL_INFO_SCAN);
                            intent2.putExtra("SatID", item2.satid);
                            intent2.putExtra("LocationID", item2.parentAction.mItemID);
                            this.mContext.startActivity(intent2);
                        }
                    }
                    return true;
                case 23:
                    break;
                default:
                    return false;
            }
        }
        if (event.getAction() != 0) {
            return false;
        }
        SatItem item = (SatItem) v.getTag(R.id.satellite_num);
        this.selNum = Integer.parseInt(item.satNum);
        if (this.mListener != null) {
            MtkLog.d("SatAdpter", "mListener!=null,onActionClicked");
            this.mListener.onActionClicked(item);
        }
        return true;
    }

    public void onScrolled(View view, int position, float mainPosition, float secondPosition) {
        boolean hasFocus = ((double) mainPosition) == 0.0d;
        MtkLog.d("SatAdapter", "scroll...." + hasFocus + ",");
        if (hasFocus) {
            if (view != null) {
                this.mSelectedView = view;
            }
        } else if (this.mSelectedView != null) {
            this.mSelectedView = null;
        }
    }

    public ScrollAdapterBase getExpandAdapter() {
        return null;
    }

    class ViewHolder {
        TextView abilityText;
        TextView nameText;
        TextView numText;
        TextView onOffText;
        ImageView scanIcon;

        ViewHolder() {
        }
    }

    public static class SatItem extends Action {
        public String abilityType;
        public boolean isOn;
        public String name;
        public Action parentAction;
        public String satNum;
        public int satid;

        public SatItem(int satid2, String satNum2, String name2, String abilityType2, boolean isOn2, Action parentAction2, String title) {
            super(parentAction2.mItemID, title, Action.DataType.SATELITEDETAIL);
            this.satID = satid2;
            this.satid = satid2;
            this.satNum = satNum2;
            this.name = name2;
            this.abilityType = abilityType2;
            this.isOn = isOn2;
            this.parentAction = parentAction2;
        }
    }

    public void setScrollAdapterView(ScrollAdapterView mListView) {
        this.mScrollAdapterView = mListView;
    }

    public void setNeedDisableWhenisOff(boolean status) {
        this.mNeedDisable = status;
    }
}
