package com.mediatek.wwtv.tvcenter.nav.util;

import android.os.Message;
import android.view.KeyEvent;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import java.util.ArrayList;
import java.util.List;

public class ComponentsManager {
    private static final String TAG = "ComponentsManager";
    private static boolean isFromNative = false;
    /* access modifiers changed from: private */
    public static int mCurrentAndroidCompId = 0;
    /* access modifiers changed from: private */
    public static int mCurrentNativeCompId = 0;
    private static ComponentsManager mNavComponentsManager = null;
    private KeyDispatch mKeyDispatch;
    private List<NavBasicDialog> mNavDialogs;
    private List<NavBasicMisc> mNavMiscs;
    private List<NavBasicView> mNavViews;

    private ComponentsManager() {
        this.mNavDialogs = null;
        this.mNavViews = null;
        this.mNavMiscs = null;
        this.mNavDialogs = new ArrayList();
        this.mNavViews = new ArrayList();
        this.mNavMiscs = new ArrayList();
        this.mKeyDispatch = KeyDispatch.getInstance();
    }

    public static synchronized ComponentsManager getInstance() {
        ComponentsManager componentsManager;
        synchronized (ComponentsManager.class) {
            if (mNavComponentsManager == null) {
                mNavComponentsManager = new ComponentsManager();
            }
            componentsManager = mNavComponentsManager;
        }
        return componentsManager;
    }

    public NavBasic getComponentById(int compId) {
        int i = 0;
        while (i < this.mNavDialogs.size()) {
            try {
                if (this.mNavDialogs.get(i).getComponentID() == compId) {
                    return this.mNavDialogs.get(i);
                }
                i++;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        for (int i2 = 0; i2 < this.mNavViews.size(); i2++) {
            if (this.mNavViews.get(i2).getComponentID() == compId) {
                return this.mNavViews.get(i2);
            }
        }
        for (int i3 = 0; i3 < this.mNavMiscs.size(); i3++) {
            if (this.mNavMiscs.get(i3).getComponentID() == compId) {
                return this.mNavMiscs.get(i3);
            }
        }
        return null;
    }

    public void clear() {
        if (this.mNavDialogs != null) {
            this.mNavDialogs.clear();
        } else {
            this.mNavDialogs = new ArrayList();
        }
        if (this.mNavViews != null) {
            this.mNavViews.clear();
        } else {
            this.mNavViews = new ArrayList();
        }
        if (this.mNavMiscs != null) {
            this.mNavMiscs.clear();
        } else {
            this.mNavMiscs = new ArrayList();
        }
    }

    public void addDialog(NavBasicDialog mNavBasicDialog) {
        this.mNavDialogs.add(mNavBasicDialog);
        MtkLog.v(TAG, "mNavDialogs size:" + this.mNavDialogs.size() + "mNavBasicDialog object:" + mNavBasicDialog);
    }

    public void addView(NavBasicView mNavBasicView) {
        this.mNavViews.add(mNavBasicView);
        MtkLog.v(TAG, "mNavViews size:" + this.mNavViews.size() + "mNavBasicView object:" + mNavBasicView);
    }

    public void addMisc(NavBasicMisc mNavMisc) {
        this.mNavMiscs.add(mNavMisc);
        MtkLog.v(TAG, "mNavMiscs size:" + this.mNavMiscs.size() + "NavBasicMisc object:" + mNavMisc);
    }

    public boolean isCompsDestroyed() {
        if (this.mNavDialogs != null && this.mNavDialogs.size() > 0) {
            return false;
        }
        if (this.mNavViews != null && this.mNavViews.size() > 0) {
            return false;
        }
        if (this.mNavMiscs == null || this.mNavMiscs.size() <= 0) {
            return true;
        }
        return false;
    }

    private NavBasicDialog isDialogKeyHandle(int keyCode) {
        try {
            int size = this.mNavDialogs.size();
            for (int i = 0; i < size; i++) {
                if (this.mNavDialogs.get(i).isKeyHandler(keyCode)) {
                    return this.mNavDialogs.get(i);
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private NavBasicView isViewKeyHandle(int keyCode) {
        try {
            int size = this.mNavViews.size();
            for (int i = 0; i < size; i++) {
                if (this.mNavViews.get(i).isKeyHandler(keyCode)) {
                    return this.mNavViews.get(i);
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private NavBasicMisc isMiscKeyHandle(int keyCode) {
        try {
            int size = this.mNavMiscs.size();
            for (int i = 0; i < size; i++) {
                if (this.mNavMiscs.get(i).isKeyHandler(keyCode)) {
                    return this.mNavMiscs.get(i);
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean hideUnCoExistComponent(NavBasic basic) {
        try {
            int size = this.mNavDialogs.size();
            for (int i = 0; i < size; i++) {
                NavBasicDialog mNavdlg = this.mNavDialogs.get(i);
                if (mNavdlg.isVisible() && !basic.isCoExist(mNavdlg.getComponentID())) {
                    if (mNavdlg.getPriority() > basic.getPriority()) {
                        return false;
                    }
                    mNavdlg.dismiss();
                }
            }
            int size2 = this.mNavViews.size();
            for (int i2 = 0; i2 < size2; i2++) {
                NavBasicView mNavView = this.mNavViews.get(i2);
                if (mNavView.isVisible() && !basic.isCoExist(mNavView.getComponentID())) {
                    if (mNavView.getPriority() > basic.getPriority()) {
                        return false;
                    }
                    Message msg = Message.obtain();
                    msg.obj = NavBasic.NAV_COMPONENT_HIDE_FLAG;
                    int componentID = mNavView.getComponentID();
                    msg.arg2 = componentID;
                    msg.arg1 = componentID;
                    mNavView.getHandler().sendMessage(msg);
                }
            }
            int size3 = this.mNavMiscs.size();
            for (int i3 = 0; i3 < size3; i3++) {
                NavBasicMisc mNavMisc = this.mNavMiscs.get(i3);
                if (mNavMisc.isVisible() && !basic.isCoExist(mNavMisc.getComponentID())) {
                    if (mNavMisc.getPriority() > basic.getPriority()) {
                        return false;
                    }
                    Message msg2 = Message.obtain();
                    msg2.obj = NavBasic.NAV_COMPONENT_HIDE_FLAG;
                    int componentID2 = mNavMisc.getComponentID();
                    msg2.arg2 = componentID2;
                    msg2.arg1 = componentID2;
                    mNavMisc.getHandler().sendMessage(msg2);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    public boolean hideAllComponents() {
        try {
            int size = this.mNavDialogs.size();
            for (int i = 0; i < size; i++) {
                NavBasicDialog dialog = this.mNavDialogs.get(i);
                if (dialog.isVisible()) {
                    dialog.dismiss();
                }
            }
            int size2 = this.mNavViews.size();
            for (int i2 = 0; i2 < size2; i2++) {
                NavBasicView view = this.mNavViews.get(i2);
                if (view.isVisible()) {
                    Message msg = Message.obtain();
                    msg.obj = NavBasic.NAV_COMPONENT_HIDE_FLAG;
                    int componentID = this.mNavViews.get(i2).getComponentID();
                    msg.arg2 = componentID;
                    msg.arg1 = componentID;
                    view.getHandler().sendMessage(msg);
                }
            }
            int size3 = this.mNavMiscs.size();
            for (int i3 = 0; i3 < size3; i3++) {
                NavBasicMisc misc = this.mNavMiscs.get(i3);
                if (misc.isVisible()) {
                    Message msg2 = Message.obtain();
                    msg2.obj = NavBasic.NAV_COMPONENT_HIDE_FLAG;
                    int componentID2 = this.mNavMiscs.get(i3).getComponentID();
                    msg2.arg2 = componentID2;
                    msg2.arg1 = componentID2;
                    misc.getHandler().sendMessage(msg2);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    public NavBasic showNavComponent(int keyCode, KeyEvent event) {
        try {
            NavBasicDialog mNavdlg = isDialogKeyHandle(keyCode);
            if (mNavdlg == null || !hideUnCoExistComponent(mNavdlg)) {
                NavBasicMisc mNavMisc = isMiscKeyHandle(keyCode);
                if (mNavMisc == null || !hideUnCoExistComponent(mNavMisc)) {
                    NavBasicView mNavView = isViewKeyHandle(keyCode);
                    if (mNavView == null || !hideUnCoExistComponent(mNavView)) {
                        return null;
                    }
                    Message msg = Message.obtain();
                    msg.obj = "NavComponentShow";
                    int componentID = mNavView.getComponentID();
                    msg.arg2 = componentID;
                    msg.arg1 = componentID;
                    mNavView.getHandler().sendMessage(msg);
                    return mNavView;
                }
                Message msg2 = Message.obtain();
                msg2.obj = "NavComponentShow";
                int componentID2 = mNavMisc.getComponentID();
                msg2.arg2 = componentID2;
                msg2.arg1 = componentID2;
                mNavMisc.getHandler().sendMessage(msg2);
                return mNavMisc;
            }
            mNavdlg.show();
            return mNavdlg;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public NavBasic showNavComponent(int compId) {
        int i = 0;
        try {
            int size = this.mNavDialogs.size();
            while (i < size) {
                NavBasicDialog mNavdlg = this.mNavDialogs.get(i);
                if (mNavdlg.getComponentID() != compId || !hideUnCoExistComponent(mNavdlg)) {
                    i++;
                } else {
                    mNavdlg.show();
                    return mNavdlg;
                }
            }
            int i2 = 0;
            int size2 = this.mNavViews.size();
            while (i2 < size2) {
                NavBasicView mNavView = this.mNavViews.get(i2);
                if (mNavView.getComponentID() != compId || !hideUnCoExistComponent(mNavView)) {
                    i2++;
                } else {
                    Message msg = Message.obtain();
                    msg.obj = "NavComponentShow";
                    int componentID = mNavView.getComponentID();
                    msg.arg2 = componentID;
                    msg.arg1 = componentID;
                    mNavView.getHandler().sendMessage(msg);
                    return mNavView;
                }
            }
            int i3 = 0;
            int size3 = this.mNavMiscs.size();
            while (i3 < size3) {
                NavBasicMisc mNavMisc = this.mNavMiscs.get(i3);
                if (mNavMisc.getComponentID() != compId || !hideUnCoExistComponent(mNavMisc)) {
                    i3++;
                } else {
                    Message msg2 = Message.obtain();
                    msg2.obj = "NavComponentShow";
                    int componentID2 = mNavMisc.getComponentID();
                    msg2.arg2 = componentID2;
                    msg2.arg1 = componentID2;
                    mNavMisc.getHandler().sendMessage(msg2);
                    return mNavMisc;
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean dispatchKeyToActiveComponent(int keyCode, KeyEvent event) {
        boolean isHandled = false;
        try {
            if ((mCurrentNativeCompId & NavBasic.NAV_NATIVE_COMP_ID_BASIC) != 0) {
                if (MarketRegionInfo.isFunctionSupport(13) && mCurrentNativeCompId == 33554433 && keyCode == 171) {
                    PIPPOPSurfaceViewControl.getSurfaceViewControlInstance().changeOutputWithTVState(1);
                }
                if (this.mKeyDispatch.passKeyToNative(keyCode, event)) {
                    MtkLog.d(TAG, "key is respond by native module.event != NULL");
                    return true;
                }
            }
            int size = this.mNavViews.size();
            for (int i = 0; i < size; i++) {
                NavBasicView mNavView = this.mNavViews.get(i);
                if (mNavView.isVisible() && mNavView.getComponentID() != mCurrentAndroidCompId) {
                    isHandled |= mNavView.KeyHandler(keyCode, event, true);
                }
            }
            int size2 = this.mNavMiscs.size();
            for (int i2 = 0; i2 < size2; i2++) {
                NavBasicMisc mNavMisc = this.mNavMiscs.get(i2);
                if (mNavMisc.isVisible() && mNavMisc.getComponentID() != mCurrentAndroidCompId) {
                    isHandled |= mNavMisc.KeyHandler(keyCode, event, true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isHandled;
    }

    public boolean dispatchKeyToActiveComponent(int keyCode, KeyEvent event, boolean noKeyDown) {
        boolean isHandled = false;
        if (!noKeyDown) {
            try {
                if ((mCurrentNativeCompId & NavBasic.NAV_NATIVE_COMP_ID_BASIC) != 0) {
                    if (MarketRegionInfo.isFunctionSupport(13) && mCurrentNativeCompId == 33554433 && keyCode == 171) {
                        PIPPOPSurfaceViewControl.getSurfaceViewControlInstance().changeOutputWithTVState(1);
                    }
                    if (this.mKeyDispatch.passKeyToNative(keyCode, event)) {
                        MtkLog.d(TAG, "key is respond by native module");
                        return true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        int size = this.mNavViews.size();
        for (int i = 0; i < size; i++) {
            NavBasicView mNavView = this.mNavViews.get(i);
            if (mNavView.isVisible() && (isFromNative || mNavView.getComponentID() != mCurrentAndroidCompId)) {
                isHandled |= mNavView.KeyHandler(keyCode, event, true);
            }
        }
        int size2 = this.mNavMiscs.size();
        for (int i2 = 0; i2 < size2; i2++) {
            NavBasicMisc mNavMisc = this.mNavMiscs.get(i2);
            if (mNavMisc.isVisible() && (isFromNative || mNavMisc.getComponentID() != mCurrentAndroidCompId)) {
                isHandled |= mNavMisc.KeyHandler(keyCode, event, true);
            }
        }
        return isHandled;
    }

    public void deinitComponents() {
        try {
            int size = this.mNavDialogs.size();
            for (int i = 0; i < size; i++) {
                this.mNavDialogs.get(i).deinitView();
            }
            int size2 = this.mNavViews.size();
            for (int i2 = 0; i2 < size2; i2++) {
                this.mNavViews.get(i2).deinitView();
            }
            int size3 = this.mNavMiscs.size();
            for (int i3 = 0; i3 < size3; i3++) {
                this.mNavMiscs.get(i3).deinitView();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isComponentsShow() {
        try {
            int size = this.mNavDialogs.size();
            for (int i = 0; i < size; i++) {
                if (this.mNavDialogs.get(i).isVisible()) {
                    return true;
                }
            }
            int size2 = this.mNavViews.size();
            for (int i2 = 0; i2 < size2; i2++) {
                if (this.mNavViews.get(i2).isVisible()) {
                    return true;
                }
            }
            int size3 = this.mNavMiscs.size();
            for (int i3 = 0; i3 < size3; i3++) {
                if (this.mNavMiscs.get(i3).isVisible()) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Integer> getCurrentActiveComps() {
        List<Integer> list = new ArrayList<>();
        try {
            int size = this.mNavDialogs.size();
            for (int i = 0; i < size; i++) {
                if (this.mNavDialogs.get(i).isVisible()) {
                    list.add(Integer.valueOf(this.mNavDialogs.get(i).getComponentID()));
                }
            }
            int size2 = this.mNavViews.size();
            for (int i2 = 0; i2 < size2; i2++) {
                if (this.mNavViews.get(i2).isVisible()) {
                    list.add(Integer.valueOf(this.mNavViews.get(i2).getComponentID()));
                }
            }
            int size3 = this.mNavMiscs.size();
            for (int i3 = 0; i3 < size3; i3++) {
                if (this.mNavMiscs.get(i3).isVisible()) {
                    list.add(Integer.valueOf(this.mNavMiscs.get(i3).getComponentID()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static void updateActiveCompId(boolean isFromNative2, int componentId) {
        synchronized (ComponentsManager.class) {
            if (isFromNative2) {
                try {
                    mCurrentNativeCompId = componentId;
                } catch (Throwable th) {
                    while (true) {
                        throw th;
                    }
                }
            } else {
                mCurrentAndroidCompId = componentId;
            }
            if (componentId != 0) {
                isFromNative = isFromNative2;
            }
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    if (ComponentsManager.mCurrentNativeCompId == ComponentsManager.mCurrentAndroidCompId) {
                        SystemProperties.set("vendor.mtk.live.tv.top", "false");
                    } else {
                        SystemProperties.set("vendor.mtk.live.tv.top", "true");
                    }
                }
            });
        }
        MtkLog.v(TAG, "componentId:" + componentId + ", isFromNative=" + isFromNative2);
    }

    public static void nativeComponentReActive() {
        synchronized (ComponentsManager.class) {
            if ((mCurrentNativeCompId & NavBasic.NAV_NATIVE_COMP_ID_BASIC) != 0) {
                isFromNative = true;
            }
        }
    }

    public static int getActiveCompId() {
        int i;
        synchronized (ComponentsManager.class) {
            if (isFromNative) {
                i = mCurrentNativeCompId;
            } else {
                i = mCurrentAndroidCompId;
            }
        }
        return i;
    }

    public static int getNativeActiveCompId() {
        return mCurrentNativeCompId;
    }

    public String toString() {
        String str;
        Exception ex;
        String str2 = "NavComponentsManager:\n";
        try {
            int i = 0;
            str = str2 + "\nDialog:\n";
            int i2 = 0;
            while (this.mNavDialogs != null && i2 < this.mNavDialogs.size()) {
                try {
                    if (this.mNavDialogs.get(i2) == null) {
                        str = str + i2 + ":null\n";
                    } else {
                        str = str + Integer.toHexString(this.mNavDialogs.get(i2).getComponentID()) + ", status:" + this.mNavDialogs.get(i2).isVisible() + ";\n";
                    }
                    i2++;
                } catch (Exception e) {
                    ex = e;
                    ex.printStackTrace();
                    str2 = str;
                    return str2 + "\nmCurrentAndroidCompId = " + Integer.toHexString(mCurrentAndroidCompId) + ", mCurrentNativeCompId = " + Integer.toHexString(mCurrentNativeCompId) + ", isFromNative = " + isFromNative + "\n";
                }
            }
            String str3 = str + "\nView:\n";
            int i3 = 0;
            while (this.mNavViews != null && i3 < this.mNavViews.size()) {
                if (this.mNavViews.get(i3) == null) {
                    str3 = str3 + i3 + ":null\n";
                } else {
                    str3 = str3 + Integer.toHexString(this.mNavViews.get(i3).getComponentID()) + ", status:" + this.mNavViews.get(i3).isVisible() + ";\n";
                }
                i3++;
            }
            str2 = str3 + "\nMisc:\n";
            while (this.mNavMiscs != null && i < this.mNavMiscs.size()) {
                if (this.mNavMiscs.get(i) == null) {
                    str2 = str2 + "null\n";
                } else {
                    str2 = str2 + Integer.toHexString(this.mNavMiscs.get(i).getComponentID()) + ", status:" + this.mNavMiscs.get(i).isVisible() + ";\n";
                }
                i++;
            }
        } catch (Exception e2) {
            str = str2;
            ex = e2;
            ex.printStackTrace();
            str2 = str;
            return str2 + "\nmCurrentAndroidCompId = " + Integer.toHexString(mCurrentAndroidCompId) + ", mCurrentNativeCompId = " + Integer.toHexString(mCurrentNativeCompId) + ", isFromNative = " + isFromNative + "\n";
        }
        return str2 + "\nmCurrentAndroidCompId = " + Integer.toHexString(mCurrentAndroidCompId) + ", mCurrentNativeCompId = " + Integer.toHexString(mCurrentNativeCompId) + ", isFromNative = " + isFromNative + "\n";
    }
}
