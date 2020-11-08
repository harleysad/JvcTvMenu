package com.android.tv.ui.sidepanel;

import android.view.View;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;

public abstract class ChannelCheckItem extends CompoundButtonItem {
    private MtkTvChannelInfoBase mChannel;
    private final TIFChannelManager mChannelDataManager;
    private TextView mChannelNumberView;
    private final TIFProgramManager mProgramDataManager;
    private TextView mProgramTitleView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChannelCheckItem(com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r3, com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r4, com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager r5) {
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
            r2.mChannel = r3
            r2.mChannelDataManager = r4
            r2.mProgramDataManager = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.ui.sidepanel.ChannelCheckItem.<init>(com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase, com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager, com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager):void");
    }

    /* access modifiers changed from: protected */
    public MtkTvChannelInfoBase getChannel() {
        return this.mChannel;
    }

    /* access modifiers changed from: protected */
    public int getResourceId() {
        return R.layout.option_item_channel_check;
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
    public void onBind(View view) {
        super.onBind(view);
        this.mChannelNumberView = (TextView) view.findViewById(R.id.channel_number);
        this.mProgramTitleView = (TextView) view.findViewById(R.id.program_title);
        View channelContent = view.findViewById(R.id.channel_content);
        if (channelContent != null) {
            channelContent.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onUpdate() {
        String number;
        super.onUpdate();
        if (!(this.mChannel == null || this.mChannelNumberView == null)) {
            if (this.mChannel instanceof MtkTvATSCChannelInfo) {
                MtkTvATSCChannelInfo uschannel = (MtkTvATSCChannelInfo) this.mChannel;
                number = uschannel.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + uschannel.getMinorNum();
            } else if (this.mChannel instanceof MtkTvISDBChannelInfo) {
                MtkTvISDBChannelInfo sachannel = (MtkTvISDBChannelInfo) this.mChannel;
                number = sachannel.getMajorNum() + "." + sachannel.getMinorNum();
            } else {
                number = this.mChannel.getChannelNumber() + "";
            }
            this.mChannelNumberView.setText(number);
        }
        updateProgramTitle();
    }

    /* access modifiers changed from: protected */
    public void onUnbind() {
        this.mProgramTitleView = null;
        this.mChannelNumberView = null;
        super.onUnbind();
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
        setChecked(!isChecked());
    }

    private void updateProgramTitle() {
        this.mProgramTitleView.setText("");
    }
}
