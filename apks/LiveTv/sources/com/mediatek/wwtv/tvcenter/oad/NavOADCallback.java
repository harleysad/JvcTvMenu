package com.mediatek.wwtv.tvcenter.oad;

import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;

public class NavOADCallback extends MtkTvTVCallbackHandler {
    public static NavOADCallback mNavOADCallback;
    private NavOADActivity mActivity;

    public static NavOADCallback getInstance(NavOADActivity activity) {
        if (mNavOADCallback != null) {
            mNavOADCallback.removeListener();
        }
        mNavOADCallback = new NavOADCallback();
        mNavOADCallback.setActivity(activity);
        return mNavOADCallback;
    }

    public int notifyOADMessage(int messageType, String scheduleInfo, int progress, boolean autoDld, int argv5) throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putInt("arg1", messageType);
        bundle.putString("arg2", scheduleInfo);
        bundle.putInt("arg3", progress);
        bundle.putBoolean("arg4", autoDld);
        bundle.putInt("arg5", argv5);
        Message msg = Message.obtain();
        msg.setData(bundle);
        msg.what = 1;
        if (getActivity() == null || getActivity().isDestroyed()) {
            return -1;
        }
        getActivity().getmHandler().sendMessage(msg);
        return super.notifyOADMessage(messageType, scheduleInfo, progress, autoDld, argv5);
    }

    public NavOADActivity getActivity() {
        return this.mActivity;
    }

    public void setActivity(NavOADActivity mActivity2) {
        this.mActivity = mActivity2;
    }
}
