package com.mediatek.wwtv.tvcenter.search;

import android.content.ContentResolver;
import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.support.media.tv.TvContractCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import com.mediatek.wwtv.tvcenter.search.LocalSearchProvider;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TvProviderSearch implements SearchInterface {
    private static final boolean DEBUG = true;
    private static final int NO_LIMIT = 0;
    private static final String TAG = "TvProviderSearch";
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    private final Context mContext;
    private final TvInputManager mTvInputManager;

    TvProviderSearch(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mTvInputManager = (TvInputManager) context.getSystemService("tv_input");
        TIFChannelManager.enableAllChannels(context);
    }

    @WorkerThread
    public List<LocalSearchProvider.SearchResult> search(String query, int limit, int action) {
        List<LocalSearchProvider.SearchResult> results = new ArrayList<>();
        HashSet hashSet = new HashSet();
        if (action == 2) {
            results.addAll(searchChannels(query, hashSet, limit));
        } else if (action == 3) {
            results.addAll(searchInputs(query, limit));
        } else {
            results.addAll(searchChannels(query, hashSet, limit));
            if (results.size() >= limit) {
                return results;
            }
            results.addAll(searchInputs(query, limit));
            if (!results.isEmpty()) {
                return results;
            }
            String str = query;
            results.addAll(searchPrograms(str, (String[]) null, new String[]{"title", TvContractCompat.ProgramColumns.COLUMN_SHORT_DESCRIPTION}, hashSet, limit - results.size()));
        }
        return results;
    }

    private void appendSelectionString(StringBuilder sb, String[] columnForExactMatching, String[] columnForPartialMatching) {
        boolean firstColumn = true;
        if (columnForExactMatching != null) {
            boolean firstColumn2 = true;
            for (String column : columnForExactMatching) {
                if (!firstColumn2) {
                    sb.append(" OR ");
                } else {
                    firstColumn2 = false;
                }
                sb.append(column);
                sb.append("=?");
            }
            firstColumn = firstColumn2;
        }
        if (columnForPartialMatching != null) {
            for (String column2 : columnForPartialMatching) {
                if (!firstColumn) {
                    sb.append(" OR ");
                } else {
                    firstColumn = false;
                }
                sb.append(column2);
                sb.append(" LIKE ?");
            }
        }
    }

    private void insertSelectionArgumentStrings(String[] selectionArgs, int pos, String query, String[] columnForExactMatching, String[] columnForPartialMatching) {
        if (columnForExactMatching != null) {
            int until = columnForExactMatching.length + pos;
            while (pos < until) {
                selectionArgs[pos] = query;
                pos++;
            }
        }
        String selectionArg = "%" + query + "%";
        if (columnForPartialMatching != null) {
            int until2 = columnForPartialMatching.length + pos;
            while (pos < until2) {
                selectionArgs[pos] = selectionArg;
                pos++;
            }
        }
    }

    @WorkerThread
    private List<LocalSearchProvider.SearchResult> searchChannels(String query, Set<Long> channels, int limit) {
        List<LocalSearchProvider.SearchResult> results = new ArrayList<>();
        if (TextUtils.isDigitsOnly(query)) {
            results.addAll(searchChannels(query, new String[]{TvContractCompat.Channels.COLUMN_DISPLAY_NUMBER}, (String[]) null, channels, 0));
            if (results.size() > 1) {
                Collections.sort(results, new ChannelComparatorWithSameDisplayNumber());
            }
        }
        if (results.size() < limit) {
            results.addAll(searchChannels(query, (String[]) null, new String[]{TvContractCompat.Channels.COLUMN_DISPLAY_NAME, "description"}, channels, limit - results.size()));
        }
        if (results.size() > limit) {
            results = results.subList(0, limit);
        }
        for (LocalSearchProvider.SearchResult result : results) {
            fillProgramInfo(result);
        }
        return results;
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0125  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.mediatek.wwtv.tvcenter.search.LocalSearchProvider.SearchResult> searchChannels(java.lang.String r25, java.lang.String[] r26, java.lang.String[] r27, java.util.Set<java.lang.Long> r28, int r29) {
        /*
            r24 = this;
            r7 = r24
            r8 = r26
            r9 = r27
            r10 = r28
            r11 = r29
            java.lang.String r0 = "_id"
            java.lang.String r1 = "display_number"
            java.lang.String r2 = "display_name"
            java.lang.String r3 = "description"
            java.lang.String[] r14 = new java.lang.String[]{r0, r1, r2, r3}
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r15 = r0
            java.lang.String r0 = "browsable"
            r15.append(r0)
            java.lang.String r0 = "=1 AND "
            r15.append(r0)
            java.lang.String r0 = "searchable"
            r15.append(r0)
            java.lang.String r0 = "=1 AND "
            r15.append(r0)
            java.lang.String r0 = "type"
            r15.append(r0)
            java.lang.String r0 = "!='TYPE_PREVIEW' AND "
            r15.append(r0)
            java.lang.String r0 = "input_id"
            r15.append(r0)
            java.lang.String r0 = "!=''"
            r15.append(r0)
            android.media.tv.TvInputManager r0 = r7.mTvInputManager
            boolean r0 = r0.isParentalControlsEnabled()
            if (r0 == 0) goto L_0x005b
            java.lang.String r0 = " AND "
            r15.append(r0)
            java.lang.String r0 = "locked"
            r15.append(r0)
            java.lang.String r0 = "=0"
            r15.append(r0)
        L_0x005b:
            java.lang.String r0 = " AND ("
            r15.append(r0)
            r7.appendSelectionString(r15, r8, r9)
            java.lang.String r0 = ")"
            r15.append(r0)
            java.lang.String r18 = r15.toString()
            r0 = 0
            if (r8 != 0) goto L_0x0071
            r1 = r0
            goto L_0x0072
        L_0x0071:
            int r1 = r8.length
        L_0x0072:
            if (r9 != 0) goto L_0x0076
            r2 = r0
            goto L_0x0077
        L_0x0076:
            int r2 = r9.length
        L_0x0077:
            int r13 = r1 + r2
            java.lang.String[] r12 = new java.lang.String[r13]
            r3 = 0
            r1 = r7
            r2 = r12
            r4 = r25
            r5 = r8
            r6 = r9
            r1.insertSelectionArgumentStrings(r2, r3, r4, r5, r6)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            android.content.ContentResolver r2 = r7.mContentResolver
            android.net.Uri r3 = android.media.tv.TvContract.Channels.CONTENT_URI
            r17 = 0
            r4 = r12
            r12 = r2
            r2 = r13
            r13 = r3
            r3 = r15
            r15 = r18
            r16 = r4
            android.database.Cursor r5 = r12.query(r13, r14, r15, r16, r17)
            if (r5 == 0) goto L_0x0129
            r12 = r0
        L_0x00a0:
            boolean r13 = r5.moveToNext()     // Catch:{ Throwable -> 0x011f, all -> 0x011c }
            if (r13 == 0) goto L_0x0129
            long r15 = r5.getLong(r0)     // Catch:{ Throwable -> 0x011f, all -> 0x011c }
            r19 = r15
            r21 = r1
            r0 = r19
            java.lang.Long r13 = java.lang.Long.valueOf(r0)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            boolean r13 = r10.contains(r13)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            if (r13 == 0) goto L_0x00bf
            r1 = r21
        L_0x00bd:
            r0 = 0
            goto L_0x00a0
        L_0x00bf:
            java.lang.Long r13 = java.lang.Long.valueOf(r0)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r10.add(r13)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            com.mediatek.wwtv.tvcenter.search.LocalSearchProvider$SearchResult r13 = new com.mediatek.wwtv.tvcenter.search.LocalSearchProvider$SearchResult     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.<init>()     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.channelId = r0     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r15 = 1
            java.lang.String r6 = r5.getString(r15)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.channelNumber = r6     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r6 = 2
            java.lang.String r6 = r5.getString(r6)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.title = r6     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r6 = 3
            java.lang.String r6 = r5.getString(r6)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.description = r6     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            java.lang.String r6 = "android.resource://com.mediatek.wwtv.tvcenter/drawable/icon"
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            java.lang.String r6 = r6.toString()     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.imageUri = r6     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            java.lang.String r6 = "android.intent.action.VIEW"
            r13.intentAction = r6     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r22 = r0
            long r0 = r13.channelId     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            java.lang.String r0 = r7.buildIntentData(r0)     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.intentData = r0     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            java.lang.String r0 = "vnd.android.cursor.item/program"
            r13.contentType = r0     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r13.isLive = r15     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r0 = -1
            r13.progressPercentage = r0     // Catch:{ Throwable -> 0x0117, all -> 0x0112 }
            r1 = r21
            r1.add(r13)     // Catch:{ Throwable -> 0x011f, all -> 0x011c }
            if (r11 == 0) goto L_0x0111
            int r12 = r12 + 1
            if (r12 < r11) goto L_0x0111
            goto L_0x0129
        L_0x0111:
            goto L_0x00bd
        L_0x0112:
            r0 = move-exception
            r1 = r21
            r6 = 0
            goto L_0x0123
        L_0x0117:
            r0 = move-exception
            r1 = r21
            r6 = r0
            goto L_0x0121
        L_0x011c:
            r0 = move-exception
            r6 = 0
            goto L_0x0123
        L_0x011f:
            r0 = move-exception
            r6 = r0
        L_0x0121:
            throw r6     // Catch:{ all -> 0x0122 }
        L_0x0122:
            r0 = move-exception
        L_0x0123:
            if (r5 == 0) goto L_0x0128
            $closeResource(r6, r5)
        L_0x0128:
            throw r0
        L_0x0129:
            if (r5 == 0) goto L_0x012f
            r6 = 0
            $closeResource(r6, r5)
        L_0x012f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.search.TvProviderSearch.searchChannels(java.lang.String, java.lang.String[], java.lang.String[], java.util.Set, int):java.util.List");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fillProgramInfo(com.mediatek.wwtv.tvcenter.search.LocalSearchProvider.SearchResult r24) {
        /*
            r23 = this;
            r8 = r23
            r9 = r24
            long r10 = java.lang.System.currentTimeMillis()
            long r1 = r9.channelId
            r3 = r10
            r5 = r10
            android.net.Uri r18 = android.media.tv.TvContract.buildProgramsUriForChannel(r1, r3, r5)
            java.lang.String r1 = "title"
            java.lang.String r2 = "poster_art_uri"
            java.lang.String r3 = "content_rating"
            java.lang.String r4 = "video_width"
            java.lang.String r5 = "video_height"
            java.lang.String r6 = "start_time_utc_millis"
            java.lang.String r7 = "end_time_utc_millis"
            java.lang.String[] r14 = new java.lang.String[]{r1, r2, r3, r4, r5, r6, r7}
            android.content.ContentResolver r12 = r8.mContentResolver
            r15 = 0
            r16 = 0
            r17 = 0
            r13 = r18
            android.database.Cursor r12 = r12.query(r13, r14, r15, r16, r17)
            if (r12 == 0) goto L_0x00a3
            boolean r0 = r12.moveToNext()     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            if (r0 == 0) goto L_0x00a3
            r0 = 2
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            boolean r0 = r8.isRatingBlocked(r0)     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            if (r0 != 0) goto L_0x00a3
            java.lang.String r3 = r9.title     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            r0 = 5
            long r0 = r12.getLong(r0)     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            r6 = r0
            r0 = 6
            long r0 = r12.getLong(r0)     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            r4 = r0
            r0 = 0
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            r9.title = r0     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            java.lang.String r2 = r9.channelNumber     // Catch:{ Throwable -> 0x0097, all -> 0x0092 }
            r1 = r8
            r19 = r4
            r4 = r6
            r21 = r14
            r13 = r6
            r6 = r19
            java.lang.String r0 = r1.buildProgramDescription(r2, r3, r4, r6)     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r9.description = r0     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r0 = 1
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            if (r0 == 0) goto L_0x0071
            r9.imageUri = r0     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
        L_0x0071:
            r1 = 3
            int r1 = r12.getInt(r1)     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r9.videoWidth = r1     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r1 = 4
            int r1 = r12.getInt(r1)     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r9.videoHeight = r1     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r1 = r19
            long r4 = r1 - r13
            r9.duration = r4     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            int r4 = r8.getProgressPercentage(r13, r1)     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            r9.progressPercentage = r4     // Catch:{ Throwable -> 0x008f, all -> 0x008c }
            goto L_0x00a5
        L_0x008c:
            r0 = move-exception
            r13 = 0
            goto L_0x009d
        L_0x008f:
            r0 = move-exception
            r13 = r0
            goto L_0x009b
        L_0x0092:
            r0 = move-exception
            r21 = r14
            r13 = 0
            goto L_0x009d
        L_0x0097:
            r0 = move-exception
            r21 = r14
            r13 = r0
        L_0x009b:
            throw r13     // Catch:{ all -> 0x009c }
        L_0x009c:
            r0 = move-exception
        L_0x009d:
            if (r12 == 0) goto L_0x00a2
            $closeResource(r13, r12)
        L_0x00a2:
            throw r0
        L_0x00a3:
            r21 = r14
        L_0x00a5:
            if (r12 == 0) goto L_0x00ab
            r1 = 0
            $closeResource(r1, r12)
        L_0x00ab:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.search.TvProviderSearch.fillProgramInfo(com.mediatek.wwtv.tvcenter.search.LocalSearchProvider$SearchResult):void");
    }

    private String buildProgramDescription(String channelNumber, String channelName, long programStartUtcMillis, long programEndUtcMillis) {
        return getDurationString(this.mContext, programStartUtcMillis, programEndUtcMillis, false) + System.lineSeparator() + channelNumber + " " + channelName;
    }

    private int getProgressPercentage(long startUtcMillis, long endUtcMillis) {
        long current = System.currentTimeMillis();
        if (startUtcMillis > current || endUtcMillis <= current) {
            return -1;
        }
        return (int) ((100 * (current - startUtcMillis)) / (endUtcMillis - startUtcMillis));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:106:0x02a3, code lost:
        $closeResource(r2, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x01d7, code lost:
        if (r15 == null) goto L_0x01e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x01d9, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:?, code lost:
        $closeResource((java.lang.Throwable) null, r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x01de, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x01df, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x01e2, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x01e3, code lost:
        r2 = r1;
        r0 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0254, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0257, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0258, code lost:
        r4 = r0;
        r1 = r18;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:53:0x01da, B:81:0x023d] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x02a3  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x023d A[SYNTHETIC, Splitter:B:81:0x023d] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0250 A[Catch:{ Throwable -> 0x0257, all -> 0x0254 }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0257 A[ExcHandler: Throwable (r0v30 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r11 r18 
  PHI: (r11v16 'c' android.database.Cursor) = (r11v17 'c' android.database.Cursor), (r11v24 'c' android.database.Cursor), (r11v24 'c' android.database.Cursor) binds: [B:81:0x023d, B:53:0x01da, B:54:?] A[DONT_GENERATE, DONT_INLINE]
  PHI: (r18v9 'sb' java.lang.StringBuilder) = (r18v10 'sb' java.lang.StringBuilder), (r18v19 'sb' java.lang.StringBuilder), (r18v19 'sb' java.lang.StringBuilder) binds: [B:81:0x023d, B:53:0x01da, B:54:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:53:0x01da] */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.mediatek.wwtv.tvcenter.search.LocalSearchProvider.SearchResult> searchPrograms(java.lang.String r36, java.lang.String[] r37, java.lang.String[] r38, java.util.Set<java.lang.Long> r39, int r40) {
        /*
            r35 = this;
            r8 = r35
            r9 = r37
            r10 = r38
            r11 = r39
            r12 = r40
            java.lang.String r13 = "channel_id"
            java.lang.String r14 = "title"
            java.lang.String r15 = "poster_art_uri"
            java.lang.String r16 = "content_rating"
            java.lang.String r17 = "video_width"
            java.lang.String r18 = "video_height"
            java.lang.String r19 = "start_time_utc_millis"
            java.lang.String r20 = "end_time_utc_millis"
            java.lang.String[] r0 = new java.lang.String[]{r13, r14, r15, r16, r17, r18, r19, r20}
            r13 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start_time_utc_millis"
            r0.append(r1)
            java.lang.String r1 = "<=? AND "
            r0.append(r1)
            java.lang.String r1 = "end_time_utc_millis"
            r0.append(r1)
            java.lang.String r1 = ">=? AND ("
            r0.append(r1)
            r8.appendSelectionString(r0, r9, r10)
            java.lang.String r1 = ")"
            r0.append(r1)
            java.lang.String r14 = r0.toString()
            r15 = 0
            if (r9 != 0) goto L_0x0049
            r1 = r15
            goto L_0x004a
        L_0x0049:
            int r1 = r9.length
        L_0x004a:
            if (r10 != 0) goto L_0x004e
            r2 = r15
            goto L_0x004f
        L_0x004e:
            int r2 = r10.length
        L_0x004f:
            int r16 = r1 + r2
            int r1 = r16 + 2
            java.lang.String[] r7 = new java.lang.String[r1]
            long r1 = java.lang.System.currentTimeMillis()
            java.lang.String r1 = java.lang.String.valueOf(r1)
            r6 = 1
            r7[r6] = r1
            r7[r15] = r1
            r3 = 2
            r1 = r8
            r2 = r7
            r4 = r36
            r5 = r9
            r6 = r10
            r1.insertSelectionArgumentStrings(r2, r3, r4, r5, r6)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r6 = r1
            java.lang.String r1 = "TvProviderSearch"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "searchPrograms("
            r2.append(r3)
            r2.append(r13)
            java.lang.String r3 = ", selection="
            r2.append(r3)
            r2.append(r14)
            r2.append(r7)
            java.lang.String r3 = ")"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r1, r2)
            android.content.ContentResolver r1 = r8.mContentResolver
            android.net.Uri r2 = android.media.tv.TvContract.Programs.CONTENT_URI
            r17 = 0
            r3 = r13
            r4 = r14
            r5 = r7
            r21 = r6
            r6 = r17
            android.database.Cursor r6 = r1.query(r2, r3, r4, r5, r6)
            if (r6 == 0) goto L_0x02a7
            r1 = r0
            r0 = r15
        L_0x00ad:
            r17 = r0
            boolean r0 = r6.moveToNext()     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            if (r0 == 0) goto L_0x0283
            long r2 = r6.getLong(r15)     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            java.lang.Long r0 = java.lang.Long.valueOf(r2)     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            boolean r0 = r11.contains(r0)     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            if (r0 == 0) goto L_0x00c7
            r0 = r17
            goto L_0x00ad
        L_0x00c7:
            java.lang.Long r0 = java.lang.Long.valueOf(r2)     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            r11.add(r0)     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            java.lang.String r0 = "_id"
            java.lang.String r5 = "display_number"
            java.lang.String r4 = "display_name"
            java.lang.String[] r24 = new java.lang.String[]{r0, r5, r4}     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            r0.<init>()     // Catch:{ Throwable -> 0x0295, all -> 0x028b }
            r4 = r0
            java.lang.String r0 = "_id"
            r4.append(r0)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            java.lang.String r0 = "=? AND "
            r4.append(r0)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            java.lang.String r0 = "browsable"
            r4.append(r0)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            java.lang.String r0 = "=1 AND "
            r4.append(r0)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            java.lang.String r0 = "searchable"
            r4.append(r0)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            java.lang.String r0 = "=1"
            r4.append(r0)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            android.media.tv.TvInputManager r0 = r8.mTvInputManager     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            boolean r0 = r0.isParentalControlsEnabled()     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            if (r0 == 0) goto L_0x0129
            java.lang.String r0 = " AND "
            r4.append(r0)     // Catch:{ Throwable -> 0x011f, all -> 0x0114 }
            java.lang.String r0 = "locked"
            r4.append(r0)     // Catch:{ Throwable -> 0x011f, all -> 0x0114 }
            java.lang.String r0 = "=0"
            r4.append(r0)     // Catch:{ Throwable -> 0x011f, all -> 0x0114 }
            goto L_0x0129
        L_0x0114:
            r0 = move-exception
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
            goto L_0x02a1
        L_0x011f:
            r0 = move-exception
            r1 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r4 = r0
            goto L_0x029c
        L_0x0129:
            java.lang.String r25 = r4.toString()     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            android.content.ContentResolver r0 = r8.mContentResolver     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            android.net.Uri r23 = android.media.tv.TvContract.Channels.CONTENT_URI     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            r5 = 1
            java.lang.String[] r1 = new java.lang.String[r5]     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            java.lang.String r18 = java.lang.String.valueOf(r2)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            r1[r15] = r18     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            r27 = 0
            r22 = r0
            r26 = r1
            android.database.Cursor r0 = r22.query(r23, r24, r25, r26, r27)     // Catch:{ Throwable -> 0x0277, all -> 0x026d }
            r1 = r0
            if (r1 == 0) goto L_0x0241
            boolean r0 = r1.moveToNext()     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            if (r0 == 0) goto L_0x0241
            r0 = 3
            java.lang.String r0 = r6.getString(r0)     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            boolean r0 = r8.isRatingBlocked(r0)     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            if (r0 != 0) goto L_0x0241
            r0 = 6
            long r18 = r6.getLong(r0)     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            r28 = r18
            r0 = 7
            long r18 = r6.getLong(r0)     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            r30 = r18
            com.mediatek.wwtv.tvcenter.search.LocalSearchProvider$SearchResult r0 = new com.mediatek.wwtv.tvcenter.search.LocalSearchProvider$SearchResult     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            r0.<init>()     // Catch:{ Throwable -> 0x022d, all -> 0x0220 }
            r32 = r2
            long r2 = r6.getLong(r15)     // Catch:{ Throwable -> 0x0212, all -> 0x0204 }
            r0.channelId = r2     // Catch:{ Throwable -> 0x0212, all -> 0x0204 }
            java.lang.String r2 = r6.getString(r5)     // Catch:{ Throwable -> 0x0212, all -> 0x0204 }
            r0.title = r2     // Catch:{ Throwable -> 0x0212, all -> 0x0204 }
            java.lang.String r2 = r1.getString(r5)     // Catch:{ Throwable -> 0x0212, all -> 0x0204 }
            r3 = 2
            java.lang.String r18 = r1.getString(r3)     // Catch:{ Throwable -> 0x0212, all -> 0x0204 }
            r15 = r1
            r1 = r8
            r9 = r32
            r11 = r3
            r3 = r18
            r18 = r4
            r4 = r28
            r11 = r6
            r19 = r7
            r6 = r30
            java.lang.String r1 = r1.buildProgramDescription(r2, r3, r4, r6)     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r0.description = r1     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r1 = 2
            java.lang.String r1 = r11.getString(r1)     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r0.imageUri = r1     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            java.lang.String r1 = "android.intent.action.VIEW"
            r0.intentAction = r1     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            java.lang.String r1 = r8.buildIntentData(r9)     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r0.intentData = r1     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            java.lang.String r1 = "vnd.android.cursor.item/program"
            r0.contentType = r1     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r1 = 1
            r0.isLive = r1     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r2 = 4
            int r2 = r11.getInt(r2)     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r0.videoWidth = r2     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r2 = 5
            int r2 = r11.getInt(r2)     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r0.videoHeight = r2     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r2 = r28
            r4 = r30
            long r6 = r4 - r2
            r0.duration = r6     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            int r6 = r8.getProgressPercentage(r2, r4)     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r0.progressPercentage = r6     // Catch:{ Throwable -> 0x01fe, all -> 0x01f8 }
            r6 = r21
            r6.add(r0)     // Catch:{ Throwable -> 0x01f4, all -> 0x01ef }
            if (r12 == 0) goto L_0x01ec
            int r7 = r17 + 1
            if (r7 < r12) goto L_0x01e8
            if (r15 == 0) goto L_0x01e2
            r1 = 0
            $closeResource(r1, r15)     // Catch:{ Throwable -> 0x0257, all -> 0x01de }
            goto L_0x01e3
        L_0x01de:
            r0 = move-exception
            r2 = r1
            goto L_0x02a1
        L_0x01e2:
            r1 = 0
        L_0x01e3:
            r2 = r1
            r0 = r18
            goto L_0x02ad
        L_0x01e8:
            r2 = 0
            r0 = r7
            goto L_0x024e
        L_0x01ec:
            r2 = 0
            goto L_0x024c
        L_0x01ef:
            r0 = move-exception
            r2 = 0
            r4 = r2
            goto L_0x023b
        L_0x01f4:
            r0 = move-exception
            r2 = 0
            r4 = r0
            goto L_0x0239
        L_0x01f8:
            r0 = move-exception
            r6 = r21
            r2 = 0
            r4 = r2
            goto L_0x023b
        L_0x01fe:
            r0 = move-exception
            r6 = r21
            r2 = 0
            r4 = r0
            goto L_0x0239
        L_0x0204:
            r0 = move-exception
            r15 = r1
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r9 = r32
            r2 = 0
            r4 = r2
            goto L_0x023b
        L_0x0212:
            r0 = move-exception
            r15 = r1
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r9 = r32
            r2 = 0
            r4 = r0
            goto L_0x0239
        L_0x0220:
            r0 = move-exception
            r15 = r1
            r9 = r2
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
            r4 = r2
            goto L_0x023b
        L_0x022d:
            r0 = move-exception
            r15 = r1
            r9 = r2
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
            r4 = r0
        L_0x0239:
            throw r4     // Catch:{ all -> 0x023a }
        L_0x023a:
            r0 = move-exception
        L_0x023b:
            if (r15 == 0) goto L_0x0240
            $closeResource(r4, r15)     // Catch:{ Throwable -> 0x0257, all -> 0x0254 }
        L_0x0240:
            throw r0     // Catch:{ Throwable -> 0x0257, all -> 0x0254 }
        L_0x0241:
            r15 = r1
            r9 = r2
            r18 = r4
            r1 = r5
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
        L_0x024c:
            r0 = r17
        L_0x024e:
            if (r15 == 0) goto L_0x025c
            $closeResource(r2, r15)     // Catch:{ Throwable -> 0x0257, all -> 0x0254 }
            goto L_0x025c
        L_0x0254:
            r0 = move-exception
            goto L_0x02a1
        L_0x0257:
            r0 = move-exception
            r4 = r0
            r1 = r18
            goto L_0x029c
        L_0x025c:
            r21 = r6
            r6 = r11
            r1 = r18
            r7 = r19
            r9 = r37
            r10 = r38
            r11 = r39
            r15 = 0
            goto L_0x00ad
        L_0x026d:
            r0 = move-exception
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
            goto L_0x02a1
        L_0x0277:
            r0 = move-exception
            r18 = r4
            r11 = r6
            r19 = r7
            r6 = r21
            r4 = r0
            r1 = r18
            goto L_0x029c
        L_0x0283:
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
            r0 = r1
            goto L_0x02ad
        L_0x028b:
            r0 = move-exception
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
            r18 = r1
            goto L_0x02a1
        L_0x0295:
            r0 = move-exception
            r11 = r6
            r19 = r7
            r6 = r21
            r4 = r0
        L_0x029c:
            throw r4     // Catch:{ all -> 0x029d }
        L_0x029d:
            r0 = move-exception
            r18 = r1
            r2 = r4
        L_0x02a1:
            if (r11 == 0) goto L_0x02a6
            $closeResource(r2, r11)
        L_0x02a6:
            throw r0
        L_0x02a7:
            r11 = r6
            r19 = r7
            r6 = r21
            r2 = 0
        L_0x02ad:
            if (r11 == 0) goto L_0x02b2
            $closeResource(r2, r11)
        L_0x02b2:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.search.TvProviderSearch.searchPrograms(java.lang.String, java.lang.String[], java.lang.String[], java.util.Set, int):java.util.List");
    }

    private String buildIntentData(long channelId) {
        return TvContract.buildChannelUri(channelId).buildUpon().appendQueryParameter("source", SearchInterface.SOURCE_TV_SEARCH).build().toString();
    }

    private boolean isRatingBlocked(String ratings) {
        if (ratings == null) {
            return false;
        }
        for (String rating : ratings.split("\\s*,\\s*")) {
            try {
                if (this.mTvInputManager.isParentalControlsEnabled() && this.mTvInputManager.isRatingBlocked(TvContentRating.unflattenFromString(rating))) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
            }
        }
        return false;
    }

    private List<LocalSearchProvider.SearchResult> searchInputs(String query, int limit) {
        Log.d(TAG, "searchInputs(" + query + ", limit=" + limit + ")");
        String query2 = canonicalizeLabel(query);
        List<TvInputInfo> inputList = this.mTvInputManager.getTvInputList();
        List<LocalSearchProvider.SearchResult> results = new ArrayList<>();
        for (TvInputInfo input : inputList) {
            if (input.getType() != 0 || input.getId().startsWith("com.mediatek.tvinput")) {
                String label = canonicalizeLabel(input.loadLabel(this.mContext));
                String customLabel = canonicalizeLabel(input.loadCustomLabel(this.mContext));
                if (TextUtils.equals(query2, label) || TextUtils.equals(query2, customLabel)) {
                    results.add(buildSearchResultForInput(input));
                    if (results.size() > 0) {
                        return results;
                    }
                }
            }
        }
        for (TvInputInfo input2 : inputList) {
            if (input2.getType() != 0 || input2.getId().startsWith("com.mediatek.tvinput")) {
                String label2 = canonicalizeLabel(input2.loadLabel(this.mContext));
                String customLabel2 = canonicalizeLabel(input2.loadCustomLabel(this.mContext));
                if ((label2 != null && label2.contains(query2)) || (customLabel2 != null && customLabel2.contains(query2))) {
                    results.add(buildSearchResultForInput(input2));
                    if (results.size() >= limit) {
                        return results;
                    }
                }
            }
        }
        return results;
    }

    private String canonicalizeLabel(CharSequence cs) {
        Locale locale = this.mContext.getResources().getConfiguration().locale;
        if (cs != null) {
            return cs.toString().replaceAll("[ -]", "").toLowerCase(locale);
        }
        return null;
    }

    private LocalSearchProvider.SearchResult buildSearchResultForInput(TvInputInfo input) {
        LocalSearchProvider.SearchResult result = new LocalSearchProvider.SearchResult();
        result.title = input.loadLabel(this.mContext).toString();
        result.imageUri = Uri.parse("android.resource://com.mediatek.wwtv.tvcenter/drawable/icon").toString();
        result.intentAction = "android.intent.action.VIEW";
        result.intentData = TvContract.buildChannelUriForPassthroughInput(input.getId()).toString();
        return result;
    }

    @WorkerThread
    private class ChannelComparatorWithSameDisplayNumber implements Comparator<LocalSearchProvider.SearchResult> {
        private final Map<Long, Long> mMaxWatchStartTimeMap;

        private ChannelComparatorWithSameDisplayNumber() {
            this.mMaxWatchStartTimeMap = new HashMap();
        }

        public int compare(LocalSearchProvider.SearchResult lhs, LocalSearchProvider.SearchResult rhs) {
            Long lhsMaxWatchStartTime = this.mMaxWatchStartTimeMap.get(Long.valueOf(lhs.channelId));
            if (lhsMaxWatchStartTime == null) {
                lhsMaxWatchStartTime = Long.valueOf(getMaxWatchStartTime(lhs.channelId));
                this.mMaxWatchStartTimeMap.put(Long.valueOf(lhs.channelId), lhsMaxWatchStartTime);
            }
            Long rhsMaxWatchStartTime = this.mMaxWatchStartTimeMap.get(Long.valueOf(rhs.channelId));
            if (rhsMaxWatchStartTime == null) {
                rhsMaxWatchStartTime = Long.valueOf(getMaxWatchStartTime(rhs.channelId));
                this.mMaxWatchStartTimeMap.put(Long.valueOf(rhs.channelId), rhsMaxWatchStartTime);
            }
            if (!Objects.equals(lhsMaxWatchStartTime, rhsMaxWatchStartTime)) {
                return Long.compare(rhsMaxWatchStartTime.longValue(), lhsMaxWatchStartTime.longValue());
            }
            return Long.compare(rhs.channelId, lhs.channelId);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0034, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0038, code lost:
            if (r0 != null) goto L_0x003a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x003a, code lost:
            if (r1 != null) goto L_0x003c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0040, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0041, code lost:
            r1.addSuppressed(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0045, code lost:
            r0.close();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private long getMaxWatchStartTime(long r11) {
            /*
                r10 = this;
                android.net.Uri r6 = android.media.tv.TvContract.WatchedPrograms.CONTENT_URI
                java.lang.String r0 = "MAX(start_time_utc_millis) AS max_watch_start_time"
                java.lang.String[] r2 = new java.lang.String[]{r0}
                java.lang.String r7 = "channel_id=?"
                r0 = 1
                java.lang.String[] r4 = new java.lang.String[r0]
                java.lang.String r0 = java.lang.Long.toString(r11)
                r8 = 0
                r4[r8] = r0
                com.mediatek.wwtv.tvcenter.search.TvProviderSearch r0 = com.mediatek.wwtv.tvcenter.search.TvProviderSearch.this
                android.content.ContentResolver r0 = r0.mContentResolver
                r5 = 0
                r1 = r6
                r3 = r7
                android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5)
                if (r0 == 0) goto L_0x0049
                r1 = 0
                boolean r3 = r0.moveToNext()     // Catch:{ Throwable -> 0x0036 }
                if (r3 == 0) goto L_0x0049
                long r8 = r0.getLong(r8)     // Catch:{ Throwable -> 0x0036 }
                if (r0 == 0) goto L_0x0033
                r0.close()
            L_0x0033:
                return r8
            L_0x0034:
                r3 = move-exception
                goto L_0x0038
            L_0x0036:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x0034 }
            L_0x0038:
                if (r0 == 0) goto L_0x0048
                if (r1 == 0) goto L_0x0045
                r0.close()     // Catch:{ Throwable -> 0x0040 }
                goto L_0x0048
            L_0x0040:
                r5 = move-exception
                r1.addSuppressed(r5)
                goto L_0x0048
            L_0x0045:
                r0.close()
            L_0x0048:
                throw r3
            L_0x0049:
                if (r0 == 0) goto L_0x004e
                r0.close()
            L_0x004e:
                r0 = -1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.search.TvProviderSearch.ChannelComparatorWithSameDisplayNumber.getMaxWatchStartTime(long):long");
        }
    }

    public static String getDurationString(Context context, long startUtcMillis, long endUtcMillis, boolean useShortFormat) {
        return getDurationString(context, System.currentTimeMillis(), startUtcMillis, endUtcMillis, useShortFormat, 0);
    }

    static String getDurationString(Context context, long baseMillis, long startUtcMillis, long endUtcMillis, boolean useShortFormat, int flag) {
        int flag2 = flag | (useShortFormat ? 131072 : 0) | 65537;
        if (!isInGivenDay(baseMillis, startUtcMillis)) {
            flag2 |= 16;
        }
        if (startUtcMillis == endUtcMillis || !useShortFormat || isInGivenDay(startUtcMillis, endUtcMillis - 1) || endUtcMillis - startUtcMillis >= TimeUnit.HOURS.toMillis(11)) {
            return DateUtils.formatDateRange(context, startUtcMillis, endUtcMillis, flag2);
        }
        return DateUtils.formatDateRange(context, startUtcMillis, endUtcMillis - TimeUnit.DAYS.toMillis(1), flag2);
    }

    public static boolean isInGivenDay(long dayToMatchInMillis, long subjectTimeInMillis) {
        long DAY_IN_MS = TimeUnit.DAYS.toMillis(1);
        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        long offset = (long) timeZone.getRawOffset();
        if (timeZone.inDaylightTime(new Date(dayToMatchInMillis))) {
            offset += (long) timeZone.getDSTSavings();
        }
        return (dayToMatchInMillis + offset) - ((dayToMatchInMillis + offset) % DAY_IN_MS) == (subjectTimeInMillis + offset) - ((subjectTimeInMillis + offset) % DAY_IN_MS);
    }
}
