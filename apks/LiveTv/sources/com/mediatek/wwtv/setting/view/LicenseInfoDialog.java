package com.mediatek.wwtv.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.io.File;
import java.io.FileInputStream;

public class LicenseInfoDialog extends Dialog {
    private static String TAG = "LicenseInfoView";
    /* access modifiers changed from: private */
    public int PAGENUM = 1;
    /* access modifiers changed from: private */
    public int PAGETEXTCOUNT = 1600;
    public int height = 0;
    boolean isPositionView = false;
    WindowManager.LayoutParams lp;
    private Action mAction;
    private Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentPage;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LicenseInfoDialog.this.vLicenseInfo.setText(LicenseInfoDialog.this.noticeString);
            int i = 1;
            int unused = LicenseInfoDialog.this.mCurrentPage = 1;
            LicenseInfoDialog licenseInfoDialog = LicenseInfoDialog.this;
            int length = LicenseInfoDialog.this.noticeString.length() / LicenseInfoDialog.this.PAGETEXTCOUNT;
            if (LicenseInfoDialog.this.noticeString.length() % LicenseInfoDialog.this.PAGETEXTCOUNT == 0) {
                i = 0;
            }
            int unused2 = licenseInfoDialog.PAGENUM = length + i;
            TextView access$400 = LicenseInfoDialog.this.vPageNum;
            access$400.setText(LicenseInfoDialog.this.mCurrentPage + "/" + LicenseInfoDialog.this.PAGENUM);
            MtkLog.d("LicenseInfoView", "PAGENUM:" + LicenseInfoDialog.this.PAGENUM + "noticeString.length():" + LicenseInfoDialog.this.noticeString.length());
        }
    };
    private ViewGroup mRootView;
    private TVContent mTVContent;
    String noticeString = "";
    /* access modifiers changed from: private */
    public TextView vLicenseInfo;
    /* access modifiers changed from: private */
    public TextView vPageNum;
    public int width = 0;
    Window window;
    private int xOff;
    private int yOff;

    public LicenseInfoDialog(Context context) {
        super(context, 2131755419);
        this.mContext = context;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_license_info);
        this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.45d);
        this.lp.width = this.width;
        this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.85d);
        this.lp.height = this.height;
        this.lp.x = 0 - (this.lp.width / 3);
        this.window.setAttributes(this.lp);
        init();
    }

    public void setPositon(int xoff, int yoff) {
        Window window2 = getWindow();
        WindowManager.LayoutParams lp2 = window2.getAttributes();
        lp2.x = xoff;
        lp2.y = yoff;
        this.xOff = xoff;
        this.yOff = yoff;
        window2.setAttributes(lp2);
    }

    public void init() {
        this.mTVContent = TVContent.getInstance(this.mContext);
        this.vLicenseInfo = (TextView) findViewById(R.id.common_license_name);
        this.vPageNum = (TextView) findViewById(R.id.common_pagenum);
        getLicenseInfo();
    }

    public void getLicenseInfo() {
        new Thread() {
            public void run() {
                super.run();
                LicenseInfoDialog.this.noticeString = "";
                LicenseInfoDialog.this.reshFile("/");
                LicenseInfoDialog.this.mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 21) {
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onKeyLeft() {
        handleKey(21);
    }

    public void onKeyRight() {
        handleKey(22);
    }

    public void handleKey(int keycode) {
        switch (keycode) {
            case 21:
                prePage();
                return;
            case 22:
                nextPage();
                return;
            default:
                return;
        }
    }

    private void nextPage() {
        if (this.PAGENUM != 0) {
            this.mCurrentPage++;
            if (this.mCurrentPage > this.PAGENUM) {
                this.mCurrentPage = this.PAGENUM;
            }
            int end = this.mCurrentPage * this.PAGETEXTCOUNT;
            if (end > this.noticeString.length()) {
                end = this.noticeString.length();
            }
            this.vLicenseInfo.setText(this.noticeString.substring((this.mCurrentPage - 1) * this.PAGETEXTCOUNT, end));
            this.vPageNum.setText(this.mCurrentPage + "/" + this.PAGENUM);
        }
    }

    private void prePage() {
        if (this.PAGENUM != 0) {
            this.mCurrentPage--;
            if (this.mCurrentPage < 1) {
                this.mCurrentPage = 1;
            }
            int end = this.mCurrentPage * this.PAGETEXTCOUNT;
            if (end > this.noticeString.length()) {
                end = this.noticeString.length();
            }
            this.vLicenseInfo.setText(this.noticeString.substring((this.mCurrentPage - 1) * this.PAGETEXTCOUNT, end));
            this.vPageNum.setText(this.mCurrentPage + "/" + this.PAGENUM);
        }
    }

    /* access modifiers changed from: private */
    public void reshFile(String path) {
        File[] arrFiles = new File(path).listFiles();
        if (arrFiles != null) {
            for (int i = 0; i < arrFiles.length; i++) {
                if (arrFiles[i] != null && arrFiles[i].isDirectory() && !arrFiles[i].getPath().startsWith("/sys") && !arrFiles[i].getPath().startsWith("/proc")) {
                    String str = TAG;
                    MtkLog.d(str, "arrFiles[i].getPath():" + arrFiles[i].getPath());
                    reshFile(arrFiles[i].getPath());
                } else if (arrFiles[i].isFile() && arrFiles[i].getName().contains("NOTICE")) {
                    String str2 = TAG;
                    MtkLog.d(str2, " maybe licence ->file :" + arrFiles[i].getName());
                    if (arrFiles[i].getName().endsWith(".txt")) {
                        String str3 = TAG;
                        MtkLog.d(str3, "NOTICE file Name" + arrFiles[i].getPath());
                        readFile(arrFiles[i]);
                        String str4 = TAG;
                        MtkLog.d(str4, "NOTICE noticeString:" + this.noticeString);
                    }
                }
            }
        }
    }

    private void readFile(File file) {
        String str;
        StringBuilder sb;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            byte[] buffer = new byte[fin.available()];
            this.noticeString += new String(buffer, "UTF-8");
            Log.d("readFile", "error==" + this.noticeString + fin.read(buffer));
            try {
                fin.close();
                return;
            } catch (Exception e) {
                e = e;
                e.printStackTrace();
                str = "readFile";
                sb = new StringBuilder();
            }
            sb.append("error==");
            sb.append(e.getMessage());
            MtkLog.d(str, sb.toString());
        } catch (Exception e2) {
            e2.printStackTrace();
            MtkLog.d("readFile", "error==" + e2.getMessage());
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e3) {
                    e = e3;
                    e.printStackTrace();
                    str = "readFile";
                    sb = new StringBuilder();
                }
            }
        } catch (Throwable th) {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                    MtkLog.d("readFile", "error==" + e4.getMessage());
                }
            }
            throw th;
        }
    }
}
