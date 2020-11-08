package com.mediatek.wwtv.tvcenter.homescreenchannels;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.support.media.tv.TvContractCompat;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TvPreviewData {
    private static final String CHANNEL_SELECTION = "browsable = 1 and searchable = 1 and input_id = ?";
    public static final String PLAYLIST_ID = "TvPlayList";
    private static final String TAG = "TvPreviewData";
    public static final String TMP_BITMAP_DIR = "bitmap";
    private static List<Playlist> mPlaylists = null;

    private TvPreviewData() {
    }

    private static String getImageResourceUri(Context context, int resId) {
        return "android.resource://" + context.getResources().getResourcePackageName(resId) + "/" + context.getResources().getResourceTypeName(resId) + "/" + context.getResources().getResourceEntryName(resId);
    }

    public static List<Playlist> getDefaultPlayList(Context context) {
        Playlist playlist = new Playlist(context.getString(R.string.app_name), new ArrayList(), PLAYLIST_ID, R.drawable.icon);
        if (mPlaylists == null) {
            mPlaylists = new ArrayList();
        }
        mPlaylists.add(playlist);
        collectPreviewPrograms(context, playlist);
        return mPlaylists;
    }

    private static void collectPreviewPrograms(Context context, Playlist playlist) {
        try {
            List<TvInputInfo> inputs = ((TvInputManager) context.getSystemService("tv_input")).getTvInputList();
            if (inputs == null || inputs.size() == 0) {
                MtkLog.d(TAG, "getTvInputList failed");
                return;
            }
            insertLiveChannelsPrograms(context, playlist, inputs);
            insertInputPrograms(context, playlist, inputs, 1007);
            insertInputPrograms(context, playlist, inputs, 1001);
            insertInputPrograms(context, playlist, inputs, 1004);
            insertInputPrograms(context, playlist, inputs, 1003);
            insertInputPrograms(context, playlist, inputs, 1005);
        } catch (Exception ex) {
            MtkLog.d(TAG, ex.toString());
        }
    }

    private static void insertLiveChannelsPrograms(Context context, Playlist playlist, List<TvInputInfo> inputs) {
        TvInputInfo tv = null;
        for (TvInputInfo input : inputs) {
            if (input == null) {
                MtkLog.d(TAG, "input == null");
            } else if (input.getType() == 0 && input.getId().contains("mediatek")) {
                tv = input;
            }
        }
        if (tv == null) {
            MtkLog.d(TAG, "insertLiveChannelsPrograms failed");
            return;
        }
        Cursor cursor = context.getContentResolver().query(TvContractCompat.Channels.CONTENT_URI, (String[]) null, CHANNEL_SELECTION, new String[]{tv.getId()}, (String) null);
        if (cursor == null) {
            MtkLog.d(TAG, "insertLiveChannelsPrograms cursor null");
            return;
        }
        while (cursor.moveToNext()) {
            Long mId = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
            String displayName = cursor.getString(cursor.getColumnIndex(TvContractCompat.Channels.COLUMN_DISPLAY_NAME));
            String displayNumber = cursor.getString(cursor.getColumnIndex(TvContractCompat.Channels.COLUMN_DISPLAY_NUMBER));
            String str = displayNumber + " " + displayName;
            String str2 = displayNumber + " " + displayName;
            playlist.getClips().add(new Clip(str, str2, "", "", TvContractCompat.buildChannelUri(mId.longValue()).toSafeString() + "?input=" + tv.getId(), TvContractCompat.buildChannelUri(mId.longValue()).toSafeString(), PLAYLIST_ID + displayNumber + displayName, mId.longValue(), mId.longValue(), String.valueOf(mId), 0));
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private static void insertInputPrograms(Context context, Playlist playlist, List<TvInputInfo> inputs, int type) {
        Context context2 = context;
        for (TvInputInfo input : inputs) {
            if (input == null) {
                MtkLog.d(TAG, "input == null");
            } else if (input.getType() == type) {
                String name = String.valueOf(input.loadLabel(context2));
                String icon = saveInputIcon(context2, name, input.loadIcon(context2));
                String videouri = TvContractCompat.buildChannelUriForPassthroughInput(input.getId()).toSafeString() + "?input=" + input.getId();
                playlist.getClips().add(new Clip(name, name, icon, icon, videouri, videouri, PLAYLIST_ID + Integer.toString(type), 123, 123, Integer.toString(MessageType.MESSAGE_INACTIVE_CHANNELS), 0));
            }
        }
        int i = type;
    }

    @WorkerThread
    static List<Playlist> getPlaylistBlocking() {
        return mPlaylists;
    }

    @WorkerThread
    static Clip getClipByIdBlocking(long clipId) {
        if (mPlaylists == null) {
            return null;
        }
        for (Playlist playlist : mPlaylists) {
            Iterator<Clip> it = playlist.getClips().iterator();
            while (true) {
                if (it.hasNext()) {
                    Clip candidateClip = it.next();
                    if (candidateClip.getClipId() == clipId) {
                        return candidateClip;
                    }
                }
            }
        }
        return null;
    }

    private static String saveInputIcon(Context context, String name, Drawable draw) {
        File file;
        Bitmap bitmap = drawableToBitmap(draw);
        String filePath = context.getDataDir() + File.separator + TMP_BITMAP_DIR + name + File.separator + ".png";
        FileOutputStream fos = null;
        if (bitmap == null) {
            MtkLog.d(TAG, "bitmap is null." + draw);
            return "";
        }
        try {
            file = new File(filePath);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            try {
                fos.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            MtkLog.e(TAG, e.getMessage());
            file = null;
            if (fos != null) {
                fos.close();
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e12) {
                    e12.printStackTrace();
                }
            }
            throw th;
        }
        if (file != null) {
            return Uri.fromFile(file).toSafeString();
        }
        return "";
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
