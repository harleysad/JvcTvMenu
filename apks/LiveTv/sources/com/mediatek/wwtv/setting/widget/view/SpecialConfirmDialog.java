package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import java.util.Date;
import java.util.List;

public class SpecialConfirmDialog extends CommonDialog {
    private final float hScale = 0.35f;
    private List<MtkTvBookingBase> mItems;
    private TextView mTV1;
    private TextView mTV2;
    private Button negativeBtn;
    private Button positiveBtn;
    private final float wScale = 0.55f;

    public SpecialConfirmDialog(Context context) {
        super(context, R.layout.pvr_tshift_special_confirmdialog);
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        getWindow().setLayout((int) (((float) outSize.x) * 0.55f), (int) (((float) outSize.y) * 0.35f));
        setCancelable(true);
        initView2();
    }

    public SpecialConfirmDialog(Context context, List<MtkTvBookingBase> item) {
        super(context, R.layout.pvr_tshift_special_confirmdialog);
        context.getSystemService("window");
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        getWindow().setLayout((int) (((float) outSize.x) * 0.55f), (int) (((float) outSize.y) * 0.35f));
        this.mItems = item;
        setCancelable(true);
        initView2();
    }

    private void initView2() {
        this.mTV1 = (TextView) findViewById(R.id.diskop_title_line1);
        this.mTV1.setText("");
        this.mTV2 = (TextView) findViewById(R.id.diskop_title_line2);
        this.mTV2.setText("");
        this.positiveBtn = (Button) findViewById(R.id.confirm_btn_yes);
        this.negativeBtn = (Button) findViewById(R.id.confirm_btn_no);
        setItemValue(this.mItems);
    }

    private void setItemValue(List<MtkTvBookingBase> items) {
        TableLayout rootView = (TableLayout) findViewById(R.id.device_info);
        if (items == null) {
            Util.showDLog("specialConifrmDilog.setItemValue():MtkTvBookingBase==null");
            rootView.setVisibility(4);
            return;
        }
        for (MtkTvBookingBase item : items) {
            TableRow row = new TableRow(this.mContext);
            TextView label = new TextView(this.mContext);
            label.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
            label.setText("CH" + item.getEventTitle());
            row.addView(label);
            TextView size = new TextView(this.mContext);
            size.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
            size.setText(Util.dateToStringYMD3(new Date(item.getRecordStartTime() * 1000)));
            row.addView(size);
            TextView duration = new TextView(this.mContext);
            duration.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
            duration.setText(Util.longToHrMinN(Long.valueOf(item.getRecordDuration())));
            row.addView(duration);
            TextView repeatType = new TextView(this.mContext);
            repeatType.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
            String[] repeat = this.mContext.getResources().getStringArray(R.array.pvr_tshift_repeat_type);
            int i = 0;
            if (item.getRepeatMode() == 128) {
                repeatType.setText(repeat[0]);
            } else if (item.getRepeatMode() == 0) {
                repeatType.setText(repeat[2]);
            } else {
                repeatType.setText(repeat[1]);
            }
            row.addView(repeatType);
            TextView scheduleType = new TextView(this.mContext);
            scheduleType.setTextColor(this.mContext.getResources().getColor(R.color.yellow));
            String[] schedule = this.mContext.getResources().getStringArray(R.array.pvr_tshift_schedule_type);
            if (item.getRecordMode() != 2) {
                i = item.getRecordMode();
            }
            scheduleType.setText(schedule[i]);
            row.addView(scheduleType);
            rootView.addView(row);
        }
    }

    public void setPositiveButton(View.OnClickListener listener) {
        this.positiveBtn.setOnClickListener(listener);
    }

    public void setNegativeButton(View.OnClickListener listener) {
        this.negativeBtn.setOnClickListener(listener);
    }

    public void setTitle(String line1, String line2) {
        this.mTV1.setText(line1);
        this.mTV2.setText(line2);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Util.showDLog("CommonConfirmDialog.onKeyDown()" + keyCode);
        if (keyCode == 23) {
            View view = getCurrentFocus();
            switch (view.getId()) {
                case R.id.confirm_btn_no /*2131362122*/:
                case R.id.confirm_btn_yes /*2131362123*/:
                    onClick(view);
                    break;
                default:
                    Util.showDLog("Current Focus !=Confirm Button.");
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void show() {
        getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
        super.show();
    }
}
