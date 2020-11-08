package com.mediatek.wwtv.setting.fragments;

import android.app.AlarmManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class USTimeZoneFragment extends Fragment implements View.OnKeyListener {
    private static final String TAG = "USTimeZoneFragment";
    int defIndex;
    private Action mAction;
    private MenuConfigManager mConfigManager;
    private Context mContext;
    private ViewGroup mRootView;
    private Integer[] mTimeZoneImg = {Integer.valueOf(R.drawable.time_zone_map_0), Integer.valueOf(R.drawable.time_zone_map_7), Integer.valueOf(R.drawable.time_zone_map_6), Integer.valueOf(R.drawable.time_zone_map_5), Integer.valueOf(R.drawable.time_zone_map_4), Integer.valueOf(R.drawable.time_zone_map_3), Integer.valueOf(R.drawable.time_zone_map_2), Integer.valueOf(R.drawable.time_zone_map_1)};
    private ImageView mZoneImg;
    private String[] zoneArray = null;

    public void setAction(Action action) {
        this.mAction = action;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.ustimezone_frag, (ViewGroup) null);
        this.mZoneImg = (ImageView) this.mRootView.findViewById(R.id.timezone_img);
        bindData();
        MtkLog.d(TAG, "onCreateView~");
        return this.mRootView;
    }

    private void bindData() {
        this.mConfigManager = MenuConfigManager.getInstance(this.mContext);
        this.zoneArray = this.mContext.getResources().getStringArray(R.array.menu_setup_us_timezone_array);
        this.defIndex = this.mConfigManager.getDefault("g_time__time_zone");
        this.mZoneImg.setImageResource(this.mTimeZoneImg[this.defIndex].intValue());
        this.mZoneImg.setFocusable(true);
        this.mZoneImg.requestFocus();
        this.mZoneImg.setOnKeyListener(this);
    }

    public void onKeyLeft() {
        switchValuePrevious();
        this.mZoneImg.setImageResource(this.mTimeZoneImg[this.defIndex].intValue());
    }

    public void onKeyRight() {
        switchValueNext();
        this.mZoneImg.setImageResource(this.mTimeZoneImg[this.defIndex].intValue());
    }

    private void switchValuePrevious() {
        if (this.defIndex == 0) {
            this.defIndex = this.zoneArray.length - 1;
        } else {
            this.defIndex--;
        }
        setAndroidTimeZone(this.defIndex);
        this.mConfigManager.setValue("g_time__time_zone", this.defIndex, this.mAction);
    }

    private void setAndroidTimeZone(int defIndex2) {
        AlarmManager mAlarmManager = (AlarmManager) getActivity().getSystemService(NotificationCompat.CATEGORY_ALARM);
        switch (defIndex2) {
            case 0:
                mAlarmManager.setTimeZone("US/Eastern");
                return;
            case 1:
                mAlarmManager.setTimeZone("US/Hawaii");
                return;
            case 2:
                mAlarmManager.setTimeZone("US/Alaska");
                return;
            case 3:
                mAlarmManager.setTimeZone("US/Pacific");
                return;
            case 4:
                mAlarmManager.setTimeZone("US/Arizona");
                return;
            case 5:
                mAlarmManager.setTimeZone("US/Mountain");
                return;
            case 6:
                mAlarmManager.setTimeZone("US/Central");
                return;
            case 7:
                mAlarmManager.setTimeZone("US/East-Indiana");
                return;
            default:
                return;
        }
    }

    private void switchValueNext() {
        if (this.defIndex == this.zoneArray.length - 1) {
            this.defIndex = 0;
        } else {
            this.defIndex++;
        }
        setAndroidTimeZone(this.defIndex);
        this.mConfigManager.setValue("g_time__time_zone", this.defIndex, this.mAction);
    }

    public void onDestroyView() {
        super.onDestroyView();
        MtkLog.d(TAG, "onDestroyView~");
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "event.getAction(): " + event.getAction() + ",keyCode: " + keyCode);
        if (event.getAction() != 0) {
            return false;
        }
        if (keyCode == 4) {
            MtkLog.d(TAG, "back key down.");
            return true;
        } else if (keyCode == 183) {
            MtkLog.d(TAG, "left key down.");
            onKeyLeft();
            return true;
        } else if (keyCode != 186) {
            switch (keyCode) {
                case 21:
                    MtkLog.d(TAG, "left key down.");
                    onKeyLeft();
                    return true;
                case 22:
                    MtkLog.d(TAG, "right key down.");
                    onKeyRight();
                    return true;
                default:
                    return false;
            }
        } else {
            MtkLog.d(TAG, "right key down.");
            onKeyRight();
            return true;
        }
    }
}
