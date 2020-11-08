package com.mediatek.wwtv.tvcenter.epg.sa;

import android.content.Context;
import android.support.v4.view.InputDeviceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EPGTypeListAdapter extends BaseAdapter {
    private static final String TAG = "EPGListAdapter";
    public static boolean mFocus = true;
    public static int mPosition = 0;
    public static int sPosition = 0;
    private Context mContext;
    private List<EPGListViewDataItem> mData;
    private ListView mList;
    private ViewHolder mViewHolder;
    private ListView sList;
    private SaveValue sv;

    EPGTypeListAdapter(Context context) {
        this.mContext = context;
        this.sv = SaveValue.getInstance(context);
    }

    public EPGTypeListAdapter(Context context, ListView mainList, ListView subList) {
        this.mContext = context;
        this.mList = mainList;
        this.sList = subList;
        mPosition = 0;
        sPosition = 0;
        mFocus = true;
        this.sv = SaveValue.getInstance(context);
    }

    public boolean isMfocus() {
        return mFocus;
    }

    public int getCount() {
        if (this.mData == null) {
            return 0;
        }
        return this.mData.size();
    }

    public Object getItem(int position) {
        if (this.mData == null) {
            return null;
        }
        return this.mData.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            this.mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.epg_type_item_layout, (ViewGroup) null);
            this.mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.epg_type_icon);
            this.mViewHolder.mTextView = (TextView) convertView.findViewById(R.id.epg_type_name);
            convertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (!(this.mData == null || this.mData.size() <= 0 || this.mData.get(position) == null || this.mData.get(position).data.length() == 0 || this.mViewHolder.mTextView == null)) {
            MtkLog.d(TAG, "--- Text View is null ---");
            this.mViewHolder.mTextView.setText(this.mData.get(position).data);
            this.mViewHolder.mTextView.setTextColor(-3355444);
            MtkLog.i(TAG, "++++++++++ getView() Position: " + position + "    Data: " + this.sv.readBooleanValue(this.mData.get(position).data, false));
            if (this.sv.readBooleanValue(this.mData.get(position).data, false)) {
                this.mViewHolder.imageView.setVisibility(0);
            } else {
                this.mViewHolder.imageView.setVisibility(4);
            }
        }
        convertView.setLayoutParams(new AbsListView.LayoutParams(-1, (int) (((((double) ScreenConstant.SCREEN_HEIGHT) * 0.85d) * 0.83d) / 14.0d)));
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView mTextView;

        ViewHolder() {
        }
    }

    public List<EPGListViewDataItem> getEPGData() {
        return this.mData;
    }

    public void setEPGGroup(List<EPGListViewDataItem> data) {
        this.mData = data;
    }

    public List<EPGListViewDataItem> loadEPGFilterTypeData() {
        String[][] sType = (String[][]) Array.newInstance(String.class, new int[]{17, 20});
        String[] mType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_sa_type);
        sType[0] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_news);
        sType[1] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_sports);
        sType[2] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_education);
        sType[3] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_soap_opera);
        sType[4] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_mini_series);
        sType[5] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_series);
        sType[6] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_variety);
        sType[7] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_reality_show);
        sType[8] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_information);
        sType[9] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_comical);
        sType[10] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_children);
        sType[11] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_erotic);
        sType[12] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_movie);
        sType[13] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_RA_TE_SA_PR);
        sType[14] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_DEBATE_INTERVIEW);
        sType[15] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_other);
        List<EPGListViewDataItem> mDataGroup = new ArrayList<>();
        for (int i = 0; i < mType.length; i++) {
            EPGListViewDataItem mTypeData = new EPGListViewDataItem(mType[i]);
            List<EPGListViewDataItem> mSubTypeData = new ArrayList<>();
            for (String ePGListViewDataItem : sType[i]) {
                mSubTypeData.add(new EPGListViewDataItem(ePGListViewDataItem));
            }
            mTypeData.setSubChildDataItem(mSubTypeData);
            mDataGroup.add(mTypeData);
        }
        return mDataGroup;
    }

    public class EPGListViewDataItem {
        protected String data;
        List<EPGListViewDataItem> mSubChildDataItem;
        boolean marked = false;

        public List<EPGListViewDataItem> getSubChildDataItem() {
            return this.mSubChildDataItem;
        }

        public void setSubChildDataItem(List<EPGListViewDataItem> mSubChildDataItem2) {
            this.mSubChildDataItem = mSubChildDataItem2;
        }

        public EPGListViewDataItem() {
        }

        public EPGListViewDataItem(String data2) {
            this.data = data2;
        }

        public String getData() {
            return this.data;
        }

        public void setData(String data2) {
            this.data = data2;
        }

        public boolean isMarked() {
            return this.marked;
        }

        public void setMarked(boolean marked2) {
            this.marked = marked2;
        }
    }

    public void onMainKey(View v, int keyCode) {
        MtkLog.d(TAG, " ==== main list select item position: " + mPosition + ">>>" + sPosition + ">>>" + mFocus);
        switch (keyCode) {
            case 19:
                MtkLog.i(TAG, " ==== main list item nums: " + this.mList.getAdapter().getCount());
                if (mFocus) {
                    mPosition--;
                    if (mPosition < 0) {
                        mPosition = this.mList.getAdapter().getCount() - 1;
                        MtkLog.i(TAG, " main list select item position: " + mPosition);
                        this.mList.setSelection(mPosition);
                        return;
                    }
                    return;
                }
                return;
            case 20:
                if (mFocus) {
                    mPosition++;
                    if (mPosition >= this.mList.getAdapter().getCount()) {
                        mPosition = 0;
                        this.mList.setSelection(mPosition);
                        return;
                    }
                    return;
                }
                return;
            case 22:
                if (this.sList == null) {
                    MtkLog.d(TAG, "do not have sub type,so do nothing!");
                    return;
                } else if (mFocus) {
                    this.mList.clearFocus();
                    this.mList.setFocusable(false);
                    this.sList.setFocusable(true);
                    if (this.mList.getSelectedItemPosition() < 0) {
                        this.mList.setSelection(0);
                        mPosition = 0;
                    }
                    sPosition = 0;
                    if (this.mList.getSelectedView() != null) {
                        ((TextView) ((LinearLayout) this.mList.getSelectedView()).getChildAt(1)).setTextColor(InputDeviceCompat.SOURCE_ANY);
                    }
                    this.sList.setSelection(sPosition);
                    this.sList.requestFocus();
                    mFocus = false;
                    return;
                } else {
                    return;
                }
            case 23:
            case 33:
                MtkLog.d(TAG, "KEYCODE_DPAD_CENTER  Position: " + mPosition + "      is selected: " + this.mData.get(mPosition).marked);
                if (this.sv.readBooleanValue(this.mData.get(mPosition).data, false)) {
                    this.mData.get(mPosition).marked = false;
                    ((LinearLayout) this.mList.getSelectedView()).getChildAt(0).setVisibility(4);
                    this.sv.saveBooleanValue(this.mData.get(mPosition).data, false);
                    for (int i = 0; i < this.mData.get(mPosition).getSubChildDataItem().size(); i++) {
                        this.sv.saveBooleanValue(this.mData.get(mPosition).getSubChildDataItem().get(i).data, false);
                    }
                    ((BaseAdapter) this.sList.getAdapter()).notifyDataSetChanged();
                    return;
                } else if (this.mList.getSelectedView() != null) {
                    this.mData.get(mPosition).marked = true;
                    ((LinearLayout) this.mList.getSelectedView()).getChildAt(0).setVisibility(0);
                    this.sv.saveBooleanValue(this.mData.get(mPosition).data, true);
                    return;
                } else {
                    this.mList.setSelection(mPosition);
                    return;
                }
            default:
                return;
        }
    }

    public void onSubKey(View v, int keyCode) {
        MtkLog.d(TAG, " ==== sub list select item position: " + mPosition + ">>>" + sPosition + ">>>" + mFocus);
        if (keyCode == 23 || keyCode == 33) {
            MtkLog.d(TAG, "KEYCODE_DPAD_CENTER Sub List Position: " + sPosition + "      is selected: " + this.mData.get(sPosition).marked);
            if (this.sv.readBooleanValue(this.mData.get(sPosition).data, false)) {
                this.mData.get(sPosition).marked = false;
                MtkLog.d(TAG, "------- sub list not marked ---------");
                ((LinearLayout) this.sList.getSelectedView()).getChildAt(0).setVisibility(4);
                this.sv.saveBooleanValue(this.mData.get(sPosition).data, false);
                return;
            }
            this.mData.get(sPosition).marked = true;
            MtkLog.d(TAG, "-------sub list is marked ---------");
            ((LinearLayout) this.sList.getSelectedView()).getChildAt(0).setVisibility(0);
            this.sv.saveBooleanValue(this.mData.get(sPosition).data, true);
            setMainItemVisible();
            return;
        }
        switch (keyCode) {
            case 19:
                if (!mFocus) {
                    sPosition--;
                    if (sPosition < 0) {
                        sPosition = this.sList.getAdapter().getCount() - 1;
                        this.sList.setSelection(sPosition);
                        return;
                    }
                    return;
                }
                return;
            case 20:
                if (!mFocus) {
                    sPosition++;
                    if (sPosition >= this.sList.getAdapter().getCount()) {
                        sPosition = 0;
                        this.sList.setSelection(sPosition);
                        return;
                    }
                    return;
                }
                return;
            case 21:
                if (!mFocus) {
                    this.sList.clearFocus();
                    this.sList.setFocusable(false);
                    this.mList.setFocusable(true);
                    this.mList.requestFocus();
                    this.mList.setSelection(mPosition);
                    ((TextView) ((LinearLayout) this.mList.getSelectedView()).getChildAt(1)).setTextColor(-3355444);
                    sPosition = 0;
                    mFocus = true;
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setMainItemVisible() {
        ((EPGTypeListAdapter) this.mList.getAdapter()).mData.get(mPosition).marked = true;
        this.sv.saveBooleanValue(((EPGTypeListAdapter) this.mList.getAdapter()).mData.get(mPosition).data, true);
        ((EPGTypeListAdapter) this.mList.getAdapter()).notifyDataSetChanged();
    }
}
