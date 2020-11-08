package com.android.tv.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class AsyncDbTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static final boolean DEBUG = false;
    private static final String TAG = "AsyncDbTask";
    boolean mCalledExecuteOnDbThread;
    private final Executor mExecutor;

    public interface CursorFilter extends Filter<Cursor> {
    }

    protected AsyncDbTask(Executor mExecutor2) {
        this.mExecutor = mExecutor2;
    }

    public static abstract class AsyncQueryTask<Result> extends AsyncDbTask<Void, Void, Result> {
        private final WeakReference<Context> mContextReference;
        private final String mOrderBy;
        private String[] mProjection;
        private final String mSelection;
        private final String[] mSelectionArgs;
        private final Uri mUri;

        /* access modifiers changed from: protected */
        @WorkerThread
        public abstract Result onQuery(Cursor cursor);

        public AsyncQueryTask(Executor executor, Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
            super(executor);
            this.mContextReference = new WeakReference<>(context);
            this.mUri = uri;
            this.mProjection = projection;
            this.mSelection = selection;
            this.mSelectionArgs = selectionArgs;
            this.mOrderBy = orderBy;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0060, code lost:
            r3 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0061, code lost:
            r4 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x007f, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0080, code lost:
            r8 = r4;
            r4 = r3;
            r3 = r8;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final Result doInBackground(java.lang.Void... r10) {
            /*
                r9 = this;
                boolean r0 = r9.mCalledExecuteOnDbThread
                if (r0 != 0) goto L_0x0026
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r9)
                java.lang.String r2 = " should only be executed using executeOnDbThread, but it was called on thread "
                r1.append(r2)
                java.lang.Thread r2 = java.lang.Thread.currentThread()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                java.lang.String r1 = "AsyncDbTask"
                android.util.Log.w(r1, r0)
            L_0x0026:
                boolean r0 = r9.isCancelled()
                r1 = 0
                if (r0 == 0) goto L_0x002e
                return r1
            L_0x002e:
                java.lang.ref.WeakReference<android.content.Context> r0 = r9.mContextReference
                java.lang.Object r0 = r0.get()
                android.content.Context r0 = (android.content.Context) r0
                if (r0 != 0) goto L_0x0039
                return r1
            L_0x0039:
                android.content.ContentResolver r2 = r0.getContentResolver()     // Catch:{ Exception -> 0x009b }
                android.net.Uri r3 = r9.mUri     // Catch:{ Exception -> 0x009b }
                java.lang.String[] r4 = r9.mProjection     // Catch:{ Exception -> 0x009b }
                java.lang.String r5 = r9.mSelection     // Catch:{ Exception -> 0x009b }
                java.lang.String[] r6 = r9.mSelectionArgs     // Catch:{ Exception -> 0x009b }
                java.lang.String r7 = r9.mOrderBy     // Catch:{ Exception -> 0x009b }
                android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x009b }
                if (r2 == 0) goto L_0x0065
                boolean r3 = r9.isCancelled()     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                if (r3 != 0) goto L_0x0065
                java.lang.Object r3 = r9.onQuery(r2)     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                if (r2 == 0) goto L_0x005f
                r2.close()     // Catch:{ Exception -> 0x009b }
            L_0x005f:
                return r3
            L_0x0060:
                r3 = move-exception
                r4 = r1
                goto L_0x0083
            L_0x0063:
                r3 = move-exception
                goto L_0x007e
            L_0x0065:
                if (r2 != 0) goto L_0x0094
                java.lang.String r3 = "AsyncDbTask"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                r4.<init>()     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                java.lang.String r5 = "Unknown query error for "
                r4.append(r5)     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                r4.append(r9)     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                java.lang.String r4 = r4.toString()     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                android.util.Log.e(r3, r4)     // Catch:{ Throwable -> 0x0063, all -> 0x0060 }
                goto L_0x0094
            L_0x007e:
                throw r3     // Catch:{ all -> 0x007f }
            L_0x007f:
                r4 = move-exception
                r8 = r4
                r4 = r3
                r3 = r8
            L_0x0083:
                if (r2 == 0) goto L_0x0093
                if (r4 == 0) goto L_0x0090
                r2.close()     // Catch:{ Throwable -> 0x008b }
                goto L_0x0093
            L_0x008b:
                r5 = move-exception
                r4.addSuppressed(r5)     // Catch:{ Exception -> 0x009b }
                goto L_0x0093
            L_0x0090:
                r2.close()     // Catch:{ Exception -> 0x009b }
            L_0x0093:
                throw r3     // Catch:{ Exception -> 0x009b }
            L_0x0094:
                if (r2 == 0) goto L_0x009a
                r2.close()     // Catch:{ Exception -> 0x009b }
            L_0x009a:
                return r1
            L_0x009b:
                r2 = move-exception
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.tv.util.AsyncDbTask.AsyncQueryTask.doInBackground(java.lang.Void[]):java.lang.Object");
        }

        public String toString() {
            return getClass().getName() + "(" + this.mUri + ")";
        }
    }

    public static abstract class AsyncQueryListTask<T> extends AsyncQueryTask<List<T>> {
        private final CursorFilter mFilter;

        /* access modifiers changed from: protected */
        @WorkerThread
        public abstract T fromCursor(Cursor cursor);

        public AsyncQueryListTask(Executor executor, Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
            this(executor, context, uri, projection, selection, selectionArgs, orderBy, (CursorFilter) null);
        }

        public AsyncQueryListTask(Executor executor, Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy, CursorFilter filter) {
            super(executor, context, uri, projection, selection, selectionArgs, orderBy);
            this.mFilter = filter;
        }

        /* access modifiers changed from: protected */
        public final List<T> onQuery(Cursor c) {
            List<T> result = new ArrayList<>();
            while (c.moveToNext()) {
                if (isCancelled()) {
                    return null;
                }
                if (this.mFilter == null || this.mFilter.filter(c)) {
                    result.add(fromCursor(c));
                }
            }
            return result;
        }
    }

    public static abstract class AsyncQueryItemTask<T> extends AsyncQueryTask<T> {
        /* access modifiers changed from: protected */
        @WorkerThread
        public abstract T fromCursor(Cursor cursor);

        public AsyncQueryItemTask(Executor executor, Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
            super(executor, context, uri, projection, selection, selectionArgs, orderBy);
        }

        /* access modifiers changed from: protected */
        public final T onQuery(Cursor c) {
            if (!c.moveToNext() || isCancelled()) {
                return null;
            }
            T result = fromCursor(c);
            if (c.moveToNext()) {
                Log.w(AsyncDbTask.TAG, "More than one result for found for  " + this);
            }
            return result;
        }
    }

    @MainThread
    public final void executeOnDbThread(Params... params) {
        this.mCalledExecuteOnDbThread = true;
        executeOnExecutor(this.mExecutor, params);
    }
}
