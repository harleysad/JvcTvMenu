package com.android.tv.menu;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.android.tv.menu.ItemListRowView;
import com.android.tv.menu.MenuOptionMain;
import com.mediatek.wwtv.tvcenter.R;
import java.util.List;

public abstract class OptionsRowAdapter extends ItemListRowView.ItemListAdapter<MenuAction> {
    private static final int[] BACKGROUND_IMAGES = {R.drawable.tv_options_item_bg_1, R.drawable.tv_options_item_bg_2, R.drawable.tv_options_item_bg_3, R.drawable.tv_options_item_bg_4, R.drawable.tv_options_item_bg_5, R.drawable.tv_options_item_bg_6, R.drawable.tv_options_item_bg_7, R.drawable.tv_options_item_bg_8};
    private static final String CUSTOM_ACTION_LABEL = "custom action";
    /* access modifiers changed from: private */
    public List<MenuAction> mActionList;
    private Context mContext;
    private final View.OnClickListener mMenuActionOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            final MenuAction action = (MenuAction) view.getTag();
            Log.d("OptionsRowAdapter", "onClick: child=" + action);
            view.post(new Runnable() {
                public void run() {
                    int actionNameResId = action.getActionNameResId();
                    OptionsRowAdapter.this.executeAction(action.getType());
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

    /* access modifiers changed from: protected */
    public abstract List<MenuAction> createActions();

    /* access modifiers changed from: protected */
    public abstract void executeAction(int i);

    /* access modifiers changed from: protected */
    public abstract boolean updateActions();

    public OptionsRowAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    public void update() {
        if (this.mActionList == null) {
            this.mActionList = createActions();
            updateActions();
            setItemList(this.mActionList);
        } else if (updateActions()) {
            setItemList(this.mActionList);
        }
    }

    /* access modifiers changed from: protected */
    public int getLayoutResId(int viewType) {
        return R.layout.menu_card_action;
    }

    /* access modifiers changed from: protected */
    public MenuAction getAction(int position) {
        return this.mActionList.get(position);
    }

    /* access modifiers changed from: protected */
    public void setAction(int position, MenuAction action) {
        this.mActionList.set(position, action);
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

    public void onBindViewHolder(ItemListRowView.ItemListAdapter.MyViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.itemView.setTag(getItemList().get(position));
        viewHolder.itemView.setOnClickListener(this.mMenuActionOnClickListener);
        viewHolder.itemView.setOnFocusChangeListener(this.mMenuActionOnFocusChangeListener);
        viewHolder.itemView.setBackgroundResource(BACKGROUND_IMAGES[position % BACKGROUND_IMAGES.length]);
    }

    public int getItemViewType(int position) {
        return this.mActionList.get(position).getType();
    }

    /* access modifiers changed from: protected */
    public void setOptionChangedListener(MenuAction action) {
        MenuOptionMain om = getMainActivity().getTvOptionsManager();
        if (om != null) {
            om.setOptionChangedListener(action.getType(), new MenuOptionMain.OptionChangedListener() {
                public void onOptionChanged(String newOption) {
                    OptionsRowAdapter.this.setItemList(OptionsRowAdapter.this.mActionList);
                }
            });
        }
    }
}
