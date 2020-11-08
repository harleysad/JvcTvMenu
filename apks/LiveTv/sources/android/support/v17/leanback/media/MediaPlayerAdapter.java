package android.support.v17.leanback.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v17.leanback.R;
import android.view.SurfaceHolder;
import java.io.IOException;

public class MediaPlayerAdapter extends PlayerAdapter {
    long mBufferedProgress;
    boolean mBufferingStart;
    Context mContext;
    final Handler mHandler = new Handler();
    boolean mHasDisplay;
    boolean mInitialized = false;
    Uri mMediaSourceUri = null;
    final MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            MediaPlayerAdapter.this.mBufferedProgress = (MediaPlayerAdapter.this.getDuration() * ((long) percent)) / 100;
            MediaPlayerAdapter.this.getCallback().onBufferedPositionChanged(MediaPlayerAdapter.this);
        }
    };
    final MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            MediaPlayerAdapter.this.getCallback().onPlayStateChanged(MediaPlayerAdapter.this);
            MediaPlayerAdapter.this.getCallback().onPlayCompleted(MediaPlayerAdapter.this);
        }
    };
    final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            MediaPlayerAdapter.this.getCallback().onError(MediaPlayerAdapter.this, what, MediaPlayerAdapter.this.mContext.getString(R.string.lb_media_player_error, new Object[]{Integer.valueOf(what), Integer.valueOf(extra)}));
            return MediaPlayerAdapter.this.onError(what, extra);
        }
    };
    final MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            boolean handled = false;
            switch (what) {
                case 701:
                    MediaPlayerAdapter.this.mBufferingStart = true;
                    MediaPlayerAdapter.this.notifyBufferingStartEnd();
                    handled = true;
                    break;
                case 702:
                    MediaPlayerAdapter.this.mBufferingStart = false;
                    MediaPlayerAdapter.this.notifyBufferingStartEnd();
                    handled = true;
                    break;
            }
            boolean thisHandled = MediaPlayerAdapter.this.onInfo(what, extra);
            if (handled || thisHandled) {
                return true;
            }
            return false;
        }
    };
    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            MediaPlayerAdapter.this.mInitialized = true;
            MediaPlayerAdapter.this.notifyBufferingStartEnd();
            if (MediaPlayerAdapter.this.mSurfaceHolderGlueHost == null || MediaPlayerAdapter.this.mHasDisplay) {
                MediaPlayerAdapter.this.getCallback().onPreparedStateChanged(MediaPlayerAdapter.this);
            }
        }
    };
    final MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        public void onSeekComplete(MediaPlayer mp) {
            MediaPlayerAdapter.this.onSeekComplete();
        }
    };
    final MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
            MediaPlayerAdapter.this.getCallback().onVideoSizeChanged(MediaPlayerAdapter.this, width, height);
        }
    };
    final MediaPlayer mPlayer = new MediaPlayer();
    final Runnable mRunnable = new Runnable() {
        public void run() {
            MediaPlayerAdapter.this.getCallback().onCurrentPositionChanged(MediaPlayerAdapter.this);
            MediaPlayerAdapter.this.mHandler.postDelayed(this, (long) MediaPlayerAdapter.this.getProgressUpdatingInterval());
        }
    };
    SurfaceHolderGlueHost mSurfaceHolderGlueHost;

    /* access modifiers changed from: package-private */
    public void notifyBufferingStartEnd() {
        getCallback().onBufferingStateChanged(this, this.mBufferingStart || !this.mInitialized);
    }

    public MediaPlayerAdapter(Context context) {
        this.mContext = context;
    }

    public void onAttachedToHost(PlaybackGlueHost host) {
        if (host instanceof SurfaceHolderGlueHost) {
            this.mSurfaceHolderGlueHost = (SurfaceHolderGlueHost) host;
            this.mSurfaceHolderGlueHost.setSurfaceHolderCallback(new VideoPlayerSurfaceHolderCallback());
        }
    }

    public void reset() {
        changeToUnitialized();
        this.mPlayer.reset();
    }

    /* access modifiers changed from: package-private */
    public void changeToUnitialized() {
        if (this.mInitialized) {
            this.mInitialized = false;
            notifyBufferingStartEnd();
            if (this.mHasDisplay) {
                getCallback().onPreparedStateChanged(this);
            }
        }
    }

    public void release() {
        changeToUnitialized();
        this.mHasDisplay = false;
        this.mPlayer.release();
    }

    public void onDetachedFromHost() {
        if (this.mSurfaceHolderGlueHost != null) {
            this.mSurfaceHolderGlueHost.setSurfaceHolderCallback((SurfaceHolder.Callback) null);
            this.mSurfaceHolderGlueHost = null;
        }
        reset();
        release();
    }

    /* access modifiers changed from: protected */
    public boolean onError(int what, int extra) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSeekComplete() {
    }

    /* access modifiers changed from: protected */
    public boolean onInfo(int what, int extra) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setDisplay(SurfaceHolder surfaceHolder) {
        boolean hadDisplay = this.mHasDisplay;
        this.mHasDisplay = surfaceHolder != null;
        if (hadDisplay != this.mHasDisplay) {
            this.mPlayer.setDisplay(surfaceHolder);
            if (this.mHasDisplay) {
                if (this.mInitialized) {
                    getCallback().onPreparedStateChanged(this);
                }
            } else if (this.mInitialized) {
                getCallback().onPreparedStateChanged(this);
            }
        }
    }

    public void setProgressUpdatingEnabled(boolean enabled) {
        this.mHandler.removeCallbacks(this.mRunnable);
        if (enabled) {
            this.mHandler.postDelayed(this.mRunnable, (long) getProgressUpdatingInterval());
        }
    }

    public int getProgressUpdatingInterval() {
        return 16;
    }

    public boolean isPlaying() {
        return this.mInitialized && this.mPlayer.isPlaying();
    }

    public long getDuration() {
        if (this.mInitialized) {
            return (long) this.mPlayer.getDuration();
        }
        return -1;
    }

    public long getCurrentPosition() {
        if (this.mInitialized) {
            return (long) this.mPlayer.getCurrentPosition();
        }
        return -1;
    }

    public void play() {
        if (this.mInitialized && !this.mPlayer.isPlaying()) {
            this.mPlayer.start();
            getCallback().onPlayStateChanged(this);
            getCallback().onCurrentPositionChanged(this);
        }
    }

    public void pause() {
        if (isPlaying()) {
            this.mPlayer.pause();
            getCallback().onPlayStateChanged(this);
        }
    }

    public void seekTo(long newPosition) {
        if (this.mInitialized) {
            this.mPlayer.seekTo((int) newPosition);
        }
    }

    public long getBufferedPosition() {
        return this.mBufferedProgress;
    }

    public boolean setDataSource(Uri uri) {
        if (this.mMediaSourceUri != null) {
            if (this.mMediaSourceUri.equals(uri)) {
                return false;
            }
        } else if (uri == null) {
            return false;
        }
        this.mMediaSourceUri = uri;
        prepareMediaForPlaying();
        return true;
    }

    private void prepareMediaForPlaying() {
        reset();
        try {
            if (this.mMediaSourceUri != null) {
                this.mPlayer.setDataSource(this.mContext, this.mMediaSourceUri);
                this.mPlayer.setAudioStreamType(3);
                this.mPlayer.setOnPreparedListener(this.mOnPreparedListener);
                this.mPlayer.setOnVideoSizeChangedListener(this.mOnVideoSizeChangedListener);
                this.mPlayer.setOnErrorListener(this.mOnErrorListener);
                this.mPlayer.setOnSeekCompleteListener(this.mOnSeekCompleteListener);
                this.mPlayer.setOnCompletionListener(this.mOnCompletionListener);
                this.mPlayer.setOnInfoListener(this.mOnInfoListener);
                this.mPlayer.setOnBufferingUpdateListener(this.mOnBufferingUpdateListener);
                notifyBufferingStartEnd();
                this.mPlayer.prepareAsync();
                getCallback().onPlayStateChanged(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public final MediaPlayer getMediaPlayer() {
        return this.mPlayer;
    }

    public boolean isPrepared() {
        return this.mInitialized && (this.mSurfaceHolderGlueHost == null || this.mHasDisplay);
    }

    class VideoPlayerSurfaceHolderCallback implements SurfaceHolder.Callback {
        VideoPlayerSurfaceHolderCallback() {
        }

        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            MediaPlayerAdapter.this.setDisplay(surfaceHolder);
        }

        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            MediaPlayerAdapter.this.setDisplay((SurfaceHolder) null);
        }
    }
}
