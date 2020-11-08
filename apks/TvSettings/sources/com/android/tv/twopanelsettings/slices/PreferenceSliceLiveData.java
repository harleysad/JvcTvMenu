package com.android.tv.twopanelsettings.slices;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import androidx.slice.Slice;
import androidx.slice.SliceViewManager;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiresApi(19)
public final class PreferenceSliceLiveData {
    private static final String TAG = "SliceLiveData";

    @NonNull
    static SliceLiveDataImpl fromUri(@NonNull Context context, @NonNull Uri uri) {
        return new SliceLiveDataImpl(context.getApplicationContext(), uri);
    }

    static class SliceLiveDataImpl extends MutableLiveData<Slice> {
        final Intent mIntent;
        final SliceViewManager.SliceCallback mSliceCallback = new SliceViewManager.SliceCallback() {
            public void onSliceUpdated(@NonNull Slice s) {
                SliceLiveDataImpl.this.postValue(s);
            }
        };
        final SliceViewManager mSliceViewManager;
        final AtomicBoolean mUpdatePending = new AtomicBoolean(false);
        private final Runnable mUpdateSlice = new Runnable() {
            public void run() {
                Slice s;
                try {
                    if (SliceLiveDataImpl.this.mUri != null) {
                        s = SliceLiveDataImpl.this.mSliceViewManager.bindSlice(SliceLiveDataImpl.this.mUri);
                    } else {
                        s = SliceLiveDataImpl.this.mSliceViewManager.bindSlice(SliceLiveDataImpl.this.mIntent);
                    }
                    if (SliceLiveDataImpl.this.mUri == null && s != null) {
                        SliceLiveDataImpl.this.mUri = s.getUri();
                        SliceLiveDataImpl.this.mSliceViewManager.registerSliceCallback(SliceLiveDataImpl.this.mUri, SliceLiveDataImpl.this.mSliceCallback);
                    }
                    SliceLiveDataImpl.this.postValue(s);
                } catch (Exception e) {
                    Log.e(PreferenceSliceLiveData.TAG, "Error binding slice", e);
                    SliceLiveDataImpl.this.postValue(null);
                }
            }
        };
        Uri mUri;

        SliceLiveDataImpl(Context context, Uri uri) {
            this.mSliceViewManager = SliceViewManager.getInstance(context);
            this.mUri = uri;
            this.mIntent = null;
        }

        /* access modifiers changed from: protected */
        public void onActive() {
            AsyncTask.execute(this.mUpdateSlice);
            if (this.mUri != null) {
                this.mSliceViewManager.registerSliceCallback(this.mUri, this.mSliceCallback);
            }
        }

        /* access modifiers changed from: protected */
        public void onInactive() {
            if (this.mUri != null) {
                this.mSliceViewManager.unregisterSliceCallback(this.mUri, this.mSliceCallback);
            }
        }

        @MainThread
        public void setValue(Slice slice) {
            this.mUpdatePending.set(true);
            super.setValue(slice);
        }
    }

    private PreferenceSliceLiveData() {
    }
}
