package com.mediatek.wwtv.tvcenter.epg.sa;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.sa.db.DBMgrProgramList;
import com.mediatek.wwtv.tvcenter.epg.sa.db.EPGBookListViewDataItem;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.ArrayList;
import java.util.List;

public class EPGBookedListDilog extends Dialog {
    private static final String TAG = "BookedListDilog";
    private static int menuHeight = 610;
    private static int menuWidth = 800;
    private ListView mBookListView;
    private List<EPGBookListViewDataItem> mBookedList;
    private Context mContext;
    private DBMgrProgramList mDBMgrProgramList;
    /* access modifiers changed from: private */
    public EPGBookListAdapter mEPGBookListAdapter;

    public EPGBookedListDilog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public EPGBookedListDilog(Context context) {
        super(context, 2131755397);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_book_list_main);
        setWindowPosition();
        this.mBookListView = (ListView) findViewById(R.id.nav_epg_book_list);
        this.mBookListView.setDividerHeight(0);
        this.mDBMgrProgramList = DBMgrProgramList.getInstance(this.mContext);
        if (this.mBookedList == null) {
            this.mBookedList = new ArrayList();
        }
        this.mEPGBookListAdapter = new EPGBookListAdapter(this.mContext, this.mBookListView, this.mBookedList);
        this.mBookListView.setAdapter(this.mEPGBookListAdapter);
        this.mBookListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                EPGBookedListDilog.this.mEPGBookListAdapter.onKey(v, keyCode);
                return false;
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 || keyCode == 7) {
            if (isShowing()) {
                dismiss();
                saveChangeProgramList();
                EPGSaActivity epgActivity = (EPGSaActivity) this.mContext;
                epgActivity.changeBottomViewText(false, 0);
                epgActivity.notifyEPGLinearlayoutRefresh();
            }
        } else if (keyCode == 178) {
            return true;
        }
        if (keyCode == 24 || keyCode == 25) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    public void updateAdapter() {
        this.mDBMgrProgramList.getWriteableDB();
        this.mBookedList = this.mDBMgrProgramList.getProgramListWithDelete();
        this.mDBMgrProgramList.closeDB();
        this.mEPGBookListAdapter.setEPGBookList(this.mBookedList);
        this.mEPGBookListAdapter.notifyDataSetChanged();
        if (this.mBookedList.size() > 0) {
            this.mBookListView.setFocusable(true);
            this.mBookListView.requestFocus();
            this.mBookListView.setSelection(0);
        }
    }

    private void saveChangeProgramList() {
        this.mDBMgrProgramList.getWriteableDB();
        for (EPGBookListViewDataItem tempInfo : this.mBookedList) {
            if (!tempInfo.marked) {
                this.mDBMgrProgramList.deleteProgram(tempInfo);
                SaveValue instance = SaveValue.getInstance(this.mContext);
                instance.removekey(tempInfo.mProgramStartTime + "");
            }
        }
        this.mDBMgrProgramList.closeDB();
    }
}
