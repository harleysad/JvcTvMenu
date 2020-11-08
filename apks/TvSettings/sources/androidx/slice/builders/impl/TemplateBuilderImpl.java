package androidx.slice.builders.impl;

import android.support.annotation.RestrictTo;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public abstract class TemplateBuilderImpl {
    private Clock mClock;
    private final Slice.Builder mSliceBuilder;
    private final SliceSpec mSpec;

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public abstract void apply(Slice.Builder builder);

    protected TemplateBuilderImpl(Slice.Builder b, SliceSpec spec) {
        this(b, spec, new SystemClock());
    }

    protected TemplateBuilderImpl(Slice.Builder b, SliceSpec spec, Clock clock) {
        this.mSliceBuilder = b;
        this.mSpec = spec;
        this.mClock = clock;
    }

    public Slice build() {
        this.mSliceBuilder.setSpec(this.mSpec);
        apply(this.mSliceBuilder);
        return this.mSliceBuilder.build();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Slice.Builder getBuilder() {
        return this.mSliceBuilder;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Slice.Builder createChildBuilder() {
        return new Slice.Builder(this.mSliceBuilder);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Clock getClock() {
        return this.mClock;
    }
}
