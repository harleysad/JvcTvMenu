package androidx.slice;

import android.os.Parcelable;
import android.support.annotation.RestrictTo;
import androidx.versionedparcelable.VersionedParcelable;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class SliceItemHolder implements VersionedParcelable {
    int mInt;
    long mLong;
    Parcelable mParcelable;
    String mStr;
    VersionedParcelable mVersionedParcelable;

    public SliceItemHolder() {
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SliceItemHolder(java.lang.String r3, java.lang.Object r4, boolean r5) {
        /*
            r2 = this;
            r2.<init>()
            int r0 = r3.hashCode()
            r1 = 0
            switch(r0) {
                case -1422950858: goto L_0x0048;
                case 104431: goto L_0x003e;
                case 3327612: goto L_0x0034;
                case 3556653: goto L_0x002a;
                case 100313435: goto L_0x0020;
                case 100358090: goto L_0x0016;
                case 109526418: goto L_0x000c;
                default: goto L_0x000b;
            }
        L_0x000b:
            goto L_0x0052
        L_0x000c:
            java.lang.String r0 = "slice"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = 2
            goto L_0x0053
        L_0x0016:
            java.lang.String r0 = "input"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = 3
            goto L_0x0053
        L_0x0020:
            java.lang.String r0 = "image"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = 1
            goto L_0x0053
        L_0x002a:
            java.lang.String r0 = "text"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = 4
            goto L_0x0053
        L_0x0034:
            java.lang.String r0 = "long"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = 6
            goto L_0x0053
        L_0x003e:
            java.lang.String r0 = "int"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = 5
            goto L_0x0053
        L_0x0048:
            java.lang.String r0 = "action"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0052
            r0 = r1
            goto L_0x0053
        L_0x0052:
            r0 = -1
        L_0x0053:
            switch(r0) {
                case 0: goto L_0x0094;
                case 1: goto L_0x008e;
                case 2: goto L_0x008e;
                case 3: goto L_0x007e;
                case 4: goto L_0x006c;
                case 5: goto L_0x0062;
                case 6: goto L_0x0058;
                default: goto L_0x0056;
            }
        L_0x0056:
            goto L_0x00c5
        L_0x0058:
            r0 = r4
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            r2.mLong = r0
            goto L_0x00c5
        L_0x0062:
            r0 = r4
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r2.mInt = r0
            goto L_0x00c5
        L_0x006c:
            boolean r0 = r4 instanceof android.text.Spanned
            if (r0 == 0) goto L_0x0078
            r0 = r4
            android.text.Spanned r0 = (android.text.Spanned) r0
            java.lang.String r0 = android.support.v4.text.HtmlCompat.toHtml(r0, r1)
            goto L_0x007b
        L_0x0078:
            r0 = r4
            java.lang.String r0 = (java.lang.String) r0
        L_0x007b:
            r2.mStr = r0
            goto L_0x00c5
        L_0x007e:
            if (r5 != 0) goto L_0x0086
            r0 = r4
            android.os.Parcelable r0 = (android.os.Parcelable) r0
            r2.mParcelable = r0
            goto L_0x00c5
        L_0x0086:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Cannot write RemoteInput to stream"
            r0.<init>(r1)
            throw r0
        L_0x008e:
            r0 = r4
            androidx.versionedparcelable.VersionedParcelable r0 = (androidx.versionedparcelable.VersionedParcelable) r0
            r2.mVersionedParcelable = r0
            goto L_0x00c5
        L_0x0094:
            r0 = r4
            android.support.v4.util.Pair r0 = (android.support.v4.util.Pair) r0
            F r0 = r0.first
            boolean r0 = r0 instanceof android.app.PendingIntent
            if (r0 == 0) goto L_0x00b1
            if (r5 != 0) goto L_0x00a9
            r0 = r4
            android.support.v4.util.Pair r0 = (android.support.v4.util.Pair) r0
            F r0 = r0.first
            android.os.Parcelable r0 = (android.os.Parcelable) r0
            r2.mParcelable = r0
            goto L_0x00b3
        L_0x00a9:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Cannot write PendingIntent to stream"
            r0.<init>(r1)
            throw r0
        L_0x00b1:
            if (r5 == 0) goto L_0x00bd
        L_0x00b3:
            r0 = r4
            android.support.v4.util.Pair r0 = (android.support.v4.util.Pair) r0
            S r0 = r0.second
            androidx.versionedparcelable.VersionedParcelable r0 = (androidx.versionedparcelable.VersionedParcelable) r0
            r2.mVersionedParcelable = r0
            goto L_0x00c5
        L_0x00bd:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Cannot write callback to parcel"
            r0.<init>(r1)
            throw r0
        L_0x00c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItemHolder.<init>(java.lang.String, java.lang.Object, boolean):void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object getObj(java.lang.String r4) {
        /*
            r3 = this;
            int r0 = r4.hashCode()
            r1 = 0
            switch(r0) {
                case -1422950858: goto L_0x0045;
                case 104431: goto L_0x003b;
                case 3327612: goto L_0x0031;
                case 3556653: goto L_0x0027;
                case 100313435: goto L_0x001d;
                case 100358090: goto L_0x0013;
                case 109526418: goto L_0x0009;
                default: goto L_0x0008;
            }
        L_0x0008:
            goto L_0x004f
        L_0x0009:
            java.lang.String r0 = "slice"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = 2
            goto L_0x0050
        L_0x0013:
            java.lang.String r0 = "input"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = 3
            goto L_0x0050
        L_0x001d:
            java.lang.String r0 = "image"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = 1
            goto L_0x0050
        L_0x0027:
            java.lang.String r0 = "text"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = 4
            goto L_0x0050
        L_0x0031:
            java.lang.String r0 = "long"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = 6
            goto L_0x0050
        L_0x003b:
            java.lang.String r0 = "int"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = 5
            goto L_0x0050
        L_0x0045:
            java.lang.String r0 = "action"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004f
            r0 = r1
            goto L_0x0050
        L_0x004f:
            r0 = -1
        L_0x0050:
            switch(r0) {
                case 0: goto L_0x0085;
                case 1: goto L_0x0082;
                case 2: goto L_0x0082;
                case 3: goto L_0x007f;
                case 4: goto L_0x0078;
                case 5: goto L_0x0071;
                case 6: goto L_0x006a;
                default: goto L_0x0053;
            }
        L_0x0053:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unrecognized format "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x006a:
            long r0 = r3.mLong
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            return r0
        L_0x0071:
            int r0 = r3.mInt
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x0078:
            java.lang.String r0 = r3.mStr
            android.text.Spanned r0 = android.support.v4.text.HtmlCompat.fromHtml(r0, r1)
            return r0
        L_0x007f:
            android.os.Parcelable r0 = r3.mParcelable
            return r0
        L_0x0082:
            androidx.versionedparcelable.VersionedParcelable r0 = r3.mVersionedParcelable
            return r0
        L_0x0085:
            android.support.v4.util.Pair r0 = new android.support.v4.util.Pair
            android.os.Parcelable r1 = r3.mParcelable
            androidx.versionedparcelable.VersionedParcelable r2 = r3.mVersionedParcelable
            androidx.slice.Slice r2 = (androidx.slice.Slice) r2
            r0.<init>(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItemHolder.getObj(java.lang.String):java.lang.Object");
    }
}
