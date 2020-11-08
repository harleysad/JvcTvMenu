package com.mediatek.wwtv.setting.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.preference.Preference;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.wwtv.setting.preferences.PreferenceData;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class FacSetup implements Preference.OnPreferenceClickListener {
    private static final int MESSAGE_RESET = 34;
    private static final int MESSAGE_SEND_RESET = 35;
    protected static final String TAG = "FacSetup";
    private static FacSetup mInstance = null;
    /* access modifiers changed from: private */
    public static CommonIntegration mNavIntegration = null;
    /* access modifiers changed from: private */
    public LiveTVDialog factroyCofirm = null;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public MenuDataHelper mDataHelper;
    /* access modifiers changed from: private */
    public EditChannel mEditChannel;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public PreferenceData mPrefData;
    /* access modifiers changed from: private */
    public ProgressDialog pdialog = null;

    private FacSetup(Context context) {
        this.mContext = context;
        this.mPrefData = PreferenceData.getInstance(this.mContext);
        this.mEditChannel = EditChannel.getInstance(this.mContext);
        this.mDataHelper = MenuDataHelper.getInstance(this.mContext);
        mNavIntegration = CommonIntegration.getInstance();
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 34:
                        MtkLog.d(FacSetup.TAG, "MESSAGE_RESET, " + System.currentTimeMillis());
                        FacSetup.this.mPrefData.mTV.cleanRatings();
                        FacSetup.this.mDataHelper.resetCallFlashStore();
                        MtkTvUtil.getInstance().resetFac();
                        FacSetup.this.mEditChannel.resetParental(FacSetup.this.mContext, new Runnable() {
                            public void run() {
                                FacSetup.this.mHandler.sendEmptyMessage(35);
                            }
                        });
                        return;
                    case 35:
                        MtkLog.d(FacSetup.TAG, "MESSAGE_SEND_RESET, " + System.currentTimeMillis());
                        SettingsUtil.sendResetBroadcast(FacSetup.this.mContext);
                        if (FacSetup.this.pdialog != null) {
                            FacSetup.this.pdialog.dismiss();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
    }

    public static FacSetup getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FacSetup(context);
        } else {
            mInstance.mContext = context;
        }
        return mInstance;
    }

    public boolean onPreferenceClick(Preference preference) {
        MtkLog.d(TAG, "onPreferenceClick, " + preference);
        if (preference.getKey().equals(MenuConfigManager.FACTORY_SETUP_CI_UPDATE)) {
            factoryCi(1);
        } else if (preference.getKey().equals(MenuConfigManager.FACTORY_SETUP_CI_ECP_UPDATE)) {
            factoryCi(3);
        } else if (preference.getKey().equals(MenuConfigManager.FACTORY_SETUP_CI_ERASE)) {
            factoryCi(2);
        } else if (!preference.getKey().equals(MenuConfigManager.FACTORY_SETUP_CI_QUERY)) {
            if (preference.getKey().equals(MenuConfigManager.FACTORY_SETUP_CLEAN_STORAGE)) {
                factoryCleanStorage();
            } else if (preference.getKey().equals(MenuConfigManager.RESET_DEFAULT)) {
                resetDefault();
            } else if (preference.getKey().equals(MenuConfigManager.PARENTAL_CLEAN_ALL)) {
                cleanParentalChannelConfirm();
            } else if (preference.getKey().equals(MenuConfigManager.TV_CHANNEL_CLEAR)) {
                cleanChannelList();
            }
        }
        return true;
    }

    private void factoryCi(int state) {
        this.factroyCofirm = new LiveTVDialog(this.mContext, 0);
        switch (state) {
            case 0:
                this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_cleanstorage));
                break;
            case 1:
                int update = this.mPrefData.mTV.updateCIKey();
                if (update != 0) {
                    if (update != -9) {
                        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_update_false));
                        break;
                    } else {
                        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_key_false));
                        break;
                    }
                } else {
                    this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_update_success));
                    break;
                }
            case 2:
                if (this.mPrefData.mTV.eraseCIKey() != 0) {
                    this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_erase_false));
                    break;
                } else {
                    this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_erase_success));
                    break;
                }
            case 3:
                int updateecp = this.mPrefData.mTV.updateCIECPKey();
                if (updateecp != 0) {
                    if (updateecp != -9) {
                        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_update_false));
                        break;
                    } else {
                        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_key_false));
                        break;
                    }
                } else {
                    this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_ci_update_success));
                    break;
                }
        }
        this.factroyCofirm.show();
        this.factroyCofirm.setPositon(-20, 70);
    }

    private void factoryCleanStorage() {
        this.factroyCofirm = new LiveTVDialog(this.mContext, 3);
        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_factory_setup_cleanstorage));
        this.factroyCofirm.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.factroyCofirm.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.factroyCofirm.show();
        this.factroyCofirm.setPositon(-20, 70);
        this.factroyCofirm.getButtonNo().requestFocus();
        View.OnKeyListener listener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && (keyCode == 66 || keyCode == 23 || keyCode == 183)) {
                    if (v.getId() == FacSetup.this.factroyCofirm.getButtonYes().getId()) {
                        FacSetup.this.factroyCofirm.dismiss();
                        ProgressDialog unused = FacSetup.this.pdialog = ProgressDialog.show(FacSetup.this.mContext, "Clean Storage", "Reseting please wait...", false, false);
                        FacSetup.this.mHandler.sendEmptyMessage(34);
                        return true;
                    } else if (v.getId() == FacSetup.this.factroyCofirm.getButtonNo().getId()) {
                        FacSetup.this.factroyCofirm.dismiss();
                        return true;
                    }
                }
                return false;
            }
        };
        this.factroyCofirm.getButtonNo().setOnKeyListener(listener);
        this.factroyCofirm.getButtonYes().setOnKeyListener(listener);
    }

    private void resetDefault() {
        this.factroyCofirm = new LiveTVDialog(this.mContext, 3);
        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_tv_reset_all));
        this.factroyCofirm.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.factroyCofirm.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.factroyCofirm.show();
        this.factroyCofirm.getTextView().setTextSize(13.5f);
        this.factroyCofirm.getButtonNo().requestFocus();
        this.factroyCofirm.setPositon(-20, 70);
        this.factroyCofirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23 && keyCode != 183)) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                EditChannel.getInstance(FacSetup.this.mContext).resetDefAfterClean();
                ProgressDialog unused = FacSetup.this.pdialog = ProgressDialog.show(FacSetup.this.mContext, "ResetDefault", "Reseting please wait...", false, false);
                FacSetup.this.mPrefData.mTV.resetPub(FacSetup.this.mHandler);
                FacSetup.this.mDataHelper.resetCallFlashStore();
                return true;
            }
        };
        this.factroyCofirm.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23 && keyCode != 183) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                return true;
            }
        });
        this.factroyCofirm.getButtonYes().setOnKeyListener(yesListener);
    }

    private void cleanParentalChannelConfirm() {
        this.factroyCofirm = new LiveTVDialog(this.mContext, 3);
        this.factroyCofirm.setMessage(this.mContext.getString(R.string.menu_tv_clear_channel_info));
        this.factroyCofirm.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.factroyCofirm.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.factroyCofirm.show();
        this.factroyCofirm.getTextView().setTextSize(13.5f);
        this.factroyCofirm.getButtonNo().requestFocus();
        this.factroyCofirm.setPositon(-20, 70);
        this.factroyCofirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23 && keyCode != 183)) {
                    return false;
                }
                FacSetup.this.mDataHelper.resetCallFlashStore();
                FacSetup.this.mEditChannel.resetDefAfterClean();
                ProgressDialog unused = FacSetup.this.pdialog = ProgressDialog.show(FacSetup.this.mContext, "Clean All", "Reseting please wait...", false, false);
                FacSetup.this.mEditChannel.resetParental(FacSetup.this.mContext, new Runnable() {
                    public void run() {
                        FacSetup.this.factroyCofirm.dismiss();
                    }
                });
                FacSetup.this.mPrefData.mTV.resetPri(FacSetup.this.mHandler);
                return true;
            }
        };
        this.factroyCofirm.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23 && keyCode != 183) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                return true;
            }
        });
        this.factroyCofirm.getButtonYes().setOnKeyListener(yesListener);
    }

    private void cleanChannelList() {
        MtkLog.d(TAG, "initCleanDialog start stop session getCurrentFocus " + mNavIntegration.getCurrentFocus());
        String info = this.mContext.getString(R.string.menu_tv_clear_channel_info);
        this.factroyCofirm = new LiveTVDialog(this.mContext, 3);
        this.factroyCofirm.setMessage(info);
        this.factroyCofirm.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        this.factroyCofirm.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        this.factroyCofirm.show();
        this.factroyCofirm.getTextView().setTextSize(13.5f);
        this.factroyCofirm.getButtonNo().requestFocus();
        this.factroyCofirm.setPositon(-20, 70);
        this.factroyCofirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 66 && keyCode != 23 && keyCode != 183)) {
                    return false;
                }
                MtkLog.d(FacSetup.TAG, "initCleanDialog() start");
                EditChannel.getInstance(FacSetup.this.mContext).cleanChannelList();
                FacSetup.this.factroyCofirm.dismiss();
                FacSetup.this.mDataHelper.changePreferenceEnable();
                FacSetup.this.mPrefData.invalidate(MenuConfigManager.TV_CHANNEL_CLEAR, 1);
                MtkLog.d(FacSetup.TAG, "initCleanDialog() start 1");
                if (FacSetup.this.mPrefData.mTV.getConfigValue("g_misc__ch_list_type") > 0) {
                    FacSetup.this.mPrefData.mTV.setConfigValue("g_misc__ch_list_type", 0);
                }
                MtkLog.d(FacSetup.TAG, "initCleanDialog start stop session getCurrentFocus " + FacSetup.mNavIntegration.getCurrentFocus());
                InputSourceManager inputSourceManager = InputSourceManager.getInstance();
                if (FacSetup.mNavIntegration.getCurrentFocus().equals("sub")) {
                    MtkLog.d(FacSetup.TAG, "initCleanDialog sub");
                    inputSourceManager.stopPipSession();
                    inputSourceManager.changeCurrentInputSourceByName(inputSourceManager.getCurrentInputSourceName("sub"));
                } else if (FacSetup.mNavIntegration.getCurrentFocus().equals("main")) {
                    inputSourceManager.stopSession();
                    inputSourceManager.changeCurrentInputSourceByName(inputSourceManager.getCurrentInputSourceName("main"));
                    MtkLog.d(FacSetup.TAG, "initCleanDialog main");
                }
                MtkLog.d(FacSetup.TAG, "initCleanDialog end stop session");
                return true;
            }
        };
        this.factroyCofirm.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23 && keyCode != 183) {
                    return false;
                }
                FacSetup.this.factroyCofirm.dismiss();
                return true;
            }
        });
        this.factroyCofirm.getButtonYes().setOnKeyListener(yesListener);
    }
}
