package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class ScrollController {
    public static final int OPERATION_AUTO = 3;
    public static final int OPERATION_DISABLE = 0;
    public static final int OPERATION_NOTOUCH = 1;
    public static final int OPERATION_TOUCH = 2;
    public static final int SCROLL_CENTER_FIXED = 1;
    public static final int SCROLL_CENTER_FIXED_PERCENT = 2;
    public static final int SCROLL_CENTER_FIXED_TO_END = 3;
    public static final int SCROLL_CENTER_IN_MIDDLE = 0;
    private static final int SCROLL_DURATION_MAX = 1500;
    private static final int SCROLL_DURATION_MIN = 250;
    private static final float SCROLL_DURATION_MS_PER_PIX = 0.25f;
    private static final int SCROLL_DURATION_PAGE_MIN = 250;
    public static final int SCROLL_ITEM_ALIGN_CENTER = 0;
    public static final int SCROLL_ITEM_ALIGN_HIGH = 2;
    public static final int SCROLL_ITEM_ALIGN_LOW = 1;
    private static final int STATE_DRAG = 3;
    private static final int STATE_FLING = 1;
    private static final int STATE_NONE = 0;
    private static final int STATE_SCROLL = 2;
    public final Axis horizontal = new Axis(this.mLerper, "horizontal");
    private Context mContext;
    private int mDragMode = 3;
    private int mFlingMode = 3;
    private Scroller mFlingScroller;
    private boolean mHorizontalForward = true;
    private Lerper mLerper = new Lerper();
    private Axis mMainAxis = this.horizontal;
    private boolean mMainHorizontal;
    private int mOrientation = 0;
    private int mScrollMode = 1;
    private Scroller mScrollScroller;
    private Axis mSecondAxis = this.vertical;
    private int mState = 0;
    private boolean mVerticalForward = true;
    public final Axis vertical = new Axis(this.mLerper, "vertical");

    public static class Axis {
        private int mAlignExtraOffset;
        /* access modifiers changed from: private */
        public float mDragOffset;
        private int mExpandedSize;
        private int mExtraSpaceHigh;
        private int mExtraSpaceLow;
        private Lerper mLerper;
        private int mMaxEdge;
        private int mMinEdge;
        private String mName;
        private int mOperationMode = 1;
        private int mPaddingHigh;
        private int mPaddingLow;
        /* access modifiers changed from: private */
        public float mScrollCenter;
        private int mScrollCenterOffset = -1;
        private float mScrollCenterOffsetPercent = -1.0f;
        private int mScrollCenterStrategy = 0;
        private int mScrollItemAlign = 0;
        private int mScrollMax;
        private int mScrollMin;
        private boolean mSelectedTakesMoreSpace = false;
        private int mSize;
        private int mTouchScrollMax;
        private int mTouchScrollMin;

        public static class Item {
            private int mCenter;
            private int mHigh;
            private int mIndex = -1;
            private int mLow;

            public final int getLow() {
                return this.mLow;
            }

            public final int getHigh() {
                return this.mHigh;
            }

            public final int getCenter() {
                return this.mCenter;
            }

            public final int getIndex() {
                return this.mIndex;
            }

            public final void setValue(int index, int low, int high) {
                this.mIndex = index;
                this.mLow = low;
                this.mHigh = high;
                this.mCenter = (low + high) / 2;
            }

            public final boolean isValid() {
                return this.mIndex >= 0;
            }

            public final String toString() {
                return this.mIndex + "[" + this.mLow + "," + this.mHigh + "]";
            }
        }

        public Axis(Lerper lerper, String name) {
            this.mLerper = lerper;
            reset();
            this.mName = name;
        }

        public final int getScrollCenterStrategy() {
            return this.mScrollCenterStrategy;
        }

        public final void setScrollCenterStrategy(int scrollCenterStrategy) {
            this.mScrollCenterStrategy = scrollCenterStrategy;
        }

        public final int getScrollCenterOffset() {
            return this.mScrollCenterOffset;
        }

        public final void setScrollCenterOffset(int scrollCenterOffset) {
            this.mScrollCenterOffset = scrollCenterOffset;
        }

        public final void setScrollCenterOffsetPercent(int scrollCenterOffsetPercent) {
            if (scrollCenterOffsetPercent < 0) {
                scrollCenterOffsetPercent = 0;
            } else if (scrollCenterOffsetPercent > 100) {
                scrollCenterOffsetPercent = 100;
            }
            this.mScrollCenterOffsetPercent = ((float) scrollCenterOffsetPercent) / 100.0f;
        }

        public final void setSelectedTakesMoreSpace(boolean selectedTakesMoreSpace) {
            this.mSelectedTakesMoreSpace = selectedTakesMoreSpace;
        }

        public final boolean getSelectedTakesMoreSpace() {
            return this.mSelectedTakesMoreSpace;
        }

        public final void setScrollItemAlign(int align) {
            this.mScrollItemAlign = align;
        }

        public final int getScrollItemAlign() {
            return this.mScrollItemAlign;
        }

        public final int getScrollCenter() {
            return (int) this.mScrollCenter;
        }

        public final void setOperationMode(int mode) {
            this.mOperationMode = mode;
        }

        private int scrollMin() {
            return this.mOperationMode == 2 ? this.mTouchScrollMin : this.mScrollMin;
        }

        private int scrollMax() {
            return this.mOperationMode == 2 ? this.mTouchScrollMax : this.mScrollMax;
        }

        public final void updateScrollMin(int scrollMin, int minEdge) {
            this.mScrollMin = scrollMin;
            if (this.mScrollCenter < ((float) this.mScrollMin)) {
                this.mScrollCenter = (float) this.mScrollMin;
            }
            this.mMinEdge = minEdge;
            if (this.mScrollCenterStrategy != 0 || this.mScrollMin == Integer.MIN_VALUE) {
                this.mTouchScrollMin = this.mScrollMin;
            } else {
                this.mTouchScrollMin = Math.max(this.mScrollMin, this.mMinEdge + (this.mSize / 2));
            }
        }

        public void invalidateScrollMin() {
            this.mScrollMin = Integer.MIN_VALUE;
            this.mMinEdge = Integer.MIN_VALUE;
            this.mTouchScrollMin = Integer.MIN_VALUE;
        }

        public final void updateScrollMax(int scrollMax, int maxEdge) {
            this.mScrollMax = scrollMax;
            if (this.mScrollCenter > ((float) this.mScrollMax)) {
                this.mScrollCenter = (float) this.mScrollMax;
            }
            this.mMaxEdge = maxEdge;
            if (this.mScrollCenterStrategy != 0 || this.mScrollMax == Integer.MAX_VALUE) {
                this.mTouchScrollMax = this.mScrollMax;
            } else {
                this.mTouchScrollMax = Math.min(this.mScrollMax, this.mMaxEdge - (this.mSize / 2));
            }
        }

        public void invalidateScrollMax() {
            this.mScrollMax = Integer.MAX_VALUE;
            this.mMaxEdge = Integer.MAX_VALUE;
            this.mTouchScrollMax = Integer.MAX_VALUE;
        }

        public final boolean canScroll(boolean forward) {
            if (forward) {
                if (this.mScrollCenter >= ((float) this.mScrollMax)) {
                    return false;
                }
                return true;
            } else if (this.mScrollCenter <= ((float) this.mScrollMin)) {
                return false;
            } else {
                return true;
            }
        }

        /* access modifiers changed from: private */
        public boolean updateScrollCenter(float scrollTarget, boolean lerper) {
            this.mDragOffset = 0.0f;
            int scrollMin = scrollMin();
            int scrollMax = scrollMax();
            boolean overScroll = false;
            if (scrollMin >= scrollMax) {
                scrollTarget = this.mScrollCenter;
                overScroll = true;
            } else if (scrollTarget < ((float) scrollMin)) {
                scrollTarget = (float) scrollMin;
                overScroll = true;
            } else if (scrollTarget > ((float) scrollMax)) {
                scrollTarget = (float) scrollMax;
                overScroll = true;
            }
            if (lerper) {
                this.mScrollCenter = this.mLerper.getValue(this.mScrollCenter, scrollTarget);
            } else {
                this.mScrollCenter = scrollTarget;
            }
            return overScroll;
        }

        /* access modifiers changed from: private */
        public void updateFromDrag() {
            updateScrollCenter(this.mScrollCenter + this.mDragOffset, false);
        }

        /* access modifiers changed from: private */
        public void dragBy(float distanceX) {
            this.mDragOffset += distanceX;
        }

        /* access modifiers changed from: private */
        public void reset() {
            this.mScrollCenter = -2.14748365E9f;
            this.mScrollMin = Integer.MIN_VALUE;
            this.mMinEdge = Integer.MIN_VALUE;
            this.mTouchScrollMin = Integer.MIN_VALUE;
            this.mScrollMax = Integer.MAX_VALUE;
            this.mMaxEdge = Integer.MAX_VALUE;
            this.mTouchScrollMax = Integer.MAX_VALUE;
            this.mExpandedSize = 0;
            this.mDragOffset = 0.0f;
        }

        public final boolean isMinUnknown() {
            return this.mScrollMin == Integer.MIN_VALUE;
        }

        public final boolean isMaxUnknown() {
            return this.mScrollMax == Integer.MAX_VALUE;
        }

        public final int getSizeForExpandableItem() {
            return ((this.mSize - this.mPaddingLow) - this.mPaddingHigh) - this.mExpandedSize;
        }

        public final void setSize(int size) {
            this.mSize = size;
        }

        public final void setExpandedSize(int expandedSize) {
            this.mExpandedSize = expandedSize;
        }

        public final void setExtraSpaceLow(int extraSpaceLow) {
            this.mExtraSpaceLow = extraSpaceLow;
        }

        public final void setExtraSpaceHigh(int extraSpaceHigh) {
            this.mExtraSpaceHigh = extraSpaceHigh;
        }

        public final void setAlignExtraOffset(int extraOffset) {
            this.mAlignExtraOffset = extraOffset;
        }

        public final void setPadding(int paddingLow, int paddingHigh) {
            this.mPaddingLow = paddingLow;
            this.mPaddingHigh = paddingHigh;
        }

        public final int getPaddingLow() {
            return this.mPaddingLow;
        }

        public final int getPaddingHigh() {
            return this.mPaddingHigh;
        }

        public final int getSystemScrollPos() {
            return getSystemScrollPos((int) this.mScrollCenter);
        }

        public final int getSystemScrollPos(int scrollCenter) {
            int middlePosition;
            int shift;
            int extraSpaceLow;
            int scrollCenter2 = scrollCenter + this.mAlignExtraOffset;
            int compensate = this.mSelectedTakesMoreSpace ? this.mExtraSpaceLow : -this.mExtraSpaceLow;
            if (this.mScrollCenterStrategy == 1) {
                return (scrollCenter2 - this.mScrollCenterOffset) + compensate;
            }
            if (this.mScrollCenterStrategy == 3) {
                return (scrollCenter2 - (this.mSize - this.mScrollCenterOffset)) + compensate;
            }
            if (this.mScrollCenterStrategy == 2) {
                return ((int) (((float) (scrollCenter2 - this.mScrollCenterOffset)) - (((float) this.mSize) * this.mScrollCenterOffsetPercent))) + compensate;
            }
            int clientSize = (this.mSize - this.mPaddingLow) - this.mPaddingHigh;
            if (this.mScrollCenterOffset >= 0) {
                middlePosition = this.mScrollCenterOffset - this.mPaddingLow;
            } else if (this.mScrollCenterOffsetPercent >= 0.0f) {
                middlePosition = ((int) (((float) this.mSize) * this.mScrollCenterOffsetPercent)) - this.mPaddingLow;
            } else {
                middlePosition = clientSize / 2;
            }
            int afterMiddlePosition = clientSize - middlePosition;
            boolean isMinUnknown = isMinUnknown();
            boolean isMaxUnknown = isMaxUnknown();
            if (this.mSelectedTakesMoreSpace) {
                switch (getScrollItemAlign()) {
                    case 1:
                        extraSpaceLow = 0;
                        break;
                    case 2:
                        extraSpaceLow = this.mExtraSpaceLow + this.mExtraSpaceHigh;
                        break;
                    default:
                        extraSpaceLow = this.mExtraSpaceLow;
                        break;
                }
                if (!isMinUnknown && !isMaxUnknown && (this.mMaxEdge - this.mMinEdge) + this.mExpandedSize <= clientSize) {
                    return this.mMinEdge - this.mPaddingLow;
                }
                if (!isMinUnknown && (scrollCenter2 - this.mMinEdge) + extraSpaceLow <= middlePosition) {
                    return this.mMinEdge - this.mPaddingLow;
                }
                if (!isMaxUnknown) {
                    if ((this.mMaxEdge - scrollCenter2) + (this.mExpandedSize - extraSpaceLow) <= afterMiddlePosition) {
                        return (this.mMaxEdge - this.mPaddingLow) - (clientSize - this.mExpandedSize);
                    }
                }
                return ((scrollCenter2 - middlePosition) - this.mPaddingLow) + extraSpaceLow;
            }
            switch (getScrollItemAlign()) {
                case 1:
                    shift = -this.mExtraSpaceLow;
                    break;
                case 2:
                    shift = this.mExtraSpaceHigh;
                    break;
                default:
                    shift = 0;
                    break;
            }
            if (!isMinUnknown && !isMaxUnknown && (this.mMaxEdge - this.mMinEdge) + this.mExpandedSize <= clientSize) {
                return this.mMinEdge - this.mPaddingLow;
            }
            if (!isMinUnknown && (scrollCenter2 + shift) - this.mMinEdge <= middlePosition) {
                return this.mMinEdge - this.mPaddingLow;
            }
            if (isMaxUnknown || ((this.mMaxEdge - scrollCenter2) - shift) + this.mExpandedSize > afterMiddlePosition) {
                return ((scrollCenter2 - middlePosition) - this.mPaddingLow) + shift;
            }
            return (this.mMaxEdge - this.mPaddingLow) - (clientSize - this.mExpandedSize);
        }

        public String toString() {
            return "center: " + this.mScrollCenter + " min:" + this.mMinEdge + "," + this.mScrollMin + " max:" + this.mScrollMax + "," + this.mMaxEdge;
        }
    }

    public final Lerper lerper() {
        return this.mLerper;
    }

    public final Axis mainAxis() {
        return this.mMainAxis;
    }

    public final Axis secondAxis() {
        return this.mSecondAxis;
    }

    public final void setLerperDivisor(float divisor) {
        this.mLerper.setDivisor(divisor);
    }

    public ScrollController(Context context) {
        this.mContext = context;
        this.mScrollScroller = new Scroller(this.mContext, new DecelerateInterpolator(2.0f));
        this.mFlingScroller = new Scroller(this.mContext, new LinearInterpolator());
    }

    public final void setOrientation(int orientation) {
        int align = mainAxis().getScrollItemAlign();
        boolean selectedTakesMoreSpace = mainAxis().getSelectedTakesMoreSpace();
        this.mOrientation = orientation;
        if (this.mOrientation == 0) {
            this.mMainAxis = this.horizontal;
            this.mSecondAxis = this.vertical;
        } else {
            this.mMainAxis = this.vertical;
            this.mSecondAxis = this.horizontal;
        }
        this.mMainAxis.setScrollItemAlign(align);
        this.mSecondAxis.setScrollItemAlign(0);
        this.mMainAxis.setSelectedTakesMoreSpace(selectedTakesMoreSpace);
        this.mSecondAxis.setSelectedTakesMoreSpace(false);
    }

    public void setScrollItemAlign(int align) {
        mainAxis().setScrollItemAlign(align);
    }

    public int getScrollItemAlign() {
        return mainAxis().getScrollItemAlign();
    }

    public final int getOrientation() {
        return this.mOrientation;
    }

    public final int getFlingMode() {
        return this.mFlingMode;
    }

    public final void setFlingMode(int mode) {
        this.mFlingMode = mode;
    }

    public final int getDragMode() {
        return this.mDragMode;
    }

    public final void setDragMode(int mode) {
        this.mDragMode = mode;
    }

    public final int getScrollMode() {
        return this.mScrollMode;
    }

    public final void setScrollMode(int mode) {
        this.mScrollMode = mode;
    }

    public final float getCurrVelocity() {
        if (this.mState == 1) {
            return this.mFlingScroller.getCurrVelocity();
        }
        if (this.mState == 2) {
            return this.mScrollScroller.getCurrVelocity();
        }
        return 0.0f;
    }

    public final boolean canScroll(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }
        if (dx != 0) {
            if (!this.horizontal.canScroll(dx < 0)) {
                return false;
            }
        }
        if (dy != 0) {
            if (!this.vertical.canScroll(dy < 0)) {
                return false;
            }
        }
        return true;
    }

    private int getMode(int mode) {
        if (mode != 3) {
            return mode;
        }
        if (this.mContext.getResources().getConfiguration().touchscreen == 1) {
            return 1;
        }
        return 2;
    }

    private void updateDirection(float dx, float dy) {
        this.mMainHorizontal = Math.abs(dx) >= Math.abs(dy);
        if (dx > 0.0f) {
            this.mHorizontalForward = true;
        } else if (dx < 0.0f) {
            this.mHorizontalForward = false;
        }
        if (dy > 0.0f) {
            this.mVerticalForward = true;
        } else if (dy < 0.0f) {
            this.mVerticalForward = false;
        }
    }

    public final boolean fling(int velocity_x, int velocity_y) {
        if (this.mFlingMode == 0) {
            return false;
        }
        int operationMode = getMode(this.mFlingMode);
        this.horizontal.setOperationMode(operationMode);
        this.vertical.setOperationMode(operationMode);
        this.mState = 1;
        this.mFlingScroller.fling((int) this.horizontal.mScrollCenter, (int) this.vertical.mScrollCenter, velocity_x, velocity_y, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        updateDirection((float) velocity_x, (float) velocity_y);
        return true;
    }

    public final void startScroll(int dx, int dy, boolean easeFling, int duration, boolean page) {
        Scroller scroller;
        int basey;
        int basex;
        int dy2;
        int dx2;
        int duration2;
        int duration3;
        if (this.mScrollMode != 0) {
            int operationMode = getMode(this.mScrollMode);
            this.horizontal.setOperationMode(operationMode);
            this.vertical.setOperationMode(operationMode);
            if (easeFling) {
                this.mState = 1;
                scroller = this.mFlingScroller;
            } else {
                this.mState = 2;
                scroller = this.mScrollScroller;
            }
            int basex2 = this.horizontal.getScrollCenter();
            int basey2 = this.vertical.getScrollCenter();
            if (!scroller.isFinished()) {
                int dx3 = (basex2 + dx) - scroller.getCurrX();
                basex = scroller.getCurrX();
                basey = scroller.getCurrY();
                dx2 = dx3;
                dy2 = (basey2 + dy) - scroller.getCurrY();
            } else {
                dx2 = dx;
                dy2 = dy;
                basex = basex2;
                basey = basey2;
            }
            updateDirection((float) dx2, (float) dy2);
            if (easeFling) {
                float curDx = (float) Math.abs(this.mFlingScroller.getFinalX() - this.mFlingScroller.getStartX());
                float curDy = (float) Math.abs(this.mFlingScroller.getFinalY() - this.mFlingScroller.getStartY());
                float hyp = (float) Math.sqrt((double) ((curDx * curDx) + (curDy * curDy)));
                float velocity = this.mFlingScroller.getCurrVelocity();
                float velocityX = (velocity * curDx) / hyp;
                float velocityY = (velocity * curDy) / hyp;
                int i = 0;
                int durationX = velocityX == 0.0f ? 0 : (int) (((float) (Math.abs(dx2) * 1000)) / velocityX);
                if (velocityY != 0.0f) {
                    i = (int) (((float) (Math.abs(dy2) * 1000)) / velocityY);
                }
                int durationY = i;
                if (duration == 0) {
                    duration3 = Math.max(durationX, durationY);
                } else {
                    duration3 = duration;
                }
                if (duration3 == 0) {
                    duration3 = 1;
                }
                boolean z = page;
                duration2 = duration3;
            } else {
                if (duration == 0) {
                    duration2 = getScrollDuration((int) Math.sqrt((double) ((dx2 * dx2) + (dy2 * dy2))), page);
                } else {
                    boolean z2 = page;
                    duration2 = duration;
                }
                if (duration2 == 0) {
                    duration2 = 1;
                }
            }
            scroller.startScroll(basex, basey, dx2, dy2, duration2);
        }
    }

    public final int getCurrentAnimationDuration() {
        Scroller scroller;
        if (this.mState == 1) {
            scroller = this.mFlingScroller;
        } else if (this.mState != 2) {
            return 0;
        } else {
            scroller = this.mScrollScroller;
        }
        return scroller.getDuration();
    }

    public final void startScrollByMain(int deltaMain, int deltaSecond, boolean easeFling, int duration, boolean page) {
        int dy;
        int dx;
        if (this.mOrientation == 0) {
            dx = deltaMain;
            dy = deltaSecond;
        } else {
            dx = deltaSecond;
            dy = deltaMain;
        }
        startScroll(dx, dy, easeFling, duration, page);
    }

    public final boolean dragBy(float distanceX, float distanceY) {
        if (this.mDragMode == 0) {
            return false;
        }
        int operationMode = getMode(this.mDragMode);
        this.horizontal.setOperationMode(operationMode);
        this.vertical.setOperationMode(operationMode);
        this.horizontal.dragBy(distanceX);
        this.vertical.dragBy(distanceY);
        this.mState = 3;
        return true;
    }

    public final void stopDrag() {
        this.mState = 0;
        float unused = this.vertical.mDragOffset = 0.0f;
        float unused2 = this.horizontal.mDragOffset = 0.0f;
    }

    public final void setScrollCenterByMain(int centerMain, int centerSecond) {
        if (this.mOrientation == 0) {
            setScrollCenter(centerMain, centerSecond);
        } else {
            setScrollCenter(centerSecond, centerMain);
        }
    }

    public final void setScrollCenter(int centerX, int centerY) {
        boolean unused = this.horizontal.updateScrollCenter((float) centerX, false);
        boolean unused2 = this.vertical.updateScrollCenter((float) centerY, false);
        int centerX2 = this.horizontal.getScrollCenter();
        int centerY2 = this.vertical.getScrollCenter();
        this.mFlingScroller.setFinalX(centerX2);
        this.mFlingScroller.setFinalY(centerY2);
        this.mFlingScroller.abortAnimation();
        this.mScrollScroller.setFinalX(centerX2);
        this.mScrollScroller.setFinalY(centerY2);
        this.mScrollScroller.abortAnimation();
    }

    public final int getFinalX() {
        if (this.mState == 1) {
            return this.mFlingScroller.getFinalX();
        }
        if (this.mState == 2) {
            return this.mScrollScroller.getFinalX();
        }
        return this.horizontal.getScrollCenter();
    }

    public final int getFinalY() {
        if (this.mState == 1) {
            return this.mFlingScroller.getFinalY();
        }
        if (this.mState == 2) {
            return this.mScrollScroller.getFinalY();
        }
        return this.vertical.getScrollCenter();
    }

    public final void setFinalX(int finalX) {
        if (this.mState == 1) {
            this.mFlingScroller.setFinalX(finalX);
        } else if (this.mState == 2) {
            this.mScrollScroller.setFinalX(finalX);
        }
    }

    public final void setFinalY(int finalY) {
        if (this.mState == 1) {
            this.mFlingScroller.setFinalY(finalY);
        } else if (this.mState == 2) {
            this.mScrollScroller.setFinalY(finalY);
        }
    }

    public final boolean isFinished() {
        Scroller scroller;
        if (this.mState == 1) {
            scroller = this.mFlingScroller;
        } else if (this.mState != 2) {
            return this.mState != 3;
        } else {
            scroller = this.mScrollScroller;
        }
        if (scroller.isFinished() && this.horizontal.getScrollCenter() == scroller.getCurrX() && this.vertical.getScrollCenter() == scroller.getCurrY()) {
            return true;
        }
        return false;
    }

    public final boolean isMainAxisMovingForward() {
        return this.mOrientation == 0 ? this.mHorizontalForward : this.mVerticalForward;
    }

    public final boolean isSecondAxisMovingForward() {
        return this.mOrientation == 0 ? this.mVerticalForward : this.mHorizontalForward;
    }

    public final int getLastDirection() {
        return this.mMainHorizontal ? this.mHorizontalForward ? 66 : 17 : this.mVerticalForward ? 130 : 33;
    }

    public final void computeAndSetScrollPosition() {
        Scroller scroller;
        if (this.mState == 1) {
            scroller = this.mFlingScroller;
        } else if (this.mState == 2) {
            scroller = this.mScrollScroller;
        } else if (this.mState != 3) {
            return;
        } else {
            if (this.horizontal.mDragOffset != 0.0f || this.vertical.mDragOffset != 0.0f) {
                this.horizontal.updateFromDrag();
                this.vertical.updateFromDrag();
                return;
            }
            return;
        }
        if (!isFinished()) {
            scroller.computeScrollOffset();
            boolean unused = this.horizontal.updateScrollCenter((float) scroller.getCurrX(), true);
            boolean unused2 = this.vertical.updateScrollCenter((float) scroller.getCurrY(), true);
        }
    }

    public final int getScrollDuration(int distance, boolean isPage) {
        int ms = (int) (((float) distance) * SCROLL_DURATION_MS_PER_PIX);
        if (ms < 250) {
            return 250;
        }
        if (ms > SCROLL_DURATION_MAX) {
            return SCROLL_DURATION_MAX;
        }
        return ms;
    }

    public final void reset() {
        mainAxis().reset();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("horizontal=");
        stringBuffer.append(this.horizontal.toString());
        stringBuffer.append("vertical=");
        stringBuffer.append(this.vertical.toString());
        return stringBuffer.toString();
    }
}
