package com.android.tv.ui.sidepanel;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;

public class CheckBoxItem extends CompoundButtonItem {
    private final String TAG;
    private MtkTvChannelInfoBase mChannel;
    private final TIFChannelManager mChannelDataManager;
    private TextView mChannelNumberView;
    private final boolean mLayoutForLargeDescription;
    private final TIFProgramManager mProgramDataManager;
    private TextView mProgramTitleView;

    /* access modifiers changed from: protected */
    public MtkTvChannelInfoBase getChannel() {
        return this.mChannel;
    }

    public CheckBoxItem(String title) {
        this(title, (String) null);
    }

    public CheckBoxItem(String title, String description) {
        this(title, description, false);
    }

    public CheckBoxItem(String title, String description, boolean layoutForLargeDescription) {
        super(title, description);
        this.TAG = "CheckBoxItem";
        this.mChannel = null;
        this.mChannelDataManager = null;
        this.mProgramDataManager = null;
        this.mLayoutForLargeDescription = layoutForLargeDescription;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public CheckBoxItem(com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r3, com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r4, com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager r5) {
        /*
            r2 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = ""
            r0.append(r1)
            java.lang.String r1 = r3.getServiceName()
            if (r1 != 0) goto L_0x0013
            java.lang.String r1 = ""
            goto L_0x0017
        L_0x0013:
            java.lang.String r1 = r3.getServiceName()
        L_0x0017:
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = ""
            r2.<init>(r0, r1)
            java.lang.String r0 = "CheckBoxItem"
            r2.TAG = r0
            r2.mChannel = r3
            r2.mChannelDataManager = r4
            r2.mProgramDataManager = r5
            r0 = 0
            r2.mLayoutForLargeDescription = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.ui.sidepanel.CheckBoxItem.<init>(com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase, com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager, com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager):void");
    }

    /* access modifiers changed from: protected */
    public void onBind(View view) {
        super.onBind(view);
        this.mChannelNumberView = (TextView) view.findViewById(R.id.channel_number);
        this.mProgramTitleView = (TextView) view.findViewById(R.id.program_title);
        if (this.mLayoutForLargeDescription) {
            Log.d("CheckBoxItem", "onBind()");
            CompoundButton checkBox = (CompoundButton) view.findViewById(getCompoundButtonId());
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
            lp.gravity = 49;
            lp.topMargin = view.getResources().getDimensionPixelOffset(R.dimen.option_item_check_box_margin_top);
            checkBox.setLayoutParams(lp);
            TypedValue outValue = new TypedValue();
            view.getResources().getValue(R.dimen.option_item_check_box_line_spacing_multiplier, outValue, true);
            TextView descriptionTextView = (TextView) view.findViewById(getDescriptionViewId());
            descriptionTextView.setMaxLines(Integer.MAX_VALUE);
            descriptionTextView.setLineSpacing(0.0f, outValue.getFloat());
        }
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_check_box;
    }

    /* access modifiers changed from: protected */
    public int getCompoundButtonId() {
        return R.id.check_box;
    }

    /* access modifiers changed from: protected */
    public int getTitleViewId() {
        return R.id.channel_name;
    }

    /* access modifiers changed from: protected */
    public int getDescriptionViewId() {
        return R.id.program_title;
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
        setChecked(!isChecked());
    }

    /* access modifiers changed from: protected */
    public void onUpdate() {
        String number;
        super.onUpdate();
        if (!(this.mChannel == null || this.mChannelNumberView == null)) {
            if (this.mChannel instanceof MtkTvATSCChannelInfo) {
                MtkTvATSCChannelInfo uschannel = (MtkTvATSCChannelInfo) this.mChannel;
                number = uschannel.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + uschannel.getMinorNum();
            } else {
                number = this.mChannel.getChannelNumber() + "";
            }
            this.mChannelNumberView.setText(number);
        }
        updateProgramTitle();
    }

    private void updateProgramTitle() {
    }
}
