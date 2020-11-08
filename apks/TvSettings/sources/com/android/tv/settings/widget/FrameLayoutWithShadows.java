package com.android.tv.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.tv.settings.R;
import java.util.ArrayList;

public class FrameLayoutWithShadows extends FrameLayout {
    private static final int MAX_RECYCLE = 12;
    private int mBottomResourceId;
    private final ArrayList<ShadowView> mRecycleBin;
    private int mShadowResourceId;
    private float mShadowsAlpha;
    private final Rect rect;
    private final RectF rectf;

    static class ShadowView extends View {
        private float mAlpha = 1.0f;
        private Drawable mDrawableBottom;
        /* access modifiers changed from: private */
        public View shadowedView;

        ShadowView(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        /* access modifiers changed from: package-private */
        public void init() {
            this.shadowedView = null;
            this.mDrawableBottom = null;
        }

        public void setBackground(Drawable background) {
            super.setBackground(background);
            if (background != null) {
                background.setCallback((Drawable.Callback) null);
                background.setAlpha((int) (255.0f * this.mAlpha));
            }
        }

        public void setAlpha(float alpha) {
            if (this.mAlpha != alpha) {
                this.mAlpha = alpha;
                Drawable d = getBackground();
                int alphaMulitplied = (int) (255.0f * alpha);
                if (d != null) {
                    d.setAlpha(alphaMulitplied);
                }
                if (this.mDrawableBottom != null) {
                    this.mDrawableBottom.setAlpha(alphaMulitplied);
                }
                invalidate();
            }
        }

        @ViewDebug.ExportedProperty(category = "drawing")
        public float getAlpha() {
            return this.mAlpha;
        }

        /* access modifiers changed from: protected */
        public boolean onSetAlpha(int alpha) {
            return true;
        }

        public void setDrawableBottom(Drawable drawable) {
            this.mDrawableBottom = drawable;
            if (this.mAlpha >= 0.0f) {
                this.mDrawableBottom.setAlpha((int) (255.0f * this.mAlpha));
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.mDrawableBottom != null) {
                this.mDrawableBottom.setBounds(getPaddingLeft(), getHeight() - getPaddingBottom(), getWidth() - getPaddingRight(), (getHeight() - getPaddingBottom()) + this.mDrawableBottom.getIntrinsicHeight());
                this.mDrawableBottom.draw(canvas);
            }
        }
    }

    public FrameLayoutWithShadows(Context context) {
        this(context, (AttributeSet) null);
    }

    public FrameLayoutWithShadows(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameLayoutWithShadows(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.rect = new Rect();
        this.rectf = new RectF();
        this.mShadowsAlpha = 1.0f;
        this.mRecycleBin = new ArrayList<>(12);
        initFromAttributes(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        layoutShadows();
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FrameLayoutWithShadows);
            setDefaultShadowResourceId(a.getResourceId(0, 0));
            setDrawableBottomResourceId(a.getResourceId(1, 0));
            a.recycle();
        }
    }

    public void setDefaultShadowResourceId(int id) {
        this.mShadowResourceId = id;
    }

    public int getDefaultShadowResourceId() {
        return this.mShadowResourceId;
    }

    public void setDrawableBottomResourceId(int id) {
        this.mBottomResourceId = id;
    }

    public int getDrawableBottomResourceId() {
        return this.mBottomResourceId;
    }

    public void setShadowsAlpha(float alpha) {
        this.mShadowsAlpha = alpha;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View shadow = getChildAt(i);
            if (shadow instanceof ShadowView) {
                shadow.setAlpha(alpha);
            }
        }
    }

    private void prune() {
        if (getWindowToken() != null) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View shadow = getChildAt(i);
                if (shadow instanceof ShadowView) {
                    ShadowView shadowView = (ShadowView) shadow;
                    View view = shadowView.shadowedView;
                    if (this != findParentShadowsView(view)) {
                        view.setTag(R.id.ShadowView, (Object) null);
                        View unused = shadowView.shadowedView = null;
                        removeView(shadowView);
                        addToRecycleBin(shadowView);
                    }
                }
            }
        }
    }

    public void layoutShadows() {
        View view;
        prune();
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View shadow = getChildAt(i);
            if ((shadow instanceof ShadowView) && (view = ((ShadowView) shadow).shadowedView) != null && this == findParentShadowsView(view)) {
                boolean isImageMatrix = false;
                if (view instanceof ImageView) {
                    Matrix matrix = ((ImageView) view).getImageMatrix();
                    Drawable drawable = ((ImageView) view).getDrawable();
                    if (drawable != null) {
                        isImageMatrix = true;
                        this.rect.set(drawable.getBounds());
                        this.rectf.set(this.rect);
                        matrix.mapRect(this.rectf);
                        this.rectf.offset((float) view.getPaddingLeft(), (float) view.getPaddingTop());
                        this.rectf.intersect((float) view.getPaddingLeft(), (float) view.getPaddingTop(), (float) ((view.getWidth() - view.getPaddingLeft()) - view.getPaddingRight()), (float) ((view.getHeight() - view.getPaddingTop()) - view.getPaddingBottom()));
                        this.rectf.left -= (float) shadow.getPaddingLeft();
                        this.rectf.top -= (float) shadow.getPaddingTop();
                        this.rectf.right += (float) shadow.getPaddingRight();
                        this.rectf.bottom += (float) shadow.getPaddingBottom();
                        this.rect.left = (int) (this.rectf.left + 0.5f);
                        this.rect.top = (int) (this.rectf.top + 0.5f);
                        this.rect.right = (int) (this.rectf.right + 0.5f);
                        this.rect.bottom = (int) (this.rectf.bottom + 0.5f);
                    }
                }
                if (!isImageMatrix) {
                    this.rect.left = view.getPaddingLeft() - shadow.getPaddingLeft();
                    this.rect.top = view.getPaddingTop() - shadow.getPaddingTop();
                    this.rect.right = view.getWidth() + view.getPaddingRight() + shadow.getPaddingRight();
                    this.rect.bottom = view.getHeight() + view.getPaddingBottom() + shadow.getPaddingBottom();
                }
                offsetDescendantRectToMyCoords(view, this.rect);
                shadow.layout(this.rect.left, this.rect.top, this.rect.right, this.rect.bottom);
            }
        }
    }

    public View addShadowView(View view, Drawable shadow) {
        ShadowView shadowView = (ShadowView) view.getTag(R.id.ShadowView);
        if (shadowView == null) {
            shadowView = getFromRecycleBin();
            if (shadowView == null) {
                shadowView = new ShadowView(getContext());
                shadowView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
            }
            view.setTag(R.id.ShadowView, shadowView);
            View unused = shadowView.shadowedView = view;
            addView(shadowView, 0);
        }
        shadow.mutate();
        shadowView.setAlpha(this.mShadowsAlpha);
        shadowView.setBackground(shadow);
        if (this.mBottomResourceId != 0) {
            shadowView.setDrawableBottom(getContext().getDrawable(this.mBottomResourceId).mutate());
        }
        return shadowView;
    }

    public View addShadowView(View view) {
        if (this.mShadowResourceId != 0) {
            return addShadowView(view, getContext().getDrawable(this.mShadowResourceId));
        }
        return null;
    }

    public static View getShadowView(View view) {
        View shadowView = (View) view.getTag(R.id.ShadowView);
        if (shadowView != null) {
            return shadowView;
        }
        return null;
    }

    public void setShadowViewUnderline(View shadowView, int underlineColor, int heightInPx) {
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(new RectShape());
        drawable.setIntrinsicHeight(heightInPx);
        drawable.getPaint().setColor(underlineColor);
        ((ShadowView) shadowView).setDrawableBottom(drawable);
    }

    public void setShadowViewUnderline(View shadowView, Drawable drawable) {
        ((ShadowView) shadowView).setDrawableBottom(drawable);
    }

    public void bringViewShadowToTop(View view) {
        int index;
        int lastIndex;
        View shadowView = (View) view.getTag(R.id.ShadowView);
        if (shadowView != null && (index = indexOfChild(shadowView)) >= 0 && getChildCount() - 1 != index) {
            View lastShadowView = getChildAt(lastIndex);
            if (!(lastShadowView instanceof ShadowView)) {
                removeView(shadowView);
                addView(shadowView);
                return;
            }
            removeView(lastShadowView);
            removeView(shadowView);
            addView(lastShadowView, 0);
            addView(shadowView);
        }
    }

    public static void removeShadowView(View view) {
        ShadowView shadowView = (ShadowView) view.getTag(R.id.ShadowView);
        if (shadowView != null) {
            view.setTag(R.id.ShadowView, (Object) null);
            View unused = shadowView.shadowedView = null;
            if (shadowView.getRootView() != null) {
                ViewParent parent = shadowView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(shadowView);
                    if (parent instanceof FrameLayoutWithShadows) {
                        ((FrameLayoutWithShadows) parent).addToRecycleBin(shadowView);
                    }
                }
            }
        }
    }

    private void addToRecycleBin(ShadowView shadowView) {
        if (this.mRecycleBin.size() < 12) {
            this.mRecycleBin.add(shadowView);
        }
    }

    public ShadowView getFromRecycleBin() {
        int size = this.mRecycleBin.size();
        if (size <= 0) {
            return null;
        }
        this.mRecycleBin.remove(size - 1).init();
        return null;
    }

    public void setShadowVisibility(View view, int visibility) {
        View shadowView = (View) view.getTag(R.id.ShadowView);
        if (shadowView != null) {
            shadowView.setVisibility(visibility);
        }
    }

    public static FrameLayoutWithShadows findParentShadowsView(View view) {
        ViewParent nextView = view.getParent();
        while (nextView != null && !(nextView instanceof FrameLayoutWithShadows)) {
            nextView = nextView.getParent();
        }
        return (FrameLayoutWithShadows) nextView;
    }
}
