package androidx.slice.builders;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.RestrictTo;
import android.util.Log;
import android.util.Pair;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.slice.SliceSpecs;
import androidx.slice.SystemClock;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TemplateSliceBuilder {
    private static final String TAG = "TemplateSliceBuilder";
    private final Slice.Builder mBuilder;
    private final Context mContext;
    private final TemplateBuilderImpl mImpl;
    private List<SliceSpec> mSpecs;

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public abstract void setImpl(TemplateBuilderImpl templateBuilderImpl);

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    protected TemplateSliceBuilder(TemplateBuilderImpl impl) {
        this.mContext = null;
        this.mBuilder = null;
        this.mImpl = impl;
        setImpl(impl);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    protected TemplateSliceBuilder(Slice.Builder b, Context context) {
        this.mBuilder = b;
        this.mContext = context;
        this.mSpecs = getSpecs();
        this.mImpl = selectImpl();
        if (this.mImpl != null) {
            setImpl(this.mImpl);
            return;
        }
        throw new IllegalArgumentException("No valid specs found");
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public TemplateSliceBuilder(Context context, Uri uri) {
        this.mBuilder = new Slice.Builder(uri);
        this.mContext = context;
        this.mSpecs = getSpecs();
        this.mImpl = selectImpl();
        if (this.mImpl != null) {
            setImpl(this.mImpl);
            return;
        }
        throw new IllegalArgumentException("No valid specs found");
    }

    public Slice build() {
        return this.mImpl.build();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Slice.Builder getBuilder() {
        return this.mBuilder;
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public TemplateBuilderImpl selectImpl() {
        return null;
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public boolean checkCompatible(SliceSpec candidate) {
        int size = this.mSpecs.size();
        for (int i = 0; i < size; i++) {
            if (this.mSpecs.get(i).canRender(candidate)) {
                return true;
            }
        }
        return false;
    }

    private List<SliceSpec> getSpecs() {
        if (SliceProvider.getCurrentSpecs() != null) {
            return new ArrayList(SliceProvider.getCurrentSpecs());
        }
        Log.w(TAG, "Not currently bunding a slice");
        return Arrays.asList(new SliceSpec[]{SliceSpecs.BASIC});
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Clock getClock() {
        if (SliceProvider.getClock() != null) {
            return SliceProvider.getClock();
        }
        return new SystemClock();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    static <T> Pair<SliceSpec, Class<? extends TemplateBuilderImpl>> pair(SliceSpec spec, Class<T> cls) {
        return new Pair<>(spec, cls);
    }
}
