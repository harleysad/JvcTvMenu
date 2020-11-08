package com.mediatek.wwtv.tvcenter.dvr.manager;

import com.mediatek.dm.Device;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.DeviceManagerListener;
import com.mediatek.dm.MountPoint;
import java.util.ArrayList;
import java.util.Iterator;

public class DevManager {
    private static final String TAG = "DevManager";
    private static DevManager dman = null;
    private DeviceManager dm = DeviceManager.getInstance();
    private DeviceManagerListener dmListener = new DeviceManagerListener() {
        public void onEvent(DeviceManagerEvent arg0) {
            if (DevManager.this.onDevListener.size() > 0) {
                Iterator<DevListener> it = DevManager.this.onDevListener.iterator();
                while (it.hasNext()) {
                    it.next().onEvent(arg0);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<DevListener> onDevListener = new ArrayList<>();

    private DevManager() {
        this.dm.addListener(this.dmListener);
    }

    public static synchronized DevManager getInstance() {
        DevManager devManager;
        synchronized (DevManager.class) {
            if (dman == null) {
                dman = new DevManager();
            }
            devManager = dman;
        }
        return devManager;
    }

    public int getMountCount() {
        return this.dm.getMountPointCount();
    }

    public ArrayList<MountPoint> getMountList() {
        return this.dm.getMountPointList();
    }

    public MountPoint getPointInfo(String path) {
        return this.dm.getMountPoint(path);
    }

    public void addDevListener(DevListener devListener) {
        this.onDevListener.add(devListener);
    }

    public void removeDevListener(DevListener devListener) {
        this.onDevListener.remove(devListener);
    }

    public void removeDevListeners() {
        this.onDevListener.clear();
    }

    public void mountISOFile(String isoFilePath) {
        this.dm.mountISO(isoFilePath);
    }

    public void umoutISOFile(String isoMountPath) {
        this.dm.umountISO(isoMountPath);
    }

    public boolean isVirtualDev(String isMountPath) {
        return this.dm.isVirtualDevice(isMountPath);
    }

    public void unMountDevice(MountPoint mountPoint) {
        unMountDevice(getDeviceName(mountPoint));
    }

    private void unMountDevice(String devName) {
        if (devName != null) {
            this.dm.umountDevice(devName);
        }
    }

    private String getDeviceName(MountPoint mountPoint) {
        Device dv = this.dm.getParentDevice(mountPoint);
        if (dv != null) {
            return dv.mDeviceName;
        }
        return null;
    }

    public void destroy() {
        this.dm.removeListener(this.dmListener);
        this.onDevListener.clear();
    }
}
