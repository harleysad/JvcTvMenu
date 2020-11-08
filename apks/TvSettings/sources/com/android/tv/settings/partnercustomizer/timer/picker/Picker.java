package com.android.tv.settings.partnercustomizer.timer.picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.v17.leanback.widget.BaseGridView;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Picker extends Fragment {
    private Interpolator mAccelerateInterpolator;
    private int mAlphaAnimDuration;
    /* access modifiers changed from: private */
    public boolean mClicked = false;
    /* access modifiers changed from: private */
    public List<VerticalGridView> mColumnViews;
    private ArrayList<PickerColumn> mColumns = new ArrayList<>();
    private Context mContext;
    private Interpolator mDecelerateInterpolator;
    private float mFocusedAlpha;
    private float mInvisibleColumnAlpha;
    /* access modifiers changed from: private */
    public boolean mKeyDown = false;
    /* access modifiers changed from: private */
    public List<String> mResult;
    /* access modifiers changed from: private */
    public ResultListener mResultListener;
    private float mUnfocusedAlpha;
    private float mVisibleColumnAlpha;

    public interface ResultListener {
        void onCommitResult(List<String> list);
    }

    /* access modifiers changed from: protected */
    public abstract ArrayList<PickerColumn> getColumns();

    /* access modifiers changed from: protected */
    public abstract String getSeparator();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        this.mFocusedAlpha = getFloat(R.dimen.list_item_selected_title_text_alpha);
        this.mUnfocusedAlpha = getFloat(R.dimen.list_item_unselected_text_alpha);
        this.mVisibleColumnAlpha = getFloat(R.dimen.picker_item_visible_column_item_alpha);
        this.mInvisibleColumnAlpha = getFloat(R.dimen.picker_item_invisible_column_item_alpha);
        this.mAlphaAnimDuration = this.mContext.getResources().getInteger(R.integer.dialog_animation_duration);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.5f);
        this.mAccelerateInterpolator = new AccelerateInterpolator(2.5f);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mColumns = getColumns();
        if (this.mColumns == null || this.mColumns.size() == 0) {
            return null;
        }
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.partner_picker, container, false);
        PickerLayout pickerView = (PickerLayout) rootView.findViewById(R.id.picker);
        pickerView.setChildFocusListener(this);
        this.mColumnViews = new ArrayList();
        this.mResult = new ArrayList();
        int totalCol = this.mColumns.size();
        for (int i = 0; i < totalCol; i++) {
            this.mResult.add(this.mColumns.get(i).getItems()[0]);
            VerticalGridView columnView = (VerticalGridView) inflater.inflate(R.layout.partner_picker_column, pickerView, false);
            columnView.setWindowAlignment(0);
            this.mColumnViews.add(columnView);
            columnView.setTag(Integer.valueOf(i));
            pickerView.addView(columnView);
            if (!(i == totalCol - 1 || getSeparator() == null)) {
                TextView separator = (TextView) inflater.inflate(R.layout.partner_picker_separator, pickerView, false);
                separator.setText(getSeparator());
                pickerView.addView(separator);
            }
        }
        initAdapters();
        this.mColumnViews.get(0).requestFocus();
        this.mClicked = false;
        this.mKeyDown = false;
        return rootView;
    }

    private void initAdapters() {
        int totalCol = this.mColumns.size();
        for (int i = 0; i < totalCol; i++) {
            VerticalGridView gridView = this.mColumnViews.get(i);
            gridView.setAdapter(new Adapter(i, Arrays.asList(this.mColumns.get(i).getItems())));
            gridView.setOnKeyInterceptListener(new BaseGridView.OnKeyInterceptListener() {
                public boolean onInterceptKeyEvent(KeyEvent event) {
                    int keyCode = event.getKeyCode();
                    if (!(keyCode == 23 || keyCode == 66)) {
                        if (keyCode != 183) {
                            switch (keyCode) {
                                case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                    ((VerticalGridView) Picker.this.mColumnViews.get(0)).requestFocus();
                                    break;
                                case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                    ((VerticalGridView) Picker.this.mColumnViews.get(1)).requestFocus();
                                    break;
                            }
                            return false;
                        } else if (Picker.this.mKeyDown) {
                            boolean unused = Picker.this.mKeyDown = false;
                            boolean unused2 = Picker.this.mClicked = true;
                            Picker.this.updateAllColumnsForClick(true);
                        }
                    }
                    if (event.getAction() == 0 && !Picker.this.mKeyDown) {
                        boolean unused3 = Picker.this.mKeyDown = true;
                        Picker.this.updateAllColumnsForClick(false);
                    }
                    return false;
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void updateAdapter(int index, PickerColumn pickerColumn) {
        final VerticalGridView gridView = this.mColumnViews.get(index);
        this.mColumns.set(index, pickerColumn);
        ((Adapter) gridView.getAdapter()).setItems(Arrays.asList(pickerColumn.getItems()));
        gridView.post(new Runnable() {
            public void run() {
                Picker.this.updateColumn(gridView, false, (ArrayList<Animator>) null);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void updateSelection(int columnIndex, int selectedIndex) {
        VerticalGridView columnView = this.mColumnViews.get(columnIndex);
        if (columnView != null) {
            columnView.setSelectedPosition(selectedIndex);
            this.mResult.set(columnIndex, this.mColumns.get(columnIndex).getItems()[selectedIndex]);
        }
    }

    public void setResultListener(ResultListener listener) {
        this.mResultListener = listener;
    }

    /* access modifiers changed from: private */
    public void updateAllColumnsForClick(boolean keyUp) {
        ArrayList<Animator> animList = new ArrayList<>();
        for (VerticalGridView column : this.mColumnViews) {
            int selected = column.getSelectedPosition();
            RecyclerView.LayoutManager manager = column.getLayoutManager();
            int size = manager.getChildCount();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < size) {
                    View item = manager.getChildAt(i2);
                    if (item != null) {
                        if (selected != column.getChildAdapterPosition(item)) {
                            View item2 = item;
                            if (!keyUp) {
                                setOrAnimateAlphaInternal(item2, true, this.mInvisibleColumnAlpha, -1.0f, animList, this.mDecelerateInterpolator);
                            }
                        } else if (keyUp) {
                            View view = item;
                            setOrAnimateAlphaInternal(item, true, this.mFocusedAlpha, this.mUnfocusedAlpha, animList, this.mAccelerateInterpolator);
                        } else {
                            setOrAnimateAlphaInternal(item, true, this.mUnfocusedAlpha, -1.0f, animList, this.mDecelerateInterpolator);
                        }
                    }
                    i = i2 + 1;
                }
            }
        }
        if (!animList.isEmpty()) {
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animList);
            if (this.mClicked) {
                animSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (Picker.this.mResultListener != null) {
                            Picker.this.mResultListener.onCommitResult(Picker.this.mResult);
                        }
                    }
                });
            }
            animSet.start();
        } else if (this.mClicked && this.mResultListener != null) {
            this.mResultListener.onCommitResult(this.mResult);
        }
    }

    public void childFocusChanged() {
        ArrayList<Animator> animList = new ArrayList<>();
        for (VerticalGridView column : this.mColumnViews) {
            updateColumn(column, column.hasFocus(), animList);
        }
        if (!animList.isEmpty()) {
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animList);
            animSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void updateColumn(VerticalGridView column, boolean animateAlpha, ArrayList<Animator> animList) {
        VerticalGridView verticalGridView = column;
        if (verticalGridView != null) {
            int selected = column.getSelectedPosition();
            boolean focused = column.hasFocus();
            ArrayList<Animator> localAnimList = animList;
            if (animateAlpha && localAnimList == null) {
                localAnimList = new ArrayList<>();
            }
            ArrayList<Animator> localAnimList2 = localAnimList;
            RecyclerView.LayoutManager manager = column.getLayoutManager();
            int size = manager.getChildCount();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= size) {
                    break;
                }
                View item = manager.getChildAt(i2);
                if (item != null) {
                    setOrAnimateAlpha(item, selected == verticalGridView.getChildAdapterPosition(item), focused, animateAlpha, localAnimList2);
                }
                i = i2 + 1;
            }
            if (animateAlpha && animList == null && !localAnimList2.isEmpty()) {
                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(localAnimList2);
                animSet.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setOrAnimateAlpha(View view, boolean selected, boolean focused, boolean animate, ArrayList<Animator> animList) {
        if (selected) {
            if ((!focused || this.mKeyDown) && !this.mClicked) {
                setOrAnimateAlphaInternal(view, animate, this.mUnfocusedAlpha, -1.0f, animList, this.mDecelerateInterpolator);
                return;
            }
            setOrAnimateAlphaInternal(view, animate, this.mFocusedAlpha, -1.0f, animList, this.mDecelerateInterpolator);
        } else if (!focused || this.mClicked || this.mKeyDown) {
            setOrAnimateAlphaInternal(view, animate, this.mInvisibleColumnAlpha, -1.0f, animList, this.mDecelerateInterpolator);
        } else {
            setOrAnimateAlphaInternal(view, animate, this.mVisibleColumnAlpha, -1.0f, animList, this.mDecelerateInterpolator);
        }
    }

    private void setOrAnimateAlphaInternal(View view, boolean animate, float destAlpha, float startAlpha, ArrayList<Animator> animList, Interpolator interpolator) {
        ObjectAnimator anim;
        view.clearAnimation();
        if (!animate) {
            view.setAlpha(destAlpha);
            return;
        }
        if (startAlpha >= 0.0f) {
            anim = ObjectAnimator.ofFloat(view, "alpha", new float[]{startAlpha, destAlpha});
        } else {
            anim = ObjectAnimator.ofFloat(view, "alpha", new float[]{destAlpha});
        }
        anim.setDuration((long) this.mAlphaAnimDuration);
        anim.setInterpolator(interpolator);
        if (animList != null) {
            animList.add(anim);
        } else {
            anim.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onScroll(int column, View v, int position) {
    }

    private float getFloat(@DimenRes int resourceId) {
        TypedValue buffer = new TypedValue();
        this.mContext.getResources().getValue(resourceId, buffer, true);
        return buffer.getFloat();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.list_item);
            itemView.setOnClickListener(this);
        }

        public TextView getTextView() {
            return this.mTextView;
        }

        public void onClick(View v) {
            if (Picker.this.mKeyDown) {
                boolean unused = Picker.this.mKeyDown = false;
                boolean unused2 = Picker.this.mClicked = true;
                Picker.this.updateAllColumnsForClick(true);
            }
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> implements OnChildSelectedListener {
        private final int mColumnId;
        private VerticalGridView mGridView;
        private List<String> mItems;

        public Adapter(int columnId, List<String> items) {
            this.mColumnId = columnId;
            this.mItems = items;
            setHasStableIds(true);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(Picker.this.getLayoutInflater().inflate(R.layout.partner_picker_item, parent, false));
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView textView = holder.getTextView();
            textView.setText(this.mItems.get(position));
            Picker.this.setOrAnimateAlpha(textView, this.mGridView.getSelectedPosition() == position, this.mGridView.hasFocus(), false, (ArrayList<Animator>) null);
        }

        public int getItemCount() {
            return this.mItems.size();
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            this.mGridView = (VerticalGridView) recyclerView;
            this.mGridView.setOnChildSelectedListener(this);
        }

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            this.mGridView = null;
        }

        public void onChildSelected(ViewGroup parent, View view, int position, long id) {
            if (this.mGridView != null) {
                TextView textView = ((ViewHolder) this.mGridView.getChildViewHolder(view)).getTextView();
                Picker.this.updateColumn(this.mGridView, this.mGridView.hasFocus(), (ArrayList<Animator>) null);
                Picker.this.mResult.set(this.mColumnId, textView.getText().toString());
                Picker.this.onScroll(this.mColumnId, textView, position);
            }
        }

        public void setItems(List<String> items) {
            List<String> oldItems = this.mItems;
            this.mItems = items;
            if (oldItems.size() < items.size()) {
                notifyItemRangeInserted(oldItems.size(), oldItems.size() - items.size());
            } else if (items.size() < oldItems.size()) {
                notifyItemRangeRemoved(items.size(), items.size() - oldItems.size());
            }
        }
    }
}
