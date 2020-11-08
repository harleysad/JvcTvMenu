package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvRecordBase;
import com.mediatek.wwtv.setting.base.scan.model.StateDiskSettingsCallback;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.setting.widget.view.DiskSettingSubMenuDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.manager.Core;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevListener;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiskSettingDialog extends CommonDialog implements AdapterView.OnItemClickListener, View.OnClickListener, DevListener {
    private static final int DIALOG_DISMISS = 1;
    private static final int DIALOG_REFASH = 2;
    /* access modifiers changed from: private */
    public static String TAG = "DiskSettingDialog";
    private static final int TIMEOUT = 15000;
    StateInitDiskItemAdapter adapter;
    private final float hScale = 0.5f;
    /* access modifiers changed from: private */
    public final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    DiskSettingDialog.this.handler.removeMessages(msg.what);
                    DiskSettingDialog.this.dismiss();
                    return;
                case 2:
                    DiskSettingDialog.this.refreshList();
                    return;
                default:
                    return;
            }
        }
    };
    private View.AccessibilityDelegate mAccDelegate = new View.AccessibilityDelegate() {
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            String access$000 = DiskSettingDialog.TAG;
            MtkLog.d(access$000, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
            if (DiskSettingDialog.this.mUsbList != host) {
                String access$0002 = DiskSettingDialog.TAG;
                MtkLog.d(access$0002, "host:" + DiskSettingDialog.this.mUsbList + "," + host);
            } else {
                MtkLog.d(DiskSettingDialog.TAG, ":host =false");
                List<CharSequence> texts = event.getText();
                if (texts == null) {
                    String access$0003 = DiskSettingDialog.TAG;
                    MtkLog.d(access$0003, "texts :" + texts);
                } else if (event.getEventType() == 32768) {
                    MtkLog.d(DiskSettingDialog.TAG, ":ttsSelectchannelIndex =");
                } else if (event.getEventType() == 1) {
                    MtkLog.d(DiskSettingDialog.TAG, "click item");
                    DiskSettingDialog.this.showOpMenuList();
                }
            }
            try {
                return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
            } catch (Exception e) {
                String access$0004 = DiskSettingDialog.TAG;
                Log.d(access$0004, "Exception " + e);
                return true;
            }
        }
    };
    private StateDiskSettingsCallback mCallback;
    private final Context mContext;
    private Button mFormatBtn;
    private LinearLayout mOpMenuList;
    private Button mSetPVRBtn;
    private Button mSetShiftBtn;
    private Button mSpeedTestBtn;
    private final StorageManager mStorageManager;
    /* access modifiers changed from: private */
    public ListView mUsbList;
    private RelativeLayout mainLayout;
    public ArrayList<MountPoint> mountList = new ArrayList<>();
    private final float wScale = 0.8f;

    public DiskSettingDialog(Context context, int layoutID) {
        super(context, layoutID);
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        DevManager.getInstance().addDevListener(this);
        getWindow().setLayout((int) (((float) outSize.x) * 0.8f), (int) (((float) outSize.y) * 0.5f));
        getWindow().setBackgroundDrawableResource(R.drawable.tv_background);
        this.mContext = context;
        this.mountList = intiView2();
        this.mStorageManager = (StorageManager) this.mContext.getSystemService("storage");
        this.adapter = new StateInitDiskItemAdapter(this.mContext, this.mountList);
        setAdapter(this.adapter);
    }

    public void initView() {
        super.initView();
        this.mainLayout = (RelativeLayout) findViewById(R.id.pvr_timeshift_deviceinfo);
        this.mSetShiftBtn = (Button) findViewById(R.id.disksetting_setshift);
        this.mSetPVRBtn = (Button) findViewById(R.id.disksetting_setpvr);
        this.mFormatBtn = (Button) findViewById(R.id.disksetting_format);
        this.mSpeedTestBtn = (Button) findViewById(R.id.disksetting_speedtest);
        ((TextView) findViewById(R.id.device_info_title)).setImportantForAccessibility(2);
        if (CommonIntegration.isCNRegion()) {
            this.mSetShiftBtn.setVisibility(8);
            this.mSetPVRBtn.setFocusable(true);
            this.mSetPVRBtn.setFocusableInTouchMode(true);
            this.mSetPVRBtn.requestFocus();
        } else {
            this.mSetShiftBtn.setFocusable(true);
            this.mSetShiftBtn.setFocusableInTouchMode(true);
            this.mSetShiftBtn.requestFocus();
        }
        this.mOpMenuList = (LinearLayout) findViewById(R.id.device_info_sub_menu);
        this.mOpMenuList.setVisibility(4);
        this.mUsbList = (ListView) findViewById(R.id.device_info_list);
        this.mUsbList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        this.mUsbList.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                switch (keyCode) {
                    case 19:
                    case 20:
                        DiskSettingDialog.this.refreshTime();
                        return false;
                    case 22:
                    case 23:
                        DiskSettingDialog.this.showOpMenuList();
                        DiskSettingDialog.this.refreshTime();
                        return false;
                    case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                        DiskSettingDialog.this.dismiss();
                        break;
                }
                return false;
            }
        });
        this.mUsbList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DiskSettingDialog.this.showOpMenuList();
            }
        });
        initListener();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.handler.removeMessages(1);
        if (keyCode == 4) {
            this.handler.sendEmptyMessageDelayed(1, 500);
        } else if (keyCode == 21) {
            hiddenOpMenuList();
            this.handler.sendEmptyMessageDelayed(1, 15000);
        } else if (keyCode != 186) {
            this.handler.sendEmptyMessageDelayed(1, 15000);
        } else {
            dismiss();
        }
        return super.onKeyDown(keyCode, event);
    }

    private ArrayList<MountPoint> intiView2() {
        ArrayList<MountPoint> list = DeviceManager.getInstance().getMountPointList();
        String str = TAG;
        MtkLog.d(str, "LIST null" + list.size());
        if (list == null || list.size() == 0) {
            ArrayList<MountPoint> list2 = new ArrayList<>();
            String str2 = TAG;
            MtkLog.d(str2, "LIST size=" + list2.size());
            list2.add(new MountPoint("", "", this.mContext.getResources().getString(R.string.dvr_device_no)));
            return list2;
        }
        String str3 = TAG;
        MtkLog.d(str3, "LIST size=! null" + list.get(0).mMountPoint);
        return list;
    }

    public void refreshList(ListView view) {
        this.mountList = intiView2();
        this.adapter.setGroup(this.mountList);
        view.setAdapter(this.adapter);
    }

    public void refreshList() {
        this.mOpMenuList.setVisibility(4);
        this.mountList = intiView2();
        this.adapter.setGroup(this.mountList);
        this.adapter.setSelect(getSelectedPosition());
        setAdapter(this.adapter);
    }

    public ListView getListView() {
        return this.mUsbList;
    }

    /* access modifiers changed from: package-private */
    public void setAdapter(StateInitDiskItemAdapter adapter2) {
        this.mUsbList.setAdapter(adapter2);
    }

    public int getSelectedPosition() {
        return this.mUsbList.getSelectedItemPosition();
    }

    public int setSelectedPosition(int position) {
        this.mUsbList.setSelection(position);
        return 0;
    }

    private void initListener() {
        this.mSetShiftBtn.setOnClickListener(this);
        this.mSetPVRBtn.setOnClickListener(this);
        this.mFormatBtn.setOnClickListener(this);
        this.mSpeedTestBtn.setOnClickListener(this);
        this.mUsbList.setOnItemClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disksetting_format /*2131362188*/:
                dismiss();
                if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
                    Toast.makeText(this.mContext, "Recording! Format Failed !", 0).show();
                    break;
                } else {
                    ArrayList<MountPoint> deviceList = DeviceManager.getInstance().getMountPointList();
                    Util.showDLog("deviceList.size()::" + deviceList.size());
                    new DiskSettingSubMenuDialog(this.mContext, DiskSettingSubMenuDialog.UiType.FORMATCONFIRM, deviceList.get(getSelectedPosition())).show();
                    break;
                }
            case R.id.disksetting_setpvr /*2131362189*/:
                setPVR();
                break;
            case R.id.disksetting_setshift /*2131362190*/:
                setTSHIFT();
                break;
            case R.id.disksetting_speedtest /*2131362191*/:
                dismiss();
                if (StateDvr.getInstance() == null || !StateDvr.getInstance().isRecording()) {
                    if (!SaveValue.getInstance(this.mContext).readBooleanValue(MenuConfigManager.TIMESHIFT_START)) {
                        ArrayList<MountPoint> deviceList1 = DeviceManager.getInstance().getMountPointList();
                        int selection1 = getSelectedPosition();
                        DiskSettingSubMenuDialog mSpeechTest = new DiskSettingSubMenuDialog(this.mContext, DiskSettingSubMenuDialog.UiType.SPEEDTEST_ING, deviceList1.get(selection1));
                        if (!deviceList1.get(selection1).mFsType.equals(MountPoint.FS_TYPE.FS_TYPE_NTFS)) {
                            mSpeechTest.show();
                            break;
                        } else {
                            Toast.makeText(this.mContext, "Device not support! Speed Test Failed !", 0).show();
                            break;
                        }
                    } else {
                        Toast.makeText(this.mContext, "TimeShift is Running! Speed Test Failed !", 0).show();
                        break;
                    }
                } else {
                    Toast.makeText(this.mContext, "Recording! Speed Test Failed !", 0).show();
                    break;
                }
                break;
        }
        super.onClick(v);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    public void hiddenOpMenuList() {
        this.mOpMenuList.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public void showOpMenuList() {
        String str = TAG;
        MtkLog.d(str, "string" + this.mountList.get(0).mMountPoint);
        if (!"".equalsIgnoreCase(this.mountList.get(0).mMountPoint)) {
            String str2 = TAG;
            MtkLog.d(str2, "string11" + this.mountList.get(0).mMountPoint);
            this.mOpMenuList.setVisibility(0);
            if (CommonIntegration.isCNRegion()) {
                this.mSetShiftBtn.setVisibility(8);
                this.mSetPVRBtn.requestFocus();
                this.mSetPVRBtn.setFocusable(true);
                this.mSetPVRBtn.setFocusableInTouchMode(true);
                this.mSetPVRBtn.requestFocus();
            } else {
                this.mSetShiftBtn.requestFocus();
                this.mSetShiftBtn.setFocusable(true);
                this.mSetShiftBtn.setFocusableInTouchMode(true);
                this.mSetShiftBtn.requestFocus();
            }
            if (DataSeparaterUtil.getInstance() != null && !DataSeparaterUtil.getInstance().isSupportPvr()) {
                this.mSetPVRBtn.setVisibility(8);
            }
            if (DataSeparaterUtil.getInstance() != null && !DataSeparaterUtil.getInstance().isSupportTShift()) {
                this.mSetShiftBtn.setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: private */
    public void refreshTime() {
        this.handler.removeMessages(1);
        MtkLog.d(TAG, "default msg=");
        this.handler.sendEmptyMessageDelayed(1, 15000);
    }

    public boolean setTSHIFT() {
        ArrayList<MountPoint> deviceList = DeviceManager.getInstance().getMountPointList();
        int selection = getSelectedPosition();
        String diskPath = deviceList.get(selection).mMountPoint;
        if (selection >= 0) {
            Util.tempSetTHIFT(diskPath);
        }
        Iterator<MountPoint> it = deviceList.iterator();
        while (it.hasNext()) {
            MountPoint point = it.next();
            if (!diskPath.equalsIgnoreCase(point.mMountPoint)) {
                Util.tempDelTSHIFT(point, this.mStorageManager);
            }
        }
        this.adapter.setTypeFlag(2);
        refreshList();
        if (selection < 0) {
            return false;
        }
        setSelectedPosition(selection);
        return false;
    }

    public boolean setPVR() {
        ArrayList<MountPoint> deviceList = DeviceManager.getInstance().getMountPointList();
        int selection = getSelectedPosition();
        String diskPath = deviceList.get(selection).mMountPoint;
        if (selection >= 0) {
            Util.tempSetPVR(diskPath);
        }
        Iterator<MountPoint> it = deviceList.iterator();
        while (it.hasNext()) {
            MountPoint point = it.next();
            if (!diskPath.equalsIgnoreCase(point.mMountPoint)) {
                Util.tempDelPVR(point.mMountPoint);
            }
        }
        this.adapter.setTypeFlag(1);
        refreshList();
        if (selection >= 0) {
            setSelectedPosition(selection);
        }
        int i = 0;
        while (true) {
            if (i >= deviceList.size()) {
                break;
            }
            if (new File(deviceList.get(i).mMountPoint + Core.PVR_DISK_TAG).exists()) {
                MtkLog.d(TAG, "isTuned = " + deviceList.get(i).mMountPoint);
                MtkTvRecordBase.setDisk(deviceList.get(i).mMountPoint);
                break;
            }
            i++;
        }
        return false;
    }

    public void show() {
        super.show();
        this.mUsbList.setAccessibilityDelegate(this.mAccDelegate);
        this.handler.sendEmptyMessageDelayed(1, 15000);
    }

    public void onEvent(DeviceManagerEvent event) {
        switch (event.getType()) {
            case 601:
                DiskSettingSubMenuDialog.resetSpeedList(event.getMountPointPath());
                this.handler.sendEmptyMessage(2);
                return;
            case 602:
                if (!DiskSettingSubMenuDialog.isFormat()) {
                    DiskSettingSubMenuDialog.resetSpeedList(event.getMountPointPath());
                    this.handler.sendEmptyMessage(2);
                    return;
                }
                return;
            default:
                return;
        }
    }
}
