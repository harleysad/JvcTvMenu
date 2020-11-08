package com.mediatek.wwtv.tvcenter.util;

public final class SomeArgs {
    private static final int MAX_POOL_SIZE = 10;
    static final int WAIT_FINISHED = 2;
    static final int WAIT_NONE = 0;
    static final int WAIT_WAITING = 1;
    private static SomeArgs sPool;
    private static Object sPoolLock = new Object();
    private static int sPoolSize;
    public Object arg1;
    public Object arg2;
    public Object arg3;
    public Object arg4;
    public Object arg5;
    public Object arg6;
    public Object arg7;
    public Object arg8;
    public Object arg9;
    public int argi1;
    public int argi2;
    public int argi3;
    public int argi4;
    public int argi5;
    public int argi6;
    private boolean mInPool;
    private SomeArgs mNext;
    int mWaitState = 0;

    private SomeArgs() {
    }

    public static SomeArgs obtain() {
        synchronized (sPoolLock) {
            if (sPoolSize > 0) {
                SomeArgs args = sPool;
                sPool = sPool.mNext;
                args.mNext = null;
                args.mInPool = false;
                sPoolSize--;
                return args;
            }
            SomeArgs someArgs = new SomeArgs();
            return someArgs;
        }
    }

    public void complete() {
        synchronized (this) {
            if (this.mWaitState == 1) {
                this.mWaitState = 2;
                notifyAll();
            } else {
                throw new IllegalStateException("Not waiting");
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0010, code lost:
        r0 = sPoolLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0012, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001a, code lost:
        if (sPoolSize >= 10) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x001c, code lost:
        r3.mNext = sPool;
        r3.mInPool = true;
        sPool = r3;
        sPoolSize++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002a, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void recycle() {
        /*
            r3 = this;
            java.lang.Object r0 = sPoolLock
            monitor-enter(r0)
            boolean r1 = r3.mInPool     // Catch:{ all -> 0x003a }
            if (r1 != 0) goto L_0x0032
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            monitor-enter(r3)
            int r0 = r3.mWaitState     // Catch:{ all -> 0x002f }
            if (r0 == 0) goto L_0x000f
            monitor-exit(r3)     // Catch:{ all -> 0x002f }
            return
        L_0x000f:
            monitor-exit(r3)     // Catch:{ all -> 0x002f }
            java.lang.Object r0 = sPoolLock
            monitor-enter(r0)
            r3.clear()     // Catch:{ all -> 0x002c }
            int r1 = sPoolSize     // Catch:{ all -> 0x002c }
            r2 = 10
            if (r1 >= r2) goto L_0x002a
            com.mediatek.wwtv.tvcenter.util.SomeArgs r1 = sPool     // Catch:{ all -> 0x002c }
            r3.mNext = r1     // Catch:{ all -> 0x002c }
            r1 = 1
            r3.mInPool = r1     // Catch:{ all -> 0x002c }
            sPool = r3     // Catch:{ all -> 0x002c }
            int r2 = sPoolSize     // Catch:{ all -> 0x002c }
            int r2 = r2 + r1
            sPoolSize = r2     // Catch:{ all -> 0x002c }
        L_0x002a:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1
        L_0x002f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x002f }
            throw r0
        L_0x0032:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x003a }
            java.lang.String r2 = "Already recycled."
            r1.<init>(r2)     // Catch:{ all -> 0x003a }
            throw r1     // Catch:{ all -> 0x003a }
        L_0x003a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.SomeArgs.recycle():void");
    }

    private void clear() {
        this.arg1 = null;
        this.arg2 = null;
        this.arg3 = null;
        this.arg4 = null;
        this.arg5 = null;
        this.arg6 = null;
        this.arg7 = null;
        this.argi1 = 0;
        this.argi2 = 0;
        this.argi3 = 0;
        this.argi4 = 0;
        this.argi5 = 0;
        this.argi6 = 0;
    }
}
