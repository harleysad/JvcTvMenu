package android.support.v17.leanback.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.dm.DMNativeDaemonConnector;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import java.lang.ref.WeakReference;
import java.util.List;

public abstract class PlaybackControlGlue extends PlaybackGlue implements OnActionClickedListener, View.OnKeyListener {
    public static final int ACTION_CUSTOM_LEFT_FIRST = 1;
    public static final int ACTION_CUSTOM_RIGHT_FIRST = 4096;
    public static final int ACTION_FAST_FORWARD = 128;
    public static final int ACTION_PLAY_PAUSE = 64;
    public static final int ACTION_REWIND = 32;
    public static final int ACTION_SKIP_TO_NEXT = 256;
    public static final int ACTION_SKIP_TO_PREVIOUS = 16;
    static final boolean DEBUG = false;
    static final int MSG_UPDATE_PLAYBACK_STATE = 100;
    private static final int NUMBER_OF_SEEK_SPEEDS = 5;
    public static final int PLAYBACK_SPEED_FAST_L0 = 10;
    public static final int PLAYBACK_SPEED_FAST_L1 = 11;
    public static final int PLAYBACK_SPEED_FAST_L2 = 12;
    public static final int PLAYBACK_SPEED_FAST_L3 = 13;
    public static final int PLAYBACK_SPEED_FAST_L4 = 14;
    public static final int PLAYBACK_SPEED_INVALID = -1;
    public static final int PLAYBACK_SPEED_NORMAL = 1;
    public static final int PLAYBACK_SPEED_PAUSED = 0;
    static final String TAG = "PlaybackControlGlue";
    private static final int UPDATE_PLAYBACK_STATE_DELAY_MS = 2000;
    static final Handler sHandler = new UpdatePlaybackStateHandler();
    private PlaybackControlsRow mControlsRow;
    private PlaybackRowPresenter mControlsRowPresenter;
    private boolean mFadeWhenPlaying;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private final int[] mFastForwardSpeeds;
    final WeakReference<PlaybackControlGlue> mGlueWeakReference;
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private int mPlaybackSpeed;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private final int[] mRewindSpeeds;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;

    public abstract int getCurrentPosition();

    public abstract int getCurrentSpeedId();

    public abstract Drawable getMediaArt();

    public abstract int getMediaDuration();

    public abstract CharSequence getMediaSubtitle();

    public abstract CharSequence getMediaTitle();

    public abstract long getSupportedActions();

    public abstract boolean hasValidMedia();

    public abstract boolean isMediaPlaying();

    static class UpdatePlaybackStateHandler extends Handler {
        UpdatePlaybackStateHandler() {
        }

        public void handleMessage(Message msg) {
            PlaybackControlGlue glue;
            if (msg.what == 100 && (glue = (PlaybackControlGlue) ((WeakReference) msg.obj).get()) != null) {
                glue.updatePlaybackState();
            }
        }
    }

    public PlaybackControlGlue(Context context, int[] seekSpeeds) {
        this(context, seekSpeeds, seekSpeeds);
    }

    public PlaybackControlGlue(Context context, int[] fastForwardSpeeds, int[] rewindSpeeds) {
        super(context);
        this.mPlaybackSpeed = 1;
        this.mFadeWhenPlaying = true;
        this.mGlueWeakReference = new WeakReference<>(this);
        if (fastForwardSpeeds.length == 0 || fastForwardSpeeds.length > 5) {
            throw new IllegalStateException("invalid fastForwardSpeeds array size");
        }
        this.mFastForwardSpeeds = fastForwardSpeeds;
        if (rewindSpeeds.length == 0 || rewindSpeeds.length > 5) {
            throw new IllegalStateException("invalid rewindSpeeds array size");
        }
        this.mRewindSpeeds = rewindSpeeds;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHost(PlaybackGlueHost host) {
        super.onAttachedToHost(host);
        host.setOnKeyInterceptListener(this);
        host.setOnActionClickedListener(this);
        if (getControlsRow() == null || getPlaybackRowPresenter() == null) {
            onCreateControlsRowAndPresenter();
        }
        host.setPlaybackRowPresenter(getPlaybackRowPresenter());
        host.setPlaybackRow(getControlsRow());
    }

    /* access modifiers changed from: protected */
    public void onHostStart() {
        enableProgressUpdating(true);
    }

    /* access modifiers changed from: protected */
    public void onHostStop() {
        enableProgressUpdating(false);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromHost() {
        enableProgressUpdating(false);
        super.onDetachedFromHost();
    }

    /* access modifiers changed from: protected */
    public void onCreateControlsRowAndPresenter() {
        if (getControlsRow() == null) {
            setControlsRow(new PlaybackControlsRow(this));
        }
        if (getPlaybackRowPresenter() == null) {
            setPlaybackRowPresenter(new PlaybackControlsRowPresenter(new AbstractDetailsDescriptionPresenter() {
                /* access modifiers changed from: protected */
                public void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder viewHolder, Object object) {
                    PlaybackControlGlue glue = (PlaybackControlGlue) object;
                    if (glue.hasValidMedia()) {
                        viewHolder.getTitle().setText(glue.getMediaTitle());
                        viewHolder.getSubtitle().setText(glue.getMediaSubtitle());
                        return;
                    }
                    viewHolder.getTitle().setText("");
                    viewHolder.getSubtitle().setText("");
                }
            }) {
                /* access modifiers changed from: protected */
                public void onBindRowViewHolder(RowPresenter.ViewHolder vh, Object item) {
                    super.onBindRowViewHolder(vh, item);
                    vh.setOnKeyListener(PlaybackControlGlue.this);
                }

                /* access modifiers changed from: protected */
                public void onUnbindRowViewHolder(RowPresenter.ViewHolder vh) {
                    super.onUnbindRowViewHolder(vh);
                    vh.setOnKeyListener((View.OnKeyListener) null);
                }
            });
        }
    }

    public int[] getFastForwardSpeeds() {
        return this.mFastForwardSpeeds;
    }

    public int[] getRewindSpeeds() {
        return this.mRewindSpeeds;
    }

    public void setFadingEnabled(boolean enable) {
        this.mFadeWhenPlaying = enable;
        if (!this.mFadeWhenPlaying && getHost() != null) {
            getHost().setControlsOverlayAutoHideEnabled(false);
        }
    }

    public boolean isFadingEnabled() {
        return this.mFadeWhenPlaying;
    }

    public void setControlsRow(PlaybackControlsRow controlsRow) {
        this.mControlsRow = controlsRow;
        this.mControlsRow.setPrimaryActionsAdapter(createPrimaryActionsAdapter(new ControlButtonPresenterSelector()));
        ArrayObjectAdapter secondaryActions = new ArrayObjectAdapter((PresenterSelector) new ControlButtonPresenterSelector());
        onCreateSecondaryActions(secondaryActions);
        getControlsRow().setSecondaryActionsAdapter(secondaryActions);
        updateControlsRow();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SparseArrayObjectAdapter createPrimaryActionsAdapter(PresenterSelector presenterSelector) {
        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter(presenterSelector);
        onCreatePrimaryActions(adapter);
        return adapter;
    }

    @Deprecated
    public void setControlsRowPresenter(PlaybackControlsRowPresenter presenter) {
        this.mControlsRowPresenter = presenter;
    }

    public PlaybackControlsRow getControlsRow() {
        return this.mControlsRow;
    }

    @Deprecated
    public PlaybackControlsRowPresenter getControlsRowPresenter() {
        if (this.mControlsRowPresenter instanceof PlaybackControlsRowPresenter) {
            return (PlaybackControlsRowPresenter) this.mControlsRowPresenter;
        }
        return null;
    }

    public void setPlaybackRowPresenter(PlaybackRowPresenter presenter) {
        this.mControlsRowPresenter = presenter;
    }

    public PlaybackRowPresenter getPlaybackRowPresenter() {
        return this.mControlsRowPresenter;
    }

    public void enableProgressUpdating(boolean enable) {
    }

    public int getUpdatePeriod() {
        return DMNativeDaemonConnector.ResponseCode.CommandSyntaxError;
    }

    public void updateProgress() {
        int position = getCurrentPosition();
        if (this.mControlsRow != null) {
            this.mControlsRow.setCurrentTime(position);
        }
    }

    public void onActionClicked(Action action) {
        dispatchAction(action, (KeyEvent) null);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (!(keyCode == 4 || keyCode == 111)) {
            switch (keyCode) {
                case 19:
                case 20:
                case 21:
                case 22:
                    break;
                default:
                    SparseArrayObjectAdapter primaryActionsAdapter = (SparseArrayObjectAdapter) this.mControlsRow.getPrimaryActionsAdapter();
                    Action action = this.mControlsRow.getActionForKeyCode(primaryActionsAdapter, keyCode);
                    if (action == null || (action != primaryActionsAdapter.lookup(64) && action != primaryActionsAdapter.lookup(32) && action != primaryActionsAdapter.lookup(128) && action != primaryActionsAdapter.lookup(16) && action != primaryActionsAdapter.lookup(256))) {
                        return false;
                    }
                    if (event.getAction() == 0) {
                        dispatchAction(action, event);
                    }
                    return true;
            }
        }
        if (!(this.mPlaybackSpeed >= 10 || this.mPlaybackSpeed <= -10)) {
            return false;
        }
        this.mPlaybackSpeed = 1;
        play(this.mPlaybackSpeed);
        updatePlaybackStatusAfterUserAction();
        if (keyCode == 4 || keyCode == 111) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean dispatchAction(Action action, KeyEvent keyEvent) {
        if (action == this.mPlayPauseAction) {
            boolean canPlay = keyEvent == null || keyEvent.getKeyCode() == 85 || keyEvent.getKeyCode() == 126;
            if ((keyEvent == null || keyEvent.getKeyCode() == 85 || keyEvent.getKeyCode() == 127) && (!canPlay ? this.mPlaybackSpeed != 0 : this.mPlaybackSpeed == 1)) {
                this.mPlaybackSpeed = 0;
                pause();
            } else if (canPlay && this.mPlaybackSpeed != 1) {
                this.mPlaybackSpeed = 1;
                play(this.mPlaybackSpeed);
            }
            updatePlaybackStatusAfterUserAction();
            return true;
        } else if (action == this.mSkipNextAction) {
            next();
            return true;
        } else if (action == this.mSkipPreviousAction) {
            previous();
            return true;
        } else if (action == this.mFastForwardAction) {
            if (this.mPlaybackSpeed < getMaxForwardSpeedId()) {
                switch (this.mPlaybackSpeed) {
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        this.mPlaybackSpeed++;
                        break;
                    default:
                        this.mPlaybackSpeed = 10;
                        break;
                }
                play(this.mPlaybackSpeed);
                updatePlaybackStatusAfterUserAction();
            }
            return true;
        } else if (action != this.mRewindAction) {
            return false;
        } else {
            if (this.mPlaybackSpeed > (-getMaxRewindSpeedId())) {
                switch (this.mPlaybackSpeed) {
                    case -13:
                    case -12:
                    case -11:
                    case -10:
                        this.mPlaybackSpeed--;
                        break;
                    default:
                        this.mPlaybackSpeed = -10;
                        break;
                }
                play(this.mPlaybackSpeed);
                updatePlaybackStatusAfterUserAction();
            }
            return true;
        }
    }

    private int getMaxForwardSpeedId() {
        return 10 + (this.mFastForwardSpeeds.length - 1);
    }

    private int getMaxRewindSpeedId() {
        return 10 + (this.mRewindSpeeds.length - 1);
    }

    private void updateControlsRow() {
        updateRowMetadata();
        updateControlButtons();
        sHandler.removeMessages(100, this.mGlueWeakReference);
        updatePlaybackState();
    }

    private void updatePlaybackStatusAfterUserAction() {
        updatePlaybackState(this.mPlaybackSpeed);
        sHandler.removeMessages(100, this.mGlueWeakReference);
        sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), MessageType.delayMillis5);
    }

    public void play(int speed) {
    }

    public final void play() {
        play(1);
    }

    private void updateRowMetadata() {
        if (this.mControlsRow != null) {
            if (!hasValidMedia()) {
                this.mControlsRow.setImageDrawable((Drawable) null);
                this.mControlsRow.setTotalTime(0);
                this.mControlsRow.setCurrentTime(0);
            } else {
                this.mControlsRow.setImageDrawable(getMediaArt());
                this.mControlsRow.setTotalTime(getMediaDuration());
                this.mControlsRow.setCurrentTime(getCurrentPosition());
            }
            if (getHost() != null) {
                getHost().notifyPlaybackRowChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePlaybackState() {
        if (hasValidMedia()) {
            this.mPlaybackSpeed = getCurrentSpeedId();
            updatePlaybackState(this.mPlaybackSpeed);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateControlButtons() {
        SparseArrayObjectAdapter primaryActionsAdapter = (SparseArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
        long actions = getSupportedActions();
        if ((actions & 16) != 0 && this.mSkipPreviousAction == null) {
            this.mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getContext());
            primaryActionsAdapter.set(16, this.mSkipPreviousAction);
        } else if ((16 & actions) == 0 && this.mSkipPreviousAction != null) {
            primaryActionsAdapter.clear(16);
            this.mSkipPreviousAction = null;
        }
        if ((actions & 32) != 0 && this.mRewindAction == null) {
            this.mRewindAction = new PlaybackControlsRow.RewindAction(getContext(), this.mRewindSpeeds.length);
            primaryActionsAdapter.set(32, this.mRewindAction);
        } else if ((32 & actions) == 0 && this.mRewindAction != null) {
            primaryActionsAdapter.clear(32);
            this.mRewindAction = null;
        }
        if ((actions & 64) != 0 && this.mPlayPauseAction == null) {
            this.mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
            primaryActionsAdapter.set(64, this.mPlayPauseAction);
        } else if ((64 & actions) == 0 && this.mPlayPauseAction != null) {
            primaryActionsAdapter.clear(64);
            this.mPlayPauseAction = null;
        }
        if ((actions & 128) != 0 && this.mFastForwardAction == null) {
            this.mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getContext(), this.mFastForwardSpeeds.length);
            primaryActionsAdapter.set(128, this.mFastForwardAction);
        } else if ((128 & actions) == 0 && this.mFastForwardAction != null) {
            primaryActionsAdapter.clear(128);
            this.mFastForwardAction = null;
        }
        if ((actions & 256) != 0 && this.mSkipNextAction == null) {
            this.mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getContext());
            primaryActionsAdapter.set(256, this.mSkipNextAction);
        } else if ((256 & actions) == 0 && this.mSkipNextAction != null) {
            primaryActionsAdapter.clear(256);
            this.mSkipNextAction = null;
        }
    }

    private void updatePlaybackState(int playbackSpeed) {
        if (this.mControlsRow != null) {
            SparseArrayObjectAdapter primaryActionsAdapter = (SparseArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
            if (this.mFastForwardAction != null) {
                int index = 0;
                if (playbackSpeed >= 10) {
                    index = (playbackSpeed - 10) + 1;
                }
                if (this.mFastForwardAction.getIndex() != index) {
                    this.mFastForwardAction.setIndex(index);
                    notifyItemChanged(primaryActionsAdapter, this.mFastForwardAction);
                }
            }
            if (this.mRewindAction != null) {
                int index2 = 0;
                if (playbackSpeed <= -10) {
                    index2 = ((-playbackSpeed) - 10) + 1;
                }
                if (this.mRewindAction.getIndex() != index2) {
                    this.mRewindAction.setIndex(index2);
                    notifyItemChanged(primaryActionsAdapter, this.mRewindAction);
                }
            }
            int index3 = 0;
            if (playbackSpeed == 0) {
                updateProgress();
                enableProgressUpdating(false);
            } else {
                enableProgressUpdating(true);
            }
            if (this.mFadeWhenPlaying && getHost() != null) {
                getHost().setControlsOverlayAutoHideEnabled(playbackSpeed == 1);
            }
            if (this.mPlayPauseAction != null) {
                if (playbackSpeed != 0) {
                    index3 = 1;
                }
                if (this.mPlayPauseAction.getIndex() != index3) {
                    this.mPlayPauseAction.setIndex(index3);
                    notifyItemChanged(primaryActionsAdapter, this.mPlayPauseAction);
                }
            }
            List<PlaybackGlue.PlayerCallback> callbacks = getPlayerCallbacks();
            if (callbacks != null) {
                int size = callbacks.size();
                for (int i = 0; i < size; i++) {
                    callbacks.get(i).onPlayStateChanged(this);
                }
            }
        }
    }

    private static void notifyItemChanged(SparseArrayObjectAdapter adapter, Object object) {
        int index = adapter.indexOf(object);
        if (index >= 0) {
            adapter.notifyArrayItemRangeChanged(index, 1);
        }
    }

    private static String getSpeedString(int speed) {
        switch (speed) {
            case -14:
                return "-PLAYBACK_SPEED_FAST_L4";
            case -13:
                return "-PLAYBACK_SPEED_FAST_L3";
            case -12:
                return "-PLAYBACK_SPEED_FAST_L2";
            case -11:
                return "-PLAYBACK_SPEED_FAST_L1";
            case -10:
                return "-PLAYBACK_SPEED_FAST_L0";
            default:
                switch (speed) {
                    case -1:
                        return "PLAYBACK_SPEED_INVALID";
                    case 0:
                        return "PLAYBACK_SPEED_PAUSED";
                    case 1:
                        return "PLAYBACK_SPEED_NORMAL";
                    default:
                        switch (speed) {
                            case 10:
                                return "PLAYBACK_SPEED_FAST_L0";
                            case 11:
                                return "PLAYBACK_SPEED_FAST_L1";
                            case 12:
                                return "PLAYBACK_SPEED_FAST_L2";
                            case 13:
                                return "PLAYBACK_SPEED_FAST_L3";
                            case 14:
                                return "PLAYBACK_SPEED_FAST_L4";
                            default:
                                return null;
                        }
                }
        }
    }

    public boolean isPlaying() {
        return isMediaPlaying();
    }

    /* access modifiers changed from: protected */
    public void onCreatePrimaryActions(SparseArrayObjectAdapter primaryActionsAdapter) {
    }

    /* access modifiers changed from: protected */
    public void onCreateSecondaryActions(ArrayObjectAdapter secondaryActionsAdapter) {
    }

    /* access modifiers changed from: protected */
    public void onStateChanged() {
        if (hasValidMedia()) {
            if (sHandler.hasMessages(100, this.mGlueWeakReference)) {
                sHandler.removeMessages(100, this.mGlueWeakReference);
                if (getCurrentSpeedId() != this.mPlaybackSpeed) {
                    sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), MessageType.delayMillis5);
                } else {
                    updatePlaybackState();
                }
            } else {
                updatePlaybackState();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMetadataChanged() {
        updateRowMetadata();
    }
}
