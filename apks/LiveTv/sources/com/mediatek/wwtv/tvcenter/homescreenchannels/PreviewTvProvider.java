package com.mediatek.wwtv.tvcenter.homescreenchannels;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.DrawableRes;
import android.support.annotation.WorkerThread;
import android.support.media.tv.Channel;
import android.support.media.tv.ChannelLogoUtils;
import android.support.media.tv.PreviewProgram;
import android.support.media.tv.TvContractCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class PreviewTvProvider {
    private static final String APPS_LAUNCH_HOST = "com.mediatek.wwtv.tvcenter";
    static final int CHANNELS_COLUMN_BROWSABLE_INDEX = 2;
    static final int CHANNELS_COLUMN_ID_INDEX = 0;
    static final int CHANNELS_COLUMN_INTERNAL_PROVIDER_ID_INDEX = 1;
    static final String[] CHANNELS_MAP_PROJECTION = {"_id", "internal_provider_id", "browsable"};
    private static final int COLUMN_WATCH_NEXT_COLUMN_BROWSABLE_INDEX = 2;
    private static final int COLUMN_WATCH_NEXT_ID_INDEX = 0;
    private static final int COLUMN_WATCH_NEXT_INTERNAL_PROVIDER_ID_INDEX = 1;
    private static final String PLAY_VIDEO_ACTION_PATH = "playvideo";
    private static final Uri PREVIEW_PROGRAMS_CONTENT_URI = TvContractCompat.PreviewPrograms.CONTENT_URI;
    static final int PROGRAMS_COLUMN_ID_INDEX = 0;
    static final int PROGRAMS_COLUMN_INTERNAL_INTERACTION_COUNT_INDEX = 4;
    static final int PROGRAMS_COLUMN_INTERNAL_INTERACTION_TYPE_INDEX = 3;
    static final int PROGRAMS_COLUMN_INTERNAL_PROVIDER_ID_INDEX = 1;
    static final int PROGRAMS_COLUMN_TITLE_INDEX = 2;
    static final String[] PROGRAMS_MAP_PROJECTION = {"_id", "internal_provider_id", "title", TvContractCompat.PreviewProgramColumns.COLUMN_INTERACTION_TYPE, TvContractCompat.PreviewProgramColumns.COLUMN_INTERACTION_COUNT};
    private static final String SCHEME = "tvrecommendations";
    private static final String START_APP_ACTION_PATH = "apps/action";
    private static final String START_APP_LAUNCH_PATH = "apps/launch";
    private static final String TAG = "PreviewTvProvider";
    private static final String[] WATCH_NEXT_MAP_PROJECTION = {"_id", "internal_provider_id", "browsable"};

    private PreviewTvProvider() {
    }

    private static String createInputId(Context context) {
        return TvContractCompat.buildInputId(new ComponentName(context, TurnkeyUiMainActivity.class.getName()));
    }

    private static void postNotification(Context context, Clip clip) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage(APPS_LAUNCH_HOST);
        intent.setData(TvContractCompat.buildChannelUri(clip.getProgramId()));
        ((NotificationManager) context.getSystemService("notification")).notifyAsUser((String) null, (int) clip.getClipId(), new Notification.Builder(context).setSmallIcon(R.drawable.icon).setWhen(0).extend(new Notification.TvExtender()).setCategory("mediatek").setGroup("mediatek").setContentIntent(PendingIntent.getActivityAsUser(context, 0, intent, 134217728, (Bundle) null, UserHandle.CURRENT)).setContentTitle(APPS_LAUNCH_HOST).setContentText(String.valueOf(clip.getClipId())).build(), UserHandle.CURRENT);
    }

    @WorkerThread
    private static void writeChannelLogo(Context context, long channelId, @DrawableRes int drawableId) {
        ChannelLogoUtils.storeChannelLogo(context, channelId, BitmapFactory.decodeResource(context.getResources(), drawableId));
    }

    @WorkerThread
    static boolean isChannelExist(Context context, Playlist playlist) {
        Cursor cursor = context.getContentResolver().query(TvContractCompat.Channels.CONTENT_URI, (String[]) null, "input_id = ? and display_name = ?", new String[]{createInputId(context), playlist.getName()}, (String) null);
        if (cursor == null) {
            return false;
        }
        if (!cursor.moveToNext()) {
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
        playlist.setChannelPublishedId(cursor.getLong(cursor.getColumnIndex("_id")));
        if (cursor != null) {
            cursor.close();
        }
        return true;
    }

    static boolean isPreviewContentExist(Context context, Clip clip) {
        boolean exist = false;
        Cursor cursor = context.getContentResolver().query(TvContractCompat.Channels.CONTENT_URI, (String[]) null, "_id=?", new String[]{String.valueOf(clip.getProgramId())}, (String) null);
        if (cursor != null && true == cursor.moveToNext()) {
            exist = true;
        }
        if (cursor != null) {
            cursor.close();
        }
        if (exist) {
            Cursor cursor2 = context.getContentResolver().query(PREVIEW_PROGRAMS_CONTENT_URI, (String[]) null, "preview_video_uri=? and intent_uri = ?", new String[]{clip.getPreviewVideoUrl(), clip.getVideoUrl()}, (String) null);
            if (cursor2 == null || true != cursor2.moveToNext()) {
                Clip clip2 = clip;
            } else {
                clip.setClipId(cursor2.getLong(cursor2.getColumnIndex("_id")));
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        } else {
            Clip clip3 = clip;
            Object obj = "_id=?";
        }
        return exist;
    }

    @WorkerThread
    static long addChannel(Context context, Playlist playlist) {
        long channelId;
        List<Clip> clips;
        Channel channel;
        boolean z;
        Context context2 = context;
        Uri channelUri = null;
        String channelInputId = createInputId(context);
        Channel channel2 = new Channel.Builder().setDisplayName(playlist.getName()).setDescription(playlist.getDescription()).setType(TvContractCompat.Channels.TYPE_PREVIEW).setInputId(channelInputId).setAppLinkIntentUri(Uri.parse("tvrecommendations://apps/launch/com.mediatek.wwtv.tvcenter")).setInternalProviderId(playlist.getPlaylistId()).build();
        if (!isChannelExist(context, playlist)) {
            channelUri = context.getContentResolver().insert(TvContractCompat.Channels.CONTENT_URI, channel2.toContentValues());
            if (channelUri == null || channelUri.equals(Uri.EMPTY)) {
                Playlist playlist2 = playlist;
                Log.e(TAG, "Insert channel failed");
                return 0;
            }
            channelId = ContentUris.parseId(channelUri);
            ContentValues values = new ContentValues();
            values.put("browsable", 1);
            context.getContentResolver().update(TvContractCompat.Channels.CONTENT_URI, values, "_id = ?", new String[]{Long.toString(channelId)});
            playlist.setChannelPublishedId(channelId);
        } else {
            Playlist playlist3 = playlist;
            channelId = playlist.getChannelId();
        }
        writeChannelLogo(context2, channelId, playlist.getLogoId());
        List<Clip> clips2 = playlist.getClips();
        int weight = clips2.size();
        int i = 0;
        while (i < clips2.size()) {
            Clip clip = clips2.get(i);
            if (!isPreviewContentExist(context2, clip)) {
                Uri programUri = context.getContentResolver().insert(PREVIEW_PROGRAMS_CONTENT_URI, ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) new PreviewProgram.Builder().setChannelId(channelId).setTitle(clip.getTitle())).setDescription(clip.getDescription())).setPosterArtUri(Uri.parse(clip.getCardImageUrl()))).setPreviewVideoUri(Uri.parse(clip.getPreviewVideoUrl()))).setIntentUri(Uri.parse(clip.getVideoUrl()))).setContentId(clip.getContentId())).setWeight(weight).setPosterArtAspectRatio(clip.getAspectRatio())).setType(6)).setLive(true)).build().toContentValues());
                if (programUri == null || programUri.equals(Uri.EMPTY)) {
                    Log.e(TAG, "Insert program failed");
                    i++;
                    weight--;
                } else {
                    clip.setClipId(ContentUris.parseId(programUri));
                }
            }
            if (clip.getPreviewVideoUrl().equals(clip.getVideoUrl()) || clip.getVideoUrl().length() == 0) {
                postNotification(context2, clip);
                i++;
                weight--;
            } else {
                i++;
                weight--;
            }
        }
        StatusBarNotification[] notifications = ((NotificationManager) context2.getSystemService("notification")).getActiveNotifications();
        if (notifications == null) {
            Log.e(TAG, "empty notification.");
            return channelId;
        }
        int length = notifications.length;
        int i2 = 0;
        while (i2 < length) {
            StatusBarNotification notification = notifications[i2];
            CharSequence titleText = notification.getNotification().extras.getCharSequence(NotificationCompat.EXTRA_TITLE);
            CharSequence text = notification.getNotification().extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
            Uri channelUri2 = channelUri;
            StringBuilder sb = new StringBuilder();
            String channelInputId2 = channelInputId;
            sb.append("titleText: ");
            sb.append(titleText);
            sb.append(", ");
            sb.append(notification.getKey());
            sb.append(", ");
            sb.append(text);
            MtkLog.e(TAG, sb.toString());
            if (!APPS_LAUNCH_HOST.equals(titleText)) {
                Log.e(TAG, "titleText: " + titleText + ", " + notification.getKey() + ", " + text + " continue.");
                channel = channel2;
                clips = clips2;
                z = true;
            } else {
                ContentValues values2 = new ContentValues();
                values2.put("internal_provider_id", notification.getKey());
                StringBuilder sb2 = new StringBuilder();
                channel = channel2;
                sb2.append("tvrecommendations://apps/action/");
                sb2.append(notification.getKey());
                values2.put(TvContractCompat.PreviewProgramColumns.COLUMN_INTENT_URI, sb2.toString());
                clips = clips2;
                z = true;
                context.getContentResolver().update(PREVIEW_PROGRAMS_CONTENT_URI, values2, "_id = ?", new String[]{text.toString()});
            }
            i2++;
            boolean z2 = z;
            channelUri = channelUri2;
            channelInputId = channelInputId2;
            channel2 = channel;
            clips2 = clips;
            Context context3 = context;
        }
        String str = channelInputId;
        Channel channel3 = channel2;
        List<Clip> list = clips2;
        return channelId;
    }

    @WorkerThread
    static void deleteChannel(Context context, long channelId) {
        if (context.getContentResolver().delete(TvContractCompat.buildChannelUri(channelId), (String) null, (String[]) null) < 1) {
            Log.e(TAG, "Delete channel failed");
        }
    }

    @WorkerThread
    public static void deleteProgram(Context context, Clip clip) {
        deleteProgram(context, clip.getProgramId());
    }

    @WorkerThread
    static void deleteProgram(Context context, long programId) {
        if (context.getContentResolver().delete(TvContractCompat.buildPreviewProgramUri(programId), (String) null, (String[]) null) < 1) {
            Log.e(TAG, "Delete program failed");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0056, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005a, code lost:
        if (r2 != null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x005c, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005f, code lost:
        throw r4;
     */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void updateProgramClip(android.content.Context r10, com.mediatek.wwtv.tvcenter.homescreenchannels.Clip r11) {
        /*
            long r0 = r11.getProgramId()
            android.net.Uri r8 = android.support.media.tv.TvContractCompat.buildPreviewProgramUri(r0)
            android.content.ContentResolver r2 = r10.getContentResolver()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r3 = r8
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)
            r3 = 0
            boolean r4 = r2.moveToFirst()     // Catch:{ Throwable -> 0x0058 }
            if (r4 != 0) goto L_0x0023
            java.lang.String r4 = "PreviewTvProvider"
            java.lang.String r5 = "Update program failed"
            android.util.Log.e(r4, r5)     // Catch:{ Throwable -> 0x0058 }
        L_0x0023:
            android.support.media.tv.PreviewProgram r4 = android.support.media.tv.PreviewProgram.fromCursor(r2)     // Catch:{ Throwable -> 0x0058 }
            android.support.media.tv.PreviewProgram$Builder r5 = new android.support.media.tv.PreviewProgram$Builder     // Catch:{ Throwable -> 0x0058 }
            r5.<init>(r4)     // Catch:{ Throwable -> 0x0058 }
            java.lang.String r6 = r11.getTitle()     // Catch:{ Throwable -> 0x0058 }
            android.support.media.tv.BaseProgram$Builder r5 = r5.setTitle(r6)     // Catch:{ Throwable -> 0x0058 }
            android.support.media.tv.PreviewProgram$Builder r5 = (android.support.media.tv.PreviewProgram.Builder) r5     // Catch:{ Throwable -> 0x0058 }
            android.content.ContentResolver r6 = r10.getContentResolver()     // Catch:{ Throwable -> 0x0058 }
            android.support.media.tv.PreviewProgram r7 = r5.build()     // Catch:{ Throwable -> 0x0058 }
            android.content.ContentValues r7 = r7.toContentValues()     // Catch:{ Throwable -> 0x0058 }
            int r6 = r6.update(r8, r7, r3, r3)     // Catch:{ Throwable -> 0x0058 }
            r7 = 1
            if (r6 >= r7) goto L_0x0050
            java.lang.String r7 = "PreviewTvProvider"
            java.lang.String r9 = "Update program failed"
            android.util.Log.e(r7, r9)     // Catch:{ Throwable -> 0x0058 }
        L_0x0050:
            if (r2 == 0) goto L_0x0055
            $closeResource(r3, r2)
        L_0x0055:
            return
        L_0x0056:
            r4 = move-exception
            goto L_0x005a
        L_0x0058:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0056 }
        L_0x005a:
            if (r2 == 0) goto L_0x005f
            $closeResource(r3, r2)
        L_0x005f:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.homescreenchannels.PreviewTvProvider.updateProgramClip(android.content.Context, com.mediatek.wwtv.tvcenter.homescreenchannels.Clip):void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    static void publishProgram(Context context, Clip clip, long channelId, int weight) {
        String clipId = String.valueOf(clip.getClipId());
        Uri programUri = context.getContentResolver().insert(PREVIEW_PROGRAMS_CONTENT_URI, ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) ((PreviewProgram.Builder) new PreviewProgram.Builder().setChannelId(channelId).setTitle(clip.getTitle())).setDescription(clip.getDescription())).setPosterArtUri(Uri.parse(clip.getCardImageUrl()))).setIntentUri(Uri.parse("tvrecommendations://com.mediatek.wwtv.tvcenter/playvideo/" + clipId))).setPreviewVideoUri(Uri.parse(clip.getPreviewVideoUrl()))).setInternalProviderId(clipId)).setWeight(weight).setPosterArtAspectRatio(clip.getAspectRatio())).setType(0)).build().toContentValues());
        if (programUri == null || programUri.equals(Uri.EMPTY)) {
            Log.e(TAG, "Insert program failed");
        } else {
            clip.setProgramId(ContentUris.parseId(programUri));
        }
    }

    static String decodeVideoId(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() != 2 || !TextUtils.equals(paths.get(0), PLAY_VIDEO_ACTION_PATH)) {
            return new String();
        }
        return paths.get(1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0059, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005d, code lost:
        if (r0 != null) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x005f, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0062, code lost:
        throw r2;
     */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void setProgramViewCount(android.content.Context r8, long r9, int r11) {
        /*
            android.net.Uri r6 = android.support.media.tv.TvContractCompat.buildPreviewProgramUri(r9)
            android.content.ContentResolver r0 = r8.getContentResolver()
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r1 = r6
            android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5)
            r1 = 0
            boolean r2 = r0.moveToFirst()     // Catch:{ Throwable -> 0x005b }
            if (r2 != 0) goto L_0x001e
            if (r0 == 0) goto L_0x001d
            $closeResource(r1, r0)
        L_0x001d:
            return
        L_0x001e:
            android.support.media.tv.PreviewProgram r2 = android.support.media.tv.PreviewProgram.fromCursor(r0)     // Catch:{ Throwable -> 0x005b }
            android.support.media.tv.PreviewProgram$Builder r3 = new android.support.media.tv.PreviewProgram$Builder     // Catch:{ Throwable -> 0x005b }
            r3.<init>(r2)     // Catch:{ Throwable -> 0x005b }
            long r4 = (long) r11     // Catch:{ Throwable -> 0x005b }
            android.support.media.tv.BasePreviewProgram$Builder r3 = r3.setInteractionCount(r4)     // Catch:{ Throwable -> 0x005b }
            android.support.media.tv.PreviewProgram$Builder r3 = (android.support.media.tv.PreviewProgram.Builder) r3     // Catch:{ Throwable -> 0x005b }
            r4 = 0
            android.support.media.tv.BasePreviewProgram$Builder r3 = r3.setInteractionType(r4)     // Catch:{ Throwable -> 0x005b }
            android.support.media.tv.PreviewProgram$Builder r3 = (android.support.media.tv.PreviewProgram.Builder) r3     // Catch:{ Throwable -> 0x005b }
            android.content.ContentResolver r4 = r8.getContentResolver()     // Catch:{ Throwable -> 0x005b }
            android.net.Uri r5 = android.support.media.tv.TvContractCompat.buildPreviewProgramUri(r9)     // Catch:{ Throwable -> 0x005b }
            android.support.media.tv.PreviewProgram r7 = r3.build()     // Catch:{ Throwable -> 0x005b }
            android.content.ContentValues r7 = r7.toContentValues()     // Catch:{ Throwable -> 0x005b }
            int r4 = r4.update(r5, r7, r1, r1)     // Catch:{ Throwable -> 0x005b }
            r5 = 1
            if (r4 == r5) goto L_0x0053
            java.lang.String r5 = "PreviewTvProvider"
            java.lang.String r7 = "Update program failed"
            android.util.Log.e(r5, r7)     // Catch:{ Throwable -> 0x005b }
        L_0x0053:
            if (r0 == 0) goto L_0x0058
            $closeResource(r1, r0)
        L_0x0058:
            return
        L_0x0059:
            r2 = move-exception
            goto L_0x005d
        L_0x005b:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0059 }
        L_0x005d:
            if (r0 == 0) goto L_0x0062
            $closeResource(r1, r0)
        L_0x0062:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.homescreenchannels.PreviewTvProvider.setProgramViewCount(android.content.Context, long, int):void");
    }
}
