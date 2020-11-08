package com.mediatek.wwtv.setting.widget.view;

public class RefcountObject<T> {
    private T mObject;
    private int mRefcount;
    private RefcountListener mRefcountListener;

    public interface RefcountListener {
        void onRefcountZero(RefcountObject<?> refcountObject);
    }

    public RefcountObject(T object) {
        this.mObject = object;
    }

    public void setRefcountListener(RefcountListener listener) {
        this.mRefcountListener = listener;
    }

    public int addRef() {
        this.mRefcount++;
        return this.mRefcount;
    }

    public int releaseRef() {
        this.mRefcount--;
        if (this.mRefcount == 0 && this.mRefcountListener != null) {
            this.mRefcountListener.onRefcountZero(this);
        }
        return this.mRefcount;
    }

    public int getRef() {
        return this.mRefcount;
    }

    public T getObject() {
        return this.mObject;
    }
}
