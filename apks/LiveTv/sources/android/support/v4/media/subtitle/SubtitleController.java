package android.support.v4.media.subtitle;

import android.content.Context;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.media.subtitle.SubtitleTrack;
import android.view.accessibility.CaptioningManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

@RequiresApi(28)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class SubtitleController {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int WHAT_HIDE = 2;
    private static final int WHAT_SELECT_DEFAULT_TRACK = 4;
    private static final int WHAT_SELECT_TRACK = 3;
    private static final int WHAT_SHOW = 1;
    private Anchor mAnchor;
    private final Handler.Callback mCallback;
    private CaptioningManager.CaptioningChangeListener mCaptioningChangeListener;
    private CaptioningManager mCaptioningManager;
    private Handler mHandler;
    private Listener mListener;
    private ArrayList<Renderer> mRenderers;
    private final Object mRenderersLock;
    private SubtitleTrack mSelectedTrack;
    private boolean mShowing;
    private MediaTimeProvider mTimeProvider;
    private boolean mTrackIsExplicit;
    private ArrayList<SubtitleTrack> mTracks;
    private final Object mTracksLock;
    private boolean mVisibilityIsExplicit;

    public interface Anchor {
        Looper getSubtitleLooper();

        void setSubtitleWidget(SubtitleTrack.RenderingWidget renderingWidget);
    }

    interface Listener {
        void onSubtitleTrackSelected(SubtitleTrack subtitleTrack);
    }

    public static abstract class Renderer {
        public abstract SubtitleTrack createTrack(MediaFormat mediaFormat);

        public abstract boolean supports(MediaFormat mediaFormat);
    }

    public SubtitleController(Context context) {
        this(context, (MediaTimeProvider) null, (Listener) null);
    }

    public SubtitleController(Context context, MediaTimeProvider timeProvider, Listener listener) {
        this.mRenderersLock = new Object();
        this.mTracksLock = new Object();
        this.mCallback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        SubtitleController.this.doShow();
                        return true;
                    case 2:
                        SubtitleController.this.doHide();
                        return true;
                    case 3:
                        SubtitleController.this.doSelectTrack((SubtitleTrack) msg.obj);
                        return true;
                    case 4:
                        SubtitleController.this.doSelectDefaultTrack();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mCaptioningChangeListener = new CaptioningManager.CaptioningChangeListener() {
            public void onEnabledChanged(boolean enabled) {
                SubtitleController.this.selectDefaultTrack();
            }

            public void onLocaleChanged(Locale locale) {
                SubtitleController.this.selectDefaultTrack();
            }
        };
        this.mTrackIsExplicit = false;
        this.mVisibilityIsExplicit = false;
        this.mTimeProvider = timeProvider;
        this.mListener = listener;
        this.mRenderers = new ArrayList<>();
        this.mShowing = false;
        this.mTracks = new ArrayList<>();
        this.mCaptioningManager = (CaptioningManager) context.getSystemService("captioning");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
        super.finalize();
    }

    public SubtitleTrack[] getTracks() {
        SubtitleTrack[] tracks;
        synchronized (this.mTracksLock) {
            tracks = new SubtitleTrack[this.mTracks.size()];
            this.mTracks.toArray(tracks);
        }
        return tracks;
    }

    public SubtitleTrack getSelectedTrack() {
        return this.mSelectedTrack;
    }

    private SubtitleTrack.RenderingWidget getRenderingWidget() {
        if (this.mSelectedTrack == null) {
            return null;
        }
        return this.mSelectedTrack.getRenderingWidget();
    }

    public boolean selectTrack(SubtitleTrack track) {
        if (track != null && !this.mTracks.contains(track)) {
            return false;
        }
        processOnAnchor(this.mHandler.obtainMessage(3, track));
        return true;
    }

    /* access modifiers changed from: private */
    public void doSelectTrack(SubtitleTrack track) {
        this.mTrackIsExplicit = true;
        if (this.mSelectedTrack != track) {
            if (this.mSelectedTrack != null) {
                this.mSelectedTrack.hide();
                this.mSelectedTrack.setTimeProvider((MediaTimeProvider) null);
            }
            this.mSelectedTrack = track;
            if (this.mAnchor != null) {
                this.mAnchor.setSubtitleWidget(getRenderingWidget());
            }
            if (this.mSelectedTrack != null) {
                this.mSelectedTrack.setTimeProvider(this.mTimeProvider);
                this.mSelectedTrack.show();
            }
            if (this.mListener != null) {
                this.mListener.onSubtitleTrackSelected(track);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.support.v4.media.subtitle.SubtitleTrack getDefaultTrack() {
        /*
            r18 = this;
            r1 = r18
            r2 = 0
            r3 = -1
            android.view.accessibility.CaptioningManager r0 = r1.mCaptioningManager
            java.util.Locale r4 = r0.getLocale()
            r0 = r4
            if (r0 != 0) goto L_0x0011
            java.util.Locale r0 = java.util.Locale.getDefault()
        L_0x0011:
            r5 = r0
            android.view.accessibility.CaptioningManager r0 = r1.mCaptioningManager
            boolean r0 = r0.isEnabled()
            r6 = 1
            r0 = r0 ^ r6
            r7 = r0
            java.lang.Object r8 = r1.mTracksLock
            monitor-enter(r8)
            java.util.ArrayList<android.support.v4.media.subtitle.SubtitleTrack> r0 = r1.mTracks     // Catch:{ all -> 0x00c0 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x00c0 }
        L_0x0024:
            boolean r9 = r0.hasNext()     // Catch:{ all -> 0x00c0 }
            if (r9 == 0) goto L_0x00be
            java.lang.Object r9 = r0.next()     // Catch:{ all -> 0x00c0 }
            android.support.v4.media.subtitle.SubtitleTrack r9 = (android.support.v4.media.subtitle.SubtitleTrack) r9     // Catch:{ all -> 0x00c0 }
            android.media.MediaFormat r10 = r9.getFormat()     // Catch:{ all -> 0x00c0 }
            java.lang.String r11 = "language"
            java.lang.String r11 = r10.getString(r11)     // Catch:{ all -> 0x00c0 }
            java.lang.String r12 = "is-forced-subtitle"
            r13 = 0
            int r12 = android.support.v4.media.subtitle.SubtitleController.MediaFormatUtil.getInteger(r10, r12, r13)     // Catch:{ all -> 0x00c0 }
            if (r12 == 0) goto L_0x0045
            r12 = r6
            goto L_0x0046
        L_0x0045:
            r12 = r13
        L_0x0046:
            java.lang.String r14 = "is-autoselect"
            int r14 = android.support.v4.media.subtitle.SubtitleController.MediaFormatUtil.getInteger(r10, r14, r6)     // Catch:{ all -> 0x00c0 }
            if (r14 == 0) goto L_0x0050
            r14 = r6
            goto L_0x0051
        L_0x0050:
            r14 = r13
        L_0x0051:
            java.lang.String r15 = "is-default"
            int r15 = android.support.v4.media.subtitle.SubtitleController.MediaFormatUtil.getInteger(r10, r15, r13)     // Catch:{ all -> 0x00c0 }
            if (r15 == 0) goto L_0x005b
            r15 = r6
            goto L_0x005c
        L_0x005b:
            r15 = r13
        L_0x005c:
            if (r5 == 0) goto L_0x0081
            java.lang.String r6 = r5.getLanguage()     // Catch:{ all -> 0x00c0 }
            java.lang.String r13 = ""
            boolean r6 = r6.equals(r13)     // Catch:{ all -> 0x00c0 }
            if (r6 != 0) goto L_0x0081
            java.lang.String r6 = r5.getISO3Language()     // Catch:{ all -> 0x00c0 }
            boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x00c0 }
            if (r6 != 0) goto L_0x0081
            java.lang.String r6 = r5.getLanguage()     // Catch:{ all -> 0x00c0 }
            boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x00c0 }
            if (r6 == 0) goto L_0x007f
            goto L_0x0081
        L_0x007f:
            r6 = 0
            goto L_0x0082
        L_0x0081:
            r6 = 1
        L_0x0082:
            if (r12 == 0) goto L_0x0086
            r13 = 0
            goto L_0x0088
        L_0x0086:
            r13 = 8
        L_0x0088:
            if (r4 != 0) goto L_0x008f
            if (r15 == 0) goto L_0x008f
            r17 = 4
            goto L_0x0091
        L_0x008f:
            r17 = 0
        L_0x0091:
            int r13 = r13 + r17
            if (r14 == 0) goto L_0x0098
            r17 = 0
            goto L_0x009a
        L_0x0098:
            r17 = 2
        L_0x009a:
            int r13 = r13 + r17
            if (r6 == 0) goto L_0x00a1
            r16 = 1
            goto L_0x00a3
        L_0x00a1:
            r16 = 0
        L_0x00a3:
            int r13 = r13 + r16
            if (r7 == 0) goto L_0x00ad
            if (r12 != 0) goto L_0x00ad
        L_0x00aa:
            r6 = 1
            goto L_0x0024
        L_0x00ad:
            if (r4 != 0) goto L_0x00b1
            if (r15 != 0) goto L_0x00b9
        L_0x00b1:
            if (r6 == 0) goto L_0x00bd
            if (r14 != 0) goto L_0x00b9
            if (r12 != 0) goto L_0x00b9
            if (r4 == 0) goto L_0x00bd
        L_0x00b9:
            if (r13 <= r3) goto L_0x00bd
            r3 = r13
            r2 = r9
        L_0x00bd:
            goto L_0x00aa
        L_0x00be:
            monitor-exit(r8)     // Catch:{ all -> 0x00c0 }
            return r2
        L_0x00c0:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00c0 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.subtitle.SubtitleController.getDefaultTrack():android.support.v4.media.subtitle.SubtitleTrack");
    }

    static class MediaFormatUtil {
        MediaFormatUtil() {
        }

        static int getInteger(MediaFormat format, String name, int defaultValue) {
            try {
                return format.getInteger(name);
            } catch (ClassCastException | NullPointerException e) {
                return defaultValue;
            }
        }
    }

    public void selectDefaultTrack() {
        processOnAnchor(this.mHandler.obtainMessage(4));
    }

    /* access modifiers changed from: private */
    public void doSelectDefaultTrack() {
        if (this.mTrackIsExplicit) {
            if (!this.mVisibilityIsExplicit) {
                if (this.mCaptioningManager.isEnabled() || !(this.mSelectedTrack == null || MediaFormatUtil.getInteger(this.mSelectedTrack.getFormat(), "is-forced-subtitle", 0) == 0)) {
                    show();
                } else if (this.mSelectedTrack != null && this.mSelectedTrack.getTrackType() == 4) {
                    hide();
                }
                this.mVisibilityIsExplicit = false;
            } else {
                return;
            }
        }
        SubtitleTrack track = getDefaultTrack();
        if (track != null) {
            selectTrack(track);
            this.mTrackIsExplicit = false;
            if (!this.mVisibilityIsExplicit) {
                show();
                this.mVisibilityIsExplicit = false;
            }
        }
    }

    public void reset() {
        checkAnchorLooper();
        hide();
        selectTrack((SubtitleTrack) null);
        this.mTracks.clear();
        this.mTrackIsExplicit = false;
        this.mVisibilityIsExplicit = false;
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
    }

    public SubtitleTrack addTrack(MediaFormat format) {
        SubtitleTrack track;
        synchronized (this.mRenderersLock) {
            Iterator<Renderer> it = this.mRenderers.iterator();
            while (it.hasNext()) {
                Renderer renderer = it.next();
                if (renderer.supports(format) && (track = renderer.createTrack(format)) != null) {
                    synchronized (this.mTracksLock) {
                        if (this.mTracks.size() == 0) {
                            this.mCaptioningManager.addCaptioningChangeListener(this.mCaptioningChangeListener);
                        }
                        this.mTracks.add(track);
                    }
                    return track;
                }
            }
            return null;
        }
    }

    public void show() {
        processOnAnchor(this.mHandler.obtainMessage(1));
    }

    /* access modifiers changed from: private */
    public void doShow() {
        this.mShowing = true;
        this.mVisibilityIsExplicit = true;
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.show();
        }
    }

    public void hide() {
        processOnAnchor(this.mHandler.obtainMessage(2));
    }

    /* access modifiers changed from: private */
    public void doHide() {
        this.mVisibilityIsExplicit = true;
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.hide();
        }
        this.mShowing = false;
    }

    public void registerRenderer(Renderer renderer) {
        synchronized (this.mRenderersLock) {
            if (!this.mRenderers.contains(renderer)) {
                this.mRenderers.add(renderer);
            }
        }
    }

    public boolean hasRendererFor(MediaFormat format) {
        synchronized (this.mRenderersLock) {
            Iterator<Renderer> it = this.mRenderers.iterator();
            while (it.hasNext()) {
                if (it.next().supports(format)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setAnchor(Anchor anchor) {
        if (this.mAnchor != anchor) {
            if (this.mAnchor != null) {
                checkAnchorLooper();
                this.mAnchor.setSubtitleWidget((SubtitleTrack.RenderingWidget) null);
            }
            this.mAnchor = anchor;
            this.mHandler = null;
            if (this.mAnchor != null) {
                this.mHandler = new Handler(this.mAnchor.getSubtitleLooper(), this.mCallback);
                checkAnchorLooper();
                this.mAnchor.setSubtitleWidget(getRenderingWidget());
            }
        }
    }

    private void checkAnchorLooper() {
    }

    private void processOnAnchor(Message m) {
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            this.mHandler.dispatchMessage(m);
        } else {
            this.mHandler.sendMessage(m);
        }
    }
}
