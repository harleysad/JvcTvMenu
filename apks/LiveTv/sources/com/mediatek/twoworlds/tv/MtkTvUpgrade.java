package com.mediatek.twoworlds.tv;

public class MtkTvUpgrade extends MtkTvUpgradeBase {
    private static MtkTvUpgrade mtkTvUpgrade = null;

    private MtkTvUpgrade() {
    }

    public static MtkTvUpgrade getInstance() {
        if (mtkTvUpgrade != null) {
            return mtkTvUpgrade;
        }
        mtkTvUpgrade = new MtkTvUpgrade();
        return mtkTvUpgrade;
    }
}
