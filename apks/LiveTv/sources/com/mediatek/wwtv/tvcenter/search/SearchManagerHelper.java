package com.mediatek.wwtv.tvcenter.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;

public final class SearchManagerHelper {
    private static final String TAG = "SearchManagerHelper";
    private static SearchManagerHelper sInstance;
    private static final Object sLock = new Object();
    private final SearchManager mSearchManager;

    private SearchManagerHelper(Context context) {
        this.mSearchManager = (SearchManager) context.getSystemService("search");
    }

    public static SearchManagerHelper getInstance(Context context) {
        SearchManagerHelper searchManagerHelper;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new SearchManagerHelper(context.getApplicationContext());
            }
            searchManagerHelper = sInstance;
        }
        return searchManagerHelper;
    }

    public void launchAssistAction() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                SearchManager.class.getDeclaredMethod("launchLegacyAssist", new Class[]{String.class, Integer.TYPE, Bundle.class}).invoke(this.mSearchManager, new Object[]{null, Integer.valueOf(UserHandle.myUserId()), null});
                return;
            }
            SearchManager.class.getDeclaredMethod("launchAssistAction", new Class[]{Integer.TYPE, String.class, Integer.TYPE}).invoke(this.mSearchManager, new Object[]{0, null, Integer.valueOf(UserHandle.myUserId())});
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            Log.e(TAG, "Fail to call SearchManager.launchAssistAction", e);
        }
    }
}
