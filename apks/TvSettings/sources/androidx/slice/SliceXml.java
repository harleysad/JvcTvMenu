package androidx.slice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import androidx.slice.Slice;
import androidx.slice.SliceUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

@RestrictTo({RestrictTo.Scope.LIBRARY})
class SliceXml {
    private static final String ATTR_FORMAT = "format";
    private static final String ATTR_HINTS = "hints";
    private static final String ATTR_ICON_PACKAGE = "pkg";
    private static final String ATTR_ICON_RES_TYPE = "resType";
    private static final String ATTR_ICON_TYPE = "iconType";
    private static final String ATTR_SUBTYPE = "subtype";
    private static final String ATTR_URI = "uri";
    private static final String ICON_TYPE_DEFAULT = "def";
    private static final String ICON_TYPE_RES = "res";
    private static final String ICON_TYPE_URI = "uri";
    private static final String NAMESPACE = null;
    private static final String TAG_ACTION = "action";
    private static final String TAG_ITEM = "item";
    private static final String TAG_SLICE = "slice";

    public static Slice parseSlice(Context context, InputStream input, String encoding, SliceUtils.SliceActionListener listener) throws IOException, SliceUtils.SliceParseException {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(input, encoding);
            int outerDepth = parser.getDepth();
            Slice s = null;
            while (true) {
                int next = parser.next();
                int type = next;
                if (next == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                    return s;
                }
                if (type == 2) {
                    s = parseSlice(context, parser, listener);
                }
            }
            return s;
        } catch (XmlPullParserException e) {
            throw new IOException("Unable to init XML Serialization", e);
        }
    }

    @SuppressLint({"WrongConstant"})
    private static Slice parseSlice(Context context, XmlPullParser parser, SliceUtils.SliceActionListener listener) throws IOException, XmlPullParserException, SliceUtils.SliceParseException {
        if ("slice".equals(parser.getName()) || "action".equals(parser.getName())) {
            int outerDepth = parser.getDepth();
            Slice.Builder b = new Slice.Builder(Uri.parse(parser.getAttributeValue(NAMESPACE, "uri")));
            b.addHints(hints(parser.getAttributeValue(NAMESPACE, ATTR_HINTS)));
            while (true) {
                int next = parser.next();
                int type = next;
                if (next != 1 && (type != 3 || parser.getDepth() > outerDepth)) {
                    if (type == 2 && TAG_ITEM.equals(parser.getName())) {
                        parseItem(context, b, parser, listener);
                    }
                }
            }
            return b.build();
        }
        throw new IOException("Unexpected tag " + parser.getName());
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    @android.annotation.SuppressLint({"DefaultCharset"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseItem(android.content.Context r23, androidx.slice.Slice.Builder r24, org.xmlpull.v1.XmlPullParser r25, androidx.slice.SliceUtils.SliceActionListener r26) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException, androidx.slice.SliceUtils.SliceParseException {
        /*
            r1 = r23
            r2 = r24
            r3 = r25
            r4 = r26
            int r5 = r25.getDepth()
            java.lang.String r0 = NAMESPACE
            java.lang.String r6 = "format"
            java.lang.String r6 = r3.getAttributeValue(r0, r6)
            java.lang.String r0 = NAMESPACE
            java.lang.String r7 = "subtype"
            java.lang.String r7 = r3.getAttributeValue(r0, r7)
            java.lang.String r0 = NAMESPACE
            java.lang.String r8 = "hints"
            java.lang.String r8 = r3.getAttributeValue(r0, r8)
            java.lang.String r0 = NAMESPACE
            java.lang.String r9 = "iconType"
            java.lang.String r9 = r3.getAttributeValue(r0, r9)
            java.lang.String r0 = NAMESPACE
            java.lang.String r10 = "pkg"
            java.lang.String r10 = r3.getAttributeValue(r0, r10)
            java.lang.String r0 = NAMESPACE
            java.lang.String r11 = "resType"
            java.lang.String r11 = r3.getAttributeValue(r0, r11)
            java.lang.String[] r0 = hints(r8)
        L_0x0040:
            r12 = r0
            int r0 = r25.next()
            r13 = r0
            r14 = 1
            if (r0 == r14) goto L_0x020c
            r0 = 3
            if (r13 != r0) goto L_0x0059
            int r15 = r25.getDepth()
            if (r15 <= r5) goto L_0x0053
            goto L_0x0059
        L_0x0053:
            r21 = r5
            r22 = r8
            goto L_0x0210
        L_0x0059:
            r15 = 4
            if (r13 != r15) goto L_0x01cf
            int r17 = r6.hashCode()
            r18 = -1
            switch(r17) {
                case 104431: goto L_0x0092;
                case 3327612: goto L_0x0087;
                case 3556653: goto L_0x007c;
                case 100313435: goto L_0x0071;
                case 100358090: goto L_0x0066;
                default: goto L_0x0065;
            }
        L_0x0065:
            goto L_0x009d
        L_0x0066:
            java.lang.String r15 = "input"
            boolean r15 = r6.equals(r15)
            if (r15 == 0) goto L_0x009d
            r16 = 0
            goto L_0x009f
        L_0x0071:
            java.lang.String r15 = "image"
            boolean r15 = r6.equals(r15)
            if (r15 == 0) goto L_0x009d
            r16 = 1
            goto L_0x009f
        L_0x007c:
            java.lang.String r15 = "text"
            boolean r15 = r6.equals(r15)
            if (r15 == 0) goto L_0x009d
            r16 = 3
            goto L_0x009f
        L_0x0087:
            java.lang.String r15 = "long"
            boolean r15 = r6.equals(r15)
            if (r15 == 0) goto L_0x009d
            r16 = 4
            goto L_0x009f
        L_0x0092:
            java.lang.String r15 = "int"
            boolean r15 = r6.equals(r15)
            if (r15 == 0) goto L_0x009d
            r16 = 2
            goto L_0x009f
        L_0x009d:
            r16 = r18
        L_0x009f:
            switch(r16) {
                case 0: goto L_0x01ca;
                case 1: goto L_0x00f8;
                case 2: goto L_0x00e6;
                case 3: goto L_0x00c9;
                case 4: goto L_0x00bd;
                default: goto L_0x00a2;
            }
        L_0x00a2:
            r21 = r5
            r22 = r8
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r8 = "Unrecognized format "
            r5.append(r8)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r0.<init>(r5)
            throw r0
        L_0x00bd:
            java.lang.String r0 = r25.getText()
            long r14 = java.lang.Long.parseLong(r0)
            r2.addLong((long) r14, (java.lang.String) r7, (java.lang.String[]) r12)
            goto L_0x00f2
        L_0x00c9:
            java.lang.String r14 = r25.getText()
            int r15 = android.os.Build.VERSION.SDK_INT
            r0 = 22
            if (r15 >= r0) goto L_0x00de
            java.lang.String r0 = new java.lang.String
            r15 = 2
            byte[] r15 = android.util.Base64.decode(r14, r15)
            r0.<init>(r15)
            r14 = r0
        L_0x00de:
            android.text.Spanned r0 = android.text.Html.fromHtml(r14)
            r2.addText((java.lang.CharSequence) r0, (java.lang.String) r7, (java.lang.String[]) r12)
            goto L_0x00f2
        L_0x00e6:
            java.lang.String r0 = r25.getText()
            int r14 = java.lang.Integer.parseInt(r0)
            r2.addInt((int) r14, (java.lang.String) r7, (java.lang.String[]) r12)
        L_0x00f2:
            r21 = r5
            r22 = r8
            goto L_0x0205
        L_0x00f8:
            int r0 = r9.hashCode()
            r15 = 112800(0x1b8a0, float:1.58066E-40)
            if (r0 == r15) goto L_0x0112
            r15 = 116076(0x1c56c, float:1.62657E-40)
            if (r0 == r15) goto L_0x0107
            goto L_0x011c
        L_0x0107:
            java.lang.String r0 = "uri"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x011c
            r18 = 1
            goto L_0x011c
        L_0x0112:
            java.lang.String r0 = "res"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x011c
            r18 = 0
        L_0x011c:
            switch(r18) {
                case 0: goto L_0x014d;
                case 1: goto L_0x013b;
                default: goto L_0x011f;
            }
        L_0x011f:
            r21 = r5
            r22 = r8
            java.lang.String r0 = r25.getText()
            r5 = 2
            byte[] r5 = android.util.Base64.decode(r0, r5)
            int r8 = r5.length
            r14 = 0
            android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeByteArray(r5, r14, r8)
            android.support.v4.graphics.drawable.IconCompat r14 = android.support.v4.graphics.drawable.IconCompat.createWithBitmap(r8)
            r2.addIcon((android.support.v4.graphics.drawable.IconCompat) r14, (java.lang.String) r7, (java.lang.String[]) r12)
            goto L_0x01c9
        L_0x013b:
            java.lang.String r0 = r25.getText()
            android.support.v4.graphics.drawable.IconCompat r14 = android.support.v4.graphics.drawable.IconCompat.createWithContentUri((java.lang.String) r0)
            r2.addIcon((android.support.v4.graphics.drawable.IconCompat) r14, (java.lang.String) r7, (java.lang.String[]) r12)
        L_0x0147:
            r21 = r5
            r22 = r8
            goto L_0x01c9
        L_0x014d:
            java.lang.String r0 = r25.getText()
            r15 = r0
            android.content.pm.PackageManager r0 = r23.getPackageManager()     // Catch:{ NameNotFoundException -> 0x01ad }
            android.content.res.Resources r0 = r0.getResourcesForApplication(r10)     // Catch:{ NameNotFoundException -> 0x01ad }
            int r16 = r0.getIdentifier(r15, r11, r10)     // Catch:{ NameNotFoundException -> 0x01ad }
            r19 = r16
            r14 = r19
            if (r14 == 0) goto L_0x017a
            r20 = r0
            r0 = 0
            android.content.Context r0 = r1.createPackageContext(r10, r0)     // Catch:{ NameNotFoundException -> 0x0174 }
            android.support.v4.graphics.drawable.IconCompat r0 = android.support.v4.graphics.drawable.IconCompat.createWithResource(r0, r14)     // Catch:{ NameNotFoundException -> 0x0174 }
            r2.addIcon((android.support.v4.graphics.drawable.IconCompat) r0, (java.lang.String) r7, (java.lang.String[]) r12)     // Catch:{ NameNotFoundException -> 0x0174 }
            goto L_0x0147
        L_0x0174:
            r0 = move-exception
            r21 = r5
            r22 = r8
            goto L_0x01b2
        L_0x017a:
            r20 = r0
            androidx.slice.SliceUtils$SliceParseException r0 = new androidx.slice.SliceUtils$SliceParseException     // Catch:{ NameNotFoundException -> 0x01ad }
            r21 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x01a9 }
            r5.<init>()     // Catch:{ NameNotFoundException -> 0x01a9 }
            r22 = r8
            java.lang.String r8 = "Cannot find resource "
            r5.append(r8)     // Catch:{ NameNotFoundException -> 0x01a7 }
            r5.append(r10)     // Catch:{ NameNotFoundException -> 0x01a7 }
            java.lang.String r8 = ":"
            r5.append(r8)     // Catch:{ NameNotFoundException -> 0x01a7 }
            r5.append(r11)     // Catch:{ NameNotFoundException -> 0x01a7 }
            java.lang.String r8 = "/"
            r5.append(r8)     // Catch:{ NameNotFoundException -> 0x01a7 }
            r5.append(r15)     // Catch:{ NameNotFoundException -> 0x01a7 }
            java.lang.String r5 = r5.toString()     // Catch:{ NameNotFoundException -> 0x01a7 }
            r0.<init>(r5)     // Catch:{ NameNotFoundException -> 0x01a7 }
            throw r0     // Catch:{ NameNotFoundException -> 0x01a7 }
        L_0x01a7:
            r0 = move-exception
            goto L_0x01b2
        L_0x01a9:
            r0 = move-exception
            r22 = r8
            goto L_0x01b2
        L_0x01ad:
            r0 = move-exception
            r21 = r5
            r22 = r8
        L_0x01b2:
            androidx.slice.SliceUtils$SliceParseException r5 = new androidx.slice.SliceUtils$SliceParseException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r14 = "Invalid icon package "
            r8.append(r14)
            r8.append(r10)
            java.lang.String r8 = r8.toString()
            r5.<init>(r8, r0)
            throw r5
        L_0x01c9:
            goto L_0x0205
        L_0x01ca:
            r21 = r5
            r22 = r8
            goto L_0x0205
        L_0x01cf:
            r21 = r5
            r22 = r8
            r0 = 2
            if (r13 != r0) goto L_0x01ea
            java.lang.String r0 = "slice"
            java.lang.String r5 = r25.getName()
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x01ea
            androidx.slice.Slice r0 = parseSlice(r1, r3, r4)
            r2.addSubSlice(r0, r7)
            goto L_0x0205
        L_0x01ea:
            r0 = 2
            if (r13 != r0) goto L_0x0205
            java.lang.String r0 = "action"
            java.lang.String r5 = r25.getName()
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x0205
            androidx.slice.SliceXml$1 r0 = new androidx.slice.SliceXml$1
            r0.<init>(r4)
            androidx.slice.Slice r5 = parseSlice(r1, r3, r4)
            r2.addAction((androidx.slice.SliceItem.ActionHandler) r0, (androidx.slice.Slice) r5, (java.lang.String) r7)
        L_0x0205:
            r0 = r12
            r5 = r21
            r8 = r22
            goto L_0x0040
        L_0x020c:
            r21 = r5
            r22 = r8
        L_0x0210:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceXml.parseItem(android.content.Context, androidx.slice.Slice$Builder, org.xmlpull.v1.XmlPullParser, androidx.slice.SliceUtils$SliceActionListener):void");
    }

    @Slice.SliceHint
    private static String[] hints(String hintStr) {
        return TextUtils.isEmpty(hintStr) ? new String[0] : hintStr.split(",");
    }

    public static void serializeSlice(Slice s, Context context, OutputStream output, String encoding, SliceUtils.SerializeOptions options) throws IOException {
        try {
            XmlSerializer serializer = XmlPullParserFactory.newInstance().newSerializer();
            serializer.setOutput(output, encoding);
            serializer.startDocument(encoding, (Boolean) null);
            serialize(s, context, options, serializer, false, (String) null);
            serializer.endDocument();
            serializer.flush();
        } catch (XmlPullParserException e) {
            throw new IOException("Unable to init XML Serialization", e);
        }
    }

    private static void serialize(Slice s, Context context, SliceUtils.SerializeOptions options, XmlSerializer serializer, boolean isAction, String subType) throws IOException {
        serializer.startTag(NAMESPACE, isAction ? "action" : "slice");
        serializer.attribute(NAMESPACE, "uri", s.getUri().toString());
        if (subType != null) {
            serializer.attribute(NAMESPACE, ATTR_SUBTYPE, subType);
        }
        if (!s.getHints().isEmpty()) {
            serializer.attribute(NAMESPACE, ATTR_HINTS, hintStr(s.getHints()));
        }
        for (SliceItem item : s.getItems()) {
            serialize(item, context, options, serializer);
        }
        serializer.endTag(NAMESPACE, isAction ? "action" : "slice");
    }

    private static void serialize(SliceItem item, Context context, SliceUtils.SerializeOptions options, XmlSerializer serializer) throws IOException {
        String format = item.getFormat();
        options.checkThrow(format);
        serializer.startTag(NAMESPACE, TAG_ITEM);
        serializer.attribute(NAMESPACE, ATTR_FORMAT, format);
        if (item.getSubType() != null) {
            serializer.attribute(NAMESPACE, ATTR_SUBTYPE, item.getSubType());
        }
        if (!item.getHints().isEmpty()) {
            serializer.attribute(NAMESPACE, ATTR_HINTS, hintStr(item.getHints()));
        }
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 3;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 6;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 5;
                    break;
                }
                break;
            case 100313435:
                if (format.equals("image")) {
                    c = 2;
                    break;
                }
                break;
            case 100358090:
                if (format.equals("input")) {
                    c = 1;
                    break;
                }
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (options.getActionMode() == 2) {
                    serialize(item.getSlice(), context, options, serializer, true, item.getSubType());
                    break;
                } else if (options.getActionMode() == 0) {
                    throw new IllegalArgumentException("Slice contains an action " + item);
                }
                break;
            case 1:
                break;
            case 2:
                if (options.getImageMode() == 2) {
                    IconCompat icon = item.getIcon();
                    int type = icon.getType();
                    if (type != 2) {
                        if (type == 4) {
                            if (!"file".equals(icon.getUri().getScheme())) {
                                serializeIcon(serializer, icon, context, options);
                                break;
                            } else {
                                serializeFileIcon(serializer, icon, context);
                                break;
                            }
                        } else {
                            serializeIcon(serializer, icon, context, options);
                            break;
                        }
                    } else {
                        serializeResIcon(serializer, icon, context);
                        break;
                    }
                } else if (options.getImageMode() == 0) {
                    throw new IllegalArgumentException("Slice contains an image " + item);
                }
                break;
            case 3:
                serializer.text(String.valueOf(item.getInt()));
                break;
            case 4:
                serialize(item.getSlice(), context, options, serializer, false, item.getSubType());
                break;
            case 5:
                if (!(item.getText() instanceof Spanned)) {
                    String text = String.valueOf(item.getText());
                    if (Build.VERSION.SDK_INT < 22) {
                        text = Base64.encodeToString(text.getBytes(StandardCharsets.UTF_8), 2);
                    }
                    serializer.text(text);
                    break;
                } else {
                    String text2 = Html.toHtml((Spanned) item.getText());
                    if (Build.VERSION.SDK_INT < 22) {
                        text2 = Base64.encodeToString(text2.getBytes(StandardCharsets.UTF_8), 2);
                    }
                    serializer.text(text2);
                    break;
                }
            case 6:
                serializer.text(String.valueOf(item.getLong()));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized format " + format);
        }
        serializer.endTag(NAMESPACE, TAG_ITEM);
    }

    private static void serializeResIcon(XmlSerializer serializer, IconCompat icon, Context context) throws IOException {
        try {
            Resources res = context.getPackageManager().getResourcesForApplication(icon.getResPackage());
            int id = icon.getResId();
            serializer.attribute(NAMESPACE, ATTR_ICON_TYPE, ICON_TYPE_RES);
            serializer.attribute(NAMESPACE, "pkg", res.getResourcePackageName(id));
            serializer.attribute(NAMESPACE, ATTR_ICON_RES_TYPE, res.getResourceTypeName(id));
            serializer.text(res.getResourceEntryName(id));
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("Slice contains invalid icon", e);
        }
    }

    private static void serializeFileIcon(XmlSerializer serializer, IconCompat icon, Context context) throws IOException {
        serializer.attribute(NAMESPACE, ATTR_ICON_TYPE, "uri");
        serializer.text(icon.getUri().toString());
    }

    private static void serializeIcon(XmlSerializer serializer, IconCompat icon, Context context, SliceUtils.SerializeOptions options) throws IOException {
        byte[] outputStream = convertToBytes(icon, context, options);
        serializer.attribute(NAMESPACE, ATTR_ICON_TYPE, ICON_TYPE_DEFAULT);
        serializer.text(new String(Base64.encode(outputStream, 2), StandardCharsets.UTF_8));
    }

    public static byte[] convertToBytes(IconCompat icon, Context context, SliceUtils.SerializeOptions options) {
        Drawable d = icon.loadDrawable(context);
        int width = d.getIntrinsicWidth();
        int height = d.getIntrinsicHeight();
        if (width > options.getMaxWidth()) {
            height = (int) (((double) (options.getMaxWidth() * height)) / ((double) width));
            width = options.getMaxWidth();
        }
        if (height > options.getMaxHeight()) {
            width = (int) (((double) (options.getMaxHeight() * width)) / ((double) height));
            height = options.getMaxHeight();
        }
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, c.getWidth(), c.getHeight());
        d.draw(c);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        b.compress(options.getFormat(), options.getQuality(), outputStream);
        b.recycle();
        return outputStream.toByteArray();
    }

    private static String hintStr(List<String> hints) {
        return TextUtils.join(",", hints);
    }

    private SliceXml() {
    }
}
