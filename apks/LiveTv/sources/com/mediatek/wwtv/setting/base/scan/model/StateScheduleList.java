package com.mediatek.wwtv.setting.base.scan.model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.widget.view.ScheduleListDialog;
import com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StateScheduleList {
    public static final int Auto_Dismiss_Info_Dialog_Timer = 3;
    public static final int Auto_Dismiss_List_Dialog_Timer = 4;
    public static final int HIDDEN_CO_EXIST_COMP = 5;
    private static final int SHOW_LIST = 1;
    private static final int SHOW_LIST_ITEM = 2;
    private static final String TAG = "StateScheduleList";
    /* access modifiers changed from: private */
    public static StateScheduleList mStateSelf;
    private Context mContext;
    public MyHandler mHandler;
    /* access modifiers changed from: private */
    public MtkTvBookingBase mItem;
    private ScheduleListItemInfoDialog mItemDialogWindow;
    private ScheduleListDialog mScheduleListWindow;
    private int temp_count = 1;

    public enum StatusType {
        UNKNOWN,
        TIMESHIFT,
        PVR,
        NORMAL,
        DISKSETTING,
        INITDISK,
        FILELIST,
        SCHEDULELIST
    }

    public static class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            if (StateScheduleList.mStateSelf != null) {
                switch (msg.what) {
                    case 1:
                        StateScheduleList.mStateSelf.showListDialog();
                        break;
                    case 2:
                        StateScheduleList.mStateSelf.showItemInfoDialog(StateScheduleList.mStateSelf.mItem);
                        break;
                    case 3:
                        StateScheduleList.mStateSelf.dimissItemInfoDialog();
                        break;
                    case 4:
                        StateScheduleList.mStateSelf.dimissListDialog();
                        break;
                }
                super.handleMessage(msg);
            }
        }
    }

    public static StateScheduleList getInstance(Context mContext2) {
        if (mStateSelf == null) {
            mStateSelf = new StateScheduleList(mContext2);
        }
        return mStateSelf;
    }

    public static StateScheduleList getInstance() {
        return mStateSelf;
    }

    public StateScheduleList(Context mContext2) {
        this.mContext = mContext2;
        mStateSelf = this;
    }

    private void hiddenCoExistViews() {
    }

    public void initViews2() {
        MtkLog.e("schedulelist", "initViews2:item:" + this.mItem);
        if (this.mItem == null) {
            showListDialog();
        } else {
            showItemInfoDialog(this.mItem);
        }
        hiddenCoExistViews();
        this.mHandler.sendEmptyMessageDelayed(5, MessageType.delayMillis5);
    }

    /* access modifiers changed from: private */
    public void showListDialog() {
    }

    public List<MtkTvBookingBase> queryItem() {
        List<MtkTvBookingBase> books = MtkTvRecord.getInstance().getBookingList();
        MtkLog.d(TAG, "books==" + books);
        return books;
    }

    public static void deleteAllItems(Context context) {
        List<MtkTvBookingBase> itemLists = queryItemList();
        if (itemLists != null && itemLists.size() > 0) {
            for (MtkTvBookingBase item : itemLists) {
                MtkTvRecord.getInstance().deleteBooking(item.getBookingId());
            }
        }
    }

    public static List<MtkTvBookingBase> queryItem(Context context, int taskid) {
        return queryItemList();
    }

    public MtkTvBookingBase getMtkTvBookingBase() {
        new ArrayList();
        return queryItem().get(0);
    }

    public int getChannelToStart() {
        return 0;
    }

    private static List<MtkTvBookingBase> queryItemList() {
        new ArrayList();
        return MtkTvRecord.getInstance().getBookingList();
    }

    public void tryToRelease() {
        boolean listWindow = false;
        boolean infoWindow = false;
        if (this.mItemDialogWindow == null || !this.mItemDialogWindow.isShowing()) {
            infoWindow = true;
        }
        if (this.mScheduleListWindow == null || !this.mScheduleListWindow.isShowing()) {
            listWindow = true;
        }
        if (listWindow && infoWindow) {
            if (this.mItemDialogWindow != null) {
                this.mItemDialogWindow.setOnDismissListener((DialogInterface.OnDismissListener) null);
            }
            if (this.mScheduleListWindow != null) {
                this.mScheduleListWindow.setOnDismissListener((DialogInterface.OnDismissListener) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showItemInfoDialog(MtkTvBookingBase t) {
    }

    /* access modifiers changed from: private */
    public void dimissListDialog() {
        if (this.mScheduleListWindow != null && this.mScheduleListWindow.isShowing()) {
            this.mScheduleListWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void dimissItemInfoDialog() {
        if (this.mItemDialogWindow != null && this.mItemDialogWindow.isShowing()) {
            this.mItemDialogWindow.dismiss();
        }
    }

    public boolean insertItem(MtkTvBookingBase item) {
        MtkLog.d(TAG, "insertItem + item = " + item.toString());
        int addBooking = MtkTvRecord.getInstance().addBooking(item);
        return true;
    }

    public void deleteItem(MtkTvBookingBase item) {
        MtkTvRecord.getInstance().deleteBooking(item.getBookingId());
    }

    public void replaceItem(MtkTvBookingBase item) {
        MtkTvRecord.getInstance().replaceBooking(item.getBookingId(), item);
    }
}
