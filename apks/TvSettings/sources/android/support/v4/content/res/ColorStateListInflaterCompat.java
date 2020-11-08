package android.support.v4.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class ColorStateListInflaterCompat {
    private ColorStateListInflaterCompat() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x0012  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0017  */
    @android.support.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.content.res.ColorStateList createFromXml(@android.support.annotation.NonNull android.content.res.Resources r4, @android.support.annotation.NonNull org.xmlpull.v1.XmlPullParser r5, @android.support.annotation.Nullable android.content.res.Resources.Theme r6) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            android.util.AttributeSet r0 = android.util.Xml.asAttributeSet(r5)
        L_0x0004:
            int r1 = r5.next()
            r2 = r1
            r3 = 2
            if (r1 == r3) goto L_0x0010
            r1 = 1
            if (r2 == r1) goto L_0x0010
            goto L_0x0004
        L_0x0010:
            if (r2 != r3) goto L_0x0017
            android.content.res.ColorStateList r1 = createFromXmlInner(r4, r5, r0, r6)
            return r1
        L_0x0017:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r3 = "No start tag found"
            r1.<init>(r3)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.res.ColorStateListInflaterCompat.createFromXml(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.content.res.Resources$Theme):android.content.res.ColorStateList");
    }

    @NonNull
    public static ColorStateList createFromXmlInner(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (name.equals("selector")) {
            return inflate(r, parser, attrs, theme);
        }
        throw new XmlPullParserException(parser.getPositionDescription() + ": invalid color state list tag " + name);
    }

    /* JADX WARNING: type inference failed for: r7v8, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.content.res.ColorStateList inflate(@android.support.annotation.NonNull android.content.res.Resources r20, @android.support.annotation.NonNull org.xmlpull.v1.XmlPullParser r21, @android.support.annotation.NonNull android.util.AttributeSet r22, @android.support.annotation.Nullable android.content.res.Resources.Theme r23) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r0 = r22
            int r1 = r21.getDepth()
            r2 = 1
            int r1 = r1 + r2
            r3 = 20
            int[][] r3 = new int[r3][]
            int r4 = r3.length
            int[] r4 = new int[r4]
            r6 = r4
            r4 = r3
            r3 = 0
        L_0x0012:
            int r7 = r21.next()
            r8 = r7
            if (r7 == r2) goto L_0x00da
            int r7 = r21.getDepth()
            r9 = r7
            if (r7 >= r1) goto L_0x002c
            r7 = 3
            if (r8 == r7) goto L_0x0024
            goto L_0x002c
        L_0x0024:
            r10 = r20
            r11 = r23
            r16 = r1
            goto L_0x00e0
        L_0x002c:
            r7 = 2
            if (r8 != r7) goto L_0x00cf
            if (r9 > r1) goto L_0x00cf
            java.lang.String r7 = r21.getName()
            java.lang.String r10 = "item"
            boolean r7 = r7.equals(r10)
            if (r7 != 0) goto L_0x0046
            r10 = r20
            r11 = r23
            r16 = r1
            goto L_0x00d5
        L_0x0046:
            int[] r7 = android.support.compat.R.styleable.ColorStateListItem
            r10 = r20
            r11 = r23
            android.content.res.TypedArray r7 = obtainAttributes(r10, r11, r0, r7)
            int r12 = android.support.compat.R.styleable.ColorStateListItem_android_color
            r13 = -65281(0xffffffffffff00ff, float:NaN)
            int r12 = r7.getColor(r12, r13)
            r13 = 1065353216(0x3f800000, float:1.0)
            int r14 = android.support.compat.R.styleable.ColorStateListItem_android_alpha
            boolean r14 = r7.hasValue(r14)
            if (r14 == 0) goto L_0x006a
            int r14 = android.support.compat.R.styleable.ColorStateListItem_android_alpha
            float r13 = r7.getFloat(r14, r13)
            goto L_0x0078
        L_0x006a:
            int r14 = android.support.compat.R.styleable.ColorStateListItem_alpha
            boolean r14 = r7.hasValue(r14)
            if (r14 == 0) goto L_0x0078
            int r14 = android.support.compat.R.styleable.ColorStateListItem_alpha
            float r13 = r7.getFloat(r14, r13)
        L_0x0078:
            r7.recycle()
            r14 = 0
            int r15 = r22.getAttributeCount()
            int[] r2 = new int[r15]
            r5 = r14
            r14 = 0
        L_0x0084:
            if (r14 >= r15) goto L_0x00b5
            r16 = r1
            int r1 = r0.getAttributeNameResource(r14)
            r17 = r7
            r7 = 16843173(0x10101a5, float:2.3694738E-38)
            if (r1 == r7) goto L_0x00ae
            r7 = 16843551(0x101031f, float:2.3695797E-38)
            if (r1 == r7) goto L_0x00ae
            int r7 = android.support.compat.R.attr.alpha
            if (r1 == r7) goto L_0x00ae
            int r7 = r5 + 1
            r18 = r7
            r7 = 0
            boolean r19 = r0.getAttributeBooleanValue(r14, r7)
            if (r19 == 0) goto L_0x00a9
            r7 = r1
            goto L_0x00aa
        L_0x00a9:
            int r7 = -r1
        L_0x00aa:
            r2[r5] = r7
            r5 = r18
        L_0x00ae:
            int r14 = r14 + 1
            r1 = r16
            r7 = r17
            goto L_0x0084
        L_0x00b5:
            r16 = r1
            r17 = r7
            int[] r1 = android.util.StateSet.trimStateSet(r2, r5)
            int r2 = modulateColorAlpha(r12, r13)
            int[] r6 = android.support.v4.content.res.GrowingArrayUtils.append((int[]) r6, (int) r3, (int) r2)
            java.lang.Object[] r7 = android.support.v4.content.res.GrowingArrayUtils.append((T[]) r4, (int) r3, r1)
            r4 = r7
            int[][] r4 = (int[][]) r4
            int r3 = r3 + 1
            goto L_0x00d5
        L_0x00cf:
            r10 = r20
            r11 = r23
            r16 = r1
        L_0x00d5:
            r1 = r16
            r2 = 1
            goto L_0x0012
        L_0x00da:
            r10 = r20
            r11 = r23
            r16 = r1
        L_0x00e0:
            int[] r1 = new int[r3]
            int[][] r2 = new int[r3][]
            r5 = 0
            java.lang.System.arraycopy(r6, r5, r1, r5, r3)
            java.lang.System.arraycopy(r4, r5, r2, r5, r3)
            android.content.res.ColorStateList r5 = new android.content.res.ColorStateList
            r5.<init>(r2, r1)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.res.ColorStateListInflaterCompat.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):android.content.res.ColorStateList");
    }

    private static TypedArray obtainAttributes(Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    @ColorInt
    private static int modulateColorAlpha(@ColorInt int color, @FloatRange(from = 0.0d, to = 1.0d) float alphaMod) {
        return (16777215 & color) | (Math.round(((float) Color.alpha(color)) * alphaMod) << 24);
    }
}
