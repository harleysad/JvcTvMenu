package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class TvView extends ImageView {
    private static final int CHANGE_TV_VIEW_POSITION = 11;
    private static final String PhoneBarChange = "com.mediatek.phonestatus.bar.change";
    static final String TAG = "TvView";
    /* access modifiers changed from: private */
    public static float viewHeight;
    /* access modifiers changed from: private */
    public static float viewWidth;
    /* access modifiers changed from: private */
    public static float viewX;
    /* access modifiers changed from: private */
    public static float viewY;
    /* access modifiers changed from: private */
    public int lastScreenTypeValue = 0;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 11) {
                TvView.this.setTvViewPosition(TvView.viewX, TvView.viewY, TvView.viewWidth, TvView.viewHeight);
            }
        }
    };
    private Runnable mRunnable = new Runnable() {
        public void run() {
            int messageFullScreenValue = 0;
            try {
                messageFullScreenValue = Settings.System.getInt(TvView.this.mContext.getContentResolver(), "fullScreenOrNot");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (messageFullScreenValue == 0 && TvView.this.lastScreenTypeValue != 0) {
                boolean unused = TvView.this.statusBarVisible = true;
                TvView.this.mHandler.removeMessages(11);
                TvView.this.mHandler.sendEmptyMessage(11);
                int unused2 = TvView.this.lastScreenTypeValue = messageFullScreenValue;
            } else if (messageFullScreenValue == 1 && TvView.this.lastScreenTypeValue != 1) {
                boolean unused3 = TvView.this.statusBarVisible = false;
                TvView.this.mHandler.removeMessages(11);
                TvView.this.mHandler.sendEmptyMessage(11);
                int unused4 = TvView.this.lastScreenTypeValue = messageFullScreenValue;
            }
            TvView.this.mHandler.postDelayed(this, 200);
        }
    };
    private int mScreenHeight;
    private int mScreenWidth;
    private int messagelastScreenValue = 0;
    /* access modifiers changed from: private */
    public boolean statusBarVisible = true;
    private WindowManager windowManager;
    private WindowManager.LayoutParams wmParams;

    public TvView(Context context) {
        super(context);
        this.mContext = context;
        this.windowManager = (WindowManager) context.getApplicationContext().getSystemService("window");
        this.mScreenWidth = ScreenConstant.SCREEN_WIDTH;
        this.mScreenHeight = ScreenConstant.SCREEN_HEIGHT;
        this.wmParams = new WindowManager.LayoutParams();
        this.wmParams.type = DvrManager.CHANGE_CHANNEL;
        this.wmParams.flags |= 8;
        this.wmParams.gravity = 51;
        this.windowManager.addView(this, this.wmParams);
        setBackgroundResource(R.drawable.translucent_background);
    }

    public void setTvViewPosition(float x, float y, float width, float height) {
        viewX = x;
        viewY = y;
        viewWidth = width;
        viewHeight = height;
        this.wmParams.width = (int) (((float) this.mScreenWidth) * width);
        this.wmParams.height = (int) (((float) this.mScreenHeight) * height);
        this.wmParams.x = (int) (((float) this.mScreenWidth) * x);
        if (this.statusBarVisible) {
            this.wmParams.y = ((int) (((float) this.mScreenHeight) * y)) - getSystemBarheight();
        } else {
            this.wmParams.y = (int) (((float) this.mScreenHeight) * y);
        }
        MtkLog.i("OSD", "~~~~~~~~mScrrenWidth:" + this.mScreenWidth + "~~mScreenHeight:" + this.mScreenHeight);
        MtkLog.i("OSD", "~~wmParams.width: " + this.wmParams.width + "~~wmParams.height: " + this.wmParams.height + "~~wmParams.x: " + this.wmParams.x + "~~wmParams.y:" + this.wmParams.y + "~~");
        this.windowManager.updateViewLayout(this, this.wmParams);
    }

    public void show(Context context) {
        MtkLog.d(TAG, "come in TvView to show tvview");
        setVisibility(0);
    }

    public void startCheckPosition() {
        try {
            this.lastScreenTypeValue = Settings.System.getInt(this.mContext.getContentResolver(), "fullScreenOrNot");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        this.mHandler.post(this.mRunnable);
    }

    public void hide() {
        setVisibility(4);
        this.mHandler.removeCallbacks(this.mRunnable);
    }

    public int getSystemBarheight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            return getResources().getDimensionPixelSize(Integer.parseInt(c.getField("status_bar_height").get(c.newInstance()).toString()));
        } catch (Exception e1) {
            MtkLog.e("tag", "get status bar height fail");
            e1.printStackTrace();
            return 0;
        }
    }
}
