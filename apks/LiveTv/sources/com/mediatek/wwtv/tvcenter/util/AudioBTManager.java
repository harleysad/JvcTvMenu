package com.mediatek.wwtv.tvcenter.util;

import android.content.Context;
import android.util.Log;
import com.mediatek.mtkaudiopatchmanager.MtkAudioPatchManager;

public class AudioBTManager {
    private static final String TAG = "AudioBTManager";
    private static AudioBTManager mInstance;
    private Context mContext;
    private MtkAudioPatchManager mMtkAudioPatchManager = new MtkAudioPatchManager(this.mContext);

    private AudioBTManager(Context context) {
        this.mContext = context;
    }

    public static AudioBTManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AudioBTManager(context);
        }
        return mInstance;
    }

    public boolean creatAudioPatch() {
        boolean reslut = this.mMtkAudioPatchManager.createAudioPatch();
        Log.i(TAG, "createAudioPatch reslut = " + reslut);
        return reslut;
    }

    public boolean releaseAudioPatch() {
        boolean reslut = this.mMtkAudioPatchManager.releaseAudioPatch();
        Log.i(TAG, "releaseAudioPatch reslut = " + reslut);
        this.mMtkAudioPatchManager = null;
        this.mContext = null;
        mInstance = null;
        return reslut;
    }
}
