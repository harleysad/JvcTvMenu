package com.android.tv.parental;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.tv.TvContentRatingSystemInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.parental.ContentRatingSystem;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class ContentRatingsParser {
    private static final String ATTR_CONTENT_AGE_HINT = "contentAgeHint";
    private static final String ATTR_COUNTRY = "country";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_VERSION_CODE = "versionCode";
    private static final boolean DEBUG = false;
    public static final String DOMAIN_SYSTEM_RATINGS = "com.android.tv";
    private static final String TAG = "ContentRatingsParser";
    private static final String TAG_RATING = "rating";
    private static final String TAG_RATING_DEFINITION = "rating-definition";
    private static final String TAG_RATING_ORDER = "rating-order";
    private static final String TAG_RATING_SYSTEM_DEFINITION = "rating-system-definition";
    private static final String TAG_RATING_SYSTEM_DEFINITIONS = "rating-system-definitions";
    private static final String TAG_SUB_RATING = "sub-rating";
    private static final String TAG_SUB_RATING_DEFINITION = "sub-rating-definition";
    private static final String VERSION_CODE = "1";
    private final Context mContext;
    private Resources mResources;
    private String mXmlVersionCode;

    public ContentRatingsParser(Context context) {
        this.mContext = context;
    }

    public List<ContentRatingSystem> parse(TvContentRatingSystemInfo info) {
        XmlResourceParser parser;
        List<ContentRatingSystem> ratingSystems = null;
        Uri uri = info.getXmlUri();
        try {
            String packageName = uri.getAuthority();
            String curPackageName = this.mContext.getPackageName();
            MtkLog.d(TAG, " packageName=" + packageName + ",curPackageName=" + curPackageName);
            if (!TextUtils.equals(packageName, curPackageName) && !TextUtils.equals(packageName, "com.android.cts.verifier")) {
                return null;
            }
            parser = this.mContext.getPackageManager().getXml(packageName, (int) ContentUris.parseId(uri), (ApplicationInfo) null);
            if (parser != null) {
                ratingSystems = parse(parser, packageName, !info.isSystemDefined());
                if (parser != null) {
                    parser.close();
                }
                return ratingSystems;
            }
            throw new IllegalArgumentException("Cannot get XML with URI " + uri);
        } catch (Exception e) {
            Log.w(TAG, "Error parsing XML " + uri, e);
        } catch (Throwable th) {
            r5.addSuppressed(th);
        }
        throw th;
    }

    private List<ContentRatingSystem> parse(XmlResourceParser parser, String domain, boolean isCustom) throws XmlPullParserException, IOException {
        try {
            this.mResources = this.mContext.getPackageManager().getResourcesForApplication(domain);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to get resources for " + domain, e);
            this.mResources = this.mContext.getResources();
        }
        if (domain.equals(this.mContext.getPackageName())) {
            domain = "com.android.tv";
        }
        do {
        } while (parser.next() == 0);
        assertEquals(parser.getEventType(), 2, "Malformed XML: Not a valid XML file");
        assertEquals(parser.getName(), TAG_RATING_SYSTEM_DEFINITIONS, "Malformed XML: Should start with tag rating-system-definitions");
        boolean hasVersionAttr = false;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (ATTR_VERSION_CODE.equals(parser.getAttributeName(i))) {
                hasVersionAttr = true;
                this.mXmlVersionCode = parser.getAttributeValue(i);
            }
        }
        if (hasVersionAttr) {
            List<ContentRatingSystem> ratingSystems = new ArrayList<>();
            while (parser.next() != 1) {
                switch (parser.getEventType()) {
                    case 2:
                        if (!TAG_RATING_SYSTEM_DEFINITION.equals(parser.getName())) {
                            checkVersion("Malformed XML: Should contains rating-system-definition");
                            break;
                        } else {
                            ratingSystems.add(parseRatingSystemDefinition(parser, domain, isCustom));
                            break;
                        }
                    case 3:
                        if (!TAG_RATING_SYSTEM_DEFINITIONS.equals(parser.getName())) {
                            checkVersion("Malformed XML: Should end with tag rating-system-definitions");
                            break;
                        } else {
                            assertEquals(parser.next(), 1, "Malformed XML: Should end with tag rating-system-definitions");
                            return ratingSystems;
                        }
                }
            }
            throw new XmlPullParserException("rating-system-definitions section is incomplete or section ending tag is missing");
        }
        throw new XmlPullParserException("Malformed XML: Should contains a version attribute in rating-system-definitions");
    }

    private static void assertEquals(int a, int b, String msg) throws XmlPullParserException {
        if (a != b) {
            throw new XmlPullParserException(msg);
        }
    }

    private static void assertEquals(String a, String b, String msg) throws XmlPullParserException {
        if (!b.equals(a)) {
            throw new XmlPullParserException(msg);
        }
    }

    private void checkVersion(String msg) throws XmlPullParserException {
        if (!VERSION_CODE.equals(this.mXmlVersionCode)) {
            throw new XmlPullParserException(msg);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0042, code lost:
        if (r3.equals("title") != false) goto L_0x005a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0166  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.tv.parental.ContentRatingSystem parseRatingSystemDefinition(android.content.res.XmlResourceParser r10, java.lang.String r11, boolean r12) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r9 = this;
            com.android.tv.parental.ContentRatingSystem$Builder r0 = new com.android.tv.parental.ContentRatingSystem$Builder
            android.content.Context r1 = r9.mContext
            r0.<init>(r1)
            r0.setDomain(r11)
            r1 = 0
            r2 = r1
        L_0x000c:
            int r3 = r10.getAttributeCount()
            r4 = 2
            r5 = -1
            r6 = 1
            if (r2 >= r3) goto L_0x00b5
            java.lang.String r3 = r10.getAttributeName(r2)
            int r7 = r3.hashCode()
            r8 = -1724546052(0xffffffff993583fc, float:-9.384135E-24)
            if (r7 == r8) goto L_0x004f
            r8 = 3373707(0x337a8b, float:4.72757E-39)
            if (r7 == r8) goto L_0x0045
            r8 = 110371416(0x6942258, float:5.5721876E-35)
            if (r7 == r8) goto L_0x003c
            r4 = 957831062(0x39175796, float:1.443311E-4)
            if (r7 == r4) goto L_0x0032
            goto L_0x0059
        L_0x0032:
            java.lang.String r4 = "country"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x0059
            r4 = r6
            goto L_0x005a
        L_0x003c:
            java.lang.String r6 = "title"
            boolean r6 = r3.equals(r6)
            if (r6 == 0) goto L_0x0059
            goto L_0x005a
        L_0x0045:
            java.lang.String r4 = "name"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x0059
            r4 = r1
            goto L_0x005a
        L_0x004f:
            java.lang.String r4 = "description"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x0059
            r4 = 3
            goto L_0x005a
        L_0x0059:
            r4 = r5
        L_0x005a:
            switch(r4) {
                case 0: goto L_0x00a9;
                case 1: goto L_0x0092;
                case 2: goto L_0x008a;
                case 3: goto L_0x007c;
                default: goto L_0x005d;
            }
        L_0x005d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Malformed XML: Unknown attribute "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r5 = " in "
            r4.append(r5)
            java.lang.String r5 = "rating-system-definition"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r9.checkVersion(r4)
            goto L_0x00b1
        L_0x007c:
            android.content.res.Resources r4 = r9.mResources
            int r5 = r10.getAttributeResourceValue(r2, r1)
            java.lang.String r4 = r4.getString(r5)
            r0.setDescription(r4)
            goto L_0x00b1
        L_0x008a:
            java.lang.String r4 = r9.getTitle(r10, r2)
            r0.setTitle(r4)
            goto L_0x00b1
        L_0x0092:
            java.lang.String r4 = r10.getAttributeValue(r2)
            java.lang.String r5 = "\\s*,\\s*"
            java.lang.String[] r4 = r4.split(r5)
            int r5 = r4.length
            r6 = r1
        L_0x009e:
            if (r6 >= r5) goto L_0x00a8
            r7 = r4[r6]
            r0.addCountry(r7)
            int r6 = r6 + 1
            goto L_0x009e
        L_0x00a8:
            goto L_0x00b1
        L_0x00a9:
            java.lang.String r4 = r10.getAttributeValue(r2)
            r0.setName(r4)
        L_0x00b1:
            int r2 = r2 + 1
            goto L_0x000c
        L_0x00b5:
            int r2 = r10.next()
            if (r2 == r6) goto L_0x0171
            int r2 = r10.getEventType()
            switch(r2) {
                case 2: goto L_0x00fd;
                case 3: goto L_0x00e2;
                default: goto L_0x00c2;
            }
        L_0x00c2:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r7 = "Malformed XML: Unknown event type "
            r3.append(r7)
            r3.append(r2)
            java.lang.String r7 = " in "
            r3.append(r7)
            java.lang.String r7 = "rating-system-definition"
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r9.checkVersion(r3)
            goto L_0x016f
        L_0x00e2:
            java.lang.String r3 = "rating-system-definition"
            java.lang.String r7 = r10.getName()
            boolean r3 = r3.equals(r7)
            if (r3 == 0) goto L_0x00f6
            r0.setIsCustom(r12)
            com.android.tv.parental.ContentRatingSystem r1 = r0.build()
            return r1
        L_0x00f6:
            java.lang.String r3 = "Malformed XML: Tag mismatch for rating-system-definition"
            r9.checkVersion(r3)
            goto L_0x016f
        L_0x00fd:
            java.lang.String r3 = r10.getName()
            int r7 = r3.hashCode()
            r8 = -1751456994(0xffffffff979ae31e, float:-1.0009349E-24)
            if (r7 == r8) goto L_0x0129
            r8 = 308029750(0x125c2936, float:6.9470556E-28)
            if (r7 == r8) goto L_0x011f
            r8 = 1137752963(0x43d0bb83, float:417.46494)
            if (r7 == r8) goto L_0x0115
            goto L_0x0133
        L_0x0115:
            java.lang.String r7 = "rating-definition"
            boolean r7 = r3.equals(r7)
            if (r7 == 0) goto L_0x0133
            r7 = r1
            goto L_0x0134
        L_0x011f:
            java.lang.String r7 = "sub-rating-definition"
            boolean r7 = r3.equals(r7)
            if (r7 == 0) goto L_0x0133
            r7 = r6
            goto L_0x0134
        L_0x0129:
            java.lang.String r7 = "rating-order"
            boolean r7 = r3.equals(r7)
            if (r7 == 0) goto L_0x0133
            r7 = r4
            goto L_0x0134
        L_0x0133:
            r7 = r5
        L_0x0134:
            switch(r7) {
                case 0: goto L_0x0166;
                case 1: goto L_0x015e;
                case 2: goto L_0x0156;
                default: goto L_0x0137;
            }
        L_0x0137:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Malformed XML: Unknown tag "
            r7.append(r8)
            r7.append(r3)
            java.lang.String r8 = " in "
            r7.append(r8)
            java.lang.String r8 = "rating-system-definition"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r9.checkVersion(r7)
            goto L_0x016e
        L_0x0156:
            com.android.tv.parental.ContentRatingSystem$Order$Builder r7 = r9.parseOrder(r10)
            r0.addOrderBuilder(r7)
            goto L_0x016e
        L_0x015e:
            com.android.tv.parental.ContentRatingSystem$SubRating$Builder r7 = r9.parseSubRatingDefinition(r10)
            r0.addSubRatingBuilder(r7)
            goto L_0x016e
        L_0x0166:
            com.android.tv.parental.ContentRatingSystem$Rating$Builder r7 = r9.parseRatingDefinition(r10)
            r0.addRatingBuilder(r7)
        L_0x016e:
        L_0x016f:
            goto L_0x00b5
        L_0x0171:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "rating-system-definition section is incomplete or section ending tag is missing"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.parental.ContentRatingsParser.parseRatingSystemDefinition(android.content.res.XmlResourceParser, java.lang.String, boolean):com.android.tv.parental.ContentRatingSystem");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0021, code lost:
        if (r3.equals("title") != false) goto L_0x004d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.tv.parental.ContentRatingSystem.Rating.Builder parseRatingDefinition(android.content.res.XmlResourceParser r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r7 = this;
            com.android.tv.parental.ContentRatingSystem$Rating$Builder r0 = new com.android.tv.parental.ContentRatingSystem$Rating$Builder
            r0.<init>()
            r1 = 0
            r2 = r1
        L_0x0007:
            int r3 = r8.getAttributeCount()
            r4 = 1
            if (r2 >= r3) goto L_0x00ba
            java.lang.String r3 = r8.getAttributeName(r2)
            int r5 = r3.hashCode()
            r6 = -1
            switch(r5) {
                case -1724546052: goto L_0x0042;
                case -706851475: goto L_0x0038;
                case 3226745: goto L_0x002e;
                case 3373707: goto L_0x0024;
                case 110371416: goto L_0x001b;
                default: goto L_0x001a;
            }
        L_0x001a:
            goto L_0x004c
        L_0x001b:
            java.lang.String r5 = "title"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x004c
            goto L_0x004d
        L_0x0024:
            java.lang.String r4 = "name"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x004c
            r4 = r1
            goto L_0x004d
        L_0x002e:
            java.lang.String r4 = "icon"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x004c
            r4 = 3
            goto L_0x004d
        L_0x0038:
            java.lang.String r4 = "contentAgeHint"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x004c
            r4 = 4
            goto L_0x004d
        L_0x0042:
            java.lang.String r4 = "description"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x004c
            r4 = 2
            goto L_0x004d
        L_0x004c:
            r4 = r6
        L_0x004d:
            switch(r4) {
                case 0: goto L_0x00ae;
                case 1: goto L_0x00a6;
                case 2: goto L_0x0098;
                case 3: goto L_0x0089;
                case 4: goto L_0x006f;
                default: goto L_0x0050;
            }
        L_0x0050:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Malformed XML: Unknown attribute "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r5 = " in "
            r4.append(r5)
            java.lang.String r5 = "rating-definition"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r7.checkVersion(r4)
            goto L_0x00b6
        L_0x006f:
            r4 = r6
            java.lang.String r5 = r8.getAttributeValue(r2)     // Catch:{ NumberFormatException -> 0x007a }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ NumberFormatException -> 0x007a }
            r4 = r5
            goto L_0x007b
        L_0x007a:
            r5 = move-exception
        L_0x007b:
            if (r4 < 0) goto L_0x0081
            r0.setContentAgeHint(r4)
            goto L_0x00b6
        L_0x0081:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r5 = "Malformed XML: contentAgeHint should be a non-negative number"
            r1.<init>(r5)
            throw r1
        L_0x0089:
            android.content.res.Resources r4 = r7.mResources
            int r5 = r8.getAttributeResourceValue(r2, r1)
            r6 = 0
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5, r6)
            r0.setIcon(r4)
            goto L_0x00b6
        L_0x0098:
            android.content.res.Resources r4 = r7.mResources
            int r5 = r8.getAttributeResourceValue(r2, r1)
            java.lang.String r4 = r4.getString(r5)
            r0.setDescription(r4)
            goto L_0x00b6
        L_0x00a6:
            java.lang.String r4 = r7.getTitle(r8, r2)
            r0.setTitle(r4)
            goto L_0x00b6
        L_0x00ae:
            java.lang.String r4 = r8.getAttributeValue(r2)
            r0.setName(r4)
        L_0x00b6:
            int r2 = r2 + 1
            goto L_0x0007
        L_0x00ba:
            int r1 = r8.next()
            if (r1 == r4) goto L_0x00f2
            int r1 = r8.getEventType()
            switch(r1) {
                case 2: goto L_0x00db;
                case 3: goto L_0x00c8;
                default: goto L_0x00c7;
            }
        L_0x00c7:
            goto L_0x00ba
        L_0x00c8:
            java.lang.String r1 = "rating-definition"
            java.lang.String r2 = r8.getName()
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x00d5
            return r0
        L_0x00d5:
            java.lang.String r1 = "Malformed XML: Tag mismatch for rating-definition"
            r7.checkVersion(r1)
            goto L_0x00ba
        L_0x00db:
            java.lang.String r1 = "sub-rating"
            java.lang.String r2 = r8.getName()
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x00ec
            com.android.tv.parental.ContentRatingSystem$Rating$Builder r0 = r7.parseSubRating(r8, r0)
            goto L_0x00ba
        L_0x00ec:
            java.lang.String r1 = "Malformed XML: Only sub-rating is allowed in rating-definition"
            r7.checkVersion(r1)
            goto L_0x00ba
        L_0x00f2:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "rating-definition section is incomplete or section ending tag is missing"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.parental.ContentRatingsParser.parseRatingDefinition(android.content.res.XmlResourceParser):com.android.tv.parental.ContentRatingSystem$Rating$Builder");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0047, code lost:
        if (r3.equals(ATTR_ICON) != false) goto L_0x0055;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0058  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.tv.parental.ContentRatingSystem.SubRating.Builder parseSubRatingDefinition(android.content.res.XmlResourceParser r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r9 = this;
            com.android.tv.parental.ContentRatingSystem$SubRating$Builder r0 = new com.android.tv.parental.ContentRatingSystem$SubRating$Builder
            r0.<init>()
            r1 = 0
            r2 = r1
        L_0x0007:
            int r3 = r10.getAttributeCount()
            r4 = 3
            r5 = 1
            if (r2 >= r3) goto L_0x00a8
            java.lang.String r3 = r10.getAttributeName(r2)
            r6 = -1
            int r7 = r3.hashCode()
            r8 = -1724546052(0xffffffff993583fc, float:-9.384135E-24)
            if (r7 == r8) goto L_0x004a
            r8 = 3226745(0x313c79, float:4.521633E-39)
            if (r7 == r8) goto L_0x0041
            r4 = 3373707(0x337a8b, float:4.72757E-39)
            if (r7 == r4) goto L_0x0037
            r4 = 110371416(0x6942258, float:5.5721876E-35)
            if (r7 == r4) goto L_0x002d
            goto L_0x0054
        L_0x002d:
            java.lang.String r4 = "title"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x0054
            r4 = r5
            goto L_0x0055
        L_0x0037:
            java.lang.String r4 = "name"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x0054
            r4 = r1
            goto L_0x0055
        L_0x0041:
            java.lang.String r5 = "icon"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x0054
            goto L_0x0055
        L_0x004a:
            java.lang.String r4 = "description"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L_0x0054
            r4 = 2
            goto L_0x0055
        L_0x0054:
            r4 = r6
        L_0x0055:
            switch(r4) {
                case 0: goto L_0x009c;
                case 1: goto L_0x0094;
                case 2: goto L_0x0086;
                case 3: goto L_0x0077;
                default: goto L_0x0058;
            }
        L_0x0058:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Malformed XML: Unknown attribute "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r5 = " in "
            r4.append(r5)
            java.lang.String r5 = "sub-rating-definition"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r9.checkVersion(r4)
            goto L_0x00a4
        L_0x0077:
            android.content.res.Resources r4 = r9.mResources
            int r5 = r10.getAttributeResourceValue(r2, r1)
            r6 = 0
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5, r6)
            r0.setIcon(r4)
            goto L_0x00a4
        L_0x0086:
            android.content.res.Resources r4 = r9.mResources
            int r5 = r10.getAttributeResourceValue(r2, r1)
            java.lang.String r4 = r4.getString(r5)
            r0.setDescription(r4)
            goto L_0x00a4
        L_0x0094:
            java.lang.String r4 = r9.getTitle(r10, r2)
            r0.setTitle(r4)
            goto L_0x00a4
        L_0x009c:
            java.lang.String r4 = r10.getAttributeValue(r2)
            r0.setName(r4)
        L_0x00a4:
            int r2 = r2 + 1
            goto L_0x0007
        L_0x00a8:
            int r1 = r10.next()
            if (r1 == r5) goto L_0x00cd
            int r1 = r10.getEventType()
            if (r1 == r4) goto L_0x00ba
            java.lang.String r1 = "Malformed XML: sub-rating-definition has child"
            r9.checkVersion(r1)
            goto L_0x00a8
        L_0x00ba:
            java.lang.String r1 = "sub-rating-definition"
            java.lang.String r2 = r10.getName()
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x00c7
            return r0
        L_0x00c7:
            java.lang.String r1 = "Malformed XML: sub-rating-definition isn't closed"
            r9.checkVersion(r1)
            goto L_0x00a8
        L_0x00cd:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "sub-rating-definition section is incomplete or section ending tag is missing"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.parental.ContentRatingsParser.parseSubRatingDefinition(android.content.res.XmlResourceParser):com.android.tv.parental.ContentRatingSystem$SubRating$Builder");
    }

    private ContentRatingSystem.Order.Builder parseOrder(XmlResourceParser parser) throws XmlPullParserException, IOException {
        ContentRatingSystem.Order.Builder builder = new ContentRatingSystem.Order.Builder();
        assertEquals(parser.getAttributeCount(), 0, "Malformed XML: Attribute isn't allowed in rating-order");
        while (parser.next() != 1) {
            switch (parser.getEventType()) {
                case 2:
                    if (!TAG_RATING.equals(parser.getName())) {
                        checkVersion("Malformed XML: Only rating is allowed in rating-order");
                        break;
                    } else {
                        builder = parseRating(parser, builder);
                        break;
                    }
                case 3:
                    assertEquals(parser.getName(), TAG_RATING_ORDER, "Malformed XML: Tag mismatch for rating-order");
                    return builder;
            }
        }
        throw new XmlPullParserException("rating-order section is incomplete or section ending tag is missing");
    }

    private ContentRatingSystem.Order.Builder parseRating(XmlResourceParser parser, ContentRatingSystem.Order.Builder builder) throws XmlPullParserException, IOException {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attr = parser.getAttributeName(i);
            char c = 65535;
            if (attr.hashCode() == 3373707 && attr.equals(ATTR_NAME)) {
                c = 0;
            }
            if (c != 0) {
                checkVersion("Malformed XML: rating-order should only contain name");
            } else {
                builder.addRatingName(parser.getAttributeValue(i));
            }
        }
        while (parser.next() != 1) {
            if (parser.getEventType() == 3) {
                if (TAG_RATING.equals(parser.getName())) {
                    return builder;
                }
                checkVersion("Malformed XML: rating has child");
            }
        }
        throw new XmlPullParserException("rating section is incomplete or section ending tag is missing");
    }

    private ContentRatingSystem.Rating.Builder parseSubRating(XmlResourceParser parser, ContentRatingSystem.Rating.Builder builder) throws XmlPullParserException, IOException {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attr = parser.getAttributeName(i);
            char c = 65535;
            if (attr.hashCode() == 3373707 && attr.equals(ATTR_NAME)) {
                c = 0;
            }
            if (c != 0) {
                checkVersion("Malformed XML: sub-rating should only contain name");
            } else {
                builder.addSubRatingName(parser.getAttributeValue(i));
            }
        }
        while (parser.next() != 1) {
            if (parser.getEventType() == 3) {
                if (TAG_SUB_RATING.equals(parser.getName())) {
                    return builder;
                }
                checkVersion("Malformed XML: sub-rating has child");
            }
        }
        throw new XmlPullParserException("sub-rating section is incomplete or section ending tag is missing");
    }

    private String getTitle(XmlResourceParser parser, int index) {
        int titleResId = parser.getAttributeResourceValue(index, 0);
        if (titleResId != 0) {
            return this.mResources.getString(titleResId);
        }
        return parser.getAttributeValue(index);
    }
}
