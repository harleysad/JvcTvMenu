package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvGinga;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.ConfirmDialog;
import com.mediatek.wwtv.tvcenter.commonview.CustListView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GingaTvDialog extends NavBasicDialog implements ComponentStatusListener.ICStatusListener, ConfirmDialog.IResultCallback {
    private static final String RECOVER_VOLUME = "recover_volume_value";
    private static final String TAG = "GingaTvDialog";
    private static int mHeight = 390;
    private static int mPerPage = 10;
    private static int mWidth = 650;
    private boolean isFirstSet;
    private AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public Map<String, String> mCurrentApps;
    /* access modifiers changed from: private */
    public CustListView mCustListView;
    private boolean mGetGingaInfo;
    /* access modifiers changed from: private */
    public GingaTvAdapter mGingaTvAdapter;
    private Handler mHandler;
    private LinearLayout mLinearLayout;
    /* access modifiers changed from: private */
    public String mSelectedAppId;
    private TextView mTextViewMessage;
    private final CustListView.UpDateListView mUpDateListView;

    public GingaTvDialog(Context context) {
        this(context, R.style.nav_dialog);
    }

    public GingaTvDialog(Context context, int theme) {
        super(context, theme);
        this.mHandler = null;
        this.mCustListView = null;
        this.mTextViewMessage = null;
        this.mLinearLayout = null;
        this.mGingaTvAdapter = null;
        this.mSelectedAppId = "";
        this.mCurrentApps = null;
        this.mGetGingaInfo = false;
        this.isFirstSet = true;
        this.mUpDateListView = new CustListView.UpDateListView() {
            public void updata() {
                MtkLog.d(GingaTvDialog.TAG, "UpDateListView ");
                if (GingaTvDialog.this.mCustListView.getSelectedItemPosition() == 0) {
                    GingaTvDialog.this.mGingaTvAdapter.updateData(GingaTvDialog.this.mCustListView.getPreList());
                } else {
                    GingaTvDialog.this.mGingaTvAdapter.updateData(GingaTvDialog.this.mCustListView.getCurrentList());
                }
                GingaTvDialog.this.mCustListView.setAdapter(GingaTvDialog.this.mGingaTvAdapter);
            }
        };
        this.componentID = NavBasic.NAV_COMP_ID_GINGA_TV;
        this.mCurrentApps = new HashMap();
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(6, this);
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public boolean isKeyHandler(int keyCode) {
        if (keyCode == 215) {
            return true;
        }
        return false;
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777218 || componentID == 16777235 || componentID == 16777241) {
            return true;
        }
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        boolean isHandled = true;
        MtkLog.d(TAG, "KeyHandler: keyCode=" + keyCode);
        int keyCode2 = KeyMap.getKeyCode(keyCode, event);
        switch (keyCode2) {
            case 4:
            case 215:
                dismiss();
                break;
            case 35:
            case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                MtkLog.d(TAG, "KEYCODE_MTKIR_GREEN");
                if (this.mCurrentApps != null && this.mCurrentApps.size() > mPerPage) {
                    if (this.mCustListView.getSelectedItemPosition() == mPerPage - 1) {
                        this.mGingaTvAdapter.updateData(this.mCustListView.getNextList());
                    }
                    this.mCustListView.setSelection(mPerPage - 1);
                }
                return true;
            case 46:
            case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                MtkLog.d(TAG, "KEYCODE_MTKIR_RED");
                if (this.mCurrentApps != null && this.mCurrentApps.size() > mPerPage) {
                    if (this.mCustListView.getSelectedItemPosition() == 0) {
                        this.mGingaTvAdapter.updateData(this.mCustListView.getPreList());
                    }
                    this.mCustListView.setSelection(0);
                }
                return true;
            case 82:
            case KeyMap.KEYCODE_MTKIR_PIPPOP /*171*/:
            case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
            case KeyMap.KEYCODE_MTKIR_SOURCE /*178*/:
                dismiss();
                isHandled = false;
                break;
            case 86:
            case 10062:
                dismiss();
                isHandled = false;
                break;
            case KeyMap.KEYCODE_MTKIR_MUTE /*164*/:
                break;
            default:
                isHandled = false;
                break;
        }
        if (isHandled || TurnkeyUiMainActivity.getInstance() == null) {
            return isHandled;
        }
        return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode2, event);
    }

    public boolean initView() {
        this.mHandler = new InternalHandler(this);
        setContentView(R.layout.nav_ginga_tv);
        this.mCustListView = (CustListView) findViewById(R.id.nav_ginga_tv_listview);
        this.mTextViewMessage = (TextView) findViewById(R.id.nav_ginga_message);
        this.mLinearLayout = (LinearLayout) findViewById(R.id.nav_ginga_tv_pageupdown);
        this.mGingaTvAdapter = new GingaTvAdapter(getContext());
        this.mCustListView.setAdapter(this.mGingaTvAdapter);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        clearApps();
        this.mGetGingaInfo = true;
        MtkTvGinga.getInstance().getApplicationInfo();
        setWindowPosition();
        updateList();
        int value = MtkTvConfig.getInstance().getConfigValue("g_ginga__ginga_enable");
        if (this.mCurrentApps.size() == 0 || value == 0) {
            MtkLog.d(TAG, "show no apps");
            this.mCustListView.setVisibility(8);
            this.mTextViewMessage.setVisibility(0);
        } else {
            MtkLog.d(TAG, "show apps");
            this.mCustListView.setVisibility(0);
            this.mTextViewMessage.setVisibility(8);
            this.mCustListView.setFocusable(true);
            this.mCustListView.setSelection(0);
            this.mCustListView.requestFocus();
        }
        startTimeout(5000);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        MtkLog.d(TAG, "dispatchKeyEvent: keyCode=" + keyCode);
        startTimeout(5000);
        if (event.getAction() != 0 || (keyCode != 23 && keyCode != 33 && keyCode != 66)) {
            return super.dispatchKeyEvent(event);
        }
        MtkLog.d(TAG, "selected item:" + this.mCustListView.getSelectedItem());
        dismiss();
        MtkTvConfig config = MtkTvConfig.getInstance();
        int dig_cc = config.getConfigValue("g_cc__cc_digital_cc");
        int super_cc = config.getConfigValue("g_cc__cc_digital_cc");
        if (dig_cc == 1 || super_cc != 0) {
            Log.d(TAG, "come in dialog handle WARNING_MSG_STOP_CC_START_GINGA");
        }
        String selected = (String) this.mCustListView.getSelectedItem();
        if (this.mCurrentApps != null && this.mCurrentApps.containsValue(selected)) {
            for (String key : this.mCurrentApps.keySet()) {
                if (this.mCurrentApps.get(key).equals(selected)) {
                    if (this.mSelectedAppId == null || !this.mSelectedAppId.equals(key)) {
                        MtkTvGinga.getInstance().startApplication(key);
                        InfoBarDialog.getInstance(this.mContext).show(0, this.mContext.getString(R.string.nav_ginga_tv_for_infobar));
                    } else {
                        MtkLog.d(TAG, "startApplication, key=" + key);
                        if (this.mSelectedAppId.length() > 0) {
                            MtkTvGinga.getInstance().stopApplication(key);
                        }
                    }
                }
            }
        }
        return true;
    }

    private void setWindowPosition() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (mWidth * ScreenConstant.SCREEN_WIDTH) / 1280;
        lp.height = (mHeight * ScreenConstant.SCREEN_HEIGHT) / 720;
        lp.x = (ScreenConstant.SCREEN_WIDTH / 2) - (lp.width / 2);
        lp.y = ((ScreenConstant.SCREEN_HEIGHT / 2) - (lp.height / 2)) - 50;
        MtkLog.d(TAG, "ScreenConstant.SCREEN_WIDTH=" + ScreenConstant.SCREEN_WIDTH + ",ScreenConstant.SCREEN_HEIGHT=" + ScreenConstant.SCREEN_HEIGHT + ",lp.width=" + lp.width + "," + lp.x + ", lp.height=" + lp.height + "," + lp.y);
        window.setAttributes(lp);
    }

    private boolean updateList() {
        if (this.mCurrentApps == null || this.mCurrentApps.size() <= mPerPage) {
            this.mLinearLayout.setVisibility(4);
        } else {
            this.mLinearLayout.setVisibility(0);
        }
        if (this.mCurrentApps == null || this.mCurrentApps.size() <= 0) {
            return false;
        }
        List<String> list = new ArrayList<>();
        for (String key : this.mCurrentApps.keySet()) {
            list.add(this.mCurrentApps.get(key));
            MtkLog.d(TAG, "create list:" + this.mCurrentApps.get(key));
        }
        this.mCustListView.initData(list, mPerPage, this.mUpDateListView);
        this.mGingaTvAdapter.updateData(this.mCustListView.getCurrentList());
        return true;
    }

    private boolean isAppStarted(String id) {
        if (id == this.mSelectedAppId) {
            return true;
        }
        return false;
    }

    private class GingaTvAdapter extends BaseAdapter {
        private List<String> mCurrentList = null;
        private final LayoutInflater mInflater;

        public GingaTvAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            if (this.mCurrentList != null) {
                return this.mCurrentList.size();
            }
            return 0;
        }

        public String getItem(int position) {
            if (this.mCurrentList != null) {
                return this.mCurrentList.get(position);
            }
            return "";
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public void updateData(List<String> mCurrentApps) {
            this.mCurrentList = mCurrentApps;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder hodler;
            String selectName;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nav_ginga_tv_item, (ViewGroup) null);
                hodler = new ViewHolder();
                hodler.mImageView = (ImageView) convertView.findViewById(R.id.nav_ginga_tv_item_imageView);
                hodler.mTextView = (TextView) convertView.findViewById(R.id.nav_ginga_tv_item_textView);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHolder) convertView.getTag();
            }
            String mCurrentApp = this.mCurrentList.get(position);
            hodler.mTextView.setText(mCurrentApp);
            if (GingaTvDialog.this.mCurrentApps == null || (selectName = (String) GingaTvDialog.this.mCurrentApps.get(GingaTvDialog.this.mSelectedAppId)) == null || !selectName.equals(mCurrentApp)) {
                hodler.mImageView.setImageResource(0);
                return convertView;
            }
            hodler.mImageView.setImageResource(R.drawable.nav_source_list_select_icon);
            return convertView;
        }

        private class ViewHolder {
            ImageView mImageView;
            TextView mTextView;

            private ViewHolder() {
            }
        }
    }

    private static class InternalHandler extends Handler {
        private final WeakReference<GingaTvDialog> mDialog;

        public InternalHandler(GingaTvDialog dialog) {
            this.mDialog = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            MtkLog.d(GingaTvDialog.TAG, "[InternalHandler] handlerMessage occur~");
            if (this.mDialog.get() != null) {
            }
        }
    }

    public void changeVolume(int type, int level) {
        MtkLog.d(TAG, "changeVolume||type =" + type + "||level =" + level);
        switch (type) {
            case 0:
                int minValue = this.mAudioManager.getStreamMinVolume(3);
                int currentValue = this.mAudioManager.getStreamVolume(3);
                int newValue = (((currentValue - minValue) * level) / 100) + minValue;
                if (this.isFirstSet) {
                    MtkLog.d(TAG, "changeVolume||currentValue =" + currentValue);
                    this.isFirstSet = false;
                    SaveValue.getInstance(this.mContext).saveValue(RECOVER_VOLUME, currentValue);
                } else {
                    int savedValue = SaveValue.getInstance(this.mContext).readValue(RECOVER_VOLUME, -1);
                    if (savedValue != -1) {
                        newValue = minValue + (((savedValue - minValue) * level) / 100);
                    }
                }
                this.mAudioManager.setStreamVolume(3, newValue, 4);
                MtkLog.d(TAG, "changeVolume||minValue =" + minValue + "||newValue =" + newValue);
                return;
            case 2:
                this.mAudioManager.adjustVolume(-100, 4);
                return;
            case 3:
                this.mAudioManager.adjustVolume(100, 4);
                return;
            case 4:
                this.isFirstSet = true;
                int recoverValue = SaveValue.getInstance(this.mContext).readValue(RECOVER_VOLUME, -1);
                MtkLog.d(TAG, "changeVolume||recoverValue =" + recoverValue);
                if (recoverValue != -1) {
                    this.mAudioManager.setStreamVolume(3, recoverValue, 4);
                    SaveValue.getInstance(this.mContext).saveValue(RECOVER_VOLUME, -1);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void addGingaAppInfo(int type, String id, String name) {
        if (type == 2) {
            this.mSelectedAppId = id;
            MtkLog.d(TAG, "CommonIntegration.getInstance().getCurrentScreenMode()" + CommonIntegration.getInstance().getCurrentScreenMode());
            if (6 == CommonIntegration.getInstance().getCurrentScreenMode()) {
                MtkLog.d(TAG, "setCurrentScreenMode(CommonIntegration.SCREEN_MODE_NORMAL)");
                CommonIntegration.getInstance().setCurrentScreenMode(1);
            }
            if (InfoBarDialog.getInstance(this.mContext).isInfoIn()) {
                InfoBarDialog.getInstance(this.mContext).dismiss(0);
            }
            boolean isBannerActive = false;
            Iterator<Integer> it = ComponentsManager.getInstance().getCurrentActiveComps().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Integer compId = it.next();
                MtkLog.d(TAG, "compId:" + compId);
                if (compId.intValue() == 16777218) {
                    isBannerActive = true;
                    break;
                }
            }
            if (!this.mGetGingaInfo && !isBannerActive) {
                ComponentsManager.getInstance().hideAllComponents();
            }
            this.mGetGingaInfo = false;
            ComponentsManager.updateActiveCompId(true, NavBasic.NAV_NATIVE_COMP_ID_GINGA);
        } else if (type == 1) {
            if (this.mSelectedAppId.equals(id)) {
                this.mSelectedAppId = "";
            }
        } else if (type == 0) {
            if (this.mSelectedAppId.equals(id)) {
                this.mSelectedAppId = "";
            }
            ComponentsManager.updateActiveCompId(true, 0);
            ((TwinkleView) ComponentsManager.getInstance().getComponentById(16777232)).showHandler();
        } else {
            return;
        }
        if (this.mCurrentApps == null || this.mCurrentApps.size() >= 100) {
            MtkLog.d(TAG, "addGingaAppInfo: mCurrentApps = null or space full");
        } else {
            this.mCurrentApps.put(id, name);
            MtkLog.d(TAG, "addGingaAppInfo: name:" + name + ",id:" + id + ",mSelectedAppId=" + this.mSelectedAppId);
        }
        if (isVisible()) {
            updateList();
            if (this.mCurrentApps == null || this.mCurrentApps.size() == 0) {
                MtkLog.d(TAG, "show no apps");
                this.mCustListView.setVisibility(8);
                this.mTextViewMessage.setVisibility(0);
                return;
            }
            MtkLog.d(TAG, "show apps");
            this.mCustListView.setVisibility(0);
            this.mTextViewMessage.setVisibility(8);
            this.mCustListView.setFocusable(true);
            this.mCustListView.setSelection(0);
            this.mCustListView.requestFocus();
        }
    }

    public void handleSvctxMessage(int code) {
        MtkLog.d(TAG, " handleSvctxMessage code = " + code);
        if (code == 1 || code == 2) {
            clearApps();
        }
    }

    public void clearApps() {
        this.mCurrentApps.clear();
        this.mSelectedAppId = "";
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 10) {
            if (value != -1) {
                clearApps();
                ComponentsManager.updateActiveCompId(true, 0);
            }
        } else if (statusID == 1) {
            if (!ComponentsManager.getInstance().isComponentsShow()) {
                ComponentsManager.nativeComponentReActive();
            }
        } else if (statusID == 6) {
            MtkLog.d(TAG, "stop ginga app by NAV_ENTER_LANCHER, mSelectedAppId=" + this.mSelectedAppId);
            if (this.mSelectedAppId != null) {
                int length = this.mSelectedAppId.length();
            }
        } else if (statusID == 3) {
            MtkTvGinga.getInstance().getApplicationInfo();
        }
    }

    public void handleUserSelection(int result) {
        MtkLog.d(TAG, "handleUserSelection, result=" + result);
        if (result == 0) {
            MtkTvConfig.getInstance().setConfigValue("g_cc__cc_digital_cc", 0);
            String selected = (String) this.mCustListView.getSelectedItem();
            if (this.mCurrentApps != null && this.mCurrentApps.containsValue(selected)) {
                for (String key : this.mCurrentApps.keySet()) {
                    if (this.mCurrentApps.get(key).equals(selected) && !this.mSelectedAppId.equals(key)) {
                        MtkLog.d(TAG, "startApplication, key=" + key);
                        MtkTvGinga.getInstance().startApplication(key);
                        InfoBarDialog.getInstance(this.mContext).show(0, this.mContext.getString(R.string.nav_ginga_tv_for_infobar));
                    }
                }
            }
        }
    }

    public void dismiss() {
        super.dismiss();
        if (isShowing()) {
            dismiss();
        }
    }

    public void show() {
        MtkLog.d(TAG, "ginga is showing");
        super.show();
    }
}
