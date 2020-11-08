package com.mediatek.wwtv.setting.util;

import android.net.Uri;
import java.util.Comparator;

class TransitionImageMatcher implements Comparator<TransitionImage> {
    public int compare(TransitionImage lhs, TransitionImage rhs) {
        Uri r = null;
        Uri l = lhs == null ? null : lhs.getUri();
        if (rhs != null) {
            r = rhs.getUri();
        }
        if (l == null) {
            return r == null ? 0 : -1;
        }
        if (r == null) {
            return 1;
        }
        return l.compareTo(r);
    }
}
