package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.CustListView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.adapter.SundryModeAdapter;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.ArrayList;
import java.util.List;

public class SundryShowWithDialog extends NavBasicDialog {
    private static final int LIST_PAGE_MAX = 7;
    private static final String TAG = "SundryShowWithDialog";
    private ComponentsManager comManager;
    private int index;
    /* access modifiers changed from: private */
    public int lastPressKeyCode;
    private CommonIntegration mCommonIntegration;
    private IntegrationZoom mIntegrationZoom;
    /* access modifiers changed from: private */
    public SundryImplement mSundryImplement;
    /* access modifiers changed from: private */
    public SundryModeAdapter mSundryModeAdapter;
    private ZoomTipView mZoomTip;
    private CustListView.UpDateListView modeListUpdate;
    /* access modifiers changed from: private */
    public CustListView sundryModeListView;
    private TextView sundryModeTitleTextView;
    private List<Integer> supportPictureModeList;
    private List<Integer> supportScreenModeList;
    private List<Integer> supportSoundEffectList;

    public SundryShowWithDialog(Context context, int theme) {
        super(context, theme);
        this.index = 0;
        this.modeListUpdate = new CustListView.UpDateListView() {
            public void updata() {
                if (SundryShowWithDialog.this.sundryModeListView.getSelectedItemPosition() == 0) {
                    SundryShowWithDialog.this.mSundryModeAdapter.updateList(SundryShowWithDialog.this.sundryModeListView.getPreList());
                } else {
                    SundryShowWithDialog.this.mSundryModeAdapter.updateList(SundryShowWithDialog.this.sundryModeListView.getNextList());
                }
                SundryShowWithDialog.this.sundryModeListView.setAdapter(SundryShowWithDialog.this.mSundryModeAdapter);
            }
        };
        this.mSundryImplement = SundryImplement.getInstanceNavSundryImplement(context);
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.comManager = ComponentsManager.getInstance();
        this.mIntegrationZoom = IntegrationZoom.getInstance(context);
        this.mSundryModeAdapter = new SundryModeAdapter(context);
        this.mZoomTip = (ZoomTipView) this.comManager.getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN);
    }

    public SundryShowWithDialog(Context context) {
        this(context, R.style.nav_dialog);
        this.componentID = NavBasic.NAV_COMP_ID_SUNDRY_DIALOG;
        setContentView(R.layout.nav_sundry_dialog_list);
        initDialog();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean isKeyHandler(int keyCode) {
        int i = 0;
        if (1 == SaveValue.getInstance(this.mContext).readValue(MenuConfigManager.MODE_LIST_STYLE)) {
            MtkLog.v(TAG, "isKeyHandler");
            if (keyCode == 222) {
                this.lastPressKeyCode = keyCode;
                int[] supportSoundEffect = this.mSundryImplement.getSupportSoundEffects();
                this.supportSoundEffectList.clear();
                if (supportSoundEffect != null) {
                    while (i < supportSoundEffect.length) {
                        this.supportSoundEffectList.add(Integer.valueOf(supportSoundEffect[i]));
                        i++;
                    }
                }
                updataModeDate(this.supportSoundEffectList, getCurrentValueIndex(supportSoundEffect, this.mSundryImplement.getCurrentSoundEffect()), keyCode);
                this.sundryModeTitleTextView.setText(R.string.menu_audio_equalize);
                return true;
            } else if (keyCode == 251) {
                this.lastPressKeyCode = keyCode;
                int[] supportPictureModes = this.mSundryImplement.getSupportPictureModes();
                this.supportPictureModeList.clear();
                if (supportPictureModes != null) {
                    while (i < supportPictureModes.length) {
                        this.supportPictureModeList.add(Integer.valueOf(supportPictureModes[i]));
                        i++;
                    }
                }
                updataModeDate(this.supportPictureModeList, getCurrentValueIndex(supportPictureModes, this.mSundryImplement.getCurrentPictureMode()), keyCode);
                this.sundryModeTitleTextView.setText(R.string.menu_video_picture_mode);
                return true;
            } else if (keyCode == 10471) {
                this.lastPressKeyCode = keyCode;
                int[] supportScreenModes = this.mSundryImplement.getSupportScreenModes();
                if (supportScreenModes != null) {
                    if (!this.mIsComponetShow) {
                        if (this.mSundryImplement.isFreeze()) {
                            this.mSundryImplement.setFreeze(false);
                        }
                        if (1 != this.mIntegrationZoom.getCurrentZoom() && !this.mCommonIntegration.isPipOrPopState()) {
                            if (this.mZoomTip != null && this.mZoomTip.getVisibility() == 0) {
                                this.mZoomTip.setVisibility(8);
                            }
                            if (ComponentsManager.getNativeActiveCompId() != 33554435) {
                                this.mIntegrationZoom.setZoomMode(1);
                            }
                        }
                        this.supportScreenModeList.clear();
                        while (i < supportScreenModes.length) {
                            this.supportScreenModeList.add(Integer.valueOf(supportScreenModes[i]));
                            i++;
                        }
                        updataModeDate(this.supportScreenModeList, getCurrentValueIndex(supportScreenModes, this.mSundryImplement.getCurrentScreenMode()), keyCode);
                        this.sundryModeTitleTextView.setText(R.string.menu_setup_screenmode);
                    }
                    return true;
                }
            }
        }
        MtkLog.v(TAG, "isKeyHandler false");
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        boolean changeValueFlag;
        int[] supportScreenModes;
        MtkLog.d(TAG, "come in KeyHandler,keycode == " + keyCode);
        boolean isHandler = true;
        if (!this.mIsComponetShow || this.lastPressKeyCode != keyCode) {
            changeValueFlag = false;
        } else {
            changeValueFlag = true;
        }
        switch (keyCode) {
            case 4:
                dismiss();
                return true;
            case 19:
            case 20:
                if (this.mCommonIntegration.isPipOrPopState()) {
                    dismiss();
                    isHandler = false;
                    break;
                } else {
                    return true;
                }
            case 24:
            case 25:
            case 82:
            case KeyMap.KEYCODE_MTKIR_INFO /*165*/:
            case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
            case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
            case 227:
            case 10062:
            case 10065:
                dismiss();
                isHandler = false;
                break;
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
            case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
                return true;
            case 222:
                int[] supportSoundEffect = this.mSundryImplement.getSupportSoundEffects();
                MtkLog.d(TAG, "come in KeyHandler supportSoundEffect.length ==" + supportSoundEffect.length);
                this.supportSoundEffectList.clear();
                if (supportSoundEffect != null) {
                    for (int valueOf : supportSoundEffect) {
                        this.supportSoundEffectList.add(Integer.valueOf(valueOf));
                    }
                    if (changeValueFlag) {
                        MtkLog.d(TAG, "come in KeyHandler supportSoundEffectList.size()===" + this.supportSoundEffectList.size());
                        this.index = getCurrentValueIndex(supportSoundEffect, this.mSundryImplement.getCurrentSoundEffect());
                        if (this.index < this.supportSoundEffectList.size() - 1) {
                            this.index++;
                        } else {
                            this.index = 0;
                        }
                        if (this.mSundryImplement.getSupportSoundEffects() != null) {
                            this.mSundryImplement.setCurrentSoundEffect(this.mSundryImplement.getSupportSoundEffects()[this.index]);
                        }
                    } else {
                        this.index = getCurrentValueIndex(supportSoundEffect, this.mSundryImplement.getCurrentSoundEffect());
                        this.sundryModeTitleTextView.setText(R.string.menu_audio_equalize);
                    }
                    updataModeDate(this.supportSoundEffectList, this.index, keyCode);
                    break;
                } else {
                    return true;
                }
            case 251:
                int[] supportPictureModes = this.mSundryImplement.getSupportPictureModes();
                this.supportPictureModeList.clear();
                if (supportPictureModes != null) {
                    for (int valueOf2 : supportPictureModes) {
                        this.supportPictureModeList.add(Integer.valueOf(valueOf2));
                    }
                    if (changeValueFlag) {
                        this.index = getCurrentValueIndex(supportPictureModes, this.mSundryImplement.getCurrentPictureMode());
                        if (this.index < this.supportPictureModeList.size() - 1) {
                            this.index++;
                        } else {
                            this.index = 0;
                        }
                        if (this.mSundryImplement.getSupportPictureModes() != null) {
                            this.mSundryImplement.setCurrentPictureMode(this.mSundryImplement.getSupportPictureModes()[this.index]);
                        }
                    } else {
                        this.index = getCurrentValueIndex(supportPictureModes, this.mSundryImplement.getCurrentPictureMode());
                        this.sundryModeTitleTextView.setText(R.string.menu_video_picture_mode);
                    }
                    updataModeDate(this.supportPictureModeList, this.index, keyCode);
                    break;
                } else {
                    return true;
                }
            case 10471:
                if (changeValueFlag) {
                    int result = 0;
                    MtkLog.d(TAG, "come in KeyHandler KEYCODE_MTKIR_ASPECT changeValueFlag");
                    this.mCommonIntegration.updateOutputChangeState("Before_ASP_Ratio_chg");
                    do {
                        supportScreenModes = this.mSundryImplement.getSupportScreenModes();
                        if (supportScreenModes != null) {
                            this.supportScreenModeList.clear();
                            for (int valueOf3 : supportScreenModes) {
                                this.supportScreenModeList.add(Integer.valueOf(valueOf3));
                            }
                            MtkLog.d(TAG, "come in KeyHandler KEYCODE_MTKIR_ASPECT getCurrentScreenMode == " + this.mSundryImplement.getCurrentScreenMode());
                            this.index = getCurrentValueIndex(supportScreenModes, this.mSundryImplement.getCurrentScreenMode());
                            MtkLog.d(TAG, "come in KeyHandler KEYCODE_MTKIR_ASPECT get current index == " + this.index);
                            if (this.index < this.supportScreenModeList.size() - 1) {
                                this.index++;
                            } else {
                                this.index = 0;
                            }
                            MtkLog.d(TAG, "come in KeyHandler KEYCODE_MTKIR_ASPECT change next screen mode index == " + this.index);
                            result = this.mSundryImplement.setCurrentScreenMode(supportScreenModes[this.index]);
                            MtkLog.d(TAG, "come in KeyHandler KEYCODE_MTKIR_ASPECT change next screen mode result == " + result);
                            continue;
                        }
                    } while (result != 0);
                    this.mCommonIntegration.updateOutputChangeState("After_ASP_Ratio_chg");
                } else {
                    supportScreenModes = this.mSundryImplement.getSupportScreenModes();
                    if (supportScreenModes != null) {
                        this.supportScreenModeList.clear();
                        for (int valueOf4 : supportScreenModes) {
                            this.supportScreenModeList.add(Integer.valueOf(valueOf4));
                        }
                        this.index = getCurrentValueIndex(supportScreenModes, this.mSundryImplement.getCurrentScreenMode());
                        if (this.mSundryImplement.isFreeze()) {
                            this.mSundryImplement.setFreeze(false);
                        }
                        if (1 != this.mIntegrationZoom.getCurrentZoom()) {
                            if (this.mZoomTip != null && this.mZoomTip.getVisibility() == 0) {
                                this.mZoomTip.setVisibility(8);
                            }
                            this.mIntegrationZoom.setZoomMode(1);
                        }
                    }
                    this.sundryModeTitleTextView.setText(R.string.menu_setup_screenmode);
                }
                if (supportScreenModes == null) {
                    isHandler = false;
                    dismiss();
                    break;
                } else {
                    updataModeDate(this.supportScreenModeList, this.index, keyCode);
                    break;
                }
            default:
                isHandler = false;
                break;
        }
        if (isHandler) {
            this.lastPressKeyCode = keyCode;
            startTimeout(5000);
        }
        if (isHandler || TurnkeyUiMainActivity.getInstance() == null) {
            return isHandler;
        }
        MtkLog.d(TAG, "come in keyHandler dispatch key to turnkey");
        return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode, event);
    }

    public boolean isCoExist(int componentID) {
        switch (componentID) {
            case NavBasic.NAV_COMP_ID_BANNER /*16777218*/:
            case NavBasic.NAV_COMP_ID_ZOOM_PAN /*16777223*/:
            case NavBasic.NAV_COMP_ID_POP /*16777235*/:
            case NavBasic.NAV_COMP_ID_PVR_TIMESHIFT /*16777241*/:
                return true;
            case NavBasic.NAV_COMP_ID_CEC /*16777220*/:
                if (10467 == this.lastPressKeyCode) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    private void initDialog() {
        this.sundryModeTitleTextView = (TextView) findViewById(R.id.nav_sundry_mode_title);
        this.sundryModeListView = (CustListView) findViewById(R.id.nav_sundry_mode_listview);
        this.sundryModeListView.setOnKeyListener(new SundryModeListOnKey());
        this.supportPictureModeList = new ArrayList();
        this.supportSoundEffectList = new ArrayList();
        this.supportScreenModeList = new ArrayList();
        WindowManager windowManager = getWindow().getWindowManager();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.27d);
        lp.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.56d);
        lp.x = -((int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.2d));
        lp.y = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.4d);
        window.setAttributes(lp);
    }

    public void show() {
        super.show();
        startTimeout(5000);
    }

    class SundryModeListOnKey implements View.OnKeyListener {
        SundryModeListOnKey() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int slectPosition = SundryShowWithDialog.this.sundryModeListView.getSelectedItemPosition();
            if (slectPosition < 0) {
                slectPosition = 0;
            }
            if (event.getAction() != 0) {
                return false;
            }
            if (keyCode != 23) {
                switch (keyCode) {
                    case 19:
                        SundryShowWithDialog.this.startTimeout(5000);
                        if (slectPosition != 0 || SundryShowWithDialog.this.sundryModeListView.hasPrePage()) {
                            return false;
                        }
                        return true;
                    case 20:
                        SundryShowWithDialog.this.startTimeout(5000);
                        return false;
                    default:
                        return false;
                }
            } else {
                int selectedValue = ((Integer) SundryShowWithDialog.this.sundryModeListView.getSelectedItem()).intValue();
                MtkLog.d(SundryShowWithDialog.TAG, "come in SundryModeListOnKey get selected item vlaue == " + selectedValue);
                int access$100 = SundryShowWithDialog.this.lastPressKeyCode;
                if (access$100 != 222) {
                    if (access$100 == 251) {
                        if (selectedValue != SundryShowWithDialog.this.mSundryImplement.getCurrentPictureMode()) {
                            MtkLog.d(SundryShowWithDialog.TAG, "come in SundryModeListOnKey setCurrentPictureMode vlaue == " + selectedValue);
                            SundryShowWithDialog.this.mSundryImplement.setCurrentPictureMode(selectedValue);
                        } else {
                            SundryShowWithDialog.this.dismiss();
                        }
                    }
                } else if (selectedValue != SundryShowWithDialog.this.mSundryImplement.getCurrentSoundEffect()) {
                    MtkLog.d(SundryShowWithDialog.TAG, "come in SundryModeListOnKey setCurrentSoundEffect vlaue == " + selectedValue);
                    SundryShowWithDialog.this.mSundryImplement.setCurrentSoundEffect(selectedValue);
                } else {
                    SundryShowWithDialog.this.dismiss();
                }
                SundryShowWithDialog.this.startTimeout(5000);
                return true;
            }
        }
    }

    public int getCurrentValueIndex(int[] currentArray, int value) {
        if (currentArray != null) {
            for (int i = 0; i < currentArray.length; i++) {
                if (value == currentArray[i]) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void updataModeDate(List<Integer> modeList, int currentModeIndex, int key) {
        this.sundryModeListView.initData(modeList, 7, this.modeListUpdate);
        int currentIndexPage = (currentModeIndex / 7) + 1;
        if (currentIndexPage > 1) {
            this.mSundryModeAdapter.updateList(this.sundryModeListView.getListWithPage(currentIndexPage), key);
        } else {
            this.mSundryModeAdapter.updateList(this.sundryModeListView.getCurrentList(), key);
        }
        this.sundryModeListView.setAdapter(this.mSundryModeAdapter);
        this.sundryModeListView.setFocusable(true);
        MtkLog.d(TAG, "come in updataModeDate setSelection currentModeIndex ==" + currentModeIndex);
        this.sundryModeListView.setSelection(currentModeIndex % 7);
        MtkLog.d(TAG, "come in updataModeDate setSelection ===" + (currentModeIndex % 7));
        this.sundryModeListView.requestFocus();
    }
}
