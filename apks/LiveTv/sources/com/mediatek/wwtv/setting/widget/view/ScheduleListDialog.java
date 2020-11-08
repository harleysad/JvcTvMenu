package com.mediatek.wwtv.setting.widget.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.base.scan.adapter.StateScheduleListAdapter;
import com.mediatek.wwtv.setting.base.scan.model.StateScheduleList;
import com.mediatek.wwtv.setting.base.scan.model.StateScheduleListCallback;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevListener;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevManager;
import com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity;
import com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.List;

public class ScheduleListDialog extends CommonDialog implements AdapterView.OnItemClickListener, DevListener {
    public static final int Auto_Dismiss_List_Dialog_Timer = 4;
    private static final int MSG_DELAY_TIME = 1000;
    private static final int MSG_NOTIFY_DEVICE_MOUNT = 0;
    private static ScheduleListDialog scheduleListDialog;
    private final String TAG = "ScheduleListDialog";
    public boolean epgFlag = false;
    private final float hScale = 0.8f;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                ScheduleListDialog.this.setDiskInfoTitle();
            } else if (i == 4 && ScheduleListDialog.this != null && ScheduleListDialog.this.isShowing()) {
                ScheduleListDialog.this.dismiss();
            }
        }
    };
    private List<MtkTvBookingBase> itemList = new ArrayList();
    private StateScheduleListCallback<MtkTvBookingBase> mCallback;
    private final Context mContext;
    private TextView mDiskInfoTitle;
    private TextView mNoRecord;
    private ListView mScheduleList;
    private StateScheduleList mState;
    private int position = 0;
    private TextView selectText;
    private ImageView selectView;
    private StateScheduleListAdapter stateScheduleListAdapter;
    private final float wScale = 0.7f;

    public ScheduleListDialog(Context context) {
        super(context, R.layout.pvr_tshfit_schudulelist);
        DevManager.getInstance().addDevListener(this);
        this.mContext = context;
        scheduleListDialog = this;
        getWindow().setLayout((int) (((float) ScreenConstant.SCREEN_WIDTH) * 0.7f), (int) (((float) ScreenConstant.SCREEN_HEIGHT) * 0.8f));
    }

    public ScheduleListDialog(Context context, int position2) {
        super(context, R.layout.pvr_tshfit_schudulelist);
        DevManager.getInstance().addDevListener(this);
        this.mContext = context;
        this.position = position2;
        initData();
    }

    private void initData() {
        scheduleListDialog = this;
        getWindow().setLayout((int) (((float) ScreenConstant.SCREEN_WIDTH) * 0.7f), (int) (((float) ScreenConstant.SCREEN_HEIGHT) * 0.8f));
        try {
            List<MtkTvBookingBase> books = StateScheduleList.getInstance(this.mContext).queryItem();
            if (books != null) {
                setItemList(books);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ScheduleListDialog getDialog() {
        return scheduleListDialog;
    }

    public void initView() {
        super.initView();
        this.mScheduleList = (ListView) findViewById(R.id.schedulelist_list);
        this.mScheduleList.setDivider((Drawable) null);
        this.mNoRecord = (TextView) findViewById(R.id.schedulelist_nofiles);
        this.mDiskInfoTitle = (TextView) findViewById(R.id.schedulelist_title_diskinfo);
        this.selectView = (ImageView) findViewById(R.id.schedulelist_icon_select);
        this.selectText = (TextView) findViewById(R.id.schedulelist_icon_selecttext);
        ((TextView) findViewById(R.id.schedulelist_title_txt)).setImportantForAccessibility(2);
        initListener();
    }

    private void initView2() {
        MtkLog.d("ScheduleListDialog", "is true" + modifyUIWhenNoChannels());
        if (getItemList().size() > 0) {
            this.stateScheduleListAdapter = new StateScheduleListAdapter(this.mContext, getItemList());
            this.mScheduleList.setAdapter(this.stateScheduleListAdapter);
            this.mScheduleList.setSelection(this.position);
            this.mScheduleList.setVisibility(0);
            this.mNoRecord.setVisibility(4);
            this.selectView.setVisibility(0);
            this.selectText.setVisibility(0);
        } else {
            this.mNoRecord.setVisibility(0);
            this.mScheduleList.setVisibility(4);
            this.selectView.setVisibility(8);
            this.selectText.setVisibility(8);
        }
        setDiskInfoTitle();
        updateDissmissTimer();
    }

    private boolean modifyUIWhenNoChannels() {
        if (TIFChannelManager.getInstance(this.mContext).getAllDTVTIFChannels().size() > 0) {
            return false;
        }
        ((ImageView) findViewById(R.id.schedule_add_item_icon)).setVisibility(4);
        ((TextView) findViewById(R.id.schedule_add_item_str)).setVisibility(4);
        this.mNoRecord.setText(this.mContext.getString(R.string.schedulelist_nofiles_disable_add));
        return true;
    }

    public void setDiskInfoTitle() {
        MountPoint mp = null;
        ArrayList<MountPoint> list = DevManager.getInstance().getMountList();
        if (list != null && list.size() > 0) {
            mp = list.get(0);
        }
        this.mDiskInfoTitle.setText(Util.getGBSizeOfDisk(mp));
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        MtkLog.d("ScheduleListDialog", "dispatchkeycode=" + event);
        if (event.getKeyCode() != 93 && event.getKeyCode() != 130) {
            return super.dispatchKeyEvent(event);
        }
        onKeyDown(event.getKeyCode(), event);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        updateDissmissTimer();
        MtkLog.d("ScheduleListDialog", "keycode=" + keyCode);
        if (keyCode == 4) {
            this.handler.removeMessages(4);
            this.handler.sendEmptyMessageDelayed(4, 500);
            MtkLog.e("KEYCODE_BACK", "KEYCODE_BACK");
            boolean z = this.epgFlag;
        } else if (keyCode == 93 || keyCode == 130) {
            if (TIFChannelManager.getInstance(this.mContext).getAllDTVTIFChannels().size() <= 0) {
                return true;
            }
            if (getItemList() == null || getItemList().size() >= 5) {
                Toast.makeText(this.mContext, "Requested add of schedule exceeds maximum allowed!", 0).show();
            } else {
                MtkLog.d("ScheduleListDialog", "add item");
                if (isEpgFlag()) {
                    showEpgitemlist();
                    dismiss();
                } else {
                    MtkTvBookingBase item = new MtkTvBookingBase();
                    item.setEventTitle("1");
                    item.setTunerType(CommonIntegration.getInstance().getTunerMode());
                    long mStartTime = getBroadcastLocalTime();
                    if (mStartTime != -1) {
                        item.setRecordStartTime(mStartTime);
                    }
                    item.setRecordDuration(120);
                    item.setRepeatMode(128);
                    showItemInfoDialog(item);
                }
            }
            return true;
        } else if (keyCode != 229) {
            switch (keyCode) {
                case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                    if (!DestroyApp.isCurEPGActivity()) {
                        CommonIntegration.getInstance().channelUp();
                        break;
                    }
                    break;
                case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                    if (!DestroyApp.isCurEPGActivity()) {
                        CommonIntegration.getInstance().channelDown();
                        break;
                    }
                    break;
            }
        } else if (!DestroyApp.isCurEPGActivity()) {
            CommonIntegration.getInstance().channelPre();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showEpgitemlist() {
        Activity act = DestroyApp.getTopActivity();
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
                ((EPGCnActivity) act).calledByScheduleList();
                return;
            case 1:
                ((EPGUsActivity) act).calledByScheduleList();
                return;
            case 2:
                ((EPGSaActivity) act).calledByScheduleList();
                return;
            case 3:
                ((EPGEuActivity) act).calledByScheduleList();
                return;
            default:
                return;
        }
    }

    private long getBroadcastLocalTime() {
        MtkTvTimeFormatBase mTime = MtkTvTime.getInstance().getBroadcastLocalTime();
        MtkLog.d("ScheduleListDialog", "getBroadcastLocalTime == " + mTime.toSeconds());
        return mTime.toSeconds();
    }

    private void initListener() {
        this.mScheduleList.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position2, long id) {
        this.handler.removeMessages(4);
        MtkTvBookingBase mItem = getItemList().get(position2);
        long rec = mItem.getRecordStartTime();
        MtkTvTimeFormatBase timeBaseFrom = new MtkTvTimeFormatBase();
        timeBaseFrom.setByUtc(rec);
        MtkLog.d("Start time Log", "mItem.getRecordStartTime() = " + rec);
        MtkTvTimeFormatBase timeBaseTo = new MtkTvTimeFormatBase();
        timeBaseFrom.print("Jiayang.li show sys_utc----");
        MtkTvTime.getInstance().convertTime(4, timeBaseFrom, timeBaseTo);
        timeBaseTo.print("Jiayang.li show brcst_utc--");
        mItem.setRecordStartTime(timeBaseTo.toSeconds());
        MtkLog.d("Start time Log", "timeBaseTo.toSeconds() = " + timeBaseTo.toSeconds());
        ScheduleListItemInfoDialog mItemDialogWindow = new ScheduleListItemInfoDialog(this.mContext, mItem, position2);
        MtkLog.d("ScheduleListDialog", "onItemClick + item info = " + mItem.toString());
        this.position = position2;
        mItemDialogWindow.show();
        dismiss();
    }

    public List<MtkTvBookingBase> getItemList() {
        return this.itemList;
    }

    public void setItemList(List<MtkTvBookingBase> itemList2) {
        this.itemList = itemList2;
        initView2();
    }

    public void updateDissmissTimer() {
        this.handler.removeMessages(4);
        this.handler.sendEmptyMessageDelayed(4, 15000);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dismiss() {
        /*
            r1 = this;
            com.mediatek.wwtv.tvcenter.dvr.manager.DevManager r0 = com.mediatek.wwtv.tvcenter.dvr.manager.DevManager.getInstance()
            r0.removeDevListener(r1)
            boolean r0 = r1.isShowing()     // Catch:{ Exception -> 0x0013, all -> 0x0011 }
            if (r0 == 0) goto L_0x0014
            super.dismiss()     // Catch:{ Exception -> 0x0013, all -> 0x0011 }
            goto L_0x0014
        L_0x0011:
            r0 = move-exception
            throw r0
        L_0x0013:
            r0 = move-exception
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.ScheduleListDialog.dismiss():void");
    }

    public void onEvent(DeviceManagerEvent event) {
        switch (event.getType()) {
            case 601:
            case 602:
                this.handler.sendEmptyMessageDelayed(0, 1000);
                return;
            default:
                return;
        }
    }

    private void showItemInfoDialog(MtkTvBookingBase t) {
        if (ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext) == null) {
            new ScheduleListItemInfoDialog(this.mContext, t).show();
        } else if (!ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext).isShowing()) {
            ScheduleListItemInfoDialog.getscheduleListItemInfoDialog(this.mContext).show();
        }
        dismiss();
    }

    public boolean isEpgFlag() {
        return this.epgFlag;
    }

    public void setEpgFlag(boolean epgFlag2) {
        this.epgFlag = epgFlag2;
    }

    public void show() {
        initData();
        super.show();
    }
}
