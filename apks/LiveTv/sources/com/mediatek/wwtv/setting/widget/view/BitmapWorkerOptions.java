package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import com.mediatek.wwtv.setting.util.UriUtils;

public class BitmapWorkerOptions {
    public static final int CACHE_FLAG_DISK_DISABLED = 2;
    public static final int CACHE_FLAG_MEM_DISABLED = 1;
    static final int MAX_IMAGE_DIMENSION_PX = 2048;
    /* access modifiers changed from: private */
    public Bitmap.Config mBitmapConfig;
    /* access modifiers changed from: private */
    public int mCacheFlag;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public int mHeight;
    /* access modifiers changed from: private */
    public Intent.ShortcutIconResource mIconResource;
    private String mKey;
    /* access modifiers changed from: private */
    public Uri mResourceUri;
    /* access modifiers changed from: private */
    public int mWidth;

    public static class Builder {
        private Bitmap.Config mBitmapConfig;
        private int mCacheFlag;
        private Context mContext;
        private int mHeight = 2048;
        private String mPackageName;
        private String mResourceName;
        private Uri mResourceUri;
        private int mWidth = 2048;

        public Builder(Context context) {
            this.mContext = context.getApplicationContext();
            this.mCacheFlag = 0;
            this.mBitmapConfig = null;
        }

        public BitmapWorkerOptions build() {
            BitmapWorkerOptions options = new BitmapWorkerOptions();
            if (!TextUtils.isEmpty(this.mPackageName)) {
                Intent.ShortcutIconResource unused = options.mIconResource = new Intent.ShortcutIconResource();
                options.mIconResource.packageName = this.mPackageName;
                options.mIconResource.resourceName = this.mResourceName;
            }
            int largestDim = Math.max(this.mWidth, this.mHeight);
            if (largestDim > 2048) {
                double scale = 2048.0d / ((double) largestDim);
                this.mWidth = (int) (((double) this.mWidth) * scale);
                this.mHeight = (int) (((double) this.mHeight) * scale);
            }
            Uri unused2 = options.mResourceUri = this.mResourceUri;
            int unused3 = options.mWidth = this.mWidth;
            int unused4 = options.mHeight = this.mHeight;
            Context unused5 = options.mContext = this.mContext;
            int unused6 = options.mCacheFlag = this.mCacheFlag;
            Bitmap.Config unused7 = options.mBitmapConfig = this.mBitmapConfig;
            if (options.mIconResource != null || options.mResourceUri != null) {
                return options;
            }
            throw new RuntimeException("Both Icon and ResourceUri are null");
        }

        public Builder resource(String packageName, String resourceName) {
            this.mPackageName = packageName;
            this.mResourceName = resourceName;
            return this;
        }

        public Builder resource(Intent.ShortcutIconResource iconResource) {
            this.mPackageName = iconResource.packageName;
            this.mResourceName = iconResource.resourceName;
            return this;
        }

        public Builder resource(Uri resourceUri) {
            this.mResourceUri = resourceUri;
            return this;
        }

        public Builder width(int width) {
            if (width > 0) {
                this.mWidth = width;
                return this;
            }
            throw new IllegalArgumentException("Can't set width to " + width);
        }

        public Builder height(int height) {
            if (height > 0) {
                this.mHeight = height;
                return this;
            }
            throw new IllegalArgumentException("Can't set height to " + height);
        }

        public Builder cacheFlag(int flag) {
            this.mCacheFlag = flag;
            return this;
        }

        public Builder bitmapConfig(Bitmap.Config config) {
            this.mBitmapConfig = config;
            return this;
        }
    }

    private BitmapWorkerOptions() {
    }

    public Intent.ShortcutIconResource getIconResource() {
        return this.mIconResource;
    }

    public Uri getResourceUri() {
        return this.mResourceUri;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public Context getContext() {
        return this.mContext;
    }

    public boolean isFromResource() {
        return getIconResource() != null || UriUtils.isAndroidResourceUri(getResourceUri()) || UriUtils.isShortcutIconResourceUri(getResourceUri());
    }

    public int getCacheFlag() {
        return this.mCacheFlag;
    }

    public boolean isMemCacheEnabled() {
        return (this.mCacheFlag & 1) == 0;
    }

    public boolean isDiskCacheEnabled() {
        return (this.mCacheFlag & 2) == 0;
    }

    public Bitmap.Config getBitmapConfig() {
        return this.mBitmapConfig;
    }

    public String getCacheKey() {
        String str;
        if (this.mKey == null) {
            if (this.mIconResource != null) {
                str = this.mIconResource.packageName + "/" + this.mIconResource.resourceName;
            } else {
                str = this.mResourceUri.toString();
            }
            this.mKey = str;
        }
        return this.mKey;
    }

    public String toString() {
        if (this.mIconResource == null) {
            return "URI: " + this.mResourceUri;
        }
        return "PackageName: " + this.mIconResource.packageName + " Resource: " + this.mIconResource + " URI: " + this.mResourceUri;
    }
}
