package com.mediatek.wwtv.tvcenter.util;

public class ScreenConstant {
    public static final String ACTION_PREPARE_SHUTDOWN = "android.intent.action.ACTION_PREPARE_SHUTDOWN";
    public static final String DLNA_PROP = "mtk.force_dlna_enable";
    public static int SCREEN_HEIGHT = 0;
    public static int SCREEN_WIDTH = 0;
    public static final String SMB_PROP = "mtk.force_samba_enable";
    public static final String TAG = "ScreenConstant";
    public static final String TK_MMP_CMPB_FLAG = "use.cmpb.in.videoview";

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0055, code lost:
        com.mediatek.wwtv.tvcenter.util.MtkLog.w(TAG, r7 + "line =" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x006e, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0074, code lost:
        if (r0.length() <= 0) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return java.lang.Integer.valueOf(r0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0046, code lost:
        if (r1 != null) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0048, code lost:
        r1.destroy();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0052, code lost:
        if (r1 == null) goto L_0x0055;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getProperty(java.lang.String r7) {
        /*
            r0 = 0
            r1 = 0
            r2 = -1
            java.lang.Runtime r3 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x004e }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r4.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r5 = "getprop "
            r4.append(r5)     // Catch:{ Exception -> 0x004e }
            r4.append(r7)     // Catch:{ Exception -> 0x004e }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x004e }
            java.lang.Process r3 = r3.exec(r4)     // Catch:{ Exception -> 0x004e }
            r1 = r3
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ Exception -> 0x004e }
            java.io.InputStreamReader r4 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x004e }
            java.io.InputStream r5 = r1.getInputStream()     // Catch:{ Exception -> 0x004e }
            r4.<init>(r5)     // Catch:{ Exception -> 0x004e }
            r3.<init>(r4)     // Catch:{ Exception -> 0x004e }
            java.lang.String r4 = r3.readLine()     // Catch:{ Exception -> 0x004e }
            r0 = r4
            java.lang.String r4 = "ScreenConstant"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r5.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r6 = "line ="
            r5.append(r6)     // Catch:{ Exception -> 0x004e }
            r5.append(r0)     // Catch:{ Exception -> 0x004e }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x004e }
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r4, r5)     // Catch:{ Exception -> 0x004e }
            if (r1 == 0) goto L_0x0055
        L_0x0048:
            r1.destroy()
            goto L_0x0055
        L_0x004c:
            r3 = move-exception
            goto L_0x0082
        L_0x004e:
            r3 = move-exception
            r3.printStackTrace()     // Catch:{ all -> 0x004c }
            if (r1 == 0) goto L_0x0055
            goto L_0x0048
        L_0x0055:
            java.lang.String r3 = "ScreenConstant"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r7)
            java.lang.String r5 = "line ="
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r3, r4)
            if (r0 == 0) goto L_0x0081
            int r3 = r0.length()
            if (r3 <= 0) goto L_0x0081
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0080 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x0080 }
            r2 = r3
            goto L_0x0081
        L_0x0080:
            r3 = move-exception
        L_0x0081:
            return r2
        L_0x0082:
            if (r1 == 0) goto L_0x0087
            r1.destroy()
        L_0x0087:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.ScreenConstant.getProperty(java.lang.String):int");
    }
}
