package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.ui.OnRepeatedKeyInterceptListener;
import com.android.tv.ui.sidepanel.ActionItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.android.tv.ui.sidepanel.SideFragmentManager;
import com.android.tv.ui.sidepanel.SubMenuItem;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.widget.view.DatePicker;
import com.mediatek.wwtv.setting.widget.view.Picker;
import com.mediatek.wwtv.setting.widget.view.TimePicker;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.DateFormatUtil;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SubChannelScheduleBlockedFragment extends SideFragment {
    private static final String ARGS_CHANNEL_ID = "args_channel_id";
    private static final String ARGS_NAME = "args_name";
    private static final String TAG = "SubChannelScheduleBlockedFragment";
    Consumer<Picker> mBackStack = new Consumer() {
        public final void accept(Object obj) {
            SubChannelScheduleBlockedFragment.lambda$new$0(SubChannelScheduleBlockedFragment.this, (Picker) obj);
        }
    };
    /* access modifiers changed from: private */
    public int mChannelId;
    private final List<Item> mItems = new ArrayList();
    private String mName;
    /* access modifiers changed from: private */
    public final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            SubChannelScheduleBlockedFragment.this.notifyDataSetChanged();
        }
    };

    public static SubChannelScheduleBlockedFragment create(String name, int channelId) {
        SubChannelScheduleBlockedFragment fragment = new SubChannelScheduleBlockedFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_NAME, name);
        args.putInt(ARGS_CHANNEL_ID, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mName = getArguments().getString(ARGS_NAME);
        this.mChannelId = getArguments().getInt(ARGS_CHANNEL_ID);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        VerticalGridView listView = (VerticalGridView) view.findViewById(R.id.side_panel_list);
        listView.setOnKeyInterceptListener(new OnRepeatedKeyInterceptListener(listView) {
            public boolean onInterceptKeyEvent(KeyEvent event) {
                if (event.getAction() == 1) {
                    event.getKeyCode();
                }
                return super.onInterceptKeyEvent(event);
            }
        });
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        MtkLog.d(TAG, "onDestroyView");
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return this.mName;
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mItems.clear();
        MenuConfigManager instance = MenuConfigManager.getInstance(getActivity());
        int index = instance.getDefault(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE + this.mChannelId);
        MtkLog.d(TAG, "index: " + index);
        this.mItems.add(new OperationModeItem(getString(R.string.menu_parental_channel_schedule_block_operation_mode), getActivity().getResources().getStringArray(R.array.menu_parental_block_channel_schedule_operation_array)[index], this.mSideFragmentManager, index));
        SaveValue instance2 = SaveValue.getInstance(getActivity());
        String strStartDate = instance2.readStrValue(MenuConfigManager.TIME_START_DATE + this.mChannelId);
        MtkLog.d(TAG, "strStartDate: " + strStartDate);
        boolean z = true;
        if (strStartDate.length() != 10 || index == 1) {
            strStartDate = DateFormatUtil.getCurrentTime();
            SaveValue instance3 = SaveValue.getInstance(getActivity());
            instance3.saveStrValue(MenuConfigManager.TIME_START_DATE + this.mChannelId, strStartDate);
        }
        DateItem statingDate = new DateItem(getString(R.string.menu_parental_channel_schedule_start_date), strStartDate, 0);
        this.mItems.add(statingDate);
        statingDate.setEnabled(index == 2);
        SaveValue instance4 = SaveValue.getInstance(getActivity());
        String strStartTime = instance4.readStrValue(MenuConfigManager.TIME_START_TIME + this.mChannelId);
        if (strStartTime.length() == 1) {
            strStartTime = "00:00";
            SaveValue instance5 = SaveValue.getInstance(getActivity());
            instance5.saveStrValue(MenuConfigManager.TIME_START_TIME + this.mChannelId, strStartTime);
        }
        MtkLog.d(TAG, "strStartTime: " + strStartTime);
        TimeItem statingTime = new TimeItem(getString(R.string.menu_parental_channel_schedule_start_time), strStartTime, 0);
        this.mItems.add(statingTime);
        statingTime.setEnabled(index != 0);
        SaveValue instance6 = SaveValue.getInstance(getActivity());
        String strEndDate = instance6.readStrValue(MenuConfigManager.TIME_END_DATE + this.mChannelId);
        if (strEndDate.length() != 10 || index == 1) {
            strEndDate = DateFormatUtil.getCurrentTime();
            SaveValue instance7 = SaveValue.getInstance(getActivity());
            instance7.saveStrValue(MenuConfigManager.TIME_END_DATE + this.mChannelId, strEndDate);
        }
        DateItem endDate = new DateItem(getString(R.string.menu_parental_channel_schedule_end_date), strEndDate, 1);
        this.mItems.add(endDate);
        endDate.setEnabled(index == 2);
        SaveValue instance8 = SaveValue.getInstance(getActivity());
        String strEndTime = instance8.readStrValue(MenuConfigManager.TIME_END_TIME + this.mChannelId);
        if (strEndTime.length() == 1) {
            strEndTime = "00:00";
            SaveValue instance9 = SaveValue.getInstance(getActivity());
            instance9.saveStrValue(MenuConfigManager.TIME_END_TIME + this.mChannelId, strEndTime);
        }
        TimeItem endTime = new TimeItem(getString(R.string.menu_parental_channel_schedule_end_time), strEndTime, 1);
        this.mItems.add(endTime);
        if (index == 0) {
            z = false;
        }
        endTime.setEnabled(z);
        return this.mItems;
    }

    private class OperationModeItem extends SubMenuItem {
        private int index;

        public OperationModeItem(String title, String description, SideFragmentManager fragmentManager, int index2) {
            super(title, description, fragmentManager);
            this.index = index2;
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
        }

        /* access modifiers changed from: protected */
        public SideFragment getFragment() {
            SideFragment fragment = OperationModeFragment.create(this.index, SubChannelScheduleBlockedFragment.this.mChannelId);
            fragment.setListener(SubChannelScheduleBlockedFragment.this.mSideFragmentListener);
            return fragment;
        }
    }

    private class DateItem extends ActionItem {
        public static final int MODE_END = 1;
        public static final int MODE_STARTING = 0;
        private int mode;

        public DateItem(String title, String description, int mode2) {
            super(title, description);
            this.mode = mode2;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            String key;
            DatePicker datePicker = DatePicker.newInstance();
            datePicker.setResultListener(new Picker.ResultListener() {
                public void onCommitResult(String result) {
                    MtkLog.d(SubChannelScheduleBlockedFragment.TAG, "result: " + result);
                    String unused = DateItem.this.mDescription = result;
                    SubChannelScheduleBlockedFragment.this.notifyDataSetChanged();
                }
            });
            datePicker.setFocusDisabled(SubChannelScheduleBlockedFragment.this.mBackStack);
            Bundle bundle = new Bundle();
            FragmentTransaction ft = SubChannelScheduleBlockedFragment.this.getMainActivity().getFragmentManager().beginTransaction();
            if (this.mode == 0) {
                key = MenuConfigManager.TIME_START_DATE + SubChannelScheduleBlockedFragment.this.mChannelId;
            } else {
                key = MenuConfigManager.TIME_END_DATE + SubChannelScheduleBlockedFragment.this.mChannelId;
            }
            bundle.putCharSequence(PreferenceUtil.PARENT_PREFERENCE_ID, key);
            datePicker.setArguments(bundle);
            ft.replace(R.id.main_fragment_container, datePicker, key);
            ft.addToBackStack(key);
            ft.commit();
        }
    }

    private class TimeItem extends ActionItem {
        public static final int MODE_END = 1;
        public static final int MODE_STARTING = 0;
        private int mode;

        public TimeItem(String title, String description, int mode2) {
            super(title, description);
            this.mode = mode2;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            String key;
            TimePicker timePicker = TimePicker.newInstance();
            timePicker.setResultListener(new Picker.ResultListener() {
                public void onCommitResult(String result) {
                    MtkLog.d(SubChannelScheduleBlockedFragment.TAG, "result: " + result);
                    String unused = TimeItem.this.mDescription = result;
                    SubChannelScheduleBlockedFragment.this.notifyDataSetChanged();
                }
            });
            timePicker.setFocusDisabled(SubChannelScheduleBlockedFragment.this.mBackStack);
            Bundle bundle = new Bundle();
            FragmentTransaction ft = SubChannelScheduleBlockedFragment.this.getMainActivity().getFragmentManager().beginTransaction();
            if (this.mode == 0) {
                key = MenuConfigManager.TIME_START_TIME + SubChannelScheduleBlockedFragment.this.mChannelId;
            } else {
                key = MenuConfigManager.TIME_END_TIME + SubChannelScheduleBlockedFragment.this.mChannelId;
            }
            bundle.putCharSequence(PreferenceUtil.PARENT_PREFERENCE_ID, key);
            timePicker.setArguments(bundle);
            ft.replace(R.id.main_fragment_container, timePicker, key);
            ft.addToBackStack(key);
            ft.commit();
        }
    }

    public static /* synthetic */ void lambda$new$0(SubChannelScheduleBlockedFragment subChannelScheduleBlockedFragment, Picker picker) {
        try {
            subChannelScheduleBlockedFragment.getMainActivity().getFragmentManager().popBackStack();
        } catch (NullPointerException e) {
            MtkLog.e(TAG, "e :" + e.toString());
        }
    }
}
