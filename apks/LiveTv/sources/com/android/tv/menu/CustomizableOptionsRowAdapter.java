package com.android.tv.menu;

import android.content.Context;
import com.android.tv.menu.customization.CustomAction;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomizableOptionsRowAdapter extends OptionsRowAdapter {
    private final List<CustomAction> mCustomActions;

    /* access modifiers changed from: protected */
    public abstract List<MenuAction> createBaseActions();

    /* access modifiers changed from: protected */
    public abstract void executeBaseAction(int i);

    public CustomizableOptionsRowAdapter(Context context, List<CustomAction> customActions) {
        super(context);
        this.mCustomActions = customActions;
    }

    /* access modifiers changed from: protected */
    public List<MenuAction> createActions() {
        List<MenuAction> actions = new ArrayList<>(createBaseActions());
        if (this.mCustomActions != null) {
            int position = 0;
            for (int i = 0; i < this.mCustomActions.size(); i++) {
                CustomAction customAction = this.mCustomActions.get(i);
                MenuAction action = new MenuAction(customAction.getTitle(), -(i + 1), customAction.getIconDrawable());
                if (customAction.isFront()) {
                    actions.add(position, action);
                    position++;
                } else {
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    /* access modifiers changed from: protected */
    public void executeAction(int type) {
        if (type < 0) {
            getMainActivity().startActivitySafe(this.mCustomActions.get(-(type + 1)).getIntent());
            return;
        }
        executeBaseAction(type);
    }

    /* access modifiers changed from: protected */
    public List<CustomAction> getCustomActions() {
        return this.mCustomActions;
    }
}
