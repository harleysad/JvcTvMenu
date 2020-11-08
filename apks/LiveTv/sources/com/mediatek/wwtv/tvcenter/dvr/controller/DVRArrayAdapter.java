package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DVRArrayAdapter<T> extends BaseAdapter implements Filterable {
    private int currenSelect = 0;
    private Context mContext;
    private int mDropDownResource;
    private int mFieldId = 0;
    private DVRArrayAdapter<T>.ArrayFilter mFilter;
    private LayoutInflater mInflater;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private boolean mNotifyOnChange = true;
    /* access modifiers changed from: private */
    public List<T> mObjects;
    /* access modifiers changed from: private */
    public volatile ArrayList<T> mOriginalValues;
    private int mResource;
    private int subStartIndex = 0;

    public List<T> getmObjects() {
        return this.mObjects;
    }

    public void setmObjects(List<T> mObjects2) {
        this.mObjects = mObjects2;
    }

    public int getCurrenSelect() {
        return this.currenSelect;
    }

    public void setCurrenSelect(int currenSelect2) {
        this.currenSelect = currenSelect2;
    }

    public int getSubStartIndex() {
        return this.subStartIndex;
    }

    public void setSubStartIndex(int subStartIndex2) {
        this.subStartIndex = subStartIndex2;
    }

    public DVRArrayAdapter(Context context, int textViewResourceId) {
        init(context, textViewResourceId, 0, new ArrayList());
    }

    public DVRArrayAdapter(Context context, int resource, int textViewResourceId) {
        init(context, resource, textViewResourceId, new ArrayList());
    }

    public DVRArrayAdapter(Context context, int textViewResourceId, T[] objects) {
        init(context, textViewResourceId, 0, Arrays.asList(objects));
    }

    public DVRArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        init(context, resource, textViewResourceId, Arrays.asList(objects));
    }

    public DVRArrayAdapter(Context context, int textViewResourceId, List<T> objects, int subStartIndex2, int currenSelect2) {
        this.subStartIndex = subStartIndex2;
        this.currenSelect = currenSelect2;
        init(context, textViewResourceId, 0, objects);
    }

    public DVRArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        init(context, resource, textViewResourceId, objects);
    }

    public void add(T object) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.add(object);
            } else {
                this.mObjects.add(object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(Collection<? extends T> collection) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.addAll(collection);
            } else {
                this.mObjects.addAll(collection);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(T... items) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                Collections.addAll(this.mOriginalValues, items);
            } else {
                Collections.addAll(this.mObjects, items);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void insert(T object, int index) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.add(index, object);
            } else {
                this.mObjects.add(index, object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void remove(T object) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.remove(object);
            } else {
                this.mObjects.remove(object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.clear();
            } else {
                this.mObjects.clear();
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void sort(Comparator<? super T> comparator) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                Collections.sort(this.mOriginalValues, comparator);
            } else {
                Collections.sort(this.mObjects, comparator);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.mNotifyOnChange = notifyOnChange;
    }

    private void init(Context context, int resource, int textViewResourceId, List<T> objects) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mDropDownResource = resource;
        this.mResource = resource;
        this.mObjects = objects;
        this.mFieldId = R.id.record_item;
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getCount() {
        try {
            if (this.mObjects != null) {
                return this.mObjects.size();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public T getItem(int position) {
        try {
            if (this.mObjects != null) {
                return this.mObjects.get(position);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPosition(T item) {
        return this.mObjects.indexOf(item);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, this.mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        DVRArrayAdapter<T>.ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            LinearLayout unused = holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
            TextView unused2 = holder.textview = (TextView) convertView.findViewById(R.id.record_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DVRFiles item = (DVRFiles) getItem(position);
        if (item != null) {
            if (item instanceof CharSequence) {
                holder.textview.setText((CharSequence) item);
            } else {
                holder.textview.setText(String.format("[%2d] %s", new Object[]{Integer.valueOf(this.subStartIndex + position), item.getProgramName().replace("pvr", this.mContext.getResources().getString(R.string.add_schedule).toLowerCase())}));
            }
            if (item.isRecording || item.isPlaying) {
                if (item.isRecording) {
                    holder.textview.setCompoundDrawablesRelativeWithIntrinsicBounds(this.mContext.getResources().getDrawable(R.drawable.pvr_record_rec), (Drawable) null, (Drawable) null, (Drawable) null);
                }
                if (item.isPlaying) {
                    holder.textview.setCompoundDrawablesRelativeWithIntrinsicBounds(this.mContext.getResources().getDrawable(R.drawable.timeshift_play), (Drawable) null, (Drawable) null, (Drawable) null);
                }
            } else {
                holder.textview.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        }
        return convertView;
    }

    class ViewHolder {
        /* access modifiers changed from: private */
        public LinearLayout layout;
        /* access modifiers changed from: private */
        public TextView textview;

        ViewHolder() {
        }
    }

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, this.mDropDownResource);
    }

    public static DVRArrayAdapter<CharSequence> createFromResource(Context context, int textArrayResId, int textViewResId) {
        return new DVRArrayAdapter<>(context, textViewResId, (T[]) context.getResources().getTextArray(textArrayResId));
    }

    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new ArrayFilter();
        }
        return this.mFilter;
    }

    private class ArrayFilter extends Filter {
        private ArrayFilter() {
        }

        /* access modifiers changed from: protected */
        public Filter.FilterResults performFiltering(CharSequence prefix) {
            ArrayList<T> list;
            ArrayList<T> values;
            Filter.FilterResults results = new Filter.FilterResults();
            synchronized (DVRArrayAdapter.this.mLock) {
                if (DVRArrayAdapter.this.mOriginalValues == null) {
                    ArrayList unused = DVRArrayAdapter.this.mOriginalValues = new ArrayList(DVRArrayAdapter.this.mObjects);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (DVRArrayAdapter.this.mLock) {
                    list = new ArrayList<>(DVRArrayAdapter.this.mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                synchronized (DVRArrayAdapter.this.mLock) {
                    values = new ArrayList<>(DVRArrayAdapter.this.mOriginalValues);
                }
                int count = values.size();
                ArrayList<T> newValues = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    T value = values.get(i);
                    String valueText = value.toString().toLowerCase();
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        String[] words = valueText.split(" ");
                        int wordCount = words.length;
                        int k = 0;
                        while (true) {
                            if (k >= wordCount) {
                                break;
                            } else if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            } else {
                                k++;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        /* access modifiers changed from: protected */
        public void publishResults(CharSequence constraint, Filter.FilterResults results) {
            List unused = DVRArrayAdapter.this.mObjects = (List) results.values;
            if (results.count > 0) {
                DVRArrayAdapter.this.notifyDataSetChanged();
            } else {
                DVRArrayAdapter.this.notifyDataSetInvalidated();
            }
        }
    }
}
