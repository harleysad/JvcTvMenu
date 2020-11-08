package com.mediatek.wwtv.tvcenter.epg.cn;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGTypeListAdapter;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.List;

public class EpgType extends Dialog {
    public static final String COUNTRY_AUS = "AUS";
    public static final String COUNTRY_NZL = "NZL";
    public static final String COUNTRY_UK = "GBR";
    private static final String TAG = "EpgType";
    private static boolean disableSubFilter = false;
    public static boolean mHasEditType;
    private static int menuHeight = 610;
    private static int menuWidth = 800;
    ListView epgList;
    ListView epgSubList;
    EPGTypeListAdapter listAdapter;
    private Context mContext;
    List<EPGTypeListAdapter.EPGListViewDataItem> mData;
    private String mEpgCountry;
    EPGTypeListAdapter subAdapter;

    public EpgType(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public EpgType(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public EpgType(Context context) {
        super(context, 2131755397);
        this.mContext = context;
    }

    private boolean SubFilterDisable() {
        this.mEpgCountry = MtkTvConfig.getInstance().getCountry();
        MtkLog.d(TAG, "scube" + this.mEpgCountry);
        if (this.mEpgCountry.equals("GBR") || this.mEpgCountry.equals("NZL") || this.mEpgCountry.equals("AUS")) {
            return true;
        }
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (disableSubFilter) {
            setContentView(R.layout.epg_type_eu_main);
        } else {
            setContentView(R.layout.epg_type_main);
        }
        setWindowPosition();
        this.epgList = (ListView) findViewById(R.id.nav_epg_type_view);
        this.epgList.setDividerHeight(0);
        if (!disableSubFilter) {
            this.epgSubList = (ListView) findViewById(R.id.nav_epg_sub_type_view);
            this.epgSubList.setDividerHeight(0);
        }
        this.listAdapter = new EPGTypeListAdapter(this.mContext, this.epgList, this.epgSubList);
        if (!disableSubFilter) {
            this.subAdapter = new EPGTypeListAdapter(this.mContext, this.epgList, this.epgSubList);
        }
        if (this.mEpgCountry != null && this.mEpgCountry.equals("GBR")) {
            this.mData = this.listAdapter.loadEPGFilterTypeData("GBR");
        } else if (this.mEpgCountry == null || !this.mEpgCountry.equals("AUS")) {
            this.mData = this.listAdapter.loadEPGFilterTypeData("");
        } else {
            this.mData = this.listAdapter.loadEPGFilterTypeData("AUS");
        }
        this.listAdapter.setEPGGroup(this.mData);
        if (this.mData != null) {
            this.epgList.setAdapter(this.listAdapter);
            MtkLog.d(TAG, "*********** mData is not null**************");
            if (!disableSubFilter) {
                this.subAdapter.setEPGGroup(this.mData.get(0).getSubChildDataItem());
            }
        } else {
            MtkLog.d(TAG, "*********** mData is null **************");
        }
        if (!disableSubFilter) {
            this.epgSubList.setAdapter(this.subAdapter);
        }
        this.epgList.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || !EpgType.this.listAdapter.isMfocus()) {
                    return false;
                }
                EpgType.this.listAdapter.onMainKey(v, keyCode);
                return false;
            }
        });
        if (!disableSubFilter) {
            this.epgList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    MtkLog.d(EpgType.TAG, "epgList.setOnItemSelectedListener Position:" + position + ">>>" + EpgType.this.listAdapter.isMfocus());
                    if (EpgType.this.listAdapter.isMfocus()) {
                        EpgType.this.subAdapter.setEPGGroup(EpgType.this.listAdapter.getEPGData().get(position).getSubChildDataItem());
                        EpgType.this.epgSubList.setAdapter(EpgType.this.subAdapter);
                    }
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            this.epgSubList.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != 0 || EpgType.this.subAdapter.isMfocus()) {
                        return false;
                    }
                    EpgType.this.subAdapter.onSubKey(v, keyCode);
                    return false;
                }
            });
            this.epgSubList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    MtkLog.d(EpgType.TAG, "epgSubList.setOnItemSelectedListener Position:" + position);
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!(keyCode == 4 || keyCode == 30)) {
            if (keyCode != 172) {
                if (keyCode != 178) {
                    if (keyCode != 186) {
                        switch (keyCode) {
                            case 24:
                            case 25:
                                break;
                        }
                    }
                }
                return true;
            }
            MtkLog.d(TAG, "event.getRepeatCount()>>>" + event.getRepeatCount());
            if (event.getRepeatCount() <= 0) {
                if (isShowing()) {
                    dismiss();
                    mHasEditType = false;
                }
                ((EPGCnActivity) this.mContext).onKeyDown(keyCode, event);
            }
            return super.onKeyDown(keyCode, event);
        }
        if (isShowing()) {
            dismiss();
            EPGCnActivity epgActivity = (EPGCnActivity) this.mContext;
            epgActivity.changeBottomViewText(false, 0);
            if (mHasEditType) {
                epgActivity.notifyEPGLinearlayoutRefresh();
                mHasEditType = false;
            }
        }
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyUp>>>>" + keyCode + "  " + event.getAction());
        return super.onKeyUp(keyCode, event);
    }

    public void setWindowPosition() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        menuWidth = (650 * ScreenConstant.SCREEN_WIDTH) / 1280;
        menuHeight = (610 * ScreenConstant.SCREEN_HEIGHT) / 720;
        if (disableSubFilter) {
            menuWidth /= 2;
        }
        lp.width = menuWidth;
        lp.height = menuHeight;
        window.setAttributes(lp);
    }
}
