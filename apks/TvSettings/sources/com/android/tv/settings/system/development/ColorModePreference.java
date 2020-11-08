package com.android.tv.settings.system.development;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.Display;
import com.android.tv.settings.R;
import java.util.ArrayList;

public class ColorModePreference extends SwitchPreference implements DisplayManager.DisplayListener {
    private int mCurrentIndex;
    private ArrayList<ColorModeDescription> mDescriptions;
    private Display mDisplay;
    private DisplayManager mDisplayManager = ((DisplayManager) getContext().getSystemService(DisplayManager.class));

    public ColorModePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getColorModeCount() {
        return this.mDescriptions.size();
    }

    public void startListening() {
        this.mDisplayManager.registerDisplayListener(this, new Handler(Looper.getMainLooper()));
    }

    public void stopListening() {
        this.mDisplayManager.unregisterDisplayListener(this);
    }

    public void onDisplayAdded(int displayId) {
        if (displayId == 0) {
            updateCurrentAndSupported();
        }
    }

    public void onDisplayChanged(int displayId) {
        if (displayId == 0) {
            updateCurrentAndSupported();
        }
    }

    public void onDisplayRemoved(int displayId) {
    }

    public void updateCurrentAndSupported() {
        boolean z = false;
        this.mDisplay = this.mDisplayManager.getDisplay(0);
        this.mDescriptions = new ArrayList<>();
        Resources resources = getContext().getResources();
        int[] colorModes = resources.getIntArray(R.array.color_mode_ids);
        String[] titles = resources.getStringArray(R.array.color_mode_names);
        String[] descriptions = resources.getStringArray(R.array.color_mode_descriptions);
        for (int i = 0; i < colorModes.length; i++) {
            if (!(colorModes[i] == -1 || i == 1)) {
                ColorModeDescription desc = new ColorModeDescription();
                int unused = desc.colorMode = colorModes[i];
                String unused2 = desc.title = titles[i];
                String unused3 = desc.summary = descriptions[i];
                this.mDescriptions.add(desc);
            }
        }
        int currentColorMode = this.mDisplay.getColorMode();
        this.mCurrentIndex = -1;
        int i2 = 0;
        while (true) {
            if (i2 >= this.mDescriptions.size()) {
                break;
            } else if (this.mDescriptions.get(i2).colorMode == currentColorMode) {
                this.mCurrentIndex = i2;
                break;
            } else {
                i2++;
            }
        }
        if (this.mCurrentIndex == 1) {
            z = true;
        }
        setChecked(z);
    }

    /* access modifiers changed from: protected */
    public boolean persistBoolean(boolean value) {
        if (this.mDescriptions.size() != 2) {
            return true;
        }
        ColorModeDescription desc = this.mDescriptions.get(value);
        this.mDisplay.requestColorMode(desc.colorMode);
        this.mCurrentIndex = this.mDescriptions.indexOf(desc);
        return true;
    }

    private static class ColorModeDescription {
        /* access modifiers changed from: private */
        public int colorMode;
        /* access modifiers changed from: private */
        public String summary;
        /* access modifiers changed from: private */
        public String title;

        private ColorModeDescription() {
        }
    }
}
