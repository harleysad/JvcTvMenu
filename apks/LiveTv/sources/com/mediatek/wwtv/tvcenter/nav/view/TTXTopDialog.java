package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.model.MtkTvRectangle;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopPageBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.util.TeletextImplement;
import com.mediatek.wwtv.tvcenter.nav.util.TeletextTopItem;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TTXTopDialog extends Dialog {
    private static final int SET_SCREEN_RECTANGLE = 100;
    private static final String SOURCE_MAIN = "main";
    private static final String TAG = "TTXTopDialog";
    private List<TeletextTopItem> mBlockList;
    private ListView mBlockView;
    private Context mContext;
    private List<TeletextTopItem> mGroupList;
    private ListView mGroupView;
    private Handler mHandler;
    private List<TeletextTopItem> mPageList;
    /* access modifiers changed from: private */
    public ListView mPageView;
    MtkTvRectangle mSrcRectangleRectF;
    /* access modifiers changed from: private */
    public TeletextImplement mTTXImp;
    private LinearLayout mainLayout;
    private MtkTvUtil mtkTvUtil;
    private HashMap<String, List<String>> valueMap;

    private interface DataUpdateListener {
        void onDataUpdate(TeletextTopItem teletextTopItem);
    }

    public TTXTopDialog(Context context, int theme) {
        super(context, theme);
        this.valueMap = new HashMap<>();
        this.mContext = context;
        this.mTTXImp = TeletextImplement.getInstance();
        this.mtkTvUtil = MtkTvUtil.getInstance();
        this.mHandler = new MyHandler();
    }

    public TTXTopDialog(Context context) {
        this(context, -1);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_ttx_top);
        findView();
    }

    private void findView() {
        this.mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        this.mBlockView = (ListView) findViewById(R.id.ttx_block_list);
        this.mGroupView = (ListView) findViewById(R.id.ttx_group_list);
        this.mPageView = (ListView) findViewById(R.id.ttx_page_list);
        this.mBlockList = this.mTTXImp.getTopList();
        this.mBlockView.setAdapter(new TopAdapter(this.mContext, this.mBlockList, new MyDataUpdateListener(this.mGroupView)));
        this.mGroupView.setAdapter(new TopAdapter(this.mContext, this.mGroupList, new MyDataUpdateListener(this.mPageView)));
        this.mPageView.setAdapter(new TopAdapter(this.mContext, this.mPageList));
        this.mBlockView.setOnItemSelectedListener(new MyItemSelectedListener(this.mGroupView));
        this.mGroupView.setOnItemSelectedListener(new MyItemSelectedListener(this.mPageView));
        this.mBlockView.setOnKeyListener(new MyPageItemOnKey());
        this.mGroupView.setOnKeyListener(new MyPageItemOnKey());
        this.mPageView.setOnKeyListener(new MyPageItemOnKey());
        this.mBlockView.setFocusable(true);
        this.mGroupView.setFocusable(true);
        this.mPageView.setFocusable(true);
        this.mBlockView.requestFocus();
        this.mBlockView.requestFocusFromTouch();
        this.mBlockView.setSelection(0);
        this.mPageView.setNextFocusRightId(R.id.ttx_block_list);
        this.mPageView.setNextFocusLeftId(R.id.ttx_group_list);
        this.mGroupView.setNextFocusRightId(R.id.ttx_page_list);
        this.mGroupView.setNextFocusLeftId(R.id.ttx_block_list);
        this.mBlockView.setNextFocusLeftId(R.id.ttx_page_list);
        this.mBlockView.setNextFocusRightId(R.id.ttx_group_list);
    }

    private class MyDataUpdateListener implements DataUpdateListener {
        private ListView subListView;

        public MyDataUpdateListener() {
        }

        public MyDataUpdateListener(ListView listView) {
            this.subListView = listView;
        }

        public void setSubList(ListView listview) {
            this.subListView = listview;
        }

        public void onDataUpdate(TeletextTopItem data) {
            MtkLog.d(TTXTopDialog.TAG, "onDataUpdate : " + data + "subListView:" + this.subListView);
            if (data != null) {
                List<TeletextTopItem> sublist = data.getNextList();
                TopAdapter subAdapter = (TopAdapter) this.subListView.getAdapter();
                subAdapter.update(sublist);
                subAdapter.getCount();
                this.subListView.invalidate();
            }
        }
    }

    public void dismiss() {
        MtkLog.d(TAG, "dismiss!!!!!!!");
        this.mSrcRectangleRectF = this.mtkTvUtil.getScreenOutputDispRect("main");
        super.dismiss();
        if (this.mHandler != null) {
            if (this.mHandler.hasMessages(100)) {
                this.mHandler.removeMessages(100);
            }
            this.mHandler.sendEmptyMessageDelayed(100, 20);
        }
    }

    private class MyHandler extends Handler {
        private MyHandler() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                float l = 0.0f;
                float t = 0.0f;
                float r = 0.0f;
                float b = 0.0f;
                if (TTXTopDialog.this.mSrcRectangleRectF != null) {
                    l = TTXTopDialog.this.mSrcRectangleRectF.getX();
                    t = TTXTopDialog.this.mSrcRectangleRectF.getY();
                    r = TTXTopDialog.this.mSrcRectangleRectF.getW();
                    b = TTXTopDialog.this.mSrcRectangleRectF.getH();
                }
                MtkLog.d(TTXTopDialog.TAG, "handleMessage,mSrcRectangleRectF,l==" + l + ",t==" + t + ",r==" + r + ",b==" + b);
            }
        }
    }

    class MyPageItemOnKey implements View.OnKeyListener {
        MyPageItemOnKey() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int slectPosition = TTXTopDialog.this.mPageView.getSelectedItemPosition();
            MtkLog.v(TTXTopDialog.TAG, "page item keyCode =" + keyCode + " slectPosition = " + slectPosition);
            if (slectPosition < 0 || event.getAction() != 0) {
                return false;
            }
            if (keyCode == 23) {
                TTXTopDialog.this.dismiss();
                TTXTopDialog.this.mTTXImp.setTeletextPage(((MtkTvTeletextTopPageBase) ((TeletextTopItem) TTXTopDialog.this.mPageView.getAdapter().getItem(slectPosition)).getObject()).getNormalPageAddr());
                return true;
            } else if (keyCode == 56) {
                TTXTopDialog.this.dismiss();
                return true;
            } else if (keyCode != 89) {
                switch (keyCode) {
                    case 19:
                    case 20:
                        return false;
                    default:
                        return false;
                }
            } else {
                TTXTopDialog.this.dismiss();
                TeletextImplement.getInstance().stopTTX();
                return true;
            }
        }
    }

    private class MyItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private ListView subListView;

        public MyItemSelectedListener(ListView listView) {
            this.subListView = listView;
        }

        public MyItemSelectedListener() {
        }

        public void setSubList(ListView listview) {
            this.subListView = listview;
        }

        /* JADX WARNING: type inference failed for: r6v0, types: [android.widget.AdapterView<?>, android.widget.AdapterView] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onItemSelected(android.widget.AdapterView<?> r6, android.view.View r7, int r8, long r9) {
            /*
                r5 = this;
                java.lang.String r0 = "TTXTopDialog"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "onItemSelected : "
                r1.append(r2)
                r1.append(r7)
                java.lang.String r2 = "arg2:"
                r1.append(r2)
                r1.append(r8)
                java.lang.String r2 = "arg3 :"
                r1.append(r2)
                r1.append(r9)
                java.lang.String r2 = "subListView:"
                r1.append(r2)
                android.widget.ListView r2 = r5.subListView
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                android.widget.ListView r0 = r5.subListView
                if (r0 == 0) goto L_0x005a
                android.widget.Adapter r0 = r6.getAdapter()
                java.lang.Object r0 = r0.getItem(r8)
                com.mediatek.wwtv.tvcenter.nav.util.TeletextTopItem r0 = (com.mediatek.wwtv.tvcenter.nav.util.TeletextTopItem) r0
                java.util.List r1 = r0.getNextList()
                android.widget.ListView r2 = r5.subListView
                android.widget.ListAdapter r2 = r2.getAdapter()
                com.mediatek.wwtv.tvcenter.nav.view.TTXTopDialog$TopAdapter r2 = (com.mediatek.wwtv.tvcenter.nav.view.TTXTopDialog.TopAdapter) r2
                if (r2 == 0) goto L_0x004f
                r2.update(r1)
            L_0x004f:
                android.widget.ListView r3 = r5.subListView
                r4 = 0
                r3.setSelection(r4)
                android.widget.ListView r3 = r5.subListView
                r3.invalidate()
            L_0x005a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.TTXTopDialog.MyItemSelectedListener.onItemSelected(android.widget.AdapterView, android.view.View, int, long):void");
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
            MtkLog.d(TTXTopDialog.TAG, "onNothingSelected ~~ ");
        }
    }

    private List<String> getSubList(String str) {
        if (str == null) {
            return null;
        }
        return this.valueMap.get(str);
    }

    private class TopAdapter extends BaseAdapter {
        private List<TeletextTopItem> dataList = new ArrayList();
        private Context mContext;
        private LayoutInflater mInflater;
        private DataUpdateListener updateListener;

        public TopAdapter(Context context, List<TeletextTopItem> data) {
            if (data != null) {
                this.dataList.clear();
                this.dataList.addAll(data);
            }
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
        }

        public TopAdapter(Context context, List<TeletextTopItem> data, DataUpdateListener listener) {
            if (data != null) {
                this.dataList.clear();
                this.dataList.addAll(data);
            }
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
            this.updateListener = listener;
        }

        public void setListener(DataUpdateListener listener) {
            this.updateListener = listener;
        }

        public int getCount() {
            return this.dataList.size();
        }

        public Object getItem(int arg0) {
            return this.dataList.get(arg0);
        }

        public long getItemId(int arg0) {
            return (long) arg0;
        }

        public void update(List<TeletextTopItem> list) {
            this.dataList = list;
            if (this.updateListener != null) {
                if (this.dataList == null || this.dataList.size() <= 0) {
                    this.updateListener.onDataUpdate((TeletextTopItem) null);
                } else {
                    this.updateListener.onDataUpdate(this.dataList.get(0));
                }
            }
            notifyDataSetChanged();
        }

        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder holder;
            if (arg1 == null) {
                arg1 = this.mInflater.inflate(R.layout.top_item, (ViewGroup) null);
                holder = new ViewHolder();
                holder.mTextView = (TextView) arg1.findViewById(R.id.list_item);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }
            if (this.dataList != null) {
                holder.mTextView.setText(this.dataList.get(arg0).getName());
            }
            return arg1;
        }
    }

    private class ViewHolder {
        TextView mTextView;

        private ViewHolder() {
        }
    }
}
