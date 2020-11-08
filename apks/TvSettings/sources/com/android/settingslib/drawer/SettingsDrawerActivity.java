package com.android.settingslib.drawer;

import android.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toolbar;
import com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails;
import java.util.ArrayList;
import java.util.List;

public class SettingsDrawerActivity extends Activity {
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    protected static final boolean DEBUG_TIMING = false;
    public static final String EXTRA_SHOW_MENU = "show_drawer_menu";
    private static final String TAG = "SettingsDrawerActivity";
    /* access modifiers changed from: private */
    public static ArraySet<ComponentName> sTileBlacklist = new ArraySet<>();
    private final List<CategoryListener> mCategoryListeners = new ArrayList();
    private FrameLayout mContentHeaderContainer;
    private final PackageReceiver mPackageReceiver = new PackageReceiver();

    public interface CategoryListener {
        void onCategoriesChanged();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long currentTimeMillis = System.currentTimeMillis();
        TypedArray theme = getTheme().obtainStyledAttributes(R.styleable.Theme);
        if (!theme.getBoolean(38, false)) {
            getWindow().addFlags(Integer.MIN_VALUE);
            requestWindowFeature(1);
        }
        super.setContentView(com.android.settingslib.R.layout.settings_with_drawer);
        this.mContentHeaderContainer = (FrameLayout) findViewById(com.android.settingslib.R.id.content_header_container);
        Toolbar toolbar = (Toolbar) findViewById(com.android.settingslib.R.id.action_bar);
        if (theme.getBoolean(38, false)) {
            toolbar.setVisibility(8);
        } else {
            setActionBar(toolbar);
        }
    }

    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addDataScheme(DirectoryAccessDetails.ARG_PACKAGE_NAME);
        registerReceiver(this.mPackageReceiver, filter);
        new CategoriesUpdateTask().execute(new Void[0]);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        unregisterReceiver(this.mPackageReceiver);
        super.onPause();
    }

    public void addCategoryListener(CategoryListener listener) {
        this.mCategoryListeners.add(listener);
    }

    public void remCategoryListener(CategoryListener listener) {
        this.mCategoryListeners.remove(listener);
    }

    public void setContentView(int layoutResID) {
        ViewGroup parent = (ViewGroup) findViewById(com.android.settingslib.R.id.content_frame);
        if (parent != null) {
            parent.removeAllViews();
        }
        LayoutInflater.from(this).inflate(layoutResID, parent);
    }

    public void setContentView(View view) {
        ((ViewGroup) findViewById(com.android.settingslib.R.id.content_frame)).addView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        ((ViewGroup) findViewById(com.android.settingslib.R.id.content_frame)).addView(view, params);
    }

    /* access modifiers changed from: private */
    public void onCategoriesChanged() {
        int N = this.mCategoryListeners.size();
        for (int i = 0; i < N; i++) {
            this.mCategoryListeners.get(i).onCategoriesChanged();
        }
    }

    public boolean setTileEnabled(ComponentName component, boolean enabled) {
        PackageManager pm = getPackageManager();
        int state = pm.getComponentEnabledSetting(component);
        if ((state == 1) == enabled && state != 0) {
            return false;
        }
        if (enabled) {
            sTileBlacklist.remove(component);
        } else {
            sTileBlacklist.add(component);
        }
        pm.setComponentEnabledSetting(component, enabled ? 1 : 2, 1);
        return true;
    }

    public void updateCategories() {
        new CategoriesUpdateTask().execute(new Void[0]);
    }

    public String getSettingPkg() {
        return TileUtils.SETTING_PKG;
    }

    private class CategoriesUpdateTask extends AsyncTask<Void, Void, Void> {
        private final CategoryManager mCategoryManager;

        public CategoriesUpdateTask() {
            this.mCategoryManager = CategoryManager.get(SettingsDrawerActivity.this);
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            this.mCategoryManager.reloadAllCategories(SettingsDrawerActivity.this, SettingsDrawerActivity.this.getSettingPkg());
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            this.mCategoryManager.updateCategoryFromBlacklist(SettingsDrawerActivity.sTileBlacklist);
            SettingsDrawerActivity.this.onCategoriesChanged();
        }
    }

    private class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            new CategoriesUpdateTask().execute(new Void[0]);
        }
    }
}
