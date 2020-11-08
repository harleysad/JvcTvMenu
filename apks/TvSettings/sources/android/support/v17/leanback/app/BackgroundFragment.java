package android.support.v17.leanback.app;

import android.app.Fragment;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class BackgroundFragment extends Fragment {
    private BackgroundManager mBackgroundManager;

    /* access modifiers changed from: package-private */
    public void setBackgroundManager(BackgroundManager backgroundManager) {
        this.mBackgroundManager = backgroundManager;
    }

    /* access modifiers changed from: package-private */
    public BackgroundManager getBackgroundManager() {
        return this.mBackgroundManager;
    }

    public void onStart() {
        super.onStart();
        if (this.mBackgroundManager != null) {
            this.mBackgroundManager.onActivityStart();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mBackgroundManager != null) {
            this.mBackgroundManager.onResume();
        }
    }

    public void onStop() {
        if (this.mBackgroundManager != null) {
            this.mBackgroundManager.onStop();
        }
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mBackgroundManager != null) {
            this.mBackgroundManager.detach();
        }
    }
}
