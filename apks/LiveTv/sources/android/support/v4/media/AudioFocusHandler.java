package android.support.v4.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.annotation.GuardedBy;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

@VisibleForTesting(otherwise = 3)
@RestrictTo({RestrictTo.Scope.LIBRARY})
public class AudioFocusHandler {
    private static final boolean DEBUG = false;
    private static final String TAG = "AudioFocusHandler";
    private final AudioFocusHandlerImpl mImpl;

    interface AudioFocusHandlerImpl {
        void close();

        boolean onPauseRequested();

        boolean onPlayRequested();

        void onPlayerStateChanged(int i);

        void sendIntent(Intent intent);
    }

    AudioFocusHandler(Context context, MediaSession2 session) {
        this.mImpl = new AudioFocusHandlerImplBase(context, session);
    }

    public boolean onPlayRequested() {
        return this.mImpl.onPlayRequested();
    }

    public boolean onPauseRequested() {
        return this.mImpl.onPauseRequested();
    }

    public void onPlayerStateChanged(int playerState) {
        this.mImpl.onPlayerStateChanged(playerState);
    }

    public void close() {
        this.mImpl.close();
    }

    public void sendIntent(Intent intent) {
        this.mImpl.sendIntent(intent);
    }

    private static class AudioFocusHandlerImplBase implements AudioFocusHandlerImpl {
        private static final float VOLUME_DUCK_FACTOR = 0.2f;
        /* access modifiers changed from: private */
        @GuardedBy("mLock")
        public AudioAttributesCompat mAudioAttributes;
        private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioFocusListener();
        private final AudioManager mAudioManager;
        private final BroadcastReceiver mBecomingNoisyIntentReceiver = new NoisyIntentReceiver();
        @GuardedBy("mLock")
        private boolean mHasAudioFocus;
        /* access modifiers changed from: private */
        @GuardedBy("mLock")
        public boolean mHasRegisteredReceiver;
        private final IntentFilter mIntentFilter = new IntentFilter("android.media.AUDIO_BECOMING_NOISY");
        /* access modifiers changed from: private */
        public final Object mLock = new Object();
        /* access modifiers changed from: private */
        @GuardedBy("mLock")
        public boolean mResumeWhenAudioFocusGain;
        /* access modifiers changed from: private */
        public final MediaSession2 mSession;

        AudioFocusHandlerImplBase(Context context, MediaSession2 session) {
            this.mSession = session;
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003e, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateAudioAttributesIfNeeded() {
            /*
                r4 = this;
                android.support.v4.media.MediaSession2 r0 = r4.mSession
                android.support.v4.media.VolumeProviderCompat r0 = r0.getVolumeProvider()
                if (r0 == 0) goto L_0x000a
                r0 = 0
                goto L_0x0019
            L_0x000a:
                android.support.v4.media.MediaSession2 r0 = r4.mSession
                android.support.v4.media.BaseMediaPlayer r0 = r0.getPlayer()
                if (r0 != 0) goto L_0x0014
                r1 = 0
                goto L_0x0018
            L_0x0014:
                android.support.v4.media.AudioAttributesCompat r1 = r0.getAudioAttributes()
            L_0x0018:
                r0 = r1
            L_0x0019:
                java.lang.Object r1 = r4.mLock
                monitor-enter(r1)
                android.support.v4.media.AudioAttributesCompat r2 = r4.mAudioAttributes     // Catch:{ all -> 0x003f }
                boolean r2 = android.support.v4.util.ObjectsCompat.equals(r0, r2)     // Catch:{ all -> 0x003f }
                if (r2 == 0) goto L_0x0026
                monitor-exit(r1)     // Catch:{ all -> 0x003f }
                return
            L_0x0026:
                r4.mAudioAttributes = r0     // Catch:{ all -> 0x003f }
                boolean r2 = r4.mHasAudioFocus     // Catch:{ all -> 0x003f }
                if (r2 == 0) goto L_0x003d
                boolean r2 = r4.requestAudioFocusLocked()     // Catch:{ all -> 0x003f }
                r4.mHasAudioFocus = r2     // Catch:{ all -> 0x003f }
                boolean r2 = r4.mHasAudioFocus     // Catch:{ all -> 0x003f }
                if (r2 != 0) goto L_0x003d
                java.lang.String r2 = "AudioFocusHandler"
                java.lang.String r3 = "Failed to regain audio focus."
                android.util.Log.w(r2, r3)     // Catch:{ all -> 0x003f }
            L_0x003d:
                monitor-exit(r1)     // Catch:{ all -> 0x003f }
                return
            L_0x003f:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x003f }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.updateAudioAttributesIfNeeded():void");
        }

        public boolean onPlayRequested() {
            updateAudioAttributesIfNeeded();
            synchronized (this.mLock) {
                if (!requestAudioFocusLocked()) {
                    return false;
                }
                return true;
            }
        }

        public boolean onPauseRequested() {
            synchronized (this.mLock) {
                this.mResumeWhenAudioFocusGain = false;
            }
            return true;
        }

        public void onPlayerStateChanged(int playerState) {
            synchronized (this.mLock) {
                switch (playerState) {
                    case 0:
                        abandonAudioFocusLocked();
                        break;
                    case 1:
                        updateAudioAttributesIfNeeded();
                        unregisterReceiverLocked();
                        break;
                    case 2:
                        updateAudioAttributesIfNeeded();
                        registerReceiverLocked();
                        break;
                    case 3:
                        abandonAudioFocusLocked();
                        unregisterReceiverLocked();
                        break;
                }
            }
        }

        public void close() {
            synchronized (this.mLock) {
                unregisterReceiverLocked();
                abandonAudioFocusLocked();
            }
        }

        public void sendIntent(Intent intent) {
            this.mBecomingNoisyIntentReceiver.onReceive(this.mSession.getContext(), intent);
        }

        @GuardedBy("mLock")
        private boolean requestAudioFocusLocked() {
            int focusGain = convertAudioAttributesToFocusGainLocked();
            if (focusGain == 0) {
                return true;
            }
            int audioFocusRequestResult = this.mAudioManager.requestAudioFocus(this.mAudioFocusListener, this.mAudioAttributes.getVolumeControlStream(), focusGain);
            if (audioFocusRequestResult == 1) {
                this.mHasAudioFocus = true;
            } else {
                Log.w(AudioFocusHandler.TAG, "requestAudioFocus(" + focusGain + ") failed (return=" + audioFocusRequestResult + ") playback wouldn't start.");
                this.mHasAudioFocus = false;
            }
            this.mResumeWhenAudioFocusGain = false;
            return this.mHasAudioFocus;
        }

        @GuardedBy("mLock")
        private void abandonAudioFocusLocked() {
            if (this.mHasAudioFocus) {
                this.mAudioManager.abandonAudioFocus(this.mAudioFocusListener);
                this.mHasAudioFocus = false;
                this.mResumeWhenAudioFocusGain = false;
            }
        }

        @GuardedBy("mLock")
        private void registerReceiverLocked() {
            if (!this.mHasRegisteredReceiver) {
                this.mSession.getContext().registerReceiver(this.mBecomingNoisyIntentReceiver, this.mIntentFilter);
                this.mHasRegisteredReceiver = true;
            }
        }

        @GuardedBy("mLock")
        private void unregisterReceiverLocked() {
            if (this.mHasRegisteredReceiver) {
                this.mSession.getContext().unregisterReceiver(this.mBecomingNoisyIntentReceiver);
                this.mHasRegisteredReceiver = false;
            }
        }

        @GuardedBy("mLock")
        private int convertAudioAttributesToFocusGainLocked() {
            AudioAttributesCompat audioAttributesCompat = this.mAudioAttributes;
            if (audioAttributesCompat == null) {
                return 0;
            }
            switch (audioAttributesCompat.getUsage()) {
                case 0:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 16:
                    return 3;
                case 1:
                case 14:
                    return 1;
                case 2:
                case 3:
                case 4:
                    return 2;
                default:
                    return 0;
            }
        }

        private class NoisyIntentReceiver extends BroadcastReceiver {
            private NoisyIntentReceiver() {
            }

            /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
                r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.access$200(r4.this$0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:11:0x0024, code lost:
                monitor-enter(r0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:15:0x002c, code lost:
                if (android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.access$400(r4.this$0) != null) goto L_0x0030;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
                monitor-exit(r0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:17:0x002f, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
                r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.access$400(r4.this$0).getUsage();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
                monitor-exit(r0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
                if (r1 == 1) goto L_0x005c;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:23:0x0041, code lost:
                if (r1 == 14) goto L_0x0044;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:24:0x0044, code lost:
                r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.access$500(r4.this$0).getPlayer();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:25:0x004e, code lost:
                if (r0 == null) goto L_?;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:26:0x0050, code lost:
                r0.setPlayerVolume(r0.getPlayerVolume() * android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.VOLUME_DUCK_FACTOR);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x005c, code lost:
                android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.access$500(r4.this$0).pause();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:9:0x001c, code lost:
                if ("android.media.AUDIO_BECOMING_NOISY".equals(r6.getAction()) == false) goto L_?;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r5, android.content.Intent r6) {
                /*
                    r4 = this;
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r0 = r0.mLock
                    monitor-enter(r0)
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x006a }
                    boolean r1 = r1.mHasRegisteredReceiver     // Catch:{ all -> 0x006a }
                    if (r1 != 0) goto L_0x0011
                    monitor-exit(r0)     // Catch:{ all -> 0x006a }
                    return
                L_0x0011:
                    monitor-exit(r0)     // Catch:{ all -> 0x006a }
                    java.lang.String r0 = "android.media.AUDIO_BECOMING_NOISY"
                    java.lang.String r1 = r6.getAction()
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x0069
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r0 = r0.mLock
                    monitor-enter(r0)
                    r1 = 0
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r2 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0066 }
                    android.support.v4.media.AudioAttributesCompat r2 = r2.mAudioAttributes     // Catch:{ all -> 0x0066 }
                    if (r2 != 0) goto L_0x0030
                    monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                    return
                L_0x0030:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r2 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0066 }
                    android.support.v4.media.AudioAttributesCompat r2 = r2.mAudioAttributes     // Catch:{ all -> 0x0066 }
                    int r2 = r2.getUsage()     // Catch:{ all -> 0x0066 }
                    r1 = r2
                    monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                    r0 = 1
                    if (r1 == r0) goto L_0x005c
                    r0 = 14
                    if (r1 == r0) goto L_0x0044
                    goto L_0x0069
                L_0x0044:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r0 = r0.mSession
                    android.support.v4.media.BaseMediaPlayer r0 = r0.getPlayer()
                    if (r0 == 0) goto L_0x0069
                    float r2 = r0.getPlayerVolume()
                    r3 = 1045220557(0x3e4ccccd, float:0.2)
                    float r2 = r2 * r3
                    r0.setPlayerVolume(r2)
                    goto L_0x0069
                L_0x005c:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r0 = r0.mSession
                    r0.pause()
                    goto L_0x0069
                L_0x0066:
                    r2 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                    throw r2
                L_0x0069:
                    return
                L_0x006a:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x006a }
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.NoisyIntentReceiver.onReceive(android.content.Context, android.content.Intent):void");
            }
        }

        private class AudioFocusListener implements AudioManager.OnAudioFocusChangeListener {
            private float mPlayerDuckingVolume;
            private float mPlayerVolumeBeforeDucking;

            private AudioFocusListener() {
            }

            /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
                return;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onAudioFocusChange(int r7) {
                /*
                    r6 = this;
                    r0 = 1
                    if (r7 == r0) goto L_0x0093
                    switch(r7) {
                        case -3: goto L_0x003f;
                        case -2: goto L_0x0024;
                        case -1: goto L_0x0008;
                        default: goto L_0x0006;
                    }
                L_0x0006:
                    goto L_0x00ea
                L_0x0008:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r0 = r0.mSession
                    r0.pause()
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r1 = r0.mLock
                    monitor-enter(r1)
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0021 }
                    r2 = 0
                    boolean unused = r0.mResumeWhenAudioFocusGain = r2     // Catch:{ all -> 0x0021 }
                    monitor-exit(r1)     // Catch:{ all -> 0x0021 }
                    goto L_0x00ea
                L_0x0021:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x0021 }
                    throw r0
                L_0x0024:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r1 = r1.mSession
                    r1.pause()
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r1 = r1.mLock
                    monitor-enter(r1)
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r2 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x003c }
                    boolean unused = r2.mResumeWhenAudioFocusGain = r0     // Catch:{ all -> 0x003c }
                    monitor-exit(r1)     // Catch:{ all -> 0x003c }
                    goto L_0x00ea
                L_0x003c:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x003c }
                    throw r0
                L_0x003f:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r1 = r1.mLock
                    monitor-enter(r1)
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r2 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0090 }
                    android.support.v4.media.AudioAttributesCompat r2 = r2.mAudioAttributes     // Catch:{ all -> 0x0090 }
                    if (r2 != 0) goto L_0x0051
                    monitor-exit(r1)     // Catch:{ all -> 0x0090 }
                    goto L_0x00ea
                L_0x0051:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r2 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0090 }
                    android.support.v4.media.AudioAttributesCompat r2 = r2.mAudioAttributes     // Catch:{ all -> 0x0090 }
                    int r2 = r2.getContentType()     // Catch:{ all -> 0x0090 }
                    if (r2 != r0) goto L_0x0067
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0090 }
                    android.support.v4.media.MediaSession2 r0 = r0.mSession     // Catch:{ all -> 0x0090 }
                    r0.pause()     // Catch:{ all -> 0x0090 }
                    goto L_0x008e
                L_0x0067:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0090 }
                    android.support.v4.media.MediaSession2 r0 = r0.mSession     // Catch:{ all -> 0x0090 }
                    android.support.v4.media.BaseMediaPlayer r0 = r0.getPlayer()     // Catch:{ all -> 0x0090 }
                    if (r0 == 0) goto L_0x008e
                    float r2 = r0.getPlayerVolume()     // Catch:{ all -> 0x0090 }
                    r3 = 1045220557(0x3e4ccccd, float:0.2)
                    float r3 = r3 * r2
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r4 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x0090 }
                    java.lang.Object r4 = r4.mLock     // Catch:{ all -> 0x0090 }
                    monitor-enter(r4)     // Catch:{ all -> 0x0090 }
                    r6.mPlayerVolumeBeforeDucking = r2     // Catch:{ all -> 0x008b }
                    r6.mPlayerDuckingVolume = r3     // Catch:{ all -> 0x008b }
                    monitor-exit(r4)     // Catch:{ all -> 0x008b }
                    r0.setPlayerVolume(r3)     // Catch:{ all -> 0x0090 }
                    goto L_0x008e
                L_0x008b:
                    r5 = move-exception
                    monitor-exit(r4)     // Catch:{ all -> 0x008b }
                    throw r5     // Catch:{ all -> 0x0090 }
                L_0x008e:
                    monitor-exit(r1)     // Catch:{ all -> 0x0090 }
                    goto L_0x00ea
                L_0x0090:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x0090 }
                    throw r0
                L_0x0093:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r1 = r1.mSession
                    int r1 = r1.getPlayerState()
                    if (r1 != r0) goto L_0x00be
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r0 = r0.mLock
                    monitor-enter(r0)
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r1 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this     // Catch:{ all -> 0x00bb }
                    boolean r1 = r1.mResumeWhenAudioFocusGain     // Catch:{ all -> 0x00bb }
                    if (r1 != 0) goto L_0x00b0
                    monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                    goto L_0x00ea
                L_0x00b0:
                    monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r0 = r0.mSession
                    r0.play()
                    goto L_0x00ea
                L_0x00bb:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                    throw r1
                L_0x00be:
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r0 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    android.support.v4.media.MediaSession2 r0 = r0.mSession
                    android.support.v4.media.BaseMediaPlayer r0 = r0.getPlayer()
                    if (r0 == 0) goto L_0x00e9
                    float r1 = r0.getPlayerVolume()
                    android.support.v4.media.AudioFocusHandler$AudioFocusHandlerImplBase r2 = android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.this
                    java.lang.Object r2 = r2.mLock
                    monitor-enter(r2)
                    r3 = 0
                    float r4 = r6.mPlayerDuckingVolume     // Catch:{ all -> 0x00e6 }
                    int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                    if (r4 == 0) goto L_0x00de
                    monitor-exit(r2)     // Catch:{ all -> 0x00e6 }
                    goto L_0x00ea
                L_0x00de:
                    float r4 = r6.mPlayerVolumeBeforeDucking     // Catch:{ all -> 0x00e6 }
                    r3 = r4
                    monitor-exit(r2)     // Catch:{ all -> 0x00e6 }
                    r0.setPlayerVolume(r3)
                    goto L_0x00e9
                L_0x00e6:
                    r4 = move-exception
                    monitor-exit(r2)     // Catch:{ all -> 0x00e6 }
                    throw r4
                L_0x00e9:
                L_0x00ea:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.AudioFocusHandler.AudioFocusHandlerImplBase.AudioFocusListener.onAudioFocusChange(int):void");
            }
        }
    }
}
