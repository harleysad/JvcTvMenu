package androidx.slice.builders.impl;

import android.app.PendingIntent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.SliceAction;
import androidx.slice.builders.impl.GridRowBuilder;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class GridRowBuilderBasicImpl extends TemplateBuilderImpl implements GridRowBuilder {
    public GridRowBuilderBasicImpl(@NonNull ListBuilderBasicImpl parent) {
        super(parent.createChildBuilder(), (SliceSpec) null);
    }

    public TemplateBuilderImpl createGridRowBuilder() {
        return new CellBuilder(this);
    }

    public TemplateBuilderImpl createGridRowBuilder(Uri uri) {
        return new CellBuilder(uri);
    }

    public void addCell(TemplateBuilderImpl impl) {
    }

    public void setSeeMoreCell(TemplateBuilderImpl impl) {
    }

    public void setSeeMoreAction(PendingIntent intent) {
    }

    public void setPrimaryAction(SliceAction action) {
    }

    public void setContentDescription(CharSequence description) {
    }

    public void setLayoutDirection(int layoutDirection) {
    }

    public void apply(Slice.Builder builder) {
    }

    public static final class CellBuilder extends TemplateBuilderImpl implements GridRowBuilder.CellBuilder {
        public CellBuilder(@NonNull GridRowBuilderBasicImpl parent) {
            super(parent.createChildBuilder(), (SliceSpec) null);
        }

        public CellBuilder(@NonNull Uri uri) {
            super(new Slice.Builder(uri), (SliceSpec) null);
        }

        @NonNull
        public void addText(@NonNull CharSequence text) {
        }

        public void addText(@Nullable CharSequence text, boolean isLoading) {
        }

        @NonNull
        public void addTitleText(@NonNull CharSequence text) {
        }

        @NonNull
        public void addTitleText(@Nullable CharSequence text, boolean isLoading) {
        }

        @NonNull
        public void addImage(@NonNull IconCompat image, int imageMode) {
        }

        @NonNull
        public void addImage(@Nullable IconCompat image, int imageMode, boolean isLoading) {
        }

        @NonNull
        public void setContentIntent(@NonNull PendingIntent intent) {
        }

        public void setContentDescription(CharSequence description) {
        }

        public void apply(Slice.Builder builder) {
        }
    }
}
