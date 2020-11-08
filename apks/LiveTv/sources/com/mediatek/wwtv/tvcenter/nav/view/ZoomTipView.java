package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class ZoomTipView extends NavBasicView {
    private static final String TAG = "ZoomTipView";
    private IntegrationZoom mIntegrationZoom;

    public ZoomTipView(Context context) {
        super(context);
    }

    public ZoomTipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ZoomTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVisibility(int visibility) {
        if (visibility == 0) {
            startTimeout(5000);
        }
        super.setVisibility(visibility);
    }

    public boolean isKeyHandler(int keyCode) {
        return false;
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777218 || componentID == 16777222 || componentID == 16777241) {
            return true;
        }
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d(TAG, "KeyHandler: keyCode=" + keyCode);
        if (keyCode != 4) {
            switch (keyCode) {
                case 19:
                    startTimeout(5000);
                    this.mIntegrationZoom.moveScreenZoom(0);
                    return true;
                case 20:
                    startTimeout(5000);
                    this.mIntegrationZoom.moveScreenZoom(1);
                    return true;
                case 21:
                    startTimeout(5000);
                    this.mIntegrationZoom.moveScreenZoom(2);
                    return true;
                case 22:
                    startTimeout(5000);
                    this.mIntegrationZoom.moveScreenZoom(3);
                    return true;
                default:
                    return false;
            }
        } else {
            MtkLog.d(TAG, "KeyHandler hide");
            setVisibility(8);
            SundryShowTextView sundryShowTextView = (SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
            if (sundryShowTextView == null || sundryShowTextView.getVisibility() != 0) {
                return true;
            }
            sundryShowTextView.setVisibility(8);
            return true;
        }
    }

    public boolean initView() {
        ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.nav_zoom_layout, this);
        this.componentID = NavBasic.NAV_COMP_ID_ZOOM_PAN;
        this.mIntegrationZoom = IntegrationZoom.getInstance(this.mContext);
        return true;
    }
}
