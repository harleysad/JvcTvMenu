package com.android.tv.settings.about;

import android.content.Context;
import android.support.annotation.Keep;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractSimStatusImeiInfoPreferenceController;
import com.android.tv.settings.NopePreferenceController;
import com.android.tv.settings.PreferenceControllerFragment;
import com.android.tv.settings.R;
import java.util.ArrayList;
import java.util.List;

@Keep
public class StatusFragment extends PreferenceControllerFragment {
    private static final String KEY_BATTERY_LEVEL = "battery_level";
    private static final String KEY_BATTERY_STATUS = "battery_status";
    private static final String KEY_IMEI_INFO = "imei_info";
    private static final String KEY_SIM_STATUS = "sim_status";

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    public int getMetricsCategory() {
        return 44;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return R.xml.device_info_status;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> onCreatePreferenceControllers(Context context) {
        List<AbstractPreferenceController> controllers = new ArrayList<>(10);
        Lifecycle lifecycle = getLifecycle();
        controllers.add(new NopePreferenceController(context, KEY_BATTERY_LEVEL));
        controllers.add(new NopePreferenceController(context, KEY_BATTERY_STATUS));
        controllers.add(new SerialNumberPreferenceController(context));
        controllers.add(new UptimePreferenceController(context, lifecycle));
        controllers.add(new BluetoothAddressPreferenceController(context, lifecycle));
        controllers.add(new IpAddressPreferenceController(context, lifecycle));
        controllers.add(new WifiMacAddressPreferenceController(context, lifecycle));
        controllers.add(new ImsStatusPreferenceController(context, lifecycle));
        controllers.add(new AdminUserAndPhoneOnlyPreferenceController(context, KEY_SIM_STATUS));
        controllers.add(new AdminUserAndPhoneOnlyPreferenceController(context, KEY_IMEI_INFO));
        return controllers;
    }

    private static class AdminUserAndPhoneOnlyPreferenceController extends AbstractSimStatusImeiInfoPreferenceController {
        private final String mKey;

        private AdminUserAndPhoneOnlyPreferenceController(Context context, String key) {
            super(context);
            this.mKey = key;
        }

        public String getPreferenceKey() {
            return this.mKey;
        }
    }
}
