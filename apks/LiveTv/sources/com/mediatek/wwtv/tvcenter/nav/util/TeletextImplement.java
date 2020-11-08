package com.mediatek.wwtv.tvcenter.nav.util;

import com.mediatek.twoworlds.tv.MtkTvKeyEvent;
import com.mediatek.twoworlds.tv.MtkTvTeletext;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextPageBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopBlockBase;
import java.util.ArrayList;
import java.util.List;

public class TeletextImplement {
    private static final String TAG = "TeletextImplement";
    private static TeletextImplement mInstance;
    /* access modifiers changed from: private */
    public MtkTvTeletext mTeletext = MtkTvTeletext.getInstance();
    private MtkTvKeyEvent mtkKeyEvent = MtkTvKeyEvent.getInstance();

    public interface OnStopTTXCallback {
        void onStopTTX(int i);
    }

    private TeletextImplement() {
    }

    public static TeletextImplement getInstance() {
        if (mInstance == null) {
            mInstance = new TeletextImplement();
        }
        return mInstance;
    }

    public int startTTX(int keycode) {
        return this.mtkKeyEvent.sendKeyClick(this.mtkKeyEvent.androidKeyToDFBkey(keycode));
    }

    public void stopTTX(final OnStopTTXCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                int resultCode = TeletextImplement.this.mTeletext.stop();
                if (callback != null) {
                    callback.onStopTTX(resultCode);
                }
            }
        }).start();
    }

    public void stopTTX() {
        stopTTX((OnStopTTXCallback) null);
    }

    public boolean hasTopInfo() {
        return this.mTeletext.teletextHasTopInfo();
    }

    public MtkTvTeletextPageBase getCurrentTeletextPage() {
        return this.mTeletext.getCurrentTeletextPage();
    }

    public int setTeletextPage(MtkTvTeletextPageBase teletextPageAddr) {
        return this.mTeletext.setTeletextPage(teletextPageAddr);
    }

    public List<TeletextTopItem> getTopList() {
        List<TeletextTopItem> blockList = new ArrayList<>();
        List<MtkTvTeletextTopBlockBase> tmpBlockList = this.mTeletext.getTeletextTopBlockList();
        if (tmpBlockList != null && tmpBlockList.size() > 0) {
            for (MtkTvTeletextTopBlockBase block : tmpBlockList) {
                blockList.add(new TeletextTopItem(block));
            }
        }
        return blockList;
    }
}
