package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvTeletext;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextPageBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.TeletextImplement;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class TTXFavListDialog extends Dialog {
    static final int CHANNEL_LIST_PAGE_MAX = 7;
    private static final int DISMISS_TAG = 0;
    private static final String TAG = "TTXFavListDialog";
    private static final int TIME_OUT = 8000;
    /* access modifiers changed from: private */
    public static MtkTvTeletextPageBase[] mFavlist = new MtkTvTeletextPageBase[7];
    private int channelid;
    private int currentChannelId;
    Handler handler;
    private Context mContext;
    private View mFavLayout;
    /* access modifiers changed from: private */
    public FavListAdapter mFavListAdapter;
    /* access modifiers changed from: private */
    public ListView mFavListView;
    private final AdapterView.OnItemClickListener mItemClickListener;
    /* access modifiers changed from: private */
    public final TeletextImplement mTTXImpl;
    private final MtkTvTeletext mTvTxt;
    private int position;

    public TTXFavListDialog(Context context, int theme) {
        super(context, theme);
        this.channelid = -1;
        this.currentChannelId = -1;
        this.position = 0;
        this.mItemClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                TTXFavListDialog.this.handler.removeMessages(0);
                TTXFavListDialog.this.handler.sendEmptyMessageDelayed(0, 8000);
                int prepagenum = TTXFavListDialog.this.mTTXImpl.getCurrentTeletextPage().getPageNumber();
                MtkLog.d(TTXFavListDialog.TAG, "onItemClick arg2 = " + arg2);
                TTXFavListDialog.this.mTTXImpl.setTeletextPage(TTXFavListDialog.this.getFavPage(arg2));
                if (prepagenum == TTXFavListDialog.this.mTTXImpl.getCurrentTeletextPage().getPageNumber()) {
                    TTXFavListDialog.this.setFavPage(TTXFavListDialog.this.mTTXImpl.getCurrentTeletextPage(), arg2);
                    TTXFavListDialog.this.mFavListAdapter.updateData(TTXFavListDialog.mFavlist);
                    TTXFavListDialog.this.mFavListView.invalidateViews();
                }
            }
        };
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                TTXFavListDialog.this.dismiss();
            }
        };
        this.mContext = context;
        this.mTvTxt = MtkTvTeletext.getInstance();
        this.mTTXImpl = TeletextImplement.getInstance();
    }

    public TTXFavListDialog(Context context) {
        this(context, R.style.nav_dialog);
        MtkLog.d(TAG, "Constructor!");
    }

    public void setFavPage(MtkTvTeletextPageBase page) {
        MtkLog.d(TAG, "setFavPage  mFavlist.length =" + mFavlist.length);
        if (page == null) {
            MtkLog.d(TAG, "setFavPage page is null ");
            return;
        }
        int paraPageNum = page.getPageNumber();
        MtkLog.d(TAG, "setFavPage pageNum = " + paraPageNum);
        int space = -1;
        int curIndex = 0;
        while (curIndex < mFavlist.length) {
            MtkTvTeletextPageBase curPage = mFavlist[curIndex];
            MtkLog.d(TAG, "setFavPage loop curPage  = " + curIndex);
            if (space == -1 && curPage == null) {
                space = curIndex;
            }
            if (curPage == null || curPage.getPageNumber() != paraPageNum) {
                curIndex++;
            } else {
                MtkLog.d(TAG, "setFavPage setCurPage null  " + paraPageNum);
                this.position = 0;
                mFavlist[curIndex] = null;
                return;
            }
        }
        if (space != -1) {
            this.position = space;
            mFavlist[space] = page;
            return;
        }
        this.position = 0;
    }

    public void setFavPage(MtkTvTeletextPageBase page, int position2) {
        MtkLog.d(TAG, "setFavPage (two param) mFavlist.length =" + mFavlist.length);
        if (position2 >= 0 && position2 < 7) {
            if (mFavlist[position2] != null) {
                mFavlist[position2] = null;
            } else if (page != null) {
                int paraPageNum = page.getPageNumber();
                for (int curIndex = 0; curIndex < mFavlist.length; curIndex++) {
                    MtkTvTeletextPageBase curPage = mFavlist[curIndex];
                    MtkLog.d(TAG, "setFavPage (two param)loop curPage  = " + curIndex);
                    if (curPage != null && curPage.getPageNumber() == paraPageNum) {
                        MtkLog.d(TAG, "setFavPage (two param) setold  null  " + curIndex);
                        mFavlist[curIndex] = null;
                    }
                }
                mFavlist[position2] = page;
            }
        }
    }

    public MtkTvTeletextPageBase getFavPage(int position2) {
        MtkLog.d(TAG, "getFavPage (two param) mFavlist.length =" + mFavlist.length);
        if (position2 < 0 || position2 >= 7 || mFavlist[position2] == null) {
            return null;
        }
        return mFavlist[position2];
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        setContentView(R.layout.nav_ttx_favoritelist);
        setWindowPosition();
        init();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        MtkLog.d(TAG, "onStart");
    }

    public void setPositionByPage(MtkTvTeletextPageBase page) {
        MtkLog.d(TAG, "setPositionByPage");
        if (page == null) {
            MtkLog.d(TAG, "setFavPage page is null ");
            return;
        }
        for (int curIndex = 0; curIndex < mFavlist.length; curIndex++) {
            MtkTvTeletextPageBase curPage = mFavlist[curIndex];
            if (curPage != null && curPage.getPageNumber() == page.getPageNumber()) {
                this.position = curIndex;
            }
        }
        this.position = 0;
        MtkLog.d(TAG, "setPositionByPage position  = " + this.position);
    }

    public void show() {
        super.show();
        this.handler.sendEmptyMessageDelayed(0, 8000);
        this.currentChannelId = CommonIntegration.getInstance().getCurrentChannelId();
        if (this.currentChannelId != this.channelid) {
            mFavlist = null;
            mFavlist = new MtkTvTeletextPageBase[7];
            this.channelid = this.currentChannelId;
        }
        this.mFavListAdapter.updateData(mFavlist);
        this.mFavListView.invalidateViews();
        this.mFavListView.setSelection(this.position);
    }

    public void processExtFAVkey() {
        int index;
        int index2 = 0;
        while (true) {
            if (index < mFavlist.length) {
                if (mFavlist[index] != null && mFavlist[index].getPageNumber() == this.mTTXImpl.getCurrentTeletextPage().getPageNumber()) {
                    int endIndex = index;
                    index = (index + 1) % mFavlist.length;
                    while (true) {
                        if (index == endIndex) {
                            break;
                        }
                        index %= mFavlist.length;
                        if (mFavlist[index] != null) {
                            this.mTTXImpl.setTeletextPage(mFavlist[index]);
                            break;
                        }
                        index = (index + 1) % mFavlist.length;
                    }
                } else {
                    index2 = index + 1;
                }
            } else {
                break;
            }
        }
        if (index == mFavlist.length) {
            for (int index3 = 0; index3 < mFavlist.length; index3++) {
                if (mFavlist[index3] != null) {
                    this.mTTXImpl.setTeletextPage(mFavlist[index3]);
                    return;
                }
            }
        }
    }

    public void clearFavlist() {
        MtkLog.e("chengcl", "clean-------");
        if (mFavlist != null) {
            for (int i = 0; i < mFavlist.length; i++) {
                mFavlist[i] = null;
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown keyCode = " + keyCode);
        this.handler.removeMessages(0);
        this.handler.sendEmptyMessageDelayed(0, 8000);
        switch (keyCode) {
            case 19:
                if (this.mFavListView != null && this.mFavListView.getSelectedItemPosition() == 0) {
                    this.mFavListView.setSelection(mFavlist.length - 1);
                    break;
                }
            case 20:
                if (this.mFavListView != null && this.mFavListView.getSelectedItemPosition() == mFavlist.length - 1) {
                    this.mFavListView.setSelection(0);
                    break;
                }
            case 85:
                dismiss();
                break;
            case 86:
                this.mTTXImpl.setTeletextPage(getFavPage((this.mFavListView.getSelectedItemPosition() + 1) % 7));
                this.mFavListView.setSelection((this.mFavListView.getSelectedItemPosition() + 1) % 7);
                break;
            case 89:
                dismiss();
                TurnkeyUiMainActivity.getInstance().onKeyDown(keyCode, event);
                break;
            case 93:
                setFavPage(this.mTTXImpl.getCurrentTeletextPage(), this.mFavListView.getSelectedItemPosition());
                this.mFavListAdapter.updateData(mFavlist);
                this.mFavListView.invalidateViews();
                break;
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
            case KeyMap.KEYCODE_MTKIR_PRECH /*229*/:
                TurnkeyUiMainActivity.getInstance().onKeyDown(keyCode, event);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        this.mFavLayout = (LinearLayout) findViewById(R.id.nav_ttx_favoriteList_layout);
        this.mFavListView = (ListView) findViewById(R.id.nav_favorite_listview);
        this.mFavListAdapter = new FavListAdapter(this.mContext, mFavlist);
        this.mFavListView.setAdapter(this.mFavListAdapter);
        this.mFavListView.setFocusable(true);
        this.mFavListView.requestFocus();
        this.mFavListView.setSelection(this.position);
        this.mFavListView.setOnItemClickListener(this.mItemClickListener);
    }

    public void setWindowPosition() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int menuWidth = (int) (((double) display.getWidth()) * 0.32d);
        int menuHeight = (int) (((double) display.getHeight()) * 0.56d);
        lp.width = menuWidth;
        lp.height = menuHeight;
        int width = (display.getWidth() / 2) - (menuWidth / 2);
        int height = display.getHeight() / 2;
        lp.x = width - ((int) (((double) display.getWidth()) * 0.48d));
        lp.y = (height - ((int) (((double) display.getHeight()) * 0.0139d))) - (menuHeight / 2);
        window.setAttributes(lp);
    }

    public void onDismiss(DialogInterface dialog) {
        MtkLog.d(TAG, "onDismiss!!!!!!!!!");
        this.mContext = null;
    }

    public void dismiss() {
        MtkLog.d(TAG, "dismiss!!!!!!!");
        this.handler.removeMessages(0);
        super.dismiss();
    }

    class FavListAdapter extends BaseAdapter {
        private final String TAG = "FavListAdapter.FavListAdapter";
        private final Context mContext;
        private final LayoutInflater mInflater;
        private MtkTvTeletextPageBase[] mcurrentFavList;

        public FavListAdapter(Context context, MtkTvTeletextPageBase[] mcurrentChannelList) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
            this.mcurrentFavList = mcurrentChannelList;
        }

        public int getCount() {
            if (this.mcurrentFavList != null) {
                return this.mcurrentFavList.length;
            }
            return 0;
        }

        public MtkTvTeletextPageBase getItem(int position) {
            return this.mcurrentFavList[position];
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public void updateData(MtkTvTeletextPageBase[] mcurrentChannelList) {
            this.mcurrentFavList = mcurrentChannelList;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder hodler;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nav_ttx_fav_item, (ViewGroup) null);
                hodler = new ViewHolder();
                hodler.mTextView = (TextView) convertView.findViewById(R.id.nav_ttx_fav_item_txt);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHolder) convertView.getTag();
            }
            MtkTvTeletextPageBase mCurrentPage = this.mcurrentFavList[position];
            if (mCurrentPage != null) {
                hodler.mTextView.setText(Integer.toHexString(mCurrentPage.getPageNumber()));
            } else {
                hodler.mTextView.setText("");
            }
            return convertView;
        }

        private class ViewHolder {
            TextView mTextView;

            private ViewHolder() {
            }
        }
    }
}
