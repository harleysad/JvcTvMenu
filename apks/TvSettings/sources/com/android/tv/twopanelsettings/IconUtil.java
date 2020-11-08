package com.android.tv.twopanelsettings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class IconUtil {
    private static final int INSET = 12;
    private static final String TAG = "IconUtil";

    public static Drawable getCompoundIcon(Context context, Drawable icon) {
        Drawable container = ContextCompat.getDrawable(context, R.drawable.compound_icon_background);
        Resources res = context.getResources();
        try {
            icon.setTintList(ColorStateList.createFromXml(res, res.getXml(R.color.two_panel_preference_icon_color)));
        } catch (Exception e) {
            Log.e(TAG, "Cannot set tint", e);
        }
        LayerDrawable compoundDrawable = new LayerDrawable(new Drawable[]{container, icon});
        compoundDrawable.setLayerGravity(0, 17);
        compoundDrawable.setLayerGravity(1, 17);
        compoundDrawable.setLayerInset(1, 12, 12, 12, 12);
        return compoundDrawable;
    }
}
