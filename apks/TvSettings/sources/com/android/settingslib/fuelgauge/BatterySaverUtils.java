package com.android.settingslib.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Slog;

public class BatterySaverUtils {
    public static final String ACTION_SHOW_AUTO_SAVER_SUGGESTION = "PNW.autoSaverSuggestion";
    public static final String ACTION_SHOW_START_SAVER_CONFIRMATION = "PNW.startSaverConfirmation";
    private static final boolean DEBUG = false;
    private static final String SYSUI_PACKAGE = "com.android.systemui";
    private static final String TAG = "BatterySaverUtils";

    private BatterySaverUtils() {
    }

    private static class Parameters {
        private static final int AUTO_SAVER_SUGGESTION_END_NTH = 8;
        private static final int AUTO_SAVER_SUGGESTION_START_NTH = 4;
        public final int endNth;
        private final Context mContext;
        public final int startNth;

        public Parameters(Context context) {
            this.mContext = context;
            String newValue = Settings.Global.getString(this.mContext.getContentResolver(), "low_power_mode_suggestion_params");
            KeyValueListParser parser = new KeyValueListParser(',');
            try {
                parser.setString(newValue);
            } catch (IllegalArgumentException e) {
                Slog.wtf(BatterySaverUtils.TAG, "Bad constants: " + newValue);
            }
            this.startNth = parser.getInt("start_nth", 4);
            this.endNth = parser.getInt("end_nth", 8);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0059, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized boolean setPowerSaveMode(android.content.Context r7, boolean r8, boolean r9) {
        /*
            java.lang.Class<com.android.settingslib.fuelgauge.BatterySaverUtils> r0 = com.android.settingslib.fuelgauge.BatterySaverUtils.class
            monitor-enter(r0)
            android.content.ContentResolver r1 = r7.getContentResolver()     // Catch:{ all -> 0x005c }
            r2 = 0
            if (r8 == 0) goto L_0x0014
            if (r9 == 0) goto L_0x0014
            boolean r3 = maybeShowBatterySaverConfirmation(r7)     // Catch:{ all -> 0x005c }
            if (r3 == 0) goto L_0x0014
            monitor-exit(r0)
            return r2
        L_0x0014:
            if (r8 == 0) goto L_0x001b
            if (r9 != 0) goto L_0x001b
            setBatterySaverConfirmationAcknowledged(r7)     // Catch:{ all -> 0x005c }
        L_0x001b:
            java.lang.Class<android.os.PowerManager> r3 = android.os.PowerManager.class
            java.lang.Object r3 = r7.getSystemService(r3)     // Catch:{ all -> 0x005c }
            android.os.PowerManager r3 = (android.os.PowerManager) r3     // Catch:{ all -> 0x005c }
            boolean r3 = r3.setPowerSaveMode(r8)     // Catch:{ all -> 0x005c }
            if (r3 == 0) goto L_0x005a
            r3 = 1
            if (r8 == 0) goto L_0x0058
            java.lang.String r4 = "low_power_manual_activation_count"
            int r4 = android.provider.Settings.Secure.getInt(r1, r4, r2)     // Catch:{ all -> 0x005c }
            int r4 = r4 + r3
            java.lang.String r5 = "low_power_manual_activation_count"
            android.provider.Settings.Secure.putInt(r1, r5, r4)     // Catch:{ all -> 0x005c }
            com.android.settingslib.fuelgauge.BatterySaverUtils$Parameters r5 = new com.android.settingslib.fuelgauge.BatterySaverUtils$Parameters     // Catch:{ all -> 0x005c }
            r5.<init>(r7)     // Catch:{ all -> 0x005c }
            int r6 = r5.startNth     // Catch:{ all -> 0x005c }
            if (r4 < r6) goto L_0x0058
            int r6 = r5.endNth     // Catch:{ all -> 0x005c }
            if (r4 > r6) goto L_0x0058
            java.lang.String r6 = "low_power_trigger_level"
            int r6 = android.provider.Settings.Global.getInt(r1, r6, r2)     // Catch:{ all -> 0x005c }
            if (r6 != 0) goto L_0x0058
            java.lang.String r6 = "suppress_auto_battery_saver_suggestion"
            int r2 = android.provider.Settings.Secure.getInt(r1, r6, r2)     // Catch:{ all -> 0x005c }
            if (r2 != 0) goto L_0x0058
            showAutoBatterySaverSuggestion(r7)     // Catch:{ all -> 0x005c }
        L_0x0058:
            monitor-exit(r0)
            return r3
        L_0x005a:
            monitor-exit(r0)
            return r2
        L_0x005c:
            r7 = move-exception
            monitor-exit(r0)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.fuelgauge.BatterySaverUtils.setPowerSaveMode(android.content.Context, boolean, boolean):boolean");
    }

    private static boolean maybeShowBatterySaverConfirmation(Context context) {
        if (Settings.Secure.getInt(context.getContentResolver(), "low_power_warning_acknowledged", 0) != 0) {
            return false;
        }
        context.sendBroadcast(getSystemUiBroadcast(ACTION_SHOW_START_SAVER_CONFIRMATION));
        return true;
    }

    private static void showAutoBatterySaverSuggestion(Context context) {
        context.sendBroadcast(getSystemUiBroadcast(ACTION_SHOW_AUTO_SAVER_SUGGESTION));
    }

    private static Intent getSystemUiBroadcast(String action) {
        Intent i = new Intent(action);
        i.setFlags(268435456);
        i.setPackage("com.android.systemui");
        return i;
    }

    private static void setBatterySaverConfirmationAcknowledged(Context context) {
        Settings.Secure.putInt(context.getContentResolver(), "low_power_warning_acknowledged", 1);
    }

    public static void suppressAutoBatterySaver(Context context) {
        Settings.Secure.putInt(context.getContentResolver(), "suppress_auto_battery_saver_suggestion", 1);
    }

    public static void setAutoBatterySaverTriggerLevel(Context context, int level) {
        if (level > 0) {
            suppressAutoBatterySaver(context);
        }
        Settings.Global.putInt(context.getContentResolver(), "low_power_trigger_level", level);
    }

    public static void ensureAutoBatterySaver(Context context, int level) {
        if (Settings.Global.getInt(context.getContentResolver(), "low_power_trigger_level", 0) == 0) {
            setAutoBatterySaverTriggerLevel(context, level);
        }
    }
}
