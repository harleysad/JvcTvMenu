package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEWSPABase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.MtkTvEWSPA;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.AudioFocusManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.Util;
import java.io.IOException;

public class EWSDialog extends NavBasicDialog implements ComponentStatusListener.ICStatusListener {
    public static final int STATUS_AWAS = 1;
    public static final int STATUS_SIAGA = 2;
    public static final int STATUS_WASPADA = 3;
    private static final String TAG = "EWSDialog";
    private TextView areaView;
    private Drawable[] authorityDrawables;
    private ImageView authorityView;
    private TextView bencanaView;
    private Context context;
    private Drawable[] disasterLogoDrawables;
    private ImageView disasterLogoView;
    private MtkTvEWSPABase.EwsInfo ewsInfo;
    private int ewsStatus;
    private TextView infoView;
    private boolean isPlayTone;
    private TextView karakterView;
    private MtkTvEWSPA mMtkTvEWSPA;
    private MediaPlayer mPlayer;
    private LinearLayout middleLayout;
    private TextView pesanView;
    private TextView posisiView;
    private TextView statusView;
    private TextView tanggalView;
    private String testString;
    private int volume;

    public EWSDialog(Context context2, int theme) {
        super(context2, theme);
        this.volume = 0;
        this.ewsStatus = 0;
        this.isPlayTone = false;
        this.testString = "testLo1dshowSpecialView~ lastIndex2showSpecialView~ lastIndex2showSpecialView~ lastIndex2showSpecialView~ lastIndex2showSpecialView~ lastIndex2";
        MtkLog.d(TAG, "new EWSDialog");
        this.componentID = NavBasic.NAV_COMP_ID_EWS;
        this.context = context2;
        this.mMtkTvEWSPA = MtkTvEWSPA.getInstance();
        this.mMtkTvEWSPA.createMonitorInst((byte) 0);
        String value = MtkTvConfig.getInstance().getConfigString("g_eas__lct_ct");
        MtkLog.d(TAG, "postalcode:[" + value + "]");
        MtkTvEWSPABase.EwspaRet ret = MtkTvEWSPA.getInstance().setLocationCode(Util.stringToByte(value));
        MtkLog.d(TAG, "setLocationCode response:" + ret);
        MtkLog.d(TAG, "createMonitorInst");
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public EWSDialog(Context context2) {
        this(context2, R.style.nav_dialog);
    }

    public boolean isCoExist(int componentID) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_ews_view);
        findViews();
        prepareResource();
        this.mPlayer = new MediaPlayer();
    }

    public boolean isKeyHandler(int keyCode) {
        MtkLog.d(TAG, "isKeyHandler,keyCode:" + keyCode);
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        MtkLog.d(TAG, "dispatchKeyEvent||ewsStatus =" + this.ewsStatus);
        MtkLog.d(TAG, "dispatchKeyEvent||getKeyCode =" + event.getKeyCode());
        switch (this.ewsStatus) {
            case 1:
            case 2:
                if (!(event.getKeyCode() == 20 || event.getKeyCode() == 19)) {
                    return true;
                }
        }
        return super.dispatchKeyEvent(event);
    }

    public void show() {
        if (!CommonIntegration.getInstance().isCurrentSourceDTV()) {
            MtkLog.d(TAG, "showEWS||CurrentSource not DTV");
            return;
        }
        ComponentsManager.getInstance().hideAllComponents();
        super.show();
        if (this.mMtkTvEWSPA != null) {
            this.ewsInfo = this.mMtkTvEWSPA.getEWSInfo((byte) 0);
            if (this.ewsInfo == null) {
                MtkLog.d(TAG, "showEWS||ewsInfo is null");
                return;
            }
            MtkLog.d(TAG, "ewsInfo:locationTypeCode = " + this.ewsInfo.locationTypeCode);
            MtkLog.d(TAG, "ewsInfo:disasterCode = " + this.ewsInfo.disasterCode);
            MtkLog.d(TAG, "ewsInfo:authority = " + this.ewsInfo.authority);
            MtkLog.d(TAG, "ewsInfo:charLocationCode = " + this.ewsInfo.charLocationCode);
            MtkLog.d(TAG, "ewsInfo:charDisasterCode = " + this.ewsInfo.charDisasterCode);
            MtkLog.d(TAG, "ewsInfo:charDisasterDate = " + this.ewsInfo.charDisasterDate);
            MtkLog.d(TAG, "ewsInfo:charDisasterPosition = " + this.ewsInfo.charDisasterPosition);
            MtkLog.d(TAG, "ewsInfo:charDisasterCharacterstic = " + this.ewsInfo.charDisasterCharacterstic);
            this.ewsStatus = this.ewsInfo.locationTypeCode;
            MtkLog.i(TAG, "ewsStatus== " + this.ewsStatus);
            sendIRControl(this.ewsStatus);
            setWindowPosition();
            MtkLog.d(TAG, "EWSDialog show");
            showEWSDialog();
            return;
        }
        dismiss();
    }

    public void dismiss() {
        super.dismiss();
        sendIRControl(-1);
        stopTone();
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        return KeyHandler(keyCode, event);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        boolean isHandle = true;
        MtkLog.d(TAG, "KeyHandler");
        if (keyCode != 4) {
            if (keyCode != 82) {
                switch (keyCode) {
                    case 8:
                    case 9:
                    case 10:
                        this.ewsStatus = keyCode - 8;
                        show();
                        break;
                    default:
                        switch (keyCode) {
                            case 19:
                            case 20:
                                break;
                            default:
                                isHandle = false;
                                break;
                        }
                }
            } else {
                dismiss();
                isHandle = false;
            }
        } else if (this.ewsStatus == 3) {
            MtkLog.d(TAG, "KeyHandler||waspada");
            dismiss();
        }
        if (isHandle || TurnkeyUiMainActivity.getInstance() == null) {
            return isHandle;
        }
        MtkLog.d(TAG, "TurnkeyUiMainActivity");
        return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode, event);
    }

    public void updateComponentStatus(int statusID, int value) {
    }

    private void findViews() {
        this.disasterLogoView = (ImageView) findViewById(R.id.disaster_logo);
        this.authorityView = (ImageView) findViewById(R.id.disaster_authority);
        this.statusView = (TextView) findViewById(R.id.disaster_status);
        this.areaView = (TextView) findViewById(R.id.disaster_area);
        this.bencanaView = (TextView) findViewById(R.id.ews_bencana);
        this.tanggalView = (TextView) findViewById(R.id.ews_tanggal);
        this.posisiView = (TextView) findViewById(R.id.ews_posisi);
        this.karakterView = (TextView) findViewById(R.id.ews_karakter);
        this.pesanView = (TextView) findViewById(R.id.ews_pesan);
        this.infoView = (TextView) findViewById(R.id.infomation);
        this.middleLayout = (LinearLayout) findViewById(R.id.ews_middle_lay);
    }

    private void prepareResource() {
        TypedArray typedArray = null;
        try {
            String[] logoStrings = this.context.getResources().getStringArray(R.array.ews_disaster_logo);
            typedArray = this.context.getResources().obtainTypedArray(R.array.ews_disaster_logo);
            this.disasterLogoDrawables = new Drawable[logoStrings.length];
            for (int i = 0; i < logoStrings.length; i++) {
                this.disasterLogoDrawables[i] = typedArray.getDrawable(i);
            }
            String[] logoStrings1 = this.context.getResources().getStringArray(R.array.ews_authority);
            TypedArray typedArray1 = this.context.getResources().obtainTypedArray(R.array.ews_authority);
            this.authorityDrawables = new Drawable[logoStrings1.length];
            for (int j = 0; j < logoStrings1.length; j++) {
                this.authorityDrawables[j] = typedArray1.getDrawable(j);
            }
        } catch (Exception e) {
        }
        if (typedArray != null) {
            try {
                typedArray.recycle();
            } catch (Exception e2) {
            }
        }
    }

    private String getEwsStatus(int ews) {
        switch (ews) {
            case 1:
                return "AWAS";
            case 2:
                return "SIAGA";
            case 3:
                return "WASPADA";
            default:
                return "";
        }
    }

    private void sendIRControl(int ewsStatusInt) {
        MtkLog.d(TAG, "sendIRControl||ewsStatusInt =: " + ewsStatusInt);
        switch (ewsStatusInt) {
            case 1:
                MtkTvUtil.IRRemoteControl(0);
                return;
            case 2:
                MtkTvUtil.IRRemoteControl(0);
                return;
            case 3:
                MtkTvUtil.IRRemoteControl(3);
                return;
            default:
                MtkTvUtil.IRRemoteControl(3);
                return;
        }
    }

    private void playTone() {
        MtkLog.d(TAG, "playTone");
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            MtkLog.d(TAG, "playTone||sleep exception");
        }
        AudioFocusManager.getInstance(this.mContext).muteTVAudio(4);
        try {
            AssetFileDescriptor afd = this.context.getResources().getAssets().openFd("music/IndonesiaEWS.mp3");
            if (afd != null) {
                MtkLog.d(TAG, "file exist");
                this.mPlayer.reset();
                this.mPlayer.setAudioStreamType(3);
                this.mPlayer.setDataSource(afd.getFileDescriptor());
                this.mPlayer.setLooping(true);
                this.mPlayer.prepare();
                this.mPlayer.start();
                setPlayTone(true);
                ((AudioManager) this.mContext.getApplicationContext().getSystemService("audio")).setStreamVolume(3, 40, 0);
            }
        } catch (IllegalArgumentException e2) {
            MtkLog.e(TAG, "IllegalArgumentException:" + e2.getMessage());
        } catch (IllegalStateException e3) {
            MtkLog.e(TAG, "IllegalStateException:" + e3.getMessage());
        } catch (IOException e4) {
            MtkLog.e(TAG, "IOException:" + e4.getMessage());
        } catch (Exception e5) {
            MtkLog.e(TAG, "Exception:" + e5.getMessage());
        }
    }

    private void stopTone() {
        MtkLog.d(TAG, "stopTone||ewsStatus =" + this.ewsStatus + "||volume = " + this.volume);
        AudioFocusManager.getInstance(this.mContext).unmuteTVAudio(4);
        if (this.ewsStatus == 1 || this.ewsStatus == 2) {
            ((AudioManager) this.mContext.getApplicationContext().getSystemService("audio")).setStreamVolume(3, this.volume, 0);
        }
        try {
            this.mPlayer.stop();
            this.mPlayer.reset();
            setPlayTone(false);
        } catch (Exception e) {
            MtkLog.e(TAG, "stopTone(); --> exception ");
        }
    }

    public void setWindowPosition() {
        MtkLog.d(TAG, "setWindowPosition");
        WindowManager windowManager = getWindow().getWindowManager();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (this.ewsStatus == 1) {
            lp.width = ScreenConstant.SCREEN_WIDTH;
            lp.height = ScreenConstant.SCREEN_HEIGHT;
            lp.x = 0;
            lp.y = 0;
            lp.alpha = 1.0f;
            MtkLog.d(TAG, "STATUS_AWAS");
            window.setAttributes(lp);
        } else if (this.ewsStatus == 2) {
            lp.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.9d);
            lp.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.9d);
            lp.x = 0;
            lp.y = 0;
            lp.alpha = 1.0f;
            MtkLog.d(TAG, "STATUS_SIAGA");
            window.setAttributes(lp);
        } else if (this.ewsStatus == 3) {
            lp.width = ScreenConstant.SCREEN_WIDTH;
            lp.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.45d);
            lp.y = ScreenConstant.SCREEN_HEIGHT - ((int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.45d));
            lp.x = 0;
            MtkLog.d(TAG, "STATUS_WASPADA");
            window.setAttributes(lp);
        }
    }

    public void showEWSDialog() {
        int i = this.ewsInfo.disasterCode - 1;
        int authority = this.ewsInfo.authority - 1 < 0 ? 0 : this.ewsInfo.authority - 1;
        String sLocationCode = this.ewsInfo.charLocationCode;
        String sDisasterCode = this.ewsInfo.charDisasterCode;
        String sDisasterDate = this.ewsInfo.charDisasterDate;
        String sDisasterPosi = this.ewsInfo.charDisasterPosition;
        String sDisasterChar = this.ewsInfo.charDisasterCharacterstic;
        String msgString = getMsgString(this.ewsInfo.infoMsg, this.ewsInfo.charInfoMsgNum);
        this.volume = ((AudioManager) this.mContext.getApplicationContext().getSystemService("audio")).getStreamVolume(3);
        MtkLog.d(TAG, "showEWSDialog||volume = " + this.volume);
        if (this.ewsStatus == 1 || this.ewsStatus == 2) {
            this.middleLayout.setVisibility(0);
            showDisaster();
            this.authorityView.setImageDrawable(this.authorityDrawables[authority]);
            if (1 == this.ewsStatus) {
                this.statusView.setTextColor(this.context.getResources().getColor(R.color.red_new));
            } else {
                this.statusView.setTextColor(this.context.getResources().getColor(R.color.orange));
            }
            this.statusView.setText(this.context.getResources().getString(R.string.ews_status, new Object[]{getEwsStatus(this.ewsStatus)}));
            this.areaView.setText(sLocationCode);
            this.bencanaView.setText(sDisasterCode);
            this.tanggalView.setText(sDisasterDate);
            this.posisiView.setText(sDisasterPosi);
            this.karakterView.setText(sDisasterChar);
            TextView textView = this.infoView;
            textView.setText(sDisasterCode + "," + msgString + ",Daerah Anda:Status " + getEwsStatus(this.ewsStatus));
            playTone();
        } else if (this.ewsStatus == 3) {
            if (this.isPlayTone) {
                stopTone();
            }
            this.middleLayout.setVisibility(8);
            showDisaster();
            this.authorityView.setImageDrawable(this.authorityDrawables[authority]);
            this.statusView.setTextColor(this.context.getResources().getColor(R.drawable.green));
            this.statusView.setText(this.context.getResources().getString(R.string.ews_status, new Object[]{getEwsStatus(this.ewsStatus)}));
            this.areaView.setText(sLocationCode);
            TextView textView2 = this.infoView;
            textView2.setText(sDisasterCode + "," + sDisasterDate + "," + sDisasterPosi + "," + sDisasterChar + "," + msgString + ",Daerah Anda:Status " + getEwsStatus(this.ewsStatus));
        } else {
            MtkLog.e(TAG, "EWS INFO == null or bad ");
        }
    }

    private void showDisaster() {
        if (this.ewsInfo.disasterCode < 16 && this.ewsInfo.disasterCode > 0) {
            this.disasterLogoView.setVisibility(0);
            this.disasterLogoView.setImageDrawable(this.disasterLogoDrawables[this.ewsInfo.disasterCode - 1]);
        } else if (this.ewsInfo.disasterCode == 255) {
            this.disasterLogoView.setVisibility(0);
            this.disasterLogoView.setImageResource(R.drawable.disaster_15_warning);
        } else {
            this.disasterLogoView.setVisibility(4);
        }
    }

    private String getMsgString(MtkTvEWSPABase.TmdwInfoMsg[] infoMsg, int infoMsgNum) {
        MtkLog.d(TAG, "getMsgString");
        StringBuffer sBuffer = new StringBuffer();
        for (MtkTvEWSPABase.TmdwInfoMsg tmdwInfoMsg : infoMsg) {
            sBuffer.append(tmdwInfoMsg.charInfoMsg);
        }
        MtkLog.d(TAG, "getmsgString end,msg:" + sBuffer.toString());
        return sBuffer.toString();
    }

    public boolean isPlayTone() {
        return this.isPlayTone;
    }

    public void setPlayTone(boolean isPlayTone2) {
        this.isPlayTone = isPlayTone2;
    }
}
