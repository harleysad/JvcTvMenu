package com.mediatek.wwtv.setting.widget.view;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RecycleBitmapPool {
    private static final boolean DEBUG = false;
    private static final boolean LARGER_BITMAP_ALLOWED = false;
    private static final int LARGER_BITMAP_ALLOWED_REUSE = 0;
    private static final String TAG = "RecycleBitmapPool";
    private static Method sGetAllocationByteCount;
    private final SparseArray<ArrayList<SoftReference<Bitmap>>> mRecycled8888 = new SparseArray<>();

    static {
        try {
            sGetAllocationByteCount = Bitmap.class.getMethod("getAllocationByteCount", new Class[0]);
        } catch (NoSuchMethodException e) {
        }
    }

    public static int getSize(Bitmap bitmap) {
        if (sGetAllocationByteCount != null) {
            try {
                return ((Integer) sGetAllocationByteCount.invoke(bitmap, new Object[0])).intValue();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "getAllocationByteCount() failed", e);
                sGetAllocationByteCount = null;
                return bitmap.getByteCount();
            } catch (IllegalAccessException e2) {
                Log.e(TAG, "getAllocationByteCount() failed", e2);
                sGetAllocationByteCount = null;
                return bitmap.getByteCount();
            } catch (InvocationTargetException e3) {
                Log.e(TAG, "getAllocationByteCount() failed", e3);
                sGetAllocationByteCount = null;
                return bitmap.getByteCount();
            }
        }
        return bitmap.getByteCount();
    }

    private static int getSize(int width, int height) {
        if (width >= 2048 || height >= 2048) {
            return 0;
        }
        return width * height * 4;
    }

    public void addRecycledBitmap(Bitmap bitmap) {
        int key;
        if (!bitmap.isRecycled() && bitmap.getConfig() == Bitmap.Config.ARGB_8888 && (key = getSize(bitmap)) != 0) {
            synchronized (this.mRecycled8888) {
                ArrayList<SoftReference<Bitmap>> list = this.mRecycled8888.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    this.mRecycled8888.put(key, list);
                }
                list.add(new SoftReference(bitmap));
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap getRecycledBitmap(int r6, int r7) {
        /*
            r5 = this;
            int r0 = getSize(r6, r7)
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            android.util.SparseArray<java.util.ArrayList<java.lang.ref.SoftReference<android.graphics.Bitmap>>> r2 = r5.mRecycled8888
            monitor-enter(r2)
            android.util.SparseArray<java.util.ArrayList<java.lang.ref.SoftReference<android.graphics.Bitmap>>> r3 = r5.mRecycled8888     // Catch:{ all -> 0x0022 }
            java.lang.Object r3 = r3.get(r0)     // Catch:{ all -> 0x0022 }
            java.util.ArrayList r3 = (java.util.ArrayList) r3     // Catch:{ all -> 0x0022 }
            android.graphics.Bitmap r3 = getRecycledBitmap(r3)     // Catch:{ all -> 0x0022 }
            java.lang.reflect.Method r4 = sGetAllocationByteCount     // Catch:{ all -> 0x0022 }
            if (r4 == 0) goto L_0x0020
            if (r3 == 0) goto L_0x001e
            goto L_0x0020
        L_0x001e:
            monitor-exit(r2)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x0020:
            monitor-exit(r2)     // Catch:{ all -> 0x0022 }
            return r3
        L_0x0022:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.RecycleBitmapPool.getRecycledBitmap(int, int):android.graphics.Bitmap");
    }

    private static Bitmap getRecycledBitmap(ArrayList<SoftReference<Bitmap>> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        while (!list.isEmpty()) {
            Bitmap bitmap = list.remove(list.size() - 1).get();
            if (bitmap != null && !bitmap.isRecycled()) {
                return bitmap;
            }
        }
        return null;
    }
}
