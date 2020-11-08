package com.mediatek.wwtv.tvcenter.util;

import java.io.FileOutputStream;

public final class Commands {
    public static final String TAG = "Commands";

    public static void startCmd() {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("/data/vendor/tmp/fifo_am_write");
            output.write(":dtv_app_mtk,am,LMK:livetv=started\n".getBytes());
            output.write(":dtv_app_mtk,am,LMK:start_bg=wpf_browser\n".getBytes());
            output.close();
            output = null;
        } catch (Exception e) {
        }
        if (output != null) {
            try {
                output.close();
            } catch (Exception e2) {
            }
        }
    }

    public static void stopCmd() {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("/data/vendor/tmp/fifo_am_write");
            output.write(":dtv_app_mtk,am,LMK:livetv=stopped\n".getBytes());
            output.close();
            output = null;
        } catch (Exception e) {
        }
        if (output != null) {
            try {
                output.close();
            } catch (Exception e2) {
            }
        }
    }
}
