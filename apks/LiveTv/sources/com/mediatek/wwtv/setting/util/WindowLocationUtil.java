package com.mediatek.wwtv.setting.util;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;

public class WindowLocationUtil {
    private static final float[] sTmpFloat4 = new float[4];
    private static final float[] sTmpFloat8 = new float[8];

    public static void getLocationsInWindow(View view, float[] points) {
        if (points == null || (points.length & 1) != 0) {
            throw new IllegalArgumentException();
        }
        int length = points.length;
        Matrix matrix = view.getMatrix();
        if (matrix != null && !matrix.isIdentity()) {
            matrix.mapPoints(points);
        }
        int deltax = view.getLeft();
        int deltay = view.getTop();
        int i = 0;
        while (i < length) {
            points[i] = points[i] + ((float) deltax);
            int i2 = i + 1;
            points[i2] = points[i2] + ((float) deltay);
            i = i2 + 1;
        }
        ViewParent viewParent = view.getParent();
        while (viewParent instanceof View) {
            View view2 = (View) viewParent;
            int deltax2 = view2.getScrollX();
            int deltay2 = view2.getScrollY();
            int i3 = 0;
            while (i3 < length) {
                points[i3] = points[i3] - ((float) deltax2);
                int i4 = i3 + 1;
                points[i4] = points[i4] - ((float) deltay2);
                i3 = i4 + 1;
            }
            Matrix matrix2 = view2.getMatrix();
            if (matrix2 != null && !matrix2.isIdentity()) {
                matrix2.mapPoints(points);
            }
            int deltax3 = view2.getLeft();
            int deltay3 = view2.getTop();
            int i5 = 0;
            while (i5 < length) {
                points[i5] = points[i5] + ((float) deltax3);
                int i6 = i5 + 1;
                points[i6] = points[i6] + ((float) deltay3);
                i5 = i6 + 1;
            }
            viewParent = view2.getParent();
        }
    }

    public static void getLocationsInWindow(View view, RectF rect) {
        sTmpFloat4[0] = rect.left;
        sTmpFloat4[1] = rect.top;
        sTmpFloat4[2] = rect.right;
        sTmpFloat4[3] = rect.bottom;
        getLocationsInWindow(view, sTmpFloat4);
        rect.left = sTmpFloat4[0];
        rect.top = sTmpFloat4[1];
        rect.right = sTmpFloat4[2];
        rect.bottom = sTmpFloat4[3];
    }

    public static void getImageLocationsInWindow(ImageView view, RectF clippedBounds, RectF unclippedBitmapRect) {
        clippedBounds.set((float) view.getPaddingLeft(), (float) view.getPaddingTop(), (float) (view.getWidth() - view.getPaddingRight()), (float) (view.getHeight() - view.getPaddingBottom()));
        Matrix matrix = view.getImageMatrix();
        Drawable drawable = view.getDrawable();
        if (drawable != null) {
            unclippedBitmapRect.set(drawable.getBounds());
            matrix.mapRect(unclippedBitmapRect);
            unclippedBitmapRect.offset((float) view.getPaddingLeft(), (float) view.getPaddingTop());
            sTmpFloat8[0] = clippedBounds.left;
            sTmpFloat8[1] = clippedBounds.top;
            sTmpFloat8[2] = clippedBounds.right;
            sTmpFloat8[3] = clippedBounds.bottom;
            sTmpFloat8[4] = unclippedBitmapRect.left;
            sTmpFloat8[5] = unclippedBitmapRect.top;
            sTmpFloat8[6] = unclippedBitmapRect.right;
            sTmpFloat8[7] = unclippedBitmapRect.bottom;
            getLocationsInWindow((View) view, sTmpFloat8);
            clippedBounds.left = sTmpFloat8[0];
            clippedBounds.top = sTmpFloat8[1];
            clippedBounds.right = sTmpFloat8[2];
            clippedBounds.bottom = sTmpFloat8[3];
            unclippedBitmapRect.left = sTmpFloat8[4];
            unclippedBitmapRect.top = sTmpFloat8[5];
            unclippedBitmapRect.right = sTmpFloat8[6];
            unclippedBitmapRect.bottom = sTmpFloat8[7];
            clippedBounds.intersect(unclippedBitmapRect);
            return;
        }
        sTmpFloat4[0] = clippedBounds.left;
        sTmpFloat4[1] = clippedBounds.top;
        sTmpFloat4[2] = clippedBounds.right;
        sTmpFloat4[3] = clippedBounds.bottom;
        getLocationsInWindow((View) view, sTmpFloat4);
        clippedBounds.left = sTmpFloat4[0];
        clippedBounds.top = sTmpFloat4[1];
        clippedBounds.right = sTmpFloat4[2];
        clippedBounds.bottom = sTmpFloat4[3];
        unclippedBitmapRect.set(clippedBounds);
    }
}
