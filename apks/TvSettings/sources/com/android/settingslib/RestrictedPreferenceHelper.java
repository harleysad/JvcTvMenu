package com.android.settingslib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;

public class RestrictedPreferenceHelper {
    private String mAttrUserRestriction = null;
    private final Context mContext;
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private final Preference mPreference;
    private boolean mUseAdminDisabledSummary;

    public RestrictedPreferenceHelper(Context context, Preference preference, AttributeSet attrs) {
        boolean z = false;
        this.mUseAdminDisabledSummary = false;
        this.mContext = context;
        this.mPreference = preference;
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RestrictedPreference);
            TypedValue userRestriction = attributes.peekValue(R.styleable.RestrictedPreference_userRestriction);
            CharSequence data = null;
            if (userRestriction != null && userRestriction.type == 3) {
                data = userRestriction.resourceId != 0 ? context.getText(userRestriction.resourceId) : userRestriction.string;
            }
            this.mAttrUserRestriction = data == null ? null : data.toString();
            if (RestrictedLockUtils.hasBaseUserRestriction(this.mContext, this.mAttrUserRestriction, UserHandle.myUserId())) {
                this.mAttrUserRestriction = null;
                return;
            }
            TypedValue useAdminDisabledSummary = attributes.peekValue(R.styleable.RestrictedPreference_useAdminDisabledSummary);
            if (useAdminDisabledSummary != null) {
                if (useAdminDisabledSummary.type == 18 && useAdminDisabledSummary.data != 0) {
                    z = true;
                }
                this.mUseAdminDisabledSummary = z;
            }
        }
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        TextView summaryView;
        if (this.mDisabledByAdmin) {
            holder.itemView.setEnabled(true);
        }
        if (this.mUseAdminDisabledSummary && (summaryView = (TextView) holder.findViewById(16908304)) != null) {
            CharSequence disabledText = summaryView.getContext().getText(R.string.disabled_by_admin_summary_text);
            if (this.mDisabledByAdmin) {
                summaryView.setText(disabledText);
            } else if (TextUtils.equals(disabledText, summaryView.getText())) {
                summaryView.setText((CharSequence) null);
            }
        }
    }

    public void useAdminDisabledSummary(boolean useSummary) {
        this.mUseAdminDisabledSummary = useSummary;
    }

    public boolean performClick() {
        if (!this.mDisabledByAdmin) {
            return false;
        }
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
        return true;
    }

    public void onAttachedToHierarchy() {
        if (this.mAttrUserRestriction != null) {
            checkRestrictionAndSetDisabled(this.mAttrUserRestriction, UserHandle.myUserId());
        }
    }

    public void checkRestrictionAndSetDisabled(String userRestriction, int userId) {
        setDisabledByAdmin(RestrictedLockUtils.checkIfRestrictionEnforced(this.mContext, userRestriction, userId));
    }

    public boolean setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin admin) {
        boolean z = false;
        boolean disabled = admin != null;
        this.mEnforcedAdmin = admin;
        boolean changed = false;
        if (this.mDisabledByAdmin != disabled) {
            this.mDisabledByAdmin = disabled;
            changed = true;
        }
        Preference preference = this.mPreference;
        if (!disabled) {
            z = true;
        }
        preference.setEnabled(z);
        return changed;
    }

    public boolean isDisabledByAdmin() {
        return this.mDisabledByAdmin;
    }
}
