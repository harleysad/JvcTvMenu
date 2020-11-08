package com.android.tv.settings.users;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

public class RestrictedProfileModel {
    private static final String TAG = "RestrictedProfile";
    private final ActivityManager mActivityManager;
    private final boolean mApplyRestrictions;
    private final Context mContext;
    private final UserInfo mCurrentUserInfo;
    private final UserManager mUserManager;

    public RestrictedProfileModel(Context context) {
        this(context, true);
    }

    RestrictedProfileModel(Context context, boolean applyRestrictions) {
        this.mContext = context;
        this.mApplyRestrictions = applyRestrictions;
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mCurrentUserInfo = this.mUserManager.getUserInfo(this.mContext.getUserId());
    }

    public boolean enterUser() {
        if (isCurrentUser()) {
            Log.w(TAG, "Tried to switch into current user");
            return false;
        }
        UserInfo restrictedUser = getUser();
        if (restrictedUser == null) {
            Log.e(TAG, "Tried to enter non-existent restricted user");
            return false;
        }
        updateBackgroundRestriction(restrictedUser);
        switchUserNow(restrictedUser.id);
        return true;
    }

    public void exitUser() {
        if (this.mCurrentUserInfo.isRestricted()) {
            if (this.mCurrentUserInfo.restrictedProfileParentId == -10000) {
                switchUserNow(0);
            } else {
                switchUserNow(this.mCurrentUserInfo.restrictedProfileParentId);
            }
        }
    }

    public void removeUser() {
        UserInfo restrictedUser = getUser();
        if (restrictedUser == null) {
            Log.w(TAG, "No restricted user to remove?");
            return;
        }
        this.mUserManager.removeUser(restrictedUser.id);
    }

    public boolean isCurrentUser() {
        return this.mCurrentUserInfo.isRestricted();
    }

    public UserInfo getUser() {
        if (this.mCurrentUserInfo.isRestricted()) {
            return this.mCurrentUserInfo;
        }
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (userInfo.isRestricted()) {
                return userInfo;
            }
        }
        return null;
    }

    private void switchUserNow(int userId) {
        try {
            this.mActivityManager.switchUser(userId);
        } catch (RuntimeException e) {
            Log.e(TAG, "Caught exception while switching user! ", e);
        }
    }

    private void updateBackgroundRestriction(UserInfo user) {
        if (this.mApplyRestrictions) {
            this.mUserManager.setUserRestriction("no_run_in_background", !shouldAllowRunInBackground(), user.getUserHandle());
        }
    }

    private boolean shouldAllowRunInBackground() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "keep_profile_in_background", this.mContext.getResources().getBoolean(17956987)) > 0;
    }
}
