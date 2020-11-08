package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.manager.Util;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class PVRPlayView extends PopupWindow {
    private static final String TAG = "PVRPlayView";
    private TextView fastView;
    private ImageView fbView;
    private Activity mContext;
    private ProgressBar mProgressBar;
    private TextView multiAudio;
    private ImageView playView;
    private TextView subTitle;
    private TextView timeView;
    private TextView timeView_endtime;

    public PVRPlayView(Activity mContext2, StateBase state) {
        super(mContext2.getLayoutInflater().inflate(R.layout.pvr_playpvr_view, (ViewGroup) null), (int) (((double) Util.getTVWidth()) * 0.5d), (int) (((double) Util.getTVHeight()) * 0.2d));
        this.mContext = mContext2;
        initControlView();
    }

    public void initControlView() {
        this.mProgressBar = (ProgressBar) getContentView().findViewById(R.id.pvr_play_progress);
        this.fastView = (TextView) getContentView().findViewById(R.id.pvr_playfast_view);
        this.timeView = (TextView) getContentView().findViewById(R.id.pvr_playinfo);
        this.timeView_endtime = (TextView) getContentView().findViewById(R.id.pvr_playinfo_endtime);
        this.playView = (ImageView) getContentView().findViewById(R.id.pvr_play_btn);
        this.fbView = (ImageView) getContentView().findViewById(R.id.pvr_play_fb_btn);
        this.fbView.setVisibility(4);
        this.multiAudio = (TextView) getContentView().findViewById(R.id.pvr_play_multi_audio);
        this.subTitle = (TextView) getContentView().findViewById(R.id.pvr_play_subtitle);
    }

    public void show() {
    }

    public void setPlay() {
        this.fbView.setVisibility(4);
        this.playView.setImageResource(R.drawable.timeshift_play);
    }

    public void setFBPlay() {
        this.fbView.setVisibility(4);
        this.playView.setImageResource(R.drawable.timeshift_fb);
    }

    public void setPause() {
        this.fbView.setVisibility(4);
        this.playView.setImageResource(R.drawable.timshift_pasuse);
    }

    public void setFF() {
        this.fbView.setVisibility(0);
        this.playView.setImageResource(R.drawable.timeshift_ff);
    }

    public void setFB() {
        this.fbView.setVisibility(0);
        this.playView.setImageResource(R.drawable.timeshift_fb);
    }

    public void setFFNumView(int value) {
        if (value == 8) {
            value = 4;
        }
        this.fbView.setVisibility(value);
    }

    public void setFFNum(int value) {
        if (value == 8) {
            value = 4;
        }
        this.fbView.setImageResource(value);
    }

    public void dissmiss() {
        super.dismiss();
    }

    public void setProgressMax(int max) {
        this.mProgressBar.setMax(max);
    }

    public TextView getFastView() {
        return this.fastView;
    }

    public int getProgressMax() {
        return this.mProgressBar.getMax();
    }

    public void setProgress(int progress) {
        this.mProgressBar.setProgress(progress);
    }

    public void setCurrentTime(long mills) {
        long mills2 = mills + 1;
        long minute = mills2 / 60;
        long hour = minute / 60;
        long second = mills2 % 60;
        long minute2 = minute % 60;
        MtkLog.e("setCurrentTime", "setCurrentTime--:" + String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(hour), Long.valueOf(minute2), Long.valueOf(second)}));
        this.timeView.setText(String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(hour), Long.valueOf(minute2), Long.valueOf(second)}));
        if (mills2 >= ((long) getProgressMax())) {
            setProgress(getProgressMax());
        }
    }

    public void setCurrentTimeZero() {
        this.timeView.setText(String.format("%02d:%02d:%02d", new Object[]{0, 0, 0}));
    }

    public void setEndtime(int mills) {
        int minute = mills / 60;
        int hour = minute / 60;
        int second = mills % 60;
        int minute2 = minute % 60;
        try {
            this.timeView_endtime.setText(String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hour), Integer.valueOf(minute2), Integer.valueOf(second)}));
        } catch (Exception e) {
            this.timeView_endtime.setText("");
        }
    }

    public void setVisibility(boolean flag) {
        if (flag) {
            this.timeView.setVisibility(0);
        } else {
            this.timeView.setVisibility(4);
        }
    }

    public void updateMultiAudioStr(String str) {
        this.multiAudio.setText(str);
    }

    public void updateSubtitleStr(String str) {
        this.subTitle.setText(str);
    }
}
