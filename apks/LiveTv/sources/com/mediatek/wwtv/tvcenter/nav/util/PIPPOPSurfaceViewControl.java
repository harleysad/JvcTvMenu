package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class PIPPOPSurfaceViewControl {
    private static final int SCREEN_MODE_AUTO = 7;
    private static final int SCREEN_MODE_DOT_BY_DOT = 6;
    private static PIPPOPSurfaceViewControl mpPippopSurfaceViewControl;
    private static int pipOutputPos;
    private static int pipOutputSize;
    private boolean hasChanged = false;
    private int lastScreenMode = -1;
    private SundryImplement mNavSundryImplement;
    private LinearLayout mainLayout;
    private float mainOutputH;
    private FrameLayout.LayoutParams mainOutputLayoutParams;
    private TvSurfaceView mainOutputView;
    private float mainOutputW;
    private float mainOutputX;
    private float mainOutputY;
    private SurfaceHolder mainSurfaceHolder;
    private LinearLayout subLayout;
    private float subOutputH;
    private FrameLayout.LayoutParams subOutputLayoutParams;
    private TvSurfaceView subOutputView;
    private float subOutputW;
    private float subOutputX;
    private float subOutputY;
    private SurfaceHolder subSurfaceHolder;
    private int[] supportScreenModes;

    public PIPPOPSurfaceViewControl() {
        pipOutputPos = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_PIP_POSITION) / 2;
        pipOutputSize = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_PIP_SIZE);
        this.mNavSundryImplement = SundryImplement.getInstanceNavSundryImplement(TurnkeyUiMainActivity.getInstance());
    }

    public PIPPOPSurfaceViewControl(Context context) {
    }

    public static PIPPOPSurfaceViewControl getSurfaceViewControlInstance() {
        if (mpPippopSurfaceViewControl == null) {
            mpPippopSurfaceViewControl = new PIPPOPSurfaceViewControl();
        }
        return mpPippopSurfaceViewControl;
    }

    private void init() {
        this.mainOutputLayoutParams = (FrameLayout.LayoutParams) this.mainOutputView.getLayoutParams();
        this.subOutputLayoutParams = (FrameLayout.LayoutParams) this.subOutputView.getLayoutParams();
    }

    public void setSignalOutputView(TvSurfaceView mainOutput, TvSurfaceView subOutput, LinearLayout mainLY, LinearLayout subLY) {
        this.mainOutputView = mainOutput;
        this.subOutputView = subOutput;
        this.mainLayout = mainLY;
        this.subLayout = subLY;
        init();
    }

    public void changeOutputWithTVState(int state) {
        int index;
        setMainOutputPos(state);
        setSubOutputPos(state);
        updateMainAndSubOutputPostion();
        if (!MarketRegionInfo.isFunctionSupport(26)) {
            return;
        }
        if (1 == state) {
            if (6 == MtkTvConfig.getInstance().getConfigValue("g_video__screen_mode")) {
                this.lastScreenMode = 6;
            }
        } else if (2 == state) {
            if (6 == MtkTvConfig.getInstance().getConfigValue("g_video__screen_mode")) {
                this.supportScreenModes = this.mNavSundryImplement.getSupportScreenModes();
                if (this.supportScreenModes != null) {
                    int index2 = getCurrentValueIndex(this.supportScreenModes, this.mNavSundryImplement.getCurrentScreenMode());
                    if (index2 < this.supportScreenModes.length - 1) {
                        index = index2 + 1;
                    } else {
                        index = 0;
                    }
                    this.mNavSundryImplement.setCurrentScreenMode(this.supportScreenModes[index]);
                }
            }
        } else if (state == 0) {
            MtkLog.d("MultiViewControl", "hasChanged: " + this.hasChanged);
            if (this.hasChanged) {
                this.hasChanged = false;
                this.supportScreenModes = this.mNavSundryImplement.getSupportScreenModes();
                if (this.supportScreenModes != null) {
                    int index3 = getCurrentValueIndex(this.supportScreenModes, this.mNavSundryImplement.getCurrentScreenMode());
                    MtkLog.d("MultiViewControl", "index: " + index3);
                    if (index3 > this.supportScreenModes.length - 1) {
                        index3 = 0;
                    }
                    this.mNavSundryImplement.setCurrentScreenMode(this.supportScreenModes[index3]);
                }
                this.lastScreenMode = -1;
            } else if (6 == this.lastScreenMode) {
                MtkTvConfig.getInstance().setConfigValue("g_video__screen_mode", 6);
                this.lastScreenMode = -1;
            }
        }
    }

    public void setScreenModeChangedFlag(boolean flag) {
        MtkLog.d("MultiViewControl", "setScreenModeChangedFlag");
        this.hasChanged = flag;
    }

    public int getCurrentValueIndex(int[] currentArray, int value) {
        if (currentArray != null) {
            for (int i = 0; i < currentArray.length; i++) {
                if (value == currentArray[i]) {
                    return i;
                }
            }
        }
        return 0;
    }

    public void changeSubOutputPosition() {
        if (pipOutputPos < 4) {
            pipOutputPos++;
        } else {
            pipOutputPos = 0;
        }
        setSubOutputPos(1);
        changeSubOutputViewPosition();
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_PIP_POSITION, pipOutputPos * 2);
    }

    public void changeSubOutputSize() {
        if (pipOutputSize < 2) {
            pipOutputSize++;
        } else {
            pipOutputSize = 0;
        }
        setSubOutputPos(1);
        changeSubOutputViewSize();
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_PIP_SIZE, pipOutputSize);
    }

    public void setSubOutputPos(int TVState) {
        switch (TVState) {
            case 0:
                this.subOutputX = 0.0f;
                this.subOutputY = 0.0f;
                this.subOutputW = 0.0f;
                this.subOutputH = 0.0f;
                break;
            case 1:
                switch (pipOutputPos) {
                    case 0:
                        if (pipOutputSize != 0) {
                            if (pipOutputSize != 1) {
                                this.subOutputX = 0.5f;
                                this.subOutputY = 0.5f;
                                this.subOutputW = 0.9f;
                                this.subOutputH = 0.9f;
                                break;
                            } else {
                                this.subOutputX = 0.65f;
                                this.subOutputY = 0.65f;
                                this.subOutputW = 0.9f;
                                this.subOutputH = 0.9f;
                                break;
                            }
                        } else {
                            this.subOutputX = 0.7f;
                            this.subOutputY = 0.7f;
                            this.subOutputW = 0.9f;
                            this.subOutputH = 0.9f;
                            break;
                        }
                    case 1:
                        if (pipOutputSize != 0) {
                            if (pipOutputSize != 1) {
                                this.subOutputX = 0.5f;
                                this.subOutputY = 0.1f;
                                this.subOutputW = 0.9f;
                                this.subOutputH = 0.5f;
                                break;
                            } else {
                                this.subOutputX = 0.65f;
                                this.subOutputY = 0.1f;
                                this.subOutputW = 0.9f;
                                this.subOutputH = 0.35f;
                                break;
                            }
                        } else {
                            this.subOutputX = 0.7f;
                            this.subOutputY = 0.1f;
                            this.subOutputW = 0.9f;
                            this.subOutputH = 0.3f;
                            break;
                        }
                    case 2:
                        if (pipOutputSize != 0) {
                            if (pipOutputSize != 1) {
                                this.subOutputX = 0.3f;
                                this.subOutputY = 0.3f;
                                this.subOutputW = 0.7f;
                                this.subOutputH = 0.7f;
                                break;
                            } else {
                                this.subOutputX = 0.375f;
                                this.subOutputY = 0.375f;
                                this.subOutputW = 0.625f;
                                this.subOutputH = 0.625f;
                                break;
                            }
                        } else {
                            this.subOutputX = 0.4f;
                            this.subOutputY = 0.4f;
                            this.subOutputW = 0.6f;
                            this.subOutputH = 0.6f;
                            break;
                        }
                    case 3:
                        if (pipOutputSize != 0) {
                            if (pipOutputSize != 1) {
                                this.subOutputX = 0.1f;
                                this.subOutputY = 0.1f;
                                this.subOutputW = 0.5f;
                                this.subOutputH = 0.5f;
                                break;
                            } else {
                                this.subOutputX = 0.1f;
                                this.subOutputY = 0.1f;
                                this.subOutputW = 0.35f;
                                this.subOutputH = 0.35f;
                                break;
                            }
                        } else {
                            this.subOutputX = 0.1f;
                            this.subOutputY = 0.1f;
                            this.subOutputW = 0.3f;
                            this.subOutputH = 0.3f;
                            break;
                        }
                    case 4:
                        if (pipOutputSize != 0) {
                            if (pipOutputSize != 1) {
                                this.subOutputX = 0.1f;
                                this.subOutputY = 0.5f;
                                this.subOutputW = 0.5f;
                                this.subOutputH = 0.9f;
                                break;
                            } else {
                                this.subOutputX = 0.1f;
                                this.subOutputY = 0.65f;
                                this.subOutputW = 0.35f;
                                this.subOutputH = 0.9f;
                                break;
                            }
                        } else {
                            this.subOutputX = 0.1f;
                            this.subOutputY = 0.7f;
                            this.subOutputW = 0.3f;
                            this.subOutputH = 0.9f;
                            break;
                        }
                }
            case 2:
                this.subOutputX = 0.5f;
                this.subOutputY = 0.25f;
                this.subOutputW = 1.0f;
                this.subOutputH = 0.75f;
                break;
        }
        MtkLog.i("SCAL sub", "~~~~~~~(x,y,w,h)(" + this.subOutputX + "," + this.subOutputY + "," + this.subOutputW + "," + this.subOutputH + ")");
    }

    private void setMainOutputPos(int TVState) {
        switch (TVState) {
            case 0:
            case 1:
                this.mainOutputX = 0.0f;
                this.mainOutputY = 0.0f;
                this.mainOutputW = 1.0f;
                this.mainOutputH = 1.0f;
                break;
            case 2:
                this.mainOutputX = 0.0f;
                this.mainOutputY = 0.25f;
                this.mainOutputW = 0.5f;
                this.mainOutputH = 0.75f;
                break;
        }
        MtkLog.i("SCAL mian", "~~~~~~~(x,y,w,h)(" + this.mainOutputX + "," + this.mainOutputY + "," + this.mainOutputW + "," + this.mainOutputH + ")");
    }

    private void updateMainAndSubOutputPostion() {
        this.mainOutputLayoutParams.gravity = 51;
        this.mainOutputLayoutParams.leftMargin = (int) (((float) ScreenConstant.SCREEN_WIDTH) * this.mainOutputX);
        this.mainOutputLayoutParams.topMargin = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * this.mainOutputY);
        this.mainOutputLayoutParams.width = (int) (((float) ScreenConstant.SCREEN_WIDTH) * (this.mainOutputW - this.mainOutputX));
        this.mainOutputLayoutParams.height = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * (this.mainOutputH - this.mainOutputY));
        MtkLog.d("SCAL", "mainOutputLayoutParams.width = " + this.mainOutputLayoutParams.width + ", mainOutputLayoutParams.height = " + this.mainOutputLayoutParams.height);
        if (this.mainOutputLayoutParams.width == ScreenConstant.SCREEN_WIDTH) {
            this.mainOutputLayoutParams.leftMargin = 0;
            this.mainOutputLayoutParams.rightMargin = 0;
            this.mainOutputLayoutParams.topMargin = 0;
            this.mainOutputLayoutParams.bottomMargin = 0;
            this.mainOutputLayoutParams.width = -1;
            this.mainOutputLayoutParams.height = -1;
        }
        this.mainOutputView.setLayoutParams(this.mainOutputLayoutParams);
        this.mainOutputView.invalidate();
        this.subOutputLayoutParams.gravity = 51;
        this.subOutputLayoutParams.leftMargin = (int) (((float) ScreenConstant.SCREEN_WIDTH) * this.subOutputX);
        this.subOutputLayoutParams.topMargin = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * this.subOutputY);
        this.subOutputLayoutParams.width = (int) (((float) ScreenConstant.SCREEN_WIDTH) * (this.subOutputW - this.subOutputX));
        this.subOutputLayoutParams.height = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * (this.subOutputH - this.subOutputY));
        MtkLog.d("SCAL", "subOutputLayoutParams.width = " + this.subOutputLayoutParams.width + ", subOutputLayoutParams.height = " + this.subOutputLayoutParams.height);
        this.subOutputView.setLayoutParams(this.subOutputLayoutParams);
        this.subOutputView.invalidate();
    }

    private void changeSubOutputViewPosition() {
        this.subOutputLayoutParams.leftMargin = (int) (((float) ScreenConstant.SCREEN_WIDTH) * this.subOutputX);
        this.subOutputLayoutParams.topMargin = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * this.subOutputY);
        this.subOutputView.setLayoutParams(this.subOutputLayoutParams);
        this.subOutputView.invalidate();
    }

    private void changeSubOutputViewSize() {
        this.subOutputLayoutParams.leftMargin = (int) (((float) ScreenConstant.SCREEN_WIDTH) * this.subOutputX);
        this.subOutputLayoutParams.topMargin = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * this.subOutputY);
        this.subOutputLayoutParams.width = (int) (((float) ScreenConstant.SCREEN_WIDTH) * (this.subOutputW - this.subOutputX));
        this.subOutputLayoutParams.height = (int) (((float) ScreenConstant.SCREEN_HEIGHT) * (this.subOutputH - this.subOutputY));
        this.subOutputView.setLayoutParams(this.subOutputLayoutParams);
        this.subOutputView.invalidate();
    }

    public float[] getSubPosition() {
        return new float[]{(this.subOutputX + this.subOutputW) / 2.0f, this.subOutputY};
    }

    public float[] getMainPosition() {
        return new float[]{(this.mainOutputX + this.mainOutputW) / 2.0f, this.mainOutputY};
    }
}
