package com.android.tv.settings.partnercustomizer.utils;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class HiddenApplicationsConfig {
    private static final String CONFIG_FILE = "hidden-applications-configs.xml";
    private static final boolean DEBUG = true;
    private static final String PATH = "/system/etc/";
    private static final String TAG = "HiddenApplicationsConfig";
    private static final String TAG_RESOURCES = "hidden-applications";
    private static final String TAG_SUB_ITEM = "item";
    private static final String VENDOR_PATH = "/vendor/etc/";
    private static HiddenApplicationsConfig mConfig = null;
    public ArrayList<String> mPackageNames = new ArrayList<>();
    private boolean mParseXml = false;

    private HiddenApplicationsConfig() {
        if (!this.mParseXml) {
            init();
        }
        Log.d(TAG, toString());
    }

    public static HiddenApplicationsConfig getInstance(Context context) {
        if (mConfig == null) {
            mConfig = new HiddenApplicationsConfig();
        }
        return mConfig;
    }

    private void init() {
        String[] paths = {"/system/etc/hidden-applications-configs.xml", "/vendor/etc/hidden-applications-configs.xml"};
        FileReader conReader = null;
        int length = paths.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            File config = new File(paths[i]);
            try {
                conReader = new FileReader(config);
                break;
            } catch (Exception e) {
                Log.e(TAG, "Couldn't find or open file " + config);
                i++;
            }
        }
        if (conReader == null) {
            Log.e(TAG, "Couldn't find or open file!");
            return;
        }
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(conReader);
            beginDocument(parser, TAG_RESOURCES);
            int depth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                if ((next == 3 && parser.getDepth() <= depth) || type == 1) {
                    break;
                } else if (type == 2) {
                    if (TAG_SUB_ITEM.equals(parser.getName())) {
                        parser.next();
                        this.mPackageNames.add(parser.getText());
                    }
                }
            }
        } catch (XmlPullParserException e2) {
            Log.w(TAG, "Got exception parsing favorites.", e2);
        } catch (IOException e3) {
            Log.w(TAG, "Got exception parsing favorites.", e3);
        } catch (NumberFormatException e4) {
            Log.w(TAG, "Got exception parsing favorites.", e4);
        } catch (Exception e5) {
            Log.w(TAG, "Got exception parsing favorites.", e5);
        }
        this.mParseXml = true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x000e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void beginDocument(org.xmlpull.v1.XmlPullParser r5, java.lang.String r6) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r4 = this;
        L_0x0000:
            int r0 = r5.next()
            r1 = r0
            r2 = 2
            if (r0 == r2) goto L_0x000c
            r0 = 1
            if (r1 == r0) goto L_0x000c
            goto L_0x0000
        L_0x000c:
            if (r1 != r2) goto L_0x003c
            java.lang.String r0 = r5.getName()
            boolean r0 = r0.equals(r6)
            if (r0 == 0) goto L_0x0019
            return
        L_0x0019:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unexpected start tag: found "
            r2.append(r3)
            java.lang.String r3 = r5.getName()
            r2.append(r3)
            java.lang.String r3 = ", expected "
            r2.append(r3)
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x003c:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "No start tag found"
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.utils.HiddenApplicationsConfig.beginDocument(org.xmlpull.v1.XmlPullParser, java.lang.String):void");
    }

    public ArrayList<String> getPackageList() {
        return this.mPackageNames;
    }

    public String toString() {
        String info = "HiddenApplicationsConfig\nmParseXml:" + this.mParseXml + "\n:" + CONFIG_FILE + "\n";
        for (int i = 0; i < this.mPackageNames.size(); i++) {
            info = info + "[" + i + ":" + this.mPackageNames.get(i) + "]\n";
        }
        return info;
    }
}
