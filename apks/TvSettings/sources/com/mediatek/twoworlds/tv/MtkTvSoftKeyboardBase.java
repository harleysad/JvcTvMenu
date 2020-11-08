package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvSoftKeyboardBase {
    public static final int ACTION_REQUEST_HIDE = 1;
    public static final int ACTION_REQUEST_SHOW = 0;
    public static final int STATUS_CHANGE_FROM_HIDE_TO_SHOW = 2;
    public static final int STATUS_CHANGE_FROM_SHOW_TO_HIDE_BY_CANCEL_ACTION = 1;
    public static final int STATUS_CHANGE_FROM_SHOW_TO_HIDE_BY_CONFIRM_ACTION = 0;
    public static final String TAG = "MtkTvSoftKeyboard";
    private static OnSoftKeyboardActionListener mOnSoftKeyboardActionListener;

    public interface OnSoftKeyboardActionListener {
        void onHide();

        void onShow(int i);
    }

    public MtkTvSoftKeyboardBase() {
        TVNativeWrapper.softKeyboardInit_native();
    }

    public void notifySoftKeyboardStatusChange(int status) {
        Log.d(TAG, "notifySoftKeyboardStatusChange\n");
        TVNativeWrapper.notifySoftKeyboardStatusChange_native(status);
    }

    public void notifyTextChange(String text) {
        Log.d(TAG, "notifyTextChange\n");
        TVNativeWrapper.notifyTextChange_native(text);
    }

    public void setOnSoftKeyboardActionListener(OnSoftKeyboardActionListener l) {
        mOnSoftKeyboardActionListener = l;
    }

    public boolean hasOnClickListeners() {
        return mOnSoftKeyboardActionListener != null;
    }

    public static void receiveSoftKeyboardActionRequest(int action, int arg) {
        Log.d(TAG, "receiveSoftKeyboardActionRequest " + action + "\n");
        if (action == 0 && mOnSoftKeyboardActionListener != null) {
            mOnSoftKeyboardActionListener.onShow(arg);
        }
        if (action == 1 && mOnSoftKeyboardActionListener != null) {
            mOnSoftKeyboardActionListener.onHide();
        }
    }
}
