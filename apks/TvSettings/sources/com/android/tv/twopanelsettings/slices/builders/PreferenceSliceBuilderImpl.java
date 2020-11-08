package com.android.tv.twopanelsettings.slices.builders;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.util.Pair;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;
import androidx.slice.builders.SliceAction;
import androidx.slice.core.SliceHints;
import com.android.tv.twopanelsettings.slices.builders.PreferenceSliceBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PreferenceSliceBuilderImpl extends TemplateBuilderImpl {
    public static final String SUBTYPE_BUTTON_STYLE = "SUBTYPE_BUTTON_STYLE";
    public static final String SUBTYPE_FOLLOWUP_INTENT = "SUBTYPE_FOLLOWUP_INTENT";
    public static final String SUBTYPE_ICON_NEED_TO_BE_PROCESSED = "SUBTYPE_ICON_NEED_TO_BE_PROCESSED";
    public static final String SUBTYPE_INFO_PREFERENCE = "SUBTYPE_INFO_PREFERENCE";
    public static final String SUBTYPE_INTENT = "SUBTYPE_INTENT";
    public static final String SUBTYPE_IS_ENABLED = "SUBTYPE_IS_ENABLED";
    public static final String TAG_KEY = "TAG_KEY";
    public static final String TAG_TARGET_URI = "TAG_TARGET_URI";
    public static final String TYPE_PREFERENCE = "TYPE_PREFERENCE";
    public static final String TYPE_PREFERENCE_CATEGORY = "TYPE_PREFERENCE_CATEGORY";
    public static final String TYPE_PREFERENCE_EMBEDDED = "TYPE_PREFERENCE_EMBEDDED";
    public static final String TYPE_PREFERENCE_RADIO = "TYPE_PREFERENCE_RADIO";
    public static final String TYPE_PREFERENCE_SCREEN_TITLE = "TYPE_PREFERENCE_SCREEN_TITLE";
    private List<Slice> mSliceActions;

    public PreferenceSliceBuilderImpl(Slice.Builder b, SliceSpec spec) {
        this(b, spec, new SystemClock());
    }

    public PreferenceSliceBuilderImpl(Slice.Builder b, SliceSpec spec, Clock clock) {
        super(b, spec, clock);
    }

    public void apply(Slice.Builder builder) {
        builder.addLong(System.currentTimeMillis(), SliceHints.SUBTYPE_MILLIS, SliceHints.HINT_LAST_UPDATED);
        if (this.mSliceActions != null) {
            Slice.Builder sb = new Slice.Builder(builder);
            for (int i = 0; i < this.mSliceActions.size(); i++) {
                sb.addSubSlice(this.mSliceActions.get(i));
            }
            builder.addSubSlice(sb.addHints("actions").build());
        }
    }

    @NonNull
    public void addPreference(@NonNull PreferenceSliceBuilder.RowBuilder builder) {
        addRow(builder, "TYPE_PREFERENCE");
    }

    @NonNull
    public void addPreferenceCategory(@NonNull PreferenceSliceBuilder.RowBuilder builder) {
        addRow(builder, "TYPE_PREFERENCE_CATEGORY");
    }

    @NonNull
    public void addScreenTitle(@NonNull PreferenceSliceBuilder.RowBuilder builder) {
        addRow(builder, "TYPE_PREFERENCE_SCREEN_TITLE");
    }

    public void setEmbeddedPreference(@NonNull PreferenceSliceBuilder.RowBuilder builder) {
        addRow(builder, "TYPE_PREFERENCE_EMBEDDED");
    }

    @NonNull
    private void addRow(@NonNull PreferenceSliceBuilder.RowBuilder builder, String type) {
        RowBuilderImpl impl = new RowBuilderImpl(createChildBuilder());
        impl.fillFrom(builder);
        impl.getBuilder().addHints("list_item");
        getBuilder().addSubSlice(impl.build(), type);
    }

    private void addAction(@NonNull SliceAction action) {
        if (this.mSliceActions == null) {
            this.mSliceActions = new ArrayList();
        }
        this.mSliceActions.add(action.buildSlice(new Slice.Builder(getBuilder()).addHints("actions")));
    }

    private void setKeywords(@NonNull List<String> keywords) {
        Slice.Builder sb = new Slice.Builder(getBuilder());
        for (int i = 0; i < keywords.size(); i++) {
            sb.addText((CharSequence) keywords.get(i), (String) null, new String[0]);
        }
        getBuilder().addSubSlice(sb.addHints(SliceHints.HINT_KEYWORDS).build());
    }

    public void setNotReady() {
        getBuilder().addHints("partial");
    }

    public void setTtl(long ttl) {
        long expiry = -1;
        if (ttl != -1) {
            expiry = System.currentTimeMillis() + ttl;
        }
        getBuilder().addTimestamp(expiry, SliceHints.SUBTYPE_MILLIS, SliceHints.HINT_TTL);
    }

    @RequiresApi(26)
    public void setTtl(@Nullable Duration ttl) {
        setTtl(ttl == null ? -1 : ttl.toMillis());
    }

    public static class RowBuilderImpl extends TemplateBuilderImpl {
        private SliceItem mButtonStyleItem;
        private CharSequence mContentDescr;
        private ArrayList<Slice> mEndItems = new ArrayList<>();
        private SliceAction mFollowupAction;
        private SliceItem mIconNeedsToBeProcessedItem;
        private ArrayList<Slice> mInfoItems = new ArrayList<>();
        private SliceItem mIsEnabledItem;
        private SliceItem mKeyItem;
        private SliceAction mPrimaryAction;
        private Slice mStartItem;
        private SliceItem mSubtitleItem;
        private SliceItem mTitleItem;
        private SliceItem mUriItem;

        public RowBuilderImpl(@NonNull PreferenceSliceBuilderImpl parent) {
            super(parent.createChildBuilder(), (SliceSpec) null);
        }

        public RowBuilderImpl(@NonNull Uri uri) {
            super(new Slice.Builder(uri), (SliceSpec) null);
        }

        public RowBuilderImpl(Slice.Builder builder) {
            super(builder, (SliceSpec) null);
        }

        private void setLayoutDirection(int layoutDirection) {
            getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
        }

        /* access modifiers changed from: package-private */
        public void fillFrom(PreferenceSliceBuilder.RowBuilder builder) {
            if (builder.getUri() != null) {
                setBuilder(new Slice.Builder(builder.getUri()));
            }
            if (builder.getPrimaryAction() != null) {
                setPrimaryAction(builder.getPrimaryAction());
            }
            if (builder.getFollowupAction() != null) {
                setFollowupAction(builder.getFollowupAction());
            }
            if (builder.getLayoutDirection() != -1) {
                setLayoutDirection(builder.getLayoutDirection());
            }
            if (builder.getTitleAction() != null || builder.isTitleActionLoading()) {
                setTitleItem(builder.getTitleAction(), builder.isTitleActionLoading());
            } else if (builder.getTitleIcon() != null || builder.isTitleItemLoading()) {
                setTitleItem(builder.getTitleIcon(), 0, builder.isTitleItemLoading());
            }
            if (builder.getTitle() != null || builder.isTitleLoading()) {
                setTitle(builder.getTitle(), builder.isTitleLoading());
            }
            if (builder.getSubtitle() != null || builder.isSubtitleLoading()) {
                setSubtitle(builder.getSubtitle(), builder.isSubtitleLoading());
            }
            if (builder.getContentDescription() != null) {
                setContentDescription(builder.getContentDescription());
            }
            if (builder.getTargetSliceUri() != null) {
                setTargetSliceUri(builder.getTargetSliceUri());
            }
            if (builder.getKey() != null) {
                setKey(builder.getKey());
            }
            if (builder.getTitleIcon() != null) {
                setIconNeedsToBeProcessed(builder.iconNeedsToBeProcessed());
            }
            setButtonStyle(builder.getButtonStyle());
            setEnabled(builder.isEnabled());
            List<Object> endItems = builder.getEndItems();
            List<Integer> endTypes = builder.getEndTypes();
            List<Boolean> endLoads = builder.getEndLoads();
            for (int i = 0; i < endItems.size(); i++) {
                switch (endTypes.get(i).intValue()) {
                    case 1:
                        Pair<IconCompat, Integer> pair = (Pair) endItems.get(i);
                        addEndItem((IconCompat) pair.first, ((Integer) pair.second).intValue(), endLoads.get(i).booleanValue());
                        break;
                    case 2:
                        addEndItem((SliceAction) endItems.get(i), endLoads.get(i).booleanValue());
                        break;
                }
            }
            List<Pair<String, String>> infoItems = builder.getInfoItems();
            for (int i2 = 0; i2 < infoItems.size(); i2++) {
                addInfoItem((String) infoItems.get(i2).first, (String) infoItems.get(i2).second);
            }
        }

        @NonNull
        private void setTitleItem(IconCompat icon, int imageMode) {
            setTitleItem(icon, imageMode, false);
        }

        private void addInfoItem(String title, String summary) {
            this.mInfoItems.add(new Slice.Builder(getBuilder()).addText((CharSequence) title, (String) null, "title").addText((CharSequence) summary, (String) null, "summary").build());
        }

        @NonNull
        private void setTitleItem(IconCompat icon, int imageMode, boolean isLoading) {
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
        private void setTitleItem(@NonNull SliceAction action) {
            setTitleItem(action, false);
        }

        private void setTitleItem(SliceAction action, boolean isLoading) {
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

        public void setFollowupAction(@NonNull SliceAction action) {
            this.mFollowupAction = action;
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

        public void setTargetSliceUri(CharSequence uri) {
            this.mUriItem = new SliceItem((Object) uri, "text", "TAG_TARGET_URI", new String[]{"actions"});
        }

        public void setIconNeedsToBeProcessed(boolean needed) {
            this.mIconNeedsToBeProcessedItem = new SliceItem((Object) Integer.valueOf(needed), "int", "SUBTYPE_ICON_NEED_TO_BE_PROCESSED", new String[0]);
        }

        public void setButtonStyle(int buttonStyle) {
            this.mButtonStyleItem = new SliceItem((Object) Integer.valueOf(buttonStyle), "int", "SUBTYPE_BUTTON_STYLE", new String[0]);
        }

        public void setEnabled(boolean enabled) {
            this.mIsEnabledItem = new SliceItem((Object) Integer.valueOf(enabled), "int", "SUBTYPE_IS_ENABLED", new String[0]);
        }

        public void setKey(CharSequence key) {
            this.mKeyItem = new SliceItem((Object) key, "text", "TAG_KEY", new String[]{SliceHints.HINT_KEYWORDS});
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
            if (this.mUriItem != null) {
                b.addItem(this.mUriItem);
            }
            if (this.mKeyItem != null) {
                b.addItem(this.mKeyItem);
            }
            if (this.mIconNeedsToBeProcessedItem != null) {
                b.addItem(this.mIconNeedsToBeProcessedItem);
            }
            if (this.mButtonStyleItem != null) {
                b.addItem(this.mButtonStyleItem);
            }
            if (this.mIsEnabledItem != null) {
                b.addItem(this.mIsEnabledItem);
            }
            for (int i = 0; i < this.mEndItems.size(); i++) {
                b.addSubSlice(this.mEndItems.get(i));
            }
            for (int i2 = 0; i2 < this.mInfoItems.size(); i2++) {
                b.addSubSlice(this.mInfoItems.get(i2), "SUBTYPE_INFO_PREFERENCE");
            }
            if (this.mContentDescr != null) {
                b.addText(this.mContentDescr, "content_description", new String[0]);
            }
            if (this.mPrimaryAction != null) {
                b.addSubSlice(this.mPrimaryAction.buildSlice(new Slice.Builder(getBuilder()).addHints("title", "shortcut")), "SUBTYPE_INTENT");
            }
            if (this.mFollowupAction != null) {
                b.addSubSlice(this.mFollowupAction.buildSlice(new Slice.Builder(getBuilder()).addHints("title", "shortcut")), "SUBTYPE_FOLLOWUP_INTENT");
            }
        }
    }
}
