package android.support.v17.leanback.app;

import android.support.v17.leanback.media.SurfaceHolderGlueHost;
import android.view.SurfaceHolder;

@Deprecated
public class VideoFragmentGlueHost extends PlaybackFragmentGlueHost implements SurfaceHolderGlueHost {
    private final VideoFragment mFragment;

    public VideoFragmentGlueHost(VideoFragment fragment) {
        super(fragment);
        this.mFragment = fragment;
    }

    public void setSurfaceHolderCallback(SurfaceHolder.Callback callback) {
        this.mFragment.setSurfaceHolderCallback(callback);
    }
}
