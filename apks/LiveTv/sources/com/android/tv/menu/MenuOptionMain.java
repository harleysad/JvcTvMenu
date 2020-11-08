package com.android.tv.menu;

import android.content.Context;
import android.media.tv.TvView;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.android.tv.menu.Menu;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class MenuOptionMain extends NavBasicMisc implements ComponentStatusListener.ICStatusListener {
    private static final int INTERVAL_TIME = 400;
    public static final int OPTION_AUTO_PICTURE = 9;
    public static final int OPTION_BROADCAST_TV_CI = 14;
    public static final int OPTION_BROADCAST_TV_OAD = 13;
    public static final int OPTION_BROADCAST_TV_SETTINGS = 11;
    public static final int OPTION_CLOSED_CAPTIONS = 0;
    public static final int OPTION_DEVELOPER = 6;
    public static final int OPTION_DEVICE_INFO = 19;
    public static final int OPTION_DISPLAY_MODE = 1;
    public static final int OPTION_DVR_LIST = 16;
    public static final int OPTION_GINGA = 22;
    public static final int OPTION_IN_APP_PIP = 2;
    public static final int OPTION_MORE_CHANNELS = 5;
    public static final int OPTION_MULTI_AUDIO = 4;
    public static final int OPTION_MY_FAVORITE = 8;
    public static final int OPTION_PIP_INPUT = 100;
    public static final int OPTION_PIP_LAYOUT = 103;
    public static final int OPTION_PIP_SIZE = 104;
    public static final int OPTION_PIP_SOUND = 102;
    public static final int OPTION_PIP_SWAP = 101;
    public static final int OPTION_POWER = 12;
    public static final int OPTION_PVR_START = 20;
    public static final int OPTION_PVR_STOP = 21;
    public static final int OPTION_SCHEDULE_LIST = 18;
    public static final int OPTION_SETTINGS = 7;
    public static final int OPTION_SOURCE_CAPTIONS = 15;
    public static final int OPTION_SPEAKERS = 10;
    public static final int OPTION_SYSTEMWIDE_PIP = 3;
    public static final int OPTION_TSHIFT_MODE = 17;
    public static final String TAG = "MenuOptionMain";
    long lastMenuKeyTime = 0;
    private Context mContext;
    private Menu mMenu;
    private MenuView mMenuView;
    private final SparseArray<OptionChangedListener> mOptionChangedListeners = new SparseArray<>();

    public interface OptionChangedListener {
        void onOptionChanged(String str);
    }

    public MenuOptionMain(Context context) {
        super(context);
        this.mContext = context;
        this.componentID = NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG;
        TvView tvView = ((TurnkeyUiMainActivity) context).getTvView();
        this.mMenuView = (MenuView) ((TurnkeyUiMainActivity) context).findViewById(R.id.menu);
        this.mMenu = new Menu(context, tvView, this, this.mMenuView, new MenuRowFactory(context, tvView), new Menu.OnMenuVisibilityChangeListener() {
            public void onMenuVisibilityChange(boolean visible) {
            }
        }, new Menu.OnAutoHideListener() {
            public void onAutoHide() {
                MenuOptionMain.this.setVisibility(4);
            }
        });
        ComponentStatusListener.getInstance().addListener(20, this);
    }

    public boolean isCoExist(int componentID) {
        return componentID == this.componentID;
    }

    public boolean isKeyHandler(int keyCode) {
        Log.d(TAG, "isKeyHandler,keyCode=" + keyCode);
        if (keyCode != 82) {
            return false;
        }
        long currentTime = SystemClock.uptimeMillis();
        if (currentTime - this.lastMenuKeyTime <= 400) {
            return false;
        }
        this.lastMenuKeyTime = currentTime;
        return true;
    }

    public void setVisibility(int visibility) {
        if (visibility != 0) {
            super.setVisibility(visibility);
            this.mMenu.hide(true);
        } else if (this.mMenu.isActive()) {
            super.setVisibility(4);
            this.mMenu.hide(true);
        } else {
            super.setVisibility(visibility);
            this.mMenu.show(9);
        }
    }

    public boolean KeyHandler(int keyCode, KeyEvent event) {
        Log.d(TAG, "KeyHandler,keyCode=" + keyCode);
        this.mMenu.scheduleHide();
        if (event != null) {
            this.mMenuView.dispatchKeyEvent(event);
        }
        if (keyCode != 4) {
            if (keyCode != 233) {
                switch (keyCode) {
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        break;
                    case 23:
                        setVisibility(4);
                        return true;
                    default:
                        return super.KeyHandler(keyCode, event, false);
                }
            }
            return true;
        }
        setVisibility(4);
        return true;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        Log.d(TAG, "KeyHandler keyCode = " + keyCode + ",event = " + event + " ,fromNative=" + fromNative);
        return KeyHandler(keyCode, event);
    }

    public String getOptionString(int option) {
        return "";
    }

    private void notifyOptionChanged(int option) {
        OptionChangedListener listener = this.mOptionChangedListeners.get(option);
        if (listener != null) {
            listener.onOptionChanged(getOptionString(option));
        }
    }

    public void setOptionChangedListener(int option, OptionChangedListener listener) {
        this.mOptionChangedListeners.put(option, listener);
    }

    public boolean isShowing() {
        return this.mMenu.isActive();
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus statusID =" + statusID + ">>value=" + value);
        if (statusID == 20) {
            this.mMenu.updateLanguage();
        }
    }
}
