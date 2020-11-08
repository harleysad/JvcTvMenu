package com.mediatek.wwtv.tvcenter.dvr.manager;

import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.Time;
import android.util.Log;
import com.mediatek.dm.DeviceManager;
import com.mediatek.dm.FileSystemType;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Util {
    private static final boolean PRINT_TRACE = true;
    private static final String TAG = "Util[dvr]";

    public static void showDLog(String string) {
    }

    public static void showELog(String string) {
    }

    public static void showDLog(String tag, String string) {
    }

    public static void showDLog(String tag, int string) {
    }

    public static void showELog(String tag, String string) {
    }

    public static void showELog(String tag, int string) {
    }

    public static void printStackTrace() {
        Throwable tr = new Throwable();
        Log.getStackTraceString(tr);
        tr.printStackTrace();
    }

    public static String getWeek(int week) {
        switch (week) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Sunday";
        }
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
        MtkLog.d(TAG, "time in year:" + timeFormat.toMillis());
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
        MtkLog.e(TAG, "strToDate:" + str);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            Date date = format.parse(str.toString());
            MtkLog.e(TAG, "sItem:strToDate:" + date);
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
        MtkLog.e(TAG, "strToTime:" + str);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        try {
            return format.parse(str.toString());
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getDateTime(String str) {
        Calendar calendar = Calendar.getInstance();
        String str2 = calendar.get(5) + "";
        String str3 = (calendar.get(1) + "") + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + ((calendar.get(2) + 1) + "") + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + (calendar.get(5) + "") + "/" + str;
        MtkLog.e(TAG, "sItem:getDateTime" + str3);
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
        MtkLog.d(TAG, "longStrToDateStr," + format.format(date));
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

    public static String longToHrMin(Long time) {
        Date date = new Date(time.longValue());
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.format("%dhr %dmin", new Object[]{Integer.valueOf(ca.get(11)), Integer.valueOf(ca.get(12))});
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

    /* JADX WARNING: Removed duplicated region for block: B:47:0x01aa  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01bb  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x01c6  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:43:0x01a0=Splitter:B:43:0x01a0, B:51:0x01b1=Splitter:B:51:0x01b1} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static float speedTest(com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r26) {
        /*
            boolean r0 = r26.hasRemovableDisk()
            if (r0 != 0) goto L_0x0009
            r0 = -1082130432(0xffffffffbf800000, float:-1.0)
            return r0
        L_0x0009:
            double r0 = java.lang.Math.random()
            r2 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r0 = r0 * r2
            int r1 = (int) r0
            com.mediatek.dm.MountPoint r0 = r26.getPvrMountPoint()
            r2 = 0
            if (r0 != 0) goto L_0x0035
            java.util.ArrayList r0 = r26.getDeviceList()
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0035
            java.util.ArrayList r0 = r26.getDeviceList()
            java.lang.Object r0 = r0.get(r2)
            com.mediatek.dm.MountPoint r0 = (com.mediatek.dm.MountPoint) r0
            r3 = r26
            r3.setPvrMountPoint(r0)
            goto L_0x0037
        L_0x0035:
            r3 = r26
        L_0x0037:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            com.mediatek.dm.MountPoint r4 = r26.getPvrMountPoint()
            java.lang.String r4 = r4.mMountPoint
            r0.append(r4)
            java.lang.String r4 = "/speedTest%d.dat"
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r1)
            r5[r2] = r6
            java.lang.String r5 = java.lang.String.format(r0, r5)
            java.lang.String r0 = "Util[dvr]"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "speedTest,"
            r6.append(r7)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r6)
            java.io.File r0 = new java.io.File
            r0.<init>(r5)
            r6 = r0
            r7 = 0
            r8 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            boolean r0 = r6.exists()
            if (r0 == 0) goto L_0x008a
            r6.delete()
        L_0x008a:
            r9 = 0
            r6.createNewFile()     // Catch:{ IOException -> 0x01ca }
            r10 = 122880(0x1e000, float:1.72192E-40)
            r11 = 300(0x12c, double:1.48E-321)
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            r12 = r11
            byte[] r0 = new byte[r10]
            r13 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
            r0.<init>(r6)     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
            long r14 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
            java.lang.Long r14 = java.lang.Long.valueOf(r14)     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
            r2 = 0
            java.lang.Long r15 = java.lang.Long.valueOf(r2)     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
            java.lang.Long r17 = java.lang.Long.valueOf(r2)     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
        L_0x00b3:
            long r18 = r12.longValue()     // Catch:{ FileNotFoundException -> 0x01ae, IOException -> 0x019d, all -> 0x0199 }
            int r18 = (r18 > r2 ? 1 : (r18 == r2 ? 0 : -1))
            if (r18 <= 0) goto L_0x011b
            r20 = r5
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            r15 = r4
            r0.write(r13)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r17 = r8.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r21 = r4.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r23 = r15.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r21 = r21 - r23
            int r5 = (r17 > r21 ? 1 : (r17 == r21 ? 0 : -1))
            if (r5 <= 0) goto L_0x0100
            long r17 = r4.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r21 = r15.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r17 = r17 - r21
            int r5 = (r17 > r2 ? 1 : (r17 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x0100
            long r17 = r4.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r21 = r15.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            long r2 = r17 - r21
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            r8 = r2
        L_0x0100:
            long r2 = r12.longValue()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            r17 = 1
            long r2 = r2 - r17
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            r12 = r2
            r17 = r4
            r5 = r20
            r2 = 0
            r4 = 1
            goto L_0x00b3
        L_0x0115:
            r0 = move-exception
            goto L_0x01a0
        L_0x0118:
            r0 = move-exception
            goto L_0x01b1
        L_0x011b:
            r20 = r5
            r0.close()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0115 }
            boolean r2 = r6.exists()
            if (r2 == 0) goto L_0x0129
            r6.delete()
        L_0x0129:
            r2 = r14
            long r3 = java.lang.System.currentTimeMillis()
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            float r4 = (float) r10
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 * r5
            long r14 = r8.longValue()
            float r5 = (float) r14
            float r4 = r4 / r5
            r5 = 1149239296(0x44800000, float:1024.0)
            float r4 = r4 / r5
            float r4 = r4 / r5
            java.math.BigDecimal r5 = new java.math.BigDecimal
            double r14 = (double) r4
            r5.<init>(r14)
            r7 = 4
            r9 = 1
            java.math.BigDecimal r5 = r5.setScale(r9, r7)
            float r4 = r5.floatValue()
            java.io.PrintStream r5 = java.lang.System.out
            java.lang.String r7 = "MaxSpeed:%3.1f MB/s "
            java.lang.Object[] r14 = new java.lang.Object[r9]
            java.lang.Float r9 = java.lang.Float.valueOf(r4)
            r15 = 0
            r14[r15] = r9
            java.lang.String r7 = java.lang.String.format(r7, r14)
            r5.println(r7)
            long r14 = (long) r10
            long r17 = r11.longValue()
            long r14 = r14 * r17
            r17 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 * r17
            long r17 = r3.longValue()
            long r21 = r2.longValue()
            long r17 = r17 - r21
            long r14 = r14 / r17
            r17 = 1024(0x400, double:5.06E-321)
            long r14 = r14 / r17
            long r14 = r14 / r17
            float r5 = (float) r14
            java.io.PrintStream r7 = java.lang.System.out
            java.lang.String r9 = "average:%3.1f MB/s "
            r14 = 1
            java.lang.Object[] r14 = new java.lang.Object[r14]
            java.lang.Float r15 = java.lang.Float.valueOf(r5)
            r16 = 0
            r14[r16] = r15
            java.lang.String r9 = java.lang.String.format(r9, r14)
            r7.println(r9)
            return r5
        L_0x0199:
            r0 = move-exception
            r20 = r5
            goto L_0x01c0
        L_0x019d:
            r0 = move-exception
            r20 = r5
        L_0x01a0:
            r0.printStackTrace()     // Catch:{ all -> 0x01bf }
            boolean r2 = r6.exists()
            if (r2 == 0) goto L_0x01ad
            r6.delete()
        L_0x01ad:
            return r9
        L_0x01ae:
            r0 = move-exception
            r20 = r5
        L_0x01b1:
            r0.printStackTrace()     // Catch:{ all -> 0x01bf }
            boolean r2 = r6.exists()
            if (r2 == 0) goto L_0x01be
            r6.delete()
        L_0x01be:
            return r9
        L_0x01bf:
            r0 = move-exception
        L_0x01c0:
            boolean r2 = r6.exists()
            if (r2 == 0) goto L_0x01c9
            r6.delete()
        L_0x01c9:
            throw r0
        L_0x01ca:
            r0 = move-exception
            r20 = r5
            r2 = r0
            r0.printStackTrace()
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.dvr.manager.Util.speedTest(com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager):float");
    }

    public static String getGBSizeOfDisk(MountPoint mp) {
        if (mp == null) {
            return String.format("No Device", new Object[0]);
        }
        return String.format(mp.mVolumeLabel + ": %.1f/%.1f GB", new Object[]{Float.valueOf((((float) mp.mFreeSize) / 1024.0f) / 1024.0f), Float.valueOf((((float) mp.mTotalSize) / 1024.0f) / 1024.0f)});
    }

    public static void longToDate(Long time) {
        dataFormat(new Date(time.longValue()));
    }

    public static void dataFormat(Date date) {
        MtkLog.e(TAG, new SimpleDateFormat("HH:mm:ss,EEEEEEEE,yyyy/MM/dd").format(date));
    }

    public static String[] covertFreeSizeToArray(boolean auto, Long freeSize) {
        long fsize = freeSize.longValue();
        if (fsize < 0) {
            fsize = -fsize;
        }
        int count = (int) (fsize / PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED);
        MtkLog.e(TAG, "FreeSize:" + fsize);
        if (count <= 1) {
            MtkLog.e(TAG, "There is not enough space.");
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

    public static boolean fomatDisk() {
        try {
            DeviceManager dm = DeviceManager.getInstance();
            ArrayList<MountPoint> mps = dm.getMountPointList();
            if (mps != null) {
                if (mps.size() > 0) {
                    String extern_sd = mps.get(0).mDeviceName;
                    MtkLog.e(TAG, "mp:" + mps.get(0).mDeviceName + ", usb_path:" + extern_sd);
                    MtkLog.e(TAG, "unmountVolume.");
                    dm.umountVol(extern_sd);
                    Thread.sleep(4000);
                    MtkLog.e(TAG, "start formatVolume");
                    int result_format = dm.formatVol(FileSystemType.FS_FAT32, extern_sd);
                    MtkLog.e(TAG, "format result_:" + result_format);
                    if (result_format != 0) {
                        return true;
                    }
                    Thread.sleep(4000);
                    dm.mountVol(extern_sd);
                    MtkLog.e(TAG, "result_mount:");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            MtkLog.e(TAG, "Exception:" + e.getMessage());
            return false;
        }
    }

    public static int getTVHeight() {
        return ScreenConstant.SCREEN_HEIGHT;
    }

    public static int getTVWidth() {
        return ScreenConstant.SCREEN_WIDTH;
    }
}
