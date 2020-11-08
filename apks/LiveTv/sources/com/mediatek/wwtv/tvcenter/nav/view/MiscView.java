package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;

public class MiscView extends NavBasicView implements ComponentStatusListener.ICStatusListener {
    private static final int NO_FUNCTION = 0;
    private static final String TAG = "MiscView";
    private static final int VIDEO_3D = 1;
    /* access modifiers changed from: private */
    public ComponentsManager comManager;
    private TvCallbackHandler mTvCallbackHandler;
    /* access modifiers changed from: private */
    public TextView mView;
    private Handler miscHandler;

    public MiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.miscHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1879048224) {
                    TvCallbackData data = (TvCallbackData) msg.obj;
                    MtkLog.d(MiscView.TAG, "come in TvCallbackConst.MSG_CB_FEATURE_MSG ==" + data.param1);
                    if (data.param1 == 1) {
                        MiscView.this.mView.setText(R.string.menu_video_3d_wear_glass);
                        MiscView.this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_SUNDRY);
                    }
                }
            }
        };
        this.componentID = NavBasic.NAV_COMP_ID_MISC;
        ComponentStatusListener.getInstance().addListener(10, this);
    }

    public MiscView(Context context) {
        this(context, (AttributeSet) null);
    }

    public boolean initView() {
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.nav_sundry_view, this);
        this.mView = (TextView) findViewById(R.id.nav_sundry_textview_id);
        this.comManager = ComponentsManager.getInstance();
        this.mTvCallbackHandler = TvCallbackHandler.getInstance();
        this.mTvCallbackHandler.addCallBackListener(this.miscHandler);
        return true;
    }

    public boolean deinitView() {
        this.mTvCallbackHandler.removeCallBackListener(this.miscHandler);
        return true;
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777218 || componentID == 16777220) {
            return true;
        }
        return false;
    }

    public boolean isKeyHandler(int keyCode) {
        MtkLog.d(TAG, "isKeyHandler keyCode =" + keyCode);
        if (keyCode != 171 && keyCode != 227 && keyCode != 255 && keyCode != 10065) {
            return false;
        }
        this.mView.setText(R.string.nav_no_function);
        this.comManager.showNavComponent(NavBasic.NAV_COMP_ID_MISC);
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
            StateDvr.getInstance().clearWindow(true);
        }
        return true;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "KeyHandler>no fromNative");
        return KeyHandler(keyCode, event, false);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        if (this.mView == null) {
            return false;
        }
        MtkLog.d(TAG, "KeyHandler>" + fromNative + "  " + keyCode + "  " + this.mView.getText().toString() + "  " + getVisibility());
        if (keyCode != 4 || getVisibility() != 0 || this.mContext == null || !this.mView.getText().equals(this.mContext.getText(R.string.nav_no_function))) {
            return false;
        }
        setVisibility(8);
        return true;
    }

    public void setVisibility(int visibility) {
        MtkLog.d(TAG, "setVisibility visibility =" + visibility);
        if (visibility == 0) {
            startTimeout(5000);
        }
        super.setVisibility(visibility);
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus>>" + statusID + "  " + value);
        if (statusID == 10 && this.mContext != null && getVisibility() == 0) {
            setVisibility(8);
        }
    }
}
