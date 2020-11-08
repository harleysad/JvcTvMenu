package com.mediatek.wwtv.tvcenter.nav.fav;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoriteListDialog extends NavBasicDialog implements DialogInterface.OnDismissListener, ComponentStatusListener.ICStatusListener {
    static final int CHANNEL_LIST_PAGE_MAX = 7;
    private static final int FAVOURITE_1 = 0;
    private static final int FAVOURITE_2 = 1;
    private static final int FAVOURITE_3 = 2;
    private static final int FAVOURITE_4 = 3;
    private static final String FAVOURITE_TYPE = "favouriteType";
    private static final int SET_SELECTION_INDEX = 4097;
    private static final String SPNAME = "CHMODE";
    private static final String TAG = "ShowFavoriteChannelListView";
    private static final int TYPE_CHANGECHANNEL_ENTER = 4101;
    private static final int TYPE_RESET_FAVOURITELIST = 4102;
    /* access modifiers changed from: private */
    public static ListView mFavoriteListView;
    /* access modifiers changed from: private */
    public int CURRENT_FAVOURITE_TYPE;
    private boolean canContinueChangeChannel;
    /* access modifiers changed from: private */
    public final CommonIntegration commonIntegration;
    private final FavChannelManager favChannelManager;
    private final int[] favMask;
    FavoriteListListener favoriteListListener;
    Handler handler;
    /* access modifiers changed from: private */
    public boolean hasNextPage;
    /* access modifiers changed from: private */
    public ChannelAdapter mChannelAdapter;
    private final List<Integer> mChannelIdList;
    private View.AccessibilityDelegate mFavListViewDelegate;
    private View.AccessibilityDelegate mFavListViewDelegateforType;
    private String mFavoriteChannelListTitle;
    private View mFavouriteListPageUpDownView;
    /* access modifiers changed from: private */
    public View mFavouriteListTipView;
    /* access modifiers changed from: private */
    public ListView mFavouriteTypeView;
    /* access modifiers changed from: private */
    public int mLastSelection;
    private String mNavExitString;
    private TextView mNavExitTextView;
    private String mNavFavoriteSelectString;
    private TextView mNavFavoriteSelectTextView;
    private TextView mNavPageUpTextView;
    private SaveValue mSaveValue;
    /* access modifiers changed from: private */
    public String mTitlePre;
    /* access modifiers changed from: private */
    public TextView mTitleTextView;
    /* access modifiers changed from: private */
    public String[] types;

    public FavoriteListDialog(Context context, int theme) {
        super(context, theme);
        this.CURRENT_FAVOURITE_TYPE = 0;
        this.favMask = new int[]{MtkTvChCommonBase.SB_VNET_FAVORITE1, MtkTvChCommonBase.SB_VNET_FAVORITE2, MtkTvChCommonBase.SB_VNET_FAVORITE3, MtkTvChCommonBase.SB_VNET_FAVORITE4};
        this.mLastSelection = 0;
        this.hasNextPage = false;
        this.canContinueChangeChannel = true;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i != 4097) {
                    switch (i) {
                        case 4101:
                            FavoriteListDialog.this.commonIntegration.selectChannelById(msg.arg1);
                            return;
                        case 4102:
                            List<MtkTvChannelInfoBase> tempChlist = (List) msg.obj;
                            if (tempChlist == null) {
                                MtkLog.d(FavoriteListDialog.TAG, "mChlist = null");
                                FavoriteListDialog.mFavoriteListView.setAdapter((ListAdapter) null);
                            } else {
                                ChannelAdapter unused = FavoriteListDialog.this.mChannelAdapter = new ChannelAdapter(FavoriteListDialog.this.mContext, tempChlist);
                                int index = FavoriteListDialog.this.mChannelAdapter.isExistCh(msg.arg1);
                                int selection = index < 0 ? FavoriteListDialog.this.mLastSelection : index;
                                if (selection > FavoriteListDialog.this.mChannelAdapter.getCount() - 1) {
                                    selection = 0;
                                }
                                int unused2 = FavoriteListDialog.this.mLastSelection = selection;
                                if (FavoriteListDialog.this.mChannelAdapter != null) {
                                    FavoriteListDialog.this.saveLastPosition(FavoriteListDialog.this.commonIntegration.getCurrentChannelId(), FavoriteListDialog.this.mChannelAdapter.getChannellist());
                                }
                                FavoriteListDialog.mFavoriteListView.setAdapter(FavoriteListDialog.this.mChannelAdapter);
                                MtkLog.d(FavoriteListDialog.TAG, "mChlist = " + tempChlist.size());
                                FavoriteListDialog.mFavoriteListView.setFocusable(true);
                                FavoriteListDialog.mFavoriteListView.requestFocus();
                                FavoriteListDialog.mFavoriteListView.setSelection(selection);
                            }
                            FavoriteListDialog.this.showPageUpDownView();
                            FavoriteListDialog.this.mFavouriteListTipView.setVisibility(0);
                            return;
                        default:
                            return;
                    }
                } else {
                    int chIndex = FavoriteListDialog.this.mChannelAdapter.isExistCh(FavoriteListDialog.this.commonIntegration.getCurrentChannelId());
                    if (chIndex >= 0) {
                        FavoriteListDialog.mFavoriteListView.requestFocus();
                        FavoriteListDialog.mFavoriteListView.setSelection(chIndex);
                        int unused3 = FavoriteListDialog.this.mLastSelection = chIndex;
                        return;
                    }
                    FavoriteListDialog.this.mChannelAdapter.updateData(FavoriteListDialog.this.getNextPrePageChList(true));
                    FavoriteListDialog.mFavoriteListView.requestFocus();
                    FavoriteListDialog.mFavoriteListView.setSelection(0);
                }
            }
        };
        this.favoriteListListener = new FavoriteListListener() {
            public void updateFavoriteList() {
                FavoriteListDialog.this.updateFavList();
            }
        };
        this.mFavListViewDelegate = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                MtkLog.d(FavoriteListDialog.TAG, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
                if (FavoriteListDialog.mFavoriteListView != host) {
                    MtkLog.d(FavoriteListDialog.TAG, "host:" + FavoriteListDialog.mFavoriteListView + "," + host);
                } else {
                    MtkLog.d(FavoriteListDialog.TAG, ":host =false");
                    List<CharSequence> texts = event.getText();
                    if (texts == null) {
                        MtkLog.d(FavoriteListDialog.TAG, "texts :" + texts);
                    } else if (event.getEventType() == 32768) {
                        int index = FavoriteListDialog.this.findSelectItem(texts.get(0).toString());
                        if (index >= 0) {
                            FavoriteListDialog.mFavoriteListView.setSelection(index);
                            MtkLog.d(FavoriteListDialog.TAG, ":index =" + index);
                            FavoriteListDialog.this.startTimeout(10000);
                        }
                    } else if (event.getEventType() == 1) {
                        MtkLog.d(FavoriteListDialog.TAG, "onRequestSendAccessibilityEvent: enterKeySelectChannel");
                        FavoriteListDialog.this.enterKeySelectChannel();
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d(FavoriteListDialog.TAG, "Exception " + e);
                    return true;
                }
            }
        };
        this.mFavListViewDelegateforType = new View.AccessibilityDelegate() {
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                MtkLog.d(FavoriteListDialog.TAG, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
                if (FavoriteListDialog.this.mFavouriteTypeView != host) {
                    MtkLog.d(FavoriteListDialog.TAG, "host:" + FavoriteListDialog.this.mFavouriteTypeView + "," + host);
                } else {
                    MtkLog.d(FavoriteListDialog.TAG, ":host =false");
                    List<CharSequence> texts = event.getText();
                    if (texts == null) {
                        MtkLog.d(FavoriteListDialog.TAG, "texts :" + texts);
                    } else if (event.getEventType() == 32768) {
                        int index = FavoriteListDialog.this.findSelectItemforType(texts.get(0).toString());
                        if (index >= 0 && index <= 3) {
                            FavoriteListDialog.this.mFavouriteTypeView.setSelection(index);
                            MtkLog.d(FavoriteListDialog.TAG, ":index =" + index);
                            FavoriteListDialog.this.startTimeout(10000);
                        }
                    } else if (event.getEventType() == 1) {
                        MtkLog.d(FavoriteListDialog.TAG, "onRequestSendAccessibilityEvent: enterKeySelectChannel");
                        FavoriteListDialog.this.resetFavouriteListViw();
                    }
                }
                try {
                    return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
                } catch (Exception e) {
                    Log.d(FavoriteListDialog.TAG, "Exception " + e);
                    return true;
                }
            }
        };
        MtkLog.d(TAG, "Constructor!");
        this.mContext = context;
        this.componentID = NavBasic.NAV_COMP_ID_FAV_LIST;
        this.commonIntegration = CommonIntegration.getInstance();
        this.favChannelManager = FavChannelManager.getInstance(this.mContext);
        this.mChannelIdList = new ArrayList();
        this.mSaveValue = SaveValue.getInstance(context);
        this.CURRENT_FAVOURITE_TYPE = this.mSaveValue.readValue(FAVOURITE_TYPE, 0);
        ComponentStatusListener lister = ComponentStatusListener.getInstance();
        lister.addListener(10, this);
        lister.addListener(2, this);
    }

    public FavoriteListDialog(Context context) {
        this(context, R.style.nav_dialog);
    }

    public void show() {
        MtkLog.d(TAG, "show");
        super.show();
        setWindowPosition();
        this.canContinueChangeChannel = true;
        this.mTitleTextView.setImportantForAccessibility(2);
        updateFavList();
        if (TextToSpeechUtil.isTTSEnabled(this.mContext)) {
            MtkLog.d(TAG, "TTS enable");
            mFavoriteListView.setAccessibilityDelegate(this.mFavListViewDelegate);
            this.mFavouriteTypeView.setAccessibilityDelegate(this.mFavListViewDelegateforType);
            return;
        }
        MtkLog.d(TAG, "TTS disEnable");
        mFavoriteListView.setAccessibilityDelegate((View.AccessibilityDelegate) null);
        this.mFavouriteTypeView.setAccessibilityDelegate((View.AccessibilityDelegate) null);
    }

    public boolean isCoExist(int componentID) {
        if (componentID == 16777218) {
            return false;
        }
        if (componentID == 16777235 || componentID == 16777241) {
            return true;
        }
        return super.isCoExist(componentID);
    }

    public boolean dispatchKeyToTimeshift() {
        int setUpTShiftValue = MtkTvConfig.getInstance().getConfigValue("g_record__rec_tshift_mode");
        MtkLog.d(TAG, "value:fav:" + setUpTShiftValue);
        if (setUpTShiftValue != 0) {
            return true;
        }
        if (!CommonIntegration.getInstance().isCurrentSourceATV() && MtkTvConfig.getInstance().getConfigValue("g_record__rec_tshift_mode") != 0) {
            return true;
        }
        return false;
    }

    public boolean isKeyHandler(int keyCode) {
        MtkLog.d(TAG, "isKeyHandler keyCode=" + keyCode);
        if (keyCode != 217 || this.commonIntegration.is3rdTVSource() || !CommonIntegration.getInstance().isCurrentSourceTv() || CommonIntegration.getInstance().isCurrentSourceBlocked() || !CommonIntegration.getInstance().hasActiveChannel()) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x0173  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0183 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean KeyHandler(int r6, android.view.KeyEvent r7, boolean r8) {
        /*
            r5 = this;
            java.lang.String r0 = "ShowFavoriteChannelListView"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "KeyHandler keyCode="
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 1
            r1 = 10000(0x2710, float:1.4013E-41)
            r5.startTimeout(r1)
            android.content.Context r1 = r5.mContext
            r2 = 0
            if (r1 != 0) goto L_0x0022
            return r2
        L_0x0022:
            r1 = 4
            if (r6 == r1) goto L_0x0167
            r1 = 23
            r3 = 5
            if (r6 == r1) goto L_0x015c
            r1 = 82
            if (r6 == r1) goto L_0x015a
            r1 = 93
            if (r6 == r1) goto L_0x0142
            r1 = 172(0xac, float:2.41E-43)
            if (r6 == r1) goto L_0x015a
            r1 = 178(0xb2, float:2.5E-43)
            if (r6 == r1) goto L_0x015a
            r1 = 229(0xe5, float:3.21E-43)
            if (r6 == r1) goto L_0x0137
            switch(r6) {
                case 85: goto L_0x0167;
                case 86: goto L_0x0136;
                default: goto L_0x0041;
            }
        L_0x0041:
            switch(r6) {
                case 166: goto L_0x0134;
                case 167: goto L_0x0134;
                default: goto L_0x0044;
            }
        L_0x0044:
            r1 = 1
            switch(r6) {
                case 183: goto L_0x00fe;
                case 184: goto L_0x00b1;
                case 185: goto L_0x007b;
                case 186: goto L_0x004b;
                default: goto L_0x0048;
            }
        L_0x0048:
            r0 = 0
            goto L_0x016b
        L_0x004b:
            android.widget.ListView r1 = mFavoriteListView
            java.lang.Object r1 = r1.getSelectedItem()
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1
            com.mediatek.wwtv.tvcenter.nav.fav.FavChannelManager r2 = r5.favChannelManager
            com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListListener r4 = r5.favoriteListListener
            r2.deleteFavorite(r1, r4)
            com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener r2 = com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener.getInstance()
            r2.updateStatus(r3, r6)
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r2 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r3 = 16777218(0x1000002, float:2.3509893E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r2 = r2.getComponentById(r3)
            com.mediatek.wwtv.tvcenter.nav.view.BannerView r2 = (com.mediatek.wwtv.tvcenter.nav.view.BannerView) r2
            if (r2 == 0) goto L_0x016b
            boolean r3 = r2.isShown()
            if (r3 == 0) goto L_0x016b
            r2.changeChannelFavoriteMark()
            goto L_0x016b
        L_0x007b:
            android.widget.ListView r3 = mFavoriteListView
            r4 = 8
            r3.setVisibility(r4)
            android.widget.ListView r3 = r5.mFavouriteTypeView
            r3.setVisibility(r2)
            android.view.View r2 = r5.mFavouriteListTipView
            r2.setVisibility(r4)
            android.widget.ListView r2 = r5.mFavouriteTypeView
            r2.setFocusable(r1)
            android.widget.ListView r1 = r5.mFavouriteTypeView
            r1.requestFocus()
            android.widget.ListView r1 = r5.mFavouriteTypeView
            int r2 = r5.CURRENT_FAVOURITE_TYPE
            r1.setSelection(r2)
            android.widget.TextView r1 = r5.mTitleTextView
            android.content.Context r2 = r5.mContext
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131692053(0x7f0f0a15, float:1.9013195E38)
            java.lang.String r2 = r2.getString(r3)
            r1.setText(r2)
            goto L_0x016b
        L_0x00b1:
            java.lang.String r2 = "ShowFavoriteChannelListView"
            java.lang.String r3 = "KEYCODE_MTKIR_GREEN"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            boolean r2 = r5.hasNextPage
            if (r2 == 0) goto L_0x00de
            android.widget.ListView r2 = mFavoriteListView
            int r2 = r2.getSelectedItemPosition()
            com.mediatek.wwtv.tvcenter.nav.fav.ChannelAdapter r3 = r5.mChannelAdapter
            int r3 = r3.getCount()
            int r3 = r3 - r1
            if (r2 == r3) goto L_0x00de
            android.widget.ListView r2 = mFavoriteListView
            r2.requestFocus()
            android.widget.ListView r2 = mFavoriteListView
            com.mediatek.wwtv.tvcenter.nav.fav.ChannelAdapter r3 = r5.mChannelAdapter
            int r3 = r3.getCount()
            int r3 = r3 - r1
            r2.setSelection(r3)
            goto L_0x016b
        L_0x00de:
            boolean r2 = r5.hasNextPage
            if (r2 == 0) goto L_0x016b
            com.mediatek.wwtv.tvcenter.nav.fav.ChannelAdapter r2 = r5.mChannelAdapter
            java.util.List r3 = r5.getNextPrePageChList(r1)
            r2.updateData(r3)
            android.widget.ListView r2 = mFavoriteListView
            r2.requestFocus()
            android.widget.ListView r2 = mFavoriteListView
            com.mediatek.wwtv.tvcenter.nav.fav.ChannelAdapter r3 = r5.mChannelAdapter
            int r3 = r3.getCount()
            int r3 = r3 - r1
            r2.setSelection(r3)
            goto L_0x016b
        L_0x00fe:
            java.lang.String r1 = "ShowFavoriteChannelListView"
            java.lang.String r3 = "KEYCODE_MTKIR_RED"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            boolean r1 = r5.hasNextPage
            if (r1 == 0) goto L_0x011c
            android.widget.ListView r1 = mFavoriteListView
            int r1 = r1.getSelectedItemPosition()
            if (r1 == 0) goto L_0x011c
            android.widget.ListView r1 = mFavoriteListView
            r1.requestFocus()
            android.widget.ListView r1 = mFavoriteListView
            r1.setSelection(r2)
            goto L_0x016b
        L_0x011c:
            boolean r1 = r5.hasNextPage
            if (r1 == 0) goto L_0x016b
            com.mediatek.wwtv.tvcenter.nav.fav.ChannelAdapter r1 = r5.mChannelAdapter
            java.util.List r3 = r5.getNextPrePageChList(r2)
            r1.updateData(r3)
            android.widget.ListView r1 = mFavoriteListView
            r1.requestFocus()
            android.widget.ListView r1 = mFavoriteListView
            r1.setSelection(r2)
            goto L_0x016b
        L_0x0134:
            r0 = 0
            goto L_0x016b
        L_0x0136:
            goto L_0x016b
        L_0x0137:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener r1 = com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener.getInstance()
            r1.updateStatus(r3, r6)
            r5.updateFavList()
            goto L_0x016b
        L_0x0142:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r5.commonIntegration
            boolean r1 = r1.is3rdTVSource()
            if (r1 == 0) goto L_0x014b
            return r2
        L_0x014b:
            com.mediatek.wwtv.tvcenter.nav.fav.FavChannelManager r1 = r5.favChannelManager
            com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListListener r2 = r5.favoriteListListener
            r1.favAddOrErase(r2)
            com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener r1 = com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener.getInstance()
            r1.updateStatus(r3, r6)
            goto L_0x016b
        L_0x015a:
            r0 = 0
            goto L_0x016b
        L_0x015c:
            r5.enterKeySelectChannel()
            com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener r1 = com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener.getInstance()
            r1.updateStatus(r3, r6)
            goto L_0x016b
        L_0x0167:
            r5.dismiss()
        L_0x016b:
            if (r0 != 0) goto L_0x0183
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r1 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            if (r1 == 0) goto L_0x0183
            java.lang.String r1 = "ShowFavoriteChannelListView"
            java.lang.String r2 = "Back Key To TurnkeyUiMainActivity"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r1 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            boolean r1 = r1.KeyHandler(r6, r7)
            return r1
        L_0x0183:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog.KeyHandler(int, android.view.KeyEvent, boolean):boolean");
    }

    public void enterKeySelectChannel() {
        MtkTvChannelInfoBase selectedChannel = (MtkTvChannelInfoBase) mFavoriteListView.getSelectedItem();
        MtkTvChannelInfoBase currentChannel = this.commonIntegration.getCurChInfo();
        if (selectedChannel != null && currentChannel != null && !selectedChannel.equals(currentChannel) && this.commonIntegration.selectChannelByInfo(selectedChannel)) {
            this.mLastSelection = mFavoriteListView.getSelectedItemPosition();
            if (this.mChannelAdapter != null) {
                saveLastPosition(selectedChannel.getChannelId(), this.mChannelAdapter.getChannellist());
            }
        }
    }

    private String[] addSuffix(String[] strtemp, int strId) {
        if (this.mContext == null || strtemp.length == 0) {
            return null;
        }
        String[] str = new String[strtemp.length];
        for (int i = 0; i < strtemp.length; i++) {
            String numStr = strtemp[i];
            int num = (numStr == null || numStr.isEmpty()) ? Integer.MAX_VALUE : Integer.parseInt(numStr);
            if (num != Integer.MAX_VALUE) {
                str[i] = this.mContext.getResources().getString(strId, new Object[]{Integer.valueOf(num)});
            }
        }
        return str;
    }

    private void loadChannelTypeRes() {
        this.types = addSuffix(this.mContext.getResources().getStringArray(R.array.nav_favourite_type), R.string.str);
        this.mTitlePre = this.mContext.getResources().getString(R.string.nav_exit) + " - ";
        this.mTitleTextView.setText(this.mTitlePre + this.types[this.CURRENT_FAVOURITE_TYPE]);
        ArrayList<HashMap<String, String>> typeList = new ArrayList<>();
        for (String type : this.types) {
            HashMap<String, String> tmpType = new HashMap<>();
            tmpType.put(FAVOURITE_TYPE, type);
            typeList.add(tmpType);
        }
        this.mFavouriteTypeView.setAdapter(new SimpleAdapter(this.mContext, typeList, R.layout.nav_channel_type_item, new String[]{FAVOURITE_TYPE}, new int[]{R.id.nav_channel_type_list_item}));
        this.mFavouriteTypeView.setOnKeyListener(new ChannelListOnKey());
    }

    /* access modifiers changed from: private */
    public void updateFavList() {
        MtkLog.d(TAG, "updateFavList");
        new Thread(new Runnable() {
            public void run() {
                int chId = CommonIntegration.getInstance().getCurrentChannelId();
                List<MtkTvChannelInfoBase> tempList = FavoriteListDialog.this.processListWithThread(chId);
                Message msg = Message.obtain();
                msg.what = 4102;
                msg.arg1 = chId;
                msg.obj = tempList;
                FavoriteListDialog.this.handler.sendMessage(msg);
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public List<MtkTvChannelInfoBase> processListWithThread(int chId) {
        MtkTvChannelInfoBase currentChannel = this.commonIntegration.getCurChInfo();
        if (currentChannel == null) {
            return null;
        }
        boolean z = true;
        int i = 7;
        if ((currentChannel.getNwMask() & this.favMask[this.CURRENT_FAVOURITE_TYPE]) > 0) {
            int preNum = this.commonIntegration.getFavouriteChannelCount(this.favMask[this.CURRENT_FAVOURITE_TYPE]);
            if (preNum <= 7) {
                z = false;
            }
            this.hasNextPage = z;
            if (this.hasNextPage) {
                int index = currentChannelInListIndex(chId);
                if (index >= 0) {
                    return this.commonIntegration.getFavoriteListByFilter(this.favMask[this.CURRENT_FAVOURITE_TYPE], chId, index, 7 - index);
                }
                return this.commonIntegration.getFavoriteListByFilter(this.favMask[this.CURRENT_FAVOURITE_TYPE], chId, this.mLastSelection, 7 - this.mLastSelection);
            } else if (preNum > 0) {
                return this.commonIntegration.getFavoriteListByFilter(this.favMask[this.CURRENT_FAVOURITE_TYPE], 0, 0, preNum);
            } else {
                return null;
            }
        } else if ((currentChannel.getNwMask() & this.favMask[this.CURRENT_FAVOURITE_TYPE]) != 0) {
            return null;
        } else {
            int preNum2 = this.commonIntegration.getFavouriteChannelCount(this.favMask[this.CURRENT_FAVOURITE_TYPE]);
            if (preNum2 <= 7) {
                z = false;
            }
            this.hasNextPage = z;
            if (preNum2 <= 0) {
                return null;
            }
            CommonIntegration commonIntegration2 = this.commonIntegration;
            int i2 = this.favMask[this.CURRENT_FAVOURITE_TYPE];
            if (preNum2 <= 7) {
                i = preNum2;
            }
            return commonIntegration2.getFavoriteListByFilter(i2, 0, 0, i);
        }
    }

    private int currentChannelInListIndex(int currentChannelId) {
        int size = this.mChannelIdList.size();
        for (int i = 0; i < size; i++) {
            if (currentChannelId == this.mChannelIdList.get(i).intValue()) {
                MtkLog.d(TAG, "i>>>>" + i);
                return i;
            }
        }
        MtkLog.d(TAG, "i>>>>-1");
        return -1;
    }

    /* access modifiers changed from: private */
    public void saveLastPosition(int currentChannelId, List<MtkTvChannelInfoBase> tempChlist) {
        if (tempChlist != null) {
            this.mChannelIdList.clear();
            int size = tempChlist.size();
            for (int i = 0; i < size; i++) {
                int channelId = tempChlist.get(i).getChannelId();
                this.mChannelIdList.add(Integer.valueOf(channelId));
                MtkLog.d(TAG, "currentChannelId>>>" + currentChannelId + " chid=" + channelId);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        setContentView(R.layout.nav_favoritelist);
        setWindowPosition();
        findViews();
        register();
        updateFavList();
        loadChannelTypeRes();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        startTimeout(10000);
        super.onStart();
    }

    private void findViews() {
        mFavoriteListView = (ListView) findViewById(R.id.nav_favorite_listview);
        this.mFavouriteTypeView = (ListView) findViewById(R.id.nav_favourite_typeview);
        this.mFavouriteListPageUpDownView = findViewById(R.id.nav_fav_page_up_down);
        this.mFavouriteListTipView = findViewById(R.id.nav_favourite_list_tip);
        this.mTitleTextView = (TextView) findViewById(R.id.nav_favorite_list_title);
        this.mNavPageUpTextView = (TextView) findViewById(R.id.fav_nav_page_up);
        this.mNavFavoriteSelectTextView = (TextView) findViewById(R.id.fav_nav_favorite_select);
        this.mNavExitTextView = (TextView) findViewById(R.id.fav_nav_favorite_exit);
    }

    private void register() {
        mFavoriteListView.setOnKeyListener(new ChannelListOnKey());
    }

    /* access modifiers changed from: private */
    public void showPageUpDownView() {
        if (this.hasNextPage) {
            if (this.mFavouriteListPageUpDownView.getVisibility() != 0) {
                this.mFavouriteListPageUpDownView.setVisibility(0);
            }
        } else if (this.mFavouriteListPageUpDownView.getVisibility() != 4) {
            this.mFavouriteListPageUpDownView.setVisibility(4);
        }
    }

    public void setWindowPosition() {
        WindowManager m = getWindow().getWindowManager();
        Display display = m.getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        TypedValue sca = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_marginY, sca, true);
        float chmarginY = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_marginX, sca, true);
        float chmarginX = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_size_width, sca, true);
        float chwidth = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_channellist_size_height, sca, true);
        float chheight = sca.getFloat();
        int height = (int) (((float) display.getHeight()) * chmarginY);
        int width = (int) (((float) display.getWidth()) * chmarginX);
        int menuWidth = (int) (((float) display.getWidth()) * chwidth);
        int height2 = (int) (((float) display.getHeight()) * chheight);
        lp.width = menuWidth;
        lp.height = display.getHeight();
        int x = display.getWidth();
        WindowManager windowManager = m;
        StringBuilder sb = new StringBuilder();
        TypedValue typedValue = sca;
        sb.append("setWindowPosition menuWidth ");
        sb.append(menuWidth);
        sb.append(" x ");
        sb.append(x);
        sb.append(" display.getWidth() ");
        sb.append(display.getWidth());
        MtkLog.d(TAG, sb.toString());
        lp.x = x;
        lp.y = 0;
        window.setAttributes(lp);
    }

    public int dip2px(Context context, int dp) {
        return (int) ((((float) dp) / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void onDismiss(DialogInterface dialog) {
        this.mContext = null;
    }

    public void dismiss() {
        this.mFavouriteTypeView.setVisibility(8);
        mFavoriteListView.setVisibility(0);
        super.dismiss();
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    class ChannelListOnKey implements View.OnKeyListener {
        ChannelListOnKey() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int id = v.getId();
            if (id != R.id.nav_favorite_listview) {
                if (id == R.id.nav_favourite_typeview && event.getAction() == 0) {
                    FavoriteListDialog.this.startTimeout(10000);
                    if (keyCode != 23) {
                        switch (keyCode) {
                            case 19:
                                if (FavoriteListDialog.this.mFavouriteTypeView.getSelectedItemPosition() == 0) {
                                    FavoriteListDialog.this.mFavouriteTypeView.setSelection(3);
                                    return true;
                                }
                                break;
                            case 20:
                                MtkLog.d(FavoriteListDialog.TAG, "KEYCODE_DPAD_DOWN position = " + FavoriteListDialog.this.mFavouriteTypeView.getSelectedItemPosition());
                                if (FavoriteListDialog.this.mFavouriteTypeView.getSelectedItemPosition() == 3) {
                                    FavoriteListDialog.this.mFavouriteTypeView.setSelection(0);
                                    return true;
                                }
                                break;
                            default:
                                switch (keyCode) {
                                    case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                    case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                    case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                    case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                        return true;
                                }
                        }
                    } else {
                        FavoriteListDialog.this.resetFavouriteListViw();
                        return true;
                    }
                }
                return false;
            }
            int slectPosition = FavoriteListDialog.mFavoriteListView.getSelectedItemPosition();
            if (event.getAction() != 0) {
                return false;
            }
            if (keyCode == 23) {
                FavoriteListDialog.this.startTimeout(10000);
                MtkLog.v(FavoriteListDialog.TAG, "mChannelItemKeyLsner*********** selectPosition" + FavoriteListDialog.mFavoriteListView.getSelectedItemPosition());
                MtkTvChannelInfoBase selectedChannel = (MtkTvChannelInfoBase) FavoriteListDialog.mFavoriteListView.getSelectedItem();
                if (!selectedChannel.equals(FavoriteListDialog.this.commonIntegration.getCurChInfo())) {
                    if (StateDvr.getInstance() == null || !StateDvr.getInstance().isRunning()) {
                        MtkLog.d(FavoriteListDialog.TAG, "no pvr and time shift");
                        FavoriteListDialog.this.enterKeySelectChannel();
                    } else {
                        MtkLog.d(FavoriteListDialog.TAG, "DVR is running !");
                        DvrDialog conDialog = new DvrDialog((Activity) StateDvr.getInstance().getmContext(), 40961, keyCode, 1);
                        MtkLog.e(FavoriteListDialog.TAG, "channelID:-1,ID:" + selectedChannel.getChannelId());
                        MtkLog.e(FavoriteListDialog.TAG, "channelID:-1,Name:" + selectedChannel.getServiceName());
                        conDialog.setMtkTvChannelInfoBase(selectedChannel.getChannelId());
                        conDialog.show();
                        FavoriteListDialog.this.dismiss();
                    }
                }
                return true;
            } else if (keyCode != 129) {
                switch (keyCode) {
                    case 19:
                        MtkLog.d(FavoriteListDialog.TAG, "KEYCODE_DPAD_UP!!!!!");
                        FavoriteListDialog.mFavoriteListView.getChildAt(slectPosition).requestFocusFromTouch();
                        if (!FavoriteListDialog.this.hasNextPage || slectPosition != 0) {
                            FavoriteListDialog.this.startTimeout(10000);
                            if (FavoriteListDialog.mFavoriteListView.getSelectedItemPosition() != 0) {
                                return false;
                            }
                            FavoriteListDialog.mFavoriteListView.setSelection(FavoriteListDialog.this.mChannelAdapter.getCount() - 1);
                            return true;
                        }
                        FavoriteListDialog.this.mChannelAdapter.updateData(FavoriteListDialog.this.getNextPrePageChList(false));
                        FavoriteListDialog.mFavoriteListView.setSelection(FavoriteListDialog.this.mChannelAdapter.getCount() - 1);
                        FavoriteListDialog.this.startTimeout(10000);
                        return true;
                    case 20:
                        MtkLog.d(FavoriteListDialog.TAG, "KEYCODE_DPAD_DOWN!!!!!");
                        FavoriteListDialog.mFavoriteListView.getChildAt(slectPosition).requestFocusFromTouch();
                        if (!FavoriteListDialog.this.hasNextPage || slectPosition != FavoriteListDialog.this.mChannelAdapter.getCount() - 1) {
                            FavoriteListDialog.this.startTimeout(10000);
                            if (FavoriteListDialog.mFavoriteListView.getSelectedItemPosition() != FavoriteListDialog.this.mChannelAdapter.getCount() - 1) {
                                return false;
                            }
                            FavoriteListDialog.mFavoriteListView.setSelection(0);
                            return true;
                        }
                        FavoriteListDialog.this.mChannelAdapter.updateData(FavoriteListDialog.this.getNextPrePageChList(true));
                        FavoriteListDialog.mFavoriteListView.setSelection(0);
                        FavoriteListDialog.this.startTimeout(10000);
                        return true;
                    default:
                        return FavoriteListDialog.this.KeyHandler(keyCode, event);
                }
            } else {
                MtkLog.d(FavoriteListDialog.TAG, "KEYCODE_MEDIA_EJECT!!!!!");
                FavoriteListDialog.this.startTimeout(10000);
                return false;
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetFavouriteListViw() {
        this.CURRENT_FAVOURITE_TYPE = this.mFavouriteTypeView.getSelectedItemPosition();
        this.favChannelManager.setFavoriteType(this.CURRENT_FAVOURITE_TYPE);
        this.mSaveValue.saveValue(FAVOURITE_TYPE, this.CURRENT_FAVOURITE_TYPE);
        mFavoriteListView.setAdapter((ListAdapter) null);
        mFavoriteListView.setVisibility(0);
        this.mFavouriteTypeView.setVisibility(8);
        resetFavouriteList();
    }

    private void selectTifChannel(int keyCode, TIFChannelInfo selectedChannel) {
        if (keyCode == 23 && TIFChannelManager.getInstance(this.mContext).selectChannelByTIFInfo(selectedChannel)) {
            this.mLastSelection = mFavoriteListView.getSelectedItemPosition();
            if (this.mChannelAdapter != null) {
                saveLastPosition(selectedChannel.mMtkTvChannelInfo.getChannelId(), this.mChannelAdapter.getChannellist());
            }
        }
    }

    /* access modifiers changed from: private */
    public List<MtkTvChannelInfoBase> getNextPrePageChList(boolean next) {
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        if (this.mChannelAdapter == null || this.mChannelAdapter.getCount() <= 0) {
            return list;
        }
        if (next) {
            return this.commonIntegration.getFavoriteListByFilter(this.favMask[this.CURRENT_FAVOURITE_TYPE], this.mChannelAdapter.getItem(this.mChannelAdapter.getCount() - 1).getChannelId() + 1, 0, 7);
        }
        return this.commonIntegration.getFavoriteListByFilter(this.favMask[this.CURRENT_FAVOURITE_TYPE], this.mChannelAdapter.getItem(0).getChannelId(), 7, 0);
    }

    private synchronized void resetFavouriteList() {
        ((Activity) this.mContext).runOnUiThread(new Runnable() {
            public void run() {
                TextView access$1700 = FavoriteListDialog.this.mTitleTextView;
                access$1700.setText(FavoriteListDialog.this.mTitlePre + FavoriteListDialog.this.types[FavoriteListDialog.this.CURRENT_FAVOURITE_TYPE]);
            }
        });
        updateFavList();
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 10) {
            if (isVisible()) {
                dismiss();
            }
            MtkLog.d(TAG, "come in ComponentStatusListener.NAV_CHANNEL_CHANGED");
            this.canContinueChangeChannel = true;
        } else if (statusID == 2 && value == 16777233 && isVisible()) {
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public int findSelectItem(String string) {
        if (this.mChannelAdapter == null || this.mChannelAdapter.getChannellist() == null) {
            return -1;
        }
        List<MtkTvChannelInfoBase> channellist = this.mChannelAdapter.getChannellist();
        for (int i = 0; i < channellist.size(); i++) {
            MtkTvChannelInfoBase mCurrentChannel = channellist.get(i);
            if (mCurrentChannel instanceof MtkTvATSCChannelInfo) {
                MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel;
                if (string.equals(tmpAtsc.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpAtsc.getMinorNum())) {
                    return i;
                }
            } else if (mCurrentChannel instanceof MtkTvISDBChannelInfo) {
                MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel;
                if (string.equals(tmpIsdb.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpIsdb.getMinorNum())) {
                    return i;
                }
            } else {
                if (string.equals("" + mCurrentChannel.getChannelNumber())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public int findSelectItemforType(String string) {
        if (this.types == null) {
            return -1;
        }
        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i].equals(string)) {
                return i;
            }
        }
        return -1;
    }
}
