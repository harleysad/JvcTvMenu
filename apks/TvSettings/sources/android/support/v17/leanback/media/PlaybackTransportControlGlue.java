package android.support.v17.leanback.media;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PlaybackSeekDataProvider;
import android.support.v17.leanback.widget.PlaybackSeekUi;
import android.support.v17.leanback.widget.PlaybackTransportRowPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.KeyEvent;
import android.view.View;
import java.lang.ref.WeakReference;

public class PlaybackTransportControlGlue<T extends PlayerAdapter> extends PlaybackBaseControlGlue<T> {
    static final boolean DEBUG = false;
    static final int MSG_UPDATE_PLAYBACK_STATE = 100;
    static final String TAG = "PlaybackTransportGlue";
    static final int UPDATE_PLAYBACK_STATE_DELAY_MS = 2000;
    static final Handler sHandler = new UpdatePlaybackStateHandler();
    final WeakReference<PlaybackBaseControlGlue> mGlueWeakReference = new WeakReference<>(this);
    final PlaybackTransportControlGlue<T>.SeekUiClient mPlaybackSeekUiClient = new SeekUiClient();
    boolean mSeekEnabled;
    PlaybackSeekDataProvider mSeekProvider;

    static class UpdatePlaybackStateHandler extends Handler {
        UpdatePlaybackStateHandler() {
        }

        public void handleMessage(Message msg) {
            PlaybackTransportControlGlue glue;
            if (msg.what == 100 && (glue = (PlaybackTransportControlGlue) ((WeakReference) msg.obj).get()) != null) {
                glue.onUpdatePlaybackState();
            }
        }
    }

    public PlaybackTransportControlGlue(Context context, T impl) {
        super(context, impl);
    }

    public void setControlsRow(PlaybackControlsRow controlsRow) {
        super.setControlsRow(controlsRow);
        sHandler.removeMessages(100, this.mGlueWeakReference);
        onUpdatePlaybackState();
    }

    /* access modifiers changed from: protected */
    public void onCreatePrimaryActions(ArrayObjectAdapter primaryActionsAdapter) {
        PlaybackControlsRow.PlayPauseAction playPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
        this.mPlayPauseAction = playPauseAction;
        primaryActionsAdapter.add(playPauseAction);
    }

    /* access modifiers changed from: protected */
    public PlaybackRowPresenter onCreateRowPresenter() {
        AbstractDetailsDescriptionPresenter detailsPresenter = new AbstractDetailsDescriptionPresenter() {
            /* access modifiers changed from: protected */
            public void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder viewHolder, Object obj) {
                PlaybackBaseControlGlue glue = (PlaybackBaseControlGlue) obj;
                viewHolder.getTitle().setText(glue.getTitle());
                viewHolder.getSubtitle().setText(glue.getSubtitle());
            }
        };
        PlaybackTransportRowPresenter rowPresenter = new PlaybackTransportRowPresenter() {
            /* access modifiers changed from: protected */
            public void onBindRowViewHolder(RowPresenter.ViewHolder vh, Object item) {
                super.onBindRowViewHolder(vh, item);
                vh.setOnKeyListener(PlaybackTransportControlGlue.this);
            }

            /* access modifiers changed from: protected */
            public void onUnbindRowViewHolder(RowPresenter.ViewHolder vh) {
                super.onUnbindRowViewHolder(vh);
                vh.setOnKeyListener((View.OnKeyListener) null);
            }
        };
        rowPresenter.setDescriptionPresenter(detailsPresenter);
        return rowPresenter;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHost(PlaybackGlueHost host) {
        super.onAttachedToHost(host);
        if (host instanceof PlaybackSeekUi) {
            ((PlaybackSeekUi) host).setPlaybackSeekUiClient(this.mPlaybackSeekUiClient);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromHost() {
        super.onDetachedFromHost();
        if (getHost() instanceof PlaybackSeekUi) {
            ((PlaybackSeekUi) getHost()).setPlaybackSeekUiClient((PlaybackSeekUi.Client) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onUpdateProgress() {
        if (!this.mPlaybackSeekUiClient.mIsSeek) {
            super.onUpdateProgress();
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
                    Action action = this.mControlsRow.getActionForKeyCode(this.mControlsRow.getPrimaryActionsAdapter(), keyCode);
                    if (action == null) {
                        action = this.mControlsRow.getActionForKeyCode(this.mControlsRow.getSecondaryActionsAdapter(), keyCode);
                    }
                    if (action == null) {
                        return false;
                    }
                    if (event.getAction() != 0) {
                        return true;
                    }
                    dispatchAction(action, event);
                    return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void onUpdatePlaybackStatusAfterUserAction() {
        updatePlaybackState(this.mIsPlaying);
        sHandler.removeMessages(100, this.mGlueWeakReference);
        sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), 2000);
    }

    /* access modifiers changed from: package-private */
    public boolean dispatchAction(Action action, KeyEvent keyEvent) {
        if (action instanceof PlaybackControlsRow.PlayPauseAction) {
            boolean canPlay = keyEvent == null || keyEvent.getKeyCode() == 85 || keyEvent.getKeyCode() == 126;
            if ((keyEvent == null || keyEvent.getKeyCode() == 85 || keyEvent.getKeyCode() == 127) && this.mIsPlaying) {
                this.mIsPlaying = false;
                pause();
            } else if (canPlay && !this.mIsPlaying) {
                this.mIsPlaying = true;
                play();
            }
            onUpdatePlaybackStatusAfterUserAction();
            return true;
        } else if (action instanceof PlaybackControlsRow.SkipNextAction) {
            next();
            return true;
        } else if (!(action instanceof PlaybackControlsRow.SkipPreviousAction)) {
            return false;
        } else {
            previous();
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onPlayStateChanged() {
        if (sHandler.hasMessages(100, this.mGlueWeakReference)) {
            sHandler.removeMessages(100, this.mGlueWeakReference);
            if (this.mPlayerAdapter.isPlaying() != this.mIsPlaying) {
                sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), 2000);
            } else {
                onUpdatePlaybackState();
            }
        } else {
            onUpdatePlaybackState();
        }
        super.onPlayStateChanged();
    }

    /* access modifiers changed from: package-private */
    public void onUpdatePlaybackState() {
        this.mIsPlaying = this.mPlayerAdapter.isPlaying();
        updatePlaybackState(this.mIsPlaying);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: CodeShrinkVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x002f: MOVE  (r0v3 'index' int) = (r4v0 'isPlaying' boolean)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    private void updatePlaybackState(boolean r4) {
        /*
            r3 = this;
            android.support.v17.leanback.widget.PlaybackControlsRow r0 = r3.mControlsRow
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            if (r4 != 0) goto L_0x0014
            r3.onUpdateProgress()
            android.support.v17.leanback.media.PlayerAdapter r0 = r3.mPlayerAdapter
            android.support.v17.leanback.media.PlaybackTransportControlGlue<T>$SeekUiClient r1 = r3.mPlaybackSeekUiClient
            boolean r1 = r1.mIsSeek
            r0.setProgressUpdatingEnabled(r1)
            goto L_0x001a
        L_0x0014:
            android.support.v17.leanback.media.PlayerAdapter r0 = r3.mPlayerAdapter
            r1 = 1
            r0.setProgressUpdatingEnabled(r1)
        L_0x001a:
            boolean r0 = r3.mFadeWhenPlaying
            if (r0 == 0) goto L_0x002b
            android.support.v17.leanback.media.PlaybackGlueHost r0 = r3.getHost()
            if (r0 == 0) goto L_0x002b
            android.support.v17.leanback.media.PlaybackGlueHost r0 = r3.getHost()
            r0.setControlsOverlayAutoHideEnabled(r4)
        L_0x002b:
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r0 = r3.mPlayPauseAction
            if (r0 == 0) goto L_0x004c
            r0 = r4
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r1 = r3.mPlayPauseAction
            int r1 = r1.getIndex()
            if (r1 == r0) goto L_0x004c
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r1 = r3.mPlayPauseAction
            r1.setIndex(r0)
            android.support.v17.leanback.widget.PlaybackControlsRow r1 = r3.getControlsRow()
            android.support.v17.leanback.widget.ObjectAdapter r1 = r1.getPrimaryActionsAdapter()
            android.support.v17.leanback.widget.ArrayObjectAdapter r1 = (android.support.v17.leanback.widget.ArrayObjectAdapter) r1
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r2 = r3.mPlayPauseAction
            notifyItemChanged(r1, r2)
        L_0x004c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.media.PlaybackTransportControlGlue.updatePlaybackState(boolean):void");
    }

    class SeekUiClient extends PlaybackSeekUi.Client {
        boolean mIsSeek;
        long mLastUserPosition;
        boolean mPausedBeforeSeek;
        long mPositionBeforeSeek;

        SeekUiClient() {
        }

        public PlaybackSeekDataProvider getPlaybackSeekDataProvider() {
            return PlaybackTransportControlGlue.this.mSeekProvider;
        }

        public boolean isSeekEnabled() {
            return PlaybackTransportControlGlue.this.mSeekProvider != null || PlaybackTransportControlGlue.this.mSeekEnabled;
        }

        public void onSeekStarted() {
            this.mIsSeek = true;
            this.mPausedBeforeSeek = !PlaybackTransportControlGlue.this.isPlaying();
            PlaybackTransportControlGlue.this.mPlayerAdapter.setProgressUpdatingEnabled(true);
            this.mPositionBeforeSeek = PlaybackTransportControlGlue.this.mSeekProvider == null ? PlaybackTransportControlGlue.this.mPlayerAdapter.getCurrentPosition() : -1;
            this.mLastUserPosition = -1;
            PlaybackTransportControlGlue.this.pause();
        }

        public void onSeekPositionChanged(long pos) {
            if (PlaybackTransportControlGlue.this.mSeekProvider == null) {
                PlaybackTransportControlGlue.this.mPlayerAdapter.seekTo(pos);
            } else {
                this.mLastUserPosition = pos;
            }
            if (PlaybackTransportControlGlue.this.mControlsRow != null) {
                PlaybackTransportControlGlue.this.mControlsRow.setCurrentPosition(pos);
            }
        }

        public void onSeekFinished(boolean cancelled) {
            if (!cancelled) {
                if (this.mLastUserPosition >= 0) {
                    PlaybackTransportControlGlue.this.seekTo(this.mLastUserPosition);
                }
            } else if (this.mPositionBeforeSeek >= 0) {
                PlaybackTransportControlGlue.this.seekTo(this.mPositionBeforeSeek);
            }
            this.mIsSeek = false;
            if (!this.mPausedBeforeSeek) {
                PlaybackTransportControlGlue.this.play();
                return;
            }
            PlaybackTransportControlGlue.this.mPlayerAdapter.setProgressUpdatingEnabled(false);
            PlaybackTransportControlGlue.this.onUpdateProgress();
        }
    }

    public final void setSeekProvider(PlaybackSeekDataProvider seekProvider) {
        this.mSeekProvider = seekProvider;
    }

    public final PlaybackSeekDataProvider getSeekProvider() {
        return this.mSeekProvider;
    }

    public final void setSeekEnabled(boolean seekEnabled) {
        this.mSeekEnabled = seekEnabled;
    }

    public final boolean isSeekEnabled() {
        return this.mSeekEnabled;
    }
}
