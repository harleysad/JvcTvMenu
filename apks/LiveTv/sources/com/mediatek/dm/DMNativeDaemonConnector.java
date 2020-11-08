package com.mediatek.dm;

import android.os.SystemClock;
import android.util.Slog;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

final class DMNativeDaemonConnector implements Runnable {
    private static final boolean LOCAL_LOGD = true;
    private final int BUFFER_SIZE = 4096;
    private String TAG = "DMNativeDaemonConnector";
    private IDMNativeDaemonConnectorCallbacks mCallbacks;
    private OutputStream mOutputStream;
    private BlockingQueue<String> mResponseQueue;
    private String mSocket;

    class ResponseCode {
        public static final int ActionInitiated = 100;
        public static final int CommandOkay = 200;
        public static final int CommandParameterError = 501;
        public static final int CommandSyntaxError = 500;
        public static final int FailedRangeEnd = 599;
        public static final int FailedRangeStart = 400;
        public static final int OperationFailed = 400;
        public static final int UnsolicitedInformational = 600;

        ResponseCode() {
        }
    }

    DMNativeDaemonConnector(IDMNativeDaemonConnectorCallbacks callbacks, String socket, int responseQueueSize, String logTag) {
        this.mCallbacks = callbacks;
        if (logTag != null) {
            this.TAG = logTag;
        }
        this.mSocket = socket;
        this.mResponseQueue = new LinkedBlockingQueue(responseQueueSize);
    }

    public void run() {
        while (true) {
            try {
                listenToSocket();
            } catch (Exception e) {
                Slog.e(this.TAG, "Error in NativeDaemonConnector", e);
                SystemClock.sleep(MessageType.delayMillis4);
            }
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
        	at java.base/jdk.internal.util.Preconditions.outOfBounds(Unknown Source)
        	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Unknown Source)
        	at java.base/jdk.internal.util.Preconditions.checkIndex(Unknown Source)
        	at java.base/java.util.Objects.checkIndex(Unknown Source)
        	at java.base/java.util.ArrayList.get(Unknown Source)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    private void listenToSocket() throws java.io.IOException {
        /*
            r18 = this;
            r1 = r18
            r2 = 0
            java.lang.String r0 = r1.TAG
            java.lang.String r3 = "Start to listenToSocket.\n"
            android.util.Slog.i(r0, r3)
            r3 = 0
            android.net.LocalSocket r0 = new android.net.LocalSocket     // Catch:{ IOException -> 0x0152 }
            r0.<init>()     // Catch:{ IOException -> 0x0152 }
            r2 = r0
            android.net.LocalSocketAddress r0 = new android.net.LocalSocketAddress     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            java.lang.String r4 = r1.mSocket     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            android.net.LocalSocketAddress$Namespace r5 = android.net.LocalSocketAddress.Namespace.RESERVED     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r0.<init>(r4, r5)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r4 = r0
            r2.connect(r4)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            java.io.InputStream r0 = r2.getInputStream()     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r5 = r0
            monitor-enter(r18)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            java.io.OutputStream r0 = r2.getOutputStream()     // Catch:{ all -> 0x0133 }
            r1.mOutputStream = r0     // Catch:{ all -> 0x0133 }
            monitor-exit(r18)     // Catch:{ all -> 0x0133 }
            com.mediatek.dm.IDMNativeDaemonConnectorCallbacks r0 = r1.mCallbacks     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r0.onDaemonConnected()     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r6 = 4096(0x1000, float:5.74E-42)
            byte[] r0 = new byte[r6]     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r7 = r0
            byte[] r0 = new byte[r6]     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r8 = r0
            r9 = 0
            r0 = r9
        L_0x003a:
            int r10 = 4096 - r0
            int r10 = r5.read(r7, r0, r10)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            if (r10 >= 0) goto L_0x006c
            monitor-enter(r18)
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ all -> 0x0069 }
            if (r0 == 0) goto L_0x0057
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ IOException -> 0x004d }
            r0.close()     // Catch:{ IOException -> 0x004d }
            goto L_0x0055
        L_0x004d:
            r0 = move-exception
            java.lang.String r4 = r1.TAG     // Catch:{ all -> 0x0069 }
            java.lang.String r5 = "Failed closing output stream"
            android.util.Slog.w(r4, r5, r0)     // Catch:{ all -> 0x0069 }
        L_0x0055:
            r1.mOutputStream = r3     // Catch:{ all -> 0x0069 }
        L_0x0057:
            monitor-exit(r18)     // Catch:{ all -> 0x0069 }
            r2.close()     // Catch:{ IOException -> 0x005d }
            goto L_0x0067
        L_0x005d:
            r0 = move-exception
            r3 = r0
            java.lang.String r3 = r1.TAG
            java.lang.String r4 = "Failed closing socket"
            android.util.Slog.w(r3, r4, r0)
            goto L_0x0068
        L_0x0067:
        L_0x0068:
            return
        L_0x0069:
            r0 = move-exception
            monitor-exit(r18)     // Catch:{ all -> 0x0069 }
            throw r0
        L_0x006c:
            r11 = r0
            r0 = r9
        L_0x006e:
            r12 = r0
            if (r12 >= r10) goto L_0x0119
            byte r0 = r7[r12]     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            if (r0 != 0) goto L_0x010d
            java.lang.String r0 = new java.lang.String     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            int r13 = r12 - r11
            r0.<init>(r7, r11, r13)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r13 = r0
            java.lang.String r0 = r1.TAG     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            java.lang.String r14 = "RCV <- {%s}"
            r15 = 1
            java.lang.Object[] r3 = new java.lang.Object[r15]     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r3[r9] = r13     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            java.lang.String r3 = java.lang.String.format(r14, r3)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            android.util.Slog.d(r0, r3)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r0 = r9
        L_0x008e:
            int r3 = r12 - r11
            if (r0 >= r3) goto L_0x009b
            int r3 = r11 + r0
            byte r3 = r7[r3]     // Catch:{ IOException -> 0x0152 }
            r8[r0] = r3     // Catch:{ IOException -> 0x0152 }
            int r0 = r0 + 1
            goto L_0x008e
        L_0x009b:
            java.lang.String r0 = " \""
            java.lang.String[] r0 = r13.split(r0)     // Catch:{ IOException -> 0x014a, all -> 0x0143 }
            r3 = r0
            r0 = r3[r9]     // Catch:{ NumberFormatException -> 0x00f6 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x00f6 }
            r14 = r0
            r0 = 600(0x258, float:8.41E-43)
            if (r14 < r0) goto L_0x00e3
            com.mediatek.dm.IDMNativeDaemonConnectorCallbacks r0 = r1.mCallbacks     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
            boolean r0 = r0.onEvent(r14, r8, r3)     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
            if (r0 != 0) goto L_0x00c6
            java.lang.String r0 = r1.TAG     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
            java.lang.String r6 = "Unhandled event (%s)"
            java.lang.Object[] r9 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
            r16 = 0
            r9[r16] = r13     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
            java.lang.String r6 = java.lang.String.format(r6, r9)     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
            android.util.Slog.w(r0, r6)     // Catch:{ Exception -> 0x00ce, NumberFormatException -> 0x00ca }
        L_0x00c6:
            r17 = r2
            goto L_0x00f5
        L_0x00ca:
            r0 = move-exception
            r17 = r2
            goto L_0x00f9
        L_0x00ce:
            r0 = move-exception
            java.lang.String r6 = r1.TAG     // Catch:{ NumberFormatException -> 0x00f6 }
            java.lang.String r9 = "Error handling '%s'"
            r17 = r2
            java.lang.Object[] r2 = new java.lang.Object[r15]     // Catch:{ NumberFormatException -> 0x00eb }
            r16 = 0
            r2[r16] = r13     // Catch:{ NumberFormatException -> 0x00eb }
            java.lang.String r2 = java.lang.String.format(r9, r2)     // Catch:{ NumberFormatException -> 0x00eb }
            android.util.Slog.e(r6, r2, r0)     // Catch:{ NumberFormatException -> 0x00eb }
            goto L_0x00f5
        L_0x00e3:
            r17 = r2
            java.util.concurrent.BlockingQueue<java.lang.String> r0 = r1.mResponseQueue     // Catch:{ InterruptedException -> 0x00ed }
            r0.put(r13)     // Catch:{ InterruptedException -> 0x00ed }
            goto L_0x00f5
        L_0x00eb:
            r0 = move-exception
            goto L_0x00f9
        L_0x00ed:
            r0 = move-exception
            java.lang.String r2 = r1.TAG     // Catch:{ NumberFormatException -> 0x00eb }
            java.lang.String r6 = "Failed to put response onto queue"
            android.util.Slog.e(r2, r6, r0)     // Catch:{ NumberFormatException -> 0x00eb }
        L_0x00f5:
            goto L_0x0109
        L_0x00f6:
            r0 = move-exception
            r17 = r2
        L_0x00f9:
            java.lang.String r2 = r1.TAG     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
            java.lang.String r6 = "Bad msg (%s)"
            java.lang.Object[] r9 = new java.lang.Object[r15]     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
            r14 = 0
            r9[r14] = r13     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
            java.lang.String r6 = java.lang.String.format(r6, r9)     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
            android.util.Slog.w(r2, r6)     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
        L_0x0109:
            int r0 = r12 + 1
            r11 = r0
            goto L_0x010f
        L_0x010d:
            r17 = r2
        L_0x010f:
            int r0 = r12 + 1
            r2 = r17
            r3 = 0
            r6 = 4096(0x1000, float:5.74E-42)
            r9 = 0
            goto L_0x006e
        L_0x0119:
            r17 = r2
            if (r11 == r10) goto L_0x0127
            r2 = 4096(0x1000, float:5.74E-42)
            int r6 = 4096 - r11
            r3 = 0
            java.lang.System.arraycopy(r7, r11, r7, r3, r6)     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
            r0 = r6
            goto L_0x012b
        L_0x0127:
            r2 = 4096(0x1000, float:5.74E-42)
            r3 = 0
            r0 = 0
        L_0x012b:
            r6 = r2
            r9 = r3
            r2 = r17
            r3 = 0
            goto L_0x003a
        L_0x0133:
            r0 = move-exception
            r17 = r2
        L_0x0136:
            monitor-exit(r18)     // Catch:{ all -> 0x0141 }
            throw r0     // Catch:{ IOException -> 0x013d, all -> 0x0138 }
        L_0x0138:
            r0 = move-exception
            r2 = r0
            r3 = r17
            goto L_0x015b
        L_0x013d:
            r0 = move-exception
            r2 = r17
            goto L_0x0153
        L_0x0141:
            r0 = move-exception
            goto L_0x0136
        L_0x0143:
            r0 = move-exception
            r17 = r2
            r2 = r0
            r3 = r17
            goto L_0x015b
        L_0x014a:
            r0 = move-exception
            r17 = r2
            goto L_0x0153
        L_0x014e:
            r0 = move-exception
            r3 = r2
            r2 = r0
            goto L_0x015b
        L_0x0152:
            r0 = move-exception
        L_0x0153:
            java.lang.String r3 = r1.TAG     // Catch:{ all -> 0x014e }
            java.lang.String r4 = "Communications error"
            android.util.Slog.e(r3, r4, r0)     // Catch:{ all -> 0x014e }
            throw r0     // Catch:{ all -> 0x014e }
        L_0x015b:
            monitor-enter(r18)
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ all -> 0x0183 }
            if (r0 == 0) goto L_0x0171
            java.io.OutputStream r0 = r1.mOutputStream     // Catch:{ IOException -> 0x0166 }
            r0.close()     // Catch:{ IOException -> 0x0166 }
            goto L_0x016e
        L_0x0166:
            r0 = move-exception
            java.lang.String r4 = r1.TAG     // Catch:{ all -> 0x0183 }
            java.lang.String r5 = "Failed closing output stream"
            android.util.Slog.w(r4, r5, r0)     // Catch:{ all -> 0x0183 }
        L_0x016e:
            r4 = 0
            r1.mOutputStream = r4     // Catch:{ all -> 0x0183 }
        L_0x0171:
            monitor-exit(r18)     // Catch:{ all -> 0x0183 }
            if (r3 == 0) goto L_0x0182
            r3.close()     // Catch:{ IOException -> 0x0178 }
            goto L_0x0182
        L_0x0178:
            r0 = move-exception
            r4 = r0
            java.lang.String r4 = r1.TAG
            java.lang.String r5 = "Failed closing socket"
            android.util.Slog.w(r4, r5, r0)
        L_0x0182:
            throw r2
        L_0x0183:
            r0 = move-exception
            monitor-exit(r18)     // Catch:{ all -> 0x0183 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.dm.DMNativeDaemonConnector.listenToSocket():void");
    }

    private void sendCommand(String command) {
        sendCommand(command, (String) null);
    }

    private void sendCommand(String command, String argument) {
        synchronized (this) {
            if (argument != null) {
                try {
                    Slog.d(this.TAG, String.format("SND -> {%s} {%s}", new Object[]{command, argument}));
                } catch (IOException ex) {
                    Slog.e(this.TAG, "IOException in sendCommand", ex);
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                Slog.d(this.TAG, String.format("SND -> {%s}", new Object[]{command}));
            }
            for (int retries = ResponseCode.CommandSyntaxError; retries > 0 && this.mOutputStream == null; retries--) {
                SystemClock.sleep(10);
            }
            if (this.mOutputStream == null) {
                Slog.e(this.TAG, "No connection to daemon", new IllegalStateException());
            } else {
                StringBuilder builder = new StringBuilder(command);
                if (argument != null) {
                    builder.append(argument);
                }
                builder.append(0);
                this.mOutputStream.write(builder.toString().getBytes());
            }
        }
    }

    public void doCommand(String cmd) throws DMNativeDaemonConnectorException {
        sendCommand(cmd);
    }
}
