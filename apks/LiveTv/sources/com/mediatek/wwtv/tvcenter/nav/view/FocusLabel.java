package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.input.ISource;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class FocusLabel extends LinearLayout {
    private static final int DEFAULT_HEIGHT = 540;
    private static final int DEFAULT_WIDTH = 960;
    static final String TAG = "FocusLabel";
    private int defaultHeight = 60;
    private int defaultOffsetX = 0;
    private int defaultOffsetY = 0;
    private int defaultWidth = 60;
    private int delayTime = ISource.TYPE_DTV;
    private ImageView focusImage;
    private Context mContext;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };
    private ViewGroup.LayoutParams mLayoutParams;
    private Runnable timerTask = new Runnable() {
        public void run() {
            FocusLabel.this.setVisibility(4);
        }
    };

    public FocusLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public FocusLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FocusLabel(Context context) {
        super(context);
        initView(context);
    }

    public void setSize(int width, int height) {
        this.mLayoutParams.width = width;
        this.mLayoutParams.height = height;
        this.focusImage.setLayoutParams(this.mLayoutParams);
    }

    public void setPadding(float x, float y) {
        setTranslationX((float) ((int) ((((float) ScreenConstant.SCREEN_WIDTH) * x) - ((float) (this.defaultWidth / 2)))));
        setTranslationY((float) ((int) (((float) ScreenConstant.SCREEN_HEIGHT) * y)));
    }

    public void setPaddingWithCallBack(int x, int y) {
        setTranslationX((float) (((ScreenConstant.SCREEN_WIDTH / DEFAULT_WIDTH) * x) - (this.defaultWidth / 2)));
        setTranslationY((float) ((ScreenConstant.SCREEN_HEIGHT / DEFAULT_HEIGHT) * y));
    }

    public void setDefaultLocation() {
        setTranslationX((float) this.defaultOffsetX);
        setTranslationY((float) this.defaultOffsetY);
        this.mLayoutParams.width = this.defaultWidth;
        this.mLayoutParams.height = this.defaultHeight;
        this.focusImage.setLayoutParams(this.mLayoutParams);
    }

    public void show() {
        if (getTranslationX() < 10.0f) {
            MtkLog.d(TAG, "focus label x position < 10");
            return;
        }
        setDefaultPic();
        setVisibility(0);
        startTimerTask();
    }

    public void release() {
        setVisibility(8);
        this.mHandler.removeCallbacks(this.timerTask);
    }

    public void setDefaultPic() {
        this.focusImage.setBackgroundResource(R.drawable.nav_pip_focus_icon);
    }

    private void startTimerTask() {
        this.mHandler.removeCallbacks(this.timerTask);
        this.mHandler.postDelayed(this.timerTask, (long) this.delayTime);
    }

    public void hiddenFocus(boolean hidden) {
        if (hidden) {
            this.mHandler.removeCallbacks(this.timerTask);
        } else {
            startTimerTask();
        }
    }

    private void initView(Context context) {
        MtkLog.d(TAG, "come in focuslabel initView");
        this.mContext = context;
        ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.nav_pippop_focuslabel, this);
        this.focusImage = (ImageView) findViewById(R.id.nav_focus_label_image);
        this.focusImage.setBackgroundResource(R.drawable.nav_pip_focus_icon);
        this.mLayoutParams = this.focusImage.getLayoutParams();
        setDefaultLocation();
    }
}
