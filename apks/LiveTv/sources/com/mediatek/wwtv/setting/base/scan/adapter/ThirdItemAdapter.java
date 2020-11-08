package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class ThirdItemAdapter extends BaseAdapter {
    public static final String TAG = "ThirdItemAdapter";
    Context mContext;
    List<ThirdItem> mList;

    public ThirdItemAdapter(Context mContext2, List<ThirdItem> mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return this.mList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter$ViewHolder} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r9, android.view.View r10, android.view.ViewGroup r11) {
        /*
            r8 = this;
            java.util.List<com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter$ThirdItem> r0 = r8.mList
            java.lang.Object r0 = r0.get(r9)
            com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter$ThirdItem r0 = (com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter.ThirdItem) r0
            r1 = 2131362213(0x7f0a01a5, float:1.83442E38)
            r2 = 0
            if (r10 != 0) goto L_0x0058
            android.content.Context r3 = r8.mContext
            android.view.LayoutInflater r3 = android.view.LayoutInflater.from(r3)
            r4 = 2131492906(0x7f0c002a, float:1.8609277E38)
            android.view.View r10 = r3.inflate(r4, r11, r2)
            com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter$ViewHolder r4 = new com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter$ViewHolder
            r4.<init>()
            r5 = 2131362210(0x7f0a01a2, float:1.8344194E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.itemName = r5
            android.view.View r5 = r10.findViewById(r1)
            android.widget.EditText r5 = (android.widget.EditText) r5
            r4.itemValue = r5
            r5 = 2131362211(0x7f0a01a3, float:1.8344196E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.itemOption = r5
            r5 = 2131362212(0x7f0a01a4, float:1.8344198E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            r4.itemOpLayout = r5
            r5 = 2131362209(0x7f0a01a1, float:1.8344192E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.imgOption = r5
            r10.setTag(r4)
            goto L_0x005f
        L_0x0058:
            java.lang.Object r3 = r10.getTag()
            r4 = r3
            com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter$ViewHolder r4 = (com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter.ViewHolder) r4
        L_0x005f:
            r3 = r4
            android.widget.TextView r4 = r3.itemName
            java.lang.String r5 = r0.title
            r4.setText(r5)
            java.lang.String[] r4 = r0.optionValues
            r5 = 8
            if (r4 == 0) goto L_0x0083
            android.widget.LinearLayout r4 = r3.itemOpLayout
            r4.setVisibility(r2)
            android.widget.EditText r4 = r3.itemValue
            r4.setVisibility(r5)
            android.widget.TextView r4 = r3.itemOption
            java.lang.String[] r6 = r0.optionValues
            int r7 = r0.optionValue
            r6 = r6[r7]
            r4.setText(r6)
            goto L_0x008d
        L_0x0083:
            android.widget.EditText r4 = r3.itemValue
            r4.setVisibility(r2)
            android.widget.LinearLayout r4 = r3.itemOpLayout
            r4.setVisibility(r5)
        L_0x008d:
            boolean r4 = r0.isEnable
            if (r4 != 0) goto L_0x00b3
            android.widget.TextView r4 = r3.itemName
            r6 = -7829368(0xffffffffff888888, float:NaN)
            r4.setTextColor(r6)
            android.widget.EditText r4 = r3.itemValue
            r4.setTextColor(r6)
            android.widget.TextView r4 = r3.itemOption
            r4.setTextColor(r6)
            android.widget.EditText r4 = r3.itemValue
            r4.setEnabled(r2)
            android.widget.TextView r4 = r3.itemOption
            r4.setEnabled(r2)
            android.widget.ImageView r2 = r3.imgOption
            r2.setVisibility(r5)
            goto L_0x00e2
        L_0x00b3:
            android.widget.TextView r4 = r3.itemName
            r6 = -1
            r4.setTextColor(r6)
            android.widget.EditText r4 = r3.itemValue
            r4.setTextColor(r6)
            android.widget.TextView r4 = r3.itemOption
            r4.setTextColor(r6)
            android.widget.EditText r4 = r3.itemValue
            r6 = 1
            r4.setEnabled(r6)
            android.widget.TextView r4 = r3.itemOption
            r4.setEnabled(r6)
            java.lang.String[] r4 = r0.optionValues
            if (r4 == 0) goto L_0x00dd
            java.lang.String[] r4 = r0.optionValues
            int r4 = r4.length
            if (r4 <= r6) goto L_0x00dd
            android.widget.ImageView r4 = r3.imgOption
            r4.setVisibility(r2)
            goto L_0x00e2
        L_0x00dd:
            android.widget.ImageView r2 = r3.imgOption
            r2.setVisibility(r5)
        L_0x00e2:
            java.lang.String r2 = "ThirdItemAdapter"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "biaoqingdev:thirdadapter enable"
            r4.append(r5)
            boolean r5 = r0.isEnable
            r4.append(r5)
            java.lang.String r5 = ",item.title="
            r4.append(r5)
            java.lang.String r5 = r0.title
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r4)
            r10.setTag(r1, r0)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public void optionTurnLeft(View selectView, String[] mData) {
        ThirdItem item = (ThirdItem) selectView.getTag(R.id.editdetail_value);
        if (item == null || item.optionValues == null || !item.isEnable) {
            MtkLog.d(TAG, "optionTurnLeft-> not option item so do nothing");
            return;
        }
        MtkLog.d(TAG, "optionTurnLeft:" + item.optionValues);
        ViewHolder holder = (ViewHolder) selectView.getTag();
        item.optionValue = item.optionValue - 1;
        if (item.optionValue < 0) {
            item.optionValue = item.optionValues.length - 1;
        }
        holder.itemOption.setText(item.optionValues[item.optionValue]);
        if (item.id.equals(MenuConfigManager.TV_CHANNEL_AFTER_SCAN_UK_REGION)) {
            item.callValueChange();
            return;
        }
        if (item.id.equals(this.mContext.getString(R.string.scan_trd_uk_reg_reg_x, new Object[]{2}))) {
            item.callValueChange();
        }
    }

    public void optionTurnRight(View selectView, String[] mData) {
        ThirdItem item = (ThirdItem) selectView.getTag(R.id.editdetail_value);
        if (item == null || item.optionValues == null || !item.isEnable) {
            MtkLog.d(TAG, "optionTurnRight-> not option item so do nothing");
            return;
        }
        MtkLog.d(TAG, "optionTurnRight:" + item.optionValues);
        ViewHolder holder = (ViewHolder) selectView.getTag();
        item.optionValue = item.optionValue + 1;
        if (item.optionValue > item.optionValues.length - 1) {
            item.optionValue = 0;
        }
        MtkLog.d(TAG, "optionTurnRight:text is:" + item.optionValues[item.optionValue]);
        holder.itemOption.setText(item.optionValues[item.optionValue]);
        if (item.id.equals(MenuConfigManager.TV_CHANNEL_AFTER_SCAN_UK_REGION)) {
            item.callValueChange();
            return;
        }
        if (item.id.equals(this.mContext.getString(R.string.scan_trd_uk_reg_reg_x, new Object[]{2}))) {
            item.callValueChange();
        }
    }

    class ViewHolder {
        ImageView imgOption;
        TextView itemName;
        LinearLayout itemOpLayout;
        TextView itemOption;
        EditText itemValue;

        ViewHolder() {
        }
    }

    public static class ThirdItem {
        public String id;
        public boolean isEnable;
        OnValueChangeListener listener;
        public int optionValue;
        public String[] optionValues;
        public String title;

        public interface OnValueChangeListener {
            void afterValueChanged(String str);
        }

        public ThirdItem(String id2, String title2, int optionValue2, String[] optionValues2, boolean isEnable2) {
            this.id = id2;
            this.title = title2;
            this.optionValue = optionValue2;
            this.optionValues = optionValues2;
            this.isEnable = isEnable2;
        }

        public void setValueChangeListener(OnValueChangeListener ler) {
            this.listener = ler;
        }

        public void callValueChange() {
            if (this.listener != null) {
                this.listener.afterValueChanged(this.optionValues[this.optionValue]);
            }
        }
    }
}
