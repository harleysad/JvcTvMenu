package com.android.tv.menu.customization;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvCustomizationManager {
    private static final String CATEGORY_TV_CUSTOMIZATION = "com.android.tv.category";
    private static final boolean DEBUG = false;
    public static final String ID_OPTIONS_ROW = "options_row";
    public static final String ID_PARTNER_ROW = "partner_row";
    private static final HashMap<String, String> INTENT_CATEGORY_TO_ROW_ID = new HashMap<>();
    private static final String RES_ID_PARTNER_ROW_TITLE = "partner_row_title";
    private static final String RES_TYPE_STRING = "string";
    private static final String TAG = "TvCustomizationManager";
    private final Context mContext;
    private String mCustomizationPackage;
    private boolean mInitialized;
    private String mPartnerRowTitle;
    private final Map<String, List<CustomAction>> mRowIdToCustomActionsMap = new HashMap();

    static {
        INTENT_CATEGORY_TO_ROW_ID.put("com.android.tv.category.OPTIONS_ROW", ID_OPTIONS_ROW);
        INTENT_CATEGORY_TO_ROW_ID.put("com.android.tv.category.PARTNER_ROW", ID_PARTNER_ROW);
    }

    public TvCustomizationManager(Context context) {
        this.mContext = context;
        this.mInitialized = false;
    }

    public void initialize() {
        if (!this.mInitialized) {
            this.mInitialized = true;
            buildCustomActions();
            if (!TextUtils.isEmpty(this.mCustomizationPackage)) {
                buildPartnerRow();
            }
        }
    }

    private void buildCustomActions() {
        this.mCustomizationPackage = null;
        this.mRowIdToCustomActionsMap.clear();
        PackageManager pm = this.mContext.getPackageManager();
        for (String intentCategory : INTENT_CATEGORY_TO_ROW_ID.keySet()) {
            Intent customOptionIntent = new Intent("android.intent.action.MAIN");
            customOptionIntent.addCategory(intentCategory);
            for (ResolveInfo info : pm.queryIntentActivities(customOptionIntent, 194)) {
                String packageName = info.activityInfo.packageName;
                if (TextUtils.isEmpty(this.mCustomizationPackage)) {
                    if ((info.activityInfo.applicationInfo.flags & 1) == 0) {
                        Log.w(TAG, "Only system app can customize TV. Ignoring " + packageName);
                    } else {
                        this.mCustomizationPackage = packageName;
                    }
                } else if (!packageName.equals(this.mCustomizationPackage)) {
                    Log.w(TAG, "A customization package " + this.mCustomizationPackage + " already exist. Ignoring " + packageName);
                }
                int position = info.filter.getPriority();
                String title = info.loadLabel(pm).toString();
                Drawable drawable = info.loadIcon(pm);
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory(intentCategory);
                intent.setClassName(this.mCustomizationPackage, info.activityInfo.name);
                String rowId = INTENT_CATEGORY_TO_ROW_ID.get(intentCategory);
                List<CustomAction> actions = this.mRowIdToCustomActionsMap.get(rowId);
                if (actions == null) {
                    actions = new ArrayList<>();
                    this.mRowIdToCustomActionsMap.put(rowId, actions);
                }
                actions.add(new CustomAction(position, title, drawable, intent));
            }
        }
        for (List<CustomAction> actions2 : this.mRowIdToCustomActionsMap.values()) {
            Collections.sort(actions2);
        }
    }

    public List<CustomAction> getCustomActions(String rowId) {
        return this.mRowIdToCustomActionsMap.get(rowId);
    }

    private void buildPartnerRow() {
        this.mPartnerRowTitle = null;
        try {
            Resources res = this.mContext.getPackageManager().getResourcesForApplication(this.mCustomizationPackage);
            int resId = res.getIdentifier(RES_ID_PARTNER_ROW_TITLE, RES_TYPE_STRING, this.mCustomizationPackage);
            if (resId != 0) {
                this.mPartnerRowTitle = res.getString(resId);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Could not get resources for package " + this.mCustomizationPackage);
        }
    }

    public String getPartnerRowTitle() {
        return this.mPartnerRowTitle;
    }
}
