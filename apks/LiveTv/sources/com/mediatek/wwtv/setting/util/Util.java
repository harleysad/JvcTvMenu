package com.mediatek.wwtv.setting.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.Time;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.FileSystemType;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.MtkTvTimeshift;
import com.mediatek.wwtv.setting.widget.view.DiskSettingSubMenuDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.Core;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Util {
    private static final String TAG = "TimeShift_PVR";
    public static final String TALKBACK_SERVICE = "com.google.android.marvin.talkback/.TalkBackService";

    public static void showELog(String string) {
    }

    public static void showDLog(String string) {
    }

    public static void showDLog(String tag, String string) {
    }

    public static void showDLog(String tag, int string) {
    }

    public static void showELog(String tag, String string) {
    }

    public static void showELog(String tag, int string) {
    }

    public static String secondToString(int second) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        date.setTime((long) (second * 1000));
        return format.format(date);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    public static String formatCurrentTime() {
        MtkTvTimeFormatBase timeFormat = MtkTvTime.getInstance().getLocalTime();
        MtkLog.d("timeFormat", "time in year:" + timeFormat.toMillis());
        String hour = timeFormat.hour + "";
        String min = timeFormat.minute + "";
        String sec = timeFormat.second + "";
        if (timeFormat.hour < 10) {
            hour = "0" + hour;
        }
        if (timeFormat.minute < 10) {
            min = "0" + min;
        }
        if (timeFormat.second < 10) {
            sec = "0" + sec;
        }
        return hour + ":" + min + ":" + sec;
    }

    public static String dateToStringYMD(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date);
    }

    public static String dateToStringYMD2(Date date) {
        return new SimpleDateFormat("yyyyMMdd_HHmm").format(date);
    }

    public static String dateToStringYMD3(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd/HH:mm", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String timeMillisToChar() {
        String str = Long.toString(System.currentTimeMillis());
        return str.substring(str.length() - 4);
    }

    public static Date strToDate(String str) {
        MtkLog.e("strToDate", "strToDate:" + str);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            Date date = format.parse(str.toString());
            MtkLog.e("setItemValue", "sItem:strToDate:" + date);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static long dateToMills(Date date) {
        Time time = new Time();
        time.set(date.getSeconds(), date.getMinutes(), date.getHours(), date.getDay(), date.getMonth(), date.getYear());
        return Long.valueOf(time.toMillis(true)).longValue();
    }

    public static Date strToTime(String str) {
        MtkLog.e("strToTime", "strToTime:" + str);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(str.toString());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String longStrToDateStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(new Date(1000 * time));
    }

    public static Date getDateTime(String str) {
        Calendar calendar = Calendar.getInstance();
        String str2 = calendar.get(5) + "";
        String str3 = (calendar.get(1) + "") + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + ((calendar.get(2) + 1) + "") + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + (calendar.get(5) + "") + "/" + str;
        MtkLog.e("getDateTime", "sItem:getDateTime" + str3);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(str3.toString());
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getDateTime(long time) {
        Date date = new Date(time);
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(format.format(gCalendar.getTime()).toString());
        } catch (ParseException e) {
            return null;
        }
    }

    public static int strToSecond(String str) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            Date date = format.parse(str.toString());
            return (date.getHours() * MtkTvTimeFormatBase.SECONDS_PER_HOUR) + (date.getMinutes() * 60) + date.getSeconds();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String longStrToDateStr(String str) {
        Date date = new Date(Long.parseLong(str));
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        showDLog(format.format(date));
        return format.format(date);
    }

    public static String longStrToTimeStr(String str) {
        Date date = new Date(Long.parseLong(str));
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String longStrToTimeStr(Long msTime) {
        Date date = new Date(msTime.longValue());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    public static String longStrToTimeStrN(Long msTime) {
        MtkTvTimeFormatBase from = new MtkTvTimeFormatBase();
        MtkTvTimeFormatBase to = new MtkTvTimeFormatBase();
        from.setByUtc(msTime.longValue() / 1000);
        new MtkTvTimeBase().convertTime(4, from, to);
        Date date = new Date(to.toSeconds() * 1000);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format.format(date);
    }

    public static String longToHrMin(Long time) {
        Date date = new Date(time.longValue());
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.format("%dhr %dmin", new Object[]{Integer.valueOf(ca.get(11)), Integer.valueOf(ca.get(12))});
    }

    public static String longToHrMinN(Long time) {
        if (time.longValue() < 0) {
            return "0hr 0min";
        }
        return String.format("%dhr %dmin", new Object[]{Long.valueOf(time.longValue() / 3600), Long.valueOf((time.longValue() % 3600) / 60)});
    }

    public static long strToTime(String str, int i) {
        MtkLog.e("strToTime", "strToTime:" + str);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(str.toString()).getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    public static long strToTimeEx(String str, int i) {
        MtkLog.e("strToTime", "strToTime:" + str);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        try {
            return format.parse(str.toString()).getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String timeToDateStringEx(long timeSc, int i) {
        MtkLog.d("timeToStringEx", "timeToStringEx:" + timeSc);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String date = null;
        try {
            date = format.format(Long.valueOf(timeSc));
        } catch (Exception e) {
        }
        MtkLog.d("timeToStringEx", "date = " + date);
        return date;
    }

    public static String timeToTimeStringEx(long timeSc, int i) {
        MtkLog.d("timeToTimeStringEx", "timeToTimeStringEx:" + timeSc);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String time = null;
        try {
            time = format.format(Long.valueOf(timeSc));
        } catch (Exception e) {
        }
        MtkLog.d("timeToTimeStringEx", "time = " + time);
        return time;
    }

    public static Date addDateAndTime(Date date1, Date date2) {
        Date newDate = new Date();
        newDate.setYear(date1.getYear());
        newDate.setMonth(date1.getMonth());
        newDate.setDate(date1.getDate());
        newDate.setHours(date2.getHours());
        newDate.setMinutes(date2.getMinutes());
        newDate.setSeconds(date2.getSeconds());
        return newDate;
    }

    public static String getGBSizeOfDisk(MountPoint mp) {
        if (mp == null) {
            return String.format(TurnkeyUiMainActivity.getInstance().getResources().getString(R.string.dvr_device_no), new Object[0]);
        }
        try {
            StatFs stat = new StatFs(mp.mMountPoint);
            MtkLog.d(TAG, "tSize=" + stat.getTotalBytes() + " fSize=" + stat.getAvailableBytes());
            return String.format("%.1f/%.1f GB", new Object[]{Float.valueOf(((float) stat.getAvailableBytes()) / 1.07374182E9f), Float.valueOf(((float) stat.getTotalBytes()) / 1.07374182E9f)});
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static CharSequence getIsTshift(MountPoint item) {
        StringBuilder diskProperityStr = new StringBuilder();
        CharSequence pvrStr = isPvrDisk(item);
        CharSequence tshiftStr = isTshiftDisk(item);
        if (DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportPvr()) {
            diskProperityStr.append(pvrStr);
            if (pvrStr.length() > 0 && tshiftStr.length() > 0) {
                diskProperityStr.append("/");
            }
        }
        diskProperityStr.append(tshiftStr);
        return diskProperityStr.toString();
    }

    private static CharSequence isPvrDisk(MountPoint item) {
        String mountPoint = item.mMountPoint;
        File file = new File(mountPoint + Core.PVR_DISK_TAG);
        if (file.exists()) {
            showDLog("isPvrDisk(),true, " + file.getAbsolutePath());
            return "PVR";
        }
        showDLog("isPvrDisk(),false, " + file.getAbsolutePath());
        return "";
    }

    private static CharSequence isTshiftDisk(MountPoint item) {
        String mountPoint = item.mMountPoint;
        File file = new File(mountPoint + Core.TSHIFT_DISK_TAG);
        if (file.exists()) {
            showDLog("isTshiftDisk(),true, " + file.getAbsolutePath());
            return "TSHIFT";
        }
        showDLog("isTshiftDisk(),false, " + file.getAbsolutePath());
        return "";
    }

    public static void dataFormat(Date date) {
        System.out.println(new SimpleDateFormat("HH:mm:ss,EEEEEEEE,yyyy/MM/dd").format(date));
    }

    public static void longToDate(Long time) {
        dataFormat(new Date(time.longValue()));
    }

    public static String[] covertFreeSizeToArray(boolean auto, Long freeSize) {
        long fsize = freeSize.longValue();
        if (fsize < 0) {
            fsize = -fsize;
        }
        int count = (int) (fsize / PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED);
        showDLog("FreeSize:" + fsize);
        if (count <= 1) {
            showELog("There is not enough space.");
            return null;
        }
        if (count >= 7 && auto) {
            count = 7;
        }
        String[] list = new String[count];
        float size = 500.0f;
        for (int i = 0; i < list.length; i++) {
            if (size < 1000.0f) {
                list[i] = String.format("%dMB", new Object[]{512});
            } else {
                list[i] = String.format("%.1fGB", new Object[]{Float.valueOf(size / 1000.0f)});
            }
            size += 500.0f;
        }
        return list;
    }

    public static boolean makeDIR(String path) {
        File folder = new File(path.toString());
        if ((!folder.exists() || !folder.isDirectory()) && !folder.mkdir()) {
            return false;
        }
        return true;
    }

    public static void tempSetPVR(String diskPath) {
        String changeDeleteFile = fixDiskPath(diskPath);
        File file = new File(changeDeleteFile + Core.PVR_DISK_TAG);
        boolean setSuccessFul = false;
        if (!file.exists()) {
            try {
                setSuccessFul = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showDLog("tempSetPVR(),setSuccessFul:" + setSuccessFul);
            return;
        }
        showDLog("tempSetPVR(),file.exists():" + changeDeleteFile);
    }

    public static void tempSetTHIFT(String diskPath) {
        String changeDeleteFile = fixDiskPath(diskPath);
        File file = new File(changeDeleteFile + Core.TSHIFT_DISK_TAG);
        boolean setSuccessFul = false;
        if (!file.exists()) {
            try {
                setSuccessFul = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showDLog("tempSetTHIFT(),setSuccessFul:" + setSuccessFul);
            return;
        }
        showDLog("tempSetTHIFT(),file.exists():" + diskPath);
    }

    public static boolean tempIsTSHIFT(String diskPath) {
        if (new File(diskPath + Core.TSHIFT_DISK_TAG).exists()) {
            return true;
        }
        return false;
    }

    public static boolean tempIsPVR(String diskPath) {
        if (new File(diskPath + Core.PVR_DISK_TAG).exists()) {
            return true;
        }
        return false;
    }

    public static void tempDelTSHIFT(MountPoint point, StorageManager storageManager) {
        String changeDeleteFile = fixDiskPath(point.mMountPoint);
        MtkLog.d(TAG, "deleteSelectedFile,changeDeleteFile=" + changeDeleteFile);
        File tshiftTag = new File(changeDeleteFile + Core.TSHIFT_DISK_TAG);
        File tshiftDir = new File(changeDeleteFile + Core.TSHIFT_DIR_DISK);
        if (tshiftTag.exists()) {
            MtkLog.d(TAG, "deletetime(),delete timeshift tag file setSuccessFul:" + tshiftTag.delete());
        }
        MtkLog.d(TAG, "deletetime(),delete dir exists: " + tshiftDir.exists() + ", isDirectory: " + tshiftDir.isDirectory());
        if (tshiftDir.exists() && tshiftDir.isDirectory()) {
            for (File f : tshiftDir.listFiles()) {
                f.delete();
            }
            boolean setSuccessFul2 = tshiftDir.delete();
            MtkLog.d(TAG, "deletetime(),delete timeshift directory setSuccessFul:" + setSuccessFul2);
            if (setSuccessFul2) {
                TifTimeShiftManager.getInstance().stop();
                TifTimeShiftManager.getInstance().stopAll();
                MtkTvTimeshift.getInstance().setAutoRecord(false);
                MtkTvTimeshift.getInstance().setAutoRecord(true);
                unmountAndMountDisk(point.mDeviceName, storageManager);
            }
        }
    }

    private static void unmountAndMountDisk(final String diskPath, final StorageManager storageManager) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    MtkLog.d("unmountAndMountDisk", "usb_path:" + diskPath);
                    MtkLog.d("unmountAndMountDisk", "start unmountVolume.");
                    storageManager.unmount(diskPath);
                    MtkLog.d("unmountAndMountDisk", "end unmountVolume.");
                    Thread.sleep(1000);
                    storageManager.mount(diskPath);
                    MtkLog.d("unmountAndMountDisk", "result_mount:");
                } catch (Exception e) {
                    MtkLog.e("unmountAndMountDisk", "Exception:" + e.getMessage());
                }
            }
        }).start();
    }

    public static void tempDelPVR(String diskPath) {
        String changeDeleteFile = fixDiskPath(diskPath);
        File file1 = new File(changeDeleteFile + Core.PVR_DISK_TAG);
        boolean setSuccessFul = false;
        if (file1.exists()) {
            try {
                setSuccessFul = file1.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            showDLog("delete(),setSuccessFul:" + setSuccessFul);
        }
    }

    private static String fixDiskPath(String diskPath) {
        String subPath = diskPath.substring(9, diskPath.length());
        return "/mnt/media_rw/" + subPath;
    }

    public static void showRecordToast(Activity activity) {
    }

    public static void commonAimationIn(Context context, View view, int animID) {
        Animation anim = AnimationUtils.loadAnimation(context, animID);
        anim.setFillAfter(true);
        anim.setFillBefore(false);
        anim.setRepeatCount(-1);
        view.startAnimation(anim);
        showDLog("commonAimationIn:" + view.getId());
    }

    public static TranslateAnimation commonAimationIn(Context context, float fromX, float endX, Long duration) {
        TranslateAnimation anim = new TranslateAnimation(fromX, endX, 0.0f, 0.0f);
        anim.setFillAfter(true);
        anim.setFillBefore(true);
        anim.setDuration(duration.longValue());
        anim.setRepeatCount(-1);
        return anim;
    }

    public static TranslateAnimation commonAimationIn(Context context, float fromX, Long duration) {
        TranslateAnimation anim = new TranslateAnimation(fromX, -500.0f, 0.0f, 0.0f);
        anim.setFillAfter(true);
        anim.setFillBefore(true);
        anim.setDuration(duration.longValue());
        anim.setRepeatCount(-1);
        return anim;
    }

    public static TranslateAnimation commonAimationIn(Context context, float offsetX) {
        TranslateAnimation anim = new TranslateAnimation(0.0f, offsetX, 0.0f, 0.0f);
        showDLog("commonAimationIn,offsetX: " + offsetX);
        anim.setFillAfter(true);
        anim.setFillBefore(true);
        anim.setDuration(0);
        anim.setRepeatCount(-1);
        return anim;
    }

    public static boolean fomatDisk(MountPoint mountPoint) {
        try {
            DeviceManager dm = DeviceManager.getInstance();
            if (mountPoint == null) {
                return false;
            }
            String extern_sd = mountPoint.mDeviceName;
            MtkLog.e("fomatDisk", "label:" + mountPoint.mVolumeLabel);
            MtkLog.e("fomatDisk", "mp:" + mountPoint.mMountPoint);
            MtkLog.e("fomatDisk", "devname:" + mountPoint.mDeviceName);
            MtkLog.e("fomatDisk", "usb_path:" + extern_sd);
            MtkLog.e("fomatDisk", "start unmountVolume.");
            DiskSettingSubMenuDialog.setFormat(true);
            dm.umountVol(extern_sd);
            DiskSettingSubMenuDialog.setFormat(false);
            MtkLog.e("fomatDisk", "end unmountVolume.");
            Thread.sleep(4000);
            MtkLog.e("fomatDisk", "start formatVolume");
            int result_format = dm.formatVol(FileSystemType.FS_FAT32, mountPoint.mDeviceName);
            MtkLog.e("fomatDisk", "end formatVolume");
            MtkLog.e("fomatDisk", "format result_:" + result_format);
            if (result_format == 0) {
                Thread.sleep(4000);
                dm.mountVol(extern_sd);
                MtkLog.e("fomatDisk", "result_mount:");
            }
            return true;
        } catch (Exception e) {
            MtkLog.e("fomatDisk", "Exception:" + e.getMessage());
            return false;
        }
    }

    public static void removeTshiftTag(String diskPath) {
        showDLog("removeTshiftTag(),diskPath:?," + diskPath);
        new File(diskPath + Core.TSHIFT_DISK_TAG).delete();
    }

    public static boolean isTTSEnabled(Context context) {
        List<AccessibilityServiceInfo> enableServices = ((AccessibilityManager) context.getSystemService(PartnerSettingsConfig.ATTR_DEVICE_ACCESSIBILITY)).getEnabledAccessibilityServiceList(-1);
        for (int i = 0; i < enableServices.size(); i++) {
            if (enableServices.get(i).getId().contains("com.google.android.marvin.talkback/.TalkBackService")) {
                return true;
            }
        }
        return false;
    }

    public static byte[] stringToByte(String s) {
        byte[] b = new byte[3];
        byte[] bytes = s.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(i + "  = " + Integer.toBinaryString(bytes[i] - 48));
        }
        b[0] = (byte) (((bytes[0] - 48) * 16) | (bytes[1] - 48));
        b[1] = (byte) ((bytes[3] - 48) | ((bytes[2] - 48) * 16));
        b[2] = (byte) (((bytes[4] - 48) * 16) | 15);
        return b;
    }

    public static String formatTime24_12(String strTime) {
        MtkLog.d(TAG, "formatTime24_12-----> strTime=" + strTime);
        String formatTime = "";
        try {
            formatTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new SimpleDateFormat("HH:mm").parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MtkLog.d(TAG, "formatTime24_12-----> formatTime=" + formatTime);
        return formatTime;
    }

    public static String formatTime12_24(String strTime) {
        String formatTime = "";
        try {
            formatTime = new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MtkLog.d(TAG, "formatTime12_24-----> formatTime=" + formatTime);
        return formatTime;
    }
}
