package com.android.tv.settings.partnercustomizer.inputs;

import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiTvClient;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import java.util.List;

public class CecDeviceListFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String KEY_CEC_DEV_LIST = "cec_dev_list";
    private static final String TAG = "CecDeviceListFragment";
    private static CecDeviceListFragment cdlf;
    private PreferenceConfigUtils mConfigUtils;
    private HdmiControlManager mHdmiManager;
    private HdmiTvClient mHdmiTvClient;

    public static CecDeviceListFragment newInstance() {
        if (cdlf == null) {
            cdlf = new CecDeviceListFragment();
        }
        return cdlf;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "onCreatePreferences");
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        this.mConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        this.mHdmiManager = (HdmiControlManager) getContext().getSystemService(HdmiControlManager.class);
        Log.d(TAG, "mHdmiManager==" + this.mHdmiManager);
        if (this.mHdmiManager != null) {
            synchronized (this.mHdmiManager) {
                this.mHdmiTvClient = this.mHdmiManager.getTvClient();
            }
        }
        createPreferences();
    }

    private void createPreferences() {
        Log.d(TAG, "createPreferences");
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.s_cec_dev_list);
        if (this.mHdmiTvClient == null) {
            Log.d(TAG, "null == mHdmiTvClient");
            mScreen.addPreference(this.mConfigUtils.createPreference((LeanbackPreferenceFragment) this, "no_cec_device", (int) R.string.s_empty_cec_dev, (String) null));
            return;
        }
        List<HdmiDeviceInfo> devList = this.mHdmiTvClient.getDeviceList();
        if (!devList.isEmpty()) {
            for (HdmiDeviceInfo devInfo : devList) {
                Log.d(TAG, "CEC device list print: " + devInfo.toString());
                String hdmi_port = getContext().getString(R.string.s_cec_dev_port, new Object[]{Integer.valueOf(devInfo.getPortId())});
                String devName = new String(hdmi_port + ": " + devInfo.getDisplayName());
                mScreen.addPreference(this.mConfigUtils.createPreference((LeanbackPreferenceFragment) this, devName, devName, (String) null));
            }
            return;
        }
        mScreen.addPreference(this.mConfigUtils.createPreference((LeanbackPreferenceFragment) this, "no_cec_device", (int) R.string.s_empty_cec_dev, (String) null));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        return true;
    }

    public int getMetricsCategory() {
        return 336;
    }
}
