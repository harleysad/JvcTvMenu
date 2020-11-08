package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
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
import java.util.List;

public class EPGTypeListAdapter extends BaseAdapter {
    private static final String TAG = "EPGListAdapter";
    private static boolean mFocus = true;
    static int mPosition = 0;
    static int sPosition = 0;
    Context mContext;
    List<EPGListViewDataItem> mData;
    ListView mList;
    ViewHolder mViewHolder;
    ListView sList;
    SaveValue sv;

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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.String[][]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter.EPGListViewDataItem> loadEPGFilterTypeData(java.lang.String r11) {
        /*
            r10 = this;
            r0 = 11
            r1 = 20
            int[] r0 = new int[]{r0, r1}
            java.lang.Class<java.lang.String> r1 = java.lang.String.class
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r1, r0)
            java.lang.String[][] r0 = (java.lang.String[][]) r0
            r1 = 0
            r2 = 0
            if (r11 == 0) goto L_0x002b
            java.lang.String r3 = "GBR"
            boolean r3 = r11.equals(r3)
            if (r3 == 0) goto L_0x002b
            android.content.Context r3 = r10.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2130903313(0x7f030111, float:1.741344E38)
            java.lang.String[] r1 = r3.getStringArray(r4)
            goto L_0x0112
        L_0x002b:
            if (r11 == 0) goto L_0x0053
            java.lang.String r3 = "AUS"
            boolean r3 = r11.equals(r3)
            if (r3 == 0) goto L_0x0053
            r3 = 14
            int[] r3 = new int[]{r3, r2}
            java.lang.Class<java.lang.String> r4 = java.lang.String.class
            java.lang.Object r3 = java.lang.reflect.Array.newInstance(r4, r3)
            r0 = r3
            java.lang.String[][] r0 = (java.lang.String[][]) r0
            android.content.Context r3 = r10.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2130903312(0x7f030110, float:1.7413438E38)
            java.lang.String[] r1 = r3.getStringArray(r4)
            goto L_0x0112
        L_0x0053:
            android.content.Context r3 = r10.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2130903311(0x7f03010f, float:1.7413436E38)
            java.lang.String[] r1 = r3.getStringArray(r4)
            android.content.Context r3 = r10.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2130903323(0x7f03011b, float:1.741346E38)
            java.lang.String[] r3 = r3.getStringArray(r4)
            r0[r2] = r3
            android.content.Context r3 = r10.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2130903327(0x7f03011f, float:1.7413469E38)
            java.lang.String[] r3 = r3.getStringArray(r4)
            r4 = 1
            r0[r4] = r3
            r3 = 2
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903345(0x7f030131, float:1.7413505E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 3
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903351(0x7f030137, float:1.7413518E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 4
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903317(0x7f030115, float:1.7413449E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 5
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903325(0x7f03011d, float:1.7413465E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 6
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903315(0x7f030113, float:1.7413445E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 7
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903347(0x7f030133, float:1.741351E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 8
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903319(0x7f030117, float:1.7413453E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 9
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903321(0x7f030119, float:1.7413457E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
            r3 = 10
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903349(0x7f030135, float:1.7413513E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            r0[r3] = r4
        L_0x0112:
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r4 = r2
        L_0x0118:
            int r5 = r1.length
            if (r4 >= r5) goto L_0x0145
            com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter$EPGListViewDataItem r5 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter$EPGListViewDataItem
            r6 = r1[r4]
            r5.<init>(r6)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r7 = r2
        L_0x0128:
            r8 = r0[r4]
            int r8 = r8.length
            if (r7 >= r8) goto L_0x013c
            com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter$EPGListViewDataItem r8 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter$EPGListViewDataItem
            r9 = r0[r4]
            r9 = r9[r7]
            r8.<init>(r9)
            r6.add(r8)
            int r7 = r7 + 1
            goto L_0x0128
        L_0x013c:
            r5.setSubChildDataItem(r6)
            r3.add(r5)
            int r4 = r4 + 1
            goto L_0x0118
        L_0x0145:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter.loadEPGFilterTypeData(java.lang.String):java.util.List");
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
                        MtkLog.i(TAG, " main list select item position: " + mPosition);
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
                    sPosition = 0;
                    ((TextView) ((LinearLayout) this.mList.getSelectedView()).getChildAt(1)).setTextColor(-1232808961);
                    if (this.sList != null) {
                        this.sList.setFocusable(true);
                        this.sList.setSelection(sPosition);
                        this.sList.requestFocus();
                    }
                    mFocus = false;
                    return;
                } else {
                    return;
                }
            case 23:
            case 33:
                MtkLog.d(TAG, "Position: " + mPosition + "      is selected: " + this.mData.get(mPosition).marked);
                EpgType.mHasEditType = true;
                if (this.sv.readBooleanValue(this.mData.get(mPosition).data, false)) {
                    this.mData.get(mPosition).marked = false;
                    ((LinearLayout) this.mList.getSelectedView()).getChildAt(0).setVisibility(4);
                    this.sv.saveBooleanValue(this.mData.get(mPosition).data, false);
                    for (int i = 0; i < this.mData.get(mPosition).getSubChildDataItem().size(); i++) {
                        this.sv.saveBooleanValue(this.mData.get(mPosition).getSubChildDataItem().get(i).data, false);
                    }
                    if (this.sList != null) {
                        ((BaseAdapter) this.sList.getAdapter()).notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                this.mData.get(mPosition).marked = true;
                ((LinearLayout) this.mList.getSelectedView()).getChildAt(0).setVisibility(0);
                this.sv.saveBooleanValue(this.mData.get(mPosition).data, true);
                return;
            default:
                return;
        }
    }

    public void onSubKey(View v, int keyCode) {
        MtkLog.d(TAG, " ==== sub list select item position: " + mPosition + ">>>" + sPosition + ">>>" + mFocus);
        if (keyCode == 23 || keyCode == 33) {
            MtkLog.d(TAG, "Sub List Position: " + sPosition + "      is selected: " + this.mData.get(sPosition).marked);
            EpgType.mHasEditType = true;
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
                    MtkLog.d(TAG, " ==== sub sList.getAdapter().getCount():" + this.sList.getAdapter().getCount() + "  sPosition:" + sPosition);
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
                    this.mList.setSelection(mPosition);
                    this.mList.requestFocus();
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
