package com.android.settingslib.dream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DreamBackend {
    private static final boolean DEBUG = false;
    public static final int EITHER = 2;
    public static final int NEVER = 3;
    private static final String TAG = "DreamBackend";
    public static final int WHILE_CHARGING = 0;
    public static final int WHILE_DOCKED = 1;
    private static DreamBackend sInstance;
    private final DreamInfoComparator mComparator = new DreamInfoComparator(getDefaultDream());
    private final Context mContext;
    private final IDreamManager mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
    private final boolean mDreamsActivatedOnDockByDefault = this.mContext.getResources().getBoolean(17956941);
    private final boolean mDreamsActivatedOnSleepByDefault = this.mContext.getResources().getBoolean(17956942);
    private final boolean mDreamsEnabledByDefault = this.mContext.getResources().getBoolean(17956943);

    @Retention(RetentionPolicy.SOURCE)
    public @interface WhenToDream {
    }

    public static class DreamInfo {
        public CharSequence caption;
        public ComponentName componentName;
        public Drawable icon;
        public boolean isActive;
        public ComponentName settingsComponentName;

        public String toString() {
            StringBuilder sb = new StringBuilder(DreamInfo.class.getSimpleName());
            sb.append('[');
            sb.append(this.caption);
            if (this.isActive) {
                sb.append(",active");
            }
            sb.append(',');
            sb.append(this.componentName);
            if (this.settingsComponentName != null) {
                sb.append("settings=");
                sb.append(this.settingsComponentName);
            }
            sb.append(']');
            return sb.toString();
        }
    }

    public static DreamBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DreamBackend(context);
        }
        return sInstance;
    }

    public DreamBackend(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public List<DreamInfo> getDreamInfos() {
        logd("getDreamInfos()", new Object[0]);
        ComponentName activeDream = getActiveDream();
        PackageManager pm = this.mContext.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(new Intent("android.service.dreams.DreamService"), 128);
        List<DreamInfo> dreamInfos = new ArrayList<>(resolveInfos.size());
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (resolveInfo.serviceInfo != null) {
                DreamInfo dreamInfo = new DreamInfo();
                dreamInfo.caption = resolveInfo.loadLabel(pm);
                dreamInfo.icon = resolveInfo.loadIcon(pm);
                dreamInfo.componentName = getDreamComponentName(resolveInfo);
                dreamInfo.isActive = dreamInfo.componentName.equals(activeDream);
                dreamInfo.settingsComponentName = getSettingsComponentName(pm, resolveInfo);
                dreamInfos.add(dreamInfo);
            }
        }
        Collections.sort(dreamInfos, this.mComparator);
        return dreamInfos;
    }

    public ComponentName getDefaultDream() {
        if (this.mDreamManager == null) {
            return null;
        }
        try {
            return this.mDreamManager.getDefaultDreamComponent();
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to get default dream", e);
            return null;
        }
    }

    public CharSequence getActiveDreamName() {
        ComponentName cn = getActiveDream();
        if (cn != null) {
            PackageManager pm = this.mContext.getPackageManager();
            try {
                ServiceInfo ri = pm.getServiceInfo(cn, 0);
                if (ri != null) {
                    return ri.loadLabel(pm);
                }
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    public int getWhenToDreamSetting() {
        if (!isEnabled()) {
            return 3;
        }
        if (isActivatedOnDock() && isActivatedOnSleep()) {
            return 2;
        }
        if (isActivatedOnDock()) {
            return 1;
        }
        if (isActivatedOnSleep()) {
            return 0;
        }
        return 3;
    }

    public void setWhenToDream(int whenToDream) {
        setEnabled(whenToDream != 3);
        switch (whenToDream) {
            case 0:
                setActivatedOnDock(false);
                setActivatedOnSleep(true);
                return;
            case 1:
                setActivatedOnDock(true);
                setActivatedOnSleep(false);
                return;
            case 2:
                setActivatedOnDock(true);
                setActivatedOnSleep(true);
                return;
            default:
                return;
        }
    }

    public boolean isEnabled() {
        return getBoolean("screensaver_enabled", this.mDreamsEnabledByDefault);
    }

    public void setEnabled(boolean value) {
        logd("setEnabled(%s)", Boolean.valueOf(value));
        setBoolean("screensaver_enabled", value);
    }

    public boolean isActivatedOnDock() {
        return getBoolean("screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefault);
    }

    public void setActivatedOnDock(boolean value) {
        logd("setActivatedOnDock(%s)", Boolean.valueOf(value));
        setBoolean("screensaver_activate_on_dock", value);
    }

    public boolean isActivatedOnSleep() {
        return getBoolean("screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefault);
    }

    public void setActivatedOnSleep(boolean value) {
        logd("setActivatedOnSleep(%s)", Boolean.valueOf(value));
        setBoolean("screensaver_activate_on_sleep", value);
    }

    private boolean getBoolean(String key, boolean def) {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), key, def) == 1;
    }

    private void setBoolean(String key, boolean value) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), key, value);
    }

    public void setActiveDream(ComponentName dream) {
        logd("setActiveDream(%s)", dream);
        if (this.mDreamManager != null) {
            try {
                this.mDreamManager.setDreamComponents(dream == null ? null : new ComponentName[]{dream});
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to set active dream to " + dream, e);
            }
        }
    }

    public ComponentName getActiveDream() {
        if (this.mDreamManager == null) {
            return null;
        }
        try {
            ComponentName[] dreams = this.mDreamManager.getDreamComponents();
            if (dreams == null || dreams.length <= 0) {
                return null;
            }
            return dreams[0];
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to get active dream", e);
            return null;
        }
    }

    public void launchSettings(Context uiContext, DreamInfo dreamInfo) {
        logd("launchSettings(%s)", dreamInfo);
        if (dreamInfo != null && dreamInfo.settingsComponentName != null) {
            uiContext.startActivity(new Intent().setComponent(dreamInfo.settingsComponentName));
        }
    }

    public void preview(DreamInfo dreamInfo) {
        logd("preview(%s)", dreamInfo);
        if (this.mDreamManager != null && dreamInfo != null && dreamInfo.componentName != null) {
            try {
                this.mDreamManager.testDream(dreamInfo.componentName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to preview " + dreamInfo, e);
            }
        }
    }

    public void startDreaming() {
        logd("startDreaming()", new Object[0]);
        if (this.mDreamManager != null) {
            try {
                this.mDreamManager.dream();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to dream", e);
            }
        }
    }

    private static ComponentName getDreamComponentName(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            return null;
        }
        return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006c, code lost:
        if (r2 != null) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006e, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007b, code lost:
        if (r2 == null) goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007e, code lost:
        if (r3 == null) goto L_0x009b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0080, code lost:
        android.util.Log.w(TAG, "Error parsing : " + r11.serviceInfo.packageName, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x009a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x009b, code lost:
        if (r1 == null) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a3, code lost:
        if (r1.indexOf(47) >= 0) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00a5, code lost:
        r1 = r11.serviceInfo.packageName + "/" + r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00bd, code lost:
        if (r1 != null) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        return android.content.ComponentName.unflattenFromString(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.content.ComponentName getSettingsComponentName(android.content.pm.PackageManager r10, android.content.pm.ResolveInfo r11) {
        /*
            r0 = 0
            if (r11 == 0) goto L_0x00c5
            android.content.pm.ServiceInfo r1 = r11.serviceInfo
            if (r1 == 0) goto L_0x00c5
            android.content.pm.ServiceInfo r1 = r11.serviceInfo
            android.os.Bundle r1 = r1.metaData
            if (r1 != 0) goto L_0x000f
            goto L_0x00c5
        L_0x000f:
            r1 = 0
            r2 = 0
            r3 = r0
            android.content.pm.ServiceInfo r4 = r11.serviceInfo     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            java.lang.String r5 = "android.service.dream"
            android.content.res.XmlResourceParser r4 = r4.loadXmlMetaData(r10, r5)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            r2 = r4
            if (r2 != 0) goto L_0x002b
            java.lang.String r4 = "DreamBackend"
            java.lang.String r5 = "No android.service.dream meta-data"
            android.util.Log.w(r4, r5)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            if (r2 == 0) goto L_0x002a
            r2.close()
        L_0x002a:
            return r0
        L_0x002b:
            android.content.pm.ServiceInfo r4 = r11.serviceInfo     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            android.content.pm.ApplicationInfo r4 = r4.applicationInfo     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            android.content.res.Resources r4 = r10.getResourcesForApplication(r4)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            android.util.AttributeSet r5 = android.util.Xml.asAttributeSet(r2)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
        L_0x0037:
            int r6 = r2.next()     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            r7 = r6
            r8 = 1
            if (r6 == r8) goto L_0x0043
            r6 = 2
            if (r7 == r6) goto L_0x0043
            goto L_0x0037
        L_0x0043:
            java.lang.String r6 = r2.getName()     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            java.lang.String r8 = "dream"
            boolean r8 = r8.equals(r6)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            if (r8 != 0) goto L_0x005d
            java.lang.String r8 = "DreamBackend"
            java.lang.String r9 = "Meta-data does not start with dream tag"
            android.util.Log.w(r8, r9)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            if (r2 == 0) goto L_0x005c
            r2.close()
        L_0x005c:
            return r0
        L_0x005d:
            int[] r8 = com.android.internal.R.styleable.Dream     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            android.content.res.TypedArray r8 = r4.obtainAttributes(r5, r8)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            r9 = 0
            java.lang.String r9 = r8.getString(r9)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            r1 = r9
            r8.recycle()     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0079, all -> 0x0072 }
            if (r2 == 0) goto L_0x007e
        L_0x006e:
            r2.close()
            goto L_0x007e
        L_0x0072:
            r0 = move-exception
            if (r2 == 0) goto L_0x0078
            r2.close()
        L_0x0078:
            throw r0
        L_0x0079:
            r4 = move-exception
            r3 = r4
            if (r2 == 0) goto L_0x007e
            goto L_0x006e
        L_0x007e:
            if (r3 == 0) goto L_0x009b
            java.lang.String r4 = "DreamBackend"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error parsing : "
            r5.append(r6)
            android.content.pm.ServiceInfo r6 = r11.serviceInfo
            java.lang.String r6 = r6.packageName
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            android.util.Log.w(r4, r5, r3)
            return r0
        L_0x009b:
            if (r1 == 0) goto L_0x00bd
            r4 = 47
            int r4 = r1.indexOf(r4)
            if (r4 >= 0) goto L_0x00bd
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            android.content.pm.ServiceInfo r5 = r11.serviceInfo
            java.lang.String r5 = r5.packageName
            r4.append(r5)
            java.lang.String r5 = "/"
            r4.append(r5)
            r4.append(r1)
            java.lang.String r1 = r4.toString()
        L_0x00bd:
            if (r1 != 0) goto L_0x00c0
            goto L_0x00c4
        L_0x00c0:
            android.content.ComponentName r0 = android.content.ComponentName.unflattenFromString(r1)
        L_0x00c4:
            return r0
        L_0x00c5:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.dream.DreamBackend.getSettingsComponentName(android.content.pm.PackageManager, android.content.pm.ResolveInfo):android.content.ComponentName");
    }

    private static void logd(String msg, Object... args) {
    }

    private static class DreamInfoComparator implements Comparator<DreamInfo> {
        private final ComponentName mDefaultDream;

        public DreamInfoComparator(ComponentName defaultDream) {
            this.mDefaultDream = defaultDream;
        }

        public int compare(DreamInfo lhs, DreamInfo rhs) {
            return sortKey(lhs).compareTo(sortKey(rhs));
        }

        private String sortKey(DreamInfo di) {
            StringBuilder sb = new StringBuilder();
            sb.append(di.componentName.equals(this.mDefaultDream) ? '0' : '1');
            sb.append(di.caption);
            return sb.toString();
        }
    }
}
