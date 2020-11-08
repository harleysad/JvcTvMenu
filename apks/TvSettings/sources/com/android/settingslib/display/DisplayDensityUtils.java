package com.android.settingslib.display;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.WindowManagerGlobal;
import com.android.settingslib.R;

public class DisplayDensityUtils {
    private static final String LOG_TAG = "DisplayDensityUtils";
    private static final float MAX_SCALE = 1.5f;
    private static final int MIN_DIMENSION_DP = 320;
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_SCALE_INTERVAL = 0.09f;
    private static final int[] SUMMARIES_LARGER = {R.string.screen_zoom_summary_large, R.string.screen_zoom_summary_very_large, R.string.screen_zoom_summary_extremely_large};
    private static final int[] SUMMARIES_SMALLER = {R.string.screen_zoom_summary_small};
    private static final int SUMMARY_CUSTOM = R.string.screen_zoom_summary_custom;
    public static final int SUMMARY_DEFAULT = R.string.screen_zoom_summary_default;
    private final int mCurrentIndex;
    private final int mDefaultDensity;
    private final String[] mEntries;
    private final int[] mValues;

    /* JADX WARNING: type inference failed for: r14v2, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DisplayDensityUtils(android.content.Context r24) {
        /*
            r23 = this;
            r0 = r23
            r23.<init>()
            r1 = 0
            int r2 = getDefaultDisplayDensity(r1)
            if (r2 > 0) goto L_0x0017
            r3 = 0
            r0.mEntries = r3
            r0.mValues = r3
            r0.mDefaultDensity = r1
            r1 = -1
            r0.mCurrentIndex = r1
            return
        L_0x0017:
            android.content.res.Resources r3 = r24.getResources()
            android.util.DisplayMetrics r4 = new android.util.DisplayMetrics
            r4.<init>()
            android.view.Display r5 = r24.getDisplay()
            r5.getRealMetrics(r4)
            int r5 = r4.densityDpi
            r6 = -1
            int r7 = r4.widthPixels
            int r8 = r4.heightPixels
            int r7 = java.lang.Math.min(r7, r8)
            r8 = 160(0xa0, float:2.24E-43)
            int r8 = r8 * r7
            int r8 = r8 / 320
            r9 = 1069547520(0x3fc00000, float:1.5)
            float r10 = (float) r8
            float r11 = (float) r2
            float r10 = r10 / r11
            float r9 = java.lang.Math.min(r9, r10)
            r10 = 1062836634(0x3f59999a, float:0.85)
            r11 = 1065353216(0x3f800000, float:1.0)
            float r12 = r9 - r11
            r13 = 1035489772(0x3db851ec, float:0.09)
            float r12 = r12 / r13
            int[] r13 = SUMMARIES_LARGER
            int r13 = r13.length
            float r13 = (float) r13
            r14 = 0
            float r12 = android.util.MathUtils.constrain(r12, r14, r13)
            int r12 = (int) r12
            r13 = 1070945619(0x3fd55553, float:1.6666664)
            int[] r15 = SUMMARIES_SMALLER
            int r15 = r15.length
            float r15 = (float) r15
            float r13 = android.util.MathUtils.constrain(r13, r14, r15)
            int r13 = (int) r13
            r14 = 1
            int r15 = r14 + r13
            int r15 = r15 + r12
            java.lang.String[] r15 = new java.lang.String[r15]
            int r1 = r15.length
            int[] r1 = new int[r1]
            r17 = 0
            if (r13 <= 0) goto L_0x00a1
            r18 = 1041865112(0x3e199998, float:0.14999998)
            float r14 = (float) r13
            float r18 = r18 / r14
            int r14 = r13 + -1
        L_0x0076:
            if (r14 < 0) goto L_0x00a1
            float r11 = (float) r2
            r20 = r4
            int r4 = r14 + 1
            float r4 = (float) r4
            float r4 = r4 * r18
            r19 = 1065353216(0x3f800000, float:1.0)
            float r4 = r19 - r4
            float r11 = r11 * r4
            int r4 = (int) r11
            r4 = r4 & -2
            if (r5 != r4) goto L_0x008c
            r6 = r17
        L_0x008c:
            int[] r11 = SUMMARIES_SMALLER
            r11 = r11[r14]
            java.lang.String r11 = r3.getString(r11)
            r15[r17] = r11
            r1[r17] = r4
            int r17 = r17 + 1
            int r14 = r14 + -1
            r4 = r20
            r11 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0076
        L_0x00a1:
            r20 = r4
            if (r5 != r2) goto L_0x00a7
            r6 = r17
        L_0x00a7:
            r1[r17] = r2
            int r4 = SUMMARY_DEFAULT
            java.lang.String r4 = r3.getString(r4)
            r15[r17] = r4
            r4 = 1
            int r17 = r17 + 1
            if (r12 <= 0) goto L_0x00eb
            r4 = 1065353216(0x3f800000, float:1.0)
            float r11 = r9 - r4
            float r4 = (float) r12
            float r11 = r11 / r4
            r4 = 0
        L_0x00bd:
            if (r4 >= r12) goto L_0x00e9
            float r14 = (float) r2
            r21 = r6
            int r6 = r4 + 1
            float r6 = (float) r6
            float r6 = r6 * r11
            r18 = 1065353216(0x3f800000, float:1.0)
            float r6 = r18 + r6
            float r14 = r14 * r6
            int r6 = (int) r14
            r6 = r6 & -2
            if (r5 != r6) goto L_0x00d3
            r14 = r17
            goto L_0x00d5
        L_0x00d3:
            r14 = r21
        L_0x00d5:
            r1[r17] = r6
            int[] r19 = SUMMARIES_LARGER
            r22 = r6
            r6 = r19[r4]
            java.lang.String r6 = r3.getString(r6)
            r15[r17] = r6
            int r17 = r17 + 1
            int r4 = r4 + 1
            r6 = r14
            goto L_0x00bd
        L_0x00e9:
            r21 = r6
        L_0x00eb:
            if (r6 < 0) goto L_0x00ef
            r4 = r6
            goto L_0x0114
        L_0x00ef:
            int r4 = r1.length
            r11 = 1
            int r4 = r4 + r11
            int[] r1 = java.util.Arrays.copyOf(r1, r4)
            r1[r17] = r5
            java.lang.Object[] r14 = java.util.Arrays.copyOf(r15, r4)
            r15 = r14
            java.lang.String[] r15 = (java.lang.String[]) r15
            int r14 = SUMMARY_CUSTOM
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.Integer r18 = java.lang.Integer.valueOf(r5)
            r16 = 0
            r11[r16] = r18
            java.lang.String r11 = r3.getString(r14, r11)
            r15[r17] = r11
            r4 = r17
        L_0x0114:
            r0.mDefaultDensity = r2
            r0.mCurrentIndex = r4
            r0.mEntries = r15
            r0.mValues = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.display.DisplayDensityUtils.<init>(android.content.Context):void");
    }

    public String[] getEntries() {
        return this.mEntries;
    }

    public int[] getValues() {
        return this.mValues;
    }

    public int getCurrentIndex() {
        return this.mCurrentIndex;
    }

    public int getDefaultDensity() {
        return this.mDefaultDensity;
    }

    private static int getDefaultDisplayDensity(int displayId) {
        try {
            return WindowManagerGlobal.getWindowManagerService().getInitialDisplayDensity(displayId);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public static void clearForcedDisplayDensity(int displayId) {
        AsyncTask.execute(new Runnable(displayId, UserHandle.myUserId()) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                DisplayDensityUtils.lambda$clearForcedDisplayDensity$0(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$clearForcedDisplayDensity$0(int displayId, int userId) {
        try {
            WindowManagerGlobal.getWindowManagerService().clearForcedDisplayDensityForUser(displayId, userId);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, "Unable to clear forced display density setting");
        }
    }

    public static void setForcedDisplayDensity(int displayId, int density) {
        AsyncTask.execute(new Runnable(displayId, density, UserHandle.myUserId()) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DisplayDensityUtils.lambda$setForcedDisplayDensity$1(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$setForcedDisplayDensity$1(int displayId, int density, int userId) {
        try {
            WindowManagerGlobal.getWindowManagerService().setForcedDisplayDensityForUser(displayId, density, userId);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, "Unable to save forced display density setting");
        }
    }
}
