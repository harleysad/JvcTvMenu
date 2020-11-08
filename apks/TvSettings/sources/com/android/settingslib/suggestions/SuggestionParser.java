package com.android.settingslib.suggestions;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.drawer.TileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SuggestionParser {
    private static final String DEFAULT_SMART_DISMISS_CONTROL = "0";
    private static final String IS_DISMISSED = "_is_dismissed";
    private static final String META_DATA_ADMIN_USER_TYPE_VALUE = "admin";
    public static final String META_DATA_DISMISS_CONTROL = "com.android.settings.dismiss";
    private static final String META_DATA_GUEST_USER_TYPE_VALUE = "guest";
    private static final String META_DATA_IS_CONNECTION_REQUIRED = "com.android.settings.require_connection";
    private static final String META_DATA_IS_SUPPORTED = "com.android.settings.is_supported";
    private static final String META_DATA_PRIMARY_USER_TYPE_VALUE = "primary";
    private static final String META_DATA_REQUIRE_ACCOUNT = "com.android.settings.require_account";
    public static final String META_DATA_REQUIRE_FEATURE = "com.android.settings.require_feature";
    private static final String META_DATA_REQUIRE_USER_TYPE = "com.android.settings.require_user_type";
    private static final String META_DATA_RESTRICTED_USER_TYPE_VALUE = "restricted";
    public static final String SETUP_TIME = "_setup_time";
    private static final String TAG = "SuggestionParser";
    private final ArrayMap<Pair<String, String>, Tile> mAddCache;
    private final Context mContext;
    private final String mDefaultDismissControl;
    private final SharedPreferences mSharedPrefs;
    private final List<SuggestionCategory> mSuggestionList;

    public SuggestionParser(Context context, SharedPreferences sharedPrefs, int orderXml, String defaultDismissControl) {
        this(context, sharedPrefs, (List<SuggestionCategory>) (List) new SuggestionOrderInflater(context).parse(orderXml), defaultDismissControl);
    }

    public SuggestionParser(Context context, SharedPreferences sharedPrefs, int orderXml) {
        this(context, sharedPrefs, orderXml, DEFAULT_SMART_DISMISS_CONTROL);
    }

    @VisibleForTesting
    public SuggestionParser(Context context, SharedPreferences sharedPrefs, List<SuggestionCategory> suggestionList, String defaultDismissControl) {
        this.mAddCache = new ArrayMap<>();
        this.mContext = context;
        this.mSuggestionList = suggestionList;
        this.mSharedPrefs = sharedPrefs;
        this.mDefaultDismissControl = defaultDismissControl;
    }

    public SuggestionList getSuggestions(boolean isSmartSuggestionEnabled) {
        SuggestionList suggestionList = new SuggestionList();
        int N = this.mSuggestionList.size();
        for (int i = 0; i < N; i++) {
            SuggestionCategory category = this.mSuggestionList.get(i);
            if (!category.exclusive || isExclusiveCategoryExpired(category)) {
                List<Tile> suggestions = new ArrayList<>();
                readSuggestions(category, suggestions, isSmartSuggestionEnabled);
                suggestionList.addSuggestions(category, suggestions);
            } else {
                List<Tile> exclusiveSuggestions = new ArrayList<>();
                readSuggestions(category, exclusiveSuggestions, false);
                if (!exclusiveSuggestions.isEmpty()) {
                    SuggestionList exclusiveList = new SuggestionList();
                    exclusiveList.addSuggestions(category, exclusiveSuggestions);
                    return exclusiveList;
                }
            }
        }
        return suggestionList;
    }

    public boolean dismissSuggestion(Tile suggestion) {
        String keyBase = suggestion.intent.getComponent().flattenToShortString();
        SharedPreferences.Editor edit = this.mSharedPrefs.edit();
        edit.putBoolean(keyBase + IS_DISMISSED, true).commit();
        return true;
    }

    @VisibleForTesting
    public void filterSuggestions(List<Tile> suggestions, int countBefore, boolean isSmartSuggestionEnabled) {
        int i = countBefore;
        while (i < suggestions.size()) {
            if (!isAvailable(suggestions.get(i)) || !isSupported(suggestions.get(i)) || !satisifesRequiredUserType(suggestions.get(i)) || !satisfiesRequiredAccount(suggestions.get(i)) || !satisfiesConnectivity(suggestions.get(i)) || isDismissed(suggestions.get(i), isSmartSuggestionEnabled)) {
                suggestions.remove(i);
                i--;
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void readSuggestions(SuggestionCategory category, List<Tile> suggestions, boolean isSmartSuggestionEnabled) {
        SuggestionCategory suggestionCategory = category;
        List<Tile> list = suggestions;
        int countBefore = suggestions.size();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory(suggestionCategory.category);
        if (suggestionCategory.pkg != null) {
            intent.setPackage(suggestionCategory.pkg);
        }
        TileUtils.getTilesForIntent(this.mContext, new UserHandle(UserHandle.myUserId()), intent, this.mAddCache, (String) null, list, true, false, false, true);
        filterSuggestions(list, countBefore, isSmartSuggestionEnabled);
        if (!suggestionCategory.multiple && suggestions.size() > countBefore + 1) {
            Tile item = list.remove(suggestions.size() - 1);
            while (suggestions.size() > countBefore) {
                Tile last = list.remove(suggestions.size() - 1);
                if (last.priority > item.priority) {
                    item = last;
                }
            }
            if (!isCategoryDone(suggestionCategory.category)) {
                list.add(item);
            }
        }
    }

    private boolean isAvailable(Tile suggestion) {
        String featuresRequired = suggestion.metaData.getString(META_DATA_REQUIRE_FEATURE);
        if (featuresRequired == null) {
            return true;
        }
        for (String feature : featuresRequired.split(",")) {
            if (TextUtils.isEmpty(feature)) {
                Log.w(TAG, "Found empty substring when parsing required features: " + featuresRequired);
            } else if (!this.mContext.getPackageManager().hasSystemFeature(feature)) {
                Log.i(TAG, suggestion.title + " requires unavailable feature " + feature);
                return false;
            }
        }
        return true;
    }

    private boolean satisifesRequiredUserType(Tile suggestion) {
        String requiredUser = suggestion.metaData.getString(META_DATA_REQUIRE_USER_TYPE);
        if (requiredUser == null) {
            return true;
        }
        UserInfo userInfo = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserInfo(UserHandle.myUserId());
        for (String userType : requiredUser.split("\\|")) {
            boolean primaryUserCondtionMet = userInfo.isPrimary() && META_DATA_PRIMARY_USER_TYPE_VALUE.equals(userType);
            boolean adminUserConditionMet = userInfo.isAdmin() && META_DATA_ADMIN_USER_TYPE_VALUE.equals(userType);
            boolean guestUserCondtionMet = userInfo.isGuest() && META_DATA_GUEST_USER_TYPE_VALUE.equals(userType);
            boolean restrictedUserCondtionMet = userInfo.isRestricted() && META_DATA_RESTRICTED_USER_TYPE_VALUE.equals(userType);
            if (primaryUserCondtionMet || adminUserConditionMet || guestUserCondtionMet || restrictedUserCondtionMet) {
                return true;
            }
        }
        Log.i(TAG, suggestion.title + " requires user type " + requiredUser);
        return false;
    }

    public boolean satisfiesRequiredAccount(Tile suggestion) {
        String requiredAccountType = suggestion.metaData.getString(META_DATA_REQUIRE_ACCOUNT);
        boolean satisfiesRequiredAccount = true;
        if (requiredAccountType == null) {
            return true;
        }
        if (((AccountManager) this.mContext.getSystemService(AccountManager.class)).getAccountsByType(requiredAccountType).length <= 0) {
            satisfiesRequiredAccount = false;
        }
        if (!satisfiesRequiredAccount) {
            Log.i(TAG, suggestion.title + " requires unavailable account type " + requiredAccountType);
        }
        return satisfiesRequiredAccount;
    }

    public boolean isSupported(Tile suggestion) {
        int isSupportedResource = suggestion.metaData.getInt(META_DATA_IS_SUPPORTED);
        try {
            if (suggestion.intent == null) {
                return false;
            }
            boolean isSupported = isSupportedResource != 0 ? this.mContext.getPackageManager().getResourcesForActivity(suggestion.intent.getComponent()).getBoolean(isSupportedResource) : true;
            if (!isSupported) {
                Log.i(TAG, suggestion.title + " requires unsupported resource " + isSupportedResource);
            }
            return isSupported;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Cannot find resources for " + suggestion.intent.getComponent());
            return false;
        } catch (Resources.NotFoundException e2) {
            Log.w(TAG, "Cannot find resources for " + suggestion.intent.getComponent(), e2);
            return false;
        }
    }

    private boolean satisfiesConnectivity(Tile suggestion) {
        boolean satisfiesConnectivity = true;
        if (!suggestion.metaData.getBoolean(META_DATA_IS_CONNECTION_REQUIRED)) {
            return true;
        }
        NetworkInfo netInfo = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            satisfiesConnectivity = false;
        }
        if (!satisfiesConnectivity) {
            Log.i(TAG, suggestion.title + " is missing required connection.");
        }
        return satisfiesConnectivity;
    }

    public boolean isCategoryDone(String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("suggested.completed_category.");
        sb.append(category);
        return Settings.Secure.getInt(this.mContext.getContentResolver(), sb.toString(), 0) != 0;
    }

    public void markCategoryDone(String category) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "suggested.completed_category." + category, 1);
    }

    private boolean isExclusiveCategoryExpired(SuggestionCategory category) {
        String keySetupTime = category.category + SETUP_TIME;
        long currentTime = System.currentTimeMillis();
        if (!this.mSharedPrefs.contains(keySetupTime)) {
            this.mSharedPrefs.edit().putLong(keySetupTime, currentTime).commit();
        }
        if (category.exclusiveExpireDaysInMillis < 0) {
            return false;
        }
        long elapsedTime = currentTime - this.mSharedPrefs.getLong(keySetupTime, 0);
        Log.d(TAG, "Day " + (elapsedTime / 86400000) + " for " + category.category);
        if (elapsedTime > category.exclusiveExpireDaysInMillis) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isDismissed(Tile suggestion, boolean isSmartSuggestionEnabled) {
        String dismissControl = getDismissControl(suggestion, isSmartSuggestionEnabled);
        String keyBase = suggestion.intent.getComponent().flattenToShortString();
        SharedPreferences sharedPreferences = this.mSharedPrefs;
        if (!sharedPreferences.contains(keyBase + SETUP_TIME)) {
            SharedPreferences.Editor edit = this.mSharedPrefs.edit();
            edit.putLong(keyBase + SETUP_TIME, System.currentTimeMillis()).commit();
        }
        SharedPreferences sharedPreferences2 = this.mSharedPrefs;
        if (sharedPreferences2.getBoolean(keyBase + IS_DISMISSED, false)) {
            return true;
        }
        if (dismissControl == null) {
            return false;
        }
        int firstAppearDay = parseDismissString(dismissControl);
        SharedPreferences sharedPreferences3 = this.mSharedPrefs;
        if (System.currentTimeMillis() < getEndTime(sharedPreferences3.getLong(keyBase + SETUP_TIME, 0), firstAppearDay)) {
            return true;
        }
        SharedPreferences.Editor edit2 = this.mSharedPrefs.edit();
        edit2.putBoolean(keyBase + IS_DISMISSED, false).commit();
        return false;
    }

    private long getEndTime(long startTime, int daysDelay) {
        return startTime + (((long) daysDelay) * 86400000);
    }

    private int parseDismissString(String dismissControl) {
        return Integer.parseInt(dismissControl.split(",")[0]);
    }

    private String getDismissControl(Tile suggestion, boolean isSmartSuggestionEnabled) {
        if (isSmartSuggestionEnabled) {
            return this.mDefaultDismissControl;
        }
        return suggestion.metaData.getString(META_DATA_DISMISS_CONTROL);
    }

    private static class SuggestionOrderInflater {
        private static final String ATTR_CATEGORY = "category";
        private static final String ATTR_EXCLUSIVE = "exclusive";
        private static final String ATTR_EXCLUSIVE_EXPIRE_DAYS = "exclusiveExpireDays";
        private static final String ATTR_MULTIPLE = "multiple";
        private static final String ATTR_PACKAGE = "package";
        private static final String TAG_ITEM = "step";
        private static final String TAG_LIST = "optional-steps";
        private final Context mContext;

        public SuggestionOrderInflater(Context context) {
            this.mContext = context;
        }

        /* JADX WARNING: Removed duplicated region for block: B:7:0x001a A[Catch:{ IOException | XmlPullParserException -> 0x0041 }] */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0026 A[Catch:{ IOException | XmlPullParserException -> 0x0041 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object parse(int r7) {
            /*
                r6 = this;
                android.content.Context r0 = r6.mContext
                android.content.res.Resources r0 = r0.getResources()
                android.content.res.XmlResourceParser r0 = r0.getXml(r7)
                android.util.AttributeSet r1 = android.util.Xml.asAttributeSet(r0)
            L_0x000e:
                int r2 = r0.next()     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                r3 = 2
                if (r2 == r3) goto L_0x0018
                r4 = 1
                if (r2 != r4) goto L_0x000e
            L_0x0018:
                if (r2 != r3) goto L_0x0026
                java.lang.String r3 = r0.getName()     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                java.lang.Object r3 = r6.onCreateItem(r3, r1)     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                r6.rParse(r0, r3, r1)     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                return r3
            L_0x0026:
                android.view.InflateException r3 = new android.view.InflateException     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                r4.<init>()     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                java.lang.String r5 = r0.getPositionDescription()     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                r4.append(r5)     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                java.lang.String r5 = ": No start tag found!"
                r4.append(r5)     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                java.lang.String r4 = r4.toString()     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                r3.<init>(r4)     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
                throw r3     // Catch:{ IOException | XmlPullParserException -> 0x0041 }
            L_0x0041:
                r2 = move-exception
                java.lang.String r3 = "SuggestionParser"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Problem parser resource "
                r4.append(r5)
                r4.append(r7)
                java.lang.String r4 = r4.toString()
                android.util.Log.w(r3, r4, r2)
                r3 = 0
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.suggestions.SuggestionParser.SuggestionOrderInflater.parse(int):java.lang.Object");
        }

        private void rParse(XmlPullParser parser, Object parent, AttributeSet attrs) throws XmlPullParserException, IOException {
            int depth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                if ((next == 3 && parser.getDepth() <= depth) || type == 1) {
                    return;
                }
                if (type == 2) {
                    Object item = onCreateItem(parser.getName(), attrs);
                    onAddChildItem(parent, item);
                    rParse(parser, item, attrs);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onAddChildItem(Object parent, Object child) {
            if (!(parent instanceof List) || !(child instanceof SuggestionCategory)) {
                throw new IllegalArgumentException("Parent was not a list");
            }
            ((List) parent).add((SuggestionCategory) child);
        }

        /* access modifiers changed from: protected */
        public Object onCreateItem(String name, AttributeSet attrs) {
            long expireDays;
            if (name.equals(TAG_LIST)) {
                return new ArrayList();
            }
            if (name.equals(TAG_ITEM)) {
                SuggestionCategory category = new SuggestionCategory();
                category.category = attrs.getAttributeValue((String) null, ATTR_CATEGORY);
                category.pkg = attrs.getAttributeValue((String) null, "package");
                String multiple = attrs.getAttributeValue((String) null, ATTR_MULTIPLE);
                boolean z = false;
                category.multiple = !TextUtils.isEmpty(multiple) && Boolean.parseBoolean(multiple);
                String exclusive = attrs.getAttributeValue((String) null, ATTR_EXCLUSIVE);
                if (!TextUtils.isEmpty(exclusive) && Boolean.parseBoolean(exclusive)) {
                    z = true;
                }
                category.exclusive = z;
                String expireDaysAttr = attrs.getAttributeValue((String) null, ATTR_EXCLUSIVE_EXPIRE_DAYS);
                if (!TextUtils.isEmpty(expireDaysAttr)) {
                    expireDays = (long) Integer.parseInt(expireDaysAttr);
                } else {
                    expireDays = -1;
                }
                category.exclusiveExpireDaysInMillis = 86400000 * expireDays;
                return category;
            }
            throw new IllegalArgumentException("Unknown item " + name);
        }
    }
}
