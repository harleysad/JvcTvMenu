package com.mediatek.twoworlds.tv;

public class MtkTvPWDDialog extends MtkTvPWDDialogBase {
    private static MtkTvPWDDialog mtkTvPWDDialog = null;

    private MtkTvPWDDialog() {
    }

    public static MtkTvPWDDialog getInstance() {
        if (mtkTvPWDDialog != null) {
            return mtkTvPWDDialog;
        }
        mtkTvPWDDialog = new MtkTvPWDDialog();
        return mtkTvPWDDialog;
    }
}
