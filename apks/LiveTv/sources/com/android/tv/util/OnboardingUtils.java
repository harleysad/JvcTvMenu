package com.android.tv.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;

public final class OnboardingUtils {
    private static final String MERCHANT_COLLECTION_URL_STRING = getMerchantCollectionUrl();
    private static final int ONBOARDING_VERSION = 1;
    public static final Intent ONLINE_STORE_INTENT = new Intent("android.intent.action.VIEW", Uri.parse(MERCHANT_COLLECTION_URL_STRING));
    private static final String PREF_KEY_IS_FIRST_BOOT = "pref_onbaording_is_first_boot";
    private static final String PREF_KEY_ONBOARDING_VERSION_CODE = "pref_onbaording_versionCode";

    public static boolean isFirstBoot(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_KEY_IS_FIRST_BOOT, true);
    }

    public static void setFirstBootCompleted(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_KEY_IS_FIRST_BOOT, false).apply();
    }

    public static boolean isFirstRunWithCurrentVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_KEY_ONBOARDING_VERSION_CODE, 0) != 1;
    }

    public static void setFirstRunWithCurrentVersionCompleted(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_KEY_ONBOARDING_VERSION_CODE, 1).apply();
    }

    public static boolean needToShowOnboarding(Context context) {
        return isFirstRunWithCurrentVersion(context) || !areChannelsAvailable(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0038, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003c, code lost:
        if (r4 != null) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003e, code lost:
        if (r5 != null) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0044, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0045, code lost:
        r5.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0049, code lost:
        r4.close();
     */
    @android.support.annotation.UiThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean areChannelsAvailable(android.content.Context r10) {
        /*
            com.mediatek.wwtv.tvcenter.TvSingletons r0 = com.mediatek.wwtv.tvcenter.TvSingletons.getSingletons()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r0 = r0.getChannelDataManager()
            boolean r1 = r0.isDbLoadFinished()
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0019
            int r1 = r0.getChannelCount()
            if (r1 == 0) goto L_0x0018
            r2 = r3
        L_0x0018:
            return r2
        L_0x0019:
            android.content.ContentResolver r1 = r10.getContentResolver()
            android.net.Uri r5 = android.media.tv.TvContract.Channels.CONTENT_URI
            java.lang.String r4 = "_id"
            java.lang.String[] r6 = new java.lang.String[]{r4}
            r7 = 0
            r8 = 0
            r9 = 0
            r4 = r1
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)
            r5 = 0
            if (r4 == 0) goto L_0x004d
            int r6 = r4.getCount()     // Catch:{ Throwable -> 0x003a }
            if (r6 == 0) goto L_0x004d
            r2 = r3
            goto L_0x004e
        L_0x0038:
            r2 = move-exception
            goto L_0x003c
        L_0x003a:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0038 }
        L_0x003c:
            if (r4 == 0) goto L_0x004c
            if (r5 == 0) goto L_0x0049
            r4.close()     // Catch:{ Throwable -> 0x0044 }
            goto L_0x004c
        L_0x0044:
            r3 = move-exception
            r5.addSuppressed(r3)
            goto L_0x004c
        L_0x0049:
            r4.close()
        L_0x004c:
            throw r2
        L_0x004d:
        L_0x004e:
            if (r4 == 0) goto L_0x0053
            r4.close()
        L_0x0053:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.util.OnboardingUtils.areChannelsAvailable(android.content.Context):boolean");
    }

    public static boolean areInputsAvailable(Context context) {
        if (((DestroyApp) context.getApplicationContext()).getTvInputManagerHelper().getTvInputInfos(true, false).size() > 0) {
            return true;
        }
        return false;
    }

    private static String getMerchantCollectionUrl() {
        return "https://play.google.com/store/apps";
    }
}
