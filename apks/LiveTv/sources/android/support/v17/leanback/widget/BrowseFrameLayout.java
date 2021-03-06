package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

public class BrowseFrameLayout extends FrameLayout {
    private OnFocusSearchListener mListener;
    private OnChildFocusListener mOnChildFocusListener;
    private View.OnKeyListener mOnDispatchKeyListener;

    public interface OnChildFocusListener {
        void onRequestChildFocus(View view, View view2);

        boolean onRequestFocusInDescendants(int i, Rect rect);
    }

    public interface OnFocusSearchListener {
        View onFocusSearch(View view, int i);
    }

    public BrowseFrameLayout(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public BrowseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrowseFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnFocusSearchListener(OnFocusSearchListener listener) {
        this.mListener = listener;
    }

    public OnFocusSearchListener getOnFocusSearchListener() {
        return this.mListener;
    }

    public void setOnChildFocusListener(OnChildFocusListener listener) {
        this.mOnChildFocusListener = listener;
    }

    public OnChildFocusListener getOnChildFocusListener() {
        return this.mOnChildFocusListener;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (this.mOnChildFocusListener == null || !this.mOnChildFocusListener.onRequestFocusInDescendants(direction, previouslyFocusedRect)) {
            return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
        }
        return true;
    }

    public View focusSearch(View focused, int direction) {
        View view;
        if (this.mListener == null || (view = this.mListener.onFocusSearch(focused, direction)) == null) {
            return super.focusSearch(focused, direction);
        }
        return view;
    }

    public void requestChildFocus(View child, View focused) {
        if (this.mOnChildFocusListener != null) {
            this.mOnChildFocusListener.onRequestChildFocus(child, focused);
        }
        super.requestChildFocus(child, focused);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean consumed = super.dispatchKeyEvent(event);
        if (this.mOnDispatchKeyListener == null || consumed) {
            return consumed;
        }
        return this.mOnDispatchKeyListener.onKey(getRootView(), event.getKeyCode(), event);
    }

    public void setOnDispatchKeyListener(View.OnKeyListener listener) {
        this.mOnDispatchKeyListener = listener;
    }
}
