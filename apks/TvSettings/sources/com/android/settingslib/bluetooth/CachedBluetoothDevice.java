package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import com.android.settingslib.R;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice> {
    public static final int ACCESS_ALLOWED = 1;
    public static final int ACCESS_REJECTED = 2;
    public static final int ACCESS_UNKNOWN = 0;
    private static final boolean DEBUG = false;
    private static final long MAX_HOGP_DELAY_FOR_AUTO_CONNECT = 30000;
    private static final long MAX_UUID_DELAY_FOR_AUTO_CONNECT = 5000;
    private static final int MESSAGE_REJECTION_COUNT_LIMIT_TO_PERSIST = 2;
    private static final String MESSAGE_REJECTION_COUNT_PREFS_NAME = "bluetooth_message_reject";
    private static final String TAG = "CachedBluetoothDevice";
    private final AudioManager mAudioManager;
    private BluetoothClass mBtClass;
    private final Collection<Callback> mCallbacks = new ArrayList();
    private long mConnectAttempted;
    private final Context mContext;
    private final BluetoothDevice mDevice;
    private long mHiSyncId;
    private boolean mIsActiveDeviceA2dp = false;
    private boolean mIsActiveDeviceHeadset = false;
    private boolean mIsActiveDeviceHearingAid = false;
    private boolean mIsConnectingErrorPossible;
    private boolean mJustDiscovered;
    private final LocalBluetoothAdapter mLocalAdapter;
    private boolean mLocalNapRoleConnected;
    private int mMessageRejectionCount;
    private String mName;
    private HashMap<LocalBluetoothProfile, Integer> mProfileConnectionState;
    private final LocalBluetoothProfileManager mProfileManager;
    private final List<LocalBluetoothProfile> mProfiles = new ArrayList();
    private final List<LocalBluetoothProfile> mRemovedProfiles = new ArrayList();
    private short mRssi;

    public interface Callback {
        void onDeviceAttributesChanged();
    }

    public long getHiSyncId() {
        return this.mHiSyncId;
    }

    public void setHiSyncId(long id) {
        Log.d(TAG, "setHiSyncId: mDevice " + this.mDevice + ", id " + id);
        this.mHiSyncId = id;
    }

    private String describe(LocalBluetoothProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:");
        sb.append(this.mDevice);
        if (profile != null) {
            sb.append(" Profile:");
            sb.append(profile);
        }
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public void onProfileStateChanged(LocalBluetoothProfile profile, int newProfileState) {
        Log.d(TAG, "onProfileStateChanged: profile " + profile + ", device=" + this.mDevice + ", newProfileState " + newProfileState);
        if (this.mLocalAdapter.getBluetoothState() == 13) {
            Log.d(TAG, " BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        this.mProfileConnectionState.put(profile, Integer.valueOf(newProfileState));
        if (newProfileState == 2) {
            if (profile instanceof MapProfile) {
                profile.setPreferred(this.mDevice, true);
            }
            if (!this.mProfiles.contains(profile)) {
                this.mRemovedProfiles.remove(profile);
                this.mProfiles.add(profile);
                if ((profile instanceof PanProfile) && ((PanProfile) profile).isLocalRoleNap(this.mDevice)) {
                    this.mLocalNapRoleConnected = true;
                }
            }
        } else if ((profile instanceof MapProfile) && newProfileState == 0) {
            profile.setPreferred(this.mDevice, false);
        } else if (this.mLocalNapRoleConnected && (profile instanceof PanProfile) && ((PanProfile) profile).isLocalRoleNap(this.mDevice) && newProfileState == 0) {
            Log.d(TAG, "Removing PanProfile from device after NAP disconnect");
            this.mProfiles.remove(profile);
            this.mRemovedProfiles.add(profile);
            this.mLocalNapRoleConnected = false;
        }
        fetchActiveDevices();
    }

    CachedBluetoothDevice(Context context, LocalBluetoothAdapter adapter, LocalBluetoothProfileManager profileManager, BluetoothDevice device) {
        this.mContext = context;
        this.mLocalAdapter = adapter;
        this.mProfileManager = profileManager;
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
        this.mDevice = device;
        this.mProfileConnectionState = new HashMap<>();
        fillData();
        this.mHiSyncId = 0;
    }

    public void disconnect() {
        for (LocalBluetoothProfile profile : this.mProfiles) {
            disconnect(profile);
        }
        PbapServerProfile PbapProfile = this.mProfileManager.getPbapProfile();
        if (PbapProfile.getConnectionStatus(this.mDevice) == 2) {
            PbapProfile.disconnect(this.mDevice);
        }
    }

    public void disconnect(LocalBluetoothProfile profile) {
        if (profile.disconnect(this.mDevice)) {
            Log.d(TAG, "Command sent successfully:DISCONNECT " + describe(profile));
        }
    }

    public void connect(boolean connectAllProfiles) {
        if (ensurePaired()) {
            this.mConnectAttempted = SystemClock.elapsedRealtime();
            connectWithoutResettingTimer(connectAllProfiles);
        }
    }

    public boolean isHearingAidDevice() {
        return this.mHiSyncId != 0;
    }

    /* access modifiers changed from: package-private */
    public void onBondingDockConnect() {
        connect(false);
    }

    private void connectWithoutResettingTimer(boolean connectAllProfiles) {
        if (this.mProfiles.isEmpty()) {
            Log.d(TAG, "No profiles. Maybe we will connect later");
            return;
        }
        this.mIsConnectingErrorPossible = true;
        int preferredProfiles = 0;
        for (LocalBluetoothProfile profile : this.mProfiles) {
            if (connectAllProfiles) {
                if (!profile.isConnectable()) {
                }
            } else if (!profile.isAutoConnectable()) {
            }
            if (profile.isPreferred(this.mDevice)) {
                preferredProfiles++;
                connectInt(profile);
            }
        }
        if (preferredProfiles == 0) {
            connectAutoConnectableProfiles();
        }
    }

    private void connectAutoConnectableProfiles() {
        if (ensurePaired()) {
            this.mIsConnectingErrorPossible = true;
            for (LocalBluetoothProfile profile : this.mProfiles) {
                if (profile.isAutoConnectable()) {
                    profile.setPreferred(this.mDevice, true);
                    connectInt(profile);
                }
            }
        }
    }

    public void connectProfile(LocalBluetoothProfile profile) {
        this.mConnectAttempted = SystemClock.elapsedRealtime();
        this.mIsConnectingErrorPossible = true;
        connectInt(profile);
        refresh();
    }

    /* access modifiers changed from: package-private */
    public synchronized void connectInt(LocalBluetoothProfile profile) {
        if (ensurePaired()) {
            if (profile.connect(this.mDevice)) {
                Log.d(TAG, "Command sent successfully:CONNECT " + describe(profile));
                return;
            }
            Log.i(TAG, "Failed to connect " + profile.toString() + " to " + this.mName);
        }
    }

    private boolean ensurePaired() {
        if (getBondState() != 10) {
            return true;
        }
        startPairing();
        return false;
    }

    public boolean startPairing() {
        if (this.mLocalAdapter.isDiscovering()) {
            this.mLocalAdapter.cancelDiscovery();
        }
        if (!this.mDevice.createBond()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isUserInitiatedPairing() {
        return this.mDevice.isBondingInitiatedLocally();
    }

    public void unpair() {
        BluetoothDevice dev;
        int state = getBondState();
        if (state == 11) {
            this.mDevice.cancelBondProcess();
        }
        if (state != 10 && (dev = this.mDevice) != null && dev.removeBond()) {
            Log.d(TAG, "Command sent successfully:REMOVE_BOND " + describe((LocalBluetoothProfile) null));
        }
    }

    public int getProfileConnectionState(LocalBluetoothProfile profile) {
        if (this.mProfileConnectionState.get(profile) == null) {
            this.mProfileConnectionState.put(profile, Integer.valueOf(profile.getConnectionStatus(this.mDevice)));
        }
        return this.mProfileConnectionState.get(profile).intValue();
    }

    public void clearProfileConnectionState() {
        Log.d(TAG, " Clearing all connection state for dev:" + this.mDevice.getName());
        for (LocalBluetoothProfile profile : getProfiles()) {
            this.mProfileConnectionState.put(profile, 0);
        }
    }

    private void fillData() {
        fetchName();
        fetchBtClass();
        updateProfiles();
        fetchActiveDevices();
        migratePhonebookPermissionChoice();
        migrateMessagePermissionChoice();
        fetchMessageRejectionCount();
        dispatchAttributesChanged();
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public String getAddress() {
        return this.mDevice.getAddress();
    }

    public String getName() {
        return this.mName;
    }

    /* access modifiers changed from: package-private */
    public void setNewName(String name) {
        if (this.mName == null) {
            this.mName = name;
            if (this.mName == null || TextUtils.isEmpty(this.mName)) {
                this.mName = this.mDevice.getAddress();
            }
            dispatchAttributesChanged();
        }
    }

    public void setName(String name) {
        if (name != null && !TextUtils.equals(name, this.mName)) {
            this.mName = name;
            this.mDevice.setAlias(name);
            dispatchAttributesChanged();
        }
    }

    public boolean setActive() {
        boolean result = false;
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null && isConnectedProfile(a2dpProfile) && a2dpProfile.setActiveDevice(getDevice())) {
            Log.i(TAG, "OnPreferenceClickListener: A2DP active device=" + this);
            result = true;
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null && isConnectedProfile(headsetProfile) && headsetProfile.setActiveDevice(getDevice())) {
            Log.i(TAG, "OnPreferenceClickListener: Headset active device=" + this);
            result = true;
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile == null || !isConnectedProfile(hearingAidProfile) || !hearingAidProfile.setActiveDevice(getDevice())) {
            return result;
        }
        Log.i(TAG, "OnPreferenceClickListener: Hearing Aid active device=" + this);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void refreshName() {
        fetchName();
        dispatchAttributesChanged();
    }

    private void fetchName() {
        this.mName = this.mDevice.getAliasName();
        if (TextUtils.isEmpty(this.mName)) {
            this.mName = this.mDevice.getAddress();
        }
    }

    public boolean hasHumanReadableName() {
        return !TextUtils.isEmpty(this.mDevice.getAliasName());
    }

    public int getBatteryLevel() {
        return this.mDevice.getBatteryLevel();
    }

    /* access modifiers changed from: package-private */
    public void refresh() {
        dispatchAttributesChanged();
    }

    public void setJustDiscovered(boolean justDiscovered) {
        if (this.mJustDiscovered != justDiscovered) {
            this.mJustDiscovered = justDiscovered;
            dispatchAttributesChanged();
        }
    }

    public int getBondState() {
        return this.mDevice.getBondState();
    }

    public void onActiveDeviceChanged(boolean isActive, int bluetoothProfile) {
        boolean changed = false;
        boolean z = false;
        if (bluetoothProfile != 21) {
            switch (bluetoothProfile) {
                case 1:
                    if (this.mIsActiveDeviceHeadset != isActive) {
                        z = true;
                    }
                    changed = z;
                    this.mIsActiveDeviceHeadset = isActive;
                    break;
                case 2:
                    if (this.mIsActiveDeviceA2dp != isActive) {
                        z = true;
                    }
                    changed = z;
                    this.mIsActiveDeviceA2dp = isActive;
                    break;
                default:
                    Log.w(TAG, "onActiveDeviceChanged: unknown profile " + bluetoothProfile + " isActive " + isActive);
                    break;
            }
        } else {
            if (this.mIsActiveDeviceHearingAid != isActive) {
                z = true;
            }
            changed = z;
            this.mIsActiveDeviceHearingAid = isActive;
        }
        if (changed) {
            dispatchAttributesChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onAudioModeChanged() {
        dispatchAttributesChanged();
    }

    @VisibleForTesting(otherwise = 3)
    public boolean isActiveDevice(int bluetoothProfile) {
        if (bluetoothProfile == 21) {
            return this.mIsActiveDeviceHearingAid;
        }
        switch (bluetoothProfile) {
            case 1:
                return this.mIsActiveDeviceHeadset;
            case 2:
                return this.mIsActiveDeviceA2dp;
            default:
                Log.w(TAG, "getActiveDevice: unknown profile " + bluetoothProfile);
                return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void setRssi(short rssi) {
        if (this.mRssi != rssi) {
            this.mRssi = rssi;
            dispatchAttributesChanged();
        }
    }

    public boolean isConnected() {
        for (LocalBluetoothProfile profile : this.mProfiles) {
            if (getProfileConnectionState(profile) == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean isConnectedProfile(LocalBluetoothProfile profile) {
        return getProfileConnectionState(profile) == 2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x000d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isBusy() {
        /*
            r5 = this;
            java.util.List<com.android.settingslib.bluetooth.LocalBluetoothProfile> r0 = r5.mProfiles
            java.util.Iterator r0 = r0.iterator()
        L_0x0006:
            boolean r1 = r0.hasNext()
            r2 = 1
            if (r1 == 0) goto L_0x001f
            java.lang.Object r1 = r0.next()
            com.android.settingslib.bluetooth.LocalBluetoothProfile r1 = (com.android.settingslib.bluetooth.LocalBluetoothProfile) r1
            int r3 = r5.getProfileConnectionState(r1)
            if (r3 == r2) goto L_0x001e
            r4 = 3
            if (r3 != r4) goto L_0x001d
            goto L_0x001e
        L_0x001d:
            goto L_0x0006
        L_0x001e:
            return r2
        L_0x001f:
            int r0 = r5.getBondState()
            r1 = 11
            if (r0 != r1) goto L_0x0028
            goto L_0x0029
        L_0x0028:
            r2 = 0
        L_0x0029:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.CachedBluetoothDevice.isBusy():boolean");
    }

    private void fetchBtClass() {
        this.mBtClass = this.mDevice.getBluetoothClass();
    }

    private boolean updateProfiles() {
        ParcelUuid[] localUuids;
        ParcelUuid[] uuids = this.mDevice.getUuids();
        if (uuids == null || (localUuids = this.mLocalAdapter.getUuids()) == null) {
            return false;
        }
        processPhonebookAccess();
        this.mProfileManager.updateProfiles(uuids, localUuids, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
        return true;
    }

    private void fetchActiveDevices() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            this.mIsActiveDeviceA2dp = this.mDevice.equals(a2dpProfile.getActiveDevice());
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            this.mIsActiveDeviceHeadset = this.mDevice.equals(headsetProfile.getActiveDevice());
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            this.mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(this.mDevice);
        }
    }

    /* access modifiers changed from: package-private */
    public void refreshBtClass() {
        fetchBtClass();
        dispatchAttributesChanged();
    }

    /* access modifiers changed from: package-private */
    public void onUuidChanged() {
        updateProfiles();
        ParcelUuid[] uuids = this.mDevice.getUuids();
        long timeout = MAX_UUID_DELAY_FOR_AUTO_CONNECT;
        if (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Hogp)) {
            timeout = MAX_HOGP_DELAY_FOR_AUTO_CONNECT;
        }
        if (!this.mProfiles.isEmpty() && this.mConnectAttempted + timeout > SystemClock.elapsedRealtime()) {
            connectWithoutResettingTimer(false);
        }
        dispatchAttributesChanged();
    }

    /* access modifiers changed from: package-private */
    public void onBondingStateChanged(int bondState) {
        if (bondState == 10) {
            this.mProfiles.clear();
            setPhonebookPermissionChoice(0);
            setMessagePermissionChoice(0);
            setSimPermissionChoice(0);
            this.mMessageRejectionCount = 0;
            saveMessageRejectionCount();
        }
        refresh();
        if (bondState != 12) {
            return;
        }
        if (this.mDevice.isBluetoothDock()) {
            onBondingDockConnect();
        } else if (this.mDevice.isBondingInitiatedLocally()) {
            connect(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void setBtClass(BluetoothClass btClass) {
        if (btClass != null && this.mBtClass != btClass) {
            this.mBtClass = btClass;
            dispatchAttributesChanged();
        }
    }

    public BluetoothClass getBtClass() {
        return this.mBtClass;
    }

    public List<LocalBluetoothProfile> getProfiles() {
        return Collections.unmodifiableList(this.mProfiles);
    }

    public List<LocalBluetoothProfile> getConnectableProfiles() {
        List<LocalBluetoothProfile> connectableProfiles = new ArrayList<>();
        for (LocalBluetoothProfile profile : this.mProfiles) {
            if (profile.isConnectable()) {
                connectableProfiles.add(profile);
            }
        }
        return connectableProfiles;
    }

    public List<LocalBluetoothProfile> getRemovedProfiles() {
        return this.mRemovedProfiles;
    }

    public void registerCallback(Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
        }
    }

    private void dispatchAttributesChanged() {
        synchronized (this.mCallbacks) {
            for (Callback callback : this.mCallbacks) {
                callback.onDeviceAttributesChanged();
            }
        }
    }

    public String toString() {
        return this.mDevice.toString();
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof CachedBluetoothDevice)) {
            return false;
        }
        return this.mDevice.equals(((CachedBluetoothDevice) o).mDevice);
    }

    public int hashCode() {
        return this.mDevice.getAddress().hashCode();
    }

    public int compareTo(CachedBluetoothDevice another) {
        int comparison = (another.isConnected() ? 1 : 0) - (isConnected() ? 1 : 0);
        if (comparison != 0) {
            return comparison;
        }
        int i = 0;
        int i2 = another.getBondState() == 12 ? 1 : 0;
        if (getBondState() == 12) {
            i = 1;
        }
        int comparison2 = i2 - i;
        if (comparison2 != 0) {
            return comparison2;
        }
        int comparison3 = (another.mJustDiscovered ? 1 : 0) - (this.mJustDiscovered ? 1 : 0);
        if (comparison3 != 0) {
            return comparison3;
        }
        int comparison4 = another.mRssi - this.mRssi;
        if (comparison4 != 0) {
            return comparison4;
        }
        return this.mName.compareTo(another.mName);
    }

    public int getPhonebookPermissionChoice() {
        int permission = this.mDevice.getPhonebookAccessPermission();
        if (permission == 1) {
            return 1;
        }
        if (permission == 2) {
            return 2;
        }
        return 0;
    }

    public void setPhonebookPermissionChoice(int permissionChoice) {
        int permission = 0;
        if (permissionChoice == 1) {
            permission = 1;
        } else if (permissionChoice == 2) {
            permission = 2;
        }
        this.mDevice.setPhonebookAccessPermission(permission);
    }

    private void migratePhonebookPermissionChoice() {
        SharedPreferences preferences = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
        if (preferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                int oldPermission = preferences.getInt(this.mDevice.getAddress(), 0);
                if (oldPermission == 1) {
                    this.mDevice.setPhonebookAccessPermission(1);
                } else if (oldPermission == 2) {
                    this.mDevice.setPhonebookAccessPermission(2);
                }
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(this.mDevice.getAddress());
            editor.commit();
        }
    }

    public int getMessagePermissionChoice() {
        int permission = this.mDevice.getMessageAccessPermission();
        if (permission == 1) {
            return 1;
        }
        if (permission == 2) {
            return 2;
        }
        return 0;
    }

    public void setMessagePermissionChoice(int permissionChoice) {
        int permission = 0;
        if (permissionChoice == 1) {
            permission = 1;
        } else if (permissionChoice == 2) {
            permission = 2;
        }
        this.mDevice.setMessageAccessPermission(permission);
    }

    public int getSimPermissionChoice() {
        int permission = this.mDevice.getSimAccessPermission();
        if (permission == 1) {
            return 1;
        }
        if (permission == 2) {
            return 2;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void setSimPermissionChoice(int permissionChoice) {
        int permission = 0;
        if (permissionChoice == 1) {
            permission = 1;
        } else if (permissionChoice == 2) {
            permission = 2;
        }
        this.mDevice.setSimAccessPermission(permission);
    }

    private void migrateMessagePermissionChoice() {
        SharedPreferences preferences = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
        if (preferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getMessageAccessPermission() == 0) {
                int oldPermission = preferences.getInt(this.mDevice.getAddress(), 0);
                if (oldPermission == 1) {
                    this.mDevice.setMessageAccessPermission(1);
                } else if (oldPermission == 2) {
                    this.mDevice.setMessageAccessPermission(2);
                }
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(this.mDevice.getAddress());
            editor.commit();
        }
    }

    public boolean checkAndIncreaseMessageRejectionCount() {
        if (this.mMessageRejectionCount < 2) {
            this.mMessageRejectionCount++;
            saveMessageRejectionCount();
        }
        if (this.mMessageRejectionCount >= 2) {
            return true;
        }
        return false;
    }

    private void fetchMessageRejectionCount() {
        this.mMessageRejectionCount = this.mContext.getSharedPreferences(MESSAGE_REJECTION_COUNT_PREFS_NAME, 0).getInt(this.mDevice.getAddress(), 0);
    }

    private void saveMessageRejectionCount() {
        SharedPreferences.Editor editor = this.mContext.getSharedPreferences(MESSAGE_REJECTION_COUNT_PREFS_NAME, 0).edit();
        if (this.mMessageRejectionCount == 0) {
            editor.remove(this.mDevice.getAddress());
        } else {
            editor.putInt(this.mDevice.getAddress(), this.mMessageRejectionCount);
        }
        editor.commit();
    }

    private void processPhonebookAccess() {
        if (this.mDevice.getBondState() == 12 && BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS) && getPhonebookPermissionChoice() == 0) {
            if (this.mDevice.getBluetoothClass().getDeviceClass() == 1032 || this.mDevice.getBluetoothClass().getDeviceClass() == 1028) {
                EventLog.writeEvent(1397638484, new Object[]{"138529441", -1, ""});
            }
            setPhonebookPermissionChoice(2);
        }
    }

    public int getMaxConnectionState() {
        int maxState = 0;
        for (LocalBluetoothProfile profile : getProfiles()) {
            int connectionStatus = getProfileConnectionState(profile);
            if (connectionStatus > maxState) {
                maxState = connectionStatus;
            }
        }
        return maxState;
    }

    public String getConnectionSummary() {
        boolean profileConnected = false;
        boolean a2dpConnected = true;
        boolean hfpConnected = true;
        boolean hearingAidConnected = true;
        for (LocalBluetoothProfile profile : getProfiles()) {
            int connectionStatus = getProfileConnectionState(profile);
            switch (connectionStatus) {
                case 0:
                    if (profile.isProfileReady()) {
                        if (!(profile instanceof A2dpProfile) && !(profile instanceof A2dpSinkProfile)) {
                            if (!(profile instanceof HeadsetProfile) && !(profile instanceof HfpClientProfile)) {
                                if (!(profile instanceof HearingAidProfile)) {
                                    break;
                                } else {
                                    hearingAidConnected = false;
                                    break;
                                }
                            } else {
                                hfpConnected = false;
                                break;
                            }
                        } else {
                            a2dpConnected = false;
                            break;
                        }
                    } else {
                        break;
                    }
                case 1:
                case 3:
                    return this.mContext.getString(Utils.getConnectionStateSummary(connectionStatus));
                case 2:
                    profileConnected = true;
                    break;
            }
        }
        String batteryLevelPercentageString = null;
        int batteryLevel = getBatteryLevel();
        if (batteryLevel != -1) {
            batteryLevelPercentageString = Utils.formatPercentage(batteryLevel);
        }
        int stringRes = R.string.bluetooth_pairing;
        if (profileConnected) {
            if (a2dpConnected || hfpConnected || hearingAidConnected) {
                if (batteryLevelPercentageString != null) {
                    stringRes = Utils.isAudioModeOngoingCall(this.mContext) ? this.mIsActiveDeviceHeadset ? R.string.bluetooth_active_battery_level : R.string.bluetooth_battery_level : (this.mIsActiveDeviceHearingAid || this.mIsActiveDeviceA2dp) ? R.string.bluetooth_active_battery_level : R.string.bluetooth_battery_level;
                } else if (Utils.isAudioModeOngoingCall(this.mContext)) {
                    if (this.mIsActiveDeviceHeadset) {
                        stringRes = R.string.bluetooth_active_no_battery_level;
                    }
                } else if (this.mIsActiveDeviceHearingAid || this.mIsActiveDeviceA2dp) {
                    stringRes = R.string.bluetooth_active_no_battery_level;
                }
            } else if (batteryLevelPercentageString != null) {
                stringRes = R.string.bluetooth_battery_level;
            }
        }
        if (stringRes == R.string.bluetooth_pairing && getBondState() != 11) {
            return null;
        }
        return this.mContext.getString(stringRes, new Object[]{batteryLevelPercentageString});
    }

    public String getCarConnectionSummary() {
        boolean profileConnected = false;
        boolean a2dpNotConnected = false;
        boolean hfpNotConnected = false;
        boolean hearingAidNotConnected = false;
        for (LocalBluetoothProfile profile : getProfiles()) {
            int connectionStatus = getProfileConnectionState(profile);
            switch (connectionStatus) {
                case 0:
                    if (profile.isProfileReady()) {
                        if (!(profile instanceof A2dpProfile) && !(profile instanceof A2dpSinkProfile)) {
                            if (!(profile instanceof HeadsetProfile) && !(profile instanceof HfpClientProfile)) {
                                if (!(profile instanceof HearingAidProfile)) {
                                    break;
                                } else {
                                    hearingAidNotConnected = true;
                                    break;
                                }
                            } else {
                                hfpNotConnected = true;
                                break;
                            }
                        } else {
                            a2dpNotConnected = true;
                            break;
                        }
                    } else {
                        break;
                    }
                case 1:
                case 3:
                    return this.mContext.getString(Utils.getConnectionStateSummary(connectionStatus));
                case 2:
                    profileConnected = true;
                    break;
            }
        }
        String batteryLevelPercentageString = null;
        int batteryLevel = getBatteryLevel();
        if (batteryLevel != -1) {
            batteryLevelPercentageString = Utils.formatPercentage(batteryLevel);
        }
        String[] activeDeviceStringsArray = this.mContext.getResources().getStringArray(R.array.bluetooth_audio_active_device_summaries);
        String activeDeviceString = activeDeviceStringsArray[0];
        if (!this.mIsActiveDeviceA2dp || !this.mIsActiveDeviceHeadset) {
            if (this.mIsActiveDeviceA2dp) {
                activeDeviceString = activeDeviceStringsArray[2];
            }
            if (this.mIsActiveDeviceHeadset) {
                activeDeviceString = activeDeviceStringsArray[3];
            }
        } else {
            activeDeviceString = activeDeviceStringsArray[1];
        }
        if (!hearingAidNotConnected && this.mIsActiveDeviceHearingAid) {
            String activeDeviceString2 = activeDeviceStringsArray[1];
            return this.mContext.getString(R.string.bluetooth_connected, new Object[]{activeDeviceString2});
        } else if (profileConnected) {
            if (!a2dpNotConnected || !hfpNotConnected) {
                if (a2dpNotConnected) {
                    if (batteryLevelPercentageString != null) {
                        return this.mContext.getString(R.string.bluetooth_connected_no_a2dp_battery_level, new Object[]{batteryLevelPercentageString, activeDeviceString});
                    }
                    return this.mContext.getString(R.string.bluetooth_connected_no_a2dp, new Object[]{activeDeviceString});
                } else if (hfpNotConnected) {
                    if (batteryLevelPercentageString != null) {
                        return this.mContext.getString(R.string.bluetooth_connected_no_headset_battery_level, new Object[]{batteryLevelPercentageString, activeDeviceString});
                    }
                    return this.mContext.getString(R.string.bluetooth_connected_no_headset, new Object[]{activeDeviceString});
                } else if (batteryLevelPercentageString != null) {
                    return this.mContext.getString(R.string.bluetooth_connected_battery_level, new Object[]{batteryLevelPercentageString, activeDeviceString});
                } else {
                    return this.mContext.getString(R.string.bluetooth_connected, new Object[]{activeDeviceString});
                }
            } else if (batteryLevelPercentageString != null) {
                return this.mContext.getString(R.string.bluetooth_connected_no_headset_no_a2dp_battery_level, new Object[]{batteryLevelPercentageString, activeDeviceString});
            } else {
                return this.mContext.getString(R.string.bluetooth_connected_no_headset_no_a2dp, new Object[]{activeDeviceString});
            }
        } else if (getBondState() == 11) {
            return this.mContext.getString(R.string.bluetooth_pairing);
        } else {
            return null;
        }
    }

    public boolean isA2dpDevice() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        return a2dpProfile != null && a2dpProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isHfpDevice() {
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        return headsetProfile != null && headsetProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        return hearingAidProfile != null && hearingAidProfile.getConnectionStatus(this.mDevice) == 2;
    }
}
