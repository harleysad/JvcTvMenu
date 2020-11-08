package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions;
import com.mediatek.wwtv.setting.widget.view.DrawableDownloader;
import java.util.ArrayList;
import java.util.List;

public class TransitionImage {
    public static final String EXTRA_TRANSITION_BITMAP = "com.android.tv.settings.transition_bitmap";
    public static final String EXTRA_TRANSITION_BITMAP_ALPHA = "com.android.tv.settings.transition_bmp_alpha";
    public static final String EXTRA_TRANSITION_BITMAP_BACKGROUND = "com.android.tv.settings.transition_bmp_background";
    public static final String EXTRA_TRANSITION_BITMAP_CLIPPED_RECT = "com.android.tv.settings.transition_bmp_clipped_rect";
    public static final String EXTRA_TRANSITION_BITMAP_RECT = "com.android.tv.settings.transition_bmp_rect";
    public static final String EXTRA_TRANSITION_BITMAP_SATURATION = "com.android.tv.settings.transition_bmp_saturation";
    public static final String EXTRA_TRANSITION_BITMAP_UNCLIPPED_RECT = "com.android.tv.settings.transition_bmp_unclipped_rect";
    public static final String EXTRA_TRANSITION_BITMAP_URI = "com.android.tv.settings.transition_bmp_uri";
    public static final String EXTRA_TRANSITION_MULTIPLE_BITMAP = "com.android.tv.settings.transition_multiple_bitmap";
    private float mAlpha = 1.0f;
    private int mBackground = 0;
    private BitmapDrawable mBitmap;
    private RectF mClippedRect = new RectF();
    private final Rect mRect = new Rect();
    private float mSaturation = 1.0f;
    private RectF mUnclippedRect = new RectF();
    private Uri mUri;
    private boolean mUseClippedRectOnTransparent = true;
    private Object mUserObject;

    public Uri getUri() {
        return this.mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

    public BitmapDrawable getBitmap() {
        return this.mBitmap;
    }

    public void setBitmap(BitmapDrawable bitmap) {
        this.mBitmap = bitmap;
    }

    public Rect getRect() {
        return this.mRect;
    }

    public void setRect(Rect rect) {
        this.mRect.set(rect);
    }

    public int getBackground() {
        return this.mBackground;
    }

    public void setBackground(int color) {
        this.mBackground = color;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public float getSaturation() {
        return this.mSaturation;
    }

    public void setSaturation(float saturation) {
        this.mSaturation = saturation;
    }

    public RectF getUnclippedRect() {
        return this.mUnclippedRect;
    }

    public void setUnclippedRect(RectF rect) {
        this.mUnclippedRect.set(rect);
    }

    public RectF getClippedRect() {
        return this.mClippedRect;
    }

    public void setClippedRect(RectF rect) {
        this.mClippedRect.set(rect);
    }

    public static List<TransitionImage> readMultipleFromIntent(Context context, Intent intent) {
        ArrayList<TransitionImage> transitions = new ArrayList<>();
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return transitions;
        }
        TransitionImage image = new TransitionImage();
        if (image.readFromBundle(context, intent.getSourceBounds(), extras)) {
            transitions.add(image);
        }
        Parcelable[] multiple = intent.getParcelableArrayExtra(EXTRA_TRANSITION_MULTIPLE_BITMAP);
        if (multiple != null) {
            int i = 0;
            int size = multiple.length;
            while (i < size && (multiple[i] instanceof Bundle)) {
                TransitionImage image2 = new TransitionImage();
                if (image2.readFromBundle(context, (Rect) null, (Bundle) multiple[i])) {
                    transitions.add(image2);
                }
                i++;
            }
        }
        return transitions;
    }

    public static void writeMultipleToIntent(List<TransitionImage> transitions, Intent intent) {
        if (transitions != null && transitions.size() != 0) {
            int size = transitions.size();
            if (size == 1) {
                transitions.get(0).writeToIntent(intent);
                return;
            }
            Parcelable[] multipleBundle = new Parcelable[size];
            for (int i = 0; i < size; i++) {
                Bundle b = new Bundle();
                transitions.get(i).writeToBundle(b);
                multipleBundle[i] = b;
            }
            intent.putExtra(EXTRA_TRANSITION_MULTIPLE_BITMAP, multipleBundle);
        }
    }

    public boolean readFromBundle(Context context, Rect intentSourceBounds, Bundle bundle) {
        setBitmap((BitmapDrawable) null);
        if (bundle == null) {
            return false;
        }
        this.mUri = (Uri) bundle.getParcelable(EXTRA_TRANSITION_BITMAP_URI);
        BitmapDrawable bitmap = null;
        if (this.mUri != null) {
            BitmapDrawable bitmap2 = DrawableDownloader.getInstance(context).getLargestBitmapFromMemCache(new BitmapWorkerOptions.Builder(context).resource(this.mUri).build());
            if (bitmap2 instanceof BitmapDrawable) {
                bitmap = bitmap2;
            }
        }
        if (bitmap == null) {
            if (bundle.containsKey(EXTRA_TRANSITION_BITMAP)) {
                bitmap = new BitmapDrawable(context.getResources(), ActivityTransitionBitmapHelper.getBitmapFromBinderBundle(bundle.getBundle(EXTRA_TRANSITION_BITMAP)));
            }
            if (bitmap == null) {
                return false;
            }
        }
        Rect rect = null;
        String bitmapRectStr = bundle.getString(EXTRA_TRANSITION_BITMAP_RECT);
        if (!TextUtils.isEmpty(bitmapRectStr)) {
            rect = Rect.unflattenFromString(bitmapRectStr);
        }
        if (rect == null) {
            rect = intentSourceBounds;
        }
        if (rect == null) {
            return false;
        }
        setBitmap(bitmap);
        setRect(rect);
        if (!readRectF(bundle.getFloatArray(EXTRA_TRANSITION_BITMAP_CLIPPED_RECT), this.mClippedRect)) {
            this.mClippedRect.set(rect);
        }
        if (!readRectF(bundle.getFloatArray(EXTRA_TRANSITION_BITMAP_UNCLIPPED_RECT), this.mUnclippedRect)) {
            this.mUnclippedRect.set(rect);
        }
        setAlpha(bundle.getFloat(EXTRA_TRANSITION_BITMAP_ALPHA, 1.0f));
        setSaturation(bundle.getFloat(EXTRA_TRANSITION_BITMAP_SATURATION, 1.0f));
        setBackground(bundle.getInt(EXTRA_TRANSITION_BITMAP_BACKGROUND, 0));
        return true;
    }

    public void writeToBundle(Bundle bundle) {
        bundle.putParcelable(EXTRA_TRANSITION_BITMAP_URI, this.mUri);
        bundle.putString(EXTRA_TRANSITION_BITMAP_RECT, this.mRect.flattenToString());
        if (this.mBitmap != null) {
            bundle.putBundle(EXTRA_TRANSITION_BITMAP, ActivityTransitionBitmapHelper.bitmapAsBinderBundle(this.mBitmap.getBitmap()));
        }
        bundle.putFloatArray(EXTRA_TRANSITION_BITMAP_CLIPPED_RECT, writeRectF(this.mClippedRect, new float[4]));
        bundle.putFloatArray(EXTRA_TRANSITION_BITMAP_UNCLIPPED_RECT, writeRectF(this.mUnclippedRect, new float[4]));
        bundle.putFloat(EXTRA_TRANSITION_BITMAP_ALPHA, this.mAlpha);
        bundle.putFloat(EXTRA_TRANSITION_BITMAP_SATURATION, this.mSaturation);
        bundle.putInt(EXTRA_TRANSITION_BITMAP_BACKGROUND, this.mBackground);
    }

    public void writeToIntent(Intent intent) {
        intent.setSourceBounds(this.mRect);
        intent.putExtra(EXTRA_TRANSITION_BITMAP_URI, this.mUri);
        intent.putExtra(EXTRA_TRANSITION_BITMAP_RECT, this.mRect.flattenToString());
        if (this.mBitmap != null) {
            intent.putExtra(EXTRA_TRANSITION_BITMAP, ActivityTransitionBitmapHelper.bitmapAsBinderBundle(this.mBitmap.getBitmap()));
        }
        intent.putExtra(EXTRA_TRANSITION_BITMAP_CLIPPED_RECT, writeRectF(this.mClippedRect, new float[4]));
        intent.putExtra(EXTRA_TRANSITION_BITMAP_UNCLIPPED_RECT, writeRectF(this.mUnclippedRect, new float[4]));
        intent.putExtra(EXTRA_TRANSITION_BITMAP_ALPHA, this.mAlpha);
        intent.putExtra(EXTRA_TRANSITION_BITMAP_SATURATION, this.mSaturation);
        intent.putExtra(EXTRA_TRANSITION_BITMAP_BACKGROUND, this.mBackground);
    }

    public static boolean readRectF(float[] values, RectF f) {
        if (values == null || values.length != 4) {
            return false;
        }
        f.set(values[0], values[1], values[2], values[3]);
        return true;
    }

    public static float[] writeRectF(RectF f, float[] values) {
        values[0] = f.left;
        values[1] = f.top;
        values[2] = f.right;
        values[3] = f.bottom;
        return values;
    }

    public void createFromImageView(ImageView imageView) {
        createFromImageView(imageView, imageView);
    }

    public void createFromImageView(ImageView view, View backgroundView) {
        Drawable drawable = view.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            setBitmap((BitmapDrawable) drawable);
        }
        this.mClippedRect.set(0.0f, 0.0f, (float) backgroundView.getWidth(), (float) backgroundView.getHeight());
        WindowLocationUtil.getLocationsInWindow(backgroundView, this.mClippedRect);
        this.mClippedRect.round(this.mRect);
        WindowLocationUtil.getImageLocationsInWindow(view, this.mClippedRect, this.mUnclippedRect);
    }

    public void setUseClippedRectOnTransparent(boolean ignoreBackground) {
        this.mUseClippedRectOnTransparent = ignoreBackground;
    }

    public boolean getUseClippedRectOnTransparent() {
        return this.mUseClippedRectOnTransparent;
    }

    public void getOptimizedRect(Rect rect) {
        if (!this.mUseClippedRectOnTransparent || this.mBackground != 0) {
            rect.set(this.mRect);
        } else {
            this.mClippedRect.round(rect);
        }
    }

    public void setUserObject(Object object) {
        this.mUserObject = object;
    }

    public Object getUserObject() {
        return this.mUserObject;
    }

    public String toString() {
        return "{TransitionImage Uri=" + this.mUri + " rect=" + this.mRect + " unclipRect=" + this.mUnclippedRect + " clipRect=" + this.mClippedRect + " bitmap=" + this.mBitmap + " alpha=" + this.mAlpha + " saturation=" + this.mSaturation + " background=" + this.mBackground;
    }
}
