package com.mediatek.wwtv.setting.widget.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import com.mediatek.wwtv.setting.util.AccountImageHelper;
import com.mediatek.wwtv.setting.util.ByteArrayPool;
import com.mediatek.wwtv.setting.util.CachedInputStream;
import com.mediatek.wwtv.setting.util.UriUtils;
import com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class BitmapWorkerTask extends AsyncTask<BitmapWorkerOptions, Void, Bitmap> {
    private static final boolean DEBUG = false;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final int READ_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final String TAG = "BitmapWorker";
    private WeakReference<ImageView> mImageView;
    protected boolean mScaled = false;

    public BitmapWorkerTask(ImageView imageView) {
        this.mImageView = new WeakReference<>(imageView);
    }

    /* access modifiers changed from: protected */
    public Bitmap doInBackground(BitmapWorkerOptions... params) {
        return retrieveBitmap(params[0]);
    }

    /* access modifiers changed from: protected */
    public Bitmap retrieveBitmap(BitmapWorkerOptions workerOptions) {
        try {
            if (workerOptions.getIconResource() != null) {
                return getBitmapFromResource(workerOptions.getContext(), workerOptions.getIconResource(), workerOptions);
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
                return getBitmapFromResource(workerOptions.getContext(), UriUtils.getIconResource(workerOptions.getResourceUri()), workerOptions);
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
    public void onPostExecute(Bitmap bitmap) {
        ImageView imageView;
        if (this.mImageView != null && (imageView = (ImageView) this.mImageView.get()) != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap getBitmapFromResource(Context context, Intent.ShortcutIconResource iconResource, BitmapWorkerOptions outputOptions) throws IOException {
        try {
            Object drawable = loadDrawable(context, iconResource);
            if (drawable instanceof InputStream) {
                return decodeBitmap((InputStream) drawable, outputOptions);
            }
            if (drawable instanceof Drawable) {
                return createIconBitmap((Drawable) drawable, outputOptions);
            }
            Log.w(TAG, "getBitmapFromResource failed, unrecognized resource: " + drawable);
            return (Bitmap) drawable;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Could not load package: " + iconResource.packageName + "! NameNotFound");
            return null;
        } catch (Resources.NotFoundException e2) {
            Log.w(TAG, "Could not load resource: " + iconResource.resourceName + "! NotFound");
            return null;
        }
    }

    public final boolean isScaled() {
        return this.mScaled;
    }

    private Bitmap scaleBitmapIfNecessary(BitmapWorkerOptions outputOptions, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        float heightScale = 1.0f;
        if (bitmap.getHeight() > outputOptions.getHeight()) {
            heightScale = ((float) outputOptions.getHeight()) / ((float) bitmap.getHeight());
        }
        float widthScale = 1.0f;
        if (bitmap.getWidth() > outputOptions.getWidth()) {
            widthScale = ((float) outputOptions.getWidth()) / ((float) bitmap.getWidth());
        }
        float scale = heightScale < widthScale ? heightScale : widthScale;
        if (scale >= 1.0f) {
            return bitmap;
        }
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * scale), (int) (((float) bitmap.getHeight()) * scale), true);
        this.mScaled = true;
        return newBitmap;
    }

    private Bitmap decodeBitmap(InputStream in, BitmapWorkerOptions options) throws IOException {
        CachedInputStream bufferedStream = null;
        try {
            CachedInputStream bufferedStream2 = new CachedInputStream(in);
            bufferedStream2.setOverrideMarkLimit(Integer.MAX_VALUE);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            if (options.getBitmapConfig() != null) {
                bitmapOptions.inPreferredConfig = options.getBitmapConfig();
            }
            bitmapOptions.inTempStorage = ByteArrayPool.get16KBPool().allocateChunk();
            bufferedStream2.mark(Integer.MAX_VALUE);
            BitmapFactory.decodeStream(bufferedStream2, (Rect) null, bitmapOptions);
            float heightScale = 1.0f;
            int height = options.getHeight();
            if (height > 0) {
                heightScale = ((float) bitmapOptions.outHeight) / ((float) height);
            }
            float widthScale = 1.0f;
            int width = options.getWidth();
            if (width > 0) {
                widthScale = ((float) bitmapOptions.outWidth) / ((float) width);
            }
            float scale = heightScale > widthScale ? heightScale : widthScale;
            bitmapOptions.inJustDecodeBounds = false;
            if (scale >= 2.0f) {
                bitmapOptions.inSampleSize = (int) scale;
            }
            bufferedStream2.reset();
            bufferedStream2.setOverrideMarkLimit(0);
            Bitmap scaleBitmapIfNecessary = scaleBitmapIfNecessary(options, BitmapFactory.decodeStream(bufferedStream2, (Rect) null, bitmapOptions));
            ByteArrayPool.get16KBPool().releaseChunk(bitmapOptions.inTempStorage);
            bufferedStream2.close();
            return scaleBitmapIfNecessary;
        } catch (Throwable th) {
            if (0 != 0) {
                ByteArrayPool.get16KBPool().releaseChunk(null.inTempStorage);
            }
            if (bufferedStream != null) {
                bufferedStream.close();
            }
            throw th;
        }
    }

    private Bitmap getBitmapFromHttp(BitmapWorkerOptions options) throws IOException {
        URLConnection connection = new URL(options.getResourceUri().toString()).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        return decodeBitmap(new BufferedInputStream(connection.getInputStream()), options);
    }

    private Bitmap getBitmapFromContent(BitmapWorkerOptions options) throws IOException {
        InputStream bitmapStream = options.getContext().getContentResolver().openInputStream(options.getResourceUri());
        if (bitmapStream != null) {
            return decodeBitmap(bitmapStream, options);
        }
        Log.w(TAG, "Content provider returned a null InputStream when trying to open resource.");
        return null;
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

    private static Bitmap createIconBitmap(Drawable drawable, BitmapWorkerOptions workerOptions) {
        int width = drawable.getIntrinsicWidth();
        if (width == -1) {
            width = workerOptions.getWidth();
        }
        int height = drawable.getIntrinsicHeight();
        if (height == -1) {
            height = workerOptions.getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable getDrawable(Context context, Intent.ShortcutIconResource iconResource) throws PackageManager.NameNotFoundException {
        Resources resources = context.getPackageManager().getResourcesForApplication(iconResource.packageName);
        int id = resources.getIdentifier(iconResource.resourceName, (String) null, (String) null);
        if (id != 0) {
            return resources.getDrawable(id);
        }
        throw new PackageManager.NameNotFoundException();
    }

    private Bitmap getAccountImage(BitmapWorkerOptions options) {
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
            return BitmapDownloader.getInstance(context).loadBitmapBlocking(new BitmapWorkerOptions.Builder(context).width(options.getWidth()).height(options.getHeight()).cacheFlag(options.getCacheFlag()).bitmapConfig(options.getBitmapConfig()).resource(Uri.parse(picUriString)).build());
        }
        return null;
    }
}
