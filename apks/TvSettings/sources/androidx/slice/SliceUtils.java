package androidx.slice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.core.SliceHints;
import androidx.slice.core.SliceQuery;
import androidx.versionedparcelable.ParcelUtils;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SliceUtils {
    @Deprecated
    public static final int LOADING_ALL = 0;
    @Deprecated
    public static final int LOADING_COMPLETE = 2;
    @Deprecated
    public static final int LOADING_PARTIAL = 1;

    public interface SliceActionListener {
        void onSliceAction(Uri uri, Context context, Intent intent);
    }

    private SliceUtils() {
    }

    @Deprecated
    public static void serializeSlice(@NonNull Slice s, @NonNull Context context, @NonNull OutputStream output, @NonNull String encoding, @NonNull SerializeOptions options) throws IOException, IllegalArgumentException {
        serializeSlice(s, context, output, options);
    }

    public static void serializeSlice(@NonNull Slice s, @NonNull Context context, @NonNull OutputStream output, @NonNull SerializeOptions options) throws IllegalArgumentException {
        ParcelUtils.toOutputStream(convert(context, s, options), output);
    }

    @SuppressLint({"NewApi"})
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static Slice convert(Context context, Slice slice, SerializeOptions options) {
        Slice.Builder builder = new Slice.Builder(slice.getUri());
        builder.setSpec(slice.getSpec());
        builder.addHints(slice.getHints());
        for (SliceItem item : slice.getItems()) {
            String format = item.getFormat();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals(BaseDialogFragment.TAG_ACTION)) {
                        c = 3;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 5;
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
                        c = 4;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals("image")) {
                        c = 1;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 2;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals(SliceProviderCompat.EXTRA_SLICE)) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addSubSlice(convert(context, item.getSlice(), options), item.getSubType());
                    break;
                case 1:
                    switch (options.getImageMode()) {
                        case 0:
                            throw new IllegalArgumentException("Cannot serialize icon");
                        case 2:
                            builder.addIcon(convert(context, item.getIcon(), options), item.getSubType(), item.getHints());
                            break;
                    }
                case 2:
                    if (options.getActionMode() != 0) {
                        break;
                    } else {
                        builder.addRemoteInput(item.getRemoteInput(), item.getSubType(), item.getHints());
                        break;
                    }
                case 3:
                    switch (options.getActionMode()) {
                        case 0:
                            throw new IllegalArgumentException("Cannot serialize action");
                        case 2:
                            builder.addAction(new SliceItem.ActionHandler() {
                                public void onAction(SliceItem item, Context context, Intent intent) {
                                }
                            }, convert(context, item.getSlice(), options), item.getSubType());
                            break;
                    }
                case 4:
                    builder.addText(item.getText(), item.getSubType(), item.getHints());
                    break;
                case 5:
                    builder.addInt(item.getInt(), item.getSubType(), item.getHints());
                    break;
                case 6:
                    builder.addLong(item.getLong(), item.getSubType(), item.getHints());
                    break;
            }
        }
        return builder.build();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static IconCompat convert(Context context, IconCompat icon, SerializeOptions options) {
        if (icon.getType() == 2) {
            return icon;
        }
        byte[] data = SliceXml.convertToBytes(icon, context, options);
        return IconCompat.createWithData(data, 0, data.length);
    }

    @NonNull
    public static Slice parseSlice(@NonNull Context context, @NonNull InputStream input, @NonNull String encoding, @NonNull final SliceActionListener listener) throws IOException, SliceParseException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(input);
        String parcelName = Slice.class.getName();
        bufferedInputStream.mark(parcelName.length() + 4);
        boolean usesParcel = doesStreamStartWith(parcelName, bufferedInputStream);
        bufferedInputStream.reset();
        if (!usesParcel) {
            return SliceXml.parseSlice(context, bufferedInputStream, encoding, listener);
        }
        Slice slice = (Slice) ParcelUtils.fromInputStream(bufferedInputStream);
        setActions(slice, new SliceItem.ActionHandler() {
            public void onAction(SliceItem item, Context context, Intent intent) {
                listener.onSliceAction(item.getSlice().getUri(), context, intent);
            }
        });
        return slice;
    }

    private static void setActions(Slice slice, SliceItem.ActionHandler listener) {
        for (SliceItem sliceItem : slice.getItems()) {
            String format = sliceItem.getFormat();
            char c = 65535;
            int hashCode = format.hashCode();
            if (hashCode != -1422950858) {
                if (hashCode == 109526418 && format.equals(SliceProviderCompat.EXTRA_SLICE)) {
                    c = 1;
                }
            } else if (format.equals(BaseDialogFragment.TAG_ACTION)) {
                c = 0;
            }
            switch (c) {
                case 0:
                    sliceItem.mObj = new Pair(listener, ((Pair) sliceItem.mObj).second);
                    setActions(sliceItem.getSlice(), listener);
                    break;
                case 1:
                    setActions(sliceItem.getSlice(), listener);
                    break;
            }
        }
    }

    private static boolean doesStreamStartWith(String parcelName, BufferedInputStream inputStream) {
        byte[] data = parcelName.getBytes(Charset.forName("UTF-16"));
        byte[] buf = new byte[data.length];
        try {
            if (inputStream.read(buf, 0, 4) >= 0 && inputStream.read(buf, 0, buf.length) >= 0) {
                return Arrays.equals(buf, data);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static class SerializeOptions {
        public static final int MODE_CONVERT = 2;
        public static final int MODE_REMOVE = 1;
        public static final int MODE_THROW = 0;
        private int mActionMode = 0;
        private Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.PNG;
        private int mImageMode = 0;
        private int mMaxHeight = 1000;
        private int mMaxWidth = 1000;
        private int mQuality = 100;

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0036 A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0037  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x003c  */
        @android.support.annotation.RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void checkThrow(java.lang.String r4) {
            /*
                r3 = this;
                int r0 = r4.hashCode()
                r1 = -1422950858(0xffffffffab2f7e36, float:-6.234764E-13)
                if (r0 == r1) goto L_0x0028
                r1 = 100313435(0x5faa95b, float:2.3572098E-35)
                if (r0 == r1) goto L_0x001e
                r1 = 100358090(0x5fb57ca, float:2.3636175E-35)
                if (r0 == r1) goto L_0x0014
                goto L_0x0032
            L_0x0014:
                java.lang.String r0 = "input"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x0032
                r0 = 1
                goto L_0x0033
            L_0x001e:
                java.lang.String r0 = "image"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x0032
                r0 = 2
                goto L_0x0033
            L_0x0028:
                java.lang.String r0 = "action"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x0032
                r0 = 0
                goto L_0x0033
            L_0x0032:
                r0 = -1
            L_0x0033:
                switch(r0) {
                    case 0: goto L_0x003c;
                    case 1: goto L_0x003c;
                    case 2: goto L_0x0037;
                    default: goto L_0x0036;
                }
            L_0x0036:
                return
            L_0x0037:
                int r0 = r3.mImageMode
                if (r0 == 0) goto L_0x0041
                return
            L_0x003c:
                int r0 = r3.mActionMode
                if (r0 == 0) goto L_0x0041
                return
            L_0x0041:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r4)
                java.lang.String r2 = " cannot be serialized"
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceUtils.SerializeOptions.checkThrow(java.lang.String):void");
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public int getActionMode() {
            return this.mActionMode;
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public int getImageMode() {
            return this.mImageMode;
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public int getMaxWidth() {
            return this.mMaxWidth;
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public int getMaxHeight() {
            return this.mMaxHeight;
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public Bitmap.CompressFormat getFormat() {
            return this.mFormat;
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public int getQuality() {
            return this.mQuality;
        }

        public SerializeOptions setActionMode(int mode) {
            this.mActionMode = mode;
            return this;
        }

        public SerializeOptions setImageMode(int mode) {
            this.mImageMode = mode;
            return this;
        }

        public SerializeOptions setMaxImageWidth(int width) {
            this.mMaxWidth = width;
            return this;
        }

        public SerializeOptions setMaxImageHeight(int height) {
            this.mMaxHeight = height;
            return this;
        }

        public SerializeOptions setImageConversionFormat(Bitmap.CompressFormat format, int quality) {
            this.mFormat = format;
            this.mQuality = quality;
            return this;
        }
    }

    @Deprecated
    public static int getLoadingState(@NonNull Slice slice) {
        boolean hasHintPartial = SliceQuery.find(slice, (String) null, "partial", (String) null) != null;
        if (slice.getItems().size() == 0) {
            return 0;
        }
        if (hasHintPartial) {
            return 1;
        }
        return 2;
    }

    @Nullable
    @Deprecated
    public static List<SliceItem> getSliceActions(@NonNull Slice slice) {
        SliceItem actionGroup = SliceQuery.find(slice, SliceProviderCompat.EXTRA_SLICE, "actions", (String) null);
        String[] hints = {"actions", "shortcut"};
        if (actionGroup != null) {
            return SliceQuery.findAll(actionGroup, SliceProviderCompat.EXTRA_SLICE, hints, (String[]) null);
        }
        return null;
    }

    @Nullable
    @Deprecated
    public static List<String> getSliceKeywords(@NonNull Slice slice) {
        List<SliceItem> itemList;
        SliceItem keywordGroup = SliceQuery.find(slice, SliceProviderCompat.EXTRA_SLICE, SliceHints.HINT_KEYWORDS, (String) null);
        if (keywordGroup == null || (itemList = SliceQuery.findAll(keywordGroup, "text")) == null) {
            return null;
        }
        ArrayList<String> stringList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            String keyword = (String) itemList.get(i).getText();
            if (!TextUtils.isEmpty(keyword)) {
                stringList.add(keyword);
            }
        }
        return stringList;
    }

    public static class SliceParseException extends Exception {
        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public SliceParseException(String s, Throwable e) {
            super(s, e);
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public SliceParseException(String s) {
            super(s);
        }
    }
}
