package com.android.settingslib.wifi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.wifi.WifiConfiguration;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.R;
import com.android.settingslib.TronUtils;
import com.android.settingslib.Utils;

public class AccessPointPreference extends Preference {
    private static final int[] FRICTION_ATTRS = {R.attr.wifi_friction};
    private static final int[] STATE_METERED = {R.attr.state_metered};
    private static final int[] STATE_SECURED = {R.attr.state_encrypted};
    private static final int[] WIFI_CONNECTION_STRENGTH = {R.string.accessibility_no_wifi, R.string.accessibility_wifi_one_bar, R.string.accessibility_wifi_two_bars, R.string.accessibility_wifi_three_bars, R.string.accessibility_wifi_signal_full};
    private AccessPoint mAccessPoint;
    private Drawable mBadge;
    private final UserBadgeCache mBadgeCache;
    private final int mBadgePadding;
    private CharSequence mContentDescription;
    private int mDefaultIconResId;
    private boolean mForSavedNetworks;
    private final StateListDrawable mFrictionSld;
    private final IconInjector mIconInjector;
    private int mLevel;
    private final Runnable mNotifyChanged;
    private boolean mShowDivider;
    private TextView mTitleView;
    private int mWifiSpeed;

    private static StateListDrawable getFrictionStateListDrawable(Context context) {
        TypedArray frictionSld;
        try {
            frictionSld = context.getTheme().obtainStyledAttributes(FRICTION_ATTRS);
        } catch (Resources.NotFoundException e) {
            frictionSld = null;
        }
        if (frictionSld != null) {
            return (StateListDrawable) frictionSld.getDrawable(0);
        }
        return null;
    }

    public AccessPointPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mForSavedNetworks = false;
        this.mWifiSpeed = 0;
        this.mNotifyChanged = new Runnable() {
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        this.mFrictionSld = null;
        this.mBadgePadding = 0;
        this.mBadgeCache = null;
        this.mIconInjector = new IconInjector(context);
    }

    public AccessPointPreference(AccessPoint accessPoint, Context context, UserBadgeCache cache, boolean forSavedNetworks) {
        this(accessPoint, context, cache, 0, forSavedNetworks);
        refresh();
    }

    public AccessPointPreference(AccessPoint accessPoint, Context context, UserBadgeCache cache, int iconResId, boolean forSavedNetworks) {
        this(accessPoint, context, cache, iconResId, forSavedNetworks, getFrictionStateListDrawable(context), -1, new IconInjector(context));
    }

    @VisibleForTesting
    AccessPointPreference(AccessPoint accessPoint, Context context, UserBadgeCache cache, int iconResId, boolean forSavedNetworks, StateListDrawable frictionSld, int level, IconInjector iconInjector) {
        super(context);
        this.mForSavedNetworks = false;
        this.mWifiSpeed = 0;
        this.mNotifyChanged = new Runnable() {
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        setLayoutResource(R.layout.preference_access_point);
        setWidgetLayoutResource(getWidgetLayoutResourceId());
        this.mBadgeCache = cache;
        this.mAccessPoint = accessPoint;
        this.mForSavedNetworks = forSavedNetworks;
        this.mAccessPoint.setTag(this);
        this.mLevel = level;
        this.mDefaultIconResId = iconResId;
        this.mFrictionSld = frictionSld;
        this.mIconInjector = iconInjector;
        this.mBadgePadding = context.getResources().getDimensionPixelSize(R.dimen.wifi_preference_badge_padding);
    }

    /* access modifiers changed from: protected */
    public int getWidgetLayoutResourceId() {
        return R.layout.access_point_friction_widget;
    }

    public AccessPoint getAccessPoint() {
        return this.mAccessPoint;
    }

    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        if (this.mAccessPoint != null) {
            Drawable drawable = getIcon();
            if (drawable != null) {
                drawable.setLevel(this.mLevel);
            }
            this.mTitleView = (TextView) view.findViewById(16908310);
            if (this.mTitleView != null) {
                this.mTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mBadge, (Drawable) null);
                this.mTitleView.setCompoundDrawablePadding(this.mBadgePadding);
            }
            view.itemView.setContentDescription(this.mContentDescription);
            bindFrictionImage((ImageView) view.findViewById(R.id.friction_icon));
            view.findViewById(R.id.two_target_divider).setVisibility(shouldShowDivider() ? 0 : 4);
        }
    }

    public boolean shouldShowDivider() {
        return this.mShowDivider;
    }

    public void setShowDivider(boolean showDivider) {
        this.mShowDivider = showDivider;
        notifyChanged();
    }

    /* access modifiers changed from: protected */
    public void updateIcon(int level, Context context) {
        if (level == -1) {
            safeSetDefaultIcon();
            return;
        }
        TronUtils.logWifiSettingsSpeed(context, this.mWifiSpeed);
        Drawable drawable = this.mIconInjector.getIcon(level);
        if (this.mForSavedNetworks || drawable == null) {
            safeSetDefaultIcon();
            return;
        }
        drawable.setTint(Utils.getColorAttr(context, 16843817));
        setIcon(drawable);
    }

    private void bindFrictionImage(ImageView frictionImageView) {
        if (frictionImageView != null && this.mFrictionSld != null) {
            if (this.mAccessPoint.getSecurity() != 0) {
                this.mFrictionSld.setState(STATE_SECURED);
            } else if (this.mAccessPoint.isMetered()) {
                this.mFrictionSld.setState(STATE_METERED);
            }
            frictionImageView.setImageDrawable(this.mFrictionSld.getCurrent());
        }
    }

    private void safeSetDefaultIcon() {
        if (this.mDefaultIconResId != 0) {
            setIcon(this.mDefaultIconResId);
        } else {
            setIcon((Drawable) null);
        }
    }

    /* access modifiers changed from: protected */
    public void updateBadge(Context context) {
        WifiConfiguration config = this.mAccessPoint.getConfig();
        if (config != null) {
            this.mBadge = this.mBadgeCache.getUserBadge(config.creatorUid);
        }
    }

    public void refresh() {
        String str;
        setTitle(this, this.mAccessPoint, this.mForSavedNetworks);
        Context context = getContext();
        int level = this.mAccessPoint.getLevel();
        int wifiSpeed = this.mAccessPoint.getSpeed();
        if (!(level == this.mLevel && wifiSpeed == this.mWifiSpeed)) {
            this.mLevel = level;
            this.mWifiSpeed = wifiSpeed;
            updateIcon(this.mLevel, context);
            notifyChanged();
        }
        updateBadge(context);
        if (this.mForSavedNetworks) {
            str = this.mAccessPoint.getSavedNetworkSummary();
        } else {
            str = this.mAccessPoint.getSettingsSummary();
        }
        setSummary((CharSequence) str);
        this.mContentDescription = buildContentDescription(getContext(), this, this.mAccessPoint);
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            postNotifyChanged();
        } else {
            super.notifyChanged();
        }
    }

    @VisibleForTesting
    static void setTitle(AccessPointPreference preference, AccessPoint ap, boolean savedNetworks) {
        if (savedNetworks) {
            preference.setTitle((CharSequence) ap.getConfigName());
        } else {
            preference.setTitle((CharSequence) ap.getSsidStr());
        }
    }

    @VisibleForTesting
    static CharSequence buildContentDescription(Context context, Preference pref, AccessPoint ap) {
        String str;
        CharSequence contentDescription = pref.getTitle();
        CharSequence summary = pref.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            contentDescription = TextUtils.concat(new CharSequence[]{contentDescription, ",", summary});
        }
        int level = ap.getLevel();
        if (level >= 0 && level < WIFI_CONNECTION_STRENGTH.length) {
            contentDescription = TextUtils.concat(new CharSequence[]{contentDescription, ",", context.getString(WIFI_CONNECTION_STRENGTH[level])});
        }
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = contentDescription;
        charSequenceArr[1] = ",";
        if (ap.getSecurity() == 0) {
            str = context.getString(R.string.accessibility_wifi_security_type_none);
        } else {
            str = context.getString(R.string.accessibility_wifi_security_type_secured);
        }
        charSequenceArr[2] = str;
        return TextUtils.concat(charSequenceArr);
    }

    public void onLevelChanged() {
        postNotifyChanged();
    }

    private void postNotifyChanged() {
        if (this.mTitleView != null) {
            this.mTitleView.post(this.mNotifyChanged);
        }
    }

    public static class UserBadgeCache {
        private final SparseArray<Drawable> mBadges = new SparseArray<>();
        private final PackageManager mPm;

        public UserBadgeCache(PackageManager pm) {
            this.mPm = pm;
        }

        /* access modifiers changed from: private */
        public Drawable getUserBadge(int userId) {
            int index = this.mBadges.indexOfKey(userId);
            if (index >= 0) {
                return this.mBadges.valueAt(index);
            }
            Drawable badge = this.mPm.getUserBadgeForDensity(new UserHandle(userId), 0);
            this.mBadges.put(userId, badge);
            return badge;
        }
    }

    static class IconInjector {
        private final Context mContext;

        public IconInjector(Context context) {
            this.mContext = context;
        }

        public Drawable getIcon(int level) {
            return this.mContext.getDrawable(Utils.getWifiIconResource(level));
        }
    }
}
