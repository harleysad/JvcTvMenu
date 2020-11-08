package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.Cache;
import com.android.volley.Header;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;
    private static final int DEFAULT_POOL_SIZE = 4096;
    private static final int SLOW_REQUEST_THRESHOLD_MS = 3000;
    private final BaseHttpStack mBaseHttpStack;
    @Deprecated
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    @Deprecated
    public BasicNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(4096));
    }

    @Deprecated
    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
        this.mHttpStack = httpStack;
        this.mBaseHttpStack = new AdaptedHttpStack(httpStack);
        this.mPool = pool;
    }

    public BasicNetwork(BaseHttpStack httpStack) {
        this(httpStack, new ByteArrayPool(4096));
    }

    public BasicNetwork(BaseHttpStack httpStack, ByteArrayPool pool) {
        this.mBaseHttpStack = httpStack;
        this.mHttpStack = httpStack;
        this.mPool = pool;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00bd, code lost:
        throw new java.io.IOException();
     */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0173 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00f2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.volley.NetworkResponse performRequest(com.android.volley.Request<?> r30) throws com.android.volley.VolleyError {
        /*
            r29 = this;
            r7 = r29
            r8 = r30
            long r0 = android.os.SystemClock.elapsedRealtime()
        L_0x0008:
            r9 = r0
            r1 = 0
            r2 = 0
            java.util.List r0 = java.util.Collections.emptyList()
            r3 = r0
            r11 = 0
            com.android.volley.Cache$Entry r0 = r30.getCacheEntry()     // Catch:{ SocketTimeoutException -> 0x0196, MalformedURLException -> 0x0179, IOException -> 0x00ee }
            java.util.Map r0 = r7.getCacheHeaders(r0)     // Catch:{ SocketTimeoutException -> 0x0196, MalformedURLException -> 0x0179, IOException -> 0x00ee }
            com.android.volley.toolbox.BaseHttpStack r4 = r7.mBaseHttpStack     // Catch:{ SocketTimeoutException -> 0x0196, MalformedURLException -> 0x0179, IOException -> 0x00ee }
            com.android.volley.toolbox.HttpResponse r4 = r4.executeRequest(r8, r0)     // Catch:{ SocketTimeoutException -> 0x0196, MalformedURLException -> 0x0179, IOException -> 0x00ee }
            r12 = r4
            int r1 = r12.getStatusCode()     // Catch:{ SocketTimeoutException -> 0x00eb, MalformedURLException -> 0x00e8, IOException -> 0x00e6 }
            r15 = r1
            java.util.List r1 = r12.getHeaders()     // Catch:{ SocketTimeoutException -> 0x00eb, MalformedURLException -> 0x00e8, IOException -> 0x00e6 }
            r14 = r1
            r1 = 304(0x130, float:4.26E-43)
            if (r15 != r1) goto L_0x0071
            com.android.volley.Cache$Entry r1 = r30.getCacheEntry()     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            if (r1 != 0) goto L_0x004b
            com.android.volley.NetworkResponse r3 = new com.android.volley.NetworkResponse     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            r17 = 304(0x130, float:4.26E-43)
            r18 = 0
            r19 = 1
            long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            long r20 = r4 - r9
            r16 = r3
            r22 = r14
            r16.<init>((int) r17, (byte[]) r18, (boolean) r19, (long) r20, (java.util.List<com.android.volley.Header>) r22)     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            return r3
        L_0x004b:
            java.util.List r28 = combineHeaders(r14, r1)     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            com.android.volley.NetworkResponse r3 = new com.android.volley.NetworkResponse     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            r23 = 304(0x130, float:4.26E-43)
            byte[] r4 = r1.data     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            r25 = 1
            long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            long r26 = r5 - r9
            r22 = r3
            r24 = r4
            r22.<init>((int) r23, (byte[]) r24, (boolean) r25, (long) r26, (java.util.List<com.android.volley.Header>) r28)     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            return r3
        L_0x0065:
            r0 = move-exception
            r3 = r14
            goto L_0x00f0
        L_0x0069:
            r0 = move-exception
            r3 = r14
            goto L_0x017b
        L_0x006d:
            r0 = move-exception
            r3 = r14
            goto L_0x0198
        L_0x0071:
            java.io.InputStream r1 = r12.getContent()     // Catch:{ SocketTimeoutException -> 0x00e2, MalformedURLException -> 0x00de, IOException -> 0x00db }
            r13 = r1
            if (r13 == 0) goto L_0x0082
            int r1 = r12.getContentLength()     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            byte[] r1 = r7.inputStreamToBytes(r13, r1)     // Catch:{ SocketTimeoutException -> 0x006d, MalformedURLException -> 0x0069, IOException -> 0x0065 }
            goto L_0x0084
        L_0x0082:
            byte[] r1 = new byte[r11]     // Catch:{ SocketTimeoutException -> 0x00e2, MalformedURLException -> 0x00de, IOException -> 0x00db }
        L_0x0084:
            r20 = r1
            long r1 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x00d6, MalformedURLException -> 0x00d1, IOException -> 0x00cc }
            long r21 = r1 - r9
            r1 = r7
            r2 = r21
            r4 = r8
            r5 = r20
            r6 = r15
            r1.logSlowRequests(r2, r4, r5, r6)     // Catch:{ SocketTimeoutException -> 0x00d6, MalformedURLException -> 0x00d1, IOException -> 0x00cc }
            r1 = 200(0xc8, float:2.8E-43)
            if (r15 < r1) goto L_0x00b5
            r1 = 299(0x12b, float:4.19E-43)
            if (r15 > r1) goto L_0x00b5
            com.android.volley.NetworkResponse r1 = new com.android.volley.NetworkResponse     // Catch:{ SocketTimeoutException -> 0x00d6, MalformedURLException -> 0x00d1, IOException -> 0x00cc }
            r16 = 0
            long r2 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x00d6, MalformedURLException -> 0x00d1, IOException -> 0x00cc }
            long r17 = r2 - r9
            r2 = r13
            r13 = r1
            r3 = r14
            r14 = r15
            r4 = r15
            r15 = r20
            r19 = r3
            r13.<init>((int) r14, (byte[]) r15, (boolean) r16, (long) r17, (java.util.List<com.android.volley.Header>) r19)     // Catch:{ SocketTimeoutException -> 0x00c7, MalformedURLException -> 0x00c2, IOException -> 0x00be }
            return r1
        L_0x00b5:
            r2 = r13
            r3 = r14
            r4 = r15
            java.io.IOException r1 = new java.io.IOException     // Catch:{ SocketTimeoutException -> 0x00c7, MalformedURLException -> 0x00c2, IOException -> 0x00be }
            r1.<init>()     // Catch:{ SocketTimeoutException -> 0x00c7, MalformedURLException -> 0x00c2, IOException -> 0x00be }
            throw r1     // Catch:{ SocketTimeoutException -> 0x00c7, MalformedURLException -> 0x00c2, IOException -> 0x00be }
        L_0x00be:
            r0 = move-exception
            r2 = r20
            goto L_0x00f0
        L_0x00c2:
            r0 = move-exception
            r2 = r20
            goto L_0x017b
        L_0x00c7:
            r0 = move-exception
            r2 = r20
            goto L_0x0198
        L_0x00cc:
            r0 = move-exception
            r3 = r14
            r2 = r20
            goto L_0x00dd
        L_0x00d1:
            r0 = move-exception
            r3 = r14
            r2 = r20
            goto L_0x00e0
        L_0x00d6:
            r0 = move-exception
            r3 = r14
            r2 = r20
            goto L_0x00e4
        L_0x00db:
            r0 = move-exception
            r3 = r14
        L_0x00dd:
            goto L_0x00f0
        L_0x00de:
            r0 = move-exception
            r3 = r14
        L_0x00e0:
            goto L_0x017b
        L_0x00e2:
            r0 = move-exception
            r3 = r14
        L_0x00e4:
            goto L_0x0198
        L_0x00e6:
            r0 = move-exception
            goto L_0x00f0
        L_0x00e8:
            r0 = move-exception
            goto L_0x017b
        L_0x00eb:
            r0 = move-exception
            goto L_0x0198
        L_0x00ee:
            r0 = move-exception
            r12 = r1
        L_0x00f0:
            if (r12 == 0) goto L_0x0173
            int r1 = r12.getStatusCode()
            java.lang.String r4 = "Unexpected response code %d for %s"
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r1)
            r5[r11] = r6
            r6 = 1
            java.lang.String r11 = r30.getUrl()
            r5[r6] = r11
            com.android.volley.VolleyLog.e(r4, r5)
            if (r2 == 0) goto L_0x0168
            com.android.volley.NetworkResponse r4 = new com.android.volley.NetworkResponse
            r16 = 0
            long r5 = android.os.SystemClock.elapsedRealtime()
            long r17 = r5 - r9
            r13 = r4
            r14 = r1
            r15 = r2
            r19 = r3
            r13.<init>((int) r14, (byte[]) r15, (boolean) r16, (long) r17, (java.util.List<com.android.volley.Header>) r19)
            r5 = 401(0x191, float:5.62E-43)
            if (r1 == r5) goto L_0x015d
            r5 = 403(0x193, float:5.65E-43)
            if (r1 != r5) goto L_0x0129
            goto L_0x015d
        L_0x0129:
            r5 = 400(0x190, float:5.6E-43)
            if (r1 < r5) goto L_0x0138
            r5 = 499(0x1f3, float:6.99E-43)
            if (r1 <= r5) goto L_0x0132
            goto L_0x0138
        L_0x0132:
            com.android.volley.ClientError r5 = new com.android.volley.ClientError
            r5.<init>(r4)
            throw r5
        L_0x0138:
            r5 = 500(0x1f4, float:7.0E-43)
            if (r1 < r5) goto L_0x0157
            r5 = 599(0x257, float:8.4E-43)
            if (r1 > r5) goto L_0x0157
            boolean r5 = r30.shouldRetryServerErrors()
            if (r5 == 0) goto L_0x0151
            java.lang.String r5 = "server"
            com.android.volley.ServerError r6 = new com.android.volley.ServerError
            r6.<init>(r4)
            attemptRetryOnException(r5, r8, r6)
            goto L_0x01a3
        L_0x0151:
            com.android.volley.ServerError r5 = new com.android.volley.ServerError
            r5.<init>(r4)
            throw r5
        L_0x0157:
            com.android.volley.ServerError r5 = new com.android.volley.ServerError
            r5.<init>(r4)
            throw r5
        L_0x015d:
            java.lang.String r5 = "auth"
            com.android.volley.AuthFailureError r6 = new com.android.volley.AuthFailureError
            r6.<init>((com.android.volley.NetworkResponse) r4)
            attemptRetryOnException(r5, r8, r6)
            goto L_0x01a3
        L_0x0168:
            java.lang.String r4 = "network"
            com.android.volley.NetworkError r5 = new com.android.volley.NetworkError
            r5.<init>()
            attemptRetryOnException(r4, r8, r5)
            goto L_0x01a3
        L_0x0173:
            com.android.volley.NoConnectionError r1 = new com.android.volley.NoConnectionError
            r1.<init>(r0)
            throw r1
        L_0x0179:
            r0 = move-exception
            r12 = r1
        L_0x017b:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Bad URL "
            r4.append(r5)
            java.lang.String r5 = r30.getUrl()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r1.<init>(r4, r0)
            throw r1
        L_0x0196:
            r0 = move-exception
            r12 = r1
        L_0x0198:
            java.lang.String r1 = "socket"
            com.android.volley.TimeoutError r4 = new com.android.volley.TimeoutError
            r4.<init>()
            attemptRetryOnException(r1, r8, r4)
        L_0x01a3:
            r0 = r9
            goto L_0x0008
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.toolbox.BasicNetwork.performRequest(com.android.volley.Request):com.android.volley.NetworkResponse");
    }

    private void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents, int statusCode) {
        if (DEBUG || requestLifetime > 3000) {
            Object[] objArr = new Object[5];
            objArr[0] = request;
            objArr[1] = Long.valueOf(requestLifetime);
            objArr[2] = responseContents != null ? Integer.valueOf(responseContents.length) : "null";
            objArr[3] = Integer.valueOf(statusCode);
            objArr[4] = Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount());
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", objArr);
        }
    }

    private static void attemptRetryOnException(String logPrefix, Request<?> request, VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();
        try {
            retryPolicy.retry(exception);
            request.addMarker(String.format("%s-retry [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
        } catch (VolleyError e) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
            throw e;
        }
    }

    private Map<String, String> getCacheHeaders(Cache.Entry entry) {
        if (entry == null) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new HashMap<>();
        if (entry.etag != null) {
            headers.put("If-None-Match", entry.etag);
        }
        if (entry.lastModified > 0) {
            headers.put("If-Modified-Since", HttpHeaderParser.formatEpochAsRfc1123(entry.lastModified));
        }
        return headers;
    }

    /* access modifiers changed from: protected */
    public void logError(String what, String url, long start) {
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, Long.valueOf(SystemClock.elapsedRealtime() - start), url);
    }

    private byte[] inputStreamToBytes(InputStream in, int contentLength) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(this.mPool, contentLength);
        if (in != null) {
            try {
                byte[] buffer = this.mPool.getBuf(1024);
                while (true) {
                    int read = in.read(buffer);
                    int count = read;
                    if (read == -1) {
                        break;
                    }
                    bytes.write(buffer, 0, count);
                }
                byte[] byteArray = bytes.toByteArray();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        VolleyLog.v("Error occurred when closing InputStream", new Object[0]);
                    }
                }
                this.mPool.returnBuf(buffer);
                bytes.close();
                return byteArray;
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e2) {
                        VolleyLog.v("Error occurred when closing InputStream", new Object[0]);
                    }
                }
                this.mPool.returnBuf((byte[]) null);
                bytes.close();
                throw th;
            }
        } else {
            throw new ServerError();
        }
    }

    @Deprecated
    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }

    private static List<Header> combineHeaders(List<Header> responseHeaders, Cache.Entry entry) {
        Set<String> headerNamesFromNetworkResponse = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (!responseHeaders.isEmpty()) {
            for (Header header : responseHeaders) {
                headerNamesFromNetworkResponse.add(header.getName());
            }
        }
        List<Header> combinedHeaders = new ArrayList<>(responseHeaders);
        if (entry.allResponseHeaders != null) {
            if (!entry.allResponseHeaders.isEmpty()) {
                for (Header header2 : entry.allResponseHeaders) {
                    if (!headerNamesFromNetworkResponse.contains(header2.getName())) {
                        combinedHeaders.add(header2);
                    }
                }
            }
        } else if (!entry.responseHeaders.isEmpty()) {
            for (Map.Entry<String, String> header3 : entry.responseHeaders.entrySet()) {
                if (!headerNamesFromNetworkResponse.contains(header3.getKey())) {
                    combinedHeaders.add(new Header(header3.getKey(), header3.getValue()));
                }
            }
        }
        return combinedHeaders;
    }
}
