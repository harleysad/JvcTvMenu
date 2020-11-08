package com.android.tv.settings.device.apps.specialaccess;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.IconDrawableFactory;
import android.util.Log;
import com.android.settingslib.applications.ServiceListing;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.List;

@Keep
public class NotificationAccess extends SettingsPreferenceFragment {
    private static final String HEADER_KEY = "header";
    private static final String TAG = "NotificationAccess";
    private IconDrawableFactory mIconDrawableFactory;
    private NotificationManager mNotificationManager;
    private PackageManager mPackageManager;
    private ServiceListing mServiceListing;

    public int getMetricsCategory() {
        return 179;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPackageManager = context.getPackageManager();
        this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mServiceListing = new ServiceListing.Builder(getContext()).setTag(TAG).setSetting("enabled_notification_listeners").setIntentAction("android.service.notification.NotificationListenerService").setPermission("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE").setNoun("notification listener").build();
        this.mServiceListing.addCallback(new ServiceListing.Callback() {
            public final void onServicesReloaded(List list) {
                NotificationAccess.this.updateList(list);
            }
        });
    }

    public void onResume() {
        super.onResume();
        this.mServiceListing.reload();
        this.mServiceListing.setListening(true);
    }

    public void onPause() {
        super.onPause();
        this.mServiceListing.setListening(false);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_access, (String) null);
    }

    /* access modifiers changed from: private */
    public void updateList(List<ServiceInfo> services) {
        PreferenceScreen screen = getPreferenceScreen();
        Preference header = screen.findPreference("header");
        screen.removeAll();
        if (header != null) {
            screen.addPreference(header);
        }
        services.sort(new PackageItemInfo.DisplayNameComparator(this.mPackageManager));
        for (ServiceInfo service : services) {
            ComponentName cn = new ComponentName(service.packageName, service.name);
            CharSequence title = null;
            try {
                title = this.mPackageManager.getApplicationInfo(service.packageName, 0).loadLabel(this.mPackageManager);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "can't find package name", e);
            }
            String summary = service.loadLabel(this.mPackageManager).toString();
            SwitchPreference pref = new SwitchPreference(getPreferenceManager().getContext());
            pref.setPersistent(false);
            pref.setIcon(this.mIconDrawableFactory.getBadgedIcon(service, service.applicationInfo, UserHandle.getUserId(service.applicationInfo.uid)));
            if (title == null || title.equals(summary)) {
                pref.setTitle((CharSequence) summary);
            } else {
                pref.setTitle(title);
                pref.setSummary((CharSequence) summary);
            }
            pref.setKey(cn.flattenToString());
            pref.setChecked(this.mNotificationManager.isNotificationListenerAccessGranted(cn));
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(cn) {
                private final /* synthetic */ ComponentName f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return NotificationAccess.this.mNotificationManager.setNotificationListenerAccessGranted(this.f$1, ((Boolean) obj).booleanValue());
                }
            });
            screen.addPreference(pref);
        }
        if (services.isEmpty()) {
            new Preference(getPreferenceManager().getContext()).setTitle((int) R.string.no_notification_listeners);
        }
    }
}
