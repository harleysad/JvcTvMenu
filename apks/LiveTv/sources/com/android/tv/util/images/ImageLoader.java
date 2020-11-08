package com.android.tv.util.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.ArraySet;
import android.util.Log;
import com.android.tv.common.concurrent.NamedThreadFactory;
import com.android.tv.util.images.BitmapUtils;
import com.android.tv.util.images.ImageLoader;
import com.mediatek.wwtv.tvcenter.R;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ImageLoader {
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final boolean DEBUG = false;
    private static final Executor IMAGE_THREAD_POOL_EXECUTOR;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    private static final String TAG = "ImageLoader";
    private static Handler sMainHandler;
    /* access modifiers changed from: private */
    public static final Map<String, LoadBitmapTask> sPendingListMap = new HashMap();
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(128);
    private static final ThreadFactory sThreadFactory = new NamedThreadFactory(TAG);

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        IMAGE_THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    @UiThread
    public static abstract class ImageLoaderCallback<T> {
        private final WeakReference<T> mWeakReference;

        public abstract void onBitmapLoaded(T t, @Nullable Bitmap bitmap);

        public ImageLoaderCallback(T referent) {
            this.mWeakReference = new WeakReference<>(referent);
        }

        /* access modifiers changed from: private */
        public void onBitmapLoaded(@Nullable Bitmap bitmap) {
            T referent = this.mWeakReference.get();
            if (referent != null) {
                onBitmapLoaded(referent, bitmap);
            }
        }
    }

    public static void prefetchBitmap(Context context, String uriString, int maxWidth, int maxHeight) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            doLoadBitmap(context, uriString, maxWidth, maxHeight, (ImageLoaderCallback) null, AsyncTask.SERIAL_EXECUTOR);
            return;
        }
        getMainHandler().post(new Runnable(context.getApplicationContext(), uriString, maxWidth, maxHeight) {
            private final /* synthetic */ Context f$0;
            private final /* synthetic */ String f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ImageLoader.doLoadBitmap(this.f$0, this.f$1, this.f$2, this.f$3, (ImageLoader.ImageLoaderCallback) null, AsyncTask.SERIAL_EXECUTOR);
            }
        });
    }

    @UiThread
    public static boolean loadBitmap(Context context, String uriString, ImageLoaderCallback callback) {
        return loadBitmap(context, uriString, Integer.MAX_VALUE, Integer.MAX_VALUE, callback);
    }

    @UiThread
    public static boolean loadBitmap(Context context, String uriString, int maxWidth, int maxHeight, ImageLoaderCallback callback) {
        return doLoadBitmap(context, uriString, maxWidth, maxHeight, callback, IMAGE_THREAD_POOL_EXECUTOR);
    }

    /* access modifiers changed from: private */
    public static boolean doLoadBitmap(Context context, String uriString, int maxWidth, int maxHeight, ImageLoaderCallback callback, Executor executor) {
        ImageCache imageCache = ImageCache.getInstance();
        BitmapUtils.ScaledBitmapInfo bitmapInfo = imageCache.get(uriString);
        if (bitmapInfo == null || bitmapInfo.needToReload(maxWidth, maxHeight)) {
            return doLoadBitmap(callback, executor, new LoadBitmapFromUriTask(context, imageCache, uriString, maxWidth, maxHeight));
        }
        if (callback == null) {
            return true;
        }
        callback.onBitmapLoaded(bitmapInfo.bitmap);
        return true;
    }

    @UiThread
    public static boolean loadBitmap(ImageLoaderCallback callback, LoadBitmapTask loadBitmapTask) {
        return doLoadBitmap(callback, IMAGE_THREAD_POOL_EXECUTOR, loadBitmapTask);
    }

    @UiThread
    private static boolean doLoadBitmap(ImageLoaderCallback callback, Executor executor, LoadBitmapTask loadBitmapTask) {
        BitmapUtils.ScaledBitmapInfo bitmapInfo = loadBitmapTask.getFromCache();
        boolean needToReload = loadBitmapTask.isReloadNeeded();
        if (bitmapInfo == null || needToReload) {
            LoadBitmapTask existingTask = sPendingListMap.get(loadBitmapTask.getKey());
            if (existingTask == null || loadBitmapTask.isReloadNeeded(existingTask)) {
                if (callback != null) {
                    loadBitmapTask.mCallbacks.add(callback);
                }
                sPendingListMap.put(loadBitmapTask.getKey(), loadBitmapTask);
                try {
                    loadBitmapTask.executeOnExecutor(executor, new Void[0]);
                } catch (RejectedExecutionException e) {
                    Log.e(TAG, "Failed to create new image loader", e);
                    sPendingListMap.remove(loadBitmapTask.getKey());
                }
            } else if (callback != null) {
                existingTask.mCallbacks.add(callback);
            }
            return false;
        } else if (callback == null) {
            return true;
        } else {
            callback.onBitmapLoaded(bitmapInfo.bitmap);
            return true;
        }
    }

    public static abstract class LoadBitmapTask extends AsyncTask<Void, Void, BitmapUtils.ScaledBitmapInfo> {
        protected final Context mAppContext;
        /* access modifiers changed from: private */
        public final Set<ImageLoaderCallback> mCallbacks = new ArraySet();
        private final ImageCache mImageCache;
        private final String mKey;
        protected final int mMaxHeight;
        protected final int mMaxWidth;

        @Nullable
        @WorkerThread
        public abstract BitmapUtils.ScaledBitmapInfo doGetBitmapInBackground();

        /* access modifiers changed from: private */
        public boolean isReloadNeeded() {
            BitmapUtils.ScaledBitmapInfo bitmapInfo = getFromCache();
            return bitmapInfo != null && bitmapInfo.needToReload(this.mMaxWidth, this.mMaxHeight);
        }

        /* access modifiers changed from: private */
        public boolean isReloadNeeded(LoadBitmapTask other) {
            return (other.mMaxHeight != Integer.MAX_VALUE && this.mMaxHeight >= other.mMaxHeight * 2) || (other.mMaxWidth != Integer.MAX_VALUE && this.mMaxWidth >= other.mMaxWidth * 2);
        }

        @Nullable
        public final BitmapUtils.ScaledBitmapInfo getFromCache() {
            return this.mImageCache.get(this.mKey);
        }

        public LoadBitmapTask(Context context, ImageCache imageCache, String key, int maxHeight, int maxWidth) {
            if (maxWidth == 0 || maxHeight == 0) {
                throw new IllegalArgumentException("Image size should not be 0. {width=" + maxWidth + ", height=" + maxHeight + "}");
            }
            this.mAppContext = context.getApplicationContext();
            this.mKey = key;
            this.mImageCache = imageCache;
            this.mMaxHeight = maxHeight;
            this.mMaxWidth = maxWidth;
        }

        @Nullable
        public final BitmapUtils.ScaledBitmapInfo doInBackground(Void... params) {
            BitmapUtils.ScaledBitmapInfo bitmapInfo = getFromCache();
            if (bitmapInfo != null && !isReloadNeeded()) {
                return bitmapInfo;
            }
            BitmapUtils.ScaledBitmapInfo bitmapInfo2 = doGetBitmapInBackground();
            if (bitmapInfo2 != null) {
                this.mImageCache.putIfNeeded(bitmapInfo2);
            }
            return bitmapInfo2;
        }

        public final void onPostExecute(BitmapUtils.ScaledBitmapInfo scaledBitmapInfo) {
            for (ImageLoaderCallback callback : this.mCallbacks) {
                callback.onBitmapLoaded(scaledBitmapInfo == null ? null : scaledBitmapInfo.bitmap);
            }
            ImageLoader.sPendingListMap.remove(this.mKey);
        }

        public final String getKey() {
            return this.mKey;
        }

        public String toString() {
            return getClass().getSimpleName() + "(" + this.mKey + " " + this.mMaxWidth + "x" + this.mMaxHeight + ")";
        }
    }

    private static final class LoadBitmapFromUriTask extends LoadBitmapTask {
        private LoadBitmapFromUriTask(Context context, ImageCache imageCache, String uriString, int maxWidth, int maxHeight) {
            super(context, imageCache, uriString, maxHeight, maxWidth);
        }

        @Nullable
        public final BitmapUtils.ScaledBitmapInfo doGetBitmapInBackground() {
            return BitmapUtils.decodeSampledBitmapFromUriString(this.mAppContext, getKey(), this.mMaxWidth, this.mMaxHeight);
        }
    }

    public static final class LoadTvInputLogoTask extends LoadBitmapTask {
        private final TvInputInfo mInfo;

        public LoadTvInputLogoTask(Context context, ImageCache cache, TvInputInfo info) {
            super(context, cache, getTvInputLogoKey(info.getId()), context.getResources().getDimensionPixelSize(R.dimen.channel_banner_input_logo_size), context.getResources().getDimensionPixelSize(R.dimen.channel_banner_input_logo_size));
            this.mInfo = info;
        }

        @Nullable
        public BitmapUtils.ScaledBitmapInfo doGetBitmapInBackground() {
            Bitmap original;
            Drawable drawable = this.mInfo.loadIcon(this.mAppContext);
            if ((drawable instanceof BitmapDrawable) && (original = ((BitmapDrawable) drawable).getBitmap()) != null) {
                return BitmapUtils.createScaledBitmapInfo(getKey(), original, this.mMaxWidth, this.mMaxHeight);
            }
            return null;
        }

        public static String getTvInputLogoKey(String inputId) {
            return inputId + "-logo";
        }
    }

    private static synchronized Handler getMainHandler() {
        Handler handler;
        synchronized (ImageLoader.class) {
            if (sMainHandler == null) {
                sMainHandler = new Handler(Looper.getMainLooper());
            }
            handler = sMainHandler;
        }
        return handler;
    }

    private ImageLoader() {
    }
}
