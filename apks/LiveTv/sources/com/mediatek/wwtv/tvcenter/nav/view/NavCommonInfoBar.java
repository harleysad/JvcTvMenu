package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public class NavCommonInfoBar extends NavBaseInfoBar {
    public static final int COMMON_INFO = 0;
    public static final int ERROR_INFO = 2;
    public static final int WARRNING_INFO = 1;
    private int infoType = 0;
    private ImageView mIconImage;
    private TextView mInfo;
    private String mToastString = "";

    public NavCommonInfoBar(Activity context) {
        super(context, R.layout.nav_common_info_nf);
    }

    public NavCommonInfoBar(Activity context, String info) {
        super(context, R.layout.nav_common_info_nf);
        this.mToastString = info;
    }

    public NavCommonInfoBar(Activity context, String info, int type) {
        super(context, R.layout.nav_common_info_nf);
        this.mToastString = info;
        this.infoType = type;
    }

    public NavCommonInfoBar(Activity context, int layoutID, Long duration, String strInfo) {
        super(context, layoutID);
        this.mToastString = strInfo;
    }

    public void setInfo(String info) {
        this.mInfo.setText(info);
    }

    public void initView() {
        super.initView();
        this.mIconImage = (ImageView) getContentView().findViewById(R.id.nav_common_info_icon);
        switch (this.infoType) {
            case 0:
            case 2:
                break;
            case 1:
                this.mIconImage.setVisibility(0);
                this.mIconImage.setBackgroundResource(R.drawable.nav_ib_warning_icon);
                break;
            default:
                this.mIconImage.setVisibility(0);
                this.mIconImage.setBackgroundResource(R.drawable.nav_ib_warning_icon);
                break;
        }
        this.mInfo = (TextView) getContentView().findViewById(R.id.info);
        this.mInfo.setText(this.mToastString);
    }
}
