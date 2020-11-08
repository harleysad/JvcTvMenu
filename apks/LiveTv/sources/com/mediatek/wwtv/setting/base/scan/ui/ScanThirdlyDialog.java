package com.mediatek.wwtv.setting.base.scan.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter;
import com.mediatek.wwtv.setting.base.scan.model.APTargetRegion;
import com.mediatek.wwtv.setting.base.scan.model.IRegionChangeInterface;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.model.ScannerManager;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScanThirdlyDialog extends Dialog {
    /* access modifiers changed from: private */
    public static String TAG = "ScanThirdlyDialog";
    Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentGroupIndex = 0;
    TVContent mTV;
    ThirdItemAdapter tRDAdapter;
    ListView trdItemsListView;
    RelativeLayout trdRootLayout;
    int whichViewType = 0;

    public ScanThirdlyDialog(Context context, int whichView) {
        super(context, 2131755419);
        this.mContext = context;
        this.mTV = TVContent.getInstance(this.mContext);
        this.whichViewType = whichView;
        switch (whichView) {
            case 1:
                showTRDFavNetWorkChoices();
                return;
            case 2:
                showTRDSelectRegion();
                return;
            case 3:
                showTRDConflictChannels();
                return;
            case 4:
                showTRDLCNv2Choices();
                return;
            case 5:
                showOrderChoices();
                return;
            default:
                return;
        }
    }

    public ScanThirdlyDialog(Context context, int whichDVBSOP, int satID) {
        super(context, 2131755419);
        this.mContext = context;
        this.mTV = TVContent.getInstance(this.mContext);
        switch (whichDVBSOP) {
            case 1:
                showTRDDVBSBATChoices(satID);
                return;
            case 2:
                showTRDDVBSTKGSServiceList(satID);
                return;
            case 3:
                showTricolorChannelList();
                return;
            default:
                return;
        }
    }

    private void showTricolorChannelList() {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        ((TextView) view.findViewById(R.id.scan_fav_network_str)).setText("Channel List");
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = dvbsScan.dvbsGetNfyGetInfo();
        MtkLog.d(TAG, "showTricolorChannelList ret:" + dvbsRet);
        List<String> strings = new ArrayList<>();
        final List<Integer> ids = new ArrayList<>();
        if (dvbsRet == MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK && dvbsScan.nyfGetInfo_list != null && dvbsScan.nyfGetInfo_list.length == dvbsScan.nfyGetInfo_lstNfyNum) {
            for (int i = 0; i < dvbsScan.nfyGetInfo_lstNfyNum; i++) {
                MtkTvScanDvbsBase.OneRECNfyData data = dvbsScan.nyfGetInfo_list[i];
                strings.add(data.recName);
                ids.add(Integer.valueOf(data.recId));
            }
        }
        if (strings.isEmpty() != 0 || ids.isEmpty()) {
            MtkLog.d(TAG, "showTricolorChannelList strings isEmpty:");
            return;
        }
        this.trdItemsListView.setAdapter(new ArrayAdapter(this.mContext, 17367046, strings));
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (ScanThirdlyDialog.this.mContext instanceof ScanViewActivity) {
                    ((ScanViewActivity) ScanThirdlyDialog.this.mContext).reStartDVBSFullScanAfterTricolorChannelList(((Integer) ids.get(position)).intValue());
                }
                ScanThirdlyDialog.this.dismiss();
            }
        });
        this.trdItemsListView.setSelection(0);
        this.trdItemsListView.requestFocus();
        getWindow().setContentView(view);
        getWindow().setLayout(-2, -2);
    }

    public void showTRDSelectRegion() {
        MtkLog.d(TAG, "showTRDSelectRegion()");
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        ((TextView) view.findViewById(R.id.scan_fav_network_str)).setText(this.mContext.getString(R.string.select_region_title));
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        List<ThirdItemAdapter.ThirdItem> items = prepareTRDSelectRegionAction(this.mContext, this.mTV.getScanManager(), 0, 0, 0);
        this.mTV.getScanManager().getRegionMgr().setOnRegionChangeListener(new IRegionChangeInterface() {
            public void onRegionChange(List<ThirdItemAdapter.ThirdItem> items) {
                int position = ScanThirdlyDialog.this.trdItemsListView.getSelectedItemPosition();
                ScanThirdlyDialog.this.reMapRegions(items);
                ScanThirdlyDialog.this.trdItemsListView.setSelection(position);
            }
        });
        reMapRegions(items);
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                ScanThirdlyDialog.this.saveAfterScanGBRRegionInfo();
                ScanThirdlyDialog.this.trdItemsListView.setSelection(position);
                ((ScanDialogActivity) ScanThirdlyDialog.this.mContext).showCompleteInfo();
                ScanThirdlyDialog.this.dismiss();
            }
        });
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode == 23) {
                    MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                    ScanThirdlyDialog.this.saveAfterScanGBRRegionInfo();
                    ((ScanDialogActivity) ScanThirdlyDialog.this.mContext).showCompleteInfo();
                    ScanThirdlyDialog.this.dismiss();
                    return false;
                } else if (keyCode == 21) {
                    MtkLog.d(ScanThirdlyDialog.TAG, "region to left");
                    ScanThirdlyDialog.this.tRDAdapter.optionTurnLeft(ScanThirdlyDialog.this.trdItemsListView.getSelectedView(), (String[]) null);
                    return false;
                } else if (keyCode != 22) {
                    return false;
                } else {
                    MtkLog.d(ScanThirdlyDialog.TAG, "region to right");
                    ScanThirdlyDialog.this.tRDAdapter.optionTurnRight(ScanThirdlyDialog.this.trdItemsListView.getSelectedView(), (String[]) null);
                    return false;
                }
            }
        });
        this.trdItemsListView.setSelection(0);
        this.trdItemsListView.requestFocus();
        getWindow().setContentView(view);
        getWindow().setLayout(-2, -2);
    }

    /* access modifiers changed from: private */
    public void reMapRegions(List<ThirdItemAdapter.ThirdItem> items) {
        this.tRDAdapter = new ThirdItemAdapter(this.mContext, items);
        this.trdItemsListView.setAdapter(this.tRDAdapter);
    }

    public void saveAfterScanGBRRegionInfo() {
        MtkLog.d(TAG, "saveAfterScanGBRRegionInfo(),");
        ThirdItemAdapter.ThirdItem temp = (ThirdItemAdapter.ThirdItem) this.tRDAdapter.getItem(0);
        String region1 = temp.optionValues[temp.optionValue];
        ThirdItemAdapter.ThirdItem temp2 = (ThirdItemAdapter.ThirdItem) this.tRDAdapter.getItem(1);
        String region2 = temp2.optionValues[temp2.optionValue];
        ThirdItemAdapter.ThirdItem temp3 = (ThirdItemAdapter.ThirdItem) this.tRDAdapter.getItem(2);
        String region3 = temp3.optionValues[temp3.optionValue];
        String str = TAG;
        MtkLog.d(str, "region1: " + region1);
        String str2 = TAG;
        MtkLog.d(str2, "region2: " + region2);
        String str3 = TAG;
        MtkLog.d(str3, "region3: " + region3);
        MtkTvScanDvbtBase.TargetRegion region = getTaragetRegionObj(region1, region2, region3);
        if (region != null) {
            MtkTvScanDvbtBase.ScanDvbtRet rect = MtkTvScan.getInstance().getScanDvbtInstance().uiOpSetTargetRegion(region);
            String str4 = TAG;
            MtkLog.d(str4, "saveAfterScanGBRRegionInfo()," + rect.name());
            return;
        }
        MtkLog.d(TAG, "saveAfterScanGBRRegionInfo(),TargetRegion=====null");
    }

    private MtkTvScanDvbtBase.TargetRegion getTaragetRegionObj(String level1, String level2, String level3) {
        MtkTvScanDvbtBase.TargetRegion[] regionList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetTargetRegion();
        int level = 3;
        String name = level3;
        String noRegion = this.mContext.getString(R.string.scan_trd_uk_reg_reg_nodefine);
        if (level3.equalsIgnoreCase(noRegion)) {
            level = 2;
            name = level2;
        }
        if (level2.equalsIgnoreCase(noRegion)) {
            level = 1;
            name = level1;
        }
        int i = 0;
        MtkLog.d(TAG, String.format("level:%d,name:%s", new Object[]{Integer.valueOf(level), name}));
        while (true) {
            int i2 = i;
            if (i2 >= regionList.length) {
                return null;
            }
            if (regionList[i2].level == level && regionList[i2].name.equalsIgnoreCase(name)) {
                return regionList[i2];
            }
            i = i2 + 1;
        }
    }

    public List<ThirdItemAdapter.ThirdItem> prepareTRDSelectRegionAction(Context context, ScannerManager scanMgr, int level1, int level2, int level3) {
        int i;
        char c;
        final Context context2 = context;
        String str = TAG;
        MtkLog.d(str, "prepareTRDSelectRegionAction," + String.format("level1:%d,level2:%d,level3:%d", new Object[]{Integer.valueOf(level1), Integer.valueOf(level2), Integer.valueOf(level3)}));
        int level12 = Math.max(0, level1);
        int level22 = Math.max(0, level2);
        int level32 = Math.max(0, level3);
        List<ThirdItemAdapter.ThirdItem> items = new ArrayList<>();
        HashMap<Integer, APTargetRegion> regions = scanMgr.getRegionsOfGBR();
        final List<APTargetRegion> leveL1RegionsObj = new ArrayList<>();
        leveL1RegionsObj.addAll(regions.values());
        int level1Int = Math.min(level12, leveL1RegionsObj.size());
        ArrayList arrayList = new ArrayList();
        if (leveL1RegionsObj.size() > 0) {
            arrayList.addAll(leveL1RegionsObj.get(level12).getChildren().values());
        }
        int level2Int = Math.min(level22, arrayList.size());
        ArrayList arrayList2 = new ArrayList();
        if (arrayList.size() > 0) {
            arrayList2.addAll(((APTargetRegion) arrayList.get(level22)).getChildren().values());
        }
        int level3Int = Math.min(level32, arrayList2.size());
        String[] level1Array = regionsObjToStringArray(context2, leveL1RegionsObj);
        String[] level2Array = regionsObjToStringArray(context2, arrayList);
        String[] level3Array = regionsObjToStringArray(context2, arrayList2);
        String str2 = TAG;
        MtkLog.d(str2, "level1Array," + Arrays.asList(level1Array).toString());
        String str3 = TAG;
        MtkLog.d(str3, "level2Array," + Arrays.asList(level2Array).toString());
        String str4 = TAG;
        MtkLog.d(str4, "level3Array," + Arrays.asList(level3Array).toString());
        String title1 = context2.getString(R.string.scan_trd_uk_reg_reg_x, new Object[]{1});
        ThirdItemAdapter.ThirdItem thirdItem = new ThirdItemAdapter.ThirdItem(MenuConfigManager.TV_CHANNEL_AFTER_SCAN_UK_REGION, title1, level1Int, level1Array, true);
        items.add(thirdItem);
        String title2 = context2.getString(R.string.scan_trd_uk_reg_reg_x, new Object[]{2});
        ThirdItemAdapter.ThirdItem thirdItem2 = new ThirdItemAdapter.ThirdItem(title2, title2, level2Int, level2Array, true);
        String[] level1Array2 = level1Array;
        ArrayList arrayList3 = arrayList2;
        String[] level2Array2 = level2Array;
        if (level2Array[0].equals(context2.getString(R.string.scan_trd_uk_reg_reg_nodefine))) {
            c = 0;
            thirdItem2.isEnable = false;
            i = 1;
        } else {
            c = 0;
            i = 1;
            thirdItem2.isEnable = true;
        }
        items.add(thirdItem2);
        Object[] objArr = new Object[i];
        objArr[c] = 3;
        String title3 = context2.getString(R.string.scan_trd_uk_reg_reg_x, objArr);
        ThirdItemAdapter.ThirdItem level3Regions = new ThirdItemAdapter.ThirdItem(title3, title3, level3Int, level3Array, true);
        if (level3Array[0].equals(context2.getString(R.string.scan_trd_uk_reg_reg_nodefine))) {
            level3Regions.isEnable = false;
        } else {
            level3Regions.isEnable = true;
        }
        items.add(level3Regions);
        String str5 = title2;
        final ScannerManager scannerManager = scanMgr;
        thirdItem.setValueChangeListener(new ThirdItemAdapter.ThirdItem.OnValueChangeListener() {
            public void afterValueChanged(String afterName) {
                scannerManager.getRegionMgr().getOnRegionChangeListener().onRegionChange(ScanThirdlyDialog.this.prepareTRDSelectRegionAction(context2, scannerManager, leveL1RegionsObj.indexOf(new APTargetRegion(-1, -1, -1, -1, -1, afterName)), 0, 0));
            }
        });
        String[] strArr = level3Array;
        String str6 = title1;
        String[] strArr2 = level2Array2;
        AnonymousClass6 r9 = r0;
        String[] strArr3 = level1Array2;
        final ArrayList arrayList4 = arrayList;
        ThirdItemAdapter.ThirdItem thirdItem3 = thirdItem;
        final ScannerManager scannerManager2 = scannerManager;
        ThirdItemAdapter.ThirdItem thirdItem4 = level3Regions;
        ArrayList arrayList5 = arrayList3;
        final Context context3 = context2;
        ArrayList arrayList6 = arrayList;
        final int i2 = level1Int;
        AnonymousClass6 r0 = new ThirdItemAdapter.ThirdItem.OnValueChangeListener() {
            public void afterValueChanged(String afterName) {
                scannerManager2.getRegionMgr().getOnRegionChangeListener().onRegionChange(ScanThirdlyDialog.this.prepareTRDSelectRegionAction(context3, scannerManager2, i2, arrayList4.indexOf(new APTargetRegion(-1, -1, -1, -1, -1, afterName)), 0));
            }
        };
        thirdItem2.setValueChangeListener(r9);
        return items;
    }

    private static String[] regionsObjToStringArray(Context context, List<APTargetRegion> leveL1RegionsObj) {
        String[] regions;
        if (leveL1RegionsObj == null || leveL1RegionsObj.size() <= 0) {
            regions = new String[]{context.getString(R.string.scan_trd_uk_reg_reg_nodefine)};
        } else {
            regions = new String[leveL1RegionsObj.size()];
            for (int i = 0; i < regions.length; i++) {
                regions[i] = leveL1RegionsObj.get(i).name;
            }
        }
        return regions;
    }

    public void showTRDFavNetWorkChoices() {
        MtkLog.d(TAG, "showTRDFavNetWorkChoices()");
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        this.trdItemsListView.setAdapter(new ArrayAdapter(this.mContext, 17367046, getTRDFavNetworkList()));
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                ScanThirdlyDialog.this.setTRDFavNetwork();
                ScanThirdlyDialog.this.dismiss();
            }
        });
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 23) {
                    return false;
                }
                MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                ScanThirdlyDialog.this.setTRDFavNetwork();
                ScanThirdlyDialog.this.dismiss();
                return false;
            }
        });
        this.trdItemsListView.setSelection(0);
        this.trdItemsListView.requestFocus();
        getWindow().setContentView(view);
    }

    public List<String> getTRDFavNetworkList() {
        List<String> networkList = new ArrayList<>();
        MtkTvScanDvbtBase.FavNwk[] nwkList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetFavNwk();
        for (MtkTvScanDvbtBase.FavNwk favNwk : nwkList) {
            networkList.add(favNwk.networkName);
        }
        return networkList;
    }

    /* access modifiers changed from: private */
    public void setTRDFavNetwork() {
        MtkTvScanDvbtBase.FavNwk[] nwkList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetFavNwk();
        MtkTvScanDvbtBase.ScanDvbtRet rect = MtkTvScan.getInstance().getScanDvbtInstance().uiOpSetFavNwk(nwkList[Math.min(Math.max(0, this.trdItemsListView.getSelectedItemPosition()), nwkList.length - 1)]);
        String str = TAG;
        MtkLog.d(str, "saveAfterScanGBRRegionInfo()," + rect.name());
    }

    public void showTRDConflictChannels() {
        MtkLog.d(TAG, "showTRDConflictChannels()");
        final View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_lcn, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_lcn_conflict_list);
        view.findViewById(R.id.scan_lcn_stub_view);
        List<MtkTvScanDvbtBase.LcnConflictGroup> lcnList = getLcnConflictGroup(this.mContext);
        final int totalGroup = lcnList.size();
        String str = TAG;
        MtkLog.d(str, "showTRDConflictChannels() totalGroup==" + totalGroup);
        if (totalGroup != 0) {
            updateLCNChannelList(view, 0, lcnList);
            this.mCurrentGroupIndex = 0;
            this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                    ScanThirdlyDialog.this.saveLCNChannel(view, position);
                    if (ScanThirdlyDialog.this.mCurrentGroupIndex >= totalGroup) {
                        ScanThirdlyDialog.this.dismiss();
                    }
                }
            });
            this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    String access$100 = ScanThirdlyDialog.TAG;
                    MtkLog.d(access$100, "write settings to System Config22" + event.getAction() + ",keyCode" + 23);
                    if (event.getAction() != 0 || keyCode != 23) {
                        return false;
                    }
                    MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config22");
                    ScanThirdlyDialog.this.saveLCNChannel(view, ScanThirdlyDialog.this.trdItemsListView.getSelectedItemPosition());
                    if (ScanThirdlyDialog.this.mCurrentGroupIndex < totalGroup) {
                        return false;
                    }
                    ScanThirdlyDialog.this.dismiss();
                    return false;
                }
            });
            getWindow().setLayout(-2, -2);
            getWindow().setContentView(view);
        }
    }

    /* access modifiers changed from: private */
    public void saveLCNChannel(View view, int position) {
        saveLCNChannel(view, this.mCurrentGroupIndex, position);
    }

    private void saveLCNChannel(View view, int currentGoup, int position) {
        List<MtkTvScanDvbtBase.LcnConflictGroup> lcnList = getLcnConflictGroup(this.mContext);
        if (currentGoup < lcnList.size()) {
            int currentGroupSize = lcnList.get(currentGoup).channelName.length;
            if (position > currentGroupSize - 1 || currentGoup >= lcnList.size() - 1) {
                if (position > currentGroupSize - 1) {
                    ScanContent.restoreForAllLCNChannelsForMenu(this.mContext, currentGoup);
                } else {
                    ScanContent.setAfterScanLCNForMenu(this.mContext, currentGoup, position);
                }
                this.mCurrentGroupIndex = lcnList.size();
                return;
            }
            ScanContent.setAfterScanLCNForMenu(this.mContext, currentGoup, position);
            this.mCurrentGroupIndex++;
            updateLCNChannelList(view, this.mCurrentGroupIndex, lcnList);
        }
    }

    private void updateLCNChannelList(View view, int currentGoup, List<MtkTvScanDvbtBase.LcnConflictGroup> lcnList) {
        List<String> nextChannelList = getTRDChannelList(this.mContext, currentGoup);
        updateLCNTitle(view, String.format("%d/%d", new Object[]{Integer.valueOf(currentGoup + 1), Integer.valueOf(lcnList.size())}), nextChannelList.size() - 1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.mContext, 17367046);
        for (String str : nextChannelList) {
            adapter.add(str);
        }
        this.trdItemsListView.setAdapter(adapter);
        this.trdItemsListView.setSelection(0);
        this.trdItemsListView.requestFocus();
    }

    public List<MtkTvScanDvbtBase.LcnConflictGroup> getLcnConflictGroup(Context context) {
        MtkTvScanDvbtBase.LcnConflictGroup[] lcnList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLcnConflictGroup();
        String str = TAG;
        MtkLog.d(str, "getLcnConflictGroup" + lcnList.toString());
        return Arrays.asList(lcnList);
    }

    public List<String> getTRDChannelList(Context context, int index) {
        MtkTvScanDvbtBase.LcnConflictGroup nwkList = getLcnConflictGroup(context).get(index);
        List<String> networkList = new ArrayList<>();
        networkList.addAll(Arrays.asList(nwkList.channelName));
        String str = TAG;
        MtkLog.d(str, "getTRDChannelList-1" + Arrays.asList(nwkList.channelName));
        networkList.add(context.getString(R.string.scan_trd_lcn_CONFLICT_USE_DEFAULT));
        String str2 = TAG;
        MtkLog.d(str2, "getTRDChannelList-2" + networkList.toString());
        return networkList;
    }

    private void updateLCNTitle(View view, String index, int channelNum) {
        String str = TAG;
        MtkLog.d(str, "updateLCNTitle()," + String.format("index:%s,channelsNum:%d", new Object[]{index, Integer.valueOf(channelNum)}));
        String title1Str = this.mContext.getString(R.string.scan_trd_lcn_CONFLICT_INDEX);
        String title2Str = this.mContext.getString(R.string.scan_trd_lcn_CONFLICT_CHANL_NUM);
        ((TextView) view.findViewById(R.id.scan_lcn_group_name)).setText(String.format("%s %s", new Object[]{title1Str, index}));
        ((TextView) view.findViewById(R.id.scan_lcn_group_conflict_num)).setText(String.format("%s %d", new Object[]{title2Str, Integer.valueOf(channelNum)}));
    }

    public void showTRDLCNv2Choices() {
        MtkLog.d(TAG, "showTRDLCNv2Choices()");
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        ((TextView) view.findViewById(R.id.scan_fav_network_str)).setText(this.mContext.getString(R.string.scan_trd_lcnv2_title));
        this.trdItemsListView.setAdapter(new ArrayAdapter(this.mContext, 17367046, getTRDLCNv2ChannelList()));
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                ScanThirdlyDialog.this.setTRDLCNv2();
                ScanThirdlyDialog.this.dismiss();
            }
        });
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || keyCode != 23) {
                    return false;
                }
                MtkLog.d(ScanThirdlyDialog.TAG, "write settings to System Config");
                ScanThirdlyDialog.this.setTRDLCNv2();
                ScanThirdlyDialog.this.dismiss();
                return false;
            }
        });
        this.trdItemsListView.setSelection(0);
        this.trdItemsListView.requestFocus();
        getWindow().setContentView(view);
    }

    public void showOrderChoices() {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        ((TextView) view.findViewById(R.id.scan_fav_network_str)).setText(this.mContext.getString(R.string.scan_way));
        final List<String> LCNv2List = Arrays.asList(this.mContext.getResources().getStringArray(R.array.order_array));
        this.trdItemsListView.setAdapter(new ArrayAdapter(this.mContext, 17367046, LCNv2List));
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ScanThirdlyDialog.this.dismiss();
                String autoOrdering = ScanThirdlyDialog.this.mContext.getResources().getString(R.string.automatic_Channel_ordering);
                String mediaset = ScanThirdlyDialog.this.mContext.getResources().getString(R.string.mediaset_premium_ordering);
                String access$100 = ScanThirdlyDialog.TAG;
                MtkLog.d(access$100, "autoOrdering:" + autoOrdering + ",--,mediaset:" + mediaset + "," + ((String) LCNv2List.get(position)).equals(autoOrdering) + "," + ((String) LCNv2List.get(position)).equals(mediaset));
                if (((String) LCNv2List.get(position)).equals(autoOrdering)) {
                    MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_TERRESTRIAL_BRDCSTER, 1);
                }
                if (((String) LCNv2List.get(position)).equals(mediaset)) {
                    MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_TERRESTRIAL_BRDCSTER, 2);
                }
                String access$1002 = ScanThirdlyDialog.TAG;
                MtkLog.d(access$1002, "scanconfigOnItem" + MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_TERRESTRIAL_BRDCSTER));
                if (ScanThirdlyDialog.this.mContext instanceof ScanDialogActivity) {
                    ((ScanDialogActivity) ScanThirdlyDialog.this.mContext).startScan();
                }
            }
        });
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || keyCode != 23) {
                    return false;
                }
                ScanThirdlyDialog.this.dismiss();
                String selectScanType = (String) ScanThirdlyDialog.this.trdItemsListView.getSelectedItem();
                String access$100 = ScanThirdlyDialog.TAG;
                MtkLog.d(access$100, "selectscantype:" + selectScanType);
                String autoOrdering = ScanThirdlyDialog.this.mContext.getResources().getString(R.string.automatic_Channel_ordering);
                String mediaset = ScanThirdlyDialog.this.mContext.getResources().getString(R.string.mediaset_premium_ordering);
                String access$1002 = ScanThirdlyDialog.TAG;
                MtkLog.d(access$1002, "autoOrdering:" + autoOrdering + ",--,mediaset:" + mediaset + "," + selectScanType.equals(autoOrdering) + "," + selectScanType.equals(mediaset));
                if (selectScanType.equals(autoOrdering)) {
                    MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_TERRESTRIAL_BRDCSTER, 1);
                }
                if (selectScanType.equals(mediaset)) {
                    MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_TERRESTRIAL_BRDCSTER, 2);
                }
                String access$1003 = ScanThirdlyDialog.TAG;
                MtkLog.d(access$1003, "scanconfigOnKey" + MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_TERRESTRIAL_BRDCSTER));
                if (!(ScanThirdlyDialog.this.mContext instanceof ScanDialogActivity)) {
                    return false;
                }
                ((ScanDialogActivity) ScanThirdlyDialog.this.mContext).startScan();
                return false;
            }
        });
        this.trdItemsListView.setSelection(0);
        this.trdItemsListView.requestFocus();
        getWindow().setContentView(view);
        getWindow().setLayout(-2, -2);
    }

    /* access modifiers changed from: private */
    public void setTRDLCNv2() {
        MtkTvScanDvbtBase.LCNv2ChannelList[] lcnv2List = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLCNv2ChannelList();
        MtkTvScanDvbtBase.ScanDvbtRet rect = MtkTvScan.getInstance().getScanDvbtInstance().uiOpSetLCNv2ChannelList(lcnv2List[Math.min(Math.max(0, this.trdItemsListView.getSelectedItemPosition()), lcnv2List.length - 1)]);
        String str = TAG;
        MtkLog.d(str, "saveAfterScanGBRRegionInfo()," + rect.name());
    }

    public List<String> getTRDLCNv2ChannelList() {
        List<String> channelList = new ArrayList<>();
        MtkTvScanDvbtBase.LCNv2ChannelList[] lcnv2List = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLCNv2ChannelList();
        for (MtkTvScanDvbtBase.LCNv2ChannelList lCNv2ChannelList : lcnv2List) {
            channelList.add(lCNv2ChannelList.channelListName);
        }
        return channelList;
    }

    public void showTRDDVBSBATChoices(final int satID) {
        MtkLog.d(TAG, "showDVBSBATChoices()");
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        final List<MtkTvScanDvbsBase.OneBatData> batList = getDVBS_BATList();
        this.trdItemsListView.setAdapter(new ArrayAdapter(this.mContext, 17367046, getDVBS_BATStrList(batList)));
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(ScanThirdlyDialog.TAG, "show write settings to System Config");
                ScanThirdlyDialog.this.setDVBS_BAT(batList, satID);
                ScanThirdlyDialog.this.dismiss();
            }
        });
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode == 23) {
                    MtkLog.d(ScanThirdlyDialog.TAG, "show write settings to System Config KEYCODE_DPAD_CENTER");
                    ScanThirdlyDialog.this.setDVBS_BAT(batList, satID);
                    ScanThirdlyDialog.this.dismiss();
                    return true;
                } else if (keyCode != 4) {
                    return false;
                } else {
                    MtkLog.d(ScanThirdlyDialog.TAG, "show KeyEvent.KEYCODE_BACK");
                    ScanThirdlyDialog.this.dismiss();
                    return true;
                }
            }
        });
        this.trdItemsListView.setSelection(0);
        getWindow().setContentView(view);
    }

    /* access modifiers changed from: private */
    public void setDVBS_BAT(List<MtkTvScanDvbsBase.OneBatData> list, int satID) {
        ((ScanViewActivity) this.mContext).startDVBSFullScan(satID, list.get(Math.min(Math.max(0, this.trdItemsListView.getSelectedItemPosition()), list.size() - 1)).batId, -1, (String) null);
    }

    public static List<MtkTvScanDvbsBase.OneBatData> getDVBS_BATList() {
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        dvbsScan.dvbsGetNfyBatInfo();
        int num = dvbsScan.nfyBatInfo_batNum;
        String str = TAG;
        MtkLog.d(str, "num:" + num);
        MtkTvScanDvbsBase.OneBatData[] data = dvbsScan.nyfBatInfo_batList;
        for (int i = 0; i < data.length; i++) {
            String str2 = TAG;
            MtkLog.d(str2, "bat name:" + data[i].batName);
        }
        return Arrays.asList(data);
    }

    public static List<String> getDVBS_BATStrList(List<MtkTvScanDvbsBase.OneBatData> list) {
        List<String> batList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            batList.add(list.get(i).batName);
        }
        return batList;
    }

    public void showTRDDVBSTKGSServiceList(final int satID) {
        MtkLog.d(TAG, "showTRDDVBSTKGSServiceList()");
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.menu_main_scan_trd_fav_network2, (ViewGroup) null);
        this.trdItemsListView = (ListView) view.findViewById(R.id.scan_fav_network_list);
        ((TextView) view.findViewById(R.id.scan_fav_network_str)).setText("Please select a service list");
        MenuDataHelper helper = MenuDataHelper.getInstance(this.mContext);
        final List<MtkTvScanDvbsBase.TKGSOneSvcList> svcList = helper.getTKGSOneSvcList();
        List<String> batStrList = helper.getTKGSOneServiceStrList(svcList);
        int tkgsSvcListSelPos = helper.getTKGSOneServiceSelectValue();
        this.trdItemsListView.setAdapter(new ArrayAdapter(this.mContext, 17367046, batStrList));
        this.trdItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MtkLog.d(ScanThirdlyDialog.TAG, "onItemClick setServiceList for tkgs");
                ScanThirdlyDialog.this.setDVBS_TKGS(svcList, satID);
                ScanThirdlyDialog.this.dismiss();
            }
        });
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode == 23) {
                    MtkLog.d(ScanThirdlyDialog.TAG, "setServiceList for tkgs KEYCODE_DPAD_CENTER");
                    ScanThirdlyDialog.this.setDVBS_TKGS(svcList, satID);
                    ScanThirdlyDialog.this.dismiss();
                    return true;
                } else if (keyCode != 4) {
                    return false;
                } else {
                    MtkLog.d(ScanThirdlyDialog.TAG, "setServiceList show KeyEvent.KEYCODE_BACK");
                    ScanThirdlyDialog.this.dismiss();
                    return true;
                }
            }
        });
        this.trdItemsListView.setSelection(tkgsSvcListSelPos);
        getWindow().setContentView(view);
    }

    /* access modifiers changed from: private */
    public void setDVBS_TKGS(List<MtkTvScanDvbsBase.TKGSOneSvcList> list, int satID) {
        int svcListNo = list.get(Math.min(Math.max(0, this.trdItemsListView.getSelectedItemPosition()), list.size() - 1)).svcListNo;
        if (new MtkTvScanDvbsBase().dvbsTKGSSelSvcList(svcListNo).ordinal() == 0) {
            String str = TAG;
            MtkLog.d(str, "getTKGSOneSvcList set svcListNo to:" + svcListNo);
        }
        ((ScanViewActivity) this.mContext).startDVBSFullScan(satID, -1, 1, (String) null);
    }
}
