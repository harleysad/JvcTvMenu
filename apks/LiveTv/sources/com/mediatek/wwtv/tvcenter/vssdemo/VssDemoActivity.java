package com.mediatek.wwtv.tvcenter.vssdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.mediatek.jni.GLJniLib;

public class VssDemoActivity extends Activity {
    private final int STOP = 1000;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                VssDemoActivity.this.stoped = true;
                GLJniLib.nativeUnInit();
                VssDemoActivity.this.finish();
            }
        }
    };
    public VssDemoView mVssDemoView;
    boolean stoped = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mVssDemoView = new VssDemoView(this);
        setContentView(this.mVssDemoView);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mVssDemoView.bOnVssDemo) {
            this.mVssDemoView.mRenderer.onPause();
            this.mVssDemoView.bOnVssDemo = false;
            Log.i("VssDemo", "mRenderer.onPause()....");
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mVssDemoView.onDraw();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (!this.stoped) {
            GLJniLib.nativeUnInit();
        }
        Log.i("VssDemo", "mRenderer.onStop()....");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((4 != event.getKeyCode() && 184 != event.getKeyCode()) || event.getAction() != 0) {
            return this.mVssDemoView.mRenderer.setKeyEvent(event);
        }
        if (this.mVssDemoView.bOnVssDemo) {
            this.mVssDemoView.mRenderer.onPause();
            this.mVssDemoView.bOnVssDemo = false;
            this.mHandler.sendEmptyMessageDelayed(1000, 100);
            return true;
        }
        Log.i("VssDemo", "Do nothing for Keycode_MTKIR_Green Key...");
        return false;
    }
}
