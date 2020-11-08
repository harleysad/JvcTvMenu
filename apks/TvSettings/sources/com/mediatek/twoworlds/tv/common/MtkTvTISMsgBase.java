package com.mediatek.twoworlds.tv.common;

import android.content.ContentUris;
import android.net.Uri;

public class MtkTvTISMsgBase {
    public static final String CMD_APP_PRIVATE_COMMAND_FOR_MUTE_AUDIO = "APP_PRIVATE_COMMAND_FOR_MUTE_AUDIO";
    public static final Uri FILTER_CHANNEL_URI = Uri.parse("content://filter/channel");
    public static final Uri MAIN_CONTENT_URI = Uri.parse("content://main");
    public static final String MSG_CHANNEL_IS_BARKER_CHANNEL = "mtk_channel_msgis_barker_channel";
    public static final String MSG_CHANNEL_IS_SILENT_CHANNEL = "mtk_channel_msgis_silent_channel";
    public static final String MSG_START_RECORDING_ERROR_EVENT = "START_RECORDING_ERROR_EVENT";
    public static final String MTK_TIS_MSG_CHANNEL = "mtk_channel_msg";
    public static final String MTK_TIS_MSG_RESET = "mtk_reset";
    public static final String MTK_TIS_SESSION_EVENT = "session_event_";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK = "session_event_dvr_playbackstatus";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_ADINFO_VALUE = "session_event_dvr_playback_adinfo_value";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_AUDIO_ONLY_SERVICE = "session_event_dvr_audio_only_service";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_COUNTRYINFO_VALUE = "session_event_dvr_playback_countryinfo_value";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_PIN_VALUE = "session_event_dvr_playback_pin_value";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETADCOUNTRYINFO = "session_event_dvr_playback_setadcountryinfo";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETADINFO = "session_event_dvr_playback_setadinfo";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETPIN = "session_event_dvr_playback_setpin";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SPEED_CHANGED_KEY = "PlaybackSpeedUpdate";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SPEED_SET_KEY = "PlaybackSpeedSet";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ERROR_UNKNOWN = "com.mediatek.tvinput.dtv.TunerInputService.DVR_ERROR_UNKNOWN";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_CORRUPT = "com.mediatek.tvinput.dtv.TunerInputService.DVR_FILE_CORRUPT";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_LOCKED = "com.mediatek.tvinput.dtv.TunerInputService.DVR_FILE_LOCKED";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_NOTSUPPORT = "com.mediatek.tvinput.dtv.TunerInputService.DVR_FILE_NOTSUPPORT";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_VIDEO_ENCODE_FORMAT_UNSUPPORT = "com.mediatek.tvinput.dtv.TunerInputService.DVR_FILE_VIDEO_ENCODE_FORMAT_UNSUPPORT";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY = "PlaybackStatusUpdate";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ON_REPLY_VALUE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_ON_REPLY";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PAUSE_VALUE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_PAUSE";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_COMPLETE_VALUE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_PLAY_COMPLETE";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_VALUE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_PLAY";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_REPLY_DONE_VALUE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_REPLY_DONE";
    public static final String MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_SEEK_COMPLETE_VALUE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_SEEK_COMPLETE";
    public static final String MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION = "session_event_dvr_record_duration";
    public static final String MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION_VALUE = "session_event_dvr_record_duration_value";
    public static final String MTK_TIS_SESSION_EVENT_RECORDING = "Recording_Session_event_recordingStart";
    public static final String MTK_TIS_SESSION_EVENT_RECORDING_CI_PLUS_ECP = "Recording_Session_event_recording_CI plus ECP";
    public static final String MTK_TIS_SESSION_EVENT_RECORDING_KEY = "RecordingStart";
    public static final String MTK_TIS_SESSION_EVENT_RECORDING_TUNED_KEY = "Recording_Session_event_Tuned_Key";
    public static final String MTK_TIS_SESSION_EVENT_RECORDING_TUNED_STATUS = "Recording_Session_event_Tuned_Status";
    public static final int MTK_TIS_SESSION_EVENT_RECORDING_TUNED_VALUE_FAILED = -1;
    public static final int MTK_TIS_SESSION_EVENT_RECORDING_TUNED_VALUE_SUCCESSED = 0;
    public static final int MTK_TIS_SESSION_EVENT_RECORDING_VALUE = 1;
    public static final String MTK_TIS_SESSION_EVENT_STOPPED_BY_PREEMPT = "session_event_stopped_by_preempt";
    public static final String MTK_TIS_SESSION_EVENT_TIMESHIFT_RECORD_NOT_STARTED = "TimeshiftRecordNotStarted";
    public static final String MTK_TIS_SESSION_EVENT_TIMESHIFT_RECORD_STARTED = "TimeshiftRecordStarted";
    public static final String MTK_TIS_SESSION_EVENT_VIDEO_DECODE_RESOURCE_CONFLICT = "session_event_video_decode_resource_conflict";
    public static final byte MTK_TIS_VALUE_FALSE = 0;
    public static final byte MTK_TIS_VALUE_TRUE = 1;
    public static final Uri SUB_CONTENT_URI = Uri.parse("content://sub");
    public static final Uri SVL_CONTENT_URI = Uri.parse("content://svl/channel");

    public static Uri createSvlChannelUri(long id) {
        return ContentUris.withAppendedId(SVL_CONTENT_URI, id);
    }

    public static boolean isSvlChannelUri(Uri channelUri) {
        if (channelUri == null) {
            return false;
        }
        return channelUri.toString().contains(SVL_CONTENT_URI.toString());
    }

    public static Uri createFilterChannelUri(long id) {
        return ContentUris.withAppendedId(FILTER_CHANNEL_URI, id);
    }

    public static boolean isFilterChannelUri(Uri channelUri) {
        if (channelUri == null) {
            return false;
        }
        return channelUri.toString().contains(FILTER_CHANNEL_URI.toString());
    }

    public static long parseSvlChannelId(Uri channelUri) {
        return ContentUris.parseId(channelUri);
    }
}
