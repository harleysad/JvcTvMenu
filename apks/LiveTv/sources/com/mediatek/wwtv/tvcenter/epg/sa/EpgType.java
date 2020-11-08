package com.mediatek.wwtv.tvcenter.epg.sa;

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
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGTypeListAdapter;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.List;

public class EpgType extends Dialog {
    private static final String TAG = "EpgType";
    private static int menuHeight = 610;
    private static int menuWidth = 800;
    ListView epgList;
    ListView epgSubList;
    EPGTypeListAdapter listAdapter;
    private Context mContext;
    List<EPGTypeListAdapter.EPGListViewDataItem> mData;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_type_main);
        setWindowPosition();
        this.epgList = (ListView) findViewById(R.id.nav_epg_type_view);
        this.epgSubList = (ListView) findViewById(R.id.nav_epg_sub_type_view);
        this.epgList.setDividerHeight(0);
        this.epgSubList.setDividerHeight(0);
        this.listAdapter = new EPGTypeListAdapter(this.mContext, this.epgList, this.epgSubList);
        this.subAdapter = new EPGTypeListAdapter(this.mContext, this.epgList, this.epgSubList);
        this.mData = this.listAdapter.loadEPGFilterTypeData();
        this.listAdapter.setEPGGroup(this.mData);
        if (this.mData != null) {
            this.epgList.setAdapter(this.listAdapter);
            MtkLog.d(TAG, "*********** mData is not null**************");
            this.subAdapter.setEPGGroup(this.mData.get(0).getSubChildDataItem());
        } else {
            MtkLog.d(TAG, "*********** mData is null **************");
        }
        this.epgSubList.setAdapter(this.subAdapter);
        this.epgList.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || !EpgType.this.listAdapter.isMfocus()) {
                    return false;
                }
                EpgType.this.listAdapter.onMainKey(v, keyCode);
                return false;
            }
        });
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

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0011, code lost:
        if (r4 != 186) goto L_0x005c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r4, android.view.KeyEvent r5) {
        /*
            r3 = this;
            r0 = 4
            if (r4 == r0) goto L_0x0047
            r0 = 30
            if (r4 == r0) goto L_0x0047
            r0 = 172(0xac, float:2.41E-43)
            if (r4 == r0) goto L_0x0016
            r0 = 178(0xb2, float:2.5E-43)
            if (r4 == r0) goto L_0x0014
            r0 = 186(0xba, float:2.6E-43)
            if (r4 == r0) goto L_0x0047
            goto L_0x005c
        L_0x0014:
            r0 = 1
            return r0
        L_0x0016:
            java.lang.String r0 = "EpgType"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "event.getRepeatCount()>>>"
            r1.append(r2)
            int r2 = r5.getRepeatCount()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            int r0 = r5.getRepeatCount()
            if (r0 > 0) goto L_0x005c
            boolean r0 = r3.isShowing()
            if (r0 == 0) goto L_0x003f
            r3.dismiss()
        L_0x003f:
            android.content.Context r0 = r3.mContext
            com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r0 = (com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity) r0
            r0.onKeyDown(r4, r5)
            goto L_0x005c
        L_0x0047:
            boolean r0 = r3.isShowing()
            if (r0 == 0) goto L_0x005c
            r3.dismiss()
            android.content.Context r0 = r3.mContext
            com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity r0 = (com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity) r0
            r1 = 0
            r0.changeBottomViewText(r1, r1)
            r0.notifyEPGLinearlayoutRefresh()
        L_0x005c:
            boolean r0 = super.onKeyDown(r4, r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.sa.EpgType.onKeyDown(int, android.view.KeyEvent):boolean");
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
        lp.width = menuWidth;
        lp.height = menuHeight;
        window.setAttributes(lp);
    }
}
