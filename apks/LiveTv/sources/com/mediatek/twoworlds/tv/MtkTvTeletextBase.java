package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextPageBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopBlockBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopPageBase;
import java.util.ArrayList;
import java.util.List;

public class MtkTvTeletextBase {
    private static final String TAG = "MtkTvTeletextBase";
    MtkTvKeyEventBase mtkKeyEvent = new MtkTvKeyEventBase();

    public MtkTvTeletextBase() {
        Log.d(TAG, "MtkTvTeletextBase object created");
    }

    public int start() {
        return this.mtkKeyEvent.sendKeyClick(this.mtkKeyEvent.androidKeyToDFBkey(89));
    }

    public int stop() {
        return this.mtkKeyEvent.sendKeyClick(this.mtkKeyEvent.androidKeyToDFBkey(4));
    }

    public MtkTvTeletextPageBase getCurrentTeletextPage() {
        Log.d(TAG, "Enter getCurrentTeletextPage Here.\n");
        MtkTvTeletextPageBase teletextPageAddr = new MtkTvTeletextPageBase();
        int ret = TVNativeWrapper.getCurrentTeletextPage_native(teletextPageAddr);
        if (ret != 0) {
            Log.e(TAG, "TVNativeWrapper.getCurrentTeletextPage_native failed! return " + ret + ".\n");
        }
        return teletextPageAddr;
    }

    public int setTeletextPage(MtkTvTeletextPageBase teletextPageAddr) {
        Log.d(TAG, "Enter setTeletextPage Here.\n");
        if (teletextPageAddr != null && 255 != teletextPageAddr.getPageNumber()) {
            return TVNativeWrapper.setTeletextPage_native(teletextPageAddr);
        }
        Log.e(TAG, "Invalid teletext page address!\n");
        return -1;
    }

    public boolean teletextHasTopInfo() {
        Log.d(TAG, "Enter teletextHasTopInfo Here.\n");
        return TVNativeWrapper.teletextHasTopInfo_native();
    }

    public List<MtkTvTeletextTopBlockBase> getTeletextTopBlockList() {
        Log.d(TAG, "Enter getTeletextTopBlockList Here.\n");
        List<MtkTvTeletextTopBlockBase> blockList = new ArrayList<>();
        int ret = TVNativeWrapper.getTeletextTopBlockList_native(blockList);
        if (ret == 0) {
            return blockList;
        }
        Log.e(TAG, "TVNativeWrapper.getTeletextTopBlockList_native failed! return " + ret + ".\n");
        return null;
    }

    public List<MtkTvTeletextTopGroupBase> getTeletextTopGroupList(MtkTvTeletextTopBlockBase indexBlock) {
        Log.d(TAG, "Enter getTeletextTopGroupList Here.\n");
        if (indexBlock == null || indexBlock.getBlockPageAddr() == null || 255 == indexBlock.getBlockPageAddr().getPageNumber()) {
            Log.e(TAG, "Invalid teletext top block!\n");
            return null;
        }
        List<MtkTvTeletextTopGroupBase> groupList = new ArrayList<>();
        int ret = TVNativeWrapper.getTeletextTopGroupList_native(indexBlock, groupList);
        if (ret == 0) {
            return groupList;
        }
        Log.e(TAG, "TVNativeWrapper.getTeletextTopGroupList failed! return " + ret + ".\n");
        return null;
    }

    public List<MtkTvTeletextTopPageBase> getTeletextTopPageList(MtkTvTeletextTopGroupBase indexGroup) {
        Log.d(TAG, "Enter getTeletextTopPageList Here.\n");
        if (indexGroup == null || indexGroup.getGroupPageAddr() == null || 255 == indexGroup.getGroupPageAddr().getPageNumber()) {
            Log.e(TAG, "Invalid teletext top group!\n");
            return null;
        }
        List<MtkTvTeletextTopPageBase> normalPageList = new ArrayList<>();
        int ret = TVNativeWrapper.getTeletextTopPageList_native(indexGroup, normalPageList);
        if (ret == 0) {
            return normalPageList;
        }
        Log.e(TAG, "TVNativeWrapper.getTeletextTopPageList failed! return " + ret + ".\n");
        return null;
    }
}
