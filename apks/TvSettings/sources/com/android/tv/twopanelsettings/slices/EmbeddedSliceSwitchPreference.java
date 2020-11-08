package com.android.tv.twopanelsettings.slices;

import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.widget.ListContent;
import com.android.tv.twopanelsettings.R;
import com.android.tv.twopanelsettings.slices.PreferenceSliceLiveData;
import java.util.List;

public class EmbeddedSliceSwitchPreference extends SliceSwitchPreference implements Observer<Slice> {
    private static final String TAG = "EmbeddedSliceSwitchPreference";
    private Slice mSlice;
    private String mUri;

    public void onAttached() {
        super.onAttached();
        getSliceLiveData().observeForever(this);
    }

    public void onDetached() {
        super.onDetached();
        getSliceLiveData().removeObserver(this);
    }

    public EmbeddedSliceSwitchPreference(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public EmbeddedSliceSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            initStyleAttributes(attrs);
        }
    }

    private PreferenceSliceLiveData.SliceLiveDataImpl getSliceLiveData() {
        return ContextSingleton.getInstance().getSliceLiveData(getContext(), Uri.parse(this.mUri));
    }

    private void initStyleAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SlicePreference);
        for (int i = a.getIndexCount() - 1; i >= 0; i--) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SlicePreference_uri) {
                this.mUri = a.getString(attr);
                return;
            }
        }
    }

    public void onChanged(Slice slice) {
        if (getSliceLiveData().mUpdatePending.compareAndSet(true, false)) {
            this.mSlice = slice;
            if (slice == null || slice.getHints() == null || slice.getHints().contains("partial")) {
                setVisible(false);
            } else {
                update();
            }
        }
    }

    private void update() {
        List<SliceItem> items = new ListContent(getContext(), this.mSlice, (AttributeSet) null, 0, 0).getRowItems();
        if (items == null || items.size() == 0) {
            setVisible(false);
            return;
        }
        Preference newPref = SlicePreferencesUtil.getPreference(SlicePreferencesUtil.getEmbeddedItem(items), (ContextThemeWrapper) getContext(), (String) null);
        if (newPref == null) {
            setVisible(false);
            return;
        }
        setTitle(newPref.getTitle());
        setSummary(newPref.getSummary());
        setIcon(newPref.getIcon());
        if (newPref instanceof TwoStatePreference) {
            setChecked(((TwoStatePreference) newPref).isChecked());
        }
        if (newPref instanceof HasSliceAction) {
            setSliceAction(((HasSliceAction) newPref).getSliceAction());
        }
        setVisible(true);
    }

    public void onClick() {
        boolean z = true;
        boolean newValue = !isChecked();
        try {
            if (this.mAction != null) {
                if (this.mAction.isToggle()) {
                    this.mAction.getActionItem().fireAction(getContext(), new Intent().putExtra("android.app.slice.extra.TOGGLE_STATE", newValue));
                } else {
                    this.mAction.getActionItem().fireAction((Context) null, (Intent) null);
                }
                if (callChangeListener(Boolean.valueOf(newValue))) {
                    setChecked(newValue);
                }
            }
        } catch (PendingIntent.CanceledException e) {
            if (newValue) {
                z = false;
            }
            newValue = z;
            Log.e(TAG, "PendingIntent for slice cannot be sent", e);
        }
    }
}
