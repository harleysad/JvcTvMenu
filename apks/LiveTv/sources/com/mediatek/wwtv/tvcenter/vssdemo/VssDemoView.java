package com.mediatek.wwtv.tvcenter.vssdemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

/* compiled from: VssDemoActivity */
class VssDemoView extends GLSurfaceView {
    public boolean bOnVssDemo = false;
    public GLRenderer mRenderer;

    public VssDemoView(Context context) {
        super(context);
        this.mRenderer = new GLRenderer(context);
        setRenderer(this.mRenderer);
    }

    public void onDraw() {
        this.mRenderer.onDraw();
        this.bOnVssDemo = true;
        Log.i("VssDemo", "set the bOnVssDemo to true");
    }
}
