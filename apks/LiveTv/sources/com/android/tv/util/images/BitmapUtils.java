package com.android.tv.util.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.mediatek.wwtv.setting.widget.detailui.BaseDialogFragment;
import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class BitmapUtils {
    private static final int CONNECTION_TIMEOUT_MS_FOR_URLCONNECTION = 3000;
    private static final boolean DEBUG = false;
    private static final int MARK_READ_LIMIT = 65536;
    private static final int READ_TIMEOUT_MS_FOR_URLCONNECTION = 10000;
    private static final String TAG = "BitmapUtils";

    private BitmapUtils() {
    }

    public static Bitmap scaleBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        Rect rect = calculateNewSize(bm, maxWidth, maxHeight);
        return Bitmap.createScaledBitmap(bm, rect.right, rect.bottom, false);
    }

    public static Bitmap getScaledMutableBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        Bitmap scaledBitmap = scaleBitmap(bm, maxWidth, maxHeight);
        if (scaledBitmap.isMutable()) {
            return scaledBitmap;
        }
        return scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    /* access modifiers changed from: private */
    public static Rect calculateNewSize(Bitmap bm, int maxWidth, int maxHeight) {
        double ratio = ((double) maxHeight) / ((double) maxWidth);
        double bmRatio = ((double) bm.getHeight()) / ((double) bm.getWidth());
        Rect rect = new Rect();
        if (ratio > bmRatio) {
            rect.right = maxWidth;
            rect.bottom = Math.round((((float) bm.getHeight()) * ((float) maxWidth)) / ((float) bm.getWidth()));
        } else {
            rect.right = Math.round((((float) bm.getWidth()) * ((float) maxHeight)) / ((float) bm.getHeight()));
            rect.bottom = maxHeight;
        }
        return rect;
    }

    public static ScaledBitmapInfo createScaledBitmapInfo(String id, Bitmap bm, int maxWidth, int maxHeight) {
        return new ScaledBitmapInfo(id, scaleBitmap(bm, maxWidth, maxHeight), calculateInSampleSize(bm.getWidth(), bm.getHeight(), maxWidth, maxHeight));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        close(r4, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005b, code lost:
        if (r2 != false) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005d, code lost:
        r4 = r10.getContentResolver().openInputStream(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0067, code lost:
        r3 = getUrlConnection(r11);
        r4 = r3.getInputStream();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        android.util.Log.e(TAG, "Failed to open stream: " + r11, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00b1, code lost:
        close(r4, r3);
        android.net.TrafficStats.setThreadStatsTag(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b8, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b9, code lost:
        close(r4, r3);
        android.net.TrafficStats.setThreadStatsTag(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bf, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x002a, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002d, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x002d A[Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }, ExcHandler: SQLiteException (r6v7 'e' android.database.sqlite.SQLiteException A[CUSTOM_DECLARE, Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }]), PHI: r3 r4 
  PHI: (r3v6 'urlConnection' java.net.URLConnection) = (r3v1 'urlConnection' java.net.URLConnection), (r3v2 'urlConnection' java.net.URLConnection), (r3v2 'urlConnection' java.net.URLConnection), (r3v1 'urlConnection' java.net.URLConnection), (r3v1 'urlConnection' java.net.URLConnection), (r3v0 'urlConnection' java.net.URLConnection) binds: [B:15:0x0053, B:27:0x008c, B:28:?, B:18:0x0058, B:16:?, B:5:0x0020] A[DONT_GENERATE, DONT_INLINE]
  PHI: (r4v8 'inputStream' java.io.InputStream) = (r4v2 'inputStream' java.io.InputStream), (r4v3 'inputStream' java.io.InputStream), (r4v3 'inputStream' java.io.InputStream), (r4v2 'inputStream' java.io.InputStream), (r4v2 'inputStream' java.io.InputStream), (r4v0 'inputStream' java.io.InputStream) binds: [B:15:0x0053, B:27:0x008c, B:28:?, B:18:0x0058, B:16:?, B:5:0x0020] A[DONT_GENERATE, DONT_INLINE], Splitter:B:5:0x0020] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.android.tv.util.images.BitmapUtils.ScaledBitmapInfo decodeSampledBitmapFromUriString(android.content.Context r10, java.lang.String r11, int r12, int r13) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r11)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            android.net.Uri r0 = android.net.Uri.parse(r11)
            android.net.Uri r0 = r0.normalizeScheme()
            boolean r2 = isContentResolverUri(r0)
            r3 = 0
            r4 = 0
            int r5 = android.net.TrafficStats.getThreadStatsTag()
            r6 = 2
            android.net.TrafficStats.setThreadStatsTag(r6)
            if (r2 == 0) goto L_0x0033
            android.content.ContentResolver r6 = r10.getContentResolver()     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            java.io.InputStream r6 = r6.openInputStream(r0)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r4 = r6
            goto L_0x003d
        L_0x002a:
            r1 = move-exception
            goto L_0x00b9
        L_0x002d:
            r6 = move-exception
            goto L_0x009a
        L_0x0030:
            r6 = move-exception
            goto L_0x00c0
        L_0x0033:
            java.net.URLConnection r6 = getUrlConnection(r11)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r3 = r6
            java.io.InputStream r6 = r3.getInputStream()     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r4 = r6
        L_0x003d:
            java.io.BufferedInputStream r6 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r6.<init>(r4)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r4 = r6
            r6 = 65536(0x10000, float:9.18355E-41)
            r4.mark(r6)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r6.<init>()     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r7 = 1
            r6.inJustDecodeBounds = r7     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            android.graphics.BitmapFactory.decodeStream(r4, r1, r6)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r4.reset()     // Catch:{ IOException -> 0x0057, SQLiteException -> 0x002d }
            goto L_0x0071
        L_0x0057:
            r7 = move-exception
            close(r4, r3)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            if (r2 == 0) goto L_0x0067
            android.content.ContentResolver r8 = r10.getContentResolver()     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            java.io.InputStream r8 = r8.openInputStream(r0)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r4 = r8
            goto L_0x0071
        L_0x0067:
            java.net.URLConnection r8 = getUrlConnection(r11)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r3 = r8
            java.io.InputStream r8 = r3.getInputStream()     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r4 = r8
        L_0x0071:
            r7 = 0
            r6.inJustDecodeBounds = r7     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r6.inPreferredConfig = r7     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            int r7 = calculateInSampleSize(r6, r12, r13)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r6.inSampleSize = r7     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeStream(r4, r1, r6)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            if (r7 != 0) goto L_0x008c
            close(r4, r3)
            android.net.TrafficStats.setThreadStatsTag(r5)
            return r1
        L_0x008c:
            com.android.tv.util.images.BitmapUtils$ScaledBitmapInfo r8 = new com.android.tv.util.images.BitmapUtils$ScaledBitmapInfo     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            int r9 = r6.inSampleSize     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            r8.<init>(r11, r7, r9)     // Catch:{ IOException -> 0x0030, SQLiteException -> 0x002d }
            close(r4, r3)
            android.net.TrafficStats.setThreadStatsTag(r5)
            return r8
        L_0x009a:
            java.lang.String r7 = "BitmapUtils"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x002a }
            r8.<init>()     // Catch:{ all -> 0x002a }
            java.lang.String r9 = "Failed to open stream: "
            r8.append(r9)     // Catch:{ all -> 0x002a }
            r8.append(r11)     // Catch:{ all -> 0x002a }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x002a }
            android.util.Log.e(r7, r8, r6)     // Catch:{ all -> 0x002a }
            close(r4, r3)
            android.net.TrafficStats.setThreadStatsTag(r5)
            return r1
        L_0x00b9:
            close(r4, r3)
            android.net.TrafficStats.setThreadStatsTag(r5)
            throw r1
        L_0x00c0:
            close(r4, r3)
            android.net.TrafficStats.setThreadStatsTag(r5)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.util.images.BitmapUtils.decodeSampledBitmapFromUriString(android.content.Context, java.lang.String, int, int):com.android.tv.util.images.BitmapUtils$ScaledBitmapInfo");
    }

    private static URLConnection getUrlConnection(String uriString) throws IOException {
        URLConnection urlConnection = new URL(uriString).openConnection();
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(10000);
        return urlConnection;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        return calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        return Math.max(1, Integer.highestOneBit(Math.max(width / reqWidth, height / reqHeight)));
    }

    private static boolean isContentResolverUri(Uri uri) {
        String scheme = uri.getScheme();
        return BaseDialogFragment.TAG_CONTENT.equals(scheme) || "android.resource".equals(scheme) || "file".equals(scheme);
    }

    private static void close(Closeable closeable, URLConnection urlConnection) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.w(TAG, "Error closing " + closeable, e);
            }
        }
        if (urlConnection instanceof HttpURLConnection) {
            ((HttpURLConnection) urlConnection).disconnect();
        }
    }

    public static class ScaledBitmapInfo {
        @NonNull
        public final Bitmap bitmap;
        @NonNull
        public final String id;
        public final int inSampleSize;

        public ScaledBitmapInfo(@NonNull String id2, @NonNull Bitmap bitmap2, int inSampleSize2) {
            this.id = id2;
            this.bitmap = bitmap2;
            this.inSampleSize = inSampleSize2;
        }

        public boolean needToReload(int reqWidth, int reqHeight) {
            if (this.inSampleSize <= 1) {
                return false;
            }
            Rect size = BitmapUtils.calculateNewSize(this.bitmap, reqWidth, reqHeight);
            if (size.right >= this.bitmap.getWidth() * 2 || size.bottom >= this.bitmap.getHeight() * 2) {
                return true;
            }
            return false;
        }

        public boolean needToReload(ScaledBitmapInfo other) {
            return needToReload(other.bitmap.getWidth(), other.bitmap.getHeight());
        }

        public String toString() {
            return "ScaledBitmapInfo[" + this.id + "](in=" + this.inSampleSize + ", w=" + this.bitmap.getWidth() + ", h=" + this.bitmap.getHeight() + ")";
        }
    }

    public static void setColorFilterToDrawable(int color, Drawable drawable) {
        if (drawable != null) {
            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
