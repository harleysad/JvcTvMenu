package android.support.v17.leanback.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.KeyEvent;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlaybackBannerControlGlue<T extends PlayerAdapter> extends PlaybackBaseControlGlue<T> {
    public static final int ACTION_CUSTOM_LEFT_FIRST = 1;
    public static final int ACTION_CUSTOM_RIGHT_FIRST = 4096;
    public static final int ACTION_FAST_FORWARD = 128;
    public static final int ACTION_PLAY_PAUSE = 64;
    public static final int ACTION_REWIND = 32;
    public static final int ACTION_SKIP_TO_NEXT = 256;
    public static final int ACTION_SKIP_TO_PREVIOUS = 16;
    private static final int NUMBER_OF_SEEK_SPEEDS = 5;
    public static final int PLAYBACK_SPEED_FAST_L0 = 10;
    public static final int PLAYBACK_SPEED_FAST_L1 = 11;
    public static final int PLAYBACK_SPEED_FAST_L2 = 12;
    public static final int PLAYBACK_SPEED_FAST_L3 = 13;
    public static final int PLAYBACK_SPEED_FAST_L4 = 14;
    public static final int PLAYBACK_SPEED_INVALID = -1;
    public static final int PLAYBACK_SPEED_NORMAL = 1;
    public static final int PLAYBACK_SPEED_PAUSED = 0;
    private static final String TAG = PlaybackBannerControlGlue.class.getSimpleName();
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private final int[] mFastForwardSpeeds;
    private boolean mIsCustomizedFastForwardSupported;
    private boolean mIsCustomizedRewindSupported;
    private int mPlaybackSpeed;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private final int[] mRewindSpeeds;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private long mStartPosition;
    private long mStartTime;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ACTION_ {
    }

    public PlaybackBannerControlGlue(Context context, int[] seekSpeeds, T impl) {
        this(context, seekSpeeds, seekSpeeds, impl);
    }

    public PlaybackBannerControlGlue(Context context, int[] fastForwardSpeeds, int[] rewindSpeeds, T impl) {
        super(context, impl);
        this.mPlaybackSpeed = 0;
        this.mStartPosition = 0;
        if (fastForwardSpeeds.length == 0 || fastForwardSpeeds.length > 5) {
            throw new IllegalArgumentException("invalid fastForwardSpeeds array size");
        }
        this.mFastForwardSpeeds = fastForwardSpeeds;
        if (rewindSpeeds.length == 0 || rewindSpeeds.length > 5) {
            throw new IllegalArgumentException("invalid rewindSpeeds array size");
        }
        this.mRewindSpeeds = rewindSpeeds;
        if ((this.mPlayerAdapter.getSupportedActions() & 128) != 0) {
            this.mIsCustomizedFastForwardSupported = true;
        }
        if ((this.mPlayerAdapter.getSupportedActions() & 32) != 0) {
            this.mIsCustomizedRewindSupported = true;
        }
    }

    public void setControlsRow(PlaybackControlsRow controlsRow) {
        super.setControlsRow(controlsRow);
        onUpdatePlaybackState();
    }

    /* access modifiers changed from: protected */
    public void onCreatePrimaryActions(ArrayObjectAdapter primaryActionsAdapter) {
        long supportedActions = getSupportedActions();
        if ((supportedActions & 16) != 0 && this.mSkipPreviousAction == null) {
            PlaybackControlsRow.SkipPreviousAction skipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getContext());
            this.mSkipPreviousAction = skipPreviousAction;
            primaryActionsAdapter.add(skipPreviousAction);
        } else if ((16 & supportedActions) == 0 && this.mSkipPreviousAction != null) {
            primaryActionsAdapter.remove(this.mSkipPreviousAction);
            this.mSkipPreviousAction = null;
        }
        if ((supportedActions & 32) != 0 && this.mRewindAction == null) {
            PlaybackControlsRow.RewindAction rewindAction = new PlaybackControlsRow.RewindAction(getContext(), this.mRewindSpeeds.length);
            this.mRewindAction = rewindAction;
            primaryActionsAdapter.add(rewindAction);
        } else if ((32 & supportedActions) == 0 && this.mRewindAction != null) {
            primaryActionsAdapter.remove(this.mRewindAction);
            this.mRewindAction = null;
        }
        if ((supportedActions & 64) != 0 && this.mPlayPauseAction == null) {
            this.mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
            PlaybackControlsRow.PlayPauseAction playPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
            this.mPlayPauseAction = playPauseAction;
            primaryActionsAdapter.add(playPauseAction);
        } else if ((64 & supportedActions) == 0 && this.mPlayPauseAction != null) {
            primaryActionsAdapter.remove(this.mPlayPauseAction);
            this.mPlayPauseAction = null;
        }
        if ((supportedActions & 128) != 0 && this.mFastForwardAction == null) {
            this.mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getContext(), this.mFastForwardSpeeds.length);
            PlaybackControlsRow.FastForwardAction fastForwardAction = new PlaybackControlsRow.FastForwardAction(getContext(), this.mFastForwardSpeeds.length);
            this.mFastForwardAction = fastForwardAction;
            primaryActionsAdapter.add(fastForwardAction);
        } else if ((128 & supportedActions) == 0 && this.mFastForwardAction != null) {
            primaryActionsAdapter.remove(this.mFastForwardAction);
            this.mFastForwardAction = null;
        }
        if ((supportedActions & 256) != 0 && this.mSkipNextAction == null) {
            PlaybackControlsRow.SkipNextAction skipNextAction = new PlaybackControlsRow.SkipNextAction(getContext());
            this.mSkipNextAction = skipNextAction;
            primaryActionsAdapter.add(skipNextAction);
        } else if ((256 & supportedActions) == 0 && this.mSkipNextAction != null) {
            primaryActionsAdapter.remove(this.mSkipNextAction);
            this.mSkipNextAction = null;
        }
    }

    /* access modifiers changed from: protected */
    public PlaybackRowPresenter onCreateRowPresenter() {
        return new PlaybackControlsRowPresenter(new AbstractDetailsDescriptionPresenter() {
            /* access modifiers changed from: protected */
            public void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder viewHolder, Object object) {
                PlaybackBannerControlGlue glue = (PlaybackBannerControlGlue) object;
                viewHolder.getTitle().setText(glue.getTitle());
                viewHolder.getSubtitle().setText(glue.getSubtitle());
            }
        }) {
            /* access modifiers changed from: protected */
            public void onBindRowViewHolder(RowPresenter.ViewHolder vh, Object item) {
                super.onBindRowViewHolder(vh, item);
                vh.setOnKeyListener(PlaybackBannerControlGlue.this);
            }

            /* access modifiers changed from: protected */
            public void onUnbindRowViewHolder(RowPresenter.ViewHolder vh) {
                super.onUnbindRowViewHolder(vh);
                vh.setOnKeyListener((View.OnKeyListener) null);
            }
        };
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
                    if (event.getAction() == 0) {
                        dispatchAction(action, event);
                    }
                    return true;
            }
        }
        if (!(this.mPlaybackSpeed >= 10 || this.mPlaybackSpeed <= -10)) {
            return false;
        }
        play();
        onUpdatePlaybackStatusAfterUserAction();
        if (keyCode == 4 || keyCode == 111) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void onUpdatePlaybackStatusAfterUserAction() {
        updatePlaybackState(this.mIsPlaying);
    }

    private void incrementFastForwardPlaybackSpeed() {
        switch (this.mPlaybackSpeed) {
            case 10:
            case 11:
            case 12:
            case 13:
                this.mPlaybackSpeed++;
                return;
            default:
                this.mPlaybackSpeed = 10;
                return;
        }
    }

    private void decrementRewindPlaybackSpeed() {
        switch (this.mPlaybackSpeed) {
            case -13:
            case -12:
            case -11:
            case -10:
                this.mPlaybackSpeed--;
                return;
            default:
                this.mPlaybackSpeed = -10;
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean dispatchAction(Action action, KeyEvent keyEvent) {
        if (action == this.mPlayPauseAction) {
            boolean canPause = false;
            boolean canPlay = keyEvent == null || keyEvent.getKeyCode() == 85 || keyEvent.getKeyCode() == 126;
            if (keyEvent == null || keyEvent.getKeyCode() == 85 || keyEvent.getKeyCode() == 127) {
                canPause = true;
            }
            if (canPause && (!canPlay ? this.mPlaybackSpeed != 0 : this.mPlaybackSpeed == 1)) {
                pause();
            } else if (canPlay && this.mPlaybackSpeed != 1) {
                play();
            }
            onUpdatePlaybackStatusAfterUserAction();
            return true;
        } else if (action == this.mSkipNextAction) {
            next();
            return true;
        } else if (action == this.mSkipPreviousAction) {
            previous();
            return true;
        } else if (action == this.mFastForwardAction) {
            if (this.mPlayerAdapter.isPrepared() && this.mPlaybackSpeed < getMaxForwardSpeedId()) {
                if (this.mIsCustomizedFastForwardSupported) {
                    this.mIsPlaying = true;
                    this.mPlayerAdapter.fastForward();
                } else {
                    fakePause();
                }
                incrementFastForwardPlaybackSpeed();
                onUpdatePlaybackStatusAfterUserAction();
            }
            return true;
        } else if (action != this.mRewindAction) {
            return false;
        } else {
            if (this.mPlayerAdapter.isPrepared() && this.mPlaybackSpeed > (-getMaxRewindSpeedId())) {
                if (this.mIsCustomizedFastForwardSupported) {
                    this.mIsPlaying = true;
                    this.mPlayerAdapter.rewind();
                } else {
                    fakePause();
                }
                decrementRewindPlaybackSpeed();
                onUpdatePlaybackStatusAfterUserAction();
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onPlayStateChanged() {
        onUpdatePlaybackState();
        super.onPlayStateChanged();
    }

    /* access modifiers changed from: protected */
    public void onPlayCompleted() {
        super.onPlayCompleted();
        this.mIsPlaying = false;
        this.mPlaybackSpeed = 0;
        this.mStartPosition = getCurrentPosition();
        this.mStartTime = System.currentTimeMillis();
        onUpdatePlaybackState();
    }

    /* access modifiers changed from: package-private */
    public void onUpdatePlaybackState() {
        updatePlaybackState(this.mIsPlaying);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: CodeShrinkVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0037: MOVE  (r2v9 'index' int) = (r7v0 'isPlaying' boolean)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    private void updatePlaybackState(boolean r7) {
        /*
            r6 = this;
            android.support.v17.leanback.widget.PlaybackControlsRow r0 = r6.mControlsRow
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r0 = 1
            if (r7 != 0) goto L_0x0012
            r6.onUpdateProgress()
            android.support.v17.leanback.media.PlayerAdapter r1 = r6.mPlayerAdapter
            r2 = 0
            r1.setProgressUpdatingEnabled(r2)
            goto L_0x0017
        L_0x0012:
            android.support.v17.leanback.media.PlayerAdapter r1 = r6.mPlayerAdapter
            r1.setProgressUpdatingEnabled(r0)
        L_0x0017:
            boolean r1 = r6.mFadeWhenPlaying
            if (r1 == 0) goto L_0x0028
            android.support.v17.leanback.media.PlaybackGlueHost r1 = r6.getHost()
            if (r1 == 0) goto L_0x0028
            android.support.v17.leanback.media.PlaybackGlueHost r1 = r6.getHost()
            r1.setControlsOverlayAutoHideEnabled(r7)
        L_0x0028:
            android.support.v17.leanback.widget.PlaybackControlsRow r1 = r6.getControlsRow()
            android.support.v17.leanback.widget.ObjectAdapter r1 = r1.getPrimaryActionsAdapter()
            android.support.v17.leanback.widget.ArrayObjectAdapter r1 = (android.support.v17.leanback.widget.ArrayObjectAdapter) r1
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r2 = r6.mPlayPauseAction
            if (r2 == 0) goto L_0x004a
            r2 = r7
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r3 = r6.mPlayPauseAction
            int r3 = r3.getIndex()
            if (r3 == r2) goto L_0x004a
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r3 = r6.mPlayPauseAction
            r3.setIndex(r2)
            android.support.v17.leanback.widget.PlaybackControlsRow$PlayPauseAction r3 = r6.mPlayPauseAction
            notifyItemChanged(r1, r3)
        L_0x004a:
            android.support.v17.leanback.widget.PlaybackControlsRow$FastForwardAction r2 = r6.mFastForwardAction
            r3 = 10
            if (r2 == 0) goto L_0x006c
            r2 = 0
            int r4 = r6.mPlaybackSpeed
            if (r4 < r3) goto L_0x005a
            int r4 = r6.mPlaybackSpeed
            int r4 = r4 - r3
            int r2 = r4 + 1
        L_0x005a:
            android.support.v17.leanback.widget.PlaybackControlsRow$FastForwardAction r4 = r6.mFastForwardAction
            int r4 = r4.getIndex()
            if (r4 == r2) goto L_0x006c
            android.support.v17.leanback.widget.PlaybackControlsRow$FastForwardAction r4 = r6.mFastForwardAction
            r4.setIndex(r2)
            android.support.v17.leanback.widget.PlaybackControlsRow$FastForwardAction r4 = r6.mFastForwardAction
            notifyItemChanged(r1, r4)
        L_0x006c:
            android.support.v17.leanback.widget.PlaybackControlsRow$RewindAction r2 = r6.mRewindAction
            if (r2 == 0) goto L_0x008f
            r2 = 0
            int r4 = r6.mPlaybackSpeed
            r5 = -10
            if (r4 > r5) goto L_0x007d
            int r4 = r6.mPlaybackSpeed
            int r4 = -r4
            int r4 = r4 - r3
            int r2 = r4 + 1
        L_0x007d:
            android.support.v17.leanback.widget.PlaybackControlsRow$RewindAction r0 = r6.mRewindAction
            int r0 = r0.getIndex()
            if (r0 == r2) goto L_0x008f
            android.support.v17.leanback.widget.PlaybackControlsRow$RewindAction r0 = r6.mRewindAction
            r0.setIndex(r2)
            android.support.v17.leanback.widget.PlaybackControlsRow$RewindAction r0 = r6.mRewindAction
            notifyItemChanged(r1, r0)
        L_0x008f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.media.PlaybackBannerControlGlue.updatePlaybackState(boolean):void");
    }

    @NonNull
    public int[] getFastForwardSpeeds() {
        return this.mFastForwardSpeeds;
    }

    @NonNull
    public int[] getRewindSpeeds() {
        return this.mRewindSpeeds;
    }

    private int getMaxForwardSpeedId() {
        return 10 + (this.mFastForwardSpeeds.length - 1);
    }

    private int getMaxRewindSpeedId() {
        return 10 + (this.mRewindSpeeds.length - 1);
    }

    public long getCurrentPosition() {
        int index;
        if (this.mPlaybackSpeed == 0 || this.mPlaybackSpeed == 1) {
            return this.mPlayerAdapter.getCurrentPosition();
        }
        if (this.mPlaybackSpeed >= 10) {
            if (this.mIsCustomizedFastForwardSupported) {
                return this.mPlayerAdapter.getCurrentPosition();
            }
            index = getFastForwardSpeeds()[this.mPlaybackSpeed - 10];
        } else if (this.mPlaybackSpeed > -10) {
            return -1;
        } else {
            if (this.mIsCustomizedRewindSupported) {
                return this.mPlayerAdapter.getCurrentPosition();
            }
            index = -getRewindSpeeds()[(-this.mPlaybackSpeed) - 10];
        }
        long position = this.mStartPosition + ((System.currentTimeMillis() - this.mStartTime) * ((long) index));
        if (position > getDuration()) {
            this.mPlaybackSpeed = 0;
            long position2 = getDuration();
            this.mPlayerAdapter.seekTo(position2);
            this.mStartPosition = 0;
            pause();
            return position2;
        } else if (position >= 0) {
            return position;
        } else {
            this.mPlaybackSpeed = 0;
            this.mPlayerAdapter.seekTo(0);
            this.mStartPosition = 0;
            pause();
            return 0;
        }
    }

    public void play() {
        if (this.mPlayerAdapter.isPrepared()) {
            if (this.mPlaybackSpeed != 0 || this.mPlayerAdapter.getCurrentPosition() < this.mPlayerAdapter.getDuration()) {
                this.mStartPosition = getCurrentPosition();
            } else {
                this.mStartPosition = 0;
            }
            this.mStartTime = System.currentTimeMillis();
            this.mIsPlaying = true;
            this.mPlaybackSpeed = 1;
            this.mPlayerAdapter.seekTo(this.mStartPosition);
            super.play();
            onUpdatePlaybackState();
        }
    }

    public void pause() {
        this.mIsPlaying = false;
        this.mPlaybackSpeed = 0;
        this.mStartPosition = getCurrentPosition();
        this.mStartTime = System.currentTimeMillis();
        super.pause();
        onUpdatePlaybackState();
    }

    private void fakePause() {
        this.mIsPlaying = true;
        this.mStartPosition = getCurrentPosition();
        this.mStartTime = System.currentTimeMillis();
        super.pause();
        onUpdatePlaybackState();
    }
}
