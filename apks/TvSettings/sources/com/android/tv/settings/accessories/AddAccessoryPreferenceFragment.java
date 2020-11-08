package com.android.tv.settings.accessories;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v17.preference.BaseLeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.SparseArray;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.tv.settings.R;
import java.util.List;

public class AddAccessoryPreferenceFragment extends BaseLeanbackPreferenceFragment implements LifecycleOwner, Instrumentable {
    private final Lifecycle mLifecycle = new Lifecycle(this);
    private SparseArray<Drawable> mResizedDrawables = new SparseArray<>();
    private final VisibilityLoggerMixin mVisibilityLoggerMixin = new VisibilityLoggerMixin(getMetricsCategory(), new MetricsFeatureProvider());

    public AddAccessoryPreferenceFragment() {
        getLifecycle().addObserver(this.mVisibilityLoggerMixin);
    }

    public static AddAccessoryPreferenceFragment newInstance() {
        return new AddAccessoryPreferenceFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        AddAccessoryActivity activity = (AddAccessoryActivity) getActivity();
        updateList(activity.getBluetoothDevices(), activity.getCurrentTargetAddress(), activity.getCurrentTargetStatus(), activity.getCancelledAddress());
    }

    public void updateList(List<BluetoothDevice> devices, String currentTargetAddress, String currentTargetStatus, String cancelledAddress) {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            screen = getPreferenceManager().createPreferenceScreen(themedContext);
            setPreferenceScreen(screen);
        } else {
            screen.removeAll();
        }
        if (devices != null) {
            for (BluetoothDevice bt : devices) {
                Preference preference = new Preference(themedContext);
                if (currentTargetAddress.equalsIgnoreCase(bt.getAddress()) && !currentTargetStatus.isEmpty()) {
                    preference.setSummary((CharSequence) currentTargetStatus);
                } else if (cancelledAddress.equalsIgnoreCase(bt.getAddress())) {
                    preference.setSummary((int) R.string.accessory_state_canceled);
                } else {
                    preference.setSummary((CharSequence) bt.getAddress());
                }
                preference.setKey(bt.getAddress());
                preference.setTitle((CharSequence) bt.getName());
                preference.setIcon(getDeviceDrawable(bt));
                screen.addPreference(preference);
            }
        }
    }

    private Drawable getDeviceDrawable(BluetoothDevice device) {
        int resId = AccessoryUtils.getImageIdForDevice(device);
        Drawable drawable = this.mResizedDrawables.get(resId);
        if (drawable != null) {
            return drawable;
        }
        Drawable tempDrawable = getActivity().getDrawable(resId);
        int iconWidth = getResources().getDimensionPixelSize(R.dimen.lb_dialog_list_item_icon_width);
        int iconHeight = getResources().getDimensionPixelSize(R.dimen.lb_dialog_list_item_icon_height);
        tempDrawable.setBounds(0, 0, iconWidth, iconHeight);
        Bitmap bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        tempDrawable.draw(new Canvas(bitmap));
        Drawable drawable2 = new BitmapDrawable(getResources(), bitmap);
        this.mResizedDrawables.put(resId, drawable2);
        return drawable2;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        ((AddAccessoryActivity) getActivity()).onActionClicked(preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    public void advanceSelection() {
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        if (preferenceCount > 0) {
            VerticalGridView vgv = (VerticalGridView) getListView();
            vgv.setSelectedPositionSmooth((vgv.getSelectedPosition() + 1) % preferenceCount);
        }
    }

    public int getMetricsCategory() {
        return 1018;
    }

    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public void onResume() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        super.onResume();
    }

    public void onPause() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }
}
