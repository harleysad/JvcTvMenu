package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import androidx.slice.Slice;
import androidx.versionedparcelable.CustomVersionedParcelable;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import java.util.Arrays;
import java.util.List;

public final class SliceItem extends CustomVersionedParcelable {
    private static final String FORMAT = "format";
    private static final String HINTS = "hints";
    private static final String OBJ = "obj";
    private static final String OBJ_2 = "obj_2";
    private static final String SUBTYPE = "subtype";
    String mFormat;
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @Slice.SliceHint
    protected String[] mHints;
    SliceItemHolder mHolder;
    Object mObj;
    String mSubType;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public interface ActionHandler {
        void onAction(SliceItem sliceItem, Context context, Intent intent);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public @interface SliceType {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceItem(Object obj, @SliceType String format, String subType, @Slice.SliceHint String[] hints) {
        this.mHints = new String[0];
        this.mHints = hints;
        this.mFormat = format;
        this.mSubType = subType;
        this.mObj = obj;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceItem(Object obj, @SliceType String format, String subType, @Slice.SliceHint List<String> hints) {
        this(obj, format, subType, (String[]) hints.toArray(new String[hints.size()]));
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceItem() {
        this.mHints = new String[0];
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceItem(PendingIntent intent, Slice slice, String format, String subType, @Slice.SliceHint String[] hints) {
        this((Object) new Pair(intent, slice), format, subType, hints);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceItem(ActionHandler action, Slice slice, String format, String subType, @Slice.SliceHint String[] hints) {
        this((Object) new Pair(action, slice), format, subType, hints);
    }

    @NonNull
    @Slice.SliceHint
    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public void addHint(@Slice.SliceHint String hint) {
        this.mHints = (String[]) ArrayUtils.appendElement(String.class, this.mHints, hint);
    }

    @SliceType
    public String getFormat() {
        return this.mFormat;
    }

    public String getSubType() {
        return this.mSubType;
    }

    public CharSequence getText() {
        return (CharSequence) this.mObj;
    }

    public IconCompat getIcon() {
        return (IconCompat) this.mObj;
    }

    public PendingIntent getAction() {
        return (PendingIntent) ((Pair) this.mObj).first;
    }

    public void fireAction(@Nullable Context context, @Nullable Intent i) throws PendingIntent.CanceledException {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            ((PendingIntent) f).send(context, 0, i, (PendingIntent.OnFinished) null, (Handler) null);
        } else {
            ((ActionHandler) f).onAction(this, context, i);
        }
    }

    @RequiresApi(20)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteInput getRemoteInput() {
        return (RemoteInput) this.mObj;
    }

    public int getInt() {
        return ((Integer) this.mObj).intValue();
    }

    public Slice getSlice() {
        if (BaseDialogFragment.TAG_ACTION.equals(getFormat())) {
            return (Slice) ((Pair) this.mObj).second;
        }
        return (Slice) this.mObj;
    }

    public long getLong() {
        return ((Long) this.mObj).longValue();
    }

    @Deprecated
    public long getTimestamp() {
        return ((Long) this.mObj).longValue();
    }

    public boolean hasHint(@Slice.SliceHint String hint) {
        return ArrayUtils.contains(this.mHints, hint);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceItem(Bundle in) {
        this.mHints = new String[0];
        this.mHints = in.getStringArray(HINTS);
        this.mFormat = in.getString(FORMAT);
        this.mSubType = in.getString(SUBTYPE);
        this.mObj = readObj(this.mFormat, in);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putStringArray(HINTS, this.mHints);
        b.putString(FORMAT, this.mFormat);
        b.putString(SUBTYPE, this.mSubType);
        writeObj(b, this.mObj, this.mFormat);
        return b;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public boolean hasHints(@Slice.SliceHint String[] hints) {
        if (hints == null) {
            return true;
        }
        for (String hint : hints) {
            if (!TextUtils.isEmpty(hint) && !ArrayUtils.contains(this.mHints, hint)) {
                return false;
            }
        }
        return true;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public boolean hasAnyHints(@Slice.SliceHint String... hints) {
        if (hints == null) {
            return false;
        }
        for (String hint : hints) {
            if (ArrayUtils.contains(this.mHints, hint)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeObj(android.os.Bundle r4, java.lang.Object r5, java.lang.String r6) {
        /*
            r3 = this;
            int r0 = r6.hashCode()
            switch(r0) {
                case -1422950858: goto L_0x0044;
                case 104431: goto L_0x003a;
                case 3327612: goto L_0x0030;
                case 3556653: goto L_0x0026;
                case 100313435: goto L_0x001c;
                case 100358090: goto L_0x0012;
                case 109526418: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004e
        L_0x0008:
            java.lang.String r0 = "slice"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 2
            goto L_0x004f
        L_0x0012:
            java.lang.String r0 = "input"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 1
            goto L_0x004f
        L_0x001c:
            java.lang.String r0 = "image"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 0
            goto L_0x004f
        L_0x0026:
            java.lang.String r0 = "text"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 4
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "long"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 6
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "int"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 5
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "action"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 3
            goto L_0x004f
        L_0x004e:
            r0 = -1
        L_0x004f:
            switch(r0) {
                case 0: goto L_0x00ab;
                case 1: goto L_0x00a2;
                case 2: goto L_0x0095;
                case 3: goto L_0x0078;
                case 4: goto L_0x006f;
                case 5: goto L_0x0061;
                case 6: goto L_0x0053;
                default: goto L_0x0052;
            }
        L_0x0052:
            goto L_0x00b8
        L_0x0053:
            java.lang.String r0 = "obj"
            java.lang.Object r1 = r3.mObj
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r4.putLong(r0, r1)
            goto L_0x00b8
        L_0x0061:
            java.lang.String r0 = "obj"
            java.lang.Object r1 = r3.mObj
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r4.putInt(r0, r1)
            goto L_0x00b8
        L_0x006f:
            java.lang.String r0 = "obj"
            r1 = r5
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            r4.putCharSequence(r0, r1)
            goto L_0x00b8
        L_0x0078:
            java.lang.String r0 = "obj"
            r1 = r5
            android.support.v4.util.Pair r1 = (android.support.v4.util.Pair) r1
            F r1 = r1.first
            android.app.PendingIntent r1 = (android.app.PendingIntent) r1
            r4.putParcelable(r0, r1)
            java.lang.String r0 = "obj_2"
            r1 = r5
            android.support.v4.util.Pair r1 = (android.support.v4.util.Pair) r1
            S r1 = r1.second
            androidx.slice.Slice r1 = (androidx.slice.Slice) r1
            android.os.Bundle r1 = r1.toBundle()
            r4.putBundle(r0, r1)
            goto L_0x00b8
        L_0x0095:
            java.lang.String r0 = "obj"
            r1 = r5
            androidx.slice.Slice r1 = (androidx.slice.Slice) r1
            android.os.Bundle r1 = r1.toBundle()
            r4.putParcelable(r0, r1)
            goto L_0x00b8
        L_0x00a2:
            java.lang.String r0 = "obj"
            r1 = r5
            android.os.Parcelable r1 = (android.os.Parcelable) r1
            r4.putParcelable(r0, r1)
            goto L_0x00b8
        L_0x00ab:
            java.lang.String r0 = "obj"
            r1 = r5
            android.support.v4.graphics.drawable.IconCompat r1 = (android.support.v4.graphics.drawable.IconCompat) r1
            android.os.Bundle r1 = r1.toBundle()
            r4.putBundle(r0, r1)
        L_0x00b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.writeObj(android.os.Bundle, java.lang.Object, java.lang.String):void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Object readObj(java.lang.String r4, android.os.Bundle r5) {
        /*
            int r0 = r4.hashCode()
            switch(r0) {
                case -1422950858: goto L_0x0044;
                case 104431: goto L_0x003a;
                case 3327612: goto L_0x0030;
                case 3556653: goto L_0x0026;
                case 100313435: goto L_0x001c;
                case 100358090: goto L_0x0012;
                case 109526418: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004e
        L_0x0008:
            java.lang.String r0 = "slice"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 2
            goto L_0x004f
        L_0x0012:
            java.lang.String r0 = "input"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 1
            goto L_0x004f
        L_0x001c:
            java.lang.String r0 = "image"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 0
            goto L_0x004f
        L_0x0026:
            java.lang.String r0 = "text"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 3
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "long"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 6
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "int"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 5
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "action"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 4
            goto L_0x004f
        L_0x004e:
            r0 = -1
        L_0x004f:
            switch(r0) {
                case 0: goto L_0x00b0;
                case 1: goto L_0x00a9;
                case 2: goto L_0x009d;
                case 3: goto L_0x0096;
                case 4: goto L_0x007f;
                case 5: goto L_0x0074;
                case 6: goto L_0x0069;
                default: goto L_0x0052;
            }
        L_0x0052:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unsupported type "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0069:
            java.lang.String r0 = "obj"
            long r0 = r5.getLong(r0)
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            return r0
        L_0x0074:
            java.lang.String r0 = "obj"
            int r0 = r5.getInt(r0)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x007f:
            android.support.v4.util.Pair r0 = new android.support.v4.util.Pair
            java.lang.String r1 = "obj"
            android.os.Parcelable r1 = r5.getParcelable(r1)
            androidx.slice.Slice r2 = new androidx.slice.Slice
            java.lang.String r3 = "obj_2"
            android.os.Bundle r3 = r5.getBundle(r3)
            r2.<init>(r3)
            r0.<init>(r1, r2)
            return r0
        L_0x0096:
            java.lang.String r0 = "obj"
            java.lang.CharSequence r0 = r5.getCharSequence(r0)
            return r0
        L_0x009d:
            androidx.slice.Slice r0 = new androidx.slice.Slice
            java.lang.String r1 = "obj"
            android.os.Bundle r1 = r5.getBundle(r1)
            r0.<init>(r1)
            return r0
        L_0x00a9:
            java.lang.String r0 = "obj"
            android.os.Parcelable r0 = r5.getParcelable(r0)
            return r0
        L_0x00b0:
            java.lang.String r0 = "obj"
            android.os.Bundle r0 = r5.getBundle(r0)
            android.support.v4.graphics.drawable.IconCompat r0 = android.support.v4.graphics.drawable.IconCompat.createFromBundle(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.readObj(java.lang.String, android.os.Bundle):java.lang.Object");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    @android.support.annotation.RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String typeToString(java.lang.String r2) {
        /*
            int r0 = r2.hashCode()
            switch(r0) {
                case -1422950858: goto L_0x0044;
                case 104431: goto L_0x003a;
                case 3327612: goto L_0x0030;
                case 3556653: goto L_0x0026;
                case 100313435: goto L_0x001c;
                case 100358090: goto L_0x0012;
                case 109526418: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004e
        L_0x0008:
            java.lang.String r0 = "slice"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 0
            goto L_0x004f
        L_0x0012:
            java.lang.String r0 = "input"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 6
            goto L_0x004f
        L_0x001c:
            java.lang.String r0 = "image"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 2
            goto L_0x004f
        L_0x0026:
            java.lang.String r0 = "text"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 1
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "long"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 5
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "int"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 4
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "action"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 3
            goto L_0x004f
        L_0x004e:
            r0 = -1
        L_0x004f:
            switch(r0) {
                case 0: goto L_0x0076;
                case 1: goto L_0x0073;
                case 2: goto L_0x0070;
                case 3: goto L_0x006d;
                case 4: goto L_0x006a;
                case 5: goto L_0x0067;
                case 6: goto L_0x0064;
                default: goto L_0x0052;
            }
        L_0x0052:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unrecognized format: "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0064:
            java.lang.String r0 = "RemoteInput"
            return r0
        L_0x0067:
            java.lang.String r0 = "Long"
            return r0
        L_0x006a:
            java.lang.String r0 = "Int"
            return r0
        L_0x006d:
            java.lang.String r0 = "Action"
            return r0
        L_0x0070:
            java.lang.String r0 = "Image"
            return r0
        L_0x0073:
            java.lang.String r0 = "Text"
            return r0
        L_0x0076:
            java.lang.String r0 = "Slice"
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.typeToString(java.lang.String):java.lang.String");
    }

    public String toString() {
        return toString("");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    @android.support.annotation.RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString(java.lang.String r4) {
        /*
            r3 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r3.getFormat()
            int r2 = r1.hashCode()
            switch(r2) {
                case -1422950858: goto L_0x0043;
                case 104431: goto L_0x0039;
                case 3327612: goto L_0x002f;
                case 3556653: goto L_0x0025;
                case 100313435: goto L_0x001b;
                case 109526418: goto L_0x0011;
                default: goto L_0x0010;
            }
        L_0x0010:
            goto L_0x004d
        L_0x0011:
            java.lang.String r2 = "slice"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x004d
            r1 = 0
            goto L_0x004e
        L_0x001b:
            java.lang.String r2 = "image"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x004d
            r1 = 3
            goto L_0x004e
        L_0x0025:
            java.lang.String r2 = "text"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x004d
            r1 = 2
            goto L_0x004e
        L_0x002f:
            java.lang.String r2 = "long"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x004d
            r1 = 5
            goto L_0x004e
        L_0x0039:
            java.lang.String r2 = "int"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x004d
            r1 = 4
            goto L_0x004e
        L_0x0043:
            java.lang.String r2 = "action"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x004d
            r1 = 1
            goto L_0x004e
        L_0x004d:
            r1 = -1
        L_0x004e:
            switch(r1) {
                case 0: goto L_0x00af;
                case 1: goto L_0x0094;
                case 2: goto L_0x0081;
                case 3: goto L_0x0076;
                case 4: goto L_0x006b;
                case 5: goto L_0x0060;
                default: goto L_0x0051;
            }
        L_0x0051:
            r0.append(r4)
            java.lang.String r1 = r3.getFormat()
            java.lang.String r1 = typeToString(r1)
            r0.append(r1)
            goto L_0x00bb
        L_0x0060:
            r0.append(r4)
            long r1 = r3.getLong()
            r0.append(r1)
            goto L_0x00bb
        L_0x006b:
            r0.append(r4)
            int r1 = r3.getInt()
            r0.append(r1)
            goto L_0x00bb
        L_0x0076:
            r0.append(r4)
            android.support.v4.graphics.drawable.IconCompat r1 = r3.getIcon()
            r0.append(r1)
            goto L_0x00bb
        L_0x0081:
            r0.append(r4)
            r1 = 34
            r0.append(r1)
            java.lang.CharSequence r2 = r3.getText()
            r0.append(r2)
            r0.append(r1)
            goto L_0x00bb
        L_0x0094:
            r0.append(r4)
            android.app.PendingIntent r1 = r3.getAction()
            r0.append(r1)
            java.lang.String r1 = ",\n"
            r0.append(r1)
            androidx.slice.Slice r1 = r3.getSlice()
            java.lang.String r1 = r1.toString(r4)
            r0.append(r1)
            goto L_0x00bb
        L_0x00af:
            androidx.slice.Slice r1 = r3.getSlice()
            java.lang.String r1 = r1.toString(r4)
            r0.append(r1)
        L_0x00bb:
            java.lang.String r1 = "slice"
            java.lang.String r2 = r3.getFormat()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00d1
            r1 = 32
            r0.append(r1)
            java.lang.String[] r1 = r3.mHints
            androidx.slice.Slice.addHints(r0, r1)
        L_0x00d1:
            java.lang.String r1 = ",\n"
            r0.append(r1)
            java.lang.String r1 = r0.toString()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.toString(java.lang.String):java.lang.String");
    }

    public void onPreParceling(boolean isStream) {
        this.mHolder = new SliceItemHolder(this.mFormat, this.mObj, isStream);
    }

    public void onPostParceling() {
        this.mObj = this.mHolder.getObj(this.mFormat);
        this.mHolder = null;
    }
}
