package com.mediatek.wwtv.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;

public class MenuMjcDemoDialog extends Dialog {
    private Context mContext;
    private TextView vLeft;
    private TextView vRight;

    public MenuMjcDemoDialog(Context context) {
        super(context, 2131755375);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_mjc_demo);
        this.vLeft = (TextView) findViewById(R.id.mjc_left);
        this.vRight = (TextView) findViewById(R.id.mjc_right);
        initData();
    }

    private void initData() {
        MenuConfigManager mg = MenuConfigManager.getInstance(this.mContext);
        switch (mg.getDefault("g_video__vid_mjc_demo")) {
            case 0:
                this.vRight.setVisibility(8);
                this.vLeft.setVisibility(8);
                break;
            case 1:
                this.vRight.setVisibility(0);
                this.vLeft.setVisibility(0);
                this.vLeft.setText(this.mContext.getString(R.string.common_off));
                this.vRight.setText(this.mContext.getString(R.string.common_on));
                break;
            case 2:
                this.vRight.setVisibility(0);
                this.vLeft.setVisibility(0);
                this.vLeft.setText(this.mContext.getString(R.string.common_on));
                this.vRight.setText(this.mContext.getString(R.string.common_off));
                break;
        }
        mg.setValue("g_video__vid_mjc_status", 1);
    }
}
