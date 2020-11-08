package com.android.tv.settings.partnercustomizer.picture;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class MenuMjcDemoDialog extends Dialog {
    private Context mContext;
    private TextView vLeft;
    private ImageView vMid;
    private TextView vRight;

    public MenuMjcDemoDialog(Context context) {
        super(context, 2131755396);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partner_menu_mjc_demo);
        this.vLeft = (TextView) findViewById(R.id.mjc_left);
        this.vRight = (TextView) findViewById(R.id.mjc_right);
        this.vMid = (ImageView) findViewById(R.id.mjc_mid);
        this.vMid.setFocusable(true);
        this.vMid.requestFocus();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        initData();
        TVSettingConfig.getInstance(this.mContext).setConifg(MtkTvConfigTypeBase.CFG_VIDEO_VID_MJC_DEMO_STATUS, 1);
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        TVSettingConfig.getInstance(this.mContext).setConifg(MtkTvConfigTypeBase.CFG_VIDEO_VID_MJC_DEMO_STATUS, 0);
        super.onStop();
    }

    private void initData() {
        int value = 0;
        String partition = PreferenceUtils.getSettingStringValue(this.mContext.getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION);
        MtkLog.d("MJCDemoDialog", "getString = " + partition);
        if (partition.equalsIgnoreCase(this.mContext.getString(R.string.pic_advance_video_entries_all))) {
            value = 0;
        } else if (partition.equalsIgnoreCase(this.mContext.getString(R.string.pic_advance_video_entries_right))) {
            value = 1;
        } else if (partition.equalsIgnoreCase(this.mContext.getString(R.string.pic_advance_video_entries_left))) {
            value = 2;
        }
        MtkLog.d("MJCDemoDialog", "get TV_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION == " + value);
        switch (value) {
            case 0:
                this.vRight.setVisibility(8);
                this.vLeft.setVisibility(8);
                return;
            case 1:
                this.vRight.setVisibility(0);
                this.vLeft.setVisibility(0);
                this.vLeft.setText(this.mContext.getString(R.string.pic_advance_video_entries_off));
                this.vRight.setText(this.mContext.getString(R.string.pic_advance_video_entries_on));
                return;
            case 2:
                this.vRight.setVisibility(0);
                this.vLeft.setVisibility(0);
                this.vRight.setText(this.mContext.getString(R.string.pic_advance_video_entries_off));
                this.vLeft.setText(this.mContext.getString(R.string.pic_advance_video_entries_on));
                return;
            default:
                return;
        }
    }
}
