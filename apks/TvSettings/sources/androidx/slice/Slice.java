package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.os.BuildCompat;
import android.support.v4.util.Preconditions;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import androidx.versionedparcelable.VersionedParcelable;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class Slice implements VersionedParcelable {
    private static final String HINTS = "hints";
    private static final String ITEMS = "items";
    private static final String SPEC_REVISION = "revision";
    private static final String SPEC_TYPE = "type";
    private static final String URI = "uri";
    @SliceHint
    String[] mHints = new String[0];
    SliceItem[] mItems = new SliceItem[0];
    SliceSpec mSpec;
    String mUri;

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public @interface SliceHint {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    Slice(ArrayList<SliceItem> items, @SliceHint String[] hints, Uri uri, SliceSpec spec) {
        this.mHints = hints;
        this.mItems = (SliceItem[]) items.toArray(new SliceItem[items.size()]);
        this.mUri = uri.toString();
        this.mSpec = spec;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Slice() {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Slice(Bundle in) {
        this.mHints = in.getStringArray(HINTS);
        Parcelable[] items = in.getParcelableArray(ITEMS);
        this.mItems = new SliceItem[items.length];
        for (int i = 0; i < this.mItems.length; i++) {
            if (items[i] instanceof Bundle) {
                this.mItems[i] = new SliceItem((Bundle) items[i]);
            }
        }
        this.mUri = in.getParcelable("uri").toString();
        this.mSpec = in.containsKey(SPEC_TYPE) ? new SliceSpec(in.getString(SPEC_TYPE), in.getInt(SPEC_REVISION)) : null;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putStringArray(HINTS, this.mHints);
        Parcelable[] p = new Parcelable[this.mItems.length];
        for (int i = 0; i < this.mItems.length; i++) {
            p[i] = this.mItems[i].toBundle();
        }
        b.putParcelableArray(ITEMS, p);
        b.putParcelable("uri", Uri.parse(this.mUri));
        if (this.mSpec != null) {
            b.putString(SPEC_TYPE, this.mSpec.getType());
            b.putInt(SPEC_REVISION, this.mSpec.getRevision());
        }
        return b;
    }

    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public SliceSpec getSpec() {
        return this.mSpec;
    }

    public Uri getUri() {
        return Uri.parse(this.mUri);
    }

    public List<SliceItem> getItems() {
        return Arrays.asList(this.mItems);
    }

    @SliceHint
    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean hasHint(@SliceHint String hint) {
        return ArrayUtils.contains(this.mHints, hint);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static class Builder {
        @SliceHint
        private ArrayList<String> mHints = new ArrayList<>();
        private ArrayList<SliceItem> mItems = new ArrayList<>();
        private SliceSpec mSpec;
        private final Uri mUri;

        public Builder(@NonNull Uri uri) {
            this.mUri = uri;
        }

        public Builder(@NonNull Builder parent) {
            this.mUri = parent.mUri.buildUpon().appendPath("_gen").appendPath(String.valueOf(this.mItems.size())).build();
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public Builder setSpec(SliceSpec spec) {
            this.mSpec = spec;
            return this;
        }

        public Builder addHints(@SliceHint String... hints) {
            this.mHints.addAll(Arrays.asList(hints));
            return this;
        }

        public Builder addHints(@SliceHint List<String> hints) {
            return addHints((String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addSubSlice(@NonNull Slice slice) {
            Preconditions.checkNotNull(slice);
            return addSubSlice(slice, (String) null);
        }

        public Builder addSubSlice(@NonNull Slice slice, String subType) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem((Object) slice, SliceProviderCompat.EXTRA_SLICE, subType, (String[]) slice.getHints().toArray(new String[slice.getHints().size()])));
            return this;
        }

        public Builder addAction(@NonNull PendingIntent action, @NonNull Slice s, @Nullable String subType) {
            Preconditions.checkNotNull(action);
            Preconditions.checkNotNull(s);
            this.mItems.add(new SliceItem(action, s, BaseDialogFragment.TAG_ACTION, subType, s != null ? (String[]) s.getHints().toArray(new String[s.getHints().size()]) : new String[0]));
            return this;
        }

        public Builder addAction(@NonNull SliceItem.ActionHandler action, @NonNull Slice s, @Nullable String subType) {
            Preconditions.checkNotNull(s);
            this.mItems.add(new SliceItem(action, s, BaseDialogFragment.TAG_ACTION, subType, s != null ? (String[]) s.getHints().toArray(new String[s.getHints().size()]) : new String[0]));
            return this;
        }

        public Builder addText(CharSequence text, @Nullable String subType, @SliceHint String... hints) {
            this.mItems.add(new SliceItem((Object) text, "text", subType, hints));
            return this;
        }

        public Builder addText(CharSequence text, @Nullable String subType, @SliceHint List<String> hints) {
            return addText(text, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addIcon(IconCompat icon, @Nullable String subType, @SliceHint String... hints) {
            Preconditions.checkNotNull(icon);
            this.mItems.add(new SliceItem((Object) icon, "image", subType, hints));
            return this;
        }

        public Builder addIcon(IconCompat icon, @Nullable String subType, @SliceHint List<String> hints) {
            Preconditions.checkNotNull(icon);
            return addIcon(icon, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public Builder addRemoteInput(RemoteInput remoteInput, @Nullable String subType, @SliceHint List<String> hints) {
            Preconditions.checkNotNull(remoteInput);
            return addRemoteInput(remoteInput, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public Builder addRemoteInput(RemoteInput remoteInput, @Nullable String subType, @SliceHint String... hints) {
            Preconditions.checkNotNull(remoteInput);
            this.mItems.add(new SliceItem((Object) remoteInput, "input", subType, hints));
            return this;
        }

        public Builder addInt(int value, @Nullable String subType, @SliceHint String... hints) {
            this.mItems.add(new SliceItem((Object) Integer.valueOf(value), "int", subType, hints));
            return this;
        }

        public Builder addInt(int value, @Nullable String subType, @SliceHint List<String> hints) {
            return addInt(value, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addLong(long time, @Nullable String subType, @SliceHint String... hints) {
            this.mItems.add(new SliceItem((Object) Long.valueOf(time), "long", subType, hints));
            return this;
        }

        public Builder addLong(long time, @Nullable String subType, @SliceHint List<String> hints) {
            return addLong(time, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        @Deprecated
        public Builder addTimestamp(long time, @Nullable String subType, @SliceHint String... hints) {
            this.mItems.add(new SliceItem((Object) Long.valueOf(time), "long", subType, hints));
            return this;
        }

        public Builder addTimestamp(long time, @Nullable String subType, @SliceHint List<String> hints) {
            return addTimestamp(time, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public Builder addItem(SliceItem item) {
            this.mItems.add(item);
            return this;
        }

        public Slice build() {
            return new Slice(this.mItems, (String[]) this.mHints.toArray(new String[this.mHints.size()]), this.mUri, this.mSpec);
        }
    }

    public String toString() {
        return toString("");
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append("slice ");
        addHints(sb, this.mHints);
        sb.append("{\n");
        String nextIndent = indent + "  ";
        for (SliceItem item : this.mItems) {
            sb.append(item.toString(nextIndent));
        }
        sb.append(indent);
        sb.append("}");
        return sb.toString();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static void addHints(StringBuilder sb, String[] hints) {
        if (hints != null && hints.length != 0) {
            sb.append("(");
            int end = hints.length - 1;
            for (int i = 0; i < end; i++) {
                sb.append(hints[i]);
                sb.append(", ");
            }
            sb.append(hints[end]);
            sb.append(") ");
        }
    }

    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Slice bindSlice(Context context, @NonNull Uri uri, Set<SliceSpec> supportedSpecs) {
        if (BuildCompat.isAtLeastP()) {
            return callBindSlice(context, uri, supportedSpecs);
        }
        return SliceProviderCompat.bindSlice(context, uri, supportedSpecs);
    }

    @RequiresApi(28)
    private static Slice callBindSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        return SliceConvert.wrap(((SliceManager) context.getSystemService(SliceManager.class)).bindSlice(uri, SliceConvert.unwrap(supportedSpecs)));
    }
}
