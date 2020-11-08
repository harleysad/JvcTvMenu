package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.media.MediaPlayer2;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.epg.EPGManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.fav.NavIntegration;
import com.mediatek.wwtv.tvcenter.nav.fav.TVChannel;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl;
import com.mediatek.wwtv.tvcenter.nav.util.PIPPOPSurfaceViewControl;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.SundryShowTextView;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeshiftView;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;

public class DvrDialog extends AlertDialog implements Runnable {
    public static final String DISSMISS_DIALOG = "com.mediatek.dialog.dismiss";
    public static final int KEYCODE_FROM_FAV = 40962;
    public static final int Menu_pip = 15;
    private static final int Msg_ID_Change_Channel = 40962;
    public static final int OTHER_SOURCE = 3;
    public static final int TIMESHIFT_RECORDLIST = 11249;
    public static final int TYPE_BGM = 0;
    public static final int TYPE_CHANNEL_CENTER = 11245;
    public static final int TYPE_CHANNEL_Desramble = 11243;
    private static final int TYPE_CHANNEL_Desramble_By_SOURCE_KEY = 0;
    public static final int TYPE_CHANNEL_Desramble_SRC_KEY = 11247;
    public static final int TYPE_CHANNEL_NUMBER = 11244;
    private static final int TYPE_ChangeChanel_Down = 11236;
    private static final int TYPE_ChangeChanel_Pre = 11237;
    private static final int TYPE_ChangeChanel_UP = 11235;
    public static final int TYPE_Change_ChannelNum = 11238;
    public static final int TYPE_Change_ChannelNum_SRC = 11248;
    public static final int TYPE_Change_Source = 11234;
    public static final int TYPE_Change_Source_By_Src_Key = 40963;
    public static final int TYPE_Change_Source_By_URL = 11246;
    public static final int TYPE_Confirm = 1;
    public static final int TYPE_Confirm_From_ChannelList = 40961;
    public static final int TYPE_DVR = 5;
    public static final int TYPE_Normal = 2;
    private static final int TYPE_PIPPOP = 11239;
    public static final int TYPE_Record = 1;
    public static final int TYPE_SCART = 11242;
    public static final int TYPE_SCHEDULE = 11240;
    public static final int TYPE_STOP_PVR_PLAYING_ENTER_MMP = 3;
    public static final int TYPE_TSHIFT = 11241;
    public static final int TYPE_Timeshift = 2;
    private final String TAG = "DvrDialog";
    BannerView bannerView;
    private Button buttonNo;
    private String buttonNoName;
    private String buttonOKName;
    private Button buttonYes;
    private String buttonYesName;
    SourceListView dialog;
    private DialogDismissRecevier dreceiver;
    private final int focusedButton = 0;
    public int height = 0;
    /* access modifiers changed from: private */
    public int keyCode = 0;
    private Loading loading;
    WindowManager.LayoutParams lp = this.window.getAttributes();
    /* access modifiers changed from: private */
    public Activity mActivity;
    /* access modifiers changed from: private */
    public final Context mContext;
    private TVChannel mFavChannel = new TVChannel();
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageType.MESSAGE_INACTIVE_CHANNELS /*123*/:
                    DvrDialog.this.dialog.handleCenterKey();
                    return;
                case DvrDialog.TYPE_ChangeChanel_UP /*11235*/:
                    CommonIntegration.getInstance().channelUp();
                    ComponentStatusListener.getInstance().updateStatus(5, DvrDialog.this.keyCode);
                    return;
                case DvrDialog.TYPE_ChangeChanel_Down /*11236*/:
                    CommonIntegration.getInstance().channelDown();
                    ComponentStatusListener.getInstance().updateStatus(5, DvrDialog.this.keyCode);
                    return;
                case DvrDialog.TYPE_ChangeChanel_Pre /*11237*/:
                    CommonIntegration.getInstance().channelPre();
                    ComponentStatusListener.getInstance().updateStatus(5, DvrDialog.this.keyCode);
                    return;
                case DvrDialog.TYPE_PIPPOP /*11239*/:
                    MtkLog.e("pippop", "pippop:");
                    if (1 != IntegrationZoom.getInstance(DvrDialog.this.mContext).getCurrentZoom()) {
                        IntegrationZoom.getInstance(DvrDialog.this.mContext).setZoomModeToNormal();
                    }
                    if (MarketRegionInfo.isFunctionSupport(26)) {
                        ((MultiViewControl) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_POP)).setModeToPIP();
                    } else {
                        if (MarketRegionInfo.isFunctionSupport(13)) {
                            PIPPOPSurfaceViewControl.getSurfaceViewControlInstance().changeOutputWithTVState(1);
                        }
                        KeyDispatch.getInstance().passKeyToNative(DvrDialog.this.keyCode, (KeyEvent) null);
                    }
                    ComponentStatusListener.getInstance().updateStatus(5, DvrDialog.this.keyCode);
                    return;
                case DvrDialog.TYPE_TSHIFT /*11241*/:
                    TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager().stop();
                    SaveValue.getInstance(DvrDialog.this.mContext).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
                    SaveValue.saveWorldBooleanValue(DvrDialog.this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
                    SystemClock.sleep(1000);
                    DvrManager.getInstance().startSchedulePvr();
                    return;
                case DvrDialog.TYPE_SCART /*11242*/:
                    InputSourceManager.getInstance().changeCurrentInputSourceByName("SCART");
                    return;
                case DvrDialog.TYPE_CHANNEL_NUMBER /*11244*/:
                    if (DvrDialog.this.bannerView != null && DvrDialog.this.mSelectedChannelNumString != null) {
                        DvrDialog.this.bannerView.pvrChangeNum(DvrDialog.this.mSelectedChannelNumString);
                        return;
                    }
                    return;
                case DvrDialog.TYPE_CHANNEL_CENTER /*11245*/:
                    if (DvrDialog.this.mSelectedChannelID != -1) {
                        CommonIntegration.getInstance().selectChannelById(DvrDialog.this.mSelectedChannelID);
                        return;
                    }
                    return;
                case DvrDialog.TYPE_Change_Source_By_URL /*11246*/:
                    TurnkeyUiMainActivity.getInstance().processInputUri(DvrDialog.this.uri);
                    return;
                case DvrDialog.TIMESHIFT_RECORDLIST /*11249*/:
                    BannerView bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                    if (bannerView.isShown()) {
                        bannerView.setVisibility(8);
                    }
                    DvrManager.getInstance().setState((StateBase) StateDvrFileList.getInstance(DvrManager.getInstance()));
                    ((StateDvrFileList) DvrManager.getInstance().getState()).showPVRlist();
                    return;
                case 40962:
                    MtkLog.e("FAV", "Msg_ID_Change_Channel:");
                    NavIntegration.getInstance(DvrDialog.this.mContext).selectChannel(DvrDialog.this.getFavChannel());
                    ComponentStatusListener.getInstance().updateStatus(5, DvrDialog.this.keyCode);
                    return;
                case 40963:
                    DvrDialog.this.dialog.handleSourceKey();
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public int mSelectedChannelID = -1;
    /* access modifiers changed from: private */
    public String mSelectedChannelNumString = "0";
    private String message;
    int modeValue;
    private OnDVRDialogListener onPVRDialogListener;
    private pvrReceiver receiver;
    private MtkTvBookingBase scheduleItem;
    private TextView textView;
    private String title;
    private TextView titleView;
    private int type = 0;
    /* access modifiers changed from: private */
    public Uri uri;
    private TextView waitView;
    public int width = 0;
    Window window = getWindow();
    private int xOff;
    private int yOff;

    public void registerBroadcast() {
        this.receiver = new pvrReceiver();
        this.mContext.registerReceiver(this.receiver, new IntentFilter("com.mediatek.dialog.dismiss"));
    }

    public DvrDialog(Context context, int type2) {
        super(context, 2131755420);
        this.type = type2;
        this.mContext = context;
        this.mSelectedChannelID = -1;
    }

    public DvrDialog(Activity context, int type2, int keyCode2, int mode) {
        super(context, 2131755420);
        this.type = type2;
        this.keyCode = keyCode2;
        this.mContext = context;
        this.mActivity = context;
        this.modeValue = mode;
    }

    public DvrDialog(Activity context, int type2, int keyCode2, SourceListView dialog2, int mode) {
        super(context, 2131755420);
        this.type = type2;
        this.keyCode = keyCode2;
        this.mContext = context;
        this.mActivity = context;
        this.dialog = dialog2;
        this.modeValue = mode;
    }

    public DvrDialog(Activity context, int type2, int keyCode2, BannerView dialog2, int mode) {
        super(context, 2131755420);
        this.type = type2;
        this.keyCode = keyCode2;
        this.mContext = context;
        this.mActivity = context;
        this.bannerView = dialog2;
        this.modeValue = mode;
        if (!CommonIntegration.getInstance().isContextInit()) {
            CommonIntegration.getInstance().setContext(this.mContext.getApplicationContext());
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dreceiver = new DialogDismissRecevier();
        this.mContext.registerReceiver(this.dreceiver, new IntentFilter("com.mtk.dialog.dismiss"));
        setContentView(R.layout.pvr_dialog_one_button);
        initview(this.type);
        setPositon(0, 0);
        registerBroadcast();
    }

    private void initview(int type2) {
        int i = type2;
        this.titleView = (TextView) findViewById(R.id.comm_dialog_title);
        this.textView = (TextView) findViewById(R.id.comm_dialog_text);
        this.buttonYes = (Button) findViewById(R.id.comm_dialog_buttonYes);
        this.buttonNo = (Button) findViewById(R.id.comm_dialog_buttonNo);
        this.buttonNo.setVisibility(0);
        if (i != 40961) {
            switch (i) {
                case 1:
                    this.title = "Warning:";
                    this.titleView.setText(this.title);
                    this.buttonNo.setVisibility(0);
                    switch (this.keyCode) {
                        case 15:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_pip);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_pip);
                            } else if (this.modeValue == 5) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_pip);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    if (TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager() != null) {
                                        TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager().stop();
                                        SaveValue.getInstance(DvrDialog.this.mContext).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
                                        SaveValue.saveWorldBooleanValue(DvrDialog.this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
                                        ((TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW)).setVisibility(8);
                                    }
                                    if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
                                        DvrManager.getInstance().stopDvr();
                                    }
                                    ComponentStatusListener.getInstance().updateStatus(14, 0);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case 19:
                        case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_channel);
                            } else if (this.modeValue == 3) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_channel);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrDialog.this.stopTimeShift();
                                    StateDvr.getInstance().setChangeSource(true);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_ChangeChanel_UP, 3000);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case 20:
                        case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_channel);
                            } else if (this.modeValue == 3) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_channel);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.stopTimeShift();
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_ChangeChanel_Down, 3000);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_pip);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_pip);
                            } else if (this.modeValue == 5) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_playback_pip);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrDialog.this.stopTimeShift();
                                    if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
                                        DvrManager.getInstance().stopDvr();
                                    }
                                    if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                                        StateDvrPlayback.getInstance().stopDvrFilePlay();
                                    }
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_PIPPOP, 1000);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_epg);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_epg);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    if (CommonIntegration.getInstance().isCurrentSourceTv()) {
                                        EPGManager.getInstance(DvrDialog.this.mActivity).startEpg(DvrDialog.this.mActivity, NavBasic.NAV_REQUEST_CODE);
                                        DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    }
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_channel);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_channel);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.stopTimeShift();
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_ChangeChanel_Pre, 3000);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case 10062:
                            if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_record);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TIMESHIFT_RECORDLIST, MessageType.delayMillis5);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_Change_Source /*11234*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.stopTimeShift();
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_INACTIVE_CHANNELS, 100);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_Change_ChannelNum /*11238*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_channel);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_channel);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    if (DvrDialog.this.mSelectedChannelID != -1) {
                                        Message msg = DvrManager.getInstance().getTopHandler().obtainMessage();
                                        msg.what = DvrManager.CHANGE_CHANNEL;
                                        msg.arg1 = DvrDialog.this.mSelectedChannelID;
                                        msg.arg2 = DvrDialog.this.keyCode;
                                        DvrManager.getInstance().getTopHandler().sendMessageDelayed(msg, 3000);
                                    } else if (DvrDialog.this.getOnPVRDialogListener() != null) {
                                        DvrDialog.this.getOnPVRDialogListener().onDVRDialogListener(DvrDialog.this.keyCode);
                                    } else {
                                        DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_CHANNEL_NUMBER, 3000);
                                    }
                                }
                            });
                            this.buttonNo.setVisibility(0);
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_SCHEDULE /*11240*/:
                            if (this.scheduleItem != null) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_shcedule_before) + this.scheduleItem.getEventTitle() + this.mContext.getResources().getString(R.string.dvr_dialog_message_schedule_record);
                            }
                            this.titleView.setVisibility(4);
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    if (StateDvr.getInstance() == null || !StateDvr.getInstance().isRunning()) {
                                        DvrManager.getInstance().startSchedulePvr();
                                    } else {
                                        DvrManager.getInstance().stopDvr();
                                    }
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().clearSchedulePvr();
                                }
                            });
                            break;
                        case TYPE_TSHIFT /*11241*/:
                            if (this.scheduleItem != null) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_shcedule_before) + this.scheduleItem.getEventTitle() + this.mContext.getResources().getString(R.string.dvr_dialog_message_schedule_timeshift);
                            }
                            this.titleView.setVisibility(4);
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_TSHIFT, 0);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().clearSchedulePvr();
                                }
                            });
                            break;
                        case TYPE_SCART /*11242*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_SCART, 2500);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_CHANNEL_Desramble /*11243*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_desramble);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_INACTIVE_CHANNELS, 2500);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_Change_Source_By_URL /*11246*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_Change_Source_By_URL, 2500);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_CHANNEL_Desramble_SRC_KEY /*11247*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_desramble);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(40963, 2500);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case TYPE_Change_ChannelNum_SRC /*11248*/:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    if (DvrDialog.this.mSelectedChannelID != -1) {
                                        Message msg = DvrManager.getInstance().getTopHandler().obtainMessage();
                                        msg.what = DvrManager.CHANGE_CHANNEL;
                                        msg.arg1 = DvrDialog.this.mSelectedChannelID;
                                        msg.arg2 = DvrDialog.this.keyCode;
                                        DvrManager.getInstance().getTopHandler().sendMessageDelayed(msg, 3000);
                                    } else if (DvrDialog.this.getOnPVRDialogListener() != null) {
                                        DvrDialog.this.getOnPVRDialogListener().onDVRDialogListener(DvrDialog.this.keyCode);
                                    } else {
                                        DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_CHANNEL_NUMBER, 3000);
                                    }
                                }
                            });
                            this.buttonNo.setVisibility(0);
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case 40962:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_channel);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_channel);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(40962, 1000);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                        case 40963:
                            if (this.modeValue == 1) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                            } else if (this.modeValue == 2) {
                                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                            }
                            this.textView.setText(this.message);
                            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                    DvrManager.getInstance().stopAllRunning();
                                    DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                    DvrDialog.this.mHandler.sendEmptyMessageDelayed(40963, 2500);
                                }
                            });
                            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    DvrDialog.this.dismiss();
                                }
                            });
                            break;
                    }
            }
        } else {
            int i2 = this.keyCode;
            if (i2 != 23) {
                if (i2 == 171) {
                    if (this.modeValue == 1) {
                        this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_pip);
                    } else if (this.modeValue == 2) {
                        this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_pip);
                    }
                    this.textView.setText(this.message);
                    this.buttonYes.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            DvrDialog.this.dismiss();
                            DvrManager.getInstance().stopAllRunning();
                            DvrManager.getInstance().getTopHandler().removeMessages(1002);
                            DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_PIPPOP, 1500);
                        }
                    });
                    this.buttonNo.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            DvrDialog.this.dismiss();
                        }
                    });
                } else if (i2 != 229) {
                    if (i2 != 11234) {
                        if (i2 != 11238) {
                            switch (i2) {
                                case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                                case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                                    break;
                            }
                        }
                    } else {
                        if (this.modeValue == 1) {
                            this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_source);
                        } else if (this.modeValue == 2) {
                            this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_source);
                        }
                        this.textView.setText(this.message);
                        this.buttonYes.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                DvrDialog.this.dismiss();
                                DvrManager.getInstance().stopAllRunning();
                                DvrManager.getInstance().getTopHandler().removeMessages(1002);
                                DvrDialog.this.stopTimeShift();
                                DvrDialog.this.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_INACTIVE_CHANNELS, 100);
                            }
                        });
                        this.buttonNo.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                DvrDialog.this.dismiss();
                            }
                        });
                    }
                }
            }
            if (this.modeValue == 1) {
                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_record_channel);
            } else if (this.modeValue == 2) {
                this.message = this.mContext.getResources().getString(R.string.dvr_dialog_message_timeshift_channel);
            }
            this.textView.setText(this.message);
            this.buttonNo.setVisibility(0);
            this.buttonYes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DvrDialog.this.dismiss();
                    DvrManager.getInstance().stopAllRunning();
                    DvrDialog.this.stopTimeShift();
                    if (DvrDialog.this.keyCode == 166) {
                        DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_ChangeChanel_UP, 3000);
                    } else if (DvrDialog.this.keyCode == 167) {
                        DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_ChangeChanel_Down, 3000);
                    } else if (DvrDialog.this.keyCode == 229) {
                        DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_ChangeChanel_Pre, 3000);
                    } else if (DvrDialog.this.keyCode == 23) {
                        DvrDialog.this.mHandler.sendEmptyMessageDelayed(DvrDialog.TYPE_CHANNEL_CENTER, 3000);
                    }
                }
            });
            this.buttonNo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DvrDialog.this.dismiss();
                }
            });
        }
        this.buttonYes.setFocusable(true);
        this.buttonYes.requestFocus();
        this.titleView.setImportantForAccessibility(1);
    }

    /* access modifiers changed from: private */
    public void stopTimeShift() {
        if (TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager() != null) {
            TurnkeyUiMainActivity.getInstance().getmTifTimeShiftManager().stopAll();
            SaveValue.getInstance(this.mContext).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
            SaveValue.saveWorldBooleanValue(this.mContext, MenuConfigManager.TIMESHIFT_START, false, false);
            ((TifTimeshiftView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_TIFTIMESHIFT_VIEW)).setVisibility(8);
        }
    }

    public void setPositon(int xoff, int yoff) {
        Window window2 = getWindow();
        WindowManager.LayoutParams lp2 = window2.getAttributes();
        lp2.x = xoff;
        lp2.y = yoff;
        lp2.width = MediaPlayer2.MEDIA_INFO_VIDEO_TRACK_LAGGING;
        lp2.height = 400;
        this.xOff = xoff;
        this.yOff = yoff;
        window2.setAttributes(lp2);
    }

    public void dismiss() {
        super.dismiss();
        DvrManager.getInstance().setBGMState(false);
        DvrManager.getInstance().isPvrDialogShow = false;
        DvrManager.getInstance().setVisibility(8);
        try {
            this.mContext.unregisterReceiver(this.receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Button getButtonYes() {
        return this.buttonYes;
    }

    public Button getButtonNo() {
        return this.buttonNo;
    }

    public void setButtonYesName(String buttonYesName2) {
        this.buttonYesName = buttonYesName2;
    }

    public void setButtonNoName(String buttonNoName2) {
        this.buttonNoName = buttonNoName2;
    }

    public void setMessage(String info) {
        this.message = info;
    }

    public void run() {
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getWaitView() {
        return this.waitView;
    }

    public Loading getLoading() {
        return this.loading;
    }

    public boolean onKeyUp(int keyCode2, KeyEvent event) {
        super.onKeyUp(keyCode2, event);
        if (keyCode2 == 24 || keyCode2 == 25) {
            return true;
        }
        return super.onKeyDown(keyCode2, event);
    }

    public boolean onKeyDown(int keyCode2, KeyEvent event) {
        int keyCode3 = KeyMap.getKeyCode(keyCode2, event);
        switch (keyCode3) {
            case 4:
                dismiss();
                break;
            case 82:
            case KeyMap.KEYCODE_MTKIR_SOURCE /*178*/:
                dismiss();
                TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode3, event);
                break;
            case 85:
            case 89:
            case 90:
            case KeyMap.KEYCODE_MTKIR_FREEZE /*10467*/:
                DvrManager.getInstance().KeyHandler(keyCode3, event);
                return true;
            case 86:
                DvrManager.getInstance().KeyHandler(keyCode3, event);
                dismiss();
                return true;
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
            case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
            case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
            case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
                dismiss();
                DvrManager.getInstance().KeyHandler(keyCode3, event);
                return true;
            case 10062:
                return true;
        }
        return super.onKeyDown(keyCode3, event);
    }

    public void setChangeChannelNum(String value) {
        this.mSelectedChannelNumString = value;
    }

    public void setMtkTvChannelInfoBase(int channelID) {
        this.mSelectedChannelID = channelID;
    }

    public void setFavChannel(TVChannel selectChannel) {
        this.mFavChannel = selectChannel;
    }

    public TVChannel getFavChannel() {
        return this.mFavChannel;
    }

    public OnDVRDialogListener getOnPVRDialogListener() {
        return this.onPVRDialogListener;
    }

    public void setOnPVRDialogListener(OnDVRDialogListener onPVRDialogListener2) {
        this.onPVRDialogListener = onPVRDialogListener2;
    }

    class DialogDismissRecevier extends BroadcastReceiver {
        DialogDismissRecevier() {
        }

        public void onReceive(Context context, Intent intent) {
            DvrDialog.this.dismiss();
        }
    }

    public void show() {
        if (TurnkeyUiMainActivity.getInstance() != null) {
            TurnkeyUiMainActivity.getInstance();
            TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
        }
        ZoomTipView mZoomTip = (ZoomTipView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
        if (mZoomTip != null) {
            mZoomTip.setVisibility(8);
        }
        SundryShowTextView stxtView = (SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
        if (stxtView != null) {
            stxtView.setVisibility(8);
        }
        if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
            StateDvrFileList.getInstance().dissmiss();
        }
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            StateDvr.getInstance().getHandler().sendEmptyMessage(10001);
        }
        DvrManager.getInstance().setPvrDialogShow(true);
        super.show();
    }

    public MtkTvBookingBase getScheduleItem() {
        return this.scheduleItem;
    }

    public void setScheduleItem(MtkTvBookingBase scheduleItem2) {
        this.scheduleItem = scheduleItem2;
    }

    class pvrReceiver extends BroadcastReceiver {
        pvrReceiver() {
        }

        public void onReceive(Context arg0, Intent intent) {
            MtkLog.e("DvrDialog", "pvrdialog==DISSMISS_DIALOG==");
            if (intent.getAction().equals("com.mediatek.dialog.dismiss")) {
                DvrDialog.this.dismiss();
            }
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void setUri(Uri uri2) {
        this.uri = uri2;
    }
}
