package androidx.slice.builders.impl;

import android.app.PendingIntent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;
import androidx.slice.builders.SliceAction;
import androidx.slice.builders.impl.ListBuilder;
import androidx.slice.core.SliceHints;
import com.android.tv.twopanelsettings.slices.SlicesConstants;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class ListBuilderV1Impl extends TemplateBuilderImpl implements ListBuilder {
    private boolean mIsError;
    private List<Slice> mSliceActions;
    private Slice mSliceHeader;

    public ListBuilderV1Impl(Slice.Builder b, SliceSpec spec) {
        this(b, spec, new SystemClock());
    }

    public ListBuilderV1Impl(Slice.Builder b, SliceSpec spec, Clock clock) {
        super(b, spec, clock);
    }

    public void apply(Slice.Builder builder) {
        builder.addLong(getClock().currentTimeMillis(), SliceHints.SUBTYPE_MILLIS, SliceHints.HINT_LAST_UPDATED);
        if (this.mSliceHeader != null) {
            builder.addSubSlice(this.mSliceHeader);
        }
        if (this.mSliceActions != null) {
            Slice.Builder sb = new Slice.Builder(builder);
            for (int i = 0; i < this.mSliceActions.size(); i++) {
                sb.addSubSlice(this.mSliceActions.get(i));
            }
            builder.addSubSlice(sb.addHints("actions").build());
        }
        if (this.mIsError) {
            builder.addHints(SlicesConstants.PARAMETER_ERROR);
        }
    }

    @NonNull
    public void addRow(@NonNull TemplateBuilderImpl builder) {
        builder.getBuilder().addHints("list_item");
        getBuilder().addSubSlice(builder.build());
    }

    @NonNull
    public void addGridRow(@NonNull TemplateBuilderImpl builder) {
        builder.getBuilder().addHints("list_item");
        getBuilder().addSubSlice(builder.build());
    }

    public void setHeader(@NonNull TemplateBuilderImpl builder) {
        this.mSliceHeader = builder.build();
    }

    public void addAction(@NonNull SliceAction action) {
        if (this.mSliceActions == null) {
            this.mSliceActions = new ArrayList();
        }
        this.mSliceActions.add(action.buildSlice(new Slice.Builder(getBuilder()).addHints("actions")));
    }

    public void addInputRange(TemplateBuilderImpl builder) {
        getBuilder().addSubSlice(builder.build(), "range");
    }

    public void addRange(TemplateBuilderImpl builder) {
        getBuilder().addSubSlice(builder.build(), "range");
    }

    public void setSeeMoreRow(TemplateBuilderImpl builder) {
        builder.getBuilder().addHints("see_more");
        getBuilder().addSubSlice(builder.build());
    }

    public void setSeeMoreAction(PendingIntent intent) {
        getBuilder().addSubSlice(new Slice.Builder(getBuilder()).addHints("see_more").addAction(intent, new Slice.Builder(getBuilder()).addHints("see_more").build(), (String) null).build());
    }

    public static class RangeBuilderImpl extends TemplateBuilderImpl implements ListBuilder.RangeBuilder {
        private CharSequence mContentDescr;
        private int mLayoutDir = -1;
        private int mMax = 100;
        private int mMin = 0;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private CharSequence mTitle;
        private int mValue = 0;
        private boolean mValueSet = false;

        public RangeBuilderImpl(Slice.Builder sb) {
            super(sb, (SliceSpec) null);
        }

        public void setMin(int min) {
            this.mMin = min;
        }

        public void setMax(int max) {
            this.mMax = max;
        }

        public void setValue(int value) {
            this.mValue = value;
            this.mValueSet = true;
        }

        public void setTitle(@NonNull CharSequence title) {
            this.mTitle = title;
        }

        public void setSubtitle(@NonNull CharSequence title) {
            this.mSubtitle = title;
        }

        public void setPrimaryAction(@NonNull SliceAction action) {
            this.mPrimaryAction = action;
        }

        public void setContentDescription(@NonNull CharSequence description) {
            this.mContentDescr = description;
        }

        public void setLayoutDirection(int layoutDirection) {
            this.mLayoutDir = layoutDirection;
        }

        public void apply(Slice.Builder builder) {
            if (!this.mValueSet) {
                this.mValue = this.mMin;
            }
            if (this.mMin > this.mValue || this.mValue > this.mMax || this.mMin >= this.mMax) {
                throw new IllegalArgumentException("Invalid range values, min=" + this.mMin + ", value=" + this.mValue + ", max=" + this.mMax + " ensure value falls within (min, max) and min < max.");
            }
            if (this.mTitle != null) {
                builder.addText(this.mTitle, (String) null, "title");
            }
            if (this.mSubtitle != null) {
                builder.addText(this.mSubtitle, (String) null, new String[0]);
            }
            if (this.mContentDescr != null) {
                builder.addText(this.mContentDescr, "content_description", new String[0]);
            }
            if (this.mPrimaryAction != null) {
                builder.addSubSlice(this.mPrimaryAction.buildSlice(new Slice.Builder(getBuilder()).addHints("title", "shortcut")), (String) null);
            }
            if (this.mLayoutDir != -1) {
                builder.addInt(this.mLayoutDir, "layout_direction", new String[0]);
            }
            builder.addHints("list_item").addInt(this.mMin, SliceHints.SUBTYPE_MIN, new String[0]).addInt(this.mMax, "max", new String[0]).addInt(this.mValue, SaveValue.GLOBAL_VALUE_VALUE, new String[0]);
        }
    }

    public static class InputRangeBuilderImpl extends RangeBuilderImpl implements ListBuilder.InputRangeBuilder {
        private PendingIntent mAction;
        private IconCompat mThumb;

        public InputRangeBuilderImpl(Slice.Builder sb) {
            super(sb);
        }

        public void setInputAction(@NonNull PendingIntent action) {
            this.mAction = action;
        }

        public void setThumb(@NonNull IconCompat thumb) {
            this.mThumb = thumb;
        }

        public void apply(Slice.Builder builder) {
            if (this.mAction != null) {
                Slice.Builder sb = new Slice.Builder(builder);
                super.apply(sb);
                if (this.mThumb != null) {
                    sb.addIcon(this.mThumb, (String) null, new String[0]);
                }
                builder.addAction(this.mAction, sb.build(), "range").addHints("list_item");
                return;
            }
            throw new IllegalStateException("Input ranges must have an associated action.");
        }
    }

    @NonNull
    public void setColor(@ColorInt int color) {
        getBuilder().addInt(color, "color", new String[0]);
    }

    public void setKeywords(@NonNull List<String> keywords) {
        Slice.Builder sb = new Slice.Builder(getBuilder());
        for (int i = 0; i < keywords.size(); i++) {
            sb.addText((CharSequence) keywords.get(i), (String) null, new String[0]);
        }
        getBuilder().addSubSlice(sb.addHints(SliceHints.HINT_KEYWORDS).build());
    }

    public void setTtl(long ttl) {
        long expiry = -1;
        if (ttl != -1) {
            expiry = getClock().currentTimeMillis() + ttl;
        }
        getBuilder().addTimestamp(expiry, SliceHints.SUBTYPE_MILLIS, SliceHints.HINT_TTL);
    }

    public void setIsError(boolean isError) {
        this.mIsError = isError;
    }

    public void setLayoutDirection(int layoutDirection) {
        getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
    }

    public TemplateBuilderImpl createRowBuilder() {
        return new RowBuilderImpl(this);
    }

    public TemplateBuilderImpl createRowBuilder(Uri uri) {
        return new RowBuilderImpl(uri);
    }

    public TemplateBuilderImpl createInputRangeBuilder() {
        return new InputRangeBuilderImpl(createChildBuilder());
    }

    public TemplateBuilderImpl createRangeBuilder() {
        return new RangeBuilderImpl(createChildBuilder());
    }

    public TemplateBuilderImpl createGridBuilder() {
        return new GridRowBuilderListV1Impl(this);
    }

    public TemplateBuilderImpl createHeaderBuilder() {
        return new HeaderBuilderImpl(this);
    }

    public TemplateBuilderImpl createHeaderBuilder(Uri uri) {
        return new HeaderBuilderImpl(uri);
    }

    public static class RowBuilderImpl extends TemplateBuilderImpl implements ListBuilder.RowBuilder {
        private CharSequence mContentDescr;
        private ArrayList<Slice> mEndItems = new ArrayList<>();
        private SliceAction mPrimaryAction;
        private Slice mStartItem;
        private SliceItem mSubtitleItem;
        private SliceItem mTitleItem;

        public RowBuilderImpl(@NonNull ListBuilderV1Impl parent) {
            super(parent.createChildBuilder(), (SliceSpec) null);
        }

        public RowBuilderImpl(@NonNull Uri uri) {
            super(new Slice.Builder(uri), (SliceSpec) null);
        }

        public RowBuilderImpl(Slice.Builder builder) {
            super(builder, (SliceSpec) null);
        }

        @NonNull
        public void setTitleItem(long timeStamp) {
            this.mStartItem = new Slice.Builder(getBuilder()).addTimestamp(timeStamp, (String) null, new String[0]).addHints("title").build();
        }

        @NonNull
        public void setTitleItem(IconCompat icon, int imageMode) {
            setTitleItem(icon, imageMode, false);
        }

        @NonNull
        public void setTitleItem(IconCompat icon, int imageMode, boolean isLoading) {
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
            Slice.Builder sb = new Slice.Builder(getBuilder()).addIcon(icon, (String) null, (List<String>) hints);
            if (isLoading) {
                sb.addHints("partial");
            }
            this.mStartItem = sb.addHints("title").build();
        }

        @NonNull
        public void setTitleItem(@NonNull SliceAction action) {
            setTitleItem(action, false);
        }

        public void setTitleItem(SliceAction action, boolean isLoading) {
            Slice.Builder sb = new Slice.Builder(getBuilder()).addHints("title");
            if (isLoading) {
                sb.addHints("partial");
            }
            this.mStartItem = action.buildSlice(sb);
        }

        @NonNull
        public void setPrimaryAction(@NonNull SliceAction action) {
            this.mPrimaryAction = action;
        }

        @NonNull
        public void setTitle(CharSequence title) {
            setTitle(title, false);
        }

        public void setTitle(CharSequence title, boolean isLoading) {
            this.mTitleItem = new SliceItem((Object) title, "text", (String) null, new String[]{"title"});
            if (isLoading) {
                this.mTitleItem.addHint("partial");
            }
        }

        @NonNull
        public void setSubtitle(CharSequence subtitle) {
            setSubtitle(subtitle, false);
        }

        public void setSubtitle(CharSequence subtitle, boolean isLoading) {
            this.mSubtitleItem = new SliceItem((Object) subtitle, "text", (String) null, new String[0]);
            if (isLoading) {
                this.mSubtitleItem.addHint("partial");
            }
        }

        @NonNull
        public void addEndItem(long timeStamp) {
            this.mEndItems.add(new Slice.Builder(getBuilder()).addTimestamp(timeStamp, (String) null, new String[0]).build());
        }

        @NonNull
        public void addEndItem(IconCompat icon, int imageMode) {
            addEndItem(icon, imageMode, false);
        }

        @NonNull
        public void addEndItem(IconCompat icon, int imageMode, boolean isLoading) {
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
            Slice.Builder sb = new Slice.Builder(getBuilder()).addIcon(icon, (String) null, (List<String>) hints);
            if (isLoading) {
                sb.addHints("partial");
            }
            this.mEndItems.add(sb.build());
        }

        @NonNull
        public void addEndItem(@NonNull SliceAction action) {
            addEndItem(action, false);
        }

        public void addEndItem(@NonNull SliceAction action, boolean isLoading) {
            Slice.Builder sb = new Slice.Builder(getBuilder());
            if (isLoading) {
                sb.addHints("partial");
            }
            this.mEndItems.add(action.buildSlice(sb));
        }

        public void setContentDescription(CharSequence description) {
            this.mContentDescr = description;
        }

        public void setLayoutDirection(int layoutDirection) {
            getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
        }

        public void apply(Slice.Builder b) {
            if (this.mStartItem != null) {
                b.addSubSlice(this.mStartItem);
            }
            if (this.mTitleItem != null) {
                b.addItem(this.mTitleItem);
            }
            if (this.mSubtitleItem != null) {
                b.addItem(this.mSubtitleItem);
            }
            for (int i = 0; i < this.mEndItems.size(); i++) {
                b.addSubSlice(this.mEndItems.get(i));
            }
            if (this.mContentDescr != null) {
                b.addText(this.mContentDescr, "content_description", new String[0]);
            }
            if (this.mPrimaryAction != null) {
                b.addSubSlice(this.mPrimaryAction.buildSlice(new Slice.Builder(getBuilder()).addHints("title", "shortcut")), (String) null);
            }
        }
    }

    public static class HeaderBuilderImpl extends TemplateBuilderImpl implements ListBuilder.HeaderBuilder {
        private CharSequence mContentDescr;
        private SliceAction mPrimaryAction;
        private SliceItem mSubtitleItem;
        private SliceItem mSummaryItem;
        private SliceItem mTitleItem;

        public HeaderBuilderImpl(@NonNull ListBuilderV1Impl parent) {
            super(parent.createChildBuilder(), (SliceSpec) null);
        }

        public HeaderBuilderImpl(@NonNull Uri uri) {
            super(new Slice.Builder(uri), (SliceSpec) null);
        }

        public void apply(Slice.Builder b) {
            if (this.mTitleItem != null) {
                b.addItem(this.mTitleItem);
            }
            if (this.mSubtitleItem != null) {
                b.addItem(this.mSubtitleItem);
            }
            if (this.mSummaryItem != null) {
                b.addItem(this.mSummaryItem);
            }
            if (this.mContentDescr != null) {
                b.addText(this.mContentDescr, "content_description", new String[0]);
            }
            if (this.mPrimaryAction != null) {
                b.addSubSlice(this.mPrimaryAction.buildSlice(new Slice.Builder(getBuilder()).addHints("title", "shortcut")), (String) null);
            }
        }

        public void setTitle(CharSequence title, boolean isLoading) {
            this.mTitleItem = new SliceItem((Object) title, "text", (String) null, new String[]{"title"});
            if (isLoading) {
                this.mTitleItem.addHint("partial");
            }
        }

        public void setSubtitle(CharSequence subtitle, boolean isLoading) {
            this.mSubtitleItem = new SliceItem((Object) subtitle, "text", (String) null, new String[0]);
            if (isLoading) {
                this.mSubtitleItem.addHint("partial");
            }
        }

        public void setSummary(CharSequence summarySubtitle, boolean isLoading) {
            this.mSummaryItem = new SliceItem((Object) summarySubtitle, "text", (String) null, new String[]{"summary"});
            if (isLoading) {
                this.mSummaryItem.addHint("partial");
            }
        }

        public void setPrimaryAction(SliceAction action) {
            this.mPrimaryAction = action;
        }

        public void setContentDescription(CharSequence description) {
            this.mContentDescr = description;
        }

        public void setLayoutDirection(int layoutDirection) {
            getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
        }
    }
}
