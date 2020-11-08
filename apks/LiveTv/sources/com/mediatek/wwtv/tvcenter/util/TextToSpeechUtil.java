package com.mediatek.wwtv.tvcenter.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import java.util.List;

public class TextToSpeechUtil {
    public static final int QUEUE_ADD = 1;
    public static final int QUEUE_FLUSH = 0;
    private static final String TAG = "TextToSpeechUtil";
    public static final String TALKBACK_SERVICE = "com.google.android.marvin.talkback/.TalkBackService";
    private static TextToSpeech mTts = null;
    private final TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            TextToSpeechUtil.this.onInitEngine(status);
        }
    };
    private boolean mTalkBackEnabled;
    private Bundle mTtsBundle = null;
    private Context myContext = null;

    public TextToSpeechUtil(Context context) {
        if (context != null) {
            this.myContext = context;
        } else {
            Log.d(TAG, "context is null!!!");
        }
    }

    private TextToSpeech getTextToSpeech() {
        if (mTts == null) {
            mTts = new TextToSpeech(this.myContext, this.mInitListener);
            Log.d(TAG, "new TextToSpeech created!!!");
            mTts.setAudioAttributes(new AudioAttributes.Builder().setContentType(1).setUsage(11).build());
            this.mTtsBundle = new Bundle();
            this.mTtsBundle.putInt("streamType", 10);
        }
        return mTts;
    }

    /* access modifiers changed from: private */
    public void onInitEngine(int status) {
        if (status == 0) {
            Log.d(TAG, "TTS engine for settings screen initialized.");
            Log.d(TAG, "Updating engine: Successfully bound to the engine: " + getTextToSpeech().getCurrentEngine());
            return;
        }
        Log.d(TAG, "TTS engine for settings screen failed to initialize successfully.");
    }

    public boolean isTTSEnabled() {
        return isTTSEnabled(this.myContext);
    }

    public static boolean isTTSEnabled(Context context) {
        List<AccessibilityServiceInfo> enableServices = ((AccessibilityManager) context.getSystemService(PartnerSettingsConfig.ATTR_DEVICE_ACCESSIBILITY)).getEnabledAccessibilityServiceList(-1);
        for (int i = 0; i < enableServices.size(); i++) {
            if (enableServices.get(i).getId().contains("com.google.android.marvin.talkback/.TalkBackService")) {
                return true;
            }
        }
        return false;
    }

    public int speak(String text) {
        if (!isTTSEnabled()) {
            Log.d(TAG, "isTTSEnabled is false!!!");
            return -1;
        } else if (getTextToSpeech() == null) {
            Log.d(TAG, "mTts is NULL in speak!!!");
            return -1;
        } else {
            Log.d(TAG, "mTts.getLanguage()=" + mTts.getLanguage());
            Log.d(TAG, "mTts.isLanguageAvailable(mTts.getLanguage())=" + mTts.isLanguageAvailable(mTts.getLanguage()));
            if (mTts.isLanguageAvailable(mTts.getLanguage()) >= 0) {
                Log.d(TAG, "mTts.speak " + text);
                return mTts.speak(text, 0, this.mTtsBundle, "LiveTv");
            }
            Log.d(TAG, "mTts.isLanguageAvailable false");
            return -1;
        }
    }

    public int speak(String text, int queueMode) {
        if (!isTTSEnabled()) {
            Log.d(TAG, "isTTSEnabled is false!!!");
            return -1;
        } else if (getTextToSpeech() == null) {
            Log.d(TAG, "mTts is NULL in speak!!!");
            return -1;
        } else {
            Log.d(TAG, "mTts.getLanguage()=" + mTts.getLanguage());
            Log.d(TAG, "mTts.isLanguageAvailable(mTts.getLanguage())=" + mTts.isLanguageAvailable(mTts.getLanguage()));
            if (mTts.isLanguageAvailable(mTts.getLanguage()) >= 0) {
                Log.d(TAG, "mTts.speak " + text);
                return mTts.speak(text, queueMode, this.mTtsBundle, "LiveTv");
            }
            Log.d(TAG, "mTts.isLanguageAvailable false");
            return -1;
        }
    }

    public void shutdown() {
        if (getTextToSpeech() == null) {
            Log.d(TAG, "mTts is NULL in shutdown!!!");
            return;
        }
        try {
            mTts.shutdown();
            mTts = null;
            Log.d(TAG, "TextToSpeech shutdown now!!!");
        } catch (Exception e) {
            Log.e(TAG, "Error shutting down TTS engine" + e);
        }
    }
}
