package com.android.tv.menu;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.android.tv.menu.ItemListRowView;
import com.android.tv.menu.MenuOptionMain;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import java.util.ArrayList;
import java.util.List;

public class RecordRowAdapter extends ItemListRowView.ItemListAdapter<MenuAction> {
    private static final int[] BACKGROUND_IMAGES = {R.drawable.tv_options_item_bg_1, R.drawable.tv_options_item_bg_2, R.drawable.tv_options_item_bg_3, R.drawable.tv_options_item_bg_4, R.drawable.tv_options_item_bg_5, R.drawable.tv_options_item_bg_6, R.drawable.tv_options_item_bg_7, R.drawable.tv_options_item_bg_8};
    /* access modifiers changed from: private */
    public static final String TAG = RecordRowAdapter.class.getSimpleName();
    /* access modifiers changed from: private */
    public List<MenuAction> mActionList;
    private Context mContext;
    private final View.OnClickListener mMenuActionOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            final MenuAction action = (MenuAction) view.getTag();
            String access$000 = RecordRowAdapter.TAG;
            Log.d(access$000, "onClick: child=" + action);
            view.post(new Runnable() {
                public void run() {
                    int actionNameResId = action.getActionNameResId();
                    RecordRowAdapter.this.executeBaseAction(action.getType());
                }
            });
        }
    };
    private final View.OnFocusChangeListener mMenuActionOnFocusChangeListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                view.setElevation(2.0f);
                view.animate().scaleX(1.2f).scaleY(1.2f).translationZ(4.0f).start();
                return;
            }
            view.setElevation(0.0f);
            view.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0.0f).start();
        }
    };
    boolean pvr_support = false;
    boolean tshift_support = false;

    public RecordRowAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public List<MenuAction> createBaseActions() {
        List<MenuAction> actionList = new ArrayList<>();
        if (MarketRegionInfo.isFunctionSupport(36) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportTShift()) {
            this.tshift_support = true;
        }
        if (MarketRegionInfo.isFunctionSupport(43) && DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportPvr()) {
            this.pvr_support = true;
        }
        if (this.pvr_support) {
            actionList.add(MenuAction.PVR_START_ICON);
            setOptionChangedListener(MenuAction.PVR_START_ICON);
        }
        if (this.pvr_support) {
            actionList.add(MenuAction.DVR_LIST_ACTION);
            setOptionChangedListener(MenuAction.DVR_LIST_ACTION);
        }
        if (this.pvr_support || this.tshift_support) {
            actionList.add(MenuAction.DEVICE_INFO_ACTION);
            setOptionChangedListener(MenuAction.DEVICE_INFO_ACTION);
        }
        if (this.pvr_support) {
            actionList.add(MenuAction.SCHEDULE_LIST_ACTION);
            setOptionChangedListener(MenuAction.SCHEDULE_LIST_ACTION);
        }
        if (this.tshift_support) {
            actionList.add(MenuAction.TSHIFT_MODE_ACTION);
            setOptionChangedListener(MenuAction.TSHIFT_MODE_ACTION);
        }
        return actionList;
    }

    /* access modifiers changed from: protected */
    public boolean updateActions() {
        return updateScheduleItem() && updateTimeShift();
    }

    private boolean updateScheduleItem() {
        boolean is3rdTVSource = !CommonIntegration.getInstance().getCurChInfo().isAnalogService() && (CommonIntegration.getInstance().isCurrentSourceDTV() || CommonIntegration.getInstance().isCurrentSourceTv());
        String str = TAG;
        Log.d(str, "updateScheduleItem||is3rdTVSource =" + is3rdTVSource);
        MenuAction.setEnabled(MenuAction.SCHEDULE_LIST_ACTION, is3rdTVSource);
        if (!this.pvr_support || (!CommonIntegration.getInstance().isCurrentSourceDTV() && !CommonIntegration.getInstance().isCurrentSourceTv())) {
            int index = getActionIndex(MenuAction.PVR_START_ICON.getType());
            if (index >= 0) {
                removeAction(index);
                notifyItemRemoved(index);
            }
        } else if (getActionIndex(MenuAction.PVR_START_ICON.getType()) < 0) {
            addAction(getActionIndex(MenuAction.PVR_START_ICON.getType()) + 1, MenuAction.PVR_START_ICON);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void addAction(int position, MenuAction action) {
        this.mActionList.add(position, action);
    }

    /* access modifiers changed from: protected */
    public void removeAction(int position) {
        this.mActionList.remove(position);
    }

    /* access modifiers changed from: protected */
    public int getActionSize() {
        return this.mActionList.size();
    }

    /* access modifiers changed from: protected */
    public int getActionIndex(int type) {
        for (int index = 0; index < getActionSize(); index++) {
            if (this.mActionList.get(index).getType() == type) {
                return index;
            }
        }
        return -1;
    }

    private boolean updateTimeShift() {
        boolean is3rdTVSource = CommonIntegration.getInstance().is3rdTVSource();
        String str = TAG;
        Log.d(str, "updateTimeShift||is3rdTVSource =" + is3rdTVSource);
        return true;
    }

    public void update() {
        if (this.mActionList == null) {
            this.mActionList = createBaseActions();
            updateActions();
            setItemList(this.mActionList);
        } else if (updateActions()) {
            Log.d(TAG, "update||setItemList");
            setItemList(this.mActionList);
        }
    }

    /* access modifiers changed from: protected */
    public int getLayoutResId(int viewType) {
        return R.layout.menu_card_action;
    }

    public void onBindViewHolder(ItemListRowView.ItemListAdapter.MyViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.itemView.setTag(getItemList().get(position));
        viewHolder.itemView.setOnClickListener(this.mMenuActionOnClickListener);
        viewHolder.itemView.setOnFocusChangeListener(this.mMenuActionOnFocusChangeListener);
        viewHolder.itemView.setBackgroundResource(BACKGROUND_IMAGES[position % BACKGROUND_IMAGES.length]);
    }

    /* access modifiers changed from: protected */
    public void setOptionChangedListener(MenuAction action) {
        MenuOptionMain om = getMainActivity().getTvOptionsManager();
        if (om != null) {
            om.setOptionChangedListener(action.getType(), new MenuOptionMain.OptionChangedListener() {
                public void onOptionChanged(String newOption) {
                    RecordRowAdapter.this.setItemList(RecordRowAdapter.this.mActionList);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void executeBaseAction(int type) {
        String str = TAG;
        Log.d(str, "executeBaseAction: type=" + type);
        switch (type) {
            case 16:
                MenuAction.showRecordList();
                return;
            case 17:
                MenuAction.showTShiftMode(this.mContext);
                return;
            case 18:
                MenuAction.showScheduleList(this.mContext);
                return;
            case 19:
                MenuAction.showRecordDeviceInfo(this.mContext);
                return;
            case 20:
                MenuAction.showStartPvr(this.mContext);
                return;
            default:
                return;
        }
    }
}
