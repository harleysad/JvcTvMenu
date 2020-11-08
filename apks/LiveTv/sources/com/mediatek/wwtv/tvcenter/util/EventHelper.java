package com.mediatek.wwtv.tvcenter.util;

import android.content.Intent;
import android.util.Log;

public class EventHelper {
    public static final int INTENT_FLAG_LUNCHER_APP = 1;
    public static final int INTENT_FLAG_OTHER_APP = 2;
    public static final int INTENT_SRC_DEFAULT = 4;
    public static final int INTENT_SRC_LIVE_TV = 8;
    public static final int INTENT_SRC_TV_SETTING_PLUS = 16;
    public static final int INTENT_SUB_TYPE_3RD_CAPTIONS_SRC = 32768;
    public static final int INTENT_SUB_TYPE_AUDIO_SRC = 524288;
    public static final int INTENT_SUB_TYPE_CAPTIONS_SRC = 64;
    public static final int INTENT_SUB_TYPE_CHANNEL_SRC = 32;
    public static final int INTENT_SUB_TYPE_DISPLAY_MODE_SRC = 128;
    public static final int INTENT_SUB_TYPE_MULTI_AUDIO_SRC = 256;
    public static final int INTENT_SUB_TYPE_NRT_SRC = 131072;
    public static final int INTENT_SUB_TYPE_PICTURE_STYLE_SRC = 1024;
    public static final int INTENT_SUB_TYPE_POWER_SRC = 512;
    public static final int INTENT_SUB_TYPE_SLEEP_SRC = 65536;
    public static final int INTENT_SUB_TYPE_SOUND_STYLE_SRC = 16384;
    public static final int INTENT_SUB_TYPE_SPEAKER_SRC = 2048;
    public static final int INTENT_SUB_TYPE_SUBTITLE_SRC = 1048576;
    public static final int INTENT_SUB_TYPE_TSHIFT_SRC = 262144;
    public static final int INTENT_TYPE_PICTURE_SRC = 4096;
    public static final int INTENT_TYPE_SOUND_SRC = 8192;
    public static final String MTK_EVENT_EXTRA_FLAG = "FLAG";
    public static final String MTK_EVENT_EXTRA_FLAG_OTHER_APP = "OTHER_APP";
    public static final String MTK_EVENT_EXTRA_RECORD_TSHIFT_SRC = "TSHIFT_SRC";
    public static final String MTK_EVENT_EXTRA_SRC = "SOURCE";
    public static final String MTK_EVENT_EXTRA_SRC_LIVE_TV = "LIVE_TV";
    public static final String MTK_EVENT_EXTRA_SRC_TV_SETTING_PLUS = "TV_SETTING_PLUS";
    public static final String MTK_EVENT_EXTRA_SUB_SOUND_STYLE_SRC = "SOUND_STYLE_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE = "SUB_TYPE";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_3RD_CAPTION_SRC = "3RD_CAPTIONS_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_CAPTIONS_SRC = "CAPTIONS_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_CHANNEL_SRC = "CHANNEL_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_DISPLAY_MODE_SRC = "DISPLAY_MODE_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_MULTI_AUDIO_SRC = "MULTI_AUDIO_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_PICTURE_STYLE_SRC = "PICTURE_STYLE_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_POWER_SRC = "POWER_SRC";
    public static final String MTK_EVENT_EXTRA_SUB_TYPE_SPEAKER_SRC = "SPEAKER_SRC";
    public static final String MTK_EVENT_EXTRA_TYPE_AUDIO_SRC = "HBBTV_AUDIO_SRC";
    public static final String MTK_EVENT_EXTRA_TYPE_NRT_SRC = "NRT_SRC";
    public static final String MTK_EVENT_EXTRA_TYPE_PICTURE_SRC = "PICTURE_SRC";
    public static final String MTK_EVENT_EXTRA_TYPE_SLEEP_SRC = "SLEEP_TIME_SRC";
    public static final String MTK_EVENT_EXTRA_TYPE_SOUND_SRC = "SOUND_SRC";
    public static final String MTK_EVENT_EXTRA_TYPE_SUBTITLE_SRC = "HBBTV_SUBTITLE_SRC";
    private int mLoadType = 0;

    public int updateIntent(Intent intent) {
        this.mLoadType = 0;
        this.mLoadType |= getLauncherType(intent);
        this.mLoadType |= getLauncherSource(intent);
        this.mLoadType |= getLauncherSubType(intent);
        Log.e("EventHelper", "updateIntent, " + this.mLoadType);
        return this.mLoadType;
    }

    private static int getLauncherType(Intent intent) {
        String action;
        if (intent == null || (action = intent.getStringExtra(MTK_EVENT_EXTRA_FLAG)) == null || !action.equals(MTK_EVENT_EXTRA_FLAG_OTHER_APP)) {
            return 1;
        }
        return 2;
    }

    private static int getLauncherSource(Intent intent) {
        if (intent == null) {
            return 0;
        }
        String action = intent.getStringExtra(MTK_EVENT_EXTRA_SRC);
        if (action != null && action.equals(MTK_EVENT_EXTRA_SRC_LIVE_TV)) {
            return 8;
        }
        if (action == null || !action.equals(MTK_EVENT_EXTRA_SRC_TV_SETTING_PLUS)) {
            return 0;
        }
        return 16;
    }

    private static int getLauncherSubType(Intent intent) {
        if (intent != null) {
            String action = intent.getStringExtra(MTK_EVENT_EXTRA_SUB_TYPE);
            Log.e("EventHelper", "action,===" + action);
            if (action != null) {
                char c = 65535;
                switch (action.hashCode()) {
                    case -2064528852:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_3RD_CAPTION_SRC)) {
                            c = 10;
                            break;
                        }
                        break;
                    case -1866575768:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_CHANNEL_SRC)) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1526918458:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_SOUND_STYLE_SRC)) {
                            c = 9;
                            break;
                        }
                        break;
                    case -1410053131:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_PICTURE_STYLE_SRC)) {
                            c = 5;
                            break;
                        }
                        break;
                    case -1361078251:
                        if (action.equals(MTK_EVENT_EXTRA_TYPE_NRT_SRC)) {
                            c = 12;
                            break;
                        }
                        break;
                    case -661911702:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_POWER_SRC)) {
                            c = 3;
                            break;
                        }
                        break;
                    case -496099642:
                        if (action.equals(MTK_EVENT_EXTRA_TYPE_AUDIO_SRC)) {
                            c = 14;
                            break;
                        }
                        break;
                    case -308718236:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_SPEAKER_SRC)) {
                            c = 6;
                            break;
                        }
                        break;
                    case 58983059:
                        if (action.equals(MTK_EVENT_EXTRA_RECORD_TSHIFT_SRC)) {
                            c = 13;
                            break;
                        }
                        break;
                    case 682513941:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_MULTI_AUDIO_SRC)) {
                            c = 4;
                            break;
                        }
                        break;
                    case 736357426:
                        if (action.equals(MTK_EVENT_EXTRA_TYPE_SUBTITLE_SRC)) {
                            c = 15;
                            break;
                        }
                        break;
                    case 881638115:
                        if (action.equals(MTK_EVENT_EXTRA_TYPE_PICTURE_SRC)) {
                            c = 7;
                            break;
                        }
                        break;
                    case 975385204:
                        if (action.equals(MTK_EVENT_EXTRA_TYPE_SOUND_SRC)) {
                            c = 8;
                            break;
                        }
                        break;
                    case 1259443666:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_CAPTIONS_SRC)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1301554650:
                        if (action.equals(MTK_EVENT_EXTRA_TYPE_SLEEP_SRC)) {
                            c = 11;
                            break;
                        }
                        break;
                    case 1638906917:
                        if (action.equals(MTK_EVENT_EXTRA_SUB_TYPE_DISPLAY_MODE_SRC)) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        return 32;
                    case 1:
                        return 64;
                    case 2:
                        return 128;
                    case 3:
                        return 512;
                    case 4:
                        return 256;
                    case 5:
                        return 1024;
                    case 6:
                        return 2048;
                    case 7:
                        return 4096;
                    case 8:
                        return 8192;
                    case 9:
                        return 16384;
                    case 10:
                        return 32768;
                    case 11:
                        return 65536;
                    case 12:
                        return 131072;
                    case 13:
                        return 262144;
                    case 14:
                        return 524288;
                    case 15:
                        return 1048576;
                }
            }
        }
        return 0;
    }

    public boolean isEvent(int type) {
        return (this.mLoadType & type) != 0;
    }
}
