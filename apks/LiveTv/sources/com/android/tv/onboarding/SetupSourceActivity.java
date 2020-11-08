package com.android.tv.onboarding;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.SetupPassthroughActivity;
import com.android.tv.common.actions.InputSetupActionUtils;
import com.android.tv.common.ui.setup.OnActionClickListener;
import com.android.tv.common.ui.setup.animation.SetupAnimationHelper;
import com.android.tv.dialog.PinDialogFragment;
import com.android.tv.util.OnboardingUtils;
import com.android.tv.util.SetupUtils;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.wwtv.setting.base.scan.ui.ScanDialogActivity;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.List;

public class SetupSourceActivity extends BaseActivity implements OnActionClickListener, PinDialogFragment.OnPinCheckedListener {
    private static final int REQUEST_CODE_START_SETUP_ACTIVITY = 1;
    private static final String TAG = "SetupSourceActivity";
    private String mInputIdUnderSetup;
    private TvInputInfo mInputInfo;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetupAnimationHelper.initialize(this);
        setContentView(R.layout.activity_setupsource);
        SetupSourcesFragment setupFragment = new SetupSourcesFragment();
        setupFragment.enableFragmentTransition(15);
        setupFragment.setFragmentTransition(2, GravityCompat.END);
        getFragmentManager().beginTransaction().replace(R.id.container, setupFragment).commit();
    }

    public boolean onActionClick(String category, int id, Bundle params) {
        MtkLog.d(TAG, "category:" + category + " id:" + id + " params:" + params);
        if (!category.equals(SetupSourcesFragment.ACTION_CATEGORY)) {
            return true;
        }
        if (id != Integer.MAX_VALUE) {
            switch (id) {
                case 1:
                    try {
                        startActivity(OnboardingUtils.ONLINE_STORE_INTENT);
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                case 2:
                    if (params == null) {
                        return true;
                    }
                    this.mInputInfo = ((DestroyApp) getApplicationContext()).getTvInputManagerHelper().getTvInputInfo(params.getString("input_id"));
                    if (!this.mInputInfo.getServiceInfo().packageName.equals("com.mediatek.tvinput")) {
                        handleSetupInput(this.mInputInfo);
                        return true;
                    } else if (TVContent.getInstance(this).isTvInputBlock() || EditChannel.getInstance(this).getBlockChannelNumForSource() > 0) {
                        Log.d(TAG, "SetupSourceActivity show Pwd");
                        PinDialogFragment.create(7).show(getFragmentManager(), "PinDialogFragment");
                        return true;
                    } else {
                        handleSetupInput(this.mInputInfo);
                        return true;
                    }
                default:
                    return true;
            }
        } else {
            finish();
            return true;
        }
    }

    private void handleSetupInput(TvInputInfo input) {
        if (!input.getServiceInfo().packageName.equals("com.mediatek.tvinput") || MarketRegionInfo.getCurrentMarketRegion() == 3) {
            Intent intent = createSetupIntent(input.createSetupIntent(), input.getId());
            MtkLog.d(TAG, input.toString());
            if (intent == null) {
                Toast.makeText(this, R.string.msg_no_setup_activity, 0).show();
                return;
            }
            intent.setComponent(new ComponentName(this, SetupPassthroughActivity.class));
            try {
                this.mInputIdUnderSetup = input.getId();
                SetupUtils.grantEpgPermission(this, input.getServiceInfo().packageName);
                startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException e) {
                this.mInputIdUnderSetup = null;
                Toast.makeText(this, getString(R.string.msg_unable_to_start_setup_activity, new Object[]{input.loadLabel(this)}), 0).show();
            }
        } else if (MtkTvScan.getInstance().isScanning()) {
            Toast.makeText(this, R.string.menu_string_toast_scanning_background, 0).show();
        } else {
            Intent intent2 = new Intent(this, ScanDialogActivity.class);
            intent2.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN);
            startActivity(intent2);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String text;
        MtkLog.v(TAG, "onActivityResult, requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == 1) {
            if (resultCode == -1) {
                int count = ((DestroyApp) getApplicationContext()).getChannelDataManager().getChannelCountForInput(this.mInputIdUnderSetup);
                MtkLog.d(TAG, "count:" + count);
                if (count <= 0) {
                    text = getString(R.string.msg_no_channel_added);
                } else if (count == 1) {
                    text = getString(R.string.msg_channel_added_one, new Object[]{Integer.valueOf(count)});
                } else {
                    text = getString(R.string.msg_no_channel_added_other, new Object[]{Integer.valueOf(count)});
                }
                Toast.makeText(this, text, 0).show();
            }
            try {
                ContentValues values = new ContentValues();
                values.put("browsable", 1);
                values.put("searchable", 1);
                int count2 = getContentResolver().update(TvContract.Channels.CONTENT_URI, values, "input_id = ?", new String[]{this.mInputIdUnderSetup});
                MtkLog.d(TAG, "count after:" + count2 + ":" + this.mInputIdUnderSetup);
                if (count2 == 0 && !this.mInputIdUnderSetup.contains("com.mediatek.tvinput")) {
                    selectOther3rdChannel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mInputIdUnderSetup = null;
        }
    }

    private void selectOther3rdChannel() {
        List<TIFChannelInfo> list = TIFChannelManager.getInstance(this).get3RDChannelList();
        TIFChannelInfo foundInfo = null;
        if (list.size() > 0) {
            for (TIFChannelInfo info : list) {
                MtkLog.d(TAG, "list input id=" + info.mInputServiceName);
                if (!this.mInputIdUnderSetup.equals(info.mInputServiceName)) {
                    foundInfo = info;
                }
            }
            if (foundInfo != null) {
                MtkLog.d(TAG, "foundInfo input id=" + foundInfo.mInputServiceName);
                TIFChannelManager.getInstance(this).selectChannelByTIFInfo(foundInfo);
                return;
            }
            setBroadCastChannelList();
            return;
        }
        setBroadCastChannelList();
    }

    private void setBroadCastChannelList() {
        SaveValue instance = SaveValue.getInstance(this);
        instance.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
        SaveValue.getInstance(this).saveValue(CommonIntegration.channelListfortypeMask, CommonIntegration.CH_LIST_MASK);
        SaveValue.getInstance(this).saveValue(CommonIntegration.channelListfortypeMaskvalue, CommonIntegration.CH_LIST_VAL);
    }

    public Intent createSetupIntent(Intent originalSetupIntent, String inputId) {
        if (originalSetupIntent == null) {
            return null;
        }
        Intent setupIntent = new Intent(originalSetupIntent);
        if (InputSetupActionUtils.hasInputSetupAction(originalSetupIntent)) {
            return setupIntent;
        }
        Intent intentContainer = new Intent("com.android.tv.action.LAUNCH_INPUT_SETUP");
        intentContainer.putExtra("com.android.tv.extra.SETUP_INTENT", originalSetupIntent);
        intentContainer.putExtra("android.media.tv.extra.INPUT_ID", inputId);
        return intentContainer;
    }

    public void onPinChecked(boolean checked, int type, String rating) {
        if (type == 7 && checked) {
            handleSetupInput(this.mInputInfo);
        }
    }
}
