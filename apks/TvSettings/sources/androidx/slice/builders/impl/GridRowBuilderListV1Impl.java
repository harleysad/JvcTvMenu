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
import java.util.ArrayList;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class GridRowBuilderListV1Impl extends TemplateBuilderImpl implements GridRowBuilder {
    private SliceAction mPrimaryAction;

    public GridRowBuilderListV1Impl(@NonNull ListBuilderV1Impl parent) {
        super(parent.createChildBuilder(), (SliceSpec) null);
    }

    public void apply(Slice.Builder builder) {
        builder.addHints("horizontal");
        if (this.mPrimaryAction != null) {
            builder.addSubSlice(this.mPrimaryAction.buildSlice(new Slice.Builder(getBuilder()).addHints("title")));
        }
    }

    public TemplateBuilderImpl createGridRowBuilder() {
        return new CellBuilder(this);
    }

    public TemplateBuilderImpl createGridRowBuilder(Uri uri) {
        return new CellBuilder(uri);
    }

    public void addCell(TemplateBuilderImpl builder) {
        builder.apply(getBuilder());
    }

    public void setSeeMoreCell(@NonNull TemplateBuilderImpl builder) {
        builder.getBuilder().addHints("see_more");
        builder.apply(getBuilder());
    }

    public void setSeeMoreAction(PendingIntent intent) {
        getBuilder().addSubSlice(new Slice.Builder(getBuilder()).addHints("see_more").addAction(intent, new Slice.Builder(getBuilder()).build(), (String) null).build());
    }

    public void setPrimaryAction(SliceAction action) {
        this.mPrimaryAction = action;
    }

    public void setContentDescription(CharSequence description) {
        getBuilder().addText(description, "content_description", new String[0]);
    }

    public void setLayoutDirection(int layoutDirection) {
        getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
    }

    public static final class CellBuilder extends TemplateBuilderImpl implements GridRowBuilder.CellBuilder {
        private PendingIntent mContentIntent;

        public CellBuilder(@NonNull GridRowBuilderListV1Impl parent) {
            super(parent.createChildBuilder(), (SliceSpec) null);
        }

        public CellBuilder(@NonNull Uri uri) {
            super(new Slice.Builder(uri), (SliceSpec) null);
        }

        @NonNull
        public void addText(@NonNull CharSequence text) {
            addText(text, false);
        }

        public void addText(@Nullable CharSequence text, boolean isLoading) {
            getBuilder().addText(text, (String) null, isLoading ? new String[]{"partial"} : new String[0]);
        }

        @NonNull
        public void addTitleText(@NonNull CharSequence text) {
            addTitleText(text, false);
        }

        @NonNull
        public void addTitleText(@Nullable CharSequence text, boolean isLoading) {
            getBuilder().addText(text, (String) null, isLoading ? new String[]{"partial", "title"} : new String[]{"title"});
        }

        @NonNull
        public void addImage(@NonNull IconCompat image, int imageMode) {
            addImage(image, imageMode, false);
        }

        @NonNull
        public void addImage(@Nullable IconCompat image, int imageMode, boolean isLoading) {
            ArrayList<String> hints = new ArrayList<>();
            if (imageMode != 0) {
                hints.add("no_tint");
            }
            if (imageMode == 2) {
                hints.add("large");
            }
            if (isLoading) {
                hints.add("partial");
            }
            getBuilder().addIcon(image, (String) null, (List<String>) hints);
        }

        @NonNull
        public void setContentIntent(@NonNull PendingIntent intent) {
            this.mContentIntent = intent;
        }

        public void setContentDescription(CharSequence description) {
            getBuilder().addText(description, "content_description", new String[0]);
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY})
        public void apply(Slice.Builder b) {
            getBuilder().addHints("horizontal");
            if (this.mContentIntent != null) {
                b.addAction(this.mContentIntent, getBuilder().build(), (String) null);
            } else {
                b.addSubSlice(getBuilder().build());
            }
        }
    }
}
