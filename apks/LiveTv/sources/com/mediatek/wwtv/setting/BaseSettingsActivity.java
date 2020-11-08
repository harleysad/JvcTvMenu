package com.mediatek.wwtv.setting;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.mediatek.wwtv.setting.AsyncLoader;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.setting.util.SpecialOptionDealer;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.detailui.ActionFragment;
import com.mediatek.wwtv.setting.widget.detailui.ContentFragment;
import com.mediatek.wwtv.setting.widget.detailui.DialogActivity;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class BaseSettingsActivity extends DialogActivity implements AsyncLoader.DataLoadListener {
    long ENTER_DELAYMILLS = 500;
    private final String TAG = "BaseSettings";
    protected WindowManager.LayoutParams baseLp = null;
    long enterPressTime = 0;
    AsyncLoader mALoader;
    protected Fragment mActionFragment;
    protected Stack<Action> mActionLevelStack = new Stack<>();
    protected ArrayList<Action> mActions;
    protected MenuConfigManager mConfigManager;
    protected Fragment mContentFragment;
    protected Action mCurrAction;
    protected MenuDataHelper mDataHelper;
    protected CommonIntegration mIntegration;
    protected Action mParentAction;
    protected Resources mResources;
    protected SpecialOptionDealer mSpecialOptionDealer;
    protected Object mState;
    protected Stack<Object> mStateStack = new Stack<>();
    protected TVContent mTV;

    /* access modifiers changed from: protected */
    public abstract Object getInitialDataType();

    /* access modifiers changed from: protected */
    public abstract void setActionsForTopView();

    /* access modifiers changed from: protected */
    public abstract void setProperty(boolean z);

    /* access modifiers changed from: protected */
    public abstract void updateView();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        this.mResources = getResources();
        this.mActions = new ArrayList<>();
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (((double) SettingsUtil.SCREEN_WIDTH) * 0.89d);
        lp.height = (int) (((double) SettingsUtil.SCREEN_HEIGHT) * 0.94d);
        this.baseLp = lp;
        getWindow().setAttributes(lp);
        MtkLog.d("BaseSettingsActivity", "resume time00==" + System.currentTimeMillis());
        this.mDataHelper = MenuDataHelper.getInstance(this);
        this.mConfigManager = MenuConfigManager.getInstance(this);
        this.mTV = TVContent.getInstance(this);
        this.mIntegration = CommonIntegration.getInstanceWithContext(this);
        this.mALoader = AsyncLoader.getInstance();
        this.mALoader.bindDataLoadListener(this);
        setState(getInitialDataType(), true);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        MtkLog.d("BaseSettingsActivity", "resume time==" + System.currentTimeMillis());
        SettingsUtil.loadStatus = 22;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        this.mResources = getResources();
        this.mActions = new ArrayList<>();
        this.mDataHelper = MenuDataHelper.getInstance(this);
        this.mConfigManager = MenuConfigManager.getInstance(this);
        this.mTV = TVContent.getInstance(this);
        this.mIntegration = CommonIntegration.getInstanceWithContext(this);
        setState(getInitialDataType(), true);
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void setState(Object state, boolean updateStateStack) {
        if (updateStateStack && this.mState != null) {
            this.mStateStack.push(this.mState);
        }
        this.mState = state;
        updateView();
    }

    /* access modifiers changed from: protected */
    public void setView(int titleResId, int breadcrumbResId, int descResId, int iconResId) {
        String description = null;
        String title = titleResId != 0 ? this.mResources.getString(titleResId) : null;
        String breadcrumb = breadcrumbResId != 0 ? this.mResources.getString(breadcrumbResId) : null;
        if (descResId != 0) {
            description = this.mResources.getString(descResId);
        }
        setView(title, breadcrumb, description, iconResId);
    }

    /* access modifiers changed from: protected */
    public void setView(String title, String breadcrumb, String description, int iconResId) {
        this.mContentFragment = ContentFragment.newInstance(title, breadcrumb, description, iconResId, getResources().getColor(R.color.icon_background));
        this.mActionFragment = ActionFragment.newInstance(this.mActions);
        setContentAndActionFragments(this.mContentFragment, this.mActionFragment);
    }

    /* access modifiers changed from: protected */
    public void setView(String title, String breadcrumb, String description, Uri uri) {
        this.mContentFragment = ContentFragment.newInstance(title, breadcrumb, (String) null, uri, getResources().getColor(R.color.icon_background));
        this.mActionFragment = ActionFragment.newInstance(this.mActions);
        setContentAndActionFragments(this.mContentFragment, this.mActionFragment);
    }

    /* access modifiers changed from: protected */
    public void setView(int titleResId, String breadcrumb, int descResId, Uri uri) {
        String description = null;
        String title = titleResId != 0 ? this.mResources.getString(titleResId) : null;
        if (descResId != 0) {
            description = this.mResources.getString(descResId);
        }
        setView(title, breadcrumb, description, uri);
    }

    /* access modifiers changed from: protected */
    public void refreshActionList() {
        this.mActions.clear();
        switch ((Action.DataType) this.mState) {
            case TOPVIEW:
                this.mALoader.execute((Object) null);
                return;
            case OPTIONVIEW:
            case SWICHOPTIONVIEW:
            case EFFECTOPTIONVIEW:
                MtkLog.d("BaseSettings", "mCurrAction.mOptionValue.length==" + this.mCurrAction.mOptionValue.length);
                for (int i = 0; i < this.mCurrAction.mOptionValue.length; i++) {
                    Action action = new Action(this.mCurrAction.mItemID + SettingsUtil.OPTIONSPLITER + i, this.mCurrAction.mOptionValue[i], 10004, 10004, 10004, (String[]) null, 1, Action.DataType.LASTVIEW);
                    if (i == this.mCurrAction.mInitValue) {
                        action.setmChecked(true);
                    }
                    this.mActions.add(action);
                }
                return;
            case HAVESUBCHILD:
                MtkLog.d("BaseSettings", "mCurrAction.mSubChild.length==" + this.mCurrAction.mSubChildGroup.size());
                this.mActions.addAll(this.mCurrAction.mSubChildGroup);
                return;
            default:
                return;
        }
    }

    public void onActionClicked(Action action) {
        MtkLog.d("BaseSettings", "dispatchKeyEvent onActionClicked:" + action);
        if (action.mDataType != Action.DataType.LASTVIEW && action.mDataType != Action.DataType.SCANROOTVIEW && action.mDataType != Action.DataType.DIALOGPOP && action.mDataType != Action.DataType.SAVEDATA && action.mDataType != Action.DataType.PROGRESSBAR && action.hasRealChild) {
            this.mParentAction = this.mCurrAction;
            this.mCurrAction = action;
            this.mActionLevelStack.push(this.mParentAction);
            MtkLog.d("BaseSettings", "action.mDataType==" + action.mItemID + "," + action.mDataType);
            setState(action.mDataType, true);
        } else if (this.mCurrAction.mDataType == Action.DataType.OPTIONVIEW || this.mCurrAction.mDataType == Action.DataType.EFFECTOPTIONVIEW || this.mCurrAction.mDataType == Action.DataType.SWICHOPTIONVIEW) {
            String[] idValue = SettingsUtil.getRealIdAndValue(action.mItemID);
            if (idValue != null) {
                try {
                    int value = Integer.parseInt(idValue[1]);
                    int i = this.mCurrAction.mInitValue;
                    this.mCurrAction.mInitValue = value;
                    this.mCurrAction.setDescription(value);
                    MtkLog.d("BaseSettings", "des:" + this.mCurrAction.getDescription() + ",initVal:" + this.mCurrAction.mInitValue);
                    if (!this.mCurrAction.mItemID.startsWith("DVBS_DETAIL_")) {
                        if (this.mCurrAction.getCallBack() != null) {
                            this.mCurrAction.getCallBack().afterOptionValseChanged(this.mCurrAction.getDescription());
                        }
                        this.mConfigManager.setValue(idValue[0], value, this.mCurrAction);
                    } else if (this.mCurrAction.getCallBack() != null) {
                        this.mCurrAction.getCallBack().afterOptionValseChanged(this.mCurrAction.getDescription());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (this.mSpecialOptionDealer != null) {
                    this.mSpecialOptionDealer.specialOptionClick(this.mCurrAction);
                }
            }
            if (this.mCurrAction.mDataType == Action.DataType.EFFECTOPTIONVIEW) {
                dealEffectOptionValues(this.mCurrAction);
            } else if (this.mCurrAction.mDataType == Action.DataType.SWICHOPTIONVIEW) {
                dealSwitchChildItemEnable(this.mCurrAction);
                if (this.mSpecialOptionDealer != null) {
                    this.mSpecialOptionDealer.specialOptionClick(this.mCurrAction);
                }
            }
            goBack();
        }
        super.onActionClicked(action);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0 && (event.getKeyCode() == 66 || event.getKeyCode() == 23)) {
            long nowMills = System.currentTimeMillis();
            long spaceMills = nowMills - this.enterPressTime;
            MtkLog.d("BaseSettings", "dispatchKeyEvent nowMills:" + spaceMills);
            if (Math.abs(spaceMills) <= this.ENTER_DELAYMILLS) {
                return true;
            }
            this.enterPressTime = nowMills;
        }
        return super.dispatchKeyEvent(event);
    }

    public void goBack() {
        if (this.mState.equals(getInitialDataType())) {
            if (this.mALoader.isTaskRunning()) {
                this.mALoader.cancelTask();
            }
            finish();
        } else if (getPrevState() != null) {
            this.mState = this.mStateStack.pop();
            this.mCurrAction = this.mActionLevelStack.pop();
            MtkLog.d("BaseSettings", "curr action is== " + this.mCurrAction.mDataType);
            this.mParentAction = getPrevAction();
            StringBuilder sb = new StringBuilder();
            sb.append("back prevaction is null== ");
            sb.append(this.mParentAction == null);
            MtkLog.d("BaseSettings", sb.toString());
            getFragmentManager().popBackStackImmediate();
            this.mActionFragment = getActionFragment();
            if (this.mActionFragment == null || !(this.mActionFragment instanceof ActionFragment)) {
                MtkLog.d("BaseSettings", "which frag");
                return;
            }
            MtkLog.d("BaseSettings", "action frag");
            refreshActionList();
            ((ActionAdapter) ((ActionFragment) this.mActionFragment).getAdapter()).setActions(this.mActions);
        }
    }

    public void finish() {
        setResult(-1, new Intent());
        super.finish();
    }

    /* access modifiers changed from: protected */
    public Object getPrevState() {
        if (this.mStateStack.isEmpty()) {
            return null;
        }
        return this.mStateStack.peek();
    }

    /* access modifiers changed from: protected */
    public Action getPrevAction() {
        if (this.mActionLevelStack.isEmpty()) {
            return null;
        }
        return this.mActionLevelStack.peek();
    }

    public void dealEffectOptionValues(Action mainAction) {
        List<Action> mEeffectGroup = mainAction.getmEffectGroup();
        int[] initValues = this.mDataHelper.getEffectGroupInitValues(mainAction);
        if (this.mActionFragment != null && (this.mActionFragment instanceof ActionFragment)) {
            ActionFragment actFrag = (ActionFragment) this.mActionFragment;
            for (int i = 0; i < mEeffectGroup.size(); i++) {
                Action effectChildData = mEeffectGroup.get(i);
                MtkLog.d("BaseSettings", "itemId,initValues[i]==" + effectChildData.mItemID + "," + initValues[i]);
                effectChildData.setmInitValue(initValues[i]);
                effectChildData.setDescription(initValues[i]);
            }
            ((ActionAdapter) actFrag.getAdapter()).notifyDataSetChanged();
        }
    }

    public void dealSwitchChildItemEnable(Action mainAction) {
        this.mDataHelper.dealSwitchChildGroupEnable(mainAction);
    }

    public void loadData() {
        MtkLog.d("BaseSettings", "loadData begin....");
        MtkLog.d("BaseSettings", "loadData ....");
        setActionsForTopView();
    }

    public void loadFinished() {
        MtkLog.d("BaseSettings", "loadFinished begin....");
        getWaitingBar().setVisibility(8);
        if (this.mActionFragment != null && (this.mActionFragment instanceof ActionFragment)) {
            MtkLog.d("BaseSettings", "loadFinished setDatas....");
            ActionFragment actFrag = (ActionFragment) this.mActionFragment;
            if (((ActionAdapter) actFrag.getAdapter()) != null) {
                ((ActionAdapter) actFrag.getAdapter()).setActions(this.mActions);
            }
        }
    }

    public void loadStarting() {
        ProgressBar waiting = getWaitingBar();
        if (!(waiting == null || waiting.getVisibility() == 0)) {
            waiting.setVisibility(0);
        }
        MtkLog.d("BaseSettings", "loadStarting begin....");
    }

    /* access modifiers changed from: protected */
    public void refreshListView() {
        if (this.mActionFragment != null && (this.mActionFragment instanceof ActionFragment)) {
            MtkLog.d("BaseSettings", "loadFinished setDatas....");
            ((ActionAdapter) ((ActionFragment) this.mActionFragment).getAdapter()).notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        MtkLog.d("BaseSettings", "base setting-now onRestart");
        LiveTvSetting.tryToStartTV(this);
        super.onRestart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d("BaseSettings", "base setting-now onStop");
        super.onStop();
    }
}
