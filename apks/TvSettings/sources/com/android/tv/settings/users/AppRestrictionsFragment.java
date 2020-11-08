package com.android.tv.settings.users;

import android.app.AppGlobals;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.util.ArrayMap;
import android.support.v7.preference.AndroidResources;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.settingslib.users.AppRestrictionsHelper;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AppRestrictionsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, AppRestrictionsHelper.OnDisableUiForPackageListener {
    private static final String ACTIVITY_PREFIX = "activity_";
    /* access modifiers changed from: private */
    public static final Drawable BLANK_DRAWABLE = new ColorDrawable(0);
    private static final int CUSTOM_REQUEST_CODE_START = 1000;
    private static final boolean DEBUG = false;
    private static final String DELIMITER = ";";
    private static final String EXTRA_NEW_USER = "new_user";
    private static final String EXTRA_USER_ID = "user_id";
    private static final int MAX_APP_RESTRICTIONS = 100;
    private static final String PKG_PREFIX = "pkg_";
    private static final String STATE_CUSTOM_REQUEST_MAP_KEYS = "customRequestMapKeys";
    private static final String STATE_CUSTOM_REQUEST_MAP_VALUES = "customRequestMapValues";
    /* access modifiers changed from: private */
    public static final String TAG = AppRestrictionsFragment.class.getSimpleName();
    private PreferenceGroup mAppList;
    /* access modifiers changed from: private */
    public boolean mAppListChanged;
    private AsyncTask mAppLoadingTask;
    private int mCustomRequestCode = 1000;
    private Map<Integer, String> mCustomRequestMap = new ArrayMap();
    private boolean mFirstTime = true;
    /* access modifiers changed from: private */
    public AppRestrictionsHelper mHelper;
    private IPackageManager mIPm;
    private boolean mNewUser;
    private PackageManager mPackageManager;
    private BroadcastReceiver mPackageObserver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AppRestrictionsFragment.this.onPackageChanged(intent);
        }
    };
    /* access modifiers changed from: private */
    public boolean mRestrictedProfile;
    private PackageInfo mSysPackageInfo;
    /* access modifiers changed from: private */
    public UserHandle mUser;
    private BroadcastReceiver mUserBackgrounding = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (AppRestrictionsFragment.this.mAppListChanged) {
                AppRestrictionsFragment.this.mHelper.applyUserAppsStates(AppRestrictionsFragment.this);
            }
        }
    };
    /* access modifiers changed from: private */
    public UserManager mUserManager;

    private static class AppRestrictionsPreference extends PreferenceGroup {
        private boolean mChecked;
        private boolean mCheckedSet;
        private boolean mImmutable;
        private final Listener mListener = new Listener();
        private ArrayList<RestrictionEntry> mRestrictions;

        AppRestrictionsPreference(Context context) {
            super(context, (AttributeSet) null, 0, 2131755199);
        }

        /* access modifiers changed from: package-private */
        public void setRestrictions(ArrayList<RestrictionEntry> restrictions) {
            this.mRestrictions = restrictions;
        }

        /* access modifiers changed from: package-private */
        public void setImmutable(boolean immutable) {
            this.mImmutable = immutable;
        }

        /* access modifiers changed from: package-private */
        public boolean isImmutable() {
            return this.mImmutable;
        }

        /* access modifiers changed from: package-private */
        public ArrayList<RestrictionEntry> getRestrictions() {
            return this.mRestrictions;
        }

        public void setChecked(boolean checked) {
            boolean changed = this.mChecked != checked;
            if (changed || !this.mCheckedSet) {
                this.mChecked = checked;
                this.mCheckedSet = true;
                persistBoolean(checked);
                if (changed) {
                    notifyDependencyChange(shouldDisableDependents());
                    notifyChanged();
                    notifyHierarchyChanged();
                }
            }
        }

        public int getPreferenceCount() {
            if (isChecked()) {
                return super.getPreferenceCount();
            }
            return 0;
        }

        public boolean isChecked() {
            return this.mChecked;
        }

        public void onBindViewHolder(PreferenceViewHolder holder) {
            super.onBindViewHolder(holder);
            syncSwitchView(holder.findViewById(AndroidResources.ANDROID_R_SWITCH_WIDGET));
        }

        private void syncSwitchView(View view) {
            if (view instanceof Switch) {
                ((Switch) view).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            }
            if (view instanceof Checkable) {
                ((Checkable) view).setChecked(this.mChecked);
            }
            if (view instanceof Switch) {
                ((Switch) view).setOnCheckedChangeListener(this.mListener);
            }
        }

        private class Listener implements CompoundButton.OnCheckedChangeListener {
            private Listener() {
            }

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!AppRestrictionsPreference.this.callChangeListener(Boolean.valueOf(isChecked))) {
                    buttonView.setChecked(!isChecked);
                } else {
                    AppRestrictionsPreference.this.setChecked(isChecked);
                }
            }
        }
    }

    public static void prepareArgs(@NonNull Bundle bundle, int userId, boolean newUser) {
        bundle.putInt(EXTRA_USER_ID, userId);
        bundle.putBoolean(EXTRA_NEW_USER, newUser);
    }

    public static AppRestrictionsFragment newInstance(int userId, boolean newUser) {
        Bundle args = new Bundle(2);
        prepareArgs(args, userId, newUser);
        AppRestrictionsFragment fragment = new AppRestrictionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mUser = new UserHandle(savedInstanceState.getInt(EXTRA_USER_ID));
            ArrayList<Integer> keys = savedInstanceState.getIntegerArrayList(STATE_CUSTOM_REQUEST_MAP_KEYS);
            List<String> values = Arrays.asList(savedInstanceState.getStringArray(STATE_CUSTOM_REQUEST_MAP_VALUES));
            Map<Integer, String> map = this.mCustomRequestMap;
            Stream<Integer> boxed = IntStream.range(0, keys.size()).boxed();
            Objects.requireNonNull(keys);
            $$Lambda$AppRestrictionsFragment$eEmq8kKv5ezb4fAFotHJQy0dYAQ r5 = new Function(keys) {
                private final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final Object apply(Object obj) {
                    return (Integer) this.f$0.get(((Integer) obj).intValue());
                }
            };
            Objects.requireNonNull(values);
            map.putAll((Map) boxed.collect(Collectors.toMap(r5, new Function(values) {
                private final /* synthetic */ List f$0;

                {
                    this.f$0 = r1;
                }

                public final Object apply(Object obj) {
                    return (String) this.f$0.get(((Integer) obj).intValue());
                }
            })));
        } else {
            Bundle args = getArguments();
            if (args != null) {
                if (args.containsKey(EXTRA_USER_ID)) {
                    this.mUser = new UserHandle(args.getInt(EXTRA_USER_ID));
                }
                this.mNewUser = args.getBoolean(EXTRA_NEW_USER, false);
            }
        }
        if (this.mUser == null) {
            this.mUser = Process.myUserHandle();
        }
        this.mHelper = new AppRestrictionsHelper(getContext(), this.mUser);
        this.mHelper.setLeanback(true);
        this.mPackageManager = getActivity().getPackageManager();
        this.mIPm = AppGlobals.getPackageManager();
        this.mUserManager = (UserManager) getActivity().getSystemService("user");
        this.mRestrictedProfile = this.mUserManager.getUserInfo(this.mUser.getIdentifier()).isRestricted();
        try {
            this.mSysPackageInfo = this.mPackageManager.getPackageInfo("android", 64);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(TAG, "Could not find system package signatures", nnfe);
        }
        this.mAppList = getAppPreferenceGroup();
        this.mAppList.setOrderingAsAdded(false);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((int) R.string.restricted_profile_configure_apps_title);
        setPreferenceScreen(screen);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_USER_ID, this.mUser.getIdentifier());
        ArrayList<Integer> keys = new ArrayList<>(this.mCustomRequestMap.keySet());
        Stream stream = keys.stream();
        Map<Integer, String> map = this.mCustomRequestMap;
        Objects.requireNonNull(map);
        List<String> values = (List) stream.map(new Function(map) {
            private final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return (String) this.f$0.get((Integer) obj);
            }
        }).collect(Collectors.toList());
        outState.putIntegerArrayList(STATE_CUSTOM_REQUEST_MAP_KEYS, keys);
        outState.putStringArray(STATE_CUSTOM_REQUEST_MAP_VALUES, (String[]) values.toArray(new String[values.size()]));
    }

    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.mUserBackgrounding, new IntentFilter("android.intent.action.USER_BACKGROUND"));
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        packageFilter.addDataScheme(DirectoryAccessDetails.ARG_PACKAGE_NAME);
        getActivity().registerReceiver(this.mPackageObserver, packageFilter);
        this.mAppListChanged = false;
        if (this.mAppLoadingTask == null || this.mAppLoadingTask.getStatus() == AsyncTask.Status.FINISHED) {
            this.mAppLoadingTask = new AppLoadingTask().execute(new Void[0]);
        }
    }

    public void onPause() {
        super.onPause();
        this.mNewUser = false;
        getActivity().unregisterReceiver(this.mUserBackgrounding);
        getActivity().unregisterReceiver(this.mPackageObserver);
        if (this.mAppListChanged) {
            new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                public Void doInBackground(Void... params) {
                    AppRestrictionsFragment.this.mHelper.applyUserAppsStates(AppRestrictionsFragment.this);
                    return null;
                }
            }.execute(new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public void onPackageChanged(Intent intent) {
        String action = intent.getAction();
        AppRestrictionsPreference pref = (AppRestrictionsPreference) findPreference(getKeyForPackage(intent.getData().getSchemeSpecificPart()));
        if (pref != null) {
            if (("android.intent.action.PACKAGE_ADDED".equals(action) && pref.isChecked()) || ("android.intent.action.PACKAGE_REMOVED".equals(action) && !pref.isChecked())) {
                pref.setEnabled(true);
            }
        }
    }

    private PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }

    public void onDisableUiForPackage(String packageName) {
        AppRestrictionsPreference pref = (AppRestrictionsPreference) findPreference(getKeyForPackage(packageName));
        if (pref != null) {
            pref.setEnabled(false);
        }
    }

    private class AppLoadingTask extends AsyncTask<Void, Void, Void> {
        private AppLoadingTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            AppRestrictionsFragment.this.mHelper.fetchAndMergeApps();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            AppRestrictionsFragment.this.populateApps();
        }
    }

    private boolean isPlatformSigned(PackageInfo pi) {
        if (pi == null || pi.signatures == null || !this.mSysPackageInfo.signatures[0].equals(pi.signatures[0])) {
            return false;
        }
        return true;
    }

    private boolean isAppEnabledForUser(PackageInfo pi) {
        if (pi == null) {
            return false;
        }
        int flags = pi.applicationInfo.flags;
        int privateFlags = pi.applicationInfo.privateFlags;
        if ((8388608 & flags) == 0 || (privateFlags & 1) != 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void populateApps() {
        PackageInfo pi;
        Context context = getActivity();
        if (context != null) {
            PackageManager pm = this.mPackageManager;
            int userId = this.mUser.getIdentifier();
            if (getExistingUser(this.mUserManager, this.mUser) != null) {
                this.mAppList.removeAll();
                addLocationAppRestrictionsPreference();
                List<ResolveInfo> receivers = pm.queryBroadcastReceivers(new Intent("android.intent.action.GET_RESTRICTION_ENTRIES"), 0);
                for (AppRestrictionsHelper.SelectableAppInfo app : this.mHelper.getVisibleApps()) {
                    String packageName = app.packageName;
                    if (packageName != null) {
                        boolean isSettingsApp = packageName.equals(context.getPackageName());
                        AppRestrictionsPreference p = new AppRestrictionsPreference(getPreferenceManager().getContext());
                        boolean hasSettings = resolveInfoListHasPackage(receivers, packageName);
                        if (isSettingsApp) {
                            this.mHelper.setPackageSelected(packageName, true);
                        } else {
                            try {
                                pi = this.mIPm.getPackageInfo(packageName, 4194368, userId);
                            } catch (RemoteException e) {
                                pi = null;
                            }
                            if (pi != null && (!this.mRestrictedProfile || !isAppUnsupportedInRestrictedProfile(pi))) {
                                p.setIcon(app.icon != null ? app.icon.mutate() : null);
                                p.setChecked(false);
                                p.setTitle(app.activityName);
                                p.setKey(getKeyForPackage(packageName));
                                p.setPersistent(false);
                                p.setOnPreferenceChangeListener(this);
                                p.setSummary((CharSequence) getPackageSummary(pi, app));
                                if (pi.requiredForAllUsers || isPlatformSigned(pi)) {
                                    p.setChecked(true);
                                    p.setImmutable(true);
                                    if (!hasSettings) {
                                    }
                                } else if (!this.mNewUser && isAppEnabledForUser(pi)) {
                                    p.setChecked(true);
                                }
                                if (app.masterEntry == null && hasSettings) {
                                    requestRestrictionsForApp(packageName, p);
                                }
                                if (app.masterEntry != null) {
                                    p.setImmutable(true);
                                    p.setChecked(this.mHelper.isPackageSelected(packageName));
                                }
                                p.setOrder(100 * (this.mAppList.getPreferenceCount() + 2));
                                this.mHelper.setPackageSelected(packageName, p.isChecked());
                                this.mAppList.addPreference(p);
                            }
                        }
                    }
                }
                this.mAppListChanged = true;
                if (this.mNewUser && this.mFirstTime) {
                    this.mFirstTime = false;
                    this.mHelper.applyUserAppsStates(this);
                }
            }
        }
    }

    private String getPackageSummary(PackageInfo pi, AppRestrictionsHelper.SelectableAppInfo app) {
        if (app.masterEntry != null) {
            if (!this.mRestrictedProfile || pi.restrictedAccountType == null) {
                return getString(R.string.user_restrictions_controlled_by, new Object[]{app.masterEntry.activityName});
            }
            return getString(R.string.app_sees_restricted_accounts_and_controlled_by, new Object[]{app.masterEntry.activityName});
        } else if (pi.restrictedAccountType != null) {
            return getString(R.string.app_sees_restricted_accounts);
        } else {
            return null;
        }
    }

    private static boolean isAppUnsupportedInRestrictedProfile(PackageInfo pi) {
        return pi.requiredAccountType != null && pi.restrictedAccountType == null;
    }

    private void addLocationAppRestrictionsPreference() {
        AppRestrictionsPreference p = new AppRestrictionsPreference(getPreferenceManager().getContext());
        String packageName = getContext().getPackageName();
        p.setIcon((int) R.drawable.ic_location_on);
        p.setKey(getKeyForPackage(packageName));
        ArrayList<RestrictionEntry> restrictions = RestrictionUtils.getRestrictions(getActivity(), this.mUser);
        RestrictionEntry locationRestriction = restrictions.get(0);
        p.setTitle((CharSequence) locationRestriction.getTitle());
        p.setRestrictions(restrictions);
        p.setSummary((CharSequence) locationRestriction.getDescription());
        p.setChecked(locationRestriction.getSelectedState());
        p.setPersistent(false);
        p.setOrder(100);
        this.mAppList.addPreference(p);
    }

    private String getKeyForPackage(String packageName) {
        return PKG_PREFIX + packageName;
    }

    /* access modifiers changed from: private */
    public String getKeyForPackageActivity(String packageName) {
        return ACTIVITY_PREFIX + packageName;
    }

    private String getPackageFromKey(String key) {
        if (key.startsWith(PKG_PREFIX)) {
            return key.substring(PKG_PREFIX.length());
        }
        if (key.startsWith(ACTIVITY_PREFIX)) {
            return key.substring(ACTIVITY_PREFIX.length());
        }
        throw new IllegalArgumentException("Tried to extract package from wrong key: " + key);
    }

    private boolean resolveInfoListHasPackage(List<ResolveInfo> receivers, String packageName) {
        for (ResolveInfo info : receivers) {
            if (info.activityInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void updateAllEntries(String prefKey, boolean checked) {
        for (int i = 0; i < this.mAppList.getPreferenceCount(); i++) {
            Preference pref = this.mAppList.getPreference(i);
            if ((pref instanceof AppRestrictionsPreference) && prefKey.equals(pref.getKey())) {
                ((AppRestrictionsPreference) pref).setChecked(checked);
            }
        }
    }

    private void assertSafeToStartCustomActivity(Intent intent, String packageName) {
        if (intent.getPackage() == null || !intent.getPackage().equals(packageName)) {
            List<ResolveInfo> resolveInfos = this.mPackageManager.queryIntentActivities(intent, 0);
            if (resolveInfos.size() == 1 && !packageName.equals(resolveInfos.get(0).activityInfo.packageName)) {
                throw new SecurityException("Application " + packageName + " is not allowed to start activity " + intent);
            }
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof AppRestrictionsPreference) {
            AppRestrictionsPreference pref = (AppRestrictionsPreference) preference;
            if (!pref.isImmutable()) {
                pref.setChecked(!pref.isChecked());
                String packageName = getPackageFromKey(pref.getKey());
                if (packageName.equals(getActivity().getPackageName())) {
                    pref.getRestrictions().get(0).setSelectedState(pref.isChecked());
                    RestrictionUtils.setRestrictions(getActivity(), pref.getRestrictions(), this.mUser);
                    return true;
                }
                this.mHelper.setPackageSelected(packageName, pref.isChecked());
                this.mAppListChanged = true;
                if (!this.mRestrictedProfile) {
                    this.mHelper.applyUserAppState(packageName, pref.isChecked(), this);
                }
                updateAllEntries(pref.getKey(), pref.isChecked());
            }
            return true;
        } else if (preference.getIntent() == null) {
            return super.onPreferenceTreeClick(preference);
        } else {
            assertSafeToStartCustomActivity(preference.getIntent(), getPackageFromKey(preference.getKey()));
            try {
                startActivityForResult(preference.getIntent(), generateCustomActivityRequestCode(preference));
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Activity not found", e);
            }
            return true;
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key == null || !key.contains(DELIMITER)) {
            return true;
        }
        StringTokenizer st = new StringTokenizer(key, DELIMITER);
        String packageName = st.nextToken();
        String restrictionKey = st.nextToken();
        ArrayList<RestrictionEntry> restrictions = ((AppRestrictionsPreference) this.mAppList.findPreference(getKeyForPackage(packageName))).getRestrictions();
        if (restrictions == null) {
            return true;
        }
        Iterator<RestrictionEntry> it = restrictions.iterator();
        while (it.hasNext()) {
            RestrictionEntry entry = it.next();
            if (entry.getKey().equals(restrictionKey)) {
                switch (entry.getType()) {
                    case 1:
                        entry.setSelectedState(((Boolean) newValue).booleanValue());
                        break;
                    case 2:
                    case 3:
                        entry.setSelectedString((String) newValue);
                        ((ListPreference) preference).setSummary(findInArray(entry.getChoiceEntries(), entry.getChoiceValues(), (String) newValue));
                        break;
                    case 4:
                        Set<String> set = (Set) newValue;
                        String[] selectedValues = new String[set.size()];
                        set.toArray(selectedValues);
                        entry.setAllSelectedStrings(selectedValues);
                        break;
                }
                this.mUserManager.setApplicationRestrictions(packageName, RestrictionsManager.convertRestrictionsToBundle(restrictions), this.mUser);
                return true;
            }
        }
        return true;
    }

    private void requestRestrictionsForApp(String packageName, AppRestrictionsPreference preference) {
        Bundle oldEntries = this.mUserManager.getApplicationRestrictions(packageName, this.mUser);
        Intent intent = new Intent("android.intent.action.GET_RESTRICTION_ENTRIES");
        intent.setPackage(packageName);
        intent.putExtra("android.intent.extra.restrictions_bundle", oldEntries);
        intent.addFlags(32);
        getActivity().sendOrderedBroadcast(intent, (String) null, new RestrictionsResultReceiver(packageName, preference), (Handler) null, -1, (String) null, (Bundle) null);
    }

    private class RestrictionsResultReceiver extends BroadcastReceiver {
        private static final String CUSTOM_RESTRICTIONS_INTENT = "android.intent.extra.restrictions_intent";
        private final String mPackageName;
        private final AppRestrictionsPreference mPreference;

        RestrictionsResultReceiver(String packageName, AppRestrictionsPreference preference) {
            this.mPackageName = packageName;
            this.mPreference = preference;
        }

        public void onReceive(Context context, Intent intent) {
            Bundle results = getResultExtras(true);
            ArrayList<RestrictionEntry> restrictions = results != null ? results.getParcelableArrayList("android.intent.extra.restrictions_list") : null;
            Intent restrictionsIntent = results != null ? (Intent) results.getParcelable(CUSTOM_RESTRICTIONS_INTENT) : null;
            if (restrictions != null && restrictionsIntent == null) {
                AppRestrictionsFragment.this.onRestrictionsReceived(this.mPreference, restrictions);
                if (AppRestrictionsFragment.this.mRestrictedProfile) {
                    AppRestrictionsFragment.this.mUserManager.setApplicationRestrictions(this.mPackageName, RestrictionsManager.convertRestrictionsToBundle(restrictions), AppRestrictionsFragment.this.mUser);
                }
            } else if (restrictionsIntent != null) {
                this.mPreference.setRestrictions((ArrayList<RestrictionEntry>) null);
                this.mPreference.removeAll();
                Preference p = new Preference(this.mPreference.getContext());
                p.setKey(AppRestrictionsFragment.this.getKeyForPackageActivity(this.mPackageName));
                p.setIcon(AppRestrictionsFragment.BLANK_DRAWABLE);
                p.setTitle((int) R.string.restricted_profile_customize_restrictions);
                p.setIntent(restrictionsIntent);
                this.mPreference.addPreference(p);
            } else {
                String access$1200 = AppRestrictionsFragment.TAG;
                Log.e(access$1200, "No restrictions returned from " + this.mPackageName);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRestrictionsReceived(AppRestrictionsPreference preference, ArrayList<RestrictionEntry> restrictions) {
        preference.removeAll();
        int count = 1;
        Context themedContext = getPreferenceManager().getContext();
        Iterator<RestrictionEntry> it = restrictions.iterator();
        while (it.hasNext()) {
            RestrictionEntry entry = it.next();
            Preference p = null;
            switch (entry.getType()) {
                case 1:
                    p = new SwitchPreference(themedContext);
                    p.setTitle((CharSequence) entry.getTitle());
                    p.setSummary((CharSequence) entry.getDescription());
                    ((SwitchPreference) p).setChecked(entry.getSelectedState());
                    break;
                case 2:
                case 3:
                    p = new ListPreference(themedContext);
                    p.setTitle((CharSequence) entry.getTitle());
                    String value = entry.getSelectedString();
                    if (value == null) {
                        value = entry.getDescription();
                    }
                    p.setSummary((CharSequence) findInArray(entry.getChoiceEntries(), entry.getChoiceValues(), value));
                    ((ListPreference) p).setEntryValues((CharSequence[]) entry.getChoiceValues());
                    ((ListPreference) p).setEntries((CharSequence[]) entry.getChoiceEntries());
                    ((ListPreference) p).setValue(value);
                    ((ListPreference) p).setDialogTitle((CharSequence) entry.getTitle());
                    break;
                case 4:
                    p = new MultiSelectListPreference(themedContext);
                    p.setTitle((CharSequence) entry.getTitle());
                    ((MultiSelectListPreference) p).setEntryValues((CharSequence[]) entry.getChoiceValues());
                    ((MultiSelectListPreference) p).setEntries((CharSequence[]) entry.getChoiceEntries());
                    HashSet<String> set = new HashSet<>();
                    Collections.addAll(set, entry.getAllSelectedStrings());
                    ((MultiSelectListPreference) p).setValues(set);
                    ((MultiSelectListPreference) p).setDialogTitle((CharSequence) entry.getTitle());
                    break;
            }
            if (p != null) {
                p.setPersistent(false);
                p.setOrder(preference.getOrder() + count);
                p.setKey(getPackageFromKey(preference.getKey()) + DELIMITER + entry.getKey());
                preference.addPreference(p);
                p.setOnPreferenceChangeListener(this);
                p.setIcon(BLANK_DRAWABLE);
                count++;
            }
        }
        preference.setRestrictions(restrictions);
        if (count == 1 && preference.isImmutable() && preference.isChecked()) {
            this.mAppList.removePreference(preference);
        }
    }

    private int generateCustomActivityRequestCode(Preference preference) {
        this.mCustomRequestCode++;
        this.mCustomRequestMap.put(Integer.valueOf(this.mCustomRequestCode), getKeyForPackage(getPackageFromKey(preference.getKey())));
        return this.mCustomRequestCode;
    }

    /* JADX WARNING: type inference failed for: r2v6, types: [android.support.v7.preference.Preference] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r9, int r10, android.content.Intent r11) {
        /*
            r8 = this;
            super.onActivityResult(r9, r10, r11)
            java.util.Map<java.lang.Integer, java.lang.String> r0 = r8.mCustomRequestMap
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            r1 = 0
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x001d
            android.support.v7.preference.Preference r2 = r8.findPreference(r0)
            r1 = r2
            com.android.tv.settings.users.AppRestrictionsFragment$AppRestrictionsPreference r1 = (com.android.tv.settings.users.AppRestrictionsFragment.AppRestrictionsPreference) r1
        L_0x001d:
            if (r1 != 0) goto L_0x0036
            java.lang.String r2 = TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unknown requestCode "
            r3.append(r4)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            android.util.Log.w(r2, r3)
            return
        L_0x0036:
            r2 = -1
            if (r10 != r2) goto L_0x0067
            java.lang.String r2 = r1.getKey()
            java.lang.String r2 = r8.getPackageFromKey(r2)
            java.lang.String r3 = "android.intent.extra.restrictions_list"
            java.util.ArrayList r3 = r11.getParcelableArrayListExtra(r3)
            java.lang.String r4 = "android.intent.extra.restrictions_bundle"
            android.os.Bundle r4 = r11.getBundleExtra(r4)
            if (r3 == 0) goto L_0x005e
            r1.setRestrictions(r3)
            android.os.UserManager r5 = r8.mUserManager
            android.os.Bundle r6 = android.content.RestrictionsManager.convertRestrictionsToBundle(r3)
            android.os.UserHandle r7 = r8.mUser
            r5.setApplicationRestrictions(r2, r6, r7)
            goto L_0x0067
        L_0x005e:
            if (r4 == 0) goto L_0x0067
            android.os.UserManager r5 = r8.mUserManager
            android.os.UserHandle r6 = r8.mUser
            r5.setApplicationRestrictions(r2, r4, r6)
        L_0x0067:
            java.util.Map<java.lang.Integer, java.lang.String> r2 = r8.mCustomRequestMap
            java.lang.Integer r3 = java.lang.Integer.valueOf(r9)
            r2.remove(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.users.AppRestrictionsFragment.onActivityResult(int, int, android.content.Intent):void");
    }

    private String findInArray(String[] choiceEntries, String[] choiceValues, String selectedString) {
        for (int i = 0; i < choiceValues.length; i++) {
            if (choiceValues[i].equals(selectedString)) {
                return choiceEntries[i];
            }
        }
        return selectedString;
    }

    private static UserInfo getExistingUser(UserManager userManager, UserHandle checkUser) {
        List<UserInfo> users = userManager.getUsers(true);
        int checkUserId = checkUser.getIdentifier();
        for (UserInfo user : users) {
            if (user.id == checkUserId) {
                return user;
            }
        }
        return null;
    }

    public int getMetricsCategory() {
        return 97;
    }
}
