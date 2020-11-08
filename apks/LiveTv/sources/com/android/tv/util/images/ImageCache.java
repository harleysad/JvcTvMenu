package com.android.tv.util.images;

import android.support.annotation.VisibleForTesting;
import android.util.LruCache;
import com.android.tv.common.memory.MemoryManageable;
import com.android.tv.util.images.BitmapUtils;
import com.mediatek.twoworlds.tv.MtkTvScanCeBase;

public class ImageCache implements MemoryManageable {
    private static final boolean DEBUG = false;
    private static final float DEFAULT_CACHE_SIZE_PERCENT = 0.1f;
    private static final float MAX_CACHE_SIZE_PERCENT = 0.8f;
    private static final int MIN_CACHE_SIZE_KBYTES = 1024;
    private static final float MIN_CACHE_SIZE_PERCENT = 0.05f;
    private static final String TAG = "ImageCache";
    private static ImageCache sImageCache;
    private final LruCache<String, BitmapUtils.ScaledBitmapInfo> mMemoryCache;

    private ImageCache(float memCacheSizePercent) {
        this.mMemoryCache = new LruCache<String, BitmapUtils.ScaledBitmapInfo>(calculateMemCacheSize(memCacheSizePercent)) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapUtils.ScaledBitmapInfo bitmapInfo) {
                return (bitmapInfo.bitmap.getByteCount() + MtkTvScanCeBase.OPERATOR_SNN_OTHERS) / 1024;
            }
        };
    }

    public static synchronized ImageCache getInstance(float memCacheSizePercent) {
        ImageCache imageCache;
        synchronized (ImageCache.class) {
            if (sImageCache == null) {
                sImageCache = newInstance(memCacheSizePercent);
            }
            imageCache = sImageCache;
        }
        return imageCache;
    }

    @VisibleForTesting
    static ImageCache newInstance(float memCacheSizePercent) {
        return new ImageCache(memCacheSizePercent);
    }

    public static ImageCache getInstance() {
        return getInstance(DEFAULT_CACHE_SIZE_PERCENT);
    }

    public void putIfNeeded(BitmapUtils.ScaledBitmapInfo bitmapInfo) {
        if (bitmapInfo == null || bitmapInfo.id == null) {
            throw new IllegalArgumentException("Neither bitmap nor bitmap.id should be null.");
        }
        String key = bitmapInfo.id;
        synchronized (this.mMemoryCache) {
            BitmapUtils.ScaledBitmapInfo old = this.mMemoryCache.put(key, bitmapInfo);
            if (old != null && !old.needToReload(bitmapInfo)) {
                this.mMemoryCache.put(key, old);
            }
        }
    }

    public BitmapUtils.ScaledBitmapInfo get(String key) {
        return this.mMemoryCache.get(key);
    }

    public BitmapUtils.ScaledBitmapInfo remove(String key) {
        return this.mMemoryCache.remove(key);
    }

    public static int calculateMemCacheSize(float percent) {
        if (percent >= MIN_CACHE_SIZE_PERCENT && percent <= MAX_CACHE_SIZE_PERCENT) {
            return Math.max(1024, Math.round((((float) Runtime.getRuntime().maxMemory()) * percent) / 1024.0f));
        }
        throw new IllegalArgumentException("setMemCacheSizePercent - percent must be between 0.05 and 0.8 (inclusive)");
    }

    public void performTrimMemory(int level) {
        this.mMemoryCache.evictAll();
    }
}
