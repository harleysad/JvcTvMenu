package com.android.tv.settings.partnercustomizer.picture;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;

public class MJCFragment extends LeanbackPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "AdvanceVideoFragment";
    private ListPreference AdvanceVideoMJCDemoPartitionPref;
    private DialogPreference AdvanceVideoMJCDemoPref;
    private ListPreference AdvanceVideoMJCEffectPref;
    private PreferenceManager mManager;
    private MenuMjcDemoDialog mjcDialog;

    public static MJCFragment newInstance() {
        return new MJCFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_mjc, (String) null);
        this.mManager = getPreferenceManager();
        this.mjcDialog = new MenuMjcDemoDialog(this.mManager.getContext());
        findAllPreferences();
        doItemInit();
    }

    private void findAllPreferences() {
        this.AdvanceVideoMJCEffectPref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT);
        this.AdvanceVideoMJCDemoPartitionPref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION);
        this.AdvanceVideoMJCDemoPref = (DialogPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO);
    }

    private void doItemInit() {
        this.AdvanceVideoMJCDemoPref.setDialog(this.mjcDialog);
        PreferenceUtils.setupListPreference(this, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT);
        PreferenceUtils.setupListPreference(this, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION, PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION);
        if (this.AdvanceVideoMJCEffectPref.getValue() == null || !this.AdvanceVideoMJCEffectPref.getValue().equals("0")) {
            this.AdvanceVideoMJCDemoPartitionPref.setEnabled(true);
            this.AdvanceVideoMJCDemoPref.setEnabled(true);
            return;
        }
        this.AdvanceVideoMJCDemoPartitionPref.setEnabled(false);
        this.AdvanceVideoMJCDemoPref.setEnabled(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0083  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(android.support.v7.preference.Preference r9, java.lang.Object r10) {
        /*
            r8 = this;
            android.content.Context r0 = r8.getContext()
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = r9.getKey()
            java.lang.String r2 = "AdvanceVideoFragment"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "onPreferenceChange preference "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            android.util.Log.e(r2, r3)
            r2 = 1
            int r3 = r1.hashCode()
            r4 = -348804343(0xffffffffeb35ab09, float:-2.1962326E26)
            r5 = 0
            r6 = 1
            if (r3 == r4) goto L_0x003e
            r4 = 1863726121(0x6f163429, float:4.648581E28)
            if (r3 == r4) goto L_0x0034
            goto L_0x0048
        L_0x0034:
            java.lang.String r3 = "tv_picture_advance_video_mjc_effect"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0048
            r3 = r5
            goto L_0x0049
        L_0x003e:
            java.lang.String r3 = "tv_picture_video_mjc_demo_partition"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0048
            r3 = r6
            goto L_0x0049
        L_0x0048:
            r3 = -1
        L_0x0049:
            switch(r3) {
                case 0: goto L_0x0056;
                case 1: goto L_0x004d;
                default: goto L_0x004c;
            }
        L_0x004c:
            goto L_0x0081
        L_0x004d:
            java.lang.String r3 = "tv_picture_video_mjc_demo_partition"
            r4 = r10
            java.lang.String r4 = (java.lang.String) r4
            android.provider.Settings.Global.putString(r0, r3, r4)
            goto L_0x0081
        L_0x0056:
            java.lang.String r3 = "tv_picture_advance_video_mjc_effect"
            r4 = r10
            java.lang.String r4 = (java.lang.String) r4
            android.provider.Settings.Global.putString(r0, r3, r4)
            if (r10 == 0) goto L_0x0076
            r3 = r10
            java.lang.String r3 = (java.lang.String) r3
            java.lang.String r4 = "0"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0076
            android.support.v7.preference.ListPreference r3 = r8.AdvanceVideoMJCDemoPartitionPref
            r3.setEnabled(r5)
            com.android.tv.settings.partnercustomizer.picture.DialogPreference r3 = r8.AdvanceVideoMJCDemoPref
            r3.setEnabled(r5)
            goto L_0x0081
        L_0x0076:
            android.support.v7.preference.ListPreference r3 = r8.AdvanceVideoMJCDemoPartitionPref
            r3.setEnabled(r6)
            com.android.tv.settings.partnercustomizer.picture.DialogPreference r3 = r8.AdvanceVideoMJCDemoPref
            r3.setEnabled(r6)
        L_0x0081:
            if (r2 == 0) goto L_0x00a8
            android.support.v7.preference.Preference r3 = r8.findPreference(r1)
            android.support.v7.preference.ListPreference r3 = (android.support.v7.preference.ListPreference) r3
            if (r3 == 0) goto L_0x0092
            r4 = r10
            java.lang.String r4 = (java.lang.String) r4
            r3.setValue(r4)
            goto L_0x00a8
        L_0x0092:
            java.lang.String r4 = "AdvanceVideoFragment"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Cannot find ListPreference for "
            r5.append(r7)
            r5.append(r1)
            java.lang.String r5 = r5.toString()
            android.util.Log.e(r4, r5)
        L_0x00a8:
            r8.updateAllPreferences()
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.picture.MJCFragment.onPreferenceChange(android.support.v7.preference.Preference, java.lang.Object):boolean");
    }

    private void updateAllPreferences() {
        Log.d("AdvanceVideoFragment", "updateAllPreferences ~ ");
        this.AdvanceVideoMJCEffectPref.setValue(Settings.Global.getString(getContext().getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT));
        this.AdvanceVideoMJCDemoPartitionPref.setValue(Settings.Global.getString(getContext().getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_DEMO_PARTITION));
    }

    private void updateOptionsEnableStated() {
    }
}
