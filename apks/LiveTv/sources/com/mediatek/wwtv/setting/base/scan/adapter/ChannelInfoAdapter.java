package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.wwtv.setting.ChannelInfoActivity;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.Pager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.util.TransItem;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelInfoAdapter extends BaseAdapter implements View.OnKeyListener {
    private static final String TAG = "ChannelInfoAdapter";
    private static Integer sDescriptionMaxHeight = null;
    String cfgId;
    private int channelSortNum = 0;
    Map<Integer, Boolean> checkMap = new HashMap();
    int currselectPosition = 0;
    EditChannel editChannel;
    TransItem mAction;
    Context mContext;
    EnterEditDetailListener mDetailListener;
    List<String[]> mList;
    private Pager mPager;
    private View mSelectedView = null;
    boolean needHighLightPos = false;

    public interface EnterEditDetailListener {
        void enterEditDetailItem(String[] strArr);
    }

    public ChannelInfoAdapter(Context context, TransItem action) {
        this.mContext = context;
        this.mAction = action;
    }

    public ChannelInfoAdapter(Context context, List<String[]> list, TransItem action, int gotoPage) {
        this.mContext = context;
        this.mPager = new Pager(list, gotoPage);
        this.mList = this.mPager.getRealDataList();
        MtkLog.d(TAG, "mList.size:" + this.mList.size());
        this.mAction = action;
        this.mDetailListener = (ChannelInfoActivity) this.mContext;
        bindData();
    }

    public void updateList(List<?> ulist) {
        if (this.mPager != null) {
            this.mPager.setPagerList(ulist);
            this.mList = this.mPager.getRealDataList();
            MtkLog.d(TAG, "updateList---mList.size:" + this.mList.size());
            for (int i = 0; i < this.mList.size(); i++) {
                MtkLog.d("clasica", "when update:" + this.mList.get(i)[2]);
            }
        }
    }

    public int updatePage(int pos, List<?> ulist) {
        this.mPager.currentPage = (pos / this.mPager.ITEM_PER_PAGE) + 1;
        updateList(ulist);
        if (pos % this.mPager.ITEM_PER_PAGE == 0) {
            return 0;
        }
        return pos % this.mPager.ITEM_PER_PAGE;
    }

    private void bindData() {
        this.editChannel = EditChannel.getInstance(this.mContext);
        if (TVContent.getInstance(this.mContext).getCurrentTunerMode() == 0) {
            this.cfgId = "g_nav__air_on_time_ch";
        } else {
            this.cfgId = "g_nav__cable_on_time_ch";
        }
    }

    public int getCount() {
        return this.mList.size();
    }

    public void setChannelSortNum(int channelSortNum2) {
        this.channelSortNum = channelSortNum2;
    }

    public Object getItem(int position) {
        return this.mList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getSelectPos() {
        return this.currselectPosition;
    }

    public void putCheckMap(int pos, boolean flag) {
        this.checkMap.put(Integer.valueOf(pos), Boolean.valueOf(flag));
    }

    public void clearCheckMap() {
        this.checkMap.clear();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v17, resolved type: com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter$ViewHolder} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r9, android.view.View r10, android.view.ViewGroup r11) {
        /*
            r8 = this;
            java.util.List<java.lang.String[]> r0 = r8.mList
            java.lang.Object r0 = r0.get(r9)
            java.lang.String[] r0 = (java.lang.String[]) r0
            r1 = 0
            if (r0 != 0) goto L_0x0013
            java.lang.String r2 = "ChannelInfoAdapter"
            java.lang.String r3 = "mInfo is null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            goto L_0x0049
        L_0x0013:
            java.lang.String r2 = "ChannelInfoAdapter"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "getView---mInfo.length:"
            r3.append(r4)
            int r4 = r0.length
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            r2 = r1
        L_0x002b:
            int r3 = r0.length
            if (r2 >= r3) goto L_0x0049
            java.lang.String r3 = "ChannelInfoAdapter"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "getView---mInfo:"
            r4.append(r5)
            r5 = r0[r2]
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            int r2 = r2 + 1
            goto L_0x002b
        L_0x0049:
            r2 = 2131362002(0x7f0a00d2, float:1.8343772E38)
            if (r10 != 0) goto L_0x00dd
            android.content.Context r3 = r8.mContext
            android.view.LayoutInflater r3 = android.view.LayoutInflater.from(r3)
            com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter$ViewHolder r4 = new com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter$ViewHolder
            r4.<init>()
            boolean r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            r6 = 2131361999(0x7f0a00cf, float:1.8343766E38)
            if (r5 == 0) goto L_0x00aa
            r5 = 2131493025(0x7f0c00a1, float:1.8609518E38)
            android.view.View r10 = r3.inflate(r5, r11, r1)
            r5 = 2131362043(0x7f0a00fb, float:1.8343855E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.num = r5
            r5 = 2131362045(0x7f0a00fd, float:1.834386E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.type = r5
            r5 = 2131362041(0x7f0a00f9, float:1.8343851E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.freq = r5
            r5 = 2131362044(0x7f0a00fc, float:1.8343857E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.system = r5
            r5 = 2131362042(0x7f0a00fa, float:1.8343853E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.name = r5
            android.view.View r5 = r10.findViewById(r6)
            android.widget.CheckBox r5 = (android.widget.CheckBox) r5
            r4.isCheck = r5
            goto L_0x00d9
        L_0x00aa:
            r5 = 2131492897(0x7f0c0021, float:1.8609259E38)
            android.view.View r10 = r3.inflate(r5, r11, r1)
            android.view.View r5 = r10.findViewById(r2)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.channelNo = r5
            r5 = 2131362003(0x7f0a00d3, float:1.8343774E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.channelType = r5
            r5 = 2131362001(0x7f0a00d1, float:1.834377E38)
            android.view.View r5 = r10.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.channelName = r5
            android.view.View r5 = r10.findViewById(r6)
            android.widget.CheckBox r5 = (android.widget.CheckBox) r5
            r4.isCheck = r5
        L_0x00d9:
            r10.setTag(r4)
            goto L_0x00e4
        L_0x00dd:
            java.lang.Object r3 = r10.getTag()
            r4 = r3
            com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter$ViewHolder r4 = (com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter.ViewHolder) r4
        L_0x00e4:
            r3 = r4
            if (r0 == 0) goto L_0x019d
            if (r3 != 0) goto L_0x00f2
            java.lang.String r1 = "ChannelInfoAdapter"
            java.lang.String r4 = "holder is null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
            goto L_0x0198
        L_0x00f2:
            java.lang.String r4 = "forupdate"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getView:"
            r5.append(r6)
            r6 = 2
            r7 = r0[r6]
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            boolean r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            r5 = 1
            if (r4 == 0) goto L_0x0165
            android.widget.TextView r4 = r3.num
            r1 = r0[r1]
            r4.setText(r1)
            android.widget.TextView r1 = r3.type
            r4 = r0[r5]
            r1.setText(r4)
            android.widget.TextView r1 = r3.freq
            r4 = 4
            r5 = r0[r4]
            r1.setText(r5)
            android.widget.TextView r1 = r3.system
            r5 = 6
            r5 = r0[r5]
            r1.setText(r5)
            java.lang.String r1 = "ChannelInfoAdapter"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = ":"
            r5.append(r7)
            r4 = r0[r4]
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.v(r1, r4)
            r1 = r0[r6]
            if (r1 == 0) goto L_0x015d
            r1 = r0[r6]
            int r1 = r1.length()
            if (r1 != 0) goto L_0x0155
            goto L_0x015d
        L_0x0155:
            android.widget.TextView r1 = r3.name
            r4 = r0[r6]
            r1.setText(r4)
            goto L_0x0198
        L_0x015d:
            android.widget.TextView r1 = r3.name
            java.lang.String r4 = "________"
            r1.setText(r4)
            goto L_0x0198
        L_0x0165:
            android.widget.TextView r4 = r3.channelNo
            r1 = r0[r1]
            r4.setText(r1)
            android.widget.TextView r1 = r3.channelType
            r4 = r0[r5]
            r1.setText(r4)
            android.widget.TextView r1 = r3.channelName
            r4 = r0[r6]
            r1.setText(r4)
            r1 = r0[r6]
            if (r1 == 0) goto L_0x0191
            java.lang.String r1 = ""
            r4 = r0[r6]
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x0189
            goto L_0x0191
        L_0x0189:
            android.widget.TextView r1 = r3.channelName
            r4 = r0[r6]
            r1.setText(r4)
            goto L_0x0198
        L_0x0191:
            android.widget.TextView r1 = r3.channelName
            java.lang.String r4 = "________"
            r1.setText(r4)
        L_0x0198:
            if (r3 == 0) goto L_0x019d
            r8.showData(r0, r3)
        L_0x019d:
            r10.setTag(r2, r0)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    class ViewHolder {
        TextView channelName;
        TextView channelNo;
        TextView channelType;
        TextView freq;
        CheckBox isCheck;
        TextView name;
        TextView num;
        TextView system;
        TextView type;

        ViewHolder() {
        }
    }

    public void goToPrevPage() {
        this.mPager.currentPage--;
        if (this.mPager.currentPage < 1) {
            this.mPager.currentPage = 1;
            return;
        }
        this.mList = this.mPager.getRealDataList();
        notifyDataSetChanged();
    }

    public void goToNextPage() {
        this.mPager.currentPage++;
        if (this.mPager.currentPage > this.mPager.pageTotal) {
            this.mPager.currentPage = this.mPager.pageTotal;
            return;
        }
        this.mList = this.mPager.getRealDataList();
        notifyDataSetChanged();
    }

    public int getCurrPage() {
        return this.mPager.currentPage;
    }

    private void showData(String[] mData, ViewHolder holder) {
        if (this.mAction.getmItemId().equals(MenuConfigManager.SETUP_POWER_ONCHANNEL_LIST)) {
            if (Integer.parseInt(mData[3]) == TVContent.getInstance(this.mContext).getConfigValue(this.cfgId)) {
                holder.isCheck.setChecked(true);
            } else {
                holder.isCheck.setChecked(false);
            }
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.PARENTAL_CHANNEL_BLOCK_CHANNELLIST)) {
            int ch_num = Integer.parseInt(mData[3]);
            if (this.editChannel.isChannelBlock(ch_num)) {
                MtkLog.d(TAG, ch_num + " channel is blocked");
                holder.isCheck.setChecked(true);
                return;
            }
            holder.isCheck.setChecked(false);
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.TV_CHANNEL_DECODE_LIST)) {
            int ch_num2 = Integer.parseInt(mData[3]);
            if (this.editChannel.isChannelDecode(ch_num2)) {
                MtkLog.d(TAG, ch_num2 + " channel is decode");
                holder.isCheck.setChecked(true);
                return;
            }
            holder.isCheck.setChecked(false);
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.TV_CHANNELFINE_TUNE_EDIT_LIST)) {
            holder.isCheck.setVisibility(8);
            if (CommonIntegration.getInstance().getCurChInfo() instanceof MtkTvAnalogChannelInfo) {
                holder.channelType.setVisibility(0);
                if (mData[2] == null || "".equals(mData[2])) {
                    holder.channelType.setText("________");
                } else {
                    holder.channelType.setText(mData[2]);
                }
            } else {
                holder.channelType.setVisibility(0);
            }
            holder.channelName.setText(mData[4]);
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST)) {
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                int ch_num3 = Integer.parseInt(mData[3]);
                if (this.editChannel.getCurrentChannelId() == ch_num3) {
                    holder.isCheck.setVisibility(4);
                    return;
                }
                holder.isCheck.setVisibility(0);
                if (this.editChannel.isChannelSkip(ch_num3)) {
                    MtkLog.d(TAG, ch_num3 + " channel is skipped");
                    holder.isCheck.setChecked(true);
                    return;
                }
                holder.isCheck.setChecked(false);
                return;
            }
            holder.isCheck.setVisibility(4);
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.TV_CHANNEL_EDIT_LIST)) {
            if (CommonIntegration.isCNRegion()) {
                int ch_num4 = Integer.parseInt(mData[3]);
                if (this.editChannel.isChannelSkip(ch_num4)) {
                    MtkLog.d(TAG, ch_num4 + " channel is skipped");
                    holder.isCheck.setChecked(true);
                    return;
                }
                holder.isCheck.setChecked(false);
                return;
            }
            holder.isCheck.setVisibility(8);
            if (((ChannelInfoActivity) this.mContext).isM7Enable && Integer.parseInt(mData[0]) < 4001) {
                holder.channelNo.setTextColor(-7829368);
                holder.channelName.setTextColor(-7829368);
                holder.channelType.setTextColor(-7829368);
            }
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.TV_CHANNEL_SORT_CHANNELLIST)) {
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                int ch_num5 = Integer.parseInt(mData[3]);
                if (this.channelSortNum != 0 && ch_num5 == this.channelSortNum) {
                    putCheckMap(ch_num5, true);
                    holder.isCheck.setChecked(this.checkMap.get(Integer.valueOf(ch_num5)).booleanValue());
                } else if (this.checkMap.containsKey(Integer.valueOf(ch_num5))) {
                    holder.isCheck.setChecked(this.checkMap.get(Integer.valueOf(ch_num5)).booleanValue());
                } else {
                    holder.isCheck.setChecked(false);
                }
            } else {
                holder.isCheck.setVisibility(4);
            }
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.TV_CHANNEL_MOVE_CHANNELLIST)) {
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                int ch_num6 = Integer.parseInt(mData[3]);
                if (this.channelSortNum != 0 && ch_num6 == this.channelSortNum) {
                    putCheckMap(ch_num6, true);
                    holder.isCheck.setChecked(this.checkMap.get(Integer.valueOf(ch_num6)).booleanValue());
                } else if (!this.editChannel.isChannelSkip(ch_num6)) {
                    holder.isCheck.setVisibility(0);
                    MtkLog.d(TAG, ch_num6 + " channel is skipped");
                    if (this.checkMap.containsKey(Integer.valueOf(ch_num6))) {
                        holder.isCheck.setChecked(this.checkMap.get(Integer.valueOf(ch_num6)).booleanValue());
                    } else {
                        holder.isCheck.setChecked(false);
                    }
                } else {
                    holder.isCheck.setVisibility(4);
                }
            } else {
                holder.isCheck.setVisibility(4);
            }
        } else if (this.mAction.getmItemId().equals(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST)) {
            if (this.editChannel.getSchBlockType(Integer.parseInt(mData[3])) == 0) {
                holder.isCheck.setChecked(false);
            } else {
                holder.isCheck.setChecked(true);
            }
        } else if (this.mAction.getmItemId().startsWith(MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL)) {
            if (this.mAction.getTitle().equals("")) {
                holder.channelNo.setText("(No Text.)");
            } else {
                holder.channelNo.setText(this.mAction.getTitle());
            }
            if (this.mAction.mInitValue == 1) {
                holder.isCheck.setChecked(true);
            } else {
                holder.isCheck.setChecked(false);
            }
        } else if (this.mAction.getmItemId().startsWith(MenuConfigManager.SOUNDTRACKS_GET_STRING) || this.mAction.getmItemId().startsWith(MenuConfigManager.CFG_MENU_AUDIOINFO_GET_STRING)) {
            holder.isCheck.setVisibility(8);
        }
    }

    public void onKeyEnter(String[] mData, View view) {
        String mId = this.mAction.getmItemId();
        CheckBox box = (CheckBox) view.findViewById(R.id.channel_info_check);
        MtkLog.d(TAG, "onKeyEnter mId:" + mId);
        if (mId.equals(MenuConfigManager.SETUP_POWER_ONCHANNEL_LIST)) {
            changeBoxCheck(this.editChannel.setPowerOnChannel(Integer.parseInt(mData[3])), box);
            notifyDataSetChanged();
        } else if (mId.equals(MenuConfigManager.PARENTAL_CHANNEL_BLOCK_CHANNELLIST)) {
            int ch_num = Integer.parseInt(mData[3]);
            boolean isChannelBlock = this.editChannel.isChannelBlock(ch_num);
            MtkLog.d(TAG, " channel block previous state: " + isChannelBlock);
            if (isChannelBlock) {
                this.editChannel.blockChannel(ch_num, false);
            } else {
                this.editChannel.blockChannel(ch_num, true);
            }
            boolean isChannelBlock2 = this.editChannel.isChannelBlock(ch_num);
            changeBoxCheck(isChannelBlock2, box);
            MtkLog.d(TAG, "channel block current state: " + isChannelBlock2);
        } else if (mId.equals(MenuConfigManager.TV_CHANNELFINE_TUNE_EDIT_LIST)) {
            ((ChannelInfoActivity) this.mContext).finetuneInfoDialog(mData);
        } else if (mId.equals(MenuConfigManager.TV_CHANNEL_SKIP_CHANNELLIST)) {
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                int ch_num2 = Integer.parseInt(mData[3]);
                if (this.editChannel.getCurrentChannelId() != ch_num2) {
                    boolean isSkip = this.editChannel.isChannelSkip(ch_num2);
                    MtkLog.d(TAG, mId + ",isSkip: " + isSkip);
                    this.editChannel.setChannelSkip(ch_num2, isSkip ^ true);
                    changeBoxCheck(isSkip ^ true, box);
                    return;
                }
                return;
            }
            MtkLog.d(TAG, " isM7Enable mData[0] " + mData[0]);
        } else if (mId.equals(MenuConfigManager.TV_CHANNEL_DECODE_LIST)) {
            int ch_num3 = Integer.parseInt(mData[3]);
            boolean isDecode = this.editChannel.isChannelDecode(ch_num3);
            MtkLog.d(TAG, mId + ",isdecode: " + isDecode);
            this.editChannel.setChannelDecode(ch_num3, isDecode ^ true);
            changeBoxCheck(isDecode ^ true, box);
        } else if (mId.equals(MenuConfigManager.TV_CHANNEL_SORT_CHANNELLIST)) {
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                int ch_num4 = Integer.parseInt(mData[3]);
                MtkLog.d(TAG, mId + ",ch_num: " + ch_num4);
                if (((ChannelInfoActivity) this.mContext).channelSort(ch_num4)) {
                    box.setChecked(false);
                    putCheckMap(ch_num4, false);
                    return;
                }
                putCheckMap(ch_num4, true);
                box.setChecked(true);
                return;
            }
            MtkLog.d(TAG, "isTkgsEnable or  isM7Enable mData[0] " + mData[0]);
        } else if (mId.equals(MenuConfigManager.TV_CHANNEL_MOVE_CHANNELLIST)) {
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                int ch_num5 = Integer.parseInt(mData[3]);
                MtkLog.d(TAG, mId + ",ch_num: " + ch_num5);
                if (this.editChannel.isChannelSkip(ch_num5)) {
                    return;
                }
                if (((ChannelInfoActivity) this.mContext).channelMove(ch_num5)) {
                    box.setChecked(false);
                    putCheckMap(ch_num5, false);
                    return;
                }
                putCheckMap(ch_num5, true);
                box.setChecked(true);
                return;
            }
            MtkLog.d(TAG, "isTkgsEnable or  isM7Enable mData[0] " + mData[0]);
        } else if (mId.equals(MenuConfigManager.TV_CHANNEL_EDIT_LIST)) {
            MtkLog.d(TAG, mId);
            if (!((ChannelInfoActivity) this.mContext).isM7Enable || Integer.parseInt(mData[0]) >= 4001) {
                this.mDetailListener.enterEditDetailItem(mData);
                return;
            }
            MtkLog.d(TAG, "isM7Enable mData[0] " + mData[0]);
        } else if (mId.equals(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST)) {
            MtkLog.d(TAG, mId + "mDataItem.mInitValue:" + this.mAction.mInitValue);
        } else if (mId.startsWith(MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL)) {
            if (this.mAction.mInitValue == 1) {
                this.mAction.mInitValue = 0;
                box.setChecked(false);
            } else {
                this.mAction.mInitValue = 1;
                box.setChecked(true);
            }
            int index = Integer.parseInt(mId.substring(MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL.length(), mId.length()));
            this.editChannel.setOpenVCHIP(this.mAction.mStartValue, this.mAction.mEndValue, index);
            MtkLog.d(TAG, "PARENTAL_OPEN_VCHIP_LEVEL regionIndex: " + this.mAction.mStartValue);
            MtkLog.d(TAG, "PARENTAL_OPEN_VCHIP_LEVEL dimIndex: " + this.mAction + "mDataItem:" + this.mAction.mEndValue);
            StringBuilder sb = new StringBuilder();
            sb.append("PARENTAL_OPEN_VCHIP_LEVEL levIndex: ");
            sb.append(index);
            MtkLog.d(TAG, sb.toString());
        } else if (mId.startsWith(MenuConfigManager.SOUNDTRACKS_GET_STRING)) {
            int index2 = Integer.parseInt(mId.substring(MenuConfigManager.SOUNDTRACKS_GET_STRING.length() + 1, mId.length()));
            MtkLog.d(TAG, "set SOUNDTRACKS_SET_SELECT" + index2);
            TVContent.getInstance(this.mContext).setConfigValue("g_menu__soundtracksselect", index2);
        } else if (mId.startsWith(MenuConfigManager.CFG_MENU_AUDIOINFO_GET_STRING)) {
            int index3 = Integer.parseInt(mId.substring(MenuConfigManager.CFG_MENU_AUDIOINFO_GET_STRING.length() + 1, mId.length()));
            MtkLog.d(TAG, "set CFG_MENU_AUDIOINFO_GET_STRING" + index3);
            TVContent.getInstance(this.mContext).setConfigValue("g_menu__audioinfoselect", index3);
        }
    }

    private void changeBoxCheck(boolean isSelected, CheckBox box) {
        MtkLog.d(TAG, "changeBackGround isSelected:" + isSelected);
        if (isSelected) {
            box.setChecked(true);
        } else {
            box.setChecked(false);
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "adapter onkey listener");
        if ((keyCode != 23 && keyCode != 66) || event.getAction() != 0) {
            return false;
        }
        String[] mData = (String[]) v.getTag(R.id.channel_info_no);
        MtkLog.d(TAG, "onKEY:" + mData[0]);
        onKeyEnter(mData, v);
        return true;
    }
}
