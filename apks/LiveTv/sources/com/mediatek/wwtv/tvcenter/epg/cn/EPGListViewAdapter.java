package com.mediatek.wwtv.tvcenter.epg.cn;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGBaseAdapter;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EPGListViewAdapter extends EPGBaseAdapter<EPGChannelInfo> {
    private static final String TAG = "EPGListViewAdpter";
    private List<Integer> mActiveWindowChannelIdList;
    private List<Integer> mAlreadyGetChannelIdList;
    private Drawable mAnalogIcon;
    private int mDayNum;
    private EPGListView mEPGListView;
    public HashMap<Integer, EPGChannelInfo> mHashGroup;
    private DataReader mReader;
    private int mStartHour;
    private HashMap<Integer, EPGChannelInfo> mUpdateHashGroup = new HashMap<>();
    private int mWidth;

    public EPGListView getEPGListView() {
        return this.mEPGListView;
    }

    public void setEPGListView(EPGListView epgListView) {
        this.mEPGListView = epgListView;
    }

    public int getDayNum() {
        return this.mDayNum;
    }

    public void setDayNum(int mDayNum2) {
        this.mDayNum = mDayNum2;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int mWidth2) {
        this.mWidth = mWidth2;
    }

    public int getStartHour() {
        return this.mStartHour;
    }

    public void setStartHour(int mStartTime) {
        this.mStartHour = mStartTime;
    }

    public boolean containsChannelId(int channelId) {
        if (this.mActiveWindowChannelIdList != null) {
            return this.mActiveWindowChannelIdList.contains(Integer.valueOf(channelId));
        }
        return false;
    }

    public void addAlreadyChnnelId(int channelId) {
        if (this.mAlreadyGetChannelIdList != null && !this.mAlreadyGetChannelIdList.contains(Integer.valueOf(channelId))) {
            for (Integer intValue : this.mActiveWindowChannelIdList) {
                int id = intValue.intValue();
                if (!this.mAlreadyGetChannelIdList.contains(Integer.valueOf(id))) {
                    if (id == channelId) {
                        this.mAlreadyGetChannelIdList.add(Integer.valueOf(channelId));
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public boolean isAlreadyGetAll() {
        if (this.mAlreadyGetChannelIdList == null || this.mActiveWindowChannelIdList == null || this.mAlreadyGetChannelIdList.size() >= this.mActiveWindowChannelIdList.size()) {
            return true;
        }
        return false;
    }

    public void clearWindowList() {
        if (this.mActiveWindowChannelIdList != null) {
            this.mActiveWindowChannelIdList.clear();
        }
        if (this.mAlreadyGetChannelIdList != null) {
            this.mAlreadyGetChannelIdList.clear();
        }
    }

    public EPGListViewAdapter(Context mContext, int mStartTime) {
        super(mContext);
        this.mStartHour = mStartTime;
        this.mReader = DataReader.getInstance(mContext);
        this.mAnalogIcon = mContext.getResources().getDrawable(R.drawable.epg_channel_icon);
        this.mActiveWindowChannelIdList = new ArrayList();
        this.mAlreadyGetChannelIdList = new ArrayList();
    }

    public void setGroup(List<EPGChannelInfo> group) {
        super.setGroup(group);
        MtkLog.d(TAG, "setActiveWindow setGroup>>");
        this.mActiveWindowChannelIdList.clear();
        this.mAlreadyGetChannelIdList.clear();
        if (group != null && group.size() > 0) {
            if (this.mHashGroup == null) {
                this.mHashGroup = new HashMap<>();
            } else {
                this.mHashGroup.clear();
            }
            for (EPGChannelInfo iiiii : group) {
                this.mHashGroup.put(Integer.valueOf(iiiii.getTVChannel().getChannelId()), iiiii);
                this.mActiveWindowChannelIdList.add(Integer.valueOf(iiiii.getTVChannel().getChannelId()));
                MtkLog.d(TAG, "syncActiveWindowProgram setActiveWindow>>" + iiiii.mId + "  " + iiiii.getTVChannel().getChannelId() + "  " + iiiii.getName());
            }
            TIFFunctionUtil.setActivityWindow(TIFFunctionUtil.getApiChannelListFromEpgChannel(group), EPGTimeConvert.getInstance().setDate(EPGUtil.getCurrentDateDayAsMills(), this.mDayNum, (long) this.mStartHour));
        }
    }

    public void setActiveWindow() {
        MtkLog.d(TAG, "setActiveWindow group>>");
        this.mActiveWindowChannelIdList.clear();
        this.mAlreadyGetChannelIdList.clear();
        if (this.group != null && this.group.size() > 0) {
            if (this.mHashGroup == null) {
                this.mHashGroup = new HashMap<>();
            } else {
                this.mHashGroup.clear();
            }
            for (EPGChannelInfo iiiii : this.group) {
                this.mActiveWindowChannelIdList.add(Integer.valueOf(iiiii.getTVChannel().getChannelId()));
                this.mHashGroup.put(Integer.valueOf(iiiii.getTVChannel().getChannelId()), iiiii);
                MtkLog.d(TAG, "syncActiveWindowProgram setActiveWindow>>" + iiiii.mId + "  " + iiiii.getTVChannel().getChannelId() + "  " + iiiii.getName());
            }
            TIFFunctionUtil.setActivityWindow(TIFFunctionUtil.getApiChannelListFromEpgChannel(this.group), EPGTimeConvert.getInstance().setDate(EPGUtil.getCurrentDateDayAsMills(), this.mDayNum, (long) this.mStartHour));
        }
    }

    public int getIndexOfChannel(EPGChannelInfo channel) {
        return this.mActiveWindowChannelIdList.indexOf(Integer.valueOf(channel.getTVChannel().getChannelId()));
    }

    public void putUpdateChannel(int channelId) {
        this.mUpdateHashGroup.put(Integer.valueOf(channelId), this.mHashGroup.get(Integer.valueOf(channelId)));
    }

    public void clearUpdateChannels() {
        this.mUpdateHashGroup.clear();
    }

    public List<EPGChannelInfo> getActivewindowChannels() {
        new ArrayList();
        return this.group;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        MtkLog.d(TAG, "-----getView----->[Position] " + position + "  " + EPGConfig.FROM_WHERE);
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.epg_cn_listview_item_layout, (ViewGroup) null);
            mViewHolder.number = (TextView) convertView.findViewById(R.id.epg_channel_number);
            mViewHolder.name = (TextView) convertView.findViewById(R.id.epg_channel_name);
            mViewHolder.mDynamicLinearLayout = (EPGLinearLayout) convertView.findViewById(R.id.epg_program_forecast_linearlayout);
            mViewHolder.mDynamicLinearLayout.setWidth(this.mWidth);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (getCount() > 0) {
            EPGChannelInfo mChannel = (EPGChannelInfo) getItem(position);
            if (mChannel != null) {
                MtkLog.d(TAG, "mChannel.getTVChannel().isRadioService()>>" + mChannel.getTVChannel().isRadioService());
                if (mChannel.getTVChannel().isRadioService()) {
                    Drawable radioIcon = this.mContext.getResources().getDrawable(R.drawable.epg_radio_channel_icon);
                    radioIcon.setBounds(0, 0, this.mAnalogIcon.getMinimumWidth(), this.mAnalogIcon.getMinimumWidth());
                    mViewHolder.number.setCompoundDrawables(radioIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                } else if (mChannel.getTVChannel() instanceof MtkTvAnalogChannelInfo) {
                    Drawable analogIcon = this.mContext.getResources().getDrawable(R.drawable.epg_channel_icon);
                    analogIcon.setBounds(0, 0, analogIcon.getMinimumWidth(), analogIcon.getMinimumWidth());
                    mViewHolder.number.setCompoundDrawables(analogIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                } else {
                    Drawable nothingIcon = this.mContext.getResources().getDrawable(R.drawable.translucent_background);
                    nothingIcon.setBounds(0, 0, this.mAnalogIcon.getMinimumWidth(), this.mAnalogIcon.getMinimumWidth());
                    mViewHolder.number.setCompoundDrawables(nothingIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                }
                mViewHolder.number.setCompoundDrawablePadding(10);
                mViewHolder.number.setText("   " + mChannel.getDisplayNumber());
                mViewHolder.name.setText("   " + mChannel.getName());
            }
            if (mChannel == null || mChannel.getTVChannel().getChannelId() != this.mReader.getCurrentChId() || !EPGConfig.init) {
                if (mChannel == null || mChannel.getmTVProgramInfoList() == null || mChannel.getmTVProgramInfoList().size() <= 0) {
                    mViewHolder.mDynamicLinearLayout.removeAllViews();
                    mViewHolder.mDynamicLinearLayout.setBackgroundResource(R.drawable.epg_analog_channel_bg);
                } else {
                    List<EPGProgramInfo> mChildViewData = mChannel.getmTVProgramInfoList();
                    MtkLog.d(TAG, "---- getView----->[Child View Size] " + mChildViewData.size() + ">>" + EPGConfig.FROM_WHERE);
                    mViewHolder.mDynamicLinearLayout.setBackground((Drawable) null);
                    mViewHolder.mDynamicLinearLayout.setAdpter(mChildViewData, false);
                    if (position == EPGConfig.SELECTED_CHANNEL_POSITION) {
                        if (EPGConfig.FROM_WHERE == 21) {
                            mViewHolder.mDynamicLinearLayout.setSelectedPosition(mChildViewData.size() - 1);
                        } else if (EPGConfig.FROM_WHERE == 22) {
                            mViewHolder.mDynamicLinearLayout.setSelectedPosition(0);
                        } else if (EPGConfig.FROM_WHERE == 24 || EPGConfig.FROM_WHERE == 23) {
                            mViewHolder.mDynamicLinearLayout.setSelectedPosition(0);
                        } else if (EPGConfig.FROM_WHERE == 25) {
                            mViewHolder.mDynamicLinearLayout.setSelectedPosition(mChannel.getNextPosition(this.mEPGListView.getLastSelectedTVProgram()));
                        } else if (EPGConfig.FROM_WHERE == 26) {
                            mViewHolder.mDynamicLinearLayout.setSelectedPosition(mChannel.getPlayingTVProgramPositon());
                        } else if (EPGConfig.FROM_WHERE == 27) {
                            int index = mViewHolder.mDynamicLinearLayout.getmCurrentSelectPosition();
                            if (index < 0) {
                                index = 0;
                            } else if (index >= mChildViewData.size()) {
                                index = mChildViewData.size() - 1;
                            }
                            mViewHolder.mDynamicLinearLayout.setSelectedPosition(index);
                        }
                    }
                }
            } else if (mChannel.getmTVProgramInfoList() == null || mChannel.getmTVProgramInfoList().size() <= 0) {
                mViewHolder.mDynamicLinearLayout.removeAllViews();
                mViewHolder.mDynamicLinearLayout.setBackgroundResource(R.drawable.epg_analog_channel_bg);
            } else {
                int index2 = mChannel.getPlayingTVProgramPositon();
                mViewHolder.mDynamicLinearLayout.setmCurrentSelectPosition(index2);
                List<EPGProgramInfo> mChildViewData2 = mChannel.getmTVProgramInfoList();
                MtkLog.d(TAG, "---- getView EPGConfig.init == true----->[Playing TVProgram Position] " + index2 + ">>" + mChildViewData2.size());
                mViewHolder.mDynamicLinearLayout.setBackground((Drawable) null);
                if (EPGConfig.SELECTED_CHANNEL_POSITION != position) {
                    mViewHolder.mDynamicLinearLayout.setAdpter(mChildViewData2, false);
                } else {
                    mViewHolder.mDynamicLinearLayout.setAdpter(mChildViewData2, true);
                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        EPGLinearLayout mDynamicLinearLayout;
        TextView name;
        TextView number;

        ViewHolder() {
        }
    }
}