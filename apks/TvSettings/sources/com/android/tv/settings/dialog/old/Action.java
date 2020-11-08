package com.android.tv.settings.dialog.old;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;

public class Action implements Parcelable {
    public static Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        public Action createFromParcel(Parcel source) {
            boolean z = false;
            Builder checked = new Builder().key(source.readString()).title(source.readString()).description(source.readString()).intent((Intent) source.readParcelable(Intent.class.getClassLoader())).resourcePackageName(source.readString()).drawableResource(source.readInt()).checked(source.readInt() != 0);
            if (source.readInt() != 0) {
                z = true;
            }
            return checked.multilineDescription(z).checkSetId(source.readInt()).build();
        }

        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
    public static final int DEFAULT_CHECK_SET_ID = 1;
    public static final int NO_CHECK_SET = 0;
    public static final int NO_DRAWABLE = 0;
    private static final String TAG = "Action";
    private final int mCheckSetId;
    private boolean mChecked;
    private final String mDescription;
    private final int mDrawableResource;
    private boolean mEnabled;
    private final boolean mHasNext;
    private final boolean mInfoOnly;
    private final Intent mIntent;
    private final String mKey;
    private final boolean mMultilineDescription;
    private final String mResourcePackageName;
    private final String mTitle;

    public static class Builder {
        private int mCheckSetId = 0;
        private boolean mChecked;
        private String mDescription;
        private int mDrawableResource = 0;
        private boolean mEnabled = true;
        private boolean mHasNext;
        private boolean mInfoOnly;
        private Intent mIntent;
        private String mKey;
        private boolean mMultilineDescription;
        private String mResourcePackageName;
        private String mTitle;

        public Action build() {
            return new Action(this.mKey, this.mTitle, this.mDescription, this.mResourcePackageName, this.mDrawableResource, this.mChecked, this.mMultilineDescription, this.mHasNext, this.mInfoOnly, this.mIntent, this.mCheckSetId, this.mEnabled);
        }

        public Builder key(String key) {
            this.mKey = key;
            return this;
        }

        public Builder title(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder description(String description) {
            this.mDescription = description;
            return this;
        }

        public Builder intent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder resourcePackageName(String resourcePackageName) {
            this.mResourcePackageName = resourcePackageName;
            return this;
        }

        public Builder drawableResource(int drawableResource) {
            this.mDrawableResource = drawableResource;
            return this;
        }

        public Builder checked(boolean checked) {
            this.mChecked = checked;
            return this;
        }

        public Builder multilineDescription(boolean multilineDescription) {
            this.mMultilineDescription = multilineDescription;
            return this;
        }

        public Builder hasNext(boolean hasNext) {
            this.mHasNext = hasNext;
            return this;
        }

        public Builder infoOnly(boolean infoOnly) {
            this.mInfoOnly = infoOnly;
            return this;
        }

        public Builder checkSetId(int checkSetId) {
            this.mCheckSetId = checkSetId;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }
    }

    protected Action(String key, String title, String description, String resourcePackageName, int drawableResource, boolean checked, boolean multilineDescription, boolean hasNext, boolean infoOnly, Intent intent, int checkSetId, boolean enabled) {
        this.mKey = key;
        this.mTitle = title;
        this.mDescription = description;
        this.mResourcePackageName = resourcePackageName;
        this.mDrawableResource = drawableResource;
        this.mChecked = checked;
        this.mMultilineDescription = multilineDescription;
        this.mHasNext = hasNext;
        this.mInfoOnly = infoOnly;
        this.mIntent = intent;
        this.mCheckSetId = checkSetId;
        this.mEnabled = enabled;
    }

    public static ArrayList<Action> createActionsFromArrays(String[] keys, String[] titles) {
        return createActionsFromArrays(keys, titles, 0, (String) null);
    }

    public static ArrayList<Action> createActionsFromArrays(String[] keys, String[] titles, int checkSetId, String checkedItemKey) {
        int keysLength = keys.length;
        if (keysLength == titles.length) {
            ArrayList<Action> actions = new ArrayList<>();
            for (int i = 0; i < keysLength; i++) {
                Builder builder = new Builder();
                builder.key(keys[i]).title(titles[i]).checkSetId(checkSetId);
                if (checkedItemKey != null) {
                    if (checkedItemKey.equals(keys[i])) {
                        builder.checked(true);
                    } else {
                        builder.checked(false);
                    }
                }
                actions.add(builder.build());
            }
            return actions;
        }
        throw new IllegalArgumentException("Keys and titles dimensions must match");
    }

    public String getKey() {
        return this.mKey;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public Uri getIconUri() {
        return null;
    }

    public int getCheckSetId() {
        return this.mCheckSetId;
    }

    public boolean hasMultilineDescription() {
        return this.mMultilineDescription;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean hasNext() {
        return this.mHasNext;
    }

    public boolean infoOnly() {
        return this.mInfoOnly;
    }

    public Drawable getIndicator(Context context) {
        if (this.mDrawableResource == 0) {
            return null;
        }
        if (this.mResourcePackageName == null) {
            return context.getDrawable(this.mDrawableResource);
        }
        try {
            return context.createPackageContext(this.mResourcePackageName, 0).getDrawable(this.mDrawableResource);
        } catch (PackageManager.NameNotFoundException e) {
            if (!Log.isLoggable(TAG, 5)) {
                return null;
            }
            Log.w(TAG, "No icon for this action.");
            return null;
        } catch (Resources.NotFoundException e2) {
            if (!Log.isLoggable(TAG, 5)) {
                return null;
            }
            Log.w(TAG, "No icon for this action.");
            return null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mKey);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);
        dest.writeParcelable(this.mIntent, flags);
        dest.writeString(this.mResourcePackageName);
        dest.writeInt(this.mDrawableResource);
        dest.writeInt(this.mChecked ? 1 : 0);
        dest.writeInt(this.mMultilineDescription ? 1 : 0);
        dest.writeInt(this.mCheckSetId);
    }
}
