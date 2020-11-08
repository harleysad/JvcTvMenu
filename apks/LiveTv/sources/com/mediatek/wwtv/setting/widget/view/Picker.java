package com.mediatek.wwtv.setting.widget.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.TextView;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.view.Picker;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Picker extends Fragment {
    protected static final String TAG = "Picker";
    protected String channelId;
    private boolean isPositionView = false;
    private Interpolator mAccelerateInterpolator;
    public Action mAction;
    private int mAlphaAnimDuration;
    protected Consumer<Picker> mBackStack;
    /* access modifiers changed from: private */
    public boolean mClicked = false;
    private ChangeTextColorOnFocus mColumnChangeListener;
    /* access modifiers changed from: private */
    public List<ScrollAdapterView> mColumnViews;
    private ArrayList<PickerColumn> mColumns = new ArrayList<>();
    protected PickerConstant mConstant;
    private Context mContext;
    private Interpolator mDecelerateInterpolator;
    private float mFocusedAlpha;
    private float mInvisibleColumnAlpha;
    protected String mItemId;
    /* access modifiers changed from: private */
    public boolean mKeyDown = false;
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (Picker.this.mKeyDown) {
                boolean unused = Picker.this.mKeyDown = false;
                boolean unused2 = Picker.this.mClicked = true;
                MtkLog.d(Picker.TAG, "onItemClick mClicked:" + Picker.this.mClicked);
                Picker.this.updateAllColumnsForClick(true);
            }
        }
    };
    private ViewGroup mPickerView;
    /* access modifiers changed from: private */
    public List<String> mResult;
    protected ResultListener mResultListener;
    private ViewGroup mRootView;
    private String mSeparator;
    private float mUnfocusedAlpha;
    private float mVisibleColumnAlpha;

    public interface ResultListener {
        void onCommitResult(String str);
    }

    public void setAction(Action action) {
        this.mAction = action;
        if (action.mDataType == Action.DataType.POSITIONVIEW) {
            this.isPositionView = true;
        }
    }

    public static Picker newInstance() {
        return new Picker();
    }

    /* access modifiers changed from: protected */
    public ArrayList<PickerColumn> getColumns() {
        return null;
    }

    /* access modifiers changed from: protected */
    public String getSeparator() {
        return this.mSeparator;
    }

    /* access modifiers changed from: protected */
    public int getRootLayoutId() {
        return R.layout.picker;
    }

    /* access modifiers changed from: protected */
    public int getPickerId() {
        return R.id.picker;
    }

    /* access modifiers changed from: protected */
    public int getPickerSeparatorLayoutId() {
        return R.layout.picker_separator;
    }

    /* access modifiers changed from: protected */
    public int getPickerItemLayoutId() {
        return R.layout.picker_item;
    }

    /* access modifiers changed from: protected */
    public int getPickerItemTextViewId() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getPickerColumnHeightPixels() {
        return getActivity().getResources().getDimensionPixelSize(R.dimen.picker_column_height);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        this.mConstant = PickerConstant.getInstance(this.mContext.getResources());
        this.mFocusedAlpha = getFloat(R.dimen.list_item_selected_title_text_alpha);
        this.mUnfocusedAlpha = getFloat(R.dimen.list_item_unselected_text_alpha);
        this.mVisibleColumnAlpha = getFloat(R.dimen.picker_item_visible_column_item_alpha);
        this.mInvisibleColumnAlpha = getFloat(R.dimen.picker_item_invisible_column_item_alpha);
        this.mColumnChangeListener = new ChangeTextColorOnFocus();
        this.mAlphaAnimDuration = this.mContext.getResources().getInteger(R.integer.dialog_animation_duration);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.5f);
        this.mAccelerateInterpolator = new AccelerateInterpolator(2.5f);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mColumns = getColumns();
        if (this.mColumns == null || this.mColumns.size() == 0) {
            return null;
        }
        this.mRootView = (ViewGroup) inflater.inflate(getRootLayoutId(), (ViewGroup) null);
        this.mPickerView = (ViewGroup) this.mRootView.findViewById(getPickerId());
        this.mColumnViews = new ArrayList();
        this.mResult = new ArrayList();
        int totalCol = this.mColumns.size();
        for (int i = 0; i < totalCol; i++) {
            this.mResult.add(this.mColumns.get(i).getItems()[0]);
            ScrollAdapterView columnView = (ScrollAdapterView) inflater.inflate(R.layout.picker_column, this.mPickerView, false);
            ViewGroup.LayoutParams lp = columnView.getLayoutParams();
            lp.height = getPickerColumnHeightPixels();
            columnView.setLayoutParams(lp);
            this.mColumnViews.add(columnView);
            columnView.setTag(Integer.valueOf(i));
            this.mPickerView.addView(columnView);
            if (!(i == totalCol - 1 || getSeparator() == null)) {
                TextView separator = (TextView) inflater.inflate(getPickerSeparatorLayoutId(), this.mPickerView, false);
                separator.setText(getSeparator());
                this.mPickerView.addView(separator);
            }
        }
        initAdapters();
        this.mColumnViews.get(0).requestFocus();
        this.mClicked = false;
        this.mKeyDown = false;
        return this.mRootView;
    }

    private void initAdapters() {
        int totalCol = this.mColumns.size();
        for (int i = 0; i < totalCol; i++) {
            ScrollAdapterView columnView = this.mColumnViews.get(i);
            setAdapter(columnView, this.mColumns.get(i).getItems(), i);
            columnView.setOnFocusChangeListener(this.mColumnChangeListener);
            columnView.setOnItemSelectedListener(this.mColumnChangeListener);
            columnView.setOnItemClickListener(this.mOnClickListener);
            columnView.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    MtkLog.d(Picker.TAG, "keyCode:" + keyCode + ", noKeyDown:" + Picker.this.mKeyDown);
                    if (keyCode != 23 && keyCode != 66) {
                        switch (keyCode) {
                            case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                ((ScrollAdapterView) Picker.this.mColumnViews.get(0)).requestFocus();
                                break;
                            case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                ((ScrollAdapterView) Picker.this.mColumnViews.get(1)).requestFocus();
                                break;
                        }
                    } else if (event.getAction() == 0 && !Picker.this.mKeyDown) {
                        boolean unused = Picker.this.mKeyDown = true;
                        Picker.this.updateAllColumnsForClick(false);
                    }
                    return false;
                }
            });
        }
    }

    private void unregisterListeners() {
        int totalCol = this.mColumns.size();
        for (int i = 0; i < totalCol; i++) {
            ScrollAdapterView columnView = this.mColumnViews.get(i);
            columnView.setOnFocusChangeListener((View.OnFocusChangeListener) null);
            columnView.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) null);
            columnView.setOnItemClickListener((AdapterView.OnItemClickListener) null);
            columnView.setOnKeyListener((View.OnKeyListener) null);
        }
    }

    private void setAdapter(ScrollAdapterView columnView, String[] col, int colIndex) {
        PickerScrollArrayAdapter pickerScrollArrayAdapter;
        List<String> arrayList = new ArrayList<>(Arrays.asList(col));
        if (getPickerItemTextViewId() == 0) {
            pickerScrollArrayAdapter = new PickerScrollArrayAdapter(this.mContext, getPickerItemLayoutId(), arrayList, colIndex);
        } else {
            pickerScrollArrayAdapter = new PickerScrollArrayAdapter(this.mContext, getPickerItemLayoutId(), getPickerItemTextViewId(), arrayList, colIndex);
        }
        columnView.setAdapter(pickerScrollArrayAdapter);
    }

    /* access modifiers changed from: protected */
    public void updateAdapter(int index, PickerColumn pickerColumn) {
        ScrollAdapterView columnView = this.mColumnViews.get(index);
        String[] col = pickerColumn.getItems();
        int selectedItemPosition = columnView.getSelectedItemPosition();
        MtkLog.d(TAG, "selectedItemPosition:" + selectedItemPosition);
        ScrollArrayAdapter<String> adapter = (ScrollArrayAdapter) columnView.getAdapter();
        if (adapter != null) {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(col);
            adapter.notifyDataSetChanged();
            if (selectedItemPosition >= col.length) {
                selectedItemPosition = col.length - 1;
            }
            columnView.setSelection(selectedItemPosition);
        }
        updateColumn(columnView, false, (ArrayList<Animator>) null);
        this.mColumns.set(index, pickerColumn);
    }

    /* access modifiers changed from: protected */
    public void updateSelection(int columnIndex, int selectedIndex) {
        ScrollAdapterView columnView = this.mColumnViews.get(columnIndex);
        if (columnView != null) {
            columnView.setSelection(selectedIndex);
            this.mResult.set(columnIndex, this.mColumns.get(columnIndex).getItems()[selectedIndex]);
        }
    }

    public void setResultListener(ResultListener listener) {
        this.mResultListener = listener;
    }

    /* access modifiers changed from: private */
    public void updateAllColumnsForClick(boolean keyUp) {
        ArrayList<Animator> animList = new ArrayList<>();
        int i = 0;
        while (true) {
            int j = i;
            if (j >= this.mColumnViews.size()) {
                break;
            }
            ScrollAdapterView column = this.mColumnViews.get(j);
            int selected = column.getSelectedItemPosition();
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= column.getAdapter().getCount()) {
                    break;
                }
                View item = column.getItemView(i3);
                if (item != null) {
                    if (selected == i3) {
                        if (keyUp) {
                            setOrAnimateAlpha(item, true, this.mFocusedAlpha, this.mUnfocusedAlpha, animList, this.mAccelerateInterpolator);
                        } else {
                            setOrAnimateAlpha(item, true, this.mUnfocusedAlpha, -1.0f, animList, this.mDecelerateInterpolator);
                        }
                    } else if (!keyUp) {
                        setOrAnimateAlpha(item, true, this.mInvisibleColumnAlpha, -1.0f, animList, this.mDecelerateInterpolator);
                    }
                }
                i2 = i3 + 1;
            }
            i = j + 1;
        }
        MtkLog.d(TAG, "animList.size():" + animList.size());
        if (animList.size() > 0) {
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animList);
            MtkLog.d(TAG, "mClicked=" + this.mClicked + ",keyUp=" + keyUp);
            if (this.mClicked && keyUp) {
                animSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Picker.this.recordResult(Picker.this.mResult);
                    }
                });
            }
            animSet.start();
        }
    }

    /* access modifiers changed from: protected */
    public void recordResult(List<String> list) {
    }

    public void setFocusDisabled(Consumer<Picker> callback) {
        this.mBackStack = callback;
    }

    /* access modifiers changed from: private */
    public void updateColumn(ScrollAdapterView column, boolean animateAlpha, ArrayList<Animator> animList) {
        if (column != null) {
            int selected = column.getSelectedItemPosition();
            boolean focused = column.hasFocus();
            ArrayList<Animator> localAnimList = animList;
            if (animateAlpha && localAnimList == null) {
                localAnimList = new ArrayList<>();
            }
            ArrayList<Animator> localAnimList2 = localAnimList;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= column.getAdapter().getCount()) {
                    break;
                }
                View item = column.getItemView(i2);
                if (item != null) {
                    setOrAnimateAlpha(item, selected == i2, focused, animateAlpha, localAnimList2);
                }
                i = i2 + 1;
            }
            if (animateAlpha && animList == null && localAnimList2 != null && localAnimList2.size() > 0) {
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
                setOrAnimateAlpha(view, animate, this.mUnfocusedAlpha, -1.0f, animList, this.mDecelerateInterpolator);
                return;
            }
            setOrAnimateAlpha(view, animate, this.mFocusedAlpha, -1.0f, animList, this.mDecelerateInterpolator);
        } else if (!focused || this.mClicked || this.mKeyDown) {
            setOrAnimateAlpha(view, animate, this.mInvisibleColumnAlpha, -1.0f, animList, this.mDecelerateInterpolator);
        } else {
            setOrAnimateAlpha(view, animate, this.mVisibleColumnAlpha, -1.0f, animList, this.mDecelerateInterpolator);
        }
    }

    private void setOrAnimateAlpha(View view, boolean animate, float destAlpha, float startAlpha, ArrayList<Animator> animList, Interpolator interpolator) {
        ObjectAnimator anim;
        MtkLog.d(TAG, "animate=" + animate + ",destAlpha=" + destAlpha + ",startAlpha=" + startAlpha + ",tag=" + view.getTag());
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
    public void onScroll(View v) {
    }

    public void onDestroyView() {
        unregisterListeners();
        if (this.mColumnChangeListener != null) {
            this.mColumnChangeListener.setDisabled();
        }
        super.onDestroyView();
    }

    private float getFloat(int resourceId) {
        TypedValue buffer = new TypedValue();
        this.mContext.getResources().getValue(resourceId, buffer, true);
        return buffer.getFloat();
    }

    private class PickerScrollArrayAdapter extends ScrollArrayAdapter<String> {
        private final int mColIndex;
        private final int mTextViewResourceId;

        PickerScrollArrayAdapter(Context context, int resource, List<String> objects, int colIndex) {
            super(context, resource, objects);
            this.mColIndex = colIndex;
            this.mTextViewResourceId = 0;
        }

        PickerScrollArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects, int colIndex) {
            super(context, resource, textViewResourceId, objects);
            this.mColIndex = colIndex;
            this.mTextViewResourceId = textViewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setTag(Integer.valueOf(this.mColIndex));
            MtkLog.d(Picker.TAG, "getView=" + position);
            Picker.this.setOrAnimateAlpha(view, ((ScrollAdapterView) Picker.this.mColumnViews.get(this.mColIndex)).getSelectedItemPosition() == position, false, false, (ArrayList<Animator>) null);
            return view;
        }

        /* access modifiers changed from: package-private */
        public TextView getTextViewFromAdapterView(View adapterView) {
            if (this.mTextViewResourceId != 0) {
                return (TextView) adapterView.findViewById(this.mTextViewResourceId);
            }
            return (TextView) adapterView;
        }
    }

    private class ChangeTextColorOnFocus implements View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {
        private int focusCount;
        private boolean mDisabled = false;

        ChangeTextColorOnFocus() {
        }

        public void setDisabled() {
            this.mDisabled = true;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!this.mDisabled) {
                TextView textView = ((PickerScrollArrayAdapter) parent.getAdapter()).getTextViewFromAdapterView(view);
                int colIndex = ((Integer) parent.getTag()).intValue();
                Picker.this.updateColumn((ScrollAdapterView) parent, parent.hasFocus(), (ArrayList<Animator>) null);
                Picker.this.mResult.set(colIndex, textView.getText().toString());
                Picker.this.onScroll(textView);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onFocusChange(View view, boolean hasFocus) {
            if (!this.mDisabled && (view instanceof ScrollAdapterView)) {
                this.focusCount = Picker.this.mColumnViews.size();
                Picker.this.mColumnViews.stream().filter(new Predicate() {
                    public final boolean test(Object obj) {
                        return Picker.ChangeTextColorOnFocus.lambda$onFocusChange$0(Picker.ChangeTextColorOnFocus.this, (ScrollAdapterView) obj);
                    }
                }).forEach(new Consumer() {
                    public final void accept(Object obj) {
                        Picker.ChangeTextColorOnFocus.lambda$onFocusChange$1(Picker.ChangeTextColorOnFocus.this, (ScrollAdapterView) obj);
                    }
                });
                Picker.this.updateColumn((ScrollAdapterView) view, true, (ArrayList<Animator>) null);
            }
        }

        public static /* synthetic */ boolean lambda$onFocusChange$0(ChangeTextColorOnFocus changeTextColorOnFocus, ScrollAdapterView columView) {
            return Picker.this.mBackStack != null && !columView.hasFocus();
        }

        public static /* synthetic */ void lambda$onFocusChange$1(ChangeTextColorOnFocus changeTextColorOnFocus, ScrollAdapterView columView) {
            changeTextColorOnFocus.focusCount--;
            if (changeTextColorOnFocus.focusCount == 0) {
                Picker.this.mBackStack.accept(Picker.this);
            }
        }
    }
}
