package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class EditDetailAdapter extends BaseAdapter implements View.OnKeyListener {
    public static final String TAG = "EditDetailAdapter";
    Context mContext;
    List<EditItem> mList;

    public EditDetailAdapter(Context mContext2, List<EditItem> mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
    }

    public void setNewList(List<EditItem> mList2) {
        this.mList = mList2;
        notifyDataSetChanged();
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v49, resolved type: com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$ViewHolder} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r10, android.view.View r11, android.view.ViewGroup r12) {
        /*
            r9 = this;
            java.util.List<com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem> r0 = r9.mList
            java.lang.Object r0 = r0.get(r10)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r0 = (com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter.EditItem) r0
            r1 = 2131362213(0x7f0a01a5, float:1.83442E38)
            r2 = 0
            if (r11 != 0) goto L_0x0061
            android.content.Context r3 = r9.mContext
            android.view.LayoutInflater r3 = android.view.LayoutInflater.from(r3)
            r4 = 2131492906(0x7f0c002a, float:1.8609277E38)
            android.view.View r11 = r3.inflate(r4, r12, r2)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$ViewHolder r4 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$ViewHolder
            r4.<init>()
            r5 = 2131362210(0x7f0a01a2, float:1.8344194E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.itemName = r5
            android.view.View r5 = r11.findViewById(r1)
            android.widget.EditText r5 = (android.widget.EditText) r5
            r4.itemValue = r5
            r5 = 2131362211(0x7f0a01a3, float:1.8344196E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.itemOption = r5
            r5 = 2131362212(0x7f0a01a4, float:1.8344198E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            r4.itemOpLayout = r5
            r5 = 2131362209(0x7f0a01a1, float:1.8344192E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.imgOption = r5
            r5 = 2131362208(0x7f0a01a0, float:1.834419E38)
            android.view.View r5 = r11.findViewById(r5)
            r4.hasSubLayout = r5
            r11.setTag(r4)
            goto L_0x0068
        L_0x0061:
            java.lang.Object r3 = r11.getTag()
            r4 = r3
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$ViewHolder r4 = (com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter.ViewHolder) r4
        L_0x0068:
            r3 = r4
            android.widget.TextView r4 = r3.itemName
            java.lang.String r5 = r0.title
            r4.setText(r5)
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r4 = r0.dataType
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r5 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r6 = 8
            if (r4 != r5) goto L_0x0094
            android.widget.LinearLayout r4 = r3.itemOpLayout
            r4.setVisibility(r2)
            android.widget.TextView r4 = r3.itemOption
            java.lang.String[] r5 = r0.optionValues
            int r7 = r0.optionValue
            r5 = r5[r7]
            r4.setText(r5)
            android.widget.EditText r4 = r3.itemValue
            r4.setVisibility(r6)
            android.view.View r4 = r3.hasSubLayout
            r4.setVisibility(r6)
            goto L_0x0151
        L_0x0094:
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r4 = r0.dataType
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r5 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
            if (r4 != r5) goto L_0x00b6
            android.widget.EditText r4 = r3.itemValue
            r4.setVisibility(r6)
            android.widget.LinearLayout r4 = r3.itemOpLayout
            r4.setVisibility(r6)
            boolean r4 = r0.isEnable
            if (r4 == 0) goto L_0x00af
            android.view.View r4 = r3.hasSubLayout
            r4.setVisibility(r2)
            goto L_0x0151
        L_0x00af:
            android.view.View r4 = r3.hasSubLayout
            r4.setVisibility(r6)
            goto L_0x0151
        L_0x00b6:
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r4 = r0.dataType
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r5 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            if (r4 != r5) goto L_0x0135
            android.widget.EditText r4 = r3.itemValue
            r4.setVisibility(r2)
            java.lang.String r4 = r0.value
            if (r4 == 0) goto L_0x00f8
            boolean r4 = r0.isEnable
            if (r4 != 0) goto L_0x00f8
            java.lang.String r4 = r0.value
            int r4 = r4.length()
            r5 = 32
            if (r4 <= r5) goto L_0x00f0
            android.widget.EditText r4 = r3.itemValue
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = r0.value
            java.lang.String r5 = r8.substring(r2, r5)
            r7.append(r5)
            java.lang.String r5 = "..."
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            r4.setText(r5)
            goto L_0x012a
        L_0x00f0:
            android.widget.EditText r4 = r3.itemValue
            java.lang.String r5 = r0.value
            r4.setText(r5)
            goto L_0x012a
        L_0x00f8:
            java.lang.String r4 = r0.value
            if (r4 == 0) goto L_0x012a
            java.lang.String r4 = r0.value
            int r4 = r4.length()
            r5 = 16
            if (r4 <= r5) goto L_0x0123
            android.widget.EditText r4 = r3.itemValue
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = r0.value
            java.lang.String r5 = r8.substring(r2, r5)
            r7.append(r5)
            java.lang.String r5 = "..."
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            r4.setText(r5)
            goto L_0x012a
        L_0x0123:
            android.widget.EditText r4 = r3.itemValue
            java.lang.String r5 = r0.value
            r4.setText(r5)
        L_0x012a:
            android.widget.LinearLayout r4 = r3.itemOpLayout
            r4.setVisibility(r6)
            android.view.View r4 = r3.hasSubLayout
            r4.setVisibility(r6)
            goto L_0x0151
        L_0x0135:
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r4 = r0.dataType
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r5 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            if (r4 != r5) goto L_0x0151
            android.widget.EditText r4 = r3.itemValue
            r4.setVisibility(r2)
            android.widget.EditText r4 = r3.itemValue
            java.lang.String r5 = r0.value
            r4.setText(r5)
            android.widget.LinearLayout r4 = r3.itemOpLayout
            r4.setVisibility(r6)
            android.view.View r4 = r3.hasSubLayout
            r4.setVisibility(r6)
        L_0x0151:
            boolean r4 = r0.isEnable
            r5 = 2131099903(0x7f0600ff, float:1.7812172E38)
            if (r4 != 0) goto L_0x0189
            android.widget.TextView r4 = r3.itemName
            r7 = -7829368(0xffffffffff888888, float:NaN)
            r4.setTextColor(r7)
            android.widget.EditText r4 = r3.itemValue
            r4.setTextColor(r7)
            android.widget.TextView r4 = r3.itemOption
            r4.setTextColor(r7)
            android.widget.EditText r4 = r3.itemValue
            r4.setEnabled(r2)
            android.widget.TextView r4 = r3.itemOption
            r4.setEnabled(r2)
            android.widget.EditText r2 = r3.itemValue
            android.content.Context r4 = r9.mContext
            android.content.res.Resources r4 = r4.getResources()
            int r4 = r4.getColor(r5)
            r2.setBackgroundColor(r4)
            android.widget.ImageView r2 = r3.imgOption
            r2.setVisibility(r6)
            goto L_0x01d8
        L_0x0189:
            android.widget.TextView r4 = r3.itemName
            android.content.Context r6 = r9.mContext
            android.content.res.Resources r6 = r6.getResources()
            r7 = 2131099782(0x7f060086, float:1.7811927E38)
            int r6 = r6.getColor(r7)
            r4.setTextColor(r6)
            android.widget.EditText r4 = r3.itemValue
            android.content.Context r6 = r9.mContext
            android.content.res.Resources r6 = r6.getResources()
            int r6 = r6.getColor(r7)
            r4.setTextColor(r6)
            android.widget.TextView r4 = r3.itemOption
            android.content.Context r6 = r9.mContext
            android.content.res.Resources r6 = r6.getResources()
            int r6 = r6.getColor(r7)
            r4.setTextColor(r6)
            android.widget.EditText r4 = r3.itemValue
            r6 = 1
            r4.setEnabled(r6)
            android.widget.TextView r4 = r3.itemOption
            r4.setEnabled(r6)
            android.widget.EditText r4 = r3.itemValue
            android.content.Context r6 = r9.mContext
            android.content.res.Resources r6 = r6.getResources()
            int r5 = r6.getColor(r5)
            r4.setBackgroundColor(r5)
            android.widget.ImageView r4 = r3.imgOption
            r4.setVisibility(r2)
        L_0x01d8:
            r11.setTag(r1, r0)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public void optionTurnLeft(View selectView, String[] mData) {
        EditItem item = (EditItem) selectView.getTag(R.id.editdetail_value);
        if (item == null || !item.isEnable) {
            MtkLog.d(TAG, "optionTurnLeft-> not option item so do nothing");
        } else if (item.optionValues != null) {
            MtkLog.d(TAG, "optionTurnLeft:" + item.optionValues);
            ViewHolder holder = (ViewHolder) selectView.getTag();
            item.optionValue = item.optionValue + -1;
            if (item.optionValue < 0) {
                item.optionValue = item.optionValues.length - 1;
            }
            holder.itemOption.setText(item.optionValues[item.optionValue]);
            if (item.id.equals(MenuConfigManager.TV_CHANNEL_COLOR_SYSTEM)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelColorSystem(item.optionValue, mData);
            } else if (item.id.equals(MenuConfigManager.TV_SOUND_SYSTEM)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelSoundSystem(item.optionValue, mData);
            } else if (item.id.equals(MenuConfigManager.TV_AUTO_FINETUNE) || item.id.equals(MenuConfigManager.TV_FINETUNE)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelIsFine(mData[3], item.optionValue);
            } else if (item.id.equals(MenuConfigManager.TV_SKIP)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelSkip(mData[3], item.optionValue);
            }
        }
    }

    public void optionTurnRight(View selectView, String[] mData) {
        EditItem item = (EditItem) selectView.getTag(R.id.editdetail_value);
        if (item == null || !item.isEnable) {
            MtkLog.d(TAG, "optionTurnRight-> not option item so do nothing");
        } else if (item.optionValues != null) {
            MtkLog.d(TAG, "optionTurnRight:" + item.optionValues);
            ViewHolder holder = (ViewHolder) selectView.getTag();
            item.optionValue = item.optionValue + 1;
            if (item.optionValue > item.optionValues.length - 1) {
                item.optionValue = 0;
            }
            holder.itemOption.setText(item.optionValues[item.optionValue]);
            if (item.id.equals(MenuConfigManager.TV_CHANNEL_COLOR_SYSTEM)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelColorSystem(item.optionValue, mData);
            } else if (item.id.equals(MenuConfigManager.TV_SOUND_SYSTEM)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelSoundSystem(item.optionValue, mData);
            } else if (item.id.equals(MenuConfigManager.TV_AUTO_FINETUNE) || item.id.equals(MenuConfigManager.TV_FINETUNE)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelIsFine(mData[3], item.optionValue);
            } else if (item.id.equals(MenuConfigManager.TV_SKIP)) {
                MenuDataHelper.getInstance(this.mContext).updateChannelSkip(mData[3], item.optionValue);
            }
        }
    }

    class ViewHolder {
        View hasSubLayout;
        ImageView imgOption;
        TextView itemName;
        LinearLayout itemOpLayout;
        TextView itemOption;
        EditText itemValue;

        ViewHolder() {
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public static class EditItem {
        public Action.DataType dataType;
        public String id;
        public boolean isDigit;
        public boolean isEnable;
        public float maxValue;
        public float minValue;
        public int optionValue;
        public String[] optionValues;
        public String title;
        public String value;

        public EditItem(String id2, String title2, String value2, boolean isEnable2, boolean isDigit2, Action.DataType dataType2) {
            this.id = id2;
            this.title = title2;
            this.value = value2;
            this.isEnable = isEnable2;
            this.isDigit = isDigit2;
            this.dataType = dataType2;
        }

        public EditItem(String id2, String title2, int opvalue, String[] optionValues2, boolean isEnable2, Action.DataType dataType2) {
            this.id = id2;
            this.title = title2;
            this.optionValue = opvalue;
            this.isEnable = isEnable2;
            this.optionValues = optionValues2;
            this.dataType = dataType2;
        }
    }
}
