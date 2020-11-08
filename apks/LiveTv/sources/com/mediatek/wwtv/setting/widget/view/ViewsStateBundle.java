package com.mediatek.wwtv.setting.widget.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

public abstract class ViewsStateBundle {
    public static final int SAVE_ALL_CHILD = 3;
    public static final int SAVE_LIMITED_CHILD = 2;
    public static final int SAVE_LIMITED_CHILD_DEFAULT_VALUE = 100;
    public static final int SAVE_NO_CHILD = 0;
    public static final int SAVE_VISIBLE_CHILD = 1;
    private final Bundle childStates = new Bundle();
    private int limitNumber;
    private int savePolicy;

    /* access modifiers changed from: protected */
    public abstract void saveVisibleViewsUnchecked();

    public ViewsStateBundle(int policy, int limit) {
        this.savePolicy = policy;
        this.limitNumber = limit;
    }

    public void clear() {
        this.childStates.clear();
    }

    public final Bundle getChildStates() {
        return this.childStates;
    }

    public final int getSavePolicy() {
        return this.savePolicy;
    }

    public final int getLimitNumber() {
        return this.limitNumber;
    }

    public final void setSavePolicy(int savePolicy2) {
        this.savePolicy = savePolicy2;
    }

    public final void setLimitNumber(int limitNumber2) {
        this.limitNumber = limitNumber2;
    }

    public final void loadView(View view, int id) {
        SparseArray<Parcelable> container = this.childStates.getSparseParcelableArray(getSaveStatesKey(id));
        if (container != null) {
            view.restoreHierarchyState(container);
        }
    }

    /* access modifiers changed from: protected */
    public final void saveViewUnchecked(View view, int id) {
        String key = getSaveStatesKey(id);
        SparseArray<Parcelable> container = new SparseArray<>();
        view.saveHierarchyState(container);
        this.childStates.putSparseParcelableArray(key, container);
    }

    public final void saveVisibleView(View view, int id) {
        if (this.savePolicy != 0) {
            saveViewUnchecked(view, id);
        }
    }

    public final void saveVisibleViews() {
        if (this.savePolicy != 0) {
            saveVisibleViewsUnchecked();
        }
    }

    public final void saveInvisibleView(View view, int id) {
        switch (this.savePolicy) {
            case 2:
                this.childStates.size();
                int i = this.limitNumber;
                break;
            case 3:
                break;
            default:
                return;
        }
        saveViewUnchecked(view, id);
    }

    static String getSaveStatesKey(int id) {
        return Integer.toString(id);
    }
}
