package com.mediatek.wwtv.setting.widget.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;
import com.mediatek.wwtv.setting.util.AccountImageChangeObserver;
import com.mediatek.wwtv.setting.util.UriUtils;
import com.mediatek.wwtv.tvcenter.R;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DrawableDownloader {
    private static final Executor BITMAP_DOWNLOADER_THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(5);
    private static final Executor BITMAP_RESOURCE_DOWNLOADER_THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(1);
    private static final int CACHE_HARD_LIMIT = 32;
    private static final int CORE_POOL_SIZE = 5;
    private static final int CORE_RESOURCE_POOL_SIZE = 1;
    private static final boolean DEBUG = false;
    private static final int MEM_TO_CACHE = 4;
    private static final String TAG = "DrawableDownloader";
    private static DrawableDownloader sBitmapDownloader;
    private static final Object sBitmapDownloaderLock = new Object();
    private Context mContext;
    private LruCache<String, BitmapItem> mMemoryCache;
    private RecycleBitmapPool mRecycledBitmaps;

    public static abstract class BitmapCallback {
        SoftReference<DrawableLoader> mTask;

        public abstract void onBitmapRetrieved(Drawable drawable);
    }

    private static class BitmapItem {
        ArrayList<BitmapDrawable> mBitmaps = new ArrayList<>(3);
        int mByteCount;
        int mOriginalHeight;
        int mOriginalWidth;

        public BitmapItem(int originalWidth, int originalHeight) {
            this.mOriginalWidth = originalWidth;
            this.mOriginalHeight = originalHeight;
        }

        /* access modifiers changed from: package-private */
        public BitmapDrawable findDrawable(BitmapWorkerOptions options) {
            int c = this.mBitmaps.size();
            for (int i = 0; i < c; i++) {
                BitmapDrawable d = this.mBitmaps.get(i);
                if (d.getIntrinsicWidth() == this.mOriginalWidth && d.getIntrinsicHeight() == this.mOriginalHeight) {
                    return d;
                }
                if (options.getHeight() != 2048) {
                    if (options.getHeight() <= d.getIntrinsicHeight()) {
                        return d;
                    }
                } else if (options.getWidth() != 2048 && options.getWidth() <= d.getIntrinsicWidth()) {
                    return d;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public BitmapDrawable findLargestDrawable(BitmapWorkerOptions options) {
            if (this.mBitmaps.size() == 0) {
                return null;
            }
            return this.mBitmaps.get(0);
        }

        /* access modifiers changed from: package-private */
        public void addDrawable(BitmapDrawable d) {
            int i = 0;
            int c = this.mBitmaps.size();
            while (i < c && this.mBitmaps.get(i).getIntrinsicHeight() >= d.getIntrinsicHeight()) {
                i++;
            }
            this.mBitmaps.add(i, d);
            this.mByteCount += RecycleBitmapPool.getSize(d.getBitmap());
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            int c = this.mBitmaps.size();
            for (int i = 0; i < c; i++) {
                BitmapDrawable d = this.mBitmaps.get(i);
                if (d instanceof RefcountBitmapDrawable) {
                    ((RefcountBitmapDrawable) d).getRefcountObject().releaseRef();
                }
            }
            this.mBitmaps.clear();
            this.mByteCount = 0;
        }
    }

    public static final DrawableDownloader getInstance(Context context) {
        if (sBitmapDownloader == null) {
            sBitmapDownloader = new DrawableDownloader(context);
        }
        return sBitmapDownloader;
    }

    private static String getBucketKey(String baseKey, Bitmap.Config bitmapConfig) {
        StringBuilder sb = new StringBuilder(baseKey.length() + 16);
        sb.append(baseKey);
        sb.append(":");
        sb.append(bitmapConfig == null ? "" : Integer.valueOf(bitmapConfig.ordinal()));
        return sb.toString();
    }

    public static Drawable getDrawable(Context context, Intent.ShortcutIconResource iconResource) throws PackageManager.NameNotFoundException {
        return DrawableLoader.getDrawable(context, iconResource);
    }

    private DrawableDownloader(Context context) {
        this.mContext = context;
        int memClass = ((ActivityManager) context.getSystemService("activity")).getMemoryClass() / 4;
        this.mMemoryCache = new LruCache<String, BitmapItem>(1048576 * (memClass > 32 ? 32 : memClass)) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapItem bitmap) {
                return bitmap.mByteCount;
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, BitmapItem oldValue, BitmapItem newValue) {
                if (evicted) {
                    oldValue.clear();
                }
            }
        };
        this.mRecycledBitmaps = new RecycleBitmapPool();
    }

    public void trimTo(float amount) {
        if (amount == 0.0f) {
            this.mMemoryCache.evictAll();
        } else {
            this.mMemoryCache.trimToSize((int) (((float) this.mMemoryCache.maxSize()) * amount));
        }
    }

    @Deprecated
    public final Drawable loadBitmapBlocking(BitmapWorkerOptions options) {
        final boolean hasAccountImageUri = UriUtils.isAccountImageUri(options.getResourceUri());
        Drawable bitmap = null;
        if (hasAccountImageUri) {
            AccountImageChangeObserver.getInstance().registerChangeUriIfPresent(options);
        } else {
            bitmap = getBitmapFromMemCache(options);
        }
        if (bitmap != null) {
            return bitmap;
        }
        return new DrawableLoader((ImageView) null, this.mRecycledBitmaps) {
            /* access modifiers changed from: protected */
            public Drawable doInBackground(BitmapWorkerOptions... params) {
                Drawable bitmap = super.doInBackground(params);
                if (bitmap != null && !hasAccountImageUri) {
                    DrawableDownloader.this.addBitmapToMemoryCache(params[0], bitmap, this);
                }
                return bitmap;
            }
        }.doInBackground(options);
    }

    public void loadBitmap(BitmapWorkerOptions options, ImageView imageView) {
        cancelDownload(imageView);
        final boolean hasAccountImageUri = UriUtils.isAccountImageUri(options.getResourceUri());
        Drawable bitmap = null;
        if (hasAccountImageUri) {
            AccountImageChangeObserver.getInstance().registerChangeUriIfPresent(options);
        } else {
            bitmap = getBitmapFromMemCache(options);
        }
        if (bitmap != null) {
            imageView.setImageDrawable(bitmap);
            return;
        }
        DrawableLoader task = new DrawableLoader(imageView, this.mRecycledBitmaps) {
            /* access modifiers changed from: protected */
            public Drawable doInBackground(BitmapWorkerOptions... params) {
                Drawable bitmap = super.doInBackground(params);
                if (bitmap != null && !hasAccountImageUri) {
                    DrawableDownloader.this.addBitmapToMemoryCache(params[0], bitmap, this);
                }
                return bitmap;
            }
        };
        imageView.setTag(R.id.imageDownloadTask, new SoftReference(task));
        scheduleTask(task, options);
    }

    public void getBitmap(BitmapWorkerOptions options, BitmapCallback callback) {
        cancelDownload(callback);
        boolean hasAccountImageUri = UriUtils.isAccountImageUri(options.getResourceUri());
        Drawable bitmap = hasAccountImageUri ? null : getBitmapFromMemCache(options);
        if (hasAccountImageUri) {
            AccountImageChangeObserver.getInstance().registerChangeUriIfPresent(options);
        }
        if (bitmap != null) {
            callback.onBitmapRetrieved(bitmap);
            return;
        }
        final boolean z = hasAccountImageUri;
        final BitmapCallback bitmapCallback = callback;
        DrawableLoader task = new DrawableLoader((ImageView) null, this.mRecycledBitmaps) {
            /* access modifiers changed from: protected */
            public Drawable doInBackground(BitmapWorkerOptions... params) {
                Drawable bitmap = super.doInBackground(params);
                if (bitmap != null && !z) {
                    DrawableDownloader.this.addBitmapToMemoryCache(params[0], bitmap, this);
                }
                return bitmap;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Drawable bitmap) {
                bitmapCallback.onBitmapRetrieved(bitmap);
            }
        };
        callback.mTask = new SoftReference<>(task);
        scheduleTask(task, options);
    }

    private static void scheduleTask(DrawableLoader task, BitmapWorkerOptions options) {
        if (options.isFromResource()) {
            task.executeOnExecutor(BITMAP_RESOURCE_DOWNLOADER_THREAD_POOL_EXECUTOR, new BitmapWorkerOptions[]{options});
            return;
        }
        task.executeOnExecutor(BITMAP_DOWNLOADER_THREAD_POOL_EXECUTOR, new BitmapWorkerOptions[]{options});
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: com.mediatek.wwtv.setting.widget.view.DrawableLoader} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean cancelDownload(java.lang.Object r5) {
        /*
            r4 = this;
            r0 = 0
            boolean r1 = r5 instanceof android.widget.ImageView
            if (r1 == 0) goto L_0x001e
            r1 = r5
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            r2 = 2131362369(0x7f0a0241, float:1.8344517E38)
            java.lang.Object r2 = r1.getTag(r2)
            java.lang.ref.SoftReference r2 = (java.lang.ref.SoftReference) r2
            if (r2 == 0) goto L_0x001d
            java.lang.Object r3 = r2.get()
            r0 = r3
            com.mediatek.wwtv.setting.widget.view.DrawableLoader r0 = (com.mediatek.wwtv.setting.widget.view.DrawableLoader) r0
            r2.clear()
        L_0x001d:
            goto L_0x0035
        L_0x001e:
            boolean r1 = r5 instanceof com.mediatek.wwtv.setting.widget.view.DrawableDownloader.BitmapCallback
            if (r1 == 0) goto L_0x0035
            r1 = r5
            com.mediatek.wwtv.setting.widget.view.DrawableDownloader$BitmapCallback r1 = (com.mediatek.wwtv.setting.widget.view.DrawableDownloader.BitmapCallback) r1
            java.lang.ref.SoftReference<com.mediatek.wwtv.setting.widget.view.DrawableLoader> r2 = r1.mTask
            if (r2 == 0) goto L_0x0035
            java.lang.ref.SoftReference<com.mediatek.wwtv.setting.widget.view.DrawableLoader> r2 = r1.mTask
            java.lang.Object r2 = r2.get()
            r0 = r2
            com.mediatek.wwtv.setting.widget.view.DrawableLoader r0 = (com.mediatek.wwtv.setting.widget.view.DrawableLoader) r0
            r2 = 0
            r1.mTask = r2
        L_0x0035:
            if (r0 == 0) goto L_0x003d
            r1 = 1
            boolean r1 = r0.cancel(r1)
            return r1
        L_0x003d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.DrawableDownloader.cancelDownload(java.lang.Object):boolean");
    }

    /* access modifiers changed from: private */
    public void addBitmapToMemoryCache(BitmapWorkerOptions key, Drawable bitmap, DrawableLoader loader) {
        if (key.isMemCacheEnabled() && (bitmap instanceof BitmapDrawable)) {
            String bucketKey = getBucketKey(key.getCacheKey(), key.getBitmapConfig());
            BitmapItem bitmapItem = this.mMemoryCache.get(bucketKey);
            if (bitmapItem != null) {
                this.mMemoryCache.remove(bucketKey);
            } else {
                bitmapItem = new BitmapItem(loader.getOriginalWidth(), loader.getOriginalHeight());
            }
            if (bitmap instanceof RefcountBitmapDrawable) {
                ((RefcountBitmapDrawable) bitmap).getRefcountObject().addRef();
            }
            bitmapItem.addDrawable((BitmapDrawable) bitmap);
            this.mMemoryCache.put(bucketKey, bitmapItem);
        }
    }

    private Drawable getBitmapFromMemCache(BitmapWorkerOptions key) {
        BitmapItem item = this.mMemoryCache.get(getBucketKey(key.getCacheKey(), key.getBitmapConfig()));
        if (item != null) {
            return createRefCopy(item.findDrawable(key));
        }
        return null;
    }

    public BitmapDrawable getLargestBitmapFromMemCache(BitmapWorkerOptions key) {
        BitmapItem item = this.mMemoryCache.get(getBucketKey(key.getCacheKey(), key.getBitmapConfig()));
        if (item != null) {
            return (BitmapDrawable) createRefCopy(item.findLargestDrawable(key));
        }
        return null;
    }

    private Drawable createRefCopy(Drawable d) {
        if (d == null) {
            return null;
        }
        if (!(d instanceof RefcountBitmapDrawable)) {
            return d;
        }
        RefcountBitmapDrawable refcountDrawable = (RefcountBitmapDrawable) d;
        refcountDrawable.getRefcountObject().addRef();
        return new RefcountBitmapDrawable(this.mContext.getResources(), refcountDrawable);
    }
}
