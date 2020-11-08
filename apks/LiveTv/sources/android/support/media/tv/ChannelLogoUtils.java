package android.support.media.tv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.tv.TvContract;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@WorkerThread
public class ChannelLogoUtils {
    private static final int CONNECTION_TIMEOUT_MS_FOR_URLCONNECTION = 3000;
    private static final int READ_TIMEOUT_MS_FOR_URLCONNECTION = 10000;
    private static final String TAG = "ChannelLogoUtils";

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004a, code lost:
        if ((r1 instanceof java.net.HttpURLConnection) == false) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004c, code lost:
        r1.disconnect();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007a, code lost:
        if ((0 instanceof java.net.HttpURLConnection) == false) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007d, code lost:
        if (r3 == null) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0083, code lost:
        if (storeChannelLogo(r8, r9, r3) == false) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0085, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0087, code lost:
        return false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0043 A[SYNTHETIC, Splitter:B:14:0x0043] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean storeChannelLogo(@android.support.annotation.NonNull android.content.Context r8, long r9, @android.support.annotation.NonNull android.net.Uri r11) {
        /*
            android.net.Uri r0 = r11.normalizeScheme()
            java.lang.String r0 = r0.getScheme()
            r1 = 0
            r2 = 0
            r3 = 0
            java.lang.String r4 = "android.resource"
            boolean r4 = r4.equals(r0)     // Catch:{ IOException -> 0x0055 }
            if (r4 != 0) goto L_0x0033
            java.lang.String r4 = "file"
            boolean r4 = r4.equals(r0)     // Catch:{ IOException -> 0x0055 }
            if (r4 != 0) goto L_0x0033
            java.lang.String r4 = "content"
            boolean r4 = r4.equals(r0)     // Catch:{ IOException -> 0x0055 }
            if (r4 == 0) goto L_0x0024
            goto L_0x0033
        L_0x0024:
            java.lang.String r4 = r11.toString()     // Catch:{ IOException -> 0x0055 }
            java.net.URLConnection r4 = getUrlConnection(r4)     // Catch:{ IOException -> 0x0055 }
            r1 = r4
            java.io.InputStream r4 = r1.getInputStream()     // Catch:{ IOException -> 0x0055 }
            r2 = r4
            goto L_0x003c
        L_0x0033:
            android.content.ContentResolver r4 = r8.getContentResolver()     // Catch:{ IOException -> 0x0055 }
            java.io.InputStream r4 = r4.openInputStream(r11)     // Catch:{ IOException -> 0x0055 }
            r2 = r4
        L_0x003c:
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeStream(r2)     // Catch:{ IOException -> 0x0055 }
            r3 = r4
            if (r2 == 0) goto L_0x0048
            r2.close()     // Catch:{ IOException -> 0x0047 }
            goto L_0x0048
        L_0x0047:
            r4 = move-exception
        L_0x0048:
            boolean r4 = r1 instanceof java.net.HttpURLConnection
            if (r4 == 0) goto L_0x007d
        L_0x004c:
            r4 = r1
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4
            r4.disconnect()
            goto L_0x007d
        L_0x0053:
            r4 = move-exception
            goto L_0x0089
        L_0x0055:
            r4 = move-exception
            java.lang.String r5 = "ChannelLogoUtils"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0053 }
            r6.<init>()     // Catch:{ all -> 0x0053 }
            java.lang.String r7 = "Failed to get logo from the URI: "
            r6.append(r7)     // Catch:{ all -> 0x0053 }
            r6.append(r11)     // Catch:{ all -> 0x0053 }
            java.lang.String r7 = "\n"
            r6.append(r7)     // Catch:{ all -> 0x0053 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0053 }
            android.util.Log.i(r5, r6, r4)     // Catch:{ all -> 0x0053 }
            if (r2 == 0) goto L_0x0078
            r2.close()     // Catch:{ IOException -> 0x0077 }
            goto L_0x0078
        L_0x0077:
            r4 = move-exception
        L_0x0078:
            boolean r4 = r1 instanceof java.net.HttpURLConnection
            if (r4 == 0) goto L_0x007d
            goto L_0x004c
        L_0x007d:
            if (r3 == 0) goto L_0x0087
            boolean r4 = storeChannelLogo((android.content.Context) r8, (long) r9, (android.graphics.Bitmap) r3)
            if (r4 == 0) goto L_0x0087
            r4 = 1
            goto L_0x0088
        L_0x0087:
            r4 = 0
        L_0x0088:
            return r4
        L_0x0089:
            if (r2 == 0) goto L_0x0090
            r2.close()     // Catch:{ IOException -> 0x008f }
            goto L_0x0090
        L_0x008f:
            r5 = move-exception
        L_0x0090:
            boolean r5 = r1 instanceof java.net.HttpURLConnection
            if (r5 == 0) goto L_0x009a
            r5 = r1
            java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5
            r5.disconnect()
        L_0x009a:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.media.tv.ChannelLogoUtils.storeChannelLogo(android.content.Context, long, android.net.Uri):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0024, code lost:
        if (r2 != null) goto L_0x0026;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        if (r3 != null) goto L_0x0028;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0031, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0034, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0035, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0036, code lost:
        android.util.Log.i(TAG, "Failed to store the logo to the system content provider.\n", r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0035 A[ExcHandler: SQLiteException | IOException (r2v0 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r0 
  PHI: (r0v2 'result' boolean) = (r0v0 'result' boolean), (r0v3 'result' boolean), (r0v3 'result' boolean), (r0v3 'result' boolean), (r0v4 'result' boolean), (r0v4 'result' boolean) binds: [B:1:0x0005, B:15:0x0028, B:18:0x002d, B:16:?, B:7:0x001c, B:8:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0005] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean storeChannelLogo(@android.support.annotation.NonNull android.content.Context r6, long r7, @android.support.annotation.NonNull android.graphics.Bitmap r9) {
        /*
            r0 = 0
            android.net.Uri r1 = android.media.tv.TvContract.buildChannelLogoUri(r7)
            android.content.ContentResolver r2 = r6.getContentResolver()     // Catch:{ SQLiteException | IOException -> 0x0035 }
            java.io.OutputStream r2 = r2.openOutputStream(r1)     // Catch:{ SQLiteException | IOException -> 0x0035 }
            r3 = 0
            android.graphics.Bitmap$CompressFormat r4 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ Throwable -> 0x0022 }
            r5 = 100
            boolean r4 = r9.compress(r4, r5, r2)     // Catch:{ Throwable -> 0x0022 }
            r0 = r4
            r2.flush()     // Catch:{ Throwable -> 0x0022 }
            if (r2 == 0) goto L_0x001f
            r2.close()     // Catch:{ SQLiteException | IOException -> 0x0035 }
        L_0x001f:
            goto L_0x003d
        L_0x0020:
            r4 = move-exception
            goto L_0x0024
        L_0x0022:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0020 }
        L_0x0024:
            if (r2 == 0) goto L_0x0034
            if (r3 == 0) goto L_0x0031
            r2.close()     // Catch:{ Throwable -> 0x002c, SQLiteException | IOException -> 0x0035 }
            goto L_0x0034
        L_0x002c:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ SQLiteException | IOException -> 0x0035 }
            goto L_0x0034
        L_0x0031:
            r2.close()     // Catch:{ SQLiteException | IOException -> 0x0035 }
        L_0x0034:
            throw r4     // Catch:{ SQLiteException | IOException -> 0x0035 }
        L_0x0035:
            r2 = move-exception
            java.lang.String r3 = "ChannelLogoUtils"
            java.lang.String r4 = "Failed to store the logo to the system content provider.\n"
            android.util.Log.i(r3, r4, r2)
        L_0x003d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.media.tv.ChannelLogoUtils.storeChannelLogo(android.content.Context, long, android.graphics.Bitmap):boolean");
    }

    public static Bitmap loadChannelLogo(@NonNull Context context, long channelId) {
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(TvContract.buildChannelLogoUri(channelId)));
        } catch (FileNotFoundException e) {
            Log.i(TAG, "Channel logo for channel (ID:" + channelId + ") not found.", e);
            return null;
        }
    }

    private static URLConnection getUrlConnection(String uriString) throws IOException {
        URLConnection urlConnection = new URL(uriString).openConnection();
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(10000);
        return urlConnection;
    }
}
