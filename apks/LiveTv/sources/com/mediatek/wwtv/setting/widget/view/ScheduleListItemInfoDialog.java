package com.mediatek.wwtv.setting.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.dm.MountPoint;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter;
import com.mediatek.wwtv.setting.base.scan.model.StateScheduleList;
import com.mediatek.wwtv.setting.base.scan.model.StateScheduleListCallback;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.setting.view.DateTimeInputView;
import com.mediatek.wwtv.setting.view.OptionView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DevManager;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ScheduleListItemInfoDialog extends CommonDialog implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, SetConfigListViewAdapter.SrctypeChangeListener {
    private static final int ADD_SUCCESS = 100;
    public static final int INVALID_VALUE = 10004;
    public static final String SCHEDULE_PVR_CHANNELLIST = "SCHEDULE_PVR_CHANNELLIST";
    public static final String SCHEDULE_PVR_REMINDER_TYPE = "SCHEDULE_PVR_REMINDER_TYPE";
    public static final String SCHEDULE_PVR_REPEAT_TYPE = "SCHEDULE_PVR_REPEAT_TYPE";
    public static final String SCHEDULE_PVR_SRCTYPE = "SCHEDULE_PVR_SRCTYPE";
    public static final int STEP_VALUE = 1;
    public static final String TIME_DATE = "SETUP_date";
    public static final String TIME_TIME = "SETUP_time";
    private static ScheduleListItemInfoDialog scheduleListItemInfoDialog = null;
    private final String TAG;
    private final int UI_Index_ChannelNum;
    private final int UI_Index_RepeatType;
    private final int UI_Index_ScheduleType;
    private final int UI_Index_SrcType;
    private final int UI_Index_StartDate;
    private final int UI_Index_StartTime;
    private final int UI_Index_StopTime;
    /* access modifiers changed from: private */
    public final String[] WEEK_ARRAY;
    private View.OnClickListener addListener;
    private BtnTypeEnum btnListType;
    private View.OnClickListener cancelListener;
    private SpecialConfirmDialog confirm;
    private View.OnClickListener deleteListener;
    private View.OnClickListener editListener;
    public boolean epgFlag;
    private final float hScale;
    Handler handler;
    /* access modifiers changed from: private */
    public boolean isTTSKey;
    /* access modifiers changed from: private */
    public int itemPosition;
    /* access modifiers changed from: private */
    public Button mBtn1;
    /* access modifiers changed from: private */
    public Button mBtn2;
    /* access modifiers changed from: private */
    public Button mBtn3;
    private StateScheduleListCallback<?> mCallback;
    /* access modifiers changed from: private */
    public ListView mDataItemList;
    private List<SetConfigListViewAdapter.DataItem> mDataList;
    private final View.AccessibilityDelegate mDelegate;
    private TextView mDiskInfo;
    /* access modifiers changed from: private */
    public final LayoutInflater mInflater;
    private MtkTvBookingBase mScheItem;
    private StateScheduleList mState;
    private HashMap<String, Boolean> mWeekItem;
    /* access modifiers changed from: private */
    public GridView mWeekList;
    /* access modifiers changed from: private */
    public int position;
    private View.OnClickListener replaceListener;
    private String[] srcArray;
    String startDate;
    long startRecordTime;
    private final int temp_count;
    private final float wScale;
    private boolean weekflag;

    enum BtnTypeEnum {
        ADD_CANCEL,
        EDIT_DELETE_CANCEL,
        REPLACE_ADD_CANCEL
    }

    /* access modifiers changed from: private */
    public int findSelectItem(String text) {
        if (this.mDataList == null) {
            return -1;
        }
        for (int i = 0; i < this.mDataList.size(); i++) {
            if (this.mDataList.get(i).getmName().equals(text)) {
                return i;
            }
        }
        return -1;
    }

    public ScheduleListItemInfoDialog(Context context) {
        super(context, R.layout.pvr_tshfit_schudulelist_item);
        this.epgFlag = false;
        this.TAG = "ScheduleListItemInfoDialog";
        this.wScale = 0.85f;
        this.hScale = 0.99f;
        this.mWeekItem = new HashMap<>();
        this.weekflag = false;
        this.UI_Index_SrcType = 0;
        this.UI_Index_ChannelNum = 1;
        this.UI_Index_StartDate = 2;
        this.UI_Index_StartTime = 3;
        this.UI_Index_StopTime = 4;
        this.UI_Index_ScheduleType = 5;
        this.UI_Index_RepeatType = 6;
        this.temp_count = 20;
        this.btnListType = BtnTypeEnum.ADD_CANCEL;
        this.position = 0;
        this.itemPosition = -1;
        this.isTTSKey = false;
        this.mDelegate = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                List<CharSequence> texts;
                if (ScheduleListItemInfoDialog.this.mDataItemList == host && (texts = event.getText()) != null) {
                    if (event.getEventType() == 32768) {
                        MtkLog.d("ScheduleListItemInfoDialog", "TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                        int index = ScheduleListItemInfoDialog.this.findSelectItem(texts.get(0).toString());
                        MtkLog.d("ScheduleListItemInfoDialog", "index = " + index);
                        if (index >= 0) {
                            ((SetConfigListViewAdapter) ScheduleListItemInfoDialog.this.mDataItemList.getAdapter()).setSelectPos(index);
                            ScheduleListItemInfoDialog.this.showHightLight(index);
                            int unused = ScheduleListItemInfoDialog.this.itemPosition = index;
                            boolean unused2 = ScheduleListItemInfoDialog.this.isTTSKey = true;
                        }
                    } else if (event.getEventType() == 1) {
                        MtkLog.d("ScheduleListItemInfoDialog", "TYPE_VIEW_CLICKED");
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d("ScheduleListItemInfoDialog", "Exception " + e);
                    return true;
                }
            }
        };
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                OptionView view;
                int i = msg.what;
                if (i == 100) {
                    ScheduleListItemInfoDialog.this.dismiss();
                    ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                    scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                    scheduleListDialog.show();
                } else if (i != 111) {
                    if (i == 222) {
                        ScheduleListItemInfoDialog.this.mBtn1.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn1.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn1.setSelected(false);
                        ScheduleListItemInfoDialog.this.mBtn2.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn2.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn2.setSelected(false);
                        ScheduleListItemInfoDialog.this.mBtn3.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn3.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn3.setSelected(false);
                    } else if (i == 333) {
                        ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                        ScheduleListItemInfoDialog.this.mBtn1.setFocusable(true);
                        ScheduleListItemInfoDialog.this.mBtn1.requestFocus();
                        ScheduleListItemInfoDialog.this.mBtn1.setSelected(true);
                    }
                } else if (ScheduleListItemInfoDialog.this.getBtnListType() == BtnTypeEnum.EDIT_DELETE_CANCEL) {
                    ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                    ScheduleListItemInfoDialog.this.mBtn3.requestFocus();
                    ScheduleListItemInfoDialog.this.mBtn3.setSelected(true);
                } else {
                    ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                    ScheduleListItemInfoDialog.this.mBtn3.requestFocus();
                    ScheduleListItemInfoDialog.this.mBtn3.setSelected(true);
                    if (ScheduleListItemInfoDialog.this.getScheItem().getRecordMode() != 0 && ScheduleListItemInfoDialog.this.getScheItem().getSourceType() == 1 && (view = (OptionView) ScheduleListItemInfoDialog.this.mDataItemList.getChildAt(1)) != null && view.getValueView() != null) {
                        view.getValueView().setVisibility(4);
                    }
                }
            }
        };
        this.startRecordTime = 0;
        this.startDate = "";
        this.WEEK_ARRAY = this.mContext.getResources().getStringArray(R.array.week_day);
        scheduleListItemInfoDialog = this;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
    }

    public ScheduleListItemInfoDialog(Context context, MtkTvBookingBase item) {
        super(context, R.layout.pvr_tshfit_schudulelist_item);
        this.epgFlag = false;
        this.TAG = "ScheduleListItemInfoDialog";
        this.wScale = 0.85f;
        this.hScale = 0.99f;
        this.mWeekItem = new HashMap<>();
        this.weekflag = false;
        this.UI_Index_SrcType = 0;
        this.UI_Index_ChannelNum = 1;
        this.UI_Index_StartDate = 2;
        this.UI_Index_StartTime = 3;
        this.UI_Index_StopTime = 4;
        this.UI_Index_ScheduleType = 5;
        this.UI_Index_RepeatType = 6;
        this.temp_count = 20;
        this.btnListType = BtnTypeEnum.ADD_CANCEL;
        this.position = 0;
        this.itemPosition = -1;
        this.isTTSKey = false;
        this.mDelegate = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                List<CharSequence> texts;
                if (ScheduleListItemInfoDialog.this.mDataItemList == host && (texts = event.getText()) != null) {
                    if (event.getEventType() == 32768) {
                        MtkLog.d("ScheduleListItemInfoDialog", "TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                        int index = ScheduleListItemInfoDialog.this.findSelectItem(texts.get(0).toString());
                        MtkLog.d("ScheduleListItemInfoDialog", "index = " + index);
                        if (index >= 0) {
                            ((SetConfigListViewAdapter) ScheduleListItemInfoDialog.this.mDataItemList.getAdapter()).setSelectPos(index);
                            ScheduleListItemInfoDialog.this.showHightLight(index);
                            int unused = ScheduleListItemInfoDialog.this.itemPosition = index;
                            boolean unused2 = ScheduleListItemInfoDialog.this.isTTSKey = true;
                        }
                    } else if (event.getEventType() == 1) {
                        MtkLog.d("ScheduleListItemInfoDialog", "TYPE_VIEW_CLICKED");
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d("ScheduleListItemInfoDialog", "Exception " + e);
                    return true;
                }
            }
        };
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                OptionView view;
                int i = msg.what;
                if (i == 100) {
                    ScheduleListItemInfoDialog.this.dismiss();
                    ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                    scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                    scheduleListDialog.show();
                } else if (i != 111) {
                    if (i == 222) {
                        ScheduleListItemInfoDialog.this.mBtn1.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn1.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn1.setSelected(false);
                        ScheduleListItemInfoDialog.this.mBtn2.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn2.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn2.setSelected(false);
                        ScheduleListItemInfoDialog.this.mBtn3.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn3.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn3.setSelected(false);
                    } else if (i == 333) {
                        ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                        ScheduleListItemInfoDialog.this.mBtn1.setFocusable(true);
                        ScheduleListItemInfoDialog.this.mBtn1.requestFocus();
                        ScheduleListItemInfoDialog.this.mBtn1.setSelected(true);
                    }
                } else if (ScheduleListItemInfoDialog.this.getBtnListType() == BtnTypeEnum.EDIT_DELETE_CANCEL) {
                    ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                    ScheduleListItemInfoDialog.this.mBtn3.requestFocus();
                    ScheduleListItemInfoDialog.this.mBtn3.setSelected(true);
                } else {
                    ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                    ScheduleListItemInfoDialog.this.mBtn3.requestFocus();
                    ScheduleListItemInfoDialog.this.mBtn3.setSelected(true);
                    if (ScheduleListItemInfoDialog.this.getScheItem().getRecordMode() != 0 && ScheduleListItemInfoDialog.this.getScheItem().getSourceType() == 1 && (view = (OptionView) ScheduleListItemInfoDialog.this.mDataItemList.getChildAt(1)) != null && view.getValueView() != null) {
                        view.getValueView().setVisibility(4);
                    }
                }
            }
        };
        this.startRecordTime = 0;
        this.startDate = "";
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        getWindow().setLayout((int) (((float) outSize.x) * 0.85f), (int) (((float) outSize.y) * 0.99f));
        this.position = this.position;
        MtkLog.d("ScheduleListItemInfoDialog", "ScheduleListItemInfoDialog = " + item.toString());
        setScheItem(item);
        this.mWeekItem = prepareWeekItem(getScheItem());
        this.WEEK_ARRAY = this.mContext.getResources().getStringArray(R.array.week_day);
        scheduleListItemInfoDialog = this;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        initView2();
    }

    public ScheduleListItemInfoDialog(Context context, MtkTvBookingBase item, int position2) {
        super(context, R.layout.pvr_tshfit_schudulelist_item);
        this.epgFlag = false;
        this.TAG = "ScheduleListItemInfoDialog";
        this.wScale = 0.85f;
        this.hScale = 0.99f;
        this.mWeekItem = new HashMap<>();
        this.weekflag = false;
        this.UI_Index_SrcType = 0;
        this.UI_Index_ChannelNum = 1;
        this.UI_Index_StartDate = 2;
        this.UI_Index_StartTime = 3;
        this.UI_Index_StopTime = 4;
        this.UI_Index_ScheduleType = 5;
        this.UI_Index_RepeatType = 6;
        this.temp_count = 20;
        this.btnListType = BtnTypeEnum.ADD_CANCEL;
        this.position = 0;
        this.itemPosition = -1;
        this.isTTSKey = false;
        this.mDelegate = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                List<CharSequence> texts;
                if (ScheduleListItemInfoDialog.this.mDataItemList == host && (texts = event.getText()) != null) {
                    if (event.getEventType() == 32768) {
                        MtkLog.d("ScheduleListItemInfoDialog", "TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                        int index = ScheduleListItemInfoDialog.this.findSelectItem(texts.get(0).toString());
                        MtkLog.d("ScheduleListItemInfoDialog", "index = " + index);
                        if (index >= 0) {
                            ((SetConfigListViewAdapter) ScheduleListItemInfoDialog.this.mDataItemList.getAdapter()).setSelectPos(index);
                            ScheduleListItemInfoDialog.this.showHightLight(index);
                            int unused = ScheduleListItemInfoDialog.this.itemPosition = index;
                            boolean unused2 = ScheduleListItemInfoDialog.this.isTTSKey = true;
                        }
                    } else if (event.getEventType() == 1) {
                        MtkLog.d("ScheduleListItemInfoDialog", "TYPE_VIEW_CLICKED");
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d("ScheduleListItemInfoDialog", "Exception " + e);
                    return true;
                }
            }
        };
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                OptionView view;
                int i = msg.what;
                if (i == 100) {
                    ScheduleListItemInfoDialog.this.dismiss();
                    ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                    scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                    scheduleListDialog.show();
                } else if (i != 111) {
                    if (i == 222) {
                        ScheduleListItemInfoDialog.this.mBtn1.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn1.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn1.setSelected(false);
                        ScheduleListItemInfoDialog.this.mBtn2.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn2.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn2.setSelected(false);
                        ScheduleListItemInfoDialog.this.mBtn3.setFocusable(false);
                        ScheduleListItemInfoDialog.this.mBtn3.clearFocus();
                        ScheduleListItemInfoDialog.this.mBtn3.setSelected(false);
                    } else if (i == 333) {
                        ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                        ScheduleListItemInfoDialog.this.mBtn1.setFocusable(true);
                        ScheduleListItemInfoDialog.this.mBtn1.requestFocus();
                        ScheduleListItemInfoDialog.this.mBtn1.setSelected(true);
                    }
                } else if (ScheduleListItemInfoDialog.this.getBtnListType() == BtnTypeEnum.EDIT_DELETE_CANCEL) {
                    ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                    ScheduleListItemInfoDialog.this.mBtn3.requestFocus();
                    ScheduleListItemInfoDialog.this.mBtn3.setSelected(true);
                } else {
                    ScheduleListItemInfoDialog.this.mBtn3.setFocusable(true);
                    ScheduleListItemInfoDialog.this.mBtn3.requestFocus();
                    ScheduleListItemInfoDialog.this.mBtn3.setSelected(true);
                    if (ScheduleListItemInfoDialog.this.getScheItem().getRecordMode() != 0 && ScheduleListItemInfoDialog.this.getScheItem().getSourceType() == 1 && (view = (OptionView) ScheduleListItemInfoDialog.this.mDataItemList.getChildAt(1)) != null && view.getValueView() != null) {
                        view.getValueView().setVisibility(4);
                    }
                }
            }
        };
        this.startRecordTime = 0;
        this.startDate = "";
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        getWindow().setLayout((int) (((float) outSize.x) * 0.85f), (int) (((float) outSize.y) * 0.99f));
        setScheItem(item);
        this.mWeekItem = prepareWeekItem(getScheItem());
        this.position = position2;
        this.WEEK_ARRAY = this.mContext.getResources().getStringArray(R.array.week_day);
        scheduleListItemInfoDialog = this;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        initView2();
    }

    public void dismiss() {
        if (this.confirm != null && this.confirm.isShowing()) {
            this.confirm.dismiss();
        }
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
        } catch (Throwable th) {
            scheduleListItemInfoDialog = null;
            throw th;
        }
        scheduleListItemInfoDialog = null;
    }

    private HashMap<String, Boolean> prepareWeekItem(MtkTvBookingBase item) {
        HashMap<String, Boolean> mapList = new HashMap<>();
        if (item.getRepeatMode() == 0 || item.getRepeatMode() == 128) {
            String[] array = this.mContext.getResources().getStringArray(R.array.week_day);
            int weekday = MtkTvTime.getInstance().getLocalTime().weekDay;
            MtkLog.d("ScheduleListItemInfoDialog", "weekday==" + weekday);
            for (int i = 0; i < 7; i++) {
                if (i == weekday) {
                    mapList.put(array[i], true);
                } else {
                    mapList.put(array[i], false);
                }
            }
        } else {
            int repeat = item.getRepeatMode();
            String[] array2 = this.mContext.getResources().getStringArray(R.array.week_day);
            for (int i2 = 0; i2 < 7; i2++) {
                if (((1 << i2) & repeat) != 0) {
                    mapList.put(array2[i2], true);
                } else {
                    mapList.put(array2[i2], false);
                }
            }
        }
        return mapList;
    }

    public void initView() {
        super.initView();
        this.mDataItemList = (ListView) findViewById(R.id.schedulelist_item_list);
        this.mDataItemList.setDivider((Drawable) null);
        this.mDiskInfo = (TextView) findViewById(R.id.schedulelist_item_title_diskinfo);
        this.mWeekList = (GridView) findViewById(R.id.week_gridview);
        this.mBtn1 = (Button) findViewById(R.id.schedulelist_item_btn_first);
        this.mBtn2 = (Button) findViewById(R.id.schedulelist_item_btn_second);
        this.mBtn3 = (Button) findViewById(R.id.schedulelist_item_btn_third);
        initListener();
    }

    @SuppressLint({"NewApi"})
    private void initView2() {
        SetConfigListViewAdapter infoAdapter = new SetConfigListViewAdapter(this.mContext);
        this.mDataList = initItemList();
        infoAdapter.setmGroup(this.mDataList);
        infoAdapter.setSrctypeChangelistener(this);
        this.mDataItemList.setAdapter(infoAdapter);
        this.mDataItemList.setOnItemClickListener(this);
        this.mDataItemList.setOnItemSelectedListener(this);
        this.mWeekList.setChoiceMode(3);
        this.mWeekList.setOnItemClickListener(this);
        this.mWeekList.setAdapter(new WeekListAdapter(this.mWeekItem));
        if (getScheItem().getChannelId() == 0) {
            setBtnListType(BtnTypeEnum.ADD_CANCEL);
        } else {
            setBtnListType(BtnTypeEnum.EDIT_DELETE_CANCEL);
        }
        MountPoint mp = null;
        ArrayList<MountPoint> list = DevManager.getInstance().getMountList();
        if (list != null && list.size() > 0) {
            mp = list.get(0);
        }
        this.mDiskInfo.setText(Util.getGBSizeOfDisk(mp));
        if (getScheItem().getRepeatMode() == 0 || getScheItem().getRepeatMode() == 128) {
            this.mWeekList.setVisibility(4);
        } else {
            this.mWeekList.setVisibility(0);
        }
        reSetBtnList();
        this.handler.sendEmptyMessageDelayed(111, 300);
        updateDissmissTimer();
        this.mDataItemList.setAccessibilityDelegate(this.mDelegate);
    }

    /* access modifiers changed from: private */
    public void reSetBtnList() {
        switch (getBtnListType()) {
            case ADD_CANCEL:
                this.mBtn1.setVisibility(4);
                this.mBtn2.setText(this.mContext.getResources().getString(R.string.pvr_schedule_add));
                this.mBtn2.setVisibility(0);
                this.mBtn3.setText(this.mContext.getResources().getString(R.string.pvr_schedule_cancel));
                this.mBtn2.setOnClickListener(this.addListener);
                this.mBtn3.setOnClickListener(this.cancelListener);
                break;
            case EDIT_DELETE_CANCEL:
                this.mBtn1.setVisibility(0);
                this.mBtn1.setText(this.mContext.getResources().getString(R.string.pvr_schedule_edit));
                this.mBtn2.setText(this.mContext.getResources().getString(R.string.pager_mid));
                this.mBtn2.setVisibility(0);
                this.mBtn3.setText(this.mContext.getResources().getString(R.string.pvr_schedule_ok));
                this.mBtn1.setOnClickListener(this.editListener);
                this.mBtn2.setOnClickListener(this.deleteListener);
                this.mBtn3.setOnClickListener(this.cancelListener);
                break;
            case REPLACE_ADD_CANCEL:
                this.mBtn1.setVisibility(0);
                this.mBtn1.setText(this.mContext.getResources().getString(R.string.pvr_schedule_replace));
                this.mBtn2.setText(this.mContext.getResources().getString(R.string.pvr_schedule_add));
                this.mBtn2.setVisibility(0);
                this.mBtn3.setText(this.mContext.getResources().getString(R.string.pvr_schedule_cancel));
                this.mBtn1.setOnClickListener(this.replaceListener);
                this.mBtn2.setOnClickListener(this.addListener);
                this.mBtn3.setOnClickListener(this.cancelListener);
                this.mBtn3.setFocusable(true);
                this.mBtn3.requestFocus();
                this.mBtn3.setSelected(true);
                break;
        }
        reSetFocusPath();
    }

    /* access modifiers changed from: package-private */
    public void reSetFocusPath() {
        if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
            this.mBtn1.setNextFocusUpId(R.id.week_gridview);
            this.mBtn1.setNextFocusLeftId(R.id.week_gridview);
        } else {
            this.mBtn1.setNextFocusUpId(this.mBtn1.getId());
        }
        this.mBtn1.setNextFocusLeftId(this.mBtn3.getId());
        if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
            this.mBtn2.setNextFocusUpId(R.id.week_gridview);
        } else {
            this.mBtn2.setNextFocusUpId(this.mBtn2.getId());
        }
        this.mBtn2.setNextFocusLeftId(R.id.week_gridview);
        int id = R.id.week_gridview;
        if (this.mWeekList.getVisibility() != 0) {
            id = R.id.schedulelist_item_list;
        }
        if (this.mBtn1.getVisibility() == 0) {
            if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
                this.mBtn1.setNextFocusUpId(id);
                this.mBtn1.setNextFocusLeftId(id);
            } else {
                this.mBtn1.setNextFocusUpId(this.mBtn1.getId());
                this.mBtn1.setNextFocusLeftId(this.mBtn3.getId());
            }
            if (this.mBtn2.getVisibility() == 0) {
                this.mBtn1.setNextFocusRightId(this.mBtn2.getId());
            } else {
                this.mBtn1.setNextFocusRightId(this.mBtn3.getId());
            }
        }
        if (this.mBtn2.getVisibility() == 0) {
            if (this.mWeekList.getVisibility() == 0) {
                if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
                    this.mBtn2.setNextFocusUpId(this.mWeekList.getId());
                } else {
                    this.mBtn2.setNextFocusUpId(this.mBtn2.getId());
                }
            }
            if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
                this.mBtn2.setNextFocusUpId(id);
            } else {
                this.mBtn2.setNextFocusUpId(this.mBtn2.getId());
            }
            if (this.mBtn1.getVisibility() == 0) {
                this.mBtn2.setNextFocusLeftId(this.mBtn1.getId());
            } else {
                this.mBtn2.setNextFocusLeftId(this.mBtn3.getId());
            }
            this.mBtn2.setNextFocusRightId(this.mBtn3.getId());
        }
        if (this.mBtn3.getVisibility() == 0) {
            if (this.mWeekList.getVisibility() == 0) {
                if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
                    this.mBtn3.setNextFocusUpId(this.mWeekList.getId());
                } else {
                    this.mBtn3.setNextFocusUpId(this.mBtn3.getId());
                }
            }
            if (getBtnListType() != BtnTypeEnum.EDIT_DELETE_CANCEL) {
                this.mBtn3.setNextFocusUpId(id);
            } else {
                this.mBtn3.setNextFocusUpId(this.mBtn3.getId());
            }
            if (this.mBtn2.getVisibility() == 0) {
                this.mBtn3.setNextFocusLeftId(this.mBtn2.getId());
            } else {
                this.mBtn3.setNextFocusLeftId(this.mBtn1.getId());
            }
            if (this.mBtn1.getVisibility() == 0) {
                this.mBtn3.setNextFocusRightId(this.mBtn1.getId());
            } else {
                this.mBtn3.setNextFocusRightId(this.mBtn2.getId());
            }
        }
    }

    private List<SetConfigListViewAdapter.DataItem> initItemList() {
        int channelIndex;
        int repeatmode;
        MtkTvBookingBase item = getScheItem();
        List<SetConfigListViewAdapter.DataItem> items = new ArrayList<>();
        this.srcArray = this.mContext.getResources().getStringArray(R.array.pvr_tshift_srctype);
        List<TIFChannelInfo> list = TIFChannelManager.getInstance(this.mContext).getAllDTVTIFChannels();
        if (list == null || list.size() == 0) {
            dismiss();
        }
        int defsrctype = 0;
        if (item.getRecordMode() != 0) {
            int i = 0;
            while (true) {
                if (i >= this.srcArray.length) {
                    break;
                } else if (item.getSourceType() == 1) {
                    defsrctype = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        SetConfigListViewAdapter.DataItem srcType = new SetConfigListViewAdapter.DataItem("SCHEDULE_PVR_SRCTYPE", this.mContext.getString(R.string.schedule_pvr_srctype), 10004, 10004, defsrctype, this.srcArray, 1, SetConfigListViewAdapter.DataItem.DataType.OPTIONVIEW);
        items.add(srcType);
        String[] channelArray = new String[list.size()];
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < list.size(); i2++) {
            String channelName = list.get(i2).mDisplayName;
            String channelNumber = "" + list.get(i2).mDisplayNumber;
            if (channelName == null) {
                channelName = channelNumber;
            }
            if (channelName.equalsIgnoreCase(channelNumber)) {
                channelNumber = "";
            }
            channelArray[i2] = String.format("CH%s:%3s%s", new Object[]{channelNumber, "", channelName});
            arrayList.add(Integer.valueOf(list.get(i2).mMtkTvChannelInfo.getChannelId()));
        }
        int i3 = 0;
        if (arrayList.size() > 0) {
            int channelIndex2 = 0;
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                if (((Integer) arrayList.get(i4)).intValue() == item.getChannelId()) {
                    channelIndex2 = i4;
                }
            }
            i3 = channelIndex2;
        }
        if (item.getChannelId() == 0) {
            int channelIndex3 = i3;
            for (int i5 = 0; i5 < arrayList.size(); i5++) {
                if (((Integer) arrayList.get(i5)).intValue() == EditChannel.getInstance(this.mContext).getCurrentChannelId()) {
                    channelIndex3 = i5;
                }
            }
            channelIndex = channelIndex3;
        } else {
            channelIndex = i3;
        }
        Util.showDLog("ScheduleListItemInfoDialog", "schedulelist channelIndex:" + channelIndex + "==>:" + channelIndex);
        ArrayList arrayList2 = arrayList;
        items.add(new SetConfigListViewAdapter.DataItem("SCHEDULE_PVR_CHANNELLIST", this.mContext.getString(R.string.schedule_pvr_channel_num), 10004, 10004, channelIndex, channelArray, 1, SetConfigListViewAdapter.DataItem.DataType.OPTIONVIEW));
        long startDate2 = item.getRecordStartTime();
        if (startDate2 == 0) {
            startDate2 = System.currentTimeMillis() / 1000;
        }
        SetConfigListViewAdapter.DataItem timeSet_date = new SetConfigListViewAdapter.DataItem("SETUP_date", this.mContext.getString(R.string.schedule_pvr_start_date), 10004, 10004, 0, (String[]) null, 1, SetConfigListViewAdapter.DataItem.DataType.DATETIMEVIEW);
        timeSet_date.setmDateTimeType(0);
        List<TIFChannelInfo> list2 = list;
        int i6 = defsrctype;
        timeSet_date.setmDateTimeStr(Util.timeToDateStringEx(startDate2 * 1000, 0));
        timeSet_date.setAutoUpdate(false);
        SetConfigListViewAdapter.DataItem dataItem = new SetConfigListViewAdapter.DataItem("SETUP_time", this.mContext.getString(R.string.schedule_pvr_start_time), 10004, 10004, 0, (String[]) null, 1, SetConfigListViewAdapter.DataItem.DataType.DATETIMEVIEW);
        dataItem.setmDateTimeType(1);
        Object obj = "SCHEDULE_PVR_SRCTYPE";
        String str = Util.timeToTimeStringEx(startDate2 * 1000, 0);
        MtkLog.d("ScheduleListItemInfoDialog", "startTime = " + startDate2 + " str = " + str);
        dataItem.setmDateTimeStr(str);
        dataItem.setAutoUpdate(false);
        SetConfigListViewAdapter.DataItem dataItem2 = new SetConfigListViewAdapter.DataItem("SETUP_time", this.mContext.getString(R.string.schedule_pvr_stop_time), 10004, 10004, 0, (String[]) null, 1, SetConfigListViewAdapter.DataItem.DataType.DATETIMEVIEW);
        dataItem2.setmDateTimeType(1);
        SetConfigListViewAdapter.DataItem dataItem3 = srcType;
        String[] strArr = channelArray;
        dataItem2.setmDateTimeStr(Util.timeToTimeStringEx((startDate2 + item.getRecordDuration()) * 1000, 0));
        dataItem2.setAutoUpdate(false);
        items.add(timeSet_date);
        items.add(dataItem);
        items.add(dataItem2);
        SetConfigListViewAdapter.DataItem dataItem4 = dataItem;
        String str2 = str;
        SetConfigListViewAdapter.DataItem reminderType = new SetConfigListViewAdapter.DataItem("SCHEDULE_PVR_REMINDER_TYPE", this.mContext.getString(R.string.schedule_pvr_reminder_type), 10004, 10004, item.getRecordMode() == 2 ? 0 : item.getRecordMode(), this.mContext.getResources().getStringArray(R.array.pvr_tshift_schedule_type), 1, SetConfigListViewAdapter.DataItem.DataType.OPTIONVIEW);
        items.add(reminderType);
        SetConfigListViewAdapter.DataItem dataItem5 = reminderType;
        String[] repeatArray = this.mContext.getResources().getStringArray(R.array.pvr_tshift_repeat_type);
        if (item.getRepeatMode() == 0) {
            MtkTvBookingBase mtkTvBookingBase = item;
            SetConfigListViewAdapter.DataItem dataItem6 = dataItem2;
            repeatmode = 2;
        } else {
            SetConfigListViewAdapter.DataItem dataItem7 = dataItem2;
            MtkTvBookingBase mtkTvBookingBase2 = item;
            if (item.getRepeatMode() == 128) {
                repeatmode = 0;
            } else {
                repeatmode = 1;
            }
        }
        SetConfigListViewAdapter.DataItem dataItem8 = timeSet_date;
        items.add(new SetConfigListViewAdapter.DataItem("SCHEDULE_PVR_REPEAT_TYPE", this.mContext.getString(R.string.schedule_pvr_repeat_type), 10004, 10004, repeatmode, repeatArray, 1, SetConfigListViewAdapter.DataItem.DataType.OPTIONVIEW));
        return items;
    }

    private void initListener() {
        this.mDataItemList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                MtkLog.d("mDataItemList.onFocusChange,", v.getClass().getSimpleName());
                if (!(v instanceof OptionView)) {
                    return;
                }
                if (hasFocus) {
                    ((OptionView) v).setRightImageSource(true);
                } else {
                    ((OptionView) v).setRightImageSource(false);
                }
            }
        });
        this.mDataItemList.setOnKeyListener(new View.OnKeyListener() {
            /* JADX WARNING: Code restructure failed: missing block: B:10:0x004b, code lost:
                if ((r1 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) == false) goto L_0x005d;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:11:0x004d, code lost:
                ((com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r1).onKeyRight();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:12:0x0055, code lost:
                if ((r1 instanceof com.mediatek.wwtv.setting.view.OptionView) == false) goto L_0x0062;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:13:0x0057, code lost:
                com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$900(r4.this$0, r1);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:14:0x005d, code lost:
                com.mediatek.wwtv.setting.util.Util.showDLog("optionView instanceof RespondedKeyEvent: false");
             */
            /* JADX WARNING: Code restructure failed: missing block: B:15:0x0062, code lost:
                return true;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:16:0x0063, code lost:
                r1 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$000(r4.this$0).getChildAt(com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$000(r4.this$0).getSelectedItemPosition());
                com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$302(r4.this$0, com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$000(r4.this$0).getSelectedItemPosition());
             */
            /* JADX WARNING: Code restructure failed: missing block: B:17:0x0088, code lost:
                if ((r1 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) == false) goto L_0x009a;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:18:0x008a, code lost:
                ((com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r1).onKeyLeft();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:19:0x0092, code lost:
                if ((r1 instanceof com.mediatek.wwtv.setting.view.OptionView) == false) goto L_0x009f;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:20:0x0094, code lost:
                com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$900(r4.this$0, r1);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:21:0x009a, code lost:
                com.mediatek.wwtv.setting.util.Util.showDLog("optionView instanceof RespondedKeyEvent: false");
             */
            /* JADX WARNING: Code restructure failed: missing block: B:22:0x009f, code lost:
                return true;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:9:0x0030, code lost:
                r1 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$000(r4.this$0).getSelectedView();
                com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$302(r4.this$0, com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.access$000(r4.this$0).getSelectedItemPosition());
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onKey(android.view.View r5, int r6, android.view.KeyEvent r7) {
                /*
                    r4 = this;
                    int r0 = r7.getAction()
                    r1 = 0
                    if (r0 == 0) goto L_0x0008
                    return r1
                L_0x0008:
                    java.lang.String r0 = "ScheduleListItemInfoDialog"
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "mDataItemList.onKey = "
                    r2.append(r3)
                    r2.append(r6)
                    java.lang.String r2 = r2.toString()
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r2)
                    r0 = 165(0xa5, float:2.31E-43)
                    if (r6 == r0) goto L_0x00c5
                    switch(r6) {
                        case 7: goto L_0x00b6;
                        case 8: goto L_0x00b6;
                        case 9: goto L_0x00b6;
                        case 10: goto L_0x00b6;
                        case 11: goto L_0x00b6;
                        case 12: goto L_0x00b6;
                        case 13: goto L_0x00b6;
                        case 14: goto L_0x00b6;
                        case 15: goto L_0x00b6;
                        case 16: goto L_0x00b6;
                        default: goto L_0x0025;
                    }
                L_0x0025:
                    r0 = 1
                    switch(r6) {
                        case 19: goto L_0x00ab;
                        case 20: goto L_0x00a0;
                        case 21: goto L_0x0063;
                        case 22: goto L_0x0030;
                        case 23: goto L_0x002e;
                        default: goto L_0x0029;
                    }
                L_0x0029:
                    switch(r6) {
                        case 183: goto L_0x0063;
                        case 184: goto L_0x0030;
                        default: goto L_0x002c;
                    }
                L_0x002c:
                    goto L_0x00cb
                L_0x002e:
                    goto L_0x00cb
                L_0x0030:
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r1 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r1 = r1.mDataItemList
                    android.view.View r1 = r1.getSelectedView()
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r3 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r3 = r3.mDataItemList
                    int r3 = r3.getSelectedItemPosition()
                    int unused = r2.itemPosition = r3
                    boolean r2 = r1 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent
                    if (r2 == 0) goto L_0x005d
                    r2 = r1
                    com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent r2 = (com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r2
                    r2.onKeyRight()
                    boolean r2 = r1 instanceof com.mediatek.wwtv.setting.view.OptionView
                    if (r2 == 0) goto L_0x0062
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    r2.hiddenWeekList(r1)
                    goto L_0x0062
                L_0x005d:
                    java.lang.String r2 = "optionView instanceof RespondedKeyEvent: false"
                    com.mediatek.wwtv.setting.util.Util.showDLog(r2)
                L_0x0062:
                    return r0
                L_0x0063:
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r1 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r1 = r1.mDataItemList
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r2 = r2.mDataItemList
                    int r2 = r2.getSelectedItemPosition()
                    android.view.View r1 = r1.getChildAt(r2)
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r3 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r3 = r3.mDataItemList
                    int r3 = r3.getSelectedItemPosition()
                    int unused = r2.itemPosition = r3
                    boolean r2 = r1 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent
                    if (r2 == 0) goto L_0x009a
                    r2 = r1
                    com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent r2 = (com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r2
                    r2.onKeyLeft()
                    boolean r2 = r1 instanceof com.mediatek.wwtv.setting.view.OptionView
                    if (r2 == 0) goto L_0x009f
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    r2.hiddenWeekList(r1)
                    goto L_0x009f
                L_0x009a:
                    java.lang.String r2 = "optionView instanceof RespondedKeyEvent: false"
                    com.mediatek.wwtv.setting.util.Util.showDLog(r2)
                L_0x009f:
                    return r0
                L_0x00a0:
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r0 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r0 = r0.mDataItemList
                    int r0 = r0.getSelectedItemPosition()
                    return r1
                L_0x00ab:
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r0 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r0 = r0.mDataItemList
                    int r0 = r0.getSelectedItemPosition()
                    return r1
                L_0x00b6:
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r0 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r0 = r0.mDataItemList
                    android.view.View r0 = r0.getSelectedView()
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    boolean unused = r2.handleKeyboardInput(r0, r6)
                L_0x00c5:
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r0 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    r0.dumpValuesInfo()
                L_0x00cb:
                    r0 = 23
                    if (r6 != r0) goto L_0x00e8
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r0 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r0 = r0.mDataItemList
                    android.view.View r0 = r0.getFocusedChild()
                    if (r0 == 0) goto L_0x00e8
                    com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog r0 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.this
                    android.widget.ListView r0 = r0.mDataItemList
                    android.view.View r0 = r0.getFocusedChild()
                    r0.performClick()
                L_0x00e8:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.AnonymousClass4.onKey(android.view.View, int, android.view.KeyEvent):boolean");
            }
        });
        this.mWeekList.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Util.showDLog("mWeekList.setOnKeyListener: keycode:" + keyCode);
                Util.showDLog("mWeekList.setOnKeyListener Action:" + event.getAction());
                if (keyCode != 23 || event.getAction() != 0) {
                    return false;
                }
                if (ScheduleListItemInfoDialog.this.mWeekList.getSelectedView() != null) {
                    Util.showDLog("mWeekList.getSelectedView():::" + ScheduleListItemInfoDialog.this.mWeekList.getSelectedView().getClass().toString());
                    View view = ScheduleListItemInfoDialog.this.mWeekList.getSelectedView().findViewById(R.id.schedule_list_item_week_item_cb);
                    if (view != null) {
                        view.performClick();
                        return false;
                    }
                    Util.showDLog("findViewById R.id.schedule_list_item_week_item_cb Null");
                    return false;
                }
                Util.showDLog("mWeekList.getSelectedView() Null");
                return false;
            }
        });
        this.addListener = new View.OnClickListener() {
            public void onClick(View v) {
                MtkTvBookingBase item = ScheduleListItemInfoDialog.this.prepareItem();
                int code = ScheduleListItemInfoDialog.this.checkItem(item);
                if (code != 0) {
                    Util.showELog("checkItem fail,Error Code: " + code);
                    String format = new SimpleDateFormat("HH:mm:ss").format(Long.valueOf(item.getRecordStartTime()));
                    Toast.makeText(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.mContext.getResources().getString(R.string.schedule_toast_time_before), 0).show();
                    return;
                }
                List<MtkTvBookingBase> items = ScheduleListItemInfoDialog.this.getReplaceItems(item);
                if (items != null && items.size() > 0) {
                    MtkLog.d("ScheduleListItemInfoDialog", "items===" + items.size());
                    ScheduleListItemInfoDialog.this.showAddConfirmDialog(items, item);
                } else if (StateScheduleList.getInstance() == null || StateScheduleList.getInstance().queryItem().size() < 5) {
                    MtkLog.d("ScheduleListItemInfoDialog", "items==" + item.toString());
                    if (StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).insertItem(item)) {
                        ScheduleListItemInfoDialog.this.handler.sendEmptyMessage(100);
                    }
                } else {
                    Toast.makeText(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.mContext.getResources().getString(R.string.schedule_toast_max_five), 0).show();
                }
            }
        };
        this.replaceListener = new View.OnClickListener() {
            public void onClick(View v) {
                MtkTvBookingBase item = ScheduleListItemInfoDialog.this.prepareItem();
                MtkLog.d("ScheduleListItemInfoDialog", "item 1 =" + item.toString());
                if (ScheduleListItemInfoDialog.this.checkItem(item) == 0) {
                    List<MtkTvBookingBase> items = ScheduleListItemInfoDialog.this.getReplaceItems(item);
                    MtkLog.d("ScheduleListItemInfoDialog", " items 1 = " + items);
                    if (items == null || items.size() <= 0) {
                        MtkLog.d("ScheduleListItemInfoDialog", "items ==null");
                        ScheduleListItemInfoDialog.this.handler.sendEmptyMessageDelayed(222, 300);
                        StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).replaceItem(item);
                        ScheduleListItemInfoDialog.this.dismiss();
                        ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                        scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                        scheduleListDialog.show();
                        return;
                    }
                    MtkLog.d("ScheduleListItemInfoDialog", "items===" + items.size());
                    ScheduleListItemInfoDialog.this.handler.sendEmptyMessageDelayed(222, 300);
                    ScheduleListItemInfoDialog.this.showReplaceConfirmDialog(items, item);
                    return;
                }
                Toast.makeText(ScheduleListItemInfoDialog.this.mContext, "Replace fail", 0).show();
            }
        };
        this.cancelListener = new View.OnClickListener() {
            public void onClick(View v) {
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        };
        this.deleteListener = new View.OnClickListener() {
            public void onClick(View v) {
                ScheduleListItemInfoDialog.this.showDeleteConfirmDialog();
            }
        };
        this.editListener = new View.OnClickListener() {
            public void onClick(View v) {
                ScheduleListItemInfoDialog.this.setBtnListType(BtnTypeEnum.REPLACE_ADD_CANCEL);
                ScheduleListItemInfoDialog.this.reSetBtnList();
            }
        };
    }

    /* access modifiers changed from: private */
    public void hiddenWeekList(View optionViewL) {
        if (this.mDataList.get(this.itemPosition).getmItemID().equalsIgnoreCase("SCHEDULE_PVR_REPEAT_TYPE")) {
            int value = ((OptionView) optionViewL).getValue();
            if (value == 0 || value == 2) {
                this.mWeekList.setVisibility(4);
            } else {
                this.mWeekList.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public MtkTvBookingBase getRemoveList(List<MtkTvBookingBase> mtktvbooklist, MtkTvBookingBase m) {
        for (MtkTvBookingBase mm : mtktvbooklist) {
            if (mm.getRecordStartTime() == m.getRecordStartTime() && mm.getEventTitle().equals(m.getEventTitle()) && mm.getChannelId() == m.getChannelId() && mm.getDeviceIndex() == m.getDeviceIndex() && mm.getGenre() == m.getGenre() && mm.getRecordDuration() == m.getRecordDuration() && mm.getRecordDelay() == m.getRecordDelay() && mm.getRecordMode() == m.getRecordMode() && mm.getRepeatMode() == m.getRepeatMode() && mm.getSourceType() == m.getSourceType() && mm.getTunerType() == m.getTunerType() && mm.getRsltMode() == m.getRsltMode() && mm.getInfoData() == m.getInfoData()) {
                m.setBookingId(mm.getBookingId());
            }
        }
        return m;
    }

    /* access modifiers changed from: private */
    public List<MtkTvBookingBase> getReplaceItems(MtkTvBookingBase item) {
        Long startTime;
        Long endTime;
        Long startTime2 = Long.valueOf(item.getRecordStartTime());
        Long endTime2 = Long.valueOf(startTime2.longValue() + item.getRecordDuration());
        List<MtkTvBookingBase> replacetItems = new ArrayList<>();
        List<MtkTvBookingBase> items = StateScheduleList.getInstance(this.mContext).queryItem();
        if (items != null && items.size() > 0) {
            for (MtkTvBookingBase sItem : items) {
                Long sstartTime = Long.valueOf(sItem.getRecordStartTime());
                Long sendTime = Long.valueOf(sstartTime.longValue() + sItem.getRecordDuration());
                MtkLog.d("ScheduleListItemInfoDialog", " |startTime " + startTime2 + " |endTime " + endTime2 + " |sstartTime " + sstartTime + " |sendTime " + sendTime);
                if (startTime2.longValue() <= sstartTime.longValue() && endTime2.longValue() > sstartTime.longValue()) {
                    replacetItems.add(sItem);
                } else if (startTime2.longValue() >= sstartTime.longValue() && endTime2.longValue() <= sendTime.longValue()) {
                    replacetItems.add(sItem);
                } else if (startTime2.longValue() < sendTime.longValue() && endTime2.longValue() >= sendTime.longValue()) {
                    replacetItems.add(sItem);
                } else if (item.getRepeatMode() == 2) {
                    long mod = (sstartTime.longValue() - startTime2.longValue()) / 86400;
                    long yushu = (sstartTime.longValue() - startTime2.longValue()) % 86400;
                    MtkLog.d("ScheduleListItemInfoDialog", "mod===" + mod + "yushu==" + yushu);
                    if (yushu != 0 && yushu > 86300) {
                        mod++;
                    }
                    if (yushu <= -60 || yushu >= 0) {
                        startTime2 = Long.valueOf(startTime2.longValue() + (mod * 24 * 60 * 60));
                        endTime2 = Long.valueOf(endTime2.longValue() + (24 * mod * 60 * 60));
                    } else {
                        startTime2 = Long.valueOf(startTime2.longValue() + (mod * 24 * 60 * 60) + yushu);
                        endTime2 = Long.valueOf(endTime2.longValue() + (24 * mod * 60 * 60) + yushu);
                    }
                    MtkLog.d("ScheduleListItemInfoDialog", "startTime=" + startTime2 + " sstartTime==" + sstartTime + " endtime=" + endTime2 + "sendTime==" + sendTime);
                    if (startTime2.longValue() <= sstartTime.longValue() && endTime2.longValue() > sstartTime.longValue()) {
                        replacetItems.add(sItem);
                    } else if (startTime2.longValue() >= sstartTime.longValue() && endTime2.longValue() <= sendTime.longValue()) {
                        replacetItems.add(sItem);
                    } else if (startTime2.longValue() < sendTime.longValue() && endTime2.longValue() >= sendTime.longValue()) {
                        replacetItems.add(sItem);
                    }
                } else if (sItem.getRepeatMode() == 2) {
                    long mod2 = (startTime2.longValue() - sstartTime.longValue()) / 86400;
                    long yushu2 = (startTime2.longValue() - sstartTime.longValue()) % 86400;
                    if (item.getRepeatMode() != 2 && mod2 < 0) {
                        return replacetItems;
                    }
                    if (yushu2 < 60) {
                        startTime = Long.valueOf((startTime2.longValue() - (((mod2 * 24) * 60) * 60)) - yushu2);
                        endTime = Long.valueOf((endTime2.longValue() - (((24 * mod2) * 60) * 60)) - yushu2);
                    } else {
                        startTime = Long.valueOf(startTime2.longValue() - (((mod2 * 24) * 60) * 60));
                        endTime = Long.valueOf(endTime2.longValue() - (((24 * mod2) * 60) * 60));
                    }
                    if (startTime2.longValue() <= sstartTime.longValue() && endTime2.longValue() > sstartTime.longValue()) {
                        replacetItems.add(sItem);
                    } else if (startTime2.longValue() >= sstartTime.longValue() && endTime2.longValue() <= sendTime.longValue()) {
                        replacetItems.add(sItem);
                    } else if (startTime2.longValue() < sendTime.longValue() && endTime2.longValue() >= sendTime.longValue()) {
                        replacetItems.add(sItem);
                    }
                } else {
                    continue;
                }
            }
        }
        return replacetItems;
    }

    /* access modifiers changed from: private */
    public void dumpValuesInfo() {
        MtkLog.d("focusName:", getCurrentFocus().getClass().getSimpleName());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x008c, code lost:
        if ((r0 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) == false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0090, code lost:
        if (r5.isTTSKey == false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0092, code lost:
        ((com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r0).onKeyRight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x009a, code lost:
        if ((r0 instanceof com.mediatek.wwtv.setting.view.OptionView) == false) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x009c, code lost:
        hiddenWeekList(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a0, code lost:
        com.mediatek.wwtv.setting.util.Util.showDLog("optionView instanceof RespondedKeyEvent: false");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00a5, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a8, code lost:
        if ((r0 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) == false) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00ac, code lost:
        if (r5.isTTSKey == false) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ae, code lost:
        ((com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r0).onKeyLeft();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b6, code lost:
        if ((r0 instanceof com.mediatek.wwtv.setting.view.OptionView) == false) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00b8, code lost:
        hiddenWeekList(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00bc, code lost:
        com.mediatek.wwtv.setting.util.Util.showDLog("optionView instanceof RespondedKeyEvent: false");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00c1, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyUp(int r6, android.view.KeyEvent r7) {
        /*
            r5 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "keyCode UP:"
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            com.mediatek.wwtv.setting.util.Util.showDLog(r0)
            java.lang.String r0 = "ScheduleListItemInfoDialog"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onKeyUp  = "
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            android.widget.ListView r0 = r5.mDataItemList
            r1 = 0
            if (r0 != 0) goto L_0x0030
            return r1
        L_0x0030:
            android.widget.ListView r0 = r5.mDataItemList
            boolean r0 = r0.hasFocus()
            r2 = 1
            if (r0 == 0) goto L_0x00d9
            android.widget.ListView r0 = r5.mDataItemList
            int r1 = r5.itemPosition
            android.view.View r0 = r0.getChildAt(r1)
            boolean r1 = r5.isTTSKey
            if (r1 != 0) goto L_0x007b
            android.widget.ListView r1 = r5.mDataItemList
            android.widget.ListAdapter r1 = r1.getAdapter()
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter r1 = (com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter) r1
            android.widget.ListView r3 = r5.mDataItemList
            int r3 = r3.getSelectedItemPosition()
            r1.setSelectPos(r3)
            java.lang.String r1 = "ScheduleListItemInfoDialog"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "mDataItemList.getSelectedItemPosition() = "
            r3.append(r4)
            android.widget.ListView r4 = r5.mDataItemList
            int r4 = r4.getSelectedItemPosition()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            android.widget.ListView r1 = r5.mDataItemList
            int r1 = r1.getSelectedItemPosition()
            r5.showHightLight(r1)
        L_0x007b:
            r1 = 165(0xa5, float:2.31E-43)
            if (r6 == r1) goto L_0x00d4
            switch(r6) {
                case 7: goto L_0x00c2;
                case 8: goto L_0x00c2;
                case 9: goto L_0x00c2;
                case 10: goto L_0x00c2;
                case 11: goto L_0x00c2;
                case 12: goto L_0x00c2;
                case 13: goto L_0x00c2;
                case 14: goto L_0x00c2;
                case 15: goto L_0x00c2;
                case 16: goto L_0x00c2;
                default: goto L_0x0082;
            }
        L_0x0082:
            switch(r6) {
                case 21: goto L_0x00a6;
                case 22: goto L_0x008a;
                case 23: goto L_0x0089;
                default: goto L_0x0085;
            }
        L_0x0085:
            switch(r6) {
                case 183: goto L_0x00a6;
                case 184: goto L_0x008a;
                default: goto L_0x0088;
            }
        L_0x0088:
            goto L_0x00d7
        L_0x0089:
            goto L_0x00d7
        L_0x008a:
            boolean r1 = r0 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent
            if (r1 == 0) goto L_0x00a0
            boolean r1 = r5.isTTSKey
            if (r1 == 0) goto L_0x00a0
            r1 = r0
            com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent r1 = (com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r1
            r1.onKeyRight()
            boolean r1 = r0 instanceof com.mediatek.wwtv.setting.view.OptionView
            if (r1 == 0) goto L_0x00a5
            r5.hiddenWeekList(r0)
            goto L_0x00a5
        L_0x00a0:
            java.lang.String r1 = "optionView instanceof RespondedKeyEvent: false"
            com.mediatek.wwtv.setting.util.Util.showDLog(r1)
        L_0x00a5:
            return r2
        L_0x00a6:
            boolean r1 = r0 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent
            if (r1 == 0) goto L_0x00bc
            boolean r1 = r5.isTTSKey
            if (r1 == 0) goto L_0x00bc
            r1 = r0
            com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent r1 = (com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent) r1
            r1.onKeyLeft()
            boolean r1 = r0 instanceof com.mediatek.wwtv.setting.view.OptionView
            if (r1 == 0) goto L_0x00c1
            r5.hiddenWeekList(r0)
            goto L_0x00c1
        L_0x00bc:
            java.lang.String r1 = "optionView instanceof RespondedKeyEvent: false"
            com.mediatek.wwtv.setting.util.Util.showDLog(r1)
        L_0x00c1:
            return r2
        L_0x00c2:
            boolean r1 = r0 instanceof com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent
            if (r1 == 0) goto L_0x00ce
            boolean r1 = r5.isTTSKey
            if (r1 == 0) goto L_0x00ce
            r5.handleKeyboardInput(r0, r6)
            goto L_0x00d7
        L_0x00ce:
            java.lang.String r1 = "optionView instanceof RespondedKeyEvent: false"
            com.mediatek.wwtv.setting.util.Util.showDLog(r1)
            goto L_0x00d7
        L_0x00d4:
            r5.dumpValuesInfo()
        L_0x00d7:
            goto L_0x0224
        L_0x00d9:
            r0 = 20
            r3 = -1
            if (r6 != r0) goto L_0x0216
            android.widget.ListView r0 = r5.mDataItemList
            int r0 = r0.getSelectedItemPosition()
            android.widget.ListView r4 = r5.mDataItemList
            int r4 = r4.getCount()
            int r4 = r4 - r2
            r2 = 3
            if (r0 != r4) goto L_0x0142
            android.widget.Button r0 = r5.mBtn1
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x0142
            android.widget.Button r0 = r5.mBtn2
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x0142
            android.widget.Button r0 = r5.mBtn3
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x0142
            android.widget.GridView r0 = r5.mWeekList
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x010f
            goto L_0x0114
        L_0x010f:
            android.widget.Button r0 = r5.mBtn3
            r0.requestFocus()
        L_0x0114:
            android.widget.GridView r0 = r5.mWeekList
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x012f
            android.widget.GridView r0 = r5.mWeekList
            int r0 = r0.getSelectedItemPosition()
            if (r0 <= r2) goto L_0x012f
            boolean r0 = r5.weekflag
            if (r0 == 0) goto L_0x012f
            android.widget.Button r0 = r5.mBtn3
            r0.requestFocus()
            r5.weekflag = r1
        L_0x012f:
            r5.reSetFocusPath()
            r5.hiddenHightLight()
            android.widget.ListView r0 = r5.mDataItemList
            android.widget.ListAdapter r0 = r0.getAdapter()
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter r0 = (com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter) r0
            r0.setSelectPos(r3)
            goto L_0x0224
        L_0x0142:
            android.widget.ListView r0 = r5.mDataItemList
            int r0 = r0.getSelectedItemPosition()
            if (r0 != 0) goto L_0x0192
            android.widget.Button r0 = r5.mBtn1
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x0192
            android.widget.Button r0 = r5.mBtn2
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x0192
            android.widget.Button r0 = r5.mBtn3
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x0192
            android.widget.GridView r0 = r5.mWeekList
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0224
            android.widget.GridView r0 = r5.mWeekList
            boolean r0 = r0.hasFocus()
            if (r0 == 0) goto L_0x0224
            android.widget.GridView r0 = r5.mWeekList
            int r0 = r0.getSelectedItemPosition()
            if (r0 <= r2) goto L_0x0224
            android.widget.Button r0 = r5.mBtn3
            r0.requestFocus()
            r5.reSetFocusPath()
            r5.hiddenHightLight()
            android.widget.ListView r0 = r5.mDataItemList
            android.widget.ListAdapter r0 = r0.getAdapter()
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter r0 = (com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter) r0
            r0.setSelectPos(r3)
            goto L_0x0224
        L_0x0192:
            android.widget.Button r0 = r5.mBtn1
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x01ba
            android.widget.Button r0 = r5.mBtn2
            boolean r0 = r0.hasFocus()
            if (r0 != 0) goto L_0x01ba
            android.widget.Button r0 = r5.mBtn3
            boolean r0 = r0.hasFocus()
            if (r0 == 0) goto L_0x01ab
            goto L_0x01ba
        L_0x01ab:
            r5.hiddenHightLight()
            android.widget.ListView r0 = r5.mDataItemList
            android.widget.ListAdapter r0 = r0.getAdapter()
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter r0 = (com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter) r0
            r0.setSelectPos(r3)
            goto L_0x0224
        L_0x01ba:
            com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog$BtnTypeEnum r0 = r5.getBtnListType()
            com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog$BtnTypeEnum r2 = com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.BtnTypeEnum.EDIT_DELETE_CANCEL
            if (r0 == r2) goto L_0x01ee
            android.widget.ListView r0 = r5.mDataItemList
            android.view.View r0 = r0.getChildAt(r1)
            if (r0 == 0) goto L_0x0224
            android.widget.GridView r0 = r5.mWeekList
            r0.setSelection(r3)
            android.widget.ListView r0 = r5.mDataItemList
            r0.requestFocus()
            android.widget.ListView r0 = r5.mDataItemList
            r0.setSelection(r1)
            android.widget.ListView r0 = r5.mDataItemList
            android.widget.ListAdapter r0 = r0.getAdapter()
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter r0 = (com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter) r0
            r0.setSelectPos(r1)
            android.widget.ListView r0 = r5.mDataItemList
            int r0 = r0.getSelectedItemPosition()
            r5.showHightLight(r0)
            goto L_0x0224
        L_0x01ee:
            android.widget.Button r0 = r5.mBtn1
            boolean r0 = r0.hasFocus()
            if (r0 == 0) goto L_0x01fb
            android.widget.Button r0 = r5.mBtn1
            r0.requestFocus()
        L_0x01fb:
            android.widget.Button r0 = r5.mBtn2
            boolean r0 = r0.hasFocus()
            if (r0 == 0) goto L_0x0208
            android.widget.Button r0 = r5.mBtn2
            r0.requestFocus()
        L_0x0208:
            android.widget.Button r0 = r5.mBtn3
            boolean r0 = r0.hasFocus()
            if (r0 == 0) goto L_0x0224
            android.widget.Button r0 = r5.mBtn3
            r0.requestFocus()
            goto L_0x0224
        L_0x0216:
            r5.hiddenHightLight()
            android.widget.ListView r0 = r5.mDataItemList
            android.widget.ListAdapter r0 = r0.getAdapter()
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter r0 = (com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter) r0
            r0.setSelectPos(r3)
        L_0x0224:
            r5.updateDissmissTimer()
            boolean r0 = super.onKeyUp(r6, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.onKeyUp(int, android.view.KeyEvent):boolean");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int keyCode2 = KeyMap.getKeyCode(keyCode, event);
        MtkLog.d("ScheduleListItemInfoDialog", "onKeyDown = " + keyCode2);
        if (keyCode2 == 4) {
            ScheduleListDialog scheduleListDialog = new ScheduleListDialog(this.mContext, this.position);
            scheduleListDialog.setEpgFlag(this.epgFlag);
            scheduleListDialog.getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
            scheduleListDialog.show();
            dismiss();
            Util.showDLog("ScheduleListItemInfoDialog", "KEYCODE_BACK");
        } else if (keyCode2 == 23) {
            if (this.mWeekList.hasFocus()) {
                Util.showDLog("mWeekList.hasFocus()");
                ((CheckBox) this.mWeekList.getFocusedChild()).performClick();
            }
            if (this.mDataItemList.hasFocus()) {
                Util.showDLog("mDataItemList.hasFocus()");
                if (this.mDataItemList.getFocusedChild() != null) {
                    this.mDataItemList.getFocusedChild().performClick();
                }
            }
        } else if (keyCode2 != 229) {
            switch (keyCode2) {
                case 19:
                    if (this.mDataItemList.hasFocus() && this.mDataItemList.getSelectedItemPosition() == 0) {
                        this.mBtn3.requestFocus();
                        return true;
                    }
                case 20:
                    if (this.mWeekList.getVisibility() == 0 && this.mWeekList.getSelectedItemPosition() > 3) {
                        if (this.mBtn1.hasFocus() || this.mBtn2.hasFocus() || this.mBtn3.hasFocus()) {
                            return false;
                        }
                        this.weekflag = true;
                        return true;
                    }
                    break;
                default:
                    switch (keyCode2) {
                        case KeyMap.KEYCODE_MTKIR_INFO /*165*/:
                            dumpValuesInfo();
                            break;
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
            }
        } else if (!DestroyApp.isCurEPGActivity()) {
            CommonIntegration.getInstance().channelPre();
        }
        return super.onKeyDown(keyCode2, event);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position2, long id) {
        Util.showDLog("onItemClick.................." + this.mDataItemList);
    }

    public void setCallback(StateScheduleListCallback callback) {
        this.mCallback = callback;
    }

    public StateScheduleListCallback<?> getmCallback() {
        return this.mCallback;
    }

    private void initBtnListener(BtnTypeEnum type) {
        switch (type) {
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0040, code lost:
        r3 = (com.mediatek.wwtv.setting.view.DateTimeInputView) r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean handleKeyboardInput(android.view.View r6, int r7) {
        /*
            r5 = this;
            r0 = 0
            java.lang.String r1 = "0"
            switch(r7) {
                case 7: goto L_0x0034;
                case 8: goto L_0x002f;
                case 9: goto L_0x002a;
                case 10: goto L_0x0025;
                case 11: goto L_0x0020;
                case 12: goto L_0x001b;
                case 13: goto L_0x0016;
                case 14: goto L_0x0011;
                case 15: goto L_0x000c;
                case 16: goto L_0x0007;
                default: goto L_0x0006;
            }
        L_0x0006:
            goto L_0x0039
        L_0x0007:
            r0 = 57
            java.lang.String r1 = "9"
            goto L_0x0039
        L_0x000c:
            r0 = 56
            java.lang.String r1 = "8"
            goto L_0x0039
        L_0x0011:
            r0 = 55
            java.lang.String r1 = "7"
            goto L_0x0039
        L_0x0016:
            r0 = 54
            java.lang.String r1 = "6"
            goto L_0x0039
        L_0x001b:
            r0 = 53
            java.lang.String r1 = "5"
            goto L_0x0039
        L_0x0020:
            r0 = 52
            java.lang.String r1 = "4"
            goto L_0x0039
        L_0x0025:
            r0 = 51
            java.lang.String r1 = "3"
            goto L_0x0039
        L_0x002a:
            r0 = 50
            java.lang.String r1 = "2"
            goto L_0x0039
        L_0x002f:
            r0 = 49
            java.lang.String r1 = "1"
            goto L_0x0039
        L_0x0034:
            r0 = 48
            java.lang.String r1 = "0"
        L_0x0039:
            r2 = 0
            if (r6 == 0) goto L_0x0053
            boolean r3 = r6 instanceof com.mediatek.wwtv.setting.view.DateTimeInputView
            if (r3 == 0) goto L_0x0053
            r3 = r6
            com.mediatek.wwtv.setting.view.DateTimeInputView r3 = (com.mediatek.wwtv.setting.view.DateTimeInputView) r3
            com.mediatek.wwtv.setting.view.DateTimeView r4 = r3.getmDateTimeView()
            if (r4 == 0) goto L_0x0052
            com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter$DataItem r2 = r3.getmDataItem()
            r4.input(r0, r2)
            r2 = 1
            return r2
        L_0x0052:
            return r2
        L_0x0053:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.widget.view.ScheduleListItemInfoDialog.handleKeyboardInput(android.view.View, int):boolean");
    }

    public BtnTypeEnum getBtnListType() {
        return this.btnListType;
    }

    public void setBtnListType(BtnTypeEnum mType) {
        this.btnListType = mType;
    }

    /* access modifiers changed from: private */
    public int checkItem(MtkTvBookingBase item) {
        Long startTime = Long.valueOf(item.getRecordStartTime() * 1000);
        MtkLog.d("ScheduleListItemInfoDialog", "startTime = " + startTime + "  currentTime = " + System.currentTimeMillis());
        Long valueOf = Long.valueOf(startTime.longValue() + item.getRecordDuration());
        if (startTime.longValue() <= System.currentTimeMillis()) {
            return 1;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public MtkTvBookingBase prepareItem() {
        int diff;
        long startRecordEndTime;
        MtkTvBookingBase item = new MtkTvBookingBase();
        for (int i = 0; i < this.mDataItemList.getCount(); i++) {
            View optionViewL = this.mDataItemList.getChildAt(i);
            switch (i) {
                case 0:
                    item.setSourceType(((OptionView) optionViewL).getValue());
                    break;
                case 1:
                    try {
                        int channelIndex = ((OptionView) optionViewL).getValue();
                        String valueOf = String.valueOf(TIFChannelManager.getInstance(this.mContext).getAllDTVTIFChannels().get(channelIndex).mDisplayName);
                        String channlString = TIFChannelManager.getInstance(this.mContext).getAllDTVTIFChannels().get(channelIndex).mDisplayNumber;
                        int channelID = TIFChannelManager.getInstance(this.mContext).getAllDTVTIFChannels().get(channelIndex).mMtkTvChannelInfo.getChannelId();
                        item.setEventTitle("" + channlString);
                        item.setChannelId(channelID);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                case 2:
                    this.startDate = ((DateTimeInputView) optionViewL).getmDateTimeView().getmDate();
                    break;
                case 3:
                    String startTime = ((DateTimeInputView) optionViewL).getmDateTimeView().getmDate();
                    if ("0".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
                        this.startRecordTime = Util.strToTimeEx(this.startDate + " " + startTime, 0);
                        MtkTvTimeFormatBase timeBaseFrom = new MtkTvTimeFormatBase();
                        timeBaseFrom.setByUtc(this.startRecordTime);
                        MtkTvTimeFormatBase timeBaseTo = new MtkTvTimeFormatBase();
                        MtkTvTimeBase time = new MtkTvTimeBase();
                        timeBaseFrom.print("Jiayang.li --brdcst-local");
                        time.convertTime(2, timeBaseFrom, timeBaseTo);
                        timeBaseTo.print("Jiayang.li--sys-utc");
                        this.startRecordTime = timeBaseTo.toSeconds();
                    } else {
                        this.startRecordTime = Util.strToTime(this.startDate + " " + startTime, 0);
                    }
                    item.setRecordStartTime(this.startRecordTime);
                    MtkLog.d("{ScheduleListTimeInfo}", "setRecordStartTime:" + this.startRecordTime);
                    break;
                case 4:
                    String endTime = ((DateTimeInputView) optionViewL).getmDateTimeView().getmDate();
                    if ("0".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
                        long startRecordEndTime2 = Util.strToTimeEx(this.startDate + " " + endTime, 0);
                        MtkTvTimeFormatBase timeBaseFrom2 = new MtkTvTimeFormatBase();
                        timeBaseFrom2.setByUtc(startRecordEndTime2);
                        MtkTvTimeFormatBase timeBaseTo2 = new MtkTvTimeFormatBase();
                        new MtkTvTimeBase().convertTime(2, timeBaseFrom2, timeBaseTo2);
                        startRecordEndTime = timeBaseTo2.toSeconds();
                    } else {
                        startRecordEndTime = Util.strToTime(this.startDate + " " + endTime, 0);
                    }
                    long duration = startRecordEndTime - this.startRecordTime;
                    if (duration <= 0) {
                        duration += 86400;
                    }
                    MtkLog.d("{ScheduleListTimeInfo}", "setRecordDuration" + duration);
                    item.setRecordDuration(duration);
                    break;
                case 5:
                    int scheduleType = ((OptionView) optionViewL).getValue();
                    if (scheduleType == 0) {
                        scheduleType += 2;
                    }
                    item.setRecordMode(scheduleType);
                    break;
                case 6:
                    int value = ((OptionView) optionViewL).getValue();
                    break;
            }
        }
        int repeatType = ((OptionView) this.mDataItemList.getChildAt(6)).getValue();
        StringBuilder weekValue = new StringBuilder();
        if (repeatType == 1) {
            int weekdey = dayForWeek(this.startDate);
            int diff2 = 0;
            int diff22 = 7;
            int count = 0;
            for (int i2 = 6; i2 >= 0; i2--) {
                boolean selected = ((WeekListAdapter) this.mWeekList.getAdapter()).isChecked(i2).booleanValue();
                if (selected) {
                    if ((i2 + 1) - weekdey > 0) {
                        diff = (i2 + 1) - weekdey;
                    } else if ((i2 + 1) - weekdey < 0) {
                        diff = 7 - (weekdey - (i2 + 1));
                    } else {
                        diff = 0;
                    }
                    if (diff22 >= diff) {
                        diff22 = diff;
                    }
                    count += 1 << i2;
                    MtkLog.d("ScheduleListItemInfoDialog", "count+=" + count);
                    diff2 = diff;
                }
                weekValue.append(selected);
            }
            MtkLog.d("ScheduleListItemInfoDialog", "diff" + diff2 + "   diff2==" + diff22);
            this.startRecordTime = this.startRecordTime + ((long) (getday(Integer.parseInt(weekValue.toString(), 2)) * 24 * 60 * 60));
            item.setRecordStartTime(this.startRecordTime);
            item.setRepeatMode(Integer.parseInt(weekValue.toString(), 2));
            MtkLog.d("ScheduleListItemInfoDialog", "2 to 10 ==" + Integer.parseInt(weekValue.toString(), 2));
        } else if (repeatType == 2) {
            item.setRepeatMode(0);
        } else {
            item.setRepeatMode(128);
        }
        item.setTunerType(CommonIntegration.getInstance().getTunerMode());
        return item;
    }

    private int getday(int repeatcount) {
        int weekday = MtkTvTime.getInstance().getLocalTime().weekDay;
        MtkLog.d("ScheduleListItemInfoDialog", "weekday==" + weekday);
        int count = -6;
        for (int i = 6; i >= 0; i--) {
            if (((1 << i) & repeatcount) == (1 << i)) {
                if (i >= weekday) {
                    count = i - weekday;
                    MtkLog.d("ScheduleListItemInfoDialog", "count1==" + count);
                } else if (count < 0) {
                    count = i - weekday;
                    MtkLog.d("ScheduleListItemInfoDialog", "count2==" + count);
                }
            }
        }
        if (count < 0) {
            count += 7;
        }
        MtkLog.d("ScheduleListItemInfoDialog", "count==" + count);
        return count;
    }

    public int dayForWeek(String pTime) {
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(pTime);
            Calendar calendar = new GregorianCalendar();
            calendar.set(date.getYear(), date.getMonth(), date.getDay());
            return calendar.get(7);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public MtkTvBookingBase getScheItem() {
        return this.mScheItem;
    }

    public void setScheItem(MtkTvBookingBase mScheItem2) {
        this.mScheItem = mScheItem2;
    }

    /* access modifiers changed from: private */
    public void showDeleteConfirmDialog() {
        List<MtkTvBookingBase> items = new ArrayList<>();
        items.add(prepareItem());
        this.confirm = new SpecialConfirmDialog(this.mContext, items);
        this.confirm.setPositiveButton(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showDLog("showDeleteConfirmDialog().PositiveButton");
                StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).deleteItem(ScheduleListItemInfoDialog.this.getScheItem());
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        });
        this.confirm.setNegativeButton(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showDLog("showDeleteConfirmDialog().NegativeButton");
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        });
        this.confirm.setTitle(this.mContext.getResources().getString(R.string.pvr_schedulelist_delete_line1), this.mContext.getResources().getString(R.string.pvr_schedulelist_delete_line2));
        this.confirm.show();
    }

    /* access modifiers changed from: private */
    public void showAddConfirmDialog(final List<MtkTvBookingBase> items, final MtkTvBookingBase currenItem) {
        this.confirm = new SpecialConfirmDialog(this.mContext, items);
        this.confirm.setPositiveButton((View.OnClickListener) null);
        this.confirm.setNegativeButton((View.OnClickListener) null);
        this.confirm.setPositiveButton(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showDLog("showDeleteConfirmDialog().PositiveButton");
                for (MtkTvBookingBase item : items) {
                    int bookingid = item.getBookingId();
                    StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).deleteItem(item);
                    for (MtkTvBookingBase item1 : items) {
                        int bookingid1 = item1.getBookingId();
                        if (bookingid < bookingid1) {
                            item1.setBookingId(bookingid1 - 1);
                        }
                    }
                }
                StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).insertItem(currenItem);
                MtkLog.d("ScheduleListItemInfoDialog", "showAddConfirmDialog + currenItem  = " + currenItem.toString());
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        });
        this.confirm.setNegativeButton(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showDLog("showDeleteConfirmDialog().NegativeButton");
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        });
        this.confirm.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
                ScheduleListItemInfoDialog.this.handler.sendEmptyMessageDelayed(333, 300);
            }
        });
        this.confirm.setTitle(this.mContext.getResources().getString(R.string.pvr_schedulelist_replace_line1), this.mContext.getResources().getString(R.string.pvr_schedulelist_replace_line2));
        this.confirm.show();
    }

    /* access modifiers changed from: private */
    public void showReplaceConfirmDialog(final List<MtkTvBookingBase> items, final MtkTvBookingBase currenItem) {
        this.confirm = new SpecialConfirmDialog(this.mContext, items);
        this.confirm.setPositiveButton((View.OnClickListener) null);
        this.confirm.setNegativeButton((View.OnClickListener) null);
        this.confirm.setPositiveButton(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showDLog("showDeleteConfirmDialog().PositiveButton");
                StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).deleteItem(ScheduleListItemInfoDialog.this.getScheItem());
                StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).deleteItem(ScheduleListItemInfoDialog.this.getRemoveList(items, currenItem));
                for (MtkTvBookingBase item : items) {
                    int bookingid = item.getBookingId();
                    MtkLog.d("ScheduleListItemInfoDialog", "item 2 = " + item.toString());
                    StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).deleteItem(item);
                    for (MtkTvBookingBase item1 : items) {
                        int bookingid1 = item1.getBookingId();
                        if (bookingid < bookingid1) {
                            item1.setBookingId(bookingid1 - 1);
                        }
                    }
                }
                StateScheduleList.getInstance(ScheduleListItemInfoDialog.this.mContext).insertItem(currenItem);
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        });
        this.confirm.setNegativeButton(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showDLog("showDeleteConfirmDialog().NegativeButton");
                ScheduleListDialog scheduleListDialog = new ScheduleListDialog(ScheduleListItemInfoDialog.this.mContext, ScheduleListItemInfoDialog.this.position);
                scheduleListDialog.setEpgFlag(ScheduleListItemInfoDialog.this.epgFlag);
                scheduleListDialog.show();
                ScheduleListItemInfoDialog.this.dismiss();
            }
        });
        this.confirm.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
                ScheduleListItemInfoDialog.this.handler.sendEmptyMessageDelayed(333, 300);
            }
        });
        this.confirm.setTitle(this.mContext.getResources().getString(R.string.pvr_schedulelist_replace_line1), this.mContext.getResources().getString(R.string.pvr_schedulelist_replace_line2));
        this.confirm.show();
    }

    public class WeekListAdapter extends BaseAdapter {
        private ViewGroup mGroup;
        private final List<Boolean> weekList = new ArrayList();

        public WeekListAdapter(HashMap<String, Boolean> list) {
            boolean z;
            for (int i = 0; i < ScheduleListItemInfoDialog.this.WEEK_ARRAY.length; i++) {
                List<Boolean> list2 = this.weekList;
                if (list.get(ScheduleListItemInfoDialog.this.WEEK_ARRAY[i]) == null) {
                    z = false;
                } else {
                    z = list.get(ScheduleListItemInfoDialog.this.WEEK_ARRAY[i]).booleanValue();
                }
                list2.add(Boolean.valueOf(z));
            }
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = ScheduleListItemInfoDialog.this.mInflater.inflate(R.layout.pvr_tshift_schedule_list_item_week, (ViewGroup) null);
                this.mGroup = parent;
            } else {
                view = convertView;
            }
            if (view.isFocused()) {
                view.setBackgroundResource(R.drawable.top_focus_img);
            } else {
                view.setBackgroundResource(0);
            }
            setItemValue(view, ScheduleListItemInfoDialog.this.WEEK_ARRAY[position], getItem(position));
            return view;
        }

        private void setItemValue(View view, String name, Boolean item) {
            CheckBox tmp = (CheckBox) view.findViewById(R.id.schedule_list_item_week_item_cb);
            tmp.setChecked(item.booleanValue());
            tmp.setText(name);
        }

        public final int getCount() {
            return this.weekList.size();
        }

        public final Boolean getItem(int position) {
            return this.weekList.get(position);
        }

        public final Boolean isChecked(int position) {
            View view = this.mGroup.getChildAt(position).findViewById(R.id.schedule_list_item_week_item_cb);
            if (view instanceof CheckBox) {
                return Boolean.valueOf(((CheckBox) view).isChecked());
            }
            return false;
        }

        public final long getItemId(int position) {
            return (long) position;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long id) {
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void updateDissmissTimer() {
    }

    /* access modifiers changed from: private */
    public void showHightLight(int id) {
        if (id == 1) {
            ((OptionView) this.mDataItemList.getChildAt(id)).setRightImageSource(true);
            ((OptionView) this.mDataItemList.getChildAt(0)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(5)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(6)).setRightImageSource(false);
            hiddenYellow();
        } else if (id == 5) {
            ((OptionView) this.mDataItemList.getChildAt(id)).setRightImageSource(true);
            ((OptionView) this.mDataItemList.getChildAt(0)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(1)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(6)).setRightImageSource(false);
            hiddenYellow();
        } else if (id == 6) {
            ((OptionView) this.mDataItemList.getChildAt(id)).setRightImageSource(true);
            ((OptionView) this.mDataItemList.getChildAt(0)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(1)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(5)).setRightImageSource(false);
            hiddenYellow();
        } else if (id == 0) {
            ((OptionView) this.mDataItemList.getChildAt(id)).setRightImageSource(true);
            ((OptionView) this.mDataItemList.getChildAt(1)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(5)).setRightImageSource(false);
            ((OptionView) this.mDataItemList.getChildAt(6)).setRightImageSource(false);
            hiddenYellow();
        } else {
            hiddenHightLight();
            ((DateTimeInputView) this.mDataItemList.getChildAt(id)).setFlag();
            if (id == 2) {
                ((DateTimeInputView) this.mDataItemList.getChildAt(3)).setWhiteColor();
                ((DateTimeInputView) this.mDataItemList.getChildAt(4)).setWhiteColor();
            } else if (id == 3) {
                ((DateTimeInputView) this.mDataItemList.getChildAt(2)).setWhiteColor();
                ((DateTimeInputView) this.mDataItemList.getChildAt(4)).setWhiteColor();
            } else if (id == 4) {
                ((DateTimeInputView) this.mDataItemList.getChildAt(2)).setWhiteColor();
                ((DateTimeInputView) this.mDataItemList.getChildAt(3)).setWhiteColor();
            }
        }
    }

    private void hiddenHightLight() {
        if (this.mDataItemList != null) {
            if (this.mDataItemList.getChildAt(5) != null) {
                ((OptionView) this.mDataItemList.getChildAt(5)).setRightImageSource(false);
            }
            if (this.mDataItemList.getChildAt(0) != null) {
                ((OptionView) this.mDataItemList.getChildAt(0)).setRightImageSource(false);
            }
            if (this.mDataItemList.getChildAt(1) != null) {
                ((OptionView) this.mDataItemList.getChildAt(1)).setRightImageSource(false);
            }
            if (this.mDataItemList.getChildAt(6) != null) {
                ((OptionView) this.mDataItemList.getChildAt(6)).setRightImageSource(false);
            }
        }
    }

    private void hiddenYellow() {
        if (this.mDataItemList.getChildAt(2) != null) {
            ((DateTimeInputView) this.mDataItemList.getChildAt(2)).setWhiteColor();
        }
        if (this.mDataItemList.getChildAt(3) != null) {
            ((DateTimeInputView) this.mDataItemList.getChildAt(3)).setWhiteColor();
        }
        if (this.mDataItemList.getChildAt(4) != null) {
            ((DateTimeInputView) this.mDataItemList.getChildAt(4)).setWhiteColor();
        }
    }

    public void srcTypeChange(int value) {
        if (this.mDataItemList != null) {
            OptionView view = (OptionView) this.mDataItemList.getChildAt(1);
            if (value == 1) {
                view.getValueView().setVisibility(4);
            } else {
                view.getValueView().setVisibility(0);
            }
        }
    }

    public boolean isEpgFlag() {
        return this.epgFlag;
    }

    public void setEpgFlag(boolean epgFlag2) {
        this.epgFlag = epgFlag2;
    }

    public void show() {
        if (DataSeparaterUtil.getInstance() == null || DataSeparaterUtil.getInstance().isSupportPvr()) {
            super.show();
        } else {
            MtkLog.d("ScheduleListItemInfoDialog", "isSupportPVR is false not  show dialog");
        }
    }

    public static ScheduleListItemInfoDialog getscheduleListItemInfoDialog(Context context) {
        return scheduleListItemInfoDialog;
    }
}
