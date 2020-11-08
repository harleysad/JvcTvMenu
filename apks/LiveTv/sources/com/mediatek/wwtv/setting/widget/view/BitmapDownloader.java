package com.mediatek.wwtv.setting.widget.view;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;
import com.mediatek.wwtv.setting.util.AccountImageChangeObserver;
import com.mediatek.wwtv.setting.util.UriUtils;
import com.mediatek.wwtv.tvcenter.R;
import java.lang.ref.SoftReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BitmapDownloader {
    private static final Executor BITMAP_DOWNLOADER_THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(5);
    private static final int CACHE_HARD_LIMIT = 32;
    private static final int CORE_POOL_SIZE = 5;
    private static final boolean DEBUG = false;
    private static final int MEM_TO_CACHE = 4;
    private static final int[] SIZE_BUCKET = {128, 512, Integer.MAX_VALUE};
    private static final String TAG = "BitmapDownloader";
    private static BitmapDownloader sBitmapDownloader;
    private static final Object sBitmapDownloaderLock = new Object();
    private LruCache<String, BitmapItem> mMemoryCache;

    public static abstract class BitmapCallback {
        SoftReference<BitmapWorkerTask> mTask;

        public abstract void onBitmapRetrieved(Bitmap bitmap);
    }

    private static class BitmapItem {
        Bitmap mBitmap;
        boolean mScaled;

        public BitmapItem(Bitmap bitmap, boolean scaled) {
            this.mBitmap = bitmap;
            this.mScaled = scaled;
        }
    }

    public static final BitmapDownloader getInstance(Context context) {
        if (sBitmapDownloader == null) {
            sBitmapDownloader = new BitmapDownloader(context);
        }
        return sBitmapDownloader;
    }

    public BitmapDownloader(Context context) {
        int memClass = ((ActivityManager) context.getSystemService("activity")).getMemoryClass() / 4;
        this.mMemoryCache = new LruCache<String, BitmapItem>(1048576 * (memClass > 32 ? 32 : memClass)) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapItem bitmap) {
                return bitmap.mBitmap.getByteCount();
            }
        };
    }

    @Deprecated
    public final Bitmap loadBitmapBlocking(BitmapWorkerOptions options) {
        final boolean hasAccountImageUri = UriUtils.isAccountImageUri(options.getResourceUri());
        Bitmap bitmap = null;
        if (hasAccountImageUri) {
            AccountImageChangeObserver.getInstance().registerChangeUriIfPresent(options);
        } else {
            bitmap = getBitmapFromMemCache(options);
        }
        if (bitmap != null) {
            return bitmap;
        }
        return new BitmapWorkerTask((ImageView) null) {
            /* access modifiers changed from: protected */
            public Bitmap doInBackground(BitmapWorkerOptions... params) {
                Bitmap bitmap = super.doInBackground(params);
                if (bitmap != null && !hasAccountImageUri) {
                    BitmapDownloader.this.addBitmapToMemoryCache(params[0], bitmap, isScaled());
                }
                return bitmap;
            }
        }.doInBackground(options);
    }

    public void loadBitmap(BitmapWorkerOptions options, ImageView imageView) {
        cancelDownload(imageView);
        final boolean hasAccountImageUri = UriUtils.isAccountImageUri(options.getResourceUri());
        Bitmap bitmap = null;
        if (hasAccountImageUri) {
            AccountImageChangeObserver.getInstance().registerChangeUriIfPresent(options);
        } else {
            bitmap = getBitmapFromMemCache(options);
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        BitmapWorkerTask task = new BitmapWorkerTask(imageView) {
            /* access modifiers changed from: protected */
            public Bitmap doInBackground(BitmapWorkerOptions... params) {
                Bitmap bitmap = super.doInBackground(params);
                if (bitmap != null && !hasAccountImageUri) {
                    BitmapDownloader.this.addBitmapToMemoryCache(params[0], bitmap, isScaled());
                }
                return bitmap;
            }
        };
        imageView.setTag(R.id.imageDownloadTask, new SoftReference(task));
        task.execute(new BitmapWorkerOptions[]{options});
    }

    public void getBitmap(BitmapWorkerOptions options, BitmapCallback callback) {
        cancelDownload(callback);
        boolean hasAccountImageUri = UriUtils.isAccountImageUri(options.getResourceUri());
        final Bitmap bitmap = hasAccountImageUri ? null : getBitmapFromMemCache(options);
        if (hasAccountImageUri) {
            AccountImageChangeObserver.getInstance().registerChangeUriIfPresent(options);
        }
        final boolean z = hasAccountImageUri;
        final BitmapCallback bitmapCallback = callback;
        BitmapWorkerTask task = new BitmapWorkerTask((ImageView) null) {
            /* access modifiers changed from: protected */
            public Bitmap doInBackground(BitmapWorkerOptions... params) {
                if (bitmap != null) {
                    return bitmap;
                }
                Bitmap bitmap = super.doInBackground(params);
                if (bitmap != null && !z) {
                    BitmapDownloader.this.addBitmapToMemoryCache(params[0], bitmap, isScaled());
                }
                return bitmap;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Bitmap bitmap) {
                bitmapCallback.onBitmapRetrieved(bitmap);
            }
        };
        callback.mTask = new SoftReference<>(task);
        task.executeOnExecutor(BITMAP_DOWNLOADER_THREAD_POOL_EXECUTOR, new BitmapWorkerOptions[]{options});
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask} */
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
            com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask r0 = (com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask) r0
            r2.clear()
        L_0x001d:
            goto L_0x0035
        L_0x001e:
            boolean r1 = r5 instanceof com.mediatek.wwtv.setting.widget.view.BitmapDownloader.BitmapCallback
            if (r1 == 0) goto L_0x0035
            r1 = r5
            com.mediatek.wwtv.setting.widget.view.BitmapDownloader$BitmapCallback r1 = (com.mediatek.wwtv.setting.widget.view.BitmapDownloader.BitmapCallback) r1
            java.lang.ref.SoftReference<com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask> r2 = r1.mTask
            if (r2 == 0) goto L_0x0035
            java.lang.ref.SoftReference<com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask> r2 = r1.mTask
            java.lang.Object r2 = r2.get()
            r0 = r2
            com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask r0 = (com.mediatek.wwtv.setting.widget.view.BitmapWorkerTask) r0
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
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.BitmapDownloader.cancelDownload(java.lang.Object):boolean");
    }

    private static String getBucketKey(String baseKey, Bitmap.Config bitmapConfig, int width) {
        for (int i = 0; i < SIZE_BUCKET.length; i++) {
            if (width <= SIZE_BUCKET[i]) {
                StringBuilder sb = new StringBuilder(baseKey.length() + 16);
                sb.append(baseKey);
                sb.append(":");
                sb.append(bitmapConfig == null ? "" : Integer.valueOf(bitmapConfig.ordinal()));
                sb.append(":");
                sb.append(SIZE_BUCKET[i]);
                return sb.toString();
            }
        }
        throw new RuntimeException();
    }

    /* access modifiers changed from: private */
    public void addBitmapToMemoryCache(BitmapWorkerOptions key, Bitmap bitmap, boolean isScaled) {
        if (key.isMemCacheEnabled()) {
            String bucketKey = getBucketKey(key.getCacheKey(), key.getBitmapConfig(), bitmap.getHeight());
            BitmapItem bitmapItem = this.mMemoryCache.get(bucketKey);
            if (bitmapItem != null) {
                Bitmap currentBitmap = bitmapItem.mBitmap;
                if (currentBitmap.getWidth() >= bitmap.getWidth() && currentBitmap.getHeight() >= bitmap.getHeight()) {
                    return;
                }
            }
            this.mMemoryCache.put(bucketKey, new BitmapItem(bitmap, isScaled));
        }
    }

    private Bitmap getBitmapFromMemCache(BitmapWorkerOptions key) {
        BitmapItem bitmapItem;
        if (key.getHeight() != 2048) {
            BitmapItem bitmapItem2 = this.mMemoryCache.get(getBucketKey(key.getCacheKey(), key.getBitmapConfig(), key.getHeight()));
            if (bitmapItem2 != null) {
                Bitmap bitmap = bitmapItem2.mBitmap;
                if (!bitmapItem2.mScaled || bitmap.getHeight() >= key.getHeight()) {
                    return bitmap;
                }
            }
            for (int i = SIZE_BUCKET.length - 1; i >= 0; i--) {
                if (SIZE_BUCKET[i] < key.getHeight() && (bitmapItem = this.mMemoryCache.get(getBucketKey(key.getCacheKey(), key.getBitmapConfig(), SIZE_BUCKET[i]))) != null && !bitmapItem.mScaled) {
                    return bitmapItem.mBitmap;
                }
            }
            return null;
        }
        for (int i2 = SIZE_BUCKET.length - 1; i2 >= 0; i2--) {
            BitmapItem bitmapItem3 = this.mMemoryCache.get(getBucketKey(key.getCacheKey(), key.getBitmapConfig(), SIZE_BUCKET[i2]));
            if (bitmapItem3 != null && !bitmapItem3.mScaled) {
                return bitmapItem3.mBitmap;
            }
        }
        return null;
    }

    public Bitmap getLargestBitmapFromMemCache(BitmapWorkerOptions key) {
        for (int i = SIZE_BUCKET.length - 1; i >= 0; i--) {
            BitmapItem bitmapItem = this.mMemoryCache.get(getBucketKey(key.getCacheKey(), key.getBitmapConfig(), SIZE_BUCKET[i]));
            if (bitmapItem != null) {
                return bitmapItem.mBitmap;
            }
        }
        return null;
    }
}
