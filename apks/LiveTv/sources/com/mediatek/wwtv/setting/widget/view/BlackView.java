package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class BlackView extends CommonDialog {
    public BlackView(Context context) {
        super(context, R.layout.blackview);
        getWindow().setLayout(ScreenConstant.SCREEN_WIDTH, ScreenConstant.SCREEN_HEIGHT);
    }

    public BlackView(Context context, int position) {
        super(context, R.layout.pvr_tshfit_schudulelist);
        initData();
    }

    private void initData() {
        getWindow().setLayout(ScreenConstant.SCREEN_WIDTH, ScreenConstant.SCREEN_HEIGHT);
    }

    public void initView() {
        super.initView();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dismiss() {
        /*
            r1 = this;
            boolean r0 = r1.isShowing()     // Catch:{ Exception -> 0x000c, all -> 0x000a }
            if (r0 == 0) goto L_0x000d
            super.dismiss()     // Catch:{ Exception -> 0x000c, all -> 0x000a }
            goto L_0x000d
        L_0x000a:
            r0 = move-exception
            throw r0
        L_0x000c:
            r0 = move-exception
        L_0x000d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.BlackView.dismiss():void");
    }

    public void show() {
        getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
        initData();
        super.show();
    }
}
