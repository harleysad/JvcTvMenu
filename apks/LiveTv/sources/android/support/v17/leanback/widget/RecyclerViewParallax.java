package android.support.v17.leanback.widget;

import android.support.v17.leanback.widget.Parallax;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

public class RecyclerViewParallax extends Parallax<ChildPositionProperty> {
    boolean mIsVertical;
    View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        public void onLayoutChange(View view, int l, int t, int r, int b, int oldL, int oldT, int oldR, int oldB) {
            RecyclerViewParallax.this.updateValues();
        }
    };
    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            RecyclerViewParallax.this.updateValues();
        }
    };
    RecyclerView mRecylerView;

    public static final class ChildPositionProperty extends Parallax.IntProperty {
        int mAdapterPosition;
        float mFraction;
        int mOffset;
        int mViewId;

        ChildPositionProperty(String name, int index) {
            super(name, index);
        }

        public ChildPositionProperty adapterPosition(int adapterPosition) {
            this.mAdapterPosition = adapterPosition;
            return this;
        }

        public ChildPositionProperty viewId(int viewId) {
            this.mViewId = viewId;
            return this;
        }

        public ChildPositionProperty offset(int offset) {
            this.mOffset = offset;
            return this;
        }

        public ChildPositionProperty fraction(float fraction) {
            this.mFraction = fraction;
            return this;
        }

        public int getAdapterPosition() {
            return this.mAdapterPosition;
        }

        public int getViewId() {
            return this.mViewId;
        }

        public int getOffset() {
            return this.mOffset;
        }

        public float getFraction() {
            return this.mFraction;
        }

        /* JADX WARNING: type inference failed for: r6v6, types: [android.view.ViewParent] */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateValue(android.support.v17.leanback.widget.RecyclerViewParallax r11) {
            /*
                r10 = this;
                android.support.v7.widget.RecyclerView r0 = r11.mRecylerView
                if (r0 != 0) goto L_0x0006
                r1 = 0
                goto L_0x000c
            L_0x0006:
                int r1 = r10.mAdapterPosition
                android.support.v7.widget.RecyclerView$ViewHolder r1 = r0.findViewHolderForAdapterPosition(r1)
            L_0x000c:
                r2 = 0
                if (r1 != 0) goto L_0x004e
                r3 = 2147483647(0x7fffffff, float:NaN)
                if (r0 == 0) goto L_0x0046
                android.support.v7.widget.RecyclerView$LayoutManager r4 = r0.getLayoutManager()
                int r4 = r4.getChildCount()
                if (r4 != 0) goto L_0x001f
                goto L_0x0046
            L_0x001f:
                android.support.v7.widget.RecyclerView$LayoutManager r4 = r0.getLayoutManager()
                android.view.View r2 = r4.getChildAt(r2)
                android.support.v7.widget.RecyclerView$ViewHolder r4 = r0.findContainingViewHolder(r2)
                int r5 = r4.getAdapterPosition()
                int r6 = r10.mAdapterPosition
                if (r5 >= r6) goto L_0x003b
                int r6 = r10.getIndex()
                r11.setIntPropertyValue(r6, r3)
                goto L_0x0044
            L_0x003b:
                int r3 = r10.getIndex()
                r6 = -2147483648(0xffffffff80000000, float:-0.0)
                r11.setIntPropertyValue(r3, r6)
            L_0x0044:
                goto L_0x00c4
            L_0x0046:
                int r2 = r10.getIndex()
                r11.setIntPropertyValue(r2, r3)
                return
            L_0x004e:
                android.view.View r3 = r1.itemView
                int r4 = r10.mViewId
                android.view.View r3 = r3.findViewById(r4)
                if (r3 != 0) goto L_0x0059
                return
            L_0x0059:
                android.graphics.Rect r4 = new android.graphics.Rect
                int r5 = r3.getWidth()
                int r6 = r3.getHeight()
                r4.<init>(r2, r2, r5, r6)
                r2 = r4
                r0.offsetDescendantRectToMyCoords(r3, r2)
                r4 = 0
                r5 = 0
            L_0x006c:
                if (r3 == r0) goto L_0x008e
                if (r3 == 0) goto L_0x008e
                android.view.ViewParent r6 = r3.getParent()
                if (r6 != r0) goto L_0x007c
                boolean r6 = r0.isAnimating()
                if (r6 != 0) goto L_0x0086
            L_0x007c:
                float r6 = r3.getTranslationX()
                float r4 = r4 + r6
                float r6 = r3.getTranslationY()
                float r5 = r5 + r6
            L_0x0086:
                android.view.ViewParent r6 = r3.getParent()
                r3 = r6
                android.view.View r3 = (android.view.View) r3
                goto L_0x006c
            L_0x008e:
                int r6 = (int) r4
                int r7 = (int) r5
                r2.offset(r6, r7)
                boolean r6 = r11.mIsVertical
                if (r6 == 0) goto L_0x00ae
                int r6 = r10.getIndex()
                int r7 = r2.top
                int r8 = r10.mOffset
                int r7 = r7 + r8
                float r8 = r10.mFraction
                int r9 = r2.height()
                float r9 = (float) r9
                float r8 = r8 * r9
                int r8 = (int) r8
                int r7 = r7 + r8
                r11.setIntPropertyValue(r6, r7)
                goto L_0x00c4
            L_0x00ae:
                int r6 = r10.getIndex()
                int r7 = r2.left
                int r8 = r10.mOffset
                int r7 = r7 + r8
                float r8 = r10.mFraction
                int r9 = r2.width()
                float r9 = (float) r9
                float r8 = r8 * r9
                int r8 = (int) r8
                int r7 = r7 + r8
                r11.setIntPropertyValue(r6, r7)
            L_0x00c4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.RecyclerViewParallax.ChildPositionProperty.updateValue(android.support.v17.leanback.widget.RecyclerViewParallax):void");
        }
    }

    public ChildPositionProperty createProperty(String name, int index) {
        return new ChildPositionProperty(name, index);
    }

    public float getMaxValue() {
        if (this.mRecylerView == null) {
            return 0.0f;
        }
        return (float) (this.mIsVertical ? this.mRecylerView.getHeight() : this.mRecylerView.getWidth());
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        if (this.mRecylerView != recyclerView) {
            if (this.mRecylerView != null) {
                this.mRecylerView.removeOnScrollListener(this.mOnScrollListener);
                this.mRecylerView.removeOnLayoutChangeListener(this.mOnLayoutChangeListener);
            }
            this.mRecylerView = recyclerView;
            if (this.mRecylerView != null) {
                this.mRecylerView.getLayoutManager();
                boolean z = false;
                if (RecyclerView.LayoutManager.getProperties(this.mRecylerView.getContext(), (AttributeSet) null, 0, 0).orientation == 1) {
                    z = true;
                }
                this.mIsVertical = z;
                this.mRecylerView.addOnScrollListener(this.mOnScrollListener);
                this.mRecylerView.addOnLayoutChangeListener(this.mOnLayoutChangeListener);
            }
        }
    }

    public void updateValues() {
        for (Property prop : getProperties()) {
            ((ChildPositionProperty) prop).updateValue(this);
        }
        super.updateValues();
    }

    public RecyclerView getRecyclerView() {
        return this.mRecylerView;
    }
}
