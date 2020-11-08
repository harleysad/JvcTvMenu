package com.mediatek.wwtv.tvcenter.capturelogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import com.mediatek.twoworlds.tv.MtkTvUtil;

public class TVStorage {
    public static int CAP_LOGO_MAX = (CAP_LOGO_MM_IMAGE + 1);
    public static int CAP_LOGO_MM_IMAGE = 2;
    public static int CAP_LOGO_MM_VIDEO = 1;
    public static int CAP_LOGO_TV = 0;
    private static final String TAG = "TVStorage";
    private static final String TVAP_TABLE = "tvap_storage_table";
    public static final int capture_save_index_0 = 0;
    public static final int capture_save_index_1 = 1;
    public static final int capture_save_index_default = 256;
    public static final int capture_save_index_none = 255;
    private static TVStorage storage;
    private final SharedPreferences.Editor editor;
    private Handler handler;
    private Context mContext;
    private final SharedPreferences pref;
    private byte[] ps_path = null;

    public interface LogoCaptureListener {
        public static final int CAP_CANCLE = 2;
        public static final int CAP_COMPLETE = 0;
        public static final int CAP_FAIL = 1;

        void onEvent(int i);
    }

    private TVStorage(Context context) {
        this.mContext = context;
        this.pref = context.getSharedPreferences(TVAP_TABLE, 0);
        this.editor = this.pref.edit();
        this.handler = new Handler(context.getMainLooper(), new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                TVStorage.this.handleMessage(msg.what, msg.obj);
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void handleMessage(int msgCode, Object obj) {
    }

    public static TVStorage getInstance(Context context) {
        if (storage == null) {
            storage = new TVStorage(context);
        }
        return storage;
    }

    public SharedPreferences getShareFerences() {
        return this.pref;
    }

    public SharedPreferences.Editor getEditor() {
        return this.editor;
    }

    public void set(String k, String v) {
        getEditor().putString(k, v);
        getEditor().commit();
        flushMedia();
    }

    public String get(String key) {
        return this.pref.getString(key, (String) null);
    }

    public String get(String key, String defValue) {
        return this.pref.getString(key, defValue);
    }

    public void clean() {
        getEditor().clear();
        getEditor().commit();
        flushMedia();
    }

    private void captureCmLogo(int source, Rect rect, int logoId, LogoCaptureListener listener, byte videoPath, int skBitmap, int bufferWidth, int bufferHeight, int bufferPitch, int colorMode) {
    }

    public void captureLogo(int source, Rect rect, int logoId, LogoCaptureListener listener) {
        captureCmLogo(source, rect, logoId, listener, (byte) 0, 0, 0, 0, 0, 0);
    }

    public void captureLogo(int source, Rect rect, int logoId, LogoCaptureListener listener, int skBitmap, int bufferWidth, int bufferHeight, int bufferPitch, int colorMode) {
        captureCmLogo(source, rect, logoId, listener, (byte) 0, skBitmap, bufferWidth, bufferHeight, bufferPitch, colorMode);
    }

    public void cancleCaptureLogo(int source) {
    }

    public void finishCaptureLogo(int source) {
    }

    public void setBootLogo(int source, int id) {
    }

    public void captureTVPic(int source, Rect rect, int logoId, LogoCaptureListener listener, String path) {
        captureCmTVPic(source, rect, logoId, listener, (byte) 0, 0, 0, 0, 0, 0, path);
    }

    private void captureCmTVPic(int source, Rect rect, int logoId, LogoCaptureListener listener, byte videoPath, int skBitmap, int bufferWidth, int bufferHeight, int bufferPitch, int colorMode, String path) {
    }

    public boolean isCaptureLogo() {
        return MtkTvUtil.getInstance().isCaptureLogo();
    }

    public void flushMedia() {
        try {
            Runtime.getRuntime().exec("sync");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
