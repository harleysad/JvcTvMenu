package com.mediatek.wwtv.tvcenter.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import com.android.tv.menu.MenuOptionMain;
import com.mediatek.twoworlds.tv.MtkTvKeyEvent;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.db.DBHelper;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.VgaPowerManager;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String PACKAGE_NAME = "com.mediatek.wwtv.tvcenter";
    private static int isKeyUpTwice = 0;
    private static boolean keyFlag = false;

    public void onReceive(Context context, Intent intent) {
        final Context mContext = context;
        Log.d("BootBroadcastReceiver", "intent:" + intent);
        boolean isshow = false;
        if (intent.getAction().compareTo("android.intent.action.GLOBAL_BUTTON") == 0) {
            KeyEvent localKeyEvents = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            int keycodes = localKeyEvents.getKeyCode();
            Log.d("BootBroadcastReceiver", "localkey==" + localKeyEvents);
            if (MarketRegionInfo.isFunctionSupport(38) && localKeyEvents.getDeviceId() == 1) {
                if (keycodes == 178) {
                    if (localKeyEvents.getAction() == 0 && keyFlag && isScreenOff(context)) {
                        Log.d("BootBroadcastReceiver", "send KEYCODE_POWER");
                        InstrumentationHandler.getInstance().sendKeyDownUpSync(26);
                        return;
                    } else if (localKeyEvents.getAction() == 0) {
                        if (localKeyEvents.getRepeatCount() >= 5) {
                            keyFlag = true;
                        } else {
                            keyFlag = false;
                        }
                    }
                }
                if (keyFlag || isScreenOff(context) || localKeyEvents.getAction() != 1) {
                    if (!isScreenOff(context) && localKeyEvents.getAction() == 1) {
                        keyFlag = false;
                    }
                    if (!DestroyApp.isCurActivityTkuiMainActivity() && localKeyEvents.getRepeatCount() == 0 && keycodes == 178 && localKeyEvents.getAction() == 1) {
                        Log.d("BootBroadcastReceiver", "enter key");
                        InstrumentationHandler.getInstance().sendKeyDownUpSync(23);
                        return;
                    }
                } else {
                    Log.d("BootBroadcastReceiver", "power: off");
                    ((PowerManager) context.getSystemService("power")).wakeUp(SystemClock.uptimeMillis());
                    return;
                }
            }
            if (!SystemsApi.isUserSetupComplete(context)) {
                Log.d("BootBroadcastReceiver", "User Setup not Complete!");
                return;
            }
            SystemsApi.dayDreamAwaken();
            if (!MtkTvScan.getInstance().isScanning()) {
                setTVKey(intent, mContext);
                if (DestroyApp.isCurActivityTkuiMainActivity()) {
                    ComponentsManager mNavCompsMagr = ComponentsManager.getInstance();
                    if (((SourceListView) mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC)).isShowing() || ((ChannelListDialog) mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_CH_LIST)).isShowing() || StateDvrFileList.getInstance(DvrManager.getInstance()).isShowing() || ((CIMainDialog) mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG)).isShowing()) {
                        isshow = true;
                    }
                    if (MarketRegionInfo.isFunctionSupport(35)) {
                        Intent global_btn_intent = new Intent("tv.samba.ssm.GLOBAL_BUTTON");
                        global_btn_intent.addFlags(32);
                        context.sendBroadcast(global_btn_intent);
                        Log.d("BootBroadcastReceiver", "send tv.samba.ssm.GLOBAL_BUTTON intent");
                    }
                    if (MarketRegionInfo.isFunctionSupport(38)) {
                        Log.d("BootBroadcastReceiver", "localKeyEvents = " + localKeyEvents);
                        if (localKeyEvents.getDeviceId() == 1) {
                            if (localKeyEvents.getRepeatCount() != 0 || keycodes != 178) {
                                return;
                            }
                            if (localKeyEvents.getAction() == 1 && !((MenuOptionMain) mNavCompsMagr.getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG)).isShowing() && !isshow) {
                                InstrumentationHandler.getInstance().sendKeyDownUpSync(82);
                                return;
                            } else if (localKeyEvents.getAction() == 1) {
                                InstrumentationHandler.getInstance().sendKeyDownUpSync(23);
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                    KeyEvent localKeyEvent = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
                    int keycode = localKeyEvent.getKeyCode();
                    if (keycode == 178) {
                        handleInputKey(context, keycode, localKeyEvent);
                    }
                } else if (!MtkTvScan.getInstance().isScanning() && !isCamUpgradeing(context)) {
                    Log.d("BootBroadcastReceiver", "event=" + localKeyEvents.getFlags() + ",keycode=" + keycodes + ",is true=" + (localKeyEvents.getFlags() & 128));
                    context.sendBroadcast(new Intent("mtk.intent.input.source"));
                    if (!MarketRegionInfo.isFunctionSupport(38) || localKeyEvents.getDeviceId() != 1) {
                        if (keycodes == 178) {
                            boolean interactive = ((PowerManager) context.getSystemService("power")).isInteractive();
                            boolean oadActivity = DestroyApp.isCurOADActivity();
                            int factory = SystemProperties.getInt("vendor.mtk.factory.disable.input", 0);
                            Log.d("BootBroadcastReceiver", "PowerManager.isInteractive=" + interactive + " oadActivity:" + oadActivity + " factory:" + factory);
                            if (interactive && !oadActivity && factory != 1) {
                                Intent mIntent = new Intent("android.mtk.intent.action.ACTION_REQUEST_TOP_RESUME");
                                if (DestroyApp.isCurTaskTKUI()) {
                                    mIntent.putExtra("showSourceList", false);
                                } else {
                                    mIntent.putExtra("showSourceList", true);
                                }
                                mIntent.addFlags(268435456);
                                mContext.getApplicationContext().startActivity(mIntent);
                            }
                        }
                    } else if (localKeyEvents.getAction() == 1 && localKeyEvents.getRepeatCount() == 0 && keycodes == 178) {
                        InstrumentationHandler.getInstance().sendKeyDownUpSync(23);
                    }
                }
            }
        } else {
            if (intent.getAction().compareTo("android.mediatek.intent.logcattousb") == 0) {
                MtkLog.logOnFlag = intent.getBooleanExtra(NotificationCompat.CATEGORY_STATUS, false);
            } else if (intent.getAction().compareTo(DBHelper.SCHEDULE_ALARM_ACTION) == 0) {
                DvrManager.getInstance(mContext).getController().handleRecordNotify(intent);
                return;
            }
            Log.d("BootBroadcastReceiver", PACKAGE_NAME);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(BootBroadcastReceiver.PACKAGE_NAME);
                        if (intent != null) {
                            Log.d("BootBroadcastReceiver", intent.getAction());
                        }
                    } catch (Exception e) {
                        Log.d("BootBroadcastReceiver", e.getMessage());
                    }
                }
            }).start();
        }
    }

    private void handleInputKey(Context context, int keycode, KeyEvent event) {
        if (keycode == 178) {
            Log.d("BootBroadcastReceiver", "event.getAction =" + event.getAction() + " " + event.getScanCode());
            if (event.getAction() == 1 && event.getScanCode() != 0) {
                Log.d("BootBroadcastReceiver", "ACTION_UP = " + event);
                isKeyUpTwice = 0;
                return;
            } else if (event.getAction() == 0 && event.getScanCode() != 0) {
                Log.d("BootBroadcastReceiver", "ACTION_down = " + event);
            } else if ((event.getAction() == 1 || event.getAction() == 0) && event.getScanCode() == 0) {
                Log.d("BootBroadcastReceiver", "isKeyUpTwice =" + isKeyUpTwice);
                isKeyUpTwice = isKeyUpTwice + 1;
                if (isKeyUpTwice == 2) {
                    isKeyUpTwice = 0;
                    Log.d("BootBroadcastReceiver", "isKeyUp == 2");
                    return;
                }
                Log.d("BootBroadcastReceiver", "ACTION_UP twice" + isKeyUpTwice);
            }
        } else {
            Log.d("BootBroadcastReceiver", "should never happen! keycode = " + keycode + event);
        }
        if (ComponentsManager.getActiveCompId() != 16777233) {
            if (DvrManager.getInstance() == null || !(DvrManager.getInstance().getState() instanceof StateDvrFileList) || !((StateDvrFileList) DvrManager.getInstance().getState()).isShowing()) {
                NavBasic basic = ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
                if (basic == null) {
                    Log.d("BootBroadcastReceiver", "basic == null");
                } else if (basic.isVisible()) {
                    Log.d("BootBroadcastReceiver", "visible");
                    basic.KeyHandler(keycode, event, false);
                } else {
                    Log.d("BootBroadcastReceiver", "invisible");
                    if (!isCamUpgradeing(context)) {
                        ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_INPUT_SRC);
                    } else {
                        Log.d("BootBroadcastReceiver", "cam is upgrading now ,ignore source");
                    }
                    VgaPowerManager vgaPowerManager = (VgaPowerManager) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POWER_OFF);
                    if (vgaPowerManager != null) {
                        vgaPowerManager.handleSourceKey();
                    }
                }
            }
        }
    }

    private boolean isCamUpgradeing(Context context) {
        return CIStateChangedCallBack.getInstance(context).camUpgradeStatus();
    }

    private boolean isScreenOff(Context context) {
        for (Display displayManager : ((DisplayManager) context.getSystemService("display")).getDisplays()) {
            if (displayManager.getState() != 1) {
                Log.d("BootBroadcastReceiver", "power off");
                return true;
            }
        }
        return false;
    }

    private void setTVKey(Intent intent, Context mContext) {
        Log.d("BootBroadcastReceiver", "set TV key");
        KeyEvent localKeyEvents = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
        int keycodes = localKeyEvents.getKeyCode();
        if (keycodes != 170) {
            return;
        }
        if (!DestroyApp.isCurActivityTkuiMainActivity()) {
            Intent mIntent = new Intent("android.mtk.intent.action.ACTION_REQUEST_TOP_RESUME");
            mIntent.addFlags(268435456);
            mContext.getApplicationContext().startActivity(mIntent);
        } else if (localKeyEvents.getAction() == 0 && 0 != 0) {
            Log.d("BootBroadcastReceiver", "dismiss all ui");
            dismissUI(keycodes);
        } else if (localKeyEvents.getAction() == 0) {
            if (localKeyEvents.getRepeatCount() < 5) {
            }
        } else if (0 == 0 && localKeyEvents.getAction() == 1 && localKeyEvents.getRepeatCount() == 0) {
            Log.d("BootBroadcastReceiver", "dismiss all ui +");
            dismissUI(keycodes);
        }
    }

    private void dismissUI(int keycodes) {
        if (ComponentsManager.getNativeActiveCompId() != 0) {
            Log.d("BootBroadcastReceiver", "native ui");
            MtkTvKeyEvent mtkKeyEvent = MtkTvKeyEvent.getInstance();
            mtkKeyEvent.sendKeyClick(mtkKeyEvent.androidKeyToDFBkey(keycodes));
        }
        if (ComponentsManager.getInstance().isComponentsShow()) {
            ComponentsManager.getInstance().hideAllComponents();
        }
        if (StateDvrFileList.getInstance(DvrManager.getInstance()).isShowing()) {
            StateDvrFileList.getInstance(DvrManager.getInstance()).dissmiss();
        }
    }
}
