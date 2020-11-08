package com.android.tv.menu;

import java.util.List;

public interface IMenuView {
    boolean isVisible();

    void onHide();

    void onShow(int i, String str, Runnable runnable);

    void setMenuRows(List<MenuRow> list);

    boolean update(String str, boolean z);

    boolean update(boolean z);

    void updateLanguage();
}
