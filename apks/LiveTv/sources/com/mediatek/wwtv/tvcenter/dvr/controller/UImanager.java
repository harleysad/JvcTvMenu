package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.app.Activity;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.ui.CommonInfoBar;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;

public class UImanager {
    public static boolean showing = false;
    private final Activity mActivity;
    private CommonInfoBar mPopup;

    public UImanager(Activity mActivity2) {
        this.mActivity = mActivity2;
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public void hiddenAllViews() {
        try {
            if (this.mPopup != null && this.mPopup.isShowing()) {
                this.mPopup.dismiss();
                showing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInfoBar(String info, Long duration) {
    }

    public void showInfoBar(String info) {
        CommonIntegration.getInstance().closeFavFullMsg();
        if (!DvrManager.getInstance().isInPictureMode()) {
            try {
                if (ComponentsManager.getActiveCompId() == 16777226) {
                    ((FavoriteListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST)).dismiss();
                }
                if (this.mPopup == null || ((Activity) this.mPopup.getContentView().getContext()) != TurnkeyUiMainActivity.getInstance()) {
                    this.mPopup = new CommonInfoBar(TurnkeyUiMainActivity.getInstance(), info);
                } else {
                    this.mPopup.setInfo(info);
                }
                this.mPopup.show();
                showing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dissmiss() {
        try {
            if (this.mPopup != null) {
                this.mPopup.dismiss();
            }
            showing = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
