package com.mediatek.wwtv.tvcenter.nav.util;

import com.mediatek.twoworlds.tv.MtkTvTeletext;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopBlockBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopPageBase;
import java.util.ArrayList;
import java.util.List;

public class TeletextTopAdapter {
    private MtkTvTeletext mTeletext = MtkTvTeletext.getInstance();
    private Object mTopAdapter;

    public TeletextTopAdapter(Object obj) {
        this.mTopAdapter = obj;
    }

    public Object getObject() {
        return this.mTopAdapter;
    }

    public String getName() {
        if (this.mTopAdapter instanceof MtkTvTeletextTopBlockBase) {
            MtkTvTeletextTopBlockBase tmpBlock = (MtkTvTeletextTopBlockBase) this.mTopAdapter;
            if (tmpBlock.isBlockHasName()) {
                return tmpBlock.getBlockName();
            }
            return Integer.toHexString(tmpBlock.getBlockPageAddr().getPageNumber());
        } else if (this.mTopAdapter instanceof MtkTvTeletextTopGroupBase) {
            MtkTvTeletextTopGroupBase tmpGroup = (MtkTvTeletextTopGroupBase) this.mTopAdapter;
            if (tmpGroup.isGroupHasName()) {
                return tmpGroup.getGroupName();
            }
            return Integer.toHexString(tmpGroup.getGroupPageAddr().getPageNumber());
        } else if (!(this.mTopAdapter instanceof MtkTvTeletextTopPageBase)) {
            return "";
        } else {
            MtkTvTeletextTopPageBase tmpPage = (MtkTvTeletextTopPageBase) this.mTopAdapter;
            if (tmpPage.isNormalPageHasName()) {
                return tmpPage.getNormalPageName();
            }
            return Integer.toHexString(tmpPage.getNormalPageAddr().getPageNumber());
        }
    }

    public List<TeletextTopAdapter> getNextList() {
        List<MtkTvTeletextTopPageBase> tmpPageList;
        List<TeletextTopAdapter> resultList = new ArrayList<>();
        if (this.mTopAdapter instanceof MtkTvTeletextTopBlockBase) {
            List<MtkTvTeletextTopGroupBase> tmpGroupList = this.mTeletext.getTeletextTopGroupList((MtkTvTeletextTopBlockBase) this.mTopAdapter);
            if (tmpGroupList != null && tmpGroupList.size() > 0) {
                for (MtkTvTeletextTopGroupBase group : tmpGroupList) {
                    resultList.add(new TeletextTopAdapter(group));
                }
            }
        } else if ((this.mTopAdapter instanceof MtkTvTeletextTopGroupBase) && (tmpPageList = this.mTeletext.getTeletextTopPageList((MtkTvTeletextTopGroupBase) this.mTopAdapter)) != null && tmpPageList.size() > 0) {
            for (MtkTvTeletextTopPageBase page : tmpPageList) {
                resultList.add(new TeletextTopAdapter(page));
            }
        }
        return resultList;
    }
}
