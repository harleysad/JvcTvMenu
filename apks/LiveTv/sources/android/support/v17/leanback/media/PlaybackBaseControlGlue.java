package android.support.v17.leanback.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import java.util.List;

public abstract class PlaybackBaseControlGlue<T extends PlayerAdapter> extends PlaybackGlue implements OnActionClickedListener, View.OnKeyListener {
    public static final int ACTION_CUSTOM_LEFT_FIRST = 1;
    public static final int ACTION_CUSTOM_RIGHT_FIRST = 4096;
    public static final int ACTION_FAST_FORWARD = 128;
    public static final int ACTION_PLAY_PAUSE = 64;
    public static final int ACTION_REPEAT = 512;
    public static final int ACTION_REWIND = 32;
    public static final int ACTION_SHUFFLE = 1024;
    public static final int ACTION_SKIP_TO_NEXT = 256;
    public static final int ACTION_SKIP_TO_PREVIOUS = 16;
    static final boolean DEBUG = false;
    static final String TAG = "PlaybackTransportGlue";
    final PlayerAdapter.Callback mAdapterCallback = new PlayerAdapter.Callback() {
        public void onPlayStateChanged(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onPlayStateChanged();
        }

        public void onCurrentPositionChanged(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onUpdateProgress();
        }

        public void onBufferedPositionChanged(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onUpdateBufferedProgress();
        }

        public void onDurationChanged(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onUpdateDuration();
        }

        public void onPlayCompleted(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onPlayCompleted();
        }

        public void onPreparedStateChanged(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onPreparedStateChanged();
        }

        public void onVideoSizeChanged(PlayerAdapter wrapper, int width, int height) {
            PlaybackBaseControlGlue.this.mVideoWidth = width;
            PlaybackBaseControlGlue.this.mVideoHeight = height;
            if (PlaybackBaseControlGlue.this.mPlayerCallback != null) {
                PlaybackBaseControlGlue.this.mPlayerCallback.onVideoSizeChanged(width, height);
            }
        }

        public void onError(PlayerAdapter wrapper, int errorCode, String errorMessage) {
            PlaybackBaseControlGlue.this.mErrorSet = true;
            PlaybackBaseControlGlue.this.mErrorCode = errorCode;
            PlaybackBaseControlGlue.this.mErrorMessage = errorMessage;
            if (PlaybackBaseControlGlue.this.mPlayerCallback != null) {
                PlaybackBaseControlGlue.this.mPlayerCallback.onError(errorCode, errorMessage);
            }
        }

        public void onBufferingStateChanged(PlayerAdapter wrapper, boolean start) {
            PlaybackBaseControlGlue.this.mBuffering = start;
            if (PlaybackBaseControlGlue.this.mPlayerCallback != null) {
                PlaybackBaseControlGlue.this.mPlayerCallback.onBufferingStateChanged(start);
            }
        }

        public void onMetadataChanged(PlayerAdapter wrapper) {
            PlaybackBaseControlGlue.this.onMetadataChanged();
        }
    };
    boolean mBuffering = false;
    PlaybackControlsRow mControlsRow;
    PlaybackRowPresenter mControlsRowPresenter;
    Drawable mCover;
    int mErrorCode;
    String mErrorMessage;
    boolean mErrorSet = false;
    boolean mFadeWhenPlaying = true;
    boolean mIsPlaying = false;
    PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    final T mPlayerAdapter;
    PlaybackGlueHost.PlayerCallback mPlayerCallback;
    CharSequence mSubtitle;
    CharSequence mTitle;
    int mVideoHeight = 0;
    int mVideoWidth = 0;

    public abstract void onActionClicked(Action action);

    /* access modifiers changed from: protected */
    public abstract PlaybackRowPresenter onCreateRowPresenter();

    public abstract boolean onKey(View view, int i, KeyEvent keyEvent);

    public PlaybackBaseControlGlue(Context context, T impl) {
        super(context);
        this.mPlayerAdapter = impl;
        this.mPlayerAdapter.setCallback(this.mAdapterCallback);
    }

    public final T getPlayerAdapter() {
        return this.mPlayerAdapter;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHost(PlaybackGlueHost host) {
        super.onAttachedToHost(host);
        host.setOnKeyInterceptListener(this);
        host.setOnActionClickedListener(this);
        onCreateDefaultControlsRow();
        onCreateDefaultRowPresenter();
        host.setPlaybackRowPresenter(getPlaybackRowPresenter());
        host.setPlaybackRow(getControlsRow());
        this.mPlayerCallback = host.getPlayerCallback();
        onAttachHostCallback();
        this.mPlayerAdapter.onAttachedToHost(host);
    }

    /* access modifiers changed from: package-private */
    public void onAttachHostCallback() {
        if (this.mPlayerCallback != null) {
            if (!(this.mVideoWidth == 0 || this.mVideoHeight == 0)) {
                this.mPlayerCallback.onVideoSizeChanged(this.mVideoWidth, this.mVideoHeight);
            }
            if (this.mErrorSet) {
                this.mPlayerCallback.onError(this.mErrorCode, this.mErrorMessage);
            }
            this.mPlayerCallback.onBufferingStateChanged(this.mBuffering);
        }
    }

    /* access modifiers changed from: package-private */
    public void onDetachHostCallback() {
        this.mErrorSet = false;
        this.mErrorCode = 0;
        this.mErrorMessage = null;
        if (this.mPlayerCallback != null) {
            this.mPlayerCallback.onBufferingStateChanged(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onHostStart() {
        this.mPlayerAdapter.setProgressUpdatingEnabled(true);
    }

    /* access modifiers changed from: protected */
    public void onHostStop() {
        this.mPlayerAdapter.setProgressUpdatingEnabled(false);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromHost() {
        onDetachHostCallback();
        this.mPlayerCallback = null;
        this.mPlayerAdapter.onDetachedFromHost();
        this.mPlayerAdapter.setProgressUpdatingEnabled(false);
        super.onDetachedFromHost();
    }

    /* access modifiers changed from: package-private */
    public void onCreateDefaultControlsRow() {
        if (this.mControlsRow == null) {
            setControlsRow(new PlaybackControlsRow(this));
        }
    }

    /* access modifiers changed from: package-private */
    public void onCreateDefaultRowPresenter() {
        if (this.mControlsRowPresenter == null) {
            setPlaybackRowPresenter(onCreateRowPresenter());
        }
    }

    public void setControlsOverlayAutoHideEnabled(boolean enable) {
        this.mFadeWhenPlaying = enable;
        if (!this.mFadeWhenPlaying && getHost() != null) {
            getHost().setControlsOverlayAutoHideEnabled(false);
        }
    }

    public boolean isControlsOverlayAutoHideEnabled() {
        return this.mFadeWhenPlaying;
    }

    public void setControlsRow(PlaybackControlsRow controlsRow) {
        this.mControlsRow = controlsRow;
        this.mControlsRow.setCurrentPosition(-1);
        this.mControlsRow.setDuration(-1);
        this.mControlsRow.setBufferedPosition(-1);
        if (this.mControlsRow.getPrimaryActionsAdapter() == null) {
            ArrayObjectAdapter adapter = new ArrayObjectAdapter((PresenterSelector) new ControlButtonPresenterSelector());
            onCreatePrimaryActions(adapter);
            this.mControlsRow.setPrimaryActionsAdapter(adapter);
        }
        if (this.mControlsRow.getSecondaryActionsAdapter() == null) {
            ArrayObjectAdapter secondaryActions = new ArrayObjectAdapter((PresenterSelector) new ControlButtonPresenterSelector());
            onCreateSecondaryActions(secondaryActions);
            getControlsRow().setSecondaryActionsAdapter(secondaryActions);
        }
        updateControlsRow();
    }

    public void setPlaybackRowPresenter(PlaybackRowPresenter presenter) {
        this.mControlsRowPresenter = presenter;
    }

    public PlaybackControlsRow getControlsRow() {
        return this.mControlsRow;
    }

    public PlaybackRowPresenter getPlaybackRowPresenter() {
        return this.mControlsRowPresenter;
    }

    private void updateControlsRow() {
        onMetadataChanged();
    }

    public final boolean isPlaying() {
        return this.mPlayerAdapter.isPlaying();
    }

    public void play() {
        this.mPlayerAdapter.play();
    }

    public void pause() {
        this.mPlayerAdapter.pause();
    }

    public void next() {
        this.mPlayerAdapter.next();
    }

    public void previous() {
        this.mPlayerAdapter.previous();
    }

    protected static void notifyItemChanged(ArrayObjectAdapter adapter, Object object) {
        int index = adapter.indexOf(object);
        if (index >= 0) {
            adapter.notifyArrayItemRangeChanged(index, 1);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreatePrimaryActions(ArrayObjectAdapter primaryActionsAdapter) {
    }

    /* access modifiers changed from: protected */
    public void onCreateSecondaryActions(ArrayObjectAdapter secondaryActionsAdapter) {
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onUpdateProgress() {
        if (this.mControlsRow != null) {
            this.mControlsRow.setCurrentPosition(this.mPlayerAdapter.isPrepared() ? getCurrentPosition() : -1);
        }
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onUpdateBufferedProgress() {
        if (this.mControlsRow != null) {
            this.mControlsRow.setBufferedPosition(this.mPlayerAdapter.getBufferedPosition());
        }
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onUpdateDuration() {
        if (this.mControlsRow != null) {
            this.mControlsRow.setDuration(this.mPlayerAdapter.isPrepared() ? this.mPlayerAdapter.getDuration() : -1);
        }
    }

    public final long getDuration() {
        return this.mPlayerAdapter.getDuration();
    }

    public long getCurrentPosition() {
        return this.mPlayerAdapter.getCurrentPosition();
    }

    public final long getBufferedPosition() {
        return this.mPlayerAdapter.getBufferedPosition();
    }

    public final boolean isPrepared() {
        return this.mPlayerAdapter.isPrepared();
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onPreparedStateChanged() {
        onUpdateDuration();
        List<PlaybackGlue.PlayerCallback> callbacks = getPlayerCallbacks();
        if (callbacks != null) {
            int size = callbacks.size();
            for (int i = 0; i < size; i++) {
                callbacks.get(i).onPreparedStateChanged(this);
            }
        }
    }

    public void setArt(Drawable cover) {
        if (this.mCover != cover) {
            this.mCover = cover;
            this.mControlsRow.setImageDrawable(this.mCover);
            if (getHost() != null) {
                getHost().notifyPlaybackRowChanged();
            }
        }
    }

    public Drawable getArt() {
        return this.mCover;
    }

    public void setSubtitle(CharSequence subtitle) {
        if (!TextUtils.equals(subtitle, this.mSubtitle)) {
            this.mSubtitle = subtitle;
            if (getHost() != null) {
                getHost().notifyPlaybackRowChanged();
            }
        }
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.equals(title, this.mTitle)) {
            this.mTitle = title;
            if (getHost() != null) {
                getHost().notifyPlaybackRowChanged();
            }
        }
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    /* access modifiers changed from: protected */
    public void onMetadataChanged() {
        if (this.mControlsRow != null) {
            this.mControlsRow.setImageDrawable(getArt());
            this.mControlsRow.setDuration(getDuration());
            this.mControlsRow.setCurrentPosition(getCurrentPosition());
            if (getHost() != null) {
                getHost().notifyPlaybackRowChanged();
            }
        }
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onPlayStateChanged() {
        List<PlaybackGlue.PlayerCallback> callbacks = getPlayerCallbacks();
        if (callbacks != null) {
            int size = callbacks.size();
            for (int i = 0; i < size; i++) {
                callbacks.get(i).onPlayStateChanged(this);
            }
        }
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onPlayCompleted() {
        List<PlaybackGlue.PlayerCallback> callbacks = getPlayerCallbacks();
        if (callbacks != null) {
            int size = callbacks.size();
            for (int i = 0; i < size; i++) {
                callbacks.get(i).onPlayCompleted(this);
            }
        }
    }

    public final void seekTo(long position) {
        this.mPlayerAdapter.seekTo(position);
    }

    public long getSupportedActions() {
        return this.mPlayerAdapter.getSupportedActions();
    }
}
