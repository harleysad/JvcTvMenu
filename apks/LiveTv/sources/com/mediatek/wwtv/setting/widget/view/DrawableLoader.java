package com.mediatek.wwtv.setting.widget.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import com.mediatek.wwtv.setting.util.AccountImageHelper;
import com.mediatek.wwtv.setting.util.UriUtils;
import com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions;
import com.mediatek.wwtv.setting.widget.view.RefcountObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

class DrawableLoader extends AsyncTask<BitmapWorkerOptions, Void, Drawable> {
    private static final boolean DEBUG = false;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final int READ_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final String TAG = "DrawableLoader";
    private WeakReference<ImageView> mImageView;
    private int mOriginalHeight;
    private int mOriginalWidth;
    /* access modifiers changed from: private */
    public RecycleBitmapPool mRecycledBitmaps;
    private RefcountObject.RefcountListener mRefcountListener = new RefcountObject.RefcountListener() {
        public void onRefcountZero(RefcountObject object) {
            DrawableLoader.this.mRecycledBitmaps.addRecycledBitmap((Bitmap) object.getObject());
        }
    };

    DrawableLoader(ImageView imageView, RecycleBitmapPool recycledBitmapPool) {
        this.mImageView = new WeakReference<>(imageView);
        this.mRecycledBitmaps = recycledBitmapPool;
    }

    public int getOriginalWidth() {
        return this.mOriginalWidth;
    }

    public int getOriginalHeight() {
        return this.mOriginalHeight;
    }

    /* access modifiers changed from: protected */
    public Drawable doInBackground(BitmapWorkerOptions... params) {
        return retrieveDrawable(params[0]);
    }

    /* access modifiers changed from: protected */
    public Drawable retrieveDrawable(BitmapWorkerOptions workerOptions) {
        try {
            if (workerOptions.getIconResource() != null) {
                return getBitmapFromResource(workerOptions.getIconResource(), workerOptions);
            }
            if (workerOptions.getResourceUri() != null) {
                if (!UriUtils.isAndroidResourceUri(workerOptions.getResourceUri())) {
                    if (!UriUtils.isShortcutIconResourceUri(workerOptions.getResourceUri())) {
                        if (UriUtils.isWebUri(workerOptions.getResourceUri())) {
                            return getBitmapFromHttp(workerOptions);
                        }
                        if (UriUtils.isContentUri(workerOptions.getResourceUri())) {
                            return getBitmapFromContent(workerOptions);
                        }
                        if (UriUtils.isAccountImageUri(workerOptions.getResourceUri())) {
                            return getAccountImage(workerOptions);
                        }
                        Log.e(TAG, "Error loading bitmap - unknown resource URI! " + workerOptions.getResourceUri());
                    }
                }
                return getBitmapFromResource(UriUtils.getIconResource(workerOptions.getResourceUri()), workerOptions);
            }
            Log.e(TAG, "Error loading bitmap - no source!");
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error loading url " + workerOptions.getResourceUri(), e);
            return null;
        } catch (RuntimeException e2) {
            Log.e(TAG, "Critical Error loading url " + workerOptions.getResourceUri(), e2);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Drawable bitmap) {
        ImageView imageView;
        if (this.mImageView != null && (imageView = (ImageView) this.mImageView.get()) != null) {
            imageView.setImageDrawable(bitmap);
        }
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Drawable result) {
        if (result instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) result).getRefcountObject().releaseRef();
        }
    }

    private Drawable getBitmapFromResource(Intent.ShortcutIconResource iconResource, BitmapWorkerOptions outputOptions) throws IOException {
        try {
            Object drawable = loadDrawable(outputOptions.getContext(), iconResource);
            if (drawable instanceof InputStream) {
                return decodeBitmap((InputStream) drawable, outputOptions);
            }
            if (drawable instanceof Drawable) {
                Drawable d = (Drawable) drawable;
                this.mOriginalWidth = d.getIntrinsicWidth();
                this.mOriginalHeight = d.getIntrinsicHeight();
                return d;
            }
            Log.w(TAG, "getBitmapFromResource failed, unrecognized resource: " + drawable);
            return (Drawable) drawable;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Could not load package: " + iconResource.packageName + "! NameNotFound");
            return null;
        } catch (Resources.NotFoundException e2) {
            Log.w(TAG, "Could not load resource: " + iconResource.resourceName + "! NotFound");
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0123  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable decodeBitmap(java.io.InputStream r17, com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions r18) throws java.io.IOException {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            r3 = 0
            r4 = r3
            com.mediatek.wwtv.setting.util.CachedInputStream r0 = new com.mediatek.wwtv.setting.util.CachedInputStream     // Catch:{ all -> 0x010c }
            r5 = r17
            r0.<init>(r5)     // Catch:{ all -> 0x010a }
            r2 = r0
            r0 = 2147483647(0x7fffffff, float:NaN)
            r2.setOverrideMarkLimit(r0)     // Catch:{ all -> 0x010a }
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x010a }
            r6.<init>()     // Catch:{ all -> 0x010a }
            r4 = r6
            r6 = 1
            r4.inJustDecodeBounds = r6     // Catch:{ all -> 0x010a }
            android.graphics.Bitmap$Config r7 = r18.getBitmapConfig()     // Catch:{ all -> 0x010a }
            if (r7 == 0) goto L_0x0028
            android.graphics.Bitmap$Config r7 = r18.getBitmapConfig()     // Catch:{ all -> 0x010a }
            r4.inPreferredConfig = r7     // Catch:{ all -> 0x010a }
        L_0x0028:
            com.mediatek.wwtv.setting.util.ByteArrayPool r7 = com.mediatek.wwtv.setting.util.ByteArrayPool.get16KBPool()     // Catch:{ all -> 0x010a }
            byte[] r7 = r7.allocateChunk()     // Catch:{ all -> 0x010a }
            r4.inTempStorage = r7     // Catch:{ all -> 0x010a }
            r2.mark(r0)     // Catch:{ all -> 0x010a }
            android.graphics.BitmapFactory.decodeStream(r2, r3, r4)     // Catch:{ all -> 0x010a }
            int r0 = r4.outWidth     // Catch:{ all -> 0x010a }
            r1.mOriginalWidth = r0     // Catch:{ all -> 0x010a }
            int r0 = r4.outHeight     // Catch:{ all -> 0x010a }
            r1.mOriginalHeight = r0     // Catch:{ all -> 0x010a }
            r0 = 1
            int r7 = r18.getHeight()     // Catch:{ all -> 0x010a }
            if (r7 <= 0) goto L_0x004c
            int r8 = r4.outHeight     // Catch:{ all -> 0x010a }
            int r8 = r8 / r7
            r0 = r8
            goto L_0x004d
        L_0x004c:
            r8 = r0
        L_0x004d:
            r0 = 1
            int r9 = r18.getWidth()     // Catch:{ all -> 0x010a }
            if (r9 <= 0) goto L_0x0059
            int r10 = r4.outWidth     // Catch:{ all -> 0x010a }
            int r10 = r10 / r9
            r0 = r10
            goto L_0x005a
        L_0x0059:
            r10 = r0
        L_0x005a:
            if (r8 <= r10) goto L_0x005e
            r0 = r8
            goto L_0x005f
        L_0x005e:
            r0 = r10
        L_0x005f:
            r11 = 0
            if (r0 > r6) goto L_0x0065
            r0 = 1
        L_0x0063:
            r12 = r0
            goto L_0x0070
        L_0x0065:
            r12 = r0
            r0 = r11
        L_0x0067:
            int r12 = r12 >> r6
            int r0 = r0 + r6
            if (r12 != 0) goto L_0x0067
            int r13 = r0 + -1
            int r0 = r6 << r13
            goto L_0x0063
        L_0x0070:
            r2.reset()     // Catch:{ all -> 0x010a }
            r2.setOverrideMarkLimit(r11)     // Catch:{ all -> 0x010a }
            r13 = r3
            r4.inJustDecodeBounds = r11     // Catch:{ RuntimeException -> 0x0090 }
            r4.inSampleSize = r12     // Catch:{ RuntimeException -> 0x0090 }
            r4.inMutable = r6     // Catch:{ RuntimeException -> 0x0090 }
            com.mediatek.wwtv.setting.widget.view.RecycleBitmapPool r0 = r1.mRecycledBitmaps     // Catch:{ RuntimeException -> 0x0090 }
            int r6 = r1.mOriginalWidth     // Catch:{ RuntimeException -> 0x0090 }
            int r6 = r6 / r12
            int r14 = r1.mOriginalHeight     // Catch:{ RuntimeException -> 0x0090 }
            int r14 = r14 / r12
            android.graphics.Bitmap r0 = r0.getRecycledBitmap(r6, r14)     // Catch:{ RuntimeException -> 0x0090 }
            r4.inBitmap = r0     // Catch:{ RuntimeException -> 0x0090 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r2, r3, r4)     // Catch:{ RuntimeException -> 0x0090 }
            goto L_0x00b9
        L_0x0090:
            r0 = move-exception
            java.lang.String r6 = "DrawableLoader"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x010a }
            r14.<init>()     // Catch:{ all -> 0x010a }
            java.lang.String r15 = "RuntimeException"
            r14.append(r15)     // Catch:{ all -> 0x010a }
            r14.append(r0)     // Catch:{ all -> 0x010a }
            java.lang.String r15 = ", trying decodeStream again"
            r14.append(r15)     // Catch:{ all -> 0x010a }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x010a }
            android.util.Log.e(r6, r14)     // Catch:{ all -> 0x010a }
            r2.reset()     // Catch:{ all -> 0x010a }
            r2.setOverrideMarkLimit(r11)     // Catch:{ all -> 0x010a }
            r4.inBitmap = r3     // Catch:{ all -> 0x010a }
            android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeStream(r2, r3, r4)     // Catch:{ all -> 0x010a }
            r0 = r6
        L_0x00b9:
            if (r0 != 0) goto L_0x00d9
            java.lang.String r6 = "DrawableLoader"
            java.lang.String r11 = "bitmap was null"
            android.util.Log.d(r6, r11)     // Catch:{ all -> 0x010a }
            java.lang.String r6 = "DrawableLoader"
            java.lang.String r11 = "couldn't load bitmap, releasing resources"
            android.util.Log.w(r6, r11)
            com.mediatek.wwtv.setting.util.ByteArrayPool r6 = com.mediatek.wwtv.setting.util.ByteArrayPool.get16KBPool()
            byte[] r11 = r4.inTempStorage
            r6.releaseChunk(r11)
            r2.close()
            return r3
        L_0x00d9:
            com.mediatek.wwtv.setting.widget.view.RefcountObject r3 = new com.mediatek.wwtv.setting.widget.view.RefcountObject     // Catch:{ all -> 0x010a }
            r3.<init>(r0)     // Catch:{ all -> 0x010a }
            r3.addRef()     // Catch:{ all -> 0x010a }
            com.mediatek.wwtv.setting.widget.view.RefcountObject$RefcountListener r6 = r1.mRefcountListener     // Catch:{ all -> 0x010a }
            r3.setRefcountListener(r6)     // Catch:{ all -> 0x010a }
            com.mediatek.wwtv.setting.widget.view.RefcountBitmapDrawable r6 = new com.mediatek.wwtv.setting.widget.view.RefcountBitmapDrawable     // Catch:{ all -> 0x010a }
            android.content.Context r11 = r18.getContext()     // Catch:{ all -> 0x010a }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ all -> 0x010a }
            r6.<init>((android.content.res.Resources) r11, (com.mediatek.wwtv.setting.widget.view.RefcountObject<android.graphics.Bitmap>) r3)     // Catch:{ all -> 0x010a }
            java.lang.String r11 = "DrawableLoader"
            java.lang.String r13 = "couldn't load bitmap, releasing resources"
            android.util.Log.w(r11, r13)
            com.mediatek.wwtv.setting.util.ByteArrayPool r11 = com.mediatek.wwtv.setting.util.ByteArrayPool.get16KBPool()
            byte[] r13 = r4.inTempStorage
            r11.releaseChunk(r13)
            r2.close()
            return r6
        L_0x010a:
            r0 = move-exception
            goto L_0x010f
        L_0x010c:
            r0 = move-exception
            r5 = r17
        L_0x010f:
            java.lang.String r3 = "DrawableLoader"
            java.lang.String r6 = "couldn't load bitmap, releasing resources"
            android.util.Log.w(r3, r6)
            if (r4 == 0) goto L_0x0121
            com.mediatek.wwtv.setting.util.ByteArrayPool r3 = com.mediatek.wwtv.setting.util.ByteArrayPool.get16KBPool()
            byte[] r6 = r4.inTempStorage
            r3.releaseChunk(r6)
        L_0x0121:
            if (r2 == 0) goto L_0x0126
            r2.close()
        L_0x0126:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.DrawableLoader.decodeBitmap(java.io.InputStream, com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions):android.graphics.drawable.Drawable");
    }

    private Drawable getBitmapFromHttp(BitmapWorkerOptions options) throws IOException {
        URL url = new URL(options.getResourceUri().toString());
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            return decodeBitmap(connection.getInputStream(), options);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "loading " + url + " timed out");
            return null;
        }
    }

    private Drawable getBitmapFromContent(BitmapWorkerOptions options) throws IOException {
        Uri resourceUri = options.getResourceUri();
        if (resourceUri != null) {
            try {
                InputStream bitmapStream = options.getContext().getContentResolver().openInputStream(resourceUri);
                if (bitmapStream != null) {
                    return decodeBitmap(bitmapStream, options);
                }
                Log.w(TAG, "Content provider returned a null InputStream when trying to open resource.");
                return null;
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException during openInputStream for uri: " + resourceUri.toString());
                return null;
            }
        } else {
            Log.w(TAG, "Get null resourceUri from BitmapWorkerOptions.");
            return null;
        }
    }

    private static Object loadDrawable(Context context, Intent.ShortcutIconResource r) throws PackageManager.NameNotFoundException {
        Resources resources = context.getPackageManager().getResourcesForApplication(r.packageName);
        if (resources == null) {
            return null;
        }
        int id = resources.getIdentifier(r.resourceName, (String) null, (String) null);
        if (id == 0) {
            Log.e(TAG, "Couldn't get resource " + r.resourceName + " in resources of " + r.packageName);
            return null;
        }
        TypedValue value = new TypedValue();
        resources.getValue(id, value, true);
        if ((value.type != 3 || !value.string.toString().endsWith(".xml")) && (value.type < 28 || value.type > 31)) {
            return resources.openRawResource(id, value);
        }
        return resources.getDrawable(id);
    }

    public static Drawable getDrawable(Context context, Intent.ShortcutIconResource iconResource) throws PackageManager.NameNotFoundException {
        Resources resources = context.getPackageManager().getResourcesForApplication(iconResource.packageName);
        int id = resources.getIdentifier(iconResource.resourceName, (String) null, (String) null);
        if (id != 0) {
            return resources.getDrawable(id);
        }
        throw new PackageManager.NameNotFoundException();
    }

    private Drawable getAccountImage(BitmapWorkerOptions options) {
        String picUriString;
        String accountName = UriUtils.getAccountName(options.getResourceUri());
        Context context = options.getContext();
        if (!(accountName == null || context == null)) {
            Account thisAccount = null;
            Account[] accountsByType = AccountManager.get(context).getAccountsByType(GOOGLE_ACCOUNT_TYPE);
            int length = accountsByType.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Account account = accountsByType[i];
                if (account.name.equals(accountName)) {
                    thisAccount = account;
                    break;
                }
                i++;
            }
            if (thisAccount == null || (picUriString = AccountImageHelper.getAccountPictureUri(context, thisAccount)) == null) {
                return null;
            }
            return DrawableDownloader.getInstance(context).loadBitmapBlocking(new BitmapWorkerOptions.Builder(context).width(options.getWidth()).height(options.getHeight()).cacheFlag(options.getCacheFlag()).bitmapConfig(options.getBitmapConfig()).resource(Uri.parse(picUriString)).build());
        }
        return null;
    }
}
