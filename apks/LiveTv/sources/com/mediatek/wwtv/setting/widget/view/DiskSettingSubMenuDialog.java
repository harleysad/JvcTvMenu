package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.FileSystemType;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvTimeshift;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevListener;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevManager;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DiskSettingSubMenuDialog extends CommonDialog implements DevListener {
    private static final int LAYOUT_FORMAT_CONFIRM = 2131493169;
    private static final int LAYOUT_PROGRESS = 2131493170;
    /* access modifiers changed from: private */
    public static String TAG = "DiskSettingSubMenuDialog";
    private static boolean isFormat = false;
    /* access modifiers changed from: private */
    public static Map<String, Float> speedList = new HashMap();
    private final int EXIT_BTN = R.id.pvr_tshift_device_diskop_exit;
    private final int FORMAT_CONFIRM_BTN_NO = R.id.confirm_btn_no;
    private final int FORMAT_CONFIRM_BTN_YES = R.id.confirm_btn_yes;
    private final int FORMAT_FAILED = 257;
    private final int FORMAT_SUC = EPGConfig.EPG_SYNCHRONIZATION_MESSAGE;
    private final int LAYOUT_LOADING = R.layout.pvr_tshift_dialog_layout_loading;
    private final int SPEED_TEST_FAILED = 256;
    private final int SPEED_TEST_PROGRESS = EPGConfig.EPG_PROGRAMINFO_SHOW;
    private final int SPEED_TEST_SUC = 258;
    /* access modifiers changed from: private */
    public final Handler dvrEventHandler = new Handler() {
        int mm = 0;

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int callBack = msg.what;
            String access$200 = DiskSettingSubMenuDialog.TAG;
            MtkLog.d(access$200, "dvrEventHandler  = " + callBack);
            if (callBack != 4100) {
                switch (callBack) {
                    case 4103:
                        if (this.mm == callBack) {
                            DvrManager.getInstance().getController().removeEventHandler(DiskSettingSubMenuDialog.this.dvrEventHandler);
                            DiskSettingSubMenuDialog.this.showFormatDisk();
                            this.mm = 0;
                            return;
                        }
                        this.mm = 4104;
                        return;
                    case 4104:
                        if (this.mm == callBack) {
                            DvrManager.getInstance().getController().removeEventHandler(DiskSettingSubMenuDialog.this.dvrEventHandler);
                            DiskSettingSubMenuDialog.this.showFormatDisk();
                            this.mm = 0;
                            return;
                        }
                        this.mm = 4103;
                        return;
                    default:
                        return;
                }
            } else {
                DvrManager.getInstance().getController().removeEventHandler(DiskSettingSubMenuDialog.this.dvrEventHandler);
                DiskSettingSubMenuDialog.this.showFormatDisk();
            }
        }
    };
    private Button exitBtn;
    /* access modifiers changed from: private */
    public FileOutputStream fis = null;
    private final float hScale = 0.35f;
    /* access modifiers changed from: private */
    public final Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            String access$200 = DiskSettingSubMenuDialog.TAG;
            MtkLog.d(access$200, "dispatchMessage  msg= " + msg.what);
            switch (msg.what) {
                case 256:
                    Toast.makeText(DiskSettingSubMenuDialog.this.mContext, "speed test failed !", 0).show();
                    removeMessages(256);
                    DiskSettingSubMenuDialog.setFormat(false);
                    DiskSettingSubMenuDialog.this.dismiss();
                    new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                    return;
                case 257:
                    Toast.makeText(DiskSettingSubMenuDialog.this.mContext, "format failed !", 0).show();
                    removeMessages(257);
                    DiskSettingSubMenuDialog.setFormat(false);
                    DiskSettingSubMenuDialog.this.dismiss();
                    new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                    return;
                case 258:
                    DiskSettingSubMenuDialog.this.refreshProgres(msg.arg1);
                    return;
                case EPGConfig.EPG_SYNCHRONIZATION_MESSAGE /*259*/:
                    removeMessages(EPGConfig.EPG_SYNCHRONIZATION_MESSAGE);
                    DiskSettingSubMenuDialog.setFormat(false);
                    DiskSettingSubMenuDialog.this.dismiss();
                    new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                    return;
                case EPGConfig.EPG_PROGRAMINFO_SHOW /*260*/:
                    DiskSettingSubMenuDialog.this.refreshProgres(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    };
    private ImageView iv;
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar;
    private TextView mProgressValueTextview;
    private TextView mTitle1;
    private TextView mTitle2;
    private UiType mType;
    /* access modifiers changed from: private */
    public final MountPoint mountPoint;
    /* access modifiers changed from: private */
    public float speedRate = 0.0f;
    private final float wScale = 0.3f;

    public enum UiType {
        FORMATCONFIRM,
        FORMAT_ING,
        FORMAT_DONE,
        FORMAT_FAIL,
        SPEEDTEST_ING,
        SPEEDTEST_DONE
    }

    public DiskSettingSubMenuDialog(Context context, UiType type, MountPoint mountPoint2) {
        super(context, R.layout.pvr_tshift_confirmdialog);
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        DevManager.getInstance().addDevListener(this);
        getWindow().setLayout((int) (((float) outSize.x) * 0.3f), (int) (((float) outSize.y) * 0.35f));
        this.mType = type;
        this.mountPoint = mountPoint2;
        setCancelable(false);
    }

    public void initView() {
        super.initView();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && (this.mType == UiType.SPEEDTEST_DONE || this.mType == UiType.FORMAT_DONE)) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    DiskSettingSubMenuDialog.this.dismiss();
                    new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                }
            }, 300);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void show() {
        super.show();
        refreshUI(this.mType);
    }

    private void initViewItem() {
        this.mTitle1 = (TextView) findViewById(R.id.diskop_title_line1);
        this.mTitle2 = (TextView) findViewById(R.id.diskop_title_line2);
        this.mProgressValueTextview = (TextView) findViewById(R.id.finishpercentage);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        this.iv = (ImageView) findViewById(R.id.iv);
        this.mProgressValueTextview.setText("0%");
        this.iv.setVisibility(8);
        this.mProgressBar.setMax(100);
        this.mProgressBar.setProgress(0);
    }

    public void refreshProgres(int percent) {
        if (percent >= 100) {
            percent = 100;
            if (this.mType == UiType.SPEEDTEST_ING) {
                refreshUI(UiType.SPEEDTEST_DONE);
            }
            if (this.mType == UiType.FORMAT_ING) {
                refreshUI(UiType.FORMAT_FAIL);
            }
        }
        if (percent < 0) {
            percent = 0;
        }
        TextView textView = this.mProgressValueTextview;
        textView.setText(String.format("%3.0f", new Object[]{Float.valueOf(((float) percent) * 1.0f)}) + "%");
        this.mProgressBar.setProgress(percent);
    }

    public void refreshUI(UiType type) {
        this.mType = type;
        switch (type) {
            case FORMATCONFIRM:
                setContentView(R.layout.pvr_tshift_confirmdialog);
                ((TextView) findViewById(R.id.diskop_title_line1_confirm)).setText(this.mContext.getResources().getString(R.string.format_confirm_dialog_line1));
                ((TextView) findViewById(R.id.diskop_title_line2_confirm)).setText(this.mContext.getResources().getString(R.string.format_confirm_dialog_line2));
                ((Button) findViewById(R.id.confirm_btn_yes)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DiskSettingSubMenuDialog.this.refreshUI(UiType.FORMAT_ING);
                    }
                });
                Button cancelBTN = (Button) findViewById(R.id.confirm_btn_no);
                cancelBTN.requestFocus();
                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DiskSettingSubMenuDialog.this.dismiss();
                        new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                    }
                });
                return;
            case FORMAT_ING:
                setContentView(R.layout.pvr_tshift_dialog_layout_loading);
                showFormatDisk();
                return;
            case FORMAT_DONE:
                this.mTitle1.setText("");
                this.mTitle2.setText(this.mContext.getResources().getString(R.string.disk_setting_format_done));
                this.mProgressValueTextview.setText("100%");
                return;
            case SPEEDTEST_ING:
                setContentView(R.layout.pvr_tshift_dialog_layout);
                initViewItem();
                this.mTitle1.setText(this.mContext.getResources().getString(R.string.disk_setting_speedtesting));
                this.mTitle2.setText(this.mContext.getResources().getString(R.string.disk_setting_unplugdevice_warnning));
                showSpeedTest("");
                this.exitBtn = (Button) findViewById(R.id.pvr_tshift_device_diskop_exit);
                this.exitBtn.setVisibility(0);
                this.exitBtn.setText(R.string.disk_setting_speeding);
                this.exitBtn.requestFocus();
                return;
            case SPEEDTEST_DONE:
                this.mTitle1.setText(this.mContext.getResources().getString(R.string.disk_setting_speedtest_done));
                this.mTitle2.setText(String.format(this.mContext.getResources().getString(R.string.disk_setting_speedtest_maxspeed), new Object[]{Float.valueOf(this.speedRate)}));
                this.mProgressValueTextview.setText("100%");
                this.exitBtn = (Button) findViewById(R.id.pvr_tshift_device_diskop_exit);
                this.exitBtn.setText(R.string.exit);
                this.exitBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (DiskSettingSubMenuDialog.this.mProgressBar.getProgress() == 100) {
                            DiskSettingSubMenuDialog.this.dismiss();
                            new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                        } else {
                            Toast.makeText(DiskSettingSubMenuDialog.this.mContext, "Formating !Please wait!", 1).show();
                        }
                        DiskSettingSubMenuDialog.this.dismiss();
                    }
                });
                this.exitBtn.setVisibility(0);
                this.exitBtn.requestFocus();
                return;
            case FORMAT_FAIL:
                this.mTitle1.setText("");
                this.mTitle2.setText(this.mContext.getResources().getString(R.string.disk_setting_format_fail));
                this.mProgressValueTextview.setText("100%");
                this.exitBtn = (Button) findViewById(R.id.pvr_tshift_device_diskop_exit);
                this.exitBtn.requestFocus();
                this.exitBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DiskSettingSubMenuDialog.this.dismiss();
                        new DiskSettingDialog(DiskSettingSubMenuDialog.this.mContext, R.layout.pvr_timeshfit_deviceinfo).show();
                    }
                });
                return;
            default:
                return;
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.pvr_tshift_device_diskop_exit) {
            switch (id) {
                case R.id.confirm_btn_no /*2131362122*/:
                    dismiss();
                    break;
                case R.id.confirm_btn_yes /*2131362123*/:
                    refreshUI(UiType.FORMAT_ING);
                    break;
            }
        } else {
            dismiss();
        }
        super.onClick(v);
    }

    public boolean showFormatDisk() {
        if (isStopDvrRec() || isStopTshiftRec()) {
            return false;
        }
        startFormatThread();
        return true;
    }

    private boolean isStopDvrRec() {
        if (StateDvr.getInstance() == null || !StateDvr.getInstance().isRecording()) {
            return false;
        }
        MtkLog.i(TAG, "StateDvr.stopRecording ");
        DvrManager.getInstance().getController().addEventHandler(this.dvrEventHandler);
        StateDvr.getInstance().getController().stopRecording();
        return true;
    }

    private boolean isStopTshiftRec() {
        if (TifTimeShiftManager.getInstance() == null || !TifTimeShiftManager.getInstance().isTimeshiftStarted()) {
            return false;
        }
        MtkLog.i(TAG, "StopTshiftRec");
        DvrManager.getInstance().getController().addEventHandler(this.dvrEventHandler);
        MtkTvTimeshift.getInstance().setAutoRecord(false);
        TifTimeShiftManager.getInstance().stopAll();
        return true;
    }

    /* access modifiers changed from: private */
    public void startTshiftRec() {
        MtkLog.i(TAG, "startTshiftRec ? ");
        if (TifTimeShiftManager.getInstance() != null && DvrManager.getInstance().timeShiftIsEnable()) {
            MtkLog.i(TAG, "startTshiftRec ++");
            MtkTvTimeshift.getInstance().setAutoRecord(true);
        }
    }

    private void startFormatThread() {
        MtkLog.i(TAG, "startFormatThread()");
        if (!isFormat) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    DiskSettingSubMenuDialog.setFormat(true);
                    try {
                        DeviceManager dm = DeviceManager.getInstance();
                        if (DiskSettingSubMenuDialog.this.mountPoint == null) {
                            DiskSettingSubMenuDialog.this.startTshiftRec();
                            return;
                        }
                        String extern_sd = DiskSettingSubMenuDialog.this.mountPoint.mDeviceName;
                        MtkLog.i(DiskSettingSubMenuDialog.TAG, "umountVol()");
                        dm.umountVol(extern_sd);
                        int result_format = dm.formatVol(FileSystemType.FS_FAT32, DiskSettingSubMenuDialog.this.mountPoint.mDeviceName);
                        MtkLog.i(DiskSettingSubMenuDialog.TAG, "formatVol()");
                        if (result_format == 0) {
                            dm.mountVol(extern_sd);
                            DiskSettingSubMenuDialog.this.handler.sendEmptyMessageDelayed(EPGConfig.EPG_SYNCHRONIZATION_MESSAGE, 100);
                        } else {
                            DiskSettingSubMenuDialog.this.handler.sendEmptyMessageDelayed(257, 100);
                        }
                        DiskSettingSubMenuDialog.this.startTshiftRec();
                    } catch (Exception e) {
                        DiskSettingSubMenuDialog.this.handler.sendEmptyMessageDelayed(257, 100);
                    } catch (Throwable th) {
                        DiskSettingSubMenuDialog.this.startTshiftRec();
                        throw th;
                    }
                }
            }, 2500);
        }
    }

    public boolean showSpeedTest(String args) {
        new Thread(new Runnable() {
            public void run() {
                int progress;
                Long startTime1;
                int progress2;
                int progress3;
                Long startTime12;
                Long startTime13;
                String subPath;
                int index = (int) (Math.random() * 1000.0d);
                ArrayList<MountPoint> mountList = DevManager.getInstance().getMountList();
                if (mountList == null) {
                    ArrayList<MountPoint> arrayList = mountList;
                } else if (mountList == null || mountList.size() > 0) {
                    DevManager.getInstance().addDevListener(DiskSettingSubMenuDialog.this);
                    String subPath2 = DiskSettingSubMenuDialog.this.mountPoint.mMountPoint.substring(9, DiskSettingSubMenuDialog.this.mountPoint.mMountPoint.length());
                    String changeDeleteFile = "/mnt/media_rw/" + subPath2;
                    MtkLog.i(DiskSettingSubMenuDialog.TAG, "deleteSelectedFile,changeDeleteFile=" + changeDeleteFile);
                    String path = String.format(changeDeleteFile + "/speedTest%d.dat", new Object[]{Integer.valueOf(index)});
                    File testFile = new File(path);
                    Long MinTime = Long.MAX_VALUE;
                    if (testFile.exists()) {
                        testFile.delete();
                    }
                    try {
                        testFile.createNewFile();
                        int bufferSize = 122880;
                        float counts = 900.0f;
                        byte[] writeByte = new byte[122880];
                        String str = path;
                        try {
                            FileOutputStream unused = DiskSettingSubMenuDialog.this.fis = new FileOutputStream(testFile);
                            Long startTime = Long.valueOf(System.currentTimeMillis());
                            int i = index;
                            ArrayList<MountPoint> arrayList2 = mountList;
                            Long startTime14 = 0L;
                            int progress4 = 0;
                            while (true) {
                                progress = progress4;
                                startTime1 = startTime14;
                                if (counts <= 0.0f) {
                                    break;
                                }
                                progress2 = progress;
                                try {
                                    startTime14 = Long.valueOf(System.currentTimeMillis());
                                    try {
                                        DiskSettingSubMenuDialog.this.fis.write(writeByte);
                                        subPath = subPath2;
                                    } catch (FileNotFoundException e) {
                                        e = e;
                                        String str2 = subPath2;
                                        progress3 = progress2;
                                        try {
                                            e.printStackTrace();
                                            bufferSize = 0;
                                            startTime12 = startTime14;
                                        } catch (Throwable th) {
                                            th = th;
                                            Long l = startTime14;
                                            byte[] bArr = writeByte;
                                            int i2 = progress3;
                                            testFile.delete();
                                            throw th;
                                        }
                                        try {
                                            DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                            testFile.delete();
                                            Long l2 = startTime12;
                                            int i3 = progress3;
                                            Long endTime = Long.valueOf(System.currentTimeMillis());
                                            byte[] bArr2 = writeByte;
                                            float maxSpeed = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                            float unused2 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                            DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                            MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                            Message msg = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                            msg.arg1 = 100;
                                            msg.what = 258;
                                            DiskSettingSubMenuDialog.this.handler.sendMessage(msg);
                                        } catch (Throwable th2) {
                                            th = th2;
                                            byte[] bArr3 = writeByte;
                                            int i4 = progress3;
                                            Long l3 = startTime12;
                                            testFile.delete();
                                            throw th;
                                        }
                                    } catch (IOException e2) {
                                        e = e2;
                                        String str3 = subPath2;
                                        progress3 = progress2;
                                        try {
                                            e.printStackTrace();
                                            bufferSize = 0;
                                            startTime13 = startTime14;
                                        } catch (Throwable th3) {
                                            th = th3;
                                            Long l4 = startTime14;
                                            int i5 = progress3;
                                            byte[] bArr4 = writeByte;
                                            testFile.delete();
                                            throw th;
                                        }
                                        try {
                                            DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                            testFile.delete();
                                            int i32 = progress3;
                                            Long endTime2 = Long.valueOf(System.currentTimeMillis());
                                            byte[] bArr22 = writeByte;
                                            float maxSpeed2 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                            float unused3 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime2.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                            DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                            MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                            Message msg2 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                            msg2.arg1 = 100;
                                            msg2.what = 258;
                                            DiskSettingSubMenuDialog.this.handler.sendMessage(msg2);
                                        } catch (Throwable th4) {
                                            th = th4;
                                            int i6 = progress3;
                                            byte[] bArr5 = writeByte;
                                            Long l5 = startTime13;
                                            testFile.delete();
                                            throw th;
                                        }
                                    } catch (Throwable th5) {
                                        th = th5;
                                        String str4 = subPath2;
                                        byte[] bArr6 = writeByte;
                                        testFile.delete();
                                        throw th;
                                    }
                                    try {
                                        Long startTime2 = Long.valueOf(System.currentTimeMillis());
                                        if (MinTime.longValue() > startTime2.longValue() - startTime14.longValue()) {
                                            if (startTime2.longValue() - startTime14.longValue() > 0) {
                                                MinTime = Long.valueOf(startTime2.longValue() - startTime14.longValue());
                                            }
                                        }
                                        counts -= 1.0f;
                                        progress3 = (int) (((900.0f - counts) / 900.0f) * 100.0f);
                                        if (progress3 != 100) {
                                            try {
                                                Message msg3 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                                msg3.arg1 = progress3;
                                                msg3.what = EPGConfig.EPG_PROGRAMINFO_SHOW;
                                                DiskSettingSubMenuDialog.this.handler.sendMessage(msg3);
                                            } catch (FileNotFoundException e3) {
                                                e = e3;
                                            } catch (IOException e4) {
                                                e = e4;
                                                e.printStackTrace();
                                                bufferSize = 0;
                                                startTime13 = startTime14;
                                                DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                                testFile.delete();
                                                int i322 = progress3;
                                                Long endTime22 = Long.valueOf(System.currentTimeMillis());
                                                byte[] bArr222 = writeByte;
                                                float maxSpeed22 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                                float unused4 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime22.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                                DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                                MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                                Message msg22 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                                msg22.arg1 = 100;
                                                msg22.what = 258;
                                                DiskSettingSubMenuDialog.this.handler.sendMessage(msg22);
                                            } catch (Throwable th6) {
                                                th = th6;
                                                int i7 = progress3;
                                                testFile.delete();
                                                throw th;
                                            }
                                        }
                                        progress4 = progress3;
                                        subPath2 = subPath;
                                    } catch (FileNotFoundException e5) {
                                        e = e5;
                                        progress3 = progress2;
                                        e.printStackTrace();
                                        bufferSize = 0;
                                        startTime12 = startTime14;
                                        DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                        testFile.delete();
                                        Long l22 = startTime12;
                                        int i3222 = progress3;
                                        Long endTime222 = Long.valueOf(System.currentTimeMillis());
                                        byte[] bArr2222 = writeByte;
                                        float maxSpeed222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                        float unused5 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                        DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                        MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                        Message msg222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                        msg222.arg1 = 100;
                                        msg222.what = 258;
                                        DiskSettingSubMenuDialog.this.handler.sendMessage(msg222);
                                    } catch (IOException e6) {
                                        e = e6;
                                        progress3 = progress2;
                                        e.printStackTrace();
                                        bufferSize = 0;
                                        startTime13 = startTime14;
                                        DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                        testFile.delete();
                                        int i32222 = progress3;
                                        Long endTime2222 = Long.valueOf(System.currentTimeMillis());
                                        byte[] bArr22222 = writeByte;
                                        float maxSpeed2222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                        float unused6 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime2222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                        DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                        MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                        Message msg2222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                        msg2222.arg1 = 100;
                                        msg2222.what = 258;
                                        DiskSettingSubMenuDialog.this.handler.sendMessage(msg2222);
                                    } catch (Throwable th7) {
                                        th = th7;
                                        testFile.delete();
                                        throw th;
                                    }
                                } catch (FileNotFoundException e7) {
                                    e = e7;
                                    String str5 = subPath2;
                                    startTime14 = startTime1;
                                    progress3 = progress2;
                                    e.printStackTrace();
                                    bufferSize = 0;
                                    startTime12 = startTime14;
                                    DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                    testFile.delete();
                                    Long l222 = startTime12;
                                    int i322222 = progress3;
                                    Long endTime22222 = Long.valueOf(System.currentTimeMillis());
                                    byte[] bArr222222 = writeByte;
                                    float maxSpeed22222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                    float unused7 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime22222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                    DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                    MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                    Message msg22222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                    msg22222.arg1 = 100;
                                    msg22222.what = 258;
                                    DiskSettingSubMenuDialog.this.handler.sendMessage(msg22222);
                                } catch (IOException e8) {
                                    e = e8;
                                    String str6 = subPath2;
                                    startTime14 = startTime1;
                                    progress3 = progress2;
                                    e.printStackTrace();
                                    bufferSize = 0;
                                    startTime13 = startTime14;
                                    DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                    testFile.delete();
                                    int i3222222 = progress3;
                                    Long endTime222222 = Long.valueOf(System.currentTimeMillis());
                                    byte[] bArr2222222 = writeByte;
                                    float maxSpeed222222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                    float unused8 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime222222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                    DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                    MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                    Message msg222222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                    msg222222.arg1 = 100;
                                    msg222222.what = 258;
                                    DiskSettingSubMenuDialog.this.handler.sendMessage(msg222222);
                                } catch (Throwable th8) {
                                    th = th8;
                                    String str7 = subPath2;
                                    byte[] bArr7 = writeByte;
                                    Long l6 = startTime1;
                                    testFile.delete();
                                    throw th;
                                }
                            }
                            progress2 = progress;
                            String str8 = subPath2;
                            try {
                                DiskSettingSubMenuDialog.this.fis.close();
                                FileOutputStream unused9 = DiskSettingSubMenuDialog.this.fis = null;
                                testFile.delete();
                                Long l7 = startTime1;
                                progress3 = progress2;
                            } catch (FileNotFoundException e9) {
                                e = e9;
                                startTime14 = startTime1;
                                progress3 = progress2;
                                e.printStackTrace();
                                bufferSize = 0;
                                startTime12 = startTime14;
                                DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                testFile.delete();
                                Long l2222 = startTime12;
                                int i32222222 = progress3;
                                Long endTime2222222 = Long.valueOf(System.currentTimeMillis());
                                byte[] bArr22222222 = writeByte;
                                float maxSpeed2222222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                float unused10 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime2222222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                Message msg2222222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                msg2222222.arg1 = 100;
                                msg2222222.what = 258;
                                DiskSettingSubMenuDialog.this.handler.sendMessage(msg2222222);
                            } catch (IOException e10) {
                                e = e10;
                                startTime14 = startTime1;
                                progress3 = progress2;
                                e.printStackTrace();
                                bufferSize = 0;
                                startTime13 = startTime14;
                                DiskSettingSubMenuDialog.this.handler.sendEmptyMessage(256);
                                testFile.delete();
                                int i322222222 = progress3;
                                Long endTime22222222 = Long.valueOf(System.currentTimeMillis());
                                byte[] bArr222222222 = writeByte;
                                float maxSpeed22222222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                                float unused11 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime22222222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                                DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                                MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                                Message msg22222222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                                msg22222222.arg1 = 100;
                                msg22222222.what = 258;
                                DiskSettingSubMenuDialog.this.handler.sendMessage(msg22222222);
                            } catch (Throwable th9) {
                                th = th9;
                                byte[] bArr8 = writeByte;
                                Long l8 = startTime1;
                                testFile.delete();
                                throw th;
                            }
                            int i3222222222 = progress3;
                            Long endTime222222222 = Long.valueOf(System.currentTimeMillis());
                            byte[] bArr2222222222 = writeByte;
                            float maxSpeed222222222 = new BigDecimal((double) ((((((float) bufferSize) * 1000.0f) / ((float) MinTime.longValue())) / 1024.0f) / 1024.0f)).setScale(1, 4).floatValue();
                            float unused12 = DiskSettingSubMenuDialog.this.speedRate = (((((((float) bufferSize) * 900.0f) * 1000.0f) / ((float) (endTime222222222.longValue() - startTime.longValue()))) / 1024.0f) / 1024.0f) / 3.0f;
                            DiskSettingSubMenuDialog.speedList.put("/storage/0000-0000", Float.valueOf(DiskSettingSubMenuDialog.this.speedRate));
                            MtkLog.d(DiskSettingSubMenuDialog.TAG, DiskSettingSubMenuDialog.speedList.toString());
                            Message msg222222222 = DiskSettingSubMenuDialog.this.handler.obtainMessage();
                            msg222222222.arg1 = 100;
                            msg222222222.what = 258;
                            DiskSettingSubMenuDialog.this.handler.sendMessage(msg222222222);
                        } catch (FileNotFoundException e11) {
                            int i8 = index;
                            ArrayList<MountPoint> arrayList3 = mountList;
                            String str9 = subPath2;
                            byte[] bArr9 = writeByte;
                        }
                    } catch (IOException e12) {
                        int i9 = index;
                        ArrayList<MountPoint> arrayList4 = mountList;
                        String str10 = subPath2;
                        String str11 = path;
                        IOException iOException = e12;
                        e12.printStackTrace();
                    }
                } else {
                    int i10 = index;
                    ArrayList<MountPoint> arrayList5 = mountList;
                }
            }
        }).start();
        return false;
    }

    public static float getUsbSpeed(MountPoint item) {
        if (item == null || speedList == null || speedList.size() < 1) {
            return 0.0f;
        }
        String str = TAG;
        MtkLog.d(str, "MountPoint=" + item.mMountPoint);
        for (String key : speedList.keySet()) {
            if (key.equals(item.mMountPoint)) {
                return speedList.get(key).floatValue();
            }
        }
        return 0.0f;
    }

    public static void resetSpeedList(String item) {
        if (item != null && speedList != null && speedList.size() >= 1) {
            for (String key : speedList.keySet()) {
                if (key.equals(item)) {
                    speedList.remove(item);
                    return;
                }
            }
        }
    }

    public void onEvent(DeviceManagerEvent event) {
        String str = TAG;
        MtkLog.d(str, "DeviceManagerEvent event = " + event + " -- " + event.getType());
        int type = event.getType();
        if (type != 602) {
            if (type != 607) {
            }
        } else if (this.fis != null) {
            try {
                this.fis.close();
                this.fis = null;
            } catch (Exception e) {
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isFormat()) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void setFormat(boolean isFormat2) {
        isFormat = isFormat2;
    }

    public static boolean isFormat() {
        return isFormat;
    }
}
