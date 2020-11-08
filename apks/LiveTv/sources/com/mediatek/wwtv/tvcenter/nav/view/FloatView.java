package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import com.mediatek.twoworlds.tv.MtkTvEASBase;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.model.MtkTvEASParaBase;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.FocusLabelControl;
import com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.Constants;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;

public class FloatView extends NavBasicMisc implements ComponentStatusListener.ICStatusListener {
    private static final String EAS_ACTION = "mtk.intent.EAS.message";
    /* access modifiers changed from: private */
    public static String TAG = "FloatView";
    private static int mEastype = 0;
    private static FloatView mView;
    /* access modifiers changed from: private */
    public boolean isEasPlaying;

    public static synchronized FloatView getInstance(Context context) {
        FloatView floatView;
        synchronized (FloatView.class) {
            MtkLog.v(TAG, "FloatView ----  getInstance()");
            if (mView == null) {
                mView = new FloatView(context);
            }
            floatView = mView;
        }
        return floatView;
    }

    public FloatView(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.componentID = 16777217;
        this.componentPriority = 13;
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(10, this);
    }

    public boolean isVisible() {
        return super.isVisible();
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        String str = TAG;
        MtkLog.i(str, "keyCode:" + keyCode);
        switch (keyCode) {
            case 24:
            case 25:
                MtkLog.v(TAG, "FloatView ----  KeyMap.KEYCODE_VOLUME_UP");
                return false;
            case 26:
                return false;
            default:
                switch (keyCode) {
                    case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                    case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                        if (mEastype != 1) {
                            return true;
                        }
                        new MtkTvEASBase().EASSetAndroidLaunchStatus(true);
                        return false;
                    default:
                        return true;
                }
        }
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        return super.KeyHandler(keyCode, event);
    }

    public boolean initView() {
        return true;
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        MtkLog.v(TAG, "setVisibility ---------");
    }

    public void handleEasMessage(int msgType, int data) {
        MultiViewControl multiViewControl;
        String str = TAG;
        MtkLog.v(str, "handleEasMessage, msgType=" + msgType + ", data=" + data);
        if (msgType == 1) {
            this.isEasPlaying = true;
            String str2 = TAG;
            MtkLog.d(str2, "SystemProperties.set isEasPlaying=" + this.isEasPlaying);
            SystemProperties.set(Constants.MTK_3RD_APP_FLAG, this.isEasPlaying ? "1" : "0");
            if (!isVisible()) {
                TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
                if (!MarketRegionInfo.isFunctionSupport(13)) {
                    FocusLabelControl focusLabelControl = (FocusLabelControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP);
                    if (focusLabelControl != null) {
                        focusLabelControl.setVisibility(4);
                    }
                } else if (MarketRegionInfo.isFunctionSupport(26) && (multiViewControl = (MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP)) != null) {
                    multiViewControl.setVisibility(4);
                }
                ComponentsManager.getInstance().showNavComponent(16777217);
            }
            mEastype = data;
        } else if (msgType == 0) {
            this.isEasPlaying = false;
            String str3 = TAG;
            MtkLog.d(str3, "SystemProperties.set isEasPlaying=" + this.isEasPlaying);
            SystemProperties.set(Constants.MTK_3RD_APP_FLAG, this.isEasPlaying ? "1" : "0");
            setVisibility(4);
            mEastype = 0;
        }
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                if (FloatView.this.mContext == null) {
                    MtkLog.d(FloatView.TAG, "mContext==null");
                    return;
                }
                MtkLog.d(FloatView.TAG, "Send broadcast for mtk.intent.EAS.message.permission");
                Intent intent = new Intent();
                intent.putExtra("EASPLAYFLAG", FloatView.this.isEasPlaying);
                intent.setAction(FloatView.EAS_ACTION);
                FloatView.this.mContext.sendBroadcast(intent, "mtk.intent.EAS.message.permission");
            }
        });
    }

    public boolean isEasPlaying() {
        return this.isEasPlaying;
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 3) {
            MtkTvEASParaBase info = new MtkTvEASParaBase();
            new MtkTvEASBase().getEASCurrentStatus(info);
            String str = TAG;
            MtkLog.v(str, "1 updateComponentStatus, EASType=" + info.getEASType() + ", ChannelChange=" + info.getChannelChange());
            handleEasMessage(info.getEASType(), info.getChannelChange());
        } else if (statusID == 10 && value != -1) {
            MtkTvEASBase eas = new MtkTvEASBase();
            if (eas.EASGetAndroidLaunchStatus()) {
                MtkLog.d(TAG, "EASSetAndroidLaunchStatus(false)");
                eas.EASSetAndroidLaunchStatus(false);
            }
            MtkTvEASParaBase info2 = new MtkTvEASParaBase();
            eas.getEASCurrentStatus(info2);
            String str2 = TAG;
            MtkLog.v(str2, "updateComponentStatus, EASType=" + info2.getEASType() + ", ChannelChange=" + info2.getChannelChange());
            handleEasMessage(info2.getEASType(), info2.getChannelChange());
        }
    }
}
