package com.mediatek.wwtv.tvcenter.capturelogo;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.capturelogo.TVStorage;
import com.mediatek.wwtv.tvcenter.epg.EPGManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class CaptureLogoActivity extends Activity {
    public static final int AUTO_DISMISS_TIME = 5000;
    private static final int CAPTURELOGO_CANCEL = -1;
    public static final String FROM_MMP = "mmp";
    public static final int MESSAGE_DISMISS = 1;
    public static final int MMP_PHOTO = 1;
    public static final int MMP_VIDEO = 2;
    private static final String TAG = "CaptureLogoDialog";
    public static LinearLayout captureMain = null;
    static int selectSaveId = 0;
    private String bt_left;
    private String bt_right;
    private Button button1;
    private Button button2;
    /* access modifiers changed from: private */
    public CaptureLogoImp capImp;
    private String content;
    private boolean isFreezing = false;
    private TVStorage.LogoCaptureListener mCaptureListener = new TVStorage.LogoCaptureListener() {
        public void onEvent(int event) {
            CaptureLogoActivity.this.mHandler.removeMessages(1);
            CaptureLogoActivity.this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis4);
            CaptureLogoActivity.this.createDialog(5, event);
        }
    };
    private ProgressBar mCapturingProgressBar;
    private int mContentViewId;
    private TextView mContextTextView;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                CaptureLogoActivity.this.finish();
                MtkLog.d(CaptureLogoActivity.TAG, " -------receive message----------");
            }
        }
    };
    private View mRootView;
    private TextView mSavePosition;
    /* access modifiers changed from: private */
    public CaptureSelectArea mSelectArea;
    private int mType = -1;
    String[] saveIds = {"0", "1"};
    private LinearLayout saveLogoView = null;
    int status = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_capture_logo_main_view);
        this.capImp = CaptureLogoImp.getInstance(this);
        this.isFreezing = this.capImp.isFreeze();
        if (!this.isFreezing) {
            this.capImp.freezeScreen(true);
        }
        this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis4);
        initUI();
        initData();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (!this.isFreezing) {
            this.capImp.freezeScreen(false);
        }
    }

    public int getContentViewId() {
        return this.mContentViewId;
    }

    public void setContentViewId(int mContentViewId2) {
        this.mContentViewId = mContentViewId2;
    }

    private void initUI() {
        this.mContextTextView = (TextView) findViewById(R.id.cap_logo_msg);
        captureMain = (LinearLayout) findViewById(R.id.capture_main);
        captureMain.setLayoutParams(new LinearLayout.LayoutParams((int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.43d), (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.417d)));
        this.button1 = (Button) findViewById(R.id.bt_left);
        this.button2 = (Button) findViewById(R.id.bt_right);
        this.mCapturingProgressBar = (ProgressBar) findViewById(R.id.cap_logo_progressbar);
        this.saveLogoView = (LinearLayout) findViewById(R.id.savePositionView);
        this.mSavePosition = (TextView) findViewById(R.id.save_position_select);
        this.mRootView = findViewById(R.id.capturelogo_bg);
    }

    public void setMyProgressBarVisibility(int v) {
        this.mCapturingProgressBar.setVisibility(v);
    }

    public void setSelectSavePositionView(int v) {
        this.saveLogoView.setVisibility(v);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis4);
        if (keyCode != 82) {
            if (keyCode != 130) {
                if (keyCode != 172) {
                    if (keyCode != 185) {
                        switch (keyCode) {
                            case 19:
                                if (this.status == 3) {
                                    this.mSavePosition.setBackgroundResource(R.drawable.selectbg_nor);
                                    break;
                                }
                                break;
                            case 20:
                                if (this.status == 3) {
                                    this.mSavePosition.setBackgroundResource(R.drawable.selectbg_gray);
                                    this.button1.requestFocus();
                                    break;
                                }
                                break;
                            case 21:
                                if (this.status == 3 && !this.button1.hasFocus() && !this.button2.hasFocus()) {
                                    if (selectSaveId > 0) {
                                        selectSaveId--;
                                    } else {
                                        selectSaveId = this.saveIds.length - 1;
                                    }
                                    this.mSavePosition.setText(this.saveIds[selectSaveId]);
                                    break;
                                }
                            case 22:
                                if (this.status == 3 && !this.button1.hasFocus() && !this.button2.hasFocus()) {
                                    if (selectSaveId < this.saveIds.length - 1) {
                                        selectSaveId++;
                                    } else {
                                        selectSaveId = 0;
                                    }
                                    this.mSavePosition.setText(this.saveIds[selectSaveId]);
                                    break;
                                }
                            case 23:
                                if (this.status == 5) {
                                    finish();
                                    break;
                                }
                                break;
                        }
                    }
                } else if (EPGManager.getInstance(this).startEpg(this, NavBasic.NAV_REQUEST_CODE)) {
                    finish();
                }
            }
            MtkLog.d(TAG, "------------------ pressed YELLOW KEY");
            if (!(keyCode == 185 && (this.mType == 2 || this.mType == 1))) {
                if (this.status == 4) {
                    this.capImp.removeLogoCaptureListener(getCaptureSourceType());
                }
                finish();
            }
        } else if (!(this.mType == 2 || this.mType == 1 || this.status != 4)) {
            this.capImp.removeLogoCaptureListener(getCaptureSourceType());
        }
        return super.onKeyDown(keyCode, event);
    }

    public void createDialog(int id) {
        if (id != 6) {
            switch (id) {
                case 2:
                    this.status = 2;
                    this.content = getResources().getString(R.string.cplogo_msg_this_screen);
                    this.bt_left = getResources().getString(R.string.cplogo_bt_ok);
                    this.bt_right = getResources().getString(R.string.cplogo_bt_cancel);
                    this.button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CaptureLogoActivity.this.createDialog(3);
                        }
                    });
                    this.button2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CaptureLogoActivity.this.finish();
                        }
                    });
                    break;
                case 3:
                    this.status = 3;
                    this.content = getResources().getString(R.string.cplogo_msg_select_position);
                    setSelectSavePositionView(0);
                    this.mSavePosition.setText("0");
                    this.bt_left = getResources().getString(R.string.cplogo_bt_ok);
                    this.bt_right = getResources().getString(R.string.cplogo_bt_cancel);
                    this.button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CaptureLogoActivity.this.capImp.setSavePosition(Integer.valueOf(CaptureLogoActivity.this.saveIds[CaptureLogoActivity.selectSaveId]).intValue());
                            CaptureLogoActivity.this.setSelectSavePositionView(8);
                            CaptureLogoActivity.this.createDialog(4);
                        }
                    });
                    this.button2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CaptureLogoActivity.this.finish();
                        }
                    });
                    break;
                case 4:
                    this.mHandler.removeMessages(1);
                    this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis2);
                    this.status = 4;
                    this.content = getResources().getString(R.string.cplogo_msg_capturing);
                    this.bt_right = getResources().getString(R.string.cplogo_bt_cancel);
                    int type = getCaptureSourceType();
                    if (type != TVStorage.CAP_LOGO_MM_IMAGE) {
                        this.capImp.setLogoCaptureListener(this.mCaptureListener, type);
                    }
                    setMyProgressBarVisibility(0);
                    this.button1.setVisibility(4);
                    this.button2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CaptureLogoActivity.this.capImp.removeLogoCaptureListener(CaptureLogoActivity.this.getCaptureSourceType());
                            CaptureLogoActivity.this.createDialog(5, -1);
                        }
                    });
                    break;
            }
        } else {
            this.status = 6;
            this.content = getResources().getString(R.string.cplogo_msg_adjust_position);
            this.bt_left = getResources().getString(R.string.cplogo_bt_no);
            this.bt_right = getResources().getString(R.string.cplogo_bt_adjust);
            this.button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (CaptureLogoActivity.this.mSelectArea == null) {
                        CaptureSelectArea unused = CaptureLogoActivity.this.mSelectArea = new CaptureSelectArea(CaptureLogoActivity.this);
                        CaptureLogoActivity.this.mSelectArea.setHandler(CaptureLogoActivity.this.mHandler);
                    }
                    CaptureLogoActivity.this.capImp.setSpecialArea(CaptureLogoActivity.this.mSelectArea.getCaptureArea());
                    CaptureLogoActivity.this.mSelectArea.setVisibility(4);
                    CaptureLogoActivity.this.createDialog(3);
                }
            });
            this.button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CaptureLogoActivity.captureMain.setVisibility(4);
                    CaptureLogoActivity.this.showSelectAreaView();
                }
            });
        }
        this.mContextTextView.setText(this.content);
        this.button1.setText(this.bt_left);
        this.button2.setText(this.bt_right);
    }

    /* access modifiers changed from: private */
    public int getCaptureSourceType() {
        int type = TVStorage.CAP_LOGO_TV;
        if (this.mType == 1) {
            return TVStorage.CAP_LOGO_MM_IMAGE;
        }
        if (this.mType == 2) {
            return TVStorage.CAP_LOGO_MM_VIDEO;
        }
        return type;
    }

    /* access modifiers changed from: protected */
    public void createDialog(int id, int result) {
        MtkLog.d(TAG, "capturing result : " + result);
        if (5 == id) {
            this.status = 5;
            if (result == 0) {
                this.content = getResources().getString(R.string.cplogo_msg_res_success);
            } else if (result == 1) {
                this.content = getResources().getString(R.string.cplogo_msg_res_fail);
            } else {
                this.content = getResources().getString(R.string.cplogo_capture_res_cancel);
            }
            this.capImp.finishLogoCaputer(getCaptureSourceType());
            this.button1.setVisibility(4);
            this.button2.setVisibility(4);
            setMyProgressBarVisibility(4);
            this.mContextTextView.setText(this.content);
        }
    }

    public void showSelectAreaView() {
        if (this.mSelectArea == null) {
            MtkLog.d(TAG, "showSelectAreaView------>mSelectArea == null");
            this.mSelectArea = new CaptureSelectArea(this);
            this.mSelectArea.setFocusable(true);
            this.mSelectArea.setHandler(this.mHandler);
            addContentView(this.mSelectArea, new ViewGroup.LayoutParams(-2, -2));
            this.mSelectArea.requestFocus();
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis4);
        } else {
            MtkLog.d(TAG, "showSelectAreaView------>mSelectArea =!null");
            this.mSelectArea.setVisibility(0);
            this.mSelectArea.requestFocus();
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, MessageType.delayMillis4);
        }
        MtkLog.d(TAG, "select area has focus: " + this.mSelectArea.hasFocus());
    }

    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mType = bundle.getInt(FROM_MMP, -1);
            if (this.mType == 1 || this.mType == 2) {
                createDialog(2);
                if (this.mType == 1) {
                    this.mRootView.setBackgroundResource(R.drawable.translucent_background);
                    return;
                }
                return;
            }
        }
        if (CommonIntegration.getInstance().isCurrentSourceTv()) {
            createDialog(2);
            return;
        }
        this.status = 1;
        if (this.mContextTextView != null) {
            this.mContextTextView.setText(getResources().getString(R.string.cplogo_msg_which_screen));
        }
        this.button1.setText(getResources().getString(R.string.cplogo_bt_full_screen));
        this.button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CaptureLogoActivity.this.createDialog(2);
                CaptureLogoActivity.this.capImp.setSpecialArea((Rect) null);
            }
        });
        this.button2.setText(getResources().getString(R.string.cplogo_bt_special_area));
        this.button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CaptureLogoActivity.captureMain.setVisibility(4);
                CaptureLogoActivity.this.showSelectAreaView();
            }
        });
    }

    public class DialogId {
        public static final int ADJUST_POSITION = 6;
        public static final int CAPTURE_RESULT = 5;
        public static final int CAPTURE_THIS_SCREEN = 2;
        public static final int CAPTURING = 4;
        public static final int SELECT_SAVE_POSITION = 3;
        public static final int WHICH_AREA = 1;

        public DialogId() {
        }
    }
}
