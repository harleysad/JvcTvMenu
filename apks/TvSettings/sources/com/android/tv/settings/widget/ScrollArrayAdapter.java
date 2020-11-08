package com.android.tv.settings.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ScrollArrayAdapter<T> extends ArrayAdapter<T> implements ScrollAdapter {
    private int mLayoutResource = -1;

    public ScrollArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ScrollArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.mLayoutResource = resource;
    }

    public ScrollArrayAdapter(Context context, int textViewResourceId, T[] objects) {
        super(context, textViewResourceId, objects);
    }

    public ScrollArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.mLayoutResource = resource;
    }

    public ScrollArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    public ScrollArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
        this.mLayoutResource = resource;
    }

    public View getScrapView(ViewGroup parent) {
        if (getCount() > 0) {
            return getView(0, (View) null, parent);
        }
        if (this.mLayoutResource != -1) {
            return ((LayoutInflater) parent.getContext().getSystemService("layout_inflater")).inflate(this.mLayoutResource, parent);
        }
        return new TextView(parent.getContext());
    }

    public void viewRemoved(View view) {
    }

    public ScrollAdapterBase getExpandAdapter() {
        return null;
    }
}
