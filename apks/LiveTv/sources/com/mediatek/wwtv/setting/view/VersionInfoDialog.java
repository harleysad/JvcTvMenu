package com.mediatek.wwtv.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class VersionInfoDialog extends Dialog {
    public int height = 0;
    WindowManager.LayoutParams lp;
    private Context mContext;
    private TVContent mTVContent;
    private TextView modelNameShow;
    private TextView serialNumShow;
    private TextView versionShow;
    public int width = 0;
    Window window;
    private int xOff;
    private int yOff;

    public VersionInfoDialog(Context context) {
        super(context, 2131755419);
        this.mContext = context;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_version_info);
        this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
        this.lp.width = this.width;
        this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.35d);
        this.lp.height = this.height;
        this.lp.x = 0 - (this.lp.width / 3);
        this.window.setAttributes(this.lp);
        init();
    }

    public void setPositon(int xoff, int yoff) {
        Window window2 = getWindow();
        WindowManager.LayoutParams lp2 = window2.getAttributes();
        lp2.x = xoff;
        lp2.y = yoff;
        this.xOff = xoff;
        this.yOff = yoff;
        window2.setAttributes(lp2);
    }

    public void init() {
        this.mTVContent = TVContent.getInstance(this.mContext);
        this.modelNameShow = (TextView) findViewById(R.id.common_versioninfo_name_r);
        MtkLog.e("chengcl", "modelNameShow==" + this.modelNameShow);
        this.versionShow = (TextView) findViewById(R.id.common_versioninfo_ver_r);
        this.serialNumShow = (TextView) findViewById(R.id.common_versioninfo_num_r);
        setValue();
    }

    public void setValue() {
        String modelName = this.mTVContent.getSysVersion(3, "");
        MtkLog.e("chengcl", "modelNameShow==" + this.modelNameShow + "  modelName=" + modelName);
        this.modelNameShow.setText(modelName.substring(modelName.lastIndexOf("_") + 1));
        this.versionShow.setText(this.mTVContent.getSysVersion(0, ""));
        this.serialNumShow.setText(this.mTVContent.getSysVersion(2, ""));
    }
}
