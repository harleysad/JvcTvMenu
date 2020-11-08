package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.DVRArrayAdapter;
import com.mediatek.wwtv.tvcenter.dvr.controller.DVRFiles;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StatusType;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DvrFilelist extends Dialog {
    /* access modifiers changed from: private */
    public static String TAG = "DvrFilelist";
    private static final float hScale = 0.7f;
    private static final float wScale = 0.8f;
    private final String CLSName = (getClass().getSimpleName() + ".");
    private final int TOTAL_ITEM_EVERY_PAGE = 5;
    TextView delet;
    private final FileReceiver fileReceiver;
    private final Handler handler;
    TextView info;
    private AdapterView.OnItemClickListener listener;
    private final View.AccessibilityDelegate mAccDelegate = new View.AccessibilityDelegate() {
        public void sendAccessibilityEvent(View host, int eventType) {
            try {
                Class.forName("android.view.View").getDeclaredMethod("sendAccessibilityEventInternal", new Class[]{Integer.TYPE}).invoke(host, new Object[]{Integer.valueOf(eventType)});
            } catch (Exception e) {
                String access$000 = DvrFilelist.TAG;
                Log.d(access$000, "Exception " + e);
            }
            if (DvrFilelist.this.mPVRFileListLV != host) {
                DvrFilelist.this.resetTimeout();
            }
            String access$0002 = DvrFilelist.TAG;
            MtkLog.d(access$0002, "sendAccessibilityEvent." + eventType + "," + host);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            String access$000 = DvrFilelist.TAG;
            MtkLog.d(access$000, "onRequestSendAccessibilityEvent." + host + "," + child + "," + event);
            if (DvrFilelist.this.mPVRFileListLV != host) {
                String access$0002 = DvrFilelist.TAG;
                MtkLog.d(access$0002, ":" + DvrFilelist.this.mPVRFileListLV + "," + host);
            } else {
                List<CharSequence> texts = event.getText();
                if (texts == null) {
                    String access$0003 = DvrFilelist.TAG;
                    MtkLog.d(access$0003, ":" + texts);
                } else if (event.getEventType() == 32768) {
                    int index = findSelectItem(texts.get(0).toString());
                    if (index >= 0) {
                        DvrFilelist.this.setselectItem(index);
                        DvrFilelist.this.resetTimeout();
                    }
                    String access$0004 = DvrFilelist.TAG;
                    MtkLog.d(access$0004, "focus=" + texts.get(0).toString());
                } else if (event.getEventType() == 1) {
                    String access$0005 = DvrFilelist.TAG;
                    MtkLog.d(access$0005, "click=" + texts.get(0).toString());
                    if (texts.get(0).toString().equals("Page Up")) {
                        DvrFilelist.this.pageUP();
                    } else if (texts.get(0).toString().equals("Page Down")) {
                        DvrFilelist.this.pageDown();
                    }
                }
            }
            try {
                return ((Boolean) Class.forName("android.view.ViewGroup").getDeclaredMethod("onRequestSendAccessibilityEventInternal", new Class[]{View.class, AccessibilityEvent.class}).invoke(host, new Object[]{child, event})).booleanValue();
            } catch (Exception e) {
                String access$0006 = DvrFilelist.TAG;
                Log.d(access$0006, "Exception " + e);
                return true;
            }
        }

        private int findSelectItem(String text) {
            if (DvrFilelist.this.mCurrentPageList == null) {
                return -1;
            }
            for (int i = 0; i < DvrFilelist.this.mCurrentPageList.size(); i++) {
                if (((DVRFiles) DvrFilelist.this.mCurrentPageList.get(i)).getProgramName().equals(text.substring(5))) {
                    return i;
                }
            }
            return -1;
        }
    };
    private final Activity mContext;
    private int mCurrentPage = 0;
    /* access modifiers changed from: private */
    public List<DVRFiles> mCurrentPageList = new ArrayList();
    private final int mDefaultDuration = 10;
    private List<DVRFiles> mFileList = new ArrayList();
    private RelativeLayout mInfoWindow;
    private TableLayout mInfoWindowDetail;
    private int mMaxPage = 0;
    private final int mMsgWhat = 0;
    private TextView[] mPVRFileInfo = new TextView[7];
    /* access modifiers changed from: private */
    public ListView mPVRFileListLV;
    private final StateBase mState;
    private DVRArrayAdapter pVRArrayAdapter;
    TextView page_down;
    TextView page_up;
    private boolean showInfoWindow = false;
    private int subStartindex = 1;
    TextView titlelisTextView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        setContentView(R.layout.pvr_timeshfit_playlist);
        initViews();
    }

    public DvrFilelist(Activity context, StateBase state, Handler handler2) {
        super(context, R.style.nav_dialog);
        this.mContext = context;
        this.handler = handler2;
        this.fileReceiver = new FileReceiver();
        this.mContext.registerReceiver(this.fileReceiver, new IntentFilter("com.mediatek.pvr.file"));
        this.mState = state;
    }

    public List<DVRFiles> getmFileList() {
        return this.mFileList;
    }

    public void setmFileList(List<DVRFiles> mFileList2) {
        this.mFileList = mFileList2;
    }

    private void initViews() {
        this.titlelisTextView = (TextView) findViewById(R.id.playlist_title);
        this.page_up = (TextView) findViewById(R.id.playlist_page_up);
        this.page_down = (TextView) findViewById(R.id.playlist_page_down);
        this.delet = (TextView) findViewById(R.id.playlist_delet);
        this.info = (TextView) findViewById(R.id.playlist_info);
        this.mPVRFileInfo = new TextView[]{(TextView) findViewById(R.id.pvr_channel_num), (TextView) findViewById(R.id.pvr_channel_str), (TextView) findViewById(R.id.pvr_programename), (TextView) findViewById(R.id.pvr_channel_date), (TextView) findViewById(R.id.pvr_week), (TextView) findViewById(R.id.pvr_time), (TextView) findViewById(R.id.pvr_duration), (TextView) findViewById(R.id.pvr_programe_info)};
        this.mInfoWindow = (RelativeLayout) findViewById(R.id.pvr_file_info);
        this.mInfoWindowDetail = (TableLayout) findViewById(R.id.playlist_filemeta);
        this.mPVRFileListLV = (ListView) findViewById(R.id.playlist_list);
        this.mPVRFileListLV.setFocusableInTouchMode(true);
        this.mPVRFileListLV.setAccessibilityDelegate(this.mAccDelegate);
    }

    public void setWindowPosition() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int menuWidth = (int) (((double) display.getWidth()) * 0.7d);
        int menuHeight = (int) (((double) display.getHeight()) * 0.7d);
        lp.width = menuWidth;
        lp.height = menuHeight;
        int width = (display.getWidth() / 2) - (menuWidth / 2);
        int height = display.getHeight() / 2;
        lp.x = width - ((int) (((double) display.getWidth()) * 0.15d));
        lp.y = (height - ((int) (((double) display.getHeight()) * 0.0139d))) - (menuHeight / 2);
        window.setAttributes(lp);
    }

    public void show() {
        super.show();
        this.titlelisTextView.setImportantForAccessibility(2);
        this.page_up.setAccessibilityDelegate(this.mAccDelegate);
        this.page_down.setAccessibilityDelegate(this.mAccDelegate);
        this.delet.setAccessibilityDelegate(this.mAccDelegate);
        this.info.setAccessibilityDelegate(this.mAccDelegate);
        setWindowPosition();
        this.pVRArrayAdapter = new DVRArrayAdapter(this.mContext, R.layout.recode_list_item, this.mCurrentPageList, this.subStartindex, 0);
        this.mPVRFileListLV.setAdapter(this.pVRArrayAdapter);
        this.mPVRFileListLV.setDivider((Drawable) null);
        this.mPVRFileListLV.setSelected(true);
        this.mPVRFileListLV.setChoiceMode(1);
        this.mPVRFileListLV.setSelector(R.color.nav_button_select);
        this.mPVRFileListLV.requestFocus();
        setSelection(0);
        this.mPVRFileListLV.setOnItemClickListener(getListener());
        if (this.mPVRFileListLV.getCount() > 0) {
            try {
                String simpleName = this.mPVRFileListLV.getFocusedChild().getClass().getSimpleName();
            } catch (Exception e) {
            }
        }
        resetTimeout();
    }

    /* access modifiers changed from: private */
    public void resetTimeout() {
        this.handler.removeMessages(21);
        this.handler.sendEmptyMessageDelayed(21, MessageType.delayMillis10);
    }

    public void setLVonItemClickListener(AdapterView.OnItemClickListener listener2) {
        this.mPVRFileListLV.setOnItemClickListener(listener2);
    }

    public void dimissInfobar() {
        if (this.handler.hasMessages(21)) {
            this.handler.removeMessages(21);
        }
        if (MtkTvPWDDialog.getInstance().PWDShow() == 0) {
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_PWD_DLG);
        }
        try {
            this.mContext.unregisterReceiver(this.fileReceiver);
        } catch (Exception e) {
            MtkLog.e("PVRFILE", "unregister error");
        }
        super.dismiss();
    }

    public void initList() {
        if (this.mFileList != null) {
            this.mCurrentPageList = this.mFileList.subList(0, Math.min(this.mFileList.size(), 5));
            refreshMaxPage();
            if (this.mPVRFileListLV != null) {
                refreshCurrentPage(0);
            }
        }
    }

    private void refreshMaxPage() {
        this.mMaxPage = this.mFileList.size() / 5;
        if (this.mFileList.size() % 5 != 0) {
            this.mMaxPage++;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int selection = this.mPVRFileListLV.getSelectedItemPosition();
        MtkLog.d(TAG, "selecton  = " + selection);
        if (selection == -1) {
            if (this.mPVRFileListLV.getCount() > 0) {
                this.mPVRFileListLV.setSelection(0);
            } else {
                if (this.showInfoWindow) {
                    this.mInfoWindow.setVisibility(4);
                }
                if (event.getAction() == 0) {
                    if (event.getKeyCode() == 4) {
                        DvrManager.getInstance().restoreToDefault(StatusType.FILELIST);
                        return true;
                    }
                    if (event.getKeyCode() == 82) {
                        DvrManager.getInstance().restoreToDefault(StatusType.FILELIST);
                    }
                    if (TurnkeyUiMainActivity.getInstance() != null) {
                        MtkLog.d(TAG, "TurnkeyUiMainActivity");
                        return TurnkeyUiMainActivity.getInstance().KeyHandler(event.getKeyCode(), event, false);
                    }
                }
                return true;
            }
        }
        if (event.getAction() == 0) {
            int keyCode = event.getKeyCode();
            if (keyCode == 4) {
                DvrManager.getInstance().restoreToDefault(StatusType.FILELIST);
                return true;
            } else if (keyCode != 23) {
                if (keyCode != 82) {
                    if (keyCode != 165) {
                        switch (keyCode) {
                            case 19:
                                resetTimeout();
                                int selection2 = selection - 1;
                                if (selection2 < 0) {
                                    pageUP();
                                    selection2 = 0;
                                } else {
                                    setselectItem(selection2);
                                }
                                MtkLog.d(TAG, "selecton up = " + selection2);
                                return true;
                            case 20:
                                resetTimeout();
                                int selection3 = selection + 1;
                                if (selection3 >= this.mPVRFileListLV.getCount()) {
                                    pageDown();
                                    selection3 = 0;
                                } else {
                                    setselectItem(selection3);
                                }
                                MtkLog.d(TAG, "selecton down = " + selection3);
                                return true;
                            default:
                                switch (keyCode) {
                                    case KeyMap.KEYCODE_MTKIR_RED /*183*/:
                                        resetTimeout();
                                        pageUP();
                                        return true;
                                    case KeyMap.KEYCODE_MTKIR_GREEN /*184*/:
                                        resetTimeout();
                                        pageDown();
                                        return true;
                                    case KeyMap.KEYCODE_MTKIR_YELLOW /*185*/:
                                        resetTimeout();
                                        ((StateDvrFileList) this.mState).deletePvrFile();
                                        return true;
                                    case KeyMap.KEYCODE_MTKIR_BLUE /*186*/:
                                        break;
                                }
                        }
                    }
                    resetTimeout();
                    info();
                    return true;
                }
                DvrManager.getInstance().restoreToDefault(StatusType.FILELIST);
                if (TurnkeyUiMainActivity.getInstance() != null) {
                    MtkLog.d(TAG, "TurnkeyUiMainActivity");
                    return TurnkeyUiMainActivity.getInstance().KeyHandler(event.getKeyCode(), event, false);
                }
            } else {
                this.mPVRFileListLV.getOnItemClickListener().onItemClick(this.mPVRFileListLV, this.mPVRFileListLV.getSelectedView(), selection, this.mPVRFileListLV.getSelectedItemId());
                return true;
            }
        }
        super.dispatchKeyEvent(event);
        return true;
    }

    /* access modifiers changed from: private */
    public void setselectItem(int selection) {
        setSelection(selection);
        int i = 0;
        while (i < this.mPVRFileListLV.getCount()) {
            try {
                this.mPVRFileListLV.getChildAt(i).setBackgroundColor(0);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void info() {
        if (this.showInfoWindow) {
            this.mInfoWindow.setVisibility(4);
        } else {
            this.mInfoWindow.setVisibility(0);
        }
        this.showInfoWindow = !this.showInfoWindow;
    }

    public DVRFiles getSelectedFile() {
        return this.mCurrentPageList.get(this.mPVRFileListLV.getSelectedItemPosition());
    }

    public void deleteFile() {
        int selection = this.mPVRFileListLV.getSelectedItemPosition();
        DVRFiles fileName = this.mCurrentPageList.get(selection);
        int valueInt = -1;
        if (this.mFileList.indexOf(fileName) != -1) {
            valueInt = DvrManager.getInstance().getController().deletePvrFiles(this.mContext, fileName.getmId());
        }
        if (0 != 0 || valueInt == 0) {
            this.mCurrentPageList.remove(selection);
            if (this.mFileList.indexOf(fileName) != -1) {
                this.mCurrentPageList.remove(fileName);
            }
        } else {
            this.mCurrentPageList.remove(selection);
        }
        if (this.mCurrentPageList.size() == 0) {
            pageUP();
        } else {
            refreshCurrentPage(selection == 0 ? 0 : selection - 1);
        }
        refreshMaxPage();
    }

    private void refreshCurrentPage() {
        refreshCurrentPage(this.mPVRFileListLV.getSelectedItemPosition());
    }

    /* access modifiers changed from: private */
    public void pageDown() {
        this.mCurrentPage++;
        if (this.mCurrentPage >= this.mMaxPage) {
            this.mCurrentPage = 0;
        }
        if (this.mCurrentPage < 0) {
            this.mCurrentPage = 0;
        }
        this.subStartindex = (this.mCurrentPage * 5) + 1;
        this.mCurrentPageList = this.mFileList.subList(this.mCurrentPage * 5, Math.min((this.mCurrentPage + 1) * 5, this.mFileList.size()));
        refreshAdapter(0);
    }

    /* access modifiers changed from: private */
    public void pageUP() {
        if (this.mCurrentPage != 0) {
            int i = this.mCurrentPage - 1;
            this.mCurrentPage = i;
            this.mCurrentPage = Math.max(0, i);
            this.subStartindex = (this.mCurrentPage * 5) + 1;
            this.mCurrentPageList = this.mFileList.subList(this.mCurrentPage * 5, Math.min((this.mCurrentPage + 1) * 5, this.mFileList.size()));
            refreshAdapter(this.mCurrentPageList.size() - 1);
        } else if (this.mMaxPage == 0) {
            setSelection(this.mCurrentPageList.size() - 1);
        } else {
            this.mCurrentPage = this.mMaxPage - 1;
            if (this.mCurrentPage < 0) {
                this.mCurrentPage = 0;
            }
            this.subStartindex = (this.mCurrentPage * 5) + 1;
            this.mCurrentPageList = this.mFileList.subList(this.mCurrentPage * 5, Math.min((this.mCurrentPage + 1) * 5, this.mFileList.size()));
            refreshAdapter(this.mCurrentPageList.size() - 1);
        }
    }

    private void refreshCurrentPage(int selectItem) {
        this.mCurrentPageList = this.mFileList.subList(this.mCurrentPage * 5, Math.min((this.mCurrentPage + 1) * 5, this.mFileList.size()));
        refreshAdapter(selectItem);
    }

    private void refreshAdapter(int selectItem) {
        this.pVRArrayAdapter.setCurrenSelect(selectItem);
        this.pVRArrayAdapter.setmObjects(this.mCurrentPageList);
        this.pVRArrayAdapter.setSubStartIndex(this.subStartindex);
        this.mPVRFileListLV.setAdapter(this.pVRArrayAdapter);
        this.pVRArrayAdapter.notifyDataSetChanged();
        if (this.mFileList.size() != 0) {
            setSelection(selectItem);
        } else if (this.showInfoWindow) {
            this.mInfoWindow.setVisibility(4);
        }
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"NewApi"})
    public void setSelection(int index) {
        if (index >= this.mPVRFileListLV.getCount() || this.mCurrentPageList.get(index) == null) {
            this.mPVRFileInfo[0].setText("");
            this.mPVRFileInfo[1].setText("");
            this.mPVRFileInfo[2].setText("");
            this.mPVRFileInfo[3].setText("");
            this.mPVRFileInfo[4].setText("");
            this.mPVRFileInfo[5].setText("");
            this.mPVRFileInfo[6].setText("");
            this.mPVRFileInfo[7].setText("");
            return;
        }
        this.mPVRFileListLV.setSelection(index);
        if (MarketRegionInfo.getCurrentMarketRegion() == 2) {
            this.mPVRFileInfo[0].setText("CH" + this.mCurrentPageList.get(index).getChannelNum().replace(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING, "."));
        } else {
            this.mPVRFileInfo[0].setText("CH" + this.mCurrentPageList.get(index).getChannelNum());
        }
        String chanelname = this.mCurrentPageList.get(index).getChannelName();
        if (chanelname.length() > 10) {
            chanelname = chanelname.substring(0, 9) + "...";
        }
        this.mPVRFileInfo[1].setText(chanelname);
        this.mPVRFileInfo[2].setText(this.mCurrentPageList.get(index).getProgramName().replace("pvr", this.mContext.getResources().getString(R.string.add_schedule).toLowerCase()));
        this.mPVRFileInfo[3].setText(this.mCurrentPageList.get(index).getDate());
        this.mPVRFileInfo[4].setText(this.mCurrentPageList.get(index).getWeek());
        this.mPVRFileInfo[5].setText(this.mCurrentPageList.get(index).getTime());
        if (!DvrManager.getInstance().pvrIsRecording() || !((StateDvrFileList) this.mState).getSelectedFile().isRecording()) {
            this.mPVRFileInfo[6].setText(this.mCurrentPageList.get(index).getDurationStr());
        } else {
            this.mPVRFileInfo[6].setText("00:00:00");
        }
        this.mPVRFileInfo[7].setText(this.mCurrentPageList.get(index).getmDetailInfo());
    }

    public AdapterView.OnItemClickListener getListener() {
        return this.listener;
    }

    public void setListener(AdapterView.OnItemClickListener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: private */
    public void resetFile() {
        try {
            if (this.mFileList != null) {
                Iterator<DVRFiles> it = this.mFileList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    DVRFiles pvrfile = it.next();
                    if (pvrfile.isRecording) {
                        pvrfile.isRecording = false;
                        break;
                    }
                }
                refreshCurrentPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FileReceiver extends BroadcastReceiver {
        FileReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().endsWith("com.mediatek.pvr.file")) {
                DvrFilelist.this.resetFile();
            }
        }
    }
}
