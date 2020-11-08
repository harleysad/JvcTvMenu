package com.android.tv.menu;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class MenuView extends FrameLayout implements IMenuView {
    static final String TAG = MenuView.class.getSimpleName();
    private final LayoutInflater mLayoutInflater;
    private final MenuLayoutManager mLayoutManager;
    /* access modifiers changed from: private */
    public final List<MenuRowView> mMenuRowViews;
    private final List<MenuRow> mMenuRows;
    private int mShowReason;

    public MenuView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMenuRows = new ArrayList();
        this.mMenuRowViews = new ArrayList();
        this.mShowReason = 0;
        this.mLayoutInflater = LayoutInflater.from(context);
        setLayerType(2, (Paint) null);
        getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                String str = MenuView.TAG;
                Log.d(str, "Focus changed to " + oldFocus + newFocus);
                MenuRowView newParent = MenuView.this.getParentMenuRowView(newFocus);
                if (newParent != null) {
                    String str2 = MenuView.TAG;
                    Log.d(str2, "Focus changed to " + newParent);
                    newParent.setImportantForAccessibility(2);
                    MenuView.this.setSelectedPositionSmooth(MenuView.this.mMenuRowViews.indexOf(newParent));
                }
            }
        });
        this.mLayoutManager = new MenuLayoutManager(context, this);
    }

    public void setMenuRows(List<MenuRow> menuRows) {
        this.mMenuRows.clear();
        this.mMenuRows.addAll(menuRows);
        for (MenuRow row : menuRows) {
            MenuRowView view = createMenuRowView(row);
            this.mMenuRowViews.add(view);
            addView(view);
        }
        this.mLayoutManager.setMenuRowsAndViews(this.mMenuRows, this.mMenuRowViews);
    }

    private MenuRowView createMenuRowView(MenuRow row) {
        MenuRowView view = (MenuRowView) this.mLayoutInflater.inflate(row.getLayoutResId(), this, false);
        view.onBind(row);
        row.setMenuRowView(view);
        return view;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLayoutManager.layout(left, top, right, bottom);
    }

    public void onShow(int reason, String rowIdToSelect, final Runnable runnableAfterShow) {
        int position;
        String str = TAG;
        Log.d(str, "onShow(reason=" + reason + ", rowIdToSelect=" + rowIdToSelect + ")");
        this.mShowReason = reason;
        if (getVisibility() != 0) {
            initializeChildren();
            update(true);
            int position2 = getItemPosition(rowIdToSelect);
            if (position2 != -1) {
                this.mMenuRows.get(position2).isVisible();
            }
            setSelectedPosition(position2);
            setVisibility(0);
            SaveValue.saveWorldBooleanValue(getContext(), "is_menu_show", true, false);
            requestFocus();
            if (runnableAfterShow != null) {
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        MenuView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        runnableAfterShow.run();
                    }
                });
            }
            this.mLayoutManager.onMenuShow();
        } else if (rowIdToSelect != null && (position = getItemPosition(rowIdToSelect)) >= 0) {
            this.mMenuRowViews.get(position).initialize(reason);
            setSelectedPosition(position);
        }
    }

    public void onHide() {
        if (getVisibility() != 8) {
            getParentMenuRowView(getFocusedChild()).setImportantForAccessibility(1);
            this.mLayoutManager.onMenuHide();
            setVisibility(8);
            SaveValue.saveWorldBooleanValue(getContext(), "is_menu_show", false, false);
        }
    }

    public boolean isVisible() {
        return getVisibility() == 0;
    }

    public boolean update(boolean menuActive) {
        if (!menuActive) {
            return false;
        }
        for (MenuRow row : this.mMenuRows) {
            row.update();
        }
        this.mLayoutManager.onMenuRowUpdated();
        return true;
    }

    public boolean update(String rowId, boolean menuActive) {
        MenuRow row;
        if (!menuActive || (row = getMenuRow(rowId)) == null) {
            return false;
        }
        row.update();
        this.mLayoutManager.onMenuRowUpdated();
        return true;
    }

    public void updateLanguage() {
        for (MenuRowView rowView : this.mMenuRowViews) {
            rowView.updateLanguage();
        }
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int selectedPosition = this.mLayoutManager.getSelectedPosition();
        if (selectedPosition < 0 || selectedPosition >= this.mMenuRowViews.size()) {
            return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
        }
        return this.mMenuRowViews.get(selectedPosition).requestFocus();
    }

    public void focusableViewAvailable(View v) {
        if (getVisibility() == 0) {
            super.focusableViewAvailable(v);
        }
    }

    private void setSelectedPosition(int position) {
        this.mLayoutManager.setSelectedPosition(position);
    }

    /* access modifiers changed from: private */
    public void setSelectedPositionSmooth(int position) {
        this.mLayoutManager.setSelectedPositionSmooth(position);
    }

    private void initializeChildren() {
        for (MenuRowView view : this.mMenuRowViews) {
            view.initialize(this.mShowReason);
        }
    }

    private MenuRow getMenuRow(String rowId) {
        for (MenuRow item : this.mMenuRows) {
            if (rowId.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    private int getItemPosition(String rowIdToSelect) {
        if (rowIdToSelect == null) {
            return -1;
        }
        int position = 0;
        for (MenuRow item : this.mMenuRows) {
            if (rowIdToSelect.equals(item.getId())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public View focusSearch(View focused, int direction) {
        if (direction == 33) {
            View newView = super.focusSearch(focused, direction);
            MenuRowView oldfocusedParent = getParentMenuRowView(focused);
            MenuRowView newFocusedParent = getParentMenuRowView(newView);
            int selectedPosition = this.mLayoutManager.getSelectedPosition();
            if (newFocusedParent != oldfocusedParent) {
                for (int i = selectedPosition - 1; i >= 0; i--) {
                    MenuRowView view = this.mMenuRowViews.get(i);
                    if (view.getVisibility() == 0) {
                        return view;
                    }
                }
            }
            return newView;
        } else if (direction != 130) {
            return super.focusSearch(focused, direction);
        } else {
            View newView2 = super.focusSearch(focused, direction);
            MenuRowView oldfocusedParent2 = getParentMenuRowView(focused);
            MenuRowView newFocusedParent2 = getParentMenuRowView(newView2);
            int selectedPosition2 = this.mLayoutManager.getSelectedPosition();
            if (newFocusedParent2 != oldfocusedParent2) {
                int count = this.mMenuRowViews.size();
                for (int i2 = selectedPosition2 + 1; i2 < count; i2++) {
                    MenuRowView view2 = this.mMenuRowViews.get(i2);
                    if (view2.getVisibility() == 0) {
                        return view2;
                    }
                }
            }
            return newView2;
        }
    }

    /* access modifiers changed from: private */
    public MenuRowView getParentMenuRowView(View view) {
        if (view == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        if (parent == this) {
            return (MenuRowView) view;
        }
        if (parent instanceof View) {
            return getParentMenuRowView((View) parent);
        }
        return null;
    }
}
