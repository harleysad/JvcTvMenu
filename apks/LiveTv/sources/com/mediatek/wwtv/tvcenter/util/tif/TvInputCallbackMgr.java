package com.mediatek.wwtv.tvcenter.util.tif;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.OnLoadingListener;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.ArrayList;
import java.util.List;

public class TvInputCallbackMgr {
    private static final String TAG = "TvInputCallbackMgr";
    private static TvInputCallbackMgr mInstance;
    /* access modifiers changed from: private */
    public DvrCallback dvrCallback;
    /* access modifiers changed from: private */
    public final Context mContext;
    private boolean mIsLayoutChanged = true;
    /* access modifiers changed from: private */
    public boolean mIsViewSizeChanged = false;
    /* access modifiers changed from: private */
    public OnLoadingListener mOnLoadingListener;
    /* access modifiers changed from: private */
    public final SundryImplement mSundryImplement;
    private final TvView.TvInputCallback mTvInputCallback = new TvView.TvInputCallback() {
        public void onConnectionFailed(String inputId) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onConnectionFailed inputId>>>" + inputId);
            TvSingletons.getSingletons().getInputSourceManager().retryLoadSourceListAfterStartSession();
        }

        public void onDisconnected(String inputId) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onDisconnected inputId>>>" + inputId);
        }

        public void onVideoSizeChanged(String inputId, int width, int height) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onVideoSizeChanged inputId>>>" + inputId + ">>" + width + ">>" + height);
        }

        public void onChannelRetuned(String inputId, Uri channelUri) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onChannelRetuned inputId>>>" + inputId + ">>" + channelUri.toString());
            if (TvInputCallbackMgr.this.timeshiftCallback != null) {
                TvInputCallbackMgr.this.timeshiftCallback.onChannelChanged(inputId, channelUri);
            }
        }

        public void onTracksChanged(String inputId, List<TvTrackInfo> tracks) {
            new ArrayList();
            List<TvTrackInfo> tvTrackInfos = tracks;
            MtkLog.d(TvInputCallbackMgr.TAG, "onTracksChanged inputId>>>" + inputId + ">>" + tvTrackInfos.size());
            TvInputCallbackMgr.this.dvrCallback.onTracksChanged(inputId, tvTrackInfos);
            if (!tvTrackInfos.isEmpty() && tvTrackInfos.get(0).getType() == 0) {
                TvInputCallbackMgr.this.mSundryImplement.setmMtsAudioTracks(tvTrackInfos);
            }
        }

        public void onTrackSelected(String inputId, int type, String trackId) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onTrackSelected inputId>>>" + inputId + ">>" + type + ">>" + trackId);
            if (!MarketRegionInfo.isFunctionSupport(20)) {
                return;
            }
            if (type == 0) {
                if (ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY) != null) {
                    ((SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY)).updateTrackChanged(type, trackId);
                }
            } else if (1 == type) {
                TvInputCallbackMgr.this.handleVideoSizeChanged(inputId, trackId);
            }
        }

        public void onVideoAvailable(String inputId) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onVideoAvailable inputId>>>" + inputId);
            int current3rdMId = SaveValue.getInstance(TvInputCallbackMgr.this.mContext).readValue(TIFFunctionUtil.current3rdMId, -1);
            MtkLog.d(TvInputCallbackMgr.TAG, "current3rdId:" + current3rdMId);
            if (TvInputCallbackMgr.this.mOnLoadingListener != null) {
                TvInputCallbackMgr.this.mOnLoadingListener.onHideLoading();
            }
            if (TvInputCallbackMgr.this.mIsViewSizeChanged) {
                TvInputCallbackMgr.this.resetViewSize();
            }
        }

        public void onVideoUnavailable(String inputId, int reason) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onVideoUnavailable inputId>>>" + inputId + ">>" + reason);
            int current3rdMId = SaveValue.getInstance(TvInputCallbackMgr.this.mContext).readValue(TIFFunctionUtil.current3rdMId, -1);
            StringBuilder sb = new StringBuilder();
            sb.append("current3rdId:");
            sb.append(current3rdMId);
            MtkLog.d(TvInputCallbackMgr.TAG, sb.toString());
            if (TvInputCallbackMgr.this.mOnLoadingListener != null) {
                if (!InputUtil.isTunerTypeByInputId(inputId) || current3rdMId == -1) {
                    TvInputCallbackMgr.this.mOnLoadingListener.onHideLoading();
                } else {
                    TvInputCallbackMgr.this.mOnLoadingListener.onShowLoading();
                }
            }
            if (reason == 1) {
                ComponentStatusListener.getInstance().updateStatus(10, 0);
            }
        }

        public void onContentAllowed(String inputId) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onContentAllowed inputId>>>" + inputId);
            PwdDialog.setContentBlockRating((TvContentRating) null);
            if (MarketRegionInfo.isFunctionSupport(29)) {
                ComponentStatusListener.getInstance().updateStatus(12, 0);
            }
        }

        public void onContentBlocked(String inputId, TvContentRating rating) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onContentBlocked inputId>>>" + inputId + ">>" + rating.getMainRating());
            PwdDialog.setContentBlockRating(rating);
            if (MarketRegionInfo.isFunctionSupport(29)) {
                ComponentStatusListener.getInstance().updateStatus(13, 0);
            }
        }

        public void onEvent(String inputId, String eventType, Bundle eventArgs) {
            MtkLog.d(TvInputCallbackMgr.TAG, "onEvent inputId>>>" + inputId + ">>" + eventType + ">>" + eventArgs.toString());
            if (TvInputCallbackMgr.this.timeshiftCallback != null) {
                TvInputCallbackMgr.this.timeshiftCallback.onEvent(inputId, eventType, eventArgs);
            }
            if (TvInputCallbackMgr.this.dvrCallback != null) {
                TvInputCallbackMgr.this.dvrCallback.onEvent(inputId, eventType, eventArgs);
            }
        }

        public void onTimeShiftStatusChanged(String inputId, int status) {
            if (TvInputCallbackMgr.this.timeshiftCallback != null) {
                TvInputCallbackMgr.this.timeshiftCallback.onTimeShiftStatusChanged(inputId, status);
            }
            if (TvInputCallbackMgr.this.dvrCallback != null) {
                TvInputCallbackMgr.this.dvrCallback.onTimeShiftStatusChanged(inputId, status);
            }
        }
    };
    /* access modifiers changed from: private */
    public TimeshiftCallback timeshiftCallback;

    public interface DvrCallback {
        void onEvent(String str, String str2, Bundle bundle);

        void onTimeShiftStatusChanged(String str, int i);

        void onTrackSelected(String str, int i, String str2);

        void onTracksChanged(String str, List<TvTrackInfo> list);
    }

    public interface TimeshiftCallback {
        void onChannelChanged(String str, Uri uri);

        void onEvent(String str, String str2, Bundle bundle);

        void onTimeShiftStatusChanged(String str, int i);
    }

    public static TvInputCallbackMgr getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TvInputCallbackMgr(context);
        }
        return mInstance;
    }

    private TvInputCallbackMgr(Context context) {
        this.mContext = context;
        this.mSundryImplement = SundryImplement.getInstanceNavSundryImplement(this.mContext);
        if (context instanceof OnLoadingListener) {
            this.mOnLoadingListener = (OnLoadingListener) context;
        }
    }

    public TvView.TvInputCallback getTvInputCallback() {
        return this.mTvInputCallback;
    }

    public TimeshiftCallback getTimeshiftCallback() {
        return this.timeshiftCallback;
    }

    public void setTimeshiftCallback(TimeshiftCallback timeshiftCallback2) {
        this.timeshiftCallback = timeshiftCallback2;
    }

    public DvrCallback getDvrCallback() {
        return this.dvrCallback;
    }

    public void setDvrCallback(DvrCallback dvrCallback2) {
        this.dvrCallback = dvrCallback2;
    }

    public void handleVideoSizeChanged(String inputId, String trackId) {
        String str = trackId;
        TvSurfaceView view = TurnkeyUiMainActivity.getInstance().getTvView();
        List<TvTrackInfo> list = view.getTracks(1);
        if (list == null || list.size() == 0 || str == null || trackId.length() == 0 || !CommonIntegration.getInstance().is3rdTVSource()) {
            MtkLog.d(TAG, "handleVideoSizeChanged, " + str);
            return;
        }
        MtkLog.d(TAG, "handleVideoSizeChanged, " + list);
        for (int i = 0; i < list.size(); i++) {
            TvTrackInfo info = list.get(i);
            if (str.equals(info.getId())) {
                float ratio = info.getVideoPixelAspectRatio();
                int width = info.getVideoWidth();
                int height = info.getVideoHeight();
                int outputWidth = width;
                int outputHeight = height;
                MtkLog.d(TAG, "handleVideoSizeChanged, " + ratio + "," + width + "," + height);
                if (Math.abs(((double) ratio) - 1.0d) >= 1.0E-5d) {
                    if (!(height == 480 || height == 576 || height == 720 || height == 1080 || height == 2160)) {
                        outputHeight = (int) (((float) height) * ratio);
                    }
                    if (!(width == 640 || width == 768 || width == 960 || width == 1280 || width == 1920 || width == 3840)) {
                        outputWidth = (int) (((float) width) * ratio);
                    }
                    FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    mLayoutParams.gravity = 51;
                    mLayoutParams.leftMargin = (ScreenConstant.SCREEN_WIDTH - outputWidth) / 2;
                    mLayoutParams.topMargin = (ScreenConstant.SCREEN_HEIGHT - outputHeight) / 2;
                    mLayoutParams.width = outputWidth;
                    mLayoutParams.height = outputHeight;
                    view.setLayoutParams(mLayoutParams);
                    view.invalidate();
                    MtkLog.d(TAG, "handleVideoSizeChanged, " + outputWidth + outputHeight);
                    this.mIsViewSizeChanged = true;
                }
            }
        }
    }

    public void resetViewSize() {
        TvSurfaceView view = TurnkeyUiMainActivity.getInstance().getTvView();
        FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        this.mIsViewSizeChanged = false;
        mLayoutParams.gravity = 51;
        mLayoutParams.leftMargin = 0;
        mLayoutParams.topMargin = 0;
        mLayoutParams.width = ScreenConstant.SCREEN_WIDTH;
        mLayoutParams.height = ScreenConstant.SCREEN_HEIGHT;
        view.setLayoutParams(mLayoutParams);
        view.invalidate();
        MtkLog.d(TAG, "resetViewSize, ");
        this.mIsViewSizeChanged = false;
    }

    public void handleLayoutChanged(int width, int height) {
        if (width == ScreenConstant.SCREEN_WIDTH && height == ScreenConstant.SCREEN_HEIGHT) {
            if (!this.mIsLayoutChanged) {
                ComponentStatusListener.getInstance().updateStatus(15, 0);
                this.mIsLayoutChanged = true;
            }
        } else if (width == 480 && height == 270) {
            MtkLog.d(TAG, "mIsLayoutChanged, false");
            this.mIsLayoutChanged = false;
        }
    }
}
