package com.mediatek.wwtv.tvcenter.nav.util;

import com.mediatek.twoworlds.tv.MtkTvTeletext;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopBlockBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopPageBase;
import java.util.ArrayList;
import java.util.List;

public class TeletextTopItem {
    private MtkTvTeletext mTeletext = MtkTvTeletext.getInstance();
    private Object mTopItem;

    public TeletextTopItem(Object obj) {
        this.mTopItem = obj;
    }

    public Object getObject() {
        return this.mTopItem;
    }

    public String getName() {
        if (this.mTopItem instanceof MtkTvTeletextTopBlockBase) {
            MtkTvTeletextTopBlockBase tmpBlock = (MtkTvTeletextTopBlockBase) this.mTopItem;
            if (tmpBlock.isBlockHasName()) {
                return tmpBlock.getBlockName();
            }
            return Integer.toHexString(tmpBlock.getBlockPageAddr().getPageNumber());
        } else if (this.mTopItem instanceof MtkTvTeletextTopGroupBase) {
            MtkTvTeletextTopGroupBase tmpGroup = (MtkTvTeletextTopGroupBase) this.mTopItem;
            if (tmpGroup.isGroupHasName()) {
                return tmpGroup.getGroupName();
            }
            return Integer.toHexString(tmpGroup.getGroupPageAddr().getPageNumber());
        } else if (this.mTopItem instanceof MtkTvTeletextTopPageBase) {
            return Integer.toHexString(((MtkTvTeletextTopPageBase) this.mTopItem).getNormalPageAddr().getPageNumber());
        } else {
            return "";
        }
    }

    public List<TeletextTopItem> getNextList() {
        List<MtkTvTeletextTopPageBase> tmpPageList;
        List<TeletextTopItem> resultList = new ArrayList<>();
        if (this.mTopItem instanceof MtkTvTeletextTopBlockBase) {
            List<MtkTvTeletextTopGroupBase> tmpGroupList = this.mTeletext.getTeletextTopGroupList((MtkTvTeletextTopBlockBase) this.mTopItem);
            if (tmpGroupList != null && tmpGroupList.size() > 0) {
                for (MtkTvTeletextTopGroupBase group : tmpGroupList) {
                    resultList.add(new TeletextTopItem(group));
                }
            }
        } else if ((this.mTopItem instanceof MtkTvTeletextTopGroupBase) && (tmpPageList = this.mTeletext.getTeletextTopPageList((MtkTvTeletextTopGroupBase) this.mTopItem)) != null && tmpPageList.size() > 0) {
            for (MtkTvTeletextTopPageBase page : tmpPageList) {
                resultList.add(new TeletextTopItem(page));
            }
        }
        return resultList;
    }
}
