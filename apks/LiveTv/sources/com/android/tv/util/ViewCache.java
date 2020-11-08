package com.android.tv.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class ViewCache {
    private static final SparseArray<ArrayList<View>> mViews = new SparseArray<>();
    private static ViewCache sViewCache;

    private ViewCache() {
    }

    public static ViewCache getInstance() {
        if (sViewCache == null) {
            sViewCache = new ViewCache();
        }
        return sViewCache;
    }

    public boolean isEmpty() {
        return mViews.size() == 0;
    }

    public void putView(int resId, View view) {
        ArrayList<View> views = mViews.get(resId);
        if (views == null) {
            views = new ArrayList<>();
            mViews.put(resId, views);
        }
        views.add(view);
    }

    public void putView(Context context, int resId, ViewGroup fakeParent, int num) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        ArrayList<View> views = mViews.get(resId);
        if (views == null) {
            views = new ArrayList<>();
            mViews.put(resId, views);
        }
        for (int i = 0; i < num; i++) {
            views.add(inflater.inflate(resId, fakeParent, false));
        }
    }

    public View getView(int resId) {
        ArrayList<View> views = mViews.get(resId);
        if (views == null || views.isEmpty()) {
            return null;
        }
        View view = views.remove(views.size() - 1);
        if (views.isEmpty()) {
            mViews.remove(resId);
        }
        return view;
    }

    public View getOrCreateView(LayoutInflater inflater, int resId, ViewGroup container) {
        View view = getView(resId);
        if (view == null) {
            return inflater.inflate(resId, container, false);
        }
        return view;
    }

    public void clear() {
        mViews.clear();
    }
}
