package com.mediatek.mtkaudiopatchmanager;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.media.AudioDevicePort;
import android.media.AudioGain;
import android.media.AudioGainConfig;
import android.media.AudioManager;
import android.media.AudioPatch;
import android.media.AudioPortConfig;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MtkAudioPatchManager {
    private static final int BLUETOOTH_CONNECTED = 1;
    private static final int BLUETOOTH_DISCONNECTED = 2;
    private static final int DISABLED = 0;
    private static final int ENABLED = 1;
    private static final int HDMI_POWER_OFF = 4;
    private static final int HDMI_POWER_ON = 3;
    private static final int HEADPHONES_CONNECTED = 5;
    private static final int HEADPHONES_DISCONNECTED = 6;
    /* access modifiers changed from: private */
    public static final String TAG = MtkAudioPatchManager.class.getSimpleName();
    private Object MtkAudioManager = null;
    private final AudioManager mAudioManager;
    private AudioPatch mAudioPatch = null;
    private AudioDevicePort mAudioSink = null;
    List<AudioDevicePort> mAudioSinks = new ArrayList();
    private AudioDevicePort mAudioSource = null;
    /* access modifiers changed from: private */
    public boolean mBluetoothStatus = false;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentIndex = 0;
    private int mCurrentMaxIndex = 0;
    private int mDesiredChannelMask = 1;
    private int mDesiredFormat = 1;
    private int mDesiredSamplingRate = 0;
    /* access modifiers changed from: private */
    public DeviceHandler mHandler = null;
    private final HdmiSystemAudioModeChangeListener mHdmiAudioModeListener;
    private IHdmiControlService mHdmiControlService;
    private final HdmiObserver mHdmiObserver;
    /* access modifiers changed from: private */
    public boolean mHdmiPowerOffStatus = false;
    /* access modifiers changed from: private */
    public boolean mHdmiPowerOnStatus = false;
    /* access modifiers changed from: private */
    public boolean mHdmiStatus = false;
    /* access modifiers changed from: private */
    public boolean mHpStatus = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x01e6, code lost:
            if (r2.equals("android.media.STREAM_MUTE_CHANGED_ACTION") == false) goto L_0x01f3;
         */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01f7  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0210  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0219  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r17, android.content.Intent r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "onReceive action: "
                r3.append(r4)
                java.lang.String r4 = r18.getAction()
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Log.d(r2, r3)
                java.lang.String r2 = r18.getAction()
                java.lang.String r3 = "android.media.STREAM_DEVICES_CHANGED_ACTION"
                boolean r3 = r2.equals(r3)
                r4 = 3
                r5 = 50
                r7 = 1
                r8 = -1
                if (r3 == 0) goto L_0x00b8
                java.lang.String r3 = "android.media.EXTRA_VOLUME_STREAM_DEVICES"
                int r3 = r1.getIntExtra(r3, r8)
                java.lang.String r9 = "android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES"
                int r9 = r1.getIntExtra(r9, r8)
                java.lang.String r10 = "android.media.EXTRA_VOLUME_STREAM_TYPE"
                int r10 = r1.getIntExtra(r10, r8)
                java.lang.String r11 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                java.lang.String r13 = "onReceive device:"
                r12.append(r13)
                r12.append(r3)
                java.lang.String r13 = "  preDevice:"
                r12.append(r13)
                r12.append(r9)
                java.lang.String r13 = "  streamType:"
                r12.append(r13)
                r12.append(r10)
                java.lang.String r12 = r12.toString()
                android.util.Log.i(r11, r12)
                if (r10 != r4) goto L_0x00b8
                r11 = 2
                if (r3 != r11) goto L_0x0093
                r12 = r9 & 896(0x380, float:1.256E-42)
                if (r12 == 0) goto L_0x0093
                java.lang.String r4 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r5 = "BLUETOOTH_STATE_DISCONNECTED Do Disconnect"
                android.util.Log.d(r4, r5)
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r4 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r4 = r4.mHandler
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r5 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r5 = r5.mHandler
                android.os.Message r5 = android.os.Message.obtain(r5, r11)
                r6 = 10
                r4.sendMessageDelayed(r5, r6)
                return
            L_0x0093:
                r11 = 1073741824(0x40000000, float:2.0)
                if (r9 == r11) goto L_0x00b8
                r11 = r3 & 896(0x380, float:1.256E-42)
                if (r11 == 0) goto L_0x00b8
                java.lang.String r4 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r8 = "BLUETOOTH_STATE_DISCONNECTED Do Connecting"
                android.util.Log.d(r4, r8)
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r4 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r4 = r4.mHandler
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r8 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r8 = r8.mHandler
                android.os.Message r7 = android.os.Message.obtain(r8, r7)
                r4.sendMessageDelayed(r7, r5)
                return
            L_0x00b8:
                java.lang.String r3 = "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x011d
                java.lang.String r3 = "android.bluetooth.adapter.extra.CONNECTION_STATE"
                int r3 = r1.getIntExtra(r3, r8)
                switch(r3) {
                    case 0: goto L_0x0113;
                    case 1: goto L_0x0109;
                    case 2: goto L_0x00ec;
                    case 3: goto L_0x00e2;
                    default: goto L_0x00c9;
                }
            L_0x00c9:
                java.lang.String r9 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r11 = "unhandled BLUETOOTH state:"
                r10.append(r11)
                r10.append(r3)
                java.lang.String r10 = r10.toString()
                android.util.Log.d(r9, r10)
                goto L_0x011d
            L_0x00e2:
                java.lang.String r9 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r10 = "BLUETOOTH_STATE_DISCONNECTING"
                android.util.Log.d(r9, r10)
                goto L_0x011d
            L_0x00ec:
                java.lang.String r9 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r10 = "BLUETOOTH_STATE_CONNECTED"
                android.util.Log.d(r9, r10)
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r9 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r9 = r9.mHandler
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r10 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r10 = r10.mHandler
                android.os.Message r10 = android.os.Message.obtain(r10, r7)
                r9.sendMessageDelayed(r10, r5)
                goto L_0x011d
            L_0x0109:
                java.lang.String r9 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r10 = "BLUETOOTH_STATE_CONNECTING"
                android.util.Log.d(r9, r10)
                goto L_0x011d
            L_0x0113:
                java.lang.String r9 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r10 = "BLUETOOTH_STATE_DISCONNECTED Do Nothing"
                android.util.Log.d(r9, r10)
            L_0x011d:
                java.lang.String r3 = "android.intent.action.HEADSET_PLUG"
                boolean r3 = r2.equals(r3)
                r9 = 0
                if (r3 == 0) goto L_0x01c1
                java.lang.String r3 = "state"
                int r3 = r1.getIntExtra(r3, r9)
                if (r3 == 0) goto L_0x0130
                r3 = r7
                goto L_0x0131
            L_0x0130:
                r3 = r9
            L_0x0131:
                java.lang.String r10 = "deviceName"
                java.lang.String r10 = r1.getStringExtra(r10)
                java.lang.String r11 = "address"
                java.lang.String r11 = r1.getStringExtra(r11)
                java.lang.String r12 = "microphone"
                int r12 = r1.getIntExtra(r12, r7)
                if (r12 == 0) goto L_0x0147
                r12 = r7
                goto L_0x0148
            L_0x0147:
                r12 = r9
            L_0x0148:
                java.lang.String r13 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r15 = "device: "
                r14.append(r15)
                r14.append(r10)
                java.lang.String r15 = " connetcted : "
                r14.append(r15)
                r14.append(r3)
                java.lang.String r15 = " address: "
                r14.append(r15)
                r14.append(r11)
                java.lang.String r15 = "hasMicrophone : "
                r14.append(r15)
                r14.append(r12)
                java.lang.String r14 = r14.toString()
                android.util.Log.d(r13, r14)
                if (r12 != 0) goto L_0x01b8
                if (r3 == 0) goto L_0x019a
                java.lang.String r13 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r14 = "headphone connected"
                android.util.Log.d(r13, r14)
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r13 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r13 = r13.mHandler
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r14 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r14 = r14.mHandler
                r15 = 5
                android.os.Message r14 = android.os.Message.obtain(r14, r15)
                r13.sendMessageDelayed(r14, r5)
                goto L_0x01c1
            L_0x019a:
                java.lang.String r13 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r14 = "headphone disconnected"
                android.util.Log.d(r13, r14)
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r13 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r13 = r13.mHandler
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r14 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager$DeviceHandler r14 = r14.mHandler
                r15 = 6
                android.os.Message r14 = android.os.Message.obtain(r14, r15)
                r13.sendMessageDelayed(r14, r5)
                goto L_0x01c1
            L_0x01b8:
                java.lang.String r5 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r6 = "unhandled HEADSET_PLUG "
                android.util.Log.d(r5, r6)
            L_0x01c1:
                java.lang.String r3 = "android.media.VOLUME_CHANGED_ACTION"
                boolean r3 = r2.equals(r3)
                if (r3 != 0) goto L_0x01d1
                java.lang.String r3 = "android.media.STREAM_MUTE_CHANGED_ACTION"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x023c
            L_0x01d1:
                int r3 = r2.hashCode()
                r5 = -1940635523(0xffffffff8c54407d, float:-1.6351292E-31)
                if (r3 == r5) goto L_0x01e9
                r5 = 1920758225(0x727c71d1, float:5.0001804E30)
                if (r3 == r5) goto L_0x01e0
                goto L_0x01f3
            L_0x01e0:
                java.lang.String r3 = "android.media.STREAM_MUTE_CHANGED_ACTION"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x01f3
                goto L_0x01f4
            L_0x01e9:
                java.lang.String r3 = "android.media.VOLUME_CHANGED_ACTION"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x01f3
                r7 = r9
                goto L_0x01f4
            L_0x01f3:
                r7 = r8
            L_0x01f4:
                switch(r7) {
                    case 0: goto L_0x0219;
                    case 1: goto L_0x0210;
                    default: goto L_0x01f7;
                }
            L_0x01f7:
                java.lang.String r3 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Unrecognized intent: "
                r4.append(r5)
                r4.append(r1)
                java.lang.String r4 = r4.toString()
                android.util.Log.w(r3, r4)
                return
            L_0x0210:
                java.lang.String r3 = "android.media.EXTRA_VOLUME_STREAM_TYPE"
                int r3 = r1.getIntExtra(r3, r8)
                if (r3 == r4) goto L_0x0237
                return
            L_0x0219:
                java.lang.String r3 = "android.media.EXTRA_VOLUME_STREAM_TYPE"
                int r3 = r1.getIntExtra(r3, r8)
                if (r3 == r4) goto L_0x0222
                return
            L_0x0222:
                java.lang.String r4 = "android.media.EXTRA_VOLUME_STREAM_VALUE"
                int r4 = r1.getIntExtra(r4, r9)
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r5 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                int r5 = r5.mCurrentIndex
                if (r4 != r5) goto L_0x0231
                return
            L_0x0231:
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r5 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                int unused = r5.mCurrentIndex = r4
            L_0x0237:
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r3 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                r3.updateVolume()
            L_0x023c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private int mSinkDevice = 0;
    private Method methodResetAudioPortGeneration = null;

    private class DeviceHandler extends Handler {
        public DeviceHandler() {
            Log.d(MtkAudioPatchManager.TAG, "start DeviceHandler");
        }

        public DeviceHandler(Looper looper) {
            super(looper);
            Log.d(MtkAudioPatchManager.TAG, "start DeviceHandler looper");
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    boolean res = MtkAudioPatchManager.this.updateBluetoothStatus();
                    if (!MtkAudioPatchManager.this.mBluetoothStatus || !res) {
                        sendMessageDelayed(Message.obtain(MtkAudioPatchManager.this.mHandler, 1), 50);
                        return;
                    } else {
                        MtkAudioPatchManager.this.updateAudioPatch(128);
                        return;
                    }
                case 2:
                    boolean res2 = MtkAudioPatchManager.this.updateBluetoothStatus();
                    if (MtkAudioPatchManager.this.mBluetoothStatus || !res2) {
                        sendMessageDelayed(Message.obtain(MtkAudioPatchManager.this.mHandler, 2), 50);
                        return;
                    } else {
                        MtkAudioPatchManager.this.updateAudioPatch(1073741824);
                        return;
                    }
                case 3:
                    boolean res3 = MtkAudioPatchManager.this.updateHdmiStatus();
                    if (!MtkAudioPatchManager.this.mHdmiStatus || !res3) {
                        sendMessageDelayed(Message.obtain(MtkAudioPatchManager.this.mHandler, 3), 50);
                        return;
                    }
                    Log.d(MtkAudioPatchManager.TAG, "handle HDMI_POWER_ON");
                    MtkAudioPatchManager.this.updateAudioPatch(262144);
                    boolean unused = MtkAudioPatchManager.this.mHdmiPowerOnStatus = false;
                    return;
                case 4:
                    if (!MtkAudioPatchManager.this.updateHdmiStatus() || MtkAudioPatchManager.this.mHdmiStatus) {
                        sendMessageDelayed(Message.obtain(MtkAudioPatchManager.this.mHandler, 4), 50);
                        return;
                    }
                    Log.d(MtkAudioPatchManager.TAG, "handle HDMI_POWER_OFF");
                    MtkAudioPatchManager.this.updateAudioPatch(1073741824);
                    boolean unused2 = MtkAudioPatchManager.this.mHdmiPowerOffStatus = false;
                    return;
                case 5:
                    boolean res4 = MtkAudioPatchManager.this.updateHpStatus();
                    if (!MtkAudioPatchManager.this.mHpStatus || !res4) {
                        Log.d(MtkAudioPatchManager.TAG, "unhandled updateHpStatus HEADPHONES_CONNECTED");
                        return;
                    } else {
                        MtkAudioPatchManager.this.updateAudioPatch(8);
                        return;
                    }
                case 6:
                    boolean res5 = MtkAudioPatchManager.this.updateHpStatus();
                    if (MtkAudioPatchManager.this.mHpStatus || !res5) {
                        Log.d(MtkAudioPatchManager.TAG, "unhandled updateHpStatus HEADPHONES_DISCONNECTED");
                        return;
                    } else {
                        MtkAudioPatchManager.this.updateAudioPatch(1073741824);
                        return;
                    }
                default:
                    throw new IllegalStateException("unhandled message: " + msg.what);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVolume() {
        int gainValue;
        this.mCurrentMaxIndex = this.mAudioManager.getStreamMaxVolume(3);
        this.mCurrentIndex = this.mAudioManager.getStreamVolume(3);
        float mStreamVolume = ((float) this.mCurrentIndex) / ((float) this.mCurrentMaxIndex);
        Log.i(TAG, "updateVolume mStreamVolume: " + mStreamVolume + " mCurrentIndex:" + this.mCurrentIndex + " mCurrentMaxIndex: " + this.mCurrentMaxIndex);
        if (this.mAudioSource != null) {
            AudioGainConfig sourceGainConfig = null;
            if (this.mAudioSource.gains().length > 0) {
                AudioGain sourceGain = null;
                AudioGain[] gains = this.mAudioSource.gains();
                int length = gains.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    AudioGain gain = gains[i];
                    if ((gain.mode() & 1) != 0) {
                        sourceGain = gain;
                        AudioGain setVolumeGain = gain;
                        break;
                    }
                    i++;
                }
                if (sourceGain != null) {
                    int steps = (sourceGain.maxValue() - sourceGain.minValue()) / sourceGain.stepValue();
                    int gainValue2 = sourceGain.minValue();
                    if (mStreamVolume < 1.0f) {
                        gainValue = gainValue2 + (sourceGain.stepValue() * ((int) (((double) (((float) steps) * mStreamVolume)) + 0.5d)));
                    } else {
                        gainValue = sourceGain.maxValue();
                    }
                    Log.d(TAG, "gainValue: " + gainValue);
                    sourceGainConfig = sourceGain.buildConfig(1, sourceGain.channelMask(), new int[]{gainValue}, 0);
                } else {
                    Log.w(TAG, "No audio source gain with MODE_JOINT support exists.");
                }
            }
            if (sourceGainConfig != null) {
                AudioManager audioManager = this.mAudioManager;
                AudioManager.setAudioPortGain(this.mAudioSource, sourceGainConfig);
                return;
            }
            Log.i(TAG, "sourceGainConfig can not create!");
            return;
        }
        Log.i(TAG, "unavailable AudioSource!");
    }

    public MtkAudioPatchManager(Context context) {
        Log.d(TAG, "MtkAudioPatchManager++");
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mHandler = new DeviceHandler(context.getMainLooper());
        this.mHdmiObserver = new HdmiObserver(this.mHandler);
        this.mHdmiAudioModeListener = new HdmiSystemAudioModeChangeListener(this.mHandler);
        this.mHdmiControlService = IHdmiControlService.Stub.asInterface(ServiceManager.getService("hdmi_control"));
        registerHdmiAudioModeListener();
        registerReceivers();
        registerContentObserver();
        try {
            this.methodResetAudioPortGeneration = Class.forName("android.media.AudioManager").getDeclaredMethod("resetAudioPortGeneration", (Class[]) null);
            this.methodResetAudioPortGeneration.setAccessible(true);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "android.media.AudioManager NOT Found");
        } catch (NoSuchMethodException e2) {
            Log.e(TAG, "NO func resetAudioPortGeneration");
        }
        Log.d(TAG, "MtkAudioPatchManager--");
    }

    private static boolean intArrayContains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    private void registerReceivers() {
        IntentFilter intent = new IntentFilter();
        intent.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        intent.addAction("android.media.VOLUME_CHANGED_ACTION");
        intent.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
        intent.addAction("android.intent.action.HEADSET_PLUG");
        intent.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
        this.mContext.registerReceiver(this.mReceiver, intent);
    }

    private void unRegisterReceivers() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    private void registerContentObserver() {
        ContentResolver resolver = this.mContext.getContentResolver();
        for (String s : new String[]{"hdmi_control_enabled", "hdmi_control_auto_wakeup_enabled", "hdmi_control_auto_device_off_enabled", "hdmi_system_audio_control_enabled", "mhl_input_switching_enabled", "mhl_power_charge_enabled"}) {
            resolver.registerContentObserver(Settings.Global.getUriFor(s), false, this.mHdmiObserver);
        }
    }

    private void unRegisterContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mHdmiObserver);
    }

    private void registerHdmiAudioModeListener() {
        if (this.mHdmiControlService != null) {
            try {
                this.mHdmiControlService.addSystemAudioModeChangeListener(this.mHdmiAudioModeListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Error registering listeners to HdmiControlService:", e);
            }
        } else {
            Log.w(TAG, "HdmiControlService is not available");
        }
    }

    private void unRegisterHdmiAudioModeListener() {
        if (this.mHdmiControlService != null) {
            try {
                this.mHdmiControlService.removeSystemAudioModeChangeListener(this.mHdmiAudioModeListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Error unregistering listeners to HdmiControlService:", e);
            }
        } else {
            Log.w(TAG, "HdmiControlService is not available");
        }
    }

    public boolean createAudioPatch() {
        Log.d(TAG, "createAudioPatch");
        int mDeviceType = 1073741824;
        updateBluetoothStatus();
        updateHdmiStatus();
        if (this.mHdmiStatus) {
            mDeviceType = 262144;
        } else if (this.mBluetoothStatus) {
            mDeviceType = 128;
        }
        return createAudioPatchLocked(mDeviceType);
    }

    public boolean releaseAudioPatch() {
        Log.d(TAG, "releaseAudioPatch");
        return releaseAudioPatchLocked();
    }

    /* access modifiers changed from: private */
    public boolean updateBluetoothStatus() {
        findAudioSinkFromAudioPolicy(this.mAudioSinks);
        String str = TAG;
        Log.d(str, "updateBluetoothStatus mAudioSink size = " + this.mAudioSinks.size());
        if (this.mAudioSinks.size() < 1) {
            return false;
        }
        for (AudioDevicePort audioDevicePort : this.mAudioSinks) {
            if (audioDevicePort.role() == 2 && (audioDevicePort instanceof AudioDevicePort) && audioDevicePort.type() == 128) {
                String str2 = TAG;
                Log.d(str2, "find AudioDevicePort A2DP: " + audioDevicePort);
                this.mBluetoothStatus = true;
                return true;
            }
        }
        Log.d(TAG, "AudioDevicePort A2DP isn't exist!");
        this.mBluetoothStatus = false;
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateHdmiStatus() {
        findAudioSinkFromAudioPolicy(this.mAudioSinks);
        String str = TAG;
        Log.d(str, "updateHdmiStatus mAudioSink size = " + this.mAudioSinks.size());
        if (this.mAudioSinks.size() < 1) {
            return false;
        }
        for (AudioDevicePort audioDevicePort : this.mAudioSinks) {
            if (audioDevicePort.role() == 2 && (audioDevicePort instanceof AudioDevicePort) && audioDevicePort.type() == 262144) {
                String str2 = TAG;
                Log.d(str2, "find AudioDevicePort hdmi: " + audioDevicePort);
                this.mHdmiStatus = true;
                return true;
            }
        }
        Log.d(TAG, "AudioDevicePort hdmi isn't exist!");
        this.mHdmiStatus = false;
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateHpStatus() {
        findAudioSinkFromAudioPolicy(this.mAudioSinks);
        String str = TAG;
        Log.d(str, "updateHpStatus mAudioSink size = " + this.mAudioSinks.size());
        if (this.mAudioSinks.size() < 1) {
            return false;
        }
        for (AudioDevicePort audioDevicePort : this.mAudioSinks) {
            if (audioDevicePort.role() == 2 && (audioDevicePort instanceof AudioDevicePort) && audioDevicePort.type() == 8) {
                String str2 = TAG;
                Log.d(str2, "find AudioDevicePort Hp: " + audioDevicePort);
                this.mHpStatus = true;
                return true;
            }
        }
        Log.d(TAG, "AudioDevicePort Hp isn't exist!");
        this.mHpStatus = false;
        return true;
    }

    /* access modifiers changed from: private */
    public void updateAudioPatch(int type) {
        Log.d(TAG, "updateAudioPatch++");
        createAudioPatchLocked(type);
        Log.d(TAG, "updateAudioPatch--");
    }

    private boolean createAudioPatchLocked(int type) {
        AudioPortConfig srcPortConfig;
        ArrayList<AudioDevicePort> devicePorts;
        AudioPortConfig srcPortConfig2;
        int gainValue;
        int i = type;
        ArrayList<AudioDevicePort> devicePorts2 = new ArrayList<>();
        try {
            this.methodResetAudioPortGeneration.invoke((Object) null, new Object[0]);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException on methodResetAudioPortGeneration");
        } catch (InvocationTargetException e2) {
            Log.e(TAG, "InvocationTargetExcept on methodResetAudioPortGeneration");
        }
        AudioManager audioManager = this.mAudioManager;
        if (AudioManager.listAudioDevicePorts(devicePorts2) != 0) {
            Log.d(TAG, "createAudioPatchLocked fail! ");
            return false;
        }
        AudioPortConfig srcPortConfig3 = null;
        Log.d(TAG, "list devicePorts size = " + devicePorts2.size());
        Iterator<AudioDevicePort> it = devicePorts2.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AudioDevicePort devicePort = it.next();
            Log.i(TAG, "devicePorts: " + devicePort.toString());
            if (0 == 0 && devicePort.role() == 1 && devicePort.type() == -2147221504) {
                this.mAudioSource = devicePort;
                Log.d(TAG, "find devicePort DEVICE_IN_LOOPBACK: " + devicePort);
                break;
            }
        }
        int mCurrentMaxIndex2 = this.mAudioManager.getStreamMaxVolume(3);
        int mCurrentIndex2 = this.mAudioManager.getStreamVolume(3);
        float mStreamVolume = ((float) mCurrentIndex2) / ((float) mCurrentMaxIndex2);
        Log.i(TAG, "mStreamVolume: " + mStreamVolume + " mCurrentIndex:" + mCurrentIndex2 + " mCurrentMaxIndex: " + mCurrentMaxIndex2);
        AudioGainConfig sourceGainConfig = null;
        if (this.mAudioSource.gains().length > 0) {
            AudioGain sourceGain = null;
            AudioGain[] gains = this.mAudioSource.gains();
            int length = gains.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                AudioGain gain = gains[i2];
                if ((gain.mode() & 1) != 0) {
                    sourceGain = gain;
                    AudioGain audioGain = gain;
                    break;
                }
                i2++;
            }
            if (sourceGain != null) {
                int steps = (sourceGain.maxValue() - sourceGain.minValue()) / sourceGain.stepValue();
                int gainValue2 = sourceGain.minValue();
                if (mStreamVolume < 1.0f) {
                    float f = mStreamVolume;
                    gainValue = gainValue2 + (sourceGain.stepValue() * ((int) (((double) (((float) steps) * mStreamVolume)) + 0.5d)));
                } else {
                    gainValue = sourceGain.maxValue();
                }
                Log.d(TAG, "gainValue: " + gainValue);
                sourceGainConfig = sourceGain.buildConfig(1, sourceGain.channelMask(), new int[]{gainValue}, 0);
            } else {
                Log.w(TAG, "No audio source gain with MODE_JOINT support exists.");
            }
        }
        List<AudioPortConfig> sinkConfigs = new ArrayList<>();
        for (AudioDevicePort audioSink : this.mAudioSinks) {
            AudioPortConfig sinkConfig = audioSink.activeConfig();
            int sinkSamplingRate = this.mDesiredSamplingRate;
            int sinkChannelMask = this.mDesiredChannelMask;
            int sinkFormat = this.mDesiredFormat;
            if (sinkConfig != null) {
                if (sinkSamplingRate == 0) {
                    sinkSamplingRate = sinkConfig.samplingRate();
                }
                devicePorts = devicePorts2;
                if (sinkChannelMask == 1) {
                    sinkChannelMask = sinkConfig.channelMask();
                }
                if (sinkFormat == 1) {
                    sinkChannelMask = sinkConfig.format();
                }
            } else {
                devicePorts = devicePorts2;
            }
            if (!(sinkConfig != null && sinkConfig.samplingRate() == sinkSamplingRate && sinkConfig.channelMask() == sinkChannelMask && sinkConfig.format() == sinkFormat)) {
                if (!intArrayContains(audioSink.samplingRates(), sinkSamplingRate) && audioSink.samplingRates().length > 0) {
                    sinkSamplingRate = audioSink.samplingRates()[0];
                }
                if (!intArrayContains(audioSink.channelMasks(), sinkChannelMask)) {
                    sinkChannelMask = 1;
                }
                if (!intArrayContains(audioSink.formats(), sinkFormat)) {
                    sinkFormat = 1;
                }
                sinkConfig = audioSink.buildConfig(sinkSamplingRate, sinkChannelMask, sinkFormat, (AudioGainConfig) null);
            }
            if (i != 1073741824) {
                if (audioSink.type() == i) {
                    sinkConfigs.add(sinkConfig);
                }
                srcPortConfig2 = srcPortConfig3;
            } else {
                srcPortConfig2 = srcPortConfig3;
                Log.d(TAG, "createAudioPatchLocked add device " + audioSink.type());
                sinkConfigs.add(sinkConfig);
            }
            devicePorts2 = devicePorts;
            srcPortConfig3 = srcPortConfig2;
            i = type;
        }
        AudioPortConfig srcPortConfig4 = srcPortConfig3;
        ArrayList<AudioDevicePort> arrayList = devicePorts2;
        if (this.mAudioSource != null) {
            AudioPortConfig srcPortConfig5 = this.mAudioSource.activeConfig();
            int sourceSamplingRate = this.mDesiredSamplingRate;
            int sourceChannelMask = this.mDesiredChannelMask;
            int sourceFormat = this.mDesiredFormat;
            if (srcPortConfig5 != null) {
                if (sourceSamplingRate == 0) {
                    sourceSamplingRate = srcPortConfig5.samplingRate();
                }
                if (sourceChannelMask == 1) {
                    sourceChannelMask = srcPortConfig5.channelMask();
                }
                if (sourceFormat == 1) {
                    sourceFormat = srcPortConfig5.format();
                }
            }
            srcPortConfig = this.mAudioSource.buildConfig(sourceSamplingRate, sourceChannelMask, sourceFormat, sourceGainConfig);
        } else {
            srcPortConfig = srcPortConfig4;
        }
        if (srcPortConfig == null || sinkConfigs.size() <= 0) {
            Log.e(TAG, "failed to create patch");
            return false;
        }
        if (this.mAudioPatch != null) {
            Log.d(TAG, "release last patch mAudioPatch +++");
            AudioManager audioManager2 = this.mAudioManager;
            AudioManager.releaseAudioPatch(this.mAudioPatch);
            this.mAudioPatch = null;
            Log.d(TAG, "release last patch mAudioPatch ---");
        } else {
            Log.d(TAG, "mAudioPatch is null");
        }
        AudioPatch[] patches = {this.mAudioPatch};
        try {
            this.methodResetAudioPortGeneration.invoke((Object) null, new Object[0]);
        } catch (IllegalAccessException e3) {
            Log.e(TAG, "IllegalAccessException on methodResetAudioPortGeneration");
        } catch (InvocationTargetException e4) {
            Log.e(TAG, "InvocationTargetExcept on methodResetAudioPortGeneration");
        }
        ArrayList<AudioPatch> patchList = new ArrayList<>();
        AudioManager audioManager3 = this.mAudioManager;
        AudioManager.listAudioPatches(patchList);
        Log.d(TAG, "clear all audio patchs size = " + patchList.size());
        Iterator<AudioPatch> it2 = patchList.iterator();
        while (it2.hasNext()) {
            AudioPatch patch = it2.next();
            Log.i(TAG, "AudioPatch: " + patch.toString());
            AudioManager audioManager4 = this.mAudioManager;
            AudioManager.releaseAudioPatch(patch);
        }
        Log.i(TAG, "srcPortConfig: " + srcPortConfig.toString());
        for (AudioPortConfig portConfig : sinkConfigs) {
            Log.i(TAG, "sinkPortConfig: " + portConfig.toString());
        }
        if (this.mAudioPatch != null) {
            Log.d(TAG, "before update mAudioPatch: " + this.mAudioPatch.toString());
        }
        AudioManager audioManager5 = this.mAudioManager;
        int status = AudioManager.createAudioPatch(patches, new AudioPortConfig[]{srcPortConfig}, (AudioPortConfig[]) sinkConfigs.toArray(new AudioPortConfig[sinkConfigs.size()]));
        this.mAudioPatch = patches[0];
        if (this.mAudioPatch != null) {
            Log.d(TAG, "after update mAudioPatch: " + this.mAudioPatch.toString());
        }
        if (sourceGainConfig != null) {
            AudioManager audioManager6 = this.mAudioManager;
            AudioManager.setAudioPortGain(this.mAudioSource, sourceGainConfig);
        }
        if (status == 0) {
            return true;
        }
        return false;
    }

    private boolean releaseAudioPatchLocked() {
        Log.d(TAG, "releaseAudioPatchLocked");
        this.mHandler.removeMessages(1);
        unRegisterReceivers();
        unRegisterContentObserver();
        unRegisterHdmiAudioModeListener();
        if (this.mAudioPatch != null) {
            Log.i(TAG, this.mAudioPatch.toString());
            AudioManager audioManager = this.mAudioManager;
            AudioManager.releaseAudioPatch(this.mAudioPatch);
            this.mAudioPatch = null;
            Log.d(TAG, "releaseAudioPatchLocked done!");
        }
        return true;
    }

    private void findAudioSinkFromAudioPolicy(List<AudioDevicePort> sinks) {
        sinks.clear();
        try {
            this.methodResetAudioPortGeneration.invoke((Object) null, new Object[0]);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException on methodResetAudioPortGeneration");
        } catch (InvocationTargetException e2) {
            Log.e(TAG, "InvocationTargetExcept on methodResetAudioPortGeneration");
        }
        this.mSinkDevice = this.mAudioManager.getDevicesForStream(3);
        String str = TAG;
        Log.d(str, "findAudioSinkFromAudioPolicy sinkDevice = " + Integer.toHexString(this.mSinkDevice));
        ArrayList<AudioDevicePort> devicePorts = new ArrayList<>();
        AudioManager audioManager = this.mAudioManager;
        if (AudioManager.listAudioDevicePorts(devicePorts) != 0) {
            Log.d(TAG, "fail to listAudioDevicePorts!");
            return;
        }
        Iterator<AudioDevicePort> it = devicePorts.iterator();
        while (it.hasNext()) {
            AudioDevicePort devicePort = it.next();
            if ((devicePort.type() & this.mSinkDevice) != 0 && (devicePort.type() & Integer.MIN_VALUE) == 0) {
                sinks.add(devicePort);
            }
        }
    }

    private class HdmiObserver extends ContentObserver {
        public HdmiObserver(Handler handler) {
            super(handler);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x003b, code lost:
            if (r0.equals("hdmi_control_auto_wakeup_enabled") != false) goto L_0x005d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r7, android.net.Uri r8) {
            /*
                r6 = this;
                java.lang.String r0 = r8.getLastPathSegment()
                com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager r1 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.this
                r2 = 1
                boolean r1 = r1.readBooleanSetting(r0, r2)
                java.lang.String r3 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "HdmiObserver:onChange enabled:"
                r4.append(r5)
                r4.append(r1)
                java.lang.String r4 = r4.toString()
                android.util.Log.d(r3, r4)
                int r3 = r0.hashCode()
                switch(r3) {
                    case -2009736264: goto L_0x0052;
                    case -1262529811: goto L_0x0048;
                    case -885757826: goto L_0x003e;
                    case 726613192: goto L_0x0035;
                    case 1628046095: goto L_0x002b;
                    default: goto L_0x002a;
                }
            L_0x002a:
                goto L_0x005c
            L_0x002b:
                java.lang.String r2 = "hdmi_control_auto_device_off_enabled"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x005c
                r2 = 2
                goto L_0x005d
            L_0x0035:
                java.lang.String r3 = "hdmi_control_auto_wakeup_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x005c
                goto L_0x005d
            L_0x003e:
                java.lang.String r2 = "mhl_power_charge_enabled"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x005c
                r2 = 4
                goto L_0x005d
            L_0x0048:
                java.lang.String r2 = "mhl_input_switching_enabled"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x005c
                r2 = 3
                goto L_0x005d
            L_0x0052:
                java.lang.String r2 = "hdmi_control_enabled"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x005c
                r2 = 0
                goto L_0x005d
            L_0x005c:
                r2 = -1
            L_0x005d:
                switch(r2) {
                    case 0: goto L_0x00a1;
                    case 1: goto L_0x0097;
                    case 2: goto L_0x008d;
                    case 3: goto L_0x0083;
                    case 4: goto L_0x0079;
                    default: goto L_0x0060;
                }
            L_0x0060:
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "unhandled option:"
                r3.append(r4)
                r3.append(r0)
                java.lang.String r3 = r3.toString()
                android.util.Log.d(r2, r3)
                goto L_0x00ab
            L_0x0079:
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r3 = "MHL_POWER_CHARGE_ENABLED"
                android.util.Log.d(r2, r3)
                goto L_0x00ab
            L_0x0083:
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r3 = "MHL_INPUT_SWITCHING_ENABLED"
                android.util.Log.d(r2, r3)
                goto L_0x00ab
            L_0x008d:
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r3 = "HDMI_CONTROL_AUTO_DEVICE_OFF_ENABLED"
                android.util.Log.d(r2, r3)
                goto L_0x00ab
            L_0x0097:
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r3 = "HDMI_CONTROL_AUTO_WAKEUP_ENABLED"
                android.util.Log.d(r2, r3)
                goto L_0x00ab
            L_0x00a1:
                java.lang.String r2 = com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.TAG
                java.lang.String r3 = "HDMI_CONTROL_ENABLED"
                android.util.Log.d(r2, r3)
            L_0x00ab:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager.HdmiObserver.onChange(boolean, android.net.Uri):void");
        }
    }

    private static int toInt(boolean enabled) {
        return enabled;
    }

    /* access modifiers changed from: package-private */
    public boolean readBooleanSetting(String key, boolean defVal) {
        return Settings.Global.getInt(this.mContext.getContentResolver(), key, toInt(defVal)) == 1;
    }

    private final class HdmiSystemAudioModeChangeListener extends IHdmiSystemAudioModeChangeListener.Stub {
        private Handler mHdmiListenerHandler = null;

        public HdmiSystemAudioModeChangeListener(Handler handler) {
            this.mHdmiListenerHandler = handler;
        }

        public void onStatusChanged(boolean enabled) {
            String access$000 = MtkAudioPatchManager.TAG;
            Log.d(access$000, "HdmiSystemAudioModeChanged = " + enabled + ", mHdmiPowerOnStatus = " + MtkAudioPatchManager.this.mHdmiPowerOnStatus + ", mHdmiPowerOffStatus = " + MtkAudioPatchManager.this.mHdmiPowerOffStatus);
            if (enabled && !MtkAudioPatchManager.this.mHdmiPowerOnStatus) {
                this.mHdmiListenerHandler.removeMessages(3);
                boolean unused = MtkAudioPatchManager.this.mHdmiPowerOnStatus = true;
                this.mHdmiListenerHandler.sendMessageDelayed(Message.obtain(this.mHdmiListenerHandler, 3), 200);
            } else if (!enabled && !MtkAudioPatchManager.this.mHdmiPowerOffStatus) {
                this.mHdmiListenerHandler.removeMessages(4);
                boolean unused2 = MtkAudioPatchManager.this.mHdmiPowerOffStatus = true;
                this.mHdmiListenerHandler.sendMessageDelayed(Message.obtain(this.mHdmiListenerHandler, 4), 200);
            }
        }
    }
}
