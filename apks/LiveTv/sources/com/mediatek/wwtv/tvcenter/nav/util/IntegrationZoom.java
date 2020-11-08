package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.model.MtkTvRectangle;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;

public class IntegrationZoom {
    private static final String SOURCE_MAIN = "main";
    private static final String SOURCE_SUB = "sub";
    private static final String TAG = "IntegrationZoom";
    public static final int ZOOM_0_5 = 0;
    public static final int ZOOM_1 = 1;
    public static final int ZOOM_2 = 2;
    public static final int ZOOM_DOWN = 1;
    public static final int ZOOM_LEFT = 2;
    public static final int ZOOM_RIGHT = 3;
    public static final int ZOOM_UP = 0;
    private static IntegrationZoom instance;
    /* access modifiers changed from: private */
    public int current_zoom = 1;
    /* access modifiers changed from: private */
    public TVAsyncExecutor executor = TVAsyncExecutor.getInstance();
    /* access modifiers changed from: private */
    public MtkTvAppTV mtkTvAppTv = MtkTvAppTV.getInstance();
    private MtkTvUtil mtkTvUtil = MtkTvUtil.getInstance();
    /* access modifiers changed from: private */
    public ZoomListener zoomListener = null;

    public interface ZoomListener {
        void zoomShow(int i);
    }

    private IntegrationZoom(Context context) {
    }

    public static synchronized IntegrationZoom getInstance(Context context) {
        IntegrationZoom integrationZoom;
        synchronized (IntegrationZoom.class) {
            if (instance == null) {
                instance = new IntegrationZoom(context);
            }
            integrationZoom = instance;
        }
        return integrationZoom;
    }

    public void setZoomListener(ZoomListener listener) {
        this.zoomListener = listener;
    }

    public boolean screenModeZoomShow() {
        boolean show = MtkTvAVMode.getInstance().isZoomEnable();
        MtkLog.d(TAG, "screenModeZoomShow show = " + show);
        return show;
    }

    public void moveScreenZoom(int moveType) {
        MtkTvRectangle mSrcRectangleRectF;
        MtkLog.d(TAG, "moveScreenZoom moveType =" + moveType + "screenModeZoomShow() =" + screenModeZoomShow() + "current_zoom =" + this.current_zoom);
        if (screenModeZoomShow() && this.current_zoom == 2 && (mSrcRectangleRectF = this.mtkTvUtil.getScreenSourceRect("main")) != null) {
            float l = mSrcRectangleRectF.getX();
            float t = mSrcRectangleRectF.getY();
            float r = mSrcRectangleRectF.getW();
            float b = mSrcRectangleRectF.getH();
            switch (moveType) {
                case 0:
                    t = (float) (((double) t) - 0.02d);
                    if (((double) t) < 0.0d) {
                        t = 0.0f;
                        break;
                    }
                    break;
                case 1:
                    t = (float) (((double) t) + 0.02d);
                    if (((double) t) > 0.5d) {
                        t = 0.5f;
                        break;
                    }
                    break;
                case 2:
                    l = (float) (((double) l) - 0.02d);
                    if (((double) l) < 0.0d) {
                        l = 0.0f;
                        break;
                    }
                    break;
                case 3:
                    l = (float) (((double) l) + 0.02d);
                    if (((double) l) > 0.5d) {
                        l = 0.5f;
                        break;
                    }
                    break;
            }
            CommonIntegration.getInstance().updateOutputChangeState("Before_Zoom_Mode_Chg");
            this.mtkTvUtil.setScreenSourceRect("main", new MtkTvRectangle(l, t, r, b));
            CommonIntegration.getInstance().updateOutputChangeState("SAfter_Zoom_Mode_Chg");
        }
    }

    public boolean showCurrentZoom() {
        MtkLog.d(TAG, "showCurrentZoom");
        if (!screenModeZoomShow()) {
            return false;
        }
        if (this.zoomListener == null) {
            return true;
        }
        this.zoomListener.zoomShow(getCurrentZoom());
        return true;
    }

    public int getCurrentZoom() {
        MtkTvRectangle mScreenRectangle = this.mtkTvUtil.getScreenOutputDispRect("main");
        MtkTvRectangle mSrcRectangle = this.mtkTvUtil.getScreenSourceRect("main");
        if (!(mScreenRectangle == null || mSrcRectangle == null)) {
            float tmpscr = mScreenRectangle.getW();
            float tmpsrc = mSrcRectangle.getW();
            if (((double) tmpscr) < 0.6d && ((double) tmpscr) > 0.4d) {
                this.current_zoom = 0;
            } else if (((double) tmpsrc) >= 0.6d || ((double) tmpsrc) <= 0.4d) {
                this.current_zoom = 1;
            } else {
                this.current_zoom = 2;
            }
        }
        MtkLog.d(TAG, "getCurrentZoom current_zoom =" + this.current_zoom);
        return this.current_zoom;
    }

    public boolean nextZoom() {
        MtkLog.d(TAG, "nextZoom~~");
        if (!screenModeZoomShow()) {
            return false;
        }
        this.current_zoom = getCurrentZoom();
        MtkLog.d(TAG, "nextZoom current_zoom = " + this.current_zoom);
        this.current_zoom = (this.current_zoom + 1) % 3;
        MtkLog.d(TAG, "nextZoom add current_zoom = " + this.current_zoom);
        this.executor.execute(new Runnable() {
            public void run() {
                IntegrationZoom.this.setZoomMode(IntegrationZoom.this.current_zoom);
            }
        });
        return true;
    }

    public void setZoomMode(int zoomMode) {
        if (screenModeZoomShow()) {
            this.mtkTvAppTv.setVideoMute("main", true);
            switch (zoomMode) {
                case 0:
                    CommonIntegration.getInstance().updateOutputChangeState("Before_Zoom_Mode_Chg");
                    this.mtkTvUtil.setScreenSourceRect("main", new MtkTvRectangle(0.0f, 0.0f, 1.0f, 1.0f));
                    this.mtkTvUtil.setScreenOutputDispRect("main", new MtkTvRectangle(0.25f, 0.25f, 0.5f, 0.5f));
                    CommonIntegration.getInstance().updateOutputChangeState("SAfter_Zoom_Mode_Chg");
                    break;
                case 1:
                    CommonIntegration.getInstance().updateOutputChangeState("Before_Zoom_Mode_Chg");
                    this.mtkTvUtil.setScreenSourceRect("main", new MtkTvRectangle(0.0f, 0.0f, 1.0f, 1.0f));
                    this.mtkTvUtil.setScreenOutputDispRect("main", new MtkTvRectangle(0.0f, 0.0f, 1.0f, 1.0f));
                    CommonIntegration.getInstance().updateOutputChangeState("SAfter_Zoom_Mode_Chg");
                    break;
                case 2:
                    CommonIntegration.getInstance().updateOutputChangeState("Before_Zoom_Mode_Chg");
                    this.mtkTvUtil.setScreenSourceRect("main", new MtkTvRectangle(0.25f, 0.25f, 0.5f, 0.5f));
                    this.mtkTvUtil.setScreenOutputDispRect("main", new MtkTvRectangle(0.0f, 0.0f, 1.0f, 1.0f));
                    CommonIntegration.getInstance().updateOutputChangeState("SAfter_Zoom_Mode_Chg");
                    break;
            }
            this.current_zoom = getCurrentZoom();
            TurnkeyUiMainActivity.getInstance().getHandler().post(new Runnable() {
                public void run() {
                    IntegrationZoom.this.executor.execute(new Runnable() {
                        public void run() {
                            IntegrationZoom.this.mtkTvAppTv.setVideoMute("main", false);
                        }
                    });
                    if (IntegrationZoom.this.zoomListener != null) {
                        IntegrationZoom.this.zoomListener.zoomShow(IntegrationZoom.this.current_zoom);
                    }
                }
            });
        }
    }

    public void setZoomModeToNormal() {
        setZoomMode(1);
    }

    public void setZoomModeToNormalWithThread() {
        this.executor.execute(new Runnable() {
            public void run() {
                if (IntegrationZoom.this.getCurrentZoom() != 1 && IntegrationZoom.this.screenModeZoomShow()) {
                    IntegrationZoom.this.setZoomModeToNormal();
                    final ZoomTipView mZoomTip = (ZoomTipView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
                    if (mZoomTip != null && mZoomTip.getVisibility() == 0) {
                        TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                            public void run() {
                                mZoomTip.setVisibility(8);
                            }
                        });
                    }
                }
            }
        });
    }
}
