package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.view.ScanOptionView;
import java.util.List;

public class ScanFactorAdapter extends BaseAdapter {
    Context mContext;
    List<ScanFactorItem> mList;

    public interface ScanOptionChangeListener {
        void onScanOptionChange(String str);
    }

    public ScanFactorAdapter(Context mContext2, List<ScanFactorItem> mList2) {
        this.mContext = mContext2;
        this.mList = mList2;
    }

    public List<ScanFactorItem> getList() {
        return this.mList;
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter$ViewHolder} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r10, android.view.View r11, android.view.ViewGroup r12) {
        /*
            r9 = this;
            java.util.List<com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter$ScanFactorItem> r0 = r9.mList
            java.lang.Object r0 = r0.get(r10)
            com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter$ScanFactorItem r0 = (com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter.ScanFactorItem) r0
            r1 = 2131362294(0x7f0a01f6, float:1.8344365E38)
            r2 = 0
            if (r11 != 0) goto L_0x00bb
            android.content.Context r3 = r9.mContext
            android.view.LayoutInflater r3 = android.view.LayoutInflater.from(r3)
            r4 = 2131493181(0x7f0c013d, float:1.8609835E38)
            android.view.View r11 = r3.inflate(r4, r12, r2)
            com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter$ViewHolder r4 = new com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter$ViewHolder
            r4.<init>()
            android.view.View r5 = r11.findViewById(r1)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.factorTitle = r5
            r5 = 2131362295(0x7f0a01f7, float:1.8344367E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.factorNum = r5
            r5 = 2131362296(0x7f0a01f8, float:1.8344369E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.factorNUmImg = r5
            r5 = 2131362300(0x7f0a01fc, float:1.8344377E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            r4.optionLayout = r5
            r5 = 2131362293(0x7f0a01f5, float:1.8344362E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            r4.inputLayout = r5
            r5 = 2131362291(0x7f0a01f3, float:1.8344358E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            r4.doLayout = r5
            r5 = 2131362297(0x7f0a01f9, float:1.834437E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            r4.numLayout = r5
            r5 = 2131362302(0x7f0a01fe, float:1.834438E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.RelativeLayout r5 = (android.widget.RelativeLayout) r5
            r4.progressLayout = r5
            r5 = 2131362303(0x7f0a01ff, float:1.8344383E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.progressPrecent = r5
            r5 = 2131362304(0x7f0a0200, float:1.8344385E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.progressTitle = r5
            r5 = 2131362298(0x7f0a01fa, float:1.8344373E38)
            android.view.View r5 = r11.findViewById(r5)
            com.mediatek.wwtv.setting.view.ScanOptionView r5 = (com.mediatek.wwtv.setting.view.ScanOptionView) r5
            r4.factorOption = r5
            r5 = 2131362299(0x7f0a01fb, float:1.8344375E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.factorOPtionImg = r5
            r5 = 2131362301(0x7f0a01fd, float:1.8344379E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.ProgressBar r5 = (android.widget.ProgressBar) r5
            r4.factorProgress = r5
            r5 = 2131362292(0x7f0a01f4, float:1.834436E38)
            android.view.View r5 = r11.findViewById(r5)
            android.widget.EditText r5 = (android.widget.EditText) r5
            r4.factorInput = r5
            r11.setTag(r4)
            goto L_0x00c2
        L_0x00bb:
            java.lang.Object r3 = r11.getTag()
            r4 = r3
            com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter$ViewHolder r4 = (com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter.ViewHolder) r4
        L_0x00c2:
            r3 = r4
            android.widget.TextView r4 = r3.factorTitle
            java.lang.String r5 = r0.title
            r4.setText(r5)
            boolean r4 = r0.isEnable
            r5 = 1
            r6 = -1
            r7 = 8
            if (r4 != 0) goto L_0x00f9
            android.widget.TextView r4 = r3.factorTitle
            r8 = -7829368(0xffffffffff888888, float:NaN)
            r4.setTextColor(r8)
            android.widget.TextView r4 = r3.factorNum
            r4.setTextColor(r8)
            com.mediatek.wwtv.setting.view.ScanOptionView r4 = r3.factorOption
            r4.setTextColor(r8)
            android.widget.TextView r4 = r3.factorNum
            r4.setEnabled(r2)
            com.mediatek.wwtv.setting.view.ScanOptionView r4 = r3.factorOption
            r4.setEnabled(r2)
            android.widget.ImageView r4 = r3.factorNUmImg
            r4.setVisibility(r7)
            android.widget.ImageView r4 = r3.factorOPtionImg
            r4.setVisibility(r7)
            goto L_0x011c
        L_0x00f9:
            android.widget.TextView r4 = r3.factorTitle
            r4.setTextColor(r6)
            android.widget.TextView r4 = r3.factorNum
            r4.setTextColor(r6)
            com.mediatek.wwtv.setting.view.ScanOptionView r4 = r3.factorOption
            r4.setTextColor(r6)
            android.widget.TextView r4 = r3.factorNum
            r4.setEnabled(r5)
            com.mediatek.wwtv.setting.view.ScanOptionView r4 = r3.factorOption
            r4.setEnabled(r5)
            android.widget.ImageView r4 = r3.factorNUmImg
            r4.setVisibility(r2)
            android.widget.ImageView r4 = r3.factorOPtionImg
            r4.setVisibility(r2)
        L_0x011c:
            int r4 = r0.factorType
            if (r4 != 0) goto L_0x0142
            android.widget.LinearLayout r4 = r3.numLayout
            r4.setVisibility(r2)
            android.widget.LinearLayout r2 = r3.optionLayout
            r2.setVisibility(r7)
            android.widget.RelativeLayout r2 = r3.progressLayout
            r2.setVisibility(r7)
            android.widget.LinearLayout r2 = r3.inputLayout
            r2.setVisibility(r7)
            android.widget.LinearLayout r2 = r3.doLayout
            r2.setVisibility(r7)
            android.widget.TextView r2 = r3.factorNum
            java.lang.String r4 = r0.value
            r2.setText(r4)
            goto L_0x025a
        L_0x0142:
            int r4 = r0.factorType
            if (r4 != r5) goto L_0x016c
            android.widget.LinearLayout r4 = r3.numLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.optionLayout
            r4.setVisibility(r2)
            android.widget.RelativeLayout r2 = r3.progressLayout
            r2.setVisibility(r7)
            android.widget.LinearLayout r2 = r3.inputLayout
            r2.setVisibility(r7)
            android.widget.LinearLayout r2 = r3.doLayout
            r2.setVisibility(r7)
            com.mediatek.wwtv.setting.view.ScanOptionView r2 = r3.factorOption
            java.lang.String r4 = r0.id
            java.lang.String[] r5 = r0.optionValues
            int r6 = r0.optionValue
            r2.bindData(r4, r5, r6)
            goto L_0x025a
        L_0x016c:
            int r4 = r0.factorType
            r5 = 2
            if (r4 != r5) goto L_0x01b9
            android.widget.TextView r4 = r3.factorTitle
            java.lang.String r5 = ""
            r4.setText(r5)
            android.widget.TextView r4 = r3.progressTitle
            java.lang.String r5 = r0.title
            r4.setText(r5)
            android.widget.LinearLayout r4 = r3.numLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.optionLayout
            r4.setVisibility(r7)
            android.widget.RelativeLayout r4 = r3.progressLayout
            r4.setVisibility(r2)
            android.widget.LinearLayout r2 = r3.inputLayout
            r2.setVisibility(r7)
            android.widget.LinearLayout r2 = r3.doLayout
            r2.setVisibility(r7)
            android.widget.ProgressBar r2 = r3.factorProgress
            int r4 = r0.progress
            r2.setProgress(r4)
            android.widget.TextView r2 = r3.progressPrecent
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r0.progress
            r4.append(r5)
            java.lang.String r5 = "%"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r2.setText(r4)
            goto L_0x025a
        L_0x01b9:
            int r4 = r0.factorType
            r5 = 3
            if (r4 != r5) goto L_0x023c
            android.widget.LinearLayout r4 = r3.numLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.optionLayout
            r4.setVisibility(r7)
            android.widget.RelativeLayout r4 = r3.progressLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.inputLayout
            r4.setVisibility(r2)
            android.widget.LinearLayout r2 = r3.doLayout
            r2.setVisibility(r7)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = r0.inputValue
            r2.append(r4)
            java.lang.String r4 = ""
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.String r4 = r0.id
            java.lang.String r5 = "UNDEFINE_channel_start_frequency"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x0223
            java.lang.String r4 = r0.id
            java.lang.String r5 = "UNDEFINE_channel_end_frequency"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x01ff
            goto L_0x0223
        L_0x01ff:
            java.lang.String r4 = r0.id
            java.lang.String r5 = "tv_dvbc_scan_frequency"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x0213
            java.lang.String r4 = r0.id
            java.lang.String r5 = "tv_dvbc_scan_networkid"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0236
        L_0x0213:
            int r4 = r0.inputValue
            if (r4 == r6) goto L_0x021c
            int r4 = r0.inputValue
            r5 = -3
            if (r4 != r5) goto L_0x0236
        L_0x021c:
            java.lang.String r4 = r0.value
            if (r4 == 0) goto L_0x0236
            java.lang.String r2 = r0.value
            goto L_0x0236
        L_0x0223:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r0.inputValue
            r4.append(r5)
            java.lang.String r5 = ".00"
            r4.append(r5)
            java.lang.String r2 = r4.toString()
        L_0x0236:
            android.widget.EditText r4 = r3.factorInput
            r4.setText(r2)
            goto L_0x025a
        L_0x023c:
            int r4 = r0.factorType
            r5 = 4
            if (r4 != r5) goto L_0x025a
            android.widget.LinearLayout r4 = r3.numLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.optionLayout
            r4.setVisibility(r7)
            android.widget.RelativeLayout r4 = r3.progressLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.inputLayout
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r3.doLayout
            r4.setVisibility(r2)
        L_0x025a:
            r11.setTag(r1, r0)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.adapter.ScanFactorAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    class ViewHolder {
        LinearLayout doLayout;
        public EditText factorInput;
        public ImageView factorNUmImg;
        public TextView factorNum;
        public ImageView factorOPtionImg;
        public ScanOptionView factorOption;
        public ProgressBar factorProgress;
        TextView factorTitle;
        LinearLayout inputLayout;
        LinearLayout numLayout;
        LinearLayout optionLayout;
        RelativeLayout progressLayout;
        public TextView progressPrecent;
        public TextView progressTitle;

        ViewHolder() {
        }
    }

    public static class ScanFactorItem {
        public int factorType;
        public String id;
        public int inputValue;
        public boolean isEnable;
        public ScanOptionChangeListener listener;
        public int maxValue;
        public int minValue;
        public int optionValue = -1;
        public String[] optionValues;
        public int progress = -1;
        public String title;
        public String value;

        public ScanFactorItem(String id2, String title2, String value2, boolean isEnable2) {
            this.id = id2;
            this.title = title2;
            this.value = value2;
            this.isEnable = isEnable2;
            this.factorType = 0;
        }

        public ScanFactorItem(String id2, String title2, int optionValue2, String[] optionValues2, boolean isEnable2) {
            this.id = id2;
            this.title = title2;
            this.optionValue = optionValue2;
            this.optionValues = optionValues2;
            this.isEnable = isEnable2;
            this.factorType = 1;
        }

        public ScanFactorItem(String id2, String title2, int progress2, boolean isEnable2) {
            this.id = id2;
            this.title = title2;
            this.progress = progress2;
            this.isEnable = isEnable2;
            this.factorType = 2;
        }

        public ScanFactorItem(String id2, String title2, int inputValue2, int minValue2, int maxValue2, boolean isEnable2) {
            this.id = id2;
            this.title = title2;
            this.inputValue = inputValue2;
            this.isEnable = isEnable2;
            this.minValue = minValue2;
            this.maxValue = maxValue2;
            this.factorType = 3;
        }

        public ScanFactorItem(String id2, String title2, boolean isEnable2) {
            this.id = id2;
            this.title = title2;
            this.isEnable = isEnable2;
            this.factorType = 4;
        }

        public void addScanOptionChangeListener(ScanOptionChangeListener listener2) {
            this.listener = listener2;
        }
    }
}
