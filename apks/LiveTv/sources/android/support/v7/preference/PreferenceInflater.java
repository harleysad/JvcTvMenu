package android.support.v7.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PreferenceInflater {
    private static final HashMap<String, Constructor> CONSTRUCTOR_MAP = new HashMap<>();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";
    private final Object[] mConstructorArgs = new Object[2];
    private final Context mContext;
    private String[] mDefaultPackages;
    private PreferenceManager mPreferenceManager;

    public PreferenceInflater(Context context, PreferenceManager preferenceManager) {
        this.mContext = context;
        init(preferenceManager);
    }

    private void init(PreferenceManager preferenceManager) {
        this.mPreferenceManager = preferenceManager;
        setDefaultPackages(new String[]{Preference.class.getPackage().getName() + ".", SwitchPreference.class.getPackage().getName() + "."});
    }

    public void setDefaultPackages(String[] defaultPackage) {
        this.mDefaultPackages = defaultPackage;
    }

    public String[] getDefaultPackages() {
        return this.mDefaultPackages;
    }

    public Context getContext() {
        return this.mContext;
    }

    public Preference inflate(int resource, @Nullable PreferenceGroup root) {
        XmlResourceParser parser = getContext().getResources().getXml(resource);
        try {
            return inflate((XmlPullParser) parser, root);
        } finally {
            parser.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x001a A[Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0030 A[SYNTHETIC, Splitter:B:14:0x0030] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.support.v7.preference.Preference inflate(org.xmlpull.v1.XmlPullParser r7, @android.support.annotation.Nullable android.support.v7.preference.PreferenceGroup r8) {
        /*
            r6 = this;
            java.lang.Object[] r0 = r6.mConstructorArgs
            monitor-enter(r0)
            android.util.AttributeSet r1 = android.util.Xml.asAttributeSet(r7)     // Catch:{ all -> 0x0081 }
            java.lang.Object[] r2 = r6.mConstructorArgs     // Catch:{ all -> 0x0081 }
            r3 = 0
            android.content.Context r4 = r6.mContext     // Catch:{ all -> 0x0081 }
            r2[r3] = r4     // Catch:{ all -> 0x0081 }
        L_0x000e:
            int r2 = r7.next()     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            r3 = 2
            if (r2 == r3) goto L_0x0018
            r4 = 1
            if (r2 != r4) goto L_0x000e
        L_0x0018:
            if (r2 != r3) goto L_0x0030
            java.lang.String r3 = r7.getName()     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            android.support.v7.preference.Preference r3 = r6.createItemFromTag(r3, r1)     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            r4 = r3
            android.support.v7.preference.PreferenceGroup r4 = (android.support.v7.preference.PreferenceGroup) r4     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            android.support.v7.preference.PreferenceGroup r4 = r6.onMergeRoots(r8, r4)     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            r6.rInflate(r7, r4, r1)     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            monitor-exit(r0)     // Catch:{ all -> 0x0081 }
            return r4
        L_0x0030:
            android.view.InflateException r3 = new android.view.InflateException     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            r4.<init>()     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            java.lang.String r5 = r7.getPositionDescription()     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            r4.append(r5)     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            java.lang.String r5 = ": No start tag found!"
            r4.append(r5)     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            java.lang.String r4 = r4.toString()     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            r3.<init>(r4)     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
            throw r3     // Catch:{ InflateException -> 0x007f, XmlPullParserException -> 0x0071, IOException -> 0x004b }
        L_0x004b:
            r2 = move-exception
            android.view.InflateException r3 = new android.view.InflateException     // Catch:{ all -> 0x0081 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0081 }
            r4.<init>()     // Catch:{ all -> 0x0081 }
            java.lang.String r5 = r7.getPositionDescription()     // Catch:{ all -> 0x0081 }
            r4.append(r5)     // Catch:{ all -> 0x0081 }
            java.lang.String r5 = ": "
            r4.append(r5)     // Catch:{ all -> 0x0081 }
            java.lang.String r5 = r2.getMessage()     // Catch:{ all -> 0x0081 }
            r4.append(r5)     // Catch:{ all -> 0x0081 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0081 }
            r3.<init>(r4)     // Catch:{ all -> 0x0081 }
            r3.initCause(r2)     // Catch:{ all -> 0x0081 }
            throw r3     // Catch:{ all -> 0x0081 }
        L_0x0071:
            r2 = move-exception
            android.view.InflateException r3 = new android.view.InflateException     // Catch:{ all -> 0x0081 }
            java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x0081 }
            r3.<init>(r4)     // Catch:{ all -> 0x0081 }
            r3.initCause(r2)     // Catch:{ all -> 0x0081 }
            throw r3     // Catch:{ all -> 0x0081 }
        L_0x007f:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0081 }
        L_0x0081:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0081 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceInflater.inflate(org.xmlpull.v1.XmlPullParser, android.support.v7.preference.PreferenceGroup):android.support.v7.preference.Preference");
    }

    @NonNull
    private PreferenceGroup onMergeRoots(PreferenceGroup givenRoot, @NonNull PreferenceGroup xmlRoot) {
        if (givenRoot != null) {
            return givenRoot;
        }
        xmlRoot.onAttachedToHierarchy(this.mPreferenceManager);
        return xmlRoot;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0034, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
        r4 = r8;
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0071, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0080, code lost:
        r2 = new android.view.InflateException(r12.getPositionDescription() + ": Error inflating class " + r10);
        r2.initCause(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a1, code lost:
        throw r2;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0071 A[Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }, ExcHandler: Exception (r1v4 'e' java.lang.Exception A[CUSTOM_DECLARE, Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }]), Splitter:B:2:0x000b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.support.v7.preference.Preference createItem(@android.support.annotation.NonNull java.lang.String r10, @android.support.annotation.Nullable java.lang.String[] r11, android.util.AttributeSet r12) throws java.lang.ClassNotFoundException, android.view.InflateException {
        /*
            r9 = this;
            java.util.HashMap<java.lang.String, java.lang.reflect.Constructor> r0 = CONSTRUCTOR_MAP
            java.lang.Object r0 = r0.get(r10)
            java.lang.reflect.Constructor r0 = (java.lang.reflect.Constructor) r0
            r1 = 1
            if (r0 != 0) goto L_0x0075
            android.content.Context r2 = r9.mContext     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.ClassLoader r2 = r2.getClassLoader()     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r3 = 0
            if (r11 == 0) goto L_0x005c
            int r4 = r11.length     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            if (r4 != 0) goto L_0x0018
            goto L_0x005c
        L_0x0018:
            r4 = 0
            int r5 = r11.length     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r6 = 0
        L_0x001b:
            if (r6 >= r5) goto L_0x0039
            r7 = r11[r6]     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0071 }
            r8.<init>()     // Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0071 }
            r8.append(r7)     // Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0071 }
            r8.append(r10)     // Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0071 }
            java.lang.String r8 = r8.toString()     // Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0071 }
            java.lang.Class r8 = r2.loadClass(r8)     // Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0071 }
            r3 = r8
            goto L_0x0039
        L_0x0034:
            r8 = move-exception
            r4 = r8
            int r6 = r6 + 1
            goto L_0x001b
        L_0x0039:
            if (r3 != 0) goto L_0x0061
            if (r4 != 0) goto L_0x005b
            android.view.InflateException r1 = new android.view.InflateException     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r5.<init>()     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.String r6 = r12.getPositionDescription()     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r5.append(r6)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.String r6 = ": Error inflating class "
            r5.append(r6)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r5.append(r10)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.String r5 = r5.toString()     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r1.<init>(r5)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            throw r1     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
        L_0x005b:
            throw r4     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
        L_0x005c:
            java.lang.Class r4 = r2.loadClass(r10)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r3 = r4
        L_0x0061:
            java.lang.Class<?>[] r4 = CONSTRUCTOR_SIGNATURE     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.reflect.Constructor r4 = r3.getConstructor(r4)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r0 = r4
            r0.setAccessible(r1)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.util.HashMap<java.lang.String, java.lang.reflect.Constructor> r4 = CONSTRUCTOR_MAP     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r4.put(r10, r0)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            goto L_0x0075
        L_0x0071:
            r1 = move-exception
            goto L_0x0080
        L_0x0073:
            r1 = move-exception
            goto L_0x00a2
        L_0x0075:
            java.lang.Object[] r2 = r9.mConstructorArgs     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            r2[r1] = r12     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            java.lang.Object r1 = r0.newInstance(r2)     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            android.support.v7.preference.Preference r1 = (android.support.v7.preference.Preference) r1     // Catch:{ ClassNotFoundException -> 0x0073, Exception -> 0x0071 }
            return r1
        L_0x0080:
            android.view.InflateException r2 = new android.view.InflateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = r12.getPositionDescription()
            r3.append(r4)
            java.lang.String r4 = ": Error inflating class "
            r3.append(r4)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            r2.initCause(r1)
            throw r2
        L_0x00a2:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceInflater.createItem(java.lang.String, java.lang.String[], android.util.AttributeSet):android.support.v7.preference.Preference");
    }

    /* access modifiers changed from: protected */
    public Preference onCreateItem(String name, AttributeSet attrs) throws ClassNotFoundException {
        return createItem(name, this.mDefaultPackages, attrs);
    }

    private Preference createItemFromTag(String name, AttributeSet attrs) {
        try {
            if (-1 == name.indexOf(46)) {
                return onCreateItem(name, attrs);
            }
            return createItem(name, (String[]) null, attrs);
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class (not found)" + name);
            ie.initCause(e2);
            throw ie;
        } catch (Exception e3) {
            InflateException ie2 = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
            ie2.initCause(e3);
            throw ie2;
        }
    }

    private void rInflate(XmlPullParser parser, Preference parent, AttributeSet attrs) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if ((next == 3 && parser.getDepth() <= depth) || type == 1) {
                return;
            }
            if (type == 2) {
                String name = parser.getName();
                if (INTENT_TAG_NAME.equals(name)) {
                    try {
                        parent.setIntent(Intent.parseIntent(getContext().getResources(), parser, attrs));
                    } catch (IOException e) {
                        XmlPullParserException ex = new XmlPullParserException("Error parsing preference");
                        ex.initCause(e);
                        throw ex;
                    }
                } else if (EXTRA_TAG_NAME.equals(name)) {
                    getContext().getResources().parseBundleExtra(EXTRA_TAG_NAME, attrs, parent.getExtras());
                    try {
                        skipCurrentTag(parser);
                    } catch (IOException e2) {
                        XmlPullParserException ex2 = new XmlPullParserException("Error parsing preference");
                        ex2.initCause(e2);
                        throw ex2;
                    }
                } else {
                    Preference item = createItemFromTag(name, attrs);
                    ((PreferenceGroup) parent).addItemFromInflater(item);
                    rInflate(parser, item, attrs);
                }
            }
        }
    }

    private static void skipCurrentTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
        }
    }
}
