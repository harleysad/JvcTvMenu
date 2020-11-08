package com.mediatek.wwtv.tvcenter.vssdemo;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import com.mediatek.jni.GLJniLib;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    public static final int FRAMERATE_SAMPLEINTERVAL_MS = 1000;
    private float N = 10.0f;
    private int TextureHeight = 0;
    private int[] TextureId = new int[2];
    private int TextureWidth = 0;
    private float _fps = 0.0f;
    private long _frameCount = 0;
    private long _timeLastSample;
    private float angle = 0.0f;
    private boolean bFirstEnter = false;
    private boolean bOnshow = true;
    private int gWidht;
    private int gheight;
    private int iIdleCount = 30;
    private int iIdleCount_Timing = 50;
    private InitBuffer initbuf;
    Context mContext;
    private GL10 mGl;
    private CommonIntegration mNavIntegration = null;
    private float[][][] mPoints = ((float[][][]) Array.newInstance(float.class, new int[]{10, 10, 3}));
    public FloatBuffer mRectBuffer;
    public FloatBuffer mRectTexBuffer;
    private int mSceneId = 0;
    private int wiggle_count = 0;
    private float xrot = 0.0f;
    private float yrot = 0.0f;
    public float zValue = 0.0f;
    private float zrot = 0.0f;

    public GLRenderer(Context context) {
        this.mContext = context;
    }

    private void doFps() {
        this._frameCount++;
        long now = SystemClock.uptimeMillis();
        long delta = now - this._timeLastSample;
        if (delta >= 1000) {
            this._fps = ((float) this._frameCount) / (((float) delta) / 1000.0f);
            if (SystemProperties.getInt("mtk.vss.demo.fps", 0) == 1) {
                Log.e("VssDebug", "the current fps is :" + this._fps);
            }
            this._timeLastSample = now;
            this._frameCount = 0;
        }
    }

    public boolean isCurrentSourceHasSignal() {
        this.mNavIntegration = CommonIntegration.getInstance();
        return this.mNavIntegration.isCurrentSourceHasSignal();
    }

    public void DrawTexture(GL10 gl, int type) {
        GL10 gl10 = gl;
        GLES10.glClear(16640);
        GLES10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES10.glMatrixMode(5889);
        GLES10.glLoadIdentity();
        if (1 == type) {
            GLU.gluPerspective(gl10, 45.0f, ((float) this.gWidht) / ((float) this.gheight), 0.1f, 5000.0f);
            GLU.gluLookAt(gl10, 0.0f, 0.0f, this.zValue + 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        } else {
            GLU.gluPerspective(gl10, 0.0f, ((float) this.gWidht) / ((float) this.gheight), 0.0f, 0.0f);
            GLU.gluLookAt(gl10, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        GLES10.glMatrixMode(5888);
        GLES10.glLoadIdentity();
        GLES10.glEnable(3553);
        GLES10.glBindTexture(3553, this.TextureId[0]);
        GLES10.glEnableClientState(32884);
        GLES10.glEnableClientState(32888);
        if (this.mSceneId == 1) {
            GLES10.glVertexPointer(3, 5126, 0, this.initbuf.mRectBuffer);
            GLES10.glTexCoordPointer(2, 5126, 0, this.initbuf.mRectTexBuffer);
            GLES10.glEnableClientState(32884);
            GLES10.glEnableClientState(32888);
            GLES10.glRotatef(this.angle, 0.0f, 1.0f, 0.0f);
            this.angle += 1.0f;
            GLES10.glDrawArrays(6, 0, 4);
        } else if (this.mSceneId == 0) {
            this.xrot += 0.3f;
            this.yrot += 0.2f;
            this.zrot += 0.4f;
            GLES10.glRotatef(this.xrot, 1.0f, 0.0f, 0.0f);
            GLES10.glRotatef(this.yrot, 0.0f, 1.0f, 0.0f);
            GLES10.glRotatef(this.zrot, 0.0f, 0.0f, 1.0f);
            GLES10.glScalef(0.2f, 0.2f, 0.2f);
            anim_flag();
            draw_flag();
        }
        GLES10.glDisableClientState(32884);
        GLES10.glDisableClientState(32888);
    }

    public void onDrawFrame(GL10 gl) {
        if (this.bOnshow) {
            int tempH = GLJniLib.nativeGetHeight();
            int tempW = GLJniLib.nativeGetWidth();
            gl.glGetError();
            gl.glDeleteTextures(2, this.TextureId, 0);
            if (isCurrentSourceHasSignal()) {
                GLJniLib.nativeUpdateTexture(this.TextureId[0]);
                if (SystemProperties.getInt("mtk.vss.demo.debug", 0) == 1) {
                    Log.i("VssDebug", "height is : " + tempH + " width is :" + tempW);
                }
            } else {
                Log.e("VssDebug", "Current Input Source has no signal");
            }
            DrawTexture(gl, 1);
            doFps();
        }
    }

    public void onDraw() {
        Log.e("VssDebug", "Starting .... onDraw");
        this.bOnshow = true;
        this.TextureHeight = 0;
        this.TextureWidth = 0;
        this.bFirstEnter = true;
    }

    public void onPause() {
        Log.e("VssDebug", "Ending .... onDraw");
        this.bOnshow = false;
        this.mGl.glGetError();
        this.mGl.glDeleteTextures(2, this.TextureId, 0);
        this.bFirstEnter = false;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("VssDebug", "onSurfaceChanged width = " + width + "height = " + height);
        gl.glViewport(0, 0, width, height);
        float ratio = ((float) width) / ((float) height);
        gl.glMatrixMode(5889);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1.0f, 1.0f, 1.0f, 100.0f);
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
        this.gWidht = width;
        this.gheight = height;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.mGl = gl;
        Log.e("VssDebug", "onSurfaceCreated");
        GLES10.glGenTextures(2, this.TextureId, 0);
        GLJniLib.nativeInit();
        this.initbuf = new InitBuffer();
        GLES10.glBindTexture(3553, this.TextureId[0]);
        GLES10.glTexParameterf(3553, 10241, 9728.0f);
        GLES10.glTexParameterf(3553, 10240, 9729.0f);
        GLES10.glTexParameterf(3553, 10242, 33071.0f);
        GLES10.glTexParameterf(3553, 10243, 33071.0f);
        init_flag();
        this.TextureWidth = GLJniLib.nativeGetWidth();
        this.TextureHeight = GLJniLib.nativeGetHeight();
    }

    /* access modifiers changed from: package-private */
    public int init_flag() {
        float a = (5.0f * this.N) / 45.0f;
        float b = (4.5f * this.N) / 45.0f;
        for (int x = 0; ((float) x) < this.N; x++) {
            for (int y = 0; ((float) y) < this.N; y++) {
                this.mPoints[x][y][0] = (((float) x) / a) - b;
                this.mPoints[x][y][1] = (((float) y) / a) - b;
                this.mPoints[x][y][2] = (float) Math.sin((double) ((((((float) x) / a) * 40.0f) / 360.0f) * 3.1415927f * 2.0f));
            }
        }
        ByteBuffer rvbb = ByteBuffer.allocateDirect(4800);
        rvbb.order(ByteOrder.nativeOrder());
        this.mRectBuffer = rvbb.asFloatBuffer();
        this.mRectBuffer.position(0);
        ByteBuffer tvbb = ByteBuffer.allocateDirect(3200);
        tvbb.order(ByteOrder.nativeOrder());
        this.mRectTexBuffer = tvbb.asFloatBuffer();
        this.mRectTexBuffer.position(0);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int anim_flag() {
        this.wiggle_count++;
        float a = (5.0f * this.N) / 45.0f;
        float f = (4.5f * this.N) / 45.0f;
        for (int x = 0; ((float) x) < this.N; x++) {
            for (int y = 0; ((float) y) < this.N; y++) {
                this.mPoints[x][y][2] = (float) Math.sin((double) (((((((float) x) / a) * 40.0f) / 360.0f) * 3.1415927f * 2.0f) + (((float) this.wiggle_count) * 0.1f)));
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int draw_flag() {
        int x = 0;
        while (true) {
            if (((float) x) >= this.N - 1.0f) {
                return 0;
            }
            int y = 0;
            for (float f = 1.0f; ((float) y) < this.N - f; f = 1.0f) {
                float f_x = ((float) x) / (this.N - f);
                float f_y = ((float) y) / (this.N - f);
                float f_xb = ((float) (x + 1)) / (this.N - f);
                float f_yb = ((float) (y + 1)) / (this.N - f);
                float[] vertices = {this.mPoints[x][y][0], this.mPoints[x][y][1], this.mPoints[x][y][2], this.mPoints[x][y + 1][0], this.mPoints[x][y + 1][1], this.mPoints[x][y + 1][2], this.mPoints[x + 1][y][0], this.mPoints[x + 1][y][1], this.mPoints[x + 1][y][2], this.mPoints[x + 1][y + 1][0], this.mPoints[x + 1][y + 1][1], this.mPoints[x + 1][y + 1][2]};
                this.mRectBuffer.clear();
                this.mRectBuffer.put(vertices);
                this.mRectBuffer.rewind();
                this.mRectTexBuffer.clear();
                this.mRectTexBuffer.put(new float[]{f_x, f_y, f_x, f_yb, f_xb, f_y, f_xb, f_yb});
                this.mRectTexBuffer.rewind();
                GLES10.glVertexPointer(3, 5126, 0, this.mRectBuffer);
                GLES10.glTexCoordPointer(2, 5126, 0, this.mRectTexBuffer);
                GLES10.glDrawArrays(5, 0, 4);
                y++;
            }
            x++;
        }
    }

    public void SetNextState(int Id) {
        this.mSceneId = Id;
    }

    class InitBuffer {
        public FloatBuffer mRectBuffer;
        public FloatBuffer mRectTexBuffer;

        public InitBuffer() {
            float[] rectangle = {-1.4f, -1.4f, 0.0f, 1.4f, -1.4f, 0.0f, 1.4f, 1.4f, 0.0f, -1.4f, 1.4f, 0.0f};
            float[] rectTexcoords = {0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
            ByteBuffer rvbb = ByteBuffer.allocateDirect(rectangle.length * 4);
            rvbb.order(ByteOrder.nativeOrder());
            this.mRectBuffer = rvbb.asFloatBuffer();
            this.mRectBuffer.put(rectangle);
            this.mRectBuffer.position(0);
            ByteBuffer tvbb = ByteBuffer.allocateDirect(rectTexcoords.length * 4);
            tvbb.order(ByteOrder.nativeOrder());
            this.mRectTexBuffer = tvbb.asFloatBuffer();
            this.mRectTexBuffer.put(rectTexcoords);
            this.mRectTexBuffer.position(0);
        }
    }

    public boolean setKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            Log.e("log", "event.getAction() == KeyEvent.ACTION_DOWN");
            switch (event.getKeyCode()) {
                case 7:
                    SetNextState(0);
                    return true;
                case 8:
                    SetNextState(1);
                    return true;
                case 19:
                    this.zValue -= 0.1f;
                    return true;
                case 20:
                    this.zValue += 0.1f;
                    return true;
            }
        }
        return false;
    }
}
