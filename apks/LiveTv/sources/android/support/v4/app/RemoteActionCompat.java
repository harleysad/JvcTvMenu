package android.support.v4.app;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.util.Preconditions;

public final class RemoteActionCompat {
    private static final String EXTRA_ACTION_INTENT = "action";
    private static final String EXTRA_CONTENT_DESCRIPTION = "desc";
    private static final String EXTRA_ENABLED = "enabled";
    private static final String EXTRA_ICON = "icon";
    private static final String EXTRA_SHOULD_SHOW_ICON = "showicon";
    private static final String EXTRA_TITLE = "title";
    private final PendingIntent mActionIntent;
    private final CharSequence mContentDescription;
    private boolean mEnabled;
    private final IconCompat mIcon;
    private boolean mShouldShowIcon;
    private final CharSequence mTitle;

    public RemoteActionCompat(@NonNull IconCompat icon, @NonNull CharSequence title, @NonNull CharSequence contentDescription, @NonNull PendingIntent intent) {
        if (icon == null || title == null || contentDescription == null || intent == null) {
            throw new IllegalArgumentException("Expected icon, title, content description and action callback");
        }
        this.mIcon = icon;
        this.mTitle = title;
        this.mContentDescription = contentDescription;
        this.mActionIntent = intent;
        this.mEnabled = true;
        this.mShouldShowIcon = true;
    }

    public RemoteActionCompat(@NonNull RemoteActionCompat other) {
        Preconditions.checkNotNull(other);
        this.mIcon = other.mIcon;
        this.mTitle = other.mTitle;
        this.mContentDescription = other.mContentDescription;
        this.mActionIntent = other.mActionIntent;
        this.mEnabled = other.mEnabled;
        this.mShouldShowIcon = other.mShouldShowIcon;
    }

    @RequiresApi(26)
    @NonNull
    public static RemoteActionCompat createFromRemoteAction(@NonNull RemoteAction remoteAction) {
        Preconditions.checkNotNull(remoteAction);
        RemoteActionCompat action = new RemoteActionCompat(IconCompat.createFromIcon(remoteAction.getIcon()), remoteAction.getTitle(), remoteAction.getContentDescription(), remoteAction.getActionIntent());
        action.setEnabled(remoteAction.isEnabled());
        if (Build.VERSION.SDK_INT >= 28) {
            action.setShouldShowIcon(remoteAction.shouldShowIcon());
        }
        return action;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setShouldShowIcon(boolean shouldShowIcon) {
        this.mShouldShowIcon = shouldShowIcon;
    }

    public boolean shouldShowIcon() {
        return this.mShouldShowIcon;
    }

    @NonNull
    public IconCompat getIcon() {
        return this.mIcon;
    }

    @NonNull
    public CharSequence getTitle() {
        return this.mTitle;
    }

    @NonNull
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @NonNull
    public PendingIntent getActionIntent() {
        return this.mActionIntent;
    }

    @RequiresApi(26)
    @NonNull
    public RemoteAction toRemoteAction() {
        RemoteAction action = new RemoteAction(this.mIcon.toIcon(), this.mTitle, this.mContentDescription, this.mActionIntent);
        action.setEnabled(isEnabled());
        if (Build.VERSION.SDK_INT >= 28) {
            action.setShouldShowIcon(shouldShowIcon());
        }
        return action;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle(EXTRA_ICON, this.mIcon.toBundle());
        bundle.putCharSequence("title", this.mTitle);
        bundle.putCharSequence(EXTRA_CONTENT_DESCRIPTION, this.mContentDescription);
        bundle.putParcelable("action", this.mActionIntent);
        bundle.putBoolean(EXTRA_ENABLED, this.mEnabled);
        bundle.putBoolean(EXTRA_SHOULD_SHOW_ICON, this.mShouldShowIcon);
        return bundle;
    }

    @Nullable
    public static RemoteActionCompat createFromBundle(@NonNull Bundle bundle) {
        RemoteActionCompat action = new RemoteActionCompat(IconCompat.createFromBundle(bundle.getBundle(EXTRA_ICON)), bundle.getCharSequence("title"), bundle.getCharSequence(EXTRA_CONTENT_DESCRIPTION), (PendingIntent) bundle.getParcelable("action"));
        action.setEnabled(bundle.getBoolean(EXTRA_ENABLED));
        action.setShouldShowIcon(bundle.getBoolean(EXTRA_SHOULD_SHOW_ICON));
        return action;
    }
}
