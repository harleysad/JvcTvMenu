package com.android.settingslib.drawer;

import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.applications.InterestingConfigChanges;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryManager {
    private static final String TAG = "CategoryManager";
    private static CategoryManager sInstance;
    private List<DashboardCategory> mCategories;
    private final Map<String, DashboardCategory> mCategoryByKeyMap = new ArrayMap();
    private String mExtraAction;
    private final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
    private final Map<Pair<String, String>, Tile> mTileByComponentCache = new ArrayMap();

    public static CategoryManager get(Context context) {
        return get(context, (String) null);
    }

    public static CategoryManager get(Context context, String action) {
        if (sInstance == null) {
            sInstance = new CategoryManager(context, action);
        }
        return sInstance;
    }

    CategoryManager(Context context, String action) {
        this.mInterestingConfigChanges.applyNewConfig(context.getResources());
        this.mExtraAction = action;
    }

    public synchronized DashboardCategory getTilesByCategory(Context context, String categoryKey) {
        return getTilesByCategory(context, categoryKey, TileUtils.SETTING_PKG);
    }

    public synchronized DashboardCategory getTilesByCategory(Context context, String categoryKey, String settingPkg) {
        tryInitCategories(context, settingPkg);
        return this.mCategoryByKeyMap.get(categoryKey);
    }

    public synchronized List<DashboardCategory> getCategories(Context context) {
        return getCategories(context, TileUtils.SETTING_PKG);
    }

    public synchronized List<DashboardCategory> getCategories(Context context, String settingPkg) {
        tryInitCategories(context, settingPkg);
        return this.mCategories;
    }

    public synchronized void reloadAllCategories(Context context, String settingPkg) {
        boolean forceClearCache = this.mInterestingConfigChanges.applyNewConfig(context.getResources());
        this.mCategories = null;
        tryInitCategories(context, forceClearCache, settingPkg);
    }

    public synchronized void updateCategoryFromBlacklist(Set<ComponentName> tileBlacklist) {
        if (this.mCategories == null) {
            Log.w(TAG, "Category is null, skipping blacklist update");
        }
        for (int i = 0; i < this.mCategories.size(); i++) {
            DashboardCategory category = this.mCategories.get(i);
            int j = 0;
            while (j < category.getTilesCount()) {
                if (tileBlacklist.contains(category.getTile(j).intent.getComponent())) {
                    int j2 = j - 1;
                    category.removeTile(j);
                    j = j2;
                }
                j++;
            }
        }
    }

    private synchronized void tryInitCategories(Context context, String settingPkg) {
        tryInitCategories(context, false, settingPkg);
    }

    private synchronized void tryInitCategories(Context context, boolean forceClearCache, String settingPkg) {
        if (this.mCategories == null) {
            if (forceClearCache) {
                this.mTileByComponentCache.clear();
            }
            this.mCategoryByKeyMap.clear();
            this.mCategories = TileUtils.getCategories(context, this.mTileByComponentCache, false, this.mExtraAction, settingPkg);
            for (DashboardCategory category : this.mCategories) {
                this.mCategoryByKeyMap.put(category.key, category);
            }
            backwardCompatCleanupForCategory(this.mTileByComponentCache, this.mCategoryByKeyMap);
            sortCategories(context, this.mCategoryByKeyMap);
            filterDuplicateTiles(this.mCategoryByKeyMap);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public synchronized void backwardCompatCleanupForCategory(Map<Pair<String, String>, Tile> tileByComponentCache, Map<String, DashboardCategory> categoryByKeyMap) {
        Map<String, List<Tile>> packageToTileMap = new HashMap<>();
        for (Map.Entry<Pair<String, String>, Tile> tileEntry : tileByComponentCache.entrySet()) {
            String packageName = (String) tileEntry.getKey().first;
            List<Tile> tiles = packageToTileMap.get(packageName);
            if (tiles == null) {
                tiles = new ArrayList<>();
                packageToTileMap.put(packageName, tiles);
            }
            tiles.add(tileEntry.getValue());
        }
        for (Map.Entry<String, List<Tile>> entry : packageToTileMap.entrySet()) {
            List<Tile> tiles2 = entry.getValue();
            boolean useNewKey = false;
            boolean useOldKey = false;
            Iterator<Tile> it = tiles2.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (!CategoryKey.KEY_COMPAT_MAP.containsKey(it.next().category)) {
                        useNewKey = true;
                        break;
                    }
                    useOldKey = true;
                } else {
                    break;
                }
            }
            if (useOldKey && !useNewKey) {
                for (Tile tile : tiles2) {
                    String newCategoryKey = CategoryKey.KEY_COMPAT_MAP.get(tile.category);
                    tile.category = newCategoryKey;
                    DashboardCategory newCategory = categoryByKeyMap.get(newCategoryKey);
                    if (newCategory == null) {
                        newCategory = new DashboardCategory();
                        categoryByKeyMap.put(newCategoryKey, newCategory);
                    }
                    newCategory.addTile(tile);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public synchronized void sortCategories(Context context, Map<String, DashboardCategory> categoryByKeyMap) {
        for (Map.Entry<String, DashboardCategory> categoryEntry : categoryByKeyMap.entrySet()) {
            categoryEntry.getValue().sortTiles(context.getPackageName());
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public synchronized void filterDuplicateTiles(Map<String, DashboardCategory> categoryByKeyMap) {
        for (Map.Entry<String, DashboardCategory> categoryEntry : categoryByKeyMap.entrySet()) {
            DashboardCategory category = categoryEntry.getValue();
            int count = category.getTilesCount();
            Set<ComponentName> components = new ArraySet<>();
            for (int i = count - 1; i >= 0; i--) {
                Tile tile = category.getTile(i);
                if (tile.intent != null) {
                    ComponentName tileComponent = tile.intent.getComponent();
                    if (components.contains(tileComponent)) {
                        category.removeTile(i);
                    } else {
                        components.add(tileComponent);
                    }
                }
            }
        }
    }

    private synchronized void sortCategoriesForExternalTiles(Context context, DashboardCategory dashboardCategory) {
        dashboardCategory.sortTiles(context.getPackageName());
    }
}
