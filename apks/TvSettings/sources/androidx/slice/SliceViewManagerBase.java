package androidx.slice;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.ArrayMap;
import android.util.Pair;
import androidx.slice.SliceViewManager;
import androidx.slice.widget.SliceLiveData;
import java.util.concurrent.Executor;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public abstract class SliceViewManagerBase extends SliceViewManager {
    protected final Context mContext;
    private final ArrayMap<Pair<Uri, SliceViewManager.SliceCallback>, SliceListenerImpl> mListenerLookup = new ArrayMap<>();

    SliceViewManagerBase(Context context) {
        this.mContext = context;
    }

    public void registerSliceCallback(@NonNull Uri uri, @NonNull SliceViewManager.SliceCallback callback) {
        final Handler h = new Handler(Looper.getMainLooper());
        registerSliceCallback(uri, new Executor() {
            public void execute(@NonNull Runnable command) {
                h.post(command);
            }
        }, callback);
    }

    public void registerSliceCallback(@NonNull Uri uri, @NonNull Executor executor, @NonNull SliceViewManager.SliceCallback callback) {
        getListener(uri, callback, new SliceListenerImpl(uri, executor, callback)).startListening();
    }

    public void unregisterSliceCallback(@NonNull Uri uri, @NonNull SliceViewManager.SliceCallback callback) {
        synchronized (this.mListenerLookup) {
            SliceListenerImpl impl = this.mListenerLookup.remove(new Pair(uri, callback));
            if (impl != null) {
                impl.stopListening();
            }
        }
    }

    private SliceListenerImpl getListener(Uri uri, SliceViewManager.SliceCallback callback, SliceListenerImpl listener) {
        Pair<Uri, SliceViewManager.SliceCallback> key = new Pair<>(uri, callback);
        synchronized (this.mListenerLookup) {
            if (this.mListenerLookup.containsKey(key)) {
                this.mListenerLookup.get(key).stopListening();
            }
            this.mListenerLookup.put(key, listener);
        }
        return listener;
    }

    private class SliceListenerImpl {
        /* access modifiers changed from: private */
        public final SliceViewManager.SliceCallback mCallback;
        /* access modifiers changed from: private */
        public final Executor mExecutor;
        private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                AsyncTask.execute(SliceListenerImpl.this.mUpdateSlice);
            }
        };
        private boolean mPinned;
        /* access modifiers changed from: private */
        public final Runnable mUpdateSlice = new Runnable() {
            public void run() {
                SliceListenerImpl.this.tryPin();
                final Slice s = Slice.bindSlice(SliceViewManagerBase.this.mContext, SliceListenerImpl.this.mUri, SliceLiveData.SUPPORTED_SPECS);
                SliceListenerImpl.this.mExecutor.execute(new Runnable() {
                    public void run() {
                        SliceListenerImpl.this.mCallback.onSliceUpdated(s);
                    }
                });
            }
        };
        /* access modifiers changed from: private */
        public Uri mUri;

        SliceListenerImpl(Uri uri, Executor executor, SliceViewManager.SliceCallback callback) {
            this.mUri = uri;
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        /* access modifiers changed from: package-private */
        public void startListening() {
            SliceViewManagerBase.this.mContext.getContentResolver().registerContentObserver(this.mUri, true, this.mObserver);
            tryPin();
        }

        /* access modifiers changed from: private */
        public void tryPin() {
            if (!this.mPinned) {
                try {
                    SliceViewManagerBase.this.pinSlice(this.mUri);
                    this.mPinned = true;
                } catch (SecurityException e) {
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void stopListening() {
            SliceViewManagerBase.this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
            if (this.mPinned) {
                SliceViewManagerBase.this.unpinSlice(this.mUri);
                this.mPinned = false;
            }
        }
    }
}
