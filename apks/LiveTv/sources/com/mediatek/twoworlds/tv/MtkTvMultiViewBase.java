package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvMultiViewBase {
    public static final int MULTIVIEW_RET_FAIL = -1;
    public static final int MULTIVIEW_RET_OK = 0;
    public static final String MULTI_VIEW_MAIN = "main";
    public static final String MULTI_VIEW_SUB = "sub";
    public static final String TAG = "TV_MtkTvMultiViewBase";
    public static final int TV_MODE_NORMAL = 0;
    public static final int TV_MODE_PIP = 1;
    public static final int TV_MODE_POP = 2;

    public static class Region_Info_T {
        private static final String TAG = "Region_Info_T";
        private int height;
        private int width;
        private int x;
        private int y;

        public Region_Info_T(int x2, int y2, int width2, int height2) {
            this.x = x2;
            this.y = y2;
            this.width = width2;
            this.height = height2;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }

    public int getPOPTunerFocus() {
        Log.d(TAG, "Enter getPOPTunerFocus\n");
        return TVNativeWrapper.getPOPTunerFocus_native();
    }

    public int getAudioFocus() {
        Log.d(TAG, "Enter getAudioFocus\n");
        return TVNativeWrapper.getAudioFocus_native();
    }

    public int setAudioFocusbySourceid(int source_id) {
        Log.d(TAG, "Enter setAudioFocusbySourceid, source_id = " + source_id + "\n");
        if (source_id >= 0) {
            return TVNativeWrapper.setAudioFocusbySourceid_native(source_id);
        }
        Log.d(TAG, "Fail to setAudioFocusbySourceid, (source_id < 0) \n");
        return -1;
    }

    public int setAudioFocus(String path) {
        Log.d(TAG, "Enter setAudioFocus, audio focus designate path = " + path + "\n");
        if (path == null) {
            Log.d(TAG, "Fail to setAudioFocus, path is null \n");
            return -1;
        } else if (path.equals("main") || path.equals("sub")) {
            return TVNativeWrapper.setAudioFocus_native(path);
        } else {
            Log.d(TAG, "Fail to setAudioFocus, path is not main or sub \n");
            return -1;
        }
    }

    public int setNewTvMode(int tv_mode) {
        Log.d(TAG, "Enter setNewTvMode, tv mode = " + tv_mode + "\n");
        if (tv_mode == 0 || tv_mode == 2 || tv_mode == 1) {
            return TVNativeWrapper.setNewTvMode_native(tv_mode);
        }
        Log.d(TAG, "Fail to setNewTvMode, tv_mode is illegal!\n");
        return -1;
    }

    public boolean isMultiviewInputsourceAvailable(int ui1_input_src, String path) {
        Log.d(TAG, "Enter isMultiviewInputsourceAvailable, ui1_input_src = " + ui1_input_src + ", designate path = " + path + "\n");
        if (path == null) {
            Log.d(TAG, "Fail to isMultiviewInputsourceAvailable, path is null \n");
            return false;
        } else if (path.equals("main") || path.equals("sub")) {
            boolean is_available = TVNativeWrapper.isMultiviewInputsourceAvailable_native(ui1_input_src, path);
            Log.d(TAG, "Leave isMultiviewInputsourceAvailable, is_available = " + is_available + "\n");
            return is_available;
        } else {
            Log.d(TAG, "Fail to isMultiviewInputsourceAvailable, path is illegal \n");
            return false;
        }
    }

    public int startMainVideo(int ui1_main_input_src, Region_Info_T region) {
        Log.d(TAG, "Enter startMainVideo ui1_main_input_src = " + ui1_main_input_src + ", region.x = " + region.getX() + ", region.y = " + region.getY() + ", region.width = " + region.getWidth() + ", region.height = " + region.getHeight() + "\n");
        return TVNativeWrapper.startMainVideo_native(ui1_main_input_src, region);
    }

    public int startSubVideo(int ui1_sub_input_src, Region_Info_T sub_region) {
        Log.d(TAG, "Enter startSubVideo , ui1_sub_input_src = " + ui1_sub_input_src + "\n");
        Log.d(TAG, "Enter startSubVideo , sub_region.x = " + sub_region.getX() + ", sub_region.y = " + sub_region.getY() + ", sub_region.width = " + sub_region.getWidth() + ", sub_region.height = " + sub_region.getHeight() + "\n");
        return TVNativeWrapper.startSubVideo_native(ui1_sub_input_src, sub_region);
    }

    public int stopMainVideo() {
        Log.d(TAG, "Enter stopMainVideo\n");
        return TVNativeWrapper.stopMainVideo_native();
    }

    public int stopSubVideo() {
        Log.d(TAG, "Enter stopSubVideo\n");
        return TVNativeWrapper.stopSubVideo_native();
    }

    public int setChgSource(boolean b_chg_source) {
        Log.d(TAG, "Enter setChgSource\n");
        return TVNativeWrapper.setChgSource_native(b_chg_source);
    }

    public boolean isTvRunning(String path) {
        return TVNativeWrapper.isTvRunning_native(path);
    }

    public int onlyChgFocus(String path) {
        Log.d(TAG, "Enter onlyChgFocus, change focus designate path = " + path + "\n");
        if (path == null) {
            Log.d(TAG, "Fail to onlyChgFocus, path is null \n");
            return -1;
        } else if (path.equals("main") || path.equals("sub")) {
            return TVNativeWrapper.onlyChgFocus_native(path);
        } else {
            Log.d(TAG, "Fail to onlyChgFocus, path is not main or sub \n");
            return -1;
        }
    }
}
