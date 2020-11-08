package com.android.tv.twopanelsettings.slices.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.util.Pair;
import androidx.slice.Slice;
import androidx.slice.SliceSpecs;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PreferenceSliceBuilder extends TemplateSliceBuilder {
    public static final long INFINITY = -1;
    private PreferenceSliceBuilderImpl mImpl;

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutDirection {
    }

    public PreferenceSliceBuilder(@NonNull Context context, @NonNull Uri uri) {
        super(context, uri);
    }

    public PreferenceSliceBuilder(@NonNull Context context, @NonNull Uri uri, long ttl) {
        super(context, uri);
        this.mImpl.setTtl(ttl);
    }

    public PreferenceSliceBuilder(@NonNull Context context, @NonNull Uri uri, @Nullable Duration ttl) {
        super(context, uri);
        this.mImpl.setTtl(ttl);
    }

    /* access modifiers changed from: protected */
    public TemplateBuilderImpl selectImpl(Uri uri) {
        return new PreferenceSliceBuilderImpl(getBuilder(), SliceSpecs.LIST, getClock());
    }

    @NonNull
    public Slice build() {
        return this.mImpl.build();
    }

    /* access modifiers changed from: package-private */
    public void setImpl(TemplateBuilderImpl impl) {
        this.mImpl = (PreferenceSliceBuilderImpl) impl;
    }

    public PreferenceSliceBuilder addPreference(RowBuilder builder) {
        this.mImpl.addPreference(builder);
        return this;
    }

    public PreferenceSliceBuilder addPreferenceCategory(RowBuilder builder) {
        this.mImpl.addPreferenceCategory(builder);
        return this;
    }

    public PreferenceSliceBuilder addScreenTitle(RowBuilder builder) {
        this.mImpl.addScreenTitle(builder);
        return this;
    }

    public PreferenceSliceBuilder setEmbeddedPreference(RowBuilder builder) {
        this.mImpl.setEmbeddedPreference(builder);
        return this;
    }

    public PreferenceSliceBuilder setNotReady() {
        this.mImpl.setNotReady();
        return this;
    }

    public static class RowBuilder {
        public static final int TYPE_ACTION = 2;
        public static final int TYPE_ICON = 1;
        private int mButtonStyle;
        private CharSequence mContentDescription;
        private boolean mEnabled;
        private List<Object> mEndItems;
        private List<Boolean> mEndLoads;
        private List<Integer> mEndTypes;
        private SliceAction mFollowupAction;
        private boolean mHasDefaultToggle;
        private boolean mHasEndActionOrToggle;
        private boolean mHasEndImage;
        private boolean mIconNeedsToBeProcessed;
        private List<Pair<String, String>> mInfoItems;
        private CharSequence mKey;
        private int mLayoutDirection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private CharSequence mTargetSliceUri;
        private CharSequence mTitle;
        private SliceAction mTitleAction;
        private boolean mTitleActionLoading;
        private IconCompat mTitleIcon;
        private boolean mTitleItemLoading;
        private boolean mTitleLoading;
        private final Uri mUri;

        public RowBuilder() {
            this.mLayoutDirection = -1;
            this.mEndItems = new ArrayList();
            this.mEndTypes = new ArrayList();
            this.mEndLoads = new ArrayList();
            this.mInfoItems = new ArrayList();
            this.mEnabled = true;
            this.mUri = null;
        }

        public RowBuilder(Uri uri) {
            this.mLayoutDirection = -1;
            this.mEndItems = new ArrayList();
            this.mEndTypes = new ArrayList();
            this.mEndLoads = new ArrayList();
            this.mInfoItems = new ArrayList();
            this.mEnabled = true;
            this.mUri = uri;
        }

        public RowBuilder(@NonNull ListBuilder parent) {
            this();
        }

        public RowBuilder(@NonNull ListBuilder parent, @NonNull Uri uri) {
            this(uri);
        }

        public RowBuilder(@NonNull Context context, @NonNull Uri uri) {
            this(uri);
        }

        private RowBuilder setTitleItem(@NonNull IconCompat icon) {
            return setTitleItem(icon, false);
        }

        @NonNull
        private RowBuilder setTitleItem(@Nullable IconCompat icon, boolean isLoading) {
            this.mTitleAction = null;
            this.mTitleIcon = icon;
            this.mTitleItemLoading = isLoading;
            return this;
        }

        @NonNull
        private RowBuilder setTitleItem(@NonNull SliceAction action) {
            return setTitleItem(action, false);
        }

        @NonNull
        private RowBuilder setTitleItem(@NonNull SliceAction action, boolean isLoading) {
            this.mTitleAction = action;
            this.mTitleIcon = null;
            this.mTitleActionLoading = isLoading;
            return this;
        }

        @NonNull
        public RowBuilder setIcon(@NonNull IconCompat icon) {
            return setTitleItem(icon);
        }

        @NonNull
        private RowBuilder setPrimaryAction(@NonNull SliceAction action) {
            this.mPrimaryAction = action;
            return this;
        }

        @NonNull
        public RowBuilder setPendingIntent(@NonNull PendingIntent pendingIntent) {
            return setPrimaryAction(new SliceAction(pendingIntent, (CharSequence) "", false));
        }

        @NonNull
        public RowBuilder setFollowupPendingIntent(@NonNull PendingIntent pendingIntent) {
            this.mFollowupAction = new SliceAction(pendingIntent, (CharSequence) "", false);
            return this;
        }

        @NonNull
        public RowBuilder setTitle(@NonNull CharSequence title) {
            return setTitle(title, false);
        }

        @NonNull
        public RowBuilder setTitle(@Nullable CharSequence title, boolean isLoading) {
            this.mTitle = title;
            this.mTitleLoading = isLoading;
            return this;
        }

        @NonNull
        public RowBuilder setSubtitle(@NonNull CharSequence subtitle) {
            return setSubtitle(subtitle, false);
        }

        @NonNull
        public RowBuilder setSubtitle(@Nullable CharSequence subtitle, boolean isLoading) {
            this.mSubtitle = subtitle;
            this.mSubtitleLoading = isLoading;
            return this;
        }

        @NonNull
        private RowBuilder addEndItem(@NonNull IconCompat icon) {
            return addEndItem(icon, false);
        }

        @NonNull
        private RowBuilder addEndItem(@Nullable IconCompat icon, boolean isLoading) {
            if (!this.mHasEndActionOrToggle) {
                this.mEndItems.add(new Pair(icon, 0));
                this.mEndTypes.add(1);
                this.mEndLoads.add(Boolean.valueOf(isLoading));
                this.mHasEndImage = true;
                return this;
            }
            throw new IllegalArgumentException("Trying to add an icon to end items when anaction has already been added. End items cannot have a mixture of actions and icons.");
        }

        @NonNull
        private RowBuilder addEndItem(@NonNull SliceAction action) {
            return addEndItem(action, false);
        }

        public RowBuilder addInfoItem(String title, String summary) {
            this.mInfoItems.add(new Pair(title, summary));
            return this;
        }

        public RowBuilder addRadioButton(PendingIntent pendingIntent, boolean isChecked) {
            return addButton(pendingIntent, isChecked, 2);
        }

        public RowBuilder addCheckMark(PendingIntent pendingIntent, boolean isChecked) {
            return addButton(pendingIntent, isChecked, 1);
        }

        public RowBuilder addSwitch(PendingIntent pendingIntent, boolean isChecked) {
            return addButton(pendingIntent, isChecked, 0);
        }

        private RowBuilder addButton(PendingIntent pendingIntent, boolean isChecked, int style) {
            SliceAction switchAction = new SliceAction(pendingIntent, (CharSequence) "", isChecked);
            this.mButtonStyle = style;
            return addEndItem(switchAction);
        }

        @NonNull
        public RowBuilder addSwitch(PendingIntent pendingIntent, @NonNull CharSequence actionTitle, boolean isChecked) {
            SliceAction switchAction = new SliceAction(pendingIntent, actionTitle, isChecked);
            this.mButtonStyle = 0;
            return addEndItem(switchAction);
        }

        @NonNull
        private RowBuilder addEndItem(@NonNull SliceAction action, boolean isLoading) {
            if (this.mHasEndImage) {
                throw new IllegalArgumentException("Trying to add an action to end items when anicon has already been added. End items cannot have a mixture of actions and icons.");
            } else if (!this.mHasDefaultToggle) {
                this.mEndItems.add(action);
                this.mEndTypes.add(2);
                this.mEndLoads.add(Boolean.valueOf(isLoading));
                this.mHasDefaultToggle = action.getImpl().isDefaultToggle();
                this.mHasEndActionOrToggle = true;
                return this;
            } else {
                throw new IllegalStateException("Only one non-custom toggle can be added in a single row. If you would like to include multiple toggles in a row, set a custom icon for each toggle.");
            }
        }

        @NonNull
        public RowBuilder setContentDescription(@NonNull CharSequence description) {
            this.mContentDescription = description;
            return this;
        }

        public RowBuilder setTargetSliceUri(@NonNull CharSequence targetSliceUri) {
            this.mTargetSliceUri = targetSliceUri;
            return this;
        }

        public RowBuilder setKey(@NonNull CharSequence key) {
            this.mKey = key;
            return this;
        }

        @NonNull
        public RowBuilder setLayoutDirection(int layoutDirection) {
            this.mLayoutDirection = layoutDirection;
            return this;
        }

        @Deprecated
        @NonNull
        public RowBuilder setCheckmark(boolean isCheckMark) {
            if (isCheckMark) {
                this.mButtonStyle = 1;
            } else {
                this.mButtonStyle = 0;
            }
            return this;
        }

        public RowBuilder setButtonStyle(int buttonStyle) {
            this.mButtonStyle = buttonStyle;
            return this;
        }

        @NonNull
        public RowBuilder setIconNeedsToBeProcessed(boolean needed) {
            this.mIconNeedsToBeProcessed = needed;
            return this;
        }

        @NonNull
        public RowBuilder setEnabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }

        public boolean iconNeedsToBeProcessed() {
            return this.mIconNeedsToBeProcessed;
        }

        public int getButtonStyle() {
            return this.mButtonStyle;
        }

        public CharSequence getTargetSliceUri() {
            return this.mTargetSliceUri;
        }

        public CharSequence getKey() {
            return this.mKey;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public boolean hasEndActionOrToggle() {
            return this.mHasEndActionOrToggle;
        }

        public boolean hasEndImage() {
            return this.mHasEndImage;
        }

        public boolean hasDefaultToggle() {
            return this.mHasDefaultToggle;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isTitleItemLoading() {
            return this.mTitleItemLoading;
        }

        public IconCompat getTitleIcon() {
            return this.mTitleIcon;
        }

        public SliceAction getTitleAction() {
            return this.mTitleAction;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public SliceAction getFollowupAction() {
            return this.mFollowupAction;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }

        public List<Object> getEndItems() {
            return this.mEndItems;
        }

        public List<Pair<String, String>> getInfoItems() {
            return this.mInfoItems;
        }

        public List<Integer> getEndTypes() {
            return this.mEndTypes;
        }

        public List<Boolean> getEndLoads() {
            return this.mEndLoads;
        }

        public boolean isTitleActionLoading() {
            return this.mTitleActionLoading;
        }
    }
}
