package com.mediatek.wwtv.tvcenter.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvConfig;

public final class AudioFocusManager {
    public static final int AUDIO_EPG_BARKER = 2;
    public static final int AUDIO_EWBS = 4;
    public static final int AUDIO_NAVIGATOR = 1;
    public static final String TAG = "MediaFocusManager";
    private static AudioFocusManager mAudioFocusManager = null;
    AudioAttributes mAttributeMusic = new AudioAttributes.Builder().setUsage(1).setContentType(2).build();
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.d(AudioFocusManager.TAG, "onAudioFocusChange, " + focusChange);
            if (focusChange == -1 || focusChange == -2 || focusChange == -3) {
                if (!CommonIntegration.getInstance().is3rdTVSource() || !DestroyApp.isCurActivityTkuiMainActivity()) {
                    AudioFocusManager.this.muteTVAudioInternal(1);
                } else {
                    Log.d(AudioFocusManager.TAG, "3rd channel playing, ignore");
                }
            } else if (focusChange == 1) {
                AudioFocusManager.this.unmuteTVAudioInternal(1);
            }
        }
    };
    private AudioManager mAudioManager = null;
    private AudioTrack mAudioTrack;
    private int mBits = 0;
    private Handler mHandler = null;
    private boolean mLoss = true;
    protected MediaSession mMediaSession;
    protected MediaMetadata.Builder mMetadataBuilder;
    private AudioFocusRequest mRequest = null;

    public void createAudioTrack() {
        AudioAttributes.Builder attributes_builder = new AudioAttributes.Builder();
        attributes_builder.setContentType(2);
        attributes_builder.setUsage(1);
        AudioAttributes attributes = attributes_builder.build();
        AudioFormat.Builder format_builder = new AudioFormat.Builder();
        format_builder.setSampleRate(48000);
        format_builder.setEncoding(2);
        format_builder.setChannelMask(12);
        try {
            this.mAudioTrack = new AudioTrack(attributes, format_builder.build(), 1024, 1, 0);
            this.mAudioTrack.play();
        } catch (Exception e) {
            MtkLog.e(TAG, "audiotrack init fail");
        }
    }

    public void createMediaSession(Context context) {
        MtkLog.d(TAG, "createMediaSession");
        this.mMediaSession = new MediaSession(context, "com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity", 0);
        this.mMediaSession.setFlags(3);
        MtkLog.d(TAG, "setActive true");
        this.mMediaSession.setActive(true);
        MtkLog.d(TAG, "setMetadata title");
        this.mMetadataBuilder = new MediaMetadata.Builder();
        this.mMetadataBuilder.putString("android.media.metadata.TITLE", "test");
        this.mMediaSession.setMetadata(this.mMetadataBuilder.build());
        setMediaPlaybackPlaying();
    }

    public void setMediaPlaybackPlaying() {
        if (this.mMediaSession != null) {
            MtkLog.d(TAG, "setPlaybackState 3");
            PlaybackState.Builder mPb = new PlaybackState.Builder();
            mPb.setState(3, 0, 1.0f);
            this.mMediaSession.setPlaybackState(mPb.build());
        }
    }

    public void releaseAudioTrackAndMediaSession() {
        MtkLog.d(TAG, "releaseAudioTrackAndMediaSession");
        if (this.mAudioTrack != null) {
            this.mAudioTrack.pause();
            this.mAudioTrack.flush();
            this.mAudioTrack.release();
        }
        if (this.mMediaSession != null && this.mMediaSession.isActive()) {
            PlaybackState.Builder mPb = new PlaybackState.Builder();
            mPb.setState(0, 0, 0.0f);
            this.mMediaSession.setPlaybackState(mPb.build());
            this.mMediaSession.release();
        }
    }

    public static synchronized AudioFocusManager getInstance(Context context) {
        AudioFocusManager audioFocusManager;
        synchronized (AudioFocusManager.class) {
            if (mAudioFocusManager == null) {
                mAudioFocusManager = new AudioFocusManager(context);
            }
            audioFocusManager = mAudioFocusManager;
        }
        return audioFocusManager;
    }

    private AudioFocusManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        synchronized (AudioFocusManager.class) {
            if (this.mHandler == null) {
                HandlerThread mThread = new HandlerThread(TAG, 0);
                mThread.start();
                this.mHandler = new Handler(mThread.getLooper());
            }
        }
        this.mHandler.post(new Runnable(context) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AudioFocusManager.lambda$init$0(AudioFocusManager.this, this.f$1);
            }
        });
    }

    public static /* synthetic */ void lambda$init$0(AudioFocusManager audioFocusManager, Context context) {
        synchronized (AudioFocusManager.class) {
            audioFocusManager.mAudioManager = (AudioManager) context.getSystemService("audio");
            audioFocusManager.mRequest = new AudioFocusRequest.Builder(1).setAudioAttributes(audioFocusManager.mAttributeMusic).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener(audioFocusManager.mAudioFocusListener, audioFocusManager.mHandler).setWillPauseWhenDucked(true).build();
        }
    }

    public void deinit() {
        this.mAudioManager = null;
    }

    public void requestAudioFocus() {
        if (this.mHandler != null) {
            this.mHandler.post(new Runnable() {
                public final void run() {
                    AudioFocusManager.lambda$requestAudioFocus$1(AudioFocusManager.this);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$requestAudioFocus$1(AudioFocusManager audioFocusManager) {
        synchronized (AudioFocusManager.class) {
            try {
                audioFocusManager.mAudioManager.requestAudioFocus(audioFocusManager.mRequest);
                Log.d(TAG, "requestAudioFocus, " + audioFocusManager.mRequest + "," + audioFocusManager.mBits);
                audioFocusManager.unmuteTVAudioInternal(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void abandonAudioFocus() {
        if (this.mHandler != null) {
            this.mHandler.post(new Runnable() {
                public final void run() {
                    AudioFocusManager.lambda$abandonAudioFocus$2(AudioFocusManager.this);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$abandonAudioFocus$2(AudioFocusManager audioFocusManager) {
        synchronized (AudioFocusManager.class) {
            audioFocusManager.mAudioManager.abandonAudioFocus(audioFocusManager.mAudioFocusListener, audioFocusManager.mAttributeMusic);
        }
    }

    public void muteTVAudio(int bitFlag) {
        if (this.mHandler != null) {
            this.mHandler.post(new Runnable(bitFlag) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioFocusManager.this.muteTVAudioInternal(this.f$1);
                }
            });
        }
    }

    public void unmuteTVAudio(int bitFlag) {
        if (this.mHandler != null) {
            this.mHandler.post(new Runnable(bitFlag) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioFocusManager.this.unmuteTVAudioInternal(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void muteTVAudioInternal(int bitFlag) {
        Log.d(TAG, "muteTVAudio, " + bitFlag + "," + this.mBits);
        this.mBits = this.mBits | bitFlag;
        this.mLoss = true;
        MtkTvConfig.getInstance().setAndroidWorldInfoToLinux(1, 0);
    }

    /* access modifiers changed from: private */
    public void unmuteTVAudioInternal(int bitFlag) {
        Log.d(TAG, "muteTVAudio, " + bitFlag + "," + this.mBits);
        this.mBits = this.mBits & (~bitFlag);
        if (this.mLoss && this.mBits == 0) {
            this.mLoss = false;
            MtkTvConfig.getInstance().setAndroidWorldInfoToLinux(1, 1);
        }
    }
}
