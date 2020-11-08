package android.support.v17.leanback.app;

import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PlaybackSeekUi;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;

public class PlaybackSupportFragmentGlueHost extends PlaybackGlueHost implements PlaybackSeekUi {
    final PlaybackSupportFragment mFragment;
    final PlaybackGlueHost.PlayerCallback mPlayerCallback = new PlaybackGlueHost.PlayerCallback() {
        public void onBufferingStateChanged(boolean start) {
            PlaybackSupportFragmentGlueHost.this.mFragment.onBufferingStateChanged(start);
        }

        public void onError(int errorCode, CharSequence errorMessage) {
            PlaybackSupportFragmentGlueHost.this.mFragment.onError(errorCode, errorMessage);
        }

        public void onVideoSizeChanged(int videoWidth, int videoHeight) {
            PlaybackSupportFragmentGlueHost.this.mFragment.onVideoSizeChanged(videoWidth, videoHeight);
        }
    };

    public PlaybackSupportFragmentGlueHost(PlaybackSupportFragment fragment) {
        this.mFragment = fragment;
    }

    public void setControlsOverlayAutoHideEnabled(boolean enabled) {
        this.mFragment.setControlsOverlayAutoHideEnabled(enabled);
    }

    public boolean isControlsOverlayAutoHideEnabled() {
        return this.mFragment.isControlsOverlayAutoHideEnabled();
    }

    public void setOnKeyInterceptListener(View.OnKeyListener onKeyListener) {
        this.mFragment.setOnKeyInterceptListener(onKeyListener);
    }

    public void setOnActionClickedListener(final OnActionClickedListener listener) {
        if (listener == null) {
            this.mFragment.setOnPlaybackItemViewClickedListener((BaseOnItemViewClickedListener) null);
        } else {
            this.mFragment.setOnPlaybackItemViewClickedListener(new OnItemViewClickedListener() {
                public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                    if (item instanceof Action) {
                        listener.onActionClicked((Action) item);
                    }
                }
            });
        }
    }

    public void setHostCallback(PlaybackGlueHost.HostCallback callback) {
        this.mFragment.setHostCallback(callback);
    }

    public void notifyPlaybackRowChanged() {
        this.mFragment.notifyPlaybackRowChanged();
    }

    public void setPlaybackRowPresenter(PlaybackRowPresenter presenter) {
        this.mFragment.setPlaybackRowPresenter(presenter);
    }

    public void setPlaybackRow(Row row) {
        this.mFragment.setPlaybackRow(row);
    }

    public void fadeOut() {
        this.mFragment.fadeOut();
    }

    public boolean isControlsOverlayVisible() {
        return this.mFragment.isControlsOverlayVisible();
    }

    public void hideControlsOverlay(boolean runAnimation) {
        this.mFragment.hideControlsOverlay(runAnimation);
    }

    public void showControlsOverlay(boolean runAnimation) {
        this.mFragment.showControlsOverlay(runAnimation);
    }

    public void setPlaybackSeekUiClient(PlaybackSeekUi.Client client) {
        this.mFragment.setPlaybackSeekUiClient(client);
    }

    public PlaybackGlueHost.PlayerCallback getPlayerCallback() {
        return this.mPlayerCallback;
    }
}
