package com.android.tv.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.core.AbstractPreferenceController;

public class HotwordSwitchController extends AbstractPreferenceController {
    static final String ACTION_HOTWORD_DISABLE = "com.google.android.assistant.HOTWORD_DISABLE";
    static final String ACTION_HOTWORD_ENABLE = "com.google.android.assistant.HOTWORD_ENABLE";
    static final String ASSISTANT_PGK_NAME = "com.google.android.katniss";
    static final String KEY_HOTWORD_SWITCH = "hotword_switch";
    private static final String TAG = "HotwordController";
    /* access modifiers changed from: private */
    public static final Uri URI = Uri.parse("content://com.google.android.katniss.search.searchapi.VoiceInteractionProvider/sharedvalue");
    /* access modifiers changed from: private */
    public HotwordState mHotwordState = new HotwordState();
    /* access modifiers changed from: private */
    public HotwordStateListener mHotwordStateListener = null;
    private ContentObserver mHotwordSwitchObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean selfChange) {
            onChange(selfChange, (Uri) null);
        }

        public void onChange(boolean selfChange, Uri uri) {
            new HotwordLoader().execute(new Void[0]);
        }
    };

    public interface HotwordStateListener {
        void onHotwordDisable();

        void onHotwordEnable();

        void onHotwordStateChanged();
    }

    private static class HotwordState {
        /* access modifiers changed from: private */
        public boolean mHotwordEnabled;
        /* access modifiers changed from: private */
        public String mHotwordSwitchDescription;
        /* access modifiers changed from: private */
        public boolean mHotwordSwitchDisabled;
        /* access modifiers changed from: private */
        public String mHotwordSwitchTitle;
        /* access modifiers changed from: private */
        public boolean mHotwordSwitchVisible;

        private HotwordState() {
        }
    }

    private class HotwordLoader extends AsyncTask<Void, Void, HotwordState> {
        private HotwordLoader() {
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00f6, code lost:
            r4 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x00f7, code lost:
            r5 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x00fb, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x00fc, code lost:
            r12 = r5;
            r5 = r4;
            r4 = r12;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.android.tv.settings.HotwordSwitchController.HotwordState doInBackground(java.lang.Void... r14) {
            /*
                r13 = this;
                com.android.tv.settings.HotwordSwitchController$HotwordState r0 = new com.android.tv.settings.HotwordSwitchController$HotwordState
                r1 = 0
                r0.<init>()
                com.android.tv.settings.HotwordSwitchController r2 = com.android.tv.settings.HotwordSwitchController.this
                android.content.Context r2 = r2.mContext
                android.content.Context r2 = r2.getApplicationContext()
                android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x0116 }
                android.net.Uri r4 = com.android.tv.settings.HotwordSwitchController.URI     // Catch:{ Exception -> 0x0116 }
                r5 = 0
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0116 }
                if (r3 == 0) goto L_0x0110
                java.lang.String r4 = "key"
                int r4 = r3.getColumnIndex(r4)     // Catch:{ Throwable -> 0x00f9, all -> 0x00f6 }
                java.lang.String r5 = "value"
                int r5 = r3.getColumnIndex(r5)     // Catch:{ Throwable -> 0x00f9, all -> 0x00f6 }
                if (r4 < 0) goto L_0x00ef
                if (r5 >= 0) goto L_0x0035
                goto L_0x00ef
            L_0x0035:
                boolean r6 = r3.moveToNext()     // Catch:{ Throwable -> 0x00f9, all -> 0x00f6 }
                if (r6 == 0) goto L_0x00e8
                java.lang.String r6 = r3.getString(r4)     // Catch:{ Throwable -> 0x00f9, all -> 0x00f6 }
                java.lang.String r7 = r3.getString(r5)     // Catch:{ Throwable -> 0x00f9, all -> 0x00f6 }
                if (r6 == 0) goto L_0x0035
                if (r7 != 0) goto L_0x0048
                goto L_0x0035
            L_0x0048:
                r8 = -1
                int r9 = r6.hashCode()     // Catch:{ NumberFormatException -> 0x00de }
                r10 = 0
                r11 = 1
                switch(r9) {
                    case -1895606908: goto L_0x007b;
                    case -982394891: goto L_0x0071;
                    case -247039864: goto L_0x0067;
                    case 52524970: goto L_0x005d;
                    case 929681369: goto L_0x0053;
                    default: goto L_0x0052;
                }     // Catch:{ NumberFormatException -> 0x00de }
            L_0x0052:
                goto L_0x0084
            L_0x0053:
                java.lang.String r9 = "hotword_switch_description"
                boolean r9 = r6.equals(r9)     // Catch:{ NumberFormatException -> 0x00de }
                if (r9 == 0) goto L_0x0084
                r8 = 4
                goto L_0x0084
            L_0x005d:
                java.lang.String r9 = "is_hotword_switch_disabled"
                boolean r9 = r6.equals(r9)     // Catch:{ NumberFormatException -> 0x00de }
                if (r9 == 0) goto L_0x0084
                r8 = 2
                goto L_0x0084
            L_0x0067:
                java.lang.String r9 = "is_listening_for_hotword"
                boolean r9 = r6.equals(r9)     // Catch:{ NumberFormatException -> 0x00de }
                if (r9 == 0) goto L_0x0084
                r8 = r10
                goto L_0x0084
            L_0x0071:
                java.lang.String r9 = "hotword_switch_title"
                boolean r9 = r6.equals(r9)     // Catch:{ NumberFormatException -> 0x00de }
                if (r9 == 0) goto L_0x0084
                r8 = 3
                goto L_0x0084
            L_0x007b:
                java.lang.String r9 = "is_hotword_switch_visible"
                boolean r9 = r6.equals(r9)     // Catch:{ NumberFormatException -> 0x00de }
                if (r9 == 0) goto L_0x0084
                r8 = r11
            L_0x0084:
                switch(r8) {
                    case 0: goto L_0x00cd;
                    case 1: goto L_0x00bb;
                    case 2: goto L_0x00a9;
                    case 3: goto L_0x0092;
                    case 4: goto L_0x0088;
                    default: goto L_0x0087;
                }     // Catch:{ NumberFormatException -> 0x00de }
            L_0x0087:
                goto L_0x00dd
            L_0x0088:
                com.android.tv.settings.HotwordSwitchController r8 = com.android.tv.settings.HotwordSwitchController.this     // Catch:{ NumberFormatException -> 0x00de }
                java.lang.String r8 = r8.getLocalizedStringResource(r7, r1)     // Catch:{ NumberFormatException -> 0x00de }
                java.lang.String unused = r0.mHotwordSwitchDescription = r8     // Catch:{ NumberFormatException -> 0x00de }
                goto L_0x00dd
            L_0x0092:
                com.android.tv.settings.HotwordSwitchController r8 = com.android.tv.settings.HotwordSwitchController.this     // Catch:{ NumberFormatException -> 0x00de }
                com.android.tv.settings.HotwordSwitchController r9 = com.android.tv.settings.HotwordSwitchController.this     // Catch:{ NumberFormatException -> 0x00de }
                android.content.Context r9 = r9.mContext     // Catch:{ NumberFormatException -> 0x00de }
                r10 = 2131690190(0x7f0f02ce, float:1.9009417E38)
                java.lang.String r9 = r9.getString(r10)     // Catch:{ NumberFormatException -> 0x00de }
                java.lang.String r8 = r8.getLocalizedStringResource(r7, r9)     // Catch:{ NumberFormatException -> 0x00de }
                java.lang.String unused = r0.mHotwordSwitchTitle = r8     // Catch:{ NumberFormatException -> 0x00de }
                goto L_0x00dd
            L_0x00a9:
                java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ NumberFormatException -> 0x00de }
                int r8 = r8.intValue()     // Catch:{ NumberFormatException -> 0x00de }
                if (r8 != r11) goto L_0x00b6
                r10 = r11
                goto L_0x00b7
            L_0x00b6:
            L_0x00b7:
                boolean unused = r0.mHotwordSwitchDisabled = r10     // Catch:{ NumberFormatException -> 0x00de }
                goto L_0x00dd
            L_0x00bb:
                java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ NumberFormatException -> 0x00de }
                int r8 = r8.intValue()     // Catch:{ NumberFormatException -> 0x00de }
                if (r8 != r11) goto L_0x00c8
                r10 = r11
                goto L_0x00c9
            L_0x00c8:
            L_0x00c9:
                boolean unused = r0.mHotwordSwitchVisible = r10     // Catch:{ NumberFormatException -> 0x00de }
                goto L_0x00dd
            L_0x00cd:
                java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ NumberFormatException -> 0x00de }
                int r8 = r8.intValue()     // Catch:{ NumberFormatException -> 0x00de }
                if (r8 != r11) goto L_0x00d9
                r10 = r11
            L_0x00d9:
                boolean unused = r0.mHotwordEnabled = r10     // Catch:{ NumberFormatException -> 0x00de }
            L_0x00dd:
                goto L_0x00e6
            L_0x00de:
                r8 = move-exception
                java.lang.String r9 = "HotwordController"
                java.lang.String r10 = "Invalid value."
                android.util.Log.w(r9, r10, r8)     // Catch:{ Throwable -> 0x00f9, all -> 0x00f6 }
            L_0x00e6:
                goto L_0x0035
            L_0x00e8:
                if (r3 == 0) goto L_0x00ee
                r3.close()     // Catch:{ Exception -> 0x0116 }
            L_0x00ee:
                return r0
            L_0x00ef:
                if (r3 == 0) goto L_0x00f5
                r3.close()     // Catch:{ Exception -> 0x0116 }
            L_0x00f5:
                return r1
            L_0x00f6:
                r4 = move-exception
                r5 = r1
                goto L_0x00ff
            L_0x00f9:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x00fb }
            L_0x00fb:
                r5 = move-exception
                r12 = r5
                r5 = r4
                r4 = r12
            L_0x00ff:
                if (r3 == 0) goto L_0x010f
                if (r5 == 0) goto L_0x010c
                r3.close()     // Catch:{ Throwable -> 0x0107 }
                goto L_0x010f
            L_0x0107:
                r6 = move-exception
                r5.addSuppressed(r6)     // Catch:{ Exception -> 0x0116 }
                goto L_0x010f
            L_0x010c:
                r3.close()     // Catch:{ Exception -> 0x0116 }
            L_0x010f:
                throw r4     // Catch:{ Exception -> 0x0116 }
            L_0x0110:
                if (r3 == 0) goto L_0x0115
                r3.close()     // Catch:{ Exception -> 0x0116 }
            L_0x0115:
                goto L_0x011e
            L_0x0116:
                r3 = move-exception
                java.lang.String r4 = "HotwordController"
                java.lang.String r5 = "Exception loading hotword state."
                android.util.Log.e(r4, r5, r3)
            L_0x011e:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.HotwordSwitchController.HotwordLoader.doInBackground(java.lang.Void[]):com.android.tv.settings.HotwordSwitchController$HotwordState");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(HotwordState hotwordState) {
            if (hotwordState != null) {
                HotwordState unused = HotwordSwitchController.this.mHotwordState = hotwordState;
            }
            HotwordSwitchController.this.mHotwordStateListener.onHotwordStateChanged();
        }
    }

    public HotwordSwitchController(Context context) {
        super(context);
    }

    public void init(HotwordStateListener listener) {
        String unused = this.mHotwordState.mHotwordSwitchTitle = this.mContext.getString(R.string.hotwording_title);
        this.mHotwordStateListener = listener;
        try {
            this.mContext.getContentResolver().registerContentObserver(URI, true, this.mHotwordSwitchObserver);
            new HotwordLoader().execute(new Void[0]);
        } catch (SecurityException e) {
            Log.w(TAG, "Hotword content provider not found.", e);
        }
    }

    public void unregister() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mHotwordSwitchObserver);
    }

    public boolean isAvailable() {
        return this.mHotwordState.mHotwordSwitchVisible;
    }

    public String getPreferenceKey() {
        return KEY_HOTWORD_SWITCH;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (KEY_HOTWORD_SWITCH.equals(preference.getKey())) {
            ((SwitchPreference) preference).setChecked(this.mHotwordState.mHotwordEnabled);
            preference.setIcon(this.mHotwordState.mHotwordEnabled ? R.drawable.ic_mic_on : R.drawable.ic_mic_off);
            preference.setEnabled(!this.mHotwordState.mHotwordSwitchDisabled);
            preference.setTitle((CharSequence) this.mHotwordState.mHotwordSwitchTitle);
            preference.setSummary((CharSequence) this.mHotwordState.mHotwordSwitchDescription);
        }
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_HOTWORD_SWITCH.equals(preference.getKey())) {
            SwitchPreference hotwordSwitchPref = (SwitchPreference) preference;
            if (hotwordSwitchPref.isChecked()) {
                hotwordSwitchPref.setChecked(false);
                this.mHotwordStateListener.onHotwordEnable();
            } else {
                hotwordSwitchPref.setChecked(true);
                this.mHotwordStateListener.onHotwordDisable();
            }
        }
        return super.handlePreferenceTreeClick(preference);
    }

    /* access modifiers changed from: private */
    public String getLocalizedStringResource(String resource, String defaultValue) {
        if (TextUtils.isEmpty(resource)) {
            return defaultValue;
        }
        try {
            String[] parts = TextUtils.split(resource, ":");
            if (parts.length == 0) {
                return defaultValue;
            }
            Context targetContext = this.mContext.createPackageContext(parts[0], 0);
            int resId = targetContext.getResources().getIdentifier(resource, (String) null, (String) null);
            if (resId != 0) {
                return targetContext.getResources().getString(resId);
            }
            return defaultValue;
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException | SecurityException e) {
            Log.w(TAG, "Unable to get string resource.", e);
        }
    }
}
