package com.mediatek.wwtv.tvcenter.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalBitmapUtil {
    private static final int DEFAULT_COMPRESS_QUALITY = 90;
    private static final String[] IMAGE_PROJECTION = {"orientation"};
    private static final int INDEX_ORIENTATION = 0;
    private static final String TAG = "LocalBitmapUtil";
    private final Context context;

    public LocalBitmapUtil(Context context2) {
        this.context = context2;
    }

    public static Bitmap createBitmap(Bitmap bitmap, Matrix m) {
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Rect getBitmapBounds(Uri uri) {
        Rect bounds = new Rect();
        InputStream is = null;
        try {
            is = this.context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, (Rect) null, options);
            bounds.right = options.outWidth;
            bounds.bottom = options.outHeight;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            closeStream(is);
            throw th;
        }
        closeStream(is);
        return bounds;
    }

    private int getOrientation(Uri uri) {
        Cursor cursor = this.context.getContentResolver().query(uri, IMAGE_PROJECTION, (String) null, (String[]) null, (String) null);
        if (cursor == null || !cursor.moveToNext()) {
            return 0;
        }
        return cursor.getInt(0);
    }

    private Bitmap decodeBitmap(Uri uri, int width, int height) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            Rect bounds = getBitmapBounds(uri);
            int sampleSize = Math.min(Math.max(bounds.width() / width, bounds.height() / height), Math.max(bounds.width() / height, bounds.height() / width));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(sampleSize, 1);
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            is = this.context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, (Rect) null, options);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException: " + uri);
        } catch (Throwable th) {
            closeStream((Closeable) null);
            throw th;
        }
        closeStream(is);
        if (bitmap != null) {
            float scale = Math.max(Math.min(((float) width) / ((float) bitmap.getWidth()), ((float) height) / ((float) bitmap.getHeight())), Math.min(((float) height) / ((float) bitmap.getWidth()), ((float) width) / ((float) bitmap.getHeight())));
            if (scale < 1.0f) {
                Matrix m = new Matrix();
                m.setScale(scale, scale);
                Bitmap transformed = createBitmap(bitmap, m);
                bitmap.recycle();
                return transformed;
            }
        }
        return bitmap;
    }

    public Bitmap getBitmap(Uri uri, int width, int height) {
        int orientation;
        Bitmap bitmap = decodeBitmap(uri, width, height);
        if (bitmap == null || (orientation = getOrientation(uri)) == 0) {
            return bitmap;
        }
        Matrix m = new Matrix();
        m.setRotate((float) orientation);
        Bitmap transformed = createBitmap(bitmap, m);
        bitmap.recycle();
        return transformed;
    }

    public File saveBitmap(Bitmap bitmap, String directory, String filename, Bitmap.CompressFormat format) {
        StringBuilder sb;
        OutputStream os = null;
        if (directory == null) {
            directory = this.context.getCacheDir().getAbsolutePath();
        } else {
            File file = new File(directory);
            if (!file.isDirectory() && !file.mkdirs()) {
                return null;
            }
        }
        File file2 = null;
        try {
            if (format == Bitmap.CompressFormat.PNG) {
                sb = new StringBuilder();
                sb.append(filename);
                sb.append(".png");
            } else {
                sb = new StringBuilder();
                sb.append(filename);
                sb.append(".jpg");
            }
            file2 = new File(directory, sb.toString());
            os = new FileOutputStream(file2);
            bitmap.compress(format, 90, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            closeStream((Closeable) null);
            throw th;
        }
        closeStream(os);
        return file2;
    }
}
