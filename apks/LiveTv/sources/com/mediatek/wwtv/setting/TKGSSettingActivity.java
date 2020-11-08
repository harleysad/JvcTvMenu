package com.mediatek.wwtv.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.setting.base.scan.adapter.TkgsLocatorListAdapter;
import com.mediatek.wwtv.setting.fragments.TkgsLocatorListFrag;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.detailui.ActionFragment;
import com.mediatek.wwtv.setting.widget.detailui.ContentFragment;
import com.mediatek.wwtv.setting.widget.view.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class TKGSSettingActivity extends BaseSettingsActivity {
    static final int REQ_EDITTEXT = 35;
    final int MESSAGE_UPDATE_TP = 33;
    /* access modifiers changed from: private */
    public String TAG = "TKGSSettingActivity";
    private boolean isTKGSSettingShow = false;
    private Context mContext;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };
    private String mItemId = "";
    private Action mTKGSSetting;
    private String rootTitle;
    private int selectPos = 0;
    private String title = "";
    TurnkeyCommDialog tkgsLocConfirm;
    Action tkgsResetTabver;
    boolean tkgslocGoBack = false;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MtkLog.d(this.TAG, "onCreate()");
        this.mContext = this;
        Intent intent = getIntent();
        this.title = intent.getStringExtra("title");
        this.mActions = new ArrayList();
        this.mItemId = intent.getStringExtra("mItemId");
        this.selectPos = intent.getIntExtra("selectPos", 0);
        this.mConfigManager = MenuConfigManager.getInstance(this.mContext);
        this.mDataHelper = MenuDataHelper.getInstance(this.mContext);
        this.rootTitle = "TKGSSetting";
        this.mTKGSSetting = new Action(MenuConfigManager.TKGS_SETTING, "TKGS Setting", 10004, 10004, 10004, (String[]) null, 1, Action.DataType.HAVESUBCHILD);
        this.mTKGSSetting.mSubChildGroup = new ArrayList();
        this.mCurrAction = this.mTKGSSetting;
        super.onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MtkLog.d(this.TAG, "onResume()");
        Intent intent = getIntent();
        this.title = intent.getStringExtra("title");
        this.mItemId = intent.getStringExtra("mItemId");
        this.selectPos = intent.getIntExtra("selectPos", 0);
        String str = this.TAG;
        MtkLog.d(str, "isTKGSSettingShow" + this.isTKGSSettingShow);
        if (!this.isTKGSSettingShow) {
            onActionClicked(this.mTKGSSetting);
            this.isTKGSSettingShow = true;
        }
        super.onResume();
    }

    private void handleSpecialItem(Action action) {
        String[] idValue;
        if (action.mDataType == Action.DataType.LASTVIEW && this.mCurrAction != null) {
            if ((this.mCurrAction.mDataType == Action.DataType.OPTIONVIEW || this.mCurrAction.mDataType == Action.DataType.EFFECTOPTIONVIEW || this.mCurrAction.mDataType == Action.DataType.SWICHOPTIONVIEW) && (idValue = SettingsUtil.getRealIdAndValue(action.mItemID)) != null) {
                try {
                    String selId = this.mCurrAction.mItemID;
                    int value = Integer.parseInt(idValue[1]);
                    int lastValue = this.mCurrAction.mInitValue;
                    String str = this.TAG;
                    MtkLog.d(str, "value===" + value + ",lastvalue==" + lastValue + " selId=" + selId);
                    if (selId.equals(MenuConfigManager.TKGS_PREFER_LIST)) {
                        this.mDataHelper.setTKGSOneServiceListValue(value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void tkgsLocConfirmShow(final int flag) {
        String message = "";
        if (flag == 1) {
            message = "Do you want to clear all hidden locators?";
        } else if (flag == 2) {
            message = "Do you want to reset table version?";
        }
        this.tkgsLocConfirm = new TurnkeyCommDialog(this, 3);
        this.tkgsLocConfirm.setMessage(message);
        this.tkgsLocConfirm.setButtonYesName(getString(R.string.menu_ok));
        this.tkgsLocConfirm.setButtonNoName(getString(R.string.menu_cancel));
        this.tkgsLocConfirm.show();
        this.tkgsLocConfirm.getButtonNo().requestFocus();
        this.tkgsLocConfirm.setPositon(-20, 70);
        this.tkgsLocConfirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                TKGSSettingActivity.this.tkgsLocConfirm.dismiss();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                if (flag == 1) {
                    int ret = TKGSSettingActivity.this.mDataHelper.cleanAllHiddenLocs();
                    String access$000 = TKGSSettingActivity.this.TAG;
                    MtkLog.d(access$000, "clean hidden locs ret ==" + ret);
                    TKGSSettingActivity.this.goBack();
                } else if (flag == 2) {
                    boolean ret2 = TKGSSettingActivity.this.mDataHelper.resetTKGSTableVersion(255);
                    String access$0002 = TKGSSettingActivity.this.TAG;
                    MtkLog.d(access$0002, "reset table version ret ==" + ret2);
                    if (ret2) {
                        int resetVersion = TKGSSettingActivity.this.mDataHelper.getTKGSTableVersion();
                        String access$0003 = TKGSSettingActivity.this.TAG;
                        MtkLog.d(access$0003, "reset table version retver ==" + resetVersion);
                        if (resetVersion == 255) {
                            TKGSSettingActivity.this.tkgsResetTabver.setDescription("None");
                            final ActionFragment frag = (ActionFragment) TKGSSettingActivity.this.mActionFragment;
                            ((ActionAdapter) frag.getAdapter()).notifyDataSetChanged();
                            TKGSSettingActivity.this.mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    frag.getScrollAdapterView().setSelection(3);
                                }
                            }, 500);
                        } else {
                            Action action = TKGSSettingActivity.this.tkgsResetTabver;
                            action.setDescription("" + resetVersion);
                        }
                    }
                }
                TKGSSettingActivity.this.tkgsLocConfirm.dismiss();
                return true;
            }
        };
        this.tkgsLocConfirm.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                TKGSSettingActivity.this.tkgsLocConfirm.dismiss();
                return true;
            }
        });
        this.tkgsLocConfirm.getButtonYes().setOnKeyListener(yesListener);
    }

    public void goBack() {
        if (this.mCurrAction != null && this.mCurrAction.mItemID.equals(MenuConfigManager.TKGS_SETTING)) {
            this.mDataHelper.enableMonitor();
            this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
            finish();
        } else if (this.mCurrAction != null && this.mCurrAction.mItemID.equals(MenuConfigManager.TKGS_LOC_POLAZATION)) {
            MtkLog.d(this.TAG, "goBack for TKGS_LOC_POLAZATION");
            this.tkgslocGoBack = true;
        }
        if (this.mCurrAction != null) {
            String str = this.TAG;
            MtkLog.d(str, "goBack 0mCurrAction.mItemID : " + this.mCurrAction.mItemID);
        }
        super.goBack();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void refreshActionList() {
        if (this.mCurrAction != null) {
            String str = this.TAG;
            MtkLog.d(str, "refreshActionList mCurrAction" + this.mCurrAction.mItemID);
        }
        this.mActions.clear();
        switch ((Action.DataType) this.mState) {
            case HAVESUBCHILD:
                if (this.mCurrAction != null && this.mCurrAction.mItemID.equals(MenuConfigManager.TKGS_SETTING)) {
                    MtkLog.d(this.TAG, "loadTKGSSetting mActions.addAll");
                    this.mActions.addAll(this.mCurrAction.mSubChildGroup);
                    return;
                } else if (this.mCurrAction != null && this.mCurrAction.mItemID.equals(MenuConfigManager.TKGS_LOC_ITEM_ADD)) {
                    String str2 = this.TAG;
                    MtkLog.d(str2, "loadTKGSLocatorInfo add:" + this.mDataHelper.TKGSVisibleLocSize);
                    if (this.mDataHelper.TKGSVisibleLocSize < 5) {
                        loadTKGSLocatorInfo(1, this.mCurrAction);
                        this.mActions.addAll(this.mCurrAction.mSubChildGroup);
                        return;
                    }
                    Toast.makeText(this.mContext, "should only have 5 item", 1).show();
                    return;
                } else {
                    return;
                }
            case TKGSLOCITEMVIEW:
                if (this.mCurrAction != null) {
                    MtkLog.d(this.TAG, "loadTKGSLocatorInfo update");
                    loadTKGSLocatorInfo(2, this.mCurrAction);
                    this.mActions.addAll(this.mCurrAction.mSubChildGroup);
                    return;
                }
                return;
            default:
                if (this.mCurrAction != null) {
                    super.refreshActionList();
                    return;
                }
                return;
        }
    }

    private void loadTKGSLocatorInfo(int flag, Action parentAction) {
        int pola;
        int i = flag;
        Action action = parentAction;
        if (!this.tkgslocGoBack || action.mSubChildGroup.size() <= 0) {
            MtkLog.d(this.TAG, "loadTKGSLocatorInfo");
            action.mSubChildGroup = new ArrayList();
            int freq = -1;
            int rate = -1;
            int pola2 = -1;
            int prog_id = -1;
            int recId = -1;
            List<Action> childGroup = new ArrayList<>();
            if (i == 1) {
                TkgsLocatorListAdapter.TkgsLocatorItem defItem = this.mDataHelper.getDefaultTKGSLocItem();
                int findPola = defItem.threePry.indexOf(72);
                if (findPola == -1) {
                    findPola = defItem.threePry.indexOf(86);
                    pola2 = 1;
                } else {
                    pola2 = 0;
                }
                freq = Integer.parseInt(defItem.threePry.substring(0, findPola));
                rate = Integer.parseInt(defItem.threePry.substring(findPola + 1));
                prog_id = defItem.progId;
            } else if (i == 2 && (action instanceof TkgsLocatorListAdapter.TkgsLocatorItem)) {
                TkgsLocatorListAdapter.TkgsLocatorItem defItem2 = (TkgsLocatorListAdapter.TkgsLocatorItem) action;
                int findPola2 = defItem2.threePry.indexOf(72);
                if (findPola2 == -1) {
                    findPola2 = defItem2.threePry.indexOf(86);
                    pola = 1;
                } else {
                    pola = 0;
                }
                freq = Integer.parseInt(defItem2.threePry.substring(0, findPola2));
                rate = Integer.parseInt(defItem2.threePry.substring(findPola2 + 1));
                prog_id = defItem2.progId;
                recId = defItem2.bnum;
            }
            String str = this.TAG;
            MtkLog.d(str, "loadTKGSLocatorInfo:prog_id:" + prog_id + ",rec:" + recId);
            Action action2 = new Action(MenuConfigManager.TKGS_LOC_FREQ, "Freqency", 3000, MenuConfigManager.BISS_KEY_FREQ_MAX, freq, (String[]) null, 1, Action.DataType.NUMVIEW);
            action2.setInputLength(5);
            childGroup.add(action2);
            action2.setmParentGroup(childGroup);
            Action action3 = new Action(MenuConfigManager.TKGS_LOC_SYMBOL_RATE, "Symbol Rate(Ksym/s)", 2000, MenuConfigManager.BISS_KEY_SYMBOL_RATE_MAX, rate, (String[]) null, 1, Action.DataType.NUMVIEW);
            action3.setInputLength(5);
            childGroup.add(action3);
            action3.setmParentGroup(childGroup);
            Action action4 = new Action(MenuConfigManager.TKGS_LOC_POLAZATION, "Polazation", 10004, 10004, pola2, new String[]{"Horizonal", "Vertical"}, 1, Action.DataType.OPTIONVIEW);
            childGroup.add(action4);
            action4.setmParentGroup(childGroup);
            Action action5 = new Action(MenuConfigManager.TKGS_LOC_SVC_ID, "PID", 0, 8191, prog_id, (String[]) null, 1, Action.DataType.NUMVIEW);
            action5.setInputLength(4);
            childGroup.add(action5);
            action5.setmParentGroup(childGroup);
            if (i == 1) {
                Action action6 = new Action(MenuConfigManager.TKGS_LOC_ITEM_SAVE, "Save Locator", 10004, 10004, 10004, (String[]) null, 1, Action.DataType.SAVEDATA);
                childGroup.add(action6);
                action6.setmParentGroup(childGroup);
            } else if (i == 2) {
                int i2 = recId;
                Action action7 = new Action(MenuConfigManager.TKGS_LOC_ITEM_UPDATE, "Update Locator", 10004, 10004, i2, (String[]) null, 1, Action.DataType.SAVEDATA);
                childGroup.add(action7);
                action7.setmParentGroup(childGroup);
                Action action8 = new Action(MenuConfigManager.TKGS_LOC_ITEM_DELETE, "Delete Locator", 10004, 10004, i2, (String[]) null, 1, Action.DataType.SAVEDATA);
                childGroup.add(action8);
                action8.setmParentGroup(childGroup);
            }
            action.mSubChildGroup.addAll(childGroup);
            return;
        }
        MtkLog.d(this.TAG, "loadTKGSLocatorInfo not refresh return");
        this.tkgslocGoBack = false;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String str = this.TAG;
        MtkLog.d(str, "onActivityResult requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);
        if (resultCode == -1 && data != null) {
            String value = data.getStringExtra(SaveValue.GLOBAL_VALUE_VALUE);
            if ((this.mActionFragment instanceof ActionFragment) && value != null) {
                ActionFragment frag = (ActionFragment) this.mActionFragment;
                int pos = frag.getSelectedItemPosition();
                Action selectAction = (Action) frag.getScrollAdapterView().getSelectedView().getTag(R.id.action_title);
                String str2 = this.TAG;
                MtkLog.d(str2, "onActivityResult pos:" + pos + ",ationId:" + selectAction.mItemID);
                int now = Integer.parseInt(value);
                if (now < selectAction.mStartValue) {
                    now = selectAction.mStartValue;
                } else if (now > selectAction.mEndValue) {
                    now = selectAction.mEndValue;
                }
                selectAction.mInitValue = now;
                selectAction.setDescription(now + "");
                ((ActionAdapter) frag.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    public void onActionClicked(Action action) {
        String threePry;
        MtkLog.d(this.TAG, "onActionClicked " + action.mItemID);
        handleSpecialItem(action);
        if (action.mItemID.equals(MenuConfigManager.TKGS_SETTING)) {
            loadTKGSSetting(action);
            this.mDataHelper.disableMonitor();
        } else if (action.mItemID.equals(MenuConfigManager.TKGS_LOC_ITEM_ADD)) {
            MtkLog.e(this.TAG, "TKGS_LOC_ITEM_ADD");
            if (this.mDataHelper.TKGSVisibleLocSize >= 5) {
                Toast.makeText(this.mContext, "should only have 5 item", 1).show();
                return;
            }
        }
        switch (action.mDataType) {
            case SAVEDATA:
                MtkLog.d(this.TAG, "do save data...");
                int freq = action.getmParentGroup().get(0).mInitValue;
                int rate = action.getmParentGroup().get(1).mInitValue;
                int pola = action.getmParentGroup().get(2).mInitValue;
                int progId = action.getmParentGroup().get(3).mInitValue;
                if (pola <= 0) {
                    threePry = freq + "H" + rate;
                } else {
                    threePry = freq + MtkTvRatingConvert2Goo.SUB_RATING_STR_V + rate;
                }
                MtkLog.d(this.TAG, "do save data...threePry==" + threePry);
                if (action.mItemID.equals(MenuConfigManager.TKGS_LOC_ITEM_SAVE)) {
                    TkgsLocatorListAdapter.TkgsLocatorItem item = new TkgsLocatorListAdapter.TkgsLocatorItem(-1, progId, threePry);
                    MtkLog.d(this.TAG, "freq" + freq + "rate" + rate + "pola" + pola + "progId" + progId);
                    int ret = this.mDataHelper.operateTKGSLocatorinfo(item, 0);
                    if (ret == -2) {
                        Toast.makeText(this.mContext, "TKGS LOC Existed!", 0).show();
                        return;
                    } else if (ret == -9) {
                        Toast.makeText(this.mContext, "TKGS LOC's max num is 5!", 0).show();
                        return;
                    } else if (ret == 0) {
                        goBack();
                        return;
                    } else {
                        MtkLog.d(this.TAG, "addTKGSLocatorinfo ret is:" + ret);
                        return;
                    }
                } else if (action.mItemID.equals(MenuConfigManager.TKGS_LOC_ITEM_UPDATE)) {
                    int ret2 = this.mDataHelper.operateTKGSLocatorinfo(new TkgsLocatorListAdapter.TkgsLocatorItem(action.mInitValue, progId, threePry), 1);
                    if (ret2 == -2) {
                        Toast.makeText(this.mContext, "TKGS LOC Existed!", 0).show();
                    } else if (ret2 == 0) {
                        goBack();
                    }
                    MtkLog.d(this.TAG, "updateTKGSLocatorinfo ret is:" + ret2);
                    return;
                } else if (action.mItemID.equals(MenuConfigManager.TKGS_LOC_ITEM_DELETE)) {
                    int ret3 = this.mDataHelper.operateTKGSLocatorinfo(new TkgsLocatorListAdapter.TkgsLocatorItem(action.mInitValue, progId, threePry), 2);
                    MtkLog.d(this.TAG, "deleteTKGSLocatorinfo ret is:" + ret3);
                    if (ret3 == 0) {
                        goBack();
                        return;
                    }
                    return;
                } else {
                    return;
                }
            case NUMVIEW:
                gotoEditTextAct(action);
                return;
            case BISSKEYVIEW:
                gotoEditTextAct(action);
                return;
            case DIALOGPOP:
                if (action.mItemID.equals(MenuConfigManager.TKGS_LOC_ITEM_HIDD_CLEANALL)) {
                    tkgsLocConfirmShow(1);
                    return;
                } else if (action.mItemID.equals(MenuConfigManager.TKGS_RESET_TAB_VERSION)) {
                    tkgsLocConfirmShow(2);
                    return;
                } else {
                    return;
                }
            default:
                super.onActionClicked(action);
                return;
        }
    }

    private void loadTKGSSetting(Action setupAction) {
        this.mTKGSSetting.mSubChildGroup.add(new Action("g_misc__tkgs_operating_mode", "Operate Mode", 10004, 10004, this.mConfigManager.getDefault("g_misc__tkgs_operating_mode"), new String[]{"Automatic", "Customisable", "TKGS Off"}, 1, Action.DataType.OPTIONVIEW));
        Action action = new Action(MenuConfigManager.TKGS_LOC_LIST, "TKGS Locator List", 10004, 10004, 10004, (String[]) null, 1, Action.DataType.HAVESUBCHILD);
        action.mSubChildGroup = new ArrayList();
        this.mTKGSSetting.mSubChildGroup.add(action);
        Action action2 = new Action(MenuConfigManager.TKGS_HIDD_LOCS, "TKGS Hidden Locations", 10004, 10004, 10004, (String[]) null, 1, Action.DataType.HAVESUBCHILD);
        action2.mSubChildGroup = new ArrayList();
        this.tkgsResetTabver = new Action(MenuConfigManager.TKGS_RESET_TAB_VERSION, "Reset Table Version", 10004, 10004, 10004, (String[]) null, 1, Action.DataType.DIALOGPOP);
        this.tkgsResetTabver.mSubChildGroup = new ArrayList();
        int tversion = this.mDataHelper.getTKGSTableVersion();
        if (tversion == 255) {
            this.tkgsResetTabver.setDescription("None");
        } else {
            Action action3 = this.tkgsResetTabver;
            action3.setDescription("" + tversion);
        }
        if (this.mConfigManager.getDefault(MenuConfigManager.TKGS_FAC_SETUP_AVAIL_CONDITION) == 0) {
            this.mTKGSSetting.mSubChildGroup.add(this.tkgsResetTabver);
        } else {
            this.mTKGSSetting.mSubChildGroup.add(action2);
            this.mTKGSSetting.mSubChildGroup.add(this.tkgsResetTabver);
        }
        List<String> batStrList = this.mDataHelper.getTKGSOneServiceStrList(this.mDataHelper.getTKGSOneSvcList());
        int defVal = this.mDataHelper.getTKGSOneServiceSelectValue();
        String[] preferlistArray = (String[]) batStrList.toArray(new String[batStrList.size()]);
        if (preferlistArray != null && preferlistArray.length > 0) {
            this.mTKGSSetting.mSubChildGroup.add(new Action(MenuConfigManager.TKGS_PREFER_LIST, "Preferred List", 10004, 10004, defVal, preferlistArray, 1, Action.DataType.OPTIONVIEW));
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        goBack();
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        event.getAction();
        return super.dispatchKeyEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        MtkLog.d(this.TAG, "onPause()");
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d(this.TAG, "onStop()");
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public Object getInitialDataType() {
        return Action.DataType.TOPVIEW;
    }

    /* access modifiers changed from: protected */
    public void updateView() {
        String str;
        if (this.mCurrAction != null) {
            String str2 = this.TAG;
            MtkLog.d(str2, "updateView CurrentAction =" + this.mCurrAction.mItemID);
        }
        String str3 = this.TAG;
        MtkLog.d(str3, "DataType=" + ((Action.DataType) this.mState));
        int i = AnonymousClass5.$SwitchMap$com$mediatek$wwtv$setting$widget$detailui$Action$DataType[((Action.DataType) this.mState).ordinal()];
        switch (i) {
            case 1:
                break;
            case 2:
                refreshActionList();
                setView(this.mCurrAction == null ? "Menu Setup" : this.mCurrAction.getTitle(), this.mParentAction == null ? this.rootTitle : this.mParentAction.getTitle(), "", (int) R.drawable.menu_setup_icon);
                return;
            default:
                switch (i) {
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        break;
                    default:
                        return;
                }
        }
        refreshActionList();
        String title2 = this.mCurrAction == null ? "TKGS Setting" : this.mCurrAction.getTitle();
        if (this.mParentAction == null) {
            str = this.mContext.getResources().getString(R.string.menu_interface_name);
        } else {
            str = this.mParentAction.getTitle();
        }
        setView(title2, str, "", (int) R.drawable.menu_tv_icon);
        if (this.mCurrAction == null) {
            return;
        }
        if (this.mCurrAction.mItemID.equals(MenuConfigManager.TKGS_LOC_LIST) || this.mCurrAction.mItemID.equals(MenuConfigManager.TKGS_HIDD_LOCS)) {
            TkgsLocatorListFrag tkgsfrag = new TkgsLocatorListFrag();
            tkgsfrag.setAction(this.mCurrAction);
            this.mActionFragment = tkgsfrag;
            setViewWithActionFragment(this.mCurrAction == null ? "Menu Setup" : this.mCurrAction.getTitle(), this.mParentAction == null ? this.rootTitle : this.mParentAction.getTitle(), "", R.drawable.menu_setup_icon);
        }
    }

    /* access modifiers changed from: protected */
    public void setActionsForTopView() {
    }

    /* access modifiers changed from: protected */
    public void setProperty(boolean enable) {
    }

    /* access modifiers changed from: protected */
    public void setViewWithActionFragment(String title2, String breadcrumb, String description, int iconResId) {
        this.mContentFragment = ContentFragment.newInstance(title2, breadcrumb, description, iconResId, getResources().getColor(R.color.icon_background));
        setContentAndActionFragments(this.mContentFragment, this.mActionFragment);
    }

    public void gotoEditTextAct(Action action) {
        if (action.isEnabled()) {
            Intent intent = new Intent(this.mContext, EditTextActivity.class);
            intent.putExtra("password", false);
            intent.putExtra("description", action.getTitle());
            if (action.mItemID.equals(MenuConfigManager.SETUP_POSTAL_CODE) || action.mItemID.equals(MenuConfigManager.BISS_KEY_CW_KEY)) {
                intent.putExtra("initialText", action.getDescription());
            } else {
                intent.putExtra("initialText", "" + action.mInitValue);
            }
            intent.putExtra("itemId", action.mItemID);
            if (action.mDataType == Action.DataType.NUMVIEW) {
                intent.putExtra("isDigit", true);
                intent.putExtra("length", 5);
            } else if (action.mDataType == Action.DataType.BISSKEYVIEW) {
                intent.putExtra("length", 16);
                intent.putExtra("canWatchText", true);
            }
            startActivityForResult(intent, 35);
            return;
        }
        MtkLog.e(this.TAG, "Option Item needn't go to editText Activity");
    }
}
